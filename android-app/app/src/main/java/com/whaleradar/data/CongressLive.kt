package com.whaleradar.data

import android.content.Context
import android.content.SharedPreferences
import com.whaleradar.data.model.CongressTrade
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.Callable
import java.util.concurrent.Executors

/**
 * Live loader for the OFFICIAL U.S. Congress stock-trade disclosures (STOCK Act, Periodic Transaction Reports):
 *  - U.S. House of Representatives: Clerk of the House  (https://disclosures-clerk.house.gov)
 *      index  : /public_disc/financial-pdfs/{year}FD.zip  (XML list of all filings)
 *      reports: /public_disc/ptr-pdfs/{year}/{DocID}.pdf   (PTR PDF -> text -> transactions)
 *  - U.S. Senate: Electronic Financial Disclosure system (https://efdsearch.senate.gov)
 *      search : POST /search/report/data/  (report type 11 = Periodic Transaction Report)
 *      reports: /search/view/ptr/{uuid}/   (HTML table of transactions)
 * Party affiliation comes from the public congress-legislators dataset (unitedstates.github.io).
 * Nothing is invented: only transactions that were really parsed from the official reports are returned.
 */
class CongressLive(private val context: Context, private val prefs: SharedPreferences) {

    companion object {
        const val CACHE_KEY = "cached_live_congress_v3"
        private const val DEFAULT_WINDOW_DAYS = 30
        private const val MAX_CACHE_DAYS = 730
        private const val MAX_NEW_HOUSE_DOCS = 160
        private const val MAX_NEW_HOUSE_DOCS_PRO = 280
        private const val MAX_NEW_SENATE_DOCS = 120
        private const val MAX_NEW_SENATE_DOCS_PRO = 220
        private const val UA = "Mozilla/5.0 (Linux; Android 13; WhaleRadar) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0 Mobile Safari/537.36"

        private val ISO = SimpleDateFormat("yyyy-MM-dd", Locale.US).apply { timeZone = TimeZone.getTimeZone("UTC") }
        private val US = SimpleDateFormat("M/d/yyyy", Locale.US).apply { timeZone = TimeZone.getTimeZone("UTC"); isLenient = false }

        /** "10/20/2026" -> "2026-10-20" (empty if unparsable) */
        fun usToIso(s: String): String = try { ISO.format(US.parse(s.trim())!!) } catch (_: Exception) { "" }

        fun daysBetween(fromIso: String, toIso: String): Int = try {
            Math.round((ISO.parse(toIso)!!.time - ISO.parse(fromIso)!!.time) / 86_400_000.0).toInt()
        } catch (_: Exception) { 0 }

        private fun typeLabel(raw: String): String {
            val t = raw.trim().lowercase()
            return when {
                t.startsWith("p") -> "Purchase"
                t.contains("partial") -> "Sale (Partial)"
                t.startsWith("s") -> "Sale (Full)"
                t.startsWith("e") -> "Exchange"
                else -> raw.trim()
            }
        }

        private fun cleanAmount(raw: String): String {
            val a = raw.replace("\u00A0", " ").replace(Regex("\\s+"), " ").trim()
            return a.replace(Regex("\\s*-\\s*"), " - ")
        }

        private val tickerRegex = Regex("\\(([A-Z]{1,5}(?:[.\\-][A-Z])?)\\)\\s*\\[(ST|OP)\\]")
        private val dateRegex = Regex("(\\d{1,2}/\\d{1,2}/\\d{4})")
        private val amountRegex = Regex("(Over \\$[\\d,]+|\\$[\\d,]+\\s*-\\s*\\$[\\d,]+)")
        private val typeRegex = Regex("(?:^|\\s)(P|S \\(partial\\)|S\\(partial\\)|S|E)(?=\\s+\\d{1,2}/\\d{1,2}/\\d{4})")

        /**
         * Parses the text of a House Periodic Transaction Report PDF.
         * Every transaction row contains: asset "Name (TICKER) [ST]", type (P/S/S (partial)/E),
         * transaction date, notification date and the amount range.
         */
        fun parseHousePtrText(
            text: String, filerName: String, party: String, filingDateIso: String, docId: String
        ): List<CongressTrade> {
            val lines = text.replace("\u0000", "").replace("\u00A0", " ").replace("\r", "")
                .split("\n").map { it.replace(Regex("\\s+"), " ").trim() }.filter { it.isNotEmpty() }
            val trailerRegex = Regex("^(F S ?:|S O ?:|D ?:|C ?:|L ?:|I V D|I P O|C S|\\* For the complete)")
            val headerRegex = Regex("^(ID Owner Asset|Type$|Date Notification|Amount Cap|Gains >|\\$200\\?|Notification|Date$)")
            val out = mutableListOf<CongressTrade>()
            var i = 0
            while (i < lines.size) {
                val m = tickerRegex.find(lines[i])
                if (m == null) { i++; continue }
                val ticker = m.groupValues[1]
                val kind = m.groupValues[2]
                // Asset name = text before "(TICKER)" on this line, plus a wrapped previous line (if it is part of the asset name)
                var name = lines[i].substring(0, m.range.first).trim()
                if (i > 0) {
                    val prev = lines[i - 1]
                    val prevIsName = !trailerRegex.containsMatchIn(prev) && !headerRegex.containsMatchIn(prev) &&
                        !amountRegex.containsMatchIn(prev) && !prev.endsWith("Yes No") && tickerRegex.find(prev) == null &&
                        !prev.startsWith("Digitally Signed") && !prev.startsWith("Filing ID")
                    if (prevIsName) name = (prev + " " + name).trim()
                }
                name = name.replace(Regex("^(SP|DC|JT) "), "").trim()
                // Row data (type, dates, amount) follows the ticker, possibly wrapped over the next lines
                val sb = StringBuilder(lines[i].substring(m.range.last + 1))
                var j = i + 1
                while (j < lines.size && j <= i + 3 && tickerRegex.find(lines[j]) == null && !trailerRegex.containsMatchIn(lines[j])) {
                    sb.append(' ').append(lines[j]); j++
                }
                val rest = sb.toString().trim()
                val typeM = typeRegex.find(" $rest")
                val amountM = amountRegex.find(rest)
                val dates = dateRegex.findAll(rest).map { it.value }.toList()
                val txIso = if (dates.isNotEmpty()) usToIso(dates[0]) else ""
                if (typeM != null && amountM != null && txIso.isNotEmpty()) {
                    val days = if (filingDateIso.isNotEmpty()) daysBetween(txIso, filingDateIso) else 0
                    out.add(
                        CongressTrade(
                            id = "house_${docId}_${out.size}",
                            filerName = filerName,
                            party = party,
                            chamber = "house",
                            ticker = ticker,
                            assetName = (name + if (kind == "OP") " (Options)" else "").ifBlank { ticker },
                            transactionType = typeLabel(typeM.groupValues[1]),
                            amountRangeLabel = cleanAmount(amountM.value),
                            transactionDate = txIso,
                            filingDate = filingDateIso,
                            daysToFile = days,
                            isLate = days > 45
                        )
                    )
                }
                i = j
            }
            return out
        }
        private fun stripTags(s: String): String =
            s.replace(Regex("<[^>]*>"), " ")
                .replace("&amp;", "&").replace("&nbsp;", " ").replace("&#39;", "'").replace("&quot;", "\"")
                .replace(Regex("\\s+"), " ").trim()

        /** Parses a Senate eFD "ptr" HTML report page (transaction table). */
        fun parseSenatePtrHtml(
            html: String, filerName: String, party: String, filingDateIso: String, docId: String
        ): List<CongressTrade> {
            val out = mutableListOf<CongressTrade>()
            val rows = Regex("<tr[^>]*>([\\s\\S]*?)</tr>", RegexOption.IGNORE_CASE).findAll(html)
            for (row in rows) {
                val cells = Regex("<td[^>]*>([\\s\\S]*?)</td>", RegexOption.IGNORE_CASE).findAll(row.groupValues[1])
                    .map { it.groupValues[1] }.toList()
                if (cells.size < 8) continue
                val txIso = usToIso(stripTags(cells[1]))
                if (txIso.isEmpty()) continue
                val ticker = stripTags(cells[3]).uppercase().trim()
                if (ticker.isEmpty() || ticker == "--" || !ticker.matches(Regex("^[A-Z]{1,5}(?:[.\\-][A-Z])?$"))) continue
                val assetType = stripTags(cells[5])
                if (!assetType.contains("Stock", ignoreCase = true) && !assetType.contains("Option", ignoreCase = true)) continue
                val type = stripTags(cells[6])
                val days = if (filingDateIso.isNotEmpty()) daysBetween(txIso, filingDateIso) else 0
                val assetName = stripTags(cells[4]).ifBlank { ticker } +
                    if (assetType.contains("Option", ignoreCase = true)) " (Options)" else ""
                out.add(
                    CongressTrade(
                        id = "senate_${docId}_${out.size}",
                        filerName = filerName,
                        party = party,
                        chamber = "senate",
                        ticker = ticker,
                        assetName = assetName,
                        transactionType = typeLabel(type),
                        amountRangeLabel = cleanAmount(stripTags(cells[7])),
                        transactionDate = txIso,
                        filingDate = filingDateIso,
                        daysToFile = days,
                        isLate = days > 45
                    )
                )
            }
            return out
        }
    }

    // ------------------------------------------------------------------ cache

    private data class Cache(val trades: List<CongressTrade>, val docIds: Set<String>)

    private fun readCache(): Cache {
        val s = prefs.getString(CACHE_KEY, null) ?: return Cache(emptyList(), emptySet())
        return try {
            val root = JSONObject(s)
            val arr = root.optJSONArray("trades") ?: JSONArray()
            val list = mutableListOf<CongressTrade>()
            for (i in 0 until arr.length()) {
                val t = arr.getJSONObject(i)
                list.add(
                    CongressTrade(
                        id = t.optString("id"), filerName = t.optString("filer_name"), party = t.optString("party"),
                        chamber = t.optString("chamber"), ticker = t.optString("ticker"), assetName = t.optString("asset_name"),
                        transactionType = t.optString("transaction_type"), amountRangeLabel = t.optString("amount"),
                        transactionDate = t.optString("transaction_date"), filingDate = t.optString("filing_date"),
                        daysToFile = t.optInt("days_to_file"), isLate = t.optBoolean("is_late")
                    )
                )
            }
            val ids = mutableSetOf<String>()
            val idArr = root.optJSONArray("doc_ids") ?: JSONArray()
            for (i in 0 until idArr.length()) ids.add(idArr.getString(i))
            Cache(list, ids)
        } catch (_: Exception) { Cache(emptyList(), emptySet()) }
    }

    private fun writeCache(trades: List<CongressTrade>, docIds: Set<String>) {
        val arr = JSONArray()
        for (t in trades) arr.put(JSONObject().apply {
            put("id", t.id); put("filer_name", t.filerName); put("party", t.party); put("chamber", t.chamber)
            put("ticker", t.ticker); put("asset_name", t.assetName); put("transaction_type", t.transactionType)
            put("amount", t.amountRangeLabel); put("transaction_date", t.transactionDate)
            put("filing_date", t.filingDate); put("days_to_file", t.daysToFile); put("is_late", t.isLate)
        })
        val root = JSONObject().apply {
            put("updated_at", System.currentTimeMillis())
            put("trades", arr)
            put("doc_ids", JSONArray(docIds.toList()))
        }
        prefs.edit().putString(CACHE_KEY, root.toString()).apply()
    }

    /** Last successfully loaded official data (House + Senate). */
    fun loadCached(): List<CongressTrade> = readCache().trades

    // ------------------------------------------------------------------ http

    private fun http(url: String, method: String = "GET", body: String? = null,
                     headers: Map<String, String> = emptyMap(), cookies: MutableMap<String, String>? = null,
                     follow: Boolean = true): Pair<Int, ByteArray> {
        val c = URL(url).openConnection() as HttpURLConnection
        c.requestMethod = method
        c.connectTimeout = 15000
        c.readTimeout = 30000
        c.instanceFollowRedirects = follow
        c.setRequestProperty("User-Agent", UA)
        c.setRequestProperty("Accept-Language", "en-US,en;q=0.9")
        headers.forEach { (k, v) -> c.setRequestProperty(k, v) }
        if (cookies != null && cookies.isNotEmpty()) {
            c.setRequestProperty("Cookie", cookies.entries.joinToString("; ") { "${it.key}=${it.value}" })
        }
        if (body != null) {
            c.doOutput = true
            c.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
            c.outputStream.use { it.write(body.toByteArray(Charsets.UTF_8)) }
        }
        val code = c.responseCode
        if (cookies != null) {
            c.headerFields["Set-Cookie"]?.forEach { sc ->
                val kv = sc.substringBefore(";")
                cookies[kv.substringBefore("=").trim()] = kv.substringAfter("=").trim()
            }
        }
        val stream = if (code >= 400) c.errorStream else c.inputStream
        val bytes = stream?.use { it.readBytes() } ?: ByteArray(0)
        return Pair(code, bytes)
    }

    private fun enc(s: String) = URLEncoder.encode(s, "UTF-8")

    // ------------------------------------------------------------------ legislators (party)

    private class Legislator(val first: String, val last: String, val nick: String, val type: String,
                             val state: String, val district: Int, val party: String)

    private fun norm(s: String): String =
        java.text.Normalizer.normalize(s, java.text.Normalizer.Form.NFD)
            .replace(Regex("\\p{M}+"), "").lowercase().replace(Regex("[^a-z ]"), "").trim()

    private fun loadLegislators(): List<Legislator> {
        return try {
            val (code, bytes) = http("https://unitedstates.github.io/congress-legislators/legislators-current.json")
            if (code != 200) return emptyList()
            val arr = JSONArray(String(bytes, Charsets.UTF_8))
            val out = mutableListOf<Legislator>()
            for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i)
                val n = o.getJSONObject("name")
                val terms = o.getJSONArray("terms")
                val t = terms.getJSONObject(terms.length() - 1)
                out.add(
                    Legislator(
                        first = norm(n.optString("first")), last = norm(n.optString("last")), nick = norm(n.optString("nickname")),
                        type = t.optString("type"), state = t.optString("state"), district = t.optInt("district", -1),
                        party = when (t.optString("party")) { "Republican" -> "R"; "Democrat" -> "D"; else -> "I" }
                    )
                )
            }
            out
        } catch (_: Exception) { emptyList() }
    }

    private fun partyFor(leg: List<Legislator>, first: String, last: String, type: String, state: String?, district: Int?): String {
        val l = norm(last).split(" ").lastOrNull() ?: ""
        val f = norm(first).split(" ").firstOrNull() ?: ""
        val sameType = leg.filter { it.type == type && (norm(it.last) == norm(last) || it.last.endsWith(l)) }
        val byState = if (state != null) sameType.filter { it.state == state && (district == null || it.district == district || type == "sen") } else sameType
        val pick = byState.firstOrNull { it.first.startsWith(f) || f.startsWith(it.first) || it.nick == f }
            ?: byState.singleOrNull() ?: sameType.firstOrNull { it.first.startsWith(f) || f.startsWith(it.first) || it.nick == f }
        return pick?.party ?: "I"
    }

    // ------------------------------------------------------------------ House

    private class HouseDoc(val docId: String, val name: String, val last: String, val first: String,
                           val state: String, val district: Int, val filingIso: String)

    private fun listHousePtrs(year: Int, sinceIso: String): List<HouseDoc> {
        val (code, bytes) = http("https://disclosures-clerk.house.gov/public_disc/financial-pdfs/${year}FD.zip")
        if (code != 200) return emptyList()
        val zis = java.util.zip.ZipInputStream(bytes.inputStream())
        var xml = ""
        while (true) {
            val e = zis.nextEntry ?: break
            if (e.name.endsWith(".xml", ignoreCase = true)) { xml = String(zis.readBytes(), Charsets.UTF_8); break }
        }
        val docs = mutableListOf<HouseDoc>()
        for (m in Regex("<Member>([\\s\\S]*?)</Member>").findAll(xml)) {
            val b = m.groupValues[1]
            fun tag(n: String) = Regex("<$n>([^<]*)</$n>").find(b)?.groupValues?.get(1)?.trim() ?: ""
            if (tag("FilingType") != "P") continue
            val filingIso = usToIso(tag("FilingDate"))
            if (filingIso.isEmpty() || filingIso < sinceIso) continue
            val sd = tag("StateDst")
            val state = sd.take(2)
            val district = sd.drop(2).toIntOrNull() ?: 0
            val first = tag("First"); val last = tag("Last"); val suffix = tag("Suffix")
            docs.add(HouseDoc(tag("DocID"), listOf(first, last, suffix).filter { it.isNotBlank() }.joinToString(" "), last, first, state, district, filingIso))
        }
        return docs
    }

    private fun fetchHouse(sinceIso: String, known: Set<String>, leg: List<Legislator>, newDocIds: MutableSet<String>, windowDays: Int): List<CongressTrade> {
        com.tom_roush.pdfbox.android.PDFBoxResourceLoader.init(context.applicationContext)
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val sinceYear = sinceIso.take(4).toIntOrNull() ?: currentYear
        val startYear = (if (Calendar.getInstance().get(Calendar.MONTH) <= 1) minOf(sinceYear, currentYear - 1) else sinceYear).coerceAtLeast(currentYear - 3)
        val docs = mutableListOf<HouseDoc>()
        for (y in currentYear downTo startYear) {
            try {
                docs.addAll(listHousePtrs(y, sinceIso))
            } catch (_: Exception) {}
        }
        val maxDocs = if (windowDays > 30) MAX_NEW_HOUSE_DOCS_PRO else MAX_NEW_HOUSE_DOCS
        val todo = docs.filter { "house_${it.docId}" !in known }.sortedByDescending { it.filingIso }.take(maxDocs)
        val pool = Executors.newFixedThreadPool(4)
        try {
            val futures = todo.map { d ->
                pool.submit(Callable {
                    try {
                        val y = d.filingIso.take(4)
                        val (code, pdf) = http("https://disclosures-clerk.house.gov/public_disc/ptr-pdfs/$y/${d.docId}.pdf")
                        if (code != 200) return@Callable null
                        val text = com.tom_roush.pdfbox.pdmodel.PDDocument.load(pdf.inputStream()).use {
                            com.tom_roush.pdfbox.text.PDFTextStripper().getText(it)
                        }
                        val party = partyFor(leg, d.first, d.last, "rep", d.state, d.district)
                        Pair(d.docId, parseHousePtrText(text, d.name, party, d.filingIso, d.docId))
                    } catch (_: Exception) { null }
                })
            }
            val out = mutableListOf<CongressTrade>()
            for (f in futures) {
                val r = f.get() ?: continue
                newDocIds.add("house_${r.first}")
                out.addAll(r.second)
            }
            return out
        } finally { pool.shutdown() }
    }

    // ------------------------------------------------------------------ Senate

    private fun fetchSenate(sinceIso: String, known: Set<String>, leg: List<Legislator>, newDocIds: MutableSet<String>, windowDays: Int): List<CongressTrade>? {
        val base = "https://efdsearch.senate.gov"
        val cookies = mutableMapOf<String, String>()
        val (c1, home) = http("$base/search/home/", cookies = cookies)
        if (c1 != 200) return null
        val token = Regex("name=\"csrfmiddlewaretoken\" value=\"([^\"]+)\"").find(String(home))?.groupValues?.get(1) ?: return null
        // Accept the terms of use (required by the Senate site before any search)
        val (c2, _) = http(
            "$base/search/home/", "POST",
            "prohibition_agreement=1&csrfmiddlewaretoken=${enc(token)}",
            mapOf("Referer" to "$base/search/home/"), cookies, follow = false
        )
        if (c2 != 302 && c2 != 200) return null
        val csrf = cookies["csrftoken"] ?: token
        val sinceUs = SimpleDateFormat("MM/dd/yyyy", Locale.US).format(ISO.parse(sinceIso)!!)
        val docs = mutableListOf<List<String>>() // first, last, link, filedUs
        var start = 0
        var searchOk = false
        while (start < 800) {
            val body = "start=$start&length=100&report_types=${enc("[11]")}&filer_types=${enc("[]")}&submitted_start_date=${enc("$sinceUs 00:00:00")}" +
                "&submitted_end_date=&candidate_state=&senator_state=&office_id=&first_name=&last_name=&csrfmiddlewaretoken=${enc(csrf)}"
            val (c3, resp) = http(
                "$base/search/report/data/", "POST", body,
                mapOf("Referer" to "$base/search/", "X-CSRFToken" to csrf, "X-Requested-With" to "XMLHttpRequest"), cookies
            )
            if (c3 != 200) break
            searchOk = true
            val json = JSONObject(String(resp, Charsets.UTF_8))
            val data = json.optJSONArray("data") ?: break
            if (data.length() == 0) break
            for (i in 0 until data.length()) {
                val r = data.getJSONArray(i)
                docs.add(listOf(r.optString(0), r.optString(1), r.optString(3), r.optString(4)))
            }
            start += 100
            if (start >= json.optInt("recordsFiltered", 0)) break
        }
        if (!searchOk) return null
        val out = mutableListOf<CongressTrade>()
        var fetched = 0
        val maxSenateDocs = if (windowDays > 30) MAX_NEW_SENATE_DOCS_PRO else MAX_NEW_SENATE_DOCS
        for (d in docs) {
            if (fetched >= maxSenateDocs) break
            val href = Regex("href=\"(/search/view/ptr/([^/\"]+)/)\"").find(d[2]) ?: continue // paper filings are scans -> skipped
            val uuid = href.groupValues[2]
            if ("senate_$uuid" in known) continue
            try {
                val (c4, page) = http(base + href.groupValues[1], cookies = cookies, headers = mapOf("Referer" to "$base/search/"))
                if (c4 != 200) continue
                val filingIso = usToIso(d[3])
                val name = "${d[0]} ${d[1]}".replace(Regex("\\s+"), " ").trim()
                val party = partyFor(leg, d[0], d[1], "sen", null, null)
                out.addAll(parseSenatePtrHtml(String(page, Charsets.UTF_8), name, party, filingIso, uuid))
                newDocIds.add("senate_$uuid")
                fetched++
            } catch (_: Exception) {}
        }
        return out
    }

    // ------------------------------------------------------------------ public API

    /**
     * Loads new official House + Senate PTRs for the requested windowDays, merges them with the cached ones,
     * caches up to MAX_CACHE_DAYS (2 years), and returns the trades within windowDays.
     */
    fun fetchAll(windowDays: Int = DEFAULT_WINDOW_DAYS): List<CongressTrade>? {
        val cache = readCache()
        val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        cal.add(Calendar.DAY_OF_YEAR, -windowDays)
        val sinceIso = ISO.format(cal.time)

        val maxCacheCal = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        maxCacheCal.add(Calendar.DAY_OF_YEAR, -MAX_CACHE_DAYS)
        val maxCacheIso = ISO.format(maxCacheCal.time)

        val leg = loadLegislators()
        val newDocs = mutableSetOf<String>()
        val fresh = mutableListOf<CongressTrade>()
        var anyOk = false
        var senateOk = false
        try { fresh.addAll(fetchHouse(sinceIso, cache.docIds, leg, newDocs, windowDays)); anyOk = true } catch (e: Exception) {
            android.util.Log.w("CongressLive", "House feed failed", e)
        }
        try {
            val senate = fetchSenate(sinceIso, cache.docIds, leg, newDocs, windowDays)
            if (senate != null) { fresh.addAll(senate); senateOk = true; anyOk = true }
        } catch (e: Exception) {
            android.util.Log.w("CongressLive", "Senate feed failed", e)
        }
        // Senate data is only used if the Senate feed could actually be retrieved
        val cachedTrades = if (senateOk) cache.trades else cache.trades.filter { it.chamber != "senate" }
        val knownIds = if (senateOk) cache.docIds else cache.docIds.filterNot { it.startsWith("senate_") }.toSet()
        if (!anyOk) return cachedTrades.filter { it.transactionDate >= sinceIso }
        val allToCache = (fresh + cachedTrades)
            .filter { it.transactionDate >= maxCacheIso }
            .distinctBy { "${it.chamber}|${it.filerName}|${it.ticker}|${it.transactionDate}|${it.amountRangeLabel}|${it.transactionType}|${it.filingDate}" }
            .sortedWith(compareByDescending<CongressTrade> { it.filingDate }.thenByDescending { it.transactionDate })
        writeCache(allToCache, knownIds + newDocs)
        return allToCache.filter { it.transactionDate >= sinceIso }
    }
}

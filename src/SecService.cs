using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Text;
using System.Text.RegularExpressions;
using System.Web.Script.Serialization;
using System.Xml.Linq;

namespace WhaleWatch
{
    public static class SecService
    {
        private static readonly JavaScriptSerializer _json = new JavaScriptSerializer() { MaxJsonLength = 20 * 1024 * 1024 };
        private static readonly HttpClient _http = new HttpClient();
        private static object _lock = new object();
        private static string _cachedJson = null;
        private static DateTime _cacheTime = DateTime.MinValue;

        public static void Handle(HttpListenerContext ctx)
        {
            bool force = ctx.Request.QueryString["refresh"] == "1";
            string json = GetData(force);
            byte[] data = Encoding.UTF8.GetBytes(json);
            ctx.Response.ContentType = "application/json; charset=utf-8";
            ctx.Response.OutputStream.Write(data, 0, data.Length);
            ctx.Response.Close();
        }

        public static string GetData(bool force)
        {
            lock (_lock)
            {
                if (!force && _cachedJson != null && (DateTime.UtcNow - _cacheTime).TotalMinutes < 1)
                {
                    return _cachedJson;
                }
            }

            try
            {
                string url = "https://www.sec.gov/cgi-bin/browse-edgar?action=getcurrent&CIK=&type=4&company=&dateb=&owner=only&start=0&count=100&output=atom";
                var req = new HttpRequestMessage(HttpMethod.Get, url);
                req.Headers.Add("User-Agent", "WhaleWatchApp/1.0 (trader@whalewatch.local)");

                var resp = _http.SendAsync(req).Result;
                string xml = resp.Content.ReadAsStringAsync().Result;

                var xdoc = XDocument.Parse(xml);
                XNamespace ns = "http://www.w3.org/2005/Atom";

                var entries = xdoc.Descendants(ns + "entry").ToList();
                var groups = new Dictionary<string, List<XElement>>();
                var updatedDates = new Dictionary<string, string>();
                var filingLinks = new Dictionary<string, string>();

                foreach (var entry in entries)
                {
                    string summary = entry.Element(ns + "summary") != null ? entry.Element(ns + "summary").Value : "";
                    var match = Regex.Match(summary, @"AccNo:\s*</b>\s*([0-9\-]+)");
                    string accNo = match.Success ? match.Groups[1].Value : (entry.Element(ns + "id") != null ? entry.Element(ns + "id").Value : Guid.NewGuid().ToString());

                    if (!groups.ContainsKey(accNo)) groups[accNo] = new List<XElement>();
                    groups[accNo].Add(entry);

                    if (!updatedDates.ContainsKey(accNo) && entry.Element(ns + "updated") != null)
                        updatedDates[accNo] = entry.Element(ns + "updated").Value;

                    if (!filingLinks.ContainsKey(accNo) && entry.Element(ns + "link") != null)
                    {
                        var href = entry.Element(ns + "link").Attribute("href");
                        if (href != null) filingLinks[accNo] = href.Value;
                    }
                }

                var alertList = new List<Dictionary<string, object>>();
                foreach (var pair in groups)
                {
                    string accNo = pair.Key;
                    var list = pair.Value;

                    string issuer = "Unbekanntes Unternehmen";
                    string issuerCik = "";
                    string reportingOwner = "Insider";
                    string ownerCik = "";

                    foreach (var e in list)
                    {
                        string title = e.Element(ns + "title") != null ? e.Element(ns + "title").Value : "";
                        if (title.EndsWith("(Issuer)", StringComparison.OrdinalIgnoreCase))
                        {
                            var m = Regex.Match(title, @"^4\s*-\s*(.+?)\s*\((\d+)\)\s*\(Issuer\)");
                            if (m.Success) { issuer = m.Groups[1].Value.Trim(); issuerCik = m.Groups[2].Value.Trim(); }
                            else { issuer = title.Replace("4 - ", "").Replace("(Issuer)", "").Trim(); }
                        }
                        else if (title.EndsWith("(Reporting)", StringComparison.OrdinalIgnoreCase))
                        {
                            var m = Regex.Match(title, @"^4\s*-\s*(.+?)\s*\((\d+)\)\s*\(Reporting\)");
                            if (m.Success) { reportingOwner = m.Groups[1].Value.Trim(); ownerCik = m.Groups[2].Value.Trim(); }
                            else { reportingOwner = title.Replace("4 - ", "").Replace("(Reporting)", "").Trim(); }
                        }
                    }

                    string updated = updatedDates.ContainsKey(accNo) ? updatedDates[accNo] : DateTime.UtcNow.ToString("yyyy-MM-ddTHH:mm:ssZ");
                    string link = filingLinks.ContainsKey(accNo) ? filingLinks[accNo] : "https://www.sec.gov/edgar/searchedgar/companysearch";

                    alertList.Add(new Dictionary<string, object>
                    {
                        { "accession_number", accNo },
                        { "issuer", issuer },
                        { "issuer_cik", issuerCik },
                        { "reporting_owner", reportingOwner },
                        { "owner_cik", ownerCik },
                        { "filing_time", updated },
                        { "filing_url", link },
                        { "alert_type", "Insider Trade (Form 4)" },
                        { "badge_color", "#3b82f6" },
                        { "role", "Officer / Director / 10% Owner" },
                        { "is_fresh", true }
                    });
                }

                var companyCounts = alertList.GroupBy(a => a["issuer"].ToString()).ToDictionary(g => g.Key, g => g.Count());
                foreach (var a in alertList)
                {
                    string iss = a["issuer"].ToString();
                    if (companyCounts.ContainsKey(iss) && companyCounts[iss] > 1)
                    {
                        a["is_cluster"] = true;
                        a["cluster_count"] = companyCounts[iss];
                        a["alert_type"] = "🔥 CLUSTER-ALERT (" + companyCounts[iss] + " Filings)";
                        a["badge_color"] = "#f59e0b";
                    }
                    else
                    {
                        a["is_cluster"] = false;
                        a["cluster_count"] = 1;
                    }
                }

                var res = new Dictionary<string, object>
                {
                    { "source", "SEC EDGAR Form 4 Real-time Feed" },
                    { "updated_at", DateTime.UtcNow.ToString("yyyy-MM-ddTHH:mm:ssZ") },
                    { "total_alerts", alertList.Count },
                    { "alerts", alertList }
                };

                string outJson = _json.Serialize(res);
                lock (_lock)
                {
                    _cachedJson = outJson;
                    _cacheTime = DateTime.UtcNow;
                }
                return outJson;
            }
            catch (Exception ex)
            {
                lock (_lock) { if (_cachedJson != null) return _cachedJson; }
                return _json.Serialize(new { error = ex.Message });
            }
        }
    }
}

using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Text;
using System.Web.Script.Serialization;

namespace WhaleWatch
{
    public static class CotService
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
                if (!force && _cachedJson != null && (DateTime.UtcNow - _cacheTime).TotalMinutes < 15)
                {
                    return _cachedJson;
                }
            }

            try
            {
                string url = "https://publicreporting.cftc.gov/resource/jun7-fc8e.json?$order=report_date_as_yyyy_mm_dd%20DESC&$limit=500";
                var req = new HttpRequestMessage(HttpMethod.Get, url);
                req.Headers.Add("Accept", "application/json");

                var resp = _http.SendAsync(req).Result;
                string body = resp.Content.ReadAsStringAsync().Result;

                var rawList = _json.Deserialize<List<Dictionary<string, object>>>(body);
                var keyMarkets = new List<Dictionary<string, object>>();

                var targets = new[]
                {
                    new { Key = "SPY", Pattern = "S&P 500", Display = "SPY (SPDR S&P 500 ETF) / VOO", AssetClass = "S&P 500 Large-Cap ETF", Icon = "📈" },
                    new { Key = "QQQ", Pattern = "NASDAQ", Display = "QQQ (Invesco QQQ Trust)", AssetClass = "Nasdaq-100 Tech ETF", Icon = "💻" },
                    new { Key = "RUSSELL", Pattern = "RUSSELL E-MINI", Display = "IWM (Russell 2000 ETF)", AssetClass = "Small Caps US", Icon = "🏢" },
                    new { Key = "DOW", Pattern = "DJIA", Display = "DIA (Dow Jones ETF)", AssetClass = "Blue Chips US", Icon = "🏭" },
                    new { Key = "SILVER", Pattern = "SILVER", Display = "Silber (SLV / Futures)", AssetClass = "Edelmetall / Industrie", Icon = "🥈" },
                    new { Key = "CRUDE_OIL", Pattern = "CRUDE OIL", Display = "WTI Rohöl (USO ETF)", AssetClass = "Energie & Rohstoffe", Icon = "🛢️" },
                    new { Key = "US10Y", Pattern = "UST 10Y NOTE", Display = "10Y US Treasuries (IEF / TLT)", AssetClass = "US Staatsanleihen", Icon = "💵" },
                    new { Key = "BITCOIN", Pattern = "BITCOIN", Display = "Bitcoin (IBIT ETF / Futures)", AssetClass = "Krypto / Risk-On", Icon = "₿" }
                };

                var found = new HashSet<string>();
                foreach (var item in rawList)
                {
                    string mName = (item.ContainsKey("market_and_exchange_names") && item["market_and_exchange_names"] != null) 
                        ? item["market_and_exchange_names"].ToString() : "";

                    foreach (var target in targets)
                    {
                        if (!found.Contains(target.Key) && mName.IndexOf(target.Pattern, StringComparison.OrdinalIgnoreCase) >= 0)
                        {
                            found.Add(target.Key);

                            long noncommLong = ParseLong(item, "noncomm_positions_long_all");
                            long noncommShort = ParseLong(item, "noncomm_positions_short_all");
                            long commLong = ParseLong(item, "comm_positions_long_all");
                            long commShort = ParseLong(item, "comm_positions_short_all");
                            long retailLong = ParseLong(item, "nonrept_positions_long_all");
                            long retailShort = ParseLong(item, "nonrept_positions_short_all");
                            long openInterest = ParseLong(item, "open_interest_all");

                            long chgLong = ParseLong(item, "change_in_noncomm_long_all");
                            long chgShort = ParseLong(item, "change_in_noncomm_short_all");
                            long netWhale = noncommLong - noncommShort;
                            long netChange = chgLong - chgShort;

                            double whaleTotal = noncommLong + noncommShort;
                            double whaleBullishPct = whaleTotal > 0 ? Math.Round((noncommLong / whaleTotal) * 100.0, 1) : 50.0;

                            double retailTotal = retailLong + retailShort;
                            double retailBullishPct = retailTotal > 0 ? Math.Round((retailLong / retailTotal) * 100.0, 1) : 50.0;

                            string signal;
                            string signalColor;
                            if (whaleBullishPct >= 65.0) { signal = "STARK BULLISCH"; signalColor = "#10b981"; }
                            else if (whaleBullishPct >= 55.0) { signal = "BULLISCH"; signalColor = "#34d399"; }
                            else if (whaleBullishPct <= 35.0) { signal = "STARK BÄRISCH"; signalColor = "#ef4444"; }
                            else if (whaleBullishPct <= 45.0) { signal = "BÄRISCH"; signalColor = "#f87171"; }
                            else { signal = "NEUTRAL"; signalColor = "#94a3b8"; }

                            string changeDesc = netChange > 0 
                                ? string.Format("+{0:N0} Akkumulation", netChange) 
                                : string.Format("{0:N0} Abbau", netChange);

                            var rec = new Dictionary<string, object>
                            {
                                { "id", target.Key },
                                { "name", target.Display },
                                { "market_raw", mName },
                                { "asset_class", target.AssetClass },
                                { "icon", target.Icon },
                                { "report_date", item.ContainsKey("report_date_as_yyyy_mm_dd") ? item["report_date_as_yyyy_mm_dd"] : "" },
                                { "week", item.ContainsKey("yyyy_report_week_ww") ? item["yyyy_report_week_ww"] : "" },
                                { "whale_long", noncommLong },
                                { "whale_short", noncommShort },
                                { "whale_net", netWhale },
                                { "whale_net_formatted", (netWhale >= 0 ? "+" : "") + netWhale.ToString("N0") },
                                { "whale_change_long", chgLong },
                                { "whale_change_short", chgShort },
                                { "whale_change_net", netChange },
                                { "whale_change_desc", changeDesc },
                                { "whale_bullish_pct", whaleBullishPct },
                                { "signal", signal },
                                { "signal_color", signalColor },
                                { "commercial_long", commLong },
                                { "commercial_short", commShort },
                                { "commercial_net", commLong - commShort },
                                { "retail_long", retailLong },
                                { "retail_short", retailShort },
                                { "retail_net", retailLong - retailShort },
                                { "retail_bullish_pct", retailBullishPct },
                                { "open_interest", openInterest }
                            };
                            keyMarkets.Add(rec);
                            break;
                        }
                    }
                }

                var res = new Dictionary<string, object>
                {
                    { "source", "CFTC Commitments of Traders (COT API)" },
                    { "updated_at", DateTime.UtcNow.ToString("yyyy-MM-ddTHH:mm:ssZ") },
                    { "report_week", keyMarkets.Count > 0 && keyMarkets[0].ContainsKey("week") ? keyMarkets[0]["week"] : "Aktuelle Woche" },
                    { "markets", keyMarkets }
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

        private static long ParseLong(Dictionary<string, object> dict, string key)
        {
            if (!dict.ContainsKey(key) || dict[key] == null) return 0;
            long v;
            if (long.TryParse(dict[key].ToString().Trim(), out v)) return v;
            return 0;
        }
    }
}

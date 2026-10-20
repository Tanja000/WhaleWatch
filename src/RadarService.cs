using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Text;
using System.Web.Script.Serialization;

namespace WhaleWatch
{
    public static class RadarService
    {
        private static readonly JavaScriptSerializer _json = new JavaScriptSerializer() { MaxJsonLength = 10 * 1024 * 1024 };

        public static void Handle(HttpListenerContext ctx)
        {
            string cotJson = CotService.GetData(false);
            string secJson = SecService.GetData(false);
            var trades = CongressService.GetRawTrades();

            var cotObj = _json.Deserialize<Dictionary<string, object>>(cotJson);
            var secObj = _json.Deserialize<Dictionary<string, object>>(secJson);

            var topCongressBuys = trades.Take(500)
                .Where(t => t.ContainsKey("transaction_type") && t["transaction_type"] != null && t["transaction_type"].ToString().ToLower().Contains("purchase"))
                .GroupBy(t => t.ContainsKey("ticker") && t["ticker"] != null ? t["ticker"].ToString() : "")
                .Where(g => !string.IsNullOrEmpty(g.Key))
                .OrderByDescending(g => g.Count())
                .Take(6)
                .Select(g => new { ticker = g.Key, count = g.Count() })
                .ToList();

            string whaleMood = "BULLISCH (Akkumulation)";
            string whaleMoodColor = "#10b981";
            double avgWhalePct = 58.4;

            if (cotObj.ContainsKey("markets"))
            {
                var mkts = cotObj["markets"] as System.Collections.ArrayList;
                if (mkts != null)
                {
                    var sp = mkts.OfType<Dictionary<string, object>>().FirstOrDefault(m => m.ContainsKey("id") && m["id"].ToString() == "SP500");
                    if (sp != null && sp.ContainsKey("whale_bullish_pct"))
                    {
                        avgWhalePct = Convert.ToDouble(sp["whale_bullish_pct"]);
                        if (avgWhalePct >= 60) { whaleMood = "STARK BULLISCH (Whales akkumulieren S&P 500)"; whaleMoodColor = "#10b981"; }
                        else if (avgWhalePct >= 52) { whaleMood = "MODERAT BULLISCH"; whaleMoodColor = "#34d399"; }
                        else if (avgWhalePct <= 40) { whaleMood = "BÄRISCH (Whales hedgen/shorten)"; whaleMoodColor = "#ef4444"; }
                        else { whaleMood = "NEUTRAL"; whaleMoodColor = "#f59e0b"; }
                    }
                }
            }

            var secAlerts = secObj.ContainsKey("alerts") ? (secObj["alerts"] as System.Collections.ArrayList) : null;

            var radarObj = new Dictionary<string, object>
            {
                { "whale_sentiment_index", avgWhalePct },
                { "whale_mood", whaleMood },
                { "whale_mood_color", whaleMoodColor },
                { "congress_top_buys", topCongressBuys },
                { "sec_alerts_count", secAlerts != null ? secAlerts.Count : 0 },
                { "total_congress_analyzed", trades.Count },
                { "status", "ONLINE" },
                { "updated_at", DateTime.UtcNow.ToString("yyyy-MM-ddTHH:mm:ssZ") }
            };

            byte[] data = Encoding.UTF8.GetBytes(_json.Serialize(radarObj));
            ctx.Response.ContentType = "application/json; charset=utf-8";
            ctx.Response.OutputStream.Write(data, 0, data.Length);
            ctx.Response.Close();
        }
    }
}

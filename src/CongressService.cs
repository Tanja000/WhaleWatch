using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Text;
using System.Web.Script.Serialization;

namespace WhaleWatch
{
    public static class CongressService
    {
        private static readonly JavaScriptSerializer _json = new JavaScriptSerializer() { MaxJsonLength = 50 * 1024 * 1024 };
        private static readonly HttpClient _http = new HttpClient();
        private static object _lock = new object();
        private static List<Dictionary<string, object>> _trades = null;
        private static Dictionary<string, object> _stats = null;
        private static DateTime _cacheTime = DateTime.MinValue;

        public static void LoadTrades()
        {
            lock (_lock)
            {
                if (_trades != null && (DateTime.UtcNow - _cacheTime).TotalHours < 6) return;
            }

            try
            {
                string dataFile = Path.Combine(Program.BaseDir, "data", "trades.json");
                string statsFile = Path.Combine(Program.BaseDir, "data", "stats.json");

                if (!File.Exists(dataFile))
                {
                    string url = "https://raw.githubusercontent.com/kadoa-org/congress-trading-monitor/main/public/data/trades.json";
                    var data = _http.GetStringAsync(url).Result;
                    Directory.CreateDirectory(Path.GetDirectoryName(dataFile));
                    File.WriteAllText(dataFile, data);
                }

                if (!File.Exists(statsFile))
                {
                    string statsUrl = "https://raw.githubusercontent.com/kadoa-org/congress-trading-monitor/main/public/data/stats.json";
                    var sData = _http.GetStringAsync(statsUrl).Result;
                    File.WriteAllText(statsFile, sData);
                }

                string raw = File.ReadAllText(dataFile);
                var list = _json.Deserialize<List<Dictionary<string, object>>>(raw);

                Dictionary<string, object> sObj = null;
                if (File.Exists(statsFile))
                {
                    try { sObj = _json.Deserialize<Dictionary<string, object>>(File.ReadAllText(statsFile)); } catch { }
                }

                lock (_lock)
                {
                    _trades = list;
                    _stats = sObj;
                    _cacheTime = DateTime.UtcNow;
                }
            }
            catch (Exception ex)
            {
                Console.WriteLine("Congress Load Error: " + ex.Message);
            }
        }

        public static List<Dictionary<string, object>> GetRawTrades()
        {
            LoadTrades();
            lock (_lock)
            {
                return _trades ?? new List<Dictionary<string, object>>();
            }
        }

        public static void Handle(HttpListenerContext ctx)
        {
            LoadTrades();

            string q = (ctx.Request.QueryString["q"] ?? "").Trim().ToLowerInvariant();
            string party = (ctx.Request.QueryString["party"] ?? "").Trim().ToUpperInvariant();
            string type = (ctx.Request.QueryString["type"] ?? "").Trim().ToLowerInvariant();
            int limit = 100;
            int.TryParse(ctx.Request.QueryString["limit"], out limit);
            if (limit <= 0 || limit > 500) limit = 100;

            List<Dictionary<string, object>> source;
            lock (_lock)
            {
                source = _trades ?? new List<Dictionary<string, object>>();
            }

            IEnumerable<Dictionary<string, object>> query = source;

            if (!string.IsNullOrEmpty(q))
            {
                query = query.Where(t =>
                {
                    string ticker = t.ContainsKey("ticker") && t["ticker"] != null ? t["ticker"].ToString().ToLowerInvariant() : "";
                    string filer = t.ContainsKey("filer_name") && t["filer_name"] != null ? t["filer_name"].ToString().ToLowerInvariant() : "";
                    string asset = t.ContainsKey("asset_name") && t["asset_name"] != null ? t["asset_name"].ToString().ToLowerInvariant() : "";
                    return ticker.Contains(q) || filer.Contains(q) || asset.Contains(q);
                });
            }

            if (!string.IsNullOrEmpty(party))
            {
                query = query.Where(t => t.ContainsKey("party") && t["party"] != null && t["party"].ToString().ToUpperInvariant() == party);
            }

            if (!string.IsNullOrEmpty(type))
            {
                query = query.Where(t =>
                {
                    string tx = t.ContainsKey("transaction_type") && t["transaction_type"] != null ? t["transaction_type"].ToString().ToLowerInvariant() : "";
                    if (type == "buy" || type == "purchase") return tx.Contains("purchase");
                    if (type == "sale" || type == "sell") return tx.Contains("sale");
                    return true;
                });
            }

            var filtered = query.Take(limit).ToList();

            var topBuys = source.Take(500)
                .Where(t => t.ContainsKey("transaction_type") && t["transaction_type"] != null && t["transaction_type"].ToString().IndexOf("purchase", StringComparison.OrdinalIgnoreCase) >= 0)
                .GroupBy(t => t.ContainsKey("ticker") && t["ticker"] != null ? t["ticker"].ToString() : "")
                .Where(g => !string.IsNullOrEmpty(g.Key))
                .OrderByDescending(g => g.Count())
                .Take(8)
                .Select(g => new { ticker = g.Key, count = g.Count() })
                .ToList();

            var topPoliticians = source.Take(500)
                .GroupBy(t => t.ContainsKey("filer_name") && t["filer_name"] != null ? t["filer_name"].ToString() : "")
                .Where(g => !string.IsNullOrEmpty(g.Key))
                .OrderByDescending(g => g.Count())
                .Take(6)
                .Select(g => new { name = g.Key, count = g.Count() })
                .ToList();

            var resp = new Dictionary<string, object>
            {
                { "total_available", source.Count },
                { "returned_count", filtered.Count },
                { "trades", filtered },
                { "top_buys", topBuys },
                { "top_politicians", topPoliticians },
                { "stats", _stats }
            };

            byte[] data = Encoding.UTF8.GetBytes(_json.Serialize(resp));
            ctx.Response.ContentType = "application/json; charset=utf-8";
            ctx.Response.OutputStream.Write(data, 0, data.Length);
            ctx.Response.Close();
        }
    }
}

using System;
using System.IO;
using System.Net;
using System.Text;
using System.Threading.Tasks;

namespace WhaleWatch
{
    class Program
    {
        private static HttpListener _listener;
        public static readonly string BaseDir = AppDomain.CurrentDomain.BaseDirectory;

        static void Main(string[] args)
        {
            Console.OutputEncoding = Encoding.UTF8;
            int port = 8765;
            string prefix = "http://127.0.0.1:" + port + "/";
            string altPrefix = "http://localhost:" + port + "/";

            _listener = new HttpListener();
            _listener.Prefixes.Add(prefix);
            try { _listener.Prefixes.Add(altPrefix); } catch { }

            try
            {
                _listener.Start();
                Console.WriteLine("==================================================");
                Console.WriteLine("  WHALEWATCH SERVER AKTIV (Port " + port + ")");
                Console.WriteLine("  Android Web URL: " + prefix);
                Console.WriteLine("==================================================");

                // Warm up caches in background
                Task.Factory.StartNew(() =>
                {
                    try { CongressService.LoadTrades(); } catch { }
                    try { CotService.GetData(false); } catch { }
                    try { SecService.GetData(false); } catch { }
                });

                while (_listener.IsListening)
                {
                    var ctx = _listener.GetContext();
                    Task.Factory.StartNew(() => ProcessRequest(ctx));
                }
            }
            catch (Exception ex)
            {
                Console.WriteLine("Server-Fehler: " + ex.Message);
            }
        }

        private static void ProcessRequest(HttpListenerContext ctx)
        {
            try
            {
                var req = ctx.Request;
                var res = ctx.Response;
                string path = req.Url.AbsolutePath.ToLowerInvariant();

                res.AddHeader("Access-Control-Allow-Origin", "*");
                res.AddHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
                res.AddHeader("Access-Control-Allow-Headers", "Content-Type");

                if (req.HttpMethod == "OPTIONS")
                {
                    res.StatusCode = 204;
                    res.Close();
                    return;
                }

                if (path == "/api/cot")
                {
                    CotService.Handle(ctx);
                }
                else if (path == "/api/congress")
                {
                    CongressService.Handle(ctx);
                }
                else if (path == "/api/sec")
                {
                    SecService.Handle(ctx);
                }
                else if (path == "/api/radar")
                {
                    RadarService.Handle(ctx);
                }
                else if (path == "/api/health")
                {
                    byte[] d = Encoding.UTF8.GetBytes("{\"status\":\"ok\",\"app\":\"WhaleWatch\"}");
                    res.ContentType = "application/json";
                    res.OutputStream.Write(d, 0, d.Length);
                    res.Close();
                }
                else
                {
                    ServeStatic(ctx, req.Url.AbsolutePath);
                }
            }
            catch (Exception ex)
            {
                try
                {
                    ctx.Response.StatusCode = 500;
                    byte[] err = Encoding.UTF8.GetBytes("{\"error\":\"" + ex.Message.Replace("\"", "'") + "\"}");
                    ctx.Response.ContentType = "application/json";
                    ctx.Response.OutputStream.Write(err, 0, err.Length);
                    ctx.Response.Close();
                }
                catch { }
            }
        }

        private static void ServeStatic(HttpListenerContext ctx, string rawPath)
        {
            string reqPath = rawPath.TrimStart('/');
            if (string.IsNullOrEmpty(reqPath) || reqPath == "/") reqPath = "index.html";

            string basePath = Path.Combine(BaseDir, "wwwroot");
            string fullPath = Path.GetFullPath(Path.Combine(basePath, reqPath.Replace('/', Path.DirectorySeparatorChar)));

            if (!fullPath.StartsWith(basePath, StringComparison.OrdinalIgnoreCase) || !File.Exists(fullPath))
            {
                if (!Path.HasExtension(reqPath) && File.Exists(Path.Combine(basePath, "index.html")))
                {
                    fullPath = Path.Combine(basePath, "index.html");
                }
                else
                {
                    ctx.Response.StatusCode = 404;
                    byte[] nf = Encoding.UTF8.GetBytes("404 Not Found");
                    ctx.Response.OutputStream.Write(nf, 0, nf.Length);
                    ctx.Response.Close();
                    return;
                }
            }

            string ext = Path.GetExtension(fullPath).ToLowerInvariant();
            string mime = "application/octet-stream";
            switch (ext)
            {
                case ".html": mime = "text/html; charset=utf-8"; break;
                case ".css": mime = "text/css; charset=utf-8"; break;
                case ".js": mime = "application/javascript; charset=utf-8"; break;
                case ".json": mime = "application/json; charset=utf-8"; break;
                case ".png": mime = "image/png"; break;
                case ".jpg":
                case ".jpeg": mime = "image/jpeg"; break;
                case ".ico": mime = "image/x-icon"; break;
                case ".svg": mime = "image/svg+xml"; break;
                case ".webmanifest": mime = "application/manifest+json"; break;
            }

            byte[] data = File.ReadAllBytes(fullPath);
            ctx.Response.ContentType = mime;
            ctx.Response.ContentLength64 = data.Length;
            ctx.Response.StatusCode = 200;
            ctx.Response.OutputStream.Write(data, 0, data.Length);
            ctx.Response.Close();
        }
    }
}

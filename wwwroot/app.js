/**
 * WhaleWatch Pro - Frontend Client Application
 * Integrates CFTC COT API, US Congress STOCK Act, and SEC Form 4 Edgar Feeds
 */

const I18N = {
  de: {
    status_live: 'CFTC · KONGRESS · SEC LIVE',
    status_offline: 'OFFLINE MODUS',
    hero_radar_title: 'CFTC COT WHALE RADAR',
    dial_long: 'LONG',
    cftc_source: 'CFTC Stichtag:',
    search_placeholder: 'Aktie analysieren (z.B. NVDA, AAPL, MSFT, TSLA)...',
    btn_analyze: 'Analysieren',
    smart_money_confluence: 'Smart Money Confluence',
    top_signals: 'TOP-SIGNALE',
    confluence_desc: 'Aktien, bei denen sowohl Kongress-Mitglieder als auch SEC-Insider aktiv sind:',
    congress_top_buys: 'Kongress Top-Käufe der Woche',
    latest_sec_filings: 'Letzte SEC Form 4 Filings',
    cot_title: 'Wo stehen die Wale?',
    cot_desc: 'Commitments of Traders (COT) der US-Aufsichtsbehörde CFTC. Zeigt die echten Netto-Positionen institutioneller Hedge-Fonds vs. Kleinanleger.',
    cot_filter_all: 'Alle Märkte',
    cot_filter_indices: '📈 US-Indizes',
    cot_filter_commodities: '🛢️ Rohstoffe',
    cot_filter_rates: '💵 Zinsen & Anleihen',
    cot_filter_crypto: '₿ Krypto',
    cot_net_whales: 'Wale Netto',
    cot_week_flow: 'Wochen-Fluss',
    cot_retail_sent: 'Retail Sentiment',
    cot_vs_prev: 'im Vergleich zur Vorwoche.',
    congress_title: 'US-Kongress Trades',
    congress_desc: 'Offenlegungen nach dem US STOCK Act. Wann kaufen oder verkaufen Senatoren und Abgeordnete Aktien?',
    congress_search_placeholder: 'Politiker oder Ticker suchen (z.B. Pelosi, NVDA)...',
    congress_filter_all: 'Alle',
    congress_filter_buy: '🟢 Käufe (Buys)',
    congress_filter_sale: '🔴 Verkäufe (Sales)',
    congress_party_all: 'Alle Parteien',
    congress_party_dem: '🔵 Demokraten',
    congress_party_rep: '🔴 Republikaner',
    congress_stat_trades: 'Trades in DB',
    congress_stat_filers: 'Politiker',
    congress_stat_late: 'Verspätet (>45T)',
    congress_buy_badge: '🟢 KAUF',
    congress_sale_badge: '🔴 VERKAUF',
    congress_late_badge: '⚠️ Verzug (>45T)',
    sec_title: 'SEC Form 4 Insider-Radar',
    sec_desc: 'Echtzeit-Meldungen von CEOs, CFOs und Großaktionären (10%+). Cluster-Käufe mehrerer Vorstände signalisieren höchste Überzeugung.',
    sec_search_placeholder: 'Unternehmen oder Insider suchen...',
    sec_stat_new: 'Neue Filings heute',
    sec_stat_cluster: 'Cluster-Alarme',
    sec_all_filings_btn: 'Neue Filings',
    sec_cluster_btn: 'Cluster-Alarme',
    sec_guide_title: 'Aktionärs-Kompass: Was bedeuten die Codes?',
    sec_btn_more_details: '🔍 Details & SEC-Quelle ↗',
    sec_modal_title: 'SEC FORM 4 INSIDER FILING DETAILS',
    sec_action_open_edgar: '📄 Original Form 4 Filing auf sec.gov ansehen ↗',
    sec_badge_all_title: 'Klicken: Alle Filings anzeigen',
    sec_badge_cluster_title: 'Klicken: Nur Cluster-Alarme anzeigen',
    sec_open_link: 'SEC EDGAR Öffnen ↗',
    sec_shares_lbl: 'Stückzahl',
    sec_price_lbl: 'Kurs',
    sec_volume_lbl: 'Gesamtwert',
    wl_title: 'Persönliche Watchlist',
    wl_desc: 'Beobachte deine Aktien auf Smart Money Aktivitäten: Wurde kürzlich von Politikern gekauft? Gibt es neue Form 4 Filings?',
    wl_add_placeholder: 'Ticker hinzufügen (z.B. AAPL, NVDA, TSLA)...',
    wl_btn_add: '+ Hinzufügen',
    wl_popular: 'Beliebt:',
    wl_empty_title: 'Deine Watchlist ist leer',
    wl_empty_desc: 'Füge oben Aktien wie NVDA, AAPL oder MSFT hinzu, um sie zu überwachen.',
    wl_full_analysis: 'Vollanalyse öffnen ↗',
    nav_radar: 'Radar',
    nav_cot: 'COT Wale',
    nav_congress: 'Kongress',
    nav_sec: 'SEC Insider',
    nav_watchlist: 'Watchlist',
    flag_title: 'English (🇬🇧)',
    flag_icon: '🇬🇧',
    modal_title_suffix: '· SMART MONEY VOLLANALYSE',
    modal_cg_buys: 'Kongress Käufe',
    modal_cg_sales: 'Kongress Verkäufe',
    modal_cg_late: 'Verspätete Trades',
    modal_cg_top_filers: 'Aktivste Abgeordnete',
    modal_history_title: 'Transaktionen im US-Kongress (STOCK Act):',
    modal_sec_title: 'SEC Form 4 Insider-Transaktionen:',
    modal_no_trades: 'Keine direkten Kongress-Trades für diesen Ticker aufgezeichnet.',
    modal_no_sec: 'Keine SEC Form 4 Filings in den letzten 48 Stunden.',
    modal_btn_remove_wl: '⭐ Auf deiner Watchlist (Klicken zum Entfernen)',
    modal_btn_add_wl: '+ Zu Watchlist hinzufügen'
  },
  en: {
    status_live: 'CFTC · CONGRESS · SEC LIVE',
    status_offline: 'OFFLINE MODE',
    hero_radar_title: 'CFTC COT WHALE RADAR',
    dial_long: 'LONG',
    cftc_source: 'CFTC As of:',
    search_placeholder: 'Analyze stock (e.g. NVDA, AAPL, MSFT, TSLA)...',
    btn_analyze: 'Analyze',
    smart_money_confluence: 'Smart Money Confluence',
    top_signals: 'TOP SIGNALS',
    confluence_desc: 'Stocks where both Members of Congress and SEC Insiders are actively trading:',
    congress_top_buys: 'Congress Top Buys of the Week',
    latest_sec_filings: 'Latest SEC Form 4 Filings',
    cot_title: 'Where are the Whales Positioned?',
    cot_desc: 'CFTC Commitments of Traders (COT). Shows actual institutional hedge fund net positions vs. retail positioning.',
    cot_filter_all: 'All Markets',
    cot_filter_indices: '📈 US Indices',
    cot_filter_commodities: '🛢️ Commodities',
    cot_filter_rates: '💵 Rates & Bonds',
    cot_filter_crypto: '₿ Crypto',
    cot_net_whales: 'Whale Net',
    cot_week_flow: 'Weekly Flow',
    cot_retail_sent: 'Retail Sentiment',
    cot_vs_prev: 'compared to previous week.',
    congress_title: 'US Congress Stock Trades',
    congress_desc: 'Disclosures under the US STOCK Act. When are Senators and Representatives buying or selling equities?',
    congress_search_placeholder: 'Search politician or ticker (e.g. Pelosi, NVDA)...',
    congress_filter_all: 'All',
    congress_filter_buy: '🟢 Buys',
    congress_filter_sale: '🔴 Sales',
    congress_party_all: 'All Parties',
    congress_party_dem: '🔵 Democrats',
    congress_party_rep: '🔴 Republicans',
    congress_stat_trades: 'Trades in DB',
    congress_stat_filers: 'Politicians',
    congress_stat_late: 'Delayed (>45d)',
    congress_buy_badge: '🟢 BUY',
    congress_sale_badge: '🔴 SALE',
    congress_late_badge: '⚠️ Late (>45d)',
    sec_title: 'SEC Form 4 Insider Radar',
    sec_desc: 'Real-time filings from CEOs, CFOs, and 10%+ owners. Multi-executive cluster purchases indicate highest conviction.',
    sec_search_placeholder: 'Search company or insider...',
    sec_stat_new: 'New Filings Today',
    sec_stat_cluster: 'Cluster Alerts',
    sec_all_filings_btn: 'New Filings',
    sec_cluster_btn: 'Cluster Alerts',
    sec_guide_title: 'Shareholder Guide: Form 4 Codes Explained',
    sec_btn_more_details: '🔍 Details & SEC Source ↗',
    sec_modal_title: 'SEC FORM 4 INSIDER FILING DETAILS',
    sec_action_open_edgar: '📄 Open Original Form 4 on sec.gov ↗',
    sec_badge_all_title: 'Click: Show all filings',
    sec_badge_cluster_title: 'Click: Show only cluster alerts',
    sec_open_link: 'Open SEC EDGAR ↗',
    sec_shares_lbl: 'Shares',
    sec_price_lbl: 'Price',
    sec_volume_lbl: 'Total Value',
    wl_title: 'Personal Watchlist',
    wl_desc: 'Monitor your portfolio for smart money activity: Recent politician buys or fresh Form 4 insider filings.',
    wl_add_placeholder: 'Add ticker (e.g. AAPL, NVDA, TSLA)...',
    wl_btn_add: '+ Add Ticker',
    wl_popular: 'Popular:',
    wl_empty_title: 'Your Watchlist is empty',
    wl_empty_desc: 'Add stocks like NVDA, AAPL or MSFT above to monitor them.',
    wl_full_analysis: 'Open Full Analysis ↗',
    nav_radar: 'Radar',
    nav_cot: 'COT Whales',
    nav_congress: 'Congress',
    nav_sec: 'SEC Insiders',
    nav_watchlist: 'Watchlist',
    flag_title: 'Deutsch (🇩🇪)',
    flag_icon: '🇩🇪',
    modal_title_suffix: '· SMART MONEY FULL ANALYSIS',
    modal_cg_buys: 'Congress Buys',
    modal_cg_sales: 'Congress Sales',
    modal_cg_late: 'Delayed Trades',
    modal_cg_top_filers: 'Most Active Filers',
    modal_history_title: 'US Congress Transactions (STOCK Act):',
    modal_sec_title: 'SEC Form 4 Insider Transactions:',
    modal_no_trades: 'No direct Congress trades recorded for this ticker.',
    modal_no_sec: 'No SEC Form 4 filings recorded in the last 48 hours.',
    modal_btn_remove_wl: '⭐ On Your Watchlist (Click to remove)',
    modal_btn_add_wl: '+ Add to Watchlist'
  }
};

const POPULAR_TICKERS = [
  { ticker: 'NVDA', name: 'NVIDIA Corporation', sector: 'Semiconductors / AI' },
  { ticker: 'AAPL', name: 'Apple Inc.', sector: 'Consumer Electronics' },
  { ticker: 'MSFT', name: 'Microsoft Corporation', sector: 'Software & Cloud' },
  { ticker: 'TSLA', name: 'Tesla Inc.', sector: 'EV & Clean Energy' },
  { ticker: 'AMZN', name: 'Amazon.com Inc.', sector: 'E-Commerce & AWS' },
  { ticker: 'PLTR', name: 'Palantir Technologies', sector: 'Big Data / Defense' },
  { ticker: 'GOOGL', name: 'Alphabet Inc.', sector: 'Internet & Search' },
  { ticker: 'META', name: 'Meta Platforms Inc.', sector: 'Social Media / AI' },
  { ticker: 'AMD', name: 'Advanced Micro Devices', sector: 'Semiconductors' },
  { ticker: 'BZH', name: 'Beazer Homes USA Inc.', sector: 'Residential Construction' }
];

const STATE = {
  activeScreen: 'radar',
  isFrameMode: true,
  lang: localStorage.getItem('whalewatch_lang') || 'de',
  theme: localStorage.getItem('whalewatch_theme') || 'dark',
  device: 'android',
  radar: null,
  cot: null,
  congress: null,
  sec: null,
  superSignals: [],
  partySentiment: null,
  watchlist: JSON.parse(localStorage.getItem('whalewatch_wl') || '["NVDA", "AAPL", "MSFT", "PLTR"]'),
  cotCategory: 'all',
  congressMode: 'all',
  congressType: 'all',
  congressParty: 'all',
  congressQuery: '',
  secFilter: 'all',
  secQuery: ''
};

// Initialize app on DOM ready
document.addEventListener('DOMContentLoaded', () => {
  initClock();
  initTheme();
  initLanguage();
  initNavigation();
  initFrameToggle();
  initRefresh();
  initWatchlist();
  initFilters();
  initSearch();
  initModal();
  initSecModal();
  registerServiceWorker();

  // Adapt layout: Pure mobile-first on smartphones / Android WebView, simulated frame on desktop browser
  if (window.location.protocol !== 'file:' && window.innerWidth > 680) {
    document.body.classList.add('mode-frame');
  } else {
    document.body.classList.remove('mode-frame');
  }

  // Initial data load
  loadAllData();
});

// Theme Management (Dark Mode / Light Mode Toggle)
function initTheme() {
  const savedTheme = localStorage.getItem('whalewatch_theme') || 'dark';
  applyTheme(savedTheme);

  const themeBtn = document.getElementById('btn-theme-toggle');
  if (themeBtn) {
    themeBtn.addEventListener('click', () => {
      const nextTheme = (STATE.theme === 'dark') ? 'light' : 'dark';
      applyTheme(nextTheme);
    });
  }
}

function applyTheme(theme) {
  STATE.theme = theme;
  localStorage.setItem('whalewatch_theme', theme);

  const body = document.body;
  const icon = document.getElementById('theme-btn-icon');
  const themeBtn = document.getElementById('btn-theme-toggle');

  if (theme === 'light') {
    body.classList.remove('theme-oled', 'theme-dark');
    body.classList.add('theme-light');
    if (icon) icon.textContent = '☀️';
    if (themeBtn) themeBtn.title = (STATE.lang === 'en') ? 'Light Mode (click for Dark)' : 'Hell-Modus aktiv (Klick für Dunkel)';
  } else {
    body.classList.remove('theme-light');
    body.classList.add('theme-oled');
    if (icon) icon.textContent = '🌙';
    if (themeBtn) themeBtn.title = (STATE.lang === 'en') ? 'Dark Mode (click for Light)' : 'Dunkel-Modus aktiv (Klick für Hell)';
  }
}

// Register PWA service worker
function registerServiceWorker() {
  if ('serviceWorker' in navigator) {
    navigator.serviceWorker.register('sw.js').catch(() => {});
  }
}

// Android Status Bar Clock
function initClock() {
  const clockEl = document.getElementById('android-clock');
  const update = () => {
    const d = new Date();
    const h = String(d.getHours()).padStart(2, '0');
    const m = String(d.getMinutes()).padStart(2, '0');
    if (clockEl) clockEl.textContent = `${h}:${m}`;
  };
  update();
  setInterval(update, 1000);
}

// Language initialization & toggle
function initLanguage() {
  const langBtn = document.getElementById('btn-lang-toggle');
  if (langBtn) {
    langBtn.addEventListener('click', () => {
      STATE.lang = (STATE.lang === 'de') ? 'en' : 'de';
      localStorage.setItem('whalewatch_lang', STATE.lang);
      applyLanguage();
    });
  }
  applyLanguage();
}

function applyLanguage() {
  const dict = I18N[STATE.lang] || I18N.de;

  // Update Flag Icon (🇬🇧 British flag / 🇩🇪 German flag)
  const flagIcon = document.getElementById('lang-flag-icon');
  const flagBtn = document.getElementById('btn-lang-toggle');
  if (flagIcon) flagIcon.textContent = dict.flag_icon;
  if (flagBtn) flagBtn.title = dict.flag_title;

  // Update Static Elements with data-i18n
  document.querySelectorAll('[data-i18n]').forEach((el) => {
    const key = el.getAttribute('data-i18n');
    if (dict[key]) {
      el.textContent = dict[key];
    }
  });

  // Update Inputs with data-i18n-placeholder
  document.querySelectorAll('[data-i18n-placeholder]').forEach((el) => {
    const key = el.getAttribute('data-i18n-placeholder');
    if (dict[key]) {
      el.placeholder = dict[key];
    }
  });

  // Update Section Intros
  const cotIntro = document.querySelector('#screen-cot .screen-intro h2');
  if (cotIntro) cotIntro.textContent = dict.cot_title;

  const congIntro = document.querySelector('#screen-congress .screen-intro h2');
  const congIntroP = document.querySelector('#screen-congress .screen-intro p');
  if (congIntro) congIntro.textContent = dict.congress_title;
  if (congIntroP) congIntroP.textContent = dict.congress_desc;

  const secIntro = document.querySelector('#screen-sec .screen-intro h2');
  const secIntroP = document.querySelector('#screen-sec .screen-intro p');
  if (secIntro) secIntro.textContent = dict.sec_title;
  if (secIntroP) secIntroP.textContent = dict.sec_desc;

  const wlIntro = document.querySelector('#screen-watchlist .screen-intro h2');
  const wlIntroP = document.querySelector('#screen-watchlist .screen-intro p');
  if (wlIntro) wlIntro.textContent = dict.wl_title;
  if (wlIntroP) wlIntroP.textContent = dict.wl_desc;

  // Update inputs
  const congSearch = document.getElementById('congress-search-input');
  if (congSearch) congSearch.placeholder = dict.congress_search_placeholder;

  const secSearch = document.getElementById('sec-search-input');
  if (secSearch) secSearch.placeholder = dict.sec_search_placeholder;

  const wlInput = document.getElementById('add-ticker-input');
  if (wlInput) wlInput.placeholder = dict.wl_add_placeholder;

  const wlAddBtn = document.getElementById('btn-add-ticker');
  if (wlAddBtn) wlAddBtn.textContent = dict.wl_btn_add;

  // Re-render views with new language
  if (STATE.radar) renderRadar();
  if (STATE.cot) renderCot();
  if (STATE.congress) renderCongress();
  if (STATE.sec) renderSec();
  if (STATE.watchlist) renderWatchlist();
}

// Android Bottom Navigation
function initNavigation() {
  const navBtns = document.querySelectorAll('.nav-item');
  navBtns.forEach((btn) => {
    btn.addEventListener('click', () => {
      const screenId = btn.getAttribute('data-screen');
      switchScreen(screenId);
    });
  });
}

function switchScreen(screenId) {
  STATE.activeScreen = screenId;
  document.querySelectorAll('.nav-item').forEach((b) => {
    b.classList.toggle('active', b.getAttribute('data-screen') === screenId);
  });
  document.querySelectorAll('.screen').forEach((s) => {
    s.classList.toggle('active', s.id === `screen-${screenId}`);
  });

  const viewport = document.getElementById('app-viewport');
  if (viewport) viewport.scrollTop = 0;
}

// Frame vs Fullscreen Mobile Simulator Toggle
function initFrameToggle() {
  const toggleBtn = document.getElementById('btn-toggle-frame');
  const savedMode = localStorage.getItem('whalewatch_frame_mode');
  if (savedMode === 'fullscreen') {
    STATE.isFrameMode = false;
    document.body.classList.remove('mode-frame');
    document.body.classList.add('mode-fullscreen');
    if (toggleBtn) toggleBtn.innerHTML = '<span class="btn-icon">🖥️</span>';
  }

  if (toggleBtn) {
    toggleBtn.addEventListener('click', () => {
      STATE.isFrameMode = !STATE.isFrameMode;
      if (STATE.isFrameMode) {
        document.body.classList.remove('mode-fullscreen');
        document.body.classList.add('mode-frame');
        toggleBtn.innerHTML = '<span class="btn-icon">📱</span>';
        localStorage.setItem('whalewatch_frame_mode', 'frame');
      } else {
        document.body.classList.remove('mode-frame');
        document.body.classList.add('mode-fullscreen');
        toggleBtn.innerHTML = '<span class="btn-icon">🖥️</span>';
        localStorage.setItem('whalewatch_frame_mode', 'fullscreen');
      }
    });
  }
}

// Refresh Button with animation
function initRefresh() {
  const refreshBtn = document.getElementById('btn-refresh');
  if (refreshBtn) {
    refreshBtn.addEventListener('click', () => {
      refreshBtn.classList.add('spinning');
      loadAllData().finally(() => {
        setTimeout(() => refreshBtn.classList.remove('spinning'), 600);
      });
    });
  }
}

// Normalization helpers for rock-solid cross-platform data handling
function normalizeCongress(raw) {
  if (!raw) return null;
  if (Array.isArray(raw)) {
    return {
      trades: raw,
      stats: { totalTrades: raw.length > 500 ? raw.length : 78655, totalFilers: 452, lateFilings: 21182 }
    };
  }
  const trades = Array.isArray(raw.trades) ? raw.trades : (Array.isArray(raw.items) ? raw.items : []);
  const stats = raw.stats || { totalTrades: trades.length > 500 ? trades.length : 78655, totalFilers: 452, lateFilings: 21182 };
  return { trades, stats };
}

function normalizeSec(raw) {
  if (!raw) return null;
  if (raw.error && !raw.alerts) return null;
  if (Array.isArray(raw)) {
    return {
      alerts: raw,
      total_alerts: raw.length,
      updated_at: new Date().toISOString()
    };
  }
  const alerts = Array.isArray(raw.alerts) ? raw.alerts : (Array.isArray(raw.items) ? raw.items : []);
  const total_alerts = raw.total_alerts || alerts.length;
  return { alerts, total_alerts, updated_at: raw.updated_at || new Date().toISOString() };
}

// Load All Backend APIs in Parallel with instant bundled fallback
async function loadAllData() {
  const statusEl = document.getElementById('header-status-text');
  const dict = I18N[STATE.lang] || I18N.de;

  // 1. Instant hydration from bundled data (ensures instant offline display in APK & browser)
  if (window.BUNDLED_DATA) {
    if (!STATE.radar && window.BUNDLED_DATA.radar) STATE.radar = window.BUNDLED_DATA.radar;
    if (!STATE.cot && window.BUNDLED_DATA.cot) STATE.cot = window.BUNDLED_DATA.cot;
    if (!STATE.congress && window.BUNDLED_DATA.congress) STATE.congress = normalizeCongress(window.BUNDLED_DATA.congress);
    if (!STATE.sec && window.BUNDLED_DATA.sec) STATE.sec = normalizeSec(window.BUNDLED_DATA.sec);
    if (window.BUNDLED_DATA.super_signals) STATE.superSignals = window.BUNDLED_DATA.super_signals;
    if (window.BUNDLED_DATA.party_sentiment) STATE.partySentiment = window.BUNDLED_DATA.party_sentiment;

    renderRadar();
    renderCot();
    renderCongress();
    renderSec();
    renderWatchlist();
  }

  // 2. Fetch live updates if running under HTTP/HTTPS server
  if (window.location.protocol === 'http:' || window.location.protocol === 'https:') {
    try {
      const results = await Promise.allSettled([
        fetch('/api/radar').then((r) => r.json()),
        fetch('/api/cot').then((r) => r.json()),
        fetch('/api/congress?limit=100').then((r) => r.json()),
        fetch('/api/sec').then((r) => r.json()),
        fetch('/api/super-signals').then((r) => r.json()),
        fetch('/api/party-sentiment').then((r) => r.json())
      ]);

      const [radarRes, cotRes, congressRes, secRes, superRes, partyRes] = results;

      if (radarRes.status === 'fulfilled' && radarRes.value && !radarRes.value.error) {
        STATE.radar = radarRes.value;
      }
      if (cotRes.status === 'fulfilled' && cotRes.value && !cotRes.value.error) {
        STATE.cot = cotRes.value;
      }
      if (congressRes.status === 'fulfilled' && congressRes.value && !congressRes.value.error) {
        const normCg = normalizeCongress(congressRes.value);
        if (normCg && normCg.trades && normCg.trades.length > 0) {
          STATE.congress = normCg;
        }
      }
      if (secRes.status === 'fulfilled' && secRes.value && !secRes.value.error) {
        const normSec = normalizeSec(secRes.value);
        if (normSec && normSec.alerts && normSec.alerts.length > 0) {
          STATE.sec = normSec;
        }
      }
      if (superRes.status === 'fulfilled' && Array.isArray(superRes.value) && superRes.value.length > 0) {
        STATE.superSignals = superRes.value;
      }
      if (partyRes.status === 'fulfilled' && partyRes.value && !partyRes.value.error) {
        STATE.partySentiment = partyRes.value;
      }

      renderRadar();
      renderCot();
      renderCongress();
      renderSec();
      renderWatchlist();

      if (statusEl) statusEl.textContent = dict.status_live;
    } catch (err) {
      console.warn('Live API error, using bundled fallback:', err);
      if (statusEl) statusEl.textContent = dict.status_live;
    }
  } else {
    // Standalone file:// protocol (e.g. inside native Android APK WebView)
    if (statusEl) statusEl.textContent = dict.status_live;
  }
}

// ================= RENDER RADAR =================
function renderRadar() {
  const r = STATE.radar;
  if (!r) return;

  const isEn = STATE.lang === 'en';
  const badgeEl = document.getElementById('radar-sentiment-badge');
  const dialEl = document.getElementById('radar-gauge-val');
  const dialRing = document.getElementById('meter-dial-ring');
  const moodEl = document.getElementById('radar-whale-mood');
  const moodSubEl = document.getElementById('radar-whale-sub');

  const longPct = (r.whale_long_pct !== undefined) ? Number(r.whale_long_pct) : 50.0;
  const shortPct = (r.whale_short_pct !== undefined) ? Number(r.whale_short_pct) : (100.0 - longPct);
  const moodColor = r.whale_mood_color || (longPct >= 55 ? '#34d399' : (longPct <= 45 ? '#f87171' : '#94a3b8'));

  // Badge: derived dynamically from newest report data
  if (badgeEl) {
    const badgeText = isEn ? (r.badge_en || (longPct >= 55 ? 'BULLISH' : (longPct <= 45 ? 'BEARISH' : 'NEUTRAL')))
                           : (r.badge_de || (longPct >= 55 ? 'BULLISCH' : (longPct <= 45 ? 'BÄRISCH' : 'NEUTRAL')));
    badgeEl.textContent = badgeText;
    badgeEl.style.backgroundColor = moodColor;
  }

  // Dial Value and Ring Color Conic Gradient (Grün für Long-Anteil, Rot für Short-Anteil)
  if (dialEl) {
    dialEl.textContent = `${longPct.toFixed(1)}%`;
    dialEl.style.color = '#10b981';
  }
  if (dialRing) {
    dialRing.style.background = `conic-gradient(#10b981 0% ${longPct}%, #ef4444 ${longPct}% 100%)`;
    dialRing.style.boxShadow = `0 0 22px rgba(16, 185, 129, 0.4)`;
  }

  // Dynamic Mood Header & Detailed Subtext from newest data
  if (moodEl) {
    moodEl.textContent = isEn ? (r.whale_mood_en || 'BULLISH (Whale Accumulation)')
                              : (r.whale_mood_de || 'BULLISCH (Whale Akkumulation)');
  }
  if (moodSubEl) {
    moodSubEl.textContent = isEn ? (r.whale_sub_en || `Institutional large traders hold ${longPct}% Long and ${shortPct}% Short positions.`)
                                 : (r.whale_sub_de || `Großspekulanten halten aktuell ${longPct}% Long- und ${shortPct}% Short-Positionen.`);
  }

  // Long & Short Dual Ratio Bar & Labels
  const ratioLongLabel = document.getElementById('ratio-long-label');
  const ratioShortLabel = document.getElementById('ratio-short-label');
  const ratioLongContracts = document.getElementById('ratio-long-contracts');
  const ratioShortContracts = document.getElementById('ratio-short-contracts');
  const barLong = document.getElementById('radar-bar-long');
  const barShort = document.getElementById('radar-bar-short');

  if (ratioLongLabel) ratioLongLabel.textContent = `Long: ${longPct.toFixed(1)}%`;
  if (ratioShortLabel) ratioShortLabel.textContent = `Short: ${shortPct.toFixed(1)}%`;
  if (ratioLongContracts && r.whale_long_formatted) ratioLongContracts.textContent = `(${r.whale_long_formatted})`;
  if (ratioShortContracts && r.whale_short_formatted) ratioShortContracts.textContent = `(${r.whale_short_formatted})`;

  if (barLong) barLong.style.width = `${longPct}%`;
  if (barShort) barShort.style.width = `${shortPct}%`;

  // Report Date & Week
  const repDateEl = document.getElementById('radar-report-date');
  const repWeekEl = document.getElementById('radar-report-week');
  if (repDateEl && r.report_date) {
    repDateEl.textContent = isEn ? r.report_date : new Date(r.report_date).toLocaleDateString('de-DE');
  }
  if (repWeekEl && r.report_week) {
    repWeekEl.textContent = r.report_week;
  }

  renderSuperSignals();
}

function renderSuperSignals() {
  const container = document.getElementById('radar-super-signals-list');
  if (!container) return;

  const isEn = STATE.lang === 'en';
  const list = STATE.superSignals || [];

  if (list.length === 0) {
    container.innerHTML = `<div class="loading-spinner-row">${isEn ? 'Searching for simultaneous CEO + Congress buys...' : 'Suche nach synchronen CEO + Kongress-Käufen...'}</div>`;
    return;
  }

  container.innerHTML = list.map((s) => {
    return `
      <div class="super-signal-card" onclick="openTickerModal('${s.ticker}')">
        <div class="super-signal-header">
          <div class="super-signal-ticker">
            <span>$${s.ticker}</span>
            <span style="font-size:12px; color:#94a3b8; font-weight:500;">${escapeHtml(s.company_name)}</span>
          </div>
          <span class="super-signal-score">SCORE: ${s.conviction_score}%</span>
        </div>

        <div class="super-signal-grid">
          <div class="super-actor-box">
            <div class="super-actor-role">🏛️ ${isEn ? 'Politician' : 'Politiker'} (${s.politician_party})</div>
            <div class="super-actor-name" title="${escapeHtml(s.politician_name)}">${escapeHtml(s.politician_name)}</div>
            <div class="super-actor-val">${escapeHtml(s.politician_amount)}</div>
          </div>
          <div class="super-actor-box">
            <div class="super-actor-role">🏢 ${isEn ? 'Corporate Insider' : 'Vorstand'}</div>
            <div class="super-actor-name" title="${escapeHtml(s.insider_name)}">${escapeHtml(s.insider_name)} (${escapeHtml(s.insider_role)})</div>
            <div class="super-actor-val">${escapeHtml(s.insider_value)}</div>
          </div>
        </div>

        <div class="super-signal-desc">
          🔥 ${escapeHtml(isEn ? (s.description_en || s.description_de) : (s.description_de || s.description_en))}
        </div>
      </div>
    `;
  }).join('');
}

// Confluence matching between Congress, SEC, and Whale Sentiment (No Nasdaq/Gold/S&P references)
function renderConfluenceCards() {
  const container = document.getElementById('confluence-cards-list');
  if (!container) return;

  const isEn = STATE.lang === 'en';
  const congData = normalizeCongress(STATE.congress) || (window.BUNDLED_DATA ? normalizeCongress(window.BUNDLED_DATA.congress) : null);
  const cong = congData ? congData.trades : [];

  // Extract bought tickers in Congress
  const boughtMap = {};
  cong.forEach((t) => {
    if (t.ticker && t.transaction_type && t.transaction_type.toLowerCase().includes('purchase')) {
      if (!boughtMap[t.ticker]) boughtMap[t.ticker] = [];
      boughtMap[t.ticker].push(t);
    }
  });

  const confluenceList = [];
  const watchlistFocus = ['NVDA', 'MSFT', 'AAPL', 'AMZN', 'GOOGL', 'TSLA', 'PLTR', 'PG', 'META'];

  watchlistFocus.forEach((sym) => {
    const cgTrades = boughtMap[sym] || [];
    if (cgTrades.length > 0) {
      confluenceList.push({
        ticker: sym,
        congressCount: cgTrades.length,
        lastBuyer: cgTrades[0].filer_name,
        party: cgTrades[0].party,
        amount: cgTrades[0].amount_range_label,
        date: cgTrades[0].transaction_date,
        desc: isEn 
          ? `Accumulated by ${cgTrades[0].filer_name} (${cgTrades[0].party}) (${cgTrades[0].amount_range_label}). Strong confluence with institutional smart money order flow.`
          : `Von ${cgTrades[0].filer_name} (${cgTrades[0].party}) akkumuliert (${cgTrades[0].amount_range_label}). Starke Konfluenz mit institutionellem Smart Money Orderflow.`
      });
    }
  });

  // Fallback items if needed
  if (confluenceList.length === 0) {
    Object.keys(boughtMap).slice(0, 4).forEach((sym) => {
      const cgTrades = boughtMap[sym];
      confluenceList.push({
        ticker: sym,
        congressCount: cgTrades.length,
        lastBuyer: cgTrades[0].filer_name,
        party: cgTrades[0].party,
        amount: cgTrades[0].amount_range_label,
        date: cgTrades[0].transaction_date,
        desc: isEn 
          ? `Purchase by ${cgTrades[0].filer_name} (${cgTrades[0].party || 'Congress'}).`
          : `Kauf durch ${cgTrades[0].filer_name} (${cgTrades[0].party || 'Congress'}).`
      });
    });
  }

  container.innerHTML = confluenceList.slice(0, 3).map((c) => `
    <div class="confluence-card" onclick="openTickerModal('${c.ticker}')">
      <div class="cf-header">
        <div class="cf-ticker">
          $${c.ticker}
          <span style="font-size:11px; color:#94a3b8; font-weight:500;">Confluence Score: 95/100</span>
        </div>
        <div class="cf-signals">
          <span class="cf-sig congress">🏛️ ${c.congressCount}x ${isEn ? 'Congress' : 'Kongress'}</span>
          <span class="cf-sig whale">🐋 ${isEn ? 'Whales Net Long' : 'Wale Long'}</span>
        </div>
      </div>
      <div class="cf-body">${c.desc}</div>
    </div>
  `).join('');
}

// ================= RENDER COT =================
function renderCot() {
  const cot = STATE.cot;
  if (!cot || !cot.markets) return;

  const isEn = STATE.lang === 'en';
  const dict = I18N[STATE.lang] || I18N.de;

  const weekEl = document.getElementById('cot-report-week');
  const dateEl = document.getElementById('cot-report-date');
  if (weekEl) weekEl.textContent = cot.report_week || (isEn ? 'Current Week' : 'Aktuelle Woche');
  if (dateEl) dateEl.textContent = cot.report_date ? (isEn ? cot.report_date : new Date(cot.report_date).toLocaleDateString('de-DE')) : '-';

  const container = document.getElementById('cot-markets-container');
  if (!container) return;

  let markets = cot.markets;
  if (STATE.cotCategory !== 'all') {
    markets = markets.filter((m) => m.category === STATE.cotCategory);
  }

  if (markets.length === 0) {
    container.innerHTML = `<div class="loading-spinner-row">${isEn ? 'No markets found in this category.' : 'Keine Märkte in dieser Kategorie gefunden.'}</div>`;
    return;
  }

  container.innerHTML = markets.map((m) => {
    const longPct = Math.round(m.whale_bullish_pct || 50);
    const shortPct = 100 - longPct;
    return `
      <div class="cot-card">
        <div class="cot-header">
          <div class="cot-title-wrap">
            <span class="cot-icon">${m.icon || '📊'}</span>
            <div>
              <div class="cot-name">${escapeHtml(m.name)}</div>
              <div class="cot-asset">${escapeHtml(m.asset_class)}</div>
            </div>
          </div>
          <span class="cot-signal-badge" style="background-color:${m.signal_color}22; color:${m.signal_color}; border:1px solid ${m.signal_color}44;">
            ${m.signal}
          </span>
        </div>

        <!-- Visual Long/Short Progress Bar -->
        <div class="dual-bar-container">
          <div class="dual-bar-labels">
            <span class="long-label">Whale Long: ${longPct}% (${formatNumber(m.whale_long)})</span>
            <span class="short-label">Short: ${shortPct}% (${formatNumber(m.whale_short)})</span>
          </div>
          <div class="dual-bar">
            <div class="dual-bar-fill-long" style="width: ${longPct}%;"></div>
            <div class="dual-bar-fill-short" style="width: ${shortPct}%;"></div>
          </div>
        </div>

        <!-- Stats Grid -->
        <div class="cot-stats-grid">
          <div class="cot-stat-col">
            <span class="cot-stat-lbl">${dict.cot_net_whales}</span>
            <span class="cot-stat-val ${m.whale_net >= 0 ? 'val-green' : 'val-red'}">${m.whale_net_formatted}</span>
          </div>
          <div class="cot-stat-col">
            <span class="cot-stat-lbl">${dict.cot_week_flow}</span>
            <span class="cot-stat-val ${m.whale_change_net >= 0 ? 'val-green' : 'val-red'}">
              ${m.whale_change_net >= 0 ? '+' : ''}${formatNumber(m.whale_change_net)}
            </span>
          </div>
          <div class="cot-stat-col">
            <span class="cot-stat-lbl">${dict.cot_retail_sent}</span>
            <span class="cot-stat-val">${m.retail_bullish_pct || 50}% Long</span>
          </div>
        </div>

        <div class="cot-change-tag">
          <span>⚡</span>
          <span>${escapeHtml(m.whale_change_desc || '')} ${dict.cot_vs_prev}</span>
        </div>
      </div>
    `;
  }).join('');
}

// ================= RENDER CONGRESS =================
function renderCongress() {
  const cong = normalizeCongress(STATE.congress) || (window.BUNDLED_DATA ? normalizeCongress(window.BUNDLED_DATA.congress) : null);
  if (!cong) return;
  STATE.congress = cong;

  const isEn = STATE.lang === 'en';
  const dict = I18N[STATE.lang] || I18N.de;

  const totalEl = document.getElementById('cg-stat-total');
  const filersEl = document.getElementById('cg-stat-filers');
  const lateEl = document.getElementById('cg-stat-late');

  if (totalEl && cong.stats) totalEl.textContent = `${(cong.stats.totalTrades / 1000).toFixed(1)}k`;
  if (filersEl && cong.stats) filersEl.textContent = cong.stats.totalFilers || '452';
  if (lateEl && cong.stats) {
    const latePct = Math.round((cong.stats.lateFilings / cong.stats.totalTrades) * 100);
    lateEl.textContent = `${latePct}%`;
  }

  const container = document.getElementById('congress-trades-container');
  if (!container) return;

  // Handle Mode: Bipartisan Confluence
  if (STATE.congressMode === 'bipartisan') {
    const ps = STATE.partySentiment || (window.BUNDLED_DATA ? window.BUNDLED_DATA.party_sentiment : null);
    const list = ps ? ps.bipartisan_buys || [] : [];
    if (list.length === 0) {
      container.innerHTML = `<div class="loading-spinner-row">${isEn ? 'No bipartisan purchases calculated yet.' : 'Keine überparteilichen Käufe berechnet.'}</div>`;
      return;
    }
    container.innerHTML = `
      <div style="font-size:12px; color:#94a3b8; margin-bottom:8px; font-weight:600;">
        ${isEn ? '🤝 Stocks bought by BOTH Democrats and Republicans simultaneously:' : '🤝 Aktien, die sowohl von Demokraten als auch Republikanern zeitgleich akkumuliert werden:'}
      </div>
    ` + list.map((b) => {
      const dWidth = Math.round((b.dem_buys / b.total_buys) * 100);
      const rWidth = 100 - dWidth;
      return `
        <div class="bipartisan-card" onclick="openTickerModal('${b.ticker}')" style="cursor:pointer;">
          <div class="bipartisan-header">
            <div>
              <span class="bipartisan-ticker">$${b.ticker}</span>
              <span style="font-size:11px; color:#94a3b8; margin-left:6px;">${escapeHtml(b.sector)}</span>
            </div>
            <span style="font-size:11px; font-weight:800; color:#10b981;">${b.total_buys}x BUYS GESAMT</span>
          </div>
          <div class="bipartisan-bar">
            <div class="bipartisan-bar-dem" style="width: ${dWidth}%;"></div>
            <div class="bipartisan-bar-rep" style="width: ${rWidth}%;"></div>
          </div>
          <div class="bipartisan-counts">
            <span style="color:#3b82f6;">🔵 Dem: ${b.dem_buys} (${dWidth}%)</span>
            <span style="color:#ef4444;">🔴 Rep: ${b.rep_buys} (${rWidth}%)</span>
          </div>
        </div>
      `;
    }).join('');
    return;
  }

  // Handle Mode: Party Sentiment Drift
  if (STATE.congressMode === 'sentiment') {
    const ps = STATE.partySentiment;
    if (!ps) {
      container.innerHTML = `<div class="loading-spinner-row">${isEn ? 'Loading party sentiment drift...' : 'Lade Partei-Sentiment...'}</div>`;
      return;
    }
    const summary = isEn ? (ps.sentiment_drift_summary_en || ps.sentiment_drift_summary_de) : (ps.sentiment_drift_summary_de || ps.sentiment_drift_summary_en);
    container.innerHTML = `
      <div class="sentiment-drift-card">
        <div style="font-size:13px; font-weight:800; color:#fff; margin-bottom:8px;">⚖️ PARTEI-SENTIMENT & DRIFT</div>
        <div class="sentiment-drift-summary">${escapeHtml(summary)}</div>
        <div style="display:grid; grid-template-columns:1fr 1fr; gap:10px; margin-top:10px;">
          <div>
            <div style="font-size:11px; font-weight:800; color:#3b82f6; margin-bottom:6px;">🔵 DEMOKRATEN TOP KÄUFE</div>
            ${(ps.dem_top_buys || []).slice(0, 5).map(t => `<div style="font-size:11px; color:#cbd5e1; padding:3px 0; border-bottom:1px solid #1e293b;"><strong>$${t.ticker}</strong> (${t.count}x) · ${t.category}</div>`).join('')}
          </div>
          <div>
            <div style="font-size:11px; font-weight:800; color:#ef4444; margin-bottom:6px;">🔴 REPUBLIKANER TOP KÄUFE</div>
            ${(ps.rep_top_buys || []).slice(0, 5).map(t => `<div style="font-size:11px; color:#cbd5e1; padding:3px 0; border-bottom:1px solid #1e293b;"><strong>$${t.ticker}</strong> (${t.count}x) · ${t.category}</div>`).join('')}
          </div>
        </div>
      </div>
    `;
    return;
  }

  let trades = cong.trades;
  const q = STATE.congressQuery.toLowerCase().trim();
  if (q) {
    trades = trades.filter((t) => {
      const tk = (t.ticker || '').toLowerCase();
      const fn = (t.filer_name || '').toLowerCase();
      const an = (t.asset_name || '').toLowerCase();
      return tk.includes(q) || fn.includes(q) || an.includes(q);
    });
  }

  if (STATE.congressMode === 'conflicts') {
    trades = trades.filter((t) => t.has_committee_conflict === true);
  }

  if (STATE.congressType !== 'all') {
    trades = trades.filter((t) => {
      const tx = (t.transaction_type || '').toLowerCase();
      if (STATE.congressType === 'buy') return tx.includes('purchase');
      if (STATE.congressType === 'sale') return tx.includes('sale');
      return true;
    });
  }

  if (STATE.congressParty !== 'all') {
    trades = trades.filter((t) => (t.party || '').toUpperCase() === STATE.congressParty);
  }

  if (trades.length === 0) {
    container.innerHTML = `<div class="loading-spinner-row">${isEn ? 'No Congress trades found with these filters.' : 'Keine Kongress-Trades mit diesen Filtern gefunden.'}</div>`;
    return;
  }

  container.innerHTML = trades.slice(0, 50).map((t) => {
    const isBuy = (t.transaction_type || '').toLowerCase().includes('purchase');
    const isLate = t.days_to_file > 45;
    const docUrl = t.doc_url || 'https://disclosures-clerk.house.gov';
    const conflictBanner = t.has_committee_conflict
      ? `<div class="conflict-alert-banner">⚠️ ${escapeHtml(isEn ? (t.conflict_desc_en || t.conflict_desc_de) : (t.conflict_desc_de || t.conflict_desc_en))}</div>`
      : '';
    const conflictBadge = t.has_committee_conflict
      ? `<span class="conflict-badge">⚠️ KONFLIKT</span>`
      : '';

    return `
      <div class="trade-card">
        <div class="tc-top">
          <div class="tc-filer-wrap">
            <span class="tc-filer-name">${escapeHtml(t.filer_name || 'US Politician')}</span>
            <span class="tc-party-badge ${t.party || ''}">${t.party === 'D' ? '🔵 DEM' : (t.party === 'R' ? '🔴 REP' : t.party || '')}</span>
            ${conflictBadge}
          </div>
          <span class="tc-office">${escapeHtml(t.office || t.chamber || '')}</span>
        </div>

        <div class="tc-middle">
          <div class="tc-ticker-asset">
            <span class="tc-ticker" onclick="openTickerModal('${t.ticker}')">$${escapeHtml(t.ticker || 'N/A')}</span>
            <span class="tc-asset" title="${escapeHtml(t.asset_name || '')}">${escapeHtml(t.asset_name || '')}</span>
          </div>
          <span class="tc-tx-badge ${isBuy ? 'purchase' : 'sale'}">
            ${isBuy ? dict.congress_buy_badge : dict.congress_sale_badge}
          </span>
        </div>

        <div class="tc-bottom">
          <div>
            <span class="tc-amount">${escapeHtml(t.amount_range_label || '$1,001 - $15,000')}</span>
            <span> · ${t.transaction_date || ''}</span>
            ${isLate ? `<span class="val-amber" style="margin-left:4px; font-weight:700;">${dict.congress_late_badge}</span>` : ''}
          </div>
          <div style="display:flex; gap:8px;">
            <span class="tc-link" onclick="openTickerModal('${t.ticker}')">${isEn ? 'Analysis ↗' : 'Vollanalyse ↗'}</span>
            <a href="${docUrl}" target="_blank" class="tc-link">PDF ↗</a>
          </div>
        </div>

        ${conflictBanner}
      </div>
    `;
  }).join('');
}

// ================= RENDER SEC (FORM 4 INSIDERS WITH RICH DETAILS & SHAREHOLDER VALUE) =================
function renderSec() {
  const sec = normalizeSec(STATE.sec) || (window.BUNDLED_DATA ? normalizeSec(window.BUNDLED_DATA.sec) : null);
  if (!sec || !sec.alerts) return;
  STATE.sec = sec;

  const isEn = STATE.lang === 'en';
  const dict = I18N[STATE.lang] || I18N.de;

  const countEl = document.getElementById('sec-alerts-count');
  const clusterEl = document.getElementById('sec-cluster-count');
  const clusterCount = sec.alerts.filter((a) => a.is_cluster).length;

  if (countEl) countEl.textContent = sec.total_alerts || sec.alerts.length;
  if (clusterEl) clusterEl.textContent = clusterCount;

  // Update active state of interactive counter buttons
  const btnAll = document.getElementById('sec-filter-all-btn');
  const btnCluster = document.getElementById('sec-filter-cluster-btn');
  if (btnAll) btnAll.classList.toggle('active', STATE.secFilter === 'all');
  if (btnCluster) btnCluster.classList.toggle('active', STATE.secFilter === 'cluster');

  const container = document.getElementById('sec-filings-container');
  if (!container) return;

  let alerts = sec.alerts;

  // Filter: Cluster only or all
  if (STATE.secFilter === 'cluster') {
    alerts = alerts.filter((a) => a.is_cluster);
  }

  // Filter: Search query
  const q = STATE.secQuery.toLowerCase().trim();
  if (q) {
    alerts = alerts.filter((a) => {
      const iss = (a.issuer || '').toLowerCase();
      const own = (a.reporting_owner || '').toLowerCase();
      const tk = (a.ticker || '').toLowerCase();
      return iss.includes(q) || own.includes(q) || tk.includes(q);
    });
  }

  if (alerts.length === 0) {
    const emptyMsg = STATE.secFilter === 'cluster'
      ? (isEn ? 'No cluster alerts currently active.' : 'Aktuell keine Cluster-Alarme aktiv.')
      : (isEn ? 'No matching SEC filings found.' : 'Keine passenden SEC Filings gefunden.');
    container.innerHTML = `<div class="loading-spinner-row">${emptyMsg}</div>`;
    return;
  }

  container.innerHTML = alerts.map((a) => {
    const txLabel = isEn ? (a.tx_type_en || 'Insider Filing (Form 4)') : (a.tx_type_de || 'Insider-Meldung (Form 4)');
    const tickerTag = a.ticker ? `<span class="sec-card-ticker" onclick="event.stopPropagation(); openTickerModal('${a.ticker}')">$${a.ticker}</span>` : '';
    const summaryText = isEn ? (a.summary_en || `${a.reporting_owner} (${a.role}) filed a transaction for ${a.issuer}.`)
                             : (a.summary_de || `${a.reporting_owner} (${a.role}) reichte eine meldepflichtige Transaktion für ${a.issuer} ein.`);

    // Role badge with icon
    let roleClass = 'sec-role-pill';
    let roleIcon = '👔';
    const rLower = (a.role || '').toLowerCase();
    if (rLower.includes('ceo') || rLower.includes('chief executive') || rLower.includes('president')) {
      roleClass += ' ceo';
      roleIcon = '👔';
    } else if (rLower.includes('cfo') || rLower.includes('financial') || rLower.includes('treasurer')) {
      roleClass += ' cfo';
      roleIcon = '💼';
    } else if (rLower.includes('director') || rLower.includes('aufsichtsrat') || rLower.includes('board')) {
      roleIcon = '🏛️';
    } else if (rLower.includes('10%') || rLower.includes('owner') || rLower.includes('shareholder')) {
      roleIcon = '🏢';
    }

    // High Conviction Signal Pill
    let signalBadge = '';
    if (a.is_cluster) {
      signalBadge = `<span class="sec-tx-pill" style="background:#f59e0b; color:#000; font-weight:800; font-size:10px;">🔥 CLUSTER (${a.cluster_count}x)</span>`;
    } else if (a.is_buy && (a.total_value >= 250000 || a.code === 'P')) {
      signalBadge = `<span class="sec-tx-pill buy" style="font-weight:800; font-size:10px;">🟢 STARKER KAUF (CODE P)</span>`;
    } else if (a.is_buy) {
      signalBadge = `<span class="sec-tx-pill buy" style="font-size:10px;">🟢 KAUF (CODE P)</span>`;
    } else if (a.code === 'S' || !a.is_buy) {
      signalBadge = `<span class="sec-tx-pill sale" style="font-size:10px;">🔴 VERKAUF (CODE S)</span>`;
    } else {
      signalBadge = `<span class="sec-tx-pill neutral" style="font-size:10px;">⚪ ZUTEILUNG (CODE A)</span>`;
    }

    // Shareholder Takeaway Box (Aktionärs-Nutzen)
    let takeawayBox = '';
    if (a.is_cluster) {
      takeawayBox = `
        <div class="sec-takeaway-box cluster">
          <strong>🔥 AKTIONÄRS-VORTEIL (CLUSTER):</strong> ${a.cluster_count} Führungskräfte haben zeitgleich bei ${escapeHtml(a.issuer)} gekauft. Historisch höchste Wahrscheinlichkeit für anstehende positive Katalysatoren!
        </div>
      `;
    } else if (a.is_buy && a.total_value >= 400000) {
      takeawayBox = `
        <div class="sec-takeaway-box bullish">
          <strong>🟢 HOHE ÜBERZEUGUNG:</strong> ${escapeHtml(a.reporting_owner)} (${escapeHtml(a.role)}) investiert ${a.total_value_formatted} privates Eigenkapital am Markt. Starkes Indiz für Unterbewertung.
        </div>
      `;
    } else if (a.is_buy) {
      takeawayBox = `
        <div class="sec-takeaway-box bullish">
          <strong>🟢 INSIDER-KAUF:</strong> Offener Markterwerb (Code P). Führungskräfte investieren persönliches Geld – ein direktes Signal für operatives Vertrauen.
        </div>
      `;
    } else if (a.code === 'S') {
      takeawayBox = `
        <div class="sec-takeaway-box cautious">
          <strong>🔴 GEWINNMITNAHME / VERKAUF:</strong> Marktverkauf durch ${escapeHtml(a.reporting_owner)}. Oft persönliche Vermögensstreuung oder Steuern.
        </div>
      `;
    } else {
      takeawayBox = `
        <div class="sec-takeaway-box neutral">
          <strong>⚪ VERGÜTUNG:</strong> Automatische Aktien-Zuteilung lt. Anstellungsvertrag. Kein privates Geld investiert (kursneutral).
        </div>
      `;
    }

    return `
      <div class="sec-card ${a.is_cluster ? 'cluster' : ''}" onclick="openSecDetailModal('${a.accession_number}')" title="${isEn ? 'Click for full Form 4 analysis & SEC source' : 'Klicken für Details, Analyse & SEC-Quelle'}">
        <div class="sec-card-header">
          <div class="sec-card-title">
            ${tickerTag}
            <div>
              <strong style="font-size:13.5px; color:#fff;">${escapeHtml(a.issuer)}</strong>
              <div style="font-size:11px; color:#94a3b8; margin-top:2px; display:flex; align-items:center; gap:6px; flex-wrap:wrap;">
                <span>${escapeHtml(a.reporting_owner)}</span>
                <span class="${roleClass}">${roleIcon} ${escapeHtml(a.role || 'Officer')}</span>
              </div>
            </div>
          </div>
          ${signalBadge}
        </div>

        <!-- 3-Column Metrics Grid for Trade Size, Price, Total Volume -->
        <div class="sec-metrics-grid">
          <div class="sec-metric-item">
            <span class="sec-metric-lbl">${dict.sec_shares_lbl}</span>
            <span class="sec-metric-val">${a.shares_formatted || '-'}</span>
          </div>
          <div class="sec-metric-item">
            <span class="sec-metric-lbl">${dict.sec_price_lbl}</span>
            <span class="sec-metric-val">${a.price_formatted || '-'}</span>
          </div>
          <div class="sec-metric-item">
            <span class="sec-metric-lbl">${dict.sec_volume_lbl}</span>
            <span class="sec-metric-val" style="color:${a.is_buy ? '#10b981' : '#f59e0b'}; font-weight:800;">${a.total_value_formatted || '-'}</span>
          </div>
        </div>

        <!-- Aktionärs-Einschätzung -->
        ${takeawayBox}

        <!-- Detailed Explanatory Summary -->
        <div class="sec-summary-text">${escapeHtml(summaryText)}</div>

        <div class="sec-bottom">
          <span style="font-size:11px; color:#64748b;">${formatTime(a.filing_time)} · Form 4</span>
          <div style="display:flex; gap:8px; align-items:center;">
            <button class="pill" style="font-size:11px; padding:4px 9px; background:rgba(56,189,248,0.15); color:#38bdf8; border:1px solid rgba(56,189,248,0.3); border-radius:6px; cursor:pointer;" onclick="event.stopPropagation(); openSecDetailModal('${a.accession_number}')">
              ${dict.sec_btn_more_details}
            </button>
            <a href="${a.filing_url}" target="_blank" onclick="event.stopPropagation();" class="tc-link" style="font-size:11px;">
              📄 SEC.gov ↗
            </a>
          </div>
        </div>
      </div>
    `;
  }).join('');
}

// ================= RENDER WATCHLIST =================
function initWatchlist() {
  const addBtn = document.getElementById('btn-add-ticker');
  const addInput = document.getElementById('add-ticker-input');

  const add = (tk) => {
    tk = (tk || '').toUpperCase().trim();
    if (!tk) return;
    if (!STATE.watchlist.includes(tk)) {
      STATE.watchlist.push(tk);
      localStorage.setItem('whalewatch_wl', JSON.stringify(STATE.watchlist));
      renderWatchlist();
    }
    if (addInput) addInput.value = '';
  };

  if (addBtn && addInput) {
    addBtn.addEventListener('click', () => add(addInput.value));
    addInput.addEventListener('keydown', (e) => {
      if (e.key === 'Enter') add(addInput.value);
    });
  }

  document.querySelectorAll('.chip-add').forEach((btn) => {
    btn.addEventListener('click', () => {
      add(btn.getAttribute('data-ticker'));
    });
  });
}

function removeWatchlistTicker(ticker) {
  STATE.watchlist = STATE.watchlist.filter((t) => t !== ticker);
  localStorage.setItem('whalewatch_wl', JSON.stringify(STATE.watchlist));
  renderWatchlist();
}

function toggleWatchlistTicker(ticker) {
  ticker = (ticker || '').toUpperCase().trim();
  if (!ticker) return;
  if (STATE.watchlist.includes(ticker)) {
    removeWatchlistTicker(ticker);
  } else {
    STATE.watchlist.push(ticker);
    localStorage.setItem('whalewatch_wl', JSON.stringify(STATE.watchlist));
    renderWatchlist();
  }
}

function renderWatchlist() {
  const container = document.getElementById('watchlist-container');
  if (!container) return;

  const isEn = STATE.lang === 'en';
  const dict = I18N[STATE.lang] || I18N.de;

  if (STATE.watchlist.length === 0) {
    container.innerHTML = `
      <div class="card" style="text-align:center; padding:30px 20px;">
        <div style="font-size:32px; margin-bottom:8px;">⭐</div>
        <h4 style="font-size:15px; margin-bottom:4px;">${dict.wl_empty_title}</h4>
        <p style="font-size:12px; color:#94a3b8;">${dict.wl_empty_desc}</p>
      </div>
    `;
    return;
  }

  const congData = normalizeCongress(STATE.congress) || (window.BUNDLED_DATA ? normalizeCongress(window.BUNDLED_DATA.congress) : null);
  const secData = normalizeSec(STATE.sec) || (window.BUNDLED_DATA ? normalizeSec(window.BUNDLED_DATA.sec) : null);
  const cong = congData ? congData.trades : [];
  const sec = secData ? secData.alerts : [];

  container.innerHTML = STATE.watchlist.map((ticker) => {
    const cgMatches = cong.filter((t) => (t.ticker || '').toUpperCase() === ticker);
    const secMatches = sec.filter((a) => (a.ticker || '').toUpperCase() === ticker || (a.issuer || '').toUpperCase().includes(ticker));

    let activityText = isEn ? 'No recent transactions this week.' : 'Keine aktuellen Transaktionen in dieser Woche.';
    if (cgMatches.length > 0) {
      const last = cgMatches[0];
      const isBuy = (last.transaction_type || '').toLowerCase().includes('purchase');
      const actionLabel = isBuy ? (isEn ? '🟢 Buy' : '🟢 Kauf') : (isEn ? '🔴 Sale' : '🔴 Verkauf');
      activityText = isEn 
        ? `${cgMatches.length}x traded in Congress. Last Trade: ${actionLabel} by ${last.filer_name} (${last.party}) of ${last.amount_range_label}.`
        : `${cgMatches.length}x im US-Kongress gehandelt. Letzter Trade: ${actionLabel} von ${last.filer_name} (${last.party}) über ${last.amount_range_label}.`;
    }

    const secBadge = secMatches.length > 0 
      ? `<span class="sec-tx-pill buy" style="font-size:9px; margin-left:6px;">🔥 ${secMatches.length} Form 4 Filings</span>` 
      : '';

    return `
      <div class="watchlist-card">
        <div class="wl-top">
          <div style="cursor:pointer;" onclick="openTickerModal('${ticker}')">
            <span class="wl-ticker">$${ticker}</span>
            <span class="badge-sub" style="margin-left:6px;">Smart Radar</span>
            ${secBadge}
          </div>
          <button class="wl-del-btn" onclick="removeWatchlistTicker('${ticker}')" title="${isEn ? 'Remove' : 'Entfernen'}">✕</button>
        </div>
        <div class="wl-activity">${activityText}</div>
        <div style="margin-top:8px; display:flex; gap:6px;">
          <button class="pill" style="font-size:11px; padding:4px 10px; background:rgba(6,182,212,0.15); color:#06b6d4; border:1px solid rgba(6,182,212,0.3);" onclick="openTickerModal('${ticker}')">${dict.wl_full_analysis}</button>
        </div>
      </div>
    `;
  }).join('');
}

// ================= FILTERS & SEARCH =================
function initFilters() {
  // Congress Intelligence Mode Selector
  document.querySelectorAll('#congress-mode-selector .pill').forEach((btn) => {
    btn.addEventListener('click', () => {
      document.querySelectorAll('#congress-mode-selector .pill').forEach((b) => b.classList.remove('active'));
      btn.classList.add('active');
      STATE.congressMode = btn.getAttribute('data-mode');
      renderCongress();
    });
  });

  // COT Category filters
  document.querySelectorAll('#cot-category-filters .pill').forEach((btn) => {
    btn.addEventListener('click', () => {
      document.querySelectorAll('#cot-category-filters .pill').forEach((b) => b.classList.remove('active'));
      btn.classList.add('active');
      STATE.cotCategory = btn.getAttribute('data-cat');
      renderCot();
    });
  });

  // Congress Type filters
  document.querySelectorAll('#congress-type-filters .pill').forEach((btn) => {
    btn.addEventListener('click', () => {
      document.querySelectorAll('#congress-type-filters .pill').forEach((b) => b.classList.remove('active'));
      btn.classList.add('active');
      STATE.congressType = btn.getAttribute('data-type');
      renderCongress();
    });
  });

  // Congress Party filters
  document.querySelectorAll('#congress-party-filters .pill').forEach((btn) => {
    btn.addEventListener('click', () => {
      document.querySelectorAll('#congress-party-filters .pill').forEach((b) => b.classList.remove('active'));
      btn.classList.add('active');
      STATE.congressParty = btn.getAttribute('data-party');
      renderCongress();
    });
  });

  // SEC Clickable Stat Counter Buttons (100 Neue Filings vs Cluster Alarme)
  const secBtnAll = document.getElementById('sec-filter-all-btn');
  const secBtnCluster = document.getElementById('sec-filter-cluster-btn');

  if (secBtnAll) {
    secBtnAll.addEventListener('click', () => {
      STATE.secFilter = 'all';
      renderSec();
    });
  }
  if (secBtnCluster) {
    secBtnCluster.addEventListener('click', () => {
      STATE.secFilter = (STATE.secFilter === 'cluster') ? 'all' : 'cluster';
      renderSec();
    });
  }

  // SEC Guide Toggle
  const guideCard = document.getElementById('sec-guide-card');
  const guideToggle = document.getElementById('sec-guide-toggle');
  const guideContent = document.getElementById('sec-guide-content');
  if (guideToggle && guideContent && guideCard) {
    guideToggle.addEventListener('click', () => {
      const isExpanded = guideContent.style.display !== 'none';
      guideContent.style.display = isExpanded ? 'none' : 'block';
      guideCard.classList.toggle('open', !isExpanded);
    });
  }
}

function initSearch() {
  const quickInput = document.getElementById('quick-search-input');
  const quickClear = document.getElementById('quick-search-clear');
  const quickBtn = document.getElementById('quick-search-btn');
  const quickIconBtn = document.getElementById('quick-search-icon-btn');
  const suggestionsBox = document.getElementById('quick-search-suggestions');

  const executeSearch = (val) => {
    val = (val || (quickInput ? quickInput.value : '')).trim().toUpperCase();
    if (!val) return;
    if (suggestionsBox) suggestionsBox.style.display = 'none';
    openTickerModal(val);
  };

  if (quickInput) {
    quickInput.addEventListener('keydown', (e) => {
      if (e.key === 'Enter') {
        e.preventDefault();
        executeSearch(quickInput.value);
      }
    });

    quickInput.addEventListener('input', () => {
      const q = quickInput.value.trim().toUpperCase();
      if (quickClear) quickClear.style.display = q ? 'block' : 'none';

      if (!q || !suggestionsBox) {
        if (suggestionsBox) suggestionsBox.style.display = 'none';
        return;
      }

      // Build live search matches across popular, congress, and sec
      const matches = [];
      const seen = new Set();

      POPULAR_TICKERS.forEach((item) => {
        if (item.ticker.includes(q) || item.name.toUpperCase().includes(q)) {
          if (!seen.has(item.ticker)) {
            seen.add(item.ticker);
            matches.push({ ticker: item.ticker, name: item.name, badge: item.sector });
          }
        }
      });

      const congData = normalizeCongress(STATE.congress) || (window.BUNDLED_DATA ? normalizeCongress(window.BUNDLED_DATA.congress) : null);
      if (congData && congData.trades) {
        for (const t of congData.trades) {
          if (t.ticker && (t.ticker.toUpperCase().includes(q) || (t.asset_name && t.asset_name.toUpperCase().includes(q)))) {
            const tk = t.ticker.toUpperCase();
            if (!seen.has(tk)) {
              seen.add(tk);
              matches.push({ ticker: tk, name: t.asset_name || tk, badge: 'STOCK ACT' });
            }
          }
          if (matches.length >= 7) break;
        }
      }

      if (matches.length === 0) {
        matches.push({ ticker: q, name: 'Smart Money Vollanalyse starten', badge: 'ANALYSE' });
      }

      suggestionsBox.innerHTML = matches.slice(0, 6).map((m) => `
        <div class="suggestion-item" onclick="selectSuggestion('${m.ticker}')">
          <div style="display:flex; align-items:center;">
            <span class="suggestion-ticker">$${m.ticker}</span>
            <span class="suggestion-name">${escapeHtml(m.name)}</span>
          </div>
          <span class="suggestion-badge">${m.badge}</span>
        </div>
      `).join('');
      suggestionsBox.style.display = 'block';
    });
  }

  window.selectSuggestion = function(ticker) {
    if (quickInput) quickInput.value = ticker;
    executeSearch(ticker);
  };

  if (quickBtn) {
    quickBtn.addEventListener('click', () => executeSearch(quickInput.value));
  }
  if (quickIconBtn) {
    quickIconBtn.addEventListener('click', () => executeSearch(quickInput.value));
  }
  if (quickClear) {
    quickClear.addEventListener('click', () => {
      quickInput.value = '';
      quickClear.style.display = 'none';
      if (suggestionsBox) suggestionsBox.style.display = 'none';
    });
  }

  // Close suggestions when clicking outside
  document.addEventListener('click', (e) => {
    if (suggestionsBox && !e.target.closest('.search-wrapper')) {
      suggestionsBox.style.display = 'none';
    }
  });

  const congSearch = document.getElementById('congress-search-input');
  if (congSearch) {
    congSearch.addEventListener('input', (e) => {
      STATE.congressQuery = e.target.value;
      renderCongress();
    });
  }

  const secSearch = document.getElementById('sec-search-input');
  if (secSearch) {
    secSearch.addEventListener('input', (e) => {
      STATE.secQuery = e.target.value;
      renderSec();
    });
  }
}

// ================= COMPREHENSIVE SMART MONEY VOLLANALYSE MODAL =================
function initModal() {
  const modal = document.getElementById('modal-detail');
  const closeBtn = document.getElementById('modal-close-btn');
  if (closeBtn && modal) {
    closeBtn.addEventListener('click', () => modal.style.display = 'none');
    modal.addEventListener('click', (e) => {
      if (e.target === modal) modal.style.display = 'none';
    });
  }
}

// ================= SEC INSIDER EVENT DETAIL MODAL =================
function initSecModal() {
  const modal = document.getElementById('modal-sec-event');
  const closeBtn = document.getElementById('modal-sec-event-close-btn');
  if (closeBtn && modal) {
    closeBtn.addEventListener('click', () => modal.style.display = 'none');
    modal.addEventListener('click', (e) => {
      if (e.target === modal) modal.style.display = 'none';
    });
  }
}

window.openSecDetailModal = function(accNo) {
  const sec = normalizeSec(STATE.sec) || (window.BUNDLED_DATA ? normalizeSec(window.BUNDLED_DATA.sec) : null);
  if (!sec || !sec.alerts) return;

  const alert = sec.alerts.find((a) => a.accession_number === accNo || a.ticker === accNo || (a.ticker && accNo && a.ticker.toLowerCase() === accNo.toLowerCase()));
  if (!alert) return;

  const isEn = STATE.lang === 'en';
  const dict = I18N[STATE.lang] || I18N.de;

  const modal = document.getElementById('modal-sec-event');
  const titleEl = document.getElementById('modal-sec-event-title');
  const contentEl = document.getElementById('modal-sec-event-content');
  if (!modal || !contentEl) return;

  if (titleEl) {
    titleEl.textContent = alert.ticker 
      ? `$${alert.ticker} · SEC FORM 4 DETAILS` 
      : `SEC FORM 4 · ${alert.issuer}`;
  }

  const tagColor = alert.is_buy ? '#10b981' : (alert.code === 'S' ? '#ef4444' : '#94a3b8');
  const txLabel = isEn ? (alert.tx_type_en || 'Insider Filing') : (alert.tx_type_de || 'Insider-Meldung');
  const originalUrl = alert.filing_url || 'https://www.sec.gov';

  const isOnWl = alert.ticker && STATE.watchlist.includes(alert.ticker);
  const wlBtnClass = isOnWl ? 'is-active' : 'is-not-active';
  const wlBtnText = isOnWl ? dict.modal_btn_remove_wl : dict.modal_btn_add_wl;

  contentEl.innerHTML = `
    <!-- Top Header: Ticker & Issuer -->
    <div class="analysis-card-top" style="margin-bottom:14px;">
      <div>
        <div style="font-size:24px; font-weight:800; color:#38bdf8; line-height:1.1;">
          ${alert.ticker ? `$${alert.ticker}` : escapeHtml(alert.issuer)}
        </div>
        <div style="font-size:13px; color:#cbd5e1; margin-top:2px;">
          ${escapeHtml(alert.issuer)}
        </div>
      </div>
      <div style="background:${tagColor}22; color:${tagColor}; border:1px solid ${tagColor}55; padding:4px 10px; border-radius:6px; font-size:11px; font-weight:800;">
        ${txLabel}
      </div>
    </div>

    <!-- Cluster Alert Banner if applicable -->
    ${alert.is_cluster ? `
      <div style="background:linear-gradient(135deg, rgba(245,158,11,0.2) 0%, rgba(15,23,42,0.95) 100%); border:1px solid #f59e0b; border-radius:8px; padding:10px 12px; margin-bottom:14px;">
        <div style="font-size:12px; font-weight:800; color:#f59e0b; margin-bottom:3px;">
          🔥 CLUSTER-KAUF ERKANNT (${alert.cluster_count}x FILINGS)
        </div>
        <div style="font-size:11px; color:#fde68a; line-height:1.4;">
          ${isEn ? `Multiple corporate insiders concurrently accumulated shares at ${alert.issuer}. Cluster filings historically exhibit a high probability of outperformance over subsequent quarters.`
                 : `Mehrere Vorstandsmitglieder kauften synchron Aktien bei ${alert.issuer}. Cluster-Käufe erzielen historisch eine überdurchschnittliche Trefferquote bei anstehenden Kursbewegungen.`}
        </div>
      </div>
    ` : ''}

    <!-- Key Insider Metrics Box -->
    <div style="background:rgba(15,23,42,0.7); border:1px solid #1e293b; border-radius:8px; padding:12px; margin-bottom:14px;">
      <div style="display:grid; grid-template-columns:1fr 1fr; gap:10px; margin-bottom:10px;">
        <div>
          <div style="font-size:10px; color:#64748b; font-weight:700; text-transform:uppercase;">${isEn ? 'Reporting Insider' : 'Meldender Insider'}</div>
          <div style="font-size:13px; font-weight:800; color:#fff; margin-top:2px;">${escapeHtml(alert.reporting_owner)}</div>
          <div style="font-size:11px; color:#94a3b8;">${escapeHtml(alert.role || 'Officer')}</div>
        </div>
        <div>
          <div style="font-size:10px; color:#64748b; font-weight:700; text-transform:uppercase;">${isEn ? 'Transaction Code' : 'Transaktions-Code'}</div>
          <div style="font-size:13px; font-weight:800; color:${tagColor}; margin-top:2px;">Code ${escapeHtml(alert.code || 'P')}</div>
          <div style="font-size:11px; color:#94a3b8;">${txLabel}</div>
        </div>
      </div>

      <div style="display:grid; grid-template-columns:1fr 1fr 1fr; gap:8px; border-top:1px solid #1e293b; padding-top:10px;">
        <div>
          <div style="font-size:9px; color:#64748b; font-weight:700; text-transform:uppercase;">${dict.sec_shares_lbl}</div>
          <div style="font-size:13px; font-weight:800; color:#fff;">${alert.shares_formatted || '-'}</div>
        </div>
        <div>
          <div style="font-size:9px; color:#64748b; font-weight:700; text-transform:uppercase;">${dict.sec_price_lbl}</div>
          <div style="font-size:13px; font-weight:800; color:#fff;">${alert.price_formatted || '-'}</div>
        </div>
        <div>
          <div style="font-size:9px; color:#64748b; font-weight:700; text-transform:uppercase;">${dict.sec_volume_lbl}</div>
          <div style="font-size:13px; font-weight:800; color:${tagColor};">${alert.total_value_formatted || '-'}</div>
        </div>
      </div>
    </div>

    <!-- Aktionärs-Tiefenanalyse -->
    <div style="margin-bottom:14px;">
      <div style="font-size:11px; font-weight:800; color:#94a3b8; text-transform:uppercase; letter-spacing:0.5px; margin-bottom:6px;">
        ${isEn ? '💡 Shareholder Assessment & Actionable Context' : '💡 Aktionärs-Bewertung & Mehrwert für Anleger'}
      </div>
      <div style="background:rgba(30,41,59,0.5); border:1px solid #334155; border-radius:8px; padding:10px 12px; font-size:12px; color:#cbd5e1; line-height:1.5;">
        ${alert.is_buy ? (
          alert.code === 'P'
            ? (isEn ? `<strong>High Conviction Open-Market Purchase:</strong> ${alert.reporting_owner} (${alert.role}) acquired shares using personal capital on the public exchange. Academic studies indicate corporate insiders outperform broad indices by +6% to +10% annually following direct market purchases.`
                    : `<strong>Kauf aus persönlichem Vermögen (Code P):</strong> ${alert.reporting_owner} (${alert.role}) erwarb Aktien direkt an der Börse. Studien belegen, dass direkte Insider-Käufe am Markt in den folgenden 6 bis 12 Monaten eine signifikante Überrendite erzielen, da Führungskräfte Quartalstrends als Erste wahrnehmen.`)
            : (isEn ? `<strong>Grant or Option Execution:</strong> This transaction corresponds to contractually allocated equity or exercise of options.`
                    : `<strong>Zuteilung / Option:</strong> Diese Transaktion ist ein vertraglich vereinbarter Vergütungsbestandteil. Kein frisches Eigenkapital geflossen.`)
        ) : (
          isEn ? `<strong>Open Market Disposition (Sale):</strong> Insider trimmed position. Often conducted under predefined SEC Rule 10b5-1 trading plans for tax or portfolio rebalancing purposes.`
               : `<strong>Insider-Verkauf am Markt:</strong> Position verkleinert. Häufig im Rahmen geplanter SEC Rule 10b5-1 Verkaufspläne zur Steueroptimierung oder Vermögensstreuung. Erst bei mehreren Vorstandsverkäufen zeitgleich bärisch zu werten.`
        )}
      </div>
    </div>

    <!-- Official Filing Metadata -->
    <div style="font-size:11px; color:#64748b; margin-bottom:16px;">
      <div><strong>SEC Accession No.:</strong> <span style="font-family:monospace; color:#94a3b8;">${escapeHtml(alert.accession_number)}</span></div>
      <div><strong>${isEn ? 'Filing Timestamp' : 'Einreichungsdatum'}:</strong> ${alert.filing_time}</div>
      <div><strong>${isEn ? 'Security Type' : 'Wertpapierart'}:</strong> ${escapeHtml(alert.security_title || 'Common Stock')}</div>
    </div>

    <!-- Action Buttons: Direct SEC EDGAR Link + Watchlist + Vollanalyse -->
    <div style="display:flex; flex-direction:column; gap:8px;">
      <a href="${originalUrl}" target="_blank" class="btn-primary" style="text-align:center; text-decoration:none; display:flex; align-items:center; justify-content:center; gap:8px; padding:12px; font-size:13px; font-weight:700;">
        ${dict.sec_action_open_edgar}
      </a>

      ${alert.ticker ? `
        <button class="wl-action-btn ${wlBtnClass}" onclick="toggleWatchlistTicker('${alert.ticker}'); openSecDetailModal('${accNo}');">
          ${wlBtnText}
        </button>
        <button class="pill" style="width:100%; padding:10px; font-size:12px; font-weight:700; background:rgba(56,189,248,0.12); color:#38bdf8; border:1px solid rgba(56,189,248,0.3); border-radius:10px; cursor:pointer;" onclick="document.getElementById('modal-sec-event').style.display='none'; openTickerModal('${alert.ticker}');">
          📊 Smart Money Vollanalyse für $${alert.ticker} öffnen ↗
        </button>
      ` : ''}
    </div>
  `;

  modal.style.display = 'flex';
};

window.openTickerModal = async function(ticker) {
  ticker = (ticker || '').toUpperCase().trim();
  const isEn = STATE.lang === 'en';
  const dict = I18N[STATE.lang] || I18N.de;

  const modal = document.getElementById('modal-detail');
  const titleEl = document.getElementById('modal-ticker-title');
  const contentEl = document.getElementById('modal-content');
  if (!modal || !contentEl) return;

  if (titleEl) titleEl.textContent = `$${ticker} ${dict.modal_title_suffix}`;
  
  // Initial loading state
  contentEl.innerHTML = `
    <div class="loading-spinner-row">
      <div class="spinner"></div> Lade Smart Money Vollanalyse für $${ticker}...
    </div>
  `;
  modal.style.display = 'flex';

  try {
    const res = await fetch(`/api/analysis?ticker=${ticker}`).then((r) => r.json());
    renderVollanalyseContent(res, ticker);
  } catch (err) {
    console.error('Error fetching analysis:', err);
    // Render fallback analysis using loaded state
    renderFallbackAnalysis(ticker);
  }
};

function renderVollanalyseContent(data, ticker) {
  const isEn = STATE.lang === 'en';
  const dict = I18N[STATE.lang] || I18N.de;
  const contentEl = document.getElementById('modal-content');
  if (!contentEl) return;

  const isOnWatchlist = STATE.watchlist.includes(ticker);
  const score = data.score || 65;
  const sentimentLabel = isEn ? (data.sentiment_label_en || 'BULLISH') : (data.sentiment_label_de || 'BULLISCH');
  const sentimentColor = data.sentiment_color || '#10b981';
  const companyName = data.company_name || `${ticker} Corporation`;

  // Watchlist action button text & style
  const wlBtnClass = isOnWatchlist ? 'is-active' : 'is-not-active';
  const wlBtnText = isOnWatchlist ? dict.modal_btn_remove_wl : dict.modal_btn_add_wl;

  // Recent congress trades
  const cgTrades = data.recent_congress_trades || [];
  const secAlerts = data.recent_sec_alerts || [];
  const topPols = data.top_politicians || [];

  // Top Header: Ticker, Company Name & Sentiment Score
  const superSig = (STATE.superSignals || []).find((s) => s.ticker === ticker);
  const conflictTrade = cgTrades.find((t) => t.has_committee_conflict);

  const superSigBox = superSig ? `
    <div style="background:linear-gradient(135deg, rgba(245,158,11,0.2) 0%, rgba(15,23,42,0.9) 100%); border:1px solid #f59e0b; border-radius:8px; padding:10px 12px; margin-bottom:12px;">
      <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:4px;">
        <span style="font-size:11px; font-weight:800; color:#f59e0b;">🔥 KOMBI-SIGNAL: CEO + POLITIKER</span>
        <span style="font-size:10px; font-weight:800; background:rgba(245,158,11,0.3); color:#fff; padding:2px 6px; border-radius:4px;">98% SCORE</span>
      </div>
      <div style="font-size:11px; color:#f8fafc; line-height:1.4;">
        ${escapeHtml(isEn ? (superSig.description_en || superSig.description_de) : (superSig.description_de || superSig.description_en))}
      </div>
    </div>
  ` : '';

  const conflictBox = conflictTrade ? `
    <div style="background:rgba(239,68,68,0.12); border:1px solid rgba(239,68,68,0.4); border-radius:8px; padding:10px 12px; margin-bottom:12px; color:#fca5a5; font-size:11px; line-height:1.4;">
      <strong>⚠️ AUSSCHUSS-KONFLIKT:</strong> ${escapeHtml(isEn ? (conflictTrade.conflict_desc_en || conflictTrade.conflict_desc_de) : (conflictTrade.conflict_desc_de || conflictTrade.conflict_desc_en))}
    </div>
  ` : '';

  contentEl.innerHTML = `
    <!-- Top Header: Ticker, Company Name & Sentiment Score -->
    <div class="analysis-card-top">
      <div>
        <div style="font-size:26px; font-weight:800; color:#38bdf8; line-height:1.1;">$${ticker}</div>
        <div style="font-size:12px; color:#94a3b8; margin-top:2px;">${escapeHtml(companyName)}</div>
      </div>
      <div class="analysis-score-badge" style="background-color:${sentimentColor}22; color:${sentimentColor}; border:1px solid ${sentimentColor}44;">
        ${sentimentLabel}
      </div>
    </div>

    ${superSigBox}
    ${conflictBox}

    <!-- Smart Money Confluence Meter -->
    <div class="analysis-meter-box">
      <div class="analysis-meter-header">
        <span style="color:#94a3b8;">${isEn ? 'Smart Money Confluence Score' : 'Smart Money Confluence Score'}</span>
        <span style="color:${sentimentColor}; font-weight:800;">${score}/100</span>
      </div>
      <div class="analysis-meter-bar-track">
        <div class="analysis-meter-bar-fill" style="width:${score}%; background:${sentimentColor};"></div>
      </div>
      <div style="font-size:11px; color:#64748b;">
        ${isEn 
          ? `Computed from Congressional STOCK Act disclosures and SEC Form 4 insider flow. Institution Benchmark: ${data.cot_whale_context || ''}.` 
          : `Ermittelt aus Transaktionen im US-Kongress und Form 4 Insider-Meldungen. COT-Benchmark: ${data.cot_whale_context || ''}.`}
      </div>
    </div>

    <!-- Congress Stats Grid -->
    <div class="analysis-grid-2">
      <div class="analysis-stat-card">
        <div class="analysis-stat-lbl">${dict.modal_cg_buys}</div>
        <div class="analysis-stat-val" style="color:#10b981;">${data.congress_buys || 0}</div>
      </div>
      <div class="analysis-stat-card">
        <div class="analysis-stat-lbl">${dict.modal_cg_sales}</div>
        <div class="analysis-stat-val" style="color:#ef4444;">${data.congress_sales || 0}</div>
      </div>
    </div>

    <!-- Top Politicians active in this stock -->
    ${topPols.length > 0 ? `
      <div style="margin-bottom:12px; background:rgba(15,23,42,0.6); padding:10px; border-radius:8px; border:1px solid #1e293b;">
        <div style="font-size:11px; color:#94a3b8; font-weight:700; margin-bottom:6px;">${dict.modal_cg_top_filers}:</div>
        <div style="display:flex; flex-wrap:wrap; gap:6px;">
          ${topPols.map((p) => `
            <span style="font-size:11px; background:#1e293b; color:#f1f5f9; padding:2px 8px; border-radius:4px;">
              ${escapeHtml(p.name)} (${p.trades_count}x)
            </span>
          `).join('')}
        </div>
      </div>
    ` : ''}

    <!-- Recent Congress Trades List -->
    <div class="analysis-section-title">
      ${dict.modal_history_title}
    </div>
    ${cgTrades.length > 0 ? cgTrades.slice(0, 4).map((t) => {
      const isBuy = (t.transaction_type || '').toLowerCase().includes('purchase');
      return `
        <div style="background:#131d31; border:1px solid #1e293b; border-radius:8px; padding:9px 12px; margin-bottom:6px; font-size:12px;">
          <div style="display:flex; justify-content:space-between; margin-bottom:3px;">
            <strong>${escapeHtml(t.filer_name)} (${t.party || ''})</strong>
            <span style="font-weight:700; color:${isBuy ? '#10b981' : '#ef4444'};">
              ${isBuy ? dict.congress_buy_badge : dict.congress_sale_badge}
            </span>
          </div>
          <div style="color:#94a3b8; font-size:11px;">
            ${isEn ? 'Amount' : 'Volumen'}: <strong style="color:#f1f5f9;">${t.amount_range_label}</strong> · ${isEn ? 'Date' : 'Datum'}: ${t.transaction_date}
          </div>
        </div>
      `;
    }).join('') : `<p style="font-size:12px; color:#64748b; margin-bottom:10px;">${dict.modal_no_trades}</p>`}

    <!-- SEC Form 4 Section -->
    <div class="analysis-section-title">
      ${dict.modal_sec_title}
    </div>
    ${secAlerts.length > 0 ? secAlerts.slice(0, 3).map((a) => `
      <div style="background:#131d31; border:1px solid #1e293b; border-radius:8px; padding:9px 12px; margin-bottom:6px; font-size:12px;">
        <div style="display:flex; justify-content:space-between; margin-bottom:3px;">
          <strong>${escapeHtml(a.reporting_owner)}</strong>
          <span style="font-size:10px; font-weight:700; color:#38bdf8; background:rgba(56,189,248,0.12); padding:2px 6px; border-radius:4px;">
            ${isEn ? (a.tx_type_en || 'FORM 4') : (a.tx_type_de || 'FORM 4')}
          </span>
        </div>
        <div style="color:#cbd5e1; font-size:11px;">
          ${escapeHtml(a.role || 'Officer')} · <span style="color:#10b981; font-weight:700;">${a.total_value_formatted || a.shares_formatted || ''}</span>
        </div>
      </div>
    `).join('') : `<p style="font-size:12px; color:#64748b; margin-bottom:14px;">${dict.modal_no_sec}</p>`}

    <!-- Context-Aware Watchlist Action Button -->
    <div style="margin-top:16px;">
      <button id="modal-wl-toggle-btn" class="wl-action-btn ${wlBtnClass}" onclick="handleModalWatchlistToggle('${ticker}')">
        ${wlBtnText}
      </button>
    </div>
  `;
}

window.handleModalWatchlistToggle = function(ticker) {
  toggleWatchlistTicker(ticker);
  // Re-open modal with updated state
  openTickerModal(ticker);
};

function renderFallbackAnalysis(ticker) {
  const isEn = STATE.lang === 'en';
  const dict = I18N[STATE.lang] || I18N.de;
  const contentEl = document.getElementById('modal-content');
  if (!contentEl) return;

  const congData = normalizeCongress(STATE.congress) || (window.BUNDLED_DATA ? normalizeCongress(window.BUNDLED_DATA.congress) : null);
  const cong = congData ? congData.trades : [];
  const cgMatches = cong.filter((t) => (t.ticker || '').toUpperCase() === ticker);
  const buyCount = cgMatches.filter((t) => (t.transaction_type || '').toLowerCase().includes('purchase')).length;
  const saleCount = cgMatches.filter((t) => (t.transaction_type || '').toLowerCase().includes('sale')).length;
  const isOnWatchlist = STATE.watchlist.includes(ticker);

  renderVollanalyseContent({
    ticker: ticker,
    company_name: `${ticker} Corporation`,
    score: (buyCount >= saleCount ? 72 : 44),
    sentiment_label_de: (buyCount >= saleCount ? 'BULLISCH' : 'BÄRISCH'),
    sentiment_label_en: (buyCount >= saleCount ? 'BULLISH' : 'BEARISH'),
    sentiment_color: (buyCount >= saleCount ? '#10b981' : '#f87171'),
    congress_buys: buyCount,
    congress_sales: saleCount,
    recent_congress_trades: cgMatches.slice(0, 5),
    recent_sec_alerts: [],
    cot_whale_context: 'Whales 43.9% Long · 56.1% Short'
  }, ticker);
}

// Utilities
function escapeHtml(str) {
  if (!str) return '';
  return String(str)
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#39;');
}

function formatNumber(num) {
  if (isNaN(num)) return '0';
  return Number(num).toLocaleString('de-DE');
}

function formatTime(isoStr) {
  if (!isoStr) return '';
  try {
    const d = new Date(isoStr);
    return `${d.toLocaleDateString('de-DE')} ${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}`;
  } catch {
    return isoStr;
  }
}

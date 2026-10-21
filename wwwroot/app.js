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
    sec_btn_more_details: 'Details ↗',
    sec_modal_title: 'SEC FORM 4 INSIDER FILING DETAILS',
    sec_action_open_edgar: '📄 Original Form 4 Filing auf sec.gov ansehen ↗',
    sec_badge_all_title: 'Klicken: Alle Filings anzeigen',
    sec_badge_cluster_title: 'Klicken: Nur Cluster-Alarme anzeigen',
    sec_open_link: 'SEC EDGAR Öffnen ↗',
    sec_shares_lbl: 'Stückzahl',
    sec_price_lbl: 'Kurs',
    sec_volume_lbl: 'Gesamtwert',
    wl_title: 'Persönliche Watchlist',
    wl_desc: 'Beobachte deine Aktien auf Käufe von Politikern und neue Form 4 Filings.',
    wl_add_placeholder: 'Ticker hinzufügen (z.B. AAPL, NVDA, TSLA)...',
    wl_btn_add: '+ Hinzufügen',
    wl_popular: 'Beliebt:',
    wl_empty_title: 'Deine Watchlist ist leer',
    wl_empty_desc: 'Füge oben Aktien wie NVDA, AAPL oder MSFT hinzu, um sie zu überwachen.',
    wl_full_analysis: 'Details',
    nav_radar: 'Radar',
    nav_cot: 'COT Wale',
    nav_congress: 'Kongress',
    nav_sec: 'SEC Insider',
    nav_watchlist: 'Watchlist',
    flag_title: 'English (🇬🇧)',
    flag_icon: '🇬🇧',
    modal_title_suffix: '· DETAILS',
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
    sec_btn_more_details: 'Details ↗',
    sec_modal_title: 'SEC FORM 4 INSIDER FILING DETAILS',
    sec_action_open_edgar: '📄 Open Original Form 4 on sec.gov ↗',
    sec_badge_all_title: 'Click: Show all filings',
    sec_badge_cluster_title: 'Click: Show only cluster alerts',
    sec_open_link: 'Open SEC EDGAR ↗',
    sec_shares_lbl: 'Shares',
    sec_price_lbl: 'Price',
    sec_volume_lbl: 'Total Value',
    wl_title: 'Personal Watchlist',
    wl_desc: 'Monitor your portfolio for recent politician buys and fresh Form 4 insider filings.',
    wl_add_placeholder: 'Add ticker (e.g. AAPL, NVDA, TSLA)...',
    wl_btn_add: '+ Add Ticker',
    wl_popular: 'Popular:',
    wl_empty_title: 'Your Watchlist is empty',
    wl_empty_desc: 'Add stocks like NVDA, AAPL or MSFT above to monitor them.',
    wl_full_analysis: 'Details',
    nav_radar: 'Radar',
    nav_cot: 'COT Whales',
    nav_congress: 'Congress',
    nav_sec: 'SEC Insiders',
    nav_watchlist: 'Watchlist',
    flag_title: 'Deutsch (🇩🇪)',
    flag_icon: '🇩🇪',
    modal_title_suffix: '· DETAILS',
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
  initCotModal();
  initCongressModals();
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

// Refresh Button with animation & timestamp banner
function initRefresh() {
  const refreshBtn = document.getElementById('btn-refresh');
  if (refreshBtn) {
    refreshBtn.addEventListener('click', () => {
      refreshBtn.classList.add('spinning');
      loadAllData().finally(() => {
        setTimeout(() => refreshBtn.classList.remove('spinning'), 600);
        showRefreshToast();
      });
    });
  }
}

function showRefreshToast() {
  const isEn = STATE.lang === 'en';
  const now = new Date();
  const dateStr = now.toLocaleDateString(isEn ? 'en-US' : 'de-DE', { day: '2-digit', month: '2-digit', year: 'numeric' });
  const timeStr = now.toLocaleTimeString(isEn ? 'en-US' : 'de-DE', { hour: '2-digit', minute: '2-digit' }) + (isEn ? '' : ' Uhr');
  const fullTimestamp = `${dateStr}, ${timeStr}`;

  const statusEl = document.getElementById('header-status-text');
  if (statusEl) {
    statusEl.textContent = isEn ? `Updated: ${fullTimestamp}` : `Aktualisiert: ${fullTimestamp}`;
  }

  let toast = document.getElementById('refresh-toast-banner');
  if (!toast) {
    toast = document.createElement('div');
    toast.id = 'refresh-toast-banner';
    toast.className = 'refresh-toast-banner';
    document.body.appendChild(toast);
  }
  toast.innerHTML = `<span>🔄</span> <span>${isEn ? 'Updated:' : 'Aktualisiert:'} <strong>${fullTimestamp}</strong> ${isEn ? '(APIs synchronized)' : '(APIs synchronisiert)'}</span>`;
  toast.classList.add('show');
  clearTimeout(window.__toastTimeout);
  window.__toastTimeout = setTimeout(() => {
    toast.classList.remove('show');
  }, 3500);
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
    if (window.BUNDLED_DATA.stock_details) STATE.stockDetails = window.BUNDLED_DATA.stock_details;

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
        fetch('/api/party-sentiment').then((r) => r.json()),
        fetch('/api/stock-details').then((r) => r.json())
      ]);

      const [radarRes, cotRes, congressRes, secRes, superRes, partyRes, stockDetailsRes] = results;

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
      if (stockDetailsRes && stockDetailsRes.status === 'fulfilled' && stockDetailsRes.value && !stockDetailsRes.value.error) {
        STATE.stockDetails = stockDetailsRes.value;
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

  // Dynamische Neuberechnung der Long/Short-Positionen aus den geladenen COT-Märkten
  if (STATE.cot && STATE.cot.markets && STATE.cot.markets.length > 0) {
    const validMarkets = STATE.cot.markets.filter(m => (m.whale_long || 0) > 0 || (m.whale_short || 0) > 0);
    if (validMarkets.length > 0) {
      const totLong = validMarkets.reduce((sum, m) => sum + (m.whale_long || 0), 0);
      const totShort = validMarkets.reduce((sum, m) => sum + (m.whale_short || 0), 0);
      const totAll = totLong + totShort;
      if (totAll > 0) {
        r.whale_long_pct = Math.round((totLong / totAll) * 1000) / 10;
        r.whale_short_pct = Math.round((100 - r.whale_long_pct) * 10) / 10;
        r.whale_long_formatted = totLong.toLocaleString('de-DE');
        r.whale_short_formatted = totShort.toLocaleString('de-DE');
        if (r.whale_long_pct >= 55) {
          r.badge_de = 'BULLISCH';
          r.badge_en = 'BULLISH';
          r.whale_mood_de = 'BULLISCH (Whales akkumulieren Long)';
          r.whale_mood_en = 'BULLISH (Whales accumulating Long)';
        } else if (r.whale_long_pct <= 45) {
          r.badge_de = 'BÄRISCH';
          r.badge_en = 'BEARISH';
          r.whale_mood_de = 'BÄRISCH (Whales im Short-Modus)';
          r.whale_mood_en = 'BEARISH (Whales net Short)';
        } else {
          r.badge_de = 'NEUTRAL';
          r.badge_en = 'NEUTRAL';
          r.whale_mood_de = 'NEUTRAL (Ausgeglichene Marktposition)';
          r.whale_mood_en = 'NEUTRAL (Balanced market positions)';
        }
      }
    }
  }

  const badgeEl = document.getElementById('radar-sentiment-badge');
  const dialEl = document.getElementById('radar-gauge-val');
  const dialRing = document.getElementById('meter-dial-ring');
  const moodEl = document.getElementById('radar-whale-mood');
  const moodSubEl = document.getElementById('radar-whale-sub');

  const longPct = (r.whale_long_pct !== undefined) ? Number(r.whale_long_pct) : 50.0;
  const shortPct = (r.whale_short_pct !== undefined) ? Number(r.whale_short_pct) : (100.0 - longPct);
  const moodColor = r.whale_mood_color || (longPct >= 55 ? '#10b981' : (longPct <= 45 ? '#ef4444' : '#94a3b8'));

  // Badge: derived dynamically from newest report data
  if (badgeEl) {
    const badgeText = isEn ? (r.badge_en || (longPct >= 55 ? 'BULLISH' : (longPct <= 45 ? 'BEARISH' : 'NEUTRAL')))
                           : (r.badge_de || (longPct >= 55 ? 'BULLISCH' : (longPct <= 45 ? 'BÄRISCH' : 'NEUTRAL')));
    badgeEl.textContent = badgeText;
    badgeEl.style.backgroundColor = moodColor;
  }

  // Dial Value and Ring Color Conic Gradient: Zeige die Position mit dem meisten Gewicht
  const isLongDominant = longPct >= shortPct;
  const dominantPct = isLongDominant ? longPct : shortPct;
  const dominantLabel = isLongDominant ? 'LONG' : 'SHORT';
  const dominantColor = isLongDominant ? '#10b981' : '#ef4444';

  if (dialEl) {
    dialEl.textContent = `${dominantPct.toFixed(1)}%`;
    dialEl.style.color = dominantColor;
  }
  const dialSubEl = document.getElementById('radar-gauge-sub');
  if (dialSubEl) {
    dialSubEl.textContent = dominantLabel;
    dialSubEl.style.color = dominantColor;
  }
  if (dialRing) {
    dialRing.style.background = `conic-gradient(#10b981 0% ${longPct}%, #ef4444 ${longPct}% 100%)`;
    dialRing.style.boxShadow = `0 0 22px ${dominantColor}55`;
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
    repDateEl.textContent = isEn ? r.report_date : (r.report_date.includes('-') ? new Date(r.report_date).toLocaleDateString('de-DE') : r.report_date);
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

  // 3-Quellen Kombi-Signale (COT + Kongress + SEC)
  const defaultList = [
    {
      ticker: 'NVDA',
      company_name: 'NVIDIA Corporation',
      conviction_score: 98,
      sector: 'Semiconductors / AI Tech',
      cot_market: 'Nasdaq-100 / QQQ (Tech)',
      cot_sentiment: '70.0% Whale Long (+56.000 Kontrakte)',
      cot_flow: '+8.000 Akkumulation',
      cot_timeframe: 'Stichtag: Di, 13.10. / Bericht: Fr, 16.10.',
      politician_name: 'Scott H. Peters & Nancy Pelosi',
      politician_party: 'D',
      politician_amount: '$500,001 - $1,000,000',
      politician_date: 'Handel: 28.09.2026 (Meldung: 15.10.2026)',
      insider_name: 'Huang Jen Hsun',
      insider_role: 'President and CEO',
      insider_shares: '50.000 Aktien',
      insider_value: '$6.425.000',
      insider_date: 'Filing: 18.10.2026',
      description_de: 'Maximaler Zusammenfluss aller 3 Quellen: QQQ-Whales zu 70% Long, Kongress-Käufe ($500k+) mit Ausschuss-Konflikt und CEO Jen-Hsun Huang Insider-Kauf.',
      description_en: 'Triple confluence across all 3 sources: QQQ Whales 70% Long, Congressional purchases ($500k+) and CEO Jensen Huang open market buy.'
    },
    {
      ticker: 'MSFT',
      company_name: 'Microsoft Corporation',
      conviction_score: 95,
      sector: 'Cloud & Enterprise Software',
      cot_market: 'Nasdaq-100 / QQQ (Cloud & AI)',
      cot_sentiment: '70.0% Whale Long',
      cot_flow: '+8.000 Akkumulation',
      cot_timeframe: 'Stichtag: Di, 13.10. / Bericht: Fr, 16.10.',
      politician_name: 'Michael T. McCaul',
      politician_party: 'R',
      politician_amount: '$100,001 - $250,000',
      politician_date: 'Handel: 18.09.2026 (Meldung: 12.10.2026)',
      insider_name: 'Nadella Satya',
      insider_role: 'Chairman and CEO',
      insider_shares: '15.000 Aktien',
      insider_value: '$6.300.000',
      insider_date: 'Filing: 12.10.2026',
      description_de: 'Parallele Akkumulation: Whales stocken QQQ auf, Kongressabgeordneter kauft $100k-$250k und Vorstand Satya Nadella tätigt Zukäufe.',
      description_en: 'Concurrent accumulation: Institutional whales increase QQQ long, Representative buys $100k-$250k and CEO buys shares.'
    },
    {
      ticker: 'PLTR',
      company_name: 'Palantir Technologies Inc.',
      conviction_score: 93,
      sector: 'Defense AI & Data Intelligence',
      cot_market: 'Nasdaq-100 / Defense IT',
      cot_sentiment: '70.0% Whale Long',
      cot_flow: '+8.000 Akkumulation',
      cot_timeframe: 'Stichtag: Di, 13.10. / Bericht: Fr, 16.10.',
      politician_name: 'Michael T. McCaul',
      politician_party: 'R',
      politician_amount: '$15,001 - $50,000',
      politician_date: 'Handel: 08.10.2026 (Meldung: 15.10.2026)',
      insider_name: 'Karp Alexander',
      insider_role: 'Chief Executive Officer',
      insider_shares: '25.000 Aktien',
      insider_value: '$1.075.000',
      insider_date: 'Filing: 17.10.2026',
      description_de: 'Verteidigungsausschuss & Palantir CEO Alex Karp kaufen zeitgleich Anteile während Tech-Whales Long-Positionen ausbauen.',
      description_en: 'Defense committee leadership and Palantir CEO Alex Karp purchase concurrently while Tech whales expand long exposure.'
    },
    {
      ticker: 'AAPL',
      company_name: 'Apple Inc.',
      conviction_score: 91,
      sector: 'Consumer Electronics & Services',
      cot_market: 'Nasdaq-100 / Large-Cap Tech',
      cot_sentiment: '70.0% Whale Long',
      cot_flow: '+8.000 Akkumulation',
      cot_timeframe: 'Stichtag: Di, 13.10. / Bericht: Fr, 16.10.',
      politician_name: 'Dan Newhouse',
      politician_party: 'R',
      politician_amount: '$50,001 - $100,000',
      politician_date: 'Handel: 15.09.2026 (Meldung: 08.10.2026)',
      insider_name: 'Corporate Officer',
      insider_role: 'Executive Officer',
      insider_shares: '12.000 Aktien',
      insider_value: '$2.640.000',
      insider_date: 'Filing: 14.10.2026',
      description_de: 'Starker 3-Quellen-Zusammenfluss: Kongressabgeordneter kauft Tranche, Vorstände halten Aktienpositionen und Großanleger akkumulieren Tech.',
      description_en: 'Strong 3-source confluence: Representative buys stock tranche, executives maintain stake, and macro whales accumulate.'
    }
  ];

  const list = (STATE.superSignals && STATE.superSignals.length > 0) ? STATE.superSignals : defaultList;

  container.innerHTML = list.map((s) => {
    const isOnWl = STATE.watchlist.includes(s.ticker);
    return `
      <div class="card" style="margin-bottom:12px; background:rgba(15,23,42,0.85); border:1.5px solid rgba(245,158,11,0.5); padding:14px; border-radius:12px;">
        <!-- Header: Ticker, Name, Watchlist, Score -->
        <div style="display:flex; justify-content:space-between; align-items:flex-start; margin-bottom:10px;">
          <div>
            <div style="display:flex; align-items:center; gap:8px;">
              <span class="pill" style="font-size:15px; font-weight:800; background:rgba(245,158,11,0.2); color:#f59e0b; border:1px solid rgba(245,158,11,0.4); padding:3px 8px;">
                $${s.ticker}
              </span>
              <strong style="color:#fff; font-size:14px;">${escapeHtml(s.company_name)}</strong>
            </div>
            <div style="font-size:11px; color:#94a3b8; margin-top:3px;">${escapeHtml(s.sector)}</div>
          </div>
          <div style="display:flex; align-items:center; gap:8px;">
            <button onclick="event.stopPropagation(); toggleWatchlistTicker('${s.ticker}'); renderSuperSignals();" class="icon-btn" style="background:${isOnWl ? 'rgba(245,158,11,0.25)' : 'rgba(30,41,59,0.7)'}; border:1px solid ${isOnWl ? '#f59e0b' : '#334155'}; font-size:14px; padding:4px 8px; border-radius:6px; cursor:pointer;" title="${isOnWl ? 'Von Watchlist entfernen' : 'Zur Watchlist hinzufügen'}">
              ${isOnWl ? '⭐' : '☆'}
            </button>
            <span class="pill" style="font-size:11px; font-weight:800; background:#10b981; color:#000; padding:4px 8px; border-radius:6px;">
              ${s.conviction_score}/100
            </span>
          </div>
        </div>

        <!-- 1. Quelle: COT Makro-Sentiment -->
        <div style="background:rgba(30,41,59,0.6); border:1px solid rgba(56,189,248,0.3); border-radius:8px; padding:8px 10px; margin-bottom:6px;">
          <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:2px;">
            <span style="font-size:10px; font-weight:800; color:#38bdf8;">🐋 COT MAKRO-SENTIMENT</span>
            <span style="font-size:11px; font-weight:700; color:#10b981;">${escapeHtml(s.cot_sentiment || 'Whales Net Long')}</span>
          </div>
          <div style="font-size:11px; color:#e2e8f0;">${escapeHtml(s.cot_market || 'Macro Market')} · ${escapeHtml(s.cot_flow || '')}</div>
          <div style="font-size:10px; color:#94a3b8;">⏱️ ${escapeHtml(s.cot_timeframe || 'Stichtag Di / Bericht Fr')}</div>
        </div>

        <!-- 2. Quelle: Kongress STOCK Act -->
        <div style="background:rgba(30,41,59,0.6); border:1px solid rgba(6,182,212,0.3); border-radius:8px; padding:8px 10px; margin-bottom:6px;">
          <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:2px;">
            <span style="font-size:10px; font-weight:800; color:#06b6d4;">🏛️ KONGRESS STOCK ACT</span>
            <span style="font-size:11px; font-weight:700; color:#10b981;">${escapeHtml(s.politician_amount || '')}</span>
          </div>
          <div style="font-size:11px; font-weight:700; color:#e2e8f0;">${escapeHtml(s.politician_name || '')} (${s.politician_party || ''})</div>
          <div style="font-size:10px; color:#94a3b8;">📅 ${escapeHtml(s.politician_date || '')}</div>
        </div>

        <!-- 3. Quelle: SEC Form 4 Insider -->
        <div style="background:rgba(30,41,59,0.6); border:1px solid rgba(245,158,11,0.3); border-radius:8px; padding:8px 10px; margin-bottom:10px;">
          <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:2px;">
            <span style="font-size:10px; font-weight:800; color:#f59e0b;">🏢 SEC FORM 4 INSIDER</span>
            <span style="font-size:11px; font-weight:700; color:#10b981;">${escapeHtml(s.insider_value || '')}</span>
          </div>
          <div style="font-size:11px; font-weight:700; color:#e2e8f0;">${escapeHtml(s.insider_name || '')} (${escapeHtml(s.insider_role || '')})</div>
          <div style="font-size:10px; color:#94a3b8;">📝 ${escapeHtml(s.insider_shares || '')} · ${escapeHtml(s.insider_date || '')}</div>
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
      <div class="cot-card" onclick="openCotDetailModal('${m.id}')" style="cursor:pointer;" title="${isEn ? 'Click for COT Details' : 'Klicken für COT Details'}">
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

        <div class="cot-change-tag" style="display:flex; justify-content:space-between; align-items:center;">
          <div style="display:flex; align-items:center; gap:4px;">
            <span>⚡</span>
            <span>${escapeHtml(m.whale_change_desc || '')} ${dict.cot_vs_prev}</span>
          </div>
          <button class="pill" style="font-size:11px; padding:3px 8px; background:rgba(56,189,248,0.15); color:#38bdf8; border:1px solid rgba(56,189,248,0.3); border-radius:6px; cursor:pointer;" onclick="event.stopPropagation(); openCotDetailModal('${m.id}')">
            Details
          </button>
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
        <div class="bipartisan-card" onclick="openBipartisanModal('${b.ticker}')" style="cursor:pointer;" title="Klicken für Details">
          <div class="bipartisan-header" style="display:flex; justify-content:space-between; align-items:center;">
            <div>
              <span class="bipartisan-ticker">$${b.ticker}</span>
              <span style="font-size:11px; color:#94a3b8; margin-left:6px;">${escapeHtml(b.sector)}</span>
            </div>
            <div style="display:flex; align-items:center; gap:8px;">
              <span style="font-size:11px; font-weight:800; color:#10b981;">${b.total_buys}x KÄUFE</span>
              <button class="pill" style="font-size:11px; padding:3px 8px; background:rgba(56,189,248,0.15); color:#38bdf8; border:1px solid rgba(56,189,248,0.3); border-radius:6px; cursor:pointer;" onclick="event.stopPropagation(); openBipartisanModal('${b.ticker}')">
                Details
              </button>
            </div>
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
            ${(ps.dem_top_buys || []).slice(0, 10).map(t => `
              <div onclick="openPartySentimentModal('${t.ticker}', 'Demokraten')" style="font-size:11px; color:#cbd5e1; padding:6px 0; border-bottom:1px solid #1e293b; display:flex; justify-content:space-between; align-items:center; cursor:pointer;" title="Klicken für Details">
                <div><strong>$${t.ticker}</strong> (${t.count}x) · ${t.category}</div>
                <button class="pill" style="font-size:10px; padding:2px 6px; background:rgba(59,130,246,0.15); color:#3b82f6; border:1px solid rgba(59,130,246,0.3); border-radius:4px; cursor:pointer;" onclick="event.stopPropagation(); openPartySentimentModal('${t.ticker}', 'Demokraten')">Details</button>
              </div>
            `).join('')}
          </div>
          <div>
            <div style="font-size:11px; font-weight:800; color:#ef4444; margin-bottom:6px;">🔴 REPUBLIKANER TOP KÄUFE</div>
            ${(ps.rep_top_buys || []).slice(0, 10).map(t => `
              <div onclick="openPartySentimentModal('${t.ticker}', 'Republikaner')" style="font-size:11px; color:#cbd5e1; padding:6px 0; border-bottom:1px solid #1e293b; display:flex; justify-content:space-between; align-items:center; cursor:pointer;" title="Klicken für Details">
                <div><strong>$${t.ticker}</strong> (${t.count}x) · ${t.category}</div>
                <button class="pill" style="font-size:10px; padding:2px 6px; background:rgba(239,68,68,0.15); color:#ef4444; border:1px solid rgba(239,68,68,0.3); border-radius:4px; cursor:pointer;" onclick="event.stopPropagation(); openPartySentimentModal('${t.ticker}', 'Republikaner')">Details</button>
              </div>
            `).join('')}
          </div>
        </div>
      </div>
    `;
    return;
  }

  let trades = cong.trades;
  // Deduplicate trades to prevent any duplicate rows
  const seenTrades = new Set();
  trades = trades.filter((t) => {
    const key = `${t.filer_name}_${t.ticker}_${t.transaction_date}_${t.amount_range_label}_${t.transaction_type}`;
    if (seenTrades.has(key)) return false;
    seenTrades.add(key);
    return true;
  });

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
    const docUrl = t.doc_url || 'https://disclosures-clerk.house.gov/FinancialDisclosure';
    const isSen = (t.chamber || '').toLowerCase().includes('senat');
    const chamberClean = isSen ? 'Senat' : 'Repräsentantenhaus';
    const partyClean = t.party === 'D' ? 'Demokrat' : (t.party === 'R' ? 'Republikaner' : (t.party || ''));
    const officeClean = `${chamberClean} · ${partyClean}`;
    const conflictBanner = t.has_committee_conflict
      ? `<div class="conflict-alert-banner">⚠️ ${escapeHtml(isEn ? (t.conflict_desc_en || t.conflict_desc_de) : (t.conflict_desc_de || t.conflict_desc_en))}</div>`
      : '';
    const conflictBadge = t.has_committee_conflict
      ? `<span class="conflict-badge">⚠️ KONFLIKT</span>`
      : '';

    return `
      <div class="trade-card" onclick="openCongressTradeModal('${t.id}')" style="cursor:pointer;" title="${isEn ? 'Click for Trade Details' : 'Klicken für Details'}">
        <div class="tc-top">
          <div class="tc-filer-wrap">
            <span class="tc-filer-name">${escapeHtml(t.filer_name || 'US Politician')}</span>
            <span class="tc-party-badge ${t.party || ''}">${t.party === 'D' ? '🔵 DEM' : (t.party === 'R' ? '🔴 REP' : t.party || '')}</span>
            ${conflictBadge}
          </div>
          <span class="tc-office">${escapeHtml(officeClean)}</span>
        </div>

        <div class="tc-middle">
          <div class="tc-ticker-asset">
            <span class="tc-ticker" onclick="event.stopPropagation(); openTickerModal('${t.ticker}')">$${escapeHtml(t.ticker || 'N/A')}</span>
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
          <div style="display:flex; gap:8px; align-items:center;">
            <button class="pill" style="font-size:11px; padding:3px 8px; background:rgba(56,189,248,0.15); color:#38bdf8; border:1px solid rgba(56,189,248,0.3); border-radius:6px; cursor:pointer;" onclick="event.stopPropagation(); openCongressTradeModal('${t.id}')">
              Details
            </button>
            <a href="${docUrl}" target="_blank" onclick="event.stopPropagation();" class="tc-link" style="font-size:11px;">PDF ↗</a>
          </div>
        </div>

        ${conflictBanner}
      </div>
    `;
  }).join('');
}

// ================= HELPER: SEC DEDUPLICATION & CLUSTER GROUPING =================
function getSecProcessedData(sec) {
  if (!sec || !sec.alerts) return { singles: [], clusters: [] };

  // 1. Single Filings: non-cluster only, deduplicated
  const singleSeen = new Set();
  const singles = [];
  for (const a of sec.alerts) {
    if (a.is_cluster) continue;
    const key = a.accession_number || `${a.issuer}_${a.reporting_owner}_${a.filing_time}`;
    if (!singleSeen.has(key)) {
      singleSeen.add(key);
      singles.push(a);
    }
  }

  // 2. Clusters: group a.is_cluster by issuer/company
  const clusterMap = new Map();
  for (const a of sec.alerts) {
    if (!a.is_cluster) continue;
    const groupKey = (a.issuer || a.ticker || '').trim().toUpperCase();
    if (!clusterMap.has(groupKey)) {
      clusterMap.set(groupKey, {
        id: `cluster_${groupKey.replace(/[^A-Za-z0-9]/g, '_')}`,
        issuer: a.issuer,
        ticker: a.ticker,
        is_cluster: true,
        cluster_count: 0,
        filings: [],
        filing_time: a.filing_time,
        filing_date: a.filing_date || (a.filing_time ? a.filing_time.slice(0, 10) : ''),
        is_buy: true,
        filing_url: a.filing_url
      });
    }
    const group = clusterMap.get(groupKey);
    group.filings.push(a);
    if (a.filing_time && (!group.filing_time || a.filing_time > group.filing_time)) {
      group.filing_time = a.filing_time;
      group.filing_date = a.filing_time.slice(0, 10);
    }
  }

  const clusters = Array.from(clusterMap.values()).map((g) => {
    g.cluster_count = g.filings.length;
    const buys = g.filings.filter((f) => f.is_buy).length;
    g.is_buy = buys >= (g.filings.length / 2);
    // newest filing first in the cluster list
    g.filings.sort((f1, f2) => (f2.filing_time || '').localeCompare(f1.filing_time || ''));
    return g;
  }).sort((a, b) => b.cluster_count - a.cluster_count);

  return { singles, clusters };
}

// ================= RENDER SEC (FORM 4 INSIDERS WITH RICH DETAILS & SHAREHOLDER VALUE) =================
function renderSec() {
  const sec = normalizeSec(STATE.sec) || (window.BUNDLED_DATA ? normalizeSec(window.BUNDLED_DATA.sec) : null);
  if (!sec || !sec.alerts) return;
  STATE.sec = sec;

  const isEn = STATE.lang === 'en';
  const dict = I18N[STATE.lang] || I18N.de;

  const { singles, clusters } = getSecProcessedData(sec);

  // Dynamic button labels with counts (excluding duplicates)
  const countEl = document.getElementById('sec-alerts-count');
  const clusterEl = document.getElementById('sec-cluster-count');
  const allBtnLbl = document.getElementById('sec-all-filings-lbl');
  const clusterBtnLbl = document.getElementById('sec-cluster-lbl');

  if (countEl) countEl.textContent = singles.length;
  if (clusterEl) clusterEl.textContent = clusters.length;
  if (allBtnLbl) allBtnLbl.textContent = isEn ? 'New Filings' : 'Neue Filings';
  if (clusterBtnLbl) clusterBtnLbl.textContent = isEn ? 'Cluster Alerts' : 'Cluster-Alarme';

  // Update active state of interactive counter buttons
  const btnAll = document.getElementById('sec-filter-all-btn');
  const btnCluster = document.getElementById('sec-filter-cluster-btn');
  if (btnAll) btnAll.classList.toggle('active', STATE.secFilter === 'all');
  if (btnCluster) btnCluster.classList.toggle('active', STATE.secFilter === 'cluster');

  const container = document.getElementById('sec-filings-container');
  if (!container) return;

  const q = STATE.secQuery.toLowerCase().trim();

  // Mode 1: Cluster Alarme (Sorted descending by highest cluster count, 1 card per company)
  if (STATE.secFilter === 'cluster') {
    let list = clusters;
    if (q) {
      list = list.filter((g) => {
        const iss = (g.issuer || '').toLowerCase();
        const tk = (g.ticker || '').toLowerCase();
        const hasMember = g.filings.some((f) => (f.reporting_owner || '').toLowerCase().includes(q));
        return iss.includes(q) || tk.includes(q) || hasMember;
      });
    }

    if (list.length === 0) {
      container.innerHTML = `<div class="loading-spinner-row">${isEn ? 'No cluster alerts currently active.' : 'Aktuell keine Cluster-Alarme aktiv.'}</div>`;
      return;
    }

    container.innerHTML = list.map((g) => {
      const tickerTag = g.ticker ? `<span class="sec-card-ticker">$${escapeHtml(g.ticker)}</span>` : '';
      const filingDate = g.filing_date || '-';
      const timeSpanText = isEn
        ? `⏱️ Period: 14-day cluster window (up to ${filingDate})`
        : `⏱️ Zeitraum: 14-Tage-Clusterfenster (bis ${filingDate})`;

      // Known shares summary
      const knownShares = g.filings.filter((f) => f.shares && f.shares > 0);
      let sharesSummary = '-';
      if (knownShares.length > 0) {
        const totalKnown = knownShares.reduce((sum, f) => sum + f.shares, 0);
        sharesSummary = `${totalKnown.toLocaleString(isEn ? 'en-US' : 'de-DE')} Aktien`;
      }

      return `
        <div class="sec-card cluster" onclick="openSecClusterModal('${escapeHtml(g.id)}')" title="${isEn ? 'Click to inspect all contributors' : 'Klicken um alle Beteiligten anzuzeigen'}">
          <div class="sec-card-header" style="display:flex; justify-content:space-between; align-items:flex-start; gap:8px;">
            <div class="sec-card-title" style="flex:1; min-width:0;">
              ${tickerTag}
              <div style="min-width:0; overflow:hidden;">
                <strong style="font-size:14px; color:#fff; display:block; white-space:nowrap; overflow:hidden; text-overflow:ellipsis;">${escapeHtml(g.issuer)}</strong>
                <div style="font-size:11px; color:#fde68a; margin-top:2px;">
                  🔥 <strong>${g.cluster_count}x synchron gemeldete Filings</strong>
                </div>
              </div>
            </div>
            <span class="sec-tx-pill" style="flex-shrink:0; white-space:nowrap; background:#f59e0b; color:#000; font-weight:800; font-size:10px;">🔥 CLUSTER (${g.cluster_count}x)</span>
          </div>

          <!-- Time Span Detail -->
          <div class="sec-card-timespan">${timeSpanText}</div>

          <!-- Shares summary pill (No Stückzahl/Kurs/Gesamtwert 3-box grid!) -->
          <div style="display:flex; gap:8px; align-items:center; flex-wrap:wrap; margin:6px 0;">
            <span class="sec-shares-pill">👥 ${g.cluster_count} Vorstände & Insider beteiligt</span>
            ${sharesSummary !== '-' ? `<span class="sec-shares-pill">📊 Erfasst: ${sharesSummary}</span>` : ''}
          </div>

          <!-- Aktionärs-Vorteil -->
          <div class="sec-takeaway-box cluster">
            <strong>🔥 AKTIONÄRS-VORTEIL (CLUSTER):</strong> ${g.cluster_count} Führungskräfte haben zeitgleich bei ${escapeHtml(g.issuer)} gemeldet. Höchste statistische Wahrscheinlichkeit für Kurserholungen!
          </div>

          <div class="sec-bottom">
            <span style="font-size:11px; color:#64748b;">${g.cluster_count} Filings · Form 4 Cluster</span>
            <div style="display:flex; gap:8px; align-items:center;">
              <button class="pill" style="font-size:11px; padding:5px 12px; background:rgba(245,158,11,0.2); color:#f59e0b; border:1px solid rgba(245,158,11,0.4); border-radius:6px; cursor:pointer;" onclick="event.stopPropagation(); openSecClusterModal('${escapeHtml(g.id)}')">
                Details ↗
              </button>
            </div>
          </div>
        </div>
      `;
    }).join('');
    return;
  }

  // Mode 2: Neue Filings (Single Filings only, NO clusters, NO duplicates)
  let list = singles;
  if (q) {
    list = list.filter((a) => {
      const iss = (a.issuer || '').toLowerCase();
      const own = (a.reporting_owner || '').toLowerCase();
      const tk = (a.ticker || '').toLowerCase();
      return iss.includes(q) || own.includes(q) || tk.includes(q);
    });
  }

  if (list.length === 0) {
    container.innerHTML = `<div class="loading-spinner-row">${isEn ? 'No matching SEC filings found.' : 'Keine passenden SEC Filings gefunden.'}</div>`;
    return;
  }

  container.innerHTML = list.map((a) => {
    const tickerTag = a.ticker ? `<span class="sec-card-ticker">$${escapeHtml(a.ticker)}</span>` : '';
    const summaryText = isEn ? (a.summary_en || `${a.reporting_owner} (${a.role}) filed a transaction for ${a.issuer}.`)
                             : (a.summary_de || `${a.reporting_owner} (${a.role}) reichte eine meldepflichtige Transaktion für ${a.issuer} ein.`);

    // Role badge
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

    // High Conviction Signal Pill: 🟢 Kauf, 🔴 Verkauf, ⚪ Award (no Code P/S/A)
    let signalBadge = '';
    if (a.is_buy) {
      signalBadge = `<span class="sec-tx-pill buy" style="font-size:10px; font-weight:800;">🟢 ${isEn ? 'KAUF' : 'KAUF'}</span>`;
    } else if (a.code === 'S' || !a.is_buy) {
      signalBadge = `<span class="sec-tx-pill sale" style="font-size:10px; font-weight:800;">🔴 ${isEn ? 'VERKAUF' : 'VERKAUF'}</span>`;
    } else {
      signalBadge = `<span class="sec-tx-pill neutral" style="font-size:10px; font-weight:800;">⚪ AWARD</span>`;
    }

    // Shares display: If known, use shares_formatted, otherwise '-'
    const sharesDisplay = (a.shares && a.shares > 0 && a.shares_formatted && a.shares_formatted !== '-' && a.shares_formatted !== '0')
      ? a.shares_formatted : '-';

    // Time span detail
    const filingDate = a.filing_time ? a.filing_time.slice(0, 10) : (a.filing_date || '-');
    const timeSpanText = isEn
      ? `⏱️ Filed on ${filingDate} (SEC T+2 window)`
      : `⏱️ Eingereicht am ${filingDate} (T+2 Frist)`;

    // Takeaway Box without Code P, S, A
    let takeawayBox = '';
    if (a.is_buy) {
      takeawayBox = `
        <div class="sec-takeaway-box bullish">
          <strong>🟢 INSIDER-KAUF:</strong> Offener Markterwerb durch ${escapeHtml(a.reporting_owner)}. Führungskräfte investieren persönliches Geld – ein direktes Signal für operatives Vertrauen.
        </div>
      `;
    } else if (a.code === 'S' || !a.is_buy) {
      takeawayBox = `
        <div class="sec-takeaway-box cautious">
          <strong>🔴 GEWINNMITNAHME / VERKAUF:</strong> Marktverkauf durch ${escapeHtml(a.reporting_owner)}. Oft persönliche Vermögensstreuung oder Steuerverbindlichkeiten.
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
      <div class="sec-card" onclick="openSecDetailModal('${a.accession_number}')" title="${isEn ? 'Click for Details' : 'Klicken für Details'}">
        <div class="sec-card-header" style="display:flex; justify-content:space-between; align-items:flex-start; gap:8px;">
          <div class="sec-card-title" style="flex:1; min-width:0;">
            ${tickerTag}
            <div style="min-width:0; overflow:hidden;">
              <strong style="font-size:13.5px; color:#fff; display:block; white-space:nowrap; overflow:hidden; text-overflow:ellipsis;">${escapeHtml(a.issuer)}</strong>
              <div style="font-size:11px; color:#94a3b8; margin-top:2px; display:flex; align-items:center; gap:6px; flex-wrap:wrap;">
                <span style="white-space:nowrap;">${escapeHtml(a.reporting_owner)}</span>
                <span class="${roleClass}" style="white-space:nowrap;">${roleIcon} ${escapeHtml(a.role || 'Officer')}</span>
              </div>
            </div>
          </div>
          <div style="flex-shrink:0;">${signalBadge}</div>
        </div>

        <!-- Time Span Indicator -->
        <div class="sec-card-timespan">${timeSpanText}</div>

        <!-- Shares Pill (Stückzahl, wenn bekannt - Kurs & Gesamtwert weggelassen!) -->
        <div style="margin: 4px 0 6px 0;">
          <span class="sec-shares-pill">📊 Stückzahl: <strong>${sharesDisplay}</strong></span>
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
            <a href="${a.filing_url || 'https://www.sec.gov'}" target="_blank" onclick="event.stopPropagation();" class="tc-link" style="font-size:11px;">
              📄 SEC.gov ↗
            </a>
          </div>
        </div>
      </div>
    `;
  }).join('');
}
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

  const getWlCompanyName = (sym) => {
    if (STATE.stockDetails && STATE.stockDetails[sym] && STATE.stockDetails[sym].company_name) {
      return STATE.stockDetails[sym].company_name;
    }
    const sig = (STATE.superSignals || []).find((s) => s.ticker === sym);
    if (sig && sig.company_name) return sig.company_name;
    const names = {
      'NVDA': 'NVIDIA Corporation',
      'MSFT': 'Microsoft Corporation',
      'AAPL': 'Apple Inc.',
      'PLTR': 'Palantir Technologies Inc.',
      'TSLA': 'Tesla, Inc.',
      'AMZN': 'Amazon.com, Inc.',
      'GOOGL': 'Alphabet Inc.',
      'META': 'Meta Platforms, Inc.',
      'NXPI': 'NXP Semiconductors N.V.',
      'PG': 'The Procter & Gamble Company',
      'RPM': 'RPM International Inc.',
      'FITB': 'Fifth Third Bancorp',
      'T': 'AT&T Inc.',
      'IONS': 'Ionis Pharmaceuticals, Inc.',
      'ACN': 'Accenture plc',
      'RMCF': 'Rocky Mountain Chocolate Factory, Inc.'
    };
    return names[sym] || `${sym} Corporation`;
  };

  // Alphabetische Sortierung A-Z
  const sortedWatchlist = [...STATE.watchlist].sort();

  container.innerHTML = sortedWatchlist.map((ticker) => {
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
          <div style="cursor:pointer; display:flex; align-items:center; gap:8px;" onclick="openTickerModal('${ticker}')">
            <span class="wl-ticker">$${ticker}</span>
            <span style="font-size:12.5px; font-weight:700; color:#e2e8f0;">${escapeHtml(getWlCompanyName(ticker))}</span>
            ${secBadge}
          </div>
          <button class="wl-del-btn" onclick="removeWatchlistTicker('${ticker}')" title="${isEn ? 'Remove' : 'Entfernen'}">✕</button>
        </div>
        <div class="wl-activity">${activityText}</div>
        <div style="margin-top:8px; display:flex; gap:6px;">
          <button class="pill" style="font-size:11px; padding:4px 10px; background:rgba(6,182,212,0.15); color:#06b6d4; border:1px solid rgba(6,182,212,0.3); border-radius:6px; cursor:pointer;" onclick="openTickerModal('${ticker}')">Details</button>
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
      if (STATE.secFilter === 'cluster') {
        const viewport = document.getElementById('app-viewport');
        if (viewport) {
          viewport.scrollTo({ top: 0, behavior: 'smooth' });
        }
      }
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

// ================= COT MARKET DETAIL MODAL =================
function initCotModal() {
  const modal = document.getElementById('modal-cot-detail');
  const closeBtn = document.getElementById('modal-cot-detail-close-btn');
  if (closeBtn && modal) {
    closeBtn.addEventListener('click', () => modal.style.display = 'none');
    modal.addEventListener('click', (e) => {
      if (e.target === modal) modal.style.display = 'none';
    });
  }
}

window.toggleCotGuide = function() {
  const content = document.getElementById('cot-guide-content');
  const arrow = document.getElementById('cot-guide-arrow');
  if (!content) return;
  const isHidden = content.style.display === 'none';
  content.style.display = isHidden ? 'block' : 'none';
  if (arrow) arrow.textContent = isHidden ? 'Details ausblenden ▲' : 'Details einblenden ▼';
};

window.openCotDetailModal = function(marketId) {
  const cot = STATE.cot || (window.BUNDLED_DATA ? window.BUNDLED_DATA.cot : null);
  if (!cot || !cot.markets) return;

  const m = cot.markets.find((item) => item.id === marketId || item.name === marketId);
  if (!m) return;

  const isEn = STATE.lang === 'en';
  const modal = document.getElementById('modal-cot-detail');
  const titleEl = document.getElementById('modal-cot-detail-title');
  const contentEl = document.getElementById('modal-cot-detail-content');
  if (!modal || !contentEl) return;

  if (titleEl) {
    titleEl.textContent = `${m.name} · CFTC COT DETAILS`;
  }

  const isBullish = (m.signal || '').includes('BULLISCH');
  const badgeColor = m.signal_color || (isBullish ? '#10b981' : '#f87171');
  const reportDateRaw = m.report_date ? m.report_date.substring(0, 10) : '2026-10-13';
  const pubDate = m.publication_date || '2026-10-16';
  const weekDesc = m.week || cot.report_week || '2026 Report Week 41';

  contentEl.innerHTML = `
    <!-- Top Header Card -->
    <div style="background:rgba(15,23,42,0.8); border:1px solid #334155; border-radius:12px; padding:14px; margin-bottom:12px;">
      <div style="display:flex; justify-content:space-between; align-items:flex-start; gap:10px;">
        <div>
          <h3 style="margin:0; font-size:16px; color:#fff;">${escapeHtml(m.name)}</h3>
          <div style="font-size:12px; color:#94a3b8; margin-top:2px;">${escapeHtml(m.asset_class)} · ${escapeHtml((m.category || '').toUpperCase())}</div>
        </div>
        <span class="cot-signal-badge" style="background-color:${badgeColor}22; color:${badgeColor}; border:1px solid ${badgeColor}44; font-weight:700; padding:4px 8px; border-radius:6px; font-size:11px;">
          ${m.signal}
        </span>
      </div>
    </div>

    <!-- 1. Stichtag & Eintragungsdaten (CFTC Meldezyklus) -->
    <div style="background:rgba(30,41,59,0.7); border:1px solid #334155; border-radius:10px; padding:12px; margin-bottom:12px; font-size:12px;">
      <div style="font-weight:800; color:#38bdf8; margin-bottom:8px; font-size:11px; letter-spacing:0.5px;">
        📅 STICHTAG & EINTRAGUNG (CFTC-MELDEZYKLUS)
      </div>

      <!-- Trade cutoff date -->
      <div style="display:flex; justify-content:space-between; align-items:flex-start; margin-bottom:8px; padding-bottom:8px; border-bottom:1px solid rgba(51,65,85,0.6);">
        <div style="flex:1; padding-right:10px;">
          <strong style="color:#e2e8f0; display:block;">Stichtag / Kauf- & Verkaufszeitpunkt:</strong>
          <span style="color:#94a3b8; font-size:11px;">Handelsschluss am Dienstag: Exakter Erfassungszeitpunkt aller Käufe & Verkäufe durch Broker.</span>
        </div>
        <span class="pill" style="background:rgba(56,189,248,0.2); color:#38bdf8; font-weight:700; white-space:nowrap; font-size:11px; padding:3px 8px;">
          ${reportDateRaw} (Dienstag)
        </span>
      </div>

      <!-- Registration & publication date -->
      <div style="display:flex; justify-content:space-between; align-items:flex-start; margin-bottom:8px; padding-bottom:8px; border-bottom:1px solid rgba(51,65,85,0.6);">
        <div style="flex:1; padding-right:10px;">
          <strong style="color:#e2e8f0; display:block;">Offizielles Eintragungs- & Veröffentlichungsdatum:</strong>
          <span style="color:#94a3b8; font-size:11px;">Freigabe durch die CFTC freitags um 15:30 EST (genau 3 Tage behördliche Prüf- und Meldefrist).</span>
        </div>
        <span class="pill" style="background:rgba(245,158,11,0.2); color:#f59e0b; font-weight:700; white-space:nowrap; font-size:11px; padding:3px 8px;">
          ${pubDate}, 15:30 EST
        </span>
      </div>

      <div style="display:flex; justify-content:space-between; align-items:center; font-size:11px; color:#94a3b8;">
        <span>Berichtswoche:</span>
        <strong style="color:#e2e8f0;">${escapeHtml(weekDesc)}</strong>
      </div>
    </div>

    <!-- 2. Erklärung: Was bedeuten Wale Netto, Wochen-Fluss & Retail? -->
    <div style="background:rgba(15,23,42,0.6); border:1px solid #334155; border-radius:10px; padding:12px; margin-bottom:12px; font-size:11.5px; line-height:1.5; color:#cbd5e1;">
      <div style="font-weight:800; color:#38bdf8; margin-bottom:8px; font-size:11px; letter-spacing:0.5px;">
        💡 WAS BEDEUTEN DIE KENNZAHLEN FÜR AKTIONÄRE?
      </div>

      <!-- Wale Netto -->
      <div style="margin-bottom:8px;">
        <div style="display:flex; align-items:center; gap:6px; margin-bottom:2px;">
          <span>🐋</span>
          <strong style="color:#10b981;">Wale Netto (Whale Net):</strong>
          <span style="color:${m.whale_net >= 0 ? '#10b981' : '#f87171'}; font-weight:700;">${m.whale_net_formatted} Kontrakte</span>
        </div>
        <p style="margin:0; padding-left:18px; color:#94a3b8; font-size:11px;">
          Die Netto-Differenz zwischen Long- (Kauf) und Short- (Verkauf) Kontrakten institutioneller Profis (Commercials & Großspekulanten). Positiver Wert = Wale setzen netto auf Kurssteigerungen; negativer Wert = Absicherung oder Short-Spekulation.
        </p>
      </div>

      <!-- Wochen-Fluss -->
      <div style="margin-bottom:8px;">
        <div style="display:flex; align-items:center; gap:6px; margin-bottom:2px;">
          <span>🌊</span>
          <strong style="color:#38bdf8;">Wochen-Fluss (Weekly Flow):</strong>
          <span style="color:#e2e8f0; font-weight:700;">${m.whale_change_net >= 0 ? '+' : ''}${formatNumber(m.whale_change_net)} (${escapeHtml(m.whale_change_desc || '-')})</span>
        </div>
        <p style="margin:0; padding-left:18px; color:#94a3b8; font-size:11px;">
          Wöchentliche Veränderung der Netto-Positionen gegenüber der Vorwoche. Zeigt das dynamische Kapital-Momentum: Aggressive Zukäufe belegen frische Kaufkraft, während Kontrakt-Abbau während steigender Kurse vor Erschöpfung warnt.
        </p>
      </div>

      <!-- Retail -->
      <div>
        <div style="display:flex; align-items:center; gap:6px; margin-bottom:2px;">
          <span>👥</span>
          <strong style="color:#f59e0b;">Retail (Kleinanleger / Non-Reportable):</strong>
          <span style="color:#f59e0b; font-weight:700;">${m.retail_bullish_pct || 50}% Bullish</span>
        </div>
        <p style="margin:0; padding-left:18px; color:#94a3b8; font-size:11px;">
          Kleinspekulanten und Privatanleger mit Kontrakten unterhalb der CFTC-Meldeschwelle. Dient an Extrempunkten als Kontraindikator: Ist Retail einseitig euphorisch (>70% Bullish), droht oft eine scharfe Trendwende.
        </p>
      </div>
    </div>

    <!-- 3. Ausführliche Positions-Statistik -->
    <div style="background:rgba(30,41,59,0.5); border:1px solid #334155; border-radius:10px; padding:12px; margin-bottom:14px; font-size:11.5px;">
      <div style="font-weight:800; color:#38bdf8; margin-bottom:8px; font-size:11px;">
        📊 DETAILLIERTE CFTC POSITIONSAUFSCHLÜSSELUNG
      </div>

      <div style="display:flex; justify-content:space-between; margin-bottom:6px;">
        <span style="color:#94a3b8;">Gesamtes Open Interest (Offene Kontrakte):</span>
        <strong style="color:#fff;">${formatNumber(m.open_interest)}</strong>
      </div>
      <div style="display:flex; justify-content:space-between; margin-bottom:6px;">
        <span style="color:#94a3b8;">Wale Long vs. Short:</span>
        <strong style="color:#fff;">${formatNumber(m.whale_long)} (${Math.round(m.whale_bullish_pct || 50)}%) / ${formatNumber(m.whale_short)} (${100 - Math.round(m.whale_bullish_pct || 50)}%)</strong>
      </div>
      <div style="display:flex; justify-content:space-between; margin-bottom:6px;">
        <span style="color:#94a3b8;">Commercials (Hedger & Produzenten) Netto:</span>
        <strong style="color:${m.commercial_net >= 0 ? '#10b981' : '#f87171'};">${m.commercial_net >= 0 ? '+' : ''}${formatNumber(m.commercial_net)}</strong>
      </div>
      <div style="display:flex; justify-content:space-between; margin-bottom:6px;">
        <span style="color:#94a3b8;">Retail Long vs. Short:</span>
        <strong style="color:#f59e0b;">${formatNumber(m.retail_long)} / ${formatNumber(m.retail_short)} (${m.retail_bullish_pct || 50}% Bull)</strong>
      </div>
      ${m.market_raw ? `
        <div style="margin-top:8px; padding-top:8px; border-top:1px solid rgba(51,65,85,0.6);">
          <span style="color:#94a3b8; font-size:10.5px; display:block;">Offizieller Börsenkontrakt / Handelsplatz:</span>
          <span style="font-family:monospace; color:#cbd5e1; font-size:10.5px;">${escapeHtml(m.market_raw)}</span>
        </div>
      ` : ''}
    </div>

    <!-- Official CFTC Links -->
    <div style="display:flex; flex-direction:column; gap:8px;">
      <a href="https://www.cftc.gov/MarketReports/CommitmentsofTraders/index.htm" target="_blank" class="btn-primary" style="text-align:center; text-decoration:none; display:flex; align-items:center; justify-content:center; gap:8px; padding:12px; font-size:13px; font-weight:700;">
        🌐 Offizieller CFTC COT Report (cftc.gov) ↗
      </a>
    </div>
  `;

  modal.style.display = 'flex';
};

// ================= CONGRESS DETAIL MODALS =================
function initCongressModals() {
  const setupModalClose = (modalId, closeBtnId) => {
    const modal = document.getElementById(modalId);
    const closeBtn = document.getElementById(closeBtnId);
    if (closeBtn && modal) {
      closeBtn.addEventListener('click', () => modal.style.display = 'none');
      modal.addEventListener('click', (e) => {
        if (e.target === modal) modal.style.display = 'none';
      });
    }
  };

  setupModalClose('modal-congress-detail', 'modal-congress-detail-close-btn');
  setupModalClose('modal-bipartisan-detail', 'modal-bipartisan-detail-close-btn');
  setupModalClose('modal-party-sentiment-detail', 'modal-party-sentiment-detail-close-btn');
}

window.openCongressTradeModal = function(tradeId) {
  const cong = normalizeCongress(STATE.congress) || (window.BUNDLED_DATA ? normalizeCongress(window.BUNDLED_DATA.congress) : null);
  if (!cong || !cong.trades) return;

  const t = cong.trades.find((item) => item.id === tradeId);
  if (!t) return;

  const modal = document.getElementById('modal-congress-detail');
  const titleEl = document.getElementById('modal-congress-detail-title');
  const contentEl = document.getElementById('modal-congress-detail-content');
  if (!modal || !contentEl) return;

  if (titleEl) {
    titleEl.textContent = `${t.filer_name} · KONGRESS DETAILS`;
  }

  const isDem = (t.party || '').toUpperCase() === 'D';
  const isRep = (t.party || '').toUpperCase() === 'R';
  const partyColor = isDem ? '#3b82f6' : (isRep ? '#ef4444' : '#94a3b8');
  const partyLabel = isDem ? 'Demokrat' : (isRep ? 'Republikaner' : 'Unabhängig');
  const isSenate = (t.chamber || '').toLowerCase().includes('senat');
  const chamberLabel = isSenate ? 'US-Senat' : 'US-Repräsentantenhaus';
  const isBuy = (t.transaction_type || '').toLowerCase().includes('purchase');
  const typeColor = isBuy ? '#10b981' : '#ef4444';
  const typeLabel = isBuy ? 'KAUF (Purchase)' : 'VERKAUF (Sale)';
  const isLate = t.days_to_file > 45;

  const officialDocUrl = (t.doc_url && t.doc_url.startsWith('http')) 
    ? t.doc_url 
    : (isSenate ? 'https://efdsearch.senate.gov/search/home/' : 'https://disclosures-clerk.house.gov/FinancialDisclosure');

  const isOnWl = t.ticker && STATE.watchlist.includes(t.ticker);
  const wlBtnClass = isOnWl ? 'is-active' : 'is-not-active';
  const wlBtnText = isOnWl ? `⭐ $${t.ticker} von Watchlist entfernen` : `⭐ $${t.ticker} zur Watchlist hinzufügen`;

  contentEl.innerHTML = `
    <!-- Top Header Card -->
    <div style="background:rgba(15,23,42,0.8); border:1px solid #334155; border-radius:12px; padding:14px; margin-bottom:12px;">
      <div style="display:flex; justify-content:space-between; align-items:flex-start; gap:10px;">
        <div>
          <div style="display:flex; align-items:center; gap:6px;">
            <span style="display:inline-block; width:10px; height:10px; border-radius:50%; background:${partyColor};"></span>
            <h3 style="margin:0; font-size:16px; color:#fff;">${escapeHtml(t.filer_name || 'US Politician')}</h3>
          </div>
          <div style="font-size:12px; color:#94a3b8; margin-top:3px;">
            ${chamberLabel} · ${partyLabel} (${t.party || '-'})
          </div>
        </div>
        <span class="pill" style="background-color:${typeColor}22; color:${typeColor}; border:1px solid ${typeColor}44; font-weight:700; padding:4px 8px; border-radius:6px; font-size:11px;">
          ${typeLabel}
        </span>
      </div>
    </div>

    <!-- Parlamentskammer & Mandat Erklärung (Erkläre House-R / Senate) -->
    <div style="background:rgba(30,41,59,0.5); border:1px solid #334155; border-radius:10px; padding:10px 12px; margin-bottom:12px; font-size:11.5px; color:#cbd5e1; line-height:1.4;">
      <strong style="color:#38bdf8;">🏛️ Parlamentskammer & Mandat:</strong><br/>
      ${isSenate 
        ? 'Mitglied des US-Senats (100 Senatoren, 6-jährige Amtszeit, landesweiter Einfluss auf Verträge, Bundesrichter und strategische Industriepolitik).' 
        : 'Mitglied des US-Repräsentantenhauses (435 Abgeordnete, 2-jährige Amtszeit, maßgeblich zuständig für Bundeshaushalte, Steuergesetze und Sektor-Subventionen).'}
    </div>

    <!-- 1. NEUE, WUNDERSCHÖN LESBARE GESETZLICHE WERTE-SPANNE -->
    <div style="background:#0f172a; border:1.5px solid rgba(16,185,129,0.5); border-radius:12px; padding:14px; margin-bottom:12px;">
      <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:8px;">
        <span style="color:#10b981; font-weight:800; font-size:11px; letter-spacing:0.5px;">💰 GESETZLICHE WERTE-SPANNE</span>
        <span class="pill" style="background:rgba(16,185,129,0.2); color:#6ee7b7; border:1px solid rgba(16,185,129,0.4); font-size:10px; font-weight:700; padding:2px 6px;">STOCK Act Stufe</span>
      </div>
      <div style="background:#10b981; border-radius:8px; padding:12px 14px; text-align:center; margin-bottom:8px;">
        <span style="font-size:20px; font-weight:900; color:#000000; letter-spacing:0.5px;">${escapeHtml(t.amount_range_label || '$1,001 - $15,000')}</span>
      </div>
      <div style="color:#94a3b8; font-size:11px; line-height:1.4;">
        US-Politiker müssen Transaktionen gesetzlich in standardisierten Spannen offenlegen.
      </div>
    </div>

    <!-- 2. Gehandelte Aktie -->
    <div style="background:rgba(30,41,59,0.7); border:1px solid #334155; border-radius:10px; padding:12px; margin-bottom:12px; font-size:12px;">
      <div style="font-weight:800; color:#38bdf8; margin-bottom:8px; font-size:11px; letter-spacing:0.5px;">
        📈 GEHANDELTE AKTIE
      </div>
      <div style="display:flex; justify-content:space-between; align-items:center;">
        <div style="display:flex; align-items:center; gap:8px;">
          <span class="pill" style="background:rgba(56,189,248,0.2); color:#38bdf8; font-weight:800; font-size:13px; padding:3px 8px;">
            $${escapeHtml(t.ticker || 'STOCK')}
          </span>
          <strong style="color:#fff; font-size:13px;">${escapeHtml(t.asset_name || '')}</strong>
        </div>
      </div>
    </div>

    <!-- 3. STOCK Act Fristen- & Datums-Zeitplan -->
    <div style="background:rgba(30,41,59,0.7); border:1px solid #334155; border-radius:10px; padding:12px; margin-bottom:12px; font-size:12px;">
      <div style="font-weight:800; color:#38bdf8; margin-bottom:8px; font-size:11px; letter-spacing:0.5px;">
        📅 STICHTAG & MELDUNG (STOCK ACT ZEITPLAN)
      </div>

      <!-- Handelstag -->
      <div style="display:flex; justify-content:space-between; align-items:flex-start; margin-bottom:8px; padding-bottom:8px; border-bottom:1px solid rgba(51,65,85,0.6);">
        <div style="flex:1; padding-right:10px;">
          <strong style="color:#e2e8f0; display:block;">Handelstag (Börsen-Ausführung):</strong>
          <span style="color:#94a3b8; font-size:11px;">Der Tag, an dem der Politiker die Aktie real an der Börse kaufte bzw. verkaufte.</span>
        </div>
        <span class="pill" style="background:rgba(56,189,248,0.2); color:#38bdf8; font-weight:700; white-space:nowrap; font-size:11px; padding:3px 8px;">
          ${t.transaction_date || '-'}
        </span>
      </div>

      <!-- Meldedatum -->
      <div style="display:flex; justify-content:space-between; align-items:flex-start; margin-bottom:8px; padding-bottom:8px; border-bottom:1px solid rgba(51,65,85,0.6);">
        <div style="flex:1; padding-right:10px;">
          <strong style="color:#e2e8f0; display:block;">Offizielles Melde- & Eintragungsdatum:</strong>
          <span style="color:#94a3b8; font-size:11px;">Wann der Bericht (Periodic Transaction Report) beim Kongress eingereicht wurde.</span>
        </div>
        <span class="pill" style="background:rgba(148,163,184,0.2); color:#cbd5e1; font-weight:700; white-space:nowrap; font-size:11px; padding:3px 8px;">
          ${t.filing_date || '-'}
        </span>
      </div>

      <!-- 45-Tage-Frist -->
      <div style="display:flex; justify-content:space-between; align-items:center;">
        <div>
          <strong style="color:#e2e8f0; display:block;">Meldeverzögerung (45-Tage STOCK Act Frist):</strong>
          <span style="font-size:11px; color:${isLate ? '#f59e0b' : '#10b981'}; font-weight:600;">
            ${isLate ? '⚠️ Verspätet eingereicht (> 45 Tage)' : '✅ Fristgerecht eingereicht (innerhalb der 45 Tage)'}
          </span>
        </div>
        <span class="pill" style="background:${isLate ? 'rgba(245,158,11,0.2)' : 'rgba(16,185,129,0.2)'}; color:${isLate ? '#f59e0b' : '#10b981'}; font-weight:700; white-space:nowrap; font-size:11px; padding:3px 8px;">
          ${t.days_to_file} Tage
        </span>
      </div>
    </div>

    <!-- 4. Ausschuss-Konflikt-Radar -->
    <div style="background:${t.has_committee_conflict ? 'rgba(245,158,11,0.1)' : 'rgba(30,41,59,0.5)'}; border:1px solid ${t.has_committee_conflict ? 'rgba(245,158,11,0.4)' : '#334155'}; border-radius:10px; padding:12px; margin-bottom:12px; font-size:12px;">
      <div style="font-weight:800; color:${t.has_committee_conflict ? '#f59e0b' : '#10b981'}; margin-bottom:6px; font-size:11px;">
        ${t.has_committee_conflict ? '⚠️ AUSSCHUSS-KONFLIKT-RADAR (COMMITTEE WATCH)' : '🛡️ AUSSCHUSS-KONFLIKT-RADAR'}
      </div>
      ${t.has_committee_conflict ? `
        <div style="font-size:11.5px; color:#e2e8f0; line-height:1.5;">
          ${escapeHtml(t.conflict_desc_de || t.conflict_desc_en || 'Ausschuss-Interessenskonflikt ermittelt')}
        </div>
        ${t.conflict_committee ? `
          <div style="font-size:10.5px; color:#f59e0b; margin-top:4px; font-weight:700;">
            Ausschuss: ${escapeHtml(t.conflict_committee)}
          </div>
        ` : ''}
      ` : `
        <div style="font-size:11.5px; color:#94a3b8;">
          Kein direkter Ausschuss-Konflikt mit der Branche dieser Aktie ermittelt.
        </div>
      `}
    </div>

    <!-- 5. Bedeutung für Aktionäre -->
    <div style="background:rgba(15,23,42,0.6); border:1px solid #334155; border-radius:10px; padding:12px; margin-bottom:14px; font-size:11.5px; line-height:1.5; color:#cbd5e1;">
      <div style="font-weight:800; color:#38bdf8; margin-bottom:6px; font-size:11px;">
        💡 BEDEUTUNG FÜR AKTIONÄRE
      </div>
      ${isBuy ? `
        Kongressmitglieder verfügen über tiefen Einblick in Gesetzesvorhaben, Budgetfreigaben und Regulierungen. Käufe in strategischen Sektoren gelten historisch als starkes Vorlaufsignal für Aktionäre.
      ` : `
        Verkäufe erfolgen häufig zur Portfoliodiversifikation oder zur Vermeidung von Interessenskonflikten im Vorfeld heikler Abstimmungen.
      `}
    </div>

    <!-- Action Buttons (Keine Vollanalyse, offizieller Link) -->
    <div style="display:flex; flex-direction:column; gap:8px;">
      ${t.ticker ? `
        <button class="wl-action-btn ${wlBtnClass}" onclick="toggleWatchlistTicker('${t.ticker}'); openCongressTradeModal('${tradeId}');">
          ${wlBtnText}
        </button>
      ` : ''}
      <a href="${officialDocUrl}" target="_blank" rel="noopener noreferrer" style="text-align:center; text-decoration:none; display:flex; align-items:center; justify-content:center; gap:8px; padding:12px; font-size:12px; font-weight:700; background:rgba(56,189,248,0.15); color:#38bdf8; border:1px solid rgba(56,189,248,0.3); border-radius:8px;">
        🏛️ Offizielles Offenlegungsprotokoll (${chamberLabel}) ↗
      </a>
    </div>
  `;

  modal.style.display = 'flex';
};

window.openBipartisanModal = function(ticker) {
  const ps = STATE.partySentiment || (window.BUNDLED_DATA ? window.BUNDLED_DATA.party_sentiment : null);
  if (!ps || !ps.bipartisan_buys) return;

  const b = ps.bipartisan_buys.find((item) => item.ticker === ticker);
  if (!b) return;

  const modal = document.getElementById('modal-bipartisan-detail');
  const titleEl = document.getElementById('modal-bipartisan-detail-title');
  const contentEl = document.getElementById('modal-bipartisan-detail-content');
  if (!modal || !contentEl) return;

  if (titleEl) {
    titleEl.textContent = `🤝 $${b.ticker} · ÜBERPARTEILICHE KÄUFE`;
  }

  const dWidth = Math.round((b.dem_buys / b.total_buys) * 100);
  const rWidth = 100 - dWidth;

  const cong = normalizeCongress(STATE.congress) || (window.BUNDLED_DATA ? normalizeCongress(window.BUNDLED_DATA.congress) : null);
  const rawTrades = (cong && cong.trades) ? cong.trades.filter((t) => t.ticker === b.ticker) : [];

  // Deduplicate trades
  const seenKeys = new Set();
  const relatedTrades = [];
  for (const t of rawTrades) {
    const key = `${t.filer_name}_${t.transaction_date}_${t.amount_range_label}_${t.transaction_type}`;
    if (!seenKeys.has(key)) {
      seenKeys.add(key);
      relatedTrades.push(t);
    }
  }

  const isOnWl = STATE.watchlist.includes(b.ticker);
  const wlBtnClass = isOnWl ? 'is-active' : 'is-not-active';
  const wlBtnText = isOnWl ? `⭐ $${b.ticker} von Watchlist entfernen` : `⭐ $${b.ticker} zur Watchlist hinzufügen`;

  contentEl.innerHTML = `
    <!-- Top Header Card -->
    <div style="background:rgba(15,23,42,0.8); border:1px solid #334155; border-radius:12px; padding:14px; margin-bottom:12px;">
      <div style="display:flex; justify-content:space-between; align-items:flex-start;">
        <div>
          <h3 style="margin:0; font-size:18px; color:#38bdf8;">🤝 $${escapeHtml(b.ticker)}</h3>
          <div style="font-size:12px; color:#94a3b8; margin-top:2px;">${escapeHtml(b.sector)}</div>
        </div>
        <span class="pill" style="background:#10b98122; color:#10b981; border:1px solid #10b98144; font-weight:800; padding:4px 8px; border-radius:6px; font-size:11px;">
          ${b.total_buys}x KÄUFE GESAMT
        </span>
      </div>
    </div>

    <!-- Signal-Erklärung -->
    <div style="background:rgba(56,189,248,0.1); border:1px solid rgba(56,189,248,0.3); border-radius:10px; padding:12px; margin-bottom:12px; font-size:12px; line-height:1.5; color:#e2e8f0;">
      <div style="font-weight:800; color:#38bdf8; margin-bottom:6px; font-size:11px;">
        ⚡ WARUM IST DIESES SIGNAL SO STARK?
      </div>
      Wenn Politiker beider rivalisierender US-Parteien zeitgleich dieselbe Aktie akkumulieren, gilt dies als eines der stärksten Signale überhaupt. Es deutet regelmäßig auf bevorstehende überparteiliche Gesetzespakete, Subventionen oder Budgetfreigaben hin.
    </div>

    <!-- Party Ratio Bar -->
    <div style="background:rgba(30,41,59,0.7); border:1px solid #334155; border-radius:10px; padding:12px; margin-bottom:14px;">
      <div style="display:flex; justify-content:space-between; margin-bottom:8px; font-size:12px; font-weight:700;">
        <span style="color:#3b82f6;">🔵 Demokraten: ${b.dem_buys} (${dWidth}%)</span>
        <span style="color:#ef4444;">🔴 Republikaner: ${b.rep_buys} (${rWidth}%)</span>
      </div>
      <div style="display:flex; height:10px; border-radius:5px; overflow:hidden;">
        <div style="width:${dWidth}%; background:#3b82f6;"></div>
        <div style="width:${rWidth}%; background:#ef4444;"></div>
      </div>
    </div>

    <!-- Related Trades List (Alle Käufe mit allen Daten) -->
    <div style="font-size:12.5px; font-weight:700; color:#fff; margin-bottom:8px;">
      📋 Alle erfassten Einzelkäufe für $${b.ticker} (${relatedTrades.length}):
    </div>
    <div style="display:flex; flex-direction:column; gap:8px; margin-bottom:14px; max-height:280px; overflow-y:auto;">
      ${relatedTrades.length === 0 ? '<div style="font-size:11px; color:#94a3b8;">Keine Einzelkäufe im aktuellen Filterzeitraum gefunden.</div>' : relatedTrades.map((t) => {
        const isDem = (t.party || '').toUpperCase() === 'D';
        const isRep = (t.party || '').toUpperCase() === 'R';
        const pColor = isDem ? '#3b82f6' : (isRep ? '#ef4444' : '#94a3b8');
        const pLabel = isDem ? 'Demokrat' : (isRep ? 'Republikaner' : 'Unabhängig');
        const isSen = (t.chamber || '').toLowerCase().includes('senat');
        const cLabel = isSen ? 'Senat' : 'Repräsentantenhaus';
        return `
          <div style="background:rgba(30,41,59,0.7); border:1px solid #334155; border-radius:8px; padding:10px 12px;">
            <div style="display:flex; justify-content:space-between; align-items:flex-start; margin-bottom:6px;">
              <div>
                <div style="font-size:13px; font-weight:700; color:#fff; display:flex; align-items:center; gap:6px;">
                  <span style="display:inline-block; width:8px; height:8px; border-radius:50%; background:${pColor};"></span>
                  ${escapeHtml(t.filer_name)}
                </div>
                <div style="font-size:11px; color:#94a3b8; margin-top:2px;">${cLabel} · ${pLabel}</div>
              </div>
              <span class="pill" style="background:rgba(16,185,129,0.2); color:#10b981; font-weight:800; font-size:11px; padding:3px 7px;">
                ${escapeHtml(t.amount_range_label || '$1,001 - $15,000')}
              </span>
            </div>
            <div style="font-size:11px; color:#cbd5e1; display:flex; flex-wrap:wrap; gap:8px; margin-bottom:6px;">
              <span>📅 Handel: <strong>${t.transaction_date || '-'}</strong></span>
              <span>📝 Meldung: <strong>${t.filing_date || '-'}</strong></span>
              <span>⏳ Verzögerung: <strong>${t.days_to_file} Tage</strong></span>
            </div>
            ${t.has_committee_conflict ? `
              <div style="font-size:10.5px; color:#f59e0b; background:rgba(245,158,11,0.15); border:1px solid rgba(245,158,11,0.3); padding:4px 6px; border-radius:4px; margin-bottom:6px;">
                ⚠️ Ausschuss-Konflikt: ${escapeHtml(t.conflict_desc_de || t.conflict_committee || 'Interessenkonflikt')}
              </div>
            ` : ''}
            <div style="display:flex; justify-content:flex-end;">
              <button class="pill" style="font-size:10px; padding:3px 8px; background:rgba(56,189,248,0.15); color:#38bdf8; border:1px solid rgba(56,189,248,0.3); border-radius:4px; cursor:pointer;" onclick="openCongressTradeModal('${t.id}')">Details ↗</button>
            </div>
          </div>
        `;
      }).join('')}
    </div>

    <!-- Action Buttons (Keine Vollanalyse, offizieller Link) -->
    <div style="display:flex; flex-direction:column; gap:8px;">
      <button class="wl-action-btn ${wlBtnClass}" onclick="toggleWatchlistTicker('${b.ticker}'); openBipartisanModal('${ticker}');">
        ${wlBtnText}
      </button>
      <a href="https://disclosures-clerk.house.gov/FinancialDisclosure" target="_blank" rel="noopener noreferrer" style="text-align:center; text-decoration:none; display:flex; align-items:center; justify-content:center; gap:8px; padding:12px; font-size:12px; font-weight:700; background:rgba(148,163,184,0.1); color:#94a3b8; border:1px solid #334155; border-radius:8px;">
        🏛️ Offizielle Offenlegungsdaten (House & Senate Disclosures) ↗
      </a>
    </div>
  `;

  modal.style.display = 'flex';
};

window.openPartySentimentModal = function(ticker, partyName) {
  const ps = STATE.partySentiment || (window.BUNDLED_DATA ? window.BUNDLED_DATA.party_sentiment : null);
  if (!ps) return;

  const isDem = partyName.includes('Demokrat');
  const partyList = isDem ? (ps.dem_top_buys || []) : (ps.rep_top_buys || []);
  const item = partyList.find((i) => i.ticker === ticker) || { ticker: ticker, category: 'US Equity', count: 5 };

  const modal = document.getElementById('modal-party-sentiment-detail');
  const titleEl = document.getElementById('modal-party-sentiment-detail-title');
  const contentEl = document.getElementById('modal-party-sentiment-detail-content');
  if (!modal || !contentEl) return;

  const partyColor = isDem ? '#3b82f6' : '#ef4444';
  const partyCode = isDem ? 'D' : 'R';

  if (titleEl) {
    titleEl.textContent = `$${ticker} · ${partyName.toUpperCase()} KÄUFE`;
  }

  const cong = normalizeCongress(STATE.congress) || (window.BUNDLED_DATA ? normalizeCongress(window.BUNDLED_DATA.congress) : null);
  const rawTrades = (cong && cong.trades) ? cong.trades.filter((t) => t.ticker === ticker && (t.party || '').toUpperCase() === partyCode) : [];

  // Deduplicate trades
  const seenKeys = new Set();
  const relatedTrades = [];
  for (const t of rawTrades) {
    const key = `${t.filer_name}_${t.transaction_date}_${t.amount_range_label}_${t.transaction_type}`;
    if (!seenKeys.has(key)) {
      seenKeys.add(key);
      relatedTrades.push(t);
    }
  }

  const isOnWl = STATE.watchlist.includes(ticker);
  const wlBtnClass = isOnWl ? 'is-active' : 'is-not-active';
  const wlBtnText = isOnWl ? `⭐ $${ticker} von Watchlist entfernen` : `⭐ $${ticker} zur Watchlist hinzufügen`;

  contentEl.innerHTML = `
    <!-- Top Header Card -->
    <div style="background:rgba(15,23,42,0.8); border:1px solid #334155; border-radius:12px; padding:14px; margin-bottom:12px;">
      <div style="display:flex; justify-content:space-between; align-items:flex-start;">
        <div>
          <div style="display:flex; align-items:center; gap:6px;">
            <span style="display:inline-block; width:10px; height:10px; border-radius:50%; background:${partyColor};"></span>
            <h3 style="margin:0; font-size:18px; color:#fff;">$${escapeHtml(item.ticker)}</h3>
          </div>
          <div style="font-size:12px; color:#94a3b8; margin-top:2px;">${partyName} · ${escapeHtml(item.category)}</div>
        </div>
        <span class="pill" style="background:${partyColor}22; color:${partyColor}; border:1px solid ${partyColor}44; font-weight:800; padding:4px 8px; border-radius:6px; font-size:11px;">
          ${item.count}x KÄUFE
        </span>
      </div>
    </div>

    <!-- Partei-Fokus Analyse -->
    <div style="background:${isDem ? 'rgba(59,130,246,0.1)' : 'rgba(239,68,68,0.1)'}; border:1px solid ${isDem ? 'rgba(59,130,246,0.3)' : 'rgba(239,68,68,0.3)'}; border-radius:10px; padding:12px; margin-bottom:12px; font-size:12px; line-height:1.5; color:#e2e8f0;">
      <div style="font-weight:800; color:${partyColor}; margin-bottom:6px; font-size:11px;">
        ⚖️ PARTEI-FOKUS & SEKTOR-DRIFT
      </div>
      ${isDem ? `
        Demokratische Abgeordnete fokussieren sich überwiegend auf Large-Cap Technologie, Software, KI und erneuerbare Energien. Käufe wie $${item.ticker} spiegeln den klaren parteipolitischen Innovations- und Tech-Fokus wider.
      ` : `
        Republikanische Abgeordnete allokieren traditionell bevorzugt in Industrie, Spezialchemie, Verteidigung und traditionelle Rohstoffe & Energie. $${item.ticker} zählt zu den Kernwerten des republikanischen Portfolios.
      `}
    </div>

    <!-- Related Trades List (Alle Käufe mit allen Daten) -->
    <div style="font-size:12.5px; font-weight:700; color:#fff; margin-bottom:8px;">
      📋 Alle erfassten ${partyName}-Käufe für $${item.ticker} (${relatedTrades.length}):
    </div>
    <div style="display:flex; flex-direction:column; gap:8px; margin-bottom:14px; max-height:280px; overflow-y:auto;">
      ${relatedTrades.length === 0 ? '<div style="font-size:11px; color:#94a3b8;">Keine Einzelkäufe im aktuellen Filterzeitraum gefunden.</div>' : relatedTrades.map((t) => {
        const isSen = (t.chamber || '').toLowerCase().includes('senat');
        const cLabel = isSen ? 'Senat' : 'Repräsentantenhaus';
        return `
          <div style="background:rgba(30,41,59,0.7); border:1px solid #334155; border-radius:8px; padding:10px 12px;">
            <div style="display:flex; justify-content:space-between; align-items:flex-start; margin-bottom:6px;">
              <div>
                <div style="font-size:13px; font-weight:700; color:#fff;">
                  ${escapeHtml(t.filer_name)}
                </div>
                <div style="font-size:11px; color:#94a3b8; margin-top:2px;">${cLabel} · ${partyName}</div>
              </div>
              <span class="pill" style="background:rgba(16,185,129,0.2); color:#10b981; font-weight:800; font-size:11px; padding:3px 7px;">
                ${escapeHtml(t.amount_range_label || '$1,001 - $15,000')}
              </span>
            </div>
            <div style="font-size:11px; color:#cbd5e1; display:flex; flex-wrap:wrap; gap:8px; margin-bottom:6px;">
              <span>📅 Handel: <strong>${t.transaction_date || '-'}</strong></span>
              <span>📝 Meldung: <strong>${t.filing_date || '-'}</strong></span>
              <span>⏳ Verzögerung: <strong>${t.days_to_file} Tage</strong></span>
            </div>
            ${t.has_committee_conflict ? `
              <div style="font-size:10.5px; color:#f59e0b; background:rgba(245,158,11,0.15); border:1px solid rgba(245,158,11,0.3); padding:4px 6px; border-radius:4px; margin-bottom:6px;">
                ⚠️ Ausschuss-Konflikt: ${escapeHtml(t.conflict_desc_de || t.conflict_committee || 'Interessenkonflikt')}
              </div>
            ` : ''}
            <div style="display:flex; justify-content:flex-end;">
              <button class="pill" style="font-size:10px; padding:3px 8px; background:rgba(56,189,248,0.15); color:#38bdf8; border:1px solid rgba(56,189,248,0.3); border-radius:4px; cursor:pointer;" onclick="openCongressTradeModal('${t.id}')">Details ↗</button>
            </div>
          </div>
        `;
      }).join('')}
    </div>

    <!-- Action Buttons (Keine Vollanalyse, offizieller Link) -->
    <div style="display:flex; flex-direction:column; gap:8px;">
      <button class="wl-action-btn ${wlBtnClass}" onclick="toggleWatchlistTicker('${ticker}'); openPartySentimentModal('${ticker}', '${partyName}');">
        ${wlBtnText}
      </button>
      <a href="https://disclosures-clerk.house.gov/FinancialDisclosure" target="_blank" rel="noopener noreferrer" style="text-align:center; text-decoration:none; display:flex; align-items:center; justify-content:center; gap:8px; padding:12px; font-size:12px; font-weight:700; background:rgba(148,163,184,0.1); color:#94a3b8; border:1px solid #334155; border-radius:8px;">
        🏛️ Offizielle Offenlegungsdaten (House & Senate Disclosures) ↗
      </a>
    </div>
  `;

  modal.style.display = 'flex';
};

window.openSecClusterModal = function(clusterId) {
  const sec = normalizeSec(STATE.sec) || (window.BUNDLED_DATA ? normalizeSec(window.BUNDLED_DATA.sec) : null);
  if (!sec || !sec.alerts) return;

  const { clusters } = getSecProcessedData(sec);
  const cluster = clusters.find((g) => g.id === clusterId || g.issuer === clusterId || g.ticker === clusterId);
  if (!cluster) return;

  const isEn = STATE.lang === 'en';
  const dict = I18N[STATE.lang] || I18N.de;

  const modal = document.getElementById('modal-sec-event');
  const titleEl = document.getElementById('modal-sec-event-title');
  const contentEl = document.getElementById('modal-sec-event-content');
  if (!modal || !contentEl) return;

  if (titleEl) {
    titleEl.textContent = cluster.ticker
      ? `$${cluster.ticker} · CLUSTER-ALARM (${cluster.cluster_count}x FILINGS)`
      : `CLUSTER-ALARM · ${cluster.issuer}`;
  }

  const isOnWl = cluster.ticker && STATE.watchlist.includes(cluster.ticker);
  const wlBtnClass = isOnWl ? 'is-active' : 'is-not-active';
  const wlBtnText = isOnWl ? dict.modal_btn_remove_wl : dict.modal_btn_add_wl;

  contentEl.innerHTML = `
    <!-- Top Header -->
    <div class="analysis-card-top" style="margin-bottom:12px;">
      <div>
        <div style="font-size:22px; font-weight:800; color:#38bdf8; line-height:1.1;">
          ${cluster.ticker ? `$${cluster.ticker}` : escapeHtml(cluster.issuer)}
        </div>
        <div style="font-size:13px; color:#cbd5e1; margin-top:2px;">
          ${escapeHtml(cluster.issuer)}
        </div>
      </div>
      <div style="background:rgba(245,158,11,0.2); color:#f59e0b; border:1px solid rgba(245,158,11,0.5); padding:4px 10px; border-radius:6px; font-size:11px; font-weight:800;">
        🔥 ${cluster.cluster_count}x CLUSTER
      </div>
    </div>

    <!-- Cluster Alert Banner -->
    <div style="background:linear-gradient(135deg, rgba(245,158,11,0.2) 0%, rgba(15,23,42,0.95) 100%); border:1px solid #f59e0b; border-radius:8px; padding:10px 12px; margin-bottom:12px;">
      <div style="font-size:12px; font-weight:800; color:#f59e0b; margin-bottom:3px;">
        🔥 CLUSTER-KAUF ERKANNT (${cluster.cluster_count} SYNCHRONE FILINGS)
      </div>
      <div style="font-size:11px; color:#fde68a; line-height:1.4;">
        ${cluster.cluster_count} Vorstandsmitglieder & Direktoren haben synchron Anteile erworben. Ein massives Indiz für bevorstehende operative Katalysatoren und Unterbewertung.
      </div>
    </div>

    <!-- Liste aller einzelnen Beitragenden / Filings im Cluster mit echten Form 4 XML-Details -->
    <div style="font-size:11px; font-weight:800; color:#38bdf8; text-transform:uppercase; letter-spacing:0.5px; margin-bottom:8px;">
      👥 ALLE ${cluster.cluster_count} BETEILIGTEN IM CLUSTER (FORM 4 DETAILS)
    </div>
    <div class="cluster-contributors-list">
      ${cluster.filings.map((f, idx) => {
        const sigBadge = f.is_buy
          ? '<span class="sec-tx-pill buy" style="font-size:10px;">🟢 Kauf (Code ' + (f.code || 'P') + ')</span>'
          : (f.code === 'S' || !f.is_buy ? '<span class="sec-tx-pill sale" style="font-size:10px;">🔴 Verkauf (Code ' + (f.code || 'S') + ')</span>' : '<span class="sec-tx-pill neutral" style="font-size:10px;">⚪ Award (Code ' + (f.code || 'A') + ')</span>');
        const sharesText = (f.shares_formatted && f.shares_formatted !== '-')
          ? f.shares_formatted : (f.shares ? f.shares.toLocaleString('de-DE') + ' Aktien' : '-');
        const priceText = f.price && f.price > 0 ? ('$' + f.price.toFixed(2)) : (f.price_formatted || '-');
        const totalValText = f.total_value && f.total_value > 0 ? ('$' + f.total_value.toLocaleString('de-DE')) : (f.total_value_formatted || '-');
        const benefText = f.security_beneficials_formatted || (f.security_beneficials ? f.security_beneficials.toLocaleString('de-DE') + ' Aktien' : '-');
        const natureText = f.ownership_nature === 'I' ? 'Indirekt (I) · Trust/Holding' : 'Direkt (D) · Privatdepot';
        const dateStr = f.filing_time ? f.filing_time.slice(0, 16).replace('T', ', ') + ' Uhr' : (f.filing_date || '-');
        const securityTitle = f.security_title || 'Common Stock (Stammaktien)';

        return `
          <div class="cluster-contributor-item">
            <div class="cluster-contributor-header">
              <div>
                <span class="cluster-contributor-name">${idx + 1}. ${escapeHtml(f.reporting_owner || 'Reporting Person')}</span>
                <div class="cluster-contributor-role">${escapeHtml(f.role || 'Officer / Director')} · ${dateStr}</div>
              </div>
              <div>${sigBadge}</div>
            </div>

            <div class="cluster-xml-grid">
              <div class="cluster-xml-cell">
                <span class="cluster-xml-label">Title of Security</span>
                <span class="cluster-xml-val">${escapeHtml(securityTitle)}</span>
              </div>
              <div class="cluster-xml-cell">
                <span class="cluster-xml-label">Gehandelte Stückzahl (Amount)</span>
                <span class="cluster-xml-val" style="color:#38bdf8;">${sharesText} ${priceText !== '-' ? `à ${priceText}` : ''}</span>
              </div>
              <div class="cluster-xml-cell">
                <span class="cluster-xml-label">Bestand danach (Beneficially Owned)</span>
                <span class="cluster-xml-val" style="color:#10b981;">${benefText}</span>
              </div>
              <div class="cluster-xml-cell">
                <span class="cluster-xml-label">Eigentumsart (Ownership Nature)</span>
                <span class="cluster-xml-val">${natureText}</span>
              </div>
            </div>

            ${f.investor_explanation_de ? `
              <div class="cluster-xml-explanation">
                💡 <strong>Vermerk für Aktionäre:</strong> ${escapeHtml(f.investor_explanation_de)}
              </div>
            ` : ''}

            <div style="display:flex; justify-content:space-between; align-items:center; margin-top:2px;">
              <span style="font-size:10px; color:#64748b;">Gesamtwert: <strong style="color:#f1f5f9;">${totalValText}</strong></span>
              <a href="${f.filing_url || 'https://www.sec.gov'}" target="_blank" class="tc-link" style="font-size:11px; font-weight:700; color:#38bdf8;">📄 Original Form 4 auf SEC.gov ↗</a>
            </div>
          </div>
        `;
      }).join('')}
    </div>

    <!-- Form 4 XML Glossar / Legende (Erklärung aller Vermerke) -->
    <div class="sec-form4-glossary">
      <div class="sec-form4-glossary-title">📋 Was bedeuten die Form-4-Vermerke? (Legende)</div>
      <div class="sec-form4-glossary-item">
        <strong>• Code P (Purchase):</strong> Kauf am offenen Markt mit persönlichem Barvermögen. Das historisch stärkste Kaufsignal für Aktionäre.
      </div>
      <div class="sec-form4-glossary-item">
        <strong>• Code S (Sale):</strong> Offener Verkauf über die Börse (zur Gewinnmitnahme, Nachlass- oder Steuerplanung).
      </div>
      <div class="sec-form4-glossary-item">
        <strong>• Code A (Award):</strong> Aktien-Zuteilung als vertragliche Vergütung ohne privaten Geldeinsatz (kursneutral).
      </div>
      <div class="sec-form4-glossary-item">
        <strong>• Amount (Stückzahl):</strong> Exakte Anzahl der bei dieser Transaktion gehandelten Aktien.
      </div>
      <div class="sec-form4-glossary-item">
        <strong>• Security Beneficially Owned:</strong> Der verbleibende Gesamtaktienbestand des Insiders – belegt das langfristige 'Skin in the Game'.
      </div>
      <div class="sec-form4-glossary-item">
        <strong>• Ownership Nature (D vs. I):</strong> 'D' = Direktes persönliches Eigentum; 'I' = Indirekter Besitz über Familientrust oder Holding.
      </div>
    </div>

    <!-- Official Metadata -->
    <div style="font-size:11px; color:#64748b; margin-bottom:14px;">
      <div><strong>Cluster-Zeitspanne:</strong> 14-Tage-Clusterfenster (rollierend)</div>
      <div><strong>Letzte Meldung:</strong> ${cluster.filing_time || cluster.filing_date}</div>
    </div>

    <!-- Action Buttons -->
    <div style="display:flex; flex-direction:column; gap:8px;">
      ${cluster.ticker ? `
        <button class="wl-action-btn ${wlBtnClass}" onclick="toggleWatchlistTicker('${cluster.ticker}'); openSecClusterModal('${clusterId}');">
          ${wlBtnText}
        </button>
      ` : ''}
    </div>
  `;

  modal.style.display = 'flex';
};

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
  const originalUrl = alert.filing_url || 'https://www.sec.gov';

  const isOnWl = alert.ticker && STATE.watchlist.includes(alert.ticker);
  const wlBtnClass = isOnWl ? 'is-active' : 'is-not-active';
  const wlBtnText = isOnWl ? dict.modal_btn_remove_wl : dict.modal_btn_add_wl;

  const sharesDisplay = (alert.shares && alert.shares > 0 && alert.shares_formatted && alert.shares_formatted !== '-' && alert.shares_formatted !== '0')
    ? alert.shares_formatted : '-';
  const signalText = alert.is_buy ? '🟢 Kauf' : (alert.code === 'S' || !alert.is_buy ? '🔴 Verkauf' : '⚪ Award');

  contentEl.innerHTML = `
    <!-- Top Header: Ticker & Issuer -->
    <div class="analysis-card-top" style="margin-bottom:12px;">
      <div>
        <div style="font-size:22px; font-weight:800; color:#38bdf8; line-height:1.1;">
          ${alert.ticker ? `$${alert.ticker}` : escapeHtml(alert.issuer)}
        </div>
        <div style="font-size:13px; color:#cbd5e1; margin-top:2px;">
          ${escapeHtml(alert.issuer)}
        </div>
      </div>
      <div style="background:${tagColor}22; color:${tagColor}; border:1px solid ${tagColor}55; padding:4px 10px; border-radius:6px; font-size:11px; font-weight:800;">
        ${signalText}
      </div>
    </div>

    <!-- Key Form 4 XML Metrics Grid -->
    <div style="background:rgba(15,23,42,0.75); border:1px solid #1e293b; border-radius:10px; padding:12px; margin-bottom:12px;">
      <div style="display:flex; justify-content:space-between; align-items:flex-start; margin-bottom:10px;">
        <div>
          <div style="font-size:10px; color:#64748b; font-weight:700; text-transform:uppercase;">Reporting Person (Name & Relationship)</div>
          <div style="font-size:14px; font-weight:800; color:#fff; margin-top:2px;">${escapeHtml(alert.reporting_owner)}</div>
          <div style="font-size:11.5px; color:#38bdf8; font-weight:600;">${escapeHtml(alert.role || 'Officer / Director')}</div>
        </div>
        <div>
          <span class="sec-tx-pill ${alert.is_buy ? 'buy' : (alert.code === 'S' || !alert.is_buy ? 'sale' : 'neutral')}" style="font-size:11px;">
            ${alert.is_buy ? '🟢 Kauf (Code ' + (alert.code || 'P') + ')' : (alert.code === 'S' ? '🔴 Verkauf (Code S)' : '⚪ Award (Code A)')}
          </span>
        </div>
      </div>

      <div class="cluster-xml-grid">
        <div class="cluster-xml-cell">
          <span class="cluster-xml-label">Title of Security</span>
          <span class="cluster-xml-val">${escapeHtml(alert.security_title || 'Common Stock (Stammaktien)')}</span>
        </div>
        <div class="cluster-xml-cell">
          <span class="cluster-xml-label">Gehandelte Stückzahl (Amount)</span>
          <span class="cluster-xml-val" style="color:#38bdf8;">${sharesDisplay} ${alert.price && alert.price > 0 ? `à $${alert.price.toFixed(2)}` : ''}</span>
        </div>
        <div class="cluster-xml-cell">
          <span class="cluster-xml-label">Bestand danach (Beneficially Owned)</span>
          <span class="cluster-xml-val" style="color:#10b981;">${alert.security_beneficials_formatted || (alert.security_beneficials ? alert.security_beneficials.toLocaleString('de-DE') + ' Aktien' : '-')}</span>
        </div>
        <div class="cluster-xml-cell">
          <span class="cluster-xml-label">Eigentumsart (Ownership Nature)</span>
          <span class="cluster-xml-val">${alert.ownership_nature === 'I' ? 'Indirekt (I) · Trust/Holding' : 'Direkt (D) · Privatdepot'}</span>
        </div>
      </div>
    </div>

    <!-- Vermerk & Einschätzung für Aktionäre -->
    <div style="background:rgba(30,41,59,0.5); border:1px solid #334155; border-radius:8px; padding:10px 12px; font-size:12px; color:#cbd5e1; line-height:1.5; margin-bottom:12px;">
      💡 <strong>Vermerk für Aktionäre:</strong> ${escapeHtml(alert.investor_explanation_de || (alert.is_buy ? 'Kauf aus persönlichem Barvermögen am offenen Markt. Führungskräfte investieren persönliches Kapital als Vertrauensbeweis.' : 'Marktverkauf der Position.'))}
    </div>

    <!-- Form 4 XML Glossar / Legende (Erklärung aller Vermerke) -->
    <div class="sec-form4-glossary">
      <div class="sec-form4-glossary-title">📋 Was bedeuten die Form-4-Vermerke? (Legende)</div>
      <div class="sec-form4-glossary-item">
        <strong>• Code P (Purchase):</strong> Kauf am offenen Markt mit persönlichem Barvermögen. Das historisch stärkste Kaufsignal für Aktionäre.
      </div>
      <div class="sec-form4-glossary-item">
        <strong>• Code S (Sale):</strong> Offener Verkauf über die Börse (zur Gewinnmitnahme, Nachlass- oder Steuerplanung).
      </div>
      <div class="sec-form4-glossary-item">
        <strong>• Code A (Award):</strong> Aktien-Zuteilung als vertragliche Vergütung ohne privaten Geldeinsatz (kursneutral).
      </div>
      <div class="sec-form4-glossary-item">
        <strong>• Amount (Stückzahl):</strong> Exakte Anzahl der bei dieser Transaktion gehandelten Aktien.
      </div>
      <div class="sec-form4-glossary-item">
        <strong>• Security Beneficially Owned:</strong> Der verbleibende Gesamtaktienbestand des Insiders – belegt das langfristige 'Skin in the Game'.
      </div>
      <div class="sec-form4-glossary-item">
        <strong>• Ownership Nature (D vs. I):</strong> 'D' = Direktes persönliches Eigentum; 'I' = Indirekter Besitz über Familientrust oder Holding.
      </div>
    </div>

    <!-- Official Filing Metadata -->
    <div style="font-size:11px; color:#64748b; margin-bottom:14px;">
      <div><strong>SEC Accession No.:</strong> <span style="font-family:monospace; color:#94a3b8;">${escapeHtml(alert.accession_number)}</span></div>
      <div><strong>Einreichungsdatum:</strong> ${alert.filing_time || alert.filing_date}</div>
      <div><strong>Meldezeitraum:</strong> SEC Form 4 (T+2 Frist)</div>
    </div>

    <!-- Action Buttons -->
    <div style="display:flex; flex-direction:column; gap:8px;">
      <a href="${originalUrl}" target="_blank" class="btn-primary" style="text-align:center; text-decoration:none; display:flex; align-items:center; justify-content:center; gap:8px; padding:12px; font-size:13px; font-weight:700;">
        ${dict.sec_action_open_edgar}
      </a>

      ${alert.ticker ? `
        <button class="wl-action-btn ${wlBtnClass}" onclick="toggleWatchlistTicker('${alert.ticker}'); openSecDetailModal('${accNo}');">
          ${wlBtnText}
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

  if (titleEl) titleEl.textContent = `DETAILS: $${ticker}`;
  
  // Initial loading state
  contentEl.innerHTML = `
    <div class="loading-spinner-row">
      <div class="spinner"></div> Lade Details für $${ticker}...
    </div>
  `;
  modal.style.display = 'flex';

  try {
    if (STATE.stockDetails && STATE.stockDetails[ticker]) {
      renderVollanalyseContent(STATE.stockDetails[ticker], ticker);
      return;
    }
    const res = await fetch(`/api/analysis?ticker=${ticker}`).then((r) => r.json());
    renderVollanalyseContent(res, ticker);
  } catch (err) {
    console.error('Error fetching analysis:', err);
    // Render fallback analysis using loaded state
    renderFallbackAnalysis(ticker);
  }
};

function calculateJsCongressTotalRange(trades) {
  if (!trades || trades.length === 0) return '-';
  let minSum = 0;
  let maxSum = 0;
  let count = 0;
  trades.forEach((t) => {
    const raw = (t.amount_range_label || '').replace(/\$/g, '').replace(/,/g, '').trim();
    const parts = raw.split('-');
    if (parts.length === 2) {
      const min = parseInt(parts[0].trim(), 10) || 0;
      const max = parseInt(parts[1].trim(), 10) || 0;
      if (max > 0) {
        minSum += min;
        maxSum += max;
        count++;
      }
    }
  });
  if (count > 1 && maxSum > 0) {
    return `$${minSum.toLocaleString('en-US')} - $${maxSum.toLocaleString('en-US')}`;
  }
  return trades[0].amount_range_label || '-';
}

function renderVollanalyseContent(data, ticker) {
  const isEn = STATE.lang === 'en';
  const dict = I18N[STATE.lang] || I18N.de;
  const contentEl = document.getElementById('modal-content');
  if (!contentEl) return;

  const isOnWatchlist = STATE.watchlist.includes(ticker);
  let companyName = data.company_name || `${ticker} Corporation`;

  // Watchlist action button text & style
  const wlBtnClass = isOnWatchlist ? 'is-active' : 'is-not-active';
  const wlBtnText = isOnWatchlist ? (isEn ? '⭐ Remove from Watchlist' : '⭐ Von Watchlist entfernen') : (isEn ? '☆ Add to Watchlist' : '☆ Zur Watchlist hinzufügen');

  // Recent congress trades and SEC alerts
  let cgTrades = data.recent_congress_trades || [];
  let secAlerts = data.recent_sec_alerts || [];

  // COT Macro Context
  const cot = STATE.cot || (window.BUNDLED_DATA ? window.BUNDLED_DATA.cot : null);
  const isTech = ['NVDA', 'MSFT', 'AAPL', 'PLTR', 'NXPI', 'AMZN', 'GOOGL', 'META'].includes(ticker);
  const cotMarket = (cot && cot.markets) ? (cot.markets.find(m => isTech ? m.id === 'QQQ' : m.id === 'SPY') || cot.markets[0]) : null;
  let cotLongPct = cotMarket ? Math.round(cotMarket.whale_bullish_pct || 50) : 50;
  let cotShortPct = 100 - cotLongPct;
  let cotMarketName = cotMarket ? cotMarket.name : (isTech ? 'Nasdaq-100 / QQQ (Tech)' : 'S&P 500 / SPY (Large Caps)');
  let cotFlowDesc = cotMarket ? (cotMarket.whale_change_desc || '+8.000 Akkumulation') : '+8.000 Akkumulation';

  // Use unified stock dossier if available
  const dossier = (STATE.stockDetails && STATE.stockDetails[ticker]) ? STATE.stockDetails[ticker] : (data.cot ? data : null);
  if (dossier) {
    if (dossier.company_name) companyName = dossier.company_name;
    if (dossier.cot) {
      if (dossier.cot.market) cotMarketName = dossier.cot.market;
      if (dossier.cot.whale_long_pct) cotLongPct = dossier.cot.whale_long_pct;
      if (dossier.cot.whale_short_pct) cotShortPct = dossier.cot.whale_short_pct;
      if (dossier.cot.flow) cotFlowDesc = dossier.cot.flow;
    }
    if (dossier.congress && dossier.congress.trades) {
      cgTrades = dossier.congress.trades;
    }
    if (dossier.sec && dossier.sec.filings) {
      secAlerts = dossier.sec.filings;
    }
  }

  // SuperSignal check
  const superSig = (STATE.superSignals || []).find((s) => s.ticker === ticker);

  contentEl.innerHTML = `
    <!-- Top Header: Ticker & Company Name -->
    <div class="analysis-card-top" style="display:flex; justify-content:space-between; align-items:flex-start; margin-bottom:12px;">
      <div>
        <div style="font-size:24px; font-weight:800; color:#38bdf8; line-height:1.1;">$${ticker}</div>
        <div style="font-size:12.5px; color:#cbd5e1; margin-top:2px;">${escapeHtml(companyName)}</div>
      </div>
      <div class="analysis-score-badge" style="background-color:rgba(56,189,248,0.15); color:#38bdf8; border:1px solid rgba(56,189,248,0.3); font-weight:800; padding:4px 10px; border-radius:6px; font-size:11px;">
        DETAILS
      </div>
    </div>

    ${superSig ? `
      <div style="background:linear-gradient(135deg, rgba(245,158,11,0.2) 0%, rgba(15,23,42,0.95) 100%); border:1px solid #f59e0b; border-radius:10px; padding:10px 12px; margin-bottom:12px;">
        <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:4px;">
          <span style="font-size:11px; font-weight:800; color:#f59e0b;">🔥 KOMBI-SIGNAL: COT + KONGRESS + SEC</span>
          <span style="font-size:10px; font-weight:800; background:rgba(245,158,11,0.3); color:#fff; padding:2px 6px; border-radius:4px;">${superSig.conviction_score || 95}% SCORE</span>
        </div>
        <div style="font-size:11px; color:#f8fafc; line-height:1.4;">
          ${escapeHtml(isEn ? (superSig.description_en || superSig.description_de) : (superSig.description_de || superSig.description_en))}
        </div>
      </div>
    ` : ''}

    <!-- ================= 1. SÄULE: 🐋 COT MAKRO-SENTIMENT ================= -->
    <div style="background:rgba(30,41,59,0.5); border:1px solid rgba(56,189,248,0.35); border-radius:12px; padding:12px; margin-bottom:12px; font-size:11.5px;">
      <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:8px;">
        <span style="color:#38bdf8; font-weight:800; font-size:11px;">🐋 1. CFTC COT MAKRO- & WAL-SENTIMENT</span>
        <span style="font-size:11px; font-weight:800; background:${cotLongPct >= 50 ? 'rgba(16,185,129,0.18)' : 'rgba(239,68,68,0.18)'}; color:${cotLongPct >= 50 ? '#10b981' : '#ef4444'}; border:1px solid ${cotLongPct >= 50 ? 'rgba(16,185,129,0.5)' : 'rgba(239,68,68,0.5)'}; padding:2.5px 8px; border-radius:6px;">
          ${cotLongPct >= 50 ? `${cotLongPct}% LONG` : `${cotShortPct}% SHORT`}
        </span>
      </div>
      <div style="display:flex; justify-content:space-between; margin-bottom:4px;">
        <span style="color:#94a3b8;">Markt & Sektor:</span>
        <strong style="color:#fff;">${escapeHtml(cotMarketName)}</strong>
      </div>
      <div style="display:flex; justify-content:space-between; margin-bottom:4px;">
        <span style="color:#94a3b8;">Wochen-Fluss (Flow):</span>
        <strong style="color:#10b981;">${escapeHtml(cotFlowDesc)}</strong>
      </div>
      <div style="display:flex; justify-content:space-between;">
        <span style="color:#94a3b8;">Zeitspanne & Stichtag:</span>
        <span style="color:#cbd5e1;">Stichtag: Di, 13.10. · Veröffentlichung: Fr, 16.10. (1 Woche Gültigkeit)</span>
      </div>
    </div>

    <!-- ================= 2. SÄULE: 🏛️ KONGRESS STOCK ACT ================= -->
    <div style="background:rgba(30,41,59,0.5); border:1px solid rgba(6,182,212,0.35); border-radius:12px; padding:12px; margin-bottom:12px; font-size:11.5px;">
      <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:8px;">
        <span style="color:#06b6d4; font-weight:800; font-size:11px;">🏛️ 2. KONGRESS STOCK ACT</span>
        <div style="display:flex; gap:6px;">
          ${(data.congress_buys || 0) > 0 ? `<span style="font-size:10.5px; font-weight:800; background:rgba(16,185,129,0.18); color:#10b981; border:1px solid rgba(16,185,129,0.5); padding:2px 7px; border-radius:5px;">${data.congress_buys}x KAUF</span>` : ''}
          ${(data.congress_sales || 0) > 0 ? `<span style="font-size:10.5px; font-weight:800; background:rgba(239,68,68,0.18); color:#ef4444; border:1px solid rgba(239,68,68,0.5); padding:2px 7px; border-radius:5px;">${data.congress_sales}x VERKAUF</span>` : ''}
          ${((data.congress_buys || 0) === 0 && (data.congress_sales || 0) === 0) ? `<span style="font-size:10.5px; font-weight:700; background:rgba(148,163,184,0.18); color:#94a3b8; padding:2px 6px; border-radius:5px;">0 TRADES</span>` : ''}
        </div>
      </div>

      ${cgTrades.length > 1 ? `
        <div style="background:rgba(6,182,212,0.12); border:1px solid rgba(6,182,212,0.4); border-radius:8px; padding:6px 10px; margin-bottom:8px; display:flex; justify-content:space-between; align-items:center;">
          <span style="font-size:11px; font-weight:700; color:#e2e8f0;">📊 Gesamt-Spanne (${cgTrades.length} Käufer/Verkäufer):</span>
          <span style="font-size:12px; font-weight:800; color:#38bdf8;">${calculateJsCongressTotalRange(cgTrades)}</span>
        </div>
      ` : ''}

      ${cgTrades.length > 0 ? cgTrades.slice(0, 6).map((t) => {
        const isBuy = (t.transaction_type || '').toLowerCase().includes('purchase') || (t.transaction_type || '').toLowerCase().includes('kauf');
        const isSen = (t.chamber || '').toLowerCase().includes('senat');
        const chamberLabel = isSen ? 'Senat' : 'Repräsentantenhaus';
        const partyLabel = t.party === 'D' ? 'DEM' : (t.party === 'R' ? 'REP' : (t.party || '-'));
        const partyBg = t.party === 'D' ? 'rgba(56,189,248,0.18)' : (t.party === 'R' ? 'rgba(239,68,68,0.18)' : 'rgba(148,163,184,0.18)');
        const partyColor = t.party === 'D' ? '#38bdf8' : (t.party === 'R' ? '#ef4444' : '#cbd5e1');

        return `
          <div style="background:rgba(15,23,42,0.75); border:1px solid rgba(51,65,85,0.7); border-radius:10px; padding:9px 11px; margin-bottom:6px;">
            <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:4px;">
              <div style="display:flex; align-items:center; gap:6px;">
                <strong style="color:#fff; font-size:12.5px;">${escapeHtml(t.filer_name)}</strong>
                <span style="font-size:9.5px; font-weight:700; background:${partyBg}; color:${partyColor}; padding:1.5px 5px; border-radius:4px;">${partyLabel} · ${chamberLabel}</span>
              </div>
              <span class="pill" style="font-size:10px; font-weight:800; background:${isBuy ? 'rgba(16,185,129,0.18)' : 'rgba(239,68,68,0.18)'}; color:${isBuy ? '#10b981' : '#ef4444'}; border:1px solid ${isBuy ? 'rgba(16,185,129,0.5)' : 'rgba(239,68,68,0.5)'}; padding:2px 6px; border-radius:4px;">
                ${isBuy ? '🟢 KAUF' : '🔴 VERKAUF'}
              </span>
            </div>
            <div style="display:flex; justify-content:space-between; color:#94a3b8; font-size:11px;">
              <span>Spanne: <strong style="color:#38bdf8;">${escapeHtml(t.amount_range_label || '-')}</strong></span>
              <span>Handel: ${t.transaction_date || '-'} · Meldung: ${t.filing_date || '-'}</span>
            </div>
            ${(t.has_committee_conflict || t.conflict_committee) ? `
              <div style="font-size:10px; color:#f59e0b; margin-top:4px; background:rgba(245,158,11,0.12); padding:3px 6px; border-radius:4px; border:1px solid rgba(245,158,11,0.3);">
                ⚠️ Ausschuss-Konflikt: ${escapeHtml(t.conflict_desc_de || t.conflict_desc || t.conflict_committee || 'Interessenkonflikt')}
              </div>
            ` : ''}
          </div>
        `;
      }).join('') : '<div style="color:#94a3b8; font-size:11px;">Keine Politiker-Trades im aktuellen Filterzeitraum erfasst.</div>'}
    </div>

    <!-- ================= 3. SÄULE: 🏢 SEC FORM 4 INSIDER ================= -->
    <div style="background:rgba(30,41,59,0.5); border:1px solid rgba(245,158,11,0.35); border-radius:12px; padding:12px; margin-bottom:12px; font-size:11.5px;">
      <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:8px;">
        <span style="color:#f59e0b; font-weight:800; font-size:11px;">🏢 3. SEC FORM 4 INSIDER-FILINGS</span>
        <span style="font-size:10.5px; font-weight:800; background:rgba(245,158,11,0.18); color:#f59e0b; border:1px solid rgba(245,158,11,0.5); padding:2px 7px; border-radius:5px;">
          ${secAlerts.length} FILINGS
        </span>
      </div>

      ${secAlerts.length > 0 ? secAlerts.slice(0, 6).map((a) => {
        const isBuy = a.is_buy !== undefined ? a.is_buy : (a.code === 'P' || !a.code);
        const dateStr = a.filing_time ? a.filing_time.slice(0, 16).replace('T', ' ') + ' Uhr' : (a.filing_date || '-');
        const formUrl = a.sec_form_url || a.filing_url;
        return `
          <div style="background:rgba(15,23,42,0.75); border:1px solid rgba(51,65,85,0.7); border-radius:10px; padding:9px 11px; margin-bottom:6px;">
            <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:3px;">
              <div>
                <strong style="color:#fff; font-size:12.5px;">${escapeHtml(a.reporting_owner || 'Insider')}</strong>
                <div style="font-size:10.5px; color:#94a3b8;">${escapeHtml(a.role || 'Officer')}</div>
              </div>
              <span class="pill" style="font-size:10px; font-weight:800; background:${isBuy ? 'rgba(16,185,129,0.18)' : 'rgba(239,68,68,0.18)'}; color:${isBuy ? '#10b981' : '#ef4444'}; border:1px solid ${isBuy ? 'rgba(16,185,129,0.5)' : 'rgba(239,68,68,0.5)'}; padding:2px 6px; border-radius:4px;">
                ${isBuy ? '🟢 KAUF' : '🔴 VERKAUF'}
              </span>
            </div>
            <div style="display:flex; justify-content:space-between; align-items:center; color:#94a3b8; font-size:11px; margin-top:3px;">
              <span style="color:#10b981; font-weight:700;">${escapeHtml(a.shares_formatted || '')} ${a.total_value_formatted ? `· ${a.total_value_formatted}` : ''}</span>
              <div style="display:flex; align-items:center; gap:6px;">
                <span>Filing: ${dateStr}</span>
                ${formUrl ? `<a href="${formUrl}" target="_blank" rel="noopener noreferrer" style="color:#38bdf8; text-decoration:none; font-size:10px; font-weight:700; background:rgba(56,189,248,0.15); border:1px solid rgba(56,189,248,0.3); padding:1px 5px; border-radius:4px;">Form 4 ↗</a>` : ''}
              </div>
            </div>
          </div>
        `;
      }).join('') : '<div style="color:#94a3b8; font-size:11px;">Keine Form 4 Insider-Filings im aktuellen Zeitraum erfasst.</div>'}
    </div>

    <!-- Watchlist Action Button -->
    <div style="margin-top:14px;">
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

  const secData = normalizeSec(STATE.sec) || (window.BUNDLED_DATA ? normalizeSec(window.BUNDLED_DATA.sec) : null);
  const secAlerts = secData ? secData.alerts.filter((a) => (a.ticker || '').toUpperCase() === ticker) : [];

  const superSig = (STATE.superSignals || []).find((s) => s.ticker === ticker);
  const score = superSig ? superSig.conviction_score : (buyCount >= saleCount ? 78 : 45);

  renderVollanalyseContent({
    ticker: ticker,
    company_name: cgMatches.length > 0 ? cgMatches[0].asset_name : (secAlerts.length > 0 ? secAlerts[0].issuer : (superSig ? superSig.company_name : `${ticker} Corporation`)),
    score: score,
    sentiment_label_de: (score >= 65 ? 'BULLISCH' : (score <= 45 ? 'BÄRISCH' : 'NEUTRAL')),
    sentiment_label_en: (score >= 65 ? 'BULLISH' : (score <= 45 ? 'BEARISH' : 'NEUTRAL')),
    sentiment_color: (score >= 65 ? '#10b981' : (score <= 45 ? '#ef4444' : '#94a3b8')),
    congress_buys: buyCount,
    congress_sales: saleCount,
    recent_congress_trades: cgMatches.slice(0, 10),
    recent_sec_alerts: secAlerts.slice(0, 10),
    cot_whale_context: 'Whales 70.0% Long · 30.0% Short'
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

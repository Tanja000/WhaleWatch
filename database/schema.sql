-- ====================================================================
-- WhaleWatch Pro: Production Relational Database Schema (SQLite / PostgreSQL)
-- 3 Core Base Tables: insider_trades, congress_trades, cluster_alerts
-- ====================================================================

-- 1. SEC Form 4 Insider Trades
CREATE TABLE IF NOT EXISTS insider_trades (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    accession_number VARCHAR(64) UNIQUE,
    ticker VARCHAR(12) NOT NULL,
    company_name VARCHAR(255),
    reporting_owner VARCHAR(255) NOT NULL,
    owner_cik VARCHAR(32),
    role VARCHAR(128) NOT NULL, -- e.g. 'CEO', 'CFO', 'Director', '10% Owner'
    transaction_code VARCHAR(4) NOT NULL, -- 'P' (Purchase), 'S' (Sale), 'A', 'M'
    transaction_code_desc VARCHAR(64),
    is_buy BOOLEAN NOT NULL DEFAULT 1,
    shares NUMERIC(18, 4) NOT NULL DEFAULT 0,
    price NUMERIC(18, 4) NOT NULL DEFAULT 0,
    total_value NUMERIC(18, 2) NOT NULL DEFAULT 0,
    security_title VARCHAR(128),
    filing_date DATE NOT NULL,
    filing_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    filing_url TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_insider_ticker ON insider_trades(ticker);
CREATE INDEX IF NOT EXISTS idx_insider_filing_date ON insider_trades(filing_date);
CREATE INDEX IF NOT EXISTS idx_insider_code ON insider_trades(transaction_code);

-- 2. US Congress STOCK Act Trades
CREATE TABLE IF NOT EXISTS congress_trades (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    source_filing_id VARCHAR(64),
    politician VARCHAR(255) NOT NULL,
    party VARCHAR(4) NOT NULL, -- 'D', 'R', 'I'
    chamber VARCHAR(32) NOT NULL, -- 'House', 'Senate'
    state VARCHAR(8),
    ticker VARCHAR(12) NOT NULL,
    asset_name VARCHAR(255),
    transaction_type VARCHAR(32) NOT NULL, -- 'Purchase', 'Sale (Full)', 'Sale (Partial)'
    amount_range_label VARCHAR(64) NOT NULL, -- '$1,001 - $15,000', '$100,001 - $250,000'
    amount_min NUMERIC(18, 2),
    amount_max NUMERIC(18, 2),
    transaction_date DATE NOT NULL,
    filing_date DATE NOT NULL,
    days_to_file INTEGER DEFAULT 0,
    is_late BOOLEAN DEFAULT 0,
    has_committee_conflict BOOLEAN DEFAULT 0,
    conflict_committee VARCHAR(255),
    conflict_sector VARCHAR(128),
    conflict_desc TEXT,
    doc_url TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_congress_ticker ON congress_trades(ticker);
CREATE INDEX IF NOT EXISTS idx_congress_politician ON congress_trades(politician);
CREATE INDEX IF NOT EXISTS idx_congress_party ON congress_trades(party);
CREATE INDEX IF NOT EXISTS idx_congress_tx_date ON congress_trades(transaction_date);
CREATE INDEX IF NOT EXISTS idx_congress_conflict ON congress_trades(has_committee_conflict);

-- 3. Cluster & Super-Signal Alerts (Updated every 15 mins by Cronjob)
CREATE TABLE IF NOT EXISTS cluster_alerts (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    alert_type VARCHAR(64) NOT NULL, -- 'SUPER_SIGNAL_CEO_CONGRESS', 'INSIDER_CLUSTER', 'BIPARTISAN_CONFLUENCE', 'COMMITTEE_CONFLICT'
    ticker VARCHAR(12) NOT NULL,
    company_name VARCHAR(255),
    conviction_score INTEGER NOT NULL DEFAULT 80, -- 0 - 100
    title_de VARCHAR(255) NOT NULL,
    title_en VARCHAR(255) NOT NULL,
    summary_de TEXT NOT NULL,
    summary_en TEXT NOT NULL,
    politician_names TEXT,
    politician_party VARCHAR(8),
    politician_amount TEXT,
    insider_names TEXT,
    insider_roles TEXT,
    insider_value TEXT,
    timeframe_start DATE,
    timeframe_end DATE,
    trade_count INTEGER DEFAULT 1,
    is_pushed BOOLEAN DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_cluster_ticker ON cluster_alerts(ticker);
CREATE INDEX IF NOT EXISTS idx_cluster_created ON cluster_alerts(created_at);
CREATE INDEX IF NOT EXISTS idx_cluster_score ON cluster_alerts(conviction_score);

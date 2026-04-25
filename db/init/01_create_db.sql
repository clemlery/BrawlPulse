--
-- SYNOPSIS:
-- Database initialization script for BrawlPulse that creates core tables for tracking
-- Brawlhalla player statistics and daily performance snapshots. This schema maintains
-- a record of player identities (linked via Steam and Brawlhalla APIs) and historical
-- ELO ratings, match statistics, and legend-specific data.
--
-- TABLES:
-- - tracked_players: Master table of monitored players with identity mappings
-- - daily_snapshots: Time-series data capturing daily player statistics and ratings

-- tracked_players table
CREATE TABLE IF NOT EXISTS tracked_players (
    id SERIAL PRIMARY KEY,
    steam_id BIGINT UNIQUE NOT NULL,
    brawlhalla_id INT UNIQUE NOT NULL,
    current_name TEXT NOT NULL,
    added_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS player_names (
    player_id INT NOT NULL REFERENCES tracked_players(id) ON DELETE CASCADE,
    name TEXT NOT NULL,
    last_seen_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    PRIMARY KEY (player_id, name)
);

CREATE INDEX idx_player_names_name ON player_names(name);
-- tracked_players table comments
COMMENT ON COLUMN tracked_players.steam_id IS 'Provided by the user for now';
COMMENT ON COLUMN tracked_players.brawlhalla_id IS 'Resolved from the brawlhlla API';
COMMENT ON COLUMN tracked_players.current_name IS 'Last known in game name';
COMMENT ON COLUMN tracked_players.all_names IS 'All names changed by this player';
COMMENT ON COLUMN tracked_players.added_at IS 'DEFAULT NOW()';

-- daily_snapshots table
CREATE TABLE IF NOT EXISTS daily_snapshots (
    id SERIAL PRIMARY KEY,
    player_id INT,
    snapshot_date DATE,
    wins INT,
    games INT,
    rating INT,
    peak_rating INT,
    legends_raw JSONB,
    created_at TIMESTAMPTZ,
    CONSTRAINT fk_player_id
    FOREIGN KEY (player_id)
    REFERENCES tracked_players(id)
);
CREATE UNIQUE INDEX IF NOT EXISTS idx_player_id_snapshot_date ON daily_snapshots (player_id, snapshot_date DESC);

-- daily_snapshots table comments  
COMMENT ON COLUMN daily_snapshots.player_id IS 'CASCADE DELETE';
COMMENT ON COLUMN daily_snapshots.snapshot_date IS 'DEFAULT CURRENT_DATE';
COMMENT ON COLUMN daily_snapshots.rating IS 'Global ELO';
COMMENT ON COLUMN daily_snapshots.legends_raw IS 'Full per-legend stats';
COMMENT ON COLUMN daily_snapshots.created_at IS 'DEFAULT NOW()';

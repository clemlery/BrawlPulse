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
CREATE TABLE tracked_players (
    id SERIAL PRIMARY KEY,
    steam_id  BIGINT UNIQUE,
    brawlhalla_id INT,
    current_name TEXT,
    all_names TEXT[] UNIQUE,
    added_at TIMESTAMPTZ,
);

-- tracked_players table comments
COMMENT ON COLUMN tracked_players.steam_id 'Provided by the user for now';
COMMENT ON COLUMN tracked_players.brawlhalla_id 'Resolved from the brawlhlla API';
COMMENT ON COLUMN tracked_players.current_name 'Last known in game name';
COMMENT ON COLUMN tracked_players.all_names 'All names changed by this player';
COMMENT ON COLUMN tracked_players.added_at 'DEFAULT NOW()';

-- daily_snapshots table
CREATE TABLE daily_snapshots (
    id SERIAL PRIMARY KEY,
    player_id INT,
    snapshot_date DATE,
    wins INT,
    games INT,
    rating INT,
    peak_rating INT,
    legend_raw JSONB,
    created_at TIMESTAMPZ,
        CONSTRAINT fk_Person
    FOREIGN KEY (PersonID)
    REFERENCES Persons(PersonID)
)

-- daily_snapshots table comments 
COMMENT ON COLUMN daily_snapshots.player_id 'CASCADE DELETE';
COMMENT ON COLUMN daily_snapshots.snapshot_date 'DEFAULT CURRENT_DATE';
COMMENT ON COLUMN daily_snapshots.rating 'Global ELO';
COMMENT ON COLUMN daily_snapshots.legends_raw 'Full per-legend stats';
COMMENT ON COLUMN daily_snapshots.created_at 'DEFAULT NOW()'

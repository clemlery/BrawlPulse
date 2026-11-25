-- Migration 001: add ON DELETE CASCADE to daily_snapshots.player_id
--
-- Context: db/init/ only runs on an empty Postgres volume. Databases created
-- before this fix have a fk_player_id constraint without ON DELETE CASCADE,
-- so DELETE /players/{steamId} fails as soon as the player has snapshots.
--
-- Apply manually, once, on an existing database. Idempotent: safe to re-run.

BEGIN;

ALTER TABLE daily_snapshots
    DROP CONSTRAINT IF EXISTS fk_player_id;

ALTER TABLE daily_snapshots
    ADD CONSTRAINT fk_player_id
    FOREIGN KEY (player_id)
    REFERENCES tracked_players(id)
    ON DELETE CASCADE;

COMMIT;

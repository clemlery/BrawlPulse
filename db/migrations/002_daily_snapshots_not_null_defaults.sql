-- Migration 002: align daily_snapshots with the Exposed table definition
--
-- Context: the original init script declared every daily_snapshots column as
-- nullable and without defaults, while the backend (DailySnapshotsTable.kt)
-- assumes NOT NULL columns and a DB-side default for created_at.
--
-- Apply manually, once, on an existing database, after migration 001.
-- Aborts without changing anything if NULL values are found: inspect them with
--   SELECT * FROM daily_snapshots WHERE <column> IS NULL;
-- Idempotent: safe to re-run.

BEGIN;

DO $$
DECLARE
    null_rows INT;
BEGIN
    SELECT COUNT(*) INTO null_rows
    FROM daily_snapshots
    WHERE player_id IS NULL
       OR snapshot_date IS NULL
       OR wins IS NULL
       OR games IS NULL
       OR rating IS NULL
       OR peak_rating IS NULL
       OR legends_raw IS NULL;

    IF null_rows > 0 THEN
        RAISE EXCEPTION 'Migration aborted: % daily_snapshots row(s) contain NULL values', null_rows;
    END IF;
END $$;

-- created_at can be backfilled safely: it is only a technical timestamp.
UPDATE daily_snapshots
SET created_at = snapshot_date::timestamptz
WHERE created_at IS NULL;

ALTER TABLE daily_snapshots
    ALTER COLUMN player_id SET NOT NULL,
    ALTER COLUMN snapshot_date SET NOT NULL,
    ALTER COLUMN snapshot_date SET DEFAULT CURRENT_DATE,
    ALTER COLUMN wins SET NOT NULL,
    ALTER COLUMN games SET NOT NULL,
    ALTER COLUMN rating SET NOT NULL,
    ALTER COLUMN peak_rating SET NOT NULL,
    ALTER COLUMN legends_raw SET NOT NULL,
    ALTER COLUMN created_at SET NOT NULL,
    ALTER COLUMN created_at SET DEFAULT NOW();

COMMIT;

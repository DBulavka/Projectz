CREATE TABLE IF NOT EXISTS game_code_difficulty (
    id UUID PRIMARY KEY,
    value VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

ALTER TABLE game_level_code
    ADD COLUMN IF NOT EXISTS value VARCHAR(255),
    ADD COLUMN IF NOT EXISTS description TEXT,
    ADD COLUMN IF NOT EXISTS difficulty_id UUID;

UPDATE game_level_code
SET value = code
WHERE value IS NULL;

DO $$
DECLARE
    default_difficulty_id UUID;
BEGIN
    SELECT id INTO default_difficulty_id FROM game_code_difficulty WHERE value = 'normal';

    IF default_difficulty_id IS NULL THEN
        default_difficulty_id := '00000000-0000-0000-0000-000000000001'::uuid;
        INSERT INTO game_code_difficulty(id, value, description, created_at, updated_at)
        VALUES (default_difficulty_id, 'normal', 'Default migrated difficulty', now(), now());
    END IF;

    UPDATE game_level_code
    SET difficulty_id = default_difficulty_id
    WHERE difficulty_id IS NULL;
END$$;

ALTER TABLE game_level_code
    ALTER COLUMN value SET NOT NULL,
    ALTER COLUMN difficulty_id SET NOT NULL;

ALTER TABLE game_level_code
    ADD CONSTRAINT fk_game_level_code_difficulty FOREIGN KEY (difficulty_id)
        REFERENCES game_code_difficulty(id);

DROP INDEX IF EXISTS uq_game_level_code_value;
CREATE UNIQUE INDEX IF NOT EXISTS uq_game_level_code_value
    ON game_level_code(process_definition_meta_id, level_key, value, difficulty_id);

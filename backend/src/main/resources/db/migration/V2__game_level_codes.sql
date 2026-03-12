CREATE TABLE IF NOT EXISTS game_level_code (
    id UUID PRIMARY KEY,
    process_id VARCHAR(255) NOT NULL,
    level_key VARCHAR(255) NOT NULL,
    code VARCHAR(255) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE UNIQUE INDEX IF NOT EXISTS uq_game_level_code_value
    ON game_level_code(process_id, level_key, code);

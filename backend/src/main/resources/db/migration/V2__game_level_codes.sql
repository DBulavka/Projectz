CREATE TABLE IF NOT EXISTS game_level_code (
    id UUID PRIMARY KEY,
    process_definition_meta_id UUID NOT NULL,
    level_key VARCHAR(255) NOT NULL,
    code VARCHAR(255) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT fk_game_level_code_meta FOREIGN KEY (process_definition_meta_id)
        REFERENCES process_definition_meta(id) ON DELETE CASCADE
);

CREATE UNIQUE INDEX IF NOT EXISTS uq_game_level_code_value
    ON game_level_code(process_definition_meta_id, level_key, code);

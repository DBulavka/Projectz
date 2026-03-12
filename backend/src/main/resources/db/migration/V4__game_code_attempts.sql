CREATE TABLE IF NOT EXISTS game_code_attempt (
    id UUID PRIMARY KEY,
    task_id VARCHAR(255) NOT NULL,
    process_id VARCHAR(255) NOT NULL,
    level_key VARCHAR(255) NOT NULL,
    value VARCHAR(255) NOT NULL,
    is_correct BOOLEAN NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_game_code_attempt_task_created
    ON game_code_attempt(task_id, created_at);

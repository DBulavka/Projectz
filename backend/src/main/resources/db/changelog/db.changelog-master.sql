--liquibase formatted sql

--changeset codex:1
CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(32) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE IF NOT EXISTS group_type (
    id UUID PRIMARY KEY,
    code VARCHAR(100) NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE IF NOT EXISTS user_group (
    id UUID PRIMARY KEY,
    group_type_id UUID NOT NULL,
    key VARCHAR(255) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT fk_group_type FOREIGN KEY (group_type_id) REFERENCES group_type(id)
);

CREATE TABLE IF NOT EXISTS user_group_membership (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    group_id UUID NOT NULL,
    group_role VARCHAR(32) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT fk_group_membership_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_group_membership_group FOREIGN KEY (group_id) REFERENCES user_group(id) ON DELETE CASCADE,
    CONSTRAINT uq_group_membership UNIQUE (user_id, group_id)
);

CREATE TABLE IF NOT EXISTS audit_log (
    id UUID PRIMARY KEY,
    user_id UUID,
    entity_type VARCHAR(64) NOT NULL,
    entity_id VARCHAR(255) NOT NULL,
    action VARCHAR(64) NOT NULL,
    payload_json TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL
);


CREATE TABLE IF NOT EXISTS game_code_difficulty (
    id UUID PRIMARY KEY,
    value VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE IF NOT EXISTS game_level_code (
    id UUID PRIMARY KEY,
    process_id VARCHAR(255) NOT NULL,
    level_key VARCHAR(255) NOT NULL,
    code VARCHAR(255) NOT NULL,
    value VARCHAR(255) NOT NULL,
    description TEXT,
    difficulty_id UUID NOT NULL,
    CONSTRAINT fk_game_level_code_difficulty FOREIGN KEY (difficulty_id) REFERENCES game_code_difficulty(id),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE UNIQUE INDEX IF NOT EXISTS uq_game_level_code_value
    ON game_level_code(process_id, level_key, code);

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

--changeset codex:2
CREATE TABLE IF NOT EXISTS game (
    id UUID PRIMARY KEY,
    number INTEGER NOT NULL UNIQUE,
    process_definition_id VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    start_at TIMESTAMP WITH TIME ZONE NOT NULL,
    started_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE IF NOT EXISTS game_registration (
    id UUID PRIMARY KEY,
    game_id UUID NOT NULL,
    group_id UUID NOT NULL,
    status VARCHAR(32) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT fk_game_registration_game FOREIGN KEY (game_id) REFERENCES game(id) ON DELETE CASCADE,
    CONSTRAINT fk_game_registration_group FOREIGN KEY (group_id) REFERENCES user_group(id) ON DELETE CASCADE,
    CONSTRAINT uq_game_registration UNIQUE (game_id, group_id)
);

CREATE INDEX IF NOT EXISTS idx_game_start_at_pending
    ON game(start_at)
    WHERE started_at IS NULL;

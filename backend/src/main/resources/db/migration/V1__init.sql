CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(32) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE IF NOT EXISTS process_definition_meta (
    id UUID PRIMARY KEY,
    owner_id UUID NOT NULL,
    key VARCHAR(255) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    category VARCHAR(255),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE IF NOT EXISTS process_definition_version (
    id UUID PRIMARY KEY,
    process_definition_meta_id UUID NOT NULL,
    version_number INT NOT NULL,
    bpmn_xml TEXT NOT NULL,
    flowable_deployment_id VARCHAR(255),
    flowable_process_definition_key VARCHAR(255),
    flowable_process_definition_id VARCHAR(255),
    status VARCHAR(32) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    published_at TIMESTAMP WITH TIME ZONE,
    CONSTRAINT fk_version_meta FOREIGN KEY (process_definition_meta_id) REFERENCES process_definition_meta(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS process_instance_meta (
    id UUID PRIMARY KEY,
    process_definition_meta_id UUID NOT NULL,
    process_definition_version_id UUID NOT NULL,
    owner_id UUID NOT NULL,
    flowable_process_instance_id VARCHAR(255) NOT NULL UNIQUE,
    status VARCHAR(32) NOT NULL,
    started_at TIMESTAMP WITH TIME ZONE NOT NULL,
    ended_at TIMESTAMP WITH TIME ZONE,
    CONSTRAINT fk_instance_meta FOREIGN KEY (process_definition_meta_id) REFERENCES process_definition_meta(id) ON DELETE CASCADE,
    CONSTRAINT fk_instance_version FOREIGN KEY (process_definition_version_id) REFERENCES process_definition_version(id)
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

alter table if exists json_schema
    add column if not exists last_used_for_validation datetime(6);
    
alter table if exists json_schema
    add column if not exists validation_usage_count bigint not null;

    create table json_schema (
        created datetime(6),
        last_used_for_validation datetime(6),
        municipality_id varchar(8),
        validation_usage_count bigint not null,
        version varchar(32),
        name varchar(64),
        description longtext,
        id varchar(255) not null,
        value longtext,
        primary key (id)
    ) engine=InnoDB;

    create index idx_municipality_id 
       on json_schema (municipality_id);

    create index idx_municipality_id_name 
       on json_schema (municipality_id, name);

    create index idx_municipality_id_name_version 
       on json_schema (municipality_id, name, version);

    alter table if exists json_schema 
       add constraint uc_json_schema_municipality_id_name_version unique (municipality_id, name, version);

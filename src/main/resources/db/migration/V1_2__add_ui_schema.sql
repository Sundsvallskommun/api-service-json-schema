    create table if not exists ui_schema (
        created datetime(6),
        description longtext,
        id varchar(255) not null,
        json_schema_id varchar(255) not null,
        value longtext,
        primary key (id)
    ) engine=InnoDB;
    
    alter table if exists ui_schema 
       add constraint uq_ui_schema_json_schema unique (json_schema_id);

    alter table if exists ui_schema 
       add constraint fk_ui_schema_json_schema 
       foreign key (json_schema_id) 
       references json_schema (id);

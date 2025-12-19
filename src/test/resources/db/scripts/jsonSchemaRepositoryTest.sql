insert into json_schema 
     (id, created, municipality_id, version, name, description, value, validation_usage_count) 
values 
     ('2281_schema_1.0.0', NOW(6), '2281', '1.0.0', 'schema', 'Schema 1', '{ "type": "object", "properties": { "firstName": { "type": "string" }, "lastName": { "type": "string" } } }', 0),
     ('2281_schema_with_uischema_1.0.0', NOW(6), '2281', '1.0.0', 'schema_with_uischema', 'Schema 2', '{ "type": "object", "properties": { "firstName": { "type": "string" }, "lastName": { "type": "string" } } }', 0);

     
insert into ui_schema 
     (id, json_schema_id, value, description, created) 
values 
     ('5acd163f-f959-4ba3-ab81-9c705753eaf3','2281_schema_with_uischema_1.0.0','{"firstName":{"ui:widget":"text","ui:placeholder":"Enter first name"},"lastName":{"ui:widget":"text","ui:placeholder":"Enter last name"},"ui:order":["firstName","lastName"]}','UI schema for rendering the person form','2025-01-01 10:05:00');

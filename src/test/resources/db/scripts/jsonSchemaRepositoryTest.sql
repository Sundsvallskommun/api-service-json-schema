insert into json_schema 
     (id, created, municipality_id, version, name, description, value, validation_usage_count) 
values 
     ('2281_schema_1.0.0', NOW(6), '2281', '1.0.0', 'schema', 'Schema 1', '{ "type": "object", "properties": { "firstName": { "type": "string" }, "lastName": { "type": "string" } } }', 0),
     ('2281_schema_with_references_1.0.0', NOW(6), '2281', '1.0.0', 'schema_with_references', 'Schema 2', '{ "type": "object", "properties": { "firstName": { "type": "string" }, "lastName": { "type": "string" } } }', 0);

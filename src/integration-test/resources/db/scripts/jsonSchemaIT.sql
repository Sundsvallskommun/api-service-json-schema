insert into json_schema 
     (id, created, municipality_id, version, name, description, value) 
values 
     ('2281_schema_1.0.0', '2025-01-01 12:13:14.000', '2281', '1.0.0', 'schema', 'Schema 1', '{ "type": "object", "properties": { "firstName": { "type": "string" }, "lastName": { "type": "string" } } }'),
     ('2281_schema_1.5.0', '2025-02-02 12:13:14.000', '2281', '1.5.0', 'schema', 'Schema 1', '{ "type": "object", "properties": { "firstName": { "type": "string" }, "lastName": { "type": "string" } } }'),
     ('2281_schema_with_references_1.0.0', '2025-02-02 12:13:14.000', '2281', '1.0.0', 'schema_with_references', 'Schema 2', '{ "type": "object", "properties": { "firstName": { "type": "string" }, "lastName": { "type": "string" } } }');

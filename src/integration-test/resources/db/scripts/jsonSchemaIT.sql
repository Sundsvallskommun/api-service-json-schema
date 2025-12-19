insert into json_schema 
     (id, created, municipality_id, version, name, description, value, validation_usage_count) 
values 
     ('2281_schema_1.0.0', '2025-01-01 12:13:14.000', '2281', '1.0.0', 'schema', 'Schema 1', '{ "type": "object", "properties": { "firstName": { "type": "string" }, "lastName": { "type": "string" } } }', 0),
     ('2281_schema_1.5.0', '2025-02-02 12:13:14.000', '2281', '1.5.0', 'schema', 'Schema 1', '{ "type": "object", "properties": { "firstName": { "type": "string" }, "lastName": { "type": "string" } } }', 0),
     ('2281_schema_with_uischema_1.0.0', '2025-02-02 12:13:14.000', '2281', '1.0.0', 'Schema_with_uischema', 'Schema 2', '{ "type": "object", "properties": { "firstName": { "type": "string" }, "lastName": { "type": "string" } } }', 0),
     ('2281_product_1.0.0', '2025-03-01 12:13:14.000', '2281', '1.0.0', 'product', 'Product schema', '{ "$schema": "https://json-schema.org/draft/2020-12/schema", "$id": "https://example.com/product.schema.json", "title": "Product", "description": "A product from Acme catalog", "type": "object", "properties": { "productId": { "description": "The unique identifier for a product", "type": "integer" }, "productName": { "description": "Name of the product", "type": "string" }, "price": { "description": "The price of the product", "type": "number", "exclusiveMinimum": 0 }, "tags": { "description": "Tags for the product", "type": "array", "items": { "type": "string" }, "minItems": 1, "uniqueItems": true } }, "required": [ "productId", "productName", "price" ] }', 0);

     
insert into ui_schema 
     (id, json_schema_id, value, description, created) 
values 
     ('5acd163f-f959-4ba3-ab81-9c705753eaf3','2281_schema_with_uischema_1.0.0','{"firstName":{"ui:widget":"text","ui:placeholder":"Enter first name"},"lastName":{"ui:widget":"text","ui:placeholder":"Enter last name"},"ui:order":["firstName","lastName"]}','UI schema for rendering the person form','2025-01-01 10:05:00');

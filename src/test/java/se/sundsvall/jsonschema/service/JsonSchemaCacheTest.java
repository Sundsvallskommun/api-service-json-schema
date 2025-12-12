package se.sundsvall.jsonschema.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.networknt.schema.Schema;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.test.context.ActiveProfiles;
import se.sundsvall.jsonschema.integration.db.model.JsonSchemaEntity;

@SpringBootTest(classes = {
	JsonSchemaCache.class,
	CacheAutoConfiguration.class
})
@EnableCaching
@ActiveProfiles("junit")
class JsonSchemaCacheTest {

	private static final String SCHEMA_VALUE = """
		{
		  "$schema": "https://json-schema.org/draft/2020-12/schema",
		  "type": "object",
		  "properties": {
		    "name": { "type": "string" }
		  }
		}
		""";

	@Autowired
	private JsonSchemaCache cache;

	private JsonSchemaEntity entity;

	@BeforeEach
	void setup() {
		entity = new JsonSchemaEntity();
		entity.setId("schema1");
		entity.setValue(SCHEMA_VALUE);
	}

	@Test
	void cacheShouldParseSchemaAndReturnSchemaInstance() {

		// Act
		var schema = cache.getSchema(entity);

		// Assert
		assertThat(schema)
			.isNotNull()
			.isInstanceOf(Schema.class);
	}

	@Test
	void cacheShouldReturnSameInstanceForRepeatedCalls() {

		// Act
		var schema1 = cache.getSchema(entity);
		var schema2 = cache.getSchema(entity);
		var schema3 = cache.getSchema(entity);

		// Assert – all calls should return the same cached instance
		assertThat(schema1)
			.isSameAs(schema2)
			.isSameAs(schema3);
	}

	@Test
	void cacheShouldCacheDifferentSchemaIdsSeparately() {
		// First entity
		var e1 = new JsonSchemaEntity();
		e1.setId("schema1");
		e1.setValue(SCHEMA_VALUE);

		// Second entity
		var e2 = new JsonSchemaEntity();
		e2.setId("schema2");
		e2.setValue("""
			{
			  "$schema": "https://json-schema.org/draft/2020-12/schema",
			  "type": "string"
			}
			""");

		// Act – retrieve schema objects
		var schema1First = cache.getSchema(e1);
		var schema2First = cache.getSchema(e2);
		var schema1Second = cache.getSchema(e1);
		var schema2Second = cache.getSchema(e2);

		// Assert – schema1 is cached and reused
		assertThat(schema1Second).isSameAs(schema1First);

		// Assert – schema2 is cached and reused
		assertThat(schema2Second).isSameAs(schema2First);

		// Assert – schema1 and schema2 must not share the same cache entry
		assertThat(schema1First).isNotSameAs(schema2First);
	}

	@Test
	void cacheShouldUseIdAsCacheKey() {
		var e1 = new JsonSchemaEntity();
		e1.setId("schema33");
		e1.setValue(SCHEMA_VALUE);

		var e2 = new JsonSchemaEntity();
		e2.setId("schema33"); // Same ID
		e2.setValue(SCHEMA_VALUE);

		// First call → schema parsed and cached
		Schema schema1 = cache.getSchema(e1);

		// Second call with different object but same ID → same cached instance
		Schema schema2 = cache.getSchema(e2);

		// Assert – both calls must return the same cached instance
		assertThat(schema1).isSameAs(schema2);
	}
}

package se.sundsvall.jsonschema.service;

import static com.networknt.schema.SpecificationVersion.DRAFT_2020_12;

import com.networknt.schema.Schema;
import com.networknt.schema.SchemaRegistry;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import se.sundsvall.jsonschema.integration.db.model.JsonSchemaEntity;

/**
 * Cache component responsible for parsing and caching JSON Schema definitions.
 *
 * <p>
 * This component uses a {@link SchemaRegistry} initialized with the default
 * {@code DRAFT_2020_12} dialect to parse raw JSON Schema documents into
 * {@link Schema} instances. Parsed schemas are cached to avoid repeated parsing
 * and to improve performance when the same schema is requested multiple times.
 * </p>
 *
 * <p>
 * The cache entry is keyed by the {@code id} of the associated
 * {@link JsonSchemaEntity}. If the schema has previously been parsed and
 * cached, the cached instance will be returned. Otherwise, the schema is parsed
 * from the entity's {@code value} field and stored in the cache.
 * </p>
 */
@Component
public class JsonSchemaCache {

	private static final SchemaRegistry REGISTRY = SchemaRegistry.withDefaultDialect(DRAFT_2020_12);

	/**
	 * Returns a parsed {@link Schema} for the supplied {@link JsonSchemaEntity}.
	 *
	 * <p>
	 * The method is cache-enabled using the {@code jsonSchemas} cache. Cache keys
	 * are derived from {@code entity.id}. When no cached value exists, the method
	 * parses the raw JSON Schema contained in {@code entity.value} and stores the
	 * resulting {@link Schema} instance in the cache.
	 * </p>
	 *
	 * @param  entity the JSON Schema entity containing the schema definition; must not be null
	 * @return        the parsed {@link Schema} instance
	 */
	@Cacheable(value = "jsonSchemas", key = "#entity.id")
	public Schema getSchema(JsonSchemaEntity entity) {
		final var schema = REGISTRY.getSchema(entity.getValue());
		schema.initializeValidators();
		return schema;
	}
}

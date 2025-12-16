package se.sundsvall.jsonschema.api.model;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;
import java.util.Objects;

@Schema(description = "JsonSchema model")
public class JsonSchema {

	@Schema(description = "Schema ID. The ID is composed by the municipalityId, schema name and version. I.e.: [municipality_id]_[schema_name]_[schema_version]", examples = "2281_person_1.0")
	private String id;

	@Schema(description = "Schema name", examples = "person")
	private String name;

	@Schema(description = "Schema version on the format [major version].[minor version]", examples = "1.0")
	private String version;

	@Schema(description = "The JSON schema, specified by: https://json-schema.org/draft/2020-12/schema", example = """
		{
		  "$id": "https://example.com/person.schema.json",
		  "$schema": "https://json-schema.org/draft/2020-12/schema",
		  "title": "Person",
		  "type": "object",
		  "properties": {
		    "firstName": {
		      "type": "string",
		      "description": "The person's first name."
		    },
		    "lastName": {
		      "type": "string",
		      "description": "The person's last name."
		    }
		  }
		}
		""")
	private JsonNode value;

	@Schema(description = "Description of the schema purpose", examples = "A JSON-schema that defines a person object")
	private String description;

	@Schema(description = "Created timestamp")
	private OffsetDateTime created;

	@Schema(description = "Number of times this schema has been used to validate a JSON instance")
	private long validationUsageCount;

	@Schema(description = "Timestamp when this schema was last used to validate a JSON instance")
	private OffsetDateTime lastUsedForValidation;

	public static JsonSchema create() {
		return new JsonSchema();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public JsonSchema withId(String id) {
		this.id = id;
		return this;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public JsonSchema withName(String name) {
		this.name = name;
		return this;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public JsonSchema withVersion(String version) {
		this.version = version;
		return this;
	}

	public JsonNode getValue() {
		return value;
	}

	public void setValue(JsonNode value) {
		this.value = value;
	}

	public JsonSchema withValue(JsonNode value) {
		this.value = value;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public JsonSchema withDescription(String description) {
		this.description = description;
		return this;
	}

	public OffsetDateTime getCreated() {
		return created;
	}

	public void setCreated(OffsetDateTime created) {
		this.created = created;
	}

	public JsonSchema withCreated(OffsetDateTime created) {
		this.created = created;
		return this;
	}

	public long getValidationUsageCount() {
		return validationUsageCount;
	}

	public void setValidationUsageCount(long validationUsageCount) {
		this.validationUsageCount = validationUsageCount;
	}

	public JsonSchema withValidationUsageCount(long validationUsageCount) {
		this.validationUsageCount = validationUsageCount;
		return this;
	}

	public OffsetDateTime getLastUsedForValidation() {
		return lastUsedForValidation;
	}

	public void setLastUsedForValidation(OffsetDateTime lastUsedForValidation) {
		this.lastUsedForValidation = lastUsedForValidation;
	}

	public JsonSchema withLastUsedForValidation(OffsetDateTime lastUsedForValidation) {
		this.lastUsedForValidation = lastUsedForValidation;
		return this;
	}

	@Override
	public int hashCode() {
		return Objects.hash(created, description, id, lastUsedForValidation, name, validationUsageCount, value, version);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		JsonSchema other = (JsonSchema) obj;
		return Objects.equals(created, other.created) && Objects.equals(description, other.description) && Objects.equals(id, other.id) && Objects.equals(lastUsedForValidation, other.lastUsedForValidation) && Objects.equals(name, other.name)
			&& validationUsageCount == other.validationUsageCount && Objects.equals(value, other.value) && Objects.equals(version, other.version);
	}

	@Override
	public String toString() {
		return "JsonSchema [id=" + id + ", name=" + name + ", version=" + version + ", value=" + value + ", description=" + description + ", created=" + created + ", validationUsageCount=" + validationUsageCount + ", lastUsedForValidation="
			+ lastUsedForValidation + "]";
	}
}

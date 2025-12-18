package se.sundsvall.jsonschema.api.model;

import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;
import java.util.Objects;

@Schema(description = "UI schema model", accessMode = READ_ONLY)
public class UiSchema {

	@Schema(description = "UI Schema ID", examples = "f83529c1-2dff-4a5d-aab0-8d4ec082a995", accessMode = READ_ONLY)
	private String id;

	@Schema(description = "The UI schema", example = """
		{
		  "ui:title": "Title",
		  "ui:description": "Description",
		  "ui:submitButtonOptions": {
		    "props": {
		      "disabled": false,
		      "className": "btn btn-info",
		    },
		      "norender": false,
		      "submitText": "Submit"
		    }
		}
		""", accessMode = READ_ONLY)
	private JsonNode value;

	@Schema(description = "Description of the UI schema purpose", examples = "An UI-schema that defines how the form in the web-app should be rendered", accessMode = READ_ONLY)
	private String description;

	@Schema(description = "Created timestamp", accessMode = READ_ONLY)
	private OffsetDateTime created;

	public static UiSchema create() {
		return new UiSchema();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public UiSchema withId(String id) {
		this.id = id;
		return this;
	}

	public JsonNode getValue() {
		return value;
	}

	public void setValue(JsonNode value) {
		this.value = value;
	}

	public UiSchema withValue(JsonNode value) {
		this.value = value;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public UiSchema withDescription(String description) {
		this.description = description;
		return this;
	}

	public OffsetDateTime getCreated() {
		return created;
	}

	public void setCreated(OffsetDateTime created) {
		this.created = created;
	}

	public UiSchema withCreated(OffsetDateTime created) {
		this.created = created;
		return this;
	}

	@Override
	public int hashCode() {
		return Objects.hash(created, description, id, value);
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
		UiSchema other = (UiSchema) obj;
		return Objects.equals(created, other.created) && Objects.equals(description, other.description) && Objects.equals(id, other.id) && Objects.equals(value, other.value);
	}

	@Override
	public String toString() {
		return "UiSchema [id=" + id + ", value=" + value + ", description=" + description + ", created=" + created + "]";
	}
}

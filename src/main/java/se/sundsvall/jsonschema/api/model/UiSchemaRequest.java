package se.sundsvall.jsonschema.api.model;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.util.Objects;

@Schema(description = "JsonSchemaCreateRequest model")
public class UiSchemaRequest {

	@NotNull
	@Schema(description = "The UI schema", requiredMode = REQUIRED, example = """
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
		""")
	private JsonNode value;

	@Schema(description = "Description of the UI schema purpose", examples = "A UI-schema that defines the rendering of the person form")
	private String description;

	public static UiSchemaRequest create() {
		return new UiSchemaRequest();
	}

	public JsonNode getValue() {
		return value;
	}

	public void setValue(JsonNode value) {
		this.value = value;
	}

	public UiSchemaRequest withValue(JsonNode value) {
		this.value = value;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public UiSchemaRequest withDescription(String description) {
		this.description = description;
		return this;
	}

	@Override
	public int hashCode() {
		return Objects.hash(description, value);
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
		UiSchemaRequest other = (UiSchemaRequest) obj;
		return Objects.equals(description, other.description) && Objects.equals(value, other.value);
	}

	@Override
	public String toString() {
		return "UiSchemaRequest [value=" + value + ", description=" + description + "]";
	}
}

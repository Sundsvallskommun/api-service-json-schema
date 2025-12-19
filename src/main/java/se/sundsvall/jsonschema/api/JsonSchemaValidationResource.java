package se.sundsvall.jsonschema.api;

import static org.springframework.http.MediaType.ALL_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON_VALUE;
import static org.springframework.http.ResponseEntity.noContent;
import static se.sundsvall.jsonschema.service.mapper.JsonSchemaMapper.toJsonString;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zalando.problem.Problem;
import org.zalando.problem.violations.ConstraintViolationProblem;
import se.sundsvall.dept44.common.validators.annotation.ValidMunicipalityId;
import se.sundsvall.jsonschema.service.JsonSchemaValidationService;

@RestController
@Validated
@RequestMapping(value = "/{municipalityId}/schemas")
@Tag(name = "JSON-schema validation")
@ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(oneOf = {
	Problem.class, ConstraintViolationProblem.class
})))
@ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
class JsonSchemaValidationResource {

	private final JsonSchemaValidationService jsonSchemaValidationService;

	public JsonSchemaValidationResource(JsonSchemaValidationService jsonSchemaValidationService) {
		this.jsonSchemaValidationService = jsonSchemaValidationService;
	}

	@PostMapping(path = "/{id}/validation", consumes = APPLICATION_JSON_VALUE, produces = ALL_VALUE)
	@Operation(operationId = "validateJson", summary = "Validate a JSON structure against the specified schema", responses = {
		@ApiResponse(responseCode = "204", description = "No content - JSON is valid according to the schema", useReturnTypeSchema = true),
		@ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
	})
	public ResponseEntity<Void> validateJson(
		@Parameter(name = "municipalityId", description = "Municipality ID", example = "2281") @ValidMunicipalityId @PathVariable String municipalityId,
		@Parameter(name = "id", description = "Schema ID", example = "2281_person_1.0") @PathVariable @NotBlank final String id,
		@NotNull @RequestBody JsonNode json) {

		jsonSchemaValidationService.validateAndThrow(toJsonString(json), id);

		return noContent().build();
	}
}

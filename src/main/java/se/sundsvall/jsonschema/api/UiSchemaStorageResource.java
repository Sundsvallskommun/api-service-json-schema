package se.sundsvall.jsonschema.api;

import static org.springframework.http.MediaType.ALL_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON_VALUE;
import static org.springframework.http.ResponseEntity.noContent;
import static org.springframework.http.ResponseEntity.ok;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zalando.problem.Problem;
import org.zalando.problem.violations.ConstraintViolationProblem;
import se.sundsvall.dept44.common.validators.annotation.ValidMunicipalityId;
import se.sundsvall.jsonschema.api.model.UiSchema;
import se.sundsvall.jsonschema.api.model.UiSchemaRequest;

@RestController
@Validated
@RequestMapping(value = "/{municipalityId}/schemas/{id}/ui-schema")
@Tag(name = "UI-schemas")
@ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(oneOf = {
	Problem.class, ConstraintViolationProblem.class
})))
@ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
class UiSchemaStorageResource {

	public UiSchemaStorageResource() {}

	@GetMapping(produces = APPLICATION_JSON_VALUE)
	@Operation(operationId = "getUiSchemaById", summary = "Get the UI schema that belongs to the provided JSON schema", responses = {
		@ApiResponse(responseCode = "200", description = "OK", useReturnTypeSchema = true),
		@ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
	})
	ResponseEntity<UiSchema> getUiSchemaById(
		@Parameter(name = "municipalityId", description = "Municipality ID", example = "2281") @ValidMunicipalityId @PathVariable final String municipalityId,
		@Parameter(name = "id", description = "Schema ID", example = "2281_person_1.0") @NotBlank @PathVariable final String id) {

		return ok(UiSchema.create());
	}

	@PutMapping(consumes = APPLICATION_JSON_VALUE, produces = ALL_VALUE)
	@Operation(operationId = "createOrUpdateUiSchema",
		summary = "Create or replace a UI schema for the provided JSON schema",
		responses = @ApiResponse(responseCode = "204", description = "No content - Successful operation", useReturnTypeSchema = true))
	ResponseEntity<Void> createOrUpdateUiSchema(
		@Parameter(name = "municipalityId", description = "Municipality ID", example = "2281") @ValidMunicipalityId @PathVariable final String municipalityId,
		@Parameter(name = "id", description = "Schema ID", example = "2281_person_1.0") @NotBlank @PathVariable final String id,
		@Valid @NotNull @RequestBody final UiSchemaRequest body) {

		return noContent().build();
	}

	@DeleteMapping(produces = ALL_VALUE)
	@Operation(operationId = "deleteUiSchema", summary = "Delete the UI schema for the provided JSON schema", responses = {
		@ApiResponse(responseCode = "204", description = "No content - Successful operation", useReturnTypeSchema = true),
		@ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
	})
	ResponseEntity<Void> deleteUiSchema(
		@Parameter(name = "municipalityId", description = "Municipality ID", example = "2281") @ValidMunicipalityId @PathVariable final String municipalityId,
		@Parameter(name = "id", description = "Schema ID", example = "2281_person_1.0") @PathVariable @NotBlank final String id) {

		return noContent().build();
	}
}

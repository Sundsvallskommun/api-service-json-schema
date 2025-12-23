package se.sundsvall.jsonschema.service;

public final class Constants {

	static final String MESSAGE_JSON_SCHEMA_NOT_FOUND_BY_ID = "No JsonSchema with ID '%s' was found!";
	static final String MESSAGE_JSON_SCHEMA_NOT_FOUND_BY_NAME = "No JsonSchema with name '%s' was found!";
	static final String JSON_SCHEMA_ALREADY_EXISTS = "A JsonSchema with ID '%s' already exists!";
	static final String JSON_SCHEMA_WITH_GREATER_VERSION_EXISTS = "A JsonSchema with a greater version already exists! (see schema with ID: '%s')";

	static final String MESSAGE_UI_SCHEMA_NOT_FOUND_BY_JSON_SCHEMA_ID = "No UiSchema on JsonSchema with ID '%s' was found!";

	private Constants() {}
}

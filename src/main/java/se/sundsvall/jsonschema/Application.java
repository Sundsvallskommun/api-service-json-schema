package se.sundsvall.jsonschema;

import static org.springframework.boot.SpringApplication.run;

import org.springframework.cache.annotation.EnableCaching;
import se.sundsvall.dept44.ServiceApplication;
import se.sundsvall.dept44.util.jacoco.ExcludeFromJacocoGeneratedCoverageReport;

@ServiceApplication
@EnableCaching
@ExcludeFromJacocoGeneratedCoverageReport
public class Application {
	public static void main(final String... args) {
		run(Application.class, args);
	}
}

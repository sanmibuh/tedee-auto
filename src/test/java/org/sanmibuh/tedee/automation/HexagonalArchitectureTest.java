package org.sanmibuh.tedee.automation;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

class HexagonalArchitectureTest {

	private static final String BASE_PACKAGE = "org.sanmibuh.tedee.automation";

	private final JavaClasses classes = new ClassFileImporter().importPackages(BASE_PACKAGE);

	@Test
	void domainLayerShouldNotDependOnSpring() {
		final ArchRule rule = noClasses().that().resideInAPackage("..domain..").should().dependOnClassesThat()
				.resideInAPackage("org.springframework..");
		rule.check(this.classes);
	}

	@Test
	void domainLayerShouldNotDependOnInfrastructure() {
		final ArchRule rule = noClasses().that().resideInAPackage("..domain..").should().dependOnClassesThat()
				.resideInAPackage("..infrastructure..");
		rule.check(this.classes);
	}

	@Test
	void infrastructureLayerShouldNotDependOnApplicationLayerOfOtherAggregates() {
		final ArchRule rule = noClasses().that().resideInAPackage("..lock.infrastructure..").should()
				.dependOnClassesThat().resideInAPackage("..notification.application..");
		rule.check(this.classes);
	}
}

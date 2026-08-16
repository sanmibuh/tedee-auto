package org.sanmibuh.tedee.automation;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import org.junit.jupiter.api.Test;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;

class HexagonalArchitectureTest {

  private static final String BASE_PACKAGE = "org.sanmibuh";

  private final JavaClasses classes = new ClassFileImporter().importPackages(BASE_PACKAGE);

  @Test
  void should_forbidSpringDependencies_whenInDomainLayer() {
    final ArchRule rule = noClasses()
      .that().resideInAPackage("..domain..")
      .should().dependOnClassesThat()
      .resideInAPackage("org.springframework..");
    rule.check(classes);
  }

  @Test
  void should_forbidInfrastructureDependencies_whenInDomainLayer() {
    final ArchRule rule = noClasses()
      .that().resideInAPackage("..domain..")
      .should().dependOnClassesThat()
      .resideInAPackage("..infrastructure..");
    rule.check(classes);
  }
}

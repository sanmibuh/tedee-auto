package org.sanmibuh.tedee.automation;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

import org.junit.jupiter.api.Test;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;

class HexagonalArchitectureTest {

  private static final String BASE_PACKAGE = "org.sanmibuh";

  private final JavaClasses classes = new ClassFileImporter().importPackages(BASE_PACKAGE);

  @Test
  void should_forbidSpringDependencies_whenInDomainLayer() {
    final var rule = noClasses()
      .that().resideInAPackage("..domain..")
      .should().dependOnClassesThat()
      .resideInAPackage("org.springframework..");
    rule.check(classes);
  }

  @Test
  void should_forbidInfrastructureDependencies_whenInDomainLayer() {
    final var rule = noClasses()
      .that().resideInAPackage("..domain..")
      .should().dependOnClassesThat()
      .resideInAPackage("..infrastructure..");
    rule.check(classes);
  }

  // allowEmptyShould: currently only one aggregate exists (tedee.automation), so no
  // cross-aggregate violations are possible yet. The rule will start enforcing automatically
  // once a second aggregate with an infrastructure sub-package is introduced.
  @Test
  void should_forbidCrossAggregateCoupling_whenInInfrastructureLayer() {
    slices()
      .matching("org.sanmibuh.tedee.(*).infrastructure..")
      .should().notDependOnEachOther()
      .allowEmptyShould(true)
      .check(classes);
  }
}

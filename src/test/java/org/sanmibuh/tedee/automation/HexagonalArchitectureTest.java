package org.sanmibuh.tedee.automation;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.Test;

class HexagonalArchitectureTest {

  private static final String BASE_PACKAGE = "org.sanmibuh";

  private final JavaClasses classes = new ClassFileImporter().importPackages(BASE_PACKAGE);

  @Test
  void should_domainLayer_notDependOnSpring() {
    final ArchRule rule = noClasses().that().resideInAPackage("..domain..").should().dependOnClassesThat()
        .resideInAPackage("org.springframework..");
    rule.check(classes);
  }

  @Test
  void should_domainLayer_notDependOnInfrastructure() {
    final ArchRule rule = noClasses().that().resideInAPackage("..domain..").should().dependOnClassesThat()
        .resideInAPackage("..infrastructure..");
    rule.check(classes);
  }
}

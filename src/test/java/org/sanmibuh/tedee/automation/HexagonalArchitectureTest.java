package org.sanmibuh.tedee.automation;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaModifier;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import java.util.Set;
import org.sanmibuh.ddd.domain.AggregateRoot;
import org.sanmibuh.ddd.domain.DomainEvent;

@AnalyzeClasses(packages = "org.sanmibuh")
class HexagonalArchitectureTest {

  private static final Set<String> ALLOWED_SCALAR_PACKAGES = Set.of("java.time", "java.math");

  private static final Set<String> ALLOWED_SCALAR_TYPES =
      Set.of(
          "java.lang.String",
          "java.lang.Boolean",
          "java.lang.Byte",
          "java.lang.Short",
          "java.lang.Integer",
          "java.lang.Long",
          "java.lang.Float",
          "java.lang.Double",
          "java.lang.Character",
          "java.util.UUID");

  @ArchTest
  static final ArchRule should_forbidApplicationDependencies_whenInDomainLayer =
      noClasses()
          .that()
          .resideInAPackage("..domain..")
          .should()
          .dependOnClassesThat()
          .resideInAPackage("..application..");

  @ArchTest
  static final ArchRule should_forbidInfrastructureDependencies_whenInApplicationLayer =
      noClasses()
          .that()
          .resideInAPackage("..application..")
          .should()
          .dependOnClassesThat()
          .resideInAPackage("..infrastructure..");

  @ArchTest
  static final ArchRule should_forbidDomainAndSecondaryDependencies_whenInPrimarySlice =
      noClasses()
          .that()
          .resideInAPackage("..infrastructure.primary..")
          .should()
          .dependOnClassesThat()
          .resideInAnyPackage("..domain..", "..infrastructure.secondary..");

  @ArchTest
  static final ArchRule should_forbidPrimaryAndApplicationDependencies_whenInSecondarySlice =
      noClasses()
          .that()
          .resideInAPackage("..infrastructure.secondary..")
          .should()
          .dependOnClassesThat()
          .resideInAnyPackage("..infrastructure.primary..", "..application..");

  @ArchTest
  static final ArchRule should_forbidSpringDependencies_whenInDomainLayer =
      noClasses()
          .that()
          .resideInAPackage("..domain..")
          .should()
          .dependOnClassesThat()
          .resideInAPackage("org.springframework..");

  @ArchTest
  static final ArchRule should_forbidInfrastructureDependencies_whenInDomainLayer =
      noClasses()
          .that()
          .resideInAPackage("..domain..")
          .should()
          .dependOnClassesThat()
          .resideInAPackage("..infrastructure..");

  @ArchTest
  static final ArchRule should_carryOnlyScalarPayloads_whenDomainEvent =
      classes().that().implement(DomainEvent.class).should(haveOnlyScalarFields());

  @ArchTest
  static final ArchRule should_beFinal_whenConcreteAggregate =
      classes()
          .that()
          .areAssignableTo(AggregateRoot.class)
          .and()
          .doNotHaveModifier(JavaModifier.ABSTRACT)
          .should()
          .haveModifier(JavaModifier.FINAL);

  private static ArchCondition<JavaClass> haveOnlyScalarFields() {
    return new ArchCondition<>("carry only primitive or standard scalar payloads") {
      @Override
      public void check(final JavaClass event, final ConditionEvents events) {
        event.getFields().stream()
            .filter(field -> !field.getModifiers().contains(JavaModifier.STATIC))
            .filter(field -> !isScalar(field.getRawType()))
            .forEach(
                field ->
                    events.add(
                        SimpleConditionEvent.violated(
                            field,
                            "%s has non-scalar payload field %s of type %s"
                                .formatted(
                                    event.getName(),
                                    field.getName(),
                                    field.getRawType().getName()))));
      }
    };
  }

  private static boolean isScalar(final JavaClass type) {
    return type.isPrimitive()
        || type.isEnum()
        || ALLOWED_SCALAR_TYPES.contains(type.getName())
        || ALLOWED_SCALAR_PACKAGES.contains(type.getPackageName());
  }
}

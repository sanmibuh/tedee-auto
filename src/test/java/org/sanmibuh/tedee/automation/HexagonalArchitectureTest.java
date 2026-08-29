package org.sanmibuh.tedee.automation;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaField;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import java.util.Set;
import org.sanmibuh.ddd.domain.DomainEvent;

@AnalyzeClasses(packages = "org.sanmibuh")
class HexagonalArchitectureTest {

  private static final Set<String> ALLOWED_SCALAR_PACKAGES =
      Set.of("java.lang", "java.time", "java.math");

  private static final Set<String> ALLOWED_SCALAR_TYPES = Set.of("java.util.UUID");

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
      classes()
          .that()
          .implement(DomainEvent.class)
          .should(haveOnlyScalarFields());

  private static ArchCondition<JavaClass> haveOnlyScalarFields() {
    return new ArchCondition<>("carry only primitive or standard scalar payloads") {
      @Override
      public void check(final JavaClass event, final ConditionEvents events) {
        for (final JavaField field : event.getFields()) {
          if (!isScalar(field.getRawType())) {
            events.add(
                SimpleConditionEvent.violated(
                    field,
                    "%s has non-scalar payload field %s of type %s"
                        .formatted(
                            event.getName(), field.getName(), field.getRawType().getName())));
          }
        }
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

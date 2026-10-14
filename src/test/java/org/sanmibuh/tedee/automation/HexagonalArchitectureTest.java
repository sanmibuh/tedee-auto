package org.sanmibuh.tedee.automation;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaField;
import com.tngtech.archunit.core.domain.JavaModifier;
import com.tngtech.archunit.core.domain.JavaParameterizedType;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import java.util.Set;
import org.sanmibuh.ddd.domain.AggregateRoot;
import org.sanmibuh.ddd.domain.AggregateRootId;
import org.sanmibuh.ddd.domain.DomainEvent;
import org.sanmibuh.ddd.domain.EntityId;
import org.sanmibuh.ddd.domain.Snapshot;
import org.sanmibuh.ddd.port.Command;
import org.sanmibuh.ddd.port.Query;

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
          "java.net.URI",
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
  static final ArchRule should_forbidAdapterDependencies_whenInApplicationLayer =
      noClasses()
          .that()
          .resideInAPackage("..application..")
          .should()
          .dependOnClassesThat()
          .resideInAPackage("..adapter..");

  @ArchTest
  static final ArchRule should_forbidDomainAndSecondaryDependencies_whenInPrimarySlice =
      noClasses()
          .that()
          .resideInAPackage("..adapter.primary..")
          .should()
          .dependOnClassesThat()
          .resideInAnyPackage("org.sanmibuh.tedee..domain..", "..adapter.secondary..");

  @ArchTest
  static final ArchRule should_forbidPrimaryAndApplicationDependencies_whenInSecondarySlice =
      noClasses()
          .that()
          .resideInAPackage("..adapter.secondary..")
          .should()
          .dependOnClassesThat()
          .resideInAnyPackage("..adapter.primary..", "..application..");

  @ArchTest
  static final ArchRule should_forbidSpringDependencies_whenInDomainLayer =
      noClasses()
          .that()
          .resideInAPackage("..domain..")
          .should()
          .dependOnClassesThat()
          .resideInAPackage("org.springframework..");

  @ArchTest
  static final ArchRule should_forbidAdapterDependencies_whenInDomainLayer =
      noClasses()
          .that()
          .resideInAPackage("..domain..")
          .should()
          .dependOnClassesThat()
          .resideInAPackage("..adapter..");

  @ArchTest
  static final ArchRule should_carryOnlyScalarOrEventPayloadRecords_whenDomainEvent =
      classes().that().implement(DomainEvent.class).should(haveOnlyAllowedPayloadFields());

  @ArchTest
  static final ArchRule should_carryOnlyScalarOrImmutableListPayloads_whenSnapshot =
      classes().that().implement(Snapshot.class).should(haveOnlyAllowedSnapshotFields());

  @ArchTest
  static final ArchRule should_beRecord_whenSnapshot =
      classes().that().implement(Snapshot.class).should().beRecords();

  @ArchTest
  static final ArchRule should_beFinal_whenConcreteAggregate =
      classes()
          .that()
          .areAssignableTo(AggregateRoot.class)
          .and()
          .doNotHaveModifier(JavaModifier.ABSTRACT)
          .should()
          .haveModifier(JavaModifier.FINAL);

  @ArchTest
  static final ArchRule should_resideInEventPackage_whenLockDomainEvent =
      classes()
          .that()
          .resideInAPackage("org.sanmibuh.tedee.lock.domain..")
          .and()
          .implement(DomainEvent.class)
          .should()
          .resideInAPackage("..domain.event..");

  @ArchTest
  static final ArchRule should_resideInExceptionPackage_whenLockDomainException =
      classes()
          .that()
          .resideInAPackage("org.sanmibuh.tedee.lock.domain..")
          .and()
          .areAssignableTo(RuntimeException.class)
          .should()
          .resideInAPackage("..domain.exception..");

  @ArchTest
  static final ArchRule should_resideInValueObjectPackage_whenLockDomainEnum =
      classes()
          .that()
          .resideInAPackage("org.sanmibuh.tedee.lock.domain..")
          .and()
          .areEnums()
          .should()
          .resideInAPackage("..domain.vo..");

  @ArchTest
  static final ArchRule should_resideInValueObjectPackage_whenLockAggregateRootId =
      classes()
          .that()
          .resideInAPackage("org.sanmibuh.tedee.lock.domain..")
          .and()
          .implement(AggregateRootId.class)
          .should()
          .resideInAPackage("..domain.vo..");

  @ArchTest
  static final ArchRule should_resideInValueObjectPackage_whenLockEntityId =
      classes()
          .that()
          .resideInAPackage("org.sanmibuh.tedee.lock.domain..")
          .and()
          .implement(EntityId.class)
          .should()
          .resideInAPackage("..domain.vo..");

  @ArchTest
  static final ArchRule should_forbidDomainDependencies_whenCommandOrQuery =
      noClasses()
          .that()
          .implement(Command.class)
          .or()
          .implement(Query.class)
          .should()
          .dependOnClassesThat()
          .resideInAPackage("..domain..");

  private static ArchCondition<JavaClass> haveOnlyAllowedPayloadFields() {
    return new ArchCondition<>("carry only scalar or event-owned record payloads") {
      @Override
      public void check(final JavaClass event, final ConditionEvents events) {
        event.getFields().stream()
            .filter(field -> !field.getModifiers().contains(JavaModifier.STATIC))
            .filter(field -> !isAllowedPayload(field.getRawType(), event))
            .forEach(
                field ->
                    events.add(
                        SimpleConditionEvent.violated(
                            field,
                            "%s has unsupported payload field %s of type %s"
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

  private static boolean isAllowedPayload(final JavaClass type, final JavaClass event) {
    return isScalar(type)
        || (type.isRecord()
            && type.getName().startsWith(event.getName() + "$")
            && type.getFields().stream()
                .filter(field -> !field.getModifiers().contains(JavaModifier.STATIC))
                .allMatch(field -> isScalar(field.getRawType())));
  }

  private static ArchCondition<JavaClass> haveOnlyAllowedSnapshotFields() {
    return new ArchCondition<>("carry only scalar or immutable list payloads") {
      @Override
      public void check(final JavaClass snapshot, final ConditionEvents events) {
        snapshot.getFields().stream()
            .filter(field -> !field.getModifiers().contains(JavaModifier.STATIC))
            .filter(field -> !isAllowedSnapshotField(field))
            .forEach(
                field ->
                    events.add(
                        SimpleConditionEvent.violated(
                            field,
                            "%s has unsupported snapshot field %s of type %s"
                                .formatted(
                                    snapshot.getName(),
                                    field.getName(),
                                    field.getRawType().getName()))));
      }
    };
  }

  private static boolean isAllowedSnapshotField(final JavaField field) {
    final var type = field.getRawType();
    return isScalar(type)
        || (type.isAssignableTo(java.util.List.class)
            && field.getType() instanceof JavaParameterizedType parameterizedType
            && parameterizedType.getActualTypeArguments().size() == 1
            && isScalar(parameterizedType.getActualTypeArguments().getFirst().toErasure()));
  }
}

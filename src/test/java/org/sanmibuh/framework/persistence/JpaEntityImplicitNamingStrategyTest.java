package org.sanmibuh.framework.persistence;

import static org.assertj.core.api.BDDAssertions.then;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.junit.jupiter.api.Test;

class JpaEntityImplicitNamingStrategyTest {

  private final JpaEntityImplicitNamingStrategy sut = new JpaEntityImplicitNamingStrategy();

  @Test
  void should_removeJpaEntitySuffix_whenDerivingTableName() {
    then(tableNameOf(SampleJpaEntity.class)).isEqualTo("Sample");
  }

  @Test
  void should_keepEntityName_whenItHasNoJpaEntitySuffix() {
    then(tableNameOf(PlainThing.class)).isEqualTo("PlainThing");
  }

  private String tableNameOf(final Class<?> entityType) {
    final var registry =
        new StandardServiceRegistryBuilder()
            .applySetting("hibernate.connection.url", "jdbc:h2:mem:naming")
            .build();
    try {
      final var metadata =
          new MetadataSources(registry)
              .addAnnotatedClass(entityType)
              .getMetadataBuilder()
              .applyImplicitNamingStrategy(sut)
              .build();
      return metadata.getEntityBinding(entityType.getName()).getTable().getName();
    } finally {
      StandardServiceRegistryBuilder.destroy(registry);
    }
  }
}

@Entity
class SampleJpaEntity {
  @Id int id;
}

@Entity
class PlainThing {
  @Id int id;
}

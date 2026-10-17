package org.sanmibuh.framework.persistence;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.ImplicitEntityNameSource;
import org.springframework.boot.hibernate.SpringImplicitNamingStrategy;

public final class JpaEntityImplicitNamingStrategy extends SpringImplicitNamingStrategy {

  static final String ENTITY_SUFFIX = "JpaEntity";

  @Override
  public Identifier determinePrimaryTableName(final ImplicitEntityNameSource source) {
    final var entityName = transformEntityName(source.getEntityNaming());
    final var tableName =
        entityName.endsWith(ENTITY_SUFFIX)
            ? entityName.substring(0, entityName.length() - ENTITY_SUFFIX.length())
            : entityName;
    return toIdentifier(tableName, source.getBuildingContext());
  }
}

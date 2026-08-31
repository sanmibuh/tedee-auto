package org.sanmibuh.tedee.lock.infrastructure.secondary;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;
import org.jspecify.annotations.Nullable;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.type.filter.AssignableTypeFilter;

class TedeeBridgeRuntimeHints implements RuntimeHintsRegistrar {

  private static final String MODEL_PACKAGE = "com.tedee.bridge.client.model";
  private static final MemberCategory[] JACKSON_CATEGORIES = {
    MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS, MemberCategory.INVOKE_PUBLIC_METHODS
  };

  @Override
  public void registerHints(final RuntimeHints hints, final @Nullable ClassLoader classLoader) {
    var effectiveLoader = classLoader != null ? classLoader : getClass().getClassLoader();
    var scanner = new ClassPathScanningCandidateComponentProvider(false);
    scanner.setResourceLoader(new DefaultResourceLoader(effectiveLoader));
    scanner.addIncludeFilter(new AssignableTypeFilter(Object.class));
    scanner.findCandidateComponents(MODEL_PACKAGE).stream()
        .flatMap(bd -> loadWithDeclaredClasses(bd, effectiveLoader))
        .forEach(type -> hints.reflection().registerType(type, JACKSON_CATEGORIES));
  }

  private Stream<Class<?>> loadWithDeclaredClasses(
      final BeanDefinition bd, final ClassLoader loader) {
    try {
      var className =
          Objects.requireNonNull(bd.getBeanClassName(), "scanned component has no class name");
      var type = Class.forName(className, false, loader);
      return Stream.concat(Stream.of(type), Arrays.stream(type.getDeclaredClasses()));
    } catch (ClassNotFoundException e) {
      throw new IllegalStateException("Cannot load model class: " + bd.getBeanClassName(), e);
    }
  }
}

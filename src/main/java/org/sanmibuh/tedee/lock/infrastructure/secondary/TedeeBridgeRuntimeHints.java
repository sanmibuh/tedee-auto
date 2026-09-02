package org.sanmibuh.tedee.lock.infrastructure.secondary;

import java.util.Arrays;
import java.util.stream.Stream;
import org.jspecify.annotations.Nullable;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.util.ClassUtils;

final class TedeeBridgeRuntimeHints implements RuntimeHintsRegistrar {

  private static final String MODEL_PACKAGE = "com.tedee.bridge.client.model";
  private static final MemberCategory[] JACKSON_CATEGORIES = {
    MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS,
    MemberCategory.INVOKE_PUBLIC_METHODS,
    MemberCategory.ACCESS_DECLARED_FIELDS
  };

  @Override
  public void registerHints(final RuntimeHints hints, final @Nullable ClassLoader classLoader) {
    var scanner =
        setResourceLoaderPITEquivalent(
            new ClassPathScanningCandidateComponentProvider(false), classLoader);
    scanner.addIncludeFilter(new AssignableTypeFilter(Object.class));
    scanner.findCandidateComponents(MODEL_PACKAGE).stream()
        .flatMap(
            bd -> {
              var className = bd.getBeanClassName();
              return className != null
                  ? loadWithDeclaredClasses(className, classLoader)
                  : Stream.empty();
            })
        .forEach(type -> hints.reflection().registerType(type, JACKSON_CATEGORIES));
  }

  private ClassPathScanningCandidateComponentProvider setResourceLoaderPITEquivalent(
      final ClassPathScanningCandidateComponentProvider scanner,
      final @Nullable ClassLoader classLoader) {
    scanner.setResourceLoader(new DefaultResourceLoader(classLoader));
    return scanner;
  }

  private Stream<Class<?>> loadWithDeclaredClasses(
      final String className, final @Nullable ClassLoader loader) {
    var type = ClassUtils.resolveClassName(className, loader);
    return Stream.concat(Stream.of(type), Arrays.stream(type.getDeclaredClasses()));
  }
}

package io.github.cactacea.finagger.valuetypes.locator;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import io.github.cactacea.finagger.valuetypes.Value;
import io.github.cactacea.finagger.valuetypes.annotations.WrapsValueType;
import io.github.cactacea.finagger.valuetypes.StringValue;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ValueTypeLocator {


    public ArrayList<ValueTypeLocatorResult> getValueTypes(String packageName) {
        final List<URL> packageNames = ImmutableList.of(ClasspathHelper.forPackage(packageName),
                                                        ClasspathHelper.forPackage(Value.class.getPackage().getName()),
                                                        ClasspathHelper.forPackage(StringValue.class.getPackage().getName()))
                                                    .stream()
                                                    .flatMap(Collection::stream)
                                                    .collect(Collectors.toList());

        final ConfigurationBuilder config = ConfigurationBuilder.build().setUrls(packageNames);

        config.setInputsFilter(new FilterBuilder().includePackage(packageName)
                                                  .includePackage(Value.class)
                                                  .includePackage(StringValue.class));

        final Reflections reflections = new Reflections(config);

        final Set<Class<? extends Value>> subTypesOf = reflections.getSubTypesOf(Value.class);

        return Lists.newArrayList(subTypesOf
                                          .stream()
                                          .filter(i -> !Modifier.isAbstract(i.getModifiers()))
                                          .filter(i -> i.getSuperclass().getAnnotation(WrapsValueType.class) != null)
                                          .map(i -> {
                                              final Class wrappingValue = i.getSuperclass()
                                                                           .getAnnotation(WrapsValueType.class)
                                                                           .value();

                                              return new ValueTypeLocatorResult(i, wrappingValue);
                                          })
                                          .collect(Collectors.toList()));
    }
}

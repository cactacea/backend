package io.cactacea.finagger;

import io.swagger.util.PrimitiveType;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.stream.Collectors;

public class SwaggerTypeRegister {

    static public void setExternalTypes(Map<Class, PrimitiveType> externalTypes) {
        // ugly hack until swagger supports adding external classes as primitive types
        try {
            Field externalTypesField = PrimitiveType.class.getDeclaredField("EXTERNAL_CLASSES");
            Field modifiersField = Field.class.getDeclaredField("modifiers");

            modifiersField.setAccessible(true);
            externalTypesField.setAccessible(true);
            modifiersField.set(externalTypesField, externalTypesField.getModifiers() & ~Modifier.FINAL);

            Map<String, PrimitiveType> externalTypesInternal = externalTypes.entrySet().stream()
                    .collect(Collectors.toMap(e -> e.getKey().getName(), e -> e.getValue()));
            externalTypesField.set(null, externalTypesInternal);
        } catch (NoSuchFieldException|IllegalAccessException e) {
        }
    }
}
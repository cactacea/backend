package io.github.cactacea.finagger.valuetypes.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface WrapsValueType {
    Class<?> value();
}

package io.cactacea.finagger.valuetypes;

import com.fasterxml.jackson.annotation.JsonValue;

import javax.annotation.Nonnull;

/**
 * Indicates that the class has single value of specified type. Typically
 * implemented by data classes.
 *
 * @author Tomasz Guzik tomek@tguzik.com
 * @since 0.1
 */
public interface HasValue<T> {
    @JsonValue
    @Nonnull
    T get();
}

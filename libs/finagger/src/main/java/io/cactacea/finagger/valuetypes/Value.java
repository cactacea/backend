package io.cactacea.finagger.valuetypes;

import com.fasterxml.jackson.annotation.JsonValue;
import io.cactacea.finagger.valuetypes.adapters.xml.JaxbValueAdapter;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

import javax.annotation.Nonnull;

/**
 * Base abstract class for wrappers on values allowing to give them their own
 * type. This class and its subclasses are meant to be immutable by themselves -
 * not allowing to change the reference to held value. This class cannot give
 * any guarantees about the value itself.
 *
 * @author Tomasz Guzik tomek@tguzik.com
 * @see JaxbValueAdapter
 * @since 0.1
 */
@SuppressWarnings("WeakerAccess")
@EqualsAndHashCode
public abstract class Value<T> implements HasValue<T> {
    @Nonnull
    private final T encapsulatedValue;

    @SuppressWarnings("WeakerAccess")
    protected Value(@NonNull @Nonnull T encapsulatedValue) {
        this.encapsulatedValue = encapsulatedValue;
    }

    @JsonValue
    @Nonnull
    @Override
    public T get() {
        return encapsulatedValue;
    }

    @Override
    public String toString() {
        final T localValue = get();

        return localValue.toString();
    }
}

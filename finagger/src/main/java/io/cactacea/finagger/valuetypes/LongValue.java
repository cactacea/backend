package io.cactacea.finagger.valuetypes;

import io.cactacea.finagger.valuetypes.annotations.WrapsValueType;
import lombok.NonNull;

import javax.annotation.Nonnull;

@WrapsValueType(Long.class)
public abstract class LongValue extends Value<Long> implements Comparable<Value<Long>>, ValueTypeWrapper {
    protected LongValue(final Long encapsulatedValue) {
        super(encapsulatedValue);
    }

    @Override
    public int compareTo(@NonNull @Nonnull final Value<Long> other) {

        Long thisValue = get();
        Long otherValue = other.get();

        return thisValue.compareTo(otherValue);
    }
}

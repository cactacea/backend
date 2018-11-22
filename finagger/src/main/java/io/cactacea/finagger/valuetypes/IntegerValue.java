package io.cactacea.finagger.valuetypes;

import io.cactacea.finagger.valuetypes.annotations.WrapsValueType;
import lombok.NonNull;

import javax.annotation.Nonnull;

@WrapsValueType(Integer.class)
public abstract class IntegerValue extends Value<Integer> implements Comparable<Value<Integer>>, ValueTypeWrapper {
    protected IntegerValue(final Integer encapsulatedValue) {
        super(encapsulatedValue);
    }

    @Override
    public int compareTo(@NonNull @Nonnull final Value<Integer> other) {

        Integer thisValue = get();
        Integer otherValue = other.get();

        return thisValue.compareTo(otherValue);
    }
}

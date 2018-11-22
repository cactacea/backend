package io.cactacea.finagger.valuetypes;

import io.cactacea.finagger.valuetypes.annotations.WrapsValueType;
import lombok.NonNull;

import javax.annotation.Nonnull;

@WrapsValueType(Double.class)
public abstract class DoubleValue extends Value<Double> implements Comparable<Value<Double>>, ValueTypeWrapper {
    protected DoubleValue(final Double encapsulatedValue) {
        super(encapsulatedValue);
    }

    @Override
    public int compareTo(@NonNull @Nonnull final Value<Double> other) {

        Double thisValue = get();
        Double otherValue = other.get();

        return thisValue.compareTo(otherValue);
    }
}

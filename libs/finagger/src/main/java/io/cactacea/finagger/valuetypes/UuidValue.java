package io.cactacea.finagger.valuetypes;

import io.cactacea.finagger.valuetypes.annotations.WrapsValueType;
import lombok.NonNull;

import javax.annotation.Nonnull;
import java.util.UUID;

@WrapsValueType(UUID.class)
public abstract class UuidValue extends Value<UUID> implements Comparable<Value<UUID>>, ValueTypeWrapper {
    protected UuidValue(@NonNull @Nonnull final UUID encapsulatedValue) {
        super(encapsulatedValue);
    }

    @Override
    public int compareTo(@NonNull @Nonnull final Value<UUID> other) {

        UUID thisValue = get();
        UUID otherValue = other.get();

        return thisValue.compareTo(otherValue);
    }
}
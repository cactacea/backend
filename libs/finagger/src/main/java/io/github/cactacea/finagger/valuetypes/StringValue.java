package io.github.cactacea.finagger.valuetypes;

import com.google.common.base.CharMatcher;
import io.github.cactacea.finagger.valuetypes.annotations.WrapsValueType;
import lombok.NonNull;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

@Immutable
@WrapsValueType(String.class)
public abstract class StringValue extends Value<String> implements Comparable<Value<String>>, ValueTypeWrapper {
    protected StringValue(@NonNull @Nonnull final String value) {
        super(value);
    }

    public int length() {
        return get().length();
    }

    public boolean isEmpty() {
        return length() == 0;
    }

    public boolean isBlank() {
        return isEmpty() || CharMatcher.WHITESPACE.matchesAllOf(get());
    }

    public int indexOf(String str) {
        return this.get().indexOf(str);
    }

    public String[] split(String str) {
        return this.get().split(str);
    }

    public String substring(int beginIndex) {
        return this.get().substring(beginIndex);
    }

    @Override
    public int compareTo(@NonNull @Nonnull Value<String> other) {
        String thisValue = get();
        String otherValue = other.get();

        return thisValue.compareTo(otherValue);
    }
}
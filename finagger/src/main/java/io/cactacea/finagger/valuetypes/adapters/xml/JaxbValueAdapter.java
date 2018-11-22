package io.cactacea.finagger.valuetypes.adapters.xml;

import io.cactacea.finagger.valuetypes.Value;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.adapters.XmlAdapter;


/**
 * JaxB adapter template for value classes in this package. The user is expected
 * to subclass one of the descendants of this class, implementing method
 * {@link #createNewInstance(Object)}. The method is expected to create new
 * instance of the value class of expected type.
 *
 * @author Tomasz Guzik tomek@tguzik.com
 * @since 0.2
 */
public abstract class JaxbValueAdapter<UnderlyingType, ValueClass extends Value<UnderlyingType>>
        extends XmlAdapter<UnderlyingType, ValueClass> {

    @Override
    @Nullable
    public ValueClass unmarshal(@Nullable UnderlyingType value) throws Exception {
        return createNewInstance(value);
    }

    @Override
    @Nullable
    public UnderlyingType marshal(@Nullable ValueClass valueClass) throws Exception {
        if (valueClass == null) {
            return null;
        }

        return valueClass.get();
    }

    /**
     * Creates new instance of correct value class with argument as the
     * contained value. It is not recommended for implementations of this method
     * to return `null` values.
     * @param value the underlying type
     * @return the value type
     */
    @Nullable
    protected abstract ValueClass createNewInstance(@Nullable UnderlyingType value);
}

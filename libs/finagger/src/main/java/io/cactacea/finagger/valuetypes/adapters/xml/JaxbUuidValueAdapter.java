package io.cactacea.finagger.valuetypes.adapters.xml;

import io.cactacea.finagger.valuetypes.UuidValue;

import java.util.UUID;

public abstract class JaxbUuidValueAdapter<ValueClass extends UuidValue> extends JaxbValueAdapter<UUID, ValueClass> { }

package io.github.cactacea.finagger.valuetypes.adapters.xml;

import io.github.cactacea.finagger.valuetypes.UuidValue;

import java.util.UUID;

public abstract class JaxbUuidValueAdapter<ValueClass extends UuidValue> extends JaxbValueAdapter<UUID, ValueClass> { }

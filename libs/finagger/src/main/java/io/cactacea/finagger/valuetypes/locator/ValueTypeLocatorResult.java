package io.cactacea.finagger.valuetypes.locator;

import lombok.Data;

@Data
public class ValueTypeLocatorResult {
    private final Class valueType;
    private final Class wrapsValueType;
}
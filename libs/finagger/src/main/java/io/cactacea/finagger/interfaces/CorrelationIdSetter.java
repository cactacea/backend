package io.cactacea.finagger.interfaces;

import java.util.UUID;

public interface CorrelationIdSetter {
    void setCorrelationId(UUID correlationId);
}

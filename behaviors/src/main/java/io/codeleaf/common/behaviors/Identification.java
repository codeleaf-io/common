package io.codeleaf.common.behaviors;

import java.security.Principal;
import java.security.PublicKey;
import java.util.UUID;

public interface Identification extends ValueObject {

    Principal getPrincipal();

    PublicKey getPublicKey();

    UUID getUuid();

}

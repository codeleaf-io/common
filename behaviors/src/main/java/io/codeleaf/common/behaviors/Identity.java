package io.codeleaf.common.behaviors;

import java.security.PrivateKey;

public interface Identity extends Identification {

    PrivateKey getPrivateKey();

    Identification getPublicId();

}

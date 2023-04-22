package io.codeleaf.common.behaviors;

import java.net.URI;
import java.security.Principal;
import java.security.PublicKey;
import java.util.UUID;

public interface Identification extends ValueObject {

    /**
     * Returns the name of a service
     * @return the name of a service
     */
    Principal getPrincipal();

    /**
     * Returns the public key of the service, or null
     * @return the public key of the service, or null
     */
    PublicKey getPublicKey();

    /**
     * Returns a unique uuid. This allows for multiple worker nodes to be the same principal,
     * and we can still separate them between each other.
     * @return a unique uuid
     */
    UUID getInstanceId();

    /**
     * Returns the URI of the instance of this service
     * @return the URI
     */
    URI getURI();
}

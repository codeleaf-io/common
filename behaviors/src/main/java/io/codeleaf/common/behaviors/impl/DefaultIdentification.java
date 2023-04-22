package io.codeleaf.common.behaviors.impl;

import io.codeleaf.common.behaviors.Identification;

import java.net.URI;
import java.security.Principal;
import java.security.PublicKey;
import java.util.Objects;
import java.util.UUID;

public class DefaultIdentification implements Identification {

    private final Principal principal;
    private final PublicKey publicKey;
    private final UUID uuid;
    private final URI uri;

    public DefaultIdentification(Principal principal, PublicKey publicKey, UUID uuid, URI uri) {
        this.principal = principal;
        this.publicKey = publicKey;
        this.uuid = uuid;
        this.uri = uri;
    }

    @Override
    public Principal getPrincipal() {
        return principal;
    }

    @Override
    public PublicKey getPublicKey() {
        return publicKey;
    }

    @Override
    public UUID getInstanceId() {
        return uuid;
    }

    @Override
    public URI getURI() {
        return uri;
    }

    @Override
    public String toString() {
        return String.format("%s(%s)", principal.getName(), uuid);
    }

    @Override
    public boolean equals(final Object object) {
        return object == this
                || null != object &&
                (object instanceof Identification
                        && Objects.equals(principal, ((Identification) object).getPrincipal())
                        && Objects.equals(publicKey, ((Identification) object).getPublicKey()));
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(principal) * 31 + Objects.hashCode(publicKey) * 47 + 19;
    }

}

package io.codeleaf.common.behaviors.impl;

import io.codeleaf.common.behaviors.Identification;

import java.security.Principal;
import java.security.PublicKey;
import java.util.Objects;
import java.util.UUID;

public class DefaultIdentification implements Identification {

    private final Principal principal;
    private final PublicKey publicKey;
    private final UUID uuid;

    public DefaultIdentification(Principal principal, PublicKey publicKey, UUID uuid) {
        this.principal = principal;
        this.publicKey = publicKey;
        this.uuid = uuid;
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
    public UUID getUuid() {
        return uuid;
    }

    @Override
    public String toString() {
        return String.format("Identification{%s, %s}", principal, uuid);
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

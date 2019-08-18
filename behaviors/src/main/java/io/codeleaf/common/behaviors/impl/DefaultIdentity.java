package io.codeleaf.common.behaviors.impl;

import io.codeleaf.common.behaviors.Identification;
import io.codeleaf.common.behaviors.Identity;

import java.security.Principal;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Objects;
import java.util.UUID;

public class DefaultIdentity implements Identity {

    private final Identification identification;
    private final PrivateKey privateKey;

    public DefaultIdentity(Identification identification, PrivateKey privateKey) {
        this.identification = identification;
        this.privateKey = privateKey;
    }

    @Override
    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    @Override
    public Principal getPrincipal() {
        return identification.getPrincipal();
    }

    @Override
    public PublicKey getPublicKey() {
        return identification.getPublicKey();
    }

    @Override
    public UUID getUuid() {
        return identification.getUuid();
    }

    @Override
    public Identification getPublicId() {
        return identification;
    }

    @Override
    public String toString() {
        return String.format("Identity{%s, %s}", identification.getPrincipal(), identification.getUuid());
    }

    @Override
    public boolean equals(final Object object) {
        return object == this
                || null != object &&
                (object instanceof Identity
                        && Objects.equals(getPublicId(), ((Identity) object).getPublicId()));
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getPublicId()) * 31 + 11;
    }

}

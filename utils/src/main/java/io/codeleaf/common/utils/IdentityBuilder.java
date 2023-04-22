package io.codeleaf.common.utils;

import io.codeleaf.common.behaviors.Identity;
import io.codeleaf.common.behaviors.impl.DefaultIdentification;
import io.codeleaf.common.behaviors.impl.DefaultIdentity;
import io.codeleaf.common.behaviors.impl.DefaultPrincipal;

import java.net.URI;
import java.security.KeyPair;
import java.security.Principal;
import java.util.Objects;
import java.util.UUID;

public final class IdentityBuilder {

    private Principal principal;
    private UUID instanceId;
    private URI uri;
    private KeyPair keyPair;

    public IdentityBuilder withPrincipal(Principal principal) {
        Objects.requireNonNull(principal);
        Objects.requireNonNull(principal.getName());
        if (principal.getName().isBlank()) {
            throw new IllegalArgumentException("Principal name can not be blank!");
        }
        this.principal = principal;
        return this;
    }

    public IdentityBuilder withName(String name) {
        Objects.requireNonNull(name);
        if (name.isBlank()) {
            throw new IllegalArgumentException("name can not be blank!");
        }
        this.principal = new DefaultPrincipal(name);
        return this;
    }

    public IdentityBuilder withURI(URI uri) {
        Objects.requireNonNull(uri);
        this.uri = uri;
        return this;
    }

    public IdentityBuilder withKeyPair(KeyPair keyPair) {
        Objects.requireNonNull(keyPair);
        Objects.requireNonNull(keyPair.getPrivate());
        Objects.requireNonNull(keyPair.getPublic());
        this.keyPair = keyPair;
        return this;
    }

    public IdentityBuilder withInstanceId(UUID instanceId) {
        Objects.requireNonNull(instanceId);
        this.instanceId = instanceId;
        return this;
    }

    public Identity build() {
        if (instanceId == null) {
            instanceId = UUID.randomUUID();
        }
        if (principal == null) {
            principal = new DefaultPrincipal(instanceId.toString());
        }
        if (uri == null) {
            uri = URI.create("urn:id:" + principal.getName());
        }
        if (keyPair == null) {
            keyPair = RSA.createKeyPair();
        }
        return new DefaultIdentity(
                new DefaultIdentification(principal, keyPair.getPublic(), instanceId, uri),
                keyPair.getPrivate());
    }
}

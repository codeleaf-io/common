package io.codeleaf.common.behaviors.impl;

import java.security.Principal;

public class DefaultPrincipal implements Principal {

    private final String name;

    public DefaultPrincipal(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}

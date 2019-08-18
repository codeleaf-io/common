package io.codeleaf.common.behaviors;

import java.io.Serializable;

/**
 * This interface represents a ValueObject. A ValueObject determines its equality based on the values.
 * This means that the equals and hashCode method need to be implemented to work correctly.
 */
public interface ValueObject extends Serializable {

    @Override
    boolean equals(Object object);

    @Override
    int hashCode();

    @Override
    String toString();

}
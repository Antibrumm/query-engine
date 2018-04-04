package ch.mfrey.jpa.query.builder;

import java.beans.PropertyDescriptor;

public class SimpleJoinBuilder {
    boolean supports(PropertyDescriptor pd) {
        return true;
    }
}

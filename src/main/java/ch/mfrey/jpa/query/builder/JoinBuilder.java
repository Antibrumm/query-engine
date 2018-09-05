package ch.mfrey.jpa.query.builder;

import java.beans.PropertyDescriptor;

public interface JoinBuilder {

    boolean supports(String link, PropertyDescriptor pd);

    StringBuilder buildJoin(String link, PropertyDescriptor pd, String synonym, String nextSynonym);

}

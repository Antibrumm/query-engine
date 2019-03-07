package ch.mfrey.jpa.query.builder;

import ch.mfrey.bean.ad.BeanPropertyDescriptor;

public interface JoinBuilder {

    boolean supports(String link, BeanPropertyDescriptor pd);

    StringBuilder buildJoin(String link, BeanPropertyDescriptor pd, String synonym, String nextSynonym);

}

package ch.mfrey.jpa.query.builder;

import ch.mfrey.bean.ad.BeanPropertyDescriptor;
import ch.mfrey.jpa.query.definition.AbstractCriteriaDefinition;

public class SimpleJoinBuilder implements JoinBuilder {

    @Override
    public StringBuilder buildJoin(String link, BeanPropertyDescriptor pd, String synonym, String nextSynonym) {
        return new StringBuilder().append(synonym)
                .append(AbstractCriteriaDefinition.QUERY_APPEND_DOT)
                .append(pd.getName())
                .append(AbstractCriteriaDefinition.QUERY_APPEND_SPACE)
                .append(nextSynonym);
    }

    @Override
    public boolean supports(String link, BeanPropertyDescriptor pd) {
        return link.equals(pd.getName());
    }
}

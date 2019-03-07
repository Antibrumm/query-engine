package ch.mfrey.jpa.query.definition;

import java.util.List;

import ch.mfrey.bean.ad.BeanPropertyDescriptor;
import ch.mfrey.jpa.query.model.Criteria;
import ch.mfrey.jpa.query.model.Query;

public interface CriteriaDefinition<CRITERIA extends Criteria<?>> {

    List<String> getOperators();

    String getCriteriaKey();
 
    List<BeanPropertyDescriptor> getBeanPropertyDescriptors();
 
    StringBuilder getRestriction(Query<?> query, CRITERIA criteria);

    boolean isTerminal();

}

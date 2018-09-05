package ch.mfrey.jpa.query.definition;

import java.beans.PropertyDescriptor;
import java.util.List;

import ch.mfrey.jpa.query.model.Criteria;
import ch.mfrey.jpa.query.model.Query;

public interface CriteriaDefinition<CRITERIA extends Criteria<?>> {

    List<String> getOperators();

    String getCriteriaKey();
 
    List<PropertyDescriptor> getPropertyDescriptors();
 
    StringBuilder getRestriction(Query<?> query, CRITERIA criteria);

    boolean isTerminal();

}

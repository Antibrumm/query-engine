package ch.mfrey.jpa.query.definition;

import java.util.Arrays;
import java.util.List;

import ch.mfrey.bean.ad.AccessorDescriptor;
import ch.mfrey.jpa.query.model.CriteriaSimple;
import ch.mfrey.jpa.query.model.Query;

public class CriteriaDefinitionNumber extends AbstractCriteriaDefinition<CriteriaSimple<Number>> {

    public CriteriaDefinitionNumber(AccessorDescriptor accessorDescriptor) {
        super(accessorDescriptor);
    }

    @Override
    public StringBuilder getRestriction(Query<?> query, CriteriaSimple<Number> criteria) {

        return new StringBuilder().append(getSynonym())
                .append(QUERY_APPEND_DOT).append(getResultDescriptor().getName())
                .append(QUERY_APPEND_SPACE).append(criteria.getOperator()).append(QUERY_APPEND_SPACE)
                .append(CRITERIA_PARAMETER);
    }

    @Override
    public List<String> getOperators() {
        return Arrays.asList("=", "!=", "<", "<=", ">=", ">");
    }

}

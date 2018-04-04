package ch.mfrey.jpa.query.definition;

import java.util.Arrays;
import java.util.List;

import ch.mfrey.bean.ad.AccessorDescriptor;
import ch.mfrey.jpa.query.model.Criteria;
import ch.mfrey.jpa.query.model.Query;

public class CriteriaDefinitionNumber<E extends Criteria<? extends Number>> extends AbstractCriteriaDefinition<E> {

    public CriteriaDefinitionNumber(AccessorDescriptor accessorDescriptor) {
        super(accessorDescriptor);
    }

    @Override
    public void applyRestriction(StringBuilder restrictionsPart, Query query, E criteria, int position) {

        StringBuilder restriction = new StringBuilder().append(getSynonym()) // $NON-NLS-1$
                .append(QUERY_APPEND_DOT).append(getResultDescriptor().getName())
                .append(QUERY_APPEND_SPACE).append(criteria.getOperator()).append(QUERY_APPEND_SPACE)
                .append(CRITERIA_PARAMETER);
        restrictionsPart.append(replacePosition(restriction, position));
    }

    @Override
    public List<String> getOperators() {
        return Arrays.asList("=", "!=", "<", "<=", "=>", ">");
    }

}

package ch.mfrey.jpa.query.definition;

import java.util.Arrays;
import java.util.List;

import ch.mfrey.bean.ad.AccessorDescriptor;
import ch.mfrey.jpa.query.model.CriteriaSimple;
import ch.mfrey.jpa.query.model.Query;

public class CriteriaDefinitionString extends AbstractCriteriaDefinition<CriteriaSimple<String>> {

    public CriteriaDefinitionString(AccessorDescriptor accessorDescriptor) {
        super(accessorDescriptor);
    }

    /**
     * Expression for fetching the string value from the criteria and use it with ignorecase.
     */
    public static final String CRITERIA_UPPER_STRING_VALUE = "upper({#query.criterias[|POSITION|].parameter})"; //$NON-NLS-1$

    @Override
    public StringBuilder getRestriction(Query<?> query, CriteriaSimple<String> criteria) {
        StringBuilder restriction;
        if (criteria.getParameter().indexOf('%') != -1) {
            switch (criteria.getOperator()) {
                case "=":
                    restriction = new StringBuilder().append("upper(").append(getSynonym()) //$NON-NLS-1$
                            .append(QUERY_APPEND_DOT).append(getResultDescriptor().getName())
                            .append(')')
                            .append(" LIKE ")
                            .append(CRITERIA_UPPER_STRING_VALUE);
                    break;
                case "!=":
                    restriction = new StringBuilder().append("upper(").append(getSynonym()) //$NON-NLS-1$
                            .append(QUERY_APPEND_DOT).append(getResultDescriptor().getName())
                            .append(')')
                            .append(" NOT LIKE ")
                            .append(CRITERIA_UPPER_STRING_VALUE);
                    break;
                default:
                    throw new OperatorNotHandledException(this, criteria);
            }
        } else {
            restriction = new StringBuilder().append("upper(").append(getSynonym()) //$NON-NLS-1$
                    .append(QUERY_APPEND_DOT).append(getResultDescriptor().getName())
                    .append(')')
                    .append(QUERY_APPEND_SPACE).append(criteria.getOperator()).append(QUERY_APPEND_SPACE)
                    .append(CRITERIA_UPPER_STRING_VALUE);
        }
        return restriction;
    }

    @Override
    public List<String> getOperators() {
        return Arrays.asList("=", "!=");
    }

}

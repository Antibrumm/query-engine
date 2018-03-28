package ch.mfrey.jpa.query.definition;

import java.util.Arrays;
import java.util.List;

import ch.mfrey.jpa.query.model.CriteriaString;

public class CriteriaDefinitionString extends AbstractCriteriaDefinition<CriteriaString> {

    /**
     * Expression for fetching the string value from the criteria and use it with ignorecase.
     */
    public static final String CRITERIA_UPPER_STRING_VALUE = "upper({#query.criterias[{0}].parameter})"; //$NON-NLS-1$

    @Override
    public void applyRestriction(StringBuilder restrictionsPart, CriteriaString criteria, int position) {

        StringBuilder restriction;
        if (criteria.getParameter().indexOf('%') != -1) {
            switch (criteria.getOperator()) {
                case "=":
                    restriction = new StringBuilder().append("upper(").append(getSynonym()) //$NON-NLS-1$
                            .append(QUERY_APPEND_DOT).append(getAccessorDescriptor().getResultDescriptor().getName())
                            .append(')')
                            .append(" LIKE ")
                            .append(CRITERIA_UPPER_STRING_VALUE);
                case "!=":
                    restriction = new StringBuilder().append("upper(").append(getSynonym()) //$NON-NLS-1$
                            .append(QUERY_APPEND_DOT).append(getAccessorDescriptor().getResultDescriptor().getName())
                            .append(')')
                            .append(" NOT LIKE ")
                            .append(CRITERIA_UPPER_STRING_VALUE);
                    break;
                default:
                    throw new OperatorNotHandledException(this, criteria);
            }
        } else {
            restriction = new StringBuilder().append("upper(").append(getSynonym()) //$NON-NLS-1$
                    .append(QUERY_APPEND_DOT).append(getAccessorDescriptor().getResultDescriptor().getName())
                    .append(')')
                    .append(QUERY_APPEND_SPACE).append(criteria.getOperator()).append(QUERY_APPEND_SPACE)
                    .append(CRITERIA_UPPER_STRING_VALUE);
        }
        restrictionsPart.append(replacePosition(restriction, position));
    }

    @Override
    public List<String> getOperators() {
        return Arrays.asList("=", "!=");
    }

}

package ch.mfrey.jpa.query.definition;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import ch.mfrey.jpa.query.model.CriteriaDate;

public class CriteriaDefinitionDate extends AbstractCriteriaDefinition<CriteriaDate> {

    /** The Constant CRITERIA_DATE_EQUAL_PARAMETER. */
    public static final String CRITERIA_DATE_TIME_MAX_PARAMETER =
            "{#query.criterias[{0}].maxDate}"; //$NON-NLS-1$

    /** The Constant CRITERIA_DATE_EQUAL_PARAMETER. */
    public static final String CRITERIA_DATE_TIME_MIN_PARAMETER =
            "{#query.criterias[{0}].minDate}"; //$NON-NLS-1$

    /** The Constant CRITERIA_DATE_EQUAL_PARAMETER. */
    public static final String CRITERIA_DATE_TIME_BETWEEN_PARAMETER =
            " BETWEEN " + CRITERIA_DATE_TIME_MIN_PARAMETER + " AND " + CRITERIA_DATE_TIME_MAX_PARAMETER; //$NON-NLS-1$

    @Override
    public void applyRestriction(StringBuilder restrictionsPart, CriteriaDate criteria, int position) {

        StringBuilder restriction;
        if (LocalDateTime.class.isAssignableFrom(
                getAccessorDescriptor().getResultDescriptor().getPropertyType())) {
            switch (criteria.getOperator()) {
                case "=":
                    restriction = new StringBuilder().append(getSynonym()).append(QUERY_APPEND_DOT)
                            .append(getAccessorDescriptor().getResultDescriptor().getName())
                            .append(CRITERIA_DATE_TIME_BETWEEN_PARAMETER);
                    break;
                case "!=":
                    restriction = new StringBuilder().append(getSynonym()).append(QUERY_APPEND_DOT)
                            .append(getAccessorDescriptor().getResultDescriptor().getName())
                            .append(" NOT ").append(CRITERIA_DATE_TIME_BETWEEN_PARAMETER);
                    break;
                case "<":
                case "<=":
                    restriction = new StringBuilder().append(getSynonym()).append(QUERY_APPEND_DOT)
                            .append(getAccessorDescriptor().getResultDescriptor().getName())
                            .append(QUERY_APPEND_SPACE).append(criteria.getOperator()).append(QUERY_APPEND_SPACE)
                            .append(CRITERIA_DATE_TIME_MIN_PARAMETER);
                    break;
                case ">":
                case ">=":
                    restriction = new StringBuilder().append(getSynonym()).append(QUERY_APPEND_DOT)
                            .append(getAccessorDescriptor().getResultDescriptor().getName())
                            .append(QUERY_APPEND_SPACE).append(criteria.getOperator()).append(QUERY_APPEND_SPACE)
                            .append(CRITERIA_DATE_TIME_MAX_PARAMETER);
                    break;
                default:
                    throw new OperatorNotHandledException(this, criteria);
            }
        } else {
            restriction = new StringBuilder().append(getSynonym()).append(QUERY_APPEND_DOT)
                    .append(getAccessorDescriptor().getResultDescriptor().getName())
                    .append(QUERY_APPEND_SPACE).append(criteria.getOperator()).append(QUERY_APPEND_SPACE)
                    .append(CRITERIA_PARAMETER);
        }
        restrictionsPart.append(replacePosition(restriction, position));
    }

    @Override
    public List<String> getOperators() {
        return Arrays.asList("=", "!=", "<", "<=", ">=", ">");
    }

}

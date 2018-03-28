package ch.mfrey.jpa.query.definition;

import java.util.Arrays;
import java.util.List;

import ch.mfrey.jpa.query.model.CriteriaString;

public class CriteriaDefinitionEnum extends AbstractCriteriaDefinition<CriteriaString> {

    @Override
    public void applyRestriction(StringBuilder restrictionsPart, CriteriaString criteria, int position) {

        StringBuilder restriction = new StringBuilder().append(getSynonym()).append(QUERY_APPEND_DOT)
                .append(getAccessorDescriptor().getResultDescriptor().getName()) // $NON-NLS-1$
                .append(QUERY_APPEND_SPACE).append(criteria.getOperator())
                .append(" {T(")
                .append(getAccessorDescriptor().getResultDescriptor().getPropertyType().getName())
                .append(").parameterOf(")
                .append(CRITERIA_PARAMETER.substring(2, CRITERIA_PARAMETER.length() - 2))
                .append(")} ");

        restrictionsPart.append(replacePosition(restriction, position));
    }

    @Override
    public List<String> getOperators() {
        return Arrays.asList("=", "!=");
    }

}

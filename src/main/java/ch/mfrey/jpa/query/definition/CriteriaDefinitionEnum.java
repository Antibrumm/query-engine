package ch.mfrey.jpa.query.definition;

import java.util.Arrays;
import java.util.List;

import ch.mfrey.bean.ad.AccessorDescriptor;
import ch.mfrey.jpa.query.model.CriteriaString;
import ch.mfrey.jpa.query.model.Query;

public class CriteriaDefinitionEnum extends AbstractCriteriaDefinition<CriteriaString> {

    public CriteriaDefinitionEnum(AccessorDescriptor accessorDescriptor) {
        super(accessorDescriptor);
    }

    @Override
    public void applyRestriction(StringBuilder restrictionsPart, Query query, CriteriaString criteria, int position) {

        StringBuilder restriction = new StringBuilder().append(getSynonym()).append(QUERY_APPEND_DOT)
                .append(getResultDescriptor().getName()) // $NON-NLS-1$
                .append(QUERY_APPEND_SPACE).append(criteria.getOperator())
                .append(" {T(")
                .append(getResultDescriptor().getPropertyType().getName())
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

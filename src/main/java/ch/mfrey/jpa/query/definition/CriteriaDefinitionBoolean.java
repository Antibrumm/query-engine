package ch.mfrey.jpa.query.definition;

import java.util.Arrays;
import java.util.List;

import ch.mfrey.jpa.query.model.CriteriaBoolean;
import ch.mfrey.jpa.query.model.Query;

public class CriteriaDefinitionBoolean extends AbstractCriteriaDefinition<CriteriaBoolean> {

    @Override
    public void applyRestriction(StringBuilder restrictionsPart, Query query, CriteriaBoolean criteria, int position) {
        StringBuilder restriction = new StringBuilder().append(getSynonym()).append(QUERY_APPEND_DOT)
                .append(getAccessorDescriptor().getResultDescriptor().getName())
                .append(QUERY_APPEND_SPACE).append(criteria.getOperator()).append(QUERY_APPEND_SPACE)
                .append(CRITERIA_PARAMETER);
        restrictionsPart.append(replacePosition(restriction, position));
    }

    @Override
    public List<String> getOperators() {
        return Arrays.asList("=", "!=");
    }

}

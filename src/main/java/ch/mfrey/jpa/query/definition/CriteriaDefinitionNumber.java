package ch.mfrey.jpa.query.definition;

import java.util.Arrays;
import java.util.List;

import ch.mfrey.jpa.query.model.CriteriaNumber;

public class CriteriaDefinitionNumber extends AbstractCriteriaDefinition<CriteriaNumber> {

    @Override
    public void applyRestriction(StringBuilder restrictionsPart, CriteriaNumber criteria, int position) {

        StringBuilder restriction = new StringBuilder().append(getSynonym()) // $NON-NLS-1$
                .append(QUERY_APPEND_DOT).append(getAccessorDescriptor().getResultDescriptor().getName())
                .append(QUERY_APPEND_SPACE).append(criteria.getOperator()).append(QUERY_APPEND_SPACE)
                .append(CRITERIA_PARAMETER);
        restrictionsPart.append(replacePosition(restriction, position));
    }

    @Override
    public List<String> getOperators() {
        return Arrays.asList("=", "!=", "<", "<=", "=>", ">");
    }

}

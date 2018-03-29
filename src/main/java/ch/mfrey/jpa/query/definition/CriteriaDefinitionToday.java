package ch.mfrey.jpa.query.definition;

import java.util.Arrays;
import java.util.List;

import ch.mfrey.jpa.query.model.CriteriaToday;
import ch.mfrey.jpa.query.model.Query;

public class CriteriaDefinitionToday extends AbstractCriteriaDefinition<CriteriaToday> {

    @Override
    public void applyRestriction(StringBuilder restrictionsPart, Query query, CriteriaToday criteria, int position) {

        throw new IllegalArgumentException("TODO");
    }

    @Override
    public List<String> getOperators() {
        return Arrays.asList("=", "!=", "<", "<=", ">=", ">");
    }
}

package ch.mfrey.jpa.query.definition;

import java.util.List;

import ch.mfrey.jpa.query.model.Criteria;

public interface CriteriaDefinition<CRITERIA extends Criteria<?>> {

    List<String> getOperators();

    String getPropertyAccessor();

    void applyJoins(StringBuilder joinsPart, List<String> appliedJoins, int position);

    List<String> getJoins();

    void applyRestriction(StringBuilder restrictionsPart, CRITERIA criteria, int position);

    boolean isTerminal();

}

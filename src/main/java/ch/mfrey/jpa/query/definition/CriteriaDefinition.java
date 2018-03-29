package ch.mfrey.jpa.query.definition;

import java.util.List;

import ch.mfrey.jpa.query.model.Criteria;
import ch.mfrey.jpa.query.model.Query;

public interface CriteriaDefinition<CRITERIA extends Criteria<?>> {

    List<String> getOperators();

    String getPropertyAccessor();

    void applyJoins(StringBuilder joinsPart, List<String> appliedJoins, Query query, CRITERIA criteria, int position);

    void applyRestriction(StringBuilder restrictionsPart, Query query, CRITERIA criteria, int position);

    boolean isTerminal();

}

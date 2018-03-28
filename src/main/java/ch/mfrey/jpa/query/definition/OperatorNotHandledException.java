package ch.mfrey.jpa.query.definition;

import ch.mfrey.jpa.query.model.Criteria;

public class OperatorNotHandledException extends RuntimeException {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public OperatorNotHandledException(CriteriaDefinition<?> criteriaDefinition, Criteria<?> criteria) {
        super("Operator not handled in " + criteriaDefinition.getPropertyAccessor() + " : " + criteria.getOperator());
    }

}

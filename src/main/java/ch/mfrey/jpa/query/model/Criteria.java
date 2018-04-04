package ch.mfrey.jpa.query.model;

public interface Criteria<PARAMETER_TYPE> {

    String getOperator();

    PARAMETER_TYPE getParameter();

    String getCriteriaKey();

    String getLinkOperator();

    boolean isBracketOpen();

    boolean isBracketClose();

}

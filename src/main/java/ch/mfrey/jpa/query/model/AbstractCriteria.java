package ch.mfrey.jpa.query.model;

public abstract class AbstractCriteria<PARAMETER_TYPE> implements Criteria<PARAMETER_TYPE> {

    private boolean bracketClose = false;

    private boolean bracketOpen = false;

    private Class<?> entityClass;

    private String propertyAccessor;

    /** The link operator. */
    private String linkOperator = "AND"; //$NON-NLS-1$

    /** The operator. */
    private String operator = "="; //$NON-NLS-1$

    private PARAMETER_TYPE parameter;

    public boolean isBracketClose() {
        return bracketClose;
    }

    public void setBracketClose(boolean bracketClose) {
        this.bracketClose = bracketClose;
    }

    public boolean isBracketOpen() {
        return bracketOpen;
    }

    public void setBracketOpen(boolean bracketOpen) {
        this.bracketOpen = bracketOpen;
    }

    public Class<?> getEntityClass() {
        return entityClass;
    }

    public void setEntityClass(Class<?> entityClass) {
        this.entityClass = entityClass;
    }

    public String getPropertyAccessor() {
        return propertyAccessor;
    }

    public void setPropertyAccessor(String propertyAccessor) {
        this.propertyAccessor = propertyAccessor;
    }

    public String getLinkOperator() {
        return linkOperator;
    }

    public void setLinkOperator(String linkOperator) {
        this.linkOperator = linkOperator;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public PARAMETER_TYPE getParameter() {
        return parameter;
    }

    public void setParameter(PARAMETER_TYPE parameter) {
        this.parameter = parameter;
    }

}

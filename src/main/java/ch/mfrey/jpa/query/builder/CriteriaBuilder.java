package ch.mfrey.jpa.query.builder;

import java.time.LocalDate;

import ch.mfrey.jpa.query.model.AbstractCriteria;
import ch.mfrey.jpa.query.model.CriteriaDate;
import ch.mfrey.jpa.query.model.CriteriaSimple;

public class CriteriaBuilder<E, PARAMETER_TYPE, CRITERIA_TYPE extends AbstractCriteria<PARAMETER_TYPE>> {

    private QueryBuilder<E> queryBuilder;
    private CRITERIA_TYPE criteria;

    private CriteriaBuilder(QueryBuilder<E> queryBuilder, CRITERIA_TYPE criteria, String criteriaKey,
            PARAMETER_TYPE parameter) {
        this.queryBuilder = queryBuilder;
        this.criteria = criteria;
        this.criteria.setCriteriaKey(criteriaKey);
        this.criteria.setParameter(parameter);
        this.queryBuilder.getQuery().getCriterias().add(this.criteria);
    }

    public QueryBuilder<E> and() {
        return queryBuilder;
    }

    public CriteriaBuilder<E, PARAMETER_TYPE, CRITERIA_TYPE> withBracketClose(boolean bracketClose) {
        criteria.setBracketClose(bracketClose);
        return this;
    }

    public CriteriaBuilder<E, PARAMETER_TYPE, CRITERIA_TYPE> withBracketOpen(boolean bracketOpen) {
        criteria.setBracketOpen(bracketOpen);
        return this;
    }

    public CriteriaBuilder<E, PARAMETER_TYPE, CRITERIA_TYPE> withLinkOperator(String linkOperator) {
        criteria.setLinkOperator(linkOperator);
        return this;
    }

    public CriteriaBuilder<E, PARAMETER_TYPE, CRITERIA_TYPE> withOperator(String operator) {
        criteria.setOperator(operator);
        return this;
    }

    public CriteriaBuilder<E, PARAMETER_TYPE, CRITERIA_TYPE> withParameter(PARAMETER_TYPE parameter) {
        criteria.setParameter(parameter);
        return this;
    }

    public QueryBuilder<E> end() {
        return queryBuilder;
    }

    public static <E, F> CriteriaBuilder<E, F, CriteriaSimple<F>> addCriteria(QueryBuilder<E> queryBuilder,
            String criteriaKey, F parameter) {
        return new CriteriaBuilder<>(queryBuilder, new CriteriaSimple<>(), criteriaKey, parameter);
    }
    
    public static <E> CriteriaBuilder<E, LocalDate, CriteriaDate> addCriteria(QueryBuilder<E> queryBuilder,
            String criteriaKey, LocalDate parameter) {
        return new CriteriaBuilder<>(queryBuilder, new CriteriaDate(), criteriaKey, parameter);
    }


}

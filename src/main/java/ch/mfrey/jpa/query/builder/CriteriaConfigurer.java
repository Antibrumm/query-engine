package ch.mfrey.jpa.query.builder;

import java.time.LocalDate;

import ch.mfrey.jpa.query.model.AbstractCriteria;
import ch.mfrey.jpa.query.model.CriteriaBoolean;
import ch.mfrey.jpa.query.model.CriteriaDate;
import ch.mfrey.jpa.query.model.CriteriaDouble;
import ch.mfrey.jpa.query.model.CriteriaFloat;
import ch.mfrey.jpa.query.model.CriteriaInteger;
import ch.mfrey.jpa.query.model.CriteriaLong;
import ch.mfrey.jpa.query.model.CriteriaString;

public class CriteriaConfigurer<PARAMETER_TYPE, CRITERIA_TYPE extends AbstractCriteria<PARAMETER_TYPE>> {

    private QueryBuilder queryBuilder;
    private CRITERIA_TYPE criteria;

    private CriteriaConfigurer(QueryBuilder queryBuilder, CRITERIA_TYPE criteria, String criteriaKey,
            PARAMETER_TYPE parameter) {
        this.queryBuilder = queryBuilder;
        this.criteria = criteria;
        this.criteria.setCriteriaKey(criteriaKey);
        this.criteria.setParameter(parameter);
        queryBuilder.getQuery().getCriterias().add(this.criteria);
    }

    public QueryBuilder and() {
        return queryBuilder;
    }

    public CriteriaConfigurer<PARAMETER_TYPE, CRITERIA_TYPE> withBracketClose(boolean bracketClose) {
        criteria.setBracketClose(bracketClose);
        return this;
    }

    public CriteriaConfigurer<PARAMETER_TYPE, CRITERIA_TYPE> withBracketOpen(boolean bracketOpen) {
        criteria.setBracketOpen(bracketOpen);
        return this;
    }

    public CriteriaConfigurer<PARAMETER_TYPE, CRITERIA_TYPE> withLinkOperator(String linkOperator) {
        criteria.setLinkOperator(linkOperator);
        return this;
    }

    public CriteriaConfigurer<PARAMETER_TYPE, CRITERIA_TYPE> withOperator(String operator) {
        criteria.setOperator(operator);
        return this;
    }

    public QueryBuilder end() {
        return queryBuilder;
    }

    public static CriteriaConfigurer<String, CriteriaString> forCriteria(QueryBuilder queryBuilder,
            String criteriaKey, String parameter) {
        return new CriteriaConfigurer<>(queryBuilder, new CriteriaString(), criteriaKey, parameter);
    }

    public static CriteriaConfigurer<Integer, CriteriaInteger> forCriteria(QueryBuilder queryBuilder,
            String criteriaKey, Integer parameter) {
        return new CriteriaConfigurer<>(queryBuilder, new CriteriaInteger(), criteriaKey, parameter);
    }

    public static CriteriaConfigurer<Float, CriteriaFloat> forCriteria(QueryBuilder queryBuilder,
            String criteriaKey, Float parameter) {
        return new CriteriaConfigurer<>(queryBuilder, new CriteriaFloat(), criteriaKey, parameter);
    }

    public static CriteriaConfigurer<Double, CriteriaDouble> forCriteria(QueryBuilder queryBuilder,
            String criteriaKey, Double parameter) {
        return new CriteriaConfigurer<>(queryBuilder, new CriteriaDouble(), criteriaKey, parameter);
    }

    public static CriteriaConfigurer<Boolean, CriteriaBoolean> forCriteria(QueryBuilder queryBuilder,
            String criteriaKey, Boolean parameter) {
        return new CriteriaConfigurer<>(queryBuilder, new CriteriaBoolean(), criteriaKey, parameter);
    }

    public static CriteriaConfigurer<LocalDate, CriteriaDate> forCriteria(QueryBuilder queryBuilder,
            String criteriaKey, LocalDate parameter) {
        return new CriteriaConfigurer<>(queryBuilder, new CriteriaDate(), criteriaKey, parameter);
    }

    public static CriteriaConfigurer<Long, CriteriaLong> forCriteria(QueryBuilder queryBuilder,
            String criteriaKey, Long parameter) {
        return new CriteriaConfigurer<>(queryBuilder, new CriteriaLong(), criteriaKey, parameter);
    }
}

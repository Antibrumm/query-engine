package ch.mfrey.jpa.query.builder;

import java.time.LocalDate;

import ch.mfrey.jpa.query.model.CriteriaBoolean;
import ch.mfrey.jpa.query.model.CriteriaDate;
import ch.mfrey.jpa.query.model.CriteriaDouble;
import ch.mfrey.jpa.query.model.CriteriaFloat;
import ch.mfrey.jpa.query.model.CriteriaInteger;
import ch.mfrey.jpa.query.model.CriteriaLong;
import ch.mfrey.jpa.query.model.CriteriaString;
import ch.mfrey.jpa.query.model.Query;

public class QueryBuilder {

    private Query query;

    private QueryBuilder(Query query) {
        this.query = query;
    }

    public static QueryBuilder forQuery(Class<?> entityClass) {
        Query query = new Query();
        query.setEntityClass(entityClass);
        return new QueryBuilder(query);
    }

    public Query build() {
        return query;
    }

    public CriteriaConfigurer<String, CriteriaString> withCriteria(String criteriaKey, String parameter) {
        return CriteriaConfigurer.forCriteria(this, criteriaKey, parameter);
    }

    public CriteriaConfigurer<Long, CriteriaLong> withCriteria(String criteriaKey, Long parameter) {
        return CriteriaConfigurer.forCriteria(this, criteriaKey, parameter);
    }

    public CriteriaConfigurer<Integer, CriteriaInteger> withCriteria(String criteriaKey, Integer parameter) {
        return CriteriaConfigurer.forCriteria(this, criteriaKey, parameter);
    }

    public CriteriaConfigurer<Float, CriteriaFloat> withCriteria(String criteriaKey, Float parameter) {
        return CriteriaConfigurer.forCriteria(this, criteriaKey, parameter);
    }

    public CriteriaConfigurer<Double, CriteriaDouble> withCriteria(String criteriaKey, Double parameter) {
        return CriteriaConfigurer.forCriteria(this, criteriaKey, parameter);

    }

    public CriteriaConfigurer<Boolean, CriteriaBoolean> withCriteria(String criteriaKey, Boolean parameter) {
        return CriteriaConfigurer.forCriteria(this, criteriaKey, parameter);

    }

    public CriteriaConfigurer<LocalDate, CriteriaDate> withCriteria(String criteriaKey, LocalDate parameter) {
        return CriteriaConfigurer.forCriteria(this, criteriaKey, parameter);
    }

    public QueryBuilder and() {
        return this;
    }

    /**
     * Used by the configurers.
     * 
     * @return
     */
    Query getQuery() {
        return query;
    }
}

package ch.mfrey.jpa.query.builder;

import java.time.LocalDate;

import ch.mfrey.jpa.query.model.CriteriaDate;
import ch.mfrey.jpa.query.model.CriteriaSimple;
import ch.mfrey.jpa.query.model.Query;

public class QueryBuilder<E> {

    private Query<E> query;

    private QueryBuilder(Query<E> query) {
        this.query = query;
    }

    public static <E> QueryBuilder<E> forEntity(Class<E> entityClass) {
        Query<E> query = new Query<>();
        query.setEntityClass(entityClass);
        return new QueryBuilder<>(query);
    }

    public Query<E> build() {
        return query;
    }

    public QueryBuilder<E> setMaxResults(int maxResults) {
        query.setMaxResults(maxResults);
        return this;
    }

    public <F> CriteriaBuilder<E, F, CriteriaSimple<F>> withCriteria(String criteriaKey, F parameter) {
        return CriteriaBuilder.addCriteria(this, criteriaKey, parameter);
    }

    public CriteriaBuilder<E, LocalDate, CriteriaDate> withCriteria(String criteriaKey, LocalDate parameter) {
        return CriteriaBuilder.addCriteria(this, criteriaKey, parameter);
    }

    public <F> QueryBuilder<E> orCriteria(String criteriaKey, F parameter) {
        return CriteriaBuilder.addCriteria(this, criteriaKey, parameter).withLinkOperator("OR").and();
    }

    public QueryBuilder<E> orCriteria(String criteriaKey, LocalDate parameter) {
        return CriteriaBuilder.addCriteria(this, criteriaKey, parameter).withLinkOperator("OR").and();
    }

    public <F> QueryBuilder<E> andCriteria(String criteriaKey, F parameter) {
        return CriteriaBuilder.addCriteria(this, criteriaKey, parameter).and();
    }

    public QueryBuilder<E> andCriteria(String criteriaKey, LocalDate parameter) {
        return CriteriaBuilder.addCriteria(this, criteriaKey, parameter).and();
    }

    /**
     * Used by the configurers.
     * 
     * @return
     */
    Query<E> getQuery() {
        return query;
    }
}

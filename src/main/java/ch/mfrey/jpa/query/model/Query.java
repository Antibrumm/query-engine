package ch.mfrey.jpa.query.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.StringUtils;

public class Query<E> {

    private Integer maxResults = null;

    private List<Criteria<?>> criterias = new ArrayList<>();

    private Class<E> entityClass;

    public List<Criteria<?>> getCriterias() {
        return criterias;
    }

    public Class<E> getEntityClass() {
        return entityClass;
    }

    public void setCriterias(List<Criteria<?>> criterias) {
        this.criterias = criterias;
    }

    public void setEntityClass(Class<E> entityClass) {
        this.entityClass = entityClass;
    }

    public String getSynonym() {
        return StringUtils.uncapitalize(getEntityClass().getSimpleName());
    }

    /**
     * Checks if a subselect is needed to deduplicate the results. could be more sophisticated, like "includes ..ToMany
     * join"
     * 
     * @return
     */
    public boolean needsSubselect() {
        for (Criteria<?> c : getCriterias()) {
            if (c.getCriteriaKey().contains(".")) {
                return true;
            }
        }
        return false;
    }

    public Integer getMaxResults() {
        return maxResults;
    }

    public void setMaxResults(Integer maxResults) {
        this.maxResults = maxResults;
    }
}

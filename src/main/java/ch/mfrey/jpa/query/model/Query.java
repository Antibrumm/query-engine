package ch.mfrey.jpa.query.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.StringUtils;

public class Query {

    private List<Criteria<?>> criterias = new ArrayList<>();

    private Class<?> entityClass;

    public List<Criteria<?>> getCriterias() {
        return criterias;
    }

    public Class<?> getEntityClass() {
        return entityClass;
    }

    public void setCriterias(List<Criteria<?>> criterias) {
        this.criterias = criterias;
    }

    public void setEntityClass(Class<?> entityClass) {
        this.entityClass = entityClass;
    }

    public String getSynonym() {
        return StringUtils.uncapitalize(getEntityClass().getSimpleName());
    }

    public boolean needsSubselect() {
        for (Criteria<?> c : getCriterias()) {
            if (c.getPropertyAccessor().contains(".")) {
                return true;
            }
        }
        return false;
    }
}

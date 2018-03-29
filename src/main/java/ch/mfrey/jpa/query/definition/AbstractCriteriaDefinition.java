package ch.mfrey.jpa.query.definition;

import java.beans.PropertyDescriptor;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import ch.mfrey.bean.ad.AccessorDescriptor;
import ch.mfrey.bean.ad.ClassUtils;
import ch.mfrey.jpa.query.model.Criteria;
import ch.mfrey.jpa.query.model.Query;

/**
 * The Class CriteriaDefinition.
 *
 * @author Martin Frey
 */
public abstract class AbstractCriteriaDefinition<CRITERIA extends Criteria<?>> implements CriteriaDefinition<CRITERIA> {

    /** The Constant QUERY_APPEND_DOT. */
    public static final char QUERY_APPEND_DOT = '.';

    /** The Constant QUERY_APPEND_JOIN. */
    public static final String QUERY_APPEND_JOIN = "JOIN "; //$NON-NLS-1$

    /** The Constant QUERY_APPEND_SPACE. */
    public static final char QUERY_APPEND_SPACE = ' ';

    public static final String CRITERIA_PARAMETER = "{#query.criterias[{0}].parameter}"; //$NON-NLS-1$

    private AccessorDescriptor accessorDescriptor;

    /**
     * Gets the entity class.
     *
     * @return the entity class
     */
    public Class<?> getEntityClass() {
        return getAccessorDescriptor().getType();
    }

    /**
     * Gets the joins.
     * 
     * @param criteria
     * @param query
     *
     * @return the joins
     */
    protected List<String> getJoins(Query query, CRITERIA criteria) {
        List<String> joins = new ArrayList<>();
        if (getAccessorDescriptor().getPropertyLevel() > 0) {
            String[] links = criteria.getPropertyAccessor().split("\\.");
            Assert.isTrue(links.length != getAccessorDescriptor().getPropertyLevel(), "Not same length");
            String synonym = StringUtils.uncapitalize(getEntityClass().getSimpleName());
            List<PropertyDescriptor> propertyDescriptors = getAccessorDescriptor().getPropertyDescriptors();
            for (int i = 0; i < propertyDescriptors.size() - 1; i++) {
                PropertyDescriptor pd = propertyDescriptors.get(i);
                String nextSynonym = synonym + "_" + pd.getName();
                StringBuilder join = new StringBuilder().append(synonym)
                        .append(QUERY_APPEND_DOT)
                        .append(pd.getName())
                        .append(QUERY_APPEND_SPACE)
                        .append(nextSynonym);

                int mapIdx = links[i].indexOf('[');
                if (mapIdx != -1) {
                    if (Map.class.isAssignableFrom(pd.getReadMethod().getReturnType())) {
                        ParameterizedType pt = (ParameterizedType) pd.getReadMethod().getGenericReturnType();
                        Class<?> typeToCheck = (Class<?>) pt.getActualTypeArguments()[0];
                        if (ClassUtils.isSimpleValueType(typeToCheck)) {
                            join.append(" ON key(")
                                    .append(nextSynonym)
                                    .append(") = '")
                                    .append(links[i].substring(mapIdx+1, links[i].indexOf(']', mapIdx)))
                                    .append("'");
                        }
                    }
                }
                joins.add(join.toString());
                synonym = nextSynonym;
            }
        }
        return joins;
    }

    /**
     * Gets the property accessor.
     *
     * @return the property accessor
     */
    public String getPropertyAccessor() {
        return getAccessorDescriptor().getPropertyAccessor();
    }

    @Override
    public void applyJoins(StringBuilder joinsPart, List<String> appliedJoins, Query query, CRITERIA criteria,
            int position) {
        for (String join : getJoins(query, criteria)) {
            if (!appliedJoins.contains(join)) {
                joinsPart.append(" JOIN ").append(join);
                appliedJoins.add(join);
            }
        }
    }

    public AccessorDescriptor getAccessorDescriptor() {
        return accessorDescriptor;
    }

    public void setAccessorDescriptor(AccessorDescriptor accessorDescriptor) {
        this.accessorDescriptor = accessorDescriptor;
    }

    public String getSynonym() {
        String base = StringUtils.uncapitalize(getEntityClass().getSimpleName());
        if (accessorDescriptor.getPropertyLevel() == 0) {
            return base;
        }
        String propertyAccessor = accessorDescriptor.getPropertyAccessor();
        String synonym = base + "_"
                + propertyAccessor
                        .substring(0, propertyAccessor.lastIndexOf('.'))
                        .replace('.', '_');
        return synonym;
    }

    public boolean isNullable() {
        for (PropertyDescriptor pd : getAccessorDescriptor().getPropertyDescriptors()) {
            if (pd.getReadMethod() != null) {
                return true;
            }
        }
        return false;
    }

    protected StringBuilder replacePosition(StringBuilder restriction, int position) {
        int start;
        while ((start = restriction.indexOf("{0}")) != -1) {
            restriction.replace(start, start + 3, String.valueOf(position));
        }
        return restriction;
    }

    public boolean isTerminal() {
        return true;
    }

}

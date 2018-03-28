package ch.mfrey.jpa.query.definition;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;

import org.springframework.util.StringUtils;

import ch.mfrey.bean.ad.AccessorDescriptor;
import ch.mfrey.jpa.query.model.Criteria;

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
     * @return the joins
     */
    public List<String> getJoins() {
        List<String> joins = new ArrayList<>();
        if (getAccessorDescriptor().getPropertyLevel() > 0) {
            String synonym = StringUtils.uncapitalize(getEntityClass().getSimpleName());
            List<PropertyDescriptor> propertyDescriptors = getAccessorDescriptor().getPropertyDescriptors();
            for (int i = 0; i < propertyDescriptors.size() - 1; i++) {
                PropertyDescriptor pd = propertyDescriptors.get(i);
                String nextSynonym = synonym + "_" + pd.getName();
                joins.add(synonym + QUERY_APPEND_DOT + pd.getName() + " " + nextSynonym);
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

    public void applyJoins(StringBuilder joinsPart, List<String> appliedJoins, int position) {
        for (String join : getJoins()) {
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

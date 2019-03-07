package ch.mfrey.jpa.query.definition;

import java.util.List;

import org.springframework.util.StringUtils;

import ch.mfrey.bean.ad.AccessorDescriptor;
import ch.mfrey.bean.ad.BeanPropertyDescriptor;
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

    public static final String CRITERIA_PARAMETER = "{#query.criterias[|POSITION|].parameter}"; //$NON-NLS-1$

    private final Class<?> entityClass;

    private final int propertyLevel;

    private final List<BeanPropertyDescriptor> propertyDescriptors;

    private final String fullPropertyAccessor;

    private final String criteriaKey;

    public AbstractCriteriaDefinition(AccessorDescriptor accessorDescriptor) {
        this.entityClass = accessorDescriptor.getType();
        this.criteriaKey = accessorDescriptor.getPropertyAccessor();
        this.fullPropertyAccessor = accessorDescriptor.getFullPropertyAccessor();
        this.propertyDescriptors = accessorDescriptor.getBeanPropertyDescriptors();
        this.propertyLevel = accessorDescriptor.getPropertyLevel();
    }

    public String getCriteriaKey() {
        return criteriaKey;
    }

    /**
     * Gets the property descriptors.
     *
     * @return the property descriptors
     */
    public List<BeanPropertyDescriptor> getBeanPropertyDescriptors() {
        return propertyDescriptors;
    }

    /**
     * Gets the property level.
     *
     * @return the property level
     */
    public int getPropertyLevel() {
        return propertyLevel;
    }

    /**
     * Gets the result descriptor.
     *
     * @return the result descriptor
     */
    public BeanPropertyDescriptor getResultDescriptor() {
        return propertyDescriptors.get(propertyDescriptors.size() - 1);
    }

    /**
     * Gets the entity class.
     *
     * @return the entity class
     */
    public Class<?> getEntityClass() {
        return entityClass;
    }

    public String getSynonym() {
        String base = StringUtils.uncapitalize(getEntityClass().getSimpleName());
        if (getPropertyLevel() == 0) {
            return base;
        }
        String criteriaKey = getCriteriaKey();
        String synonym = base + "_"
                + criteriaKey
                        .substring(0, criteriaKey.lastIndexOf('.'))
                        .replace('.', '_');
        return synonym;
    }

    public boolean isNullable() {
      
        return false;
    }

    public boolean isTerminal() {
        return true;
    }

    public String getFullPropertyAccessor() {
        return fullPropertyAccessor;
    }

}

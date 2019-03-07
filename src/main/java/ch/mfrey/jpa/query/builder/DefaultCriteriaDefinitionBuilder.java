package ch.mfrey.jpa.query.builder;

import java.lang.reflect.ParameterizedType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ch.mfrey.bean.ad.AccessorDescriptor;
import ch.mfrey.bean.ad.AccessorDescriptorFactory;
import ch.mfrey.bean.ad.BeanPropertyDescriptor;
import ch.mfrey.jpa.query.definition.CriteriaDefinition;
import ch.mfrey.jpa.query.definition.CriteriaDefinitionBoolean;
import ch.mfrey.jpa.query.definition.CriteriaDefinitionDate;
import ch.mfrey.jpa.query.definition.CriteriaDefinitionNumber;
import ch.mfrey.jpa.query.definition.CriteriaDefinitionString;
import ch.mfrey.jpa.query.model.Criteria;

@SuppressWarnings("rawtypes")
@Component
class DefaultCriteriaDefinitionBuilder
        implements CriteriaDefinitionBuilder<Criteria<?>, CriteriaDefinition<Criteria<?>>> {

    @Autowired
    private AccessorDescriptorFactory accessorDescriptorFactory;

    public DefaultCriteriaDefinitionBuilder() {
        super();
    }

    @Override
    public CriteriaDefinition build(AccessorDescriptor descriptor) {
        BeanPropertyDescriptor pd = descriptor.getResultBeanPropertyDescriptor();
        Class<?> typeToCheck = getTypeToCheck(pd);
        if (isEntity(typeToCheck) && isEntityProperty(pd)) {
            for (AccessorDescriptor ad : accessorDescriptorFactory.getAccessorDescriptors(typeToCheck)) {
                if (ad.getPropertyLevel() <= 0 && ad.isAnnotationPresent(Id.class)) {
                    return buildFromType(descriptor, ad.getResultBeanPropertyDescriptor().getPropertyType());
                }
            }
        }
        return buildFromType(descriptor, typeToCheck);
    }

    public CriteriaDefinition<?> buildFromType(AccessorDescriptor descriptor, Class<?> typeToCheck) {
        if (String.class.isAssignableFrom(typeToCheck)) {
            return new CriteriaDefinitionString(descriptor);
        }
        if (Number.class.isAssignableFrom(typeToCheck)) {
            return new CriteriaDefinitionNumber(descriptor);
        }
        if (Boolean.class.isAssignableFrom(typeToCheck)) {
            return new CriteriaDefinitionBoolean(descriptor);
        }
        if (LocalDate.class.isAssignableFrom(typeToCheck)) {
            return new CriteriaDefinitionDate(descriptor);
        }
        if (LocalDateTime.class.isAssignableFrom(typeToCheck)) {
            return new CriteriaDefinitionDate(descriptor);
        }
        throw new IllegalArgumentException("EEK");
    }

    public boolean isEntity(final Class<?> beanClass) {
        return beanClass != null
                && (beanClass.isAnnotationPresent(MappedSuperclass.class)
                        || beanClass.isAnnotationPresent(Entity.class)
                        || beanClass.isAnnotationPresent(Embeddable.class));
    }

    public boolean isEntityProperty(BeanPropertyDescriptor pd) {
        return pd.isAnnotationPresent(OneToOne.class)
                || pd.isAnnotationPresent(OneToMany.class)
                || pd.isAnnotationPresent(ManyToMany.class)
                || pd.isAnnotationPresent(ManyToOne.class)
                || pd.isAnnotationPresent(Embedded.class);
    }

    @Override
    public boolean supports(AccessorDescriptor descriptor) {
        List<BeanPropertyDescriptor> propertyDescriptors = descriptor.getBeanPropertyDescriptors();
        for (int i = 0; i < propertyDescriptors.size() - 1; i++) {
            BeanPropertyDescriptor pd = propertyDescriptors.get(i);
            Class<?> typeToCheck = getTypeToCheck(pd);

            if (!isEntity(typeToCheck) || !isEntityProperty(pd)) {
                return false;
            }
        }

        BeanPropertyDescriptor pd = descriptor.getResultBeanPropertyDescriptor();
        Class<?> typeToCheck = getTypeToCheck(pd);

        if (isEntity(typeToCheck) && isEntityProperty(pd)) {
            return true;
        }

        List<Class<?>> supported =
                Arrays.asList(
                        String.class,
                        Number.class,
                        Boolean.class,
                        LocalDate.class,
                        LocalDateTime.class);
        for (Class<?> type : supported) {
            if (type.isAssignableFrom(typeToCheck)) {
                return true;
            }
        }
        return false;
    }

    public Class<?> getTypeToCheck(BeanPropertyDescriptor pd) {
        Class<?> typeToCheck = pd.getPropertyType();
        if (typeToCheck.isArray()) {
            typeToCheck = typeToCheck.getComponentType();
        } else if (Map.class.isAssignableFrom(typeToCheck)) {
            ParameterizedType pt =
                    (ParameterizedType) pd.getPropertyDescriptor().getReadMethod().getGenericReturnType();
            typeToCheck = (Class<?>) pt.getActualTypeArguments()[1];
        } else if (Collection.class.isAssignableFrom(typeToCheck)) {
            ParameterizedType pt =
                    (ParameterizedType) pd.getPropertyDescriptor().getReadMethod().getGenericReturnType();
            typeToCheck = (Class<?>) pt.getActualTypeArguments()[0];
        }
        return typeToCheck;
    }

}

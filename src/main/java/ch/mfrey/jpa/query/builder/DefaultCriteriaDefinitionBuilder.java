package ch.mfrey.jpa.query.builder;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
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
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import ch.mfrey.bean.ad.AccessorDescriptor;
import ch.mfrey.jpa.query.definition.CriteriaDefinition;
import ch.mfrey.jpa.query.definition.CriteriaDefinitionBoolean;
import ch.mfrey.jpa.query.definition.CriteriaDefinitionDate;
import ch.mfrey.jpa.query.definition.CriteriaDefinitionNumber;
import ch.mfrey.jpa.query.definition.CriteriaDefinitionString;
import ch.mfrey.jpa.query.model.Criteria;
import ch.mfrey.jpa.query.model.CriteriaDouble;
import ch.mfrey.jpa.query.model.CriteriaFloat;
import ch.mfrey.jpa.query.model.CriteriaInteger;
import ch.mfrey.jpa.query.model.CriteriaLong;

@SuppressWarnings("rawtypes")
@Component
public class DefaultCriteriaDefinitionBuilder
        implements CriteriaDefinitionBuilder<Criteria<?>, CriteriaDefinition<Criteria<?>>> {

    @Override
    public CriteriaDefinition build(AccessorDescriptor descriptor) {
        Class<?> resultType = descriptor.getResultDescriptor().getPropertyType();
        if (String.class.isAssignableFrom(resultType)) {
            CriteriaDefinitionString criteria = new CriteriaDefinitionString();
            criteria.setAccessorDescriptor(descriptor);
            return criteria;
        }
        if (Integer.class.isAssignableFrom(resultType)) {
            CriteriaDefinitionNumber<CriteriaInteger> criteria = new CriteriaDefinitionNumber<>();
            criteria.setAccessorDescriptor(descriptor);
            return criteria;
        }
        if (Long.class.isAssignableFrom(resultType)) {
            CriteriaDefinitionNumber<CriteriaLong> criteria = new CriteriaDefinitionNumber<>();
            criteria.setAccessorDescriptor(descriptor);
            return criteria;
        }
        if (Float.class.isAssignableFrom(resultType)) {
            CriteriaDefinitionNumber<CriteriaFloat> criteria = new CriteriaDefinitionNumber<>();
            criteria.setAccessorDescriptor(descriptor);
            return criteria;
        }
        if (Double.class.isAssignableFrom(resultType)) {
            CriteriaDefinitionNumber<CriteriaDouble> criteria = new CriteriaDefinitionNumber<>();
            criteria.setAccessorDescriptor(descriptor);
            return criteria;
        }
        if (Boolean.class.isAssignableFrom(resultType)) {
            CriteriaDefinitionBoolean criteria = new CriteriaDefinitionBoolean();
            criteria.setAccessorDescriptor(descriptor);
            return criteria;
        }
        if (LocalDate.class.isAssignableFrom(resultType)) {
            CriteriaDefinitionDate criteria = new CriteriaDefinitionDate();
            criteria.setAccessorDescriptor(descriptor);
            return criteria;
        }
        if (LocalDateTime.class.isAssignableFrom(resultType)) {
            CriteriaDefinitionDate criteria = new CriteriaDefinitionDate();
            criteria.setAccessorDescriptor(descriptor);
            return criteria;
        }
        throw new IllegalArgumentException("EEK");

    }

    public boolean isEntity(final Class<?> beanClass) {
        return beanClass != null
                && (beanClass.isAnnotationPresent(MappedSuperclass.class)
                        || beanClass.isAnnotationPresent(Entity.class)
                        || beanClass.isAnnotationPresent(Embeddable.class));
    }

    public boolean isEntityProperty(PropertyDescriptor pd) {
        Method readMethod = pd.getReadMethod();
        if (readMethod != null
                && (readMethod.isAnnotationPresent(OneToOne.class)
                        || readMethod.isAnnotationPresent(OneToMany.class)
                        || readMethod.isAnnotationPresent(ManyToMany.class)
                        || readMethod.isAnnotationPresent(ManyToOne.class)
                        || readMethod.isAnnotationPresent(Embedded.class))) {
            return true;
        }

        Class<?> declaringClass = pd.getReadMethod().getDeclaringClass();
        Field field = ReflectionUtils.findField(declaringClass, pd.getName());
        if (field != null
                && (field.isAnnotationPresent(OneToOne.class)
                        || field.isAnnotationPresent(OneToMany.class)
                        || field.isAnnotationPresent(ManyToMany.class)
                        || field.isAnnotationPresent(ManyToOne.class)
                        || field.isAnnotationPresent(Embedded.class))) {
            return true;
        }
        return false;
    }

    @Override
    public boolean supports(AccessorDescriptor descriptor) {
        List<PropertyDescriptor> propertyDescriptors = descriptor.getPropertyDescriptors();
        for (int i = 0; i < propertyDescriptors.size() - 1; i++) {
            PropertyDescriptor pd = propertyDescriptors.get(i);
            Class<?> typeToCheck = pd.getReadMethod().getReturnType();
            if (typeToCheck.isArray()) {
                typeToCheck = typeToCheck.getComponentType();
            } else if (Map.class.isAssignableFrom(typeToCheck)) {
                ParameterizedType pt = (ParameterizedType) pd.getReadMethod().getGenericReturnType();
                typeToCheck = (Class<?>) pt.getActualTypeArguments()[1];
            } else if (Collection.class.isAssignableFrom(typeToCheck)) {
                ParameterizedType pt = (ParameterizedType) pd.getReadMethod().getGenericReturnType();
                typeToCheck = (Class<?>) pt.getActualTypeArguments()[0];
            }

            if (!isEntity(typeToCheck)
                    || !isEntityProperty(pd)) {
                return false;
            }
        }

        Class<?> resultType = descriptor.getResultDescriptor().getPropertyType();
        List<Class<?>> supported =
                Arrays.asList(String.class, Number.class, Boolean.class, LocalDate.class, LocalDateTime.class);
        for (Class<?> type : supported) {
            if (type.isAssignableFrom(resultType)) {
                return true;
            }
        }
        return false;
    }

}

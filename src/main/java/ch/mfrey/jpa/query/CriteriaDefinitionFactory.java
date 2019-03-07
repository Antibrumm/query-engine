package ch.mfrey.jpa.query;

import java.beans.PropertyDescriptor;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.mfrey.bean.ad.AccessorDescriptor;
import ch.mfrey.bean.ad.AccessorDescriptorBuilder;
import ch.mfrey.bean.ad.AccessorDescriptorFactory;
import ch.mfrey.bean.ad.BeanPropertyDescriptor;
import ch.mfrey.jpa.query.builder.CriteriaDefinitionBuilder;
import ch.mfrey.jpa.query.definition.CriteriaDefinition;

@Service
public class CriteriaDefinitionFactory {

    @Autowired
    private List<CriteriaDefinitionBuilder<?, ?>> builders;
    private static final Logger log = LoggerFactory.getLogger(CriteriaDefinitionFactory.class);

    @Autowired
    private AccessorDescriptorFactory accessorDescriptorFactory;

    public List<CriteriaDefinition<?>> getCriteriaDefinitions(Class<?> entityClass) {
        List<AccessorDescriptor> accessorDescriptors =
                accessorDescriptorFactory.getAccessorDescriptors(entityClass);
        List<CriteriaDefinition<?>> definitions = new ArrayList<>();
        for (AccessorDescriptor descriptor : accessorDescriptors) {
            try {
                handleAccessorDescriptor(descriptor, definitions);
            } catch (IllegalArgumentException e) {
                log.debug("No builder for accessor descriptor {}", descriptor);
            }
        }
        return definitions;
    }

    private <E extends CriteriaDefinition<?>> E buildDefinition(AccessorDescriptor descriptor) {
        for (CriteriaDefinitionBuilder<?, ?> builder : builders) {
            if (builder.supports(descriptor)) {
                @SuppressWarnings("unchecked")
                E criteriaDefinition = (E) builder.build(descriptor);
                return criteriaDefinition;
            }
        }
        throw new IllegalArgumentException("Could not build CriteriaDefinition for AccessorDescriptor " + descriptor);
    }

    private boolean handleAccessorDescriptor(AccessorDescriptor descriptor, List<CriteriaDefinition<?>> definitions) {
        CriteriaDefinition<?> criteriaDefinition = buildDefinition(descriptor);
        definitions.add(criteriaDefinition);
        return criteriaDefinition.isTerminal();
    }

    @SuppressWarnings("unchecked")
    public <E extends CriteriaDefinition<?>> E getCriteriaDefinition(Class<?> entityClass,
            String criteriaKey) {
        String processedKey = criteriaKey.replaceAll(AccessorDescriptorBuilder.INDEXED_ACCESSOR_PART, "");
        for (CriteriaDefinition<?> definition : getCriteriaDefinitions(entityClass)) {
            if (criteriaKey.equals(definition.getCriteriaKey()) || processedKey.equals(definition.getCriteriaKey())) {
                return (E) definition;
            }
        }

        return tryBuildDefinition(entityClass, criteriaKey);
    }

    private <E extends CriteriaDefinition<?>> E tryBuildDefinition(Class<?> entityClass, String criteriaKey) {
        try {
            String[] links = criteriaKey.split("\\.");
            AccessorDescriptorBuilder builder = AccessorDescriptorBuilder.builder(entityClass, null);
            Class<?> type = entityClass;
            for (int i = 0; i < links.length; i++) {
                String propertyName = links[i].replaceAll(AccessorDescriptorBuilder.INDEXED_ACCESSOR_PART, "");
                PropertyDescriptor pd = BeanUtils.getPropertyDescriptor(type, propertyName);
                if (pd == null) {
                    throw new IllegalArgumentException(
                            "PropertyDescriptor does not exist : " + type.getSimpleName() + "." + propertyName);
                }
                Class<?> returnType = pd.getPropertyType();
                if (returnType.isArray()) {
                    builder = builder.withPropertyDescriptor(pd.getName() + "[]",
                            new BeanPropertyDescriptor(entityClass, pd));
                    type = pd.getClass().getComponentType();
                } else if (Map.class.isAssignableFrom(returnType) && pd.getReadMethod() != null
                        && pd.getReadMethod().getGenericReturnType() instanceof ParameterizedType) {
                    ParameterizedType parameterizedType = (ParameterizedType) pd.getReadMethod().getGenericReturnType();
                    builder = builder.withPropertyDescriptor(links[i],
                            new BeanPropertyDescriptor(entityClass, pd));
                    type = (Class<?>) parameterizedType.getActualTypeArguments()[1];
                } else if (Collection.class.isAssignableFrom(returnType)
                        && pd.getReadMethod().getGenericReturnType() instanceof ParameterizedType) {
                    ParameterizedType parameterizedType = (ParameterizedType) pd.getReadMethod().getGenericReturnType();
                    if (parameterizedType.getActualTypeArguments()[0] instanceof Class) {
                        builder = builder.withPropertyDescriptor(pd.getName() + "[]",
                                new BeanPropertyDescriptor(entityClass, pd));
                        type = (Class<?>) parameterizedType.getActualTypeArguments()[0];
                    }
                } else {
                    builder = builder.withPropertyDescriptor(pd.getName(),
                            new BeanPropertyDescriptor(entityClass, pd));
                }
            }
            return buildDefinition(builder.build());
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    "Could not build CriteriaDefinition for '" + criteriaKey + "' in class '" + entityClass.getName(),
                    e);
        }
    }

}

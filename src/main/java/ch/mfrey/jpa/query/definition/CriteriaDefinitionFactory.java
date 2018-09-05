package ch.mfrey.jpa.query.definition;

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
import ch.mfrey.jpa.query.builder.CriteriaDefinitionBuilder;

@Service
public class CriteriaDefinitionFactory {

    @Autowired
    private List<CriteriaDefinitionBuilder<?, ?>> builders;
    private static final Logger log = LoggerFactory.getLogger(CriteriaDefinitionFactory.class);
    private AccessorDescriptorFactory accessorDescriptorFactory = new AccessorDescriptorFactory();

    public List<CriteriaDefinition<?>> getCriteriaDefinitions(Class<?> entityClass) {
        List<AccessorDescriptor> accessorDescriptors =
                accessorDescriptorFactory.getAccessorDescriptors(entityClass);
        List<CriteriaDefinition<?>> definitions = new ArrayList<>();
        List<String> terminatingDescriptors = new ArrayList<>();
        for (AccessorDescriptor descriptor : accessorDescriptors) {
            if (!shouldIgnore(descriptor, terminatingDescriptors)) {
                try {
                    boolean isTerminating = handleAccessorDescriptor(descriptor, definitions);
                    if (isTerminating) {
                        terminatingDescriptors.add(descriptor.getPropertyAccessor());
                    } else {
                        // we need to check deeper as there might be other references of the same class which
                        // should produce more criterias.
                        // ..ToMany backreferences and any other reference

                    }
                } catch (IllegalArgumentException e) {
                    log.debug("No builder for accessor descriptor {}", descriptor);
                }
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

    private boolean shouldIgnore(AccessorDescriptor descriptor, List<String> terminatingDescriptors) {
        for (String terminatingDescriptor : terminatingDescriptors) {
            if (descriptor.getPropertyAccessor().startsWith(terminatingDescriptor + '.')) {
                return true;
            }
        }
        return false;
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
                    builder = builder.withPropertyDescriptor(pd.getName() + "[]", pd);
                    type = pd.getClass().getComponentType();
                } else if (Collection.class.isAssignableFrom(returnType)
                        && pd.getReadMethod().getGenericReturnType() instanceof ParameterizedType) {
                    ParameterizedType parameterizedType = (ParameterizedType) pd.getReadMethod().getGenericReturnType();
                    if (parameterizedType.getActualTypeArguments()[0] instanceof Class) {
                        builder = builder.withPropertyDescriptor(pd.getName() + "[]", pd);
                        type = (Class<?>) parameterizedType.getActualTypeArguments()[0];
                    }
                } else if (Map.class.isAssignableFrom(returnType) && pd.getReadMethod() != null
                        && pd.getReadMethod().getGenericReturnType() instanceof ParameterizedType) {
                    ParameterizedType parameterizedType = (ParameterizedType) pd.getReadMethod().getGenericReturnType();
                    builder = builder.withPropertyDescriptor(links[i], pd);
                    type = (Class<?>) parameterizedType.getActualTypeArguments()[0];
                } else {
                    builder = builder.withPropertyDescriptor(pd.getName(), pd);
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

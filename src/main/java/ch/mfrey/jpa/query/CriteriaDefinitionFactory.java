package ch.mfrey.jpa.query;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.mfrey.bean.ad.AccessorDescriptor;
import ch.mfrey.bean.ad.AccessorDescriptorFactory;
import ch.mfrey.jpa.query.definition.CriteriaDefinition;

@Service
public class CriteriaDefinitionFactory {

    @Autowired
    private List<CriteriaDefinitionBuilder<?, ?>> builders;

    public List<CriteriaDefinition<?>> getCriteriaDefinitions(Class<?> entityClass) {
        List<AccessorDescriptor> accessorDescriptors =
                new AccessorDescriptorFactory().getAccessorDescriptors(entityClass);
        List<CriteriaDefinition<?>> definitions = new ArrayList<>();
        List<String> terminatingDescriptors = new ArrayList<>();
        for (AccessorDescriptor descriptor : accessorDescriptors) {
            if (!shouldIgnore(descriptor, terminatingDescriptors)) {
                boolean isTerminating = handleAccessorDescriptor(descriptor, definitions);
                if (isTerminating) {
                    terminatingDescriptors.add(descriptor.getPropertyAccessor());
                }
            }
        }
        return definitions;
    }

    private boolean handleAccessorDescriptor(AccessorDescriptor descriptor, List<CriteriaDefinition<?>> definitions) {
        for (CriteriaDefinitionBuilder<?, ?> builder : builders) {
            if (builder.supports(descriptor)) {
                CriteriaDefinition<?> criteriaDefinition = builder.build(descriptor);
                definitions.add(criteriaDefinition);
                return criteriaDefinition.isTerminal();
            }
        }
        return false;
    }

    private boolean shouldIgnore(AccessorDescriptor descriptor, List<String> terminatingDescriptors) {
        for (String terminatingDescriptor : terminatingDescriptors) {
            if (descriptor.getPropertyAccessor().startsWith(terminatingDescriptor + '.')) {
                return true;
            }
        }
        return false;
    }

    public CriteriaDefinition<?> getCriteriaDefinition(Class<?> entityClass, String propertyAccessor) {
        for (CriteriaDefinition<?> definition : getCriteriaDefinitions(entityClass)) {
            if (propertyAccessor.equals(definition.getPropertyAccessor())) {
                return definition;
            }
        }
        throw new IllegalArgumentException(
                "Could not find '" + propertyAccessor + "' in class '" + entityClass.getName() + "'");
    }
}

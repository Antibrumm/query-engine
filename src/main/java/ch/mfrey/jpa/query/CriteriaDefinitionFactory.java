package ch.mfrey.jpa.query;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.mfrey.bean.ad.AccessorDescriptor;
import ch.mfrey.bean.ad.AccessorDescriptorFactory;
import ch.mfrey.jpa.query.builder.CriteriaDefinitionBuilder;
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
                } else {
                    // we need to check deeper as there might be other references of the same class which
                    // should produce more criterias.
                    // ..ToMany backreferences and any other reference
                    
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

    @SuppressWarnings("unchecked")
    public <E extends CriteriaDefinition<?>> E getCriteriaDefinition(Class<?> entityClass,
            String criteriaKey) {
        String processedKey = criteriaKey.replaceAll("(\\[.*\\])?", "");
        for (CriteriaDefinition<?> definition : getCriteriaDefinitions(entityClass)) {
            if (processedKey.equals(definition.getCriteriaKey())) {
                return (E) definition;
            }
        }
        throw new IllegalArgumentException(
                "Could not find CriteriaDefinition for '" + criteriaKey + "' in class '" + entityClass.getName()
                        + "' (propertyAccessor '" + processedKey + "')");
    }
}

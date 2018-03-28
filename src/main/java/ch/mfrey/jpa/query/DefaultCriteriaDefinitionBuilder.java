package ch.mfrey.jpa.query;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Component;

import ch.mfrey.bean.ad.AccessorDescriptor;
import ch.mfrey.jpa.query.definition.CriteriaDefinition;
import ch.mfrey.jpa.query.definition.CriteriaDefinitionBoolean;
import ch.mfrey.jpa.query.definition.CriteriaDefinitionDate;
import ch.mfrey.jpa.query.definition.CriteriaDefinitionNumber;
import ch.mfrey.jpa.query.definition.CriteriaDefinitionString;
import ch.mfrey.jpa.query.model.Criteria;

@SuppressWarnings("rawtypes")
@Component
public class DefaultCriteriaDefinitionBuilder
        implements CriteriaDefinitionBuilder<Criteria, CriteriaDefinition<Criteria>> {

    @Override
    public CriteriaDefinition build(AccessorDescriptor descriptor) {
        Class<?> resultType = descriptor.getResultDescriptor().getPropertyType();
        if (String.class.isAssignableFrom(resultType)) {
            CriteriaDefinitionString criteria = new CriteriaDefinitionString();
            criteria.setAccessorDescriptor(descriptor);
            return criteria;
        }
        if (Number.class.isAssignableFrom(resultType)) {
            CriteriaDefinitionNumber criteria = new CriteriaDefinitionNumber();
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

    @Override
    public boolean supports(AccessorDescriptor descriptor) {
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

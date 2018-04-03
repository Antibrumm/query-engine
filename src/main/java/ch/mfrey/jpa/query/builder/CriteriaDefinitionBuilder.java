package ch.mfrey.jpa.query.builder;

import ch.mfrey.bean.ad.AccessorDescriptor;
import ch.mfrey.jpa.query.definition.CriteriaDefinition;
import ch.mfrey.jpa.query.model.Criteria;

public interface CriteriaDefinitionBuilder<CRITERIA extends Criteria<?>, CRITERIA_DEFINITION extends CriteriaDefinition<CRITERIA>> {

    CRITERIA_DEFINITION build(AccessorDescriptor descriptor);

    boolean supports(AccessorDescriptor descriptor);

}

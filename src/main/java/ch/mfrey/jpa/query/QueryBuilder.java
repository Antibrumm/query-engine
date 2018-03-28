package ch.mfrey.jpa.query;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.mfrey.jpa.query.definition.CriteriaDefinition;
import ch.mfrey.jpa.query.model.Criteria;
import ch.mfrey.jpa.query.model.Query;

/**
 * The Class QueryBuilder.
 *
 * @author Martin Frey
 */
@Service
public class QueryBuilder {

    /** The criteria property factory. */
    @Autowired
    private CriteriaDefinitionFactory criteriaDefinitionFactory;

    /**
     * Append fixed restrictions.
     *
     * @param userRestrictions
     *            the user restrictions
     * @param entitySynonym
     *            the entity synonym
     * @param restrictions
     *            the restrictions
     */
    private void appendFixedRestrictions(final StringBuilder userRestrictions, final String entitySynonym,
            final List<String> restrictions) {
        if (userRestrictions.length() == 0) {
            userRestrictions.append(" WHERE ("); //$NON-NLS-1$
        } else {
            userRestrictions.append(" AND ("); //$NON-NLS-1$
        }
        for (int i = 0; i < restrictions.size(); i++) {
            if (i > 0) {
                userRestrictions.append(" AND "); //$NON-NLS-1$
            }
            userRestrictions
                    .append('(').append(restrictions.get(i).replaceAll("|entitySynonym|", entitySynonym)).append(')'); //$NON-NLS-1$
        }
        userRestrictions.append(')');
    }

    /**
     * Append query restrictions.
     *
     * @param userRestrictions
     *            the user restrictions
     * @param query
     *            the query
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void appendQueryRestrictions(final StringBuilder userRestrictions, final Query query) {
        int brackets = 0;
        for (int position = 0; position < query.getCriterias().size(); position++) {
            Criteria<?> criteria = query.getCriterias().get(position);

            // ignore not filled filter
            if (criteria.getParameter() == null) {
                continue;
            }
            CriteriaDefinition cp =
                    criteriaDefinitionFactory.getCriteriaDefinition(query.getEntityClass(),
                            criteria.getPropertyAccessor());
            if (userRestrictions.length() == 0) {
                userRestrictions.append(" WHERE ("); //$NON-NLS-1$
            } else {
                userRestrictions.append(' ');
                userRestrictions.append(criteria.getLinkOperator());
                userRestrictions.append(' ');
            }
            if (criteria.isBracketOpen()) {
                userRestrictions.append('(');
                brackets++;
            }

            cp.applyRestriction(userRestrictions, criteria, position);

            if (criteria.isBracketClose()) {
                userRestrictions.append(')');
                brackets--;
            }

        }
        if (userRestrictions.length() > 0) {
            // if the user just closed some brackets but never
            // opened some correct the brackets with placing the forgotten
            // ones directly after the WHERE
            if (brackets < 0) {
                int whereIndex = userRestrictions.indexOf("WHERE (") + 6; //$NON-NLS-1$
                while (brackets < 0) {
                    userRestrictions.insert(whereIndex, "("); //$NON-NLS-1$
                    brackets++;
                }
            }
            // close the brackets if the user just opened some correct it
            // with placing the forgotten ones at the end
            while (brackets > 0) {
                userRestrictions.append(')');
                brackets--;
            }
            // This is the general bracket from the initial where
            // clause
            userRestrictions.append(')');
        }
    }

    public String buildJpaQuery(final Query query) {
        return buildJpaQuery(query, null);
    }

    public String buildJpaQuery(final Query query, final List<String> restrictions) {
        if (query == null) {
            throw new IllegalArgumentException("Query cannot be null"); //$NON-NLS-1$
        }
        String entityClassName = query.getEntityClass().getSimpleName();
        String entitySynonym = query.getSynonym();
        StringBuilder inner = new StringBuilder(64);
        inner.append("SELECT ").append(entitySynonym)
                .append(" FROM ")
                .append(entityClassName)
                .append(' ')
                .append(entitySynonym);
        inner.append(buildJoins(query));
        inner.append(buildWhereClause(query, restrictions));
        if (!needsSubSelect(query)) {
            return inner.toString();
        }

        StringBuilder full = new StringBuilder(64);
        full.append("SELECT ")
                .append(entitySynonym)
                .append(" FROM ")
                .append(entityClassName)
                .append(' ')
                .append(entitySynonym)
                .append(", Dummy WHERE 1=1 AND ")
                .append(entitySynonym)
                .append(" IN (");

        full.append(inner);
        full.append(')');
        return full.toString();
    }

    /**
     * Gets the joins.
     *
     * @param query
     *            the query
     * @return the joins
     */
    private String buildJoins(final Query query) {
        StringBuilder sb = new StringBuilder();
        List<String> appliedJoins = new ArrayList<>();
        for (int position = 0; position < query.getCriterias().size(); position++) {
            Criteria<?> criteria = query.getCriterias().get(position);
            // ignore not filled filter
            if (criteria.getParameter() == null) {
                continue;
            }
            CriteriaDefinition<?> cp = criteriaDefinitionFactory.getCriteriaDefinition(
                    query.getEntityClass(),
                    criteria.getPropertyAccessor());
            cp.applyJoins(sb, appliedJoins, position);
        }
        return sb.toString();
    }

    /**
     * Gets the user criterias.
     *
     * @param query
     *            the query
     * @param restrictions
     *            the restrictions
     * @param ignorableStates
     *            the ignorable states
     * @return the user criterias
     */
    protected String buildWhereClause(final Query query, final List<String> fixedRestrictions) {
        StringBuilder whereClause = new StringBuilder();
        if (!query.getCriterias().isEmpty()) {
            appendQueryRestrictions(whereClause, query);
        }
        if (fixedRestrictions != null && !fixedRestrictions.isEmpty()) {
            appendFixedRestrictions(whereClause, query.getSynonym(), fixedRestrictions);
        }
        return whereClause.toString();
    }

    /**
     * Needs sub select.
     *
     * @param query
     *            the query
     * @return true, if successful
     */
    private boolean needsSubSelect(final Query query) {
        for (Criteria<?> dc : query.getCriterias()) {
            List<String> joins =
                    criteriaDefinitionFactory.getCriteriaDefinition(query.getEntityClass(), dc.getPropertyAccessor())
                            .getJoins();
            if (joins != null && !joins.isEmpty()) {
                return true;
            }
        }
        return false;
    }

}

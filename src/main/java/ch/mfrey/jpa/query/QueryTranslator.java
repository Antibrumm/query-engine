package ch.mfrey.jpa.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import ch.mfrey.bean.ad.AccessorDescriptorBuilder;
import ch.mfrey.bean.ad.BeanPropertyDescriptor;
import ch.mfrey.jpa.query.builder.JoinBuilder;
import ch.mfrey.jpa.query.builder.MapJoinBuilder;
import ch.mfrey.jpa.query.builder.SimpleJoinBuilder;
import ch.mfrey.jpa.query.definition.CriteriaDefinition;
import ch.mfrey.jpa.query.definition.CriteriaDefinitionFactory;
import ch.mfrey.jpa.query.model.Criteria;
import ch.mfrey.jpa.query.model.Query;

/**
 * The Class QueryTranslator.
 *
 * @author Martin Frey
 */
@Service
public class QueryTranslator {

    /** The criteria property factory. */
    @Autowired
    private CriteriaDefinitionFactory criteriaDefinitionFactory;

    protected void appendExplicitRestrictions(final StringBuilder resultingRestrictions, final String entitySynonym,
            final List<String> explicitRestrictions) {
        if (resultingRestrictions.length() == 0) {
            resultingRestrictions.append(" WHERE ("); //$NON-NLS-1$
        } else {
            resultingRestrictions.append(" AND ("); //$NON-NLS-1$
        }
        for (int i = 0; i < explicitRestrictions.size(); i++) {
            if (i > 0) {
                resultingRestrictions.append(" AND "); //$NON-NLS-1$
            }
            resultingRestrictions
                    .append('(').append(explicitRestrictions.get(i).replaceAll("|synonym|", entitySynonym)) //$NON-NLS-1$
                    .append(')');
        }
        resultingRestrictions.append(')');
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected void appendQueryRestrictions(final StringBuilder resultingRestrictions, final Query<?> query) {
        int brackets = 0;
        for (int position = 0; position < query.getCriterias().size(); position++) {
            Criteria<?> criteria = query.getCriterias().get(position);

            // ignore not filled filter
            if (criteria.getParameter() == null) {
                continue;
            }

            if (resultingRestrictions.length() == 0) {
                resultingRestrictions.append(" WHERE ("); //$NON-NLS-1$
            } else {
                resultingRestrictions.append(' ');
                resultingRestrictions.append(criteria.getLinkOperator());
                resultingRestrictions.append(' ');
            }
            if (criteria.isBracketOpen()) {
                resultingRestrictions.append('(');
                brackets++;
            }

            CriteriaDefinition definition = criteriaDefinitionFactory.getCriteriaDefinition(query.getEntityClass(),
                    criteria.getCriteriaKey());
            StringBuilder restriction = definition.getRestriction(query, criteria);
            replacePosition(restriction, position);
            resultingRestrictions.append(restriction);

            if (criteria.isBracketClose()) {
                resultingRestrictions.append(')');
                brackets--;
            }

        }
        if (resultingRestrictions.length() > 0) {
            // if the user just closed some brackets but never
            // opened some correct the brackets with placing the forgotten
            // ones directly after the WHERE
            if (brackets < 0) {
                int whereIndex = resultingRestrictions.indexOf("WHERE (") + 6; //$NON-NLS-1$
                while (brackets < 0) {
                    resultingRestrictions.insert(whereIndex, "("); //$NON-NLS-1$
                    brackets++;
                }
            }
            // close the brackets if the user just opened some correct it
            // with placing the forgotten ones at the end
            while (brackets > 0) {
                resultingRestrictions.append(')');
                brackets--;
            }
            // This is the general bracket from the initial where
            // clause
            resultingRestrictions.append(')');
        }
    }

    protected StringBuilder replacePosition(StringBuilder restriction, int position) {
        int start;
        while ((start = restriction.indexOf("|POSITION|")) != -1) {
            restriction.replace(start, start + 10, String.valueOf(position));
        }
        return restriction;
    }

    protected List<String> getJoins(Query<?> query, Criteria<?> criteria, CriteriaDefinition<Criteria<?>> definition) {
        List<String> joins = new ArrayList<>();
        String[] links = criteria.getCriteriaKey().split("\\.");
        Assert.isTrue(links.length != definition.getBeanPropertyDescriptors().size() + 1,
                "Not same length in links and propertyDescriptors");
        String synonym = query.getSynonym();
        List<BeanPropertyDescriptor> propertyDescriptors = definition.getBeanPropertyDescriptors();
        for (int i = 0; i < links.length - 1; i++) {
            String link = links[i];
            BeanPropertyDescriptor pd = propertyDescriptors.get(i);
            String nextSynonym = synonym + "_" + link.replaceAll(AccessorDescriptorBuilder.INDEXED_ACCESSOR_PART, "");
            JoinBuilder joinBuilder = getJoinBuilder(link, pd);
            StringBuilder join = joinBuilder.buildJoin(link, pd, synonym, nextSynonym);
            joins.add(join.toString());
            synonym = nextSynonym;
        }
        return joins;
    }

    private JoinBuilder getJoinBuilder(String link, BeanPropertyDescriptor pd) {
        for (JoinBuilder jb : joinBuilders) {
            if (jb.supports(link, pd)) {
                return jb;
            }
        }
        throw new IllegalArgumentException(
                "Not JoinBuilder found for link " + link + " and PropertyDescriptor " + pd.getName());
    }

    private List<JoinBuilder> joinBuilders = Arrays.asList(new MapJoinBuilder(), new SimpleJoinBuilder());

    private static final String QUERY_SELECT = "SELECT ";

    private static final String QUERY_WHERE = " WHERE ";

    private static final String QUERY_FROM = " FROM ";

    private static final Pattern SELECT_PATTERN = Pattern.compile("select\\s+(\\w+)\\s+from", Pattern.CASE_INSENSITIVE); //$NON-NLS-1$

    public String buildCountQuery(Query<?> query) {
        String ejbql = buildQuery(query);
        Matcher m = SELECT_PATTERN.matcher(ejbql);
        if (m.find()) {
            String subject = m.group(1);
            ejbql = new StringBuilder(ejbql.length()).append("SELECT count(").append(subject).append(") FROM ") //$NON-NLS-1$ //$NON-NLS-2$
                    .append(ejbql.substring(m.end())).toString();
        }
        return ejbql;
    }

    public String buildQuery(final Query<?> query) {
        if (query == null) {
            throw new IllegalArgumentException("Query cannot be null"); //$NON-NLS-1$
        }
        String entityName = query.getEntityClass().getSimpleName();
        String entitySynonym = query.getSynonym();
        StringBuilder inner = new StringBuilder(64);
        inner.append(QUERY_SELECT).append(entitySynonym)
                .append(QUERY_FROM)
                .append(entityName)
                .append(' ')
                .append(entitySynonym);
        inner.append(buildJoins(query));
        inner.append(buildWhereClause(query));
        if (!query.needsSubselect()) {
            return inner.toString();
        }

        StringBuilder full = new StringBuilder(64);
        full.append(QUERY_SELECT)
                .append(entitySynonym)
                .append(QUERY_FROM)
                .append(entityName)
                .append(' ')
                .append(entitySynonym)
                .append(QUERY_WHERE)
                .append(entitySynonym)
                .append(" IN (");

        full.append(inner);
        full.append(')');
        return full.toString();
    }

    protected <E extends Criteria<?>> String buildJoins(final Query<?> query) {
        StringBuilder joinsPart = new StringBuilder();
        List<String> appliedJoins = new ArrayList<>();
        for (int position = 0; position < query.getCriterias().size(); position++) {
            Criteria<?> criteria = query.getCriterias().get(position);
            // ignore not filled filter
            if (criteria.getParameter() == null) {
                continue;
            }
            CriteriaDefinition<Criteria<?>> definition = criteriaDefinitionFactory.getCriteriaDefinition(
                    query.getEntityClass(),
                    criteria.getCriteriaKey());
            for (String join : getJoins(query, criteria, definition)) {
                if (!appliedJoins.contains(join)) {
                    joinsPart.append(" JOIN ").append(join);
                    appliedJoins.add(join);
                }
            }
        }
        return joinsPart.toString();
    }

    protected String buildWhereClause(final Query<?> query) {
        StringBuilder whereClause = new StringBuilder();
        if (!query.getCriterias().isEmpty()) {
            appendQueryRestrictions(whereClause, query);
        }
        return whereClause.toString();
    }

}

package ch.mfrey.jpa.query;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ch.mfrey.jpa.query.model.Query;
import ch.mfrey.jpa.query.support.SpringContextExpressionEvaluator;

@Service
public class QueryService {

    @Autowired
    private SpringContextExpressionEvaluator expressionEvaluator;

    @Autowired
    private QueryTranslator queryTranslator;

    @PersistenceContext
    private EntityManager entityManager;

    protected String getParameterName(final int loc) {
        return "el" + (loc + 1); //$NON-NLS-1$
    }

    @SuppressWarnings("unchecked")
    public <E> TypedQuery<E> parseQuery(Query<E> query) {
        String queryString = queryTranslator.buildQuery(query);
        return (TypedQuery<E>) parseAndAssignQuery(queryString, query, false);
    }

    @SuppressWarnings("unchecked")
    public <E> TypedQuery<Long> parseCountQuery(Query<E> query) {
        String queryString = queryTranslator.buildCountQuery(query);
        return (TypedQuery<Long>) parseAndAssignQuery(queryString, query, true);
    }

    protected <E> TypedQuery<?> parseAndAssignQuery(String queryString, Query<E> query, boolean forCountQuery) {
        StringTokenizer tokens = new StringTokenizer(queryString, "{}", true); //$NON-NLS-1$
        StringBuilder queryBuilder = new StringBuilder(queryString.length());
        Map<String, String> parameters = new HashMap<>();
        while (tokens.hasMoreTokens()) {
            String token = tokens.nextToken();
            if ("{".equals(token) && tokens.hasMoreTokens()) { //$NON-NLS-1$
                String expressionToken = tokens.nextToken();
                if (!tokens.hasMoreTokens()) {
                    queryBuilder.append(token).append(expressionToken);
                } else {
                    String parameterName = getParameterName(parameters.size());
                    queryBuilder.append(':').append(parameterName);
                    parameters.put(parameterName, expressionToken);
                    tokens.nextToken();
                }
            } else {
                queryBuilder.append(token);
            }
        }
        Class<?> returnType = forCountQuery ? Long.class : query.getEntityClass();
        TypedQuery<?> jpaQuery = entityManager.createQuery(queryBuilder.toString(), returnType);
        assignParameters(jpaQuery, query, parameters);
        if (!forCountQuery && query.getMaxResults() != null) {
            jpaQuery.setMaxResults(query.getMaxResults());
        }
        return jpaQuery;
    }

    protected <E, R> void assignParameters(final TypedQuery<R> jpaQuery, Query<E> query,
            Map<String, String> parameterExpressions) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("query", query); //$NON-NLS-1$
        parameterExpressions.entrySet().forEach(entry -> {
            Object result = expressionEvaluator.evaluate(entry.getValue(), variables);
            jpaQuery.setParameter(entry.getKey(), result);
        });
    }

    @Transactional(readOnly = true)
    public <E> Long getResultCount(final Query<E> query) {
        return (Long) parseCountQuery(query).getSingleResult();
    }

    @Transactional(readOnly = true)
    public <E> List<E> getResultList(final Query<E> query) {
        return parseQuery(query).getResultList();
    }

}

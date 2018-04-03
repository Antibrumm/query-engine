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

    public javax.persistence.Query parseQuery(Query query) {
        return parseQuery(query, null);
    }

    public javax.persistence.Query parseQuery(Query query, List<String> restrictions) {
        Map<String, String> parameters = new HashMap<>();
        String queryString = queryTranslator.buildQuery(query, restrictions);
        StringTokenizer tokens = new StringTokenizer(queryString, "{}", true); //$NON-NLS-1$
        StringBuilder queryBuilder = new StringBuilder(queryString.length());
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

        TypedQuery<?> jpaQuery = entityManager.createQuery(queryBuilder.toString(), query.getEntityClass());
        assignParameters(jpaQuery, query, parameters);
        return jpaQuery;
    }

    protected void assignParameters(final javax.persistence.Query jpaQuery, Query query,
            Map<String, String> parameterExpressions) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("query", query); //$NON-NLS-1$
        parameterExpressions.entrySet().forEach(entry -> {
            Object result = expressionEvaluator.evaluate(entry.getValue(), variables);
            jpaQuery.setParameter(entry.getKey(), result);
        });
    }

}

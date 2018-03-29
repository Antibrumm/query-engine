package ch.mfrey.jpa.query.support;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.expression.BeanExpressionContextAccessor;
import org.springframework.context.expression.BeanFactoryAccessor;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.context.expression.EnvironmentAccessor;
import org.springframework.context.expression.MapAccessor;
import org.springframework.core.convert.ConversionService;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.spel.SpelParserConfiguration;
import org.springframework.expression.spel.standard.SpelExpression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.expression.spel.support.StandardTypeConverter;
import org.springframework.stereotype.Service;

/**
 * The Class SpringContextExpressionEvaluator.
 *
 * @author Martin Frey
 */
@Service
public class SpringContextExpressionEvaluator {

    /** The Constant CACHE_NAME. */
    private static final String CACHE_NAME = "LOCAL_EXPRESSIONS"; //$NON-NLS-1$

    /** The Constant PARSER. */
    public static final SpelExpressionParser PARSER = new SpelExpressionParser(
            new SpelParserConfiguration(false, false));

    /** The application context. */
    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private Optional<ConversionService> conversionService;

    @Autowired
    private Optional<CacheManager> cacheManager;

    /**
     * Gets the context.
     *
     * @return the context
     */
    public StandardEvaluationContext buildContext() {
        StandardEvaluationContext sec = new StandardEvaluationContext();
        sec.addPropertyAccessor(new BeanExpressionContextAccessor());
        sec.addPropertyAccessor(new BeanFactoryAccessor());
        sec.addPropertyAccessor(new MapAccessor());
        sec.addPropertyAccessor(new EnvironmentAccessor());
        sec.setBeanResolver(new BeanFactoryResolver(applicationContext));

        if (conversionService.isPresent()) {
            sec.setTypeConverter(new StandardTypeConverter(conversionService.get()));
        }
        return sec;
    }

    /**
     * Evaluate.
     *
     * @param expression
     *            the expression
     * @param variables
     *            the variables
     * @return the object
     * @throws EvaluationException
     *             the evaluation exception
     */
    public Object evaluate(final String expression, final Map<String, Object> variables) throws EvaluationException {
        StandardEvaluationContext context = buildContext();
        if (variables != null) {
            context.setVariables(variables);
        }
        return evaluateOnContext(expression, context);
    }

    /**
     * Evaluate.
     *
     * @param <E>
     *            the element type
     * @param expression
     *            the expression
     * @param variables
     *            the variables
     * @param expectedResultType
     *            the expected result type
     * @return the e
     * @throws EvaluationException
     *             the evaluation exception
     */
    public <E> E evaluate(final String expression, final Map<String, Object> variables,
            final Class<E> expectedResultType) throws EvaluationException {
        StandardEvaluationContext context = buildContext();
        if (variables != null) {
            context.setVariables(variables);
        }
        return evaluateOnContext(expression, context, expectedResultType);
    }

    /**
     * Evaluate on context.
     *
     * @param expression
     *            the expression
     * @param context
     *            the context
     * @return the object
     */
    public Object evaluateOnContext(final String expression, final StandardEvaluationContext context) {
        SpelExpression spel = getExpression(expression);
        return spel.getValue(context);
    }

    /**
     * Evaluate on context.
     *
     * @param <E>
     *            the element type
     * @param expression
     *            the expression
     * @param context
     *            the context
     * @param expectedResultType
     *            the expected result type
     * @return the e
     */
    public <E> E evaluateOnContext(final String expression, final StandardEvaluationContext context,
            final Class<E> expectedResultType) {
        SpelExpression spel = getExpression(expression);
        return spel.getValue(context, expectedResultType);
    }

    /**
     * Gets the expression.
     *
     * @param spelExpression
     *            the spel expression
     * @return the expression
     */
    private SpelExpression getExpression(final String spelExpression) {
        SpelExpression exp = null;
        Cache cache = null;
        if (cacheManager.isPresent()) {
            cache = cacheManager.get().getCache(CACHE_NAME);
            if (cache != null) {
                ValueWrapper wrapper = cache.get(spelExpression);
                if (wrapper != null) {
                    exp = (SpelExpression) wrapper.get();
                }
            }
        }

        if (exp == null) {
            exp = (SpelExpression) PARSER.parseExpression(spelExpression);
            if (cache != null && exp != null) {
                cache.put(spelExpression, exp);
            }
            if (exp == null) {
                throw new IllegalArgumentException("SPEL cannot be null: " + spelExpression);
            }
        }

        return exp;

    }
}

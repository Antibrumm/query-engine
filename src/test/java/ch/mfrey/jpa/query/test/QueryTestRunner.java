package ch.mfrey.jpa.query.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

import org.hibernate.proxy.HibernateProxyHelper;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import ch.mfrey.bean.ad.BeanPropertyDescriptor;
import ch.mfrey.jpa.query.QueryService;
import ch.mfrey.jpa.query.QueryTranslator;
import ch.mfrey.jpa.query.builder.CriteriaBuilder;
import ch.mfrey.jpa.query.builder.QueryBuilder;
import ch.mfrey.jpa.query.definition.CriteriaDefinition;
import ch.mfrey.jpa.query.definition.CriteriaDefinitionFactory;
import ch.mfrey.jpa.query.model.CriteriaSimple;
import ch.mfrey.jpa.query.model.Query;
import ch.mfrey.jpa.query.test.entity.A;
import ch.mfrey.jpa.query.test.entity.B;
import ch.mfrey.jpa.query.test.entity.C;

/**
 * The Class QueryTestRunner.
 *
 */
@Component
public class QueryTestRunner {

    private static final Logger log = LoggerFactory.getLogger(QueryTestRunner.class);

    @Autowired
    private QueryService queryService;

    @Autowired
    private QueryTranslator queryTranslator;

    @Autowired
    private DataInitializer dataInitializer;

    @Autowired
    private CriteriaDefinitionFactory criteriaDefinitionFactory;

    /**
     * Filter.
     *
     * @param cp
     *            the cp
     * @param beanWrapper
     *            the bean entityQuery
     * @param totalCount
     *            the total count
     * @param doLoadEntitiesAndValidate
     *            the do load entities and validate
     * @param failedCriteriaProperties
     *            the failed criteria properties
     * @return true, if successful
     */
    private <E> boolean filter(final CriteriaDefinition<?> definition, final BeanWrapperImpl beanWrapper,
            final Long totalCount,
            final boolean doLoadEntitiesAndValidate, final List<CriteriaDefinition<?>> failedCriteriaProperties) {
        log.debug("Testing {}", definition.getCriteriaKey());
        @SuppressWarnings("unchecked")
        Class<E> queryClass = HibernateProxyHelper.getClassWithoutInitializingProxy(beanWrapper.getWrappedInstance());
        QueryBuilder<E> builder = QueryBuilder.forEntity(queryClass);
        boolean filterOk = false;
        try {
            Object parameter = getSearchParameter(definition, new BeanWrapperImpl(beanWrapper.getWrappedInstance()));
            CriteriaBuilder<E, Object, CriteriaSimple<Object>> criteria =
                    builder.withCriteria(definition.getCriteriaKey(), parameter);

            Long equalsCount = -1l;
            Long notEqualsCount = -1l;
            List<Object> operatorCounts = new ArrayList<>();
            boolean first = true;
            for (String operator : definition.getOperators()) {
                criteria.withOperator(operator);
                Query<E> query = builder.build();
                if (first) {
                    log.debug(queryTranslator.buildCountQuery(query));
                    first = false;
                } else {
                    log.trace(queryTranslator.buildCountQuery(query));
                }

                Long count = queryService.getResultCount(query);
                if ("=".equals(operator)) {
                    equalsCount = count;
                } else if ("!=".equals(operator)) {
                    notEqualsCount = count;
                }
                operatorCounts.add(operator);
                operatorCounts.add(count);

                if (doLoadEntitiesAndValidate) {
                    List<?> entities = queryService.getResultList(query);
                    Assert.assertEquals(count.intValue(), entities.size());
                }
            }
            if (definition.getBeanPropertyDescriptors().size() == 1 && (equalsCount + notEqualsCount != totalCount)) {
                log.error("Error in Filter: {} (= {}) + (!= {}) == {} [{}]",
                        definition.getCriteriaKey(), equalsCount, notEqualsCount, totalCount,
                        StringUtils.collectionToCommaDelimitedString(operatorCounts));
                failedCriteriaProperties.add(definition);
            } else {
                log.trace("Filter: {} (= {}) + (!= {}) == {} [{}]",
                        definition.getCriteriaKey(), equalsCount, notEqualsCount, totalCount,
                        StringUtils.collectionToCommaDelimitedString(operatorCounts));
                filterOk = true;
            }
        } catch (Exception e) {
            log.error("Error", e);
            failedCriteriaProperties.add(definition);
        }
        return filterOk;
    }

    /**
     * Builds the Criteria from the criteria definition.
     *
     * @param cp
     *            the cp
     * @param beanWrapper
     *            the bean entityQuery
     * @return the from criteria type
     */
    private Object getSearchParameter(final CriteriaDefinition<?> definition, final BeanWrapperImpl beanWrapper) {
        // Dive in to find a value candidate
        Object propertyValue = null;
        for (BeanPropertyDescriptor pd : definition.getBeanPropertyDescriptors()) {
            Class<?> propertyType = pd.getPropertyType();
            propertyValue = beanWrapper.getPropertyValue(pd.getName());

            if (propertyValue == null) {
                return null;
            }

            // In case of a map or collection take the first value from the
            // iterator
            if (Map.class.isAssignableFrom(propertyType)) {
                Map<?, ?> coll = (Map<?, ?>) propertyValue;
                if (coll.isEmpty()) {
                    propertyValue = null;
                    break;
                } else {
                    propertyValue = coll.values().iterator().next();
                }
            } else if (Collection.class.isAssignableFrom(propertyType)) {
                Collection<?> coll = (Collection<?>) propertyValue;
                if (coll.isEmpty()) {
                    propertyValue = null;
                    break;
                } else {
                    propertyValue = coll.iterator().next();
                }
            }
            beanWrapper.setWrappedInstance(propertyValue);
        }
        return propertyValue;
    }

    /**
     * Gets the total count.
     *
     * @param <E>
     *            the element type
     * @param entityClass
     *            the entity class
     * @return the total count
     */
    public <E> Long getTotalCount(final Class<E> entityClass) {
        return queryService.getResultCount(QueryBuilder.forEntity(entityClass).build());
    }

    /**
     * Test operation filters.
     *
     * @param entityClass
     *            the entity class
     * @param maxFilterLevel
     *            the max filter level
     * @return true, if successful
     */
    private <E> boolean testEntityFilters(final Class<E> entityClass, final int maxFilterLevel) {
        // Operation
        Long totalCount = getTotalCount(entityClass);
        log.debug("{}s total: {}", entityClass.getSimpleName(), totalCount);
        if (totalCount == 0) {
            log.warn("Query Test for '{}' cannot be run as there are 0 entities.",
                    entityClass.getSimpleName());
            return true;
        }

        List<CriteriaDefinition<?>> failedCriteriaProperties = new ArrayList<>();
        E testingEntity = getOneEntity(entityClass);

        List<CriteriaDefinition<?>> definitions = criteriaDefinitionFactory.getCriteriaDefinitions(entityClass);
        BeanWrapperImpl beanWrapper = new BeanWrapperImpl();
        for (CriteriaDefinition<?> definition : definitions) {
            if (maxFilterLevel == -1
                    || definition.getBeanPropertyDescriptors().size() <= maxFilterLevel) {
                beanWrapper.setWrappedInstance(testingEntity);
                filter(definition, beanWrapper, totalCount, false, failedCriteriaProperties);
            } else {
                log.debug("Ignoring accessor: {} (maxFilterLevel: {})", definition.getCriteriaKey(),
                        maxFilterLevel);
            }
        }
        if (!failedCriteriaProperties.isEmpty()) {
            for (CriteriaDefinition<?> definition : failedCriteriaProperties) {
                log.error("Query failed: {}", definition);
            }
        }
        return failedCriteriaProperties.isEmpty();
    }

    /**
     * Test filters.
     *
     * @param entityClass
     *            the entity class
     * @param maxFilterLevel
     *            the max filter level
     * @return true, if successful
     */
    @Transactional
    public boolean testFilters(final Class<?> entityClass, final int maxFilterLevel) {
        log.info("Filter Test ({} - {})", entityClass.getSimpleName());
        boolean allFiltersOk = testEntityFilters(entityClass, maxFilterLevel);
        Assert.assertTrue("Some Filters had issues!", allFiltersOk);
        return allFiltersOk;
    }

    /**
     * Gets the one entity.
     *
     * @param <E>
     *            the element type
     * @param entityClass
     *            the entity class
     * @return the one entity
     */
    public <E> E getOneEntity(final Class<E> entityClass) {
        QueryBuilder<E> builder = QueryBuilder.forEntity(entityClass).setMaxResults(1);
        List<E> resultList = queryService.getResultList(builder.build());
        if (resultList.isEmpty()) {
            throw new NoResultException("No entity for class " + entityClass.getSimpleName() + " not found"); //$NON-NLS-2$
        } else {
            return resultList.get(0);
        }
    }

    @PostConstruct
    public void initData() {
        dataInitializer.initSomeEntities();
    }

    @Component
    public static class DataInitializer {

        @PersistenceContext
        private EntityManager em;

        private Random random = new Random();

        @Transactional
        public void initSomeEntities() {
            for (int i = 0; i < 100; i++) {
                A a = new A();
                a.setActive(random.nextBoolean());
                a.setTitle("A Title -" + i);
                em.persist(a);

                B b = new B();
                b.setActive(random.nextBoolean());
                b.setTitle("B Title - " + i);
                b.setA(a);
                em.persist(b);

                C c = new C();
                c.setTitle("C Title - " + i);
                c.setB1(b);
                c.setActive(random.nextBoolean());
                em.persist(c);
            }
        }
    }
}

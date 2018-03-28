package ch.mfrey.jpa.query.test;

import java.time.LocalDate;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ch.mfrey.jpa.query.CriteriaDefinitionFactory;
import ch.mfrey.jpa.query.QueryBuilder;
import ch.mfrey.jpa.query.definition.CriteriaDefinition;
import ch.mfrey.jpa.query.model.CriteriaBoolean;
import ch.mfrey.jpa.query.model.CriteriaDate;
import ch.mfrey.jpa.query.model.CriteriaNumber;
import ch.mfrey.jpa.query.model.CriteriaString;
import ch.mfrey.jpa.query.model.Query;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { QueryTest.Context.class })
public class QueryTest {

    @Configuration
    @ComponentScan(basePackageClasses = { CriteriaDefinitionFactory.class })
    public static class Context {

    }

    @Autowired
    private CriteriaDefinitionFactory criteriaDefinitionFactory;

    @Autowired
    private QueryBuilder queryBuilder;

    @Test
    public void testA() {
        List<CriteriaDefinition<?>> criteriaDefinitions = criteriaDefinitionFactory.getCriteriaDefinitions(A.class);
        Assert.assertEquals(3, criteriaDefinitions.size());

        Query query = new Query();
        query.setEntityClass(A.class);
        CriteriaString title = new CriteriaString();
        title.setPropertyAccessor("title");
        title.setParameter("A");
        query.getCriterias().add(title);
        String jpaQuery = queryBuilder.buildJpaQuery(query);
        Assert.assertEquals("SELECT a FROM A a WHERE (upper(a.title) = upper({#query.criterias[0].parameter}))",
                jpaQuery);

        query = new Query();
        query.setEntityClass(A.class);
        CriteriaNumber id = new CriteriaNumber();
        id.setPropertyAccessor("id");
        id.setParameter(1);
        query.getCriterias().add(id);
        jpaQuery = queryBuilder.buildJpaQuery(query);
        Assert.assertEquals("SELECT a FROM A a WHERE (a.id = {#query.criterias[0].parameter})", jpaQuery);

        query = new Query();
        query.setEntityClass(A.class);
        CriteriaBoolean active = new CriteriaBoolean();
        active.setPropertyAccessor("active");
        active.setParameter(true);
        query.getCriterias().add(active);
        jpaQuery = queryBuilder.buildJpaQuery(query);
        Assert.assertEquals("SELECT a FROM A a WHERE (a.active = {#query.criterias[0].parameter})", jpaQuery);

        query = new Query();
        query.setEntityClass(A.class);
        query.getCriterias().add(title);
        query.getCriterias().add(id);
        query.getCriterias().add(active);
        jpaQuery = queryBuilder.buildJpaQuery(query);
        Assert.assertEquals("SELECT a FROM A a"
                + " WHERE (upper(a.title) = upper({#query.criterias[0].parameter})"
                + " AND a.id = {#query.criterias[1].parameter}"
                + " AND a.active = {#query.criterias[2].parameter})", jpaQuery);

    }

    @Test
    public void testB() {
        List<CriteriaDefinition<?>> criteriaDefinitions = criteriaDefinitionFactory.getCriteriaDefinitions(B.class);
        Assert.assertEquals(6, criteriaDefinitions.size());

        Query query = new Query();
        query.setEntityClass(B.class);
        CriteriaString title = new CriteriaString();
        title.setPropertyAccessor("title");
        title.setParameter("B");
        query.getCriterias().add(title);
        String jpaQuery = queryBuilder.buildJpaQuery(query);
        Assert.assertEquals("SELECT b FROM B b WHERE (upper(b.title) = upper({#query.criterias[0].parameter}))",
                jpaQuery);

        query = new Query();
        query.setEntityClass(B.class);
        CriteriaNumber id = new CriteriaNumber();
        id.setPropertyAccessor("a.id");
        id.setParameter(1);
        query.getCriterias().add(id);
        jpaQuery = queryBuilder.buildJpaQuery(query);
        Assert.assertEquals(
                "SELECT b FROM B b, Dummy WHERE 1=1 AND b IN (SELECT b FROM B b JOIN b.a b_a WHERE (b_a.id = {#query.criterias[0].parameter}))",
                jpaQuery);

        query = new Query();
        query.setEntityClass(B.class);
        id = new CriteriaNumber();
        id.setPropertyAccessor("a.id");
        id.setParameter(1);
        query.getCriterias().add(id);
        title = new CriteriaString();
        title.setPropertyAccessor("a.title");
        title.setParameter("A");
        query.getCriterias().add(title);
        jpaQuery = queryBuilder.buildJpaQuery(query);
        Assert.assertEquals(
                "SELECT b FROM B b, Dummy WHERE 1=1 AND b IN ("
                        + "SELECT b FROM B b JOIN b.a b_a"
                        + " WHERE (b_a.id = {#query.criterias[0].parameter}"
                        + " AND upper(b_a.title) = upper({#query.criterias[1].parameter})))",
                jpaQuery);

    }

    @Test
    public void testC() {
        List<CriteriaDefinition<?>> criteriaDefinitions = criteriaDefinitionFactory.getCriteriaDefinitions(C.class);
        Assert.assertEquals(15, criteriaDefinitions.size());
        Query query = new Query();
        query.setEntityClass(C.class);
        CriteriaNumber id = new CriteriaNumber();
        id.setPropertyAccessor("b1.id");
        id.setParameter(1);
        query.getCriterias().add(id);
        CriteriaString title = new CriteriaString();
        title.setPropertyAccessor("b1.a.title");
        title.setParameter("A");
        query.getCriterias().add(title);
        String jpaQuery = queryBuilder.buildJpaQuery(query);
        Assert.assertEquals(
                "SELECT c FROM C c, Dummy WHERE 1=1 AND c IN ("
                        + "SELECT c FROM C c JOIN c.b1 c_b1 JOIN c_b1.a c_b1_a"
                        + " WHERE (c_b1.id = {#query.criterias[0].parameter}"
                        + " AND upper(c_b1_a.title) = upper({#query.criterias[1].parameter})))",
                jpaQuery);

        query = new Query();
        query.setEntityClass(C.class);
        id = new CriteriaNumber();
        id.setPropertyAccessor("b1.id");
        id.setParameter(1);
        query.getCriterias().add(id);
        title = new CriteriaString();
        title.setPropertyAccessor("b2.a.title");
        title.setParameter("A");
        query.getCriterias().add(title);
        jpaQuery = queryBuilder.buildJpaQuery(query);
        Assert.assertEquals(
                "SELECT c FROM C c, Dummy WHERE 1=1 AND c IN ("
                        + "SELECT c FROM C c JOIN c.b1 c_b1 JOIN c.b2 c_b2 JOIN c_b2.a c_b2_a"
                        + " WHERE (c_b1.id = {#query.criterias[0].parameter}"
                        + " AND upper(c_b2_a.title) = upper({#query.criterias[1].parameter})))",
                jpaQuery);
    }

    @Test
    public void testOne() {
        List<CriteriaDefinition<?>> criteriaDefinitions = criteriaDefinitionFactory.getCriteriaDefinitions(One.class);
        Assert.assertEquals(4, criteriaDefinitions.size());

        Query query = new Query();
        query.setEntityClass(One.class);
        CriteriaNumber id = new CriteriaNumber();
        id.setPropertyAccessor("manys.id");
        id.setParameter(1);
        query.getCriterias().add(id);
        String jpaQuery = queryBuilder.buildJpaQuery(query);
        Assert.assertEquals(
                "SELECT one FROM One one, Dummy WHERE 1=1 AND one IN ("
                        + "SELECT one FROM One one JOIN one.manys one_manys WHERE"
                        + " (one_manys.id = {#query.criterias[0].parameter}))",
                jpaQuery);

    }

    @Test
    public void testDate() {
        List<CriteriaDefinition<?>> criteriaDefinitions = criteriaDefinitionFactory.getCriteriaDefinitions(Date.class);
        Assert.assertEquals(3, criteriaDefinitions.size());

        Query query = new Query();
        query.setEntityClass(Date.class);
        CriteriaDate date = new CriteriaDate();
        date.setPropertyAccessor("date");
        date.setParameter(LocalDate.now());
        query.getCriterias().add(date);
        String jpaQuery = queryBuilder.buildJpaQuery(query);
        Assert.assertEquals(
                "SELECT date FROM Date date WHERE (date.date = {#query.criterias[0].parameter})",
                jpaQuery);

        date.setOperator(">=");
        jpaQuery = queryBuilder.buildJpaQuery(query);
        Assert.assertEquals(
                "SELECT date FROM Date date WHERE (date.date >= {#query.criterias[0].parameter})",
                jpaQuery);
 
        date.setOperator("<=");
        jpaQuery = queryBuilder.buildJpaQuery(query);
        Assert.assertEquals(
                "SELECT date FROM Date date WHERE (date.date <= {#query.criterias[0].parameter})",
                jpaQuery);
        
        query = new Query();
        query.setEntityClass(Date.class);
        date = new CriteriaDate();
        date.setPropertyAccessor("dateTime");
        date.setParameter(LocalDate.now());
        query.getCriterias().add(date);
        jpaQuery = queryBuilder.buildJpaQuery(query);
        Assert.assertEquals(
                "SELECT date FROM Date date WHERE (date.dateTime BETWEEN {#query.criterias[0].minDate} AND {#query.criterias[0].maxDate})",
                jpaQuery);

        date.setOperator(">=");
        jpaQuery = queryBuilder.buildJpaQuery(query);
        Assert.assertEquals(
                "SELECT date FROM Date date WHERE (date.dateTime >= {#query.criterias[0].maxDate})",
                jpaQuery);
 
        date.setOperator("<=");
        jpaQuery = queryBuilder.buildJpaQuery(query);
        Assert.assertEquals(
                "SELECT date FROM Date date WHERE (date.dateTime <= {#query.criterias[0].minDate})",
                jpaQuery);
 }
}

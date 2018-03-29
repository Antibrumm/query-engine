package ch.mfrey.jpa.query.test;

import java.time.LocalDate;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ch.mfrey.jpa.query.CriteriaDefinitionFactory;
import ch.mfrey.jpa.query.QueryBuilder;
import ch.mfrey.jpa.query.QueryService;
import ch.mfrey.jpa.query.definition.CriteriaDefinition;
import ch.mfrey.jpa.query.model.CriteriaBoolean;
import ch.mfrey.jpa.query.model.CriteriaDate;
import ch.mfrey.jpa.query.model.CriteriaLong;
import ch.mfrey.jpa.query.model.CriteriaString;
import ch.mfrey.jpa.query.model.Query;
import ch.mfrey.jpa.query.test.entity.A;
import ch.mfrey.jpa.query.test.entity.ArrayOne;
import ch.mfrey.jpa.query.test.entity.B;
import ch.mfrey.jpa.query.test.entity.C;
import ch.mfrey.jpa.query.test.entity.CollectionOne;
import ch.mfrey.jpa.query.test.entity.Date;
import ch.mfrey.jpa.query.test.entity.MapOne;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ContextConfig.class })
public class QueryTest {

    @Autowired
    private CriteriaDefinitionFactory criteriaDefinitionFactory;

    @Autowired
    private QueryBuilder queryBuilder;

    @Autowired
    private QueryService queryService;

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
        String jpaQuery = queryBuilder.buildQuery(query);
        Assert.assertEquals("SELECT a FROM A a WHERE (upper(a.title) = upper({#query.criterias[0].parameter}))",
                jpaQuery);
        queryService.parseQuery(query);

        query = new Query();
        query.setEntityClass(A.class);
        CriteriaLong id = new CriteriaLong();
        id.setPropertyAccessor("id");
        id.setParameter(1L);
        query.getCriterias().add(id);
        jpaQuery = queryBuilder.buildQuery(query);
        Assert.assertEquals("SELECT a FROM A a WHERE (a.id = {#query.criterias[0].parameter})", jpaQuery);
        queryService.parseQuery(query);

        query = new Query();
        query.setEntityClass(A.class);
        CriteriaBoolean active = new CriteriaBoolean();
        active.setPropertyAccessor("active");
        active.setParameter(true);
        query.getCriterias().add(active);
        jpaQuery = queryBuilder.buildQuery(query);
        Assert.assertEquals("SELECT a FROM A a WHERE (a.active = {#query.criterias[0].parameter})", jpaQuery);
        queryService.parseQuery(query);

        query = new Query();
        query.setEntityClass(A.class);
        query.getCriterias().add(title);
        query.getCriterias().add(id);
        query.getCriterias().add(active);
        jpaQuery = queryBuilder.buildQuery(query);
        Assert.assertEquals("SELECT a FROM A a"
                + " WHERE (upper(a.title) = upper({#query.criterias[0].parameter})"
                + " AND a.id = {#query.criterias[1].parameter}"
                + " AND a.active = {#query.criterias[2].parameter})", jpaQuery);
        queryService.parseQuery(query);

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
        String jpaQuery = queryBuilder.buildQuery(query);
        Assert.assertEquals("SELECT b FROM B b WHERE (upper(b.title) = upper({#query.criterias[0].parameter}))",
                jpaQuery);
        queryService.parseQuery(query);

        query = new Query();
        query.setEntityClass(B.class);
        CriteriaLong id = new CriteriaLong();

        id.setPropertyAccessor("a.id");
        id.setParameter(1L);
        query.getCriterias().add(id);
        jpaQuery = queryBuilder.buildQuery(query);
        Assert.assertEquals(
                "SELECT b FROM B b, Dummy WHERE 1=1 AND b IN (SELECT b FROM B b JOIN b.a b_a WHERE (b_a.id = {#query.criterias[0].parameter}))",
                jpaQuery);
        queryService.parseQuery(query);

        query = new Query();
        query.setEntityClass(B.class);
        id = new CriteriaLong();
        id.setPropertyAccessor("a.id");
        id.setParameter(1L);
        query.getCriterias().add(id);
        title = new CriteriaString();
        title.setPropertyAccessor("a.title");
        title.setParameter("A");
        query.getCriterias().add(title);
        jpaQuery = queryBuilder.buildQuery(query);
        Assert.assertEquals(
                "SELECT b FROM B b, Dummy WHERE 1=1 AND b IN ("
                        + "SELECT b FROM B b JOIN b.a b_a"
                        + " WHERE (b_a.id = {#query.criterias[0].parameter}"
                        + " AND upper(b_a.title) = upper({#query.criterias[1].parameter})))",
                jpaQuery);
        queryService.parseQuery(query);

    }

    @Test
    public void testC() {
        List<CriteriaDefinition<?>> criteriaDefinitions = criteriaDefinitionFactory.getCriteriaDefinitions(C.class);
        Assert.assertEquals(15, criteriaDefinitions.size());
        Query query = new Query();
        query.setEntityClass(C.class);
        CriteriaLong id = new CriteriaLong();

        id.setPropertyAccessor("b1.id");
        id.setParameter(1L);
        query.getCriterias().add(id);
        CriteriaString title = new CriteriaString();
        title.setPropertyAccessor("b1.a.title");
        title.setParameter("A");
        query.getCriterias().add(title);
        String jpaQuery = queryBuilder.buildQuery(query);
        Assert.assertEquals(
                "SELECT c FROM C c, Dummy WHERE 1=1 AND c IN ("
                        + "SELECT c FROM C c JOIN c.b1 c_b1 JOIN c_b1.a c_b1_a"
                        + " WHERE (c_b1.id = {#query.criterias[0].parameter}"
                        + " AND upper(c_b1_a.title) = upper({#query.criterias[1].parameter})))",
                jpaQuery);
        queryService.parseQuery(query);

        query = new Query();
        query.setEntityClass(C.class);
        id = new CriteriaLong();
        id.setPropertyAccessor("b1.id");
        id.setParameter(1L);
        query.getCriterias().add(id);
        title = new CriteriaString();
        title.setPropertyAccessor("b2.a.title");
        title.setParameter("A");
        query.getCriterias().add(title);
        jpaQuery = queryBuilder.buildQuery(query);
        Assert.assertEquals(
                "SELECT c FROM C c, Dummy WHERE 1=1 AND c IN ("
                        + "SELECT c FROM C c JOIN c.b1 c_b1 JOIN c.b2 c_b2 JOIN c_b2.a c_b2_a"
                        + " WHERE (c_b1.id = {#query.criterias[0].parameter}"
                        + " AND upper(c_b2_a.title) = upper({#query.criterias[1].parameter})))",
                jpaQuery);
        queryService.parseQuery(query);
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
        String jpaQuery = queryBuilder.buildQuery(query);
        Assert.assertEquals(
                "SELECT date FROM Date date WHERE (date.date = {#query.criterias[0].parameter})",
                jpaQuery);
        queryService.parseQuery(query);

        date.setOperator(">=");
        jpaQuery = queryBuilder.buildQuery(query);
        Assert.assertEquals(
                "SELECT date FROM Date date WHERE (date.date >= {#query.criterias[0].parameter})",
                jpaQuery);
        queryService.parseQuery(query);

        date.setOperator("<=");
        jpaQuery = queryBuilder.buildQuery(query);
        Assert.assertEquals(
                "SELECT date FROM Date date WHERE (date.date <= {#query.criterias[0].parameter})",
                jpaQuery);
        queryService.parseQuery(query);

        query = new Query();
        query.setEntityClass(Date.class);
        date = new CriteriaDate();
        date.setPropertyAccessor("dateTime");
        date.setParameter(LocalDate.now());
        query.getCriterias().add(date);
        jpaQuery = queryBuilder.buildQuery(query);
        Assert.assertEquals(
                "SELECT date FROM Date date WHERE (date.dateTime BETWEEN {#query.criterias[0].minDateTime} AND {#query.criterias[0].maxDateTime})",
                jpaQuery);
        queryService.parseQuery(query);

        date.setOperator(">=");
        jpaQuery = queryBuilder.buildQuery(query);
        Assert.assertEquals(
                "SELECT date FROM Date date WHERE (date.dateTime >= {#query.criterias[0].maxDateTime})",
                jpaQuery);
        queryService.parseQuery(query);

        date.setOperator("<=");
        jpaQuery = queryBuilder.buildQuery(query);
        Assert.assertEquals(
                "SELECT date FROM Date date WHERE (date.dateTime <= {#query.criterias[0].minDateTime})",
                jpaQuery);
        queryService.parseQuery(query);
    }

    @Test
    public void testArrayOne() {
        List<CriteriaDefinition<?>> criteriaDefinitions =
                criteriaDefinitionFactory.getCriteriaDefinitions(ArrayOne.class);
        Assert.assertEquals(4, criteriaDefinitions.size());

        Query query = new Query();
        query.setEntityClass(ArrayOne.class);
        CriteriaLong id = new CriteriaLong();
        id.setPropertyAccessor("manys.id");
        id.setParameter(1L);
        query.getCriterias().add(id);
        String jpaQuery = queryBuilder.buildQuery(query);
        Assert.assertEquals(
                "SELECT arrayOne FROM ArrayOne arrayOne, Dummy WHERE 1=1 AND arrayOne IN ("
                        + "SELECT arrayOne FROM ArrayOne arrayOne"
                        + " JOIN arrayOne.manys arrayOne_manys WHERE"
                        + " (arrayOne_manys.id = {#query.criterias[0].parameter}))",
                jpaQuery);
        queryService.parseQuery(query);
    }

    @Test
    public void testCollectionOne() {
        List<CriteriaDefinition<?>> criteriaDefinitions =
                criteriaDefinitionFactory.getCriteriaDefinitions(MapOne.class);
        Assert.assertEquals(4, criteriaDefinitions.size());

        Query query = new Query();
        query.setEntityClass(CollectionOne.class);
        CriteriaLong id = new CriteriaLong();
        id.setPropertyAccessor("manys.id");
        id.setParameter(1L);
        query.getCriterias().add(id);
        String jpaQuery = queryBuilder.buildQuery(query);
        Assert.assertEquals(
                "SELECT collectionOne FROM CollectionOne collectionOne, Dummy WHERE 1=1 AND collectionOne IN ("
                        + "SELECT collectionOne FROM CollectionOne collectionOne"
                        + " JOIN collectionOne.manys collectionOne_manys WHERE"
                        + " (collectionOne_manys.id = {#query.criterias[0].parameter}))",
                jpaQuery);
        queryService.parseQuery(query);
    }

    @Test
    public void testMapOne() {
        List<CriteriaDefinition<?>> criteriaDefinitions =
                criteriaDefinitionFactory.getCriteriaDefinitions(MapOne.class);
        Assert.assertEquals(4, criteriaDefinitions.size());

        Query query = new Query();
        query.setEntityClass(MapOne.class);
        CriteriaLong id = new CriteriaLong();
        id.setPropertyAccessor("manys[someStr].id");
        id.setParameter(1L);
        query.getCriterias().add(id);
        String jpaQuery = queryBuilder.buildQuery(query);
        Assert.assertEquals(
                "SELECT mapOne FROM MapOne mapOne, Dummy WHERE 1=1 AND mapOne IN ("
                        + "SELECT mapOne FROM MapOne mapOne"
                        + " JOIN mapOne.manys mapOne_manys ON key(mapOne_manys) = 'someStr'"
                        + " WHERE (mapOne_manys.id = {#query.criterias[0].parameter}))",
                jpaQuery);
        queryService.parseQuery(query);
    }
}

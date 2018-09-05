package ch.mfrey.jpa.query.test;

import java.time.LocalDate;
import java.util.List;

import javax.persistence.TypedQuery;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ch.mfrey.jpa.query.QueryService;
import ch.mfrey.jpa.query.QueryTranslator;
import ch.mfrey.jpa.query.builder.CriteriaBuilder;
import ch.mfrey.jpa.query.builder.QueryBuilder;
import ch.mfrey.jpa.query.definition.CriteriaDefinition;
import ch.mfrey.jpa.query.definition.CriteriaDefinitionFactory;
import ch.mfrey.jpa.query.model.CriteriaDate;
import ch.mfrey.jpa.query.model.CriteriaSimple;
import ch.mfrey.jpa.query.model.Query;
import ch.mfrey.jpa.query.test.entity.A;
import ch.mfrey.jpa.query.test.entity.ArrayOne;
import ch.mfrey.jpa.query.test.entity.B;
import ch.mfrey.jpa.query.test.entity.C;
import ch.mfrey.jpa.query.test.entity.CollectionOne;
import ch.mfrey.jpa.query.test.entity.Date;
import ch.mfrey.jpa.query.test.entity.LeftRightNode;
import ch.mfrey.jpa.query.test.entity.MapOne;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ContextConfig.class })
public class QueryTest {

    @Autowired
    private CriteriaDefinitionFactory criteriaDefinitionFactory;

    @Autowired
    private QueryTranslator queryTranslator;

    @Autowired
    private QueryService queryService;

    @Test
    public void testOR() {
        QueryBuilder<A> builder = QueryBuilder
                .forEntity(A.class)
                .andCriteria("title", "A")
                .orCriteria("id", 1L);
        assertAndParse("SELECT a FROM A a"
                + " WHERE (upper(a.title) = upper({#query.criterias[0].parameter})"
                + " OR a.id = {#query.criterias[1].parameter})",
                builder.build());
    }

    @Test
    public void testA() {
        List<CriteriaDefinition<?>> criteriaDefinitions = criteriaDefinitionFactory.getCriteriaDefinitions(A.class);
        Assert.assertEquals(3, criteriaDefinitions.size());
        QueryBuilder<A> builder = QueryBuilder
                .forEntity(A.class)
                .withCriteria("title", "A")
                .end();
        assertAndParse("SELECT a FROM A a"
                + " WHERE (upper(a.title) = upper({#query.criterias[0].parameter}))",
                builder.build());

        builder = QueryBuilder
                .forEntity(A.class)
                .withCriteria("id", 1L)
                .end();
        assertAndParse("SELECT a FROM A a"
                + " WHERE (a.id = {#query.criterias[0].parameter})",
                builder.build());

        builder = QueryBuilder
                .forEntity(A.class)
                .withCriteria("active", true)
                .end();
        assertAndParse("SELECT a FROM A a"
                + " WHERE (a.active = {#query.criterias[0].parameter})",
                builder.build());

        builder = QueryBuilder
                .forEntity(A.class).withCriteria("title", "A")
                .and().withCriteria("id", 1L)
                .and().withCriteria("active", true)
                .end();
        assertAndParse("SELECT a FROM A a"
                + " WHERE (upper(a.title) = upper({#query.criterias[0].parameter})"
                + " AND a.id = {#query.criterias[1].parameter}"
                + " AND a.active = {#query.criterias[2].parameter})",
                builder.build());
    }

    private <E> void assertAndParse(String expected, Query<E> query) {
        Assert.assertEquals(expected, queryTranslator.buildQuery(query));
        TypedQuery<E> parsed = queryService.parseQuery(query);
        Assert.assertNotNull(parsed);
        queryService.getResultCount(query);
    }

    @Test
    public void testB() {
        List<CriteriaDefinition<?>> criteriaDefinitions = criteriaDefinitionFactory.getCriteriaDefinitions(B.class);
        Assert.assertEquals(6, criteriaDefinitions.size());

        QueryBuilder<B> builder = QueryBuilder
                .forEntity(B.class)
                .withCriteria("title", "B")
                .end();
        assertAndParse("SELECT b FROM B b"
                + " WHERE (upper(b.title) = upper({#query.criterias[0].parameter}))",
                builder.build());

        builder = QueryBuilder
                .forEntity(B.class)
                .withCriteria("a.id", 1L)
                .end();
        assertAndParse("SELECT b FROM B b WHERE b IN ("
                + "SELECT b FROM B b JOIN b.a b_a"
                + " WHERE (b_a.id = {#query.criterias[0].parameter}))",
                builder.build());

        builder = QueryBuilder
                .forEntity(B.class)
                .withCriteria("a.id", 1L)
                .and().withCriteria("a.title", "A")
                .end();
        assertAndParse("SELECT b FROM B b WHERE b IN ("
                + "SELECT b FROM B b JOIN b.a b_a"
                + " WHERE (b_a.id = {#query.criterias[0].parameter}"
                + " AND upper(b_a.title) = upper({#query.criterias[1].parameter})))",
                builder.build());

    }

    @Test
    public void testC() {
        List<CriteriaDefinition<?>> criteriaDefinitions = criteriaDefinitionFactory.getCriteriaDefinitions(C.class);
        Assert.assertEquals(15, criteriaDefinitions.size());
        QueryBuilder<C> builder = QueryBuilder
                .forEntity(C.class)
                .withCriteria("b1.id", 1L)
                .and().withCriteria("b1.a.title", "A")
                .end();
        assertAndParse(
                "SELECT c FROM C c WHERE c IN ("
                        + "SELECT c FROM C c JOIN c.b1 c_b1 JOIN c_b1.a c_b1_a"
                        + " WHERE (c_b1.id = {#query.criterias[0].parameter}"
                        + " AND upper(c_b1_a.title) = upper({#query.criterias[1].parameter})))",
                builder.build());

        builder = QueryBuilder
                .forEntity(C.class)
                .withCriteria("b1.id", 1L)
                .and().withCriteria("b2.a.title", "A")
                .end();
        assertAndParse(
                "SELECT c FROM C c WHERE c IN ("
                        + "SELECT c FROM C c JOIN c.b1 c_b1 JOIN c.b2 c_b2 JOIN c_b2.a c_b2_a"
                        + " WHERE (c_b1.id = {#query.criterias[0].parameter}"
                        + " AND upper(c_b2_a.title) = upper({#query.criterias[1].parameter})))",
                builder.build());

    }

    @Test
    public void testDate() {
        List<CriteriaDefinition<?>> criteriaDefinitions = criteriaDefinitionFactory.getCriteriaDefinitions(Date.class);
        Assert.assertEquals(3, criteriaDefinitions.size());

        CriteriaBuilder<Date, LocalDate, CriteriaDate> date = QueryBuilder
                .forEntity(Date.class)
                .withCriteria("date", LocalDate.now());
        QueryBuilder<Date> builder = date.end();
        assertAndParse(
                "SELECT date FROM Date date"
                        + " WHERE (date.date = {#query.criterias[0].parameter})",
                builder.build());

        builder = date.withOperator(">=").end();
        assertAndParse(
                "SELECT date FROM Date date"
                        + " WHERE (date.date >= {#query.criterias[0].parameter})",
                builder.build());

        builder = date.withOperator("<=").end();
        assertAndParse(
                "SELECT date FROM Date date"
                        + " WHERE (date.date <= {#query.criterias[0].parameter})",
                builder.build());

        date = QueryBuilder
                .forEntity(Date.class)
                .withCriteria("dateTime", LocalDate.now());
        builder = date.end();
        assertAndParse(
                "SELECT date FROM Date date"
                        + " WHERE (date.dateTime BETWEEN {#query.criterias[0].minDateTime} AND {#query.criterias[0].maxDateTime})",
                builder.build());

        builder = date.withOperator(">=").end();
        assertAndParse(
                "SELECT date FROM Date date"
                        + " WHERE (date.dateTime >= {#query.criterias[0].maxDateTime})",
                builder.build());

        builder = date.withOperator("<=").end();
        assertAndParse(
                "SELECT date FROM Date date"
                        + " WHERE (date.dateTime <= {#query.criterias[0].minDateTime})",
                builder.build());

    }

    @Test
    public void testArrayOne() {
        List<CriteriaDefinition<?>> criteriaDefinitions =
                criteriaDefinitionFactory.getCriteriaDefinitions(ArrayOne.class);
        Assert.assertEquals(4, criteriaDefinitions.size());

        QueryBuilder<ArrayOne> builder = QueryBuilder
                .forEntity(ArrayOne.class)
                .withCriteria("manys.id", 1L).end();
        assertAndParse(
                "SELECT arrayOne FROM ArrayOne arrayOne WHERE arrayOne IN ("
                        + "SELECT arrayOne FROM ArrayOne arrayOne"
                        + " JOIN arrayOne.manys arrayOne_manys WHERE"
                        + " (arrayOne_manys.id = {#query.criterias[0].parameter}))",
                builder.build());

    }

    @Test
    public void testCollectionOne() {
        List<CriteriaDefinition<?>> criteriaDefinitions =
                criteriaDefinitionFactory.getCriteriaDefinitions(MapOne.class);
        Assert.assertEquals(4, criteriaDefinitions.size());

        QueryBuilder<CollectionOne> builder = QueryBuilder
                .forEntity(CollectionOne.class)
                .withCriteria("manys.id", 1L).end();
        assertAndParse(
                "SELECT collectionOne FROM CollectionOne collectionOne WHERE collectionOne IN ("
                        + "SELECT collectionOne FROM CollectionOne collectionOne"
                        + " JOIN collectionOne.manys collectionOne_manys WHERE"
                        + " (collectionOne_manys.id = {#query.criterias[0].parameter}))",
                builder.build());

    }

    @Test
    public void testMapOne() {
        List<CriteriaDefinition<?>> criteriaDefinitions =
                criteriaDefinitionFactory.getCriteriaDefinitions(MapOne.class);
        Assert.assertEquals(4, criteriaDefinitions.size());

        QueryBuilder<MapOne> builder = QueryBuilder
                .forEntity(MapOne.class)
                .withCriteria("manys[someStr].id", 1L).end();
        assertAndParse(
                "SELECT mapOne FROM MapOne mapOne WHERE mapOne IN ("
                        + "SELECT mapOne FROM MapOne mapOne"
                        + " JOIN mapOne.manys mapOne_manys ON key(mapOne_manys) = 'someStr'"
                        + " WHERE (mapOne_manys.id = {#query.criterias[0].parameter}))",
                builder.build());

        builder = QueryBuilder
                .forEntity(MapOne.class)
                .withCriteria("manys.id", 1L).end();
        assertAndParse(
                "SELECT mapOne FROM MapOne mapOne WHERE mapOne IN ("
                        + "SELECT mapOne FROM MapOne mapOne"
                        + " JOIN mapOne.manys mapOne_manys"
                        + " WHERE (mapOne_manys.id = {#query.criterias[0].parameter}))",
                builder.build());

    }

    @Test
    public void testLeftRightLeftChilds() {
        List<CriteriaDefinition<?>> criteriaDefinitions =
                criteriaDefinitionFactory.getCriteriaDefinitions(LeftRightNode.class);
        Assert.assertEquals(1, criteriaDefinitions.size());

        QueryBuilder<LeftRightNode> builder = QueryBuilder
                .forEntity(LeftRightNode.class)
                .withCriteria("leftChilds.id", 1L).end();
        assertAndParse(
                "SELECT leftRightNode FROM LeftRightNode leftRightNode WHERE leftRightNode IN ("
                        + "SELECT leftRightNode FROM LeftRightNode leftRightNode"
                        + " JOIN leftRightNode.leftChilds leftRightNode_leftChilds WHERE"
                        + " (leftRightNode_leftChilds.id = {#query.criterias[0].parameter}))",
                builder.build());
    }

    @Test
    public void testLeftRightBothChilds() {
        List<CriteriaDefinition<?>> criteriaDefinitions =
                criteriaDefinitionFactory.getCriteriaDefinitions(LeftRightNode.class);
        Assert.assertEquals(1, criteriaDefinitions.size());

        QueryBuilder<LeftRightNode> builder = QueryBuilder
                .forEntity(LeftRightNode.class)
                .withCriteria("leftChilds.id", 1L).and()
                .withCriteria("leftChilds.rightChilds.id", 1L).end();
        assertAndParse(
                "SELECT leftRightNode FROM LeftRightNode leftRightNode WHERE leftRightNode IN ("
                        + "SELECT leftRightNode FROM LeftRightNode leftRightNode"
                        + " JOIN leftRightNode.leftChilds leftRightNode_leftChilds"
                        + " JOIN leftRightNode_leftChilds.rightChilds leftRightNode_leftChilds_rightChilds"
                        + " WHERE (leftRightNode_leftChilds.id = {#query.criterias[0].parameter}"
                        + " AND leftRightNode_leftChilds_rightChilds.id = {#query.criterias[1].parameter}))",
                builder.build());
    }

    @Test
    public void testComplexQuery() {
        QueryBuilder<C> builder = QueryBuilder.forEntity(C.class)
                .withCriteria("title", "c")
                .and().withCriteria("b1.title", "b").withBracketOpen(true).withOperator("!=")
                .and().withCriteria("b1.a.title", "a").withLinkOperator("OR").withBracketClose(true)
                .end();

        assertAndParse(
                "SELECT c FROM C c WHERE c IN ("
                        + "SELECT c FROM C c"
                        + " JOIN c.b1 c_b1"
                        + " JOIN c_b1.a c_b1_a"
                        + " WHERE (upper(c.title) = upper({#query.criterias[0].parameter})"
                        + " AND ("
                        + "upper(c_b1.title) != upper({#query.criterias[1].parameter})"
                        + " OR upper(c_b1_a.title) = upper({#query.criterias[2].parameter})"
                        + ")"
                        + "))",
                builder.build());
    }

    @Test
    public void testStringQuery() {
        CriteriaBuilder<C, String, CriteriaSimple<String>> stringCriteria = QueryBuilder.forEntity(C.class)
                .withCriteria("title", "c");
        QueryBuilder<C> builder = stringCriteria.end();
        assertAndParse(
                "SELECT c FROM C c"
                        + " WHERE (upper(c.title) = upper({#query.criterias[0].parameter}))",
                builder.build());

        stringCriteria.withOperator("!=");
        assertAndParse(
                "SELECT c FROM C c"
                        + " WHERE (upper(c.title) != upper({#query.criterias[0].parameter}))",
                builder.build());
        
        stringCriteria.withParameter("%c%").withOperator("=");
        assertAndParse(
                "SELECT c FROM C c"
                        + " WHERE (upper(c.title) LIKE upper({#query.criterias[0].parameter}))",
                builder.build());
        
        stringCriteria.withParameter("%c%").withOperator("!=");;
        assertAndParse(
                "SELECT c FROM C c"
                        + " WHERE (upper(c.title) NOT LIKE upper({#query.criterias[0].parameter}))",
                builder.build());
    }
    
    @Autowired
    private QueryTestRunner queryTestRunner;
    
    @Test 
    public void testFilter() {
        queryTestRunner.testFilters(A.class, 3);
        queryTestRunner.testFilters(B.class, 3);
        queryTestRunner.testFilters(C.class, 3);
    }

}

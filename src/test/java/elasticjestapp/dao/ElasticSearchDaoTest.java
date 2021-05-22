package elasticjestapp.dao;

import io.searchbox.annotations.JestId;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.config.HttpClientConfig;
import io.searchbox.core.Bulk;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import io.searchbox.indices.CreateIndex;
import io.searchbox.indices.DeleteIndex;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;

@Slf4j
public class ElasticSearchDaoTest {

    @Test
    public void testClient() {

        var jestClient = jestClient();
        Assertions.assertNotNull(jestClient);
        log.info("jestClient:::" + jestClient);

    }

    @Test
    public void insertIndexDemo() {
        try {
//            JestResult employees = jestClient().execute(new CreateIndex.Builder("employees").build());
            var result = jestClient().execute(new CreateIndex.Builder("twitter").build());

            log.info("index is created::" + result);
        } catch (IOException e) {
            log.warn("error occurred " + e);
        }
    }

    @Test
    public void deleteIndexDemo() {
        try {
            var employees = jestClient().execute(new DeleteIndex.Builder("employees").build());
            log.info("emp index is deleted::" + employees);
        } catch (IOException e) {
            log.warn("error occurred " + e);
        }
    }

    @Test
    public void testInsertMap() throws IOException {
        var source = new LinkedHashMap<String, String>();
        source.put("user", "sudar");
        source.put("name", "kasi");
        var index = new Index.Builder(source).index("twitter").type("tweet").build();
        var execute = jestClient().execute(index);
        log.info("result document::" + execute);
    }

    @Test
    public void testInsertJavaObject() {

        var employee = createEmployee("Michael Pratt", "Java Developer", 2,
            List.of("java", "spring", "elasticsearch"));

        try {
            var employees = jestClient().execute(new Index.Builder(employee)
                .index("twitter")
                .type("tweet")
                .build()
            );
            log.info("result of emp insert::" + employees);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testInsertBulkJavaObject() {

        var employee1 = createEmployee("Sudar Kasi1", "Java Developer", 12,
            List.of("java 14", "spring", "elasticsearch"));
        var employee2 = createEmployee("Divy Ka1", "Doctor", 2,
            List.of("Medical", "Nuro"));
        var employee3 = createEmployee("Bhavya Kasi1", "Java Developer", 2,
            List.of("Multithreading"));
        var employee4 = createEmployee("Dave Batt2", "Java Developer", 15,
            List.of("java 11", "FI", "ECS"));
        List<Employee> emps = List.of(employee1, employee2, employee3, employee4);
        Bulk.Builder builder = new Bulk.Builder();
        for (Employee employee : emps) {
            builder.addAction(new Index.Builder(employee)
                .index("twitter")
                .type("tweet")
                .build());
        }

        try {
            var employees = jestClient().execute(builder.build());
            log.info("result of emp bulk insert::" + employees.getJsonString());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Employee createEmployee(String name, String title, int years, List<String> skills) {
        return new Employee.EmployeeBuilder()
            .empId(new Random().nextInt())
            .name(name)
            .title(title)
            .yearsOfService(years)
            .skills(skills)
            .build();
    }

    @Test
    public void testSearch() throws IOException {

        var searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchQuery("title", "Java Developer"));

        var search = new Search.Builder(searchSourceBuilder.toString())
            // multiple index or types can be added.
            .addIndex("twitter")
            .addType("tweet")
            .build();

        var result = jestClient().execute(search);
        var hits = result.getHits(Employee.class);
        for (SearchResult.Hit hit : hits) {
            Employee source = (Employee) hit.source;
            log.info("result Employee {}", source);
        }
        log.info("Search result:::" + result.getJsonString());
    }


    public JestClient jestClient() {
        var factory = new JestClientFactory();
        factory.setHttpClientConfig(
            new HttpClientConfig.Builder("http://localhost:9200")
                .multiThreaded(true)
                .defaultMaxTotalConnectionPerRoute(2)
                .maxTotalConnection(10)
                .build());
        return factory.getObject();
    }

    @Data
    @ToString
    @Builder
    private static class Employee {
        @JestId
        long empId;
        String name;
        String title;
        List<String> skills;
        int yearsOfService;
    }
}
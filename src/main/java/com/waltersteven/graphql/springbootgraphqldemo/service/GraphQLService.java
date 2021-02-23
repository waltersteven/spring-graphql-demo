package com.waltersteven.graphql.springbootgraphqldemo.service;

import com.waltersteven.graphql.springbootgraphqldemo.model.Book;
import com.waltersteven.graphql.springbootgraphqldemo.repository.BookRepository;
import com.waltersteven.graphql.springbootgraphqldemo.service.datafetchers.AllBooksDataFetcher;
import com.waltersteven.graphql.springbootgraphqldemo.service.datafetchers.BookDataFetcher;
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Service
public class GraphQLService {
    @Autowired
    BookRepository bookRepository;

    @Value("classpath:books.graphql")
    Resource resource;

    private GraphQL graphQL;
    @Autowired
    private AllBooksDataFetcher allBooksDataFetcher;
    @Autowired
    private BookDataFetcher bookDataFetcher;

    // Load schema at application start up
    @PostConstruct
    private void loadSchema() throws IOException {
        // Load books into repository.
        loadDataIntoHSQL();

        File schemaFile = resource.getFile();

        // Parse schema
        TypeDefinitionRegistry typeRegistry = new SchemaParser().parse(schemaFile);
        RuntimeWiring wiring = buildRuntimeWiring();
        GraphQLSchema schema = new SchemaGenerator().makeExecutableSchema(typeRegistry, wiring);
        graphQL = GraphQL.newGraphQL(schema).build();
    }

    private void loadDataIntoHSQL() {
        Stream.of(
            new Book("123",
                    "Book of Clouds",
                    "Kindle Edition",
                    new ArrayList<>(
                            List.of("Archie", "Robert")
                    ),
                    "Nov 2017"),
            new Book("456",
                    "Cloud Architecture",
                    "Orielly",
                    new ArrayList<>(
                            List.of("Peter", "Sam")
                    ),
                    "Jan 2015"),
            new Book("789",
                    "Java 9 Programming",
                    "Orielly",
                    new ArrayList<>(
                            List.of("Venkat", "Eddy")
                    ),
                    "Dec 2016")
        ).forEach(book -> bookRepository.save(book));
    }

    private RuntimeWiring buildRuntimeWiring() {
        return RuntimeWiring.newRuntimeWiring()
                .type("Query", typeWiring ->
                    typeWiring.dataFetcher("allBooks", allBooksDataFetcher)
                            .dataFetcher("book", bookDataFetcher))
                .build();
    }

    public GraphQL getGraphQL() {
        return graphQL;
    }
}

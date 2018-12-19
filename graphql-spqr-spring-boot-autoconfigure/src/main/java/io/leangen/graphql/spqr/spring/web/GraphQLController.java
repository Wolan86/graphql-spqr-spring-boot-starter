package io.leangen.graphql.spqr.spring.web;

import graphql.ExecutionInput;
import graphql.GraphQL;
import io.leangen.graphql.spqr.spring.autoconfigure.DataLoaderRegistryFactory;
import io.leangen.graphql.spqr.spring.autoconfigure.GlobalContextFactory;
import io.leangen.graphql.spqr.spring.autoconfigure.GlobalContextFactoryParams;
import io.leangen.graphql.spqr.spring.web.dto.GraphQLRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@CrossOrigin
public class GraphQLController extends AbstractController<HttpServletRequest>{

    protected final GlobalContextFactory contextFactory;
    protected final DataLoaderRegistryFactory dataLoaderRegistryFactory;

    @Autowired
    public GraphQLController(GraphQL graphQL, GlobalContextFactory contextFactory, DataLoaderRegistryFactory dataLoaderRegistryFactory) {
        super(graphQL);
        this.contextFactory = contextFactory;
        this.dataLoaderRegistryFactory = dataLoaderRegistryFactory;
    }

    protected ExecutionInput input(GraphQLRequest request, HttpServletRequest raw) {
        ExecutionInput.Builder inputBuilder = ExecutionInput.newExecutionInput()
                .query(request.getQuery())
                .operationName(request.getOperationName())
                .variables(request.getVariables())
                .context(contextFactory.createGlobalContext(GlobalContextFactoryParams.builder()
                        .withGraphQLRequest(request)
                        .withHttpRequest(raw)
                        .build()));
        if (dataLoaderRegistryFactory != null) {
            inputBuilder.dataLoaderRegistry(dataLoaderRegistryFactory.createDataLoaderRegistry());
        }
        return inputBuilder.build();
    }
}

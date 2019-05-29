package br.com.quotator.integration.product;

import br.com.quotator.api.v1.dto.ProductOutputDto;
import br.com.quotator.model.Product;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProductListTest {

    @Autowired
    private MongoOperations mongoOperations;

    @Autowired
    private WebTestClient webClient;

    @Before
    public void setUp() {
        mongoOperations.dropCollection(Product.class);
        mongoOperations.insert(new Product(null, "Caderno", null));
        mongoOperations.insert(new Product(null, "Caneta", null));
    }

    @Test
    public void shouldListAllProducts() {
        EntityExchangeResult<ProductOutputDto[]> result = webClient
                .get().uri("/v1/products")
                .exchange()
                .expectStatus().isOk()
                .expectBody(ProductOutputDto[].class)
                .returnResult();

        List<String> productNames = Arrays.asList("Caderno", "Caneta");

        boolean resultValidation = isExpectedResult(result, productNames);

        Assert.assertTrue(resultValidation);
    }

    @Test
    public void shouldListAllProductsFilteringByName() {
        EntityExchangeResult<ProductOutputDto[]> result = webClient
                .get().uri("/v1/products?name=can")
                .exchange()
                .expectStatus().isOk()
                .expectBody(ProductOutputDto[].class)
                .returnResult();

        List<String> productNames = Collections.singletonList("Caneta");

        boolean resultValidation = isExpectedResult(result, productNames);

        Assert.assertTrue(resultValidation);
    }

    private boolean isExpectedResult(EntityExchangeResult<ProductOutputDto[]> result, List<String> productNames) {
        return Arrays.stream(Objects.requireNonNull(result.getResponseBody()))
                .allMatch(productOutputDto -> productNames.contains(productOutputDto.getName()));
    }
}

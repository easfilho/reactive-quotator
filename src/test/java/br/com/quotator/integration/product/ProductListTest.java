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
        mongoOperations.insert(new Product(null, "Caderno"));
        mongoOperations.insert(new Product(null, "Caneta"));
    }

    @Test
    public void shouldListAllImages() {
        EntityExchangeResult<ProductOutputDto[]> result = webClient
                .get().uri("/v1/products")
                .exchange()
                .expectStatus().isOk()
                .expectBody(ProductOutputDto[].class)
                .returnResult();

        boolean resultaValidation = Arrays.stream(Objects.requireNonNull(result.getResponseBody()))
                .allMatch(productOutputDto -> productOutputDto.getName().equals("Caderno") || productOutputDto.getName().equals("Caneta"));

        Assert.assertTrue(resultaValidation);
    }
}

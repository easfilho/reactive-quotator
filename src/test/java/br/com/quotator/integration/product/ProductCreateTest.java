package br.com.quotator.integration.product;

import br.com.quotator.api.v1.dto.ProductInputDto;
import br.com.quotator.api.v1.dto.ProductOutputDto;
import br.com.quotator.model.Product;
import br.com.quotator.repository.ProductRepository;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import java.util.Objects;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProductCreateTest {

    @Autowired
    private MongoOperations operations;

    @Autowired
    private WebTestClient webClient;

    @Before
    public void setUp() {
        operations.dropCollection(Product.class);
    }

    @Test
    public void shouldCreateProduct() {
        ProductInputDto productInputDto = new ProductInputDto("Notebook");

        ProductOutputDto result = webClient
                .post()
                .uri("/v1/products")
                .body(BodyInserters.fromObject(productInputDto))
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(ProductOutputDto.class)
                .returnResult()
                .getResponseBody();

        Assert.assertEquals("Notebook", Objects.requireNonNull(result).getName());
        Assert.assertTrue(StringUtils.isNotBlank(result.getId()));

        Product createdProduct = operations.findById(result.getId(), Product.class);
        Assert.assertEquals(result.getId(), createdProduct.getId());
        Assert.assertEquals(result.getName(), createdProduct.getName());
    }
}
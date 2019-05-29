package br.com.quotator.integration.product;

import br.com.quotator.api.v1.dto.ProductInputDto;
import br.com.quotator.api.v1.dto.ProductOutputDto;
import br.com.quotator.model.Product;
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
public class ProductUpdateTest {

    @Autowired
    private MongoOperations mongoOperations;

    @Autowired
    private WebTestClient webClient;

    @Before
    public void setUp() {
        mongoOperations.dropCollection(Product.class);
    }

    @Test
    public void shouldCreateProduct() {
        Product product = mongoOperations.insert(new Product(null, "Notebuque", null));
        ProductInputDto productInputDto = new ProductInputDto("Notebook", null);

        ProductOutputDto result = webClient
                .put()
                .uri("/v1/products/{id}", product.getId())
                .body(BodyInserters.fromObject(productInputDto))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(ProductOutputDto.class)
                .returnResult()
                .getResponseBody();

        Assert.assertEquals("Notebook", Objects.requireNonNull(result).getName());
        Assert.assertTrue(StringUtils.isNotBlank(result.getId()));

        Product createdProduct = mongoOperations.findById(result.getId(), Product.class);
        Assert.assertEquals(result.getId(), Objects.requireNonNull(createdProduct).getId());
        Assert.assertEquals(result.getName(), createdProduct.getName());
    }
}
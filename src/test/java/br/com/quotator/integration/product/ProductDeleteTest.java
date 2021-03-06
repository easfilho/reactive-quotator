package br.com.quotator.integration.product;

import br.com.quotator.model.Product;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProductDeleteTest {

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
        Product product = mongoOperations.insert(new Product(null, "Televisão", null));
        webClient
                .delete()
                .uri("/v1/products/{id}", product.getId())
                .exchange()
                .expectStatus()
                .isOk();
        Assert.assertNull(mongoOperations.findById(product.getId(), Product.class));
    }
}

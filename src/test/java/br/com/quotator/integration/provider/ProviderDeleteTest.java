package br.com.quotator.integration.provider;

import br.com.quotator.model.Provider;
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
public class ProviderDeleteTest {

    @Autowired
    private MongoOperations mongoOperations;

    @Autowired
    private WebTestClient webClient;

    @Before
    public void setUp() {
        mongoOperations.dropCollection(Provider.class);
    }

    @Test
    public void shouldCreateProduct() {
        Provider provider = mongoOperations.insert(new Provider(null, "Fornecedor X", "(51) 9999-9999", "email@email.com.uy"));
        webClient
                .delete()
                .uri("/v1/providers/{id}", provider.getId())
                .exchange()
                .expectStatus()
                .isOk();
        Assert.assertNull(mongoOperations.findById(provider.getId(), Provider.class));
    }
}

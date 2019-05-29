package br.com.quotator.integration.product;

import br.com.quotator.api.v1.dto.ProductInputDto;
import br.com.quotator.api.v1.dto.ProductOutputDto;
import br.com.quotator.api.v1.dto.ProviderInputDto;
import br.com.quotator.model.Product;
import br.com.quotator.model.Provider;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProductCreateTest {

    @Autowired
    private MongoOperations mongoOperations;

    @Autowired
    private WebTestClient webClient;

    @Before
    public void setUp() {
        mongoOperations.dropCollection(Product.class);
        mongoOperations.dropCollection(Provider.class);
    }

    @Test
    public void shouldCreateProductWithoutProviders() {
        ProductInputDto productInputDto = new ProductInputDto("Notebook", null);

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

        Product createdProduct = mongoOperations.findById(result.getId(), Product.class);
        Assert.assertEquals(result.getId(), Objects.requireNonNull(createdProduct).getId());
        Assert.assertEquals(result.getName(), createdProduct.getName());
    }


    @Test
    public void shouldCreateProductWithProviders() {
        List<Provider> providers = new ArrayList<Provider>() {{
            add(mongoOperations.insert(new Provider(null, "Gerdau", "(51) 9999-9999", "contato@gerdau.com")));
            add(mongoOperations.insert(new Provider(null, "DELL", "(51) 8888-8888", "contato@dell.com")));
        }};

        List<ProviderInputDto> providerInputDtos = new ArrayList<>();
        providers.forEach(provider ->
                providerInputDtos.add(new ProviderInputDto(provider.getId(), provider.getName(), provider.getPhone(), provider.getEmail()))
        );

        ProductInputDto productInputDto = new ProductInputDto("Notebook", providerInputDtos);

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

        Product createdProduct = mongoOperations.findById(result.getId(), Product.class);
        Assert.assertEquals(result.getId(), Objects.requireNonNull(createdProduct).getId());
        Assert.assertEquals(result.getName(), createdProduct.getName());
        result.getProviders().forEach(providerOutputDto -> {
            Assert.assertTrue(providers.stream().anyMatch(providerInputDto -> providerInputDto.getId().equals(providerOutputDto.getId())));
            Assert.assertTrue(providers.stream().anyMatch(providerInputDto -> providerInputDto.getName().equals(providerOutputDto.getName())));
            Assert.assertTrue(providers.stream().anyMatch(providerInputDto -> providerInputDto.getPhone().equals(providerOutputDto.getPhone())));
            Assert.assertTrue(providers.stream().anyMatch(providerInputDto -> providerInputDto.getEmail().equals(providerOutputDto.getEmail())));
        });
        Assert.assertEquals(result.getProviders().size(), providerInputDtos.size());
    }
}
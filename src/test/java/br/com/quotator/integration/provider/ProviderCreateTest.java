package br.com.quotator.integration.provider;

import br.com.quotator.api.v1.dto.ProviderInputDto;
import br.com.quotator.api.v1.dto.ProviderOutputDto;
import br.com.quotator.integration.DefaultError;
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

import java.util.Objects;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProviderCreateTest {

    @Autowired
    private MongoOperations mongoOperations;

    @Autowired
    private WebTestClient webClient;

    @Before
    public void setUp() {
        mongoOperations.dropCollection(Provider.class);
    }

    @Test
    public void shouldCreateProvider() {
        ProviderInputDto providerInputDto = new ProviderInputDto(null, "DELL", "(51) 1234-1234", "email@email.com");

        ProviderOutputDto result = webClient
                .post()
                .uri("/v1/providers")
                .body(BodyInserters.fromObject(providerInputDto))
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(ProviderOutputDto.class)
                .returnResult()
                .getResponseBody();

        Assert.assertEquals("DELL", Objects.requireNonNull(result).getName());
        Assert.assertEquals("(51) 1234-1234", Objects.requireNonNull(result).getPhone());
        Assert.assertEquals("email@email.com", Objects.requireNonNull(result).getEmail());
        Assert.assertTrue(StringUtils.isNotBlank(result.getId()));

        Provider createdProvider = mongoOperations.findById(result.getId(), Provider.class);
        Assert.assertNotNull(createdProvider);
        Assert.assertEquals(result.getId(), createdProvider.getId());
        Assert.assertEquals(result.getName(), createdProvider.getName());
        Assert.assertEquals(result.getPhone(), createdProvider.getPhone());
        Assert.assertEquals(result.getEmail(), createdProvider.getEmail());
    }

    @Test
    public void shouldValidateRequiredName() {
        ProviderInputDto providerInputDto = new ProviderInputDto(null, null, "123456789", "email@email.com");

        DefaultError defaultError = webClient
                .post()
                .uri("/v1/providers")
                .body(BodyInserters.fromObject(providerInputDto))
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody(DefaultError.class)
                .returnResult()
                .getResponseBody();

        Assert.assertNotNull(defaultError);
        Assert.assertTrue(defaultError.getMessage().contains("Nome é obrigatório"));
        Assert.assertTrue(mongoOperations.findAll(Provider.class).isEmpty());
    }

    @Test
    public void shouldValidateRequiredPhone() {
        ProviderInputDto providerInputDto = new ProviderInputDto(null, "Nacional", null, "email@email.com");

        DefaultError defaultError = webClient
                .post()
                .uri("/v1/providers")
                .body(BodyInserters.fromObject(providerInputDto))
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody(DefaultError.class)
                .returnResult()
                .getResponseBody();

        Assert.assertNotNull(defaultError);
        Assert.assertTrue(defaultError.getMessage().contains("Telefone é obrigatório"));
        Assert.assertTrue(mongoOperations.findAll(Provider.class).isEmpty());
    }

    @Test
    public void shouldValidateInvalidPhone() {
        ProviderInputDto providerInputDto = new ProviderInputDto(null, "Nacional", "51 1234 1234", "email@email.com");

        DefaultError defaultError = webClient
                .post()
                .uri("/v1/providers")
                .body(BodyInserters.fromObject(providerInputDto))
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody(DefaultError.class)
                .returnResult()
                .getResponseBody();

        Assert.assertNotNull(defaultError);
        Assert.assertTrue(defaultError.getMessage().contains("Telefone com formato inválido"));
        Assert.assertTrue(mongoOperations.findAll(Provider.class).isEmpty());
    }

    @Test
    public void shouldValidateRequiredEmail() {
        ProviderInputDto providerInputDto = new ProviderInputDto(null, "Nacional", "(51) 1234-1234", null);

        DefaultError defaultError = webClient
                .post()
                .uri("/v1/providers")
                .body(BodyInserters.fromObject(providerInputDto))
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody(DefaultError.class)
                .returnResult()
                .getResponseBody();

        Assert.assertNotNull(defaultError);
        Assert.assertTrue(defaultError.getMessage().contains("E-mail é obrigatório"));
        Assert.assertTrue(mongoOperations.findAll(Provider.class).isEmpty());
    }

    @Test
    public void shouldValidateInvalidEmail() {
        ProviderInputDto providerInputDto = new ProviderInputDto(null, "Nacional", "(51) 1234-1234", "email");

        DefaultError defaultError = webClient
                .post()
                .uri("/v1/providers")
                .body(BodyInserters.fromObject(providerInputDto))
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody(DefaultError.class)
                .returnResult()
                .getResponseBody();

        Assert.assertNotNull(defaultError);
        Assert.assertTrue(defaultError.getMessage().contains("E-mail inválido"));
        Assert.assertTrue(mongoOperations.findAll(Provider.class).isEmpty());
    }
}
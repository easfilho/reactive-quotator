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
public class ProviderUpdateTest {

    @Autowired
    private MongoOperations mongoOperations;

    @Autowired
    private WebTestClient webClient;

    @Before
    public void setUp() {
        mongoOperations.dropCollection(Provider.class);
    }

    @Test
    public void shouldUpdateProvider() {
        Provider provider = mongoOperations.insert(new Provider("null", "Havaianas", "(51) 1234-1234", "email@email.com"));
        ProviderInputDto providerInputDto = new ProviderInputDto("Havaianas Atualizado", "(51) 4321-4321", "havaianas@bol.com.br");

        ProviderOutputDto result = webClient
                .put()
                .uri("/v1/providers/{id}", provider.getId())
                .body(BodyInserters.fromObject(providerInputDto))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(ProviderOutputDto.class)
                .returnResult()
                .getResponseBody();

        Assert.assertEquals("Havaianas Atualizado", Objects.requireNonNull(result).getName());
        Assert.assertEquals("(51) 4321-4321", Objects.requireNonNull(result).getPhone());
        Assert.assertEquals("havaianas@bol.com.br", Objects.requireNonNull(result).getEmail());
        Assert.assertTrue(StringUtils.isNotBlank(result.getId()));

        Provider updatedProvider = mongoOperations.findById(result.getId(), Provider.class);
        Assert.assertNotNull(updatedProvider);
        Assert.assertEquals(result.getId(), updatedProvider.getId());
        Assert.assertEquals(result.getName(), updatedProvider.getName());
        Assert.assertEquals(result.getPhone(), updatedProvider.getPhone());
        Assert.assertEquals(result.getEmail(), updatedProvider.getEmail());
    }

    @Test
    public void shouldValidateRequiredName() {
        Provider provider = mongoOperations.insert(new Provider("null", "Havaianas", "(51) 1234-1234", "email@email.com"));
        ProviderInputDto providerInputDto = new ProviderInputDto(null, "(51) 4321-4321", "havaianas@bol.com.br");

        DefaultError defaultError = webClient
                .put()
                .uri("/v1/providers/{id}", provider.getId())
                .body(BodyInserters.fromObject(providerInputDto))
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody(DefaultError.class)
                .returnResult()
                .getResponseBody();

        Assert.assertTrue(Objects.requireNonNull(defaultError).getMessage().contains("Nome é obrigatório"));
        validateProviderNotUpdated(provider);
    }

    @Test
    public void shouldValidateRequiredPhone() {
        Provider provider = mongoOperations.insert(new Provider("null", "Havaianas", "(51) 1234-1234", "email@email.com"));
        ProviderInputDto providerInputDto = new ProviderInputDto("Havainas as legítimas", null, "havaianas@bol.com.br");

        DefaultError defaultError = webClient
                .put()
                .uri("/v1/providers/{id}", provider.getId())
                .body(BodyInserters.fromObject(providerInputDto))
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody(DefaultError.class)
                .returnResult()
                .getResponseBody();

        Assert.assertTrue(Objects.requireNonNull(defaultError).getMessage().contains("Telefone é obrigatório"));
        validateProviderNotUpdated(provider);
    }

    @Test
    public void shouldValidateInvalidPhone() {
        Provider provider = mongoOperations.insert(new Provider("null", "Havaianas", "(51) 1234-1234", "email@email.com"));
        ProviderInputDto providerInputDto = new ProviderInputDto("Havainas as legítimas", "51 12341234", "havaianas@bol.com.br");

        DefaultError defaultError = webClient
                .put()
                .uri("/v1/providers/{id}", provider.getId())
                .body(BodyInserters.fromObject(providerInputDto))
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody(DefaultError.class)
                .returnResult()
                .getResponseBody();

        Assert.assertTrue(Objects.requireNonNull(defaultError).getMessage().contains("Telefone com formato inválido"));
        validateProviderNotUpdated(provider);
    }

    @Test
    public void shouldValidateRequiredEmail() {
        Provider provider = mongoOperations.insert(new Provider("null", "Havaianas", "(51) 1234-1234", "email@email.com"));
        ProviderInputDto providerInputDto = new ProviderInputDto("Havainas as legítimas", "(51) 1234-1234", null);

        DefaultError defaultError = webClient
                .put()
                .uri("/v1/providers/{id}", provider.getId())
                .body(BodyInserters.fromObject(providerInputDto))
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody(DefaultError.class)
                .returnResult()
                .getResponseBody();

        Assert.assertTrue(Objects.requireNonNull(defaultError).getMessage().contains("E-mail é obrigatório"));
        validateProviderNotUpdated(provider);
    }

    @Test
    public void shouldValidateInvalidEmail() {
        Provider provider = mongoOperations.insert(new Provider("null", "Havaianas", "(51) 1234-1234", "email@email.com"));
        ProviderInputDto providerInputDto = new ProviderInputDto("Havainas as legítimas", "(51) 1234-1234", "email");

        DefaultError defaultError = webClient
                .put()
                .uri("/v1/providers/{id}", provider.getId())
                .body(BodyInserters.fromObject(providerInputDto))
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody(DefaultError.class)
                .returnResult()
                .getResponseBody();

        Assert.assertTrue(Objects.requireNonNull(defaultError).getMessage().contains("E-mail inválido"));
        validateProviderNotUpdated(provider);
    }

    private void validateProviderNotUpdated(Provider provider) {
        Provider savedProvider = mongoOperations.findById(provider.getId(), Provider.class);
        Assert.assertNotNull(savedProvider);
        Assert.assertEquals(provider.getId(), savedProvider.getId());
        Assert.assertEquals(provider.getName(), savedProvider.getName());
        Assert.assertEquals(provider.getPhone(), savedProvider.getPhone());
        Assert.assertEquals(provider.getEmail(), savedProvider.getEmail());
    }
}
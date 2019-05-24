package br.com.quotator.integration.provider;

import br.com.quotator.api.v1.dto.ProviderOutputDto;
import br.com.quotator.model.Provider;
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
public class ProviderListTest {

    @Autowired
    private MongoOperations mongoOperations;

    @Autowired
    private WebTestClient webClient;

    @Before
    public void setUp() {
        mongoOperations.dropCollection(Provider.class);
        mongoOperations.insert(new Provider(null, "Tramontina", "(51) 1234 1234", "tramontina@email.com"));
        mongoOperations.insert(new Provider(null, "Gerdau", "(51) 4321 4321", "gerdau@email.com"));
        mongoOperations.insert(new Provider(null, "Havaianas", "(51) 5555 5555", "havaianas@email.com"));
    }

    @Test
    public void shouldListAllProviders() {
        EntityExchangeResult<ProviderOutputDto[]> result = webClient
                .get().uri("/v1/providers")
                .exchange()
                .expectStatus().isOk()
                .expectBody(ProviderOutputDto[].class)
                .returnResult();

        List<String> providerNames = Arrays.asList("Tramontina", "Gerdau", "Havaianas");
        List<String> providerPhones = Arrays.asList("(51) 1234 1234", "(51) 4321 4321", "(51) 5555 5555");
        List<String> providerEmails = Arrays.asList("tramontina@email.com", "gerdau@email.com", "havaianas@email.com");

        boolean resultValidation = isResultdExpected(result, providerNames, providerPhones, providerEmails);

        Assert.assertTrue(resultValidation);
    }

    @Test
    public void shouldListAllProvidersFilteringByName() {
        EntityExchangeResult<ProviderOutputDto[]> result = webClient
                .get().uri("/v1/providers?name=tramon")
                .exchange()
                .expectStatus().isOk()
                .expectBody(ProviderOutputDto[].class)
                .returnResult();

        List<String> providerNames = Collections.singletonList("Tramontina");
        List<String> providerPhones = Collections.singletonList("(51) 1234 1234");
        List<String> providerEmails = Collections.singletonList("tramontina@email.com");

        boolean resultValidation = isResultdExpected(result, providerNames, providerPhones, providerEmails);

        Assert.assertTrue(resultValidation);
    }

    private boolean isResultdExpected(EntityExchangeResult<ProviderOutputDto[]> result, List<String> providerNames, List<String> providerPhones, List<String> providerEmails) {
        return Arrays.stream(Objects.requireNonNull(result.getResponseBody()))
                .allMatch(providerOutputDto ->
                        providerNames.contains(providerOutputDto.getName()) &&
                        providerPhones.contains(providerOutputDto.getPhone()) &&
                        providerEmails.contains(providerOutputDto.getEmail()));
    }
}
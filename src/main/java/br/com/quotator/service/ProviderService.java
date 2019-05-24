package br.com.quotator.service;

import br.com.quotator.model.Provider;
import br.com.quotator.repository.ProviderRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ProviderService {

    private ProviderRepository providerRepository;

    public ProviderService(ProviderRepository providerRepository) {
        this.providerRepository = providerRepository;
    }

    public Flux<Provider> getProducts(String name) {
        return providerRepository.findByNameContainingIgnoringCase(name);
    }

    public Mono<Provider> save(Provider provider) {
        return providerRepository.save(provider);
    }

    public Mono<Void> delete(String id) {
        return providerRepository.deleteById(id);
    }
}

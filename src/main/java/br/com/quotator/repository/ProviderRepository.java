package br.com.quotator.repository;

import br.com.quotator.model.Provider;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface ProviderRepository extends ReactiveCrudRepository<Provider, String> {

    Flux<Provider> findByNameContainingIgnoringCase(String name);

}

package br.com.quotator.repository;

import br.com.quotator.model.Product;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface ProductRepository extends ReactiveCrudRepository<Product, String> {

    Flux<Product> findByNameContainingIgnoringCase(String name);
}

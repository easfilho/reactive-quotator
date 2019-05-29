package br.com.quotator.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Component;

@Component
public class InitDatabase {

    @Bean
    CommandLineRunner init(MongoOperations operations) {
        return args -> {
//            operations.dropCollection(Product.class);
//            operations.insert(new Product(null, "Caderno", null));
//            operations.insert(new Product(null, "Caneta", null));
        };
    }
}

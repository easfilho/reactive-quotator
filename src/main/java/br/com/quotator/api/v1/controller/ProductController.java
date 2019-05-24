package br.com.quotator.api.v1.controller;

import br.com.quotator.api.v1.dto.ProductInputDto;
import br.com.quotator.api.v1.dto.ProductOutputDto;
import br.com.quotator.mapper.ProductMapper;
import br.com.quotator.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RestController
@RequestMapping("/v1/products")
public class ProductController {

    private ProductService productService;
    private ProductMapper productMapper;


    public ProductController(ProductService productService, ProductMapper productMapper) {
        this.productService = productService;
        this.productMapper = productMapper;
    }

    @GetMapping
    public Flux<ProductOutputDto> getProducts() {
        return productService.getProducts()
                .flatMap(product -> Mono.just(productMapper.map(product)));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ProductOutputDto> create(@Valid @RequestBody Mono<ProductInputDto> productDtoMono) {
        return productDtoMono.flatMap(productInputDto -> Mono.just(productMapper.map(productInputDto)))
                .flatMap(productService::save)
                .flatMap(p -> Mono.just(productMapper.map(p)));
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<ProductOutputDto> update(@PathVariable("id") String id, @Valid @RequestBody Mono<ProductInputDto> productInputDtoMono) {
        return productInputDtoMono.flatMap(productInputDto -> Mono.just(productMapper.map(productInputDto)))
                .flatMap(product -> {
                    product.setId(id);
                    return productService.save(product);
                }).flatMap(p -> Mono.just(productMapper.map(p)));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<Void> delete(@PathVariable("id") String id) {
        return productService.delete(id);
    }
}

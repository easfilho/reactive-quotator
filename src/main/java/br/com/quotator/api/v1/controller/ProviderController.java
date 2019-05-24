package br.com.quotator.api.v1.controller;

import br.com.quotator.api.v1.dto.ProviderInputDto;
import br.com.quotator.api.v1.dto.ProviderOutputDto;
import br.com.quotator.mapper.ProviderMapper;
import br.com.quotator.service.ProviderService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RestController
@RequestMapping("/v1/providers")
public class ProviderController {

    private ProviderService providerService;
    private ProviderMapper providerMapper;


    public ProviderController(ProviderService providerService, ProviderMapper providerMapper) {
        this.providerService = providerService;
        this.providerMapper = providerMapper;
    }

    @GetMapping
    public Flux<ProviderOutputDto> list(@RequestParam(value = "name", required = false, defaultValue = "") String name) {
        return providerService.getProducts(name)
                .flatMap(product -> Mono.just(providerMapper.map(product)));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ProviderOutputDto> create(@Valid @RequestBody Mono<ProviderInputDto> providerProductInputDtoMono) {
        return providerProductInputDtoMono.flatMap(providerInputDto -> Mono.just(providerMapper.map(providerInputDto)))
                .flatMap(providerService::save)
                .flatMap(provider -> Mono.just(providerMapper.map(provider)));
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<ProviderOutputDto> update(@PathVariable("id") String id, @Valid @RequestBody Mono<ProviderInputDto> providerInputDtoMono) {
        return providerInputDtoMono.flatMap(providerInputDto -> Mono.just(providerMapper.map(providerInputDto)))
                .flatMap(product -> {
                    product.setId(id);
                    return providerService.save(product);
                }).flatMap(provider -> Mono.just(providerMapper.map(provider)));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<Void> delete(@PathVariable("id") String id) {
        return providerService.delete(id);
    }
}

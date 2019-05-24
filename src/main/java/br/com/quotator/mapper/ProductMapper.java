package br.com.quotator.mapper;

import br.com.quotator.api.v1.dto.ProductInputDto;
import br.com.quotator.api.v1.dto.ProductOutputDto;
import br.com.quotator.model.Product;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public ProductOutputDto map(Product product) {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(product, ProductOutputDto.class);
    }

    public Product map(ProductInputDto productInputDto) {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(productInputDto, Product.class);
    }
}

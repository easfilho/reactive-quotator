package br.com.quotator.mapper;

import br.com.quotator.api.v1.dto.ProviderInputDto;
import br.com.quotator.api.v1.dto.ProviderOutputDto;
import br.com.quotator.model.Provider;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class ProviderMapper {

    public ProviderOutputDto map(Provider provider) {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(provider, ProviderOutputDto.class);
    }

    public Provider map(ProviderInputDto providerInputDto) {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(providerInputDto, Provider.class);
    }
}

package br.com.quotator.api.v1.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductInputDto {

    private String name;
    private List<ProviderInputDto> providers;

}

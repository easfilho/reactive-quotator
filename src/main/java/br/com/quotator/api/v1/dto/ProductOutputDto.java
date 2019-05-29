package br.com.quotator.api.v1.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ProductOutputDto {

    private String id;
    private String name;
    private List<ProviderOutputDto> providers;

}

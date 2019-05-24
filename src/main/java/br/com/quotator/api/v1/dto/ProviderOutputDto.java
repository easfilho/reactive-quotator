package br.com.quotator.api.v1.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProviderOutputDto {

    private String id;
    private String name;
    private String phone;
    private String email;

}

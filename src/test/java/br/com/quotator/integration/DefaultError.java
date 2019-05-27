package br.com.quotator.integration;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DefaultError {

    private String message;
    private String status;
    private String path;
    private String error;
    private String timestamp;

}

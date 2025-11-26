package tntra.io.pss_client.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.ResponseEntity;

public interface ResponseValidation {
    public void  validateResponse(String response) throws JsonProcessingException;
}

package tntra.io.pss_client.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.ResponseEntity;
import tntra.io.pss_client.dto.ResponseDTO;

public interface ResponseValidation {
    public ResponseDTO validateResponse(String response);
}

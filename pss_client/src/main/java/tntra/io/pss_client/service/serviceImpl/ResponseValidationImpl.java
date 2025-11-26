package tntra.io.pss_client.service.serviceImpl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import tntra.io.pss_client.controller.ClientController;
import tntra.io.pss_client.service.ResponseValidation;

@Slf4j
@Service
public class ResponseValidationImpl implements ResponseValidation {

    @Override
    public void validateResponse(String response) throws JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();

        JsonNode jsonNode = objectMapper.readTree(response);

        String responseCode = jsonNode.path("responseCode").asText();

        if("00".equals(responseCode)){
            log.info("Transaction successful with responseCode: {}", responseCode);
        } else{
            log.warn("Transaction failed with responseCode: {}", responseCode);
            throw new RuntimeException("Transaction failed with responseCode: " + responseCode);
        }
    }
}

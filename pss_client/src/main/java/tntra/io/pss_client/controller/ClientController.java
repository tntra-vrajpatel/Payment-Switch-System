package tntra.io.pss_client.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tntra.io.pss_client.service.ClientService;

@Slf4j
@RestController
@RequestMapping("/message")
@Tag(name = "Payment Client", description = "APIs for sending transaction requests to TCP server")
public class ClientController {

    @Autowired
    private ClientService clientService;

    @PostMapping("/send")
    @Operation(summary = "Send Transaction Message", description = "Sends a JSON transaction request over TCP to the Payment Switch Server.")
    public ResponseEntity<String> sendMessage(@RequestBody String jsonRequest) {

        try {
            log.info("Received request from client:  {} ",jsonRequest);

            String response = clientService.sendRequest(jsonRequest);
            log.info("Received response from server: {} ",response);

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(response);

            String responseCode = jsonNode.path("responseCode").asText();

            if ("00".equals(responseCode)) {
                log.info("Transaction successful with responseCode: {} ",responseCode);
                return ResponseEntity.ok(response);
            } else {
                log.info("Transaction failed with responseCode: {} ",responseCode);
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            log.error("Error while sending message: {} ",e.getMessage(),e);
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
}
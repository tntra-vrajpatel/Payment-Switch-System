package tntra.io.pss_client.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tntra.io.pss_client.dto.ResponseDTO;
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
    public ResponseEntity<ResponseDTO> sendMessage(@RequestBody String jsonRequest)  {

        log.info("Received request from client:  {} ",jsonRequest);

        ResponseDTO response = clientService.sendRequest(jsonRequest);
        log.info("Received response from server: {} ",response);

        return ResponseEntity.ok(response);
    }
}
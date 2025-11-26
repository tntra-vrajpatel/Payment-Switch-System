package tntra.io.pss_client.service.serviceImpl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import tntra.io.pss_client.config.TcpClientConfig;
import tntra.io.pss_client.service.ClientService;
import tntra.io.pss_client.service.ResponseValidation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

@Slf4j
@Service
public class ClientServiceImpl implements ClientService {

    @Autowired
    private TcpClientConfig clientConfig;

    @Autowired
    private ResponseValidation responseValidation;

    @Retryable(
            value = { java.io.IOException.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000)
    )

    @Override
    public String sendRequest(String json) throws Exception {

        String host = clientConfig.getHost();
        int port = clientConfig.getPort();
        String response;

        log.info("Connecting to TCP server at {}:{}", host, port);

        // JSON Validation
        if (json == null || json.trim().isEmpty()) {
            log.error("Validation failed: Empty request body");
            throw new IllegalArgumentException("Request JSON cannot be empty");
        }
        try {
            new com.fasterxml.jackson.databind.ObjectMapper().readTree(json);
        } catch (Exception e) {
            log.error("Validation failed: Invalid JSON format -> {}", json);
            throw new IllegalArgumentException("Invalid JSON format. Please check input body.");
        }

        // Socket Creation
        try (Socket socket = new Socket(host, port)) {
            socket.setSoTimeout(10000);
            log.info("TCP connection established successfully");

            // Send Request
            OutputStream outputStream = socket.getOutputStream();
            outputStream.write((json.trim() + "\r\n").getBytes(StandardCharsets.UTF_8)); // converting String to bytes
            outputStream.flush();
            log.info("Request sent to server: {} ",json);

            // Read Response
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8))) {
                response = bufferedReader.readLine();
                log.info("Received response from server: {} ",response);

                // to check response code
                responseValidation.validateResponse(response);

                return response != null ? response : ""; // send to clientController
            }
        }
        catch (Exception e){
            log.error("TCP communication error: {}\", e.getMessage(), e");
            throw e;
        }
    }
    @Recover
    public String recover(IOException e, String json) {
        log.error("TCP retry exhausted for request: {}", json, e);
        return "TCP_SERVER_UNAVAILABLE";
    }
}
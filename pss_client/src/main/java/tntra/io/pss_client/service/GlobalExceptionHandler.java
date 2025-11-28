package tntra.io.pss_client.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import tntra.io.pss_client.dto.ResponseDTO;
import tntra.io.pss_client.dto.TransactionFailedException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    // Business failure from server (responseCode != "00")
    @ExceptionHandler(TransactionFailedException.class)
    public ResponseEntity<ResponseDTO> handleTransactionFailed(TransactionFailedException e) {

        log.warn("Business transaction failed: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(e.getResponse());
    }

    // Client-side invalid JSON / validation failure
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ResponseDTO> handleInvalidRequest(IllegalArgumentException e) {

        log.error("Invalid client request: {}", e.getMessage());

        ResponseDTO error = new ResponseDTO();
        error.setResponseCode("09");
        error.setDestination(e.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(error);
    }

    // Network / system / unknown failures
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseDTO> handleGenericException(Exception e) {

        log.error("Unexpected system error", e);

        ResponseDTO error = new ResponseDTO();
        error.setResponseCode("99");
        error.setDestination("INTERNAL_SERVER_ERROR");

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(error);
    }
}

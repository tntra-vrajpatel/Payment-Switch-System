package tntra.io.pss_client.service.serviceImpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tntra.io.pss_client.dto.ResponseDTO;
import tntra.io.pss_client.dto.TransactionFailedException;
import tntra.io.pss_client.service.ResponseValidation;

@Slf4j
@Service
public class ResponseValidationImpl implements ResponseValidation {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public ResponseDTO validateResponse(String response) {

        try {
            // Convert JSON → DTO
            ResponseDTO dto = objectMapper.readValue(response, ResponseDTO.class);

            String responseCode = dto.getResponseCode();

            // SUCCESS FLOW
            if ("00".equals(responseCode)) {
                log.info("Transaction Successful with responseCode: {}", responseCode);
                dto.setMessage("Transaction Successful");
                return dto;
            }

            // BUSINESS FAILURE FLOW
            log.warn("Transaction failed with responseCode: {}", responseCode);

            switch (responseCode) {
                case "01":
                    dto.setMessage("Transaction failed due to Missing Transaction ID");
                    break;

                case "02":
                    dto.setMessage("Transaction failed due to Invalid PAN");
                    break;

                case "03":
                    dto.setMessage("Transaction failed due to Invalid Amount");
                    break;

                case "04":
                    dto.setMessage("Transaction failed because PAN is Blacklisted");
                    break;

                default:
                    dto.setMessage("Transaction failed due to Unknown Business Error");
                    break;
            }

            // MUST throw business exception so GlobalExceptionHandler returns 400
            throw new TransactionFailedException(dto);

        }
        // Let business exception pass through unchanged
        catch (TransactionFailedException e) {
            throw e;
        }
        // only malformed JSON / deserialization errors come here → 99
        catch (Exception e) {
            log.error("Invalid JSON received from server: {}", response, e);
            throw new RuntimeException("Invalid response format received from server", e);
        }
    }
}

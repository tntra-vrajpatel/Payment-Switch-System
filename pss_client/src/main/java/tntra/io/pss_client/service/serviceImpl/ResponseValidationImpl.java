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

    @Override
    public ResponseDTO validateResponse(String response) {

        try{
            ResponseDTO dto = new ObjectMapper().readValue(response, ResponseDTO.class);

            if("00".equals(dto.getResponseCode())){
                log.info("Transaction Successful: {}",dto.getResponseCode());
                return dto;
            }else {
                log.warn("Transaction failed with responseCode: {}", dto.getResponseCode());
                throw new TransactionFailedException(dto);

            }
        }catch (TransactionFailedException e) {
            throw e;
        }
        catch (Exception e){
            log.error("Invalid JSON received from server: {}", response);
            throw new RuntimeException("Invalid response format received from server", e);
        }
    }
}

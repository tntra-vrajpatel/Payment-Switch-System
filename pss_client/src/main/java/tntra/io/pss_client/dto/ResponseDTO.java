package tntra.io.pss_client.dto;

import lombok.Data;

@Data
public class ResponseDTO {
    private String transactionId;
    private String pan;
    private String amount;
    private String responseCode;
    private String destination;
}

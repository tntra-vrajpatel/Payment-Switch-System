package tntra.io.pss_server.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionMessage {

    private String transactionId;
    private String pan;
    private String amount;
    private String responseCode;
    private String destination;

//    public TransactionMessage(String txn001, String number, String number1) {
//    }
}

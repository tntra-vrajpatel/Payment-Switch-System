package tntra.io.pss_client.dto;

public class TransactionFailedException extends RuntimeException {

    private final ResponseDTO response;

    public TransactionFailedException(ResponseDTO response) {
        super("Transaction failed with responseCode: " + response.getResponseCode());
        this.response = response;
    }

    public ResponseDTO getResponse() {
        return response;
    }
}

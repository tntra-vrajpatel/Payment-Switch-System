package tntra.io.pss_server.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;
import tntra.io.pss_server.model.TransactionMessage;
import java.util.Set;

@Slf4j
@ConfigurationProperties(prefix = "routing")
@Service
public class ValidationService {

    @Value("${limits.pan-min-length}")
    private int panMinLength;

    @Value("${limits.amount-min}")
    private int minAmount;

    @Value("${limits.amount-max}")
    private int maxAmount;

    @Value("${limits.pan-max-length}")
    private int panMaxLength;


    private Set<String> blackListedPan;

    public Set<String> getBlackListedPan() {
        return blackListedPan;
    }

    public void setBlackListedPan(Set<String> blackListedPan) {
        this.blackListedPan = blackListedPan;
    }

    public void validateTransaction(TransactionMessage message) {

        if(message == null){
            log.error("Validation failed. TransactionMessage object is null");
        }

        // Id validation
        if (message.getTransactionId() == null || message.getTransactionId().isEmpty()) {
            log.warn("Validation failed: Missing Transaction ID");
            message.setResponseCode("01");
            return;
        }

        // Pan Validation
        if (message.getPan() == null) {
            log.warn("Validation failed: PAN is null");
            message.setResponseCode("02");
            return;
        }
        if (message.getPan().length() < panMinLength || message.getPan().length() > panMaxLength) {
            log.warn("Validation failed: Invalid PAN length {}", message.getPan());
            message.setResponseCode("02");
            return;
        }

        // Amount Validation
        Double amount;
        try {
            amount = Double.valueOf(message.getAmount());
        } catch (NumberFormatException e) {
            log.warn("Validation failed: Amount must be numeric");
            message.setResponseCode("03");
            return;
        }

        if (amount < minAmount || amount > maxAmount || amount == null) {
            log.warn("Validation failed: Amount {} not in allowed range {} - {}", amount, minAmount, maxAmount);
            message.setResponseCode("03");
            return;
        }

        // BlackListed PAN validation
        if (blackListedPan.contains(message.getPan())) {
            log.warn("Validation failed: Blacklisted PAN {}", message.getPan());
            message.setResponseCode("04");
            return;
        }
        log.debug("Validation successful for Transaction {}", message.getTransactionId());
        message.setResponseCode("00");
    }
}
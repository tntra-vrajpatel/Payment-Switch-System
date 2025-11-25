package tntra.io.pss_server.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;
import tntra.io.pss_server.model.TransactionMessage;

import java.util.Set;

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

        // Id validation
        if (message.getTransactionId() == null || message.getTransactionId().isEmpty()) {
            message.setResponseCode("01");
            return;
        }

        // Pan Validation
        if (message.getPan() == null) {
            message.setResponseCode("02");
            return;
        }
        if (message.getPan().length() < panMinLength || message.getPan().length() > panMaxLength) {
            message.setResponseCode("02");
            return;
        }

        // Amount Validation
        Double amount = Double.parseDouble(message.getAmount());

        if (amount < minAmount || amount > maxAmount || amount == null) {
            message.setResponseCode("03");
            return;
        }

        if (blackListedPan.contains(message.getPan())) {
            message.setResponseCode("04");
            return;
        }
        message.setResponseCode("00");
    }
}
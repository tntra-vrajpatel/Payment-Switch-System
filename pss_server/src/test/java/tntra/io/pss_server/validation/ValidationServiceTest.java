package tntra.io.pss_server.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tntra.io.pss_server.model.TransactionMessage;
import tntra.io.pss_server.service.ValidationService;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class ValidationServiceTest {

    private ValidationService validationService;

    @BeforeEach
    void setUp() {
        validationService = new ValidationService();
        validationService.setBlackListedPan(Set.of("9999991234567890"));

        // manually inject property values
        try {
            var panMinLength = ValidationService.class.getDeclaredField("panMinLength");
            panMinLength.setAccessible(true);
            panMinLength.set(validationService, 12);

            var panMaxLength = ValidationService.class.getDeclaredField("panMaxLength");
            panMaxLength.setAccessible(true);
            panMaxLength.set(validationService, 19);

            var minAmount = ValidationService.class.getDeclaredField("minAmount");
            minAmount.setAccessible(true);
            minAmount.set(validationService, 1);

            var maxAmount = ValidationService.class.getDeclaredField("maxAmount");
            maxAmount.setAccessible(true);
            maxAmount.set(validationService, 10000);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testNullTransactionId() {
        TransactionMessage msg = new TransactionMessage(null, "1234567890123456", "100", null, null);
        validationService.validateTransaction(msg);
        assertThat(msg.getResponseCode()).isEqualTo("01");
    }

    @Test
    void testInvalidPanTooShort() {
        TransactionMessage msg = new TransactionMessage("TXN001", "12345", "100", null, null);
        validationService.validateTransaction(msg);
        assertThat(msg.getResponseCode()).isEqualTo("02");
    }

    @Test
    void testAmountOutOfRange() {
        TransactionMessage msg = new TransactionMessage("TXN001", "1234567890123456", "0.5", null, null);
        validationService.validateTransaction(msg);
        assertThat(msg.getResponseCode()).isEqualTo("03");
    }

    @Test
    void testBlacklistedPan() {
        TransactionMessage msg = new TransactionMessage("TXN001", "9999991234567890", "500", null, null);
        validationService.validateTransaction(msg);
        assertThat(msg.getResponseCode()).isEqualTo("04");
    }

    @Test
    void testValidTransaction() {
        TransactionMessage msg = new TransactionMessage("TXN001", "1234567890123456", "500", null, null);
        validationService.validateTransaction(msg);
        assertThat(msg.getResponseCode()).isEqualTo("00");
    }
}

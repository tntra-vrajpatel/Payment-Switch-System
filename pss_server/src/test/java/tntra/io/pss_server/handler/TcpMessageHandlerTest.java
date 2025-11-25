package tntra.io.pss_server.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import tntra.io.pss_server.model.TransactionMessage;
import tntra.io.pss_server.service.RouterService;
import tntra.io.pss_server.service.ValidationService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class TcpMessageHandlerTest {

    @Mock
    private ValidationService validationService;

    @Mock
    private RouterService routerService;

    @InjectMocks
    private TcpMessageHandler tcpMessageHandler;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();
        tcpMessageHandler = new TcpMessageHandler();
        tcpMessageHandler.validationService = validationService;
        tcpMessageHandler.routerService = routerService;
        tcpMessageHandler.objectMapper = objectMapper;
    }

    @Test
    void testHandleValidMessage() throws Exception {
        TransactionMessage inputMsg = new TransactionMessage("TXN001", "1234567890123456", "500");
        Message<byte[]> message = MessageBuilder.withPayload(objectMapper.writeValueAsBytes(inputMsg)).build();

        doAnswer(invocation -> {
            TransactionMessage arg = invocation.getArgument(0);
            arg.setResponseCode("00");
            return null;
        }).when(validationService).validateTransaction(any());

        when(routerService.routeDestination(any())).thenReturn("Bank-A ");

        String response = tcpMessageHandler.handleMessage(message);

        assertThat(response).contains("\"responseCode\":\"00\"");
        assertThat(response).contains("\"destination\":\"Bank-A \"");
    }


    @Test
    void testInvalidJsonThrowsException() {
        Message<byte[]> message = MessageBuilder.withPayload("invalid".getBytes()).build();
        assertThrows(Exception.class, () -> tcpMessageHandler.handleMessage(message));
    }
}

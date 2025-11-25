package tntra.io.pss_server.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Component;
import tntra.io.pss_server.model.TransactionMessage;
import tntra.io.pss_server.service.RouterService;
import tntra.io.pss_server.service.ValidationService;

@Component
public class TcpMessageHandler {

    @Autowired
    ValidationService validationService;

    @Autowired
    RouterService routerService;

    @Autowired
    ObjectMapper objectMapper;

    @ServiceActivator(inputChannel = "inputChannel")
    public String handleMessage(Message<?> message) throws MessagingException {

        try {
            // Bytes format --> String format
            String stringPayload = new String((byte[]) message.getPayload());

            System.out.println("Received message in bytes : " + message);

            if (stringPayload.isEmpty() || !stringPayload.startsWith("{")) {
                throw new IllegalArgumentException("JSON format error" + stringPayload);
            }

            // String format --> Entity Obj format
            TransactionMessage objPayload = objectMapper.readValue(stringPayload, TransactionMessage.class);

            // Validation
            validationService.validateTransaction(objPayload);

            // Routing
            String objPan = objPayload.getPan();
            String destination = routerService.routeDestination(objPan);
            objPayload.setDestination(destination);
            System.out.println("Destination:" + destination);

            //Entity obj format --> String format
            String responseString = objectMapper.writeValueAsString(objPayload);
            return responseString;

        } catch (Exception e) {
            throw new MessagingException("Error handling message: " + e.getMessage(), e);
        }
    }
}

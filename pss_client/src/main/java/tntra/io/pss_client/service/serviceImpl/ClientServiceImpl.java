package tntra.io.pss_client.service.serviceImpl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tntra.io.pss_client.config.TcpClientConfig;
import tntra.io.pss_client.service.ClientService;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

@Slf4j
@Service
public class ClientServiceImpl implements ClientService {

    @Autowired
    private TcpClientConfig clientConfig;

    @Override
    public String sendRequest(String json) throws Exception {

        String host = clientConfig.getHost();
        int port = clientConfig.getPort();

        log.info("Connecting to TCP server at {}:{}", host, port);

        // Socket Creation
        try (Socket socket = new Socket(host, port)) {
            socket.setSoTimeout(10000);
            log.info("TCP connection established successfully");

            // Send Request
            OutputStream outputStream = socket.getOutputStream();
            outputStream.write((json.trim() + "\r\n").getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
            log.info("Request sent to server: {} ",json);

            // Read Response
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8))) {
                String response = bufferedReader.readLine();
                log.info("Received response from server: {} ",response);

                return response != null ? response : "";
            }
        }
        catch (Exception e){
            log.error("TCP communication error: {}\", e.getMessage(), e");
            throw e;
        }
    }
}
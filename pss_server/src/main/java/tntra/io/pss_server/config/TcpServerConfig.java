package tntra.io.pss_server.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.ip.tcp.TcpInboundGateway;
import org.springframework.integration.ip.tcp.connection.TcpNetServerConnectionFactory;
import org.springframework.integration.ip.tcp.serializer.ByteArrayCrLfSerializer;
import org.springframework.messaging.MessageChannel;

@Configuration
@ConfigurationProperties(prefix = "switch")

public class TcpServerConfig {

    private int port;

    public int getPort(){
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    // Establishes Connection & perform Serialization and Deserialization
    @Bean
    public TcpNetServerConnectionFactory serverConnectionFactory() {

        TcpNetServerConnectionFactory factory = new TcpNetServerConnectionFactory(port);

        ByteArrayCrLfSerializer serializer = new ByteArrayCrLfSerializer();
        factory.setSerializer(serializer);
        factory.setDeserializer(serializer);

        // Connection & Scalability settings ---
        factory.setSoTimeout(10000);              // 10 sec read timeout
        factory.setBacklog(100);                  // Maximum queued connections
        factory.setSoReceiveBufferSize(1024 * 64); // 64KB receive buffer
        factory.setSoSendBufferSize(1024 * 64);    // 64KB send buffer
        factory.setSoTcpNoDelay(true);               // Low latency
        factory.setSingleUse(false);               // Reuse connection

        return factory;
    }

    @Bean
    public MessageChannel inputChannel() {
        return new DirectChannel();
    }

    // set up connection and channels
    @Bean
    public TcpInboundGateway inboundGateway(TcpNetServerConnectionFactory factory) {

        TcpInboundGateway inboundGateway = new TcpInboundGateway();

        inboundGateway.setConnectionFactory(factory);
        inboundGateway.setRequestChannel(inputChannel());

        return inboundGateway;
    }
}
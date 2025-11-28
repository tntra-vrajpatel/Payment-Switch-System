package tntra.io.pss_server.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;
import java.util.List;

@Slf4j
@Service
@ConfigurationProperties(prefix = "routing")
public class RouterService {

    private List<String> bankAEndpoints;
    private List<String> bankBEndpoints;
    private String defaultEndpoint;

    public List<String> getBankBEndpoints() {
        return bankBEndpoints;
    }

    public void setBankBEndpoints(List<String> bankBEndpoints) {
        this.bankBEndpoints = bankBEndpoints;
    }

    public List<String> getBankAEndpoints() {
        return bankAEndpoints;
    }

    public void setBankAEndpoints(List<String> bankAEndpoints) {
        this.bankAEndpoints = bankAEndpoints;
    }

    public String getDefaultEndpoint() {
        return defaultEndpoint;
    }

    public void setDefaultEndpoint(String defaultEndpoint) {
        this.defaultEndpoint = defaultEndpoint;
    }

    // Routing Method
    public String routeDestination(String pan) {

        if (pan == null || pan.length() < 6) {
            log.error("Routing failed: Invalid PAN");
            return "Invalid PAN";
        }

        String bin = pan.substring(0, 6);

        for (String bankA : bankAEndpoints) {
            if (bin.startsWith(bankA)) {
                log.info("Routing mapped: BIN {} -> Bank-A", bin);
                return "Bank-A ";
            }
        }

        for (String bankB : bankBEndpoints) {
            if (bin.startsWith(bankB)) {
                log.info("Routing mapped: BIN {} -> Bank-B", bin);
                return "Bank-B ";
            }
        }
        log.info("Routing to default end-point for BIN {} ",bin);
        return defaultEndpoint;
    }
}

package tntra.io.pss_server.service;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

import java.util.List;

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
            return "Invalid PAN";
        }

        String bin = pan.substring(0, 6);

        for (String bankA : bankAEndpoints) {
            if (bin.startsWith(bankA)) {
                return "Bank-A ";
            }
        }

        for (String bankB : bankBEndpoints) {
            if (bin.startsWith(bankB)) {
                return "Bank-B ";
            }
        }
        return defaultEndpoint;
    }
}

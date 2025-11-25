package tntra.io.pss_server.route;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tntra.io.pss_server.service.RouterService;

import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

public class RouterServiceTest {

    private RouterService routerService;

    @BeforeEach
    void setUp() {
        routerService = new RouterService();
        routerService.setBankAEndpoints(List.of("123456", "111111"));
        routerService.setBankBEndpoints(List.of("654321", "222222"));
        routerService.setDefaultEndpoint("DEFAULT_ENDPOINT");
    }

    @Test
    void testRouteBankA() {
        String result = routerService.routeDestination("1234567890123456");
        assertThat(result).isEqualTo("Bank-A ");
    }

    @Test
    void testRouteBankB() {
        String result = routerService.routeDestination("6543217890123456");
        assertThat(result).isEqualTo("Bank-B ");
    }

    @Test
    void testRouteDefault() {
        String result = routerService.routeDestination("7777777890123456");
        assertThat(result).isEqualTo("DEFAULT_ENDPOINT");
    }

    @Test
    void testInvalidPan() {
        String result = routerService.routeDestination("123");
        assertThat(result).isEqualTo("Invalid PAN");
    }
}

package com.duncpro.jroute.util;

import com.duncpro.jroute.Path;
import com.duncpro.jroute.route.StaticRouteElement;
import com.duncpro.jroute.route.WildcardRouteElement;
import com.duncpro.jroute.util.ParameterizedRoute;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ParameterizedRouteTest {
    @Test
    public void testParameterizedRouteParsing() {
        ParameterizedRoute route = ParameterizedRoute.parse("/customers/{customerId}/orders/{orderId}");
        assertEquals(List.of("customerId", "orderId"), route.getParameterLabels());
        assertEquals(List.of(new StaticRouteElement("customers"), new WildcardRouteElement(),
                new StaticRouteElement("orders"), new WildcardRouteElement()), route.getElements());
    }

    @Test
    public void testVariableMapExtraction() {
        ParameterizedRoute route = ParameterizedRoute.parse("/customers/{customerId}/orders/{orderId}");
        Path path = new Path("/customers/duncan/orders/abc123");
        assertEquals(Map.of("customerId", "duncan", "orderId", "abc123"), route.extractVariablesMap(path));
    }
}

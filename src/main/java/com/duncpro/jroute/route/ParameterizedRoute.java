package com.duncpro.jroute.route;

import com.duncpro.jroute.Path;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * This class is an ergonomic wrapper around {@link Route} which associates a label
 * with each {@link WildcardRouteElement} in a {@link Route}.
 *
 * This class does not represent a unique type of {@link Route}, instead it is simply a utility
 * to make accessing the variable elements in a path more ergonomic. Since {@link ParameterizedRoute}
 * is simply a {@link Route}, two instances can be equal even if their parameter labels differ, so long as
 * each route contains the same {@link RouteElement}s in the same order. Moreover, a {@link ParameterizedRoute}
 * can be equal to a {@link Route}, as long as the aforementioned condition is true.
 */
class ParameterizedRoute extends Route {
    private final List<String> parameterLabels;

    /**
     * Creates a {@link ParameterizedRoute} where the ith string in the labels list corresponds to the ith
     * {@link WildcardRouteElement} in the elements list. See also {@link ParameterizedRoute#parse(String)},
     * which constructs a {@link ParameterizedRoute} from a human-readable parameterized route string.
     */
    public ParameterizedRoute(List<String> parameterLabels, List<RouteElement> elements) {
        super(elements);
        this.parameterLabels = List.copyOf(parameterLabels);

        final var wildcardCount = this.getElements().stream()
                .filter(element -> element instanceof WildcardRouteElement)
                .count();
        if (parameterLabels.size() != wildcardCount) throw new IllegalArgumentException();
    }

    public List<String> getParameterLabels() {
        return List.copyOf(parameterLabels);
    }

    /**
     * Returns a mapping of parameters to arguments given some {@link Path} whose structure matches
     * the {@link Route} represented by this {@link ParameterizedRoute}.
     *
     * For example a parameterized route string of '/customers/{customerId}/orders/{orderId}', and path of
     * "/customers/duncan/orders/abc123" would result in a return value of [(customerId, duncan), (orderId, abc123)].
     */
    public Map<String, String> extractVariablesMap(Path path) {
        Map<String, String> arguments = new HashMap<>();

        int wildcardsSoFar = 0;
        for (int i = 0; i < getElements().size(); i++) {
            RouteElement element = getElements().get(i);
            if (element instanceof WildcardRouteElement) {
                arguments.put(this.parameterLabels.get(wildcardsSoFar), path.getElements().get(i));
                wildcardsSoFar += 1;
            }
        }

        return arguments;
    }

    private enum RouteElementType {
        WILDCARD_PARAMETER,
        STATIC
    }

    /**
     * Constructs a {@link ParameterizedRoute} given a parameterized route string.
     * For example: "/customers/{customerId}/orders/{orderId}".
     * @throws IllegalArgumentException if the route string is malformed.
     */
    public static ParameterizedRoute parse(String routeString) {
        List<RouteElement> elements = new ArrayList<>();
        List<String> parameterLabels = new ArrayList<>();

        for (String rawElement : routeString.trim().split(Pattern.quote("/"))) {
            rawElement = rawElement.trim();
            if (rawElement.isEmpty()) continue;
            boolean hasParameterStart = rawElement.startsWith("{");
            boolean hasParameterEnd = rawElement.endsWith("}");
            if (hasParameterStart ^ hasParameterEnd) {
                throw new IllegalArgumentException("Expected parameterized" +
                        " route element to begin AND end with curly braces. For example \"{id}\"");
            }
            RouteElementType type = hasParameterStart ? RouteElementType.WILDCARD_PARAMETER : RouteElementType.STATIC;
            switch (type) {
                case WILDCARD_PARAMETER:
                    String parameterName = rawElement.substring(1, rawElement.length() - 1);
                    if (parameterName.contains("{") || parameterName.contains("}"))
                        throw new IllegalArgumentException("Only a single parameter per path segment is allowed");
                    elements.add(new WildcardRouteElement());
                    parameterLabels.add(parameterName);
                    break;
                case STATIC:
                    if (rawElement.contains("{") || rawElement.contains("}"))
                        throw new IllegalArgumentException("Parameters can not exist within static route elements");
                    elements.add(new StaticRouteElement(rawElement));
                    break;
            }
        }

        return new ParameterizedRoute(parameterLabels, elements);
    }
}

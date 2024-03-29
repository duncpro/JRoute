package com.duncpro.jroute.route;

import com.duncpro.jroute.JRouteInternalUtilities;
import com.duncpro.jroute.Path;
import net.jcip.annotations.Immutable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toUnmodifiableList;

@Immutable
public class Route {
    private final List<RouteElement> elements;

    public Route(List<RouteElement> elements) {
        this.elements = List.copyOf(elements);
    }

    public Route(String routeString) {
        this.elements = Stream.of(routeString.split(Pattern.quote("/")))
                .filter(str -> !str.isEmpty()) // Account for leading slash
                .map(str -> {
                    if (str.equals("*")) {
                        return new WildcardRouteElement();
                    } else {
                        return new StaticRouteElement(str);
                    }
                })
                .collect(toUnmodifiableList());
    }

    public List<String> extractVariables(String path) throws IllegalStateException {
        return extractVariables(new Path(path));
    }

    /**
     * Compiles a list of all the arguments contained within the given path.
     * For example, consider the route string "/users/*\/pets/*" and the path "/users/duncan/pets/cocoa".
     * The returned list would be ["duncan", "cocoa"].
     * @throws IllegalStateException if the given {@code pathString} is not of the same form as this route.
     */
    public List<String> extractVariables(Path path) throws IllegalStateException {

        if (!JRouteInternalUtilities.accepts(path, this)) {
            throw new IllegalArgumentException("The given path does not follow the" +
                    " form of this route.");
        }

        final var pathVariables = new ArrayList<String>();
        for (int i = 0; i < elements.size(); i++) {
            final var routeElement = this.getElements().get(i);

            if (routeElement instanceof WildcardRouteElement) {
                final var pathElement = path.getElements().get(i);
                pathVariables.add(pathElement);
            }
        }
        return pathVariables;
    }

    public static Route ROOT = new Route(List.of());

    public Route resolve(Route suffix) {
        var newRoute = this;
        for (RouteElement element : suffix.getElements()) {
            newRoute = newRoute.withTrailingElement(element);
        }
        return newRoute;
    }

    public Route withoutLeadingElement() {
        if (elements.isEmpty()) throw new IllegalArgumentException();
        final var newElements = new ArrayList<>(elements);
        newElements.remove(0);
        return new Route(newElements);
    }

    public Route withLeadingElement(RouteElement element) {
        if (element == null) throw new IllegalArgumentException();
        final var newElements = new ArrayList<RouteElement>();
        newElements.add(element);
        newElements.addAll(this.elements);
        return new Route(newElements);
    }

    public Route withTrailingElement(RouteElement element) {
        if (element == null) throw new IllegalArgumentException();
        final var newElements = new ArrayList<>(this.elements);
        newElements.add(element);
        return new Route(newElements);
    }

    public List<RouteElement> getElements() { return elements; }

    public boolean isRoot() { return elements.isEmpty(); }

    @Override
    public String toString() {
        final var builder = new StringBuilder();
        elements.forEach(element -> builder.append("/").append(element));
        return builder.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Route)) return false;
        Route route = (Route) o;
        return elements.equals(route.elements);
    }

    @Override
    public int hashCode() {
        return Objects.hash(elements);
    }

    public static Route concat(Route... routes) {
        Route concatted = Route.ROOT;
        for (final var route : routes) {
            for (final var element : route.getElements()) {
                concatted = concatted.withTrailingElement(element);
            }
        }
        return concatted;
    }
}

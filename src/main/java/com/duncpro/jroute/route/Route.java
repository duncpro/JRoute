package com.duncpro.jroute.route;

import com.duncpro.jroute.JRouteUtilities;
import com.duncpro.jroute.Path;
import net.jcip.annotations.Immutable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toUnmodifiableList;

@Immutable
public class Route {
    private final List<RouteElement> elements;

    protected Route(List<RouteElement> elements) {
        this.elements = Collections.unmodifiableList(elements);
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

    public static Route ROOT = new Route(List.of());

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

    public List<RouteElement> getElements() { return elements; }

    public boolean isRoot() { return elements.isEmpty(); }

    public List<String> extractVariables(String pathString) {
        final var path = new Path(pathString);

        if (!JRouteUtilities.accepts(path, this)) {
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

    @Override
    public String toString() {
        final var builder = new StringBuilder();
        elements.forEach(element -> builder.append("/").append(element));
        return builder.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Route route = (Route) o;
        return elements.equals(route.elements);
    }

    @Override
    public int hashCode() {
        return Objects.hash(elements);
    }
}

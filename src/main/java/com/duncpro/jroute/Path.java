package com.duncpro.jroute;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Path {
    private final List<String> elements;

    public Path(String pathString) {
        this.elements = Stream.of(pathString.split(Pattern.quote("/")))
                .filter(e -> !e.isEmpty()) // Account for leading slash
                .collect(Collectors.toUnmodifiableList());
    }

    protected Path(List<String> elements) {
        this.elements = Collections.unmodifiableList(elements);
    }

    public List<String> getElements() {
        return elements;
    }

    public Path withoutLeadingElement() {
        if (elements.isEmpty()) throw new IllegalStateException();
        final var newElements = new ArrayList<>(elements);
        newElements.remove(0);
        return new Path(newElements);
    }

    public boolean isRoot() {
        return elements.isEmpty();
    }

    @Override
    public String toString() {
        final var builder = new StringBuilder();
        elements.forEach(e -> builder.append("/").append(e));
        return builder.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Path path = (Path) o;
        return elements.equals(path.elements);
    }

    @Override
    public int hashCode() {
        return Objects.hash(elements);
    }
}

package com.duncpro.jroute;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PathTest {
    @Test
    public void createPathWithLeadingSlash() {
        String pathString = "/a/b/c/";
        final var actualPath = new Path(pathString);

        final var expectedPath = new Path(List.of(
                "a",
                "b",
                "c"
        ));
        assertEquals(expectedPath, actualPath);
    }

    @Test
    public void createPath() {
        String pathString = "a/b/c";
        final var actualPath = new Path(pathString);

        final var expectedPath = new Path(List.of(
                "a",
                "b",
                "c"
        ));
        assertEquals(expectedPath, actualPath);
    }

    @Test
    public void createPathWithTrailingSlash() {
        String pathString = "a/b/c/";
        final var actualPath = new Path(pathString);

        final var expectedPath = new Path(List.of(
                "a",
                "b",
                "c"
        ));
        assertEquals(expectedPath, actualPath);
    }
}

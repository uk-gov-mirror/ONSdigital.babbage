package com.github.onsdigital.babbage.api.filter;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class StaticFilesFilterTest {

    @Test
    public void shouldResolveRelativeFile() {

        String indexPathPath = "dir/index.html";
        String path = "webtrends.js";

        String result = StaticFilesFilter.resolveRelativePath(indexPathPath, path);

        assertEquals("dir/webtrends.js", result);
    }

    @Test
    public void shouldResolveRootFile() {

        String indexPathPath = "index.html";
        String path = "webtrends.js";

        String result = StaticFilesFilter.resolveRelativePath(indexPathPath, path);

        assertEquals("webtrends.js", result);
    }

    @Test
    public void shouldResolveNestedFile() {

        String indexPathPath = "dir/anotherdir/index.html";
        String path = "anotherdir/webtrends.js";

        String result = StaticFilesFilter.resolveRelativePath(indexPathPath, path);

        assertEquals("dir/anotherdir/webtrends.js", result);
    }

    @Test
    public void shouldResolveIndex() {

        String indexPathPath = "dir/index.html";
        String path = "dir/index.html";

        String result = StaticFilesFilter.resolveRelativePath(indexPathPath, path);

        assertEquals("dir/index.html", result);
    }
}

package com.github.onsdigital.api.util;

import org.junit.Test;
import static com.github.onsdigital.api.util.URIUtil.*;
import static org.junit.Assert.*;

/**
 * Created by bren on 29/05/15.
 */
public class URIUtilTest {


    private void testInvalidUri(String uriString) {
        // this catch block should be as small as possible, as you want to make sure you only catch exceptions from your code
        try {
            URIUtil.validate(uriString);
            fail("Resolving request type should have failed for" + uriString);
        } catch (URIUtil.InvalidUriException e) { //Expected exception
            return;
        }
    }


    @Test
    public void testValidate() {
        testInvalidUri("/////");
        testInvalidUri("//data/economy"); //Multiple slashes should fail
        testInvalidUri("/data//economy"); //Multiple slashes should fail
        testInvalidUri("data/economy/"); //Missing leading slash should fail

        validate("/data/economy");
        validate("/Data/economy");
        validate("/DATA/ECONOMY");
    }

    @Test
    public void testCleanUri() {

        assertEquals("/data/inflation", cleanUri("/DATA/INFLATION/"));//Clean trailing slash and lowercase
        assertEquals("/a/b/c", cleanUri("/A/b/c")); //);//Clean trailing slash and lowercase
    }


    @Test
    public void testResolveRequestType() {
        assertEquals("/", resolveRequestType("/"));
        assertEquals("data", resolveRequestType("/data"));
        assertEquals("data", resolveRequestType("/economy/inflationpriceindices/data"));
        assertEquals("s", resolveRequestType("/a/b/a/s/"));
    }

    @Test
    public void testResourceUri() {
        assertEquals("/", resolveRequestType("/"));
        assertEquals("data", resolveRequestType("/data"));
        assertEquals("data", resolveRequestType("/economy/inflationpriceindices/data"));
        assertEquals("s", resolveRequestType("/a/b/a/s/"));
    }

    @Test
    public void testRemoveEndpoint() {
        assertEquals("/", removeEndpoint("/"));
        assertEquals("/", removeEndpoint("/data"));
        assertEquals("/economy/inflationpriceindices", removeEndpoint("/data/economy/inflationpriceindices"));
        assertEquals("/inflationpriceindices", removeEndpoint("/economy/inflationpriceindices"));
        assertEquals("/b/a/s", removeEndpoint("/a/b/a/s/"));
    }

}
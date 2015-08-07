package com.github.onsdigital.babbage.content.client;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * Created by bren on 24/07/15
 *
 * Represents resource read from the external content service. Resource stream is open when obtained.
 * Make sure to close the resource after use or fully consume
 *
 *
 */
public class ContentResource extends AbstractContent implements Closeable{


    ContentResource(final String mimeType, final Charset charset, final InputStream dataStream) {

    }


}

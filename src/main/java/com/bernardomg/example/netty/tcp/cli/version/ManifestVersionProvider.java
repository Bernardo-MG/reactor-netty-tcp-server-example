/**
 * The MIT License (MIT)
 * <p>
 * Copyright (c) 2023 the original author or authors.
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.bernardomg.example.netty.tcp.cli.version;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import picocli.CommandLine;
import picocli.CommandLine.IVersionProvider;

/**
 * Version provider based on the JAR manifest.
 *
 * @author Bernardo Mart&iacute;nez Garrido
 *
 */
public final class ManifestVersionProvider implements IVersionProvider {

    /**
     * Logger.
     */
    private static final Logger LOGGER  = LoggerFactory.getLogger(ManifestVersionProvider.class);

    /**
     * Project title. Used to identify the correct manifest.
     */
    private static final String PROJECT = "Netty TCP Server Example";

    /**
     * Default constructor.
     */
    public ManifestVersionProvider() {
        super();
    }

    @Override
    public final String[] getVersion() throws Exception {
        final Enumeration<URL> resources = CommandLine.class.getClassLoader()
            .getResources("META-INF/MANIFEST.MF");
        String[]               result;
        Boolean                found;

        result = new String[0];
        found = false;
        while ((!found) && (resources.hasMoreElements())) {
            final URL        url;
            final Manifest   manifest;
            final Attributes attr;
            final String     version;
            final String     finalVersion;

            url = resources.nextElement();

            try {
                manifest = new Manifest(url.openStream());
            } catch (final IOException ex) {
                LOGGER.error("Unable to read from {}", url);
                // TODO: Use detailed error
                throw new RuntimeException();
            }

            if (isValid(manifest)) {
                attr = manifest.getMainAttributes();

                version = "%s version %s";
                finalVersion = String.format(version, get(attr, "Implementation-Title"),
                    get(attr, "Implementation-Version"));
                result = new String[] { finalVersion };
                found = true;
            }
        }

        return result;
    }

    /**
     * Returns the value for the received key.
     *
     * @param attributes
     *            source to get the value
     * @param key
     *            key to search for
     * @return value for the key
     */
    private final Object get(final Attributes attributes, final String key) {
        return attributes.get(new Attributes.Name(key));
    }

    /**
     * Checks if the manifest is the correct one.
     *
     * @param manifest
     *            manifest to check
     * @return {@code true} if it is the expected manifest, {@code false} in other case
     */
    private final Boolean isValid(final Manifest manifest) {
        final Attributes attributes;
        final Object     title;

        attributes = manifest.getMainAttributes();
        title = get(attributes, "Implementation-Title");

        return PROJECT.equals(title);
    }

}

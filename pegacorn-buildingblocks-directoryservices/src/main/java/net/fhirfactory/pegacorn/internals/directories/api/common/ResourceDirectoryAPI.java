/*
 * Copyright (c) 2021 Mark A. Hunter (ACT Health)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package net.fhirfactory.pegacorn.internals.directories.api.common;

import net.fhirfactory.pegacorn.internals.PegacornReferenceProperties;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;

import javax.inject.Inject;

public abstract class ResourceDirectoryAPI extends RouteBuilder {

    @Inject
    private PegacornReferenceProperties pegacornReferenceProperties;


    private static String SERVER_PORT = "12121";
    private static String SERVER_HOST = "0.0.0.0";

    protected String getIngresEndpoint(){
        String endpointSpecification =
                "netty-http:"
                        + "http://" + SERVER_HOST
                        + ":" + SERVER_PORT
                        + pegacornReferenceProperties.getPegacornResourceDirectoryR1Path();
        return(endpointSpecification);
    }

    @Override
    public void configure() throws Exception {

    }

    abstract protected Logger getLogger();
}

/*
 * Copyright (c) 2020 Mark A. Hunter (ACT Health)
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
package net.fhirfactory.pegacorn.platform.oam.topologysync;

import net.fhirfactory.pegacorn.oamwup.base.OAMWorkUnitProcessor;
import net.fhirfactory.pegacorn.workshops.base.OAMWorkshop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class TopologySynchronisationWorker extends OAMWorkUnitProcessor {
    private static final Logger LOG = LoggerFactory.getLogger(TopologySynchronisationWorker.class);
    private static final String OAM_WUP_NAME = "OAM-WUP-TopologySynchronisationWorker";
    private static final String OAM_WUP_VERSION = "1.0.0";

    @Inject
    private TopologySyncrhonisationWorkshop topologySyncrhonisationWorkshop;

    @Override
    protected Logger specifyLogger() {
        return (LOG);
    }

    @Override
    protected String specifyOAMWUPName() {
        return (OAM_WUP_NAME);
    }

    @Override
    protected String specifyOAMWUPVersion() {
        return (OAM_WUP_VERSION);
    }

    @Override
    protected OAMWorkshop specifyOAMWorkshop() {
        return (topologySyncrhonisationWorkshop);
    }

    @Override
    protected void invokePostConstructInitialisation() {

    }

    @Override
    public void configure() throws Exception {

    }
}

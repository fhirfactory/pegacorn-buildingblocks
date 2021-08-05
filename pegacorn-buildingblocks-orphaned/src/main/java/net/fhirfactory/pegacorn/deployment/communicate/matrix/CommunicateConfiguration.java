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
package net.fhirfactory.pegacorn.deployment.communicate.matrix;

public abstract class CommunicateConfiguration implements SynapseServerConfigurationInterface, CommunicateConfigurationInterface{

    @Override
    public String getSynapseServerAdminUserName()
    {
        return("CommunicateSynapseAdmin");
        // TODO read from environment variable
    }

    @Override
    public String getSynapseServerAdminPassword(){
        return("ClaudeAndClara01");
        // TODO read from environment variable
    }

    @Override
    public String getSynapseDefaultRoomVersion(){
        return("5");
    }

    @Override
    public String getSynapsePreferredRoomVersion(){
        return("5");
    }

    @Override
    public String getEmailAddressDomainName() {
        return "act.gov.au";
    }

    @Override
    public String getSynapseServerName() {
        return "lingo.aether.health.gov.au";
    }
}

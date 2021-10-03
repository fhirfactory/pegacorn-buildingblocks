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
package net.fhirfactory.pegacorn.petasos.endpoints.roles.common;

import net.fhirfactory.pegacorn.petasos.endpoints.technologies.datatypes.PetasosAdapterAddress;
import org.jgroups.JChannel;

public class IPCEndpointProxy {
    private PetasosAdapterAddress myAddress;
    private JChannel channel;
    private IPCEndpointProxyTypeEnum proxyType;

    public IPCEndpointProxy(PetasosAdapterAddress address, JChannel channel, IPCEndpointProxyTypeEnum proxyType) {
        this.channel = channel;
        this.proxyType = proxyType;
        this.myAddress = address;
    }

    public IPCEndpointProxy(JChannel channel, IPCEndpointProxyTypeEnum proxyType) {
        this.channel = channel;
        this.proxyType = proxyType;
        this.myAddress = null;
    }

    public IPCEndpointProxy(JChannel channel) {
        this.channel = channel;
        this.proxyType = IPCEndpointProxyTypeEnum.ENDPOINT_PROXY_JGROUPS;
        this.myAddress = null;
    }

    public IPCEndpointProxy() {
        this.proxyType = null;
        this.channel = null;
        this.myAddress = null;
    }

    public JChannel getChannel() {
        return channel;
    }

    public void setChannel(JChannel channel) {
        this.channel = channel;
    }

    public IPCEndpointProxyTypeEnum getProxyType() {
        return proxyType;
    }

    public void setProxyType(IPCEndpointProxyTypeEnum proxyType) {
        this.proxyType = proxyType;
    }

    public PetasosAdapterAddress getMyAddress() {
        return myAddress;
    }

    public void setMyAddress(PetasosAdapterAddress myAddress) {
        this.myAddress = myAddress;
    }

    @Override
    public String toString() {
        return "IPCEndpointProxy{" +
                "myAddress=" + myAddress +
                ", channel=" + channel +
                ", proxyType=" + proxyType +
                '}';
    }
}

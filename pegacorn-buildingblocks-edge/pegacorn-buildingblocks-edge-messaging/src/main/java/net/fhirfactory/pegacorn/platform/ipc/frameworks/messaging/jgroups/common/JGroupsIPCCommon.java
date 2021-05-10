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
package net.fhirfactory.pegacorn.platform.ipc.frameworks.messaging.jgroups.common;

import org.jgroups.JChannel;
import org.jgroups.blocks.RpcDispatcher;

public class JGroupsIPCCommon {
    private JChannel channel;
    private String channelName;
    private boolean channelInitialised;
    private RpcDispatcher rpcDispatcher;
    private boolean connectionOperational;
    private boolean dispatcherOperational;

    public JChannel getChannel() {
        return channel;
    }

    public void setChannel(JChannel channel) {
        this.channel = channel;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public boolean isChannelInitialised() {
        return channelInitialised;
    }

    public void setChannelInitialised(boolean channelInitialised) {
        this.channelInitialised = channelInitialised;
    }

    public RpcDispatcher getRpcDispatcher() {
        return rpcDispatcher;
    }

    public void setRpcDispatcher(RpcDispatcher rpcDispatcher) {
        this.rpcDispatcher = rpcDispatcher;
    }

    public boolean isConnectionOperational() {
        return connectionOperational;
    }

    public void setConnectionOperational(boolean connectionOperational) {
        this.connectionOperational = connectionOperational;
    }

    public boolean isDispatcherOperational() {
        return dispatcherOperational;
    }

    public void setDispatcherOperational(boolean dispatcherOperational) {
        this.dispatcherOperational = dispatcherOperational;
    }

    @Override
    public String toString() {
        return "JGroupsIPCCommon{" +
                "channel=" + channel +
                ", channelName='" + channelName + '\'' +
                ", channelInitialised=" + channelInitialised +
                ", rpcDispatcher=" + rpcDispatcher +
                ", connectionOperational=" + connectionOperational +
                ", dispatcherOperational=" + dispatcherOperational +
                '}';
    }
}

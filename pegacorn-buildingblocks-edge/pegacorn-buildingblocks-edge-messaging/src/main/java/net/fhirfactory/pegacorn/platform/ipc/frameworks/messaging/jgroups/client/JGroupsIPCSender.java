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

package net.fhirfactory.pegacorn.platform.ipc.frameworks.messaging.jgroups.client;

import net.fhirfactory.pegacorn.platform.ipc.frameworks.messaging.jgroups.common.JGroupsIPCCommon;
import org.jgroups.Address;
import org.jgroups.blocks.RpcDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JGroupsIPCSender extends JGroupsIPCCommon {
    private static final Logger LOG = LoggerFactory.getLogger(JGroupsIPCSender.class);

    private Address targetAddress;
    private String targetSubsystem;
    private boolean targetAddressInitialised;

    public Address getTargetAddress() {
        return targetAddress;
    }

    public void setTargetAddress(Address targetAddress) {
        this.targetAddress = targetAddress;
    }

    public boolean isTargetAddressInitialised() {
        return targetAddressInitialised;
    }

    public void setTargetAddressInitialised(boolean targetAddressInitialised) {
        this.targetAddressInitialised = targetAddressInitialised;
    }

    public String getTargetSubsystem() {
        return targetSubsystem;
    }

    public void setTargetSubsystem(String targetSubsystem) {
        this.targetSubsystem = targetSubsystem;
    }

    @Override
    public String toString() {
        return "JGroupsIPCSender{" +
                "targetAddress=" + targetAddress +
                ", targetSubsystem='" + targetSubsystem + '\'' +
                ", targetAddressInitialised=" + targetAddressInitialised +
                '}';
    }
}

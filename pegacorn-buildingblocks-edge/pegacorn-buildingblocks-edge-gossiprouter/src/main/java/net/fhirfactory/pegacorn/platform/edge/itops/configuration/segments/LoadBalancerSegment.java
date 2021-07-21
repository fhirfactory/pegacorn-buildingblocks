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
package net.fhirfactory.pegacorn.platform.edge.itops.configuration.segments;

import org.apache.commons.lang3.StringUtils;

public class LoadBalancerSegment {
    private String type;
    private String ipAddress;

    public LoadBalancerSegment(){
        setType(null);
        setIpAddress(null);
    }

    public void mergeOverrides(LoadBalancerSegment overrides){
        if(overrides.hasType()){
            setType(overrides.getType());
        }
        if(overrides.hasIpAddress()){
            setIpAddress(overrides.getIpAddress());
        }
    }

    public boolean hasType(){
        boolean has = !(StringUtils.isEmpty(this.type));
        return(has);
    }

    public boolean hasIpAddress(){
        boolean has = !(StringUtils.isEmpty(this.ipAddress));
        return(has);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    @Override
    public String toString() {
        return "LoadBalancerSegment{" +
                "type='" + type + '\'' +
                ", ipAddress='" + ipAddress + '\'' +
                '}';
    }
}

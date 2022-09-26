/*
 * The MIT License
 *
 * Copyright 2022 Mark A. Hunter.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package net.fhirfactory.pegacorn.core.model.capabilities.definition;

import java.io.Serializable;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author mhunter
 */
public class Capability implements Serializable{
    private String sector;
    private String domain;
    private String area;
    private String level0Capability;
    private String level1Capability;
    private String level2Capability;
    private String service;
    private String function;
    private String displayName;
    private String version;

    //
    // Constructor(s)
    //

    public Capability(){
        this.sector = null;
        this.domain = null;
        this.area = null;
        this.level0Capability = null;
        this.level1Capability = null;
        this.level2Capability = null;
        this.service = null;
        this.function = null;
        this.version = null;
        this.displayName = null;
    }

    public Capability(Capability ori){
        this.sector = null;
        this.domain = null;
        this.area = null;
        this.level0Capability = null;
        this.level1Capability = null;
        this.level2Capability = null;
        this.service = null;
        this.function = null;
        this.version = null;
        this.displayName = null;

        if(StringUtils.isNotEmpty(ori.getSector())){
            setSector(ori.getSector());
        }
        if(StringUtils.isNotEmpty(ori.getDomain())){
            setDomain(ori.getDomain());
        }
        if(StringUtils.isNotEmpty(ori.getArea())){
            setArea(ori.getArea());
        }
        if(StringUtils.isNotEmpty(ori.getLevel0Capability())){
            setLevel0Capability(ori.getLevel0Capability());
        }
        if(StringUtils.isNotEmpty(ori.getLevel0Capability())){
            setLevel1Capability(ori.getLevel1Capability());
        }
        if(StringUtils.isNotEmpty(ori.getLevel0Capability())){
            setLevel2Capability(ori.getLevel2Capability());
        }
        if(StringUtils.isNotEmpty(ori.getService())){
            setService(ori.getService());
        }
        if(StringUtils.isNotEmpty(ori.getFunction())){
            setFunction(ori.getFunction());
        }
        if(StringUtils.isNotEmpty(ori.getVersion())){
            setVersion(ori.getVersion());
        }
        if(StringUtils.isNotEmpty(ori.getDisplayName())){
            setDisplayName(ori.getDisplayName());
        }
    }

    //
    // Getters and Setters
    //

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getLevel0Capability() {
        return level0Capability;
    }

    public void setLevel0Capability(String level0Capability) {
        this.level0Capability = level0Capability;
    }

    public String getLevel1Capability() {
        return level1Capability;
    }

    public void setLevel1Capability(String level1Capability) {
        this.level1Capability = level1Capability;
    }

    public String getLevel2Capability() {
        return level2Capability;
    }

    public void setLevel2Capability(String level2Capability) {
        this.level2Capability = level2Capability;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getFunction() {
        return function;
    }

    public void setFunction(String function) {
        this.function = function;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    //
    // toString
    //

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ComponentCapabilityType{");
        sb.append("sector=").append(sector);
        sb.append(", domain=").append(domain);
        sb.append(", area=").append(area);
        sb.append(", level0Capability=").append(level0Capability);
        sb.append(", level1Capability=").append(level1Capability);
        sb.append(", level2Capability=").append(level2Capability);
        sb.append(", service=").append(service);
        sb.append(", function=").append(function);
        sb.append(", displayName=").append(displayName);
        sb.append(", version=").append(version);
        sb.append('}');
        return sb.toString();
    }
}

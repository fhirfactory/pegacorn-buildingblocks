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

public class DeploymentSiteSegment {
    private Integer siteCount;
    private String site1Name;
    private String site2Name;
    private String site3Name;

    public DeploymentSiteSegment(){
        this.siteCount = 0;
        this.site1Name = null;
        this.site2Name = null;
        this.site3Name = null;
    }

    public void mergeOverrides(DeploymentSiteSegment overrides){
        if(overrides.getSiteCount() > 0){
            this.siteCount = overrides.getSiteCount();
        }
        if(overrides.hasSite1Name()){
            this.site1Name = overrides.getSite1Name();
        }
        if(overrides.hasSite2Name()){
            this.site2Name = overrides.getSite2Name();
        }
        if(overrides.hasSite3Name()){
            this.site3Name = overrides.getSite3Name();
        }
    }

    public boolean hasSite1Name(){
        boolean has = !(StringUtils.isEmpty(this.site1Name));
        return(has);
    }

    public boolean hasSite2Name(){
        boolean has = !(StringUtils.isEmpty(this.site2Name));
        return(has);
    }

    public boolean hasSite3Name(){
        boolean has = !(StringUtils.isEmpty(this.site3Name));
        return(has);
    }

    public Integer getSiteCount() {
        return siteCount;
    }

    public void setSiteCount(Integer siteCount) {
        this.siteCount = siteCount;
    }

    public String getSite2Name() {
        return site2Name;
    }

    public void setSite2Name(String site2Name) {
        this.site2Name = site2Name;
    }

    public String getSite3Name() {
        return site3Name;
    }

    public void setSite3Name(String site3Name) {
        this.site3Name = site3Name;
    }

    public String getSite1Name() {
        return site1Name;
    }

    public void setSite1Name(String site1Name) {
        this.site1Name = site1Name;
    }

    @Override
    public String toString() {
        return "DeploymentSiteSegment{" +
                "siteCount=" + siteCount +
                ", site1Name='" + site1Name + '\'' +
                ", site2Name='" + site2Name + '\'' +
                ", site3Name='" + site3Name + '\'' +
                '}';
    }
}

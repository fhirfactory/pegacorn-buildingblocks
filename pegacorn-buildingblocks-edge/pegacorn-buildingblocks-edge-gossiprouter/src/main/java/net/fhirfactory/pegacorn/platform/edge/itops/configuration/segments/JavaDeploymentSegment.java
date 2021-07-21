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

public class JavaDeploymentSegment {
    private String jmxMaxHeapSizeMB;
    private String jvmMemSizeMB;

    public JavaDeploymentSegment(){
        this.jmxMaxHeapSizeMB = null;
        this.jvmMemSizeMB = null;
    }

    public void mergeOverrides(JavaDeploymentSegment overrides){
        if(overrides.hasJmxMaxHeapSizeMB()){
            setJmxMaxHeapSizeMB(overrides.getJmxMaxHeapSizeMB());
        }
        if(overrides.hasJvmMemSizeMB()){
            setJvmMemSizeMB(overrides.getJvmMemSizeMB());
        }
    }

    public boolean hasJmxMaxHeapSizeMB(){
        boolean has = !(StringUtils.isEmpty(this.jmxMaxHeapSizeMB));
        return(has);
    }

    public boolean hasJvmMemSizeMB(){
        boolean has = !(StringUtils.isEmpty(this.jvmMemSizeMB));
        return(has);
    }

    public String getJmxMaxHeapSizeMB() {
        return jmxMaxHeapSizeMB;
    }

    public void setJmxMaxHeapSizeMB(String jmxMaxHeapSizeMB) {
        this.jmxMaxHeapSizeMB = jmxMaxHeapSizeMB;
    }

    public String getJvmMemSizeMB() {
        return jvmMemSizeMB;
    }

    public void setJvmMemSizeMB(String jvmMemSizeMB) {
        this.jvmMemSizeMB = jvmMemSizeMB;
    }

    @Override
    public String toString() {
        return "JavaDeploymentSegment{" +
                "jmxMaxHeapSizeMB='" + jmxMaxHeapSizeMB + '\'' +
                ", jvmMemSizeMB='" + jvmMemSizeMB + '\'' +
                '}';
    }
}

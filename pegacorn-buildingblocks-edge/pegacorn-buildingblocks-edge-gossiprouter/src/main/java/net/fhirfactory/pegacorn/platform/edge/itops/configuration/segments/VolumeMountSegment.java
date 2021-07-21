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

public class VolumeMountSegment {
    private String volumeMountName;
    private String volumeMountPath;
    private String volumeName;
    private String volumeHostPath;

    public VolumeMountSegment(){
        setVolumeHostPath(null);
        setVolumeMountName(null);
        setVolumeName(null);
        setVolumeMountPath(null);
    }

    public void mergeOverrides(VolumeMountSegment overrides){
        if (overrides.hasVolumeMountName()) {
            setVolumeMountName(overrides.getVolumeMountName());
        }
        if (overrides.hasVolumeName()) {
            setVolumeName(overrides.getVolumeName());
        }
        if (overrides.hasVolumeMountPath()) {
            setVolumeMountPath(overrides.getVolumeMountPath());
        }
        if(overrides.hasVolumeHostPath()){
            setVolumeHostPath(overrides.getVolumeHostPath());
        }
    }

    public boolean hasVolumeMountPath(){
        boolean has = !(StringUtils.isEmpty(volumeMountPath));
        return(has);
    }

    public boolean hasVolumeName(){
        boolean has = !(StringUtils.isEmpty(volumeName));
        return(has);
    }

    public boolean hasVolumeMountName(){
        boolean has = !(StringUtils.isEmpty(volumeMountName));
        return(has);
    }

    public boolean hasVolumeHostPath(){
        boolean has = !(StringUtils.isEmpty(volumeHostPath));
        return(has);
    }

    public String getVolumeMountName() {
        return volumeMountName;
    }

    public void setVolumeMountName(String volumeMountName) {
        this.volumeMountName = volumeMountName;
    }

    public String getVolumeMountPath() {
        return volumeMountPath;
    }

    public void setVolumeMountPath(String volumeMountPath) {
        this.volumeMountPath = volumeMountPath;
    }

    public String getVolumeName() {
        return volumeName;
    }

    public void setVolumeName(String volumeName) {
        this.volumeName = volumeName;
    }

    public String getVolumeHostPath() {
        return volumeHostPath;
    }

    public void setVolumeHostPath(String volumeHostPath) {
        this.volumeHostPath = volumeHostPath;
    }

    @Override
    public String toString() {
        return "VolumeMountSegment{" +
                "volumeMountName='" + volumeMountName + '\'' +
                ", volumeMountPath='" + volumeMountPath + '\'' +
                ", volumeName='" + volumeName + '\'' +
                ", volumeHostPath='" + volumeHostPath + '\'' +
                '}';
    }
}

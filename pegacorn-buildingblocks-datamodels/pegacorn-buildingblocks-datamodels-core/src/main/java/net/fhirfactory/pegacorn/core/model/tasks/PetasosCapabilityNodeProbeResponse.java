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
package net.fhirfactory.pegacorn.core.model.tasks;

import java.time.Instant;
import java.util.Objects;

public class PetasosCapabilityNodeProbeResponse extends PetasosCapabilityDeliveryNodeSet{
    private boolean inScope;
    private Instant probeDate;
    private boolean probeSuccessful;
    private String probeCommentary;

    public String getProbeCommentary() {
        return probeCommentary;
    }

    public void setProbeCommentary(String probeCommentary) {
        this.probeCommentary = probeCommentary;
    }

    public boolean isProbeSuccessful() {
        return probeSuccessful;
    }

    public void setProbeSuccessful(boolean probeSuccessful) {
        this.probeSuccessful = probeSuccessful;
    }

    public boolean isInScope() {
        return inScope;
    }

    public void setInScope(boolean inScope) {
        this.inScope = inScope;
    }

    public Instant getProbeDate() {
        return probeDate;
    }

    public void setProbeDate(Instant probeDate) {
        this.probeDate = probeDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PetasosCapabilityNodeProbeResponse)) return false;
        PetasosCapabilityNodeProbeResponse that = (PetasosCapabilityNodeProbeResponse) o;
        return isInScope() == that.isInScope() && isProbeSuccessful() == that.isProbeSuccessful() && Objects.equals(getProbeDate(), that.getProbeDate()) && Objects.equals(getProbeCommentary(), that.getProbeCommentary());
    }

    @Override
    public int hashCode() {
        return Objects.hash(isInScope(), getProbeDate(), isProbeSuccessful(), getProbeCommentary());
    }

    @Override
    public String toString() {
        return "PetasosCapabilityNodeProbeResponse{" +
                "capabilityDeliveryNodeSet=" + getCapabilityDeliveryNodeSet() +
                ", routingEndpointName='" + getRoutingEndpointName() + '\'' +
                ", inScope=" + inScope +
                ", probeDate=" + probeDate +
                ", probeSuccessful=" + probeSuccessful +
                ", probeCommentary='" + probeCommentary + '\'' +
                '}';
    }
}

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
package net.fhirfactory.pegacorn.core.model.petasos.oam.metrics.component;

import com.fasterxml.jackson.annotation.JsonFormat;
import net.fhirfactory.pegacorn.core.constants.petasos.PetasosPropertyConstants;
import net.fhirfactory.pegacorn.core.model.componentid.ComponentIdType;
import net.fhirfactory.pegacorn.core.model.petasos.oam.metrics.component.common.CommonComponentMetricsData;
import org.infinispan.commons.hash.Hash;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class ProcessingPlantMetricsData extends CommonComponentMetricsData {
    private Map<String, Integer> localCacheSize;
    private Map<String, Instant> localWatchDogActivity;
    private Map<String, Instant> localPathwaySynchronisationActivity;
    private int synchronousAuditEventsWritten;
    private int asynchronousAuditEventsWritten;
    private int asynchronousAuditEventsQueued;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSXXX", timezone = PetasosPropertyConstants.DEFAULT_TIMEZONE)
    private Instant lastAsynchronousAuditEventWrite;

    //
    // Constructor(s)
    //

    public ProcessingPlantMetricsData(){
        super();
        this.localCacheSize = new HashMap<>();
        this.synchronousAuditEventsWritten = 0;
        this.asynchronousAuditEventsWritten = 0;
        this.asynchronousAuditEventsQueued = 0;
        this.lastAsynchronousAuditEventWrite = null;
        this.localWatchDogActivity = new HashMap<>();
        this.localPathwaySynchronisationActivity = new HashMap<>();
    }

    public ProcessingPlantMetricsData(ComponentIdType componentId){
        super(componentId);
        this.localCacheSize = new HashMap<>();
        this.localWatchDogActivity = new HashMap<>();
        this.localPathwaySynchronisationActivity = new HashMap<>();
        this.synchronousAuditEventsWritten = 0;
        this.asynchronousAuditEventsWritten = 0;
        this.asynchronousAuditEventsQueued = 0;
        this.lastAsynchronousAuditEventWrite = null;
    }

    //
    // Getters and Setters
    //

    public Map<String, Instant> getLocalPathwaySynchronisationActivity() {
        return localPathwaySynchronisationActivity;
    }

    public void setLocalPathwaySynchronisationActivity(Map<String, Instant> localPathwaySynchronisationActivity) {
        this.localPathwaySynchronisationActivity = localPathwaySynchronisationActivity;
    }

    public Map<String, Instant> getLocalWatchDogActivity() {
        return localWatchDogActivity;
    }

    public void setLocalWatchDogActivity(Map<String, Instant> localWatchDogActivity) {
        this.localWatchDogActivity = localWatchDogActivity;
    }

    public Map<String, Integer> getLocalCacheSize() {
        return localCacheSize;
    }

    public void setLocalCacheSize(Map<String, Integer> localCacheSize) {
        this.localCacheSize = localCacheSize;
    }

    public int getSynchronousAuditEventsWritten() {
        return synchronousAuditEventsWritten;
    }

    public void setSynchronousAuditEventsWritten(int synchronousAuditEventsWritten) {
        this.synchronousAuditEventsWritten = synchronousAuditEventsWritten;
    }

    public int getAsynchronousAuditEventsWritten() {
        return asynchronousAuditEventsWritten;
    }

    public void setAsynchronousAuditEventsWritten(int asynchronousAuditEventsWritten) {
        this.asynchronousAuditEventsWritten = asynchronousAuditEventsWritten;
    }

    public int getAsynchronousAuditEventsQueued() {
        return asynchronousAuditEventsQueued;
    }

    public void setAsynchronousAuditEventsQueued(int asynchronousAuditEventsQueued) {
        this.asynchronousAuditEventsQueued = asynchronousAuditEventsQueued;
    }

    public Instant getLastAsynchronousAuditEventWrite() {
        return lastAsynchronousAuditEventWrite;
    }

    public void setLastAsynchronousAuditEventWrite(Instant lastAsynchronousAuditEventWrite) {
        this.lastAsynchronousAuditEventWrite = lastAsynchronousAuditEventWrite;
    }

    //
    // To String
    //

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ProcessingPlantMetricsData{");
        sb.append("localCacheSize=").append(localCacheSize);
        sb.append(", localWatchDogActivity=").append(localWatchDogActivity);
        sb.append(", localPathwaySynchronisationActivity=").append(localPathwaySynchronisationActivity);
        sb.append(", synchronousAuditEventsWritten=").append(synchronousAuditEventsWritten);
        sb.append(", asynchronousAuditEventsWritten=").append(asynchronousAuditEventsWritten);
        sb.append(", asynchronousAuditEventsQueued=").append(asynchronousAuditEventsQueued);
        sb.append(", lastAsynchronousAuditEventWrite=").append(lastAsynchronousAuditEventWrite);
        sb.append(", egressMessageSuccessCount=").append(getEgressMessageSuccessCount());
        sb.append(", egressMessageFailureCount=").append(getEgressMessageFailureCount());
        sb.append(", ingresMessageCount=").append(getIngresMessageCount());
        sb.append(", egressMessageAttemptCount=").append(getEgressMessageAttemptCount());
        sb.append(", internalDistributedMessageCount=").append(getInternalDistributedMessageCount());
        sb.append(", internalDistributionCountMap=").append(getInternalDistributionCountMap());
        sb.append(", componentID=").append(getComponentID());
        sb.append(", componentType=").append(getComponentType());
        sb.append(", lastActivityInstant=").append(getLastActivityInstant());
        sb.append(", componentStartupInstant=").append(getComponentStartupInstant());
        sb.append(", componentStatus='").append(getComponentStatus()).append('\'');
        sb.append(", participantName='").append(getParticipantName()).append('\'');
        sb.append(", internalReceivedMessageCount=").append(getInternalReceivedMessageCount());
        sb.append('}');
        return sb.toString();
    }
}

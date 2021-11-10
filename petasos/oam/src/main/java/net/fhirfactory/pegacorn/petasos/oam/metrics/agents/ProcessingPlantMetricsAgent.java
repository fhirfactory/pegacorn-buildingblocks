package net.fhirfactory.pegacorn.petasos.oam.metrics.agents;

import com.fasterxml.jackson.annotation.JsonFormat;
import net.fhirfactory.pegacorn.core.constants.petasos.PetasosPropertyConstants;
import net.fhirfactory.pegacorn.core.model.componentid.ComponentIdType;
import net.fhirfactory.pegacorn.petasos.oam.metrics.agents.common.ComponentMetricsAgentBase;
import org.apache.commons.lang3.StringUtils;

import java.time.Instant;
import java.util.HashMap;

public class ProcessingPlantMetricsAgent extends ComponentMetricsAgentBase {
    public static final String PROCESSING_PLANT_METRICS_TYPE = "ProcessingPlantBasedMetrics";

    private HashMap<String, String> localCacheStatusMap;
    private int synchronousAuditEventsWritten;
    private int asynchronousAuditEventsWritten;
    private int asynchronousAuditEventsQueued;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSXXX", timezone = PetasosPropertyConstants.DEFAULT_TIMEZONE)
    private Instant lastAsynchronousAuditEventWrite;

    //
    // Constructors
    //

    public ProcessingPlantMetricsAgent(){
        super();
        this.localCacheStatusMap = new HashMap<>();
        this.setMetricsType(PROCESSING_PLANT_METRICS_TYPE);
        this.synchronousAuditEventsWritten = 0;
        this.asynchronousAuditEventsQueued = 0;
        this.asynchronousAuditEventsWritten = 0;
        this.lastAsynchronousAuditEventWrite = Instant.EPOCH;
    }

    public ProcessingPlantMetricsAgent(ComponentIdType componentID){
        super(componentID);
        this.localCacheStatusMap = new HashMap<>();
        this.setMetricsType(PROCESSING_PLANT_METRICS_TYPE);
        this.synchronousAuditEventsWritten = 0;
        this.asynchronousAuditEventsQueued = 0;
        this.asynchronousAuditEventsWritten = 0;
        this.lastAsynchronousAuditEventWrite = Instant.EPOCH;
    }

    //
    // Helpers
    //

    public void updateLocalCacheStatus(String cacheName, String cacheStatus){
        if(StringUtils.isEmpty(cacheName) || StringUtils.isEmpty(cacheStatus)){
            return;
        }
        if(this.localCacheStatusMap.containsKey(cacheName)){
            this.localCacheStatusMap.remove(cacheName);
        }
        this.localCacheStatusMap.put(cacheName, cacheStatus);
    }

    public void incrementSynchronousAuditEventWritten(){
        this.synchronousAuditEventsWritten += 1;
    }

    public void incrementAsynchronousAuditEventWritten(){
        this.asynchronousAuditEventsWritten += 1;
    }

    public void touchAsynchronousAuditEventWrite(){
        this.lastAsynchronousAuditEventWrite = Instant.now();
    }

    //
    // Getters and Setters
    //


    public HashMap<String, String> getLocalCacheStatusMap() {
        return localCacheStatusMap;
    }

    public void setLocalCacheStatusMap(HashMap<String, String> localCacheStatusMap) {
        this.localCacheStatusMap = localCacheStatusMap;
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
        return "ProcessingPlantNodeMetrics{" +
                "localCacheStatusMap=" + localCacheStatusMap +
                ", synchronousAuditEventsWritten=" + synchronousAuditEventsWritten +
                ", asynchronousAuditEventsWritten=" + asynchronousAuditEventsWritten +
                ", asynchronousAuditEventsQueued=" + asynchronousAuditEventsQueued +
                ", lastAsynchronousAuditEventWrite=" + lastAsynchronousAuditEventWrite +
                ", componentID=" + getComponentID() +
                ", lastActivityInstant=" + getLastActivityInstant() +
                ", nodeStartupInstant=" + getComponentStartupInstant() +
                ", nodeStatus=" + getComponentStatus() +
                ", metricsType=" + getMetricsType() +
                '}';
    }
}

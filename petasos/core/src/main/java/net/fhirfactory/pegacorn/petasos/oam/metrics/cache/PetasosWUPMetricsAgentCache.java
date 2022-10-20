package net.fhirfactory.pegacorn.petasos.oam.metrics.cache;

import net.fhirfactory.pegacorn.petasos.oam.metrics.agents.WorkUnitProcessorMetricsAgent;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class PetasosWUPMetricsAgentCache {
    private static final Logger LOG = LoggerFactory.getLogger(PetasosWUPMetricsAgentCache.class);

    ConcurrentHashMap<String, WorkUnitProcessorMetricsAgent> metricsAgentMap;

    //
    // Constructor(s)
    //

    public PetasosWUPMetricsAgentCache(){
        metricsAgentMap = new ConcurrentHashMap<>();
    }

    //
    // Getters and Setters
    //

    public ConcurrentHashMap<String, WorkUnitProcessorMetricsAgent> getMetricsAgentMap() {
        return metricsAgentMap;
    }

    public void setMetricsAgentMap(ConcurrentHashMap<String, WorkUnitProcessorMetricsAgent> metricsAgentMap) {
        this.metricsAgentMap = metricsAgentMap;
    }

    public void addMetricsAgent(WorkUnitProcessorMetricsAgent agent){
        if(agent == null){
            return;
        }
        if(StringUtils.isNotEmpty(agent.getWUPMetricsData().getParticipantName())){
            if(getMetricsAgentMap().containsKey(agent.getWUPMetricsData().getParticipantName())){
                getMetricsAgentMap().remove(agent.getWUPMetricsData().getParticipantName());
            }
            getMetricsAgentMap().put(agent.getWUPMetricsData().getParticipantName(), agent);
        }
    }

    public WorkUnitProcessorMetricsAgent getMetricsAgent(String participantName){
        if(StringUtils.isEmpty(participantName)){
            return(null);
        }
        if(getMetricsAgentMap().containsKey(participantName)){
            WorkUnitProcessorMetricsAgent workUnitProcessorMetricsAgent = getMetricsAgentMap().get(participantName);
            return(workUnitProcessorMetricsAgent);
        }
        return(null);
    }
}

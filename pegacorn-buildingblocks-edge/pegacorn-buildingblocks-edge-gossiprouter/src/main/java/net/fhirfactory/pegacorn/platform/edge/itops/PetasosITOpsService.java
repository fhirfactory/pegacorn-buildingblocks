package net.fhirfactory.pegacorn.platform.edge.itops;

import net.fhirfactory.pegacorn.platform.edge.itops.apiservices.APIServicesFacade;
import net.fhirfactory.pegacorn.platform.edge.itops.configuration.JGroupsGossipRouterNode;
import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultCamelContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PetasosITOpsService {
    private static final Logger LOG = LoggerFactory.getLogger(PetasosITOpsService.class);

    protected Logger getLogger(){
        return(LOG);
    }
	
    CamelContext camelContext;
    APIServicesFacade servicesFacade;

    public PetasosITOpsService(JGroupsGossipRouterNode node){
        getLogger().debug(".PetasosITOpsService(): Entry, node->{}", node);
        CamelContext context = new DefaultCamelContext();
        setCamelContext(context);
        getLogger().trace(".PetasosITOpsService(): context->{}", context);
        APIServicesFacade apiService = new APIServicesFacade(node);
        setServicesFacade(apiService);
        getLogger().trace(".PetasosITOpsService(): servicesFacade->{}", apiService);
        try {
            getLogger().trace(".PetasosITOpsService(): Adding Routes");
            getCamelContext().addRoutes(getServicesFacade());
            getLogger().trace(".PetasosITOpsService(): Routes Added");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public CamelContext getCamelContext() {
        return camelContext;
    }

    public void setCamelContext(CamelContext camelContext) {
        this.camelContext = camelContext;
    }

    public APIServicesFacade getServicesFacade() {
        return servicesFacade;
    }

    public void setServicesFacade(APIServicesFacade servicesFacade) {
        this.servicesFacade = servicesFacade;
    }

    public void start(){
        getCamelContext().start();
    }

    public void stop(){
        getCamelContext().stop();
    }

    public void updateDate(){
        servicesFacade.updateDate();
    }
}

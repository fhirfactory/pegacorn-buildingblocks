package net.fhirfactory.pegacorn.platform.edge.endpoints.roles.base;

import net.fhirfactory.pegacorn.platform.edge.endpoints.roles.common.IPCEndpointAddress;

import java.util.List;

public interface EndpointChangeNotificationActionInterface {

    public void notifyMembershipChange(List<IPCEndpointAddress> endpointsAdded, List<IPCEndpointAddress> endpointsRemoved);
    public void notifyMembershipChange(IPCEndpointAddress changedEndpoint);
}

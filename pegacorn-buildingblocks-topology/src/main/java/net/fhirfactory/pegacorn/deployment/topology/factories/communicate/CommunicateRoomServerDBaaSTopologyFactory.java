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
package net.fhirfactory.pegacorn.deployment.topology.factories.communicate;

import net.fhirfactory.pegacorn.core.model.componentid.TopologyNodeRDN;
import net.fhirfactory.pegacorn.core.model.componentid.TopologyNodeTypeEnum;
import net.fhirfactory.pegacorn.deployment.names.functionality.subsystem.CommunicateRoomServerComponentNames;
import net.fhirfactory.pegacorn.deployment.properties.configurationfilebased.common.archetypes.DBaaSSubSystemPropertyFile;
import net.fhirfactory.pegacorn.deployment.topology.factories.archetypes.common.PegacornTopologyFactoryBase;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.edge.StandardEdgeIPCEndpoint;
import net.fhirfactory.pegacorn.core.model.endpoints.PetasosTopologyEndpointTypeEnum;
import net.fhirfactory.pegacorn.core.model.topology.nodes.ProcessingPlantTopologyNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

public abstract class CommunicateRoomServerDBaaSTopologyFactory extends PegacornTopologyFactoryBase {
    private static final Logger LOG = LoggerFactory.getLogger(CommunicateRoomServerDBaaSTopologyFactory.class);

    @Inject
    private CommunicateRoomServerComponentNames roomServerComponentNames;

    @Override
    protected Logger getLogger() {
        return (LOG);
    }

    public void buildProcessingPlantEndpoints(DBaaSSubSystemPropertyFile propertyFile, ProcessingPlantTopologyNode processingPlantNode) {
        LOG.debug(".buildProcessingPlantEndpoints(): Entry");
        StandardEdgeIPCEndpoint roomServerPostgreSQLServer = new StandardEdgeIPCEndpoint();
        String appServicesName = roomServerComponentNames.getEndpointServerName(roomServerComponentNames.getFunctionNameDBaaSPostgreSQL());
        roomServerPostgreSQLServer.setaServer(true);
        roomServerPostgreSQLServer.setEncrypted(propertyFile.getDeploymentMode().isUsingInternalEncryption());
        roomServerPostgreSQLServer.setPortType("SQL");
        roomServerPostgreSQLServer.setEndpointType(PetasosTopologyEndpointTypeEnum.SQL_SERVER);
        TopologyNodeRDN appServerRDN = createNodeRDN(appServicesName, propertyFile.getSubsystemInstant().getProcessingPlantVersion(), TopologyNodeTypeEnum.ENDPOINT);
        roomServerPostgreSQLServer.constructFDN(processingPlantNode.getNodeFDN(),appServerRDN);
        roomServerPostgreSQLServer.constructFunctionFDN(processingPlantNode.getNodeFunctionFDN(),appServerRDN);
        roomServerPostgreSQLServer.setHostDNSName(roomServerComponentNames.getDefaultInterfaceNameForBinding());
        roomServerPostgreSQLServer.setPortValue(propertyFile.getSqlServerPort().getPortValue());
        processingPlantNode.getEndpoints().add(roomServerPostgreSQLServer.getNodeFDN());
        LOG.debug(".buildProcessingPlantEndpoints(): Exit");
    }
}

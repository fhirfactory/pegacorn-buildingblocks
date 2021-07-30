package net.fhirfactory.pegacorn.platform.edge.model.ipc.interfaces.common;

import net.fhirfactory.pegacorn.platform.edge.model.ipc.packets.InterProcessingPlantHandoverPacket;
import net.fhirfactory.pegacorn.platform.edge.model.ipc.packets.InterProcessingPlantHandoverResponsePacket;

public interface IPCMessageSenderInterface {
    public InterProcessingPlantHandoverResponsePacket sendIPCMessagePlease(String targetParticipantServiceName, InterProcessingPlantHandoverPacket handoverPacket);
}

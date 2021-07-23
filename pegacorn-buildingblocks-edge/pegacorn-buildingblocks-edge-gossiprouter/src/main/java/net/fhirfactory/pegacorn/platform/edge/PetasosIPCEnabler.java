package net.fhirfactory.pegacorn.platform.edge;

import net.fhirfactory.pegacorn.platform.edge.messaging.IPCGossipRouter;

public abstract class PetasosIPCEnabler {

    private static IPCGossipRouter ipcRouter = new IPCGossipRouter();

    public static void main(String[] args) {
        ipcRouter.run();
        System.exit(0);
    }
}

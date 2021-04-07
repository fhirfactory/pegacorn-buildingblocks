package net.fhirfactory.pegacorn.internals.directories.api.beans;

import net.fhirfactory.pegacorn.internals.directories.api.beans.common.HandlerBase;
import net.fhirfactory.pegacorn.internals.directories.brokers.GroupDirectoryResourceBroker;
import net.fhirfactory.pegacorn.internals.directories.brokers.MatrixRoomDirectoryResourceBroker;
import net.fhirfactory.pegacorn.internals.directories.brokers.common.ResourceDirectoryBroker;
import net.fhirfactory.pegacorn.internals.directories.model.DirectoryMethodOutcome;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@Dependent
public class MatrixRoomServiceHandler extends HandlerBase {
    private static final Logger LOG = LoggerFactory.getLogger(MatrixRoomServiceHandler.class);

    @Inject
    private MatrixRoomDirectoryResourceBroker matrixRoomDirectoryResourceBroker;

    @Override
    protected Logger getLogger() {
        return (LOG);
    }

    @Override
    protected ResourceDirectoryBroker specifyResourceBroker() {
        return (matrixRoomDirectoryResourceBroker);
    }

    @Override
    protected void printOutcome(DirectoryMethodOutcome outcome) {}
}

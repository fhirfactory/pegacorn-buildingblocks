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
package net.fhirfactory.pegacorn.core.model.ui.brokers;

import net.fhirfactory.pegacorn.core.model.ui.brokers.common.ESRBroker;
import net.fhirfactory.pegacorn.core.model.ui.cache.PatientESRCache;
import net.fhirfactory.pegacorn.core.model.ui.resources.simple.PatientESR;
import net.fhirfactory.pegacorn.core.model.ui.resources.simple.common.ExtremelySimplifiedResource;
import net.fhirfactory.pegacorn.core.model.ui.resources.simple.valuesets.IdentifierESDTTypesEnum;
import net.fhirfactory.pegacorn.core.model.ui.resources.simple.valuesets.IdentifierESDTUseEnum;
import net.fhirfactory.pegacorn.core.model.ui.transactions.ESRMethodOutcome;
import net.fhirfactory.pegacorn.core.model.ui.transactions.exceptions.ResourceInvalidSearchException;
import net.fhirfactory.pegacorn.deployment.communicate.matrix.CommunicateSystemManagedRoomNames;
import net.fhirfactory.pegacorn.core.model.ui.cache.common.PegacornESRCache;
import net.fhirfactory.pegacorn.core.model.ui.resources.simple.PractitionerESR;

import javax.inject.Inject;

public abstract class PatientESRBroker extends ESRBroker {

    @Inject
    private PatientESRCache patientCache;

    @Inject
    private CommunicateSystemManagedRoomNames managedRoomNames;

    @Inject
    private CommunicateRoomESRBroker matrixRoomDirectoryResourceBroker;

    @Override
    protected PegacornESRCache specifyCache(){
        return(patientCache);
    }

    //
    // Primary Key Setting
    //
    @Override
    protected void assignSimplifiedID(ExtremelySimplifiedResource resource) {
        getLogger().debug(".assignPrimaryKey(): Entry, resource --> {}", resource);
        if(resource == null){
            getLogger().debug(".assignPrimaryKey(): Entry, resource is null, exiting");
            return;
        }
        resource.assignSimplifiedID(true, IdentifierESDTTypesEnum.ESR_IDENTIFIER_TYPE_PATIENT_URN.getIdentifierType(), IdentifierESDTUseEnum.OFFICIAL);
    }

    //
    // Create
    //
    public ESRMethodOutcome createPatientDE(PatientESR entry){
        getLogger().debug(".createPatientDE(): Entry");
        ESRMethodOutcome outcome = patientCache.addPatient(entry);
        //createSystemManagedMatrixRooms(entry);
        getLogger().debug(".createPatientDE(): Exit");
        return(outcome);
    }

    //
    // Review
    //

    @Override
    protected void enrichWithDirectoryEntryTypeSpecificInformation(ExtremelySimplifiedResource entry) throws ResourceInvalidSearchException {
        getLogger().debug(".enrichWithDirectoryEntryTypeSpecificInformation(): Entry");
        PatientESR patientESR = (PatientESR) entry;
        getLogger().info(".enrichWithDirectoryEntryTypeSpecificInformation(): Exit");
    }

    //
    // Update
    //

    public ESRMethodOutcome updatePatient(PatientESR entry) throws ResourceInvalidSearchException {
        getLogger().info(".updatePractitioner(): Entry");
        ESRMethodOutcome entryUpdate = updateDirectoryEntry(entry);
        getLogger().info(".updatePractitioner(): Exit");
        return(entryUpdate);
    }

    //
    // Delete
    //
    public ESRMethodOutcome deletePatient(PractitionerESR entry){
        ESRMethodOutcome outcome = deletePatient(entry);
        return(outcome);
    }
}

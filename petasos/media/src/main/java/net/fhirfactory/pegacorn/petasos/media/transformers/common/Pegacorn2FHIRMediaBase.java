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
package net.fhirfactory.pegacorn.petasos.media.transformers.common;

import net.fhirfactory.pegacorn.core.model.componentid.PegacornSystemComponentTypeTypeEnum;
import net.fhirfactory.pegacorn.core.model.componentid.TopologyNodeFDN;
import net.fhirfactory.pegacorn.core.model.componentid.TopologyNodeRDN;
import net.fhirfactory.pegacorn.core.model.dataparcel.DataParcelManifest;
import net.fhirfactory.pegacorn.core.model.dataparcel.DataParcelTypeDescriptor;
import net.fhirfactory.pegacorn.core.model.petasos.task.PetasosFulfillmentTask;
import net.fhirfactory.pegacorn.core.model.petasos.uow.UoW;
import net.fhirfactory.pegacorn.core.model.topology.nodes.DefaultWorkshopSetEnum;
import org.hl7.fhir.r4.model.Media;
import org.hl7.fhir.r4.model.Period;

import java.util.Date;

public abstract class Pegacorn2FHIRMediaBase {

    protected String stripEscapeCharacters(String incomingString){
        String outgoingString0 = incomingString.replaceAll("\\\\", "");
        String outgoingString1 = outgoingString0.replaceAll("\\\"","\"");
        return(outgoingString1);
    }

    protected Period extractProcessingPeriod(PetasosFulfillmentTask fulfillmentTask){
        if(fulfillmentTask == null){
            return(null);
        }
        Date startDate = null;
        Date endDate = null;
        if(fulfillmentTask.hasTaskFulfillment()) {
            if (fulfillmentTask.getTaskFulfillment().hasStartDate()) {
                startDate = fulfillmentTask.getTaskFulfillment().getStartDate();
            }
            if (fulfillmentTask.getTaskFulfillment().hasFinishedDate()) {
                endDate = fulfillmentTask.getTaskFulfillment().getFinishedDate();
            }
            if (fulfillmentTask.getTaskFulfillment().hasFinalisationDate()) {
                endDate = fulfillmentTask.getTaskFulfillment().getFinalisationDate();
            }
            if (fulfillmentTask.getTaskFulfillment().hasCancellationDate()) {
                if (endDate == null) {
                    endDate = fulfillmentTask.getTaskFulfillment().getCancellationDate();
                }
            }
        }
        if(startDate == null){
            startDate = endDate;
        }
        Period period = new Period();
        if(startDate != null) {
            period.setStart(startDate);
        }
        if(endDate != null) {
            period.setEnd(endDate);
        }
        return(period);
    }



    protected String extractBestDescriptorValue(DataParcelTypeDescriptor descriptor){
        if(descriptor == null){
            return(null);
        }
        String value = new String();
        if(descriptor.hasDataParcelResource()){
            value = descriptor.getDataParcelResource();
        }
        if(descriptor.hasDataParcelSubCategory()){
            value = descriptor.getDataParcelSubCategory() + "." + value;
        }
        if(descriptor.hasDataParcelCategory()){
            value = descriptor.getDataParcelCategory() + "." + value;
        }
        if(descriptor.hasDataParcelDefiner()){
            value = descriptor.getDataParcelDefiner() + "." + value;
        }
        if(descriptor.hasVersion()){
            value = value + "(" + descriptor.getVersion() + ")";
        }
        return(value);
    }

    protected String extractNiceNodeName(PetasosFulfillmentTask fulfillmentTask){
        if(fulfillmentTask == null){
            return(null);
        }
        TopologyNodeFDN wupFDN = fulfillmentTask.getTaskFulfillment().getFulfillerWorkUnitProcessor().getComponentFDN();
        TopologyNodeRDN processingPlantRDN = wupFDN.extractRDNForNodeType(PegacornSystemComponentTypeTypeEnum.PROCESSING_PLANT);
        TopologyNodeRDN workshopRDN = wupFDN.extractRDNForNodeType(PegacornSystemComponentTypeTypeEnum.WORKSHOP);
        TopologyNodeRDN wupRDN = wupFDN.extractRDNForNodeType(PegacornSystemComponentTypeTypeEnum.WUP);

        String name = new String();
        if(processingPlantRDN != null){
            name = processingPlantRDN.getNodeName() + ".";
        }
        if(workshopRDN != null){
            name = name + workshopRDN.getNodeName() + ".";
        }
        if(wupRDN != null){
            name = name + wupRDN.getNodeName();
        }
        return(name);
    }

}

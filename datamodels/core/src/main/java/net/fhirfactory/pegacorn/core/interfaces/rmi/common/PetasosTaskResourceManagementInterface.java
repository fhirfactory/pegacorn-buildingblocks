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
package net.fhirfactory.pegacorn.core.interfaces.rmi.common;

import net.fhirfactory.pegacorn.core.interfaces.rmi.common.base.PetasosFHIRManagementInterface;
import net.fhirfactory.pegacorn.core.interfaces.rmi.valuesets.PetasosResourceSearchQualifierEnum;
import org.hl7.fhir.r4.model.CodeType;
import org.hl7.fhir.r4.model.HumanName;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Task;

import java.util.List;

public interface PetasosTaskResourceManagementInterface extends PetasosFHIRManagementInterface {
    public List<Task> findTaskByAgent(String agentName, PetasosResourceSearchQualifierEnum agentNameQualifier);
    public List<Task> findTaskByLocation(String locationName, PetasosResourceSearchQualifierEnum locationNameQualifier);
    public List<Task> findTaskByStatus(CodeType statusCode);
    public List<Task> findTaskByTriggerAndLocation(String triggerType, String triggerValue, String locationName, PetasosResourceSearchQualifierEnum locationNameQualifier);
    public List<Task> findTaskByBeneficiary(HumanName patientName);
    public List<Task> findTaskByTriggerAndLocationAndBeneficiary(String triggerType, String triggerValue, String locationName, PetasosResourceSearchQualifierEnum locationNameQualifier, HumanName patientName);
    public List<Task> findTaskByAggregateTask(Identifier aggregateTaskIdentifier);
}

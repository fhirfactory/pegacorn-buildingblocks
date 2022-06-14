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
package net.fhirfactory.pegacorn.core.model.topology.role;

public enum ProcessingPlantRoleEnum {
    PETASOS_SERVICE_PROVIDER_ITOPS_MANAGEMENT("Petasos.ServiceProvider.ITOps"),
    PETASOS_SERVICE_PROVIDER_TASK_MANAGEMENT("Petasos.ServiceProvider.Task-Management"),
    PETASOS_SERVICE_PROVIDER_AUDIT_MANAGEMENT("Petasos.ServiceProvider.Audit-Management"),
    PETASOS_SERVICE_PROVIDER_AUDIT_STORAGE("Petasos.ServiceProvider.Audit-Storage"),
    PETASOS_SERVICE_PROVIDER_MEDIA_MANAGEMENT("Petasos.ServiceProvider.Media-Management"),    
    PETASOS_SERVICE_PROVIDER_MEDIA_STORAGE("Petasos.ServiceProvider.Media-Storage"),
    PETASOS_SERVICE_PROVIDER_SOT_PATIENT_QUERY("Petasos.ServiceProvider.Patient-SoT-Query"),
    PETASOS_SERVICE_PROVIDER_GATEWAY_EMAIL("Petasos.ServiceProvider.Email-Gateway"),
    PETASOS_SERVICE_PROVIDER_GATEWAY_SMS("Petasos.ServiceProvider.SMS-Gateway"),
    PETASOS_SERVICE_PROVIDER_FHIR_DATAGRID("Petasos.ServiceProvider.FHIR-Datagrid"),
    PETASOS_SERVICE_PROVIDER_MITAF_GENERAL("Petasos.ServiceProvider.MITaF-General"),
    PETASOS_SERVICE_PROVIDER_FHIRBREAK_GENERAL("Petasos.ServiceProvider.FHIRBreak-General");

    private String token;

    private ProcessingPlantRoleEnum(String token){
        this.token = token;
    }

    public String getToken(){
        return(this.token);
    }
}

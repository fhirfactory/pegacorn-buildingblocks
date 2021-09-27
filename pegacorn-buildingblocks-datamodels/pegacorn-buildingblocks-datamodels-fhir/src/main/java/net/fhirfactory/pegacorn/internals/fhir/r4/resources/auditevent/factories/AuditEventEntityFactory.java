/*
 * Copyright (c) 2021 Mark A. Hunter
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
package net.fhirfactory.pegacorn.internals.fhir.r4.resources.auditevent.factories;

import net.fhirfactory.pegacorn.internals.fhir.r4.resources.auditevent.valuesets.AuditEventEntityLifecycleEnum;
import net.fhirfactory.pegacorn.internals.fhir.r4.resources.auditevent.valuesets.AuditEventEntityRoleEnum;
import net.fhirfactory.pegacorn.internals.fhir.r4.resources.auditevent.valuesets.AuditEventEntityTypeEnum;
import org.hl7.fhir.r4.model.AuditEvent;
import org.hl7.fhir.r4.model.StringType;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class AuditEventEntityFactory {

    public AuditEvent.AuditEventEntityComponent newAuditEventEntity(AuditEventEntityTypeEnum entityType,
                                                                    AuditEventEntityRoleEnum entityRole,
                                                                    AuditEventEntityLifecycleEnum entityLifecycle,
                                                                    String name,
                                                                    String description,
                                                                    String detailName,
                                                                    String detailValueString){

        AuditEvent.AuditEventEntityComponent entity = new AuditEvent.AuditEventEntityComponent();

        entity.setType(entityType.getEntityTypeCoding());
        entity.setRole(entityRole.getEntityRoleCoding());
        entity.setLifecycle(entityLifecycle.getEntityLifecycleCoding());
        entity.setName(name);
        entity.setDescription(description);

        AuditEvent.AuditEventEntityDetailComponent detail = newAuditEventEntityDetailComponent(detailName, detailValueString);
        entity.addDetail(detail);

        return(entity);
    }

    public AuditEvent.AuditEventEntityComponent newAuditEventEntity(AuditEventEntityTypeEnum entityType,
                                                                    AuditEventEntityRoleEnum entityRole,
                                                                    AuditEventEntityLifecycleEnum entityLifecycle,
                                                                    String name,
                                                                    String description,
                                                                    List<AuditEvent.AuditEventEntityDetailComponent> detailSet){

        AuditEvent.AuditEventEntityComponent entity = new AuditEvent.AuditEventEntityComponent();

        entity.setType(entityType.getEntityTypeCoding());
        entity.setRole(entityRole.getEntityRoleCoding());
        entity.setLifecycle(entityLifecycle.getEntityLifecycleCoding());
        entity.setName(name);
        entity.setDescription(description);

        entity.getDetail().addAll(detailSet);

        return(entity);
    }

    public AuditEvent.AuditEventEntityDetailComponent newAuditEventEntityDetailComponent(String name, String value){
        AuditEvent.AuditEventEntityDetailComponent detail = new AuditEvent.AuditEventEntityDetailComponent();
        detail.setType(name);
        StringType detailValueStringType = new StringType(value);
        detail.setValue(detailValueStringType);
        return(detail);
    }
}

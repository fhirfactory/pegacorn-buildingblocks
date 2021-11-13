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
package net.fhirfactory.pegacorn.internals.fhir.r4.resources.task.factories;

import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.identity.datatypes.TaskIdType;
import net.fhirfactory.pegacorn.internals.fhir.r4.codesystems.PegacornIdentifierCodeEnum;
import net.fhirfactory.pegacorn.internals.fhir.r4.resources.identifier.PegacornIdentifierFactory;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Period;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Instant;
import java.util.Date;

@ApplicationScoped
public class TaskIdentifierFactory {
    private static final Logger LOG = LoggerFactory.getLogger(TaskIdentifierFactory.class);

    @Inject
    private PegacornIdentifierFactory generalIdentifierFactory;

    //
    // Constructor(s)
    //

    //
    // Business Methods
    //

    public Identifier newTaskIdentifier(TaskIdType taskId){
        getLogger().debug(".newTaskIdentifier(): Entry, taskId->{}", taskId);
        Identifier fhirIdentifier = new Identifier();
        Period identifierPeriod = new Period();
        identifierPeriod.setStart(Date.from(taskId.getCreationInstant()));
        fhirIdentifier = generalIdentifierFactory.newIdentifier(PegacornIdentifierCodeEnum.IDENTIFIER_CODE_ACTIONABLE_TASK, taskId.getId(), identifierPeriod);
        getLogger().debug(".newTaskIdentifier(): Exit, fhirIdentifier->{}", fhirIdentifier);
        return(fhirIdentifier);
    }

    public Identifier newTaskIdentifier(TaskIdType taskId, Instant endInstant){
        getLogger().debug(".newTaskIdentifier(): Entry, taskId->{}", taskId);
        Identifier fhirIdentifier = new Identifier();
        Period identifierPeriod = new Period();
        identifierPeriod.setStart(Date.from(taskId.getCreationInstant()));
        identifierPeriod.setEnd(Date.from(endInstant));
        fhirIdentifier = generalIdentifierFactory.newIdentifier(PegacornIdentifierCodeEnum.IDENTIFIER_CODE_ACTIONABLE_TASK, taskId.getId(), identifierPeriod);
        getLogger().debug(".newTaskIdentifier(): Exit, fhirIdentifier->{}", fhirIdentifier);
        return(fhirIdentifier);
    }

    //
    // Getters (and Setters)
    //

    protected Logger getLogger(){
        return(LOG);
    }
}

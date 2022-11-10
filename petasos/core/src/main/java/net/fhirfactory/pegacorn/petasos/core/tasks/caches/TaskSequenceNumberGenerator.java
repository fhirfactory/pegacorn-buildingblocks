/*
 * Copyright (c) 2022 Mark A. Hunter
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
package net.fhirfactory.pegacorn.petasos.core.tasks.caches;

import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.identity.datatypes.TaskSequenceNumber;

import javax.enterprise.context.ApplicationScoped;
import java.time.Instant;

@ApplicationScoped
public class TaskSequenceNumberGenerator {
    private Long lastSequenceNumberGenerationSeconds;
    private Long lastMinorNumber;

    //
    // Constructor(s)
    //

    public TaskSequenceNumberGenerator(){
        lastSequenceNumberGenerationSeconds = Instant.now().getEpochSecond();
        lastMinorNumber = 0L;
    }

    //
    // Methods
    //

    public TaskSequenceNumber generateNewSequenceNumber(){
        Long currentSeconds = Instant.now().getEpochSecond();
        if(currentSeconds > lastSequenceNumberGenerationSeconds){
            lastMinorNumber = 0L;
            lastSequenceNumberGenerationSeconds = currentSeconds;
        }
        lastMinorNumber += 1;
        TaskSequenceNumber newSequenceNumber = new TaskSequenceNumber(lastSequenceNumberGenerationSeconds, lastMinorNumber);
        return(newSequenceNumber);
    }
}

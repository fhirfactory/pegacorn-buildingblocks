/*
 * Copyright (c) 2020 Mark A. Hunter (ACT Health)
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
package net.fhirfactory.pegacorn.platform.edge.model.common;

import ca.uhn.fhir.rest.api.MethodOutcome;
import net.fhirfactory.pegacorn.components.transaction.model.TransactionMethodOutcome;
import org.hl7.fhir.r4.model.*;

import java.io.Serializable;
import java.util.Map;

public interface ResourceAccessorInterfaceBase {
    public TransactionMethodOutcome getResource(IdType id);
    public TransactionMethodOutcome createResource(Resource newResource);
    public TransactionMethodOutcome updateResource(Resource resourceToUpdate);
    public TransactionMethodOutcome deleteResource(Resource resourceToRemove);
    public boolean supportsSearch(String searchName, Map<Property, Serializable> parameterSet);
    public TransactionMethodOutcome searchUsingCriteria(ResourceType resourceType, String searchName, Map<Property, Serializable> parameterSet);
    public TransactionMethodOutcome searchUsingIdentifier(Identifier identifier);
    public Identifier getBestIdentifier(MethodOutcome outcome);
    public void initialiseServices();
}

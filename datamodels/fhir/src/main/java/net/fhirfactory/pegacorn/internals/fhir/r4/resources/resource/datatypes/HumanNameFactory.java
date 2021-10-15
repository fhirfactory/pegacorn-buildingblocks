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
package net.fhirfactory.pegacorn.internals.fhir.r4.resources.resource.datatypes;

import org.hl7.fhir.r4.model.HumanName;
import org.hl7.fhir.r4.model.Period;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.time.Instant;
import java.util.Date;

@ApplicationScoped
public class HumanNameFactory {
    private static final Logger LOG = LoggerFactory.getLogger(HumanNameFactory.class);

    public HumanName constructHumanName(String firstName, String lastName, String middleName, String prefix, String suffix, HumanName.NameUse nameUse){
        LOG.debug(".constructHumanName(): Entry, firstName --> {}, middleName --> {}, lastName --> {}, prefix --> {}, suffix --> {}, nameUse --> {}", firstName, lastName, middleName, prefix, suffix, nameUse);
        HumanName name = new HumanName();
        name.addGiven(firstName);
        name.addPrefix(prefix);
        name.addSuffix(suffix);
        name.addGiven(middleName);
        name.setFamily(lastName);
        name.setUse(nameUse);
        Period namePeriod = new Period();
        namePeriod.setStart(Date.from(Instant.now()));
        name.setPeriod(namePeriod);
        StringBuilder nameText = new StringBuilder();
        nameText.append(firstName);
        nameText.append(" ");
        if(middleName != null){
            nameText.append(middleName);
            nameText.append(" ");
        }
        nameText.append(lastName);
        name.setText(nameText.toString());
        LOG.debug(".constructHumanName(): Exit, HumanName --> {}", name);
        return(name);
    }

    public HumanName constructHumanName(String nameText, HumanName.NameUse nameUse){
        LOG.debug(".constructHumanName(): Entry, nameText --> {}, nameUse --> {}", nameText, nameUse);
        HumanName name = new HumanName();
        name.setUse(nameUse);
        Period namePeriod = new Period();
        namePeriod.setStart(Date.from(Instant.now()));
        name.setPeriod(namePeriod);
        name.setText(nameText);
        LOG.debug(".constructHumanName(): Exit, HumanName --> {}", name);
        return(name);
    }
}

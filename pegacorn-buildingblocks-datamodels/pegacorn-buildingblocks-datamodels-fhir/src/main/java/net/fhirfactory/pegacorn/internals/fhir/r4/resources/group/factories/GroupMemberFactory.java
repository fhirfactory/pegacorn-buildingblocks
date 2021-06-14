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
package net.fhirfactory.pegacorn.internals.fhir.r4.resources.group.factories;

import net.fhirfactory.pegacorn.common.model.dates.EffectivePeriod;
import net.fhirfactory.pegacorn.internals.fhir.r4.resources.group.extensions.GroupMemberTypeExtensionEnricher;
import net.fhirfactory.pegacorn.internals.fhir.r4.resources.group.valuesets.GroupMemberContextualTypeExtensionEnum;
import org.hl7.fhir.r4.model.Group;
import org.hl7.fhir.r4.model.Period;
import org.hl7.fhir.r4.model.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.sql.Date;
import java.time.Instant;

@ApplicationScoped
public class GroupMemberFactory {
    private static final Logger LOG = LoggerFactory.getLogger(GroupMemberFactory.class);

    @Inject
    private GroupMemberTypeExtensionEnricher memberTypeExtensionEnricher;

    public Group.GroupMemberComponent newGroupMember(Reference memberReference, Period period, boolean isInActive, GroupMemberContextualTypeExtensionEnum memberType){
        LOG.debug(".newGroupMember(): Entry, memberReference->{}, period->{}, isInActive->{}, memberType->{}", memberReference, period, isInActive, memberType);
        Group.GroupMemberComponent groupMember = newGroupMember(memberReference, period, isInActive );
        LOG.debug(".newGroupMember(): Exit, groupMember->{}", groupMember);
        return(groupMember);
    }

    public Group.GroupMemberComponent newGroupMember(Reference memberReference, Period period, GroupMemberContextualTypeExtensionEnum memberType){
        LOG.debug(".newGroupMember(): Entry, memberReference->{}, period->{}, memberType->{}", memberReference, period, memberType);
        boolean isInActive = isInActiveBasedOnPeriod(period);
        Group.GroupMemberComponent groupMember = newGroupMember(memberReference, period, isInActive );
        memberTypeExtensionEnricher.injectMemberType(groupMember, memberType);
        LOG.debug(".newGroupMember(): Exit, groupMember->{}", groupMember);
        return(groupMember);
    }

    public Group.GroupMemberComponent newGroupMember(Reference memberReference, Period period, boolean isInActive){
        LOG.debug(".newGroupMember(): Entry, memberReference->{}, period->{}, isInActive->{}", memberReference, period, isInActive);
        Group.GroupMemberComponent groupMember = new Group.GroupMemberComponent();
        groupMember.setEntity(memberReference);
        groupMember.setPeriod(period);
        groupMember.setInactive(isInActive);
        LOG.debug(".newGroupMember(): Exit, groupMember->{}", groupMember);
        return(groupMember);
    }

    public Group.GroupMemberComponent newGroupMember(Reference memberReference, EffectivePeriod effectivePeriod){
        LOG.debug(".newGroupMember(): Entry, memberReference->{}, effectivePeriod->{}", memberReference, effectivePeriod);
        Period fhirPeriod = effectivePeriod.getPeriod();
        Boolean isInActive = !(effectivePeriod.isEffectiveNow());
        Group.GroupMemberComponent groupMember = newGroupMember(memberReference, fhirPeriod, isInActive );
        LOG.debug(".newGroupMember(): Exit, groupMember->{}", groupMember);
        return(groupMember);
    }


    private boolean isInActiveBasedOnPeriod(Period period){
        LOG.debug(".isInActiveBasedOnPeriod(): Entry, period->{}", period);
        boolean isAfterStart = true;
        boolean isBeforeEnd = true;
        if(period.hasStart()){
            if(!(Date.from(Instant.now()).after(period.getStart()))){
                isAfterStart = false;
            }
        }
        if(period.hasEnd()){
            if(!(Date.from(Instant.now()).before(period.getEnd()))){
                isBeforeEnd = false;
            }
        }
        if(isBeforeEnd && isAfterStart){
            LOG.debug(".isInActiveBasedOnPeriod(): Exit, returning -false-");
            return(false);
        } else {
            LOG.debug(".isInActiveBasedOnPeriod(): Exit, returning -true-");
            return(true);
        }
    }
}

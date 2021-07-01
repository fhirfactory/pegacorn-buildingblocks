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
package net.fhirfactory.buildingblocks.esr.models.resources.group;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fhirfactory.buildingblocks.esr.resources.datatypes.ParticipantESDT;

/**
 * A group of practitioner roles within a care team.
 * 
 * @author Brendan Douglas
 *
 */
public class PractitionerRolesInCareTeamGroupESR extends GroupESR {
    private static final Logger LOG = LoggerFactory.getLogger(PractitionerRolesInCareTeamGroupESR.class);
    
    private List<ParticipantESDT>groupMembership;
    
    public PractitionerRolesInCareTeamGroupESR() {
    	super();
    	
    	this.groupMembership = new ArrayList<>();
    }

    @Override
    protected Logger getLogger() {
    	return(LOG);
    }

    
    public List<ParticipantESDT>getGroupMembership() {
    	return groupMembership;
    }
    
    
    public void setGroupMembership(List<ParticipantESDT>groupMembership) {
    	this.groupMembership = groupMembership;
    }

    
    public void addNewGroupMember(ParticipantESDT newGroupMember) {
    	if (!groupMembership.contains(newGroupMember)) {
    		this.groupMembership.add(newGroupMember);
    	}
	}
}

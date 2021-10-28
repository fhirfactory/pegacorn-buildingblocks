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
package net.fhirfactory.pegacorn.internals.communicate.entities.rooms.datatypes;

import net.fhirfactory.pegacorn.common.model.dates.EffectivePeriod;
import net.fhirfactory.pegacorn.internals.communicate.entities.user.CommunicateUser;
import net.fhirfactory.pegacorn.internals.fhir.r4.resources.group.valuesets.GroupMemberContextualTypeExtensionEnum;

public class CommunicateRoomMember {
    private CommunicateUser member;
    private GroupMemberContextualTypeExtensionEnum memberType;
    private Integer memberPowerLevel;
    private EffectivePeriod activePeriod;

    public CommunicateRoomMember(){
        this.member = null;
        this.memberType = GroupMemberContextualTypeExtensionEnum.CONTEXTUAL_USER_TYPE_GENERAL;
        this.activePeriod = new EffectivePeriod();
        this.memberPowerLevel = null;
    }

    public CommunicateUser getMember() {
        return member;
    }

    public void setMember(CommunicateUser member) {
        this.member = member;
    }

    public GroupMemberContextualTypeExtensionEnum getMemberType() {
        return memberType;
    }

    public void setMemberType(GroupMemberContextualTypeExtensionEnum memberType) {
        this.memberType = memberType;
    }

    public Integer getMemberPowerLevel() {
        return memberPowerLevel;
    }

    public void setMemberPowerLevel(Integer memberPowerLevel) {
        this.memberPowerLevel = memberPowerLevel;
    }

    public EffectivePeriod getActivePeriod() {
        return activePeriod;
    }

    public void setActivePeriod(EffectivePeriod activePeriod) {
        this.activePeriod = activePeriod;
    }


}

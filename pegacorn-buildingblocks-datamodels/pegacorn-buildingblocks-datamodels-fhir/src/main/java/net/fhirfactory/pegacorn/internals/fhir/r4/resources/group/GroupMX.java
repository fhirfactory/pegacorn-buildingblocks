/*
 * The MIT License
 *
 * Copyright 2020 Mark A. Hunter (ACT Health).
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package net.fhirfactory.pegacorn.internals.fhir.r4.resources.group;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.api.annotation.ResourceDef;
import ca.uhn.fhir.parser.IParser;
import java.util.Iterator;

import net.fhirfactory.pegacorn.internals.fhir.r4.resources.group.matrix.GroupExtensionMeanings;
import net.fhirfactory.pegacorn.internals.fhir.r4.resources.group.matrix.GroupExtensionSetException;
import net.fhirfactory.pegacorn.internals.fhir.r4.resources.group.matrix.GroupJoinRuleStatusEnum;
import org.hl7.fhir.r4.model.BooleanType;
import org.hl7.fhir.r4.model.DomainResource;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.Group;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.IntegerType;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.StringType;
import org.hl7.fhir.r4.model.UriType;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author ACT Health
 */
@ResourceDef(name = "GroupMX", profile = "http://hl7.org/fhir/profiles/custom-resource")
public class GroupMX extends Group
{

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory.getLogger(GroupMX.class);
    private static final GroupExtensionMeanings pegacornGroupExtensionMeanings = new GroupExtensionMeanings();

    @Override
    public GroupMX copy()
    {
        GroupMX retVal = new GroupMX();
        super.copyValues(retVal);
        return (retVal);
    }

    public GroupMX()
    {
        super();
        FhirContext ctx = FhirContext.forR4();
        ctx.registerCustomType(GroupMX.class);
    }

    // Group Federation Accessor Methods
    public boolean hasFederationStatus()
    {
        LOG.debug("hasFederationStatus(): Entry, checking Group Resource for Federation Status extension");
        if (this.hasExtension(pegacornGroupExtensionMeanings.getGroupFederationStatusExtensionMeaning())) {
            LOG.debug("hasFederationStatus(): Exit, has the -federation_status- extension");
            return (true);
        }
        LOG.debug("hasFederationStatus(): Exit, does not have the -federation_status- extension");
        return (false);
    }

    public boolean getFederationStatus()
            throws GroupExtensionSetException
    {
        LOG.debug("getFederationStatus(): Entry, getting Predesessor Group");
        if (!hasFederationStatus()) {
            throw (new GroupExtensionSetException("getFederationStatus(): There is no Federation Status Extension"));
        }
        LOG.trace("getFederationStatus(): Extracting the appropriate Extension");
        Extension groupExtension = this.getExtensionByUrl(pegacornGroupExtensionMeanings.getGroupFederationStatusExtensionMeaning());
        LOG.trace("getFederationStatus(): Check the Value, ensure it is the appropriate Type (BooleanType)");
        if (!(groupExtension.getValue() instanceof BooleanType)) {
            throw (new GroupExtensionSetException("getFederationStatus(): Group contains the wrong extension value type (not BooleanType)"));
        }
        LOG.trace("getFederationStatus(): Extract the Value from the Exension");
        BooleanType extractedFederationStatus = (BooleanType) (groupExtension.getValue());
        LOG.debug("getFederationStatus(): Exit, returning the Predecessor Group --> {}", extractedFederationStatus);
        return (extractedFederationStatus.booleanValue());
    }

    public void setFederationStatus(Boolean federationStatus)
    {
        LOG.debug("setFederationStatus(): Entry, setting Federation Status to --> {}", federationStatus);
        if (this.hasExtension(pegacornGroupExtensionMeanings.getGroupFederationStatusExtensionMeaning())) {
            LOG.trace("setFederationStatus(): removing existing Extension");
            this.removeExtension(pegacornGroupExtensionMeanings.getGroupFederationStatusExtensionMeaning());
        }
        LOG.trace("setFederationStatus(): Creating new Federation Status Extension");
        Extension newFederationStatusExtension = new Extension();
        newFederationStatusExtension.setUrl(pegacornGroupExtensionMeanings.getGroupFederationStatusExtensionMeaning());
        newFederationStatusExtension.setValue(new BooleanType(federationStatus));
        LOG.trace("setFederationStatus(): Injecting the Extension into Group");
        this.addExtension(newFederationStatusExtension);
        LOG.debug("setFederationStatus(): Exit, added new Federation Status Extension --> {}", newFederationStatusExtension);
    }

    // Predecessor Room Accessor Methods
    public boolean hasPredecessorGroup()
    {
        LOG.debug("hasPredecessorGroup(): Entry, checking Group Resource for Predecessor Group extension");
        if (this.hasExtension(pegacornGroupExtensionMeanings.getGroupPredecessorExtensionMeaning())) {
            LOG.debug("hasPredecessorGroup(): Exit, has the -group_predecssor_group- extension");
            return (true);
        }
        LOG.debug("hasPredecessorGroup(): Exit, does not have the -group_predecssor_group- extension");
        return (false);
    }

    public Reference getPredecessorGroup()
            throws GroupExtensionSetException
    {
        LOG.debug("hasPredecessorGroup(): Entry, getting Predesessor Group");
        if (!hasPredecessorGroup()) {
            throw (new GroupExtensionSetException("getPredecessorGroup(): There is no Federation Status Extension"));
        }
        LOG.trace("getPredecessorGroup(): Extracting the appropriate Extension");
        Extension groupExtension = this.getExtensionByUrl(pegacornGroupExtensionMeanings.getGroupPredecessorExtensionMeaning());
        LOG.trace("getPredecessorGroup(): Check the Value, ensure it is the appropriate Type (Reference)");
        if (!(groupExtension.getValue() instanceof Reference)) {
            throw (new GroupExtensionSetException("getPredecessorGroup(): Group contains the wrong extension value type (not Reference)"));
        }
        LOG.trace("getPredecessorGroup(): Extract the Value from the Exension");
        Reference extractedPredecessorGroup = (Reference) (groupExtension.getValue());
        LOG.debug("getPredecessorGroup(): Exit, returning the Predecessor Group --> {}", extractedPredecessorGroup);
        return (extractedPredecessorGroup);
    }

    public void setPredecessorGroup(Reference previousGroup)
    {
        LOG.debug("setPredecessorGroup(): Entry, setting Previous Group to --> {}", previousGroup);
        if (this.hasExtension(pegacornGroupExtensionMeanings.getGroupPredecessorExtensionMeaning())) {
            LOG.trace("setJoinRule(): removing existing Extension");
            this.removeExtension(pegacornGroupExtensionMeanings.getGroupPredecessorExtensionMeaning());
        }
        LOG.trace("setPredecessorGroup(): Creating new Predecessor Group Extension");
        Extension newPreviousGroupExtension = new Extension();
        newPreviousGroupExtension.setUrl(pegacornGroupExtensionMeanings.getGroupPredecessorExtensionMeaning());
        newPreviousGroupExtension.setValue(previousGroup);
        LOG.trace("setPredecessorGroup(): Injecting the Extension into Group");
        this.addExtension(newPreviousGroupExtension);
        LOG.debug("setPredecessorGroup(): Exit, added new Predecessor Group --> {}", newPreviousGroupExtension);
    }

    // Join Rule Accessor Methods
    public boolean hasJoinRule()
    {
        LOG.debug("hasJoinRule(): Entry, checking groupResource for JoinRule extension");
        if (this.hasExtension(pegacornGroupExtensionMeanings.getJoinRuleExtensionMeaning())) {
            LOG.debug("hasJoinRule(): Exit, has the -join_rule- extension");
            return (true);
        }
        LOG.debug("hasJoinRule(): Exit, does not have the -join_rule- extension");
        return (false);
    }

    public GroupJoinRuleStatusEnum getJoinRule()
            throws GroupExtensionSetException
    {
        LOG.debug("getJoinRule(): Entry, getting GroupPriority");
        if (!hasJoinRule()) {
            throw (new GroupExtensionSetException("getGroupPriority(): There is no GroupPriority Extension"));
        }
        LOG.trace("getJoinRule(): Extracting the appropriate Extension");
        Extension groupJoinRuleExtension = this.getExtensionByUrl(pegacornGroupExtensionMeanings.getJoinRuleExtensionMeaning());
        LOG.trace("getJoinRule(): Check the Value, ensure it is the appropriate Type (StringType");
        if (!(groupJoinRuleExtension.getValue() instanceof StringType)) {
            throw (new GroupExtensionSetException("getGroupPriority(): Group contains the wrong Group Priority extension value type"));
        }
        LOG.trace("getJoinRule(): Extract the Value from the Exension & convert to plain Integer");
        StringType extractedJoinRuleString = (StringType) (groupJoinRuleExtension.getValue());
        GroupJoinRuleStatusEnum jointRule = GroupJoinRuleStatusEnum.valueOf(extractedJoinRuleString.asStringValue());
        LOG.debug("getJoinRule(): Exit, returning the Group Priority --> {}", jointRule);
        return (jointRule);
    }

    public void setJoinRule(GroupJoinRuleStatusEnum newJoinRule)
    {
        LOG.debug("setJoinRule(): Entry, setting GroupPriority to --> {}", newJoinRule);
        if (this.hasExtension(pegacornGroupExtensionMeanings.getJoinRuleExtensionMeaning())) {
            LOG.trace("setJoinRule(): removing existing Extension");
            this.removeExtension(pegacornGroupExtensionMeanings.getJoinRuleExtensionMeaning());
        }
        LOG.trace("setJoinRule(): Creating new GroupPriority Extension");
        Extension newJoinRuleExtension = new Extension();
        newJoinRuleExtension.setUrl(pegacornGroupExtensionMeanings.getJoinRuleExtensionMeaning());
        newJoinRuleExtension.setValue(new StringType(newJoinRule.getJoinRuleStatus()));
        LOG.trace("setJoinRule(): Injecting the Extension into Group");
        this.addExtension(newJoinRuleExtension);
        LOG.debug("setJoinRule(): Exit, added new Group Priority --> {}", newJoinRuleExtension);
    }

    // Group Priority Accessor Methods
    public boolean hasGroupPriority()
    {
        LOG.debug("hasGroupPriority(): Entry, checking groupResource for GroupPriority extension");
        if (this.hasExtension(pegacornGroupExtensionMeanings.getGroupPriorityExtensionMeaning())) {
            LOG.debug("hasGroupPriorty(): Exit, has the -group_priority- extension");
            return (true);
        }
        LOG.debug("hasGroupPriority(): Exit, does not have the -group_priority- extension");
        return (false);
    }

    public Integer getGroupPriority()
            throws GroupExtensionSetException
    {
        LOG.debug("getGroupPriority(): Entry, getting GroupPriority");
        if (!hasGroupPriority()) {
            throw (new GroupExtensionSetException("getGroupPriority(): There is no GroupPriority Extension"));
        }
        LOG.trace("getGroupPriority(): Extracting the appropriate Extension");
        Extension groupPriorityExtensionSet = this.getExtensionByUrl(pegacornGroupExtensionMeanings.getGroupPriorityExtensionMeaning());
        LOG.trace("getGroupPriority(): Check the Value, ensure it is the appropriate Type (IntegerType");
        if (!(groupPriorityExtensionSet.getValue() instanceof IntegerType)) {
            throw (new GroupExtensionSetException("getGroupPriority(): Group contains the wrong Group Priority extension value type"));
        }
        LOG.trace("getGroupPriority(): Extract the Value from the Exension & convert to plain Integer");
        IntegerType extractedPriorityIntegerType = (IntegerType) (groupPriorityExtensionSet.getValue());
        Integer groupPriority = extractedPriorityIntegerType.getValue();
        LOG.debug("getGroupPriority(): Exit, returning the Group Priority --> {}", groupPriority);
        return (groupPriority);
    }

    public void setGroupPriority(Integer newPriority)
    {
        LOG.debug("setGroupPriority(): Entry, setting GroupPriority to --> {}", newPriority);
        if (this.hasExtension(pegacornGroupExtensionMeanings.getGroupPriorityExtensionMeaning())) {
            LOG.trace("setGroupPriority(): removing existing Extension");
            this.removeExtension(pegacornGroupExtensionMeanings.getGroupPriorityExtensionMeaning());
        }
        LOG.trace("setGroupPriority(): Creating new GroupPriority Extension");
        Extension newGroupPriorityExtension = new Extension();
        newGroupPriorityExtension.setUrl(pegacornGroupExtensionMeanings.getGroupPriorityExtensionMeaning());
        newGroupPriorityExtension.setValue(new IntegerType(newPriority));
        LOG.trace("setGroupPriority(): Injecting the Extension into Group");
        this.addExtension(newGroupPriorityExtension);
        LOG.debug("setGroupPriority(): Exit, added new Group Priority --> {}", newGroupPriorityExtension);
    }

    // Chat Group Version Accessor Methods
    public boolean hasChatGroupVersion()
    {
        LOG.debug("hasChatGroupVersion(): Entry, checking Group Resource for Predecessor Group extension");
        if (this.hasExtension(pegacornGroupExtensionMeanings.getGroupChatGroupVersionExtensionMeaning())) {
            LOG.debug("hasPredecessorGroup(): Exit, has the -group_room_version- extension");
            return (true);
        }
        LOG.debug("hasChatGroupVersion(): Exit, does not have the -group_room_version- extension");
        return (false);
    }

    public Integer getChatGroupVersion()
            throws GroupExtensionSetException
    {
        LOG.debug("getChatGroupVersion(): Entry, getting Predesessor Group");
        if (!hasChatGroupVersion()) {
            throw (new GroupExtensionSetException("getPredecessorGroup(): There is no Group Chat Version Extension"));
        }
        LOG.trace("getChatGroupVersion(): Extracting the appropriate Extension");
        Extension groupExtension = this.getExtensionByUrl(pegacornGroupExtensionMeanings.getGroupChatGroupVersionExtensionMeaning());
        LOG.trace("getChatGroupVersion(): Check the Value, ensure it is the appropriate Type (IntegerType)");
        if (!(groupExtension.getValue() instanceof Reference)) {
            throw (new GroupExtensionSetException("getChatGroupVersion(): Group contains the wrong extension value type (not IntegerType)"));
        }
        LOG.trace("getChatGroupVersion(): Extract the Value from the Exension");
        IntegerType extractedGroupChatVersion = (IntegerType) (groupExtension.getValue());
        LOG.debug("getChatGroupVersion(): Exit, returning the Chat Group Version --> {}", extractedGroupChatVersion);
        return (extractedGroupChatVersion.getValue());
    }

    public void setChatGroupVersion(Integer chatGroupVersion)
    {
        LOG.debug("setChatGroupVersion(): Entry, setting Chat Group Version to --> {}", chatGroupVersion);
        if (this.hasExtension(pegacornGroupExtensionMeanings.getGroupChatGroupVersionExtensionMeaning())) {
            LOG.trace("setChatGroupVersion(): removing existing Extension");
            this.removeExtension(pegacornGroupExtensionMeanings.getGroupChatGroupVersionExtensionMeaning());
        }
        LOG.trace("setChatGroupVersion(): Creating new Chat Group Version Extension");
        Extension newChatGroupVersionExtension = new Extension();
        newChatGroupVersionExtension.setUrl(pegacornGroupExtensionMeanings.getGroupPredecessorExtensionMeaning());
        newChatGroupVersionExtension.setValue(new IntegerType(chatGroupVersion));
        LOG.trace("setChatGroupVersion(): Injecting the Extension into Group");
        this.addExtension(newChatGroupVersionExtension);
        LOG.debug("setChatGroupVersion(): Exit, added new Chat Group Version --> {}", newChatGroupVersionExtension);
    }

    // Chat Group Version Accessor Methods
    public boolean hasPreviousGroupLastMessage()
    {
        LOG.debug("hasPreviousGroupLastMessage(): Entry, checking Group Resource for Predecessor Group Last Message extension");
        if (this.hasExtension(pegacornGroupExtensionMeanings.getGroupPredecessorLastMessageExtensionMeaning())) {
            LOG.debug("hasPreviousGroupLastMessage(): Exit, does  have the -group_predecessor_room_last_message- extension");
            return (true);
        }
        LOG.debug("hasPreviousGroupLastMessage(): Exit, does not have the -group_predecessor_room_last_message- extension");
        return (false);
    }

    public Reference getPreviousGroupLastMessage()
            throws GroupExtensionSetException
    {
        LOG.debug("getPreviousGroupLastMessage(): Entry, getting Predesessor Group Last Message");
        if (!hasPreviousGroupLastMessage()) {
            throw (new GroupExtensionSetException("getPreviousGroupLastMessage(): There is no Previous Group Last Message Extension"));
        }
        LOG.trace("getPreviousGroupLastMessage(): Extracting the appropriate Extension");
        Extension groupExtension = this.getExtensionByUrl(pegacornGroupExtensionMeanings.getGroupPredecessorLastMessageExtensionMeaning());
        LOG.trace("getPreviousGroupLastMessage(): Check the Value, ensure it is the appropriate Type (Reference)");
        if (!(groupExtension.getValue() instanceof Reference)) {
            throw (new GroupExtensionSetException("getChatGroupVersion(): Group contains the wrong extension value type (not Reference)"));
        }
        LOG.trace("getPreviousGroupLastMessage(): Extract the Value from the Exension");
        Reference extractedPreviousGroupLastMessageReference = (Reference) (groupExtension.getValue());
        LOG.debug("getPreviousGroupLastMessage(): Exit, returning the Previous Group Last Message Extension --> {}", extractedPreviousGroupLastMessageReference);
        return (extractedPreviousGroupLastMessageReference);
    }

    public void setPreviousGroupLastMessage(Reference previousGroupLastMessage)
    {
        LOG.debug("setPreviousGroupLastMessage(): Entry, setting Chat Group Version to --> {}", previousGroupLastMessage);
        if (this.hasExtension(pegacornGroupExtensionMeanings.getGroupPredecessorLastMessageExtensionMeaning())) {
            LOG.trace("setPreviousGroupLastMessage(): removing existing Extension");
            this.removeExtension(pegacornGroupExtensionMeanings.getGroupPredecessorLastMessageExtensionMeaning());
        }
        LOG.trace("setPreviousGroupLastMessage(): Creating new Chat Group Version Extension");
        Extension newPreviousGroupLastMessageExtension = new Extension();
        newPreviousGroupLastMessageExtension.setUrl(pegacornGroupExtensionMeanings.getGroupPredecessorLastMessageExtensionMeaning());
        newPreviousGroupLastMessageExtension.setValue(previousGroupLastMessage);
        LOG.trace("setPreviousGroupLastMessage(): Injecting the Extension into Group");
        this.addExtension(newPreviousGroupLastMessageExtension);
        LOG.debug("setPreviousGroupLastMessage(): Exit, added new Previous Group Last Message Extension --> {}", newPreviousGroupLastMessageExtension);
    }

    // Canonical Alias Accessor Methods
    public boolean hasCanonicalAlias()
    {
        LOG.debug("hasCanonicalAlias(): Entry, checking groupResource for Canonical Alias extension");
        if (this.hasExtension(pegacornGroupExtensionMeanings.getJoinRuleExtensionMeaning())) {
            LOG.debug("hasCanonicalAlias(): Exit, has the -canonical_alias- extension");
            return (true);
        }
        LOG.debug("hasJoinRule(): Exit, does not have the -canonical_alias- extension");
        return (false);
    }

    public Identifier getCanonicalAlias()
            throws GroupExtensionSetException
    {
        LOG.debug("getCanonicalAlias(): Entry, getting Canonical Alias");
        if (!hasCanonicalAlias()) {
            throw (new GroupExtensionSetException("getCanonicalAlias(): There is no Canonical Alias Extension"));
        }
        LOG.trace("getCanonicalAlias(): Extracting the appropriate Extension");
        Extension groupPriorityExtensionSet = this.getExtensionByUrl(pegacornGroupExtensionMeanings.getCanonicalAliasExtensionMeaning());
        LOG.trace("getCanonicalAlias(): Check the Value, ensure it is the appropriate Type (Identifier)");
        if (!(groupPriorityExtensionSet.getValue() instanceof Identifier)) {
            throw (new GroupExtensionSetException("getCanonicalAlias(): Group contains the wrong Canonical Alias extension value type"));
        }
        LOG.trace("getCanonicalAlias(): Extract the Value from the Exension");
        Identifier extractedCanonicalAlias = (Identifier) (groupPriorityExtensionSet.getValue());
        LOG.debug("getCanonicalAlias(): Exit, returning the Canonical Alias --> {}", extractedCanonicalAlias);
        return (extractedCanonicalAlias);
    }

    public void setCanonicalAlias(Identifier canonicalAlias)
    {
        LOG.debug("setCanonicalAlias(): Entry, setting Canonical Alias to --> {}", canonicalAlias);
        if (this.hasExtension(pegacornGroupExtensionMeanings.getCanonicalAliasExtensionMeaning())) {
            LOG.trace("setCanonicalAlias(): removing existing Extension");
            this.removeExtension(pegacornGroupExtensionMeanings.getCanonicalAliasExtensionMeaning());
        }
        LOG.trace("setCanonicalAlias(): Creating new Canonical Alias Extension");
        Extension newCanonicalAliasExtension = new Extension();
        newCanonicalAliasExtension.setUrl(pegacornGroupExtensionMeanings.getCanonicalAliasExtensionMeaning());
        newCanonicalAliasExtension.setValue(canonicalAlias);
        LOG.trace("setCanonicalAlias(): Injecting the Extension into Group");
        this.addExtension(newCanonicalAliasExtension);
        LOG.debug("setCanonicalAlias(): Exit, added new Canonical Alias --> {}", newCanonicalAliasExtension);
    }

    // Tooling
    public void removeExtension(String url)
    {
        Iterator<Extension> i = this.getExtension().iterator();
        while (i.hasNext()) {
            Extension e = i.next(); // must be called before you can call i.remove()
            if (e.getUrl().equals(url)) {
                i.remove();
            }
        }
    }

}

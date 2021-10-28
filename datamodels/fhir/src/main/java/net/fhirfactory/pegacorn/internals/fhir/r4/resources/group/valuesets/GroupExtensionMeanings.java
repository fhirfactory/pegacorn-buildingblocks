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
package net.fhirfactory.pegacorn.internals.fhir.r4.resources.group.valuesets;


import net.fhirfactory.pegacorn.internals.PegacornReferenceProperties;
import org.hl7.fhir.r4.model.UriType;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

/**
 *
 * @author ACT Health
 */

@ApplicationScoped
public final class GroupExtensionMeanings {

    @Inject
    private PegacornReferenceProperties pegacornReferenceProperties;

    private String GROUP_CODE_BASE="/group";

    private static final String COMMUNICATE_ROOM_PRIORITY_EXTENSION_MEANING = "communicate_room_priority_extension";
    private static final String COMMUNICATE_ROOM_JOINRULE_EXTENSION_MEANING = "communicate_room_join_rule_extension";
    private static final String COMMUNICATE_ROOM_PREDECESSOR_GROUP_EXTENSION_MEANING = "communicate_room_predecessor_group_extension";
    private static final String COMMUNICATE_ROOM_VERSION_EXTENSION_MEANING = "communicate_room_version_extension";
    private static final String COMMUNICATE_ROOM_PREDECSSOR_ROOM_LAST_MESSAGE_EXTENSION_MEANING = "communicate_room_predecessor_room_last_message_extension";
    private static final String COMMUNICATE_ROOM_CANONICAL_ALIAS_EXTENSION_MEANING = "communicate_room_canonical_alias_extension";
    private static final String COMMUNICATE_ROOM_FEDERATION_STATUS_EXTENSION_MEANING = "communicate_room_federation_status_extension";
    private static final String COMMUNICATE_ROOM_REPRESENTED_RESOURCE_EXTENSION_MEANING = "communicate_room_represented_resource_extension";
    private static final String COMMUNICATE_ROOM_CONTEXTUAL_STATUS_EXTENSION_MEANING = "communicate_room_contextual_status_extension";

    public String getGroupPriorityExtensionMeaning(){
        String groupPriority = pegacornReferenceProperties.getPegacornCodeSystemSite() + GROUP_CODE_BASE + COMMUNICATE_ROOM_PRIORITY_EXTENSION_MEANING;
        return(groupPriority);
    }

    public String getJoinRuleExtensionMeaning(){
        String joinRule = pegacornReferenceProperties.getPegacornCodeSystemSite() + GROUP_CODE_BASE + COMMUNICATE_ROOM_JOINRULE_EXTENSION_MEANING;
        return(joinRule);
    }

    public String getGroupPredecessorExtensionMeaning(){
        String groupPredecessor = pegacornReferenceProperties.getPegacornCodeSystemSite() + GROUP_CODE_BASE + COMMUNICATE_ROOM_PREDECESSOR_GROUP_EXTENSION_MEANING;
        return(groupPredecessor);
    }

    public String getGroupChatGroupVersionExtensionMeaning(){
        String chatGroupVersion = pegacornReferenceProperties.getPegacornCodeSystemSite() + GROUP_CODE_BASE + COMMUNICATE_ROOM_VERSION_EXTENSION_MEANING;
        return(chatGroupVersion);
    }

    public String getGroupPredecessorLastMessageExtensionMeaning(){
        String predecessorLastMessage = pegacornReferenceProperties.getPegacornCodeSystemSite() + GROUP_CODE_BASE + COMMUNICATE_ROOM_PREDECSSOR_ROOM_LAST_MESSAGE_EXTENSION_MEANING;
        return(predecessorLastMessage);
    }

    public String getCanonicalAliasExtensionMeaning(){
        String canonicalAlias = pegacornReferenceProperties.getPegacornCodeSystemSite() + GROUP_CODE_BASE + COMMUNICATE_ROOM_CANONICAL_ALIAS_EXTENSION_MEANING;
        return(canonicalAlias);
    }

    public String getGroupFederationStatusExtensionMeaning(){
        String federationStatus = pegacornReferenceProperties.getPegacornCodeSystemSite() + GROUP_CODE_BASE + COMMUNICATE_ROOM_FEDERATION_STATUS_EXTENSION_MEANING;
        return(federationStatus);
    }

    public String getRepresentedResourceMeaning(){
        String representedResource = pegacornReferenceProperties.getPegacornCodeSystemSite() + GROUP_CODE_BASE + COMMUNICATE_ROOM_REPRESENTED_RESOURCE_EXTENSION_MEANING;
        return(representedResource);
    }

    public String getContextualStatusMeaning(){
        String contextualStatus = pegacornReferenceProperties.getPegacornCodeSystemSite() + GROUP_CODE_BASE + COMMUNICATE_ROOM_CONTEXTUAL_STATUS_EXTENSION_MEANING;
        return(contextualStatus);
    }
}

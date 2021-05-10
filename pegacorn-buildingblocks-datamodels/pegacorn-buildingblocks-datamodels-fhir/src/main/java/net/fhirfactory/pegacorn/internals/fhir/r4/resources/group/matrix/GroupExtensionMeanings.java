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
package net.fhirfactory.pegacorn.internals.fhir.r4.resources.group.matrix;


import org.hl7.fhir.r4.model.UriType;

/**
 *
 * @author ACT Health
 */
public final class GroupExtensionMeanings
{
    private static final UriType GROUP_PRIORITY_EXTENSION_MEANING = new UriType("http://pegacorn.fhirfactory.net/pegacorn/fhir/R4/Group/group_priority");
    private static final UriType GROUP_JOINRULE_EXTENSION_MEANING = new UriType("http://pegacorn.fhirfactory.net/pegacorn/fhir/R4/Group/join_rule");
    private static final UriType GROUP_PREDECESSOR_GROUP_EXTENSION_MEANING = new UriType("http://pegacorn.fhirfactory.net/pegacorn/fhir/R4/Group/group_predecessor_group");
    private static final UriType GROUP_CHAT_GROUP_VERSION_EXTENSION_MEANING = new UriType("http://pegacorn.fhirfactory.net/pegacorn/fhir/R4/Group/group_room_version");
    private static final UriType GROUP_PREDECSSOR_ROOM_LAST_MESSAGE_EXTENSION_MEANING = new UriType("http://pegacorn.fhirfactory.net/pegacorn/fhir/R4/Group/group_predecessor_room_last_message");
    private static final UriType GROUP_CANONICAL_ALIAS_EXTENSION_MEANING = new UriType("http://pegacorn.fhirfactory.net/pegacorn/fhir/R4/Group/group_canonical_alias");
    private static final UriType GROUP_FEDERATION_STATUS_EXTENSION_MEANING = new UriType("http://pegacorn.fhirfactory.net/pegacorn/fhir/R4/Group/group_federation_status");

    public String getGroupPriorityExtensionMeaning(){return(GROUP_PRIORITY_EXTENSION_MEANING.asStringValue());}
    public String getJoinRuleExtensionMeaning(){return(GROUP_JOINRULE_EXTENSION_MEANING.asStringValue());}
    public String getGroupPredecessorExtensionMeaning(){return(GROUP_JOINRULE_EXTENSION_MEANING.asStringValue());}
    public String getGroupChatGroupVersionExtensionMeaning(){return(GROUP_CHAT_GROUP_VERSION_EXTENSION_MEANING.asStringValue());}
    public String getGroupPredecessorLastMessageExtensionMeaning(){return(GROUP_PREDECSSOR_ROOM_LAST_MESSAGE_EXTENSION_MEANING.asStringValue());}
    public String getCanonicalAliasExtensionMeaning(){return(GROUP_CANONICAL_ALIAS_EXTENSION_MEANING.asStringValue());}
    public String getGroupFederationStatusExtensionMeaning(){return(GROUP_FEDERATION_STATUS_EXTENSION_MEANING.asStringValue());}
}

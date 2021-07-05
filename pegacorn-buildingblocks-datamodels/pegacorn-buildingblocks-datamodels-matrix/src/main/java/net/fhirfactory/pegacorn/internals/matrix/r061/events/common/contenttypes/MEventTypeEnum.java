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
package net.fhirfactory.pegacorn.internals.matrix.r061.events.common.contenttypes;

public enum MEventTypeEnum {
    M_CALL_ANSWER("m.call.answer"),
    M_CALL_CANDIDATES("m.call.candidates"),
    M_CALL_HANGUP("m.call.hangup"),
    M_CALL_INVITE("m.call.invite"),
    M_DIRECT("m.direct"),
    M_FULLY_READ("m.fully_read"),
    M_IGNORED_USER_LIST("m.ignored_user_list"),
    M_PRESENCE("m.presence"),
    M_RECEIPT("m.receipt"),
    M_TAG("m.tag"),
    M_TYPING("m.typing"),
    M_POLICY_RULE_ROOM("m.policy.rule.room"),
    M_POLICY_RULE_SERVER("m.policy.rule.server"),
    M_POLICY_RULE_USER("m.policy.rule.user"),
    M_ROOM_CANONICAL_ALIAS("m.room.canonical_alias"),
    M_ROOM_CREATE("m.room.create"),
    M_ROOM_GUEST_ACCESS("m.room.guest_access"),
    M_ROOM_HISTORY_VISIBILITY("m.room.history_visibility"),
    M_ROOM_JOIN_RULES("m.room.join_rules"),
    M_ROOM_MEMBER("m.room.member"),
    M_ROOM_MESSAGE("m.room.message"),
    M_ROOM_MESSAGE_FEEDBACK("m.room.message.feedback"),
    M_ROOM_NAME("m.room.name"),
    M_ROOM_POWER_LEVELS("m.room.power_levels"),
    M_ROOM_REDACTION("m.room.redaction"),
    M_ROOM_SERVER_ACL("m.room.server_acl"),
    M_ROOM_THIRD_PARTY_INVITE("m.room.third_party_invite"),
    M_ROOM_TOMBSTONE("m.room.tombstone");

    private String eventType;

    private MEventTypeEnum(String eventType){
        this.eventType = eventType;
    }

    public String getEventType() {
        return eventType;
    }

    public static MEventTypeEnum fromString(String text) {
        for (MEventTypeEnum evType : MEventTypeEnum.values()) {
            if (evType.eventType.equalsIgnoreCase(text)) {
                return evType;
            }
        }
        return null;
    }
}

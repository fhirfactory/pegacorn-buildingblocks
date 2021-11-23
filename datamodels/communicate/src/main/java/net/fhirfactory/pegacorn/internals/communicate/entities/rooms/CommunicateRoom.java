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
package net.fhirfactory.pegacorn.internals.communicate.entities.rooms;

import com.fasterxml.jackson.annotation.JsonIgnore;
import net.fhirfactory.pegacorn.core.model.dates.EffectivePeriod;
import net.fhirfactory.pegacorn.internals.communicate.entities.rooms.datatypes.*;
import net.fhirfactory.pegacorn.internals.communicate.entities.message.CommunicateMessage;
import net.fhirfactory.pegacorn.internals.communicate.entities.rooms.valuesets.CommunicateRoomJoinRuleEnum;
import net.fhirfactory.pegacorn.internals.communicate.entities.rooms.valuesets.CommunicateRoomTypeEnum;
import net.fhirfactory.pegacorn.core.model.ui.resources.simple.CommunicateRoomESR;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;
import java.util.concurrent.ConcurrentLinkedQueue;

public class CommunicateRoom extends CommunicateRoomESR {
    private static final Logger LOG = LoggerFactory.getLogger(CommunicateRoom.class);
    private List<CommunicateRoomStatus> roomStatusHistory;
    private CommunicateRoomStatus currentRoomStatus;
    private CommunicateRoomTypeEnum roomType;
    private CommunicateRoomJoinRuleEnum joinRuleType;
    private CommunicateRoomPowerLevels powerLevels;
    private String topic;
    private List<CommunicateRoomMember> members;
    private ConcurrentLinkedQueue<CommunicateRoomEvent> eventQueue;
    private ConcurrentLinkedQueue<CommunicateMessage> receiveMessageQueue;
    private ConcurrentLinkedQueue<CommunicateMessage> sendMessageQueue;
    private EffectivePeriod activePeriod;

    @Override
    protected Logger getLogger(){return(LOG);}

    public CommunicateRoom(){
        super();
        this.currentRoomStatus = null;
        this.roomStatusHistory = new ArrayList<>();
        this.roomType = CommunicateRoomTypeEnum.COMMUNICATE_GENERAL_DISCUSSION_ROOM;
        this.members = new ArrayList<>();
        this.receiveMessageQueue = new ConcurrentLinkedQueue<>();
        this.sendMessageQueue = new ConcurrentLinkedQueue<>();
        this.eventQueue = new ConcurrentLinkedQueue<>();
        this.topic = null;
        this.joinRuleType = null;
        this.powerLevels = null;
        this.setRoomType(CommunicateRoomTypeEnum.COMMUNICATE_GENERAL_DISCUSSION_ROOM);
        this.activePeriod = new EffectivePeriod();
    }

    @JsonIgnore
    public CommunicateRoomID getRoomID(){
        CommunicateRoomID roomID = new CommunicateRoomID(getMatrixRoomID());
        return(roomID);
    }

    @JsonIgnore
    public void setRoomID(CommunicateRoomID roomID){
        this.setMatrixRoomID(roomID.getValue());
    }

    public EffectivePeriod getActivePeriod() {
        return activePeriod;
    }

    public void setActivePeriod(EffectivePeriod activePeriod) {
        this.activePeriod = activePeriod;
    }

    public CommunicateRoomStatus getCurrentRoomStatus() {
        return currentRoomStatus;
    }

    public void setRoomStatus(CommunicateRoomStatus roomStatus) {
        this.currentRoomStatus = roomStatus;
    }

    public Queue<CommunicateMessage> getReceiveMessageQueue() {
        return (this.receiveMessageQueue);
    }

    public void setReceiveMessageQueue(Queue<CommunicateMessage> receiveMessageQueue) {
        if(this.receiveMessageQueue == null){
            this.receiveMessageQueue = new ConcurrentLinkedQueue<>();
        }
        this.receiveMessageQueue.clear();
        this.receiveMessageQueue.addAll(receiveMessageQueue);
    }

    public Queue<CommunicateMessage> getSendMessageQueue() {
        return (this.sendMessageQueue);
    }

    public void setSendMessageQueue(Stack<CommunicateMessage> sendMessageQueue) {
        if(this.sendMessageQueue == null){
            this.sendMessageQueue = new ConcurrentLinkedQueue<>();
        }
        this.sendMessageQueue.clear();
        this.sendMessageQueue.addAll(sendMessageQueue);
    }

    @JsonIgnore
    public void addMessageToSendQueue(CommunicateMessage message){
        this.sendMessageQueue.add(message);
    }

    @JsonIgnore
    public void addMessageToReceiveQueue(CommunicateMessage message){
        this.receiveMessageQueue.add(message);
    }

    @JsonIgnore
    public CommunicateMessage getNextReceiveMessage(){
        if(this.getReceiveMessageQueue().isEmpty()){
            return(null);
        } else {
            CommunicateMessage message = this.getReceiveMessageQueue().poll();
            return(message);
        }
    }

    @JsonIgnore
    public CommunicateMessage getNextSendMessage(){
        if(this.getSendMessageQueue().isEmpty()){
            return(null);
        } else {
            CommunicateMessage message = this.getSendMessageQueue().poll();
            return(message);
        }
    }

    public Queue<CommunicateRoomEvent> getEventQueue() {
        return eventQueue;
    }

    public void setEventQueue(Queue<CommunicateRoomEvent> eventQueue) {
        if(this.eventQueue == null){
            this.eventQueue = new ConcurrentLinkedQueue<>();
        }
        this.eventQueue.clear();
        this.eventQueue.addAll(eventQueue);
    }

    @JsonIgnore
    public void addEvent(CommunicateRoomEvent message){
        this.eventQueue.add(message);
    }

    @JsonIgnore
    public CommunicateRoomEvent getNextEvent(){
        if(this.getEventQueue().isEmpty()){
            return(null);
        } else {
            CommunicateRoomEvent event = this.getEventQueue().poll();
            return(event);
        }
    }

    public CommunicateRoomTypeEnum getRoomType() {
        return roomType;
    }

    public void setRoomType(CommunicateRoomTypeEnum roomType) {
        this.roomType = roomType;
    }


    public List<CommunicateRoomMember> getMembers() {
        return members;
    }

    public void setMembers(List<CommunicateRoomMember> members) {
        this.members = members;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    @JsonIgnore
    public CommunicateRoomReference getRoomReference(){
        CommunicateRoomReference reference = new CommunicateRoomReference();
        reference.setRoomID(getSimplifiedID());
        reference.setRoomOwner(getRoomOwner());
        return(reference);
    }

    @JsonIgnore
    public String getMatrixRoomID(){
        return(this.getSimplifiedID());
    }

    @JsonIgnore
    public void setMatrixRoomID(String matrixID){
        this.setSimplifiedID(matrixID);
    }

    public CommunicateRoomJoinRuleEnum getJoinRuleType() {
        return joinRuleType;
    }

    public void setJoinRuleType(CommunicateRoomJoinRuleEnum joinRuleType) {
        this.joinRuleType = joinRuleType;
    }

    public CommunicateRoomPowerLevels getPowerLevels() {
        return powerLevels;
    }

    public void setPowerLevels(CommunicateRoomPowerLevels powerLevels) {
        this.powerLevels = powerLevels;
    }

    @Override
    public String toString() {
        return "CommunicateRoom{" +
                "roomID=" + getRoomID() +
                ", roomStatus=" + currentRoomStatus +
                ", roomType=" + roomType +
                ", joinRuleType=" + joinRuleType +
                ", powerLevels=" + powerLevels +
                ", topic=" + topic + 
                ", members=" + members +
                ", eventQueue=" + eventQueue +
                ", receiveMessageQueue=" + receiveMessageQueue +
                ", sendMessageQueue=" + sendMessageQueue +
                ", activePeriod=" + activePeriod +
                '}';
    }
}

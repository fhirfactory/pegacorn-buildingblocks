/*
 * Copyright (c) 2022 Mark A. Hunter (ACT Health)
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
package net.fhirfactory.pegacorn.petasos.participants.cache;

import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.work.datatypes.TaskWorkItemSubscriptionType;

import javax.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class DistributedTaskSubscriptionMap {
    private ArrayList<TaskWorkItemSubscriptionType> subscriptionList;
    private Object subscriptionListLock;

    //
    // Constructor(s)
    //

    public DistributedTaskSubscriptionMap(){
        this.subscriptionList = new ArrayList<>();
        this.subscriptionListLock = new Object();
    }

    //
    // Getters and Setters
    //

    protected ArrayList<TaskWorkItemSubscriptionType> getSubscriptionList() {
        return subscriptionList;
    }

    protected void setSubscriptionList(ArrayList<TaskWorkItemSubscriptionType> subscriptionList) {
        this.subscriptionList = subscriptionList;
    }

    public void addSubscription(TaskWorkItemSubscriptionType subscription){
        if(subscription != null){
            synchronized (this.subscriptionListLock) {
                if (!getSubscriptionList().contains(subscription)) {
                    getSubscriptionList().add(subscription);
                }
            }
        }
    }

    public List<TaskWorkItemSubscriptionType> getSubscriptions(){
        List<TaskWorkItemSubscriptionType> subscriptions = new ArrayList<>();
        synchronized (this.subscriptionListLock){
            subscriptions.addAll(getSubscriptionList());
        }
        return(subscriptions);
    }

    //
    // toString()
    //

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("DistributedTaskSubscriptionMap{");
        sb.append("subscriptionList=").append(subscriptionList);
        sb.append('}');
        return sb.toString();
    }
}

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
package net.fhirfactory.pegacorn.core.model.transaction.model;

import net.fhirfactory.pegacorn.core.model.transaction.valuesets.PegacornTransactionStatusEnum;
import net.fhirfactory.pegacorn.core.model.transaction.valuesets.PegacornTransactionTypeEnum;

import java.util.Objects;

public class InternalTransactionOutcome {
    private SimpleResourceID resourceID;
    private PegacornTransactionStatusEnum transactionStatus;
    private PegacornTransactionTypeEnum transactionType;
    private boolean transactionSuccessful;
    private String reason;

    public SimpleResourceID getResourceID() {
        return resourceID;
    }

    public void setResourceID(SimpleResourceID resourceID) {
        this.resourceID = resourceID;
    }

    public PegacornTransactionStatusEnum getTransactionStatus() {
        return transactionStatus;
    }

    public void setTransactionStatus(PegacornTransactionStatusEnum transactionStatus) {
        this.transactionStatus = transactionStatus;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public boolean isTransactionSuccessful() {
        return transactionSuccessful;
    }

    public void setTransactionSuccessful(boolean transactionSuccessful) {
        this.transactionSuccessful = transactionSuccessful;
    }

    public PegacornTransactionTypeEnum getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(PegacornTransactionTypeEnum transactionType) {
        this.transactionType = transactionType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InternalTransactionOutcome)) return false;
        InternalTransactionOutcome that = (InternalTransactionOutcome) o;
        return isTransactionSuccessful() == that.isTransactionSuccessful() && Objects.equals(getResourceID(), that.getResourceID()) && getTransactionStatus() == that.getTransactionStatus() && getTransactionType() == that.getTransactionType() && Objects.equals(getReason(), that.getReason());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getResourceID(), getTransactionStatus(), getTransactionType(), isTransactionSuccessful(), getReason());
    }

    @Override
    public String toString() {
        return "SimpleTransactionOutcome{" +
                "resourceID=" + resourceID +
                ", transactionStatus=" + transactionStatus +
                ", transactionType=" + transactionType +
                ", transactionSuccessful=" + transactionSuccessful +
                ", reason=" + reason +
                '}';
    }
}

/*
 * Copyright (c) 2021 Mark A. Hunter (ACT Health)
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
package net.fhirfactory.pegacorn.core.model.petasos.oam.notifications;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

public class ITOpsNotificationContent implements Serializable {
    private String contentHeading;
    private String content;
    private String formattedContent;

    //
    // Constructor
    //

    public ITOpsNotificationContent(){
        this.contentHeading = null;
        this.content = null;
        this.formattedContent = null;
    }

    public ITOpsNotificationContent(String content){

        this.content = content;
        this.formattedContent = null;
        this.contentHeading = null;
    }

    public ITOpsNotificationContent(String content, String formattedContent){
        this.content = content;
        this.formattedContent = formattedContent;
        this.contentHeading = null;
    }

    public ITOpsNotificationContent(String content, String formattedContent, String contentHeading){
        this.content = content;
        this.formattedContent = formattedContent;
        this.contentHeading = contentHeading;
    }

    public ITOpsNotificationContent(ITOpsNotificationContent ori){
        this.contentHeading = null;
        this.content = null;
        this.formattedContent = null;
        if(StringUtils.isNotEmpty(ori.getContentHeading())){
            this.contentHeading = ori.getContentHeading();
        }
        if(StringUtils.isNotEmpty(ori.getContent())){
            this.content = ori.getContent();
        }
        if(StringUtils.isNotEmpty(ori.getFormattedContent())){
            this.formattedContent = ori.getFormattedContent();
        }
    }

    //
    // Getters and Setters
    //

    public String getContentHeading() {
        return contentHeading;
    }

    public void setContentHeading(String contentHeading) {
        this.contentHeading = contentHeading;
    }

    public String getFormattedContent() {
        return formattedContent;
    }

    public void setFormattedContent(String formattedContent) {
        this.formattedContent = formattedContent;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    //
    // To String
    //

    @Override
    public String toString() {
        return "PetasosComponentITOpsNotification{" +
                "content='" + content + '\'' +
                ", formattedContent='" + formattedContent + '\'' +
                ", contentHeading=" + contentHeading +
                '}';
    }
}

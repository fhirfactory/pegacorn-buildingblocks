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
package net.fhirfactory.pegacorn.internals.communicate.entities.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import net.fhirfactory.pegacorn.internals.communicate.entities.user.datatypes.CommunicateSurrogateResourceReference;
import net.fhirfactory.pegacorn.internals.communicate.entities.user.datatypes.CommunicateUserID;
import net.fhirfactory.pegacorn.internals.communicate.entities.user.datatypes.CommunicateUserReference;
import net.fhirfactory.pegacorn.internals.communicate.entities.user.valuesets.CommunicateUserTypeEnum;
import net.fhirfactory.pegacorn.core.model.ui.resources.simple.CommunicateUserESR;
import net.fhirfactory.pegacorn.core.model.ui.resources.simple.valuesets.ExtremelySimplifiedResourceTypeEnum;
import net.fhirfactory.pegacorn.core.model.ui.resources.simple.datatypes.IdentifierESDT;
import net.fhirfactory.pegacorn.core.model.ui.resources.simple.valuesets.IdentifierESDTUseEnum;
import net.fhirfactory.pegacorn.core.model.ui.resources.simple.valuesets.IdentifierESDTTypesEnum;

public class CommunicateUser extends CommunicateUserESR {
    private CommunicateSurrogateResourceReference representedResource;
    private boolean surrogate;
    private boolean administrator;
    private boolean deactivated;
    private boolean shadowBanned;
    private String avatarURL;
    private CommunicateUserTypeEnum communicateUserType;
    private String communicateUserToken;

    public CommunicateUser(){
        super();
        this.communicateUserType = null;
        this.representedResource = null;
    }

    @JsonIgnore
    public CommunicateUserID getUserID(){
        CommunicateUserID id = new CommunicateUserID(getSimplifiedID());
        return(id);
    }

    @JsonIgnore
    public void setUserID(CommunicateUserID userID){
        this.setSimplifiedID(userID.getValue());
    }

    public CommunicateUserTypeEnum getCommunicateUserType() {
        return communicateUserType;
    }

    public void setCommunicateUserType(CommunicateUserTypeEnum communicateUserType) {
        this.communicateUserType = communicateUserType;
    }

    public CommunicateSurrogateResourceReference getRepresentedResource() {
        return representedResource;
    }

    public void setRepresentedResource(CommunicateSurrogateResourceReference representedResource) {
        this.representedResource = representedResource;
    }

    @JsonIgnore
    public CommunicateUserReference getCommunicateUserReference(){
        CommunicateUserReference userReference = new CommunicateUserReference();
        userReference.setUserType(getCommunicateUserType());
        userReference.setIdentifier(getIdentifier());
        userReference.setUserID(getUserID());
        userReference.setResourceType(ExtremelySimplifiedResourceTypeEnum.ESR_MATRIX_USER);
        return(userReference);
    }

    @JsonIgnore
    public IdentifierESDT getIdentifier(){
        IdentifierESDT identifier = new IdentifierESDT();
        identifier.setUse(IdentifierESDTUseEnum.SECONDARY);
        identifier.setValue(getSimplifiedID());
        identifier.setType(IdentifierESDTTypesEnum.ESR_IDENTIFIER_TYPE_MATRIX_USER_ID.getIdentifierType());
        return(identifier);
    }

    public String getCommunicateUserToken() {
        return communicateUserToken;
    }

    public void setCommunicateUserToken(String communicateUserToken) {
        this.communicateUserToken = communicateUserToken;
    }

    public boolean isSurrogate() {
        return surrogate;
    }

    public void setSurrogate(boolean surrogate) {
        this.surrogate = surrogate;
    }

    public boolean isAdministrator() {
        return administrator;
    }

    public void setAdministrator(boolean administrator) {
        this.administrator = administrator;
    }

    public boolean isDeactivated() {
        return deactivated;
    }

    public void setDeactivated(boolean deactivated) {
        this.deactivated = deactivated;
    }

    public boolean isShadowBanned() {
        return shadowBanned;
    }

    public void setShadowBanned(boolean shadowBanned) {
        this.shadowBanned = shadowBanned;
    }

    public String getAvatarURL() {
        return avatarURL;
    }

    public void setAvatarURL(String avatarURL) {
        this.avatarURL = avatarURL;
    }

    @Override
    public String toString() {
        return "CommunicateUser{" +
                "userID=" + getUserID() +
                ", representedResource=" + representedResource +
                ", surrogate=" + surrogate +
                ", administrator=" + administrator +
                ", deactivated=" + deactivated +
                ", shadowBanned=" + shadowBanned +
                ", avatarURL=" + avatarURL +
                ", communicateUserType=" + communicateUserType +
                '}';
    }
}

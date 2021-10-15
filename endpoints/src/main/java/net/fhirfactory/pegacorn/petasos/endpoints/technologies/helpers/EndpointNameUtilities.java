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
package net.fhirfactory.pegacorn.petasos.endpoints.technologies.helpers;

import net.fhirfactory.pegacorn.deployment.topology.model.endpoints.common.PetasosEndpointChannelScopeEnum;
import net.fhirfactory.pegacorn.deployment.topology.model.endpoints.common.PetasosEndpointFunctionTypeEnum;
import org.thymeleaf.util.StringUtils;

import javax.enterprise.context.ApplicationScoped;
import java.util.UUID;

@ApplicationScoped
public class EndpointNameUtilities {

    private String currentUUID;

    private String CHANNEL_NAME_SEPARATOR = "::";

    private static String INTERZONE_OAM_GROUP_NAME = "InterZone.OAM";
    private static String INTERZONE_IPC_GROUP_NAME = "InterZone.IPC";
    private static String INTRAZONE_OAM_GROUP_NAME = "IntraZone.OAM";
    private static String INTRAZONE_IPC_GROUP_NAME = "IntraZone.IPC";
    private static String INTRASITE_OAM_GROUP_NAME = "IntraSite.OAM";
    private static String INTRASITE_IPC_GROUP_NAME = "IntraSite.IPC";

    private static int ENDPOINT_SITE_POSITION_IN_CHANNEL_NAME = 0;
    private static int ENDPOINT_ZONE_POSITION_IN_CHANNEL_NAME = 1;
    private static int ENDPOINT_SERVICE_NAME_POSITION_IN_CHANNEL_NAME = 2;
    private static int ENDPOINT_SCOPE_POSITION_IN_CHANNEL_NAME = 3;
    private static int ENDPOINT_FUNCTION_POSITION_IN_CHANNEL_NAME = 4;
    private static int ENDPOINT_UNIQUE_ID_POSITION_IN_CHANNEL_NAME = 5;

    private static int ENDPOINT_SERVICE_NAME_POSITION_IN_ENDPOINT_NAME = 0;
    private static int ENDPOINT_SCOPE_POSITION_IN_ENDPOINT_NAME = 1;
    private static int ENDPOINT_UNIQUE_ID_POSITION_IN_ENDPOINT_NAME = 2;

    public EndpointNameUtilities(){
        this.currentUUID = UUID.randomUUID().toString();
    }

    public String buildChannelName(String endpointSite, String endpointZone, String endpointServiceName, String endpointScope, String endpointFunction, String endpointUniqueID){
        String channelName = endpointSite
                + getChannelNameSeparator()
                + endpointZone
                + getChannelNameSeparator()
                + endpointServiceName
                + getChannelNameSeparator()
                + endpointScope
                + getChannelNameSeparator()
                + endpointFunction
                + getChannelNameSeparator()
                + endpointUniqueID;
        return(channelName);
    }

    public String buildEndpointName(String endpointServiceName, String endpointScope, String endpointUniqueID ){
        String endpointName =  endpointServiceName
                + getChannelNameSeparator()
                + endpointScope
                + getChannelNameSeparator()
                + endpointUniqueID;
        return(endpointName);
    }

    public String buildEndpointNameFromChannelName(String channelName){
        if(StringUtils.isEmpty(channelName)){
            return(null);
        }
        String endpointServiceName = getEndpointServiceNameFromChannelName(channelName);
        String endpointScopeName = getEndpointScopeFromChannelName(channelName);
        String endpointUniqueID = getEndpointUniqueIDFromChannelName(channelName);
        String endpointName = buildEndpointName(endpointServiceName, endpointScopeName, endpointUniqueID);
        return(endpointName);
    }

    public String getOAMPubSubEndpointChannelNameFromOtherChannelName(String channelName){
        if(StringUtils.isEmpty(channelName)){
            return(null);
        }
        String oamPubSubName = remapFuncitonTypeInChannelName(channelName, PetasosEndpointFunctionTypeEnum.PETASOS_OAM_PUBSUB_ENDPOINT);
        return(oamPubSubName);
    }

    public String getOAMDiscoverEndpointChannelNameFromOtherChannelName(String channelName){
        if(StringUtils.isEmpty(channelName)){
            return(null);
        }
        String oamDiscoveryName = remapFuncitonTypeInChannelName(channelName, PetasosEndpointFunctionTypeEnum.PETASOS_OAM_DISCOVERY_ENDPOINT);
        return(oamDiscoveryName);
    }

    public String getIPCEndpointChannelNameFromOtherChannelName(String channelName){
        if(StringUtils.isEmpty(channelName)){
            return(null);
        }
        String ipcName = remapFuncitonTypeInChannelName(channelName, PetasosEndpointFunctionTypeEnum.PETASOS_IPC_ENDPOINT);
        return(ipcName);
    }

    private String remapFuncitonTypeInChannelName(String channelName, PetasosEndpointFunctionTypeEnum functionType){
        if(StringUtils.isEmpty(channelName)){
            return(null);
        }
        String[] nameSplit = channelName.split(getChannelNameSeparator());
        String endpointServiceName = nameSplit[ENDPOINT_SERVICE_NAME_POSITION_IN_CHANNEL_NAME];
        String endpointSiteName = nameSplit[ENDPOINT_SITE_POSITION_IN_CHANNEL_NAME];
        String endpointZoneName = nameSplit[ENDPOINT_ZONE_POSITION_IN_CHANNEL_NAME];
        String endpointScopeName = nameSplit[ENDPOINT_SCOPE_POSITION_IN_CHANNEL_NAME];
        String endpointFunctionName = functionType.getFunctionName();
        String endpointUUID = nameSplit[ENDPOINT_UNIQUE_ID_POSITION_IN_CHANNEL_NAME];
        String oamPubSubName = buildChannelName(endpointSiteName,endpointZoneName,endpointServiceName,endpointScopeName,endpointFunctionName,endpointUUID);
        return(oamPubSubName);
    }

    public String getEndpointNameFromChannelName(String channelName){
        if(StringUtils.isEmpty(channelName)){
            return(null);
        }
        String[] nameSplit = channelName.split(getChannelNameSeparator());
        String endpointServiceName = nameSplit[ENDPOINT_SERVICE_NAME_POSITION_IN_CHANNEL_NAME];
        String endpointScopeName = nameSplit[ENDPOINT_SCOPE_POSITION_IN_CHANNEL_NAME];
        String endpointUniqueID = nameSplit[ENDPOINT_UNIQUE_ID_POSITION_IN_CHANNEL_NAME];
        String endpointName = buildEndpointName(endpointServiceName, endpointScopeName, endpointUniqueID);
        return(endpointName);
    }

    public String getEndpointSiteFromChannelName(String channelName){
        if(StringUtils.isEmpty(channelName)){
            return(null);
        }
        String[] nameSplit = channelName.split(getChannelNameSeparator());
        String endpointSite = nameSplit[ENDPOINT_SITE_POSITION_IN_CHANNEL_NAME];
        return(endpointSite);
    }

    public boolean isEndpointsInSameSiteBasedOnChannelNames(String endpointChannelName1, String endpointChannelName2){
        if(StringUtils.isEmpty(endpointChannelName1) || StringUtils.isEmpty(endpointChannelName2)){
            return(false);
        }
        String endpointSite1 = getEndpointSiteFromChannelName(endpointChannelName1);
        String endpointSite2 = getEndpointSiteFromChannelName(endpointChannelName2);
        if(StringUtils.isEmpty(endpointSite1) || StringUtils.isEmpty(endpointSite2)){
            return(false);
        }
        boolean areSameSite = endpointSite1.contentEquals(endpointSite2);
        return(areSameSite);
    }

    public String getEndpointZoneFromChannelName(String channelName){
        if(StringUtils.isEmpty(channelName)){
            return(null);
        }
        String[] nameSplit = channelName.split(getChannelNameSeparator());
        String endpointZone = nameSplit[ENDPOINT_ZONE_POSITION_IN_CHANNEL_NAME];
        return(endpointZone);
    }

    public boolean isEndpointsInSameZoneBasedOnChannelNames(String endpointChannelName1, String endpointChannelName2){
        if(StringUtils.isEmpty(endpointChannelName1) || StringUtils.isEmpty(endpointChannelName2)){
            return(false);
        }
        String endpointZone1 = getEndpointZoneFromChannelName(endpointChannelName1);
        String endpointZone2 = getEndpointZoneFromChannelName(endpointChannelName2);
        if(StringUtils.isEmpty(endpointZone1) || StringUtils.isEmpty(endpointZone2)){
            return(false);
        }
        boolean areSameZone = endpointZone1.contentEquals(endpointZone2);
        return(areSameZone);
    }

    public String getEndpointServiceNameFromChannelName(String channelName){
        if(StringUtils.isEmpty(channelName)){
            return(null);
        }
        String[] nameSplit = channelName.split(getChannelNameSeparator());
        String endpointServiceName = nameSplit[ENDPOINT_SERVICE_NAME_POSITION_IN_CHANNEL_NAME];
        return(endpointServiceName);
    }

    public boolean isEndpointsDeliveringSameServiceBasedOnChannelNames(String endpointChannelName1, String endpointChannelName2){
        if(StringUtils.isEmpty(endpointChannelName1) || StringUtils.isEmpty(endpointChannelName2)){
            return(false);
        }
        String endpointServiceName1 = getEndpointServiceNameFromChannelName(endpointChannelName1);
        String endpointServiceName2 = getEndpointServiceNameFromChannelName(endpointChannelName2);
        if(StringUtils.isEmpty(endpointServiceName1) || StringUtils.isEmpty(endpointServiceName2)){
            return(false);
        }
        boolean areSameService = endpointServiceName1.contentEquals(endpointServiceName2);
        return(areSameService);
    }

    public String getEndpointScopeFromChannelName(String channelName){
        if(StringUtils.isEmpty(channelName)){
            return(null);
        }
        String[] nameSplit = channelName.split(getChannelNameSeparator());
        String endpointScopeName = nameSplit[ENDPOINT_SCOPE_POSITION_IN_CHANNEL_NAME];
        return(endpointScopeName);
    }

    public boolean isEndpointsSupportingSameScopeBasedOnChannelNames(String endpointChannelName1, String endpointChannelName2){
        if(StringUtils.isEmpty(endpointChannelName1) || StringUtils.isEmpty(endpointChannelName2)){
            return(false);
        }
        String endpointScopeName1 = getEndpointScopeFromChannelName(endpointChannelName1);
        String endpointScopeName2 = getEndpointScopeFromChannelName(endpointChannelName2);
        if(StringUtils.isEmpty(endpointScopeName1) || StringUtils.isEmpty(endpointScopeName2)){
            return(false);
        }
        boolean areSameScope = endpointScopeName1.contentEquals(endpointScopeName2);
        return(areSameScope);
    }

    public String getEndpointFunctionFromChannelName(String channelName){
        if(StringUtils.isEmpty(channelName)){
            return(null);
        }
        String[] nameSplit = channelName.split(getChannelNameSeparator());
        String endpointFunctionName = nameSplit[ENDPOINT_FUNCTION_POSITION_IN_CHANNEL_NAME];
        return(endpointFunctionName);
    }

    public boolean isEndpointsSupportingSameFunctionBaseOnChannelNames(String endpointChannelName1, String endpointChannelName2){
        if(StringUtils.isEmpty(endpointChannelName1) || StringUtils.isEmpty(endpointChannelName2)){
            return(false);
        }
        String endpointFunctionName1 = getEndpointFunctionFromChannelName(endpointChannelName1);
        String endpointFunctionName2 = getEndpointFunctionFromChannelName(endpointChannelName2);
        if(StringUtils.isEmpty(endpointFunctionName1) || StringUtils.isEmpty(endpointFunctionName2)){
            return(false);
        }
        boolean areSameFunction = endpointFunctionName1.contentEquals(endpointFunctionName2);
        return(areSameFunction);
    }

    public String getEndpointUniqueIDFromChannelName(String channelName){
        if(StringUtils.isEmpty(channelName)){
            return(null);
        }
        String[] nameSplit = channelName.split(getChannelNameSeparator());
        String endpointUniqueID = nameSplit[ENDPOINT_UNIQUE_ID_POSITION_IN_CHANNEL_NAME];
        return(endpointUniqueID);
    }

    public String getEndpointServiceNameFromEndpointName(String channelName){
        if(StringUtils.isEmpty(channelName)){
            return(null);
        }
        String[] nameSplit = channelName.split(getChannelNameSeparator());
        String endpointFunctionName = nameSplit[ENDPOINT_SERVICE_NAME_POSITION_IN_ENDPOINT_NAME];
        return(endpointFunctionName);
    }

    public boolean isEndpointsSupportingSameServiceBasedOnEndpointNames(String endpointName1, String endpointName2) {
        if (StringUtils.isEmpty(endpointName1) || StringUtils.isEmpty(endpointName2)) {
            return (false);
        }
        String endpointServiceName1 = getEndpointServiceNameFromEndpointName(endpointName1);
        String endpointServiceName2 = getEndpointServiceNameFromEndpointName(endpointName2);
        if (StringUtils.isEmpty(endpointServiceName1) || StringUtils.isEmpty(endpointServiceName2)) {
            return (false);
        }
        boolean areSameService = endpointServiceName1.contentEquals(endpointServiceName2);
        return (areSameService);
    }

    public String getEndpointScopeFromEndpointName(String endpointName){
        if(StringUtils.isEmpty(endpointName)){
            return(null);
        }
        String[] nameSplit = endpointName.split(getChannelNameSeparator());
        String endpointScopeName = nameSplit[ENDPOINT_SCOPE_POSITION_IN_ENDPOINT_NAME];
        return(endpointScopeName);
    }

    public boolean isEndpointsSupportingSameScopeBasedOnEndpointNames(String endpointName1, String endpointName2) {
        if (StringUtils.isEmpty(endpointName1) || StringUtils.isEmpty(endpointName2)) {
            return (false);
        }
        String endpointScopeName1 = getEndpointScopeFromEndpointName(endpointName1);
        String endpointScopeName2 = getEndpointScopeFromEndpointName(endpointName2);
        if (StringUtils.isEmpty(endpointScopeName1) || StringUtils.isEmpty(endpointScopeName2)) {
            return (false);
        }
        boolean areSameScope = endpointScopeName1.contentEquals(endpointScopeName2);
        return (areSameScope);
    }

    public String getEndpointUniqueIDFromEndpointName(String endpointName){
        if(StringUtils.isEmpty(endpointName)){
            return(null);
        }
        String[] nameSplit = endpointName.split(getChannelNameSeparator());
        String endpointScopeName = nameSplit[ENDPOINT_UNIQUE_ID_POSITION_IN_ENDPOINT_NAME];
        return(endpointScopeName);
    }

    //
    // Getters (and Setters)
    //

    public String getChannelNameSeparator() {
        return CHANNEL_NAME_SEPARATOR;
    }

    public String getCurrentUUID(){
        return(this.currentUUID);
    }

    public String getIntersiteScopeName() {
        return (PetasosEndpointChannelScopeEnum.ENDPOINT_CHANNEL_SCOPE_INTERSITE.getEndpointScopeName());
    }

    public String getIntrazoneScopeName() {
        return (PetasosEndpointChannelScopeEnum.ENDPOINT_CHANNEL_SCOPE_INTRAZONE.getEndpointScopeName());
    }

    public String getInterzoneScopeName() {
        return (PetasosEndpointChannelScopeEnum.ENDPOINT_CHANNEL_SCOPE_INTRAZONE.getEndpointScopeName());
    }

    public  String getOAMPubSubFunctionName(){
        return(PetasosEndpointFunctionTypeEnum.PETASOS_OAM_PUBSUB_ENDPOINT.getFunctionName());
    }

    public String getOAMDiscoveryFunctionName(){
        return(PetasosEndpointFunctionTypeEnum.PETASOS_OAM_DISCOVERY_ENDPOINT.getFunctionName());
    }

    public String getIPCFunctionName(){
        return(PetasosEndpointFunctionTypeEnum.PETASOS_IPC_ENDPOINT.getFunctionName());
    }
}

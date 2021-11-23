/*
 * Copyright (c) 2020 Mark A. Hunter (ACT Health)
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

package net.fhirfactory.pegacorn.petasos.core.subscriptions.cache;

import net.fhirfactory.pegacorn.core.model.componentid.ComponentIdType;
import net.fhirfactory.pegacorn.core.model.dataparcel.DataParcelManifest;
import net.fhirfactory.pegacorn.core.model.dataparcel.DataParcelTypeDescriptor;
import net.fhirfactory.pegacorn.core.model.dataparcel.valuesets.DataParcelNormalisationStatusEnum;
import net.fhirfactory.pegacorn.core.model.dataparcel.valuesets.DataParcelValidationStatusEnum;
import net.fhirfactory.pegacorn.core.model.dataparcel.valuesets.PolicyEnforcementPointApprovalStatusEnum;
import net.fhirfactory.pegacorn.core.model.petasos.pubsub.IntraSubsystemPubSubParticipant;
import net.fhirfactory.pegacorn.core.model.petasos.pubsub.PubSubParticipant;
import net.fhirfactory.pegacorn.core.model.petasos.pubsub.PubSubParticipantUtilisationStatusEnum;
import net.fhirfactory.pegacorn.core.model.petasos.pubsub.PubSubSubscription;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class DataParcelPublishingMapDM {
	private static final Logger LOG = LoggerFactory.getLogger(DataParcelPublishingMapDM.class);

	private ConcurrentHashMap<ComponentIdType, Set<DataParcelManifest>> publishingMap;
	private Object publishingMapLock;

	//
	// Constructor(s)
 	//

    public DataParcelPublishingMapDM(){
        this.publishingMap = new ConcurrentHashMap<ComponentIdType, Set<DataParcelManifest>>();
        this.publishingMapLock = new Object();
    }

	//
	// Business Methods
	//

	public void declarePublishedParcels(ComponentIdType publisherId, Set<DataParcelManifest> publishedTopics){
		getLogger().debug(".declarePublishedParcels(): Entry, publisherId->{}", publisherId);
		if(publisherId == null || publishedTopics == null){
			getLogger().debug(".declarePublishedParcels(): Exit, publisherId or publishedTopics is null, not registering anything");
			return;
		}
		if(publishedTopics.isEmpty()){
			getLogger().debug(".declarePublishedParcels(): Exit, publishedTopics is empty, not registering anything");
			return;
		}
		synchronized (getPublishingMapLock()){
			Set<DataParcelManifest> dataParcelManifests = null;
			if(!getPublishingMap().containsKey(publisherId)) {
				dataParcelManifests = getPublishingMap().get(publisherId);
			} else {
				dataParcelManifests = new HashSet<>();
				getPublishingMap().put(publisherId, dataParcelManifests);
			}
			for(DataParcelManifest currentManifest: publishedTopics){
				if(dataParcelManifests.contains(currentManifest)){
					// do nothing
				} else {
					dataParcelManifests.add(currentManifest);
				}
			}
		}
		getLogger().debug(".declarePublishedParcels(): Exit");
	}

	public Set<DataParcelManifest> getPublishedParcels(ComponentIdType publisherId){
		getLogger().debug(".getPublishedParcels(): Entry, publisherId->{}", publisherId);
		Set<DataParcelManifest> publishedParcels = new HashSet<>();
		if(publisherId == null){
			getLogger().debug(".getPublishedParcels(): Exit, publisherId is null, returning empty set");
			return(publishedParcels);
		}
		synchronized (getPublishingMapLock()){
			if(getPublishingMap().containsKey(publisherId)) {
				publishedParcels.addAll(getPublishingMap().get(publishedParcels));
			}
		}
		getLogger().debug(".getPublishedParcels(): Exit");
		return(publishedParcels);
	}

	//
	// Getters and Setters
	//

	protected Logger getLogger(){
		return(LOG);
	}

	protected ConcurrentHashMap<ComponentIdType, Set<DataParcelManifest>> getPublishingMap() {
		return publishingMap;
	}

	protected Object getPublishingMapLock() {
		return publishingMapLock;
	}
}

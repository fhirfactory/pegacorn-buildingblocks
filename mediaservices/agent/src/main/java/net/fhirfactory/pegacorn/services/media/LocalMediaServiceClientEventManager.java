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
package net.fhirfactory.pegacorn.services.media;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.hl7.fhir.r4.model.Media;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fhirfactory.pegacorn.core.interfaces.media.PetasosMediaServiceAgentInterface;
import net.fhirfactory.pegacorn.core.interfaces.media.PetasosMediaServiceBrokerInterface;
import net.fhirfactory.pegacorn.core.interfaces.media.PetasosMediaServiceProviderNameInterface;

@ApplicationScoped
public class LocalMediaServiceClientEventManager implements PetasosMediaServiceAgentInterface {
    private static final Logger LOG = LoggerFactory.getLogger(LocalMediaServiceClientEventManager.class);

    @Inject
    private PetasosMediaServiceBrokerInterface mediaService;
    
    @Inject
    private PetasosMediaServiceProviderNameInterface mediaServiceProvider;

    //
    // ConstructorPetasosAuditEventServiceClientWriterInterface
    //

    public LocalMediaServiceClientEventManager(){

    }

    //
    // PostConstruct Activities (Initialisation)
    //
    
    //XXX candidate for deletion?
    @PostConstruct
    public void initialise(){
        getLogger().debug(".initialise(): Entry");
        //Nothing to do here
        getLogger().debug(".initialise(): Exit");
    }

    //
    // Getters (and Setters)
    //

    protected Logger getLogger(){
        return(LOG);
    }


	@Override
	public Boolean captureMedia(Media media) {
		
        getLogger().info(".captureMedia(): Entry, media->{}", media);
        if(media == null){
            getLogger().debug(".captureMedia(): Exit, auditEvent is null");
            return(false);
        }
        Boolean 
            success = mediaService.saveMedia(mediaServiceProvider.getPetasosMediaServiceProviderName(), media);
       
        getLogger().info(".captureMedia(): Exit, success->{}", success);
        return(success);

	}

	@Override
	public Media loadMedia(String mediaId) {
        getLogger().info(".loadMedia(): Entry, mediaId->{}", mediaId);
        if(mediaId == null){
            getLogger().debug(".loadMedia(): Exit, mediaId is null");
            return(null);
        }
        Media media = mediaService.retrieveMedia(mediaServiceProvider.getPetasosMediaServiceProviderName(), mediaId);
        
        getLogger().info(".loadMedia(): Exit, media->{}", media);
        return(media);
	}
}

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
package net.fhirfactory.pegacorn.deployment.properties.configurationfilebased.petasos;

/**
 *
 * @author Mark A. Hunter
 */

public class PetasosProperties {
    
    // For assisting in monitoring expected end time in parcels, this period in milliseconds
    // can be used when monitoring a UoW. For example it can be added to the expected completion
    // time to provide an extra buffer to avoid classing a UoW as failed too early
    private final int EXPECTED_COMPLETION_TIME_BUFFER_MILLIS = 200;

    // How often to send a heartbeat between Nodes
    private final int HEARTBEAT_FREQUENCY_MILLIS = 100;
    
    // Every N heartbeats, include a status update with the heartbeat message
    private final int HEARTBEAT_STATUS_UPDATE_FREQUENCY = 10;
    
    // If a heartbeat fails, try to reconnect N times before failing and marking
    // the Node as unavailable
    private final int HEARTBEAT_NUM_RETRIES = 3;
    
    // If the cache is full, this is the location where the overflow is persisted
    private final String CACHE_OVERFLOW_DIRECTORY = "/tmp";
    
    // Size of the cache in bytes, once exceeded entries will be written to the overflow
    // directory
    private final long CACHE_SIZE_IN_BYTES = 1000000000;

  
    public int getExpectedCompletionTimeBufferMillis() {
        return EXPECTED_COMPLETION_TIME_BUFFER_MILLIS;
    }
    
    public int getHeartbeatFrequencyMillis() {
        return HEARTBEAT_FREQUENCY_MILLIS;
    }
    
    public int getHeartbeatStatusUpdateFrequency() {
        return HEARTBEAT_STATUS_UPDATE_FREQUENCY;
    }
    
    public int getHeartbeatNumRetries () {
        return HEARTBEAT_NUM_RETRIES;
    }
    
    public String getCacheOverflowDirectory() {
        return CACHE_OVERFLOW_DIRECTORY;
    }
    
    public long getCacheSizeInBytes() {
        return CACHE_SIZE_IN_BYTES;
    }
   
}
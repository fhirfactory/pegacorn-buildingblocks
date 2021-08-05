/*
 * Copyright (c) 2020 Mark A. Hunter
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
package net.fhirfactory.pegacorn.components.endpoints;

public enum PetasosEndpointStatusEnum {
    PETASOS_ENDPOINT_STATUS_SAME("petasos_endpoint.status.same"),
    PETASOS_ENDPOINT_STATUS_DETECTED("petasos_endpoint.status.detected"),
    PETASOS_ENDPOINT_STATUS_REACHABLE("petasos_endpoint.status.reachable"),
    PETASOS_ENDPOINT_STATUS_UNREACHABLE("petasos_endpoint.status.unreachable"),
    PETASOS_ENDPOINT_STATUS_STARTED("petasos_endpoint.status.started"),
    PETASOS_ENDPOINT_STATUS_OPERATIONAL("petasos_endpoint.status.operational"),
    PETASOS_ENDPOINT_STATUS_SUSPECT("petasos_endpoint.status.suspect"),
    PETASOS_ENDPOINT_STATUS_FAILED("petasos_endpoint.status.failed");

    private String endpointStatus;

    private PetasosEndpointStatusEnum(String status){
        this.endpointStatus = status;
    }

    public String getEndpointStatus() {
        return endpointStatus;
    }
}

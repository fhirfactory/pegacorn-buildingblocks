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
package net.fhirfactory.pegacorn.deployment.names.functionality.subsystem;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class FHIRPitComponentNames {

    private static final String FHIRPIT_SUBSYSTEM_DEFAULT_PUBLIC = "FHIRPit";
    private static final String FHIRPIT_EXTERNALISED_SERVICE_DEFAULT_PUBLIC = "FHIRPit";
    private static final String FHIRPIT_SERVICE_DEFAULT_PUBLIC = "FHIRPit-Public";
    private static final String FHIRPIT_PROCESSING_PLANT_DEFAULT_PUBLIC = "FHIRPit-Public";

    private static final String FHIRPIT_SUBSYSTEM_DEFAULT = "FHIRPit-Internal";
    private static final String FHIRPIT_EXTERNALISED_SERVICE_DEFAULT = "FHIRPit-Internal";
    private static final String FHIRPIT_SERVICE_DEFAULT = "FHIRPit-Public-Internal";
    private static final String FHIRPIT_PROCESSING_PLANT_DEFAULT = "FHIRPit-Public-Internal";

    public static String getFhirpitSubsystemDefaultPublic() {
        return FHIRPIT_SUBSYSTEM_DEFAULT_PUBLIC;
    }

    public static String getFhirpitExternalisedServiceDefaultPublic() {
        return FHIRPIT_EXTERNALISED_SERVICE_DEFAULT_PUBLIC;
    }

    public static String getFhirpitServiceDefaultPublic() {
        return FHIRPIT_SERVICE_DEFAULT_PUBLIC;
    }

    public static String getFhirpitProcessingPlantDefaultPublic() {
        return FHIRPIT_PROCESSING_PLANT_DEFAULT_PUBLIC;
    }

    public static String getFhirpitSubsystemDefault() {
        return FHIRPIT_SUBSYSTEM_DEFAULT;
    }

    public static String getFhirpitExternalisedServiceDefault() {
        return FHIRPIT_EXTERNALISED_SERVICE_DEFAULT;
    }

    public static String getFhirpitServiceDefault() {
        return FHIRPIT_SERVICE_DEFAULT;
    }

    public static String getFhirpitProcessingPlantDefault() {
        return FHIRPIT_PROCESSING_PLANT_DEFAULT;
    }
}

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
package net.fhirfactory.pegacorn.services.tasks.transforms.common;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TaskTransformConstants {
    private static String SYSTEM_WIDE_FOCUS_EXTENSION_URL = "task.business_status.system_wide_focus";
    private static String CLUSTER_WIDE_FOCUS_EXTENSION_URL = "task.business_status.cluster_wide_focus";
    private static String TASK_RETRY_REQUIRED_EXTENSION_URL = "task.business_status.retry_required";
    private static String TASK_RETRY_COUNT_EXTENSION_URL = "task.business_status.task_retry_count";

    private static String TASK_REGISTRATON_INSTANT_EXTENSION_URL = "task.period.registration_instant";
    private static String TASK_FINALISATION_INSTANT_EXTENSION_URL = "task.period.finalisation_instant";

    //
    // Getters and Setters
    //

    public String getSystemWideFocusExtensionUrl() {
        return SYSTEM_WIDE_FOCUS_EXTENSION_URL;
    }

    public String getClusterWideFocusExtensionUrl() {
        return CLUSTER_WIDE_FOCUS_EXTENSION_URL;
    }

    public String getTaskRetryRequiredExtensionUrl() {
        return TASK_RETRY_REQUIRED_EXTENSION_URL;
    }

    public String getTaskRetryCountExtensionUrl() {
        return TASK_RETRY_COUNT_EXTENSION_URL;
    }

    public String getTaskRegistratonInstantExtensionUrl() {
        return TASK_REGISTRATON_INSTANT_EXTENSION_URL;
    }

    public String getTaskFinalisationInstantExtensionUrl() {
        return TASK_FINALISATION_INSTANT_EXTENSION_URL;
    }
}

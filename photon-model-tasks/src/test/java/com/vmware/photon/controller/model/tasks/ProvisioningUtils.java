/*
 * Copyright (c) 2015-2016 VMware, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, without warranties or
 * conditions of any kind, EITHER EXPRESS OR IMPLIED.  See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.vmware.photon.controller.model.tasks;

import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeoutException;

import com.vmware.photon.controller.model.resources.ComputeDescriptionService;
import com.vmware.photon.controller.model.resources.ComputeService;
import com.vmware.photon.controller.model.resources.NetworkService;
import com.vmware.xenon.common.ServiceDocumentQueryResult;
import com.vmware.xenon.common.TaskState;
import com.vmware.xenon.common.UriUtils;
import com.vmware.xenon.common.Utils;
import com.vmware.xenon.common.test.VerificationHost;
import com.vmware.xenon.services.common.TaskService.TaskServiceState;

/**
 * Helper class for VM provisioning tests.
 *
 */
public class ProvisioningUtils {

    public static int getVMCount(VerificationHost host) throws Throwable {
        ServiceDocumentQueryResult res;
        res = host.getFactoryState(UriUtils
                .buildExpandLinksQueryUri(UriUtils.buildUri(host.getUri(),
                        ComputeService.FACTORY_LINK)));
        return res.documents.size() - 1;

    }

    public static ServiceDocumentQueryResult queryComputeInstances(VerificationHost host,
            int desiredCount)
            throws Throwable {
        return queryComputeInstances(host, host.getUri(), desiredCount);
    }

    public static ServiceDocumentQueryResult queryComputeInstances(VerificationHost host,
            URI remoteUri, int desiredCount)
            throws Throwable {
        Date expiration = host.getTestExpiration();
        ServiceDocumentQueryResult res;
        do {
            res = host.getFactoryState(UriUtils
                    .buildExpandLinksQueryUri(UriUtils.buildUri(remoteUri,
                            ComputeService.FACTORY_LINK)));
            if (res.documents.size() == desiredCount) {
                return res;
            }
        } while (new Date().before(expiration));
        throw new TimeoutException("Desired number of compute states not found. Expected "
                + desiredCount + "Found " + res.documents.size());
    }

    public static ServiceDocumentQueryResult queryComputeDescriptions(VerificationHost host,
            int desiredCount) throws Throwable {
        ServiceDocumentQueryResult res;
        res = host.getFactoryState(UriUtils
                .buildExpandLinksQueryUri(UriUtils.buildUri(host.getUri(),
                        ComputeDescriptionService.FACTORY_LINK)));
        if (res.documents.size() == desiredCount) {
            return res;
        }
        throw new Exception("Desired number of compute descriptions not found. Expected "
                + desiredCount + "Found " + res.documents.size());
    }

    public static ServiceDocumentQueryResult queryNetworkStates(VerificationHost host,
            int desiredCount) throws Throwable {
        ServiceDocumentQueryResult res;
        res = host.getFactoryState(UriUtils
                .buildExpandLinksQueryUri(UriUtils.buildUri(host.getUri(),
                        NetworkService.FACTORY_LINK)));
        if (res.documents.size() == desiredCount) {
            return res;
        }
        throw new Exception("Desired number of network states not found. Expected "
                + desiredCount + "Found " + res.documents.size());
    }

    public static void waitForTaskCompletion(VerificationHost host,
            List<URI> provisioningTasks, Class<? extends TaskServiceState> clazz)
            throws Throwable, InterruptedException, TimeoutException {
        Date expiration = host.getTestExpiration();
        List<String> pendingTasks = new ArrayList<String>();
        do {
            pendingTasks.clear();
            // grab in parallel, all task state, from all running tasks
            Map<URI, ? extends TaskServiceState> taskStates = host.getServiceState(null,
                    clazz,
                    provisioningTasks);

            boolean isConverged = true;
            for (Entry<URI, ? extends TaskServiceState> e : taskStates
                    .entrySet()) {
                TaskServiceState currentState = e.getValue();

                if (currentState.taskInfo.stage == TaskState.TaskStage.FAILED) {
                    throw new IllegalStateException(
                            "Task failed:" + Utils.toJsonHtml(currentState));
                }

                if (currentState.taskInfo.stage != TaskState.TaskStage.FINISHED) {
                    pendingTasks.add(currentState.documentSelfLink);
                    isConverged = false;
                }
            }

            if (isConverged) {
                return;
            }

            Thread.sleep(1000);
        } while (new Date().before(expiration));

        for (String taskLink : pendingTasks) {
            host.log("Pending task:\n%s", taskLink);
        }

        throw new TimeoutException("Some tasks never finished");
    }

}

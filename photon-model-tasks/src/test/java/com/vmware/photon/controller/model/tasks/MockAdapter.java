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

import com.vmware.photon.controller.model.UriPaths;
import com.vmware.photon.controller.model.adapterapi.ComputeBootRequest;
import com.vmware.photon.controller.model.adapterapi.ComputeEnumerateResourceRequest;
import com.vmware.photon.controller.model.adapterapi.ComputeInstanceRequest;
import com.vmware.photon.controller.model.adapterapi.EndpointConfigRequest;
import com.vmware.photon.controller.model.adapterapi.EndpointConfigRequest.RequestType;
import com.vmware.photon.controller.model.adapterapi.FirewallInstanceRequest;
import com.vmware.photon.controller.model.adapterapi.NetworkInstanceRequest;
import com.vmware.photon.controller.model.adapterapi.SnapshotRequest;
import com.vmware.photon.controller.model.helpers.BaseModelTest;
import com.vmware.photon.controller.model.resources.ComputeDescriptionService.ComputeDescription;
import com.vmware.photon.controller.model.resources.ComputeService.ComputeState;
import com.vmware.photon.controller.model.resources.EndpointService.EndpointState;
import com.vmware.photon.controller.model.tasks.EndpointAllocationTaskService.EndpointAllocationTaskState;
import com.vmware.photon.controller.model.tasks.SubTaskService.SubTaskState;
import com.vmware.xenon.common.Operation;
import com.vmware.xenon.common.ServiceErrorResponse;
import com.vmware.xenon.common.ServiceHost;
import com.vmware.xenon.common.StatelessService;
import com.vmware.xenon.common.TaskState;
import com.vmware.xenon.common.UriUtils;

/**
 * Mock adapters used by photon model task tests.
 */
public class MockAdapter {

    public static void startFactories(BaseModelTest test) throws Throwable {
        ServiceHost host = test.getHost();
        if (host.getServiceStage(MockSuccessInstanceAdapter.SELF_LINK) != null) {
            return;
        }
        host.startService(new MockSuccessInstanceAdapter());
        host.startService(new MockFailureInstanceAdapter());
        host.startService(new MockSuccessBootAdapter());
        host.startService(new MockFailureBootAdapter());
        host.startService(new MockSuccessEnumerationAdapter());
        host.startService(new MockFailureEnumerationAdapter());
        host.startService(new MockSnapshotSuccessAdapter());
        host.startService(new MockSnapshotFailureAdapter());
        host.startService(new MockNetworkInstanceSuccessAdapter());
        host.startService(new MockNetworkInstanceFailureAdapter());
        host.startService(new MockFirewallInstanceSuccessAdapter());
        host.startService(new MockFirewallInstanceFailureAdapter());
        host.startService(new MockSuccessEndpointAdapter(test));
    }

    public static TaskState createFailedTaskInfo() {
        TaskState taskState = new TaskState();
        taskState.stage = TaskState.TaskStage.FAILED;
        taskState.failure = ServiceErrorResponse
                .create(new IllegalStateException("Mock adapter failing task on purpose"), 500);
        return taskState;
    }

    /**
     * Mock instance adapter that always succeeds.
     */
    public static class MockSuccessInstanceAdapter extends StatelessService {
        public static final String SELF_LINK = UriPaths.PROVISIONING
                + "/mock_success_instance_adapter";

        @Override
        public void handleRequest(Operation op) {
            if (!op.hasBody()) {
                op.fail(new IllegalArgumentException("body is required"));
                return;
            }
            switch (op.getAction()) {
            case PATCH:
                ComputeInstanceRequest request = op
                        .getBody(ComputeInstanceRequest.class);
                SubTaskState computeSubTaskState = new SubTaskState();
                computeSubTaskState.taskInfo = new TaskState();
                computeSubTaskState.taskInfo.stage = TaskState.TaskStage.FINISHED;
                sendRequest(Operation.createPatch(
                        request.taskReference).setBody(
                                computeSubTaskState));
                break;
            default:
                super.handleRequest(op);
            }
        }
    }

    /**
     * Mock instance adapter that always fails.
     */
    public static class MockFailureInstanceAdapter extends StatelessService {
        public static final String SELF_LINK = UriPaths.PROVISIONING
                + "/mock_failure_instance_adapter";

        @Override
        public void handleRequest(Operation op) {
            if (!op.hasBody()) {
                op.fail(new IllegalArgumentException("body is required"));
                return;
            }
            switch (op.getAction()) {
            case PATCH:
                ComputeInstanceRequest request = op
                        .getBody(ComputeInstanceRequest.class);
                SubTaskState computeSubTaskState = new SubTaskState();
                computeSubTaskState.taskInfo = createFailedTaskInfo();
                sendRequest(Operation.createPatch(
                        request.taskReference).setBody(
                                computeSubTaskState));
                break;
            default:
                super.handleRequest(op);
            }
        }
    }

    /**
     * Mock boot adapter that always succeeds.
     */
    public static class MockSuccessBootAdapter extends StatelessService {
        public static final String SELF_LINK = UriPaths.PROVISIONING
                + "/mock_success_boot_adapter";

        @Override
        public void handleRequest(Operation op) {
            if (!op.hasBody()) {
                op.fail(new IllegalArgumentException("body is required"));
                return;
            }
            switch (op.getAction()) {
            case PATCH:
                ComputeBootRequest request = op
                        .getBody(ComputeBootRequest.class);
                SubTaskState computeSubTaskState = new SubTaskState();
                computeSubTaskState.taskInfo = new TaskState();
                computeSubTaskState.taskInfo.stage = TaskState.TaskStage.FINISHED;
                sendRequest(Operation.createPatch(
                        request.taskReference).setBody(
                                computeSubTaskState));
                break;
            default:
                super.handleRequest(op);
            }
        }
    }

    /**
     * Mock boot adapter that always fails.
     */
    public static class MockFailureBootAdapter extends StatelessService {
        public static final String SELF_LINK = UriPaths.PROVISIONING
                + "/mock_failure_boot_adapter";

        @Override
        public void handleRequest(Operation op) {
            if (!op.hasBody()) {
                op.fail(new IllegalArgumentException("body is required"));
                return;
            }
            switch (op.getAction()) {
            case PATCH:
                ComputeBootRequest request = op
                        .getBody(ComputeBootRequest.class);
                SubTaskState computeSubTaskState = new SubTaskState();
                computeSubTaskState.taskInfo = new TaskState();
                computeSubTaskState.taskInfo = createFailedTaskInfo();
                sendRequest(Operation.createPatch(
                        request.taskReference).setBody(
                                computeSubTaskState));
                break;
            default:
                super.handleRequest(op);
            }
        }
    }

    /**
     * Mock Enumeration adapter that always succeeds.
     */
    public static class MockSuccessEnumerationAdapter extends StatelessService {
        public static final String SELF_LINK = UriPaths.PROVISIONING
                + "/mock_success_enumeration_adapter";

        @Override
        public void handleRequest(Operation op) {
            if (!op.hasBody()) {
                op.fail(new IllegalArgumentException("body is required"));
                return;
            }
            switch (op.getAction()) {
            case PATCH:
                ComputeEnumerateResourceRequest request = op
                        .getBody(ComputeEnumerateResourceRequest.class);
                ResourceEnumerationTaskService.ResourceEnumerationTaskState patchState = new ResourceEnumerationTaskService.ResourceEnumerationTaskState();
                patchState.taskInfo = new TaskState();
                patchState.taskInfo.stage = TaskState.TaskStage.FINISHED;
                sendRequest(Operation.createPatch(
                        request.taskReference).setBody(patchState));
                break;
            default:
                super.handleRequest(op);
            }
        }
    }

    /**
     * Mock Enumeration adapter that always fails.
     */
    public static class MockFailureEnumerationAdapter extends StatelessService {
        public static final String SELF_LINK = UriPaths.PROVISIONING
                + "/mock_failure_enumeration_adapter";

        @Override
        public void handleRequest(Operation op) {
            if (!op.hasBody()) {
                op.fail(new IllegalArgumentException("body is required"));
                return;
            }
            switch (op.getAction()) {
            case PATCH:
                ComputeEnumerateResourceRequest request = op
                        .getBody(ComputeEnumerateResourceRequest.class);
                ResourceEnumerationTaskService.ResourceEnumerationTaskState patchState = new ResourceEnumerationTaskService.ResourceEnumerationTaskState();
                patchState.taskInfo = createFailedTaskInfo();
                sendRequest(Operation.createPatch(
                        request.taskReference).setBody(patchState));
                break;
            default:
                super.handleRequest(op);
            }
        }
    }

    /**
     * Mock snapshot adapter that always succeeds.
     */
    public static class MockSnapshotSuccessAdapter extends StatelessService {
        public static final String SELF_LINK = UriPaths.PROVISIONING
                + "/mock_snapshot_success_adapter";

        @Override
        public void handleRequest(Operation op) {
            if (!op.hasBody()) {
                op.fail(new IllegalArgumentException("body is required"));
                return;
            }

            switch (op.getAction()) {
            case PATCH:
                SnapshotRequest request = op.getBody(SnapshotRequest.class);
                SubTaskState computeSubTaskState = new SubTaskState();
                computeSubTaskState.taskInfo = new TaskState();
                computeSubTaskState.taskInfo.stage = TaskState.TaskStage.FINISHED;
                sendRequest(Operation
                        .createPatch(request.taskReference).setBody(
                                computeSubTaskState));
                op.complete();
                break;
            default:
                super.handleRequest(op);
            }
        }
    }

    /**
     * Mock snapshot adapter that always fails.
     */
    public static class MockSnapshotFailureAdapter extends StatelessService {
        public static final String SELF_LINK = UriPaths.PROVISIONING
                + "/mock_snapshot_failure_adapter";

        @Override
        public void handleRequest(Operation op) {
            if (!op.hasBody()) {
                op.fail(new IllegalArgumentException("body is required"));
                return;
            }
            switch (op.getAction()) {
            case PATCH:
                SnapshotRequest request = op.getBody(SnapshotRequest.class);
                SubTaskState computeSubTaskState = new SubTaskState();
                computeSubTaskState.taskInfo = createFailedTaskInfo();
                sendRequest(Operation
                        .createPatch(request.taskReference).setBody(
                                computeSubTaskState));
                op.complete();
                break;
            default:
                super.handleRequest(op);
            }
        }
    }

    /**
     * Mock network instance adapter that always succeeds.
     */
    public static class MockNetworkInstanceSuccessAdapter extends
            StatelessService {
        public static final String SELF_LINK = UriPaths.PROVISIONING
                + "/mock_network_service_success_adapter";

        @Override
        public void handleRequest(Operation op) {
            if (!op.hasBody()) {
                op.fail(new IllegalArgumentException("body is required"));
                return;
            }
            switch (op.getAction()) {
            case PATCH:
                NetworkInstanceRequest request = op
                        .getBody(NetworkInstanceRequest.class);
                ProvisionNetworkTaskService.ProvisionNetworkTaskState provisionNetworkTaskState = new ProvisionNetworkTaskService.ProvisionNetworkTaskState();
                provisionNetworkTaskState.taskInfo = new TaskState();
                provisionNetworkTaskState.taskInfo.stage = TaskState.TaskStage.FINISHED;
                sendRequest(Operation.createPatch(
                        request.taskReference).setBody(
                                provisionNetworkTaskState));
                op.complete();
                break;
            default:
                super.handleRequest(op);
            }
        }
    }

    /**
     * Mock network instance adapter that always fails.
     */
    public static class MockNetworkInstanceFailureAdapter extends
            StatelessService {
        public static final String SELF_LINK = UriPaths.PROVISIONING
                + "/mock_network_service_failure_adapter";

        @Override
        public void handleRequest(Operation op) {
            if (!op.hasBody()) {
                op.fail(new IllegalArgumentException("body is required"));
                return;
            }
            switch (op.getAction()) {
            case PATCH:
                NetworkInstanceRequest request = op
                        .getBody(NetworkInstanceRequest.class);
                ProvisionNetworkTaskService.ProvisionNetworkTaskState provisionNetworkTaskState = new ProvisionNetworkTaskService.ProvisionNetworkTaskState();
                provisionNetworkTaskState.taskInfo = createFailedTaskInfo();
                sendRequest(Operation.createPatch(
                        request.taskReference).setBody(
                                provisionNetworkTaskState));
                op.complete();
                break;
            default:
                super.handleRequest(op);
            }
        }
    }

    /**
     * Mock firewall instance adapter that always succeeds.
     */
    public static class MockFirewallInstanceSuccessAdapter extends
            StatelessService {
        public static final String SELF_LINK = UriPaths.PROVISIONING
                + "/mock_firewall_service_success_adapter";

        @Override
        public void handleRequest(Operation op) {
            if (!op.hasBody()) {
                op.fail(new IllegalArgumentException("body is required"));
                return;
            }
            switch (op.getAction()) {
            case PATCH:
                FirewallInstanceRequest request = op
                        .getBody(FirewallInstanceRequest.class);
                ProvisionFirewallTaskService.ProvisionFirewallTaskState provisionFirewallTaskState = new ProvisionFirewallTaskService.ProvisionFirewallTaskState();
                provisionFirewallTaskState.taskInfo = new TaskState();
                provisionFirewallTaskState.taskInfo.stage = TaskState.TaskStage.FINISHED;
                sendRequest(Operation.createPatch(
                        request.taskReference).setBody(
                                provisionFirewallTaskState));
                op.complete();
                break;
            default:
                super.handleRequest(op);
            }
        }
    }

    /**
     * Mock firewall instance adapter that always fails.
     */
    public static class MockFirewallInstanceFailureAdapter extends
            StatelessService {
        public static final String SELF_LINK = UriPaths.PROVISIONING
                + "/mock_firewall_service_failure_adapter";

        @Override
        public void handleRequest(Operation op) {
            if (!op.hasBody()) {
                op.fail(new IllegalArgumentException("body is required"));
                return;
            }
            switch (op.getAction()) {
            case PATCH:
                FirewallInstanceRequest request = op
                        .getBody(FirewallInstanceRequest.class);
                ProvisionFirewallTaskService.ProvisionFirewallTaskState provisionFirewallTaskState = new ProvisionFirewallTaskService.ProvisionFirewallTaskState();
                provisionFirewallTaskState.taskInfo = createFailedTaskInfo();
                sendRequest(Operation.createPatch(
                        request.taskReference).setBody(
                                provisionFirewallTaskState));
                op.complete();
                break;
            default:
                super.handleRequest(op);
            }
        }
    }

    /**
     * Mock endpoint adapter that always succeeds.
     */
    public static class MockSuccessEndpointAdapter extends StatelessService {
        public static final String SELF_LINK = UriPaths.PROVISIONING
                + "/mock_success_endpoint_adapter";

        private BaseModelTest test;

        public MockSuccessEndpointAdapter(BaseModelTest test) {
            this.test = test;
        }

        @Override
        public void handleRequest(Operation op) {
            if (!op.hasBody()) {
                op.fail(new IllegalArgumentException("body is required"));
                return;
            }
            switch (op.getAction()) {
            case PATCH:
                EndpointConfigRequest request = op
                        .getBody(EndpointConfigRequest.class);
                if (request.requestType == RequestType.VALIDATE) {
                    op.complete();
                    return;
                }
                try {
                    EndpointState endpoint = this.test.getServiceSynchronously(
                            request.resourceLink(),
                            EndpointState.class);
                    ComputeDescription cd = new ComputeDescription();
                    if (endpoint.endpointProperties.containsKey(EndpointConfigRequest.ZONE_KEY)) {
                        cd.zoneId = endpoint.endpointProperties.get(EndpointConfigRequest.ZONE_KEY);
                    }
                    cd.enumerationAdapterReference = UriUtils.buildUri(getHost(),
                            MockSuccessEnumerationAdapter.SELF_LINK);
                    this.test.patchServiceSynchronously(endpoint.computeDescriptionLink, cd);

                    ComputeState cs = new ComputeState();
                    cs.adapterManagementReference = UriUtils.buildUri(getHost(),
                            "fake-management-adapter");
                    this.test.patchServiceSynchronously(endpoint.computeLink, cs);

                    EndpointAllocationTaskState state = new EndpointAllocationTaskState();
                    state.taskInfo = new TaskState();
                    state.taskInfo.stage = TaskState.TaskStage.FINISHED;
                    state.taskSubStage = EndpointAllocationTaskService.SubStage.COMPLETED;
                    sendRequest(Operation.createPatch(
                            request.taskReference).setBody(state));
                } catch (Throwable e) {
                    op.fail(e);
                }
                break;
            default:
                super.handleRequest(op);
            }
        }
    }
}

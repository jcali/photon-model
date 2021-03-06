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

package com.vmware.photon.controller.model.tasks.monitoring;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import com.vmware.photon.controller.model.helpers.BaseModelTest;
import com.vmware.photon.controller.model.monitoring.ResourceAggregateMetricService;
import com.vmware.photon.controller.model.resources.ComputeDescriptionService;
import com.vmware.photon.controller.model.resources.ComputeDescriptionService.ComputeDescription;
import com.vmware.photon.controller.model.resources.ComputeService;
import com.vmware.photon.controller.model.resources.ComputeService.ComputeState;
import com.vmware.photon.controller.model.resources.ResourcePoolService;
import com.vmware.photon.controller.model.resources.ResourcePoolService.ResourcePoolState;
import com.vmware.photon.controller.model.tasks.PhotonModelTaskServices;
import com.vmware.photon.controller.model.tasks.monitoring.StatsAggregationTaskService.StatsAggregationTaskState;
import com.vmware.photon.controller.model.tasks.monitoring.StatsCollectionTaskService.StatsCollectionTaskState;
import com.vmware.xenon.common.Operation;
import com.vmware.xenon.common.ServiceDocumentQueryResult;
import com.vmware.xenon.common.UriUtils;
import com.vmware.xenon.services.common.QueryTask.Query;

public class StatsAggregationTaskServiceTest extends BaseModelTest {

    public int numResources = 200;

    @Override
    protected void startRequiredServices() throws Throwable {
        super.startRequiredServices();
        PhotonModelTaskServices.startServices(this.getHost());
        this.host.startService(
                Operation.createPost(UriUtils.buildUri(this.host,
                        MockStatsAdapter.class)),
                new MockStatsAdapter());

        this.host.waitForServiceAvailable(StatsCollectionTaskService.FACTORY_LINK);
        this.host.waitForServiceAvailable(SingleResourceStatsAggregationTaskService.FACTORY_LINK);
        this.host.waitForServiceAvailable(StatsAggregationTaskService.FACTORY_LINK);
        this.host.waitForServiceAvailable(MockStatsAdapter.SELF_LINK);
    }

    @Test
    public void testStatsAggregation() throws Throwable {
        // create a resource pool
        ResourcePoolState rpState = new ResourcePoolState();
        rpState.name = "testName";
        ResourcePoolState rpReturnState = postServiceSynchronously(
                ResourcePoolService.FACTORY_LINK, rpState,
                ResourcePoolState.class);
        ComputeDescription cDesc = new ComputeDescription();
        cDesc.name = rpState.name;
        cDesc.statsAdapterReference = UriUtils.buildUri(this.host, MockStatsAdapter.SELF_LINK);
        ComputeDescription descReturnState = postServiceSynchronously(
                ComputeDescriptionService.FACTORY_LINK, cDesc,
                ComputeDescription.class);
        ComputeState computeState = new ComputeState();
        computeState.name = rpState.name;
        computeState.descriptionLink = descReturnState.documentSelfLink;
        computeState.resourcePoolLink = rpReturnState.documentSelfLink;
        List<String> computeLinks = new ArrayList<>();
        for (int i = 0; i < this.numResources; i++) {
            ComputeState res = postServiceSynchronously(
                    ComputeService.FACTORY_LINK, computeState,
                    ComputeState.class);
            computeLinks.add(res.documentSelfLink);
        }
        // kick off an aggregation task when stats are not populated
        StatsAggregationTaskState aggregationTaskState = new StatsAggregationTaskState();
        Query taskQuery = Query.Builder.create()
                .addFieldClause(ComputeState.FIELD_NAME_RESOURCE_POOL_LINK, rpReturnState.documentSelfLink).build();
        aggregationTaskState.query =  taskQuery;
        aggregationTaskState.metricNames = Collections.singleton(MockStatsAdapter.KEY_1);
        StatsAggregationTaskState returnState  = postServiceSynchronously(
                StatsAggregationTaskService.FACTORY_LINK, aggregationTaskState,
                StatsAggregationTaskState.class);
        waitForFinishedTask(StatsAggregationTaskState.class,
                returnState.documentSelfLink);
        this.host.waitFor("Error waiting for stats", () -> {
            ServiceDocumentQueryResult aggrRes = this.host.getFactoryState(UriUtils.buildUri(this.host,
                        ResourceAggregateMetricService.FACTORY_LINK));
            // Expect 0 stats because they're not collected yet
            if (aggrRes.documentCount == 0) {
                return true;
            }
            return false;
        });

        StatsCollectionTaskState collectionTaskState = new StatsCollectionTaskState();
        collectionTaskState.resourcePoolLink = rpReturnState.documentSelfLink;
        StatsCollectionTaskState colelctionReturnState = postServiceSynchronously(
                StatsCollectionTaskService.FACTORY_LINK, collectionTaskState,
                StatsCollectionTaskState.class);
        waitForFinishedTask(StatsCollectionTaskState.class,
                colelctionReturnState.documentSelfLink);

        // kick off an aggregation task
        aggregationTaskState = new StatsAggregationTaskState();
        aggregationTaskState.query =  taskQuery;
        aggregationTaskState.metricNames = Collections.singleton(MockStatsAdapter.KEY_1);
        returnState  = postServiceSynchronously(StatsAggregationTaskService.FACTORY_LINK, aggregationTaskState,
                StatsAggregationTaskState.class);
        waitForFinishedTask(StatsAggregationTaskState.class,
                returnState.documentSelfLink);
        this.host.waitFor("Error waiting for stats", () -> {
            ServiceDocumentQueryResult aggrRes = this.host.getFactoryState(UriUtils.buildUri(this.host,
                        ResourceAggregateMetricService.FACTORY_LINK));
            if (aggrRes.documentCount ==  (2 * this.numResources)) {
                return true;
            }
            return false;
        });
    }
}

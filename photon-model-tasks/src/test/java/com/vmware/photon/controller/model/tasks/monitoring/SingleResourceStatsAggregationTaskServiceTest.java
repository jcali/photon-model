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

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;

import com.vmware.photon.controller.model.helpers.BaseModelTest;
import com.vmware.photon.controller.model.monitoring.ResourceAggregateMetricService;
import com.vmware.photon.controller.model.monitoring.ResourceAggregateMetricService.ResourceAggregateMetric;
import com.vmware.photon.controller.model.resources.ComputeDescriptionService;
import com.vmware.photon.controller.model.resources.ComputeDescriptionService.ComputeDescription;
import com.vmware.photon.controller.model.resources.ComputeService;
import com.vmware.photon.controller.model.resources.ComputeService.ComputeState;
import com.vmware.photon.controller.model.resources.ResourcePoolService;
import com.vmware.photon.controller.model.resources.ResourcePoolService.ResourcePoolState;
import com.vmware.photon.controller.model.tasks.PhotonModelTaskServices;
import com.vmware.photon.controller.model.tasks.monitoring.SingleResourceStatsAggregationTaskService.SingleResourceStatsAggregationTaskState;
import com.vmware.photon.controller.model.tasks.monitoring.StatsCollectionTaskService.StatsCollectionTaskState;
import com.vmware.xenon.common.Operation;
import com.vmware.xenon.common.ServiceDocument;
import com.vmware.xenon.common.ServiceDocumentQueryResult;
import com.vmware.xenon.common.ServiceHost;
import com.vmware.xenon.common.ServiceStats;
import com.vmware.xenon.common.ServiceStats.ServiceStat;
import com.vmware.xenon.common.ServiceStats.TimeSeriesStats.AggregationType;
import com.vmware.xenon.common.UriUtils;
import com.vmware.xenon.common.Utils;
import com.vmware.xenon.services.common.QueryTask.Query;
import com.vmware.xenon.services.common.QueryTask.QuerySpecification;
import com.vmware.xenon.services.common.QueryTask.QuerySpecification.QueryOption;
import com.vmware.xenon.services.common.QueryTask.QueryTerm.MatchType;

public class SingleResourceStatsAggregationTaskServiceTest extends BaseModelTest {

    private static final int NUM_COMPUTE_RESOURCES = 200;
    private static final int NUM_COLLECTIONS = 5;

    @Override
    protected void startRequiredServices() throws Throwable {
        super.startRequiredServices();
        PhotonModelTaskServices.startServices(this.getHost());
        this.host.startService(
                Operation.createPost(UriUtils.buildUri(this.host,
                        MockStatsAdapter.class)),
                new MockStatsAdapter());
        this.host.waitForServiceAvailable(StatsCollectionTaskService.FACTORY_LINK);
        this.host.waitForServiceAvailable(SingleResourceStatsCollectionTaskService.FACTORY_LINK);
        this.host.waitForServiceAvailable(SingleResourceStatsAggregationTaskService.FACTORY_LINK);
        this.host.waitForServiceAvailable(MockStatsAdapter.SELF_LINK);
    }

    @Test
    public void testResourceStatsAggregation() throws Throwable {
        // create a resource pool
        ResourcePoolState rpState = new ResourcePoolState();
        rpState.name = "testName";
        ResourcePoolState rpReturnState = postServiceSynchronously(
                ResourcePoolService.FACTORY_LINK, rpState,
                ResourcePoolState.class);

        ComputeState[] computeStateArray = new ComputeState[NUM_COMPUTE_RESOURCES];
        for (int i = 0; i < NUM_COMPUTE_RESOURCES; i++) {
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
            computeStateArray[i] = postServiceSynchronously(
                    ComputeService.FACTORY_LINK, computeState,
                    ComputeState.class);
        }

        StatsCollectionTaskState collectionTaskState = new StatsCollectionTaskState();
        collectionTaskState.resourcePoolLink = rpReturnState.documentSelfLink;
        int counter = 0;
        while (counter < NUM_COLLECTIONS) {
            StatsCollectionTaskState returnState = this
                    .postServiceSynchronously(
                            StatsCollectionTaskService.FACTORY_LINK,
                            collectionTaskState, StatsCollectionTaskState.class);
            waitForFinishedTask(StatsCollectionTaskState.class,
                    returnState.documentSelfLink);
            counter++;
        }
        // wait for stats to be populated
        this.host.waitFor("Error waiting for stats", () -> {
            boolean returnStatus = false;
            for (int i = 0; i < NUM_COMPUTE_RESOURCES; i++) {
                String statsUriPath = UriUtils.buildUriPath(computeStateArray[i].documentSelfLink,
                        ServiceHost.SERVICE_URI_SUFFIX_STATS);
                ServiceStats resStats = getServiceSynchronously(statsUriPath, ServiceStats.class);
                for (ServiceStat stat : resStats.entries.values()) {
                    if (stat.name
                            .startsWith(UriUtils.getLastPathSegment(MockStatsAdapter.SELF_LINK))
                            && stat.timeSeriesStats.bins.size() > 0) {
                        returnStatus = true;
                        break;
                    }
                }
            }
            return returnStatus;
        });

        // kick off an aggregation task
        SingleResourceStatsAggregationTaskState aggregationTaskState = new SingleResourceStatsAggregationTaskState();
        aggregationTaskState.resourceLink = computeStateArray[0].documentSelfLink;
        aggregationTaskState.metricNames = new HashSet<>(
                Arrays.asList(MockStatsAdapter.KEY_1, MockStatsAdapter.KEY_2));
        postServiceSynchronously(SingleResourceStatsAggregationTaskService.FACTORY_LINK,
                aggregationTaskState,
                SingleResourceStatsAggregationTaskState.class);
        this.host.waitFor("Error waiting for rolled up stats", () -> {
            ServiceDocumentQueryResult result = this.host
                    .getExpandedFactoryState(UriUtils.buildUri(this.host,
                            ResourceAggregateMetricService.FACTORY_LINK));
            return (result.documentCount == 4);
        });

        // kick off an another aggregation task, ensure the version number has been updated
        postServiceSynchronously(SingleResourceStatsAggregationTaskService.FACTORY_LINK,
                aggregationTaskState,
                SingleResourceStatsAggregationTaskState.class);
        this.host.waitFor("Error waiting for rolled up stats", () -> {
            ServiceDocumentQueryResult result = this.host
                    .getExpandedFactoryState(UriUtils.buildUri(this.host,
                            ResourceAggregateMetricService.FACTORY_LINK));
            if (result.documents.size() == 0) {
                return false;
            }
            boolean rightVersion = true;
            for (Object aggrDocument : result.documents.values()) {
                ResourceAggregateMetric aggrMetric = Utils
                        .fromJson(aggrDocument, ResourceAggregateMetric.class);
                if (aggrMetric.documentVersion == 1 && (aggrMetric.timeBin.count
                        == NUM_COLLECTIONS)) {
                    continue;
                }
                rightVersion = false;
            }
            return rightVersion;
        });

        // kick off an aggregation task with a query that resolves to all resources with the specified resource pool link;
        // ensure that we have aggregated data over all raw metric versions
        aggregationTaskState = new SingleResourceStatsAggregationTaskState();
        aggregationTaskState.resourceLink = rpReturnState.documentSelfLink;
        aggregationTaskState.metricNames = new HashSet<>(
                Arrays.asList(MockStatsAdapter.KEY_1, MockStatsAdapter.KEY_2));
        aggregationTaskState.query =
                Query.Builder.create()
                        .addKindFieldClause(ComputeState.class)
                        .addFieldClause(ComputeState.FIELD_NAME_RESOURCE_POOL_LINK,
                        rpReturnState.documentSelfLink).build();
        postServiceSynchronously(SingleResourceStatsAggregationTaskService.FACTORY_LINK,
                aggregationTaskState,
                SingleResourceStatsAggregationTaskState.class);
        this.host.waitFor("Error waiting for rolled up stats", () -> {
            QuerySpecification querySpec = new QuerySpecification();
            querySpec.query = Query.Builder.create()
                    .addKindFieldClause(ResourceAggregateMetric.class)
                    .addFieldClause(ServiceDocument.FIELD_NAME_SELF_LINK,
                            UriUtils.buildUriPath(ResourceAggregateMetricService.FACTORY_LINK,
                                    UriUtils.getLastPathSegment(rpReturnState.documentSelfLink)
                                            + "*"),
                            MatchType.WILDCARD).build();
            querySpec.options.add(QueryOption.EXPAND_CONTENT);
            ServiceDocumentQueryResult result = this.host
                    .createAndWaitSimpleDirectQuery(querySpec, 4, 4);
            boolean rightVersion = true;
            for (Object aggrDocument : result.documents.values()) {
                ResourceAggregateMetric aggrMetric = Utils
                        .fromJson(aggrDocument, ResourceAggregateMetric.class);
                if (aggrMetric.documentVersion == 0 && (aggrMetric.timeBin.count
                        == NUM_COLLECTIONS * NUM_COMPUTE_RESOURCES)) {
                    continue;
                }
                rightVersion = false;
            }
            return rightVersion;
        });

        // kick off an aggregation task with a query that resolves to all resources with the specified resource pool link;
        // ensure that we have aggregated data over latest metric values
        aggregationTaskState = new SingleResourceStatsAggregationTaskState();
        aggregationTaskState.resourceLink = rpReturnState.documentSelfLink;
        aggregationTaskState.latestValueOnly = Stream
                .of(MockStatsAdapter.KEY_1, MockStatsAdapter.KEY_2).collect(Collectors.toSet());

        Map<String, Set<AggregationType>> aggregations = new HashMap<>();
        aggregations.put(MockStatsAdapter.KEY_1, Stream.of(
                AggregationType.AVG, AggregationType.MAX, AggregationType.MIN)
                .collect(Collectors.toSet()));
        aggregations.put(MockStatsAdapter.KEY_2, Stream.of(
                AggregationType.AVG, AggregationType.MAX, AggregationType.MIN)
                .collect(Collectors.toSet()));

        aggregationTaskState.aggregations = aggregations;
        aggregationTaskState.metricNames = new HashSet<>(
                Arrays.asList(MockStatsAdapter.KEY_1, MockStatsAdapter.KEY_2));
        aggregationTaskState.query =
                Query.Builder.create()
                        .addKindFieldClause(ComputeState.class)
                        .addFieldClause(ComputeState.FIELD_NAME_RESOURCE_POOL_LINK,
                        rpReturnState.documentSelfLink).build();
        postServiceSynchronously(SingleResourceStatsAggregationTaskService.FACTORY_LINK,
                aggregationTaskState,
                SingleResourceStatsAggregationTaskState.class);

        this.host.waitFor("Error waiting for rolled up stats", () -> {
            QuerySpecification querySpec = new QuerySpecification();
            querySpec.query = Query.Builder.create()
                    .addKindFieldClause(ResourceAggregateMetric.class)
                    .addFieldClause(ServiceDocument.FIELD_NAME_SELF_LINK,
                            UriUtils.buildUriPath(ResourceAggregateMetricService.FACTORY_LINK,
                                    UriUtils.getLastPathSegment(rpReturnState.documentSelfLink)
                                            + "*"),
                            MatchType.WILDCARD).build();
            querySpec.options.add(QueryOption.EXPAND_CONTENT);
            ServiceDocumentQueryResult result = this.host
                    .createAndWaitSimpleDirectQuery(querySpec, 4, 4);
            boolean rightVersion = true;
            for (Object aggrDocument : result.documents.values()) {
                // The assertion here checks whether we are aggregating only on latest value. To
                // that effect, here is the breakdown for the check:
                // documentVersion = 1: this is the second aggregation that happened. In the
                // previous assertion the document version was 0.
                // count = num of resources: one value for each resource
                // sum = null: not specified in the aggregate type set
                ResourceAggregateMetric aggrMetric = Utils
                        .fromJson(aggrDocument, ResourceAggregateMetric.class);
                if (aggrMetric.documentVersion == 1
                        && aggrMetric.timeBin.count == NUM_COMPUTE_RESOURCES
                        && aggrMetric.timeBin.sum == null) {
                    continue;
                }
                rightVersion = false;
            }
            return rightVersion;
        });

        // verify that the aggregation tasks have been deleted
        this.host.waitFor("Timeout waiting for task to expire", () -> {
            ServiceDocumentQueryResult res =
                    this.host.getFactoryState(UriUtils.buildUri(
                        this.host, SingleResourceStatsAggregationTaskService.FACTORY_LINK));
            if (res.documentLinks.size() == 0) {
                return true;
            }
            return false;
        });

    }
}
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

package com.vmware.photon.controller.model.resources;

import java.util.EnumSet;

import com.vmware.photon.controller.model.UriPaths;
import com.vmware.photon.controller.model.resources.ComputeService.ComputeState;
import com.vmware.photon.controller.model.resources.ResourcePoolService.ResourcePoolState.ResourcePoolProperty;
import com.vmware.xenon.common.Operation;
import com.vmware.xenon.common.ServiceDocumentDescription.PropertyUsageOption;
import com.vmware.xenon.common.StatefulService;
import com.vmware.xenon.common.Utils;
import com.vmware.xenon.services.common.QueryTask.Query;

/**
 * Describes a resource pool. A resource pool is a grouping of {@link ComputeState}s that can be
 * used as a single unit for planning and allocation purposes.
 *
 * <p>{@link ComputeState}s that contribute capacity to this resource pool are found by
 * executing the {@link ResourcePoolState#query} query. For <b>non-elastic</b> resource pools
 * the query is auto-generated by using the {@link ComputeState#resourcePoolLink}. For
 * <b>elastic</b> resource pools the query is provided by the resource pool creator.
 *
 * <p>Thus a resource may participate in at most one non-elastic resource pool and zero or more
 * elastic resource pools.
 */
public class ResourcePoolService extends StatefulService {

    public static final String FACTORY_LINK = UriPaths.RESOURCES + "/pools";

    /**
     * This class represents the document state associated with a
     * {@link ResourcePoolService} task.
     */
    public static class ResourcePoolState extends ResourceState {

        public static final String FIELD_NAME_PROPERTIES = "properties";

        /**
         * Enumeration used to define properties of the resource pool.
         */
        public enum ResourcePoolProperty {
            /**
             * An elastic resource pool uses a dynamic query to find the participating resources.
             * The {@link ComputeState#resourcePoolLink} field of the returned resources may not
             * match this resource pool instance.
             */
            ELASTIC
        }

        /**
         * Project name of this resource pool.
         */
        @UsageOption(option = PropertyUsageOption.AUTO_MERGE_IF_NOT_NULL)
        public String projectName;

        /**
         * Properties of this resource pool, if it is elastic, etc.
         */
        @UsageOption(option = PropertyUsageOption.AUTO_MERGE_IF_NOT_NULL)
        public EnumSet<ResourcePoolProperty> properties;

        /**
         * Minimum number of CPU Cores in this resource pool.
         */
        public long minCpuCount;

        /**
         * Minimum number of GPU Cores in this resource pool.
         */
        public long minGpuCount;

        /**
         * Minimum amount of memory (in bytes) in this resource pool.
         */
        public long minMemoryBytes;

        /**
         * Minimum disk capacity (in bytes) in this resource pool.
         */
        public long minDiskCapacityBytes;

        /**
         * Maximum number of CPU Cores in this resource pool.
         */
        public long maxCpuCount;

        /**
         * Maximum number of GPU Cores in this resource pool.
         */
        public long maxGpuCount;

        /**
         * Maximum amount of memory (in bytes) in this resource pool.
         */
        public long maxMemoryBytes;

        /**
         * Maximum disk capacity (in bytes) in this resource pool.
         */
        public long maxDiskCapacityBytes;

        /**
         * Maximum CPU Cost (per minute) in this resource pool.
         */
        @UsageOption(option = PropertyUsageOption.AUTO_MERGE_IF_NOT_NULL)
        public Double maxCpuCostPerMinute;

        /**
         * Maximum Disk cost (per minute) in this resource pool.
         */
        @UsageOption(option = PropertyUsageOption.AUTO_MERGE_IF_NOT_NULL)
        public Double maxDiskCostPerMinute;

        /**
         * Currency unit used for pricing.
         */
        public String currencyUnit;

        /**
         * Query to use to retrieve resources in this resource pool.
         */
        @UsageOption(option = PropertyUsageOption.AUTO_MERGE_IF_NOT_NULL)
        public Query query;
    }

    public ResourcePoolService() {
        super(ResourcePoolState.class);
        super.toggleOption(ServiceOption.PERSISTENCE, true);
        super.toggleOption(ServiceOption.REPLICATION, true);
        super.toggleOption(ServiceOption.OWNER_SELECTION, true);
        super.toggleOption(ServiceOption.IDEMPOTENT_POST, true);
    }

    @Override
    public void handleStart(Operation start) {
        try {
            processInput(start);
            start.complete();
        } catch (Throwable t) {
            start.fail(t);
        }
    }

    @Override
    public void handlePut(Operation put) {
        try {
            ResourcePoolState returnState = processInput(put);
            setState(put, returnState);
            put.complete();
        } catch (Throwable t) {
            put.fail(t);
        }
    }

    private ResourcePoolState processInput(Operation op) {
        if (!op.hasBody()) {
            throw (new IllegalArgumentException("body is required"));
        }
        ResourcePoolState state = op.getBody(ResourcePoolState.class);
        validateState(state, Action.PUT.equals(op.getAction()));

        if (!state.properties.contains(ResourcePoolProperty.ELASTIC)) {
            state.query = generateResourcePoolQuery(state);
        }
        return state;
    }

    @Override
    public void handlePatch(Operation patch) {
        ResourcePoolState currentState = getState(patch);
        if (!currentState.properties.contains(ResourcePoolProperty.ELASTIC)) {
            // clean auto-generated query to catch patches with unexpected query
            currentState.query = null;
        }

        // apply default merge (including collection update requests)
        boolean hasStateChanged = false;
        try {
            if (Utils.mergeWithState(currentState, patch)) {
                hasStateChanged = true;

                // automatically remove the query if the ELASTIC flag was removed in order to
                // keep the state consistent (the collection update request itself cannot update
                // the query)
                if (!currentState.properties.contains(ResourcePoolProperty.ELASTIC)) {
                    currentState.query = null;
                }
            } else {
                hasStateChanged = ResourceUtils.mergeResourceStateWithPatch(getStateDescription(),
                        currentState, patch.getBody(ResourcePoolState.class));
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            patch.fail(e);
            return;
        }

        // check state and re-generate the query, if needed
        validateState(currentState, true);
        if (!currentState.properties.contains(ResourcePoolProperty.ELASTIC)) {
            currentState.query = generateResourcePoolQuery(currentState);
        }

        if (!hasStateChanged) {
            patch.setStatusCode(Operation.STATUS_CODE_NOT_MODIFIED);
        }
        patch.complete();
    }

    public void validateState(ResourcePoolState state, boolean isUpdateAction) {
        Utils.validateState(getStateDescription(), state);

        if (state.name == null) {
            throw new IllegalArgumentException("Resource pool name is required.");
        }

        if (state.properties == null) {
            state.properties = EnumSet
                    .noneOf(ResourcePoolState.ResourcePoolProperty.class);
        }

        if (state.properties.contains(ResourcePoolProperty.ELASTIC)) {
            if (state.query == null) {
                throw new IllegalArgumentException("Query is required for elastic resource pools.");
            }
        } else {
            if (state.query != null && !isUpdateAction) {
                throw new IllegalArgumentException("Query is auto-generated for " +
                        "non-elastic resource pools.");
            }
        }
    }

    /**
     * Generates a query that finds all computes which resource pool link points to this
     * resource pool. Applicable to non-elastic pools only.
     */
    private Query generateResourcePoolQuery(ResourcePoolState initState) {
        Query query = Query.Builder.create()
                .addKindFieldClause(ComputeState.class)
                .addFieldClause(ComputeState.FIELD_NAME_RESOURCE_POOL_LINK, getSelfLink())
                .build();

        return query;
    }
}

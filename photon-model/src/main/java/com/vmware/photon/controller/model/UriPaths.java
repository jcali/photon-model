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

package com.vmware.photon.controller.model;

import com.vmware.xenon.common.UriUtils;

/**
 * ServiceUriPaths
 *
 * Service paths used by the photon model.
 */
public class UriPaths {
    public static final String PROVISIONING = "/provisioning";
    public static final String RESOURCES = "/resources";
    public static final String RESOURCES_NETWORKS = RESOURCES + "/networks";
    public static final String RESOURCES_NETWORK_INTERFACES = RESOURCES + "/network-interfaces";
    public static final String MONITORING = "/monitoring";

    public static final String PROPERTY_PREFIX = "photon-model.";

    public enum AdapterTypePath {
        INSTANCE_ADAPTER("instance-adapter"),
        NETWORK_ADAPTER("network-adapter"),
        FIREWALL_ADAPTER("firewall-adapter"),
        STATS_ADAPTER("stats-adapter"),
        BOOT_ADAPTER("boot-adapter"),
        POWER_ADAPTER("power-adapter"),
        ENDPOINT_CONFIG_ADAPTER("endpoint-config-adapter"),
        ENUMERATION_ADAPTER("enumeration-adapter"),
        ENUMERATION_CREATION_ADAPTER("enumeration-creation-adapter"),
        ENUMERATION_DELETION_ADAPTER("enumeration-deletion-adapter"),
        COMPUTE_DESCRIPTION_CREATION_ADAPTER("compute-description-creation-adapter"),
        COMPUTE_STATE_CREATION_ADAPTER("compute-state-creation-adapter");

        private final String path;

        private AdapterTypePath(String path) {
            this.path = path;
        }

        public String adapterLink(String endpointType) {
            return UriUtils.buildUriPath(PROVISIONING, endpointType, this.path);
        }
    }
}

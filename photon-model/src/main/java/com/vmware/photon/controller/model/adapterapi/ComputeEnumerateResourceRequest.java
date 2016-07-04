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

package com.vmware.photon.controller.model.adapterapi;

import java.net.URI;

/**
 * Request to enumerate instantiated resources. The {@code resourceReference} value is the URI to
 * the parent compute host.
 */
public class ComputeEnumerateResourceRequest extends ResourceRequest {

    /**
     * Uri reference of the resource pool.
     */
    public String resourcePoolLink;

    /**
     * Reference URI to the Compute Description that will be used for the
     * compute instances created by the adapter.
     */
    public String computeDescriptionLink;

    /**
     * Enumeration Action Start, stop, refresh.
     */
    public EnumerationAction enumerationAction;

    /**
     * URI reference to resource pool management site.
     */
    public URI adapterManagementReference;
}

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

package com.vmware.photon.controller.model.adapters.vsphere.util.finders;

import java.util.List;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.vmware.photon.controller.model.adapters.vsphere.util.connection.Connection;
import com.vmware.vim25.InvalidPropertyFaultMsg;
import com.vmware.vim25.RuntimeFaultFaultMsg;

/**
 */
@RunWith(AutoIgnoreRunner.class)
public class RecurserTest {

    @ClassRule
    public static ConnectionRule rule = new ConnectionRule();
    private Connection connection;

    @Before
    public void setup() {
        this.connection = rule.get();
    }

    @Test
    public void recurse() throws InvalidPropertyFaultMsg, FinderException, RuntimeFaultFaultMsg {

        Recurser recurser = new Recurser(this.connection);
        recurser.setTraverseLeafs(true);

        Element root = Element.asRoot(this.connection.getServiceContent().getRootFolder());
        List<Element> all = recurser.recurse(root, "New Folder", "My*");
        for (Element element : all) {
            System.out.println(element);
        }
    }
}

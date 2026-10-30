/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2026 Payara Foundation and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

package org.jboss.weld.tck.payara;

import fish.payara.arquillian.container.payara.managed.PayaraMicroDeployableContainer;
import org.jboss.arquillian.container.spi.client.container.DeployableContainer;
import org.jboss.arquillian.core.spi.LoadableExtension;

/**
 * Replaces the stock Payara Micro managed container with {@link PayaraMicroTckContainer} so
 * that CDI deployment failures reach Arquillian's {@code @ShouldThrowException} handling.
 */
public class PayaraMicroExtension implements LoadableExtension {

    private static final String MICRO_CONTAINER_CLASS = "fish.payara.arquillian.container.payara.managed.PayaraMicroDeployableContainer";

    @Override
    public void register(ExtensionBuilder builder) {
        if (Validate.classExists(MICRO_CONTAINER_CLASS)) {
            builder.override(DeployableContainer.class, PayaraMicroDeployableContainer.class, PayaraMicroTckContainer.class);
        }
    }
}

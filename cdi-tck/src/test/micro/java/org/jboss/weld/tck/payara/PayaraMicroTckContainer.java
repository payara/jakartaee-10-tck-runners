/*
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

import java.lang.reflect.Field;
import java.util.logging.Logger;

import jakarta.enterprise.inject.spi.DefinitionException;

import fish.payara.arquillian.container.payara.managed.PayaraMicroContainerConfiguration;
import fish.payara.arquillian.container.payara.managed.PayaraMicroDeployableContainer;
import org.jboss.arquillian.container.spi.client.container.DeploymentException;
import org.jboss.arquillian.container.spi.client.protocol.metadata.HTTPContext;
import org.jboss.arquillian.container.spi.client.protocol.metadata.ProtocolMetaData;
import org.jboss.arquillian.container.spi.client.protocol.metadata.Servlet;
import org.jboss.shrinkwrap.api.Archive;

/**
 * Payara Micro managed container that reports CDI deployment failures to Arquillian.
 *
 * When Weld rejects an archive, Payara Micro still starts, prints its "Instance Configuration"
 * banner with an empty "Deployed" list and the stock adapter returns normally. The TCK's
 * {@code @ShouldThrowException} tests (243 classes, all of the {@code broken.*} packages) then
 * fail with "no exception was thrown". This subclass scans the Micro output captured by the
 * adapter after {@link #deploy(Archive)} and rethrows the Weld failure as the jakarta
 * {@link DefinitionException} / {@link jakarta.enterprise.inject.spi.DeploymentException}
 * the tests expect, wrapped in an Arquillian {@link DeploymentException} whose cause
 * Arquillian unwraps when matching.
 *
 * Arquillian does not undeploy when deploy throws, so the Micro process is stopped here
 * first; otherwise every broken deployment would leave an instance behind.
 *
 * The full-server runner (cditck-porting) solves the same problem with a
 * {@code DeploymentExceptionTransformer}, which is not an option here: the Micro adapter raises
 * no exception at all for a failed load, so there is nothing to transform.
 *
 * @see PayaraMicroExtension
 */
public class PayaraMicroTckContainer extends PayaraMicroDeployableContainer {

    private static final Logger LOGGER = Logger.getLogger(PayaraMicroTckContainer.class.getName());

    // Message fragments written by Payara's WeldDeployer; same set as the full-server transformer
    private static final String[] DEPLOYMENT_FAILURE_FRAGMENTS = {
            "CDI deployment failure:",
            "org.jboss.weld.exceptions.DeploymentException",
            "org.jboss.weld.exceptions.UnserializableDependencyException",
            "org.jboss.weld.exceptions.InconsistentSpecializationException",
            "org.jboss.weld.exceptions.NullableDependencyException" };

    private static final String[] DEFINITION_FAILURE_FRAGMENTS = {
            "CDI definition failure:",
            "org.jboss.weld.exceptions.DefinitionException" };

    private static final String LOAD_FAILURE_FRAGMENT = "Exception while loading the app";

    private static final int MAX_MESSAGE_LENGTH = 2000;

    // The adapter keeps the captured Micro output in a private field; tied to arquillian-payara-micro-managed 3.1
    private Field logField;

    @Override
    public void setup(PayaraMicroContainerConfiguration configuration) {
        super.setup(configuration);
        try {
            logField = PayaraMicroDeployableContainer.class.getDeclaredField("log");
            logField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new IllegalStateException(PayaraMicroDeployableContainer.class.getSimpleName()
                    + " has no 'log' field; the arquillian-payara-micro-managed version changed and "
                    + getClass().getSimpleName() + " must be updated, otherwise broken-deployment tests pass silently", e);
        }
    }

    @Override
    public ProtocolMetaData deploy(Archive<?> archive) throws DeploymentException {
        ProtocolMetaData metaData;
        try {
            metaData = super.deploy(archive);
        } catch (DeploymentException e) {
            // "No applications were found deployed" says nothing about why; surface Micro's output
            LOGGER.severe("Deploying " + archive.getName() + " failed (" + e.getMessage() + "). Micro output:\n" + microLog());
            throw e;
        }

        String microLog = microLog();
        if (!microLog.contains(LOAD_FAILURE_FRAGMENT)) {
            if (!hasTestServlet(metaData)) {
                // Seen on Jenkins: the adapter returned before Micro listed the Arquillian servlet, so the
                // test failed with "ArquillianServletRunnerEE9 not found". Restart Micro once.
                LOGGER.severe("Deployed " + archive.getName() + " without an Arquillian servlet in the metadata, "
                        + "restarting Micro once. Micro output:\n" + microLog);
                undeploy(archive);
                metaData = super.deploy(archive);
            }
            return metaData;
        }

        LOGGER.warning("Payara Micro failed to load " + archive.getName() + ", stopping it and reporting the failure");
        undeploy(archive);

        // Classify on the failure line only, as the full-server transformer does on the exception
        // message; the surrounding stack traces mention Weld exception classes of either kind
        String message = failureMessage(microLog);
        if (containsAny(message, DEPLOYMENT_FAILURE_FRAGMENTS)) {
            throw new DeploymentException(message, new jakarta.enterprise.inject.spi.DeploymentException(message));
        }
        if (containsAny(message, DEFINITION_FAILURE_FRAGMENTS)) {
            throw new DeploymentException(message, new DefinitionException(message));
        }
        throw new DeploymentException(message);
    }

    private static boolean hasTestServlet(ProtocolMetaData metaData) {
        HTTPContext context = metaData.getContext(HTTPContext.class);
        if (context == null) {
            return false;
        }
        for (Servlet servlet : context.getServlets()) {
            if (servlet.getName().startsWith("ArquillianServletRunner")) {
                return true;
            }
        }
        return false;
    }

    private String microLog() {
        try {
            String log = (String) logField.get(this);
            return log == null ? "" : log;
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }

    private static boolean containsAny(String log, String[] fragments) {
        for (String fragment : fragments) {
            if (log.contains(fragment)) {
                return true;
            }
        }
        return false;
    }

    /**
     * The first failure line Micro logged, e.g.
     * "CDI definition failure:WELD-000040: All stereotypes must specify the same scope ...".
     */
    private static String failureMessage(String log) {
        int start = -1;
        for (String fragment : new String[] { "CDI deployment failure:", "CDI definition failure:", LOAD_FAILURE_FRAGMENT }) {
            int index = log.indexOf(fragment);
            if (index >= 0 && (start < 0 || index < start)) {
                start = index;
            }
        }
        int end = log.indexOf('\n', start);
        String message = log.substring(start, end < 0 ? log.length() : end).trim();
        return message.length() > MAX_MESSAGE_LENGTH ? message.substring(0, MAX_MESSAGE_LENGTH) : message;
    }
}

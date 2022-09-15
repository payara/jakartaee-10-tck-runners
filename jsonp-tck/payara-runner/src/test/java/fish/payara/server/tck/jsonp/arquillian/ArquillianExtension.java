/*
 *  DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 *  Copyright (c) [2022] Payara Foundation and/or its affiliates. All rights reserved.
 *
 *  The contents of this file are subject to the terms of either the GNU
 *  General Public License Version 2 only ("GPL") or the Common Development
 *  and Distribution License("CDDL") (collectively, the "License").  You
 *  may not use this file except in compliance with the License.  You can
 *  obtain a copy of the License at
 *  https://github.com/payara/Payara/blob/master/LICENSE.txt
 *  See the License for the specific
 *  language governing permissions and limitations under the License.
 *
 *  When distributing the software, include this License Header Notice in each
 *  file and include the License.
 *
 *  When distributing the software, include this License Header Notice in each
 *  file and include the License file at glassfish/legal/LICENSE.txt.
 *
 *  GPL Classpath Exception:
 *  The Payara Foundation designates this particular file as subject to the "Classpath"
 *  exception as provided by the Payara Foundation in the GPL Version 2 section of the License
 *  file that accompanied this code.
 *
 *  Modifications:
 *  If applicable, add the following below the License Header, with the fields
 *  enclosed by brackets [] replaced by your own identifying information:
 *  "Portions Copyright [year] [name of copyright owner]"
 *
 *  Contributor(s):
 *  If you wish your version of this file to be governed by only the CDDL or
 *  only the GPL Version 2, indicate your decision by adding "[Contributor]
 *  elects to include this software in this distribution under the [CDDL or GPL
 *  Version 2] license."  If you don't indicate a single choice of license, a
 *  recipient has the option to distribute your version of this file under
 *  either the CDDL, the GPL Version 2 or to extend the choice of license to
 *  its licensees as provided above.  However, if you add GPL Version 2 code
 *  and therefore, elected the GPL Version 2 license, then the option applies
 *  only if the new code is made subject to such option by the copyright
 *  holder.
 */
package fish.payara.server.tck.jsonp.arquillian;

import ee.jakarta.tck.jsonp.api.common.TestResult;
import ee.jakarta.tck.jsonp.api.provider.JsonProviderTest;
import ee.jakarta.tck.jsonp.common.JSONP_Util;
import ee.jakarta.tck.jsonp.common.MyJsonLocation;
import fish.payara.server.tck.jsonp.propertyextension.ArchiveAppender;
import fish.payara.server.tck.jsonp.propertyextension.ArchiveProcessor;
import jakarta.json.spi.JsonProvider;
import org.jboss.arquillian.container.spi.client.deployment.DeploymentDescription;
import org.jboss.arquillian.container.test.spi.client.deployment.ApplicationArchiveProcessor;
import org.jboss.arquillian.container.test.spi.client.deployment.AuxiliaryArchiveAppender;
import org.jboss.arquillian.container.test.spi.client.deployment.DeploymentScenarioGenerator;
import org.jboss.arquillian.core.spi.LoadableExtension;
import org.jboss.arquillian.test.spi.TestClass;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;

import java.util.Arrays;
import java.util.List;

public class ArquillianExtension implements LoadableExtension {
    @Override
    public void register(ExtensionBuilder extensionBuilder) {
        extensionBuilder.service(DeploymentScenarioGenerator.class, JsonpTckDeploymentPackager.class)
                .service(ApplicationArchiveProcessor.class, ArchiveProcessor.class)
                .service(AuxiliaryArchiveAppender.class, ArchiveAppender.class);
    }

    public static class JsonpTckDeploymentPackager implements DeploymentScenarioGenerator {

        @Override
        public List<DeploymentDescription> generate(TestClass testClass) {
            WebArchive archive = ShrinkWrap.create(WebArchive.class)
                    .addPackages(true,
                            testClass.getJavaClass().getPackage().getName(),
                            MyJsonLocation.class.getPackage().getName(),
                            TestResult.class.getPackage().getName())
                    .addPackages(true, JSONP_Util.class.getPackage().getName())
                    .addAsResource("jsonArrayUTF8.json")
                    .addAsResource("jsonArrayUTF16BE.json")
                    .addAsResource("jsonArrayWithAllTypesOfData.json")
                    .addAsResource("jsonArrayWithAllTypesOfDataUTF16BE.json")
                    .addAsResource("jsonArrayWithEscapeCharsData.json")
                    .addAsResource("jsonArrayWithLotsOfNestedArraysData.json")
                    .addAsResource("jsonArrayWithLotsOfNestedObjectsData.json")
                    .addAsResource("jsonHelloWorld.json")
                    .addAsResource("jsonObjectWithLotsOfNestedObjectsData.json")
                    .addAsResource("jsonObjectWithAllTypesOfDataUTF16LE.json")
                    .addAsResource("jsonObjectEncodingUTF8.json")
                    .addAsResource("jsonObjectWithEscapeCharsData.json")
                    .addAsResource("jsonObjectUTF8.json")
                    .addAsResource("jsonObjectUTF16LE.json")
                    .addAsResource("jsonObjectEncodingUTF16.json")
                    .addAsResource("jsonObjectEncodingUTF16LE.json")
                    .addAsResource("jsonObjectEncodingUTF16BE.json")
                    .addAsResource("jsonObjectEncodingUTF32LE.json")
                    .addAsResource("jsonObjectEncodingUTF32BE.json")
                    .addAsResource("jsonObjectWithAllTypesOfData.json")
                    .addAsResource("jsonObjectUnknownEncoding.json");

            if (testClass.getName().equalsIgnoreCase("ee.jakarta.tck.jsonp.api.provider.JsonProviderTest")) {
                archive = archive.addAsServiceProvider(JsonProvider.class, JsonProviderTest.DummyJsonProvider.class);
            }

            DeploymentDescription dd = new DeploymentDescription("jsonp-tcks", archive);
            return Arrays.asList(dd);
        }
    }
}

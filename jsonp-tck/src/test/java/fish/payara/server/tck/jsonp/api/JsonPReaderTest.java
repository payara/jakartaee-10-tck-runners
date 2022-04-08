package fish.payara.server.tck.jsonp.api;

import ee.jakarta.tck.jsonp.api.common.SimpleValues;
import ee.jakarta.tck.jsonp.api.jsonreadertests.ClientTests;
import ee.jakarta.tck.jsonp.common.JSONP_Util;
import ee.jakarta.tck.jsonp.common.MyBufferedInputStream;
import ee.jakarta.tck.jsonp.common.MyBufferedReader;
import ee.jakarta.tck.jsonp.provider.MyJsonProvider;
import jakarta.json.spi.JsonProvider;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.File;

@ExtendWith(ArquillianExtension.class)
public class JsonPReaderTest extends ClientTests {

    @Deployment
    public static WebArchive createTestArchive() {
        return ShrinkWrap.create(WebArchive.class)
                .addPackages(true, ClientTests.class.getPackage().getName())
                .addPackages(true, MyBufferedInputStream.class.getPackage().getName())
                .addPackages(true, SimpleValues.class.getPackage().getName())
                .addAsResource("jsonsample/jsonArrayUTF8.json", "jsonArrayUTF8.json")
                .addAsResource("jsonsample/jsonArrayUTF16BE.json", "jsonArrayUTF16BE.json")
                .addAsResource("jsonsample/jsonArrayWithAllTypesOfData.json", "jsonArrayWithAllTypesOfData.json")
                .addAsResource("jsonsample/jsonArrayWithAllTypesOfDataUTF16BE.json", "jsonArrayWithAllTypesOfDataUTF16BE.json")
                .addAsResource("jsonsample/jsonArrayWithEscapeCharsData.json", "jsonArrayWithEscapeCharsData.json")
                .addAsResource("jsonsample/jsonArrayWithLotsOfNestedArraysData.json", "jsonArrayWithLotsOfNestedArraysData.json")
                .addAsResource("jsonsample/jsonArrayWithLotsOfNestedObjectsData.json", "jsonArrayWithLotsOfNestedObjectsData.json")
                .addAsResource("jsonsample/jsonHelloWorld.json", "jsonHelloWorld.json")
                .addAsResource("jsonsample/jsonObjectEncodingUTF8.json", "jsonObjectEncodingUTF8.json")
                .addAsResource("jsonsample/jsonObjectEncodingUTF16.json", "jsonObjectEncodingUTF16.json")
                .addAsResource("jsonsample/jsonObjectEncodingUTF16BE.json", "jsonObjectEncodingUTF16BE.json")
                .addAsResource("jsonsample/jsonObjectEncodingUTF16LE.json", "jsonObjectEncodingUTF16LE.json")
                .addAsResource("jsonsample/jsonObjectEncodingUTF32BE.json", "jsonObjectEncodingUTF32BE.json")
                .addAsResource("jsonsample/jsonObjectEncodingUTF32LE.json", "jsonObjectEncodingUTF32LE.json")
                .addAsResource("jsonsample/jsonObjectUnknownEncoding.json", "jsonObjectUnknownEncoding.json")
                .addAsResource("jsonsample/jsonObjectUTF8.json", "jsonObjectUTF8.json")
                .addAsResource("jsonsample/jsonObjectUTF16LE.json", "jsonObjectUTF16LE.json")
                .addAsResource("jsonsample/jsonObjectWithAllTypesOfData.json", "jsonObjectWithAllTypesOfData.json")
                .addAsResource("jsonsample/jsonObjectWithAllTypesOfDataUTF16LE.json", "jsonObjectWithAllTypesOfDataUTF16LE.json")
                .addAsResource("jsonsample/jsonObjectWithEscapeCharsData.json", "jsonObjectWithEscapeCharsData.json")
                .addAsResource("jsonsample/jsonObjectWithLotsOfNestedObjectsData.json", "jsonObjectWithLotsOfNestedObjectsData.json");
    }
}

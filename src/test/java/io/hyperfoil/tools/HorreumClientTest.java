package io.hyperfoil.tools;

import io.hyperfoil.tools.horreum.api.RunService;
import io.hyperfoil.tools.horreum.entity.json.Access;
import io.hyperfoil.tools.yaup.json.Json;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import javax.ws.rs.BadRequestException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.Instant;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class HorreumClientTest extends HorreumTestBase {

    @Test
    @Order(1)
    public void JsonPayload() {

        Instant stop = Instant.now();
        Instant start = stop.minusSeconds(60 * 5);

        Json payload = new Json();
        payload.add("id", "1");
        payload.add("start", start.toString());
        payload.add("stop", stop.toString());
        payload.add("build-hash", "965c96d8d21df38de4e0907efb5437b2ee462088");

        io.hyperfoil.tools.horreum.entity.json.Test test = getExistingtest();

        assertNotNull(test);
        assertEquals(getProperty("horreum.test.description"), test.description);

        RunService.RunCounts runCounts = horreumClient.runService.runCount(test.id);
        assertEquals(0, runCounts.total);

        try {
            horreumClient.runService.addRunFromData("$.start", "$.stop", test.name, getProperty("horreum.test.owner"), Access.PUBLIC, null, null, "test", payload);
        } catch (BadRequestException badRequestException) {
            fail(badRequestException.getMessage() + (badRequestException.getCause() != null ? " : " + badRequestException.getCause().getMessage() : ""));
        }

    }

    @Test
    @Order(2)
    public void ConfigQuickstartTest() {

        String jsonContemt;
        Json payload = null;

        try (InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("data/config-quickstart.jvm.json");) {
            jsonContemt = new BufferedReader(new InputStreamReader(inputStream))
                    .lines().collect(Collectors.joining(" "));
            payload = Json.fromString(jsonContemt);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

        assertNotNull(payload);

        try {
            horreumClient.runService.addRunFromData("$.start", "$.stop", getExistingtest().name, getProperty("horreum.test.owner"), Access.PUBLIC, null, null, "test", payload);
        } catch (BadRequestException badRequestException) {
            fail(badRequestException.getMessage() + (badRequestException.getCause() != null ? " : " + badRequestException.getCause().getMessage() : ""));
        }

    }
}

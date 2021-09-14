package io.hyperfoil.tools;

import io.hyperfoil.tools.horreum.entity.json.Test;
import org.jboss.logging.Logger;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.containers.ContainerState;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.output.OutputFrame;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.Properties;

import static org.junit.Assert.assertNotNull;

public class HorreumTestBase {

    static Properties configProperties;
    public static final String HORREUM_KEYCLOAK_REALM;
    public static final String HORREUM_KEYCLOAK_BASE_URL;
    private static final String HORREUM_BASE_URL;
    public static final String HORREUM_CLIENT_ID;

    public static final String HORREUM_USERNAME;
    public static final String HORREUM_PASSWORD;
    private static final String HORREUM_CLIENT_SECRET;

    protected static boolean START_HORREUM_INFRA;
    protected static boolean CREATE_HORREUM_TEST;
    protected static boolean HORREUM_DUMP_LOGS;

    protected static HorreumClient horreumClient;

    protected static Test newTest;

    private static Logger log = Logger.getLogger(HorreumTestBase.class);

    static {
        configProperties = new Properties();
        InputStream propertyStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("env.properties"); //TODO: make configurable
        try {
            if (propertyStream != null) {
                configProperties.load(propertyStream);

                HORREUM_KEYCLOAK_BASE_URL = getProperty("horreum.keycloak.base-url");
                HORREUM_BASE_URL = getProperty("horreum.base-url");
                HORREUM_CLIENT_ID = getProperty("horreum.client-id");
                HORREUM_KEYCLOAK_REALM = getProperty("keycloak.realm");

                HORREUM_USERNAME = getProperty("horreum.username");
                HORREUM_PASSWORD = getProperty("horreum.password");
                HORREUM_CLIENT_SECRET = getProperty("horreum.client_secret");

                START_HORREUM_INFRA = Boolean.valueOf(getProperty("horreum.start-infra"));
                HORREUM_DUMP_LOGS = Boolean.valueOf(getProperty("horreum.dump-logs"));
                CREATE_HORREUM_TEST = Boolean.valueOf(getProperty("horreum.create-test"));
            } else {
                throw new RuntimeException("Could not load test configuration");
            }
        } catch (IOException ioException) {
            throw new RuntimeException("Failed to load configuration properties");
        }
    }

    public static DockerComposeContainer environment = null;

    protected io.hyperfoil.tools.horreum.entity.json.Test getExistingtest(){
         return horreumClient.testService.get(newTest.id, null);
    }

    protected static String getProperty(String propertyName) {
        return configProperties.getProperty(propertyName).trim();
    }

    @BeforeAll
    public static void beforeClass() throws Exception {
        initialiseRestClients();

        log.info("Starting Infra: " + START_HORREUM_INFRA);
        if (START_HORREUM_INFRA) {

            environment = new DockerComposeContainer(new File("src/test/resources/testcontainers/docker-compose.yml"))
                    .withExposedService("postgres_1", 5432)
            ;
            environment.start();
            //wait for Horreum infrastructure to spin up
            log.info("Waiting for Horreum infrastructure to start");
            Optional<ContainerState> optionalContainer = environment.getContainerByServiceName("horreum_1");
            if (optionalContainer.isPresent()) {
                ContainerState horreumState = optionalContainer.get();
                while (!horreumState.getLogs(OutputFrame.OutputType.STDOUT).contains("started in")) {
                    Thread.sleep(1000);
                }
            } else {
                log.error("Could not find running Horreum container");
            }
        }

        if (CREATE_HORREUM_TEST) {
            createNewTest();
        } else {
            lookupTest();
        }

    }

    private static void createNewTest() {

        Test newTest = new Test();
        newTest.name = getProperty("horreum.test.name");
        newTest.owner = getProperty("horreum.test.owner");
        newTest.description = getProperty("horreum.test.description");

        HorreumTestBase.newTest = horreumClient.testService.add(newTest);
        assertNotNull(newTest);
    }

    private static void initialiseRestClients() {

        horreumClient = new HorreumClient.Builder()
                .horreumUrl(HORREUM_BASE_URL + "/")
                .keycloakUrl(HORREUM_KEYCLOAK_BASE_URL)
                .keycloakRealm(HORREUM_KEYCLOAK_REALM)
                .horreumUser(HORREUM_USERNAME)
                .horreumPassword(HORREUM_PASSWORD)
                .clientId(HORREUM_CLIENT_ID)
                .clientSecret(HORREUM_CLIENT_SECRET)
                .build();

        assertNotNull(horreumClient);

    }

    @AfterAll
    public static void afterClass() throws Exception {
        if (START_HORREUM_INFRA && HORREUM_DUMP_LOGS) {
            Optional<ContainerState> containerState = environment.getContainerByServiceName("horreum_1"); //TODO: dynamic resolve
            if (containerState.isPresent()) {
                String logs = containerState.get().getLogs(OutputFrame.OutputType.STDOUT);
                File tmpFile = File.createTempFile("horreum-client", ".log");
                BufferedWriter writer = new BufferedWriter(new FileWriter(tmpFile));
                writer.write(logs);
                writer.close();
                log.info("Logs written to: " + tmpFile.getAbsolutePath());
            }

        }
        if (environment != null) {
            environment.stop();
            environment = null;
        }
    }

    private static void lookupTest() {
        HorreumTestBase.newTest = horreumClient.testService.get(10, null);
        assertNotNull(newTest);
    }

}

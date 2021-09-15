# Hyperfoil Horreum - Java client
A low-level client library for the Hyperfoil Horreum REST API, incorportaing Keycloack authentication.

Project website: [https://horreum.hyperfoil.io](https://horreum.hyperfoil.io).

## Usage

Instantiating client instance;

        HorreumClient horreumClient = new HorreumClient.Builder()
                .horreumUrl(HORREUM_BASE_URL + "/")
                .keycloakUrl(HORREUM_KEYCLOAK_BASE_URL)
                .keycloakRealm(HORREUM_KEYCLOAK_REALM)
                .horreumUser(HORREUM_USERNAME)
                .horreumPassword(HORREUM_PASSWORD)
                .clientId(HORREUM_CLIENT_ID)
                .clientSecret(HORREUM_CLIENT_SECRET)
                .build();

Adding a new test in Horreum

        Test newTest = new Test();
        newTest.name = "New Test";
        newTest.owner = "dev-team";
        newTest.description = "Example new test definition";

        Test persistedTest = horreumClient.testService.add(newTest);

Uploading a new Run to an existing test, using extractors to extract test meta-data from payload;

        Json newRun = new Json();
        newRun.add("id", "1");
        newRun.add("start", Instant.now().minusSeconds(60 * 5).toString());
        newRun.add("stop", Instant.now().toString());
        newRun.add("build-hash", "965c96d8d21df38de4e0907efb5437b2ee462088");

        try {
            horreumClient.runService.addRunFromData("$.start", "$.stop", newTest.name, getProperty("horreum.test.owner"), Access.PUBLIC, null, null, "test", payload);
        } catch (BadRequestException badRequestException) {
            // handle failed request
        }

## Building

---
**NOTE**

At present, `horreum-client-java` has not been published to maven central. In order to use the client, users need to install the maven artifacts to their local maven repository

---
  
### Building without tests

    $ mvn clean install -DskipTests

### Running Testsuite

The test suite starts the full Horreum architecture, including keycloak and grafana, to allow for testing `horreum-client-java` against a running horreum environment

---
**NOTE**

**In order to run the testsuite, docker needs to be running on your local machine**

---
    
    $ mvn clean verify
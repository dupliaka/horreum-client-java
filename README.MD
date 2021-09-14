# Hyperfoil Horreum - Java client
A low-level client library for the Hyperfoil Horreum REST API, incorportaing Keycloack authentication.

Project website: [https://horreum.hyperfoil.io](https://horreum.hyperfoil.io).

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

        Test newTest = horreumClient.testService.add(newTest);

Uploading a new Run to an existing test, using extractors to extract test meta-data from payload;

        Json newRun = new Json();
        newRun.add("id", "1");
        newRun.add("start", Instant.now().minusSeconds(60 * 5).toString());
        newRun.add("stop", Instant.now().toString());
        newRun.add("build-hash", "965c96d8d21df38de4e0907efb5437b2ee462088");

        try {
            horreumClient.runService.addRunFromData("$.start", "$.stop", newTest.name, getProperty("horreum.test.owner"), Access.PUBLIC, null, null, "test", payload);
        } catch (BadRequestException badRequestException) {
            fail(badRequestException.getMessage() + (badRequestException.getCause() != null ? " : " + badRequestException.getCause().getMessage() : ""));
        }

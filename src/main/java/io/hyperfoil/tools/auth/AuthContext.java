package io.hyperfoil.tools.auth;

public interface AuthContext {

    String getKeycloakBaseUrl();

    String getKeycloakRealm();

    String getUsername();

    String getPassword();

    String getClientId();

    String getClient_secret();

}

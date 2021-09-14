package io.hyperfoil.tools.auth;

public class AuthContextProvider implements AuthContext{
    private String keycloakBaseUrl;
    private String keycloakRealm;
    private String username;
    private String password;
    private String clientId;
    private String clientSecret;

    public AuthContextProvider(String keycloakBaseUrl, String keycloakRealm, String username, String password, String clientId, String clientSecret) {
        this.keycloakBaseUrl = keycloakBaseUrl;
        this.keycloakRealm = keycloakRealm;
        this.username = username;
        this.password = password;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    public String getKeycloakBaseUrl() {
        return keycloakBaseUrl;
    }

    public String getKeycloakRealm() {
        return keycloakRealm;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getClientId() {
        return clientId;

    }

    public String getClient_secret() {
        return clientSecret;
    }

}

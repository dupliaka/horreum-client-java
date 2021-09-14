package io.hyperfoil.tools.auth;

import org.jboss.logging.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import java.util.Optional;

public class KeycloakClientRequestFilter implements ClientRequestFilter {

	private static final Logger LOG = Logger.getLogger(KeycloakClientRequestFilter.class);
	private static final String BEARER_SCHEME_WITH_SPACE = "Bearer ";

	Keycloak keycloak;

	String clientName = "horreum";


	public KeycloakClientRequestFilter(@Context AuthContext keycloakContext) {

		keycloak = KeycloakBuilder.builder()
				.serverUrl(keycloakContext.getKeycloakBaseUrl() + "/auth")
				.realm(keycloakContext.getKeycloakRealm())
				.username(keycloakContext.getUsername())
				.password(keycloakContext.getPassword())
				.clientId(keycloakContext.getClientId())
				.clientSecret(keycloakContext.getClient_secret())
				.resteasyClient(new ResteasyClientBuilder().connectionPoolSize(20).build())
				.build();
	}

	@Override
	public void filter(ClientRequestContext requestContext) {
		try {
			final String accessToken = getAccessToken();
			requestContext.getHeaders().add(HttpHeaders.AUTHORIZATION, BEARER_SCHEME_WITH_SPACE + accessToken);
		} catch (Exception ex) {
			LOG.warnf("Access token is not available, aborting the request with HTTP 401 error: %s", ex.getMessage());
			requestContext.abortWith(Response.status(401).build());
		}
	}

	private String getAccessToken() {
		return keycloak.tokenManager().getAccessToken().getToken();
	}

	protected Optional<String> clientId() {
		return Optional.of(clientName);
	}
}

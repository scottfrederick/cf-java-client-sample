package org.cloudfoundry.sample;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import org.cloudfoundry.client.lib.CloudCredentials;
import org.cloudfoundry.client.lib.CloudFoundryClient;
import org.cloudfoundry.client.lib.domain.*;
import org.cloudfoundry.client.lib.tokens.TokensFile;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.DefaultOAuth2RefreshToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class JavaSample {
    @Parameter(names = { "-t", "--trace" }, description = "Cloud Foundry target URL", required = true)
    private String target;

    @Parameter(names = { "-s", "--space" }, description = "Cloud Foundry space to target", required = true)
    private String spaceName;

    @Parameter(names = { "-o", "--organization" }, description = "Cloud Foundry organization to target")
    private String orgName;

    @Parameter(names = { "-u", "--username" }, description = "Username for login")
    private String username;

    @Parameter(names = { "-p", "--password" }, description = "Password for login")
    private String password;

    @Parameter(names = { "-a", "--accessToken" }, description = "OAuth access token")
    private String accessToken;

    @Parameter(names = { "-r", "--refreshToken" }, description = "OAuth access token")
    private String refreshToken;

    @Parameter(names = { "-c", "--client" }, description = "OAuth client ID")
    private String clientID;

    @Parameter(names = { "-cs", "--clientSecret"}, description = "OAuth client secret")
    private String clientSecret;

    public static void main(String[] args) {
        JavaSample sample = new JavaSample();
        new JCommander(sample, args);
        sample.run();
    }

    private void run() {
        validateArgs();

        CloudCredentials credentials = getCloudCredentials();
        CloudFoundryClient client = getCloudFoundryClient(credentials);

        displayCloudInfo(client);
    }

    private CloudCredentials getCloudCredentials() {
        CloudCredentials credentials;

        if (username != null && password != null) {
            if (clientID == null) {
                credentials = new CloudCredentials(username, password);
            } else {
                credentials = new CloudCredentials(username, password, clientID, clientSecret);
            }
        } else if (accessToken != null && refreshToken != null) {
            DefaultOAuth2RefreshToken refresh = new DefaultOAuth2RefreshToken(refreshToken);
            DefaultOAuth2AccessToken access = new DefaultOAuth2AccessToken(accessToken);
            access.setRefreshToken(refresh);

            if (clientID == null) {
                credentials = new CloudCredentials(access);
            } else {
                credentials = new CloudCredentials(access, clientID, clientSecret);
            }
        } else {
            final TokensFile tokensFile = new TokensFile();
            final OAuth2AccessToken token = tokensFile.retrieveToken(getTargetURI(target));

            if (clientID == null) {
                credentials = new CloudCredentials(token);
            } else {
                credentials = new CloudCredentials(token, clientID, clientSecret);
            }
        }

        return credentials;
    }

    private CloudFoundryClient getCloudFoundryClient(CloudCredentials credentials) {
        out("Connecting to Cloud Foundry target: " + target);

        CloudFoundryClient client = new CloudFoundryClient(credentials, getTargetURL(target), orgName, spaceName);

        if (username != null) {
            client.login();
        }

        return client;
    }

    private void validateArgs() {
        if ((username != null || password != null) && (accessToken != null || refreshToken != null)) {
            error("username/password and accessToken/refreshToken options can not be used together");
        }

        if (bothOrNeither(username, password)) {
            error("username and password options must be provided together");
        }

        if (bothOrNeither(accessToken, refreshToken)) {
            error("accessToken and refreshToken options must be provided together");
        }
    }

    private boolean bothOrNeither(String first, String second) {
        if (first != null || second != null) {
            if (first == null || second == null) {
                return true;
            }
        }
        return false;
    }

    private void displayCloudInfo(CloudFoundryClient client) {
        out("\nInfo:");
        out(client.getCloudInfo().getName());
        out(client.getCloudInfo().getVersion());
        out(client.getCloudInfo().getDescription());

        out("\nSpaces:");
        for (CloudSpace space : client.getSpaces()) {
            out(space.getName() + ":" + space.getOrganization().getName());
        }

        out("\nOrgs:");
        for (CloudOrganization org : client.getOrganizations()) {
            out(org.getName());
        }

        out("\nApplications:");
        for (CloudApplication app : client.getApplications()) {
            out(app.getName() + ":");
            out(app.getStaging().getBuildpackUrl());
            out(app.getStaging().getCommand());
            out("\tServices:");
            for (String serviceName : app.getServices()) {
                out("\t\t" + serviceName);
            }
        }

        out("\nServices:");
        for (CloudService service : client.getServices()) {
            out(service.getName() + ":");
            out("\t" + service.getLabel());
            out("\t" + service.getProvider());
            out("\t" + service.getPlan());
            out("\t" + service.getVersion());
        }

        out("\nService Offerings:");
        for (CloudServiceOffering offering : client.getServiceOfferings()) {
            out(offering.getLabel() + ":");
            out("\t" + offering.getProvider());
            final String s = "\tPlans:";
            out(s);
            for (CloudServicePlan plan : offering.getCloudServicePlans()) {
                out("\t\t" + plan.getName());
            }
            out("\t" + offering.getVersion());
            out("\t" + offering.getDescription());
        }
    }

    private URL getTargetURL(String target) {
        try {
            return getTargetURI(target).toURL();
        } catch (MalformedURLException e) {
            error("The target URL is not valid: " + e.getMessage());
        }

        return null;
    }

    private URI getTargetURI(String target) {
        try {
            return new URI(target);
        } catch (URISyntaxException e) {
            error("The target URL is not valid: " + e.getMessage());
        }

        return null;
    }

    private void out(String s) {
        System.out.println(s);
    }

    private void error(String message) {
        out(message);
        System.exit(1);
    }
}

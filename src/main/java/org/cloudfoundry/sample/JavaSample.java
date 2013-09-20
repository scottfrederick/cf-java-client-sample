package org.cloudfoundry.sample;

import com.esotericsoftware.yamlbeans.YamlReader;
import org.cloudfoundry.client.lib.CloudCredentials;
import org.cloudfoundry.client.lib.CloudFoundryClient;
import org.cloudfoundry.client.lib.domain.*;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;

import java.io.File;
import java.io.FileReader;
import java.net.URI;
import java.net.URL;
import java.util.Map;

public class JavaSample {
    public static void main(String[] args) {
        CloudCredentials credentials;
        String target;

        if (args.length > 0) {
            target = args[0];
        } else {
            Targets targets = getTargetInfo();
            target = targets.keySet().iterator().next();
        }

        if (args.length > 1) {
            String username = args[1];
            String password = args[2];
            credentials = new CloudCredentials(username, password);
        } else {
            Targets targets = getTargetInfo();
            String token = getToken(targets, target);
            credentials = new CloudCredentials(new DefaultOAuth2AccessToken(token));
        }

        String orgName = null;
        if (args.length > 3) {
            orgName = args[3];
        }

        String spaceName = null;
        if (args.length > 4) {
            spaceName = args[4];
        }

        out("Connecting to Cloud Foundry target: " + target);

        CloudFoundryClient client = new CloudFoundryClient(credentials, getTargetURL(target), orgName, spaceName);

        if (args.length > 1) {
            client.login();
        }

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

    private static Targets getTargetInfo() {
        File tokensFile = getTokensFile();
        return getTokensFromFile(tokensFile);
    }

    private static String getToken(Targets targets, String targetUrl) {
        Map<String, String> target = targets.get(targetUrl);

        if (target == null) {
            error("No tokens found in the tokens file for the target " + targetUrl);
        }

        String tokenString = target.get(":token");
        String[] tokens = tokenString.split(" ");

        return tokens[1];
    }

    private static File getTokensFile() {
        String tokensFilePath = System.getProperty("user.home") + "/.cf/tokens.yml";
        File tokensFile = new File(tokensFilePath);

        if (!tokensFile.exists() && !tokensFile.canRead()) {
            error("The Cloud Foundry tokens file " + tokensFile.getPath() + " does not exist or cannot be read. " +
                    "Use the 'cf' command line tool to target and log into a Cloud Foundry service before running this program.");
        }

        return tokensFile;
    }

    @SuppressWarnings("unchecked")
    private static Targets getTokensFromFile(File tokensFile) {
        try {
            YamlReader reader = new YamlReader(new FileReader(tokensFile));
            return reader.read(Targets.class);
        } catch (Exception e) {
            error("An error occurred reading the tokens file at " + tokensFile.getPath() + ":" + e.getMessage());
        }
        return null;
    }

    private static URL getTargetURL(String target) {
        try {
            return new URI(target).toURL();
        } catch (Exception e) {
            out("The target URL is not valid: " + e.getMessage());
        }

        System.exit(1);
        return null;
    }

    private static void out(String s) {
        System.out.println(s);
    }

    private static void error(String message) {
        out(message);
        System.exit(1);
    }
}

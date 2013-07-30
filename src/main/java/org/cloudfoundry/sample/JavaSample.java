package org.cloudfoundry.sample;

import com.esotericsoftware.yamlbeans.YamlReader;
import org.cloudfoundry.client.lib.CloudCredentials;
import org.cloudfoundry.client.lib.CloudFoundryClient;
import org.cloudfoundry.client.lib.domain.*;

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
            credentials = new CloudCredentials(token);
        }

        System.out.println("Connecting to Cloud Foundry target: " + target);

        CloudFoundryClient client = new CloudFoundryClient(credentials, getTargetURL(target));

        if (args.length > 1) {
            client.login();
        }

        System.out.println("\nInfo:");
        System.out.println(client.getCloudInfo().getName());
        System.out.println(client.getCloudInfo().getVersion());
        System.out.println(client.getCloudInfo().getDescription());

        System.out.println("\nSpaces:");
        for (CloudSpace space : client.getSpaces()) {
            System.out.println(space.getName() + ":" + space.getOrganization().getName());
        }

        System.out.println("\nApplications:");
        for (CloudApplication app : client.getApplications()) {
            System.out.println(app.getName());
        }

        System.out.println("\nServices:");
        for (CloudService service : client.getServices()) {
            System.out.println(service.getName() + ":" + service.getLabel());
        }

        System.out.println("\nService Configurations:");
        for (ServiceConfiguration config : client.getServiceConfigurations()) {
            System.out.println(config.getCloudServiceOffering().getLabel() + ":" +
                config.getCloudServiceOffering().getProvider() + ":" +
                config.getCloudServiceOffering().getDescription());
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
            System.out.println("The target URL is not valid: " + e.getMessage());
        }

        System.exit(1);
        return null;
    }

    private static void error(String message) {
        System.out.println(message);
        System.exit(1);
    }
}

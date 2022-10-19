package net.fhirfactory.pegacorn.freemarker;

import java.io.File;
import java.io.IOException;

import freemarker.cache.FileTemplateLoader;
import freemarker.template.Configuration;

/**
 * Configures the Freemarker templating engine.
 * 
 * @author Brendan Douglas
 *
 */
public class TemplateConfiguration {
    private static Configuration cfg;

    private TemplateConfiguration() {
        // Hide the constructor.
    }

    private static void configure() throws IOException {
    	
    	String fileLocation = System.getenv("EMAIL_BODY_CONTENT_FILE_LOCATION") + "/" + System.getenv("KUBERNETES_SERVICE_NAME");
    	
    	 File file = new File(fileLocation);

           	
        cfg = new Configuration(Configuration.VERSION_2_3_29);

        cfg.setTemplateLoader(new FileTemplateLoader(file));
    }

    public static Configuration getConfiguration() throws IOException {
        if (cfg == null) {
            configure();
        }

        return cfg;
    }
}

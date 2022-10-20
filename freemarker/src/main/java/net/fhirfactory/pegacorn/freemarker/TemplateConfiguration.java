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

    private static void configure(String basePath) throws IOException {
    	   	
    	File file = new File(basePath);
   	
        cfg = new Configuration(Configuration.VERSION_2_3_29);

        cfg.setTemplateLoader(new FileTemplateLoader(file));
    }

    public static Configuration getConfiguration(String basePath) throws IOException {
        if (cfg == null) {
            configure(basePath);
        }

        return cfg;
    }
}

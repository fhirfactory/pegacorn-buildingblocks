package net.fhirfactory.pegacorn.freemarker;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * Base class for all content to be generated in Freemarker.
 * 
 * @author Brendan Douglas
 *
 */
public abstract class BaseFreemarkerContent {
	private Map<String, Object> params = new HashMap<>();

    /**
     * 
     */
    public String getContent() throws IOException, TemplateException {
        StringWriter st = new StringWriter();

        Configuration config = TemplateConfiguration.getConfiguration();

        Template template = config.getTemplate(getTemplateName());

        template.process(params, st);

        return st.getBuffer().toString();
    }

    /**
     * The Freemarker template name.
     * 
     * @return
     */
    public abstract String getTemplateName();

    /**
	 * Adds a param to the template.
	 * 
	 * @param key
	 * @param data
	 */
	public void addParam(String key, Object data) {
		params.put(key, data);
	}
}
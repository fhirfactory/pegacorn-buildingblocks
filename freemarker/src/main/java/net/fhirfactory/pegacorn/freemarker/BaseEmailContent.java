package net.fhirfactory.pegacorn.freemarker;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * Base class for all email content to be generated in Freemarker.
 * 
 * @author Brendan Douglas
 *
 */
public abstract class BaseEmailContent {

    /**
     * 
     */
    public String getContent() throws IOException, TemplateException {
        StringWriter st = new StringWriter();

        Configuration config = TemplateConfiguration.getConfiguration();

        Template template = config.getTemplate(getTemplateName());

        template.process(getData(), st);

        return st.getBuffer().toString();
    }

    /**
     * The Freemarker template name.
     * 
     * @return
     */
    public abstract String getTemplateName();

    /**
     * The template data.
     * 
     * @return
     */
    public abstract Map<String, Object> getData();
}
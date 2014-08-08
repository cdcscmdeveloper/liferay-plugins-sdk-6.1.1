/**
 *  Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.util.template;

import java.io.OutputStream;
import java.io.StringWriter;
import java.util.Properties;

import javax.faces.context.FacesContext;

import my.ep.factory.EpLogger;

import org.apache.log4j.Logger;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.xhtmlrenderer.pdf.ITextRenderer;

/**
 * Singleton Helper For Document Generator.
 * 
 * @author kenny
 * @since 07.07.2014
 * 
 */
public class DocumentGenerator {
private static Logger log = EpLogger.getLogger(DocumentGenerator.class);
	
	private static DocumentGenerator instance;
	
	/**
	 * Get singleton of DocumentGenerator
	 * 
	 * @return DocumentGenerator singleton
	 * @throws Exception  
	 */
	public static DocumentGenerator getInstance() throws Exception {
		if (instance == null){
			instance = new DocumentGenerator();
			
			String myTemplatePath = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/WEB-INF/templates");

	    	Properties p = new Properties();
	    	p.setProperty( "file.resource.loader.path", myTemplatePath);
	    	Velocity.init(p);
		}
		return instance;
	}
	
	/**
	 * Generate as html by velocity.
	 * 
	 * @param velocityTemplate
	 * 		the velocity template file name.
	 * @param context
	 * 		the velocity context for parameters to be replace.
	 * @return
	 * @throws Exception
	 */
	public String getHtmlFromVelocity(String velocityTemplate, VelocityContext context) throws Exception {
		debugLogger("getHtmlFromVelocity");
		
    	Template template = Velocity.getTemplate(velocityTemplate);
    	
    	StringWriter output = new StringWriter();
    	
    	template.merge(context, output);
    	
    	debugLogger("html output: " + output);
    	
    	return output.toString();
    }
	
	/**
	 * Generate PDF file by velocity.
	 * 
	 * @param velocityTemplate
	 * 		the velocity template file name.
	 * @param context
	 * 		the velocity context for parameters to be replace.
	 * @param os
	 * 		the output stream for content to be save.
	 * @throws Exception
	 */
	public void generatePdfFromVelocity(String velocityTemplate, VelocityContext context, OutputStream os) throws Exception {
		String html = getHtmlFromVelocity(velocityTemplate, context);
		
		generatePdf(os, html);
	}
	
	/**
	 * Generate PDF file by html.
	 * 
	 * @param os
	 * 		the output stream for content to be save.
	 * @param html
	 * 		the html content to generate as PDF.
	 * @throws Exception
	 */
	public void generatePdf(OutputStream os, String html) throws Exception {
		debugLogger("generatePdf");
		
		ITextRenderer render = new ITextRenderer();
		
        if (html != null) {
        	html = html.replaceAll("&amp;nbsp;", "");
        	html = html.replaceAll("&amp;", "&amp;amp;");
            render.setDocumentFromString(html);
            render.layout();
            render.createPDF(os);
        }
        
        os.flush();
		os.close();
	}
	
	private void debugLogger(Object message) {
        if(log.isDebugEnabled()) {
            log.debug(message);
        }
    }
}

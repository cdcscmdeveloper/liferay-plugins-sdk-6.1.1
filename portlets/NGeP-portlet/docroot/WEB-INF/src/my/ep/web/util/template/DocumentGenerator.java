/**
 *  Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.util.template;

import java.awt.Font;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.math.BigInteger;
import java.util.List;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.portlet.PortletRequest;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.docx4j.jaxb.Context;
import org.docx4j.model.structure.SectionWrapper;
import org.docx4j.openpackaging.contenttype.ContentType;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.exceptions.InvalidFormatException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.PartName;
import org.docx4j.openpackaging.parts.WordprocessingML.AltChunkType;
import org.docx4j.openpackaging.parts.WordprocessingML.AlternativeFormatInputPart;
import org.docx4j.openpackaging.parts.WordprocessingML.FooterPart;
import org.docx4j.openpackaging.parts.WordprocessingML.HeaderPart;
import org.docx4j.openpackaging.parts.WordprocessingML.NumberingDefinitionsPart;
import org.docx4j.relationships.Relationship;
import org.docx4j.wml.Br;
import org.docx4j.wml.CTAltChunk;
import org.docx4j.wml.FldChar;
import org.docx4j.wml.FooterReference;
import org.docx4j.wml.Ftr;
import org.docx4j.wml.Hdr;
import org.docx4j.wml.HdrFtrRef;
import org.docx4j.wml.HeaderReference;
import org.docx4j.wml.HpsMeasure;
import org.docx4j.wml.Jc;
import org.docx4j.wml.JcEnumeration;
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.P;
import org.docx4j.wml.PPr;
import org.docx4j.wml.R;
import org.docx4j.wml.RPr;
import org.docx4j.wml.STFldCharType;
import org.docx4j.wml.SectPr;
import org.docx4j.wml.SectPr.PgMar;
import org.docx4j.wml.Text;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Cleaner;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.liferay.portal.util.PortalUtil;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.BaseFont;

import my.ep.constant.EpPropertiesConst;
import my.ep.factory.EpLogger;
import my.ep.util.EpPropertiesUtils;

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
	private VelocityEngine ve;
	
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
			log.info("myTemplatePath: " + myTemplatePath);

			VelocityEngine ve = new VelocityEngine();
			ve.setProperty(RuntimeConstants.RESOURCE_LOADER,"classpath");
			ve.setProperty("classpath.resource.loader.class",ClasspathResourceLoader.class.getName());
			ve.setProperty("runtime.log.logsystem.class", "org.apache.velocity.runtime.log.NullLogSystem");
			ve.init();
			instance.setVelocityEngine(ve);
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
		log.info("velocityTemplate: " + velocityTemplate);
		log.info("context: " + context);
        
        final String templatePath = "templates" + velocityTemplate;
        InputStream input = this.getClass().getClassLoader().getResourceAsStream(templatePath);
        if (input == null) {
            System.err.println("Template file doesn't exist");
        }
		
    	Template template = getVelocityEngine().getTemplate("templates" + velocityTemplate);
    	log.info("template: " + template);
    	
    	StringWriter output = null;
    	try {
    		output = new StringWriter();
        	template.merge(context, output);
        	debugLogger("html output: " + output);
        	return output.toString();
    	} finally {
    		if(input!=null) {
    			input.close();
    		}
    		if(output!=null) {
    			output.close();
    		}
    	}
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
		log.info("velocityTemplate: " + velocityTemplate);
		String html = getHtmlFromVelocity(velocityTemplate, context);
		try{
			generatePdf(os, html);
		}finally{
			os.close();
		}
		
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
		log.info("Velocity Issue : Generate PDF");
		ITextRenderer render = new ITextRenderer();
		try{
			//#26180
			if (html != null) {
				html = html.replaceAll("&amp;nbsp;", " ");
				html = html.replaceAll("&amp;", "&amp;amp;")
						.replaceAll("&", "&amp;");
				html = html.replaceAll("&amp;#62", "&#62");
				html = html.replaceAll("&amp;#60", "&#60"); 
			//25142 - start
				/*doc = Jsoup.parse(html);
				boolean valid = Jsoup.isValid(doc.html(), Whitelist.relaxed());
				if(!valid){
					cleanDoc = new Cleaner(Whitelist.relaxed()).clean(doc);
					doc = cleanDoc;
				}					
				doc.outputSettings().syntax(Document.OutputSettings.Syntax.xml);
				doc.outputSettings().charset("UTF-8");
			
				html = doc.html();*/
				//20220921@asyrafhaziq - 29076 -  Start
				html = html.replaceAll("&amp;nbsp;", " ");
				html = html.replaceAll("&amp;amp;", "&");
				//20220921@asyrafhaziq - 29076 - End
				/*html = html.replaceAll("&amp;lt", "&#60");
				html = html.replaceAll("&amp;gt", "&#62");*/					
			//#25142 - end
				//#26130 - start
				String fontPath ="";
				if (getServerUrl().contains("NGeP-portlet/")){
					 fontPath = getServerUrl()+"resources/fonts/Arial.ttf";
				}else if(getServerUrl().contains("http://192.168.62.110:8621")){
					fontPath="/usr/share/fonts/msttcore/arial.ttf";
				}
				else {
					fontPath = getServerUrl()+"NGeP-portlet/resources/fonts/Arial.ttf";
				}
				EpPropertiesUtils propertiesUtils = EpPropertiesUtils.getInstance();
				String test1 = propertiesUtils.getProperty(EpPropertiesConst.PORTAL_APPLICATION_URL);
				log.info("zaim font"+test1);
		        render.getFontResolver().addFont(fontPath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
		        //#26130 - end
				render.setDocumentFromString(html);
				render.layout();
				log.info("Velocity Issue : Create PDF");
				render.createPDF(os);
				log.info("Velocity Issue : PDF Created");
			}

			os.flush();
		}finally{
			html = null;
			render = null;
			os.close();
			os = null;
		}
	}
	
	/**
	 * Generate Doc file by velocity.
	 * 
	 * @param velocityTemplate
	 * 		the velocity template file name.
	 * @param context
	 * 		the velocity context for parameters to be replace.
	 * @param filePath
	 * 		the filePath to be save.
	 * @throws Exception
	 */
	public void generateDocFromVelocity(String velocityTemplate, VelocityContext context, String filePath) throws Exception {
		log.info("velocityTemplate: " + velocityTemplate);
		String html = getHtmlFromVelocity(velocityTemplate, context);
		
		generateDoc(filePath, html);
	}
	
	/**
	 * Generate Doc file by html.
	 * 
	 * @param filePath
	 * 		the filePath to be save.
	 * @param html
	 * 		the html content to generate as Doc.
	 * @throws Exception
	 */
	public void generateDocxtoHtml(String filePath, String html) throws Exception {
		debugLogger("generateDoc");
           WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.createPackage();
           try{
        	   NumberingDefinitionsPart ndp = new NumberingDefinitionsPart();
        	   wordMLPackage.getMainDocumentPart().addTargetPart(ndp);
   		    	ndp.unmarshalDefaultNumbering();
   		    	wordMLPackage.getMainDocumentPart().addAltChunk(AltChunkType.Xhtml, html.getBytes());  
   		    	wordMLPackage.save(new java.io.File(filePath));
           }finally{
        	   wordMLPackage = null;
           }
	}
	
	/**
	 * Generate Doc file by html.
	 * 
	 * @param filePath
	 * 		the filePath to be save.
	 * @param html
	 * 		the html content to generate as Doc.
	 * @throws Exception
	 */
	public void generateDoc(String filePath, String html) throws Exception {
		debugLogger("generateDoc");
		WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.createPackage();
		try{
	        AlternativeFormatInputPart afiPart = new AlternativeFormatInputPart(new PartName("/hw.html")); 
	         afiPart.setBinaryData(html.getBytes()); 
	         afiPart.setContentType(new ContentType("text/html")); 
	         Relationship altChunkRel = wordMLPackage.getMainDocumentPart().addTargetPart(afiPart); 
	         // .. the bit in document body 
	         CTAltChunk ac = Context.getWmlObjectFactory().createCTAltChunk(); 
	         ac.setId(altChunkRel.getId() ); 
	         wordMLPackage.getMainDocumentPart().addObject(ac); 
	         wordMLPackage.getContentTypeManager().addDefaultContentType("html", "text/html"); 
	         wordMLPackage.save(new java.io.File(filePath));
		}
		finally{
			// TODO not sure about the WordprocessingML_Package API 
			wordMLPackage = null;
		}
		
	}
	
	/**
	 * The method used to convert the template into document file.
	 */
	public static void convertHtmlToDocFile(String content, String filePath, String docNo) {
		WordprocessingMLPackage wordMLPackage = null;
		try {
			wordMLPackage = WordprocessingMLPackage.createPackage();
			AlternativeFormatInputPart afiPart = new AlternativeFormatInputPart(new PartName("/hw.html"));
			afiPart.setBinaryData(content.getBytes());
			afiPart.setContentType(new ContentType("text/html"));
			Relationship altChunkRel = wordMLPackage.getMainDocumentPart().addTargetPart(afiPart);

			CTAltChunk ac = Context.getWmlObjectFactory().createCTAltChunk();
			ac.setId(altChunkRel.getId());
			wordMLPackage.getMainDocumentPart().addObject(ac);
			wordMLPackage.getContentTypeManager().addDefaultContentType("html","text/html");
			createHeaderPart(wordMLPackage, docNo);
			addFooterToDocument(wordMLPackage);
			setMarginSpace(wordMLPackage);
			wordMLPackage.save(new java.io.File(filePath));
		} catch (InvalidFormatException e) {
			e.printStackTrace();
		} catch (Docx4JException e) {
			e.printStackTrace();
		}finally{
			if(wordMLPackage != null){
				wordMLPackage = null;
			}
		}
	}

	private static void addFooterToDocument(WordprocessingMLPackage wordMLPackage)throws InvalidFormatException {
		ObjectFactory factory = Context.getWmlObjectFactory();
		Relationship relationship = createFooterPart(wordMLPackage, factory);
		createFooterReference(relationship, wordMLPackage, factory);
	}

	private static Relationship createFooterPart(
			WordprocessingMLPackage wordMLPackage, ObjectFactory factory) throws InvalidFormatException {
		FooterPart footerPart = new FooterPart();
		footerPart.setPackage(wordMLPackage);
		footerPart.setJaxbElement(createFooter(factory));

		return wordMLPackage.getMainDocumentPart().addTargetPart(footerPart);
	}

	private static Ftr createFooter(ObjectFactory factory) {
		Ftr footer = factory.createFtr();
		// Page nors and spacing between page numbers
		P pageNumParagraph = factory.createP();
		PPr ppr = new PPr();
		Jc jc = new Jc();
		jc.setVal(JcEnumeration.RIGHT);
		ppr.setJc(jc);
		pageNumParagraph.setPPr(ppr);

		addFieldBegin(factory, pageNumParagraph);
		addPageNumberField(factory, pageNumParagraph, "PAGE");
		addFieldEnd(factory, pageNumParagraph);

		addFieldBegin(factory, pageNumParagraph);
		addPageNumberField(factory, pageNumParagraph, "SYMBOL 32");
		addFieldEnd(factory, pageNumParagraph);

		addFieldBegin(factory, pageNumParagraph);
		addPageNumberField(factory, pageNumParagraph, "Date \\@ 'daripada'");
		addFieldEnd(factory, pageNumParagraph);

		addFieldBegin(factory, pageNumParagraph);
		addPageNumberField(factory, pageNumParagraph, "SYMBOL 32");
		addFieldEnd(factory, pageNumParagraph);

		addFieldBegin(factory, pageNumParagraph);
		addPageNumberField(factory, pageNumParagraph,"SECTIONPAGES");
		addFieldEnd(factory, pageNumParagraph);

		footer.getContent().add(pageNumParagraph);

		// Footer content
		P paragraph = factory.createP();
		R run = factory.createR();
		RPr rpr = new RPr();
		HpsMeasure size = new HpsMeasure();
		size.setVal(BigInteger.valueOf(18));
		rpr.setSz(size);
		run.setRPr(rpr);

		// #26770 - Start.
		Text t1 = factory.createText();
		//#25025 -start
		t1.setValue("Tandatangan & Cap (Kerajaan)  : _____________");
		Br br = factory.createBr(); // this Br element is used break the current and go for next line
		Text t2 = factory.createText();
		t2.setValue("\r\n Tandatangan & Cap (Syarikat ) : _____________");
		Text t3 = factory.createText();
		t3.setValue("\r\n  Pihak Kerajaan : Pegawai yang diberi kuasa oleh Menteri di bawah seksyen 2 Akta Kontrak Kerajaan 1949 untuk menandatangani kontrak");
		// 25025 - End.
		Text t4 = factory.createText();
		t4.setValue("\r\n  Pihak Syarikat  : Penama pada sijil pendaftaran untuk menandatangani Surat ini.");
		run.getContent().add(t1);
//		run.getContent().add(br);
		run.getContent().add(br);
		run.getContent().add(br);
		// #26770 - End.
		run.getContent().add(t2);
		run.getContent().add(br);


		RPr rpr1 = new RPr();
		HpsMeasure sizeContent = new HpsMeasure();
		sizeContent.setVal(BigInteger.valueOf(18));
		rpr1.setSz(sizeContent);
		org.docx4j.wml.Color color = factory.createColor();
		R run1 = factory.createR();
		color.setVal("FF0000");
		rpr1.setColor(color);
		run1.setRPr(rpr1);

		run1.getContent().add(br);
		run1.getContent().add(t3);
		run1.getContent().add(br);
		run1.getContent().add(t4);

		paragraph.getContent().add(run);
		paragraph.getContent().add(run1);
		footer.getContent().add(paragraph);

		return footer;
	}

	private static void createFooterReference(Relationship relationship,
			WordprocessingMLPackage wordMLPackage, ObjectFactory factory) {
		List<SectionWrapper> sections = wordMLPackage.getDocumentModel()
				.getSections();
		SectPr sectionProperties = sections.get(sections.size() - 1)
				.getSectPr();
		// There is always a section wrapper, but it might not contain a sectPr
		if (sectionProperties == null) {
			sectionProperties = factory.createSectPr();
			wordMLPackage.getMainDocumentPart().addObject(sectionProperties);
			sections.get(sections.size() - 1).setSectPr(sectionProperties);
		}

		FooterReference footerReference = factory.createFooterReference();
		footerReference.setId(relationship.getId());
		footerReference.setType(HdrFtrRef.DEFAULT);
		sectionProperties.getEGHdrFtrReferences().add(footerReference);
		FooterReference firstPagefooterRef = factory.createFooterReference();
		firstPagefooterRef.setId(relationship.getId());
		firstPagefooterRef.setType(HdrFtrRef.FIRST);
		sectionProperties.getEGHdrFtrReferences().add(firstPagefooterRef);
	}


	/**
	 * Add header to the document
	 * 
	 * @param wordMLPackage
	 * @throws InvalidFormatException
	 */
	private static  void createHeaderPart(WordprocessingMLPackage wordMLPackage, String docNo)
			throws InvalidFormatException {
		ObjectFactory factory = Context.getWmlObjectFactory();
		HeaderPart headerPart = new HeaderPart();
		headerPart.setPackage(wordMLPackage);

		Hdr header = factory.createHdr();
		P paragraph = factory.createP();
		R run = factory.createR();

		RPr rpr = new RPr();
		HpsMeasure size = new HpsMeasure();
		size.setVal(BigInteger.valueOf(18));
		rpr.setSz(size);
		run.setRPr(rpr);
		Text text = new Text();  
		docNo = docNo == null ? "______________" : docNo; 
		text.setValue("No. Surat Setuju Terima : " +docNo);
		run.getContent().add(text);
		paragraph.getContent().add(run);
		header.getContent().add(paragraph);
		headerPart.setJaxbElement(header);

		Relationship relationship = wordMLPackage.getMainDocumentPart()
				.addTargetPart(headerPart);
		List<SectionWrapper> sections = wordMLPackage.getDocumentModel()
				.getSections();
		SectPr sectionProperties = sections.get(sections.size() - 1)
				.getSectPr();
		// There is always a section wrapper, but it might not contain a sectPr
		if (sectionProperties == null) {
			sectionProperties = factory.createSectPr();
			wordMLPackage.getMainDocumentPart().addObject(sectionProperties);
			sections.get(sections.size() - 1).setSectPr(sectionProperties);
		}

		HeaderReference headerReference = factory.createHeaderReference();
		headerReference.setId(relationship.getId());
		headerReference.setType(HdrFtrRef.DEFAULT);
		sectionProperties.getEGHdrFtrReferences().add(headerReference);
		HeaderReference firstPageHeaderRef = factory.createHeaderReference();
		firstPageHeaderRef.setId(relationship.getId());
		firstPageHeaderRef.setType(HdrFtrRef.FIRST);
		sectionProperties.getEGHdrFtrReferences().add(firstPageHeaderRef);
	}

	/**
	 * Creating the page number field is nearly the same as creating the field
	 * in the TOC example. The only difference is in the value. We use the PAGE
	 * command, which prints the number of the current page, together with the
	 * MERGEFORMAT switch, which indicates that the current formatting should be
	 * preserved when the field is updated.
	 * 
	 * @param paragraph
	 */
	private static void addPageNumberField(ObjectFactory factory, P paragraph, String value) {
		R run = factory.createR();
		Text txt = new Text();
		txt.setSpace("preserve");
		txt.setValue(value);
		run.getContent().add(factory.createRInstrText(txt));
		paragraph.getContent().add(run);
	}

	/**
	 * Every fields needs to be delimited by complex field characters. This
	 * method adds the delimiter that precedes the actual field to the given
	 * paragraph.
	 * 
	 * @param paragraph
	 */
	private static void addFieldBegin(ObjectFactory factory, P paragraph) {
		R run = factory.createR();
		FldChar fldchar = factory.createFldChar();
		fldchar.setFldCharType(STFldCharType.BEGIN);

		run.getContent().add(fldchar);
		paragraph.getContent().add(run);
	}

	/**
	 * Every fields needs to be delimited by complex field characters. This
	 * method adds the delimiter that follows the actual field to the given
	 * paragraph.
	 * 
	 * @param paragraph
	 */
	private static void addFieldEnd(ObjectFactory factory, P paragraph) {
		FldChar fldcharend = factory.createFldChar();
		fldcharend.setFldCharType(STFldCharType.END);
		R run3 = factory.createR();
		run3.getContent().add(fldcharend);
		paragraph.getContent().add(run3);
	}
	
	/**
	 * @return the ve
	 */
	public VelocityEngine getVelocityEngine() {
		return ve;
	}

	/**
	 * @param ve the ve to set
	 */
	public void setVelocityEngine(VelocityEngine ve) {
		this.ve = ve;
	}
	
	public static String getFileName(String fileName) {
	    int index = fileName.lastIndexOf('.');
	    if (index == -1) {
	        return fileName;
	    } else {
	        return fileName.substring(0, index);
	    }
	}

	private void debugLogger(Object message) {
        if(log.isDebugEnabled()) {
            log.debug(message);
        }
    }
	
	public static void main(String[] args) throws FileNotFoundException, DocumentException {
		String html = "<div> <div> <<CONTRACT_PERIOD>> ini dan apa-apa kontrak yang timbul daripadanya hendaklah diertikan mengikut dan dikawal oleh undang-undang Malaysia, dan pembekal bersetuju tertakluk hanya kepada bidang kuasa Mahkamah Malaysia sahaja dalam apa-apa pertikaian atau perselisihan jua pun yang mungkin timbul mengenai &lt;&gt; ini atau apa-apa kontrak yang timbul daripadanya. </div> <div> <br /> </div> </div>";
		String tempHtml = null;
		Elements elements = null;
		Document doc = null;
		ITextRenderer render = new ITextRenderer();
		boolean valid = Jsoup.isValid(html, Whitelist.relaxed().addAttributes("h3", "style"));
		System.out.println("Is Valid: "+ valid);
		/*html = html.replaceAll("&amp;nbsp;", " ");
		html = html.replaceAll("&amp;", "&amp;amp;")
				.replaceAll("&", "&amp;");
		html = html.replaceAll("&amp;#62", "&#62");
		html = html.replaceAll("&amp;#60", "&#60");*/
		System.out.println(html);
		html = html.replaceAll("<<", "&lt;&lt;");
		html = html.replaceAll(">>", "&gt;&gt;");
		doc = Jsoup.parse(html);
		System.out.println(doc.body().html());
		Document cleanDoc = new Cleaner(Whitelist.relaxed()).clean(doc);
		doc = cleanDoc;
		//doc.outputSettings().escapeMode(org.jsoup.nodes.Entities.EscapeMode.xhtml);
		doc.outputSettings().syntax(Document.OutputSettings.Syntax.xml); 
		doc.outputSettings().charset("UTF-8");
		html = doc.body().html();
		
		/*html = html.replaceAll("&amp;nbsp;", " ");
		html = html.replaceAll("&amp;amp;", "&");
		html = html.replaceAll("&amp;lt", "&#60");
		html = html.replaceAll("&amp;gt", "&#62");*/					
		System.out.println(html);
		/*render.setDocumentFromString(html);
		render.layout();
		OutputStream os = new FileOutputStream("C:\\Users\\asyrafali\\Desktop\\test (2).pdf");
		render.createPDF(os);*/
		//elements = doc.getAllElements();
		//System.out.println(doc.html());
		/*html = elements.html();elements.
		for(Element element: elements) {
			if(!element.nodeName().equalsIgnoreCase("br")&&!element.nodeName().equalsIgnoreCase("h1")
				&&!element.nodeName().equalsIgnoreCase("h2")&&!element.nodeName().equalsIgnoreCase("h3")
				&&!element.nodeName().equalsIgnoreCase("h4")&&!element.nodeName().equalsIgnoreCase("h5")
				&&!element.nodeName().equalsIgnoreCase("h6"))
			{
				tempHtml = element.toString();
				if(!tempHtml.contains("</" + element.nodeName() + ">"))
				{
					
					html = html.replace(tempHtml, tempHtml + "</" + element.nodeName() + ">");
					System.out.println("Print: " + html);
				}
			}
			
		}*/
		
		
	}
	
//#25025 -start
	public static void setMarginSpace (WordprocessingMLPackage wordPackage) {
		ObjectFactory factory = Context.getWmlObjectFactory ();
		PgMar pg = factory.createSectPrPgMar ();
		pg.setLeft (BigInteger.valueOf(1000));
		pg.setRight (BigInteger.valueOf(1000));
		wordPackage.getDocumentModel (). getSections (). get (0) .getSectPr (). setPgMar (pg);
	}
	//#25025 - end
	
	//#26130 - start
		private String getServerUrl() {
			ExternalContext extcontext = FacesContext.getCurrentInstance().getExternalContext();
			PortletRequest portletRequest = (PortletRequest) extcontext.getRequest();
			HttpServletRequest httpServletRequest = PortalUtil.getOriginalServletRequest(PortalUtil.getHttpServletRequest(portletRequest));
			String requestURL = httpServletRequest.getRequestURL().toString();
			String url = requestURL.substring(0, requestURL.length() - httpServletRequest.getRequestURI().length()) +  httpServletRequest.getContextPath() + "/";
			return url;
		}
		//#26130-end
}


// ***25201

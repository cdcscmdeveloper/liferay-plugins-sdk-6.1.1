/**
 * Copyright � 2012 Archisoft. All rights reserved.
*/
package my.ep.web.util.template;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import my.ep.biz.service.tpl.doc.ITemplateDocSO;
import my.ep.constant.pm.ParameterConst;
import my.ep.dto.tpl.doc.TemplateDocReqDTO;
import my.ep.persistence.entity.tpl.doc.TplDocMaster;
import my.ep.persistence.entity.tpl.doc.TplDocSection;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.primefaces.model.TreeNode;

/**
 * * Merge multiple documents into one document with indent.
 */

public class MergeDocx {
	private static Logger log = Logger.getLogger(MergeDocx.class);
	private static final String CONTENT_TYPE_PDF = "application/pdf";

	
	private static String insertNumberingAndIndentHtml(String text, String indexKey, int templateLevel){		
		String newText = "";
		text = text.replaceAll("&nbsp;", "&#160;");
		int width = 5 + (templateLevel-2)*3 ;
		
		switch (templateLevel) {
		case 1:
			newText = "<table width=\"100%\"><tr><td width=\"5%\" valign=\"top\">" + indexKey + "</td><td width=\"95%\">" + text + "</td></tr></table>";
			break;
			
		default:
			newText = "<table width=\"100%\"><tr><td width=\""+width+"%\">&#160;</td><td width=\"5%\" valign=\"top\">" + indexKey + "</td><td width=\""+(95-width)+"%\">" + text + "</td></tr></table>";
			
		}
		return newText;
	}

	// #38714 - Start
	/**
	 * @param tplDocSection
	 * @return
	 */
	private static boolean isNeedToModify(Long docMasterId) {
		ITemplateDocSO templateSO = (ITemplateDocSO) BusinessDelegate.getInstance().getServiceObject("cc",
				ITemplateDocSO.class);

		TemplateDocReqDTO reqDTOS = new TemplateDocReqDTO();
		reqDTOS.setDocMasterId(docMasterId);
		TplDocMaster tplDocMaster = templateSO.getTemplateMaster(reqDTOS).getTplDocMaster();

		String processType = tplDocMaster.getProcessType().getParameterCode();
		String templateType = tplDocMaster.getTemplateType().getParameterCode();

		if (!tplDocMaster.isRecordStatus() || processType == null || templateType == null) {
			return false;
		}

		// Define allowed pairs (processType - list of procurement categories)
		Map<String, List<String>> allowedPairs = new HashMap<>();
		allowedPairs.put(ParameterConst.TPL_DOC_PROCESS_TYPE_SOURCING,
				Arrays.asList(ParameterConst.TEMPLATE_TYPE_MINISTRY));

		List<String> allowedTemplates = allowedPairs.get(processType);
		if (allowedTemplates == null) {
			return false;
		}
		// Case-insensitive check
		for (String allowedTemplate : allowedTemplates) {
			if (allowedTemplate.equalsIgnoreCase(templateType)) {
				return true;
			}
		}
		return false;
	}
	// #38714 -end-

	// #38714 -start-
	/**
	 * @param text
	 * @param indexKey
	 * @param templateLevel
	 * @return
	 */
	private static String qtInsertNumberingAndIndentHtml(String text, String indexKey, int templateLevel) {

		String newText = "";
		text = text.replaceAll("&nbsp;", "&#160;");

		int width = 5 + (templateLevel - 2) * 5;
		if (width < 5) {
			width = 5;
		}
		if (templateLevel > 3) {
			width = width + 1;
		}
		if (templateLevel == 1) {
			newText = "<table width=\"100%\"><tr>" + "<td width=\"5%\" valign=\"top\">" + indexKey + "</td>"
					+ "<td width=\"95%\">" + text + "</td>" + "</tr></table>";
		} else {
			newText = "<table width=\"100%\"><tr>" + "<td width=\"" + width + "%\">&#160;</td>"
					+ "<td width=\"5%\" valign=\"top\">" + indexKey + "</td>" + "<td width=\"" + (95 - width) + "%\">"
					+ text + "</td>" + "</tr></table>";
		}

		return newText;
	}
	// #38714 - End

	public static void getDocSectionList(int level,TreeNode node,List<TplDocSection> inputStreamList){
		if(node != null && node.getData() != null){
			for(TreeNode parent : node.getChildren()){
				TplDocSection docSec = (TplDocSection) parent.getData();
				docSec.setSectionLevel(level);
				inputStreamList.add(docSec);
				if(!parent.isLeaf()){
					getDocSectionList(level+1,parent,inputStreamList);
					
				}
			}
		}
	}
	
	
	public static String mergePreviewAction(TreeNode root, String outputFileName, String action) throws Exception {
		//action= P-Preview, S-Save
		if(log.isDebugEnabled()){
			log.debug("Start mergePreviewAction");
		}
	
		List<TplDocSection> inputStreamList = new ArrayList<TplDocSection>();
		getDocSectionList(1,root,inputStreamList);

		String combinedContent = "";
		String combinedContentHtml="";
		Document doc = null;
		Elements elements = null;
		boolean isNeedToModify = isNeedToModify(inputStreamList.get(0).getTplDocMaster().getDocMasterId()); // #38714

		for (TplDocSection obj : inputStreamList) {
			String text = "";
			if(obj.getSectionTitle() != null && !obj.getSectionTitle().isEmpty()){
				text += "<span><strong>" + obj.getSectionTitle() + "</strong></span><br />";
			}
			
			if(obj.getSectionContent() != null && !obj.getSectionContent().isEmpty()){
				
				obj.setSectionContent(obj.getSectionContent().trim().replace("Apple-tab-span", "Apple-converted-space"));
				
				if(obj.getSectionContent().trim().startsWith("<p") && obj.getSectionContent().trim().endsWith("</p>")){
					doc = Jsoup.parse(obj.getSectionContent());
					elements = doc.getElementsByTag("p");
					for(Element element: elements) {
						element.clearAttributes();
						element.replaceWith(new Element("span"));
					}
					
				}
				
				text += obj.getSectionContent();
			}

			// #38714 - Start
			if (isNeedToModify) {
				text = qtInsertNumberingAndIndentHtml(text, obj.getSectionNumber() != null ? obj.getSectionNumber()
						: " ", obj.getSectionLevel());
			} else {
				text = insertNumberingAndIndentHtml(text,
						obj.getSectionNumber() != null ? obj.getSectionNumber() : " ", obj.getSectionLevel());
			}
			// #38714 - End

			combinedContent += text;
		}
		
		if(action.equals("P")){
						
			//=========================== START PDF ======================================
			//log.info("combinedContent ==== " + combinedContent);
			
			//combinedContentHtml = "<html><body>" 
					//+ combinedContent + "</body></html>";	
			
	    	//=========================== END PDF ======================================
			doc = Jsoup.parse(combinedContent);
			for(int i = 1; i<=6; i++) {
          		elements = doc.getElementsByTag("h" + i);
				for(Element element: elements) {
					element.clearAttributes();
				};
          	 }
			
			combinedContentHtml = doc.html().replace("<br>", "<br/>");
			
		}
		
		if(action.equals("S")){
//			DocumentSettingsPart dsp = target.getMainDocumentPart().getDocumentSettingsPart();
//			ObjectFactory factory = new ObjectFactory();
//			CTDocProtect protection = factory.createCTDocProtect();
//			protection.setEdit(STDocProtect.READ_ONLY);
//			protection.setEnforcement(false);
//			dsp.getJaxbElement().setDocumentProtection(protection);					
//			target.save(new java.io.File(templateFilePath+outputFileName+".docx"));
		}
		
		
    	if(log.isDebugEnabled()){
			log.debug("End mergePreviewAction");
		}
    	
    	System.out.println("==============combinedContentHtml================="+combinedContentHtml);    	
		return combinedContentHtml;

	}
	
}

/**
 * Copyright � 2012 Archisoft. All rights reserved.
*/
package my.ep.web.util.template;

import java.util.ArrayList;
import java.util.List;

import my.ep.persistence.entity.tpl.doc.TplDocSection;

import org.apache.log4j.Logger;
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
		for (TplDocSection obj : inputStreamList) {
			String text = "";
			if(obj.getSectionTitle() != null && !obj.getSectionTitle().isEmpty()){
				text += "<span><strong>" + obj.getSectionTitle() + "</strong></span><br />";
			}
			
			if(obj.getSectionContent() != null && !obj.getSectionContent().isEmpty()){
				
				obj.setSectionContent(obj.getSectionContent().trim().replace("Apple-tab-span", "Apple-converted-space"));
				
				if(obj.getSectionContent().trim().startsWith("<p>") && obj.getSectionContent().trim().endsWith("</p>")){
					obj.setSectionContent(obj.getSectionContent().trim().replaceFirst("<p>", "<span>"));
					obj.setSectionContent(obj.getSectionContent().trim().substring(0,obj.getSectionContent().trim().length()-4)+ "</span>");
				}
				text += obj.getSectionContent();
			}
						
			text = insertNumberingAndIndentHtml(text,  obj.getSectionNumber(), obj.getSectionLevel());
			
			combinedContent += text;
		}
		
		if(action.equals("P")){
						
			//=========================== START PDF ======================================
//			log.info("combinedContent ==== " + combinedContent);
			
			combinedContentHtml = "<html><body>" 
					+ combinedContent + "</body></html>";	
			
	    	//=========================== END PDF ======================================
			
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
		return combinedContentHtml;

	}
	
}

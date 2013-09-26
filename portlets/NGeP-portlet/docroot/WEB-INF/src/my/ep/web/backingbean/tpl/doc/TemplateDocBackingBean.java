/**
 * Copyright © 2012 Archisoft. All rights reserved.
*/
package my.ep.web.backingbean.tpl.doc;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import my.ep.biz.dto.tpl.doc.TemplateDocReqDTO;
import my.ep.biz.dto.tpl.doc.TemplateDocResDTO;
import my.ep.biz.service.tpl.doc.ITemplateDocSO;
import my.ep.persistence.entity.tpl.TplCategory;
import my.ep.persistence.entity.tpl.doc.TplDocMaster;
import my.ep.persistence.entity.tpl.doc.TplDocSection;
import my.ep.type.tpl.doc.TemplateType;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.tpl.doc.TemplateDocManagedBean;
import my.ep.web.util.DateUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.template.MergeDocx;

import org.apache.log4j.Logger;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;


/**
 * Backing Bean for Template Document
 * 
 * @author Melinda
 * @created 15.10.2012
 * @see my.ep.web.backingbean.BaseBackingBean
 */
@ManagedBean(name = "templateDocBackingBean")
@RequestScoped
public class TemplateDocBackingBean extends BaseBackingBean<TemplateDocManagedBean> {
	private boolean uncheck = false;
	private static Logger log = Logger.getLogger(TemplateDocBackingBean.class);

	/* navigation */	
	private static final String NAV_TO_MAINT = "templateDocMaint";

	public TemplateDocBackingBean() {
		if(this.getManagedBean().getTemplateTypeList()==null) {
			ITemplateDocSO templateSO = (ITemplateDocSO)BusinessDelegate.getInstance().getServiceObject(ITemplateDocSO.class);
//			TemplateDocResDTO resDTO = templateSO.getOptionChoices();
//			if(resDTO.isSuccessful()) {
//				this.getManagedBean().setTemplateTypeList(toSelectItems(resDTO.getTemplateTypeList()));
//				this.getManagedBean().setTemplateTypeMap(toSelectItemMap(resDTO.getTemplateTypeList()));
			}
		}
//	}
	
//	private List<SelectItem> toSelectItems(List<GenDropdown> dropdownList) {
//		List<SelectItem> itemList = new ArrayList<SelectItem>();
//		itemList.add(new SelectItem("", "- Select One -"));
//		for(GenDropdown dropdown : dropdownList) {
//			itemList.add(new SelectItem(dropdown.getDropdownCode(), dropdown.getDropdownName()));
//		}
//		return itemList;
//	}
	
//	private Map<String, String> toSelectItemMap(List<GenDropdown> dropdownList) {
//		Map<String, String> itemMap = new HashMap<String, String>();
//		for(GenDropdown dropdown : dropdownList) {
//			itemMap.put(dropdown.getDropdownCode(), dropdown.getDropdownName());
//		}
//		return itemMap;
//	}	
	
	public void positionNode(String position) {
		TreeNode selectedNode = getManagedBean().getDocSectionMap().get(getManagedBean().getSelectedSection());
		TreeNode parentNode = selectedNode.getParent();
		if(position.equalsIgnoreCase("left")) {
			if(parentNode!=null && !parentNode.getType().equalsIgnoreCase("root")) {
				selectedNode.setParent(parentNode.getParent());
				rePosition(parentNode);
			} else {
				System.out.println("Cannot move as parent is ROOT.");
			}
		} else if(position.equalsIgnoreCase("right")) {
			// First item in the level cannot indent to right
			int index = parentNode.getChildren().indexOf(selectedNode);
			if(index>0) {
				selectedNode.setParent(parentNode.getChildren().get(index-1));
				parentNode.getChildren().get(index-1).setExpanded(true);
			} else {
				System.out.println("First item in the level cannot indent to right");
			}
		} else if(position.equalsIgnoreCase("up")) {
			// First item in the level cannot move up
			int index = parentNode.getChildren().indexOf(selectedNode);
			if(index>0) {
				parentNode.getChildren().add(index-1, selectedNode);
				parentNode.getChildren().remove(index+1);
			} else {
				System.out.println("First item in the level cannot move up");
			}			
		} else if(position.equalsIgnoreCase("down")) {
			// last item in the level cannot move down
			int index = parentNode.getChildren().indexOf(selectedNode);
			if(index!=(parentNode.getChildCount()-1)) {
				parentNode.getChildren().add(index+2, selectedNode);
				parentNode.getChildren().remove(index);
			} else {
				System.out.println("Last item in the level cannot move down");
			}			
		}
	}

	public void rePosition(TreeNode node) {
		TreeNode parentNode = node.getParent();
		int position = parentNode.getChildren().indexOf(node);
		parentNode.getChildren().add(position+1, parentNode.getChildren().get(parentNode.getChildCount()-1));
		parentNode.getChildren().remove(parentNode.getChildCount()-1);
	}

	public List<SelectItem> getFlowList() {
		List<SelectItem> flowList = new ArrayList<SelectItem>();
		flowList.add(new SelectItem("", "- Select One -"));
		flowList.add(new SelectItem("M", "Modify Template"));		
		return flowList;
	}

	public List<SelectItem> getTemplateCategoryList() {
		List<SelectItem> templateCategoryList = new ArrayList<SelectItem>();

		ITemplateDocSO templateSO = (ITemplateDocSO)BusinessDelegate.getInstance().getServiceObject(ITemplateDocSO.class);		
		TemplateDocResDTO resDTO = templateSO.getTemplateCategoryList(null);
		if(resDTO.getCategoryList() != null && !resDTO.getCategoryList().isEmpty()){
			templateCategoryList.add(new SelectItem("", "- Select One -"));
			List<TplCategory> categoryListDB = resDTO.getCategoryList();
			for(TplCategory obj : categoryListDB){
				templateCategoryList.add(new SelectItem(obj.getCategoryId(), obj.getCategoryDesc()));
			}
		}		
		return templateCategoryList;
	}

	public List<SelectItem> getTemplateNameList() {
		List<SelectItem> templateNameList = new ArrayList<SelectItem>();

		if(getManagedBean().getCategoryId() != null && !getManagedBean().getCategoryId().equals("")){
			ITemplateDocSO templateSO = (ITemplateDocSO)BusinessDelegate.getInstance().getServiceObject(ITemplateDocSO.class);

			TemplateDocReqDTO reqDTO = new TemplateDocReqDTO();
			reqDTO.setCategoryId(getManagedBean().getCategoryId());
			reqDTO.setTemplateType(null);

			TemplateDocResDTO resDTO = templateSO.getTemplateMasterList(reqDTO);
			if(resDTO.getDocMasterList() != null && !resDTO.getDocMasterList().isEmpty()){	
				Long templateMasterSeq = null;
				List<TplDocMaster> docMasterListDB = resDTO.getDocMasterList();
				for(TplDocMaster obj : docMasterListDB){
					templateNameList.add(new SelectItem(obj.getDocMasterId(), obj.getDocName()));
					if(templateMasterSeq == null){
						templateMasterSeq = obj.getDocMasterId();
					}
				}
				getManagedBean().setDocMasterId(templateMasterSeq);
				getManagedBean().setShowTemplateList(true);
			}	
		}
		return templateNameList;
	}

	public String goToMaint() {
		ITemplateDocSO templateSO = (ITemplateDocSO)BusinessDelegate.getInstance().getServiceObject(ITemplateDocSO.class);
		TemplateDocReqDTO reqDTO = new TemplateDocReqDTO();
		reqDTO.setDocMasterId(getManagedBean().getSelectedTemplate().getDocMasterId());

		TemplateDocResDTO resDTO = templateSO.getTemplateSectionListByMaster(reqDTO);
		List<TplDocSection> templateSectionList = resDTO.getDocSectionList();
		TplDocMaster tplDocMaster = resDTO.getTplDocMaster();
		if(tplDocMaster.getTplCategory() == null){
			tplDocMaster.setTplCategory(getManagedBean().getSelectedTemplate().getTplCategory());
		}
		getTemplateDatatableList(templateSectionList);
		getManagedBean().setSelectedTemplate(tplDocMaster);
		getManagedBean().setDeletedList(new ArrayList<TplDocSection>());
		return NAV_TO_MAINT;
	}

	private void getTemplateDatatableList(List<TplDocSection> templateSectionList){
		if(templateSectionList != null && !templateSectionList.isEmpty()){
			getManagedBean().setDocSectionMap(new HashMap<TplDocSection,TreeNode>());
			getManagedBean().setDocSectionTN(new DefaultTreeNode("root","", null));

			Map<Long,TreeNode> secSeqMap= new HashMap<Long, TreeNode>();
			TreeNode node = null;
			for(TplDocSection objL1 : templateSectionList){
				if(objL1.getParent()==null){
					node = new DefaultTreeNode(objL1, getManagedBean().getDocSectionTN());
					getManagedBean().getDocSectionMap().put(objL1, node);
					secSeqMap.put(objL1.getDocSectionId(), node);
				}else {
					node = new DefaultTreeNode(objL1, secSeqMap.get(objL1.getParent().getDocSectionId()));
					getManagedBean().getDocSectionMap().put(objL1, node);
					secSeqMap.put(objL1.getDocSectionId(), node);
				}
			}
		}
	}

	public void editContent() {
		try {
			if (getManagedBean().getSelectedSection() != null) {
				TplDocSection selectedSection = getManagedBean().getSelectedSection();
				getManagedBean().setSelectedSectionName(selectedSection.getSectionName());
				getManagedBean().setSelectedSectionTitle(selectedSection.getSectionTitle());				
				getManagedBean().setSelectedSectionContent(selectedSection.getSectionContent());
			}

			if(getManagedBean().getVariableList() != null && !getManagedBean().getVariableList().isEmpty()){
				getManagedBean().setSelectedOption("2");
			}else {
				getManagedBean().setSelectedOption("1");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void saveContent() {
		try	{	
			String sectionTitle = getManagedBean().getSelectedSectionTitle();
			String sectionText = getManagedBean().getSelectedSectionContent();	
			String sectionName = getManagedBean().getSelectedSectionName();

			TplDocSection selectedSection = getManagedBean().getSelectedSection();
			selectedSection.setSectionTitle(sectionTitle);
			selectedSection.setSectionContent(sectionText);
			selectedSection.setSectionName(sectionName);

		} catch (Exception e) {
			e.printStackTrace();
		}		
	}

	public void reNumbering(TreeNode root) {
		int index = 1;
		for (TreeNode node : root.getChildren()) {			
			TplDocSection docSection= (TplDocSection)node.getData()  ;
			docSection.setIndex(index);

			StringBuilder key = new StringBuilder();
			if (node.getParent().getData() != null && !node.getParent().getType().equalsIgnoreCase("root")) {
				TplDocSection docSecNum= (TplDocSection)node.getParent().getData()  ;
				key.append(docSecNum.getSectionNumber());
				if(key.length() > 0){
					key.append(".");
				}
			}
			key.append(index);
			docSection.setSectionNumber(key.toString());
			if (!node.getChildren().isEmpty()) {
				reNumbering(node);
			}
			index++;
		}
	}

	public void previewAction() {
		try {					
			String outputFileName = "Document" + "_"+ DateUtils.dateToString("yyMMddHHmmssZ", new Date());
			String combinedContentHtml = MergeDocx.mergePreviewAction(getManagedBean().getDocSectionTN(),outputFileName,"P");		    
			getManagedBean().setPreviewValue(combinedContentHtml);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	

	public void createTemplateAction() {		
		save("newTemplate");
	}	

	public void createDraftAction() {
		save("newDraft");		
	}	

	public void saveAction() {
		save("update");		
	}	

	private TplDocSection cloneObj(TplDocSection obj){
		TplDocSection newObj = new TplDocSection();		
		newObj.setBrandNew(true);
		newObj.setTplDocMaster(obj.getTplDocMaster());
		newObj.setSectionName(obj.getSectionName());
		newObj.setSectionTitle(obj.getSectionTitle());
		newObj.setSectionContent(obj.getSectionContent());
		newObj.setSectionNumber(obj.getSectionNumber());
		newObj.setSectionLevel(obj.getSectionLevel());
		newObj.setNumbering(true);	
		newObj.setChangedBy(obj.getCreatedBy());	
		newObj.setChangedBy(obj.getChangedBy());
		return newObj;
	}

	public List<SelectItem> getVariableList() {
		List<SelectItem> variableList = new ArrayList<SelectItem>();
		variableList.add(new SelectItem("", "- Select One -"));
		variableList.add(new SelectItem("1001, 7005, 5552", "Kod Bidang"));
		variableList.add(new SelectItem("Mr. Abc Cde", "Supplier Name"));	
		variableList.add(new SelectItem("40", "Supplier Age"));	
		variableList.add(new SelectItem("23, Block A, Jalan A, KL", "Supplier Address"));	
		return variableList;
	}

	public void deleteNode() {  
		TreeNode deleteNode = getManagedBean().getDocSectionMap().get(getManagedBean().getSelectedSection());
		deleteNode.getParent().getChildren().remove(deleteNode);
		getManagedBean().getDeletedList().add((TplDocSection) deleteNode.getData());
	} 

	public void addNode() {
		FacesScopes facesScopes = new FacesScopes();
		Timestamp date = new Timestamp(System.currentTimeMillis());
		TreeNode selectedNode = getManagedBean().getDocSectionMap().get(getManagedBean().getSelectedSection());
		if (selectedNode != null) {
			TplDocSection docSection = new TplDocSection();
			docSection.setTplDocMaster(((TplDocSection)selectedNode.getData()).getTplDocMaster());
			docSection.setSectionName("[Section]");
			docSection.setSectionContent("");
			docSection.setChangedBy(1l);
			docSection.setBrandNew(Boolean.TRUE);
			docSection.setCreatedDate(date);
			TreeNode node = new DefaultTreeNode(docSection,selectedNode.getParent());
			getManagedBean().getDocSectionMap().put((TplDocSection)node.getData(), node);
			rePosition(selectedNode);
		}
	}

	public void searchResult(){
		if(getManagedBean().getCategoryId() != null && !getManagedBean().getCategoryId().equals("")){
			ITemplateDocSO templateSO = (ITemplateDocSO)BusinessDelegate.getInstance().getServiceObject(ITemplateDocSO.class);

			TemplateDocReqDTO reqDTO = new TemplateDocReqDTO();
			reqDTO.setCategoryId(getManagedBean().getCategoryId());
			reqDTO.setTemplateType(null);

			TemplateDocResDTO resDTO = templateSO.getTemplateMasterList(reqDTO);

			if(resDTO.getDocMasterList() != null && !resDTO.getDocMasterList().isEmpty()){	
				Long templateMasterSeq = null;
				List<TplDocMaster> docMasterListDB = resDTO.getDocMasterList();
				this.getManagedBean().setTemplateMasterList(docMasterListDB);
				for(TplDocMaster masterSeq : docMasterListDB){
					templateMasterSeq = masterSeq.getDocMasterId();
				}
				getManagedBean().setDocMasterId(templateMasterSeq);
				getManagedBean().setShowTemplateList(true);
			}	
		}
	}

	public void viewContent(){
		log.info("========view content started=====" + getManagedBean().getSelectedOption());
	}

	public void searchImportSect() {
		try {
			if (getManagedBean().getSelectedSection() != null) {
				TplDocSection selectedSection = getManagedBean().getSelectedSection();
				getManagedBean().setSelectedSectionName(selectedSection.getSectionName());
				getManagedBean().setSelectedSectionTitle(selectedSection.getSectionTitle());				
				getManagedBean().setSelectedSectionContent(selectedSection.getSectionContent());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String save(String ind) {		
		FacesScopes facesScopes = new FacesScopes();
		try{
			TplDocMaster tplDocMasterE = getManagedBean().getSelectedTemplate();
			TplDocMaster tplDocMaster = null;	
			if(ind.equals("newTemplate")){
				tplDocMaster = new TplDocMaster();
				tplDocMaster.setDocName("Library");				
				tplDocMaster.setTemplateType(TemplateType.MINISTRY.value());
				tplDocMaster.setCreatedBy(1l);
				tplDocMaster.setChangedBy(1l);
				tplDocMaster.setBrandNew(true);
			}

			if(ind.equals("newDraft")){
				tplDocMaster = new TplDocMaster();
				tplDocMaster.setDocName("Draft");				
				tplDocMaster.setTemplateType(TemplateType.DRAFT.value());
				tplDocMaster.setCreatedBy(1l);
				tplDocMaster.setChangedBy(1l);
				tplDocMaster.setBrandNew(true);
			}

			if(ind.equals("update")){
				tplDocMaster = tplDocMasterE;
				tplDocMaster.setChangedBy(1l);
				tplDocMaster.setBrandNew(false);
				tplDocMaster.setTplCategory(tplDocMasterE.getTplCategory());
				tplDocMaster.setDocName(tplDocMaster.getDocTitle()+" - MD");
				tplDocMaster.setTemplateType(TemplateType.DRAFT.value());
			}
			tplDocMaster.setCategoryId(tplDocMasterE.getCategoryId());
			tplDocMaster.setDocTitle(tplDocMasterE.getDocTitle());
			tplDocMaster.setDocDesc(tplDocMasterE.getDocDesc());
			tplDocMaster.setDocFooter(tplDocMasterE.getDocFooter());

			List<TplDocSection> docSectionList = new ArrayList<TplDocSection>();
			getDocSectionList(1,getManagedBean().getDocSectionTN(),docSectionList);
			for(int i = 0 ;i < docSectionList.size(); i++){
				TplDocSection sectionOrder = docSectionList.get(i);
				sectionOrder.setSectionOrder(i);
			}
			tplDocMaster.setTplDocSectionCollection(docSectionList);

			List<TplDocSection> deletedSectionList = new ArrayList<TplDocSection>();
			if(getManagedBean().getDeletedList() != null && !getManagedBean().getDeletedList().isEmpty()){
				for(TplDocSection genTemplateSection : getManagedBean().getDeletedList()){
					genTemplateSection.setTplDocMaster(tplDocMaster);
					genTemplateSection.setDeleted(true);
					deletedSectionList.add(genTemplateSection);
				}
			}
			tplDocMaster.setDeletedCollection(deletedSectionList);

			ITemplateDocSO templateSO = (ITemplateDocSO)BusinessDelegate.getInstance().getServiceObject(ITemplateDocSO.class);
			TemplateDocReqDTO reqDTO = new TemplateDocReqDTO();
			reqDTO.setTplDocMaster(tplDocMaster);

			TemplateDocResDTO resDTO = templateSO.save(reqDTO);
			if (resDTO.isSuccessful()) {
				TemplateDocReqDTO reqDTOS = new TemplateDocReqDTO();
				reqDTOS.setDocMasterId(getManagedBean().getDocMasterId());						
				TemplateDocResDTO resDTOS = templateSO.getTemplateSectionListByMaster(reqDTOS);
				List<TplDocSection> templateSectionList = resDTOS.getDocSectionList();
				TplDocMaster tplDocMasterDB = resDTOS.getTplDocMaster();
				getTemplateDatatableList(templateSectionList);
				getManagedBean().setSelectedTemplate(tplDocMasterDB);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return "templateDocSearch";		
	}	

	public static void getDocSectionList(int level,TreeNode node,List<TplDocSection> docSetionList){
		if(node != null && node.getData() != null){
			for(TreeNode parent : node.getChildren()){
				TplDocSection docSec = (TplDocSection) parent.getData();
				docSec.setSectionLevel(level);
				if(!node.getType().equalsIgnoreCase("root")) {
					docSec.setParent((TplDocSection) node.getData());
				}else{
					docSec.setParent(null);
				}
				docSetionList.add(docSec);
				if(!parent.isLeaf()){
					getDocSectionList(level+1,parent,docSetionList);
				}
			}
		}
	}

	public String createNewTemplate(){
		FacesScopes facesScopes = new FacesScopes();
		TplDocMaster docMaster = new TplDocMaster();
		docMaster.setBrandNew(Boolean.TRUE);
		docMaster.setCreatedBy(1l);
		docMaster.setCreatedDate(new Timestamp(System.currentTimeMillis()));
		getManagedBean().setSelectedTemplate(docMaster);
		getManagedBean().setDocSectionMap(new HashMap<TplDocSection,TreeNode>());
		TreeNode selectedNode = (new DefaultTreeNode("root","", null));
		getManagedBean().setDocSectionTN(selectedNode);
		for(int i = 1; i <= 5;i++){
			TplDocSection docSection = new TplDocSection();
			docSection.setTplDocMaster(docMaster);
			docSection.setSectionNumber(i+"");
			docSection.setSectionName("Section" + i);
			docSection.setCreatedBy(1l);
			docSection.setBrandNew(Boolean.TRUE);
			docSection.setCreatedDate(new Timestamp(System.currentTimeMillis()));
			TreeNode node = new DefaultTreeNode(docSection,selectedNode);
			getManagedBean().getDocSectionMap().put((TplDocSection)node.getData(), node);
		}
		return NAV_TO_MAINT;
	}

	public void imptSearchResult(){
		if(getManagedBean().getImpCategoryId() != null && !getManagedBean().getImpCategoryId().equals("")){
			ITemplateDocSO templateSO = (ITemplateDocSO)BusinessDelegate.getInstance().getServiceObject(ITemplateDocSO.class);

			TemplateDocReqDTO reqDTO = new TemplateDocReqDTO();
			reqDTO.setCategoryId(getManagedBean().getImpCategoryId());
			reqDTO.setTemplateType(null);

			TemplateDocResDTO resDTO = templateSO.getTemplateMasterList(reqDTO);

			if(resDTO.getDocMasterList() != null && !resDTO.getDocMasterList().isEmpty()){	
				Long templateMasterSeq = null;
				List<TplDocMaster> docMasterListDB = resDTO.getDocMasterList();
				this.getManagedBean().setImportMasterList(docMasterListDB);
				for(TplDocMaster masterSeq : docMasterListDB){
					templateMasterSeq = masterSeq.getDocMasterId();
				}
				getManagedBean().setDocMasterId(templateMasterSeq);
				getManagedBean().setShowTemplateList(true);
			}	
		}
	}

	public void selectedSectionTree(){
		ITemplateDocSO templateSO = (ITemplateDocSO)BusinessDelegate.getInstance().getServiceObject(ITemplateDocSO.class);
		TemplateDocReqDTO reqDTO = new TemplateDocReqDTO();
		reqDTO.setDocMasterId(getManagedBean().getSelectedImpMaster().getDocMasterId());

		TemplateDocResDTO resDTO = templateSO.getTemplateSectionListByMaster(reqDTO);
		List<TplDocSection> templateSectionList = resDTO.getDocSectionList();
		TplDocMaster tplDocMaster = resDTO.getTplDocMaster();
		if(tplDocMaster.getTplCategory() == null){
			tplDocMaster.setTplCategory(getManagedBean().getSelectedImpMaster().getTplCategory());
		}
		getImportedDatatableList(templateSectionList);
		getManagedBean().setSelectedImpMaster(tplDocMaster);
		getManagedBean().setImportedSectionList(null);
	}
	
	private void getImportedDatatableList(List<TplDocSection> templateSectionList){
		if(templateSectionList != null && !templateSectionList.isEmpty()){
			getManagedBean().setImptSectionTN(new DefaultTreeNode("root","", null));

			Map<Long,TreeNode> secSeqMap= new HashMap<Long, TreeNode>();
			TreeNode node = null;
			for(TplDocSection objL1 : templateSectionList){
				if(objL1.getParent()==null){
					node = new DefaultTreeNode(objL1, getManagedBean().getImptSectionTN());
					secSeqMap.put(objL1.getDocSectionId(), node);
				}else {
					node = new DefaultTreeNode(objL1, secSeqMap.get(objL1.getParent().getDocSectionId()));
					secSeqMap.put(objL1.getDocSectionId(), node);
				}
			}
		}
	}

	public void addSelectedImptNodes() {
		FacesScopes facesScopes = new FacesScopes();
		Timestamp date = new Timestamp(System.currentTimeMillis());
		TreeNode selectedNode = getManagedBean().getDocSectionMap().get(getManagedBean().getSelectedSection());
		for(TplDocSection docSection : getManagedBean().getImportedSectionList()){
			docSection = cloneObj(docSection);
			docSection.setTplDocMaster(getManagedBean().getSelectedTemplate());
			docSection.setCreatedBy(1l);
			docSection.setCreatedDate(date);
			TreeNode node = new DefaultTreeNode(docSection,selectedNode.getParent());
			getManagedBean().getDocSectionMap().put((TplDocSection)node.getData(), node);
		}
		importRePosition(selectedNode,getManagedBean().getImportedSectionList().size());
	}
	
	public void initializeImport(ActionEvent e){
		getManagedBean().setImpCategoryId(null);
		getManagedBean().setImportMasterList(new ArrayList<TplDocMaster>());
		getManagedBean().setImptSectionTN(new DefaultTreeNode());
		getManagedBean().setSelectedImpMaster(null);
		getManagedBean().setSelectedImpSection(null);
	}
	
	public void checkRow(TplDocSection docSection){
		if(getManagedBean().getImportedSectionList() == null){
			getManagedBean().setImportedSectionList(new ArrayList<TplDocSection>());
		}

		if(getManagedBean().getImportedSectionList().contains(docSection)){
			getManagedBean().getImportedSectionList().remove(docSection);
		}else{
			getManagedBean().getImportedSectionList().add(docSection);
		}
	}

	public boolean isUncheck() {
		return uncheck;
	}

	public void setUncheck(boolean uncheck) {
		this.uncheck = uncheck;
	}
	
	public void importRePosition(TreeNode node, int sectionSize) {
		TreeNode parentNode = node.getParent();
		int position = parentNode.getChildren().indexOf(node);
		
		for(int i = 1;i <= sectionSize;i++){
			parentNode.getChildren().add(position+i, parentNode.getChildren().get(parentNode.getChildCount()-(sectionSize+1-i)));
		}
		for(int i = 1;i <= sectionSize;i++){
			parentNode.getChildren().remove(parentNode.getChildCount()-1);
		}
	}
	
	public void generatePDF(){
//			PDFGenerator.generatePDF(this.getManagedBean().getPreviewValue());
		
	}
	
	public void combinePDF(){
//		PDFGenerator.execute();
		try {
//			PDFGenerator.simpleImages();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
}
	
}

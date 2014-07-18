/**
 * Copyright © 2012 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.tpl.doc;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.portlet.PortletResponse;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBException;

import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.ct.agreement.IAgreementSO;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.tpl.doc.ITemplateDocSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.dto.ct.agreement.ContractAgreementReqDTO;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
import my.ep.dto.tpl.doc.TemplateDocReqDTO;
import my.ep.dto.tpl.doc.TemplateDocResDTO;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.tpl.TplCategory;
import my.ep.persistence.entity.tpl.doc.TplDocMaster;
import my.ep.persistence.entity.tpl.doc.TplDocSection;
import my.ep.persistence.entity.tpl.spec.TplSpecMaster;
import my.ep.type.aut.SearchOperator;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.tpl.doc.TemplateDocManagedBean;
import my.ep.web.util.DateUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.template.MergeDocx;

import org.apache.log4j.Logger;
import org.docx4j.XmlUtils;
import org.docx4j.convert.in.xhtml.XHTMLImporter;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.NumberingDefinitionsPart;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfPTable;


/**
 * Backing Bean for Template Document
 * 
 * @author Melinda
 * @created 15.10.2012
 * @see my.ep.web.backingbean.BaseBackingBean
 */
@ManagedBean(name = "templateDocBackingBean")
@RequestScoped
public class TemplateDocBackingBean extends
		BaseBackingBean<TemplateDocManagedBean> {

	

	private boolean uncheck = false;
	private static Logger log = Logger.getLogger(TemplateDocBackingBean.class);

	/* navigation */
	private static final String NAV_TO_MAINT = "/xhtml/tpl/doc/templateDocMaint.xhtml";

	public TemplateDocBackingBean() {
		if (this.getManagedBean().getTemplateTypeList() == null) {
			ITemplateDocSO templateSO = (ITemplateDocSO) BusinessDelegate
					.getInstance().getServiceObject("cc", ITemplateDocSO.class);
		}
		if (this.getManagedBean().getTemplateCategoryList() == null) {

			ITemplateDocSO templateSO = (ITemplateDocSO) BusinessDelegate
					.getInstance().getServiceObject("cc", ITemplateDocSO.class);
			System.out.println("templateSO: " + templateSO);
			TemplateDocResDTO resDTO = templateSO.getTemplateCategoryList(null);

			List<TplCategory> categoryListDB = resDTO.getCategoryList();
			this.getManagedBean().setTemplateCategoryList(
					toSelectItems(resDTO.getCategoryList()));

		}
		if (this.getManagedBean().getTemplateProcessTypeList() == null) {
			SelectItem[] procesCodes = getDropdownCodeValues(ParameterTypeConst.TPL_DOC_PROCESS_TYPE);
			/*if(procesCodes !=null){
				List<SelectItem>procesCode=Arrays.asList(procesCodes);*/
			this.getManagedBean().setTemplateProcessTypeList(procesCodes);
			
		}
		
		getManagedBean().setCategoryId(null);
		getManagedBean().setProcessType(null);

	}

	private List<SelectItem> toSelectItems(List<TplCategory> dropdownList) {
		List<SelectItem> itemList = new ArrayList<SelectItem>();
		itemList.add(new SelectItem("", "- Select One -"));
		if(null!=dropdownList){
			for (TplCategory dropdown : dropdownList) {
				itemList.add(new SelectItem(dropdown.getCategoryId(), dropdown
						.getCategoryDesc()));
			}
		}
		
		return itemList;
	}
	
	
	@SuppressWarnings("unused")
	private List<SelectItem> toTplSpecDesc(List<TplSpecMaster> dataTableList) {
		List<SelectItem> specDescList = new ArrayList<SelectItem>();
		for (TplSpecMaster specDesc : dataTableList) {
			specDescList.add(new SelectItem(specDesc.getProcessType(), specDesc
					.getSpecDesc()));
		}
		return specDescList;
	}
	


	public void positionNode(String position) {
		TreeNode selectedNode = getManagedBean().getDocSectionMap().get(
				getManagedBean().getSelectedSection());
		TreeNode parentNode = selectedNode.getParent();
		if (position.equalsIgnoreCase("left")) {
			if (parentNode != null
					&& !parentNode.getType().equalsIgnoreCase("root")) {
				selectedNode.setParent(parentNode.getParent());
				rePosition(parentNode);
			} else {
				System.out.println("Cannot move as parent is ROOT.");
			}
		} else if (position.equalsIgnoreCase("right")) {
			// First item in the level cannot indent to right
			int index = parentNode.getChildren().indexOf(selectedNode);
			if (index > 0) {
				selectedNode.setParent(parentNode.getChildren().get(index - 1));
				parentNode.getChildren().get(index - 1).setExpanded(true);
			} else {
				System.out
						.println("First item in the level cannot indent to right");
			}
		} else if (position.equalsIgnoreCase("up")) {
			// First item in the level cannot move up
			int index = parentNode.getChildren().indexOf(selectedNode);
			if (index > 0) {
				parentNode.getChildren().add(index - 1, selectedNode);
				parentNode.getChildren().remove(index + 1);
			} else {
				System.out.println("First item in the level cannot move up");
			}
		} else if (position.equalsIgnoreCase("down")) {
			// last item in the level cannot move down
			int index = parentNode.getChildren().indexOf(selectedNode);
			if (index != (parentNode.getChildCount() - 1)) {
				parentNode.getChildren().add(index + 2, selectedNode);
				parentNode.getChildren().remove(index);
			} else {
				System.out.println("Last item in the level cannot move down");
			}
		}
	}

	public void rePosition(TreeNode node) {
		TreeNode parentNode = node.getParent();
		int position = parentNode.getChildren().indexOf(node);
		parentNode.getChildren().add(position + 1,
				parentNode.getChildren().get(parentNode.getChildCount() - 1));
		parentNode.getChildren().remove(parentNode.getChildCount() - 1);
	}

	public List<SelectItem> getFlowList() {
		List<SelectItem> flowList = new ArrayList<SelectItem>();
		flowList.add(new SelectItem("", "- Select One -"));
		flowList.add(new SelectItem("M", "Modify Template"));
		return flowList;
	}

	public List<SelectItem> getTemplateCategoryList() {
		List<SelectItem> templateCategoryList = new ArrayList<SelectItem>();
		ITemplateDocSO templateSO = (ITemplateDocSO) BusinessDelegate
				.getInstance().getServiceObject("cc", ITemplateDocSO.class);
		System.out.println("templateSO: " + templateSO);
		TemplateDocResDTO resDTO = templateSO.getTemplateCategoryList(null);
		if (resDTO.getCategoryList() != null
				&& !resDTO.getCategoryList().isEmpty()) {
			templateCategoryList.add(new SelectItem("", "- Select One -"));
			List<TplCategory> categoryListDB = resDTO.getCategoryList();
			for (TplCategory obj : categoryListDB) {
				templateCategoryList.add(new SelectItem(obj.getCategoryId(),
						obj.getCategoryDesc()));
			}
		}
		return templateCategoryList;
	}


	
	public List<SelectItem> getTemplateNameList() {
		List<SelectItem> templateNameList = new ArrayList<SelectItem>();

		if (getManagedBean().getCategoryId() != null
				&& !getManagedBean().getCategoryId().equals("")) {
			ITemplateDocSO templateSO = (ITemplateDocSO) BusinessDelegate
					.getInstance().getServiceObject("cc", ITemplateDocSO.class);

			TemplateDocReqDTO reqDTO = new TemplateDocReqDTO();
			reqDTO.setCategoryId(getManagedBean().getCategoryId());
			reqDTO.setTemplateType(null);

			TemplateDocResDTO resDTO = templateSO.getTemplateMasterList(reqDTO);
			if (resDTO.getDocMasterList() != null
					&& !resDTO.getDocMasterList().isEmpty()) {
				Long templateMasterSeq = null;
				List<TplDocMaster> docMasterListDB = resDTO.getDocMasterList();
				for (TplDocMaster obj : docMasterListDB) {
					templateNameList.add(new SelectItem(obj.getDocMasterId(),
							obj.getDocName()));
					if (templateMasterSeq == null) {
						templateMasterSeq = obj.getDocMasterId();
					}
				}
				getManagedBean().setDocMasterId(templateMasterSeq);
				getManagedBean().setShowTemplateList(true);
			}
		}
		return templateNameList;
	}
	
	
	public String goToMaint(TplDocMaster selTemplate) {       
		
		ITemplateDocSO templateSO = (ITemplateDocSO) BusinessDelegate
				.getInstance().getServiceObject("cc", ITemplateDocSO.class);
		TemplateDocReqDTO reqDTO1 = new TemplateDocReqDTO();
		
		getManagedBean().setSelectedTemplate(selTemplate);
		
		System.out.println("==============>>>>>>>>>>>>>>>> " + getManagedBean().getSelectedTemplate());
		
		reqDTO1.setDocMasterId(getManagedBean().getSelectedTemplate()
				.getDocMasterId());
//		TemplateDocResDTO resDTO = templateSO
//				.getTemplateCategoryById(reqDTO1);
		TemplateDocResDTO resDTO1= templateSO.getTemplateSectionListByMaster(reqDTO1);
		List<TplDocSection> templateSectionList = resDTO1.getDocSectionList();
		TplDocMaster tplDocMaster1 = resDTO1.getTplDocMaster();
//				if (tplDocMaster1.getTplCategory() == null) {
//			tplDocMaster1.setTplCategory(getManagedBean().getSelectedTemplate()
//					.getTplCategory());
//		}
//		if(tplDocMaster1.getProcessType() == null){
//			tplDocMaster1.setProcessType(getManagedBean().getSelectedTemplate().getProcessType());
//		}
		getTemplateDatatableList(templateSectionList);
		getManagedBean().setSelectedTemplate(tplDocMaster1);
		getManagedBean().setDeletedList(new ArrayList<TplDocSection>());
//		TplDocMaster tplDocMaster = new TplDocMaster();
//		TemplateDocReqDTO reqDTO = new TemplateDocReqDTO();
//		reqDTO.setTplDocMaster(this.getManagedBean().getSelectedTemplate());
		getManagedBean().setProcessType(selTemplate.getProcessTypeCode().getParameterCode());

		return NAV_TO_MAINT;
	}

public void reset() {  
	this.getManagedBean().setTemplateSearchList(null);
	this.getManagedBean().setCategoryId(null);
	this.getManagedBean().setTitle(null);
	this.getManagedBean().setDesc(null);
	
	
} 


public void destroyWorld(ActionEvent actionEvent){  
    FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "System Error",  "Please try again later.");  
      
    FacesContext.getCurrentInstance().addMessage(null, message);  
}  

	private void getTemplateDatatableList(
			List<TplDocSection> templateSectionList) {
		if (templateSectionList != null && !templateSectionList.isEmpty()) {
			getManagedBean().setDocSectionMap(
					new HashMap<TplDocSection, TreeNode>());
			getManagedBean().setDocSectionTN(
					new DefaultTreeNode("root", "", null));

			Map<Long, TreeNode> secSeqMap = new HashMap<Long, TreeNode>();
			TreeNode node = null;
			for (TplDocSection objL1 : templateSectionList) {
				if (objL1.getParent() == null) {
					node = new DefaultTreeNode(objL1, getManagedBean()
							.getDocSectionTN());
					getManagedBean().getDocSectionMap().put(objL1, node);
					secSeqMap.put(objL1.getDocSectionId(), node);
				} else {
					node = new DefaultTreeNode(objL1, secSeqMap.get(objL1
							.getParent().getDocSectionId()));
					getManagedBean().getDocSectionMap().put(objL1, node);
					secSeqMap.put(objL1.getDocSectionId(), node);
				}
			}
		}
	}

	public void editContent() {
		try {
			if (getManagedBean().getSelectedSection() != null) {
				TplDocSection selectedSection = getManagedBean()
						.getSelectedSection();
				getManagedBean().setSelectedSectionName(
						selectedSection.getSectionName());
				getManagedBean().setSelectedSectionTitle(
						selectedSection.getSectionTitle());
				getManagedBean().setSelectedSectionText(
						selectedSection.getSectionContent());
			}

			if (getManagedBean().getVariableList() != null
					&& !getManagedBean().getVariableList().isEmpty()) {
				getManagedBean().setSelectedOption("2");
			} else {
				getManagedBean().setSelectedOption("1");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void saveContent() {
		try {
			String sectionTitle = getManagedBean().getSelectedSectionTitle();
			String sectionText = getManagedBean().getSelectedSectionText();
			String sectionName = getManagedBean().getSelectedSectionName();
			String sectionNumber = getManagedBean().getSelectedSection().getSectionNumber();
			TplDocSection selectedSection = getManagedBean()
					.getSelectedSection();
			selectedSection.setSectionTitle(sectionTitle);
			selectedSection.setSectionContent(sectionText);
			selectedSection.setSectionName(sectionName);
			selectedSection.setSectionNumber(sectionNumber);

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
			String outputFileName = "Document" + "_"
					+ DateUtils.dateToString("yyMMddHHmmssZ", new Date());
			String combinedContentHtml = MergeDocx.mergePreviewAction(
					getManagedBean().getDocSectionTN(), outputFileName, "P");
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

	public String saveAction() {
		return save("update");
	}

	private TplDocSection cloneObj(TplDocSection obj) {
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
		variableList.add(new SelectItem("23, Block A, Jalan A, KL",
				"Supplier Address"));
		return variableList;
	}

	public void deleteNode() {
		
		TreeNode deleteNode = getManagedBean().getDocSectionMap().get(
				getManagedBean().getSelectedSection());
		deleteNode.getParent().getChildren().remove(deleteNode);
		getManagedBean().getDeletedList().add(
				(TplDocSection) deleteNode.getData());
		
	}

	public void addNode() {
		FacesScopes facesScopes = new FacesScopes();
		Timestamp date = new Timestamp(System.currentTimeMillis());
		TreeNode selectedNode = getManagedBean().getDocSectionMap().get(
				getManagedBean().getSelectedSection());
		if (selectedNode != null) {
			TplDocSection docSection = new TplDocSection();
			docSection.setTplDocMaster(((TplDocSection) selectedNode.getData())
					.getTplDocMaster());
			docSection.setSectionName("[Section]");
			docSection.setSectionContent("");
			docSection.setChangedBy(1l);
			docSection.setBrandNew(Boolean.TRUE);
			docSection.setCreatedDate(date);
			TreeNode node = new DefaultTreeNode(docSection,
					selectedNode.getParent());
			getManagedBean().getDocSectionMap().put(
					(TplDocSection) node.getData(), node);
			rePosition(selectedNode);
		}
	}

	public void searchResult() {
		if (getManagedBean().getCategoryId() != null
				&& !getManagedBean().getCategoryId().equals("")) {
			ITemplateDocSO templateSO = (ITemplateDocSO) BusinessDelegate
					.getInstance().getServiceObject("cc", ITemplateDocSO.class);

			TemplateDocReqDTO reqDTO = new TemplateDocReqDTO();
			reqDTO.setCategoryId(getManagedBean().getCategoryId());
			reqDTO.setTemplateType(null);

			TemplateDocResDTO resDTO = templateSO.getTemplateMasterList(reqDTO);

			if (resDTO.getDocMasterList() != null
					&& !resDTO.getDocMasterList().isEmpty()) {
				Long templateMasterSeq = null;
				List<TplDocMaster> docMasterListDB = resDTO.getDocMasterList();
				this.getManagedBean().setTemplateMasterList(docMasterListDB);
				for (TplDocMaster masterSeq : docMasterListDB) {
					templateMasterSeq = masterSeq.getDocMasterId();
				}
				getManagedBean().setDocMasterId(templateMasterSeq);
				getManagedBean().setShowTemplateList(true);
			}
		}
	}

	public void viewContent() {
		log.info("========view content started====="
				+ getManagedBean().getSelectedOption());
	}

	public void searchImportSect() {
		try {
			if (getManagedBean().getSelectedSection() != null) {
				TplDocSection selectedSection = getManagedBean()
						.getSelectedSection();
				getManagedBean().setSelectedSectionName(
						selectedSection.getSectionName());
				getManagedBean().setSelectedSectionTitle(
						selectedSection.getSectionTitle());
				getManagedBean().setSelectedSectionContent(
						selectedSection.getSectionContent());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String save(String ind) {
		FacesScopes facesScopes = new FacesScopes();
		try {
			TplDocMaster tplDocMasterE = getManagedBean().getSelectedTemplate();
			TplDocMaster tplDocMaster = null;
			if (ind.equals("newTemplate")) {
				tplDocMaster = new TplDocMaster();
				tplDocMaster.setDocName("Library");
				tplDocMaster.setCreatedBy(1l);
				tplDocMaster.setChangedBy(1l);
				tplDocMaster.setBrandNew(true);
			}

			if (ind.equals("newDraft")) {
				tplDocMaster = new TplDocMaster();
				tplDocMaster.setDocName("Draft");
				tplDocMaster.setCreatedBy(1l);
				tplDocMaster.setChangedBy(1l);
				tplDocMaster.setBrandNew(true);
			}

			if (ind.equalsIgnoreCase("update")) {
				tplDocMaster = tplDocMasterE;				
				tplDocMaster.setBrandNew(false);
				tplDocMaster.setCategoryId(tplDocMasterE.getCategoryId());				
				tplDocMaster.setDocName(tplDocMaster.getDocTitle());
				
			}
			
			tplDocMaster.setCategoryId(new Long(getManagedBean().getCategoryId()));
			tplDocMaster.setPmStatus(1l);
			tplDocMaster.setTemplateStatus('F');
//     		tplDocMaster.setRecordStatus(true);
//			tplDocMaster.setChangedBy(10l);
//			tplDocMaster.setCreatedBy(1l);
//		    tplDocMaster.setCreatedDate(new Timestamp(System.currentTimeMillis()));
			tplDocMaster.setDocTitle(tplDocMasterE.getDocTitle());
			tplDocMaster.setDocDesc(tplDocMasterE.getDocDesc());
			tplDocMaster.setDocFooter(tplDocMasterE.getDocFooter());
			tplDocMaster.setCategoryId(tplDocMasterE.getCategoryId());

			List<TplDocSection> docSectionList = new ArrayList<TplDocSection>();
			getDocSectionList(1, getManagedBean().getDocSectionTN(),
					docSectionList);
			for (int i = 0; i < docSectionList.size(); i++) {
				TplDocSection sectionOrder = docSectionList.get(i);
				sectionOrder.setSectionOrder(i);
			}
			tplDocMaster.setTplDocSectionCollection(docSectionList);

			List<TplDocSection> deletedSectionList = new ArrayList<TplDocSection>();
			if (getManagedBean().getDeletedList() != null
					&& !getManagedBean().getDeletedList().isEmpty()) {
				for (TplDocSection genTemplateSection : getManagedBean()
						.getDeletedList()) {
					genTemplateSection.setTplDocMaster(tplDocMaster);
					genTemplateSection.setDeleted(true);
					deletedSectionList.add(genTemplateSection);
				}
			}
			tplDocMaster.setDeletedCollection(deletedSectionList);
			
			IParameterSO parameterSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
			ParameterReqDTO parameterReqDTO = new ParameterReqDTO();
			parameterReqDTO.setParameterType(ParameterTypeConst.TPL_DOC_PROCESS_TYPE);
			if(getManagedBean().getProcessType().equalsIgnoreCase(ParameterConst.TPL_DOC_PROCESS_TYPE_STANDARD))
				parameterReqDTO.setParameterCode(ParameterConst.TPL_DOC_PROCESS_TYPE_STANDARD);
			else if(getManagedBean().getProcessType().equalsIgnoreCase(ParameterConst.TPL_DOC_PROCESS_TYPE_SOURCING))
				parameterReqDTO.setParameterCode(ParameterConst.TPL_DOC_PROCESS_TYPE_SOURCING);
			else if(getManagedBean().getProcessType().equalsIgnoreCase(ParameterConst.TPL_DOC_PROCESS_TYPE_CONTRACT))
				parameterReqDTO.setParameterCode(ParameterConst.TPL_DOC_PROCESS_TYPE_CONTRACT);
			else
				parameterReqDTO.setParameterCode(null);
			
			ParameterResDTO parameterResDTO = new ParameterResDTO();
			if(parameterReqDTO.getParameterCode() != null){
				parameterResDTO = parameterSO.retrieveParameterByTypeAndCode(parameterReqDTO);
				tplDocMaster.setProcessTypeCode(parameterResDTO.getPmParameter());
			} else {
				tplDocMaster.setProcessTypeCode(new PmParameter());
			}
			   
			

			ITemplateDocSO templateSO = (ITemplateDocSO) BusinessDelegate
					.getInstance().getServiceObject("cc", ITemplateDocSO.class);
			TemplateDocReqDTO reqDTO = new TemplateDocReqDTO();
			reqDTO.setTplDocMaster(tplDocMaster);

			TemplateDocResDTO resDTO = templateSO.save(reqDTO);
			if (resDTO.isSuccessful()) {
				TemplateDocReqDTO reqDTOS = new TemplateDocReqDTO();
				reqDTOS.setDocMasterId(resDTO.getTplDocMaster().getDocMasterId());
				TemplateDocResDTO resDTOS = templateSO
						.getTemplateSectionListByMaster(reqDTOS);
				List<TplDocSection> templateSectionList = resDTOS
						.getDocSectionList();
				TplDocMaster tplDocMasterDB = resDTOS.getTplDocMaster();
				getTemplateDatatableList(templateSectionList);
				getManagedBean().setSelectedTemplate(tplDocMasterDB);
				
				// TODO: common save for all process type from external page
				if (getManagedBean().getProcessType() != null && getManagedBean().getProcessType().equalsIgnoreCase(ParameterConst.TPL_DOC_PROCESS_TYPE_CONTRACT)) {
					getManagedBean().getCtAgreementContent().setTplDocMaster(resDTO.getTplDocMaster());
					ContractAgreementReqDTO contractAgreementReqDTO = new ContractAgreementReqDTO();					
					contractAgreementReqDTO.setAgreementContent(getManagedBean().getCtAgreementContent());
					IAgreementSO agreementSO = (IAgreementSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, IAgreementSO.class);
					agreementSO.saveAgreementClause(contractAgreementReqDTO);					
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return (getManagedBean().getExternalPageUrl() != null) ? getManagedBean().getExternalPageUrl() : "templateDocSearch";
	}

	public String backAction() {
		getManagedBean().setSelectedTemplate(null);
		return (getManagedBean().getExternalPageUrl() != null) ? getManagedBean().getExternalPageUrl() : "templateDocSearch";
	}
	
	public static void getDocSectionList(int level, TreeNode node,
			List<TplDocSection> docSetionList) {
		if (node != null && node.getData() != null) {
			for (TreeNode parent : node.getChildren()) {
				TplDocSection docSec = (TplDocSection) parent.getData();
				docSec.setSectionLevel(level);
				if (!node.getType().equalsIgnoreCase("root")) {
					docSec.setParent((TplDocSection) node.getData());
				} else {
					docSec.setParent(null);
				}
				docSetionList.add(docSec);
				if (!parent.isLeaf()) {
					getDocSectionList(level + 1, parent, docSetionList);
				}
			}
		}
	}

	public String createNewTemplate() {
		FacesScopes facesScopes = new FacesScopes();
		TplDocMaster docMaster = new TplDocMaster();
		docMaster.setBrandNew(Boolean.TRUE);
		docMaster.setCreatedBy(1l);
		docMaster.setCreatedDate(new Timestamp(System.currentTimeMillis()));
		getManagedBean().setSelectedTemplate(docMaster);
		getManagedBean().setDocSectionMap(
				new HashMap<TplDocSection, TreeNode>());
		TreeNode selectedNode = (new DefaultTreeNode("root", "", null));
		getManagedBean().setDocSectionTN(selectedNode);
		for (int i = 1; i <= 5; i++) {
			TplDocSection docSection = new TplDocSection();
			docSection.setTplDocMaster(docMaster);
			docSection.setSectionNumber(i + "");
			docSection.setSectionName("Section" + i);
			docSection.setCreatedBy(1l);
			docSection.setChangedBy(1l);
			docSection.setCreatedDate(new Timestamp(System.currentTimeMillis()));
			docSection.setBrandNew(Boolean.TRUE);
			docSection
					.setCreatedDate(new Timestamp(System.currentTimeMillis()));
			TreeNode node = new DefaultTreeNode(docSection, selectedNode);
			getManagedBean().getDocSectionMap().put(
					(TplDocSection) node.getData(), node);
		}
		return NAV_TO_MAINT;
	}

	public void imptSearchResult() {
		if (getManagedBean().getImpCategoryId() != null
				&& !getManagedBean().getImpCategoryId().equals("")) {
			ITemplateDocSO templateSO = (ITemplateDocSO) BusinessDelegate
					.getInstance().getServiceObject("cc", ITemplateDocSO.class);

			TemplateDocReqDTO reqDTO = new TemplateDocReqDTO();
			reqDTO.setCategoryId(getManagedBean().getImpCategoryId());
			reqDTO.setTemplateType(null);

			TemplateDocResDTO resDTO = templateSO.getTemplateMasterList(reqDTO);

			if (resDTO.getDocMasterList() != null
					&& !resDTO.getDocMasterList().isEmpty()) {
				Long templateMasterSeq = null;
				List<TplDocMaster> docMasterListDB = resDTO.getDocMasterList();
				this.getManagedBean().setImportMasterList(docMasterListDB);
				for (TplDocMaster masterSeq : docMasterListDB) {
					templateMasterSeq = masterSeq.getDocMasterId();
				}
				getManagedBean().setDocMasterId(templateMasterSeq);
				getManagedBean().setShowTemplateList(true);
			}
		}
	}

	public void selectedSectionTree() {
		ITemplateDocSO templateSO = (ITemplateDocSO) BusinessDelegate
				.getInstance().getServiceObject("cc", ITemplateDocSO.class);
		TemplateDocReqDTO reqDTO = new TemplateDocReqDTO();
		reqDTO.setDocMasterId(getManagedBean().getSelectedImpMaster()
				.getDocMasterId());

		TemplateDocResDTO resDTO = templateSO
				.getTemplateSectionListByMaster(reqDTO);
		List<TplDocSection> templateSectionList = resDTO.getDocSectionList();
		TplDocMaster tplDocMaster = resDTO.getTplDocMaster();
		if (tplDocMaster.getTplCategory() == null) {
			tplDocMaster.setTplCategory(getManagedBean().getSelectedImpMaster()
					.getTplCategory());
		}
		getImportedDatatableList(templateSectionList);
		getManagedBean().setSelectedImpMaster(tplDocMaster);
		getManagedBean().setImportedSectionList(null);
	}

	private void getImportedDatatableList(
			List<TplDocSection> templateSectionList) {
		if (templateSectionList != null && !templateSectionList.isEmpty()) {
			getManagedBean().setImptSectionTN(
					new DefaultTreeNode("root", "", null));

			Map<Long, TreeNode> secSeqMap = new HashMap<Long, TreeNode>();
			TreeNode node = null;
			for (TplDocSection objL1 : templateSectionList) {
				if (objL1.getParent() == null) {
					node = new DefaultTreeNode(objL1, getManagedBean()
							.getImptSectionTN());
					secSeqMap.put(objL1.getDocSectionId(), node);
				} else {
					node = new DefaultTreeNode(objL1, secSeqMap.get(objL1
							.getParent().getDocSectionId()));
					secSeqMap.put(objL1.getDocSectionId(), node);
				}
			}
		}
	}

	public void addSelectedImptNodes() {
		FacesScopes facesScopes = new FacesScopes();
		Timestamp date = new Timestamp(System.currentTimeMillis());
		TreeNode selectedNode = getManagedBean().getDocSectionMap().get(
				getManagedBean().getSelectedSection());
		for (TplDocSection docSection : getManagedBean()
				.getImportedSectionList()) {
			docSection = cloneObj(docSection);
			docSection.setTplDocMaster(getManagedBean().getSelectedTemplate());
			docSection.setCreatedBy(1l);
			docSection.setCreatedDate(date);
			TreeNode node = new DefaultTreeNode(docSection,
					selectedNode.getParent());
			getManagedBean().getDocSectionMap().put(
					(TplDocSection) node.getData(), node);
		}
		importRePosition(selectedNode, getManagedBean()
				.getImportedSectionList().size());
	}

	public void initializeImport(ActionEvent e) {
		getManagedBean().setImpCategoryId(null);
		getManagedBean().setImportMasterList(new ArrayList<TplDocMaster>());
		getManagedBean().setImptSectionTN(new DefaultTreeNode());
		getManagedBean().setSelectedImpMaster(null);
		getManagedBean().setSelectedImpSection(null);
	}

	public void checkRow(TplDocSection docSection) {
		if (getManagedBean().getImportedSectionList() == null) {
			getManagedBean().setImportedSectionList(
					new ArrayList<TplDocSection>());
		}

		if (getManagedBean().getImportedSectionList().contains(docSection)) {
			getManagedBean().getImportedSectionList().remove(docSection);
		} else {
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

		for (int i = 1; i <= sectionSize; i++) {
			parentNode.getChildren()
					.add(position + i,
							parentNode.getChildren().get(
									parentNode.getChildCount()
											- (sectionSize + 1 - i)));
		}
		for (int i = 1; i <= sectionSize; i++) {
			parentNode.getChildren().remove(parentNode.getChildCount() - 1);
		}
	}

	private String value = "";

	public String getValue() {
		System.out.println("getValue::: " + value);
		return value;
	}

	public void setValue(String value) throws JAXBException, Docx4JException {

		System.out.println("setValue::: " + value);
		this.value = value;
	}
	
		
	
	private static final long serialVersionUID = 1L;

	private String name="test";

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
	
	public void generatePDF() throws JAXBException, Docx4JException,
	IOException, DocumentException {

        /*TplDocMaster docMaster = new TplDocMaster();
		
		docMaster.setCategoryId(1000l);
		
		
		StringBuffer sb= new StringBuffer();
		sb.append("<html><head><title>Import me</title></head><body><p>Content Section!+");
		sb.append(value);
		for( TplDocSection docSection : docMaster.getTplDocSections()){
		sb.append(docSection.getSectionName());
		}
		sb.append("</p></body></html>");

		String html = sb.toString();*/   
		
		String html = "<html><head><title>Import me</title></head><body><p>Content Section!</p></body></html>";
        WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.createPackage();
        NumberingDefinitionsPart ndp = new NumberingDefinitionsPart();
        wordMLPackage.getMainDocumentPart().addTargetPart(ndp);
        ndp.unmarshalDefaultNumbering();
        wordMLPackage.getMainDocumentPart().getContent()
                .addAll(XHTMLImporter.convert(html, null, wordMLPackage));
        System.out.println(XmlUtils.marshaltoString(wordMLPackage
                .getMainDocumentPart().getJaxbElement(), true, true));
        wordMLPackage.save(new java.io.File("E:/html_output.docx"));
        System.out.println("done");

	}
	
	private PdfPTable exportPDFTable() {
	    int numberOfColumns = 1;
	    PdfPTable pdfTable = new PdfPTable(numberOfColumns);
        pdfTable.addCell("TESTING"+getName());
	 
	    return pdfTable;
	}
	private void writePDFToResponse(ExternalContext externalContext, ByteArrayOutputStream baos, String fileName) {
	    try {
	    	
	    	

	            PortletResponse portletResponse = (PortletResponse) externalContext.getResponse();
	            HttpServletResponse res = com.liferay.portal.util.PortalUtil
	                    .getHttpServletResponse(portletResponse);
	            res.setContentType("application/msword");
	            res.setHeader("Expires", "0");
	            res.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
	            res.setHeader("Pragma", "public");
	            res.setHeader("Content-disposition", "attachment;filename=" + fileName + ".docx");
	            res.setContentLength(baos.size());
	            OutputStream out = res.getOutputStream();
			 	        baos.writeTo(out);
			        res.flushBuffer();
			        out.flush();
			    } catch (Exception e) {
			        //e.printStackTrace();
			    }
	}
	
	
	public void combinePDF() {
		// PDFGenerator.execute();
		try {
			// PDFGenerator.simpleImages();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void tempDocSearch() {
		
		ITemplateDocSO  templateDocSO = null;
		try {
			System.out.println("!!!!!!!!!! Enter Template Doc Search Method !!!!!!!!");
			templateDocSO = (ITemplateDocSO)BusinessDelegate.getInstance().getServiceObject("cc",ITemplateDocSO.class);
			TemplateDocReqDTO reqDTO = new TemplateDocReqDTO();
			
			SearchBean<TplDocMaster> searchBean = new SearchBean<TplDocMaster>();
			searchBean.setNavigatable(false);
			Long templLong = this.getManagedBean().getCategoryId();
			if(null != templLong){
			searchBean.getDataFilterBeans().add(new SearchFilterBean("categoryId",SearchOperator.OPERATOR_EQUALS.value(),templLong));
			}
			String processValue =this.getManagedBean().getProcessValue();
			if(null != processValue){
				searchBean.getDataFilterBeans().add(new SearchFilterBean("processType.codeId",SearchOperator.OPERATOR_EQUALS.value(),processValue));
				}
			String title=this.getManagedBean().getTitle();
			if(null != title){
				searchBean.getDataFilterBeans().add(new SearchFilterBean("docTitle",SearchOperator.OPERATOR_LIKE.value(),title));
				}
			String desc=this.getManagedBean().getDesc();
			if(null != desc){
				searchBean.getDataFilterBeans().add(new SearchFilterBean("docDesc",SearchOperator.OPERATOR_LIKE.value(),desc));
				}
			reqDTO.setSearchBean(searchBean);
			TemplateDocResDTO resDTO = templateDocSO.TemplateDocSearch(reqDTO);
			System.out.println("===============resDTO======"+resDTO.getDocMasterList().size());
			this.getManagedBean().setTemplateSearchList(resDTO.getDocMasterList());
		} finally {
			templateDocSO = null;
		}
		
		this.getManagedBean().setProcessType(null);
		this.getManagedBean().setCategory(null);
		this.getManagedBean().setTitle(null);
		
	}
	
	public String createPDF() throws Exception {
	
		FacesContext context = FacesContext.getCurrentInstance();
            
		    String outputFileName = "Document" + "_"
				+ DateUtils.dateToString("yyMMddHHmmssZ", new Date());
			String html =  MergeDocx.mergePreviewAction(
					getManagedBean().getDocSectionTN(), outputFileName, "P");
	        WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.createPackage();
		
		    /*XWPFDocument document = new XWPFDocument();
		    String outputFileName = "Document" + "_"
					+ DateUtils.dateToString("yyMMddHHmmssZ", new Date());
		    String combinedContentHtml = MergeDocx.mergePreviewAction(
					getManagedBean().getDocSectionTN(), outputFileName, "P");
		 
	        XWPFParagraph paragraphOne = document.createParagraph();
	        XWPFRun paragraphOneRunOne = paragraphOne.createRun();
	        paragraphOneRunOne.setText(combinedContentHtml);
	        XWPFRun paragraphOneRunTwo = paragraphOne.createRun();
	        paragraphOneRunTwo.setText(" ");
	        System.out.println(combinedContentHtml);*/
	 
	       /* XWPFParagraph paragraphTwo = document.createParagraph();
	        XWPFRun paragraphTwoRunOne = paragraphTwo.createRun();
	        paragraphTwoRunOne.setText("And this is paragraph two.");*/
	       
	        
	        wordMLPackage.getMainDocumentPart().getContent()
            .addAll(XHTMLImporter.convert(html, null, wordMLPackage));
	        File f =new File("sectionContent.docx");
	        wordMLPackage.save(f);
	        
	        
		try {

			// Writting to Response code
			PortletResponse portletResponse = (PortletResponse) context
					.getExternalContext().getResponse();
			HttpServletResponse res = com.liferay.portal.util.PortalUtil
					.getHttpServletResponse(portletResponse);
			res.setContentType("application/msword");
			res.setHeader("Expires", "0");
			res.setHeader("Cache-Control",
					"must-revalidate, post-check=0, pre-check=0");
			res.setHeader("Pragma", "public");
			res.setHeader("Content-disposition",
					"attachment;filename=sectionContent.docx");
			OutputStream out = res.getOutputStream();
			FileInputStream fin=new FileInputStream(f);
			int i=-1;
			while( (i=fin.read())!=-1){
				out.write(i);	
			}
			
			//document.write(out);
			out.flush();
			res.flushBuffer();
			out.close();
			fin.close();
			context.responseComplete();
			
		} catch (FileNotFoundException e) {
	            e.printStackTrace();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	
		 return null;
		
		
		
		
	}
	
	
}

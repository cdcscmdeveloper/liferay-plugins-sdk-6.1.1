package my.ep.web.backingbean.sc.quatationtender;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import javax.el.ELContext;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchBetweenValue;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.pm.general.settings.rule.IRuleSO;
import my.ep.biz.service.sc.quationtender.IQTDetailSO;
import my.ep.biz.service.sc.quationtender.IQTSpecSO;
import my.ep.biz.service.tpl.spec.ITemplateSpecSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.sc.QTStatusConst;
import my.ep.constant.sc.SourcingConstants;
import my.ep.constant.tpl.TemplateSpecConst;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
import my.ep.dto.pm.general.settings.rule.RuleReqDTO;
import my.ep.dto.sc.quationtender.QuationTenderReqDTO;
import my.ep.dto.sc.quationtender.QuationTenderResDTO;
import my.ep.dto.sc.quationtender.SpecificationReqDTO;
import my.ep.dto.sc.quationtender.SpecificationResDTO;
import my.ep.dto.tpl.spec.TemplateSpecReqDTO;
import my.ep.dto.tpl.spec.TemplateSpecResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.common.PmAddress;
import my.ep.persistence.entity.pm.common.PmAddressType;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.general.settings.rule.PmConfigRuleValue;
import my.ep.persistence.entity.sc.common.ScAddress;
import my.ep.persistence.entity.sc.common.ScAttachment;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtCheckList;
import my.ep.persistence.entity.sc.quationtender.ScQtCheckListAttachment;
import my.ep.persistence.entity.sc.quationtender.ScQtCheckListScore;
import my.ep.persistence.entity.sc.quationtender.ScQtCheckListValue;
import my.ep.persistence.entity.sc.quationtender.ScQtDeliveryLoc;
import my.ep.persistence.entity.sc.quationtender.ScQtDoc;
import my.ep.persistence.entity.sc.quationtender.ScQtScoreSetting;
import my.ep.persistence.entity.sc.quationtender.ScQtSupplierCriteria;
import my.ep.persistence.entity.tpl.spec.TplSpecMaster;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneDtl;
import my.ep.type.aut.SearchOperator;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.backingbean.sc.specification.SpecificationBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.bean.sc.quatationtender.BriefingZoneBean;
import my.ep.web.bean.sc.quatationtender.QtnTenderSearchBean;
import my.ep.web.bean.tpl.TemplateSpecBean;
import my.ep.web.constant.sc.QTConstants;
import my.ep.web.managedbean.sc.quationtender.QTDetailManagedBean;
import my.ep.web.util.DateUtils;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.tpl.helper.TemplateSpecHelper;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

@ManagedBean(name = "qtSpecCloneBackingBean")
@RequestScoped
public class QtSpecCloneBackingBean extends
		SourcingBaseBackingBean<QTDetailManagedBean> {

	/** The Constant logger. */
	private static final Logger logger = EpLogger
			.getLogger(QTCloneBackingbean.class);

	private TreeNode root;
	private TreeNode[] selectedNodes;
	private boolean showTree = false;
	private static final String ALL = "lbl.txt.all";
	private static final String TECHNICAL_CHECKLIST = "lbl.sc.spec.tech.check.list";
	private static final String FINANCIAL_CHECKLIST = "lbl.sc.spec.finance.check.list";
	private static final String TECHNICAL_SCORING = "lbl.sc.spec.tech.scoring";
	private static final String FINANCIAL_SCORING = "lbl.sc.spec.financial.scoring";
	private static final String OVERALL_SCORING = "lbl.sc.spec.recommendation";
	private static final String DELIVERY_SERVICE_ADDRESS = "lbl.sc.spec.delivery.service.address";
	private static final String FULFILMENT_SCHEDULE = "lbl.sc.spec.ful.schedule";
	private static final String SPECIFICATION = "lbl.sc.committee.endorsement.specification";

	public QtSpecCloneBackingBean() {
		this.root = new DefaultTreeNode("Root Node", null);

		TreeNode child = new DefaultTreeNode(getLabel(ALL), this.root);
		child.setParent(this.root);

		TreeNode descendent1 = new DefaultTreeNode(
				getLabel(TECHNICAL_CHECKLIST), child);
		descendent1.setParent(child);
		TreeNode descendent2 = new DefaultTreeNode(
				getLabel(FINANCIAL_CHECKLIST), child);
		descendent2.setParent(child);
		TreeNode descendent3 = new DefaultTreeNode(getLabel(TECHNICAL_SCORING),
				child);
		descendent3.setParent(child);
		TreeNode descendent4 = new DefaultTreeNode(getLabel(FINANCIAL_SCORING),
				child);
		descendent4.setParent(child);
		TreeNode descendent5 = new DefaultTreeNode(getLabel(OVERALL_SCORING),
				child);
		descendent5.setParent(child);
		TreeNode descendent6 = new DefaultTreeNode(
				getLabel(DELIVERY_SERVICE_ADDRESS), child);
		descendent6.setParent(child);
		TreeNode descendent7 = new DefaultTreeNode(
				getLabel(FULFILMENT_SCHEDULE), child);
		descendent7.setParent(child);
		TreeNode descendent8 = new DefaultTreeNode(getLabel(SPECIFICATION),
				child);
		descendent8.setParent(child);
	}

	private String getLabel(String label) {
		FacesContext context = FacesContext.getCurrentInstance();
		ResourceBundle bundle = context.getApplication().getResourceBundle(
				context, "lbl");
		String message = bundle.getString(label);
		return message;
	}

	public void cloneCommitteePopupAction() {
		debugLogger("cloneCommitteePopupAction");
		// logic to check whether committe will allow for clone or not..
		// checkIsSpecCommitteFormOrNot();
		getManagedBean().setCloneCommitteeSelectQt(null);

		IPagingDataModel<ScQt> cloneSearchQTDataModel = getManagedBean()
				.getCloneSearchQTDataModel();

		if (null == cloneSearchQTDataModel) {
			getManagedBean().setCloneSearchQTDataModel(
					new QTPagingDataModel("clonesearchCommitteid2",
							getManagedBean().getCloneQtnTenderSearchBean()));
		}
		getManagedBean().getCloneSearchQTDataModel().resetData();

		getManagedBean().getCloneQtnTenderSearchBean().setQtTitle(null);
		getManagedBean().getCloneQtnTenderSearchBean().setQtNo(null);
		getManagedBean().getCloneQtnTenderSearchBean().setProcurementMode(null);
		getManagedBean().getCloneQtnTenderSearchBean().setFromCreatedDate(null);
		getManagedBean().getCloneQtnTenderSearchBean().setToCreatedDate(null);
		getManagedBean().getCloneQtnTenderSearchBean().setQtStatus(null);
		getManagedBean().setCloneTenderList(null);
		getManagedBean().setSearchcommitteeNode(new DefaultTreeNode());
		RequestContext.getCurrentInstance().execute("screen3.show()");
		getManagedBean().setAutoUpdate(false);
		getManagedBean().setSelectSearchcommitteeNode(new TreeNode[0]);
		getManagedBean().setCommType(null);
	}

	/**
	 * On row clone action.
	 * 
	 * @param event
	 *            the event
	 */
	public void onRowCloneAction(SelectEvent event) {
		debugLogger("onRowCloneAction");
		this.getManagedBean().setSearchcommitteeNode(new DefaultTreeNode());
		ScQt scQt = (ScQt) event.getObject();
		this.getManagedBean().setCloneCommitteeSelectQt(scQt);
		this.showTree = true;
	}
	
	private ScQtCheckList retrieveScoreNValue(ScQtCheckList originCL,ScQtCheckList copyCL){
		IQTSpecSO specSo = null;
		SpecificationResDTO resDTO = null;
		try{
		SpecificationReqDTO reqDTO = new SpecificationReqDTO();
		specSo = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);
		reqDTO.setScQtCheckList(originCL);
		resDTO = specSo.retrieveCheckListScoreNValue(reqDTO);
		if (null != resDTO.getScQtCheckListValue()) {
			ScQtCheckListValue origin=resDTO.getScQtCheckListValue();
			
			ScQtCheckListValue checkListValue = new ScQtCheckListValue();
			if(null!=copyCL.getScQtCheckListValue()){
				checkListValue=copyCL.getScQtCheckListValue();
			}else{
				checkListValue.setScQtCheckList(copyCL);
			}
			setUserDetails(checkListValue);
			checkListValue.setYear(origin.getYear());
			
			checkListValue.setAllEmployee(origin.isAllEmployee());
			checkListValue.setFromDate(origin.getFromDate());
			checkListValue.setToDate(origin.getToDate());
			copyCL.setScQtCheckListValue(checkListValue);
		}
		
		if(resDTO.getCheckListScores()!=null){
			List<ScQtCheckListScore> scores = new ArrayList<ScQtCheckListScore>();
			for (ScQtCheckListScore origin : resDTO
					.getCheckListScores()) {
				
				ScQtCheckListScore copy=new ScQtCheckListScore();
				copy.setCheckList(copyCL);
				copy.setRangeFrom(origin.getRangeFrom());
				copy.setRangeTo(origin.getRangeTo());
				copy.setScore(origin.getScore());
				copy.setScoreType(origin.getScoreType());
				copy.setCriteriaType(origin.getCriteriaType());
				scores.add(copy);
			}
			if(null==copyCL.getScQtCheckListScores()){
				copyCL.setScQtCheckListScores(scores);
			}
			
		}
		
		}finally{
			specSo = null;
		}
		return copyCL;
	}
	
	private boolean isChecklistDefault(ScQtCheckList cl){
		List<ScQtCheckList> list=this.getManagedBean().getScQtCheckSt2financeProposalList();
		if(list!=null){
			for(ScQtCheckList def:list){
				if(def.getTitle().equals(cl.getTitle())){
					return true;
				}
			}
		}
		return false;
	}
	
	private ScQtCheckList getDefault(String title){
		IQTSpecSO specSo = null;
		SpecificationResDTO resDTO = null;
		try{
			SpecificationReqDTO reqDTO = new SpecificationReqDTO();
			specSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);
			reqDTO.setScQt(this.getManagedBean().getQt());
			reqDTO.setSpecType(SourcingConstants.FINANCIAL_DOCTYPE);
			resDTO = specSo.retrieveCheckLists(reqDTO);
			if(null!=resDTO.getFinancialCheckLists()){
				for(ScQtCheckList cl:resDTO.getFinancialCheckLists()){
					if(title.equals(cl.getTitle())){
						return cl;
					}
				}
			}
		}finally{
			specSo=null;
		}
		
		
		
		return null;
	}
	
	private List<ScQtCheckList> getCheckListInDB(ScQt scqt,String type){
		IQTSpecSO specSo = null;
		SpecificationResDTO resDTO = null;
		try{
			SpecificationReqDTO reqDTO = new SpecificationReqDTO();
			specSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);
			reqDTO.setScQt(scqt);
			reqDTO.setSpecType(type);
			//reqDTO.setSpecType(SourcingConstants.FINANCIAL_DOCTYPE);
			//reqDTO.setSpecType(SourcingConstants.TECHNICAL_DOCTYPE);
			resDTO = specSo.retrieveCheckLists(reqDTO);
		}finally{
			specSo=null;
		}
		
		if(SourcingConstants.FINANCIAL_DOCTYPE.equals(type)){
			return resDTO.getFinancialCheckLists();
		}
		if(SourcingConstants.TECHNICAL_DOCTYPE.equals(type)){
			return resDTO.getTechnicalCheckList();
		}
		
		return null;
	}
	
	private void retrieveFinancialChecklist(ScQt scqt) throws FileNotFoundException{
		IQTSpecSO specSo = null;
		SpecificationResDTO resDTO = null;
		
		if (getManagedBean().getMarkDeletedFinChecklist() == null) {
			getManagedBean().setMarkDeletedFinChecklist(
					new ArrayList<ScQtCheckList>());
		}
		try{
			SpecificationReqDTO reqDTO = new SpecificationReqDTO();
			specSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);
			reqDTO.setScQt(scqt);
			reqDTO.setSpecType(SourcingConstants.FINANCIAL_DOCTYPE);
			resDTO = specSo.retrieveCheckLists(reqDTO);
			if(null!=resDTO.getFinancialCheckLists()){
				List<ScQtCheckList> toAdd =new ArrayList<ScQtCheckList>();
				List<ScQtCheckList> localcheckList =this.getManagedBean().getScQtCheckSt2financeProposalList();
				for (ScQtCheckList checklist : resDTO
						.getFinancialCheckLists()) {
					
					if (!ParameterConst.DOCUMENT_DEFINITION_TYPE_TEMPLATE_SPECIFICATION
							.equalsIgnoreCase(checklist.getDefinationType().getParameterCode())) {
						 // if(!checklist.isManditory()){
							ScQtCheckList copy = copyBean(checklist);
							copy.setScQt(this.getManagedBean().getQt());
							copy=retrieveScoreNValue(checklist,copy);
							
							if (null != copy.getPmDocument()) {
								if (!SourcingConstants.DOC_DEF_KEY_SERVICE_SCOPE
										.equalsIgnoreCase(checklist.getPmDocument()
												.getDocDefinitionKey())) {
									toAdd.add(copy);
								}
							} else {
								toAdd.add(copy);
							}
//						}else{
//							ScQtCheckList copy =getDefault(checklist.getTitle());
//							if(null!=copy){
//								copy=retrieveScoreNValue(checklist,copy);
//								copy=setStatus(copy);
//							}else{
//								copy = copyBean(checklist);
//								copy.setScQt(this.getManagedBean().getQt());
//								copy=retrieveScoreNValue(checklist,copy);
//							}
//							toAdd.add(copy);
//						}
					}
				}
				
				//this.getManagedBean().setMarkDeletedFinChecklist(new ArrayList<ScQtCheckList>());
				//List<ScQtCheckList> finList=getCheckListInDB(this.getManagedBean().getQt(),SourcingConstants.FINANCIAL_DOCTYPE);
//				if(null!=finList
//						&& !finList.isEmpty()
//						&& finList.size()!=0){
//					
//					List<ScQtCheckList> toRemove=new ArrayList<ScQtCheckList>();
//					for(ScQtCheckList toDelete:finList){
//						if(!toDelete.isManditory()){
//							toDelete.setSelected(true);
//							toDelete.setDeleted(true);
//							toRemove.add(toDelete);
//						}
//					}
//					//this.getManagedBean().getScQtCheckSt2financeProposalList().removeAll(toRemove);
//					this.getManagedBean().setMarkDeletedFinChecklist(toRemove);
//					
//				}
				
				List<ScQtCheckList> toRemove=new ArrayList<ScQtCheckList>();
				if(null!=this.getManagedBean().getScQtCheckSt2financeProposalList()
				&& !this.getManagedBean().getScQtCheckSt2financeProposalList().isEmpty()
				&& this.getManagedBean().getScQtCheckSt2financeProposalList().size()!=0){
					
					for(ScQtCheckList toDelete:this.getManagedBean().getScQtCheckSt2financeProposalList()){
						//if (!ParameterConst.DOCUMENT_DEFINITION_TYPE_TEMPLATE_SPECIFICATION
						//		.equalsIgnoreCase(toDelete.getDefinationType().getParameterCode())) {
							toDelete.setSelected(true);
							toDelete.setDeleted(true);
							//toRemove.add(toDelete);
						//}
						
					}
					
				}
				
				
				this.getManagedBean().setMarkDeletedFinChecklist(this.getManagedBean()
						.getScQtCheckSt2financeProposalList());
							
				this.getManagedBean().setScQtCheckSt2financeProposalList(toAdd);
				if (CollectionUtils.isNotEmpty(this.getManagedBean()
						.getScQtCheckSt2financeProposalList())) {
					checklistSorting(this.getManagedBean()
							.getScQtCheckSt2financeProposalList());
				}
			}
		}finally{
			specSo = null;
		}
	}
	
	private void removeAdditional(List<ScQtCheckList> list){
		List<ScQtCheckList> toRemove=new ArrayList<ScQtCheckList>();
		for(ScQtCheckList o:list){
			if(!o.isManditory()){
				list.remove(o);
			}
		}
	}

	private void retrieveTechnicalChecklist(ScQt scqt)
			throws FileNotFoundException {
		IQTSpecSO specSo = null;
		SpecificationResDTO resDTO = null;

		try {

			List<ScQtCheckList> localcheckList = new ArrayList<ScQtCheckList>();
			this.getManagedBean().getQt().setScQtChecklists(localcheckList);
			SpecificationReqDTO reqDTO = new SpecificationReqDTO();
			specSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);
			reqDTO.setScQt(scqt);
			reqDTO.setSpecType(SourcingConstants.TECHNICAL_DOCTYPE);
			resDTO = specSo.retrieveCheckLists(reqDTO);
			
			//if (!isAtleastOneSeriveReq(resDTO.getTechnicalCheckList())) {}
			
			if (null != resDTO.getTechnicalCheckList()) {
				// Collections.sort(resDTO.getTechnicalCheckList());
				for (ScQtCheckList checklist : resDTO
						.getTechnicalCheckList()) {
					ScQtCheckList copy = copyBean(checklist);
					copy.setScQt(this.getManagedBean().getQt());
					if (!ParameterConst.DOCUMENT_DEFINITION_TYPE_TEMPLATE_SPECIFICATION
							.equalsIgnoreCase(checklist.getDefinationType().getParameterCode())) {
					copy=retrieveScoreNValue(checklist,copy);
					}
					if (null != copy.getPmDocument()) {
						if (!SourcingConstants.DOC_DEF_KEY_SERVICE_SCOPE
								.equalsIgnoreCase(checklist.getPmDocument()
										.getDocDefinitionKey())) {
							localcheckList.add(copy);
						}
					} else {
						localcheckList.add(copy);
					}
				}
			}
			
			//this.getManagedBean().setMarkDeletedTechChecklist(new ArrayList<ScQtCheckList>());			
			//List<ScQtCheckList> tecList=getCheckListInDB(this.getManagedBean().getQt(),SourcingConstants.TECHNICAL_DOCTYPE);
//			if(null!=tecList
//					&& !tecList.isEmpty()
//					&& tecList.size()!=0){
//				
//				List<ScQtCheckList> toRemove=new ArrayList<ScQtCheckList>();
//				for(ScQtCheckList toDelete:tecList){
//					if(!toDelete.isManditory()){
//						toDelete.setSelected(true);
//						toDelete.setDeleted(true);
//						toRemove.add(toDelete);
//					}
//				}
//			
//				this.getManagedBean().setMarkDeletedTechChecklist(toRemove);
//				
//			}
			
			if(null!=this.getManagedBean().getScQtCheckSt2techProposal()
					&& !this.getManagedBean().getScQtCheckSt2techProposal().isEmpty()
					&& this.getManagedBean().getScQtCheckSt2techProposal().size()!=0){
				
				for(ScQtCheckList toDelete:this.getManagedBean().getScQtCheckSt2techProposal()){
						toDelete.setSelected(true);
						toDelete.setDeleted(true);
				}
				
			}
			
			this.getManagedBean().setMarkDeletedTechChecklist(this.getManagedBean().getScQtCheckSt2techProposal());
			
			
			this.getManagedBean().setScQtCheckSt2techProposal(localcheckList);
			if (CollectionUtils.isNotEmpty(this.getManagedBean()
					.getScQtCheckSt2techProposal())) {
				checklistSorting(this.getManagedBean()
						.getScQtCheckSt2techProposal());
			}


			localcheckList = null;

		} finally {
			specSo = null;
		}
	}

	private ScQtCheckList copyStandardChecklist(ScQtCheckList origin)
			throws FileNotFoundException {
		this.getManagedBean().setTechStandaraChecklist(true);
		ScQtCheckList copy = new ScQtCheckList();

		copy.setTitle(origin.getTitle());
		copy.setDefinationType(origin.getDefinationType());
		copy.setAction(origin.getAction());
		copy.setManditory(origin.isManditory());
		copy.setScoringReq(origin.isScoringReq());
		if(QTConstants.TECHNICAL_DOC_TYPE.equals(origin.getDocCategory())){
			copy.setDocCategory(QTConstants.TECHNICAL_DOC_TYPE);
		}
		if(QTConstants.FINANCIAL_DOC_TYPE.equals(origin.getDocCategory())){
			copy.setDocCategory(QTConstants.FINANCIAL_DOC_TYPE);
		}
		
		copy.setPmDocument(origin.getPmDocument());
		copy.setChecklistSeq(origin.getChecklistSeq());
		if (origin.getScQtCheckListAttachment() != null)
			copy.setScQtCheckListAttachment(copyAttachment(origin, copy));
		return copy;
	}

	private ScQtCheckListAttachment copyAttachment(
			ScQtCheckList originCheckList, ScQtCheckList copyCheckList)
			throws FileNotFoundException {
		ScQtCheckListAttachment copy = new ScQtCheckListAttachment();
		copy.setScQtCheckList(copyCheckList);
		copy.setDocNo(this.getManagedBean().getQt().getQtNo());
		try {
			uploadAttachment(copy, originCheckList.getScQtCheckListAttachment());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return copy;
	}

	private void uploadAttachment(ScAttachment copy, ScAttachment origin)
			throws FileNotFoundException, IOException {
		String fileName = origin.getFileName();
		File file = new File(getActualFilePath(origin));
		byte[] fileBytes = Files.readAllBytes(file.toPath());
		// byte[] fileBytes = null;//file.getContents();
		FileOutputStream fos = null;
		try {
			if (fileBytes != null) {
				copy.setFileName(fileName);
				copy.setFileDesc("File uploded");
				populateAttachmentDetails(copy);
				fos = new FileOutputStream(getActualFilePath(copy));
				fos.write(fileBytes);
			}
		} finally {
			if (fos != null) {
				fos.close();
			}
		}
	}
	
	/**
	 * Construct qt doc by type.
	 * 
	 * @param specMaster
	 *            the spec master
	 * @param docType
	 *            the doc type
	 * @return the sc qt check list
	 */
	private ScQtCheckList constructQtDocByType(TplSpecMaster specMaster,
			String docType) {
		IParameterSO parameterSo = null;
		parameterSo = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
		ScQtDoc scqtDocDest = new ScQtDoc();
		setUserDetails(scqtDocDest);
		specMaster.setStatus(getPmStatusByStatusId(QTStatusConst.BSV_PENDING));
		specMaster.setScQtDoc(scqtDocDest);
		if (null == specMaster.getItemOriginCode()
				|| null == specMaster.getItemOriginCode().getParameterId()) {
			ParameterReqDTO localGoodsReqDTO = new ParameterReqDTO();
			localGoodsReqDTO.setParameterType(ParameterTypeConst.ITEM_ORIGIN);
			localGoodsReqDTO.setParameterCode(ParameterConst.LOCAL_GOODS);
			ParameterResDTO localGoodsResDTO = parameterSo
					.retrieveParameterByTypeAndCode(localGoodsReqDTO);
			specMaster.setItemOriginCode(localGoodsResDTO.getPmParameter());
		} else {
			specMaster.setItemOriginCode(specMaster.getItemOriginCode());
		}
		scqtDocDest.setTplSpecMaster(specMaster);
		scqtDocDest.setScQt(this.getManagedBean().getQt());
		// scqtDocDest.setTplDocMaster(null);
		ScQtCheckList checkList = new ScQtCheckList();
		checkList.setDocCategory(docType);

		ParameterReqDTO specType = new ParameterReqDTO();
		specType.setParameterType(ParameterTypeConst.DOCUMENT_DEFINITION_TYPE);
		specType.setParameterCode(ParameterConst.DOCUMENT_DEFINITION_TYPE_TEMPLATE_SPECIFICATION);
		ParameterResDTO specTyperesDTO = parameterSo
				.retrieveParameterByTypeAndCode(specType);

		ParameterReqDTO checkListActionType = new ParameterReqDTO();
		checkListActionType
				.setParameterType(ParameterTypeConst.SC_SUPPLIER_ACTION);
		checkListActionType
				.setParameterCode(ParameterConst.CHECKLIST_ACTION_INPUT);
		ParameterResDTO checkListactionresDTO = parameterSo
				.retrieveParameterByTypeAndCode(checkListActionType);
		checkList
				.setChecklistSeq(SourcingConstants.CHECKLIST_SEQ_TEMPLATE_SPEC);
		// PmStatus status = new PmStatus();
		// status.setStatusId(1l);
		// checkList.setPmStatus(status);
		checkList.setScQtDoc(scqtDocDest);
		checkList.setScoringReq(this.getManagedBean().getQt()
				.isTechEvaluationReq());
		checkList.setAction(checkListactionresDTO.getPmParameter());
		checkList.setScQt(this.getManagedBean().getQt());
		checkList.setDefinationType(specTyperesDTO.getPmParameter());
		checkList.setPmStatus(getPmStatusByStatusId(QTStatusConst.TS_PENDING_SUBMISSION));
		checkList.setPmEvalStatus(getPmStatusByStatusId(QTStatusConst.TS_PENDING_SUBMISSION));
		setUserDetails(checkList);
		scqtDocDest.setScQtCheckList(checkList);
		 parameterSo = null;
		return checkList;
	}

	private ScQtCheckList copySpecTemplate(TplSpecMaster origin) {
		ScQtCheckList qtCheckList=null;
		try {
			ITemplateSpecSO templateSpecSO = EpEjbBusinessDelegate
					.getInstance().getServiceObject(
							ModuleCodeConst.COMMON_COMPONENT,
							ITemplateSpecSO.class);
			TemplateSpecReqDTO specReqDTO = new TemplateSpecReqDTO();
			specReqDTO.setSpecMaster(origin);
			specReqDTO.setContentType(TemplateSpecConst.CONTENT_TYPE_QUESTION);
			TemplateSpecResDTO resDTO = templateSpecSO
					.getTemplateMaster(specReqDTO);
			TplSpecMaster copy = TemplateSpecHelper.getInstance()
					.cloneTplSpecMaster(resDTO.getSpecMaster(), true,
							getManagedBean().getQt().isTechEvaluationReq(),
							true);

			copy.setProcessType(ParameterConst.TPL_DOC_PROCESS_TYPE_SOURCING);
			if (getManagedBean().getQt().isZonal()) {
				ScQtSupplierCriteria supplierCriteria = getManagedBean()
						.getQt().getScQtSupplierCriteria();
				if (null != supplierCriteria
						&& null != supplierCriteria.getQtZoneCriterias()) {
					TemplateSpecHelper.getInstance().updateZoneDtl(copy,
							supplierCriteria.getQtZoneCriterias());
				} else {
					throw new EpException(
							"Please choose the zones before proceed.");
				}
			} else {
				TemplateSpecHelper.getInstance()
						.updateZoneDtl(copy, null);
			}
			
			qtCheckList = constructQtDocByType(copy,
					SourcingConstants.TECHNICAL_DOCTYPE);
			qtCheckList.setTitle(origin.getSpecName());
			TemplateSpecBean templateSpecBean = getManagedBean()
					.getTemplateSpecBean();
			
			templateSpecBean.setSpecMaster(copy);
			
			
		} finally {

		}

		return qtCheckList;
	}

	private ScQtCheckList copyBean(ScQtCheckList origin)
			throws FileNotFoundException {
		ScQtCheckList copy = new ScQtCheckList();
		if (ParameterConst.DOCUMENT_DEFINITION_TYPE_TEMPLATE_SPECIFICATION
				.equalsIgnoreCase(origin.getDefinationType().getParameterCode())) {
			copy=copySpecTemplate(origin.getScQtDoc().getTplSpecMaster());
			copy.setChecklistSeq(origin.getChecklistSeq());
		} else {
			copy = copyStandardChecklist(origin);
		}

		copy=setStatus(copy);
		
		setUserDetails(copy);
		return copy;
	}
	
	private ScQtCheckList setStatus(ScQtCheckList copy){
		if (null != copy.getDefinationType()
				&& !ParameterConst.DOCUMENT_DEFINITION_TYPE_UPLOAD
						.equalsIgnoreCase(copy.getDefinationType()
								.getParameterCode())) {
			Long statusId = null;
			if (!copy.isScoringReq()) {
				statusId = QTStatusConst.TS_COMPLETED;
			} else {
				statusId = QTStatusConst.TS_PENDING_SUBMISSION;
			}
			if (ParameterConst.DOCUMENT_DEFINITION_TYPE_SAMPLE
					.equalsIgnoreCase(copy.getDefinationType()
							.getParameterCode())) {
				statusId = QTStatusConst.TS_COMPLETED;
			}
			copy.setPmStatus(getPmStatusByStatusId(statusId));
		} else {
			if (ParameterConst.DOCUMENT_DEFINITION_TYPE_UPLOAD
					.equalsIgnoreCase(copy.getDefinationType()
							.getParameterCode())) {
				copy.setPmStatus(getPmStatusByStatusId(QTStatusConst.TS_COMPLETED));
			} else {
				copy.setPmStatus(getPmStatusByStatusId(QTStatusConst.TS_PENDING_SUBMISSION));
			}

		}
		if(null!=copy.getDefinationType() && ParameterConst.DOCUMENT_DEFINITION_TYPE_SUPPLIER_UPLOAD_AND_DOWNLOAD
			.equalsIgnoreCase(copy.getDefinationType()
					.getParameterCode())){
			copy.setPmStatus(getPmStatusByStatusId(QTStatusConst.TS_COMPLETED));
		}
		copy.setPmEvalStatus(getPmStatusByStatusId(QTStatusConst.TS_PENDING_SUBMISSION));
		return copy;
	}

	public void addToClone() throws FileNotFoundException {
		ScQt selected = this.getManagedBean().getSelectedCloneTender();
		IQTDetailSO QtDetailsSO = BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
						IQTDetailSO.class);
		QuationTenderReqDTO tenderReqDTO = new QuationTenderReqDTO();
		tenderReqDTO.setQtId(selected.getQtId());
		QuationTenderResDTO tenderResDTO = QtDetailsSO
				.retriveQTDetails(tenderReqDTO);
		
		FacesContext context = FacesContext.getCurrentInstance();
		ELContext elContext = context.getELContext();
		SpecificationBackingBean specificationBackingBean = (SpecificationBackingBean) elContext.getELResolver().getValue(elContext, null, "specificationBackingBean");
		specificationBackingBean.retriveTechnicalCheckList();
		specificationBackingBean.retriveFinancialCheckList();
		retrieveTechnicalChecklist(tenderResDTO.getQtDetail());
		retrieveFinancialChecklist(tenderResDTO.getQtDetail());
		retrieveTechnicalScoreSettings(tenderResDTO.getQtDetail());
		retrieveFinancialScoreSettings(tenderResDTO.getQtDetail());
		specificationBackingBean.saveSpecificationCommitteeDtls();
		
		//retrieveDeliveryAddress(tenderResDTO.getQtDetail());
		
		
		
	}
	
	private void retrieveFinancialScoreSettings(ScQt scqt){
		SpecificationReqDTO reqDTO = new SpecificationReqDTO();
		reqDTO.setScQt(scqt);
		SpecificationResDTO resDTO = null;
		IQTSpecSO specSo = null;
		try {
		List<Integer> settinglist = new ArrayList<Integer>();
		settinglist
				.add(SourcingConstants.SCORE_SETTING_TYPE_FINANCIAL_BY_PACKAGE);
		settinglist
				.add(SourcingConstants.SCORE_SETTING_TYPE_FINANCIAL_BY_ITEM);

		// reqDTO.setSpecType("T");
		reqDTO.setSettingTypes(settinglist);
		specSo = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);
		resDTO = specSo
				.retrieveScoreSettingDtls(reqDTO);
		
		if (null != resDTO.getScoreSettingResultBean().getResultList()
				&& !resDTO.getScoreSettingResultBean().getResultList()
						.isEmpty()) {
			
			
			if (resDTO.getScoreSettingResultBean().getResultList().size() == 2) {
				for (ScQtScoreSetting scoreSttting : resDTO
						.getScoreSettingResultBean().getResultList()) {
					if (scoreSttting.getSettingType() == SourcingConstants.SCORE_SETTING_TYPE_FINANCIAL_BY_PACKAGE) {
						ScQtScoreSetting settings=copyScoreSettings(scoreSttting);
							this.getManagedBean().setFinancialScoreSetting(
									settings);
						
					}
					if (scoreSttting.getSettingType() == SourcingConstants.SCORE_SETTING_TYPE_FINANCIAL_BY_ITEM) {
						ScQtScoreSetting settings=copyScoreSettings(scoreSttting);
							this.getManagedBean()
									.setFinacialItemScoreSetting(settings);
							calculateFinancialItemScore(this
									.getManagedBean()
									.getFinacialItemScoreSetting());
						

					}
				}
				
				
				
			}else if(resDTO.getScoreSettingResultBean().getResultList()
					.size() == 1) {
				for (ScQtScoreSetting scoreSttting : resDTO
						.getScoreSettingResultBean().getResultList()) {
					if (scoreSttting.getSettingType() == SourcingConstants.SCORE_SETTING_TYPE_FINANCIAL_BY_PACKAGE) {
						ScQtScoreSetting settings=copyScoreSettings(scoreSttting);
							this.getManagedBean().setFinancialScoreSetting(
									settings);
						
					}
					if (scoreSttting.getSettingType() == SourcingConstants.SCORE_SETTING_TYPE_FINANCIAL_BY_ITEM) {
						ScQtScoreSetting settings=copyScoreSettings(scoreSttting);
							this.getManagedBean()
									.setFinacialItemScoreSetting(settings);

					}
				}
			}
			
		}
		
		}finally{
			specSo = null;
		}
	}
	
	public void calculateFinancialItemScore(ScQtScoreSetting financialItem) {
		try {
			if (null != financialItem) {
				int value1 = financialItem.getOverallFin() != null ? financialItem
						.getOverallFin().intValue() : 0;
				int value2 = financialItem.getOverallOtherFin() != null ? financialItem
						.getOverallOtherFin().intValue() : 0;
				int local = value1 + value2;
				this.getManagedBean().setFinancialItemScore(
						new BigDecimal(local));
				if ((null != this.getManagedBean().getFinancialItemScore())
						&& this.getManagedBean().getFinancialItemScore()
								.intValue() > 100) {
					throw new EpException(MessageConst.ERROR_760257);

				}
			}

		} finally {

		}
	}
	
	private void retrieveTechnicalScoreSettings(ScQt scqt){
		SpecificationReqDTO reqDTO = new SpecificationReqDTO();
		reqDTO.setScQt(scqt);
		SpecificationResDTO resDTO = null;
		IQTSpecSO specSo = null;
		try {
			List<Integer> settinglist = new ArrayList<Integer>();
			settinglist.add(SourcingConstants.SCORE_SETTING_TYPE_TECHNICAL);
			settinglist.add(SourcingConstants.SCORE_SETTING_TYPE_DEMO);
			settinglist.add(SourcingConstants.SCORE_SETTING_TYPE_TECHNICAL_BY_ITEM);
			reqDTO.setSettingTypes(settinglist);
			specSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);
			resDTO = specSo.retrieveScoreSettingDtls(reqDTO);
			
			if (null != resDTO.getScoreSettingResultBean().getResultList()
					&& !resDTO.getScoreSettingResultBean().getResultList()
							.isEmpty()) {
				
				for (ScQtScoreSetting recommnendationSetting : resDTO
						.getScoreSettingResultBean().getResultList()) {
					if (recommnendationSetting.getSettingType() == SourcingConstants.SCORE_SETTING_TYPE_TECHNICAL) {
							
							ScQtScoreSetting settings=copyScoreSettings(recommnendationSetting);
							this.getManagedBean().setTechnicalScoreSetting(
									settings);
						
					}
					if (recommnendationSetting.getSettingType() == SourcingConstants.SCORE_SETTING_TYPE_DEMO) {
	
							ScQtScoreSetting settings=copyScoreSettings(recommnendationSetting);
							this.getManagedBean().setDemoScoreSetting(
									settings);
						
					}
					if (recommnendationSetting.getSettingType() == SourcingConstants.SCORE_SETTING_TYPE_TECHNICAL_BY_ITEM) {
		
							ScQtScoreSetting settings=copyScoreSettings(recommnendationSetting);
							this.getManagedBean().setTechnicalItemScoreSetting(
									settings);
						
					}
					
				}
			}
			
//			settinglist = null;
//			this.getManagedBean().setTechnicalScoring(null);
//			this.getManagedBean().setTechnicalScoring(
//					new ArrayList<ScQtCheckList>());
//			List<ScQtCheckList> checkList = this.getManagedBean()
//					.getScQtCheckSt2techProposal();
//			if (null != checkList && !checkList.isEmpty()) {
//				// Collections.sort(checkList);
//				int score = 0;
//				float addScore = 0;
//				for (ScQtCheckList list : checkList) {
//					if (null == list.getQtScoreWeightage()) {
//						ScQtScoreWeightage scoreWeightage = new ScQtScoreWeightage();
//						scoreWeightage.setScore(new BigDecimal(0));
//						list.setQtScoreWeightage(scoreWeightage);
//						setUserDetails(list.getQtScoreWeightage());
//					}
//					if (null != list.getQtScoreWeightage()
//							&& null != list.getQtScoreWeightage().getScore()) {
//						addScore = addScore
//								+ list.getQtScoreWeightage().getScore()
//										.intValue();
//					}
//					score = (int) addScore;
//					if (list.isScoringReq()) {
//						this.getManagedBean().getTechnicalScoring().add(list);
//					} else {
//						list.getQtScoreWeightage().setScore(new BigDecimal(0));
//					}
//					if (null != list.getQtScoreWeightage()
//							&& null != list.getQtScoreWeightage().getScore()) {
//						list.getQtScoreWeightage().setScore(
//								list.getQtScoreWeightage().getScore());
//					}
//
//				}
//
//				this.getManagedBean().setTechPoints(score);
//				techWeightageSum();
//				techScoreSum();
//			}
//			
//			
//			if (null != this.getManagedBean().getTechnicalScoring()
//					&& !this.getManagedBean().getTechnicalScoring().isEmpty()) {
//				this.getManagedBean().setTechnicalUserScoringSchema(false);
//				for (ScQtCheckList technicalScoringChecklist : this
//						.getManagedBean().getTechnicalScoring()) {
//					if (null != technicalScoringChecklist.getPmStatus()
//							&& QTStatusConst.TS_COMPLETED
//									.equals(technicalScoringChecklist
//											.getPmStatus().getStatusId())
//							&& null != technicalScoringChecklist
//									.getPmEvalStatus()
//							&& QTStatusConst.TS_PENDING_SUBMISSION
//									.equals(technicalScoringChecklist
//											.getPmEvalStatus().getStatusId())) {
//						this.getManagedBean().setTechnicalUserScoringSchema(
//								true);
//						break;
//
//					}
//
//				}
//			}
//			checklistSorting(this.getManagedBean().getTechnicalScoring());
//			
//
			
			
		} finally {
			specSo = null;
		}
	}
	
	private ScQtScoreSetting copyScoreSettings(ScQtScoreSetting src){
		ScQtScoreSetting copy=new ScQtScoreSetting();
		copy.setBrandNew(true);
		copy.setSettingType(src.getSettingType());
		copy.setScQt(this.getManagedBean().getQt());
		copy.setWeightageReq(src.isWeightageReq());
		copy.setPassingScore(src.getPassingScore());
		setUserDetails(copy);
		
		return copy;
	}
	
	private BigDecimal retrieveTechnicalEvalutionPassingScorePmrules(
			String ruleType) {
		IRuleSO ruleSO = (IRuleSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IRuleSO.class);
		BigDecimal technicalPassingScore = new BigDecimal(0);
		RuleReqDTO ruleReqDTO = new RuleReqDTO();
		ruleReqDTO.setRuleType(ruleType);
		List<PmConfigRuleValue> ruleValueList = ruleSO
				.getRuleValuesByTypeInCurrentDate(ruleReqDTO)
				.getRuleValueList();
		if (ruleValueList != null && ruleValueList.size() > 0) {
			PmConfigRuleValue configRuleValue = ruleValueList.get(0);
			if (configRuleValue != null
					&& configRuleValue.getRuleValue() != null) {
				technicalPassingScore = new BigDecimal(
						configRuleValue.getRuleValue());
			}
		} else {
			throw new EpException(MessageConst.ERROR_770222,
					new String[] { getRuleTypeDesc(ruleReqDTO.getRuleType()) });
		}
		ruleSO = null;
		return technicalPassingScore;
	}
	
	private List<ScQtCheckList> getToBeRemoveList() {
		List<ScQtCheckList> toBeRemoveList = new ArrayList<ScQtCheckList>();

		List<ScQtCheckList> techList = getManagedBean()
				.getMarkDeletedTechChecklist();
		if (techList != null && techList.size() > 0) {
			toBeRemoveList.addAll(techList);
		}

		List<ScQtCheckList> finList = getManagedBean()
				.getMarkDeletedFinChecklist();
		if (finList != null && finList.size() > 0) {
			toBeRemoveList.addAll(finList);
		}

		return toBeRemoveList;

	}
	
	private void retrieveDeliveryAddress(ScQt copy){
		IQTSpecSO specSo = null;
		try {
			SpecificationReqDTO reqDTO = new SpecificationReqDTO();
			specSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);
			reqDTO.setQtId(copy.getQtId());
			SpecificationResDTO resDTO = specSo
					.retrieveDeliverSeriviceAddress(reqDTO);
			
			if (resDTO.getSpecZoneDtls() == null
					|| resDTO.getSpecZoneDtls().isEmpty()) {
				List<ScQtDeliveryLoc> locs=new ArrayList<ScQtDeliveryLoc>();
				for(ScQtDeliveryLoc copyLoc:resDTO.getAddressResultBean()){
					ScQtDeliveryLoc loc = new ScQtDeliveryLoc();
					ScAddress deliveryAddress = populateDeliveryAddress(copyLoc.getPmAddressType());
					loc.setScDeliveryAddress(deliveryAddress);
					loc.setDeliveryAddress(getDeliveryAddressString(deliveryAddress));
					loc.setPmAddressType(copyLoc.getPmAddressType());
					loc.setTplSpecZoneDtl(this.getManagedBean()
							.getTplSpecZoneDtl());
					loc.setScQt(this.getManagedBean().getQt());

					setUserDetails(loc);
					locs.add(loc);
				}
				
				this.getManagedBean().setDeliveryNonZoneList(locs);
			}else {
				this.getManagedBean().setDeliveryZones(
						resDTO.getSpecZoneDtls());

				for (TplSpecZoneDtl tplSpecZoneDtl : this.getManagedBean()
						.getDeliveryZones()) {
					this.getManagedBean()
							.setTplSpecZoneDtl(
									this.getManagedBean()
											.getDeliveryZones().get(0));
					reqDTO.setQtId(copy.getQtId());
					reqDTO.setZoneId(tplSpecZoneDtl.getSpecZoneId());
					resDTO = specSo.retrieveDeliveryAddressDtls(reqDTO);
					if (this.getManagedBean().getDeliveryLocBean() == null) {
						this.getManagedBean().setDeliveryLocBean(
								new ArrayList<BriefingZoneBean>());
					}
					
					List<ScQtDeliveryLoc> locs=new ArrayList<ScQtDeliveryLoc>();
					for(ScQtDeliveryLoc copyLoc:resDTO.getAddressResultBean()){
						ScQtDeliveryLoc loc = new ScQtDeliveryLoc();
						ScAddress deliveryAddress = populateDeliveryAddress(copyLoc.getPmAddressType());
						loc.setScDeliveryAddress(deliveryAddress);
						loc.setDeliveryAddress(getDeliveryAddressString(deliveryAddress));
						loc.setPmAddressType(copyLoc.getPmAddressType());
						loc.setTplSpecZoneDtl(this.getManagedBean()
								.getTplSpecZoneDtl());
						loc.setScQt(this.getManagedBean().getQt());

						setUserDetails(loc);
						locs.add(loc);
					}
					
					BriefingZoneBean bean = new BriefingZoneBean(
							tplSpecZoneDtl, locs);
					this.getManagedBean().getDeliveryLocBean().add(bean);
				}
			}
			
		}finally{
			specSo = null;
		}
	}
	
	private ScAddress populateDeliveryAddress(PmAddressType addressType) {
		PmAddress pmAddress = addressType.getPmAddress();
		ScAddress scAddress = new ScAddress();
		scAddress.setAddressName(pmAddress.getAddressName());
		scAddress.setAddress1(pmAddress.getAddress1());
		scAddress.setAddress2(pmAddress.getAddress2());
		scAddress.setAddress3(pmAddress.getAddress3());
		// scAddress.setAddress4(pmAddress.getAddress4());
		scAddress.setPostcode(pmAddress.getPostcode());
		scAddress.setPmCountry(pmAddress.getPmCountry());
		scAddress.setPmState(pmAddress.getPmState());
		scAddress.setPmDivision(pmAddress.getPmDivision());
		scAddress.setPmDistrict(pmAddress.getPmDistrict());
		scAddress.setPmCity(pmAddress.getPmCity());
		scAddress.setMapLatitude(pmAddress.getMapLatitude());
		scAddress.setMapLongitude(pmAddress.getMapLongitude());

		scAddress.setContactFaxArea(pmAddress.getFaxArea());
		scAddress.setContactFaxCountry(pmAddress.getFaxCountry());
		scAddress.setContactPhoneArea(pmAddress.getPhoneArea());
		scAddress.setContactPhoneCountry(pmAddress.getPhoneCountry());
		scAddress.setContactFaxNo(pmAddress.getFaxNo());
		scAddress.setContactPhoneNo(pmAddress.getPhoneNo());

		return scAddress;
	}
	
	private String getDeliveryAddressString(ScAddress scAddress) {
		String COMMA = ",";
		StringBuilder address = new StringBuilder();

		String NULL_VALUE = "null";
		if (StringUtils.isNotBlank(scAddress.getAddressName())
				&& !NULL_VALUE.equalsIgnoreCase(scAddress.getAddressName())) {
			address.append(scAddress.getAddressName());
		} else {
			address.append("-");
		}

		if (StringUtils.isNotBlank(scAddress.getAddress1())
				&& !NULL_VALUE.equalsIgnoreCase(scAddress.getAddress1())) {
			address.append(COMMA).append(scAddress.getAddress1());
		}

		if (StringUtils.isNotBlank(scAddress.getAddress2())
				&& !NULL_VALUE.equalsIgnoreCase(scAddress.getAddress2())) {
			address.append(COMMA).append(scAddress.getAddress2());
		}
		if (StringUtils.isNotBlank(scAddress.getAddress3())
				&& !NULL_VALUE.equalsIgnoreCase(scAddress.getAddress3())) {
			address.append(COMMA).append(scAddress.getAddress3());
		}
		if (StringUtils.isNotBlank(scAddress.getPostcode())
				&& !NULL_VALUE.equalsIgnoreCase(scAddress.getPostcode())) {
			address.append(COMMA).append(scAddress.getPostcode());
		}
		if (scAddress.getPmCity() != null
				&& scAddress.getPmCity().getCityName() != null
				&& scAddress.getPmCity().getCityName().length() > 0) {
			address.append(COMMA).append(scAddress.getPmCity().getCityName());
		}
		if (scAddress.getPmState() != null
				&& scAddress.getPmState().getStateName() != null
				&& scAddress.getPmState().getStateName().length() > 0) {
			address.append(COMMA).append(scAddress.getPmState().getStateName());
		}
		/*
		 * if (StringUtils.isNotBlank( scAddress.getAddress4())) {
		 * address.append(COMMA).append(scAddress.getAddress4()); }
		 */

		return address.toString();
	}

	/**
	 * Checks if is atleast one serive req.check service spec is existing or not
	 * in Technical checklist.
	 * 
	 * @param checklist
	 *            (List<ScQtCheckList> checklist)
	 * @return true, if is atleast one serive req
	 */
	private boolean isAtleastOneSeriveReq(List<ScQtCheckList> checklist) {
		int i = 0;
		if (null != checklist && !checklist.isEmpty()) {
			for (ScQtCheckList specChecklist : checklist) {
				if (QTConstants.TEMPLETE_SPEC_CODE
						.equalsIgnoreCase(specChecklist.getDefinationType()
								.getParameterCode())) {
					if (null != specChecklist.getScQtDoc()) {
						ITemplateSpecSO templateSpecSO = EpEjbBusinessDelegate
								.getInstance().getServiceObject(
										ModuleCodeConst.COMMON_COMPONENT,
										ITemplateSpecSO.class);
						TemplateSpecReqDTO specReqDTO = new TemplateSpecReqDTO();
						specReqDTO.setSpecMaster(specChecklist.getScQtDoc()
								.getTplSpecMaster());
						specReqDTO
								.setContentType(TemplateSpecConst.CONTENT_TYPE_QUESTION);
						TemplateSpecResDTO resDTO = templateSpecSO
								.getTemplateMaster(specReqDTO);
						/*
						 * specChecklist.getScQtDoc().setTplSpecMaster(
						 * resDTO.getSpecMaster());
						 */
						if (resDTO
								.getSpecMaster()
								.getItemTypeCode()
								.getParameterCode()
								.equalsIgnoreCase(
										ParameterConst.ITEM_TYPE_SERVICE)) {
							i++;
							if (i >= 1) {
								return true;
							}
						}
						templateSpecSO = null;
					}
				}
			}
		}

		return false;
	}

	private void checklistSorting(List<ScQtCheckList> checkList) {
		Collections.sort(checkList, new Comparator<ScQtCheckList>() {
			public int compare(ScQtCheckList checklist1,
					ScQtCheckList checklist2) {

				// return
				// checklist1.getChecklistSeq()-checklist2.getChecklistSeq();
				return new CompareToBuilder()
						.append(checklist1.getChecklistSeq(),
								checklist2.getChecklistSeq())
						.append(checklist1.getCreatedDate(),
								checklist2.getCreatedDate()).toComparison();
			}
		});
	}

	private Set<String> getNullPropertyNames(Object source, String... extra) {
		final BeanWrapper src = new BeanWrapperImpl(source);
		java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();
		Set<String> emptyNames = new HashSet<String>();
		for (java.beans.PropertyDescriptor pd : pds) {
			Object srcValue = src.getPropertyValue(pd.getName());
			if (srcValue == null) {
				debugLogger("attribute {} " + pd.getName());
				emptyNames.add(pd.getName());
			}
		}
		for (String args : extra) {
			emptyNames.add(args);
		}
		return emptyNames;
	}

	/**
	 * Debug logger.
	 * 
	 * @param message
	 *            the message
	 */
	private void debugLogger(Object message) {

		ScQt scQt = this.getManagedBean().getQt();
		if (scQt != null) {
			message += " @qtNo = " + scQt.getQtNo();
		}
		if (logger.isDebugEnabled()) {
			logger.debug(message);
		}
		/*
		 * else{ logger.info(message); }
		 */

	}

	/**
	 * The Class QTPagingDataModel.
	 */
	private class QTPagingDataModel extends PagingDataModel<ScQt> {

		/** The Constant serialVersionUID. */
		private static final long serialVersionUID = 1L;

		/** The clone qtn tender search bean. */
		private QtnTenderSearchBean cloneQtnTenderSearchBean;

		/**
		 * Instantiates a new QT paging data model.
		 * 
		 * @param componentId
		 *            the component id
		 * @param cloneQtnTenderSearchBean
		 *            the clone qtn tender search bean
		 */
		public QTPagingDataModel(String componentId,
				QtnTenderSearchBean cloneQtnTenderSearchBean) {
			super(componentId, false);
			this.cloneQtnTenderSearchBean = cloneQtnTenderSearchBean;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * my.ep.web.bean.paginator.PagingDataModel#pageChangeListener(my.ep
		 * .biz.bean.search.ISearchBean)
		 */
		@Override
		public ISearchResultBean<ScQt> pageChangeListener(
				ISearchBean<ScQt> searchBean) {
			if (logger.isDebugEnabled()) {
				logger.debug("pageChangeListener");
			}

			IQTDetailSO detailSO;
			try {
				detailSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);

				QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();

				populateCloneQTSearchCriteria(cloneQtnTenderSearchBean,
						searchBean);
				reqDTO.setQtSearchBean(searchBean);

				QuationTenderResDTO resDTO = detailSO
						.retriveQuationTenderDetailsBySearchCriteria(reqDTO);

				return resDTO.getQtResultBean();
			} finally {
				detailSO = null;
			}
		}
	}

	private void populateCloneQTSearchCriteria(
			QtnTenderSearchBean cloneQtnTenderSearchBean,
			ISearchBean<ScQt> qtSearchBean) {
		if (cloneQtnTenderSearchBean != null) {
			List<SearchFilterBean> searchFilterBean = new ArrayList<SearchFilterBean>();
			String qtTitle = cloneQtnTenderSearchBean.getQtTitle();
			if (!StringUtils.isBlank(qtTitle)) {
				qtSearchBean.getDataFilterBeans().add(
						new SearchFilterBean(QTConstants.QT_TITILE,
								SearchOperator.OPERATOR_LIKE.value(), qtTitle,
								true));
			}
			String qtNumber = cloneQtnTenderSearchBean.getQtNo();
			if (!StringUtils.isBlank(qtNumber)) {
				SearchFilterBean bean = new SearchFilterBean();
				bean.setSearchField(QTConstants.QT_NUMBER);
				bean.setSearchOperator(SearchOperator.OPERATOR_LIKE.value());
				bean.setSearchValue(qtNumber);
				searchFilterBean.add(bean);
			}

			if (null != this.getManagedBean().getQt()
					&& null != this.getManagedBean().getQt().getQtNo()) {
				List<String> qtNo = new ArrayList<String>();
				qtNo.add(this.getManagedBean().getQt().getQtNo());
				SearchFilterBean bean = new SearchFilterBean();
				bean.setSearchField(QTConstants.QT_NUMBER);
				bean.setSearchOperator(SearchOperator.OPERATOR_NOT_IN.value());
				bean.setSearchValue(qtNo);
				searchFilterBean.add(bean);
			}
			
			if (null != this.getManagedBean().getQt() 
					&& null != this.getManagedBean().getPanel() 
					//&& QTStatusConst.QT_PENDING_QT_SUBMISSION.equals(statId)
					){
				
				SearchFilterBean bean = new SearchFilterBean();
				bean.setSearchField("isPanel");
				bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
				bean.setSearchValue(this.getManagedBean().getPanel());
				searchFilterBean.add(bean);
			}
			
			if (null != this.getManagedBean().getQt()){
				
				SearchFilterBean bean = new SearchFilterBean();
				bean.setSearchField("isZonal");
				bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
				bean.setSearchValue(this.getManagedBean().getQt().isZonal());
				searchFilterBean.add(bean);
				
			}

			String procurementMode = cloneQtnTenderSearchBean
					.getProcurementMode();
			if (!StringUtils.isBlank(procurementMode)) {
				qtSearchBean.getDataFilterBeans().add(
						new SearchFilterBean("procurementMode.parameterCode",
								SearchOperator.OPERATOR_EQUALS.value(),
								procurementMode));
			} else {
				// Filter By - procurementMode
				List<String> procurementModes = new ArrayList<String>();
				procurementModes.add(ParameterConst.PROCUREMENT_MODE_QUOTATION);
				procurementModes.add(ParameterConst.PROCUREMENT_MODE_TENDER);
				qtSearchBean.getDataFilterBeans().add(
						new SearchFilterBean("procurementMode.parameterCode",
								SearchOperator.OPERATOR_IN.value(),
								procurementModes));
			}
			/*
			 * Date fromCreatedDate = cloneQtnTenderSearchBean
			 * .getFromCreatedDate(); Date toCreatedDate =
			 * cloneQtnTenderSearchBean.getToCreatedDate(); if (null !=
			 * fromCreatedDate || toCreatedDate != null) { SearchFilterBean bean
			 * = new SearchFilterBean();
			 * bean.setSearchField(QTConstants.CREATED_DATE);
			 * bean.setSearchOperator(SearchOperator.OPERATOR_BETWEEN.value());
			 * SearchBetweenValue<Date> betweenVal = new
			 * SearchBetweenValue<Date>( fromCreatedDate, toCreatedDate);
			 * bean.setSearchValue(betweenVal); searchFilterBean.add(bean); }
			 */
			Date fromCreatedDate = cloneQtnTenderSearchBean
					.getFromCreatedDate();
			Date toCreatedDate = cloneQtnTenderSearchBean.getToCreatedDate();
			if (null != fromCreatedDate || toCreatedDate != null) {
				Calendar c1 = Calendar.getInstance();
				c1.setTime(fromCreatedDate);
				Calendar fromDate = Calendar.getInstance();
				fromDate.set(c1.get(Calendar.YEAR), c1.get(Calendar.MONTH),
						c1.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
				fromDate.set(Calendar.MILLISECOND, 0);
				fromCreatedDate = fromDate.getTime();
				// Setting current date as To-date to perform search if user
				// selected only From Date
				if (toCreatedDate == null) {
					toCreatedDate = DateUtils.getCurrentDateWithoutTime();
				}
				if (toCreatedDate != null) {
					Calendar c2 = Calendar.getInstance();
					c2.setTime(toCreatedDate);
					Calendar toDate = Calendar.getInstance();
					toDate.set(c2.get(Calendar.YEAR), c2.get(Calendar.MONTH),
							c2.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
					toDate.set(Calendar.MILLISECOND, 0);
					toCreatedDate = toDate.getTime();
				}
				SearchFilterBean bean = new SearchFilterBean();
				bean.setSearchField(QTConstants.CREATED_DATE);
				bean.setSearchOperator(SearchOperator.OPERATOR_BETWEEN.value());
				SearchBetweenValue<Date> betweenVal = new SearchBetweenValue<Date>(
						fromCreatedDate, toCreatedDate);
				bean.setSearchValue(betweenVal);
				searchFilterBean.add(bean);
			}

			Date fromPublishDate = cloneQtnTenderSearchBean
					.getFromPublishDate();
			Date toPublishDate = cloneQtnTenderSearchBean.getToPublishDate();
			if (null != fromPublishDate || null != toPublishDate) {
				SearchFilterBean bean = new SearchFilterBean();
				bean.setSearchField(QTConstants.PUBLISH_DATE);
				bean.setSearchOperator(SearchOperator.OPERATOR_BETWEEN.value());
				// Setting current date as To-date to perform search if user
				// selected only From Date
				if (toPublishDate == null) {
					toPublishDate = DateUtils.getCurrentDateWithoutTime();
				}
				SearchBetweenValue<Date> betweenVal = new SearchBetweenValue<Date>(
						fromPublishDate, toPublishDate);
				bean.setSearchValue(betweenVal);
				searchFilterBean.add(bean);
			}

			Date fromClosingDate = cloneQtnTenderSearchBean
					.getFromClosingDate();
			Date toClosingDate = cloneQtnTenderSearchBean.getToClosingDate();
			if (null != fromClosingDate || null != toClosingDate) {
				SearchFilterBean bean = new SearchFilterBean();
				bean.setSearchField(QTConstants.CLOSED_DATE);
				bean.setSearchOperator(SearchOperator.OPERATOR_BETWEEN.value());
				// Setting current date as To-date to perform search if user
				// selected only From Date
				if (toClosingDate == null) {
					toClosingDate = DateUtils.getCurrentDateWithoutTime();
				}
				SearchBetweenValue<Date> betweenVal = new SearchBetweenValue<Date>(
						fromClosingDate, toClosingDate);
				bean.setSearchValue(betweenVal);
				searchFilterBean.add(bean);
			}
			String status = cloneQtnTenderSearchBean.getQtStatus();
			if (!StringUtils.isBlank(status)) {
				qtSearchBean
						.getDataFilterBeans()
						.add(new SearchFilterBean(
								"scQtStatus.pmStatus.statusId",
								SearchOperator.OPERATOR_EQUALS.value(), status));
				qtSearchBean.getDataFilterBeans().add(
						new SearchFilterBean("scQtStatus.current",
								SearchOperator.OPERATOR_EQUALS.value(), true));
			}else{
				Long[] stats= {60002L,60024L,60003L};
				ArrayList<Long> s=new ArrayList<Long>(Arrays.asList(stats));
				qtSearchBean
				.getDataFilterBeans()
				.add(new SearchFilterBean(
						"scQtStatus.pmStatus.statusId",
						SearchOperator.OPERATOR_NOT_IN.value(), s));
				qtSearchBean.getDataFilterBeans().add(
						new SearchFilterBean("scQtStatus.current",
								SearchOperator.OPERATOR_EQUALS.value(), true));
			}
			// Filter by - ptj org profileId
			qtSearchBean.getDataFilterBeans().add(
					new SearchFilterBean("pmOrgProfile.orgProfileId",
							SearchOperator.OPERATOR_EQUALS.value(),
							getPTJOrgProfileByLoginUser().getOrgProfileId()));

			// Filter by - evaluationType
			String commType = getManagedBean().getCommType();
			String evaluationType = getManagedBean().getQt()
					.getEvaluationType();
			if (StringUtils.isNotBlank(evaluationType)
					&& (commType == null || !ParameterConst.COMMITTEE_TYPE_SPECIFICATION_COMMITTEE
							.equals(commType))) {
				qtSearchBean.getDataFilterBeans().add(
						new SearchFilterBean("evaluationType",
								SearchOperator.OPERATOR_EQUALS.value(),
								evaluationType));
			}

			// Adding search filterbeans to searchbean.
			qtSearchBean.getDataFilterBeans().addAll(searchFilterBean);
		}
	}

	public TreeNode getRoot() {
		return root;
	}

	public void setRoot(TreeNode root) {
		this.root = root;
	}

	public TreeNode[] getSelectedNodes() {
		return selectedNodes;
	}

	public void setSelectedNodes(TreeNode[] selectedNodes) {
		this.selectedNodes = selectedNodes;
	}

	public boolean isShowTree() {
		return showTree;
	}

	public void setShowTree(boolean showTree) {
		this.showTree = showTree;
	}
	
	/**
	 * Get parameter code select items by parameter type for radio buttons.
	 * 
	 * @return array of SelectItem
	 */
	public SelectItem[] getProcurementModeDropdownValues() {
		ISearchBean<PmParameterDesc> searchBean = new SearchBean<PmParameterDesc>();
		List<String> recommendList = new ArrayList<String>();
		recommendList.add(ParameterConst.PROCUREMENT_MODE_DIRECT_NEGO);
		recommendList.add(ParameterConst.PROCUREMENT_MODE_DIRECT_PURCHASE);
		searchBean.getDataFilterBeans().add(
				new SearchFilterBean("pmParameter.parameterCode",
						SearchOperator.OPERATOR_NOT_IN.value(), recommendList));
		SelectItem[] items = getDropdownCodeValuesBySearchBean(
				ParameterTypeConst.SC_PROCUREMENT_MODE, searchBean);
		return items;
	}
	
	/**
	 * Clone search qt action.
	 */
	public void cloneSearchQtAction() {
		getManagedBean().setCloneCommitteeSelectQt(null);

		getManagedBean().getCloneSearchQTDataModel().reloadData();
	}
	
	/**
	 * Reset clone.
	 */
	public void resetClone() {
		debugLogger("resetClone");
		this.getManagedBean().getCloneQtnTenderSearchBean().setQtTitle(null);
		this.getManagedBean().getCloneQtnTenderSearchBean().setQtNo(null);
		this.getManagedBean().getCloneQtnTenderSearchBean()
				.setProcurementMode(null);
		this.getManagedBean().getCloneQtnTenderSearchBean()
				.setFromCreatedDate(null);
		this.getManagedBean().getCloneQtnTenderSearchBean()
				.setToCreatedDate(null);
		this.getManagedBean().getCloneQtnTenderSearchBean().setQtStatus(null);
		this.getManagedBean().setSearchcommitteeNode(new DefaultTreeNode());
		this.getManagedBean().getCloneQtnTenderSearchBean()
				.setFromPublishDate(null);
		this.getManagedBean().getCloneQtnTenderSearchBean()
				.setToPublishDate(null);
		this.getManagedBean().getCloneQtnTenderSearchBean()
				.setFromClosingDate(null);
		this.getManagedBean().getCloneQtnTenderSearchBean()
		.setToClosingDate(null);
		getManagedBean().setCloneCommitteeSelectQt(null);
	}
}

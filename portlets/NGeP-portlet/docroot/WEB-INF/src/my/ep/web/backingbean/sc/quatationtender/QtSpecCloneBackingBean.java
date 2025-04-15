package my.ep.web.backingbean.sc.quatationtender;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
import my.ep.constant.pm.RuleTypeConst;
import my.ep.constant.sc.QTStatusConst;
import my.ep.constant.sc.SourcingConstants;
import my.ep.constant.tpl.TemplateSpecConst;
import my.ep.constants.CommonConstants;
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
import my.ep.persistence.entity.ct.circular.CtCircularVer;
import my.ep.persistence.entity.ct.circular.panel.CtPanelItemGroup;
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
import my.ep.persistence.entity.sc.quationtender.ScQtDeduction;
import my.ep.persistence.entity.sc.quationtender.ScQtDeductionClause;
import my.ep.persistence.entity.sc.quationtender.ScQtDeliveryLoc;
import my.ep.persistence.entity.sc.quationtender.ScQtDoc;
import my.ep.persistence.entity.sc.quationtender.ScQtProgressivePayment;
import my.ep.persistence.entity.sc.quationtender.ScQtSchedule;
import my.ep.persistence.entity.sc.quationtender.ScQtScoreOfferPrice;
import my.ep.persistence.entity.sc.quationtender.ScQtScoreSetting;
import my.ep.persistence.entity.sc.quationtender.ScQtScoreWeightage;
import my.ep.persistence.entity.sc.quationtender.ScQtSupplierCriteria;
import my.ep.persistence.entity.tpl.spec.TplSpecMaster;
import my.ep.persistence.entity.tpl.spec.TplSpecQuestion;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneDtl;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneQty;
import my.ep.type.aut.SearchOperator;
import my.ep.util.SLAUtils;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.backingbean.sc.specification.SpecificationBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
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
	private static final Logger logger = EpLogger.getLogger(QTCloneBackingbean.class);

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
	private String scoreTypeN = ""; // ahmaa-#25138
	private static final BigDecimal TECHNICAL_ITEM_SCORE = new BigDecimal(50);//35113

	public QtSpecCloneBackingBean() {
		this.root = new DefaultTreeNode("Root Node", null);

		TreeNode child = new DefaultTreeNode(getLabel(ALL), this.root);
		child.setParent(this.root);

		TreeNode descendent1 = new DefaultTreeNode(getLabel(TECHNICAL_CHECKLIST), child);
		descendent1.setParent(child);
		
		TreeNode descendent2 = new DefaultTreeNode(getLabel(FINANCIAL_CHECKLIST), child);
		descendent2.setParent(child);
		
		TreeNode descendent3 = new DefaultTreeNode(getLabel(TECHNICAL_SCORING), child);
		descendent3.setParent(child);
		
		TreeNode descendent4 = new DefaultTreeNode(getLabel(FINANCIAL_SCORING), child);
		descendent4.setParent(child);
		
		TreeNode descendent5 = new DefaultTreeNode(getLabel(OVERALL_SCORING), child);
		descendent5.setParent(child);
		
		TreeNode descendent6 = new DefaultTreeNode(getLabel(DELIVERY_SERVICE_ADDRESS), child);
		descendent6.setParent(child);
		
		TreeNode descendent7 = new DefaultTreeNode(getLabel(FULFILMENT_SCHEDULE), child);
		descendent7.setParent(child);
		
		TreeNode descendent8 = new DefaultTreeNode(getLabel(SPECIFICATION), child);
		descendent8.setParent(child);
	}

	private String getLabel(String label) {
		FacesContext context = FacesContext.getCurrentInstance();
		ResourceBundle bundle = context.getApplication().getResourceBundle(context, "lbl");
		String message = bundle.getString(label);
		
		return message;
	}

	public void cloneCommitteePopupAction() {
		// logic to check whether committe will allow for clone or not..
		// checkIsSpecCommitteFormOrNot();
		getManagedBean().setCloneCommitteeSelectQt(null);
		
		this.getManagedBean().setSelectedCloneTender(null);

		IPagingDataModel<ScQt> cloneSearchQTDataModel = getManagedBean().getCloneSearchQTDataModel();

		if (null == cloneSearchQTDataModel) {
			getManagedBean().setCloneSearchQTDataModel(
					new QTPagingDataModel("clonesearchCommitteid2", getManagedBean().getCloneQtnTenderSearchBean()));
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
		this.getManagedBean().setSearchcommitteeNode(new DefaultTreeNode());
		
		ScQt scQt = (ScQt) event.getObject();
		
		this.getManagedBean().setCloneCommitteeSelectQt(scQt);
		this.showTree = true;
	}
	
	private ScQtCheckList retrieveScoreNValue(ScQtCheckList originCL, ScQtCheckList copyCL) {
		IQTSpecSO specSo = null;
		SpecificationResDTO resDTO = null;

		try {
			SpecificationReqDTO reqDTO = new SpecificationReqDTO();
			specSo = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
					IQTSpecSO.class);
			
			reqDTO.setScQtCheckList(originCL);
			
			resDTO = specSo.retrieveCheckListScoreNValue(reqDTO);
			
			if (null != resDTO.getScQtCheckListValue()) {
				ScQtCheckListValue origin = resDTO.getScQtCheckListValue();
				ScQtCheckListValue checkListValue = new ScQtCheckListValue();
				
				if (null != copyCL.getScQtCheckListValue()) {
					checkListValue = copyCL.getScQtCheckListValue();
				} else {
					checkListValue.setScQtCheckList(copyCL);
				}
				
				setUserDetails(checkListValue);
				
				checkListValue.setYear(origin.getYear());
				checkListValue.setAllEmployee(origin.isAllEmployee());
				checkListValue.setFromDate(origin.getFromDate());
				checkListValue.setToDate(origin.getToDate());
				
				copyCL.setScQtCheckListValue(checkListValue);
			}

			if (resDTO.getCheckListScores() != null) {
				List<ScQtCheckListScore> scores = new ArrayList<ScQtCheckListScore>();
				
				for (ScQtCheckListScore origin : resDTO.getCheckListScores()) {
					ScQtCheckListScore copy = new ScQtCheckListScore();
					copy.setCheckList(copyCL);
					copy.setRangeFrom(origin.getRangeFrom());
					copy.setRangeTo(origin.getRangeTo());
					copy.setScore(origin.getScore());
					copy.setScoreType(origin.getScoreType());
					copy.setCriteriaType(origin.getCriteriaType());
					
					scores.add(copy);
				}
				
				if (null == copyCL.getScQtCheckListScores()) {
					copyCL.setScQtCheckListScores(scores);
				}
			}
		} finally {
			specSo = null;
		}
		
		return copyCL;
	}
	
	private boolean isChecklistDefault(ScQtCheckList cl) {
		List<ScQtCheckList> list = this.getManagedBean().getScQtCheckSt2financeProposalList();
		
		if (list != null) {
			for (ScQtCheckList def : list) {
				if (def.getTitle().equals(cl.getTitle())) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	private ScQtCheckList getDefault(String title) {
		IQTSpecSO specSo = null;
		SpecificationResDTO resDTO = null;
		
		try {
			SpecificationReqDTO reqDTO = new SpecificationReqDTO();
			specSo = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
					IQTSpecSO.class);
			
			reqDTO.setScQt(this.getManagedBean().getQt());
			reqDTO.setSpecType(SourcingConstants.FINANCIAL_DOCTYPE);
			
			resDTO = specSo.retrieveCheckLists(reqDTO);
			
			if (null != resDTO.getFinancialCheckLists()) {
				for (ScQtCheckList cl : resDTO.getFinancialCheckLists()) {
					if (title.equals(cl.getTitle())) {
						return cl;
					}
				}
			}
		} finally {
			specSo = null;
		}

		return null;
	}
	
	private List<ScQtCheckList> getCheckListInDB(ScQt scqt, String type) {
		IQTSpecSO specSo = null;
		SpecificationResDTO resDTO = null;
		
		try {
			SpecificationReqDTO reqDTO = new SpecificationReqDTO();
			specSo = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
					IQTSpecSO.class);
			
			reqDTO.setScQt(scqt);
			reqDTO.setSpecType(type);
			// reqDTO.setSpecType(SourcingConstants.FINANCIAL_DOCTYPE);
			// reqDTO.setSpecType(SourcingConstants.TECHNICAL_DOCTYPE);
			
			resDTO = specSo.retrieveCheckLists(reqDTO);
		} finally {
			specSo = null;
		}

		if (SourcingConstants.FINANCIAL_DOCTYPE.equals(type)) {
			return resDTO.getFinancialCheckLists();
		}
		
		if (SourcingConstants.TECHNICAL_DOCTYPE.equals(type)) {
			return resDTO.getTechnicalCheckList();
		}

		return null;
	}
	
	private void retrieveFinancialChecklist(ScQt scqt) throws FileNotFoundException {
		IQTSpecSO specSo = null;
		SpecificationResDTO resDTO = null;

		if (getManagedBean().getMarkDeletedFinChecklist() == null) {
			getManagedBean().setMarkDeletedFinChecklist(new ArrayList<ScQtCheckList>());
		}
		
		try {
			SpecificationReqDTO reqDTO = new SpecificationReqDTO();
			specSo = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
					IQTSpecSO.class);
			
			reqDTO.setScQt(scqt);
			reqDTO.setSpecType(SourcingConstants.FINANCIAL_DOCTYPE);
			
			resDTO = specSo.retrieveCheckLists(reqDTO);
			
			if (null != resDTO.getFinancialCheckLists()) {
				List<ScQtCheckList> toAdd = new ArrayList<ScQtCheckList>();
				List<ScQtCheckList> localcheckList = this.getManagedBean().getScQtCheckSt2financeProposalList();
				
				for (ScQtCheckList checklist : resDTO.getFinancialCheckLists()) {

					if (!ParameterConst.DOCUMENT_DEFINITION_TYPE_TEMPLATE_SPECIFICATION.equalsIgnoreCase(checklist
							.getDefinationType().getParameterCode())) {
						// if(!checklist.isManditory()){
						ScQtCheckList copy = copyBean(checklist);
						copy.setScQt(this.getManagedBean().getQt());
						
						copy = retrieveScoreNValue(checklist, copy);

						if (null != copy.getPmDocument()) {
							if (!SourcingConstants.DOC_DEF_KEY_SERVICE_SCOPE.equalsIgnoreCase(checklist.getPmDocument()
									.getDocDefinitionKey())) {
								toAdd.add(copy);
							}
						} else {
							toAdd.add(copy);
						}
						// }else{
						// ScQtCheckList copy =getDefault(checklist.getTitle());
						// if(null!=copy){
						// copy=retrieveScoreNValue(checklist,copy);
						// copy=setStatus(copy);
						// }else{
						// copy = copyBean(checklist);
						// copy.setScQt(this.getManagedBean().getQt());
						// copy=retrieveScoreNValue(checklist,copy);
						// }
						// toAdd.add(copy);
						// }
					}
				}

				// this.getManagedBean().setMarkDeletedFinChecklist(new
				// ArrayList<ScQtCheckList>());
				// List<ScQtCheckList>
				// finList=getCheckListInDB(this.getManagedBean().getQt(),SourcingConstants.FINANCIAL_DOCTYPE);
				// if(null!=finList
				// && !finList.isEmpty()
				// && finList.size()!=0){
				//
				// List<ScQtCheckList> toRemove=new ArrayList<ScQtCheckList>();
				// for(ScQtCheckList toDelete:finList){
				// if(!toDelete.isManditory()){
				// toDelete.setSelected(true);
				// toDelete.setDeleted(true);
				// toRemove.add(toDelete);
				// }
				// }
				// //this.getManagedBean().getScQtCheckSt2financeProposalList().removeAll(toRemove);
				// this.getManagedBean().setMarkDeletedFinChecklist(toRemove);
				//
				// }

				List<ScQtCheckList> toRemove = new ArrayList<ScQtCheckList>();
				
				if (null != this.getManagedBean().getScQtCheckSt2financeProposalList()
						&& !this.getManagedBean().getScQtCheckSt2financeProposalList().isEmpty()
						&& this.getManagedBean().getScQtCheckSt2financeProposalList().size() != 0) {

					for (ScQtCheckList toDelete : this.getManagedBean().getScQtCheckSt2financeProposalList()) {
						// 34701_start
						if (ParameterConst.DOCUMENT_DEFINITION_TYPE_TEMPLATE_SPECIFICATION.equalsIgnoreCase(toDelete
								.getDefinationType().getParameterCode()) && this.getManagedBean().getQt().isZonal()) {
							toDelete.setCloned(Boolean.TRUE);

						}
						// 34701 -end-

						toDelete.setSelected(true);
						toDelete.setDeleted(true);
						// toRemove.add(toDelete);
						// }
					}
				}

				this.getManagedBean().setMarkDeletedFinChecklist(
						this.getManagedBean().getScQtCheckSt2financeProposalList());
				this.getManagedBean().setScQtCheckSt2financeProposalList(toAdd);
				
				if (CollectionUtils.isNotEmpty(this.getManagedBean().getScQtCheckSt2financeProposalList())) {
					checklistSorting(this.getManagedBean().getScQtCheckSt2financeProposalList());
				}
			}
		} finally {
			specSo = null;
		}
	}
	
	private void removeAdditional(List<ScQtCheckList> list) {
		List<ScQtCheckList> toRemove = new ArrayList<ScQtCheckList>();
		
		for (ScQtCheckList o : list) {
			if (!o.isManditory()) {
				list.remove(o);
			}
		}
	}

	private void retrieveTechnicalChecklist(ScQt scqt) throws FileNotFoundException {
		IQTSpecSO specSo = null;
		SpecificationResDTO resDTO = null;

		try {

			List<ScQtCheckList> localcheckList = new ArrayList<ScQtCheckList>();
			
			this.getManagedBean().getQt().setScQtChecklists(localcheckList);
			
			SpecificationReqDTO reqDTO = new SpecificationReqDTO();
			specSo = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
					IQTSpecSO.class);
			
			reqDTO.setScQt(scqt);
			reqDTO.setSpecType(SourcingConstants.TECHNICAL_DOCTYPE);
			
			resDTO = specSo.retrieveCheckLists(reqDTO);

			// if (!isAtleastOneSeriveReq(resDTO.getTechnicalCheckList())) {}

			if (null != resDTO.getTechnicalCheckList()) {
				// Collections.sort(resDTO.getTechnicalCheckList());
				for (ScQtCheckList checklist : resDTO.getTechnicalCheckList()) {
					ScQtCheckList copy = copyBean(checklist);
					copy.setScQt(this.getManagedBean().getQt());
					
					if (!ParameterConst.DOCUMENT_DEFINITION_TYPE_TEMPLATE_SPECIFICATION.equalsIgnoreCase(checklist
							.getDefinationType().getParameterCode())) {
						copy = retrieveScoreNValue(checklist, copy);
					}
					
					if (null != copy.getPmDocument()) {
						/*
						 * if (!SourcingConstants.DOC_DEF_KEY_SERVICE_SCOPE
						 * .equalsIgnoreCase(checklist.getPmDocument()
						 * .getDocDefinitionKey())) {
						 */// 32120
						localcheckList.add(copy);
						// }//32120
					} else {
						// 33858if
						// (!ParameterConst.DOCUMENT_DEFINITION_TYPE_SAMPLE
						// .equalsIgnoreCase(checklist.getDefinationType().getParameterCode()))
						// {//21695&25635-start
						localcheckList.add(copy);
						// 33858}//21695&25635-End
					}
				}
			}

			// this.getManagedBean().setMarkDeletedTechChecklist(new
			// ArrayList<ScQtCheckList>());
			// List<ScQtCheckList>
			// tecList=getCheckListInDB(this.getManagedBean().getQt(),SourcingConstants.TECHNICAL_DOCTYPE);
			// if(null!=tecList
			// && !tecList.isEmpty()
			// && tecList.size()!=0){
			//
			// List<ScQtCheckList> toRemove=new ArrayList<ScQtCheckList>();
			// for(ScQtCheckList toDelete:tecList){
			// if(!toDelete.isManditory()){
			// toDelete.setSelected(true);
			// toDelete.setDeleted(true);
			// toRemove.add(toDelete);
			// }
			// }
			//
			// this.getManagedBean().setMarkDeletedTechChecklist(toRemove);
			//
			// }

			if (null != this.getManagedBean().getScQtCheckSt2techProposal()
					&& !this.getManagedBean().getScQtCheckSt2techProposal().isEmpty()
					&& this.getManagedBean().getScQtCheckSt2techProposal().size() != 0) {

				for (ScQtCheckList toDelete : this.getManagedBean().getScQtCheckSt2techProposal()) {
					toDelete.setSelected(true);
					toDelete.setDeleted(true);
				}
			}

			this.getManagedBean().setMarkDeletedTechChecklist(this.getManagedBean().getScQtCheckSt2techProposal());
			this.getManagedBean().setScQtCheckSt2techProposal(localcheckList);
			
			if (CollectionUtils.isNotEmpty(this.getManagedBean().getScQtCheckSt2techProposal())) {
				checklistSorting(this.getManagedBean().getScQtCheckSt2techProposal());
			}

			localcheckList = null;

		} finally {
			specSo = null;
		}
	}

	private ScQtCheckList copyStandardChecklist(ScQtCheckList origin) throws FileNotFoundException {
		this.getManagedBean().setTechStandaraChecklist(true);
		
		ScQtCheckList copy = new ScQtCheckList();

		copy.setTitle(origin.getTitle());
		copy.setDefinationType(origin.getDefinationType());
		copy.setAction(origin.getAction());
		copy.setManditory(origin.isManditory());
		copy.setScoringReq(origin.isScoringReq());
		
		if (QTConstants.TECHNICAL_DOC_TYPE.equals(origin.getDocCategory())) {
			copy.setDocCategory(QTConstants.TECHNICAL_DOC_TYPE);
		}
		
		if (QTConstants.FINANCIAL_DOC_TYPE.equals(origin.getDocCategory())) {
			copy.setDocCategory(QTConstants.FINANCIAL_DOC_TYPE);
		}

		copy.setPmDocument(origin.getPmDocument());
		copy.setChecklistSeq(origin.getChecklistSeq());
		
		if (origin.getScQtCheckListAttachment() != null) {
			ScQtCheckListAttachment att = copyAttachment(origin, copy);
			
			if (null != att) {
				copy.setScQtCheckListAttachment(att);
			}
		}

		return copy;
	}

	private ScQtCheckListAttachment copyAttachment(ScQtCheckList originCheckList, ScQtCheckList copyCheckList)
			throws FileNotFoundException {
		ScQtCheckListAttachment copy = new ScQtCheckListAttachment();
		copy.setScQtCheckList(copyCheckList);
		copy.setDocNo(this.getManagedBean().getQt().getQtNo());
		
		try {
			uploadAttachment(copy, originCheckList.getScQtCheckListAttachment());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
		return copy;
	}

	private void uploadAttachment(ScAttachment copy, ScAttachment origin) throws FileNotFoundException, IOException {
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
	private ScQtCheckList constructQtDocByType(TplSpecMaster specMaster, String docType) {
		IParameterSO parameterSo = null;
		parameterSo = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
				IParameterSO.class);
		ScQtDoc scqtDocDest = new ScQtDoc();
		
		setUserDetails(scqtDocDest);
		
		specMaster.setStatus(getPmStatusByStatusId(QTStatusConst.BSV_PENDING));
		specMaster.setScQtDoc(scqtDocDest);
		
		if (null == specMaster.getItemOriginCode() || null == specMaster.getItemOriginCode().getParameterId()) {
			ParameterReqDTO localGoodsReqDTO = new ParameterReqDTO();
			localGoodsReqDTO.setParameterType(ParameterTypeConst.ITEM_ORIGIN);
			localGoodsReqDTO.setParameterCode(ParameterConst.LOCAL_GOODS);
			
			ParameterResDTO localGoodsResDTO = parameterSo.retrieveParameterByTypeAndCode(localGoodsReqDTO);
			
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
		
		ParameterResDTO specTyperesDTO = parameterSo.retrieveParameterByTypeAndCode(specType);

		ParameterReqDTO checkListActionType = new ParameterReqDTO();
		checkListActionType.setParameterType(ParameterTypeConst.SC_SUPPLIER_ACTION);
		checkListActionType.setParameterCode(ParameterConst.CHECKLIST_ACTION_INPUT);
		
		ParameterResDTO checkListactionresDTO = parameterSo.retrieveParameterByTypeAndCode(checkListActionType);
		
		checkList.setChecklistSeq(SourcingConstants.CHECKLIST_SEQ_TEMPLATE_SPEC);
		// PmStatus status = new PmStatus();
		// status.setStatusId(1l);
		// checkList.setPmStatus(status);
		checkList.setScQtDoc(scqtDocDest);
		checkList.setScoringReq(this.getManagedBean().getQt().isTechEvaluationReq());
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
		ScQtCheckList qtCheckList = null;
		
		try {
			ITemplateSpecSO templateSpecSO = EpEjbBusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.COMMON_COMPONENT, ITemplateSpecSO.class);
			TemplateSpecReqDTO specReqDTO = new TemplateSpecReqDTO();
			
			specReqDTO.setSpecMaster(origin);
			specReqDTO.setContentType(TemplateSpecConst.CONTENT_TYPE_QUESTION);
			
			TemplateSpecResDTO resDTO = templateSpecSO.getTemplateMaster(specReqDTO);
			
			// 33858-start
			if (this.getManagedBean().getQt().isSampleReq()) {
				resDTO.getSpecMaster().setSampleReq(Boolean.FALSE);
			} else {
				resDTO.getSpecMaster().setSampleReq(Boolean.TRUE);
			}
			// 33858-End

			TplSpecMaster copy = TemplateSpecHelper.getInstance().cloneTplSpecMaster(resDTO.getSpecMaster(), true,
					getManagedBean().getQt().isTechEvaluationReq(), true, false); // #28784

			copy.setProcessType(ParameterConst.TPL_DOC_PROCESS_TYPE_SOURCING);
			
			if (getManagedBean().getQt().isZonal()) {
				ScQtSupplierCriteria supplierCriteria = getManagedBean().getQt().getScQtSupplierCriteria();
				
				if (null != supplierCriteria && null != supplierCriteria.getQtZoneCriterias()) {
					TemplateSpecHelper.getInstance().updateZoneDtl(copy, supplierCriteria.getQtZoneCriterias());
					
					if (copy.getTplSpecQuestions() != null) {// 21695&25635-start
						for (TplSpecQuestion specQuestion : copy.getTplSpecQuestions()) {
							if (null != specQuestion
									&& TemplateSpecConst.QUESTION_TYPE_ITEM.equalsIgnoreCase(specQuestion
											.getQuestionType())) {
								if (null != specQuestion.getTplSpecZoneQties()
										&& CollectionUtils.isNotEmpty(specQuestion.getTplSpecZoneQties())
										&& null != this.getManagedBean().getTemplateSpecZoneDtls()
										&& CollectionUtils.isNotEmpty(this.getManagedBean().getTemplateSpecZoneDtls())
										&& specQuestion.getTplSpecZoneQties().size() == this.getManagedBean()
												.getTemplateSpecZoneDtls().size()) {
									for (int i = 0; i < specQuestion.getTplSpecZoneQties().size(); i++) {
										specQuestion
												.getTplSpecZoneQties()
												.get(i)
												.setTplSpecZoneDtl(
														this.getManagedBean().getTemplateSpecZoneDtls().get(i));
										specQuestion.getTplSpecZoneQties().get(i).setTplSpecZoneOffer(null);// 33950
									}
								} else {// 33893-start
									if (null != specQuestion.getTplSpecZoneQties()
											&& CollectionUtils.isNotEmpty(specQuestion.getTplSpecZoneQties())) {// 25635&31545-start
										for (TplSpecZoneQty zoneQty : specQuestion.getTplSpecZoneQties()) {
											zoneQty.setTplSpecZoneOffer(null);
										}
									}// 25635&31545-start
									
									specQuestion.setTplSpecZoneQties(new ArrayList<TplSpecZoneQty>());// 33893
								}// 33893-End
							}
						}
					}// 21695&25635-End
				} else {
					throw new EpException("Please choose the zones before proceed.");
				}
			} else {
				TemplateSpecHelper.getInstance().updateZoneDtl(copy, null);
			}

			qtCheckList = constructQtDocByType(copy, SourcingConstants.TECHNICAL_DOCTYPE);
			
			qtCheckList.setTitle(origin.getSpecName());
			
			TemplateSpecBean templateSpecBean = getManagedBean().getTemplateSpecBean();
			
			if (this.getManagedBean().getQt().isPartial() == true) {// 21695&25635_partialOfferCloneStart
				this.getManagedBean().setSpecItemWeightageRequired(true);
				
				if (this.getManagedBean().getTemplateSpecBean() != null) {
					// if(this.getManagedBean().getTemplateSpecBean().getSpecMaster()
					// != null){
					copy.setWeightageReq(false);
					copy.getTplSpecQuestions();
					
					if (copy.getTplSpecQuestions() != null) {
						for (TplSpecQuestion specQuestWeightage : copy.getTplSpecQuestions()) {
							if (specQuestWeightage != null) {
								specQuestWeightage.setWeightageReq(false);
							}
						}
					}
				}
				// }
			}// 21695&25635_partialOfferCloneEnd

			templateSpecBean.setSpecMaster(copy);

		} finally {

		}

		return qtCheckList;
	}

	private ScQtCheckList copyBean(ScQtCheckList origin) throws FileNotFoundException {
		ScQtCheckList copy = new ScQtCheckList();
		
		if (ParameterConst.DOCUMENT_DEFINITION_TYPE_TEMPLATE_SPECIFICATION.equalsIgnoreCase(origin.getDefinationType()
				.getParameterCode())) {
			copy = copySpecTemplate(origin.getScQtDoc().getTplSpecMaster());
			
			copy.setChecklistSeq(origin.getChecklistSeq());
		} else {
			copy = copyStandardChecklist(origin);
		}

		copy = setStatus(copy);

		setUserDetails(copy);
		
		return copy;
	}
	
	private ScQtCheckList setStatus(ScQtCheckList copy) {
		Long statusId = null;
		
		if (null != copy.getDefinationType()
				&& ParameterConst.DOCUMENT_DEFINITION_TYPE_TEMPLATE_SPECIFICATION.equalsIgnoreCase(copy
						.getDefinationType().getParameterCode())) {
			copy.setPmStatus(getPmStatusByStatusId(QTStatusConst.TS_PENDING_SUBMISSION));
		}

		if (null != copy.getDefinationType()
				&& ParameterConst.DOCUMENT_DEFINITION_TYPE_SAMPLE.equalsIgnoreCase(copy.getDefinationType()
						.getParameterCode())) {
			// 37859,25635 -start-
			copy.setPmStatus(getPmStatusByStatusId(QTStatusConst.TS_PENDING_SUBMISSION));
			// 37859,25635 -end-
		}

		if (null != copy.getDefinationType()
				&& ParameterConst.DOCUMENT_DEFINITION_TYPE_UPLOAD.equalsIgnoreCase(copy.getDefinationType()
						.getParameterCode())) {
			copy.setPmStatus(getPmStatusByStatusId(QTStatusConst.TS_COMPLETED));
		}

		if (null != copy.getDefinationType()
				&& ParameterConst.DOCUMENT_DEFINITION_TYPE_UPLOAD_AND_DOWNLOAD.equalsIgnoreCase(copy
						.getDefinationType().getParameterCode())) {
			copy.setPmStatus(getPmStatusByStatusId(QTStatusConst.TS_COMPLETED));
		}

		if (null != copy.getDefinationType()
				&& ParameterConst.DOCUMENT_DEFINITION_TYPE_NO_ACTION.equalsIgnoreCase(copy.getDefinationType()
						.getParameterCode())) {
			copy.setPmStatus(getPmStatusByStatusId(QTStatusConst.TS_COMPLETED));
		}

		if (null != copy.getDefinationType()
				&& ParameterConst.DOCUMENT_DEFINITION_TYPE_SUPPLIER_UPLOAD_AND_DOWNLOAD.equalsIgnoreCase(copy
						.getDefinationType().getParameterCode())) {
			copy.setPmStatus(getPmStatusByStatusId(QTStatusConst.TS_COMPLETED));
		}
		
		if (null != copy.getDefinationType()
				&& ParameterConst.DOCUMENT_DEFINITION_TYPE_PRESENTATION_DEMO.equalsIgnoreCase(copy.getDefinationType()
						.getParameterCode())) {
			copy.setPmStatus(getPmStatusByStatusId(QTStatusConst.TS_PENDING_SUBMISSION));// 28784&25635-start
		}
		
		if (null != copy.getDefinationType()
				&& ParameterConst.DOCUMENT_DEFINITION_TYPE_TEMPLATE_DOCUMENT.equalsIgnoreCase(copy.getDefinationType()
						.getParameterCode())) {
			copy.setPmStatus(getPmStatusByStatusId(QTStatusConst.TS_PENDING_SUBMISSION));
		}

		if (null != copy.getDefinationType()
				&& ParameterConst.DOCUMENT_DEFINITION_TYPE_SYSTEM_DEFINED.equalsIgnoreCase(copy.getDefinationType()
						.getParameterCode())) {
			statusId = null;
			
			if (copy.getPmDocument().getDocumentId().equals(309L) || copy.getPmDocument().getDocumentId().equals(317L)) {
				if (!copy.isScoringReq()) {
					statusId = QTStatusConst.TS_COMPLETED;
				} else {
					statusId = QTStatusConst.TS_PENDING_SUBMISSION;
				}
				copy.setPmStatus(getPmStatusByStatusId(statusId));
			} else {
				copy.setPmStatus(getPmStatusByStatusId(QTStatusConst.TS_PENDING_SUBMISSION));
			}
		}
		
		if (null != copy.getDefinationType()
				&& ParameterConst.DOCUMENT_DEFINITION_TYPE_WEB_FORM.equalsIgnoreCase(copy.getDefinationType()
						.getParameterCode())) {
			statusId = null;
			
			if (copy.getPmDocument().getDocumentId().equals(314L) || copy.getPmDocument().getDocumentId().equals(313L)) {
				if (!copy.isScoringReq()) {
					statusId = QTStatusConst.TS_COMPLETED;
				} else {
					statusId = QTStatusConst.TS_PENDING_SUBMISSION;
				}
				copy.setPmStatus(getPmStatusByStatusId(statusId));
			} else {
				copy.setPmStatus(getPmStatusByStatusId(QTStatusConst.TS_PENDING_SUBMISSION));
			}
		}

		copy.setPmEvalStatus(getPmStatusByStatusId(QTStatusConst.TS_PENDING_SUBMISSION));
		
		return copy;
	}
	
	private ScQtCheckList setStatus2(ScQtCheckList copy) {
		if (null != copy.getDefinationType()
				&& !ParameterConst.DOCUMENT_DEFINITION_TYPE_UPLOAD.equalsIgnoreCase(copy.getDefinationType()
						.getParameterCode())) {
			Long statusId = null;
			
			if (!copy.isScoringReq()) {
				statusId = QTStatusConst.TS_COMPLETED;
			} else {
				statusId = QTStatusConst.TS_PENDING_SUBMISSION;
			}
			
			if (ParameterConst.DOCUMENT_DEFINITION_TYPE_SAMPLE.equalsIgnoreCase(copy.getDefinationType()
					.getParameterCode())) {
				statusId = QTStatusConst.TS_COMPLETED;
			}

			copy.setPmStatus(getPmStatusByStatusId(statusId));
		} else {
			if (ParameterConst.DOCUMENT_DEFINITION_TYPE_UPLOAD.equalsIgnoreCase(copy.getDefinationType()
					.getParameterCode())) {
				copy.setPmStatus(getPmStatusByStatusId(QTStatusConst.TS_COMPLETED));
			} else {
				copy.setPmStatus(getPmStatusByStatusId(QTStatusConst.TS_PENDING_SUBMISSION));
			}
		}
		
		if (null != copy.getDefinationType()
				&& ParameterConst.DOCUMENT_DEFINITION_TYPE_SUPPLIER_UPLOAD_AND_DOWNLOAD.equalsIgnoreCase(copy
						.getDefinationType().getParameterCode())) {
			copy.setPmStatus(getPmStatusByStatusId(QTStatusConst.TS_COMPLETED));
		}
		
		if (null != copy.getDefinationType()
				&& ParameterConst.DOCUMENT_DEFINITION_TYPE_PRESENTATION_DEMO.equalsIgnoreCase(copy.getDefinationType()
						.getParameterCode())) {
			copy.setPmStatus(getPmStatusByStatusId(QTStatusConst.TS_COMPLETED));
		}
		
		if (null != copy.getDefinationType()
				&& ParameterConst.DOCUMENT_DEFINITION_TYPE_TEMPLATE_DOCUMENT.equalsIgnoreCase(copy.getDefinationType()
						.getParameterCode())) {
			copy.setPmStatus(getPmStatusByStatusId(QTStatusConst.TS_PENDING_SUBMISSION));
		}
		
		if (null != copy.getDefinationType()
				&& ParameterConst.DOCUMENT_DEFINITION_TYPE_SYSTEM_DEFINED.equalsIgnoreCase(copy.getDefinationType()
						.getParameterCode())) {
			copy.setPmStatus(getPmStatusByStatusId(QTStatusConst.TS_PENDING_SUBMISSION));
		}
		
		if (null != copy.getDefinationType()
				&& ParameterConst.DOCUMENT_DEFINITION_TYPE_WEB_FORM.equalsIgnoreCase(copy.getDefinationType()
						.getParameterCode())) {
			copy.setPmStatus(getPmStatusByStatusId(QTStatusConst.TS_PENDING_SUBMISSION));
		}

		copy.setPmEvalStatus(getPmStatusByStatusId(QTStatusConst.TS_PENDING_SUBMISSION));
		
		return copy;
	}

	public void addToClone() throws FileNotFoundException {

		ScQt scQt = this.getManagedBean().getQt();// 33918
		ScQtSupplierCriteria qtSupplierCriteria = scQt.getScQtSupplierCriteria();
		
		if (!scQt.isPanel() && null != qtSupplierCriteria && scQt.isZonal()) {
			if (scQt.isZonal() && scQt.getQtType().equals(ParameterConst.QUOTATION_TENDER_TYPE_OPEN)) {
				List<TreeNode> zoneTrees = this.getManagedBean().getZoneSuppQualTrees();
				
				if (zoneTrees == null) {
					zoneTrees = this.getManagedBean().getZoneGeneralTrees();
				}
				
				List<TplSpecZoneDtl> businessOwnerZone = convertTreeNodesToZoneCriteria(zoneTrees, null);

				if (businessOwnerZone != null && !businessOwnerZone.isEmpty()) {
					// TODO Code needs to be remove
					setUserDetails(qtSupplierCriteria.getQtZoneCriterias());
					
					List<TplSpecZoneDtl> zoneLocal = new ArrayList<TplSpecZoneDtl>();
					
					for (TplSpecZoneDtl tplSpecZoneDtl : businessOwnerZone) {
						qtSupplierCriteria.getQtZoneCriterias().setModified(true);
						tplSpecZoneDtl.setTplSpecZone(qtSupplierCriteria.getQtZoneCriterias());
						zoneLocal.add(tplSpecZoneDtl);
					}
					
					qtSupplierCriteria.getQtZoneCriterias().setScQtSupplierCriteria(qtSupplierCriteria);
					qtSupplierCriteria.getQtZoneCriterias().setTplSpecZoneDtls(zoneLocal);
					
					// if(CollectionUtils.isEmpty(this.getManagedBean().getTemplateSpecZoneDtls())){//33918
					this.getManagedBean().setTemplateSpecZoneDtls(zoneLocal);
					// }//33918
				} else {
					if (null != scQt.getScQtSupplierCriteria().getQtZoneCriterias()) {
						qtSupplierCriteria.getQtZoneCriterias().setTplSpecZoneDtls(null);
					}
				}
				
				qtSupplierCriteria.setScQt(scQt);
				// TODO Need to remove below code...
				setUserDetails(qtSupplierCriteria);
				
				scQt.setScQtSupplierCriteria(qtSupplierCriteria);
			}
			
			/*
			 * if (QTConstants.OPEN_TYPE.equals(scQt.getQtType()) &&
			 * !scQt.getScQtSupplierCriteria().isAllState()) {
			 * List<ScQtLocCriteria> locCriteria = (List<ScQtLocCriteria>)
			 * convertTreeNodesToLocationCriteria(
			 * this.getManagedBean().getZoneTrees(), qtSupplierCriteria); if
			 * (locCriteria != null && !locCriteria.isEmpty()) {
			 * scQt.getScQtSupplierCriteria().setScQtLocCriteria( locCriteria);
			 * } else { scQt.getScQtSupplierCriteria().setScQtLocCriteria(null);
			 * } }
			 */

			/*
			 * if (QTConstants.OPEN_TYPE.equals(scQt.getQtType()) &&
			 * ParameterConst.PROCUREMENT_MODE_TENDER.equals(scQt
			 * .getProcurementMode().getParameterCode()) &&
			 * this.getManagedBean().getQt().getScQtSupplierCriteria()
			 * .isAllState()) { if (null != qtSupplierCriteria) {
			 * 
			 * qtSupplierCriteria.setLocationLevel(null);
			 * qtSupplierCriteria.setScQtLocCriteria(null); } }
			 */

			qtSupplierCriteria.setScQt(scQt);
			// TODO Need to remove below code...
			setUserDetails(qtSupplierCriteria);
			
			scQt.setScQtSupplierCriteria(qtSupplierCriteria);

			/*
			 * if (QTConstants.OPEN_TYPE.equalsIgnoreCase(scQt.getQtType())) {
			 * List<ScQtCategory> openList = this.getManagedBean()
			 * .getSuppCriteriaopenList(); if (null != openList &&
			 * !openList.isEmpty()) { scQt.setScQtCategorys(openList); } } else
			 * if (QTConstants.RESTRICTED_TYPE.equalsIgnoreCase(scQt
			 * .getQtType())) { scQt.setZonal(false);
			 * scQt.setZoneLocationLevel(null); if (null !=
			 * scQt.getScQtSupplierCriteria()) {
			 * scQt.getScQtSupplierCriteria().setAllState(false);
			 * scQt.getScQtSupplierCriteria().setLocationLevel(null);
			 * scQt.getScQtSupplierCriteria().setScQtLocCriteria(null); if (null
			 * != scQt.getScQtSupplierCriteria() .getQtZoneCriterias()) {
			 * qtSupplierCriteria.getQtZoneCriterias()
			 * .setTplSpecZoneDtls(null);
			 * qtSupplierCriteria.setQtZoneCriterias(null); }
			 * 
			 * }
			 * 
			 * List<ScQtCategory> openList = this.getManagedBean()
			 * .getSuppCriteriaopenList(); if (null != openList &&
			 * !openList.isEmpty()) { scQt.setScQtCategorys(openList); }
			 * 
			 * }
			 */
			
			SpecificationReqDTO specReqDTO = new SpecificationReqDTO();
			specReqDTO.setScQt(scQt);
			
			IQTSpecSO specSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
					IQTSpecSO.class);
			specSO.validateSpecCommitteGeneralInfo(specReqDTO);
		}// 33918

		ScQt selected = this.getManagedBean().getSelectedCloneTender();
		IQTDetailSO QtDetailsSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
				IQTDetailSO.class);
		
		QuationTenderReqDTO tenderReqDTO = new QuationTenderReqDTO();
		tenderReqDTO.setQtId(selected.getQtId());
		
		/*
		 * TRACE HI-EXEC TIME Development #29385
		 */
		long startTime = System.currentTimeMillis();
		QuationTenderResDTO tenderResDTO = QtDetailsSO.retriveQTDetails(tenderReqDTO);
		SLAUtils.getInstance().logHiExecInfo("QT", "QtSpecCloneBackingBean.addToClone", startTime);
		/*
		 * END
		 */
		
		FacesContext context = FacesContext.getCurrentInstance();
		ELContext elContext = context.getELContext();
		SpecificationBackingBean specificationBackingBean = (SpecificationBackingBean) elContext.getELResolver()
				.getValue(elContext, null, "specificationBackingBean");
		
		this.getManagedBean().setTobeRemoveCategoryCodes(null);// 32453
		this.getManagedBean().setMarkDeletedScQtScoreSettingTechnical(null);
		this.getManagedBean().setMarkDeletedScQtScoreSettingFinancial(null);
		this.getManagedBean().setMarkDeletedScQtScoreSettingOverall(null);
		
		getManagedBean().setMarkDeletedTechChecklist(null);
		getManagedBean().setMarkDeletedFinChecklist(null);
		
		this.getManagedBean().setScQtCheckSt2techProposal(null);
		this.getManagedBean().setScQtCheckSt2financeProposalList(null);
		this.getManagedBean().setRecommendationPackageScore(null);
		this.getManagedBean().setRecommendationScore(null);
		this.getManagedBean().setRemoveDeductions(null);// 25635&33733
		this.getManagedBean().setTechnicalItemScoreSetting(null);// 25635&34176-start
		this.getManagedBean().setTechnicalScoreSetting(null);
		this.getManagedBean().setDemoScoreSetting(null);
		this.getManagedBean().setTechnicalItemScoreSetting(null);
		this.getManagedBean().setFinancialScoreSetting(null);
		this.getManagedBean().setFinacialItemScoreSetting(null);
		this.getManagedBean().setRecommendationPackageScore(null);
		this.getManagedBean().setRecommendationScore(null);// 25635&34176-start
		
		specificationBackingBean.retriveTechnicalCheckList();
		specificationBackingBean.retriveFinancialCheckList();
		
		retrieveTechnicalChecklist(tenderResDTO.getQtDetail());
		retrieveFinancialChecklist(tenderResDTO.getQtDetail());
		
		retrieveTechnicalScoreSettings(tenderResDTO.getQtDetail());
		retrieveFinancialScoreSettings(tenderResDTO.getQtDetail());
		
		retrieveOverallScoreSettings(tenderResDTO.getQtDetail());
		retrieveDeliveryAddress(tenderResDTO.getQtDetail());
		retriveFulfilmentDtls(tenderResDTO.getQtDetail());
		retrieveDeductionPaymentTabDtl(tenderResDTO.getQtDetail());// 25635&33733
		
		// #31293 bug Farid after clone set back to index 0
		this.getManagedBean().setActiveIndex(0);
		// #31293 End
		
		Long status = tenderResDTO.getQtDetail().getQtStatus().getStatusId();// 25635&34176-start
		
		if (QTStatusConst.QT_ARCHIVED.equals(status) || QTStatusConst.QT_ARCHIVED == status) {
			this.getManagedBean().getQt().setScQtScoreSettings(null);
		} else if (QTStatusConst.QT_CANCELLED.equals(status) || QTStatusConst.QT_CANCELLED == status) {
			this.getManagedBean().getQt().setScQtScoreSettings(null);
		}
		
		this.getManagedBean().getQt().setEstimateAmt(null);
		this.getManagedBean().getQt().setEstimatedAmount(null);
		// 25635&34176-End
		this.getManagedBean().getQt().setCloned(Boolean.TRUE);//34320&25635
		specificationBackingBean.saveSpecificationCommitteeDtls();
	}	
	
	private void retriveFulfilmentDtls(ScQt copy) {
		IQTSpecSO specSo = null;
		ScQt cloned = this.getManagedBean().getQt();
		
		try {
			SpecificationReqDTO reqDTO = new SpecificationReqDTO();
			specSo = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
					IQTSpecSO.class);
			
			reqDTO.setQtId(copy.getQtId());
			
			SpecificationResDTO resDTO = specSo.retrieveFulFilMentScheduleDtls(reqDTO);

			List<ScQtSchedule> scheduleList = new ArrayList<ScQtSchedule>();
			List<ScQtProgressivePayment> paymentList = new ArrayList<ScQtProgressivePayment>();

			if (null != resDTO.getScheduleResults() && null != resDTO.getScheduleResults().getResultList()
					&& !resDTO.getScheduleResults().getResultList().isEmpty()) {

				this.getManagedBean().setTotalFrequency(resDTO.getScheduleResults().getResultList().size());
				
				try {

					for (ScQtSchedule scQtSchedule : resDTO.getScheduleResults().getResultList()) {
						String startMonth = "";
						String endMonth = "";
						SimpleDateFormat dateFormat = new SimpleDateFormat(CommonConstants.DEFAULT_DATE_FORMAT);
						int months;
						Date startDate = null;
						Date endDate = null;
						String fromdate = dateFormat.format(scQtSchedule.getFromDate());
						String todate = dateFormat.format(scQtSchedule.getToDate());
						String startdate = fromdate.substring(3, 5);
						String startYear = fromdate.substring(6, fromdate.length());
						String fromDay = fromdate.substring(0, 2);

						String enddate = todate.substring(3, 5);
						String endYear = todate.substring(6, todate.length());
						String toDay = todate.substring(0, 2);

						startMonth = fulfilmentMonthlyDtls(startdate);
						endMonth = fulfilmentMonthlyDtls(enddate);

						int sYear = Integer.parseInt(startYear);
						int eYear = Integer.parseInt(endYear);

						String fromDate = fromDay + "/" + startdate + "/" + sYear;
						String toDate = toDay + "/" + enddate + "/" + eYear;

						startDate = dateFormat.parse(fromDate);
						endDate = dateFormat.parse(toDate);

						months = DateUtils.getMonths(startDate, endDate);

						ScQtSchedule cls = new ScQtSchedule();
						cls.setBrandNew(true);
						
						setUserDetails(cls);
						
						cls.setFromDate(scQtSchedule.getFromDate());
						cls.setToDate(scQtSchedule.getToDate());
						cls.setEndMonth(endMonth);
						cls.setEndYear(endYear);
						cls.setScheduleNo(scQtSchedule.getScheduleNo());
						cls.setScheduleType(scQtSchedule.getScheduleType());
						cls.setScQt(cloned);
						cls.setStartMonth(startMonth);
						cls.setStartYear(startYear);
						cls.setTotalMonths(new BigDecimal(months));
						
						scheduleList.add(cls);

					}
				} catch (ParseException e) {
				}
			}
			
			this.getManagedBean().setScheduleList(scheduleList);

			if (null != resDTO.getProgressivePaymentResults()
					&& null != resDTO.getProgressivePaymentResults().getResultList()
					&& !resDTO.getProgressivePaymentResults().getResultList().isEmpty()) {

				this.getManagedBean().setTotalProgressivePayment(
						resDTO.getProgressivePaymentResults().getResultList().size());

				String startMonth = "";
				String endMonth = "";
				
				for (ScQtProgressivePayment payment : resDTO.getProgressivePaymentResults().getResultList()) {
					SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

					String fromdate = dateFormat.format(payment.getFromDate());
					String todate = dateFormat.format(payment.getToDate());
					String startdate = fromdate.substring(3, 5);
					String startYear = fromdate.substring(6, fromdate.length());
					String enddate = todate.substring(3, 5);
					String endYear = todate.substring(6, todate.length());
					startMonth = fulfilmentMonthlyDtls(startdate);
					endMonth = fulfilmentMonthlyDtls(enddate);

					ScQtProgressivePayment clp = new ScQtProgressivePayment();

					clp.setBrandNew(true);
					clp.setScQt(cloned);
					
					setUserDetails(clp);
					
					clp.setFromDate(payment.getFromDate());
					clp.setToDate(payment.getToDate());
					clp.setPercentage(payment.getPercentage());
					clp.setPhaseDesc(payment.getPhaseDesc());
					clp.setPhaseNo(payment.getPhaseNo());
					clp.setTotalMonths(payment.getTotalMonths());

					clp.setStartMonth(startMonth);
					clp.setEndMonth(endMonth);
					clp.setStartYear(startYear);
					clp.setEndYear(endYear);

					paymentList.add(clp);

				}
			}
			
			this.getManagedBean().setProgressivePaymentList(paymentList);

			reqDTO.setQtId(cloned.getQtId());
			resDTO = specSo.retrieveFulFilMentScheduleDtls(reqDTO);

			if (null != resDTO.getScheduleResults() && null != resDTO.getScheduleResults().getResultList()
					&& !resDTO.getScheduleResults().getResultList().isEmpty()) {
				List<ScQtSchedule> deletes = new ArrayList<ScQtSchedule>();
				
				for (ScQtSchedule toDel : resDTO.getScheduleResults().getResultList()) {
					toDel.setDeleted(true);
					deletes.add(toDel);
				}
				
				this.getManagedBean().setToBeRemoveSchedules(deletes);
			}

			if (null != resDTO.getProgressivePaymentResults()
					&& null != resDTO.getProgressivePaymentResults().getResultList()
					&& !resDTO.getProgressivePaymentResults().getResultList().isEmpty()) {
				List<ScQtProgressivePayment> deletes = new ArrayList<ScQtProgressivePayment>();
				
				for (ScQtProgressivePayment toDel : resDTO.getProgressivePaymentResults().getResultList()) {
					toDel.setDeleted(true);
					deletes.add(toDel);
				}
				
				this.getManagedBean().setToBeRemoveProgPayments(deletes);
			}

		} finally {
			specSo = null;
		}
	}
	
	private String fulfilmentMonthlyDtls(String startdate) {
		String startMonth = "";
		
		try {
			if (startdate.equalsIgnoreCase("01")) {
				startMonth = "0";
			} else if (startdate.equalsIgnoreCase("02")) {
				startMonth = "1";
			} else if (startdate.equalsIgnoreCase("03")) {
				startMonth = "2";
			} else if (startdate.equalsIgnoreCase("04")) {
				startMonth = "3";
			} else if (startdate.equalsIgnoreCase("05")) {
				startMonth = "4";
			} else if (startdate.equalsIgnoreCase("06")) {
				startMonth = "5";
			} else if (startdate.equalsIgnoreCase("07")) {
				startMonth = "6";
			} else if (startdate.equalsIgnoreCase("08")) {
				startMonth = "7";
			} else if (startdate.equalsIgnoreCase("09")) {
				startMonth = "8";
			} else if (startdate.equalsIgnoreCase("10")) {
				startMonth = "9";
			} else if (startdate.equalsIgnoreCase("11")) {
				startMonth = "10";
			} else if (startdate.equalsIgnoreCase("12")) {
				startMonth = "11";
			}

		} finally {

		}
		
		return startMonth;
	}	
	
	private void retrieveFinancialScoreSettings(ScQt scqt) {
		SpecificationReqDTO reqDTO = new SpecificationReqDTO();
		reqDTO.setScQt(scqt);
		
		SpecificationResDTO resDTO = null;
		IQTSpecSO specSo = null;
		
		try {
			List<Integer> settinglist = new ArrayList<Integer>();
			settinglist.add(SourcingConstants.SCORE_SETTING_TYPE_FINANCIAL_BY_PACKAGE);
			settinglist.add(SourcingConstants.SCORE_SETTING_TYPE_FINANCIAL_BY_ITEM);

			// reqDTO.setSpecType("T");
			reqDTO.setSettingTypes(settinglist);
			
			specSo = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
					IQTSpecSO.class);
			resDTO = specSo.retrieveScoreSettingDtls(reqDTO);
			
			deleteTechnicalScoreSetting(reqDTO, "F");
			
			if (null != resDTO.getScoreSettingResultBean().getResultList()
					&& !resDTO.getScoreSettingResultBean().getResultList().isEmpty()) {

				if (resDTO.getScoreSettingResultBean().getResultList().size() == 2) {
					for (ScQtScoreSetting scoreSttting : resDTO.getScoreSettingResultBean().getResultList()) {
						if (scoreSttting.getSettingType() == SourcingConstants.SCORE_SETTING_TYPE_FINANCIAL_BY_PACKAGE) {
							ScQtScoreSetting settings = copyScoreSettings(scoreSttting);
							this.getManagedBean().setFinancialScoreSetting(settings);

						}
						
						if (scoreSttting.getSettingType() == SourcingConstants.SCORE_SETTING_TYPE_FINANCIAL_BY_ITEM) {
							ScQtScoreSetting settings = copyScoreSettings(scoreSttting);
							this.getManagedBean().setFinacialItemScoreSetting(settings);
							calculateFinancialItemScore(this.getManagedBean().getFinacialItemScoreSetting());

						}
					}

				} else if (resDTO.getScoreSettingResultBean().getResultList().size() == 1) {
					for (ScQtScoreSetting scoreSttting : resDTO.getScoreSettingResultBean().getResultList()) {
						if (scoreSttting.getSettingType() == SourcingConstants.SCORE_SETTING_TYPE_FINANCIAL_BY_PACKAGE) {
							ScQtScoreSetting settings = copyScoreSettings(scoreSttting);
							
							this.getManagedBean().setFinancialScoreSetting(settings);

						}
						
						if (scoreSttting.getSettingType() == SourcingConstants.SCORE_SETTING_TYPE_FINANCIAL_BY_ITEM) {
							ScQtScoreSetting settings = copyScoreSettings(scoreSttting);
							
							this.getManagedBean().setFinacialItemScoreSetting(settings);

						}
					}
				}
			}

			settinglist = null;
			
			this.getManagedBean().setFinancialScoring(null);
			this.getManagedBean().setFinancialScoring(new ArrayList<ScQtCheckList>());
			
			List<ScQtCheckList> checklist = this.getManagedBean().getScQtCheckSt2financeProposalList();
			checklistSorting(checklist);
			
			if (null != checklist && !checklist.isEmpty()) {
				reqDTO.setScQt(scqt);
				reqDTO.setSpecType(SourcingConstants.FINANCIAL_DOCTYPE);
				
				resDTO = specSo.retrieveCheckLists(reqDTO);
				List<ScQtCheckList> localChecklist = new ArrayList<ScQtCheckList>();
				List<ScQtCheckList> specChecklist = new ArrayList<ScQtCheckList>();
				float addScore = 0;
				int local = 0;
				float financialWeightageScore = 0;
				float financialWeightage = 0;
				// String localTitle="";
				StringBuffer sb = new StringBuffer();
				
				for (ScQtCheckList list : checklist) {
					if (null == list.getQtScoreWeightage()) {
						list = cloneScoreWeightage(list, resDTO.getFinancialCheckLists());
					}

					if (ParameterConst.DOCUMENT_DEFINITION_TYPE_TEMPLATE_SPECIFICATION.equalsIgnoreCase(list
							.getDefinationType().getParameterCode())) {
						if (null == list.getQtScoreWeightage().getWeightage()) {
							list.getQtScoreWeightage().setWeightage(new BigDecimal(0));
						}
						
						sb.append(list.getTitle());
						sb.append(",");
						
						specChecklist.add(list);

					} else {
						if (list.isScoringReq()) {
							localChecklist.add(list);
						} else {
							if (null == list.getQtScoreWeightage()) {
								ScQtScoreWeightage scoreWeightage = new ScQtScoreWeightage();
								list.setQtScoreWeightage(scoreWeightage);
							}
							
							list.getQtScoreWeightage().setScore(new BigDecimal(0));
						}
					}

					if (null != list.getQtScoreWeightage() && null != list.getQtScoreWeightage().getScore()) {
						addScore = addScore + list.getQtScoreWeightage().getScore().intValue();
					}
					
					local = (int) addScore;

					if (null != list.getQtScoreWeightage() && null != list.getQtScoreWeightage().getScore()) {
						list.getQtScoreWeightage().setScore(list.getQtScoreWeightage().getScore());
					}

				}
				
				this.getManagedBean().setFinPoints(local);

				if (null != specChecklist && specChecklist.size() > 0) {
					Map<Long, ScQtScoreWeightage> map = new HashMap<Long, ScQtScoreWeightage>();

					for (ScQtCheckList checklistlocal : specChecklist) {
						map.put(checklistlocal.getQtScoreWeightage().getQtScoreWeightageId(),
								checklistlocal.getQtScoreWeightage());
					}
					
					List<ScQtScoreWeightage> list = new ArrayList<ScQtScoreWeightage>(map.values());
					
					for (ScQtScoreWeightage weightage : list) {
						financialWeightageScore = financialWeightageScore + weightage.getScore().intValue();
						financialWeightage = financialWeightage + weightage.getWeightage().intValue();
					}

					ScQtCheckList tmpCheckList = new ScQtCheckList();
					// PmParameter definationType = new PmParameter();
					// definationType
					// .setParameterCode(ParameterConst.DOCUMENT_DEFINITION_TYPE_TEMPLATE_SPECIFICATION);
					tmpCheckList.setDefinationType(specChecklist.get(0).getDefinationType());
					
					specChecklist.get(0).getQtScoreWeightage().setScore(new BigDecimal(financialWeightageScore));
					specChecklist.get(0).getQtScoreWeightage().setWeightage(new BigDecimal(financialWeightage));
					
					this.getManagedBean().setFinSpecList(specChecklist);
					
					tmpCheckList.setQtScoreWeightage(specChecklist.get(0).getQtScoreWeightage());
					// this.getManagedBean().setScQtChecklist(tmpCheckList);
					
					if (specChecklist.get(0).getQtScoreWeightage().getScore() != null) {
						getManagedBean().setScoreVarience(specChecklist.get(0).getQtScoreWeightage().getScore());
					}
					
					tmpCheckList.setTitle(sb.toString());

					List<ScQtScoreOfferPrice> offerPrice = specChecklist.get(0).getQtScoreWeightage()
							.getScQtScoreOfferPrices();
					
					this.getManagedBean().setOfferScoreType(scoreTypeN);

					if (null == offerPrice || offerPrice.isEmpty()) {
						offerPrice = new ArrayList<ScQtScoreOfferPrice>();
						
						if (null == this.getManagedBean().getOfferPricePackage()) {
							ScQtScoreOfferPrice pkgOfferPrice = new ScQtScoreOfferPrice();
							pkgOfferPrice.setScQtScoreWeightage(tmpCheckList.getQtScoreWeightage());
							pkgOfferPrice.setOfferPriceType(ParameterConst.RECOMMEND_TYPE_PACKAGE);

							if (scoreTypeN.equals(QTConstants.AUTO_SCORE)) {
								pkgOfferPrice.setScoreType(new BigDecimal(this.getManagedBean().getOfferScoreType()));

							} else if (scoreTypeN.equals(QTConstants.USER_SCORE)) {
								pkgOfferPrice.setScoreType(new BigDecimal(this.getManagedBean().getOfferScoreType()));
								pkgOfferPrice.setAboveScore(null);
								pkgOfferPrice.setBelowScore(null);
								pkgOfferPrice.setFromVariance(null);
								pkgOfferPrice.setToVariance(null);

							}
							
							setUserDetails(pkgOfferPrice);
							
							this.getManagedBean().setOfferPricePackage(pkgOfferPrice);
							
							offerPrice.add(pkgOfferPrice);
						}

						tmpCheckList.getQtScoreWeightage().setScQtScoreOfferPrices(offerPrice);

					} else {
						for (ScQtScoreOfferPrice scoreOfferPrice : tmpCheckList.getQtScoreWeightage()
								.getScQtScoreOfferPrices()) {
							this.getManagedBean().setOfferScoreType(
									String.valueOf(scoreOfferPrice.getScoreType().intValue()));
							if (ParameterConst.RECOMMEND_TYPE_PACKAGE.equalsIgnoreCase(scoreOfferPrice
									.getOfferPriceType())) {

								if (null == scoreOfferPrice.getScoreType()) {
									scoreOfferPrice.setScoreType(new BigDecimal(QTConstants.AUTO_SCORE));
									
									this.getManagedBean().setOfferPricePackage(scoreOfferPrice);
								} else {

									if (null == this.getManagedBean().getOfferPricePackage()) {

										this.getManagedBean().setOfferScoreType(
												String.valueOf(scoreOfferPrice.getScoreType().intValue()));
										this.getManagedBean().setOfferPricePackage(scoreOfferPrice);

										if (scoreTypeN.equals(QTConstants.AUTO_SCORE)) {

											this.getManagedBean().setOfferScoreType(scoreTypeN);
											
											scoreOfferPrice.setScoreType(new BigDecimal(this.getManagedBean()
													.getOfferScoreType()));

										} else if (scoreTypeN.equals(QTConstants.USER_SCORE)) {
											this.getManagedBean().setOfferScoreType(scoreTypeN);
											
											scoreOfferPrice.setScoreType(new BigDecimal(this.getManagedBean()
													.getOfferScoreType()));
											scoreOfferPrice.setAboveScore(null);
											scoreOfferPrice.setBelowScore(null);
											scoreOfferPrice.setFromVariance(null);
											scoreOfferPrice.setToVariance(null);

										}

									} else {
										if (scoreTypeN.equals(QTConstants.AUTO_SCORE)) {

											this.getManagedBean().setOfferScoreType(scoreTypeN);
											this.getManagedBean().getOfferPricePackage()
													.setScoreType(new BigDecimal(scoreTypeN));

										} else if (scoreTypeN.equals(QTConstants.USER_SCORE)) {
											this.getManagedBean().setOfferScoreType(scoreTypeN);
											this.getManagedBean().getOfferPricePackage().setFromVariance(null);
											this.getManagedBean().getOfferPricePackage().setToVariance(null);
											this.getManagedBean().getOfferPricePackage().setBelowScore(null);
											this.getManagedBean().getOfferPricePackage().setAboveScore(null);

										}
									}
								}
							}
						}
					}
					
					this.getManagedBean().getFinancialScoring().add(0, tmpCheckList);
					
					map = null;
					list = null;
					tmpCheckList = null;
				}
				
				this.getManagedBean().getFinancialScoring().addAll(localChecklist);
				
				finWeightageSum();
				finScoreSum();
				
				localChecklist = null;
				specChecklist = null;
				sb = null;
			}

			if (null != this.getManagedBean().getScQtCheckSt2financeProposalList()
					&& !this.getManagedBean().getScQtCheckSt2financeProposalList().isEmpty()) {
				this.getManagedBean().setFinancialUserScoringSchema(false);
				
				for (ScQtCheckList financialScoringChecklist : this.getManagedBean()
						.getScQtCheckSt2financeProposalList()) {
					if (financialScoringChecklist.isScoringReq()
							&& null != financialScoringChecklist.getPmStatus()
							&& QTStatusConst.TS_COMPLETED.equals(financialScoringChecklist.getPmStatus().getStatusId())
							&& null != financialScoringChecklist.getPmEvalStatus()
							&& QTStatusConst.TS_PENDING_SUBMISSION.equals(financialScoringChecklist.getPmEvalStatus()
									.getStatusId())) {
						this.getManagedBean().setFinancialUserScoringSchema(true);
						break;
					}
				}
			}

		} finally {
			specSo = null;
		}
	}
	
	private void finScoreSum() {
		try {
			int total = 0;
			List<ScQtCheckList> weightageList = this.getManagedBean().getFinancialScoring();
			
			if (weightageList == null) {
				weightageList = new ArrayList<ScQtCheckList>();
			}
			
			for (int i = 0; i < weightageList.size(); i++) {
				ScQtCheckList weight = (ScQtCheckList) weightageList.get(i);
				
				if (null != weight.getDefinationType()
						&& ParameterConst.DOCUMENT_DEFINITION_TYPE_TEMPLATE_SPECIFICATION.equalsIgnoreCase(weight
								.getDefinationType().getParameterCode())) {

					getManagedBean().setTechnicalChecklistScoring(weight.getQtScoreWeightage().getScore().intValue());
					getManagedBean().setScoreVarience(new BigDecimal(getManagedBean().getTechnicalChecklistScoring()));

				}

				if (null != weight.getQtScoreWeightage() && weight.getQtScoreWeightage().getWeightage() == null) {
					weight.getQtScoreWeightage().setWeightage(new BigDecimal(0));
				}
				
				if (null != weight.getQtScoreWeightage()) {
					total = total + weight.getQtScoreWeightage().getScore().intValue();
				}
				
				BigDecimal techScore = new BigDecimal(total);
				
				this.getManagedBean().setFinPoints(techScore.intValue());
			}
		} finally {

		}
	}
	
	private void finWeightageSum() {
		try {
			int total = 0;
			List<ScQtCheckList> weightageList = this.getManagedBean().getFinancialScoring();
			
			if (weightageList == null) {
				weightageList = new ArrayList<ScQtCheckList>();
			}
			
			for (int i = 0; i < weightageList.size(); i++) {
				ScQtCheckList weight = (ScQtCheckList) weightageList.get(i);
				if (null != weight.getQtScoreWeightage() && weight.getQtScoreWeightage().getWeightage() == null) {
					weight.getQtScoreWeightage().setWeightage(new BigDecimal(0));
				}
				
				if (null != weight.getQtScoreWeightage()) {
					total = total + weight.getQtScoreWeightage().getWeightage().intValue();
				}
				
				this.getManagedBean().setFinancialWeightageScore(new BigDecimal(total));

				if (total > 100) {
					throw new EpException(MessageConst.ERROR_760148);
				}
			}
		} finally {

		}
	}
	
	public void calculateFinancialItemScore(ScQtScoreSetting financialItem) {
		try {
			if (null != financialItem) {
				int value1 = financialItem.getOverallFin() != null ? financialItem.getOverallFin().intValue() : 0;
				int value2 = financialItem.getOverallOtherFin() != null ? financialItem.getOverallOtherFin().intValue()
						: 0;
				int local = value1 + value2;
				
				this.getManagedBean().setFinancialItemScore(new BigDecimal(local));
				
				if ((null != this.getManagedBean().getFinancialItemScore())
						&& this.getManagedBean().getFinancialItemScore().intValue() > 100) {
					throw new EpException(MessageConst.ERROR_760257);

				}
			}
		} finally {

		}
	}
	
	private List<ScQtScoreSetting> getCurrentScQtScoreSetting(SpecificationReqDTO reqDTO, IQTSpecSO specSo) {
		SpecificationResDTO resDTO = null;
		resDTO = specSo.retrieveScoreSettingDtls(reqDTO);

		return resDTO.getScoreSettingResultBean().getResultList();
	}
	
	private void deleteTechnicalScoreSetting(SpecificationReqDTO reqDTO, String type) {
		IQTSpecSO specSo = null;
		specSo = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);
		
		reqDTO.setScQt(this.getManagedBean().getQt());
		
		List<ScQtScoreSetting> listDelete = getCurrentScQtScoreSetting(reqDTO, specSo);

		for (ScQtScoreSetting setting : listDelete) {
			setting.setDeleted(true);
		}

		if ("T".equals(type)) {
			this.getManagedBean().setMarkDeletedScQtScoreSettingTechnical(listDelete);
		} else if ("F".equals(type)) {
			this.getManagedBean().setMarkDeletedScQtScoreSettingFinancial(listDelete);
		} else {
			this.getManagedBean().setMarkDeletedScQtScoreSettingOverall(listDelete);
		}
	}	
	
	private void retrieveOverallScoreSettings(ScQt scqt) {
		SpecificationReqDTO reqDTO = new SpecificationReqDTO();
		reqDTO.setScQt(scqt);
		
		SpecificationResDTO resDTO = null;
		IQTSpecSO specSo = null;
		
		try {
			if (null == this.getManagedBean().getRecommendationPackageScore()
					&& null == this.getManagedBean().getRecommendationScore()) {
				List<Integer> settinglist = new ArrayList<Integer>();
				settinglist.add(SourcingConstants.SCORE_SETTING_TYPE_RECOMMENDATION_BY_PACKAGE);
				settinglist.add(SourcingConstants.SCORE_SETTING_TYPE_RECOMMENDATION_BY_ITEM);
				
				reqDTO.setSettingTypes(settinglist);
				
				specSo = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
						IQTSpecSO.class);
				resDTO = specSo.retrieveScoreSettingDtls(reqDTO);
				
				deleteTechnicalScoreSetting(reqDTO, "O");

				if (resDTO.getScoreSettingResultBean().getResultList() != null
						&& !resDTO.getScoreSettingResultBean().getResultList().isEmpty()) {

					for (ScQtScoreSetting recommnendationSetting : resDTO.getScoreSettingResultBean().getResultList()) {

						if (recommnendationSetting.getSettingType() == SourcingConstants.SCORE_SETTING_TYPE_RECOMMENDATION_BY_PACKAGE) {
							ScQtScoreSetting pkgScoreSetting = copyScoreSettings(recommnendationSetting);
							
							this.getManagedBean().setRecommendationPackageScore(pkgScoreSetting);
							
							calculateTotalPackage(this.getManagedBean().getRecommendationPackageScore(), false);
						}

						if (recommnendationSetting.getSettingType() == SourcingConstants.SCORE_SETTING_TYPE_RECOMMENDATION_BY_ITEM) {
							ScQtScoreSetting itemScoreSetting = copyScoreSettings(recommnendationSetting);
							
							this.getManagedBean().setRecommendationScore(itemScoreSetting);
							
							calculateRecommendtechnicalScore(this.getManagedBean().getRecommendationScore());
							calculateRecommendFinancialScore(this.getManagedBean().getRecommendationScore());
						}
					}
				}
			}
		} finally {
			specSo = null;
		}
	}
	
	public void calculateTotalPackage(ScQtScoreSetting technical, boolean isValidationRequired) {
		try {
			if (null == technical.getOverallTechnical()) {
				technical.setOverallTechnical(new BigDecimal(0));
			}
			
			if (null == technical.getOverallFin()) {
				technical.setOverallFin(new BigDecimal(0));
			}
			
			BigDecimal passingScore = retrieveTechnicalEvalutionPassingScorePmrules(RuleTypeConst.SC_TECHNICAL_WEIGHTAGE);
			
			if (null != passingScore && technical.getOverallTechnical().intValue() < passingScore.intValue()
					&& !this.getManagedBean().isReadOnly() && isValidationRequired) {
				throw new EpException(MessageConst.ERROR_760365, new String[] { String.valueOf(passingScore) });
			}
			
			int financialScore = 100 - technical.getOverallTechnical().intValue();
			
			technical.setOverallFin(new BigDecimal(financialScore));
			
			this.getManagedBean().setRecommendationPackageScore(technical);
			
			int local = technical.getOverallTechnical().intValue() + technical.getOverallFin().intValue();

			this.getManagedBean().setRecommendationPackageTotal(new BigDecimal(local));
		} finally {

		}
	}
	
	public void calculateRecommendtechnicalScore(ScQtScoreSetting technical) {
		try {
			int value1 = technical.getOverallTechnical() != null ? technical.getOverallTechnical().intValue() : 0;
			int value2 = technical.getOverallOtherTechnical() != null ? technical.getOverallOtherTechnical().intValue()
					: 0;
			int local = value1 + value2;
			this.getManagedBean().setRecommendTechnicalScore(new BigDecimal(local));
			int financialScore = 0;
			
			if (null != getManagedBean().getRecommendfinancialScore()) {
				financialScore = getManagedBean().getRecommendfinancialScore().intValue();
			}
			
			this.getManagedBean().setTotalScore(new BigDecimal(local + financialScore));
			
			if ((null != this.getManagedBean().getTotalScore())
					&& this.getManagedBean().getTotalScore().intValue() > 100) {
				throw new EpException(MessageConst.ERROR_760257);
			}
		} finally {

		}
	}
	
	public void calculateRecommendFinancialScore(ScQtScoreSetting financial) {
		try {
			int value1 = financial.getOverallFin() != null ? financial.getOverallFin().intValue() : 0;
			int value2 = financial.getOverallOtherFin() != null ? financial.getOverallOtherFin().intValue() : 0;
			int local = value1 + value2;
			
			this.getManagedBean().setRecommendfinancialScore(new BigDecimal(local));

			int technicalScore = 0;
			
			if (null != getManagedBean().getRecommendTechnicalScore()) {
				technicalScore = getManagedBean().getRecommendTechnicalScore().intValue();
			}
			
			this.getManagedBean().setTotalScore(new BigDecimal(local + technicalScore));
			
			if ((null != this.getManagedBean().getTotalScore())
					&& this.getManagedBean().getTotalScore().intValue() > 100) {
				throw new EpException(MessageConst.ERROR_760257);

			}
		} finally {

		}
	}
	
	private void retrieveTechnicalScoreSettings(ScQt scqt) {
		SpecificationReqDTO reqDTO = new SpecificationReqDTO();
		reqDTO.setScQt(scqt);

		SpecificationResDTO resDTO = null;
		IQTSpecSO specSo = null;

		try {
			List<Integer> settinglist = new ArrayList<>();//35378
			settinglist.add(SourcingConstants.SCORE_SETTING_TYPE_TECHNICAL);
			settinglist.add(SourcingConstants.SCORE_SETTING_TYPE_DEMO);
			settinglist.add(SourcingConstants.SCORE_SETTING_TYPE_TECHNICAL_BY_ITEM);
			
			reqDTO.setSettingTypes(settinglist);
			
			specSo = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
					IQTSpecSO.class);
			resDTO = specSo.retrieveScoreSettingDtls(reqDTO);
			
			deleteTechnicalScoreSetting(reqDTO,ParameterConst.SPECIFICATION_TYPE_TECHNICAL);//35378-start
			boolean isCloneTechnicalItemExist=Boolean.FALSE;
			boolean isCurrentTechnicalItemExist=Boolean.FALSE;
			if (CollectionUtils.isNotEmpty(resDTO.getScoreSettingResultBean().getResultList())) {//35378-End

				for (ScQtScoreSetting recommnendationSetting : resDTO.getScoreSettingResultBean().getResultList()) {
					if (recommnendationSetting.getSettingType() == SourcingConstants.SCORE_SETTING_TYPE_TECHNICAL) {

						ScQtScoreSetting settings = copyScoreSettings(recommnendationSetting);
						this.getManagedBean().setTechnicalScoreSetting(settings);

					}
					
					if (recommnendationSetting.getSettingType() == SourcingConstants.SCORE_SETTING_TYPE_DEMO) {

						ScQtScoreSetting settings = copyScoreSettings(recommnendationSetting);
						this.getManagedBean().setDemoScoreSetting(settings);

					}
					
					if (recommnendationSetting.getSettingType() == SourcingConstants.SCORE_SETTING_TYPE_TECHNICAL_BY_ITEM) {
						isCloneTechnicalItemExist=Boolean.TRUE;//35378
						ScQtScoreSetting settings = copyScoreSettings(recommnendationSetting);
						this.getManagedBean().setTechnicalItemScoreSetting(settings);

					}
				}
			}
			if(CollectionUtils.isNotEmpty(this.getManagedBean().getMarkDeletedScQtScoreSettingTechnical())){//35378-start
				for(ScQtScoreSetting technicalCuurentSetting:this.getManagedBean().getMarkDeletedScQtScoreSettingTechnical()){
					if(SourcingConstants.SCORE_SETTING_TYPE_TECHNICAL_BY_ITEM == technicalCuurentSetting.getSettingType()){
						isCurrentTechnicalItemExist=Boolean.TRUE;
						break;
					}
				}
			}
			boolean isTechnicalSettingItemCreate=(isCloneTechnicalItemExist && isCurrentTechnicalItemExist) || (!isCloneTechnicalItemExist && isCurrentTechnicalItemExist);
			boolean isTechnicalSettingItem=(isCloneTechnicalItemExist && !isCurrentTechnicalItemExist) || (!isCloneTechnicalItemExist && !isCurrentTechnicalItemExist);
			if(isTechnicalSettingItem){
				this.getManagedBean().setTechnicalItemScoreSetting(null);	
			}//35378-End
			boolean isTechnicalItemExist=Boolean.FALSE;//35113-start
			if (isTechnicalSettingItemCreate && null == this.getManagedBean().getTechnicalItemScoreSetting()) {//35378
				isTechnicalItemExist=Boolean.TRUE;
				ScQtScoreSetting technicalItemScoresetting = new ScQtScoreSetting();
				technicalItemScoresetting.setSettingType(SourcingConstants.SCORE_SETTING_TYPE_TECHNICAL_BY_ITEM);
				technicalItemScoresetting.setScQt(this.getManagedBean().getQt());
				technicalItemScoresetting.setOverallTechnical(TECHNICAL_ITEM_SCORE);
				technicalItemScoresetting.setPassingScore(TECHNICAL_ITEM_SCORE);
				setUserDetails(technicalItemScoresetting);
				this.getManagedBean().setTechnicalItemScoreSetting(technicalItemScoresetting);
			}//35113-End

			settinglist = null;
			
			this.getManagedBean().setTechnicalScoring(null);
			this.getManagedBean().setTechnicalScoring(new ArrayList<ScQtCheckList>());
			
			List<ScQtCheckList> checkList = this.getManagedBean().getScQtCheckSt2techProposal();
			// 37813,35384,25635 remove coding.
			if (null != checkList && !checkList.isEmpty()) {
				reqDTO.setSpecType(SourcingConstants.TECHNICAL_DOCTYPE);
				reqDTO.setScQt(scqt);
				resDTO = specSo.retrieveCheckLists(reqDTO);
				
				for (ScQtCheckList list : checkList) {
					list=cloneScoreWeightage(list,resDTO.getTechnicalCheckList());
				// 37813,35384,25635 -start
				   checkScoreWeightageCheckList(list);
				// 37813,35384,25635 -end
				}
				// 37813,35384,25635 remove coding.				
				techWeightageSum();
				techScoreSum();
				if(!isTechnicalItemExist){//35113-start
				technicalItemScoreCalculate(this.getManagedBean().getTechnicalItemScoreSetting());
				}//35113-End

			}

			if (null != this.getManagedBean().getTechnicalScoring()
					&& !this.getManagedBean().getTechnicalScoring().isEmpty()) {
				this.getManagedBean().setTechnicalUserScoringSchema(false);
				
				for (ScQtCheckList technicalScoringChecklist : this.getManagedBean().getTechnicalScoring()) {
					if (null != technicalScoringChecklist.getPmStatus()
							&& QTStatusConst.TS_COMPLETED.equals(technicalScoringChecklist.getPmStatus().getStatusId())
							&& null != technicalScoringChecklist.getPmEvalStatus()
							&& QTStatusConst.TS_PENDING_SUBMISSION.equals(technicalScoringChecklist.getPmEvalStatus()
									.getStatusId())) {
						this.getManagedBean().setTechnicalUserScoringSchema(true);
						break;

					}
				}
			}
			
			checklistSorting(this.getManagedBean().getTechnicalScoring());
			//
			//

		} finally {
			specSo = null;
		}
	}	
	
	// 37813,35384,25635-start
	/**
	 * added scoring checklist to list and set weightage.
	 * 
	 * @param list
	 * @return ScQtCheckList
	 */
	private ScQtCheckList checkScoreWeightageCheckList(ScQtCheckList checkList) {
		if (checkList.isScoringReq()) {
			this.getManagedBean().getTechnicalScoring().add(checkList);
		} else {
			if (null == checkList.getQtScoreWeightage()) {
				ScQtScoreWeightage scoreWeightage = new ScQtScoreWeightage();
				checkList.setQtScoreWeightage(scoreWeightage);
			}
			checkList.getQtScoreWeightage().setScore(new BigDecimal(0));
		}
		if (null != checkList.getQtScoreWeightage() && null != checkList.getQtScoreWeightage().getScore()) {
			checkList.getQtScoreWeightage().setScore(checkList.getQtScoreWeightage().getScore());
		}
		return checkList;
	}

	// 37813,35384,25635-end
	
	private ScQtCheckList cloneScoreWeightage(ScQtCheckList val, List<ScQtCheckList> checkList) {
		String[] deftypes = { "UP", "PD", "SP", "NA", "SD" };
		List<String> dtypes = Arrays.asList(deftypes);
		
		for (ScQtCheckList check : checkList) {

			String title1 = check.getTitle().trim();
			String title2 = val.getTitle().trim();
			
			if (title1.contains(title2)) {
				// if(dtypes.contains(check.getDefinationType().getParameterCode())){
				if (check.getDocCategory().equals(val.getDocCategory())) {
					if (null != check.getQtScoreWeightage()) {
						ScQtScoreWeightage scoreWeightage = new ScQtScoreWeightage();
						scoreWeightage.setScore(check.getQtScoreWeightage().getScore());
						scoreWeightage.setWeightage(check.getQtScoreWeightage().getWeightage());
						
						val.setQtScoreWeightage(scoreWeightage);
						
						setUserDetails(val.getQtScoreWeightage());
					}
				}
				// }
			}
		}

		return val;
	}
	
	private ScQtCheckList cloneScoreWeightage2(ScQtCheckList val, List<ScQtCheckList> checkList) {
		String[] deftypes = { "UP", "PD", "SP", "NA", "SD" };
		List<String> dtypes = Arrays.asList(deftypes);
		
		for (ScQtCheckList check : checkList) {
			if (dtypes.contains(check.getDefinationType().getParameterCode())) {
				if (check.getDocCategory().equals(val.getDocCategory())) {
					if (check.getTitle().equals(val.getTitle())) {
						if (null != check.getQtScoreWeightage()) {
							ScQtScoreWeightage scoreWeightage = new ScQtScoreWeightage();
							scoreWeightage.setScore(check.getQtScoreWeightage().getScore());
							scoreWeightage.setWeightage(check.getQtScoreWeightage().getWeightage());
							
							val.setQtScoreWeightage(scoreWeightage);
							
							setUserDetails(val.getQtScoreWeightage());
						}
					}
				}
			}
		}

		return val;
	}
	
	private ScQtScoreSetting copyScoreSettings(ScQtScoreSetting src) {
		ScQtScoreSetting copy = new ScQtScoreSetting();
		BigDecimal passingScore = new BigDecimal(0);
		BigDecimal overallTechnical = new BigDecimal(0);
		BigDecimal overallFin = new BigDecimal(0);
		BigDecimal overallOtherTechnical = new BigDecimal(0);
		BigDecimal overallOtherFin = new BigDecimal(0);
		
		copy.setBrandNew(true);
		copy.setSettingType(src.getSettingType());
		copy.setScQt(this.getManagedBean().getQt());
		copy.setWeightageReq(src.isWeightageReq());
		
		if (null != src.getPassingScore()) {
			passingScore = src.getPassingScore();
		}
		
		if (null != src.getOverallTechnical()) {
			overallTechnical = src.getOverallTechnical();
		}
		
		if (null != src.getOverallFin()) {
			overallFin = src.getOverallFin();
		}
		
		if (null != src.getOverallOtherTechnical()) {
			overallOtherTechnical = src.getOverallOtherTechnical();
		}
		
		if (null != src.getOverallOtherFin()) {
			overallOtherFin = src.getOverallOtherFin();
		}
		
		copy.setPassingScore(passingScore);
		copy.setOverallTechnical(overallTechnical);
		copy.setOverallFin(overallFin);
		copy.setOverallOtherTechnical(overallOtherTechnical);
		copy.setOverallOtherFin(overallOtherFin);
		
		setUserDetails(copy);

		return copy;
	}
	
	private void technicalItemScoreCalculate(ScQtScoreSetting technicalItem) {
		try {
			if (null != technicalItem) {
				if (null == technicalItem.getOverallTechnical()) {
					throw new EpException(MessageConst.ERROR_760256);
				}
				
				if (technicalItem.getOverallTechnical().intValue() <= 0) {
					throw new EpException(MessageConst.ERROR_760269);
				}
				
				if (null == technicalItem.getOverallOtherTechnical()) {
					throw new EpException(MessageConst.ERROR_760256);
				}
				
				int value1 = technicalItem.getOverallTechnical() != null ? technicalItem.getOverallTechnical()
						.intValue() : 0;
				int value2 = technicalItem.getOverallOtherTechnical() != null ? technicalItem
						.getOverallOtherTechnical().intValue() : 0;
				int local = value1 + value2;
				
				this.getManagedBean().setTechnicalItemScore(new BigDecimal(local));
				
				if ((null != this.getManagedBean().getTechnicalItemScore())
						&& (this.getManagedBean().getTechnicalItemScore().intValue() < 100 || this.getManagedBean()
								.getTechnicalItemScore().intValue() > 100)) {
					throw new EpException(MessageConst.ERROR_760257);

				}
			}

		} finally {

		}
	}
	
	private void technicalWeightageCalculate() {
		if (null != this.getManagedBean().getTechnicalScoreSetting()
				&& this.getManagedBean().getTechnicalScoreSetting().isWeightageReq()
				&& !this.getManagedBean().isReadOnly()) {
			List<ScQtCheckList> checklist = this.getManagedBean().getTechnicalScoring();
			
			if (null == checklist) {
				checklist = new ArrayList<ScQtCheckList>();
			}
			// Map <ScQtScoreWeightage> map = new HashMap<ScQtScoreWeightage>();
			int technicalWeightage = 0;
			
			for (ScQtCheckList checklistlocal : checklist) {
				if (null != checklistlocal.getQtScoreWeightage()) {
					if (null == checklistlocal.getQtScoreWeightage().getWeightage()) {
						checklistlocal.getQtScoreWeightage().setWeightage(new BigDecimal(0));
					}

					technicalWeightage = technicalWeightage
							+ checklistlocal.getQtScoreWeightage().getWeightage().intValue();
				}
			}

			if (technicalWeightage < 100) {
				this.getManagedBean().setActiveIndex(4);
				throw new EpException(MessageConst.ERROR_760220);
			}
		}
	}
	
	private void techWeightageSum() {
		try {
			int total = 0;
			List<ScQtCheckList> weightageList = this.getManagedBean().getTechnicalScoring();
			
			if (weightageList == null) {
				weightageList = new ArrayList<ScQtCheckList>();
			}
			
			for (int i = 0; i < weightageList.size(); i++) {
				ScQtCheckList weight = (ScQtCheckList) weightageList.get(i);
				
				if (null != weight.getQtScoreWeightage() && weight.getQtScoreWeightage().getWeightage() == null) {
					weight.getQtScoreWeightage().setWeightage(new BigDecimal(0));
				}
				
				if (null != weight.getQtScoreWeightage()) {
					total = total + weight.getQtScoreWeightage().getWeightage().intValue();
				}
				
				this.getManagedBean().setTechnicalWeightageScore(new BigDecimal(total));
				
				if (total > 100) {
					throw new EpException(MessageConst.ERROR_760148);
				}
			}
		} finally {

		}
	}
	
	private void techScoreSum() {
		try {
			// 37813,35384,25635 -start
			BigDecimal totalScore = BigDecimal.valueOf(0);
			// 37813,35384,25635 -end
			List<ScQtCheckList> weightageList = this.getManagedBean()
					.getTechnicalScoring();
			if (weightageList == null) {
				// 37813,35384,25635 -start
				weightageList = new ArrayList<>();
				// 37813,35384,25635 -end
			}
			for (int i = 0; i < weightageList.size(); i++) {
				// 37813,35384,25635 -start
				ScQtCheckList weight = weightageList.get(i);
				if (null != weight.getQtScoreWeightage()) {
					ScQtScoreWeightage checklistWeighatge = setWeightageScore(weight
							.getQtScoreWeightage());
					weight.setQtScoreWeightage(checklistWeighatge);
					BigDecimal checkListScore = weight.getQtScoreWeightage()
							.getScore() != null ? weight.getQtScoreWeightage()
							.getScore() : BigDecimal.valueOf(0);
					totalScore = totalScore.add(checkListScore);
					// 37813,35384,25635 -end
				}

			}
			// 37813,35384,25635 -start
			this.getManagedBean().setTechPoints(totalScore);
			// 37813,35384,25635 -end
		} finally {

		}

	}
	
	// 37813,35384,25635 -start
	/**
	 * set checkist score weight.
	 * 
	 * @param scoreWeightage
	 * @return ScQtScoreWeightage
	 */
	private ScQtScoreWeightage setWeightageScore(ScQtScoreWeightage scoreWeightage) {
		if (null == scoreWeightage.getWeightage()) {
			scoreWeightage.setWeightage(new BigDecimal(0));
		}
		return scoreWeightage;
	}

	// 37813,35384,25635 -end
		
	private BigDecimal retrieveTechnicalEvalutionPassingScorePmrules(String ruleType) {
		IRuleSO ruleSO = (IRuleSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
				IRuleSO.class);
		BigDecimal technicalPassingScore = new BigDecimal(0);
		
		RuleReqDTO ruleReqDTO = new RuleReqDTO();
		ruleReqDTO.setRuleType(ruleType);
		
		List<PmConfigRuleValue> ruleValueList = ruleSO.getRuleValuesByTypeInCurrentDate(ruleReqDTO).getRuleValueList();
		
		if (ruleValueList != null && ruleValueList.size() > 0) {
			PmConfigRuleValue configRuleValue = ruleValueList.get(0);
			
			if (configRuleValue != null && configRuleValue.getRuleValue() != null) {
				technicalPassingScore = new BigDecimal(configRuleValue.getRuleValue());
			}
		} else {
			throw new EpException(MessageConst.ERROR_770222, new String[] { getRuleTypeDesc(ruleReqDTO.getRuleType()) });
		}
		
		ruleSO = null;
		
		return technicalPassingScore;
	}

	private List<ScQtCheckList> getToBeRemoveList() {
		List<ScQtCheckList> toBeRemoveList = new ArrayList<ScQtCheckList>();
		List<ScQtCheckList> techList = getManagedBean().getMarkDeletedTechChecklist();
		
		if (techList != null && techList.size() > 0) {
			toBeRemoveList.addAll(techList);
		}

		List<ScQtCheckList> finList = getManagedBean().getMarkDeletedFinChecklist();
		
		if (finList != null && finList.size() > 0) {
			toBeRemoveList.addAll(finList);
		}

		return toBeRemoveList;
	}

	private List<ScQtDeliveryLoc> getDeliveryByQt(ScQt copy) {
		IQTSpecSO specSo = null;
		List<ScQtDeliveryLoc> list = null;
		
		try {
			SpecificationReqDTO reqDTO = new SpecificationReqDTO();
			specSo = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
					IQTSpecSO.class);
			
			reqDTO.setQtId(copy.getQtId());
			
			SpecificationResDTO resDTO = specSo.retrieveDeliverSeriviceAddress(reqDTO);
			list = resDTO.getAddressResultBean();

		} finally {
			specSo = null;
		}

		return list;
	}

	private void retrieveDeliveryAddress(ScQt copy) {
		IQTSpecSO specSo = null;
		
		try {
			SpecificationReqDTO reqDTO = new SpecificationReqDTO();
			specSo = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
					IQTSpecSO.class);
			
			reqDTO.setQtId(copy.getQtId());
			
			SpecificationResDTO resDTO = specSo.retrieveDeliverSeriviceAddress(reqDTO);

			if (resDTO.getSpecZoneDtls() == null || resDTO.getSpecZoneDtls().isEmpty()) {

				// this.getManagedBean().setDeliveryLocRemoveList();
				ScQt qt = this.getManagedBean().getQt();
				List<ScQtDeliveryLoc> list = getDeliveryByQt(qt);
				
				if (list != null && !list.isEmpty()) {
					List<ScQtDeliveryLoc> listDelete = new ArrayList<ScQtDeliveryLoc>();
					for (ScQtDeliveryLoc loc : list) {
						loc.setDeleted(true);
						listDelete.add(loc);
					}
					
					this.getManagedBean().setDeliveryLocRemoveList(listDelete);
				}

				List<ScQtDeliveryLoc> locs = new ArrayList<ScQtDeliveryLoc>();
				
				if (null != resDTO.getAddressResultBean()) {
					for (ScQtDeliveryLoc copyLoc : resDTO.getAddressResultBean()) {
						ScQtDeliveryLoc loc = new ScQtDeliveryLoc();
						ScAddress deliveryAddress = populateDeliveryAddress(copyLoc.getPmAddressType());
						
						loc.setScDeliveryAddress(deliveryAddress);
						loc.setDeliveryAddress(getDeliveryAddressString(deliveryAddress));
						loc.setPmAddressType(copyLoc.getPmAddressType());
						loc.setTplSpecZoneDtl(this.getManagedBean().getTplSpecZoneDtl());
						loc.setScQt(this.getManagedBean().getQt());

						setUserDetails(loc);
						
						locs.add(loc);
					}
				}

				this.getManagedBean().setDeliveryNonZoneList(locs);
			}

		} finally {
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

		if (StringUtils.isNotBlank(scAddress.getAddress1()) && !NULL_VALUE.equalsIgnoreCase(scAddress.getAddress1())) {
			address.append(COMMA).append(scAddress.getAddress1());
		}

		if (StringUtils.isNotBlank(scAddress.getAddress2()) && !NULL_VALUE.equalsIgnoreCase(scAddress.getAddress2())) {
			address.append(COMMA).append(scAddress.getAddress2());
		}
		
		if (StringUtils.isNotBlank(scAddress.getAddress3()) && !NULL_VALUE.equalsIgnoreCase(scAddress.getAddress3())) {
			address.append(COMMA).append(scAddress.getAddress3());
		}
		
		if (StringUtils.isNotBlank(scAddress.getPostcode()) && !NULL_VALUE.equalsIgnoreCase(scAddress.getPostcode())) {
			address.append(COMMA).append(scAddress.getPostcode());
		}
		
		if (scAddress.getPmCity() != null && scAddress.getPmCity().getCityName() != null
				&& scAddress.getPmCity().getCityName().length() > 0) {
			address.append(COMMA).append(scAddress.getPmCity().getCityName());
		}
		
		if (scAddress.getPmState() != null && scAddress.getPmState().getStateName() != null
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
				if (QTConstants.TEMPLETE_SPEC_CODE.equalsIgnoreCase(specChecklist.getDefinationType()
						.getParameterCode())) {
					if (null != specChecklist.getScQtDoc()) {
						ITemplateSpecSO templateSpecSO = EpEjbBusinessDelegate.getInstance().getServiceObject(
								ModuleCodeConst.COMMON_COMPONENT, ITemplateSpecSO.class);
						TemplateSpecReqDTO specReqDTO = new TemplateSpecReqDTO();
						specReqDTO.setSpecMaster(specChecklist.getScQtDoc().getTplSpecMaster());
						specReqDTO.setContentType(TemplateSpecConst.CONTENT_TYPE_QUESTION);
						
						TemplateSpecResDTO resDTO = templateSpecSO.getTemplateMaster(specReqDTO);
						
						/*
						 * specChecklist.getScQtDoc().setTplSpecMaster(
						 * resDTO.getSpecMaster());
						 */
						
						if (resDTO.getSpecMaster().getItemTypeCode().getParameterCode()
								.equalsIgnoreCase(ParameterConst.ITEM_TYPE_SERVICE)) {
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
			public int compare(ScQtCheckList checklist1, ScQtCheckList checklist2) {

				// return
				// checklist1.getChecklistSeq()-checklist2.getChecklistSeq();
				return new CompareToBuilder().append(checklist1.getChecklistSeq(), checklist2.getChecklistSeq())
						.append(checklist1.getCreatedDate(), checklist2.getCreatedDate()).toComparison();
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
				// debugLogger("attribute {} " + pd.getName());
				emptyNames.add(pd.getName());
			}
		}
		
		for (String args : extra) {
			emptyNames.add(args);
		}
		
		return emptyNames;
	}

//	/**
//	 * Debug logger.
//	 * 
//	 * @param message
//	 *            the message
//	 */
//	private void debugLogger(Object message) {
//
//		ScQt scQt = this.getManagedBean().getQt();
//		if (scQt != null) {
//			message += " @qtNo = " + scQt.getQtNo();
//		}
//		if (logger.isDebugEnabled()) {
//			logger.debug(message);
//		}
//		/*
//		 * else{ logger.info(message); }
//		 */
//
//	}

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
		public QTPagingDataModel(String componentId, QtnTenderSearchBean cloneQtnTenderSearchBean) {
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
		public ISearchResultBean<ScQt> pageChangeListener(ISearchBean<ScQt> searchBean) {
			if (logger.isDebugEnabled()) {
				logger.debug("pageChangeListener");
			}

			IQTDetailSO detailSO;
			
			try {
				detailSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
						IQTDetailSO.class);

				QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();

				populateCloneQTSearchCriteria(cloneQtnTenderSearchBean, searchBean);
				
				reqDTO.setQtSearchBean(searchBean);

				QuationTenderResDTO resDTO = detailSO.retriveQuationTenderDetailsBySearchCriteria(reqDTO);

				return resDTO.getQtResultBean();
			} finally {
				detailSO = null;
			}
		}
	}

	private void populateCloneQTSearchCriteria(QtnTenderSearchBean cloneQtnTenderSearchBean,
			ISearchBean<ScQt> qtSearchBean) {
		if (cloneQtnTenderSearchBean != null) {
			List<SearchFilterBean> searchFilterBean = new ArrayList<SearchFilterBean>();
			String qtTitle = cloneQtnTenderSearchBean.getQtTitle();
			
			if (!StringUtils.isBlank(qtTitle)) {
				qtSearchBean.getDataFilterBeans()
						.add(new SearchFilterBean(QTConstants.QT_TITILE, SearchOperator.OPERATOR_LIKE.value(), qtTitle,
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

			if (null != this.getManagedBean().getQt() && null != this.getManagedBean().getQt().getQtNo()) {
				List<String> qtNo = new ArrayList<String>();
				qtNo.add(this.getManagedBean().getQt().getQtNo());
				
				SearchFilterBean bean = new SearchFilterBean();
				bean.setSearchField(QTConstants.QT_NUMBER);
				bean.setSearchOperator(SearchOperator.OPERATOR_NOT_IN.value());
				bean.setSearchValue(qtNo);
				
				searchFilterBean.add(bean);
			}

			if (null != this.getManagedBean().getQt() && null != this.getManagedBean().getPanel()
			// && QTStatusConst.QT_PENDING_QT_SUBMISSION.equals(statId)
			) {

				SearchFilterBean bean = new SearchFilterBean();
				bean.setSearchField("isPanel");
				bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
				bean.setSearchValue(this.getManagedBean().getPanel());
				
				searchFilterBean.add(bean);

				if (this.getManagedBean().getQt().isPanel()) {// 25635&31349-start
					CtCircularVer circularver = this.getManagedBean().getQt().getCircularVer();
					CtPanelItemGroup panelItemGroup = this.getManagedBean().getQt().getItemGroup();
					
					if (null != circularver) {
						SearchFilterBean circularbean = new SearchFilterBean();
						circularbean.setSearchField("circularVer.circularVerId");
						circularbean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
						circularbean.setSearchValue(circularver.getCircularVerId());
						searchFilterBean.add(circularbean);
					}

					if (null != panelItemGroup) {
						SearchFilterBean itemGroupbean = new SearchFilterBean();
						itemGroupbean.setSearchField("itemGroup.panelItemGroupId");
						itemGroupbean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
						itemGroupbean.setSearchValue(panelItemGroup.getPanelItemGroupId());
						
						searchFilterBean.add(itemGroupbean);
					}
				}// 25635&31349-End
			}

			if (null != this.getManagedBean().getQt()) {

				SearchFilterBean bean = new SearchFilterBean();
				bean.setSearchField("isZonal");
				bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
				bean.setSearchValue(this.getManagedBean().getQt().isZonal());
				
				searchFilterBean.add(bean);

			}

			if (null != this.getManagedBean().getQt() && null != this.getManagedBean().getQt().getContractType()
					&& null != this.getManagedBean().getQt().getContractType().getParameterId()) {

				SearchFilterBean bean = new SearchFilterBean();
				bean.setSearchField("contractType.parameterId");
				bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
				bean.setSearchValue(this.getManagedBean().getQt().getContractType().getParameterId());
				
				searchFilterBean.add(bean);

			}

			if (null != this.getManagedBean().getQt() && null != this.getManagedBean().getQt().getFulfilmentType()
					&& null != this.getManagedBean().getQt().getFulfilmentType().getParameterId()) {

				SearchFilterBean bean = new SearchFilterBean();
				bean.setSearchField("fulfilmentType.parameterId");
				bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
				bean.setSearchValue(this.getManagedBean().getQt().getFulfilmentType().getParameterId());
				
				searchFilterBean.add(bean);

			}

			String procurementMode = cloneQtnTenderSearchBean.getProcurementMode();
			
			if (!StringUtils.isBlank(procurementMode)) {
				qtSearchBean.getDataFilterBeans().add(
						new SearchFilterBean("procurementMode.parameterCode", SearchOperator.OPERATOR_EQUALS.value(),
								procurementMode));
			} else {
				// Filter By - procurementMode
				List<String> procurementModes = new ArrayList<String>();
				procurementModes.add(ParameterConst.PROCUREMENT_MODE_QUOTATION);
				procurementModes.add(ParameterConst.PROCUREMENT_MODE_TENDER);
				
				qtSearchBean.getDataFilterBeans().add(
						new SearchFilterBean("procurementMode.parameterCode", SearchOperator.OPERATOR_IN.value(),
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
			
			Date fromCreatedDate = cloneQtnTenderSearchBean.getFromCreatedDate();
			Date toCreatedDate = cloneQtnTenderSearchBean.getToCreatedDate();
			
			if (null != fromCreatedDate || toCreatedDate != null) {
				Calendar c1 = Calendar.getInstance();
				c1.setTime(fromCreatedDate);
				
				Calendar fromDate = Calendar.getInstance();
				fromDate.set(c1.get(Calendar.YEAR), c1.get(Calendar.MONTH), c1.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
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
					toDate.set(c2.get(Calendar.YEAR), c2.get(Calendar.MONTH), c2.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
					toDate.set(Calendar.MILLISECOND, 0);
					
					toCreatedDate = toDate.getTime();
				}
				
				SearchFilterBean bean = new SearchFilterBean();
				bean.setSearchField(QTConstants.CREATED_DATE);
				bean.setSearchOperator(SearchOperator.OPERATOR_BETWEEN.value());
				
				SearchBetweenValue<Date> betweenVal = new SearchBetweenValue<Date>(fromCreatedDate, toCreatedDate);
				
				bean.setSearchValue(betweenVal);
				
				searchFilterBean.add(bean);
			}

			Date fromPublishDate = cloneQtnTenderSearchBean.getFromPublishDate();
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
				
				SearchBetweenValue<Date> betweenVal = new SearchBetweenValue<Date>(fromPublishDate, toPublishDate);
				
				bean.setSearchValue(betweenVal);
				
				searchFilterBean.add(bean);
			}

			Date fromClosingDate = cloneQtnTenderSearchBean.getFromClosingDate();
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
				
				SearchBetweenValue<Date> betweenVal = new SearchBetweenValue<Date>(fromClosingDate, toClosingDate);
				
				bean.setSearchValue(betweenVal);
				
				searchFilterBean.add(bean);
			}
			
			String status = cloneQtnTenderSearchBean.getQtStatus();
			
			if (!StringUtils.isBlank(status)) {
				qtSearchBean.getDataFilterBeans().add(
						new SearchFilterBean("scQtStatus.pmStatus.statusId", SearchOperator.OPERATOR_EQUALS.value(),
								status));
				qtSearchBean.getDataFilterBeans().add(
						new SearchFilterBean("scQtStatus.current", SearchOperator.OPERATOR_EQUALS.value(), true));
			} else {
				Long[] stats = { QTStatusConst.QT_PENDING_QT_SUBMISSION,
						QTStatusConst.QT_PENDING_SC_COMMITTEE_APPROVAL,
						QTStatusConst.QT_PENDING_APPROVALS_CHECKLIST_SCORING,
						QTStatusConst.QT_PENDING_CHECKLIST_SCORING_REVISION };
				ArrayList<Long> s = new ArrayList<Long>(Arrays.asList(stats));
				
				qtSearchBean.getDataFilterBeans()
						.add(new SearchFilterBean("scQtStatus.pmStatus.statusId", SearchOperator.OPERATOR_NOT_IN
								.value(), s));
				qtSearchBean.getDataFilterBeans().add(
						new SearchFilterBean("scQtStatus.current", SearchOperator.OPERATOR_EQUALS.value(), true));
			}
			
			// Filter by - ptj org profileId
			qtSearchBean.getDataFilterBeans().add(
					new SearchFilterBean("pmOrgProfile.orgProfileId", SearchOperator.OPERATOR_EQUALS.value(),
							getPTJOrgProfileByLoginUser().getOrgProfileId()));

			// Filter by - evaluationType
			String commType = getManagedBean().getCommType();
			String evaluationType = getManagedBean().getQt().getEvaluationType();
			
			if (StringUtils.isNotBlank(evaluationType)
					&& (commType == null || !ParameterConst.COMMITTEE_TYPE_SPECIFICATION_COMMITTEE.equals(commType))) {
				qtSearchBean.getDataFilterBeans().add(
						new SearchFilterBean("evaluationType", SearchOperator.OPERATOR_EQUALS.value(), evaluationType));
			}
			
			if (null != this.getManagedBean().getQt()) {// 21695&25635-start

				SearchFilterBean bean = new SearchFilterBean();
				bean.setSearchField("isPartial");
				bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
				bean.setSearchValue(this.getManagedBean().getQt().isPartial());
				
				searchFilterBean.add(bean);

			}// 21695&25635-End
			
			if (null != this.getManagedBean().getQt()) {// 28784&25635-start

				SearchFilterBean bean = new SearchFilterBean();
				bean.setSearchField("demoRequired");
				bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
				bean.setSearchValue(this.getManagedBean().getQt().isDemoRequired());
				searchFilterBean.add(bean);

			}// 28784&25635-End
			
			if (null != this.getManagedBean().getQt()) {// 33870&25635-start

				SearchFilterBean bean = new SearchFilterBean();
				bean.setSearchField("sampleReq");
				bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
				bean.setSearchValue(this.getManagedBean().getQt().isSampleReq());
				searchFilterBean.add(bean);

			}// 33870&25635-End
			
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
		
		searchBean.getDataFilterBeans()
				.add(new SearchFilterBean("pmParameter.parameterCode", SearchOperator.OPERATOR_NOT_IN.value(),
						recommendList));
		
		SelectItem[] items = getDropdownCodeValuesBySearchBean(ParameterTypeConst.SC_PROCUREMENT_MODE, searchBean);
		
		return items;
	}
	
	/**
	 * Clone search qt action.
	 */
	public void cloneSearchQtAction() {
		getManagedBean().setCloneCommitteeSelectQt(null);
		
		this.getManagedBean().setSelectedCloneTender(null);

		getManagedBean().getCloneSearchQTDataModel().reloadData();
	}
	
	/**
	 * Reset clone.
	 */
	public void resetClone() {
		this.getManagedBean().getCloneQtnTenderSearchBean().setQtTitle(null);
		this.getManagedBean().getCloneQtnTenderSearchBean().setQtNo(null);
		this.getManagedBean().getCloneQtnTenderSearchBean().setProcurementMode(null);
		this.getManagedBean().getCloneQtnTenderSearchBean().setFromCreatedDate(null);
		this.getManagedBean().getCloneQtnTenderSearchBean().setToCreatedDate(null);
		this.getManagedBean().getCloneQtnTenderSearchBean().setQtStatus(null);
		this.getManagedBean().setSearchcommitteeNode(new DefaultTreeNode());
		this.getManagedBean().getCloneQtnTenderSearchBean().setFromPublishDate(null);
		this.getManagedBean().getCloneQtnTenderSearchBean().setToPublishDate(null);
		this.getManagedBean().getCloneQtnTenderSearchBean().setFromClosingDate(null);
		this.getManagedBean().getCloneQtnTenderSearchBean().setToClosingDate(null);
		
		getManagedBean().setCloneCommitteeSelectQt(null);
	}
	
	/*
	 * datatable page loading 
	 */
	public void searchActionPageChange() {
		this.getManagedBean().setSelectedCloneTender(null);
	}
	
	public void retrieveDeductionPaymentTabDtl(ScQt copyScqt) {// 25635&33733-start
		// if (this.getManagedBean().getScQtDeductionList() == null ||
		// this.getManagedBean().getScQtDeductionList().isEmpty()) {
		IQTDetailSO detailSO = null;
		
		try {
			detailSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
					IQTDetailSO.class);
			QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
			reqDTO.setQtId(copyScqt.getQtId());
			
			QuationTenderResDTO resDTO = detailSO.retriveQTDeductionPaymentDetails(reqDTO);
			List<ScQtDeduction> copyList = resDTO.getDeductionResultBean().getResultList();

			List<ScQtDeduction> deductionList = new ArrayList<ScQtDeduction>();

			for (ScQtDeduction ded : copyList) {
				ScQtDeduction obj = new ScQtDeduction();
				obj.setBrandNew(true);
				obj.setDeductionAmount(ded.getDeductionAmount());
				obj.setDeductionDesc(ded.getDeductionDesc());
				obj.setDeductionType(ded.getDeductionType());
				// obj.setScLoa(ded.getScLoa());
				// obj.setScLoi(ded.getScLoi());
				obj.setScQt(this.getManagedBean().getQt());
				
				List<ScQtDeductionClause> clauses = new ArrayList<ScQtDeductionClause>();
				
				for (ScQtDeductionClause cloneClause : ded.getScQtDeductionClauses()) {
					ScQtDeductionClause newClause = new ScQtDeductionClause();
					newClause.setBrandNew(true);
					
					setUserDetails(newClause);
					
					newClause.setScQtDeduction(obj);
					newClause.setClauseDesc(cloneClause.getClauseDesc());
					newClause.setClauseDescRendered(newClause.getClauseDescRendered());
					
					clauses.add(newClause);
				}
				
				obj.setScQtDeductionClauses(clauses);

				setUserDetails(obj);
				
				deductionList.add(obj);
			}

			reqDTO.setQtId(this.getManagedBean().getQt().getQtId());
			
			resDTO = detailSO.retriveQTDeductionPaymentDetails(reqDTO);
			List<ScQtDeduction> toDelete = resDTO.getDeductionResultBean().getResultList();
			
			if (toDelete != null && !toDelete.isEmpty()) {
				for (ScQtDeduction del : toDelete) {
					del.setDeleted(true);
				}
				
				this.getManagedBean().setRemoveDeductions(toDelete);
			}

			this.getManagedBean().setScQtDeductionList(deductionList);
			this.getManagedBean().getQt().setScQtDeductions(deductionList);
			
			getManagedBean().setSelectedScQtDeduction(null);
			
			/*
			 * if (null != deductionList && !deductionList.isEmpty()) { this.
			 * getManagedBean().setSelectedScQtDeduction(deductionList.get (0));
			 * this.getManagedBean().getSelectedScQtDeduction().
			 * setScQtDeductionClauses
			 * (deductionList.get(0).getScQtDeductionClauses()); }
			 */
		} finally {
			detailSO = null;
		}
		// }
	} //25635&33733-start
}

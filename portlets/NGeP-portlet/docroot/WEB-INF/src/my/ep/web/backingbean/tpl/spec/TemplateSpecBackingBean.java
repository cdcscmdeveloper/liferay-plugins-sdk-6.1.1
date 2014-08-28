/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.tpl.spec;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.bean.search.SearchOrderBean;
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.pm.common.uom.IUomSO;
import my.ep.biz.service.tpl.spec.ISpecZoneLocSO;
import my.ep.biz.service.tpl.spec.ITemplateSpecSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.tpl.TemplateSpecConst;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
import my.ep.dto.pm.common.uom.UomResDTO;
import my.ep.dto.tpl.spec.SpecZoneLocReqDTO;
import my.ep.dto.tpl.spec.SpecZoneLocResDTO;
import my.ep.dto.tpl.spec.TemplateSpecReqDTO;
import my.ep.dto.tpl.spec.TemplateSpecResDTO;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmUom;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.tpl.spec.TplSpecAnswer;
import my.ep.persistence.entity.tpl.spec.TplSpecEstimation;
import my.ep.persistence.entity.tpl.spec.TplSpecMaster;
import my.ep.persistence.entity.tpl.spec.TplSpecQuestion;
import my.ep.persistence.entity.tpl.spec.TplSpecScoring;
import my.ep.persistence.entity.tpl.spec.TplSpecTierQty;
import my.ep.persistence.entity.tpl.spec.TplSpecZone;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneDtl;
import my.ep.type.aut.SearchOperator;
import my.ep.util.MessageUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.bean.tpl.TemplateSpecBean;
import my.ep.web.managedbean.tpl.spec.TemplateSpecManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.tpl.helper.TemplateSpecHelper;

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;

/**
 * Template Specification Backing Bean
 * 
 * @author kian seng
 * @since 24/4/2013
 */
@ManagedBean(name="templateSpecBackingBean")
@ViewScoped
public class TemplateSpecBackingBean extends BaseBackingBean<TemplateSpecManagedBean> {

	private static Logger log = Logger.getLogger(TemplateSpecBackingBean.class);
	
	public static final String NAVIGATE_TO_SEARCH = "templateSpecSearch";
	
	private IPagingDataModel<TplSpecMaster> specMasterDataModel = new SpecMasterPagingDataModel();
	
	private void debugLogger(Object message) {
        if(log.isDebugEnabled()) {
            log.debug(message);
        }log.info(message);
    }

	public TemplateSpecBackingBean() {
	}
	
	public String retrieveTemplateSpecMaster(TemplateSpecBean templateSpecBean) {
		ITemplateSpecSO templateSpecSO = null;
		String contentType = templateSpecBean.getContentType();
		TplSpecMaster specMaster = templateSpecBean.getSpecMaster();
		
		try {
			templateSpecSO = (ITemplateSpecSO)BusinessDelegate.getInstance().getServiceObject("cc",ITemplateSpecSO.class);
			
			TemplateSpecReqDTO reqDTO = new TemplateSpecReqDTO();
			reqDTO.setSpecMaster(specMaster);
			reqDTO.setContentType(contentType);
			if (!TemplateSpecConst.CONTENT_TYPE_QUESTION.equals(contentType)) {
				SmSupplier smSupplier = templateSpecBean.getSupplier();
				
				/*if (smSupplier == null) {
					IOrgProfileSO orgProfileSO = BusinessDelegate.getInstance().getServiceObject(
							ModuleCodeConst.PROFILE_MANAGEMENT, IOrgProfileSO.class);
					OrgProfileReqDTO profileReqDTO = new OrgProfileReqDTO();
					profileReqDTO.setUserId(new FacesScopes().getUserSessionBean().getUserId());
					profileReqDTO.setOrgType(OrganizationType.SUPPLIER.value());
					OrgProfileResDTO profileResDTO = orgProfileSO
							.retrieveOrgProfileListByOrgType(profileReqDTO);
					
					if (profileResDTO.getOrgProfileList().size() > 0) {
						IRegistrationSO registrationSO = BusinessDelegate.getInstance().getServiceObject(
								ModuleCodeConst.SUPPLIER_MANAGEMENT, IRegistrationSO.class);
						RegistrationReqDTO registrationReqDTO = new RegistrationReqDTO();
						registrationReqDTO.setSupplierId(profileResDTO.getOrgProfileList().get(0).getOrgProfileId());
						RegistrationResDTO registrationResDTO = registrationSO.retrieveSuppOnlineAcc(registrationReqDTO);
						smSupplier = registrationResDTO.getSmSupp();	
					} else {
						IRegistrationSO registrationSO = BusinessDelegate.getInstance().getServiceObject(
								ModuleCodeConst.SUPPLIER_MANAGEMENT, IRegistrationSO.class);
						RegistrationReqDTO registrationReqDTO = new RegistrationReqDTO();
						registrationReqDTO.setSupplierId(27L);
						RegistrationResDTO registrationResDTO = registrationSO.retrieveSuppOnlineAcc(registrationReqDTO);
						smSupplier = registrationResDTO.getSmSupp();
					}
				}*/
				
				reqDTO.setSupplier(smSupplier);
			}

			TemplateSpecResDTO resDTO = templateSpecSO.getTemplateMaster(reqDTO);
			if(resDTO.isSuccessful()) {
				specMaster = resDTO.getSpecMaster();
				
				if (specMaster.getOrgProfile() == null) {
					specMaster.setOrgProfile(new PmOrgProfile());
				}
				
				templateSpecBean.setSpecMaster(specMaster);
				templateSpecBean.setSpecAnswerList(resDTO.getSpecAnswerList());
				templateSpecBean.setSelectedSpecQuestion(null);
			}
		} finally {
			templateSpecSO = null;
		}
		
		return "maintenance";
	}

	public void saveAction(TemplateSpecBean templateSpecBean) {
		debugLogger("saveAction");
		
		ITemplateSpecSO templateSpecSO = null;
		try {
			templateSpecSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.COMMON_COMPONENT,ITemplateSpecSO.class);
			TemplateSpecReqDTO reqDTO = new TemplateSpecReqDTO();
			
			TplSpecMaster specMaster = templateSpecBean.getSpecMaster();
			
			reqDTO.setSpecMaster(specMaster);
			TemplateSpecResDTO resDTO = templateSpecSO.saveTemplateSpec(reqDTO);		

			if(resDTO.isSuccessful()) {
				this.renewSesionObject();
				
				templateSpecBean.setSpecMaster(resDTO.getSpecMaster());
				retrieveTemplateSpecMaster(templateSpecBean);
				
				getManagedBean().setTemplateSpecBean(templateSpecBean);
				
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(
								FacesMessage.SEVERITY_INFO,
								null,
								MessageUtils.getMessage(
										MessageConst.INFO_100002,
										new String[] {},
										MessageUtils.BUNDLE_COMMON_MESSAGES,
										new FacesScopes().getUserLocale())));
			}
			
		} finally {
			templateSpecSO = null;
		}
	}

	public void saveAnswerAction(TemplateSpecBean templateSpecBean) {
		ITemplateSpecSO templateSpecSO = null;
		try {
			templateSpecSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.COMMON_COMPONENT,ITemplateSpecSO.class);

			List<TplSpecAnswer> answerList = templateSpecBean.getSpecAnswerList();
			
			TemplateSpecReqDTO reqDTO = new TemplateSpecReqDTO();
			reqDTO.setAnswerList(answerList);

			TemplateSpecResDTO resDTO = templateSpecSO.saveTemplateSpecAnswer(reqDTO);

			if(resDTO.isSuccessful()) {
				this.renewSesionObject();
				retrieveTemplateSpecMaster(templateSpecBean);
				
				getManagedBean().setTemplateSpecBean(templateSpecBean);
				
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(
								FacesMessage.SEVERITY_INFO,
								null,
								MessageUtils.getMessage(
										MessageConst.INFO_100002,
										new String[] {},
										MessageUtils.BUNDLE_COMMON_MESSAGES,
										new FacesScopes().getUserLocale())));
			}

		} finally {
			templateSpecSO = null;
		}
	}

	public void deleteQuestion(TemplateSpecBean templateSpecBean) {
		List<TplSpecQuestion> specQuestionList = templateSpecBean.getSpecMaster().getTplSpecQuestions();
		TplSpecQuestion questionToDelete = templateSpecBean.getSelectedSpecQuestion();
		int selectedIndex = specQuestionList.indexOf(questionToDelete);

		if(questionToDelete.getQuestionType().equalsIgnoreCase(TemplateSpecConst.QUESTION_TYPE_ITEM)) {
			for(int i = selectedIndex + 1; i < specQuestionList.size(); i++) {
				TplSpecQuestion childToDelete = specQuestionList.get(i);
				if(childToDelete.getQuestionType().equalsIgnoreCase(TemplateSpecConst.QUESTION_TYPE_ITEM)) {
					break;
				}
				if(!childToDelete.isBrandNew()) {
					childToDelete.setDeleted(true);
				}
				specQuestionList.remove(childToDelete);
				i--;
			}
		} 
		if(!questionToDelete.isBrandNew()) {
			questionToDelete.setDeleted(true);
		}
		specQuestionList.remove(questionToDelete);
	}

	public String newAction() {
		TemplateSpecBean templateSpecBean = new TemplateSpecBean();
		TplSpecMaster specMaster = new TplSpecMaster();
		
		specMaster.setProcurementCategory(new PmParameter());
		specMaster.setItemTypeCode(new PmParameter());
		specMaster.setTemplateTypeCode(new PmParameter());
		specMaster.setSpecTypeCode(new PmParameter());
		specMaster.setOrgProfile(new PmOrgProfile());
		
		templateSpecBean.setSpecMaster(specMaster);
		
		templateSpecBean.setSelectedSpecQuestion(null);
		getManagedBean().setTemplateSpecBean(templateSpecBean);
		
		return "maintenance"; 
	}

	public void searchResult(){
		debugLogger("searchResult");
		
		specMasterDataModel.reloadData();
	}
	
	public void storeAnswerSetting(ActionEvent event) {
		log.info("storeAnswerSetting()");
	}

	public void renderTable(ActionEvent event) {
		log.info("renderTable()");
	}

	public void addQuestion(TplSpecMaster tplSpecMaster, List<TplSpecQuestion> questionList, Integer row, String questionType) {
		IParameterSO parameterSO = null;
		try {
			TplSpecQuestion newQuestion = new TplSpecQuestion();
			newQuestion.setTplSpecMaster(tplSpecMaster);
			newQuestion.setQuestionType(questionType);
			
			parameterSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
			
			ParameterReqDTO reqDTO = new ParameterReqDTO();
			reqDTO.setParameterType(ParameterTypeConst.ANSWER_TYPE);
			if (ParameterConst.SPECIFICATION_TYPE_FINANCIAL.equals(tplSpecMaster.getSpecTypeCode().getParameterCode())) {
				reqDTO.setParameterCode(ParameterConst.ANSWER_TYPE_NUMERIC);
			} else {
				reqDTO.setParameterCode(ParameterConst.ANSWER_TYPE_TEXT);
			}
			ParameterResDTO resDTO = parameterSO.retrieveParameterByTypeAndCode(reqDTO);
			newQuestion.setAnswerTypeCode(resDTO.getPmParameter());
			resetAnswerTypeDtl(newQuestion);
			
			reqDTO = new ParameterReqDTO();
			reqDTO.setParameterType(ParameterTypeConst.SCORE_TYPE);
			reqDTO.setParameterCode(ParameterConst.SCORE_TYPE_USER);
			resDTO = parameterSO.retrieveParameterByTypeAndCode(reqDTO);
			newQuestion.setScoreTypeCode(resDTO.getPmParameter());
			resetScoreTypeDtl(newQuestion);
			
			if (TemplateSpecConst.QUESTION_TYPE_ITEM.equals(questionType)) {
				reqDTO = new ParameterReqDTO();
				reqDTO.setParameterType(ParameterTypeConst.FULFILMENT_TYPE);
				reqDTO.setParameterCode(ParameterConst.FULFILMENT_TYPE_ONE_OFF);
				resDTO = parameterSO.retrieveParameterByTypeAndCode(reqDTO);
				newQuestion.setClFulfilmentTypeCode(resDTO.getPmParameter());
				
				if (ParameterConst.ITEM_TYPE_SERVICE.equals(tplSpecMaster.getItemTypeCode().getParameterCode())) {
					reqDTO = new ParameterReqDTO();
					reqDTO.setParameterType(ParameterTypeConst.PRICE_TYPE);
					if (tplSpecMaster.getTplSpecZone() != null) {
						reqDTO.setParameterCode(ParameterConst.PRICE_TYPE_ZONAL_STANDARD);
					} else {
						reqDTO.setParameterCode(ParameterConst.PRICE_TYPE_STANDARD);
					}
					
					resDTO = parameterSO.retrieveParameterByTypeAndCode(reqDTO);
					newQuestion.setClPriceTypeCode(resDTO.getPmParameter());
				} else {
					newQuestion.setClPriceTypeCode(new PmParameter());	
				}
				resetPriceTypeDtl(newQuestion);
				
				reqDTO = new ParameterReqDTO();
				reqDTO.setParameterType(ParameterTypeConst.DELIVERY_TERM);
				reqDTO.setParameterCode(ParameterConst.DELIVERY_TERM_DAYS);
				resDTO = parameterSO.retrieveParameterByTypeAndCode(reqDTO);
				newQuestion.setClDeliveryTermTypeCode(resDTO.getPmParameter());
				
				PmUom uom = new PmUom();
				newQuestion.setClUom(uom);	
			}
			
			newQuestion.setClAnswerRequired(true);
			
			if (questionList == null) {
				questionList = new ArrayList<TplSpecQuestion>();
				
				tplSpecMaster.setTplSpecQuestions(questionList);
			}
			
			questionList.add(row + 1, newQuestion);
			for(int i = 0; i < questionList.size(); i++) {
				TplSpecQuestion question = questionList.get(i);
				question.setQuestionOrder(i+1);
			}
		} finally {
			parameterSO = null;
		}
	}
	
	public SelectItem[] getUomList() {
	    SelectItem[] items = null;
	    
		IUomSO uomSO = null;
		try {
			uomSO = (IUomSO)BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IUomSO.class);
			UomResDTO resDTO = uomSO.retrieveUomList();
			items = new SelectItem[resDTO.getPmUomList().size()+1];
			items[0] = new SelectItem(null, "- Select One -");
			int i = 1;
			for(PmUom uom : resDTO.getPmUomList()) {
				items[i++] = new SelectItem(uom.getUomId(), uom.getUomCode()+" "+uom.getUomName());
			}
		} finally {
			uomSO = null;
		}
	    return items;
	}
	
	public SelectItem[] getMinistryList() {
	    SelectItem[] items = null;
	    
		IOrgProfileSO orgProfileSO = null;
		try {
			orgProfileSO = (IOrgProfileSO)BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IOrgProfileSO.class);
			
			OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
			reqDTO.setOrgType(ParameterConst.ORG_TYPE_MINISTRY);
			OrgProfileResDTO resDTO = orgProfileSO.retrieveOrgProfileListByOrgType(reqDTO);
			
			items = new SelectItem[resDTO.getOrgProfileList().size()+1];
			items[0] = new SelectItem(null, "- Select One -");
			int i = 1;
			for(PmOrgProfile orgProfile : resDTO.getOrgProfileList()) {
				items[i++] = new SelectItem(orgProfile.getOrgProfileId(), orgProfile.getOrgName());
			}
		} finally {
			orgProfileSO = null;
		}
	    return items;
	}
	
	public SelectItem[] getSpecTypeDropdownList() {
		ISearchBean<PmParameterDesc> searchBean = new SearchBean<PmParameterDesc>();
		
		searchBean.getDataFilterBeans().add(
				new SearchFilterBean("pmParameter.parameterCode",
						SearchOperator.OPERATOR_NOT_EQUALS.value(), 
						ParameterConst.SPECIFICATION_TYPE_FINANCIAL));
		
		return super.getDropdownValuesBySearchBean(ParameterTypeConst.SPECIFICATION_TYPE, searchBean);
	}
	
	public SelectItem[] getItemTypeDropdownList() {
		ISearchBean<PmParameterDesc> searchBean = new SearchBean<PmParameterDesc>();
		
		searchBean.getDataFilterBeans().add(
				new SearchFilterBean("pmParameter.parameterCode",
						SearchOperator.OPERATOR_NOT_EQUALS.value(), 
						ParameterConst.ITEM_TYPE_PRODUCT_AND_SERVICE));
		
		return super.getDropdownValuesBySearchBean(ParameterTypeConst.SC_ITEM_TYPE, searchBean);
	}
	
	public SelectItem[] getPriceTypeDropdownList(TplSpecMaster specMaster) {
		ISearchBean<PmParameterDesc> searchBean = new SearchBean<PmParameterDesc>();
		List<String> filteredList = new ArrayList<String>();
		boolean scoringReq = specMaster.isScoringReq();
		boolean estCostReq = specMaster.isEstCostReq();
		
		if (specMaster.getTplSpecZone() == null) {
			filteredList.add(ParameterConst.PRICE_TYPE_STANDARD);
			
			if (scoringReq) {
				filteredList.add(ParameterConst.PRICE_TYPE_TIER);
				filteredList.add(ParameterConst.PRICE_TYPE_TARIFF);
			} else if (!estCostReq) {
				filteredList.add(ParameterConst.PRICE_TYPE_TIER);
			}
		} else {
			filteredList.add(ParameterConst.PRICE_TYPE_ZONAL_STANDARD);
			
			if (scoringReq) {
				filteredList.add(ParameterConst.PRICE_TYPE_ZONAL_TIER);
				filteredList.add(ParameterConst.PRICE_TYPE_ZONAL_TARIFF);
			} else if (!estCostReq) {
				filteredList.add(ParameterConst.PRICE_TYPE_ZONAL_TIER);
			}
		}
		
		searchBean.getDataFilterBeans().add(
				new SearchFilterBean("pmParameter.parameterCode",
						SearchOperator.OPERATOR_IN.value(), 
						filteredList));
		
		return super.getDropdownValuesBySearchBean(ParameterTypeConst.PRICE_TYPE, searchBean);
	}
	
	public void itemTypeChange() {
		IParameterSO parameterSO = null;
		try {
			TplSpecMaster specMaster = getManagedBean().getTemplateSpecBean().getSpecMaster();
			Long parameterId = specMaster.getItemTypeCode().getParameterId();
			
			parameterSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
			ParameterReqDTO reqDTO = new ParameterReqDTO();
			reqDTO.setParameterId(parameterId);
			ParameterResDTO resDTO = parameterSO.retrieveParameterById(reqDTO);
			specMaster.setItemTypeCode(resDTO.getPmParameter());
		} finally {
			parameterSO = null;
		}
	}
	
	public void specTypeChange() {
		IParameterSO parameterSO = null;
		try {
			TplSpecMaster specMaster = getManagedBean().getTemplateSpecBean().getSpecMaster();
			Long parameterId = specMaster.getSpecTypeCode().getParameterId();
			
			parameterSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
			ParameterReqDTO reqDTO = new ParameterReqDTO();
			reqDTO.setParameterId(parameterId);
			ParameterResDTO resDTO = parameterSO.retrieveParameterById(reqDTO);
			specMaster.setSpecTypeCode(resDTO.getPmParameter());
		} finally {
			parameterSO = null;
		}
	}
	
	public void templateTypeChange() {
		IParameterSO parameterSO = null;
		try {
			TplSpecMaster specMaster = getManagedBean().getTemplateSpecBean().getSpecMaster();
			Long parameterId = specMaster.getTemplateTypeCode().getParameterId();
			
			parameterSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
			ParameterReqDTO reqDTO = new ParameterReqDTO();
			reqDTO.setParameterId(parameterId);
			ParameterResDTO resDTO = parameterSO.retrieveParameterById(reqDTO);
			specMaster.setTemplateTypeCode(resDTO.getPmParameter());
		} finally {
			parameterSO = null;
		}
	}
	
	public void fulfilmentTypeChange(TplSpecQuestion specQuestion) {
		IParameterSO parameterSO = null;
		try {
			Long parameterId = specQuestion.getClFulfilmentTypeCode().getParameterId();
			
			parameterSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
			ParameterReqDTO reqDTO = new ParameterReqDTO();
			reqDTO.setParameterId(parameterId);
			ParameterResDTO resDTO = parameterSO.retrieveParameterById(reqDTO);
			specQuestion.setClFulfilmentTypeCode(resDTO.getPmParameter());
		} finally {
			parameterSO = null;
		}
		
	}
	
	public void priceTypeChange(TplSpecQuestion specQuestion) {
		IParameterSO parameterSO = null;
		try {
			Long parameterId = specQuestion.getClPriceTypeCode().getParameterId();
			
			parameterSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
			ParameterReqDTO reqDTO = new ParameterReqDTO();
			reqDTO.setParameterId(parameterId);
			ParameterResDTO resDTO = parameterSO.retrieveParameterById(reqDTO);
			specQuestion.setClPriceTypeCode(resDTO.getPmParameter());
			
			resetPriceTypeDtl(specQuestion);
		} finally {
			parameterSO = null;
		}
		
	}
	
	public void resetPriceTypeDtl(TplSpecQuestion specQuestion) {
		List<TplSpecEstimation> specEstimations = null;
		if (specQuestion.getTplSpecMaster().isEstCostReq() && 
				TemplateSpecHelper.getInstance().isPriceTypeStandard(
						specQuestion.getClPriceTypeCode().getParameterCode())) {
			
			specEstimations = new ArrayList<TplSpecEstimation>();
			
			TplSpecEstimation specEstimation = new TplSpecEstimation();
			specEstimation.setTplSpecQuestion(specQuestion);
			
			specEstimations.add(specEstimation);
		}
		
		specQuestion.setTplSpecTierQties(null);
		specQuestion.setTplSpecEstimations(specEstimations);
	}
	
	public void answerTypeChange(TplSpecQuestion specQuestion) {
		IParameterSO parameterSO = null;
		try {
			Long parameterId = specQuestion.getAnswerTypeCode().getParameterId();
			
			parameterSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
			ParameterReqDTO reqDTO = new ParameterReqDTO();
			reqDTO.setParameterId(parameterId);
			ParameterResDTO resDTO = parameterSO.retrieveParameterById(reqDTO);
			specQuestion.setAnswerTypeCode(resDTO.getPmParameter());
			
			resetAnswerTypeDtl(specQuestion);
		} finally {
			parameterSO = null;
		}
		
	}
	
	private void resetAnswerTypeDtl(TplSpecQuestion specQuestion) {
		if (ParameterConst.ANSWER_TYPE_TEXT.equals(specQuestion.getAnswerTypeCode().getParameterCode())) {
			IParameterSO parameterSO = null;
			try {
				parameterSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
				
				ParameterReqDTO reqDTO = new ParameterReqDTO();
				reqDTO.setParameterType(ParameterTypeConst.SCORE_TYPE);
				reqDTO.setParameterCode(ParameterConst.SCORE_TYPE_USER);
				
				ParameterResDTO resDTO = parameterSO.retrieveParameterByTypeAndCode(reqDTO);
				specQuestion.setScoreTypeCode(resDTO.getPmParameter());
			} finally {
				parameterSO = null;
			}
		}	
	}
	
	public void scoreTypeChange(TplSpecQuestion specQuestion, Long selectedScoreTypeId) {
		IParameterSO parameterSO = null;
		try {
			
			parameterSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
			ParameterReqDTO reqDTO = new ParameterReqDTO();
			reqDTO.setParameterId(selectedScoreTypeId);
			ParameterResDTO resDTO = parameterSO.retrieveParameterById(reqDTO);
			specQuestion.setScoreTypeCode(resDTO.getPmParameter());
			
			resetScoreTypeDtl(specQuestion);
		} finally {
			parameterSO = null;
		}
	}
	
	private void resetScoreTypeDtl(TplSpecQuestion specQuestion) {
		// reset the scoring list.
		specQuestion.setTplSpecScorings(null);
		
		// add compliance scoring.
		addComplianceScoringAction(specQuestion);
	}
	
	public void deliveryTermChange(TplSpecQuestion specQuestion) {
		IParameterSO parameterSO = null;
		try {
			Long parameterId = specQuestion.getClDeliveryTermTypeCode().getParameterId();
			
			parameterSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
			
			ParameterReqDTO reqDTO = new ParameterReqDTO();
			reqDTO.setParameterId(parameterId);
			ParameterResDTO resDTO = parameterSO.retrieveParameterById(reqDTO);
			specQuestion.setClDeliveryTermTypeCode(resDTO.getPmParameter());
		} finally {
			parameterSO = null;
		}
		
	}
	
	public void calcTotalAmount(TemplateSpecBean templateSpecBean, TplSpecAnswer specAnswer, boolean isItemTypeService) {
		TplSpecQuestion specQuestion = specAnswer.getTplSpecQuestion();
		TplSpecMaster specMaster = specQuestion.getTplSpecMaster();
		BigDecimal quantity = specQuestion.getClQuantity();
		BigDecimal ratePerUom = specAnswer.getRatePerUom();
		BigDecimal totalPrice = quantity;
		boolean isPriceTypeTier = TemplateSpecHelper.getInstance().isPriceTypeTier(
				specQuestion.getClPriceTypeCode().getParameterCode());
		
		if (!isPriceTypeTier) {
			if (ratePerUom != null) {
				totalPrice = totalPrice.multiply(ratePerUom);	
			}
			
			if (isItemTypeService) {
				BigDecimal uomFrequency = specQuestion.getClUomFrequency();
				BigDecimal uomPerDay = specQuestion.getClUomPerDay();
				BigDecimal uomPerMonth = specQuestion.getClUomPerMonth();
				BigDecimal durationMonth = specMaster.getDurationMonth();
				BigDecimal itemDurationMonth = specQuestion.getClDurationMonth();
				
				if (uomFrequency != null) {
					totalPrice = totalPrice.multiply(uomFrequency);
				}
				
				if (uomPerDay != null) {
					totalPrice = totalPrice.multiply(uomPerDay);
				}
				
				if (uomPerMonth != null) {
					totalPrice = totalPrice.multiply(uomPerMonth);
				}
				
				specAnswer.setAmountPerYear(totalPrice.multiply(BigDecimal.valueOf(12)));
				
				if (durationMonth != null) {
					totalPrice = totalPrice.multiply(durationMonth);
				}
				
				if (itemDurationMonth != null) {
					totalPrice = totalPrice.multiply(itemDurationMonth);
				}
			}
			specAnswer.setAnswerNumeric(totalPrice);	
		}
	}
	
	public void scoringPopupAction(TemplateSpecBean templateSpecBean) {
		debugLogger("scoringPopupAction");
		
		TplSpecQuestion tplSpecQuestion = templateSpecBean.getSelectedSpecQuestion();
		
		// update selected score type id
		templateSpecBean.setSelectedScoreTypeId(
				tplSpecQuestion.getScoreTypeCode().getParameterId());
		
		// add compliance scoring.
		addComplianceScoringAction(tplSpecQuestion);
	}
	
	private void addComplianceScoringAction(TplSpecQuestion tplSpecQuestion) {
		
		if (ParameterConst.ANSWER_TYPE_DROPDOWN.equals(tplSpecQuestion.getAnswerTypeCode().getParameterCode())) {
			List<TplSpecScoring> specScorings = tplSpecQuestion.getTplSpecScorings();
			
			if (specScorings == null || specScorings.size() == 0) {
				specScorings = new ArrayList<TplSpecScoring>();
				
				TplSpecScoring specScoring = new TplSpecScoring();
				specScoring.setTplSpecQuestion(tplSpecQuestion);
				specScoring.setCompliance(true);
				specScorings.add(specScoring);
				
				specScoring = new TplSpecScoring();
				specScoring.setTplSpecQuestion(tplSpecQuestion);
				specScoring.setCompliance(false);
				specScorings.add(specScoring);
				
				tplSpecQuestion.setTplSpecScorings(specScorings);
			}
		}
		
	}
	
	public void estimationPopupAction(TplSpecQuestion tplSpecQuestion) {
		debugLogger("estimationPopupAction");
		
		List<TplSpecEstimation> specEstimations = tplSpecQuestion.getTplSpecEstimations();
		TplSpecZone specZone = tplSpecQuestion.getTplSpecMaster().getTplSpecZone();
		List<TplSpecZoneDtl> specZoneDtls = null;
		
		if (specZone != null) {
			if (specZone.isBrandNew()) {
				specZoneDtls = specZone.getTplSpecZoneDtls();	
			} else {
				ISpecZoneLocSO specZoneLocSO;
				try {
					specZoneLocSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.COMMON_COMPONENT, ISpecZoneLocSO.class);
					
					SpecZoneLocReqDTO reqDTO = new SpecZoneLocReqDTO();
					reqDTO.setDocType(specZone.getDocType());
					reqDTO.setDocId(specZone.getDocId());
					
					// Get zone details list from db.
					SpecZoneLocResDTO resDTO = specZoneLocSO.retrieveZoneDtlsByDocIdNType(reqDTO);
					
					specZoneDtls = resDTO.getZonesList();
				} finally {
					specZoneLocSO = null;
				}	
			}
		}
		
		if (specEstimations == null || specEstimations.size() == 0 || (specZoneDtls != null && specEstimations.size() != specZoneDtls.size())) {
			
			specEstimations = new ArrayList<TplSpecEstimation>();
			
			for (int zoneRow = 0; zoneRow < (specZoneDtls == null || specZoneDtls.size() == 0 ? 1 : specZoneDtls.size()); zoneRow++) {
				if (TemplateSpecHelper.getInstance().isPriceTypeTier(
						tplSpecQuestion.getClPriceTypeCode().getParameterCode())) {
					
					if (tplSpecQuestion.getTplSpecTierQties() != null) {
						for (TplSpecTierQty specTierQty : tplSpecQuestion.getTplSpecTierQties()) {
							TplSpecEstimation specEstimation = new TplSpecEstimation();
							
							if (specZoneDtls != null && specZoneDtls.size() > 0) {
								specEstimation.setTplSpecZoneDtl(specZoneDtls.get(zoneRow));	
							}
							specEstimation.setTplSpecQuestion(tplSpecQuestion);
							specEstimation.setTplSpecTierQty(specTierQty);
							specEstimations.add(specEstimation);
						}	
					}
				} else {
					if (specZoneDtls != null && specZoneDtls.size() > 0) {
						TplSpecEstimation specEstimation = new TplSpecEstimation();
						specEstimation.setTplSpecQuestion(tplSpecQuestion);
						specEstimation.setTplSpecZoneDtl(specZoneDtls.get(zoneRow));
						
						specEstimations.add(specEstimation);
					}
				}
			}
			tplSpecQuestion.setTplSpecEstimations(specEstimations);
		}
	}
	
	public void addTierQtyAction(TplSpecQuestion tplSpecQuestion) {
		debugLogger("addTierQtyAction");
		
		List<TplSpecTierQty> tplSpecTierQties = tplSpecQuestion.getTplSpecTierQties();
		
		if (tplSpecTierQties == null) {
			tplSpecTierQties = new ArrayList<TplSpecTierQty>();
			
			tplSpecQuestion.setTplSpecTierQties(tplSpecTierQties);
		}
		
		TplSpecTierQty tplSpecTierQty = new TplSpecTierQty();
		tplSpecTierQty.setTplSpecQuestion(tplSpecQuestion);
		tplSpecTierQties.add(tplSpecTierQty);
		
		tplSpecQuestion.setTplSpecEstimations(null);
	}
	
	public void addScoringAction(TplSpecQuestion tplSpecQuestion){
		debugLogger("addScoringAction");
		
		List<TplSpecScoring> tplSpecScorings = tplSpecQuestion.getTplSpecScorings();
		
		if (tplSpecScorings == null) {
			tplSpecScorings = new ArrayList<TplSpecScoring>();
			
			tplSpecQuestion.setTplSpecScorings(tplSpecScorings);
		}
		
		TplSpecScoring tplSpecScoring = new TplSpecScoring();
		tplSpecScoring.setTplSpecQuestion(tplSpecQuestion);
		tplSpecScorings.add(tplSpecScoring);
	}
	
	public void tierQtyOkAction(TemplateSpecBean templateSpecBean) {
		debugLogger("tierQtyOkAction");
		
		ITemplateSpecSO templateSpecSO = null;
		try {
			List<TplSpecTierQty> specTierQties = templateSpecBean.getSelectedSpecQuestion().getTplSpecTierQties();
			
			templateSpecSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.COMMON_COMPONENT, ITemplateSpecSO.class);
			
			templateSpecSO.validateTierQty(specTierQties);
			
		} finally {
			templateSpecSO = null;
		}
		
		templateSpecBean.setSelectedSpecQuestion(null);
		
		RequestContext.getCurrentInstance().execute(
				templateSpecBean.getSpecComponentId() + "tierQtyPopup.hide()");
	}
	
	public void scoringOkAction(TemplateSpecBean templateSpecBean) {
		debugLogger("scoringOkAction");
		
		templateSpecBean.setSelectedSpecQuestion(null);
		
		RequestContext.getCurrentInstance().execute(
				templateSpecBean.getSpecComponentId() + "scoringSetPopup.hide()");
	}
	
	public void estCostOkAction(TemplateSpecBean templateSpecBean) {
		debugLogger("estCostOkAction");
		
		templateSpecBean.setSelectedSpecQuestion(null);
		
		RequestContext.getCurrentInstance().execute(
				templateSpecBean.getSpecComponentId() + "estimationPopup.hide()");
	}
	
	public BigDecimal calcGrandTotalAmount(TemplateSpecBean templateSpecBean) {
		BigDecimal totalAmount = new BigDecimal(0);
		BigDecimal totalAmountPerYear = new BigDecimal(0);
		Long tierSpecQuestionId = null;
		TplSpecMaster specMaster = templateSpecBean.getSpecMaster();
		List<TplSpecAnswer> specAnswerList = templateSpecBean.getSpecAnswerList();
		TemplateSpecHelper templateSpecHelper = TemplateSpecHelper.getInstance();
		
		for (TplSpecAnswer tplSpecAnswer : specAnswerList) {
			TplSpecQuestion specQuestion = tplSpecAnswer.getTplSpecQuestion();
			String priceType = specQuestion.getClPriceTypeCode().getParameterCode();
			BigDecimal quantity = specQuestion.getClQuantity();
			
			if (templateSpecHelper.isPriceTypeTariff(priceType)) {
				// TODO - calculate tariff price.
				
			} else if (templateSpecHelper.isPriceTypeTier(priceType) && 
					(tierSpecQuestionId == null || tierSpecQuestionId.compareTo(
							specQuestion.getSpecQuestionId()) != 0)) {
				// Always used the 1st tier quantity unit price to calculate total amount.
				if (tplSpecAnswer.getRatePerUom() != null) {
					totalAmount = totalAmount.add(quantity.multiply(tplSpecAnswer.getRatePerUom()));	
				}
				
				tierSpecQuestionId = specQuestion.getSpecQuestionId();
				
			} else if (templateSpecHelper.isPriceTypeStandard(priceType) || 
					templateSpecHelper.isPriceTypeZonalStandard(priceType)) {
				
				if (tplSpecAnswer.getAnswerNumeric() != null) {
					totalAmount = totalAmount.add(tplSpecAnswer.getAnswerNumeric());	
				}
			}
			
			if (tplSpecAnswer.getAmountPerYear() != null) {
				totalAmountPerYear = totalAmountPerYear.add(tplSpecAnswer.getAmountPerYear());	
			}
		}
		
		specMaster.setTotalAmountPerYear(totalAmountPerYear);
		specMaster.setTotalAmount(totalAmount);
		
		return totalAmount;
	}
	
	public boolean isAllPriceTypesStandard(TemplateSpecBean templateSpecBean) {
		boolean isAllPriceTypesStandard = true;
		String contentType = templateSpecBean.getContentType();
		List<TplSpecQuestion> specQuestions = null;
		
		if (TemplateSpecConst.CONTENT_TYPE_QUESTION.equals(contentType) ||
				TemplateSpecConst.CONTENT_TYPE_REVIEW.equals(contentType)) {
			specQuestions = templateSpecBean.getSpecMaster().getTplSpecQuestions();
		} else {
			List<TplSpecAnswer> specAnswers = templateSpecBean.getSpecAnswerList();
			
			if (specAnswers != null) {
				specQuestions = new ArrayList<TplSpecQuestion>();
				
				for (TplSpecAnswer specAnswer : specAnswers) {
					TplSpecQuestion specQuestion = specAnswer.getTplSpecQuestion();
					
					specQuestions.add(specQuestion);
				}
			}
		}
		
		if (specQuestions != null) {
			TemplateSpecHelper templateSpecHelper = TemplateSpecHelper.getInstance();
			
			for (TplSpecQuestion specQuestion : specQuestions) {
				if (TemplateSpecConst.QUESTION_TYPE_ITEM.equals(specQuestion.getQuestionType())) {
					String priceType = specQuestion.getClPriceTypeCode().getParameterCode();
					
					if (!templateSpecHelper.isPriceTypeStandard(priceType)) {
						isAllPriceTypesStandard = false;
						
						break;
					}
				}
			}
		}
		
		return isAllPriceTypesStandard;
	}
	
	/**
	 * template spec list paging class
	 * 
	 * @author kenny
	 */
	private class SpecMasterPagingDataModel extends PagingDataModel<TplSpecMaster> {

		private static final long serialVersionUID = 1L;

		public SpecMasterPagingDataModel() {
			super("tplSpecMasterTbl");
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see my.ep.web.bean.paginator.PagingDataModel#pageChangeListener(my.ep.biz.bean.search.ISearchBean)
		 */
		@Override
		public ISearchResultBean<TplSpecMaster> pageChangeListener(
				ISearchBean<TplSpecMaster> searchBean) {
			if (log.isDebugEnabled()) {
				log.debug("pageChangeListener");
			}

			ITemplateSpecSO templateSpecSO;
			try {
				templateSpecSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.COMMON_COMPONENT,ITemplateSpecSO.class);
				
				searchBean.getDataFilterBeans().add(
						new SearchFilterBean("processType",
								SearchOperator.OPERATOR_EQUALS.value(),
								TemplateSpecConst.PROCESS_TYPE_STANDARD));
				
				if(getManagedBean().getSearchProcurementCategoryId()!=null) {
					searchBean.getDataFilterBeans().add(
							new SearchFilterBean("procurementCategory.parameterId",
									SearchOperator.OPERATOR_EQUALS.value(),
									getManagedBean().getSearchProcurementCategoryId()));
				}
				
				if(getManagedBean().getSearchTemplateTypeId()!=null) {
					searchBean.getDataFilterBeans().add(
							new SearchFilterBean("templateTypeCode.parameterId",
									SearchOperator.OPERATOR_EQUALS.value(),
									getManagedBean().getSearchTemplateTypeId()));
				}
				
				if(getManagedBean().getSearchOrgProfileId()!=null) {
					searchBean.getDataFilterBeans().add(
							new SearchFilterBean("orgProfile.orgProfileId",
									SearchOperator.OPERATOR_EQUALS.value(),
									getManagedBean().getSearchOrgProfileId()));
				}
				
				if(getManagedBean().getSearchItemTypeId()!=null) {
					searchBean.getDataFilterBeans().add(
							new SearchFilterBean("itemTypeCode.parameterId",
									SearchOperator.OPERATOR_EQUALS.value(),
									getManagedBean().getSearchItemTypeId()));
				}
				
				if(getManagedBean().getSearchSpecTypeId()!=null) {
					searchBean.getDataFilterBeans().add(
							new SearchFilterBean("specTypeCode.parameterId",
									SearchOperator.OPERATOR_EQUALS.value(),
									getManagedBean().getSearchSpecTypeId()));
				}
				
				if(getManagedBean().getSearchSpecName()!=null) {
					searchBean.getDataFilterBeans().add(
							new SearchFilterBean("specName",
									SearchOperator.OPERATOR_LIKE.value(),
									getManagedBean().getSearchSpecName()));
				}

				// Order by created date
				searchBean.getSortColumns().add(new SearchOrderBean("specMasterId", false));
				
				TemplateSpecReqDTO reqDTO = new TemplateSpecReqDTO();
				reqDTO.setSearchBean(searchBean);
				TemplateSpecResDTO resDTO = templateSpecSO.searchTemplateMaster(reqDTO);
				if(resDTO.isSuccessful()) {
					return resDTO.getSearchResultBean();
				}
			} finally {
				templateSpecSO = null;
			}

			return null;
		}
	}

	/**
	 * @return the specMasterDataModel
	 */
	public IPagingDataModel<TplSpecMaster> getSpecMasterDataModel() {
		return specMasterDataModel;
	}

	/**
	 * @param specMasterDataModel the specMasterDataModel to set
	 */
	public void setSpecMasterDataModel(
			IPagingDataModel<TplSpecMaster> specMasterDataModel) {
		this.specMasterDataModel = specMasterDataModel;
	}
}
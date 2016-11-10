/* 
 ***************************************************   
 * Product nextgen ePerolehan 
 * 
 * Release 1.00    
 *    
 * Copyright © 2013 Archisoft. All rights reserved.
 * 
 ****************************************************
 */
package my.ep.web.backingbean.tpl.spec;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;

import my.ep.biz.bean.search.BaseSearchFilterBean;
import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.bean.search.SearchMultiFilterBean;
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.tpl.spec.ISpecZoneLocSO;
import my.ep.biz.service.tpl.spec.ITemplateSpecSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.tpl.TemplateSpecConst;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
import my.ep.dto.tpl.spec.SpecZoneLocReqDTO;
import my.ep.dto.tpl.spec.SpecZoneLocResDTO;
import my.ep.dto.tpl.spec.TemplateSpecReqDTO;
import my.ep.dto.tpl.spec.TemplateSpecResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.BaseEntity;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
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
import my.ep.persistence.entity.tpl.spec.TplSpecZoneQty;
import my.ep.type.aut.SearchCondition;
import my.ep.type.aut.SearchOperator;
import my.ep.util.MessageUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.bean.tpl.TemplateSpecBean;
import my.ep.web.managedbean.tpl.spec.TemplateSpecManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.PmCommonUtil;
import my.ep.web.util.tpl.helper.TemplateSpecHelper;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;

/**
 * Template Specification Backing Bean
 * 
 * @author kian seng
 * @since 24/4/2013
 */
@ManagedBean(name = "templateSpecBackingBean")
@ViewScoped
public class TemplateSpecBackingBean extends
		BaseBackingBean<TemplateSpecManagedBean> implements Serializable{

	
	private static final long serialVersionUID = 1L;

	private static Logger log = Logger.getLogger(TemplateSpecBackingBean.class);

	public static final String NAVIGATE_TO_SEARCH = "templateSpecSearch";
	private static final String TPL_SPEC_KEY = "lbl.sc.template.spec";

	private IPagingDataModel<TplSpecMaster> specMasterDataModel = new SpecMasterPagingDataModel();

	private void debugLogger(Object message) {
		if (log.isDebugEnabled()) {
			log.debug(message);
		}
		log.info(message);
	}

	/**
	 * Constructor for template specification backing bean
	 */
	public TemplateSpecBackingBean() {
	}

	/**
	 * Get the template specification details by template specification bean
	 * 
	 * @param templateSpecBean
	 *            the TemplateSpecBean instance
	 * @return the template specification redirect url
	 */
	public String retrieveTemplateSpecMaster(TemplateSpecBean templateSpecBean) {
		ITemplateSpecSO templateSpecSO = null;
		String contentType = templateSpecBean.getContentType();
		TplSpecMaster specMaster = templateSpecBean.getSpecMaster();
		TplSpecZoneDtl speczoneDtl = templateSpecBean.getTplSpecZoneDtl();

		try {
			templateSpecSO = (ITemplateSpecSO) BusinessDelegate.getInstance()
					.getServiceObject("cc", ITemplateSpecSO.class);

			TemplateSpecReqDTO reqDTO = new TemplateSpecReqDTO();
			reqDTO.setSpecMaster(specMaster);
			reqDTO.setContentType(contentType);
			if (!TemplateSpecConst.CONTENT_TYPE_QUESTION.equals(contentType)) {
				SmSupplier smSupplier = templateSpecBean.getSupplier();

				// if (smSupplier == null) {
				// IOrgProfileSO orgProfileSO = BusinessDelegate.getInstance()
				// .getServiceObject(
				// ModuleCodeConst.PROFILE_MANAGEMENT,
				// IOrgProfileSO.class);
				// OrgProfileReqDTO profileReqDTO = new OrgProfileReqDTO();
				// profileReqDTO.setUserId(/*new FacesScopes()
				// .getUserSessionBean().getUserId()*/81569L);
				// profileReqDTO.setOrgType(OrganizationType.SUPPLIER.value());
				// OrgProfileResDTO profileResDTO = orgProfileSO
				// .retrieveOrgProfileListByOrgType(profileReqDTO);
				//
				// if (profileResDTO.getOrgProfileList().size() > 0) {
				// IRegistrationSO registrationSO = BusinessDelegate
				// .getInstance().getServiceObject(
				// ModuleCodeConst.SUPPLIER_MANAGEMENT,
				// IRegistrationSO.class);
				// RegistrationReqDTO registrationReqDTO = new
				// RegistrationReqDTO();
				// registrationReqDTO.setSupplierId(profileResDTO
				// .getOrgProfileList().get(0).getOrgProfileId());
				// RegistrationResDTO registrationResDTO = registrationSO
				// .retrieveSuppOnlineAcc(registrationReqDTO);
				// smSupplier = registrationResDTO.getSmSupp();
				// } else {
				// IRegistrationSO registrationSO = BusinessDelegate
				// .getInstance().getServiceObject(
				// ModuleCodeConst.SUPPLIER_MANAGEMENT,
				// IRegistrationSO.class);
				// RegistrationReqDTO registrationReqDTO = new
				// RegistrationReqDTO();
				// registrationReqDTO.setSupplierId(27L);
				// RegistrationResDTO registrationResDTO = registrationSO
				// .retrieveSuppOnlineAcc(registrationReqDTO);
				// smSupplier = registrationResDTO.getSmSupp();
				// }
				//
				// templateSpecBean.setSupplier(smSupplier);
				// }

				reqDTO.setSupplier(smSupplier);
			}
			if (null != speczoneDtl) {
				reqDTO.setTplSpecZoneDtl(speczoneDtl);
			}
			reqDTO.setScQt(this.getManagedBean().getScQt());
			reqDTO.setDocType(this.getManagedBean().getDocType());
			TemplateSpecResDTO resDTO = templateSpecSO
					.getTemplateMaster(reqDTO);
			if (resDTO.isSuccessful()) {
				specMaster = resDTO.getSpecMaster();

				if (specMaster.getOrgProfile() == null) {
					specMaster.setOrgProfile(new PmOrgProfile());
				}

				templateSpecBean.setSpecMaster(specMaster);
				templateSpecBean.setSpecAnswerList(resDTO.getSpecAnswerList());
				templateSpecBean.setSelectedSpecQuestion(null);
				templateSpecBean.setSpecAnswersToDelete(null);

				if (specMaster.getSpecTypeCode() != null &&
						ParameterConst.SPECIFICATION_TYPE_FINANCIAL.equals(specMaster.getSpecTypeCode().getParameterCode())
						&& resDTO.getSpecAnswerList() != null) {
					calcGrandTotalAmount(templateSpecBean);
				}
			}
		} finally {
			templateSpecSO = null;
		}

		return "maintenance";
	}

	/**
	 * Save the template specification master and details action
	 * 
	 * @param templateSpecBean
	 *            the TemplateSpecBean instance
	 */
	public void saveAction(TemplateSpecBean templateSpecBean) {
		debugLogger("saveAction");

		ITemplateSpecSO templateSpecSO = null;
		try {
			templateSpecSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.COMMON_COMPONENT, ITemplateSpecSO.class);
			TemplateSpecReqDTO reqDTO = new TemplateSpecReqDTO();

			TplSpecMaster specMaster = templateSpecBean.getSpecMaster();

			reqDTO.setSpecMaster(specMaster);
			reqDTO.setSpecAnswersToDelete(templateSpecBean
					.getSpecAnswersToDelete());
			reqDTO.setLocale(new FacesScopes().getUserLocale());
			TemplateSpecResDTO resDTO = templateSpecSO.saveTemplateSpec(reqDTO);

			if (resDTO.isSuccessful()) {
				boolean isReadOnly = this.getManagedBean().isReadOnly();
				this.renewSesionObject();

				templateSpecBean.setSpecMaster(resDTO.getSpecMaster());
				retrieveTemplateSpecMaster(templateSpecBean,isReadOnly);

				getManagedBean().setTemplateSpecBean(templateSpecBean);
				
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, null,
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

	/**
	 * Save the template specification answer action
	 * 
	 * @param templateSpecBean
	 *            the TemplateSpecBean instance
	 */
	public void saveAnswerAction(TemplateSpecBean templateSpecBean) {
		ITemplateSpecSO templateSpecSO = null;
		try {
			templateSpecSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.COMMON_COMPONENT, ITemplateSpecSO.class);

			List<TplSpecAnswer> answerList = templateSpecBean
					.getSpecAnswerList();

			TemplateSpecReqDTO reqDTO = new TemplateSpecReqDTO();
			reqDTO.setAnswerList(answerList);
			reqDTO.setContentType(TemplateSpecConst.CONTENT_TYPE_ANSWER);

			TemplateSpecResDTO resDTO = templateSpecSO
					.saveTemplateSpecAnswer(reqDTO);

			if (resDTO.isSuccessful()) {
				this.renewSesionObject();
				retrieveTemplateSpecMaster(templateSpecBean);

				getManagedBean().setTemplateSpecBean(templateSpecBean);

				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, null,
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

	/**
	 * Delete template specification question by template specification bean.
	 * 
	 * @param templateSpecBean
	 *            the TemplateSpecBean instance
	 */
	public void deleteQuestion(TemplateSpecBean templateSpecBean) {
		List<TplSpecQuestion> specQuestionList = templateSpecBean
				.getSpecMaster().getTplSpecQuestions();
		TplSpecQuestion questionToDelete = templateSpecBean
				.getSelectedSpecQuestion();
		int selectedIndex = specQuestionList.indexOf(questionToDelete);

		if (questionToDelete.getQuestionType().equalsIgnoreCase(
				TemplateSpecConst.QUESTION_TYPE_ITEM)) {
			for (int i = selectedIndex + 1; i < specQuestionList.size(); i++) {
				TplSpecQuestion childToDelete = specQuestionList.get(i);
				if (childToDelete.getQuestionType().equalsIgnoreCase(
						TemplateSpecConst.QUESTION_TYPE_ITEM)) {
					break;
				}
				if (!childToDelete.isBrandNew()) {
					childToDelete.setDeleted(true);
				}
				specQuestionList.remove(childToDelete);
				i--;
			}
		}
		if (!questionToDelete.isBrandNew()) {
			questionToDelete.setDeleted(true);
		}
		specQuestionList.remove(questionToDelete);

		if (TemplateSpecConst.CONTENT_TYPE_QUESTION_AND_ASNWER
				.equals(templateSpecBean.getContentType())) {
			TemplateSpecHelper.getInstance().addToBeRemovedSpecAnswers(
					templateSpecBean, questionToDelete.getTplSpecAnswers());
		}

		templateSpecBean.setSelectedSpecQuestion(null);
	}

	/**
	 * Create new template specification action
	 * 
	 * @return the template specification redirect url
	 */
	public String newAction() {
		TemplateSpecBean templateSpecBean = new TemplateSpecBean();
		TplSpecMaster specMaster = new TplSpecMaster();
		FacesScopes facesScopes = new FacesScopes();

		specMaster.setProcurementCategory(new PmParameter());
		specMaster.setItemTypeCode(new PmParameter());
		specMaster.setTemplateTypeCode(new PmParameter());
		specMaster.setSpecTypeCode(getParameterByTypeAndCode(
				ParameterTypeConst.SPECIFICATION_TYPE, 
				ParameterConst.SPECIFICATION_TYPE_TECHNICAL));
		specMaster.setOrgProfile(new PmOrgProfile());
		setUserDetails(specMaster);
		specMaster.setCreatedUser(facesScopes.getUserSessionBean());

		IUserSO userSO = null;
		// IOrgProfileSO orgProfileSO = null;
		try {
			userSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);

			// orgProfileSO = BusinessDelegate.getInstance().getServiceObject(
			// ModuleCodeConst.PROFILE_MANAGEMENT, IOrgProfileSO.class);

			UserReqDTO reqDTO = new UserReqDTO();
			reqDTO.setUserId(facesScopes.getUserSessionBean().getUserId());
			UserResDTO resDTO = userSO.retrieveUserOrgListByUserId(reqDTO);

			if (resDTO.getUserOrgList() != null
					&& resDTO.getUserOrgList().size() > 0) {
				// PmOrgProfile pmOrgProfile = resDTO.getUserOrgList().get(0)
				// .getPmOrgProfile();
				// OrgProfileReqDTO orgProfileReqDTO = new OrgProfileReqDTO();
				// orgProfileReqDTO.setOrgProfileId(pmOrgProfile.getOrgProfileId());
				// OrgProfileResDTO orgProfileResDTO =
				// orgProfileSO.retrieveMinistryByOrgProfileId(orgProfileReqDTO);

				// if (orgProfileResDTO.getPmOrgProfile() != null) {
				// pmOrgProfile = orgProfileResDTO.getPmOrgProfile();
				// }

				// specMaster.setOwnerOrgProfile(pmOrgProfile);

				specMaster.setOwnerOrgProfile(resDTO.getUserOrgList().get(0)
						.getPmOrgProfile());
			}

		} finally {
			userSO = null;
			// orgProfileSO = null;
		}

		templateSpecBean.setSpecMaster(specMaster);

		templateSpecBean.setSelectedSpecQuestion(null);
		getManagedBean().setTemplateSpecBean(templateSpecBean);

		return "maintenance";
	}

	/**
	 * Search template specification action
	 */
	public void searchResult() {
		debugLogger("searchResult");

		specMasterDataModel.reloadData();
	}

	/**
	 * Add new template specification question
	 * 
	 * @param tplSpecMaster
	 *            the TplSpecMaster instance
	 * @param questionList
	 *            the list of TplSpecQuestion instance
	 * @param row
	 *            the insert row
	 * @param questionType
	 *            the question type
	 */
	public TplSpecQuestion addQuestion(TemplateSpecBean templateSpecBean,
			TplSpecMaster tplSpecMaster, List<TplSpecQuestion> questionList,
			Integer row, String questionType) {
		TplSpecQuestion newQuestion = new TplSpecQuestion();
		boolean isQuestionTypeItem = TemplateSpecConst.QUESTION_TYPE_ITEM
				.equals(questionType);
		boolean isFinancialSpec = ParameterConst.SPECIFICATION_TYPE_FINANCIAL
				.equals(tplSpecMaster.getSpecTypeCode().getParameterCode());
		TemplateSpecHelper specHelper = TemplateSpecHelper.getInstance();

		if (questionList == null) {
			questionList = new ArrayList<TplSpecQuestion>();

			tplSpecMaster.setTplSpecQuestions(questionList);
		}

		newQuestion.setTplSpecMaster(tplSpecMaster);
		newQuestion.setQuestionType(questionType);

		if (isQuestionTypeItem) {
			PmParameter pmParameter = getParameterByTypeAndCode(
					ParameterTypeConst.FULFILMENT_TYPE,
					ParameterConst.FULFILMENT_TYPE_ONE_OFF);
			newQuestion.setClFulfilmentTypeCode(pmParameter);

			if (ParameterConst.ITEM_TYPE_SERVICE.equals(tplSpecMaster
					.getItemTypeCode().getParameterCode())) {
				if (tplSpecMaster.getTplSpecZone() != null) {
					pmParameter = getParameterByTypeAndCode(
							ParameterTypeConst.PRICE_TYPE,
							ParameterConst.PRICE_TYPE_ZONAL_STANDARD);
					newQuestion.setClPriceTypeCode(pmParameter);
				} else {
					pmParameter = getParameterByTypeAndCode(
							ParameterTypeConst.PRICE_TYPE,
							ParameterConst.PRICE_TYPE_STANDARD);
					newQuestion.setClPriceTypeCode(pmParameter);
				}

				specHelper.resetSpecAnswers(templateSpecBean, newQuestion);
			} else {
				newQuestion.setClPriceTypeCode(new PmParameter());
			}
			specHelper.resetPriceTypeDtl(newQuestion);

			pmParameter = getParameterByTypeAndCode(
					ParameterTypeConst.DELIVERY_TERM,
					ParameterConst.SQ_DURATION_DAY);
			newQuestion.setClDeliveryTermTypeCode(pmParameter);

			PmUom uom = new PmUom();
			newQuestion.setClUom(uom);

			if (isFinancialSpec) {
				pmParameter = getParameterByTypeAndCode(
						ParameterTypeConst.ANSWER_TYPE,
						ParameterConst.ANSWER_TYPE_NUMERIC);
				newQuestion.setAnswerTypeCode(pmParameter);
				resetAnswerTypeDtl(newQuestion);
			}
		} else {
			newQuestion.setAnswerTypeCode(new PmParameter());
		}

		newQuestion.setClAnswerRequired(true);

		if ((isFinancialSpec || (!isFinancialSpec && !isQuestionTypeItem))
				&& tplSpecMaster.isWeightageReq()) {
			newQuestion.setWeightageReq(true);
		}
		if(null!=tplSpecMaster.getCtPanelItem() && TemplateSpecConst.QUESTION_TYPE_ITEM.equals(newQuestion
				.getQuestionType())){
			newQuestion.setCtPanelItem(tplSpecMaster.getCtPanelItem());
		}
		if (TemplateSpecConst.QUESTION_TYPE_ITEM_SPEC.equals(newQuestion
				.getQuestionType()) && row < questionList.size()) {
			TplSpecQuestion prevSpecQuestion = questionList.get(row);
			String prevQuestionType = prevSpecQuestion.getQuestionType();
            
			if (TemplateSpecConst.QUESTION_TYPE_ITEM.equals(prevQuestionType)) {
				newQuestion.setParentSpecQuestion(prevSpecQuestion);
			} else if (TemplateSpecConst.QUESTION_TYPE_ITEM_SPEC
					.equals(prevQuestionType)) {
				newQuestion.setParentSpecQuestion(prevSpecQuestion
						.getParentSpecQuestion());
			}
		}

		questionList.add(row + 1, newQuestion);
		for (int i = 0; i < questionList.size(); i++) {
			TplSpecQuestion question = questionList.get(i);
			question.setQuestionOrder(i + 1);
		}

		return newQuestion;
	}

	/**
	 * Get item type dropdown list
	 * 
	 * @return the list of SelectItem instance
	 */
	public SelectItem[] getItemTypeDropdownList() {
		ISearchBean<PmParameterDesc> searchBean = new SearchBean<PmParameterDesc>();

		searchBean.getDataFilterBeans().add(
				new SearchFilterBean("pmParameter.parameterCode",
						SearchOperator.OPERATOR_NOT_EQUALS.value(),
						ParameterConst.ITEM_TYPE_PRODUCT_AND_SERVICE));

		return super.getDropdownValuesBySearchBean(
				ParameterTypeConst.SC_ITEM_TYPE, searchBean);
	}

	/**
	 * Get fulfilement type dropdown list
	 * 
	 * @return the list of SelectItem instance
	 */
	public SelectItem[] getFulfilmentTypeDropdownList(TplSpecMaster specMaster) {
		ISearchBean<PmParameterDesc> searchBean = new SearchBean<PmParameterDesc>();
		List<String> filteredList = new ArrayList<String>();
		
		// For Pid SQ ,No periodic schedule
		if(!(getManagedBean().isPidSQ() || getManagedBean().isG2G())){
			filteredList.add(ParameterConst.FULFILMENT_TYPE_SCHEDULED);
		}
		filteredList.add(ParameterConst.FULFILMENT_TYPE_ONE_OFF);

		searchBean.getDataFilterBeans().add(
				new SearchFilterBean("pmParameter.parameterCode",
						SearchOperator.OPERATOR_IN.value(), filteredList));

		return super.getDropdownValuesBySearchBean(
				ParameterTypeConst.FULFILMENT_TYPE, searchBean);
	}

	/**
	 * Get price type dropdown list
	 * 
	 * @return the list of SelectItem instance
	 */
	public SelectItem[] getPriceTypeDropdownList(TplSpecMaster specMaster) {
		ISearchBean<PmParameterDesc> searchBean = new SearchBean<PmParameterDesc>();
		List<String> filteredList = new ArrayList<String>();
		boolean specificationReq = specMaster.isSpecificationReq();
		boolean estCostReq = specMaster.isEstCostReq();

		if (specMaster.getTplSpecZone() == null) {
			filteredList.add(ParameterConst.PRICE_TYPE_STANDARD);

			if (specificationReq) {
				filteredList.add(ParameterConst.PRICE_TYPE_TIER);
				// filteredList.add(ParameterConst.PRICE_TYPE_TARIFF);
			} else if (!estCostReq) {
				filteredList.add(ParameterConst.PRICE_TYPE_TIER);
			}
		} else {
			filteredList.add(ParameterConst.PRICE_TYPE_ZONAL_STANDARD);

			if (specificationReq) {
				filteredList.add(ParameterConst.PRICE_TYPE_ZONAL_TIER);
				// filteredList.add(ParameterConst.PRICE_TYPE_ZONAL_TARIFF);
			} else if (!estCostReq) {
				filteredList.add(ParameterConst.PRICE_TYPE_ZONAL_TIER);
			}
		}

		searchBean.getDataFilterBeans().add(
				new SearchFilterBean("pmParameter.parameterCode",
						SearchOperator.OPERATOR_IN.value(), filteredList));

		return super.getDropdownValuesBySearchBean(
				ParameterTypeConst.PRICE_TYPE, searchBean);
	}

	/**
	 * Get estimation cost by zone/normal
	 * 
	 * @param specAnswer
	 *            the TplSpecAnswer instance
	 * @return the estimation cost
	 */
	public BigDecimal getEstimationCostByAnswer(TplSpecAnswer specAnswer) {
		long specZoneId = specAnswer.getTplSpecSuppAnswer().getTplSpecZoneDtl() != null ? specAnswer
				.getTplSpecSuppAnswer().getTplSpecZoneDtl().getSpecZoneId()
				.longValue()
				: 0;

		BigDecimal estimatedCost = null;

		for (TplSpecEstimation tmpSpecEstimation : specAnswer
				.getTplSpecQuestion().getTplSpecEstimations()) {
			long tmpSpecZoneId = tmpSpecEstimation.getTplSpecZoneDtl() != null ? tmpSpecEstimation
					.getTplSpecZoneDtl().getSpecZoneId().longValue()
					: 0;

			if (specZoneId == tmpSpecZoneId) {
				estimatedCost = tmpSpecEstimation.getEstimatedCost();

				break;
			}
		}

		return estimatedCost;
	}

	/**
	 * Item Type dropdown change action
	 */
	public void itemTypeChange() {
		IParameterSO parameterSO = null;
		try {
			TplSpecMaster specMaster = getManagedBean().getTemplateSpecBean()
					.getSpecMaster();
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

	/**
	 * Specification Type dropdown change action
	 */
	public void specTypeChange() {
		IParameterSO parameterSO = null;
		try {
			TplSpecMaster specMaster = getManagedBean().getTemplateSpecBean()
					.getSpecMaster();
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
	
	/**
	 * Search Template Type dropdown change action
	 */
	public void searchTemplateTypeChange() {
		getManagedBean().setSearchOrgProfile(new PmOrgProfile());
	}

	/**
	 * Template Type dropdown change action
	 */
	public void templateTypeChange(AjaxBehaviorEvent event) {
		IParameterSO parameterSO = null;
		IOrgProfileSO orgProfileSO = null;
		try {
			TplSpecMaster specMaster = getManagedBean().getTemplateSpecBean()
					.getSpecMaster();
			Long parameterId = (Long) event.getComponent().getAttributes()
					.get("value");
			
			PmOrgProfile pmOrgProfile = null;

			parameterSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
			ParameterReqDTO reqDTO = new ParameterReqDTO();
			reqDTO.setParameterId(parameterId);
			ParameterResDTO resDTO = parameterSO.retrieveParameterById(reqDTO);
			specMaster.setTemplateTypeCode(resDTO.getPmParameter());
			
			if (ParameterConst.TEMPLATE_TYPE_MINISTRY.equals(specMaster.getTemplateTypeCode().getParameterCode())) {
				if (ParameterConst.ORG_TYPE_MINISTRY.equals(specMaster.getOwnerOrgProfile().getOrgTypeCode().getParameterCode())) {
					pmOrgProfile = specMaster.getOwnerOrgProfile();
				} else if (ParameterConst.ORG_TYPE_PTJ.equals(specMaster.getOwnerOrgProfile().getOrgTypeCode().getParameterCode())) {
					orgProfileSO = BusinessDelegate.getInstance().getServiceObject(
							ModuleCodeConst.PROFILE_MANAGEMENT, IOrgProfileSO.class);
					
					OrgProfileReqDTO orgProfileReqDTO = new OrgProfileReqDTO();
					orgProfileReqDTO.setOrgProfileId(specMaster.getOwnerOrgProfile().getOrgProfileId());
					pmOrgProfile = orgProfileSO.retrieveMinistryByOrgProfileId(orgProfileReqDTO)
							.getPmOrgProfile();
				}
			} else if (ParameterConst.TEMPLATE_TYPE_PTJ.equals(specMaster.getTemplateTypeCode().getParameterCode())) {
				if (ParameterConst.ORG_TYPE_PTJ.equals(specMaster.getOwnerOrgProfile().getOrgTypeCode().getParameterCode())) {
					pmOrgProfile = specMaster.getOwnerOrgProfile();
				}
			}
			
			if (pmOrgProfile == null) {
				pmOrgProfile = new PmOrgProfile();	
			}
			
			specMaster.setOrgProfile(pmOrgProfile);
		} finally {
			parameterSO = null;
			orgProfileSO = null;
		}
	}

	/**
	 * Fulfilment Type dropdown change action
	 */
	public void fulfilmentTypeChange(TplSpecQuestion specQuestion) {
		IParameterSO parameterSO = null;
		try {
			Long parameterId = specQuestion.getClFulfilmentTypeCode()
					.getParameterId();

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

	/**
	 * Price Type dropdown change action
	 */
	public void priceTypeChange(TemplateSpecBean templateSpecBean,
			TplSpecQuestion specQuestion) {
		Long parameterId = specQuestion.getClPriceTypeCode().getParameterId();

		// if (parameterId != null) {
		IParameterSO parameterSO = null;
		try {
			parameterSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);

			ParameterReqDTO reqDTO = new ParameterReqDTO();
			reqDTO.setParameterId(parameterId);
			ParameterResDTO resDTO = parameterSO.retrieveParameterById(reqDTO);
			specQuestion.setClPriceTypeCode(resDTO.getPmParameter());

			TemplateSpecHelper specHelper = TemplateSpecHelper.getInstance();
			specHelper.resetPriceTypeDtl(specQuestion);

			specHelper.resetSpecAnswers(templateSpecBean, specQuestion);
		} finally {
			parameterSO = null;
		}
	}

	/**
	 * Answer Type dropdown change action
	 * 
	 * @param specQuestion
	 *            the TplSpecQuestion instance
	 */
	public void answerTypeChange(TplSpecQuestion specQuestion) {
		IParameterSO parameterSO = null;
		try {
			Long parameterId = specQuestion.getAnswerTypeCode()
					.getParameterId();

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
		if (ParameterConst.ANSWER_TYPE_TEXT.equals(specQuestion
				.getAnswerTypeCode().getParameterCode())
				&& specQuestion.getTplSpecMaster().isScoringReq()) {
			PmParameter pmParameter = getParameterByTypeAndCode(
					ParameterTypeConst.SCORE_TYPE,
					ParameterConst.SCORE_TYPE_USER);
			specQuestion.setScoreTypeCode(pmParameter);
		} else {
			specQuestion.setScoreTypeCode(null);
		}
	}

	/**
	 * Score Type dropdown change action
	 * 
	 * @param specQuestion
	 *            the TplSpecQuestion instance
	 */
	public void scoreTypeChange(TplSpecQuestion specQuestion,
			Long selectedScoreTypeId) {
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

	/**
	 * Delivery Term dropdown change action
	 * 
	 * @param specQuestion
	 *            the TplSpecQuestion instance
	 */
	public void deliveryTermChange(TplSpecQuestion specQuestion) {
		IParameterSO parameterSO = null;
		try {
			Long parameterId = specQuestion.getClDeliveryTermTypeCode()
					.getParameterId();

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

	/**
	 * Calculate the total amount and amount per year for each item.
	 * 
	 * @param templateSpecBean
	 *            the TemplateSpecBean instance
	 * @param specAnswer
	 *            the TplSpecAnswer instance
	 * @param isItemTypeService
	 *            the item type service flag
	 */
	public void calcTotalAmount(TemplateSpecBean templateSpecBean,
			TplSpecAnswer specAnswer, boolean isItemTypeService) {
		if (null != specAnswer) {
			TplSpecQuestion specQuestion = specAnswer.getTplSpecQuestion();
			BigDecimal quantity = null;
			
			if (specAnswer.getTplSpecZoneQty() != null) {
				quantity = specAnswer.getTplSpecZoneQty().getQty();
			} else {
				quantity = specQuestion.getClQuantity();
			}
			
			BigDecimal totalPrice = TemplateSpecHelper.getInstance()
					.calcTotalAmount(specQuestion, specAnswer.getRatePerUom(),
							quantity);

			if (totalPrice != null && isItemTypeService) {
				TplSpecMaster specMaster = specQuestion.getTplSpecMaster();
				BigDecimal amountPerYear = BigDecimal.valueOf(totalPrice
						.doubleValue());
				BigDecimal itemDurationMonth = specQuestion
						.getClDurationMonth();

				if (itemDurationMonth != null) {
					amountPerYear = amountPerYear.divide(itemDurationMonth);
				} else {
					BigDecimal durationMonth = specMaster.getDurationMonth();

					if (durationMonth != null) {
						amountPerYear = amountPerYear.divide(durationMonth);
					}
				}

				specAnswer.setAmountPerYear(amountPerYear.multiply(BigDecimal
						.valueOf(12)));
			}

			specAnswer.setAnswerNumeric(totalPrice);
		}
		calcGrandTotalAmount(templateSpecBean);
	}
	
	public void deleteConfirmationAction(TemplateSpecBean templateSpecBean, TplSpecQuestion question) {
		debugLogger("deleteConfirmationAction");
		
		question.setSelected(true);
		templateSpecBean.setSelectedSpecQuestion(question);
		
		RequestContext.getCurrentInstance().execute(
				templateSpecBean.getSpecComponentId() + "delConfirmPopup.show()");
	}

	/**
	 * Tier Quantity Popup Action
	 * 
	 * @param templateSpecBean
	 *            the TemplateSpecBean instance
	 */
	public void tierQtyPopupAction(TemplateSpecBean templateSpecBean) {
		debugLogger("tierQtyPopupAction");

		TplSpecQuestion tplSpecQuestion = templateSpecBean
				.getSelectedSpecQuestion();
		TplSpecQuestion oriSpecQuestion = new TplSpecQuestion();

		List<TplSpecTierQty> oriSpecTierQties = TemplateSpecHelper
				.getInstance().cloneTplSpecTierQty(oriSpecQuestion,
						tplSpecQuestion.getTplSpecTierQties());

		if (oriSpecTierQties != null) {
			for (TplSpecTierQty oriSpecTierQty : oriSpecTierQties) {
				oriSpecTierQty.setTplSpecQuestion(tplSpecQuestion);
			}
		}

		templateSpecBean.setOriSpecQuestion(oriSpecQuestion);

		templateSpecBean.setSelectAll(false);
		RequestContext.getCurrentInstance().execute(
				templateSpecBean.getSpecComponentId() + "tierQtyPopup.show()");
	}
	
	/**
	 * Zone Quantity Popup Action
	 * 
	 * @param templateSpecBean
	 *            the TemplateSpecBean instance
	 */
	public void zoneQtyPopupAction(TemplateSpecBean templateSpecBean) {
		debugLogger("zoneQtyPopupAction");

		TplSpecMaster tplSpecMaster = templateSpecBean
				.getSpecMaster();
		TplSpecQuestion tplSpecQuestion = templateSpecBean
				.getSelectedSpecQuestion();
		List<TplSpecZoneQty> tplSpecZoneQties = tplSpecQuestion.getTplSpecZoneQties();
		TplSpecQuestion oriSpecQuestion = new TplSpecQuestion();
		
		List<TplSpecZoneDtl> specZoneDtls = getTplSpecZoneDtlsBySpecZone(templateSpecBean
				.getSpecMaster().getTplSpecZone());
		
		if (tplSpecMaster.getSpecZoneQtiesToDelete() == null) {
			tplSpecMaster.setSpecZoneQtiesToDelete(new ArrayList<TplSpecZoneQty>());
		}

		if (tplSpecZoneQties == null
				|| tplSpecZoneQties.size() == 0
				|| (specZoneDtls != null && tplSpecZoneQties.size() != specZoneDtls
						.size())
				|| !compareZoneDtlsWithZoneQty(specZoneDtls, tplSpecZoneQties)) {
			
			// add into specZoneQtiesToDelete list.
			if (tplSpecZoneQties != null && tplSpecZoneQties.size() > 0) {
				for (TplSpecZoneQty specZoneQty : tplSpecZoneQties) {
					if (!specZoneQty.isBrandNew()) {
						specZoneQty.setDeleted(true);	
					}
				}
				tplSpecMaster.getSpecZoneQtiesToDelete().addAll(tplSpecZoneQties);
			}

			tplSpecZoneQties = new ArrayList<TplSpecZoneQty>();

			for (int zoneRow = 0; zoneRow < (specZoneDtls == null
					|| specZoneDtls.size() == 0 ? 0 : specZoneDtls.size()); zoneRow++) {
				if (specZoneDtls != null && specZoneDtls.size() > 0) {
					TplSpecZoneQty specZoneQty = new TplSpecZoneQty();
					specZoneQty.setTplSpecQuestion(tplSpecQuestion);
					specZoneQty.setTplSpecZoneDtl(specZoneDtls.get(zoneRow));

					tplSpecZoneQties.add(specZoneQty);
				}
			}
			tplSpecQuestion.setTplSpecZoneQties(tplSpecZoneQties);
			
		} else {
			List<Long> zoneIds=new ArrayList<Long>();
			for(TplSpecZoneQty oriSpecZoneQty:tplSpecZoneQties){
				if(!zoneIds.contains(oriSpecZoneQty.getTplSpecZoneDtl().getSpecZoneId())){
					zoneIds.add(oriSpecZoneQty.getTplSpecZoneDtl().getSpecZoneId());
				}
			}
			if(null!=specZoneDtls && null!=tplSpecZoneQties){
			boolean isChange=false;
				for(TplSpecZoneDtl zoneDtl:specZoneDtls){
					if(!zoneIds.contains(zoneDtl.getSpecZoneId())){
						isChange=true;
					}
				}
				if(isChange){
				if(specZoneDtls.size()==tplSpecZoneQties.size()){
				for(int i=0;i<tplSpecZoneQties.size();i++){
					tplSpecZoneQties.get(i).setTplSpecZoneDtl(specZoneDtls.get(i));
				}
				}
				}
				}
			List<TplSpecZoneQty> oriSpecZoneQties = TemplateSpecHelper
					.getInstance().cloneTplSpecZoneQty(oriSpecQuestion,
							tplSpecZoneQties);

			if (oriSpecZoneQties != null) {
				for (TplSpecZoneQty oriSpecZoneQty : oriSpecZoneQties) {
					oriSpecZoneQty.setTplSpecQuestion(tplSpecQuestion);
				}
			}
		}

		templateSpecBean.setOriSpecQuestion(oriSpecQuestion);

		RequestContext.getCurrentInstance().execute(
				templateSpecBean.getSpecComponentId() + "zoneQtyPopup.show()");
	}
	
	private List<TplSpecZoneDtl> getTplSpecZoneDtlsBySpecZone(TplSpecZone specZone) {
		List<TplSpecZoneDtl> specZoneDtls = null;

		if (specZone != null) {
			if (specZone.isBrandNew() || specZone.isModified()) {
				specZoneDtls = specZone.getTplSpecZoneDtls();
			} else {
				ISpecZoneLocSO specZoneLocSO;
				try {
					specZoneLocSO = BusinessDelegate.getInstance()
							.getServiceObject(ModuleCodeConst.COMMON_COMPONENT,
									ISpecZoneLocSO.class);

					SpecZoneLocReqDTO reqDTO = new SpecZoneLocReqDTO();
					reqDTO.setDocType(specZone.getDocType());
					reqDTO.setDocId(specZone.getDocId());

					// Get zone details list from db.
					SpecZoneLocResDTO resDTO = specZoneLocSO
							.retrieveZoneDtlAndLocByDocIdNType(reqDTO);

					specZoneDtls = resDTO.getZonesList();
				} finally {
					specZoneLocSO = null;
				}
			}
		}
		
		return specZoneDtls;
	}

	/**
	 * Scoring Popup Action
	 * 
	 * @param templateSpecBean
	 *            the TemplateSpecBean instance
	 */
	public void scoringPopupAction(TemplateSpecBean templateSpecBean) {
		debugLogger("scoringPopupAction");

		TplSpecQuestion tplSpecQuestion = templateSpecBean
				.getSelectedSpecQuestion();

		if (tplSpecQuestion.getScoreTypeCode() == null) {
			tplSpecQuestion.setScoreTypeCode(new PmParameter());
		}

		// update selected score type id
		templateSpecBean.setSelectedScoreTypeId(tplSpecQuestion
				.getScoreTypeCode().getParameterId());

		try {
			TplSpecQuestion oriSpecQuestion = (TplSpecQuestion) BeanUtils
					.cloneBean(tplSpecQuestion);
			List<TplSpecScoring> oriSpecScorings = TemplateSpecHelper
					.getInstance().cloneTplSpecScoring(oriSpecQuestion,
							tplSpecQuestion.getTplSpecScorings());

			if (oriSpecScorings != null) {
				for (TplSpecScoring oriSpecScoring : oriSpecScorings) {
					oriSpecScoring.setTplSpecQuestion(tplSpecQuestion);
				}
			}

			templateSpecBean.setOriSpecQuestion(oriSpecQuestion);

		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
		}

		templateSpecBean.setSelectAll(false);
	}

	private void addComplianceScoringAction(TplSpecQuestion tplSpecQuestion) {

		if (tplSpecQuestion.getAnswerTypeCode() != null
				&& tplSpecQuestion.getScoreTypeCode() != null
				&& ParameterConst.SCORE_TYPE_AUTO.equals(tplSpecQuestion
						.getScoreTypeCode().getParameterCode())) {

			String answerType = tplSpecQuestion.getAnswerTypeCode()
					.getParameterCode();

			if (ParameterConst.ANSWER_TYPE_DROPDOWN.equals(answerType)) {
				List<TplSpecScoring> specScorings = tplSpecQuestion
						.getTplSpecScorings();

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

			} else if (ParameterConst.ANSWER_TYPE_NUMERIC.equals(answerType)
					&& ParameterConst.SPECIFICATION_TYPE_FINANCIAL
							.equals(tplSpecQuestion.getTplSpecMaster()
									.getSpecTypeCode().getParameterCode())) {

				List<TplSpecScoring> specScorings = tplSpecQuestion
						.getTplSpecScorings();

				if (specScorings == null || specScorings.size() == 0) {
					specScorings = new ArrayList<TplSpecScoring>();

					TplSpecScoring specScoring = new TplSpecScoring();
					specScoring.setTplSpecQuestion(tplSpecQuestion);
					specScorings.add(specScoring);

					tplSpecQuestion.setTplSpecScorings(specScorings);
				}
			}
		}

	}

	/**
	 * Estimation Popup Action
	 * 
	 * @param templateSpecBean
	 *            the TemplateSpecBean instance
	 */
	public void estimationPopupAction(TemplateSpecBean templateSpecBean) {
		debugLogger("estimationPopupAction");

		TplSpecQuestion tplSpecQuestion = templateSpecBean
				.getSelectedSpecQuestion();
		List<TplSpecEstimation> specEstimations = tplSpecQuestion
				.getTplSpecEstimations();
		TplSpecZone specZone = templateSpecBean.getSpecMaster()
				.getTplSpecZone();
		List<TplSpecZoneDtl> specZoneDtls = null;
		/*
		 * TplSpecQuestion oriSpecQuestion = new TplSpecQuestion();
		 * 
		 * List<TplSpecEstimation> oriSpecEstimations =
		 * TemplateSpecHelper.getInstance().cloneTplSpecEstimation(
		 * oriSpecQuestion, specEstimations);
		 * 
		 * if (oriSpecEstimations != null) { for (TplSpecEstimation
		 * oriSpecEstimation : oriSpecEstimations) {
		 * oriSpecEstimation.setTplSpecQuestion(tplSpecQuestion); } }
		 * 
		 * templateSpecBean.setOriSpecQuestion(oriSpecQuestion);
		 */

		if (specZone != null) {
			if (specZone.isBrandNew() || specZone.isModified()) {
				specZoneDtls = specZone.getTplSpecZoneDtls();
			} else {
				ISpecZoneLocSO specZoneLocSO;
				try {
					specZoneLocSO = BusinessDelegate.getInstance()
							.getServiceObject(ModuleCodeConst.COMMON_COMPONENT,
									ISpecZoneLocSO.class);

					SpecZoneLocReqDTO reqDTO = new SpecZoneLocReqDTO();
					reqDTO.setDocType(specZone.getDocType());
					reqDTO.setDocId(specZone.getDocId());

					// Get zone details list from db.
					SpecZoneLocResDTO resDTO = specZoneLocSO
							.retrieveZoneDtlAndLocByDocIdNType(reqDTO);

					specZoneDtls = resDTO.getZonesList();
				} finally {
					specZoneLocSO = null;
				}
			}
		}

		if (specEstimations == null
				|| specEstimations.size() == 0
				|| (specZoneDtls != null && specEstimations.size() != specZoneDtls
						.size())
				|| !compareZoneDtlsWithEstimations(specZoneDtls, specEstimations)) {

			specEstimations = new ArrayList<TplSpecEstimation>();

			for (int zoneRow = 0; zoneRow < (specZoneDtls == null
					|| specZoneDtls.size() == 0 ? 0 : specZoneDtls.size()); zoneRow++) {
				if (specZoneDtls != null && specZoneDtls.size() > 0) {
					TplSpecEstimation specEstimation = new TplSpecEstimation();
					specEstimation.setTplSpecQuestion(tplSpecQuestion);
					specEstimation.setTplSpecZoneDtl(specZoneDtls.get(zoneRow));

					specEstimations.add(specEstimation);
				}
			}
			tplSpecQuestion.setTplSpecEstimations(specEstimations);
		}
	}

	/**
	 * Zone Tier Price Popup Action
	 * 
	 * @param templateSpecBean
	 *            the TemplateSpecBean instance
	 */
	public void zoneTierPricePopupAction(TemplateSpecBean templateSpecBean) {
		debugLogger("zoneTierPricePopupAction");

		TplSpecQuestion specQuestion = templateSpecBean
				.getSelectedSpecQuestion();
		boolean isItemTypeService = ParameterConst.ITEM_TYPE_SERVICE
				.equals(specQuestion.getTplSpecMaster().getItemTypeCode().getParameterCode());
		if(CollectionUtils.isEmpty(specQuestion.getTplSpecAnswers())) {
			String priceType = specQuestion.getClPriceTypeCode()
					.getParameterCode();
			TplSpecZone specZone = specQuestion.getTplSpecMaster()
					.getTplSpecZone();
			TemplateSpecHelper specHelper = TemplateSpecHelper.getInstance();
			List<TplSpecAnswer> specAnswers = new ArrayList<TplSpecAnswer>();
			List<TplSpecZoneDtl> specZoneDtls = null;
			if (specZone != null) {

				if (specZone.isBrandNew() || specZone.isModified()) {
					specZoneDtls = specZone.getTplSpecZoneDtls();
				} else {
					ISpecZoneLocSO specZoneLocSO;
					try {
						specZoneLocSO = BusinessDelegate.getInstance()
								.getServiceObject(
										ModuleCodeConst.COMMON_COMPONENT,
										ISpecZoneLocSO.class);

						SpecZoneLocReqDTO reqDTO = new SpecZoneLocReqDTO();
						reqDTO.setDocType(specZone.getDocType());
						reqDTO.setDocId(specZone.getDocId());

						// Get zone details list from db.
						SpecZoneLocResDTO resDTO = specZoneLocSO
								.retrieveZoneDtlAndLocByDocIdNType(reqDTO);

						specZoneDtls = resDTO.getZonesList();
					} finally {
						specZoneLocSO = null;
					}
				}
				boolean isPriceTypeZonalStandard = specHelper
						.isPriceTypeZonalStandard(priceType);

				for (TplSpecZoneDtl specZoneDtl : specZoneDtls) {
					if (isPriceTypeZonalStandard) {
						TplSpecAnswer specAnswer = specHelper
								.createNewSpecAnswer(templateSpecBean,
										specQuestion, specZoneDtl, null);

						calcTotalAmount(templateSpecBean, specAnswer, isItemTypeService);
						specAnswers.add(specAnswer);
					} else {
						populateTierSpecAnswers(templateSpecBean, specQuestion,
								specZoneDtl, specAnswers);
					}

				}
			} else if (specHelper.isPriceTypeTier(priceType)) {
				populateTierSpecAnswers(templateSpecBean, specQuestion, null,
						specAnswers);
			}

			specQuestion.setTplSpecAnswers(specAnswers);
		}
			
	}

	/**
	 * Populate tier spec answers.
	 *
	 * @param templateSpecBean the template spec bean
	 * @param specQuestion the spec question
	 * @param specZoneDtl the spec zone dtl
	 * @param specAnswers the spec answers
	 */
	private void populateTierSpecAnswers(TemplateSpecBean templateSpecBean,
			TplSpecQuestion specQuestion, TplSpecZoneDtl specZoneDtl,
			List<TplSpecAnswer> specAnswers) {

		if(null == specQuestion.getTplSpecAnswers()){
			if (specQuestion.getTplSpecTierQties() != null) {
				TemplateSpecHelper specHelper = TemplateSpecHelper.getInstance();

				for (TplSpecTierQty specTierQty : specQuestion.getTplSpecTierQties()) {
					TplSpecAnswer specAnswer = specHelper.createNewSpecAnswer(
							templateSpecBean, specQuestion, specZoneDtl,
							specTierQty);
					specAnswers.add(specAnswer);
				}
			}
		} else{
			if(specAnswers.isEmpty()){
				specAnswers.addAll(specQuestion.getTplSpecAnswers());
			}
		}
	}

	/**
	 * Add new Tier Quantity action
	 * 
	 * @param tplSpecQuestion
	 *            the TplSpecQuestion instance
	 */
	public void addTierQtyAction(TplSpecQuestion tplSpecQuestion) {
		debugLogger("addTierQtyAction");

		TplSpecTierQty prevSpecTierQty = null;
		List<TplSpecTierQty> tplSpecTierQties = tplSpecQuestion
				.getTplSpecTierQties();

		if (tplSpecTierQties == null) {
			tplSpecTierQties = new ArrayList<TplSpecTierQty>();

			tplSpecQuestion.setTplSpecTierQties(tplSpecTierQties);
		}

		if (tplSpecTierQties.size() > 0) {
			prevSpecTierQty = tplSpecTierQties.get(tplSpecTierQties.size() - 1);
		}

		TplSpecTierQty tplSpecTierQty = new TplSpecTierQty();
		tplSpecTierQty.setTplSpecQuestion(tplSpecQuestion);
		if (prevSpecTierQty != null) {
			BigDecimal prevToQty = prevSpecTierQty.getToQty();
			if (prevToQty != null) {
				tplSpecTierQty.setFromQty(prevToQty.add(BigDecimal.valueOf(1)));
			}
		}
		tplSpecTierQties.add(tplSpecTierQty);

		tplSpecQuestion.setTplSpecEstimations(null);
	}

	/**
	 * Add new scoring action
	 * 
	 * @param tplSpecQuestion
	 *            the TplSpecQuestion instance
	 */
	public void addScoringAction(TplSpecQuestion tplSpecQuestion) {
		debugLogger("addScoringAction");

		TplSpecScoring prevSpecScoring = null;
		List<TplSpecScoring> tplSpecScorings = tplSpecQuestion
				.getTplSpecScorings();

		if (tplSpecScorings == null) {
			tplSpecScorings = new ArrayList<TplSpecScoring>();

			tplSpecQuestion.setTplSpecScorings(tplSpecScorings);
		}

		if (tplSpecScorings.size() > 0) {
			prevSpecScoring = tplSpecScorings.get(tplSpecScorings.size() - 1);
		}

		TplSpecScoring tplSpecScoring = new TplSpecScoring();
		tplSpecScoring.setTplSpecQuestion(tplSpecQuestion);
		if (prevSpecScoring != null) {
			BigDecimal prevRangeTo = prevSpecScoring.getRangeTo();
			if (prevRangeTo != null) {
				tplSpecScoring.setRangeFrom(prevRangeTo.add(BigDecimal
						.valueOf(0.01)));
			}
		}
		tplSpecScorings.add(tplSpecScoring);
	}

	/**
	 * Tier Quantity Popup Ok Action
	 * 
	 * @param templateSpecBean
	 *            the TemplateSpecBean instance
	 */
	public void tierQtyOkAction(TemplateSpecBean templateSpecBean) {
		debugLogger("tierQtyOkAction");

		ITemplateSpecSO templateSpecSO = null;
		TplSpecQuestion selectedSpecQuestion = templateSpecBean
				.getSelectedSpecQuestion();
		List<TplSpecTierQty> specTierQties = selectedSpecQuestion
				.getTplSpecTierQties();
		try {

			templateSpecSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.COMMON_COMPONENT, ITemplateSpecSO.class);

			templateSpecSO.validateTierQty(selectedSpecQuestion, specTierQties, new FacesScopes().getUserLocale());

			if (TemplateSpecConst.CONTENT_TYPE_QUESTION_AND_ASNWER
					.equals(templateSpecBean.getContentType())) {
				TemplateSpecHelper.getInstance().addToBeRemovedSpecAnswers(
						templateSpecBean,
						selectedSpecQuestion.getTplSpecAnswers());

				selectedSpecQuestion.setTplSpecAnswers(null);
			}

		} catch (EpException e) {
			String msgCode = e.getMessage();
			if (MessageConst.ERROR_700107.equalsIgnoreCase(msgCode)
					&& specTierQties.size() == 1) {
				throw new EpException(MessageConst.ERROR_760296,
						new String[] { "Tier Quantity" });
			}
			throw new EpException(msgCode, e.getParameters());
		} finally {
			templateSpecSO = null;
		}

		templateSpecBean.setSelectedSpecQuestion(null);

		RequestContext.getCurrentInstance().execute(
				templateSpecBean.getSpecComponentId() + "tierQtyPopup.hide()");
	}

	/**
	 * Tier Quantity Popup Ok Action
	 * 
	 * @param templateSpecBean
	 *            the TemplateSpecBean instance
	 */
	public void tierQtyCancelAction(TemplateSpecBean templateSpecBean) {
		debugLogger("tierQtyCancelAction");

		TplSpecQuestion oriSpecQuestion = templateSpecBean.getOriSpecQuestion();
		TplSpecQuestion selectedSpecQuestion = templateSpecBean
				.getSelectedSpecQuestion();

		selectedSpecQuestion.setTplSpecTierQties(oriSpecQuestion
				.getTplSpecTierQties());

		templateSpecBean.setSelectedSpecQuestion(null);
	}
	
	/**
	 * Zone Quantity Popup Ok Action
	 * 
	 * @param templateSpecBean
	 *            the TemplateSpecBean instance
	 */
	public void zoneQtyOkAction(TemplateSpecBean templateSpecBean) {
		debugLogger("zoneQtyOkAction");

		templateSpecBean.setSelectedSpecQuestion(null);

		RequestContext.getCurrentInstance().execute(
				templateSpecBean.getSpecComponentId() + "zoneQtyPopup.hide()");
	}

	/**
	 * Zone Quantity Popup Ok Action
	 * 
	 * @param templateSpecBean
	 *            the TemplateSpecBean instance
	 */
	public void zoneQtyCancelAction(TemplateSpecBean templateSpecBean) {
		debugLogger("zoneQtyCancelAction");

		TplSpecQuestion oriSpecQuestion = templateSpecBean.getOriSpecQuestion();
		TplSpecMaster tplSpecMaster = templateSpecBean
				.getSpecMaster();
		TplSpecQuestion selectedSpecQuestion = templateSpecBean
				.getSelectedSpecQuestion();
		List<TplSpecZoneQty> tplSpecZoneQties = selectedSpecQuestion.getTplSpecZoneQties();
		
		// add into specZoneQtiesToDelete list.
		if (tplSpecZoneQties != null && tplSpecZoneQties.size() > 0) {
			for (TplSpecZoneQty specZoneQty : tplSpecZoneQties) {
				if (!specZoneQty.isBrandNew()) {
					specZoneQty.setDeleted(true);	
				}
			}
			tplSpecMaster.getSpecZoneQtiesToDelete().addAll(tplSpecZoneQties);
		}
		
		selectedSpecQuestion.setTplSpecZoneQties(oriSpecQuestion.getTplSpecZoneQties());

		templateSpecBean.setSelectedSpecQuestion(null);
	}

	/**
	 * Scoring Popup Ok Action
	 * 
	 * @param templateSpecBean
	 *            the TemplateSpecBean instance
	 */
	public void scoringOkAction(TemplateSpecBean templateSpecBean) {
		debugLogger("scoringOkAction");

		ITemplateSpecSO templateSpecSO = null;
		try {
			TplSpecQuestion selectedSpecQuestion = templateSpecBean
					.getSelectedSpecQuestion();
			List<TplSpecScoring> specScorings = selectedSpecQuestion
					.getTplSpecScorings();

			templateSpecSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.COMMON_COMPONENT, ITemplateSpecSO.class);

			BigDecimal maxScore = templateSpecSO.validateScoring(
					selectedSpecQuestion, specScorings, new FacesScopes().getUserLocale());

			if (maxScore != null) {
				selectedSpecQuestion.setMaxScore(maxScore);
			}

		} finally {
			templateSpecSO = null;
		}

		templateSpecBean.setSelectedSpecQuestion(null);

		RequestContext.getCurrentInstance().execute(
				templateSpecBean.getSpecComponentId()
						+ "scoringSetPopup.hide()");
	}

	/**
	 * Scoring Popup Cancel Action
	 * 
	 * @param templateSpecBean
	 *            the TemplateSpecBean instance
	 */
	public void scoringCancelAction(TemplateSpecBean templateSpecBean) {
		debugLogger("scoringCancelAction");

		TplSpecQuestion oriSpecQuestion = templateSpecBean.getOriSpecQuestion();
		TplSpecQuestion selectedSpecQuestion = templateSpecBean
				.getSelectedSpecQuestion();

		if (oriSpecQuestion.getScoreTypeCode() != null
				&& oriSpecQuestion.getScoreTypeCode().getParameterId() != null) {
			selectedSpecQuestion.setScoreTypeCode(oriSpecQuestion
					.getScoreTypeCode());
		} else {
			selectedSpecQuestion.setScoreTypeCode(null);
		}

		selectedSpecQuestion.setMaxScore(oriSpecQuestion.getMaxScore());
		selectedSpecQuestion.setWeightageReq(oriSpecQuestion.isWeightageReq());
		selectedSpecQuestion.setWeightage(oriSpecQuestion.getWeightage());
		selectedSpecQuestion.setTplSpecScorings(oriSpecQuestion
				.getTplSpecScorings());

		templateSpecBean.setSelectedSpecQuestion(null);
	}

	/**
	 * Estimation Cost Popup Ok Action
	 * 
	 * @param templateSpecBean
	 *            the TemplateSpecBean instance
	 */
	public void estCostOkAction(TemplateSpecBean templateSpecBean) {
		debugLogger("estCostOkAction");

		templateSpecBean.setSelectedSpecQuestion(null);

		RequestContext.getCurrentInstance().execute(
				templateSpecBean.getSpecComponentId()
						+ "estimationPopup.hide()");
	}

	/**
	 * Estimation Popup Cancel Action
	 * 
	 * @param templateSpecBean
	 *            the TemplateSpecBean instance
	 */
	public void estCostCancelAction(TemplateSpecBean templateSpecBean) {
		debugLogger("estCostCancelAction");

		/*
		 * TplSpecQuestion oriSpecQuestion =
		 * templateSpecBean.getOriSpecQuestion(); TplSpecQuestion
		 * selectedSpecQuestion = templateSpecBean.getSelectedSpecQuestion();
		 * 
		 * selectedSpecQuestion.setTplSpecEstimations(oriSpecQuestion.
		 * getTplSpecEstimations());
		 */

		templateSpecBean.setSelectedSpecQuestion(null);
	}

	/**
	 * Tier Quantity Popup Ok Action
	 * 
	 * @param templateSpecBean
	 *            the TemplateSpecBean instance
	 */
	public void zoneTierPriceOkAction(TemplateSpecBean templateSpecBean) {
		debugLogger("zoneTierPriceOkAction");

		templateSpecBean.setSelectedSpecQuestion(null);

		RequestContext.getCurrentInstance().execute(
				templateSpecBean.getSpecComponentId()
						+ "zoneTierPricePopup.hide()");
	}

	/**
	 * Tier Quantity Popup Ok Action
	 * 
	 * @param templateSpecBean
	 *            the TemplateSpecBean instance
	 */
	public void zoneTierPriceCancelAction(TemplateSpecBean templateSpecBean) {
		debugLogger("zoneTierPriceCancelAction");

		templateSpecBean.setSelectedSpecQuestion(null);
	}

	/**
	 * Calculate Grand Total Estimation Amount
	 * 
	 * @param templateSpecBean
	 *            the TemplateSpecBean instance
	 * @return the grand total estimation amount
	 */
	public BigDecimal calcGrandTotalEstAmount(TemplateSpecBean templateSpecBean) {
		BigDecimal totalEstAmount = new BigDecimal(0);
		TplSpecMaster specMaster = templateSpecBean.getSpecMaster();
		TplSpecZoneDtl specZoneDtl = templateSpecBean.getTplSpecZoneDtl();
		List<TplSpecEstimation> specEstimations = new ArrayList<TplSpecEstimation>();

		if (TemplateSpecConst.CONTENT_TYPE_SCORE.equals(templateSpecBean
				.getContentType())) {
			List<Long> specQuestionIds = new ArrayList<Long>();

			for (TplSpecAnswer specAnswer : templateSpecBean
					.getSpecAnswerList()) {
				TplSpecQuestion specQuestion = specAnswer.getTplSpecQuestion();

				if (!specQuestionIds.contains(specQuestion.getSpecQuestionId())
						&& specQuestion.getTplSpecEstimations() != null) {

					if (specZoneDtl != null) {
						for (TplSpecEstimation specEstimation : specQuestion
								.getTplSpecEstimations()) {
							if (specZoneDtl.getSpecZoneId().equals(
									specEstimation.getTplSpecZoneDtl()
											.getSpecZoneId())) {
								specEstimations.add(specEstimation);
								break;
							}
						}

					} else {
						specEstimations.addAll(specQuestion
								.getTplSpecEstimations());
					}

					specQuestionIds.add(specQuestion.getSpecQuestionId());
				}
			}
		} else {
			for (TplSpecQuestion specQuestion : specMaster
					.getTplSpecQuestions()) {
				if (specQuestion.getTplSpecEstimations() != null) {
					specEstimations
							.addAll(specQuestion.getTplSpecEstimations());
				}
			}
		}

		for (TplSpecEstimation specEstimation : specEstimations) {
			if (specEstimation.getEstimatedCost() != null) {
				totalEstAmount = totalEstAmount.add(specEstimation
						.getEstimatedCost());
			}
		}

		specMaster.setTotalEstAmount(totalEstAmount);

		return totalEstAmount;
	}

	/**
	 * Calculate the Grand Total Amount
	 * 
	 * @param templateSpecBean
	 *            the TemplateSpecBean instance
	 * @return the grand total amount
	 */
	public BigDecimal calcGrandTotalAmount(TemplateSpecBean templateSpecBean) {
		BigDecimal totalAmount = new BigDecimal(0);
		BigDecimal totalAmountPerYear = new BigDecimal(0);
		Long tierSpecQuestionId = null;
		TplSpecMaster specMaster = templateSpecBean.getSpecMaster();
		TemplateSpecHelper templateSpecHelper = TemplateSpecHelper
				.getInstance();
		List<TplSpecAnswer> specAnswerList = null;

		if (TemplateSpecConst.CONTENT_TYPE_QUESTION_AND_ASNWER
				.equals(templateSpecBean.getContentType())) {
			specAnswerList = new ArrayList<TplSpecAnswer>();

			if (specMaster.getTplSpecQuestions() != null) {
				for (TplSpecQuestion specQuestion : specMaster
						.getTplSpecQuestions()) {
					if (specQuestion.getTplSpecAnswers() != null) {
						specAnswerList.addAll(specQuestion.getTplSpecAnswers());
					}
				}
			}

		} else {
			specAnswerList = templateSpecBean.getSpecAnswerList();
		}

		if (specAnswerList != null) {
			for (TplSpecAnswer tplSpecAnswer : specAnswerList) {
				TplSpecQuestion specQuestion = tplSpecAnswer
						.getTplSpecQuestion();
				String priceType = specQuestion.getClPriceTypeCode()
						.getParameterCode();
				BigDecimal quantity = null;
				
				if (tplSpecAnswer.getTplSpecZoneQty() != null) {
					quantity = tplSpecAnswer.getTplSpecZoneQty().getQty();
				} else {
					quantity = specQuestion.getClQuantity();
				}

				if (templateSpecHelper.isPriceTypeTariff(priceType)) {
					// TODO - calculate tariff price.

				} else if (templateSpecHelper.isPriceTypeTier(priceType)
						&& (tierSpecQuestionId == null || tierSpecQuestionId
								.compareTo(specQuestion.getSpecQuestionId()) != 0)) {
					// Always used the 1st tier quantity unit price to calculate
					// total amount.
					if (tplSpecAnswer.getRatePerUom() != null && quantity != null) {
						totalAmount = totalAmount.add(quantity
								.multiply(tplSpecAnswer.getRatePerUom()));
					}

					tierSpecQuestionId = specQuestion.getSpecQuestionId();

				} else if (templateSpecHelper.isPriceTypeStandard(priceType)
						|| templateSpecHelper
								.isPriceTypeZonalStandard(priceType)) {

					if (tplSpecAnswer.getAnswerNumeric() != null) {
						totalAmount = totalAmount.add(tplSpecAnswer
								.getAnswerNumeric());
					}
				}

				if (tplSpecAnswer.getAmountPerYear() != null) {
					totalAmountPerYear = totalAmountPerYear.add(tplSpecAnswer
							.getAmountPerYear());
				}
			}
		}

		specMaster.setTotalAmountPerYear(totalAmountPerYear);
		specMaster.setTotalAmount(totalAmount);

		return totalAmount;
	}

	/**
	 * Calculate the total amount and amount per year for each item.
	 * 
	 * @param templateSpecBean
	 *            the TemplateSpecBean instance
	 * @return the total score
	 */
	public String calcTotalScore(TemplateSpecBean templateSpecBean) {
		String totalScoreStr = null;
		List<TplSpecAnswer> specAnswerList = templateSpecBean
				.getSpecAnswerList();

		if (specAnswerList != null && specAnswerList.size() > 0) {
			BigDecimal totalScore = BigDecimal.valueOf(0);
			BigDecimal totalMaxScore = BigDecimal.valueOf(0);

			for (TplSpecAnswer specAnswer : specAnswerList) {
				BigDecimal autoScore = specAnswer.getAutoScore();
				BigDecimal userScore = specAnswer.getUserScore();

				if (autoScore != null) {
					totalScore = totalScore.add(autoScore);
				}

				if (userScore != null) {
					totalScore = totalScore.add(userScore);
				}

				if (specAnswer.getTplSpecQuestion().getMaxScore() != null
						&& (autoScore != null || userScore != null)) {
					totalMaxScore = totalMaxScore.add(specAnswer
							.getTplSpecQuestion().getMaxScore());
				}
			}

			totalScoreStr = totalScore.toString() + " / "
					+ totalMaxScore.toString();
		}

		return totalScoreStr;
	}

	/**
	 * Check all items price type is standard
	 * 
	 * @param templateSpecBean
	 *            the TemplateSpecBean instance
	 * @return the indicator all items price type is standard
	 */
	public boolean isAllPriceTypesStandard(TemplateSpecBean templateSpecBean) {
		boolean isAllPriceTypesStandard = true;
		String contentType = templateSpecBean.getContentType();
		TplSpecMaster specMaster = templateSpecBean.getSpecMaster();
		boolean isItemTypeService = ParameterConst.ITEM_TYPE_SERVICE
				.equals(specMaster.getItemTypeCode().getParameterCode());
		List<TplSpecQuestion> specQuestions = null;

		if (TemplateSpecConst.CONTENT_TYPE_QUESTION.equals(contentType)
				|| TemplateSpecConst.CONTENT_TYPE_REVIEW.equals(contentType)) {
			specQuestions = templateSpecBean.getSpecMaster()
					.getTplSpecQuestions();
		} else {
			List<TplSpecAnswer> specAnswers = templateSpecBean
					.getSpecAnswerList();

			if (specAnswers != null) {
				specQuestions = new ArrayList<TplSpecQuestion>();

				for (TplSpecAnswer specAnswer : specAnswers) {
					TplSpecQuestion specQuestion = specAnswer
							.getTplSpecQuestion();

					specQuestions.add(specQuestion);
				}
			}
		}

		if (specQuestions != null) {
			TemplateSpecHelper templateSpecHelper = TemplateSpecHelper
					.getInstance();
			boolean specificationReq = specMaster.isSpecificationReq();

			for (TplSpecQuestion specQuestion : specQuestions) {
				if (TemplateSpecConst.QUESTION_TYPE_ITEM.equals(specQuestion
						.getQuestionType())) {
					String priceType = specQuestion.getClPriceTypeCode()
							.getParameterCode();

					if (!templateSpecHelper.isPriceTypeStandard(priceType)
							&& ((!specificationReq && !isItemTypeService) || (specificationReq && !templateSpecHelper
									.isPriceTypeZonalStandard(priceType)))) {
						isAllPriceTypesStandard = false;

						break;
					}
				}
			}
		}

		return isAllPriceTypesStandard;
	}

	/**
	 * Check any weightage required
	 * 
	 * @param templateSpecBean
	 *            the TemplateSpecBean instance
	 * @return the weightage required indicator
	 */
	public boolean hasWeightage(TemplateSpecBean templateSpecBean) {
		boolean hasWeightage = false;

		for (TplSpecAnswer specAnswer : templateSpecBean.getSpecAnswerList()) {
			TplSpecQuestion specQuestion = specAnswer.getTplSpecQuestion();

			if (specQuestion.isWeightageReq()) {
				hasWeightage = true;

				break;
			}
		}

		return hasWeightage;
	}

	/**
	 * Update Item Weightage Required
	 * 
	 * @param templateSpecBean
	 *            the TemplateSpecBean instance
	 */
	public void updateItemWeightageReq(TemplateSpecBean templateSpecBean) {
		TplSpecMaster specMaster = templateSpecBean.getSpecMaster();

		TemplateSpecHelper.getInstance().updateItemWeightageReq(specMaster,
				specMaster.getTplSpecQuestions());
	}

	/**
	 * Get record status dropdown list
	 * 
	 * @return the list of SelectItem instance
	 */
	public SelectItem[] getStatusOptions() {

		return PmCommonUtil.getStatusOptionsDefault();
	}

	/**
	 * Specification Master Paging Data Model
	 * 
	 * @author kenny
	 * @since 19.03.2015
	 */
	private class SpecMasterPagingDataModel extends
			PagingDataModel<TplSpecMaster> {

		private static final long serialVersionUID = 1L;

		/**
		 * Constructor for Specification Master Paging Data Model
		 */
		public SpecMasterPagingDataModel() {
			super("tplSpecMasterTbl",false);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * my.ep.web.bean.paginator.PagingDataModel#pageChangeListener(my.ep
		 * .biz.bean.search.ISearchBean)
		 */
		@Override
		public ISearchResultBean<TplSpecMaster> pageChangeListener(
				ISearchBean<TplSpecMaster> searchBean) {
			if (log.isDebugEnabled()) {
				log.debug("pageChangeListener");
			}

			ITemplateSpecSO templateSpecSO;
			try {
				templateSpecSO = BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.COMMON_COMPONENT,
								ITemplateSpecSO.class);

				searchBean.getDataFilterBeans().add(
						new SearchFilterBean("processType",
								SearchOperator.OPERATOR_EQUALS.value(),
								TemplateSpecConst.PROCESS_TYPE_STANDARD));

				if (getManagedBean().getSearchProcurementCategoryId() != null) {
					searchBean.getDataFilterBeans().add(
							new SearchFilterBean(
									"procurementCategory.parameterId",
									SearchOperator.OPERATOR_EQUALS.value(),
									getManagedBean()
											.getSearchProcurementCategoryId()));
				}
				searchBeanForTemplateTypeCode(searchBean);
				if (StringUtils.isNotBlank(getManagedBean().getSearchTemplateTypeCode())) {
					String searchTemplateTypeCode = getManagedBean().getSearchTemplateTypeCode();
					
					searchBean
							.getDataFilterBeans()
							.add(new SearchFilterBean(
									"templateTypeCode.parameterCode",
									SearchOperator.OPERATOR_EQUALS.value(),
									searchTemplateTypeCode));
					
					if (ParameterConst.TEMPLATE_TYPE_MINISTRY.equals(searchTemplateTypeCode) ||
							ParameterConst.TEMPLATE_TYPE_PTJ.equals(searchTemplateTypeCode)) {
						if (getManagedBean().getSearchOrgProfile() != null && getManagedBean().getSearchOrgProfile().getOrgProfileId() != null) {
							searchBean.getDataFilterBeans().add(
									new SearchFilterBean("orgProfile.orgProfileId",
											SearchOperator.OPERATOR_EQUALS.value(),
											getManagedBean().getSearchOrgProfile().getOrgProfileId()));	
						}
						
					}
				}

				if (getManagedBean().getSearchItemTypeId() != null) {
					searchBean.getDataFilterBeans().add(
							new SearchFilterBean("itemTypeCode.parameterId",
									SearchOperator.OPERATOR_EQUALS.value(),
									getManagedBean().getSearchItemTypeId()));
				}

				if (getManagedBean().getSearchSpecTypeId() != null) {
					searchBean.getDataFilterBeans().add(
							new SearchFilterBean("specTypeCode.parameterId",
									SearchOperator.OPERATOR_EQUALS.value(),
									getManagedBean().getSearchSpecTypeId()));
				}

				if (StringUtils.isNotBlank(getManagedBean().getSearchSpecName())) {
					searchBean.getDataFilterBeans().add(
							new SearchFilterBean("specName",
									SearchOperator.OPERATOR_LIKE.value(),
									getManagedBean().getSearchSpecName(),
									true));
				}
				
				if (StringUtils.isNotBlank(getManagedBean().getSearchSpecDesc())) {
					searchBean.getDataFilterBeans().add(
							new SearchFilterBean("specDesc",
									SearchOperator.OPERATOR_LIKE.value(),
									getManagedBean().getSearchSpecDesc(),
									true));
				}
				
				if (StringUtils.isNotBlank(getManagedBean().getSearchTemplateTypeUser())) {
					searchBean.getDataFilterBeans().add(
							new SearchFilterBean("createdUser.userName",
									SearchOperator.OPERATOR_LIKE.value(),
									getManagedBean().getSearchTemplateTypeUser(),
									true));	
				}

				searchBean.getDataFilterBeans().add(
						new SearchFilterBean("specName",
								SearchOperator.OPERATOR_NOT_EQUALS.value(),
								TemplateSpecConst.TEMPLATE_SPEC_BLANK));
				
				// Removed the restriction condition to get All spec's across org profile.
				if (CollectionUtils.isNotEmpty(searchBean.getDataFilterMetadatas())) {
					searchBean.getDataFilterMetadatas().clear();
				}

				TemplateSpecReqDTO reqDTO = new TemplateSpecReqDTO();
				reqDTO.setSearchBean(searchBean);
				TemplateSpecResDTO resDTO = templateSpecSO
						.searchTemplateMaster(reqDTO);
				if (resDTO.isSuccessful()) {
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
	 * @param specMasterDataModel
	 *            the specMasterDataModel to set
	 */
	public void setSpecMasterDataModel(
			IPagingDataModel<TplSpecMaster> specMasterDataModel) {
		this.specMasterDataModel = specMasterDataModel;
	}

	public int getNumOfRows(String text) {

		int rowCount = 0;
		if (!StringUtils.isEmpty(text)) {
			double lenght = text.length();
			rowCount = (int) Math.round(lenght / 60);
		}
		if (rowCount == 0) {
			rowCount = 1;
		} else if (rowCount > 5) {
			rowCount = 5;// max rows limit shuld be 5. if morthan 5 then display
							// scroll bar
		}
		return rowCount;
	}

	public void reset() {

		this.getManagedBean().setSearchSpecName(null);
		this.getManagedBean().setSearchSpecDesc(null);
		this.getManagedBean().setSearchOrgProfile(new PmOrgProfile());
		this.getManagedBean().setSearchTemplateTypeCode(null);
		this.getManagedBean().setSearchTemplateTypeUser(null);
		this.getManagedBean().setSearchSpecTypeId(null);
		this.getManagedBean().setSearchItemTypeId(null);
		this.getManagedBean().setSearchProcurementCategoryId(null);

	}

	private boolean compareZoneDtlsWithEstimations(List<TplSpecZoneDtl> specZoneDtls,
			List<TplSpecEstimation> specEstimations) {
		List<String> zoneNames = new ArrayList<String>();
		for (TplSpecEstimation specEstimation : specEstimations) {
			if (specEstimation.getTplSpecZoneDtl() != null) {
				zoneNames.add(specEstimation.getTplSpecZoneDtl().getZoneName());	
			}
		}

		return compareZoneDtlsWithZoneNameList(specZoneDtls, zoneNames);
	}
	
	private boolean compareZoneDtlsWithZoneQty(List<TplSpecZoneDtl> specZoneDtls,
			List<TplSpecZoneQty> specZoneQties) {
		List<String> zoneNames = new ArrayList<String>();
		for (TplSpecZoneQty specZoneQty : specZoneQties) {
			if (specZoneQty.getTplSpecZoneDtl() != null) {
				zoneNames.add(specZoneQty.getTplSpecZoneDtl().getZoneName());	
			}
		}

		return compareZoneDtlsWithZoneNameList(specZoneDtls, zoneNames);
	}
	
	private boolean compareZoneDtlsWithZoneNameList(List<TplSpecZoneDtl> specZoneDtls,
			List<String> zoneNameList) {
		
		if (null != specZoneDtls && null != zoneNameList) {
			for (TplSpecZoneDtl zoneDtls : specZoneDtls) {				
				if (!zoneNameList.contains(zoneDtls.getZoneName())) {
					return false;
				}
			}
		}
		
		return true;
	}
	
	/**
	 * To set user Details.
	 * 
	 * @param baseObject
	 */
	private void setUserDetails(BaseEntity baseObject) {
		PmUser user = new FacesScopes().getUserSessionBean();

		if (baseObject.isBrandNew()) {
			baseObject.setCreatedBy(user.getUserId());
			baseObject
					.setCreatedDate(new Timestamp(System.currentTimeMillis()));
		}
		baseObject.setChangedBy(user.getUserId());
	}
	
	public boolean isSpecCreatedByLoginUserPtj(TplSpecMaster specMaster) {

		boolean isYes = true;
		if (this.getManagedBean().getOrgProfileOfLoginUser() == null) {
			this.getManagedBean().setOrgProfileOfLoginUser(retrieveUserOrgListByUserId());
		}
		if(specMaster != null && specMaster.getOwnerOrgProfile() != null){
			if (!specMaster.getOwnerOrgProfile().getOrgProfileId()
					.equals(this.getManagedBean().getOrgProfileOfLoginUser().getOrgProfileId())) {
				isYes = false;
			}
		}
		
		return isYes;
	}
	
	public void cloneAction(TemplateSpecBean templateSpecBean) {

		debugLogger("cloneAction");
		TplSpecMaster specMaster = templateSpecBean.getSpecMaster();
		TplSpecMaster clonedSpecMaster = TemplateSpecHelper.getInstance()
				.cloneTplSpecMaster(specMaster, true, true, false);
		if (this.getManagedBean().getOrgProfileOfLoginUser() == null) {
			this.getManagedBean().setOrgProfileOfLoginUser(retrieveUserOrgListByUserId());
		}
		clonedSpecMaster.setOwnerOrgProfile(this.getManagedBean().getOrgProfileOfLoginUser());
		clonedSpecMaster.setCreatedUser(new FacesScopes().getUserSessionBean());
		this.getManagedBean().setReadOnly(false);
		this.getManagedBean().setCloneBtnRendered(false);
		templateSpecBean.setSpecMaster(clonedSpecMaster);
		FacesContext.getCurrentInstance().addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_INFO, null,
						MessageUtils.getMessage(MessageConst.ERROR_760150,
								new String[] {ResourceBundleUtils.getMessage(
										TPL_SPEC_KEY, 
										ResourceBundleUtils.BUNDLE_COMMON_LABELS,
										new FacesScopes().getUserLocale())},
								MessageUtils.BUNDLE_COMMON_MESSAGES,
								new FacesScopes().getUserLocale())));
		
	}
	
	private PmOrgProfile retrieveUserOrgListByUserId() {

		IUserSO userSO = null;
		PmOrgProfile pmOrgProfile = null;
		try {
			userSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
			UserReqDTO reqDTO = new UserReqDTO();
			reqDTO.setUserId(new FacesScopes().getUserSessionBean().getUserId());
			UserResDTO resDTO = userSO.retrieveUserOrgListByUserId(reqDTO);
			if (resDTO.getUserOrgList() != null
					&& resDTO.getUserOrgList().size() > 0) {
				pmOrgProfile = resDTO.getUserOrgList().get(0).getPmOrgProfile();
			}
		} finally {
			userSO = null;
		}
		return pmOrgProfile;
	}
	
	public String retrieveTemplateSpecMaster(TemplateSpecBean templateSpecBean,
			boolean isReadOnly) {

		this.getManagedBean().setReadOnly(isReadOnly);
		this.getManagedBean().setCloneBtnRendered(true);
		return retrieveTemplateSpecMaster(templateSpecBean);
	}
	
	/**
	 * 
	 * @param searchBean
	 */
	private void searchBeanForTemplateTypeCode(ISearchBean<TplSpecMaster> searchBean){
		
		// Filter By - ministry orgProfileId
		List<String> templeteTypes = new ArrayList<String>();
		templeteTypes.add(ParameterConst.TEMPLATE_TYPE_MINISTRY);
		templeteTypes.add(ParameterConst.TEMPLATE_TYPE_PTJ);
		PmOrgProfile ptjByloginUser = TemplateSpecHelper.getInstance().getPTJOrgProfileByLoginUser();
		List<Long> orgProfileIds = getMinistryPTJOrgIds(ptjByloginUser);
		List<Long> ownerOrgProfileIds = getMinistryPTJOrgIds(ptjByloginUser);

		if (ownerOrgProfileIds != null
				&& ownerOrgProfileIds.size() > 0) {
			orgProfileIds.addAll(ownerOrgProfileIds);
		}

		SearchMultiFilterBean orgFilterBean = new SearchMultiFilterBean(
				new SearchFilterBean(
						"templateTypeCode.parameterCode",
						SearchOperator.OPERATOR_IN.value(),
						templeteTypes), new SearchFilterBean(
						"orgProfile.orgProfileId",
						SearchOperator.OPERATOR_IN.value(),orgProfileIds));

		SearchMultiFilterBean ownFilterBean = new SearchMultiFilterBean(
				new SearchFilterBean(
						"templateTypeCode.parameterCode",
						SearchOperator.OPERATOR_EQUALS.value(),
						ParameterConst.TEMPLATE_TYPE_OWN),
				SearchCondition.CONDITION_OR.value(),
				new SearchFilterBean("createdBy",
						SearchOperator.OPERATOR_EQUALS.value(),
						new FacesScopes().getUserSessionBean().getUserId()));

		BaseSearchFilterBean allFilterBean = new SearchMultiFilterBean(
				new SearchFilterBean(
						"templateTypeCode.parameterCode",
						SearchOperator.OPERATOR_EQUALS.value(),
						ParameterConst.TEMPLATE_TYPE_ALL),
				SearchCondition.CONDITION_OR.value(),
				new SearchFilterBean("orgProfile.orgProfileId",
						SearchOperator.OPERATOR_IS_NULL.value(),
						null));
		SearchFilterBean ownerOrgProfile = new SearchFilterBean();
		ownerOrgProfile.setAndOr(SearchCondition.CONDITION_OR.value());
		ownerOrgProfile.setSearchField("ownerOrgProfile.orgProfileId");
		ownerOrgProfile.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
		ownerOrgProfile.setSearchValue(ptjByloginUser.getOrgProfileId());
		searchBean.getDataFilterBeans().add(
				new SearchMultiFilterBean(new SearchMultiFilterBean(
						orgFilterBean, ownFilterBean), allFilterBean));				
		searchBean.getDataFilterBeans().add(ownerOrgProfile);				
	}
	
	private List<Long> getMinistryPTJOrgIds(PmOrgProfile pmOrgProfile) {
		List<Long> orgProfileIds = new ArrayList<Long>();

		if (pmOrgProfile != null) {
			IOrgProfileSO orgProfileSO = null;
			try {
				orgProfileSO = BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
								IOrgProfileSO.class);

				Long orgProfileId = pmOrgProfile.getOrgProfileId();

				orgProfileIds.add(orgProfileId);

				OrgProfileReqDTO orgProfileReqDTO = new OrgProfileReqDTO();
				orgProfileReqDTO.setOrgProfileId(orgProfileId);
				PmOrgProfile ministryOrgProfile = orgProfileSO
						.retrieveMinistryByOrgProfileId(orgProfileReqDTO)
						.getPmOrgProfile();
				if (ministryOrgProfile != null) {
					orgProfileIds.add(ministryOrgProfile.getOrgProfileId());
				}
			} finally {
				orgProfileSO = null;
			}
		}

		return orgProfileIds;
	}
	
	public String replaceWithBR(String specDetails) {
		
		return specDetails.replaceAll(TemplateSpecConst.NEW_LINE_CHARACTER,
				TemplateSpecConst.HTML_NEW_LINE_CHARACTER);
	}
	
}
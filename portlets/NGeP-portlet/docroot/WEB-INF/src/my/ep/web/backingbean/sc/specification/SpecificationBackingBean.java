/* 
 ***************************************************   
 * Product nextgen
 *                ePerolehan 
 * Release 1.00    
 *    
 * Copyright © 2013 Archisoft. All rights reserved.
 * 
 ****************************************************
 */
package my.ep.web.backingbean.sc.specification;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchBetweenValue;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.bean.search.SearchMultiFilterBean;
import my.ep.biz.service.ct.circular.ICircularDetailSO;
import my.ep.biz.service.ct.circular.ICircularSO;
import my.ep.biz.service.ct.supplier.IPanelSupplierSO;
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.address.IAddressSO;
import my.ep.biz.service.pm.common.country.ICountrySO;
import my.ep.biz.service.pm.common.district.IDistrictSO;
import my.ep.biz.service.pm.common.document.IDocumentSO;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.pm.common.state.IStateSO;
import my.ep.biz.service.pm.general.settings.rule.IRuleSO;
import my.ep.biz.service.sc.committe.ICommitteSO;
import my.ep.biz.service.sc.quationtender.IQTDetailSO;
import my.ep.biz.service.sc.quationtender.IQTSpecSO;
import my.ep.biz.service.sc.quationtender.IQTSupplierSO;
import my.ep.biz.service.sm.mofAccount.IMofAccountSO;
import my.ep.biz.service.tpl.spec.ITemplateSpecSO;
import my.ep.constant.BpmOutcomeConst;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.pm.RuleTypeConst;
import my.ep.constant.sc.QTStatusConst;
import my.ep.constant.sc.SourcingConstants;
import my.ep.constant.tpl.TemplateSpecConst;
import my.ep.constants.CommonConstants;
import my.ep.constants.SCProcessNameConst;
import my.ep.dto.ct.circular.CircularDetailReqDTO;
import my.ep.dto.ct.circular.CircularDetailResDTO;
import my.ep.dto.ct.circular.CircularReqDTO;
import my.ep.dto.ct.circular.CircularResDTO;
import my.ep.dto.ct.supplier.PanelSupplierReqDTO;
import my.ep.dto.ct.supplier.PanelSupplierResDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.common.address.AddressReqDTO;
import my.ep.dto.pm.common.address.AddressResDTO;
import my.ep.dto.pm.common.country.CountryReqDTO;
import my.ep.dto.pm.common.country.CountryResDTO;
import my.ep.dto.pm.common.district.DistrictReqDTO;
import my.ep.dto.pm.common.district.DistrictResDTO;
import my.ep.dto.pm.common.document.DocumentReqDTO;
import my.ep.dto.pm.common.document.DocumentResDTO;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
import my.ep.dto.pm.common.state.StateReqDTO;
import my.ep.dto.pm.common.state.StateResDTO;
import my.ep.dto.pm.general.settings.rule.RuleReqDTO;
import my.ep.dto.sc.committe.CommitteReqDTO;
import my.ep.dto.sc.committe.CommitteResDTO;
import my.ep.dto.sc.quationtender.QTSupplierReqDTO;
import my.ep.dto.sc.quationtender.QTSupplierResDTO;
import my.ep.dto.sc.quationtender.QuationTenderReqDTO;
import my.ep.dto.sc.quationtender.QuationTenderResDTO;
import my.ep.dto.sc.quationtender.SpecificationReqDTO;
import my.ep.dto.sc.quationtender.SpecificationResDTO;
import my.ep.dto.sm.mofAccount.MofAccountReqDTO;
import my.ep.dto.sm.mofAccount.MofAccountResDTO;
import my.ep.dto.tpl.spec.TemplateSpecReqDTO;
import my.ep.dto.tpl.spec.TemplateSpecResDTO;
import my.ep.exception.EpException;
import my.ep.jaxb.sc.quotationtenderdata.SCQuotationTenderDataType;
import my.ep.persistence.entity.ct.circular.CtCircularVer;
import my.ep.persistence.entity.ct.circular.panel.CtPanelItem;
import my.ep.persistence.entity.ct.circular.panel.CtPanelItemSpec;
import my.ep.persistence.entity.ct.circular.panel.CtPanelSupplier;
import my.ep.persistence.entity.ct.circular.panel.CtPanelZone;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmAddressType;
import my.ep.persistence.entity.pm.common.PmCategoryL2;
import my.ep.persistence.entity.pm.common.PmCategoryL3;
import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmDigiSign;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmDivision;
import my.ep.persistence.entity.pm.common.PmDocument;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.pm.general.settings.rule.PmConfigRuleValue;
import my.ep.persistence.entity.sc.common.ScRemark;
import my.ep.persistence.entity.sc.quationtender.ScCommitteeMember;
import my.ep.persistence.entity.sc.quationtender.ScFinancialUserScoringSchemaAttachment;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtCategory;
import my.ep.persistence.entity.sc.quationtender.ScQtCheckList;
import my.ep.persistence.entity.sc.quationtender.ScQtCheckListAttachment;
import my.ep.persistence.entity.sc.quationtender.ScQtCheckListScore;
import my.ep.persistence.entity.sc.quationtender.ScQtCheckListValue;
import my.ep.persistence.entity.sc.quationtender.ScQtCommitteeActivity;
import my.ep.persistence.entity.sc.quationtender.ScQtDeliveryLoc;
import my.ep.persistence.entity.sc.quationtender.ScQtDoc;
import my.ep.persistence.entity.sc.quationtender.ScQtLocCriteria;
import my.ep.persistence.entity.sc.quationtender.ScQtProgressivePayment;
import my.ep.persistence.entity.sc.quationtender.ScQtRemark;
import my.ep.persistence.entity.sc.quationtender.ScQtSchedule;
import my.ep.persistence.entity.sc.quationtender.ScQtScoreOfferPrice;
import my.ep.persistence.entity.sc.quationtender.ScQtScoreSetting;
import my.ep.persistence.entity.sc.quationtender.ScQtScoreWeightage;
import my.ep.persistence.entity.sc.quationtender.ScQtSpecificationRemark;
import my.ep.persistence.entity.sc.quationtender.ScQtSupplier;
import my.ep.persistence.entity.sc.quationtender.ScQtSupplierCriteria;
import my.ep.persistence.entity.sc.quationtender.ScQtZoneCriteria;
import my.ep.persistence.entity.sc.quationtender.ScTechnicalUserScoringSchemaAttachment;
import my.ep.persistence.entity.sm.SmCompanyAddress;
import my.ep.persistence.entity.sm.SmCompanyBasic;
import my.ep.persistence.entity.sm.SmMofAccount;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.tpl.spec.TplSpecLoc;
import my.ep.persistence.entity.tpl.spec.TplSpecMaster;
import my.ep.persistence.entity.tpl.spec.TplSpecQuestion;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneDtl;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneLoc;
import my.ep.type.aut.SearchCondition;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.AddressType;
import my.ep.type.pm.OrganizationType;
import my.ep.type.pm.PTJLocalityCode;
import my.ep.type.pm.RoleCode;
import my.ep.util.MessageUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.util.pm.OrgProfileUtil;
import my.ep.web.backingbean.sc.quatationtender.CreateQtBackingBean;
import my.ep.web.backingbean.sc.quatationtender.QTDetailBackingBean;
import my.ep.web.backingbean.sc.quatationtender.SourcingBaseBackingBean;
import my.ep.web.backingbean.tpl.spec.TemplateSpecBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.bean.sc.common.ZonesBean;
import my.ep.web.bean.sc.quatationtender.BriefingZoneBean;
import my.ep.web.bean.sc.quatationtender.CtCircularSearchBean;
import my.ep.web.bean.sc.quatationtender.QtnTenderSearchBean;
import my.ep.web.bean.tpl.TemplateSpecBean;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.constant.sc.QTConstants;
import my.ep.web.managedbean.sc.quationtender.QTDetailManagedBean;
import my.ep.web.util.DateUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.ParameterHelper;
import my.ep.web.util.pm.helper.PmCommonUtil;
import my.ep.web.util.sc.helper.LocationSingleton;
import my.ep.web.util.sc.helper.SourcingLocationHelper;
import my.ep.web.util.tpl.helper.TemplateSpecHelper;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.component.selectbooleancheckbox.SelectBooleanCheckbox;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.NodeUnselectEvent;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.model.TreeNode;

/**
 * The Class SpecificationBackingBean.
 * 
 * @author:Garikapati Mangaiah.
 */
@ManagedBean(name = "specificationBackingBean")
@ViewScoped
public class SpecificationBackingBean extends
		SourcingBaseBackingBean<QTDetailManagedBean> {

	/** The search circular. */
	private IPagingDataModel<CtCircularVer> searchCircular = new SearchCircularPagingDataModel();

	/** The template spec create. */
	private IPagingDataModel<TplSpecMaster> templateSpecCreate = new CreateTemplateSpecPagingDataModel();

	/** The search delivery address. */
	// private IPagingDataModel<PmAddressType> searchDeliveryAddress = new
	// DeliveryPagingDataModel();

	/** The log. */
	private static Logger log = Logger
			.getLogger(SpecificationBackingBean.class);
	// private List<SpecificationCompilanceBean> selectedCompilenceList;
	/** The count. */
	private int count;

	/** The selct value. */
	private String selctValue;

	/** The current tab. */
	private int currentTab;

	/** The output tab. */
	private int outputTab;

	/** The text before sign. */
	private String textBeforeSign;

	/** The pm digi sign. */
	private PmDigiSign pmDigiSign;

	/**
	 * Instantiates a new specification backing bean.
	 */
	public SpecificationBackingBean() {

		log.info("@@@@@@@@@@" + this.getManagedBean());
	}

	/**
	 * Secretary submit task.Event handle specification Committee send remainder
	 * to all committee members.
	 */
	public void secretarySubmitTask() {
		ICommitteSO committeSO = null;

		try {
			CommitteReqDTO reqDTO = new CommitteReqDTO();
			committeSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, ICommitteSO.class);
			reqDTO.setQtId(this.getManagedBean().getQt().getQtId());
			reqDTO.setCommitteeType(ParameterConst.COMMITTEE_TYPE_SPECIFICATION_COMMITTEE);

			reqDTO.setSubject(this.getManagedBean().getSubject());
			reqDTO.setBodyText(this.getManagedBean().getMessage());
			committeSO.sendCommitteEndorseNotification(reqDTO);

		} finally {

		}
	}

	/**
	 * Save specification committee dtls.save the specification committee
	 * details.
	 */
	public void saveSpecificationCommitteeDtls() {

		try {
			validatePrepareQtChecklist();

			FacesContext
					.getCurrentInstance()
					.addMessage(
							null,
							new FacesMessage(
									FacesMessage.SEVERITY_INFO,
									null,
									MessageUtils
											.getMessage(
													MessageConst.INFO_700004,
													new String[] {
															getMessageCommon("lbl.sc.specification.details.for"),
															this.getManagedBean()
																	.getQt()
																	.getQtNo() },
													MessageUtils.BUNDLE_COMMON_MESSAGES,
													new FacesScopes()
															.getUserLocale())));

		} finally {

		}
	}

	/**
	 * Construct fin specs n socre.set socreweightage for all financial specs.
	 * 
	 * @param scQtCheckList
	 *            (scQtCheckList)
	 * @return List<ScQtCheckList>
	 */
	private List<ScQtCheckList> constructFinSpecsNSocre(
			ScQtCheckList scQtCheckList) {
		List<ScQtCheckList> finSpecCheckLists = this.getManagedBean()
				.getFinSpecList();
		if (null != finSpecCheckLists) {
			for (ScQtCheckList scQtCheckList2 : finSpecCheckLists) {

				if (null != scQtCheckList.getQtScoreWeightage()) {

					List<ScQtScoreOfferPrice> offerPrice = scQtCheckList
							.getQtScoreWeightage().getScQtScoreOfferPrices();
					if (null == offerPrice || offerPrice.isEmpty()) {
						offerPrice = new ArrayList<ScQtScoreOfferPrice>();
						if (null != this.getManagedBean()
								.getOfferPricePackage()) {
							offerPrice.add(this.getManagedBean()
									.getOfferPricePackage());
						}

						scQtCheckList.getQtScoreWeightage()
								.setScQtScoreOfferPrices(offerPrice);
					} else {
						List<ScQtScoreOfferPrice> otherofferpriceofferPrice = scQtCheckList
								.getQtScoreWeightage()
								.getScQtScoreOfferPrices();
						if (null != otherofferpriceofferPrice
								&& !otherofferpriceofferPrice.isEmpty()) {
							for (int i = 0; i < otherofferpriceofferPrice
									.size(); i++) {
								ScQtScoreOfferPrice localOfferprice = otherofferpriceofferPrice
										.get(i);
								if (localOfferprice
										.getOfferPriceType()
										.equalsIgnoreCase(
												ParameterConst.RECOMMEND_TYPE_PACKAGE)) {
									if (null != this.getManagedBean()
											.getOfferPricePackage()) {
										otherofferpriceofferPrice
												.remove(localOfferprice);

										otherofferpriceofferPrice.add(this
												.getManagedBean()
												.getOfferPricePackage());
									}
								}

							}
							scQtCheckList.getQtScoreWeightage()
									.setScQtScoreOfferPrices(
											otherofferpriceofferPrice);
						}
					}

					scQtCheckList2.setQtScoreWeightage(scQtCheckList
							.getQtScoreWeightage());
				}
			}
		}
		return finSpecCheckLists;
	}

	/**
	 * Reset circular.Event handle to reset all the circular search information.
	 */
	public void resetCircular() {
		getManagedBean().setCtSearchBean(new CtCircularSearchBean());
		this.getManagedBean().setCircularType("");
		this.getManagedBean().setPanelItemList(new ArrayList<CtPanelItem>());
		this.getManagedBean().setCircularVer(null);
		searchCircular.reloadData();
	}

	/**
	 * Committee back action.Event handle navigate to specfication committee
	 * Menu page.
	 * 
	 * @return the string
	 */
	public String committeeBackAction() {
		// this.getManagedBean().setScQtCheckSt2techProposal(null);
		// retriveTechnicalCheckList();
		this.getManagedBean().setChecklistScoreType(null);
		this.getManagedBean().setCompanyScoreType(null);
		this.getManagedBean().setScQtCheckListScore(null);
		this.getManagedBean().setScQtContractScore(null);
		this.getManagedBean().setToBeRemoveList(null);
		this.getManagedBean().setCheckListScore(null);
		this.getManagedBean().setScoringForYears(null);
		this.getManagedBean().setCheckListTitle("");
		this.getManagedBean().setIndex(-1);
		
		return "/xhtml/sc/specification/specificationCommittee/specificationCommitteeMain.xhtml?faces-redirect=true";

	}

	/**
	 * Business owner template spec.navigate to Template spec Search in
	 * Businness owner.
	 * 
	 * @return the string
	 */
	public String businessOwnerTemplateSpec() {
		return "/xhtml/sc/boSctemplatespec/createTemplate.xhtml?faces-redirect=true";
	}

	/**
	 * Retrive templatespec.navigate to Template spec Search in Specification
	 * committee.
	 * 
	 * @return the string
	 */
	public String retriveTemplatespec() {
		return "/xhtml/sc/boSctemplatespec/createCommitteeTemplate.xhtml?faces-redirect=true";
	}

	/**
	 * Adds the new category action.added empty row for category codes
	 * datatable.
	 * 
	 * @param pmCategoryL3
	 *            the pmCategoryL3 instance
	 */
	public void addNewCategoryAction(PmCategoryL3 pmCategoryL3) {
		List<ScQtCategory> qtCategoryList = this.getManagedBean()
				.getQtCategoryList();

		if (qtCategoryList == null) {
			qtCategoryList = new ArrayList<ScQtCategory>();
			this.getManagedBean().setQtCategoryList(qtCategoryList);
		}

		validateDuplicateCategoryCode(pmCategoryL3, qtCategoryList);

		PmCategoryL2 pmCategoryL2 = pmCategoryL3.getPmCategoryL2();
		ScQtCategory category = new ScQtCategory();
		category.setScQt(this.getManagedBean().getQt());
		category.setPmCategoryL1(pmCategoryL2.getPmCategoryL1());
		category.setPmCategoryL2(pmCategoryL2);
		category.setPmCategoryL3(pmCategoryL3);
		category.setPmCategoryL1Name(
				pmCategoryL2.getPmCategoryL1().getCategoryName());
		category.setPmCategoryL2Name(
				pmCategoryL2.getCategoryName());
		category.setPmCategoryL3Name(
				pmCategoryL3.getCategoryName());		
		
		qtCategoryList.add(category);
	}

	/**
	 * Delete category codes.delete row from the category codes datatable.
	 */
	public void deleteCategoryCodes() {
		List<ScQtCategory> removeList = this.getManagedBean()
				.getTobeRemoveCategoryCodes();
		if (null == removeList) {
			removeList = new ArrayList<ScQtCategory>();
		}
		List<ScQtCategory> openList = this.getManagedBean()
				.getQtCategoryList();

		if (null == openList) {
			openList = new ArrayList<ScQtCategory>();
		}

		List<ScQtCategory> openSelectList = this.getManagedBean()
				.getSuppCriteriaopenSelectList();
		if (null == openSelectList || openSelectList.isEmpty()) {
			throw new EpException(MessageConst.ERROR_760146);
		}

		for (ScQtCategory category : openSelectList) {
			openList.remove(category);
			removeList.add(category);
		}
		this.getManagedBean().getQt().setScQtCategorys(openList);
		this.getManagedBean().setTobeRemoveCategoryCodes(removeList);

	}

	/**
	 * Checktemplate spec status.Check the Template Spec is Exist or not in
	 * Specification committee.
	 * 
	 * @param scqt
	 *            (Scqt scqt).
	 */
	private void checktemplateSpecStatus(ScQt scqt) {
		List<ScQtCheckList> techCheckList = this.getManagedBean()
				.getScQtCheckSt2techProposal();

		if (null != techCheckList && !techCheckList.isEmpty()) {
			isTemplateSpec(techCheckList, scqt);
			isTemplateSpecStatus(techCheckList, scqt);

		} else {
			retriveTechnicalCheckList();
			techCheckList = this.getManagedBean().getScQtCheckSt2techProposal();

			if (null == techCheckList || techCheckList.isEmpty()
					|| techCheckList.isEmpty()) {
				throw new EpException(MessageConst.ERROR_760138);

			} else {
				isTemplateSpec(techCheckList, scqt);
				isTemplateSpecStatus(techCheckList, scqt);
			}
		}

	}

	/**
	 * Checks if is template spec.
	 * 
	 * @param techCheckList
	 *            (List<ScQtCheckList>)
	 * @param scqt
	 *            (ScQt scqt)
	 */
	private void isTemplateSpec(List<ScQtCheckList> techCheckList, ScQt scqt) {
		int count = 0;
		for (ScQtCheckList checkList : techCheckList) {
			if (ParameterConst.DOCUMENT_DEFINITION_TYPE_TEMPLATE_SPECIFICATION
					.equalsIgnoreCase(checkList.getDefinationType()
							.getParameterCode())) {
				count++;
			}

		}

		if (count == 0) {
			this.getManagedBean().setActiveIndex(2);
			throw new EpException(MessageConst.ERROR_760138);
		}
		this.getManagedBean().setTechnicalSpecCount(count);
	}

	/**
	 * Checks if is template spec status.check the Technical Spec Status is
	 * pending or completed.
	 * 
	 * @param techCheckList
	 *            the tech check list
	 * @param scqt
	 *            the scqt
	 */
	private void isTemplateSpecStatus(List<ScQtCheckList> techCheckList,
			ScQt scqt) {
		int count = 0;
		for (ScQtCheckList checkList : techCheckList) {
			if (ParameterConst.DOCUMENT_DEFINITION_TYPE_TEMPLATE_SPECIFICATION
					.equalsIgnoreCase(checkList.getDefinationType()
							.getParameterCode())) {
				if (checkList.getPmStatus().getStatusId()
						.equals(QTStatusConst.TS_PENDING_SUBMISSION)) {
					count++;
				}
			}

		}
		if (count >= 1) {
			this.getManagedBean().setActiveIndex(2);
			throw new EpException(MessageConst.ERROR_760152);
		}

	}

	/**
	 * Check financial spec status.Check the Financial Spec Status is Pending or
	 * Completed.
	 * 
	 * @param scqt
	 *            (ScQt scqt).
	 */
	private void checkFinancialSpecStatus(ScQt scqt) {
		List<ScQtCheckList> financialCheckList = this.getManagedBean()
				.getScQtCheckSt2financeProposalList();
		if (null == financialCheckList || financialCheckList.isEmpty()) {
			retriveFinancialCheckList();
		}
		// retriveFinancialScoring();

		if (null != financialCheckList && !financialCheckList.isEmpty()) {
			isFinancialSpec(financialCheckList);
			for (ScQtCheckList checkList : financialCheckList) {
				if (ParameterConst.DOCUMENT_DEFINITION_TYPE_TEMPLATE_SPECIFICATION
						.equalsIgnoreCase(checkList.getDefinationType()
								.getParameterCode())) {
					if (checkList.getPmStatus().getStatusId()
							.equals(QTStatusConst.TS_PENDING_SUBMISSION)) {
						this.getManagedBean().setActiveIndex(3);
						throw new EpException(MessageConst.ERROR_760153);
					}

				}
			}

		}

	}

	/**
	 * Checks if is financial spec.Check Financial Specs is Existing or not.
	 * 
	 * @param techCheckList
	 *            the tech check list
	 */
	private void isFinancialSpec(List<ScQtCheckList> techCheckList) {
		int count = 0;
		for (ScQtCheckList checkList : techCheckList) {
			if (ParameterConst.DOCUMENT_DEFINITION_TYPE_TEMPLATE_SPECIFICATION
					.equalsIgnoreCase(checkList.getDefinationType()
							.getParameterCode())) {
				count++;
			}

		}
		if (count == 0) {
			this.getManagedBean().setActiveIndex(2);
			throw new EpException(MessageConst.ERROR_760215);

		}
		this.getManagedBean().setFinancialSpecCount(count);
		if (this.getManagedBean().getTechnicalSpecCount() != this
				.getManagedBean().getFinancialSpecCount()) {
			this.getManagedBean().setActiveIndex(2);
			throw new EpException(MessageConst.ERROR_760228);

		}

	}

	/**
	 * Qt checklist tab change.Event handle change the menu in Specification
	 * Committee .accoring to index it will display menu information.
	 * 
	 * @param activeIndex
	 *            (int activeIndex).
	 */
	public void qtChecklistTabChange(int activeIndex) {

		int previousIndex = this.getManagedBean().getActiveIndex();
		this.getManagedBean().setCheckListYesIndex(activeIndex);

		IQTSpecSO specSo = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
						IQTSpecSO.class);
		SpecificationReqDTO specReqDTO = new SpecificationReqDTO();
		boolean technicalOtherCheckList = true;
		boolean financialOtherCheckList = true;

		switch (previousIndex) {
		case 0:
			/*
			 * if (this.getManagedBean().isReadOnly()) { if ((null !=
			 * this.getManagedBean().getQt() .getFulfilmentType() && null ==
			 * this.getManagedBean()
			 * .getQt().getFulfilmentType().getParameterId()) || (null ==
			 * this.getManagedBean().getQt() .getContractDuration())) { throw
			 * new EpException(MessageConst.ERROR_760134);
			 * 
			 * } }
			 */
			if (!this.getManagedBean().isReadOnly()) {
				specReqDTO.setScQt(constructSpecCommitteDtls().getScQt());
				specSo.validateSpecCommitteGeneralInfo(specReqDTO);
			}
			if (this.getManagedBean().isZoneSpecUpdate()) {
				ZonesUpdateWarningMessage(this.getManagedBean()
						.isZonesWarnMessageUpdate());
				validatePrepareQtChecklist();
			}
			break;
		case 1:
			specReqDTO.setScQt(constructSpecCommitteDtls().getScQt());
			specSo.validateSupplierQulificationInfo(specReqDTO);

			break;

		case 2:
			//ScQt scQtTechChecklist = constructSpecCommitteDtls().getScQt();
			specSo.validateCheckList(constructSpecCommitteDtls());
			//checktemplateSpecStatus(scQtTechChecklist);
			if (!this.getManagedBean().isReadOnly()) {
				technicalOtherCheckList = validateTechnicalStandardCheckList();
			}
			specReqDTO.setScQt(constructSpecCommitteDtls().getScQt());
			specSo.validateCheckList(specReqDTO);

			break;
		case 3:
			//ScQt scQtFinChecklist = constructSpecCommitteDtls().getScQt();
			specSo.validateCheckList(constructSpecCommitteDtls());
			//checktemplateSpecStatus(scQtFinChecklist);
			//checkFinancialSpecStatus(scQtFinChecklist);
			if (!this.getManagedBean().isReadOnly()) {
				financialOtherCheckList = validateFinancialStandardCheckList();
			}
			break;

		case 4:
			ScQt scQtTechScore = constructSpecCommitteDtls().getScQt();
			checktemplateSpecStatus(scQtTechScore);
			checkFinancialSpecStatus(scQtTechScore);

			List<ScQtCheckList> technicalScoring = this.getManagedBean()
					.getTechnicalScoring();
			ScQtScoreSetting technicalScoreSetting = this.getManagedBean()
					.getTechnicalScoreSetting();

			if (technicalScoreSetting != null) {
				specReqDTO.setScoreSetting(technicalScoreSetting);
			}
			ScQtScoreSetting demoScoreSetting = this.getManagedBean()
					.getDemoScoreSetting();
			if (demoScoreSetting != null) {
				specReqDTO.setScoreSetting(demoScoreSetting);
			}
			specReqDTO.setCheckLists(technicalScoring);
			specReqDTO.setSpecType(ParameterConst.SPECIFICATION_TYPE_TECHNICAL);
			specReqDTO.setScQt(scQtTechScore);
			specSo.validateSCScoreSettings(specReqDTO);
			break;

		case 5:

			ScQt scQtFinScore = constructSpecCommitteDtls().getScQt();
			checktemplateSpecStatus(scQtFinScore);
			checkFinancialSpecStatus(scQtFinScore);

			List<ScQtCheckList> financialScoring = this.getManagedBean()
					.getFinancialScoring();
			ScQtScoreSetting financialScoreSetting = this.getManagedBean()
					.getFinancialScoreSetting();

			if (null != financialScoreSetting
					&& financialScoreSetting.getSettingType() == 2) {
				specReqDTO.setScoreSetting(financialScoreSetting);
			}

			specReqDTO.setCheckLists(financialScoring);
			specReqDTO.setSpecType(ParameterConst.SPECIFICATION_TYPE_FINANCIAL);
			specReqDTO.setScQt(scQtFinScore);
			specSo.validateSCScoreSettings(specReqDTO);

			break;
		case 6:

			ScQt scQtRecommendation = constructSpecCommitteDtls().getScQt();
			checktemplateSpecStatus(scQtRecommendation);
			checkFinancialSpecStatus(scQtRecommendation);

			List<ScQtScoreSetting> scoreSetting = new ArrayList<ScQtScoreSetting>();
			ScQtScoreSetting packageScoreSetting = this.getManagedBean()
					.getRecommendationPackageScore();
			if (null != packageScoreSetting
					&& packageScoreSetting.getSettingType() == 4) {
				scoreSetting.add(packageScoreSetting);

			}

			ScQtScoreSetting itemScoreSetting = this.getManagedBean()
					.getRecommendationScore();
			if (null != itemScoreSetting
					&& itemScoreSetting.getSettingType() == 5) {
				scoreSetting.add(itemScoreSetting);

			}
			specReqDTO.setScoreSettings(scoreSetting);
			specReqDTO.setScQt(scQtRecommendation);

			break;

		case 7:
			ScQt deliveryAddress = constructSpecCommitteDtls().getScQt();
			//checktemplateSpecStatus(deliveryAddress);
			//checkFinancialSpecStatus(deliveryAddress);

			if (deliveryAddress.isZonal()) {
				if (null == deliveryAddress.getScQtDeliveryLocs()) {
					List<BriefingZoneBean> zoneBean = this.getManagedBean()
							.getDeliveryLocBean();
					if (null != zoneBean) {
						for (BriefingZoneBean deliverybean : zoneBean) {
							if (null != deliverybean.getDeliveryLocList()) {
								deliveryAddress
										.setScQtDeliveryLocs(deliverybean
												.getDeliveryLocList());
							}
						}
					}
				}
			}

			specReqDTO.setScQt(deliveryAddress);
			specSo.validateSCdelieryLocs(specReqDTO);

			break;

		case 8:

			ScQt fulfilMentDtls = constructSpecCommitteDtls().getScQt();
			//checktemplateSpecStatus(fulfilMentDtls);
			//checkFinancialSpecStatus(fulfilMentDtls);

			if (ParameterConst.FULFILMENT_TYPE_SCHEDULED.equalsIgnoreCase(this
					.getManagedBean().getFulfilmentType())
					|| ParameterConst.FULFILMENT_TYPE_AS_AND_WHEN
							.equalsIgnoreCase(this.getManagedBean()
									.getFulfilmentType())) {
				specReqDTO.setScQt(fulfilMentDtls);
				specSo.validateSCFulfillmentSchedules(specReqDTO);
			}

			break;

		case 9:
			ScQt committeeEndorsment = constructSpecCommitteDtls().getScQt();
			//checktemplateSpecStatus(committeeEndorsment);
			//checkFinancialSpecStatus(committeeEndorsment);
			break;

		}
		switch (activeIndex) {
		case 0:
			
			retriveSpecdetails(this.getManagedBean().getQt().getQtId());
			break;
		case 1:
			retriveSupplierQualificationCriteria("SC");
			break;

		case 2:
			retriveTechnicalCheckList();
			break;
		case 3:
			retriveFinancialCheckList();
			//checktemplateSpecStatus(this.getManagedBean().getQt());
			break;

		case 4:
			checktemplateSpecStatus(this.getManagedBean().getQt());
			checkFinancialSpecStatus(this.getManagedBean().getQt());
			retriveTechnicalScoring();
			break;

		case 5:
			checktemplateSpecStatus(this.getManagedBean().getQt());
			checkFinancialSpecStatus(this.getManagedBean().getQt());
			retriveFinancialScoring();
			break;

		case 6:
			retriveRecommendationDetails();
			checktemplateSpecStatus(this.getManagedBean().getQt());
			checkFinancialSpecStatus(this.getManagedBean().getQt());
			break;

		case 7:
			retriveDeliveryDetails();
			//checktemplateSpecStatus(this.getManagedBean().getQt());
			//checkFinancialSpecStatus(this.getManagedBean().getQt());
			break;

		case 8:
			if (ParameterConst.FULFILMENT_TYPE_SCHEDULED.equalsIgnoreCase(this
					.getManagedBean().getFulfilmentType())
					|| ParameterConst.FULFILMENT_TYPE_AS_AND_WHEN
							.equalsIgnoreCase(this.getManagedBean()
									.getFulfilmentType())
					|| ParameterConst.FULFILMENT_TYPE_ONE_OFF
							.equalsIgnoreCase(this.getManagedBean()
									.getFulfilmentType())) {
				retriveFulfilmentDtls();
				//checktemplateSpecStatus(this.getManagedBean().getQt());
				//checkFinancialSpecStatus(this.getManagedBean().getQt());
			}
			break;

		case 9:
			retriveCommitteeEndorsment();
			//checktemplateSpecStatus(this.getManagedBean().getQt());
			//checkFinancialSpecStatus(this.getManagedBean().getQt());
			break;

		}
		if (getManagedBean().getTempIndex() < activeIndex) {
			getManagedBean().setTempIndex(activeIndex);
		}

		if (this.getManagedBean().getActiveIndex() == 2
				|| this.getManagedBean().getActiveIndex() == 3) {
			if ((this.getManagedBean().getActiveIndex() == 2 && technicalOtherCheckList == true)
					|| (this.getManagedBean().getActiveIndex() == 3 && financialOtherCheckList == true)) {
				this.getManagedBean().setActiveIndex(activeIndex);
			}
		} else {
			this.getManagedBean().setActiveIndex(activeIndex);
		}

	}

	/**
	 * Load prepare spec committee details.Event handle click on submit button
	 * to retrieve all the Specification Committee menus information.
	 * 
	 * @param qtId
	 *            (Long qtId)
	 * @param docType
	 *            (String docType)
	 */
	private void loadPrepareSpecCommitteeDetails(Long qtId) {

		retriveSpecdetails(qtId);

		retriveSupplierQualificationCriteria("SC");
		retriveTechnicalCheckList();
		retriveTechnicalScoring();
		retriveFinancialCheckList();
		retriveFinancialScoring();
		retriveRecommendationDetails();
		retriveFulfilmentDtls();
		retriveDeliveryDetails();
		retriveCommitteeEndorsment();
		if(isQTStatusChecklistScoring() || isQTStatusChecklistScoringRevision()){
			checktemplateSpecStatus(this.getManagedBean().getQt());
			checkFinancialSpecStatus(this.getManagedBean().getQt());
		}
		if (!this.getManagedBean().isReadOnly()) {
			//checktemplateSpecStatus(this.getManagedBean().getQt());
			//checkFinancialSpecStatus(this.getManagedBean().getQt());
			validateTechnicalPassingScoring(this.getManagedBean()
					.getTechnicalScoreSetting());
			validateTechnicalDemoPassingScoring(this.getManagedBean()
					.getDemoScoreSetting());
			technicalWeightageCalculate();
			validateFinancialPackageItemPassingScoring(this.getManagedBean()
					.getFinancialScoreSetting(), this.getManagedBean()
					.getFinacialItemScoreSetting());
			financialWeightageCalculate();
			financialItemScoreCalculate(this.getManagedBean()
					.getFinacialItemScoreSetting());
			calculateRecommendationPackageAndItemScores(this.getManagedBean()
					.getRecommendationPackageScore(), this.getManagedBean()
					.getRecommendationScore());
			validateTechnicalUserScoringAttachment(this.getManagedBean()
					.isTechnicalUserScoringSchema());

			validateFinancialUserScoringAttachment(this.getManagedBean()
					.isFinancialUserScoringSchema());
			setTechncialChecklistEvaluationStatus(this.getManagedBean()
					.getScQtCheckSt2techProposal());
			setFinancialChecklistEvaluationStatus(this.getManagedBean()
					.getScQtCheckSt2financeProposalList());
		}

		// this.getManagedBean().setActiveIndex(0);

	}

	/**
	 * Retrieve supplier qualification criteria.retrieve all the Supplier
	 * Qualification Criteria Menu information and display.
	 * 
	 * @param type
	 *            (String type)
	 */
	private void retriveSupplierQualificationCriteria(String type) {
		
		log.info("retriveBusinessOwnerQualificationCriteri - START");
		QTDetailBackingBean qtBackingBean = new QTDetailBackingBean();
		qtBackingBean.retrieveSuppQualificationTabDtl();
	}


	/**
	 * Gets the locations list.
	 * 
	 * @param zoneLocation
	 *            the zone location
	 * @return the locations list
	 */
	private List<ScQtLocCriteria> getLocationsList(TreeNode zoneLocation) {
		List<ScQtLocCriteria> results = new ArrayList<ScQtLocCriteria>();
		for (TreeNode zoneNode : zoneLocation.getChildren()) {
			for (TreeNode stateNode : zoneNode.getChildren()) {
				if (stateNode.getType().equals("state")) {

					TplSpecLoc stateLoc = (TplSpecLoc) stateNode.getData();
					ScQtLocCriteria stateLocCriteria = new ScQtLocCriteria();
					stateLocCriteria.setPmState(stateLoc.getPmState());
					// stateLocCriteria.setPmDistrict(stateLoc.getPmDistrict());
					// stateLocCriteria.setPmDivision(stateLoc.getPmDivision());
					// ScQtLocCriteria locationDTO = (ScQtLocCriteria)
					// zoneLocation.getData();
					results.add(stateLocCriteria);

					for (TreeNode districtNode : stateNode.getChildren()) {
						TplSpecLoc districtLoc = (TplSpecLoc) districtNode
								.getData();
						ScQtLocCriteria districtLocCriteria = new ScQtLocCriteria();
						// districtLocCriteria.setPmState(districtLoc.getPmState());
						districtLocCriteria.setPmDistrict(districtLoc
								.getPmDistrict());
						districtLocCriteria.setPmDivision(districtLoc
								.getPmDivision());
						results.add(districtLocCriteria);
						for (TreeNode divisionCode : districtNode.getChildren()) {
							TplSpecLoc divisionLoc = (TplSpecLoc) divisionCode
									.getData();
							ScQtLocCriteria divisionLocCriteria = new ScQtLocCriteria();
							divisionLocCriteria.setPmDistrict(divisionLoc
									.getPmDistrict());
							results.add(divisionLocCriteria);
						}
					}
				}
			}
		}
		/*
		 * if (zoneLocation.isLeaf()) {
		 * 
		 * } else { for (TreeNode childrendLocationNode :
		 * zoneLocation.getChildren()) {
		 * results.addAll(getLocationsList(childrendLocationNode)); } }
		 */
		return results;
	}

	/**
	 * Creates the circular item spec.Pnel item as a specfic create item as a
	 * template spec in technical specification menu.
	 * 
	 * @param panelItem
	 *            (CtPanelItem panelItem)
	 */
	private void createCircularItemSpec(List<CtPanelItem> panelItems) {
		IParameterSO parameterSo = null;
		try {
			TemplateSpecBean templateSpecBean = getManagedBean()
					.getTemplateSpecBean();
			TplSpecMaster specMaster = new TplSpecMaster();
			String itemName="";
			String panelCoverage="";
			if(panelItems.size()==1){
				CtPanelItem item=panelItems.get(0);
				if(ParameterConst.ITEM_CATEGORY_TYPE_SPECIFIC.equals(item.getItemCategoryId().getParameterCode())){
					String description=item.getCtPanelItemGroup().getGroupName();
					itemName=description;
					panelCoverage=item.getPanelCoverageId().getParameterCode();
					checkPanelItemIsZonalOrNonzonal(panelCoverage,item);
				}
				else if(ParameterConst.ITEM_CATEGORY_TYPE_GENERAL.equals(item.getItemCategoryId().getParameterCode())){
					specMaster.setCtPanelItem(item);
					String description=item.getCtPanelItemGroup().getGroupName();
					description=description.concat("-");
					itemName=description.concat(item.getItemName());
					panelCoverage=item.getPanelCoverageId().getParameterCode();
					//locationLevel=item.getLocationLevelId().getParameterCode();
					checkPanelItemIsZonalOrNonzonal(panelCoverage,item);
				
					
				}
				//System.out.println("====display concat itemname==="+itemName);
			}else{
				CtPanelItem item=panelItems.get(0);
				String description=item.getCtPanelItemGroup().getGroupName();
				itemName=description;
				panelCoverage=item.getPanelCoverageId().getParameterCode();
				checkPanelItemIsZonalOrNonzonal(panelCoverage,item);
				//System.out.println("====display concat itemname==="+itemName);
			}
			parameterSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
			ScQtCheckList checkList = new ScQtCheckList();
			ScQtDoc scqtDocDest = new ScQtDoc();
			

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

			checkList.setPmEvalStatus(getPmStatusByStatusId(QTStatusConst.TS_PENDING_SUBMISSION));
			checkList.setTitle(itemName);
			checkList.setDocCategory(QTConstants.TECHNICAL_DOC_TYPE);
			checkList
					.setChecklistSeq(SourcingConstants.CHECKLIST_SEQ_TEMPLATE_SPEC);
			checkList.setScoringReq(this.getManagedBean().getQt()
					.isTechEvaluationReq());
			checkList.setAction(checkListactionresDTO.getPmParameter());
			checkList.setScQt(this.getManagedBean().getQt());
			checkList.setDefinationType(specTyperesDTO.getPmParameter());
			setUserDetails(checkList);
			checkList.setPmStatus(getPmStatusByStatusId(QTStatusConst.TS_PENDING_SUBMISSION));
			checkList.setManditory(false);
			checkList.setRecordStatus(true);
			checkList.setScQtDoc(scqtDocDest);

			scqtDocDest.setScQt(this.getManagedBean().getQt());
			scqtDocDest.setTplSpecMaster(specMaster);
			scqtDocDest.setScQtCheckList(checkList);
			// scqtDocDest.setTplDocMaster(null);
			// scqtDocDest.setTplDocMaster(null);
			scqtDocDest.setRecordStatus(true);
			setUserDetails(scqtDocDest);

			IOrgProfileSO profileSo = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IOrgProfileSO.class);

			OrgProfileReqDTO profileReq = new OrgProfileReqDTO();
			profileReq.setOrgProfileId(1L);
			OrgProfileResDTO profileRes = profileSo
					.retrieveOrgProfileByProfileId(profileReq);

			ParameterReqDTO techSpecType = new ParameterReqDTO();
			techSpecType
					.setParameterType(ParameterTypeConst.SPECIFICATION_TYPE);
			techSpecType.setParameterCode(ParameterConst.ANSWER_TYPE_TEXT);
			ParameterResDTO techspecTyperesDTO = parameterSo
					.retrieveParameterByTypeAndCode(techSpecType);

			ParameterReqDTO templateTypeReqDTO = new ParameterReqDTO();
			templateTypeReqDTO
					.setParameterType(ParameterTypeConst.SC_PROCUREMENT_MODE);
			templateTypeReqDTO
					.setParameterCode(ParameterConst.ANSWER_TYPE_TEXT);
			ParameterResDTO templateTypeResDTO = parameterSo
					.retrieveParameterByTypeAndCode(templateTypeReqDTO);

			ParameterReqDTO procurementReqDTO = new ParameterReqDTO();
			procurementReqDTO
					.setParameterType(ParameterTypeConst.PROCUREMENT_CATEGORY);
			procurementReqDTO
					.setParameterCode(ParameterConst.PROCUREMENT_CATEGORY_PERALATAN_PEJABAT);
			ParameterResDTO procurementResDTO = parameterSo
					.retrieveParameterByTypeAndCode(procurementReqDTO);

			/*
			 * ParameterReqDTO itemTypeReqDTO = new ParameterReqDTO();
			 * itemTypeReqDTO.setParameterType(ParameterTypeConst.SC_ITEM_TYPE);
			 * itemTypeReqDTO
			 * .setParameterCode(ParameterConst.ITEM_TYPE_PRODUCT);
			 * ParameterResDTO itemTypeResDTO = parameterSo
			 * .retrieveParameterByTypeAndCode(itemTypeReqDTO);
			 */

			ParameterReqDTO localGoodsReqDTO = new ParameterReqDTO();
			localGoodsReqDTO.setParameterType(ParameterTypeConst.ITEM_ORIGIN);
			localGoodsReqDTO.setParameterCode(ParameterConst.LOCAL_GOODS);
			ParameterResDTO localGoodsResDTO = parameterSo
					.retrieveParameterByTypeAndCode(localGoodsReqDTO);

			specMaster.setSpecName(itemName);
			specMaster.setSpecDesc(itemName);

			specMaster.setProcessType(TemplateSpecConst.PROCESS_TYPE_SOURCING);
			specMaster.setOrgProfile(profileRes.getPmOrgProfile());
			// specMaster.setDurationMonth(12);
			specMaster
					.setTemplateStatus(TemplateSpecConst.TEMPLATE_STATUS_DRAFT);
			specMaster.setScQtDoc(null);
			specMaster.setScQuoteDocs(null);
			specMaster.setBrandNew(false);
			specMaster.setRecordStatus(true);
			specMaster.setTemplateTypeCode(templateTypeResDTO.getPmParameter());
			specMaster.setSpecTypeCode(techspecTyperesDTO.getPmParameter());
			specMaster.setProcurementCategory(procurementResDTO
					.getPmParameter());
			specMaster.setItemTypeCode(panelItems.get(0).getItemType());
			specMaster.setStatus(getPmStatusByStatusId(QTStatusConst.BSV_PENDING));
			specMaster.setItemOriginCode(localGoodsResDTO.getPmParameter());
			specMaster.setQtyByZone(true);
			setUserDetails(specMaster);
			List<TplSpecQuestion> allquestionsList = new ArrayList<TplSpecQuestion>();
			int value=-1;
			for (CtPanelItem panelItem : panelItems) {
				if(allquestionsList.isEmpty()){
					value=-1;
				}else{
					value=allquestionsList.size()-1;
				}
				new TemplateSpecBackingBean().addQuestion(
						new TemplateSpecBean(), specMaster, allquestionsList,
						value,
						TemplateSpecConst.QUESTION_TYPE_ITEM);
				TplSpecQuestion tplSpecQuestion = null;
				if(value==-1){
					tplSpecQuestion=allquestionsList.get(0);
				}else{
					tplSpecQuestion=allquestionsList.get(value+1);
				}

				// tplSpecQuestion.setTplSpecMaster(specMaster);
				tplSpecQuestion.setCtPanelItem(panelItem);
				tplSpecQuestion.setClItem(panelItem.getItemName());
				tplSpecQuestion.setClSpecDetail(panelItem.getSpecification());
				tplSpecQuestion.setClUom(panelItem.getPmUom());
				tplSpecQuestion.setClQuantity(new BigDecimal(panelItem
						.getMinimumOrderQty()));
				tplSpecQuestion.setTplSpecEstimations(null);
				ParameterReqDTO PriceTypereqDTO = new ParameterReqDTO();
				PriceTypereqDTO.setParameterType(ParameterTypeConst.PRICE_TYPE);
				if (specMaster.getTplSpecZone() != null) {
					PriceTypereqDTO
							.setParameterCode(ParameterConst.PRICE_TYPE_ZONAL_STANDARD);
				} else {
					PriceTypereqDTO
							.setParameterCode(ParameterConst.PRICE_TYPE_STANDARD);
				}

				ParameterResDTO PriceTyperesDTO = parameterSo
						.retrieveParameterByTypeAndCode(PriceTypereqDTO);
				tplSpecQuestion.setClPriceTypeCode(PriceTyperesDTO
						.getPmParameter());
				setUserDetails(tplSpecQuestion);
				// questionsList.add(tplSpecQuestion);
				specMaster.setScoringReq(this.getManagedBean().getQt()
						.isTechEvaluationReq());

				if (null != panelItem.getCtPanelItemSpecs()
						&& !panelItem.getCtPanelItemSpecs().isEmpty()) {
					for (int i = 0; i < panelItem.getCtPanelItemSpecs().size(); i++) {
						new TemplateSpecBackingBean().addQuestion(
								new TemplateSpecBean(), specMaster,
								allquestionsList, allquestionsList.size() - 1,
								TemplateSpecConst.QUESTION_TYPE_ITEM_SPEC);
						CtPanelItemSpec itemSpec = panelItem
								.getCtPanelItemSpecs().get(i);
						TplSpecQuestion tplSpecSpecifications =null;
						if(value==-1){
							tplSpecSpecifications	= allquestionsList
								.get(i + 1);
						}else{
							tplSpecSpecifications	= allquestionsList
									.get(allquestionsList.size() -1);
						}
						PmParameter pmParameter = getParameterByTypeAndCode(
								ParameterTypeConst.ANSWER_TYPE,
								ParameterConst.ANSWER_TYPE_TEXT);
						tplSpecSpecifications.setClSpecDetail(itemSpec
								.getSpecification());
						tplSpecSpecifications.setAnswerTypeCode(pmParameter);
						if (specMaster.isScoringReq()) {
							PmParameter scoreType = getParameterByTypeAndCode(
									ParameterTypeConst.SCORE_TYPE,
									ParameterConst.SCORE_TYPE_USER);
							tplSpecSpecifications.setScoreTypeCode(scoreType);
						} else {
							tplSpecSpecifications.setScoreTypeCode(null);
						}

						setUserDetails(tplSpecSpecifications);
					}
				}
				//allquestionsList.addAll(questionsList);

			}
			specMaster.setTplSpecQuestions(allquestionsList);
			// this.getManagedBean().getQt().setZoneLocationLevel(null);
			this.getManagedBean().getQt().getScQtSupplierCriteria()
					.setLocationLevel(null);
			//this.getManagedBean().getQt().setFulfilmentType(null);
			if (this.getManagedBean().getQt().isZonal()) {
				// validateBusinessOwner(true);
				ScQtSupplierCriteria supplierCriteria = getManagedBean()
						.getQt().getScQtSupplierCriteria();
				if (null != supplierCriteria
						&& null != supplierCriteria.getQtZoneCriterias()) {
					TemplateSpecHelper.getInstance().updateZoneDtl(specMaster,
							supplierCriteria.getQtZoneCriterias());
				} else {
					throw new EpException(
							"Please choose the zones before proceed.");
				}
			} else {
				TemplateSpecHelper.getInstance()
						.updateZoneDtl(specMaster, null);
			}

		
			List<ScQtCheckList> technicalCheckList = this.getManagedBean()
					.getScQtCheckSt2techProposal();
			if (null != technicalCheckList && !technicalCheckList.isEmpty()) {
				boolean istechnicalSpec = isAtleastTechnicalSpec(technicalCheckList);
				this.getManagedBean().setSpecItemWeightageRequired(
						istechnicalSpec);
				boolean weightageType = retriveExistingTemplatespec(technicalCheckList);
				specMaster.setWeightageReq(weightageType);
				templateSpecBean.setSpecMaster(specMaster);
				TemplateSpecBackingBean templateSpecBackingbean = new TemplateSpecBackingBean();
				templateSpecBackingbean
						.updateItemWeightageReq(templateSpecBean);

			} else {
				this.getManagedBean().setSpecItemWeightageRequired(false);
			}
			templateSpecBean.setSpecMaster(specMaster);
			if (this.getManagedBean().getQt().isZonal()
					&& this.getManagedBean().getQt().getQtType()
							.equals(ParameterConst.QUOTATION_TENDER_TYPE_OPEN)) {
				retriveSpecdetails(this.getManagedBean().getQt().getQtId());
				List<TplSpecZoneDtl> businessOwnerZone = convertTreeNodesToZoneCriteria(
						this.getManagedBean().getZoneGeneralTrees(), null);
				ScQtSupplierCriteria supplierCriteria = this.getManagedBean().getQt().getScQtSupplierCriteria();
				if (businessOwnerZone != null
						&& !businessOwnerZone.isEmpty()) {

					for (TplSpecZoneDtl tplSpecZoneDtl : businessOwnerZone) {
						supplierCriteria.getQtZoneCriterias().setModified(
								true);
						tplSpecZoneDtl.setTplSpecZone(supplierCriteria
								.getQtZoneCriterias());

					}
					//templateSpecBean.setSpecMaster(specMaster);
					templateSpecBean.getSpecMaster().setTplSpecZone(
							supplierCriteria.getQtZoneCriterias());
					templateSpecBean.getSpecMaster().getTplSpecZone()
							.setTplSpecZoneDtls(businessOwnerZone);
				}

			}
			this.getManagedBean().setScQtChecklist(checkList);
			//QTTemplateDocReqDTO docReqDTO = new QTTemplateDocReqDTO();
			//List<ScQtDoc> qtDocs = new ArrayList<ScQtDoc>();
			//qtDocs.add(scqtDocDest);
			//docReqDTO.setScQtDoc(qtDocs);

//			IQTTemplateSO qtTemplateSO = BusinessDelegate.getInstance()
//					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
//							IQTTemplateSO.class);

			//qtTemplateSO.saveOrUpdateTemplateDoc(docReqDTO);
		} finally {

		}

	}

	/**
	 * Retrive technical specification.retrieve all the technical specification
	 * menu information and display in business owner.
	 */
	private void retriveTechnicalSpecification() {

		try {

			if (null != this.getManagedBean().getPanelItemCategory()
					&& !"".equals(this.getManagedBean().getPanelItemCategory())
					&& ParameterConst.ITEM_CATEGORY_TYPE_SPECIFIC
							.equalsIgnoreCase(this.getManagedBean()
									.getPanelItemCategory())
					&& this.getManagedBean().getQt().isPanel()) {
				retrieveTechnicalSpecs();
				if (null == this.getManagedBean().getScQtCheckSt2techProposal()
						|| this.getManagedBean().getScQtCheckSt2techProposal()
								.isEmpty()) {
					//createCircularItemSpec(this.getManagedBean()
							//.getCtPanelItem());
					this.getManagedBean().setSelectPanelItem(false);
				}
			}
			if (null == this.getManagedBean().getScQtCheckSt2techProposal()
					|| this.getManagedBean().getScQtCheckSt2techProposal()
							.isEmpty()) {
				retrieveTechnicalSpecs();
			}

		} finally {

		}

	}

	private void retrieveTechnicalSpecs() {
		IQTSpecSO specSO;
		SpecificationResDTO resDto;
		try {
			SpecificationReqDTO reqDTO = new SpecificationReqDTO();
			reqDTO.setQtId(this.getManagedBean().getQt().getQtId());
			reqDTO.setPrepareSpec(false);

			specSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);
			resDto = specSO.retrieveSpecDetails(reqDTO);

			this.getManagedBean().setScQtCheckSt2techProposal(
					new ArrayList<ScQtCheckList>(resDto.getSpecResultBean()
							.getResultList()));
			this.getManagedBean().setScQtCheckSt2financeProposalList(
					resDto.getFinSpecResultBean().getResultList());

		} finally {
			specSO = null;
		}

	}

	/**
	 * Retrive financial specification.retrieve all the financial specification
	 * menu information and display in business owner.
	 */
	private void retriveFinancialSpecification() {
		IQTSpecSO specSO;
		SpecificationResDTO resDto;
		// this.getManagedBean().setActiveIndex(3);

		try {
			if (null == this.getManagedBean()
					.getScQtCheckSt2financeProposalList()
					|| this.getManagedBean()
							.getScQtCheckSt2financeProposalList().isEmpty()) {
				SpecificationReqDTO reqDTO = new SpecificationReqDTO();
				reqDTO.setQtId(this.getManagedBean().getQt().getQtId());
				reqDTO.setPrepareSpec(false);

				specSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);
				resDto = specSO.retrieveSpecDetails(reqDTO);

				this.getManagedBean().setScQtCheckSt2financeProposalList(
						resDto.getFinSpecResultBean().getResultList());
			}

		} finally {
			specSO = null;
		}

	}

	/**
	 * Retrive approval remarks.retrieve all the approval Remarks menu
	 * information and display in business owner.
	 */

	private void retriveApprovalRemarks() {
		IQTDetailSO qtDetailSO;
		QuationTenderResDTO resDto;
		// this.getManagedBean().setActiveIndex(4);

		try {
			QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
			reqDTO.setQtId(this.getManagedBean().getQt().getQtId());
			reqDTO.setDocType(ParameterConst.DOCTYPE_QUOTATION);
			qtDetailSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);

			resDto = qtDetailSO.retriveQtRemarks(reqDTO);

			if (resDto.getRemarks() == null) {

				this.getManagedBean().setScQtRemarksList(
						new ArrayList<ScQtRemark>());
			}

			else {

				List<ScQtRemark> remarkList = new ArrayList<ScQtRemark>();
				for (ScRemark remark : resDto.getRemarks()) {
					ScQtRemark qtRemark = (ScQtRemark) remark;
					remarkList.add(qtRemark);

				}
				this.getManagedBean().setMinistryRemarksSize(remarkList.size());
				this.getManagedBean().setScQtRemarksList(remarkList);
			}
			if (SCProcessNameConst.SPECIFICATION_APPROVAL.equalsIgnoreCase(this
					.getManagedBean().getTaskName())) {
				initializeRemarks(this.getManagedBean().getQt(), this
						.getManagedBean().getQt().getCurrentWorkFlowStatus(),
						this.getManagedBean().getScQtRemarksList(),
						ScQtRemark.class);
			}

		} finally {
			qtDetailSO = null;
		}
	}

	/**
	 * Procurement category code.set procurement category code from scqt.
	 * 
	 * @param scQt
	 *            (ScQt scQt)
	 */
	private void procurementCategoryCode(ScQt scQt) {
		try {
			PmParameter procurementCategory = scQt.getPmProcurementCategory();
			if (null != procurementCategory
					&& null != procurementCategory.getParameterId()) {
				this.getManagedBean().setProcuremntCategory(
						procurementCategory.getParameterCode());

			}

		} finally {

		}
	}

	/**
	 * Retrieve technical check list.retrieve all the Technical CheckList menu
	 * information and display in Specification committee.
	 */
	public void retriveTechnicalCheckList() {
		IQTSpecSO specSo = null;
		SpecificationResDTO resDTO = null;

		if (getManagedBean().getMarkDeletedTechChecklist() == null) {
			getManagedBean().setMarkDeletedTechChecklist(
					new ArrayList<ScQtCheckList>());
		}

		try {
			if (null == this.getManagedBean().getScQtCheckSt2techProposal()
					|| this.getManagedBean().getScQtCheckSt2techProposal()
							.isEmpty()) {
				List<ScQtCheckList> localcheckList = new ArrayList<ScQtCheckList>();
				SpecificationReqDTO reqDTO = new SpecificationReqDTO();
				specSo = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);
				reqDTO.setScQt(this.getManagedBean().getQt());
				reqDTO.setSpecType(SourcingConstants.TECHNICAL_DOCTYPE);
				resDTO = specSo.retrieveCheckLists(reqDTO);
				if (!isAtleastOneSeriveReq(resDTO.getTechnicalCheckList())) {
					if (null != resDTO.getTechnicalCheckList()) {
						// Collections.sort(resDTO.getTechnicalCheckList());
						for (ScQtCheckList checklist : resDTO
								.getTechnicalCheckList()) {
							if (null != checklist.getPmDocument()) {
								if (!SourcingConstants.DOC_DEF_KEY_SERVICE_SCOPE
										.equalsIgnoreCase(checklist
												.getPmDocument()
												.getDocDefinitionKey())) {
									localcheckList.add(checklist);
								}
							} else {
								localcheckList.add(checklist);
							}
						}
					}
				} else {
					// Collections.sort(resDTO.getTechnicalCheckList());
					localcheckList = new ArrayList<ScQtCheckList>(
							resDTO.getTechnicalCheckList());
					// localcheckList.addAll(resDTO.getTechnicalCheckList());
				}

				this.getManagedBean().setScQtCheckSt2techProposal(
						localcheckList);
				this.getManagedBean().setScQtCheckSt2financeProposalList(
						resDTO.getFinancialCheckLists());
				// Collections.sort(this.getManagedBean()
				// .getScQtCheckSt2financeProposalList());
			}
		} finally {
			specSo = null;
		}
	}

	/**
	 * Retrieve financial check list.retrieve all the Financial CheckList menu
	 * information and display in Specification committee.
	 */
	public void retriveFinancialCheckList() {
		IQTSpecSO specSo = null;
		SpecificationResDTO resDTO = null;

		if (getManagedBean().getMarkDeletedFinChecklist() == null) {
			getManagedBean().setMarkDeletedFinChecklist(
					new ArrayList<ScQtCheckList>());
		}

		try {

			if (null == this.getManagedBean()
					.getScQtCheckSt2financeProposalList()
					|| this.getManagedBean()
							.getScQtCheckSt2financeProposalList().isEmpty()) {

				SpecificationReqDTO reqDTO = new SpecificationReqDTO();
				specSo = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);
				reqDTO.setScQt(this.getManagedBean().getQt());
				reqDTO.setSpecType(SourcingConstants.FINANCIAL_DOCTYPE);
				resDTO = specSo.retrieveCheckLists(reqDTO);
				// Collections.sort(resDTO.getFinancialCheckLists());
				this.getManagedBean().setScQtCheckSt2financeProposalList(
						resDTO.getFinancialCheckLists());
			}
		} finally {
			specSo = null;
		}
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
					}
				}
			}
		}

		return false;
	}

	/**
	 * Construct score setting.create Score Setting Object for
	 * Technical/Financial/Demo/Package/Item passing scores.
	 * 
	 * @param settingType
	 *            (int settingType)
	 * @return ScQtScoreSetting.
	 */
	private ScQtScoreSetting constructScoreSetting(int settingType) {
		ScQtScoreSetting scQtScoreSetting = new ScQtScoreSetting();
		/*if (settingType == 3 || settingType == 4 || settingType == 5) {
			scQtScoreSetting.setPassingScore(new BigDecimal(0));
		}
		scQtScoreSetting.setOverallFin(new BigDecimal(0));
		scQtScoreSetting.setOverallOtherFin(new BigDecimal(0));
		scQtScoreSetting.setOverallTechnical(new BigDecimal(0));
		scQtScoreSetting.setOverallOtherTechnical(new BigDecimal(0));*/
		scQtScoreSetting.setSettingType(settingType);
		scQtScoreSetting.setScQt(this.getManagedBean().getQt());
		setUserDetails(scQtScoreSetting);
		return scQtScoreSetting;

	}

	/**
	 * Retrieve technical scoring.retrieve all the Technical Scoring menu
	 * information and display in Specification committee.
	 * 
	 * @return the string
	 */
	private String retriveTechnicalScoring() {
		IQTSpecSO specSo = null;

		SpecificationReqDTO reqDTO = new SpecificationReqDTO();
		reqDTO.setScQt(this.getManagedBean().getQt());
		SpecificationResDTO resDTO = null;
		try {
			if (null == this.getManagedBean().getScQtCheckSt2techProposal()
					|| this.getManagedBean().getScQtCheckSt2techProposal()
							.isEmpty()) {
				specSo = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);

				resDTO = specSo.retrieveCheckLists(reqDTO);

				this.getManagedBean().setScQtCheckSt2techProposal(
						new ArrayList<ScQtCheckList>(resDTO
								.getTechnicalCheckList()));
				this.getManagedBean().setScQtCheckSt2financeProposalList(
						resDTO.getFinancialCheckLists());
			}
			// reqDTO.setSpecType("T");
			List<Integer> settinglist = new ArrayList<Integer>();
			settinglist.add(1);
			settinglist.add(3);
			reqDTO.setSettingTypes(settinglist);
			specSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);
			resDTO = specSo.retrieveScoreSettingDtls(reqDTO);

			if (null != resDTO.getScoreSettingResultBean().getResultList()
					&& resDTO.getScoreSettingResultBean().getResultList()
							.isEmpty()) {
				if (null == this.getManagedBean().getTechnicalScoreSetting()) {
					ScQtScoreSetting technicalScoresetting = constructScoreSetting(1);
					technicalScoresetting
							.setPassingScore(retrieveTechnicalEvalutionPassingScorePmrules(RuleTypeConst.SC_TECHNICAL_PASSING_SCORE));
					this.getManagedBean().setTechnicalScoreSetting(
							technicalScoresetting);

				}

				if (null == this.getManagedBean().getDemoScoreSetting()) {
					ScQtScoreSetting demoScoresetting = constructScoreSetting(3);
					this.getManagedBean().setDemoScoreSetting(demoScoresetting);

				}

			}

			else {

				for (ScQtScoreSetting recommnendationSetting : resDTO
						.getScoreSettingResultBean().getResultList()) {
					if (recommnendationSetting.getSettingType() == 1) {
						if (null == this.getManagedBean()
								.getTechnicalScoreSetting()) {
							this.getManagedBean().setTechnicalScoreSetting(
									recommnendationSetting);
						}
					}

					if (recommnendationSetting.getSettingType() == 3) {
						if (null == this.getManagedBean().getDemoScoreSetting()) {
							this.getManagedBean().setDemoScoreSetting(
									recommnendationSetting);
						}
					}

				}
			}
			this.getManagedBean().setTechnicalScoring(
					new ArrayList<ScQtCheckList>());
			List<ScQtCheckList> checkList = this.getManagedBean()
					.getScQtCheckSt2techProposal();
			if (null != checkList && !checkList.isEmpty()) {
				// Collections.sort(checkList);
				int score = 0;
				float addScore = 0;
				for (ScQtCheckList list : checkList) {
					if (null == list.getQtScoreWeightage()) {
						ScQtScoreWeightage scoreWeightage = new ScQtScoreWeightage();
						scoreWeightage.setScore(new BigDecimal(0));
						list.setQtScoreWeightage(scoreWeightage);
						setUserDetails(list.getQtScoreWeightage());
					}
                    if(null!=list.getQtScoreWeightage() && null!=list.getQtScoreWeightage().getScore()){
					addScore = addScore
							+ list.getQtScoreWeightage().getScore().intValue();
                     }
					score = (int) addScore;
					if (list.isScoringReq()) {
						this.getManagedBean().getTechnicalScoring().add(list);
					}
					if(null!=list.getQtScoreWeightage() && null!=list.getQtScoreWeightage().getScore()){
					list.getQtScoreWeightage().setScore(new BigDecimal(list.getQtScoreWeightage().getScore().longValueExact()));
					}
					
				}

				this.getManagedBean().setTechPoints(score);
				techWeightageSum();
				techScoreSum();
			}

			if (null != this.getManagedBean().getTechnicalScoring()
					&& !this.getManagedBean().getTechnicalScoring().isEmpty()) {
				this.getManagedBean().setTechnicalUserScoringSchema(false);
				for (ScQtCheckList technicalScoringChecklist : this
						.getManagedBean().getTechnicalScoring()) {
					if (null != technicalScoringChecklist.getPmStatus()
							&& QTStatusConst.TS_COMPLETED
									.equals(technicalScoringChecklist
											.getPmStatus().getStatusId())
							&& null != technicalScoringChecklist
									.getPmEvalStatus()
							&& QTStatusConst.TS_PENDING_SUBMISSION
									.equals(technicalScoringChecklist
											.getPmEvalStatus().getStatusId())) {
						this.getManagedBean().setTechnicalUserScoringSchema(
								true);
						break;

					}

				}
			}

		} finally {
			specSo = null;
		}
		return "technicalScoring.xhtml";
	}

	/**
	 * Retrieve financial scoring.retrieve all the Financial Scoring menu
	 * information and display in Specification committee.
	 * 
	 * @return the string
	 */
	private String retriveFinancialScoring() {
		IQTSpecSO specSo = null;

		SpecificationReqDTO reqDTO = new SpecificationReqDTO();
		reqDTO.setScQt(this.getManagedBean().getQt());
		try {
			if (null == this.getManagedBean()
					.getScQtCheckSt2financeProposalList()
					|| this.getManagedBean()
							.getScQtCheckSt2financeProposalList().isEmpty()) {
				specSo = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);
				reqDTO.setSpecType(SourcingConstants.FINANCIAL_DOCTYPE);
				SpecificationResDTO resDTO1 = specSo.retrieveCheckLists(reqDTO);

				this.getManagedBean().setScQtCheckSt2financeProposalList(
						resDTO1.getFinancialCheckLists());
			}
			List<Integer> settinglist = new ArrayList<Integer>();
			settinglist.add(2);
			settinglist.add(6);

			// reqDTO.setSpecType("T");
			reqDTO.setSettingTypes(settinglist);
			specSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);
			SpecificationResDTO resDTO = specSo
					.retrieveScoreSettingDtls(reqDTO);

			if (null != resDTO.getScoreSettingResultBean().getResultList()
					&& resDTO.getScoreSettingResultBean().getResultList()
							.isEmpty()) {
				if (null == this.getManagedBean().getFinancialScoreSetting()) {
					ScQtScoreSetting scoreSetting = constructScoreSetting(2);
					this.getManagedBean()
							.setFinancialScoreSetting(scoreSetting);
				}
				if (null == this.getManagedBean().getFinacialItemScoreSetting()) {
					ScQtScoreSetting scoreSetting = constructScoreSetting(6);
					this.getManagedBean().setFinacialItemScoreSetting(
							scoreSetting);
				}

			} else {
				if (resDTO.getScoreSettingResultBean().getResultList().size() == 2) {
					for (ScQtScoreSetting scoreSttting : resDTO
							.getScoreSettingResultBean().getResultList()) {
						if (scoreSttting.getSettingType() == 2) {
							if (null == this.getManagedBean()
									.getFinancialScoreSetting()) {
								this.getManagedBean().setFinancialScoreSetting(
										scoreSttting);
							}
						}
						if (scoreSttting.getSettingType() == 6) {
							if (null == this.getManagedBean()
									.getFinacialItemScoreSetting()) {
								this.getManagedBean()
										.setFinacialItemScoreSetting(
												scoreSttting);
								calculateFinancialItemScore(this
										.getManagedBean()
										.getFinacialItemScoreSetting());
							}

						}
					}
				} else if (resDTO.getScoreSettingResultBean().getResultList()
						.size() == 1) {
					for (ScQtScoreSetting scoreSttting : resDTO
							.getScoreSettingResultBean().getResultList()) {
						if (scoreSttting.getSettingType() == 2) {
							if (null == this.getManagedBean()
									.getFinancialScoreSetting()) {
								this.getManagedBean().setFinancialScoreSetting(
										scoreSttting);
							}
						}

						if (null == this.getManagedBean()
								.getFinacialItemScoreSetting()) {
							ScQtScoreSetting scoreSetting = constructScoreSetting(6);
							this.getManagedBean().setFinacialItemScoreSetting(
									scoreSetting);
						}

					}

				}

			}
			this.getManagedBean().setFinancialScoring(
					new ArrayList<ScQtCheckList>());
			List<ScQtCheckList> checklist = this.getManagedBean()
					.getScQtCheckSt2financeProposalList();
			if (null != checklist && !checklist.isEmpty()) {

				List<ScQtCheckList> localChecklist = new ArrayList<ScQtCheckList>();

				List<ScQtCheckList> specChecklist = new ArrayList<ScQtCheckList>();

				float addScore = 0;
				float financialWeightageScore = 0;
				float financialWeightage = 0;
				// String localTitle="";
				StringBuffer sb = new StringBuffer();
				for (ScQtCheckList list : checklist) {
					if (null == list.getQtScoreWeightage()) {
						ScQtScoreWeightage scoreWeightage = new ScQtScoreWeightage();
						scoreWeightage.setScore(new BigDecimal(0));
						// scoreWeightage.setScQtScoreOfferPrices(null);
						scoreWeightage.setWeightage(new BigDecimal(0));
						setUserDetails(scoreWeightage);
						list.setQtScoreWeightage(scoreWeightage);

					}
                    if(null!=list.getQtScoreWeightage() && null!=list.getQtScoreWeightage().getScore()){
					addScore = addScore
							+ list.getQtScoreWeightage().getScore().intValue();
                     }

					if (ParameterConst.DOCUMENT_DEFINITION_TYPE_TEMPLATE_SPECIFICATION
							.equalsIgnoreCase(list.getDefinationType()
									.getParameterCode())) {
						// String Title=list.getTitle();
						// =localTitle.concat(Title)+"<br/>";

						if (null == list.getQtScoreWeightage().getWeightage()) {
							list.getQtScoreWeightage().setWeightage(
									new BigDecimal(0));
						}
						/*
						 * financialWeightage = financialWeightage +
						 * list.getQtScoreWeightage().getWeightage()
						 * .intValue();
						 */
						sb.append(list.getTitle());
						sb.append(",");

						specChecklist.add(list);
					} else {
						/*
						 * if (!list.getTitle().trim()
						 * .contains("Annual Balance Sheet") && !list.getTitle()
						 * .trim() .contains(
						 * "Verification from Financial Institution")) {
						 * localChecklist.add(list); }
						 */
						if (list.isScoringReq()) {
							localChecklist.add(list);
						}
					}

					int local = (int) addScore;
					this.getManagedBean().setFinPoints(local);
					if(null!=list.getQtScoreWeightage() && null!=list.getQtScoreWeightage().getScore()){
						list.getQtScoreWeightage().setScore(new BigDecimal(list.getQtScoreWeightage().getScore().longValueExact()));
						}
				} //
					// ScQtCheckList schecklist = specChecklist.get(0);
				if (null != specChecklist && specChecklist.size() > 0) {

					Map<Long, ScQtScoreWeightage> map = new HashMap<Long, ScQtScoreWeightage>();

					for (ScQtCheckList checklistlocal : specChecklist) {
						map.put(checklistlocal.getQtScoreWeightage()
								.getQtScoreWeightageId(), checklistlocal
								.getQtScoreWeightage());
					}
					List<ScQtScoreWeightage> list = new ArrayList<ScQtScoreWeightage>(
							map.values());
					for (ScQtScoreWeightage weightage : list) {
						financialWeightageScore = financialWeightageScore
								+ weightage.getScore().intValue();
						financialWeightage = financialWeightage
								+ weightage.getWeightage().intValue();
					}

					ScQtCheckList tmpCheckList = new ScQtCheckList();
					// PmParameter definationType = new PmParameter();
					// definationType
					// .setParameterCode(ParameterConst.DOCUMENT_DEFINITION_TYPE_TEMPLATE_SPECIFICATION);
					tmpCheckList.setDefinationType(specChecklist.get(0)
							.getDefinationType());
					specChecklist.get(0).getQtScoreWeightage()
							.setScore(new BigDecimal(financialWeightageScore));

					specChecklist.get(0).getQtScoreWeightage()
							.setWeightage(new BigDecimal(financialWeightage));
					this.getManagedBean().setFinSpecList(specChecklist);
					tmpCheckList.setQtScoreWeightage(specChecklist.get(0)
							.getQtScoreWeightage());
					// this.getManagedBean().setScQtChecklist(tmpCheckList);
					if (specChecklist.get(0).getQtScoreWeightage().getScore() != null) {
						getManagedBean().setScoreVarience(
								specChecklist.get(0).getQtScoreWeightage()
										.getScore());
					}
					tmpCheckList.setTitle(sb.toString());

					List<ScQtScoreOfferPrice> offerPrice = specChecklist.get(0)
							.getQtScoreWeightage().getScQtScoreOfferPrices();
					if (null == offerPrice || offerPrice.isEmpty()) {
						offerPrice = new ArrayList<ScQtScoreOfferPrice>();
						if (null == this.getManagedBean()
								.getOfferPricePackage()) {
							ScQtScoreOfferPrice pkgOfferPrice = new ScQtScoreOfferPrice();
							pkgOfferPrice.setScQtScoreWeightage(tmpCheckList
									.getQtScoreWeightage());
							pkgOfferPrice
									.setOfferPriceType(ParameterConst.RECOMMEND_TYPE_PACKAGE);
							setUserDetails(pkgOfferPrice);
							this.getManagedBean().setOfferPricePackage(
									pkgOfferPrice);
							offerPrice.add(pkgOfferPrice);
						}

						tmpCheckList.getQtScoreWeightage()
								.setScQtScoreOfferPrices(offerPrice);

					} else {
						for (ScQtScoreOfferPrice scoreOfferPrice : tmpCheckList
								.getQtScoreWeightage()
								.getScQtScoreOfferPrices()) {

							if (ParameterConst.RECOMMEND_TYPE_PACKAGE
									.equalsIgnoreCase(scoreOfferPrice
											.getOfferPriceType())) {
								if (null == this.getManagedBean()
										.getOfferPricePackage()) {
									this.getManagedBean().setOfferPricePackage(
											scoreOfferPrice);
								}
							}
						}

					}
					this.getManagedBean().getFinancialScoring()
							.add(0, tmpCheckList);
				}

				// this.getManagedBean().setScQtCheckSt2financeProposalList(checklist);

				this.getManagedBean().getFinancialScoring()
						.addAll(localChecklist);

				finWeightageSum();
				finScoreSum();

			}
			if (null != this.getManagedBean()
					.getScQtCheckSt2financeProposalList()
					&& !this.getManagedBean()
							.getScQtCheckSt2financeProposalList().isEmpty()) {
				this.getManagedBean().setFinancialUserScoringSchema(false);
				for (ScQtCheckList financialScoringChecklist : this
						.getManagedBean().getScQtCheckSt2financeProposalList()) {
					if (financialScoringChecklist.isScoringReq()
							&& null != financialScoringChecklist.getPmStatus()
							&& QTStatusConst.TS_COMPLETED
									.equals(financialScoringChecklist
											.getPmStatus().getStatusId())
							&& null != financialScoringChecklist
									.getPmEvalStatus()
							&& QTStatusConst.TS_PENDING_SUBMISSION
									.equals(financialScoringChecklist
											.getPmEvalStatus().getStatusId())) {
						this.getManagedBean().setFinancialUserScoringSchema(
								true);
						break;

					}

				}
			}
		} finally {
			specSo = null;
		}
		return "financialScoring.xhtml";

	}

	/**
	 * Retrieve recommendation details.retrieve all the Recommendation menu
	 * information and display in Specification committee.
	 * 
	 * @return the string
	 */
	private String retriveRecommendationDetails() {
		IQTSpecSO specSo = null;

		try {
			if (null == this.getManagedBean().getRecommendationPackageScore()
					&& null == this.getManagedBean().getRecommendationScore()) {

				SpecificationReqDTO reqDTO = new SpecificationReqDTO();
				reqDTO.setScQt(this.getManagedBean().getQt());
				// reqDTO.setSpecType("T");

				List<Integer> settinglist = new ArrayList<Integer>();
				settinglist.add(4);
				settinglist.add(5);
				reqDTO.setSettingTypes(settinglist);
				specSo = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);
				SpecificationResDTO resDTO = specSo
						.retrieveScoreSettingDtls(reqDTO);

				if (resDTO.getScoreSettingResultBean().getResultList() != null
						&& resDTO.getScoreSettingResultBean().getResultList()
								.isEmpty()) {

					if (null == this.getManagedBean()
							.getRecommendationPackageScore()) {
						ScQtScoreSetting pkgScoreSetting = constructScoreSetting(4);
						BigDecimal passingScore = retrieveTechnicalEvalutionPassingScorePmrules(RuleTypeConst.SC_TECHNICAL_WEIGHTAGE);
						pkgScoreSetting.setOverallTechnical(passingScore);
						int financial=100-passingScore.intValue();
						pkgScoreSetting.setOverallFin(new BigDecimal(financial));
						this.getManagedBean().setRecommendationPackageTotal(
								new BigDecimal(100));

						this.getManagedBean().setRecommendationPackageScore(
								pkgScoreSetting);
					}
					if (null == this.getManagedBean().getRecommendationScore()) {
						ScQtScoreSetting itemScoreSetting = constructScoreSetting(5);
						this.getManagedBean().setRecommendationScore(
								itemScoreSetting);
					}

				} else {

					for (ScQtScoreSetting recommnendationSetting : resDTO
							.getScoreSettingResultBean().getResultList()) {

						if (recommnendationSetting.getSettingType() == 4) {
							this.getManagedBean()
									.setRecommendationPackageScore(
											recommnendationSetting);
							calculateTotalPackage(this.getManagedBean()
									.getRecommendationPackageScore());
						}

						if (recommnendationSetting.getSettingType() == 5) {
							this.getManagedBean().setRecommendationScore(
									recommnendationSetting);
							calculateRecommendtechnicalScore(this
									.getManagedBean().getRecommendationScore());
							calculateRecommendFinancialScore(this
									.getManagedBean().getRecommendationScore());
						}

					}

				}
			}

		} finally {
			specSo = null;
		}
		return "recomondation.xhtml";
	}

	/**
	 * Retrieve fulfilment dtls.retrieve all the FulfilMentDetails menu
	 * information and display in Specification committee.
	 */
	private void retriveFulfilmentDtls() {
		IQTSpecSO specSo = null;

		try {

			SpecificationReqDTO reqDTO = new SpecificationReqDTO();
			specSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);
			reqDTO.setQtId(this.getManagedBean().getQt().getQtId());
			SpecificationResDTO resDTO = specSo
					.retrieveFulFilMentScheduleDtls(reqDTO);

			List<ScQtSchedule> scheduleList = this.getManagedBean()
					.getScheduleList();

			List<ScQtProgressivePayment> paymentList = this.getManagedBean()
					.getProgressivePaymentList();

			if (null == scheduleList) {
				scheduleList = new ArrayList<ScQtSchedule>();
				if (null != resDTO.getScheduleResults()
						&& null != resDTO.getScheduleResults().getResultList()
						&& !resDTO.getScheduleResults().getResultList()
								.isEmpty()) {

					this.getManagedBean().setTotalFrequency(
							resDTO.getScheduleResults().getResultList().size());
					try {

						for (ScQtSchedule scQtSchedule : resDTO
								.getScheduleResults().getResultList()) {
							String startMonth = "";
							String endMonth = "";
							SimpleDateFormat dateFormat = new SimpleDateFormat(
									CommonConstants.DEFAULT_DATE_FORMAT);
							int months;
							Date startDate = null;
							Date endDate = null;
							String fromdate = dateFormat.format(scQtSchedule
									.getFromDate());
							String todate = dateFormat.format(scQtSchedule
									.getToDate());
							String startdate = fromdate.substring(3, 5);
							String startYear = fromdate.substring(6,
									fromdate.length());
							String fromDay = fromdate.substring(0, 2);

							String enddate = todate.substring(3, 5);
							String endYear = todate.substring(6,
									todate.length());
							String toDay = todate.substring(0, 2);

							startMonth = fulfilmentMonthlyDtls(startdate);
							endMonth = fulfilmentMonthlyDtls(enddate);

							int sYear = Integer.parseInt(startYear);
							int eYear = Integer.parseInt(endYear);

							String fromDate = fromDay + "/" + startdate + "/"
									+ sYear;
							String toDate = toDay + "/" + enddate + "/" + eYear;

							startDate = dateFormat.parse(fromDate);
							endDate = dateFormat.parse(toDate);

							months = DateUtils.getMonths(startDate, endDate);
							scQtSchedule.setTotalMonths(new BigDecimal(months));
							// scQtSchedule.setTotalMonths(new
							// BigDecimal(months));

							scQtSchedule.setStartMonth(startMonth);
							scQtSchedule.setEndMonth(endMonth);
							scQtSchedule.setStartYear(startYear);
							scQtSchedule.setEndYear(endYear);

							scheduleList.add(scQtSchedule);

						}
					} catch (ParseException e) {

					}
				}
				this.getManagedBean().setScheduleList(scheduleList);
			}

			// this.getManagedBean().setProgressivePaymentList(resDTO.getProgressivePaymentResults().getResultList());
			if (null == paymentList) {
				paymentList = new ArrayList<ScQtProgressivePayment>();
				if (null != resDTO.getProgressivePaymentResults()
						&& null != resDTO.getProgressivePaymentResults()
								.getResultList()
						&& !resDTO.getProgressivePaymentResults()
								.getResultList().isEmpty()) {

					this.getManagedBean().setTotalProgressivePayment(
							resDTO.getProgressivePaymentResults()
									.getResultList().size());
					String startMonth = "";
					String endMonth = "";
					for (ScQtProgressivePayment payment : resDTO
							.getProgressivePaymentResults().getResultList()) {
						SimpleDateFormat dateFormat = new SimpleDateFormat(
								"dd/MM/yyyy");

						String fromdate = dateFormat.format(payment
								.getFromDate());
						String todate = dateFormat.format(payment.getToDate());
						String startdate = fromdate.substring(3, 5);
						String startYear = fromdate.substring(6,
								fromdate.length());
						String enddate = todate.substring(3, 5);
						String endYear = todate.substring(6, todate.length());
						startMonth = fulfilmentMonthlyDtls(startdate);
						endMonth = fulfilmentMonthlyDtls(enddate);
						payment.setStartMonth(startMonth);
						payment.setEndMonth(endMonth);
						payment.setStartYear(startYear);
						payment.setEndYear(endYear);
						paymentList.add(payment);

					}

				}
				this.getManagedBean().setProgressivePaymentList(paymentList);
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
			}

			else if (startdate.equalsIgnoreCase("07")) {
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

	/**
	 * Retrieve delivery details.retrieve all the Delivery Details menu
	 * information and display in Specification committee.
	 */
	private void retriveDeliveryDetails() {
		log.info("retriveDeliveryDetails - START");
		IQTSpecSO specSo = null;
		try {
			if (this.getManagedBean().getDeliveryNonZoneList() == null
					&& null == this.getManagedBean().getDeliveryLocBean()) {
				SpecificationReqDTO reqDTO = new SpecificationReqDTO();
				specSo = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);
				reqDTO.setQtId(this.getManagedBean().getQt().getQtId());
				SpecificationResDTO resDTO = specSo
						.retrieveDeliverSeriviceAddress(reqDTO);
				if (resDTO.getSpecZoneDtls() == null
						|| resDTO.getSpecZoneDtls().isEmpty()) {
					List<ScQtDeliveryLoc> deliveryLoc = this.getManagedBean()
							.getDeliveryNonZoneList();
					if (deliveryLoc == null) {
						deliveryLoc = new ArrayList<ScQtDeliveryLoc>();
					}
					this.getManagedBean().setDeliveryNonZoneList(
							resDTO.getAddressResultBean());
				} else {
					this.getManagedBean().setDeliveryZones(
							resDTO.getSpecZoneDtls());

					for (TplSpecZoneDtl tplSpecZoneDtl : this.getManagedBean()
							.getDeliveryZones()) {
						this.getManagedBean()
								.setTplSpecZoneDtl(
										this.getManagedBean()
												.getDeliveryZones().get(0));
						reqDTO.setQtId(this.getManagedBean().getQt().getQtId());
						reqDTO.setZoneId(tplSpecZoneDtl.getSpecZoneId());
						resDTO = specSo.retrieveDeliveryAddressDtls(reqDTO);
						if (this.getManagedBean().getDeliveryLocBean() == null) {
							this.getManagedBean().setDeliveryLocBean(
									new ArrayList<BriefingZoneBean>());
						}
						BriefingZoneBean bean = new BriefingZoneBean(
								tplSpecZoneDtl, resDTO.getAddressResultBean());
						this.getManagedBean().getDeliveryLocBean().add(bean);
					}
				}
			}
		} finally {
			specSo = null;
		}
		log.info("retriveDeliveryDetails - END");
	}

	/**
	 * Retrieve state list.retrieve all the state list information for Delivery
	 * Addresses.
	 */
	private void retriveStateList() {
		log.info("retriveStateList - START");
		IStateSO stateSo = null;
		StateResDTO resDto = null;
		ICountrySO countrySo = null;
		this.getManagedBean().setDeliveryAddressSelect(
				new ArrayList<PmAddressType>());
		try {
			countrySo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, ICountrySO.class);
			CountryReqDTO countryReqDTo = new CountryReqDTO();
			countryReqDTo.setCountryCode(CommonConstants.DEFAULT_COUNTRY_CODE);
			CountryResDTO countryResDTO = countrySo
					.retrieveCountryByCode(countryReqDTo);

			StateReqDTO reqDTO = new StateReqDTO();
			reqDTO.setCountryId(countryResDTO.getPmCountry().getCountryId());
			stateSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IStateSO.class);
			resDto = stateSo.retriveStatesListByCountryID(reqDTO);

			if (resDto != null) {
				this.getManagedBean().setStateList(resDto.getStateList());
			}

		} finally {
			stateSo = null;
		}
		log.info("retriveStateList - END");
	}

	/**
	 * District bystate.retrieve all the District list information by state for
	 * Delivery Addresses.
	 * 
	 * @param event
	 *            (Long stateId).
	 */
	public void districtBystate(AjaxBehaviorEvent event) {
		log.info("districtBystate - START");
		IDistrictSO districtSo = null;
		DistrictResDTO resDto = null;
		try {
			Long stateId = (Long) event.getComponent().getAttributes()
					.get("value");
			DistrictReqDTO reqDTO = new DistrictReqDTO();
			if (stateId != null) {
				reqDTO.setStateId(stateId);
				List<PmState> stateList = this.getManagedBean().getStateList();
				for (PmState sate : stateList) {
					if (sate.getStateId().equals(stateId)) {
						this.getManagedBean().setStateCode(sate.getStateCode());
					}
				}
			} else {
				this.getManagedBean().setStateCode(null);
			}
			this.getManagedBean().setDistrictList(null);
			this.getManagedBean().setDivisionList(null);

			if (getManagedBean().getQt().isZonal()) {
				PmParameter zonelocationLavel = this.getManagedBean().getQt()
						.getZoneLocationLevel();
				if (null != zonelocationLavel
						&& null != zonelocationLavel.getParameterId()) {
					PmParameter parameter = getPmParameter(zonelocationLavel
							.getParameterId());
					this.getManagedBean().setLocationLevel(
							parameter.getParameterCode());
				}
				String locationLevel = this.getManagedBean().getLocationLevel();
				List<String> codes = new ArrayList<String>();
				List<PmState> pmStates = new ArrayList<PmState>();
				List<PmDistrict> districts = new ArrayList<PmDistrict>();
				List<PmDivision> divisions = new ArrayList<PmDivision>();

				if (null != this.getManagedBean().getTplSpecZoneDtl()) {
					for (TplSpecZoneLoc specZoneLoc : this.getManagedBean()
							.getTplSpecZoneDtl().getTplSpecZoneLocs()) {
						if (ParameterConst.LOCATION_LEVEL_STATE
								.equalsIgnoreCase(locationLevel)) {
							PmState pmState = specZoneLoc.getPmState();
							if (null != pmState) {
								if (!codes.contains(pmState.getStateCode())) {
									codes.add(pmState.getStateCode());
									pmStates.add(pmState);
								}
							}
							this.getManagedBean().setStateList(pmStates);
						} else if (ParameterConst.LOCATION_LEVEL_DISTRICT
								.equalsIgnoreCase(locationLevel)) {
							if (getManagedBean().getStateCode().equals(
									specZoneLoc.getPmState().getStateCode())) {
								PmDistrict district = specZoneLoc
										.getPmDistrict();
								if (null != district) {
									if (!codes.contains(district
											.getDistrictCode())) {
										codes.add(district.getDistrictCode());
										districts.add(district);
									}
								}
							}
							this.getManagedBean().setDistrictList(districts);
						} else if (ParameterConst.LOCATION_LEVEL_DIVISION
								.equalsIgnoreCase(locationLevel)) {
							PmDivision divison = specZoneLoc.getPmDivision();
							if (null != divison) {
								if (!codes.contains(divison.getDivisionCode())) {
									codes.add(divison.getDivisionCode());
									divisions.add(divison);
								}
							}
							this.getManagedBean().setDivisionList(divisions);
						}
					}
				}
			} else {
				if (ParameterConst.SARAK_SK_CODE.equalsIgnoreCase(this
						.getManagedBean().getStateCode())) {
					this.getManagedBean().setDivisionList(
							PmCommonUtil.retrievePmDivisionList(stateId));
				} else {
					districtSo = BusinessDelegate.getInstance()
							.getServiceObject(
									ModuleCodeConst.PROFILE_MANAGEMENT,
									IDistrictSO.class);
					resDto = districtSo.retriveDistrictListByStateID(reqDTO);
					if (resDto != null) {
						this.getManagedBean().setDistrictList(
								resDto.getDistrictList());
					}
				}
			}
		} finally {
			districtSo = null;
		}
		log.info("districtBystate - END");
	}

	/**
	 * @param parameterId
	 * @return
	 */
	private PmParameter getPmParameter(Long parameterId) {
		IParameterSO parameterSO = BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IParameterSO.class);
		ParameterReqDTO parameterReqDTO = new ParameterReqDTO();
		parameterReqDTO.setParameterId(parameterId);
		return parameterSO.retrieveParameterById(parameterReqDTO)
				.getPmParameter();

	}

	/**
	 * Retrieve division district.retrieve all the District list information by
	 * division for Delivery Addresses.
	 * 
	 * @param event
	 *            (Long divisionId).
	 */
	public void retriveDivisionDistrict(AjaxBehaviorEvent event) {
		IDistrictSO districtSo = null;
		DistrictResDTO resDto = null;
		try {
			DistrictReqDTO reqDTO = new DistrictReqDTO();
			Long divisionId = (Long) event.getComponent().getAttributes()
					.get("value");
			if (divisionId != null) {
				reqDTO.setDivisionId(divisionId);
				List<PmDivision> divisionList = this.getManagedBean()
						.getDivisionList();
				for (PmDivision division : divisionList) {
					if (division.getDivisionId().equals(divisionId)) {
						this.getManagedBean().setDivisionCode(
								division.getDivisionCode());

					}
				}
			} else {
				this.getManagedBean().setDivisionCode(null);
			}
			this.getManagedBean().setDistrictList(null);
			districtSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IDistrictSO.class);
			resDto = districtSo.retriveDistrictListByDivision(reqDTO);

			this.getManagedBean().setDistrictList(resDto.getDistrictList());

		} finally {
			districtSo = null;
		}

	}

	/**
	 * District code.set District code for Delivery Address Search.
	 * 
	 * @param event
	 *            (Long districtId)
	 */
	public void districtCode(AjaxBehaviorEvent event) {
		try {
			Long districtId = (Long) event.getComponent().getAttributes()
					.get("value");
			if (null != districtId) {
				List<PmDistrict> districtList = this.getManagedBean()
						.getDistrictList();
				for (PmDistrict district : districtList) {
					if (district.getDistrictId().equals(districtId)) {
						this.getManagedBean().setDistrictCode(
								district.getDistrictCode());

					}

				}

			} else {
				this.getManagedBean().setDistrictCode(null);
			}

		} finally {

		}

	}

	/**
	 * Retrieve ministry list.retrieve all the Ministry list information for
	 * Delivery Addresses.
	 */
	private void retriveMinistryList() {
		IOrgProfileSO orgProfileSO = null;
		try {
			OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
			reqDTO.setOrgType(OrganizationType.MINISTRY.value());
			reqDTO.setParentOrgProfileId(null);
			orgProfileSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IOrgProfileSO.class);
			OrgProfileResDTO resDTO = orgProfileSO
					.retrieveOrgProfileListByOrgType(reqDTO);
			if (resDTO.getOrgProfileList() != null) {
				this.getManagedBean().setMinistryList(
						resDTO.getOrgProfileList());
			}
		} finally {
			orgProfileSO = null;
		}
	}

	/**
	 * De part ment by ministry.retrive PTJ Group List by mininstry for Delivery
	 * Addresses.
	 * 
	 * @param event
	 *            (Long ministryId).
	 */

	public void retrievePTJGroupListByMinistry(Long ministryId) {
		IOrgProfileSO orgProfileSO = null;
		try {

			if (null != ministryId) {
				OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
				reqDTO.setOrgType(OrganizationType.DEPARTMENT.value());
				reqDTO.setParentOrgProfileId(ministryId);
				orgProfileSO = BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
								IOrgProfileSO.class);
				/*
				 * OrgProfileResDTO resDTO = orgProfileSO
				 * .retrievePTJGroupListByMinistry(reqDTO);
				 */
				OrgProfileResDTO resDTO = orgProfileSO
						.retrieveOrgProfileListByOrgType(reqDTO);
				this.getManagedBean().setPtjtList(new ArrayList<Object[]>());

				this.getManagedBean().setDepartmentList(
						resDTO.getOrgProfileList());

			} else {
				this.getManagedBean().setPtjtList(new ArrayList<Object[]>());

				this.getManagedBean().setDepartmentList(
						new ArrayList<PmOrgProfile>());
			}

		} finally {
			orgProfileSO = null;
		}

	}

	/**
	 * Ptj by depart ment.retrive Ptj List by PTJ Group for Delivery Addresses.
	 * 
	 * @param departmentd
	 *            the departmentd
	 */
	public void retrivePTJByPTJGroup(Long ptjGroupId) {
		IOrgProfileSO orgProfileSO = null;
		try {

			if (null != ptjGroupId) {
				orgProfileSO = BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
								IOrgProfileSO.class);
				OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
				reqDTO.setParentOrgProfileId(ptjGroupId);
				reqDTO.setOrgCode(OrganizationType.DIVISION.value());
				/*
				 * OrgProfileResDTO resDTO = orgProfileSO
				 * .retrievePTJListByMinistry(reqDTO);
				 */

				OrgProfileResDTO resDTO = orgProfileSO
						.retrieveChildDDListByParentOrgProfileId(reqDTO);
				if (null != resDTO.getOrgProfileDDList()) {
					this.getManagedBean().setPtjtList(
							resDTO.getOrgProfileDDList());
				}
			} else {
				this.getManagedBean().setPtjtList(new ArrayList<Object[]>());
			}

		} finally {
			orgProfileSO = null;
		}

	}

	/**
	 * Retrieve committee endorsment.retrieve all the Committee Endorsmnet menu
	 * information and display in Specification committee.
	 */
	private void retriveCommitteeEndorsment() {
		ICommitteSO committeSO = null;
		CommitteResDTO resDTO = null;
		IQTDetailSO qtDetailSO;
		QuationTenderResDTO resDto;

		try {

			CommitteReqDTO reqDTO = new CommitteReqDTO();
			committeSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, ICommitteSO.class);
			reqDTO.setQtId(this.getManagedBean().getQt().getQtId());
			reqDTO.setCommitteeType(ParameterConst.COMMITTEE_TYPE_SPECIFICATION_COMMITTEE);
			resDTO = committeSO.retrieveCommitteActivityDtls(reqDTO);
			QuationTenderReqDTO qtReqDTO = new QuationTenderReqDTO();
			qtReqDTO.setQtId(this.getManagedBean().getQt().getQtId());
			qtReqDTO.setDocType(ParameterConst.DOCTYPE_SPEC_COMMITTE);
			qtDetailSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);

			resDto = qtDetailSO.retriveQtRemarks(qtReqDTO);

			if (resDto.getRemarks() == null) {
				if (null == this.getManagedBean().getSpecificationRemarks()) {
					this.getManagedBean().setSpecificationRemarks(
							new ArrayList<ScQtSpecificationRemark>());
					if (null != this.getManagedBean().getCurrentStatusId()
							&& (this.getManagedBean()
									.getCurrentStatusId()
									.equals(QTStatusConst.QT_PENDING_CHECKLIST_SCORING_ENDORSEMENT)
									|| this.getManagedBean()
											.getCurrentStatusId()
											.equals(QTStatusConst.QT_PENDING_MSC_APPROVAL) || this
									.getManagedBean()
									.getCurrentStatusId()
									.equals(QTStatusConst.QT_PENDING_CHECKLIST_SCORING_REVISION))) {
						initializeRemarks(this.getManagedBean().getQt(), this
								.getManagedBean().getQt()
								.getCurrentWorkFlowStatus(), this
								.getManagedBean().getSpecificationRemarks(),
								ScQtSpecificationRemark.class);
					}
				}
			}

			else {

				List<ScQtSpecificationRemark> remarkList = new ArrayList<ScQtSpecificationRemark>();
				for (ScRemark remark : resDto.getRemarks()) {
					ScQtSpecificationRemark qtRemark = (ScQtSpecificationRemark) remark;
					remarkList.add(qtRemark);

				}
				if (null == this.getManagedBean().getSpecificationRemarks()
						|| this.getManagedBean().getSpecificationRemarks()
								.isEmpty()) {
					this.getManagedBean().setSpecificationRemarks(remarkList);
					if (null != this.getManagedBean().getCurrentStatusId()
							&& (this.getManagedBean()
									.getCurrentStatusId()
									.equals(QTStatusConst.QT_PENDING_CHECKLIST_SCORING_ENDORSEMENT)
									|| this.getManagedBean()
											.getCurrentStatusId()
											.equals(QTStatusConst.QT_PENDING_MSC_APPROVAL) || this
									.getManagedBean()
									.getCurrentStatusId()
									.equals(QTStatusConst.QT_PENDING_CHECKLIST_SCORING_REVISION))) {
						initializeRemarks(this.getManagedBean().getQt(), this
								.getManagedBean().getQt()
								.getCurrentWorkFlowStatus(), this
								.getManagedBean().getSpecificationRemarks(),
								ScQtSpecificationRemark.class);
					}
				}
			}

			this.getManagedBean().setCommitteeActivities(
					resDTO.getCommitteActivities());

		} finally {
			committeSO = null;
			resDTO = null;
		}
	}

	/**
	 * Retrieve parameter desc by type.
	 * 
	 * @param type
	 *            (String type)
	 * @returnList<PmParameterDesc>
	 */
	public List<PmParameterDesc> retrieveParameterDescByType(String type) {
		return ParameterHelper.getInstance().getParameterDescList(type);

	}

	/**
	 * Gets the locations.convert Treenode to Location Criteria objects in
	 * supplier qualification criteria.
	 * 
	 * @param zoneLocation
	 *            (TreeNode zoneLocation)
	 * @return List<ScQtLocCriteria>
	 */
	private List<ScQtLocCriteria> getLocations(TreeNode zoneLocation) {
		List<ScQtLocCriteria> results = new ArrayList<ScQtLocCriteria>();
		if (zoneLocation.isLeaf()) {
			ScQtLocCriteria locationDTO = (ScQtLocCriteria) zoneLocation
					.getData();
			results.add(locationDTO);
		} else {
			for (TreeNode childrendLocationNode : zoneLocation.getChildren()) {
				results.addAll(getLocations(childrendLocationNode));
			}
		}
		return results;
	}

	/**
	 * Gets the zone locations. convert Treenode to TplSpecZoneLoc objects in
	 * general information.
	 * 
	 * @param zoneLocation
	 *            (TreeNode zoneLocation)
	 * @return List<TplSpecZoneLoc>
	 */
	private List<TplSpecZoneLoc> getZoneLocations(TreeNode zoneLocation) {

		List<TplSpecZoneLoc> results = new ArrayList<TplSpecZoneLoc>();
		if (zoneLocation.isLeaf()) {
			TplSpecZoneLoc locationDTO = (TplSpecZoneLoc) zoneLocation
					.getData();
			results.add(locationDTO);
		} else {
			for (TreeNode childrendLocationNode : zoneLocation.getChildren()) {
				results.addAll(getZoneLocations(childrendLocationNode));
			}
		}

		return results;
	}

	/**
	 * Edits the supplier locations.click pencil icon in supplier qualification
	 * criteria navigate to Locations Popup.
	 * 
	 * @param rootZoneLocation
	 *            (TreeNode rootZoneLocation)
	 * @param index
	 *            (int index)
	 */
	public void editSupplierLocations(TreeNode rootZoneLocation, int index) {
		IParameterSO parameterSo = null;
		ICountrySO countrySo = null;

		try {

			parameterSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
			countrySo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, ICountrySO.class);

			List<ScQtLocCriteria> locationDTOs = new ArrayList<ScQtLocCriteria>();
			if (getManagedBean().getZoneTrees() != null) {
				for (TreeNode treeNode : getManagedBean().getZoneTrees()) {
					List<ScQtLocCriteria> locationDTO = getLocations(treeNode);
					locationDTOs.addAll(locationDTO);
				}
			}

			// List<ScQtLocCriteria> locationDTOs =
			// getLocations(rootZoneLocation);
			Long locationLevel = this.getManagedBean().getQt()
					.getScQtSupplierCriteria().getLocationLevel()
					.getParameterId();

			if (locationLevel == null) {
				ParameterReqDTO districtReqDTO = new ParameterReqDTO();
				districtReqDTO
						.setParameterType(ParameterTypeConst.LOCATION_LEVEL);
				districtReqDTO
						.setParameterCode(ParameterConst.LOCATION_LEVEL_DISTRICT);
				ParameterResDTO districtResDTO = parameterSo
						.retrieveParameterByTypeAndCode(districtReqDTO);

				locationLevel = districtResDTO.getPmParameter()
						.getParameterId();
			}

			List<PmParameterDesc> localParamDesc = retrieveParameterDescByType(ParameterTypeConst.LOCATION_LEVEL);
			String locationLevelCode = null;
			for (PmParameterDesc paramDesc : localParamDesc) {

				if (paramDesc.getPmParameter().getParameterId()
						.equals(locationLevel)) {
					locationLevelCode = paramDesc.getPmParameter()
							.getParameterCode();
					this.getManagedBean().setLocalLevelCode(locationLevelCode);

					break;
				}
			}

			// ScQuoteZone zone = getZone(rootZoneLocation);
			List<PmState> states = SourcingLocationHelper
					.getStateFromLocationDTO(locationDTOs);
			List<PmDivision> divisions = SourcingLocationHelper
					.getDivisionFromLocationDTO(locationDTOs);
			List<PmDistrict> districts = SourcingLocationHelper
					.getDistrictFromLocationDTO(locationDTOs);
			List<PmCity> cities = SourcingLocationHelper
					.getCityFromLocationDTO(locationDTOs);
			CountryReqDTO countryReqDTo = new CountryReqDTO();
			countryReqDTo.setCountryCode(CommonConstants.DEFAULT_COUNTRY_CODE);
			CountryResDTO countryResDTO = countrySo
					.retrieveCountryByCode(countryReqDTo);
			getManagedBean().setRootLocationTree(
					LocationSingleton.getInstance().buildLocationPopupTree(
							countryResDTO.getPmCountry().getCountryId(),
							states, districts, divisions, cities,
							locationLevelCode, ScQtLocCriteria.class));

			// this.getManagedBean().setIndex(i);
		} finally {
			parameterSo = null;
		}

		// getManagedBean().setZoneName(zone.getZoneName());

	}

	/**
	 * Edits the zones.click pencil icon in general information menu navigate to
	 * ZoneLocation Popup.
	 * 
	 * @param rootZoneLocation
	 *            (TreeNode rootZoneLocation)
	 * @param index
	 *            (int index).
	 */
	public void editZones(TreeNode rootZoneLocation, int index) {
		IParameterSO parameterSo = null;
		ICountrySO countrySo = null;
		try {
			parameterSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
			countrySo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, ICountrySO.class);
			List<TplSpecZoneLoc> locationDTOs = getZoneLocations(rootZoneLocation);

			// TplSpecZoneDtl zone = getZone(rootZoneLocation);
			Long locationLevel = this.getManagedBean().getQt()
					.getZoneLocationLevel().getParameterId();
			if (locationLevel == null) {
				ParameterReqDTO districtReqDTO = new ParameterReqDTO();
				districtReqDTO
						.setParameterType(ParameterTypeConst.LOCATION_LEVEL);
				districtReqDTO
						.setParameterCode(ParameterConst.LOCATION_LEVEL_DISTRICT);
				ParameterResDTO districtResDTO = parameterSo
						.retrieveParameterByTypeAndCode(districtReqDTO);

				locationLevel = districtResDTO.getPmParameter()
						.getParameterId();
			}

			List<PmParameterDesc> localParamDesc = retrieveParameterDescByType(ParameterTypeConst.LOCATION_LEVEL);
			String locationLevelCode = null;
			for (PmParameterDesc paramDesc : localParamDesc) {

				if (paramDesc.getPmParameter().getParameterId()
						.equals(locationLevel)) {
					locationLevelCode = paramDesc.getPmParameter()
							.getParameterCode();

					break;
				}
			}

			List<PmState> states = SourcingLocationHelper
					.getStateFromLocationDTO(locationDTOs);

			List<PmDivision> divisions = SourcingLocationHelper
					.getDivisionFromLocationDTO(locationDTOs);
			List<PmDistrict> districts = SourcingLocationHelper
					.getDistrictFromLocationDTO(locationDTOs);
			List<PmCity> cities = SourcingLocationHelper
					.getCityFromLocationDTO(locationDTOs);
			CountryReqDTO countryReqDTo = new CountryReqDTO();
			countryReqDTo.setCountryCode(CommonConstants.DEFAULT_COUNTRY_CODE);
			CountryResDTO countryResDTO = countrySo
					.retrieveCountryByCode(countryReqDTo);
			getManagedBean().setRootZoanlTree(
					LocationSingleton.getInstance().buildLocationPopupTree(
							countryResDTO.getPmCountry().getCountryId(),
							states, districts, divisions, cities,
							locationLevelCode, TplSpecZoneLoc.class));
			int i = index;
			this.getManagedBean().setAutoUpdate(false);
			this.getManagedBean().setIndex(i);
			getManagedBean().setZoneName(
					locationDTOs.get(0).getTplSpecZoneDtl().getZoneName());
			getManagedBean().setTempZoneName(
					locationDTOs.get(0).getTplSpecZoneDtl().getZoneName());
		} finally {

		}

	}

	/**
	 * Close listener.close popup
	 */
	public void closeListener() {
		getManagedBean().setRequired(Boolean.FALSE);
		//this.getManagedBean().setZoneLocation(Boolean.FALSE);
		this.getManagedBean().setAutoUpdate(true);
	}

	/**
	 * Technical spec bo validate.Validate Template spec Popup.
	 */
	public void technicalSpecBoValidate() {
		getManagedBean().setRequired(Boolean.TRUE);
		this.getManagedBean().setAutoUpdate(false);
		if (null != getManagedBean().getQt().getPmProcurementCategory()
				&& null != getManagedBean().getQt().getPmProcurementCategory()
						.getParameterId()) {
			getManagedBean().setSearchProcurementId(
					getManagedBean().getQt().getPmProcurementCategory()
							.getParameterId());
		}

	}

	/**
	 * Gets the dropdown location values.display the Location level dropdown
	 * values.
	 * 
	 * @param locationLevel
	 *            the location level
	 * @return the dropdown location values
	 */
	public SelectItem[] getDropdownLocationValues(String locationLevel,
			ScQt scQt) {
		IOrgProfileSO orgProfile = BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IOrgProfileSO.class);
		OrgProfileResDTO orgResDto = orgProfile
				.retrieveOrgStateCodeByOrgProfileId(scQt.getPmOrgProfile()
						.getOrgProfileId());
		this.getManagedBean().setAllStateCode(orgResDto.getStateStatusId());
		List<String> types = new ArrayList<String>();
		types.add(ParameterConst.LOCATION_LEVEL_STATE);
		if (this.getManagedBean().getAllStateCode() == PTJLocalityCode.STATE_SARAWAK) {
			types.add(ParameterConst.LOCATION_LEVEL_DIVISION);
		}
		types.add(ParameterConst.LOCATION_LEVEL_DISTRICT);

		SelectItem[] items = null;
		try {
			items = new SelectItem[types.size() + 1];
			items[0] = new SelectItem(null, ResourceBundleUtils.getMessage(
					"lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					new FacesScopes().getUserLocale()));
			int i = 1;
			for (String location : types) {
				List<PmParameterDesc> localParamDesc = retrieveParameterDescByType(locationLevel);

				for (PmParameterDesc paramDesc : localParamDesc) {
					if (paramDesc.getPmParameter().getParameterCode()
							.equals(location)) {

						items[i++] = new SelectItem(paramDesc.getPmParameter()
								.getParameterId(), paramDesc.getCodeName());
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(),
							e.getMessage()));
		}
		return items;
		// return getDropdownCodeValues(committeeRole);
	}

	/**
	 * To get a list of users (DO+BO) for dropdown
	 * 
	 * @return the list of PmUser instance
	 */
	public List<PmUser> getCreatedByDropdownValues() {
		IUserSO userSO = null;
		List<PmUser> users = new ArrayList<PmUser>();

		try {
			userSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
			ScQt scQt = getManagedBean().getQt();

			// Get Desk Officer User
			UserReqDTO reqDTO = new UserReqDTO();
			reqDTO.setUserId(scQt.getCreatedBy());
			PmUser doUser = userSO.retrieveUserByUserId(reqDTO).getPmUser();
			if (doUser != null) {
				users.add(doUser);
			}

			// Get Specification Initiator Users
			reqDTO = new UserReqDTO();
			reqDTO.setRoleCode(RoleCode.BIZ_OWNER.value());
			reqDTO.setOrgProfileId(scQt.getPmOrgProfile().getOrgProfileId());
			List<PmUser> boUsers = userSO.retrieveUserListByRole(reqDTO)
					.getUserList();
			if (boUsers != null && boUsers.size() > 0) {
				users.addAll(boUsers);
			}

		} finally {
			userSO = null;
		}

		return users;
	}

	/**
	 * Show supplier location popup.Event handle navigate to the Supplier
	 * Locations Popup.
	 */
	public void showSupplierLocationPopup() {

		ICountrySO countrySo = null;
		try {

			countrySo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, ICountrySO.class);

			Long locationLevel = this.getManagedBean().getQt()
					.getScQtSupplierCriteria().getLocationLevel()
					.getParameterId();

			if (locationLevel == null) {

				throw new EpException(MessageConst.ERROR_760063);
			}

			List<PmParameterDesc> localParamDesc = retrieveParameterDescByType(ParameterTypeConst.LOCATION_LEVEL);

			for (PmParameterDesc paramDesc : localParamDesc) {

				if (paramDesc.getPmParameter().getParameterId()
						.equals(locationLevel)) {
					this.getManagedBean().setLocalLevelCode(
							paramDesc.getPmParameter().getParameterCode());
					CountryReqDTO countryReqDTo = new CountryReqDTO();
					countryReqDTo
							.setCountryCode(CommonConstants.DEFAULT_COUNTRY_CODE);
					CountryResDTO countryResDTO = countrySo
							.retrieveCountryByCode(countryReqDTo);

					List<ScQtLocCriteria> locationDTOs = new ArrayList<ScQtLocCriteria>();
					if (getManagedBean().getZoneTrees() != null) {
						for (TreeNode treeNode : getManagedBean()
								.getZoneTrees()) {
							List<ScQtLocCriteria> locationDTO = getLocations(treeNode);
							locationDTOs.addAll(locationDTO);
						}
					}

					List<PmState> states = SourcingLocationHelper
							.getStateFromLocationDTO(locationDTOs);
					List<PmDivision> divisions = SourcingLocationHelper
							.getDivisionFromLocationDTO(locationDTOs);
					List<PmDistrict> districts = SourcingLocationHelper
							.getDistrictFromLocationDTO(locationDTOs);
					getManagedBean().setRootLocationTree(
							LocationSingleton.getInstance()
									.buildLocationPopupTree(
											countryResDTO.getPmCountry()
													.getCountryId(),
											states,
											districts,
											divisions,
											null,
											paramDesc.getPmParameter()
													.getParameterCode(),
											ScQtLocCriteria.class));
					getManagedBean().setIndex(-1);
				}

			}
			RequestContext.getCurrentInstance().execute("ZoneLocation.show()");
		} finally {

		}
	}

	/**
	 * Show general zones popup.Event handle navigate to the ZoneLocations
	 * Popup.
	 */
	public void showGeneralZonesPopup() {

		ICountrySO countrySo = null;

		try {
			Long locationLevel = this.getManagedBean().getQt()
					.getZoneLocationLevel().getParameterId();

			countrySo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, ICountrySO.class);
			if (locationLevel == null) {

				throw new EpException(MessageConst.ERROR_760063);
			}

			List<PmParameterDesc> localParamDesc = retrieveParameterDescByType(ParameterTypeConst.LOCATION_LEVEL);

			for (PmParameterDesc paramDesc : localParamDesc) {

				if (paramDesc.getPmParameter().getParameterId()
						.equals(locationLevel)) {
					//this.getManagedBean().setZoneLocation(true);
					this.getManagedBean().setStateCode(
							paramDesc.getPmParameter().getParameterCode());
					CountryReqDTO countryReqDTo = new CountryReqDTO();
					countryReqDTo
							.setCountryCode(CommonConstants.DEFAULT_COUNTRY_CODE);
					CountryResDTO countryResDTO = countrySo
							.retrieveCountryByCode(countryReqDTo);
					getManagedBean().setRootZoanlTree(
							LocationSingleton.getInstance()
									.buildLocationPopupTree(
											countryResDTO.getPmCountry()
													.getCountryId(),
											null,
											null,
											null,
											null,
											paramDesc.getPmParameter()
													.getParameterCode(),
											TplSpecZoneLoc.class));
					this.getManagedBean().setIndex(-1);
					this.getManagedBean().setZoneName("");
				}

			}
			this.getManagedBean().setAutoUpdate(false);
			RequestContext.getCurrentInstance().execute("ZoneGeneral.show()");
		} finally {

		}

	}

	/**
	 * Update supplier locations.Event handle add Locations to Supplier
	 * qualification criteria menu.
	 */
	public void updateSupplierLocations() {
		List<ScQtLocCriteria> locationDTOs = new ArrayList<ScQtLocCriteria>();
		this.getManagedBean().setSupplierLocationList(
				getManagedBean().getExistingSupplierLocationList());
		if (null == this.getManagedBean().getTotalSuppliersSpecLoc()) {
			this.getManagedBean().setTotalSuppliersSpecLoc(
					new ArrayList<TplSpecLoc>());
		}
		List<TplSpecLoc> specLoc = new ArrayList<TplSpecLoc>();
		if (getManagedBean().getSelectedNodes() != null) {
			for (TreeNode treeNode : getManagedBean().getSelectedNodes()) {
				if (!treeNode.getParent().isSelected()) {
					List<ScQtLocCriteria> locationDTO = getLocations(treeNode);
					for (ScQtLocCriteria locCritera : locationDTO) {
						TplSpecLoc localSpecLoc = (TplSpecLoc) locCritera;
						specLoc.add(localSpecLoc);
					}
					this.getManagedBean().getTotalSuppliersSpecLoc()
							.addAll(specLoc);
					locationDTOs.addAll(locationDTO);
				} else {
					List<ScQtLocCriteria> locationDTO = getLocations(treeNode);
					for (ScQtLocCriteria locCritera : locationDTO) {
						TplSpecLoc localSpecLoc = (TplSpecLoc) locCritera;
						specLoc.add(localSpecLoc);
					}
					this.getManagedBean().getTotalSuppliersSpecLoc()
							.addAll(specLoc);
					locationDTOs.addAll(locationDTO);
				}
			}
		}
		retrievLocationDtls(locationDTOs);
		log.info("updateZoneLocation - END");
	}

	/**
	 * Gets the pm state ids.
	 * 
	 * @param tplSpecZoneLocs
	 *            (List<TplSpecZoneLoc> tplSpecZoneLocs)
	 * @return (List<Long>).
	 */
	private List<Long> getPmStateIds(List<TplSpecZoneLoc> tplSpecZoneLocs) {
		List<Long> results = new ArrayList<Long>();

		if (tplSpecZoneLocs != null && tplSpecZoneLocs.size() > 0) {
			for (TplSpecZoneLoc TplSpecZoneLoc : tplSpecZoneLocs) {

				if (TplSpecZoneLoc.getPmState() != null) {
					results.add(TplSpecZoneLoc.getPmState().getStateId());
				}
			}
		}
		return results;
	}

	/**
	 * Gets the pm division ids.
	 * 
	 * @param tplSpecZoneLocs
	 *            (List<TplSpecZoneLoc> tplSpecZoneLocs)
	 * @return (List<Long>).
	 */
	private List<Long> getPmDivisionIds(List<TplSpecZoneLoc> tplSpecZoneLocs) {
		List<Long> results = new ArrayList<Long>();

		if (tplSpecZoneLocs != null && tplSpecZoneLocs.size() > 0) {
			for (TplSpecZoneLoc TplSpecZoneLoc : tplSpecZoneLocs) {

				if (TplSpecZoneLoc.getPmDivision() != null) {
					results.add(TplSpecZoneLoc.getPmDivision().getDivisionId());
				}
			}
		}
		return results;
	}

	/**
	 * Gets the pm districts ids.
	 * 
	 * @param tplSpecZoneLocs
	 *            (List<TplSpecZoneLoc> tplSpecZoneLocs)
	 * @return (List<Long>).
	 */
	private List<Long> getPmDistrictsIds(List<TplSpecZoneLoc> tplSpecZoneLocs) {
		List<Long> results = new ArrayList<Long>();

		if (tplSpecZoneLocs != null && tplSpecZoneLocs.size() > 0) {
			for (TplSpecZoneLoc TplSpecZoneLoc : tplSpecZoneLocs) {

				if (TplSpecZoneLoc.getPmDistrict() != null) {
					results.add(TplSpecZoneLoc.getPmDistrict().getDistrictId());
				}
			}
		}
		return results;
	}

	/**
	 * Delete state or div or dist.
	 * 
	 * @param tplSpecZoneDtl
	 *            (TplSpecZoneDtl tplSpecZoneDtl)
	 * @param tplSpecZoneLocs
	 *            (List<TplSpecZoneLoc> tplSpecZoneLocs)
	 */
	private void deleteStateOrDivOrDist(TplSpecZoneDtl tplSpecZoneDtl,
			List<TplSpecZoneLoc> tplSpecZoneLocs) {
		List<Long> stateIds = null;
		List<Long> divIds = null;
		List<Long> distIds = null;

		if (tplSpecZoneLocs != null && tplSpecZoneLocs.size() > 0) {
			stateIds = getPmStateIds(tplSpecZoneLocs);

			divIds = getPmDivisionIds(tplSpecZoneLocs);

			distIds = getPmDistrictsIds(tplSpecZoneLocs);
		}

		List<ZonesBean> results = new ArrayList<ZonesBean>();
		List<ZonesBean> list = getManagedBean().getBeans();
		if (list != null && list.size() > 0) {
			for (ZonesBean zonesBean : list) {
				ZonesBean bean = new ZonesBean();
				TplSpecZoneDtl zone = zonesBean.getTplSpecZoneDtl();

				if (zone != null) {

					if (tplSpecZoneDtl != null) {
						if (!zone.getZoneName().equalsIgnoreCase(
								tplSpecZoneDtl.getZoneName())) {
							bean = zonesBean;
							results.add(bean);
						}

					} else {
						if (zonesBean.getTplSpecZoneLocs() != null
								&& zonesBean.getTplSpecZoneLocs().size() > 0) {
							List<TplSpecZoneLoc> specZoneLocs = new ArrayList<TplSpecZoneLoc>();
							for (TplSpecZoneLoc TplSpecZoneLoc : zonesBean
									.getTplSpecZoneLocs()) {

								if (TplSpecZoneLoc.getPmState() != null) {

									if (stateIds != null && stateIds.size() > 0) {

										if (!stateIds.contains(TplSpecZoneLoc
												.getPmState().getStateId())) {
											specZoneLocs.add(TplSpecZoneLoc);
										}
									}

								} else if (TplSpecZoneLoc.getPmDivision() != null) {
									if (divIds != null && divIds.size() > 0) {
										if (!divIds.contains(TplSpecZoneLoc
												.getPmDivision()
												.getDivisionId())) {
											specZoneLocs.add(TplSpecZoneLoc);
										}
									}
								} else if (TplSpecZoneLoc.getPmDistrict() != null) {
									if (distIds != null && distIds.size() > 0) {
										if (!distIds.contains(TplSpecZoneLoc
												.getPmDistrict()
												.getDistrictId())) {
											specZoneLocs.add(TplSpecZoneLoc);
										}
									}
								}
							}
							bean.setTplSpecZoneLocs(specZoneLocs);
						}
						results.add(bean);
					}
				}
			}
			getManagedBean().setBeans(results);
		}
	}

	/**
	 * Prepare spec req.Prepare Data for Business Owner
	 * save/Submit/Accept/Revise buttons.
	 * 
	 * @return the specification req dto
	 */

	private SpecificationReqDTO prepareSpecReq() {

		SpecificationReqDTO reqDTO = new SpecificationReqDTO();

		ScQt scQt = this.getManagedBean().getQt();
		scQt.setScQtStatus(null);
		scQt.setMaxProposal(1);
		scQt.setScheduleType("F");
		ScQtSupplierCriteria qtSupplierCriteria = scQt
				.getScQtSupplierCriteria();

		if (!scQt.isPanel() && null != qtSupplierCriteria) {
			if (scQt.isZonal()
					&& scQt.getQtType().equals(ParameterConst.QUOTATION_TENDER_TYPE_OPEN)) {

				List<TplSpecZoneDtl> businessOwnerZone = convertTreeNodesToZoneCriteria(
						this.getManagedBean().getZoneGeneralTrees(), null);

				if (businessOwnerZone != null && !businessOwnerZone.isEmpty()) {
					// TODO Code needs to be remove
					setUserDetails(qtSupplierCriteria.getQtZoneCriterias());
					List<TplSpecZoneDtl> zoneLocal = new ArrayList<TplSpecZoneDtl>();
					for (TplSpecZoneDtl tplSpecZoneDtl : businessOwnerZone) {
						qtSupplierCriteria.getQtZoneCriterias().setModified(
								true);
						tplSpecZoneDtl.setTplSpecZone(qtSupplierCriteria
								.getQtZoneCriterias());
						zoneLocal.add(tplSpecZoneDtl);
					}
					qtSupplierCriteria.getQtZoneCriterias()
							.setScQtSupplierCriteria(qtSupplierCriteria);
					qtSupplierCriteria.getQtZoneCriterias().setTplSpecZoneDtls(
							zoneLocal);
				} else {
					if (null != scQt.getScQtSupplierCriteria()
							.getQtZoneCriterias()) {
						qtSupplierCriteria.getQtZoneCriterias()
								.setTplSpecZoneDtls(null);

					}
				}
				qtSupplierCriteria.setScQt(scQt);
				// TODO Need to remove below code...
				setUserDetails(qtSupplierCriteria);
				scQt.setScQtSupplierCriteria(qtSupplierCriteria);
			}
			if (ParameterConst.QUOTATION_TENDER_TYPE_OPEN.equals(scQt.getQtType())
					&& !scQt.getScQtSupplierCriteria().isAllState()) {
				List<ScQtLocCriteria> locCriteria = (List<ScQtLocCriteria>) convertTreeNodesToLocationCriteria(
						this.getManagedBean().getZoneTrees(),
						qtSupplierCriteria);
				if (locCriteria != null && !locCriteria.isEmpty()) {
					scQt.getScQtSupplierCriteria().setScQtLocCriteria(
							locCriteria);
				} else {
					scQt.getScQtSupplierCriteria().setScQtLocCriteria(null);
				}
			}

			if (ParameterConst.QUOTATION_TENDER_TYPE_OPEN.equals(scQt.getQtType())
					&& ParameterConst.PROCUREMENT_MODE_TENDER.equals(scQt
							.getProcurementMode().getParameterCode())
					&& this.getManagedBean().getQt().getScQtSupplierCriteria()
							.isAllState()) {
				if (null != qtSupplierCriteria) {

					qtSupplierCriteria.setLocationLevel(null);
					qtSupplierCriteria.setScQtLocCriteria(null);
				}
			}

			qtSupplierCriteria.setScQt(scQt);
			// TODO Need to remove below code...
			setUserDetails(qtSupplierCriteria);
			scQt.setScQtSupplierCriteria(qtSupplierCriteria);

			if (ParameterConst.QUOTATION_TENDER_TYPE_OPEN.equalsIgnoreCase(scQt.getQtType())) {
				List<ScQtCategory> openList = this.getManagedBean()
						.getQtCategoryList();
				if (null != openList && !openList.isEmpty()) {
					scQt.setScQtCategorys(openList);
				}
			} else if (ParameterConst.QUOTATION_TENDER_TYPE_RESTRICTED_TENDER.equalsIgnoreCase(scQt
					.getQtType())) {
				scQt.setZonal(false);
				scQt.setZoneLocationLevel(null);
				if (null != scQt.getScQtSupplierCriteria()) {
					scQt.getScQtSupplierCriteria().setAllState(false);
					scQt.getScQtSupplierCriteria().setLocationLevel(null);
					scQt.getScQtSupplierCriteria().setScQtLocCriteria(null);
					if (null != scQt.getScQtSupplierCriteria()
							.getQtZoneCriterias()) {
						qtSupplierCriteria.getQtZoneCriterias()
								.setTplSpecZoneDtls(null);
						qtSupplierCriteria.setQtZoneCriterias(null);
					}

				}

				List<ScQtCategory> openList = this.getManagedBean()
						.getQtCategoryList();
				if (null != openList && !openList.isEmpty()) {
					scQt.setScQtCategorys(openList);
				}

			}
		} else if (scQt.isPanel()) {

			List<TplSpecZoneDtl> businessOwnerZone = convertTreeNodesToZoneCriteria(
					this.getManagedBean().getZoneGeneralTrees(), null);

			if (businessOwnerZone != null && !businessOwnerZone.isEmpty()) {
				if (null != scQt.getScQtSupplierCriteria()
						&& null == scQt.getScQtSupplierCriteria()
								.getQtZoneCriterias()) {
					scQt.getScQtSupplierCriteria().setQtZoneCriterias(
							new ScQtZoneCriteria());
				}
				ScQtZoneCriteria zoneCriteria = scQt.getScQtSupplierCriteria()
						.getQtZoneCriterias();
				zoneCriteria.setScQtSupplierCriteria(scQt
						.getScQtSupplierCriteria());
				// TODO: Need to remove
				setUserDetails(zoneCriteria);
				List<TplSpecZoneDtl> zoneLocal = new ArrayList<TplSpecZoneDtl>();
				for (TplSpecZoneDtl tplSpecZoneDtl : businessOwnerZone) {
					zoneCriteria.setModified(true);
					tplSpecZoneDtl.setTplSpecZone(zoneCriteria);
					zoneLocal.add(tplSpecZoneDtl);
				}
				scQt.setZonal(true);
				qtSupplierCriteria.getQtZoneCriterias().setTplSpecZoneDtls(
						zoneLocal);

			}

			// scQt.setZoneLocationLevel(null);
			scQt.getScQtSupplierCriteria().setLocationLevel(null);
			scQt.setQtType(ParameterConst.QUOTATION_TENDER_TYPE_OPEN);

		}

		List<ScQtCheckList> techSpecList = this.getManagedBean()
				.getScQtCheckSt2techProposal();
		List<ScQtCheckList> localSpecList = new ArrayList<ScQtCheckList>();
		if (!this.getManagedBean().isSpecComplete()) {
			if (null != techSpecList && !techSpecList.isEmpty()) {
				localSpecList.addAll(techSpecList);

			}

			List<ScQtCheckList> finSpecList = this.getManagedBean()
					.getScQtCheckSt2financeProposalList();
			if (null != finSpecList && !finSpecList.isEmpty()) {
				localSpecList.addAll(finSpecList);
			}
			scQt.setScQtChecklists(localSpecList);
		} else {
			List<ScQtCheckList> localSpecList1 = new ArrayList<ScQtCheckList>();
			scQt.setScQtChecklists(localSpecList1);
		}

		if (SCProcessNameConst.SPECIFICATION_APPROVAL.equalsIgnoreCase(this
				.getManagedBean().getTaskName())) {
			List<ScQtRemark> remarksList = this.getManagedBean()
					.getScQtRemarksList();

			if (isNotNullOREmpty(remarksList)) {
				scQt.setScQtRemarks(finalizeRemarks(remarksList));

			}
		}

		scQt.setScQtCommittees(null);
		scQt.setScQtBriefings(null);
		reqDTO.setScQt(scQt);
		// reqDTO.setTobeRemoveLocCriteriaList(getManagedBean().getExistingSupplierLocationList());

		return reqDTO;

	}

	/**
	 * Inits the.
	 */
	@PostConstruct
	public void init() {

	}

	/**
	 * Retrive specdetails.retrive all the general menu information and display.
	 * 
	 * @param qtId
	 *            the qt id
	 * @param docType
	 *            the doc type
	 */
	private void retriveSpecdetails(Long qtId) {
		
		QTDetailBackingBean qtBackingBean = new QTDetailBackingBean();
		qtBackingBean.retrieveQuationTenderDetails(qtId);
	}

	/**
	 * Retrive nonpanel zone dtls.retrive all the zones informations and
	 * display.
	 * 
	 * @param zonedtls
	 *            the zonedtls
	 */
	private void retriveNonpanelZoneDtls(List<TplSpecZoneDtl> zonedtls) {

		try {
			List<ZonesBean> beans = getManagedBean().getBeans();

			this.getManagedBean().setCtzones(false);
			if (isNotNullOREmpty(zonedtls)) {

				for (TplSpecZoneDtl zone : zonedtls) {
					this.getManagedBean().setCtzones(true);
					if (null != this.getManagedBean().getQt()
							&& !this.getManagedBean().getQt().isPanel()) {
						ZonesBean zonesBean = new ZonesBean();
						zonesBean.setTplSpecZoneDtl(zone);
						zonesBean.setTplSpecZoneLocs(zone.getTplSpecZoneLocs());
						beans.add(zonesBean);
					}

					getManagedBean().getZoneGeneralTrees().add(
							LocationSingleton.getInstance().constuctTreeNode(
									zone, zone.getTplSpecZoneLocs()));

				}
			}
			getManagedBean().setBeans(beans);

		} finally {

		}
	}

	/**
	 * Tier event.change Evaluation Type(OneTier/TwoTier) radion button in
	 * business owner.
	 * 
	 * @param event
	 *            (String EvaluationType).
	 */
	public void tierEvent(AjaxBehaviorEvent event) {
		String type = (String) event.getComponent().getAttributes()
				.get("value");
		this.getManagedBean().setEvaluationType(type);
	}

	/**
	 * Technical check list type change.change Technical Checilist Type Dropdown
	 * in specification committee based on type you will get action values in
	 * technical checklist
	 * 
	 * @param index
	 *            (int index)
	 * @param checkList
	 *            (List<ScQtCheckList> checkList).
	 */
	public void technicalCheckListTypeChange(int index,
			List<ScQtCheckList> checkList) {
		// Long type = (Long) event.getComponent().getAttributes().get("value");

		ScQtCheckList list = checkList.get(index);
		IParameterSO parameterSo = BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IParameterSO.class);
		List<PmParameterDesc> localParamDesc = retrieveParameterDescByType(ParameterTypeConst.DOCUMENT_DEFINITION_TYPE);
		String parameterCode = null;
		for (PmParameterDesc paramDesc : localParamDesc) {

			if (paramDesc
					.getPmParameter()
					.getParameterId()
					.equals(Long.valueOf(list.getDefinationType()
							.getParameterId()))) {
				parameterCode = paramDesc.getPmParameter().getParameterCode();
				list.setDefinationType(paramDesc.getPmParameter());
				break;
			}
		}

		try {
			this.getManagedBean().setTechnicalChecklistAction(null);
			ISearchBean<PmParameterDesc> searchBean = new SearchBean<PmParameterDesc>();

			if (ParameterConst.DOCUMENT_DEFINITION_TYPE_SAMPLE
					.equalsIgnoreCase(parameterCode)) {
				list.setScoringReq(true);

				ParameterReqDTO docDefTypeReqDTO = new ParameterReqDTO();
				docDefTypeReqDTO
						.setParameterType(ParameterTypeConst.SC_SUPPLIER_ACTION);
				docDefTypeReqDTO
						.setParameterCode(ParameterConst.CHECKLIST_ACTION_MANUAL_SUBMISSION);
				ParameterResDTO docDefTypeResDTO = parameterSo
						.retrieveParameterByTypeAndCode(docDefTypeReqDTO);
				list.setPmStatus(getPmStatusByStatusId(QTStatusConst.TS_COMPLETED));
				list.setAction(docDefTypeResDTO.getPmParameter());
				list.setScQtCheckListAttachment(null);

			} else if (ParameterConst.DOCUMENT_DEFINITION_TYPE_UPLOAD
					.equalsIgnoreCase(parameterCode)) {
				if (null == list.getScQtCheckListAttachment()) {
					list.setPmStatus(getPmStatusByStatusId(QTStatusConst.TS_PENDING_SUBMISSION));
				}

				List<String> recommendList = new ArrayList<String>();
				recommendList.add(ParameterConst.CHECKLIST_ACTION_VIEW);
				recommendList
						.add(ParameterConst.CHECKLIST_ACTION_ONLINE_SUBMISSION);

				searchBean.getDataFilterBeans().add(
						new SearchFilterBean("pmParameter.parameterCode",
								SearchOperator.OPERATOR_IN.value(),
								recommendList));
				SelectItem[] uploaditems = getDropdownValuesBySearchBean(
						ParameterTypeConst.SC_SUPPLIER_ACTION, searchBean);

				this.getManagedBean().setTechnicalChecklistAction(
						Arrays.asList(uploaditems));

			} else if (ParameterConst.DOCUMENT_DEFINITION_TYPE_PRESENTATION_DEMO
					.equalsIgnoreCase(parameterCode)) {
				list.setScoringReq(true);

				ParameterReqDTO docDefTypeReqDTO = new ParameterReqDTO();
				docDefTypeReqDTO
						.setParameterType(ParameterTypeConst.SC_SUPPLIER_ACTION);
				docDefTypeReqDTO
						.setParameterCode(ParameterConst.CHECKLIST_ACTION_PRESENTATION_DEMO);
				ParameterResDTO docDefTypeResDTO = parameterSo
						.retrieveParameterByTypeAndCode(docDefTypeReqDTO);
				list.setPmStatus(getPmStatusByStatusId(QTStatusConst.TS_COMPLETED));
				list.setAction(docDefTypeResDTO.getPmParameter());
				list.setScQtCheckListAttachment(null);
			} else if (ParameterConst.DOCUMENT_DEFINITION_TYPE_NO_ACTION
					.equalsIgnoreCase(parameterCode)) {

				ParameterReqDTO docDefTypeReqDTO = new ParameterReqDTO();
				docDefTypeReqDTO
						.setParameterType(ParameterTypeConst.SC_SUPPLIER_ACTION);
				docDefTypeReqDTO
						.setParameterCode(ParameterConst.CHECKLIST_ACTION_SUPPLIER_UPLOAD);
				ParameterResDTO docDefTypeResDTO = parameterSo
						.retrieveParameterByTypeAndCode(docDefTypeReqDTO);
				list.setPmStatus(getPmStatusByStatusId(QTStatusConst.TS_COMPLETED));

				list.setAction(docDefTypeResDTO.getPmParameter());
				list.setScQtCheckListAttachment(null);

			}
			this.getManagedBean().getScQtCheckSt2techProposal()
					.set(index, list);
		} finally {
			parameterSo = null;
		}

	}

	/**
	 * financial check list type change.change financial Checilist Type Dropdown
	 * in specification committee based on type you will get action values in
	 * financial checklist
	 * 
	 * @param index
	 *            (int index)
	 * @param checkList
	 *            (List<ScQtCheckList> checkList).
	 */
	public void financialCheckListTypeChange(int index,
			List<ScQtCheckList> checkList) {
		// Long type = (Long) event.getComponent().getAttributes().get("value");
		ScQtCheckList list = checkList.get(index);
		IParameterSO parameterSo = BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IParameterSO.class);
		List<PmParameterDesc> localParamDesc = retrieveParameterDescByType(ParameterTypeConst.DOCUMENT_DEFINITION_TYPE);
		String parameterCode = null;
		for (PmParameterDesc paramDesc : localParamDesc) {

			if (paramDesc
					.getPmParameter()
					.getParameterId()
					.equals(Long.valueOf(list.getDefinationType()
							.getParameterId()))) {
				parameterCode = paramDesc.getPmParameter().getParameterCode();
				list.setDefinationType(paramDesc.getPmParameter());
				break;
			}
		}

		try {
			this.getManagedBean().setFinancialChecklistAction(null);
			ISearchBean<PmParameterDesc> searchBean = new SearchBean<PmParameterDesc>();

			if (ParameterConst.DOCUMENT_DEFINITION_TYPE_UPLOAD
					.equalsIgnoreCase(parameterCode)) {
				if (null == list.getScQtCheckListAttachment()) {
					list.setPmStatus(getPmStatusByStatusId(QTStatusConst.TS_PENDING_SUBMISSION));
				}

				List<String> recommendList = new ArrayList<String>();
				recommendList.add(ParameterConst.CHECKLIST_ACTION_VIEW);
				recommendList
						.add(ParameterConst.CHECKLIST_ACTION_ONLINE_SUBMISSION);

				searchBean.getDataFilterBeans().add(
						new SearchFilterBean("pmParameter.parameterCode",
								SearchOperator.OPERATOR_IN.value(),
								recommendList));
				SelectItem[] uploaditems = getDropdownValuesBySearchBean(
						ParameterTypeConst.SC_SUPPLIER_ACTION, searchBean);

				this.getManagedBean().setFinancialChecklistAction(
						Arrays.asList(uploaditems));

			}
			if (ParameterConst.DOCUMENT_DEFINITION_TYPE_NO_ACTION
					.equalsIgnoreCase(parameterCode)) {
				ParameterReqDTO docDefTypeReqDTO = new ParameterReqDTO();
				docDefTypeReqDTO
						.setParameterType(ParameterTypeConst.SC_SUPPLIER_ACTION);
				docDefTypeReqDTO
						.setParameterCode(ParameterConst.CHECKLIST_ACTION_SUPPLIER_UPLOAD);
				ParameterResDTO docDefTypeResDTO = parameterSo
						.retrieveParameterByTypeAndCode(docDefTypeReqDTO);
				list.setPmStatus(getPmStatusByStatusId(QTStatusConst.TS_COMPLETED));

				list.setAction(docDefTypeResDTO.getPmParameter());
				list.setScQtCheckListAttachment(null);
			}
			this.getManagedBean().getScQtCheckSt2financeProposalList()
					.set(index, list);
		} finally {
			parameterSo = null;
		}

	}

	/**
	 * Gets the financial checklist def type values.display Financial Checklist
	 * Type Dropdown values.
	 * 
	 * @param type
	 *            (String type)
	 * @return SelectItem[].
	 */
	public SelectItem[] getFinancialChecklistDefTypeValues(String type) {

		ISearchBean<PmParameterDesc> searchBean = new SearchBean<PmParameterDesc>();
		List<String> recommendList = new ArrayList<String>();
		recommendList.add(ParameterConst.DOCUMENT_DEFINITION_TYPE_UPLOAD);
		recommendList.add(ParameterConst.DOCUMENT_DEFINITION_TYPE_NO_ACTION);
		searchBean.getDataFilterBeans().add(
				new SearchFilterBean("pmParameter.parameterCode",
						SearchOperator.OPERATOR_IN.value(), recommendList));

		SelectItem[] items = getDropdownValuesBySearchBean(
				ParameterTypeConst.DOCUMENT_DEFINITION_TYPE, searchBean);
		return items;
	}

	/**
	 * Gets the fin checklist action values.display Financial Checklist action
	 * values in dropdown.
	 * 
	 * @param type
	 *            (String type)
	 * @return SelectItem[].
	 */
	public SelectItem[] getFinChecklistActionValues(String type) {

		ISearchBean<PmParameterDesc> searchBean = new SearchBean<PmParameterDesc>();
		List<String> recommendList = new ArrayList<String>();
		recommendList.add(ParameterConst.CHECKLIST_ACTION_VIEW);
		recommendList.add(ParameterConst.CHECKLIST_ACTION_ONLINE_SUBMISSION);

		searchBean.getDataFilterBeans().add(
				new SearchFilterBean("pmParameter.parameterCode",
						SearchOperator.OPERATOR_IN.value(), recommendList));

		SelectItem[] items = getDropdownValuesBySearchBean(
				ParameterTypeConst.SC_SUPPLIER_ACTION, searchBean);
		return items;
	}

	/**
	 * Gets the technical checklist def type values.display Technical Checklist
	 * Type Dropdown values.
	 * 
	 * @param type
	 *            (String type)
	 * @return SelectItem[].
	 */
	public SelectItem[] getTechnicalChecklistDefTypeValues(String type) {

		ISearchBean<PmParameterDesc> searchBean = new SearchBean<PmParameterDesc>();
		List<String> recommendList = new ArrayList<String>();
		recommendList.add(ParameterConst.DOCUMENT_DEFINITION_TYPE_UPLOAD);
		recommendList.add(ParameterConst.DOCUMENT_DEFINITION_TYPE_SAMPLE);
		recommendList.add(ParameterConst.DOCUMENT_DEFINITION_TYPE_NO_ACTION);
		if (null != this.getManagedBean().getQt()
				&& ParameterConst.EVALUATION_TYPES_TWO_TIER.equalsIgnoreCase(this
						.getManagedBean().getQt().getEvaluationType())) {

			recommendList
					.add(ParameterConst.DOCUMENT_DEFINITION_TYPE_PRESENTATION_DEMO);
		}

		searchBean.getDataFilterBeans().add(
				new SearchFilterBean("pmParameter.parameterCode",
						SearchOperator.OPERATOR_IN.value(), recommendList));

		SelectItem[] items = getDropdownValuesBySearchBean(
				ParameterTypeConst.DOCUMENT_DEFINITION_TYPE, searchBean);
		return items;
	}

	/**
	 * Spec title change.Event handle change Title in Template Spec.
	 * 
	 * @param event
	 *            (String title)
	 * 
	 */
	public void specTitleChange(AjaxBehaviorEvent event) {
		// Boolean panel = this.getManagedBean().getQt().isPanel();
		String title = (String) event.getComponent().getAttributes()
				.get("value");
		this.getManagedBean().getScQtChecklist().setTitle(title);
	}

	/**
	 * Technical check list title change.Event handle Change Title in Technical
	 * Checklist.
	 * 
	 * @param index
	 *            (int index)
	 * @param checklist
	 *            (List<ScQtCheckList> checklist)
	 */
	public void technicalCheckListTitleChange(int index,
			List<ScQtCheckList> checklist) {
		ScQtCheckList list = checklist.get(index);

		this.getManagedBean().getScQtCheckSt2techProposal().set(index, list);

	}

	/**
	 * Financial check list title change.Event handle change title in Financial
	 * Checklist.
	 * 
	 * @param index
	 *            (int index)
	 * @param checklist
	 *            (List<ScQtCheckList> checklist)
	 */
	public void financialCheckListTitleChange(int index,
			List<ScQtCheckList> checklist) {
		ScQtCheckList list = checklist.get(index);

		this.getManagedBean().getScQtCheckSt2financeProposalList()
				.set(index, list);

	}

	/**
	 * Technical checklist drop down action change.Event handle change Action
	 * dropdown in Technical Checklist.
	 * 
	 * @param index
	 *            the index
	 * @param checklist
	 *            the checklist
	 */
	public void technicalChecklistDropDownActionChange(int index,
			List<ScQtCheckList> checklist) {
		ScQtCheckList list = checklist.get(index);

		List<PmParameterDesc> localParamDesc = retrieveParameterDescByType(ParameterTypeConst.SC_SUPPLIER_ACTION);
		String parameterCode = null;
		for (PmParameterDesc paramDesc : localParamDesc) {

			if (paramDesc.getPmParameter().getParameterId()
					.equals(Long.valueOf(list.getAction().getParameterId()))) {
				parameterCode = paramDesc.getPmParameter().getParameterCode();
				if (ParameterConst.CHECKLIST_ACTION_VIEW
						.equalsIgnoreCase(parameterCode)) {
					list.setScoringReq(false);
				}
				list.setPmStatus(getPmStatusByStatusId(QTStatusConst.TS_PENDING_SUBMISSION));

				list.setAction(paramDesc.getPmParameter());
				break;
			}
		}

		this.getManagedBean().getScQtCheckSt2techProposal().set(index, list);

	}

	/**
	 * Financial check list drop down action change.Event handle Change Action
	 * Dropdown in Financial Checklist.
	 * 
	 * @param index
	 *            (int index)
	 * @param checklist
	 *            (List<ScQtCheckList> checklist)
	 */
	public void financialCheckListDropDownActionChange(int index,
			List<ScQtCheckList> checklist) {
		ScQtCheckList list = checklist.get(index);

		List<PmParameterDesc> localParamDesc = retrieveParameterDescByType(ParameterTypeConst.SC_SUPPLIER_ACTION);
		String parameterCode = null;
		for (PmParameterDesc paramDesc : localParamDesc) {

			if (paramDesc.getPmParameter().getParameterId()
					.equals(Long.valueOf(list.getAction().getParameterId()))) {
				parameterCode = paramDesc.getPmParameter().getParameterCode();
				if (ParameterConst.CHECKLIST_ACTION_VIEW
						.equalsIgnoreCase(parameterCode)) {
					list.setScoringReq(false);
				}
				list.setPmStatus(getPmStatusByStatusId(QTStatusConst.TS_PENDING_SUBMISSION));

				list.setAction(paramDesc.getPmParameter());
				break;
			}
		}

		this.getManagedBean().getScQtCheckSt2financeProposalList()
				.set(index, list);

	}

	/**
	 * Sets the evaluation type.based on Estimated Price set Evaluation Type.
	 * 
	 * @param scQt
	 *            (ScQt scQt)
	 */
	public void populateEvaluationTypes(ScQt scQt) {
		if (isOneTierThresHold(scQt.getEstimatedAmount())) {

			List<SelectItem> itemList = new ArrayList<SelectItem>();
			List<PmParameterDesc> localParamDesc = retrieveParameterDescByType(ParameterTypeConst.SC_EVALUATION_TYPE);

			for (PmParameterDesc paramDesc : localParamDesc) {
				String evaluationTypeCode = paramDesc.getPmParameter()
						.getParameterCode();
				if (ParameterConst.EVALUATION_TYPES_ONE_TIER
						.equals(evaluationTypeCode)) {

					itemList.add(new SelectItem(evaluationTypeCode, paramDesc
							.getCodeName()));

					if (scQt.getEvaluationType() == null) {
						scQt.setEvaluationType(evaluationTypeCode);
					}
				}
			}
			getManagedBean().setEvaluationTypeItem(itemList);
		}
	}

	/**
	 * Fulfilment change.changed Fulfilemnt Dropdown in specification committee.
	 * 
	 * @param event
	 *            (Long fulfilmentId)
	 */
	public void fulfilmentChange(AjaxBehaviorEvent event) {
		/*
		 * IQTSpecSO specso = getSpecSO(); SpecificationReqDTO specReqDTO = new
		 * SpecificationReqDTO();
		 */try {
			Long type = (Long) event.getComponent().getAttributes()
					.get("value");
			List<PmParameterDesc> localParamDesc = retrieveParameterDescByType(ParameterTypeConst.FULFILMENT_TYPE);
			for (PmParameterDesc paramDesc : localParamDesc) {
				if (paramDesc.getPmParameter().getParameterId()
						.equals(Long.valueOf(type))) {

					this.getManagedBean().setFulfilmentType(
							paramDesc.getPmParameter().getParameterCode());
					this.getManagedBean().getQt()
							.setFulfilmentType(paramDesc.getPmParameter());
					break;
				}
			}

			if (ParameterConst.FULFILMENT_TYPE_ONE_OFF.equalsIgnoreCase(this
					.getManagedBean().getFulfilmentType())) {
				this.getManagedBean()
						.getQt()
						.setScheduleType(
								ParameterConst.PERIODIC_SCHEDULE_TYPE_FREQUENCY_SCHEDULE);

			} else if (ParameterConst.FULFILMENT_TYPE_AS_AND_WHEN
					.equalsIgnoreCase(this.getManagedBean().getFulfilmentType())) {
				this.getManagedBean()
						.getQt()
						.setScheduleType(
								ParameterConst.PERIODIC_SCHEDULE_TYPE_PROGRESSIVE_PAYMENT);
				this.getManagedBean().setTotalProgressivePayment(0);

			} else if (ParameterConst.FULFILMENT_TYPE_SCHEDULED
					.equalsIgnoreCase(this.getManagedBean().getFulfilmentType())) {
				this.getManagedBean()
						.getQt()
						.setScheduleType(
								ParameterConst.PERIODIC_SCHEDULE_TYPE_FREQUENCY_SCHEDULE);
				this.getManagedBean().setTotalFrequency(0);
				this.getManagedBean().setTotalProgressivePayment(0);

			}
			this.getManagedBean().getQt().setScQtSchedules(null);
			this.getManagedBean().getQt().setScQtProgressivePayments(null);
			removefullfillmentDetails(ParameterConst.PERIODIC_SCHEDULE_TYPE_PROGRESSIVE_PAYMENT);
			removefullfillmentDetails(ParameterConst.PERIODIC_SCHEDULE_TYPE_FREQUENCY_SCHEDULE);

		} finally {

		}

	}

	/**
	 * Gets the fulfilment dtls.set Fulfilment Type parameter code in Spec
	 * committee.
	 * 
	 * @param scQt
	 *            (ScQt scQt)
	 * 
	 */
	private void getfulfilmentDtls(ScQt scQt) {
		if (null != scQt.getFulfilmentType()) {
			this.getManagedBean().setFulfilmentType(
					scQt.getFulfilmentType().getParameterCode());
		} else {
			scQt.setFulfilmentType(new PmParameter());
		}

	}

	/**
	 * Procurement change.change Procurement Category Dropdown in Business
	 * owner.
	 * 
	 * @param event
	 *            (Long procuremntCategoryId).
	 */
	public void procurementChange(AjaxBehaviorEvent event) {
		try {
			Long type = (Long) event.getComponent().getAttributes()
					.get("value");

			List<PmParameterDesc> localParamDesc = retrieveParameterDescByType(ParameterTypeConst.PROCUREMENT_CATEGORY);
			for (PmParameterDesc paramDesc : localParamDesc) {
				if (paramDesc.getPmParameter().getParameterId()
						.equals(Long.valueOf(type))) {

					this.getManagedBean().setProcuremntCategory(
							paramDesc.getPmParameter().getParameterCode());
				}

			}
		} finally {

		}
	}

	/**
	 * State change.supplier qualification criteria all states check/Uncheck
	 * checkbox.
	 * 
	 * @param event
	 *            (boolean allstates)
	 */
	public void stateChange(AjaxBehaviorEvent event) {
		boolean state = (boolean) event.getComponent().getAttributes()
				.get("value");

		this.getManagedBean().getQt().getScQtSupplierCriteria()
				.setAllState(state);

		if (!state) {
			if (null != this.getManagedBean().getZoneTrees()
					&& this.getManagedBean().getZoneTrees().isEmpty()) {
				this.getManagedBean().setZoneTrees(null);
			}
			if (null == this.getManagedBean().getQt().getScQtSupplierCriteria()
					.getLocationLevel()) {
				this.getManagedBean().getQt().getScQtSupplierCriteria()
						.setLocationLevel(new PmParameter());
			}

		} else {
			this.getManagedBean().setSupplierLocationList(
					getManagedBean().getExistingSupplierLocationList());
			this.getManagedBean().setZoneTrees(null);
		}
	}

	/**
	 * Schedule type change.Event handle change Schedule
	 * type(Schedule/Progressive payment)in Specification committee.
	 * 
	 * @param event
	 *            (String ScheduleType).
	 */
	public void scheduleTypeChange(ScQt scqt) {

		String scheduleType = scqt.getScheduleType();
		log.info("display schedule type" + scheduleType);
		log.info("display fulfilment  type"
				+ this.getManagedBean().getFulfilmentType());
		if (ParameterConst.PERIODIC_SCHEDULE_TYPE_FREQUENCY_SCHEDULE
				.equalsIgnoreCase(scheduleType)) {
			scheduleType = ParameterConst.PERIODIC_SCHEDULE_TYPE_PROGRESSIVE_PAYMENT;
			this.getManagedBean().setTotalProgressivePayment(0);
		} else if (ParameterConst.PERIODIC_SCHEDULE_TYPE_PROGRESSIVE_PAYMENT
				.equalsIgnoreCase(scheduleType)) {
			scheduleType = ParameterConst.PERIODIC_SCHEDULE_TYPE_FREQUENCY_SCHEDULE;
			this.getManagedBean().setTotalFrequency(0);
		}
		removefullfillmentDetails(scheduleType);
	}

	/**
	 * Template change.Change Templte Type(Standard/Non Standard)in Template
	 * Spec.
	 * 
	 * @param event
	 *            (String templateType).
	 */
	public void templateChange(AjaxBehaviorEvent event) {
		String template = (String) event.getComponent().getAttributes()
				.get("value");
		templateSpecCreate.resetData();
		this.getManagedBean().setStandardTemplate(template);
		this.getManagedBean().setSearchCreatedBy(null);
		if (null != getManagedBean().getQt().getPmProcurementCategory()
				&& null != getManagedBean().getQt().getPmProcurementCategory()
						.getParameterId()) {
			getManagedBean().setSearchProcurementId(
					getManagedBean().getQt().getPmProcurementCategory()
							.getParameterId());
		}
	}

	/**
	 * Item type change.Change Item Type(product/Service) Dropdown in Template
	 * Spec Popup.
	 * 
	 * @param event
	 *            (Long itemType)
	 */
	public void itemTypeChange(AjaxBehaviorEvent event) {
		Long itemId = (Long) event.getComponent().getAttributes().get("value");
		if (null != itemId) {
			this.getManagedBean().setSearchItemTypeId(itemId);
			// this.getManagedBean().setRequired(false);

		}
	}

	/**
	 * Select supplier location.chnage Location level dropdown in Supplier
	 * Qualification criteria menu.
	 * 
	 * @param event
	 *            (Long locationLevelId).
	 */
	public void selectSupplierLocation(AjaxBehaviorEvent event) {
		if (null != getManagedBean().getZoneTrees()
				&& !getManagedBean().getZoneTrees().isEmpty()) {
			this.getManagedBean().setSupplierLocationList(
					getManagedBean().getExistingSupplierLocationList());
			getManagedBean().setZoneTrees(null);
		}
	}

	/**
	 * Score type change.Change Checklist Score type(Auto/User) Scores in
	 * specification committee menu.
	 * 
	 * @param event
	 *            (String scoreType).
	 */
	public void scoreTypeChange(AjaxBehaviorEvent event) {
		String scoreType = (String) event.getComponent().getAttributes()
				.get("value");
		List<ScQtCheckListScore> toBeRemoveList = this.getManagedBean()
				.getToBeRemoveList();
		if (null == toBeRemoveList) {
			toBeRemoveList = new ArrayList<ScQtCheckListScore>();
		}
		if (null != scoreType && !"".equalsIgnoreCase(scoreType)) {
			if (scoreType.equalsIgnoreCase(QTConstants.USER_SCORE)) {
				ScQtCheckListScore score = new ScQtCheckListScore();
			score.setCriteriaType(this.getManagedBean().getCriteriaType());
			setUserDetails(score);
				this.getManagedBean().setScQtCheckListScore(
						score);
				// toBeRemoveList.add(this.getManagedBean().getCheckListScore());
				if (null != this.getManagedBean().getCheckListScore()
						&& !this.getManagedBean().getCheckListScore().isEmpty()) {

					toBeRemoveList.addAll(this.getManagedBean()
							.getCheckListScore());
					this.getManagedBean().setCheckListScore(
							new ArrayList<ScQtCheckListScore>());
				}
			} else {
				if (null != this.getManagedBean().getScQtCheckListScore()) {
					toBeRemoveList.add(this.getManagedBean()
							.getScQtCheckListScore());
				}
			}
			this.getManagedBean().setToBeRemoveList(toBeRemoveList);
			this.getManagedBean().setChecklistScoreType(scoreType);
		}
	}

	/**
	 * Companyscore type change.Change Checklist Score type(Auto/User) Scores in
	 * specification committee menu.
	 * 
	 * @param event
	 *            (String ScoreType).
	 */
	public void companyscoreTypeChange(AjaxBehaviorEvent event) {
		String scoreType = (String) event.getComponent().getAttributes()
				.get("value");
		List<ScQtCheckListScore> toBeRemoveList = this.getManagedBean()
				.getToBeRemoveList();
		if (null == toBeRemoveList) {
			toBeRemoveList = new ArrayList<ScQtCheckListScore>();
		}
		if (null != scoreType && !"".equalsIgnoreCase(scoreType)) {

			if (scoreType.equalsIgnoreCase(QTConstants.USER_SCORE)) {
				ScQtCheckListScore score = new ScQtCheckListScore();
				score.setCriteriaType(this.getManagedBean().getOtherCriteriaType());
				setUserDetails(score);
				this.getManagedBean().setScQtContractScore(
						score);
				if (null != this.getManagedBean().getScoringForYears()
						&& !this.getManagedBean().getScoringForYears()
								.isEmpty()) {
					toBeRemoveList.addAll(this.getManagedBean()
							.getScoringForYears());
					this.getManagedBean().setScoringForYears(
							new ArrayList<ScQtCheckListScore>());
				}
			} else {
				if (null != this.getManagedBean().getScQtContractScore()) {
					toBeRemoveList.add(this.getManagedBean()
							.getScQtContractScore());
				}
			}
			this.getManagedBean().setToBeRemoveList(toBeRemoveList);
			this.getManagedBean().setCompanyScoreType(scoreType);
		}
	}

	/**
	 * Bank statementsfrom change.change bankStatemnt checklist month dropdown
	 * in specification committee.
	 * 
	 * @param event
	 *            (String formMonth)
	 */
	public void bankStatementsfromChange(AjaxBehaviorEvent event) {
		String formvalue = (String) event.getComponent().getAttributes()
				.get("value");
		String currentYear = this.getManagedBean().getStartYear();
		if (null != formvalue && !"".endsWith(formvalue) && null != currentYear) {
			this.getManagedBean().setFromMonth(formvalue);
			int year = Integer.parseInt(currentYear);
			int n = Integer.parseInt(this.getManagedBean().getFromMonth());
			int j = n + 2;
			calculateBankMonths(j, year);
		}
	}

	/**
	 * Bank statementsyears change.hange bankStatemnt checklist years dropdown
	 * in specification committee.
	 * 
	 * @param event
	 *            (String Year).
	 */
	public void bankStatementsyearsChange(AjaxBehaviorEvent event) {
		String year = (String) event.getComponent().getAttributes()
				.get("value");

		int currentYear = Integer.parseInt(year);

		if (currentYear > 0 && null != this.getManagedBean().getFromMonth()
				&& !"".equals(this.getManagedBean().getFromMonth())) {
			int n = Integer.parseInt(this.getManagedBean().getFromMonth());
			int j = n + 2;
			calculateBankMonths(j, currentYear);
		}

	}

	/**
	 * Calculate bank months.set StartMonth and EndMonth for bankStatement
	 * checklist.
	 * 
	 * @param j
	 *            (int month)
	 * @param currentYear
	 *            (int currentYear).
	 */
	private void calculateBankMonths(int j, int currentYear) {
		String Startmonth = "";
		if (j == 0) {
			Startmonth = ResourceBundleUtils
					.getMessage("lbl.sc.checklist.month.january",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							new FacesScopes().getUserLocale());
		} else if (j == 1) {
			Startmonth = ResourceBundleUtils
					.getMessage("lbl.sc.checklist.month.february",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							new FacesScopes().getUserLocale());
		} else if (j == 2) {
			Startmonth =  ResourceBundleUtils
					.getMessage("lbl.sc.checklist.month.march",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							new FacesScopes().getUserLocale());
		} else if (j == 3) {
			Startmonth = ResourceBundleUtils
					.getMessage("lbl.sc.checklist.month.april",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							new FacesScopes().getUserLocale());
		} else if (j == 4) {
			Startmonth = ResourceBundleUtils
					.getMessage("lbl.sc.checklist.month.may",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							new FacesScopes().getUserLocale());
		} else if (j == 5) {
			Startmonth = ResourceBundleUtils
					.getMessage("lbl.sc.checklist.month.june",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							new FacesScopes().getUserLocale());
		}

		else if (j == 6) {
			Startmonth = ResourceBundleUtils
					.getMessage("lbl.sc.checklist.month.july",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							new FacesScopes().getUserLocale());
		} else if (j == 7) {
			Startmonth = ResourceBundleUtils
					.getMessage("lbl.sc.checklist.month.august",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							new FacesScopes().getUserLocale());
		} else if (j == 8) {
			Startmonth = ResourceBundleUtils
					.getMessage("lbl.sc.checklist.month.september",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							new FacesScopes().getUserLocale());
		} else if (j == 9) {
			Startmonth = ResourceBundleUtils
					.getMessage("lbl.sc.checklist.month.october",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							new FacesScopes().getUserLocale());
		} else if (j == 10) {
			Startmonth = ResourceBundleUtils
					.getMessage("lbl.sc.checklist.month.november",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							new FacesScopes().getUserLocale());
		} else if (j == 11) {

			Startmonth = ResourceBundleUtils
					.getMessage("lbl.sc.checklist.month.december",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							new FacesScopes().getUserLocale());
		} else if (j == 12) {
			currentYear = currentYear + 1;
			Startmonth = ResourceBundleUtils
					.getMessage("lbl.sc.checklist.month.january",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							new FacesScopes().getUserLocale());

		} else if (j == 13) {
			currentYear = currentYear + 1;
			Startmonth = ResourceBundleUtils
					.getMessage("lbl.sc.checklist.month.february",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							new FacesScopes().getUserLocale());

		}

		this.getManagedBean().setBankstmntTomonth(Startmonth);
		this.getManagedBean().setToMonth(String.valueOf(j));
		this.getManagedBean().setEndYear(String.valueOf(currentYear));
	}

	/**
	 * Technical checklist edit action.click on pencil icon in technical
	 * checklist navigate to checklist score page.
	 * 
	 * @param index
	 *            (int index)
	 * @param checkList
	 *            (ScQtCheckList checkList)
	 * @param specType
	 *            ( String specType)
	 * @return navigate to checklist score page.
	 */
	public String technicalChecklistEditAction(int index,
			ScQtCheckList checkList, String specType) {
		IQTSpecSO specSo = null;
		SpecificationResDTO resDTO = new SpecificationResDTO();
		this.getManagedBean().setScQtChecklist(checkList);
		this.getManagedBean().setIndex(index);
		this.getManagedBean().setSpecType(specType);
		this.getManagedBean().setToBeRemoveList(
				new ArrayList<ScQtCheckListScore>());
		this.getManagedBean().setScQtCheckListScore(null);
		this.getManagedBean().setScQtContractScore(null);
		try {

			SpecificationReqDTO reqDTO = new SpecificationReqDTO();
			specSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);

			if (QTConstants.TEMPLETE_SPEC_CODE.equalsIgnoreCase(checkList
					.getDefinationType().getParameterCode())) {
				this.getManagedBean().setSpecStatusId(
						checkList.getPmStatus().getStatusId());
				this.getManagedBean().setIndex(index);
				this.getManagedBean().setScQtChecklist(checkList);
				this.getManagedBean().setCheckListTitle(checkList.getTitle());

				ITemplateSpecSO templateSpecSO = EpEjbBusinessDelegate
						.getInstance().getServiceObject(
								ModuleCodeConst.COMMON_COMPONENT,
								ITemplateSpecSO.class);
				TemplateSpecReqDTO specReqDTO = new TemplateSpecReqDTO();
				specReqDTO.setSpecMaster(checkList.getScQtDoc()
						.getTplSpecMaster());
				specReqDTO
						.setContentType(TemplateSpecConst.CONTENT_TYPE_QUESTION);
				TemplateSpecResDTO templateresDTO = templateSpecSO
						.getTemplateMaster(specReqDTO);
				checkList.getScQtDoc().setTplSpecMaster(
						templateresDTO.getSpecMaster());
				TemplateSpecBean templateSpecBean = getManagedBean()
						.getTemplateSpecBean();
				templateSpecBean.setSpecMaster(checkList.getScQtDoc()
						.getTplSpecMaster());
				if (this.getManagedBean().getQt().isZonal()
						&& this.getManagedBean().getQt().getQtType()
								.equals(ParameterConst.QUOTATION_TENDER_TYPE_OPEN)) {
					retriveSpecdetails(this.getManagedBean().getQt().getQtId());
					ScQtSupplierCriteria supplierCriteria = this
							.getManagedBean().getQt().getScQtSupplierCriteria();
					List<TplSpecZoneDtl> businessOwnerZone = convertTreeNodesToZoneCriteria(
							this.getManagedBean().getZoneGeneralTrees(), null);

					if (businessOwnerZone != null
							&& !businessOwnerZone.isEmpty()) {

						for (TplSpecZoneDtl tplSpecZoneDtl : businessOwnerZone) {
							supplierCriteria.getQtZoneCriterias().setModified(
									true);
							tplSpecZoneDtl.setTplSpecZone(supplierCriteria
									.getQtZoneCriterias());

						}
						templateSpecBean.getSpecMaster().setTplSpecZone(
								supplierCriteria.getQtZoneCriterias());
						templateSpecBean.getSpecMaster().getTplSpecZone()
								.setTplSpecZoneDtls(businessOwnerZone);
					}

				}
				if (null != checkList.getPmStatus()
						&& QTStatusConst.TS_PENDING_SUBMISSION.equals(checkList
								.getPmStatus().getStatusId())) {
					ScQtSupplierCriteria supplierCriteria = this
							.getManagedBean().getQt().getScQtSupplierCriteria();
					ScQtZoneCriteria zoneCriteria = null;
					if (null != supplierCriteria
							&& null != supplierCriteria.getQtZoneCriterias()) {
						if(this.getManagedBean().getQt().isZonal()){
						zoneCriteria = supplierCriteria.getQtZoneCriterias();
						}
					}
					TemplateSpecHelper.getInstance().updateZoneDtl(
							templateSpecBean.getSpecMaster(), zoneCriteria);
				}
				if (this.getManagedBean().getQt().isPanel()) {
					this.getManagedBean().setPanelItemCategory("");
					this.getManagedBean().setSpecNewable(true);
					if (null == this.getManagedBean().getPanelItemCategory()
							|| "".equals(this.getManagedBean()
									.getPanelItemCategory())) {
					//	retriveSupplierQualificationCriteria("SC");
						//List<CtPanelItem> panelItemList = getManagedBean().getPanelItemList();
						
					/*	if (CollectionUtils.isNotEmpty(panelItemList)){
							if(null!=panelItemList.get(0).getItemCategoryId()){
							 this.getManagedBean().setPanelItemCategory(panelItemList.get(0).getItemCategoryId().getParameterCode());
							 }

					}*/
						if(null!=templateresDTO.getSpecMaster() && null==templateresDTO.getSpecMaster().getCtPanelItem()){
							this.getManagedBean().setPanelItemCategory(ParameterConst.ITEM_CATEGORY_TYPE_SPECIFIC);
						}
					}
					if ((null != this.getManagedBean().getPanelItemCategory() && !""
							.equals(this.getManagedBean()
									.getPanelItemCategory()))
							&& ParameterConst.ITEM_CATEGORY_TYPE_SPECIFIC
									.equalsIgnoreCase(this.getManagedBean()
											.getPanelItemCategory())) {
						this.getManagedBean().setSpecNewable(false);
					}
				}
				/*
				 * templateSpecBean.getSpecMaster().setScoringReq(
				 * this.getManagedBean().getQt().isTechEvaluationReq());
				 */
				return "/xhtml/sc/boSctemplatespec/committeeTechnicalEditAction.xhtml?faces-redirect=true";
			} else {
				setUserDetails(checkList);
				this.getManagedBean().setChecklistScoreType(null);
				 this.getManagedBean().setCriteriaType(0);
                 this.getManagedBean().setOtherCriteriaType(0);
				this.getManagedBean().setCompanyScoreType(null);
				this.getManagedBean().setCheckListScore(
						new ArrayList<ScQtCheckListScore>());
				this.getManagedBean().setScoringForYears(
						new ArrayList<ScQtCheckListScore>());
				this.getManagedBean().setToBeRemoveList(
						new ArrayList<ScQtCheckListScore>());

				if (QTConstants.TECHNICAL_GOV_CODE.equalsIgnoreCase(checkList
						.getPmDocument().getDocDefinitionKey())) {

					reqDTO.setScQtCheckList(checkList);
					resDTO = specSo.retrieveCheckListScoreNValue(reqDTO);
					if (null == resDTO.getScQtCheckListValue()) {
                
						this.getManagedBean().setChecklistScoreType(null);

						this.getManagedBean().setCompanyScoreType(null);

						ScQtCheckListValue checkListValue = new ScQtCheckListValue();
						setUserDetails(checkListValue);
						checkListValue.setScQtCheckList(checkList);
						checkList.setScQtCheckListValue(checkListValue);

					} else {

						List<ScQtCheckListScore> noOfContracts = new ArrayList<ScQtCheckListScore>();
						List<ScQtCheckListScore> noOfYears = new ArrayList<ScQtCheckListScore>();
						for (ScQtCheckListScore checklistScore : resDTO
								.getCheckListScores()) {
							if (checklistScore.getCriteriaType() == QTConstants.CRITERIA_TYPE_TOTAL_NO_OF_CONTRACTS_FOR_FED_GOV) {
								
								if (checklistScore.getScoreType().intValue() == 1) {

									this.getManagedBean()
											.setChecklistScoreType(
													String.valueOf(checklistScore
															.getScoreType()
															.intValue()));
									noOfContracts.add(checklistScore);
									this.getManagedBean().setCheckListScore(
											noOfContracts);
								} else if (checklistScore.getScoreType()
										.intValue() == 2) {
									this.getManagedBean()
											.setChecklistScoreType(
													String.valueOf(checklistScore
															.getScoreType()
															.intValue()));
									this.getManagedBean()
											.setScQtCheckListScore(
													checklistScore);
								}

							} else if (checklistScore.getCriteriaType() == QTConstants.CRITERIA_TYPE_TOTAL_NO_OF_CONTRACT_YEARS_FOR_FED_GOV) {
								if (checklistScore.getScoreType().intValue() == 1) {

									this.getManagedBean()
											.setCompanyScoreType(
													String.valueOf(checklistScore
															.getScoreType()
															.intValue()));
									noOfYears.add(checklistScore);
									this.getManagedBean().setScoringForYears(
											noOfYears);
								} else if (checklistScore.getScoreType()
										.intValue() == 2) {
									this.getManagedBean()
											.setCompanyScoreType(
													String.valueOf(checklistScore
															.getScoreType()
															.intValue()));
									this.getManagedBean().setScQtContractScore(
											checklistScore);
								}

							}

						}
						if (null != this.getManagedBean().getCheckListScore()
								&& this.getManagedBean().getCheckListScore()
										.size() > 1) {

							returnCheckListScores(this.getManagedBean()
									.getCheckListScore());

						}

						if (null != this.getManagedBean().getScoringForYears()
								&& this.getManagedBean().getScoringForYears()
										.size() > 1) {
							returnCheckListScores(this.getManagedBean()
									.getScoringForYears());

						}
						if (null != resDTO.getScQtCheckListValue()) {
							checkList.setScQtCheckListValue(resDTO
									.getScQtCheckListValue());
						}
					}
					if (null == checkList.getScQtCheckListValue()) {
						ScQtCheckListValue checkListValue = new ScQtCheckListValue();
						setUserDetails(checkListValue);
						checkListValue.setScQtCheckList(checkList);
						checkList.setScQtCheckListValue(checkListValue);
					}
					   this.getManagedBean().setCriteriaType(QTConstants.CRITERIA_TYPE_TOTAL_NO_OF_CONTRACTS_FOR_FED_GOV);
	                   this.getManagedBean().setOtherCriteriaType(QTConstants.CRITERIA_TYPE_TOTAL_NO_OF_CONTRACT_YEARS_FOR_FED_GOV);
					this.getManagedBean().setScQtChecklist(checkList);
					return "/xhtml/sc/specification/specificationCommittee/technicalCheckListGovCompanyExperience.xhtml?faces-redirect=true";
				} else if (QTConstants.TECHNICAL_PRIVATE_CODE
						.equalsIgnoreCase(checkList.getPmDocument()
								.getDocDefinitionKey())) {

					reqDTO.setScQtCheckList(checkList);
					resDTO = specSo.retrieveCheckListScoreNValue(reqDTO);
					if (null == resDTO.getScQtCheckListValue()) {
						
						this.getManagedBean().setChecklistScoreType(null);

						this.getManagedBean().setCompanyScoreType(null);

						ScQtCheckListValue checkListValue = new ScQtCheckListValue();
						setUserDetails(checkListValue);

						checkListValue.setScQtCheckList(checkList);
						checkList.setScQtCheckListValue(checkListValue);

					} else {

						List<ScQtCheckListScore> noOfContracts = new ArrayList<ScQtCheckListScore>();
						List<ScQtCheckListScore> noOfYears = new ArrayList<ScQtCheckListScore>();
						for (ScQtCheckListScore checklistScore : resDTO
								.getCheckListScores()) {

							if (checklistScore.getCriteriaType() == QTConstants.CRITERIA_TYPE_TOTAL_NO_OF_CONTRACTS_FOR_NON_FED_GOV) {
								
				                  
								if (checklistScore.getScoreType().intValue() == 1) {

									this.getManagedBean()
											.setChecklistScoreType(
													String.valueOf(checklistScore
															.getScoreType()
															.intValue()));
									noOfContracts.add(checklistScore);
									this.getManagedBean().setCheckListScore(
											noOfContracts);
								} else if (checklistScore.getScoreType()
										.intValue() == 2) {
									this.getManagedBean()
											.setChecklistScoreType(
													String.valueOf(checklistScore
															.getScoreType()
															.intValue()));
									this.getManagedBean()
											.setScQtCheckListScore(
													checklistScore);
								}

							} else if (checklistScore.getCriteriaType() == QTConstants.CRITERIA_TYPE_TOTAL_NO_OF_CONTRACT_YEARS_FOR_NON_FED_GOV) {
								
								if (checklistScore.getScoreType().intValue() == 1) {

									this.getManagedBean()
											.setCompanyScoreType(
													String.valueOf(checklistScore
															.getScoreType()
															.intValue()));
									noOfYears.add(checklistScore);
									this.getManagedBean().setScoringForYears(
											noOfYears);
								} else if (checklistScore.getScoreType()
										.intValue() == 2) {
									this.getManagedBean()
											.setCompanyScoreType(
													String.valueOf(checklistScore
															.getScoreType()
															.intValue()));
									this.getManagedBean().setScQtContractScore(
											checklistScore);
								}

							}

						}
						if (null != this.getManagedBean().getCheckListScore()
								&& this.getManagedBean().getCheckListScore()
										.size() > 1) {

							returnCheckListScores(this.getManagedBean()
									.getCheckListScore());

						}

						if (null != this.getManagedBean().getScoringForYears()
								&& this.getManagedBean().getScoringForYears()
										.size() > 1) {
							returnCheckListScores(this.getManagedBean()
									.getScoringForYears());

						}
						if (null != resDTO.getScQtCheckListValue()) {
							checkList.setScQtCheckListValue(resDTO
									.getScQtCheckListValue());
						}
					}
					if (null == checkList.getScQtCheckListValue()) {
						ScQtCheckListValue checkListValue = new ScQtCheckListValue();
						setUserDetails(checkListValue);
						checkListValue.setScQtCheckList(checkList);
						checkList.setScQtCheckListValue(checkListValue);
					}
					 this.getManagedBean().setCriteriaType(QTConstants.CRITERIA_TYPE_TOTAL_NO_OF_CONTRACTS_FOR_NON_FED_GOV);
	                   this.getManagedBean().setOtherCriteriaType(QTConstants.CRITERIA_TYPE_TOTAL_NO_OF_CONTRACT_YEARS_FOR_NON_FED_GOV);
					this.getManagedBean().setScQtChecklist(checkList);
					return "/xhtml/sc/specification/specificationCommittee/technicalCheckListPrivateCompanyExperience.xhtml?faces-redirect=true";
				}

				else if (QTConstants.TECHNICAL_STAFF_LIST
						.equalsIgnoreCase(checkList.getPmDocument()
								.getDocDefinitionKey())) {

					reqDTO.setScQtCheckList(checkList);
					resDTO = specSo.retrieveCheckListScoreNValue(reqDTO);
					if (null == resDTO.getCheckListScores()
							|| resDTO.getCheckListScores().isEmpty()) {
						checkList.setScQtCheckListValue(resDTO
								.getScQtCheckListValue());
						this.getManagedBean().setChecklistScoreType(null);
						this.getManagedBean().setCompanyScoreType(null);

					} else {

						List<ScQtCheckListScore> technicalEmployee = new ArrayList<ScQtCheckListScore>();

						for (ScQtCheckListScore checklistScore : resDTO
								.getCheckListScores()) {
							if (checklistScore.getCriteriaType() == QTConstants.CRITERIA_TYPE_TOTAL_YEAR_EXPERIENCE) {
								if (checklistScore.getScoreType().intValue() == 1) {
									this.getManagedBean()
											.setChecklistScoreType(
													String.valueOf(checklistScore
															.getScoreType()
															.intValue()));
									technicalEmployee.add(checklistScore);
									this.getManagedBean().setCheckListScore(
											technicalEmployee);
								} else if (checklistScore.getScoreType()
										.intValue() == 2) {
									this.getManagedBean()
											.setChecklistScoreType(
													String.valueOf(checklistScore
															.getScoreType()
															.intValue()));
									this.getManagedBean()
											.setScQtCheckListScore(
													checklistScore);
								}

							}

						}

						if (null != this.getManagedBean().getCheckListScore()
								&& this.getManagedBean().getCheckListScore()
										.size() > 1) {
							returnCheckListScores(this.getManagedBean()
									.getCheckListScore());

						}

						if (null != resDTO.getScQtCheckListValue()) {
							checkList.setScQtCheckListValue(resDTO
									.getScQtCheckListValue());
						}

					}
					 this.getManagedBean().setCriteriaType(QTConstants.CRITERIA_TYPE_TOTAL_YEAR_EXPERIENCE);
					this.getManagedBean().setScQtChecklist(checkList);
					return "/xhtml/sc/specification/specificationCommittee/technicalCheckListStaffList.xhtml?faces-redirect=true";
				} else if (QTConstants.TECHNICAL_NUMBER_EMPLOYEES
						.equalsIgnoreCase(checkList.getPmDocument()
								.getDocDefinitionKey())) {

					reqDTO.setScQtCheckList(checkList);
					resDTO = specSo.retrieveCheckListScoreNValue(reqDTO);
					if (null == resDTO.getScQtCheckListValue()) {
						this.getManagedBean().setChecklistScoreType(null);
						this.getManagedBean().setCompanyScoreType(null);
						ScQtCheckListValue checkListValue = new ScQtCheckListValue();
						setUserDetails(checkListValue);

						checkListValue.setScQtCheckList(checkList);
						checkList.setScQtCheckListValue(checkListValue);

					} else {

						List<ScQtCheckListScore> totalEmployee = new ArrayList<ScQtCheckListScore>();

						for (ScQtCheckListScore checklistScore : resDTO
								.getCheckListScores()) {
							if (checklistScore.getCriteriaType() == QTConstants.CRITERIA_TYPE_TOTAL_EMPLOYEE) {
								if (checklistScore.getScoreType().intValue() == 1) {
									this.getManagedBean()
											.setChecklistScoreType(
													String.valueOf(checklistScore
															.getScoreType()
															.intValue()));
									totalEmployee.add(checklistScore);
									this.getManagedBean().setCheckListScore(
											totalEmployee);
								} else if (checklistScore.getScoreType()
										.intValue() == 2) {
									this.getManagedBean()
											.setChecklistScoreType(
													String.valueOf(checklistScore
															.getScoreType()
															.intValue()));
									this.getManagedBean()
											.setScQtCheckListScore(
													checklistScore);
								}

							}

						}
						if (null != this.getManagedBean().getCheckListScore()
								&& this.getManagedBean().getCheckListScore()
										.size() > 1) {
							returnCheckListScores(this.getManagedBean()
									.getCheckListScore());

						}
						if (null != resDTO.getScQtCheckListValue()) {
							checkList.setScQtCheckListValue(resDTO
									.getScQtCheckListValue());
						}
					}
					if (null == checkList.getScQtCheckListValue()) {
						ScQtCheckListValue checkListValue = new ScQtCheckListValue();
						setUserDetails(checkListValue);
						checkListValue.setScQtCheckList(checkList);
						checkList.setScQtCheckListValue(checkListValue);
					}
					 this.getManagedBean().setCriteriaType(QTConstants.CRITERIA_TYPE_TOTAL_EMPLOYEE);
					this.getManagedBean().setScQtChecklist(checkList);
					return "/xhtml/sc/specification/specificationCommittee/technicalCheckListNoOfEmployees.xhtml?faces-redirect=true";
				}

			}
		} finally {
			specSo = null;
		}

		return null;

	}

	/**
	 * Return check list scores.checklist score in ascending order.
	 * 
	 * @param checklistScores
	 *            the checklist scores
	 */
	private void returnCheckListScores(List<ScQtCheckListScore> checklistScores) {

		Collections.sort(checklistScores);

	}

	/**
	 * Financial checklist edit action.click on pencil icon in financial
	 * checklist navigate to checklist score page.
	 * 
	 * @param index
	 *            (int index)
	 * @param checkList
	 *            (ScQtCheckList checkList)
	 * @param specType
	 *            (String specType)
	 * @return navigate to checklist score page.
	 */
	public String financialChecklistEditAction(int index,
			ScQtCheckList checkList, String specType) {
		IQTSpecSO specSo = null;
		SpecificationResDTO resDTO = new SpecificationResDTO();
		this.getManagedBean().setScQtChecklist(checkList);
		this.getManagedBean().setIndex(index);
		this.getManagedBean().setSpecType(specType);
		this.getManagedBean().setToBeRemoveList(
				new ArrayList<ScQtCheckListScore>());
		this.getManagedBean().setScQtCheckListScore(null);
		this.getManagedBean().setScQtContractScore(null);
		try {
			this.getManagedBean().setIndex(index);
			SpecificationReqDTO reqDTO = new SpecificationReqDTO();
			specSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);

			if (QTConstants.TEMPLETE_SPEC_CODE.equalsIgnoreCase(checkList
					.getDefinationType().getParameterCode())) {
				this.getManagedBean().setSpecStatusId(
						checkList.getPmStatus().getStatusId());

				this.getManagedBean().setCheckListTitle(checkList.getTitle());

				ITemplateSpecSO templateSpecSO = EpEjbBusinessDelegate
						.getInstance().getServiceObject(
								ModuleCodeConst.COMMON_COMPONENT,
								ITemplateSpecSO.class);
				TemplateSpecReqDTO specReqDTO = new TemplateSpecReqDTO();
				specReqDTO.setSpecMaster(checkList.getScQtDoc()
						.getTplSpecMaster());
				specReqDTO
						.setContentType(TemplateSpecConst.CONTENT_TYPE_QUESTION);
				TemplateSpecResDTO templateresDTO = templateSpecSO
						.getTemplateMaster(specReqDTO);
				checkList.getScQtDoc().setTplSpecMaster(
						templateresDTO.getSpecMaster());
				TemplateSpecBean templateSpecBean = getManagedBean()
						.getTemplateSpecBean();
				templateSpecBean.setSpecMaster(checkList.getScQtDoc()
						.getTplSpecMaster());

				if (this.getManagedBean().getQt().isZonal()
						&& this.getManagedBean().getQt().getQtType()
								.equals(ParameterConst.QUOTATION_TENDER_TYPE_OPEN)) {
					retriveSpecdetails(this.getManagedBean().getQt().getQtId());
					ScQtSupplierCriteria supplierCriteria = this
							.getManagedBean().getQt().getScQtSupplierCriteria();
					List<TplSpecZoneDtl> businessOwnerZone = convertTreeNodesToZoneCriteria(
							this.getManagedBean().getZoneGeneralTrees(), null);

					if (businessOwnerZone != null
							&& !businessOwnerZone.isEmpty()) {

						for (TplSpecZoneDtl tplSpecZoneDtl : businessOwnerZone) {
							supplierCriteria.getQtZoneCriterias().setModified(
									true);
							tplSpecZoneDtl.setTplSpecZone(supplierCriteria
									.getQtZoneCriterias());

						}
						templateSpecBean.getSpecMaster().setTplSpecZone(
								supplierCriteria.getQtZoneCriterias());
						templateSpecBean.getSpecMaster().getTplSpecZone()
								.setTplSpecZoneDtls(businessOwnerZone);
					}

				}

				return "/xhtml/sc/boSctemplatespec/financialCommitteeTemplate.xhtml?faces-redirect=true";
			} else {
				setUserDetails(checkList);
				 this.getManagedBean().setCriteriaType(0);
                 this.getManagedBean().setOtherCriteriaType(0);
				this.getManagedBean().setCheckListScore(
						new ArrayList<ScQtCheckListScore>());
				this.getManagedBean().setScoringForYears(
						new ArrayList<ScQtCheckListScore>());
				this.getManagedBean().setChecklistScoreType(null);
				this.getManagedBean().setCompanyScoreType(null);
				this.getManagedBean().setToBeRemoveList(
						new ArrayList<ScQtCheckListScore>());
				if (QTConstants.FINANCIAL_GOV_CODE.equalsIgnoreCase(checkList
						.getPmDocument().getDocDefinitionKey())) {
					reqDTO.setScQtCheckList(checkList);
					resDTO = specSo.retrieveCheckListScoreNValue(reqDTO);
					if (null == resDTO.getScQtCheckListValue()) {

						this.getManagedBean().setChecklistScoreType(null);

						ScQtCheckListValue checkListValue = new ScQtCheckListValue();
						setUserDetails(checkListValue);
						checkListValue.setScQtCheckList(checkList);
						checkList.setScQtCheckListValue(checkListValue);

					} else {

						List<ScQtCheckListScore> contarctAmount = new ArrayList<ScQtCheckListScore>();

						for (ScQtCheckListScore checklistScore : resDTO
								.getCheckListScores()) {
							if (checklistScore.getCriteriaType() == QTConstants.CRITERIA_TYPE_TOTAL_CONTRACT_AMOUNT_FOR_FED_GOV) {
								if (checklistScore.getScoreType().intValue() == 1) {
									this.getManagedBean()
											.setChecklistScoreType(
													String.valueOf(checklistScore
															.getScoreType()
															.intValue()));
									contarctAmount.add(checklistScore);
									this.getManagedBean().setCheckListScore(
											contarctAmount);
								} else if (checklistScore.getScoreType()
										.intValue() == 2) {
									this.getManagedBean()
											.setChecklistScoreType(
													String.valueOf(checklistScore
															.getScoreType()
															.intValue()));
									this.getManagedBean()
											.setScQtCheckListScore(
													checklistScore);
								}

							}

						}
						if (null != this.getManagedBean().getCheckListScore()
								&& this.getManagedBean().getCheckListScore()
										.size() > 1) {
							returnCheckListScores(this.getManagedBean()
									.getCheckListScore());

						}
						if (null != resDTO.getScQtCheckListValue()) {
							checkList.setScQtCheckListValue(resDTO
									.getScQtCheckListValue());
						}
					}
					if (null == checkList.getScQtCheckListValue()) {
						ScQtCheckListValue checkListValue = new ScQtCheckListValue();
						setUserDetails(checkListValue);
						checkListValue.setScQtCheckList(checkList);
						checkList.setScQtCheckListValue(checkListValue);
					}
					 this.getManagedBean().setCriteriaType(QTConstants.CRITERIA_TYPE_TOTAL_CONTRACT_AMOUNT_FOR_FED_GOV);
					this.getManagedBean().setScQtChecklist(checkList);
					return "/xhtml/sc/specification/specificationCommittee/financialCheckListGovernementCompany.xhtml?faces-redirect=true";
				} else if (QTConstants.FINANCIAL_PRIVATE_CODE
						.equalsIgnoreCase(checkList.getPmDocument()
								.getDocDefinitionKey())) {

					reqDTO.setScQtCheckList(checkList);
					resDTO = specSo.retrieveCheckListScoreNValue(reqDTO);
					if (null == resDTO.getScQtCheckListValue()) {

						this.getManagedBean().setChecklistScoreType(null);

						ScQtCheckListValue checkListValue = new ScQtCheckListValue();
						setUserDetails(checkListValue);
						checkListValue.setScQtCheckList(checkList);
						checkList.setScQtCheckListValue(checkListValue);

					} else {

						List<ScQtCheckListScore> contarctAmount = new ArrayList<ScQtCheckListScore>();

						for (ScQtCheckListScore checklistScore : resDTO
								.getCheckListScores()) {
							if (checklistScore.getCriteriaType() ==QTConstants.CRITERIA_TYPE_TOTAL_CONTRACT_AMOUNT_FOR_NON_FED_GOV) {
								if (checklistScore.getScoreType().intValue() == 1) {
									this.getManagedBean()
											.setChecklistScoreType(
													String.valueOf(checklistScore
															.getScoreType()
															.intValue()));
									contarctAmount.add(checklistScore);
									this.getManagedBean().setCheckListScore(
											contarctAmount);
								} else if (checklistScore.getScoreType()
										.intValue() == 2) {
									this.getManagedBean()
											.setChecklistScoreType(
													String.valueOf(checklistScore
															.getScoreType()
															.intValue()));
									this.getManagedBean()
											.setScQtCheckListScore(
													checklistScore);
								}

							}

						}
						if (null != this.getManagedBean().getCheckListScore()
								&& this.getManagedBean().getCheckListScore()
										.size() > 1) {
							returnCheckListScores(this.getManagedBean()
									.getCheckListScore());
						}
						if (null != resDTO.getScQtCheckListValue()) {
							checkList.setScQtCheckListValue(resDTO
									.getScQtCheckListValue());
						}
					}
					if (null == checkList.getScQtCheckListValue()) {
						ScQtCheckListValue checkListValue = new ScQtCheckListValue();
						setUserDetails(checkListValue);
						checkListValue.setScQtCheckList(checkList);
						checkList.setScQtCheckListValue(checkListValue);
					}
					this.getManagedBean().setCriteriaType(QTConstants.CRITERIA_TYPE_TOTAL_CONTRACT_AMOUNT_FOR_NON_FED_GOV);
					this.getManagedBean().setScQtChecklist(checkList);
					return "/xhtml/sc/specification/specificationCommittee/financialCheckListPrivateCompany.xhtml?faces-redirect=true";

				} else if (QTConstants.FINANCIAL_CREDIT_FACILITY_CODE
						.equalsIgnoreCase(checkList.getPmDocument()
								.getDocDefinitionKey())) {

					reqDTO.setScQtCheckList(checkList);
					resDTO = specSo.retrieveCheckListScoreNValue(reqDTO);
					if (null == resDTO.getCheckListScores()
							|| resDTO.getCheckListScores().isEmpty()) {
						checkList.setScQtCheckListValue(resDTO
								.getScQtCheckListValue());
						this.getManagedBean().setChecklistScoreType(null);

					} else {

						List<ScQtCheckListScore> creditFacility = new ArrayList<ScQtCheckListScore>();

						for (ScQtCheckListScore checklistScore : resDTO
								.getCheckListScores()) {
							if (checklistScore.getCriteriaType() == QTConstants.CRITERIA_TYPE_TOTAL_CREDIT_FACILITIES) {
								if (checklistScore.getScoreType().intValue() == 1) {
									this.getManagedBean()
											.setChecklistScoreType(
													String.valueOf(checklistScore
															.getScoreType()
															.intValue()));
									creditFacility.add(checklistScore);
									this.getManagedBean().setCheckListScore(
											creditFacility);
								} else if (checklistScore.getScoreType()
										.intValue() == 2) {
									this.getManagedBean()
											.setChecklistScoreType(
													String.valueOf(checklistScore
															.getScoreType()
															.intValue()));
									this.getManagedBean()
											.setScQtCheckListScore(
													checklistScore);
								}

							}

						}
						if (null != this.getManagedBean().getCheckListScore()
								&& this.getManagedBean().getCheckListScore()
										.size() > 1) {
							returnCheckListScores(this.getManagedBean()
									.getCheckListScore());

						}
						if (null != resDTO.getScQtCheckListValue()) {
							checkList.setScQtCheckListValue(resDTO
									.getScQtCheckListValue());
						}
					}
					this.getManagedBean().setCriteriaType(QTConstants.CRITERIA_TYPE_TOTAL_CREDIT_FACILITIES);
					this.getManagedBean().setScQtChecklist(checkList);
					return "/xhtml/sc/specification/specificationCommittee/financialCheckListCreditFacility.xhtml?faces-redirect=true";
				}

				else if (QTConstants.FINANCIAL_CASH_ON_HAND_CODE
						.equalsIgnoreCase(checkList.getPmDocument()
								.getDocDefinitionKey())) {
					reqDTO.setScQtCheckList(checkList);
					resDTO = specSo.retrieveCheckListScoreNValue(reqDTO);
					if (null == resDTO.getCheckListScores()) {
						checkList.setScQtCheckListValue(resDTO
								.getScQtCheckListValue());
						this.getManagedBean().setChecklistScoreType(null);

					} else {

						List<ScQtCheckListScore> cashOnHand = new ArrayList<ScQtCheckListScore>();

						for (ScQtCheckListScore checklistScore : resDTO
								.getCheckListScores()) {
							if (checklistScore.getCriteriaType() ==QTConstants.CRITERIA_TYPE_TOTAL_CASH_ON_HAND) {
								if (checklistScore.getScoreType().intValue() == 1) {
									this.getManagedBean()
											.setChecklistScoreType(
													String.valueOf(checklistScore
															.getScoreType()
															.intValue()));
									cashOnHand.add(checklistScore);
									this.getManagedBean().setCheckListScore(
											cashOnHand);
								} else if (checklistScore.getScoreType()
										.intValue() == 2) {
									this.getManagedBean()
											.setChecklistScoreType(
													String.valueOf(checklistScore
															.getScoreType()
															.intValue()));
									this.getManagedBean()
											.setScQtCheckListScore(
													checklistScore);
								}

							}

						}
						if (null != this.getManagedBean().getCheckListScore()
								&& this.getManagedBean().getCheckListScore()
										.size() > 1) {
							returnCheckListScores(this.getManagedBean()
									.getCheckListScore());

						}
						if (null != resDTO.getScQtCheckListValue()) {
							checkList.setScQtCheckListValue(resDTO
									.getScQtCheckListValue());
						}
					}
					this.getManagedBean().setCriteriaType(QTConstants.CRITERIA_TYPE_TOTAL_CASH_ON_HAND);
					this.getManagedBean().setScQtChecklist(checkList);

					return "/xhtml/sc/specification/specificationCommittee/financialCheckListCashOnhand.xhtml?faces-redirect=true";
				}

				else if (QTConstants.FINANCIAL_BANK_STMT_CODE
						.equalsIgnoreCase(checkList.getPmDocument()
								.getDocDefinitionKey())) {
					int year = Calendar.getInstance().get(Calendar.YEAR);

					reqDTO.setScQtCheckList(checkList);
					resDTO = specSo.retrieveCheckListScoreNValue(reqDTO);
					if (null == resDTO.getScQtCheckListValue()) {
						this.getManagedBean()
								.setStartYear(String.valueOf(year));
						this.getManagedBean().setEndYear(String.valueOf(year));

						this.getManagedBean().setChecklistScoreType(null);

						ScQtCheckListValue checkListValue = new ScQtCheckListValue();
						setUserDetails(checkListValue);

						checkListValue.setScQtCheckList(checkList);
						checkList.setScQtCheckListValue(checkListValue);

					} else {

						List<ScQtCheckListScore> bankStatement = new ArrayList<ScQtCheckListScore>();

						for (ScQtCheckListScore checklistScore : resDTO
								.getCheckListScores()) {
							if (checklistScore.getCriteriaType() ==QTConstants.CRITERIA_TYPE_AVERAGE_OF_BANK_STATEMENT) {
								if (checklistScore.getScoreType().intValue() == 1) {
									this.getManagedBean()
											.setChecklistScoreType(
													String.valueOf(checklistScore
															.getScoreType()
															.intValue()));
									bankStatement.add(checklistScore);
									this.getManagedBean().setCheckListScore(
											bankStatement);
								} else if (checklistScore.getScoreType()
										.intValue() == 2) {
									this.getManagedBean()
											.setChecklistScoreType(
													String.valueOf(checklistScore
															.getScoreType()
															.intValue()));
									this.getManagedBean()
											.setScQtCheckListScore(
													checklistScore);
								}

							}

						}
						if (null != this.getManagedBean().getCheckListScore()
								&& this.getManagedBean().getCheckListScore()
										.size() > 1) {
							returnCheckListScores(this.getManagedBean()
									.getCheckListScore());

						}
						if (null != resDTO.getScQtCheckListValue()) {
							checkList.setScQtCheckListValue(resDTO
									.getScQtCheckListValue());
							retriveFromMonthToManthDtls(resDTO
									.getScQtCheckListValue());
						}
					}
					if (null == checkList.getScQtCheckListValue()) {

						this.getManagedBean()
								.setStartYear(String.valueOf(year));
						this.getManagedBean().setEndYear(String.valueOf(year));

						// this.getManagedBean().setChecklistScoreType(null);

						ScQtCheckListValue checkListValue = new ScQtCheckListValue();
						setUserDetails(checkListValue);

						checkListValue.setScQtCheckList(checkList);
						checkList.setScQtCheckListValue(checkListValue);

					}
					this.getManagedBean().setCriteriaType(QTConstants.CRITERIA_TYPE_AVERAGE_OF_BANK_STATEMENT);
					this.getManagedBean().setScQtChecklist(checkList);
					return "/xhtml/sc/specification/specificationCommittee/financialCheckListBankStatement.xhtml?faces-redirect=true";
				}

				else if (QTConstants.FINANCIAL_ASSET_LIABILITY_CODE
						.equalsIgnoreCase(checkList.getPmDocument()
								.getDocDefinitionKey())) {

					reqDTO.setScQtCheckList(checkList);
					resDTO = specSo.retrieveCheckListScoreNValue(reqDTO);
					if (null == resDTO.getCheckListScores()) {
						checkList.setScQtCheckListValue(resDTO
								.getScQtCheckListValue());
						this.getManagedBean().setChecklistScoreType(null);

					} else {

						List<ScQtCheckListScore> assetLiability = new ArrayList<ScQtCheckListScore>();
						List<ScQtCheckListScore> currentRation = new ArrayList<ScQtCheckListScore>();

						for (ScQtCheckListScore checklistScore : resDTO
								.getCheckListScores()) {
							if (checklistScore.getCriteriaType() ==QTConstants.CRITERIA_TYPE_ASSETS_AND_LIABILITIES_ACID_RATIO) {
								if (checklistScore.getScoreType().intValue() == 1) {
									this.getManagedBean()
											.setChecklistScoreType(
													String.valueOf(checklistScore
															.getScoreType()
															.intValue()));
									assetLiability.add(checklistScore);
									this.getManagedBean().setCheckListScore(
											assetLiability);
								} else if (checklistScore.getScoreType()
										.intValue() == 2) {
									this.getManagedBean()
											.setChecklistScoreType(
													String.valueOf(checklistScore
															.getScoreType()
															.intValue()));
									this.getManagedBean()
											.setScQtCheckListScore(
													checklistScore);
								}

							} else if (checklistScore.getCriteriaType() == QTConstants.CRITERIA_TYPE_ASSETS_AND_LIABILITIES_CURRENT_RATIO) {

								if (checklistScore.getScoreType().intValue() == 1) {

									this.getManagedBean()
											.setCompanyScoreType(
													String.valueOf(checklistScore
															.getScoreType()
															.intValue()));
									currentRation.add(checklistScore);
									this.getManagedBean().setScoringForYears(
											currentRation);
								} else if (checklistScore.getScoreType()
										.intValue() == 2) {
									this.getManagedBean()
											.setCompanyScoreType(
													String.valueOf(checklistScore
															.getScoreType()
															.intValue()));
									this.getManagedBean().setScQtContractScore(
											checklistScore);
								}

							}

						}
						if (null != this.getManagedBean().getCheckListScore()
								&& this.getManagedBean().getCheckListScore()
										.size() > 1) {
							returnCheckListScores(this.getManagedBean()
									.getCheckListScore());

						}
						if (null != this.getManagedBean().getScoringForYears()
								&& this.getManagedBean().getScoringForYears()
										.size() > 1) {
							returnCheckListScores(this.getManagedBean()
									.getScoringForYears());

						}
						if (null != resDTO.getScQtCheckListValue()) {
							checkList.setScQtCheckListValue(resDTO
									.getScQtCheckListValue());
						}
					}
					this.getManagedBean().setCriteriaType(QTConstants.CRITERIA_TYPE_ASSETS_AND_LIABILITIES_ACID_RATIO);
					this.getManagedBean().setOtherCriteriaType(QTConstants.CRITERIA_TYPE_ASSETS_AND_LIABILITIES_CURRENT_RATIO);
					this.getManagedBean().setScQtChecklist(checkList);
					return "/xhtml/sc/specification/specificationCommittee/financialCheckListAssetsLiabilities.xhtml?faces-redirect=true";
				} else if (QTConstants.FINANCIAL_SUPPLIER_DETAILS_CODE
						.equalsIgnoreCase(checkList.getPmDocument()
								.getDocDefinitionKey())) {
					reqDTO.setScQtCheckList(checkList);
					resDTO = specSo.retrieveCheckListScoreNValue(reqDTO);
					if (null == resDTO.getCheckListScores()
							|| resDTO.getCheckListScores().isEmpty()) {
						checkList.setScQtCheckListValue(resDTO
								.getScQtCheckListValue());
						this.getManagedBean().setChecklistScoreType(
								QTConstants.USER_SCORE);

						ScQtCheckListScore checklistScore = new ScQtCheckListScore();
						checklistScore.setCriteriaType(QTConstants.CRITERIA_TYPE_SUPPLIER_DETAILS);
						checklistScore.setScoreType(new BigDecimal(this
								.getManagedBean().getChecklistScoreType()));
						setUserDetails(checklistScore);
						this.getManagedBean().setScQtCheckListScore(
								checklistScore);

					} else {

						List<ScQtCheckListScore> suppliersScore = new ArrayList<ScQtCheckListScore>();

						for (ScQtCheckListScore checklistScore : resDTO
								.getCheckListScores()) {
							if (checklistScore.getCriteriaType() ==QTConstants.CRITERIA_TYPE_SUPPLIER_DETAILS) {
								if (checklistScore.getScoreType().intValue() == 1) {
									this.getManagedBean()
											.setChecklistScoreType(
													String.valueOf(checklistScore
															.getScoreType()
															.intValue()));
									suppliersScore.add(checklistScore);
									this.getManagedBean().setCheckListScore(
											suppliersScore);
								} else if (checklistScore.getScoreType()
										.intValue() == 2) {
									this.getManagedBean()
											.setChecklistScoreType(
													String.valueOf(checklistScore
															.getScoreType()
															.intValue()));
									this.getManagedBean()
											.setScQtCheckListScore(
													checklistScore);
								}

							}

						}
						if (null != this.getManagedBean().getCheckListScore()
								&& this.getManagedBean().getCheckListScore()
										.size() > 1) {
							returnCheckListScores(this.getManagedBean()
									.getCheckListScore());

						}
						if (null != resDTO.getScQtCheckListValue()) {
							checkList.setScQtCheckListValue(resDTO
									.getScQtCheckListValue());
						}
					}
					this.getManagedBean().setCriteriaType(QTConstants.CRITERIA_TYPE_SUPPLIER_DETAILS);
					this.getManagedBean().setScQtChecklist(checkList);
					return "/xhtml/sc/specification/specificationCommittee/financialChecklistSupplierinfoMain.xhtml?faces-redirect=true";
				} else if (QTConstants.FINANCIAL_PAID_UP_CAPITAL_CODE
						.equalsIgnoreCase(checkList.getPmDocument()
								.getDocDefinitionKey())) {

					reqDTO.setScQtCheckList(checkList);
					resDTO = specSo.retrieveCheckListScoreNValue(reqDTO);
					if (null == resDTO.getCheckListScores()) {

						this.getManagedBean().setChecklistScoreType(null);

					} else {

						List<ScQtCheckListScore> paidUpCapital = new ArrayList<ScQtCheckListScore>();

						for (ScQtCheckListScore checklistScore : resDTO
								.getCheckListScores()) {
							if (checklistScore.getCriteriaType() ==QTConstants.CRITERIA_TYPE_PAID_UP_CAPITAL_AMOUNT) {
								if (checklistScore.getScoreType().intValue() == 1) {
									this.getManagedBean()
											.setChecklistScoreType(
													String.valueOf(checklistScore
															.getScoreType()
															.intValue()));
									paidUpCapital.add(checklistScore);
									this.getManagedBean().setCheckListScore(
											paidUpCapital);
								} else if (checklistScore.getScoreType()
										.intValue() == 2) {
									this.getManagedBean()
											.setChecklistScoreType(
													String.valueOf(checklistScore
															.getScoreType()
															.intValue()));
									this.getManagedBean()
											.setScQtCheckListScore(
													checklistScore);
								}

							}

						}
						if (null != this.getManagedBean().getCheckListScore()
								&& this.getManagedBean().getCheckListScore()
										.size() > 1) {
							returnCheckListScores(this.getManagedBean()
									.getCheckListScore());

						}
						if (null != resDTO.getScQtCheckListValue()) {
							checkList.setScQtCheckListValue(resDTO
									.getScQtCheckListValue());
						}
					}
					this.getManagedBean().setCriteriaType(QTConstants.CRITERIA_TYPE_PAID_UP_CAPITAL_AMOUNT);
					this.getManagedBean().setScQtChecklist(checkList);
					return "/xhtml/sc/specification/specificationCommittee/financialCheckListPaidUpCapital.xhtml?faces-redirect=true";
				}
			}
		} finally {
			specSo = null;
		}

		return null;

	}

	/**
	 * Retrive from month to manth dtls.set Frommonth and tomonth for
	 * bankstatement checklist.
	 * 
	 * @param checkListValue
	 *            (ScQtCheckListValue checkListValue).
	 */
	private void retriveFromMonthToManthDtls(ScQtCheckListValue checkListValue) {
		if (null != checkListValue) {
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
			String startMonth = "";
			String endMonth = "";
			String endToMonth = "";
			if (null != checkListValue.getFromDate()
					&& null != checkListValue.getToDate()) {
				String fromdate = dateFormat.format(checkListValue
						.getFromDate());
				String todate = dateFormat.format(checkListValue.getToDate());
				String startdate = fromdate.substring(3, 5);
				String startYear = fromdate.substring(6, fromdate.length());
				String enddate = todate.substring(3, 5);
				String endYear = todate.substring(6, todate.length());

				if (startdate.equalsIgnoreCase("01")) {
					startMonth = "0";
				}
				if (startdate.equalsIgnoreCase("02")) {
					startMonth = "1";
				}
				if (startdate.equalsIgnoreCase("03")) {
					startMonth = "2";
				}
				if (startdate.equalsIgnoreCase("04")) {
					startMonth = "3";
				}
				if (startdate.equalsIgnoreCase("05")) {
					startMonth = "4";
				}
				if (startdate.equalsIgnoreCase("06")) {
					startMonth = "5";
				}

				if (startdate.equalsIgnoreCase("07")) {
					startMonth = "6";
				}
				if (startdate.equalsIgnoreCase("08")) {
					startMonth = "7";
				}
				if (startdate.equalsIgnoreCase("09")) {
					startMonth = "8";
				}
				if (startdate.equalsIgnoreCase("10")) {
					startMonth = "9";
				}
				if (startdate.equalsIgnoreCase("11")) {
					startMonth = "10";
				}
				if (startdate.equalsIgnoreCase("12")) {
					startMonth = "11";
				}

				if (enddate.equalsIgnoreCase("01")) {
					endToMonth = "0";
					endMonth = ResourceBundleUtils
							.getMessage("lbl.sc.checklist.month.january",
									ResourceBundleUtils.BUNDLE_COMMON_LABELS,
									new FacesScopes().getUserLocale());
				}
				if (enddate.equalsIgnoreCase("02")) {
					endToMonth = "1";
					endMonth = ResourceBundleUtils
							.getMessage("lbl.sc.checklist.month.february",
									ResourceBundleUtils.BUNDLE_COMMON_LABELS,
									new FacesScopes().getUserLocale());
				}
				if (enddate.equalsIgnoreCase("03")) {
					endToMonth = "2";
					endMonth = ResourceBundleUtils
							.getMessage("lbl.sc.checklist.month.march",
									ResourceBundleUtils.BUNDLE_COMMON_LABELS,
									new FacesScopes().getUserLocale());
				}
				if (enddate.equalsIgnoreCase("04")) {
					endToMonth = "3";
					endMonth = ResourceBundleUtils
							.getMessage("lbl.sc.checklist.month.april",
									ResourceBundleUtils.BUNDLE_COMMON_LABELS,
									new FacesScopes().getUserLocale());
				}
				if (enddate.equalsIgnoreCase("05")) {
					endToMonth = "4";
					endMonth = ResourceBundleUtils
							.getMessage("lbl.sc.checklist.month.may",
									ResourceBundleUtils.BUNDLE_COMMON_LABELS,
									new FacesScopes().getUserLocale());
				}
				if (enddate.equalsIgnoreCase("06")) {
					endToMonth = "5";
					endMonth = ResourceBundleUtils
							.getMessage("lbl.sc.checklist.month.june",
									ResourceBundleUtils.BUNDLE_COMMON_LABELS,
									new FacesScopes().getUserLocale());
				}

				if (enddate.equalsIgnoreCase("07")) {
					endToMonth = "6";
					endMonth = ResourceBundleUtils
							.getMessage("lbl.sc.checklist.month.july",
									ResourceBundleUtils.BUNDLE_COMMON_LABELS,
									new FacesScopes().getUserLocale());
				}
				if (enddate.equalsIgnoreCase("08")) {
					endToMonth = "7";
					endMonth = ResourceBundleUtils
							.getMessage("lbl.sc.checklist.month.august",
									ResourceBundleUtils.BUNDLE_COMMON_LABELS,
									new FacesScopes().getUserLocale());
				}
				if (enddate.equalsIgnoreCase("09")) {
					endToMonth = "8";
					endMonth = ResourceBundleUtils
							.getMessage("lbl.sc.checklist.month.september",
									ResourceBundleUtils.BUNDLE_COMMON_LABELS,
									new FacesScopes().getUserLocale());
				}
				if (enddate.equalsIgnoreCase("10")) {
					endToMonth = "9";
					endMonth = ResourceBundleUtils
							.getMessage("lbl.sc.checklist.month.october",
									ResourceBundleUtils.BUNDLE_COMMON_LABELS,
									new FacesScopes().getUserLocale());
				}
				if (enddate.equalsIgnoreCase("11")) {
					endToMonth = "10";
					endMonth = ResourceBundleUtils
							.getMessage("lbl.sc.checklist.month.november",
									ResourceBundleUtils.BUNDLE_COMMON_LABELS,
									new FacesScopes().getUserLocale());
				}
				if (enddate.equalsIgnoreCase("12")) {
					endToMonth = "11";
					endMonth = ResourceBundleUtils
							.getMessage("lbl.sc.checklist.month.december",
									ResourceBundleUtils.BUNDLE_COMMON_LABELS,
									new FacesScopes().getUserLocale());
				}
				this.getManagedBean().setFromMonth(startMonth);
				this.getManagedBean().setBankstmntTomonth(endMonth);
				this.getManagedBean().setStartYear(startYear);
				this.getManagedBean().setEndYear(endYear);
				this.getManagedBean().setToMonth(endToMonth);
			}
		}
	}

	/**
	 * Committee spec edit action.Event handle navigate to Template Spec in
	 * Specification committee.
	 * 
	 * @param index
	 *            (int index)
	 * @param checklist
	 *            (ScQtCheckList checklist)
	 * @param specType
	 *            (String specType)
	 * @return navigate to template spec.
	 */
	public String committeeSpecEditAction(int index, ScQtCheckList checklist,
			String specType) {

		ITemplateSpecSO templateSpecSO = null;
		try {

			this.getManagedBean().setSpecStatusId(
					checklist.getPmStatus().getStatusId());
			this.getManagedBean().setIndex(index);
			this.getManagedBean().setScQtChecklist(checklist);
			this.getManagedBean().setCheckListTitle(checklist.getTitle());

			templateSpecSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.COMMON_COMPONENT,
							ITemplateSpecSO.class);
			TemplateSpecReqDTO specReqDTO = new TemplateSpecReqDTO();
			specReqDTO.setSpecMaster(checklist.getScQtDoc().getTplSpecMaster());
			specReqDTO.setContentType(TemplateSpecConst.CONTENT_TYPE_QUESTION);
			TemplateSpecResDTO resDTO = templateSpecSO
					.getTemplateMaster(specReqDTO);
			checklist.getScQtDoc().setTplSpecMaster(resDTO.getSpecMaster());
			TemplateSpecBean templateSpecBean = getManagedBean()
					.getTemplateSpecBean();
			templateSpecBean.setSpecMaster(checklist.getScQtDoc()
					.getTplSpecMaster());
			if (null != checklist.getPmStatus()
					&& QTStatusConst.TS_PENDING_SUBMISSION.equals(checklist
							.getPmStatus().getStatusId())) {
				ScQtSupplierCriteria supplierCriteria = this.getManagedBean()
						.getQt().getScQtSupplierCriteria();
				ScQtZoneCriteria zoneCriteria = null;
				if (null != supplierCriteria
						&& null != supplierCriteria.getQtZoneCriterias()) {
					zoneCriteria = supplierCriteria.getQtZoneCriterias();
				}
				TemplateSpecHelper.getInstance().updateZoneDtl(
						templateSpecBean.getSpecMaster(), zoneCriteria);
			}
			if (this.getManagedBean().getQt().isPanel()) {
				if (null == this.getManagedBean().getPanelItemCategory()
						|| "".equals(this.getManagedBean()
								.getPanelItemCategory())) {
					retriveSupplierQualificationCriteria("SC");

				}
				if ((null != this.getManagedBean().getPanelItemCategory() && !""
						.equals(this.getManagedBean().getPanelItemCategory()))
						&& ParameterConst.ITEM_CATEGORY_TYPE_SPECIFIC
								.equalsIgnoreCase(this.getManagedBean()
										.getPanelItemCategory())) {
					this.getManagedBean().setSpecNewable(false);
				}
			}
			/*
			 * templateSpecBean.getSpecMaster().setScoringReq(
			 * this.getManagedBean().getQt().isTechEvaluationReq());
			 */

		} finally {
			templateSpecSO = null;
		}
		return "/xhtml/sc/boSctemplatespec/committeeTechnicalEditAction.xhtml?faces-redirect=true";
	}

	/**
	 * Creates the spec list.
	 * 
	 * @param stage
	 *            the stage
	 * @return the sc qt check list
	 */
	private ScQtCheckList createSpecList(String stage) {

		ScQtCheckList additionProposalst1 = new ScQtCheckList();

		return additionProposalst1;
	}

	/**
	 * Adds the financial check list.add empty row for financial checklist
	 * datatable.
	 */
	public void addFinancialCheckList() {
		// IParameterSO parameterSo = null;
		try {
			/*
			 * parameterSo = BusinessDelegate.getInstance().getServiceObject(
			 * ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
			 */
			List<ScQtCheckList> st2financeproposalList = getManagedBean()
					.getScQtCheckSt2financeProposalList();
			if (null == st2financeproposalList) {
				st2financeproposalList = new ArrayList<ScQtCheckList>();

			}

			ScQtCheckList st2financeproposalList2 = createSpecList("2");

			// st2financeproposalList2.setDefinationType(1);
			// st2financeproposalList2.setScoringReq(true);
			st2financeproposalList2
					.setDocCategory(QTConstants.FINANCIAL_DOC_TYPE);
			st2financeproposalList2.setScoringReq(false);
			st2financeproposalList2
					.setChecklistSeq(SourcingConstants.CHECKLIST_SEQ_UPLOAD);

			st2financeproposalList2.setScQt(this.getManagedBean().getQt());

			/*
			 * ParameterReqDTO docDefTypeReqDTO = new ParameterReqDTO();
			 * docDefTypeReqDTO
			 * .setParameterType(ParameterTypeConst.DOCUMENT_DEFINITION_TYPE);
			 * docDefTypeReqDTO
			 * .setParameterCode(ParameterConst.DOCUMENT_DEFINITION_TYPE_UPLOAD
			 * ); ParameterResDTO docDefTypeResDTO = parameterSo
			 * .retrieveParameterByTypeAndCode(docDefTypeReqDTO);
			 */
			st2financeproposalList2.setDefinationType(new PmParameter());
			st2financeproposalList2.setAction(new PmParameter());

			setUserDetails(st2financeproposalList2);

			st2financeproposalList2.setPmStatus(getPmStatusByStatusId(QTStatusConst.TS_PENDING_SUBMISSION));
			// st2financeproposalList2.setEvaluationStage("2");
			st2financeproposalList2.setPmEvalStatus(getPmStatusByStatusId(QTStatusConst.TS_PENDING_SUBMISSION));
			st2financeproposalList.add(st2financeproposalList2);

			this.getManagedBean().setScQtCheckSt2financeProposalList(
					st2financeproposalList);
			this.getManagedBean().setFinStandaraChecklist(false);
		} finally {
		}
	}

	/**
	 * Adds the technical compliance list.add standard checklist to technical
	 * checklist in specification committee.
	 */
	public void addTechnicalComplianceList() {

		try {

			List<ScQtCheckList> compilance = this.getManagedBean()
					.getScQtCheckSt2techProposal();

			List<PmDocument> selectList = this.getManagedBean()
					.getStageTwoTechComplianceSelect();
			this.getManagedBean().setTechStandaraChecklist(false);
			if (null == selectList || selectList.isEmpty()) {
				this.getManagedBean().setTechStandaraChecklist(true);
				throw new EpException(MessageConst.ERROR_760147);
			}

			for (int i = 0; i < selectList.size(); i++) {
				PmDocument document = selectList.get(i);
				boolean isRequired = false;
				for (ScQtCheckList techStandard : compilance) {
					if (techStandard.getTitle().equals(document.getDocName())) {
						this.getManagedBean().setTechStandaraChecklist(true);
						isRequired = true;
						throw new EpException(document.getDocName() + " "
								+ "is Existed");
					}
				}
				if (!isRequired) {
					this.getManagedBean().setTechStandaraChecklist(true);
					ScQtCheckList scqtlist = createSpecList("2");

					scqtlist.setTitle(document.getDocName());
					scqtlist.setDefinationType(document
							.getDocDefinitionTypeCode());
					scqtlist.setAction(document.getChecklistActionCode());
					scqtlist.setManditory(false);
					scqtlist.setScoringReq(document.isScoringReq());
					scqtlist.setDocCategory(QTConstants.TECHNICAL_DOC_TYPE);
					// scqtlist.setChecklistSeq(document.getDisplaySeq());
					scqtlist.setPmDocument(document);
					setUserDetails(scqtlist);
					if (null == document.getDisplaySeq()) {
						scqtlist.setChecklistSeq(new Integer(10));
					} else {
						scqtlist.setChecklistSeq(new Integer(document
								.getDisplaySeq()));
					}
					scqtlist.setScQt(this.getManagedBean().getQt());

					// scqtlist.setQtScoreWeightage(new ScQtScoreWeightage());

					if (null != scqtlist.getDefinationType()
							&& !ParameterConst.DOCUMENT_DEFINITION_TYPE_UPLOAD
									.equalsIgnoreCase(scqtlist
											.getDefinationType()
											.getParameterCode())) {
						Long statusId = null;
						if (!scqtlist.isScoringReq()) {
							statusId = QTStatusConst.TS_COMPLETED;
						} else if (scqtlist.isScoringReq()) {
							statusId = QTStatusConst.TS_PENDING_SUBMISSION;
						}
						scqtlist.setPmStatus(getPmStatusByStatusId(statusId));
					}
					if (null != scqtlist.getDefinationType()
							&& ParameterConst.DOCUMENT_DEFINITION_TYPE_UPLOAD
									.equalsIgnoreCase(scqtlist
											.getDefinationType()
											.getParameterCode())) {
						scqtlist.setPmStatus(getPmStatusByStatusId(QTStatusConst.TS_PENDING_SUBMISSION));
					}
					scqtlist.setPmEvalStatus(getPmStatusByStatusId(QTStatusConst.TS_PENDING_SUBMISSION));
					compilance.add(scqtlist);
				}

			}

			getManagedBean().setScQtCheckSt2techProposal(compilance);

		} finally {

		}
		this.getManagedBean().setAutoUpdate(true);
		RequestContext.getCurrentInstance().execute("stageTwoTech.hide()");
	}

	/**
	 * Adds the technical check list.add empty row for Technical checklist
	 * datatable.
	 */
	public void addTechnicalCheckList() {
		try {
			List<ScQtCheckList> st2technicalproposalList = getManagedBean()
					.getScQtCheckSt2techProposal();

			if (null == st2technicalproposalList) {
				st2technicalproposalList = new ArrayList<ScQtCheckList>();

			}

			ScQtCheckList st2technicalproposalList1 = createSpecList("2");
			st2technicalproposalList1.setDefinationType(new PmParameter());
			st2technicalproposalList1
					.setDocCategory(QTConstants.TECHNICAL_DOC_TYPE);
			st2technicalproposalList1.setScoringReq(false);
			st2technicalproposalList1
					.setChecklistSeq(SourcingConstants.CHECKLIST_SEQ_UPLOAD);

			st2technicalproposalList1.setScQt(this.getManagedBean().getQt());
			st2technicalproposalList1.setAction(new PmParameter());

			setUserDetails(st2technicalproposalList1);
			st2technicalproposalList1.setPmStatus(getPmStatusByStatusId(QTStatusConst.TS_PENDING_SUBMISSION));
			st2technicalproposalList1.setPmEvalStatus(getPmStatusByStatusId(QTStatusConst.TS_PENDING_SUBMISSION));

			st2technicalproposalList.add(st2technicalproposalList1);

			this.getManagedBean().setScQtCheckSt2techProposal(
					st2technicalproposalList);
			this.getManagedBean().setTechStandaraChecklist(false);
		} finally {
		}
	}

	/**
	 * Adds the financial compliance list.add standard checklist to financial
	 * checklist datatable.
	 */
	public void addFinancialComplianceList() {
		try {

			List<ScQtCheckList> compilance = this.getManagedBean()
					.getScQtCheckSt2financeProposalList();

			List<PmDocument> selectList = this.getManagedBean()
					.getStageTwoFinComplianceSelect();
			this.getManagedBean().setFinStandaraChecklist(false);
			if (compilance == null) {
				compilance = new ArrayList<ScQtCheckList>();
			}
			if (null == selectList || selectList.isEmpty()) {
				this.getManagedBean().setFinStandaraChecklist(true);
				throw new EpException(MessageConst.ERROR_760147);
			}

			for (int i = 0; i < selectList.size(); i++) {
				PmDocument document = selectList.get(i);
				boolean isRequired = false;
				for (ScQtCheckList techStandard : compilance) {
					if (techStandard.getTitle().equals(document.getDocName())) {
						isRequired = true;
						this.getManagedBean().setFinStandaraChecklist(true);
						throw new EpException(document.getDocName() + " "
								+ "is Existed");
					}
				}
				if (!isRequired) {
					this.getManagedBean().setFinStandaraChecklist(true);
					ScQtCheckList scqtlist = createSpecList("2");
					scqtlist.setTitle(document.getDocName());
					scqtlist.setDocCategory(QTConstants.FINANCIAL_DOC_TYPE);
					scqtlist.setScoringReq(document.isScoringReq());
					// scqtlist.setEnable(true);
					scqtlist.setDefinationType(document
							.getDocDefinitionTypeCode());
					scqtlist.setAction(document.getChecklistActionCode());
					// scqtlist.setEvaluationStage("2");
					scqtlist.setManditory(false);
					// scqtlist.setChecklistSeq(document.getDisplaySeq());
					scqtlist.setPmDocument(document);
					if (null == document.getDisplaySeq()) {
						scqtlist.setChecklistSeq(new Integer(10));
					} else {
						scqtlist.setChecklistSeq(new Integer(document
								.getDisplaySeq()));
					}
					scqtlist.setScQt(this.getManagedBean().getQt());

					setUserDetails(scqtlist);
					// scqtlist.setQtScoreWeightage(new ScQtScoreWeightage());

					if (null != scqtlist.getDefinationType()
							&& !ParameterConst.DOCUMENT_DEFINITION_TYPE_UPLOAD
									.equalsIgnoreCase(scqtlist
											.getDefinationType()
											.getParameterCode())) {
						Long statusId = null;
						if (!scqtlist.isScoringReq()) {
							statusId = QTStatusConst.TS_COMPLETED;
						} else if (scqtlist.isScoringReq()) {
							statusId = QTStatusConst.TS_PENDING_SUBMISSION;
						}
						scqtlist.setPmStatus(getPmStatusByStatusId(statusId));
					}
					if (null != scqtlist.getDefinationType()
							&& ParameterConst.DOCUMENT_DEFINITION_TYPE_UPLOAD
									.equalsIgnoreCase(scqtlist
											.getDefinationType()
											.getParameterCode())) {
						scqtlist.setPmStatus(getPmStatusByStatusId(QTStatusConst.TS_PENDING_SUBMISSION));
					}

					scqtlist.setPmEvalStatus(getPmStatusByStatusId(QTStatusConst.TS_PENDING_SUBMISSION));
					compilance.add(scqtlist);
				}
			}

			getManagedBean().setScQtCheckSt2financeProposalList(compilance);

		} finally {
		}
		this.getManagedBean().setAutoUpdate(true);
		RequestContext.getCurrentInstance().execute("stageTwoFinance.hide();");
	}

	/**
	 * Retrive technical standard check list.retrieve technical standard
	 * checklist from pmdocument.
	 */
	public void retriveTechnicalStandardCheckList() {
		IDocumentSO documentSo;
		DocumentResDTO resDTO;
		try {

			documentSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IDocumentSO.class);
			DocumentReqDTO reqDTO = new DocumentReqDTO();
			procurementCategoryCode(this.getManagedBean().getQt());
			reqDTO.setProcurementCatCode(this.getManagedBean()
					.getProcuremntCategory());
			reqDTO.setIsMandatory(false);
			reqDTO.setDocCategoryCode(ParameterConst.DOCUMENT_CATEGORY_TECHNICAL_CHECKLIST);

			resDTO = documentSo.retrieveDocumentCheckList(reqDTO);

			this.getManagedBean().setStageTwoTechComplianceSelect(
					new ArrayList<PmDocument>());
			List<PmDocument> localcheckList = new ArrayList<PmDocument>();
			if (!isAtleastOneSeriveReq(this.getManagedBean()
					.getScQtCheckSt2techProposal())) {
				if (null != resDTO.getDocumentList()) {
					// Collections.sort(resDTO.getTechnicalCheckList());
					for (PmDocument checklist : resDTO.getDocumentList()) {

						if (!SourcingConstants.DOC_DEF_KEY_SERVICE_SCOPE
								.equalsIgnoreCase(checklist

								.getDocDefinitionKey())) {
							localcheckList.add(checklist);
						}

					}
				}
			} else {
				// Collections.sort(resDTO.getTechnicalCheckList());
				localcheckList = new ArrayList<PmDocument>(
						resDTO.getDocumentList());
				// localcheckList.addAll(resDTO.getTechnicalCheckList());
			}
			this.getManagedBean().setAutoUpdate(false);
			this.getManagedBean().setStageTwoTechComplianceList(localcheckList);
		} finally {
			documentSo = null;
		}

	}

	/**
	 * Delivery list.delivery address zones tab change.
	 * 
	 * @param zoneDetail
	 *            (TplSpecZoneDtl zoneDetail).
	 */
	public void deliveryList(TplSpecZoneDtl zoneDetail) {
		log.info("deliveryList - START");
		IOrgProfileSO orgProfileSO = null;

		getManagedBean().setStateId(null);
		getManagedBean().setDistrictId(null);
		this.getManagedBean().setStateCode(null);
		this.getManagedBean().setDistrictCode(null);
		this.getManagedBean().setDivisionCode(null);
		this.getManagedBean().setDeliveryAddressSelect(null);
		this.getManagedBean().setPtjtList(null);
		getManagedBean().setAutoUpdate(Boolean.FALSE);

		IPagingDataModel<PmAddressType> searchDeliveryAddress = getManagedBean()
				.getSearchDeliveryAddressDataModel();
		if (searchDeliveryAddress == null) {
			getManagedBean().setSearchDeliveryAddressDataModel(
					new DeliveryPagingDataModel("deliveryAddress"));
		}
		getManagedBean().getSearchDeliveryAddressDataModel().resetData();
		if (null != this.getManagedBean().getQt().getPmOrgProfile()) {
			orgProfileSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IOrgProfileSO.class);
			this.getManagedBean().setPtjId(
					this.getManagedBean().getQt().getPmOrgProfile()
							.getOrgProfileId());
			OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
			reqDTO.setOrgProfileId(this.getManagedBean().getQt()
					.getPmOrgProfile().getOrgProfileId());
			OrgProfileResDTO resDTO = orgProfileSO
					.retrieveMinistryByOrgProfileId(reqDTO);
			if (resDTO.getPmOrgProfile() != null) {
				this.getManagedBean().setMinistryId(
						resDTO.getPmOrgProfile().getOrgProfileId());
			}
			if (null != this.getManagedBean().getMinistryId()) {
				retrievePTJGroupListByMinistry(this.getManagedBean()
						.getMinistryId());
			}

			OrgProfileResDTO pjtGroup = orgProfileSO
					.retrieveJabatanByOrgProfileId(reqDTO);
			if (pjtGroup.getPmOrgProfile() != null) {
				this.getManagedBean().setPtjGroupId(
						pjtGroup.getPmOrgProfile().getOrgProfileId());
				/*
				 * this.getManagedBean().setPtjtList(
				 * pjtGroup.getPmOrgProfile().getChildOrgProfiles());
				 */
			}

			if (null != this.getManagedBean().getPtjGroupId()) {
				retrivePTJByPTJGroup(this.getManagedBean().getPtjGroupId());

			}

		}
		retriveMinistryList();
		if (getManagedBean().getQt().isZonal()) {
			this.getManagedBean().setTplSpecZoneDtl(zoneDetail);
			List<String> codes = new ArrayList<String>();
			List<PmState> pmStates = new ArrayList<PmState>();
			if (null != zoneDetail.getTplSpecZoneLocs()) {
				for (TplSpecZoneLoc specZoneLoc : zoneDetail
						.getTplSpecZoneLocs()) {
					PmState pmState = specZoneLoc.getPmState();
					if (null != pmState) {
						if (!codes.contains(pmState.getStateCode())) {
							codes.add(pmState.getStateCode());
							pmStates.add(pmState);
						}
					}
					this.getManagedBean().setStateList(pmStates);
				}
			}
		} else {
			retriveStateList();
			log.info("Non Zonal");
		}
		log.info("deliveryList - END");
	}

	/**
	 * Zone delivery change.
	 * 
	 * @param event
	 *            the event
	 */
	public void zoneDeliveryChange(TabChangeEvent event) {

	}

	/**
	 * Delivery address reset.to clear all the delivery address data.
	 */
	public void deliveryAddressReset() {
		try {
			getManagedBean().setStateId(null);
			getManagedBean().setDistrictId(null);
			getManagedBean().setMinistryId(null);
			getManagedBean().setDepartmentId(null);
			getManagedBean().setPtjId(null);
			getManagedBean().setPtjGroupId(null);
			this.getManagedBean().setStateCode(null);
			this.getManagedBean().setDistrictCode(null);
			this.getManagedBean().setDivisionCode(null);
		} finally {

		}
	}

	/**
	 * Delivery accept.added delivery addresses to delivery address data table.
	 */
	public void deliveryAccept() {
		IAddressSO addressSo = null;
		try {
			List<PmAddressType> SelectdeliveryList = this.getManagedBean()
					.getDeliveryAddressSelect();
			if (null == SelectdeliveryList || SelectdeliveryList.isEmpty()) {
				throw new EpException(MessageConst.ERROR_760145);

			}
			List<PmAddressType> deliveryList = new ArrayList<PmAddressType>();
			addressSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IAddressSO.class);
			AddressReqDTO reqDTO = new AddressReqDTO();
			List<Long> addresstypeId = new ArrayList<Long>();
			if (null != SelectdeliveryList && !SelectdeliveryList.isEmpty()) {
				for (PmAddressType addressType : SelectdeliveryList) {
					addresstypeId.add(addressType.getAddressTypeId());
				}
				reqDTO.setAddressTypeIds(addresstypeId);
				AddressResDTO resDTO = addressSo
						.retrieveAddressTypeDtlListById(reqDTO);
				deliveryList.addAll(resDTO.getAddressTypeList());
			}

			// List<ScQtDeliveryLoc> nonZone=new ArrayList<ScQtDeliveryLoc>();

			List<BriefingZoneBean> zoneBean = this.getManagedBean()
					.getDeliveryLocBean();
			if (this.getManagedBean().getQt().isZonal() == true
					&& this.getManagedBean().getQt().isPanel() == false) {
				for (BriefingZoneBean deliverybean : zoneBean) {

					if (deliverybean
							.getTplSpecZoneDtl()
							.getZoneName()
							.equals(this.getManagedBean().getTplSpecZoneDtl()
									.getZoneName())) {
						List<ScQtDeliveryLoc> delLoc = deliverybean
								.getDeliveryLocList();
						if (delLoc == null) {
							delLoc = new ArrayList<ScQtDeliveryLoc>();
						}
						for (PmAddressType addressType : deliveryList) {
							ScQtDeliveryLoc loc = new ScQtDeliveryLoc();
							// loc.setPmAddress(addressType.getPmAddress());
							String address1 = addressType.getPmAddress()
									.getAddress1();
							String address2 = addressType.getPmAddress()
									.getAddress2();
							String address3 = addressType.getPmAddress()
									.getAddress3();
							String address4 = addressType.getPmAddress()
									.getAddress4();
							String address = address1 + "," + address2 + ","
									+ address3 + "," + address4;
							loc.setDeliveryAddress(address);
							// loc.setPmOrgProfile(addressType.getPmOrgProfile());
							loc.setPmAddressType(addressType);
							loc.setTplSpecZoneDtl(this.getManagedBean()
									.getTplSpecZoneDtl());
							loc.setScQt(this.getManagedBean().getQt());

							setUserDetails(loc);
							delLoc.add(loc);
							deliverybean.setDeliveryLocList(delLoc);
							List<BriefingZoneBean> addressbean = this
									.getManagedBean().getAddressZoneBean();
							if (addressbean == null) {
								addressbean = new ArrayList<BriefingZoneBean>();
							}
							addressbean.add(deliverybean);
							this.getManagedBean().setAddressZoneBean(
									addressbean);

						}
						break;
					}

				}
			}

			else if (this.getManagedBean().getQt().isZonal() == true
					&& this.getManagedBean().getQt().isPanel() == true) {
				for (BriefingZoneBean deliverybean : zoneBean) {

					if (deliverybean
							.getTplSpecZoneDtl()
							.getPanelZone()
							.getName()
							.equals(this.getManagedBean().getTplSpecZoneDtl()
									.getPanelZone().getName())) {
						List<ScQtDeliveryLoc> delLoc = deliverybean
								.getDeliveryLocList();
						if (delLoc == null) {
							delLoc = new ArrayList<ScQtDeliveryLoc>();
						}
						for (PmAddressType addressType : deliveryList) {
							ScQtDeliveryLoc loc = new ScQtDeliveryLoc();
							// loc.setPmAddress(addressType.getPmAddress());
							String address1 = addressType.getPmAddress()
									.getAddress1();
							String address2 = addressType.getPmAddress()
									.getAddress2();
							String address3 = addressType.getPmAddress()
									.getAddress3();
							String address4 = addressType.getPmAddress()
									.getAddress4();
							String address = address1 + "," + address2 + ","
									+ address3 + "," + address4;
							loc.setDeliveryAddress(address);
							// loc.setPmOrgProfile(addressType.getPmOrgProfile());
							loc.setPmAddressType(addressType);
							loc.setTplSpecZoneDtl(this.getManagedBean()
									.getTplSpecZoneDtl());
							loc.setScQt(this.getManagedBean().getQt());

							setUserDetails(loc);
							delLoc.add(loc);
							deliverybean.setDeliveryLocList(delLoc);
							List<BriefingZoneBean> addressbean = this
									.getManagedBean().getAddressZoneBean();
							if (addressbean == null) {
								addressbean = new ArrayList<BriefingZoneBean>();
							}
							addressbean.add(deliverybean);
							this.getManagedBean().setAddressZoneBean(
									addressbean);

						}
						break;
					}

				}
			} else if ((this.getManagedBean().getQt().isZonal() == false && this
					.getManagedBean().getQt().isPanel() == true)
					|| (this.getManagedBean().getQt().isZonal() == false && this
							.getManagedBean().getQt().isPanel() == false)) {
				List<ScQtDeliveryLoc> nonZone = this.getManagedBean()
						.getDeliveryNonZoneList();
				if (nonZone == null) {
					nonZone = new ArrayList<ScQtDeliveryLoc>();
				}
				for (PmAddressType addressType : deliveryList) {

					ScQtDeliveryLoc loc = new ScQtDeliveryLoc();
					loc.setPmAddressType(addressType);
					String address1 = addressType.getPmAddress().getAddress1();
					String address2 = addressType.getPmAddress().getAddress2();
					String address3 = addressType.getPmAddress().getAddress3();
					String address4 = addressType.getPmAddress().getAddress4();
					String address = address1 + "," + address2 + "," + address3
							+ "," + address4;
					loc.setDeliveryAddress(address);
					loc.setPmAddressType(addressType);
					// loc.setPmOrgProfile(addressType.getPmOrgProfile());
					loc.setTplSpecZoneDtl(null);
					loc.setScQt(this.getManagedBean().getQt());

					setUserDetails(loc);
					nonZone.add(loc);
					this.getManagedBean().setDeliveryNonZoneList(nonZone);
				}
			}

		} finally {

		}

		RequestContext.getCurrentInstance().execute("deliveryAddress.hide();");
	}

	/**
	 * Delete delivery zone.delete delivery addresses based on zonal.
	 * 
	 * @param deliveryZoneName
	 *            the delivery zone name
	 */
	public void deleteDeliveryZone() {
		log.info("deleteDeliveryZone - START");
		if (getManagedBean().getQt().isZonal()) {
			String deliveryZoneName = "";
			if (null != this.getManagedBean().getTplSpecZoneDtl()) {
				deliveryZoneName = this.getManagedBean().getTplSpecZoneDtl()
						.getZoneName();
			}
			List<BriefingZoneBean> deliveryBean = this.getManagedBean()
					.getDeliveryLocBean();
			List<ScQtDeliveryLoc> locationRemoveList = this.getManagedBean()
					.getDeliveryLocRemoveList();
			if (null == locationRemoveList) {
				locationRemoveList = new ArrayList<ScQtDeliveryLoc>();
			}
			if (!this.getManagedBean().getQt().isPanel()) {
				List<ScQtDeliveryLoc> selectRemoveList = new ArrayList<ScQtDeliveryLoc>();
				for (BriefingZoneBean zonebean : deliveryBean) {
					if (zonebean.getTplSpecZoneDtl().getZoneName()
							.equalsIgnoreCase(deliveryZoneName)) {
						List<ScQtDeliveryLoc> deliveryList = zonebean
								.getDeliveryLocList();
						for (ScQtDeliveryLoc entity : deliveryList) {
							if (entity.isSelected()) {
								entity.setDeleted(true);
								selectRemoveList.add(entity);
							}
						}

						if (null == selectRemoveList
								|| selectRemoveList.isEmpty()) {
							throw new EpException(MessageConst.ERROR_760146);
						}
						locationRemoveList.addAll(selectRemoveList);
						for (ScQtDeliveryLoc delLoc : selectRemoveList) {
							delLoc.setDeleted(true);
							deliveryList.remove(delLoc);
						}

					}
					this.getManagedBean().setDeliveryLocRemoveList(
							locationRemoveList);
				}
			} else {
				List<ScQtDeliveryLoc> selectRemoveList = new ArrayList<ScQtDeliveryLoc>();
				for (BriefingZoneBean zonebean : deliveryBean) {
					if (zonebean.getTplSpecZoneDtl().getPanelZone().getName()
							.equalsIgnoreCase(deliveryZoneName)) {
						List<ScQtDeliveryLoc> deliveryList = zonebean
								.getDeliveryLocList();
						for (ScQtDeliveryLoc entity : deliveryList) {
							if (entity.isSelected()) {
								entity.setDeleted(true);
								selectRemoveList.add(entity);
							}
						}

						if (null == selectRemoveList
								|| selectRemoveList.isEmpty()) {
							throw new EpException(MessageConst.ERROR_760146);
						}
						locationRemoveList.addAll(selectRemoveList);
						for (ScQtDeliveryLoc delLoc : selectRemoveList) {
							delLoc.setDeleted(true);
							deliveryList.remove(delLoc);
						}
					}
					this.getManagedBean().setDeliveryLocRemoveList(
							locationRemoveList);
				}
			}
		} else {
			List<ScQtDeliveryLoc> selectRemoveList = new ArrayList<ScQtDeliveryLoc>();
			List<ScQtDeliveryLoc> nonzone = this.getManagedBean()
					.getDeliveryNonZoneList();
			if (null == nonzone) {
				nonzone = new ArrayList<ScQtDeliveryLoc>();
			}
			List<ScQtDeliveryLoc> locationRemoveList = this.getManagedBean()
					.getDeliveryLocRemoveList();
			if (null == locationRemoveList) {
				locationRemoveList = new ArrayList<ScQtDeliveryLoc>();
			}
			for (ScQtDeliveryLoc entity : nonzone) {
				if (entity.isSelected()) {
					entity.setDeleted(true);
					selectRemoveList.add(entity);
				}
			}

			if (null == selectRemoveList || selectRemoveList.isEmpty()) {
				throw new EpException(MessageConst.ERROR_760146);
			}
			locationRemoveList.addAll(selectRemoveList);
			for (ScQtDeliveryLoc delLoc : selectRemoveList) {
				delLoc.setDeleted(true);
				nonzone.remove(delLoc);
			}
			this.getManagedBean().setDeliveryLocRemoveList(locationRemoveList);
		}

		log.info("deleteDeliveryZone - END");
	}

	/**
	 * Delete delivery dtls.delete delivery addresses non zonal.
	 */
	public void deleteDeliveryDtls() {
		try {
			List<ScQtDeliveryLoc> nonzone = this.getManagedBean()
					.getDeliveryNonZoneList();
			if (null == nonzone) {
				nonzone = new ArrayList<ScQtDeliveryLoc>();
			}
			List<ScQtDeliveryLoc> locationRemoveList = this.getManagedBean()
					.getDeliveryLocRemoveList();
			if (null == locationRemoveList) {
				locationRemoveList = new ArrayList<ScQtDeliveryLoc>();
			}
			List<ScQtDeliveryLoc> selectdeliveryList = this.getManagedBean()
					.getSelectDeliveryList();
			if (null == selectdeliveryList || selectdeliveryList.isEmpty()) {
				throw new EpException(MessageConst.ERROR_760146);
			}
			locationRemoveList.addAll(selectdeliveryList);

			if (nonzone.size() == selectdeliveryList.size()) {

				nonzone.clear();

			} else {

				for (ScQtDeliveryLoc delLoc : selectdeliveryList) {
					delLoc.setDeleted(true);
					nonzone.remove(delLoc);
				}
			}
			this.getManagedBean().setDeliveryLocRemoveList(locationRemoveList);
		} finally {

		}
	}

	/**
	 * Retrive financial standard check list.retrieve Financial Standard
	 * checklist from pmdocument.
	 */
	public void retriveFinancialStandardCheckList() {

		IDocumentSO documentSo;
		DocumentResDTO resDTO;
		try {

			documentSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IDocumentSO.class);
			DocumentReqDTO reqDTO = new DocumentReqDTO();
			procurementCategoryCode(this.getManagedBean().getQt());
			reqDTO.setProcurementCatCode(this.getManagedBean()
					.getProcuremntCategory());
			reqDTO.setIsMandatory(false);
			reqDTO.setDocCategoryCode(ParameterConst.DOCUMENT_CATEGORY_FINANCIAL_CHECKLIST);

			resDTO = documentSo.retrieveDocumentCheckList(reqDTO);
			this.getManagedBean().setStageTwoFinComplianceSelect(
					new ArrayList<PmDocument>());
			this.getManagedBean().setAutoUpdate(false);
			this.getManagedBean().setStageTwoFinComplianceList(
					resDTO.getDocumentList());
		} finally {
			documentSo = null;
		}

	}

	/**
	 * Circular date comparision.circular list date comparision
	 * 
	 * @param enddate
	 *            (Date CircularExpiaryDate)
	 * @return (boolean true/false).
	 */
	public boolean circularDateComparision(Date enddate) {
		boolean isDate = false;
		Date d1 = new Date();

		if (null != enddate) {
			long days = DateUtils.getDaysDifference(enddate, d1);
			int day = (int) days;
			if (day > 0) {
				isDate = true;
			}
		}

		return isDate;
	}

	/*
	 * supplierStatusDateComparision.panel suppliers Date comparision.
	 * 
	 * @param enddate (Date suppliersExpiaryDate)
	 * 
	 * @return (boolean true/false).
	 */
	public boolean supplierStatusDateComparision(Date enddate) {
		boolean isDate = false;
		Date d1 = new Date();

		if (null != enddate) {
			long days = DateUtils.getDaysDifference(d1, enddate);
			int day = (int) days;
			if (day > 0) {
				isDate = true;
			}
		}

		return isDate;
	}

	/**
	 * Creates the check list.create checklist score.
	 * 
	 * @return ScQtCheckListScore.
	 */
	private ScQtCheckListScore createCheckList() {
		ScQtCheckList checkList = this.getManagedBean().getScQtChecklist();
		ScQtCheckListScore score = new ScQtCheckListScore();
		score.setCheckList(checkList);
		setUserDetails(score);
		return score;
	}

	/**
	 * Adds the scoring.add emptty row for scoring datatable.
	 * 
	 * @param criteriaType
	 *            (String criteriaType).
	 */
	public void addScoring(String criteriaType) {

		try {
			ScQtCheckListScore prevSpecScoring = null;
			List<ScQtCheckListScore> checkListScore = this.getManagedBean()
					.getCheckListScore();
			if (null == checkListScore) {
				checkListScore = new ArrayList<ScQtCheckListScore>();

			}

			if (checkListScore.size() > 0) {
				prevSpecScoring = checkListScore.get(checkListScore.size() - 1);
			}

			ScQtCheckListScore score = createCheckList();
			score.setCriteriaType(new Integer(criteriaType));
			score.setScoreType(new BigDecimal(this.getManagedBean()
					.getChecklistScoreType()));
			if (prevSpecScoring != null) {
				BigDecimal prevRangeTo = prevSpecScoring.getRangeTo();

				if (prevRangeTo != null) {
					if ("50".equalsIgnoreCase(criteriaType)
							|| "60".equalsIgnoreCase(criteriaType)) {
						score.setRangeFrom(prevRangeTo.add(BigDecimal
								.valueOf(0.01)));
					} else {
						score.setRangeFrom(prevRangeTo.add(BigDecimal
								.valueOf(1)));
					}
				}
			}
			checkListScore.add(score);

			this.getManagedBean().setCheckListScore(checkListScore);

		} finally {

		}
	}

	/**
	 * Adds the scoringyears.add empty row for scoring datatable.
	 * 
	 * @param CriteriaType
	 *            (String CriteriaType).
	 */
	public void addScoringyears(String CriteriaType) {

		try {
			ScQtCheckListScore prevSpecScoring = null;
			List<ScQtCheckListScore> checkListScore = this.getManagedBean()
					.getScoringForYears();
			if (null == checkListScore) {
				checkListScore = new ArrayList<ScQtCheckListScore>();

			}
			if (checkListScore.size() > 0) {
				prevSpecScoring = checkListScore.get(checkListScore.size() - 1);
			}

			ScQtCheckListScore score = createCheckList();
			score.setCriteriaType(new Integer(CriteriaType));
			score.setScoreType(new BigDecimal(this.getManagedBean()
					.getCompanyScoreType()));
			if (prevSpecScoring != null) {
				BigDecimal prevRangeTo = prevSpecScoring.getRangeTo();
				if (prevRangeTo != null) {
					if ("61".equalsIgnoreCase(CriteriaType)) {
						score.setRangeFrom(prevRangeTo.add(BigDecimal
								.valueOf(0.01)));
					} else {
						score.setRangeFrom(prevRangeTo.add(BigDecimal
								.valueOf(1)));
					}
				}
			}
			checkListScore.add(score);
			this.getManagedBean().setScoringForYears(checkListScore);

		} finally {

		}
	}

	/**
	 * Adds the schedule details.add empty rows for frequency schedule
	 * datatable.
	 */
	public void addScheduleDetails() {
		IParameterSO parameterSo = null;
		try {

			removefullfillmentDetails(ParameterConst.PERIODIC_SCHEDULE_TYPE_FREQUENCY_SCHEDULE);

			List<ScQtSchedule> scheduleList = this.getManagedBean()
					.getScheduleList();

			// int year = Calendar.getInstance().get(Calendar.YEAR);

			for (int j = 0; j < this.getManagedBean().getTotalFrequency(); j++) {

				ScQtSchedule schedule = new ScQtSchedule();
				// schedule.setStartYear(String.valueOf(year));
				// schedule.setEndYear(String.valueOf(year));

				if (StringUtils.isNotBlank(this.getManagedBean()
						.getFulfilmentType())
						&& ParameterConst.FULFILMENT_TYPE_SCHEDULED
								.equalsIgnoreCase(this.getManagedBean()
										.getFulfilmentType())) {
					parameterSo = BusinessDelegate.getInstance()
							.getServiceObject(
									ModuleCodeConst.PROFILE_MANAGEMENT,
									IParameterSO.class);
					ParameterReqDTO docDefTypeReqDTO = new ParameterReqDTO();
					docDefTypeReqDTO
							.setParameterType(ParameterTypeConst.SCHEDULE_TYPE);
					docDefTypeReqDTO
							.setParameterCode(ParameterConst.SCHEDULE_TYPE_PERIODIC);
					ParameterResDTO docDefTypeResDTO = parameterSo
							.retrieveParameterByTypeAndCode(docDefTypeReqDTO);
					schedule.setScheduleType(docDefTypeResDTO.getPmParameter());

				} else {
					PmParameter parameter = new PmParameter();
					schedule.setScheduleType(parameter);
				}
				scheduleList.add(schedule);
			}

			this.getManagedBean().setScheduleList(scheduleList);
		}

		finally {
			parameterSo = null;
		}
	}

	/**
	 * Calculate progressive start month.change start month in progessive
	 * payment.
	 * 
	 * @param progressivepayment
	 *            (ScQtProgressivePayment progressivepayment).
	 */
	public void calculateProgressiveStartMonth(
			ScQtProgressivePayment progressivepayment) {
		// event.getComponent().getAttributes().get("value");
		List<ScQtProgressivePayment> payment = this.getManagedBean()
				.getProgressivePaymentList();
		if (null == payment) {
			payment = new ArrayList<ScQtProgressivePayment>();

		}
		if (null != progressivepayment
				&& null != progressivepayment.getStartMonth()) {
			int index = payment.indexOf(progressivepayment);
			if (null == progressivepayment.getEndMonth()) {
				progressivepayment.setEndMonth(progressivepayment
						.getStartMonth());
			}
			payment.set(index, progressivepayment);

			this.getManagedBean().setProgressivePaymentList(payment);
		}

	}

	/**
	 * Calculate progressivepayment.change end month dropdown in progressive
	 * payment.
	 * 
	 * @param event
	 *            the event
	 */
	public void calculateProgressivepayment(
			ScQtProgressivePayment progressivepayment) {

		List<ScQtProgressivePayment> payment = this.getManagedBean()
				.getProgressivePaymentList();
		if (null == payment) {
			payment = new ArrayList<ScQtProgressivePayment>();

		}
		if (null != progressivepayment) {
			int index = payment.indexOf(progressivepayment);

			payment.set(index, progressivepayment);

			this.getManagedBean().setProgressivePaymentList(payment);
		}

	}

	/**
	 * Calculate schedule percentage.calculate months difference in Frequency
	 * schedule.
	 * 
	 * @param schedule
	 *            (ScQtSchedule schedule).
	 */
	public void calculateSchedulePercentage(ScQtSchedule schedule,
			Boolean startMonthAndYearFlag) throws ParseException {
		/*
		 * String endMonth = (String) event.getComponent().getAttributes()
		 * .get("value");
		 */
		// this.getManagedBean().setScQt(this.getManagedBean().getQt());
		List<ScQtSchedule> freqSchedule = this.getManagedBean()
				.getScheduleList();
		if (null == freqSchedule) {
			freqSchedule = new ArrayList<ScQtSchedule>();

		}
		Integer startMonth = 0, endMonth = 0, startYear = 0, endYear = 0, months = 0;
		startMonth = schedule.getStartMonth() != null ? Integer
				.parseInt(schedule.getStartMonth()) : null;
		endMonth = schedule.getEndMonth() != null ? Integer.parseInt(schedule
				.getEndMonth()) : null;
		startYear = schedule.getStartYear() != null ? Integer.parseInt(schedule
				.getStartYear()) : null;
		endYear = schedule.getEndYear() != null ? Integer.parseInt(schedule
				.getEndYear()) : null;

		if (null != startMonthAndYearFlag && startMonthAndYearFlag
				&& startMonth != null) {
			endMonth = startMonth;
			endYear = startYear;
			schedule.setEndMonth(endMonth.toString());
			if (endYear != null) {
				schedule.setEndYear(endYear.toString());
			}
		}

		if (startMonth != null && endMonth != null && startYear != null
				&& endYear != null) {
			Date currentDate = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat(
					CommonConstants.DEFAULT_DATE_FORMAT);
			String localDate = sdf.format(currentDate);
			String day = localDate.substring(0, 2);
			String localstartMonth=retriveMonthDtls(new Integer(startMonth));
			String localemdMonth=retriveMonthDtls(new Integer(endMonth));
			String fromDate = 01 + "/" + localstartMonth + "/" + startYear;
			String toDate = 01 + "/" + localemdMonth + "/" + endYear;
			Date startDate = sdf.parse(fromDate);
			Date endDate = sdf.parse(toDate);

			months = DateUtils.getMonths(startDate, endDate);
			schedule.setTotalMonths(new BigDecimal(months));
		} else {
			schedule.setTotalMonths(BigDecimal.ZERO);
		}

		int index = freqSchedule.indexOf(schedule);
		freqSchedule.set(index, schedule);

	}

	/**
	 * Retrive month dtls.
	 * 
	 * @param Start
	 *            the start
	 * @return the string
	 */
	private String retriveMonthDtls(int Start) {
		String month = "";
		if (Start == 0) {
			month = "01";
		} else if (Start == 1) {
			month = "02";
		} else if (Start == 2) {
			month = "03";
		} else if (Start == 3) {
			month = "04";
		} else if (Start == 4) {
			month = "05";
		} else if (Start == 5) {
			month = "06";
		} else if (Start == 6) {
			month = "07";
		} else if (Start == 7) {
			month = "08";
		} else if (Start == 8) {
			month = "09";
		} else if (Start == 9) {
			month = "10";
		} else if (Start == 10) {
			month = "11";
		} else if (Start == 11) {
			month = "12";
		} else if (Start == 12) {
			month = "01";
		} else if (Start == 13) {
			month = "02";
		}
		return month;
	}

	/**
	 * Adds the progressive details.add empty rows for progressive payment
	 * datatable.
	 */
	public void addProgressiveDetails() {

		try {
			removefullfillmentDetails(ParameterConst.PERIODIC_SCHEDULE_TYPE_PROGRESSIVE_PAYMENT);
			List<ScQtProgressivePayment> payments = this.getManagedBean()
					.getProgressivePaymentList();

			for (int j = 0; j < this.getManagedBean()
					.getTotalProgressivePayment(); j++) {

				ScQtProgressivePayment payment = new ScQtProgressivePayment();

				setUserDetails(payment);
				payments.add(payment);

			}
			this.getManagedBean()
					.getQt()
					.setScheduleType(
							ParameterConst.PERIODIC_SCHEDULE_TYPE_PROGRESSIVE_PAYMENT);

			this.getManagedBean().setProgressivePaymentList(payments);

		} finally {

		}
	}

	/**
	 * Calculate total package.calculate package Technical and Financial scores
	 * in Recommendation Menu.
	 * 
	 * @param technical
	 *            (ScQtScoreSetting package).
	 */
	public void calculateTotalPackage(ScQtScoreSetting technical) {
		try {
			if (null == technical.getOverallTechnical()) {
				technical.setOverallTechnical(new BigDecimal(0));
			}
			if (null == technical.getOverallFin()) {
				technical.setOverallFin(new BigDecimal(0));
			}
			if (technical.getOverallTechnical().intValue() < 50
					&& !this.getManagedBean().isReadOnly()) {
				throw new EpException(MessageConst.ERROR_760365);
			}
			int financialScore = 100 - technical.getOverallTechnical()
					.intValue();
			technical.setOverallFin(new BigDecimal(financialScore));
			this.getManagedBean().setRecommendationPackageScore(technical);
			int local = technical.getOverallTechnical().intValue()
					+ technical.getOverallFin().intValue();

			this.getManagedBean().setRecommendationPackageTotal(
					new BigDecimal(local));
		} finally {

		}
	}

	/**
	 * Calculate recommendtechnical score.calculate Item Technical Scores in
	 * Recommendation Menu.
	 * 
	 * @param technical
	 *            (ScQtScoreSetting technical).
	 */
	public void calculateRecommendtechnicalScore(ScQtScoreSetting technical) {
		try {
			int value1=technical.getOverallTechnical()!=null?technical.getOverallTechnical().intValue():0;
			int value2=technical.getOverallOtherTechnical()!=null?technical.getOverallOtherTechnical().intValue():0;
			int local = value1
					+ value2;
			this.getManagedBean().setRecommendTechnicalScore(
					new BigDecimal(local));
			int financialScore = 0;
			if (null != getManagedBean().getRecommendfinancialScore()) {
				financialScore = getManagedBean().getRecommendfinancialScore()
						.intValue();
			}
			this.getManagedBean().setTotalScore(
					new BigDecimal(local + financialScore));
			if ((null != this.getManagedBean().getTotalScore())
					&& this.getManagedBean().getTotalScore()
							.intValue() > 100) {
				throw new EpException(MessageConst.ERROR_760257);

			}

		} finally {

		}
	}

	/**
	 * Calculate recommend financial score.calculate Item Financial Scores in
	 * Recommendation Menu.
	 * 
	 * @param financial
	 *            (ScQtScoreSetting financial).
	 */
	public void calculateRecommendFinancialScore(ScQtScoreSetting financial) {
		try {
			int value1=financial.getOverallFin()!=null?financial.getOverallFin().intValue():0;
			int value2=financial.getOverallOtherFin()!=null?financial.getOverallOtherFin().intValue():0;
			int local = value1
					+ value2;
			this.getManagedBean().setRecommendfinancialScore(
					new BigDecimal(local));

			int technicalScore = 0;
			if (null != getManagedBean().getRecommendTechnicalScore()) {
				technicalScore = getManagedBean().getRecommendTechnicalScore()
						.intValue();
			}
			this.getManagedBean().setTotalScore(
					new BigDecimal(local + technicalScore));
			if ((null != this.getManagedBean().getTotalScore())
					&& this.getManagedBean().getTotalScore()
							.intValue() > 100) {
				throw new EpException(MessageConst.ERROR_760257);

			}

		} finally {

		}
	}

	/**
	 * Calculate Financial Item score.calculate Item Financial Scores in
	 * FinancialScoring Menu.
	 * 
	 * @param financial
	 *            (ScQtScoreSetting financialItem).
	 */
	public void calculateFinancialItemScore(ScQtScoreSetting financialItem) {
		try {
			if (null != financialItem) {
				int value1=	financialItem.getOverallFin()!=null? financialItem.getOverallFin().intValue():0;
				int value2=	financialItem.getOverallOtherFin()!=null?financialItem.getOverallOtherFin().intValue():0;
				int local = value1
						+ value2;
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

	/**
	 * Calculate Financial Item score.calculate Item Financial Scores in
	 * FinancialScoring Menu.
	 * 
	 * @param financial
	 *            (ScQtScoreSetting financialItem).
	 */
	private void financialItemScoreCalculate(ScQtScoreSetting financialItem) {
		try {
			if (null != financialItem) {
				if (null == financialItem.getOverallFin()) {
					throw new EpException(MessageConst.ERROR_760256);
				}
				if (financialItem.getOverallFin().intValue() <= 0) {
					throw new EpException(MessageConst.ERROR_760269);
				}
				if (null == financialItem.getOverallOtherFin()) {
					throw new EpException(MessageConst.ERROR_760256);
				}
			int value1=	financialItem.getOverallFin()!=null? financialItem.getOverallFin().intValue():0;
			int value2=	financialItem.getOverallOtherFin()!=null?financialItem.getOverallOtherFin().intValue():0;
				int local = value1
						+ value2;
				this.getManagedBean().setFinancialItemScore(
						new BigDecimal(local));
				if ((null != this.getManagedBean().getFinancialItemScore())
						&& (this.getManagedBean().getFinancialItemScore()
								.intValue() < 100 || this.getManagedBean()
								.getFinancialItemScore().intValue() > 100)) {
					throw new EpException(MessageConst.ERROR_760257);

				}
			}

		} finally {

		}
	}

	/**
	 * Compare checklist value current year.check checklist year greater than
	 * current year.
	 * 
	 * @param checkListValue
	 *            (ScQtCheckListValue checkListValue)
	 */
	public void compareChecklistValueCurrentYear(
			ScQtCheckListValue checkListValue) {
		if (null != checkListValue) {
			int year = Calendar.getInstance().get(Calendar.YEAR);
			if (null != checkListValue.getYear()) {
				if (checkListValue.getYear().intValue() > year) {
					throw new EpException(MessageConst.ERROR_760169);
				}
			}
		}

	}

	/**
	 * Save check list scores.save checklist scores.
	 */
	public void saveCheckListScores() {
		try {

			IQTSpecSO specSo;
			SpecificationReqDTO reqDTO = new SpecificationReqDTO();
			reqDTO.setTobeRemoveScores(null);
			specSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);

			ScQtCheckList checkList = this.getManagedBean().getScQtChecklist();
			String contractScoreType = this.getManagedBean()
					.getChecklistScoreType();
			// checkList.setScQtCheckListScores(null);
			String totalNoOfYearsType = this.getManagedBean()
					.getCompanyScoreType();
			List<ScQtCheckListScore> noOfContractScore = new ArrayList<ScQtCheckListScore>();
			List<ScQtCheckListScore> noOfYearsScore = new ArrayList<ScQtCheckListScore>();
			List<ScQtCheckList> checklistRemoveList = getToBeRemoveList();
			if (null != checklistRemoveList && !checklistRemoveList.isEmpty()) {
				for (int i = 0; i < checklistRemoveList.size(); i++) {
					if (null == checklistRemoveList.get(i).getQtChecklistId()) {
						checklistRemoveList.remove(i);
					} else {
						checklistRemoveList.get(i).setDeleted(true);
					}
				}
				log.info("backing bean saveCheckListScores toBeremoveCheckLists  size=="
						+ checklistRemoveList.size());
			}
			if (null != checkList && null != checkList.getScQtCheckListValue()) {
				int year = Calendar.getInstance().get(Calendar.YEAR);
				if (null != checkList.getScQtCheckListValue().getYear()) {
					String checkListValueYear = checkList
							.getScQtCheckListValue().getYear().toString();
					if (checkListValueYear.length() < 4) {
						throw new EpException(MessageConst.ERROR_760246);
					}
					if (checkList.getScQtCheckListValue().getYear().intValue() > year) {
						throw new EpException(MessageConst.ERROR_760169);
					}
				}
			}
			if (checkList.isScoringReq()) {
				if (null == contractScoreType
						|| "".equalsIgnoreCase(contractScoreType)) {
					throw new EpException(MessageConst.ERROR_760190);
				}

				if (QTConstants.USER_SCORE.equalsIgnoreCase(contractScoreType)) {
					ScQtCheckListScore noContract = this.getManagedBean()
							.getScQtCheckListScore();
					noContract.setScoreType(new BigDecimal(this
							.getManagedBean().getChecklistScoreType()));
					//noContract.setCriteriaType(new Integer(this
							//.getManagedBean().getCriteriaType()));
					noContract.setCheckList(checkList);
					setUserDetails(noContract);
					noOfContractScore.add(noContract);

				} else if (QTConstants.AUTO_SCORE
						.equalsIgnoreCase(contractScoreType)) {
					List<ScQtCheckListScore> checkListScore = this
							.getManagedBean().getCheckListScore();

					if (null == checkListScore) {
						checkListScore = new ArrayList<ScQtCheckListScore>();
					}
					if (checkListScore.size() < 2) {
						throw new EpException(MessageConst.ERROR_760140);
					}
					List<ScQtCheckListScore> localScore = new ArrayList<ScQtCheckListScore>();
					for (int i = 0; i < checkListScore.size(); i++) {
						ScQtCheckListScore Score = checkListScore.get(i);
						if (null == Score.getScore()
								|| Score.getScore().intValue() <= 0) {
							throw new EpException(MessageConst.ERROR_760139);

						}
						if (null != checkList.getPmDocument()
								&& QTConstants.FINANCIAL_CREDIT_FACILITY_CODE
										.equalsIgnoreCase(checkList
												.getPmDocument()
												.getDocDefinitionKey())) {
							if (null != Score.getRangeTo()
									&& Score.getRangeTo().intValue() > 100) {
								throw new EpException(MessageConst.ERROR_760170);
							}
						}

					}
					isOverlappingScore(checkListScore);
					for (int i = 0; i < checkListScore.size() - 1; i++) {
						ScQtCheckListScore Score = checkListScore.get(i);
						localScore.add(Score);
					}

					// Collections.sort(localScore);
					if (null != checkListScore) {
						noOfContractScore.addAll(checkListScore);
					}
				}

				if ((null != checkList && null != checkList.getPmDocument())
						&& (QTConstants.TECHNICAL_GOV_CODE
								.equalsIgnoreCase(checkList.getPmDocument()
										.getDocDefinitionKey())
								|| QTConstants.TECHNICAL_PRIVATE_CODE
										.equalsIgnoreCase(checkList
												.getPmDocument()
												.getDocDefinitionKey()) || QTConstants.FINANCIAL_ASSET_LIABILITY_CODE
									.equalsIgnoreCase(checkList.getPmDocument()
											.getDocDefinitionKey()))) {

					if (QTConstants.USER_SCORE
							.equalsIgnoreCase(totalNoOfYearsType)) {
						ScQtCheckListScore scoreOfYears = this.getManagedBean()
								.getScQtContractScore();
						scoreOfYears.setScoreType(new BigDecimal(this
								.getManagedBean().getCompanyScoreType()));
						//scoreOfYears.setCriteriaType(new Integer(this
								//.getManagedBean().getOtherCriteriaType()));
						scoreOfYears.setCheckList(checkList);
						setUserDetails(scoreOfYears);
						noOfYearsScore.add(scoreOfYears);
					} else if (QTConstants.AUTO_SCORE
							.equalsIgnoreCase(totalNoOfYearsType)) {
						List<ScQtCheckListScore> scoreYears = this
								.getManagedBean().getScoringForYears();

						if (null == scoreYears) {
							scoreYears = new ArrayList<ScQtCheckListScore>();
						}
						if (scoreYears.size() < 2) {
							throw new EpException(MessageConst.ERROR_760140);
						}

						for (int i = 0; i < scoreYears.size(); i++) {
							ScQtCheckListScore Score = scoreYears.get(i);
							if (null == Score.getScore()
									|| Score.getScore().intValue() <= 0) {
								throw new EpException(MessageConst.ERROR_760139);

							}
							/*
							 * if (null != Score.getRangeTo() &&
							 * Score.getRangeTo().intValue() > 100) { throw new
							 * EpException(MessageConst.ERROR_760170); }
							 */
						}
						isOverlappingScore(scoreYears);
						List<ScQtCheckListScore> localScore = new ArrayList<ScQtCheckListScore>();
						for (int i = 0; i < scoreYears.size() - 1; i++) {
							ScQtCheckListScore Score = scoreYears.get(i);
							localScore.add(Score);
						}
						// Collections.sort(localScore);
						if (null != scoreYears) {
							noOfYearsScore.addAll(scoreYears);
						}
					}
				}
			}
			reqDTO.setCheckListScores(noOfContractScore);
			List<ScQtCheckListScore> removedList = this.getManagedBean()
					.getToBeRemoveList();
			if (null != removedList) {
				for (int i = 0; i < removedList.size(); i++) {
					ScQtCheckListScore checkListScore = removedList.get(i);
					if (null != checkListScore) {
						if (null == checkListScore.getScQtCheckListScoreId()) {
							removedList.remove(i);
						}
					}
				}
			}
			if ((null != checkList && null != checkList.getPmDocument())
					&& (QTConstants.TECHNICAL_GOV_CODE
							.equalsIgnoreCase(checkList.getPmDocument()
									.getDocDefinitionKey())
							|| QTConstants.TECHNICAL_PRIVATE_CODE
									.equalsIgnoreCase(checkList.getPmDocument()
											.getDocDefinitionKey()) || QTConstants.FINANCIAL_ASSET_LIABILITY_CODE
								.equalsIgnoreCase(checkList.getPmDocument()
										.getDocDefinitionKey()))) {
				if (((QTConstants.AUTO_SCORE
						.equalsIgnoreCase(totalNoOfYearsType) && QTConstants.AUTO_SCORE
						.equalsIgnoreCase(contractScoreType)) || (QTConstants.AUTO_SCORE
						.equalsIgnoreCase(contractScoreType) && (null == totalNoOfYearsType || ""
						.equals(totalNoOfYearsType))))) {
					reqDTO.setEvaluationStatus(getPmStatusByStatusId(QTStatusConst.TS_COMPLETED));
					// checkList.setPmEvalStatus(evalStatus);

				} else {
					reqDTO.setEvaluationStatus(getPmStatusByStatusId(QTStatusConst.TS_PENDING_SUBMISSION));
					// checkList.setPmEvalStatus(evalStatus);
				}

			} else {
				if (QTConstants.AUTO_SCORE.equalsIgnoreCase(contractScoreType)) {
					reqDTO.setEvaluationStatus(getPmStatusByStatusId(QTStatusConst.TS_COMPLETED));
					// checkList.setPmEvalStatus(evalStatus);
				} else {
					reqDTO.setEvaluationStatus(getPmStatusByStatusId(QTStatusConst.TS_PENDING_SUBMISSION));
					// checkList.setPmEvalStatus(evalStatus);
				}

			}
			reqDTO.setSpecificationStatus(getPmStatusByStatusId(QTStatusConst.TS_COMPLETED));
			reqDTO.setTobeRemoveScores(removedList);
			reqDTO.setScQtCheckList(checkList);
			reqDTO.setOtherCheckListScores(noOfYearsScore);
			reqDTO.setCheckListValue(checkList.getScQtCheckListValue());
			reqDTO.setScoringReq(checkList.isScoringReq());
			reqDTO.setTobeRemoveList(checklistRemoveList);
			SpecificationResDTO resDTO = specSo.saveCheckListScore(reqDTO);
			// resDTO.getScQtCheckList();
			ScQtCheckList updatedCheckList = resDTO.getScQtCheckList();

			if (SourcingConstants.TECHINICAL_STAGE.equalsIgnoreCase(this
					.getManagedBean().getSpecType())) {
				this.getManagedBean().getScQtCheckSt2techProposal()
						.remove(this.getManagedBean().getIndex());
				this.getManagedBean()
						.getScQtCheckSt2techProposal()
						.add(this.getManagedBean().getIndex(), updatedCheckList);
				technicalChecklistEditAction(this.getManagedBean().getIndex(),
						updatedCheckList, ParameterConst.SPECIFICATION_TYPE_TECHNICAL);
			} else {
				this.getManagedBean().getScQtCheckSt2financeProposalList()
						.remove(this.getManagedBean().getIndex());
				this.getManagedBean()
						.getScQtCheckSt2financeProposalList()
						.add(this.getManagedBean().getIndex(), updatedCheckList);
				financialChecklistEditAction(this.getManagedBean().getIndex(),
						updatedCheckList, ParameterConst.SPECIFICATION_TYPE_FINANCIAL);
			}
			reqDTO.setTobeRemoveScores(null);
			reqDTO.setTobeRemoveList(null);
			getManagedBean().setMarkDeletedTechChecklist(null);
			getManagedBean().setMarkDeletedFinChecklist(null);
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, null,
							MessageUtils.getMessage(MessageConst.INFO_700004,
									new String[] {
											getMessageCommon("lbl.sc.scores"),
											"" },
									MessageUtils.BUNDLE_COMMON_MESSAGES,
									new FacesScopes().getUserLocale())));

		} finally {

		}

	}

	/**
	 * Save bank check list scores.save bank statemnt checklist scores.
	 */
	public void saveBankCheckListScores() {
		try {
			IQTSpecSO specSo;
			SpecificationReqDTO reqDTO = new SpecificationReqDTO();
			reqDTO.setTobeRemoveScores(null);
			specSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);
			ScQtCheckList checkList = this.getManagedBean().getScQtChecklist();
			String contractScoreType = this.getManagedBean()
					.getChecklistScoreType();
			List<ScQtCheckList> checklistRemoveList = getToBeRemoveList();
			if (null != checklistRemoveList && !checklistRemoveList.isEmpty()) {
				for (int i = 0; i < checklistRemoveList.size(); i++) {
					if (null == checklistRemoveList.get(i).getQtChecklistId()) {
						checklistRemoveList.remove(i);
					} else {
						checklistRemoveList.get(i).setDeleted(true);
					}
				}
				log.info("backing bean saveBankCheckListScores toBeremoveCheckLists  size=="
						+ checklistRemoveList.size());
			}

			List<ScQtCheckListScore> noOfContractScore = new ArrayList<ScQtCheckListScore>();
			if (checkList.isScoringReq()) {
				if (QTConstants.USER_SCORE.equalsIgnoreCase(contractScoreType)) {
					ScQtCheckListScore noContract = this.getManagedBean()
							.getScQtCheckListScore();
					noContract.setScoreType(new BigDecimal(this
							.getManagedBean().getChecklistScoreType()));
					//noContract.setCriteriaType(new Integer(this
							//.getManagedBean().getCriteriaType()));
					noContract.setCheckList(checkList);
					setUserDetails(noContract);
					noOfContractScore.add(noContract);
				} else if (QTConstants.AUTO_SCORE
						.equalsIgnoreCase(contractScoreType)) {
					List<ScQtCheckListScore> checkListScore = this
							.getManagedBean().getCheckListScore();

					if (null == checkListScore) {
						checkListScore = new ArrayList<ScQtCheckListScore>();
					}
					if (checkListScore.size() < 2) {
						throw new EpException(MessageConst.ERROR_760140);
					}

					for (int i = 0; i < checkListScore.size(); i++) {
						ScQtCheckListScore Score = checkListScore.get(i);
						if (null == Score.getScore()
								|| Score.getScore().intValue() <= 0) {
							throw new EpException(MessageConst.ERROR_760139);

						}
						/*
						 * if (null != Score.getRangeTo() &&
						 * Score.getRangeTo().intValue() > 100) { throw new
						 * EpException(MessageConst.ERROR_760170); }
						 */

					}
					isOverlappingScore(checkListScore);
					List<ScQtCheckListScore> localScore = new ArrayList<ScQtCheckListScore>();
					for (int i = 0; i < checkListScore.size() - 1; i++) {
						ScQtCheckListScore Score = checkListScore.get(i);
						localScore.add(Score);
					}

					if (null != checkListScore) {
						noOfContractScore.addAll(checkListScore);
					}
				}
			}
			String Startmonth = "";
			String endmonth = "";
			Startmonth = retriveMonthDtls(new Integer(this.getManagedBean()
					.getFromMonth()));

			endmonth = retriveMonthDtls(new Integer(this.getManagedBean()
					.getToMonth()));

			Date d1 = new Date();
			SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
			DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
			String localDate = format.format(d1);
			String date = localDate.substring(0, 2);
			String fromdate = date + "/" + Startmonth + "/"
					+ this.getManagedBean().getStartYear();
			String toDate = date + "/" + endmonth + "/"
					+ this.getManagedBean().getEndYear();
			try {
				checkList.getScQtCheckListValue().setFromDate(
						df.parse(fromdate));
				// checkList.setScQt(this.getManagedBean().getQt());
				checkList.getScQtCheckListValue().setToDate(df.parse(toDate));
			} catch (ParseException e) {
				log.debug(e.getMessage());
			}

			// checkList.setScQtCheckListScores(noOfContractScore);
			reqDTO.setCheckListScores(noOfContractScore);

			List<ScQtCheckListScore> removedList = this.getManagedBean()
					.getToBeRemoveList();
			if (null != removedList) {
				for (int i = 0; i < removedList.size(); i++) {
					ScQtCheckListScore checkListScore = removedList.get(i);
					if (null != checkListScore) {
						if (null == checkListScore.getScQtCheckListScoreId()) {
							removedList.remove(i);
						}
					}
				}
			}
			if (null != checkList.getScQtCheckListValue()) {
				if (null == checkList.getScQtCheckListValue().getFromDate()
						|| null == checkList.getScQtCheckListValue()
								.getToDate()) {
					throw new EpException(MessageConst.ERROR_760222);
					// checkList.getScQtCheckListValue().setFromDate(new
					// Date());
					// checkList.getScQtCheckListValue().setToDate(
					// DateUtils.addDays(new Date(), 90));
				}
			}
			if (QTConstants.AUTO_SCORE.equalsIgnoreCase(contractScoreType)) {
				reqDTO.setEvaluationStatus(getPmStatusByStatusId(QTStatusConst.TS_COMPLETED));
				// checkList.setPmEvalStatus(evalStatus);
			} else {
				reqDTO.setEvaluationStatus(getPmStatusByStatusId(QTStatusConst.TS_PENDING_SUBMISSION));
				// checkList.setPmEvalStatus(evalStatus);
			}

			List<ScQtCheckListScore> noOfYearsScore = new ArrayList<ScQtCheckListScore>();
			reqDTO.setSpecificationStatus(getPmStatusByStatusId(QTStatusConst.TS_COMPLETED));
			reqDTO.setTobeRemoveScores(removedList);
			reqDTO.setScQtCheckList(checkList);
			reqDTO.setOtherCheckListScores(noOfYearsScore);
			reqDTO.setCheckListValue(checkList.getScQtCheckListValue());
			reqDTO.setScoringReq(checkList.isScoringReq());
			reqDTO.setTobeRemoveList(checklistRemoveList);
			SpecificationResDTO resDTO = specSo.saveCheckListScore(reqDTO);
			ScQtCheckList updatedCheckList = resDTO.getScQtCheckList();

			this.getManagedBean().getScQtCheckSt2financeProposalList()
					.remove(this.getManagedBean().getIndex());
			this.getManagedBean().getScQtCheckSt2financeProposalList()
					.add(this.getManagedBean().getIndex(), updatedCheckList);
			financialChecklistEditAction(this.getManagedBean().getIndex(),
					updatedCheckList, ParameterConst.SPECIFICATION_TYPE_FINANCIAL);
			reqDTO.setTobeRemoveScores(null);
			reqDTO.setTobeRemoveList(null);
			getManagedBean().setMarkDeletedTechChecklist(null);
			getManagedBean().setMarkDeletedFinChecklist(null);
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, null,
							MessageUtils.getMessage(MessageConst.INFO_700004,
									new String[] {
											getMessageCommon("lbl.sc.scores"),
											"" },
									MessageUtils.BUNDLE_COMMON_MESSAGES,
									new FacesScopes().getUserLocale())));
		} finally {

		}

	}

	/**
	 * Gets the month drop down values.display months in dropdown.
	 * 
	 * @return List<SelectItem>
	 */
	public List<SelectItem> getMonthDropDownValues() {
		List<SelectItem> selectItems = new ArrayList<SelectItem>();

		try {

			selectItems.add(new SelectItem(null, ResourceBundleUtils
					.getMessage("lbl.txt.select",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							new FacesScopes().getUserLocale())));
			selectItems.add(new SelectItem(0, ResourceBundleUtils
					.getMessage("lbl.sc.checklist.month.january",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							new FacesScopes().getUserLocale())));
			selectItems.add(new SelectItem(1,  ResourceBundleUtils
					.getMessage("lbl.sc.checklist.month.february",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							new FacesScopes().getUserLocale())));
			selectItems.add(new SelectItem(2,  ResourceBundleUtils
					.getMessage("lbl.sc.checklist.month.march",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							new FacesScopes().getUserLocale())));
			selectItems.add(new SelectItem(3,  ResourceBundleUtils
					.getMessage("lbl.sc.checklist.month.april",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							new FacesScopes().getUserLocale())));
			selectItems.add(new SelectItem(4,  ResourceBundleUtils
					.getMessage("lbl.sc.checklist.month.may",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							new FacesScopes().getUserLocale())));
			selectItems.add(new SelectItem(5,  ResourceBundleUtils
					.getMessage("lbl.sc.checklist.month.june",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							new FacesScopes().getUserLocale())));
			selectItems.add(new SelectItem(6, ResourceBundleUtils
					.getMessage("lbl.sc.checklist.month.july",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							new FacesScopes().getUserLocale())));
			selectItems.add(new SelectItem(7, ResourceBundleUtils
					.getMessage("lbl.sc.checklist.month.august",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							new FacesScopes().getUserLocale())));
			selectItems.add(new SelectItem(8, ResourceBundleUtils
					.getMessage("lbl.sc.checklist.month.september",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							new FacesScopes().getUserLocale())));
			selectItems.add(new SelectItem(9, ResourceBundleUtils
					.getMessage("lbl.sc.checklist.month.october",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							new FacesScopes().getUserLocale())));
			selectItems.add(new SelectItem(10, ResourceBundleUtils
					.getMessage("lbl.sc.checklist.month.november",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							new FacesScopes().getUserLocale())));
			selectItems.add(new SelectItem(11, ResourceBundleUtils
					.getMessage("lbl.sc.checklist.month.december",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							new FacesScopes().getUserLocale())));

			this.getManagedBean().setYearList(selectItems);
		} finally {

		}
		return getManagedBean().getYearList();
	}

	/**
	 * Gets the years display dtls.display years in dropdown.
	 * 
	 * @return List<SelectItem>.
	 */
	public List<SelectItem> getYearsDisplayDtls() {

		List<SelectItem> selectItems = new ArrayList<SelectItem>();

		try {
			int year = Calendar.getInstance().get(Calendar.YEAR);

			selectItems.add(new SelectItem(null, ResourceBundleUtils
					.getMessage("lbl.txt.select",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							new FacesScopes().getUserLocale())));
			selectItems.add(new SelectItem(year, String.valueOf(year)));
			for (int i = 0; i < 20; i++) {
				year = year + 1;
				selectItems.add(new SelectItem(year, String.valueOf(year)));
			}

			this.getManagedBean().setYearsDisplayList(selectItems);
		} finally {

		}
		return getManagedBean().getYearsDisplayList();
	}

	/**
	 * Gets the score details.display scoresTypes in dropdown.
	 * 
	 * @return List<SelectItem>
	 */
	public List<SelectItem> getScoreDetails() {
		List<SelectItem> selectItems = new ArrayList<SelectItem>();

		try {

			selectItems.add(new SelectItem(null, ResourceBundleUtils
					.getMessage("lbl.txt.select",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							new FacesScopes().getUserLocale())));
			selectItems.add(new SelectItem(1, ResourceBundleUtils
					.getMessage("lbl.sc.checklist.auto.score",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							new FacesScopes().getUserLocale())));
			selectItems.add(new SelectItem(2, ResourceBundleUtils
					.getMessage("lbl.sc.checklist.user.score",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							new FacesScopes().getUserLocale())));

			this.getManagedBean().setScoreTypeList(selectItems);
		} finally {

		}
		return this.getManagedBean().getScoreTypeList();
	}

	/**
	 * Populate values for Clone Template radio button(s)
	 * 
	 * @return an array of recommend type(s)
	 */
	public SelectItem[] getCloneTemplateRadioBtn() {
		SelectItem[] items = getRadioButtonCodeValues(ParameterTypeConst.TEMPLATE_CLONE);
		return items;
	}

	/**
	 * Check list file upload.Event handle to upload technical and financial
	 * checklist files.
	 * 
	 * @param event
	 *            (FileUploadEvent)
	 * @throws FileNotFoundException
	 *             the file not found exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void checkListFileUpload(FileUploadEvent event)
			throws FileNotFoundException, IOException {
		ScQtCheckList scQtChecklist = this.getManagedBean().getScQtChecklist();
		if (null != scQtChecklist) {
			ScQtCheckListAttachment attachment = scQtChecklist
					.getScQtCheckListAttachment();
			if (null == attachment) {
				attachment = new ScQtCheckListAttachment();
				attachment.setScQtCheckList(scQtChecklist);
			}
			attachment.setDocNo(scQtChecklist.getScQt().getQtNo());

			uploadAttachment(event.getFile(), attachment);

			scQtChecklist.setScQtCheckListAttachment(attachment);
			if (null != scQtChecklist.getScQtCheckListAttachment()) {
				scQtChecklist.setPmStatus(getPmStatusByStatusId(QTStatusConst.TS_COMPLETED));
			}
		}
	}

	/**
	 * Gets the selct value.
	 * 
	 * @return the selct value
	 */
	public String getSelctValue() {
		return selctValue;
	}

	/**
	 * Sets the selct value.
	 * 
	 * @param selctValue
	 *            the new selct value
	 */
	public void setSelctValue(String selctValue) {
		this.selctValue = selctValue;
	}

	/**
	 * Update bpm task.Event handle Submit/Accept/Revise in Business owner.
	 * 
	 * @param action
	 *            (String action)
	 * @return navigate to acknowledgement page.
	 */
	public String updateBpmTask(String action) {

		SpecificationReqDTO spReqDTO = null;

		if (BpmOutcomeConst.BPM_REVISE.equalsIgnoreCase(action)) {
			this.getManagedBean().setLocalOutCome(action);
			List<ScQtRemark> remarks = this.getManagedBean()
					.getScQtRemarksList();
			if (null != remarks) {
				for (int i = 0; i < remarks.size(); i++) {
					ScQtRemark Remark = remarks.get(i);
					String userRemark = remarks.get(i).getRemark();
					if (null == userRemark || "".equals(userRemark)) {
						throw new EpException(MessageConst.ERROR_760359);
					}
					if (null == Remark.getRemarkId()) {
						RequestContext.getCurrentInstance().execute(
								"reviceApprovalDlgs.show()");
					}
				}
				/*
				 * if (remarks.size() == 1) { ScQtRemark userRemark =
				 * remarks.get(0); if (null == userRemark.getRemarkId()) {
				 * RequestContext.getCurrentInstance().execute(
				 * "reviceApprovalDlgs.show()"); } else {
				 * reviseMinistrySpecApproval(); return
				 * CommonUIConst.ACKNOWLEDGE_PAGE; } } else { if (remarks.size()
				 * > 1) { reviseMinistrySpecApproval(); return
				 * CommonUIConst.ACKNOWLEDGE_PAGE; } }
				 */
			}

		} else if (BpmOutcomeConst.BPM_APPROVE.equalsIgnoreCase(action)) {
			this.getManagedBean().setLocalOutCome(action);
			// RequestContext.getCurrentInstance().execute("gpki_dlg.show()");
			// ministrySpecgpkiSignAction(null);
			// return CommonUIConst.ACKNOWLEDGE_PAGE;
			SpecificationResDTO resDTO = prepareSpecChairPersonRequest(this
					.getManagedBean().getLocalOutCome(), null);
			if (resDTO.isSuccessful()) {
				ScQt qt = resDTO.getScQt();
				FacesContext
						.getCurrentInstance()
						.addMessage(
								null,
								new FacesMessage(
										FacesMessage.SEVERITY_INFO,
										null,
										MessageUtils
												.getMessage(
														getMessgeConstant(this
																.getManagedBean()
																.getLocalOutCome()),
														new String[] {
																getMessageCommon("lbl.sc.specification.for"),
																qt.getQtNo() },
														MessageUtils.BUNDLE_COMMON_MESSAGES,
														new FacesScopes()
																.getUserLocale())));

				return CommonUIConst.ACKNOWLEDGE_PAGE;
			}
		} else if (BpmOutcomeConst.BPM_SUBMIT.equalsIgnoreCase(action)) {
			if (this.getManagedBean().getQt().isPanel()) {
				this.getManagedBean().setSpecComplete(false);
				retriveSupplierQualificationCriteria("BO");
				retriveTechnicalSpecification();
			} else {
				retriveSupplierQualificationCriteria("BO");
			}
			spReqDTO = prepareSpecReq();
			List<ScQtLocCriteria> qtLocCriteriaRemoveList = this
					.getManagedBean().getSupplierLocationList();
			if (null != qtLocCriteriaRemoveList
					&& !qtLocCriteriaRemoveList.isEmpty()) {

				for (ScQtLocCriteria criteria : qtLocCriteriaRemoveList) {
					criteria.setDeleted(true);
				}
			}
			spReqDTO.setTobeRemoveLocCriteriaList(qtLocCriteriaRemoveList);
			spReqDTO.setOutcome(action);
			spReqDTO.setPrepareSpecStatus(this.getManagedBean()
					.getCurrentStatusId());
			spReqDTO.setTaskId(this.getManagedBean().getTaskId());
			spReqDTO.setPayLoad(this.getManagedBean().getPayLoad());
			FacesScopes facesScopes = new FacesScopes();
			spReqDTO.setPmUser(facesScopes.getUserSessionBean());
			spReqDTO.setRoleCode(getUserRoleCode());
			spReqDTO.setToken(facesScopes.getBPMAuthenticationCtxToken());
			IQTSpecSO specSO = getSpecSO();
			SpecificationResDTO resDTO = specSO.updateBPMAction(spReqDTO);
			if (resDTO.isSuccessful()) {
				ScQt qt = resDTO.getScQt();
				// Code to renew the session Object..
				renewSesionObject();

				if (qt.getMinistrySpecSecrataryId() == null) {
					FacesContext
							.getCurrentInstance()
							.addMessage(
									null,
									new FacesMessage(
											FacesMessage.SEVERITY_INFO,
											null,
											MessageUtils
													.getMessage(
															MessageConst.INFO_700005,
															new String[] {
																	getMessageCommon("lbl.sc.specification.for"),
																	qt.getQtNo() },
															MessageUtils.BUNDLE_COMMON_MESSAGES,
															new FacesScopes()
																	.getUserLocale())));
				} else {
					FacesContext
							.getCurrentInstance()
							.addMessage(
									null,
									new FacesMessage(
											FacesMessage.SEVERITY_INFO,
											null,
											MessageUtils
													.getMessage(
															MessageConst.INFO_700018,
															new String[] {
																	getMessageCommon("lbl.sc.specification.for"),
																	qt.getQtNo() },
															MessageUtils.BUNDLE_COMMON_MESSAGES,
															new FacesScopes()
																	.getUserLocale())));
				}

				return CommonUIConst.ACKNOWLEDGE_PAGE;
			}

		}
		return null;
	}

	/**
	 * Gets the spec so.
	 * 
	 * @return the spec so
	 */
	private IQTSpecSO getSpecSO() {
		return BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);
	}

	/**
	 * Paneltab change.
	 * 
	 * @param event
	 *            the event
	 */
	public void paneltabChange(TabChangeEvent event) {

	}

	/**
	 * Retrive zones or suppliers by item.retrive zones or suppliers based on
	 * item in supplier qualification criteria.
	 */
	public void retriveZonesOrSuppliersByItem() {
		IPanelSupplierSO panelSupplierSO;
		PanelSupplierResDTO resDTO = new PanelSupplierResDTO();
		try {

			PanelSupplierReqDTO reqDTO = new PanelSupplierReqDTO();

			panelSupplierSO = (IPanelSupplierSO) BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,
							IPanelSupplierSO.class);
			
			List<CtPanelItem> panelItemList = getManagedBean().getPanelItemList();

			if (CollectionUtils.isNotEmpty(panelItemList)) {

				reqDTO.setPanelItem(panelItemList.get(0));
				resDTO = panelSupplierSO.retrievePanelZonesByPanelItem(reqDTO);
			}

			if (null != resDTO && null != resDTO.getCtPanelZones()
					&& !resDTO.getCtPanelZones().isEmpty()) {

				if (isNotNullOREmpty(resDTO.getCtPanelZones())) {

					this.getManagedBean().setCtzones(true);
					getManagedBean().setZoneGeneralTrees(
							new ArrayList<TreeNode>());
					if (null != getManagedBean().getZoneGeneralTrees()
							&& getManagedBean().getZoneGeneralTrees().size() == 0) {

						for (CtPanelZone panelZones : resDTO.getCtPanelZones()) {

							TplSpecZoneDtl tplSpecZoneDtl = new TplSpecZoneDtl();
							tplSpecZoneDtl.setZoneName(panelZones.getName());
							tplSpecZoneDtl.setPanelZone(panelZones);
							setUserDetails(tplSpecZoneDtl);

							List<PmState> states = SourcingLocationHelper
									.getStateFromCtPanelZone(panelZones
											.getPanelZoneDtls());
							List<PmDistrict> districts = SourcingLocationHelper
									.getDistrictFromCtPanelZone(panelZones
											.getPanelZoneDtls());

							getManagedBean()
									.getZoneGeneralTrees()
									.add(LocationSingleton.getInstance()
											.buildTreeWithDisabledChilds(
													tplSpecZoneDtl, states,
													districts, null, null,
													true, TplSpecZoneLoc.class));

						}
					}

				}
			} else {
				
				this.getManagedBean().setTotalSuppliers(0);
				
				if (CollectionUtils.isNotEmpty(panelItemList)) {
					getManagedBean().setZoneGeneralTrees(
							new ArrayList<TreeNode>());
					this.getManagedBean().setCtzones(false);
					/*
					 * PanelSupplierReqDTO supplierreqDTO = new
					 * PanelSupplierReqDTO();
					 * supplierreqDTO.setPanelItemId(this.getManagedBean()
					 * .getCtPanelItem().getPanelItemId());
					 * supplierreqDTO.setFetchSubEntity(true);
					 * PanelSupplierResDTO panelSupplierResDTO = panelSupplierSO
					 * .retrievePanelItem(supplierreqDTO);
					 */
					IQTSupplierSO supplierSo = (IQTSupplierSO) BusinessDelegate
							.getInstance().getServiceObject(
									ModuleCodeConst.SUPPLIER_SELECTION,
									IQTSupplierSO.class);
					QTSupplierReqDTO supplierReqDTO = new QTSupplierReqDTO();
					QTSupplierResDTO supplierResDTO = new QTSupplierResDTO();
					supplierReqDTO.setPanelItemId(panelItemList.get(0).getPanelItemId());
					supplierResDTO = supplierSo
							.retrievePanelSuppliersByPanelItem(supplierReqDTO);
					this.getManagedBean().setCtZoneSupplierList(
							new ArrayList<CtPanelSupplier>());
					if (null != supplierResDTO.getCtPanelSuppliers()) {
						// CtPanelItem panelItem =
						// supplierResDTO.getCtPanelSuppliers();
						this.getManagedBean().setTotalSuppliers(
								supplierResDTO.getCtPanelSuppliers().size());

						this.getManagedBean().setCtZoneSupplierList(
								supplierResDTO.getCtPanelSuppliers());

					}
				}

			}
			/*
			 * List<CtPanelItem> itemList = this.getManagedBean()
			 * .getSelectpanelItem(); itemList = new ArrayList<CtPanelItem>();
			 * itemList.add(this.getManagedBean().getCtPanelItem());
			 * 
			 * this.getManagedBean().setSelectpanelItem(itemList);
			 */

		} finally {

		}

	}

	/**
	 * Retrive tpl spec zone dtls.
	 * 
	 * @param selectedTreeNode
	 *            the selected tree node
	 * @return the tpl spec zone dtl
	 */
	private TplSpecZoneDtl retriveTplSpecZoneDtls(TreeNode selectedTreeNode) {
		TplSpecZoneDtl tplSpecZoneDtl = null;
		List<TreeNode> zoneNodes = selectedTreeNode.getChildren();

		if (null != zoneNodes && zoneNodes.size() > 0) {
			for (TreeNode treeNode1 : zoneNodes) {

				List<TreeNode> stateNodes = treeNode1.getChildren();

				for (TreeNode treeNode2 : stateNodes) {

					List<TreeNode> district = treeNode2.getChildren();
					if (null != district && !district.isEmpty()) {
						for (TreeNode devision : district) {

							if (devision.getChildCount() > 0) {
								for (TreeNode districtNode : devision
										.getChildren()) {

									TplSpecZoneLoc criteria = (TplSpecZoneLoc) districtNode
											.getData();

									criteria.setTplSpecZoneDtl(((TplSpecZoneDtl) selectedTreeNode
											.getData()));
									tplSpecZoneDtl = criteria
											.getTplSpecZoneDtl();

								}

							} else {

								TplSpecZoneLoc criteria = (TplSpecZoneLoc) devision
										.getData();

								criteria.setTplSpecZoneDtl(((TplSpecZoneDtl) selectedTreeNode
										.getData()));
								tplSpecZoneDtl = criteria.getTplSpecZoneDtl();

							}

						}
					} else {

						TplSpecZoneLoc criteria = (TplSpecZoneLoc) treeNode2
								.getData();

						criteria.setTplSpecZoneDtl(((TplSpecZoneDtl) selectedTreeNode
								.getData()));
						tplSpecZoneDtl = criteria.getTplSpecZoneDtl();

					}

				}
				if (null != stateNodes && stateNodes.size() == 0) {
					TplSpecZoneLoc criteria = (TplSpecZoneLoc) treeNode1
							.getData();
					criteria.setTplSpecZoneDtl(((TplSpecZoneDtl) selectedTreeNode
							.getData()));
					tplSpecZoneDtl = criteria.getTplSpecZoneDtl();
				}

			}
		}
		return tplSpecZoneDtl;
	}

	/**
	 * Un select panel yes treenode.Event handle to unselect panel treenodes.
	 * 
	 * @param event
	 *            (TreeNode treeNode).
	 */
	public void unSelectPanelYesTreenode(NodeUnselectEvent event) {
		TreeNode selectedTreeNode = event.getTreeNode();

		selectedTreeNode.setSelected(false);
	}

	/**
	 * Retrive supplier dtls.retrive supplier information based on supplier in
	 * supplier qualification criteria.
	 * 
	 * @param panelSupplier
	 *            (CtPanelSupplier panelSupplier).
	 */
	public void retriveSupplierDtls(CtPanelSupplier panelSupplier) {

		QTSupplierResDTO qtSupplierResDTO = new QTSupplierResDTO();
		MofAccountResDTO mofAccResDTO = new MofAccountResDTO();
		IQTSupplierSO qtSupplierSO = null;
		IMofAccountSO mofAccountSO = null;

		try {
			if (panelSupplier.getSupplier() != null) {
				qtSupplierSO = BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
								IQTSupplierSO.class);
				QTSupplierReqDTO qtSupplierReqDTO = new QTSupplierReqDTO();
				qtSupplierReqDTO.setSupplierId(panelSupplier.getSupplier()
						.getSupplierId());
				qtSupplierResDTO = qtSupplierSO
						.retrieveSupplierDetails(qtSupplierReqDTO);
				panelSupplier.setSupplier(qtSupplierResDTO.getSmSupplier());
				// getManagedBean().setSmSupplier(smSupplier);
				// this.getManagedBean().setCtpanelSupplier(panelSupplier);

				mofAccountSO = (IMofAccountSO) BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT,
								IMofAccountSO.class);
				MofAccountReqDTO mofAccReqDTO = new MofAccountReqDTO();
				mofAccReqDTO.setSupplierId(panelSupplier.getSupplier()
						.getSupplierId());
				mofAccResDTO = mofAccountSO
						.retrieveMofNoBySupplierId(mofAccReqDTO);
				getManagedBean().setSmMofAccount(mofAccResDTO.getMofAccount());

				SmCompanyBasic smCompanyBasic = panelSupplier.getSupplier()
						.getSmAppl().getSmCompanyBasic();
				// getManagedBean().setSmCompanyBasic(smCompanyBasic);

				List<SmPersonnel> smPersonnelList = panelSupplier.getSupplier()
						.getSmAppl().getSmPersonnels();
				if (null != smPersonnelList) {
					for (SmPersonnel smPersonnel : smPersonnelList) {
						if (smPersonnel.isContactPerson()) {
							// getManagedBean().setSmPersonnel(smPersonnel);
							getManagedBean().setPersonnel(smPersonnel);

							break;
						}
					}
				}
				SmPersonnel supplierHQPersonnel = this.getManagedBean()
						.getPersonnel();
				getManagedBean().setSupplierContact("");
				getManagedBean().setSupplierEmail("");
				if (null != supplierHQPersonnel) {
					getManagedBean().setSupplierContact(
							supplierHQPersonnel.getName());
					getManagedBean().setSupplierEmail(
							supplierHQPersonnel.getEmail());
				}
				if (null != smCompanyBasic) {
					for (SmCompanyAddress smCompanyAddress : smCompanyBasic
							.getSmCompanyAddresses()) {
						if (AddressType.CONTACT_ADDRESS.value().equals(
								smCompanyAddress.getAddressType())) {
							getManagedBean().setSupplierBranch(
									QTConstants.SUPPLIER_INFO_HQ);
							getManagedBean().setSupplierAddress(
									smCompanyAddress.getSmAddress());
							break;
						}
					}
				}
				this.getManagedBean().setCtpanelSupplier(panelSupplier);
			}
		} finally {
			qtSupplierSO = null;
			mofAccountSO = null;
		}

		/*
		 * if (null != panelSupplier.getSupplier() && null !=
		 * panelSupplier.getSupplier().getSmAppl() && null !=
		 * panelSupplier.getSupplier().getSmAppl() .getSmPersonnels()) {
		 * List<SmPersonnel> list = panelSupplier.getSupplier().getSmAppl()
		 * .getSmPersonnels(); if (null != list) {
		 * 
		 * for (SmPersonnel smPersonnel : list) {
		 * getManagedBean().setPersonnel(smPersonnel); } SmPersonnel
		 * supplierHQPersonnel = this.getManagedBean() .getPersonnel();
		 * getManagedBean().setSupplierContact("");
		 * getManagedBean().setSupplierEmail(""); if (null !=
		 * supplierHQPersonnel) { getManagedBean().setSupplierContact(
		 * supplierHQPersonnel.getName()); getManagedBean().setSupplierEmail(
		 * supplierHQPersonnel.getEmail()); } // SmAppl appll=
		 * supplierHQPersonnel.getSmAppl();
		 * 
		 * SmCompanyBasic smCompanyBasic = panelSupplier.getSupplier()
		 * .getSmAppl().getSmCompanyBasic();
		 * 
		 * // System.out.println("INcomeTax No : " + //
		 * smCompanyBasic.getIncomeTaxNo());
		 * 
		 * if (null != smCompanyBasic) { if (null !=
		 * smCompanyBasic.getSmCompanyAddresses()) { for (SmCompanyAddress
		 * smCompanyAddress : smCompanyBasic .getSmCompanyAddresses()) { if
		 * (null != smCompanyAddress) { if
		 * (AddressType.CONTACT_ADDRESS.value().equals(
		 * smCompanyAddress.getAddressType())) {
		 * 
		 * getManagedBean().setSupplierBranch("HQ");
		 * 
		 * getManagedBean().setSupplierAddress(
		 * smCompanyAddress.getSmAddress());
		 * 
		 * break;
		 * 
		 * } } } } } } }
		 */

	}

	/**
	 * Circular list select.retrieve Panel Item List based on circular
	 * information.
	 * 
	 * @param circular
	 *            (CtCircularVer circular).
	 */
	public void circularListSelect(CtCircularVer circular) {
		ICircularDetailSO circularDetailSO;
		try {
			CircularDetailReqDTO reqDTO = new CircularDetailReqDTO();
			this.getManagedBean().setCtCircularVer(circular);
			circularDetailSO = (ICircularDetailSO) EpEjbBusinessDelegate
					.getInstance().getServiceObject(
							ModuleCodeConst.CONTRACT_MANAGEMENT,
							ICircularDetailSO.class);
			if (null != circular) {
				reqDTO.setCircularVerId(circular.getCircularVerId());
				// reqDTO.setQuotationTender(true);
				reqDTO.setPanelItemTypeGroup(true);
				if (null != this.getManagedBean().getQt().getItemGroup()) {
					reqDTO.setGroupItemId(this.getManagedBean().getQt()
							.getItemGroup().getPanelItemGroupId());
				}
				CircularDetailResDTO resDTO = circularDetailSO
						.getPanelItemsByCircularVerId(reqDTO);
				// this.getManagedBean().setCircularExpDate(circular.getEndDate());
				// this.getManagedBean().setCircularNo(circular.getCircularNo());
				// this.getManagedBean().setCircularName(circular.getName());
				this.getManagedBean().setCircularType(circular.getDocType());
				this.getManagedBean().setPanelItemList(
						resDTO.getPanelItemList());
				this.getManagedBean().setAutoUpdate(false);
			}

		} finally {

		}

	}

	/**
	 * Tech weightage sum.calculate technical scoring weightage.
	 */
	private void techWeightageSum() {
		try {
			int total = 0;
			List<ScQtCheckList> weightageList = this.getManagedBean()
					.getTechnicalScoring();
			if (weightageList == null) {
				weightageList = new ArrayList<ScQtCheckList>();
			}
			for (int i = 0; i < weightageList.size(); i++) {
				ScQtCheckList weight = (ScQtCheckList) weightageList.get(i);
				if (null != weight.getQtScoreWeightage()
						&& weight.getQtScoreWeightage().getWeightage() == null) {
					weight.getQtScoreWeightage()
							.setWeightage(new BigDecimal(0));
				}
				if (null != weight.getQtScoreWeightage()) {
					total = total
							+ weight.getQtScoreWeightage().getWeightage()
									.intValue();
				}
				this.getManagedBean().setTechnicalWeightageScore(
						new BigDecimal(total));
				if (total > 100) {
					throw new EpException(MessageConst.ERROR_760148);
				}

			}
		} finally {

		}

	}

	/**
	 * Tech scoring check list.Event handle change scores in technical scoring
	 * Menu.
	 * 
	 * @param checkList
	 *            (ScQtCheckList checkList)
	 */
	public void techScoringCheckList(ScQtCheckList checkList) {
		techScoreSum();
		techWeightageSum();
		List<ScQtCheckList> technicalCheckList = this.getManagedBean()
				.getScQtCheckSt2techProposal();
		if (null == technicalCheckList) {
			technicalCheckList = new ArrayList<ScQtCheckList>();
		}
		for (int i = 0; i < technicalCheckList.size(); i++) {
			if (checkList.getTitle().equalsIgnoreCase(
					technicalCheckList.get(i).getTitle())) {
				ScQtCheckList localCheckList = technicalCheckList.get(i);
				// technicalCheckList.setDeleted(true);
				// toBeRemoveCheckLists.add(technicalCheckList);
				technicalCheckList.remove(localCheckList);
				technicalCheckList.add(checkList);
				break;
			}
		}
	}

	/**
	 * Fin scoring check list.Event handle change scores in Financial scoring
	 * Menu.
	 * 
	 * @param checkList
	 *            (ScQtCheckList checkList).
	 */
	public void finScoringCheckList(ScQtCheckList checkList) {
		finScoreSum();
		finWeightageSum();
		List<ScQtCheckList> financialCheckList = this.getManagedBean()
				.getScQtCheckSt2financeProposalList();
		if (null == financialCheckList) {
			financialCheckList = new ArrayList<ScQtCheckList>();
		}
		for (int i = 0; i < financialCheckList.size(); i++) {
			if (checkList.getTitle().equalsIgnoreCase(
					financialCheckList.get(i).getTitle())) {
				ScQtCheckList localCheckList = financialCheckList.get(i);
				// technicalCheckList.setDeleted(true);
				// toBeRemoveCheckLists.add(technicalCheckList);
				financialCheckList.remove(localCheckList);
				financialCheckList.add(checkList);
				break;
			}
		}
	}

	/**
	 * Tech score sum.calculate technical scoring sum.
	 */
	private void techScoreSum() {
		try {
			int total = 0;
			List<ScQtCheckList> weightageList = this.getManagedBean()
					.getTechnicalScoring();
			if (weightageList == null) {
				weightageList = new ArrayList<ScQtCheckList>();
			}
			for (int i = 0; i < weightageList.size(); i++) {
				ScQtCheckList weight = (ScQtCheckList) weightageList.get(i);
				if (null != weight.getQtScoreWeightage()
						&& weight.getQtScoreWeightage().getWeightage() == null) {
					weight.getQtScoreWeightage()
							.setWeightage(new BigDecimal(0));
				}
				if (null != weight.getQtScoreWeightage()) {
					total = total
							+ weight.getQtScoreWeightage().getScore()
									.intValue();
				}
				BigDecimal techScore = new BigDecimal(total);
				this.getManagedBean().setTechPoints(techScore.intValue());
			}
		} finally {

		}

	}

	/**
	 * Fin score sum.calculate financial score sum.
	 */
	private void finScoreSum() {
		try {
			int total = 0;
			List<ScQtCheckList> weightageList = this.getManagedBean()
					.getFinancialScoring();
			if (weightageList == null) {
				weightageList = new ArrayList<ScQtCheckList>();
			}
			for (int i = 0; i < weightageList.size(); i++) {
				ScQtCheckList weight = (ScQtCheckList) weightageList.get(i);
				if (null != weight.getDefinationType()
						&& ParameterConst.DOCUMENT_DEFINITION_TYPE_TEMPLATE_SPECIFICATION
								.equalsIgnoreCase(weight.getDefinationType()
										.getParameterCode())) {

					getManagedBean().setTechnicalChecklistScoring(
							weight.getQtScoreWeightage().getScore().intValue());
					getManagedBean().setScoreVarience(
							new BigDecimal(getManagedBean()
									.getTechnicalChecklistScoring()));

				}

				if (null != weight.getQtScoreWeightage()
						&& weight.getQtScoreWeightage().getWeightage() == null) {
					weight.getQtScoreWeightage()
							.setWeightage(new BigDecimal(0));
				}
				if (null != weight.getQtScoreWeightage()) {
					total = total
							+ weight.getQtScoreWeightage().getScore()
									.intValue();
				}
				BigDecimal techScore = new BigDecimal(total);
				this.getManagedBean().setFinPoints(techScore.intValue());
			}
		} finally {

		}

	}

	/**
	 * Financial weightage calculate.calculate financial weightage in financial
	 * scoring menu.
	 */
	private void financialWeightageCalculate() {
		if (null != this.getManagedBean().getFinancialScoreSetting()
				&& this.getManagedBean().getFinancialScoreSetting()
						.isWeightageReq()
				&& !this.getManagedBean().isReadOnly()) {
			List<ScQtCheckList> checklist = this.getManagedBean()
					.getFinancialScoring();
			if (null == checklist) {
				checklist = new ArrayList<ScQtCheckList>();
			}
			Map<Long, ScQtScoreWeightage> map = new HashMap<Long, ScQtScoreWeightage>();
			List<ScQtScoreWeightage> finscoreWeightage = new ArrayList<ScQtScoreWeightage>();
			int financialWeightage = 0;
			for (ScQtCheckList checklistlocal : checklist) {
				if (ParameterConst.DOCUMENT_DEFINITION_TYPE_TEMPLATE_SPECIFICATION
						.equalsIgnoreCase(checklistlocal.getDefinationType()
								.getParameterCode())) {
					if (null != checklistlocal.getQtScoreWeightage()) {
						map.put(checklistlocal.getQtScoreWeightage()
								.getQtScoreWeightageId(), checklistlocal
								.getQtScoreWeightage());
					}
				} else {
					if (null != checklistlocal.getQtScoreWeightage()) {
						finscoreWeightage.add(checklistlocal
								.getQtScoreWeightage());
					}
				}
			}
			List<ScQtScoreWeightage> list = new ArrayList<ScQtScoreWeightage>(
					map.values());
			list.addAll(finscoreWeightage);
			if (null != list && !list.isEmpty()) {
				for (ScQtScoreWeightage weightage : list) {
					if (null == weightage.getWeightage()) {
						weightage.setWeightage(new BigDecimal(0));
					}
					financialWeightage = financialWeightage
							+ weightage.getWeightage().intValue();
				}
				if (financialWeightage < 100) {
					this.getManagedBean().setActiveIndex(5);
					throw new EpException(MessageConst.ERROR_760221);
				}
			}
		}

	}

	/**
	 * Technical weightage calculate.calculate technical weightage in technical
	 * scoring menu.
	 */
	private void technicalWeightageCalculate() {
		if (null != this.getManagedBean().getTechnicalScoreSetting()
				&& this.getManagedBean().getTechnicalScoreSetting()
						.isWeightageReq()
				&& !this.getManagedBean().isReadOnly()) {
			List<ScQtCheckList> checklist = this.getManagedBean()
					.getTechnicalScoring();
			if (null == checklist) {
				checklist = new ArrayList<ScQtCheckList>();
			}
			// Map <ScQtScoreWeightage> map = new HashMap<ScQtScoreWeightage>();
			int technicalWeightage = 0;
			for (ScQtCheckList checklistlocal : checklist) {
				if (null != checklistlocal.getQtScoreWeightage()) {
					if (null == checklistlocal.getQtScoreWeightage()
							.getWeightage()) {
						checklistlocal.getQtScoreWeightage().setWeightage(
								new BigDecimal(0));
					}

					technicalWeightage = technicalWeightage
							+ checklistlocal.getQtScoreWeightage()
									.getWeightage().intValue();
				}
			}

			if (technicalWeightage < 100) {
				this.getManagedBean().setActiveIndex(4);
				throw new EpException(MessageConst.ERROR_760220);
			}
		}

	}

	/**
	 * Fin weightage sum.calculate Financial weightage.
	 */
	private void finWeightageSum() {
		try {
			int total = 0;
			List<ScQtCheckList> weightageList = this.getManagedBean()
					.getFinancialScoring();
			if (weightageList == null) {
				weightageList = new ArrayList<ScQtCheckList>();
			}
			for (int i = 0; i < weightageList.size(); i++) {
				ScQtCheckList weight = (ScQtCheckList) weightageList.get(i);
				if (null != weight.getQtScoreWeightage()
						&& weight.getQtScoreWeightage().getWeightage() == null) {
					weight.getQtScoreWeightage()
							.setWeightage(new BigDecimal(0));
				}
				if (null != weight.getQtScoreWeightage()) {
					total = total
							+ weight.getQtScoreWeightage().getWeightage()
									.intValue();
				}
				this.getManagedBean().setFinancialWeightageScore(
						new BigDecimal(total));

				if (total > 100) {
					throw new EpException(MessageConst.ERROR_760148);
				}

			}
		} finally {

		}

	}

	/**
	 * Spec offerprice package from varieance.check offer price from varienace
	 * null or less than zero.
	 * 
	 * @param (ScQtScoreOfferPrice offerPrice).
	 */
	public void specOfferpricePackageFromVarieance(
			ScQtScoreOfferPrice offerPrice) {
		if (null != offerPrice) {
			if (null == offerPrice.getFromVariance()
					|| offerPrice.getFromVariance().intValue() <= 0) {
				throw new EpException(MessageConst.ERROR_760224);
			}

		}

	}

	/**
	 * Spec offerprice package to varieance.check offer price to varienace null
	 * or less than zero.
	 * 
	 * @param (ScQtScoreOfferPrice offerPrice).
	 */
	public void specOfferpricePackageToVarieance(ScQtScoreOfferPrice offerPrice) {
		if (null != offerPrice) {
			if (null == offerPrice.getToVariance()
					|| offerPrice.getToVariance().intValue() <= 0) {
				throw new EpException(MessageConst.ERROR_760225);
			}

		}

	}

	/**
	 * Spec offerprice package below score.check offer price belowScore null or
	 * less than zero.
	 * 
	 * @param (ScQtScoreOfferPrice offerPrice).
	 * 
	 * 
	 */
	public void specOfferpricePackageBelowScore(ScQtScoreOfferPrice offerPrice) {
		if (null != offerPrice) {
			if (null == offerPrice.getBelowScore()
					|| offerPrice.getBelowScore().intValue() < 0) {
				throw new EpException(MessageConst.ERROR_760216);
			}

		}

	}

	/**
	 * Spec offerprice package above score.check offer price aboveScore null or
	 * less than zero.
	 * 
	 * @param (ScQtScoreOfferPrice offerPrice).
	 * 
	 * 
	 */
	public void specOfferpricePackageAboveScore(ScQtScoreOfferPrice offerPrice) {
		if (null != offerPrice) {

			if (null == offerPrice.getAboveScore()
					|| offerPrice.getAboveScore().intValue() < 0) {
				throw new EpException(MessageConst.ERROR_760217);
			}
		}

	}

	/**
	 * Check technical passing score.check technical passing score null or less
	 * than zero or greater than hundred.
	 * 
	 * @param technicalScoreSetting
	 *            (ScQtScoreSetting technicalScoreSetting).
	 */
	public void checkTechnicalPassingScore(
			ScQtScoreSetting technicalScoreSetting) {
		if (null != technicalScoreSetting) {
			BigDecimal passingScore = retrieveTechnicalEvalutionPassingScorePmrules(RuleTypeConst.SC_TECHNICAL_PASSING_SCORE);
			if (null == technicalScoreSetting.getPassingScore()
					|| technicalScoreSetting.getPassingScore().intValue() <= 0) {
				this.getManagedBean().setActiveIndex(4);
				throw new EpException(MessageConst.ERROR_760234);
			}
			if (null != technicalScoreSetting.getPassingScore()
					&& technicalScoreSetting.getPassingScore().intValue() > 100) {
				this.getManagedBean().setActiveIndex(4);
				throw new EpException(MessageConst.ERROR_760142);
			}
			if (null != passingScore
					&& (technicalScoreSetting.getPassingScore().intValue() < passingScore
							.intValue())) {
				technicalScoreSetting.setPassingScore(new BigDecimal(50));
				this.getManagedBean().setTechnicalScoreSetting(
						technicalScoreSetting);
				throw new EpException(MessageConst.ERROR_760364,
						new String[] { String.valueOf(passingScore) });
			}

		}

	}

	/**
	 * Check financial passing score.check financial passing score null or less
	 * than zero or greater than hundred.
	 * 
	 * @param financialScoreSetting
	 *            (ScQtScoreSetting financialScoreSetting).
	 * 
	 * 
	 */
	public void checkFinancialPassingScore(
			ScQtScoreSetting financialScoreSetting) {
		if (null != financialScoreSetting) {

			if (null == financialScoreSetting.getPassingScore()
					|| financialScoreSetting.getPassingScore().intValue() < 0) {
				this.getManagedBean().setActiveIndex(5);
				throw new EpException(MessageConst.ERROR_760174);
			} else if (null != financialScoreSetting.getPassingScore()
					&& financialScoreSetting.getPassingScore().intValue() > 100) {
				this.getManagedBean().setActiveIndex(5);
				throw new EpException(MessageConst.ERROR_760143);
			}
		}

	}

	/**
	 * Check demo passing score..check technical demo passing score null or less
	 * than zero or greater than hundred.
	 * 
	 * @param demoScoreSetting
	 *            (ScQtScoreSetting demoScoreSetting).
	 * 
	 */
	public void checkDemoPassingScore(ScQtScoreSetting demoScoreSetting) {
		if (null != demoScoreSetting) {

			if (null == demoScoreSetting.getPassingScore()
					|| demoScoreSetting.getPassingScore().intValue() < 0) {
				this.getManagedBean().setActiveIndex(4);
				throw new EpException(MessageConst.ERROR_760173);
			} else if (null != demoScoreSetting.getPassingScore()
					&& demoScoreSetting.getPassingScore().intValue() > 100) {
				this.getManagedBean().setActiveIndex(4);
				throw new EpException(MessageConst.ERROR_760144);
			}
		}

	}

	/**
	 * Gets the search circular.
	 * 
	 * @return the search circular
	 */
	public IPagingDataModel<CtCircularVer> getSearchCircular() {
		return searchCircular;
	}

	/**
	 * Sets the search circular.
	 * 
	 * @param searchCircular
	 *            the new search circular
	 */
	public void setSearchCircular(IPagingDataModel<CtCircularVer> searchCircular) {
		this.searchCircular = searchCircular;
	}

	// Search circular Details Method.
	/**
	 * Search circular list.
	 */
	public void searchCircularList() {
		searchCircular.reloadData();
		this.getManagedBean().setCircularVer(null);

	}

	/**
	 * The Class SearchCircularPagingDataModel.search circular paging data
	 * model.
	 */
	private class SearchCircularPagingDataModel extends
			PagingDataModel<CtCircularVer> {

		/** The Constant serialVersionUID. */
		private static final long serialVersionUID = 1L;

		/**
		 * Instantiates a new search circular paging data model.
		 */
		public SearchCircularPagingDataModel() {

			super("circularList");

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * my.ep.web.bean.paginator.PagingDataModel#pageChangeListener(my.ep
		 * .biz.bean.search.ISearchBean)
		 */
		public ISearchResultBean<CtCircularVer> pageChangeListener(
				ISearchBean<CtCircularVer> searchBean) {
			ICircularSO circularSO;
			CircularResDTO resDto;

			try {
				CircularReqDTO circularReqDTO = new CircularReqDTO();

				if (null != getManagedBean().getCtSearchBean().getCirCularNo()
						&& !"".equalsIgnoreCase(getManagedBean()
								.getCtSearchBean().getCirCularNo())) {

					searchBean.getDataFilterBeans().add(
							new SearchFilterBean("circularNo",
									SearchOperator.OPERATOR_LIKE.value(),
									getManagedBean().getCtSearchBean()
											.getCirCularNo(), true));

				}

				if (null != getManagedBean().getCtSearchBean()
						.getCircularName()
						&& !"".equalsIgnoreCase(getManagedBean()
								.getCtSearchBean().getCircularName())) {

					searchBean.getDataFilterBeans().add(
							new SearchFilterBean("name",
									SearchOperator.OPERATOR_LIKE.value(),
									getManagedBean().getCtSearchBean()
											.getCircularName(), true));
				}

				if (null != getManagedBean().getCtSearchBean().getItemName()
						&& !"".equalsIgnoreCase(getManagedBean()
								.getCtSearchBean().getItemName())) {

					searchBean.getDataFilterBeans().add(
							new SearchFilterBean("ctPanelItems.itemName",
									SearchOperator.OPERATOR_LIKE.value(),
									getManagedBean().getCtSearchBean()
											.getItemName(), true));
				}

				searchBean.getDataFilterBeans().add(
						new SearchFilterBean("qt",
								SearchOperator.OPERATOR_EQUALS.value(), true));

				searchBean.getDataFilterBeans().add(
						new SearchFilterBean("docType",
								SearchOperator.OPERATOR_EQUALS.value(),
								ParameterConst.DOCTYPE_PANEL_CIRCULAR));

				circularReqDTO.setCircularVerSearchBean(searchBean);

				circularSO = EpEjbBusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,
								ICircularSO.class);
				// resDto = circularSO.searchCircularVer(circularReqDTO);
				resDto = circularSO.retrieveActiveCircular(circularReqDTO);

				if (resDto.isSuccessful()) {
					// System.out.println("circular and panel type"+resDto.getCircularVerResultBean().getResultList());
					return resDto.getCircularVerResultBean();
				}
			} finally {
				circularSO = null;
			}
			return null;

		}
	}

	/**
	 * Search template spec action.Event handle search template spec.
	 */
	public void searchTemplateSpecAction() {
		templateSpecCreate.reloadData();
	}

	/**
	 * Search delivery address.Event handle search delivery addresses.
	 */
	public void searchDeliveryAddress() {
		getManagedBean().getSearchDeliveryAddressDataModel().reloadData();
	}

	/**
	 * Reset template.Event handle to clear all the template spec data.
	 */
	public void resetTemplate() {
		getManagedBean().setSpecName(null);
		getManagedBean().setSearchCreatedBy(null);
		// getManagedBean().setStandardTemplate(null);
		// getManagedBean().setSearchProcurementId(null);
		if (!this.getManagedBean().getQt().isPanel()) {
			getManagedBean().setSearchItemTypeId(null);
		}
		this.getManagedBean().getQtnTendertemplateSearchBean().setQtTitle(null);
		this.getManagedBean().getQtnTendertemplateSearchBean().setQtNo(null);
		this.getManagedBean().getQtnTendertemplateSearchBean()
				.setProcurementMode(null);
		this.getManagedBean().getQtnTendertemplateSearchBean()
				.setFromCreatedDate(null);
		this.getManagedBean().getQtnTendertemplateSearchBean()
				.setToCreatedDate(null);
		this.getManagedBean().getQtnTendertemplateSearchBean()
				.setFromPublishDate(null);
		this.getManagedBean().getQtnTendertemplateSearchBean()
				.setToPublishDate(null);
		this.getManagedBean().getQtnTendertemplateSearchBean()
				.setFromClosingDate(null);
		this.getManagedBean().getQtnTendertemplateSearchBean()
				.setToClosingDate(null);
		this.getManagedBean().getQtnTendertemplateSearchBean()
				.setQtStatus(null);
	}

	/**
	 * The Class CreateTemplateSpecPagingDataModel.search Template spec paging
	 * data model.
	 */
	private class CreateTemplateSpecPagingDataModel extends
			PagingDataModel<TplSpecMaster> {

		/** The Constant serialVersionUID. */
		private static final long serialVersionUID = 1L;

		/**
		 * Instantiates a new creates the template spec paging data model.
		 */
		public CreateTemplateSpecPagingDataModel() {

			super("createList", false);

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * my.ep.web.bean.paginator.PagingDataModel#pageChangeListener(my.ep
		 * .biz.bean.search.ISearchBean)
		 */
		public ISearchResultBean<TplSpecMaster> pageChangeListener(
				ISearchBean<TplSpecMaster> searchBean) {
			ITemplateSpecSO qtTemplateSO;
			TemplateSpecResDTO resDto;
			IParameterSO parameterSo;

			try {
				TemplateSpecReqDTO qtTemplateReqDTO = new TemplateSpecReqDTO();
				parameterSo = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
				ParameterReqDTO techSpecType = new ParameterReqDTO();
				techSpecType
						.setParameterType(ParameterTypeConst.SPECIFICATION_TYPE);
				techSpecType.setParameterCode(ParameterConst.ANSWER_TYPE_TEXT);
				ParameterResDTO techspecTyperesDTO = parameterSo
						.retrieveParameterByTypeAndCode(techSpecType);
				String specTypeCode = techspecTyperesDTO.getPmParameter()
						.getParameterCode();
				if (TemplateSpecConst.PROCESS_TYPE_STANDARD
						.equalsIgnoreCase(getManagedBean()
								.getStandardTemplate())) {
					if (null != getManagedBean().getQt()
							.getPmProcurementCategory()
							&& null != getManagedBean().getQt()
									.getPmProcurementCategory()
									.getParameterId()) {
						getManagedBean().setSearchProcurementId(
								getManagedBean().getQt()
										.getPmProcurementCategory()
										.getParameterId());
					}

					// Filter By - created by
					if (getManagedBean().getSearchCreatedBy() != null) {
						searchBean.getDataFilterBeans().add(
								new SearchFilterBean("createdBy",
										SearchOperator.OPERATOR_EQUALS.value(),
										getManagedBean().getSearchCreatedBy()));
					}

					// Filter By - ministry orgProfileId
					ScQt scQt = getManagedBean().getQt();
					List<String> templeteTypes = new ArrayList<String>();
					templeteTypes.add(ParameterConst.TEMPLATE_TYPE_MINISTRY);
					templeteTypes.add(ParameterConst.TEMPLATE_TYPE_PTJ);

					List<Long> orgProfileIds = getMinistryPTJOrgIds(scQt
							.getPmOrgProfile());
					List<Long> ownerOrgProfileIds = getMinistryPTJOrgIds(scQt
							.getPmOrgProfile());

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
									SearchOperator.OPERATOR_IN.value(),
									orgProfileIds));

					SearchMultiFilterBean ownFilterBean = new SearchMultiFilterBean(
							new SearchFilterBean(
									"templateTypeCode.parameterCode",
									SearchOperator.OPERATOR_EQUALS.value(),
									ParameterConst.TEMPLATE_TYPE_OWN),
							SearchCondition.CONDITION_OR.value(),
							new SearchFilterBean("createdBy",
									SearchOperator.OPERATOR_EQUALS.value(),
									new FacesScopes().getUserSessionBean()
											.getUserId()));

					SearchMultiFilterBean allFilterBean = new SearchMultiFilterBean(
							new SearchFilterBean(
									"templateTypeCode.parameterCode",
									SearchOperator.OPERATOR_EQUALS.value(),
									ParameterConst.TEMPLATE_TYPE_ALL),
							SearchCondition.CONDITION_OR.value(),
							new SearchFilterBean("orgProfile.orgProfileId",
									SearchOperator.OPERATOR_IS_NULL.value(),
									null));

					searchBean.getDataFilterBeans().add(
							new SearchMultiFilterBean(
									new SearchMultiFilterBean(orgFilterBean,
											ownFilterBean), allFilterBean));

					qtTemplateReqDTO.setOrderByBlankTemplate(true);

				} else if (TemplateSpecConst.PROCESS_TYPE_SOURCING
						.equalsIgnoreCase(getManagedBean()
								.getStandardTemplate())) {
					QtnTenderSearchBean templateQtnTenderSearchBean = getManagedBean()
							.getQtnTendertemplateSearchBean();
					if (null == templateQtnTenderSearchBean.getQtNo()
							|| "".equalsIgnoreCase(templateQtnTenderSearchBean
									.getQtNo())) {
						getManagedBean().getQtnTendertemplateSearchBean()
								.setQtNo("QT");
					}
					if (null != getManagedBean().getQt()
							.getPmProcurementCategory()
							&& null != getManagedBean().getQt()
									.getPmProcurementCategory()
									.getParameterId()) {
						getManagedBean().setSearchProcurementId(
								getManagedBean().getQt()
										.getPmProcurementCategory()
										.getParameterId());
					}
					populateQTSearchCriteria(templateQtnTenderSearchBean,
							searchBean);
				}
				if (null != getManagedBean().getSpecName()
						&& !"".equalsIgnoreCase(getManagedBean().getSpecName())) {

					searchBean.getDataFilterBeans().add(
							new SearchFilterBean("specName",
									SearchOperator.OPERATOR_LIKE.value(),
									getManagedBean().getSpecName(), true));

				}

				if (null != getManagedBean().getStandardTemplate()
						&& !"".equalsIgnoreCase(getManagedBean()
								.getStandardTemplate())) {

					searchBean.getDataFilterBeans().add(
							new SearchFilterBean("processType",
									SearchOperator.OPERATOR_EQUALS.value(),
									getManagedBean().getStandardTemplate()));
				}

				if (null != getManagedBean().getSearchItemTypeId()) {
					SearchFilterBean itemSearchBean = new SearchFilterBean(
							SearchOperator.OPERATOR_EQUALS.value());
					itemSearchBean.setSearchField("itemTypeCode.parameterId");
					itemSearchBean.setSearchValue(getManagedBean()
							.getSearchItemTypeId());
					searchBean.getDataFilterBeans().add(itemSearchBean);
				}

				if (null != getManagedBean().getSearchProcurementId()) {
					SearchFilterBean procurementSearchBean = new SearchFilterBean(
							SearchOperator.OPERATOR_EQUALS.value());
					procurementSearchBean
							.setSearchField("procurementCategory.parameterId");
					procurementSearchBean.setSearchValue(getManagedBean()
							.getSearchProcurementId());
					searchBean.getDataFilterBeans().add(procurementSearchBean);
				}

				if (null != specTypeCode) {
					SearchFilterBean qtnoSearchBean = new SearchFilterBean(
							SearchOperator.OPERATOR_EQUALS.value());
					qtnoSearchBean.setSearchField("specTypeCode.parameterCode");
					qtnoSearchBean.setSearchValue(specTypeCode);

					searchBean.getDataFilterBeans().add(qtnoSearchBean);
				}

				qtTemplateReqDTO.setSearchBean(searchBean);

				qtTemplateSO = BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.COMMON_COMPONENT,
								ITemplateSpecSO.class);
				resDto = qtTemplateSO.searchTemplateMaster(qtTemplateReqDTO);

				if (resDto != null) {
					return resDto.getSearchResultBean();
				}

			} finally {

			}
			return null;
		}

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

	/**
	 * The Class DeliveryPagingDataModel.search delivery addresses paging data
	 * model.
	 */
	private class DeliveryPagingDataModel extends
			PagingDataModel<PmAddressType> {

		/** The Constant serialVersionUID. */
		private static final long serialVersionUID = 1L;

		/**
		 * Instantiates a new delivery paging data model.
		 */
		public DeliveryPagingDataModel(String componentId) {
			super(componentId);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * my.ep.web.bean.paginator.PagingDataModel#pageChangeListener(my.ep
		 * .biz.bean.search.ISearchBean)
		 */
		@Override
		public ISearchResultBean<PmAddressType> pageChangeListener(
				ISearchBean<PmAddressType> searchBean) {
			IAddressSO addressSO;
			AddressResDTO resDto;

			try {
				// SearchBean<PmAddressType> searchBean2=searchBean;
				AddressReqDTO addressReqDTO = new AddressReqDTO();
				addressReqDTO
						.setSearchAddressBean(new SearchBean<PmAddressType>());
				if (null != getManagedBean().getStateCode()
						&& !"".equalsIgnoreCase(getManagedBean().getStateCode())) {
					SearchFilterBean stateSearchBean = new SearchFilterBean(
							SearchOperator.OPERATOR_EQUALS.value());
					stateSearchBean
							.setSearchField("pmAddress.pmState.stateCode");
					stateSearchBean.setSearchValue(getManagedBean()
							.getStateCode());
					searchBean.getDataFilterBeans().add(stateSearchBean);
				}

				if (null != getManagedBean().getDistrictCode()
						&& !"".equals(getManagedBean().getDistrictCode())) {
					SearchFilterBean districtSearchBean = new SearchFilterBean(
							SearchOperator.OPERATOR_EQUALS.value());
					districtSearchBean
							.setSearchField("pmAddress.pmDistrict.districtCode");
					districtSearchBean.setSearchValue(getManagedBean()
							.getDistrictCode());
					searchBean.getDataFilterBeans().add(districtSearchBean);
				}

				if (null != getManagedBean().getDivisionCode()
						&& !"".equals(getManagedBean().getDivisionCode())) {
					SearchFilterBean districtSearchBean = new SearchFilterBean(
							SearchOperator.OPERATOR_EQUALS.value());
					districtSearchBean
							.setSearchField("pmAddress.pmDivision.divisionCode");
					districtSearchBean.setSearchValue(getManagedBean()
							.getDivisionCode());
					searchBean.getDataFilterBeans().add(districtSearchBean);
				}

				if (null != getManagedBean().getMinistryId()) {
					searchBean
							.getDataFilterBeans()
							.add(new SearchFilterBean(
									"pmOrgProfile."
											+ OrgProfileUtil
													.getSearchBeanParentString(OrganizationType.MINISTRY
															.value()),
									SearchOperator.OPERATOR_EQUALS.value(),

									getManagedBean().getMinistryId()

							));
				}

				if (null != getManagedBean().getPtjGroupId()) {
					searchBean
							.getDataFilterBeans()
							.add(new SearchFilterBean(
									"pmOrgProfile."
											+ OrgProfileUtil
													.getSearchBeanParentString(OrganizationType.DEPARTMENT
															.value()),
									SearchOperator.OPERATOR_EQUALS.value(),

									getManagedBean().getPtjGroupId()

							));
				}

				if (null != getManagedBean().getPtjId()) {
					SearchFilterBean ptjSearchBean = new SearchFilterBean(
							SearchOperator.OPERATOR_EQUALS.value());
					ptjSearchBean.setSearchField("pmOrgProfile.orgProfileId");
					ptjSearchBean.setSearchValue(getManagedBean().getPtjId());
					searchBean.getDataFilterBeans().add(ptjSearchBean);
				}

				addressReqDTO.setSearchAddressBean(searchBean);

				addressSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.PROFILE_MANAGEMENT, IAddressSO.class);
				resDto = addressSO.searchDeliveryAddress(addressReqDTO);

				if (resDto != null
						&& !resDto.getSearchAddressResultBean().getResultList()
								.isEmpty()) {

					return resDto.getSearchAddressResultBean();
				}

			} finally {

			}
			return null;
		}
	}

	/**
	 * Gets the template spec create.
	 * 
	 * @return the template spec create
	 */
	public IPagingDataModel<TplSpecMaster> getTemplateSpecCreate() {
		return templateSpecCreate;
	}

	/**
	 * Sets the template spec create.
	 * 
	 * @param templateSpecCreate
	 *            the new template spec create
	 */
	public void setTemplateSpecCreate(
			IPagingDataModel<TplSpecMaster> templateSpecCreate) {
		this.templateSpecCreate = templateSpecCreate;
	}

	/**
	 * Gets the current tab.
	 * 
	 * @return the current tab
	 */
	public int getCurrentTab() {
		return currentTab;
	}

	/**
	 * Sets the current tab.
	 * 
	 * @param currentTab
	 *            the new current tab
	 */
	public void setCurrentTab(int currentTab) {
		this.currentTab = currentTab;
	}

	/**
	 * Gets the output tab.
	 * 
	 * @return the output tab
	 */
	public int getOutputTab() {
		return outputTab;
	}

	/**
	 * Sets the output tab.
	 * 
	 * @param outputTab
	 *            the new output tab
	 */
	public void setOutputTab(int outputTab) {
		this.outputTab = outputTab;
	}

	/**
	 * Metho will be called click on task list task preRenderView(). Renders the
	 * specification committe screens or pages..
	 */
	public void preRenderView() {

		SCQuotationTenderDataType quotationTenderDataType = getBpmTaskDetails(
				SCQuotationTenderDataType.class, null);
		FacesScopes facesScopes = new FacesScopes();
		FacesContext fc = FacesContext.getCurrentInstance();

		if (SCProcessNameConst.SPECIFICATION_APPROVAL.equalsIgnoreCase(this
				.getManagedBean().getTaskName())) {
			this.getManagedBean().setReadOnly(true);

		}

		PmUser curLogedInUser = facesScopes.getUserSessionBean();
		ICommitteSO committeSO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
						ICommitteSO.class);
		CommitteReqDTO committeReqDTO = new CommitteReqDTO();
		committeReqDTO.setQtId(quotationTenderDataType.getQtId().longValue());
		committeReqDTO.setUser(curLogedInUser);
		committeReqDTO
				.setCommitteeType(ParameterConst.COMMITTEE_TYPE_SPECIFICATION_COMMITTEE);
		CommitteResDTO resDTO = committeSO
				.retrieveCommitteMemberByUserId(committeReqDTO);
		ScCommitteeMember member = resDTO.getCommMember();
		if (null != member) {

			if (member.getMemberRole().getParameterCode()
					.equalsIgnoreCase(ParameterConst.COMMITTEE_ROLE_SECRETARY)) {
				this.getManagedBean().setReadOnly(false);

			} else {

				this.getManagedBean().setReadOnly(true);
			}
		}
		retriveSpecdetails(quotationTenderDataType.getQtId().longValue());
		if (null == this.getManagedBean().getQt().getContractType()) {
			this.getManagedBean().getQt().setContractType(new PmParameter());
		}
		if (null == this.getManagedBean().getQt().getFulfilmentType()) {

			this.getManagedBean().getQt().setFulfilmentType(new PmParameter());

		}
		this.getManagedBean().setLoginRoleType("SC");
		fc.getApplication()
				.getNavigationHandler()
				.handleNavigation(fc, null,
						QTConstants.SPECIFICICATON_COMM_PREPARATION);

	}

	/**
	 * Construct spec committe dtls.prepare data for specification committee
	 * save/submit/accept/Revise.
	 * 
	 * @return SpecificationReqDTO
	 */
	private SpecificationReqDTO constructSpecCommitteDtls() {
		SpecificationReqDTO reqDTO = new SpecificationReqDTO();
		ScQt scQt = this.getManagedBean().getQt();

		if (null != this.getManagedBean().getFulfilmentType()
				&& !"".equalsIgnoreCase(this.getManagedBean()
						.getFulfilmentType())
				&& this.getManagedBean()
						.getFulfilmentType()
						.equalsIgnoreCase(
								ParameterConst.FULFILMENT_TYPE_AS_AND_WHEN)) {
			scQt.setScheduleType(ParameterConst.PERIODIC_SCHEDULE_TYPE_PROGRESSIVE_PAYMENT);
		}
		if (null != this.getManagedBean().getFulfilmentType()
				&& !"".equalsIgnoreCase(this.getManagedBean()
						.getFulfilmentType())
				&& this.getManagedBean()
						.getFulfilmentType()
						.equalsIgnoreCase(
								ParameterConst.FULFILMENT_TYPE_ONE_OFF)) {
			scQt.setScheduleType(ParameterConst.PERIODIC_SCHEDULE_TYPE_FREQUENCY_SCHEDULE);
		}

		if (null != this.getManagedBean().getFulfilmentType()
				&& !"".equalsIgnoreCase(this.getManagedBean()
						.getFulfilmentType())
				&& this.getManagedBean()
						.getFulfilmentType()
						.equalsIgnoreCase(
								ParameterConst.FULFILMENT_TYPE_SCHEDULED)) {
			if (null == scQt.getScheduleType()) {
				scQt.setScheduleType(ParameterConst.PERIODIC_SCHEDULE_TYPE_FREQUENCY_SCHEDULE);
			}
		}
		
		if (!scQt.isPanel()) {
			String qtType = scQt.getQtType();
			if (qtType.equals(ParameterConst.QUOTATION_TENDER_TYPE_OPEN) || qtType.equals(ParameterConst.QUOTATION_TENDER_TYPE_RESTRICTED_TENDER)) {
				List<ScQtCategory> qtCategories = this.getManagedBean()
						.getQtCategoryList();
				scQt.setScQtCategorys(qtCategories);
				if(qtType.equals(ParameterConst.QUOTATION_TENDER_TYPE_RESTRICTED_TENDER)){
					List<ScQtSupplier> qtSuppliers = this.getManagedBean()
							.getQtSupplierList();
					if (qtSuppliers != null && !qtSuppliers.isEmpty()) {
						for (ScQtSupplier scQtSupplier : qtSuppliers) {
							scQtSupplier
									.setInvitationType(SourcingConstants.INVITATION_TYPE_INSERT);
							//setUserDetails(scQtSupplier);
							// qtSupplierList.add(scQtSupplier);
						}
						scQt.setScQtSuppliers(qtSuppliers);
					}
				}
			} 
		}
		
		scQt = saveZonesBeforeTemplateSpec(scQt);
		List<ScQtCheckList> localChecklist = new ArrayList<ScQtCheckList>();

		List<ScQtCheckList> techCheckList = this.getManagedBean()
				.getScQtCheckSt2techProposal();

		List<ScQtCheckList> financialCheckList = this.getManagedBean()
				.getScQtCheckSt2financeProposalList();

		List<ScQtCheckList> technicalScoring = this.getManagedBean()
				.getTechnicalScoring();
		if (technicalScoring != null && !technicalScoring.isEmpty()) {

			techWeightageSum();

		}
		List<ScQtCheckList> financialScoring = this.getManagedBean()
				.getFinancialScoring();

		if (financialScoring != null && !financialScoring.isEmpty()) {
			ScQtCheckList finalFinSpec = null;
			finWeightageSum();

			for (ScQtCheckList checkList : financialScoring) {

				if (ParameterConst.DOCUMENT_DEFINITION_TYPE_TEMPLATE_SPECIFICATION
						.equalsIgnoreCase(checkList.getDefinationType()
								.getParameterCode())) {

					finalFinSpec = checkList;

					break;
				}
			}
			List<ScQtCheckList> finSpecs = null;
			if (null != finalFinSpec) {
				finSpecs = constructFinSpecsNSocre(finalFinSpec);
			}
			if (null != finSpecs && finSpecs.size() > 0) {
				// financialScoring.remove(0);

				// localChecklist.addAll(finSpecs);
				for (ScQtCheckList financialSpecs : finSpecs) {
					for (ScQtCheckList financialList : financialCheckList) {
						if (financialList.getTitle().equalsIgnoreCase(
								financialSpecs.getTitle())
								&& ParameterConst.DOCUMENT_DEFINITION_TYPE_TEMPLATE_SPECIFICATION
										.equalsIgnoreCase(financialSpecs
												.getDefinationType()
												.getParameterCode())) {
							if (null != financialSpecs.getQtScoreWeightage()
									&& financialSpecs.getQtScoreWeightage()
											.getScore().intValue() > 0) {
								financialList
										.setQtScoreWeightage(financialSpecs
												.getQtScoreWeightage());
							}
						}
					}

				}
			}

		}

		if (techCheckList != null && !techCheckList.isEmpty()) {
			boolean sample = false;
			boolean demo = false;
			for (ScQtCheckList checkList : techCheckList) {
				if (QTConstants.PRESENTATION_DEMO.equalsIgnoreCase(checkList
						.getDefinationType().getParameterCode())) {
					if(null!=scQt.getEvaluationType() && ParameterConst.EVALUATION_TYPES_TWO_TIER.equalsIgnoreCase(scQt.getEvaluationType())){
					demo = true;
					checkList.setScoringReq(true);
					}

				}
				if (ParameterConst.DOCUMENT_DEFINITION_TYPE_SAMPLE
						.equalsIgnoreCase(checkList.getDefinationType()
								.getParameterCode())) {
					sample = true;
					checkList.setScoringReq(true);

				}
				/*
				 * if (!ParameterConst.DOCUMENT_DEFINITION_TYPE_UPLOAD
				 * .equalsIgnoreCase(checkList.getDefinationType()
				 * .getParameterCode()) &&
				 * !ParameterConst.DOCUMENT_DEFINITION_TYPE_NO_ACTION
				 * .equalsIgnoreCase(checkList.getDefinationType()
				 * .getParameterCode())) { if (!checkList.isScoringReq()) {
				 * StatusReqDTO statusreq = new StatusReqDTO();
				 * statusreq.setStatusId(QTStatusConst.TS_COMPLETED);
				 * StatusResDTO statusres = statussO
				 * .retrieveStatusByStatusId(statusreq);
				 * 
				 * checkList.setPmEvalStatus(statusres.getPmStatus()); }
				 * 
				 * }
				 */
			}
			scQt.setDemoRequired(demo);
			scQt.setSampleReq(sample);
			localChecklist.addAll(techCheckList);
			scQt.setScQtChecklists(localChecklist);
		}

		if (CollectionUtils.isNotEmpty(financialCheckList)) {
			/*
			 * for (ScQtCheckList financialcheckList : financialCheckList) { if
			 * (!ParameterConst.DOCUMENT_DEFINITION_TYPE_UPLOAD
			 * .equalsIgnoreCase(financialcheckList
			 * .getDefinationType().getParameterCode()) &&
			 * !ParameterConst.DOCUMENT_DEFINITION_TYPE_NO_ACTION
			 * .equalsIgnoreCase(financialcheckList
			 * .getDefinationType().getParameterCode())) { if
			 * (!financialcheckList.isScoringReq()) { StatusReqDTO statusreq =
			 * new StatusReqDTO();
			 * statusreq.setStatusId(QTStatusConst.TS_COMPLETED); StatusResDTO
			 * statusres = statussO .retrieveStatusByStatusId(statusreq);
			 * 
			 * financialcheckList.setPmEvalStatus(statusres .getPmStatus()); }
			 * 
			 * }
			 * 
			 * }
			 */
			localChecklist.addAll(financialCheckList);
			scQt.setScQtChecklists(localChecklist);
		}
		if (ParameterConst.PERIODIC_SCHEDULE_TYPE_FREQUENCY_SCHEDULE
				.equalsIgnoreCase(scQt.getScheduleType())) {
			List<ScQtSchedule> freqschedule = this.getManagedBean()
					.getScheduleList();
			List<ScQtSchedule> localScheduleList = new ArrayList<ScQtSchedule>();
			if (freqschedule != null && !freqschedule.isEmpty()) {

				for (ScQtSchedule schedule : freqschedule) {
					String Startmonth = "";
					String endmonth = "";
					if (StringUtils.isNotBlank(schedule.getStartMonth())) {
						Startmonth = retriveMonthDtls(new Integer(
								schedule.getStartMonth()));
					}

					if (StringUtils.isNotBlank(schedule.getEndMonth())) {
						endmonth = retriveMonthDtls(new Integer(
								schedule.getEndMonth()));
					}

					Date currentDate = new Date();
					SimpleDateFormat df = new SimpleDateFormat(
							CommonConstants.DEFAULT_DATE_FORMAT);
					String localDate = df.format(currentDate);
					String day = localDate.substring(0, 2);
					String fromdate = 01 + "/" + Startmonth + "/"
							+ schedule.getStartYear();
					String toDate = 01 + "/" + endmonth + "/"
							+ schedule.getEndYear();

					schedule.setScheduleNo(schedule.getScheduleNo());

					try {
						schedule.setFromDate(df.parse(fromdate));
						schedule.setToDate(df.parse(toDate));
					} catch (ParseException e) {
						log.debug(e.getMessage());
					}

					schedule.setScQt(this.getManagedBean().getQt());
					localScheduleList.add(schedule);

				}
				scQt.setScQtSchedules(localScheduleList);
			} else {
				scQt.setScQtSchedules(null);
			}
		}

		if (ParameterConst.PERIODIC_SCHEDULE_TYPE_PROGRESSIVE_PAYMENT
				.equalsIgnoreCase(scQt.getScheduleType())) {
			List<ScQtProgressivePayment> progressivePayemnt = this
					.getManagedBean().getProgressivePaymentList();
			List<ScQtProgressivePayment> paymentList = new ArrayList<ScQtProgressivePayment>();
			if (progressivePayemnt != null && !progressivePayemnt.isEmpty()) {
				String Startmonth = "";
				String endmonth = "";
				for (ScQtProgressivePayment schedule : progressivePayemnt) {

					if (StringUtils.isNotBlank(schedule.getStartMonth())) {
						Startmonth = retriveMonthDtls(new Integer(
								schedule.getStartMonth()));
					}

					if (StringUtils.isNotBlank(schedule.getEndMonth())) {
						endmonth = retriveMonthDtls(new Integer(
								schedule.getEndMonth()));
					}
					Date d1 = new Date();
					SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
					DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
					String localDate = format.format(d1);
					String date = localDate.substring(0, 2);
					String fromdate = 01 + "/" + Startmonth + "/"
							+ schedule.getStartYear();
					String toDate = 01 + "/" + endmonth + "/"
							+ schedule.getEndYear();
					try {
						schedule.setFromDate(df.parse(fromdate));
						schedule.setToDate(df.parse(toDate));
					} catch (ParseException e) {
						log.debug(e.getMessage());
					}

					schedule.setScQt(this.getManagedBean().getQt());
					paymentList.add(schedule);

				}
				scQt.setScQtProgressivePayments(paymentList);
			} else {
				scQt.setScQtProgressivePayments(null);
			}
		}

		List<ScQtScoreSetting> scoreSetting = new ArrayList<ScQtScoreSetting>();
		ScQtScoreSetting technicalScoreSetting = this.getManagedBean()
				.getTechnicalScoreSetting();
		if (null != technicalScoreSetting
				&& technicalScoreSetting.getSettingType() == 1) {
			scoreSetting.add(technicalScoreSetting);
			scQt.setScQtScoreSettings(scoreSetting);
		}
		ScQtScoreSetting financialScoreSetting = this.getManagedBean()
				.getFinancialScoreSetting();
		if (null != financialScoreSetting
				&& financialScoreSetting.getSettingType() == 2) {
			scoreSetting.add(financialScoreSetting);
			scQt.setScQtScoreSettings(scoreSetting);
		}
		ScQtScoreSetting demoScoreSetting = this.getManagedBean()
				.getDemoScoreSetting();
		if (null != demoScoreSetting && demoScoreSetting.getSettingType() == 3) {
			scoreSetting.add(demoScoreSetting);
			scQt.setScQtScoreSettings(scoreSetting);

		}
		ScQtScoreSetting financialItemScoreSetting = this.getManagedBean()
				.getFinacialItemScoreSetting();
		if (null != financialItemScoreSetting
				&& financialItemScoreSetting.getSettingType() == 6) {
			scoreSetting.add(financialItemScoreSetting);
			scQt.setScQtScoreSettings(scoreSetting);
		}
		ScQtScoreSetting packageScoreSetting = this.getManagedBean()
				.getRecommendationPackageScore();
		if (null != packageScoreSetting
				&& packageScoreSetting.getSettingType() == 4) {
			scoreSetting.add(packageScoreSetting);
			scQt.setScQtScoreSettings(scoreSetting);
		}

		ScQtScoreSetting itemScoreSetting = this.getManagedBean()
				.getRecommendationScore();
		if (null != itemScoreSetting && itemScoreSetting.getSettingType() == 5) {
			scoreSetting.add(itemScoreSetting);
			scQt.setScQtScoreSettings(scoreSetting);
		}

		List<ScQtDeliveryLoc> localDeliveryList = new ArrayList<ScQtDeliveryLoc>();
		// List<BriefingZoneBean> deliveryList=
		// this.getManagedBean().getDeliveryLocBean();
		if (scQt.isZonal()) {

			List<BriefingZoneBean> deliveryList = this.getManagedBean()
					.getAddressZoneBean();

			if (CollectionUtils.isNotEmpty(deliveryList)) {

				for (BriefingZoneBean briefingZoneBean : deliveryList) {
					if (briefingZoneBean.getDeliveryLocList() != null
							&& !briefingZoneBean.getDeliveryLocList().isEmpty()) {

						localDeliveryList.addAll(briefingZoneBean
								.getDeliveryLocList());

					}

				}
				scQt.setScQtDeliveryLocs(localDeliveryList);
			} else if (CollectionUtils.isNotEmpty(deliveryList)) {

				deliveryList = new ArrayList<BriefingZoneBean>();
				List<BriefingZoneBean> deliveryListLocal = this
						.getManagedBean().getDeliveryLocBean();
				if (null != deliveryListLocal && !deliveryListLocal.isEmpty()) {

					for (BriefingZoneBean briefingZoneBean : deliveryListLocal) {
						if (briefingZoneBean.getDeliveryLocList() != null
								&& !briefingZoneBean.getDeliveryLocList()
										.isEmpty()) {

							localDeliveryList.addAll(briefingZoneBean
									.getDeliveryLocList());

						}

					}
					scQt.setScQtDeliveryLocs(localDeliveryList);

				} else {

					scQt.setScQtDeliveryLocs(null);
				}

			}
		} else {
			List<ScQtDeliveryLoc> nonZoneList = this.getManagedBean()
					.getDeliveryNonZoneList();
			if (CollectionUtils.isNotEmpty(nonZoneList)) {
				scQt.setScQtDeliveryLocs(nonZoneList);
			} else {
				scQt.setScQtDeliveryLocs(null);
			}
		}
		List<ScQtSpecificationRemark> remarks = super.finalizeRemarks(this
				.getManagedBean().getSpecificationRemarks());
		scQt.setScQtSpecificationRemarks(remarks);

		if (null != localChecklist && localChecklist.size() > 0) {

		} else {
			scQt.setScQtChecklists(null);
		}
		scQt.setScQtCommittees(null);
		scQt.setScQtStatus(null);
		scQt.setScQtBriefings(null);
		reqDTO.setScQt(scQt);
		List<ScQtCommitteeActivity> committeActivities = this.getManagedBean()
				.getCommitteeActivities();
		if (null != committeActivities) {
			PmUser pmUser = new FacesScopes().getUserSessionBean();
			for (int i = 0; i < committeActivities.size(); i++) {
				if (pmUser.getIdentificationNo().equals(
						committeActivities.get(i).getScCommitteeMember()
								.getPmUser().getIdentificationNo())) {
					committeActivities.get(i).setSignedOff(true);
					break;
				}

			}
		}
		reqDTO.setCommitteActivities(committeActivities);

		return reqDTO;
	}

	/**
	 * Gets the to be remove list.remove technical and financial checklists.
	 * 
	 * @return List<ScQtCheckList>
	 */
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

	/**
	 * Validate prepare qt checklist.validate specification committee menus
	 * data.
	 */
	private void validatePrepareQtChecklist() {
		try {
			IQTSpecSO specSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTSpecSO.class);
			int activeIndex = this.getManagedBean().getActiveIndex();
			SCQuotationTenderDataType quotationTenderDataType = (SCQuotationTenderDataType) this
					.getManagedBean().getPayLoad();
			SpecificationResDTO resDTO = null;
			String taskId = this.getManagedBean().getTaskId();
			String[] outComes = this.getManagedBean().getOutComes();
			Long statusId = this.getManagedBean().getCurrentStatusId();
			String taskName = this.getManagedBean().getTaskName();
			boolean readonly = this.getManagedBean().isReadOnly();
			String panelItemCategory = this.getManagedBean()
					.getPanelItemCategory();
			boolean specNewable = this.getManagedBean().isSpecNewable();
			String loginRoleType=this.getManagedBean().getLoginRoleType();
			SpecificationReqDTO specReqDTO = new SpecificationReqDTO();
			List<ScQtDeliveryLoc> deliveryRemoveList = this.getManagedBean()
					.getDeliveryLocRemoveList();
			if (null != deliveryRemoveList && !deliveryRemoveList.isEmpty()) {
				for (int i = 0; i < deliveryRemoveList.size(); i++) {
					if (null == deliveryRemoveList.get(i).getQtDeliveryLocId()) {
						deliveryRemoveList.remove(i);
					}
				}

			}

			List<ScQtCheckList> checklistRemoveList = getToBeRemoveList();
			if (null != checklistRemoveList && !checklistRemoveList.isEmpty()) {
				for (int i = 0; i < checklistRemoveList.size(); i++) {
					if (null == checklistRemoveList.get(i).getQtChecklistId()) {
						checklistRemoveList.remove(i);
					}
				}

			}
			specReqDTO.setTobeRemoveList(checklistRemoveList);
			specReqDTO.setTobeRemoveDeliveryLocs(deliveryRemoveList);
			specReqDTO.setToBeRemoveSchedulesList(this.getManagedBean()
					.getToBeRemoveSchedules());
			specReqDTO.setToBeRemoveProgressivePayments(this.getManagedBean()
					.getToBeRemoveProgPayments());
			specReqDTO.setTobeRemoveCategoryCodes(this.getManagedBean().getTobeRemoveCategoryCodes());
			specReqDTO.setToBeRemoveRestrictedSupplierList(this.getManagedBean().getToBeRemoveRestrictedSupplierList());
			boolean isAssignedState = this.getManagedBean().isAssignedState();
			switch (activeIndex) {
			case 0:
				ScQt scQt = constructSpecCommitteDtls().getScQt();
				if (this.getManagedBean().isReadOnly()) {
					if ((null != scQt.getFulfilmentType() && null == scQt
							.getFulfilmentType().getParameterId())
							|| (null == scQt.getContractDuration())) {
						throw new EpException(MessageConst.ERROR_760134);

					}
				}
				specReqDTO.setScQt(scQt);
				specSO.validateSpecCommitteGeneralInfo(specReqDTO);
				resDTO = specSO.saveQTDtl(specReqDTO);
				renewSesionObject();
				this.getManagedBean().setQt(resDTO.getScQt());
				retriveSpecdetails(resDTO.getScQt().getQtId());
				getfulfilmentDtls(resDTO.getScQt());
				break;
			case 1:
				ScQt scQtSupplier = constructSpecCommitteDtls().getScQt();

				specReqDTO.setScQt(scQtSupplier);
				// specSO.validateSupplierQulificationInfo(specReqDTO);
				resDTO = specSO.saveQTDtl(specReqDTO);
				renewSesionObject();
				this.getManagedBean().setQt(resDTO.getScQt());
				getfulfilmentDtls(resDTO.getScQt());
				retriveSpecdetails(resDTO.getScQt().getQtId());
				retriveSupplierQualificationCriteria("SC");

				break;
			case 2:

				ScQt scQtTechChecklist = constructSpecCommitteDtls().getScQt();
				specReqDTO.setScQt(scQtTechChecklist);
				specSO.validateCheckList(specReqDTO);
				resDTO = specSO.saveQTDtl(specReqDTO);

				renewSesionObject();
				this.getManagedBean().setQt(resDTO.getScQt());
				getfulfilmentDtls(resDTO.getScQt());
				retriveSpecdetails(resDTO.getScQt().getQtId());
				retriveTechnicalCheckList();
				break;
			case 3:
				ScQt finChecklist = constructSpecCommitteDtls().getScQt();
				specReqDTO.setScQt(finChecklist);
				specSO.validateCheckList(specReqDTO);
				// specReqDTO.setTobeRemoveList(getToBeRemoveList());

				resDTO = specSO.saveQTDtl(specReqDTO);
				renewSesionObject();
				this.getManagedBean().setQt(resDTO.getScQt());
				getfulfilmentDtls(resDTO.getScQt());
				retriveSpecdetails(resDTO.getScQt().getQtId());
				retriveFinancialCheckList();
				break;
			case 4:
				ScQt scoring = constructSpecCommitteDtls().getScQt();

				List<ScQtCheckList> technicalScoring = this.getManagedBean()
						.getTechnicalScoring();
				ScQtScoreSetting technicalScoreSetting = this.getManagedBean()
						.getTechnicalScoreSetting();

				if (technicalScoreSetting != null) {
					specReqDTO.setScoreSetting(technicalScoreSetting);
				}
				ScQtScoreSetting demoScoreSetting = this.getManagedBean()
						.getDemoScoreSetting();
				if (demoScoreSetting != null) {
					specReqDTO.setScoreSetting(demoScoreSetting);
				}

				specReqDTO.setCheckLists(technicalScoring);
				specReqDTO.setSpecType(ParameterConst.SPECIFICATION_TYPE_TECHNICAL);
				specReqDTO.setScQt(scoring);
				specSO.validateSCScoreSettings(specReqDTO);
				validateTechnicalPassingScoring(this.getManagedBean()
						.getTechnicalScoreSetting());
				validateTechnicalDemoPassingScoring(this.getManagedBean()
						.getDemoScoreSetting());
				validateTechnicalUserScoringAttachment(this.getManagedBean()
						.isTechnicalUserScoringSchema());
				technicalWeightageCalculate();
				resDTO = specSO.saveQTDtl(specReqDTO);
				renewSesionObject();
				this.getManagedBean().setQt(resDTO.getScQt());
				getfulfilmentDtls(resDTO.getScQt());
				retriveSpecdetails(resDTO.getScQt().getQtId());
				retriveTechnicalScoring();
				break;

			case 5:
				ScQt recommendation = constructSpecCommitteDtls().getScQt();

				List<ScQtCheckList> financialScoring1 = this.getManagedBean()
						.getFinancialScoring();
				ScQtScoreSetting financialScoreSetting = this.getManagedBean()
						.getFinancialScoreSetting();

				if (null != financialScoreSetting
						&& financialScoreSetting.getSettingType() == 2) {
					specReqDTO.setScoreSetting(financialScoreSetting);
				}

				specReqDTO.setCheckLists(financialScoring1);
				specReqDTO.setSpecType(ParameterConst.SPECIFICATION_TYPE_FINANCIAL);
				specReqDTO.setScQt(recommendation);
				validateFinancialPackageItemPassingScoring(this
						.getManagedBean().getFinancialScoreSetting(), this
						.getManagedBean().getFinacialItemScoreSetting());
				specSO.validateSCScoreSettings(specReqDTO);
				specSO.validateSpecOfferPrice(specReqDTO);
				financialWeightageCalculate();
				financialItemScoreCalculate(this.getManagedBean()
						.getFinacialItemScoreSetting());
				validateFinancialUserScoringAttachment(this.getManagedBean()
						.isFinancialUserScoringSchema());
				resDTO = specSO.saveQTDtl(specReqDTO);
				renewSesionObject();
				this.getManagedBean().setQt(resDTO.getScQt());
				getfulfilmentDtls(resDTO.getScQt());
				retriveSpecdetails(resDTO.getScQt().getQtId());
				retriveFinancialScoring();
				break;

			case 6:
				ScQt delivery = constructSpecCommitteDtls().getScQt();
				List<ScQtScoreSetting> scoreSetting = new ArrayList<ScQtScoreSetting>();
				ScQtScoreSetting packageScoreSetting = this.getManagedBean()
						.getRecommendationPackageScore();
				if (null != packageScoreSetting
						&& packageScoreSetting.getSettingType() == 4) {
					scoreSetting.add(packageScoreSetting);

				}

				ScQtScoreSetting itemScoreSetting = this.getManagedBean()
						.getRecommendationScore();
				if (null != itemScoreSetting
						&& itemScoreSetting.getSettingType() == 5) {
					scoreSetting.add(itemScoreSetting);

				}

				specReqDTO.setScoreSettings(scoreSetting);
				specReqDTO.setScQt(delivery);
				if (!this.getManagedBean().isReadOnly()) {
					calculateRecommendationPackageAndItemScores(
							packageScoreSetting, itemScoreSetting);
					specSO.validateSCRecommandationSettings(specReqDTO);

				}

				resDTO = specSO.saveQTDtl(specReqDTO);
				renewSesionObject();
				this.getManagedBean().setQt(resDTO.getScQt());
				getfulfilmentDtls(resDTO.getScQt());
				retriveSpecdetails(resDTO.getScQt().getQtId());
				retriveRecommendationDetails();
				break;

			case 7:
				ScQt fulfilMent = constructSpecCommitteDtls().getScQt();
				if (fulfilMent.isZonal()) {
					if (null == fulfilMent.getScQtDeliveryLocs()) {
						List<BriefingZoneBean> zoneBean = this.getManagedBean()
								.getDeliveryLocBean();
						if (null != zoneBean) {
							for (BriefingZoneBean deliverybean : zoneBean) {
								if (null != deliverybean.getDeliveryLocList()) {
									fulfilMent.setScQtDeliveryLocs(deliverybean
											.getDeliveryLocList());
								}
							}
						}
					}
				}
				specReqDTO.setScQt(fulfilMent);
				specSO.validateSCdelieryLocs(specReqDTO);
				resDTO = specSO.saveQTDtl(specReqDTO);
				renewSesionObject();
				this.getManagedBean().setQt(resDTO.getScQt());
				getfulfilmentDtls(resDTO.getScQt());
				retriveSpecdetails(resDTO.getScQt().getQtId());
				retriveDeliveryDetails();
				break;

			case 8:
				ScQt fulfilMentdtls = constructSpecCommitteDtls().getScQt();
				if (ParameterConst.FULFILMENT_TYPE_SCHEDULED
						.equalsIgnoreCase(this.getManagedBean()
								.getFulfilmentType())
						|| ParameterConst.FULFILMENT_TYPE_AS_AND_WHEN
								.equalsIgnoreCase(this.getManagedBean()
										.getFulfilmentType())) {
					specReqDTO.setScQt(fulfilMentdtls);
					specSO.validateSCFulfillmentSchedules(specReqDTO);
					resDTO = specSO.saveQTDtl(specReqDTO);
					renewSesionObject();
					this.getManagedBean().setQt(resDTO.getScQt());
					getfulfilmentDtls(resDTO.getScQt());
					retriveSpecdetails(resDTO.getScQt().getQtId());
					retriveFulfilmentDtls();
					// retriveSupplierQualificationCriteria("SC");
					// retriveCommitteeEndorsment();
					// retriveTechnicalCheckList();
				} else {
					specReqDTO.setScQt(fulfilMentdtls);
					specSO.validateSCFulfillmentSchedules(specReqDTO);
					resDTO = specSO.saveQTDtl(specReqDTO);
					renewSesionObject();
					this.getManagedBean().setQt(resDTO.getScQt());
					getfulfilmentDtls(resDTO.getScQt());
					retriveSpecdetails(resDTO.getScQt().getQtId());
					// retriveFulfilmentDtls();
					retriveCommitteeEndorsment();
				}
				break;

			case 9:

				ScQt endorsment = constructSpecCommitteDtls().getScQt();
				specReqDTO.setScQt(endorsment);
				resDTO = specSO.saveQTDtl(specReqDTO);
				renewSesionObject();
				this.getManagedBean().setQt(resDTO.getScQt());
				getfulfilmentDtls(resDTO.getScQt());
				retriveSpecdetails(resDTO.getScQt().getQtId());
				retriveCommitteeEndorsment();
				// retriveTechnicalCheckList();
				break;

			}

			if (getManagedBean().getTempIndex() < activeIndex) {
				getManagedBean().setTempIndex(activeIndex);
			}
			this.getManagedBean().setPanelItemCategory(panelItemCategory);
			this.getManagedBean().setSpecNewable(specNewable);
			this.getManagedBean().setTaskName(taskName);
			this.getManagedBean().setActiveIndex(activeIndex);
			this.getManagedBean().setTaskId(taskId);
			this.getManagedBean().setCurrentStatusId(statusId);
			this.getManagedBean().setOutComes(outComes);
			this.getManagedBean().setReadOnly(readonly);
			this.getManagedBean().setPayLoad(quotationTenderDataType);
			this.getManagedBean().setAssignedState(isAssignedState);
			this.getManagedBean().setLoginRoleType(loginRoleType);
		} /*
		 * catch (Exception e) { e.printStackTrace(); }
		 */

		finally {

		}
	}

	/**
	 * Spec committe bpm task.Event handle Submit/Revise/Accept in specification
	 * committee.
	 * 
	 * @param action
	 *            (String action)
	 * @return navigate to acknowledgement page.
	 */
	public String specCommitteBpmTask(String action) {
		FacesScopes facesScopes = new FacesScopes();

		PmUser curLogedInUser = facesScopes.getUserSessionBean();
		ICommitteSO committeSO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
						ICommitteSO.class);
		CommitteReqDTO committeReqDTO = new CommitteReqDTO();
		committeReqDTO.setQtId(this.getManagedBean().getQt().getQtId());
		committeReqDTO.setUser(curLogedInUser);
		committeReqDTO
				.setCommitteeType(ParameterConst.COMMITTEE_TYPE_SPECIFICATION_COMMITTEE);
		CommitteResDTO CommitteeresDTO = committeSO
				.retrieveCommitteMemberByUserId(committeReqDTO);
		ScCommitteeMember member = CommitteeresDTO.getCommMember();
		if (member != null
				&& member
						.getMemberRole()
						.getParameterCode()
						.equalsIgnoreCase(
								ParameterConst.COMMITTEE_ROLE_CHAIRPERSON)) {
			this.getManagedBean().setLocalOutCome(action);
			RequestContext.getCurrentInstance().execute("gpki_dlg.show()");

		} else {
			this.getManagedBean().setLocalOutCome(action);

			if (BpmOutcomeConst.BPM_REVISE.equalsIgnoreCase(action)) {
				retriveCommitteeEndorsment();
				List<ScQtSpecificationRemark> remarks = this.getManagedBean()
						.getSpecificationRemarks();
				if (null != remarks) {
					for (int i = 0; i < remarks.size(); i++) {
						ScQtSpecificationRemark Remark = remarks.get(0);
						String userRemark = remarks.get(i).getRemark();
						if (null == userRemark || "".equals(userRemark)) {
							throw new EpException(MessageConst.ERROR_760154);
						} else if (null == Remark.getRemarkId()) {
							RequestContext.getCurrentInstance().execute(
									"reviceApprovalDlgs.show()");
						} else {
							reviseSpecificationCommittee();
							return CommonUIConst.ACKNOWLEDGE_PAGE;
						}
					}

				}

			} else if (BpmOutcomeConst.BPM_SUBMIT.equalsIgnoreCase(action)
					|| action.equalsIgnoreCase(BpmOutcomeConst.BPM_ACCEPT)
					|| action.equalsIgnoreCase(BpmOutcomeConst.BPM_APPROVE)|| action.equalsIgnoreCase(BpmOutcomeConst.BPM_ENDORSE)) {
				if (action.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT)) {
					if (action.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT)
							&& !this.getManagedBean().isReadOnly()) {
						boolean technical = validateTechnicalStandardCheckListSubmit();

						if (!technical
								&& !this.getManagedBean()
										.isTechnicalOtherChecklistSubmit()) {
							RequestContext
									.getCurrentInstance()
									.execute(
											"technicalStandardChecklistDlgsSubmit.show()");
							return null;
						}
						boolean financial = validateFinancialStandardCheckListSubmit();
						if (!financial
								&& !this.getManagedBean()
										.isFinancialOtherChecklistSubmit()) {
							RequestContext
									.getCurrentInstance()
									.execute(
											"financialStandardChecklistDlgsSubmit.show()");
							return null;
						}

					}
					SpecificationResDTO resDTO = updateSpecCommitteDtls(action,
							null);
					if (resDTO.isSuccessful()) {
						ScQt qt = resDTO.getScQt();
						FacesContext
								.getCurrentInstance()
								.addMessage(
										null,
										new FacesMessage(
												FacesMessage.SEVERITY_INFO,
												null,
												MessageUtils
														.getMessage(
																getMessgeConstant(action),
																new String[] {
																		getMessageCommon("lbl.sc.specification.preparation.and.scoring.for"),
																		qt.getQtNo() },
																MessageUtils.BUNDLE_COMMON_MESSAGES,
																new FacesScopes()
																		.getUserLocale())));

						return CommonUIConst.ACKNOWLEDGE_PAGE;
					}
				} else if (action.equalsIgnoreCase(BpmOutcomeConst.BPM_ACCEPT)|| action.equalsIgnoreCase(BpmOutcomeConst.BPM_ENDORSE)) {
					SpecificationResDTO resDTO = updateSpecCommitteDtls(action,
							null);
					if (resDTO.isSuccessful()) {
						ScQt qt = resDTO.getScQt();
						FacesContext
								.getCurrentInstance()
								.addMessage(
										null,
										new FacesMessage(
												FacesMessage.SEVERITY_INFO,
												null,
												MessageUtils
														.getMessage(
																getMessgeConstant(action),
																new String[] {
																		getMessageCommon("lbl.sc.specification.preparation.and.scoring.for"),
																		qt.getQtNo() },
																MessageUtils.BUNDLE_COMMON_MESSAGES,
																new FacesScopes()
																		.getUserLocale())));

						return CommonUIConst.ACKNOWLEDGE_PAGE;
					}
				} else if (action.equalsIgnoreCase(BpmOutcomeConst.BPM_APPROVE)) {
					SpecificationResDTO resDTO = updateSpecCommitteDtls(action,
							null);
					if (resDTO.isSuccessful()) {
						ScQt qt = resDTO.getScQt();
						FacesContext
								.getCurrentInstance()
								.addMessage(
										null,
										new FacesMessage(
												FacesMessage.SEVERITY_INFO,
												null,
												MessageUtils
														.getMessage(
																getMessgeConstant(action),
																new String[] {
																		getMessageCommon("lbl.sc.specification.preparation.and.scoring.for"),
																		qt.getQtNo() },
																MessageUtils.BUNDLE_COMMON_MESSAGES,
																new FacesScopes()
																		.getUserLocale())));

						return CommonUIConst.ACKNOWLEDGE_PAGE;
					}
				}
			}
		}
		return null;

	}

	/**
	 * Select zone tree node.Event handle check non panel Treenodes.
	 * 
	 * @param event
	 *            (TreeNode treeNode)
	 */
	public void selectZoneTreeNode(NodeSelectEvent event) {
		TreeNode selectedTreeNode = event.getTreeNode();

		selectedTreeNode.setSelected(true);
	}

	/**
	 * Un select zone tree node.Event handle uncheck Nonpanel treeNodes.
	 * 
	 * @param event
	 *            (TreeNode treeNode).
	 */
	public void unSelectZoneTreeNode(NodeUnselectEvent event) {
		TreeNode selectedTreeNode = event.getTreeNode();

		selectedTreeNode.setSelected(false);
	}

	/**
	 * Delete panel tree node.Event handle delete zones from panel supplier
	 * qualification criteria.
	 */
	public void deletePanelTreeNode() {

		TreeNode selectedTreeNode = this.getManagedBean()
				.getDeletePanelTreenode();

		if (null != selectedTreeNode) {
			List<TreeNode> zoneGeneralTrees = new ArrayList<TreeNode>();
			if (null != getManagedBean().getZoneGeneralTrees()) {
				if (getManagedBean().getZoneGeneralTrees().size() > 1) {
					for (TreeNode treenode : this.getManagedBean()
							.getZoneGeneralTrees()) {
						for (TreeNode node : treenode.getChildren()) {

							if (!node.equals(selectedTreeNode)) {
								zoneGeneralTrees.add(treenode);
							}
						}
						this.getManagedBean().setZoneGeneralTrees(
								zoneGeneralTrees);
					}
				} else {

					throw new EpException(MessageConst.ERROR_760245);

				}
			}

		} else {
			throw new EpException(MessageConst.ERROR_760146);
		}
	}

	/**
	 * Delete tree node.Event handle delete NonPanel Zones from General
	 * information.
	 */
	public void deleteTreeNode() {
		getManagedBean().setSelectedFlag(true);
		List<TreeNode> rootTrees = this.getManagedBean().getZoneGeneralTrees();

		// LocationSingleton.getInstance().deleteTreeNodes(rootTrees);
		deleteZonesTreeNodes(rootTrees);
		TplSpecZoneDtl zoneDtl = null;
		// zone details
		if (getManagedBean().isSelectedFlag()) {
			throw new EpException(MessageConst.ERROR_760146);
		}
		if (!rootTrees.isEmpty()) {
			getManagedBean()
					.getQt()
					.getScQtSupplierCriteria()
					.getQtZoneCriterias()
					.setTplSpecZoneDtls(
							convertTreeNodesToZoneCriteria(rootTrees, zoneDtl));
		}
		ScQtSupplierCriteria zoneSupplierCriteria = getManagedBean().getQt()
				.getScQtSupplierCriteria();
		if (null != zoneSupplierCriteria
				&& zoneSupplierCriteria.isBrandNew() == false) {
			updateFinacialSpecStatus();
		}
		this.getManagedBean().setIndex(-1);

	}

	/**
	 * Delete zones tree nodes.
	 * 
	 * @param rootTrees
	 *            the root trees
	 * @return the list
	 */
	private List<TreeNode> deleteZonesTreeNodes(List<TreeNode> rootTrees) {
		TplSpecZoneDtl tplSpecZoneDtl = null;
		List<TreeNode> removedList = new ArrayList<TreeNode>();
		if (rootTrees != null) {
			if (null != this.getManagedBean().getQt().getScQtSupplierCriteria()
					&& null != this.getManagedBean().getQt()
							.getScQtSupplierCriteria().getQtZoneCriterias()) {
				this.getManagedBean().getQt().getScQtSupplierCriteria()
						.getQtZoneCriterias().setModified(true);
			}
			for (int row = 0; row < rootTrees.size(); row++) {
				TreeNode rootTree = rootTrees.get(row);

				for (TreeNode zoneTree : rootTree.getChildren()) {
					if (zoneTree.isSelected()) {
						getManagedBean().setSelectedFlag(false);
						tplSpecZoneDtl = (TplSpecZoneDtl) zoneTree.getData();
						if (tplSpecZoneDtl.getPanelZone() != null) {
							// deletePanelItemSuppliers(tplSpecZoneDtl);

						} else {
							deleteStateOrDivOrDist(tplSpecZoneDtl, null);
						}

						rootTrees.remove(row--);
					} else {

						removedList = deleteZoneChildTreeNodes(zoneTree
								.getChildren());

						if (removedList != null) {
							for (TreeNode treeNode : removedList) {
								List<TplSpecZoneLoc> locationDTOs = getZoneLocations(treeNode);

								deleteStateOrDivOrDist(tplSpecZoneDtl,
										locationDTOs);
							}
						}

						if (zoneTree.getChildren().size() == 0) {
							rootTrees.remove(row--);
						}
					}

				}
			}
		}
		return removedList;
	}

	/**
	 * Delete zone child tree nodes.
	 * 
	 * @param treeNodes
	 *            the tree nodes
	 * @return the list
	 */
	private List<TreeNode> deleteZoneChildTreeNodes(List<TreeNode> treeNodes) {
		List<TreeNode> nodesToRemoved = new ArrayList<TreeNode>();
		if (treeNodes != null) {

			for (TreeNode treeNode : treeNodes) {
				if (treeNode.isSelected()) {
					getManagedBean().setSelectedFlag(false);
					nodesToRemoved.add(treeNode);
				} else {
					if (treeNode.getChildren() != null
							&& treeNode.getChildren().size() > 0) {
						deleteChildTreeNodes(treeNode.getChildren());

						if (treeNode.getChildren().size() == 0) {
							nodesToRemoved.add(treeNode);
						}
					}
				}
			}

			if (nodesToRemoved.size() > 0) {
				treeNodes.removeAll(nodesToRemoved);
			}
		}
		return nodesToRemoved;
	}

	/**
	 * Select location tree node.Event handle check treeNode Locations in
	 * Supplier qualification criteria .
	 * 
	 * @param event
	 *            (TreeNode treeNode).
	 */
	public void selectLocationTreeNode(NodeSelectEvent event) {
		TreeNode selectedTreeNode = event.getTreeNode();

		selectedTreeNode.setSelected(true);
	}

	/**
	 * Un select location tree node.Event handle Uncheck treeNode Locations in
	 * Supplier qualification criteria .
	 * 
	 * @param event
	 *            (TreeNode treeNode).
	 */
	public void unSelectLocationTreeNode(NodeUnselectEvent event) {
		TreeNode selectedTreeNode = event.getTreeNode();

		selectedTreeNode.setSelected(false);
	}

	/**
	 * Delete supplier qualification tree node.Event handle delete Locations
	 * from supplier qualification criteria.
	 * 
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public void deleteSupplierQualificationTreeNode() {
		try {
			List<TreeNode> rootTrees = getManagedBean().getZoneTrees();
			deleteTreeNodes(rootTrees);
			this.getManagedBean().setTotalSuppliersSpecLoc(
					new ArrayList<TplSpecLoc>());
			List<ScQtLocCriteria> CriteriaNew = new ArrayList<ScQtLocCriteria>();
			if (!rootTrees.isEmpty()) {

				// this.getManagedBean().setZoneTrees(new
				// ArrayList<TreeNode>());
				List<TplSpecLoc> localLoc = new ArrayList<TplSpecLoc>();
				List<ScQtLocCriteria> Criteria = new ArrayList<ScQtLocCriteria>();

				Criteria = convertTreeNodesToLocationCriteria(rootTrees, this
						.getManagedBean().getQt().getScQtSupplierCriteria());

				for (ScQtLocCriteria locCriteria : Criteria) {
					// Criteria.add(locCriteria);
					ScQtLocCriteria criteriaNew = (ScQtLocCriteria) BeanUtils
							.cloneBean(locCriteria);
					CriteriaNew.add(criteriaNew);
					TplSpecLoc specLoc = (TplSpecLoc) locCriteria;

					localLoc.add(specLoc);

				}
				/*
				 * getManagedBean().setZoneTrees(new ArrayList<TreeNode>());
				 * getManagedBean().getZoneTrees().addAll(
				 * LocationSingleton.getInstance().constuctTreeNode(
				 * CriteriaNew));
				 */

				// this.getManagedBean().setExistingSupplierLocationList(Criteria);
				this.getManagedBean().setTotalSuppliersSpecLoc(localLoc);
			}
			checkExistingLocation(CriteriaNew, this.getManagedBean()
					.getExistingSupplierLocationList());
			this.getManagedBean().setIndex(-1);
		} catch (IllegalAccessException e) {
			log.error(e);
		} catch (InstantiationException e) {
			log.error(e);
		} catch (InvocationTargetException e) {
			log.error(e);
		} catch (NoSuchMethodException e) {
			log.error(e);
		}
	}

	private void checkExistingLocation(
			List<ScQtLocCriteria> currentLocationList,
			List<ScQtLocCriteria> existingLocations) {
		List<ScQtLocCriteria> localDeleteList = this.getManagedBean()
				.getSupplierLocationList();
		if (null == localDeleteList) {
			localDeleteList = new ArrayList<ScQtLocCriteria>();
		}
		if (null != existingLocations && !existingLocations.isEmpty()
				&& null != currentLocationList
				&& !currentLocationList.isEmpty()) {
			for (ScQtLocCriteria locDeleteCriteria : existingLocations) {
				boolean delectLoc = false;
				for (ScQtLocCriteria locCurrentList : currentLocationList) {
					if (null != locCurrentList.getSpecLocId()) {
						if (locCurrentList.getSpecLocId().equals(
								locDeleteCriteria.getSpecLocId())) {
							delectLoc = true;
							break;
						}
					} else {
						delectLoc = true;
					}
				}
				if (!delectLoc) {
					if (!localDeleteList.contains(locDeleteCriteria)) {
						localDeleteList.add(locDeleteCriteria);
					}
				}
			}

			this.getManagedBean().setSupplierLocationList(localDeleteList);

		}
		if (null == currentLocationList || currentLocationList.isEmpty()) {
			if (null != existingLocations && !existingLocations.isEmpty()) {
				this.getManagedBean()
						.setSupplierLocationList(existingLocations);
			}
		}
	}

	/**
	 * Delete tree nodes.
	 * 
	 * @param rootTrees
	 *            the root trees
	 */
	private void deleteTreeNodes(List<TreeNode> rootTrees) {
		if (rootTrees != null) {
			for (int row = 0; row < rootTrees.size(); row++) {
				TreeNode rootTree = rootTrees.get(row);

				for (TreeNode zoneTree : rootTree.getChildren()) {
					if (zoneTree.isSelected()) {
						/*
						 * ScQtLocCriteria loccriteria = (ScQtLocCriteria)
						 * zoneTree .getData();
						 */
						rootTrees.remove(row--);
						// deleteSupplierLocacriteria(loccriteria);
					} else {
						deleteChildTreeNodes(zoneTree.getChildren());

						if (zoneTree.getChildren().size() == 0) {
							// rootTrees.remove(row--);
						}
					}
				}
			}
		}
	}

	/**
	 * Delete child tree nodes.
	 * 
	 * @param treeNodes
	 *            the tree nodes
	 */
	private void deleteChildTreeNodes(List<TreeNode> treeNodes) {
		if (treeNodes != null) {
			List<TreeNode> nodesToRemoved = new ArrayList<TreeNode>();

			for (TreeNode treeNode : treeNodes) {
				if (treeNode.isSelected()) {

					nodesToRemoved.add(treeNode);

					// deleteSupplierLocacriteria(loccriteria);
				} else {
					if (treeNode.getChildren() != null
							&& treeNode.getChildren().size() > 0) {

						deleteChildTreeNodes(treeNode.getChildren());
						// deleteSupplierLocacriteria(loccriteria);
						if (treeNode.getChildren().size() == 0) {
							nodesToRemoved.add(treeNode);
						}
					}
				}
			}

			if (nodesToRemoved.size() > 0) {
				treeNodes.removeAll(nodesToRemoved);
			}
		}
	}

	/**
	 * Prepare spec chir person request.
	 * 
	 * @param action
	 *            the action
	 * @param pmDigiSign
	 *            the pm digi sign
	 * @return the specification res dto
	 */
	private SpecificationResDTO prepareSpecChairPersonRequest(String action,
			PmDigiSign pmDigiSign) {
		SpecificationReqDTO specReqDTO = new SpecificationReqDTO();
		SpecificationResDTO resDTO = new SpecificationResDTO();
		List<ScQtRemark> remarks = this.getManagedBean().getScQtRemarksList();
		if (null != remarks) {
			for (int i = 0; i < remarks.size(); i++) {
				String userRemark = remarks.get(i).getRemark();
				if (null == userRemark || "".equals(userRemark)) {
					remarks.remove(i);
				}
			}
		}
		ScQt scQt = this.getManagedBean().getQt();
		if (null != remarks) {
			scQt.setScQtRemarks(remarks);
		}
		scQt.setScQtCommittees(null);
		scQt.setScQtStatus(null);
		scQt.setScQtBriefings(null);
		specReqDTO.setScQt(scQt);

		specReqDTO.setOutcome(action);
		if (null != pmDigiSign) {
			updatePmDigiSignData(pmDigiSign, ParameterConst.DOCTYPE_QUOTATION,
					scQt.getQtId(), scQt.getQtNo());
		}
		specReqDTO.setDigiSignData(pmDigiSign);
		specReqDTO.setTaskId(this.getManagedBean().getTaskId());
		specReqDTO.setPayLoad(this.getManagedBean().getPayLoad());
		FacesScopes facesScopes = new FacesScopes();
		specReqDTO.setPmUser(facesScopes.getUserSessionBean());
		specReqDTO.setRoleCode(getUserRoleCode());
		specReqDTO.setPrepareSpecStatus(this.getManagedBean()
				.getCurrentStatusId());
		specReqDTO.setToken(facesScopes.getBPMAuthenticationCtxToken());
		IQTSpecSO specSO = getSpecSO();

		try {
			resDTO = specSO.updateBPMAction(specReqDTO);

		} finally {
			specSO = null;
		}

		return resDTO;
	}

	/**
	 * Gets the role type dropdown values.display item type(product/Service)in
	 * template spec dropdown.
	 * 
	 * @param itemType
	 *            (String itemType)
	 * @return SelectItem[] .
	 */
	public SelectItem[] getRoleTypeDropdownValues(String itemType) {

		ISearchBean<PmParameterDesc> searchBean = new SearchBean<PmParameterDesc>();
		List<String> recommendList = new ArrayList<String>();
		recommendList.add(ParameterConst.ITEM_TYPE_PRODUCT_AND_SERVICE);
		/*
		 * if (this.getManagedBean().getQt().isPanel()) {
		 * recommendList.add(ParameterConst.ITEM_TYPE_SERVICE); }
		 */

		searchBean.getDataFilterBeans().add(
				new SearchFilterBean("pmParameter.parameterCode",
						SearchOperator.OPERATOR_NOT_IN.value(), recommendList));

		SelectItem[] items = getDropdownValuesBySearchBean(
				ParameterTypeConst.SC_ITEM_TYPE, searchBean);
		return items;
	}

	/**
	 * Gets the quotation tendervalues.display quotation/Tender
	 * Type(open/Restricted) radio values in business owner.
	 * 
	 * @param quotationTenderType
	 *            ((String quotationTenderType)
	 * @return SelectItem[].
	 */
	public SelectItem[] getQuotationTendervalues(String quotationTenderType) {
		ISearchBean<PmParameterDesc> searchBean = new SearchBean<PmParameterDesc>();
		ScQt scQt = getManagedBean().getQt();

		if (null != scQt
				&& ParameterConst.PROCUREMENT_MODE_QUOTATION.equals(scQt
						.getProcurementMode().getParameterCode())) {
			List<String> qtTypes = new ArrayList<String>();
			qtTypes.add(ParameterConst.QUOTATION_TENDER_TYPE_RESTRICTED_TENDER);
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("pmParameter.parameterCode",
							SearchOperator.OPERATOR_NOT_IN.value(), qtTypes));
		}

		return super.getRadioButtonCodeValuesBySearchBean(quotationTenderType,
				searchBean);
	}

	/**
	 * Gets the local goods radio values.display Local goods (Local/Import)
	 * radio values in template spec.
	 * 
	 * @param itemType
	 *            (String itemType)
	 * @return SelectItem[].
	 */
	public SelectItem[] getLocalGoodsRadioValues(String itemType) {

		List<String> types = new ArrayList<String>();
		types.add(ParameterConst.LOCAL_GOODS);

		if (!this.getManagedBean().getQt().isPanel()) {
			types.add(ParameterConst.IMPORTED_GOODS);
		}

		SelectItem[] items = null;
		try {
			items = new SelectItem[types.size()];
			int i = 0;
			for (String location : types) {
				List<PmParameterDesc> localParamDesc = retrieveParameterDescByType(itemType);

				for (PmParameterDesc paramDesc : localParamDesc) {
					if (paramDesc.getPmParameter().getParameterCode()
							.equals(location)) {

						items[i++] = new SelectItem(paramDesc.getPmParameter()
								.getParameterId(), paramDesc.getCodeName());

					}
				}
			}
		} finally {
		}
		return items;
	}

	/**
	 * Gets the text before sign.
	 * 
	 * @return the text before sign
	 */
	public String getTextBeforeSign() {
		return textBeforeSign;
	}

	/**
	 * Sets the text before sign.Event handle set gpki data.
	 * 
	 * @param textBeforeSign
	 *            (String textBeforeSign).
	 */
	public void setTextBeforeSign(String textBeforeSign) {
		// Temp : QtNumber + userId
		StringBuilder sb = new StringBuilder();
		sb.append(getManagedBean().getQt().getQtNo());
		sb.append(new FacesScopes().getUserSessionBean().getUserId());
		this.textBeforeSign = sb.toString();
	}

	/**
	 * Update spec committe dtls.
	 * 
	 * @param action
	 *            the action
	 * @param digiSign
	 *            the digi sign
	 * @return the specification res dto
	 */
	public SpecificationResDTO updateSpecCommitteDtls(String action,
			PmDigiSign digiSign) {
		ScQt scQt = this.getManagedBean().getQt();
		if (action.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT)
				|| action.equalsIgnoreCase(BpmOutcomeConst.BPM_ACCEPT)
				|| action.equalsIgnoreCase(BpmOutcomeConst.BPM_REVISE)|| action.equalsIgnoreCase(BpmOutcomeConst.BPM_ENDORSE) || action.equalsIgnoreCase(BpmOutcomeConst.BPM_SIGNOFF)) {
			
			loadPrepareSpecCommitteeDetails(scQt.getQtId());
			
			// Update Procurement Mode based on final estimated price.
			scQt = getManagedBean().getQt();
			Long statusId = scQt.getQtStatus().getStatusId();
			if (action.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT) && 
					(QTStatusConst.QT_PENDING_APPROVALS_CHECKLIST_SCORING.equals(statusId) ||
							QTStatusConst.QT_PENDING_CHECKLIST_SCORING_REVISION.equals(statusId))) {
				if(null==scQt.getEstimatedAmount()){
					SpecificationReqDTO estimatedReqDTo=new SpecificationReqDTO();
					estimatedReqDTo.setQtId(scQt.getQtId());
					IQTSpecSO specSO = getSpecSO();
					SpecificationResDTO resTO=specSO.calCulateTemplateSpecEstimations(estimatedReqDTo);
					if(null!=resTO && null!=resTO.getTotalSpecEstimatedAmount()){
						scQt.setEstimatedAmount(resTO.getTotalSpecEstimatedAmount());
					}
				}
				new CreateQtBackingBean().setProcurementMode(scQt, scQt.getEstimatedAmount());
			}

		}
		SpecificationReqDTO spReqDTO = constructSpecCommitteDtls();
		List<ScQtCheckList> checklistRemoveList = getToBeRemoveList();
		if (null != checklistRemoveList && !checklistRemoveList.isEmpty()) {
			for (int i = 0; i < checklistRemoveList.size(); i++) {
				if (null == checklistRemoveList.get(i).getQtChecklistId()) {
					checklistRemoveList.remove(i);
				}
			}

		}
		List<ScQtDeliveryLoc> deliveryRemoveList = this.getManagedBean()
				.getDeliveryLocRemoveList();
		if (null != deliveryRemoveList && !deliveryRemoveList.isEmpty()) {
			for (int i = 0; i < deliveryRemoveList.size(); i++) {
				if (null == deliveryRemoveList.get(i).getQtDeliveryLocId()) {
					deliveryRemoveList.remove(i);
				}
			}

		}
		/*
		 * if (action.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT) &&
		 * !this.getManagedBean().isReadOnly()) {
		 * validateTechnicalStandardCheckListSubmit(); //
		 * validateFinancialStandardCheckListSubmit(); }
		 */

		spReqDTO.setTobeRemoveDeliveryLocs(deliveryRemoveList);
		spReqDTO.setTobeRemoveList(checklistRemoveList);
		spReqDTO.setTobeRemoveCategoryCodes(this.getManagedBean().getTobeRemoveCategoryCodes());
		spReqDTO.setToBeRemoveSchedulesList(this.getManagedBean()
				.getToBeRemoveSchedules());
		spReqDTO.setToBeRemoveProgressivePayments(this.getManagedBean()
				.getToBeRemoveProgPayments());
		spReqDTO.setToBeRemoveRestrictedSupplierList(this.getManagedBean().getToBeRemoveRestrictedSupplierList());
		spReqDTO.setOutcome(action);

		if (null != digiSign) {
			updatePmDigiSignData(digiSign,
					ParameterConst.DOCTYPE_SPEC_COMMITTE, scQt.getQtId(),
					scQt.getQtNo());
		}

		if (action.equalsIgnoreCase(BpmOutcomeConst.BPM_REVISE)) {

			List<ScQtSpecificationRemark> remarks = super
					.finalizeRemarks(getManagedBean().getSpecificationRemarks());

			if (null != remarks) {
				ScQt localScQt = spReqDTO.getScQt();
				localScQt.setScQtSpecificationRemarks(remarks);
				spReqDTO.setScQt(localScQt);
			}
			List<ScQtCommitteeActivity> committeActivities = this
					.getManagedBean().getCommitteeActivities();
			if (null != committeActivities) {

				for (int i = 0; i < committeActivities.size(); i++) {

					committeActivities.get(i).setSignedOff(false);

				}
			}
			spReqDTO.setCommitteActivities(committeActivities);
		}
		spReqDTO.setDigiSignData(digiSign);
		spReqDTO.setTaskId(this.getManagedBean().getTaskId());
		spReqDTO.setPayLoad(this.getManagedBean().getPayLoad());
		FacesScopes facesScopes = new FacesScopes();
		spReqDTO.setToken(facesScopes.getBPMAuthenticationCtxToken());
		spReqDTO.setPmUser(facesScopes.getUserSessionBean());
		spReqDTO.setRoleCode(getUserRoleCode());
		spReqDTO.setPrepareSpecStatus(this.getManagedBean()
				.getCurrentStatusId());
		IQTSpecSO specSO = getSpecSO();
		SpecificationResDTO resDTO = new SpecificationResDTO();
		try {
			resDTO = specSO.updateBPMAction(spReqDTO);

		} finally {
			specSO = null;
		}
		return resDTO;
	}

	/**
	 * Cancel qt.cancel quotation tender.
	 * 
	 * @return navigate to acknowledgement page.
	 */
	public String cancelQt() {
		SpecificationReqDTO spReqDTO = new SpecificationReqDTO();
		ScQt scQt = this.getManagedBean().getQt();
		scQt.setScQtCommittees(null);
		scQt.setScQtStatus(null);
		scQt.setScQtBriefings(null);
		List<ScQtRemark> remarks = this.getManagedBean().getScQtRemarksList();
		if (null != remarks) {
			for (int i = 0; i < remarks.size(); i++) {
				String userRemark = remarks.get(i).getRemark();
				if (null == userRemark || "".equals(userRemark)) {
					remarks.remove(i);
				}
			}
		}

		if (null != remarks) {
			scQt.setScQtRemarks(remarks);
		}
		spReqDTO.setScQt(scQt);
		spReqDTO.setOutcome(BpmOutcomeConst.BPM_SUBMIT);
		spReqDTO.setTaskId(this.getManagedBean().getTaskId());
		((SCQuotationTenderDataType) this.getManagedBean().getPayLoad())
				.setCircular(true);
		spReqDTO.setPayLoad(this.getManagedBean().getPayLoad());
		FacesScopes facesScopes = new FacesScopes();
		spReqDTO.setToken(facesScopes.getBPMAuthenticationCtxToken());
		IQTSpecSO specSO = getSpecSO();

		try {
			SpecificationResDTO resDTO = specSO.updateBPMAction(spReqDTO);
			if (resDTO.isSuccessful()) {
				ScQt qt = resDTO.getScQt();
				// Code to renew the session Object..
				renewSesionObject();
				FacesContext
						.getCurrentInstance()
						.addMessage(
								null,
								new FacesMessage(
										FacesMessage.SEVERITY_INFO,
										null,
										MessageUtils
												.getMessage(
														getMessgeConstant(BpmOutcomeConst.BPM_SUBMIT),
														new String[] {
																"Specification Creation For ",
																qt.getQtNo() },
														MessageUtils.BUNDLE_COMMON_MESSAGES,
														new FacesScopes()
																.getUserLocale())));

				return CommonUIConst.ACKNOWLEDGE_PAGE;

			}
		} finally {
			specSO = null;
		}
		return CommonUIConst.ACKNOWLEDGE_PAGE;

	}

	/**
	 * Gpki sign action.submit in specification committee chairperson.
	 * 
	 * @param digiSign
	 *            (PmDigiSign digiSign)
	 * @return navigate to acknowledgement page.
	 */
	public String gpkiSignAction(PmDigiSign digiSign) {
		SpecificationResDTO resDTO = updateSpecCommitteDtls(this
				.getManagedBean().getLocalOutCome(), digiSign);
		if (resDTO.isSuccessful()) {
			ScQt qt = resDTO.getScQt();
			FacesContext
					.getCurrentInstance()
					.addMessage(
							null,
							new FacesMessage(
									FacesMessage.SEVERITY_INFO,
									null,
									MessageUtils
											.getMessage(
													getMessgeConstant(this
															.getManagedBean()
															.getLocalOutCome()),
													new String[] {
															getMessageCommon("lbl.sc.specification.preparation.and.scoring.for"),
															qt.getQtNo() },
													MessageUtils.BUNDLE_COMMON_MESSAGES,
													new FacesScopes()
															.getUserLocale())));

			return CommonUIConst.ACKNOWLEDGE_PAGE;
		}
		return null;
	}

	/**
	 * Revise ministry spec approval.Event handle revise in ministry
	 * specification committee approval.
	 * 
	 * @return navigate to acknowledgement page.
	 */
	public String reviseMinistrySpecApproval() {
		SpecificationResDTO resDTO = prepareSpecChairPersonRequest(this
				.getManagedBean().getLocalOutCome(), null);
		if (resDTO.isSuccessful()) {
			ScQt qt = resDTO.getScQt();
			FacesContext
					.getCurrentInstance()
					.addMessage(
							null,
							new FacesMessage(
									FacesMessage.SEVERITY_INFO,
									null,
									MessageUtils
											.getMessage(
													getMessgeConstant(this
															.getManagedBean()
															.getLocalOutCome()),
													new String[] {
															getMessageCommon("lbl.sc.specification.for"),
															qt.getQtNo() },
													MessageUtils.BUNDLE_COMMON_MESSAGES,
													new FacesScopes()
															.getUserLocale())));

			return CommonUIConst.ACKNOWLEDGE_PAGE;
		}
		return null;
	}

	/**
	 * Revise specification committee.Event handle revise in specification
	 * committee .
	 * 
	 * @return navigate to acknowledgement page.
	 */
	public String reviseSpecificationCommittee() {
		SpecificationResDTO resDTO = updateSpecCommitteDtls(this
				.getManagedBean().getLocalOutCome(), null);
		if (resDTO.isSuccessful()) {
			ScQt qt = resDTO.getScQt();
			FacesContext
					.getCurrentInstance()
					.addMessage(
							null,
							new FacesMessage(
									FacesMessage.SEVERITY_INFO,
									null,
									MessageUtils
											.getMessage(
													getMessgeConstant(this
															.getManagedBean()
															.getLocalOutCome()),
													new String[] {
															getMessageCommon("lbl.sc.specification.preparation.and.scoring.for"),
															qt.getQtNo() },
													MessageUtils.BUNDLE_COMMON_MESSAGES,
													new FacesScopes()
															.getUserLocale())));

			return CommonUIConst.ACKNOWLEDGE_PAGE;
		}
		return null;

	}

	/**
	 * Gets the pm digi sign.
	 * 
	 * @return the pm digi sign
	 */
	public PmDigiSign getPmDigiSign() {
		return pmDigiSign;
	}

	/**
	 * Sets the pm digi sign.
	 * 
	 * @param pmDigiSign
	 *            the new pm digi sign
	 */
	public void setPmDigiSign(PmDigiSign pmDigiSign) {
		this.pmDigiSign = pmDigiSign;
	}

	/**
	 * Cancel spec action.delete existing specs from database.
	 * 
	 * @param flowType
	 *            (String flowType).
	 * @return navigate to business owner or specification committee menu.
	 */
	public String cancelSpecAction(String flowType) {
		IQTSpecSO specSo = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);
		ScQtCheckList checkList = this.getManagedBean().getScQtChecklist();
		if (null != checkList.getQtChecklistId()) {
			// checkList.setDeleted(true);
			List<ScQtCheckList> toBeRemoveCheckLists = new ArrayList<ScQtCheckList>();
			// toBeRemoveCheckLists.add(checkList);
			List<ScQtCheckList> technicalCheckLIsts = this.getManagedBean()
					.getScQtCheckSt2techProposal();
			for (int i = 0; i < technicalCheckLIsts.size(); i++) {
				if (checkList.getQtChecklistId().equals(
						technicalCheckLIsts.get(i).getQtChecklistId())) {
					ScQtCheckList technicalCheckList = technicalCheckLIsts
							.get(i);
					technicalCheckList.setDeleted(true);
					toBeRemoveCheckLists.add(technicalCheckList);
					technicalCheckLIsts.remove(technicalCheckList);
					break;
				}
			}

			List<ScQtCheckList> financialCheckLIsts = this.getManagedBean()
					.getScQtCheckSt2financeProposalList();

			if (null != financialCheckLIsts && !financialCheckLIsts.isEmpty()) {
				if (null != this.getManagedBean().getCheckListTitle()
						&& !"".equals(this.getManagedBean().getCheckListTitle())) {
					SpecificationReqDTO reqDTO = new SpecificationReqDTO();
					reqDTO.setQtId(this.getManagedBean().getQt().getQtId());
					reqDTO.setPrepareSpec(false);

					specSo = BusinessDelegate.getInstance()
							.getServiceObject(
									ModuleCodeConst.SUPPLIER_SELECTION,
									IQTSpecSO.class);
					SpecificationResDTO resDto = specSo
							.retrieveSpecDetails(reqDTO);

					if (null != resDto
							&& null != resDto.getFinSpecResultBean()
									.getResultList()
							&& !resDto.getFinSpecResultBean().getResultList()
									.isEmpty()) {

						for (ScQtCheckList financialspec : resDto
								.getFinSpecResultBean().getResultList()) {
							if (financialspec.getTitle().equalsIgnoreCase(
									this.getManagedBean().getCheckListTitle())) {
								financialspec.setDeleted(true);
								toBeRemoveCheckLists.add(financialspec);
								break;
								/*
								 * throw new
								 * EpException(MessageConst.ERROR_760310, new
								 * String[] { financialSpecs.getTitle() });
								 */
							}
						}

					}
				}
			}

			SpecificationReqDTO specReqDTO = new SpecificationReqDTO();
			specReqDTO.setScQt(this.getManagedBean().getQt());
			specReqDTO.setCheckLists(toBeRemoveCheckLists);
			SpecificationResDTO resDTO = specSo.deleteChecklists(specReqDTO);

			if (resDTO.isSuccessful()) {
				this.getManagedBean().setScQtCheckSt2techProposal(null);
				this.getManagedBean().setFinancialScoring(null);
				this.getManagedBean().setOfferPricePackage(null);
				this.getManagedBean().setQt(resDTO.getScQt());
				retriveTechnicalCheckList();
				this.getManagedBean().setCheckListTitle("");
				this.getManagedBean().setIndex(-1);
				return "/xhtml/sc/specification/specificationCommittee/specificationCommitteeMain.xhtml?faces-redirect=true";
			}
		} else {
			this.getManagedBean().setCheckListTitle("");
			this.getManagedBean().setIndex(-1);
			this.getManagedBean().getScQtCheckSt2techProposal();
			return "/xhtml/sc/specification/specificationCommittee/specificationCommitteeMain.xhtml?faces-redirect=true";

		}

		return null;

	}

	/**
	 * Save template spec details.
	 * 
	 * @param flowType
	 *            (String flowType)
	 * @param specType
	 *            (String specType)
	 * @return the string
	 */
	public String saveTemplateSpecDetails(String flowType, String specType) {
		IQTSpecSO specSO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
						IQTSpecSO.class);
		ScQtCheckList scQtCheckList = this.getManagedBean().getScQtChecklist();
		scQtCheckList.setTitle(scQtCheckList.getScQtDoc().getTplSpecMaster()
				.getSpecName());
		scQtCheckList.getScQtDoc().getTplSpecMaster()
				.setTemplateStatus(TemplateSpecConst.TEMPLATE_STATUS_DRAFT);
		SpecificationReqDTO reqDTO = new SpecificationReqDTO();
		if (SourcingConstants.TECHNICAL_DOCTYPE
						.equalsIgnoreCase(specType)) {
			ScQtSupplierCriteria zoneSupplierCriteria = getManagedBean()
					.getQt().getScQtSupplierCriteria();
			if (((null != zoneSupplierCriteria && zoneSupplierCriteria
					.isBrandNew() == true) || (getManagedBean().getQt()
					.isZonal()
					&& null != zoneSupplierCriteria
					&& null != zoneSupplierCriteria.getQtZoneCriterias() && null == zoneSupplierCriteria
					.getQtZoneCriterias().getSpecZoneId()))) {
				ScQt scQt = saveZonesBeforeTemplateSpec(getManagedBean()
						.getQt());
				reqDTO.setZonesQt(true);
				reqDTO.setScQt(scQt);
				// resDTO = specSO.saveTemplateSpecZoneDtls(reqDTO);
				this.getManagedBean().setQtCategoryList(
						new ArrayList<ScQtCategory>());
				//this.getManagedBean().setZoneGeneralTrees(
					//	new ArrayList<TreeNode>());
				// this.getManagedBean().setQt(resDTO.getScQt());
				// retriveSpecdetails(resDTO.getScQt().getQtId());
				// retriveSupplierQualificationCriteria("SC");
				// validateBusinessOwner(true);
			}

		}

		if (!SourcingConstants.FINANCIAL_DOCTYPE.equalsIgnoreCase(specType)) {
			scQtCheckList.setPmStatus(getPmStatusByStatusId(QTStatusConst.TS_PENDING_SUBMISSION));
			scQtCheckList.setPmEvalStatus(getPmStatusByStatusId(QTStatusConst.TS_PENDING_SUBMISSION));
			if (getManagedBean().getQt().isZonal()) {
				ScQtSupplierCriteria supplierCriteria = getManagedBean()
						.getQt().getScQtSupplierCriteria();
				if (null != supplierCriteria
						&& null != supplierCriteria.getQtZoneCriterias()) {
					scQtCheckList
							.getScQtDoc()
							.getTplSpecMaster()
							.setTplSpecZone(
									supplierCriteria.getQtZoneCriterias());
				} else {
					throw new EpException(
							"Please choose the zones before proceed.");
				}
			}

		} else {
			scQtCheckList.setPmStatus(getPmStatusByStatusId(QTStatusConst.TS_PENDING_SUBMISSION));
			scQtCheckList.setPmEvalStatus(getPmStatusByStatusId(QTStatusConst.TS_PENDING_SUBMISSION));
		}
		List<ScQtCheckList> toBeRemoveList = null;
		if (SourcingConstants.TECHNICAL_DOCTYPE.equalsIgnoreCase(specType)) {

			List<ScQtCheckList> technicalCheckList = this.getManagedBean()
					.getScQtCheckSt2techProposal();
			if (this.getManagedBean().getIndex() == -1
					&& null == scQtCheckList.getQtChecklistId()) {
				if (null != technicalCheckList && !technicalCheckList.isEmpty()) {

					if (technicalCheckList.contains(scQtCheckList)) {
						throw new EpException(MessageConst.ERROR_760207,
								new String[] { scQtCheckList.getTitle() });
					}
				}
			}

			List<ScQtCheckList> finanicalCheckList = this.getManagedBean()
					.getScQtCheckSt2financeProposalList();
			toBeRemoveList = new ArrayList<ScQtCheckList>();
			if (null != finanicalCheckList && !finanicalCheckList.isEmpty()) {
				if (null != this.getManagedBean().getCheckListTitle()
						&& !"".equals(this.getManagedBean().getCheckListTitle())) {
					for (int i = 0; i < finanicalCheckList.size(); i++) {
						ScQtCheckList localCheckList = finanicalCheckList
								.get(i);
						if (this.getManagedBean().getCheckListTitle()
								.equalsIgnoreCase(localCheckList.getTitle())) {
							localCheckList.setDeleted(true);
							toBeRemoveList.add(localCheckList);
							finanicalCheckList.remove(localCheckList);
							break;
						}
					}
				}
			}
		}
		List<ScQtCheckList> updatedCheckLists = new ArrayList<ScQtCheckList>();
		updatedCheckLists.add(scQtCheckList);

		reqDTO.setCheckLists(updatedCheckLists);
		reqDTO.setTobeRemoveList(toBeRemoveList);

		SpecificationResDTO resDTO = specSO.saveOrUpdateTemplateSpecs(reqDTO);

		// Need to redirect particualr page..
		if (resDTO.isSuccessful()) {
			if (resDTO.getScQt() != null) {
				this.getManagedBean().setQt(resDTO.getScQt());
			}
			List<ScQtCheckList> savedCheckLists = resDTO.getSavedCheckLists();
			this.getManagedBean().setFinancialScoring(null);
			this.getManagedBean().setOfferPricePackage(null);
			if(reqDTO.isZonesQt()){
				this.getManagedBean().setZoneGeneralTrees(
						new ArrayList<TreeNode>());
			}
			specCommitteeResetData();
			// settemplateSpecList(savedCheckLists);
			setTechnicalCheckList(savedCheckLists.get(0));
			
			FacesContext
					.getCurrentInstance()
					.addMessage(
							null,
							new FacesMessage(
									FacesMessage.SEVERITY_INFO,
									null,
									MessageUtils
											.getMessage(
													MessageConst.INFO_700004,
													new String[] {
															getMessageCommon("lbl.sc.template.spec"),
															"" },
													MessageUtils.BUNDLE_COMMON_MESSAGES,
													new FacesScopes()
															.getUserLocale())));
		}

		return null;
	}

	private void specCommitteeResetData(){
		this.getManagedBean().setTechnicalScoring(null);
		this.getManagedBean().setProgressivePaymentList(null);
		this.getManagedBean().setScheduleList(null);
		this.getManagedBean().setTechnicalScoreSetting(null);
		this.getManagedBean().setFinancialScoreSetting(null);
		this.getManagedBean().setDemoScoreSetting(null);
		this.getManagedBean().setFinacialItemScoreSetting(null);
		this.getManagedBean().setRecommendationPackageScore(null);
		this.getManagedBean().setRecommendationScore(null);
		this.getManagedBean().setQtCategoryList(null);
		this.getManagedBean()
		.setQtSupplierList(null);
		
	}
	/**
	 * Sets the technical check list.
	 * 
	 * @param localCheckList
	 *            (ScQtCheckList localCheckList).
	 */
	private void setTechnicalCheckList(ScQtCheckList localCheckList) {
		// this.getManagedBean().setCheckListTitle("");
		this.getManagedBean().setScQtChecklist(localCheckList);
		ITemplateSpecSO templateSpecSO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.COMMON_COMPONENT,
						ITemplateSpecSO.class);
		TemplateSpecReqDTO specReqDTO = new TemplateSpecReqDTO();
		specReqDTO
				.setSpecMaster(localCheckList.getScQtDoc().getTplSpecMaster());
		specReqDTO.setContentType(TemplateSpecConst.CONTENT_TYPE_QUESTION);
		TemplateSpecResDTO tmpresDTO = templateSpecSO
				.getTemplateMaster(specReqDTO);
		localCheckList.getScQtDoc().setTplSpecMaster(tmpresDTO.getSpecMaster());
		TemplateSpecBean templateSpecBean = getManagedBean()
				.getTemplateSpecBean();
		templateSpecBean.setSpecMaster(localCheckList.getScQtDoc()
				.getTplSpecMaster());
		if (this.getManagedBean().getQt().isZonal()) {
			ScQtSupplierCriteria supplierCriteria = this.getManagedBean()
					.getQt().getScQtSupplierCriteria();
			if (null != supplierCriteria
					&& null != supplierCriteria.getQtZoneCriterias()) {
				if (SourcingConstants.FINANCIAL_DOCTYPE
						.equalsIgnoreCase(localCheckList.getDocCategory())
						|| SourcingConstants.TECHNICAL_DOCTYPE
								.equalsIgnoreCase(localCheckList
										.getDocCategory())) {
					retriveSpecdetails(this.getManagedBean().getQt().getQtId());
					List<TplSpecZoneDtl> businessOwnerZone = convertTreeNodesToZoneCriteria(
							this.getManagedBean().getZoneGeneralTrees(), null);

					if (businessOwnerZone != null
							&& !businessOwnerZone.isEmpty()) {

						for (TplSpecZoneDtl tplSpecZoneDtl : businessOwnerZone) {
							supplierCriteria.getQtZoneCriterias().setModified(
									true);
							tplSpecZoneDtl.setTplSpecZone(supplierCriteria
									.getQtZoneCriterias());

						}
						templateSpecBean.getSpecMaster().setTplSpecZone(
								supplierCriteria.getQtZoneCriterias());
						templateSpecBean.getSpecMaster().getTplSpecZone()
								.setTplSpecZoneDtls(businessOwnerZone);
					}

				}

			}
		}
		if (null == this.getManagedBean().getScQtCheckSt2techProposal()) {
			this.getManagedBean().setScQtCheckSt2techProposal(
					new ArrayList<ScQtCheckList>());
		}
		if (null == this.getManagedBean().getScQtCheckSt2financeProposalList()) {
			this.getManagedBean().setScQtCheckSt2financeProposalList(
					new ArrayList<ScQtCheckList>());
		}
		if (SourcingConstants.TECHNICAL_DOCTYPE.equalsIgnoreCase(localCheckList
				.getDocCategory())) {
			if (this.getManagedBean().getIndex() == -1) {
				if (null != this.getManagedBean().getScQtCheckSt2techProposal()
						&& !this.getManagedBean().getScQtCheckSt2techProposal()
								.isEmpty()) {
					boolean techSpec = false;
					for (int i = 0; i < this.getManagedBean()
							.getScQtCheckSt2techProposal().size(); i++) {
						ScQtCheckList qtCheckList = this.getManagedBean()
								.getScQtCheckSt2techProposal().get(i);
						if (qtCheckList.getTitle().equalsIgnoreCase(
								localCheckList.getTitle())) {
							techSpec = true;
							this.getManagedBean().getScQtCheckSt2techProposal()
									.remove(i);
							this.getManagedBean().getScQtCheckSt2techProposal()
									.add(localCheckList);
							// Collections.sort(finanicialCheckLists);
						}
					}
					if (!techSpec) {
						this.getManagedBean().getScQtCheckSt2techProposal()
								.add(localCheckList);

					}
				} else {
					this.getManagedBean().getScQtCheckSt2techProposal()
							.add(localCheckList);
				}
				// Collections.sort(this.getManagedBean()
				// .getScQtCheckSt2techProposal());
			} else {
				this.getManagedBean().getScQtCheckSt2techProposal()
						.remove(this.getManagedBean().getIndex());
				this.getManagedBean().getScQtCheckSt2techProposal()
						.add(this.getManagedBean().getIndex(), localCheckList);
			}
		} else {
			setFinSpecScoreWeightage(localCheckList);
			this.getManagedBean().getScQtCheckSt2financeProposalList()
					.remove(this.getManagedBean().getIndex());
			this.getManagedBean().getScQtCheckSt2financeProposalList()
					.add(this.getManagedBean().getIndex(), localCheckList);

		}

	}

	/**
	 * Sets the fin spec score weightage.
	 * 
	 * @param scoreWeightage
	 *            (ScQtCheckList scoreWeightage).
	 */
	private void setFinSpecScoreWeightage(ScQtCheckList scoreWeightage) {
		List<ScQtCheckList> finacialspecLists = this.getManagedBean()
				.getScQtCheckSt2financeProposalList();
		if (null != finacialspecLists && !finacialspecLists.isEmpty()) {
			for (ScQtCheckList checklists : finacialspecLists) {
				if (ParameterConst.DOCUMENT_DEFINITION_TYPE_TEMPLATE_SPECIFICATION
						.equalsIgnoreCase(checklists.getDefinationType()
								.getParameterCode())) {
					checklists.setQtScoreWeightage(scoreWeightage
							.getQtScoreWeightage());

				}
			}
		}
	}

	/**
	 * Sets the template spec list.
	 * 
	 * @param savedCheckLists
	 *            (List<ScQtCheckList> savedCheckLists).
	 */
	private void settemplateSpecList(List<ScQtCheckList> savedCheckLists,
			String flowType) {

		if (null != savedCheckLists && !savedCheckLists.isEmpty()) {
			if (null == this.getManagedBean().getScQtCheckSt2techProposal()) {
				this.getManagedBean().setScQtCheckSt2techProposal(
						new ArrayList<ScQtCheckList>());
			}

			if (null == this.getManagedBean()
					.getScQtCheckSt2financeProposalList()) {
				this.getManagedBean().setScQtCheckSt2financeProposalList(
						new ArrayList<ScQtCheckList>());
			}

			int currIndex = this.getManagedBean().getIndex();
			int newTplSpecIndex = 0;
			int lastTplSpecIndex = 0;
			boolean isNewTplSpec = this.getManagedBean().isNewTemplateSpec();

			if (isNewTplSpec) {
				for (ScQtCheckList checkList : this.getManagedBean()
						.getScQtCheckSt2techProposal()) {
					if (checkList
							.getDefinationType()
							.getParameterCode()
							.equals(ParameterConst.DOCUMENT_DEFINITION_TYPE_TEMPLATE_SPECIFICATION)) {
						lastTplSpecIndex = lastTplSpecIndex + 1;
					}
				}
				newTplSpecIndex = lastTplSpecIndex;
			}

			for (ScQtCheckList checkList : savedCheckLists) {

				if (SourcingConstants.TECHNICAL_DOCTYPE
						.equalsIgnoreCase(checkList.getDocCategory())) {
					List<ScQtCheckList> technicalCheckLists = this
							.getManagedBean().getScQtCheckSt2techProposal();

					if (currIndex == -1) {
						if (null != technicalCheckLists
								&& !technicalCheckLists.isEmpty()) {
							boolean techSpec = false;

							for (int i = 0; i < technicalCheckLists.size(); i++) {
								ScQtCheckList qtCheckList = technicalCheckLists
										.get(i);

								if (qtCheckList.getTitle().equalsIgnoreCase(
										checkList.getTitle())) {
									techSpec = true;
									technicalCheckLists.remove(i);
									technicalCheckLists.add(i, checkList);
								}
							}
							if (!techSpec) {
								if (isNewTplSpec) {
									technicalCheckLists.add(newTplSpecIndex,
											checkList);
								} else {
									technicalCheckLists.add(checkList);
								}
							}

						} else {
							technicalCheckLists.add(checkList);
						}

					} else {
						technicalCheckLists.remove(currIndex);
						technicalCheckLists.add(currIndex, checkList);
					}

				} else {
					setFinSpecScoreWeightage(checkList);
					List<ScQtCheckList> finanicialCheckLists = this
							.getManagedBean()
							.getScQtCheckSt2financeProposalList();

					if (currIndex == -1) {
						if (isNewTplSpec) {
							if ((null != finanicialCheckLists)
									&& (finanicialCheckLists.size() >= newTplSpecIndex)) {
								finanicialCheckLists.add(newTplSpecIndex,
										checkList);
							} else {
								finanicialCheckLists.add(checkList);
							}
						} else {
							finanicialCheckLists.add(checkList);
						}

					} else {
						if (null != finanicialCheckLists
								&& !finanicialCheckLists.isEmpty()) {
							boolean finspec = false;

							for (int i = 0; i < finanicialCheckLists.size(); i++) {
								ScQtCheckList qtCheckList = finanicialCheckLists
										.get(i);
								if (qtCheckList.getTitle().equalsIgnoreCase(
										checkList.getTitle())) {
									finspec = true;
									finanicialCheckLists.remove(i);
									finanicialCheckLists.add(i, checkList);
								}
							}

							if (!finspec) {
								finanicialCheckLists.add(currIndex, checkList);
							}

						} else {
							finanicialCheckLists.add(checkList);
						}
					}
					/*
					 * this.getManagedBean().getScQtCheckSt2financeProposalList()
					 * .remove(this.getManagedBean().getIndex());
					 * this.getManagedBean
					 * ().getScQtCheckSt2financeProposalList()
					 * .add(this.getManagedBean().getIndex(), checkList);
					 */
				}
			}
		}

	}

	/**
	 * Complete template spec details.
	 * 
	 * @param flowType
	 *            ((String flowType)
	 * @param specType
	 *            (String specType)
	 * @return navigate to business owner or specification committee menu.
	 */
	public String completeTemplateSpecDetails(String flowType, String specType) {
		IQTSpecSO specSO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
						IQTSpecSO.class);
		SpecificationReqDTO reqDTO = new SpecificationReqDTO();
		if (SourcingConstants.TECHNICAL_DOCTYPE
						.equalsIgnoreCase(specType)) {
			ScQtSupplierCriteria zoneSupplierCriteria = getManagedBean()
					.getQt().getScQtSupplierCriteria();
			if (((null != zoneSupplierCriteria && zoneSupplierCriteria
					.isBrandNew() == true) || (getManagedBean().getQt()
					.isZonal()
					&& null != zoneSupplierCriteria
					&& null != zoneSupplierCriteria.getQtZoneCriterias() && null == zoneSupplierCriteria
					.getQtZoneCriterias().getSpecZoneId()))) {
				ScQt scQt = saveZonesBeforeTemplateSpec(getManagedBean()
						.getQt());
				// SpecificationReqDTO reqDTO = new SpecificationReqDTO();
				// SpecificationResDTO resDTO = new SpecificationResDTO();
				// reqDTO.setScQt(scQt);
				// resDTO = specSO.saveQTDtl(reqDTO);
				// resDTO = specSO.saveTemplateSpecZoneDtls(reqDTO);
				reqDTO.setZonesQt(true);
				reqDTO.setScQt(scQt);
				this.getManagedBean().setQtCategoryList(
						new ArrayList<ScQtCategory>());
				//this.getManagedBean().setZoneGeneralTrees(
						//new ArrayList<TreeNode>());
				// this.getManagedBean().setQt(resDTO.getScQt());
				// retriveSpecdetails(resDTO.getScQt().getQtId());
				// retriveSupplierQualificationCriteria("SC");
				// validateBusinessOwner(true);
			}

		}
		// retriveSpecdetails(this.getManagedBean().getQt().getQtId());
		// retriveBusinessOwnerQualificationCriteri();

		ScQtCheckList scQtCheckList = this.getManagedBean().getScQtChecklist();
		List<ScQtCheckList> updatedCheckLists = new ArrayList<ScQtCheckList>();

		ScQt scQt = this.getManagedBean().getQt();
		if (SourcingConstants.TECHNICAL_DOCTYPE.equalsIgnoreCase(specType)) {
			if (null == scQtCheckList.getTitle()
					|| "".equalsIgnoreCase(scQtCheckList.getTitle())) {
				scQtCheckList.setTitle(scQtCheckList.getScQtDoc()
						.getTplSpecMaster().getSpecName());
			}
			List<ScQtCheckList> technicalCheckList = this.getManagedBean()
					.getScQtCheckSt2techProposal();
			if (this.getManagedBean().getIndex() == -1
					&& null == scQtCheckList.getQtChecklistId()) {
				if (null != technicalCheckList && !technicalCheckList.isEmpty()) {

					if (technicalCheckList.contains(scQtCheckList)) {
						throw new EpException(MessageConst.ERROR_760207,
								new String[] { scQtCheckList.getTitle() });
					}
				}
			}
			scQtCheckList.setTitle(scQtCheckList.getScQtDoc()
					.getTplSpecMaster().getSpecName());
			scQtCheckList.setPmStatus(getPmStatusByStatusId(QTStatusConst.TS_COMPLETED));
			scQtCheckList.getScQtDoc().getTplSpecMaster()
					.setTemplateStatus(TemplateSpecConst.TEMPLATE_STATUS_FINAL);
			// Code to construct financial checklist..

			if (scQt.isZonal()) {
				ScQtSupplierCriteria supplierCriteria = scQt
						.getScQtSupplierCriteria();
				if (null != supplierCriteria
						&& null != supplierCriteria.getQtZoneCriterias()) {
					scQtCheckList
							.getScQtDoc()
							.getTplSpecMaster()
							.setTplSpecZone(
									supplierCriteria.getQtZoneCriterias());
				} else {
					throw new EpException(
							"Please choose the zones before proceed.");
				}
			}

			TplSpecMaster specMaster = TemplateSpecHelper.getInstance()
					.cloneTplSpecMaster(
							scQtCheckList.getScQtDoc().getTplSpecMaster(),
							false, true, true);
			specMaster
					.setProcessType(ParameterConst.TPL_DOC_PROCESS_TYPE_SOURCING);

			if (scQt.isZonal()) {
				ScQtSupplierCriteria supplierCriteria = scQt
						.getScQtSupplierCriteria();
				if (null != supplierCriteria
						&& null != supplierCriteria.getQtZoneCriterias()) {
					specMaster.setTplSpecZone(supplierCriteria
							.getQtZoneCriterias());
				} else {
					throw new EpException(
							"Please choose the zones before proceed.");
				}
			}
			IParameterSO parameterSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IParameterSO.class);
			ParameterReqDTO finSpecType = new ParameterReqDTO();
			finSpecType.setParameterType(ParameterTypeConst.SPECIFICATION_TYPE);
			finSpecType
					.setParameterCode(ParameterConst.SPECIFICATION_TYPE_FINANCIAL);
			ParameterResDTO finSpecresDTO = parameterSO
					.retrieveParameterByTypeAndCode(finSpecType);
			specMaster.setSpecTypeCode(finSpecresDTO.getPmParameter());
			specMaster.setScoringReq(true);
			TemplateSpecHelper.getInstance().cloneTplSpecQuestions(
					specMaster,
					scQtCheckList.getScQtDoc().getTplSpecMaster()
							.getTplSpecQuestions());
			ScQtCheckList qtCheckList = constructQtDocByType(specMaster,
					SourcingConstants.FINANCIAL_DOCTYPE);
			qtCheckList.setPmStatus(getPmStatusByStatusId(QTStatusConst.TS_PENDING_SUBMISSION));
			qtCheckList.setTitle(scQtCheckList.getScQtDoc().getTplSpecMaster()
					.getSpecName());
			qtCheckList.setScoringReq(true);
			qtCheckList.getScQtDoc().getTplSpecMaster()
					.setTemplateStatus(TemplateSpecConst.TEMPLATE_STATUS_DRAFT);
			qtCheckList.getScQtDoc().getTplSpecMaster().setWeightageReq(false);
			TemplateSpecHelper.getInstance().updateItemWeightageReq(
					qtCheckList.getScQtDoc().getTplSpecMaster(),
					qtCheckList.getScQtDoc().getTplSpecMaster()
							.getTplSpecQuestions());

			// copy scoring and estimated cost
			List<ScQtCheckList> toBeRemovedList = validateDuplicateFinancialSpecs(
					qtCheckList, flowType);
			if (toBeRemovedList != null && toBeRemovedList.size() > 0) {
				TplSpecMaster oldSpecMaster = toBeRemovedList.get(0)
						.getScQtDoc().getTplSpecMaster();
				ITemplateSpecSO templateSpecSO;

				try {
					templateSpecSO = EpEjbBusinessDelegate.getInstance()
							.getServiceObject(ModuleCodeConst.COMMON_COMPONENT,
									ITemplateSpecSO.class);
					TemplateSpecReqDTO specReqDTO = new TemplateSpecReqDTO();
					specReqDTO.setSpecMaster(oldSpecMaster);
					specReqDTO
							.setContentType(TemplateSpecConst.CONTENT_TYPE_QUESTION);
					oldSpecMaster = templateSpecSO
							.getTemplateMaster(specReqDTO).getSpecMaster();

					TemplateSpecHelper.getInstance().copySpecDetails(
							specMaster, oldSpecMaster);
				} finally {
					templateSpecSO = null;
				}

			}

			updatedCheckLists.add(scQtCheckList);
			updatedCheckLists.add(qtCheckList);
			List<ScQtCheckList> finanicalCheckList = this.getManagedBean()
					.getScQtCheckSt2financeProposalList();
			List<ScQtCheckList> toBeRemoveList = new ArrayList<ScQtCheckList>();
			if (null != finanicalCheckList && !finanicalCheckList.isEmpty()) {
				if (null != this.getManagedBean().getCheckListTitle()
						&& !"".equals(this.getManagedBean().getCheckListTitle())) {
					for (int i = 0; i < finanicalCheckList.size(); i++) {
						ScQtCheckList localCheckList = finanicalCheckList
								.get(i);
						if (this.getManagedBean().getCheckListTitle()
								.equalsIgnoreCase(localCheckList.getTitle())) {
							localCheckList.setDeleted(true);
							toBeRemoveList.add(localCheckList);
							finanicalCheckList.remove(localCheckList);
							break;
						}
					}
				}
			}
			reqDTO.setTobeRemoveList(toBeRemoveList);
		} else {
			scQtCheckList.setPmStatus(getPmStatusByStatusId(QTStatusConst.TS_COMPLETED));
			scQtCheckList.setPmEvalStatus(getPmStatusByStatusId(QTStatusConst.TS_PENDING_SUBMISSION));
			scQtCheckList.getScQtDoc().getTplSpecMaster()
					.setTemplateStatus(TemplateSpecConst.TEMPLATE_STATUS_FINAL);
			updatedCheckLists.add(scQtCheckList);

			reqDTO.setScQt(scQt);

		}

		reqDTO.setCheckLists(updatedCheckLists);

		// code if alredy checklist exist need to remove existing finalcialspec

		/*
		 * IQTSpecSO specSO = EpEjbBusinessDelegate.getInstance()
		 * .getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
		 * IQTSpecSO.class);
		 */
		SpecificationResDTO resDTO = new SpecificationResDTO();
		try {
			resDTO = specSO.saveOrUpdateTemplateSpecs(reqDTO);
		} catch (EpException e) {

			if (SourcingConstants.TECHNICAL_DOCTYPE.equalsIgnoreCase(specType)) {
				if (null != reqDTO.getCheckLists()) {
					for (ScQtCheckList checkList : reqDTO.getCheckLists()) {
						if (SourcingConstants.TECHNICAL_DOCTYPE
								.equalsIgnoreCase(checkList.getDocCategory())) {
							checkList.setPmStatus(getPmStatusByStatusId(QTStatusConst.TS_PENDING_SUBMISSION));
							this.getManagedBean().setScQtChecklist(checkList);
						}
					}
				}
			} else {
				if (SourcingConstants.FINANCIAL_DOCTYPE
						.equalsIgnoreCase(specType)) {
					if (null != reqDTO.getCheckLists()) {
						for (ScQtCheckList checkList : reqDTO.getCheckLists()) {
							if (SourcingConstants.FINANCIAL_DOCTYPE
									.equalsIgnoreCase(checkList
											.getDocCategory())) {
								checkList.setPmStatus(getPmStatusByStatusId(QTStatusConst.TS_PENDING_SUBMISSION));
								this.getManagedBean().setScQtChecklist(
										checkList);
							}
						}
					}
				}
			}
			String msgCode = e.getMessage();
			String msgParam[] = e.getParameters();
			if (MessageConst.ERROR_100023.equalsIgnoreCase(msgCode)) {
				msgCode = MessageConst.ERROR_760295;
				msgParam = new String[] { "Tier Quantity" };
			}
			throw new EpException(msgCode, msgParam);
		}

		// Need to redirect particualr page..
		if (resDTO.isSuccessful()) {
			if (resDTO.getScQt() != null) {
				this.getManagedBean().setQt(resDTO.getScQt());
			}

			List<ScQtCheckList> savedCheckLists = resDTO.getSavedCheckLists();
			settemplateSpecList(savedCheckLists, flowType);
			this.getManagedBean().setIndex(-1);
			this.getManagedBean().setCheckListTitle("");
			this.getManagedBean().setFinancialScoring(null);
			this.getManagedBean().setOfferPricePackage(null);
			specCommitteeResetData();
			if (reqDTO.isZonesQt() || (this.getManagedBean().getQt().isZonal()&& SourcingConstants.FINANCIAL_DOCTYPE
					.equalsIgnoreCase(specType))) {
				this.getManagedBean().setZoneGeneralTrees(
						new ArrayList<TreeNode>());
				retriveSpecdetails(this.getManagedBean().getQt().getQtId());
			}
			return "/xhtml/sc/specification/specificationCommittee/specificationCommitteeMain.xhtml?faces-redirect=true";
		}
		return null;
	}

	private List<ScQtCheckList> validateDuplicateFinancialSpecs(
			ScQtCheckList financialSpecs, String flowType) {
		IQTSpecSO specSO;
		SpecificationResDTO resDto;
		List<ScQtCheckList> toBeRemovedList = new ArrayList<ScQtCheckList>();

		try {
			if (null == financialSpecs.getQtChecklistId()
					|| financialSpecs.isBrandNew()) {
				SpecificationReqDTO reqDTO = new SpecificationReqDTO();
				reqDTO.setQtId(this.getManagedBean().getQt().getQtId());
				reqDTO.setPrepareSpec(false);

				specSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);
				resDto = specSO.retrieveSpecDetails(reqDTO);

				if (null != resDto
						&& null != resDto.getFinSpecResultBean()
								.getResultList()
						&& !resDto.getFinSpecResultBean().getResultList()
								.isEmpty()) {
					for (ScQtCheckList financialspec : resDto
							.getFinSpecResultBean().getResultList()) {
						if (financialspec.getTitle().equalsIgnoreCase(
								financialSpecs.getTitle())) {
							financialspec.setDeleted(true);
							toBeRemovedList.add(financialspec);
							break;
							/*
							 * throw new EpException(MessageConst.ERROR_760310,
							 * new String[] { financialSpecs.getTitle() });
							 */
						}
					}
					if (null != toBeRemovedList && !toBeRemovedList.isEmpty()) {
						removeDuplicateFinancialSpec(flowType, toBeRemovedList);
					}
				}
			}
		} finally {
			specSO = null;
		}

		return toBeRemovedList;
	}

	private void removeDuplicateFinancialSpec(String flowType,
			List<ScQtCheckList> toBeRemoveCheckLists) {
		IQTSpecSO specSo = null;
		try {
			specSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);
			SpecificationReqDTO specReqDTO = new SpecificationReqDTO();
			specReqDTO.setCheckLists(toBeRemoveCheckLists);
			specReqDTO.setScQt(this.getManagedBean().getQt());
			SpecificationResDTO resDTO = specSo.deleteChecklists(specReqDTO);
			if (resDTO.isSuccessful()) {
				this.getManagedBean().getQt().setEstimatedAmount(resDTO.getEstimatedamount());
				this.getManagedBean().getQt().setDecryptedEstAmt(resDTO.getDecryptedAmount());
				this.getManagedBean().setScQtCheckSt2financeProposalList(
						null);
				this.getManagedBean().setFinancialScoring(null);
				retriveFinancialCheckList();
			}
		} finally {

		}
	}

	/**
	 * Creates the template spec.
	 * 
	 * @param tplSpecMaster
	 *            (TplSpecMaster tplSpecMaster)
	 * @param flowtype
	 *            (String flowtype)
	 * @return navigate to template spec screens.
	 */
	public String createTemplateSpec(TplSpecMaster tplSpecMaster,
			String flowtype) {
		this.getManagedBean().setSpecSelectStats(false);
		this.getManagedBean().setSpecNewable(true);
		this.getManagedBean().setSpecItemWeightageRequired(false);
		try {
			ITemplateSpecSO templateSpecSO = EpEjbBusinessDelegate
					.getInstance().getServiceObject(
							ModuleCodeConst.COMMON_COMPONENT,
							ITemplateSpecSO.class);
			TemplateSpecReqDTO specReqDTO = new TemplateSpecReqDTO();
			specReqDTO.setSpecMaster(tplSpecMaster);
			specReqDTO.setContentType(TemplateSpecConst.CONTENT_TYPE_QUESTION);
			TemplateSpecResDTO resDTO = templateSpecSO
					.getTemplateMaster(specReqDTO);
			TplSpecMaster specMaster = TemplateSpecHelper.getInstance()
					.cloneTplSpecMaster(resDTO.getSpecMaster(), true,
							getManagedBean().getQt().isTechEvaluationReq(),
							true);
			specMaster
					.setProcessType(ParameterConst.TPL_DOC_PROCESS_TYPE_SOURCING);
			if (getManagedBean().getQt().isZonal()) {
				ScQtSupplierCriteria supplierCriteria = getManagedBean()
						.getQt().getScQtSupplierCriteria();
				if (null != supplierCriteria
						&& null != supplierCriteria.getQtZoneCriterias()) {
					TemplateSpecHelper.getInstance().updateZoneDtl(specMaster,
							supplierCriteria.getQtZoneCriterias());
				} else {
					throw new EpException(
							"Please choose the zones before proceed.");
				}
			} else {
				TemplateSpecHelper.getInstance()
						.updateZoneDtl(specMaster, null);
			}

			ScQtCheckList qtCheckList = constructQtDocByType(specMaster,
					SourcingConstants.TECHNICAL_DOCTYPE);

			TemplateSpecBean templateSpecBean = getManagedBean()
					.getTemplateSpecBean();
			List<ScQtCheckList> technicalCheckList = this.getManagedBean()
					.getScQtCheckSt2techProposal();
			if (null != technicalCheckList && !technicalCheckList.isEmpty()) {
				boolean istechnicalSpec = isAtleastTechnicalSpec(technicalCheckList);
				this.getManagedBean().setSpecItemWeightageRequired(
						istechnicalSpec);
				boolean weightageType = retriveExistingTemplatespec(technicalCheckList);
				specMaster.setWeightageReq(weightageType);
				templateSpecBean.setSpecMaster(specMaster);
				TemplateSpecBackingBean templateSpecBackingbean = new TemplateSpecBackingBean();
				templateSpecBackingbean
						.updateItemWeightageReq(templateSpecBean);

			} else {
				this.getManagedBean().setSpecItemWeightageRequired(false);
			}
			templateSpecBean.setSpecMaster(specMaster);

			this.getManagedBean().setScQtChecklist(qtCheckList);
			this.getManagedBean().setIndex(-1);
			this.getManagedBean().setAutoUpdate(true);
			RequestContext.getCurrentInstance().execute(
					"createOwnerSpec.hide()");
			// RequestContext.getCurrentInstance().update("btnId");
		} finally {

		}

		this.getManagedBean().setIndex(-1);
		this.getManagedBean().setNewTemplateSpec(true);

		return "/xhtml/sc/boSctemplatespec/technicalCommitteeTemplate.xhtml?faces-redirect=true";
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

		return checkList;
	}

	/**
	 * Evaluation type change.change evaluation type radio in business owner.
	 */
	public void evaluationTypeChange() {
		techEvaluationReqChanged();
	}

	/**
	 * Tech evaluation req changed.change Required Technical Evaluation radio in
	 * business owner.
	 */
	public void techEvaluationReqChanged() {
		boolean isTechEvaluationReq = getManagedBean().getQt()
				.isTechEvaluationReq();

		// Get list of technical checklist to set score req.
		if (null != getManagedBean().getQt().getScQtSupplierCriteria()
				&& null != getManagedBean().getQt().getScQtSupplierCriteria()
						.getQtSupplierCriteriaId()) {
			retriveTechnicalSpecification();
		}
		List<ScQtCheckList> techCheckList = this.getManagedBean()
				.getScQtCheckSt2techProposal();

		if (null != techCheckList) {
			for (ScQtCheckList qtCheckList : techCheckList) {
				qtCheckList.setScoringReq(isTechEvaluationReq);
				qtCheckList.getScQtDoc().getTplSpecMaster()
						.setScoringReq(isTechEvaluationReq);

			}
		}
		// this.getManagedBean().setScQtCheckSt2techProposal(localCheckList);
		// specMaster.setScoringReq(isTechEvaluationReq);
	}

	/**
	 * Construct l3 ids.
	 * 
	 * @param categories
	 *            the categories
	 * @return the list
	 */
	private List<Long> constructL3Ids(List<ScQtCategory> categories) {
		List<Long> categoryIds = null;
		if (null != categories) {
			categoryIds = new ArrayList<Long>();
			for (ScQtCategory category : categories) {
				Long catId = category.getPmCategoryL3().getCategoryL3Id();
				categoryIds.add(catId);
			}
		}
		return categoryIds;
	}

	/**
	 * Gets the states n district.
	 * 
	 * @param locations
	 *            the locations
	 * @param codes
	 *            the codes
	 * @param locLevel
	 *            the loc level
	 * @return the states n district
	 */
	private void getStatesNDistrict(List<? extends TplSpecLoc> locations,
			List<String> codes, String locLevel) {

		List<PmState> states = new ArrayList<PmState>();
		List<PmDistrict> districts = new ArrayList<PmDistrict>();
		List<PmDivision> divisions = new ArrayList<PmDivision>();
		if (null != locations) {
			for (TplSpecLoc specLoc : locations) {
				if (ParameterConst.LOCATION_LEVEL_STATE
						.equalsIgnoreCase(locLevel)) {
					PmState state = specLoc.getPmState();
					if (null != state) {
						if (!codes.contains(state.getStateCode())) {
							codes.add(state.getStateCode());
							states.add(state);
						}
					}
					this.getManagedBean().setSupplierStateCode(codes);
					if (null == this.getManagedBean().getStateList()) {
						this.getManagedBean().setStateList(
								new ArrayList<PmState>());
					}
					this.getManagedBean().setStateList(states);

				} else if (ParameterConst.LOCATION_LEVEL_DISTRICT
						.equalsIgnoreCase(locLevel)) {
					PmDistrict district = specLoc.getPmDistrict();
					if (null != district) {
						if (!codes.contains(district.getDistrictCode())) {
							codes.add(district.getDistrictCode());
							districts.add(district);
						}
					}
					this.getManagedBean().setSupplierDistrictCode(codes);
					if (null == this.getManagedBean().getDistrictList()) {
						this.getManagedBean().setDistrictList(
								new ArrayList<PmDistrict>());
					}
					this.getManagedBean().setDistrictList(districts);

				} else if (ParameterConst.LOCATION_LEVEL_DIVISION
						.equalsIgnoreCase(locLevel)) {
					PmDivision divison = specLoc.getPmDivision();
					if (null != divison) {
						if (!codes.contains(divison.getDivisionCode())) {
							codes.add(divison.getDivisionCode());
							divisions.add(divison);
						}
					}
					this.getManagedBean().setSupplierDivisionCode(codes);
					if (null == this.getManagedBean().getDivisionList()) {
						this.getManagedBean().setDivisionList(
								new ArrayList<PmDivision>());
					}
					this.getManagedBean().setDivisionList(divisions);
				}
			}
		}
	}

	/**
	 * Update selected check list indx.
	 * 
	 * @param checkList
	 *            the check list
	 * @param index
	 *            the index
	 */
	public void updateSelectedCheckListIndx(ScQtCheckList checkList, int index) {
		if (checkList.isSelected()) {
			checkList.setIndex(index);
		} else {
			checkList.setIndex(-1);
		}
	}

	/**
	 * Gets the bank years display dtls.display bankstatement checklist years
	 * dropdown values.
	 * 
	 * @return List<SelectItem>.
	 */
	public List<SelectItem> getBankYearsDisplayDtls() {

		List<SelectItem> selectItems = new ArrayList<SelectItem>();

		try {
			int currentYear = Calendar.getInstance().get(Calendar.YEAR);
			int previousYear = currentYear - 1;
			int nextYear = currentYear + 1;

			selectItems.add(new SelectItem(previousYear, String
					.valueOf(previousYear)));
			selectItems.add(new SelectItem(currentYear, String
					.valueOf(currentYear)));
			selectItems.add(new SelectItem(nextYear, String.valueOf(nextYear)));
			this.getManagedBean().setBankyearsList(selectItems);

		} finally {

		}
		return this.getManagedBean().getBankyearsList();
	}

	/**
	 * Delete check list attachment file.delete uploaded checklist attachemnt
	 * from Technical and Financial Checklist menu.
	 * 
	 * @param checkListAttachment
	 *            (ScQtCheckListAttachment checkListAttachment).
	 */
	public void deleteCheckListAttachmentFile(
			ScQtCheckListAttachment checkListAttachment) {
		checkListAttachment = null;
		this.getManagedBean().getScQtChecklist()
				.setScQtCheckListAttachment(checkListAttachment);
		if (null != this.getManagedBean().getScQtChecklist()
				&& null == this.getManagedBean().getScQtChecklist()
						.getScQtCheckListAttachment()) {
			this.getManagedBean().getScQtChecklist()
					.setPmStatus(getPmStatusByStatusId(QTStatusConst.TS_PENDING_SUBMISSION));

		}

	}

	/**
	 * Retrieve sm mof account.retrive suupplier information based on supplier
	 * in restricted tender tab.
	 * 
	 * @param scQtSupplier
	 *            the sc qt supplier
	 * @param smSupplier
	 *            the sm supplier
	 * @return the sm mof account
	 */
	public SmMofAccount retrieveSmMofAccount(ScQtSupplier scQtSupplier,
			SmSupplier smSupplier) {
		// debugLogger("retrieveSmMofAccount");

		Long supplierId = null;
		SmMofAccount smMofAccount = null;
		if (scQtSupplier != null) {
			supplierId = scQtSupplier.getSmSupplier().getSupplierId();
			smMofAccount = scQtSupplier.getSmMofAccount();

			if (smMofAccount != null) {
				return smMofAccount;
			}
		} else {
			supplierId = smSupplier.getSupplierId();
			smMofAccount = smSupplier.getSmMofAccount();

			if (smMofAccount != null) {
				return smMofAccount;
			}
		}

		IMofAccountSO mofAccountSO;
		try {
			mofAccountSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_MANAGEMENT, IMofAccountSO.class);
			MofAccountReqDTO accountReqDTO = new MofAccountReqDTO();
			accountReqDTO.setSupplierId(supplierId);
			accountReqDTO
					.setSupplierType(ParameterConst.SUPPLIER_TYPE_CONTRACTOR);

			MofAccountResDTO mofAccountResDTO = mofAccountSO
					.retriveMOFAccountBySupplierIdAndType(accountReqDTO);
			if (mofAccountResDTO.getMofAccount() != null) {
				smMofAccount = mofAccountResDTO.getMofAccount();
				if (scQtSupplier != null) {
					scQtSupplier.setSmMofAccount(smMofAccount);
				} else {
					smSupplier.setSmMofAccount(smMofAccount);
				}
			}
		} finally {
			mofAccountSO = null;
		}
		return smMofAccount;
	}

	/**
	 * Show supplier details.retrive suupplier address based on supplier in
	 * restricted tender tab
	 * 
	 * @param qtSupplier
	 *            (ScQtSupplier qtSupplier).
	 */
	public void showSupplierDetails(ScQtSupplier qtSupplier) {
		// debugLogger("showSupplierDetails");
		IQTSupplierSO qtSupplierSO = null;
		try {
			qtSupplierSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTSupplierSO.class);

			QTSupplierReqDTO reqDTO = new QTSupplierReqDTO();
			reqDTO.setSupplierId(qtSupplier.getSmSupplier().getSupplierId());

			QTSupplierResDTO resDTO = qtSupplierSO
					.retrieveSupplierDtlById(reqDTO);

			this.getManagedBean().setSmSupplier(resDTO.getSmSupplier());
			this.getManagedBean().setSmMofAccount(resDTO.getSmMofAccount());
			/*
			 * List<SmPersonnel> personnelsList = resDTO.getSmSupplier()
			 * .getSmAppl().getSmPersonnels(); if (null != personnelsList) { for
			 * (SmPersonnel smPersonnel : personnelsList) {
			 * getManagedBean().setPersonnel(smPersonnel); } }
			 */
			SmCompanyBasic smCompanyBasic = resDTO.getSmSupplier().getSmAppl()
					.getSmCompanyBasic();
			for (SmCompanyAddress smCompanyAddress : smCompanyBasic
					.getSmCompanyAddresses()) {
				if (AddressType.CONTACT_ADDRESS.value().equals(
						smCompanyAddress.getAddressType())) {
					log.info("found hq address");
					getManagedBean().setSupplierBranch("HQ");
					getManagedBean().setSupplierAddress(
							smCompanyAddress.getSmAddress());
					// getManagedBean().setSupplierContact(getManagedBean().getPersonnel().getName());
					// getManagedBean().setSupplierEmail(getManagedBean().getPersonnel().getEmail());
					break;
				}
			}
		} finally {
			qtSupplierSO = null;
		}
	}

	/**
	 * Retrive one tier check list.retrive all technical Specs and display in
	 * Prepare Specification menu.
	 */
	public void retriveOneTierCheckList() {
		IQTSpecSO specSo = null;
		SpecificationResDTO resDTO = null;

		if (getManagedBean().getMarkDeletedTechChecklist() == null) {
			getManagedBean().setMarkDeletedTechChecklist(
					new ArrayList<ScQtCheckList>());
		}

		try {
			if (null == this.getManagedBean().getScQtCheckSt2techProposal()
					|| this.getManagedBean().getScQtCheckSt2techProposal()
							.isEmpty()) {
				// List<ScQtCheckList> localcheckList = new
				// ArrayList<ScQtCheckList>();
				SpecificationReqDTO reqDTO = new SpecificationReqDTO();
				specSo = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);
				reqDTO.setScQt(this.getManagedBean().getQt());
				// reqDTO.setSpecType(SourcingConstants.TECHNICAL_DOCTYPE);
				resDTO = specSo.retrieveOneTierCheckLists(reqDTO);

				this.getManagedBean().setScQtCheckSt2techProposal(
						resDTO.getSpecResultBean().getResultList());
				this.getManagedBean().setScQtCheckSt2financeProposalList(
						resDTO.getFinancialCheckLists());
				// Collections.sort(this.getManagedBean()
				// .getScQtCheckSt2financeProposalList());
			}
		} finally {
			specSo = null;
		}
	}

	/**
	 * Retriev location dtls.
	 * 
	 * @param scQtLocCriteriaList
	 *            the sc qt loc criteria list
	 */
	private void retrievLocationDtls(List<ScQtLocCriteria> scQtLocCriteriaList) {
		log.info("retrievLocationDtls - START");
		List<PmState> states = SourcingLocationHelper
				.getStateFromLocationDTO(scQtLocCriteriaList);
		List<PmDivision> divisions = SourcingLocationHelper
				.getDivisionFromLocationDTO(scQtLocCriteriaList);
		List<PmDistrict> districts = SourcingLocationHelper
				.getDistrictFromLocationDTO(scQtLocCriteriaList);
		List<PmCity> cities = SourcingLocationHelper
				.getCityFromLocationDTO(scQtLocCriteriaList);
		getManagedBean().setZoneTrees(new ArrayList<TreeNode>());
		if (ParameterConst.LOCATION_LEVEL_STATE.equalsIgnoreCase(this
				.getManagedBean().getLocalLevelCode())) {
			if (null != states && !states.isEmpty()) {
				for (PmState state : states) {
					List<PmState> localStateList = new ArrayList<PmState>();
					localStateList.add(state);
					getManagedBean().getZoneTrees().add(
							LocationSingleton.getInstance().buildLocationTree(
									null, localStateList, districts, divisions,
									cities, ScQtLocCriteria.class));
				}
			}
		} else if (ParameterConst.LOCATION_LEVEL_DISTRICT.equalsIgnoreCase(this
				.getManagedBean().getLocalLevelCode())) {
			if (null != districts && !districts.isEmpty()) {
				Map<PmState, Map<PmDivision, Map<PmDistrict, List<PmCity>>>> mapStatesDivisionsDistrictsCities = LocationSingleton
						.getInstance().getUniqueStatesDivisionsDistrictsCities(
								districts, null);
				Set<PmState> statesDivisionsDistrictsCitiesKeySet = mapStatesDivisionsDistrictsCities
						.keySet();
				Map<PmState, Map<PmDistrict, List<PmCity>>> mapStatesDistrictsCities = LocationSingleton
						.getInstance().getUniqueStateDistrictsCities(districts,
								null);
				Set<PmState> statesDistrictsCitiesKeySet = mapStatesDistrictsCities
						.keySet();
				if (statesDivisionsDistrictsCitiesKeySet != null
						&& !statesDivisionsDistrictsCitiesKeySet.isEmpty()) {
					for (PmState state : statesDivisionsDistrictsCitiesKeySet) {
						Map<PmDivision, Map<PmDistrict, List<PmCity>>> mapDivisionsDistrictsCities = mapStatesDivisionsDistrictsCities
								.get(state);
						Set<PmDivision> divisionsDistrictsCitiesKeySet = mapDivisionsDistrictsCities
								.keySet();
						if (divisionsDistrictsCitiesKeySet != null
								&& !divisionsDistrictsCitiesKeySet.isEmpty()) {
							divisions.clear();
							districts.clear();
							for (PmDivision division : divisionsDistrictsCitiesKeySet) {
								division.setPmState(state);
								Map<PmDistrict, List<PmCity>> mapDistrictsCities = mapDivisionsDistrictsCities
										.get(division);
								Set<PmDistrict> districtsCitiesKeySet = mapDistrictsCities
										.keySet();
								if (districtsCitiesKeySet != null
										&& !districtsCitiesKeySet.isEmpty()) {
									for (PmDistrict district : districtsCitiesKeySet) {
										districts.add(district);
									}
								}
							}
							getManagedBean().getZoneTrees().add(
									LocationSingleton.getInstance()
											.buildLocationTree(null, null,
													districts, divisions,
													cities,
													ScQtLocCriteria.class));
						}
					}
				}
				if (statesDistrictsCitiesKeySet != null
						&& !statesDistrictsCitiesKeySet.isEmpty()) {
					for (PmState state : statesDistrictsCitiesKeySet) {
						divisions.clear();
						Map<PmDistrict, List<PmCity>> mapDistrictsCities = mapStatesDistrictsCities
								.get(state);
						Set<PmDistrict> districtsCitiesKeySet = mapDistrictsCities
								.keySet();
						if (districtsCitiesKeySet != null
								&& !districtsCitiesKeySet.isEmpty()) {
							districts.clear();
							for (PmDistrict district : districtsCitiesKeySet) {
								district.setPmState(state);
								districts.add(district);
							}
						}
						getManagedBean().getZoneTrees().add(
								LocationSingleton.getInstance()
										.buildLocationTree(null, null,
												districts, divisions, cities,
												ScQtLocCriteria.class));
					}
				}
			}
		} else if (ParameterConst.LOCATION_LEVEL_DIVISION.equalsIgnoreCase(this
				.getManagedBean().getLocalLevelCode())) {
			if (null != divisions && !divisions.isEmpty()) {
				Map<PmState, List<PmDivision>> mapStatesDivision = LocationSingleton
						.getInstance().getUniqueStatesDivision(divisions);
				Set<PmState> statesDivisionKeySet = mapStatesDivision.keySet();
				if (statesDivisionKeySet != null
						&& !statesDivisionKeySet.isEmpty()) {
					for (PmState state : statesDivisionKeySet) {
						divisions.clear();
						List<PmDivision> divisions1 = mapStatesDivision
								.get(state);
						if (divisions1 != null && !divisions1.isEmpty()) {
							for (PmDivision division : divisions1) {
								division.setPmState(state);
								divisions.add(division);
							}
							getManagedBean().getZoneTrees().add(
									LocationSingleton.getInstance()
											.buildLocationTree(null, null,
													districts, divisions,
													cities,
													ScQtLocCriteria.class));
						}
					}
				}
			}
		}
		log.info("retrievLocationDtls - END");
	}

	/**
	 * Retrieve non zonal dtls.
	 */
	private void retrieveNonZonalDtls() {
		log.info("retrieveNonZonalDtls - START");
		if (null != this.getManagedBean().getQt().getZoneLocationLevel()
				&& null != this.getManagedBean().getQt().getZoneLocationLevel()
						.getParameterId()) {
			Long locationLevel = this.getManagedBean().getQt()
					.getZoneLocationLevel().getParameterId();
			this.getManagedBean().getQt().getScQtSupplierCriteria()
					.getLocationLevel().setParameterId(locationLevel);
			List<PmParameterDesc> localParamDesc = retrieveParameterDescByType(ParameterTypeConst.LOCATION_LEVEL);
			String locationLevelCode = null;
			for (PmParameterDesc paramDesc : localParamDesc) {
				if (paramDesc.getPmParameter().getParameterId()
						.equals(locationLevel)) {
					locationLevelCode = paramDesc.getPmParameter()
							.getParameterCode();
					this.getManagedBean().setLocalLevelCode(locationLevelCode);
					break;
				}
			}
			List<ScQtLocCriteria> scQtLocCriteriaList = new ArrayList<ScQtLocCriteria>();
			if (null != getManagedBean().getZoneGeneralTrees()
					&& !getManagedBean().getZoneGeneralTrees().isEmpty()) {
				for (TreeNode treeNode : getManagedBean().getZoneGeneralTrees()) {
					List<ScQtLocCriteria> qtLocCriteriaList = getLocationsList(treeNode);
					scQtLocCriteriaList.addAll(qtLocCriteriaList);
				}
			}
			retrievLocationDtls(scQtLocCriteriaList);
		}
		log.info("retrieveNonZonalDtls - END");
	}

	/**
	 * technical checklist score change.Event handle check/Uncheck score
	 * checkbox in technical checklist based on score you will display
	 * specification status.
	 * 
	 * @param index
	 *            (int index)
	 * @param checklist
	 *            (List<ScQtCheckList> checklist).
	 */

	public void technicalCheckListScoreReqChange(int index,
			List<ScQtCheckList> checklist) {
		ScQtCheckList list = checklist.get(index);

		if (((null != list.getDefinationType()) && (!ParameterConst.DOCUMENT_DEFINITION_TYPE_UPLOAD
				.equalsIgnoreCase(list.getDefinationType().getParameterCode())
				&& !ParameterConst.DOCUMENT_DEFINITION_TYPE_SAMPLE
						.equalsIgnoreCase(list.getDefinationType()
								.getParameterCode())
				&& !ParameterConst.DOCUMENT_DEFINITION_TYPE_NO_ACTION
						.equalsIgnoreCase(list.getDefinationType()
								.getParameterCode()) && !ParameterConst.DOCUMENT_DEFINITION_TYPE_PRESENTATION_DEMO
					.equalsIgnoreCase(list.getDefinationType()
							.getParameterCode())))) {
			Long statusId = null;
			if (list.isScoringReq()) {
				statusId = QTStatusConst.TS_PENDING_SUBMISSION;
			} else if (!list.isScoringReq()) {
				statusId = QTStatusConst.TS_COMPLETED;
			}
			list.setPmStatus(getPmStatusByStatusId(statusId));
		}

		this.getManagedBean().getScQtCheckSt2techProposal().set(index, list);
	}

	/**
	 * financial checklist score change.Event handle check/Uncheck score
	 * checkbox in financial checklist based on score you will display
	 * specification status.
	 * 
	 * @param index
	 *            (int index)
	 * @param checklist
	 *            (List<ScQtCheckList> checklist).
	 */

	public void financialCheckListScoreReqChange(int index,
			List<ScQtCheckList> checklist) {
		ScQtCheckList list = checklist.get(index);

		if ((null != list.getDefinationType())
				&& (!ParameterConst.DOCUMENT_DEFINITION_TYPE_UPLOAD
						.equalsIgnoreCase(list.getDefinationType()
								.getParameterCode()) && !ParameterConst.DOCUMENT_DEFINITION_TYPE_NO_ACTION
						.equalsIgnoreCase(list.getDefinationType()
								.getParameterCode()))) {
			Long statusId = null;
			if (list.isScoringReq()) {
				statusId = QTStatusConst.TS_PENDING_SUBMISSION;
			} else if (!list.isScoringReq()) {
				statusId = QTStatusConst.TS_COMPLETED;
			}
			list.setPmStatus(getPmStatusByStatusId(statusId));
		}

		this.getManagedBean().getScQtCheckSt2financeProposalList()
				.set(index, list);
	}

	/**
	 * Gets the count.
	 * 
	 * @return the count
	 */
	public int getCount() {
		return count;
	}

	/**
	 * Sets the count.
	 * 
	 * @param count
	 *            the new count
	 */
	public void setCount(int count) {
		this.count = count;
	}

	/**
	 * Method to check all the state/districts in data table.
	 * 
	 * @param event
	 *            the event
	 */
	public void checkAllConditions(AjaxBehaviorEvent event) {
		boolean flag = ((SelectBooleanCheckbox) event.getSource()).isSelected();
		List<TreeNode> list = getManagedBean().getZoneTrees();
		if (null != list) {
			for (TreeNode node : list) {
				for (TreeNode childNode : node.getChildren()) {
					if (childNode.getChildCount() > 0) {
						for (TreeNode node2 : childNode.getChildren()) {
							node2.setSelected(flag);
						}
					}
					childNode.setSelected(flag);
				}
			}
			getManagedBean().setZoneTrees(list);
		} else {

		}
	}

	/**
	 * Method to check all the state/districts in data table in dialog.
	 * 
	 * @param isCheck
	 */
	public void checkAllPopUpConditions(boolean ischeck) {
		// boolean flag = ((SelectBooleanCheckbox)
		// event.getSource()).isSelected();
		TreeNode treeNode = getManagedBean().getRootLocationTree();
		if (treeNode != null) {
			for (TreeNode stateNode : treeNode.getChildren()) {
				if (stateNode.getChildCount() > 0) {
					for (TreeNode districtNode : stateNode.getChildren()) {
						districtNode.setSelected(ischeck);
					}
				}
				stateNode.setSelected(ischeck);
			}
			getManagedBean().setRootLocationTree(treeNode);
		} else {

		}
	}

	/**
	 * Method to check all the state/districts in data table in dialog.
	 * 
	 * @param isCheck
	 */
	public void checkAllZoneGeneralPopUp(boolean ischeck) {
		// boolean flag = ((SelectBooleanCheckbox)
		// event.getSource()).isSelected();
		TreeNode treeNode = getManagedBean().getRootZoanlTree();
		if (treeNode != null) {
			for (TreeNode stateNode : treeNode.getChildren()) {
				if (stateNode.getChildCount() > 0) {
					for (TreeNode districtNode : stateNode.getChildren()) {
						districtNode.setSelected(ischeck);
					}
				}
				stateNode.setSelected(ischeck);
			}
			getManagedBean().setRootZoanlTree(treeNode);
		} else {

		}
	}

	/**
	 * Method to prepare qt search criteria.
	 * 
	 * @param templateQtnTenderSearchBean
	 *            the qt search criteria bean
	 * @param qtSearchBean
	 *            the qt search filter bean
	 */
	private void populateQTSearchCriteria(
			QtnTenderSearchBean templateQtnTenderSearchBean,
			ISearchBean<TplSpecMaster> qtSearchBean) {

		if (templateQtnTenderSearchBean != null) {
			List<SearchFilterBean> searchFilterBean = new ArrayList<SearchFilterBean>();
			String qtTitle = templateQtnTenderSearchBean.getQtTitle();
			if (!StringUtils.isBlank(qtTitle)) {

				qtSearchBean.getDataFilterBeans().add(
						new SearchFilterBean("scQtDoc.scQt.qtTitle",
								SearchOperator.OPERATOR_LIKE.value(), qtTitle,
								true));
				qtSearchBean.getDataFilterBeans().add(
						new SearchFilterBean("scQtDoc.scQt.qtTitle",
								SearchOperator.OPERATOR_LIKE.value(), qtTitle,
								true));
			}
			String qtNumber = templateQtnTenderSearchBean.getQtNo();
			if (!StringUtils.isBlank(qtNumber)) {
				qtSearchBean.getDataFilterBeans().add(
						new SearchFilterBean("scQtDoc.scQt.qtNo",
								SearchOperator.OPERATOR_LIKE.value(), qtNumber,
								true));
			}
			String procurementMode = templateQtnTenderSearchBean
					.getProcurementMode();
			if (!StringUtils.isBlank(procurementMode)) {
				qtSearchBean.getDataFilterBeans().add(
						new SearchFilterBean(
								"scQtDoc.scQt.procurementMode.parameterCode",
								SearchOperator.OPERATOR_EQUALS.value(),
								procurementMode));
			}
			Date fromCreatedDate = templateQtnTenderSearchBean
					.getFromCreatedDate();
			Date toCreatedDate = templateQtnTenderSearchBean.getToCreatedDate();
			if (null != fromCreatedDate || toCreatedDate != null) {
				Calendar c1 = Calendar.getInstance();
				c1.setTime(fromCreatedDate);
				Calendar fromDate = Calendar.getInstance();
				fromDate.set(c1.get(Calendar.YEAR), c1.get(Calendar.MONTH),
						c1.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
				fromDate.set(Calendar.MILLISECOND, 0);
				fromCreatedDate = fromDate.getTime();
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
				bean.setSearchField("scQtDoc.scQt.createdDate");
				bean.setSearchOperator(SearchOperator.OPERATOR_BETWEEN.value());
				SearchBetweenValue<Date> betweenVal = new SearchBetweenValue<Date>(
						fromCreatedDate, toCreatedDate);
				bean.setSearchValue(betweenVal);
				searchFilterBean.add(bean);
			}
			Date fromPublishDate = templateQtnTenderSearchBean
					.getFromPublishDate();
			Date toPublishDate = templateQtnTenderSearchBean.getToPublishDate();
			if (null != fromPublishDate || null != toPublishDate) {
				SearchFilterBean bean = new SearchFilterBean();
				bean.setSearchField("scQtDoc.scQt.publishDate");
				bean.setSearchOperator(SearchOperator.OPERATOR_BETWEEN.value());
				SearchBetweenValue<Date> betweenVal = new SearchBetweenValue<Date>(
						fromPublishDate, toPublishDate);
				bean.setSearchValue(betweenVal);
				searchFilterBean.add(bean);
			}
			Date fromClosingDate = templateQtnTenderSearchBean
					.getFromClosingDate();
			Date toClosingDate = templateQtnTenderSearchBean.getToClosingDate();
			if (null != fromClosingDate || null != toClosingDate) {
				SearchFilterBean bean = new SearchFilterBean();
				bean.setSearchField("scQtDoc.scQt.closingDate");
				bean.setSearchOperator(SearchOperator.OPERATOR_BETWEEN.value());
				SearchBetweenValue<Date> betweenVal = new SearchBetweenValue<Date>(
						fromClosingDate, toClosingDate);
				bean.setSearchValue(betweenVal);
				searchFilterBean.add(bean);
			}
			String status = templateQtnTenderSearchBean.getQtStatus();
			if (!StringUtils.isBlank(status)) {
				qtSearchBean
						.getDataFilterBeans()
						.add(new SearchFilterBean(
								"scQtDoc.scQt.scQtStatus.pmStatus.statusId",
								SearchOperator.OPERATOR_EQUALS.value(), status));
				qtSearchBean.getDataFilterBeans().add(
						new SearchFilterBean("scQtDoc.scQt.scQtStatus.current",
								SearchOperator.OPERATOR_EQUALS.value(), true));
			}
			// Adding search filterbeans to searchbean.
			qtSearchBean.getDataFilterBeans().addAll(searchFilterBean);
		}
	}

	// validate Technical Scoring passing score
	private void validateTechnicalPassingScoring(
			ScQtScoreSetting technicalScoreSetting) {
		try {
			if (null != technicalScoreSetting
					&& technicalScoreSetting.getSettingType() == 1) {

				if (null == technicalScoreSetting.getPassingScore()) {
					this.getManagedBean().setActiveIndex(4);
					throw new EpException(MessageConst.ERROR_760192);
				} else {

					if (technicalScoreSetting.getPassingScore().intValue() <= 0) {
						this.getManagedBean().setActiveIndex(4);
						throw new EpException(MessageConst.ERROR_760234);
					}

				}

				if (null != technicalScoreSetting.getPassingScore()
						&& technicalScoreSetting.getPassingScore().intValue() > 100) {
					this.getManagedBean().setActiveIndex(4);
					throw new EpException(MessageConst.ERROR_760142);
				}
				BigDecimal passingScore = retrieveTechnicalEvalutionPassingScorePmrules(RuleTypeConst.SC_TECHNICAL_PASSING_SCORE);

				if (null != passingScore
						&& (technicalScoreSetting.getPassingScore().intValue() < passingScore
								.intValue())) {
					throw new EpException(MessageConst.ERROR_760364,
							new String[] { String.valueOf(passingScore) });
				}
			}

		} finally {

		}
	}

	// validate Presenttataion Demo Passing scores.
	private void validateTechnicalDemoPassingScoring(
			ScQtScoreSetting demoScoreSetting) {
		try {
			if (null != demoScoreSetting
					&& demoScoreSetting.getSettingType() == 3) {
				if (this.getManagedBean().getQt().isDemoRequired()
						&& this.getManagedBean()
								.getQt()
								.getEvaluationType()
								.equalsIgnoreCase(
										ParameterConst.EVALUATION_TYPES_TWO_TIER)) {
					if (null == demoScoreSetting.getPassingScore()) {
						//demoScoreSetting.setPassingScore(new BigDecimal(0));
						this.getManagedBean().setActiveIndex(4);
						throw new EpException(MessageConst.ERROR_760173);
					}
					if (demoScoreSetting.getPassingScore().intValue() < 0) {
						this.getManagedBean().setActiveIndex(4);
						throw new EpException(MessageConst.ERROR_760173);
					}
					if (null != demoScoreSetting.getPassingScore()
							&& demoScoreSetting.getPassingScore().intValue() > 100) {
						this.getManagedBean().setActiveIndex(4);
						throw new EpException(MessageConst.ERROR_760144);
					}
				}

			}

		} finally {

		}

	}

	// validate Financial Package and Item Passing scores.
	private void validateFinancialPackageItemPassingScoring(
			ScQtScoreSetting financialPackageScore,
			ScQtScoreSetting financialItemScore) {
		try {
			if (null != financialPackageScore
					&& financialPackageScore.getSettingType() == 2) {

				if (null == financialPackageScore.getPassingScore()) {

					this.getManagedBean().setActiveIndex(5);
					throw new EpException(MessageConst.ERROR_760193);
				} else {

					if (financialPackageScore.getPassingScore().intValue() < 0) {
						this.getManagedBean().setActiveIndex(5);
						throw new EpException(MessageConst.ERROR_760174);
					}

				}

				if (null != financialPackageScore.getPassingScore()
						&& financialPackageScore.getPassingScore().intValue() > 100) {
					this.getManagedBean().setActiveIndex(5);
					throw new EpException(MessageConst.ERROR_760143);
				}

			}

			if (null != financialItemScore
					&& financialItemScore.getSettingType() == 6) {
				if (null == financialItemScore.getPassingScore()) {

					this.getManagedBean().setActiveIndex(5);
					throw new EpException(MessageConst.ERROR_760258);

				}

			}

		} finally {

		}

	}

	// delete checklist scores
	public void deleteCheckListScoreContrats() {

		markDeleteSelectedRowsAction(this.getManagedBean().getCheckListScore(),
				this.getManagedBean().getToBeRemoveList());

	}

	// delete checklist scores
	public void deleteCheckListScoreYears() {
		markDeleteSelectedRowsAction(
				this.getManagedBean().getScoringForYears(), this
						.getManagedBean().getToBeRemoveList());
	}

	// delete technical Checklist List
	public void deleteTechnicalCheckList() {
		IParameterSO parameterSo = BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IParameterSO.class);
		if (null != this.getManagedBean().getScQtCheckSt2techProposal()
				&& !this.getManagedBean().getScQtCheckSt2techProposal()
						.isEmpty()) {
			for (ScQtCheckList technicalCheckList : this.getManagedBean()
					.getScQtCheckSt2techProposal()) {
				if (null == technicalCheckList.getPmDocument()
						&& !QTConstants.TEMPLETE_SPEC_CODE
								.equalsIgnoreCase(technicalCheckList
										.getDefinationType().getParameterCode())) {
					if (null != technicalCheckList.getDefinationType()
							&& null == technicalCheckList.getDefinationType()
									.getParameterId()) {
						ParameterReqDTO docDefTypeReqDTO = new ParameterReqDTO();
						docDefTypeReqDTO
								.setParameterType(ParameterTypeConst.DOCUMENT_DEFINITION_TYPE);
						docDefTypeReqDTO
								.setParameterCode(ParameterConst.DOCUMENT_DEFINITION_TYPE_UPLOAD);
						ParameterResDTO docDefTypeResDTO = parameterSo
								.retrieveParameterByTypeAndCode(docDefTypeReqDTO);
						technicalCheckList.setDefinationType(docDefTypeResDTO
								.getPmParameter());
					}
					if (null != technicalCheckList.getAction()
							&& null == technicalCheckList.getAction()
									.getParameterId()) {
						ParameterReqDTO docDefTypeReqDTO = new ParameterReqDTO();
						docDefTypeReqDTO
								.setParameterType(ParameterTypeConst.SC_SUPPLIER_ACTION);
						docDefTypeReqDTO
								.setParameterCode(ParameterConst.CHECKLIST_ACTION_ONLINE_SUBMISSION);
						ParameterResDTO docDefTypeResDTO = parameterSo
								.retrieveParameterByTypeAndCode(docDefTypeReqDTO);
						technicalCheckList.setAction(docDefTypeResDTO
								.getPmParameter());
					}

				}
			}
		}
		List<ScQtCheckList> deleteTechCheckList = markDeleteSelectedRowsActionById(
				this.getManagedBean().getScQtCheckSt2techProposal(), null);
		List<ScQtCheckList> technical = this.getManagedBean()
				.getMarkDeletedTechChecklist();
		if (null == technical) {
			technical = new ArrayList<ScQtCheckList>();
		}
		if (null != deleteTechCheckList && !deleteTechCheckList.isEmpty()) {
			technical.addAll(deleteTechCheckList);
			this.getManagedBean().setMarkDeletedTechChecklist(technical);
		}
	}

	// delete Financial Checklist List
	public void deleteFinancialCheckList() {
		IParameterSO parameterSo = BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IParameterSO.class);
		if (null != this.getManagedBean().getScQtCheckSt2financeProposalList()
				&& !this.getManagedBean().getScQtCheckSt2financeProposalList()
						.isEmpty()) {
			for (ScQtCheckList financialCheckList : this.getManagedBean()
					.getScQtCheckSt2financeProposalList()) {
				if (null == financialCheckList.getPmDocument()
						&& !QTConstants.TEMPLETE_SPEC_CODE
								.equalsIgnoreCase(financialCheckList
										.getDefinationType().getParameterCode())) {
					if (null != financialCheckList.getDefinationType()
							&& null == financialCheckList.getDefinationType()
									.getParameterId()) {
						ParameterReqDTO docDefTypeReqDTO = new ParameterReqDTO();
						docDefTypeReqDTO
								.setParameterType(ParameterTypeConst.DOCUMENT_DEFINITION_TYPE);
						docDefTypeReqDTO
								.setParameterCode(ParameterConst.DOCUMENT_DEFINITION_TYPE_UPLOAD);
						ParameterResDTO docDefTypeResDTO = parameterSo
								.retrieveParameterByTypeAndCode(docDefTypeReqDTO);
						financialCheckList.setDefinationType(docDefTypeResDTO
								.getPmParameter());
					}
					if (null != financialCheckList.getAction()
							&& null == financialCheckList.getAction()
									.getParameterId()) {
						ParameterReqDTO docDefTypeReqDTO = new ParameterReqDTO();
						docDefTypeReqDTO
								.setParameterType(ParameterTypeConst.SC_SUPPLIER_ACTION);
						docDefTypeReqDTO
								.setParameterCode(ParameterConst.CHECKLIST_ACTION_ONLINE_SUBMISSION);
						ParameterResDTO docDefTypeResDTO = parameterSo
								.retrieveParameterByTypeAndCode(docDefTypeReqDTO);
						financialCheckList.setAction(docDefTypeResDTO
								.getPmParameter());
					}

				}
			}
		}
		List<ScQtCheckList> deletefinCheckList = markDeleteSelectedRowsActionById(
				this.getManagedBean().getScQtCheckSt2financeProposalList(),
				null);
		List<ScQtCheckList> financial = this.getManagedBean()
				.getMarkDeletedFinChecklist();
		if (null == financial) {
			financial = new ArrayList<ScQtCheckList>();
		}
		if (null != deletefinCheckList && !deletefinCheckList.isEmpty()) {
			financial.addAll(deletefinCheckList);
			this.getManagedBean().setMarkDeletedFinChecklist(financial);
		}

	}

	// changed delivery details tabs
	public void deliveryDetailsZonesTabChange(TabChangeEvent event) {
		BriefingZoneBean specZoneDtl = (BriefingZoneBean) event.getData();
		this.getManagedBean()
				.setTplSpecZoneDtl(specZoneDtl.getTplSpecZoneDtl());
	}

	// this method is check for Technical Standard Checklist Exist or not
	private boolean validateTechnicalStandardCheckList() {
		boolean pmdocument = false;
		List<ScQtCheckList> techCheckList = this.getManagedBean()
				.getScQtCheckSt2techProposal();
		if (null != techCheckList && !techCheckList.isEmpty()) {

			for (ScQtCheckList documentTechnicalCheckList : techCheckList) {
				if (null != documentTechnicalCheckList.getPmDocument()) {
					pmdocument = true;
					break;
				}

			}

			if (!pmdocument) {
				RequestContext.getCurrentInstance().execute(
						"technicalStandardChecklistDlgs.show()");
			}

		}
		return pmdocument;
	}

	// this method is check for Financial Standard Checklist Exist or not
	private boolean validateFinancialStandardCheckList() {
		boolean pmdocument = false;
		List<ScQtCheckList> financialCheckList = this.getManagedBean()
				.getScQtCheckSt2financeProposalList();
		if (null != financialCheckList && !financialCheckList.isEmpty()) {

			for (ScQtCheckList documentTechnicalCheckList : financialCheckList) {
				if (null != documentTechnicalCheckList.getPmDocument()) {
					pmdocument = true;
					break;
				}

			}
			if (!pmdocument) {
				RequestContext.getCurrentInstance().execute(
						"financialStandardChecklistDlgs.show()");
			}

		}
		return pmdocument;
	}

	public void checkListYesConfirmation(String stage) {
		this.getManagedBean().setStandardTemplate(
				ParameterConst.TPL_DOC_PROCESS_TYPE_STANDARD);
		if (this.getManagedBean().getCheckListYesIndex() ==4 || this.getManagedBean().getCheckListYesIndex() ==5 || this.getManagedBean().getCheckListYesIndex() ==6) {
			checktemplateSpecStatus(this.getManagedBean().getQt());
			checkFinancialSpecStatus(this.getManagedBean().getQt());
		}
		this.getManagedBean().setActiveIndex(
				this.getManagedBean().getCheckListYesIndex());
	}

	public void checkListNoConfirmation(String stage) {
		this.getManagedBean().setStandardTemplate(
				ParameterConst.TPL_DOC_PROCESS_TYPE_STANDARD);
		if (ParameterConst.SPECIFICATION_TYPE_TECHNICAL.equalsIgnoreCase(stage)) {
			this.getManagedBean().setActiveIndex(2);

		} else if (ParameterConst.SPECIFICATION_TYPE_FINANCIAL.equalsIgnoreCase(stage)) {
			this.getManagedBean().setActiveIndex(3);

		}

	}

	// this method is check for Technical Standard Checklist Exist or not
	private boolean validateTechnicalStandardCheckListSubmit() {
		boolean pmdocument = false;
		retriveTechnicalCheckList();
		List<ScQtCheckList> techCheckList = this.getManagedBean()
				.getScQtCheckSt2techProposal();
		if (null != techCheckList && !techCheckList.isEmpty()) {

			for (ScQtCheckList documentTechnicalCheckList : techCheckList) {
				if (null != documentTechnicalCheckList.getPmDocument()) {
					pmdocument = true;
					break;
				}

			}

		}
		return pmdocument;
	}

	// this method is check for Financial Standard Checklist Exist or not
	private boolean validateFinancialStandardCheckListSubmit() {
		boolean pmdocument = false;
		retriveFinancialCheckList();
		List<ScQtCheckList> financialCheckList = this.getManagedBean()
				.getScQtCheckSt2financeProposalList();
		if (null != financialCheckList && !financialCheckList.isEmpty()) {

			for (ScQtCheckList documentTechnicalCheckList : financialCheckList) {
				if (null != documentTechnicalCheckList.getPmDocument()) {
					pmdocument = true;
					break;
				}

			}

		}
		return pmdocument;
	}

	public void checkListNoConfirmationSubmit(String stage) {

		if (ParameterConst.SPECIFICATION_TYPE_TECHNICAL.equalsIgnoreCase(stage)) {
			this.getManagedBean().setTechnicalOtherChecklistSubmit(false);
			this.getManagedBean().setActiveIndex(2);

		} else if (ParameterConst.SPECIFICATION_TYPE_FINANCIAL.equalsIgnoreCase(stage)) {
			this.getManagedBean().setFinancialOtherChecklistSubmit(false);
			this.getManagedBean().setActiveIndex(3);

		}
	}

	public String checkListYesConfirmationSubmit(String stage) {
		String checklistSubmit = CommonUIConst.ACKNOWLEDGE_PAGE;
		if (ParameterConst.SPECIFICATION_TYPE_TECHNICAL.equalsIgnoreCase(stage)) {
			this.getManagedBean().setTechnicalOtherChecklistSubmit(true);
			boolean financial = validateFinancialStandardCheckListSubmit();
			if (!financial
					&& !this.getManagedBean().isFinancialOtherChecklistSubmit()) {
				RequestContext.getCurrentInstance().execute(
						"financialStandardChecklistDlgsSubmit.show()");
				return null;
			} else {
				specCommitteBpmTask(BpmOutcomeConst.BPM_SUBMIT);
				RequestContext.getCurrentInstance().execute(
						"technicalStandardChecklistDlgsSubmit.hide();");
			}

		} else if (ParameterConst.SPECIFICATION_TYPE_FINANCIAL.equalsIgnoreCase(stage)) {
			this.getManagedBean().setFinancialOtherChecklistSubmit(true);
			specCommitteBpmTask(BpmOutcomeConst.BPM_SUBMIT);
			RequestContext.getCurrentInstance().execute(
					"financialStandardChecklistDlgsSubmit.hide()");

		}

		return checklistSubmit;

	}

	private void removefullfillmentDetails(String scheduleType) {

		if (ParameterConst.PERIODIC_SCHEDULE_TYPE_FREQUENCY_SCHEDULE
				.equalsIgnoreCase(scheduleType)) {
			List<ScQtSchedule> scheduleList = this.getManagedBean()
					.getScheduleList();

			if (CollectionUtils.isNotEmpty(scheduleList)) {
				List<ScQtSchedule> toBeRemoveList = new ArrayList<ScQtSchedule>();

				for (ScQtSchedule qtSchedule : scheduleList) {
					if (null != qtSchedule.getQtScheduleId()) {
						qtSchedule.setDeleted(true);
						toBeRemoveList.add(qtSchedule);
					}
				}
				this.getManagedBean().setToBeRemoveSchedules(toBeRemoveList);
				this.getManagedBean().getQt().setScQtSchedules(null);
			}
			this.getManagedBean()
					.setScheduleList(new ArrayList<ScQtSchedule>());
		} else if (ParameterConst.PERIODIC_SCHEDULE_TYPE_PROGRESSIVE_PAYMENT
				.equalsIgnoreCase(scheduleType)) {
			List<ScQtProgressivePayment> payments = this.getManagedBean()
					.getProgressivePaymentList();
			if (CollectionUtils.isNotEmpty(payments)) {
				List<ScQtProgressivePayment> toBeRemoveList = new ArrayList<ScQtProgressivePayment>();

				for (ScQtProgressivePayment payment : payments) {
					if (null != payment.getQtPaymentId()) {
						payment.setDeleted(true);
						toBeRemoveList.add(payment);
					}
				}
				this.getManagedBean().setToBeRemoveProgPayments(toBeRemoveList);
				this.getManagedBean().getQt().setScQtProgressivePayments(null);
			}
			this.getManagedBean().setProgressivePaymentList(
					new ArrayList<ScQtProgressivePayment>());
		}

	}

	public void cancelPopup() {
		this.getManagedBean().setAutoUpdate(true);

	}

	private void updateFinacialSpecStatus() {
		try {
			List<ScQtCheckList> financialSpecCheckList = this.getManagedBean()
					.getScQtCheckSt2financeProposalList();
			if (null == financialSpecCheckList
					|| financialSpecCheckList.isEmpty()) {
				retriveFinancialSpecification();
				financialSpecCheckList = this.getManagedBean()
						.getScQtCheckSt2financeProposalList();
			}
			if (null != financialSpecCheckList
					&& !financialSpecCheckList.isEmpty()) {
				boolean financialSpecZonesEstimatedpriceUpdate = Boolean.FALSE;
				this.getManagedBean().setZoneSpecUpdate(Boolean.TRUE);
				for (ScQtCheckList financialchecklist : financialSpecCheckList) {
					if (null != financialchecklist.getPmStatus()
							&& QTStatusConst.TS_COMPLETED
									.equals(financialchecklist.getPmStatus()
											.getStatusId())) {
						financialSpecZonesEstimatedpriceUpdate = Boolean.TRUE;
						this.getManagedBean().setZonesWarnMessageUpdate(
								Boolean.TRUE);
						this.getManagedBean().setZoneSpecUpdate(Boolean.TRUE);
						financialchecklist.setPmStatus(getPmStatusByStatusId(QTStatusConst.TS_PENDING_SUBMISSION));

					}

				}
				if (financialSpecZonesEstimatedpriceUpdate == true) {
					ZonesUpdateWarningMessage(this.getManagedBean()
							.isZonesWarnMessageUpdate());

				}
			}
		} finally {

		}

	}

	private void ZonesUpdateWarningMessage(boolean warningMessage) {
		if (warningMessage == true) {
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_WARN, null,
							MessageUtils.getMessage(MessageConst.WARN_760328,
									MessageUtils.BUNDLE_COMMON_MESSAGES,
									new FacesScopes().getUserLocale())));
		}

	}

	private boolean retriveExistingTemplatespec(List<ScQtCheckList> checkList) {
		if (null != checkList && !checkList.isEmpty()) {
			for (ScQtCheckList specChecklist : checkList) {
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

						return resDTO.getSpecMaster().isWeightageReq();

					}
				}
			}
		}
		return false;
	}

	/**
	 * Checks if is atleast one serive req.check service spec is existing or not
	 * in Technical checklist.
	 * 
	 * @param checklist
	 *            (List<ScQtCheckList> checklist)
	 * @return true, if is atleast one serive req
	 */
	private boolean isAtleastTechnicalSpec(List<ScQtCheckList> checklist) {
		int i = 0;
		if (null != checklist && !checklist.isEmpty()) {
			for (ScQtCheckList specChecklist : checklist) {
				if (QTConstants.TEMPLETE_SPEC_CODE
						.equalsIgnoreCase(specChecklist.getDefinationType()
								.getParameterCode())) {
					if (null != specChecklist.getScQtDoc()) {
						i++;
						if (i >= 1) {
							return true;
						}

					}
				}
			}
		}

		return false;
	}

	private void isOverlappingScore(List<ScQtCheckListScore> checkListScorings) {
		Collections.sort(checkListScorings);

		ScQtCheckListScore prevScore = null;
		ScQtCheckListScore lastObject = null;
		String criteriaType = null;
		for (int row = 0; row < checkListScorings.size(); row++) {
			ScQtCheckListScore currScoring = checkListScorings.get(row);
			if (null == lastObject) {
				lastObject = checkListScorings
						.get(checkListScorings.size() - 1);
			}

			if (currScoring.getRangeFrom() != null
					&& currScoring.getRangeTo() != null
					&& currScoring.getRangeFrom().compareTo(
							currScoring.getRangeTo()) >= 0) {
				/*throw new EpException(MessageConst.ERROR_100033, new String[] {
						"To", "From at row " + (row + 1) });*/
				throw new EpException(MessageConst.ERROR_760403, 
						new String[]{ 
						ResourceBundleUtils.getMessage(
								"lbl.sc.from", 
								ResourceBundleUtils.BUNDLE_COMMON_LABELS,
								new FacesScopes().getUserLocale()), 
						ResourceBundleUtils.getMessage(
								"lbl.sc.to", 
								ResourceBundleUtils.BUNDLE_COMMON_LABELS,
								new FacesScopes().getUserLocale()) ,
						 row+1 +" " });
			}

			if (prevScore != null) {

				if (currScoring.getRangeFrom() != null
						&& currScoring.getRangeTo() != null
						&& prevScore.getRangeFrom() != null
						&& prevScore.getRangeTo() != null) {
					if (null == criteriaType) {
						criteriaType = String.valueOf(prevScore
								.getCriteriaType());
					}

					if ("50".equalsIgnoreCase(criteriaType)
							|| "60".equalsIgnoreCase(criteriaType)
							|| "61".equalsIgnoreCase(criteriaType)) {
						if (prevScore.getRangeTo()
								.add(BigDecimal.valueOf(0.01d))
								.compareTo(currScoring.getRangeFrom()) != 0) {
							throw new EpException(MessageConst.ERROR_100055,
									new String[] { "From", "To", row + 1 + "" });
						}
					} else {
						if (prevScore.getRangeTo().add(BigDecimal.valueOf(1))
								.compareTo(currScoring.getRangeFrom()) != 0) {
							throw new EpException(MessageConst.ERROR_100055,
									new String[] { "From", "To", row + 1 + "" });
						}
					}
				} else {
					if (null == criteriaType) {
						criteriaType = String.valueOf(prevScore
								.getCriteriaType());
					}
					if (null != lastObject && null != prevScore.getRangeTo()
							&& null != lastObject.getRangeFrom()) {
						if ("50".equalsIgnoreCase(criteriaType)
								|| "60".equalsIgnoreCase(criteriaType)
								|| "61".equalsIgnoreCase(criteriaType)) {
							if (prevScore.getRangeTo()
									.add(BigDecimal.valueOf(0.01d))
									.compareTo(lastObject.getRangeFrom()) != 0) {
								throw new EpException(
										MessageConst.ERROR_100055,
										new String[] { "From", "To",
												row + 1 + "" });
							}
						} else {
							if (prevScore.getRangeTo()
									.add(BigDecimal.valueOf(1))
									.compareTo(lastObject.getRangeFrom()) != 0) {
								throw new EpException(
										MessageConst.ERROR_100055,
										new String[] { "From", "To",
												row + 1 + "" });
							}
						}

					}
				}
			}

			prevScore = currScoring;
		}
	}

	/**
	 * Change Supplier Type In technical and Financial CheckList get Supplier
	 * action dropdownvalues.
	 * 
	 * @return SelectItem[]
	 */
	public SelectItem[] getPTJUploadDropDownActionValues() {

		SelectItem[] uploaditems = null;
		try {
			ISearchBean<PmParameterDesc> searchBean = new SearchBean<PmParameterDesc>();
			List<String> recommendList = new ArrayList<String>();
			recommendList.add(ParameterConst.CHECKLIST_ACTION_VIEW);
			recommendList
					.add(ParameterConst.CHECKLIST_ACTION_ONLINE_SUBMISSION);

			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("pmParameter.parameterCode",
							SearchOperator.OPERATOR_IN.value(), recommendList));
			uploaditems = getDropdownValuesBySearchBean(
					ParameterTypeConst.SC_SUPPLIER_ACTION, searchBean);
		} finally {

		}
		return uploaditems;
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
			if (configRuleValue != null && configRuleValue.getRuleValue() != null) {
				technicalPassingScore = new BigDecimal(
						configRuleValue.getRuleValue());
			}
		} else {
			throw new EpException(MessageConst.ERROR_770222, new String[]{getRuleTypeDesc(ruleReqDTO.getRuleType())});
		}
		
		return technicalPassingScore;
	}

	private void calculateRecommendationPackageAndItemScores(
			ScQtScoreSetting recommendationPackage,
			ScQtScoreSetting recommendationItem) {
		if (null != recommendationPackage
				&& null != recommendationPackage.getOverallTechnical()
				&& recommendationPackage.getOverallTechnical().intValue() < 50) {
			this.getManagedBean().setActiveIndex(6);
			throw new EpException(MessageConst.ERROR_760365);
		}
		if (null != recommendationItem) {
			int itemPassingScore = 0;
			int value1=recommendationItem.getOverallTechnical()!=null?recommendationItem.getOverallTechnical().intValue():0;
			int value2= recommendationItem.getOverallOtherTechnical()!=null? recommendationItem.getOverallOtherTechnical().intValue():0;
				itemPassingScore = value1
						+ value2;
			
			if (itemPassingScore < 50) {
				this.getManagedBean().setActiveIndex(6);
				throw new EpException(MessageConst.ERROR_760366);
			}
		}
	}

	/**
	 * Upload Tehnical or Financial Upload UserScoring Schema Files.
	 * 
	 */
	public void technicalUserScoringSchemaFileUpload(FileUploadEvent event)
			throws FileNotFoundException, IOException {
		System.out.println("====upload Scoring Schema Type===="
				+ event.getFile().getFileName());
		ScTechnicalUserScoringSchemaAttachment attachment = this
				.getManagedBean().getQt()
				.getTechnicalUserScoringSchemaAttachment();

		if (null == attachment) {
			attachment = new ScTechnicalUserScoringSchemaAttachment();
			attachment.setScQt(this.getManagedBean().getQt());
		}
		attachment.setDocNo(this.getManagedBean().getQt().getQtNo());

		uploadAttachment(event.getFile(), attachment);
		this.getManagedBean().getQt()
				.setTechnicalUserScoringSchemaAttachment(attachment);

	}

	/**
	 * Upload Tehnical or Financial Upload UserScoring Schema Files.
	 * 
	 */
	public void financialUserScoringSchemaFileUpload(FileUploadEvent event)
			throws FileNotFoundException, IOException {
		System.out.println("====upload Scoring Schema Type===="
				+ event.getFile().getFileName());
		ScFinancialUserScoringSchemaAttachment attachment = this
				.getManagedBean().getQt()
				.getFinancialUserScoringSchemaAttachment();

		if (null == attachment) {
			attachment = new ScFinancialUserScoringSchemaAttachment();
			attachment.setScQt(this.getManagedBean().getQt());
		}
		attachment.setDocNo(this.getManagedBean().getQt().getQtNo());

		uploadAttachment(event.getFile(), attachment);
		this.getManagedBean().getQt()
				.setFinancialUserScoringSchemaAttachment(attachment);

	}

	/**
	 * delete Tehnical or Financial Upload UserScoring Schema Files.
	 * 
	 */
	public void deleteTechnicalFinacialUserScoringSchema(String type) {
		System.out.println("====delete Scoring Schema Type====" + type);
		if ("T".equals(type)) {
			this.getManagedBean().getQt()
					.setTechnicalUserScoringSchemaAttachment(null);
		} else if ("F".equals(type)) {
			this.getManagedBean().getQt()
					.setFinancialUserScoringSchemaAttachment(null);
		}
	}

	private void setTechncialChecklistEvaluationStatus(
			List<ScQtCheckList> technicalCheckList) {
		if (null != technicalCheckList && !technicalCheckList.isEmpty()) {
			for (ScQtCheckList checkList : technicalCheckList) {
				if (!ParameterConst.DOCUMENT_DEFINITION_TYPE_UPLOAD
						.equalsIgnoreCase(checkList.getDefinationType()
								.getParameterCode())
						&& !ParameterConst.DOCUMENT_DEFINITION_TYPE_NO_ACTION
								.equalsIgnoreCase(checkList.getDefinationType()
										.getParameterCode())) {
					if (!checkList.isScoringReq()) {
						checkList.setPmEvalStatus(getPmStatusByStatusId(QTStatusConst.TS_COMPLETED));
					}

				}
			}
		}

	}

	private void setFinancialChecklistEvaluationStatus(
			List<ScQtCheckList> financialCheckList) {
		if (null != financialCheckList && !financialCheckList.isEmpty()) {
			for (ScQtCheckList financialcheckList : financialCheckList) {
				if (!ParameterConst.DOCUMENT_DEFINITION_TYPE_UPLOAD
						.equalsIgnoreCase(financialcheckList
								.getDefinationType().getParameterCode())
						&& !ParameterConst.DOCUMENT_DEFINITION_TYPE_NO_ACTION
								.equalsIgnoreCase(financialcheckList
										.getDefinationType().getParameterCode())) {
					if (!financialcheckList.isScoringReq()) {
						financialcheckList.setPmEvalStatus(getPmStatusByStatusId(QTStatusConst.TS_COMPLETED));
					}

				}

			}
		}

	}

	private void validateTechnicalUserScoringAttachment(
			boolean isTechnicalUserScoringSchema) {

		if (isTechnicalUserScoringSchema) {
			if (null == this.getManagedBean().getQt()
					.getTechnicalUserScoringSchemaAttachment()) {
				//this.getManagedBean().setActiveIndex(4);
				//throw new EpException(MessageConst.ERROR_760378);
			}

		}
	}

	private void validateFinancialUserScoringAttachment(
			boolean isFinancialUserScoringSchema) {
		if (isFinancialUserScoringSchema) {
			if (null == this.getManagedBean().getQt()
					.getFinancialUserScoringSchemaAttachment()) {
				//this.getManagedBean().setActiveIndex(5);
				//throw new EpException(MessageConst.ERROR_760379);
			}
		}

	}

	/**
	 * Check status is checklist and scoring
	 * 
	 * @return
	 */
	public boolean isQTStatusChecklistScoring() {
		return QTStatusConst.QT_PENDING_APPROVALS_CHECKLIST_SCORING
				.equals(getManagedBean().getCurrentStatusId());
	}

	/**
	 * Check status is checklist and scoring revision
	 * 
	 * @return
	 */
	public boolean isQTStatusChecklistScoringRevision() {
		return QTStatusConst.QT_PENDING_CHECKLIST_SCORING_REVISION
				.equals(getManagedBean().getCurrentStatusId());
	}

	private ScQt saveZonesBeforeTemplateSpec(ScQt scQt) {
		ScQtSupplierCriteria qtSupplierCriteria = scQt
				.getScQtSupplierCriteria();
		if (!scQt.isPanel() && null != qtSupplierCriteria) {
			if (scQt.isZonal()
					&& scQt.getQtType().equals(ParameterConst.QUOTATION_TENDER_TYPE_OPEN)) {

				List<TplSpecZoneDtl> businessOwnerZone = convertTreeNodesToZoneCriteria(
						this.getManagedBean().getZoneGeneralTrees(), null);

				if (businessOwnerZone != null && !businessOwnerZone.isEmpty()) {
					// TODO Code needs to be remove
					setUserDetails(qtSupplierCriteria.getQtZoneCriterias());
					List<TplSpecZoneDtl> zoneLocal = new ArrayList<TplSpecZoneDtl>();
					for (TplSpecZoneDtl tplSpecZoneDtl : businessOwnerZone) {
						qtSupplierCriteria.getQtZoneCriterias().setModified(
								true);
						tplSpecZoneDtl.setTplSpecZone(qtSupplierCriteria
								.getQtZoneCriterias());
						zoneLocal.add(tplSpecZoneDtl);
					}
					qtSupplierCriteria.getQtZoneCriterias()
							.setScQtSupplierCriteria(qtSupplierCriteria);
					qtSupplierCriteria.getQtZoneCriterias().setTplSpecZoneDtls(
							zoneLocal);
				} else {
					if (null != scQt.getScQtSupplierCriteria()
							.getQtZoneCriterias()) {
						qtSupplierCriteria.getQtZoneCriterias()
								.setTplSpecZoneDtls(null);

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
		} else if (scQt.isPanel()) {

			List<TplSpecZoneDtl> businessOwnerZone = convertTreeNodesToZoneCriteria(
					this.getManagedBean().getZoneGeneralTrees(), null);

			if (businessOwnerZone != null && !businessOwnerZone.isEmpty()) {
				if (null != scQt.getScQtSupplierCriteria()
						&& null == scQt.getScQtSupplierCriteria()
								.getQtZoneCriterias()) {
					scQt.getScQtSupplierCriteria().setQtZoneCriterias(
							new ScQtZoneCriteria());
				}
				ScQtZoneCriteria zoneCriteria = scQt.getScQtSupplierCriteria()
						.getQtZoneCriterias();
				zoneCriteria.setScQtSupplierCriteria(scQt
						.getScQtSupplierCriteria());
				// TODO: Need to remove
				setUserDetails(zoneCriteria);
				List<TplSpecZoneDtl> zoneLocal = new ArrayList<TplSpecZoneDtl>();
				for (TplSpecZoneDtl tplSpecZoneDtl : businessOwnerZone) {
					zoneCriteria.setModified(true);
					tplSpecZoneDtl.setTplSpecZone(zoneCriteria);
					zoneLocal.add(tplSpecZoneDtl);
				}
				scQt.setZonal(true);
				qtSupplierCriteria.getQtZoneCriterias().setTplSpecZoneDtls(
						zoneLocal);

			}

			// scQt.setZoneLocationLevel(null);
			scQt.getScQtSupplierCriteria().setLocationLevel(null);
			scQt.setQtType(ParameterConst.QUOTATION_TENDER_TYPE_OPEN);

		}
		scQt.setScQtCommittees(null);
		scQt.setScQtStatus(null);
		scQt.setScQtBriefings(null);
		return scQt;
	}
	
	
	/*add panelItems asa TemplateSpecItems for Panel*/
	public String addPanelItemsToTemplateSpec(){
		this.getManagedBean().setSpecNewable(true);
		List<CtPanelItem> selectedPanelItems=this.getManagedBean().getSelectCtPanelItem();
		if(null==selectedPanelItems || selectedPanelItems.isEmpty()){
			selectedPanelItems=new ArrayList<CtPanelItem>();
			throw new EpException(MessageConst.ERROR_760147);
		}
		String itemType="";
		String localItemType="";
		for(CtPanelItem panelItem:selectedPanelItems){
			itemType=panelItem.getItemCategoryId().getParameterCode();
			if("".equals(localItemType) || !localItemType.equals(itemType)){
				if(null!=localItemType && !"".equals(localItemType)){
				throw new EpException(MessageConst.ERROR_760391);
				}else{
					localItemType=itemType;
				}
			}else{
				localItemType=itemType;
			}
			if(ParameterConst.ITEM_CATEGORY_TYPE_GENERAL.equals(itemType)){
				int num=selectedPanelItems.size();
				if(num>1){
					throw new EpException(MessageConst.ERROR_760392);
				}
				}
			
		}
		String itemCategory="";
		String localItemCategory="";
		for(CtPanelItem checkItem:selectedPanelItems){
			itemCategory=checkItem.getItemType().getParameterCode();
			if("".equals(localItemCategory) || !localItemCategory.equals(itemCategory)){
				if(null!=localItemCategory && !"".equals(localItemCategory)){
				throw new EpException(MessageConst.ERROR_760393);
				}else{
					localItemCategory=itemCategory;
				}
			}else{
				localItemCategory=itemCategory;
			}
		}
		for(CtPanelItem checkItem:selectedPanelItems){
			if(ParameterConst.ITEM_CATEGORY_TYPE_SPECIFIC.equals(checkItem.getItemCategoryId().getParameterCode())){
				this.getManagedBean().setSpecNewable(false);
				break;
			}
		}
		if(selectedPanelItems.size()>1){
			//String panelCoverage="";
			//String localPanelCoverage="";
			String locationLevel="";
			String locallocationLevel="";
			for(CtPanelItem checkItem:selectedPanelItems){
				String panelCoverage="";
				String localPanelCoverage="";
				if(null!=checkItem.getPanelCoverageId()){
				panelCoverage=checkItem.getPanelCoverageId().getParameterCode();
				}
				if(null!=checkItem.getLocationLevelId()){
				locationLevel=checkItem.getLocationLevelId().getParameterCode();
				}
				if("".equals(localPanelCoverage) || !localPanelCoverage.equals(panelCoverage)){
					if(null!=localPanelCoverage && !"".equals(localPanelCoverage)){
					throw new EpException(MessageConst.ERROR_760395);
					}else{
						localPanelCoverage=panelCoverage;
					}
				}
				if("".equals(locallocationLevel) || !locallocationLevel.equals(locationLevel)){
					if(null!=locallocationLevel && !"".equals(locallocationLevel)){
					throw new EpException(MessageConst.ERROR_760396);
					}else{
						locallocationLevel=locationLevel;
					}
				}
				
				else{
					locallocationLevel=locationLevel;
					localPanelCoverage=panelCoverage;
				}
				log.info("===panelCoverage"+panelCoverage+"localPanelCoverage"+localPanelCoverage);
				log.info("===locationLevel"+locationLevel+"locallocationLevel"+locallocationLevel);
			}
			
		}
		
		List<ScQtCheckList> technicalCheckList = this.getManagedBean()
				.getScQtCheckSt2techProposal();
		if (null != technicalCheckList && !technicalCheckList.isEmpty()) {
			boolean istechnicalSpec = isAtleastTechnicalSpec(technicalCheckList);
			if(istechnicalSpec){
				String ZonalType="";
				boolean zonal=this.getManagedBean().getQt().isZonal();
				if(zonal){
					ZonalType=QTConstants.CT_PANEL_ZONE_COVERAGE;
				}else{
					ZonalType=QTConstants.CT_PANEL_ALL_COVERAGE;
				}
				for(CtPanelItem checkItem:selectedPanelItems){
				String	panelCoverage=checkItem.getPanelCoverageId().getParameterCode();
				if(!ZonalType.equalsIgnoreCase(panelCoverage)){
					if(QTConstants.CT_PANEL_ZONE_COVERAGE.equalsIgnoreCase(ZonalType)){
					throw new EpException(MessageConst.ERROR_760397);
					}else{
						throw new EpException(MessageConst.ERROR_760398);
					}
				}
				}
					
			}
		}
		this.getManagedBean().setAutoUpdate(true);
		RequestContext.getCurrentInstance().execute("addPanelItem.hide()");
		createCircularItemSpec(selectedPanelItems);
		return "/xhtml/sc/boSctemplatespec/technicalCommitteeTemplate.xhtml?faces-redirect=true";
	}

	
	
	private void checkPanelItemIsZonalOrNonzonal(String panelCoverage,CtPanelItem item){
		ScQtSupplierCriteria supplierCriteria = this.getManagedBean().getQt().getScQtSupplierCriteria();
		if(QTConstants.CT_PANEL_ZONE_COVERAGE.equalsIgnoreCase(panelCoverage)){
			
			if (null == supplierCriteria) {
				supplierCriteria = new ScQtSupplierCriteria();
			}
			ScQtZoneCriteria zoneCriteria = supplierCriteria
					.getQtZoneCriterias();
			if (null == zoneCriteria) {
				zoneCriteria = new ScQtZoneCriteria();
				zoneCriteria.setScQtSupplierCriteria(supplierCriteria);
				supplierCriteria.setQtZoneCriterias(zoneCriteria);
			}
			this.getManagedBean().getQt().setZonal(true);
			this.getManagedBean().getQt().setZoneLocationLevel(item.getLocationLevelId());
			if(this.getManagedBean().getQt()
					.isZonal()
					&& null != supplierCriteria
					&& null != supplierCriteria.getQtZoneCriterias() && null == supplierCriteria
					.getQtZoneCriterias().getSpecZoneId()) {
				retriveZonesOrSuppliersByItem();
					}
		
		}else{
			this.getManagedBean().getQt().setZonal(false);
			this.getManagedBean().getQt().setZoneLocationLevel(item.getLocationLevelId());
			retriveZonesOrSuppliersByItem();
		}
		
	}

}
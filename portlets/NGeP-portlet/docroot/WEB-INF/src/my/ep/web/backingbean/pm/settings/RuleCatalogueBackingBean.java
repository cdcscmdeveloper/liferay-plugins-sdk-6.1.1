package my.ep.web.backingbean.pm.settings;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import my.ep.bean.application.NgepApplicationBean; //#12177 Refactor
import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.bean.search.SearchOrderBean;
import my.ep.biz.service.cm.codification.ICodificationSO;
import my.ep.biz.service.ct.dtl.IContractItemSO;
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.authorization.orgprofile.IPtjSpecialApprovalSO;
import my.ep.biz.service.pm.common.confgeneral.IConfigGeneralSO;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.pm.fee.settings.IFeeExemptionSO;
import my.ep.biz.service.pm.general.settings.designatedarea.IDesignatedAreaSO;
import my.ep.biz.service.pm.general.settings.gstitem.IGstItemSO;
import my.ep.biz.service.pm.general.settings.rule.IRuleSO;
import my.ep.biz.service.pm.general.settings.threshold.IOperationalThresholdSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.cm.CmConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.pm.ProfileManagementConst;
import my.ep.constant.pm.RuleTypeConst;
import my.ep.constants.CommonConstants;
import my.ep.dto.cm.codification.CodificationReqDTO;
import my.ep.dto.cm.codification.CodificationResDTO;
import my.ep.dto.ct.dtl.ContractItemReqDTO;
import my.ep.dto.ct.dtl.ContractItemResDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
import my.ep.dto.pm.authorization.orgprofile.PtjSpecialApprovalReqDTO;
import my.ep.dto.pm.authorization.orgprofile.PtjSpecialApprovalResDTO;
import my.ep.dto.pm.common.PmTrackingDairyVO;
import my.ep.dto.pm.common.confgeneral.ConfigGeneralReqDTO;
import my.ep.dto.pm.common.confgeneral.ConfigGeneralResDTO;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.dto.pm.fee.settings.FeeExemptionReqDTO;
import my.ep.dto.pm.fee.settings.FeeExemptionResDTO;
import my.ep.dto.pm.general.settings.designatedarea.DesignatedAreaReqDTO;
import my.ep.dto.pm.general.settings.designatedarea.DesignatedAreaResDTO;
import my.ep.dto.pm.general.settings.gstitem.GstItemReqDTO;
import my.ep.dto.pm.general.settings.gstitem.GstItemResDTO;
import my.ep.dto.pm.general.settings.rule.RuleReqDTO;
import my.ep.dto.pm.general.settings.rule.RuleResDTO;
import my.ep.dto.pm.general.settings.threshold.ThresholdReqDTO;
import my.ep.dto.pm.general.settings.threshold.ThresholdResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.BaseEntity;
import my.ep.persistence.entity.cm.catalog.CmBrand;
import my.ep.persistence.entity.cm.catalog.CmColor;
import my.ep.persistence.entity.cm.catalog.CmItem;
import my.ep.persistence.entity.cm.catalog.CmMeasurement;
import my.ep.persistence.entity.cm.catalog.CmType;
import my.ep.persistence.entity.cm.catalog.CmUnspsc;
import my.ep.persistence.entity.ct.CtContract;
import my.ep.persistence.entity.ct.dtl.item.CtContractItem;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmPtjSpecialApproval;
import my.ep.persistence.entity.pm.authorization.PmUserOrg;
import my.ep.persistence.entity.pm.common.PmCategoryL3;
import my.ep.persistence.entity.pm.common.PmLanguage;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.pm.fee.settings.PmFeeExemption;
import my.ep.persistence.entity.pm.general.settings.gst.PmDaGst;
import my.ep.persistence.entity.pm.general.settings.gst.PmDaValidity;
import my.ep.persistence.entity.pm.general.settings.gst.PmDesignatedArea;
import my.ep.persistence.entity.pm.general.settings.gst.PmGstItem;
import my.ep.persistence.entity.pm.general.settings.rule.PmConfigRule;
import my.ep.persistence.entity.pm.general.settings.rule.PmConfigRuleDesc;
import my.ep.persistence.entity.pm.general.settings.rule.PmConfigRuleValue;
import my.ep.persistence.entity.pm.general.settings.threshold.PmThreshold;
import my.ep.persistence.entity.sc.common.ScRequestItem;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.RoleCode;
import my.ep.util.MessageUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.pm.settings.RuleCatalogueManageBean;
import my.ep.web.util.CommonUtils;
import my.ep.web.util.DateUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.ParameterHelper;
import my.ep.web.util.pm.helper.PmCommonUtil;
import my.ep.web.util.pm.helper.RuleConfigHelper;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.event.TabChangeEvent;

@ManagedBean(name = "ruleCatalogueBackingBean")
@ViewScoped
public class RuleCatalogueBackingBean extends
		BaseBackingBean<RuleCatalogueManageBean> implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static Logger log = Logger
			.getLogger(RuleCatalogueBackingBean.class);

	private ISearchBean<PmParameterDesc> parameterSearchBean;
	private IPagingDataModel<CmUnspsc> searchDataModel = new SearchPagingDataModel();

	private static Map<Long, Object> colorMapper;
	private static Map<Long, Object> designatedAreaMapper;

	// GST tab index
	private static final int TAXABLE_ITEM_IDX = 0;
	private static final int DESIGNATED_AREA_IDX = 1;
	private static final int TAXABLE_ITEM_DA_IDX = 2;
	private static final int TAXABLE_ITEM_G2G_IDX = 3;
	private Integer currentIndexVal;
	private String inactivePopUpMsg = "";
	private String inactiveFeeOrgPopUpMsg = "";
	private String inactiveFeeComoPopUpMsg = "";
	private String inactiveFeeContPopUpMsg = "";

	private String dupplicateOrgTypeName;

	private static final String SETTING_CONFIG_MAIN = "settingConfig.xhtml?faces-redirect=true";
	private static final String ERROR_PAGE = "/xhtml/pm/shared/outputMsg.xhtml";

	// private List<SortMeta> preSortOrder = new ArrayList<SortMeta>();

	public String getDupplicateOrgTypeName() {
		return dupplicateOrgTypeName;
	}

	public void setDupplicateOrgTypeName(String dupplicateOrgTypeName) {
		this.dupplicateOrgTypeName = dupplicateOrgTypeName;
	}

	public ISearchBean<PmParameterDesc> getParameterSearchBean() {
		if (this.parameterSearchBean == null) {
			this.parameterSearchBean = super.createSearchBean(
					PmParameterDesc.class, "parameterId");
		}

		return this.parameterSearchBean;
	}

	public RuleCatalogueBackingBean() {

	}

	/**
	 * Category Selection Action
	 * 
	 * @param pmCategoryL3
	 *            the PmCategoryL3 instance
	 */
	public void selectCategoryAction(PmCategoryL3 pmCategoryL3) {
		log.info("*****addNewRuleTypeValue*$$$****");
		this.getManagedBean().setSelectedCategoryL3(pmCategoryL3);
		PmConfigRule configRuleType = this.getManagedBean()
				.getSelectedRuleDescType().getRuleType();
		PmConfigRuleValue newRuleValue = new PmConfigRuleValue();
		newRuleValue.setRuleType(configRuleType);
		newRuleValue.setEffDate(this.getManagedBean().getTomorrow());

		// if (this.getManagedBean().getSelectedRuleType().getRuleDataTypeId()
		// .getParameterCode().equals("C")) {

		newRuleValue.setCategoryL3Id(this.getManagedBean()
				.getSelectedCategoryL3());
		// }

		if (configRuleType.getRangeType() == 1) {

			if (configRuleType.getConfigRuleValues() != null
					&& configRuleType.getConfigRuleValues().size() > 0) {
				int size = configRuleType.getConfigRuleValues().size();
				newRuleValue
						.setGroupValueId(configRuleType.getConfigRuleValues()
								.get(size - 1).getGroupValueId() + 1);

			} else {

				newRuleValue.setGroupValueId(1);
			}
			newRuleValue.setFromValue(new BigDecimal(1));
		}
		int index = configRuleType.getConfigRuleValues().size();
		configRuleType.getConfigRuleValues().add(index, newRuleValue);
		RequestContext.getCurrentInstance().execute(
				"ruleValueTblWidget.paginator.endLink.click()");
	}

	public void typeValueChgangeListener(PmFeeExemption feeExemption) {
		log.info("................updateUnspscForFee...............");

		log.info("......feeExemption.getTypeId().getTypeId()"
				+ feeExemption.getTypeId().getTypeId());

		ICodificationSO unspscSO;
		try {

			if (feeExemption.getTypeId() != null) {
				unspscSO = (ICodificationSO) BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.CATALOGUE_MANAGEMENT,
								ICodificationSO.class);
				CodificationReqDTO reqDTO = new CodificationReqDTO();
				ISearchBean<CmType> searchBeanType = new SearchBean<CmType>();
				SearchFilterBean typeFilterBean = new SearchFilterBean(
						SearchOperator.OPERATOR_EQUALS.value());
				typeFilterBean.setSearchField("typeId");
				typeFilterBean.setSearchValue(feeExemption.getTypeId()
						.getTypeId());
				searchBeanType.getDataFilterBeans().add(typeFilterBean);
				reqDTO.setTypeSearchBean(searchBeanType);
				CodificationResDTO resDTO = unspscSO.searchTypeObj(reqDTO);
				if (resDTO.isSuccessful()) {
					feeExemption.setTypeId(resDTO.getTypeList().get(0));
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			unspscSO = null;
		}

	}

	public void getParameterObj(PmFeeExemption ex) {
		if (ex.getOrgTypeId() != null) {
			PmParameter parameter = ParameterHelper.getInstance()
					.getPmParameterById(ex.getOrgTypeId().getParameterId());
			if (parameter != null)
				ex.setOrgTypeId(parameter);

		}
	}

	/**
	 * The method used for generating a list of available display type of org
	 * type as selection.
	 * 
	 */
	public SelectItem[] getOrgTypeDropdownValuesFOR() {
		log.info("getOrgTypeDropdownValues for org type");
		ISearchBean<PmParameterDesc> searchBean = null;
		searchBean = getOrgTypeSearchBean("orgTypeSysParameter");
		SelectItem[] items = null;
		if (searchBean != null) {
			searchBean.getSortColumns();
			items = getDropdownValuesBySearchBean(
					ParameterTypeConst.ORGANISATION_TYPES, searchBean);
		}
		return items;
	}

	private ISearchBean<PmParameterDesc> getOrgTypeSearchBean(String componentId) {
		log.info(".......componentId=" + componentId);
		ISearchBean<PmParameterDesc> orgTypeSearchBean = super
				.createSearchBean(PmParameterDesc.class, componentId);
		return orgTypeSearchBean;
	}

	// public List<SortMeta> getPreSortOrder() {
	// return preSortOrder;
	// }

	public boolean isValidDateRange(Date effDate, Date ExpDate) {
		log.info("...............isValidDateRange...............");
		if (ExpDate != null && (ExpDate.getTime() < effDate.getTime())) {
			return false;
		}
		return true;
	}

	public void setInactivePopUpMsg(String inactivePopUpMsg) {
		this.inactivePopUpMsg = inactivePopUpMsg;
	}

	public String getInactivePopUpMsg() {
		return inactivePopUpMsg;
	}

	public String getInactiveFeeOrgPopUpMsg() {
		// log.info(".........getInactiveFeeOrgPopUpMsg.........");
		return inactiveFeeOrgPopUpMsg;
	}

	public void setInactiveFeeOrgPopUpMsg(String inactiveFeeOrgPopUpMsg) {
		this.inactiveFeeOrgPopUpMsg = inactiveFeeOrgPopUpMsg;
	}

	/*
	 * method to build initial sort order for multisort
	 */
	// private void buildSortOrder() {
	// log.info("...............buildSortOrder...............");
	// UIViewRoot viewRoot = FacesContext.getCurrentInstance().getViewRoot();
	// UIComponent column1 =
	// viewRoot.findComponent("ruleTypeValueTableID:fromValueColumnId");
	// UIComponent column2 =
	// viewRoot.findComponent("ruleTypeValueTableID:effDateColumnId");
	//
	// SortMeta sm1 = new SortMeta();
	// sm1.setSortBy((UIColumn) column1);
	// sm1.setSortField("fromValueColumnId");
	// sm1.setSortOrder(SortOrder.ASCENDING);
	// preSortOrder.add(sm1);
	//
	// SortMeta sm2 = new SortMeta();
	// sm2.setSortBy((UIColumn) column2);
	// sm2.setSortField("effDateColumnId");
	// sm2.setSortOrder(SortOrder.ASCENDING);
	// preSortOrder.add(sm2);
	// log.info(".............end of..buildSortOrder...............");
	// }

	public String getInactiveFeeComoPopUpMsg() {

		return inactiveFeeComoPopUpMsg;
	}

	public String getInactiveFeeContPopUpMsg() {

		return inactiveFeeContPopUpMsg;
	}

	public void setInactiveFeeComoPopUpMsg(String inactiveFeeComoPopUpMsg) {
		this.inactiveFeeComoPopUpMsg = inactiveFeeComoPopUpMsg;
	}

	public void setInactiveFeeContPopUpMsg(String inactiveFeeContPopUpMsg) {
		this.inactiveFeeContPopUpMsg = inactiveFeeContPopUpMsg;
	}

	public String preRenderView() {
		log.info("...........preRenderView............");
		getManagedBean().setTabActiveIndex(0);
		getManagedBean().setFeeExemptionIndex(0);
		getManagedBean().setThresholdIndex(0);
		getManagedBean().setGstIndex(TAXABLE_ITEM_IDX);
		getManagedBean().setAutoUpdate(true);
		setCurrentActiveOrg();
		// resetErrorDialog();
		FacesContext fc = FacesContext.getCurrentInstance();
		try {
			initConfigRule();
			initFeeSettings();
			initOperationThreshold();
			initGst();
			initQuotationWithTenderValue();
			initYearEndForProcessing();
			fc.getApplication().getNavigationHandler()
					.handleNavigation(fc, null, SETTING_CONFIG_MAIN);
		} catch (Exception e) {
			e.printStackTrace();
			fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					null, e.getMessage()));
			PmCommonUtil.redirectToPage(ERROR_PAGE, null, null);
		}
		return "";
	}
	
	private void initYearEndForProcessing(){
		IFeeExemptionSO feeSO = null;
		this.getManagedBean().setMinDate(null);//32333
		
		try {
			feeSO = (IFeeExemptionSO) BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IFeeExemptionSO.class);
			FeeExemptionReqDTO reqFDTO = new FeeExemptionReqDTO();
			FeeExemptionResDTO resFDTO = null;
			this.getManagedBean().getFeeExemptionsForYepProcessing().clear();
			this.getManagedBean().getDeletedYepProcessingFeeExemptions().clear();
			
			//retrieve pm_fee_exemption records
			resFDTO = feeSO.getFeeExemptionsYepProcessing(reqFDTO);
			
			log.info("[#22119] resFDTO.getFeeExemptions size: " + resFDTO.getFeeExemptions().size());
			/*22119*/
//			String lang = getLanguage();
			if(resFDTO != null && resFDTO.getFeeExemptions().size() > 0) {
				this.getManagedBean().setFeeExemptionsForYepProcessing(resFDTO.getFeeExemptions());
			}
		} finally {
			feeSO = null;
		}	
	}

	private void initConfigRule() {
		IConfigGeneralSO cgeneralSO = null;
		try {
			cgeneralSO = (IConfigGeneralSO) BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IConfigGeneralSO.class);
			ConfigGeneralReqDTO reqDTO = new ConfigGeneralReqDTO();
			ConfigGeneralResDTO resDTO = cgeneralSO.getConfigGeneral(reqDTO);
			this.getManagedBean().setSelectedConfigGeneral(
					resDTO.getPmConfigGeneral());
			log.info("resDTO.getPmConfigGeneral().getFinancialYear()="
					+ resDTO.getPmConfigGeneral().getFinancialYear());
			// this.getManagedBean().setTabActiveIndex(0);
			// for(PmConfigRule
			// configRule:RuleConfigHelper.getInstance().getRuleTypeList()){
			// PmConfigRule clone = (PmConfigRule)
			// SerializationUtils.clone(configRule);
			//
			// }

			List<PmConfigRuleDesc> list = retriveConfigRuleDescFromDB();
			getManagedBean().setConfigRuleDescList(list);
			getManagedBean().setSelectedRuleDescType(list.get(0));
			// List<PmConfigRule> list = retriveConfigRuleFromDB();
			// this.getManagedBean().setConfigRuleList(list);
			// this.getManagedBean().setSelectedRuleType(list.get(0));
		} finally {
			cgeneralSO = null;
		}
	}

	private void initFeeSettings() {
		IFeeExemptionSO feeSO = null;
		try {
			feeSO = (IFeeExemptionSO) BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IFeeExemptionSO.class);
			FeeExemptionReqDTO reqFDTO = new FeeExemptionReqDTO();
			FeeExemptionResDTO resFDTO = null;
			this.getManagedBean().getFeeExemptionsForOrgAndItem().clear();
			this.getManagedBean().getDeletedOrgAndItemFeeExemptions().clear();
			resFDTO = feeSO.getFeeExemptionsForOrgAndItem(reqFDTO);
			for (PmFeeExemption exemption : resFDTO.getFeeExemptions()) {
				if (exemption.getItemId() == null) {// TODO: clear it before
													// updating the fee list
					exemption.setItemId(new CmItem());
				}
				this.getManagedBean().getFeeExemptionsForOrgAndItem()
						.add(exemption);
			}

			this.getManagedBean().getFeeExemptionsForComodity().clear();
			this.getManagedBean().getDeletedComodityFeeExemptions().clear();

			resFDTO = feeSO.getFeeExemptionsForComodity(reqFDTO);

			// ICodificationSO unspscSO = (ICodificationSO) BusinessDelegate
			// .getInstance().getServiceObject(
			// ModuleCodeConst.CATALOGUE_MANAGEMENT,
			// ICodificationSO.class);
			// CodificationReqDTO cReqDTO = new CodificationReqDTO();
			for (PmFeeExemption exemption : resFDTO.getFeeExemptions()) {
				if (exemption.getTypeId() == null) {
					exemption.setTypeId(new CmType());
				}
				// cReqDTO.setUnspscCode(exemption.getUnspscId().getUnspscCode());
				// CodificationResDTO cResDTO = unspscSO
				// .retrieveUNSPSCByCommodityCode(cReqDTO);
				//
				// if (cResDTO.isSuccessful()) {
				// CmUnspsc commodity = cResDTO.getCommodityObj();
				// exemption.setTypes(new ArrayList<CmType>());
				// for (CmType type : commodity.getCmType()) {
				// if (!StringUtils.isBlank(type.getTypeName())) {
				// exemption.getTypes().add(type);
				// }
				// }
				// }
				this.getManagedBean().getFeeExemptionsForComodity()
						.add(exemption);
			}

			this.getManagedBean().getFeeExemptionsForContract().clear();
			this.getManagedBean().getDeletedContractFeeExemptions().clear();
			resFDTO = feeSO.getFeeExemptionsForContractAndReqItem(reqFDTO);

			for (PmFeeExemption exemption : resFDTO.getFeeExemptions()) {
				if (exemption.getRequestItemId() == null) {
					exemption.setRequestItemId(new ScRequestItem());// TODO:
																	// clear
																	// it
																	// before
																	// updating
																	// the
																	// fee
																	// list
				}
				this.getManagedBean().getFeeExemptionsForContract()
						.add(exemption);
			}
			this.getManagedBean().getFeeExemptionsForOrgType().clear();
			this.getManagedBean().getDeletedOrgTypeFeeExemptions().clear();
			resFDTO = feeSO.getFeeExemptionsForOrgType(reqFDTO);

			this.getManagedBean().setFeeExemptionsForOrgType(
					resFDTO.getFeeExemptions());

		} finally {
			feeSO = null;
		}

		// this.getManagedBean().setFeeExemptionIndex(1);

	}

	private void initOperationThreshold() {
		IOperationalThresholdSO thresholdSO = null;

		try {
			thresholdSO = (IOperationalThresholdSO) BusinessDelegate
					.getInstance().getServiceObject(
							ModuleCodeConst.PROFILE_MANAGEMENT,
							IOperationalThresholdSO.class);

			// ThresholdReqDTO reqTDTO = new ThresholdReqDTO();
			// reqTDTO.setOrgProfileId(null);
			// resTDTO = thresholdSO.getOperationalThresholds(reqTDTO);
			// if (resTDTO.getThresholdList().isEmpty()) {
			// throw new EpException("Default Operation Threshold is missing.");
			// }
			ThresholdReqDTO reqTDTO = new ThresholdReqDTO();
			reqTDTO.setEpAdmin(getFacesScopes().hasRoleUser(
					RoleCode.EP_ADMIN.value()));
			if (!getFacesScopes().hasRoleUser(RoleCode.EP_ADMIN.value())) {
				reqTDTO.setEpAdmin(getFacesScopes().hasRoleUser(
						RoleCode.EP_ADMIN_1.value()));
			}
			if (!getFacesScopes().hasRoleUser(RoleCode.EP_ADMIN.value())
					&& !getFacesScopes().hasRoleUser(
							RoleCode.EP_ADMIN_1.value())) {
				reqTDTO.setEpAdmin(getFacesScopes().hasRoleUser(
						RoleCode.BPK_ADMIN.value()));
			}
			if (getManagedBean().getCurrentActiveOrg() != null) {
				reqTDTO.setOrgProfileId(getManagedBean().getCurrentActiveOrg()
						.getOrgProfileId());
			}
			ThresholdResDTO resTDTO = thresholdSO.initThreshold(reqTDTO);
			if (resTDTO.isSuccessful() && resTDTO.getThreshold() != null) {
				getManagedBean().setThreshold(resTDTO.getThreshold());
				getManagedBean().setUnplannedThreshold(
						resTDTO.getThreshold().getS2Unplanned());
				getManagedBean().setBudgetThreshold(
						resTDTO.getThreshold().getBudgetSpent());

				if (!resTDTO.getThresholdList().isEmpty()) {
					getManagedBean().setUnplannedThresholdList(
							resTDTO.getThresholdList());
					getManagedBean().setBudgetThresholdList(
							resTDTO.getThresholdList());
				}
			} else {
				throw new EpException("Operation Threshold Error.");
			}
		} catch (Exception e) {
			throw new EpException(e.getMessage());
		} finally {
			thresholdSO = null;
		}
	}

	private List<PmConfigRuleDesc> retriveConfigRuleDescFromDB() {
		List<PmConfigRuleDesc> configRulesDesc = new ArrayList<PmConfigRuleDesc>();
		// get Rule config from DB
		IRuleSO ruleSO;
		try {
			// get from db
			ruleSO = (IRuleSO) BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IRuleSO.class);
			RuleReqDTO reqRDTO = new RuleReqDTO();
			String languageCode = new FacesScopes().getUserLocale()
					.getLanguage();
			reqRDTO.setLanguageCode(languageCode);
			ISearchBean<PmConfigRuleDesc> searchBean = new SearchBean<PmConfigRuleDesc>();
			//TODO - data restriction on next round (16/2/2017)
//			ISearchBean<PmConfigRuleDesc> searchBean = super.createSearchBean(PmConfigRuleDesc.class,
//					"ruleTypeTableID");
			// Filter By - status
//						searchBean.getDataFilterBeans().add(
//								new SearchFilterBean("ruleType.ruleType",
//										SearchOperator.OPERATOR_EQUALS.value(),
//										"QT"));
//			
			// Filter By - status
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("ruleType.recordStatus",
							SearchOperator.OPERATOR_EQUALS.value(),
							DbValueConst.RECORD_STATUS_ACTIVE));

			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("languageCode.languageCode",
							SearchOperator.OPERATOR_EQUALS.value(),
							languageCode));

			searchBean.getSortColumns().add(
					new SearchOrderBean("ruleDesc", true));

			reqRDTO.setDescSearchBean(searchBean);

			RuleResDTO resRDTO = ruleSO.getRuleTypesDesc(reqRDTO);
			if (resRDTO.isSuccessful() && resRDTO.getDescResultBean() != null) {
				configRulesDesc = resRDTO.getDescResultBean().getResultList();
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new EpException(e.getMessage());
		} finally {
			ruleSO = null;
		}

		return configRulesDesc;
	}

	// private void sortConfigRuleValueList(
	// List<PmConfigRuleValue> configRuleValueList) {
	// Comparator<PmConfigRuleValue> comparator = new
	// Comparator<PmConfigRuleValue>() {
	// @Override
	// public int compare(PmConfigRuleValue o1,
	// PmConfigRuleValue o2) {
	// return new CompareToBuilder().append(o1.getEffDate(),
	// o2.getEffDate()).append(o1.getFromValue(),
	// o2.getFromValue()).toComparison();
	// }
	// };
	// Collections.sort(configRuleValueList, comparator);
	// }

	// public void categoryCodesUpdate(BaseEntity entity) {
	//
	// PmCategoryL3 categoryL3 = (PmCategoryL3) entity;
	// log.info("in categoryCodesUpdate: " + categoryL3.getCategoryL3Code());
	//
	// ICategoryCodeSO categoryCodeSo;
	//
	// categoryCodeSo = BusinessDelegate.getInstance().getServiceObject(
	// ModuleCodeConst.PROFILE_MANAGEMENT, ICategoryCodeSO.class);
	// CategoryCodeReqDTO codeReqDTO = new CategoryCodeReqDTO();
	// codeReqDTO.setCategoryL3Id(categoryL3.getCategoryL3Id());
	// CategoryCodeResDTO CategoryCodeResDTO = categoryCodeSo
	// .retrieveCategoryL3DtlById(codeReqDTO);

	// if (CategoryCodeResDTO != null
	// && CategoryCodeResDTO.getCategoryL3() != null) {
	// String categoryL3Code =
	// CategoryCodeResDTO.getCategoryL3().getCategoryL3Code();
	// String categoryL2Code =
	// CategoryCodeResDTO.getCategoryL3().getPmCategoryL2().getCategoryL2Code();
	// String categoryL1Code =
	// CategoryCodeResDTO.getCategoryL3().getPmCategoryL2().getPmCategoryL1().getCategoryL1Code();
	// // //
	// log.info("..................CategoryCodeResDTO.getCategoryL3().getCategoryL3Id()="
	// // // + CategoryCodeResDTO.getCategoryL3().getCategoryL3Id());
	// // // log.info(".............categoryL3Code=" + categoryL3Code);
	// // // log.info(".............categoryL2Code=" + categoryL2Code);
	// // // log.info(".............categoryL1Code=" + categoryL1Code);
	// ruleValue.setCategoryDisplayValue(categoryL1Code + categoryL2Code
	// + categoryL3Code);
	// /
	// }
	// }

	// public void categoryCodesUpdate(int index,
	// List<PmConfigRuleValue> ruleTypeValueList, PmCategoryL3 categoryL3) {
	// log.info("................categoryCodesUpdate...............");
	// ICategoryCodeSO categoryCodeSo;
	//
	// categoryCodeSo = BusinessDelegate.getInstance().getServiceObject(
	// ModuleCodeConst.PROFILE_MANAGEMENT, ICategoryCodeSO.class);
	// CategoryCodeReqDTO codeReqDTO = new CategoryCodeReqDTO();
	// codeReqDTO.setCategoryL3Id(categoryL3.getCategoryL3Id());
	// CategoryCodeResDTO CategoryCodeResDTO = categoryCodeSo
	// .retrieveCategoryL3DtlById(codeReqDTO);
	// if (CategoryCodeResDTO != null
	// && CategoryCodeResDTO.getCategoryL3() != null) {
	// PmConfigRuleValue ruleValue = ruleTypeValueList.get(index);
	// ruleValue.setCategoryL3Id(CategoryCodeResDTO.getCategoryL3());
	// String categoryL3Code = CategoryCodeResDTO.getCategoryL3()
	// .getCategoryL3Code();
	// String categoryL2Code = CategoryCodeResDTO.getCategoryL3()
	// .getPmCategoryL2().getCategoryL2Code();
	// String categoryL1Code = CategoryCodeResDTO.getCategoryL3()
	// .getPmCategoryL2().getPmCategoryL1().getCategoryL1Code();
	// //
	// log.info("..................CategoryCodeResDTO.getCategoryL3().getCategoryL3Id()="
	// // + CategoryCodeResDTO.getCategoryL3().getCategoryL3Id());
	// // log.info(".............categoryL3Code=" + categoryL3Code);
	// // log.info(".............categoryL2Code=" + categoryL2Code);
	// // log.info(".............categoryL1Code=" + categoryL1Code);
	// ruleValue.setCategoryDisplayValue(categoryL1Code + categoryL2Code
	// + categoryL3Code);
	// this.getManagedBean().getSelectedRuleType().getConfigRuleValues()
	// .remove(index);
	// this.getManagedBean().getSelectedRuleType().getConfigRuleValues()
	// .add(index, ruleValue);
	// }
	//
	// }
	//
	public void ruleValueUpdate(int index,
			List<PmConfigRuleValue> ruleTypeValueList, String value) {
		log.info("................ruleValueUpdate...............");
		if (value != null) {
			PmConfigRuleValue ruleValue = ruleTypeValueList.get(index);
			PmConfigRule configRuleType = this.getManagedBean()
					.getSelectedRuleDescType().getRuleType();
			ruleValue.setRuleValue(value);
			configRuleType.getConfigRuleValues().remove(index);
			configRuleType.getConfigRuleValues().add(index, ruleValue);
		}

	}

	public void fromValueUpdate(int index,
			List<PmConfigRuleValue> ruleTypeValueList, BigDecimal fromValue) {
		log.info("................fromValueUpdate...............");
		if (fromValue != null) {
			PmConfigRuleValue ruleValue = ruleTypeValueList.get(index);
			PmConfigRule configRuleType = this.getManagedBean()
					.getSelectedRuleDescType().getRuleType();
			ruleValue.setFromValue(fromValue);
			configRuleType.getConfigRuleValues().remove(index);
			configRuleType.getConfigRuleValues().add(index, ruleValue);
		}

	}

	public void toValueUpdate(int index,
			List<PmConfigRuleValue> ruleTypeValueList, BigDecimal toValue) {
		log.info("................toValueUpdate...............");
		if (toValue != null) {
			PmConfigRuleValue ruleValue = ruleTypeValueList.get(index);
			PmConfigRule configRuleType = this.getManagedBean()
					.getSelectedRuleDescType().getRuleType();
			ruleValue.setToValue(toValue);
			configRuleType.getConfigRuleValues().remove(index);
			configRuleType.getConfigRuleValues().add(index, ruleValue);
		}

	}

	public void effDateUpdate(int index,
			List<PmConfigRuleValue> ruleTypeValueList, Date effDate) {
		log.info("................effDateUpdate...............");
		if (effDate != null) {
			PmConfigRuleValue ruleValue = ruleTypeValueList.get(index);
			PmConfigRule configRuleType = this.getManagedBean()
					.getSelectedRuleDescType().getRuleType();
			ruleValue.setEffDate(effDate);
			configRuleType.getConfigRuleValues().remove(index);
			configRuleType.getConfigRuleValues().add(index, ruleValue);
		}

	}

	public void expDateUpdate(int index,
			List<PmConfigRuleValue> ruleTypeValueList, Date expDate) {
		log.info("................expDateUpdate...............");
		if (expDate != null) {
			PmConfigRuleValue ruleValue = ruleTypeValueList.get(index);
			PmConfigRule configRuleType = this.getManagedBean()
					.getSelectedRuleDescType().getRuleType();
			ruleValue.setExpDate(expDate);
			configRuleType.getConfigRuleValues().remove(index);
			configRuleType.getConfigRuleValues().add(index, ruleValue);
		}

	}

	public void updateOrgProfileForFee(BaseEntity entity) {
		log.info("................updateOrgProfileForFee...............");
		IOrgProfileSO profileSO;
		try {
			profileSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IOrgProfileSO.class);

			OrgProfileReqDTO reqDAO = new OrgProfileReqDTO();
			reqDAO.setOrgProfileId(this.getManagedBean()
					.getSelectedSmSupplier().getSupplierId());
			OrgProfileResDTO resDAO = profileSO
					.retrieveOrgWithSupplierByProfileId(reqDAO);
			log.info("...............resDAO.getPmOrgProfile()="
					+ resDAO.getPmOrgProfile());
			log.info("...............this.getManagedBean().getSelectedSmSupplier().getSupplierId()="
					+ this.getManagedBean().getSelectedSmSupplier()
							.getSupplierId());
			PmFeeExemption exemption = this.getManagedBean()
					.getSelectedFeeExemption();
			log.info("..............exemption=" + exemption);
			// PmFeeExemption exemption = feeExcemptionList.get(index);
			exemption.setOrgProfileId(resDAO.getPmOrgProfile());
			// this.getManagedBean().getFeeExemptionsForOrgAndItem().remove(index);
			// this.getManagedBean().getFeeExemptionsForOrgAndItem()
			// .add(index, exemption);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			profileSO = null;
		}
	}

	public void updateDatesForFeeBaseOnContract(BaseEntity entity, int index) {
		log.info("................updateDatesForFeeBaseOnContract...............");
		Date currentDate = new Date();
		CtContract contract = this.getManagedBean()
				.getFeeExemptionsForContract().get(index).getContractId();
		Date defaultEffDate = contract.getCurrentContractVer()
				.getCtContractValue().getEffDate();
		Date defaultExpDate = contract.getCurrentContractVer()
				.getCtContractValue().getExpDate();
		if (defaultEffDate.getTime() < currentDate.getTime()) {
			defaultEffDate = currentDate;
		}
		PmFeeExemption exemption = this.getManagedBean()
				.getFeeExemptionsForContract().get(index);

		exemption.setEffDate(defaultEffDate);
		exemption.setExpDate(defaultExpDate);

		exemption.setDefaultEffDate(defaultEffDate);
		if (defaultEffDate.getTime() > currentDate.getTime()) {
			exemption.setDefaultEffDate(currentDate);
		}
		this.getManagedBean().getFeeExemptionsForContract().remove(index);
		this.getManagedBean().getFeeExemptionsForContract()
				.add(index, exemption);
	}

	public void updateCmItemForFee(int index,
			List<PmFeeExemption> feeExcemptionList) {
		log.info("................updateCmItemForFee...............");

		try {
			CmItem selectedCmItem = this.getManagedBean().getSelectedCmItem();
			if (selectedCmItem == null) {
				selectedCmItem = new CmItem();// set empty cmItem if not
												// selected any
			}
			PmFeeExemption exemption = feeExcemptionList.get(index);
			exemption.setItemId(selectedCmItem);
			this.getManagedBean().getFeeExemptionsForOrgAndItem().remove(index);
			this.getManagedBean().getFeeExemptionsForOrgAndItem()
					.add(index, exemption);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}
	}

	public void updateUnspscForFee(BaseEntity entity, int index) {
		log.info("................updateUnspscForFee...............");
		ICodificationSO unspscSO;
		try {
			CmUnspsc selectedComodity = (CmUnspsc) entity;
			PmFeeExemption exemption = this.getManagedBean()
					.getFeeExemptionsForComodity().get(index);
			unspscSO = (ICodificationSO) BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.CATALOGUE_MANAGEMENT,
							ICodificationSO.class);
			CodificationReqDTO reqDTO = new CodificationReqDTO();
			reqDTO.setUnspscCode(selectedComodity.getUnspscCode());
			CodificationResDTO resDTO = unspscSO
					.retrieveUNSPSCByCommodityCode(reqDTO);

			if (resDTO.isSuccessful()) {
				CmUnspsc commodity = resDTO.getCommodityObj();
				exemption.setTypes(new ArrayList<CmType>());
				for (CmType type : commodity.getCmType()) {
					if (!StringUtils.isBlank(type.getTypeName())) {
						exemption.getTypes().add(type);
					}
				}

			}

			exemption.setUnspscId(resDTO.getCommodityObj());
			this.getManagedBean().getFeeExemptionsForComodity().remove(index);
			this.getManagedBean().getFeeExemptionsForComodity()
					.add(index, exemption);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			unspscSO = null;
		}
	}

	// public void updateUnspscForFee(int index) {
	// log.info("................updateUnspscForFee...............");
	// ICodificationSO unspscSO;
	// try {
	//
	// PmFeeExemption exemption = this.getManagedBean()
	// .getFeeExemptionsForComodity().get(index);
	// unspscSO = (ICodificationSO) BusinessDelegate.getInstance()
	// .getServiceObject(ModuleCodeConst.CATALOGUE_MANAGEMENT,
	// ICodificationSO.class);
	// CodificationReqDTO reqDTO = new CodificationReqDTO();
	// reqDTO.setUnspscCode(this.getManagedBean().getSelectedComodity()
	// .getUnspscCode());
	// CodificationResDTO resDTO = unspscSO
	// .retrieveUNSPSCByCommodityCode(reqDTO);
	//
	// if (resDTO.isSuccessful()) {
	// CmUnspsc commodity = resDTO.getCommodityObj();
	// exemption.setTypes(new ArrayList<CmType>());
	// for (CmType type : commodity.getCmType()) {
	// if (!StringUtils.isBlank(type.getTypeName())) {
	// exemption.getTypes().add(type);
	// }
	// }
	//
	// }
	//
	// exemption.setUnspscId(resDTO.getCommodityObj());
	// this.getManagedBean().getFeeExemptionsForComodity().remove(index);
	// this.getManagedBean().getFeeExemptionsForComodity()
	// .add(index, exemption);
	//
	// } catch (Exception e) {
	// e.printStackTrace();
	// } finally {
	// unspscSO = null;
	// }
	// }

	public void scRequestItemCodeUpdate(BaseEntity entity, int index) {
		log.info("................scRequestItemCodeUpdate...............");
		IContractItemSO contractItemSO;
		CtContractItem ctContract = (CtContractItem) entity;
		try {
			contractItemSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.CONTRACT_MANAGEMENT, IContractItemSO.class);

			ContractItemReqDTO reqDAO = new ContractItemReqDTO();
			reqDAO.setContractItemId(ctContract.getContractItemId());
			ContractItemResDTO resDAO = contractItemSO.getContractItem(reqDAO);
			log.info("...............this.getManagedBean().getSelectedCtContractItem().getContractItemId()="
					+ this.getManagedBean().getSelectedCtContractItem()
							.getContractItemId());
			log.info("...............resDAO.getCtContractItem().getScRequestItem()="
					+ resDAO.getCtContractItem().getScRequestItem());
			log.info("...............resDAO.getCtContractItem().getScRequestItem().getRequestNoteItemId()="
					+ resDAO.getCtContractItem().getScRequestItem()
							.getRequestNoteItemId());
			PmFeeExemption exemption = this.getManagedBean()
					.getFeeExemptionsForContract().get(index);
			exemption.setRequestItemId(resDAO.getCtContractItem()
					.getScRequestItem());
			log.info(".....after set..........exemption.getRequestItemId().getRequestNoteItemId()="
					+ exemption.getRequestItemId().getRequestNoteItemId());
			this.getManagedBean().getFeeExemptionsForContract().remove(index);
			this.getManagedBean().getFeeExemptionsForContract()
					.add(index, exemption);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			contractItemSO = null;
		}

	}

	public void scRequestItemCodeUpdate(int index,
			List<PmFeeExemption> feeExcemptionList) {
		log.info("................scRequestItemCodeUpdate...............");
		IContractItemSO contractItemSO;
		try {
			contractItemSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.CONTRACT_MANAGEMENT, IContractItemSO.class);

			ContractItemReqDTO reqDAO = new ContractItemReqDTO();
			reqDAO.setContractItemId(this.getManagedBean()
					.getSelectedCtContractItem().getContractItemId());
			ContractItemResDTO resDAO = contractItemSO.getContractItem(reqDAO);
			log.info("...............this.getManagedBean().getSelectedCtContractItem().getContractItemId()="
					+ this.getManagedBean().getSelectedCtContractItem()
							.getContractItemId());
			log.info("...............resDAO.getCtContractItem().getScRequestItem()="
					+ resDAO.getCtContractItem().getScRequestItem());
			log.info("...............resDAO.getCtContractItem().getScRequestItem().getRequestNoteItemId()="
					+ resDAO.getCtContractItem().getScRequestItem()
							.getRequestNoteItemId());
			PmFeeExemption exemption = feeExcemptionList.get(index);
			exemption.setRequestItemId(resDAO.getCtContractItem()
					.getScRequestItem());
			log.info(".....after set..........exemption.getRequestItemId().getRequestNoteItemId()="
					+ exemption.getRequestItemId().getRequestNoteItemId());
			this.getManagedBean().getFeeExemptionsForContract().remove(index);
			this.getManagedBean().getFeeExemptionsForContract()
					.add(index, exemption);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			contractItemSO = null;
		}

	}

	/**
	 * get all language list
	 * 
	 * @return List<PmLanguage>
	 */
	public List<PmLanguage> getLanguageList() {
		log.info("getLanguageList");
		return PmCommonUtil.retrieveLanguageObjs();
	}

	public void addMoreRuleValue() {
		log.info(".................addMoreRuleValue...................");
		PmConfigRuleValue newRuleValue = new PmConfigRuleValue();
		log.info("...................newRuleValue.getRuleValue()="
				+ newRuleValue.getRuleValue());
		PmConfigRule configRuleType = this.getManagedBean()
				.getSelectedRuleDescType().getRuleType();
		newRuleValue.setRuleType(configRuleType);
		newRuleValue.setEffDate(this.getManagedBean().getTomorrow());

		if (configRuleType.getRuleDataTypeId().getParameterCode().equals("C")) {
			newRuleValue.setCategoryL3Id(new PmCategoryL3()); // make sure to
																// delete the
																// new obj
																// before update
																// if doesn't
																// selected
		}

		int index = configRuleType.getConfigRuleValues().size();
		configRuleType.getConfigRuleValues().add(index, newRuleValue);

	}

	public void checkStatus(int index) {
		log.info(".......checkStatus........");
		// log.info("....index="+index);
		this.setCurrentIndexVal(index);
		PmConfigRule configRuleType = this.getManagedBean()
				.getSelectedRuleDescType().getRuleType();
		PmConfigRuleValue selectedValue = configRuleType.getConfigRuleValues()
				.get(index);
		// log.info(".................selectedValue="+selectedValue);
		// log.info(".................selectedValue.getRecordStatusFlg()="+selectedValue.getRecordStatusFlg());
		if (selectedValue.getRecordStatusFlg().intValue() == DbValueConst.RECORD_STATUS_ACTIVE) {// change
																									// from
																									// active
																									// to
																									// inactive
			// log.info(".................selectedValue.getRecordStatusFlg()="+selectedValue.getRecordStatusFlg());
			// prompt popoup
			// prepare popup msg
			setInactivePopUpMsg(PmCommonUtil.getMessage(
					"lbl.pm.inactive.popUp.msg",
					new String[] { configRuleType.getRuleType() }));

			RequestContext.getCurrentInstance().execute(
					"statusConfirmDialog.show();");
			return;
		}

	}

	public void checkFeeStatusForOrg(int index) {
		log.info(".......checkFeeStatusForOrg........");
		this.setCurrentIndexVal(index);
		PmFeeExemption fee = this.getManagedBean()
				.getFeeExemptionsForOrgAndItem().get(index);
		if (fee != null) {
			if (fee.getRecordStatusFlg().intValue() == DbValueConst.RECORD_STATUS_ACTIVE) {// change
																							// from
																							// active
																							// to
																							// inactive
				// log.info(".................selectedValue.getRecordStatusFlg()="+selectedValue.getRecordStatusFlg());
				// prompt popoup
				// prepare popup Msg

				if (fee.getFeeExemptionType()
						.equals(ProfileManagementConst.FEE_EXEMPTION_TYPE_ORG_AND_SUPPLIER)
						&& fee.getItemId().getItemId() == null) {
					setInactiveFeeOrgPopUpMsg(PmCommonUtil.getMessage(
							"lbl.pm.inactive.fee.popUp.msg", new String[] { fee
									.getOrgProfileId().getSmSupplier()
									.getCompanyName() }));

				} else if (fee.getFeeExemptionType().equals(
						ProfileManagementConst.FEE_EXEMPTION_TYPE_ORG_AND_ITEM)
						|| (fee.getFeeExemptionType()
								.equals(ProfileManagementConst.FEE_EXEMPTION_TYPE_ORG_AND_SUPPLIER) && fee
								.getItemId().getItemId() != null)) {
					setInactiveFeeOrgPopUpMsg(PmCommonUtil.getMessage(
							"lbl.pm.inactive.fee.popUp.msg", new String[] { fee
									.getOrgProfileId().getSmSupplier()
									.getCompanyName()
									+ ", " + fee.getItemId().getItemName() }));
				}

				RequestContext.getCurrentInstance().execute(
						"feeOrgStatusConfirmDialog.show();");
			}
			return;
		}

	}

	public void checkFeeStatusForCommodity(int index) {
		log.info(".......checkFeeStatusForCommodity........");
		this.setCurrentIndexVal(index);
		PmFeeExemption fee = this.getManagedBean()
				.getFeeExemptionsForComodity().get(index);
		if (fee != null) {
			if (fee.getRecordStatusFlg().intValue() == DbValueConst.RECORD_STATUS_ACTIVE) {// change
																							// from
																							// active
																							// to
																							// inactive
				// log.info(".................selectedValue.getRecordStatusFlg()="+selectedValue.getRecordStatusFlg());
				// prompt popoup
				// prepare popup msg

				if (fee.getFeeExemptionType()
						.equals(ProfileManagementConst.FEE_EXEMPTION_TYPE_COMMONDITY_AND_TYPE)
						&& fee.getTypeId().getTypeId() == null) {
					setInactiveFeeComoPopUpMsg(PmCommonUtil.getMessage(
							"lbl.pm.inactive.fee.popUp.msg", new String[] { fee
									.getUnspscId().getUnspscTitle() }));
				} else if (fee.getFeeExemptionType().equals(
						ProfileManagementConst.FEE_EXEMPTION_TYPE_COMMODITY)
						|| (fee.getFeeExemptionType()
								.equals(ProfileManagementConst.FEE_EXEMPTION_TYPE_COMMONDITY_AND_TYPE) && fee
								.getTypeId().getTypeId() != null)) {
					setInactiveFeeComoPopUpMsg(PmCommonUtil.getMessage(
							"lbl.pm.inactive.fee.popUp.msg", new String[] { fee
									.getUnspscId().getUnspscTitle()
									+ ", "
									+ fee.getTypeId().getTypeName() }));
				}

				RequestContext.getCurrentInstance().execute(
						"feeCommodityStatusConfirmDialog.show();");
			}
			return;
		}

	}

	public void checkFeeStatusForContract(int index) {
		log.info(".......checkFeeStatusForContract........");
		this.setCurrentIndexVal(index);
		PmFeeExemption fee = this.getManagedBean()
				.getFeeExemptionsForContract().get(index);
		if (fee != null) {
			if (fee.getRecordStatusFlg().intValue() == DbValueConst.RECORD_STATUS_ACTIVE) {// change
																							// from
																							// active
																							// to
																							// inactive
				// log.info(".................selectedValue.getRecordStatusFlg()="+selectedValue.getRecordStatusFlg());
				// prompt popoup
				// prepare popup msg

				if (fee.getFeeExemptionType().equals(
						ProfileManagementConst.FEE_EXEMPTION_TYPE_CONTRACT)
						&& fee.getRequestItemId().getRequestNoteItemId() == null) {
					setInactiveFeeContPopUpMsg(PmCommonUtil.getMessage(
							"lbl.pm.inactive.fee.popUp.msg", new String[] { fee
									.getContractId().getContractNo() }));
				} else if (fee
						.getFeeExemptionType()
						.equals(ProfileManagementConst.FEE_EXEMPTION_TYPE_CONTRACT_AND_CONTRACTED_ITEM)
						|| (fee.getFeeExemptionType()
								.equals(ProfileManagementConst.FEE_EXEMPTION_TYPE_CONTRACT) && fee
								.getRequestItemId().getRequestNoteItemId() != null)) {
					setInactiveFeeContPopUpMsg(PmCommonUtil.getMessage(
							"lbl.pm.inactive.fee.popUp.msg", new String[] { fee
									.getContractId().getContractNo()
									+ ", "
									+ fee.getRequestItemId().getItemName() }));
				}

				RequestContext.getCurrentInstance().execute(
						"feeContractStatusConfirmDialog.show();");
			}
			return;
		}

	}

	public void deleteMoreRuleValue() {
		//#30672 - Start
		RequestContext.getCurrentInstance().reset("form:settingTabId:ruleTypeValueTableID"); // reset form UI validation error
		//#30672 - End
		
		Integer rowIndex = getManagedBean().getDeleteRowIndex();
		List<PmConfigRuleValue> removeList = new ArrayList<PmConfigRuleValue>();
		PmConfigRule configRuleType = this.getManagedBean()
				.getSelectedRuleDescType().getRuleType();
		
		//#12177 - Start
		if(configRuleType != null){
			if(configRuleType.getRuleType().equals(RuleTypeConst.SIMPLE_QUOTE_VALIDITY_PERIOD)){
				NgepApplicationBean.resetSqTimeValidity();
			}
		}
		//#12177 - End
		
		PmConfigRuleValue selectedValueToDel = configRuleType
				.getConfigRuleValues().get(rowIndex.intValue());

		if (configRuleType.getRangeType() == 1
				&& selectedValueToDel.isShowFlag()) {// if its range and parent
														// record, delete all
														// child
			Integer groupIdOfParent = selectedValueToDel.getGroupValueId();
			for (PmConfigRuleValue value : configRuleType.getConfigRuleValues()) {
				if (value.getGroupValueId().equals(groupIdOfParent)) {
					removeList.add(value);
				}
			}
			configRuleType.getConfigRuleValues().removeAll(removeList);
		} else {
			configRuleType.getConfigRuleValues().remove(rowIndex.intValue());
		}

		// merge selectedRuleType with changes in values into ConfigRuleTypes
		// this.getManagedBean().getConfigRuleList().add(indexOfSelectedRuleType,
		// this.getManagedBean().getSelectedRuleType());
		// for(PmConfigRuleValue value :
		// this.getManagedBean().getConfigRuleList().get(indexOfSelectedRuleType).getConfigRuleValues()){
		// log.info(".......................value.getRuleValue()="+value.getRuleValue());
		// }

	}

	public void addMoreOrgAndItemFee() {
		log.info(".................addMoreOrgAndItemFee...................");
		PmFeeExemption newFeeExemption = new PmFeeExemption();
		newFeeExemption
				.setFeeExemptionType(ProfileManagementConst.FEE_EXEMPTION_TYPE_ORG_AND_SUPPLIER);
		newFeeExemption.setEffDate(this.getManagedBean().getToday());// future
																		// date
		newFeeExemption.setItemId(new CmItem());// make sure to delete the new
												// objs before update if doesn't
												// selected
		newFeeExemption.setOrgProfileId(new PmOrgProfile());

		int index = this.getManagedBean().getFeeExemptionsForOrgAndItem()
				.size();
		this.getManagedBean().getFeeExemptionsForOrgAndItem()
				.add(index, newFeeExemption);
		RequestContext.getCurrentInstance().execute(
				"feeByOrgItemTableWidVar.paginator.endLink.click()");

	}

	public void deleteOrgAndItemFee() {
		log.info(".................deleteOrgAndItemFee...................");
		Integer rowIndex = getManagedBean().getDeleteRowIndex();
		if (rowIndex != null) {
			PmFeeExemption deletedExemption = this.getManagedBean()
					.getFeeExemptionsForOrgAndItem().get(rowIndex.intValue());
			if (deletedExemption != null) {
				deletedExemption.setDeleted(true);
				this.getManagedBean().getDeletedOrgAndItemFeeExemptions()
						.add(deletedExemption);// should added to original list
												// before
												// save
				this.getManagedBean().getFeeExemptionsForOrgAndItem()
						.remove(deletedExemption);
			}
		}

	}

	public void deleteOperationalThreshold(int rowIndex) {
		log.info(".................deleteOperationalThreshold...................");
		PmThreshold deletedThreshold = this.getManagedBean().getThresholds()
				.get(rowIndex);
		deletedThreshold.setDeleted(true);
		this.getManagedBean().getDeletedThresholds().add(deletedThreshold);// should
																			// added
																			// to
																			// original
																			// list
																			// before
																			// save
		this.getManagedBean().getThresholds().remove(deletedThreshold);

	}

	public void addMoreComodityFee() {
		log.info(".................addMoreComodityFee...................");
		PmFeeExemption newFeeExemption = new PmFeeExemption();
		newFeeExemption
				.setFeeExemptionType(ProfileManagementConst.FEE_EXEMPTION_TYPE_COMMONDITY_AND_TYPE);
		newFeeExemption.setEffDate(this.getManagedBean().getToday());// make
																		// sure
																		// to
																		// delete
																		// the
																		// new
		// objs before update if doesn't
		// selected
		newFeeExemption.setTypeId(new CmType());
		newFeeExemption.setUnspscId(new CmUnspsc());

		int index = this.getManagedBean().getFeeExemptionsForComodity().size();
		this.getManagedBean().getFeeExemptionsForComodity()
				.add(index, newFeeExemption);
		RequestContext.getCurrentInstance().execute(
				"feeByComodityTableWidVar.paginator.endLink.click()");

	}

	public void addMoreOrgTypeFee() {
		log.info(".................addMoreOrgTypeFee...................");
		PmFeeExemption newFeeExemption = new PmFeeExemption();
		newFeeExemption
				.setFeeExemptionType(ProfileManagementConst.FEE_EXEMPTION_TYPE_ORGTYPE);
		newFeeExemption.setEffDate(this.getManagedBean().getToday());// make
																		// sure
																		// to
																		// delete
																		// the
																		// new
		// objs before update if doesn't
		// selected
		newFeeExemption.setOrgTypeId(new PmParameter());

		int index = this.getManagedBean().getFeeExemptionsForOrgType().size();
		this.getManagedBean().getFeeExemptionsForOrgType()
				.add(index, newFeeExemption);
		RequestContext.getCurrentInstance().execute(
				"feeByOrgTypeTableWidVar.paginator.endLink.click()");

	}

	public void deleteComodityFee() {
		log.info(".................deleteComodityFee...................");
		Integer rowIndex = getManagedBean().getDeleteRowIndex();
		if (rowIndex != null) {
			PmFeeExemption deletedExemption = this.getManagedBean()
					.getFeeExemptionsForComodity().get(rowIndex.intValue());
			if (deletedExemption != null) {
				deletedExemption.setDeleted(true);
				this.getManagedBean().getDeletedComodityFeeExemptions()
						.add(deletedExemption);// should added to original list
												// before
												// save
				this.getManagedBean().getFeeExemptionsForComodity()
						.remove(deletedExemption);
			}
		}
	}

	public void deleteOrgTypeFee() {
		log.info(".................deleteOrgTypeFee...................");
		Integer rowIndex = getManagedBean().getDeleteRowIndex();
		if (rowIndex != null) {
			PmFeeExemption deletedExemption = this.getManagedBean()
					.getFeeExemptionsForOrgType().get(rowIndex.intValue());
			if (deletedExemption != null) {
				deletedExemption.setDeleted(true);
				this.getManagedBean().getDeletedOrgTypeFeeExemptions()
						.add(deletedExemption);// should added to original list
												// before
												// save
				this.getManagedBean().getFeeExemptionsForOrgType()
						.remove(deletedExemption);
			}
		}

	}

	public void addMoreContractFee() {
		log.info(".................addMoreContractFee...................");
		PmFeeExemption newFeeExemption = new PmFeeExemption();
		newFeeExemption
				.setFeeExemptionType(ProfileManagementConst.FEE_EXEMPTION_TYPE_CONTRACT);
		newFeeExemption.setEffDate(this.getManagedBean().getToday());
		newFeeExemption.setRequestItemId(new ScRequestItem());
		newFeeExemption.setContractId(new CtContract());

		int index = this.getManagedBean().getFeeExemptionsForContract().size();
		this.getManagedBean().getFeeExemptionsForContract()
				.add(index, newFeeExemption);
		RequestContext.getCurrentInstance().execute(
				"feeByContractTableWidVar.paginator.endLink.click()");

	}

	public void deleteContractFee() {
		log.info(".................deleteContractFee...................");
		Integer rowIndex = getManagedBean().getDeleteRowIndex();
		if (rowIndex != null) {
			PmFeeExemption deletedExemption = this.getManagedBean()
					.getFeeExemptionsForContract().get(rowIndex.intValue());
			if (deletedExemption != null) {
				deletedExemption.setDeleted(true);
				this.getManagedBean().getDeletedContractFeeExemptions()
						.add(deletedExemption);// should added to original list
												// before
												// save
				this.getManagedBean().getFeeExemptionsForContract()
						.remove(deletedExemption);
			}
		}
	}

	public void saveRuleCatalog() {

		// make sure to delete the new obj before update if doesn't selected
		for (PmConfigRuleDesc ruleTypeDesc : this.getManagedBean()
				.getConfigRuleDescList()) {

			if (ruleTypeDesc.getRuleType().getRuleDataTypeId()
					.getParameterCode().equals("C")) {
				for (PmConfigRuleValue ruleTypeValue : ruleTypeDesc
						.getRuleType().getConfigRuleValues()) {
					if (ruleTypeValue.getCategoryL3Id() != null
							&& ruleTypeValue.getCategoryL3Id()
									.getCategoryL3Id() == null) {
						// throw required msg
						// 29669,30672 start
						throw new EpException(MessageConst.ERROR_770133,
								new String[] { ruleTypeDesc.getRuleDesc() });
						// 29669,30672 end

					} else { // fill CategoryDisplayValue for further validation
						PmCategoryL3 categoryL3 = ruleTypeValue
								.getCategoryL3Id();
						if (categoryL3 != null) {
							String categoryL3Code = categoryL3
									.getCategoryL3Code();
							String categoryL2Code = categoryL3
									.getPmCategoryL2().getCategoryL2Code();
							String categoryL1Code = categoryL3
									.getPmCategoryL2().getPmCategoryL1()
									.getCategoryL1Code();
							ruleTypeValue
									.setCategoryDisplayValue(categoryL1Code
											+ categoryL2Code + categoryL3Code);
						}
					}

				}

			}
			// check validation
			
			// 29669,29641,30672 start
			if (ruleTypeDesc.getRuleType().getRuleDataTypeId().getParameterCode().matches("A|N|M|P") 
					&& ruleTypeDesc.getRuleType().getRangeType() != 2) {
				for (PmConfigRuleValue ruleTypeValue : ruleTypeDesc.getRuleType().getConfigRuleValues()) {
					if (ruleTypeValue.getRuleValue() == null) {
						throw new EpException(MessageConst.ERROR_770133,
								new String[] { ruleTypeDesc.getRuleDesc() });
					}
				}
			}
			// 29669,29641,30672 end
			
			for (int i = 0; i < ruleTypeDesc.getRuleType()
					.getConfigRuleValues().size(); i++) {
				// add new values to tracking Diary list

				PmConfigRuleValue baseValueToCompare = ruleTypeDesc
						.getRuleType().getConfigRuleValues().get(i);

				if (baseValueToCompare.getRecordStatusFlg() == 0) {// if is
					// inActive
					// one go to
					// next
					// record\
					continue;
				} else if ((ruleTypeDesc.getRuleType().getRangeType() == 1 || ruleTypeDesc
						.getRuleType().getRangeType() == 2)
						&& baseValueToCompare.isError()) {// to value can not be
															// less than from
															// value
					throw new EpException(MessageConst.ERROR_770210,
							new String[] { ruleTypeDesc.getRuleDesc() });
				}

				// 30672 - start 19/07/2022
				if (ruleTypeDesc.getRuleType().getRangeType() != 1
						|| (ruleTypeDesc.getRuleType().getRangeType() == 1 && baseValueToCompare.isShowFlag())) {
					if (baseValueToCompare.isErrorExpDate()) {
						if (!this.getManagedBean().getSelectedRuleDescType().getRuleType().getRuleType()
								.equals(ruleTypeDesc.getRuleType().getRuleType())) {
							throw new EpException(MessageConst.ERROR_770111,
									new String[] { ruleTypeDesc.getRuleDesc() });
						} else {
							throw new EpException(MessageConst.ERROR_770113);
						}
					}
				}
				// 30672 - end 19/07/2022
				
				for (int j = i + 1; j <= (ruleTypeDesc.getRuleType()
						.getConfigRuleValues().size() - 1); j++) {
					PmConfigRuleValue value = ruleTypeDesc.getRuleType()
							.getConfigRuleValues().get(j);

					if (value.getRecordStatusFlg() == 0) {// if is inActive one
						// go to next record
						continue;
					} else if ((ruleTypeDesc.getRuleType().getRangeType() == 1 || ruleTypeDesc
							.getRuleType().getRangeType() == 2)
							&& value.isError()) { // to
						// value
						// can
						// not
						// be
						// less
						// than
						// from
						// value
						throw new EpException(MessageConst.ERROR_770210,
								new String[] { ruleTypeDesc.getRuleDesc() });
					}

					if (!ruleValueValidation(baseValueToCompare, value,
							ruleTypeDesc.getRuleType())) {
						// log.info("...............ruleType.getValidationType()="+ruleType.getValidationType());

						// hit error and return
						switch (ruleTypeDesc.getRuleType().getValidationType()) {
						case '1':
							if (!ruleTypeDesc.getRuleType().getRuleType().equals("LOPO")) {
								if (ruleTypeDesc.getRuleType().getRangeType() == 1
										&& baseValueToCompare.getGroupValueId()
												.equals(value.getGroupValueId())) {
	
									throw new EpException(
											MessageConst.ERROR_770110,
											new String[] { ruleTypeDesc
													.getRuleDesc() });
								} else {
	
									throw new EpException(
											MessageConst.ERROR_770109,
											new String[] { ruleTypeDesc
													.getRuleDesc() });
								}
							}
						case '2':
							if (!ruleTypeDesc.getRuleType().getRuleType().equals("LOPO")) {
								if (ruleTypeDesc.getRuleType().getRuleDataTypeId()
										.getParameterCode().equalsIgnoreCase("c")) {
									throw new EpException(
											MessageConst.ERROR_770199,
											new String[] {
													ruleTypeDesc.getRuleDesc(),
													value.getCategoryDisplayValue() });
								} else {
									throw new EpException(
											MessageConst.ERROR_770110,
											new String[] { ruleTypeDesc
													.getRuleDesc() });
								}
							}

						case '3':
							if (!ruleTypeDesc.getRuleType().getRuleType().equals("LOPO")) {
								throw new EpException(MessageConst.ERROR_770154,
										new String[] { ruleTypeDesc.getRuleDesc() });
							}

						}

					}

					// }

				}

			}

		}

		IRuleSO ruleSO = null;
		try {
			ruleSO = (IRuleSO) BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IRuleSO.class);
			RuleReqDTO reqDTO = new RuleReqDTO();
			setupTrackingDiaryVal(reqDTO);
			
			List<PmConfigRule> ruleTypeList = new ArrayList<PmConfigRule>();
			for (PmConfigRuleDesc desc : getManagedBean()
					.getConfigRuleDescList()) {
				if (desc.getRuleType() != null) {
					ruleTypeList.add(desc.getRuleType());
				}
			}
			reqDTO.setRuleTypeList(ruleTypeList);
			RuleResDTO resDTO = ruleSO.updateRuleValueList(reqDTO);
			// clear the catch and retrive updated list from DB

			RuleConfigHelper.getInstance().relodeRuleTypeValueMaps();

			// this.getManagedBean().setConfigRuleList(
			// RuleConfigHelper.getInstance().getRuleTypeList());
			// this.getManagedBean().setSelectedRuleType(
			// RuleConfigHelper.getInstance().getRuleTypeList().get(0));
			List<PmConfigRuleDesc> list = retriveConfigRuleDescFromDB();
			this.getManagedBean().setConfigRuleDescList(list);

			Long configRuleId = getManagedBean().getSelectedRuleDescType()
					.getRuleType().getRuleTypeId();
			for (PmConfigRuleDesc desc : list) {
				if (configRuleId.equals(desc.getRuleType().getRuleTypeId())) {
					this.getManagedBean().setSelectedRuleDescType(desc);
					break;
				}
			}
			// this.getManagedBean().setSelectedRuleType(list.get(0));

			if (resDTO.isSuccessful()) {
				FacesContext facesContext = FacesContext.getCurrentInstance();
				facesContext.addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, null,
								MessageUtils.getMessage(
										MessageConst.INFO_770009,
										MessageUtils.BUNDLE_COMMON_MESSAGES,
										new FacesScopes().getUserLocale())));

			}
		} catch (Exception e) {
			throw new EpException(e.getMessage());
		}
	}
	
	private void setupTrackingDiaryVal(RuleReqDTO reqDTO){
		reqDTO.setTrackDairyVOs(new ArrayList<PmTrackingDairyVO>());
		for (PmConfigRuleDesc ruleTypeDesc : this.getManagedBean()
				.getConfigRuleDescList()) {

			for (PmConfigRuleValue ruleTypeValue : ruleTypeDesc
					.getRuleType().getConfigRuleValues()) {
				String messageId = null;
				String[] msgParam = null;
				if (ruleTypeValue.isBrandNew()) {

					messageId = MessageConst.TD_80200;
					if (ParameterConst.CONFIG_RULE_DATA_CATEGORY_CODE
							.equals(ruleTypeDesc.getRuleType().getRuleDataTypeId()
							.getParameterCode())) {
						msgParam = new String[] {
								ruleTypeValue.getCategoryDisplayValue(),
								getRuleTypeDesc(ruleTypeDesc.getRuleType()
										.getRuleType(),
										CommonConstants.LANGUAGE_CODE_MALAY) };

					} else {
						msgParam = new String[] {
								ruleTypeValue.getRuleValue(),
								getRuleTypeDesc(ruleTypeDesc.getRuleType()
										.getRuleType(),
										CommonConstants.LANGUAGE_CODE_MALAY) };

					}
				} else {
					if (ruleTypeValue.isModified()
							&& ruleTypeValue.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_SUSPENDED) {

						messageId = MessageConst.TD_80201;
						if (ParameterConst.CONFIG_RULE_DATA_CATEGORY_CODE
								.equals(ruleTypeDesc.getRuleType().getRuleDataTypeId()
								.getParameterCode())) {
							msgParam = new String[] {
									ruleTypeValue.getCategoryDisplayValue(),
									getRuleTypeDesc(
											ruleTypeDesc.getRuleType()
													.getRuleType(),
											CommonConstants.LANGUAGE_CODE_MALAY) };

						} else {
							msgParam = new String[] {
									ruleTypeValue.getRuleValue(),
									getRuleTypeDesc(
											ruleTypeDesc.getRuleType()
													.getRuleType(),
											CommonConstants.LANGUAGE_CODE_MALAY) };

						}
					}else if(ruleTypeValue.isModified()){
						messageId = MessageConst.TD_80221;
						msgParam = new String[] {
								getRuleTypeDesc(ruleTypeDesc.getRuleType()
										.getRuleType(),
										CommonConstants.LANGUAGE_CODE_MALAY),
										ruleTypeValue.getRuleValue(),
										ruleTypeValue.getEffDate() != null ? 
												DateUtils.dateToString("dd/MM/yyyy",ruleTypeValue.getEffDate()) : "",
										ruleTypeValue.getExpDate() != null ? 
												DateUtils.dateToString("dd/MM/yyyy",ruleTypeValue.getExpDate()) : ""
								};
					}
				}

				if (messageId != null & msgParam != null) {
					reqDTO.setTrackingStatus(true);
					reqDTO.getTrackDairyVOs().add(
							PmCommonUtil.assignTrackingDairyForRuleConfig(
									ruleTypeDesc.getRuleType(),
									getActionDesc(messageId, msgParam),
									ruleTypeValue.getRecordStatusFlg()
											.longValue()));
				}

			}

		}
	}

	private String getActionDesc(String messageId, String[] params) {
		Locale myLocale = new Locale("ms", "MY");
		return MessageUtils.getMessage(messageId, params,
				MessageUtils.BUNDLE_COMMON_MESSAGES, myLocale);
	}

	private boolean ruleValueValidation(PmConfigRuleValue firstRuleValue,
			PmConfigRuleValue secondRuleValue, PmConfigRule ruleType) {
		log.info("........ruleValueValidation >> " + ruleType.getRuleType());
		boolean validationResult = false;
		boolean isValuesEqual = false;
		if (ruleType.getValidationType() == '1'
				&& (ruleType.getRangeType() == 2 || !(ruleType.getRangeType() == 1 && firstRuleValue
						.getGroupValueId().equals(
								secondRuleValue.getGroupValueId())))) {// EffectiveDate
																		// validation
																		// //
																		// don't
																		// validate
																		// date
																		// for
																		// values
																		// in a
																		// group

			validationResult = !isDateOverlaped(firstRuleValue.getEffDate(),
					firstRuleValue.getExpDate(), secondRuleValue.getEffDate(),
					secondRuleValue.getExpDate());

		} else if ((ruleType.getValidationType() == '2' || ruleType
				.getValidationType() == '3')
				&& (ruleType.getRangeType() != 2 && !(ruleType.getRangeType() == 1 && firstRuleValue
						.getGroupValueId().equals(
								secondRuleValue.getGroupValueId())))) {// EffectiveDate
			// +value
			// validation

			if (ruleType.getRuleDataTypeId().getParameterCode()
					.equalsIgnoreCase("c")) {
				if (firstRuleValue.getCategoryDisplayValue().equals(
						secondRuleValue.getCategoryDisplayValue()))
					isValuesEqual = true;
			} else {
				if (firstRuleValue.getRuleValue().equals(
						secondRuleValue.getRuleValue()))
					isValuesEqual = true;

			}
			if (isValuesEqual) {// don't
								// validate
								// date
								// for
								// values
								// in
								// a
								// group
				validationResult = !isDateOverlaped(
						firstRuleValue.getEffDate(),
						firstRuleValue.getExpDate(),
						secondRuleValue.getEffDate(),
						secondRuleValue.getExpDate());
			} else {

				validationResult = true;
			}

		} else if (ruleType.getRangeType() == 1
				&& firstRuleValue.getGroupValueId().equals(
						secondRuleValue.getGroupValueId())) {// step-2
																// validation :
																// validate
																// values in a
																// group

			if (ruleType.getRuleDataTypeId().getParameterCode()
					.equalsIgnoreCase("c")) {// category Code
				validationResult = !(firstRuleValue.getCategoryDisplayValue()
						.equals(secondRuleValue.getCategoryDisplayValue()));
			} else if (ruleType.getRuleDataTypeId().getParameterCode()
					.equalsIgnoreCase("m")) {// parameter code
				validationResult = !(firstRuleValue.getRuleValue()
						.equals(secondRuleValue.getRuleValue()));
			} else {
				validationResult = true;
			}
		}
		log.info("........validationResult=" + validationResult);
		return validationResult;
	}

	boolean isRangeOverlaped(Double fromValue1, Double toValue1,
			Double fromValue2, Double toValue2) {
		// if (toValue1 == null) {
		// return fromValue1 <= toValue2;
		//
		// }
		// if (toValue2 == null) {
		// return fromValue2 <= toValue1;
		//
		// }
		return fromValue1 <= toValue2 && fromValue2 <= toValue1;
	}

	boolean isDateOverlaped(Date start1, Date end1, Date start2, Date end2) {
		log.info("..........isDateOverlaped............");
		log.info("..........start1=" + start1 + "......end1=" + end1 + "......");
		log.info("..........start2=" + start2 + "......end2=" + end2 + "......");
		if (end1 == null && end2 == null) {

			return true;
		}
		if (end1 == null) {
			return start1.getTime() <= end2.getTime();

		}
		if (end2 == null) {
			return start2.getTime() <= end1.getTime();

		}
		return start1.getTime() <= end2.getTime()
				&& start2.getTime() <= end1.getTime();
	}

	public void saveFeeExemptionForOrgType() {
		log.info("saveFeeExemptionForOrgType");
		List<PmFeeExemption> exemptionList = new ArrayList<PmFeeExemption>();
		exemptionList
				.addAll(this.getManagedBean().getFeeExemptionsForOrgType());

		for (PmFeeExemption exemption1 : exemptionList) {
			if (exemption1.getOrgTypeId() != null
					&& exemption1.getOrgTypeId().getParameterId() == null) {
				// throw required msg
				throw new EpException(MessageConst.ERROR_770212,
						new String[] {});
			}

			if (exemption1.getFeeExemptionId() == null) {// set ExemptionType

				exemption1
						.setFeeExemptionType(ProfileManagementConst.FEE_EXEMPTION_TYPE_ORGTYPE);

			}
		}
		if (isDateOverlappedForExemptionType(exemptionList)) {
			for (PmFeeExemption ex : exemptionList) {
				if (ex.getOrgTypeId() == null) {
					ex.setOrgTypeId(new PmParameter());
				}
			}
			// hit error and return

			throw new EpException(MessageConst.ERROR_770213, new String[] {
					ResourceBundleUtils.getMessage(
							"lbl.pm.settings.fee.organisation.type",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							new FacesScopes().getUserLocale()),
					this.dupplicateOrgTypeName });
		}
		// add deleted records before update
		exemptionList.addAll(this.getManagedBean()
				.getDeletedOrgTypeFeeExemptions());

		IFeeExemptionSO exemptionSO = null;
		FeeExemptionReqDTO reqDTO = new FeeExemptionReqDTO();
		IStatusSO statusSO = (IStatusSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IStatusSO.class);
		StatusReqDTO statusReqDTO = null;
		for (PmFeeExemption fee : exemptionList) {
			String messageId = null;
			String[] msgParam = null;
			if (!fee.isBrandNew()
					&& fee.isModified()
					&& fee.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_SUSPENDED) {
				messageId = MessageConst.TD_80207;

				msgParam = new String[] {
						fee.getOrgTypeId().getParameterCode(), "" };

			}

			if (messageId != null & msgParam != null) {
				reqDTO.setTrackingStatus(true);
				reqDTO.getTrackDairyVOs().add(
						PmCommonUtil.assignTrackingDairyForFeeExemption(fee,
								getActionDesc(messageId, msgParam), fee
										.getRecordStatusFlg().longValue()));
			}

		}
		statusReqDTO = new StatusReqDTO();
		statusReqDTO.setStatusId(Long
				.valueOf(DbValueConst.RECORD_STATUS_ACTIVE));// 1
		StatusResDTO statusResDTO = statusSO
				.retrieveStatusByStatusId(statusReqDTO);
		reqDTO.setStatus(statusResDTO.getPmStatus() != null ? statusResDTO
				.getPmStatus() : new PmStatus());
		reqDTO.setUser(new FacesScopes().getUserSessionBean());
		reqDTO.setUserRole(PmCommonUtil.getUserRoleValue());
		// //////////////////
		try {
			exemptionSO = (IFeeExemptionSO) BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IFeeExemptionSO.class);

			reqDTO.setFeeExemptionList(exemptionList);
			FeeExemptionResDTO resDTO = exemptionSO
					.updateFeeExemptionList(reqDTO);
			// retrive
			this.getManagedBean().getFeeExemptionsForOrgType().clear();
			this.getManagedBean().getDeletedOrgTypeFeeExemptions().clear();
			reqDTO = new FeeExemptionReqDTO();
			resDTO = exemptionSO.getFeeExemptionsForOrgType(reqDTO);
			this.getManagedBean().setFeeExemptionsForOrgType(
					resDTO.getFeeExemptions());

			if (resDTO.isSuccessful()) {
				FacesContext facesContext = FacesContext.getCurrentInstance();
				facesContext.addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, null,
								MessageUtils.getMessage(
										MessageConst.INFO_770010,
										MessageUtils.BUNDLE_COMMON_MESSAGES,
										new FacesScopes().getUserLocale())));

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void saveFeeExemptionForOrgItem() {

		List<PmFeeExemption> exemptionList = new ArrayList<PmFeeExemption>();
		exemptionList.addAll(this.getManagedBean()
				.getFeeExemptionsForOrgAndItem());

		for (PmFeeExemption exemption : exemptionList) {
			if (exemption.getOrgProfileId() != null
					&& exemption.getOrgProfileId().getOrgProfileId() == null) {// throw
																				// required
																				// msg
				// hit error and return
				throw new EpException(MessageConst.ERROR_770130,
						new String[] {});
			}
			if (exemption.getItemId() != null
					&& exemption.getItemId().getItemId() == null) {// clear
																	// empty
				// objects
				exemption.setItemId(null);
			}
			if (exemption.getFeeExemptionId() == null) {// set ExemptionType for
														// new FeeExemptions
				if (exemption.getItemId() == null) {
					exemption
							.setFeeExemptionType(ProfileManagementConst.FEE_EXEMPTION_TYPE_ORG_AND_SUPPLIER);
				} else {
					exemption
							.setFeeExemptionType(ProfileManagementConst.FEE_EXEMPTION_TYPE_ORG_AND_ITEM);
				}
			} else if (exemption.getFeeExemptionType().equals(
					ProfileManagementConst.FEE_EXEMPTION_TYPE_ORG_AND_SUPPLIER)
					&& exemption.getItemId() != null) {
				// update exemptionType if Item is selected
				exemption
						.setFeeExemptionType(ProfileManagementConst.FEE_EXEMPTION_TYPE_ORG_AND_ITEM);
			}
		}
		if (isDateOverlappedForExemptionType(exemptionList)) {
			for (PmFeeExemption ex : exemptionList) {
				if (ex.getItemId() == null) {
					ex.setItemId(new CmItem());
				}
			}

			// hit error and return
			throw new EpException(MessageConst.ERROR_770112, new String[] {});
		}
		// add deleted list before update
		exemptionList.addAll(this.getManagedBean()
				.getDeletedOrgAndItemFeeExemptions());
		for (PmFeeExemption exe : exemptionList) {// befor update clear remained
													// empty objs
			if (exe.getItemId() != null && exe.getItemId().getItemId() == null) {// clear
																					// empty
				// objects
				exe.setItemId(null);
			}
		}
		IFeeExemptionSO exemptionSO = null;
		FeeExemptionReqDTO reqDTO = new FeeExemptionReqDTO();
		// /////////////////////////////////////////add to Tracking Dairy
		IStatusSO statusSO = (IStatusSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IStatusSO.class);
		StatusReqDTO statusReqDTO = null;
		for (PmFeeExemption fee : exemptionList) {
			String messageId = null;
			String[] msgParam = null;

			if (!fee.isBrandNew()
					&& fee.isModified()
					&& fee.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_SUSPENDED) {

				messageId = MessageConst.TD_80207;
				if (fee.getFeeExemptionType()
						.equals(ProfileManagementConst.FEE_EXEMPTION_TYPE_ORG_AND_SUPPLIER)) {
					msgParam = new String[] {
							fee.getOrgProfileId().getSmSupplier()
									.getCompanyName(), "" };

				} else {// feeExemptionType is 4
					msgParam = new String[] {
							fee.getOrgProfileId().getSmSupplier()
									.getCompanyName(),
							fee.getItemId().getItemName() };
				}
			}
			if (messageId != null & msgParam != null) {
				reqDTO.setTrackingStatus(true);
				reqDTO.getTrackDairyVOs().add(
						PmCommonUtil.assignTrackingDairyForFeeExemption(fee,
								getActionDesc(messageId, msgParam), fee
										.getRecordStatusFlg().longValue()));
			}

		}
		statusReqDTO = new StatusReqDTO();
		statusReqDTO.setStatusId(Long
				.valueOf(DbValueConst.RECORD_STATUS_ACTIVE));// 1
		StatusResDTO statusResDTO = statusSO
				.retrieveStatusByStatusId(statusReqDTO);
		reqDTO.setStatus(statusResDTO.getPmStatus() != null ? statusResDTO
				.getPmStatus() : new PmStatus());
		reqDTO.setUser(new FacesScopes().getUserSessionBean());
		reqDTO.setUserRole(PmCommonUtil.getUserRoleValue());
		// ////////////////
		try {
			exemptionSO = (IFeeExemptionSO) BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IFeeExemptionSO.class);

			reqDTO.setFeeExemptionList(exemptionList);

			FeeExemptionResDTO resDTO = exemptionSO
					.updateFeeExemptionList(reqDTO);
			// retrieve
			this.getManagedBean().getFeeExemptionsForOrgAndItem().clear();
			this.getManagedBean().getDeletedOrgAndItemFeeExemptions().clear();
			reqDTO = new FeeExemptionReqDTO();
			resDTO = exemptionSO.getFeeExemptionsForOrgAndItem(reqDTO);
			for (PmFeeExemption exemption : resDTO.getFeeExemptions()) {
				if (exemption.getItemId() == null) {// TODO: clear it before
													// updating the fee list
					exemption.setItemId(new CmItem());
				}
				this.getManagedBean().getFeeExemptionsForOrgAndItem()
						.add(exemption);
			}
			if (resDTO.isSuccessful()) {
				FacesContext facesContext = FacesContext.getCurrentInstance();
				facesContext.addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, null,
								MessageUtils.getMessage(
										MessageConst.INFO_770010,
										MessageUtils.BUNDLE_COMMON_MESSAGES,
										new FacesScopes().getUserLocale())));

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void isDateOverlappedForDesignatedArea(
			List<PmDaValidity> daValidities) {
		log.info("..............isDateOverlappedForDesignatedArea................");
		boolean result = false;

		for (int i = 0; i < daValidities.size(); i++) {
			PmDaValidity baseDa = daValidities.get(i);
			log.info(".............baseDa.getDesignatedArea().getLocationType()"
					+ baseDa.getDesignatedArea().getLocationType());
			log.info(".............baseDa.getDesignatedArea().getLocationId()"
					+ baseDa.getDesignatedArea().getLocationId());
			for (int j = i + 1; j <= (daValidities.size() - 1); j++) {
				PmDaValidity da = daValidities.get(j);
				log.info(".............da.getDesignatedArea().getLocationType()"
						+ da.getDesignatedArea().getLocationType());
				log.info(".............da.getDesignatedArea().getLocationId()"
						+ da.getDesignatedArea().getLocationId());
				if (da.getDesignatedArea().getLocationType()
						.equals(baseDa.getDesignatedArea().getLocationType())
						&& (da.getDesignatedArea().getLocationId()
								.equals(baseDa.getDesignatedArea()
										.getLocationId()))) {

					result = isDateOverlaped(da.getEffDate(), da.getExpDate(),
							baseDa.getEffDate(), baseDa.getExpDate());

				}
				log.info("....result=" + result);

				if (result == true) {
					// hit error and return
					throw new EpException(MessageConst.ERROR_770211,
							new String[] { da.getDesignatedArea()
									.getvPmLocation().getLocationName() });
				}
			}// end of second loop

		}// end of first loop

	}

	public boolean isDateOverlappedForExemptionType(
			List<PmFeeExemption> feeExemptionList) {
		// log.info("..............isDateOverlappedForExemptionType................");
		boolean result = false;

		for (int i = 0; i < feeExemptionList.size(); i++) {
			PmFeeExemption baseExemption = feeExemptionList.get(i);
			// if (baseExemption.getRecordStatusFlg() == 0) {
			// // if record inactive choose next one
			// continue;
			// }

			for (int j = i + 1; j <= (feeExemptionList.size() - 1); j++) {
				PmFeeExemption ex = feeExemptionList.get(j);
				// if (ex.getRecordStatusFlg() == 0) {
				// // if record inactive choose next one
				// continue;
				// }
				if ((ex.getFeeExemptionType()
						.equals(ProfileManagementConst.FEE_EXEMPTION_TYPE_ORG_AND_SUPPLIER)
						&& baseExemption
								.getFeeExemptionType()
								.equals(ProfileManagementConst.FEE_EXEMPTION_TYPE_ORG_AND_SUPPLIER) && ex
						.getOrgProfileId()
						.getOrgProfileId()
						.equals(baseExemption.getOrgProfileId()
								.getOrgProfileId()))
						|| (ex.getFeeExemptionType()
								.equals(ProfileManagementConst.FEE_EXEMPTION_TYPE_ORG_AND_ITEM)
								&& baseExemption
										.getFeeExemptionType()
										.equals(ProfileManagementConst.FEE_EXEMPTION_TYPE_ORG_AND_ITEM)
								&& ex.getOrgProfileId()
										.getOrgProfileId()
										.equals(baseExemption.getOrgProfileId()
												.getOrgProfileId()) && ex
								.getItemId().getItemId()
								.equals(baseExemption.getItemId().getItemId()))) {

					result = isDateOverlaped(ex.getEffDate(), ex.getExpDate(),
							baseExemption.getEffDate(),
							baseExemption.getExpDate());

				} else if ((ex
						.getFeeExemptionType()
						.equals(ProfileManagementConst.FEE_EXEMPTION_TYPE_COMMONDITY_AND_TYPE)
						&& baseExemption
								.getFeeExemptionType()
								.equals(ProfileManagementConst.FEE_EXEMPTION_TYPE_COMMONDITY_AND_TYPE) && ex
						.getUnspscId().getUnspscCode()
						.equals(baseExemption.getUnspscId().getUnspscCode()))
						|| (ex.getFeeExemptionType()
								.equals(ProfileManagementConst.FEE_EXEMPTION_TYPE_COMMODITY)
								&& baseExemption
										.getFeeExemptionType()
										.equals(ProfileManagementConst.FEE_EXEMPTION_TYPE_COMMODITY)
								&& ex.getUnspscId()
										.getUnspscCode()
										.equals(baseExemption.getUnspscId()
												.getUnspscCode()) && ex
								.getTypeId().getTypeId()
								.equals(baseExemption.getTypeId().getTypeId()))) {
					result = isDateOverlaped(ex.getEffDate(), ex.getExpDate(),
							baseExemption.getEffDate(),
							baseExemption.getExpDate());

				} else if ((ex.getFeeExemptionType().equals(
						ProfileManagementConst.FEE_EXEMPTION_TYPE_CONTRACT)
						&& baseExemption
								.getFeeExemptionType()
								.equals(ProfileManagementConst.FEE_EXEMPTION_TYPE_CONTRACT) && ex
						.getContractId().getContractId()
						.equals(baseExemption.getContractId().getContractId()))
						|| (ex.getFeeExemptionType()
								.equals(ProfileManagementConst.FEE_EXEMPTION_TYPE_CONTRACT_AND_CONTRACTED_ITEM)
								&& baseExemption
										.getFeeExemptionType()
										.equals(ProfileManagementConst.FEE_EXEMPTION_TYPE_CONTRACT_AND_CONTRACTED_ITEM)
								&& ex.getContractId()
										.getContractId()
										.equals(baseExemption.getContractId()
												.getContractId()) && ex
								.getRequestItemId()
								.getRequestNoteItemId()
								.equals(baseExemption.getRequestItemId()
										.getRequestNoteItemId()))) {

					result = isDateOverlaped(ex.getEffDate(), ex.getExpDate(),
							baseExemption.getEffDate(),
							baseExemption.getExpDate());
				} else if (ex.getFeeExemptionType().equals(
						ProfileManagementConst.FEE_EXEMPTION_TYPE_ORGTYPE)
						&& baseExemption
								.getFeeExemptionType()
								.equals(ProfileManagementConst.FEE_EXEMPTION_TYPE_ORGTYPE)
						&& ex.getOrgTypeId()
								.getParameterId()
								.equals(baseExemption.getOrgTypeId()
										.getParameterId())) {
					result = isDateOverlaped(ex.getEffDate(), ex.getExpDate(),
							baseExemption.getEffDate(),
							baseExemption.getExpDate());
					if (result) {

						this.dupplicateOrgTypeName = getParameterDesc(ex
								.getOrgTypeId().getParameterType(), ex
								.getOrgTypeId().getParameterCode());
					}

				}

				if (result == true) {
					return result;
				}
			}// end of second loop

		}// end of first loop

		return result;
	}

	public void saveFeeExemptionForContract() {
		log.info("saveFeeExemptionForContract");
		List<PmFeeExemption> exemptionList = new ArrayList<PmFeeExemption>();
		exemptionList.addAll(this.getManagedBean()
				.getFeeExemptionsForContract());

		for (PmFeeExemption exemption : exemptionList) {
			if (exemption.getContractId() != null
					&& exemption.getContractId().getContractId() == null) {
				// throw required msg
				throw new EpException(MessageConst.ERROR_770131,
						new String[] {});
			}
			if (exemption.getRequestItemId() != null
					&& exemption.getRequestItemId().getRequestNoteItemId() == null) {// clear
				// empty
				// //
				// objects
				exemption.setRequestItemId(null);
			}

			if (exemption.getFeeExemptionId() == null) {// set ExemptionType for
														// new FeeExemptions
				if (exemption.getRequestItemId() == null) {
					exemption
							.setFeeExemptionType(ProfileManagementConst.FEE_EXEMPTION_TYPE_CONTRACT);
				} else {
					exemption
							.setFeeExemptionType(ProfileManagementConst.FEE_EXEMPTION_TYPE_CONTRACT_AND_CONTRACTED_ITEM);
				}
			} else if (exemption.getFeeExemptionType().equals(
					ProfileManagementConst.FEE_EXEMPTION_TYPE_CONTRACT)
					&& exemption.getRequestItemId() != null) {
				// update exemptionType if requestedItem is selected
				exemption
						.setFeeExemptionType(ProfileManagementConst.FEE_EXEMPTION_TYPE_CONTRACT_AND_CONTRACTED_ITEM);
			}
		}
		if (isDateOverlappedForExemptionType(exemptionList)) {
			for (PmFeeExemption ex : exemptionList) {
				if (ex.getRequestItemId() == null) {
					ex.setRequestItemId(new ScRequestItem());
				}
			}
			// hit error and return
			throw new EpException(MessageConst.ERROR_770112, new String[] {});
		}
		// add deleted records before update
		exemptionList.addAll(this.getManagedBean()
				.getDeletedContractFeeExemptions());
		for (PmFeeExemption exe : exemptionList) {
			if (exe.getRequestItemId() != null
					&& exe.getRequestItemId().getRequestNoteItemId() == null) {// clear
																				// remaining
				// empty
				// //
				// objects
				exe.setRequestItemId(null);
			}
		}
		IFeeExemptionSO exemptionSO = null;
		FeeExemptionReqDTO reqDTO = new FeeExemptionReqDTO();
		IStatusSO statusSO = (IStatusSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IStatusSO.class);
		StatusReqDTO statusReqDTO = null;
		for (PmFeeExemption fee : exemptionList) {
			String messageId = null;
			String[] msgParam = null;

			if (!fee.isBrandNew()
					&& fee.isModified()
					&& fee.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_SUSPENDED) {
				messageId = MessageConst.TD_80211;
				if (fee.getFeeExemptionType().equals(
						ProfileManagementConst.FEE_EXEMPTION_TYPE_CONTRACT)) {
					msgParam = new String[] {
							fee.getContractId().getContractNo(), "" };

				} else {// feeExemptionType is 5
					msgParam = new String[] {
							fee.getContractId().getContractNo(),
							fee.getRequestItemId().getItemName() };
				}
			}
			if (messageId != null & msgParam != null) {
				reqDTO.setTrackingStatus(true);
				reqDTO.getTrackDairyVOs().add(
						PmCommonUtil.assignTrackingDairyForFeeExemption(fee,
								getActionDesc(messageId, msgParam), fee
										.getRecordStatusFlg().longValue()));
			}

		}
		statusReqDTO = new StatusReqDTO();
		statusReqDTO.setStatusId(Long
				.valueOf(DbValueConst.RECORD_STATUS_ACTIVE));// 1
		StatusResDTO statusResDTO = statusSO
				.retrieveStatusByStatusId(statusReqDTO);
		reqDTO.setStatus(statusResDTO.getPmStatus() != null ? statusResDTO
				.getPmStatus() : new PmStatus());
		reqDTO.setUser(new FacesScopes().getUserSessionBean());
		reqDTO.setUserRole(PmCommonUtil.getUserRoleValue());
		// ///////////////////////////
		try {
			exemptionSO = (IFeeExemptionSO) BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IFeeExemptionSO.class);

			reqDTO.setFeeExemptionList(exemptionList);
			FeeExemptionResDTO resDTO = exemptionSO
					.updateFeeExemptionList(reqDTO);
			// retrieve
			this.getManagedBean().getFeeExemptionsForContract().clear();
			this.getManagedBean().getDeletedContractFeeExemptions().clear();
			reqDTO = new FeeExemptionReqDTO();
			resDTO = exemptionSO.getFeeExemptionsForContractAndReqItem(reqDTO);

			for (PmFeeExemption exemption : resDTO.getFeeExemptions()) {
				if (exemption.getRequestItemId() == null) {
					exemption.setRequestItemId(new ScRequestItem());// TODO:
																	// clear
																	// it
																	// before
																	// updating
																	// the
																	// fee
																	// list
				}
				this.getManagedBean().getFeeExemptionsForContract()
						.add(exemption);
			}
			if (resDTO.isSuccessful()) {
				FacesContext facesContext = FacesContext.getCurrentInstance();
				facesContext.addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, null,
								MessageUtils.getMessage(
										MessageConst.INFO_770010,
										MessageUtils.BUNDLE_COMMON_MESSAGES,
										new FacesScopes().getUserLocale())));

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void addMoreOperationalThreshod() {
		log.info(".................addMoreOperationalThreshod...................");
		if (this.getManagedBean().getSelectedOrgProfile().getOrgProfileId() == null) {
			// hit required msg and return
			throw new EpException(MessageConst.ERROR_770153, new String[] {});
		} else {
			PmThreshold newThreshold = new PmThreshold();
			newThreshold.setEffDate(new Date());
			newThreshold.setOrgProfileId(this.getManagedBean()
					.getSelectedOrgProfile());

			int index = this.getManagedBean().getThresholds().size();
			this.getManagedBean().getThresholds().add(index, newThreshold);
		}

	}

	public void saveFeeExemptionForComodity() {
		List<PmFeeExemption> exemptionList = new ArrayList<PmFeeExemption>();
		exemptionList.addAll(this.getManagedBean()
				.getFeeExemptionsForComodity());

		for (PmFeeExemption exemption1 : exemptionList) {
			if (exemption1.getUnspscId() != null
					&& exemption1.getUnspscId().getUnspscId() == null) {
				// throw required msg
				throw new EpException(MessageConst.ERROR_770132,
						new String[] {});
			}
			if (exemption1.getTypeId() != null
					&& exemption1.getTypeId().getTypeId() == null) {// if type
																	// not
																	// selected
																	// remove
																	// empty
																	// object
				exemption1.setTypeId(null);
			}

			if (exemption1.getFeeExemptionId() == null) {// set ExemptionType
															// for
				if (exemption1.getTypeId() == null) { // new FeeExemptions
					exemption1
							.setFeeExemptionType(ProfileManagementConst.FEE_EXEMPTION_TYPE_COMMONDITY_AND_TYPE);
				} else {
					exemption1
							.setFeeExemptionType(ProfileManagementConst.FEE_EXEMPTION_TYPE_COMMODITY);

				}
			} else if (exemption1
					.getFeeExemptionType()
					.equals(ProfileManagementConst.FEE_EXEMPTION_TYPE_COMMONDITY_AND_TYPE)
					&& exemption1.getTypeId() != null) {
				// update exemptionType if Type is selected
				exemption1
						.setFeeExemptionType(ProfileManagementConst.FEE_EXEMPTION_TYPE_COMMODITY);
			}
		}
		if (isDateOverlappedForExemptionType(exemptionList)) {
			for (PmFeeExemption ex : exemptionList) {
				if (ex.getTypeId() == null) {
					ex.setTypeId(new CmType());
				}
			}
			// hit error and return
			throw new EpException(MessageConst.ERROR_770112, new String[] {});
		}
		// add deleted records before update
		exemptionList.addAll(this.getManagedBean()
				.getDeletedComodityFeeExemptions());
		for (PmFeeExemption exe : exemptionList) { // clear remaining empty objs
			if (exe.getTypeId() != null && exe.getTypeId().getTypeId() == null) {
				exe.setTypeId(null);
			}
		}
		IFeeExemptionSO exemptionSO = null;
		FeeExemptionReqDTO reqDTO = new FeeExemptionReqDTO();
		IStatusSO statusSO = (IStatusSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IStatusSO.class);
		StatusReqDTO statusReqDTO = null;
		for (PmFeeExemption fee : exemptionList) {
			String messageId = null;
			String[] msgParam = null;
			if (!fee.isBrandNew()
					&& fee.isModified()
					&& fee.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_SUSPENDED) {
				messageId = MessageConst.TD_80209;
				if (fee.getFeeExemptionType()
						.equals(ProfileManagementConst.FEE_EXEMPTION_TYPE_COMMONDITY_AND_TYPE)) {
					msgParam = new String[] {
							fee.getUnspscId().getUnspscTitle(), "" };

				} else {// feeExemptionType is 3
					msgParam = new String[] {
							fee.getUnspscId().getUnspscTitle(),
							fee.getTypeId().getTypeName() };
				}
			}

			if (messageId != null & msgParam != null) {
				reqDTO.setTrackingStatus(true);
				reqDTO.getTrackDairyVOs().add(
						PmCommonUtil.assignTrackingDairyForFeeExemption(fee,
								getActionDesc(messageId, msgParam), fee
										.getRecordStatusFlg().longValue()));
			}

		}
		statusReqDTO = new StatusReqDTO();
		statusReqDTO.setStatusId(Long
				.valueOf(DbValueConst.RECORD_STATUS_ACTIVE));// 1
		StatusResDTO statusResDTO = statusSO
				.retrieveStatusByStatusId(statusReqDTO);
		reqDTO.setStatus(statusResDTO.getPmStatus() != null ? statusResDTO
				.getPmStatus() : new PmStatus());
		reqDTO.setUser(new FacesScopes().getUserSessionBean());
		reqDTO.setUserRole(PmCommonUtil.getUserRoleValue());
		// //////////////////
		try {
			exemptionSO = (IFeeExemptionSO) BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IFeeExemptionSO.class);

			reqDTO.setFeeExemptionList(exemptionList);
			FeeExemptionResDTO resDTO = exemptionSO
					.updateFeeExemptionList(reqDTO);
			// retrive
			this.getManagedBean().getFeeExemptionsForComodity().clear();
			this.getManagedBean().getDeletedComodityFeeExemptions().clear();
			reqDTO = new FeeExemptionReqDTO();
			resDTO = exemptionSO.getFeeExemptionsForComodity(reqDTO);

			ICodificationSO unspscSO = (ICodificationSO) BusinessDelegate
					.getInstance().getServiceObject(
							ModuleCodeConst.CATALOGUE_MANAGEMENT,
							ICodificationSO.class);
			CodificationReqDTO cReqDTO = new CodificationReqDTO();
			for (PmFeeExemption exemption : resDTO.getFeeExemptions()) {
				if (exemption.getTypeId() == null) {
					exemption.setTypeId(new CmType());
				}
				cReqDTO.setUnspscCode(exemption.getUnspscId().getUnspscCode());
				CodificationResDTO cResDTO = unspscSO
						.retrieveUNSPSCByCommodityCode(cReqDTO);

				if (cResDTO.isSuccessful()) {

					CmUnspsc commodity = cResDTO.getCommodityObj();

					exemption.setTypes(new ArrayList<CmType>());
					for (CmType type : commodity.getCmType()) {
						if (!StringUtils.isBlank(type.getTypeName())) {
							exemption.getTypes().add(type);
						}
					}
				}
				this.getManagedBean().getFeeExemptionsForComodity()
						.add(exemption);
			}
			if (resDTO.isSuccessful()) {
				FacesContext facesContext = FacesContext.getCurrentInstance();
				facesContext.addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, null,
								MessageUtils.getMessage(
										MessageConst.INFO_770010,
										MessageUtils.BUNDLE_COMMON_MESSAGES,
										new FacesScopes().getUserLocale())));

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void saveGeneralSetting() {
		IConfigGeneralSO cgeneralSO = null;
		try {
			cgeneralSO = (IConfigGeneralSO) BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IConfigGeneralSO.class);
			ConfigGeneralReqDTO reqDTO = new ConfigGeneralReqDTO();
			reqDTO.setPmConfigGeneral(this.getManagedBean()
					.getSelectedConfigGeneral());
			ConfigGeneralResDTO resDTO = cgeneralSO.updateConfigGeneral(reqDTO);
			// retrieve
			reqDTO = new ConfigGeneralReqDTO();
			resDTO = cgeneralSO.getConfigGeneral(reqDTO);
			this.getManagedBean().setSelectedConfigGeneral(
					resDTO.getPmConfigGeneral());
			if (resDTO.isSuccessful()) {
				FacesContext facesContext = FacesContext.getCurrentInstance();
				facesContext.addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, null,
								MessageUtils.getMessage(
										MessageConst.INFO_770008,
										MessageUtils.BUNDLE_COMMON_MESSAGES,
										new FacesScopes().getUserLocale())));

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// public void saveKpiValues(){
	// IKPIStatusSO kpiSO = null;
	// try {
	// kpiSO = (IKPIStatusSO) BusinessDelegate.getInstance()
	// .getServiceObject("pm", IKPIStatusSO.class);
	// KPIReqDTO reqDTO = new KPIReqDTO();
	// reqDTO.setKpiStatusList(this.getManagedBean().getKpiStatusList());
	// KPIResDTO resDTO = kpiSO.updateKpiValues(reqDTO);
	// if(resDTO.isSuccessful()){
	// FacesContext facesContext = FacesContext.getCurrentInstance();
	// facesContext.addMessage(
	// null,
	// new FacesMessage(FacesMessage.SEVERITY_INFO, null,
	// MessageUtils.getMessage(
	// MessageConst.INFO_770008,
	// MessageUtils.BUNDLE_COMMON_MESSAGES,
	// new FacesScopes().getUserLocale())));
	// System.out
	// .println("***************KPI value list have been updated*****************");
	// }
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	//
	// }
	// public void filterKPITableByDataType() {
	// System.out
	// .println("..............filterKPITableByDataType...............");
	// System.out
	// .println(".....................this.getManagedBean().getSelectedDocumentType()="
	// + this.getManagedBean().getSelectedDocumentType());
	// // this.getManagedBean().getKpiListDataModel().reloadData();
	// IKPIStatusSO kpiSO = (IKPIStatusSO) BusinessDelegate.getInstance()
	// .getServiceObject("pm", IKPIStatusSO.class);
	// KPIReqDTO reqDTO = new KPIReqDTO();
	// FacesScopes fs = new FacesScopes();
	// reqDTO.setLanguageCode(fs.getUserLocale().getLanguage());
	// reqDTO.setDocType(this.getManagedBean().getSelectedDocumentType());
	//
	// KPIResDTO resDTO = kpiSO.getKpiValuesDocyType(reqDTO);
	// this.getManagedBean().setKpiStatusList(resDTO.getKpiStatusList());
	// }

	/**
	 * get faces scopes
	 * 
	 * @return FacesScopes
	 */
	private FacesScopes getFacesScopes() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
	}

	public boolean checkIsEpAdmin() {
		if (getFacesScopes().hasRoleUser(RoleCode.EP_ADMIN.value())) {
			return true;
		} else {
			return false;
		}
	}

	public void onChangeUnplannedThreshold(ValueChangeEvent event) {
		log.info("onChangeUnplannedThreshold");
		log.info("event.getOldValue()=" + event.getOldValue());
		log.info("event.getNewValue()=" + event.getNewValue());
		PmThreshold threshold = (PmThreshold) event.getComponent()
				.getAttributes().get("threshold");
		// set modified for Tracking Diary and save the original unplanned of
		// the threshold
		if (threshold != null) {
			Integer originalS2Unplanned = this.getManagedBean()
					.getThresholdIdToS2UnplannedMap()
					.get(threshold.getThresholdId());
			if (originalS2Unplanned != null) {
				if (originalS2Unplanned.equals((Integer) event.getNewValue())) {
					this.getManagedBean().getThresholdIdToS2UnplannedMap()
							.remove(threshold.getThresholdId());
				}
			} else {
				threshold.setModified(true);
				this.getManagedBean()
						.getThresholdIdToS2UnplannedMap()
						.put(threshold.getThresholdId(),
								(Integer) event.getOldValue());
			}
		}
	}

	public void onChangeBudgetThreshold(ValueChangeEvent event) {
		log.info("onChangeBudgetThreshold");
		log.info("event.getOldValue()=" + event.getOldValue());
		log.info("event.getNewValue()=" + event.getNewValue());
		PmThreshold threshold = (PmThreshold) event.getComponent()
				.getAttributes().get("threshold");
		// set modified for Tracking Diary and save the original budget of the
		// threshold
		if (threshold != null) {
			Integer originalBudget = this.getManagedBean()
					.getThresholdIdToBudgetSpentMap()
					.get(threshold.getThresholdId());
			if (originalBudget != null) {
				if (originalBudget.equals((Integer) event.getNewValue())) {
					this.getManagedBean().getThresholdIdToBudgetSpentMap()
							.remove(threshold.getThresholdId());
				}
			} else {
				threshold.setModified(true);
				this.getManagedBean()
						.getThresholdIdToBudgetSpentMap()
						.put(threshold.getThresholdId(),
								(Integer) event.getOldValue());
			}
		}
	}

	public void onChangeMainUnplannedThreshold(ValueChangeEvent event) {
		// getManagedBean().getThreshold().setS2Unplanned(getManagedBean().getUnplannedThreshold().intValue());
		log.info("onChangeMainUnplannedThreshold");
		log.info("event.getOldValue()=" + event.getOldValue());
		log.info("event.getNewValue()=" + event.getNewValue());
		for (PmThreshold threshold : getManagedBean()
				.getUnplannedThresholdList()) {
			threshold.setS2Unplanned((Integer) event.getNewValue());
		}
		// set modified for Tracking Diary and save the original unplanned of
		// the threshold
		Integer originalS2Unplanned = this.getManagedBean()
				.getThresholdIdToS2UnplannedMap()
				.get(this.getManagedBean().getThreshold().getThresholdId());
		log.info(".......originalS2Unplanned=" + originalS2Unplanned);
		if (originalS2Unplanned != null) {
			if (originalS2Unplanned.equals((Integer) event.getNewValue())) {
				this.getManagedBean()
						.getThresholdIdToS2UnplannedMap()
						.remove(this.getManagedBean().getThreshold()
								.getThresholdId());
			}
		} else {
			this.getManagedBean().getThreshold().setModified(true);
			this.getManagedBean()
					.getThresholdIdToS2UnplannedMap()
					.put(this.getManagedBean().getThreshold().getThresholdId(),
							(Integer) event.getOldValue());
		}
	}

	public void onChangeMainBudgetThreshold(ValueChangeEvent event) {
		log.info("onChangeMainBudgetThreshold");
		log.info("event.getOldValue()=" + event.getOldValue());
		log.info("event.getNewValue()=" + event.getNewValue());
		for (PmThreshold threshold : getManagedBean().getBudgetThresholdList()) {
			threshold.setBudgetSpent((Integer) event.getNewValue());
		}
		// set modified for Tracking Diary and save the original budgetSpent of
		// the threshold
		Integer originalBudgetSpent = this.getManagedBean()
				.getThresholdIdToBudgetSpentMap()
				.get(this.getManagedBean().getThreshold().getThresholdId());
		if (originalBudgetSpent != null) {
			if (originalBudgetSpent.equals((Integer) event.getNewValue())) {
				this.getManagedBean()
						.getThresholdIdToBudgetSpentMap()
						.remove(this.getManagedBean().getThreshold()
								.getThresholdId());
			}
		} else {
			this.getManagedBean().getThreshold().setModified(true);
			this.getManagedBean()
					.getThresholdIdToBudgetSpentMap()
					.put(this.getManagedBean().getThreshold().getThresholdId(),
							(Integer) event.getOldValue());
		}

	}

	public void fillTrackingDairyForThreshold(int type, ThresholdReqDTO reqDTO) {
		log.info("Tracking diary for Threshold :");
		log.info("type" + type);
		PmThreshold centralizedLevelThreshold = this.getManagedBean()
				.getThreshold();
		log.info("centralizedLevelThreshold" + centralizedLevelThreshold);
		log.info("centralizedLevelThreshold.isModified()="
				+ centralizedLevelThreshold.isModified());
		log.info("this.getManagedBean().getThresholdIdToS2UnplannedMap().get(this.getManagedBean().getThreshold().getThresholdId())="
				+ this.getManagedBean()
						.getThresholdIdToS2UnplannedMap()
						.get(this.getManagedBean().getThreshold()
								.getThresholdId()));
		// 1 - unplanned
		// 2 - budget
		IStatusSO statusSO = (IStatusSO) BusinessDelegate.getInstance()
				.getServiceObject("pm", IStatusSO.class);
		StatusReqDTO statusReqDTO = null;
		if (type == 1) {

			if (centralizedLevelThreshold.isModified()) {
				Integer originalUnplanndthreshold = this
						.getManagedBean()
						.getThresholdIdToS2UnplannedMap()
						.get(this.getManagedBean().getThreshold()
								.getThresholdId());
				if (originalUnplanndthreshold != null
						&& !originalUnplanndthreshold
								.equals(centralizedLevelThreshold
										.getS2Unplanned())) {// track change
					log.info("Tracking diary for Threshold : Centeralized Unplanned changed");
					String messageId = MessageConst.TD_80203;
					String[] msgParam = new String[] {
							originalUnplanndthreshold.toString(),
							centralizedLevelThreshold.getS2Unplanned()
									.toString() };
					reqDTO.setTrackingStatus(true);

					reqDTO.getTrackDairyVOs().add(
							PmCommonUtil
									.assignTrackingDairyForUnplannedThreshold(
											centralizedLevelThreshold,
											getActionDesc(messageId, msgParam),
											centralizedLevelThreshold
													.getRecordStatusFlg()
													.longValue()));
				}
			}
			for (PmThreshold threshold : this.getManagedBean()
					.getUnplannedThresholdList()) {
				if (threshold.isModified()) {
					Integer originalUnplanndthreshold = this.getManagedBean()
							.getThresholdIdToS2UnplannedMap()
							.get(threshold.getThresholdId());
					if (originalUnplanndthreshold != null
							&& !originalUnplanndthreshold.equals(threshold
									.getS2Unplanned())) {// track change
						log.info("Tracking diary for Threshold : ministery Unplanned changed");
						String messageId = MessageConst.TD_80202;
						String[] msgParam = new String[] {
								threshold.getOrgProfileId().getPmOrgValidity()
										.getOrgName(),
								originalUnplanndthreshold.toString(),
								threshold.getS2Unplanned().toString() };
						reqDTO.setTrackingStatus(true);

						reqDTO.getTrackDairyVOs()
								.add(PmCommonUtil
										.assignTrackingDairyForUnplannedThreshold(
												threshold,
												getActionDesc(messageId,
														msgParam), threshold
														.getRecordStatusFlg()
														.longValue()));
					}
				}
			}
		} else if (type == 2) {
			if (centralizedLevelThreshold.isModified()) {
				Integer originalBudgetThreshold = this
						.getManagedBean()
						.getThresholdIdToBudgetSpentMap()
						.get(this.getManagedBean().getThreshold()
								.getThresholdId());
				if (originalBudgetThreshold != null
						&& !originalBudgetThreshold
								.equals(centralizedLevelThreshold
										.getBudgetSpent())) {// track change
					log.info("Tracking diary for Threshold : Centeralized BudgetVsSpent changed");
					String messageId = MessageConst.TD_80205;
					String[] msgParam = new String[] {
							originalBudgetThreshold.toString(),
							centralizedLevelThreshold.getBudgetSpent()
									.toString() };
					reqDTO.setTrackingStatus(true);

					reqDTO.getTrackDairyVOs().add(
							PmCommonUtil.assignTrackingDairyForBudgetThreshold(
									centralizedLevelThreshold,
									getActionDesc(messageId, msgParam),
									centralizedLevelThreshold
											.getRecordStatusFlg().longValue()));
				}
			}
			for (PmThreshold threshold : this.getManagedBean()
					.getBudgetThresholdList()) {
				if (threshold.isModified()) {
					Integer originalUnplanndthreshold = this.getManagedBean()
							.getThresholdIdToBudgetSpentMap()
							.get(threshold.getThresholdId());
					if (originalUnplanndthreshold != null
							&& !originalUnplanndthreshold.equals(threshold
									.getBudgetSpent())) {// track change
						log.info("Tracking diary for Threshold : ministery BudgetVsSpent changed");
						String messageId = MessageConst.TD_80204;
						String[] msgParam = new String[] {
								threshold.getOrgProfileId().getPmOrgValidity()
										.getOrgName(),
								originalUnplanndthreshold.toString(),
								threshold.getBudgetSpent().toString() };
						reqDTO.setTrackingStatus(true);

						reqDTO.getTrackDairyVOs().add(
								PmCommonUtil
										.assignTrackingDairyForBudgetThreshold(
												threshold,
												getActionDesc(messageId,
														msgParam), threshold
														.getRecordStatusFlg()
														.longValue()));
					}
				}
			}
		}

	}

	public void saveThresholds(int type) {
		// 1 - unplanned
		// 2 - budget
		ThresholdReqDTO reqDTO = new ThresholdReqDTO();
		fillTrackingDairyForThreshold(type, reqDTO);
		List<PmThreshold> saveThresholdList = new ArrayList<PmThreshold>();
		saveThresholdList.add(getManagedBean().getThreshold());
		if (type == 1) {
			saveThresholdList.addAll(getManagedBean()
					.getUnplannedThresholdList());
		} else if (type == 2) {
			saveThresholdList.addAll(getManagedBean().getBudgetThresholdList());
		}

		try {
			IOperationalThresholdSO thresholdSO = (IOperationalThresholdSO) BusinessDelegate
					.getInstance().getServiceObject(
							ModuleCodeConst.PROFILE_MANAGEMENT,
							IOperationalThresholdSO.class);

			reqDTO.setThresholdList(saveThresholdList);
			reqDTO.setPmConfigGeneral(getManagedBean()
					.getSelectedConfigGeneral());
			ThresholdResDTO resDTO = thresholdSO
					.updateOperationalThresholdList(reqDTO);
			if (resDTO.isSuccessful()) {
				initOperationThreshold();
				getManagedBean().setTabActiveIndex(3);
				if (type == 1) {
					getManagedBean().setThresholdIndex(0);
				} else if (type == 2) {
					getManagedBean().setThresholdIndex(1);
				}
				FacesContext facesContext = FacesContext.getCurrentInstance();
				facesContext.addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, null,
								MessageUtils.getMessage(
										MessageConst.INFO_770093,
										MessageUtils.BUNDLE_COMMON_MESSAGES,
										new FacesScopes().getUserLocale())));

			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new EpException(e.getMessage());
		}
	}

	/**
	 * clone Rule Type Value.
	 * 
	 * @param index
	 *            (int index(mandatory))
	 */
	public void cloneRuleTypeValue(int index) {
		log.info("cloneRuleTypeValue....index=" + index);
		PmConfigRuleValue ruleValue = new PmConfigRuleValue();
		PmConfigRule configRuleType = this.getManagedBean()
				.getSelectedRuleDescType().getRuleType();
		ruleValue.setEffDate(configRuleType.getConfigRuleValues().get(index)
				.getEffDate());
		ruleValue.setExpDate(configRuleType.getConfigRuleValues().get(index)
				.getExpDate());
		ruleValue.setShowFlag(false);

		ruleValue.setGroupValueId(configRuleType.getConfigRuleValues()
				.get(index).getGroupValueId());
		log.info("groupValueId="
				+ configRuleType.getConfigRuleValues().get(index)
						.getGroupValueId());

		int markIndex = getLastIndexToClone(
				configRuleType.getConfigRuleValues(), configRuleType
						.getConfigRuleValues().get(index).getGroupValueId());
		log.info("markIndex=" + markIndex);

		if (configRuleType.getConfigRuleValues().get(markIndex).getRuleValue() == null
				|| configRuleType.getConfigRuleValues().get(markIndex)
						.getRuleValue().isEmpty()) {
			throw new EpException(MessageConst.ERROR_770162);
		}
		if (configRuleType.getConfigRuleValues().get(markIndex).getToValue() != null
				&& !configRuleType.getConfigRuleValues().get(markIndex)
						.getRuleValue().isEmpty()) {
			ruleValue.setFromValue(configRuleType.getConfigRuleValues()
					.get(markIndex).getToValue().add(new BigDecimal(0.01)));
			ruleValue.setRuleType(configRuleType);
			configRuleType.getConfigRuleValues().add(markIndex + 1, ruleValue);
		} else {
			throw new EpException(MessageConst.ERROR_770156);
		}

	}

	/**
	 * update ToValue.
	 * 
	 * @param index
	 *            (int index(mandatory))
	 */
	public void updateToValue(int index) {
		PmConfigRule configRuleType = this.getManagedBean()
				.getSelectedRuleDescType().getRuleType();
		log.info("......updateToValue........");
		log.info("................index=" + index);
		log.info("................this.getManagedBean().getSelectedRuleType().getConfigRuleValues().size()="
				+ configRuleType.getConfigRuleValues().size());
		// validation
		// PmConfigRuleValue crulevalue =
		// this.getManagedBean().getSelectedRuleType().getConfigRuleValues().get(index);
		// if(crulevalue.getFromValue().compareTo(crulevalue.getToValue()) ==
		// 1){ //if fromValue > toValue
		// // throw error msg
		// throw new EpException(MessageConst.ERROR_770198,
		// new String[] {});
		//
		// }

		if (configRuleType.getRangeType() == 1
				&& index < configRuleType.getConfigRuleValues().size() - 1) {
			PmConfigRuleValue rulevalue = configRuleType.getConfigRuleValues()
					.get(index + 1);
			if (rulevalue.getGroupValueId().equals(
					configRuleType.getConfigRuleValues().get(index)
							.getGroupValueId())) {
				rulevalue.setFromValue(configRuleType.getConfigRuleValues()
						.get(index).getToValue().add(new BigDecimal(0.01)));
			}
		}

		PmConfigRuleValue selectedValue = configRuleType.getConfigRuleValues()
				.get(index);
		if (selectedValue.getFromValue() != null
				&& selectedValue.getToValue() != null
				&& selectedValue.getFromValue().compareTo(
						selectedValue.getToValue()) == 1) {
			selectedValue.setError(true);
		} else {
			selectedValue.setError(false);
		}
	}

	/**
	 * update Group Child Date.
	 * 
	 * @param index
	 *            (int index(mandatory))
	 */
	public void updateGroupChildDate(int index) {
		log.info("....updateGroupChildDate.....");
		log.info("................index=" + index);
		PmConfigRule configRuleType = this.getManagedBean()
				.getSelectedRuleDescType().getRuleType();
		PmConfigRuleValue ruleValue = configRuleType.getConfigRuleValues().get(
				index);
		if (configRuleType.getRangeType() == 1 && ruleValue.isShowFlag()) {
			Date effDate = ruleValue.getEffDate();
			Date expDate = ruleValue.getExpDate();

			for (PmConfigRuleValue value : configRuleType.getConfigRuleValues()) {
				if (value.getGroupValueId().equals(ruleValue.getGroupValueId())) {
					value.setEffDate(effDate);
					value.setExpDate(expDate);
				}
			}
			
		}
		
		if(!ruleValue.isBrandNew()){
			ruleValue.setModified(true);
		}
		
		// #30672, #29669 - start 29/01/2023
		if (ruleValue.getEffDate() != null && ruleValue.getExpDate() != null) {
			if (configRuleType.getRangeType() != 1 || (configRuleType.getRangeType() == 1 && ruleValue.isShowFlag())) {
				if (ruleValue.getEffDate().after(ruleValue.getExpDate())) {
					ruleValue.setErrorExpDate(true);
					throw new EpException(MessageConst.ERROR_770113);
				} else if (ruleValue.getEffDate().equals(ruleValue.getExpDate())) {
					ruleValue.setErrorExpDate(true);
					throw new EpException(MessageConst.ERROR_770113);
				} else {
					ruleValue.setErrorExpDate(false);
				}
			}
		}
		// #30672, #29669 - end 29/01/2023
	}

	/*
	 * Adds the new ruleTypeValue.
	 */
	public void addNewRuleTypeValue() {
		log.info("*****addNewRuleTypeValue*****");
		log.debug("*****addNewRuleTypeValue*****");
		if (this.getManagedBean().getSelectedRuleDescType() != null
				&& this.getManagedBean().getSelectedRuleDescType()
						.getRuleType() != null) {
			PmConfigRule configRuleType = this.getManagedBean()
					.getSelectedRuleDescType().getRuleType();
			PmConfigRuleValue newRuleValue = new PmConfigRuleValue();
			newRuleValue.setRuleType(configRuleType);
						
			/*Calendar forwardedEffDate = Calendar.getInstance();
			Date nextDay;*/
			newRuleValue.setEffDate(this.getManagedBean().getTomorrow());

			// if
			// (this.getManagedBean().getSelectedRuleType().getRuleDataTypeId()
			// .getParameterCode().equals("C")) {
			// log.info("*2****addNewRuleTypeValue*****");
			// newRuleValue.setCategoryL3Id(this.getManagedBean().getCategoryL3());
			// }
			
			//#12177 - Start
			if(configRuleType != null){
				if(configRuleType.getRuleType().equals(RuleTypeConst.SIMPLE_QUOTE_VALIDITY_PERIOD)){
					NgepApplicationBean.resetSqTimeValidity();
				}
			}
			//#12177 - End

			if (configRuleType.getRangeType() == 1) {

				if (configRuleType.getConfigRuleValues() != null
						&& configRuleType.getConfigRuleValues().size() > 0) {
					int size = configRuleType.getConfigRuleValues().size();
					newRuleValue.setGroupValueId(configRuleType
							.getConfigRuleValues().get(size - 1)
							.getGroupValueId() + 1);

				} else {

					newRuleValue.setGroupValueId(1);
				}
				newRuleValue.setFromValue(new BigDecimal(1));
			}
			int index = configRuleType.getConfigRuleValues().size();
			configRuleType.getConfigRuleValues().add(index, newRuleValue);
			RequestContext.getCurrentInstance().execute(
					"ruleValueTblWidget.paginator.endLink.click()");
		}

	}

	public int getLastIndexToClone(List<PmConfigRuleValue> pmConfigValues,
			Integer GroupId) {
		int addIndex = 0;
		for (PmConfigRuleValue ruleValue : pmConfigValues) {
			if (ruleValue.getGroupValueId().equals(GroupId)) {
				addIndex = pmConfigValues.indexOf(ruleValue);
			}
		}
		return addIndex;
	}

	public void addTaxableItem() {
		log.info(".................addTaxableItem...................");
		try {
			if (null == getManagedBean().getTaxableItems()) {
				getManagedBean().setTaxableItems(new ArrayList<PmGstItem>());
			}
			PmGstItem newGstItem = new PmGstItem();
			newGstItem
					.setExemptionType(ProfileManagementConst.EXEMPTION_GENERAL);
			newGstItem.setRecordStatusFlg(DbValueConst.RECORD_STATUS_ACTIVE);
			newGstItem.setEffDate(new Date());
			newGstItem.setRuleType(ParameterConst.RULE_TYPE_NOT_APPLICABLE);
			getManagedBean().getTaxableItems().add(newGstItem);
			RequestContext.getCurrentInstance().execute(
					"taxableItemTblWidVar.paginator.endLink.click()");
		} catch (Exception e) {
			throw new IllegalArgumentException();
		}
	}

	/**
	 * get UNSPSC Levels()
	 * 
	 * @return SelectItem[]
	 */
	public static SelectItem[] getUNSPSCLevels() {
		SelectItem[] items = new SelectItem[3];
		items[0] = new SelectItem("1", ResourceBundleUtils.getMessage(
				"lbl.cm.family.title",
				ResourceBundleUtils.BUNDLE_COMMON_LABELS,
				new FacesScopes().getUserLocale()));
		items[1] = new SelectItem("2", ResourceBundleUtils.getMessage(
				"lbl.cm.class.title", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
				new FacesScopes().getUserLocale()));
		items[2] = new SelectItem("3", ResourceBundleUtils.getMessage(
				"lbl.cm.commodity.title",
				ResourceBundleUtils.BUNDLE_COMMON_LABELS,
				new FacesScopes().getUserLocale()));
		return items;
	}

	public void searchItemCode() {
		log.info("searchItemCode");
		searchDataModel.reloadData();
	}

	public void resetItemPopUp() {
		log.info("resetItemPopUp");
		getManagedBean().setSelectedSearchLvl(null);
		getManagedBean().setSearchTitle(null);
		searchDataModel.resetData();
		getManagedBean().setSelectedUnspscResult(null);
	}

	public void populateToSaveObject() {
		if (getManagedBean().getGstIndex() == TAXABLE_ITEM_IDX
				&& getManagedBean().getSelectedGstItem() != null) {
			getManagedBean().getSelectedGstItem().setUnspsc(
					getManagedBean().getSelectedUnspscResult());
		} else if (getManagedBean().getGstIndex() == TAXABLE_ITEM_DA_IDX
				&& getManagedBean().getSelectedGstItemForDA() != null) {
			getManagedBean().getSelectedGstItemForDA().setUnspsc(
					getManagedBean().getSelectedUnspscResult());
		}
	}

	/**
	 * @return the searchDataModel
	 */
	public IPagingDataModel<CmUnspsc> getSearchDataModel() {
		return searchDataModel;
	}

	/**
	 * @param searchDataModel
	 *            the searchDataModel to set
	 */
	public void setSearchDataModel(IPagingDataModel<CmUnspsc> searchDataModel) {
		this.searchDataModel = searchDataModel;
	}

	private class SearchPagingDataModel extends PagingDataModel<CmUnspsc> {

		private static final long serialVersionUID = 1L;

		public SearchPagingDataModel() {
			super("itemCodeResultTb");
		}

		@Override
		protected ISearchResultBean<CmUnspsc> pageChangeListener(
				ISearchBean<CmUnspsc> searchBean) {
			ICodificationSO codificationSO = null;
			try {
				codificationSO = (ICodificationSO) BusinessDelegate
						.getInstance().getServiceObject(
								ModuleCodeConst.CATALOGUE_MANAGEMENT,
								ICodificationSO.class);

				if (StringUtils.isNotBlank(getManagedBean()
						.getSelectedSearchLvl())) {
					searchBean.getDataFilterBeans().add(
							new SearchFilterBean("unspscLevel",
									SearchOperator.OPERATOR_EQUALS.value(),
									getManagedBean().getSelectedSearchLvl()));

					if (StringUtils.isNotBlank(getManagedBean()
							.getSearchTitle())) {
						searchBean.getDataFilterBeans().add(
								new SearchFilterBean("unspscTitle",
										SearchOperator.OPERATOR_LIKE.value(),
										getManagedBean().getSearchTitle()
												.toUpperCase(), true));
					}
					searchBean.getDataFilterBeans().add(
							new SearchFilterBean("recordStatusFlg",
									SearchOperator.OPERATOR_EQUALS.value(),
									DbValueConst.RECORD_STATUS_ACTIVE));

					CodificationReqDTO reqDTO = new CodificationReqDTO();
					reqDTO.setSearchBean(searchBean);

					CodificationResDTO resDTO = codificationSO
							.searchUNSPSC(reqDTO);
					if (resDTO.isSuccessful()) {
						return resDTO.getResultBean();
					}
				}
			} finally {
				codificationSO = null;
			}
			return null;

		}
	}

	public void initExtensionDetails(int rowIndex) {
		if (TAXABLE_ITEM_IDX == getManagedBean().getGstIndex()) {
			getManagedBean().setSelectedGstItem(
					getManagedBean().getTaxableItems().get(rowIndex));
			if (getManagedBean().getSelectedGstItem() != null
					&& getManagedBean().getSelectedGstItem().getUnspsc() != null) {
				generateExtensionItems(getManagedBean().getSelectedGstItem());
			}
		} else if (TAXABLE_ITEM_DA_IDX == getManagedBean().getGstIndex()) {
			getManagedBean().setSelectedGstItemForDA(
					getManagedBean().getTaxableItemsForDA().get(rowIndex));
			if (getManagedBean().getSelectedGstItemForDA() != null
					&& getManagedBean().getSelectedGstItemForDA().getUnspsc() != null) {
				generateExtensionItems(getManagedBean()
						.getSelectedGstItemForDA());
			}
		}
	}

	private void generateExtensionItems(PmGstItem gstItem) {
		CmUnspsc unspsc = gstItem.getUnspsc();
		getManagedBean().setSelectedBrand(
				gstItem.getBrand() != null ? gstItem.getBrand().getBrandId()
						: null);
		getManagedBean().setSelectedType(
				gstItem.getType() != null ? gstItem.getType().getTypeId()
						: null);
		getManagedBean().setSelectedMeasurement(
				gstItem.getMeasurement() != null ? gstItem.getMeasurement()
						.getMeasurementId() : null);
		getManagedBean().setSelectedColor(
				gstItem.getColor() != null ? gstItem.getColor().getColorId()
						: null);
		generateTypeList(unspsc);
		generateBrandList(unspsc);
		generateMeasurementList(unspsc);
		generateColorList(unspsc);
	}

	/**
	 * This method is to generate cmType List for select one menu
	 * 
	 * **/
	private void generateTypeList(CmUnspsc unspsc) {
		SelectItem[] items = null;
		if (unspsc != null && !unspsc.getCmType().isEmpty()) {
			items = new SelectItem[unspsc.getCmType().size()];
			int counter = 0;
			for (CmType type : unspsc.getCmType()) {
				items[counter] = new SelectItem(counter, type.getTypeName());
				counter++;
			}
		}
		getManagedBean().setTypeList(items);

	}

	/**
	 * This method is to generate cmBrand List for select one menu *
	 * **/
	private void generateBrandList(CmUnspsc unspsc) {
		SelectItem[] items = null;
		if (unspsc != null && !unspsc.getCmBrand().isEmpty()) {
			items = new SelectItem[unspsc.getCmBrand().size()];
			int counter = 0;
			for (CmBrand brand : unspsc.getCmBrand()) {
				items[counter] = new SelectItem(counter, brand.getBrandName());
				counter++;
			}
		}
		getManagedBean().setBrandList(items);
	}

	/**
	 * This method is to generate cmMeasurement List for select one menu
	 * 
	 * **/
	private void generateMeasurementList(CmUnspsc unspsc) {
		SelectItem[] items = null;
		if (unspsc != null && !unspsc.getCmMeasurement().isEmpty()) {
			items = new SelectItem[unspsc.getCmMeasurement().size()];
			int counter = 0;
			for (CmMeasurement measurement : unspsc.getCmMeasurement()) {
				items[counter] = new SelectItem(counter,
						measurement.getMeasurementName());
				counter++;
			}
		}
		getManagedBean().setMeasurementList(items);
	}

	/**
	 * This method is to generate cmColor List for select one menu
	 * 
	 * @return SelectItem[]
	 * **/
	private void generateColorList(CmUnspsc unspsc) {
		SelectItem[] items = null;
		ICodificationSO codificationSO = null;
		try {
			if (unspsc != null) {
				codificationSO = (ICodificationSO) BusinessDelegate
						.getInstance().getServiceObject(
								ModuleCodeConst.CATALOGUE_MANAGEMENT,
								ICodificationSO.class);
				CodificationReqDTO reqDTO = new CodificationReqDTO();
				CodificationResDTO resDTO = null;

				ISearchBean<CmColor> colorSearchBean = new SearchBean<CmColor>();

				colorSearchBean.getDataFilterBeans().add(
						new SearchFilterBean("recordStatusFlg",
								SearchOperator.OPERATOR_EQUALS.value(),
								DbValueConst.RECORD_STATUS_ACTIVE));

				reqDTO.setColorSearchBean(colorSearchBean);
				resDTO = codificationSO.searchColorObj(reqDTO);
				if (resDTO.isSuccessful() && !resDTO.getColorList().isEmpty()) {
					items = new SelectItem[resDTO.getColorList().size()];
					int counter = 0;
					for (CmColor color : resDTO.getColorList()) {
						items[counter] = new SelectItem(color.getColorId(),
								color.getColorName());
						counter++;
					}
					colorMapper = new HashMap<Long, Object>();
					for (CmColor color : resDTO.getColorList()) {
						colorMapper.put(color.getColorId(), color);
					}
				}
			}
		} finally {
			codificationSO = null;
		}
		getManagedBean().setColorList(items);
	}

	public void saveExtension() {
		log.info(">> saveExtension");
		PmGstItem item = null;
		if (TAXABLE_ITEM_IDX == getManagedBean().getGstIndex()) {
			item = getManagedBean().getSelectedGstItem();
		} else if (TAXABLE_ITEM_DA_IDX == getManagedBean().getGstIndex()) {
			item = getManagedBean().getSelectedGstItemForDA();
		}

		if (item != null) {
			if (getManagedBean().getSelectedType() != null) {
				item.setType(item.getUnspsc().getCmType()
						.get(getManagedBean().getSelectedType().intValue()));
			}

			if (getManagedBean().getSelectedBrand() != null) {
				item.setBrand(item.getUnspsc().getCmBrand()
						.get(getManagedBean().getSelectedBrand().intValue()));
			}

			if (getManagedBean().getSelectedMeasurement() != null) {
				item.setMeasurement(item
						.getUnspsc()
						.getCmMeasurement()
						.get(getManagedBean().getSelectedMeasurement()
								.intValue()));
			}

			if (getManagedBean().getSelectedColor() != null) {
				item.setColor((CmColor) colorMapper.get(getManagedBean()
						.getSelectedColor()));
			}
		}
	}

	public String getItemTitle(PmGstItem item) {
		StringBuilder sb = new StringBuilder();
		if (item.getUnspsc() != null) {
			sb.append(item.getUnspsc().getUnspscTitle());
		}

		if (item.getType() != null || item.getBrand() != null
				|| item.getMeasurement() != null || item.getColor() != null) {
			sb.append("( ");

			if (item.getType() != null) {
				sb.append(item.getType().getTypeName());
				sb.append(", ");
			}

			if (item.getBrand() != null) {
				sb.append(item.getBrand().getBrandName());
				sb.append(", ");
			}

			if (item.getMeasurement() != null) {
				sb.append(item.getMeasurement().getMeasurementName());
				sb.append(", ");
			}

			if (item.getColor() != null) {
				sb.append(item.getColor().getColorName());
				sb.append(", ");
			}
			sb.deleteCharAt(sb.length() - 2);
			sb.append(")");

		}
		return sb.toString();
	}

	/**
	 * Method to retrieve description of parameter
	 * 
	 * @param String
	 *            type parameter types Return String type of description
	 * */
	public List<PmParameterDesc> retrieveParameterDescByType(String type) {
		return ParameterHelper.getInstance().getParameterDescList(type);

	}

	public List<PmParameterDesc> retrieveRuleListForTaxableItem() {
		ISearchBean<PmParameterDesc> searchBean = super.createSearchBean(
				PmParameterDesc.class, "somTiRuleTypes");
		// Filter By - status
		searchBean.getDataFilterBeans().add(
				new SearchFilterBean("recordStatusFlg",
						SearchOperator.OPERATOR_EQUALS.value(),
						DbValueConst.RECORD_STATUS_ACTIVE));
		return ParameterHelper.getInstance().getParameterDescListBySearchBean(
				ParameterTypeConst.GST_RULES_TYPE, searchBean);
	}

	/**
	 * get status item dropdown
	 * 
	 */
	public SelectItem[] getSignatoryStatusList() {
		return PmCommonUtil.getCommonStatusOptions();
	}

	public void deleteTaxableItem(int rowIndex) {
		log.info(".................deleteTaxableItem...................");

		PmGstItem deletedItem = getManagedBean().getTaxableItems()
				.get(rowIndex);
		deletedItem.setDeleted(true);
		this.getManagedBean().getDeletedTaxableItems().add(deletedItem);
		this.getManagedBean().getTaxableItems().remove(deletedItem);

	}

	private void validateTaxableItem() {
		for (PmGstItem gstItem : getManagedBean().getTaxableItems()) {
			if (gstItem.getUnspsc() == null
					|| gstItem.getUnspsc().getUnspscId() == null) {
				throw new EpException(MessageConst.ERROR_770132);
			}
		}

		if (isDateOverlappedForGstItem(getManagedBean().getTaxableItems())) {
			throw new EpException(MessageConst.ERROR_770112, new String[] {});
		}

	}

	public void saveTaxableItem() {
		log.info(".................saveTaxableItem...................");
		IGstItemSO gstItemSO = null;
		try {
			validateTaxableItem();
			gstItemSO = (IGstItemSO) BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IGstItemSO.class);
			GstItemReqDTO reqDTO = new GstItemReqDTO();
			List<PmGstItem> gstItemList = new ArrayList<PmGstItem>();
			gstItemList.addAll(getManagedBean().getTaxableItems());
			gstItemList.addAll(this.getManagedBean().getDeletedTaxableItems());
			reqDTO.setGstItemList(gstItemList);
			GstItemResDTO resDTO = gstItemSO.updateGstItemList(reqDTO);
			if (resDTO.isSuccessful()) {
				FacesContext facesContext = FacesContext.getCurrentInstance();
				facesContext.addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, null,
								MessageUtils.getMessage(
										MessageConst.INFO_770173,
										MessageUtils.BUNDLE_COMMON_MESSAGES,
										new FacesScopes().getUserLocale())));
				initGstTabsItems(ProfileManagementConst.EXEMPTION_GENERAL);

			}
		} finally {
			gstItemSO = null;
		}
	}

	private void initGst() {
		log.info("........initGst.........");
		// clear lists
		getManagedBean().getTaxableItems().clear();
		getManagedBean().getDeletedTaxableItems().clear();
		getManagedBean().getTaxableItemsForDA().clear();
		getManagedBean().getDeletedTaxableItemsForDA().clear();
		getManagedBean().getTaxableItemsForG2G().clear();
		getManagedBean().getDeletedTaxableItemsForG2G().clear();

		// TODO init exemption type tabs
		// hide GstItemTabs
		// for (int exemptionType : ProfileManagementConst.GST_EXEMPTION_LIST) {
		// initGstTabsItems(exemptionType);
		// }
		// init DA
		initDesignatedArea();
	}

	private void initGstTabsItems(int exemptionType) {
		IGstItemSO gstItemSO = null;
		try {
			gstItemSO = (IGstItemSO) BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IGstItemSO.class);
			ISearchBean<PmGstItem> searchBean = new SearchBean<PmGstItem>();

			GstItemReqDTO reqDTO = new GstItemReqDTO();
			reqDTO.setSearchBean(searchBean);
			reqDTO.setExemptionType(exemptionType);

			GstItemResDTO resDTO = gstItemSO
					.retrieveGstItemsBySearchBean(reqDTO);
			if (resDTO.isSuccessful() && resDTO.getResultBean() != null) {
				if (ProfileManagementConst.EXEMPTION_GENERAL == exemptionType) {
					getManagedBean().setTaxableItems(
							resDTO.getResultBean().getResultList());
				} else if (ProfileManagementConst.EXEMPTION_DA == exemptionType) {
					getManagedBean().setTaxableItemsForDA(
							resDTO.getResultBean().getResultList());
				} else if (ProfileManagementConst.EXEMPTION_G2G == exemptionType) {
					getManagedBean().setTaxableItemsForG2G(
							resDTO.getResultBean().getResultList());
				}
			}

		} finally {
			gstItemSO = null;
		}
	}

	private void initDesignatedArea() {
		log.info(".........initDesignatedArea.......");
		getManagedBean().getDaValidityList().clear();
		getManagedBean().getDeletedDaValidityList().clear();

		IDesignatedAreaSO designatedAreaSO = null;
		try {

			designatedAreaSO = (IDesignatedAreaSO) BusinessDelegate
					.getInstance().getServiceObject(
							ModuleCodeConst.PROFILE_MANAGEMENT,
							IDesignatedAreaSO.class);
			ISearchBean<PmDesignatedArea> searchBean = new SearchBean<PmDesignatedArea>();

			DesignatedAreaReqDTO reqDTO = new DesignatedAreaReqDTO();
			reqDTO.setSearchBean(searchBean);

			DesignatedAreaResDTO resDTO = designatedAreaSO
					.retrieveDesignatedAreaBySearchBean(reqDTO);
			if (resDTO.isSuccessful() && resDTO.getResultBean2() != null) {
				getManagedBean().setDaValidityList(
						resDTO.getResultBean2().getResultList());
			}
		} finally {
			designatedAreaSO = null;
		}
	}

	private boolean isDateOverlappedForGstItem(List<PmGstItem> gstItemList) {
		log.info("..............isDateOverlappedForGstItem................");
		boolean result = false;
		PmGstItem baseItem = null;
		PmGstItem exItem = null;
		for (int i = 0; i < gstItemList.size(); i++) {
			baseItem = gstItemList.get(i);
			if (baseItem.getRecordStatusFlg() == 0) {
				// if record inactive choose next one
				continue;
			}

			for (int j = i + 1; j <= (gstItemList.size() - 1); j++) {
				exItem = gstItemList.get(j);
				if (exItem.getRecordStatusFlg() == 0) {
					// if record inactive choose next one
					continue;
				}
				// General and Designated Area
				if ((ProfileManagementConst.EXEMPTION_GENERAL == exItem
						.getExemptionType() || ProfileManagementConst.EXEMPTION_DA == exItem
						.getExemptionType())
						&& exItem.getUnspsc().getUnspscId()
								.equals(baseItem.getUnspsc().getUnspscId())
						&& (exItem.getType() != null
								&& baseItem.getType() != null ? exItem
								.getType().getTypeId()
								.equals(baseItem.getType().getTypeId()) : true)
						&& (exItem.getBrand() != null
								&& baseItem.getBrand() != null ? exItem
								.getBrand().getBrandId() == baseItem.getBrand()
								.getBrandId() : true)
						&& (exItem.getMeasurement() != null
								&& baseItem.getMeasurement() != null ? exItem
								.getMeasurement().getMeasurementId() == baseItem
								.getMeasurement().getMeasurementId()
								: true)
						&& (exItem.getColor() != null
								&& baseItem.getColor() != null ? exItem
								.getColor().getColorId() == baseItem.getColor()
								.getColorId() : true)) {

					result = isDateOverlaped(exItem.getEffDate(),
							exItem.getExpDate(), baseItem.getEffDate(),
							baseItem.getExpDate());

				} else if (ProfileManagementConst.EXEMPTION_G2G == exItem
						.getExemptionType()
						&& (exItem.getUnspsc() != null
								&& baseItem.getUnspsc() != null ? exItem
								.getUnspsc().getUnspscId()
								.equals(baseItem.getUnspsc().getUnspscId())
								: true)
						&& exItem
								.getOrgProfile()
								.getOrgProfileId()
								.equals(baseItem.getOrgProfile()
										.getOrgProfileId())) {
					// G2G
					result = isDateOverlaped(exItem.getEffDate(),
							exItem.getExpDate(), baseItem.getEffDate(),
							baseItem.getExpDate());

				}
				if (result == true) {
					return result;
				}
			}// end of second loop

		}// end of first loop

		return result;
	}

	public List<PmParameterDesc> retrieveParameterDescList(String type) {
		return ParameterHelper.getInstance().getParameterDescList(type);
	}

	public void addDesignatedArea() {
		log.info(".................addDesignatedArea...................");
		try {
			if (null == getManagedBean().getDaValidityList()) {
				getManagedBean().setDaValidityList(
						new ArrayList<PmDaValidity>());
			}
			PmDesignatedArea newDesignatedArea = new PmDesignatedArea();
			newDesignatedArea
					.setRecordStatusFlg(DbValueConst.RECORD_STATUS_ACTIVE);

			PmDaValidity daValidity = new PmDaValidity();
			daValidity.setEffDate(this.getManagedBean().getToday());
			daValidity.setDesignatedArea(newDesignatedArea);
			getManagedBean().getDaValidityList().add(daValidity);
			RequestContext.getCurrentInstance().execute(
					"designatedAreaTblWidVar.paginator.endLink.click()");
		} catch (Exception e) {
			throw new IllegalArgumentException();
		}
	}

	public void deleteDesignatedArea() {
		log.info(".................deleteDesignatedArea...................");
		Integer rowIndex = getManagedBean().getDeleteRowIndex();
		if (rowIndex != null) {
			PmDaValidity deletedDa = getManagedBean().getDaValidityList().get(
					rowIndex.intValue());
			if (deletedDa != null) {
				deletedDa.setDeleted(true);
				this.getManagedBean().getDeletedDaValidityList().add(deletedDa);
				this.getManagedBean().getDaValidityList().remove(deletedDa);
			}
		}
	}

	private void validateDesignatedArea() {
		PmDesignatedArea baseItem = null;
		PmDesignatedArea exItem = null;
		for (int i = 0; i < getManagedBean().getDaValidityList().size(); i++) {
			baseItem = getManagedBean().getDaValidityList().get(i)
					.getDesignatedArea();
			// validate if location is not inserted.
			if (baseItem.getLocationId() == null
					|| StringUtils.isBlank(baseItem.getLocationType())) {
				throw new EpException("Location is mandatory");
			}

			if (baseItem.getRecordStatusFlg() == 0) {
				// if record inactive choose next one
				continue;
			}
			// validate duplicate items
			for (int j = i + 1; j <= (getManagedBean().getDaValidityList()
					.size() - 1); j++) {
				exItem = getManagedBean().getDaValidityList().get(j)
						.getDesignatedArea();
				if (exItem.getRecordStatusFlg() == 0) {
					// if record inactive choose next one
					continue;
				}

				// if (exItem.getLocationId().equals(baseItem.getLocationId())
				// && exItem.getLocationType().equals(
				// baseItem.getLocationType())) {
				// throw new EpException("Duplicate Designated Area");
				// }
			}
		}
	}

	public void saveDesignatedArea() {
		log.info(".................saveDesignatedArea...................");
		IDesignatedAreaSO designatedAreaSO = null;
		DesignatedAreaReqDTO reqDTO = null;
		DesignatedAreaResDTO resDTO = null;
		designatedAreaSO = (IDesignatedAreaSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IDesignatedAreaSO.class);
		try {
			for (PmDaValidity da : getManagedBean().getDaValidityList()) {
				if (da.isBrandNew()) {
					if (da.getDesignatedArea().getvPmLocation() != null
							&& da.getDesignatedArea().getvPmLocation()
									.getLocationId() != null) {
						da.getDesignatedArea().setLocationId(
								da.getDesignatedArea().getvPmLocation()
										.getLocationId());
					}
					log.info(".....da.getDesignatedArea().getId="
							+ da.getDesignatedArea().getDesignatedAreaId());
					// get any DesignatedArea with this locationType and
					// locationId
					reqDTO = new DesignatedAreaReqDTO();
					reqDTO.setLocationId(da.getDesignatedArea().getLocationId());
					reqDTO.setLocationType(da.getDesignatedArea()
							.getLocationType());
					resDTO = designatedAreaSO
							.getDesignatedAreaByLocationTypeAndId(reqDTO);

					if (resDTO.isSuccessful()
							&& resDTO.getDesignatedAreaList() != null
							&& resDTO.getDesignatedAreaList().size() > 0) {
						PmDesignatedArea retriveddesignatedArea = resDTO
								.getDesignatedAreaList().get(0);
						retriveddesignatedArea.setvPmLocation(da
								.getDesignatedArea().getvPmLocation());
						log.info("da.getDesignatedArea().getvPmLocation()="
								+ da.getDesignatedArea().getvPmLocation());
						log.info(".....retriveddesignatedArea.getDesignatedAreaId()="
								+ retriveddesignatedArea.getDesignatedAreaId());
						da.setDesignatedArea(retriveddesignatedArea);
						log.info(".....da.getDesignatedArea().getId="
								+ da.getDesignatedArea().getDesignatedAreaId());
						log.info("da.getDesignatedArea().getvPmLocation()="
								+ da.getDesignatedArea().getvPmLocation());
					}
				}

			}

			validateDesignatedArea();
			isDateOverlappedForDesignatedArea(getManagedBean()
					.getDaValidityList());

			reqDTO = new DesignatedAreaReqDTO();
			List<PmDaValidity> designatedAreaList = new ArrayList<PmDaValidity>();
			designatedAreaList.addAll(getManagedBean().getDaValidityList());
			designatedAreaList.addAll(this.getManagedBean()
					.getDeletedDaValidityList());
			reqDTO.setDaValidityList(designatedAreaList);
			// /////////////////////////////////////////add to Tracking Dairy

			for (PmDaValidity daValidity : designatedAreaList) {
				PmDesignatedArea da = daValidity.getDesignatedArea();
				String messageId = null;
				String[] msgParam = null;

				if (!da.isBrandNew() && da.isModified()) {
					// if (da.getRecordStatusFlg() ==
					// DbValueConst.RECORD_STATUS_SUSPENDED) {
					// log.info("Tracking diary for Designated-Area: de-activating designatedArea:saveDesignatedArea");
					// messageId = MessageConst.TD_80214;
					// msgParam = new String[] { da.getvPmLocation()
					// .getLocationName() };
					//
					// } else {// da.getRecordStatusFlg() ==
					// // DbValueConst.RECORD_STATUS_ACTIVE
					// log.info("Tracking diary for Designated-Area: re-activating designatedArea:saveDesignatedArea");
					// messageId = MessageConst.TD_80213;
					// msgParam = new String[] { da.getvPmLocation()
					// .getLocationName() };
					// }
				}
				if (!da.isBrandNew() && daValidity.isDeleted()) {
					log.info("Tracking diary for Designated-Area: deleted designatedArea:saveDesignatedArea");
					messageId = MessageConst.TD_80215;
					msgParam = new String[] { da.getvPmLocation()
							.getLocationName() };
				}

				if (messageId != null & msgParam != null) {
					reqDTO.setTrackingStatus(true);
					reqDTO.getTrackDairyVOs().add(
							PmCommonUtil.assignTrackingDairyForDesignatedArea(
									da, getActionDesc(messageId, msgParam), da
											.getRecordStatusFlg().longValue()));
				}

			}
			reqDTO.setUser(new FacesScopes().getUserSessionBean());
			reqDTO.setUserRole(PmCommonUtil.getUserRoleValue());
			// /////////////////////////
			resDTO = designatedAreaSO.updateDesignatedAreaList(reqDTO);
			if (resDTO.isSuccessful()) {
				FacesContext facesContext = FacesContext.getCurrentInstance();
				facesContext.addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, null,
								MessageUtils.getMessage(
										MessageConst.INFO_770174,
										MessageUtils.BUNDLE_COMMON_MESSAGES,
										new FacesScopes().getUserLocale())));
				initDesignatedArea();
			}
		} finally {
			designatedAreaSO = null;
		}
	}

	public void addTaxableItemForDA() {
		log.info(".................addTaxableItemForDA...................");
		try {
			if (null == getManagedBean().getTaxableItemsForDA()) {
				getManagedBean().setTaxableItemsForDA(
						new ArrayList<PmGstItem>());
			}
			PmGstItem newGstItem = new PmGstItem();
			newGstItem.setExemptionType(ProfileManagementConst.EXEMPTION_DA);
			newGstItem.setRecordStatusFlg(DbValueConst.RECORD_STATUS_ACTIVE);
			newGstItem.setEffDate(new Date());
			newGstItem.setRuleType(ParameterConst.RULE_TYPE_NOT_APPLICABLE);
			getManagedBean().getTaxableItemsForDA().add(newGstItem);
			RequestContext.getCurrentInstance().execute(
					"taxableItemDATblWidVar.paginator.endLink.click()");
		} catch (Exception e) {
			throw new IllegalArgumentException();
		}
	}

	public void deleteTaxableItemForDA(int rowIndex) {
		log.info(".................deleteTaxableItemForDA...................");

		PmGstItem deletedItem = getManagedBean().getTaxableItemsForDA().get(
				rowIndex);
		deletedItem.setDeleted(true);
		this.getManagedBean().getDeletedTaxableItemsForDA().add(deletedItem);
		this.getManagedBean().getTaxableItemsForDA().remove(deletedItem);
	}

	public List<PmDesignatedArea> retrieveDesignatedAreaListForTaxableItem() {
		IDesignatedAreaSO designatedAreaSO = null;
		List<PmDesignatedArea> daList = new ArrayList<PmDesignatedArea>();
		try {
			designatedAreaSO = (IDesignatedAreaSO) BusinessDelegate
					.getInstance().getServiceObject(
							ModuleCodeConst.PROFILE_MANAGEMENT,
							IDesignatedAreaSO.class);
			ISearchBean<PmDesignatedArea> searchBean = new SearchBean<PmDesignatedArea>();
			// Filter By - status
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("recordStatusFlg",
							SearchOperator.OPERATOR_EQUALS.value(),
							DbValueConst.RECORD_STATUS_ACTIVE));

			DesignatedAreaReqDTO reqDTO = new DesignatedAreaReqDTO();
			reqDTO.setSearchBean(searchBean);

			DesignatedAreaResDTO resDTO = designatedAreaSO
					.retrieveDesignatedAreaBySearchBean(reqDTO);
			if (resDTO.isSuccessful() && resDTO.getResultBean() != null) {
				daList = resDTO.getResultBean().getResultList();
				designatedAreaMapper = new HashMap<Long, Object>();
				for (PmDesignatedArea area : daList) {
					designatedAreaMapper.put(area.getDesignatedAreaId(), area);
				}
			}
		} finally {
			designatedAreaSO = null;
		}
		return daList;
	}

	public void initDesignatedAreaDetails(int rowIndex) {
		getManagedBean().setSelectedGstItemForDA(
				getManagedBean().getTaxableItemsForDA().get(rowIndex));
		getManagedBean().setDeletedDaGstList(new ArrayList<PmDaGst>());
		getManagedBean().setAutoUpdate(false);
		removeInvalidDAPopupItems();
	}

	public void addGstDaItem() {
		log.info(".................addGstDaItem...................");
		try {
			if (getManagedBean().getSelectedGstItemForDA().getPmDaGsts() == null) {
				getManagedBean().getSelectedGstItemForDA().setPmDaGsts(
						new ArrayList<PmDaGst>());
			}
			PmDaGst newDaGst = new PmDaGst();
			newDaGst.setPmGstItem(getManagedBean().getSelectedGstItemForDA());
			newDaGst.setRecordStatusFlg(DbValueConst.RECORD_STATUS_ACTIVE);
			newDaGst.setPmDesignatedArea(new PmDesignatedArea());
			getManagedBean().getSelectedGstItemForDA().getPmDaGsts()
					.add(newDaGst);
			RequestContext.getCurrentInstance().execute(
					"designatedAreaPopUpTblWidVar.paginator.endLink.click()");
		} catch (Exception e) {
			throw new IllegalArgumentException();
		}
	}

	public void saveDAPopup() {
		PmDesignatedArea area = null;
		List<PmDaGst> invalidDaGst = new ArrayList<PmDaGst>();
		Set<Long> areaSet = new HashSet<Long>();
		List<Long> areaList = new ArrayList<Long>();
		if (getManagedBean().getSelectedGstItemForDA().getPmDaGsts() == null
				|| getManagedBean().getSelectedGstItemForDA().getPmDaGsts()
						.isEmpty()) {
			throw new EpException("At least one Designated Area is required.");
		}

		for (PmDaGst daGst : getManagedBean().getSelectedGstItemForDA()
				.getPmDaGsts()) {
			if (daGst.getPmDesignatedArea().getDesignatedAreaId() != null) {
				area = (PmDesignatedArea) designatedAreaMapper.get(daGst
						.getPmDesignatedArea().getDesignatedAreaId());
				daGst.setPmDesignatedArea(area);
				areaSet.add(area.getDesignatedAreaId());
				areaList.add(area.getDesignatedAreaId());
			} else {
				invalidDaGst.add(daGst);
			}
		}

		// validate duplicate records
		if (areaList.size() != areaSet.size()) {
			throw new EpException("Designated Area must be unique.");
		}

		if (!invalidDaGst.isEmpty()) {
			getManagedBean().getSelectedGstItemForDA().getPmDaGsts()
					.removeAll(invalidDaGst);
		}

		// remove deleted gstDa
		if (!getManagedBean().getDeletedDaGstList().isEmpty()) {
			this.getManagedBean().getSelectedGstItemForDA().getPmDaGsts()
					.removeAll(getManagedBean().getDeletedDaGstList());
		}

		getManagedBean().setAutoUpdate(true);
		RequestContext.getCurrentInstance()
				.execute("dlgDesignatedArea.hide();");

	}

	public void cancelDAPopup() {
		for (PmDaGst daGst : getManagedBean().getDeletedDaGstList()) {
			daGst.setDeleted(false);
			if (!getManagedBean().getSelectedGstItemForDA().getPmDaGsts()
					.contains(daGst)) {
				this.getManagedBean().getSelectedGstItemForDA().getPmDaGsts()
						.add(daGst);
			}
		}

		removeInvalidDAPopupItems();
		getManagedBean().setAutoUpdate(true);
		RequestContext.getCurrentInstance()
				.execute("dlgDesignatedArea.hide();");

	}

	private void removeInvalidDAPopupItems() {
		List<PmDaGst> invalidDaGst = null;
		if (getManagedBean().getSelectedGstItemForDA().getPmDaGsts() != null
				&& !getManagedBean().getSelectedGstItemForDA().getPmDaGsts()
						.isEmpty()) {
			invalidDaGst = new ArrayList<PmDaGst>();
			for (PmDaGst daGst : getManagedBean().getSelectedGstItemForDA()
					.getPmDaGsts()) {
				if (daGst.getPmDesignatedArea().getLocationType() == null
						&& daGst.getPmDesignatedArea().getLocationId() == null) {
					invalidDaGst.add(daGst);
				}
			}
		}

		if (invalidDaGst != null && !invalidDaGst.isEmpty()) {
			getManagedBean().getSelectedGstItemForDA().getPmDaGsts()
					.removeAll(invalidDaGst);
		}
	}

	public void deleteDAPopUpItem(int rowIndex) {
		log.info(".................deleteDAPopUpItem...................");
		PmDaGst deletedDaGst = getManagedBean().getSelectedGstItemForDA()
				.getPmDaGsts().get(rowIndex);
		getManagedBean().getDeletedDaGstList().add(deletedDaGst);
		this.getManagedBean().getSelectedGstItemForDA().getPmDaGsts()
				.remove(deletedDaGst);
	}

	private void validateTaxableItemForDA() {
		for (PmGstItem gstItem : getManagedBean().getTaxableItemsForDA()) {
			if (gstItem.getUnspsc() == null
					|| gstItem.getUnspsc().getUnspscId() == null) {
				throw new EpException(MessageConst.ERROR_770132,
						new String[] {});
			}
			if (CmConst.UNSPSC_COMMODITY_LVL == gstItem.getUnspsc()
					.getUnspscLevel()) {
				if (gstItem.getPmDaGsts() == null
						|| gstItem.getPmDaGsts().isEmpty()) {
					throw new EpException(
							"At least one Designated Area is required for "
									+ gstItem.getUnspsc().getUnspscTitle());
				}

				if (StringUtils.isBlank(gstItem.getRuleType())) {
					throw new EpException(" Rule Type is mandatory for "
							+ gstItem.getUnspsc().getUnspscTitle());
				}

			}
		}
		if (isDateOverlappedForGstItem(getManagedBean().getTaxableItemsForDA())) {
			throw new EpException(MessageConst.ERROR_770112, new String[] {});
		}
	}

	public void saveTaxableItemForDA() {
		log.info(".................saveTaxableItemForDA...................");
		IGstItemSO gstItemSO = null;
		try {
			validateTaxableItemForDA();
			gstItemSO = (IGstItemSO) BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IGstItemSO.class);
			GstItemReqDTO reqDTO = new GstItemReqDTO();
			// mapDeletedDaGst();
			List<PmGstItem> gstItemList = new ArrayList<PmGstItem>();
			gstItemList.addAll(getManagedBean().getTaxableItemsForDA());
			gstItemList.addAll(this.getManagedBean()
					.getDeletedTaxableItemsForDA());

			reqDTO.setGstItemList(gstItemList);
			GstItemResDTO resDTO = gstItemSO.updateGstItemList(reqDTO);
			if (resDTO.isSuccessful()) {
				FacesContext facesContext = FacesContext.getCurrentInstance();
				facesContext.addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, null,
								MessageUtils.getMessage(
										MessageConst.INFO_770173,
										MessageUtils.BUNDLE_COMMON_MESSAGES,
										new FacesScopes().getUserLocale())));
				initGstTabsItems(ProfileManagementConst.EXEMPTION_DA);

			}
		} finally {
			gstItemSO = null;
		}
	}

	public List<PmParameterDesc> retrieveRuleListForTaxableItemForDA() {
		ISearchBean<PmParameterDesc> searchBean = super.createSearchBean(
				PmParameterDesc.class, "somTiDaRuleTypes");
		// Filter By - status
		searchBean.getDataFilterBeans().add(
				new SearchFilterBean("recordStatusFlg",
						SearchOperator.OPERATOR_EQUALS.value(),
						DbValueConst.RECORD_STATUS_ACTIVE));

		return ParameterHelper.getInstance().getParameterDescListBySearchBean(
				ParameterTypeConst.GST_RULES_TYPE, searchBean);
	}

	public void addTaxableItemForG2G() {
		log.info(".................addTaxableItemForG2G...................");
		try {
			if (null == getManagedBean().getTaxableItemsForG2G()) {
				getManagedBean().setTaxableItemsForG2G(
						new ArrayList<PmGstItem>());
			}
			PmGstItem newGstItem = new PmGstItem();
			newGstItem.setExemptionType(ProfileManagementConst.EXEMPTION_G2G);
			newGstItem.setRecordStatusFlg(DbValueConst.RECORD_STATUS_ACTIVE);
			newGstItem.setEffDate(new Date());
			newGstItem.setRuleType(ParameterConst.RULE_TYPE_NOT_APPLICABLE);
			getManagedBean().getTaxableItemsForG2G().add(newGstItem);
			RequestContext.getCurrentInstance().execute(
					"taxableItemG2GTblWidVar.paginator.endLink.click()");
		} catch (Exception e) {
			throw new IllegalArgumentException();
		}
	}

	public void deleteTaxableItemForG2G(int rowIndex) {
		log.info(".................deleteTaxableItemForG2G...................");

		PmGstItem deletedItem = getManagedBean().getTaxableItemsForG2G().get(
				rowIndex);
		deletedItem.setDeleted(true);
		this.getManagedBean().getDeletedTaxableItemsForG2G().add(deletedItem);
		this.getManagedBean().getTaxableItemsForG2G().remove(deletedItem);
	}

	private void validateTaxableItemForG2G() {
		for (PmGstItem gstItem : getManagedBean().getTaxableItemsForG2G()) {
			if (gstItem.getOrgProfile() == null
					|| gstItem.getOrgProfile().getOrgProfileId() == null) {
				throw new EpException(MessageConst.ERROR_770175,
						new String[] {});
			}
		}

		if (isDateOverlappedForGstItem(getManagedBean().getTaxableItemsForG2G())) {
			throw new EpException(MessageConst.ERROR_770112, new String[] {});
		}

	}

	public void saveTaxableItemForG2G() {
		log.info(".................saveTaxableItemForG2G...................");
		IGstItemSO gstItemSO = null;
		try {
			validateTaxableItemForG2G();
			gstItemSO = (IGstItemSO) BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IGstItemSO.class);
			GstItemReqDTO reqDTO = new GstItemReqDTO();
			// mapDeletedDaGst();
			List<PmGstItem> gstItemList = new ArrayList<PmGstItem>();
			gstItemList.addAll(getManagedBean().getTaxableItemsForG2G());
			gstItemList.addAll(this.getManagedBean()
					.getDeletedTaxableItemsForG2G());

			reqDTO.setGstItemList(gstItemList);
			GstItemResDTO resDTO = gstItemSO.updateGstItemList(reqDTO);
			if (resDTO.isSuccessful()) {
				FacesContext facesContext = FacesContext.getCurrentInstance();
				facesContext.addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, null,
								MessageUtils.getMessage(
										MessageConst.INFO_770173,
										MessageUtils.BUNDLE_COMMON_MESSAGES,
										new FacesScopes().getUserLocale())));
				initGstTabsItems(ProfileManagementConst.EXEMPTION_G2G);
			}
		} finally {
			gstItemSO = null;
		}
	}

	public List<PmParameterDesc> retrieveRuleListForTaxableItemForG2G() {
		ISearchBean<PmParameterDesc> searchBean = super.createSearchBean(
				PmParameterDesc.class, "somTiG2GScope");
		// Filter By - status
		searchBean.getDataFilterBeans().add(
				new SearchFilterBean("recordStatusFlg",
						SearchOperator.OPERATOR_EQUALS.value(),
						DbValueConst.RECORD_STATUS_ACTIVE));

		return ParameterHelper.getInstance().getParameterDescListBySearchBean(
				ParameterTypeConst.GST_RULES_TYPE, searchBean);
	}

	/**
	 * @return the taxableItemIdx
	 */
	public int getTaxableItemIdx() {
		return TAXABLE_ITEM_IDX;
	}

	/**
	 * @return the designatedAreaIdx
	 */
	public int getDesignatedAreaIdx() {
		return DESIGNATED_AREA_IDX;
	}

	/**
	 * @return the taxableItemDaIdx
	 */
	public int getTaxableItemDaIdx() {
		return TAXABLE_ITEM_DA_IDX;
	}

	/**
	 * @return the taxableItemG2gIdx
	 */
	public int getTaxableItemG2gIdx() {
		return TAXABLE_ITEM_G2G_IDX;
	}

	public boolean disabledExtensionFields() {
		boolean disable = false;
		if (getManagedBean().getGstIndex().equals(TAXABLE_ITEM_IDX)) {
			if (getManagedBean().getSelectedGstItem() != null
					&& !getManagedBean().getSelectedGstItem().isBrandNew()
					&& (getManagedBean().getSelectedGstItem().getEffDate()
							.before(DateUtils.getCurrentDateWithoutTime()) || (getManagedBean()
							.getSelectedGstItem().getEffDate()
							.equals(DateUtils.getCurrentDateWithoutTime()) || DbValueConst.RECORD_STATUS_ACTIVE != getManagedBean()
							.getSelectedGstItem().getRecordStatusFlg()))) {
				disable = true;
			}
		} else if (getManagedBean().getGstIndex().equals(TAXABLE_ITEM_DA_IDX)) {
			if (getManagedBean().getSelectedGstItemForDA() != null
					&& !getManagedBean().getSelectedGstItemForDA().isBrandNew()
					&& (getManagedBean().getSelectedGstItemForDA().getEffDate()
							.before(DateUtils.getCurrentDateWithoutTime()) || (getManagedBean()
							.getSelectedGstItemForDA().getEffDate()
							.equals(DateUtils.getCurrentDateWithoutTime()) || DbValueConst.RECORD_STATUS_ACTIVE != getManagedBean()
							.getSelectedGstItemForDA().getRecordStatusFlg()))) {
				disable = true;
			}
		}
		return disable;
	}

	public Long getDefaultCountry() {
		return PmCommonUtil.retrieveCountryByCountryCode(
				CommonConstants.DEFAULT_COUNTRY_CODE).getCountryId();
	}

	public void resetLovLocation(PmDaValidity da) {
		da.getDesignatedArea().setvPmLocation(null);
	}

	public void onGstTabChange(TabChangeEvent event) {
		if (event.getTab().getId().equals("taTabId")) {
			getManagedBean().setGstIndex(TAXABLE_ITEM_IDX);
		} else if (event.getTab().getId().equals("daTabId")) {
			getManagedBean().setGstIndex(DESIGNATED_AREA_IDX);
		} else if (event.getTab().getId().equals("tadaTabId")) {
			getManagedBean().setGstIndex(TAXABLE_ITEM_DA_IDX);
		} else if (event.getTab().getId().equals("tag2gTabId")) {
			getManagedBean().setGstIndex(TAXABLE_ITEM_G2G_IDX);
		}
	}

	/**
	 * Track rule value status
	 * 
	 * @param ruleValue
	 */
	public void ruleValueStatusChange() {
		log.info("ruleValueStatusChange");
		// log.info("...getCurrentIndexVal="+this.getCurrentIndexVal());
		if (this.getCurrentIndexVal() != null) {
			PmConfigRule configRuleType = this.getManagedBean()
					.getSelectedRuleDescType().getRuleType();
			PmConfigRuleValue selectedValue = configRuleType
					.getConfigRuleValues().get(this.getCurrentIndexVal());
			// log.info("....selectedValue.getRuleValue()="+selectedValue.getRuleValue());
			// log.info(".................selectedValue.getRecordStatusFlg()="+selectedValue.getRecordStatusFlg());
			if (selectedValue != null) {

				// log.info("....selectedValue="+selectedValue);

				if (!selectedValue.isModified()) {
					selectedValue.setModified(true);
				} else {
					selectedValue.setModified(false);
				}
			}
		}
	}

	public void revertStatusChange() {
		log.info(".......revertStatusChange........");
		// log.info("...getCurrentIndexVal="+this.getCurrentIndexVal());
		if (this.getCurrentIndexVal() != null) {
			PmConfigRule configRuleType = this.getManagedBean()
					.getSelectedRuleDescType().getRuleType();
			PmConfigRuleValue selectedValue = configRuleType
					.getConfigRuleValues().get(this.getCurrentIndexVal());
			// log.info("....selectedValue.getRuleValue()="+selectedValue.getRuleValue());

			selectedValue.setRecordStatusFlg(DbValueConst.RECORD_STATUS_ACTIVE);// change
																				// from
																				// inactive
																				// to
																				// active
		}
		// close popoup
		RequestContext.getCurrentInstance().execute(
				"statusConfirmDialog.hide();");
		return;

	}

	public void revertFeeStatusChangeForOrg() {
		log.info(".......revertFeeStatusChangeForOrg........");
		if (this.getCurrentIndexVal() != null) {
			PmFeeExemption fee = this.getManagedBean()
					.getFeeExemptionsForOrgAndItem()
					.get(this.getCurrentIndexVal());
			if (fee != null) {
				fee.setRecordStatusFlg(DbValueConst.RECORD_STATUS_ACTIVE);// change
																			// from
																			// inactive
																			// to
																			// active
																			// }
				// close popoup
				RequestContext.getCurrentInstance().execute(
						"feeOrgStatusConfirmDialog.hide();");
			}
		}
		return;

	}

	public void revertFeeStatusChangeForComodity() {
		log.info(".......revertFeeStatusChangeForComodity........");
		if (this.getCurrentIndexVal() != null) {
			PmFeeExemption fee = this.getManagedBean()
					.getFeeExemptionsForComodity()
					.get(this.getCurrentIndexVal());
			if (fee != null) {
				fee.setRecordStatusFlg(DbValueConst.RECORD_STATUS_ACTIVE);// change
																			// from
																			// inactive
																			// to
																			// active
																			// }
				// close popoup
				RequestContext.getCurrentInstance().execute(
						"feeCommodityStatusConfirmDialog.hide();");
			}
		}
		return;

	}

	public void revertFeeStatusChangeForContract() {
		log.info(".......revertFeeStatusChangeForContract........");
		if (this.getCurrentIndexVal() != null) {
			PmFeeExemption fee = this.getManagedBean()
					.getFeeExemptionsForContract()
					.get(this.getCurrentIndexVal());
			if (fee != null) {
				fee.setRecordStatusFlg(DbValueConst.RECORD_STATUS_ACTIVE);// change
																			// from
																			// inactive
																			// to
																			// active
																			// }
				// close popoup
				RequestContext.getCurrentInstance().execute(
						"feeContractStatusConfirmDialog.hide();");
			}
		}
		return;

	}

	/**
	 * Track fee exemption status
	 * 
	 * @param feeExemption
	 */
	// public void feeExemptionStatusChange(PmFeeExemption feeExemption) {
	// log.info("feeExemptionStatusChange");
	//
	// if (!feeExemption.isModified()) {
	// feeExemption.setModified(true);
	// } else {
	// feeExemption.setModified(false);
	// }
	//
	// }

	public void feeExemptionStatusChangeForOrg() {
		log.info("feeExemptionStatusChangeForOrg()");
		log.info(".............this.getCurrentIndexVal()"
				+ this.getCurrentIndexVal());
		if (this.getCurrentIndexVal() != null) {
			PmFeeExemption selectedFee = this.getManagedBean()
					.getFeeExemptionsForOrgAndItem()
					.get(this.getCurrentIndexVal());
			log.info(".............selectedFee=" + selectedFee);
			if (selectedFee != null) {
				if (!selectedFee.isModified()) {
					selectedFee.setModified(true);
				} else {
					selectedFee.setModified(false);
				}
			}
		}

	}

	public void feeExemptionStatusChangeForCommodity() {

		if (this.getCurrentIndexVal() != null) {
			PmFeeExemption selectedFee = this.getManagedBean()
					.getFeeExemptionsForComodity()
					.get(this.getCurrentIndexVal());

			if (selectedFee != null) {
				if (!selectedFee.isModified()) {
					selectedFee.setModified(true);
				} else {
					selectedFee.setModified(false);
				}
			}
		}

	}

	public void feeExemptionStatusChangeForContract() {

		if (this.getCurrentIndexVal() != null) {
			PmFeeExemption selectedFee = this.getManagedBean()
					.getFeeExemptionsForContract()
					.get(this.getCurrentIndexVal());
			// log.info(".............selectedFee=" + selectedFee);
			if (selectedFee != null) {
				if (!selectedFee.isModified()) {
					selectedFee.setModified(true);
				} else {
					selectedFee.setModified(false);
				}
			}
		}

	}

	/**
	 * Track Designated Area status
	 * 
	 * @param designatedArea
	 */
	public void gstStatusChange(PmDesignatedArea designatedArea) {
		log.info("gstStatusChange");

		if (!designatedArea.isModified()) {
			designatedArea.setModified(true);
		} else {
			designatedArea.setModified(false);
		}

	}

	public Integer getCurrentIndexVal() {
		return currentIndexVal;
	}

	public void setCurrentIndexVal(Integer currentIndexVal) {
		this.currentIndexVal = currentIndexVal;
	}

	/**
	 * reset Error Dialog.
	 */
	public void resetErrorDialog() {
		// this.getManagedBean().setError1(false);
		// this.getManagedBean().setError2(false);
		// this.getManagedBean().setError3(false);
		// this.getManagedBean().setError4(false);
	}

	/**
	 * update Error Dialog.
	 * 
	 * @param index
	 *            (int index(mandatory))
	 */
	public void updateErrorDialog(int index) {
		log.info("....updateErrorDialog....");
		PmConfigRule configRuleType = this.getManagedBean()
				.getSelectedRuleDescType().getRuleType();
		PmConfigRuleValue selectedValue = configRuleType.getConfigRuleValues()
				.get(index);
		if (selectedValue.getFromValue() != null
				&& selectedValue.getToValue() != null
				&& selectedValue.getFromValue().compareTo(
						selectedValue.getToValue()) == 1) {
			selectedValue.setError(true);
		} else {
			selectedValue.setError(false);
		}
		//
		// if (selectedValue.getRuleValue() == null
		// || selectedValue.getFromValue() == null) {
		// this.getManagedBean().setError2(true);
		// } else {
		// this.getManagedBean().setError2(false);
		// }

	}

	/**
	 * retrieve and set the current active org
	 */
	private void setCurrentActiveOrg() {
		log.info("setCurrentActiveOrg");
		if (getFacesScopes().getUserSessionBean().getUserId() == null) {
			throw new EpException(MessageConst.ERROR_770149);
		}

		List<PmUserOrg> userOrgList = PmCommonUtil
				.retrieveActiveOrgWithoutRole(getFacesScopes()
						.getUserSessionBean().getUserId(), false);

		if (userOrgList == null || userOrgList.isEmpty()) {
			throw new EpException(MessageConst.ERROR_770105);
		}

		PmOrgProfile pmOrgProfile = userOrgList.get(0).getPmOrgProfile();
		this.getManagedBean().setCurrentActiveOrg(pmOrgProfile);
	}

	public String getMaxCalendarYearRange() {
		return null;
	}

	private String getRuleTypeDesc(String ruleType, String languageCode) {
		IRuleSO ruleSO = null;
		try {
			ruleSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IRuleSO.class);

			RuleReqDTO reqDTO = new RuleReqDTO();

			reqDTO.setRuleType(ruleType);
			reqDTO.setLanguageCode(languageCode);

			return ruleSO.getRuleDescByType(reqDTO);

		} finally {
			ruleSO = null;
		}
	}

	public void initQuotationWithTenderValue() {
			IPtjSpecialApprovalSO ptjSpecialApprovalSO = null;
			try {
				this.getManagedBean().setDisabledSelectedMinistryId(false);
				this.getManagedBean().setMinistryProfileId(null);
				this.getManagedBean().getNewPtjSpecialApprovalList().clear();
				this.getManagedBean().getDeletedPtjSpecialApprovalList().clear();
				this.getManagedBean().getMainPtjSpecialApprovalList().clear();
				this.getManagedBean().getCloneMainPtjSpecialApprovalList().clear();
				this.getManagedBean().getPtjSpecialApprovalList().clear();
				
				ptjSpecialApprovalSO = (IPtjSpecialApprovalSO) BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
								IPtjSpecialApprovalSO.class);
				PtjSpecialApprovalReqDTO reqDTO = new PtjSpecialApprovalReqDTO();				
				//reqDTO.setMinistryOrgProfileId(getManagedBean().getSelectedMinistryId());
				PtjSpecialApprovalResDTO resDTO = ptjSpecialApprovalSO.searchPtjSpecialApproval(reqDTO);
				if(resDTO.isSuccessful() && CollectionUtils.isNotEmpty(resDTO.getPtjSpecialApprovalList())){
					sortPmPtjSpecialApprovalByMinistryName(resDTO.getPtjSpecialApprovalList());
					PmPtjSpecialApproval clone = null;
					for(PmPtjSpecialApproval approval : resDTO.getPtjSpecialApprovalList()){
						clone = (PmPtjSpecialApproval) approval.cloneEntity();
						this.getManagedBean().getCloneMainPtjSpecialApprovalList().add(clone);
					}
					this.getManagedBean().setMainPtjSpecialApprovalList(resDTO.getPtjSpecialApprovalList());
					this.getManagedBean().setPtjSpecialApprovalList(
							resDTO.getPtjSpecialApprovalList());
				}

			} finally {
				ptjSpecialApprovalSO = null;
			}

	}
	
	public void deleteQtValue() {
		log.info(".................deleteQtValue...................");
		Integer rowIndex = getManagedBean().getDeleteRowIndex();
		if (rowIndex != null) {
			PmPtjSpecialApproval deletedSpecialApproval = this.getManagedBean()
					.getPtjSpecialApprovalList().get(rowIndex.intValue());
			if (deletedSpecialApproval != null) {
				deletedSpecialApproval.setDeleted(true);
				this.getManagedBean().getDeletedPtjSpecialApprovalList()
						.add(deletedSpecialApproval);// should added to original list
												// before
												// save
				this.getManagedBean().getPtjSpecialApprovalList()
						.remove(deletedSpecialApproval);
				
				this.getManagedBean().getNewPtjSpecialApprovalList()
					.remove(deletedSpecialApproval);
			}
		}
	}
	
	public void selectPtjAction(List<PmOrgProfile> orgProfileList){
		log.info(".................selectPtjAction...................");
	}
	
	public SelectItem[] getAvailableMinistryDropdown(){
		SelectItem[] items = null;
		IPtjSpecialApprovalSO ptjSpecialApprovalSO = null;
		try {
			int i = 1;
			ptjSpecialApprovalSO = (IPtjSpecialApprovalSO) BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IPtjSpecialApprovalSO.class);
			PtjSpecialApprovalReqDTO reqDTO = new PtjSpecialApprovalReqDTO();
			PtjSpecialApprovalResDTO resDTO = ptjSpecialApprovalSO.searchAvalilableMinistryForSpecialApproval(reqDTO);

			List<PmOrgProfile> list = resDTO.getAvailableMinistryList();
			sortPmOrgProfileByName(list);
			items = new SelectItem[list.size() + 1];
			items[0] = new SelectItem(null, ResourceBundleUtils.getMessage(
					"lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					new FacesScopes().getUserLocale()));
			for (PmOrgProfile orgProfile : list) {
				items[i++] = new SelectItem(orgProfile.getOrgProfileId(),
						orgProfile.getOrgName());
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(),
							e.getMessage()));
		} finally {
			ptjSpecialApprovalSO = null;
		}
		return items;
	}
	
	public SelectItem[] getMinistryDropdown() {
		return PmCommonUtil.getMinistryDropdown();
	}
	
	public SelectItem[] getJabatanDropdown() {
		return PmCommonUtil.getJabatanDropdown(getManagedBean()
				.getMinistryProfileId());
	}	
	
	public void initPtjPopup(){
		this.getManagedBean().setMinistryProfileId(null);
		this.getManagedBean().setJabatanProfileId(null);
		this.getManagedBean().setPtjCodeName(ProfileManagementConst.PTJ_CODE);
		this.getManagedBean().setPtjCodeNameSearchValues(null);
		this.getManagedBean().setPtjList(null);
		this.getManagedBean().setQtLimit(null);
	}
	
	public SelectItem[] getPtjOrKptjDropDown(String orgType) {
		SelectItem[] items = new SelectItem[2];
		if(ParameterConst.ORG_TYPE_PEGAWAI_PENGAWAL.equals(orgType)){
			items[0] = new SelectItem(ProfileManagementConst.PEGAWAI_CODE, ResourceBundleUtils.getMessage(
					"lbl.pm.org.pegaiCode", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					new FacesScopes().getUserLocale()));
			items[1] = new SelectItem(ProfileManagementConst.PEGAWAI_NAME, ResourceBundleUtils.getMessage(
					"lbl.pm.org.pegaiName", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					new FacesScopes().getUserLocale()));
		}
		if(ParameterConst.ORG_TYPE_PTJ_GROUP.equals(orgType)){
			items[0] = new SelectItem(ProfileManagementConst.KPTJ_CODE, ResourceBundleUtils.getMessage(
					"lbl.pm.org.kptjCode", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					new FacesScopes().getUserLocale()));
			items[1] = new SelectItem(ProfileManagementConst.KPTJ_NAME, ResourceBundleUtils.getMessage(
					"lbl.pm.org.kptjName", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					new FacesScopes().getUserLocale()));
		}else if(ParameterConst.ORG_TYPE_PTJ.equals(orgType)){
			items[0] = new SelectItem(ProfileManagementConst.PTJ_CODE, ResourceBundleUtils.getMessage(
					"lbl.pm.org.ptjCode", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					new FacesScopes().getUserLocale()));
			items[1] = new SelectItem(ProfileManagementConst.PTJ_NAME, ResourceBundleUtils.getMessage(
					"lbl.pm.org.ptjName", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					new FacesScopes().getUserLocale()));
		}
		
		return items;
	}
	
	
	
	public void searchOrgProfiles(){
		getManagedBean().setPtjList(null);
		OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
		if(getManagedBean().getMinistryProfileId() != null){
			reqDTO.setMinistryId(getManagedBean().getMinistryProfileId());
		}
		if(getManagedBean().getJabatanProfileId() != null){
			reqDTO.setJabatanId(getManagedBean().getJabatanProfileId());
		}
		
		ISearchBean<PmOrgProfile> searchBeanData = getSearchBeanData();	
		
		IOrgProfileSO orgProfileSO = (IOrgProfileSO) BusinessDelegate
				.getInstance().getServiceObject(
						ModuleCodeConst.PROFILE_MANAGEMENT,
						IOrgProfileSO.class);
		reqDTO.setOrgProfileSearchBean(searchBeanData);
		OrgProfileResDTO resDTO = null;
		resDTO = orgProfileSO.retrievePtjListByParents(reqDTO);
		
		if (resDTO != null && resDTO.isSuccessful() && resDTO.getOrgProfileResultBean() !=null) {
			getManagedBean().setPtjList(resDTO.getOrgProfileResultBean().getResultList());
		}
	}
	
	public void resetOrgProfiles(){
		this.getManagedBean().setMinistryProfileId(null);
		this.getManagedBean().setJabatanProfileId(null);
		this.getManagedBean().setPtjCodeNameSearchValues(null);
		this.getManagedBean().setPtjList(null);
	}
	
	private ISearchBean<PmOrgProfile> getSearchBeanData(){
		log.info("getSearchBeanData");
		ISearchBean<PmOrgProfile> searchBean = new SearchBean<PmOrgProfile>();
		if (StringUtils.isNotBlank(getManagedBean().getPtjCodeName())
				&& (StringUtils.isNotBlank(getManagedBean().getPtjCodeNameSearchValues()))) {
			if (getManagedBean().getPtjCodeName().equalsIgnoreCase(
					ProfileManagementConst.PTJ_CODE) || 
					getManagedBean().getPtjCodeName().equalsIgnoreCase(
							ProfileManagementConst.KPTJ_CODE )
							|| getManagedBean().getPtjCodeName().equalsIgnoreCase(
									ProfileManagementConst.PEGAWAI_CODE)) {
				SearchFilterBean filterBean = new SearchFilterBean(
						"pmOrgValidity.orgCode",
						SearchOperator.OPERATOR_LIKE.value(),
						getManagedBean().getPtjCodeNameSearchValues());
				filterBean.setCaseInsensitive(true);
				searchBean.getDataFilterBeans().add(filterBean);

			} else if (getManagedBean().getPtjCodeName()
					.equalsIgnoreCase(ProfileManagementConst.PTJ_NAME) ||
					 getManagedBean().getPtjCodeName().equalsIgnoreCase(
							ProfileManagementConst.KPTJ_NAME)
					|| getManagedBean().getPtjCodeName().equalsIgnoreCase(
							ProfileManagementConst.PEGAWAI_NAME)) {
				SearchFilterBean filterBean = new SearchFilterBean(
						"pmOrgValidity.orgName",
						SearchOperator.OPERATOR_LIKE.value(),
						getManagedBean().getPtjCodeNameSearchValues());
				filterBean.setCaseInsensitive(true);
				searchBean.getDataFilterBeans().add(filterBean);
			}
		}
		searchBean.getSortColumns().add(new SearchOrderBean("pmOrgValidity.orgName"));
		return searchBean;
	}
	
	public void addQtValues(){
		log.info("addQtValues");
		PmPtjSpecialApproval approval = null;
		getManagedBean().getMainPtjSpecialApprovalList().removeAll(getManagedBean().getNewPtjSpecialApprovalList());
		for(PmOrgProfile orgProfile : getManagedBean().getPtjList()){
			if(orgProfile.isSelected()){	
				approval = new PmPtjSpecialApproval();
				orgProfile.setMinistryName(retrieveMinistryByOrgProfileId(orgProfile.getOrgProfileId()));
				approval.setProcureAmt(getManagedBean().getQtLimit());
				approval.setPmOrgProfile(orgProfile);	
				approval.setEffDate(this.getManagedBean().getTomorrow());
				getManagedBean().getNewPtjSpecialApprovalList().add(approval);	
			}
		}		
		if(!getManagedBean().getNewPtjSpecialApprovalList().isEmpty()){			
			getManagedBean().setSelectedMinistryId(null);
//			initQuotationWithTenderValue();			
			getManagedBean().setDisabledSelectedMinistryId(true);
			//copy to main list
			for(PmPtjSpecialApproval current1 : getManagedBean().getMainPtjSpecialApprovalList()){
				for(PmPtjSpecialApproval current2 : getManagedBean().getPtjSpecialApprovalList()){
					if(current1.getPtjSpecialApprovalId() != null && current2.getPtjSpecialApprovalId() != null
							&& current1.getPtjSpecialApprovalId().equals(current2.getPtjSpecialApprovalId())){
						current1.setEffDate(current2.getEffDate());
						current1.setExpDate(current2.getExpDate());
						current1.setProcureAmt(current2.getProcureAmt());
					}
				}
			}
			getManagedBean().getMainPtjSpecialApprovalList().addAll(getManagedBean().getNewPtjSpecialApprovalList());
			getManagedBean().setPtjSpecialApprovalList(getManagedBean().getMainPtjSpecialApprovalList());
			//sortPmPtjSpecialApprovalByMinistryName(getManagedBean().getPtjSpecialApprovalList());
			
		}
		RequestContext.getCurrentInstance().execute("ptjSearchPopup.hide();");
		
	}
	
	
	public void saveQtValues() {
		log.info("saveQtValues");
		/*
		 * 030125: Urgent fixing issue, re-factoring code to cater only check overlapping date range group by orgProfileID
		 * 
		boolean result = false;
		PmPtjSpecialApproval approval = null;
		PmPtjSpecialApproval compare = null;
		for (int i = 0; i < this.getManagedBean().getMainPtjSpecialApprovalList().size(); i++) {
			approval=this.getManagedBean().getMainPtjSpecialApprovalList().get(i);
			// check validation
			for (int j = i + 1; j <= (this.getManagedBean().getMainPtjSpecialApprovalList().size() - 1); j++) {				
				 compare = this.getManagedBean().getMainPtjSpecialApprovalList().get(j);
				 log.info("....approval=" + approval.getPmOrgProfile().getOrgProfileId());
				 log.info("....compare=" + compare.getPmOrgProfile().getOrgProfileId());
				if(approval.getPmOrgProfile().getOrgProfileId().equals(compare.getPmOrgProfile().getOrgProfileId())){					
					result = isDateOverlaped(approval.getEffDate(), approval.getExpDate(),
							compare.getEffDate(), compare.getExpDate());
				}
				
				log.info("....result=" + result);

				if (result == true) {
					// hit error and return
					this.getManagedBean().setDisabledSelectedMinistryId(false);
					this.getManagedBean().setMinistryProfileId(null);
					throw new EpException("QT Values : Date Overlapping - " + approval.getPmOrgProfile().getOrgName());
				}
			}// end of second loop
		}// end of first loop*/
		
		List<PmPtjSpecialApproval> approvals = this.getManagedBean().getMainPtjSpecialApprovalList();

	    // Group approvals by OrgProfileId using a HashMap
	    Map<Long, List<PmPtjSpecialApproval>> groupedApprovals = new HashMap<>();
	    for (PmPtjSpecialApproval approval : approvals) {
	        Long orgProfileId = approval.getPmOrgProfile().getOrgProfileId();
	        if (!groupedApprovals.containsKey(orgProfileId)) {
	            groupedApprovals.put(orgProfileId, new ArrayList<PmPtjSpecialApproval>());
	        }
	        groupedApprovals.get(orgProfileId).add(approval);
	    }

	    // Iterate through each group and check for overlapping dates
	    for (Map.Entry<Long, List<PmPtjSpecialApproval>> entry : groupedApprovals.entrySet()) {
	        List<PmPtjSpecialApproval> group = entry.getValue();

	        // Sort the group by effective date using Collections.sort
	        Collections.sort(group, new Comparator<PmPtjSpecialApproval>() {
	            @Override
	            public int compare(PmPtjSpecialApproval a1, PmPtjSpecialApproval a2) {
	                return a1.getEffDate().compareTo(a2.getEffDate());
	            }
	        });

	        // Check for overlapping dates in the sorted list
	        for (int i = 0; i < group.size() - 1; i++) {
	        	PmPtjSpecialApproval current = group.get(i);
	        	PmPtjSpecialApproval next = group.get(i + 1);

	            // Check for date overlap
	            boolean isOverlapping = isDateOverlaped(
	                current.getEffDate(), current.getExpDate(),
	                next.getEffDate(), next.getExpDate()
	            );

	            if (isOverlapping) {
	                // Handle overlap
	                this.getManagedBean().setDisabledSelectedMinistryId(false);
	                this.getManagedBean().setMinistryProfileId(null);

	                throw new EpException("QT Values: Date Overlapping - " + current.getPmOrgProfile().getOrgName());
	            }
	        }
	    }
		
		IPtjSpecialApprovalSO approvalSO = null;
		try{
			approvalSO = (IPtjSpecialApprovalSO) BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IPtjSpecialApprovalSO.class);
			PtjSpecialApprovalReqDTO reqDTO = new PtjSpecialApprovalReqDTO();
			getManagedBean().getMainPtjSpecialApprovalList().addAll(getManagedBean().getDeletedPtjSpecialApprovalList());
			setupTrackingDiaryVal(reqDTO);
			reqDTO.setUser(getFacesScopes().getUserSessionBean());
			reqDTO.setUserRoleValue(PmCommonUtil.getUserRoleValue());
			reqDTO.setPtjSpecialApprovalList(getManagedBean().getMainPtjSpecialApprovalList());
			PtjSpecialApprovalResDTO resDTO = approvalSO.updatePtjSpecialApprovalList(reqDTO);
			if(resDTO.isSuccessful()){	
				initQuotationWithTenderValue();
				FacesContext facesContext = FacesContext.getCurrentInstance();
				facesContext.addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, null,
								MessageUtils.getMessage(
										MessageConst.INFO_770011,
										MessageUtils.BUNDLE_COMMON_MESSAGES,
										new FacesScopes().getUserLocale())));
			}
		}catch(Exception e){
			this.getManagedBean().setDisabledSelectedMinistryId(false);
			this.getManagedBean().setMinistryProfileId(null);
			throw new EpException(MessageConst.ERROR_000001);
		}finally{
			approvalSO = null;
		}

	}
	
	private void sortPmOrgProfileByName(List<PmOrgProfile> list) {
		Comparator<PmOrgProfile> comparator = new Comparator<PmOrgProfile>() {
			@Override
			public int compare(PmOrgProfile o1, PmOrgProfile o2) {
				return new CompareToBuilder().append(o1.getOrgName(),
						o2.getOrgName()).toComparison();
			}
		};
		Collections.sort(list, comparator);
	}

	
	private void sortPmPtjSpecialApprovalByMinistryName(List<PmPtjSpecialApproval> list) {
		Comparator<PmPtjSpecialApproval> comparator = new Comparator<PmPtjSpecialApproval>() {
			@Override
			public int compare(PmPtjSpecialApproval o1, PmPtjSpecialApproval o2) {
				return new CompareToBuilder().append(o1.getPmOrgProfile().getMinistryName(),
						o2.getPmOrgProfile().getMinistryName())
						.append(o1.getPmOrgProfile().getOrgName(), o2.getPmOrgProfile().getOrgName()).toComparison();
			}
		};
		Collections.sort(list, comparator);
	}
	
	public void selectQtValues(){
		if(getManagedBean().getSelectedMinistryId() != null){
			getManagedBean().setPtjSpecialApprovalList(new ArrayList<PmPtjSpecialApproval>());
			for(PmPtjSpecialApproval approval : getManagedBean().getMainPtjSpecialApprovalList()){
				if(approval.getMinistry().getOrgProfileId().equals(getManagedBean().getSelectedMinistryId())){
					getManagedBean().getPtjSpecialApprovalList().add(approval);
				}
			}
			sortPmPtjSpecialApprovalByMinistryName(getManagedBean().getPtjSpecialApprovalList());
		}else{
			getManagedBean().setPtjSpecialApprovalList(getManagedBean().getMainPtjSpecialApprovalList());
			sortPmPtjSpecialApprovalByMinistryName(getManagedBean().getPtjSpecialApprovalList());
		}
	}
	
	private void setupTrackingDiaryVal(PtjSpecialApprovalReqDTO reqDTO){
		String messageId = null;
		String[] params = null;
		CommonUtils commonUtils = new CommonUtils();
		DecimalFormat nf = new DecimalFormat(commonUtils.getDisplayNumberFormat(CommonConstants.NUMBER_FORMAT_TYPE_AMOUNT));
		for(PmPtjSpecialApproval approval :getManagedBean().getMainPtjSpecialApprovalList()){
//			if(approval.isBrandNew()){
//				messageId = MessageConst.TD_80216;
//				params= new String[]{
//					approval.getPmOrgProfile().getPmOrgValidity().getOrgCode(),
//					approval.getPmOrgProfile().getPmOrgValidity().getOrgName()
//				};
//				addTrackingDiaryIntoList(messageId, params, approval, reqDTO);		
//				continue;
//			}
			if(!approval.isBrandNew()){
				for(PmPtjSpecialApproval clone :getManagedBean().getCloneMainPtjSpecialApprovalList()){
					if(approval.getPtjSpecialApprovalId().equals(clone.getPtjSpecialApprovalId())){
						if(!approval.getEffDate().equals(clone.getEffDate())){
							messageId = MessageConst.TD_80217;
							params= new String[]{
									approval.getPmOrgProfile().getPmOrgValidity().getOrgCode(),
									approval.getPmOrgProfile().getPmOrgValidity().getOrgName(),
									clone.getEffDate() != null ? 
											DateUtils.dateToString(CommonConstants.DEFAULT_DATE_FORMAT,clone.getEffDate()) : "-",
									approval.getEffDate() != null ? 
											DateUtils.dateToString(CommonConstants.DEFAULT_DATE_FORMAT,approval.getEffDate()) : "-"
							};
							addTrackingDiaryIntoList(messageId, params, approval, reqDTO);		
						}
						
						if(approval.getExpDate() !=null && clone.getExpDate() !=null 
								&& !approval.getExpDate().equals(clone.getExpDate())){
							messageId = MessageConst.TD_80218;
							params= new String[]{
									approval.getPmOrgProfile().getPmOrgValidity().getOrgCode(),
									approval.getPmOrgProfile().getPmOrgValidity().getOrgName(),
									clone.getExpDate() != null ? 
											DateUtils.dateToString(CommonConstants.DEFAULT_DATE_FORMAT,clone.getExpDate()) : "-",
									approval.getExpDate() != null ? 
											DateUtils.dateToString(CommonConstants.DEFAULT_DATE_FORMAT,approval.getExpDate()) : "-"
							};
							addTrackingDiaryIntoList(messageId, params, approval, reqDTO);		
						}else if(clone.getExpDate() == null && approval.getExpDate() !=null){
							messageId = MessageConst.TD_80218;
							params= new String[]{
									approval.getPmOrgProfile().getPmOrgValidity().getOrgCode(),
									approval.getPmOrgProfile().getPmOrgValidity().getOrgName(),
									clone.getExpDate() != null ? 
											DateUtils.dateToString(CommonConstants.DEFAULT_DATE_FORMAT,clone.getExpDate()) : "-",
									approval.getExpDate() != null ? 
											DateUtils.dateToString(CommonConstants.DEFAULT_DATE_FORMAT,approval.getExpDate()) : "-"
							};
							addTrackingDiaryIntoList(messageId, params, approval, reqDTO);		
						}
						
						if(!nf.format(approval.getProcureAmt()).equals(nf.format(clone.getProcureAmt()))){
							messageId = MessageConst.TD_80219;
							params= new String[]{
									approval.getPmOrgProfile().getPmOrgValidity().getOrgCode(),
									approval.getPmOrgProfile().getPmOrgValidity().getOrgName(),
									clone.getProcureAmt() != null ? nf.format(clone.getProcureAmt()).toString() : "-",
									approval.getProcureAmt() != null ? nf.format(approval.getProcureAmt()).toString() : "-"
							};
							addTrackingDiaryIntoList(messageId, params, approval, reqDTO);		
						}
						
					}
				}
			}
		}
		
		for(PmPtjSpecialApproval deleted :getManagedBean().getDeletedPtjSpecialApprovalList()){
			// only apply to effective records
			if(!deleted.isBrandNew()){
				messageId = MessageConst.TD_80220;
				params= new String[]{
						deleted.getPmOrgProfile().getPmOrgValidity().getOrgCode(),
						deleted.getPmOrgProfile().getPmOrgValidity().getOrgName()
				};
				addTrackingDiaryIntoList(messageId, params, deleted, reqDTO);	
			}
		}
	}
	
	public String getLanguage() {
//		FacesScopes facesScopes = new FacesScopes();
		
		log.info("[#22119] Language is: " + getFacesScopes().getUserLocale().getLanguage());
		return getFacesScopes().getUserLocale().getLanguage();
	}
	
	private void addTrackingDiaryIntoList(String messageId, String[] params,PmPtjSpecialApproval approval,PtjSpecialApprovalReqDTO reqDTO){
		if(messageId != null && params != null && params.length > 0){			
			reqDTO.getTrackingDiaryList().add(
					PmCommonUtil.assignTrackingDairyForQtValues(approval, 
							PmCommonUtil.getActionDesc(messageId, params), 
							approval.getRecordStatusFlg().longValue()));
		}
	}
	
	//[#22119]
	public void saveYEPValues() {
		IFeeExemptionSO feeSO = (IFeeExemptionSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,IFeeExemptionSO.class);//32333
		Calendar effectiveDt = null;
        Calendar expiryDt = null; 
        Calendar currentDt = Calendar.getInstance();         
        currentDt.setTime(DateUtils.getCurrentDateWithoutTime());
		boolean isValidDt = false;
		PmFeeExemption p2Np3_period = null;
		PmFeeExemption p5_period = null;
		PmFeeExemption p6_PATperiod = null;
		String processname = null;
		
		//32333-start
		FeeExemptionReqDTO reqFDTO = new FeeExemptionReqDTO();
		FeeExemptionResDTO resFDTO = null;		
		//32333-end
		for(PmFeeExemption feeExemption : this.getManagedBean().getFeeExemptionsForYepProcessing()) {			
			//Phase 2 and Phase 3 Period
			if(feeExemption.getOrgTypeId().getParameterCode().equals("7")) {
				if(feeExemption.getEffDate() == null) {
					throw new EpException(MessageConst.ERR_900040);
				}
				
				if(feeExemption.getExpDate() == null) {
					throw new EpException(MessageConst.ERR_900041);
				}
			}
			//Phase 5 Period
			else if(feeExemption.getOrgTypeId().getParameterCode().equals("8")) {
				if(feeExemption.getEffDate() == null) {
					throw new EpException(MessageConst.ERR_900043);
				}
				
				if(feeExemption.getExpDate() == null) {
					throw new EpException(MessageConst.ERR_900044);
				}
			}
			//Proses Akhir Tahun (PAT) Period (Phase 6)
			else if(feeExemption.getOrgTypeId().getParameterCode().equals("9")) {
				if(feeExemption.getExpDate() != null && feeExemption.getEffDate() == null) {
					throw new EpException(MessageConst.ERR_900047);
				}
				
				if(feeExemption.getEffDate() != null && feeExemption.getExpDate() == null) {
					throw new EpException(MessageConst.ERR_900048);
				}
			}			
			
			if(feeExemption.getEffDate() != null) {				
				effectiveDt = Calendar.getInstance();			
				effectiveDt.setTime(feeExemption.getEffDate());
			}
			
			if(feeExemption.getExpDate() != null) {				
				expiryDt = Calendar.getInstance();
				expiryDt.setTime(feeExemption.getExpDate());
			}
			
			//get process name
			if(this.getLanguage().equals("en")) {
				feeExemption.getOrgTypeId().getPmParameterDescs().get(0).getCodeName();
				processname = feeExemption.getOrgTypeId().getPmParameterDescs().get(0).getCodeName();
				if(feeExemption.getOrgTypeId().getParameterCode().equals("9")) {
					processname = "Proses Akhir Tahun Period";
				}
			} else if(this.getLanguage().equals("ms")) {
				feeExemption.getOrgTypeId().getPmParameterDescs().get(1).getCodeName();
				processname = feeExemption.getOrgTypeId().getPmParameterDescs().get(1).getCodeName();
				if(feeExemption.getOrgTypeId().getParameterCode().equals("9")) {
					processname = "Tempoh Proses Akhir Tahun";
				}
			}
			
			//32333-start
			reqFDTO.setParameterCode(feeExemption.getOrgTypeId().getParameterCode());
			resFDTO = feeSO.retrieveYepByParamterCode(reqFDTO);
					
			if(resFDTO != null) {
				if(feeExemption.getEffDate() != null && !feeExemption.getEffDate().equals(resFDTO.getFeeExemption().getEffDate())){
					if(feeExemption.getEffDate() != null && effectiveDt.before(currentDt)) {
						throw new EpException(MessageConst.ERR_900049, 
								new String[] {ResourceBundleUtils.getMessage("lbl.pm.yep.effdate",
		      				  ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()), processname});
					}
					if(feeExemption.getExpDate() != null && expiryDt.before(effectiveDt)) {
						throw new EpException(MessageConst.ERR_900050, 
								new String[] {ResourceBundleUtils.getMessage("lbl.pm.yep.expdate",
		      				  ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()), 
		      				  processname, 
		      				  ResourceBundleUtils.getMessage("lbl.pm.yep.effdate", ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
					}
				}
			
				if(feeExemption.getExpDate() != null && !feeExemption.getExpDate().equals(resFDTO.getFeeExemption().getExpDate())){
					if(feeExemption.getExpDate() != null && expiryDt.before(currentDt)) {
						throw new EpException(MessageConst.ERR_900049, 
								new String[] {ResourceBundleUtils.getMessage("lbl.pm.yep.expdate",
		      				  ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()), processname});
					}
			
					if(feeExemption.getExpDate() != null && expiryDt.before(effectiveDt)) {
						throw new EpException(MessageConst.ERR_900050, 
								new String[] {ResourceBundleUtils.getMessage("lbl.pm.yep.expdate",
		      				  ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()), 
		      				  processname, 
		      				  ResourceBundleUtils.getMessage("lbl.pm.yep.effdate", ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
					}
				}
			}
			isValidDt =  true;
			}
			//32333-end
		
		if(isValidDt) {
			for(PmFeeExemption feeExemption : this.getManagedBean().getFeeExemptionsForYepProcessing()) {
				if(feeExemption.getOrgTypeId().getParameterCode().equals("9")) {
					p6_PATperiod = feeExemption;
					break;
				}
			}
			
			for(PmFeeExemption feeExemption : this.getManagedBean().getFeeExemptionsForYepProcessing()) {				
				if(feeExemption.getOrgTypeId().getParameterCode().equals("7")) {
					p2Np3_period = feeExemption;
				} else if(feeExemption.getOrgTypeId().getParameterCode().equals("8")) {
					p5_period = feeExemption;
				}
			}
			
			if(p6_PATperiod != null && p6_PATperiod.getEffDate() != null && p6_PATperiod.getExpDate() != null) {				
				isValidDt = validateP2AndP3PeriodDt(p2Np3_period, p6_PATperiod);
			}
			
			if(p2Np3_period != null && p2Np3_period.getEffDate() != null && p2Np3_period.getExpDate() != null) {				
				isValidDt = validateP5PeriodDt(p5_period, p2Np3_period, p6_PATperiod);
			}			
		}
		
		log.info("[#22119] all effective and expiry date is valid ? " + isValidDt);
		if(isValidDt) {			
			
			IFeeExemptionSO feeExemptionSO = null;
			
			try{
				feeExemptionSO = (IFeeExemptionSO) BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IFeeExemptionSO.class);
				
				FeeExemptionReqDTO reqDTO = new FeeExemptionReqDTO();
				FeeExemptionResDTO resDTO = null;
				
				getManagedBean().getMainPtjSpecialApprovalList().addAll(getManagedBean().getDeletedPtjSpecialApprovalList());
				//setupTrackingDiaryVal(reqDTO);
				reqDTO.setUser(getFacesScopes().getUserSessionBean());				
				reqDTO.setFeeExemptionList(this.getManagedBean().getFeeExemptionsForYepProcessing());				
				
				//update pm_fee_exemption records
				//log.info("[#22119] TEMPORARY DISABLE updateFeeExemptionList!!!");
				resDTO = feeExemptionSO.updateFeeExemptionList(reqDTO);
				
				//TEST ONLY - START
				//resDTO = new FeeExemptionResDTO();
				//resDTO.setSuccessful(true);
				//TEST ONLY - END
				
				if(resDTO.isSuccessful()){					
					FacesContext facesContext = FacesContext.getCurrentInstance();
					facesContext.addMessage(
							null,
							new FacesMessage(FacesMessage.SEVERITY_INFO, null,
									MessageUtils.getMessage(
											MessageConst.INFO_711001,
											MessageUtils.BUNDLE_COMMON_MESSAGES,
											new FacesScopes().getUserLocale())));
				}
				
				log.info("[#22119] reload YEP page...");
				initYearEndForProcessing();
				
			}catch(Exception e){				
				throw e;
			}finally{
				feeExemptionSO = null;
			}
		} else {
			throw new EpException(
					MessageConst.ERROR_100003,
					new String[] { "Date" });			
		}
	}
	
	//[#22119]
	public void setMinDateMaxDate(Date inputDate){
		log.info("setMinDateMaxDate");
		
		if(inputDate != null) {			
			log.info("[#22119] Selected date From: " + inputDate.toString());
			this.getManagedBean().setMinDate(inputDate);
		}		
	}
	
	//[#22119]
	public boolean validateP2AndP3PeriodDt(PmFeeExemption p2Np3_period, PmFeeExemption p6_PATperiod){
		log.info("validateP2AndP3PeriodDt");
		
		Calendar p2Np3_effDt = Calendar.getInstance();			
		Calendar p2Np3_expDt = Calendar.getInstance();
		Calendar p6_effDt = Calendar.getInstance();			
		Calendar p6_expDt = Calendar.getInstance();
		
		p2Np3_effDt.setTime(p2Np3_period.getEffDate());	
		p2Np3_expDt.setTime(p2Np3_period.getExpDate());
		p6_effDt.setTime(p6_PATperiod.getEffDate());	
		p6_expDt.setTime(p6_PATperiod.getExpDate());
		
		if(p2Np3_expDt.before(p6_effDt) || p2Np3_effDt.after(p6_expDt)) {			
			log.info("[#22119] P2 and P3 date not overlap with P6 date");
			return true;
		} else {
			throw new EpException(MessageConst.ERR_900042);
		}
	}
	
	//[#22119]
	public boolean validateP5PeriodDt(PmFeeExemption p5_period, PmFeeExemption p2Np3_period, PmFeeExemption p6_PATperiod){
		log.info("validateP5PeriodDt");
		
		Calendar p5_effDt = Calendar.getInstance();			
		Calendar p5_expDt = Calendar.getInstance();
		Calendar p2Np3_effDt = Calendar.getInstance();			
		Calendar p2Np3_expDt = Calendar.getInstance();
		Calendar p6_effDt = null;			
		Calendar p6_expDt = null;
		boolean isValid = false;
		
		p5_effDt.setTime(p5_period.getEffDate());	
		p5_expDt.setTime(p5_period.getExpDate());
		p2Np3_effDt.setTime(p2Np3_period.getEffDate());	
		p2Np3_expDt.setTime(p2Np3_period.getExpDate());
		
		if(p6_PATperiod != null && p6_PATperiod.getEffDate() != null && p6_PATperiod.getExpDate() != null) {
			p6_effDt = Calendar.getInstance();			
			p6_expDt = Calendar.getInstance();
			p6_effDt.setTime(p6_PATperiod.getEffDate());	
			p6_expDt.setTime(p6_PATperiod.getExpDate());
		}
		
		if(p5_effDt.equals(p2Np3_effDt) || p5_effDt.after(p2Np3_effDt)) {			
			log.info("[#22119] P5 date is equal or greater than P2 and P3 date");
			isValid = true;
			
			if(isValid && (p6_PATperiod != null && p6_PATperiod.getEffDate() != null && p6_PATperiod.getExpDate() != null)) {
				//if(p5_expDt.before(p6_effDt) || p5_effDt.after(p6_expDt)) {
				if(p5_expDt.before(p6_effDt)) {
					log.info("[#22119] P5 date not overlap with P6 date");
					isValid = true;
				}  else {
					throw new EpException(MessageConst.ERR_900045);
				}
			}			
			return isValid;
		} else {
			throw new EpException(MessageConst.ERR_900046);
		}
	}
	
	public void onRuleType(org.primefaces.event.SelectEvent event) {

		String selectedRuleType = null;
		
        if (getManagedBean().getSelectedRuleDescType() != null) {
        	selectedRuleType = getManagedBean().getSelectedRuleDescType()
        			.getRuleType().getRuleType();
        	
        	if (selectedRuleType.equals("LOPO")) {
        		getManagedBean().setLOPOSelected(true);
        	} else {
        		getManagedBean().setLOPOSelected(false);
        	}
        }
        
	}
	
}

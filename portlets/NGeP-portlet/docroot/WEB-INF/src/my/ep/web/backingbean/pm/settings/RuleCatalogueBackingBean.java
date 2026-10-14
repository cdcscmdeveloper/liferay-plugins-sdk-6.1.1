package my.ep.web.backingbean.pm.settings;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;//#35323, #34397, #34398
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
//#35323 - Start
import java.util.MissingResourceException;
import java.util.ResourceBundle;
//#35323 - End
import java.util.Set;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import my.ep.bean.application.NgepApplicationBean;
import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.bean.search.SearchOrderBean;
import my.ep.biz.service.cm.codification.ICodificationSO;
import my.ep.biz.service.ct.dtl.IContractItemSO;
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.authorization.orgprofile.IPtjSpecialApprovalSO;
import my.ep.biz.service.pm.common.audit.IAuditPMSO;
import my.ep.biz.service.pm.common.confgeneral.IConfigGeneralSO;
import my.ep.biz.service.pm.common.parameter.IParameterSO; //#35323
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.pm.common.zone.IZoneSO;
import my.ep.biz.service.pm.fee.settings.IFeeExemptionSO;
import my.ep.biz.service.pm.general.settings.designatedarea.IDesignatedAreaSO;
import my.ep.biz.service.pm.general.settings.gstitem.IGstItemSO;
import my.ep.biz.service.pm.general.settings.rule.IRuleSO;
import my.ep.biz.service.pm.general.settings.threshold.IOperationalThresholdSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.cm.CmConst;
import my.ep.constant.pm.AuditConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.pm.ProfileManagementConst;
/* 27827 - START */
import my.ep.constant.pm.RuleTypeConst;
/* 27827 - END */
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
import my.ep.dto.pm.common.parameter.ParameterReqDTO; //#35323
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.dto.pm.common.zone.ZoneResDTO;
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
import my.ep.persistence.entity.pm.authorization.PmOrgValidity;//#36738
import my.ep.persistence.entity.pm.authorization.PmPtjSpecialApproval;
import my.ep.persistence.entity.pm.authorization.PmUserOrg;
import my.ep.persistence.entity.pm.common.PmCategoryL3;
import my.ep.persistence.entity.pm.common.PmLanguage;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmStatus;
/* 27827 - START */
import my.ep.persistence.entity.pm.common.PmZone;
/* 27827 - END */
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
import my.ep.util.SLAUtils; //#35323
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.pm.audit.AuditPMBackingBeanTemplate;
import my.ep.web.backingbean.pm.audit.mapbeans.AuditPMSysParamBackingBean;
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
//#36738 Start
import org.primefaces.event.ToggleEvent;
import org.primefaces.model.Visibility;
//#36738 End

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
	private boolean update = false; //#35509

	private static final String SETTING_CONFIG_MAIN = "settingConfig.xhtml?faces-redirect=true";
	private static final String ERROR_PAGE = "/xhtml/pm/shared/outputMsg.xhtml";
	
	//#35323 - Start
	private static final List<Long> SUPPL_ACC_TYPE_ORDER =
	        Arrays.asList(
	            RuleTypeConst.BASIC,
	            RuleTypeConst.MOF,
	            RuleTypeConst.G2G
	        );

	private static final Map<Long, String> ACC_TYPE_KEYS;
	static {
		Map<Long, String> m = new HashMap<>();
		m.put(RuleTypeConst.BASIC, "lbl.pm.rule.supp.acc.type.basic");
		m.put(RuleTypeConst.MOF, "lbl.pm.rule.supp.acc.type.mof");
		m.put(RuleTypeConst.G2G, "lbl.pm.rule.supp.acc.type.g2g");
		ACC_TYPE_KEYS = Collections.unmodifiableMap(m);
	}

	private static final Map<Long, Integer> SUPPLIER_PRIORITY;
	static {
		Map<Long, Integer> map = new HashMap<>();
		map.put(RuleTypeConst.BASIC, 1);
		map.put(RuleTypeConst.MOF, 2);
		map.put(RuleTypeConst.G2G, 3);
		SUPPLIER_PRIORITY = Collections.unmodifiableMap(map);
	}
	//#35323 - End

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
		this.getManagedBean().setSelectedCategoryL3(pmCategoryL3);
		PmConfigRule configRuleType = this.getManagedBean()
				.getSelectedRuleDescType().getRuleType();
		PmConfigRuleValue newRuleValue = new PmConfigRuleValue();
		newRuleValue.setRuleType(configRuleType);
		newRuleValue.setEffDate(this.getManagedBean().getTomorrow());

		newRuleValue.setCategoryL3Id(this.getManagedBean()
				.getSelectedCategoryL3());

		if (configRuleType.getRangeType() == 1) {
			//#29166 CR 27827: Static Code Analysis Use isEmpty() to check whether the collection is empty or not
			if (configRuleType.getConfigRuleValues() != null && !configRuleType.getConfigRuleValues().isEmpty()) {
				int size = configRuleType.getConfigRuleValues().size();
				newRuleValue.setGroupValueId(configRuleType.getConfigRuleValues().get(size - 1).getGroupValueId() + 1);
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
			log.error(e);
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
		//#29166 CR 27827: Static Code Analysis Immediately return this expression instead of assigning it to the temporary variable "orgTypeSearchBean".
		//ISearchBean<PmParameterDesc> orgTypeSearchBean = super.createSearchBean(PmParameterDesc.class, componentId);
		//return orgTypeSearchBean;
		return super.createSearchBean(PmParameterDesc.class, componentId);
		//#29166 CR 27827: Static Code Analysis End
	}

	// public List<SortMeta> getPreSortOrder() {
	// return preSortOrder;
	// }

	public boolean isValidDateRange(Date effDate, Date ExpDate) {
		//#29166 CR 27827: Static Code Analysis Replace this if-then-else statement by a single return statement.
		/*if (ExpDate != null && (ExpDate.getTime() < effDate.getTime())) {
			return false;
		}
		return true;*/ 
		return ExpDate != null && (ExpDate.getTime() < effDate.getTime()) ? false : true;
		//#29166 CR 27827: Static Code Analysis End
	}

	public void setInactivePopUpMsg(String inactivePopUpMsg) {
		this.inactivePopUpMsg = inactivePopUpMsg;
	}

	public String getInactivePopUpMsg() {
		return inactivePopUpMsg;
	}

	public String getInactiveFeeOrgPopUpMsg() {
		return inactiveFeeOrgPopUpMsg;
	}

	public void setInactiveFeeOrgPopUpMsg(String inactiveFeeOrgPopUpMsg) {
		this.inactiveFeeOrgPopUpMsg = inactiveFeeOrgPopUpMsg;
	}

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
		getManagedBean().setNewPRList(false); //#35323
		setCurrentActiveOrg();
		FacesContext fc = FacesContext.getCurrentInstance();
		try {
			initFeeSettings();
			initOperationThreshold();
			initGst();
			initQuotationWithTenderValue();
			initYearEndForProcessing();
			onAnyTabChange();//#36738
			fc.getApplication().getNavigationHandler()
					.handleNavigation(fc, null, SETTING_CONFIG_MAIN);
		} catch (Exception e) {
			SLAUtils.getInstance().logSLAError(ModuleCodeConst.PROFILE_MANAGEMENT, null, null,
					"RuleCatalogueBackingBean.preRenderView", null, e); //#35323
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
			/* 27827 - START */
			retrievePmZoneList();
			/* 27827 - END */
			// List<PmConfigRule> list = retriveConfigRuleFromDB();
			// this.getManagedBean().setConfigRuleList(list);
			// this.getManagedBean().setSelectedRuleType(list.get(0));
			//#36738 Start
			getManagedBean().setOriginalConfigRuleDescList(list);
			getManagedBean().setOriginalConfigRuleValueList(getManagedBean().getSelectedRuleDescType().getRuleType().getConfigRuleValues());
			//#36738 End
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
				if (exemption.getItemId() == null) {
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
					exemption.setRequestItemId(new ScRequestItem());
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
		log.info(".................deleteMoreRuleValue...................");

		//#30672 - Start
		RequestContext.getCurrentInstance().reset("form:settingTabId:ruleTypeValueTableID"); // reset form UI validation error
		//#30672 - End

		// int indexOfSelectedRuleType =
		// this.getManagedBean().getConfigRuleList().indexOf(this.getManagedBean().getSelectedRuleType());
		// this.getManagedBean().getConfigRuleList().remove(indexOfSelectedRuleType);
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
		
		/* START #35509 AUDIT PM */
		selectedValueToDel.setRuleTypeCode(configRuleType.getRuleType());
		selectedValueToDel.setRuleDescMS(configRuleType.getConfigRuleDesc().get(1).getRuleDesc());
		selectedValueToDel.setRuleDataTypeCode(configRuleType.getRuleDataTypeId().getParameterCode());
		selectedValueToDel.setRuleRangeType(BigInteger.valueOf(configRuleType.getRangeType()));
		if (selectedValueToDel.getZoneId() != null) {
			selectedValueToDel.setZoneName(getZoneName(selectedValueToDel.getZoneId()));
		}
		/* END 35509 AUDIT PM */

		//#35323 - Start
		if (configRuleType.getRangeType() == 1
				|| RuleTypeConst.PURCHASE_REQUEST_THERSHOLD.equalsIgnoreCase(configRuleType.getRuleType())
				&& selectedValueToDel.isShowFlag()) {
			Integer groupIdOfParent = selectedValueToDel.getGroupValueId();
			for (PmConfigRuleValue value : configRuleType.getConfigRuleValues()) {
				if (value.getGroupValueId()!= null && value.getGroupValueId().equals(groupIdOfParent)) {
					removeList.add(value);
				}
			}
			configRuleType.getConfigRuleValues().removeAll(removeList);
		} else {
			configRuleType.getConfigRuleValues().remove(rowIndex.intValue());
		}
		//#35323 - End
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

			sortConfigRuleValueList(ruleTypeDesc.getRuleType().getConfigRuleValues()); //#35323
			if (ruleTypeDesc.getRuleType().getRuleDataTypeId()
					.getParameterCode().equals("C")) {
				for (PmConfigRuleValue ruleTypeValue : ruleTypeDesc
						.getRuleType().getConfigRuleValues()) {
					if (ruleTypeValue.getCategoryL3Id() != null
							&& ruleTypeValue.getCategoryL3Id()
									.getCategoryL3Id() == null) {
						// throw required msg
						// 29669 start
						throw new EpException(MessageConst.ERROR_770133,
								new String[] { ruleTypeDesc.getRuleDesc() });
						// 29669 end

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
			
			// 29669,29641 start 
			if (ruleTypeDesc.getRuleType().getRuleDataTypeId().getParameterCode().matches("A|N|M|P") 
					&& ruleTypeDesc.getRuleType().getRangeType() != 2) {
				//#34397, #34398 - start
				if (isRenewTypes(ruleTypeDesc.getRuleType().getRuleType())){
					validateRenewMOFParams(ruleTypeDesc);
				}
				//#34397, #34398 - end
				
				//#35323 and sonarLint fixing - Start
				for (PmConfigRuleValue ruleTypeValue : ruleTypeDesc.getRuleType().getConfigRuleValues()) {
					if (ruleTypeValue.getRecordStatusFlg() == 0) {// if is inActive one go to next record
						continue;
					}
					validateRuleValue(ruleTypeDesc, ruleTypeValue);
				}
				//#35323 and sonarLint fixing - End
			}
			// 29669,29641 end
			// 29650 start
			if (ruleTypeDesc.getRuleType().getRangeType() == 3) {
				for (PmConfigRuleValue ruleTypeValue : ruleTypeDesc.getRuleType().getConfigRuleValues()) {
					if (ruleTypeValue.getFromValue() == null) {
						throw new EpException(MessageConst.ERROR_770234, new String[] { ruleTypeDesc.getRuleDesc() });
					}
					if (ruleTypeValue.getToValue() == null) {
						throw new EpException(MessageConst.ERROR_770235, new String[] { ruleTypeDesc.getRuleDesc() });
					}
				}
			}
			// 29650 end
			
			for (int i = 0; i < ruleTypeDesc.getRuleType()
					.getConfigRuleValues().size(); i++) {
				// add new values to tracking Diary list

				PmConfigRuleValue baseValueToCompare = ruleTypeDesc.getRuleType().getConfigRuleValues().get(i);
				
				//#35323 and sonarLint issue fixing - Start
				boolean isRangeTypeAllowed = isRangeTypeOneOrTwo(ruleTypeDesc) || isPurchaseRequest(ruleTypeDesc);

				boolean hasBaseValueError = baseValueToCompare.isError() || baseValueToCompare.isErrorToRequired();
				//#35323 and sonarLint issue fixing - End

				if (baseValueToCompare.getRecordStatusFlg() == 0) {// if is
					// inActive
					// one go to
					// next
					// record\
					continue;
				} 
				// 29669 - start
				else if (isRangeTypeAllowed && hasBaseValueError) { // #35323
							// to value can not be less than from value
							// or
							// previous tier's TO value can not be changed to null if already add next tier
					throw new EpException(MessageConst.ERROR_770210, new String[] { ruleTypeDesc.getRuleDesc() });
				}
				// 29669 - end
				// 29650 - start
				else if (ruleTypeDesc.getRuleType().getRangeType() == 3) {
					if (baseValueToCompare.isError1()) // Value must be less than or equal to To
						throw new EpException(MessageConst.ERROR_770236, new String[] { ruleTypeDesc.getRuleDesc() });
					if (baseValueToCompare.isError2()) // Value must be more than or equal to From
						throw new EpException(MessageConst.ERROR_770237, new String[] { ruleTypeDesc.getRuleDesc() });
					if (baseValueToCompare.isError3()) // From must be less than or equal to To
						throw new EpException(MessageConst.ERROR_770238, new String[] { ruleTypeDesc.getRuleDesc() });
					if (baseValueToCompare.isError4()) // From must be less than or equal to Value
						throw new EpException(MessageConst.ERROR_770239, new String[] { ruleTypeDesc.getRuleDesc() });
					if (baseValueToCompare.isError5()) // To must be more than or equal to Value
						throw new EpException(MessageConst.ERROR_770240, new String[] { ruleTypeDesc.getRuleDesc() });
					if (baseValueToCompare.isError6()) // To must be more than or equal to From
						throw new EpException(MessageConst.ERROR_770241, new String[] { ruleTypeDesc.getRuleDesc() });
				} 
				// 29650 - end

				// 30672 - start 19/07/2022
				if (ruleTypeDesc.getRuleType().getRangeType() != 1
						|| (ruleTypeDesc.getRuleType().getRangeType() == 1 && baseValueToCompare.isShowFlag())) {
					if (baseValueToCompare.isErrorExpDate()) {
						//#34397, #34398 - start
						if (isRenewTypes(ruleTypeDesc.getRuleType().getRuleType())) {
							throw new EpException(MessageConst.ERROR_770257, 
									new String[] {ruleTypeDesc.getRuleDesc(), getZoneName(baseValueToCompare.getZoneId())});
						} else if (!this.getManagedBean().getSelectedRuleDescType().getRuleType().getRuleType()
								.equals(ruleTypeDesc.getRuleType().getRuleType())) {
						//#34397, #34398 - end
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
					
					//#35323 and sonarLint issue fixing - Start
					boolean hasValueError = value.isError() || value.isErrorToRequired();
					//#35323 and sonarLint issue fixing - End

					if (value.getRecordStatusFlg() == 0) {// if is inActive one
						// go to next record
						continue;
					} 
					//#35323 - Start
					else if (isRangeTypeAllowed){
						if (value.isError3()) // From must be less than or equal to To
							throw new EpException(MessageConst.ERROR_770238, new String[] { ruleTypeDesc.getRuleDesc() });
					}
					//#35323 - End
					// 29669 - start
					else if (isRangeTypeAllowed && hasValueError) { //#35323
						// to value can not be less than from value
						// or
						// previous tier's TO value can not be changed to null if already add next tier
						throw new EpException(MessageConst.ERROR_770210, new String[] { ruleTypeDesc.getRuleDesc() });
					}
					// 29669 - end
					//#34397, #34398 - start
					if(isRenewTypes(ruleTypeDesc.getRuleType().getRuleType())){
						validateRenewMOFDate(baseValueToCompare, value, ruleTypeDesc.getRuleDesc());
					
					} else if (!ruleValueValidation(baseValueToCompare, value, ruleTypeDesc.getRuleType())) {
						//#34397, #34398 - end

						// hit error and return
						switch (ruleTypeDesc.getRuleType().getValidationType()) {
						//#35323 and sonarLint Fixing - Start
						case '1':
							validationType1(ruleTypeDesc, baseValueToCompare, value);
							break;
						case '2':
							validationType2(ruleTypeDesc, value);
							break;
						case '3':
							validationType3(ruleTypeDesc);
							break;
						default:
						}
						//#35323 and sonarLint Fixing - End
					}
				}

			}

		}

		IRuleSO ruleSO = null;
		try {
			ruleSO = BusinessDelegate.getInstance().getServiceObject( //#35323
					ModuleCodeConst.PROFILE_MANAGEMENT, IRuleSO.class);
			RuleReqDTO reqDTO = new RuleReqDTO();

			List<PmConfigRule> ruleTypeList = new ArrayList<>(); //#35323 and sonarlint Fixing
			for (PmConfigRuleDesc desc : getManagedBean()
					.getConfigRuleDescList()) {
				if (desc.getRuleType() != null) {
					//#35323 - Start
					validateValueForPR(desc.getRuleType());
					//#35323 - End
					ruleTypeList.add(desc.getRuleType());
				}
			}
			reqDTO.setRuleTypeList(ruleTypeList);
			/* 27827 - START */
			IOrgProfileSO orgProfileSO = (IOrgProfileSO) BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IOrgProfileSO.class);
			OrgProfileReqDTO orgProfileReqDTO = new OrgProfileReqDTO();
			orgProfileReqDTO.setPmOrgProfiles(new ArrayList<PmOrgProfile>());
			for(PmConfigRule cfRule: ruleTypeList){
				if(cfRule.getRuleType().equals(RuleTypeConst.PTJ_CODE)){
					for(PmConfigRuleValue cfRuleValue: cfRule.getConfigRuleValues()){
						if(cfRuleValue.isBrandNew()){
							orgProfileReqDTO.setOrgCode(cfRuleValue.getRuleValue());
							PmOrgProfile orgValidity = orgProfileSO.retrieveOrgProfileByCode(orgProfileReqDTO).getPmOrgProfile();
							orgProfileReqDTO.setOrgProfileId(orgValidity.getOrgProfileId());
							OrgProfileResDTO orgProfId = orgProfileSO.retrieveOrgProfileDtlByProfileId(orgProfileReqDTO);
							orgProfileReqDTO.setPmOrgProfile(orgProfId.getPmOrgProfile());
							for(PmZone zone: getManagedBean().getZoneList()){
								if(zone.getZoneId().equals(cfRuleValue.getZoneId())){
									orgProfileReqDTO.getPmOrgProfile().getPmZone();
									orgProfileReqDTO.getPmOrgProfile().setPmZone(zone);
									orgProfileReqDTO.getPmOrgProfile().setBpk(false);
									orgProfileReqDTO.getPmOrgProfiles().add(orgProfileReqDTO.getPmOrgProfile());
								}
							}
						}
					}
				}
			}
			OrgProfileResDTO orgProfResDTO = orgProfileSO.saveOrUpdatePmOrgProfileList(orgProfileReqDTO);
			/* 27827 - END */

			setupTrackingDiaryVal(reqDTO); //#35323
			
			/************ AUDIT #28895_35509 - START **************/
			/* START - AuditgetAuditReqDTOSelectedRuleValueListOld() */
			List<PmConfigRuleDesc> listOldVal = retriveConfigRuleDescFromDB();
			Long configRuleId = getManagedBean().getSelectedRuleDescType().getRuleType().getRuleTypeId();
			for (PmConfigRuleDesc desc : listOldVal) {
				if (configRuleId.equals(desc.getRuleType().getRuleTypeId())) {
					this.getManagedBean().setSelectedRuleDescType(desc);
					/*** AUDIT #28895_35509 ***/
					getManagedBean().getAuditSelectedRuleValueListOld().addAll(desc.getRuleType().getConfigRuleValues());
					break;
				}
			}
			/* END - AuditgetAuditReqDTOSelectedRuleValueListOld() */
			/************ AUDIT #28895_35509 - END **************/
			
			RuleResDTO resDTO = ruleSO.updateRuleValueList(reqDTO);
			// clear the catch and retrive updated list from DB
			
			/************ AUDIT #28895_35509 - START **************/
			for (PmConfigRule configRule : resDTO.getRuleTypeList()) {
				for (PmConfigRuleValue cfRuleValue : configRule.getConfigRuleValues()) {
					if (cfRuleValue.isBrandNew()) {
						cfRuleValue.setRuleTypeCode(configRule.getRuleType());
						cfRuleValue.setRuleDescMS(configRule.getConfigRuleDesc().get(1).getRuleDesc());
						cfRuleValue.setRuleDataTypeCode(configRule.getRuleDataTypeId().getParameterCode());
						cfRuleValue.setRuleRangeType(BigInteger.valueOf(configRule.getRangeType()));
						if (cfRuleValue.getZoneId() != null) {
							cfRuleValue.setZoneName(getZoneName(cfRuleValue.getZoneId()));
						}
						/*** AUDIT #28895_35509 ***/
						this.getManagedBean().getAuditAddedSelectedRuleValueList().add(cfRuleValue);
					}
					if (cfRuleValue.isModified()) {
						cfRuleValue.setRuleTypeCode(configRule.getRuleType());
						cfRuleValue.setRuleDescMS(configRule.getConfigRuleDesc().get(1).getRuleDesc());
						cfRuleValue.setRuleDataTypeCode(configRule.getRuleDataTypeId().getParameterCode());
						cfRuleValue.setRuleRangeType(BigInteger.valueOf(configRule.getRangeType()));
						if (cfRuleValue.getZoneId() != null) {
							cfRuleValue.setZoneName(getZoneName(cfRuleValue.getZoneId()));
						}
						/*** AUDIT #28895_35509 ***/
						this.getManagedBean().getAuditUpdatedSelectedRuleValueList().add(cfRuleValue);
					}
				}
			}
			/************ AUDIT #28895_35509 - END **************/

			RuleConfigHelper.getInstance().relodeRuleTypeValueMaps();

			List<PmConfigRuleDesc> list = retriveConfigRuleDescFromDB();
			this.getManagedBean().setConfigRuleDescList(list);

			for (PmConfigRuleDesc desc : list) {
				if (configRuleId.equals(desc.getRuleType().getRuleTypeId())) {
					this.getManagedBean().setSelectedRuleDescType(desc);
					/*
					 * Audit getAuditConfigRuleDescList() after Save - AUDIT
					 * #28895_35509 - START *************
					 */
					/************ AUDIT #28895_35509 - START **************/
					getManagedBean().getAuditResDTOSelectedRuleValueList().addAll(desc.getRuleType().getConfigRuleValues());
					/** AUDIT END **/
					break;
				}
			}
			
			/************ AUDIT #28895_35509 - START **************/
			boolean auditConstRule = false;
			/************ AUDIT #28895_35509 - END **************/
			
			/* 27827 - START */
			if (resDTO.isSuccessful() && orgProfResDTO.isSuccessful()) {
			/* 27827 - END */
				FacesContext facesContext = FacesContext.getCurrentInstance();
				facesContext.addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, null,
								MessageUtils.getMessage(
										MessageConst.INFO_770009,
										MessageUtils.BUNDLE_COMMON_MESSAGES,
										new FacesScopes().getUserLocale())));
				
				/************ AUDIT #28895_35509 - START **************/
				auditConstRule = true;
				/************ AUDIT #28895_35509 - END **************/
			}
			
			//#35323 - Start
			this.getManagedBean().setNewPRList(false);
			sortConfigRuleValueList(this.getManagedBean().getSelectedRuleDescType().getRuleType().getConfigRuleValues()); //#35323
			if (RuleTypeConst.PURCHASE_REQUEST_THERSHOLD.equalsIgnoreCase(this.getManagedBean().getSelectedRuleDescType().getRuleType().getRuleType())){
				loadPRValues();
			}
			//#35323 - End
			
			/************ AUDIT #28895_35509 - START **************/
			if (auditConstRule) {
				IAuditPMSO pmauditSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.PROFILE_MANAGEMENT, IAuditPMSO.class);
				AuditPMBackingBeanTemplate auTemplate = new AuditPMSysParamBackingBean();
				if (pmauditSO.isAuditConfig(AuditConst.PM_AUD_CONF_SYSTEMPARAMETERS)) {
					getManagedBean().setAuditLevelCode(AuditConst.PM_LVL_CODE_2_SPCONSTANTRULE);
					auTemplate.auditSaveData(getManagedBean(), AuditConst.PM_AUD_SYSPARAMETER);
				}
				clearAllAuditBeanVal(getManagedBean());
			}
			/************ AUDIT #28895_35509 - END **************/			
		} catch (Exception e) {
			clearAllAuditBeanVal(getManagedBean());//#35509
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
					//#35323 - Start
					if (isPurchaseRequest(ruleTypeDesc)) {
						String supplCodeTypeName = retrieveSupplierAccountType(ruleTypeValue);
						
						msgParam = new String[] {
								supplCodeTypeName,
								getRuleTypeDesc(ruleTypeDesc.getRuleType().getRuleType(),
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

						String ruleValueOrSupplAccType;
						if (isPurchaseRequest(ruleTypeDesc)) {
						    ruleValueOrSupplAccType = retrieveSupplierAccountType(ruleTypeValue);
						} else {
						    ruleValueOrSupplAccType = ruleTypeValue.getRuleValue();
						}

						msgParam = new String[] {
								getRuleTypeDesc(ruleTypeDesc.getRuleType()
										.getRuleType(),
										CommonConstants.LANGUAGE_CODE_MALAY),
										ruleValueOrSupplAccType,
										ruleTypeValue.getEffDate() != null ? 
												DateUtils.dateToString("dd/MM/yyyy",ruleTypeValue.getEffDate()) : "",
										ruleTypeValue.getExpDate() != null ? 
												DateUtils.dateToString("dd/MM/yyyy",ruleTypeValue.getExpDate()) : ""
								};
					}
					//#35323 - End
				}

				if (messageId != null && msgParam != null) {
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

	//#35323 - Start
	boolean isDateOverlaped(Date start1, Date end1, Date start2, Date end2) {
		this.getManagedBean().setFirstExpiryDateIsNull(false);

		if (this.getManagedBean().getTabActiveIndex() == 0 
				&& isEmptyDate(end1) && !isEmptyDate(start2)) {
			this.getManagedBean().setFirstExpiryDateIsNull(true);
			return true;
		}
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
	
	private boolean isEmptyDate(Date d) {
	    return d == null || d.getTime() == 0L;
	}
	//#35323 - End

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
			/************ AUDIT #28895_35509 *************/
			this.getManagedBean().setAuditFeeExemptionType(exemption1.getFeeExemptionType());
			/************ END AUDIT #28895_35509 *************/
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
			/************ AUDIT #28895_35509 *************/
			// Audit Get deleted value for orgType
			if (fee.isDeleted() && fee.getFeeExemptionId() != null) {
				this.getManagedBean().getAuditDeletedFeeExemptionsForOrgType().add(fee);
			}

			// Audit Get New Added value for orgType
			if (!fee.isDeleted() && fee.getFeeExemptionId() == null && fee.isBrandNew()) {
				this.getManagedBean().getAuditAddedFeeExemptionsForOrgType().add(fee);
			}

			// Audit Get Updated value for orgType
			if (!fee.isDeleted() && fee.getFeeExemptionId() != null && !fee.isBrandNew()) {
				this.getManagedBean().getAuditUpdatedFeeExemptionsForOrgType().add(fee);
			}
			/************ END AUDIT #28895_35509 *************/
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
			/************ AUDIT #28895_35509 *************/
			// getOLD value before save new value for audit update comparison
			this.getManagedBean().getAuditFeeExemptionsForOrgTypeOld()
					.addAll(exemptionSO.getFeeExemptionsForOrgType(reqDTO).getFeeExemptions());
			/************ END AUDIT #28895_35509 *************/
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
				/************ AUDIT #28895_35509 *************/
				if (CollectionUtils.isNotEmpty(this.getManagedBean().getAuditAddedFeeExemptionsForOrgType())) {
					getAuditAddedData(this.getManagedBean().getFeeExemptionsForOrgType(), this.getManagedBean()
							.getAuditAddedFeeExemptionsForOrgType());
				}
				IAuditPMSO pmauditSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IAuditPMSO.class);
				AuditPMBackingBeanTemplate auTemplate = new AuditPMSysParamBackingBean();
				if (pmauditSO.isAuditConfig(AuditConst.PM_AUD_CONF_SYSTEMPARAMETERS)) {
					getManagedBean().setAuditLevelCode(AuditConst.PM_LVL_CODE_2_SPFEE);
					auTemplate.auditSaveData(getManagedBean(), AuditConst.PM_AUD_SYSPARAMETER);
				}
			}
			clearAllAuditBeanVal(getManagedBean());
			/************ END AUDIT #28895_35509 *************/
		} catch (Exception e) {
			clearAllAuditBeanVal(getManagedBean());
			e.printStackTrace();
		}
	}
	
	/************ AUDIT #28895_35509 *************/
	void getAuditAddedData(List<PmFeeExemption> feeExemptionsForOrgType, List<PmFeeExemption> auditAddedFeeExemptionsForOrgType) {
		for (PmFeeExemption saveVal : feeExemptionsForOrgType) {
			for (PmFeeExemption updateVal : auditAddedFeeExemptionsForOrgType) {
				if (updateVal.getFeeExemptionId() == null) {
					getAuditAddedDataDetail(saveVal, updateVal);
				}
			}
		}
	}

	void getAuditAddedDataDetail(PmFeeExemption saveVal, PmFeeExemption updateVal) {
		if (updateVal.isBrandNew() && saveVal.getEffDate().equals(updateVal.getEffDate())
				&& saveVal.getFeeExemptionType().equals(updateVal.getFeeExemptionType())) {
			updateVal.setFeeExemptionId(saveVal.getFeeExemptionId());
		}
	}

	/************ END AUDIT #28895_35509 *************/

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
			/************ AUDIT #28895_35509 *************/
			this.getManagedBean().setAuditFeeExemptionType(ProfileManagementConst.FEE_EXEMPTION_TYPE_ORG_AND_SUPPLIER);
			/************ END AUDIT #28895_35509 *************/
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
			
			/************ AUDIT #28895_35509 *************/
			// Audit Get deleted value for orgType
			if (fee.isDeleted() && fee.getFeeExemptionId() != null) {
				this.getManagedBean().getAuditDeletedFeeExemptionsForOrgItem().add(fee);
			}

			// Audit Get New Added value for orgType
			if (!fee.isDeleted() && fee.getFeeExemptionId() == null && fee.isBrandNew()) {
				this.getManagedBean().getAuditAddedFeeExemptionsForOrgItem().add(fee);
			}

			// Audit Get Updated value for orgType
			if (!fee.isDeleted() && fee.getFeeExemptionId() != null && !fee.isBrandNew()) {
				this.getManagedBean().getAuditUpdatedFeeExemptionsForOrgItem().add(fee);
			}
			/************ END AUDIT #28895_35509 *************/
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
			/************ AUDIT #28895_35509 *************/
			// getOLD value before save new value for audit update comparison
			this.getManagedBean().getAuditFeeExemptionsForOrgItemOld()
					.addAll(exemptionSO.getFeeExemptionsForOrgAndItem(reqDTO).getFeeExemptions());
			/************ END AUDIT #28895_35509 *************/
			
			FeeExemptionResDTO resDTO = exemptionSO
					.updateFeeExemptionList(reqDTO);
			// retrieve
			this.getManagedBean().getFeeExemptionsForOrgAndItem().clear();
			this.getManagedBean().getDeletedOrgAndItemFeeExemptions().clear();
			reqDTO = new FeeExemptionReqDTO();
			resDTO = exemptionSO.getFeeExemptionsForOrgAndItem(reqDTO);
			for (PmFeeExemption exemption : resDTO.getFeeExemptions()) {
				if (exemption.getItemId() == null) {
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
				
				/************ AUDIT #28895_35509 *************/
				if (CollectionUtils.isNotEmpty(this.getManagedBean().getAuditAddedFeeExemptionsForOrgItem())) {
					getAuditAddedData(this.getManagedBean().getFeeExemptionsForOrgAndItem(), this.getManagedBean()
							.getAuditAddedFeeExemptionsForOrgItem());
				}
				IAuditPMSO pmauditSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IAuditPMSO.class);
				AuditPMBackingBeanTemplate auTemplate = new AuditPMSysParamBackingBean();
				if (pmauditSO.isAuditConfig(AuditConst.PM_AUD_CONF_SYSTEMPARAMETERS)) {
					getManagedBean().setAuditLevelCode(AuditConst.PM_LVL_CODE_2_SPFEE);
					auTemplate.auditSaveData(getManagedBean(), AuditConst.PM_AUD_SYSPARAMETER);
				}
				/************ END AUDIT #28895_35509 *************/
			}
		} catch (Exception e) {
			clearAllAuditBeanVal(getManagedBean());//#35509
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
				//#29166 - CR 27827: Static Code Analysis Start - Remove the literal "true" boolean value
				if (result) {
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
			/************ AUDIT #28895_35509 *************/
			this.getManagedBean().setAuditFeeExemptionType(ProfileManagementConst.FEE_EXEMPTION_TYPE_CONTRACT);
			/************ END AUDIT #28895_35509 *************/
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
			/************ AUDIT #28895_35509 *************/
			// Audit Get deleted value for orgType
			if (fee.isDeleted() && fee.getFeeExemptionId() != null) {
				this.getManagedBean().getAuditDeletedFeeExemptionsForContract().add(fee);
			}

			// Audit Get New Added value for orgType
			if (!fee.isDeleted() && fee.getFeeExemptionId() == null && fee.isBrandNew()) {
				this.getManagedBean().getAuditAddedFeeExemptionsForContract().add(fee);
			}

			// Audit Get Updated value for orgType
			if (!fee.isDeleted() && fee.getFeeExemptionId() != null && !fee.isBrandNew()) {
				this.getManagedBean().getAuditUpdatedFeeExemptionsForContract().add(fee);
			}
			/************ END AUDIT #28895_35509 *************/

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
			/************ AUDIT #28895_35509 *************/
			// getOLD value before save new value for audit update comparison
			this.getManagedBean().getAuditFeeExemptionsForOrgContractOld()
					.addAll(exemptionSO.getFeeExemptionsForContractAndReqItem(reqDTO).getFeeExemptions());
			/************ END AUDIT #28895_35509 *************/
			
			FeeExemptionResDTO resDTO = exemptionSO
					.updateFeeExemptionList(reqDTO);
			// retrieve
			this.getManagedBean().getFeeExemptionsForContract().clear();
			this.getManagedBean().getDeletedContractFeeExemptions().clear();
			reqDTO = new FeeExemptionReqDTO();
			resDTO = exemptionSO.getFeeExemptionsForContractAndReqItem(reqDTO);

			for (PmFeeExemption exemption : resDTO.getFeeExemptions()) {
				if (exemption.getRequestItemId() == null) {
					exemption.setRequestItemId(new ScRequestItem());
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
				
				/************ AUDIT #28895_35509 *************/
				if (CollectionUtils.isNotEmpty(this.getManagedBean().getAuditAddedFeeExemptionsForContract())) {
					getAuditAddedData(this.getManagedBean().getFeeExemptionsForContract(), this.getManagedBean()
							.getAuditAddedFeeExemptionsForContract());
				}
				IAuditPMSO pmauditSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IAuditPMSO.class);
				AuditPMBackingBeanTemplate auTemplate = new AuditPMSysParamBackingBean();
				if (pmauditSO.isAuditConfig(AuditConst.PM_AUD_CONF_SYSTEMPARAMETERS)) {
					getManagedBean().setAuditLevelCode(AuditConst.PM_LVL_CODE_2_SPFEE);
					auTemplate.auditSaveData(getManagedBean(), AuditConst.PM_AUD_SYSPARAMETER);
				}
				/************ AUDIT #28895_35509 *************/

			}
		} catch (Exception e) {
			clearAllAuditBeanVal(getManagedBean());//#35509
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
			/************ AUDIT #28895_35509 *************/
			this.getManagedBean().setAuditFeeExemptionType(ProfileManagementConst.FEE_EXEMPTION_TYPE_COMMODITY);
			/************ END AUDIT #28895_35509 *************/
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
			/************ AUDIT #28895_35509 *************/
			// Audit Get deleted value for orgType
			if (fee.isDeleted() && fee.getFeeExemptionId() != null) {
				this.getManagedBean().getAuditDeletedFeeExemptionsForComodity().add(fee);
			}

			// Audit Get New Added value for orgType
			if (!fee.isDeleted() && fee.getFeeExemptionId() == null && fee.isBrandNew()) {
				this.getManagedBean().getAuditAddedFeeExemptionsForComodity().add(fee);
			}

			// Audit Get Updated value for orgType
			if (!fee.isDeleted() && fee.getFeeExemptionId() != null && !fee.isBrandNew()) {
				this.getManagedBean().getAuditUpdatedFeeExemptionsForComodity().add(fee);
			}
			/************ END AUDIT #28895_35509 *************/

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
			/************ AUDIT #28895_35509 *************/
			// getOLD value before save new value for audit update comparison
			this.getManagedBean().getAuditFeeExemptionsForOrgComodityOld()
					.addAll(exemptionSO.getFeeExemptionsForComodity(reqDTO).getFeeExemptions());
			/************ END AUDIT #28895_35509 *************/
			
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
				
				/************ AUDIT #28895_35509 *************/
				if (CollectionUtils.isNotEmpty(this.getManagedBean().getAuditAddedFeeExemptionsForComodity())) {
					getAuditAddedData(this.getManagedBean().getFeeExemptionsForComodity(), this.getManagedBean()
							.getAuditAddedFeeExemptionsForComodity());
				}
				IAuditPMSO pmauditSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IAuditPMSO.class);
				AuditPMBackingBeanTemplate auTemplate = new AuditPMSysParamBackingBean();
				if (pmauditSO.isAuditConfig(AuditConst.PM_AUD_CONF_SYSTEMPARAMETERS)) {
					getManagedBean().setAuditLevelCode(AuditConst.PM_LVL_CODE_2_SPFEE);
					auTemplate.auditSaveData(getManagedBean(), AuditConst.PM_AUD_SYSPARAMETER);
				}
				/************ AUDIT #28895_35509 *************/

			}
		} catch (Exception e) {
			clearAllAuditBeanVal(getManagedBean()); //#35509
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
		IStatusSO statusSO = (IStatusSO) BusinessDelegate.getInstance().getServiceObject("pm", IStatusSO.class);
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
				//#34445, Fix as-is bug:#40055 Start
				if (resDTO.getPmConfigGeneral() != null) {
			        getManagedBean().setSelectedConfigGeneral(resDTO.getPmConfigGeneral());
			    }
				//#34445, Fix as-is bug:#40055 End
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
		
		//#35323 - Start
		if (configRuleType.getRuleType().equalsIgnoreCase(RuleTypeConst.PURCHASE_REQUEST_THERSHOLD)){
			ruleValue.setSupplAcctTypeIds(null);
		}
		//#35323 - End

		ruleValue.setGroupValueId(configRuleType.getConfigRuleValues()
				.get(index).getGroupValueId());
		log.info("groupValueId="
				+ configRuleType.getConfigRuleValues().get(index)
						.getGroupValueId());

		int markIndex = getLastIndexToClone(
				configRuleType.getConfigRuleValues(), configRuleType
						.getConfigRuleValues().get(index).getGroupValueId());
		log.info("markIndex=" + markIndex);

		//#35323 - Start
		PmConfigRuleValue configRuleValuesMarkIndex = configRuleType.getConfigRuleValues().get(markIndex);

		if (!configRuleType.getRuleType().equalsIgnoreCase(RuleTypeConst.PURCHASE_REQUEST_THERSHOLD)) {
			if (configRuleValuesMarkIndex.getRuleValue() == null
					|| configRuleValuesMarkIndex.getRuleValue().isEmpty()) {
				throw new EpException(MessageConst.ERROR_770162);
			}
			if (configRuleValuesMarkIndex.getToValue() != null
					&& (configRuleValuesMarkIndex.getRuleValue() != null
					&& !configRuleValuesMarkIndex.getRuleValue().isEmpty())) {
				ruleValue.setFromValue(configRuleValuesMarkIndex.getToValue().add(BigDecimal.valueOf(ParameterConst.TWO_DECIMAL)));
				ruleValue.setRuleType(configRuleType);
				configRuleType.getConfigRuleValues().add(markIndex + 1, ruleValue);
			} else {
				throw new EpException(MessageConst.ERROR_770156);
			}
		} else {
			if (configRuleValuesMarkIndex.getToValue() != null
					&& (configRuleValuesMarkIndex.getSupplAcctTypeId() != null
					|| configRuleValuesMarkIndex.getSupplAcctTypeIds() != null
					&& !configRuleValuesMarkIndex.getSupplAcctTypeIds().isEmpty())) {
				BigDecimal toValue = configRuleValuesMarkIndex.getToValue();
				ruleValue.setFromValue(toValue.add(BigDecimal.valueOf(ParameterConst.TWO_DECIMAL)));
				ruleValue.setRuleType(configRuleType);
				ruleValue.setSupplAcctTypeIds(null);
				configRuleType.getConfigRuleValues().add(markIndex + 1, ruleValue);
			} else {
				throw new EpException(MessageConst.ERROR_770156);
			}
		}
		//#35323 - End
	}

	/**
	 * update ToValue.
	 * 
	 * @param index
	 *            (int index(mandatory))
	 */
	public void updateToValue(int index) {
		PmConfigRule configRuleType = this.getManagedBean().getSelectedRuleDescType().getRuleType();

		if (configRuleType.getRangeType() == 1
				&& index < configRuleType.getConfigRuleValues().size() - 1) {
			PmConfigRuleValue rulevalue = configRuleType.getConfigRuleValues()
					.get(index + 1);
			if (rulevalue.getGroupValueId().equals(
					configRuleType.getConfigRuleValues().get(index)
							.getGroupValueId())) {
				// 29669 - start
				if (configRuleType.getConfigRuleValues().get(index).getToValue() == null) {
					configRuleType.getConfigRuleValues().get(index).setErrorToRequired(true);
				} else {
					rulevalue.setFromValue(configRuleType.getConfigRuleValues()
							.get(index).getToValue().add(new BigDecimal(ParameterConst.TWO_DECIMAL))); //#35323
					configRuleType.getConfigRuleValues().get(index).setErrorToRequired(false);
				}
				// 29669 - end
			}
		}

		PmConfigRuleValue selectedValue = configRuleType.getConfigRuleValues()
				.get(index);
		// 29669, #35323 - start
		if (selectedValue.getFromValue() != null
				&& selectedValue.getToValue() != null
				&& selectedValue.getFromValue().compareTo(
						selectedValue.getToValue()) > 0) {
			selectedValue.setError(true);
			selectedValue.setError3(false);
		} else {
			selectedValue.setError(false);
			selectedValue.setError3(false);
		}
		// 29669, #35323 - end
	}
	
	/**
	 * update Group Child Date.
	 * 
	 * @param index
	 *            (int index(mandatory))
	 */
	public void updateGroupChildDate(int index) {
		PmConfigRule configRuleType = this.getManagedBean()
				.getSelectedRuleDescType().getRuleType();
		PmConfigRuleValue ruleValue = configRuleType.getConfigRuleValues().get(
				index);
		if (configRuleType.getRangeType() == 1 && ruleValue.isShowFlag()) {
			Date effDate = ruleValue.getEffDate();
			Date expDate = ruleValue.getExpDate();

			for (PmConfigRuleValue value : configRuleType.getConfigRuleValues()) {
				//#35323 - Start
				if (value.getGroupValueId() != null && value.getGroupValueId().equals(ruleValue.getGroupValueId())) {
					value.setEffDate(effDate);
					value.setExpDate(expDate);
				}
				//#35323 - End
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

	//SonarLint Fixing and #35323 - Start
	/**
	 * Adds the new ruleTypeValue.
	 **/
	public void addNewRuleTypeValue() {
		PmConfigRule configRuleType = getSelectedRuleType();
		if (configRuleType == null) {
			return;
		}
		PmConfigRuleValue newRuleValue = new PmConfigRuleValue();
		newRuleValue.setRuleType(configRuleType);
		newRuleValue.setEffDate(this.getManagedBean().getTomorrow());

		// #12177 - Start
		if (configRuleType.getRuleType().equals(RuleTypeConst.SIMPLE_QUOTE_VALIDITY_PERIOD)) {
			NgepApplicationBean.resetSqTimeValidity();
		}
		// #12177 - End

		if (configRuleType.getRangeType() == 1
				|| RuleTypeConst.PURCHASE_REQUEST_THERSHOLD.equalsIgnoreCase(configRuleType.getRuleType())) {
			if (configRuleType.getConfigRuleValues() != null && !configRuleType.getConfigRuleValues().isEmpty()) {
				int size = configRuleType.getConfigRuleValues().size();
				newRuleValue.setGroupValueId(configRuleType.getConfigRuleValues().get(size - 1).getGroupValueId() + 1);
			} else {

				newRuleValue.setGroupValueId(1);
			}

			// 29669 - start
			applyFromValue(configRuleType, newRuleValue);
			// 29669 - end
		}
		int index = configRuleType.getConfigRuleValues().size();
		configRuleType.getConfigRuleValues().add(index, newRuleValue);
		RequestContext.getCurrentInstance().execute("ruleValueTblWidget.paginator.endLink.click()");
	}
	//SonarLint Fixing and #35323 - End

	//#35323 and sonarLint fixing - Start
	/**
	 * @param pmConfigValues
	 * @param groupId
	 * @return addIndex
	 */
	public int getLastIndexToClone(List<PmConfigRuleValue> pmConfigValues,
			Integer groupId) {
		int addIndex = 0;
		for (PmConfigRuleValue ruleValue : pmConfigValues) {
		    if (ruleValue.getGroupValueId() != null && ruleValue.getGroupValueId().equals(groupId)) {
		        addIndex = pmConfigValues.indexOf(ruleValue);
		    }
		}
		return addIndex;
	}
	//#35323 and sonarLint fixing - End

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
				//#29166 CR 27827: Static Code Analysis Start = Replace this if-then-else statement by a single method invocation.
				/*
				if (!selectedValue.isModified()) {
					selectedValue.setModified(true);
				} else {
					selectedValue.setModified(false);
				}
				*/
				selectedValue.setModified(!selectedValue.isModified() ? true : false);
				//#29166 CR 27827: Static Code Analysis End
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
				//#29166 CR 27827: Static Code Analysis Start = Replace this if-then-else statement by a single method invocation.
				/*
				if (!selectedFee.isModified()) {
					selectedFee.setModified(true);
				} else {
					selectedFee.setModified(false);
				}
				*/
				selectedFee.setModified(!selectedFee.isModified() ? true : false );
				//#29166 CR 27827: Static Code Analysis End
			}
		}

	}

	public void feeExemptionStatusChangeForCommodity() {

		if (this.getCurrentIndexVal() != null) {
			PmFeeExemption selectedFee = this.getManagedBean()
					.getFeeExemptionsForComodity()
					.get(this.getCurrentIndexVal());

			if (selectedFee != null) {
				//#29166 CR 27827: Static Code Analysis Start = Replace this if-then-else statement by a single method invocation.
				/*
				if (!selectedFee.isModified()) {
					selectedFee.setModified(true);
				} else {
					selectedFee.setModified(false);
				}
				*/
				selectedFee.setModified(!selectedFee.isModified() ? true : false );
				//#29166 CR 27827: Static Code Analysis End
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
				//#29166 CR 27827: Static Code Analysis Start = Replace this if-then-else statement by a single method invocation.
				/*
				if (!selectedFee.isModified()) {
					selectedFee.setModified(true);
				} else {
					selectedFee.setModified(false);
				}
				*/
				selectedFee.setModified(!selectedFee.isModified() ? true : false );
				//#29166 CR 27827: Static Code Analysis End
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
		//#29166 CR 27827: Static Code Analysis Start = Replace this if-then-else statement by a single method invocation.
		/*
		if (!designatedArea.isModified()) {
			designatedArea.setModified(true);
		} else {
			designatedArea.setModified(false);
		}
		*/
		designatedArea.setModified(!designatedArea.isModified() ? true : false);
		//#29166 CR 27827: Static Code Analysis End
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
	public void resetErrorDialog() {}

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
		// 29669, #35323 - start
		if (selectedValue.getFromValue() != null && selectedValue.getToValue() != null && selectedValue.getFromValue().compareTo(selectedValue.getToValue()) > 0) {
			selectedValue.setError(true);
		} else {
			selectedValue.setError(false);
		}
		// 29669, #35323 - end
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
					/*** AUDIT 28895_35509 ***/
					if (CollectionUtils.isNotEmpty(getManagedBean().getAuditUpdateMainPtjSpecialApprovalList())) {
						PmPtjSpecialApproval matchingApproval = findMatchingQtValues(getManagedBean()
								.getAuditUpdateMainPtjSpecialApprovalList(), clone);
						if (matchingApproval != null
								&& matchingApproval.getPtjSpecialApprovalId().equals(clone.getPtjSpecialApprovalId())) {
							getManagedBean().getAuditMainPtjSpecialApprovalList().add(clone);
						}
					}
					/*** AUDIT 28895_35509 ***/
					
				}
				this.getManagedBean().setMainPtjSpecialApprovalList(resDTO.getPtjSpecialApprovalList());
				this.getManagedBean().setPtjSpecialApprovalList(
						resDTO.getPtjSpecialApprovalList());
				this.getManagedBean().setOriginalPtjSpecialApprovalList(resDTO.getPtjSpecialApprovalList());//#36738

				//START: #35509
				if (CollectionUtils.isNotEmpty(this.getManagedBean().getAuditMainPtjSpecialApprovalListNewVal())) {
					List<PmPtjSpecialApproval> list1 = this.getManagedBean().getMainPtjSpecialApprovalList();
					List<PmPtjSpecialApproval> list2 = this.getManagedBean().getAuditMainPtjSpecialApprovalListNewVal();
					updatedQtValues(list1, list2);
				}
				//END: #35509
			}

		} catch (Exception e) {
			log.error(e);
		} 

	}
	
	//START: #35509
	/**
	 * #35509
	 * @param list1
	 * @param list2
	 */
	public void updatedQtValues(List<PmPtjSpecialApproval> list1, List<PmPtjSpecialApproval> list2) {
		for (PmPtjSpecialApproval item1 : list1) {
			for (PmPtjSpecialApproval item2 : list2) { // new added Element
				if (item2.getPtjSpecialApprovalId() == null
						&& item1.getPmOrgProfile().getOrgValidity().getOrgCode()
								.equals(item2.getPmOrgProfile().getOrgValidity().getOrgCode())
						&& item1.getEffDate().equals(item2.getEffDate())) {
					item2.setPtjSpecialApprovalId(item1.getPtjSpecialApprovalId());
					break;
				}
			}
		}
	}
	
	/**
	 * #35509
	 * @param clone
	 * @param approval
	 * @return
	 */
	public PmPtjSpecialApproval findMatchingQtValues(List<PmPtjSpecialApproval> clone, PmPtjSpecialApproval approval) {
		for (PmPtjSpecialApproval approvalClone : clone) {
			if (approval.getPtjSpecialApprovalId().equals(approvalClone.getPtjSpecialApprovalId())) {
				return approvalClone;
			}
		}
		return null; // No match found
	}
	//END: #35509
	
	public void deleteQtValue() {
		log.info(".................deleteQtValue...................");
		Integer rowIndex = getManagedBean().getDeleteRowIndex();
		Boolean resetMinistryDropdown = false;//#36738 #40071
		if (rowIndex != null) {
			PmPtjSpecialApproval deletedSpecialApproval = this.getManagedBean()
					.getPtjSpecialApprovalList().get(rowIndex.intValue());
			if (deletedSpecialApproval != null) {
				deletedSpecialApproval.setDeleted(true);
				this.getManagedBean().getDeletedPtjSpecialApprovalList()
						.add(deletedSpecialApproval);// should added to original list before save
				//#35678 #40071 Start
				resetMinistryDropdown = getDeletedMinistryList(deletedSpecialApproval);
				this.getManagedBean().getOriginalPtjSpecialApprovalList().remove(deletedSpecialApproval);
				//#36738 #40071 End
				
				/*** START: AUDIT 28895_35509 ***/
				CollectionUtils.isNotEmpty(this.getManagedBean().getDeletedPtjSpecialApprovalList());
				deletedNullPkId(this.getManagedBean().getDeletedPtjSpecialApprovalList());
				/*** END: AUDIT 28895_35509 ***/
				
				this.getManagedBean().getPtjSpecialApprovalList()
						.remove(deletedSpecialApproval);
				
				this.getManagedBean().getNewPtjSpecialApprovalList()
					.remove(deletedSpecialApproval);
				
				/*** START: AUDIT 28895_35509 ***/
				this.getManagedBean().getAuditMainPtjSpecialApprovalList().remove(deletedSpecialApproval);
				this.getManagedBean().getAuditUpdateMainPtjSpecialApprovalList().remove(deletedSpecialApproval);
				this.getManagedBean().getAuditMainPtjSpecialApprovalListNewVal().remove(deletedSpecialApproval);
				/*** END: AUDIT 28895_35509 ***/
			}
		}
		//#36738 #40071 Start
		if (resetMinistryDropdown){
			resetQtValues();
		}//#36738 #40071 End
	}
	
	/*** START: AUDIT 28895_35509 ***/
	/**
	 * #35509
	 * @param itr
	 */
	public void deletedNullPkId(List<PmPtjSpecialApproval> deletedList) {
		if (CollectionUtils.isNotEmpty(deletedList)) {
			for (PmPtjSpecialApproval del : deletedList) {
				if (del.getPtjSpecialApprovalId() == null) {
					deletedList.remove(del);
				}
				break;
			}
		}
	}
	/*** END: AUDIT 28895_35509 ***/
	
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
			//#36738 #40071 Start
			List<String> deletedMinistryNameList = this.getManagedBean().getDeletedMinistryNameList() !=  null
					? this.getManagedBean().getDeletedMinistryNameList() : new ArrayList<String>();
			for (PmOrgProfile orgProfile : list) {
				if (deletedMinistryNameList.contains(orgProfile.getPmOrgValidity().getOrgName())){
					continue;
				}
				items[i++] = new SelectItem(orgProfile.getOrgProfileId(),
						orgProfile.getOrgName());
			}
			items = Arrays.copyOf(items, i);
			//#36738 #40071 end
			
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
	    
	    /************ START: AUDIT #28895_35509 *************/
		this.getManagedBean().getAuditMainPtjSpecialApprovalListNewVal()
				.addAll(this.getManagedBean().getNewPtjSpecialApprovalList());
		/************ END: AUDIT #28895_35509 *************/
		
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
				
				/************ AUDIT #28895_35509 *************/
				IAuditPMSO pmauditSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IAuditPMSO.class);
				AuditPMBackingBeanTemplate auTemplate = new AuditPMSysParamBackingBean();
				if (pmauditSO.isAuditConfig(AuditConst.PM_AUD_CONF_SYSTEMPARAMETERS)) {
					auTemplate.auditSaveData(getManagedBean(), AuditConst.PM_AUD_SYSPARAMETER);
				}
				/************ END AUDIT #28895_35509 *************/
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
				// #36738
				if(approval.getMinistry() != null &&
						approval.getMinistry().getOrgProfileId().equals(getManagedBean().getSelectedMinistryId())){
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
							/************ AUDIT #28895_35509 *************/
							auditMatchedIdUpdateList(clone, 1);
							/************ END AUDIT #28895_35509 *************/
							addTrackingDiaryIntoList(messageId, params, approval, reqDTO);		
						}
						
						//#29166 - CR 27827: Static Code Analysis Start - This branch's code block is the same as the block.
						if( (approval.getExpDate() !=null && clone.getExpDate() !=null && !approval.getExpDate().equals(clone.getExpDate())) 
								|| (clone.getExpDate() == null && approval.getExpDate() !=null) ){
							messageId = MessageConst.TD_80218;
							params= new String[]{
									approval.getPmOrgProfile().getPmOrgValidity().getOrgCode(),
									approval.getPmOrgProfile().getPmOrgValidity().getOrgName(),
									clone.getExpDate() != null ? 
											DateUtils.dateToString(CommonConstants.DEFAULT_DATE_FORMAT,clone.getExpDate()) : "-",
									approval.getExpDate() != null ? 
											DateUtils.dateToString(CommonConstants.DEFAULT_DATE_FORMAT,approval.getExpDate()) : "-"
							};
							/************ AUDIT #28895_35509 *************/
							auditMatchedIdUpdateList(clone, 2);
							/************ END AUDIT #28895_35509 *************/
							addTrackingDiaryIntoList(messageId, params, approval, reqDTO);		
						}
						/*
						else if(clone.getExpDate() == null && approval.getExpDate() !=null){
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
						*/
						//#29166 - CR 27827: Static Code Analysis End - This branch's code block is the same as the block.
						
						if(!nf.format(approval.getProcureAmt()).equals(nf.format(clone.getProcureAmt()))){
							messageId = MessageConst.TD_80219;
							params= new String[]{
									approval.getPmOrgProfile().getPmOrgValidity().getOrgCode(),
									approval.getPmOrgProfile().getPmOrgValidity().getOrgName(),
									clone.getProcureAmt() != null ? nf.format(clone.getProcureAmt()).toString() : "-",
									approval.getProcureAmt() != null ? nf.format(approval.getProcureAmt()).toString() : "-"
							};
							
							/************ AUDIT #28895_35509 *************/
							auditMatchedIdUpdateList(clone, 3);
							/************ END AUDIT #28895_35509 *************/
							
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
				
				/************ AUDIT #28895_35509 *************/
				getManagedBean().getAuditDeletedMainPtjSpecialApprovalList().add(deleted);
				/************ END AUDIT #28895_35509 *************/
				
				addTrackingDiaryIntoList(messageId, params, deleted, reqDTO);	
			}
		}
	}
	
	/************ START: AUDIT #28895_35509 *************/
	private void auditMatchedIdUpdateList(PmPtjSpecialApproval updateVal, int paramVal) {
		if (CollectionUtils.isEmpty(getManagedBean().getAuditUpdateMainPtjSpecialApprovalList())) {
			getManagedBean().getAuditUpdateMainPtjSpecialApprovalList().add(updateVal);
		}
		for (PmPtjSpecialApproval approval : getManagedBean().getAuditUpdateMainPtjSpecialApprovalList()) {
			if (updateVal.getPtjSpecialApprovalId().equals(approval.getPtjSpecialApprovalId())) {
				insertUpdateVal(approval, updateVal, paramVal);
			} else {
				update = true;
			}
		}
		if (Boolean.valueOf(update)) {
			getManagedBean().getAuditUpdateMainPtjSpecialApprovalList().add(updateVal);
			newUpdatePK(updateVal, paramVal);
		}
	}

	private void newUpdatePK(PmPtjSpecialApproval updateVal, int paramVal) {
		for (PmPtjSpecialApproval approval : getManagedBean().getAuditUpdateMainPtjSpecialApprovalList()) {
			if (updateVal.getPtjSpecialApprovalId().equals(approval.getPtjSpecialApprovalId())) {
				insertUpdateVal(approval, updateVal, paramVal);
			}
		}
	}

	private void insertUpdateVal(PmPtjSpecialApproval approval, PmPtjSpecialApproval updateVal, int paramVal) {
		if (updateVal.getPtjSpecialApprovalId().equals(approval.getPtjSpecialApprovalId())) {
			if (paramVal == 1) {
				approval.setEffDate(updateVal.getEffDate());
			}
			if (paramVal == 2) {
				approval.setExpDate(updateVal.getExpDate());
			}
			if (paramVal == 3) {
				approval.setProcureAmt(updateVal.getProcureAmt());
			}
		}
		update = false;
	}

	/************ END AUDIT #28895_35509 *************/
	
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
				
				/************ AUDIT #28895_35509 *************/
				/* get pm_fee_exemption records before save/update new records */
				getManagedBean().setResDTOOld(feeSO.getFeeExemptionsYepProcessing(reqFDTO));
				/************ END AUDIT #28895_35509 *************/
				
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
					
					/************ AUDIT #28895_35509 *************/
					AuditPMBackingBeanTemplate auTemplate = new AuditPMSysParamBackingBean();
					IAuditPMSO pmauditSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IAuditPMSO.class);
					if (pmauditSO.isAuditConfig(AuditConst.PM_AUD_CONF_SYSTEMPARAMETERS)) {
						getManagedBean().setResDTO(resDTO);
						auTemplate.auditSaveData(getManagedBean(), AuditConst.PM_AUD_SYSPARAMETER);
					}
					/************ END AUDIT #28895_35509 *************/
					
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
			
			//#29166 CR 27827: Static Code Analysis - Remove "isValid" expression which always evaluates to "true" 
			//if(isValid && (p6_PATperiod != null && p6_PATperiod.getEffDate() != null && p6_PATperiod.getExpDate() != null)) { // original
			if((p6_PATperiod != null && p6_PATperiod.getEffDate() != null && p6_PATperiod.getExpDate() != null)) {
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
		// 29669,29641,29650 - start
		RequestContext.getCurrentInstance().reset("form:settingTabId:ruleConfigId"); // reset form when select different Rule if UI validation error
		// 29669,29641,29650 - end
        if (getManagedBean().getSelectedRuleDescType() != null) {
        	selectedRuleType = getManagedBean().getSelectedRuleDescType()
        			.getRuleType().getRuleType();
        	//#29267 - CR 22113: Static Code Analysis
        	//shaqimi start - replaced if-else with single method invocation
        	getManagedBean().setLOPOSelected(selectedRuleType.equals("LOPO"));
        	//end #29267 - CR 22113: Static Code Analysis
			//#35323 - Start
			if (RuleTypeConst.PURCHASE_REQUEST_THERSHOLD.equalsIgnoreCase(selectedRuleType)) {
				loadPRValues();
			}
			//#35323 - End
        }
        //#36738 Start
        Long ruleTypeId = this.getManagedBean().getSelectedRuleDescType().getRuleType().getRuleTypeId();
        if (this.getManagedBean().getOriginalConfigRuleValueMap() != null && this.getManagedBean().getOriginalConfigRuleValueMap().containsKey(ruleTypeId)){
        	this.getManagedBean().setOriginalConfigRuleValueList(this.getManagedBean().getOriginalConfigRuleValueMap().get(ruleTypeId));
        }else{
            this.getManagedBean().setOriginalConfigRuleValueList(this.getManagedBean().getSelectedRuleDescType().getRuleType().getConfigRuleValues());
        }
        resetRuleValue();
        this.getManagedBean().setRuleValueCollapsePanel(true);
        //#36738 End
	}
	
	/* 27827 - START */
	/**
	 * Adds the selected PTJ configuration rule values to the current rule.
	 *
	 * @throws EpException if no PTJ is selected or if the selected PTJ has already been added
	 */
	public void addPTJCRValues(){
		log.info("addPTJCRValues");
		
		PmConfigRule configRuleType = this.getManagedBean().getSelectedRuleDescType().getRuleType();
		PmConfigRuleValue newRuleValue  = null;
		int index = 0;
		
		for(PmOrgProfile orgProfile : getManagedBean().getPtjList()){
			if(orgProfile.isSelected()){
				newRuleValue = new PmConfigRuleValue();
				newRuleValue.setPmOrgProfile(orgProfile);
				newRuleValue.setRuleType(configRuleType);
				newRuleValue.setEffDate(this.getManagedBean().getTomorrow());
				newRuleValue.setRuleValue(newRuleValue.getPmOrgProfile().getOrgCode());
				newRuleValue.setRuleDescription(newRuleValue.getPmOrgProfile().getOrgName());
				newRuleValue.setZoneId(getManagedBean().getSelectedZoneId());
				newRuleValue.setPtjId(orgProfile); //#27827
				index = configRuleType.getConfigRuleValues().size();
				configRuleType.getConfigRuleValues().add(index, newRuleValue);
			}
		}
		RequestContext.getCurrentInstance().execute("ptjCRPopup.hide();");
		
		log.info("end addPTJCRValues");
	}
	
	//#34397, #34398, #27827
	/**
	 * Retrieve zones
	 */
	public void retrievePmZoneList() {
		IZoneSO zoneSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IZoneSO.class);
		ZoneResDTO resDTO = zoneSO.retrieveActiveZoneList();
		this.getManagedBean().setZoneList(resDTO.getZoneList());
		
		List<PmZone> zones = new ArrayList<>();
		//semenanjung, sabah, sarawak
		List<String> mainCodes = Arrays.asList("1", "2", "3");
		for (PmZone zone : getManagedBean().getZoneList()) {
			if (mainCodes.contains(zone.getZoneCode())) {
				zones.add(zone);
			}
		}
		Collections.sort(zones, new Comparator<PmZone>(){
			@Override
			public int compare(final PmZone s1, final PmZone s2) {
				return s1.getZoneCode().compareTo(s2.getZoneCode());
			}
		});
		getManagedBean().setMainZones(zones);
	}
	/* 27827 - END */
	
	// 29650 - start
	public void valueErrorCheck(int index) {
		PmConfigRule configRuleType = this.getManagedBean()
				.getSelectedRuleDescType().getRuleType();
		PmConfigRuleValue selectedValue = configRuleType.getConfigRuleValues()
				.get(index);
		
		if (configRuleType.getRangeType() == 3) {
			if (selectedValue.getRuleNumberValue() != null && selectedValue.getToValue() != null) {
				if (selectedValue.getRuleNumberValue().compareTo(selectedValue.getToValue()) > 0) {
					selectedValue.setError1(true);
					selectedValue.setError6(false);
				}
				else {
					selectedValue.setError1(false);
					selectedValue.setError6(false);
				}
			}
			if (selectedValue.getRuleNumberValue() != null && selectedValue.getFromValue() != null) {
				if (selectedValue.getRuleNumberValue().compareTo(selectedValue.getFromValue()) < 0) {
					selectedValue.setError2(true);
					selectedValue.setError4(false);
				}
				else {
					selectedValue.setError2(false);
					selectedValue.setError4(false);
				}
			}
		}
	}
	
	public void fromErrorCheck(int index) {
		PmConfigRule configRuleType = this.getManagedBean()
				.getSelectedRuleDescType().getRuleType();
		PmConfigRuleValue selectedValue = configRuleType.getConfigRuleValues()
				.get(index);
				
		if (configRuleType.getRangeType() == 3) {
			if (selectedValue.getFromValue() != null && selectedValue.getToValue() != null) {
				if (selectedValue.getFromValue().compareTo(selectedValue.getToValue()) > 0) {
					selectedValue.setError3(true);
					selectedValue.setError5(false);
				}
				else {
					selectedValue.setError3(false);
					selectedValue.setError5(false);
				}
			}
			if (selectedValue.getFromValue() != null && selectedValue.getRuleNumberValue() != null) {
				if (selectedValue.getFromValue().compareTo(selectedValue.getRuleNumberValue()) > 0) {
					selectedValue.setError4(true);
					selectedValue.setError2(false);
				}
				else {
					selectedValue.setError4(false);
					selectedValue.setError2(false);
				}
			}
		}
		
		//#35323 - Start
		if (configRuleType.getRangeType() == 1 || configRuleType.getRangeType() == 2) {
			if (selectedValue.getFromValue() != null && selectedValue.getToValue() != null
					&& selectedValue.getFromValue().compareTo(selectedValue.getToValue()) > 0) {
				selectedValue.setError3(true);
			} else {
				selectedValue.setError3(false);
			}
		}
		//#35323 - End
	}
	
	public void toErrorCheck(int index) {
		PmConfigRule configRuleType = this.getManagedBean()
				.getSelectedRuleDescType().getRuleType();
		PmConfigRuleValue selectedValue = configRuleType.getConfigRuleValues()
				.get(index);
				
		if (configRuleType.getRangeType() == 3) {
			if (selectedValue.getToValue() != null && selectedValue.getRuleNumberValue() != null) {
				if (selectedValue.getToValue() != null && selectedValue.getFromValue() != null) {
					if (selectedValue.getToValue().compareTo(selectedValue.getFromValue()) < 0) {
						selectedValue.setError5(true);
						selectedValue.setError3(false);
					}
					else {
						selectedValue.setError5(false);
						selectedValue.setError3(false);
					}
				}
				if (selectedValue.getToValue().compareTo(selectedValue.getRuleNumberValue()) < 0) {
					selectedValue.setError6(true);
					selectedValue.setError1(false);
				}
				else {
					selectedValue.setError6(false);
					selectedValue.setError1(false);
				}
			}
		}
	}
	// 29650 - end
	// #30672, #29669 - 29/01/2023 - Remove this code
	
	//#34397, #34398 , #35509 - start
	/**
	 * @param type
	 * @return true for MOF renew period rule types
	 */
	public boolean isRenewTypes(String type) {
		return RuleTypeConst.RENEW_PERIOD_UPDATE.equals(type) || RuleTypeConst.RENEW_PERIOD_ADD_CAT.equals(type)
			|| RuleTypeConst.RENEW_PERIOD_BUMI.equals(type) || RuleTypeConst.RENEW_PERIOD_CANCEL_CAT.equals(type)
			|| RuleTypeConst.RENEW_PERIOD.equals(type);
	}
	
	//#27827 - start
	public List<PmZone> getZones() {
		String type = getManagedBean().getSelectedRuleDescType().getRuleType().getRuleType();
		if(RuleTypeConst.PTJ_CODE.equals(type)){
			return getManagedBean().getMainZones();
		} else {
			return isRenewTypes(type) ? getManagedBean().getMainZones() : getManagedBean().getZoneList();
		}
	}
	//#27827 - end
	
	private void validateRenewMOFParams(PmConfigRuleDesc ruleDesc){
		for (PmConfigRuleValue val : ruleDesc.getRuleType().getConfigRuleValues()) {
			if (val.getZoneId() == null && val.isBrandNew()) {
				throw new EpException(MessageConst.ERROR_770253,
						new String[] {getLabel("lbl.pm.zone"), ruleDesc.getRuleDesc()});
			} else if (val.getRuleValue() == null) {
				throw new EpException(MessageConst.ERROR_770254,
						new String[] {getLabel("lbl.pm.value"), ruleDesc.getRuleDesc(), getZoneName(val.getZoneId())});
			}
		}
	}
	
	private void validateRenewMOFDate(PmConfigRuleValue baseVal, PmConfigRuleValue value, String desc){
		if (baseVal.getZoneId() != value.getZoneId()) {
			return;
		}
		if (value.getEffDate() == null) {
			throw new EpException(MessageConst.ERROR_770254,
					new String[] {getLabel("lbl.pm.as.effdate"), desc, getZoneName(value.getZoneId())});
		}
		if (baseVal.getExpDate() == null) {
			throw new EpException(MessageConst.ERROR_770255,
					new String[] {desc, getZoneName(value.getZoneId())});
		}
		long baseExp = baseVal.getExpDate().getTime();
		long valEff = value.getEffDate().getTime();
		if (baseExp == valEff) {
			throw new EpException(MessageConst.ERROR_770256,
					new String[] {desc, getZoneName(value.getZoneId())});
		}
		if (baseExp > valEff) {
			throw new EpException(MessageConst.ERROR_770257,
					new String[] {desc, getZoneName(value.getZoneId())});
		}
	}
	
	/**
	 * @param label
	 * @param params
	 * @return label based on user locale
	 */
	public String getLabel(String label, String... params){
		if (params != null) {
			return ResourceBundleUtils.getMessage(label, params, 
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, getFacesScopes().getUserLocale());
		}
		return ResourceBundleUtils.getMessage(label, 
				ResourceBundleUtils.BUNDLE_COMMON_LABELS, getFacesScopes().getUserLocale());
	}
	
	/**
	 * @param zoneId
	 * @return zone name
	 */
	public String getZoneName(Long zoneId){
		PmZone zone = (PmZone) my.ep.helper.EntityHelper.getEntityBySinglePropertyFromList(
				zoneId, getManagedBean().getZoneList(), "zoneId");
		return zone != null ? zone.getZoneName() : "";
	}
	//#34397, #34398, #35509 - end
	
	//START: #35509
	void clearAllAuditBeanVal(RuleCatalogueManageBean managedBeans) {
		managedBeans.getResDTO().setFeeExemptions(null);
		managedBeans.getResDTOOld().setFeeExemptions(null);
		managedBeans.setAuditMainPtjSpecialApprovalList(null);
		managedBeans.setAuditUpdateMainPtjSpecialApprovalList(null);
		managedBeans.setAuditDeletedMainPtjSpecialApprovalList(null);
		managedBeans.setAuditMainPtjSpecialApprovalListNewVal(null);

		managedBeans.setAuditDeletedFeeExemptionsForOrgType(null);
		managedBeans.setAuditAddedFeeExemptionsForOrgType(null);
		managedBeans.setAuditUpdatedFeeExemptionsForOrgType(null);
		managedBeans.setAuditFeeExemptionsForOrgTypeOld(null);

		managedBeans.setAuditFeeExemptionsForOrgItemOld(null);
		managedBeans.setAuditUpdatedFeeExemptionsForOrgItem(null);
		managedBeans.setAuditDeletedFeeExemptionsForOrgItem(null);
		managedBeans.setAuditAddedFeeExemptionsForOrgItem(null);

		managedBeans.setAuditFeeExemptionsForOrgContractOld(null);
		managedBeans.setAuditUpdatedFeeExemptionsForContract(null);
		managedBeans.setAuditDeletedFeeExemptionsForContract(null);
		managedBeans.setAuditAddedFeeExemptionsForContract(null);

		managedBeans.setAuditFeeExemptionsForOrgComodityOld(null);
		managedBeans.setAuditUpdatedFeeExemptionsForComodity(null);
		managedBeans.setAuditDeletedFeeExemptionsForComodity(null);
		managedBeans.setAuditAddedFeeExemptionsForComodity(null);
		
		this.getManagedBean().setAuditDeletedSelectedRuleValueList(null);
		this.getManagedBean().setAuditSelectedRuleValueListOld(null);
		this.getManagedBean().setAuditUpdatedSelectedRuleValueList(null);
		this.getManagedBean().setAuditResDTOSelectedRuleValueList(null);
		this.getManagedBean().setAuditAddedSelectedRuleValueList(null);
	}
	//END: #35509
	
	// #36738 Start
	/**
	 * #36738 Search rule type
	 */
	public void searchRuleType() {
		this.getManagedBean().setNewPRList(false); //#35323
		String searchRuleType = this.getManagedBean().getSearchRuleType();
		List<PmConfigRuleDesc> configRuleDescList = this.getManagedBean().getOriginalConfigRuleDescList();
		List<PmConfigRuleDesc> searchRuleDescList = new ArrayList<>();

		for (PmConfigRuleDesc configRuleDesc : configRuleDescList) {
			if (searchRuleType != null && !searchRuleType.isEmpty()) {
				String searchRegex = searchRegex(searchRuleType);

				if (configRuleDesc.getRuleDesc().toLowerCase().contains(searchRuleType.toLowerCase())
						|| configRuleDesc.getRuleDesc().toLowerCase().matches(searchRegex.toLowerCase())) {
					searchRuleDescList.add(configRuleDesc);
				}
			}
		}

		if (CollectionUtils.isNotEmpty(searchRuleDescList)) {
			this.getManagedBean().setConfigRuleDescList(searchRuleDescList);
			this.getManagedBean().setSelectedRuleDescType(searchRuleDescList.get(0));
			//#35323 - Start
			if (RuleTypeConst.PURCHASE_REQUEST_THERSHOLD.equalsIgnoreCase(searchRuleDescList.get(0).getRuleType().getRuleType())){
				loadPRValues();
			}
			//#35323 - End
			this.getManagedBean().setOriginalConfigRuleValueList(searchRuleDescList.get(0).getRuleType().getConfigRuleValues());
		} else {
			this.getManagedBean().setConfigRuleDescList(null);
			this.getManagedBean().setSelectedRuleDescType(null);
		}

		RequestContext.getCurrentInstance().reset("form:settingTabId:ruleConfigId");
	}

	/**
	 * #36738 Reset search rule type
	 */
	public void resetRuleType() {
		this.getManagedBean().setNewPRList(false); //#35323
		this.getManagedBean().setSearchRuleType(new String());
		this.getManagedBean().setConfigRuleDescList(this.getManagedBean().getOriginalConfigRuleDescList());
		this.getManagedBean().setSelectedRuleDescType(this.getManagedBean().getOriginalConfigRuleDescList().get(0));
	}

	/**
	 * #36738 Search by rule values
	 */
	public void searchRuleValue() {
		String searchRuleValues = this.getManagedBean().getSearchRuleDataTypeValues();
		String searchRuleDesc = this.getManagedBean().getSearchRuleDataTypeDesc();
		Date searchEffDate = this.getManagedBean().getSearchRuleDataTypeEffectiveDate();
		Date searchExpDate = this.getManagedBean().getSearchRuleDataTypeExpiryDate();
		Long searchRuleZone = this.getManagedBean().getSearchRuleDataTypeZone();
		String ruleDataType = this.getManagedBean().getSelectedRuleDescType().getRuleType().getRuleDataTypeId()
				.getParameterCode();

		List<PmConfigRuleValue> originalList = this.getManagedBean().getOriginalConfigRuleValueList() != null ? 
				 this.getManagedBean().getOriginalConfigRuleValueList() : this.getManagedBean().getSelectedRuleDescType().getRuleType().getConfigRuleValues();
		List<PmConfigRuleValue> resultList = new ArrayList<>(originalList);
		HashMap<Long, List<PmConfigRuleValue>> map = this.getManagedBean().getOriginalConfigRuleValueMap();
		
		Long ruleTypeId = this.getManagedBean().getSelectedRuleDescType().getRuleType().getRuleTypeId();
		if (map == null) {
		    map = new HashMap<>();
		}
		if (!map.containsKey(ruleTypeId)) {
		    map.put(ruleTypeId, originalList);
		    this.getManagedBean().setOriginalConfigRuleValueMap(map);
		}

		// search via value
		if (StringUtils.isNotBlank(searchRuleValues)) {
			List<PmConfigRuleValue> byValue = new ArrayList<>();
			for (PmConfigRuleValue val : originalList) {
				if (ParameterConst.CONFIG_RULE_DATA_CATEGORY_CODE.equals(ruleDataType)) {
					byValue = searchByRuleValueCategoryCode(byValue, val, searchRuleValues);
				} else {
					byValue = searchByRuleValuePTJ(byValue, val, searchRuleValues);
				}
			}
			if (CollectionUtils.isEmpty(byValue)) {
				resultList.clear();
			} else {
				resultList.retainAll(byValue);
			}
		}

		// #27827 to be deployed together - START
		// search via desc
		if (StringUtils.isNotBlank(searchRuleDesc)) {
			List<PmConfigRuleValue> byDesc = searchByRuleValueDesc(originalList, searchRuleDesc, false);
			if (CollectionUtils.isEmpty(byDesc)) {
				resultList.clear();
			} else {
				resultList.retainAll(byDesc);
			}
		}

		// search via zone
		if (searchRuleZone != null && !searchRuleZone.equals(0L)) {
			List<PmConfigRuleValue> byZone = searchByZone(originalList, searchRuleZone, false);
			if (CollectionUtils.isEmpty(byZone)) {
				resultList.clear();
			} else {
				resultList.retainAll(byZone);
			}
		}
		// #27827 to be deployed together - END

		// search via effective date and expiry date (exact date if only one,
		// between if both)
		if (searchEffDate != null || searchExpDate != null) {
			List<PmConfigRuleValue> byDate = searchByDate(originalList, searchEffDate, searchExpDate, false);
			if (CollectionUtils.isEmpty(byDate)) {
				resultList.clear();
			} else {
				resultList.retainAll(byDate);
			}
		}

		if (CollectionUtils.isNotEmpty(resultList)) {
			this.getManagedBean().getSelectedRuleDescType().getRuleType().setConfigRuleValues(resultList);
		} else {
			this.getManagedBean().getSelectedRuleDescType().getRuleType()
					.setConfigRuleValues(new ArrayList<PmConfigRuleValue>());
		}
	}

	/**
	 * filter rule values by value for rule data type: category code
	 * 
	 * @param searchRuleTypeValueList
	 * @param configRuleValue
	 * @param searchRuleValues
	 * @return
	 */
	private List<PmConfigRuleValue> searchByRuleValueCategoryCode(List<PmConfigRuleValue> searchRuleTypeValueList,
			PmConfigRuleValue configRuleValue, String searchRuleValues) {
		if (searchRuleValues != null && !searchRuleValues.isEmpty()) {
			String ruleValue = configRuleValue.getCategoryL3Id().getPmCategoryL2().getPmCategoryL1()
					.getCategoryL1Code()
					+ configRuleValue.getCategoryL3Id().getPmCategoryL2().getCategoryL2Code()
					+ configRuleValue.getCategoryL3Id().getCategoryL3Code();
			String searchRegex = searchRegex(searchRuleValues);

			if (ruleValue.toLowerCase().contains(searchRuleValues.toLowerCase())
					|| ruleValue.toLowerCase().matches(searchRegex.toLowerCase())) {
				searchRuleTypeValueList.add(configRuleValue);
			}
		}
		return searchRuleTypeValueList;
	}

	/**
	 * #36738 filter rule values by value for rule data type: PTJ code
	 * 
	 * @param configRuleValuesList
	 * @param searchRuleValues
	 * @return
	 */
	private List<PmConfigRuleValue> searchByRuleValuePTJ(List<PmConfigRuleValue> searchRuleTypeValueList,
			PmConfigRuleValue configRuleValue, String searchRuleValues) {
		if (searchRuleValues != null && !searchRuleValues.isEmpty()) {
			String searchRegex = searchRegex(searchRuleValues);

			if (configRuleValue.getRuleValue().toLowerCase().contains(searchRuleValues.toLowerCase())
					|| configRuleValue.getRuleValue().toLowerCase().matches(searchRegex.toLowerCase())) {
				searchRuleTypeValueList.add(configRuleValue);
			}
		}
		return searchRuleTypeValueList;
	}

	// #27827 to be deployed together - START
	/**
	 * #36738 filter rule values by description
	 * 
	 * @param configRuleValuesList
	 * @param searchRuleValueDesc
	 * @return
	 */
	private List<PmConfigRuleValue> searchByRuleValueDesc(List<PmConfigRuleValue> configRuleValuesList,
			String searchRuleValueDesc, Boolean multipleSearchFlag) {
		List<PmConfigRuleValue> result = new ArrayList<>();

		if (searchRuleValueDesc != null && !searchRuleValueDesc.isEmpty()) {
			String searchRegex = searchRegex(searchRuleValueDesc);

			for (PmConfigRuleValue configRuleValue : configRuleValuesList) {
				String desc = configRuleValue.getRuleDescription() != null ? configRuleValue.getRuleDescription()
						.toLowerCase() : configRuleValue.getCategoryL3Id().getCategoryName().toLowerCase();
				if (desc.contains(searchRuleValueDesc.toLowerCase()) || desc.matches(searchRegex.toLowerCase())) {
					result.add(configRuleValue);
				}
			}
		}

		if (CollectionUtils.isNotEmpty(result) || !multipleSearchFlag) {
			return result;
		} else {
			return configRuleValuesList;
		}
	}

	/**
	 * #36738 filter rule values by zone
	 * 
	 * @param configRuleValuesList
	 * @param searchRuleZone
	 * @param multipleSearchFlag
	 * @return
	 */
	private List<PmConfigRuleValue> searchByZone(List<PmConfigRuleValue> configRuleValuesList, Long searchRuleZone,
			Boolean multipleSearchFlag) {
		List<PmConfigRuleValue> result = new ArrayList<>();

		if (searchRuleZone != null && searchRuleZone != 0) {
			for (PmConfigRuleValue configRuleValue : configRuleValuesList) {
				if (configRuleValue.getZoneId() != null && configRuleValue.getZoneId().equals(searchRuleZone)) {
					result.add(configRuleValue);
				}
			}
		}

		if (CollectionUtils.isNotEmpty(result) || !multipleSearchFlag) {
			return result;
		} else {
			return configRuleValuesList;
		}
	}
	// #27827 to be deployed together - END

	/**
	 * #36738 filter rule values by effective date and expiry date
	 * 
	 * @param configRuleValuesList
	 * @param searchEffDate
	 * @param searchExpDate
	 * @return
	 */
	private List<PmConfigRuleValue> searchByDate(List<PmConfigRuleValue> configRuleValuesList, Date searchEffDate,
			Date searchExpDate, Boolean multipleSearchFlag) {
		List<PmConfigRuleValue> result = new ArrayList<>();

		for (PmConfigRuleValue value : configRuleValuesList) {
			if (isDateMatch(value, searchEffDate, searchExpDate)) {
				result.add(value);
			}
		}
		return CollectionUtils.isNotEmpty(result) || !multipleSearchFlag ? result : configRuleValuesList;
	}

	/**
	 * #36738 check whether the date is matched
	 * 
	 * @param value
	 * @param searchEffDate
	 * @param searchExpDate
	 * @return
	 */
	private boolean isDateMatch(PmConfigRuleValue value, Date searchEff, Date searchExp) {
		Date valueEffDate = truncateDate(value.getEffDate());
		Date valueExpDate = truncateDate(value.getExpDate());
		Date searchEffDate = truncateDate(searchEff);
		Date searchExpDate = truncateDate(searchExp);

		if (searchEffDate != null && searchExpDate == null && valueEffDate != null) {
			return searchEffDate.equals(valueEffDate);
		}
		if (searchExpDate != null && searchEffDate == null && valueExpDate != null) {
			return searchExpDate.equals(valueExpDate);
		}
		if (valueEffDate == null || valueExpDate == null) {
			return false;
		}
		return !valueEffDate.before(searchEff) && !valueExpDate.after(searchExp);
	}

	/**
	 * #36738 Standardized the time up to milliseconds of the dates for
	 * comparison
	 * 
	 * @param date
	 * @return
	 */
	private Date truncateDate(Date date) {
		if (date != null) {
			Calendar c = Calendar.getInstance();
			c.setTime(date);
			c.set(Calendar.HOUR_OF_DAY, 0);
			c.set(Calendar.MINUTE, 0);
			c.set(Calendar.SECOND, 0);
			c.set(Calendar.MILLISECOND, 0);
			return c.getTime();
		}
		return null;
	}

	/**
	 * #36738 Reset search rule value
	 */
	public void resetRuleValue() {
		this.getManagedBean().setSearchRuleDataTypeValues(new String());
		this.getManagedBean().setSearchRuleDataTypeDesc(new String());
		this.getManagedBean().setSearchRuleDataTypeEffectiveDate(null);
		this.getManagedBean().setSearchRuleDataTypeExpiryDate(null);
		this.getManagedBean().setSearchRuleDataTypeZone(0L);
		this.getManagedBean().getSelectedRuleDescType().getRuleType()
				.setConfigRuleValues(this.getManagedBean().getOriginalConfigRuleValueList());
		sortConfigRuleValueList(this.getManagedBean().getSelectedRuleDescType().getRuleType().getConfigRuleValues()); //#35323
	}

	/**
	 * #36738 search quotation tender values
	 */
	public void searchQtValues() {
		Long searchMinistry = this.getManagedBean().getSelectedMinistryId();
		String searchPtjName = this.getManagedBean().getSearchPtjName();
		String searchPtjCode = this.getManagedBean().getSearchPtjCode();
		BigDecimal searchLimit = this.getManagedBean().getSearchQtValueLimit();

		List<PmPtjSpecialApproval> originalList = this.getManagedBean().getOriginalPtjSpecialApprovalList();
		List<PmPtjSpecialApproval> resultList = new ArrayList<>(originalList);

		// Filter by Ministry
		if (searchMinistry != null && searchMinistry != 0L) {
			selectQtValues();
			List<PmPtjSpecialApproval> byMinistry = this.getManagedBean().getPtjSpecialApprovalList();
			if (CollectionUtils.isEmpty(byMinistry)) {
				resultList.clear();
			} else {
				resultList.retainAll(byMinistry);
			}
		}

		// Filter by PTJ name
		if (StringUtils.isNotBlank(searchPtjName)) {
			List<PmPtjSpecialApproval> byName = searchByPtj(originalList, searchPtjName, true);
			if (CollectionUtils.isEmpty(byName)) {
				resultList.clear();
			} else {
				resultList.retainAll(byName);
			}
		}

		// Filter by PTJ code
		if (StringUtils.isNotBlank(searchPtjCode)) {
			List<PmPtjSpecialApproval> byCode = searchByPtj(originalList, searchPtjCode, false);
			if (CollectionUtils.isEmpty(byCode)) {
				resultList.clear();
			} else {
				resultList.retainAll(byCode);
			}
		}

		// Filter by Limit
		if (searchLimit != null) {
			List<PmPtjSpecialApproval> byLimit = searchByLimit(originalList, searchLimit);
			if (CollectionUtils.isEmpty(byLimit)) {
				resultList.clear();
			} else {
				resultList.retainAll(byLimit);
			}
		}

		sortPmPtjSpecialApprovalByMinistryName(resultList);

		if (CollectionUtils.isNotEmpty(resultList)) {
			this.getManagedBean().setPtjSpecialApprovalList(resultList);
		} else {
			this.getManagedBean().setPtjSpecialApprovalList(new ArrayList<PmPtjSpecialApproval>());
		}
	}

	/**
	 * #36738 filter QT values by PTJ name or PTJ code
	 * 
	 * @param searchPtjApprovalList
	 * @param searchPtjApproval
	 * @param searchValue
	 * @param ptjValue
	 * @return
	 */
	private List<PmPtjSpecialApproval> searchByPtj(List<PmPtjSpecialApproval> ptjApprovalList, String search,
			boolean useName) {
		List<PmPtjSpecialApproval> result = new ArrayList<>();

		if (search == null || search.isEmpty()) {
			return result;
		}

		String searchLower = search.toLowerCase();
		String regex = searchRegex(search);

		for (PmPtjSpecialApproval ptjApproval : ptjApprovalList) {
			for (PmOrgValidity validity : ptjApproval.getPmOrgProfile().getPmOrgValidityList()) {
				if (DbValueConst.RECORD_STATUS_ACTIVE !=(validity.getRecordStatusFlg())) {
					continue;
				}
				
				String value = useName ? validity.getOrgName() : validity.getOrgCode();
				if (value == null) {
					continue;
				}
				
				String valueLower = value.toLowerCase();
				if (valueLower.contains(searchLower) || valueLower.matches(regex)) {
					result.add(ptjApproval);
					break;
				}
			}
		}

		return result;
	}

	/**
	 * #36738 filter by limit
	 * 
	 * @param searchPtjApprovalList
	 * @param searchLimitString
	 * @return
	 */
	private List<PmPtjSpecialApproval> searchByLimit(List<PmPtjSpecialApproval> searchPtjApprovalList,
			BigDecimal searchLimit) {
		List<PmPtjSpecialApproval> result = new ArrayList<>();

		if (searchLimit != null) {
			for (PmPtjSpecialApproval ptjApproval : searchPtjApprovalList) {
				BigDecimal ptjProcureLimitAmount = ptjApproval.getProcureAmt();
				if (ptjProcureLimitAmount != null && ptjProcureLimitAmount.compareTo(searchLimit) == 0) {
					result.add(ptjApproval);
				}
			}
		}

		return result;
	}

	/**
	 * #36738 reset filtered quotation values
	 */
	public void resetQtValues() {
		this.getManagedBean().setPtjSpecialApprovalList(this.getManagedBean().getOriginalPtjSpecialApprovalList());
		this.getManagedBean().setSelectedMinistryId(null);
		this.getManagedBean().setSearchPtjCode(null);
		this.getManagedBean().setSearchPtjName(null);
		this.getManagedBean().setSearchQtValueLimit(null);
	}

	/**
	 * #36738 Change the search text into a regex
	 */
	public String searchRegex(String searchText) {
		String searchRegex = "";
		if (!searchText.isEmpty()) {
			searchRegex = searchText.replace("%", ".*?").replace("_", ".").replace("?", ".").replace("*", ".*?");

			return searchRegex;
		} else {
			return "";
		}
	}

	/**
	 * #36738 Reset search value on tab change
	 */
	public void onAnyTabChange() {
		int activeIndex = this.getManagedBean().getTabActiveIndex();
		if (activeIndex == 0) {
			initConfigRule();
		}
		resetRuleType();
		resetRuleValue();
		resetQtValues();
		this.getManagedBean().setRuleTypeCollapsePanel(true);
		this.getManagedBean().setRuleValueCollapsePanel(true);
		this.getManagedBean().setQtValueCollapsePanel(true);
		this.getManagedBean().setDeletedMinistryNameList(null);//#36738 #40071
	}
	
	public void onRuleTypeSearchToggle(ToggleEvent event) {
	    this.getManagedBean().setRuleTypeCollapsePanel(event.getVisibility() == Visibility.HIDDEN);
	}
	
	/**
	 * #36738 #40071
	 * Get ministry list based on the deleted ptj
	 * @param deletedSpecialApproval
	 * @return
	 */
	public Boolean getDeletedMinistryList(PmPtjSpecialApproval deletedSpecialApproval) {
		List<PmPtjSpecialApproval> ptjSpecialApprovalList = this.getManagedBean().getPtjSpecialApprovalList();
		String deletedMinistryName = deletedSpecialApproval.getPmOrgProfile().getMinistryName();
		Map<String, Integer> countMinistryMap = new HashMap<String, Integer>();
		List<String> deleteMinistryString = this.getManagedBean().getDeletedMinistryNameList() != null
				? this.getManagedBean().getDeletedMinistryNameList() : new ArrayList<String>();
		Boolean	resetMinistryDropdown = false;		
		
		for (PmPtjSpecialApproval ptjSpecialApproval : ptjSpecialApprovalList) {
			String ministryName = ptjSpecialApproval.getPmOrgProfile().getMinistryName();
			countMinistryMap.put(ministryName,
					countMinistryMap.containsKey(ministryName) ? countMinistryMap.get(ministryName) + 1 : 1);
		}	

		if (countMinistryMap.containsKey(deletedMinistryName) && countMinistryMap.get(deletedMinistryName) == 1) {
			deleteMinistryString.add(deletedSpecialApproval.getPmOrgProfile().getMinistryName());
			resetMinistryDropdown = true;
		}
		this.getManagedBean().setDeletedMinistryNameList(deleteMinistryString);
		return resetMinistryDropdown;
	}
	// #36738 End
	
	//#35323 - Start
	/**
	 * load reasonSuppAccTypeDropDown
	 * 
	 * @return getDropdownValuesBySearchBean
	 */
	public SelectItem[] reasonSuppAccTypeDropDown() {		
		return getDropdownSupplAccTypeValuesBySearchBean(ParameterTypeConst.CONFIG_RULE_SUPPLIER_ACCOUNT_TYPE, null);
	}
	
	/**
	 * add Supplier Type Account Id
	 * 
	 */
	public void addSupplierTypeAcc() {
	    Integer index = this.getManagedBean().getIndex();
	    if (index == null) {
	        return;
	    }

	    PmConfigRule configRuleType = this.getManagedBean().getSelectedRuleDescType().getRuleType();
	    PmConfigRuleValue selectedValue = configRuleType.getConfigRuleValues().get(index);
	    
	    if (selectedValue == null) {
           return;
       }
       // save selected ids into the row
       if (this.getManagedBean().getSelectedSuppAccountType() == null || this.getManagedBean().getSelectedSuppAccountType().isEmpty()) {
       		selectedValue.setSupplAcctTypeIds(null);
       		selectedValue.setSameSupplAcctType(false);
       } else if (this.getManagedBean().getSelectedSuppAccountType().size() == 1){
       		selectedValue.setSupplAcctTypeIds(new ArrayList<Long>(this.getManagedBean().getSelectedSuppAccountType()));
       		selectedValue.setSameSupplAcctType(false);
       } else {
    		selectedValue.setSupplAcctTypeIds(new ArrayList<Long>(this.getManagedBean().getSelectedSuppAccountType()));
    	    selectedValue.setSameSupplAcctType(true);
       }
	}

	private String getLabel(String key) {
		FacesContext ctx = FacesContext.getCurrentInstance();
		if (ctx == null) {
			return key;
		}
		ResourceBundle bundle = ctx.getApplication().getResourceBundle(ctx, "lbl");
		try {
			log.info("bundle=" + bundle + "key=" + key);
			return bundle.getString(key);
		} catch (MissingResourceException e) {
			throw new IllegalStateException("Missing resource bundle key: " + key, e);
		}
	}
	
	/**
	 * get Supplier Account Type label name
	 * 
	 * @param supplAccTypeId
	 * @return getLabel(key)
	 */
	public String getSupplierTypeName(Long supplAccTypeId) {
		log.info("getSupplierTypeName=" + supplAccTypeId);
		String key = ACC_TYPE_KEYS.get(supplAccTypeId);
		if (key == null) {
			return null;
		}
		return getLabel(key);
	}

	/**
	 *  Display text in table: "Basic / MOF"
	 *  
	 *  @param ids
	 *  @return sb.toString()
	 * 
	 */
	public String displaySupplAccTypes(List<Long> ids) {
	    if (ids == null || ids.isEmpty()) {
	        return null;
	    }
	    
	    // enforce display order: Basic - MOF - G2G
	    List<Long> sorted = new ArrayList<>();
	    for (Long orderedId : SUPPL_ACC_TYPE_ORDER) {
	        if (ids.contains(orderedId)) {
	            sorted.add(orderedId);
	        }
	    }

	    StringBuilder sb = new StringBuilder();
	    for (int i = 0; i < sorted.size(); i++) {
	        if (i > 0) {
	            sb.append(" / ");
	        }
	        sb.append(getSupplierTypeName(sorted.get(i))); // Label here
	    }

	    return sb.toString();
	}
	
	/**
	 *  open the popup for selected Supplier Acc Type
	 *  
	 *  @param index
	 *  @param ids
	 * 
	 */
	public void initSupplAccTypePopup(int index, List<Long> ids) {
		this.getManagedBean().setIndex(index);
		this.getManagedBean().setSelectedSuppAccountType(null);
		this.getManagedBean().setSelectedAllSuppAccountType(false);

		if (ids != null && !ids.isEmpty()) {
			this.getManagedBean().getSelectedSuppAccountType().addAll(ids);

			// 3 means all supplier account types (BASIC/MOF/G2G) are selected.
			// User ticked the "ALL" checkbox.
			if (ids.size() == 3) {
				this.getManagedBean().setSelectedAllSuppAccountType(true);
			}
		}
	}
	
	/**
	 *  Select All Supplier Account Type Id
	 * 
	 */
	public void toggleAllSuppAccountTypes() {
		if (this.getManagedBean().isSelectedAllSuppAccountType()) {
			List<Long> all = new ArrayList<>();
			all.add(RuleTypeConst.BASIC); // Basic
			all.add(RuleTypeConst.MOF); // MOF
			all.add(RuleTypeConst.G2G); // G2G
			this.getManagedBean().setSelectedSuppAccountType(all);
		} else {
			this.getManagedBean().setSelectedSuppAccountType(null);
		}
	}
	
	/**
	 * Load Purchase Request Threshold Values List
	 * 
	 */
	public void loadPRValues() {
		IRuleSO ruleSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IRuleSO.class);

		List<Object[]> rows = ruleSO.getPRList();

		if (!rows.isEmpty() && !this.getManagedBean().isNewPRList()) {
			List<PmConfigRuleValue> newList = convertToNewPRValues(rows);

			List<PmConfigRuleValue> oldList = this.getManagedBean().getSelectedRuleDescType().getRuleType().getConfigRuleValues();

			oldList.clear();
			oldList.addAll(newList);
			this.getManagedBean().setNewPRList(true);
		}
	}
	
	/**
	 *  Convert to new Purchase Request Values List
	 *  
	 * @param rows
	 * @return list
	 * 
	 */
	public List<PmConfigRuleValue> convertToNewPRValues(List<Object[]> rows) {
		List<PmConfigRuleValue> list = new ArrayList<>();

		for (Object[] row : rows) {
			PmConfigRuleValue v = new PmConfigRuleValue();
			
			v.setBrandNew(false);

			Object rawRuleType = row[1];
			String ruleTypeCode;

			if (rawRuleType instanceof BigDecimal) {
				ruleTypeCode = ((BigDecimal) rawRuleType).toString();
			} else {
				ruleTypeCode = String.valueOf(rawRuleType);
			}

			PmConfigRule rule = new PmConfigRule();
			rule.setRuleType(ruleTypeCode);

			v.setRuleType(rule);

			if (row[2] != null){
				v.setFromValue((BigDecimal) row[2]);
			} else {
				v.setFromValue(null);
			}

			if (row[3] != null){
				v.setToValue((BigDecimal) row[3]);
			} else {
				v.setToValue(null);
			}

			if (row[4] != null) {
				Number gid = (Number) row[4];
				v.setGroupValueId(gid.intValue());
			} else {
				v.setGroupValueId(null);
			}

			if (row[5] != null){
				v.setEffDate((Date) row[5]);
			}

			if (row[6] != null){
				v.setExpDate((Date) row[6]);
			}

			Number showFlagValue = (Number) row[7];
			boolean showFlag = showFlagValue != null && showFlagValue.intValue() == 1;
			v.setShowFlag(showFlag);
			
			Number suppAccType = (Number) row[8];
			boolean isSameSupplAccType = suppAccType != null && suppAccType.intValue() == 1;
			v.setSameSupplAcctType(isSameSupplAccType);

			if (row[9] != null) {
			    v.setRecordStatusFlg(((Number) row[9]).intValue());
			}

			List<Long> supplAcctTypeList = parseLongList(row[10]);

			v.setSupplAcctTypeIds(supplAcctTypeList);
			v.setMergedConfigRuleValueIds(String.valueOf(row[11]));

			list.add(v);
		}

		return list;
	}
		
	private List<Long> parseLongList(Object raw) {
	    List<Long> result = new ArrayList<>();
	    if (raw == null){
	    	return result;
	    }

	    String str = String.valueOf(raw).trim();
	    if (str.isEmpty()){
	    	return result;
	    }

	    for (String s : str.split(",")) {
	        if (!s.trim().isEmpty()) {
	            result.add(Long.valueOf(s.trim()));
	        }
	    }

	    return result;
	}
	
	private PmConfigRule getSelectedRuleType() {
	    if (getManagedBean().getSelectedRuleDescType() == null) {
	        return null;
	    }
	    return getManagedBean().getSelectedRuleDescType().getRuleType();
	}
	
	private void applyFromValue(PmConfigRule configRuleType, PmConfigRuleValue newRuleValue) {
		String ruleType = configRuleType.getRuleType();

		if (RuleTypeConst.QTPMM.equals(ruleType)) {
			newRuleValue.setFromValue(BigDecimal.ZERO);
			return;
		}

		if (RuleTypeConst.PURCHASE_REQUEST_THERSHOLD.equals(ruleType)) {
			newRuleValue.setFromValue(null);
			newRuleValue.setSupplAcctTypeIds(null);
			return;
		}

		newRuleValue.setFromValue(BigDecimal.ONE);
	}

	/**
	 * Rebuild Purchase Request (PR) rule values from merged data.
	 *
	 * @param descRuleType the PR rule configuration containing merged value
	 */
	private void setupPurchaseRequestRule(PmConfigRule descRuleType) {
	    IParameterSO parameterSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);

	    ParameterReqDTO reqDTO = new ParameterReqDTO();

	    PmConfigRule ruleType = descRuleType;

	    List<PmConfigRuleValue> original = new ArrayList<>(ruleType.getConfigRuleValues());

	    ruleType.getConfigRuleValues().clear();

	    for (PmConfigRuleValue merged : original) {
	        processMergedRow(ruleType, merged, parameterSO, reqDTO);
	    }
	}

	/**
	 * Expand one merged rule row into multiple rule value.
	 *
	 * @param ruleType 
	 * @param merged
	 * @param parameterSO
	 * @param reqDTO
	 */
	private void processMergedRow(PmConfigRule ruleType, PmConfigRuleValue merged, IParameterSO parameterSO, ParameterReqDTO reqDTO) {
		IRuleSO ruleSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IRuleSO.class);

		List<Long> supplierTypeIds = merged.getSupplAcctTypeIds();
		List<Long> configValueIds = splitIds(merged.getMergedConfigRuleValueIds());

		boolean first = true;

		if (supplierTypeIds != null && !supplierTypeIds.isEmpty() && merged.getRecordStatusFlg().intValue() == DbValueConst.RECORD_STATUS_ACTIVE) {
			for (int i = 0; i < supplierTypeIds.size(); i++) {
				PmConfigRuleValue value = buildRuleValue(merged, supplierTypeIds, configValueIds, i, first, parameterSO, reqDTO);

				if (value.isShowFlag() && first) {
					first = false;
				}

				ruleType.getConfigRuleValues().add(value);
			}

		} else {
			for (int i = 0; i < configValueIds.size(); i++) {
				Long configValueId = getConfigValueId(configValueIds, i);

				PmConfigRuleValue value = ruleSO.getRuleValueByRuleValueId(RuleTypeConst.PURCHASE_REQUEST_THERSHOLD, configValueId);

				ruleType.getConfigRuleValues().add(value);
			}
		}
	}

	/**
	 * Build a rule value for a specific supplier account type.
	 *
	 * @return a rule value ready to be added into the rule list
	 */
	private PmConfigRuleValue buildRuleValue(PmConfigRuleValue merged, List<Long> supplierTypeIds,
			List<Long> configValueIds, int index, boolean first, IParameterSO parameterSO, ParameterReqDTO reqDTO) {

		Long supplAcctTypeId = supplierTypeIds.get(index);
		Long configValueId = getConfigValueId(configValueIds, index);

		if (configValueId == null) {
			return createNewRuleValue(merged, supplAcctTypeId, first, parameterSO, reqDTO);
		}

		return updateExistingRuleValue(merged, configValueId);
	}

	/**
	 * Create a new Purchase Request rule value.
	 *
	 * @return newly created rule value
	 */
	private PmConfigRuleValue createNewRuleValue(PmConfigRuleValue merged, Long supplAcctTypeId, boolean first,
			IParameterSO parameterSO, ParameterReqDTO reqDTO) {

		PmConfigRuleValue value = new PmConfigRuleValue();
		value.setRuleType(merged.getRuleType());
		value.setBrandNew(true);
		value.setFromValue(merged.getFromValue());
		value.setToValue(merged.getToValue());
		value.setEffDate(merged.getEffDate());
		value.setExpDate(merged.getExpDate());
		value.setGroupValueId(merged.getGroupValueId());
		value.setRecordStatusFlg(DbValueConst.RECORD_STATUS_ACTIVE);
		value.setSameSupplAcctType(merged.isSameSupplAcctType());

		reqDTO.setParameterId(supplAcctTypeId);
		value.setSupplAcctTypeId(parameterSO.retrieveParameterById(reqDTO).getPmParameter());

		if(!merged.isShowFlag()){
		  value.setShowFlag(false);
		} else {
		   value.setShowFlag(first);
		}

		return value;
	}

	/**
	 * Retrieve and update an existing Purchase Request rule value.
	 * Update expired date and set modified flag.
	 *
	 * @return updated rule value
	 */
	private PmConfigRuleValue updateExistingRuleValue(PmConfigRuleValue merged, Long configValueId) {

		IRuleSO ruleSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
				IRuleSO.class);

		PmConfigRuleValue value = ruleSO.getRuleValueByRuleValueId(RuleTypeConst.PURCHASE_REQUEST_THERSHOLD,
				configValueId);

		if (isExpDateChanged(value.getExpDate(), merged.getExpDate())) {
		    value.setExpDate(merged.getExpDate());
		    /* 35323 - Audit Start */
		    value.setModified(true);
		    /* 35323 - Audit End */
		}

		return value;
	}

	/**
	 * Retrieve a configuration rule value ID from the given list based on the specified index.
	 *
	 * @param ids
	 * @param index
	 * @return the ID if available, otherwise null
	 */
	private Long getConfigValueId(List<Long> ids, int index) {
		if (ids == null || ids.isEmpty() || index >= ids.size()) {
			return null;
		}
		return ids.get(index);
	}

	/**
	 * Split a comma-separated string of IDs and converts them into a list of Long value.
	 *
	 * @param ids
	 * @return list
	 */
	private List<Long> splitIds(String ids) {
		List<Long> result = new ArrayList<>();

		if (ids == null || ids.trim().isEmpty()) {
			return result;
		}

		String[] parts = ids.split(",");

		for (String p : parts) {
			if (p != null && !p.trim().isEmpty()) {
				try {
					result.add(Long.valueOf(p.trim()));
				} catch (NumberFormatException e) {
					// log and skip invalid value
					SLAUtils.getInstance().logSLAError(ModuleCodeConst.PROFILE_MANAGEMENT, null, null,
							"RuleCatalogueBackingBean.splitIds", null, e);
				}
			}
		}

		return result;
	}
	
	private void validateRuleValue(PmConfigRuleDesc ruleTypeDesc, PmConfigRuleValue ruleTypeValue) {
		if (isNonPRWithMissingValue(ruleTypeDesc, ruleTypeValue)) {
			throw new EpException(MessageConst.ERROR_770133, new String[] { ruleTypeDesc.getRuleDesc() });
		}

		if (isPRWithMissingFromValue(ruleTypeDesc, ruleTypeValue)) {
			throw new EpException(MessageConst.ERROR_770234, new String[] { ruleTypeDesc.getRuleDesc() });
		}

		if (isPRWithMissingSupplierType(ruleTypeDesc, ruleTypeValue)) {
			throw new EpException(MessageConst.ERROR_770274, new String[] { ruleTypeDesc.getRuleDesc() });
		}
	}

	private boolean isPR(PmConfigRuleDesc desc) {
		return RuleTypeConst.PURCHASE_REQUEST_THERSHOLD.equalsIgnoreCase(desc.getRuleType().getRuleType());
	}

	private boolean isNonPRWithMissingValue(PmConfigRuleDesc desc, PmConfigRuleValue value) {
		return !isPR(desc) && value.getRuleValue() == null;
	}

	private boolean isPRWithMissingFromValue(PmConfigRuleDesc desc, PmConfigRuleValue value) {
		return isPR(desc) && value.getFromValue() == null;
	}

	private boolean isPRWithMissingSupplierType(PmConfigRuleDesc desc, PmConfigRuleValue value) {

		return isPR(desc) && value.getSupplAcctTypeIds().isEmpty() && value.getSupplAcctTypeId() == null;
	}
	
	private boolean isRangeTypeOneOrTwo(PmConfigRuleDesc desc) {
	    int rangeType = desc.getRuleType().getRangeType();
	    return rangeType == 1 || rangeType == 2;
	}

	private boolean isPurchaseRequest(PmConfigRuleDesc desc) {
	    return RuleTypeConst.PURCHASE_REQUEST_THERSHOLD
	            .equalsIgnoreCase(desc.getRuleType().getRuleType());
	}
	
	/**
	 * Retrieves dropdown SelectItem array for Supplier Account Type. Safely
	 * handles nulls and sorts by CodeName (case-insensitive).
	 * 
	 * @param parameterType
	 *            Parameter type key.
	 * @param searchBean
	 *            Search bean for filtering parameter descriptions.
	 * @return Array of SelectItems sorted by CodeName, with a default "Select"
	 *         option.
	 */
	public SelectItem[] getDropdownSupplAccTypeValuesBySearchBean(String parameterType,
			ISearchBean<PmParameterDesc> searchBean) {

		List<PmParameterDesc> parameterDescs = getParameterDescList(parameterType, searchBean);
		if (parameterDescs == null || parameterDescs.isEmpty()) {
			return new SelectItem[0];
		}

		sortByCodeName(parameterDescs);

		List<SelectItem> items = new ArrayList<>();
		for (PmParameterDesc pd : parameterDescs) {
			if (isValid(pd)) {
				items.add(new SelectItem(pd.getPmParameter().getParameterId(), pd.getCodeName()));
			}
		}
		return items.toArray(new SelectItem[0]);
	}

	/**
	 * Retrieves the parameter description list safely.
	 */
	private List<PmParameterDesc> getParameterDescList(String parameterType, ISearchBean<PmParameterDesc> searchBean) {
		try {
			return ParameterHelper.getInstance().getParameterDescListBySearchBean(parameterType, searchBean);
		} catch (Exception e) {
			SLAUtils.getInstance().logSLAError(ModuleCodeConst.PROFILE_MANAGEMENT, null, null,
					"RuleCatalogueBackingBean.getParameterDescList", null, e);
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(), e.getMessage()));
			return Collections.emptyList();
		}
	}

	/**
	 * Sorts list safely by CodeName.
	 */
	private void sortByCodeName(List<PmParameterDesc> list) {
	    if (list == null || list.size() <= 1) {
	        return;
	    }

	    Collections.sort(list, new Comparator<PmParameterDesc>() {
	        @Override
	        public int compare(PmParameterDesc o1, PmParameterDesc o2) {
	            return resolvePriority(o1) - resolvePriority(o2);
	        }
	    });
	}
	
	private int resolvePriority(PmParameterDesc pd) {
	    if (!isValid(pd)) {
	        return 0;
	    }

	    Long id = pd.getPmParameter().getParameterId();
	    Integer priority = SUPPLIER_PRIORITY.get(id);

	    return priority != null ? priority.intValue() : 0;
	}
	
	/**
	 * Validates parameter description entry.
	 */
	private boolean isValid(PmParameterDesc pd) {
	    if (pd == null) {
	        return false;
	    }

	    PmParameter pmParam = pd.getPmParameter();
	    if (pmParam == null || pmParam.getParameterId() == null) {
	        return false;
	    }

	    String codeName = pd.getCodeName();
	    if (codeName == null || codeName.trim().isEmpty()) {
	        return false;
	    }

	    return true;
	}

	private void validateValueForPR(PmConfigRule descRuleType){
		if (RuleTypeConst.PURCHASE_REQUEST_THERSHOLD.equalsIgnoreCase(descRuleType.getRuleType())
				&& descRuleType.getConfigRuleValues().get(0).getRuleValueId() == 0L) {
			setupPurchaseRequestRule(descRuleType);
		}
	}

	private String getExpiryDateErrorCode() {
		return this.getManagedBean().isFirstExpiryDateIsNull() ? MessageConst.ERROR_770275 : MessageConst.ERROR_770109;
	}

	private void validationType1(PmConfigRuleDesc ruleTypeDesc, PmConfigRuleValue baseValueToCompare, PmConfigRuleValue value) {
		if (RuleTypeConst.LO_PO_ACKNOWLEDGMENT_PERIOD.equals(ruleTypeDesc.getRuleType().getRuleType())) {
			return;
		}

		if (ruleTypeDesc.getRuleType().getRangeType() == 1
				&& baseValueToCompare.getGroupValueId().equals(value.getGroupValueId())) {
			throw new EpException(MessageConst.ERROR_770110, new String[] { ruleTypeDesc.getRuleDesc() });
		}
		throw new EpException(getExpiryDateErrorCode(), new String[] { ruleTypeDesc.getRuleDesc() });
	}

	private void validationType2(PmConfigRuleDesc ruleTypeDesc, PmConfigRuleValue value) {
		if (RuleTypeConst.LO_PO_ACKNOWLEDGMENT_PERIOD.equals(ruleTypeDesc.getRuleType().getRuleType())) {
			return;
		}

		if (RuleTypeConst.CATEGORY_CODE.equals(ruleTypeDesc.getRuleType().getRuleDataTypeId().getParameterCode())) {

			throw new EpException(MessageConst.ERROR_770199, new String[] { ruleTypeDesc.getRuleDesc(),
					value.getCategoryDisplayValue() });
		}

		throw new EpException(MessageConst.ERROR_770110, new String[] { ruleTypeDesc.getRuleDesc() });
	}

	private void validationType3(PmConfigRuleDesc ruleTypeDesc) {
		if (RuleTypeConst.LO_PO_ACKNOWLEDGMENT_PERIOD.equals(ruleTypeDesc.getRuleType().getRuleType())) {
			return;
		}

		throw new EpException(MessageConst.ERROR_770154, new String[] { ruleTypeDesc.getRuleDesc() });
	}

	private String retrieveSupplierAccountType(PmConfigRuleValue ruleTypeValue) {

		String parameterCode = ruleTypeValue.getSupplAcctTypeId().getParameterCode();

		if (ParameterConst.BASIC_PARAM_CODE.equalsIgnoreCase(parameterCode)) {
			return ParameterConst.ASAS;
		}

		if (ParameterConst.MOF_PARAM_CODE.equalsIgnoreCase(parameterCode)) {
			return ParameterConst.MOF;
		}

		return ParameterConst.G2G;
	}
	
	private boolean isExpDateChanged(Date oldDate, Date newDate) {

	    if (oldDate == null && newDate == null) {
	        return false;
	    }

	    if (oldDate != null && newDate == null) {
	        return false;
	    }
	    
	    if (oldDate == null && newDate != null) {
	        return true;
	    }

	    return oldDate.compareTo(newDate) != 0;
	}

	private void sortConfigRuleValueList(List<PmConfigRuleValue> configRuleValueList) {
		Collections.sort(configRuleValueList, new Comparator<PmConfigRuleValue>() {
			@Override
			public int compare(PmConfigRuleValue o1, PmConfigRuleValue o2) {
				return new CompareToBuilder()
				.append(o1.getEffDate(), o2.getEffDate())
				.append(o1.getFromValue(), o2.getFromValue())
				.toComparison();
			}
		});
	}
	//#35323 - End
}

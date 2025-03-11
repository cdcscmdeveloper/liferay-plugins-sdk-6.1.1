package my.ep.web.backingbean.pm.supplierperformanceCriteria;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.bean.search.SearchMultiFilterBean;
import my.ep.biz.service.pm.common.audit.IAuditPMSO;
import my.ep.biz.service.pm.common.criteria.ICriteriaSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.AuditConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ProfileManagementConst;
import my.ep.constants.CommonConstants;
import my.ep.dto.pm.common.criteria.CriteriaReqDTO;
import my.ep.dto.pm.common.criteria.CriteriaResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.BaseEntity;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmCriteria;
import my.ep.persistence.entity.pm.common.PmCriteriaDesc;
import my.ep.persistence.entity.pm.common.PmLanguage;
import my.ep.persistence.logging.LogKey;
import my.ep.type.aut.SearchCondition;
import my.ep.type.aut.SearchOperator;
import my.ep.util.MessageUtils;
import my.ep.util.SLAUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.pm.audit.AuditPMBackingBeanTemplate;
import my.ep.web.backingbean.pm.audit.mapbeans.AuditPMCriteriaBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.bean.pm.CriteriaSearchBean;
import my.ep.web.managedbean.pm.supplierperformanceCriteria.CriteriaManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.PmCommonUtil;

@ManagedBean(name = "criteriaBackingBean")
@ViewScoped
public class CriteriaBackingBean extends BaseBackingBean<CriteriaManagedBean> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String NAV_TO_MAIN = "criteriaMain";
	public static final String NAV_TO_MAINTAIN = "/xhtml/pm/supplierperformanceCriteria/criteriaMaintenance";
	public static final boolean SEARCH_DEFAULT = false;
	private static final Logger log = EpLogger.getLogger(CriteriaManagedBean.class);

	private IPagingDataModel<PmCriteriaDesc> pmCriteriaDescDataModel = new PmCriteriaPagingDataModel();

	public IPagingDataModel<PmCriteriaDesc> getPmCriteriaDescDataModel() {
		return pmCriteriaDescDataModel;
	}

	public void setPmCriteriaDescDataModel(IPagingDataModel<PmCriteriaDesc> pmCriteriaDescDataModel) {
		this.pmCriteriaDescDataModel = pmCriteriaDescDataModel;
	}

	/**
	 * The constructor used for save user logging.
	 * 
	 **/
	public CriteriaBackingBean() {
		log.info("inside constructor");
		PmUser pmUser = new FacesScopes().getUserSessionBean();
		if (pmUser != null && pmUser.getUserId() != null) {
			log.info("***userId: " + pmUser.getUserId());

			new LogKey(pmUser.getUserId());
		} else {
			new LogKey(1L);
		}

	}

	private void debugLogger(Object message) {
		if (log.isDebugEnabled()) {
			log.debug(message);
		}
		log.info(message);
	}

	/**
	 * reload to SupplierCriteria search page
	 * 
	 */
	public String redirectSupCriteriaSearch() {
		// getManagedBean().setCriteriaSearchBean(null);
		return NAV_TO_MAIN;
	}

	/**
	 * Reset supplierCriteria search
	 */
	public void resetSupCriteriaSearch() {
		log.info("resetCriteriaSearchBean");
		getManagedBean().setLazy(false);
		CriteriaSearchBean searchBean = new CriteriaSearchBean();
		this.getManagedBean().setCriteriaSearchBean(searchBean);
		pmCriteriaDescDataModel = new PmCriteriaPagingDataModel();
		getManagedBean().setCriteriaSearchBean(null);
	}

	/**
	 * get status item dropdown
	 * 
	 */

	public static SelectItem[] getStatusList() {
		return PmCommonUtil.getCommonStatusOptions();
	}

	/**
	 * search SupplierCriteria info based on SupplierCriteria codes and names
	 * search
	 */
	private ISearchBean<PmCriteriaDesc> getSearchBeanData(ISearchBean<PmCriteriaDesc> searchBean) {
		{
			// boolean isSearchable = SEARCH_DEFAULT;
			// boolean isEmpty = true;

			CriteriaSearchBean lSearchBean = this.getManagedBean().getCriteriaSearchBean();

			if (lSearchBean.getCriteriaCode() != null && lSearchBean.getCriteriaCode().trim().length() > 0) {
				searchBean.getDataFilterBeans().add(
						new SearchFilterBean("pmCriteria.criteriaCode", SearchOperator.OPERATOR_LIKE.value(), lSearchBean
								.getCriteriaCode(), true));
			}
			if (lSearchBean.getCriteriaName() != null && lSearchBean.getCriteriaName().trim().length() > 0) {

				searchBean.getDataFilterBeans().add(
						new SearchFilterBean("criteria", SearchOperator.OPERATOR_LIKE.value(), getManagedBean()
								.getCriteriaSearchBean().getCriteriaName(), true));
			}

			if (lSearchBean.getStatus() != null && lSearchBean.getStatus().trim().length() > 0) {
				searchBean.getDataFilterBeans().add(
						new SearchFilterBean("pmCriteria.recordStatusFlg", SearchOperator.OPERATOR_EQUALS.value(), lSearchBean
								.getStatus()));
			} else {
				SearchFilterBean codesSearchBean = new SearchFilterBean(SearchOperator.OPERATOR_IN.value());
				codesSearchBean.setSearchField("pmCriteria.recordStatusFlg");
				codesSearchBean.setSearchValue(new ArrayList<String>(Arrays.asList(ParameterConst.RECORD_STATUS_INACTIVE,
						ParameterConst.RECORD_STATUS_ACTIVE)));
				searchBean.getDataFilterBeans().add(codesSearchBean);

			}
			return searchBean;
		}
	}

	public void searchSupplierCriteria() {
		this.getManagedBean().setLazy(true);
		pmCriteriaDescDataModel.reloadData();

	}

	private class PmCriteriaPagingDataModel extends PagingDataModel<PmCriteriaDesc> {
		private static final long serialVersionUID = 1L;

		public PmCriteriaPagingDataModel() {
			super("dtCriteriaResult", getManagedBean().isLazy());
			// super("dtCriteriaResult");
		}

		@Override
		public ISearchResultBean<PmCriteriaDesc> pageChangeListener(ISearchBean<PmCriteriaDesc> searchBean) {
			if (log.isDebugEnabled()) {
				log.debug("pageChangeListener");
			}

			try {
				ISearchBean<PmCriteriaDesc> searchBeanData = getSearchBeanData(searchBean);
				// CriteriaSearchBean rSearchBean =
				// getManagedBean().getCriteriaSearchBean();
				CriteriaReqDTO reqDTO = new CriteriaReqDTO();
				FacesScopes fs = new FacesScopes();
				// reqDTO.setCriteriaCode(rSearchBean.getCriteriaCode());
				reqDTO.setLanguageCode(fs.getUserLocale().getLanguage());
				reqDTO.setCriteriaDescSearchBean(searchBeanData);

				ICriteriaSO reqSO = (ICriteriaSO) BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.PROFILE_MANAGEMENT, ICriteriaSO.class);
				CriteriaResDTO resDTO = reqSO.retrieveCriteriaDescBySearchBean(reqDTO);
				if (resDTO.isSuccessful()) {
					return resDTO.getCriteriaDescResultBean();
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new EpException(MessageConst.ERROR_770005);
			}
			return null;
		}
	}

	public String redirectToMaintainCriteria(Long id) throws Exception {
		log.info("redirectToMaintainCriteria - Start");

		if (id != null && id > 0) {
			loadCriteria(id);
		} else {
			intitailCriteria();
		}

		log.info("redirectToMaintainCriteria - End");
		return NAV_TO_MAINTAIN;
	}

	public String saveSupplierCriteriaDtls() {
		log.info("saveSupplierCriteriaDtls - Start");
		String returnPage = null;

		if (this.getManagedBean().getSelectedCriteria().isBrandNew()) {
			generateCriteriaCode();

		}

		if (DbValueConst.RECORD_STATUS_ACTIVE == getManagedBean().getSelectedCriteria().getRecordStatusFlg().intValue()) {
			validateCriteriaDesc();
		}

		if (this.getManagedBean().getSelectedCriteria().isBrandNew()) {
			returnPage = createCriteria(this.getManagedBean().getSelectedCriteria());
		} else {
			returnPage = updateCriteria(this.getManagedBean().getSelectedCriteria());
		}
		log.info("saveSupplierCriteriaDtls - End");
		return returnPage;
	}

	/**
	 * This method used for reset the search bean field and navigate to the
	 * search main page.
	 * 
	 **/
	public void preLoadSupplierCriteria() {
		log.info("preLoadgroupSearch....");
		renewSesionObject();
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null, NAV_TO_MAIN);
	}

	public void setUserDetails(BaseEntity baseObject) {
		if (baseObject.isBrandNew()) {
			baseObject.setCreatedBy(1L);
			baseObject.setCreatedDate(new Timestamp(System.currentTimeMillis()));
		}
		baseObject.setChangedBy(1L);
	}

	private String updateCriteria(PmCriteria newCriteria) {
		log.info("updateCriteria");
		try {
			CriteriaReqDTO reqDTO = new CriteriaReqDTO();
			reqDTO.setPmCriteria(newCriteria);
			ICriteriaSO reqSO = (ICriteriaSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
					ICriteriaSO.class);

			/************ AUDIT #28895_35509 *************/
			IAuditPMSO pmauditSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
					IAuditPMSO.class);
			AuditPMBackingBeanTemplate auTemplate = new AuditPMCriteriaBackingBean();
			reqDTO.setCriteriaId(newCriteria.getCriteriaId());
			if (pmauditSO.isAuditConfig(AuditConst.PM_AUD_CONF_SUPPPERFCRITERIA) && newCriteria.getCriteriaId() != null) {
				getManagedBean().setPmCriteriaOld(reqSO.retrieveCriteriaById(reqDTO).getPmCriteria());
			}
			/************ END AUDIT #28895_35509 *************/
			CriteriaResDTO resDTO = reqSO.updateSupCriteria(reqDTO);
			if (resDTO.isSuccessful()) {
				loadCriteria(resDTO.getPmCriteria().getCriteriaId());
				log.info("updateCriteria - successful");
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, null, MessageUtils.getMessage(MessageConst.INFO_779006,
								new String[] { newCriteria.getCriteriaCode() }, MessageUtils.BUNDLE_COMMON_MESSAGES,
								new FacesScopes().getUserLocale())));
				/************ AUDIT #28895_35509 *************/
				if (pmauditSO.isAuditConfig(AuditConst.PM_AUD_CONF_SUPPPERFCRITERIA)) {
					getManagedBean().setSelectedCriteria(resDTO.getPmCriteria());
					auTemplate.auditSaveData(getManagedBean(), AuditConst.PM_AUD_SUPPPERFCRITERIACFG);
				}
				/************ END AUDIT #28895_35509 *************/
			}

		} catch (Exception e) {
			log.info("updateCriteria - fail"); // TODO
			e.printStackTrace();
			throw new EpException(MessageConst.ERROR_770004);
		}
		return null;
	}

	public PmCriteria retrieveCriteriaObjById(Long criteriaId) {
		log.info("retrieveCriteriaObjById - Start");
		PmCriteria pmCriteria = new PmCriteria();
		try {
			CriteriaReqDTO reqDTO = new CriteriaReqDTO();
			reqDTO.setCriteriaId(criteriaId);
			ICriteriaSO reqSO = (ICriteriaSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
					ICriteriaSO.class);
			CriteriaResDTO resDTO = reqSO.retrieveCriteriaById(reqDTO);
			if (resDTO.isSuccessful()) {
				pmCriteria = resDTO.getPmCriteria();
			}

		} catch (Exception e) {
			throw new EpException(MessageConst.ERROR_770002, new String[] { e.getMessage() });
		}
		log.info("retrieveCriteriaObjById - End");
		return pmCriteria;
	}

	public void loadCriteria(Long id) {
		log.info("loadCriteria - Start");
		try {
			PmCriteria pmCriteria = retrieveCriteriaObjById(id);
			List<PmLanguage> languagues = PmCommonUtil.retrieveLanguageObjs();
			if (pmCriteria.getPmCriteriaDescs().size() != languagues.size()) {
				for (PmLanguage langauge : languagues) {
					boolean isRequired = true;
					for (PmCriteriaDesc desc : pmCriteria.getPmCriteriaDescs()) {
						if (langauge.getLanguageCode().equals(desc.getLanguageCode())) {
							isRequired = false;
							break;
						}
					}
					if (isRequired) {
						PmCriteriaDesc criteriaDesc = new PmCriteriaDesc();
						criteriaDesc.setLanguageCode(langauge.getLanguageCode());
						criteriaDesc.setPmLanguage(langauge);
						pmCriteria.addPmCriteriaDescs(criteriaDesc);
					}
				}
			}
			this.getManagedBean().setSelectedCriteria(pmCriteria);

		} catch (Exception e) {
			e.printStackTrace();
			throw new EpException(MessageConst.ERROR_770004);
		}

		log.info("loadCriteria - End");
	}

	private void generateCriteriaCode() {
		ICriteriaSO criteriaSO = (ICriteriaSO) BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, ICriteriaSO.class);
		String criteriaCode = null;
		criteriaCode = ProfileManagementConst.seqCriteriaStartingNo;
		CriteriaReqDTO reqDTO = new CriteriaReqDTO();
		reqDTO.setCriteriaCode(criteriaCode);
		CriteriaResDTO resDTO = criteriaSO.generateCriteriaCode(reqDTO);
		getManagedBean().getSelectedCriteria().setCriteriaCode(resDTO.getCriteriaCode());

	}

	private void validateCriteriaDesc() {
		log.info("validateCriteriaDesc");
		String enDesc = null;
		String msDesc = null;
		for (PmCriteriaDesc desc : getManagedBean().getSelectedCriteria().getPmCriteriaDescs()) {
			if (CommonConstants.DEFAULT_LANGUAGE_CODE.equals(desc.getPmLanguage().getLanguageCode())) {
				enDesc = desc.getCriteria();
			}

			if (CommonConstants.LANGUAGE_CODE_MALAY.equals(desc.getPmLanguage().getLanguageCode())) {
				msDesc = desc.getCriteria();
			}
		}

		if (StringUtils.isBlank(enDesc) || StringUtils.isBlank(msDesc)) {
			throw new EpException("Error Desctiptions");
		}

		// Create search bean
		SearchBean<PmCriteriaDesc> searchBean = new SearchBean<PmCriteriaDesc>();
		searchBean.getDataFilterBeans().add(
				new SearchMultiFilterBean(new SearchFilterBean("criteria", SearchOperator.OPERATOR_EQUALS.value(), enDesc
						.toLowerCase().trim(), true), new SearchFilterBean("pmLanguage.languageCode",
						SearchOperator.OPERATOR_EQUALS.value(), CommonConstants.DEFAULT_LANGUAGE_CODE,
						SearchCondition.CONDITION_AND.value())));

		searchBean.getDataFilterBeans().add(
				new SearchMultiFilterBean(new SearchFilterBean("criteria", SearchOperator.OPERATOR_EQUALS.value(), msDesc
						.toLowerCase().trim(), true), SearchCondition.CONDITION_OR.value(), new SearchFilterBean(
						"pmLanguage.languageCode", SearchOperator.OPERATOR_EQUALS.value(), CommonConstants.LANGUAGE_CODE_MALAY,
						SearchCondition.CONDITION_AND.value())));

		searchBean.getDataFilterBeans()
				.add(new SearchFilterBean("recordStatusFlg", SearchOperator.OPERATOR_EQUALS.value(),
						DbValueConst.RECORD_STATUS_ACTIVE));

		searchBean.getDataFilterBeans().add(
				new SearchFilterBean("pmCriteria.recordStatusFlg", SearchOperator.OPERATOR_EQUALS.value(),
						DbValueConst.RECORD_STATUS_ACTIVE));

		if (!getManagedBean().getSelectedCriteria().isBrandNew()) {
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("pmCriteria.criteriaId", SearchOperator.OPERATOR_NOT_EQUALS.value(), getManagedBean()
							.getSelectedCriteria().getCriteriaId()));
		}

		// Filter by search bean
		CriteriaReqDTO reqDTO = new CriteriaReqDTO();
		reqDTO.setCriteriaDescSearchBean(searchBean);
		reqDTO.setValidation(true);
		ICriteriaSO reqSO = (ICriteriaSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
				ICriteriaSO.class);
		CriteriaResDTO resDTO = reqSO.retrieveCriteriaDescBySearchBean(reqDTO);
		if (resDTO.isSuccessful() && resDTO.getCriteriaDescList() != null && !resDTO.getCriteriaDescList().isEmpty()) {
			{
				throw new EpException(MessageConst.ERROR_770220,
						new String[] { PmCommonUtil.getResourceBundleLabels("lbl.pm.criteria.name") });
			}
		}
	}

	private String createCriteria(PmCriteria newCriteria) {
		PmUser user = new FacesScopes().getUserSessionBean();
		try {
			CriteriaReqDTO reqDTO = new CriteriaReqDTO();
			reqDTO.setPmCriteria(newCriteria);
			reqDTO.setPmUser(user);
			log.info("******createCriteria=" + newCriteria.getCriteriaCode() + "| UserId=" + reqDTO.getPmUser().getUserId());
			ICriteriaSO criteriaSO = (ICriteriaSO) BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, ICriteriaSO.class);
			CriteriaResDTO resDTO = criteriaSO.createCriteria(reqDTO);
			if (resDTO.isSuccessful()) {
				// loadCriteria(resDTO.getPmCriteria().getCriteriaId());
				log.info("createCriteria - successful"); // TODO 770015
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, null, MessageUtils.getMessage(MessageConst.INFO_779005,
								new String[] { newCriteria.getCriteriaCode() }, MessageUtils.BUNDLE_COMMON_MESSAGES,
								new FacesScopes().getUserLocale())));

				/************ AUDIT #28895_35509 *************/
				IAuditPMSO pmauditSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IAuditPMSO.class);
				AuditPMBackingBeanTemplate auTemplate = new AuditPMCriteriaBackingBean();
				if (pmauditSO.isAuditConfig(AuditConst.PM_AUD_CONF_SUPPPERFCRITERIA)) {
					getManagedBean().setSelectedCriteria(resDTO.getPmCriteria());
					auTemplate.auditSaveData(getManagedBean(), AuditConst.PM_AUD_SUPPPERFCRITERIACFG);
				}
				/************ END AUDIT #28895_35509 *************/

				return redirectSupCriteriaSearch();
			}
		} catch (Exception e) {
			log.info("createCriteria - fail");
			SLAUtils.getInstance().logSLAError(ModuleCodeConst.PROFILE_MANAGEMENT, MessageConst.ERROR_770004,
					newCriteria.getCriteriaCode(), "criteriaSO.createCriteria(reqDTO)", user.getUserId(), e);
		}
		return null;
	}

	public void intitailCriteria() {
		log.info("intitailCriteria- Start");
		PmCriteria pmCriteria = new PmCriteria();

		List<PmLanguage> languagues = PmCommonUtil.retrieveLanguageObjs();
		pmCriteria.setPmCriteriaDescs(new ArrayList<PmCriteriaDesc>());
		for (PmLanguage language : languagues) {

			PmCriteriaDesc criteriaDesc = new PmCriteriaDesc();
			criteriaDesc.setLanguageCode(language.getLanguageCode());
			criteriaDesc.setPmLanguage(language);
			pmCriteria.addPmCriteriaDescs(criteriaDesc);

		}

		this.getManagedBean().setSelectedCriteria(pmCriteria);

		log.info("intitailCriteria - End");

	}
}

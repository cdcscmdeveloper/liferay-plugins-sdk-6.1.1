package my.ep.web.backingbean.pm.specialservice;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Locale;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.pm.common.specialservice.ISpecialServiceSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.pm.common.PmTrackingDairyVO;
import my.ep.dto.pm.common.specialservice.SpecialServiceReqDTO;
import my.ep.dto.pm.common.specialservice.SpecialServiceResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.BaseEntity;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmRemarkSpecialService;
import my.ep.type.aut.SearchOperator;
import my.ep.util.MessageUtils;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.pm.specialservice.SpecialServiceManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.PmCommonUtil;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import my.ep.constant.pm.ParameterTypeConst;

/**
 * The Class specialServiceBackingBean.
 * 
 * @author
 */

@ManagedBean(name = "SpecialServiceBackingBean")
@ViewScoped
public class SpecialServiceBackingBean extends BaseBackingBean<SpecialServiceManagedBean> {
	private static final long serialVersionUID = 1L;

	/** The Constant NAV_TO_MAIN. */
	private static final String NAV_TO_MAIN = "specialServiceMain";
	
	/** The Constant NAV_TO_CREATE. */
	private static final String NAV_TO_CREATE = "/xhtml/pm/specialservice/specialServiceCreate";
	
	/** The Constant NAV_TO_MAINTAIN. */
	public static final String NAV_TO_MAINTAIN = "/xhtml/pm/specialservice/specialServiceMaintenance";

	/** The Constant log. */
	private static final Logger log = EpLogger.getLogger(SpecialServiceBackingBean.class);

	/** The Special Service field data model. */
	private IPagingDataModel<PmParameterDesc> specialServiceDataModel = new SpecialServicePagingDataModel();

	/**
	 * Instantiates a new Special Service backing bean.
	 */
	public SpecialServiceBackingBean() {
		log.info("inside constructor");
	}

	/**
	 * Debug logger.
	 * 
	 * @param message
	 *            the message
	 */
	private void debugLogger(Object message) {
		if (log.isDebugEnabled()) {
			log.debug(message);
		}
		log.info(message);
	}

	/**
	 * reload to Special Service search page.
	 * 
	 * @return the string
	 */
	public String redirectSpecialServiceSearch() {
		debugLogger("redirectSpecialServiceSearch");
		return NAV_TO_MAIN;

	}

	/**
	 * reset search for Special Service.
	 */
	public void resetSearchSpecialService() {
		debugLogger("resetSearchSpecialService");
		this.getManagedBean().setLazy(false);
		this.getManagedBean().setCodeName(new String());
		this.getManagedBean().setStatus(null);
		specialServiceDataModel = new SpecialServicePagingDataModel();
	}

	/**
	 * reload to special service creation page.
	 * 
	 * @return (/xhtml/pm/specialservice/specialServiceCreate.xhtml)
	 */
	public String createspecialService() {
		debugLogger("createSpecialService");
		PmParameterDesc pmSpecialService = new PmParameterDesc();
		String languageCode = new FacesScopes().getUserLocale().getLanguage();
		pmSpecialService.setLanguageCode(languageCode);
		this.getManagedBean().setPmSpecialService(pmSpecialService);

		return NAV_TO_CREATE;
	}

	/**
	 * get status item dropdown.
	 * 
	 * @return the special status list
	 */
	public SelectItem[] getSpecialServiceStatusList() {

		SelectItem[] items = new SelectItem[2];
		items[0] = new SelectItem(1, "Active");
		items[1] = new SelectItem(0, "Inactive");
		return items;
	}

	/**
	 * Gets the pm special service status list.
	 * 
	 * @return SelectItem[]
	 */
	public static SelectItem[] getPmspecialServiceStatusList() {
		// return PmCommonUtil.getStatusOptions();
		return PmCommonUtil.getCommonStatusOptions();
	}

	private ISearchBean<PmParameterDesc> getSpecialServiceSearchBeanData(ISearchBean<PmParameterDesc> searchBean) {

		if (StringUtils.isNotBlank(getManagedBean().getCodeName())) {
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("codeName", SearchOperator.OPERATOR_LIKE.value(), getManagedBean()
							.getCodeName(), true));
		}

		if (getManagedBean().getStatus() != null) {
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("recordStatusFlg", SearchOperator.OPERATOR_EQUALS.value(), getManagedBean()
							.getStatus()));
		}

		searchBean.getDataFilterBeans().add(
				new SearchFilterBean("pmParameter.parameterType", SearchOperator.OPERATOR_EQUALS.value(), ParameterTypeConst.SPECIAL_SERVICE_TYPE));

		return searchBean;
	}

	public void retrieveSpecialService() {
		debugLogger("retrieveSpecialService");

		specialServiceDataModel.reloadData();
	}

	public IPagingDataModel<PmParameterDesc> getSpecialServiceDataModel() {
		return specialServiceDataModel;
	}

	public void setSpecialServiceDataModel(IPagingDataModel<PmParameterDesc> specialServiceDataModel) {
		this.specialServiceDataModel = specialServiceDataModel;
	}

	/**
	 * The Class SpecialServicePagingDataModel.
	 */
	private class SpecialServicePagingDataModel extends PagingDataModel<PmParameterDesc> {

		/** The Constant serialVersionUID. */
		private static final long serialVersionUID = 1L;

		/**
		 * Instantiates a new special Service paging data model.
		 */
		public SpecialServicePagingDataModel() {
			super("dtSpecialServiceResult", getManagedBean().isLazy());
		}

		@Override
		public ISearchResultBean<PmParameterDesc> pageChangeListener(ISearchBean<PmParameterDesc> searchBean) {

			try {
				ISearchBean<PmParameterDesc> searchBeanData = getSpecialServiceSearchBeanData(searchBean);
				SpecialServiceReqDTO reqDTO = new SpecialServiceReqDTO();

				String languageCode = new FacesScopes().getUserLocale().getLanguage();
				reqDTO.setLanguageCode(languageCode);
				searchBeanData.getDataFilterBeans().add(
						new SearchFilterBean("languageCode", SearchOperator.OPERATOR_EQUALS.value(), languageCode));

				reqDTO.setSpecialServiceSearchBean(searchBeanData);

				ISpecialServiceSO reqSO = (ISpecialServiceSO) BusinessDelegate.getInstance().getServiceObject("pm",
						ISpecialServiceSO.class);
				SpecialServiceResDTO resDTO = reqSO.retrieveSpecialServiceBySearch(reqDTO);
				getManagedBean().setLazy(true);
				if (resDTO.isSuccessful()) {
					return resDTO.getSpecialServiceSearchResultBean();
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}
	}

	public String goToMaintaincePage(PmParameterDesc pmSpecialService) {
		debugLogger("goToMaintaincePage");
		//#29644 start- Remove cache after save value. add by naim misiran
		this.getManagedBean().setRemark(null);
		//#29644 End
		this.getManagedBean().setPmSpecialService(pmSpecialService);
		PmParameterDesc oldPmSpecialService = null;
		try {
			oldPmSpecialService = pmSpecialService;
			this.getManagedBean().setOldPmSpecialService(oldPmSpecialService);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return NAV_TO_MAINTAIN;
	}

	private void reloadSpecialServiceData(PmParameterDesc pmSpecialService) {
		debugLogger("goToMaintaincePage");

		this.getManagedBean().setPmSpecialService(pmSpecialService);
		PmParameterDesc oldPmSpecialService = null;
		try {

			oldPmSpecialService = pmSpecialService;
			this.getManagedBean().setOldPmSpecialService(oldPmSpecialService);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void validationMessage(PmParameterDesc pmSpecialService) {
		debugLogger("validationMessage");
		//#29644 start- Update validate message. add by naim misiran
		if (StringUtils.isBlank(pmSpecialService.getCodeName())) {
			throw new EpException(MessageConst.ERROR_770243,
			new String[] { PmCommonUtil.getResourceBundleLabels("lbl.pm.ss.name") });
		}
		
		//#29644 end
		if (DbValueConst.RECORD_STATUS_ACTIVE == pmSpecialService.getRecordStatusFlg()) {
			validateSpecialNameService(pmSpecialService);
		}
		
		//#29644 start- Update validate message. add by naim misiran
		if (!pmSpecialService.isBrandNew()) {
			if (StringUtils.isBlank(this.getManagedBean().getRemark())) {
			throw new EpException(MessageConst.ERROR_770243,
			new String[] { PmCommonUtil.getResourceBundleLabels("lbl.pm.ss.remark") });
			} 
		}
		//#29644 end
	}
	
	private void validationMessageRemark(PmRemarkSpecialService pmRemarkSS){
		debugLogger("validationMessage");
		if (StringUtils.isBlank(pmRemarkSS.getremark())) {
			throw new EpException(MessageConst.ERROR_770242,
					new String[] { PmCommonUtil.getResourceBundleLabels("lbl.pm.ss.remark") });
		}
	}

	public String saveSpecialServiceDtls() throws Exception {
		debugLogger("saveSpecialServiceDtls");
		PmParameterDesc specialService = this.getManagedBean().getPmSpecialService();
		ISpecialServiceSO specialserviceSO = null;
		String returnUrl = null;
		String remark = this.getManagedBean().getRemark();
		try {
			validationMessage(specialService);
			setUserDetails(specialService);

			specialserviceSO = EpEjbBusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
					ISpecialServiceSO.class);

			SpecialServiceReqDTO reqDTO = new SpecialServiceReqDTO();
			reqDTO.setSpecialService(specialService);
			reqDTO.setBrandNew(specialService.isBrandNew());
			reqDTO.setRemark(remark);
			reqDTO.setTrackDairyVOs(new ArrayList<PmTrackingDairyVO>());
			reqDTO.setTrackingStatus(true);

			SpecialServiceResDTO resDTO = specialserviceSO.saveOrUpdateSpecialServiceDtls(reqDTO);
			this.getManagedBean().setPmSpecialService(resDTO.getSpecialService());
			reloadSpecialServiceData(resDTO.getSpecialService());
			PmParameterDesc oldPmSpecialService = resDTO.getSpecialService();
			this.getManagedBean().setOldPmSpecialService(oldPmSpecialService);

			if (resDTO.isSuccessful()) {
				if (specialService.isBrandNew()) {
					returnUrl = NAV_TO_MAIN;
					PmCommonUtil.showFacesMsg(FacesContext.getCurrentInstance(), FacesMessage.SEVERITY_INFO,
							MessageConst.INFO_790100, new String[] { resDTO.getSpecialService().getCodeName() });
				} else {
					returnUrl = NAV_TO_MAINTAIN; //#29644 
					PmCommonUtil.showFacesMsg(FacesContext.getCurrentInstance(), FacesMessage.SEVERITY_INFO,
							MessageConst.INFO_790101, new String[] { resDTO.getSpecialService().getCodeName() });
				}
			}
		//#34445 Start
		} catch (EpException e) {
			handleException(e);
			returnUrl = NAV_TO_MAINTAIN;
		//#34445 End	
		} catch (Exception e) {
			throw (e);
		} finally {
			specialserviceSO = null;
		}
		return returnUrl;
	}

	private String getPmActionDesc(String messageId, String[] params) {
		Locale myLocale = new Locale("ms", "MY");
		return MessageUtils.getMessage(messageId, params, MessageUtils.BUNDLE_COMMON_MESSAGES, myLocale);
	}

	private boolean isExistSpecialService(String newSpecialServiceName) {
		ISpecialServiceSO specialserviceSO = null;
		boolean isValid = true;
		try {
			specialserviceSO = EpEjbBusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
					ISpecialServiceSO.class);
			SpecialServiceReqDTO reqDTO = new SpecialServiceReqDTO();
			ISearchBean<PmParameterDesc> searchBean = new SearchBean<PmParameterDesc>();
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("codeName", SearchOperator.OPERATOR_EQUALS.value(), newSpecialServiceName
							.toLowerCase().trim(), true));
			reqDTO.setSpecialServiceSearchBean(searchBean);

			SpecialServiceResDTO resDTO = specialserviceSO.retrieveSpecialServiceBySearch(reqDTO);

			if (resDTO.getSpecialServiceList() != null && !resDTO.getSpecialServiceList().isEmpty()) {
				isValid = false;
			}
		} finally {
			specialserviceSO = null;
		}

		return isValid;
	}

	public void deleteSpecialServiceList() {
		debugLogger("deleteSpecialServiceList");

	}

	public void preLoadSpecialService() {
		log.info("preLoadgroupSearch ....");
		renewSesionObject();
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null, NAV_TO_MAIN);
	}

	/**
	 * Sets the user details.
	 * 
	 * @param baseObject
	 *            (BaseEntity baseObject)
	 */
	private void setUserDetails(BaseEntity baseObject) {
		if (baseObject.isBrandNew()) {
			baseObject.setCreatedBy(1L);
			baseObject.setCreatedDate(new Timestamp(System.currentTimeMillis()));
		}
		baseObject.setChangedBy(1L);
	}

	private void validateSpecialNameService(PmParameterDesc pmSpecialService) {
		ISpecialServiceSO specialserviceSO = null;
		boolean isOriInactiveStatus = false;
		try {
			specialserviceSO = EpEjbBusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
					ISpecialServiceSO.class);
			SpecialServiceReqDTO reqDTO = new SpecialServiceReqDTO();
			SpecialServiceResDTO resDTO = null;
			ISearchBean<PmParameterDesc> searchBean = new SearchBean<PmParameterDesc>();

			if (!pmSpecialService.isBrandNew()) {
				reqDTO.setparameterDescId(pmSpecialService.getParameterDescId());
				resDTO = specialserviceSO.retrieveSpecialServiceById(reqDTO);

				if (resDTO.isSuccessful() && resDTO.getSpecialService() != null) {
					PmParameterDesc oriSpecialService = resDTO.getSpecialService();
					if (DbValueConst.RECORD_STATUS_SUSPENDED == oriSpecialService.getRecordStatusFlg()) {
						isOriInactiveStatus = true;
					}
				}
				searchBean.getDataFilterBeans().add(
						new SearchFilterBean("parameterDescId", SearchOperator.OPERATOR_NOT_EQUALS.value(),
								pmSpecialService.getParameterDescId()));
			}

			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("codeName", SearchOperator.OPERATOR_EQUALS.value(), pmSpecialService
							.getCodeName().toLowerCase().trim(), true));

			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("recordStatusFlg", SearchOperator.OPERATOR_EQUALS.value(),
							DbValueConst.RECORD_STATUS_ACTIVE));

			reqDTO = new SpecialServiceReqDTO();
			reqDTO.setSpecialServiceSearchBean(searchBean);
			resDTO = specialserviceSO.retrieveSpecialServiceBySearch(reqDTO);
		} finally {
			specialserviceSO = null;
		}
	}
} //#29644

package my.ep.web.managedbean.sm.ptjpopup;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ProfileManagementConst;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.logging.LogKey;
import my.ep.type.aut.SearchOperator;
import my.ep.util.SLAUtils;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;

/**
 * ManagedBean for Sm PTJ Popup
 * 
 * @author firdaus.omar
 */
@ManagedBean(name = "smPtjPopupMB")
@SessionScoped
public class SmPtjPopupManagedBean extends BaseManagedBean {

	private static final long serialVersionUID = 1L;
	private LogKey logKey = new LogKey();

	private Long ministryProfileId;
	private Long jabatanProfileId;
	private String ptjCodeNameKey;
	private String ptjCodeNameValue;
	private transient IPagingDataModel<PmOrgProfile> orgProfileDataModel = new OrgProfileDataModel();
	private boolean disabledSelect;
	private boolean disabledPtjGroup;
	private PmOrgProfile selectedPtj;

	public Long getMinistryProfileId() {
		return ministryProfileId;
	}

	public void setMinistryProfileId(Long ministryProfileId) {
		this.ministryProfileId = ministryProfileId;
	}

	public Long getJabatanProfileId() {
		return jabatanProfileId;
	}

	public void setJabatanProfileId(Long jabatanProfileId) {
		this.jabatanProfileId = jabatanProfileId;
	}

	public String getPtjCodeNameKey() {
		return ptjCodeNameKey;
	}

	public void setPtjCodeNameKey(String ptjCodeNameKey) {
		this.ptjCodeNameKey = ptjCodeNameKey;
	}

	public String getPtjCodeNameValue() {
		return ptjCodeNameValue;
	}

	public void setPtjCodeNameValue(String ptjCodeNameValue) {
		this.ptjCodeNameValue = ptjCodeNameValue;
	}

	private class OrgProfileDataModel extends PagingDataModel<PmOrgProfile> {

		private static final long serialVersionUID = 1L;

		public OrgProfileDataModel() {
			super("orgProfileDataTableId", false);
		}

		@Override
		public ISearchResultBean<PmOrgProfile> pageChangeListener(ISearchBean<PmOrgProfile> searchBean) {

			OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
			if (null != ministryProfileId) {
				reqDTO.setMinistryId(ministryProfileId);
			}
			if (null != jabatanProfileId) {
				reqDTO.setJabatanId(jabatanProfileId);
			}
			ISearchBean<PmOrgProfile> searchBeanData = getSearchBeanData(searchBean);
			reqDTO.setOrgProfileSearchBean(searchBeanData);

			try {
				IOrgProfileSO orgProfileSO = (IOrgProfileSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IOrgProfileSO.class);
				return orgProfileSO.retrievePtjListByParents(reqDTO).getOrgProfileResultBean();
			} catch (Exception e) {
				SLAUtils.getInstance().logSLAError(ModuleCodeConst.SUPPLIER_MANAGEMENT, "retrieve", null, "IOrgProfileSO", logKey.getUserId(), e);
				throw new EpException(MessageConst.ERROR_000001);
			}
		}

	}

	private ISearchBean<PmOrgProfile> getSearchBeanData(ISearchBean<PmOrgProfile> searchBean) {
		if (null != ptjCodeNameValue && !ptjCodeNameValue.isEmpty()) {
			if (ptjCodeNameKey.equalsIgnoreCase(ProfileManagementConst.PTJ_CODE)) {
				SearchFilterBean filterBean = new SearchFilterBean("pmOrgValidity.orgCode", SearchOperator.OPERATOR_LIKE.value(), ptjCodeNameValue);
				filterBean.setCaseInsensitive(true);
				searchBean.getDataFilterBeans().add(filterBean);
			} else if (ptjCodeNameKey.equalsIgnoreCase(ProfileManagementConst.PTJ_NAME)) {
				SearchFilterBean filterBean = new SearchFilterBean("pmOrgValidity.orgName", SearchOperator.OPERATOR_LIKE.value(), ptjCodeNameValue);
				filterBean.setCaseInsensitive(true);
				searchBean.getDataFilterBeans().add(filterBean);
			}
		}
		return searchBean;
	}

	public IPagingDataModel<PmOrgProfile> getOrgProfileDataModel() {
		return orgProfileDataModel;
	}

	public void setOrgProfileDataModel(IPagingDataModel<PmOrgProfile> orgProfileDataModel) {
		this.orgProfileDataModel = orgProfileDataModel;
	}

	public boolean isDisabledSelect() {
		return disabledSelect;
	}

	public void setDisabledSelect(boolean disabledSelect) {
		this.disabledSelect = disabledSelect;
	}

	public boolean isDisabledPtjGroup() {
		return disabledPtjGroup;
	}

	public void setDisabledPtjGroup(boolean disabledPtjGroup) {
		this.disabledPtjGroup = disabledPtjGroup;
	}

	public PmOrgProfile getSelectedPtj() {
		return selectedPtj;
	}

	public void setSelectedPtj(PmOrgProfile selectedPtj) {
		this.selectedPtj = selectedPtj;
	}

}
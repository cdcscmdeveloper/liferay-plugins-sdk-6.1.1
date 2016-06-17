package my.ep.web.backingbean.pm.organisation;

import javax.el.MethodExpression;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.ct.ContractConst;
import my.ep.constant.pm.ProfileManagementConst;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.type.aut.SearchOperator;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.pm.organisation.OrgProfileSearchManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.PmCommonUtil;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;

@ManagedBean(name = "organisationSearchBackingBean")
@RequestScoped
public class OrgProfileSearchBackingBean extends
		BaseBackingBean<OrgProfileSearchManagedBean> {
	private static Logger log = Logger
			.getLogger(OrgProfileSearchBackingBean.class);

	public SelectItem[] getMinistryDropdown() {
		return PmCommonUtil.getMinistryDropdown();
	}

	public SelectItem[] getJabatanDropdown() {
		return PmCommonUtil.getJabatanDropdown(getManagedBean()
				.getMinistryProfileId());
	}

	private class OrgProfileDataModel extends PagingDataModel<PmOrgProfile> {
		private static final long serialVersionUID = 1L;

		public OrgProfileDataModel() {
			super("dtOrgProfile",false);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * my.ep.web.bean.paginator.PagingDataModel#pageChangeListener(my.ep
		 * .biz.bean.search.ISearchBean)
		 */
		@Override
		public ISearchResultBean<PmOrgProfile> pageChangeListener(
				ISearchBean<PmOrgProfile> searchBean) {
			try {
				OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
				if(getManagedBean().getMinistryProfileId() != null){
					reqDTO.setMinistryId(getManagedBean().getMinistryProfileId());
				}
				
				if(getManagedBean().getJabatanProfileId() != null){
					reqDTO.setJabatanId(getManagedBean().getJabatanProfileId());
				}
				
				if (StringUtils.isNotBlank(getManagedBean().getPtjCodeName())
						&& (StringUtils.isNotBlank(getManagedBean().getPtjCodeNameSearchValues()))) {
					if (getManagedBean().getPtjCodeName().equalsIgnoreCase(
							ProfileManagementConst.PTJ_CODE)) {
						SearchFilterBean filterBean = new SearchFilterBean(
								"pmOrgValidity.orgCode",
								SearchOperator.OPERATOR_LIKE.value(),
								getManagedBean().getPtjCodeNameSearchValues());
						filterBean.setCaseInsensitive(true);
						searchBean.getDataFilterBeans().add(filterBean);

					} else if (getManagedBean().getPtjCodeName()
							.equalsIgnoreCase(ProfileManagementConst.PTJ_NAME)) {
						SearchFilterBean filterBean = new SearchFilterBean(
								"pmOrgValidity.orgName",
								SearchOperator.OPERATOR_LIKE.value(),
								getManagedBean().getPtjCodeNameSearchValues());
						filterBean.setCaseInsensitive(true);
						searchBean.getDataFilterBeans().add(filterBean);
					}
				}

				IOrgProfileSO orgProfileSO = (IOrgProfileSO) BusinessDelegate
						.getInstance().getServiceObject(
								ModuleCodeConst.PROFILE_MANAGEMENT,
								IOrgProfileSO.class);
				
				reqDTO.setOrgProfileSearchBean(searchBean);
				OrgProfileResDTO resDTO = orgProfileSO.retrievePtjListByParents(reqDTO);
				if (resDTO.isSuccessful()) {
					return resDTO.getOrgProfileResultBean();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
	}
	
	public void searchOrgProfiles(){
		getOrgProfileDataModel().reloadData();
	}
	
	public void resetOrgProfiles(){
		getOrgProfileDataModel().resetData();
	}

	public IPagingDataModel<PmOrgProfile> getOrgProfileDataModel() {
		IPagingDataModel<PmOrgProfile> orgProfileDataModel = getManagedBean().getOrgProfileDataModel();
		
		if (orgProfileDataModel == null) {
			orgProfileDataModel = new OrgProfileDataModel();
			getManagedBean().setOrgProfileDataModel(orgProfileDataModel);
		}
		
		return orgProfileDataModel;
	}
	
	public void okAction(String methodActionStr) {
		log.info("okAction");
		
		if (StringUtils.isNotBlank(methodActionStr)) {
			MethodExpression methodExp = FacesScopes
	                .createMethodExpression(FacesContext.getCurrentInstance(),
	                		methodActionStr, null,
	                        new Class[] { PmOrgProfile.class });
			
			methodExp.invoke(
					FacesContext.getCurrentInstance().getELContext(), 
					new Object[]{getManagedBean().getSelectedPtj()});
			
			RequestContext.getCurrentInstance().execute("ptjPopup.hide();");
		}
	}
	
}

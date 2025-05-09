package my.ep.web.backingbean.sc.loa;

import java.util.List;
import java.util.Locale;

import javax.faces.model.SelectItem;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.constant.MessageConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.exception.EpException;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.sc.loaloi.ScLoa;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.OrganizationType;
import my.ep.type.pm.RoleCode;
import my.ep.util.ResourceBundleUtils;
import my.ep.util.pm.OrgProfileUtil;
import my.ep.web.backingbean.sc.quatationtender.SourcingBaseBackingBean;
import my.ep.web.constant.sc.QTConstants;	// Bug #35227@Syazani 20/10/2022
import my.ep.web.managedbean.sc.loa.LOABaseManagedBean;
import my.ep.web.util.EpPortletUtil;	// Bug #35227@Syazani 20/10/2022
import my.ep.web.util.FacesScopes;

import org.apache.commons.lang.StringUtils;
import org.primefaces.context.RequestContext;

public abstract class LOABaseBackingBean<T extends LOABaseManagedBean> extends SourcingBaseBackingBean<T> {
	private static final long serialVersionUID = 1L;

	protected void viewContractDetails(ScLoa scLoa) {
		initDefaultSelectedOrgType();
		this.getManagedBean().setSelectedPtj(scLoa.getContractAgency());
	}

	public void initDefaultSelectedOrgType() {
		PmParameter pmParameter = retrieveParameterByTypeAndCode(ParameterTypeConst.ORGANISATION_TYPES,
				ParameterConst.ORG_TYPE_PTJ);
		this.getManagedBean().setSelectedOrgType(pmParameter);
	}

	public void updateContractAdmin() {
		Long userId = this.getManagedBean().getScLoa().getContractAdmin().getUserId();
		PmUser pmUser = new PmUser();
		if (userId != null) {
			pmUser = retrivePmUser(userId);
		}
		this.getManagedBean().getScLoa().setContractAdmin(pmUser);
	}

	public void userListener(PmOrgProfile orgProfile) {
		if (orgProfile != null) {
			this.getManagedBean().setSelectedPtj(orgProfile);
			RequestContext.getCurrentInstance().update("usergrouping");
		}
	}

	// #26475 - start
	public List<PmUser> getUsersByAgency() {
		ISearchBean<PmUser> searchBean = contractAdminSearchBean();

		ISearchResultBean<PmUser> resultBean = getUserSOGlobal().retrieveMinistryUserList(searchBean);
		List<PmUser> pmUsers = resultBean.getResultList();

		return pmUsers;
	}
	// #26475 - end

	protected ISearchBean<PmUser> contractAdminSearchBean() {
		ISearchBean<PmUser> searchBean = new SearchBean<PmUser>();

		PmOrgProfile pmOrgProfile = contractAdminPmOrgProfile();
		String roleCode = contractAdminRoleCode();
		
		//#24540 - start -
		if (null != pmOrgProfile.getPmOrgValidity()) {
		//#24540 - end -	
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("pmUserOrgs.pmOrgProfile.pmOrgValidity.orgCode", SearchOperator.OPERATOR_LIKE
							.value(), pmOrgProfile.getPmOrgValidity().getOrgCode()));
	
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("pmUserOrgs.pmOrgProfile."
							+ OrgProfileUtil.getSearchBeanParentString(OrganizationType.MINISTRY.value()),
							SearchOperator.OPERATOR_EQUALS.value(), retrieveMinistryOrgProfile(
									pmOrgProfile.getOrgProfileId()).getOrgProfileId()));
		//#24540 - start -
		}
		//#24540 - end -
		
		if (roleCode != null) {
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("pmUserOrgs.pmUserRoles.pmRole.roleCode", SearchOperator.OPERATOR_EQUALS
							.value(), roleCode));
		}

		return searchBean;
	}

	private PmOrgProfile contractAdminPmOrgProfile() {
		ScLoa scLoa = getManagedBean().getScLoa();
		PmOrgProfile pmOrgProfile;
		if (this.getManagedBean().getSelectedPtj() != null) {
			pmOrgProfile = this.getManagedBean().getSelectedPtj();
		} else if (scLoa.getContractAgency().getOrgProfileId() != null) {
			pmOrgProfile = scLoa.getContractAgency();
		} else {
			pmOrgProfile = scLoa.getScLoiLoa().getPmOrgProfile();
		}
		return pmOrgProfile;
	}

	private String contractAdminRoleCode() {
		String contractType = getManagedBean().getScLoa().getContractType().getParameterCode();
		String roleCode = null;

		if (ParameterConst.CONTRACT_TYPE_MINISTRY.equals(contractType)) {
			roleCode = RoleCode.MIN_CONTRACT_ADMIN.value();
		} else if (ParameterConst.CONTRACT_TYPE_CENTRAL.equals(contractType)) {
			roleCode = RoleCode.BPK_CCA.value();
		}
		return roleCode;
	}

	protected void updateContractDetails(ScLoa scLoa) {
		Locale locale = new FacesScopes().getUserLocale();

		if (StringUtils.isNotBlank(this.getManagedBean().getSelectedOrgType().getParameterCode())) {
			if (this.getManagedBean().getSelectedPtj() == null) {
				throw new EpException(MessageConst.ERROR_760400, new String[] { ResourceBundleUtils.getMessage(
						"lbl.sc.loiloa.loa.agency", ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale) });
			} else {
				scLoa.setContractAgency(this.getManagedBean().getSelectedPtj());
			}
		}
	}
	
	//#24540 - start -
	/**
	 * Set Performance Bond value opted by user.
	 * @param bondReqVal
	 */
	public void performanceBondChanged(String bondReqVal) {
		if (getManagedBean().getYesParameterId().equalsIgnoreCase(bondReqVal)) { 
			getManagedBean().getScLoa().setBondReq(true);
		} else {
			getManagedBean().getScLoa().setBondReq(false);
		}
		calcBondAmountWithSST(getManagedBean().getScLoa(), isDirectLOA()); //Bug #35227@Syazani 20/10/2022
	}
	
	/**
	 * Set Performance Bond value opted by user.
	 * @param bondReqVal
	 */
	public void agreementChanged(String agreeReqVal) {
		if (getManagedBean().getYesParameterId().equalsIgnoreCase(agreeReqVal)) { 
			getManagedBean().getScLoa().setAgreementReq(true);
		} else {
			getManagedBean().getScLoa().setAgreementReq(false);
		}
	}
	//#24540 - end -
	
	// Bug #35227@Syazani 20/10/2022 -- Start
	public boolean isDirectLOA() {
		return QTConstants.PORTLET_NAME_DL_CREATE.equalsIgnoreCase(EpPortletUtil.getDisplayPortletId());
	}
	// Bug #35227@Syazani 20/10/2022 -- End
	
	// #26770 - Start.
	/**
	 * @param scQt
	 * @return
	 */
	protected boolean isQuotationOrTender(ScQt scQt) {
		boolean isQuotation = true;
		PmParameter procurementMode = scQt.getProcurementMode();

		if (procurementMode != null
				&& procurementMode.getParameterCode().equals(ParameterConst.PROCUREMENT_MODE_QUOTATION)) {
			isQuotation = true;
		} else if (procurementMode != null
				&& procurementMode.getParameterCode().equals(ParameterConst.PROCUREMENT_MODE_TENDER)) {
			isQuotation = false;
		}

		return isQuotation;
	}
	// #26770 - End.
	
}

package my.ep.web.backingbean.sc.loa;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.faces.model.SelectItem;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.constant.MessageConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.exception.EpException;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.sc.loaloi.ScLoa;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.OrganizationType;
import my.ep.type.pm.RoleCode;
import my.ep.util.ResourceBundleUtils;
import my.ep.util.pm.OrgProfileUtil;
import my.ep.web.backingbean.sc.quatationtender.SourcingBaseBackingBean;
import my.ep.web.managedbean.sc.loa.LOABaseManagedBean;
import my.ep.web.util.FacesScopes;

import org.apache.commons.lang.StringUtils;
import org.primefaces.context.RequestContext;

public abstract class LOABaseBackingBean<T extends LOABaseManagedBean> extends SourcingBaseBackingBean<T> {
	private static final long serialVersionUID = 1L;

	public SelectItem[] getDropdownValuesByPtjAndKptj(String parameterType) {
		SelectItem[] items = getDropdownValuesBySearchBean(parameterType, searchBeanByPtjAndKptj());

		return items;
	}

	private ISearchBean<PmParameterDesc> searchBeanByPtjAndKptj() {
		ISearchBean<PmParameterDesc> searchBean = new SearchBean<PmParameterDesc>();

		searchBean.getDataFilterBeans().add(
				new SearchFilterBean("pmParameter.parameterCode", SearchOperator.OPERATOR_IN.value(),
						organizationTypes()));
		return searchBean;
	}

	private List<String> organizationTypes() {
		List<String> organizationTypes = new ArrayList<String>();
		organizationTypes.add(ParameterConst.ORG_TYPE_JABATAN);
		organizationTypes.add(ParameterConst.ORG_TYPE_PTJ);
		return organizationTypes;
	}

	protected void viewOrgTypeId(ScLoa scLoa) {
		if (scLoa.getOrganizationType() != null) {
			this.getManagedBean().setOrgTypeId(scLoa.getOrganizationType().getParameterId());
		}
	}

	protected void viewContractDetails(ScLoa scLoa) {
		this.getManagedBean().setSelectedOrgType(scLoa.getOrganizationType());
		this.getManagedBean().setSelectedPtj(scLoa.getContractAgency());
		viewOrgTypeId(scLoa);
	}

	public void clearOrgNameSearchFields() {
		log.info("clearOrgNameSearchFields");
		if (this.getManagedBean().getOrgTypeId() != null) {
			PmParameter pmParameter = retrieveParameterById(this.getManagedBean().getOrgTypeId());
			this.getManagedBean().setSelectedOrgType(pmParameter);
			updateSelectedPtj(pmParameter);
		} else {
			this.getManagedBean().setSelectedOrgType(null);
			this.getManagedBean().setSelectedPtj(null);
		}
	}

	public void updateSelectedPtj(PmParameter pmParameter) {
		ScLoa scLoa = this.getManagedBean().getScLoa();
		PmParameter orgType = scLoa.getOrganizationType();
		if (orgType != null && (pmParameter.getParameterId().longValue() == orgType.getParameterId().longValue())) {
			this.getManagedBean().setSelectedPtj(scLoa.getContractAgency());
		} else {
			this.getManagedBean().setSelectedPtj(null);
		}
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

	public SelectItem[] getUsersByAgency() {
		ISearchBean<PmUser> searchBean = contractAdminSearchBean();

		ISearchResultBean<PmUser> resultBean = getUserSO().retrieveMinistryUserList(searchBean);
		List<PmUser> pmUsers = resultBean.getResultList();
		log.info("Number of Users = " + pmUsers.size());

		SelectItem[] items = getDropdownUsers(pmUsers);
		return items;
	}

	protected ISearchBean<PmUser> contractAdminSearchBean() {
		ISearchBean<PmUser> searchBean = new SearchBean<PmUser>();

		PmOrgProfile pmOrgProfile = contractAdminPmOrgProfile();
		String roleCode = contractAdminRoleCode();

		searchBean.getDataFilterBeans().add(
				new SearchFilterBean("pmUserOrgs.pmOrgProfile.pmOrgValidity.orgCode", SearchOperator.OPERATOR_LIKE
						.value(), pmOrgProfile.getPmOrgValidity().getOrgCode()));

		searchBean.getDataFilterBeans().add(
				new SearchFilterBean("pmUserOrgs.pmOrgProfile."
						+ OrgProfileUtil.getSearchBeanParentString(OrganizationType.MINISTRY.value()),
						SearchOperator.OPERATOR_EQUALS.value(), retrieveMinistryOrgProfile(
								pmOrgProfile.getOrgProfileId()).getOrgProfileId()));

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
		if (this.getManagedBean().getOrgTypeId() == null) {
			throw new EpException(MessageConst.ERROR_760400, new String[] { ResourceBundleUtils.getMessage(
					"lbl.pm.organisation.orgType", ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale) });
		}

		if (StringUtils.isNotBlank(this.getManagedBean().getSelectedOrgType().getParameterCode())) {
			if (this.getManagedBean().getSelectedPtj() == null) {
				throw new EpException(MessageConst.ERROR_760400, new String[] { ResourceBundleUtils.getMessage(
						"lbl.sc.loiloa.loa.agency", ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale) });
			} else {
				scLoa.setContractAgency(this.getManagedBean().getSelectedPtj());
				scLoa.setOrganizationType(this.getManagedBean().getSelectedOrgType());
			}
		}
	}
}

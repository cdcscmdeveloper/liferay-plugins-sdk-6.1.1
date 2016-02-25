package my.ep.web.backingbean.pm.profilemanagementsearch;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.persistence.entity.BaseEntity;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.OrganizationType;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.pm.accessappl.AccessApplBackingBean;
import my.ep.web.managedbean.pm.accessappl.AccessApplManagedBean;
import my.ep.web.managedbean.pm.profilemanagementsearch.ProfileManagementSearchManagedBean;

import org.apache.log4j.Logger;

/**
 * The backing bean for Profile Management Search
 * 
 * @author Zeinab
 * @since 24.02.2016
 */
@ManagedBean(name = "profileManagSearchBackingBean")
@ViewScoped
public class ProfileManagementSearchBackingBean extends
BaseBackingBean<ProfileManagementSearchManagedBean>{

/** The log. */
private static Logger log = Logger
		.getLogger(ProfileManagementSearchBackingBean.class);

/**
 * The method used for generating a list of available display type of org
 * type as selection.
 * 
 */
public SelectItem[] getOrgTypeDropdownValues() {
	SelectItem[] items = null;
	if(getManagedBean().getOrgTypeMap().get("somAccessApplOrgType") == null){
		ISearchBean<PmParameterDesc> searchBean = super.createSearchBean(
				PmParameterDesc.class, "somAccessApplOrgType");
		
		List<String> recommendList = new ArrayList<String>();
		recommendList.add(OrganizationType.STATE_GOVERNMENT.value());
		recommendList.add(OrganizationType.GOVERNMENT_LINK_COMPANIES.value());
		recommendList.add(OrganizationType.STATUTORY_BODIES.value());
		recommendList.add(OrganizationType.STATUTORY_BODIES_STATE.value());
		recommendList.add(OrganizationType.LOCAL_AUTHORITY.value());


		searchBean.getDataFilterBeans().add(
				new SearchFilterBean("pmParameter.parameterCode",
						SearchOperator.OPERATOR_IN.value(), recommendList));
		if(searchBean != null){
			searchBean.getSortColumns();
			items = getDropdownValuesBySearchBean(
					ParameterTypeConst.ORGANISATION_TYPES, searchBean);
		}			
		getManagedBean().getOrgTypeMap().put("somAccessApplOrgType", items);
	}else{
		items = getManagedBean().getOrgTypeMap().get("somAccessApplOrgType");
	}
	
	return items;
}
public String constructConditionFields() {
	StringBuilder sb = new StringBuilder();
	sb.append("orgTypeCode.parameterId^");
	sb.append(getManagedBean().getSelectedOrgType());
	sb.append("|recordStatus^1");
	return sb.toString();
}

public void changeOrgUnit(BaseEntity entity) {
	log.info("changeOrgUnit");
}

public void changeOrgType() {
	log.info("changeResOrgType");

	if (this.getManagedBean().getSelectedOrgProfile() != null) {
		getManagedBean().setSelectedOrgProfile(null);
	}

}

}

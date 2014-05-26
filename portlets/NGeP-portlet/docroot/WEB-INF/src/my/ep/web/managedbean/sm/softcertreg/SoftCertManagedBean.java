package my.ep.web.managedbean.sm.softcertreg;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import com.liferay.portal.model.Role;

import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmCountry;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.sm.SmAddress;
import my.ep.persistence.entity.sm.SmApplSectionReview;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmPersonnelAddress;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.util.FacesScopes;

@ManagedBean(name = "softCertMB")
@SessionScoped
public class SoftCertManagedBean extends BaseManagedBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6397086927187535118L;

	private List<SmPersonnel> personnelList;
	
	private SmPersonnel loginUserPersonnelInfo;

	private String loginRoleName;
	
	public SoftCertManagedBean(){
		FacesScopes sc = new FacesScopes();
		List<Role> loginUserRoles = sc.getUserRoleList();
		List<String> roleNames = new ArrayList<String>();
		for (Role r : loginUserRoles) {
			System.out.println(" Role ===  >> " + r.getName());
			roleNames.add(r.getName());
		}
		if (sc.hasRoleUser(SupplierManagementConst.MOF_SUPPLIER_ADMIN)) {
			setLoginRoleName(SupplierManagementConst.MOF_SUPPLIER_ADMIN);
			System.out.println("  *** LOGGED IN USER IS MOF SUPPLIER ADMIN *** ");			
		} else {
			setLoginRoleName(SupplierManagementConst.BASIC_SUPPLIER_ADMIN);			
			System.out.println("  *** LOGGED IN USER IS BASIC SUPPLIER ADMIN *** ");
		}
	}
	
	public List<SmPersonnel> getPersonnelList() {
		return personnelList;
	}

	public void setPersonnelList(List<SmPersonnel> personnelList) {
		this.personnelList = personnelList;
	}

	public SmPersonnel getLoginUserPersonnelInfo() {
		return loginUserPersonnelInfo;
	}

	public void setLoginUserPersonnelInfo(SmPersonnel loginUserPersonnelInfo) {
		this.loginUserPersonnelInfo = loginUserPersonnelInfo;
	}

	public String getLoginRoleName() {
		return loginRoleName;
	}

	public void setLoginRoleName(String loginRoleName) {
		this.loginRoleName = loginRoleName;
	}

}

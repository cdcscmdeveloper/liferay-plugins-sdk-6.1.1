package my.ep.web.managedbean.sm.softcertreg;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.sm.personnel.IPersonnelSO;
import my.ep.bpm.vo.WorklistItemVO;
import my.ep.dto.sm.personnel.PersonnelReqDTO;
import my.ep.dto.sm.personnel.PersonnelResDTO;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.type.aut.SearchOperator;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.commons.lang3.StringUtils;

import com.liferay.portal.model.Role;

@ManagedBean(name = "softCertMB")
@SessionScoped
public class SoftCertManagedBean extends BaseManagedBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6397086927187535118L;

	private List<SmPersonnel> personnelList;
	private List<SmPersonnel> softCertSelectList;
	private List<SmPersonnel> softCertPayList;
	private HashMap<Long, BigDecimal> paymentMap = new HashMap<Long, BigDecimal>();
	private BigDecimal totalPaymentAmount;
	private SmPersonnel loginUserPersonnelInfo;
	private String loginRoleName;
	private String taskName;
	private String token;
	private WorklistItemVO softCertBpmTask;
	private List<SmPersonnel> authorizedPersonnels;

	public SoftCertManagedBean() {
		super();
		FacesScopes sc = new FacesScopes();
		List<Role> loginUserRoles = sc.getUserRoleList();
		List<String> roleNames = new ArrayList<String>();
		for (Role r : loginUserRoles) {
			System.out.println(" Role ===  >> " + r.getName());
			roleNames.add(r.getName());
		}
		if (sc.hasRoleUser(SupplierManagementConst.MOF_SUPPLIER_ADMIN)) {
			setLoginRoleName(SupplierManagementConst.MOF_SUPPLIER_ADMIN);
			System.out
					.println("  *** LOGGED IN USER IS MOF SUPPLIER ADMIN *** ");
		} else {
			setLoginRoleName(SupplierManagementConst.BASIC_SUPPLIER_ADMIN);
			System.out
					.println("  *** LOGGED IN USER IS BASIC SUPPLIER ADMIN *** ");
		}
	}

	public List<SmPersonnel> getPersonnelList() {
		return personnelList;
	}

	public void setPersonnelList(List<SmPersonnel> personnelList) {
		this.personnelList = personnelList;
	}

	public List<SmPersonnel> getSoftCertSelectList() {
		return softCertSelectList;
	}

	public void setSoftCertSelectList(List<SmPersonnel> softCertSelectList) {
		this.softCertSelectList = softCertSelectList;
	}

	public List<SmPersonnel> getSoftCertPayList() {
		return softCertPayList;
	}

	public void setSoftCertPayList(List<SmPersonnel> softCertPayList) {
		this.softCertPayList = softCertPayList;
	}

	public HashMap<Long, BigDecimal> getPaymentMap() {
		return paymentMap;
	}

	public void setPaymentMap(HashMap<Long, BigDecimal> paymentMap) {
		this.paymentMap = paymentMap;
	}

	public BigDecimal getTotalPaymentAmount() {
		if (totalPaymentAmount == null)
			totalPaymentAmount = BigDecimal.ZERO;
		return totalPaymentAmount;
	}

	public void setTotalPaymentAmount(BigDecimal totalPaymentAmount) {
		this.totalPaymentAmount = totalPaymentAmount;
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

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public WorklistItemVO getSoftCertBpmTask() {
		return softCertBpmTask;
	}

	public void setSoftCertBpmTask(WorklistItemVO softCertBpmTask) {
		this.softCertBpmTask = softCertBpmTask;
	}

	public List<SmPersonnel> getAuthorizedPersonnels() {
		return authorizedPersonnels;
	}

	public void setAuthorizedPersonnels(List<SmPersonnel> authorizedPersonnels) {
		this.authorizedPersonnels = authorizedPersonnels;
	}

}

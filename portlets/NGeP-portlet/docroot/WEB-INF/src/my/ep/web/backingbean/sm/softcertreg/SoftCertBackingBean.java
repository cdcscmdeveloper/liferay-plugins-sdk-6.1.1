package my.ep.web.backingbean.sm.softcertreg;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import my.ep.biz.service.pm.common.city.ICitySO;
import my.ep.biz.service.pm.common.country.ICountrySO;
import my.ep.biz.service.pm.common.district.IDistrictSO;
import my.ep.biz.service.pm.common.state.IStateSO;
import my.ep.biz.service.sm.personnel.IPersonnelSO;
import my.ep.biz.service.sm.registration.IRegistrationSO;
import my.ep.dto.pm.common.city.CityReqDTO;
import my.ep.dto.pm.common.city.CityResDTO;
import my.ep.dto.pm.common.country.CountryReqDTO;
import my.ep.dto.pm.common.country.CountryResDTO;
import my.ep.dto.pm.common.district.DistrictReqDTO;
import my.ep.dto.pm.common.district.DistrictResDTO;
import my.ep.dto.pm.common.state.StateReqDTO;
import my.ep.dto.pm.common.state.StateResDTO;
import my.ep.dto.sm.personnel.PersonnelReqDTO;
import my.ep.dto.sm.personnel.PersonnelResDTO;
import my.ep.dto.sm.registration.RegistrationReqDTO;
import my.ep.dto.sm.registration.RegistrationResDTO;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmCountry;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.sm.SmAddress;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmPersonnelAddress;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.sm.softcertreg.SoftCertManagedBean;
import my.ep.web.managedbean.sm.userprofilemaintenance.SmPersonnelManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;

@ManagedBean(name="softCertBackingBean")
@ViewScoped
public class SoftCertBackingBean extends BaseBackingBean<SoftCertManagedBean>{

	public SoftCertBackingBean(){

	}

	@PostConstruct
	public void init() {
		
		FacesScopes facesScopes = new FacesScopes();
		PmUser usr = facesScopes.getUserSessionBean();
		getPersonnelByPmUser(usr);
		searchPersonnel();
	}

	public String cancel(){

		return "softCertUserSearch.xhtml";

	}
	
	public void searchPersonnel(){

		IPersonnelSO smPersonnelSO = (IPersonnelSO) BusinessDelegate
				.getInstance().getServiceObject("sm", IPersonnelSO.class);
		PersonnelReqDTO reqDTO = new PersonnelReqDTO();
		
//		reqDTO.setSmSupplierId(new Long(1));
		
		reqDTO.setSmSupplierId(this.getManagedBean().getLoginUserPersonnelInfo().getSmAppl().getSmSupplier().getSupplierId());
		
//		System.out.println("\nP Size 1:"+this.getManagedBean().getLoginUserPersonnelInfo().getSmAppl().getSmPersonnels().size());
		
		PersonnelResDTO resDTO = new PersonnelResDTO(); 
		
		if(this.getManagedBean().getLoginRoleName().equalsIgnoreCase(SupplierManagementConst.MOF_SUPPLIER_ADMIN))
			resDTO = smPersonnelSO.retrieveAuthorizedPersonnelSoftCert(reqDTO);
		else
			resDTO = smPersonnelSO.retrievePersonnelBySupplierIdForSoftCert(reqDTO);
		
		
		
		List<SmPersonnel> smPersonnelList = resDTO.getSmPersonnelList();
		this.getManagedBean().setPersonnelList(smPersonnelList);
		
	}
	
	public void getPersonnelByPmUser(PmUser usr){
		
		IPersonnelSO smPersonnelSO = (IPersonnelSO) BusinessDelegate
				.getInstance().getServiceObject("sm", IPersonnelSO.class);
		PersonnelReqDTO reqDTO = new PersonnelReqDTO();
		
		reqDTO.setUserId(Long.valueOf(usr.getUserId()));

		PersonnelResDTO resDTO = smPersonnelSO.retrievePersonnelByUserId(reqDTO);
		
		SmPersonnel smPersonnel = resDTO.getSmPersonnel();
		
		this.getManagedBean().setLoginUserPersonnelInfo(smPersonnel);
		
	}
	
	public String navigateToPaymentPage(){
		
		this.getManagedBean().setSoftCertPayList(this.getManagedBean().getSoftCertSelectList());
		updateAmount();
		return "softCertPaymentPage.xhtml";
	}
	
	public void updateAmount(){
		
		BigDecimal amount = BigDecimal.ZERO;
		for(SmPersonnel personnel : this.getManagedBean().getSoftCertPayList()){
			this.getManagedBean().getPaymentMap().put(personnel.getPersonnelId(), BigDecimal.valueOf(150));
			amount = amount.add(BigDecimal.valueOf(150));
		}
		this.getManagedBean().setTotalPaymentAmount(amount);
	}
	
}

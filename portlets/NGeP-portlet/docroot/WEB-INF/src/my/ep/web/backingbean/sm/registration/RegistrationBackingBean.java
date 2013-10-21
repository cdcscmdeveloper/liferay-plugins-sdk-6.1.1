package my.ep.web.backingbean.sm.registration;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.sm.registration.RegistrationManagedBean;

@ManagedBean(name = "registrationBackingBean")
@RequestScoped
public class RegistrationBackingBean extends
		BaseBackingBean<RegistrationManagedBean> {}
/*
 * public String getRegistrationType(){ if(this.getManagedBean().getRegType() ==
 * "Basic"){
 * 
 * }else if(this.getManagedBean().getRegType() == "MOF"){
 * 
 * }
 * 
 * return ""; }
 */


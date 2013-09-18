package my.ep.web.managedbean.ct.agreement;


import javax.el.ELContext;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.managedbean.ct.ContractDetailsManagedBean;


@ManagedBean
@SessionScoped
public class AgreementManagedBean extends BaseManagedBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String agreement;
   
	
	public String getAgreement() {
		return agreement;
	}


	public void setAgreement(String agreement) {
		this.agreement = agreement;
	}


	public AgreementManagedBean(){
	 
	  String str="";	
	  ELContext elContext = FacesContext.getCurrentInstance().getELContext();
	  ContractDetailsManagedBean lBean = ( ContractDetailsManagedBean) FacesContext.getCurrentInstance().getApplication().getELResolver().getValue(elContext, null, "contractDetailsManagedBean");
	  str =lBean.getViewAgreement();
	  System.out.println("==========1024=============="+str);
	}	
}

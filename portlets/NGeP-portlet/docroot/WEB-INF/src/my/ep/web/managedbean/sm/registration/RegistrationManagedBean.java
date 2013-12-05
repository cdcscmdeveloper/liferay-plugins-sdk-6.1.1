package my.ep.web.managedbean.sm.registration;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.sm.SmAddress;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.persistence.entity.sm.SmBusinessNature;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmPersonnelAddress;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.sm.SmSupplierBank;
import my.ep.persistence.entity.sm.SmSupplierCert;
import my.ep.persistence.entity.sm.SmSupplierOwner;
import my.ep.web.managedbean.BaseManagedBean;

@ManagedBean(name = "registrationMB")
@SessionScoped
public class RegistrationManagedBean extends BaseManagedBean {
	private static final long serialVersionUID = 1L;

	private SmSupplier smSupp;
	private SmSupplierBank smSuppBank;
	private SmBusinessNature smBusNature;
	private SmPersonnel smPersonnel;
	private SmSupplierOwner smSuppOwner;
	private SmPersonnelAddress smPersonnelAdd;
	private SmAppl smAppl;
	private List<SmSupplierCert> smSuppCerts;

	private SmAddress smBusinessAdd;
	private SmAddress smRegAdd;
	private SmAddress smResAdd;
	
	private String applType;

	public SmSupplier getSmSupp() {
		return smSupp;
	}

	public void setSmSupp(SmSupplier smSupp) {
		this.smSupp = smSupp;
	}

	public SmSupplierBank getSmSuppBank() {
		return smSuppBank;
	}

	public void setSmSuppBank(SmSupplierBank smSuppBank) {
		this.smSuppBank = smSuppBank;
	}

	public SmBusinessNature getSmBusNature() {
		return smBusNature;
	}

	public void setSmBusNature(SmBusinessNature smBusNature) {
		this.smBusNature = smBusNature;
	}

	public SmPersonnel getSmPersonnel() {
		return smPersonnel;
	}

	public void setSmPersonnel(SmPersonnel smPersonnel) {
		this.smPersonnel = smPersonnel;
	}

	public SmSupplierOwner getSmSuppOwner() {
		return smSuppOwner;
	}

	public void setSmSuppOwner(SmSupplierOwner smSuppOwner) {
		this.smSuppOwner = smSuppOwner;
	}

	public List<SmSupplierCert> getSmSuppCerts() {
		return smSuppCerts;
	}

	public void setSmSuppCerts(List<SmSupplierCert> smSuppCerts) {
		this.smSuppCerts = smSuppCerts;
	}

	public SmPersonnelAddress getSmPersonnelAdd() {
		return smPersonnelAdd;
	}

	public void setSmPersonnelAdd(SmPersonnelAddress smPersonnelAdd) {
		this.smPersonnelAdd = smPersonnelAdd;
	}

	public SmAddress getSmBusinessAdd() {
		return smBusinessAdd;
	}

	public void setSmBusinessAdd(SmAddress smBusinessAdd) {
		this.smBusinessAdd = smBusinessAdd;
	}

	public SmAddress getSmRegAdd() {
		return smRegAdd;
	}

	public void setSmRegAdd(SmAddress smRegAdd) {
		this.smRegAdd = smRegAdd;
	}

	public SmAddress getSmResAdd() {
		return smResAdd;
	}

	public void setSmResAdd(SmAddress smResAdd) {
		this.smResAdd = smResAdd;
	}

	public SmAppl getSmAppl() {
		return smAppl;
	}

	public void setSmAppl(SmAppl smAppl) {
		this.smAppl = smAppl;
	}

	public String getApplType() {
		return applType;
	}

	public void setApplType(String applType) {
		this.applType = applType;
	}

}

package my.ep.web.managedbean.sm.updatesmeinfo;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import my.ep.persistence.entity.sm.SmCompanyBasic;
import my.ep.persistence.entity.sm.SmSmeSupplier;
import my.ep.persistence.entity.sm.SmSmeSupplierDoc;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.web.managedbean.BaseManagedBean;

@ManagedBean(name = "smeInfoMB")
@SessionScoped
public class SmUpdateSmeInfoManagedBean extends BaseManagedBean {

	private static final long serialVersionUID = 1L;
	private boolean agreeTC;
	private SmSupplier smSupplier;
	private SmCompanyBasic companyBasic;
	private SmSmeSupplier smeSupplier;
	private String smeCorpRegisteredTrans;
	private SmSmeSupplierDoc smeSupplierDoc;
	private List<SelectItem> statusTypeList;
	private List<SmSmeSupplier> supplierSmeList;

	public boolean isAgreeTC() {
		return agreeTC;
	}

	public void setAgreeTC(boolean agreeTC) {
		this.agreeTC = agreeTC;
	}

	public SmSupplier getSmSupplier() {
		return smSupplier;
	}

	public void setSmSupplier(SmSupplier smSupplier) {
		this.smSupplier = smSupplier;
	}

	public SmCompanyBasic getCompanyBasic() {
		return companyBasic;
	}

	public void setCompanyBasic(SmCompanyBasic companyBasic) {
		this.companyBasic = companyBasic;
	}

	public SmSmeSupplier getSmeSupplier() {
		return smeSupplier;
	}

	public void setSmeSupplier(SmSmeSupplier smeSupplier) {
		this.smeSupplier = smeSupplier;
	}

	public String getSmeCorpRegisteredTrans() {
		return smeCorpRegisteredTrans;
	}

	public void setSmeCorpRegisteredTrans(String smeCorpRegisteredTrans) {
		this.smeCorpRegisteredTrans = smeCorpRegisteredTrans;
	}

	public SmSmeSupplierDoc getSmeSupplierDoc() {
		return smeSupplierDoc;
	}

	public void setSmeSupplierDoc(SmSmeSupplierDoc smeSupplierDoc) {
		this.smeSupplierDoc = smeSupplierDoc;
	}

	public List<SelectItem> getStatusTypeList() {
		return statusTypeList;
	}

	public void setStatusTypeList(List<SelectItem> statusTypeList) {
		this.statusTypeList = statusTypeList;
	}

	public List<SmSmeSupplier> getSupplierSmeList() {
		return supplierSmeList;
	}

	public void setSupplierSmeList(List<SmSmeSupplier> supplierSmeList) {
		this.supplierSmeList = supplierSmeList;
	}
}

package my.ep.web.bean.ct;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

public class ContractListItem {

	
	public static String viewAgreement;
	 

	public String getViewAgreement() {
		return viewAgreement;
	}

	public void setViewAgreement(String viewAgreement) {
		this.viewAgreement = viewAgreement;
	}

	
	public ContractListItem(){
		
	}
	
   public List<SelectItem> getContractOwner() {	
		
		List<SelectItem> list = new ArrayList<SelectItem>();
		list.add(new SelectItem("jabatan","Jabatan"));
		list.add(new SelectItem("ptj","PTJ"));
		return list;
	}

	public List<SelectItem> getContractOwnerJabatan() {	
		
		List<SelectItem> list = new ArrayList<SelectItem>();
		list.add(new SelectItem("jabatan","Jabatan"));
		list.add(new SelectItem("ptj","PTJ"));
		return list;
	}

   public List<SelectItem> getContractType() {	
		
		List<SelectItem> list = new ArrayList<SelectItem>();
		list.add(new SelectItem("ministry","Ministry"));
		list.add(new SelectItem("central","Central"));
		list.add(new SelectItem("panel","Panel"));
		return list;
	   }
   
   public List<SelectItem> getServiceFee() {	
		
		List<SelectItem> list = new ArrayList<SelectItem>();
		list.add(new SelectItem("4","0.4"));
		list.add(new SelectItem("8","0.8"));
		return list;
	}

   
   public List<SelectItem> getCurrency() {	
		
		List<SelectItem> list = new ArrayList<SelectItem>();
		list.add(new SelectItem("MYR","MYR"));
		list.add(new SelectItem("USD","USD"));
		return list;
	}

	public List<SelectItem> getContractOwnerPtj() {	
		
		List<SelectItem> list = new ArrayList<SelectItem>();
		list.add(new SelectItem("jabatan","Jabatan"));
		list.add(new SelectItem("ptj","PTJ"));
		return list;
	}
	
public List<SelectItem> getFulfilmentType() {	
		
		List<SelectItem> list = new ArrayList<SelectItem>();
		list.add(new SelectItem("periodicSchedule","Periodic - Schedule"));
		list.add(new SelectItem("periodicBasis","Periodic - as when basis"));
		list.add(new SelectItem("onOf","On - Off"));
		return list;
	}
	
public List<SelectItem> getSourcingCategory() {	
		
		List<SelectItem> list = new ArrayList<SelectItem>();
		list.add(new SelectItem("product","Product"));
		list.add(new SelectItem("service","Service"));
		return list;
	}

	
public List<SelectItem> getSourcingType() {	
		
		List<SelectItem> list = new ArrayList<SelectItem>();
		list.add(new SelectItem("rtender","Restricted Tender"));
		list.add(new SelectItem("opentender","Open Tender"));
		list.add(new SelectItem("otender","Open Tender With Ebidding"));
		list.add(new SelectItem("quotation","Quotation"));
		list.add(new SelectItem("qebiding","Quotation With Ebidding"));
		list.add(new SelectItem("dnego","Direct Nego"));
		list.add(new SelectItem("pbelow","Purchase Below 50k"));
		return list;
	}
}

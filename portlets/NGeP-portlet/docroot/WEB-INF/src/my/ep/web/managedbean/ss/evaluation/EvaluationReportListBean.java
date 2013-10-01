package my.ep.web.managedbean.ss.evaluation;

import java.util.List;

import my.ep.persistence.entity.ss.directpurchase.RequestListSupplierItem;

public class EvaluationReportListBean {
	
	private String itemName;
	
	private	List<EvaluationReportSubBean> supplierItems;
	
	public EvaluationReportListBean(String itemName){
		this. itemName=itemName;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public List<EvaluationReportSubBean> getSupplierItems() {
		return supplierItems;
	}

	public void setSupplierItems(List<EvaluationReportSubBean> supplierItems) {
		this.supplierItems = supplierItems;
	}

	
	
}

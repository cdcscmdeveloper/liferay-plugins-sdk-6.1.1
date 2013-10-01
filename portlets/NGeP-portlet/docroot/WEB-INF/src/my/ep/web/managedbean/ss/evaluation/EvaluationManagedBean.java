package my.ep.web.managedbean.ss.evaluation;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.ss.directpurchase.RequestListItem;
import my.ep.persistence.entity.ss.directpurchase.RequestListSupplierItem;
import my.ep.web.managedbean.BaseManagedBean;

@ManagedBean(name="evaluationMB")
@SessionScoped
public class EvaluationManagedBean extends BaseManagedBean {
	
	private	List<EvaluationReportListBean> evaluationReportListItems;
	private	List<EvaluationReportSubBean> evaluationReportSupplierItems;
	
	public EvaluationManagedBean() {
		super();
		prepareDummyData1();
	}
	
	
	
	private	void prepareDummyData1(){
		evaluationReportListItems	=	new ArrayList<EvaluationReportListBean>();
		evaluationReportSupplierItems = new ArrayList<EvaluationReportSubBean>();
		EvaluationReportListBean requestListItem1 = new EvaluationReportListBean("General");
		List<EvaluationReportSubBean> supplierItems1 = new ArrayList<EvaluationReportSubBean>();
		supplierItems1.add(new EvaluationReportSubBean("Qtn/Tender Details"));
		supplierItems1.add(new EvaluationReportSubBean("Meeting Details"));
		supplierItems1.add(new EvaluationReportSubBean("Committee Attendence List"));
		requestListItem1.setSupplierItems(supplierItems1);
		evaluationReportListItems.add(requestListItem1);
		
		EvaluationReportListBean requestListItem2 = new EvaluationReportListBean("Stage1");
		List<EvaluationReportSubBean> supplierItems2 = new ArrayList<EvaluationReportSubBean>();
		supplierItems2.add(new EvaluationReportSubBean("Kaedah Pemarkahan"));
		supplierItems2.add(new EvaluationReportSubBean("Complience Summary"));
		
		requestListItem2.setSupplierItems(supplierItems2);
		evaluationReportListItems.add(requestListItem2);
		
		EvaluationReportListBean requestListItem3 = new EvaluationReportListBean("Stage2");
		List<EvaluationReportSubBean> supplierItems3 = new ArrayList<EvaluationReportSubBean>();
		supplierItems3.add(new EvaluationReportSubBean("Kaedah Pemarkahan"));
		supplierItems3.add(new EvaluationReportSubBean("Checklist1"));
		supplierItems3.add(new EvaluationReportSubBean("Checklist2"));
		requestListItem3.setSupplierItems(supplierItems3);
		evaluationReportListItems.add(requestListItem3);
		
		evaluationReportSupplierItems.add(new EvaluationReportSubBean("Hello"));
		evaluationReportSupplierItems.add(new EvaluationReportSubBean("Hello"));
	}



	public List<EvaluationReportListBean> getEvaluationReportListItems() {
		return evaluationReportListItems;
	}



	public void setEvaluationReportListItems(
			List<EvaluationReportListBean> evaluationReportListItems) {
		this.evaluationReportListItems = evaluationReportListItems;
	}



	public List<EvaluationReportSubBean> getEvaluationReportSupplierItems() {
		return evaluationReportSupplierItems;
	}



	public void setEvaluationReportSupplierItems(
			List<EvaluationReportSubBean> evaluationReportSupplierItems) {
		this.evaluationReportSupplierItems = evaluationReportSupplierItems;
	}



	

}

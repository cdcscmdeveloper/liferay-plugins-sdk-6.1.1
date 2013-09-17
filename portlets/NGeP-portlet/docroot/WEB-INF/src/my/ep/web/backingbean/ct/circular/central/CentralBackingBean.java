package my.ep.web.backingbean.ct.circular.central;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.ct.ApproverSetting;
import my.ep.web.bean.ct.CentralContract;
import my.ep.web.managedbean.ct.circular.central.CentralManagedBean;



@ManagedBean(name = CentralBackingBean.FACE_BEAN_NAME)
@RequestScoped
public class CentralBackingBean extends BaseBackingBean<CentralManagedBean>{

	
	public static final String FACE_BEAN_NAME = "centralBackingBean";
	private static final String PROCESS_EDIT_PAGE="/xhtml/ct/circular/centralcontract/updateCircular.xhtml";
	private static final String PROCESS_CANCEL_PAGE="/xhtml/ct/circular/centralcontract/cancelCircular.xhtml";
	
	private Date date;
	private static boolean editCircular;
	private static boolean cancelCircular;
	private static boolean viewCircular;
	private ApproverSetting approverSetting;
	private CentralContract centralContract;
	private CentralContract[] selectedCentralContractList;
	
	public CentralBackingBean(){
		centralContract= new CentralContract();
		approverSetting = new ApproverSetting();
		
	}
	
	public void flush(){
		editCircular = false;
		cancelCircular = false;
		viewCircular = true;
	}
	
	public String gotoEditPage() {	
		flush();
		editCircular = true;
		return PROCESS_EDIT_PAGE;
	}
	
	public String gotoCancelPage() {	
		flush();
		cancelCircular = true;
		return PROCESS_CANCEL_PAGE;
	}
	
	
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	
	public boolean isViewCircular() {
		return viewCircular;
	}
	public void setViewCircular(boolean viewCircular) {
		this.viewCircular = viewCircular;
	}
	public boolean isEditCircular() {
		return editCircular;
	}
	public void setEditCircular(boolean editCircular) {
		this.editCircular = editCircular;
	}
	public boolean isCancelCircular() {
		return cancelCircular;
	}
	public void setCancelCircular(boolean cancelCircular) {
		this.cancelCircular = cancelCircular;
	}
	
	public ApproverSetting getApproverSetting() {
		return approverSetting;
	}
	public void setApproverSetting(ApproverSetting approverSetting) {
		this.approverSetting = approverSetting;
	}

	
	
	public CentralContract[] getSelectedCentralContractList() {
		return selectedCentralContractList;
	}
	public void setSelectedCentralContractList(
			CentralContract[] selectedCentralContractList) {
		this.selectedCentralContractList = selectedCentralContractList;
	}
	public CentralContract getCentralContract() {
		return centralContract;
	}
	public void setCentralContract(CentralContract centralContract) {
		this.centralContract = centralContract;
	}
	
	
	public List<CentralContract> getCentralContract1(){
		List<CentralContract> centralContract1 = new ArrayList<CentralContract>(); 
		   return centralContract1;
	   }
	
	public List<CentralContract> getCentralCircularContract(){
		List<CentralContract> centralCircularContract = new ArrayList<CentralContract>(); 
		   return centralCircularContract;
	   }
	
	
	
}
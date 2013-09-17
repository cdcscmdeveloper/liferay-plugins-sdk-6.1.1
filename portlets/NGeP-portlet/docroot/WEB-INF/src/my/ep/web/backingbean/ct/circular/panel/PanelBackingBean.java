package my.ep.web.backingbean.ct.circular.panel;



import java.util.Date;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.ct.ApproverSetting;
import my.ep.web.bean.ct.CheckBoxSelections;
import my.ep.web.managedbean.ct.circular.panel.PanelManagedBean;

@ManagedBean
@RequestScoped
public class PanelBackingBean extends BaseBackingBean<PanelManagedBean>{
	
	
	private static final String PROCESS_EDIT_PAGE="/xhtml/ct/circular/panel/updateCircular.xhtml";
	private static final String PROCESS_CANCEL_PAGE="/xhtml/ct/circular/panel/cancelCircular.xhtml";
	
	private CheckBoxSelections checkBoxSelections;
	private ApproverSetting approverSetting;
	private Date date;
	private static boolean updateCircular;
	private static boolean cancelCircular;
	private static boolean viewCircular;
	
	public boolean isViewCircular() {
		return viewCircular;
	}


	public void setViewCircular(boolean viewCircular) {
		this.viewCircular = viewCircular;
	}


	public PanelBackingBean(){
		   checkBoxSelections = new CheckBoxSelections();
		   approverSetting =new ApproverSetting();
		
		}
	 
	 
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
	
	public boolean isUpdateCircular() {
		return updateCircular;
	}

	public void setUpdateCircular(boolean updateCircular) {
		this.updateCircular = updateCircular;
	}

	public boolean isCancelCircular() {
		return cancelCircular;
	}

	public void setCancelCircular(boolean cancelCircular) {
		this.cancelCircular = cancelCircular;
	}

	
	
	public void specificationField(){
		if(!checkBoxSelections.isCheckSpecification()){
			
		}
	}
	
	
	public ApproverSetting getApproverSetting() {
		return approverSetting;
	}


	public void setApproverSetting(ApproverSetting approverSetting) {
		this.approverSetting = approverSetting;
	}


	public CheckBoxSelections getCheckBoxSelections() {
		return checkBoxSelections;
	}

	public void setCheckBoxSelections(CheckBoxSelections checkBoxSelections) {
		this.checkBoxSelections = checkBoxSelections;
	}
	
	/* public TreeNode getRoot() {
			return root;
		}

		public void setRoot(TreeNode root) {
			this.root = root;
		}

		public TreeNode getSelectedNode() {
			return selectedNode;
		}

		public void setSelectedNode(TreeNode selectedNode) {
			this.selectedNode = selectedNode;
		} */
	
	   public void flush(){
		   updateCircular = false;
		   cancelCircular = false;
		   viewCircular = true;
	   }

		public String gotoEditPage() {	
			flush();
			updateCircular = true;
			return PROCESS_EDIT_PAGE;
		}
		
		public String gotoCancelPage() {	
			flush();
			cancelCircular = true;
			return PROCESS_CANCEL_PAGE;
		}
}

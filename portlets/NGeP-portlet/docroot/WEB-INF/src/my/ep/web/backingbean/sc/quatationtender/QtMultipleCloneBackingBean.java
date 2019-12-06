package my.ep.web.backingbean.sc.quatationtender;

import java.math.BigDecimal;
import java.util.ResourceBundle;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import my.ep.biz.service.sc.quationtender.IQTDetailSO;
import my.ep.constant.BpmOutcomeConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.sc.quationtender.QuationTenderReqDTO;
import my.ep.dto.sc.quationtender.QuationTenderResDTO;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtSupplierCriteria;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.managedbean.sc.quationtender.QTDetailManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

@ManagedBean(name = "qtMultipleCloneBackingBean")
@RequestScoped
public class QtMultipleCloneBackingBean extends
SourcingBaseBackingBean<QTDetailManagedBean>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** The Constant logger. */
	private static final Logger logger = EpLogger
			.getLogger(QtMultipleCloneBackingBean.class);
	
	private TreeNode root;
	private TreeNode[] selectedNodes;
	private boolean showTree = false;
	private int totalClone = 1;
	private static final String ALL = "lbl.txt.all";
	private static final String QT_DETAILS = "lbl.sc.qt.qtn.details";
	private static final String SUPPLIER_QUALIFICATION_CRITERIA = "lbl.sc.supplier.qulification.criteria";
	private static final String COMMITTEE_APPOINTMENT = "lbl.sc.qt.committee.appointment";
	private static final String APPROVAL = "lbl.sc.approval";
	private ScQt selectedScqt;
	
	public QtMultipleCloneBackingBean(){
		this.root = new DefaultTreeNode("Root Node", null);
		this.selectedScqt=new ScQt();
		TreeNode child = new DefaultTreeNode(new QTSelectionNode(getLabel(ALL),0L), this.root);
		child.setParent(this.root);
		TreeNode descendent1 = new DefaultTreeNode(new QTSelectionNode(getLabel(QT_DETAILS),1L), child);
		descendent1.setParent(child);
		TreeNode descendent2 = new DefaultTreeNode(new QTSelectionNode(getLabel(SUPPLIER_QUALIFICATION_CRITERIA),2L), child);
		descendent2.setParent(child);
		TreeNode descendent3 = new DefaultTreeNode(new QTSelectionNode(getLabel(COMMITTEE_APPOINTMENT),3L), child);
		descendent3.setParent(child);
		TreeNode descendent4 = new DefaultTreeNode(new QTSelectionNode(getLabel(APPROVAL),4L), child);
		descendent4.setParent(child);
			
	}
	
	public void clonePopupAction(ScQt scQt){
		debugLogger("clonePopupAction");
		this.totalClone=1;
		selectedScqt=scQt;
		RequestContext.getCurrentInstance().execute("kklik.show()");
	}
	
	public void addToClone(){
		IQTDetailSO QtDetailsSO;
		try {
		QtDetailsSO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
		
		QuationTenderReqDTO tenderReqDTO = new QuationTenderReqDTO();
		tenderReqDTO.setMaintanceFlow(this.getManagedBean()
				.isMaintanceFlow());
		tenderReqDTO.setQtId(selectedScqt.getQtId());
		tenderReqDTO.setBdgDtlsReq(true);
		
		QuationTenderResDTO tenderResDTO = null;
		tenderResDTO = QtDetailsSO.retriveQTDetails(tenderReqDTO);
		ScQt copyScqt = tenderResDTO.getQtDetail();
		ScQt scQt=populateQtnDetails(copyScqt,QtDetailsSO);
		
		QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
		reqDTO.setSaveFlow(true);
		reqDTO.setOutcome(BpmOutcomeConst.BPM_SAVE);
		
		}finally{
			QtDetailsSO=null;
		}
	}
	
	private ScQt populateQtnDetails(ScQt copyScqt,IQTDetailSO QtDetailsSO){
		ScQt scQt=new ScQt();
		scQt.setFileNo(null);
		scQt.setObtainSpecialApproval(copyScqt
				.getObtainSpecialApproval());
		scQt.setPanel(copyScqt.isPanel());
		scQt.setCircularVer(copyScqt.getCircularVer());
		scQt.setItemGroup(copyScqt.getItemGroup());
		scQt.setQtTitle(copyScqt.getQtTitle());
		scQt.setProcurementMode(copyScqt.getProcurementMode());
		
		BigDecimal tmpExpectedIntendedPrice = QtDetailsSO
				.getDecryptedExpectedIntendedPrice(copyScqt.getQtId());
		
		scQt.setTmpExpectedIntendedPrice(tmpExpectedIntendedPrice);
		scQt.setEstimatedAmount(copyScqt.getEstimatedAmount());
		scQt.setDecryptedEstAmt(copyScqt.getDecryptedEstAmt());
		scQt.setDecExpectedIntendedPrice(copyScqt.getDecExpectedIntendedPrice());
		
	//	scQt.setZonal(copyScqt.isZonal());
		
		scQt.setProcurementMode(copyScqt.getProcurementMode());
		scQt.setQtType(copyScqt.getQtType());
		scQt.setOpenFor(copyScqt.getOpenFor());
		scQt.setEvaluationType(copyScqt.getEvaluationType());
//		if(null!=copyScqt.getScQtSupplierCriteria()){
//		ScQtSupplierCriteria scQtSupplierCriteria =scQt.getScQtSupplierCriteria();
//		scQtSupplierCriteria.setAllState(copyScqt.getScQtSupplierCriteria().isAllState());
//		}
		
		scQt.setQtType(copyScqt.getQtType());
		scQt.setZoneLocationLevel(copyScqt.getZoneLocationLevel());
		scQt.setProcurementTypeCatId(copyScqt.getProcurementTypeCatId());
		scQt.setPmProcurementCategory(copyScqt.getPmProcurementCategory());
		scQt.setEvaluationType(copyScqt.getEvaluationType());
		scQt.setContractType(copyScqt.getContractType());
		scQt.setExistingContractNo(copyScqt.getExistingContractNo());
		scQt.setFulfilmentType(copyScqt.getFulfilmentType());
		scQt.setContractDuration(copyScqt.getContractDuration());
		scQt.setPublishPeriod(copyScqt.getPublishPeriod());
		scQt.setQtValidityPeriod(copyScqt.getQtValidityPeriod());
		
		scQt.setSpecApprovalReq(copyScqt.getSpecApprovalReq());
		scQt.setMinistrySpecSecrataryId(copyScqt.getMinistrySpecSecrataryId());
		
		return scQt;
	
	}
	
	
	private String getLabel(String label) {
		FacesContext context = FacesContext.getCurrentInstance();
		ResourceBundle bundle = context.getApplication().getResourceBundle(
				context, "lbl");
		String message = bundle.getString(label);
		return message;
	}
	
	private void debugLogger(Object message) {

		if (logger.isDebugEnabled()) {
			logger.debug(message);
		}
		/*
		 * else{ logger.info(message); }
		 */

	}

	public int getTotalClone() {
		return totalClone;
	}

	public void setTotalClone(int totalClone) {
		this.totalClone = totalClone;
	}

	public TreeNode[] getSelectedNodes() {
		return selectedNodes;
	}

	public void setSelectedNodes(TreeNode[] selectedNodes) {
		this.selectedNodes = selectedNodes;
	}
	
	public boolean isShowTree() {
		return showTree;
	}

	public void setShowTree(boolean showTree) {
		this.showTree = showTree;
	}

	public TreeNode getRoot() {
		return root;
	}

	public void setRoot(TreeNode root) {
		this.root = root;
	}

	public ScQt getSelectedScqt() {
		return selectedScqt;
	}

	public void setSelectedScqt(ScQt selectedScqt) {
		this.selectedScqt = selectedScqt;
	}
	
}

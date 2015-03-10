/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.managedbean.ct.circular.panel;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.ct.circular.ICircularSO;
import my.ep.constant.EpPropertiesConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.ct.ContractConst;
import my.ep.constant.ct.ContractStatusConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.dto.ct.circular.CircularReqDTO;
import my.ep.dto.ct.circular.CircularResDTO;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.ct.CtAttachment;
import my.ep.persistence.entity.ct.CtRemark;
import my.ep.persistence.entity.ct.circular.CtCancelCircularReq;
import my.ep.persistence.entity.ct.circular.CtCircular;
import my.ep.persistence.entity.ct.circular.CtCircularVer;
import my.ep.persistence.entity.ct.circular.dtl.CtCancelCircular;
import my.ep.persistence.entity.ct.circular.dtl.CtContactPerson;
import my.ep.persistence.entity.ct.circular.panel.CtPanelItem;
import my.ep.persistence.entity.ct.circular.panel.CtPanelSupplier;
import my.ep.persistence.entity.ct.circular.panel.CtPanelZone;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmDigiSign;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.type.aut.SearchOperator;
import my.ep.util.EpPropertiesUtils;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.primefaces.model.DualListModel;
import org.primefaces.model.TreeNode;

/**
 * Circular Panel Managed Bean
 * 
 * @author alam
 * @Created 9/10/2013
 */
@ManagedBean
@SessionScoped
public class PanelManagedBean extends BaseManagedBean {

	private static final long serialVersionUID = 1L;
	
	private String fileDirectory = EpPropertiesUtils.getInstance().getProperty(EpPropertiesConst.DOCUMENT_REPOSITORY_BASE_DIR)+"/CT/Circulars/"+ParameterConst.DOCTYPE_PANEL_CIRCULAR;
	private String circularDocType;
	private String circularAdmin;
	private String action;
	private String searchCircularNo;
	private String searchCircularName;
	private String searchPhysicalCircularNo;
	private String searchSupplierName;
	private String searchItemName;
	private String zoneName;
	private String gotoPreviewPage;	
		
	private Date searchStartDate;
	private Date searchEndDate;	
	
	private Long searchStatusId;
	private Long itemCategoryId;
	private Long panelCoverageId;
	private int selectedTabIndex;
	
	private boolean editZone;
	private boolean updateAction =false;
	private boolean viewContract =false;
	private boolean buttonHideActive;
	private boolean selectionOfZone;
	private boolean chkCancelPage;
	private boolean fromCircularNotice;
	private boolean differentModule;
	private boolean circularExtension;
	private int index = -1;
	
	
	private TreeNode[] selectedGeneralTreeNodes;
	private List<TreeNode> zoneGeneralTrees = new ArrayList<TreeNode>();
	private TreeNode rootLocationTree;
	private TreeNode rootZoanlTree;
	private TreeNode selectedZoneTree;
	
	private CtCircular circular;
	private CtCircularVer circularVer;
	private CtCancelCircular selectedCancelCircular;
	private CtCancelCircularReq cancelCircularReq;
	private CtPanelItem selectedPanelItems;
	private CtPanelZone selectedPanelZone;
	private List <CtPanelSupplier> selectedPanelSuppliers;
	private List<CtContactPerson> selectedContactPersons;
	private List<CtCancelCircular> selectedCancelCirculars;
	private List<CtCancelCircular> cancelledCircularList;
	private List<CtCircularVer> circularVerList;
	private List<CtCancelCircular> selectedCancelledCircularList;
	private List<CtRemark> remarkList;
	private List<CtAttachment> attachmentList;
	private List<CtPanelSupplier> panelSupplierList;
	private CtCircularVer[] selectedCircularVerList;
	private DualListModel<String> approverModel;
	private Map<String, PmUser> userMap;
	private List<SmSupplier> supplierList; 
	
	//gpki signing
	private PmDigiSign pmDigiSign;
	private boolean autoUpdate = true;
	private String textBeforeSign;
	
	private IPagingDataModel<CtCircularVer> circularVerPagingDataModel = new CircularVerPagingDataModel();
	
	public CtCircular getCircular() {
		return circular;
	}

	public void setCircular(CtCircular circular) {
		this.circular = circular;
	}

	public CtCircularVer getCircularVer() {
		return circularVer;
	}

	public void setCircularVer(CtCircularVer circularVer) {
		this.circularVer = circularVer;
	}

	public CtCancelCircular getSelectedCancelCircular() {
		return selectedCancelCircular;
	}

	public void setSelectedCancelCircular(CtCancelCircular selectedCancelCircular) {
		this.selectedCancelCircular = selectedCancelCircular;
	}

	public List<CtCancelCircular> getSelectedCancelledCircularList() {
		return selectedCancelledCircularList;
	}

	public void setSelectedCancelledCircularList(
			List<CtCancelCircular> selectedCancelledCircularList) {
		this.selectedCancelledCircularList = selectedCancelledCircularList;
	}

	public CtPanelItem getSelectedPanelItems() {
		return selectedPanelItems;
	}

	public void setSelectedPanelItems(CtPanelItem selectedPanelItems) {
		this.selectedPanelItems = selectedPanelItems;
	}

	public DualListModel<String> getApproverModel() {
		return approverModel;
	}

	public void setApproverModel(DualListModel<String> approverModel) {
		this.approverModel = approverModel;
	}

	public Date getSearchStartDate() {
		return searchStartDate;
	}

	public void setSearchStartDate(Date searchStartDate) {
		this.searchStartDate = searchStartDate;
	}

	public Date getSearchEndDate() {
		return searchEndDate;
	}

	public void setSearchEndDate(Date searchEndDate) {
		this.searchEndDate = searchEndDate;
	}

	public String getSearchCircularNo() {
		return searchCircularNo;
	}

	public void setSearchCircularNo(String searchCircularNo) {
		this.searchCircularNo = searchCircularNo;
	}

	public String getSearchCircularName() {
		return searchCircularName;
	}

	public void setSearchCircularName(String searchCircularName) {
		this.searchCircularName = searchCircularName;
	}
	
	public String getSearchPhysicalCircularNo() {
		return searchPhysicalCircularNo;
	}
	
	public void setSearchPhysicalCircularNo(String searchPhysicalCircularNo) {
		this.searchPhysicalCircularNo = searchPhysicalCircularNo;
	}

	public String getSearchSupplierName() {
		return searchSupplierName;
	}

	public void setSearchSupplierName(String searchSupplierName) {
		this.searchSupplierName = searchSupplierName;
	}

	public boolean isDifferentModule() {
		return differentModule;
	}

	public void setDifferentModule(boolean differentModule) {
		this.differentModule = differentModule;
	}

	public String getSearchItemName() {
		return searchItemName;
	}

	public void setSearchItemName(String searchItemName) {
		this.searchItemName = searchItemName;
	}

	public Long getSearchStatusId() {
		return searchStatusId;
	}

	public void setSearchStatusId(Long searchStatusId) {
		this.searchStatusId = searchStatusId;
	}

	public boolean isUpdateAction() {
		return updateAction;
	}

	public void setUpdateAction(boolean updateAction) {
		this.updateAction = updateAction;
	}

	public CtCancelCircularReq getCancelCircularReq() {
		return cancelCircularReq;
	}

	public void setCancelCircularReq(CtCancelCircularReq cancelCircularReq) {
		this.cancelCircularReq = cancelCircularReq;
	}

	public CtCircularVer[] getSelectedCircularVerList() {
		return selectedCircularVerList;
	}

	public void setSelectedCircularVerList(CtCircularVer[] selectedCircularVerList) {
		this.selectedCircularVerList = selectedCircularVerList;
	}

	public List<CtCircularVer> getCircularVerList() {
		return circularVerList;
	}

	public void setCircularVerList(List<CtCircularVer> circularVerList) {
		this.circularVerList = circularVerList;
	}

	public String getCircularDocType() {
		return circularDocType;
	}

	public void setCircularDocType(String circularDocType) {
		this.circularDocType = circularDocType;
	}

	public List<CtRemark> getRemarkList() {
		if(remarkList==null) {
			remarkList = new ArrayList<CtRemark>();
		}
		return remarkList;
	}

	public void setRemarkList(List<CtRemark> remarkList) {
		this.remarkList = remarkList;
	}

	public List<CtAttachment> getAttachmentList() {
		if(attachmentList==null) {
			attachmentList = new ArrayList<CtAttachment>();
		}
		return attachmentList;
	}

	public void setAttachmentList(List<CtAttachment> attachmentList) {
		this.attachmentList = attachmentList;
	}

	public String getCircularAdmin() {
		return circularAdmin;
	}

	public CtPanelZone getSelectedPanelZone() {
		return selectedPanelZone;
	}

	public void setSelectedPanelZone(CtPanelZone selectedPanelZone) {
		this.selectedPanelZone = selectedPanelZone;
	}

	public void setCircularAdmin(String circularAdmin) {
		this.circularAdmin = circularAdmin;
	}

	public List<CtContactPerson> getSelectedContactPersons() {
		return selectedContactPersons;
	}

	public void setSelectedContactPersons(List<CtContactPerson> selectedContactPersons) {
		this.selectedContactPersons = selectedContactPersons;
	}

	public List<CtCancelCircular> getSelectedCancelCirculars() {
		return selectedCancelCirculars;
	}

	public void setSelectedCancelCirculars(List<CtCancelCircular> selectedCancelCirculars) {
		this.selectedCancelCirculars = selectedCancelCirculars;
	}

	public List<CtCancelCircular> getCancelledCircularList() {
		if(cancelledCircularList == null)
			cancelledCircularList = new ArrayList<CtCancelCircular>();
		return cancelledCircularList;
	}

	public void setCancelledCircularList(List<CtCancelCircular> cancelledCircularList) {
		this.cancelledCircularList = cancelledCircularList;
	}

	public boolean isSelectionOfZone() {
		return selectionOfZone;
	}

	public void setSelectionOfZone(boolean selectionOfZone) {
		this.selectionOfZone = selectionOfZone;
	}

	public List<CtPanelSupplier> getPanelSupplierList() {
		if(panelSupplierList == null)
			panelSupplierList = new ArrayList<CtPanelSupplier>();
		return panelSupplierList;
	}

	public void setPanelSupplierList(List<CtPanelSupplier> panelSupplierList) {
		this.panelSupplierList = panelSupplierList;
	}

	public List<CtPanelSupplier> getSelectedPanelSuppliers() {
		return selectedPanelSuppliers;
	}

	public void setSelectedPanelSuppliers(
			List<CtPanelSupplier> selectedPanelSuppliers) {
		this.selectedPanelSuppliers = selectedPanelSuppliers;
	}

	public TreeNode getRootZoanlTree() {
		return rootZoanlTree;
	}

	public void setRootZoanlTree(TreeNode rootZoanlTree) {
		this.rootZoanlTree = rootZoanlTree;
	}

	public String getZoneName() {
		return zoneName;
	}

	public void setZoneName(String zoneName) {
		this.zoneName = zoneName;
	}

	public TreeNode[] getSelectedGeneralTreeNodes() {
		return selectedGeneralTreeNodes;
	}

	public void setSelectedGeneralTreeNodes(TreeNode[] selectedGeneralTreeNodes) {
		this.selectedGeneralTreeNodes = selectedGeneralTreeNodes;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public List<TreeNode> getZoneGeneralTrees() {
		return zoneGeneralTrees;
	}

	public void setZoneGeneralTrees(List<TreeNode> zoneGeneralTrees) {
		this.zoneGeneralTrees = zoneGeneralTrees;
	}

	public TreeNode getRootLocationTree() {
		return rootLocationTree;
	}

	public void setRootLocationTree(TreeNode rootLocationTree) {
		this.rootLocationTree = rootLocationTree;
	}
	
	public Map<String, PmUser> getUserMap() {
		if(userMap==null) {
			userMap = new HashMap<String, PmUser>();
		}
		return userMap;
	}

	public void setUserMap(Map<String, PmUser> userMap) {
		this.userMap = userMap;
	}

	public TreeNode getSelectedZoneTree() {
		return selectedZoneTree;
	}

	public void setSelectedZoneTree(TreeNode selectedZoneTree) {
		this.selectedZoneTree = selectedZoneTree;
	}

	public boolean isEditZone() {
		return editZone;
	}

	public void setEditZone(boolean editZone) {
		this.editZone = editZone;
	}

	public boolean isButtonHideActive() {
		return buttonHideActive;
	}

	public void setButtonHideActive(boolean buttonHideActive) {
		this.buttonHideActive = buttonHideActive;
	}

	public Long getItemCategoryId() {
		return itemCategoryId;
	}

	public void setItemCategoryId(Long itemCategoryId) {
		this.itemCategoryId = itemCategoryId;
	}

	public Long getPanelCoverageId() {
		return panelCoverageId;
	}

	public void setPanelCoverageId(Long panelCoverageId) {
		this.panelCoverageId = panelCoverageId;
	}

	public int getSelectedTabIndex() {
		return selectedTabIndex;
	}

	public void setSelectedTabIndex(int selectedTabIndex) {
		this.selectedTabIndex = selectedTabIndex;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getFileDirectory() {
		return fileDirectory;
	}

	public void setFileDirectory(String fileDirectory) {
		this.fileDirectory = fileDirectory;
	}

	public List<SmSupplier> getSupplierList() {
		return supplierList;
	}

	public void setSupplierList(List<SmSupplier> supplierList) {
		this.supplierList = supplierList;
	}

	public boolean isCircularExtension() {
		return circularExtension;
	}

	public void setCircularExtension(boolean circularExtension) {
		this.circularExtension = circularExtension;
	}

	public boolean isChkCancelPage() {
		return chkCancelPage;
	}

	public void setChkCancelPage(boolean chkCancelPage) {
		this.chkCancelPage = chkCancelPage;
	}
	
	public boolean isViewContract() {
		return viewContract;
	}
	public void setViewContract(boolean viewContract) {
		this.viewContract = viewContract;
	}

	public PmDigiSign getPmDigiSign() {
		return pmDigiSign;
	}

	public void setPmDigiSign(PmDigiSign pmDigiSign) {
		this.pmDigiSign = pmDigiSign;
	}

	public boolean isAutoUpdate() {
		return autoUpdate;
	}

	public void setAutoUpdate(boolean autoUpdate) {
		this.autoUpdate = autoUpdate;
	}

	public String getGotoPreviewPage() {
		return gotoPreviewPage;
	}

	public void setGotoPreviewPage(String gotoPreviewPage) {
		this.gotoPreviewPage = gotoPreviewPage;
	}

	public String getTextBeforeSign() {
		return textBeforeSign;
	}

	public void setTextBeforeSign(String textBeforeSign) {
		this.textBeforeSign = textBeforeSign;
	}
	
	public IPagingDataModel<CtCircularVer> getCircularVerPagingDataModel() {
		return circularVerPagingDataModel;
	}

	public void setCircularVerPagingDataModel(
			IPagingDataModel<CtCircularVer> circularVerPagingDataModel) {
		this.circularVerPagingDataModel = circularVerPagingDataModel;
	}
	
	public boolean isFromCircularNotice() {
		return fromCircularNotice;
	}

	public void setFromCircularNotice(boolean fromCircularNotice) {
		this.fromCircularNotice = fromCircularNotice;
	}

	private class CircularVerPagingDataModel extends PagingDataModel<CtCircularVer> {
		private static final long serialVersionUID = 1L;

		public CircularVerPagingDataModel() {
			super("circularVerDt");
		}
		
		@Override
		public ISearchResultBean<CtCircularVer> pageChangeListener(ISearchBean<CtCircularVer> searchBean) {
			try {				
				searchBean.getDataFilterBeans().add(new SearchFilterBean("ctCircular.circularType",SearchOperator.OPERATOR_EQUALS.value(),ContractConst.CIRCULAR_DOC_TYPE_PANEL));
				if(getSearchCircularNo()!=null && getSearchCircularNo().length()>0) {
					SearchFilterBean filterBean = new SearchFilterBean("ctCircular.circularNo",
													SearchOperator.OPERATOR_LIKE.value(),getSearchCircularNo());
					filterBean.setCaseInsensitive(true);
					searchBean.getDataFilterBeans().add(filterBean);	
				}
				if(getSearchCircularName()!=null && getSearchCircularName().length()>0) {
					SearchFilterBean filterBean = new SearchFilterBean("name",
													SearchOperator.OPERATOR_LIKE.value(),getSearchCircularName());
					filterBean.setCaseInsensitive(true);
					searchBean.getDataFilterBeans().add(filterBean);
				}
				if(getSearchSupplierName()!=null && getSearchSupplierName().length()>0) {
					SearchFilterBean filterBean = new SearchFilterBean("ctPanelItems.ctPanelSuppliers.supplier.companyName",
													SearchOperator.OPERATOR_LIKE.value(),getSearchSupplierName());
					filterBean.setCaseInsensitive(true);
					searchBean.getDataFilterBeans().add(filterBean);
				}			
				if(getSearchPhysicalCircularNo()!=null && getSearchPhysicalCircularNo().length()>0) {
					SearchFilterBean filterBean = new SearchFilterBean("circularNo",
													SearchOperator.OPERATOR_LIKE.value(),getSearchPhysicalCircularNo());
					filterBean.setCaseInsensitive(true);
					searchBean.getDataFilterBeans().add(filterBean);
				}	
				if(getSearchItemName()!=null && getSearchItemName().length()>0) {
					SearchFilterBean filterBean = new SearchFilterBean("ctPanelItems.itemName",
													SearchOperator.OPERATOR_LIKE.value(),getSearchItemName());
					filterBean.setCaseInsensitive(true);
					searchBean.getDataFilterBeans().add(filterBean);
				
				}if(getSearchStartDate()!=null && getSearchEndDate()!=null) {
				    searchBean.getDataFilterBeans().add(new SearchFilterBean("startDate",SearchOperator.OPERATOR_GREATER_EQUAL.value(), getSearchStartDate()));	
				    searchBean.getDataFilterBeans().add(new SearchFilterBean("endDate",SearchOperator.OPERATOR_LESS_EQUAL.value(), getSearchEndDate()));		
			
			    }if(getSearchStartDate()!=null && getSearchEndDate()==null) {
				   searchBean.getDataFilterBeans().add(new SearchFilterBean("startDate",SearchOperator.OPERATOR_EQUALS.value(), getSearchStartDate()));	
			
			    }if(getSearchStartDate()==null && getSearchEndDate()!=null) {
			        searchBean.getDataFilterBeans().add(new SearchFilterBean("endDate",SearchOperator.OPERATOR_EQUALS.value(), getSearchEndDate()));	
			    }
			    
			    if(getSearchStatusId()!=null) {
					//Filter out cancelled status
					if(getSearchStatusId().longValue() == ContractStatusConst.PQ_APPROVED || 
							getSearchStatusId().longValue() == ContractStatusConst.PQ_REJECTED)
						searchBean.getDataFilterBeans().add(new SearchFilterBean("ctCircular.recordStatus",SearchOperator.OPERATOR_NOT_EQUALS.value(), DbValueConst.RECORD_STATUS_CANCELLED));
					//display cancelled circular
					else if(getSearchStatusId().longValue() == 0L)
						searchBean.getDataFilterBeans().add(new SearchFilterBean("ctCircular.recordStatus",SearchOperator.OPERATOR_EQUALS.value(), DbValueConst.RECORD_STATUS_CANCELLED));
				}					
				
			    CircularReqDTO reqDTO = new CircularReqDTO();
				reqDTO.setCircularVerSearchBean(searchBean);
				reqDTO.setFilterStatusId(getSearchStatusId());
				
				ICircularSO circularSO = (ICircularSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,ICircularSO.class);
				CircularResDTO resDTO = circularSO.searchCircularVer(reqDTO);
				if(resDTO.isSuccessful()) {
					if(resDTO.getCircularVerResultBean() != null && resDTO.getCircularVerResultBean().getResultList() != null) {
						
						for(CtCircularVer circularVer : resDTO.getCircularVerResultBean().getResultList()) {
							/*CircularReqDTO circularReqDTO = new CircularReqDTO();
							circularReqDTO.setCircularId(circularVer.getCtCircular().getCircularId());
							circularVer.getCtCircular().setLatestCircularVer(circularSO.getLatestCircularVer(circularReqDTO).getCircularVer());
							*/
							if(circularVer.getCtCircular().getRecordStatusFlg() == DbValueConst.RECORD_STATUS_CANCELLED) {
								PmStatus cancelStatus = new PmStatus();
								cancelStatus.setStatusId(0L);
								circularVer.setStatus(cancelStatus);
							
						  }
							//if(circularVer.getStatus() != null && circularVer.getStatus().getStatusId().equals(ContractStatusConst.PQ_REJECTED)) {
								if(circularVer.getCtCircular().getLatestCircularVer() != null)
									circularVer.setCircularVerId(circularVer.getCtCircular().getLatestCircularVer().getCircularVerId());
						 // }
					  }
				  }
				return resDTO.getCircularVerResultBean();
			}
			} catch (Exception e) {
				e.printStackTrace();
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
			}
			return null;
		}
	}
}

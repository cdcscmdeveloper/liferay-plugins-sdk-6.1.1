/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.managedbean.ct.circular.cc;

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
import my.ep.persistence.entity.ct.CtContract;
import my.ep.persistence.entity.ct.CtRemark;
import my.ep.persistence.entity.ct.circular.CtCircular;
import my.ep.persistence.entity.ct.circular.CtCircularVer;
import my.ep.persistence.entity.ct.circular.cc.CtCircularContract;
import my.ep.persistence.entity.ct.circular.cc.CtCircularItem;
import my.ep.persistence.entity.ct.circular.dtl.CtCancelCircular;
import my.ep.persistence.entity.ct.circular.dtl.CtContactPerson;
import my.ep.persistence.entity.ct.circular.dtl.CtExemptedOrg;
import my.ep.persistence.entity.ct.dtl.item.CtItemPrice;
import my.ep.persistence.entity.ct.dtl.zone.CtContractZone;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.authorization.PmUserGroup;
import my.ep.persistence.entity.pm.common.PmDigiSign;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.type.aut.SearchOperator;
import my.ep.util.EpPropertiesUtils;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.primefaces.model.DualListModel;
import org.primefaces.model.TreeNode;

/**
 * Central Contract Circular Managed Bean
 * 
 * @author alam
 * @since 9/10/2013
 */
@ManagedBean
@SessionScoped
public class CentralContractManagedBean extends BaseManagedBean {

	private static final long serialVersionUID = 1L;
	private String fileDirectory = EpPropertiesUtils.getInstance().getProperty(EpPropertiesConst.DOCUMENT_REPOSITORY_BASE_DIR)+"/CT/Circulars/"+ParameterConst.DOCTYPE_CENTRAL_CONTRACT_CIRCULAR;

	private String circularAdmin;
	private String action;
	private String searchCircularNo;
	private String searchCircularName;
	private String searchPhysicalCircularNo;
	private String searchContractNo;
	private String searchContractName;
	private String textBeforeSign;
	private String taskName;
	
	private Date searchStartDate;
	private Date searchEndDate;	
	
	private Long searchStatusId;
	private int selectedTabIndex;
	
	private boolean updateAction = false;
	private boolean viewContract = false;
	private boolean updateTypeDisable;
	private boolean chkItemZone;
	private boolean chkItemPrice;
	private boolean chkCancelPage;
	private boolean fromCircularNotice;
	private boolean autoUpdate = true;
	private boolean circularExtension;
	
	private TreeNode centralZoneTrees;
	private PmDigiSign pmDigiSign;
	private CtCircular circular;
	private CtCircularVer circularVer;
	private CtCircularContract selectedCircularContract;
	private CtCircularItem selectedCircularItem;
	private CtContractZone selectedZoneItems;
	private List<CtContract> contractList;
	private List<CtContract> selectedContractList;
	private List<CtContactPerson> selectedContactPersons;
	private List<CtExemptedOrg> selectedExemptedOrgs;
	private List<CtCancelCircular> selectedCancelCirculars;
	private List<CtCancelCircular> cancelledCircularList;
	private List<CtCancelCircular> selectedCancelledCircularList;	
	private List<CtRemark> remarkList;
	private List<CtAttachment> attachmentList;
	private List<PmOrgProfile> orgProfiles; 
	private List<CtContractZone> contractItemZones;
	private DualListModel<String> approverModel;
	private Map<Long, PmUserGroup> userGroupMap;	
	private Map<CtContractZone, List<CtItemPrice>> zonePriceMap;
	private Map<String, PmUser> userMap;
	private IPagingDataModel<CtCircularVer> circularVerPagingDataModel = new CircularVerPagingDataModel();
	
	/**
	 * @return the updateAction
	 */
	public boolean isUpdateAction() {
		return updateAction;
	}
	
	/**
	 * @param updateAction the updateAction to set
	 */
	public void setUpdateAction(boolean updateAction) {
		this.updateAction = updateAction;
	}
	
	/**
	 * @return the circularVer
	 */
	public CtCircularVer getCircularVer() {
		return circularVer;
	}
	
	/**
	 * @param circularVer the circularVer to set
	 */
	public void setCircularVer(CtCircularVer circularVer) {
		this.circularVer = circularVer;
	}
	
	/**
	 * @return the approverModel
	 */
	public DualListModel<String> getApproverModel() {
		return approverModel;
	}
	
	/**
	 * @param approverModel the approverModel to set
	 */
	public void setApproverModel(DualListModel<String> approverModel) {
		this.approverModel = approverModel;
	}
	
	/**
	 * @return the searchCircularNo
	 */
	public String getSearchCircularNo() {
		return searchCircularNo;
	}
	
	/**
	 * @param searchCircularNo the searchCircularNo to set
	 */
	public void setSearchCircularNo(String searchCircularNo) {
		this.searchCircularNo = searchCircularNo;
	}
	
	/**
	 * @return the searchCircularName
	 */
	public String getSearchCircularName() {
		return searchCircularName;
	}
	
	/**
	 * @param searchCircularName the searchCircularName to set
	 */
	public void setSearchCircularName(String searchCircularName) {
		this.searchCircularName = searchCircularName;
	}
	
	/**
	 * @return the searchPhysicalCircularNo
	 */
	public String getSearchPhysicalCircularNo() {
		return searchPhysicalCircularNo;
	}
	
	/**
	 * @param searchPhysicalCircularNo the searchPhysicalCircularNo to set
	 */
	public void setSearchPhysicalCircularNo(String searchPhysicalCircularNo) {
		this.searchPhysicalCircularNo = searchPhysicalCircularNo;
	}
	
	/**
	 * @return the searchStartDate
	 */
	public Date getSearchStartDate() {
		return searchStartDate;
	}
	
	/**
	 * @param searchStartDate the searchStartDate to set
	 */
	public void setSearchStartDate(Date searchStartDate) {
		this.searchStartDate = searchStartDate;
	}
	
	/**
	 * @return the searchEndDate
	 */
	public Date getSearchEndDate() {
		return searchEndDate;
	}
	
	/**
	 * @param searchEndDate the searchEndDate to set
	 */
	public void setSearchEndDate(Date searchEndDate) {
		this.searchEndDate = searchEndDate;
	}
	
	/**
	 * @return the searchStatusId
	 */
	public Long getSearchStatusId() {
		return searchStatusId;
	}
	
	/**
	 * @param searchStatusId the searchStatusId to set
	 */
	public void setSearchStatusId(Long searchStatusId) {
		this.searchStatusId = searchStatusId;
	}
	
	/**
	 * @return the searchContractNo
	 */
	public String getSearchContractNo() {
		return searchContractNo;
	}
	
	/**
	 * @param searchContractNo the searchContractNo to set
	 */
	public void setSearchContractNo(String searchContractNo) {
		this.searchContractNo = searchContractNo;
	}
	
	/**
	 * @return the searchContractName
	 */
	public String getSearchContractName() {
		return searchContractName;
	}
	
	/**
	 * @param searchContractName the searchContractName to set
	 */
	public void setSearchContractName(String searchContractName) {
		this.searchContractName = searchContractName;
	}
	
	/**
	 * @return the circular
	 */
	public CtCircular getCircular() {
		return circular;
	}
	
	/**
	 * @param circular the circular to set
	 */
	public void setCircular(CtCircular circular) {
		this.circular = circular;
	}
	
	/**
	 * @return the contractList
	 */
	public List<CtContract> getContractList() {
		if(contractList==null) {
			contractList = new ArrayList<CtContract>();
		}
		return contractList;
	}
	
	/**
	 * @param contractList the contractList to set
	 */
	public void setContractList(List<CtContract> contractList) {
		this.contractList = contractList;
	}
	
	/**
	 * @return the remarkList
	 */
	public List<CtRemark> getRemarkList() {
		if(remarkList == null)
			remarkList = new ArrayList<CtRemark>();
		return remarkList;
	}

	/**
	 * @param remarkList the remarkList to set
	 */
	public void setRemarkList(List<CtRemark> remarkList) {
		this.remarkList = remarkList;
	}

	/**
	 * @return the attachmentList
	 */
	public List<CtAttachment> getAttachmentList() {
		if(attachmentList == null)
		   attachmentList = new ArrayList<CtAttachment>();
		return attachmentList;
	}

	/**
	 * @param attachmentList the attachmentList to set
	 */
	public void setAttachmentList(List<CtAttachment> attachmentList) {
		this.attachmentList = attachmentList;
	}
	
	/**
	 * @return the userGroupMap
	 */
	public Map<Long, PmUserGroup> getUserGroupMap() {
		return userGroupMap;
	}
	
	/**
	 * @param userGroupMap the userGroupMap to set
	 */
	public void setUserGroupMap(Map<Long, PmUserGroup> userGroupMap) {
		this.userGroupMap = userGroupMap;
	}
	
	/**
	 * @return the circularAdmin
	 */
	public String getCircularAdmin() {
		return circularAdmin;
	}
	
	/**
	 * @param circularAdmin the circularAdmin to set
	 */
	public void setCircularAdmin(String circularAdmin) {
		this.circularAdmin = circularAdmin;
	}
	
	/**
	 * @return the selectedContactPersons
	 */
	public List<CtContactPerson> getSelectedContactPersons() {
		return selectedContactPersons;
	}
	
	/**
	 * @param selectedContactPersons the selectedContactPersons to set
	 */
	public void setSelectedContactPersons(List<CtContactPerson> selectedContactPersons) {
		this.selectedContactPersons = selectedContactPersons;
	}
	
	/**
	 * @return the selectedCancelCirculars
	 */
	public List<CtCancelCircular> getSelectedCancelCirculars() {
		return selectedCancelCirculars;
	}
	
	/**
	 * @param selectedCancelCirculars the selectedCancelCirculars to set
	 */
	public void setSelectedCancelCirculars(List<CtCancelCircular> selectedCancelCirculars) {
		this.selectedCancelCirculars = selectedCancelCirculars;
	}
	
	/**
	 * @return the selectedContractList
	 */
	public List<CtContract> getSelectedContractList() {
		return selectedContractList;
	}
	
	/**
	 * @param selectedContractList the selectedContractList to set
	 */
	public void setSelectedContractList(List<CtContract> selectedContractList) {
		this.selectedContractList = selectedContractList;
	}
	
	/**
	 * @return the selectedCancelledCircularList
	 */
	public List<CtCancelCircular> getSelectedCancelledCircularList() {
		return selectedCancelledCircularList;
	}
	
	/**
	 * @param selectedCancelledCircularList the selectedCancelledCircularList to set
	 */
	public void setSelectedCancelledCircularList(
			List<CtCancelCircular> selectedCancelledCircularList) {
		this.selectedCancelledCircularList = selectedCancelledCircularList;
	}
	
	/**
	 * @return the cancelledCircularList
	 */
	public List<CtCancelCircular> getCancelledCircularList() {
		return cancelledCircularList;
	}
	
	/**
	 * @param cancelledCircularList the cancelledCircularList to set
	 */
	public void setCancelledCircularList(List<CtCancelCircular> cancelledCircularList) {
		this.cancelledCircularList = cancelledCircularList;
	}
	
	/**
	 * @return the updateTypeDisable
	 */
	public boolean isUpdateTypeDisable() {
		return updateTypeDisable;
	}
	
	/**
	 * @param updateTypeDisable the updateTypeDisable to set
	 */
	public void setUpdateTypeDisable(boolean updateTypeDisable) {
		this.updateTypeDisable = updateTypeDisable;
	}
	
	/**
	 * @return the selectedExemptedOrgs
	 */
	public List<CtExemptedOrg> getSelectedExemptedOrgs() {
		return selectedExemptedOrgs;
	}
	
	/**
	 * @param selectedExemptedOrgs the selectedExemptedOrgs to set
	 */
	public void setSelectedExemptedOrgs(List<CtExemptedOrg> selectedExemptedOrgs) {
		this.selectedExemptedOrgs = selectedExemptedOrgs;
	}
	
	/**
	 * @return the selectedCircularContract
	 */
	public CtCircularContract getSelectedCircularContract() {
		return selectedCircularContract;
	}
	
	/**
	 * @param selectedCircularContract the selectedCircularContract to set
	 */
	public void setSelectedCircularContract(CtCircularContract selectedCircularContract) {
		this.selectedCircularContract = selectedCircularContract;
	}
	
	/**
	 * @return the selectedCircularItem
	 */
	public CtCircularItem getSelectedCircularItem() {
		return selectedCircularItem;
	}
	
	/**
	 * @param selectedCircularItem the selectedCircularItem to set
	 */
	public void setSelectedCircularItem(CtCircularItem selectedCircularItem) {
		this.selectedCircularItem = selectedCircularItem;
	}
	
	/**
	 * @return the selectedZoneItems
	 */
	public CtContractZone getSelectedZoneItems() {
		return selectedZoneItems;
	}
	
	/**
	 * @param selectedZoneItems the selectedZoneItems to set
	 */
	public void setSelectedZoneItems(CtContractZone selectedZoneItems) {
		this.selectedZoneItems = selectedZoneItems;
	}
	
	/**
	 * @return the zonePriceMap
	 */
	public Map<CtContractZone, List<CtItemPrice>> getZonePriceMap() {
		if(zonePriceMap==null) {
			zonePriceMap = new HashMap<CtContractZone, List<CtItemPrice>>();
		}
		return zonePriceMap;
	}
	
	/**
	 * @param zonePriceMap the zonePriceMap to set
	 */
	public void setZonePriceMap(Map<CtContractZone, List<CtItemPrice>> zonePriceMap) {
		this.zonePriceMap = zonePriceMap;
	}
	
	/**
	 * @return the chkItemZone
	 */
	public boolean isChkItemZone() {
		return chkItemZone;
	}
	
	/**
	 * @param chkItemZone the chkItemZone to set
	 */
	public void setChkItemZone(boolean chkItemZone) {
		this.chkItemZone = chkItemZone;
	}
	
	/**
	 * @return the chkItemPrice
	 */
	public boolean isChkItemPrice() {
		return chkItemPrice;
	}
	
	/**
	 * @param chkItemPrice the chkItemPrice to set
	 */
	public void setChkItemPrice(boolean chkItemPrice) {
		this.chkItemPrice = chkItemPrice;
	}
	
	/**
	 * @return the userMap
	 */
	public Map<String, PmUser> getUserMap() {
		if(userMap==null) {
			userMap = new HashMap<String, PmUser>();
		}
		return userMap;
	}

	/**
	 * @param userMap the userMap to set
	 */
	public void setUserMap(Map<String, PmUser> userMap) {
		this.userMap = userMap;
	}
	
	/**
	 * @return the centralZoneTrees
	 */
	public TreeNode getCentralZoneTrees() {
		return centralZoneTrees;
	}
	
	/**
	 * @param centralZoneTrees the centralZoneTrees to set
	 */
	public void setCentralZoneTrees(TreeNode centralZoneTrees) {
		this.centralZoneTrees = centralZoneTrees;
	}
	
	/**
	 * @return the contractItemZones
	 */
	public List<CtContractZone> getContractItemZones() {
		return contractItemZones;
	}
	
	/**
	 * @param contractItemZones the contractItemZones to set
	 */
	public void setContractItemZones(List<CtContractZone> contractItemZones) {
		this.contractItemZones = contractItemZones;
	}
	
	/**
	 * @return the selectedTabIndex
	 */
	public int getSelectedTabIndex() {
		return selectedTabIndex;
	}
	
	/**
	 * @param selectedTabIndex the selectedTabIndex to set
	 */
	public void setSelectedTabIndex(int selectedTabIndex) {
		this.selectedTabIndex = selectedTabIndex;
	}
	
	/**
	 * @return the chkCancelPage
	 */
	public boolean isChkCancelPage() {
		return chkCancelPage;
	}
	
	/**
	 * @param chkCancelPage the chkCancelPage to set
	 */
	public void setChkCancelPage(boolean chkCancelPage) {
		this.chkCancelPage = chkCancelPage;
	}
	
	/**
	 * @return the action
	 */
	public String getAction() {
		return action;
	}
	
	/**
	 * @param action the action to set
	 */
	public void setAction(String action) {
		this.action = action;
	}
	
	/**
	 * @return the orgProfiles
	 */
	public List<PmOrgProfile> getOrgProfiles() {
		return orgProfiles;
	}
	
	/**
	 * @param orgProfiles the orgProfiles to set
	 */
	public void setOrgProfiles(List<PmOrgProfile> orgProfiles) {
		this.orgProfiles = orgProfiles;
	}
	
	/**
	 * @return the viewContract
	 */
	public boolean isViewContract() {
		return viewContract;
	}
	
	/**
	 * @param viewContract the viewContract to set
	 */
	public void setViewContract(boolean viewContract) {
		this.viewContract = viewContract;
	}
	
	/**
	 * @return the pmDigiSign
	 */
	public PmDigiSign getPmDigiSign() {
		return pmDigiSign;
	}
	
	/**
	 * @param pmDigiSign the pmDigiSign to set
	 */
	public void setPmDigiSign(PmDigiSign pmDigiSign) {
		this.pmDigiSign = pmDigiSign;
	}
	
	/**
	 * @return the autoUpdate
	 */
	public boolean isAutoUpdate() {
		return autoUpdate;
	}
	
	/**
	 * @param autoUpdate the autoUpdate to set
	 */
	public void setAutoUpdate(boolean autoUpdate) {
		this.autoUpdate = autoUpdate;
	}
	
	/**
	 * @return the textBeforeSign
	 */
	public String getTextBeforeSign() {
		return textBeforeSign;
	}
	
	/**
	 * @param textBeforeSign the textBeforeSign to set
	 */
	public void setTextBeforeSign(String textBeforeSign) {
		this.textBeforeSign = textBeforeSign;
	}
	
	/**
	 * @return the taskName
	 */
	public String getTaskName() {
		return taskName;
	}
	
	/**
	 * @param updateAction the updateAction to set
	 */
	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}
	
	public String getFileDirectory() {
		return fileDirectory;
	}

	public void setFileDirectory(String fileDirectory) {
		this.fileDirectory = fileDirectory;
	}

	/**
	 * @return the circularVerPagingDataModel
	 */
	public IPagingDataModel<CtCircularVer> getCircularVerPagingDataModel() {
		return circularVerPagingDataModel;
	}
	
	/**
	 * @param circularVerPagingDataModel the circularVerPagingDataModel to set
	 */
	public void setCircularVerPagingDataModel(IPagingDataModel<CtCircularVer> circularVerPagingDataModel) {
		this.circularVerPagingDataModel = circularVerPagingDataModel;
	}
	
	/**
	 * @return the fromCircularNotice
	 */
	public boolean isFromCircularNotice() {
		return fromCircularNotice;
	}
	
	/**
	 * @param fromCircularNotice the fromCircularNotice to set
	 */
	public void setFromCircularNotice(boolean fromCircularNotice) {
		this.fromCircularNotice = fromCircularNotice;
	}

	public boolean isCircularExtension() {
		return circularExtension;
	}

	public void setCircularExtension(boolean circularExtension) {
		this.circularExtension = circularExtension;
	}



	/**
	 * Pagination for central circular search list 
	 */
	private class CircularVerPagingDataModel extends PagingDataModel<CtCircularVer> {
		private static final long serialVersionUID = 1L;

		public CircularVerPagingDataModel() {
			super("circularVerDt", false);
		}
		
		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * my.ep.web.bean.paginator.PagingDataModel#pageChangeListener(my.ep
		 * .biz.bean.search.ISearchBean)
		 */
		@Override
		public ISearchResultBean<CtCircularVer> pageChangeListener(ISearchBean<CtCircularVer> searchBean) {
			try {				
				searchBean.getDataFilterBeans().add(new SearchFilterBean("ctCircular.circularType",SearchOperator.OPERATOR_EQUALS.value(),ContractConst.CIRCULAR_DOC_TYPE_CENTRAL_CONTRACT));
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
				if(getSearchPhysicalCircularNo()!=null && getSearchPhysicalCircularNo().length()>0) {
					SearchFilterBean filterBean = new SearchFilterBean("circularNo",
													SearchOperator.OPERATOR_LIKE.value(),getSearchPhysicalCircularNo());
					filterBean.setCaseInsensitive(true);
					searchBean.getDataFilterBeans().add(filterBean);
				}
				if(getSearchStartDate()!=null && getSearchEndDate()!=null) {
				    searchBean.getDataFilterBeans().add(new SearchFilterBean("startDate",SearchOperator.OPERATOR_GREATER_EQUAL.value(), getSearchStartDate()));	
				    searchBean.getDataFilterBeans().add(new SearchFilterBean("endDate",SearchOperator.OPERATOR_LESS_EQUAL.value(), getSearchEndDate()));		
				}
				if(getSearchStartDate()!=null && getSearchEndDate()==null) {
					searchBean.getDataFilterBeans().add(new SearchFilterBean("startDate",SearchOperator.OPERATOR_EQUALS.value(), getSearchStartDate()));	
			    }
				if(getSearchStartDate()==null && getSearchEndDate()!=null) {
					searchBean.getDataFilterBeans().add(new SearchFilterBean("endDate",SearchOperator.OPERATOR_EQUALS.value(), getSearchEndDate()));	
				}
		    
				if(getSearchStatusId()!=null) {
					//Filter out cancelled status
					if(getSearchStatusId().longValue() == ContractStatusConst.CQ_APPROVED || 
							getSearchStatusId().longValue() == ContractStatusConst.CQ_REJECTED)
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
							circularVer.getCtCircular().setLatestCircularVer(
									circularSO.getLatestCircularVer(circularReqDTO).getCircularVer());
						*/	
							if(circularVer.getCtCircular().getRecordStatusFlg() == DbValueConst.RECORD_STATUS_CANCELLED) {
								PmStatus cancelStatus = new PmStatus();
								cancelStatus.setStatusId(0L);
								circularVer.setStatus(cancelStatus);
							}
							//if(circularVer.getStatus() != null && circularVer.getStatus().getStatusId().equals(ContractStatusConst.CQ_REJECTED)) {
								if(circularVer.getCtCircular().getLatestCircularVer() != null)
									circularVer.setCircularVerId(circularVer.getCtCircular().getLatestCircularVer().getCircularVerId());
							//}
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
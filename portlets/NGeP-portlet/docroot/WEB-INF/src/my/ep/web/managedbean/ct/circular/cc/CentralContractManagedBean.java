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
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.ct.ContractConst;
import my.ep.constant.ct.ContractStatusConst;
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
 * @Created 9/10/2013
 */
@ManagedBean
@SessionScoped
public class CentralContractManagedBean extends BaseManagedBean {

	private static final long serialVersionUID = 1L;

	private String circularAdmin;
	private String action;
	private String searchCircularNo;
	private String searchCircularName;
	private String searchPhysicalCircularNo;
	private String searchContractNo;
	private String searchContractName;
	
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
	private TreeNode centralZoneTrees;
	
	private PmDigiSign pmDigiSign;
	private boolean autoUpdate;
	private String textBeforeSign;
	private String taskName;

	private IPagingDataModel<CtCircularVer> circularVerPagingDataModel = new CircularVerPagingDataModel();
	
	public boolean isUpdateAction() {
		return updateAction;
	}
	public void setUpdateAction(boolean updateAction) {
		this.updateAction = updateAction;
	}
	public CtCircularVer getCircularVer() {
		return circularVer;
	}
	public void setCircularVer(CtCircularVer circularVer) {
		this.circularVer = circularVer;
	}
	public DualListModel<String> getApproverModel() {
		return approverModel;
	}
	public void setApproverModel(DualListModel<String> approverModel) {
		this.approverModel = approverModel;
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
	public Long getSearchStatusId() {
		return searchStatusId;
	}
	public void setSearchStatusId(Long searchStatusId) {
		this.searchStatusId = searchStatusId;
	}
	public String getSearchContractNo() {
		return searchContractNo;
	}
	public void setSearchContractNo(String searchContractNo) {
		this.searchContractNo = searchContractNo;
	}
	public String getSearchContractName() {
		return searchContractName;
	}
	public void setSearchContractName(String searchContractName) {
		this.searchContractName = searchContractName;
	}
	public CtCircular getCircular() {
		return circular;
	}
	public void setCircular(CtCircular circular) {
		this.circular = circular;
	}
	public List<CtContract> getContractList() {
		if(contractList==null) {
			contractList = new ArrayList<CtContract>();
		}
		return contractList;
	}
	public void setContractList(List<CtContract> contractList) {
		this.contractList = contractList;
	}
	
	public List<CtRemark> getRemarkList() {
		if(remarkList == null)
			remarkList = new ArrayList<CtRemark>();
		return remarkList;
	}

	public void setRemarkList(List<CtRemark> remarkList) {
		this.remarkList = remarkList;
	}

	public List<CtAttachment> getAttachmentList() {
		if(attachmentList == null)
		   attachmentList = new ArrayList<CtAttachment>();
		return attachmentList;
	}

	public void setAttachmentList(List<CtAttachment> attachmentList) {
		this.attachmentList = attachmentList;
	}
	public Map<Long, PmUserGroup> getUserGroupMap() {
		return userGroupMap;
	}
	public void setUserGroupMap(Map<Long, PmUserGroup> userGroupMap) {
		this.userGroupMap = userGroupMap;
	}
	public String getCircularAdmin() {
		return circularAdmin;
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
	public List<CtContract> getSelectedContractList() {
		return selectedContractList;
	}
	public void setSelectedContractList(List<CtContract> selectedContractList) {
		this.selectedContractList = selectedContractList;
	}
	public List<CtCancelCircular> getSelectedCancelledCircularList() {
		return selectedCancelledCircularList;
	}
	public void setSelectedCancelledCircularList(
			List<CtCancelCircular> selectedCancelledCircularList) {
		this.selectedCancelledCircularList = selectedCancelledCircularList;
	}
	public List<CtCancelCircular> getCancelledCircularList() {
		return cancelledCircularList;
	}
	public void setCancelledCircularList(List<CtCancelCircular> cancelledCircularList) {
		this.cancelledCircularList = cancelledCircularList;
	}
	public boolean isUpdateTypeDisable() {
		return updateTypeDisable;
	}
	public void setUpdateTypeDisable(boolean updateTypeDisable) {
		this.updateTypeDisable = updateTypeDisable;
	}
	
	public List<CtExemptedOrg> getSelectedExemptedOrgs() {
		return selectedExemptedOrgs;
	}
	public void setSelectedExemptedOrgs(List<CtExemptedOrg> selectedExemptedOrgs) {
		this.selectedExemptedOrgs = selectedExemptedOrgs;
	}
	
	public CtCircularContract getSelectedCircularContract() {
		return selectedCircularContract;
	}
	public void setSelectedCircularContract(CtCircularContract selectedCircularContract) {
		this.selectedCircularContract = selectedCircularContract;
	}
	
	public CtCircularItem getSelectedCircularItem() {
		return selectedCircularItem;
	}
	public void setSelectedCircularItem(CtCircularItem selectedCircularItem) {
		this.selectedCircularItem = selectedCircularItem;
	}
	
	public CtContractZone getSelectedZoneItems() {
		return selectedZoneItems;
	}
	public void setSelectedZoneItems(CtContractZone selectedZoneItems) {
		this.selectedZoneItems = selectedZoneItems;
	}
	public Map<CtContractZone, List<CtItemPrice>> getZonePriceMap() {
		if(zonePriceMap==null) {
			zonePriceMap = new HashMap<CtContractZone, List<CtItemPrice>>();
		}
		return zonePriceMap;
	}
	public void setZonePriceMap(Map<CtContractZone, List<CtItemPrice>> zonePriceMap) {
		this.zonePriceMap = zonePriceMap;
	}
	public boolean isChkItemZone() {
		return chkItemZone;
	}
	public void setChkItemZone(boolean chkItemZone) {
		this.chkItemZone = chkItemZone;
	}
	public boolean isChkItemPrice() {
		return chkItemPrice;
	}
	public void setChkItemPrice(boolean chkItemPrice) {
		this.chkItemPrice = chkItemPrice;
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
	public TreeNode getCentralZoneTrees() {
		return centralZoneTrees;
	}
	public void setCentralZoneTrees(TreeNode centralZoneTrees) {
		this.centralZoneTrees = centralZoneTrees;
	}
	
	public List<CtContractZone> getContractItemZones() {
		return contractItemZones;
	}
	public void setContractItemZones(List<CtContractZone> contractItemZones) {
		this.contractItemZones = contractItemZones;
	}
	public int getSelectedTabIndex() {
		return selectedTabIndex;
	}
	public void setSelectedTabIndex(int selectedTabIndex) {
		this.selectedTabIndex = selectedTabIndex;
	}
	public boolean isChkCancelPage() {
		return chkCancelPage;
	}
	public void setChkCancelPage(boolean chkCancelPage) {
		this.chkCancelPage = chkCancelPage;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public List<PmOrgProfile> getOrgProfiles() {
		return orgProfiles;
	}
	public void setOrgProfiles(List<PmOrgProfile> orgProfiles) {
		this.orgProfiles = orgProfiles;
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
	public String getTextBeforeSign() {
		return textBeforeSign;
	}
	public void setTextBeforeSign(String textBeforeSign) {
		this.textBeforeSign = textBeforeSign;
	}
	public String getTaskName() {
		return taskName;
	}
	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}
	public IPagingDataModel<CtCircularVer> getCircularVerPagingDataModel() {
		return circularVerPagingDataModel;
	}
	public void setCircularVerPagingDataModel(IPagingDataModel<CtCircularVer> circularVerPagingDataModel) {
		this.circularVerPagingDataModel = circularVerPagingDataModel;
	}
	
	private class CircularVerPagingDataModel extends PagingDataModel<CtCircularVer> {
		private static final long serialVersionUID = 1L;

		public CircularVerPagingDataModel() {
			super("circularVerDt");
		}
		
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
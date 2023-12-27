/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.managedbean.ct.amendment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.primefaces.model.DualListModel;

import com.liferay.compat.portal.util.PortalUtil;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.ct.IContractSO;
import my.ep.biz.service.ct.dtl.IContractItemSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.ct.ContractConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.dto.ct.ContractReqDTO;
import my.ep.dto.ct.dtl.ContractItemReqDTO;
import my.ep.dto.ct.dtl.ContractItemResDTO;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.ct.CtAttachment;
import my.ep.persistence.entity.ct.CtContractVer;
import my.ep.persistence.entity.ct.CtDisciplinaryAction;
import my.ep.persistence.entity.ct.CtRemark;
import my.ep.persistence.entity.ct.amend.CtCommitteeMember;
import my.ep.persistence.entity.ct.dtl.item.CtContractItem;
import my.ep.persistence.entity.ct.dtl.item.CtItemCeilingQty;
import my.ep.persistence.entity.ct.dtl.item.CtItemMaxQty;
import my.ep.persistence.entity.ct.dtl.item.CtItemPrice;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmDigiSign;
import my.ep.persistence.entity.pm.common.PmUom;
import my.ep.persistence.entity.sc.loaloi.ScLoaReport;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmSupplierBank; // Fix - #33674
import my.ep.type.aut.SearchOperator;
import my.ep.web.backingbean.ct.ContractBackingBean;
import my.ep.web.backingbean.ct.circular.CircularBackingBean;
import my.ep.web.backingbean.pm.cc.SpkiBackingBean;
import my.ep.web.bean.ct.CtLetterOfAcceptance;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.vo.DisciplinaryActionVO;

/**
 * ManagedBean For Contract Amendment
 * 
 * @author Alam
 * @since 1/13/2014	 
 */
@ManagedBean(name ="amendManagedBean")
@SessionScoped
public class AmendmentManagedBean extends BaseManagedBean {

	private static final long serialVersionUID = 1L;
	private  boolean readOnly = true;
	private  boolean resetval = true;
	
	private String taskName;
	private String ackFA;
	private String committeeName = null;
	private String identificationNo = null;
	private String identificationType;
	private String fileDirectory;
	private String pbQcApproved;
	private String mofApproved;
	private String textBeforeSign;
	private String buttonAction;
	private String committeeType;
	private String approverName;
	private Long meetingStatusId;
	private Long secretaryId;
	private Date newAmendmentEffDate;
	private int selectedTabIndex;
	private int attachFileIndex;
	private boolean itemProductExist;
	private boolean itemServiceExist;
	private boolean termAndConditionFlg;
	private boolean pbConfirmMeeting;
	private boolean memberSignOff;
	private boolean letterOfAcceptance;
	private boolean prepareLOA;
	private boolean signOffDecision;
	private boolean signUpdateDecision;
	private boolean decisionPbQcLOA;
	private boolean confirmMeeting;
	private boolean selectAll;
	private boolean allowAcknowledge = false;
	private boolean autoUpdate = true;	
	private boolean disableEndorseAmendDetails;
	private boolean disableFinalAmendDetails;
	private boolean disableMeetingDetails;
	private boolean disableLoaLetter;
	private List<CtRemark> remarkList;
	private List<CtAttachment> attachmentList;
	private List<CtContractVer> amendHistoryList;
	private List<CtLetterOfAcceptance> ctLetterOfAcceptance;
	private List<String> finalListOfSideMenu;
	private List<SmPersonnel> supplierList;
	private DualListModel<PmUser> approverModel;
	private Map<Long, PmUser> userMap;
	private CtContractItem selectedServiceItem;
	private CtContractVer agreementContractVer;
	private CtItemMaxQty ctItemMaxQty;
	private CtItemPrice ctItemPrices;
	private CtItemCeilingQty ctItemCeilingQtys;
	private CtContractItem selectedItem;
	private CtItemPrice ctServiceItemPrices;
	private CtCommitteeMember newCommitteMembers;
	private ScLoaReport selectedScLoaReports;
	private PmDigiSign pmDigiSign;
	private SelectItem[] secretaryList;
	private List<PmUser> contractAdminlist;
	
	private String contractId;
	private String itemname;
	private String itemtype;
	private String itemcode;
	private String itemstatus;
	private String skuno;
	private String UOM;
	private String typeofgoods;
	private  boolean servicecollapse=false;
	private  boolean prodcollapse=false;
	private IPagingDataModel<CtContractItem> contractListingPagingDataModel = new ContractListingPagingDataModel();
	private IPagingDataModel<CtContractItem> ServiceListingPagingDataModel =  new ServiceListingPagingDataModel();
	
	//Enhancement #26186 start
	private boolean disciplinaryAction; 
	private String disciplinaryActionMsg; 
	private List<DisciplinaryActionVO> disciplineVOs; 
	private Long disciplinaryActionId;
	private boolean disciplinaryApproved;
	private String disciplinaryApprovedFlag;
	private String justification;
	//Enhancement #26186 end
	//#29793 start
	private boolean disciplinaryActionNotification;
	private boolean disciplinaryActionCancel;
	private Date disciplinaryActionStartDate;
	private Date disciplinaryActionEndDate;
	//#29793 end
	
	private boolean itemProductNotExist;
	private boolean itemServiceNotExist;
	
	//26753 - start
	private boolean updateCommittee;
	private List<CtCommitteeMember> updatedCommitteeMembers;
	private List<CtCommitteeMember> filteredUpdatedCommitteeMembers;
	private boolean hasChanges;
	//26753 - end
	@ManagedProperty(value="#{spkiBackingBean}")
	private SpkiBackingBean spkiBackingBean;
	
	//20997
	private PmUom selectedSearchItem;
	
	//25956
	private boolean viewOnly = false; /*#25956 notification setting*/
	
	private boolean disableDecisionDetails; //#29549
	
	//Enhancement 15851 - shaqimi start
	/** The back to previous page. */
	private String backToPreviousPage;

	/** The tracking dairy view. */
	private boolean trackingDairyView;
	
	private Map<String, Integer> indexing;
	
	//Enhancement 15851 - shaqimi end
	private String supplementalLoaNo = null;//Bug 33096 - shaqimi
	
	private String backToNtf; //25956
	
//	private boolean isFromViewPage = false; // Fix - #34110
	
	public void reassignValue(boolean value) 
	{
		readOnly = value;	
		resetval= value;
		servicecollapse=value;
		prodcollapse=value;

	}
	public void prodValue(boolean value) 
	{
		servicecollapse=false;
		prodcollapse=value;
		
	}
	public void serviceValue(boolean value) 
	{
		servicecollapse=value;
		prodcollapse=false;
		
	}
	public void allValue(boolean value) 
	{
		servicecollapse=value;
		prodcollapse=value;
		
	}
	// Start Fix - #33256
	private Map<Long, CtItemMaxQty> selectedChangeItemQuantity;
	
	private Map<Long, CtItemPrice> selectedChangeItemPrice;
	// End Fix - #33256
	
private class ServiceListingPagingDataModel extends PagingDataModel<CtContractItem> {
		
		private static final long serialVersionUID = 1L;
		
		private  Logger log = EpLogger.getLogger(ServiceListingPagingDataModel.class);
		
		public ServiceListingPagingDataModel() {
			super("amenditemServiceTable");
		}
		
		public ServiceListingPagingDataModel(String table) {
			super(table, false);
		}
		@Override
		public ISearchResultBean<CtContractItem> pageChangeListener(ISearchBean<CtContractItem> searchBean) {
			try {		
					
				IContractItemSO contractItemSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, IContractItemSO.class);
				ContractItemResDTO resDTO = new ContractItemResDTO();
				ContractItemReqDTO reqDTO = new ContractItemReqDTO();
				
				ContractBackingBean contractBb=new ContractBackingBean();
				Long contId=contractBb.getManagedBean().getContractVer().getCtContract().getContractId();
				// Start - #29507 Fixing
				Long contractVerId=null;				
				log.info("ServiceListingPagingDataModel");
				log.info("getTaskId : "+getTaskId());
								
				if(contractBb.getManagedBean().getContractVer().getCtContract().getCurrentContractVer().getContractVerId()
				.equals(contractBb.getManagedBean().getContractVer().getCtContract().getLatestContractVer().getContractVerId()) && getTaskId()== null){
					
					contractVerId=contractBb.getManagedBean().getContractVer().getCtContract().getLatestContractVer().getContractVerId();
					log.info("maintenance screen  :: contractVer :: >>> "+contractVerId);
				}
//				isFromViewPage = contractBb.getManagedBean().isViewContract(); //Fix - #34110
				//if contract from tasklist taskid != null
				if(getTaskId()!= null){
					
					contractVerId=contractBb.getManagedBean().getContractVer().getCtContract().getCurrentContractVer().getContractVerId();
					log.info("tasklist screen  :: contractVer :: >>> "+contractVerId);
				}else {
					
					contractVerId=contractBb.getManagedBean().getContractVer().getCtContract().getLatestContractVer().getContractVerId();
					log.info("maintenance screen  :: contractVer :: >>> "+contractVerId);
				}
				// End - #29507 Fixing
				
				  if(contractVerId!=null ){						
						SearchFilterBean filterBean = new SearchFilterBean("ctContractVer.contractVerId", SearchOperator.OPERATOR_EQUALS.value(),contractVerId);
						searchBean.getDataFilterBeans().add(filterBean);
					}
				
			  if(contId!=null ) {
					SearchFilterBean filterBean = new SearchFilterBean("ctContractVer.ctContract.contractId", SearchOperator.OPERATOR_EQUALS.value(),contId);
					searchBean.getDataFilterBeans().add(filterBean);
				}

				if(getItemname()!=null && getItemname().length()>0) {					
					SearchFilterBean filterBean = new SearchFilterBean("scRequestItem.itemName", SearchOperator.OPERATOR_LIKE.value(),getItemname());
					filterBean.setCaseInsensitive(true);
					searchBean.getDataFilterBeans().add(filterBean);
				}
				
				SearchFilterBean filterBean1 = new SearchFilterBean("itemType.parameterCode", SearchOperator.OPERATOR_LIKE.value(),ParameterConst.ITEM_TYPE_SERVICE);
				filterBean1.setCaseInsensitive(true);
				searchBean.getDataFilterBeans().add(filterBean1);

				if(getItemcode()!=null && getItemcode().length()>0 ) {					
					SearchFilterBean filterBean = new SearchFilterBean("scRequestItem.cmItem.extensionCode",	SearchOperator.OPERATOR_LIKE.value(),getItemcode());
					filterBean.setCaseInsensitive(true);
					searchBean.getDataFilterBeans().add(filterBean);	
				}
				if(getItemstatus()!=null && getItemstatus().length()>0) {				
					if(getItemstatus().equals(ParameterConst.RECORD_STATUS_INACTIVE) || getItemstatus().equals(ParameterConst.RECORD_STATUS_ACTIVE)){
						SearchFilterBean filterBean = new SearchFilterBean("recordStatus", SearchOperator.OPERATOR_LIKE.value(),getItemstatus()); // Fix - #31042
						filterBean.setCaseInsensitive(true);
						searchBean.getDataFilterBeans().add(filterBean);
					}
					if(getItemstatus().equals(ParameterConst.RECORD_STATUS_ALL)){
						SearchFilterBean filterBean = new SearchFilterBean(SearchOperator.OPERATOR_IN.value());
						filterBean.setSearchField("recordStatusFlg"); // Fix - #31042
						filterBean.setSearchValue(new ArrayList<String>(Arrays.asList(ParameterConst.RECORD_STATUS_INACTIVE, ParameterConst.RECORD_STATUS_ACTIVE)));
						searchBean.getDataFilterBeans().add(filterBean);
					}
				}

				//#20997 - Start-Fixing
				if(getSelectedSearchItem().getUomName()!=null && getSelectedSearchItem().getUomName().trim().length()>0) {
					
					SearchFilterBean filterBean = new SearchFilterBean("scRequestItem.uom.uomName", SearchOperator.OPERATOR_LIKE.value(),getSelectedSearchItem().getUomName());
					filterBean.setCaseInsensitive(true);
					searchBean.getDataFilterBeans().add(filterBean);
				}
				//#20997 - End -Fixing
				if(getSkuno()!=null && getSkuno().length()>0 ) {
					SearchFilterBean filterBean = new SearchFilterBean("sku", SearchOperator.OPERATOR_LIKE.value(),getSkuno());
					filterBean.setCaseInsensitive(true);
					searchBean.getDataFilterBeans().add(filterBean);
				}
				if(getTypeofgoods()!=null && getTypeofgoods().length()>0) {					
					if(getTypeofgoods().equals(ParameterConst.LOCAL_GOODS_ID) || getTypeofgoods().equals(ParameterConst.IMPORTED_GOODS_ID)){
						SearchFilterBean filterBean = new SearchFilterBean("itemOrigin.parameterId", SearchOperator.OPERATOR_EQUALS.value(),getTypeofgoods());
						searchBean.getDataFilterBeans().add(filterBean);					
					}
					if(getTypeofgoods().equals(ParameterConst.BOTH_GOODS_ID)){
						SearchFilterBean filterBean = new SearchFilterBean(SearchOperator.OPERATOR_IN.value());
						filterBean.setSearchField("itemOrigin.parameterId");
						filterBean.setSearchValue(new ArrayList<String>(Arrays.asList(ParameterConst.LOCAL_GOODS_ID, ParameterConst.IMPORTED_GOODS_ID)));
						searchBean.getDataFilterBeans().add(filterBean);
					}
				}
			
				reqDTO.setContractItemSearchBean(searchBean);
				
				if(isResetval()){
					if(contractBb.getManagedBean().getContractVer().getCtContract().getContractType().getParameterCode().equals(ParameterConst.CONTRACT_TYPE_CENTRAL)){
						if(getItemtype()!=null && getItemtype().length()>0){
	                		if(getItemtype().equals(ParameterConst.ITEM_TYPE_SERVICE)){
								setServicecollapse(isResetval());
	                		}
                		}else{
                			setServicecollapse(isResetval());
                		}
                	}
					
					if(isServicecollapse()){
				
						resDTO=contractItemSO.searchProductServiceItemDtls(reqDTO);
					    resDTO.getContractItemResultBean();

					    if(resDTO.getContractItemResultBean().getResultList().size()>0){
							//readOnly=false;
							return resDTO.getContractItemResultBean();
					    }
					    
						
	                }
                }
			} catch (Exception e) {
				e.printStackTrace();
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
			}
			return null;
		}
	}


private class ContractListingPagingDataModel extends PagingDataModel<CtContractItem> {
	private static final long serialVersionUID = 1L;
	
	private Logger log = EpLogger.getLogger(ContractListingPagingDataModel.class);
	public ContractListingPagingDataModel() {
		super("amenditemTable");
	}
	public ContractListingPagingDataModel(String table) {
		super(table, false);
	}

	@Override
	public ISearchResultBean<CtContractItem> pageChangeListener(ISearchBean<CtContractItem> searchBean) {
		try {			
			
			IContractItemSO contractItemSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, IContractItemSO.class);
			ContractItemResDTO resDTO = new ContractItemResDTO();
			ContractItemReqDTO reqDTO = new ContractItemReqDTO();
			
			ContractBackingBean contractBb=new ContractBackingBean();
			Long contId=contractBb.getManagedBean().getContractVer().getCtContract().getContractId();
			
			Long contractVerId=null;
			// Start - #29507 Fixing
			log.info("ContractListingPagingDataModel");
			log.info("getTaskId : "+getTaskId());
			
			if(contractBb.getManagedBean().getContractVer().getCtContract().getCurrentContractVer().getContractVerId()
			.equals(contractBb.getManagedBean().getContractVer().getCtContract().getLatestContractVer().getContractVerId()) && getTaskId()== null){
				
				contractVerId=contractBb.getManagedBean().getContractVer().getCtContract().getLatestContractVer().getContractVerId();
				log.info("maintenance screen  :: contractVer :: >>> "+contractVerId);
			}
			//if contract from tasklist taskid != null
			if(getTaskId()!= null){
				
				contractVerId=contractBb.getManagedBean().getContractVer().getCtContract().getCurrentContractVer().getContractVerId();
				log.info("tasklist screen  :: contractVer :: >>> "+contractVerId);
			}else {
				
				contractVerId=contractBb.getManagedBean().getContractVer().getCtContract().getLatestContractVer().getContractVerId();
				log.info("maintenance screen  :: contractVer :: >>> "+contractVerId);
			}
//			isFromViewPage = contractBb.getManagedBean().isViewContract(); //Fix - #34110
			// End - #29507 Fixing
			  if(contractVerId!=null ){					
					SearchFilterBean filterBean = new SearchFilterBean("ctContractVer.contractVerId", SearchOperator.OPERATOR_EQUALS.value(),contractVerId);
					searchBean.getDataFilterBeans().add(filterBean);
				}
			  
			
		  if(contId!=null ) {
				SearchFilterBean filterBean = new SearchFilterBean("ctContractVer.ctContract.contractId", SearchOperator.OPERATOR_EQUALS.value(),contId);
				searchBean.getDataFilterBeans().add(filterBean);
			}
			if(getItemname()!=null && getItemname().length()>0) {					
					SearchFilterBean filterBean = new SearchFilterBean("scRequestItem.itemName", SearchOperator.OPERATOR_LIKE.value(),getItemname());
					filterBean.setCaseInsensitive(true);
					searchBean.getDataFilterBeans().add(filterBean);
				}
	
			 SearchFilterBean filterBean1 = new SearchFilterBean("itemType.parameterCode", SearchOperator.OPERATOR_LIKE.value(),ParameterConst.ITEM_TYPE_PRODUCT);
			 filterBean1.setCaseInsensitive(true);
			 searchBean.getDataFilterBeans().add(filterBean1);
			
			if(getItemcode()!=null && getItemcode().length()>0 ) {				
				SearchFilterBean filterBean = new SearchFilterBean("scRequestItem.cmItem.extensionCode",	SearchOperator.OPERATOR_LIKE.value(),getItemcode());
				filterBean.setCaseInsensitive(true);
				searchBean.getDataFilterBeans().add(filterBean);	
			}
			if(getItemstatus()!=null && getItemstatus().length()>0) {				
				if(getItemstatus().equals(ParameterConst.RECORD_STATUS_INACTIVE) || getItemstatus().equals(ParameterConst.RECORD_STATUS_ACTIVE)){					
					SearchFilterBean filterBean = new SearchFilterBean("recordStatus", SearchOperator.OPERATOR_LIKE.value(),getItemstatus()); // Fix -  #31042
					filterBean.setCaseInsensitive(true);
					searchBean.getDataFilterBeans().add(filterBean);
				}
				if(getItemstatus().equals(ParameterConst.RECORD_STATUS_ALL)){					
					SearchFilterBean filterBean = new SearchFilterBean(SearchOperator.OPERATOR_IN.value());
					filterBean.setSearchField("recordStatusFlg"); // Fix -  #31042
					filterBean.setSearchValue(new ArrayList<String>(Arrays.asList(ParameterConst.RECORD_STATUS_INACTIVE, ParameterConst.RECORD_STATUS_ACTIVE)));
					searchBean.getDataFilterBeans().add(filterBean);
				}
			}
			
			//#20997 - Start-Fixing
			if(getSelectedSearchItem().getUomName()!=null && getSelectedSearchItem().getUomName().trim().length()>0) {
				
				SearchFilterBean filterBean = new SearchFilterBean("scRequestItem.uom.uomName", SearchOperator.OPERATOR_LIKE.value(),getSelectedSearchItem().getUomName());
				filterBean.setCaseInsensitive(true);
				searchBean.getDataFilterBeans().add(filterBean);
			}
			//#20997 - End -Fixing
			if(getSkuno()!=null && getSkuno().length()>0 ) {				
				SearchFilterBean filterBean = new SearchFilterBean("sku", SearchOperator.OPERATOR_LIKE.value(),getSkuno());
				filterBean.setCaseInsensitive(true);
				searchBean.getDataFilterBeans().add(filterBean);
			}
			if(getTypeofgoods()!=null && getTypeofgoods().length()>0) {				
				if(getTypeofgoods().equals(ParameterConst.LOCAL_GOODS_ID) || getTypeofgoods().equals(ParameterConst.IMPORTED_GOODS_ID)){					
					SearchFilterBean filterBean = new SearchFilterBean("itemOrigin.parameterId", SearchOperator.OPERATOR_EQUALS.value(),getTypeofgoods());
					searchBean.getDataFilterBeans().add(filterBean);
				
				}
				if(getTypeofgoods().equals(ParameterConst.BOTH_GOODS_ID)){
					SearchFilterBean filterBean = new SearchFilterBean(SearchOperator.OPERATOR_IN.value());
					filterBean.setSearchField("itemOrigin.parameterId");
					filterBean.setSearchValue(new ArrayList<String>(Arrays.asList(ParameterConst.LOCAL_GOODS_ID, ParameterConst.IMPORTED_GOODS_ID)));
					searchBean.getDataFilterBeans().add(filterBean);
				}
			}
		

			reqDTO.setContractItemSearchBean(searchBean);

            if(isResetval()){
            	if(contractBb.getManagedBean().getContractVer().getCtContract().getContractType().getParameterCode().equals(ParameterConst.CONTRACT_TYPE_CENTRAL)){
            		if(getItemtype()!=null && getItemtype().length()>0){
                		if(getItemtype().equals(ParameterConst.ITEM_TYPE_PRODUCT)){
	                		setProdcollapse(isResetval());
                		}	
            		}else{
            			setProdcollapse(isResetval());
            		}
            	}
            	
            	if(isProdcollapse()){
				
				resDTO=contractItemSO.searchProductServiceItemDtls(reqDTO);
				if(resDTO.getContractItemResultBean().getResultList().size()>0) {
//			    	 readOnly=false;
			    	 return resDTO.getContractItemResultBean();
			    }

				
			}
          }
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}
		return null;
	}
}


	/**
	 * @return the taskName
	 */
	public String getTaskName() {
		return taskName;
	}

	/**
	 * @param taskName the taskName to set
	 */
	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	/**
	 * @return the ackFA
	 */
	public String getAckFA() {
		return ackFA;
	}

	/**
	 * @param ackFA the ackFA to set
	 */
	public void setAckFA(String ackFA) {
		this.ackFA = ackFA;
	}

	/**
	 * @return the committeeName
	 */
	public String getCommitteeName() {
		return committeeName;
	}

	/**
	 * @param committeeName the committeeName to set
	 */
	public void setCommitteeName(String committeeName) {
		this.committeeName = committeeName;
	}
	/**
	 * @return the identificationNo
	 */
	public String getIdentificationNo() {
		return identificationNo;
	}
	/**
	 * @param identificationNo the identificationNo to set
	 */
	public void setIdentificationNo(String identificationNo) {
		this.identificationNo = identificationNo;
	}
	/**
	 * @return the identificationType
	 */
	public String getIdentificationType() {
		return identificationType;
	}
	/**
	 * @param identificationType the identificationType to set
	 */
	public void setIdentificationType(String identificationType) {
		this.identificationType = identificationType;
	}
	/**
	 * @return the fileDirectory
	 */
	public String getFileDirectory() {
		return fileDirectory;
	}

	/**
	 * @param fileDirectory the fileDirectory to set
	 */
	public void setFileDirectory(String fileDirectory) {
		this.fileDirectory = fileDirectory;
	}

	/**
	 * @return the pbQcApproved
	 */
	public String getPbQcApproved() {
		return pbQcApproved;
	}
	/**
	 * @param pbQcApproved the pbQcApproved to set
	 */
	public void setPbQcApproved(String pbQcApproved) {
		this.pbQcApproved = pbQcApproved;
	}	

	/**
	 * @return the mofApproved
	 */
	public String getMofApproved() {
		return mofApproved;
	}

	/**
	 * @param mofApproved the mofApproved to set
	 */
	public void setMofApproved(String mofApproved) {
		this.mofApproved = mofApproved;
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
	 * @return the buttonAction
	 */
	public String getButtonAction() {
		return buttonAction;
	}

	/**
	 * @param buttonAction the buttonAction to set
	 */
	public void setButtonAction(String buttonAction) {
		this.buttonAction = buttonAction;
	}
	/**
	 * @return the committeeType
	 */
	public String getCommitteeType() {
		return committeeType;
	}

	/**
	 * @param committeeType the committeeType to set
	 */
	public void setCommitteeType(String committeeType) {
		this.committeeType = committeeType;
	}

	/**
	 * @return the meetingStatusId
	 */
	public Long getMeetingStatusId() {
		return meetingStatusId;
	}
	/**
	 * @param meetingStatusId the meetingStatusId to set
	 */
	public void setMeetingStatusId(Long meetingStatusId) {
		this.meetingStatusId = meetingStatusId;
	}

	/**
	 * @return the secretaryId
	 */
	public Long getSecretaryId() {
		return secretaryId;
	}

	/**
	 * @param secretaryId the secretaryId to set
	 */
	public void setSecretaryId(Long secretaryId) {
		this.secretaryId = secretaryId;
	}

	/**
	 * @return the newAmendmentEffDate
	 */
	public Date getNewAmendmentEffDate() {
		return newAmendmentEffDate;
	}

	/**
	 * @param newAmendmentEffDate the newAmendmentEffDate to set
	 */
	public void setNewAmendmentEffDate(Date newAmendmentEffDate) {
		this.newAmendmentEffDate = newAmendmentEffDate;
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
	 * @return the attachFileIndex
	 */
	public int getAttachFileIndex() {
		return attachFileIndex;
	}

	/**
	 * @param attachFileIndex the attachFileIndex to set
	 */
	public void setAttachFileIndex(int attachFileIndex) {
		this.attachFileIndex = attachFileIndex;
	}

	/**
	 * @return the itemProductExist
	 */
	public boolean isItemProductExist() {
		return itemProductExist;
	}

	/**
	 * @param itemProductExist the itemProductExist to set
	 */
	public void setItemProductExist(boolean itemProductExist) {
		this.itemProductExist = itemProductExist;
	}

	/**
	 * @return the itemServiceExist
	 */
	public boolean isItemServiceExist() {
		return itemServiceExist;
	}

	/**
	 * @param itemServiceExist the itemServiceExist to set
	 */
	public void setItemServiceExist(boolean itemServiceExist) {
		this.itemServiceExist = itemServiceExist;
	}

	/**
	 * @return the termAndConditionFlg
	 */
	public boolean isTermAndConditionFlg() {
		return termAndConditionFlg;
	}

	/**
	 * @param termAndConditionFlg the termAndConditionFlg to set
	 */
	public void setTermAndConditionFlg(boolean termAndConditionFlg) {
		this.termAndConditionFlg = termAndConditionFlg;
	}

	/**
	 * @return the pbConfirmMeeting
	 */
	public boolean isPbConfirmMeeting() {
		return pbConfirmMeeting;
	}

	/**
	 * @param pbConfirmMeeting the pbConfirmMeeting to set
	 */
	public void setPbConfirmMeeting(boolean pbConfirmMeeting) {
		this.pbConfirmMeeting = pbConfirmMeeting;
	}

	/**
	 * @return the memberSignOff
	 */
	public boolean isMemberSignOff() {
		return memberSignOff;
	}

	/**
	 * @param memberSignOff the memberSignOff to set
	 */
	public void setMemberSignOff(boolean memberSignOff) {
		this.memberSignOff = memberSignOff;
	}

	/**
	 * @return the letterOfAcceptance
	 */
	public boolean isLetterOfAcceptance() {
		return letterOfAcceptance;
	}

	/**
	 * @param letterOfAcceptance the letterOfAcceptance to set
	 */
	public void setLetterOfAcceptance(boolean letterOfAcceptance) {
		this.letterOfAcceptance = letterOfAcceptance;
	}

	/**
	 * @return the prepareLOA
	 */
	public boolean isPrepareLOA() {
		return prepareLOA;
	}

	/**
	 * @param prepareLOA the prepareLOA to set
	 */
	public void setPrepareLOA(boolean prepareLOA) {
		this.prepareLOA = prepareLOA;
	}

	/**
	 * @return the signOffDecision
	 */
	public boolean isSignOffDecision() {
		return signOffDecision;
	}

	/**
	 * @param signOffDecision the signOffDecision to set
	 */
	public void setSignOffDecision(boolean signOffDecision) {
		this.signOffDecision = signOffDecision;
	}

	/**
	 * @return the signUpdateDecision
	 */
	public boolean isSignUpdateDecision() {
		return signUpdateDecision;
	}

	/**
	 * @param signUpdateDecision the signUpdateDecision to set
	 */
	public void setSignUpdateDecision(boolean signUpdateDecision) {
		this.signUpdateDecision = signUpdateDecision;
	}

	/**
	 * @return the decisionPbQcLOA
	 */
	public boolean isDecisionPbQcLOA() {
		return decisionPbQcLOA;
	}

	/**
	 * @param decisionPbQcLOA the decisionPbQcLOA to set
	 */
	public void setDecisionPbQcLOA(boolean decisionPbQcLOA) {
		this.decisionPbQcLOA = decisionPbQcLOA;
	}

	/**
	 * @return the confirmMeeting
	 */
	public boolean isConfirmMeeting() {
		return confirmMeeting;
	}

	/**
	 * @param confirmMeeting the confirmMeeting to set
	 */
	public void setConfirmMeeting(boolean confirmMeeting) {
		this.confirmMeeting = confirmMeeting;
	}

	/**
	 * @return the selectAll
	 */
	public boolean isSelectAll() {
		return selectAll;
	}

	/**
	 * @param selectAll the selectAll to set
	 */
	public void setSelectAll(boolean selectAll) {
		this.selectAll = selectAll;
	}	

	/**
	 * @return the allowAcknowledge
	 */
	public boolean isAllowAcknowledge() {
		return allowAcknowledge;
	}

	/**
	 * @param allowAcknowledge the allowAcknowledge to set
	 */
	public void setAllowAcknowledge(boolean allowAcknowledge) {
		this.allowAcknowledge = allowAcknowledge;
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
	 * @return the disableEndorseAmendDetails
	 */
	public boolean isDisableEndorseAmendDetails() {
		return disableEndorseAmendDetails;
	}

	/**
	 * @param disableEndorseAmendDetails the disableEndorseAmendDetails to set
	 */
	public void setDisableEndorseAmendDetails(boolean disableEndorseAmendDetails) {
		this.disableEndorseAmendDetails = disableEndorseAmendDetails;
	}

	/**
	 * @return the disableFinalAmendDetails
	 */
	public boolean isDisableFinalAmendDetails() {
		return disableFinalAmendDetails;
	}

	/**
	 * @param disableFinalAmendDetails the disableFinalAmendDetails to set
	 */
	public void setDisableFinalAmendDetails(boolean disableFinalAmendDetails) {
		this.disableFinalAmendDetails = disableFinalAmendDetails;
	}

	/**
	 * @return the remarkList
	 */
	public List<CtRemark> getRemarkList() {
		if (remarkList == null) {
			remarkList = new ArrayList<CtRemark>();
		}
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
		if (attachmentList == null){
			attachmentList = new ArrayList<CtAttachment>();		
		}
		return attachmentList;
	}

	/**
	 * @param attachmentList the attachmentList to set
	 */
	public void setAttachmentList(List<CtAttachment> attachmentList) {
		this.attachmentList = attachmentList;
	}

	/**
	 * @return the amendHistoryList
	 */
	public List<CtContractVer> getAmendHistoryList() {
		if (amendHistoryList == null) {
			amendHistoryList = new ArrayList<CtContractVer>();
		}
		return amendHistoryList;
	}

	/**
	 * @param amendHistoryList the amendHistoryList to set
	 */
	public void setAmendHistoryList(List<CtContractVer> amendHistoryList) {
		this.amendHistoryList = amendHistoryList;
	}

	/**
	 * @return the finalListOfSideMenu
	 */
	public List<String> getFinalListOfSideMenu() {
		return finalListOfSideMenu;
	}

	/**
	 * @param finalListOfSideMenu the finalListOfSideMenu to set
	 */
	public void setFinalListOfSideMenu(List<String> finalListOfSideMenu) {
		this.finalListOfSideMenu = finalListOfSideMenu;
	}

	/**
	 * @return the supplierList
	 */
	public List<SmPersonnel> getSupplierList() {
		return supplierList;
	}

	/**
	 * @param supplierList the supplierList to set
	 */
	public void setSupplierList(List<SmPersonnel> supplierList) {
		this.supplierList = supplierList;
	}

	/**
	 * @return the approverModel
	 */
	public DualListModel<PmUser> getApproverModel() {
		return approverModel;
	}

	/**
	 * @param approverModel the approverModel to set
	 */
	public void setApproverModel(DualListModel<PmUser> approverModel) {
		this.approverModel = approverModel;
	}

	/**
	 * @return the userMap
	 */
	public Map<Long, PmUser> getUserMap() {
		if (userMap == null) {
			userMap = new HashMap<Long, PmUser>();
		}
		return userMap;
	}

	/**
	 * @param userMap the userMap to set
	 */
	public void setUserMap(Map<Long, PmUser> userMap) {
		this.userMap = userMap;
	}

	/**
	 * @return the selectedServiceItem
	 */
	public CtContractItem getSelectedServiceItem() {
		return selectedServiceItem;
	}

	/**
	 * @param selectedServiceItem the selectedServiceItem to set
	 */
	public void setSelectedServiceItem(CtContractItem selectedServiceItem) {
		this.selectedServiceItem = selectedServiceItem;
	}

	/**
	 * @return the agreementContractVer
	 */
	public CtContractVer getAgreementContractVer() {
		return agreementContractVer;
	}

	/**
	 * @param agreementContractVer the agreementContractVer to set
	 */
	public void setAgreementContractVer(CtContractVer agreementContractVer) {
		this.agreementContractVer = agreementContractVer;
	}

	/**
	 * @return the ctItemMaxQty
	 */
	public CtItemMaxQty getCtItemMaxQty() {
		return ctItemMaxQty;
	}

	/**
	 * @param ctItemMaxQty the ctItemMaxQty to set
	 */
	public void setCtItemMaxQty(CtItemMaxQty ctItemMaxQty) {
		this.ctItemMaxQty = ctItemMaxQty;
	}

	/**
	 * @return the ctItemPrices
	 */
	public CtItemPrice getCtItemPrices() {
		return ctItemPrices;
	}

	/**
	 * @param ctItemPrices the ctItemPrices to set
	 */
	public void setCtItemPrices(CtItemPrice ctItemPrices) {
		this.ctItemPrices = ctItemPrices;
	}

	/**
	 * @return the ctItemCeilingQtys
	 */
	public CtItemCeilingQty getCtItemCeilingQtys() {
		return ctItemCeilingQtys;
	}

	/**
	 * @param ctItemCeilingQtys the ctItemCeilingQtys to set
	 */
	public void setCtItemCeilingQtys(CtItemCeilingQty ctItemCeilingQtys) {
		this.ctItemCeilingQtys = ctItemCeilingQtys;
	}

	/**
	 * @return the selectedItem
	 */
	public CtContractItem getSelectedItem() {
		return selectedItem;
	}

	/**
	 * @param selectedItem the selectedItem to set
	 */
	public void setSelectedItem(CtContractItem selectedItem) {
		this.selectedItem = selectedItem;
	}

	/**
	 * @return the ctServiceItemPrices
	 */
	public CtItemPrice getCtServiceItemPrices() {
		return ctServiceItemPrices;
	}

	/**
	 * @param ctServiceItemPrices the ctServiceItemPrices to set
	 */
	public void setCtServiceItemPrices(CtItemPrice ctServiceItemPrices) {
		this.ctServiceItemPrices = ctServiceItemPrices;
	}

	/**
	 * @return the newCommitteMembers
	 */
	public CtCommitteeMember getNewCommitteMembers() {
		return newCommitteMembers;
	}

	/**
	 * @param newCommitteMembers the newCommitteMembers to set
	 */
	public void setNewCommitteMembers(CtCommitteeMember newCommitteMembers) {
		this.newCommitteMembers = newCommitteMembers;
	}

	/**
	 * @return the selectedScLoaReports
	 */
	public ScLoaReport getSelectedScLoaReports() {
		return selectedScLoaReports;
	}

	/**
	 * @param selectedScLoaReports the selectedScLoaReports to set
	 */
	public void setSelectedScLoaReports(ScLoaReport selectedScLoaReports) {
		this.selectedScLoaReports = selectedScLoaReports;
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
	 * @return the spkiBackingBean
	 */
	public SpkiBackingBean getSpkiBackingBean() {
		return spkiBackingBean;
	}

	/**
	 * @param spkiBackingBean the spkiBackingBean to set
	 */
	public void setSpkiBackingBean(SpkiBackingBean spkiBackingBean) {
		this.spkiBackingBean = spkiBackingBean;
	}

	/**
	 * @return the disableMeetingDetails
	 */
	public boolean isDisableMeetingDetails() {
		return disableMeetingDetails;
	}

	/**
	 * @param disableMeetingDetails the disableMeetingDetails to set
	 */
	public void setDisableMeetingDetails(boolean disableMeetingDetails) {
		this.disableMeetingDetails = disableMeetingDetails;
	}

	/**
	 * @return the approverName
	 */
	public String getApproverName() {
		return approverName;
	}

	/**
	 * @param approverName the approverName to set
	 */
	public void setApproverName(String approverName) {
		this.approverName = approverName;
	}

	public List<CtLetterOfAcceptance> getCtLetterOfAcceptance() {
		if(ctLetterOfAcceptance == null){
			ctLetterOfAcceptance = new ArrayList<CtLetterOfAcceptance>();
		}
		return ctLetterOfAcceptance;
	}

	public void setCtLetterOfAcceptance(
			List<CtLetterOfAcceptance> ctLetterOfAcceptance) {
		this.ctLetterOfAcceptance = ctLetterOfAcceptance;
	}

	public boolean isDisableLoaLetter() {
		return disableLoaLetter;
	}

	public void setDisableLoaLetter(boolean disableLoaLetter) {
		this.disableLoaLetter = disableLoaLetter;
	}

	/**
	 * @return the secretaryList
	 */
	public SelectItem[] getSecretaryList() {
		return secretaryList;
	}

	/**
	 * @param secretaryList the secretaryList to set
	 */
	public void setSecretaryList(SelectItem[] secretaryList) {
		this.secretaryList = secretaryList;
	}	
	
	
	public boolean isReadOnly() {
		return readOnly;
	}
	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}
	public boolean isResetval() {
		return resetval;
	}
	public void setResetval(boolean resetval) {
		this.resetval = resetval;
	}

	public String getContractId() {
		return contractId;
	}

	public void setContractId(String contractId) {
		this.contractId = contractId;
	}

	public String getItemname() {
		return itemname;
	}

	public void setItemname(String itemname) {
		this.itemname = itemname;
	}

	public String getItemtype() {
		return itemtype;
	}

	public void setItemtype(String itemtype) {
		this.itemtype = itemtype;
	}

	public String getItemcode() {
		return itemcode;
	}

	public void setItemcode(String itemcode) {
		this.itemcode = itemcode;
	}

	public String getItemstatus() {
		return itemstatus;
	}

	public void setItemstatus(String itemstatus) {
		this.itemstatus = itemstatus;
	}

	public String getSkuno() {
		return skuno;
	}

	public void setSkuno(String skuno) {
		this.skuno = skuno;
	}

	public String getUOM() {
		return UOM;
	}

	public void setUOM(String uOM) {
		UOM = uOM;
	}

	public String getTypeofgoods() {
		return typeofgoods;
	}

	public void setTypeofgoods(String typeofgoods) {
		this.typeofgoods = typeofgoods;
	}

	public boolean isServicecollapse() {
		return servicecollapse;
	}

	public void setServicecollapse(boolean servicecollapse) {
		this.servicecollapse = servicecollapse;
	}

	public boolean isProdcollapse() {
		return prodcollapse;
	}

	public void setProdcollapse(boolean prodcollapse) {
		this.prodcollapse = prodcollapse;
	}

	public IPagingDataModel<CtContractItem> getContractListingPagingDataModel() {
		return contractListingPagingDataModel;
	}

	public void setContractListingPagingDataModel(
			IPagingDataModel<CtContractItem> contractListingPagingDataModel) {
		this.contractListingPagingDataModel = contractListingPagingDataModel;
	}

	public IPagingDataModel<CtContractItem> getServiceListingPagingDataModel() {
		return ServiceListingPagingDataModel;
	}

	public void setServiceListingPagingDataModel(
			IPagingDataModel<CtContractItem> serviceListingPagingDataModel) {
		ServiceListingPagingDataModel = serviceListingPagingDataModel;
	}
	
	
	//Enhancement #26186 start
	public boolean isDisciplinaryAction() {
		return disciplinaryAction;
	}

	public void setDisciplinaryAction(boolean disciplinaryAction) {
		this.disciplinaryAction = disciplinaryAction;
	}

	public String getDisciplinaryActionMsg() {
		return disciplinaryActionMsg;
	}

	public void setDisciplinaryActionMsg(String disciplinaryActionMsg) {
		this.disciplinaryActionMsg = disciplinaryActionMsg;
	}
	
	public List<DisciplinaryActionVO> getDisciplineVOs() {
		return disciplineVOs;
	}

	public void setDisciplineVOs(List<DisciplinaryActionVO> disciplineVOs) {
		this.disciplineVOs = disciplineVOs;
	}

	public Long getDisciplinaryActionId() {
		return disciplinaryActionId;
	}

	public void setDisciplinaryActionId(Long disciplinaryActionId) {
		this.disciplinaryActionId = disciplinaryActionId;
	}

	public boolean isDisciplinaryApproved() {
		return disciplinaryApproved;
	}

	public void setDisciplinaryApproved(boolean disciplinaryApproved) {
		this.disciplinaryApproved = disciplinaryApproved;
	}

	public String getDisciplinaryApprovedFlag() {
		return disciplinaryApprovedFlag;
	}

	public void setDisciplinaryApprovedFlag(String disciplinaryApprovedFlag) {
		this.disciplinaryApprovedFlag = disciplinaryApprovedFlag;
	}

	public String getJustification() {
		return justification;
	}

	public void setJustification(String justification) {
		this.justification = justification;
	}
	
	//#29793 start
	public boolean isDisciplinaryActionNotification() {
		return disciplinaryActionNotification;
	}

	public void setDisciplinaryActionNotification(boolean disciplinaryActionNotification) {
		this.disciplinaryActionNotification = disciplinaryActionNotification;
	}
	
	public boolean isDisciplinaryActionCancel() {
		return disciplinaryActionCancel;
	}

	public void setDisciplinaryActionCancel(boolean disciplinaryActionCancel) {
		this.disciplinaryActionCancel = disciplinaryActionCancel;
	}
	
	public Date getDisciplinaryActionStartDate() {
		return disciplinaryActionStartDate;
	}

	public void setDisciplinaryActionStartDate(Date disciplinaryActionStartDate) {
		this.disciplinaryActionStartDate = disciplinaryActionStartDate;
	}
	
	public Date getDisciplinaryActionEndDate() {
		return disciplinaryActionEndDate;
	}

	public void setDisciplinaryActionEndDate(Date disciplinaryActionEndDate) {
		this.disciplinaryActionEndDate = disciplinaryActionEndDate;
	}
	//#29793 end

	//Enhancement #26186 end
	
	//#25956 Start
	public List<PmUser> getContractAdminlist() {
		return contractAdminlist;
	}
	public void setContractAdminlist(List<PmUser> contractAdminlist) {
		this.contractAdminlist = contractAdminlist;
	}
	//#25956 End


	//Enhancement #26186 end
	//26753 - start
	/**
	 * @return the updateCommittee
	 */
	public boolean isUpdateCommittee() {
		return updateCommittee;
	}
	/**
	 * @param updateCommittee the updateCommittee to set
	 */
	public void setUpdateCommittee(boolean updateCommittee) {
		this.updateCommittee = updateCommittee;
	}
	/**
	 * @return the updatedCommitteeMembers
	 */
	public List<CtCommitteeMember> getUpdatedCommitteeMembers() {
		//#29403 start
		if(updatedCommitteeMembers==null) {
			updatedCommitteeMembers = new ArrayList<CtCommitteeMember>();
		}	
		return updatedCommitteeMembers;
		//#29403 end
	}
	/**
	 * @param updatedCommitteeMembers the updatedCommitteeMembers to set
	 */
	public void setUpdatedCommitteeMembers(
			List<CtCommitteeMember> updatedCommitteeMembers) {
		this.updatedCommitteeMembers = updatedCommitteeMembers;
	}
	/**
	 * @return the filteredUpdatedCommitteeMembers
	 */
	public List<CtCommitteeMember> getFilteredUpdatedCommitteeMembers() {
		return filteredUpdatedCommitteeMembers;
	}
	/**
	 * @param filteredUpdatedCommitteeMembers the filteredUpdatedCommitteeMembers to set
	 */
	public void setFilteredUpdatedCommitteeMembers(
			List<CtCommitteeMember> filteredUpdatedCommitteeMembers) {
		this.filteredUpdatedCommitteeMembers = filteredUpdatedCommitteeMembers;
	}
	/**
	 * @return the hasChanges
	 */
	public boolean isHasChanges() {
		return hasChanges;
	}
	/**
	 * @param hasChanges the hasChanges to set
	 */
	public void setHasChanges(boolean hasChanges) {
		this.hasChanges = hasChanges;
	}	
	//26753 - end
	public boolean isItemProductNotExist() {
		return itemProductNotExist;
	}
	public void setItemProductNotExist(boolean itemProductNotExist) {
		this.itemProductNotExist = itemProductNotExist;
	}
	public boolean isItemServiceNotExist() {
		return itemServiceNotExist;
	}
	public void setItemServiceNotExist(boolean itemServiceNotExist) {
		this.itemServiceNotExist = itemServiceNotExist;
	}
	
	//#20997 - Start
	// For Searching Autocomplete type UOM
	public PmUom getSelectedSearchItem() {
		if (selectedSearchItem == null)
			selectedSearchItem = new PmUom();
		return selectedSearchItem;
	}
	public void setSelectedSearchItem(PmUom selectedSearchItem) {
		this.selectedSearchItem = selectedSearchItem;
	}
	//#20997 - End
	public boolean isViewOnly() {
		return viewOnly;
	}
	public void setViewOnly(boolean viewOnly) {
		this.viewOnly = viewOnly;
	}
	
	//#29549 start
	/**
	 * @return the disableDecisionDetails
	 */
	public boolean isDisableDecisionDetails() {
		return disableDecisionDetails;
	}

	/**
	 * @param disableMeetingDetails the disableDecisionDetails to set
	 */
	public void setDisableDecisionDetails(boolean disableDecisionDetails) {
		this.disableDecisionDetails = disableDecisionDetails;
	}
	//#29549 end
	
	//15851 - shaqimi start
	public String getBackToPreviousPage() {
		return backToPreviousPage;
	}
	public void setBackToPreviousPage(String backToPreviousPage) {
		this.backToPreviousPage = backToPreviousPage;
	}
	public boolean isTrackingDairyView() {
		return trackingDairyView;
	}
	public void setTrackingDairyView(boolean trackingDairyView) {
		this.trackingDairyView = trackingDairyView;
	}
	public Map<String, Integer> getIndexing() {
		return indexing;
	}
	public void setIndexing(Map<String, Integer> indexing) {
		this.indexing = indexing;
	}

	//Bug 34142, 15851 - shaqimi 04/12/2022 removed unnecessary code
	//Bug 33096 - shaqimi start
	public String getSupplementalLoaNo() {
		return supplementalLoaNo;
	}
	public void setSupplementalLoaNo(String supplementalLoaNo) {
		this.supplementalLoaNo = supplementalLoaNo;
	}
	//Bug 33096 - shaqimi end
	//15851 - shaqimi end

	//Bug 34142, 15851 - shaqimi 04/12/2022 - Reverted bug 34030 as it is no longer needed

	// Start Fix - #33256
	public Map<Long, CtItemMaxQty> getSelectedChangeItemQuantity() {
		if (selectedChangeItemQuantity == null){
			selectedChangeItemQuantity = new HashMap<Long, CtItemMaxQty>();
			}
		return selectedChangeItemQuantity;
	}
	public void setSelectedChangeItemQuantity(Map<Long, CtItemMaxQty> selectedChangeItemQuantity) {
		this.selectedChangeItemQuantity = selectedChangeItemQuantity;
	}
	public Map<Long, CtItemPrice> getSelectedChangeItemPrice() {
		if (selectedChangeItemPrice == null){
			selectedChangeItemPrice = new HashMap<Long, CtItemPrice>();
			}
		return selectedChangeItemPrice;
	}
	public void setSelectedChangeItemPrice(Map<Long, CtItemPrice> selectedChangeItemPrice) {
		this.selectedChangeItemPrice = selectedChangeItemPrice;
	}
	// End Fix - #33256
	
	//#25956 - Start
	public String getBackToNtf() {
		return backToNtf;
	}
	public void setBackToNtf(String backToNtf) {
		this.backToNtf = backToNtf;
	}
	//#25956 - End
}

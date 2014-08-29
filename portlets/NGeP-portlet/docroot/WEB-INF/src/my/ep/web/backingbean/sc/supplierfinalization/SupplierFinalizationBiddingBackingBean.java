package my.ep.web.backingbean.sc.supplierfinalization;

import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import my.ep.biz.service.sc.quationtender.IQTDetailSO;
import my.ep.biz.service.sc.quationtender.IQTMeetingSO;
import my.ep.biz.service.sc.quationtender.IQTSuppFinalizationSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.sc.SourcingConstants;
import my.ep.dto.sc.quationtender.MeetingReqDTO;
import my.ep.dto.sc.quationtender.MeetingResDTO;
import my.ep.dto.sc.quationtender.QuationTenderReqDTO;
import my.ep.dto.sc.quationtender.QuationTenderResDTO;
import my.ep.dto.sc.quationtender.SuppFinalizationReqDTO;
import my.ep.dto.sc.quationtender.SuppFinalizationResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtCommitteeActivity;
import my.ep.persistence.entity.sc.quationtender.ScQtFinalization;
import my.ep.util.MessageUtils;
import my.ep.web.backingbean.sc.quatationtender.SourcingBaseBackingBean;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.managedbean.sc.supplierfinalization.SupplierFinalizationBiddingManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;

/**
 * @author Faradiana
 * Supplier Finalization Backing Bean
 */
/**
 * @author User
 *
 */
@ManagedBean(name = "suppFinalBiddingBackingBean")
@RequestScoped
public class SupplierFinalizationBiddingBackingBean extends SourcingBaseBackingBean<SupplierFinalizationBiddingManagedBean> {
	private static final Logger log = EpLogger.getLogger(SupplierFinalizationBiddingBackingBean.class);

	private int currentTab;
	
	public static final int ONLINE_BIDDING_ACTIVE_INDEX_SUMMARY = 0;
	public static final int ONLINE_BIDDING_ACTIVE_INDEX_ENDORSEMENT = 1;
	
	//temporary
	Long qtId = 146L;
	String committeeType = ParameterConst.COMMITTEE_TYPE_PROCUREMENT_BOARD_A;
	
	public SupplierFinalizationBiddingBackingBean() {
		if(getManagedBean().getScQt() == null){
			retriveQTDetails();
		}
		retrieveSuppFinalization();
	}
	
	public void retriveQTDetails() {
		IQTDetailSO qtDetailSO;
		try {
			qtDetailSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
			
			QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
			reqDTO.setQtId(qtId);
			
			QuationTenderResDTO resDTO = qtDetailSO.retriveQtById(reqDTO);
			ScQt scQt = resDTO.getQtDetail();
			populateParentOrgProfile(scQt);
			getManagedBean().setScQt(scQt);

		} finally {
			qtDetailSO = null;
		}
	}

	private void retrieveSuppFinalization() {
		IQTSuppFinalizationSO qtSuppFinalizationSO = null;
		try {
			qtSuppFinalizationSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IQTSuppFinalizationSO.class);
			
			SuppFinalizationReqDTO reqDTO = new SuppFinalizationReqDTO();
			reqDTO.setQtId(qtId);//FIXME
			
			//SuppFinalizationResDTO resDTO = qtSuppFinalizationSO.retrieveSuppFinalizationDtls(reqDTO);
			//getManagedBean().setScQtFinalization(resDTO.getScQtFinalization());
		} finally {
			qtSuppFinalizationSO = null;
		}
	}
	
	/**
	 * To retrieve the details of Online Bidding Summary
	 */
	public void retrieveBiddingSummary() {
		this.getManagedBean().setOnlineBiddingActiveIndex(ONLINE_BIDDING_ACTIVE_INDEX_SUMMARY);
		//TODO
		/*IQTMeetingSO meetingSO;
		try {
			meetingSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IQTMeetingSO.class);

			MeetingReqDTO reqDTO = new MeetingReqDTO();
			reqDTO.setQtId(qtId);
			reqDTO.setCommitteType(committeeType);
			reqDTO.setIsAttended(null);
			
			MeetingResDTO resDTO = meetingSO.retrieveSignOffCommitteeListByCommType(reqDTO);
			if (resDTO.getCommitteMembers() != null) {
				this.getManagedBean().setCommitteeEndorsementList(resDTO.getCommitteMembers());
			}
		} finally {
			meetingSO = null;
		}*/
	}
	
	public void retrieveEndorsement() {
		ScQtFinalization scQtFinalization = getManagedBean().getScQtFinalization();
		if(scQtFinalization.getOnlineBiddingEndorse().equals(ParameterConst.ONLINE_BIDDING_MEETING)){
			retrieveOnlineEndorsement();
		}else{
			retrieveOfflineEndorsement();
		}
	}
	
	/**
	 * To retrieve the list of Committee for Endorsement (online endorsement)
	 */
	private void retrieveOnlineEndorsement() {
		IQTMeetingSO meetingSO;
		try {
			meetingSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IQTMeetingSO.class);

			MeetingReqDTO reqDTO = new MeetingReqDTO();
			reqDTO.setQtId(qtId);
			reqDTO.setCommitteType(committeeType);
			reqDTO.setIsAttended(null);
			
			MeetingResDTO resDTO = meetingSO.retrieveSignOffCommitteeListByCommType(reqDTO);
			if (resDTO.getCommitteMembers() != null) {
				this.getManagedBean().setCommitteeEndorsementList(resDTO.getCommitteMembers());
			}
		} finally {
			meetingSO = null;
		}
	}
	
	/**
	 * To retrieve the offline Endorsement (escalation)
	 */
	private void retrieveOfflineEndorsement() {
		this.getManagedBean().setOnlineBiddingActiveIndex(ONLINE_BIDDING_ACTIVE_INDEX_ENDORSEMENT);
		//TODO
		/*IQTMeetingSO meetingSO;
		try {
			meetingSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IQTMeetingSO.class);

			MeetingReqDTO reqDTO = new MeetingReqDTO();
			reqDTO.setQtId(qtId);
			reqDTO.setCommitteType(committeeType);
			reqDTO.setIsAttended(null);
			
			MeetingResDTO resDTO = meetingSO.retrieveSignOffCommitteeListByCommType(reqDTO);
			if (resDTO.getCommitteMembers() != null) {
				this.getManagedBean().setCommitteeEndorsementList(resDTO.getCommitteMembers());
			}
		} finally {
			meetingSO = null;
		}*/
	}
	
	/**
	 * Method to call Supplier Details popup in supplier finalization
	 */
	public void showReminder() {
		debugLogger("showReminder");
		
		this.getManagedBean().setReminderList(new ArrayList<ScQtCommitteeActivity>());
		
		List<ScQtCommitteeActivity> chairpersonList = new ArrayList<ScQtCommitteeActivity>();
		List<ScQtCommitteeActivity> memberList = new ArrayList<ScQtCommitteeActivity>();
		
		for(ScQtCommitteeActivity committee : getManagedBean().getCommitteeEndorsementList()){
			String memberRole = committee.getScCommitteeMember().getMemberRole().getParameterCode();
			
			if(memberRole.equals(SourcingConstants.MEMBER_ROLE_CHAIRPERSON)){
				chairpersonList.add(committee);
			}else{
				if(!committee.isSignedOff()){
					memberList.add(committee);
				}
			}
		}
		
		if(memberList.size() == 0){
			//send reminder to Chairperson
			this.getManagedBean().getReminderList().addAll(chairpersonList);
		}else{
			//send reminder to Members
			this.getManagedBean().getReminderList().addAll(memberList);
		}
	}
	
	public void sendReminder(){
		IQTSuppFinalizationSO qtSuppFinalizationSO = null;
		try {
			qtSuppFinalizationSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IQTSuppFinalizationSO.class);
		
			SuppFinalizationReqDTO reqDTO = new SuppFinalizationReqDTO();
			reqDTO.setUserId(getFacesScopes().getUserSessionBean().getUserId().toString());
			reqDTO.setCommitteMembers(getManagedBean().getCommitteeEndorsementList());
			reqDTO.setSubject(getManagedBean().getSubject());
			reqDTO.setMessage(getManagedBean().getMessage());
			
			SuppFinalizationResDTO resDTO = qtSuppFinalizationSO.sendNotifications(reqDTO);
			if (resDTO.isSuccessful()) {
				String variable1 = getParameterDesc(ParameterTypeConst.DOCUMENT_TYPES, ParameterConst.DOCTYPE_QUOTATION);
				String variable2 = getManagedBean().getScQt().getQtNo();
				commonMessageAcknowledmentPage(MessageConst.INFO_700047, variable1, variable2);
			} else {
				throw new EpException("Failed.");
			}
		} finally {
			qtSuppFinalizationSO = null;
		}
	}
	
	//when committee members endorsed the Supplier Finalization or chairperson sign off the Supplier Finalization
	public void signOff(){
		IQTSuppFinalizationSO qtSuppFinalizationSO = null;
		try {
			qtSuppFinalizationSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IQTSuppFinalizationSO.class);
			
			ScQtCommitteeActivity committeeAct = null;
			boolean isChairperson = false;
			
			retrieveOnlineEndorsement();
			for(ScQtCommitteeActivity scQtCommitteeActivity : getManagedBean().getCommitteeEndorsementList()){
				if(scQtCommitteeActivity.getScCommitteeMember().getPmUser().equals(getFacesScopes().getUserSessionBean())){
					if(scQtCommitteeActivity.getScCommitteeMember().getMemberRole().equals(SourcingConstants.MEMBER_ROLE_CHAIRPERSON)){
						isChairperson = true;
					}
					
					scQtCommitteeActivity.setSignedOff(true);
					committeeAct = scQtCommitteeActivity;
					break;
				}
			}
			
			SuppFinalizationReqDTO reqDTO = new SuppFinalizationReqDTO();
			reqDTO.setScQtCommitteeActivity(committeeAct);
			
			SuppFinalizationResDTO resDTO = qtSuppFinalizationSO.signOff(reqDTO);
			if (resDTO.isSuccessful()) {
				
				String variable1 = getParameterDesc(ParameterTypeConst.DOCUMENT_TYPES, ParameterConst.DOCTYPE_QUOTATION);
				String variable2 = getManagedBean().getScQt().getQtNo();
				if(isChairperson){
					commonMessageAcknowledmentPage(MessageConst.INFO_700049, variable1, variable2);
				}else{
					commonMessageAcknowledmentPage(MessageConst.INFO_700048, variable1, variable2);
				}
			}
		} finally {
			qtSuppFinalizationSO = null;
		}
	}
	
	private void commonMessageAcknowledmentPage(String messageId, String variable1, String variable2){
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
			FacesMessage.SEVERITY_INFO, 
			null,
			MessageUtils.getMessage(messageId, 
					new String[]{variable1,variable2}, 
					MessageUtils.BUNDLE_COMMON_MESSAGES, 
					getFacesScopes().getUserLocale())));
		 // clear data
		super.renewSesionObject();
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null, CommonUIConst.ACKNOWLEDGE_PAGE);
	}
	
	private FacesScopes getFacesScopes() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
	}
	
	private void debugLogger(Object message) {
        if(log.isDebugEnabled()) {
            log.debug(message);
        }log.info(message);
    }
	
	/**
	 * @return the currentTab
	 */
	public int getCurrentTab() {
		return currentTab;
	}

	/**
	 * @param currentTab the currentTab to set
	 */
	public void setCurrentTab(int currentTab) {
		this.currentTab = currentTab;
	}
	
}

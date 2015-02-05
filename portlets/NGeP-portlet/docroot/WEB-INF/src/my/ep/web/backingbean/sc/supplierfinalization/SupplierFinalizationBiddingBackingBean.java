package my.ep.web.backingbean.sc.supplierfinalization;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import my.ep.biz.bean.sc.BidPackageBean;
import my.ep.biz.service.sc.bidding.IBiddingSO;
import my.ep.biz.service.sc.quationtender.IQTDetailSO;
import my.ep.biz.service.sc.quationtender.IQTMeetingSO;
import my.ep.biz.service.sc.quationtender.IQTSuppFinalizationSO;
import my.ep.biz.service.sc.schedulebidding.IScheduleBiddingSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.sc.SourcingConstants;
import my.ep.dto.sc.bidding.BiddingReqDTO;
import my.ep.dto.sc.bidding.BiddingResDTO;
import my.ep.dto.sc.quationtender.MeetingReqDTO;
import my.ep.dto.sc.quationtender.MeetingResDTO;
import my.ep.dto.sc.quationtender.QuationTenderReqDTO;
import my.ep.dto.sc.quationtender.QuationTenderResDTO;
import my.ep.dto.sc.quationtender.SuppFinalizationReqDTO;
import my.ep.dto.sc.quationtender.SuppFinalizationResDTO;
import my.ep.dto.sc.schedulebidding.ScheduleBiddingReqDTO;
import my.ep.dto.sc.schedulebidding.ScheduleBiddingResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.jaxb.sc.evaluationdata.SCEvaluationDataType;
import my.ep.persistence.entity.pm.common.PmDigiSign;
import my.ep.persistence.entity.sc.bidding.ScBidItem;
import my.ep.persistence.entity.sc.bidding.ScBidPrice;
import my.ep.persistence.entity.sc.bidding.ScBidSchedule;
import my.ep.persistence.entity.sc.quationtender.ScAttachment;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtApprovalAttachment;
import my.ep.persistence.entity.sc.quationtender.ScQtCommitteeActivity;
import my.ep.persistence.entity.sc.quationtender.ScQtFinalization;
import my.ep.persistence.entity.sc.quationtender.ScQtFinalizationEndorsementAttachment;
import my.ep.persistence.entity.sc.quationtender.ScQtFinalizationEndorsementRemarks;
import my.ep.type.pm.RoleCode;
import my.ep.util.MessageUtils;
import my.ep.web.backingbean.sc.quatationtender.SourcingBaseBackingBean;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.managedbean.sc.supplierfinalization.SupplierFinalizationBiddingManagedBean;
import my.ep.web.sc.quationtender.common.QTConstants;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;

/**
 * @author Faradiana Supplier Finalization online Bidding Backing Bean
 */
@ManagedBean(name = "suppFinalBiddingBackingBean")
@RequestScoped
public class SupplierFinalizationBiddingBackingBean extends
		SourcingBaseBackingBean<SupplierFinalizationBiddingManagedBean> {
	private static final Logger log = EpLogger
			.getLogger(SupplierFinalizationBiddingBackingBean.class);

	private int currentTab;
	private String textBeforeSign;

	public static final int ONLINE_BIDDING_ACTIVE_INDEX_SUMMARY = 0;
	public static final int ONLINE_BIDDING_ACTIVE_INDEX_ONLINE_ENDORSEMENT = 1;
	public static final int ONLINE_BIDDING_ACTIVE_INDEX_OFFLINE_ENDORSEMENT = 1;

	public SupplierFinalizationBiddingBackingBean() {

	}

	public void preRenderView() {
		debugLogger("preRenderView");

		try {
			SCEvaluationDataType dataType = getBpmTaskDetails(SCEvaluationDataType.class);
			this.getManagedBean().setPayLoad(dataType);
			Long qtId = Long.valueOf(dataType.getQtId());
			String committeeType = String.valueOf(dataType.getCommitteeType());
			getManagedBean().setCommitteeType(committeeType);
			getManagedBean().setPayLoad(dataType);
			
			retrieveOnlineBiddingEndorsement(qtId);

			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication()
					.getNavigationHandler()
					.handleNavigation(fc, null,
							QTConstants.FC_ONLINE_BIDDING_ENDORSEMENT_PAGE);
		} catch (Exception e) {
			handleException(e);
		}
	}

	private void retrieveOnlineBiddingEndorsement(Long qtId) {
		retriveQTDetails(qtId);
		retrieveSupplierFinalization(qtId);
		retrieveBiddingSummary();
	}

	public void retriveQTDetails(Long qtId) {
		IQTDetailSO qtDetailSO;
		try {
			qtDetailSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);

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

	public void retrieveSupplierFinalization(Long qtId) {
		IQTSuppFinalizationSO qtSuppFinalizationSO = null;
		try {
			qtSuppFinalizationSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTSuppFinalizationSO.class);

			SuppFinalizationReqDTO reqDTO = new SuppFinalizationReqDTO();
			reqDTO.setQtId(qtId);

			SuppFinalizationResDTO resDTO = qtSuppFinalizationSO
					.retrieveSupplierFinalization(reqDTO);
			ScQtFinalization scQtFinalization = resDTO.getScQtFinalization();
			getManagedBean().setScQtFinalization(scQtFinalization);

		} finally {
			qtSuppFinalizationSO = null;
		}
	}

	/**
	 * To retrieve the details of Online Bidding Summary
	 */
	public void retrieveBiddingSummary() {
		this.getManagedBean().setOnlineBiddingActiveIndex(
				ONLINE_BIDDING_ACTIVE_INDEX_SUMMARY);
		ScQt scQt = getManagedBean().getScQt();
		String recommendType = getManagedBean().getScQtFinalization()
				.getRecommendedTypeId().getParameterCode();

		IScheduleBiddingSO scheduleBiddingSO = null;
		IBiddingSO biddingSO = null;
		try {
			scheduleBiddingSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IScheduleBiddingSO.class);

			if (recommendType.equals(ParameterConst.RECOMMEND_TYPE_PACKAGE)) {
				biddingSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, IBiddingSO.class);

				ScheduleBiddingReqDTO reqDTO = new ScheduleBiddingReqDTO();
				Long qtId = scQt.getQtId();
				reqDTO.setQtID(qtId);
				reqDTO.setDocType(QTConstants.DOCTYPE_QT);

				ScheduleBiddingResDTO resDTO = scheduleBiddingSO
						.retrieveBiddingScheduleByDocId(reqDTO);
				if (resDTO.isSuccessful()) {
					List<ScBidSchedule> bidScheduleList = resDTO
							.getBidSchedules();
					if (scQt.isZonal()) {
						// TODO
					} else {
						BiddingReqDTO bidReqDTO = new BiddingReqDTO();
						Long bidScheduleId = bidScheduleList.get(0)
								.getBidScheduleId();
						bidReqDTO.setBidScheduleId(bidScheduleId);

						BiddingResDTO bidResDTO = biddingSO
								.retrieveBidScheduleDtl(bidReqDTO);
						if (bidResDTO.isSuccessful()) {
							List<BidPackageBean> biddingPackagePriceList = bidResDTO
									.getBidPackages();
							getManagedBean().setBiddingPackagePriceList(
									biddingPackagePriceList);
						}
					}
				}
			} else if (recommendType.equals(ParameterConst.RECOMMEND_TYPE_ITEM)) {

				ScheduleBiddingReqDTO reqDTO = new ScheduleBiddingReqDTO();
				Long qtId = scQt.getQtId();
				reqDTO.setQtID(qtId);

				ScheduleBiddingResDTO resDTO = scheduleBiddingSO
						.retrieveBiddingItem(reqDTO);
				if (resDTO.isSuccessful()) {
					List<ScBidItem> biddingItemList = resDTO.getBidItemsList();
					getManagedBean().setBiddingItemList(biddingItemList);
				}
			}
		} finally {
			scheduleBiddingSO = null;
		}
	}

	public void selectBiddingItem(SelectEvent event) {
		ScBidItem bidItem = (ScBidItem) event.getObject();

		IBiddingSO biddingSO = null;
		try {
			biddingSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IBiddingSO.class);

			BiddingReqDTO reqDTO = new BiddingReqDTO();
			reqDTO.setBidItemId(bidItem.getBidItemId());

			BiddingResDTO resDTO = biddingSO
					.retrieveLastBidPricesByItem(reqDTO);
			if (resDTO.isSuccessful()) {
				List<ScBidPrice> biddingItemPriceList = resDTO.getBidPrices();
				getManagedBean().setBiddingItemPriceList(biddingItemPriceList);
			}
		} finally {
			biddingSO = null;
		}
	}

	/*
	 * public void retrieveEndorsement() { ScQtFinalization scQtFinalization =
	 * getManagedBean().getScQtFinalization();
	 * if(scQtFinalization.getOnlineBiddingEndorse
	 * ().equals(ParameterConst.ONLINE_BIDDING_MEETING)){
	 * retrieveOnlineEndorsement(); }else{ retrieveOfflineEndorsement(); } }
	 */

	/**
	 * To retrieve the list of Committee for Endorsement (online endorsement)
	 */
	public void retrieveOnlineEndorsement() {
		this.getManagedBean().setOnlineBiddingActiveIndex(
				ONLINE_BIDDING_ACTIVE_INDEX_ONLINE_ENDORSEMENT);

		IQTMeetingSO meetingSO;
		try {
			meetingSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTMeetingSO.class);
			String committeeType = getManagedBean().getCommitteeType();

			MeetingReqDTO reqDTO = new MeetingReqDTO();
			reqDTO.setQtId(getManagedBean().getScQt().getQtId());
			reqDTO.setCommitteType(committeeType);
			reqDTO.setIsAttended(null);

			MeetingResDTO resDTO = meetingSO
					.retrieveSignOffCommitteeListByCommType(reqDTO);
			if (resDTO.getCommitteMembers() != null) {
				this.getManagedBean().setCommitteeEndorsementList(
						resDTO.getCommitteMembers());
			}
		} finally {
			meetingSO = null;
		}
	}
	
	public void supplierItemPricePopupAction(Object obj) {
		// TODO : Implement the code to display the supplier details in popup
	}

	/**
	 * Method to call Supplier Details popup in supplier finalization
	 */
	public void showReminder() {
		debugLogger("showReminder");

		this.getManagedBean().setReminderList(
				new ArrayList<ScQtCommitteeActivity>());

		List<ScQtCommitteeActivity> chairpersonList = new ArrayList<ScQtCommitteeActivity>();
		List<ScQtCommitteeActivity> memberList = new ArrayList<ScQtCommitteeActivity>();

		for (ScQtCommitteeActivity committee : getManagedBean()
				.getCommitteeEndorsementList()) {
			String memberRole = committee.getScCommitteeMember()
					.getMemberRole().getParameterCode();

			if (memberRole.equals(SourcingConstants.MEMBER_ROLE_CHAIRPERSON)) {
				chairpersonList.add(committee);
			} else {
				if (!committee.isSignedOff()) {
					memberList.add(committee);
				}
			}
		}

		if (memberList.size() == 0) {
			// send reminder to Chairperson
			this.getManagedBean().getReminderList().addAll(chairpersonList);
		} else {
			// send reminder to Members
			this.getManagedBean().getReminderList().addAll(memberList);
		}
	}

	public void sendReminder() {
		IQTSuppFinalizationSO qtSuppFinalizationSO = null;
		try {
			qtSuppFinalizationSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTSuppFinalizationSO.class);

			SuppFinalizationReqDTO reqDTO = new SuppFinalizationReqDTO();
			reqDTO.setUserId(getFacesScopes().getUserSessionBean().getUserId()
					.toString());
			reqDTO.setCommitteMembers(getManagedBean()
					.getCommitteeEndorsementList());
			reqDTO.setSubject(getManagedBean().getSubject());
			reqDTO.setMessage(getManagedBean().getMessage());

			SuppFinalizationResDTO resDTO = qtSuppFinalizationSO
					.sendNotifications(reqDTO);
			if (resDTO.isSuccessful()) {
				String variable1 = getParameterDesc(
						ParameterTypeConst.DOCUMENT_TYPES,
						ParameterConst.DOCTYPE_QUOTATION);
				String variable2 = getManagedBean().getScQt().getQtNo();
				commonMessageAcknowledmentPage(MessageConst.INFO_700047,
						variable1, variable2);
			} else {
				throw new EpException("Failed.");
			}
		} finally {
			qtSuppFinalizationSO = null;
		}
	}

	// when committee members endorsed the Supplier Finalization or chairperson
	// sign off the Supplier Finalization
	public void signOff() {
		IQTSuppFinalizationSO qtSuppFinalizationSO = null;
		try {
			qtSuppFinalizationSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTSuppFinalizationSO.class);

			ScQtCommitteeActivity committeeAct = null;
			boolean isChairperson = false;

			retrieveOnlineEndorsement();
			for (ScQtCommitteeActivity scQtCommitteeActivity : getManagedBean()
					.getCommitteeEndorsementList()) {
				if (scQtCommitteeActivity.getScCommitteeMember().getPmUser()
						.equals(getFacesScopes().getUserSessionBean())) {
					if (scQtCommitteeActivity.getScCommitteeMember()
							.getMemberRole()
							.equals(SourcingConstants.MEMBER_ROLE_CHAIRPERSON)) {
						isChairperson = true;
					}

					scQtCommitteeActivity.setSignedOff(true);
					committeeAct = scQtCommitteeActivity;
					break;
				}
			}

			SuppFinalizationReqDTO reqDTO = new SuppFinalizationReqDTO();
			reqDTO.setScQtCommitteeActivity(committeeAct);

			SuppFinalizationResDTO resDTO = qtSuppFinalizationSO
					.signOff(reqDTO);
			if (resDTO.isSuccessful()) {

				String variable1 = getParameterDesc(
						ParameterTypeConst.DOCUMENT_TYPES,
						ParameterConst.DOCTYPE_QUOTATION);
				String variable2 = getManagedBean().getScQt().getQtNo();
				if (isChairperson) {
					commonMessageAcknowledmentPage(MessageConst.INFO_700049,
							variable1, variable2);
				} else {
					commonMessageAcknowledmentPage(MessageConst.INFO_700048,
							variable1, variable2);
				}
			}
		} finally {
			qtSuppFinalizationSO = null;
		}
	}

	/**
	 * To retrieve the offline Endorsement (escalation)
	 */
	public void retrieveOfflineEndorsement() {
		this.getManagedBean().setOnlineBiddingActiveIndex(
				ONLINE_BIDDING_ACTIVE_INDEX_OFFLINE_ENDORSEMENT);

		ScQtFinalization scQtFinalization = getManagedBean()
				.getScQtFinalization();
		
		List<ScQtFinalizationEndorsementRemarks> remarks = initializeRemarks(
				scQtFinalization, null, scQtFinalization.getScQtFinalizationEndorsementRemarks(), ScQtFinalizationEndorsementRemarks.class);
		
		scQtFinalization.setScQtFinalizationEndorsementRemarks(remarks);
		getManagedBean().setRemarks(remarks);
	}

	public void handleFileUpload(FileUploadEvent event)
			throws FileNotFoundException, IOException {
		if (event.getFile() != null) {
			ScAttachment scAttachment = null;
			ScQtFinalization scQtFinalization = this.getManagedBean()
					.getScQtFinalization();
			ScQtFinalizationEndorsementAttachment attachment = scQtFinalization
					.getScQtFinalizationEndorsementAttachment();
			if (null == attachment) {
				attachment = new ScQtFinalizationEndorsementAttachment();
				attachment.setScQtFinalization(scQtFinalization);
				scQtFinalization
						.setScQtFinalizationEndorsementAttachment(attachment);
			} else {
				attachment.setScQtFinalization(scQtFinalization);
				scQtFinalization
						.setScQtFinalizationEndorsementAttachment(attachment);
			}
			attachment.setDocNo(String.valueOf(scQtFinalization
					.getFinalizationId()));
			scAttachment = attachment;
			uploadAttachment(event.getFile(), scAttachment);
			getManagedBean().setScQtFinalization(scQtFinalization);
		}
	}

	/**
	 * determineIcon for displaying image icon in pid
	 */
	public String determineIcon(ScQtApprovalAttachment attachment) {
		String imageFileName = "icon_file_default.png";
		String ext = "";
		if (attachment != null) {
			String fileName = attachment.getFileName();
			if (fileName != null) {
				int mid = fileName.lastIndexOf(".");
				ext = fileName.substring(mid + 1, fileName.length());
			}
			if ("doc".equals(ext) || "docx".equals(ext)) {
				imageFileName = "icon_doc.png";
			} else if ("pdf".equals(ext)) {
				imageFileName = "icon_pdf.png";
			} else if ("xls".equals(ext) || "xlsx".equals(ext)
					|| "csv".equals(ext)) {
				imageFileName = "icon_exel.png";
			} else if ("jpg".equals(ext) || "bmp".equals(ext)
					|| "png".equals(ext) || "gif".equals(ext)) {
				imageFileName = "icon_image.png";
			} else if ("txt".equals(ext)) {
				imageFileName = "icon_txt.png";
			}
		}
		return imageFileName;
	}

	public void saveOfflineEndorsement() {
		IQTSuppFinalizationSO qtSuppFinalizationSO = null;
		try {
			qtSuppFinalizationSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTSuppFinalizationSO.class);

			int activeIndex = this.getManagedBean()
					.getOnlineBiddingActiveIndex();
			String taskId = this.getManagedBean().getTaskId();
			String[] outComes = this.getManagedBean().getOutComes();
			String processName = this.getManagedBean().getProcessName();
			ScQt scQt = getManagedBean().getScQt();
			Long currentStatusId = this.getManagedBean().getCurrentStatusId();
			
			ScQtFinalization scQtFinalization = prepareFinalizationInfo();

			SuppFinalizationReqDTO reqDTO = new SuppFinalizationReqDTO();
			reqDTO.setScQtFinalization(scQtFinalization);
			qtSuppFinalizationSO.saveFinalizationOfflineEndorsement(reqDTO);

			renewSesionObject();
			setTaskDetails(activeIndex, taskId, outComes, processName, currentStatusId);

			Long qtId = scQt.getQtId();
			retrieveOnlineBiddingEndorsement(qtId);

			String variable1 = getParameterDesc(
					ParameterTypeConst.DOCUMENT_TYPES,
					ParameterConst.DOCTYPE_QUOTATION)
					+ " " + scQt.getQtNo();
			String variable2 = getMessageCommon("lbl.sc.online.bidding.endorsement");
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, null,
							MessageUtils.getMessage(MessageConst.INFO_700004,
									new String[] { variable1, variable2 },
									MessageUtils.BUNDLE_COMMON_MESSAGES,
									new FacesScopes().getUserLocale())));
		} finally {
			qtSuppFinalizationSO = null;
		}
	}

	private ScQtFinalization prepareFinalizationInfo() {
		ScQtFinalization scQtFinalization = getManagedBean()
				.getScQtFinalization();
		
		finalizeRemarks(scQtFinalization.getScQtFinalizationEndorsementRemarks());		
		
		setUserDetails(scQtFinalization);

		return scQtFinalization;
	}
	
	
	
	public void submitBiddingEndorsement(String outcome) {
		debugLogger("submitBiddingEndorsement");
		this.getManagedBean().setAction(outcome);
		if(isDigiSigningRequired(RoleCode.PBA_CHAIRPERSON.value())) {
			RequestContext.getCurrentInstance().execute("gpki_dlg.show()");
		} else {
			updateBPMTask(outcome, null);
		}	
	}
	

	private void updateBPMTask(String outCome, PmDigiSign digiSign){
		debugLogger("updateBPMTask");
		IQTSuppFinalizationSO qtSuppFinalizationSO = null;
		SuppFinalizationReqDTO suppFinalizationReqDTO = new SuppFinalizationReqDTO();
		SuppFinalizationResDTO suppFinalizationResDTO = null;
		suppFinalizationReqDTO.setOutcome(outCome);
		suppFinalizationReqDTO.setTaskId(this.getManagedBean().getTaskId());
		suppFinalizationReqDTO.setToken(new FacesScopes()
				.getBPMAuthenticationCtxToken());
		try {
			qtSuppFinalizationSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTSuppFinalizationSO.class);
			ScQt scQt = getManagedBean().getScQt();

			/*if (ParameterConst.ONLINE_BIDDING_MEETING 
					.equals(getManagedBean().getScQtFinalization()
							.getOnlineBiddingEndorse())) {
				// TODO - for endorse n sign off by committee member
				// status should be pending Endorsement
			} else {*/
			// TODO - for escalation, upload doc and submit
			// status should be pending Escalation

			if (null != digiSign) {
				updatePmDigiSignData(digiSign, ParameterConst.DOCTYPE_SUPPLIER_FINALIZATION, 
						 scQt.getQtId(),  scQt.getQtNo());
			}

			ScQtFinalization scQtFinalization = prepareFinalizationInfo();

			suppFinalizationReqDTO.setScQtFinalization(scQtFinalization);
			suppFinalizationReqDTO.setPayLoad(this.getManagedBean()
					.getPayLoad());
			suppFinalizationReqDTO.setDigiSignData(digiSign);
			suppFinalizationResDTO = qtSuppFinalizationSO.updateBPMAction(suppFinalizationReqDTO);
				
			//}
			if (suppFinalizationResDTO.isSuccessful()) {
				String variable1 = getParameterDesc(
						ParameterTypeConst.DOCUMENT_TYPES,
						ParameterConst.DOCTYPE_QUOTATION)
						+ " " + scQt.getQtNo();
				String variable2 = getMessageCommon("lbl.sc.online.bidding.endorsement");
				commonMessageAcknowledmentPage(MessageConst.INFO_700026,
						variable1, variable2);
			}
		} finally {
			qtSuppFinalizationSO = null;
		}
	}

	private void setTaskDetails(Integer activeIndex, String taskId,
			String[] outComes, String processName, Long currentStatusId) {
		if (activeIndex != null) {
			this.getManagedBean().setOnlineBiddingActiveIndex(activeIndex);
		}
		this.getManagedBean().setTaskId(taskId);
		this.getManagedBean().setOutComes(outComes);
		this.getManagedBean().setProcessName(processName);
	}

	private void commonMessageAcknowledmentPage(String messageId,
			String variable1, String variable2) {
		FacesContext.getCurrentInstance().addMessage(
				null,
				new FacesMessage(FacesMessage.SEVERITY_INFO, null, MessageUtils
						.getMessage(messageId, new String[] { variable1,
								variable2 },
								MessageUtils.BUNDLE_COMMON_MESSAGES,
								getFacesScopes().getUserLocale())));
		// clear data
		super.renewSesionObject();
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler()
				.handleNavigation(fc, null, CommonUIConst.ACKNOWLEDGE_PAGE);
	}

	
	public void gpkiSignAction(PmDigiSign digiSign) {
		
		updateBPMTask(this.getManagedBean().getAction(), digiSign);
	}

	public String getTextBeforeSign() {
		StringBuilder sb = new StringBuilder();
		if(getManagedBean().getScQt() != null){
			sb.append(getManagedBean().getScQt().getQtNo());
		}
		sb.append(new FacesScopes().getUserSessionBean().getUserId());
		textBeforeSign = sb.toString();
		return textBeforeSign;
	}

	public void setTextBeforeSign(String textBeforeSign) {
		this.textBeforeSign = textBeforeSign;
	}
	
	private FacesScopes getFacesScopes() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
	}

	private void debugLogger(Object message) {
		if (log.isDebugEnabled()) {
			log.debug(message);
		}
		log.info(message);
	}

	/**
	 * @return the currentTab
	 */
	public int getCurrentTab() {
		return currentTab;
	}

	/**
	 * @param currentTab
	 *            the currentTab to set
	 */
	public void setCurrentTab(int currentTab) {
		this.currentTab = currentTab;
	}

}

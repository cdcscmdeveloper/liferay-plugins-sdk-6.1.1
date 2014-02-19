package my.ep.web.backingbean.sc.supplierfinalization;

import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import my.ep.biz.service.sc.quationtender.IQTEvaluationReportSO;
import my.ep.biz.service.sc.quationtender.IQTSuppFinalizationSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.sc.quationtender.EvaluationReportReqDTO;
import my.ep.dto.sc.quationtender.EvaluationReportResDTO;
import my.ep.dto.sc.quationtender.SuppFinalizationReqDTO;
import my.ep.dto.sc.quationtender.SuppFinalizationResDTO;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtCommitteeActivity;
import my.ep.persistence.entity.sc.quationtender.ScQtEvaluationResult;
import my.ep.persistence.entity.sc.quationtender.ScQtItem;
import my.ep.persistence.entity.sc.quationtender.ScQtMeeting;
import my.ep.persistence.entity.sc.quationtender.ScQtZoneCriteria;
import my.ep.web.backingbean.sc.quatationtender.SourcingBaseBackingBean;
import my.ep.web.managedbean.sc.supplierfinalization.SupplierFinalizationManagedBean;
import my.ep.web.sc.quationtender.common.QTConstants;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.sc.helper.LocationSingleton;

import org.apache.log4j.Logger;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;

/**
 * @author sri Supplier Finalization Backing Bean
 */
@ManagedBean(name = "sfBackingBean")
@RequestScoped
public class SupplierFinalizationBackingBean extends
		SourcingBaseBackingBean<SupplierFinalizationManagedBean> {
	private static final Logger log = EpLogger
			.getLogger(SupplierFinalizationBackingBean.class);

	public SupplierFinalizationBackingBean() {
		optionsEvent();
		awardTypeEvent();
		retrieveMeetingCommitteeMemberDtl(); // Finalization
		retrieveSummaryDetails(); // Pre - Finalization
	}

	/**
	 * retrieving Meeting and Committee Member Listing
	 */
	public void retrieveMeetingCommitteeMemberDtl() {
		debugLogger("Meeting and Committee Member Details");
		IQTSuppFinalizationSO suppFinalizationSO = null;
		try {
			suppFinalizationSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTSuppFinalizationSO.class);

			SuppFinalizationReqDTO reqDTO = new SuppFinalizationReqDTO();
			reqDTO.setQtId(146L);
			reqDTO.setCommitteeType("QC");
			SuppFinalizationResDTO resDTO = suppFinalizationSO
					.retrieveSuppFinalizationDtl(reqDTO);

			// Meeting Details Listing
			if (getManagedBean().getMeetingDetails() == null) {
				List<ScQtMeeting> meetingDetails = resDTO.getMeetingDtls();
				this.getManagedBean().setMeetingDetails(meetingDetails);

			}

			// Committee Member Listing
			List<ScQtCommitteeActivity> committeeMemberList = resDTO
					.getCommitteMembers();
			this.getManagedBean().setCommitteeMemberList(committeeMemberList);

			// Committee sign-off Listing
			List<ScQtCommitteeActivity> signOffMemberlist = resDTO
					.getCommitteSignOffMembers();
			this.getManagedBean().setCommitteeSignOffMemberlist(
					signOffMemberlist);

			// Zone Listing
			List<ScQtZoneCriteria> zonelist = resDTO.getScQt()
					.getScQtCriterias().getScQtZoneCriterias();
			this.getManagedBean().setZonelist(zonelist);

			List<ScQtItem> itemlist = resDTO.getItemResultBean()
					.getResultList();
			this.getManagedBean().setItemSuppFinalizationlist(itemlist);

			// populate Header Details
			ScQt scQt = resDTO.getScQt();
			populateParentOrgProfile(scQt);
			this.getManagedBean().setScQt(scQt);

			if (!scQt.getScQtCriterias().isMultiLocality()) {

				// Recommended Supplier Listing
				List<ScQtEvaluationResult> recommendedSuppList = resDTO
						.getQualifiedSuppResultBean().getResultList();
				this.getManagedBean().setRecommendedSuppList(
						recommendedSuppList);

				// Shortlisted Supplier Listing
				List<ScQtEvaluationResult> shortlistedSuppList = resDTO
						.getQualifiedSuppResultBean().getResultList();
				this.getManagedBean().setShortlistedSuppList(
						shortlistedSuppList);
				this.getManagedBean().setTotalSelectedSupplier(
						shortlistedSuppList.size());
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							ex.toString(), ex.getMessage()));
		} finally {
			suppFinalizationSO = null;
		}
	}

	/**
	 * retrieving Shortlisted and Recommended Supplier Listing Based on Zone
	 */
	public void shortlistedRecommendedSuppDtl(ScQtZoneCriteria scQtZoneCriteria) {
		debugLogger("Shortlisted and Recommended Supplier");
		IQTSuppFinalizationSO suppFinalizationSO = null;
		try {
			suppFinalizationSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTSuppFinalizationSO.class);
			SuppFinalizationReqDTO reqDTO = new SuppFinalizationReqDTO();

			if (scQtZoneCriteria != null) {
				reqDTO.setQtId(getManagedBean().getScQt().getQtId());
				reqDTO.setQtZoneCriteriaId(scQtZoneCriteria
						.getQtZoneCriteriaId());
				SuppFinalizationResDTO resDTO = suppFinalizationSO
						.retrieveItemSupplierDtlByZone(reqDTO);

				// Shortlisted Supplier Listing
				List<ScQtEvaluationResult> shortlistedSuppList = resDTO
						.getQualifiedSuppResultBean().getResultList();
				this.getManagedBean().setShortlistedSuppList(
						shortlistedSuppList);
				this.getManagedBean().setTotalSelectedSupplier(
						shortlistedSuppList.size());
				this.getManagedBean().setScQtZoneCriteria(scQtZoneCriteria);

				// Recommended Supplier Listing
				List<ScQtEvaluationResult> recommendedSuppList = resDTO
						.getQualifiedSuppResultBean().getResultList();
				this.getManagedBean().setRecommendedSuppList(
						recommendedSuppList);
				this.getManagedBean().setTotalSelectedSupplier(
						recommendedSuppList.size());

				getManagedBean().setRootLocationTree(
						LocationSingleton.getInstance().buildLocationTree(1L,
								null, null, null, null));
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							ex.toString(), ex.getMessage()));
		} finally {
			suppFinalizationSO = null;
		}
	}

	/**
	 * Method To call to save Shortlisted and Recommended Suppliers in Supplier
	 * Finalization
	 */
	public String saveSupplierFinalization() {
		debugLogger("Save Supplier Finalization");
		IQTSuppFinalizationSO suppFinalizationSO = null;
		try {
			suppFinalizationSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTSuppFinalizationSO.class);
			SuppFinalizationReqDTO reqDTO = new SuppFinalizationReqDTO();
			/*
			 * List<ScQtProposalRanking> proposalRaningList=new
			 * ArrayList<ScQtProposalRanking>();
			 */
			List<ScQtEvaluationResult> evaluationResultList = new ArrayList<ScQtEvaluationResult>();

			if (getManagedBean().getMeetingDetails() != null) {
				reqDTO.setMeetingDtls(getManagedBean().getMeetingDetails());
			}

			if (getManagedBean().getCommitteeMemberList() != null) {
				reqDTO.setCommitteMembers(getManagedBean()
						.getCommitteeMemberList());
			}

			// Shortlisted Supplier listing
			List<ScQtEvaluationResult> list = getManagedBean()
					.getShortlistedSuppList();

			if (list != null) {
				evaluationResultList.addAll(list);
			}

			// Recommended Supplier listing
			list = getManagedBean().getRecommendedSuppList();
			if (list != null) {
				evaluationResultList.addAll(list);
			}

			if (evaluationResultList.size() > 0) {
				reqDTO.setQualifiedSuppliers(evaluationResultList);

			}
			reqDTO.setScQt(this.getManagedBean().getScQt());
			suppFinalizationSO.saveQTDtl(reqDTO);
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO,
							"Record saved successfully.",
							"Supplier Finalization "));

		} catch (Exception e) {
			e.printStackTrace();
			throw new IllegalArgumentException(e.getMessage());
		} finally {
			suppFinalizationSO = null;

		}
		return null;

	}

	/**
	 * Method To call to save Summary Details
	 */
	public String saveSummaryDtl() {
		debugLogger("Save Summary Details");
		IQTEvaluationReportSO evaluationReportSO = null;
		try {
			evaluationReportSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTEvaluationReportSO.class);

			if (getManagedBean().getQualifiedSupplist() != null) {
				EvaluationReportReqDTO reqDTO = new EvaluationReportReqDTO();
				reqDTO.setQualifiedSuppliers(getManagedBean()
						.getQualifiedSupplist());
				evaluationReportSO.saveQTDtl(reqDTO);
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO,
								"Record saved successfully.",
								"Summary Details "));

			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new IllegalArgumentException();
		} finally {
			evaluationReportSO = null;
		}
		return null;
	}

	/**
	 * Adding new row for Meeting Details in Preparation
	 */
	public void addMeetingDetails() {
		debugLogger("add Meeting Details");
		IQTSuppFinalizationSO suppFinalizationSO = null;
		ScQtMeeting meetingDetails = new ScQtMeeting();
		suppFinalizationSO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, IQTSuppFinalizationSO.class);
		SuppFinalizationReqDTO reqDTO = new SuppFinalizationReqDTO();
		try {
			List<ScQtMeeting> meetingDtllist = getManagedBean()
					.getMeetingDetails();
			if (null == meetingDtllist) {
				meetingDtllist = new ArrayList<ScQtMeeting>();
			}

			reqDTO.setQtId(getManagedBean().getScQt().getQtId());
			SuppFinalizationResDTO resDTO = suppFinalizationSO
					.retrieveSuppFinalizationDtl(reqDTO);
			meetingDetails.setScQt(resDTO.getScQt());
			meetingDetails.setMeetingType("QC");
			meetingDtllist.add(meetingDetails);
			this.getManagedBean().setMeetingDetails(meetingDtllist);
		} catch (Exception e) {
			// e.printStackTrace();
			throw new IllegalArgumentException();
		}
	}

	/**
	 * retrieving Meeting and Committee Member Listing
	 */
	public void retrieveSummaryDetails() {
		debugLogger("retrieveSummaryDetails");
		IQTEvaluationReportSO evaluationReportSO = null;
		try {
			evaluationReportSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTEvaluationReportSO.class);
			EvaluationReportReqDTO reqDTO = new EvaluationReportReqDTO();
			reqDTO.setQtId(146L);
			EvaluationReportResDTO resDTO = evaluationReportSO
					.retrieveSummaryDtl(reqDTO);

			List<ScQtZoneCriteria> zoneList = resDTO.getScQt()
					.getScQtCriterias().getScQtZoneCriterias();
			System.out.println("Zone list : " + zoneList.size());
			this.getManagedBean().setZonelist(zoneList);
			getManagedBean().setZoneItemList(zoneList);

			List<ScQtItem> itemlist = resDTO.getItemResultBean()
					.getResultList();
			System.out.println("itemlist: " + itemlist.size());
			this.getManagedBean().setItemPreSummarylist(itemlist);

			// populate Header Details
			ScQt scQt = resDTO.getScQt();
			populateParentOrgProfile(scQt);
			this.getManagedBean().setScQt(scQt);

			if (!scQt.getScQtCriterias().isMultiLocality()) {
				if (resDTO.getQualifiedSuppResultBean() != null) {
					List<ScQtEvaluationResult> qualifiedSupplist = resDTO
							.getQualifiedSuppResultBean().getResultList();
					System.out.println("qualifiedSupplist: "
							+ qualifiedSupplist.size());
					this.getManagedBean().setQualifiedSupplist(
							qualifiedSupplist);
					this.getManagedBean().setTotalQulifiedSupplier(
							qualifiedSupplist.size());
				}
				if (resDTO.getDisqualifiedSuppResultBean() != null) {
					List<ScQtEvaluationResult> disqualifiedSupplist = resDTO
							.getDisqualifiedSuppResultBean().getResultList();
					System.out.println("disqualifiedSupplist: "
							+ disqualifiedSupplist.size());
					this.getManagedBean().setDisQualifiedSupplist(
							disqualifiedSupplist);
					this.getManagedBean().setTotalDisQualifiedSupplier(
							disqualifiedSupplist.size());
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							ex.toString(), ex.getMessage()));
		} finally {
			evaluationReportSO = null;
		}
	}

	public void onRowZoneSelect(SelectEvent selectEvent) {
		debugLogger("onRowZoneSelect");
		ScQtZoneCriteria criteria = (ScQtZoneCriteria) selectEvent.getObject();
		getManagedBean().setSelectZoneByItem(criteria);
		System.out.println("Zone Id : "
				+ getManagedBean().getSelectZoneByItem().getQtZoneCriteriaId()
				+ "Zone Name : "
				+ getManagedBean().getSelectZoneByItem().getZoneName());
	}

	public void getZoneByItemDtl(SelectEvent selectEvent) {
		debugLogger("getZoneByItemDtl");
		ScQtItem item = (ScQtItem) selectEvent.getObject();
		System.out.println("Item Id : " + item.getQtItemId() + "Item Name : "
				+ item.getItemName());
		IQTSuppFinalizationSO suppFinalizationSO = null;
		try {
			suppFinalizationSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTSuppFinalizationSO.class);
			SuppFinalizationReqDTO reqDTO = new SuppFinalizationReqDTO();
			System.out.println("Zone Id : "
					+ getManagedBean().getSelectZoneByItem()
							.getQtZoneCriteriaId());
			reqDTO.setQtZoneCriteriaId(getManagedBean().getSelectZoneByItem()
					.getQtZoneCriteriaId());
			// SuppFinalizationResDTO resDTO =
			// suppFinalizationSO.retrieveItemSupplierDtlByZone(reqDTO);
		} catch (Exception ex) {
			ex.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							ex.toString(), ex.getMessage()));
		} finally {
			suppFinalizationSO = null;
		}

	}

	/**
	 * retrieving Qualified and Disqualified Supplier Listing By Zone in Summary
	 */
	public void qualifiedDisQualifiedSuppDtl(ScQtZoneCriteria scQtZoneCriteria) {
		debugLogger("Qualified And DisQualified Supplier");
		IQTSuppFinalizationSO suppFinalizationSO = null;
		try {
			suppFinalizationSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTSuppFinalizationSO.class);
			SuppFinalizationReqDTO reqDTO = new SuppFinalizationReqDTO();

			if (scQtZoneCriteria != null) {
				// reqDTO.setQtId(146L);
				reqDTO.setQtId(getManagedBean().getScQt().getQtId());
				reqDTO.setQtZoneCriteriaId(scQtZoneCriteria
						.getQtZoneCriteriaId());
				SuppFinalizationResDTO resDTO = suppFinalizationSO
						.retrieveItemSupplierDtlByZone(reqDTO);

				if (resDTO.getQualifiedSuppResultBean() != null) {
					List<ScQtEvaluationResult> qualifiedSupplist = resDTO
							.getQualifiedSuppResultBean().getResultList();
					System.out.println("list: " + qualifiedSupplist.size());
					this.getManagedBean().setQualifiedSupplist(
							qualifiedSupplist);
					this.getManagedBean().setTotalQulifiedSupplier(
							qualifiedSupplist.size());
				}

				if (resDTO.getDisqualifiedSuppResultBean() != null) {
					List<ScQtEvaluationResult> disqualifiedSupplist = resDTO
							.getDisqualifiedSuppResultBean().getResultList();
					this.getManagedBean().setDisQualifiedSupplist(
							disqualifiedSupplist);
					this.getManagedBean().setTotalDisQualifiedSupplier(
							disqualifiedSupplist.size());
				}

				this.getManagedBean().setScQtZoneCriteria(scQtZoneCriteria);
				System.out.println("Zone Name(Package) : "
						+ getManagedBean().getScQtZoneCriteria().getZoneName());
				getManagedBean().setRootLocationTree(LocationSingleton.getInstance().buildLocationTree(1L, null, null, null, null));
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							ex.toString(), ex.getMessage()));
		} finally {
			suppFinalizationSO = null;
		}
	}

	/**
	 * Event for By Item and By Package in Supplier Finalization
	 */
	public void awardTypeEvent() {
		debugLogger("awardTypeEvent");

		if (getManagedBean().isAwardType()) {
			this.getManagedBean().setByItemStatus(true);
			this.getManagedBean().setByPackageStatus(false);
		} else {
			this.getManagedBean().setByItemStatus(false);
			this.getManagedBean().setByPackageStatus(true);
		}

	}

	/**
	 * Event for By Item and By Package in Supplier Finalization
	 */
	public void viewByEvent() {
		debugLogger("viewByEvent");

		if (getManagedBean().isAwardType()) {
			this.getManagedBean().setSummaryByItemStatus(false);
			this.getManagedBean().setSummaryByPackageStatus(true);
		} else {
			this.getManagedBean().setSummaryByItemStatus(true);
			this.getManagedBean().setSummaryByPackageStatus(false);
		}

	}

	/**
	 * Event for selecting Option for Forward Higher Authority and Re-Evaluation
	 * in Supplier Finalization
	 */
	public void decisionEvent() {
		debugLogger("Decision Event ");
		if (getManagedBean().getDecisionArray().length > 0) {
			for (int i = 0; i < getManagedBean().getDecisionArray().length; i++) {
				String[] decisionArray = getManagedBean().getDecisionArray();

				if (decisionArray[i]
						.equalsIgnoreCase(QTConstants.SF_FORWARD_TO_HIGHER_AUTH)) {
					this.getManagedBean().setFhAuthority(true);
					this.getManagedBean().setReEvaluation(false);
					this.getManagedBean().setShortlistedSuppVisible(false);
					this.getManagedBean().setRecommendedSuppVisible(false);
					this.getManagedBean().setAward(false);

				} else if (decisionArray[i]
						.equalsIgnoreCase(QTConstants.SF_RE_EVALUATION)) {
					this.getManagedBean().setReEvaluation(true);
					this.getManagedBean().setFhAuthority(false);
					this.getManagedBean().setShortlistedSuppVisible(false);
					this.getManagedBean().setRecommendedSuppVisible(false);
					this.getManagedBean().setAward(false);

				} else if (decisionArray[i]
						.equalsIgnoreCase(QTConstants.SF_ONLINE_BIDDING)
						|| (decisionArray[i]
								.equalsIgnoreCase(QTConstants.SF_MULTIPLE_LOI))) {
					this.getManagedBean().setShortlistedSuppVisible(true);
					this.getManagedBean().setRecommendedSuppVisible(false);
					this.getManagedBean().setFhAuthority(false);
					this.getManagedBean().setReEvaluation(false);
					this.getManagedBean().setAward(false);

				} else if (decisionArray[i]
						.equalsIgnoreCase(QTConstants.SF_AWARD)) {
					this.getManagedBean().setShortlistedSuppVisible(false);
					this.getManagedBean().setRecommendedSuppVisible(true);
					this.getManagedBean().setFhAuthority(false);
					this.getManagedBean().setReEvaluation(false);
					this.getManagedBean().setAward(true);

				} else {
					this.getManagedBean().setFhAuthority(false);
					this.getManagedBean().setReEvaluation(false);
					this.getManagedBean().setShortlistedSuppVisible(false);
					this.getManagedBean().setRecommendedSuppVisible(false);
					this.getManagedBean().setAward(false);
				}

			}
		}
	}

	/**
	 * Event for selecting Option for Basic , Multiple Items and By Suppler in
	 * Supplier Finalization
	 */
	public void optionsEvent() {
		debugLogger("Options Event ");
		if (getManagedBean().getOptionType() != null) {
			if (getManagedBean().getOptionType().equalsIgnoreCase(
					QTConstants.SF_BASIC)) {
				this.getManagedBean().setBasicSelection(false);
				this.getManagedBean().setMultipleItemSelection(false);
				this.getManagedBean().setSelectionMode(QTConstants.SF_SINGLE);

			} else if (getManagedBean().getOptionType().equalsIgnoreCase(
					QTConstants.SF_MULTIPLE_ITEMS)) {
				this.getManagedBean().setMultipleItemSelection(true);
				this.getManagedBean().setBasicSelection(false);
				this.getManagedBean().setSelectionMode(QTConstants.SF_MULTIPLE);

			} else if (getManagedBean().getOptionType().equalsIgnoreCase(
					QTConstants.SF_BY_SUPPLIER)) {
				this.getManagedBean().setMultipleItemSelection(false);
				this.getManagedBean().setBasicSelection(true);
				this.getManagedBean().setSelectionMode(QTConstants.SF_MULTIPLE);
			}

		}
	}

	/**
	 * Method to call Supplier Details popup in supplier finalization
	 */
	public void showSupplierDetails() {
		debugLogger("showSuppDetails");
		if (getManagedBean().getScQtEvaluationResult() != null) {
			this.getManagedBean().setScQtEvaluationResult(
					getManagedBean().getScQtEvaluationResult());
		}
	}

	private void debugLogger(Object message) {
		if (log.isDebugEnabled()) {
			log.debug(message);
		}
		log.info(message);
	}

	public void handleFileUpload(FileUploadEvent event) {
		debugLogger("handleFileUpload");
	}
}

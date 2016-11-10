/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */

package my.ep.web.backingbean.sc.bidding;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import my.ep.biz.service.sc.quationtender.IQTSuppFinalizationSO;
import my.ep.biz.service.sc.schedulebidding.IScheduleBiddingSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.dto.sc.quationtender.SuppFinalizationReqDTO;
import my.ep.dto.sc.quationtender.SuppFinalizationResDTO;
import my.ep.dto.sc.schedulebidding.ScheduleBiddingReqDTO;
import my.ep.dto.sc.schedulebidding.ScheduleBiddingResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.jaxb.sc.evaluationdata.SCEvaluationDataType;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.sc.bidding.ScBidContactPerson;
import my.ep.persistence.entity.sc.bidding.ScBidItem;
import my.ep.persistence.entity.sc.bidding.ScBidSchedule;
import my.ep.persistence.entity.sc.quationtender.ScQtRecommendDtl;
import my.ep.util.MessageUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.web.backingbean.sc.quatationtender.SourcingBaseBackingBean;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.constant.sc.QTConstants;
import my.ep.web.managedbean.sc.bidding.ScheduleBiddingManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.primefaces.event.SelectEvent;

/**
 * Schedule Bidding Backing Bean
 * 
 * @author Venkat Chanumolu
 */

@ManagedBean(name = "scheduleBiddingBB")
@ViewScoped
public class ScheduleBiddingBackingBean extends
		SourcingBaseBackingBean<ScheduleBiddingManagedBean> implements Serializable{

	
	private static final long serialVersionUID = 1L;
	private static Logger log = EpLogger
			.getLogger(ScheduleBiddingBackingBean.class);

	/**
	 * Default Constructor
	 */
	public ScheduleBiddingBackingBean() {
		if (getManagedBean().getSelectedBidBidScheduleList() == null) {
			getManagedBean().setSelectedBidBidScheduleList(
					new ArrayList<ScBidSchedule>());
		}

	}

	/**
	 * Populate duration drop down values.
	 */
	public void populateDurationDropDownValues() {
		SelectItem[] items = null;
		FacesScopes face = new FacesScopes();
		try {

			items = new SelectItem[3];
			items[0] = new SelectItem(null, ResourceBundleUtils.getMessage(
					"lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					face.getUserLocale()));
			items[1] = new SelectItem(getManagedBean().getBidMinDuration(),
					"01:00");

			items[2] = new SelectItem(getManagedBean().getBidMaxDuration(),
					"02:00");
			/*
			 * getManagedBean().getBidMinDuration()+" "+
			 * ResourceBundleUtils.getMessage( "lbl.txt.hour",
			 * ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale())
			 * getManagedBean().getBidMaxDuration()+" "+
			 * ResourceBundleUtils.getMessage( "lbl.txt.hours",
			 * ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale())
			 */
			getManagedBean().setSelectItems(items);
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(),
							e.getMessage()));
		}
	}

	private void debugLogger(Object message) {
		if (log.isDebugEnabled()) {
			log.debug(message);
		}
		log.info(message);
	}

	/**
	 * method to navigate online bidding page
	 */

	public void preRenderView() {
		try {
			SCEvaluationDataType dpDataType = getBpmTaskDetails(
					SCEvaluationDataType.class, null);
			this.getManagedBean().setPayLoad(dpDataType);
			Long qtId = Long.valueOf(dpDataType.getQtId());
			List<String> recommandIds = dpDataType.getGroupkeyBa();
			List<Long> recommendDtls = new ArrayList<Long>();

			for (String str : recommandIds) {
				recommendDtls.add(Long.parseLong(str));
			}

			retriveItemsToBid(qtId, recommendDtls,
					((PmUser) (new FacesScopes().getUserSessionBean()))
							.getUserId());
			populateDurationDropDownValues();
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication()
					.getNavigationHandler()
					.handleNavigation(fc, null,
							QTConstants.SCHEDULE_BIDDING_MAIN_PAGE);
		} catch (Exception e) {
			handleException(e);
		}
	}

	/**
	 * Method to apply dates to all the selected items in the data table
	 */
	public void applyBidDates() {
		Date startDate = getManagedBean().getRowSelectedBid().getStartDate();
		Date endDate = getManagedBean().getRowSelectedBid().getEndDate();
		if (startDate == null) {
			throw new EpException(MessageConst.ERROR_760336);
		}

		if (getManagedBean().getRowSelectedBid().getDuration() == null) {
			throw new EpException(MessageConst.ERROR_760338);
		}
		if (startDate != null && endDate != null) {
			Long duration = getManagedBean().getRowSelectedBid().getDuration();
			List<ScBidItem> bidItemList = this.getManagedBean()
					.getBidItemList();
			int size = bidItemList.size();
			for (int i = 0; i < size; i++) {
				if (bidItemList.get(i).isSelected()) {
					bidItemList.get(i).getScBidSchedule()
							.setStartDate(startDate);
					bidItemList.get(i).getScBidSchedule().setEndDate(endDate);
					bidItemList.get(i).getScBidSchedule().setDuration(duration);
					bidItemList.get(i).setSelected(false);
				}
			}
			getManagedBean().setSelectedAll(false);
			getManagedBean().getRowSelectedBid().setStartDate(null);
			getManagedBean().getRowSelectedBid().setEndDate(null);
		}

	}

	/**
	 * method to add contact person to the list
	 */
	public void addContactAction() {

		try {
			log.info(" **** enter addContactAction *** ");

			List<ScBidContactPerson> contactList = getManagedBean()
					.getScBidContactPersonList();

			if (null == contactList) {
				contactList = new ArrayList<ScBidContactPerson>();
			}

			ScBidContactPerson scBidContactPerson = new ScBidContactPerson();
			scBidContactPerson.setScBid(getManagedBean().getScBid());
			contactList.add(scBidContactPerson);
			this.getManagedBean().setScBidContactPersonList(contactList);
			log.info(" **** ScBidContactPersonList Size *** "
					+ getManagedBean().getScBidContactPersonList().size());
			getManagedBean().setSelectedAllBidContacts(false);
		}

		catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Validate contact personnel.
	 */
	private void validateContactPersonnel() {

		List<String> contactPersonnelNames = new ArrayList<String>();

		List<ScBidContactPerson> contactPersonnel = getManagedBean()
				.getScBidContactPersonList();

		if (null != contactPersonnel) {

			for (ScBidContactPerson contact : contactPersonnel) {
				String name = contact.getContactName();
				if (contactPersonnelNames.contains(name)) {
					throw new EpException(MessageConst.ERROR_760335);
				} else {
					contactPersonnelNames.add(name);
				}
			}
		}
		contactPersonnelNames = null;
	}

	/**
	 * method to delete contact person from the list
	 */
	public void deleteContactDetails() {
		try {
			getManagedBean().getScBidContactPersonList();
			List<ScBidContactPerson> toDeleteContacts = new ArrayList<ScBidContactPerson>();
			for (ScBidContactPerson contact : getManagedBean()
					.getScBidContactPersonList()) {
				if (contact.isSelected()) {
					toDeleteContacts.add(contact);
				}
			}
			if (CollectionUtils.isNotEmpty(toDeleteContacts)) {
				getManagedBean().getScBidContactPersonList().removeAll(
						toDeleteContacts);
				toDeleteContacts = null;
			}
			getManagedBean().setSelectedAllBidContacts(false);
		} catch (Exception e) {
			log.info(e.getLocalizedMessage());
		}
	}

	/**
	 * Listener method for zonal bid row select event.
	 */
	public void zonalRowBidSelectListener() {
		ScBidSchedule selectedZonalBid = this.getManagedBean()
				.getRowSelectedBid();
		if (selectedZonalBid.getScBidItems() != null
				&& !selectedZonalBid.getScBidItems().isEmpty()) {
			this.getManagedBean().setSelectedBidItem(
					selectedZonalBid.getScBidItems().get(0));
			retrieveParticipantsListAction();
		}

	}

	/**
	 * Listener method for Bid item selection event.
	 *
	 * @param event the event
	 */
	public void bidItemSelectionListner(SelectEvent event) {
		ScBidItem bidItem = (ScBidItem) event.getObject();
		this.getManagedBean().setSelectedBidItem(bidItem);
		retrieveParticipantsListAction();
	}

	/**
	 * Method to retrieve list of participants for the selected bidItem
	 * 
	 * @param bidItem
	 */

	public void retrieveParticipantsListAction() {
		ScBidItem bidItem = this.getManagedBean().getSelectedBidItem();
		IQTSuppFinalizationSO suppFinalizationSO = null;
		if (bidItem != null) {
			try {
				suppFinalizationSO = BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
								IQTSuppFinalizationSO.class);
				SuppFinalizationReqDTO reqDTO = new SuppFinalizationReqDTO();

				reqDTO.setRequestItemId(bidItem.getScRequestItem()
						.getRequestNoteItemId());
				reqDTO.setFinalizationId(getManagedBean().getScQtFinalization()
						.getFinalizationId());
				if (getManagedBean().getScQt().isZonal()) {
					reqDTO.setSpecZoneId(getManagedBean().getRowSelectedBid()
							.getTplSpecZoneDtl().getSpecZoneId());
				}
				SuppFinalizationResDTO resDTO = suppFinalizationSO
						.retrieveRecommendDtlList(reqDTO);

				if (resDTO.isSuccessful()) {
					getManagedBean().setBidParticipants(
							resDTO.getScQtRecommendDtlList());
				}
			} finally {
				suppFinalizationSO = null;
			}
		}

	}

	/**
	 * Method to retrieve bidItems for the supplied qtid and recommend's
	 * 
	 * @param qtId
	 * @param recommandIds
	 */

	public void retriveItemsToBid(Long qtId, List<Long> recommandIds,
			Long userId) {

		debugLogger("retriveItemsToBid");
		IScheduleBiddingSO scheduleBiddingSO = null;

		try {
			scheduleBiddingSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IScheduleBiddingSO.class);
			ScheduleBiddingReqDTO reqDTO = new ScheduleBiddingReqDTO();
			reqDTO.setQtID(qtId);
			reqDTO.setRecommandIds(recommandIds);
			reqDTO.setUserId(userId);
			ScheduleBiddingResDTO resDTO = scheduleBiddingSO
					.retrieveBidScduleDetials(reqDTO);

			if (resDTO.isSuccessful()) {
				getManagedBean().setScQt(resDTO.getScQT());
				getManagedBean().setScQtFinalization(
						resDTO.getScQtFinalization());
				getManagedBean().setBidScheduleList(resDTO.getBidSchedules());
				getManagedBean().setScBid(resDTO.getScBid());
				getManagedBean().setBidItemList(resDTO.getBidItemsList());
				getManagedBean().setBidMinDuration(resDTO.getMinDuration());
				getManagedBean().setBidMaxDuration(resDTO.getMaxDuration());
				if (resDTO.getScQT().isZonal()) {
					getManagedBean().setRowSelectedBid(
							resDTO.getBidSchedules().get(0));
					getManagedBean().setSelectedBidItem(
							resDTO.getBidSchedules().get(0).getScBidItems()
									.get(0));
				}
				if (!(resDTO.getScQT().isZonal())
						&& ParameterConst.RECOMMEND_TYPE_ITEM.equals(resDTO
								.getScQtFinalization().getRecommendedTypeId()
								.getParameterCode())) {
					getManagedBean().setRowSelectedBid(new ScBidSchedule());
					getManagedBean().setSelectedBidItem(
							resDTO.getBidItemsList().get(0));
				}
				if (!(resDTO.getScQT().isZonal())
						&& ParameterConst.RECOMMEND_TYPE_PACKAGE.equals(resDTO
								.getScQtFinalization().getRecommendedTypeId()
								.getParameterCode())) {
					getManagedBean().setSelectedBidItem(
							resDTO.getBidSchedules().get(0).getScBidItems()
									.get(0));
				}
				retrieveParticipantsListAction();
			}
		}

		finally {
			scheduleBiddingSO = null;
		}
	}

	/**
	 * Listener for bidStartDate selection events
	 * 
	 * @param scBidSchedule
	 */

	public void bidStartDateSelectListener(ScBidSchedule scBidSchedule) {
		if (scBidSchedule != null && scBidSchedule.getStartDate() != null
				&& scBidSchedule.getDuration() != null) {
			Date date = scBidSchedule.getStartDate();
			Calendar endDate = Calendar.getInstance();
			endDate.setTime(date);
			endDate.add(Calendar.HOUR_OF_DAY, scBidSchedule.getDuration()
					.intValue());
			scBidSchedule.setEndDate(endDate.getTime());
		} else {
			scBidSchedule.setEndDate(null);
		}
		int index = getManagedBean().getBidScheduleList()
				.indexOf(scBidSchedule);
		getManagedBean().getBidScheduleList().set(index, scBidSchedule);
	}

	/**
	 * Listener for bidEndDate selection events
	 * 
	 * @param scBidSchedule
	 */
	public void durationSelectListener(ScBidSchedule scBidSchedule) {
		int index = getManagedBean().getBidScheduleList()
				.indexOf(scBidSchedule);
		if (scBidSchedule.getStartDate() != null) {
			Calendar endDate = Calendar.getInstance();
			endDate.setTime(scBidSchedule.getStartDate());
			if (scBidSchedule.getDuration() != 0) {
				endDate.add(Calendar.HOUR_OF_DAY, scBidSchedule.getDuration()
						.intValue());
				scBidSchedule.setEndDate(endDate.getTime());
			} else {
				scBidSchedule.setEndDate(null);
			}
			getManagedBean().getBidScheduleList().set(index, scBidSchedule);
		} else {
			scBidSchedule.setEndDate(null);
			getManagedBean().getBidScheduleList().set(index, scBidSchedule);
		}
	}

	/**
	 * Listener for startDate selection events for Package wise bidding
	 * 
	 * @param bidItem
	 */
	public void itemStartDateSelectListener(ScBidItem bidItem) {
		if (bidItem.getScBidSchedule().getStartDate() != null
				&& bidItem.getScBidSchedule().getDuration() != null) {
			Calendar endDate = Calendar.getInstance();
			endDate.setTime(bidItem.getScBidSchedule().getStartDate());
			endDate.add(Calendar.HOUR_OF_DAY, bidItem.getScBidSchedule()
					.getDuration().intValue());
			bidItem.getScBidSchedule().setEndDate(endDate.getTime());
		} else {
			bidItem.getScBidSchedule().setEndDate(null);
		}
		int index = getManagedBean().getBidItemList().indexOf(bidItem);
		getManagedBean().getBidItemList().set(index, bidItem);
	}

	/**
	 * Listener for endDate selection events for Package wise bidding
	 * 
	 * @param bidItem
	 */

	public void itemDurationSelectListener(ScBidItem bidItem) {
		int index = getManagedBean().getBidItemList().indexOf(bidItem);
		if (bidItem.getScBidSchedule().getStartDate() != null
				&& bidItem.getScBidSchedule().getDuration() != null) {
			Calendar endDate = Calendar.getInstance();
			endDate.setTime(bidItem.getScBidSchedule().getStartDate());
			endDate.add(Calendar.HOUR_OF_DAY, bidItem.getScBidSchedule()
					.getDuration().intValue());
			bidItem.getScBidSchedule().setEndDate(endDate.getTime());
		} else {
			bidItem.getScBidSchedule().setEndDate(null);
		}
		getManagedBean().getBidItemList().set(index, bidItem);
	}

	/**
	 * Listener for multiple start Date selection events for Package wise
	 * bidding
	 * 
	 * @param bidItem
	 */

	public void multiSelectStartDateListener(ScBidSchedule scBidSchedule) {

		if (scBidSchedule.getStartDate() != null
				&& scBidSchedule.getDuration() != null) {
			Date date = scBidSchedule.getStartDate();
			Calendar endDate = Calendar.getInstance();
			endDate.setTime(date);
			endDate.add(Calendar.HOUR_OF_DAY, getManagedBean()
					.getBidMinDuration().intValue());
			scBidSchedule.setEndDate(endDate.getTime());
		} else {
			scBidSchedule.setEndDate(null);
		}
		getManagedBean().setRowSelectedBid(scBidSchedule);
	}

	/**
	 * Listener for multiple end Date selection events for Package wise bidding
	 * 
	 * @param bidItem
	 */
	public void multiSelectDurationListener(ScBidSchedule scBidSchedule) {

		if (scBidSchedule.getStartDate() != null
				&& scBidSchedule.getDuration() != null) {
			Date date = scBidSchedule.getStartDate();
			Calendar endDate = Calendar.getInstance();
			endDate.setTime(date);
			endDate.add(Calendar.HOUR_OF_DAY, scBidSchedule.getDuration()
					.intValue());
			scBidSchedule.setEndDate(endDate.getTime());
		} else {
			scBidSchedule.setEndDate(null);
		}
		this.getManagedBean().setRowSelectedBid(scBidSchedule);
	}

	private String durationFormatter(long durationInMills) {

		long diffMinutes = durationInMills / (60 * 1000) % 60;
		long diffHours = durationInMills / (60 * 60 * 1000) % 24;
		DecimalFormat durationFormat = new DecimalFormat("#00");
		String duration = durationFormat.format(diffHours) + ":"
				+ durationFormat.format(diffMinutes);
		return duration;
	}

	/**
	 * Method to submit bidding details
	 * 
	 * @param outCome
	 */
	public void submitBiddingDetails(String outCome) {

		PmUser creatorUser = (PmUser) (new FacesScopes().getUserSessionBean());

		String taskId = this.getManagedBean().getTaskId();
		IScheduleBiddingSO scheduleBiddingSO = null;
		ScheduleBiddingReqDTO biddingReqDTO = new ScheduleBiddingReqDTO();
		try {
			scheduleBiddingSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IScheduleBiddingSO.class);
			biddingReqDTO.setCreatorUser(creatorUser);
			biddingReqDTO.setOutcome(outCome);
			biddingReqDTO.setToken(new FacesScopes()
					.getBPMAuthenticationCtxToken());
			biddingReqDTO.setTaskId(taskId);
			biddingReqDTO.setRoleCode(getUserRoleCode());
			biddingReqDTO.setPmUser(new FacesScopes().getUserSessionBean());
			biddingReqDTO.setPayLoad(this.getManagedBean().getPayLoad());
			validateContactPersonnel();
			getManagedBean().getScBid().setScBidContactPersons(
					getManagedBean().getScBidContactPersonList());
			biddingReqDTO.setScQt(getManagedBean().getScQt());
			biddingReqDTO.setScQtFinalization(getManagedBean()
					.getScQtFinalization());
			biddingReqDTO.setScBid(this.getManagedBean().getScBid());
			biddingReqDTO.setBidSchedules(this.getManagedBean()
					.getBidScheduleList());
			biddingReqDTO.setScBidItemList(getManagedBean().getBidItemList());

			ScheduleBiddingResDTO resDTO = scheduleBiddingSO
					.updateBPMAction(biddingReqDTO);

			if (resDTO.isSuccessful()) {

				String variable1 = getMessageCommon("lbl.rpt.qtNo");
				String variable2 = resDTO.getScBid().getDocNo();
				commonMessageAcknowledgmentPage(MessageConst.INFO_700026,
						variable1, variable2);
			}

		} finally {
			scheduleBiddingSO = null;
		}
	}

	/**
	 * Common message acknowledgment page.
	 *
	 * @param messageId the message id
	 * @param variable1 the variable 1
	 * @param variable2 the variable 2
	 */
	private void commonMessageAcknowledgmentPage(String messageId,
			String variable1, String variable2) {
		FacesContext.getCurrentInstance().addMessage(
				null,
				new FacesMessage(FacesMessage.SEVERITY_INFO, null, MessageUtils
						.getMessage(messageId, new String[] { variable1,
								variable2 },
								MessageUtils.BUNDLE_COMMON_MESSAGES,
								new FacesScopes().getUserLocale())));
		// clear data
		super.renewSesionObject();
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler()
				.handleNavigation(fc, null, CommonUIConst.ACKNOWLEDGE_PAGE);
	}

	/**
	 * Listener for selection of multi boolean check box events
	 */
	public void selectAll() {

		if (getManagedBean().isSelectedAll()) {
			getManagedBean().setSelected(true);
			getManagedBean().getSelectedIndexes().clear();
			for (int i = 0; i < getManagedBean().getBidItemList().size(); i++) {
				getManagedBean().getSelectedIndexes().add(i);
			}
		} else {
			getManagedBean().setSelected(false);
			getManagedBean().getSelectedIndexes().clear();
		}

	}

	/**
	 * Listener for selection boolean check box events
	 */
	public void selected(int index) {
		if (getManagedBean().isSelected()
				&& getManagedBean().getSelectedIndexes() != null) {

			getManagedBean().getSelectedIndexes().add(index);
		} else {
			if (getManagedBean().getSelectedIndexes() != null
					&& getManagedBean().getSelectedIndexes().contains(
							new Integer(index))) {
				getManagedBean().getSelectedIndexes()
						.remove(new Integer(index));
			}

		}

	}

	/**
	 * Listener method to get bid participant list
	 */

	public void getParticipantsListAction(ScBidSchedule bidSchedule) {

	}

	/**
	 * Method to calculate Item unit price sub total.
	 *
	 * @param recommendDtl the ScQtRecommendDtl
	 * @return the big decimal
	 */
	public BigDecimal itemUnitPriceSubTotal(ScQtRecommendDtl recommendDtl) {

		BigDecimal itemSubTotal = this
				.getManagedBean()
				.getTplSpecHelper()
				.calcTotalAmount(
						recommendDtl.getScQtProposalItem().getScRequestItem()
								.getTplSpecQuestion(),
						recommendDtl.getScQtProposalItem().getOfferPrice(),
						this.getManagedBean().getSelectedBidItem().getQty());
		return itemSubTotal;
	}
}

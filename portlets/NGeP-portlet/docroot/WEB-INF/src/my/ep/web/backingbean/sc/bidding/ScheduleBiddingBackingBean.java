/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */

package my.ep.web.backingbean.sc.bidding;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import my.ep.biz.service.sc.quationtender.IQTSuppFinalizationSO;
import my.ep.biz.service.sc.schedulebidding.IScheduleBiddingSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.dto.sc.quationtender.SuppFinalizationReqDTO;
import my.ep.dto.sc.quationtender.SuppFinalizationResDTO;
import my.ep.dto.sc.schedulebidding.ScheduleBiddingReqDTO;
import my.ep.dto.sc.schedulebidding.ScheduleBiddingResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.jaxb.sc.evaluationdata.SCEvaluationDataType;
import my.ep.persistence.entity.sc.bidding.ScBidContactPerson;
import my.ep.persistence.entity.sc.bidding.ScBidItem;
import my.ep.persistence.entity.sc.bidding.ScBidSchedule;
import my.ep.persistence.entity.sc.supplierproposal.ScQtPRPExperience;
import my.ep.util.MessageUtils;
import my.ep.web.backingbean.sc.quatationtender.SourcingBaseBackingBean;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.constant.sc.QTConstants;
import my.ep.web.managedbean.sc.bidding.ScheduleBiddingManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;

/**
 * Schedule Bidding Backing Bean
 * @author Venkat Chanumolu
 */

@ManagedBean(name = "scheduleBiddingBB")
@ViewScoped
public class ScheduleBiddingBackingBean extends
		SourcingBaseBackingBean<ScheduleBiddingManagedBean> {

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
			SCEvaluationDataType dpDataType = getBpmTaskDetails(SCEvaluationDataType.class);
			this.getManagedBean().setPayLoad(dpDataType);
			Long qtId = Long.valueOf(dpDataType.getQtId());
			List<String> recommandIds = dpDataType.getGroupkeyBa();
			List<Long> recommendDtls = new ArrayList<Long>();

			for (String str : recommandIds) {
				recommendDtls.add(Long.parseLong(str));
			}

			retriveItemsToBid(qtId, recommendDtls);

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

		if (getManagedBean().getSelectedIndexes() != null
				&& getManagedBean().getSelectedIndexes().size() > 0) {

			Date startDate = getManagedBean().getRowSelectedBid()
					.getStartDate();
			Date endDate = getManagedBean().getRowSelectedBid().getEndDate();
			String duration = getManagedBean().getRowSelectedBid()
					.getDuration();
			for (Integer index : getManagedBean().getSelectedIndexes()) {
				getManagedBean().getBidItemList().get(index).getScBidSchedule()
						.setStartDate(startDate);
				getManagedBean().getBidItemList().get(index).getScBidSchedule()
						.setEndDate(endDate);
				getManagedBean().getBidItemList().get(index).getScBidSchedule()
						.setDuration(duration);
			}
		}
		getManagedBean().getSelectedIndexes().clear();
		getManagedBean().setSelectedAll(false);
		getManagedBean().setSelected(false);

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
			getManagedBean().getMultiSelectContact().clear();
			getManagedBean().setSelectedBidContact(false);
			getManagedBean().setSelectedAllBidContacts(false);
		}

		catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * method to delete contact person from the list
	 */
	public void deleteContactDetails() {
		try {

			List<ScBidContactPerson> contactList = getManagedBean()
					.getScBidContactPersonList();
			List<ScBidContactPerson> multiSelectContacts = getManagedBean()
					.getMultiSelectContact();
			if (contactList != null && multiSelectContacts != null) {
				log.info(" **** contact list *** " + contactList.size());
				log.info(" **** multiSelectContacts *** "
						+ multiSelectContacts.size());
				if (contactList.size() == multiSelectContacts.size()) {
					contactList.clear();
				} else {
					for (ScBidContactPerson contact : multiSelectContacts) {
						contactList.remove(contact);
					}
				}
			}
			getManagedBean().getMultiSelectContact().clear();
			getManagedBean().setSelectedBidContact(false);
			getManagedBean().setSelectedAllBidContacts(false);

		} catch (Exception e) {

		}
	}

	/**
	 * Method to retrieve list of participants for the selected bidItem
	 * 
	 * @param bidItem
	 */

	public void retrieveParticipantsListAction(ScBidItem bidItem) {
		IQTSuppFinalizationSO suppFinalizationSO = null;

		try {
			suppFinalizationSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTSuppFinalizationSO.class);
			SuppFinalizationReqDTO reqDTO = new SuppFinalizationReqDTO();

			reqDTO.setRequestItemId(bidItem.getScRequestItem()
					.getRequestNoteItemId());
			reqDTO.setFinalizationId(getManagedBean().getScQtFinalization()
					.getFinalizationId());

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

	/**
	 * Method to retrieve bidItems for the supplied qtid and recommend's
	 * 
	 * @param qtId
	 * @param recommandIds
	 */

	public void retriveItemsToBid(Long qtId, List<Long> recommandIds) {

		debugLogger("retriveItemsToBid");
		IScheduleBiddingSO scheduleBiddingSO = null;

		try {
			scheduleBiddingSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IScheduleBiddingSO.class);
			ScheduleBiddingReqDTO reqDTO = new ScheduleBiddingReqDTO();
			reqDTO.setQtID(qtId);
			reqDTO.setRecommandIds(recommandIds);
			ScheduleBiddingResDTO resDTO = scheduleBiddingSO
					.retrieveBidScduleDetials(reqDTO);

			if (resDTO.isSuccessful()) {
				getManagedBean().setScQt(resDTO.getScQT());
				getManagedBean().setScQtFinalization(
						resDTO.getScQtFinalization());
				getManagedBean().setBidScheduleList(resDTO.getBidSchedules());
				getManagedBean().setScBid(resDTO.getScBid());
				getManagedBean().setBidItemList(resDTO.getBidItemsList());
				if (resDTO.getScQT().isZonal()) {
					getManagedBean().setRowSelectedBid(
							resDTO.getBidSchedules().get(0));
				}
				if (ParameterConst.RECOMMEND_TYPE_ITEM.equals(resDTO
						.getScQtFinalization().getRecommendedTypeId()
						.getParameterCode())) {
					getManagedBean().setRowSelectedBid(new ScBidSchedule());

				}

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
		Date date = scBidSchedule.getStartDate();
		getManagedBean().setMinEndDate(date);
		Calendar maxEndDate = Calendar.getInstance();
		maxEndDate.setTime(date);
		maxEndDate.add(Calendar.HOUR_OF_DAY, 2);
		getManagedBean().setMaxEndDate(maxEndDate.getTime());
		if (scBidSchedule.getEndDate() != null) {
			scBidSchedule.setEndDate(null);
			int index = getManagedBean().getBidScheduleList().indexOf(
					scBidSchedule);
			getManagedBean().getBidScheduleList().set(index, scBidSchedule);
		}
	}

	/**
	 * Listener for bidEndDate selection events
	 * 
	 * @param scBidSchedule
	 */
	public void bidEndDateSelectListener(ScBidSchedule bidSchedule) {

		ScBidSchedule scBidSchedule = bidSchedule;
		if (scBidSchedule.getStartDate() == null) {
			scBidSchedule.setEndDate(null);
			int index = getManagedBean().getBidScheduleList().indexOf(
					scBidSchedule);
			getManagedBean().getBidScheduleList().set(index, scBidSchedule);
			throw new EpException("Please Select Start Date");
		}
		long durationInMills = scBidSchedule.getEndDate().getTime()
				- scBidSchedule.getStartDate().getTime();
		if (durationInMills == 0) {
			throw new EpException("Please Select Proper End Date");
		}
		scBidSchedule.setDuration(durationFormatter(durationInMills));
		int index = getManagedBean().getBidScheduleList()
				.indexOf(scBidSchedule);
		getManagedBean().getBidScheduleList().set(index, scBidSchedule);
	}

	/**
	 * Listener for startDate selection events for Package wise bidding
	 * 
	 * @param bidItem
	 */
	public void itemStartDateSelectListener(ScBidItem bidItem) {
		Date date = bidItem.getScBidSchedule().getStartDate();
		getManagedBean().setMinEndDate(date);
		Calendar maxEndDate = Calendar.getInstance();
		maxEndDate.setTime(date);
		maxEndDate.add(Calendar.HOUR_OF_DAY, 2);
		getManagedBean().setMaxEndDate(maxEndDate.getTime());
		if (bidItem.getScBidSchedule().getEndDate() != null) {
			bidItem.getScBidSchedule().setEndDate(null);
			int index = getManagedBean().getBidItemList().indexOf(bidItem);
			getManagedBean().getBidItemList().set(index, bidItem);
		}
	}
	
	/**
	 * Listener for endDate selection events for Package wise bidding
	 * 
	 * @param bidItem
	 */

	public void itemEndDateSelectListener(ScBidItem bidItem) {

		// ScBidSchedule scBidSchedule = bidSchedule;
		if (bidItem.getScBidSchedule().getStartDate() == null) {
			bidItem.getScBidSchedule().setEndDate(null);
			int index = getManagedBean().getBidItemList().indexOf(bidItem);
			getManagedBean().getBidItemList().set(index, bidItem);
			throw new EpException("Please Select Start Date");
		}
		long durationInMills = bidItem.getScBidSchedule().getEndDate()
				.getTime()
				- bidItem.getScBidSchedule().getStartDate().getTime();
		if (durationInMills == 0) {
			throw new EpException("Please Select Proper End Date");
		}
		bidItem.getScBidSchedule().setDuration(
				durationFormatter(durationInMills));
		int index = getManagedBean().getBidItemList().indexOf(bidItem);
		getManagedBean().getBidItemList().set(index, bidItem);
	}
	
	/**
	 * Listener for multiple start Date selection events for Package wise bidding
	 * 
	 * @param bidItem
	 */

	public void multiSelectStartDateListener(ScBidSchedule scBidSchedule) {
		Date date = scBidSchedule.getStartDate();
		getManagedBean().setMinEndDate(date);
		Calendar maxEndDate = Calendar.getInstance();
		maxEndDate.setTime(date);
		maxEndDate.add(Calendar.HOUR_OF_DAY, 2);
		getManagedBean().setMaxEndDate(maxEndDate.getTime());
		if (getManagedBean().getRowSelectedBid().getEndDate() != null) {
			getManagedBean().getRowSelectedBid().setEndDate(null);

			getManagedBean().setRowSelectedBid(scBidSchedule);
		}
	}

	/**
	 * Listener for multiple end Date selection events for Package wise bidding
	 * 
	 * @param bidItem
	 */
	public void multiSelectEndDateListener(ScBidSchedule bidSchedule) {
		ScBidSchedule scBidSchedule = bidSchedule;
		if (scBidSchedule.getStartDate() == null) {
			scBidSchedule.setEndDate(null);
			int index = getManagedBean().getBidScheduleList().indexOf(
					scBidSchedule);
			getManagedBean().getBidScheduleList().set(index, scBidSchedule);
			throw new EpException("Please Select Start Date");
		}
		long durationInMills = scBidSchedule.getEndDate().getTime()
				- scBidSchedule.getStartDate().getTime();
		if (durationInMills == 0) {
			throw new EpException("Please Select Proper End Date");
		}
		bidSchedule.setDuration(durationFormatter(durationInMills));
		getManagedBean().setRowSelectedBid(scBidSchedule);
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
	 * @param outCome
	 */
	public void submitBiddingDetails(String outCome) {

		String taskId = this.getManagedBean().getTaskId();
		String token = new FacesScopes().getBPMAuthenticationCtxToken();
		IScheduleBiddingSO scheduleBiddingSO = null;
		ScheduleBiddingReqDTO biddingReqDTO = new ScheduleBiddingReqDTO();
		try {
			scheduleBiddingSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IScheduleBiddingSO.class);

			biddingReqDTO.setOutcome(outCome);
			biddingReqDTO.setToken(token);
			biddingReqDTO.setTaskId(taskId);
			biddingReqDTO.setRoleCode(getUserRoleCode());
			biddingReqDTO.setPmUser(new FacesScopes().getUserSessionBean());
			biddingReqDTO.setPayLoad(this.getManagedBean().getPayLoad());
			// Need to call prepare save details method..
			if (getManagedBean().getScBidContactPersonList() != null
					&& getManagedBean().getScBidContactPersonList().size() != 0) {
				getManagedBean().getScBid().setScBidContactPersons(
						getManagedBean().getScBidContactPersonList());
			} else {
				// TODO update the message
				throw new EpException(
						"please enter at least one Bid Contact Person");
			}

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
				commonMessageAcknowledmentPage(MessageConst.INFO_700026,
						variable1, variable2);
			}

		} finally {
			scheduleBiddingSO = null;
		}
	}

	private void commonMessageAcknowledmentPage(String messageId,
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
	 * Listener for selection multi boolean checkbox  events 
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
	 * Listener for selection  boolean checkbox  events 
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
	 * Listener method to select all bid contacts
	 */
	public void selectAllBidContacts() {
		if (getManagedBean().isSelectedAllBidContacts()) {
			getManagedBean().setSelectedBidContact(true);
			List<ScBidContactPerson> multiSelectContact = getManagedBean()
					.getMultiSelectContact();
			multiSelectContact.clear();
			for (ScBidContactPerson contactPerson : getManagedBean()
					.getScBidContactPersonList()) {
				multiSelectContact.add(contactPerson);
			}
			getManagedBean().setMultiSelectContact(multiSelectContact);
		} else {
			getManagedBean().getMultiSelectContact().clear();
		}
	}
	
	/**
	 * Listener method to select  bid contact
	 */

	public void selectBidContact(int index) {

		List<ScBidContactPerson> multiSelectContact = getManagedBean()
				.getMultiSelectContact();

		if (getManagedBean().isSelectedBidContact()) {
			multiSelectContact.add(getManagedBean().getScBidContactPersonList()
					.get(index));
		} else {
			if (multiSelectContact != null
					&& multiSelectContact.contains(getManagedBean()
							.getScBidContactPersonList().get(index))) {
				multiSelectContact.remove(getManagedBean()
						.getScBidContactPersonList().get(index));
			}
		}
		getManagedBean().setMultiSelectContact(multiSelectContact);
	}
	
	public void rowSelectListener(ScBidSchedule zoneBid){
		zoneBid.getScBidItems();
		int index = getManagedBean().getBidScheduleList().indexOf(getManagedBean().getRowSelectedBid());
		getManagedBean().getBidScheduleList().set(index, getManagedBean().getRowSelectedBid());
		getManagedBean().setRowSelectedBid(null);
		getManagedBean().setRowSelectedBid(zoneBid);
	}
	
	public void getParticipantsListAction(ScBidSchedule bidSchedule){
		
	}

}

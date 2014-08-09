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
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.sc.bidding.ScBidContactPerson;
import my.ep.persistence.entity.sc.bidding.ScBidItem;
import my.ep.util.MessageUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.managedbean.sc.bidding.ScheduleBiddingManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.primefaces.event.SelectEvent;

/**
 * @author Venkat Chanumolu
 * 
 */

@ManagedBean(name = "scheduleBiddingBB")
@ViewScoped
public class ScheduleBiddingBackingBean extends
		BaseBackingBean<ScheduleBiddingManagedBean> {

	public ScheduleBiddingBackingBean() {
		System.out.println("******retriveItemsToBid() called********");

	}

	public void applyBidDates() {

		System.out.println(" **** Selected start date ***"
				+ this.getManagedBean().getBidStart());
		System.out.println(" ---- Selected end date ----"
				+ this.getManagedBean().getBidEnd());
		System.out.println(" ---- Selected list values ----"
				+ this.getManagedBean().getSelectedBidItemList().size());

		if (getManagedBean().getSelectedBidItemList().size() > 0)

		{
			for (ScBidItem bidItem : getManagedBean().getSelectedBidItemList()) {

				if (getManagedBean().getBidItemList().contains(bidItem)) {

					bidItem.getScBidSchedule().setStartDate(
							getManagedBean().getBidStart());
					bidItem.getScBidSchedule().setEndDate(
							getManagedBean().getBidEnd());

					getManagedBean().getSelectedBidItemList().set(
							getManagedBean().getBidItemList().indexOf(bidItem),
							bidItem);
				}
			}
		} else {

			System.out.println("---- Please select at-least one item ---- ");
		}

	}

	public void addContactAction() {

		try {

			System.out.println("enter addContactAction==");

			List<ScBidContactPerson> contactList = getManagedBean()
					.getScBidContactPersonList();

			if (null == contactList) {
				contactList = new ArrayList<ScBidContactPerson>();
			}

			ScBidContactPerson scBidContactPerson = new ScBidContactPerson();
			contactList.add(scBidContactPerson);
			this.getManagedBean().setScBidContactPersonList(contactList);
			System.out.println(this.getManagedBean()
					.getScBidContactPersonList().size());
		}

		catch (Exception e) { // e.printStackTrace();
			throw new IllegalArgumentException();
		}

	}

	public void deleteContactDetails() {

		try {
			List<ScBidContactPerson> contactList = getManagedBean()
					.getScBidContactPersonList();
			System.out.println("contact list==" + contactList.size());
			List<ScBidContactPerson> multiSelectContacts = getManagedBean()
					.getMultiSelectContact();
			System.out.println("====multiSelectContacts===="
					+ multiSelectContacts.size());
			if (contactList.size() == multiSelectContacts.size())

			{
				System.out.println("==if===");
				contactList.clear();
			} else {
				for (ScBidContactPerson contact : multiSelectContacts)

				{
					System.out.println("else for==");
					contactList.remove(contact);
				}
			}

		}

		catch (Exception e) {

		}

	}

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
			
			if (resDTO.isSuccessful()){
				getManagedBean().setBidParticipants(resDTO.getScQtRecommendDtlList());
			}
		} finally {
			suppFinalizationSO = null;
		}
	}

	public void retriveItemsToBid() {

		Long qtId = 146L;
		IScheduleBiddingSO scheduleBiddingSO = null;

		try {
			scheduleBiddingSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IScheduleBiddingSO.class);
			ScheduleBiddingReqDTO reqDTO = new ScheduleBiddingReqDTO();
			reqDTO.setQtID(qtId);
			ScheduleBiddingResDTO resDTO = scheduleBiddingSO
					.retriveBidScheduleDtls(reqDTO);

			if (resDTO.isSuccessful()) {

				if (resDTO.getScQT() != null) {
					getManagedBean().setScQt(resDTO.getScQT());
				}
				if (resDTO.getScQtFinalization() != null) {

					getManagedBean().setScQtFinalization(
							resDTO.getScQtFinalization());
				}

				if (resDTO.getBidItemsList() != null) {
					getManagedBean().setBidItemList(resDTO.getBidItemsList());
				}
				if (resDTO.getScBid() != null) {
					getManagedBean().setScBid(resDTO.getScBid());
				}
			}
		}

		finally {
			scheduleBiddingSO = null;
		}

		if (getManagedBean().getScQt().isZonal()) {

			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication().getNavigationHandler()
					.handleNavigation(fc, null, "scheduleBidByZone.xhtml");
		} else {
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication().getNavigationHandler()
					.handleNavigation(fc, null, "scheduleBidNormal.xhtml");
		}

	}

	public String saveNonZoneBidsScheduledAction() {
		System.out.println("*****inside saveNonZoneBidsScheduledAction()*****");
		ScheduleBiddingReqDTO reqDTO = new ScheduleBiddingReqDTO();

		// check schedule by package or item and save accordingly
		if (ParameterConst.RECOMMEND_TYPE_PACKAGE.equals(getManagedBean()
				.getScQtFinalization().getRecommendedTypeId()
				.getParameterCode())) {
			System.out.println("******RECOMMEND_TYPE_PACKAGE******");

			getManagedBean().getScBid().getScBidSchedules().get(0)
					.setStartDate(getManagedBean().getBidStart());
			getManagedBean().getScBid().getScBidSchedules().get(0)
					.setEndDate(getManagedBean().getBidEnd());
			PmStatus status = new PmStatus();
			status.setStatusId(63350L);
			// TODO BidStatusConst.BID_PENDING_BIDDING
			getManagedBean().getScBid().getScBidSchedules().get(0)
					.setPmStatus(status);
			getManagedBean().getScBid().setDocType(
					ParameterConst.DOCTYPE_BIDDING);
			getManagedBean().getScBid().setDocId(
					getManagedBean().getScQt().getQtId());
			getManagedBean().getScBid().setDocNo(
					getManagedBean().getScQt().getQtNo());
			getManagedBean().getScBid().setDocTitle(
					getManagedBean().getScQt().getQtTitle());
			getManagedBean().getScBid().setPmOrgProfile(
					getManagedBean().getScQt().getPmOrgProfile());
			// TODO - temporary hard coded
			getManagedBean().getScBid().setEstimatedCost(
					getManagedBean().getScQt().getEstimatedAmount());
			getManagedBean().getScBid().setRecommendedTypeId(
					getManagedBean().getScQtFinalization()
							.getRecommendedTypeId());

			for (ScBidContactPerson contact : getManagedBean()
					.getScBidContactPersonList()) {
				contact.setScBid(getManagedBean().getScBid());
			}
			getManagedBean().getScBid().setScBidContactPersons(
					getManagedBean().getScBidContactPersonList());
			reqDTO.setScBid(getManagedBean().getScBid());
			reqDTO.setScQtFinalization(getManagedBean().getScQtFinalization());

		} else if (ParameterConst.RECOMMEND_TYPE_ITEM.equals(getManagedBean()
				.getScQtFinalization().getRecommendedTypeId()
				.getParameterCode())) {
			System.out.println("******RECOMMEND_TYPE_ITEM******");

			reqDTO.setScQt(getManagedBean().getScQt());
			reqDTO.setScQtFinalization(getManagedBean().getScQtFinalization());
			reqDTO.setScBidContactPersonList(getManagedBean()
					.getScBidContactPersonList());
			reqDTO.setScBidItemList(getManagedBean().getBidItemList());
		}

		IScheduleBiddingSO scheduleSO = null;

		try {
			scheduleSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION,
					IScheduleBiddingSO.class);
			System.out.println("submitNonZoneScheduledBidDetails() called");
			ScheduleBiddingResDTO resDTO = scheduleSO
					.submitNonZoneScheduledBidDetails(reqDTO);

			if (resDTO.isSuccessful()) {

				System.out.println("Scheduled Bids saved Successfully");
				FacesContext
						.getCurrentInstance()
						.addMessage(
								null,
								new FacesMessage(
										FacesMessage.SEVERITY_INFO,
										null,
										MessageUtils
												.getMessage(
														MessageConst.INFO_700026,
														new String[] {
																getParameterDesc(
																		ParameterTypeConst.DOCUMENT_TYPES,
																		ParameterConst.DOCTYPE_BIDDING),
																resDTO.getScBid()
																		.getDocType() },// TODO
														MessageUtils.BUNDLE_COMMON_MESSAGES,
														new FacesScopes()
																.getUserLocale())));
				return CommonUIConst.ACKNOWLEDGE_PAGE;

			} else {
				System.out.println("Scheduled Bids save failed");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			scheduleSO = null;
		}
		return null;
	}

	public void bidStartDateSelectListener(SelectEvent event) {
		Date date = (Date) event.getObject();
		getManagedBean().setMinEndDate(date);
		Calendar maxEndDate = Calendar.getInstance();
		maxEndDate.setTime(date);
		maxEndDate.add(Calendar.HOUR_OF_DAY, 2);
		getManagedBean().setMaxEndDate(maxEndDate.getTime());
	}

	public void bidEndDateSelectListener(SelectEvent event) {

		Date date = (Date) event.getObject();

		String duration = durationCalculator(getManagedBean().getMinEndDate(),
				date);
		getManagedBean().setDuration(duration);

	}

	private String durationCalculator(Date startTime, Date endDate) {

		long durationInMills = endDate.getTime() - startTime.getTime();
		long diffMinutes = durationInMills / (60 * 1000) % 60;
		long diffHours = durationInMills / (60 * 60 * 1000) % 24;
		DecimalFormat durationFormat = new DecimalFormat("#00");
		String duration = durationFormat.format(diffHours) + ":"
				+ durationFormat.format(diffMinutes);
		System.out.println("**** Value from duration calc *******" + duration);
		return duration;
	}

	public String saveZoneWiseScheduledBidAction() {

		getManagedBean().getScBid().setDocType(ParameterConst.DOCTYPE_BIDDING);
		getManagedBean().getScBid().setDocId(
				getManagedBean().getScQt().getQtId());
		getManagedBean().getScBid().setDocNo(
				getManagedBean().getScQt().getQtNo());
		getManagedBean().getScBid().setDocTitle(
				getManagedBean().getScQt().getQtTitle());
		getManagedBean().getScBid().setPmOrgProfile(
				getManagedBean().getScQt().getPmOrgProfile());
		// TODO - temporary hard coded
		getManagedBean().getScBid().setEstimatedCost(
				getManagedBean().getScQt().getEstimatedAmount());
		getManagedBean().getScBid().setRecommendedTypeId(
				getManagedBean().getScQtFinalization().getRecommendedTypeId());

		for (ScBidContactPerson contact : getManagedBean()
				.getScBidContactPersonList()) {
			contact.setScBid(getManagedBean().getScBid());
		}
		getManagedBean().getScBid().setScBidContactPersons(
				getManagedBean().getScBidContactPersonList());

		IScheduleBiddingSO scheduleSO = null;

		try {
			scheduleSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION,
					IScheduleBiddingSO.class);
			ScheduleBiddingReqDTO reqDTO = new ScheduleBiddingReqDTO();
			reqDTO.setScBid(getManagedBean().getScBid());
			reqDTO.setScQtFinalization(getManagedBean().getScQtFinalization());

			System.out.println("submitZoneWiseScheduledBidDetails() called");
			ScheduleBiddingResDTO resDTO = scheduleSO
					.submitZoneWiseScheduledBidDetails(reqDTO);

			if (resDTO.isSuccessful()) {

				System.out
						.println("Scheduled ZoneWise Bids saved Successfully");
				FacesContext
						.getCurrentInstance()
						.addMessage(
								null,
								new FacesMessage(
										FacesMessage.SEVERITY_INFO,
										null,
										MessageUtils
												.getMessage(
														MessageConst.INFO_700026,
														new String[] {
																getParameterDesc(
																		ParameterTypeConst.DOCUMENT_TYPES,
																		ParameterConst.DOCTYPE_BIDDING),
																resDTO.getScBid()
																		.getDocType() },// TODO
														MessageUtils.BUNDLE_COMMON_MESSAGES,
														new FacesScopes()
																.getUserLocale())));
				return CommonUIConst.ACKNOWLEDGE_PAGE;

			} else {
				System.out.println("Scheduled Zone Wise Bids save failed");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			scheduleSO = null;
		}

		return null;

	}

}

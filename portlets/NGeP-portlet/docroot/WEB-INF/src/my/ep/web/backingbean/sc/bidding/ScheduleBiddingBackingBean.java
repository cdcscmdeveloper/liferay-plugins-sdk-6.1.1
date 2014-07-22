/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */

package my.ep.web.backingbean.sc.bidding;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import my.ep.biz.service.sc.quationtender.IQTDetailSO;
import my.ep.biz.service.sc.quationtender.IQTSuppFinalizationSO;
import my.ep.biz.service.sc.schedulebidding.IScheduleBiddingSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.dto.sc.quationtender.QuationTenderReqDTO;
import my.ep.dto.sc.quationtender.QuationTenderResDTO;
import my.ep.dto.sc.quationtender.SuppFinalizationReqDTO;
import my.ep.dto.sc.quationtender.SuppFinalizationResDTO;
import my.ep.dto.sc.schedulebidding.ScheduleBiddingReqDTO;
import my.ep.dto.sc.schedulebidding.ScheduleBiddingResDTO;
import my.ep.persistence.entity.sc.bidding.ScBid;
import my.ep.persistence.entity.sc.bidding.ScBidContactPerson;
import my.ep.persistence.entity.sc.bidding.ScBidItem;
import my.ep.persistence.entity.sc.bidding.ScBidSchedule;
import my.ep.persistence.entity.sc.quationtender.ScQtRecommend;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.sc.bidding.ScheduleBiddingManagedBean;
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
		retriveItemsToBid(146L);

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

	public void retrieveParticipantsListAction() {
		System.out.println("**** Sample ***** ");

	}

	public void retriveItemsToBid(Long qtId) {

		List<ScQtRecommend> recommendList;

		IQTDetailSO qtDetailSO = null;

		try {
			qtDetailSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
			QuationTenderReqDTO qtReqDTO = new QuationTenderReqDTO();
			qtReqDTO.setQtId(qtId);
			QuationTenderResDTO qtResDTO = qtDetailSO.retriveQtById(qtReqDTO);

			if (qtResDTO.getQtDetail() != null) {
				System.out.println("ScQt Object fetched successfully");
				getManagedBean().setScQt(qtResDTO.getQtDetail());
				System.out.println("ScQtFinalization Id is "
						+ qtResDTO.getQtDetail().getFinalization()
								.getFinalizationId());

			}
		}

		finally {
			qtDetailSO = null;
		}

		IQTSuppFinalizationSO qtSuppFinalizationSO = null;

		try {
			qtSuppFinalizationSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTSuppFinalizationSO.class);
			SuppFinalizationReqDTO suppFinalizationReqDTO = new SuppFinalizationReqDTO();
			suppFinalizationReqDTO.setQtId(qtId);
			SuppFinalizationResDTO sfResDTO = qtSuppFinalizationSO
					.retrieveRecommendList(suppFinalizationReqDTO);

			recommendList = sfResDTO.getScQtRecommendList();

		} finally {
			qtSuppFinalizationSO = null;
		}
		List<ScBidItem> bidItemList = new ArrayList<ScBidItem>();

		for (ScQtRecommend recommendItem : recommendList) {

			System.out.println("*********iterating recommendList**********");
			ScBidItem item = new ScBidItem();
			item.setScRequestItem(recommendItem.getRequestItem());
			/*
			 * BigDecimal estimatedPrice =
			 * recommendItem.getTplSpecZoneDtl().getTplSpecEstimationList
			 * ().get(0).getEstimatedCost(); Long itemEstimatedPrice =
			 * estimatedPrice.longValue(); if (itemEstimatedPrice < 50000){
			 * item.setDecrementAmt(1l); }else if(itemEstimatedPrice >= 50000 &&
			 * itemEstimatedPrice < 500000) { item.setDecrementAmt(200l); }else
			 * if(itemEstimatedPrice >= 500000 && itemEstimatedPrice < 1000000)
			 * { item.setDecrementAmt(1000l); }else if(itemEstimatedPrice >=
			 * 1000000){ item.setDecrementAmt(2500l); }
			 */
			//item.setDecrementAmt(200L);
			item.setScBidSchedule(new ScBidSchedule());
			bidItemList.add(item);
		}
		getManagedBean().setBidItemList(bidItemList);
	}

	
	
	public void saveNonZoneBidsScheduledAction() {
		System.out.println("inside saveNonZoneBidsScheduledAction()");

		ScBid bid = new ScBid();
		System.out.println("new ScBid object constructed");

		List<ScBidSchedule> scheduledBidList = new ArrayList<ScBidSchedule>();

		System.out.println("new scheduledBidList object constructed");

		// check schedule by package or item and save accordingly

		if (ParameterConst.RECOMMEND_TYPE_PACKAGE.equals(getManagedBean()
				.getScQt().getFinalization().getRecommendedTypeId())) {

			System.out.println("******RECOMMEND_TYPE_PACKAGE******");

			ScBidSchedule bidSchedule = new ScBidSchedule();
			System.out
					.println("******for RECOMMEND_TYPE_PACKAGE bidSchedule object constructed******");
			bidSchedule.setStartDate(getManagedBean().getBidItemList().get(0)
					.getScBidSchedule().getStartDate());
			bidSchedule.setEndDate(getManagedBean().getBidItemList().get(0)
					.getScBidSchedule().getEndDate());
			bidSchedule.setScBid(bid);
			System.out
					.println("******for RECOMMEND_TYPE_PACKAGE, bidSchedule object startDate and endDate are set******");
			for (ScBidItem item : getManagedBean().getBidItemList()){
				item.setScBidSchedule(bidSchedule);
			}
			bidSchedule.setScBidItems(getManagedBean().getBidItemList());
			System.out
					.println("******for RECOMMEND_TYPE_PACKAGE, bidSchedule object bidItemList set******");
			scheduledBidList.add(bidSchedule);

		} else {

			System.out.println("******RECOMMEND_TYPE_ITEM******");

			List<ScBidItem> compareWithItemList = getManagedBean()
					.getBidItemList();

			System.out.println("*****compareWithItemList size is****"
					+ compareWithItemList.size());

			System.out
					.println("******compareWithItemList populated with bidItems******");

			for (ScBidItem bidItem : getManagedBean().getBidItemList()) {

				Date startDate = bidItem.getScBidSchedule().getStartDate();
				Date endDate = bidItem.getScBidSchedule().getEndDate();

				System.out
						.println("****** inside foreach loop for grouping  bidItems ******");

				if (compareWithItemList.contains(bidItem)) {

					List<ScBidItem> bidItemList = new ArrayList<ScBidItem>();
					ScBidSchedule bidSchedule = new ScBidSchedule();
					bidSchedule.setStartDate(startDate);
					bidSchedule.setEndDate(endDate);
					bidSchedule.setScBid(bid);
					
					System.out
							.println("******bidItemList constructed for newScBidSchedule******");
					
					bidItem.setScBidSchedule(bidSchedule);
					bidItemList.add(bidItem);
					
					
					
					System.out
							.println("******comapring Item set to bidItemList******");
					
					compareWithItemList.remove(bidItem);
					
					System.out
							.println("******comapring Item found in compareWithItemList and removed******");
					System.out.println("*****compareWithItemList size is****"
							+ compareWithItemList.size());

					
					if (compareWithItemList.size() != 0) {

						for (ScBidItem compareItem : compareWithItemList) {

							System.out
									.println("******inside foreach loop comapring Item with each item from compareWithItemList ******");
							System.out
									.println("*****compareWithItemList size is****"
											+ compareWithItemList.size());
							
							if(compareItem
									.getScBidSchedule().getStartDate()==null){
								System.out.println("*****start date is null*****");
								
								if (compareItem
										.getScBidSchedule().getEndDate()==null)
								{System.out.println("*****end date is null*****");}
							}

							if (startDate.compareTo(compareItem
									.getScBidSchedule().getStartDate()) == 0
									&& endDate.compareTo(compareItem
											.getScBidSchedule().getEndDate()) == 0) {

								 compareItem.setScBidSchedule(bidSchedule);
								bidItemList.add(compareItem);
								System.out
										.println("****** Item mtched with item from compareWithItemList and grouped ******");
								if (compareWithItemList.contains(compareItem)) {
									compareWithItemList.remove(compareItem);
									System.out.println("found Item removed");
								}
							}
						}

					}

					bidSchedule.setScBidItems(bidItemList);
					System.out
							.println("new bidSchedule constructed and set with startDate,endDate,bidItemList");
					scheduledBidList.add(bidSchedule);
					System.out
							.println("bidSchedule object added to scheduledBidList");
				}

			}

		}

		bid.setScBidSchedules(scheduledBidList);
		System.out.println("scheduledBidList set to ScBid object");

		if (getManagedBean().getScBidContactPersonList() != null
				&& getManagedBean().getScBidContactPersonList().size() != 0) {
			for(ScBidContactPerson contact: getManagedBean().getScBidContactPersonList()){
				contact.setScBid(bid);
			}
			bid.setScBidContactPersons(getManagedBean()
					.getScBidContactPersonList());
			System.out
					.println("bidContactPersonList is not null and size not zero added to ScBid");
		}
		bid.setDocType(ParameterConst.DOCTYPE_BIDDING);
		System.out.println("DOCTYPE_BIDDING set to ScBid Object");
		bid.setDocId(getManagedBean().getScQt().getQtId());

		System.out.println("****DocId set with QtId****"
				+ getManagedBean().getScQt().getQtId());

		bid.setDocNo(getManagedBean().getScQt().getQtNo());
		System.out.println("****DocNo set with QtNo****"
				+ getManagedBean().getScQt().getQtNo());

		bid.setDocTitle(getManagedBean().getScQt().getQtTitle());
		System.out.println("****DocTitle set with QtTitle****"
				+ getManagedBean().getScQt().getQtTitle());

		bid.setPmOrgProfile(getManagedBean().getScQt().getPmOrgProfile());
		System.out
				.println("****PmOrgProfile set with PmOrgProfile from ScQt****"
						+ getManagedBean().getScQt().getQtTitle());
		// TODO - temporary hard coded
		bid.setRecommendedTypeId(getManagedBean().getScQt().getFinalization()
				.getRecommendedTypeId());
		// System.out.println("****RecommendedTypeId set with RecommendedTypeId from ScQt Finalization****"+getManagedBean().getScQt().getFinalization()
		// .getRecommendedTypeId());
		bid.setEstimatedCost(getManagedBean().getScQt().getEstimatedAmount());
		System.out
				.println("****RecommendedTypeId set with RecommendedTypeId from ScQt Finalization****"
						+ getManagedBean().getScQt().getEstimatedAmount());

		IScheduleBiddingSO scheduleSO;

		try {
			scheduleSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION,
					IScheduleBiddingSO.class);
			ScheduleBiddingReqDTO reqDTO = new ScheduleBiddingReqDTO();
			reqDTO.setScBid(bid);

			System.out.println("reqDTO set with ScBid Object");
			ScheduleBiddingResDTO resDTO = scheduleSO
					.submitNonZoneScheduledBidDetails(reqDTO);
			System.out.println("submitNonZoneScheduledBidDetails() called");

			if (resDTO.isSuccessful()) {

				System.out.println("Scheduled Bids Updated Successfully");
			} else {
				System.out.println("Scheduled Bids Updated failed");
			}
		}

		catch (Exception e) {

			e.printStackTrace();
		} finally {
			scheduleSO = null;
		}

	}

	
	
	
	
	
	public void bidStartDateSelectListener(SelectEvent event) {

		Date date = (Date) event.getObject();

	}

	public void bidEndDateSelectListener(SelectEvent event) {

		Date date = (Date) event.getObject();

		Long duration = durationCalculator(new Date(), new Date());
	}

	private Long durationCalculator(Date pausedTime, Date endDate) {

		long durationInMills = endDate.getTime() - pausedTime.getTime();

		long time = TimeUnit.MINUTES.convert(durationInMills,
				TimeUnit.MILLISECONDS);

		Long hours = time % 60;
		Long minutes = time / 60;

		System.out.println("remaining time is " + hours + "hours" + minutes
				+ "minutes");
		System.out.println("**** Value from duration calc *******" + time);
		return time;

		/*
		 * long diff = d2.getTime() - d1.getTime(); long diffSeconds = diff /
		 * 1000 % 60; long diffMinutes = diff / (60 * 1000) % 60; long diffHours
		 * = diff / (60 * 60 * 1000); int diffInDays = (int) diff / (1000 * 60 *
		 * 60 * 24);
		 * System.out.println(diffInDays+"  days");
		 * System.out.println(diffHours+"  Hour");
		 * System.out.println(diffMinutes+"  min");
		 * System.out.println(diffSeconds+"  sec");
		 */
	}

	public void saveZoneWiseScheduledBidAction() {

	}

}

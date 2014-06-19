/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */

package my.ep.web.backingbean.sc.bidding;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import my.ep.biz.service.sc.schedulebidding.IScheduleBiddingSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.sc.schedulebidding.ScheduleBiddingReqDTO;
import my.ep.dto.sc.schedulebidding.ScheduleBiddingResDTO;
import my.ep.persistence.entity.sc.bidding.ScBidContactPerson;
import my.ep.persistence.entity.sc.bidding.ScBidItem;
import my.ep.persistence.entity.sc.bidding.ScBidSchedule;
import my.ep.persistence.entity.sc.directpurchase.ScRequestItem;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.sc.bidding.ScheduleBiddingManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;

/**
 * @author Venkat Chanumolu
 * 
 */

@ManagedBean(name = "scheduleBiddingBB")
@RequestScoped
public class ScheduleBiddingBackingBean extends
		BaseBackingBean<ScheduleBiddingManagedBean> {

	public ScheduleBiddingBackingBean() {
		System.out.println("******retriveScheduleBidDetails called********");
		retriveScheduleBidDetails();
	}

	public void applyBidDates() {

		System.out.println(" **** Selected start date ***S"
				+ this.getManagedBean().getBidStart());
		System.out.println(" ---- Selected end date ----"
				+ this.getManagedBean().getBidEnd());
		System.out.println(" ---- Selected list values ----"
				+ this.getManagedBean().getSelectedScBidScheduleList().size());

		if (getManagedBean().getSelectedScBidScheduleList().size() > 0)

		{
			for (ScBidSchedule bidSchedule : getManagedBean()
					.getSelectedScBidScheduleList()) {

				if (getManagedBean().getScBid().getScBidSchedules()
						.contains(bidSchedule)) {

					bidSchedule.setStartDate(getManagedBean().getBidStart());
					bidSchedule.setEndDate(getManagedBean().getBidEnd());

					getManagedBean()
							.getScBid()
							.getScBidSchedules()
							.set(getManagedBean().getScBid()
									.getScBidSchedules().indexOf(bidSchedule),
									bidSchedule);
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

	public void getParticipantsListAction() {
		System.out.println("**** Sample ***** ");

	}

	public void retriveScheduleBidDetails() {

		IScheduleBiddingSO scheduleSO;
		try {

			ScheduleBiddingReqDTO reqDTO = new ScheduleBiddingReqDTO();
			ScheduleBiddingResDTO resDTO = new ScheduleBiddingResDTO();

			scheduleSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION,
					IScheduleBiddingSO.class);
			if (this.getManagedBean().getScQt() == null) {
				reqDTO.setQtId(146l);

				resDTO = scheduleSO.retriveScheduleBidDetails(reqDTO);

				if (resDTO.getScQT() != null) {

					System.out.println("ScQt is not null");

					this.getManagedBean().setScQt(resDTO.getScQT());
				}

				List<ScBidSchedule> scheduleList = new ArrayList<ScBidSchedule>();

				if (resDTO.getScRequestItemList() != null) {

					System.out
							.println("*******++++++++requestItemlist is not null and size is not zero++++++++********");
					if (resDTO.getScRequestItemList().size() != 0) {

						System.out
								.println("*******++++++++requestItemlist size is not zero++++++++********");

						for (ScRequestItem reqItem : resDTO
								.getScRequestItemList()) {

							System.out
									.println("*********iterating requestItemsList**********");
							ScBidItem item = new ScBidItem();

							item.setScRequestItem(reqItem);
							item.setDecrementAmt(BigDecimal.valueOf(200));

							List<ScBidItem> bidItems = new ArrayList<ScBidItem>();

							bidItems.add(item);

							ScBidSchedule schedule = new ScBidSchedule();

							schedule.setScBidItems(bidItems);

							scheduleList.add(schedule);

						}
					} else {
						System.out
								.println("******++++++++requestItemList is empty+++++++*******");
					}

					this.getManagedBean().getScBid()
							.setScBidSchedules(scheduleList);

				} else {
					System.out
							.println("*********request items list is null**********");
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			scheduleSO = null;
		}

	}

	public void saveScheduledBidAction() {
		IScheduleBiddingSO scheduleSO;

		try {
			scheduleSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION,
					IScheduleBiddingSO.class);

			this.getManagedBean()
					.getScBid()
					.setScBidContactPersons(
							this.getManagedBean().getScBidContactPersonList());

			ScheduleBiddingReqDTO reqDTO = new ScheduleBiddingReqDTO();
			reqDTO.setScBid(this.getManagedBean().getScBid());

			scheduleSO.submitScheduleBidDetails(reqDTO);
		}

		catch (Exception e) {

			e.printStackTrace();
		} finally {
			scheduleSO = null;
		}

	}

}

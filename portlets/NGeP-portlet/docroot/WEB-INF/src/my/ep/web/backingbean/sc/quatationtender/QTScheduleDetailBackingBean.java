package my.ep.web.backingbean.sc.quatationtender;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.common.PmAddress;
import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmCountry;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtBriefing;
import my.ep.persistence.entity.sc.quationtender.ScQtContactPerson;
import my.ep.web.bean.sc.quatationtender.BriefingZoneBean;
import my.ep.web.managedbean.sc.quationtender.QTDetailManagedBean;

import org.apache.log4j.Logger;
import org.primefaces.model.TreeNode;

/**
 * @author SudheerKumar
 * 
 */
@ManagedBean(name = "qTScheduleDetailBackingBean")
@RequestScoped
public class QTScheduleDetailBackingBean extends
		SourcingBaseBackingBean<QTDetailManagedBean> {
	private static final Logger log = EpLogger
			.getLogger(QTScheduleDetailBackingBean.class);

	private void debugLogger(Object message) {
		if (log.isDebugEnabled()) {
			log.debug(message);
		}
		log.info(message);
	}

	private Boolean checked;

	public Boolean getChecked() {
		return checked;
	}

	public void setChecked(Boolean checked) {
		this.checked = checked;
	}

	public QTScheduleDetailBackingBean() {
		ScQtBriefing briefing = new ScQtBriefing();

		briefing.setPmAddress(new PmAddress());
		briefing.getPmAddress().setPmCountry(new PmCountry());
		briefing.getPmAddress().setPmState(new PmState());
		briefing.getPmAddress().setPmDistrict(new PmDistrict());
		briefing.getPmAddress().setPmCity(new PmCity());
		//briefing.setScQtZoneCriteria(new ScQtZoneCriteria());
		this.getManagedBean().setQtBriefing(briefing);

		ScQtBriefing siteBriefing = new ScQtBriefing();
		siteBriefing.setPmAddress(new PmAddress());
		siteBriefing.getPmAddress().setPmCountry(new PmCountry());
		siteBriefing.getPmAddress().setPmState(new PmState());
		siteBriefing.getPmAddress().setPmDistrict(new PmDistrict());
		siteBriefing.getPmAddress().setPmCity(new PmCity());
		//siteBriefing.setScQtZoneCriteria(new ScQtZoneCriteria());
		this.getManagedBean().setQtSiteBriefing(siteBriefing);

		List<TreeNode> list = getManagedBean().getZoneTrees();
		for (TreeNode treeNode : list) {
			System.out.println("Tree Node : " + treeNode.getData());
			if (treeNode.getChildCount() != 0) {
				treeNode.getChildren().clear();
			}
		}
		this.getManagedBean().setTreeNodesList(list);

		//getManagedBean().getQtCriteria().setMultiLocality(false);

		// Briefing Zone Bean
		List<BriefingZoneBean> briefingZoneBeans = getManagedBean()
				.getBriefingZoneBean();

		if (null == briefingZoneBeans) {
			briefingZoneBeans = new ArrayList<BriefingZoneBean>();
		}

		/*
		 * List<ScQtBriefing> test1 = getManagedBean().getTest(); if(null ==
		 * test1) { test1 = new ArrayList<ScQtBriefing>(); } test1.add(new
		 * ScQtBriefing()); System.out.println("Size : " + test1.size());
		 * getManagedBean().setTest(test1);
		 */

		if (this.getManagedBean().getQt() == null) {
			this.getManagedBean().setQt(new ScQt());
		}
	}

	public void addBriefingDetails() {
		debugLogger("addBriefingDetails");
		List<ScQtBriefing> briefingdetails = this.getManagedBean()
				.getBriefings();
		if (briefingdetails == null) {
			briefingdetails = new ArrayList<ScQtBriefing>();
		}

		// briefingdetails.add(this.getManagedBean().getQtBriefing());

		this.getManagedBean().setBriefings(briefingdetails);

	}

	public void addSiteDetails() {
		List<ScQtBriefing> sitedetails = this.getManagedBean()
				.getSiteBriefings();
		if (sitedetails == null) {
			sitedetails = new ArrayList<ScQtBriefing>();

		}

		// sitedetails.add(this.getManagedBean().getQtSiteBriefing());
		this.getManagedBean().setSiteBriefings(sitedetails);

	}

	public void addToBrefieyingList() {
		List<ScQtBriefing> brefings = this.getManagedBean().getBriefings();
		ScQtBriefing selectedBrefing = this.getManagedBean().getQtBriefing();
		if (null == brefings) {
			brefings = new ArrayList<ScQtBriefing>();
		}
		if (null != selectedBrefing) {
			brefings.add(selectedBrefing);
		}

		// this.getManagedBean().getBriefings().add(selectedBrefing);

		this.getManagedBean().getBriefingZoneBean().get(0)
				.setBriefingList(brefings);

	}

	public void addToSiteBriefingList() {
		List<ScQtBriefing> siteBriefings = this.getManagedBean()
				.getSiteBriefings();
		ScQtBriefing selectedSiteBriefing = this.getManagedBean()
				.getQtSiteBriefing();
		if (null == siteBriefings) {
			siteBriefings = new ArrayList<ScQtBriefing>();
		}
		if (null != selectedSiteBriefing) {
			siteBriefings.add(selectedSiteBriefing);
		}
	}

	public void addContactPerson() {
		debugLogger("addContactPerson");
		ScQtContactPerson contactperson = new ScQtContactPerson();
		try {
			List<ScQtContactPerson> contactList = this.getManagedBean()
					.getScQtContactPersons();
			if (null == contactList) {
				contactList = new ArrayList<ScQtContactPerson>();
			}
			for (ScQtContactPerson scQtContactPerson : contactList) {
				scQtContactPerson.setBrandNew(false);
			}
			contactList.add(contactperson);
			this.getManagedBean().setScQtContactPersons(contactList);
		} catch (Exception e) {
			throw new IllegalArgumentException();
		}
	}

	public void addToSiteVisitDtl() {
		List<ScQtBriefing> list = getManagedBean().getSiteBriefings();
		if (list == null) {
			list = new ArrayList<ScQtBriefing>();
		}
		System.out.println("size : "
				+ getManagedBean().getSelectedBriefingDetails());
		for (ScQtBriefing scQtBriefing : getManagedBean()
				.getSelectedBriefingDetails()) {
			list.add(scQtBriefing);
		}
		getManagedBean().setSiteBriefings(list);
		System.out.println("Breifieng Dtl : "
				+ getManagedBean().getSiteBriefings().size());
	}

	public void deleteContactPerson() {
		List<ScQtContactPerson> contactPersonsList = getManagedBean()
				.getScQtContactPersons();
		List<ScQtContactPerson> selectcontactPersonsList = getManagedBean()
				.getSelectedContactPersons();
		if (contactPersonsList.size() == selectcontactPersonsList.size()) {
			contactPersonsList.clear();
		} else {
			for (ScQtContactPerson scQtContactPerson : selectcontactPersonsList) {
				contactPersonsList.remove(scQtContactPerson);
			}
		}
	}

	public void deleteZoneBriefingDetails() {
		debugLogger("deleteZoneBriefingDetails");
		List<ScQtBriefing> beans = getManagedBean().getBriefingZoneBean()
				.get(0).getBriefingList();
		List<ScQtBriefing> selectList = getManagedBean()
				.getSelectedBriefingDetails();
		if (beans.size() == selectList.size()) {
			beans.clear();
		} else {
			for (ScQtBriefing scQtBriefing : selectList) {
				beans.remove(scQtBriefing);
			}
		}
	}

	public void deleteBriefingDetails() {
		debugLogger("deleteBriefingDetails");
		List<ScQtBriefing> beans = getManagedBean().getBriefings();
		List<ScQtBriefing> selectList = getManagedBean()
				.getSelectedBriefingDetails();
		if (beans.size() == selectList.size()) {
			beans.clear();
		} else {
			for (ScQtBriefing scQtBriefing : selectList) {
				beans.remove(scQtBriefing);
			}
		}
	}

}

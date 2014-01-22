package my.ep.web.backingbean.ss.quatationtender;

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
import my.ep.persistence.entity.ss.quationtender.ScQtBriefing;
import my.ep.persistence.entity.ss.quationtender.ScQtContactPerson;
import my.ep.persistence.entity.ss.quationtender.ScQtZoneCriteria;
import my.ep.web.managedbean.ss.quationtender.QTDetailManagedBean;

import org.apache.log4j.Logger;

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
		briefing.setScQtZoneCriteria(new ScQtZoneCriteria());
		this.getManagedBean().setQtBriefing(briefing);

		ScQtBriefing siteBriefing = new ScQtBriefing();
		siteBriefing.setPmAddress(new PmAddress());
		siteBriefing.getPmAddress().setPmCountry(new PmCountry());
		siteBriefing.getPmAddress().setPmState(new PmState());
		siteBriefing.getPmAddress().setPmDistrict(new PmDistrict());
		siteBriefing.getPmAddress().setPmCity(new PmCity());
		siteBriefing.setScQtZoneCriteria(new ScQtZoneCriteria());
		this.getManagedBean().setQtSiteBriefing(siteBriefing);

	}

	public void addBriefingDetails() {
		System.out.println("add brief");
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

}

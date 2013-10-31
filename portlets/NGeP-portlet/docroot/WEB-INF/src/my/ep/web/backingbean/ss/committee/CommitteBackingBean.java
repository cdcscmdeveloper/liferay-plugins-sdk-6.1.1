package my.ep.web.backingbean.ss.committee;

import java.sql.Timestamp;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import my.ep.biz.service.ss.committe.ICommitteSO;
import my.ep.dto.ss.committe.CommitteReqDTO;
import my.ep.dto.ss.committe.CommitteResDTO;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.ss.quationtender.ScCommittee;
import my.ep.persistence.entity.ss.quationtender.ScQtCommittee;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.ss.committee.CommitteManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;

/**
 * @author VidyasagarReddy
 * 
 */
@ManagedBean(name = "committeBBean")
@RequestScoped
public class CommitteBackingBean extends BaseBackingBean<CommitteManagedBean> {
	private static final Logger logger = EpLogger
			.getLogger(CommitteBackingBean.class);
	private static final String MODULE_NAME = "ss";
	private ICommitteSO committeSO;

	public CommitteBackingBean() {
		logger.info("inside committe backing bean constructor");
	}

	public void initialize() {
		committeSO = BusinessDelegate.getInstance().getServiceObject(
				MODULE_NAME, ICommitteSO.class);
		logger.info("Successfully created So object for Committe"+committeSO);
	}

	/**
	 * Method to save the committe details.
	 */
	public void saveCommitte() {
		// TODO Need to get the committe object from managed bean.
		ScQtCommittee committee = new ScQtCommittee();

		// Need to remove below 3 properties
		committee.setChangedBy(1l);
		committee.setCreatedBy(1l);
		committee.setCreatedDate(new Timestamp(System.currentTimeMillis()));

		// TODO need to get the sccommitte object from the managed bean
		ScCommittee scCommittee = new ScCommittee();

		// Need to remove below properties
		scCommittee.setChangedBy(10l);
		scCommittee.setChangedDate(new Timestamp(10l));
		scCommittee.setCommitteeType("TEC");
		scCommittee.setCreatedBy(1l);
		scCommittee.setCreatedDate(new Timestamp(100l));
		scCommittee.setRecordStatus(Boolean.TRUE);
		scCommittee.setIsApprovalReq(Boolean.TRUE);
		scCommittee.setStatusId(1);
		committee.setScCommittee(scCommittee);

		CommitteReqDTO committeReqDTO = new CommitteReqDTO();
		committeReqDTO.setQtCommittee(committee);
		CommitteResDTO resDTO = committeSO.saveOrUpdateCommitte(committeReqDTO);

	}
}

package my.ep.web.backingbean.ss.loiloa;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import my.ep.biz.service.ss.quationtender.IQTAwardSO;
import my.ep.biz.service.ss.quationtender.IQTDetailSO;
import my.ep.dto.ss.quationtender.AwardSupplierReqDTO;
import my.ep.dto.ss.quationtender.AwardSupplierResDTO;
import my.ep.dto.ss.quationtender.QuationTenderReqDTO;
import my.ep.dto.ss.quationtender.QuationTenderResDTO;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.ss.quationtender.ScQtProposalRanking;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.ss.quatationtender.QTDetailBackingBean;
import my.ep.web.managedbean.ss.loiloa.LOILOADetailManagedBean;
import my.ep.web.ss.quationtender.common.QTConstants;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;

/**
 * @author Viswanath
 * 
 */
@ManagedBean(name = "loiLoaDetailBackingBean")
@RequestScoped
public class LOILOADetailsBackingBean extends
		BaseBackingBean<LOILOADetailManagedBean> {
	public static Logger log = EpLogger
			.getLogger(LOILOADetailsBackingBean.class);

	private static final String MODULE_NAME = "ss";

	public LOILOADetailsBackingBean() {
		log.info("inside LOILOADetailsBackingBean constructor");
		retrieveLOILOAList();

	}

	/**
	 * 
	 */
	private void retrieveQTDtls() {
		debugLogger("retrieveQTDtls");

		IQTDetailSO detailSO = null;
		try {
			detailSO = EpEjbBusinessDelegate.getInstance().getServiceObject(
					QTConstants.SS_MODULE, IQTDetailSO.class);
			QuationTenderReqDTO tenderReqDTO = new QuationTenderReqDTO();
			tenderReqDTO.setQtId(146L);
			QuationTenderResDTO tenderResDTO = detailSO
					.retriveQTDetails(tenderReqDTO);
			QTDetailBackingBean detailBackingBean = new QTDetailBackingBean();
			detailBackingBean.getManagedBean()
					.setQt(tenderResDTO.getQtDetail());
			detailBackingBean.populateParentOrgProfile(tenderResDTO
					.getQtDetail());

			System.out.println("Qtn/Tender No : "
					+ detailBackingBean.getManagedBean().getQt().getQtNo());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@PostConstruct
	public void init() {
		retrieveQTDtls();
	}

	private void debugLogger(Object message) {
		if (log.isDebugEnabled()) {
			log.debug(message);
		}
		log.info(message);
	}

	/**
	 * retrieving LOI LOA List
	 */
	private void retrieveLOILOAList() {
		debugLogger("retrieveLOILOAList");

		IQTAwardSO qtAwardSO = null;
		try {
			qtAwardSO = BusinessDelegate.getInstance().getServiceObject(
					MODULE_NAME, IQTAwardSO.class);
			AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();
			reqDTO.setQtId(146L);

			AwardSupplierResDTO resDTO = qtAwardSO.retrieveLOILOAList(reqDTO);

			List<ScQtProposalRanking> list = resDTO
					.getProposalRankingResultBean().getResultList();
			System.out.println("list: " + list.size());
			for (ScQtProposalRanking scQtProposalRanking : list) {
				System.out.println("Proposal Ranking Id : "
						+ scQtProposalRanking.getQtProposalRankingId());
			}
			System.out.println("Supplier Name : " + list.get(0).getScQtProposalEvaluate().getScQtProposal().getScQtSupplier().getSmSupplier().getSupplierName());
			System.out.println("LOI Status : " + list.get(0).getScLoi().getPmStatus().getPmStatusDescs().get(0).getStatusName());
			System.out.println("LOA Status : " + list.get(0).getScLoa().getPmStatus().getPmStatusDescs().get(0).getStatusName());
			
			getManagedBean().setScQtProposalRankingList(list);
		} catch (Exception ex) {
			ex.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							ex.toString(), ex.getMessage()));
		}
	}

}

package my.ep.web.backingbean.ss.loiloa;

import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import my.ep.biz.service.ss.quationtender.IQTAwardSO;
import my.ep.dto.ss.quationtender.AwardSupplierReqDTO;
import my.ep.dto.ss.quationtender.AwardSupplierResDTO;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.ss.quationtender.ScQt;
import my.ep.persistence.entity.ss.quationtender.ScQtEvaluationResult;
import my.ep.web.backingbean.ss.quatationtender.SourcingBaseBackingBean;
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
		SourcingBaseBackingBean<LOILOADetailManagedBean> {
	public static Logger log = EpLogger
			.getLogger(LOILOADetailsBackingBean.class);

	//private static final String MODULE_NAME = "ss";

	public LOILOADetailsBackingBean() {
		log.info("inside LOILOADetailsBackingBean constructor");
		retrieveLOILOAList();

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
					QTConstants.SS_MODULE, IQTAwardSO.class);
			AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();
			reqDTO.setQtId(146L);

			AwardSupplierResDTO resDTO = qtAwardSO.retrieveLOILOAList(reqDTO);
			
			ScQt scQt = resDTO.getScQt();
			if (scQt != null) {
				System.out.println("Qt Id and Qt No : " + scQt.getQtId() + "|"
						+ scQt.getQtNo());
				System.out.println("Award Type : " + scQt.getAwardType());
			}
			getManagedBean().setQt(scQt);

			List<ScQtEvaluationResult> list = resDTO.getQtEvaluationResultBean().getResultList();
			System.out.println("list: " + list.size());
			
			for (ScQtEvaluationResult scQtEvaluationResult : list) {
				System.out.println(scQtEvaluationResult.getEvaluationResultId()
				 + "|" + scQtEvaluationResult.getScQtProposal().getScQtSupplier().getSmSupplier().getCompanyName());
			}

			getManagedBean().setScQtEvaluationResultList(list);
			getManagedBean().setQt(resDTO.getScQt());
			populateParentOrgProfile(resDTO.getScQt());

		} catch (Exception ex) {
			ex.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							ex.toString(), ex.getMessage()));
		}
	}

}

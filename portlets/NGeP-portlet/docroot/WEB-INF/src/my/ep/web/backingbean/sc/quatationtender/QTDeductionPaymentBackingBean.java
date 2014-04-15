package my.ep.web.backingbean.sc.quatationtender;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import my.ep.biz.service.sc.quationtender.IQTDetailSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.dto.sc.quationtender.QuationTenderReqDTO;
import my.ep.dto.sc.quationtender.QuationTenderResDTO;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.sc.quationtender.ScQtDeduction;
import my.ep.persistence.entity.sc.quationtender.ScQtDeductionClause;
import my.ep.web.managedbean.sc.quationtender.QTDetailManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.ParameterHelper;

import org.apache.log4j.Logger;

/**
 * @author Sudheer Kumar
 * 
 */
@ManagedBean(name = "qTDeductionPaymentBackingBean")
@RequestScoped
public class QTDeductionPaymentBackingBean extends
		SourcingBaseBackingBean<QTDetailManagedBean> {
	private static final Logger log = EpLogger
			.getLogger(QTDeductionPaymentBackingBean.class);

	private void debugLogger(Object message) {
		if (log.isDebugEnabled()) {
			log.debug(message);
		}
		log.info(message);
	}

	public QTDeductionPaymentBackingBean() {
	}

	public void addDedcutionPayment() {
		debugLogger("addDedcutionPayment");
		ScQtDeduction qtDeduction = new ScQtDeduction();
		qtDeduction.setDeductionType(new PmParameter());
		qtDeduction.setScQt(getManagedBean().getQt());

		try {
			if (this.getManagedBean().getScQtDeductionList() == null) {
				this.getManagedBean().setScQtDeductionList(new ArrayList());
			}
			this.getManagedBean().getScQtDeductionList().add(qtDeduction);

		} catch (Exception e) {
			throw new IllegalArgumentException();
		}
	}

	public void addDeductionClause() {
		debugLogger("addDeductionClause");
		List<ScQtDeductionClause> clauses = this.getManagedBean()
				.getScQtDeductionClauses();
		if (clauses == null) {
			clauses = new ArrayList<ScQtDeductionClause>();
		}
		this.getManagedBean().setQtDeductionClause(null);
		ScQtDeductionClause qtDeductionClause = new ScQtDeductionClause();
		qtDeductionClause.setScQtDeduction(getManagedBean().getQtDeduction());
		this.getManagedBean().setQtDeductionClause(qtDeductionClause);
		clauses.add(this.getManagedBean().getQtDeductionClause());
		this.getManagedBean().setScQtDeductionClauses(clauses);

	}

	public void deductionPaymentEvent(ScQtDeduction scQtDeduction) {
		debugLogger("deductionPaymentEvent");
		IQTDetailSO detailSO;
		PmParameter p = scQtDeduction.getDeductionType();
		List<PmParameterDesc> list = retrieveDeductionType(ParameterTypeConst.DEDUCTION_TYPE);
		if (list != null) {
			for (PmParameterDesc pmParameterDesc : list) {
				if (pmParameterDesc.getPmParameter().getParameterId()
						.equals(p.getParameterId())) {
					getManagedBean().setQtDeduction(scQtDeduction);
					detailSO = BusinessDelegate.getInstance().getServiceObject(
							ModuleCodeConst.SUPPLIER_SELECTION,
							IQTDetailSO.class);
					QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
					Long deductionId = getManagedBean().getQtDeduction()
							.getDeductionType().getParameterId();
					reqDTO.setDeductionId(deductionId);
					if (deductionId != null) {
						QuationTenderResDTO resDTO = detailSO
								.retrieveDeDuctionClauseDtls(reqDTO);
						List<ScQtDeductionClause> deductionClauseList = resDTO
								.getDeductionClauseResultBean().getResultList();
						for (ScQtDeductionClause scQtDeductionClause : deductionClauseList) {
							scQtDeductionClause.setClauseDescRendered(String
									.valueOf(deductionId));
						}
						getManagedBean().setScQtDeductionClauses(
								deductionClauseList);
					}
				}
			}
		}
	}

	public void deleteDedcutionPayment() {
		debugLogger("deleteDedcutionPayment");
		List<ScQtDeduction> deductionList = getManagedBean()
				.getScQtDeductionList();
		List<ScQtDeduction> selectedDeductionList = getManagedBean()
				.getSelectedScQtDeduction();
		if (deductionList.size() == selectedDeductionList.size()) {
			deductionList.clear();
		} else {
			for (ScQtDeduction scQtDeduction : selectedDeductionList) {
				deductionList.remove(scQtDeduction);
			}
		}
	}

	public List<PmParameterDesc> retrieveDeductionType(String type) {
		return ParameterHelper.getInstance().getParameterDescList(type);
	}
}

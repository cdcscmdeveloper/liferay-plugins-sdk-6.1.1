package my.ep.web.backingbean.sc.quatationtender;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.sc.quationtender.ScQtDeduction;
import my.ep.persistence.entity.sc.quationtender.ScQtDeductionClause;
import my.ep.web.managedbean.sc.quationtender.QTDetailManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;

/**
 * @author Sudheer Kumar
 * 
 */
@ManagedBean(name = "qTDeductionPaymentBackingBean")
@RequestScoped
public class QTDeductionPaymentBackingBean extends SourcingBaseBackingBean<QTDetailManagedBean> {
	private static final Logger log = EpLogger.getLogger(QTDeductionPaymentBackingBean.class);

	private void debugLogger(Object message) {
		if (log.isDebugEnabled()) {
			log.debug(message);
		}
		log.info(message);
	}

	public QTDeductionPaymentBackingBean() {
	}

	public void deductionTypeChange(ScQtDeduction scQtDeduction) {
		debugLogger("deductionTypeChange");
		IParameterSO parameterSO = null;
		try {
			Long parameterId = scQtDeduction.getDeductionType().getParameterId();
			parameterSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
			ParameterReqDTO reqDTO = new ParameterReqDTO();
			reqDTO.setParameterId(parameterId);
			ParameterResDTO resDTO = parameterSO.retrieveParameterById(reqDTO);
			scQtDeduction.setDeductionType(resDTO.getPmParameter());
			scQtDeduction.setScQtDeductionClauses(null);
			getManagedBean().setSelectedScQtDeduction(scQtDeduction);
		} finally {
			parameterSO = null;
		}
	}

	public void addDeductionPaymentAction() {
		debugLogger("addDeductionPaymentAction");
		List<ScQtDeduction> scQtDeductions = getManagedBean().getScQtDeductionList();
		if (scQtDeductions == null) {
			scQtDeductions = new ArrayList<ScQtDeduction>();
			getManagedBean().setScQtDeductionList(scQtDeductions);
		}
		ScQtDeduction scQtDeduction = new ScQtDeduction();
		scQtDeduction.setDeductionType(new PmParameter());
		scQtDeductions.add(scQtDeduction);
	}

	public void addDeductionClauseAction() {
		debugLogger("addDeductionClauseAction");
		ScQtDeduction selectedScQtDeduction = this.getManagedBean().getSelectedScQtDeduction();
		List<ScQtDeductionClause> scQtDeductionClauses = selectedScQtDeduction.getScQtDeductionClauses();
		if (scQtDeductionClauses == null) {
			scQtDeductionClauses = new ArrayList<ScQtDeductionClause>();
			selectedScQtDeduction.setScQtDeductionClauses(scQtDeductionClauses);
		}
		ScQtDeductionClause scQtDeductionClause = new ScQtDeductionClause();
		scQtDeductionClause.setScQtDeduction(selectedScQtDeduction);
		scQtDeductionClauses.add(scQtDeductionClause);
	}
}

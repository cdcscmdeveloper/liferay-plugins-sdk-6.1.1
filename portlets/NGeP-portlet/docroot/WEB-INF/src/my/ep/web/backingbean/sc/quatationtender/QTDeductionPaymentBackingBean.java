package my.ep.web.backingbean.sc.quatationtender;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.sc.quationtender.IQTDetailSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
import my.ep.dto.sc.quationtender.QuationTenderReqDTO;
import my.ep.dto.sc.quationtender.QuationTenderResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.sc.quationtender.ScQtDeduction;
import my.ep.persistence.entity.sc.quationtender.ScQtDeductionClause;
import my.ep.web.managedbean.sc.quationtender.QTDetailManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;

/**
 * @author Sudheer Kumar
 * 
 */
@ManagedBean(name = "qTDeductionPaymentBackingBean")
@ViewScoped
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

	public void deductionTypeChange(ScQtDeduction scQtDeduction) {
		debugLogger("deductionTypeChange");
		IParameterSO parameterSO = null;
		try {
			Long parameterId = scQtDeduction.getDeductionType()
					.getParameterId();
			parameterSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
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
		List<ScQtDeduction> scQtDeductions = getManagedBean()
				.getScQtDeductionList();
		if (scQtDeductions == null) {
			scQtDeductions = new ArrayList<ScQtDeduction>();
		}
		ScQtDeduction scQtDeduction = new ScQtDeduction();
		scQtDeduction.setDeductionType(new PmParameter());
		scQtDeduction.setScQt(this.getManagedBean().getQt());
		scQtDeductions.add(scQtDeduction);
		getManagedBean().setScQtDeductionList(scQtDeductions);
	}

	public void addDeductionClauseAction() {
		debugLogger("addDeductionClauseAction");
		ScQtDeduction selectedScQtDeduction = this.getManagedBean()
				.getSelectedScQtDeduction();
		List<ScQtDeductionClause> scQtDeductionClauses = selectedScQtDeduction
				.getScQtDeductionClauses();
		if (scQtDeductionClauses == null) {
			scQtDeductionClauses = new ArrayList<ScQtDeductionClause>();
		}
		ScQtDeductionClause scQtDeductionClause = new ScQtDeductionClause();
		scQtDeductionClause.setScQtDeduction(selectedScQtDeduction);
		scQtDeductionClauses.add(scQtDeductionClause);
		this.getManagedBean().getSelectedScQtDeduction()
				.setScQtDeductionClauses(scQtDeductionClauses);
	}

	public void retrieveDeductionCluses(SelectEvent selectEvent) {
		ScQtDeduction deduction = (ScQtDeduction) selectEvent.getObject();
		if (null != deduction) {

			List<ScQtDeductionClause> deductionClauses = deduction
					.getScQtDeductionClauses();
			if (null == deductionClauses || deductionClauses.isEmpty()) {
				IQTDetailSO detailSO = BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
								IQTDetailSO.class);
				QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
				reqDTO.setDeduction(deduction);
				QuationTenderResDTO resDTO = detailSO
						.retrieveDeDuctionClauseDtls(reqDTO);
				deduction.setScQtDeductionClauses(resDTO
						.getDeductionClauseResultBean());
			}
			this.getManagedBean().setSelectedScQtDeduction(deduction);
		}
	}

	public void deleteRowsAction() {
		ScQtDeduction deduction = this.getManagedBean()
				.getSelectedScQtDeduction();
		List<ScQtDeductionClause> deductionCluases = deduction
				.getScQtDeductionClauses();
		List<ScQtDeductionClause> selectedClauses = this.getManagedBean()
				.getSelectedDeductionClauses();
		if (null == selectedClauses || selectedClauses.isEmpty()) {
			throw new EpException("Please select at least one deduction clause");
		}
		for (ScQtDeductionClause clause : selectedClauses) {
			deductionCluases.remove(clause);
		}
		deduction.setScQtDeductionClauses(deductionCluases);
		this.getManagedBean().setSelectedScQtDeduction(deduction);
	}
	
	public void rowUnselectEvent(UnselectEvent selectEvent) {
		ScQtDeduction deduction = (ScQtDeduction) selectEvent.getObject();
		if(ParameterConst.DEDUCTION_TYPE_OTHERS.equalsIgnoreCase(deduction.getDeductionType().getParameterCode())){
			
		}
		
	}
}

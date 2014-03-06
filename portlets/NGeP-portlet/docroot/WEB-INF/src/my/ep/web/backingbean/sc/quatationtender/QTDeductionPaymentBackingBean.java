package my.ep.web.backingbean.sc.quatationtender;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import my.ep.factory.EpLogger;
import my.ep.persistence.entity.sc.quationtender.ScQtDeduction;
import my.ep.persistence.entity.sc.quationtender.ScQtDeductionClause;
import my.ep.web.managedbean.sc.quationtender.QTDetailManagedBean;
import my.ep.web.sc.quationtender.common.QTConstants;

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

		// ScQtDeduction qtDeduction = new ScQtDeduction();

	}

	public void addDedcutionPayment() {
		ScQtDeduction qtDeduction = new ScQtDeduction();
		try {
			List<ScQtDeduction> deductionList = this.getManagedBean()
					.getScQtDeductionList();
			if (null == deductionList) {
				deductionList = new ArrayList<ScQtDeduction>();
			}
			for (ScQtDeduction scQtDeduction : deductionList) {
				scQtDeduction.setBrandNew(false);
			}

			deductionList.add(qtDeduction);
			this.getManagedBean().setScQtDeductionList(deductionList);
		} catch (Exception e) {
			throw new IllegalArgumentException();
		}
	}

	public void addDedcutionClause() {
		List<ScQtDeductionClause> clauses = this.getManagedBean()
				.getScQtDeductionClauses();
		if (clauses == null) {
			clauses = new ArrayList<ScQtDeductionClause>();

		}
		this.getManagedBean().setQtDeductionClause(null);
		ScQtDeductionClause qtDeductionClause = new ScQtDeductionClause();

		this.getManagedBean().setQtDeductionClause(qtDeductionClause);
		clauses.add(this.getManagedBean().getQtDeductionClause());
		this.getManagedBean().setScQtDeductionClauses(clauses);

	}

	public void deductionPaymentEvent() {
		debugLogger("deductionPaymentEvent");
		System.out.println("Deduction Payment : "
				+ getManagedBean().getDeductionPaymentArray());

		if (getManagedBean().getDeductionPaymentArray() != null) {

			if (getManagedBean().getDeductionPaymentArray().equalsIgnoreCase(
					QTConstants.DP_PENALTY)
					|| getManagedBean().getDeductionPaymentArray()
							.equalsIgnoreCase(QTConstants.DP_ROYALTY)) {
				this.getManagedBean().setPercentageField(true);
				this.getManagedBean().setDescriptionField(false);
				this.getManagedBean().setAmountField(false);
			} else if (getManagedBean().getDeductionPaymentArray()
					.equalsIgnoreCase(QTConstants.DP_FIXED)) {
				this.getManagedBean().setPercentageField(false);
				this.getManagedBean().setDescriptionField(false);
				this.getManagedBean().setAmountField(true);
			} else if (getManagedBean().getDeductionPaymentArray()
					.equalsIgnoreCase(QTConstants.DP_NON_FIXED)) {
				this.getManagedBean().setPercentageField(false);
				this.getManagedBean().setDescriptionField(false);
				this.getManagedBean().setAmountField(false);
			} else if (getManagedBean().getDeductionPaymentArray()
					.equalsIgnoreCase(QTConstants.DP_OTHERS)) {
				this.getManagedBean().setPercentageField(true);
				this.getManagedBean().setAmountField(true);
				this.getManagedBean().setDescriptionField(true);
			} else {
				this.getManagedBean().setPercentageField(true);
				this.getManagedBean().setAmountField(true);
				this.getManagedBean().setDescriptionField(true);
			}
		}
		// getManagedBean().setDeductionPaymentArray(getManagedBean().getDeductionPaymentArray());
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

}

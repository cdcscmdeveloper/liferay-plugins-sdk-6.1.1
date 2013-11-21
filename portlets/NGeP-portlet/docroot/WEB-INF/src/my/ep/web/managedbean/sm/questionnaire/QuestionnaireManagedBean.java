package my.ep.web.managedbean.sm.questionnaire;

import java.util.List;

import javax.ejb.Stateless;
import javax.faces.bean.ManagedBean;

import my.ep.persistence.entity.sm.SmQuestion;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.sm.SmSupplierAnswer;
import my.ep.web.managedbean.BaseManagedBean;

@ManagedBean(name="questionnaireMB")
@Stateless
public class QuestionnaireManagedBean extends BaseManagedBean{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private List<SmQuestion> smQuestionLs;
	private SmSupplier smSupp;
	private List<SmSupplierAnswer> smSuppAnswerLs;

	public List<SmQuestion> getSmQuestionLs() {
		return smQuestionLs;
	}

	public void setSmQuestionLs(List<SmQuestion> smQuestionLs) {
		this.smQuestionLs = smQuestionLs;
	}

	public SmSupplier getSmSupp() {
		return smSupp;
	}

	public void setSmSupp(SmSupplier smSupp) {
		this.smSupp = smSupp;
	}

	public List<SmSupplierAnswer> getSmSuppAnswerLs() {
		return smSuppAnswerLs;
	}

	public void setSmSuppAnswerLs(List<SmSupplierAnswer> smSuppAnswerLs) {
		this.smSuppAnswerLs = smSuppAnswerLs;
	}

}

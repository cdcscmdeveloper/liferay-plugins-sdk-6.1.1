package my.ep.web.managedbean.report.sm;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean (name="colClassificationCodeMB")
@SessionScoped
public class ColClassificationCodeManagedBean extends SmReportManagedBean{
	private static final long serialVersionUID = 1L;

	private String classificationCode;

	public String getClassificationCode() {
		return classificationCode;
	}

	public void setClassificationCode(String classificationCode) {
		this.classificationCode = classificationCode;
	}
}

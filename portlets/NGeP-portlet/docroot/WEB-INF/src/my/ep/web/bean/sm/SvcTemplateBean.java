package my.ep.web.bean.sm;

public class SvcTemplateBean {

	private int templateSeq;
	private String templateName;

	public SvcTemplateBean() {
	}

	public SvcTemplateBean(int templateSeq, String templateName) {
		this.templateSeq = templateSeq;
		this.templateName = templateName;
	}

	public int getTemplateSeq() {
		return templateSeq;
	}

	public void setTemplateSeq(int templateSeq) {
		this.templateSeq = templateSeq;
	}

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

}

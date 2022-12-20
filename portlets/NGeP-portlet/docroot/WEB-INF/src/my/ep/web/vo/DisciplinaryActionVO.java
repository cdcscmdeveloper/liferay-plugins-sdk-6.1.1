package my.ep.web.vo;

import java.io.Serializable;

import my.ep.persistence.entity.sm.SmDisciplinaryAction;

public class DisciplinaryActionVO implements Serializable {
	private static final long serialVersionUID = 5887063064190149198L;
	private SmDisciplinaryAction smDiscplinaryAction;
	private boolean suspend;

	public SmDisciplinaryAction getSmDiscplinaryAction() {
		return smDiscplinaryAction;
	}

	public void setSmDiscplinaryAction(SmDisciplinaryAction smDiscplinaryAction) {
		this.smDiscplinaryAction = smDiscplinaryAction;
	}

	public boolean isSuspend() {
		return suspend;
	}

	public void setSuspend(boolean suspend) {
		this.suspend = suspend;
	}

}

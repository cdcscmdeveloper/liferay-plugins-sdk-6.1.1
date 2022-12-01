package my.ep.web.backingbean.sc.quatationtender;

import java.io.Serializable;

public class QTSelectionNode implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2027991313123244337L;

	private String label;
	private Long value;
	
	public QTSelectionNode(final String label,final Long value){
		this.label=label;
		this.value=value;
	}
	
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Long getValue() {
		return value;
	}

	public void setValue(Long value) {
		this.value = value;
	}
	
}

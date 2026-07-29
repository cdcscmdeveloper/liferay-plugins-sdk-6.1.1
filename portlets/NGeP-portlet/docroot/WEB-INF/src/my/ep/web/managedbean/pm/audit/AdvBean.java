package my.ep.web.managedbean.pm.audit;

import java.io.Serializable;
import java.util.List;

/**
 * AdvBean
 * #35509
 * @author normuliana
 * 
 */
public class AdvBean implements Serializable {

	private static final long serialVersionUID = 1L;
	private List<AdvColumnBean> columns;

	public AdvBean(){}

	/**
	 * @return the columns
	 */
	public List<AdvColumnBean> getColumns() {
		return columns;
	}

	/**
	 * @param columns the columns to set
	 */
	public void setColumns(List<AdvColumnBean> columns) {
		this.columns = columns;
	}
	

}

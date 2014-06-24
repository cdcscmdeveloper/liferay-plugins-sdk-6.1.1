/**
 * 
 */
package my.ep.web.bean.sc.quatationtender;

import java.io.Serializable;

/**
 * @author VidyaSagarReddy
 *
 */
public class CtCircularSearchBean implements Serializable{

	private String cirCularNo;
	private String circularName;
	private String itemName;
	public String getCirCularNo() {
		return cirCularNo;
	}
	public void setCirCularNo(String cirCularNo) {
		this.cirCularNo = cirCularNo;
	}
	public String getCircularName() {
		return circularName;
	}
	public void setCircularName(String circularName) {
		this.circularName = circularName;
	}
	public String getItemName() {
		return itemName;
	}
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}
}

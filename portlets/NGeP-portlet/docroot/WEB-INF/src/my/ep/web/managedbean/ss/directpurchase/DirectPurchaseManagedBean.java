package my.ep.web.managedbean.ss.directpurchase;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.web.constant.SourcingConst;
import my.ep.web.managedbean.BaseManagedBean;

import org.apache.log4j.Logger;
/**
 * 
 * @author Tran Ngoc Hoa
 *
 */
@ManagedBean(name="directPurchaseMB")
@SessionScoped
public class DirectPurchaseManagedBean extends BaseManagedBean{
	private static Logger log = Logger.getLogger(DirectPurchaseManagedBean.class);
	
	
	private	String	action	=	SourcingConst.CREATE_DIRECT_PURCHASE;

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}
	
}

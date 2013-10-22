/**
 * 
 */
package my.ep.web.managedbean.ss.committee;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.factory.EpLogger;
import my.ep.persistence.entity.ss.quationtender.ScQtCommittee;
import my.ep.web.managedbean.BaseManagedBean;

import org.apache.log4j.Logger;
/**
 * @author VidyasagarReddy
 *
 */
@ManagedBean(name="committeMbean")
@SessionScoped
public class CommitteManagedBean extends BaseManagedBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final Logger logger= EpLogger.getLogger(CommitteManagedBean.class);
	
	private ScQtCommittee committee;
	
	public CommitteManagedBean(){
		
	}
}

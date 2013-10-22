/**
 * 
 */
package my.ep.web.backingbean.ss.committee;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import org.apache.log4j.Logger;

import my.ep.factory.EpLogger;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.ss.committee.CommitteManagedBean;

/**
 * @author VidyasagarReddy
 *
 */
@ManagedBean(name="committeBBean")
@RequestScoped
public class CommitteBackingBean extends BaseBackingBean<CommitteManagedBean> {
  private static final Logger logger= EpLogger.getLogger(CommitteBackingBean.class);
	
	public CommitteBackingBean(){
		
	}
}

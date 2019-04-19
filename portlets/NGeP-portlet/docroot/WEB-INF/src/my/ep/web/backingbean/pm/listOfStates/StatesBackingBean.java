/* 
 ***************************************************   
 * Product nextgen
 *                ePerolehan 
 * Release 1.00    
 *    
 * Copyright © 2013 Archisoft. All rights reserved.
 * 
 ****************************************************
 */
package my.ep.web.backingbean.pm.listOfStates;

import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import my.ep.biz.service.pm.common.states.IListOfStatesSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.pm.listOfStates.StatesManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;
import org.apache.log4j.Logger;

/**
 * EJB BakingBean For List Of States.
 * 
 * @author Nagasiva
 * @since 10.06.2016
 * 
 */
@ManagedBean(name = "statesBackingBean")
@ViewScoped
public class StatesBackingBean extends BaseBackingBean<StatesManagedBean> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/** The log. */
	private static Logger log = Logger.getLogger(StatesBackingBean.class);

	/**
	 * Instantiates a new states backing bean.
	 */
	public StatesBackingBean() {
		super();
	}

	/**
	 * Pre render view.
	 * 
	 * @return the string
	 */
	public String preRenderView() {
		
		log.info("...........preRenderView............");

		IListOfStatesSO listOfStatesSO = null;
		try {

			listOfStatesSO = (IListOfStatesSO) BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IListOfStatesSO.class);
			
			List<PmState> result = listOfStatesSO.retrieveListOfStates();
			this.getManagedBean().setListOfStates(result);

			log.info("...List Of States...size()="
							+ result.size());

			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication().getNavigationHandler()
					.handleNavigation(fc, null, "listOfStates.xhtml");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
}

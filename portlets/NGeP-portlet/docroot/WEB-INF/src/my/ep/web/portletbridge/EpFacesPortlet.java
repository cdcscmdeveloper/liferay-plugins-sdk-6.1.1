/**
 * 
 */
package my.ep.web.portletbridge;

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.EventRequest;
import javax.portlet.EventResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.faces.GenericFacesPortlet;

import org.apache.log4j.Logger;


/**
 * @author siew mei
 *
 */
public class EpFacesPortlet extends GenericFacesPortlet {
	private static Logger log = Logger.getLogger(EpFacesPortlet.class);
	@Override
	public void processAction(ActionRequest actionRequest,
			ActionResponse actionResponse) throws PortletException, IOException {
		try{
			if(log.isDebugEnabled()){
				log.debug("In processAction");
				log.debug("actionRequest" +actionRequest);
			}
			// TODO Auto-generated method stub
			super.processAction(actionRequest, actionResponse);
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	
	

	@Override
	public void processEvent(EventRequest arg0, EventResponse arg1)
			throws PortletException, IOException {
		if(log.isDebugEnabled()){
			log.debug("In EpFacesPortlet.processEvent");
		}
		// TODO Auto-generated method stub
		super.processEvent(arg0, arg1);
	}




	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		if(log.isDebugEnabled()){
			log.debug("In EpFacesPortlet.destroy");
		}
		super.destroy();
	}




	@Override
	protected void doDispatch(RenderRequest arg0, RenderResponse arg1)
			throws PortletException, IOException {
		// TODO Auto-generated method stub
		if(log.isDebugEnabled()){
			log.debug("In EpFacesPortlet.doDispatch");
		}
		super.doDispatch(arg0, arg1);
	}




	@Override
	protected void doEdit(RenderRequest renderRequest,
			RenderResponse renderResponse) throws PortletException, IOException {
		// TODO Auto-generated method stub
		if(log.isDebugEnabled()){
			log.debug("In EpFacesPortlet.doEdit");
		}
		super.doEdit(renderRequest, renderResponse);
	}




	@Override
	protected void doHeaders(RenderRequest arg0, RenderResponse arg1) {
		// TODO Auto-generated method stub
		if(log.isDebugEnabled()){
			log.debug("In EpFacesPortlet.doHeaders");
		}
		super.doHeaders(arg0, arg1);
	}




	@Override
	protected void doHelp(RenderRequest renderRequest,
			RenderResponse renderResponse) throws PortletException, IOException {
		// TODO Auto-generated method stub
		if(log.isDebugEnabled()){
			log.debug("In EpFacesPortlet.doHelp");
		}
		super.doHelp(renderRequest, renderResponse);
	}




	@Override
	protected void doView(RenderRequest renderRequest,
			RenderResponse renderResponse) throws PortletException, IOException {
		if(log.isDebugEnabled()){
			log.debug("In doView");
		}
		
		try{
			super.doView(renderRequest, renderResponse);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}




	@Override
	public void init(PortletConfig arg0) throws PortletException {
		// TODO Auto-generated method stub
		if(log.isDebugEnabled()){
			log.debug("In EpFacesPortlet.init");
		}
		super.init(arg0);
	}
	
	
	
	
	

}

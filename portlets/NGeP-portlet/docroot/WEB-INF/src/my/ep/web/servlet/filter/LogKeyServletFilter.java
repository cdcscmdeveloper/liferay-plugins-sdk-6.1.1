package my.ep.web.servlet.filter;

import java.io.IOException;

import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.filter.FilterChain;
import javax.portlet.filter.FilterConfig;
import javax.portlet.filter.RenderFilter;

import my.ep.persistence.logging.LogKey;

import org.apache.log4j.Logger;

import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.User;

public class LogKeyServletFilter implements RenderFilter{
	
	private static Logger log = Logger.getLogger(LogKeyServletFilter.class);

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		System.out.println("*****destroy");
	}

	@Override
	public void init(FilterConfig arg0) throws PortletException {
		// TODO Auto-generated method stub
		System.out.println("*****init");
	}

	@Override
	public void doFilter(RenderRequest request, RenderResponse response,
			FilterChain filterChain) throws IOException, PortletException {
		if (log.isDebugEnabled()){
			log.debug("In doFilter");
		}
		User user = (User)request.getAttribute(WebKeys.USER);
		if (user != null){
			//String userName = user.getScreenName();
			Long userName = user.getUserId();
			new LogKey(userName);
			if (log.isDebugEnabled()){
				log.debug("userName: " + userName);
			}
		}
		
		if (log.isDebugEnabled()){
			log.debug("End doFilter");
		}
		filterChain.doFilter(request, response);
	}

	
	

	

}

package my.ep.web.scheduler.sc.ebidding.util;

import javax.faces.context.FacesContext;
import javax.portlet.PortletContext;

import my.ep.factory.EpLogger;

import org.apache.log4j.Logger;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.ee.servlet.QuartzInitializerListener;
import org.quartz.impl.StdSchedulerFactory;

public class SchedulerFactoryUtil {

	public static Logger log = EpLogger.getLogger(SchedulerFactoryUtil.class);

	public static Scheduler getFacesContextSchedulerFactory() {
		final PortletContext servletContext = (PortletContext) FacesContext
				.getCurrentInstance().getExternalContext().getContext();

		final StdSchedulerFactory schFactory = (StdSchedulerFactory) servletContext
				.getAttribute(QuartzInitializerListener.QUARTZ_FACTORY_KEY);

		Scheduler sch = null;
		try {
			sch = schFactory.getScheduler();

		} catch (final SchedulerException e) {
			log.error(e.getMessage());
		}

		return sch;
	}
}

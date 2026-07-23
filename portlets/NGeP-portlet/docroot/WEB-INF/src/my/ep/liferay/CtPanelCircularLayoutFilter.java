package my.ep.liferay;

import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;
import com.liferay.portal.model.Layout;


public final class CtPanelCircularLayoutFilter {

	private static final String HYPERLINK_CREATE_PANEL = "/create-panel-circular";
	private static final Logger log = Logger.getLogger(CtPanelCircularLayoutFilter.class);

	private CtPanelCircularLayoutFilter() {
		// Prevent instantiation
	}

	public static List<Layout> filterLayouts(List<Layout> layouts) {
		Iterator<Layout> iter = layouts.iterator();

		while (iter.hasNext()) {
			Layout layout = iter.next();

			if (HYPERLINK_CREATE_PANEL.equals(layout.getFriendlyURL())) {
//				log.info("Layout URL : " + layout.getFriendlyURL());
//				log.info("Expected   : " + HYPERLINK_CREATE_PANEL);
				iter.remove();
			}
		}
		return layouts;
	}
}
	


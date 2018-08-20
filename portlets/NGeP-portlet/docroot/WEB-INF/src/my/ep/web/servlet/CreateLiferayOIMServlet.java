package my.ep.web.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.factory.EpLogger;
import my.ep.helper.EntityHelper;
import my.ep.persistence.entity.pm.authorization.PmPendingLiferay;
import my.ep.web.util.sm.helper.SmServiceLookup;

import org.apache.log4j.Logger;

public class CreateLiferayOIMServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1433097180058283518L;
	private static Logger log = EpLogger
			.getLogger(CreateLiferayOIMServlet.class);

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		log.info("Before success calling createLiferay servlet....");
		UserReqDTO reqDTO = new UserReqDTO();
		PrintWriter out = res.getWriter();
		try {
			List<PmPendingLiferay> liferayList = SmServiceLookup.getUserSO()
					.retrievePendingLiferay(reqDTO).getPendingLiferayList();

			if (!EntityHelper.isCollectionEmpty(liferayList)) {
				for (PmPendingLiferay liferay : liferayList) {
					reqDTO.setLiferay(liferay);
					SmServiceLookup.getUserSO().saveUserLiferayOIM(reqDTO);
				}
			}
		} finally {
			out.print("success calling create liferay!!");
			out.close();
		}
		log.info("After success calling createLiferay servlet....");
	}

}

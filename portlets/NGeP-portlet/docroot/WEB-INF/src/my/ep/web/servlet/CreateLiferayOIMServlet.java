package my.ep.web.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.factory.EpLogger;
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
		log.info("Before success calling createLiferay servlet...." + req.getServerName() + " " + req.getServerPort() );
		UserReqDTO reqDTO = new UserReqDTO();
		PrintWriter out = res.getWriter();
		try {
			SmServiceLookup.getUserSO().createPendingLiferay(reqDTO);
			out.print("success!!!!");
		} catch (Exception e) {
			out.print(e.getMessage());
		} finally {
			out.close();
		}
		log.info("After success calling createLiferay servlet....");
	}

}

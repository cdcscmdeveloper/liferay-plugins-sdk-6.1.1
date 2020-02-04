package my.ep.web.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import my.ep.constant.sc.QTFieldConst;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.util.PasswordUtils;
import my.ep.web.backingbean.sc.quatationtender.QTDocumentsBackingBean;
import my.ep.web.util.sm.helper.SmServiceLookup;

import org.apache.log4j.Logger;
import org.picketbox.util.StringUtil;
import org.primefaces.component.log.Log;

/**
 * Servlet implementation class RegenerateDocumentsServlet
 */
@WebServlet("/regenerateDocumentsServlet")
public class RegenerateDocumentsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Logger log = EpLogger
			.getLogger(RegenerateDocumentsServlet.class);   

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		log.info("RegenerateDocumentsServlet begin..");
		PrintWriter out = response.getWriter();
		try {
			String qtId = request.getParameter("qtId");
			String token = request.getParameter("token");
			String hostUrl = request.getServerName() + ":"+ request.getServerPort();
			if(StringUtil.isNullOrEmpty(request.getParameter("token"))){
				throw new EpException("Failed token validation.");
			}
			if(!PasswordUtils.decrypt(token).equals("JgMfrR75RtsPNVb4Sk6msCpv488aS4Uq5VzKj4")){
				throw new EpException("Failed token validation.");
			}
			new QTDocumentsBackingBean().invokedReGenerateDocuments(new Long(qtId), hostUrl ,QTFieldConst.FIELD_QT_VALIDITY_PERIOD);
			out.print("Success");
		} catch (Exception e) {
			out.print(e.getMessage());
		} finally {
			out.close();
		}
		log.info("RegenerateDocumentsServlet end..");
	}
	
}

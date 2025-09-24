package my.ep.web.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.faces.FactoryFinder;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextFactory;
import javax.faces.lifecycle.Lifecycle;
import javax.faces.lifecycle.LifecycleFactory;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import my.ep.biz.service.sc.quationtender.IQTDetailSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.sc.QTFieldConst;
import my.ep.dto.sc.quationtender.QuationTenderReqDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.util.PasswordUtils;
import my.ep.util.SLAUtils;
import my.ep.web.backingbean.sc.quatationtender.QTDocumentsBackingBean;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.picketbox.util.StringUtil;

/**
 * Servlet implementation class RegenerateDocumentsServlet
 */
@WebServlet("/regenerateDocumentsServlet")
public class RegenerateDocumentsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static String token = "JgMfrR75RtsPNVb4Sk6msCpv488aS4Uq5VzKj4";
	private FacesContextFactory facesContextFactory;
	private Lifecycle lifecycle;

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);

		LifecycleFactory lifecycleFactory = (LifecycleFactory) FactoryFinder
				.getFactory(FactoryFinder.LIFECYCLE_FACTORY);
		facesContextFactory = (FacesContextFactory) FactoryFinder.getFactory(FactoryFinder.FACES_CONTEXT_FACTORY);
		lifecycle = lifecycleFactory.getLifecycle(LifecycleFactory.DEFAULT_LIFECYCLE);
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		facesContextFactory.getFacesContext(request.getSession().getServletContext(), request, response, lifecycle);
		PrintWriter out = response.getWriter();

		try {
			String qtId = request.getParameter("qtId");
			String tokenHash = request.getParameter("token");
			String hostUrl = request.getServerName() + ":" + request.getServerPort();
			FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("hostUrl", hostUrl);

			if (StringUtil.isNullOrEmpty(request.getParameter("token"))
					|| !PasswordUtils.decrypt(tokenHash).equals(token)) {
				throw new EpException("Failed token validation.");
			}
			QTDocumentsBackingBean bb = new QTDocumentsBackingBean();

			if (qtId != null) {
				/**
				 * Will regenerate documents after all suppliers replied to Proposal Validity Extension.
				 * List of reports:
				 * TP - Kenyataan Tawaran Pembekal
				 * SS - Syarat-syarat Am/ Arahan/Syarat-syarat Khas
				 * TK - Terma Kontrak
				 * KT - Kertas Taklimat
				 */
				bb.reGenerateDocuments(new Long(qtId), QTFieldConst.FIELD_QT_VALIDITY_PERIOD);
			} else {
				throw new EpException("Failed to obtain qt detail.");
			}
			out.print("Success");
		} catch (Exception e) {
			e.printStackTrace();
			if (e.getMessage() == null) {
				out.print("Failed without message");
			} else {
				out.print(e.getMessage());
			}
		} finally {
			out.close();
		}
	}

}

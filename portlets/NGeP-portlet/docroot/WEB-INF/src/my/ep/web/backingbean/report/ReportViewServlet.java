package my.ep.web.backingbean.report;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class ReportViewServlet
 */
@WebServlet("/ReportViewServlet")
public class ReportViewServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ReportViewServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		OutputStream outputStream = null;
		BufferedInputStream bufferedInputStream = null;
		HttpURLConnection urlConnection = null;
		String report = (String) request.getSession().getAttribute(
				"generatedUrl");
		try {
			URL url = new URL(report);
			urlConnection = (HttpURLConnection) url.openConnection();
			bufferedInputStream = new BufferedInputStream(
					urlConnection.getInputStream());
			outputStream = response.getOutputStream();
			byte[] content = new byte[urlConnection.getContentLength()];
			int data;
			int i = 0;
			while ((data = bufferedInputStream.read()) != -1) {
				byte byteRead = (byte) data;
				content[i] = byteRead;
				i++;
			}
			
			bufferedInputStream.close();
			outputStream.write(content);
			response.setContentType("application/pdf");
			response.setContentLength(urlConnection.getContentLength());
			response.setHeader("Content-Disposition",
					"attachment; filename=NgePReport.pdf");
			response.setHeader("Pragma", "no-cache");
			response.setDateHeader("Expires", 0);
			//response.setHeader("Cache-Control", "cache, must-revalidate");
			//response.setHeader("Pragma", "public");
			request.getSession().removeAttribute("generatedUrl");
			outputStream.flush();
		} catch (Exception e) {			
			generateErrorMsg(request,response);
		} finally {
			if (null != urlConnection) {
				urlConnection.disconnect();
			}
			if (null != bufferedInputStream) {
				bufferedInputStream.close();
			}
			if (null != outputStream) {
				outputStream.close();
			}
		}
	}

	private void generateErrorMsg(HttpServletRequest request,
			HttpServletResponse response) {		
		PrintWriter writer=null;
		try {
			writer = response.getWriter();
			writer.write("Service Down");
			writer.flush();
		} catch (IOException e) {		
			e.printStackTrace();
		} finally {
			if(null!=writer){
				writer.close();
			}
		}		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {		
	}

}

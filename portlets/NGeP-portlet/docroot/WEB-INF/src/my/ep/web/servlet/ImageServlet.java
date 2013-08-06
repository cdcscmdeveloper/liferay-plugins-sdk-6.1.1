package my.ep.web.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
//
//import my.ep.biz.service.cat.catalogue.ICatalogueSO;
//import my.ep.web.util.lookupservice.BusinessDelegate;

//@WebServlet("/imageServlet")
public class ImageServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		String filename = (StringUtils.isNotBlank(request.getParameter("imageName"))) ? request.getParameter("imageName") : null;
		String tempDir = System.getProperty("java.io.tmpdir");

		File file = new File(tempDir + filename);
		byte[] b = new byte[(int) file.length()];
		
		try {
			FileInputStream fileInputStream = new FileInputStream(file);
			fileInputStream.read(b);
	        response.reset();
	        response.setContentType("text/comma-separated-values");
	        response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
	        ServletOutputStream output = response.getOutputStream();
	        output.write(b);	        
	        output.flush();
	        
	        fileInputStream.close();
	        output.close();
		} catch (FileNotFoundException e) {
			System.out.println("File Not Found.");
			e.printStackTrace();
		}
	}
}

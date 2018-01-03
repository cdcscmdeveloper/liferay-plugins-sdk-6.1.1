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

import my.ep.constant.EpPropertiesConst;
import my.ep.factory.EpLogger;
import my.ep.util.EpPropertiesUtils;

import org.apache.commons.lang.StringUtils;

import org.apache.log4j.Logger;

//@WebServlet("/imageServlet")
public class ImageServlet extends HttpServlet {

	private static Logger log = EpLogger.getLogger(ImageServlet.class);
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		String filename = (StringUtils.isNotBlank(request.getParameter("imageName"))) ? request.getParameter("imageName") : null;
//		String epNo = (StringUtils.isNotBlank(request.getParameter("en"))) ? request.getParameter("en") : null;
//		String tempDir = System.getProperty("java.io.tmpdir");
		
		StringBuilder uploadDir = new StringBuilder();
//		uploadDir.append(EpPropertiesUtils.getInstance().getProperty(EpPropertiesConst.UPLOAD_URL)).append("ngep/cm");
		uploadDir.append(EpPropertiesUtils.getInstance().getProperty(EpPropertiesConst.DOCUMENT_REPOSITORY_BASE_DIR)).append("/CM");
		String imageUrl = uploadDir + "/" + filename;

		log.info("filename: " + filename);
		log.info("imageUrl: " + imageUrl);
		
		File file = new File(imageUrl);
		byte[] b = new byte[(int) file.length()];
		
		FileInputStream fileInputStream = null;
		ServletOutputStream output = null;
		
		try {
			fileInputStream = new FileInputStream(file);
			fileInputStream.read(b);
	        response.reset();
	        response.setContentType("text/comma-separated-values");
	        response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
	        output = response.getOutputStream();
	        output.write(b);	        
	        output.flush();
	        	        
		} catch (FileNotFoundException e) {
			log.error("File Not Found: " + e.getMessage());
		} finally {
			if(fileInputStream!=null) {
				fileInputStream.close();
			}
			if(output!=null) {
				output.close();		
			}
		}
	}
}

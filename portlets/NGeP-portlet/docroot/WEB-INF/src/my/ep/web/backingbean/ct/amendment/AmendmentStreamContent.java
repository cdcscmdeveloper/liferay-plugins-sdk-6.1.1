/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.ct.amendment;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;

import my.ep.constants.CommonConstants;
import my.ep.persistence.entity.sc.common.ScAttachment;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

/**
 * Stream Content for Amendment
 * 
 * @author Ken
 * @since 09/01/2015
 */
@ManagedBean(name = "amendmentStreamContent")
@RequestScoped
public class AmendmentStreamContent {

	private StreamedContent loaStreamContent = null;
	
	/**
	 * The method used to generate the pdf file and display on screen.
	 * 
	 * @return the stream content
	 * @throws Exception  if an exception occurred
	 * @see    Exception
	 */
	public StreamedContent populateLoaStreamContent() throws Exception {
		FacesContext context = FacesContext.getCurrentInstance();
		if (context.getCurrentPhaseId() == PhaseId.RENDER_RESPONSE) {
			return new DefaultStreamedContent();
		} else {
			if (getLoaStreamContent() == null) {
				String filePath = context.getExternalContext()
						.getRequestParameterMap().get("filePath");
				if (filePath != null) {
					setLoaStreamContent(getAttachmentStreamContent(filePath,
							CommonConstants.FILE_CONTENT_TYPE_PDF));
				}
			}else {
				getLoaStreamContent().getStream().reset();
			}
		}
		return getLoaStreamContent();
	}

	protected <ATTACHMENT extends ScAttachment> StreamedContent getAttachmentStreamContent(
			String filePath, String contentType) throws IOException {
		if (filePath != null) {
			File file = new File(filePath);
			ByteArrayOutputStream tmpOut = new ByteArrayOutputStream();
			FileInputStream in = new FileInputStream(file);
			byte[] buf = new byte[512];
			int len;
			while (true) {
				len = in.read(buf);
				if (len == -1) {
					break;
				}
				tmpOut.write(buf, 0, len);
			}
			in.close();
			tmpOut.close();
			ByteBuffer bb = ByteBuffer.wrap(tmpOut.toByteArray(), 0,
					tmpOut.size());

			ByteArrayInputStream stream = new ByteArrayInputStream(bb.array());
			stream.mark(0);

			return new DefaultStreamedContent(stream, contentType);
		} else {
			return null;
		}
	}

	/**
	 * @return the loaStreamContent
	 */
	public StreamedContent getLoaStreamContent() {
		return loaStreamContent;
	}

	/**
	 * @param loaStreamContent the loaStreamContent to set
	 */
	public void setLoaStreamContent(StreamedContent loaStreamContent) {
		this.loaStreamContent = loaStreamContent;
	}

}

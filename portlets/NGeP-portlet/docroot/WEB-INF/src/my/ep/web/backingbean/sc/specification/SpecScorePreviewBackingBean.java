package my.ep.web.backingbean.sc.specification;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Calendar;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.portlet.PortletResponse;
import javax.servlet.http.HttpServletResponse;

import my.ep.constant.ModuleCodeConst;
import my.ep.constants.CommonConstants;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.sc.common.ScAttachment;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtCheckListAttachment;
import my.ep.util.SLAUtils;
import my.ep.web.backingbean.sc.quatationtender.SourcingBaseBackingBean;
import my.ep.web.constant.sc.QTConstants;
import my.ep.web.constant.sc.SLALogConstant;
import my.ep.web.managedbean.sc.quationtender.QTDetailManagedBean;
import my.ep.web.util.FacesScopes;

import org.apache.log4j.Logger;
import org.primefaces.model.StreamedContent;

import com.liferay.portal.util.PortalUtil;


/**
 * The Class SpecScorePreviewBackingBean will be used to show preview of the
 * related score file generated report from the source.
 * 
 * @author: #10141 Vinod.
 */
@ManagedBean(name = "specScorePreviewBackingBean")
@RequestScoped
public class SpecScorePreviewBackingBean extends SourcingBaseBackingBean<QTDetailManagedBean> implements Serializable {

	/**
	 * The serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(SpecScorePreviewBackingBean.class);
	private StreamedContent currentStream;

	/**
	 * Listener method to download score report
	 * 
	 * @param
	 * @return
	 * 
	 * @throws IOException
	 */
	public StreamedContent getScoreReport() throws IOException {
		log.debug("getScoreReport");
		if (currentStream == null) {
			try {
				ScAttachment newAttachment = new ScQtCheckListAttachment();
				PortletResponse portletResponse = (PortletResponse) FacesContext.getCurrentInstance()
						.getExternalContext().getResponse();
				HttpServletResponse res = PortalUtil.getHttpServletResponse(portletResponse);

				generateFileDetails(newAttachment);
				res.setHeader("Content-Disposition", "inline; filename=" + newAttachment.getFileName());
				
				currentStream = getAttachmentStreamContent(newAttachment, CommonConstants.FILE_CONTENT_TYPE_PDF);
			} catch (IOException e) {
				ScQt scQt = getManagedBean().getQt();
				String qtNo = scQt.getQtNo() != null ? scQt.getQtNo() : "";
				SLAUtils.getInstance().logSLAError(SLALogConstant.QT_MODULE,
						SLALogConstant.QT_SPEC_SCORE_PREVIEW_BACKINGBEAN, qtNo, SLALogConstant.QT_GET_SCORE_REPORT,
						getUserIdSessionFromLoginId(), e);
			}
		}
		return currentStream;
	}

	/**
	 * This method will generate the file details to show in stream.
	 * 
	 * @param newAttachment
	 * @return 
	 * 
	 */
	private void generateFileDetails(ScAttachment newAttachment) throws IOException {
		log.debug("generateFileDetails");

		ScAttachment attachment = this.getManagedBean().getScoreAttachment();
		String fileName = attachment.getFileName();
		Timestamp date = new Timestamp(System.currentTimeMillis());
		PmUser createdUser = new FacesScopes().getUserSessionBean();
		File fileFromPath = new File(getActualFilePath(attachment));
		String exactFileName = fileFromPath.getName();
		StringBuilder filePath = new StringBuilder();

		filePath.append(Calendar.getInstance().get(Calendar.YEAR));
		filePath.append(CommonConstants.DOC_REPO_PATH_SEPARATOR);
		filePath.append(ModuleCodeConst.SUPPLIER_SELECTION.toUpperCase());
		filePath.append(CommonConstants.DOC_REPO_PATH_SEPARATOR);
		filePath.append(attachment.getDocNo());
		filePath.append(CommonConstants.DOC_REPO_PATH_SEPARATOR);
		filePath.append(attachment.getDocType());
		filePath.append(CommonConstants.DOC_REPO_PATH_SEPARATOR);
		filePath.append(exactFileName);

		newAttachment.setFileName(fileName);
		newAttachment.setCreatedBy(createdUser.getUserId());
		newAttachment.setCreatedDate(date);
		newAttachment.setChangedBy(createdUser.getUserId());
		newAttachment.setCreatedUser(createdUser);
		newAttachment.setFilePath(filePath.toString());
	}
	
	/**
	 * This method to return header name based on active index to display in
	 * document preview.
	 * 
	 * @param
	 * @return String will return header name.
	 * 
	 */
	public String scoreReportPreviewHeader() {
		int activeIndex = this.getManagedBean().getActiveIndex();

		switch (activeIndex) {
		case QTConstants.ACTIVE_INDEX_4:
			return getMessageCommon(QTConstants.LBL_SC_SPEC_TECH_SCORING);
		case QTConstants.ACTIVE_INDEX_5:
			return getMessageCommon(QTConstants.LBL_SC_SPEC_FINANCIAL_SCORING);
		case QTConstants.ACTIVE_INDEX_6:
			return getMessageCommon(QTConstants.LBL_SC_SPEC_RECOMMENDATION);
		default:
			return "";
		}
	}

	/**
	 * This method will be called when the preview pop-up is closed to reset currentStream.
	 * 
	 * @param
	 * @return 
	 * 
	 */
	public void closeScoreReport() {
		if (currentStream != null) {
			currentStream = null;
		}
	}
}

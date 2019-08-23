/* 
 ***************************************************   
 * Product nextgen
 *                ePerolehan 
 * Release 1.00    
 *    
 * Copyright © 2013 Archisoft. All rights reserved.
 * 
 ****************************************************
 */

package my.ep.web.backingbean.sc.specification;

import java.io.File;
import java.io.Serializable;
import java.sql.Timestamp;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import my.ep.constant.EpPropertiesConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.sc.SourcingConstants;
import my.ep.constants.CommonConstants;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.sc.common.ScAttachment;
import my.ep.persistence.entity.sc.common.ScSpecGuideLineAttachment;
import my.ep.persistence.entity.sc.loaloi.ScLoaReportAttachment;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtCheckListAttachment;
import my.ep.persistence.entity.sc.quationtender.ScQtFinalizationAttachment;
import my.ep.persistence.entity.sc.quationtender.ScQtProposalChecklistAttachment;
import my.ep.util.EpPropertiesUtils;
import my.ep.web.backingbean.sc.quatationtender.SourcingBaseBackingBean;
import my.ep.web.format.AttachmentDateFormat;
import my.ep.web.format.AttachmentDefaultFormat;
import my.ep.web.format.AttachmentFormat;
import my.ep.web.managedbean.sc.quationtender.SpecGuideLineInstructionsManagedBean;
import my.ep.web.util.FacesScopes;

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.model.StreamedContent;

/**
 * The Class SpecGuideLineInstructionsBackingBean.
 * 
 * @author:Garikapati Mangaiah.
 */
@ManagedBean(name = "specGuideLineInstructionBB")
@RequestScoped
public class SpecGuideLineInstructionsBackingBean extends SourcingBaseBackingBean<SpecGuideLineInstructionsManagedBean> implements Serializable{

	/**
	 * The serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(SpecGuideLineInstructionsBackingBean.class);
	private StreamedContent specGuideLineStreamContent;
	/**
	 * Action method to specGuideLinePopUpwindow.
	 * 
	 * @throws Exception
	 */
	public void specGuideLineWindow()throws Exception{
		log.debug("startspecGuideLineWindow");
		RequestContext.getCurrentInstance().execute(
				"specGuideLineDlgWidget.show();");	
	}

	
	/**
	 * this method used to get the SpecGuideLineContent.
	 * @return the sampleLabelStreamContent
	 */
	public StreamedContent getSpecGuideLineStreamContent() throws Exception {
		if (specGuideLineStreamContent == null) {
			ScSpecGuideLineAttachment specGuideLineAttachment = new ScSpecGuideLineAttachment();
			generateSpecGuideLinePdfFromSharedrepository(specGuideLineAttachment, null,
					SourcingConstants.DOCNAME_QT_SPEC_ITEM_GUIDELINE);
			specGuideLineStreamContent = getAttachmentStreamContent(specGuideLineAttachment,
					CommonConstants.FILE_CONTENT_TYPE_PDF);
		}
		return specGuideLineStreamContent;
	}

	public StreamedContent getSpecPreparationGuideLineStreamContent() throws Exception {
		if (specGuideLineStreamContent == null) {
			ScSpecGuideLineAttachment specGuideLineAttachment = new ScSpecGuideLineAttachment();
			generateSpecGuideLinePdfFromSharedrepository(specGuideLineAttachment, null,
					SourcingConstants.DOCNAME_QT_SPEC_FINANCIAL_ITEM_GUIDELINE);
			specGuideLineStreamContent = getAttachmentStreamContent(specGuideLineAttachment,
					CommonConstants.FILE_CONTENT_TYPE_PDF);
		}
		return specGuideLineStreamContent;
	}

	private void generateSpecGuideLinePdfFromSharedrepository(ScAttachment attachment, String prefixFileName,
			String constFileName) throws Exception {
		log.debug("generateSamplesamplesalinankontrakPdfFromVelocity");

		StringBuilder fileName = new StringBuilder();
		if(prefixFileName != null){
			fileName.append(prefixFileName).append(SourcingConstants.UNDER_SCORE);
		}
		fileName.append(constFileName);
		attachment.setFileName(fileName.toString());

		Timestamp date = new Timestamp(System.currentTimeMillis());
		PmUser createdUser = new FacesScopes().getUserSessionBean();

		attachment.setCreatedBy(createdUser.getUserId());
		attachment.setCreatedDate(date);
		attachment.setChangedBy(createdUser.getUserId());
		attachment.setCreatedUser(createdUser);

		String docRepoSharedDir = EpPropertiesUtils.getInstance().getProperty(
				EpPropertiesConst.DOCUMENT_REPOSITORY_SHARED_DIR);

		StringBuilder filePath = new StringBuilder(docRepoSharedDir);
		filePath.append(CommonConstants.DOC_REPO_PATH_SEPARATOR);
		filePath.append(ModuleCodeConst.SUPPLIER_SELECTION.toUpperCase());
		attachment.setFilePath(filePath.toString());

		File theDir = new File(getActualFilePath(attachment));
		theDir.mkdirs();

		AttachmentFormat scFormat = getAttachFormat(attachment);
		String formatedFileName = scFormat.format(constFileName, attachment.getPrefixFileName());

		filePath.append(CommonConstants.DOC_REPO_PATH_SEPARATOR);
		filePath.append(formatedFileName);
		attachment.setFilePath(filePath.toString());
	}
	
	/**
	 * Gets the attach format.
	 * 
	 * @param scAttachment
	 *            the sc attachment
	 * @return the attach format
	 */
	private AttachmentFormat getAttachFormat(ScAttachment scAttachment) {
		if (scAttachment instanceof ScLoaReportAttachment
				|| scAttachment instanceof ScQtCheckListAttachment
				|| scAttachment instanceof ScQtProposalChecklistAttachment
				|| scAttachment instanceof ScQtFinalizationAttachment) {

			return new AttachmentDateFormat();
		} else {

			return new AttachmentDefaultFormat();
		}
	}
}

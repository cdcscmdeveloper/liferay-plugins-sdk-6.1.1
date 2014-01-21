package my.ep.web.backingbean.ss.quatationtender;

import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import my.ep.biz.service.pm.common.report.IReportSectionSO;
import my.ep.dto.pm.common.ReportSectionResDTO;
import my.ep.dto.pm.common.report.ReportSectionReqDTO;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.common.PmReportSection;
import my.ep.persistence.entity.ss.quationtender.ScQtCheckList;
import my.ep.persistence.entity.ss.quationtender.ScQtCheckListAttachment;
import my.ep.web.managedbean.ss.quationtender.QTDetailManagedBean;
import my.ep.web.ss.quationtender.common.QTConstants;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;
import org.primefaces.event.FileUploadEvent;

/**
 * @author SudheerKumar
 * 
 */
@ManagedBean(name = "qTDocumentsBackingBean")
@RequestScoped
public class QTDocumentsBackingBean extends
		SourcingBaseBackingBean<QTDetailManagedBean> {
	private static final Logger log = EpLogger
			.getLogger(QTDocumentsBackingBean.class);

	private void debugLogger(Object message) {
		if (log.isDebugEnabled()) {
			log.debug(message);
		}
		log.info(message);
	}

	public QTDocumentsBackingBean() {

	}

	public void addDocumentList() {
		debugLogger("addDocumentList");
		ScQtCheckList checkList = new ScQtCheckList();
		try {
			List<ScQtCheckList> qtCheckLists = getManagedBean()
					.getScQtChecklists();
			if (null == qtCheckLists) {
				qtCheckLists = new ArrayList<ScQtCheckList>();
			}
			for (ScQtCheckList existedCheckList : qtCheckLists) {
				existedCheckList.setBrandNew(false);
			}
			qtCheckLists.add(checkList);
			this.getManagedBean().setScQtChecklists(qtCheckLists);
		} catch (Exception e) {
			throw new IllegalArgumentException();
		}
	}

	public void populatePreDefinedDetails() {
		debugLogger("populatePreDefinedDetails");
		IReportSectionSO reportSectionSO = null;
		try {
			reportSectionSO = BusinessDelegate.getInstance().getServiceObject(
					QTConstants.PM_MODULE, IReportSectionSO.class);
			ReportSectionReqDTO reportSectionReqDTO = new ReportSectionReqDTO();
			reportSectionReqDTO.setReportType(9);
			ReportSectionResDTO resDto = reportSectionSO
					.retrieveReportsByReportType(reportSectionReqDTO);
			List<PmReportSection> sections = resDto
					.getReportSectionResultBean().getResultList();
			for (PmReportSection pmReportSection : sections) {
				System.out.println(pmReportSection.getPmCheckList()
						.getCheckListName()
						+ "@@@@@@@@@"
						+ pmReportSection.getPmCheckList().getChecklistType());
			}
			getManagedBean().setPmReportSectionList(sections);
		} catch (Exception e) {

		}
	}

	public void handleFileAttachment(FileUploadEvent event) {
		debugLogger("handleFileAttachment");
		ScQtCheckList qtCheckList = getManagedBean().getQtCheckList();
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO,
				"The file was Attached successfully.", null);
		FacesContext.getCurrentInstance().addMessage(null, message);
		ScQtCheckListAttachment attachment = qtCheckList
				.getScQtCheckListAttachment();
		if (null == attachment) {
			attachment = new ScQtCheckListAttachment();
			attachment.setScQtCheckList(qtCheckList);
		}
		uploadAttachment(event.getFile(), attachment);
		qtCheckList.setScQtCheckListAttachment(attachment);
		System.out.println("File Name : "
				+ qtCheckList.getScQtCheckListAttachment().getFileName());
	}

	public void showDocument() {
		debugLogger("showDocument");
	}
}

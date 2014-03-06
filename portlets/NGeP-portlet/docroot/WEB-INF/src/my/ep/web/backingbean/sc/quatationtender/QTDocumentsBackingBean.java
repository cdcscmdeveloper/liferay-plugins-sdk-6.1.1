package my.ep.web.backingbean.sc.quatationtender;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.sc.committe.ICommitteSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.sc.committe.CommitteReqDTO;
import my.ep.dto.sc.committe.CommitteResDTO;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.common.PmReportSection;
import my.ep.persistence.entity.sc.quationtender.ScQtCheckList;
import my.ep.persistence.entity.sc.quationtender.ScQtCheckListAttachment;
import my.ep.persistence.entity.sc.quationtender.ScQtCommittee;
import my.ep.type.aut.SearchOperator;
import my.ep.web.managedbean.sc.quationtender.QTDetailManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;

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
		if (getManagedBean().getQtCheckList() == null) {
			getManagedBean().setQtCheckList(new ScQtCheckList());
		}
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

	/*
	 * public void populatePreDefinedDetails() {
	 * debugLogger("populatePreDefinedDetails"); IReportSectionSO
	 * reportSectionSO = null; try { reportSectionSO =
	 * BusinessDelegate.getInstance().getServiceObject(
	 * ModuleCodeConst.PROFILE_MANAGEMENT, IReportSectionSO.class);
	 * 
	 * ReportSectionReqDTO reportSectionReqDTO = new ReportSectionReqDTO();
	 * reportSectionReqDTO.setReportType(9); ReportSectionResDTO resDto =
	 * reportSectionSO .retrieveReportsByReportType(reportSectionReqDTO);
	 * List<PmReportSection> sections = resDto
	 * .getReportSectionResultBean().getResultList(); for (PmReportSection
	 * pmReportSection : sections) {
	 * System.out.println(pmReportSection.getPmCheckList() .getCheckListName() +
	 * "@@@@@@@@@" + pmReportSection.getPmCheckList().getChecklistType()); }
	 * getManagedBean().setSelectedPmReportSectionList( new
	 * ArrayList<PmReportSection>());
	 * getManagedBean().setPmReportSectionList(sections); } catch (Exception e)
	 * {
	 * 
	 * } }
	 */

	public void handleFileAttachment(FileUploadEvent event) {
		debugLogger("handleFileAttachment");
		FacesScopes facesScopes = new FacesScopes();
		List<ScQtCheckListAttachment> attachments = new ArrayList<ScQtCheckListAttachment>();
		if (event.getFile() != null) {
			ScQtCheckList qtCheckList = this.getManagedBean().getQtCheckList();
			ScQtCheckListAttachment attachment = getManagedBean().getQtCheckList().getScQtCheckListAttachment();
			if (null == attachment) {
				attachment = new ScQtCheckListAttachment();
				attachment.setScQtCheckList(qtCheckList);
			}
			attachment.setUser(facesScopes.getUserSessionBean());
			attachment
					.setChangedDate(new Timestamp(System.currentTimeMillis()));
			uploadAttachment(event.getFile(), attachment);
			attachments.add(attachment);
			getManagedBean().setChecklistAttachment(attachments);
		}
		
	}

	public void cloneSearchCommitteAction() {
		List<ScQtCommittee> qtCommittee = retriveQtCommittes();
		getManagedBean().setCloneSearchCommittes(qtCommittee);
	}

	/**
	 * @return
	 */
	private List<ScQtCommittee> retriveQtCommittes() {
		CommitteReqDTO committeReqDTO = new CommitteReqDTO();
		ISearchBean<ScQtCommittee> searchBean = new SearchBean<ScQtCommittee>();
		CommitteResDTO resDto = null;

		if (getManagedBean().getCreatedDtFrom() != null) {
			SearchFilterBean creatDtSearchBean = new SearchFilterBean(
					SearchOperator.OPERATOR_EQUALS.value());
			creatDtSearchBean.setSearchField("scCommittee.createdDate");
			creatDtSearchBean.setSearchValue(getManagedBean()
					.getCreatedDtFrom());
			searchBean.getDataFilterBeans().add(creatDtSearchBean);
		}

		if (getManagedBean().getCreatedDtTo() != null) {
			SearchFilterBean creatDtSearchBean = new SearchFilterBean(
					SearchOperator.OPERATOR_EQUALS.value());
			creatDtSearchBean.setSearchField("scCommittee.createdDate");
			creatDtSearchBean.setSearchValue(getManagedBean().getCreatedDtTo());
			searchBean.getDataFilterBeans().add(creatDtSearchBean);
		}

		if (getManagedBean().getQtNo() != null
				&& !"".equalsIgnoreCase(getManagedBean().getQtNo())) {
			SearchFilterBean qtnoSearchBean = new SearchFilterBean(
					SearchOperator.OPERATOR_EQUALS.value());
			qtnoSearchBean.setSearchField("scQt.qtNo");
			qtnoSearchBean.setSearchValue(getManagedBean().getQtNo());

			searchBean.getDataFilterBeans().add(qtnoSearchBean);
		}

		if (getManagedBean().getTitle() != null
				&& !"".equalsIgnoreCase(getManagedBean().getTitle())) {
			SearchFilterBean qtnoSearchBean = new SearchFilterBean(
					SearchOperator.OPERATOR_EQUALS.value());
			qtnoSearchBean.setSearchField("scQt.qtTitle");
			qtnoSearchBean.setSearchValue(getManagedBean().getTitle());

			searchBean.getDataFilterBeans().add(qtnoSearchBean);
		}

		if (getManagedBean().getStatus() != null
				&& !"".equalsIgnoreCase(getManagedBean().getStatus())) {
		}
		committeReqDTO.setCommitteSearchBean(searchBean);

		try {
			ICommitteSO committeSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							ICommitteSO.class);

			resDto = committeSO.searchQTCommittees(committeReqDTO);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return resDto.getCommitteResultBean().getResultList();
	}

	public void onRowCloneAction(SelectEvent event) {
		ScQtCommittee scQtCommittee = (ScQtCommittee) event.getObject();
		System.out.println("" + scQtCommittee.getQtCommitteeId());
	}

	public void standardListAction() {

		List<ScQtCheckList> checkLists = getManagedBean().getScQtChecklists();

		if (null == checkLists) {
			checkLists = new ArrayList<ScQtCheckList>();
		}

		/*
		 * for (int i=0; i <
		 * getManagedBean().getSelectedPmReportSectionList().size(); i++) {
		 * PmReportSection pmReportSection = (PmReportSection)
		 * getManagedBean().getSelectedPmReportSectionList().get(i);
		 * ScQtCheckList qtCheckList = new ScQtCheckList();
		 * qtCheckList.setTitle(
		 * pmReportSection.getPmCheckList().getCheckListName());
		 * qtCheckList.setChecklistType
		 * (pmReportSection.getPmCheckList().getChecklistType());
		 * checkLists.add(qtCheckList); }
		 */

		for (PmReportSection pmReportSection : getManagedBean()
				.getSelectedPmReportSectionList()) {
			ScQtCheckList qtCheckList = new ScQtCheckList();
			qtCheckList.setTitle(pmReportSection.getPmCheckList()
					.getCheckListName());
			qtCheckList.setChecklistType(pmReportSection.getPmCheckList()
					.getChecklistType());
			qtCheckList.setCheckStatus(true);
			checkLists.add(qtCheckList);
		}
		getManagedBean().setScQtChecklists(checkLists);
	}

	public void deleteUploadFile() {
		debugLogger("deleteUploadFile");
		getManagedBean().getChecklistAttachment().clear();
	}

	public void deleteDocument() {
		debugLogger("deleteDocument");
		List<ScQtCheckList> checkLists = getManagedBean().getScQtChecklists();
		List<ScQtCheckList> selectedCheckLists = getManagedBean()
				.getSelectedCheckLists();
		if (checkLists.size() == selectedCheckLists.size()) {
			checkLists.clear();
		} else {
			for (ScQtCheckList scQtCheckList : selectedCheckLists) {
				checkLists.remove(scQtCheckList);
			}
		}

	}

	/**
	 * determineIcon for displaying image icon in pid
	 */
	public String determineIcon(ScQtCheckListAttachment attachment) {
		String imageFileName = "icon_file_default.png";
		String ext = "";

		if (attachment != null) {
			String fileName = attachment.getFileName();

			if (fileName != null) {
				int mid = fileName.lastIndexOf(".");
				ext = fileName.substring(mid + 1, fileName.length());
			}

			if ("doc".equals(ext) || "docx".equals(ext)) {
				imageFileName = "icon_doc.png";
			} else if ("pdf".equals(ext)) {
				imageFileName = "icon_pdf.png";
			} else if ("xls".equals(ext) || "xlsx".equals(ext)
					|| "csv".equals(ext)) {
				imageFileName = "icon_exel.png";
			} else if ("jpg".equals(ext) || "bmp".equals(ext)
					|| "png".equals(ext) || "gif".equals(ext)) {
				imageFileName = "icon_image.png";
			} else if ("txt".equals(ext)) {
				imageFileName = "icon_txt.png";
			}
		}
		return imageFileName;
	}
}

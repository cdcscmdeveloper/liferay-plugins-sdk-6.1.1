package my.ep.web.backingbean.sc.quatationtender;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchBetweenValue;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.sc.quationtender.IQTDetailSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.sc.quationtender.QuationTenderReqDTO;
import my.ep.dto.sc.quationtender.QuationTenderResDTO;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.common.PmReportSection;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtCheckList;
import my.ep.persistence.entity.sc.quationtender.ScQtCheckListAttachment;
import my.ep.type.aut.SearchCondition;
import my.ep.type.aut.SearchOperator;
import my.ep.web.bean.sc.quatationtender.QtnTenderSearchBean;
import my.ep.web.managedbean.sc.quationtender.QTDetailManagedBean;
import my.ep.web.sc.quationtender.common.QTConstants;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.commons.lang.StringUtils;
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
			ScQtCheckListAttachment attachment = getManagedBean()
					.getQtCheckList().getScQtCheckListAttachment();
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

	public void documentCloneSearchAction() {
		try {
			QtnTenderSearchBean qtnTenderSearchBean = this.getManagedBean()
					.getCloneQtnTenderSearchBean();
			List<ScQt> cloneSearchQt = retriveQtCommittes(qtnTenderSearchBean);

			this.getManagedBean().setDocumentCloneTenderList(cloneSearchQt);
		} catch (Exception e) {

		}
		/*
		 * List<ScQtCommittee> qtCommittee = retriveQtCommittes();
		 * getManagedBean().setCloneSearchCommittes(qtCommittee);
		 */
	}

	/**
	 * @param qtnTenderSearchBean
	 * @return
	 */
	private List<ScQt> retriveQtCommittes(
			QtnTenderSearchBean qtnTenderSearchBean) {
		QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
		if (qtnTenderSearchBean != null) {
			SearchBean<ScQt> qtSearchBean = new SearchBean<ScQt>();
			List<SearchFilterBean> searchFilterBean = new ArrayList<SearchFilterBean>();

			String qtTitle = qtnTenderSearchBean.getQtTitle();
			if (!StringUtils.isBlank(qtTitle)) {
				SearchFilterBean bean = new SearchFilterBean();
				bean.setSearchField(QTConstants.QT_TITILE);
				bean.setSearchOperator(SearchOperator.OPERATOR_LIKE.value());
				bean.setSearchValue(qtTitle);
				searchFilterBean.add(bean);
			}

			String qtNumber = qtnTenderSearchBean.getQtNo();
			if (!StringUtils.isBlank(qtNumber)) {
				SearchFilterBean bean = new SearchFilterBean();
				bean.setSearchField(QTConstants.QT_NUMBER);
				bean.setSearchOperator(SearchOperator.OPERATOR_LIKE.value());
				bean.setSearchValue(qtNumber);
				searchFilterBean.add(bean);
			}

			String procurementMode = qtnTenderSearchBean.getProcurementMode();
			if (!StringUtils.isBlank(procurementMode)) {
				SearchFilterBean bean = new SearchFilterBean();
				bean.setAndOr(SearchCondition.CONDITION_OR.value());
				bean.setSearchField(QTConstants.PROCUREMENT_MODE);
				bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
				bean.setSearchValue(procurementMode);
				searchFilterBean.add(bean);
			}

			Date fromCreatedDate = qtnTenderSearchBean.getFromCreatedDate();
			Date toCreatedDate = qtnTenderSearchBean.getToCreatedDate();
			if (null != fromCreatedDate || toCreatedDate != null) {
				SearchFilterBean bean = new SearchFilterBean();
				bean.setSearchField(QTConstants.CREATED_DATE);
				bean.setSearchOperator(SearchOperator.OPERATOR_BETWEEN.value());

				SearchBetweenValue<Date> betweenVal = new SearchBetweenValue<Date>(
						fromCreatedDate, toCreatedDate);

				bean.setSearchValue(betweenVal);
				searchFilterBean.add(bean);
			}

			// Adding search filterbeans to searchbean.
			qtSearchBean.getDataFilterBeans().addAll(searchFilterBean);
			reqDTO.setQtSearchBean(qtSearchBean);

		}
		QuationTenderResDTO dto = null;
		IQTDetailSO detailSO;
		try {
			detailSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);

			dto = detailSO.getQuationTenders(reqDTO);
		} catch (Exception ex) {
			ex.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							ex.toString(), ex.getMessage()));

		} finally {
			detailSO = null;
		}

		return null != dto.getQtResultBean().getResultList() ? dto
				.getQtResultBean().getResultList() : new ArrayList<ScQt>();
	}

	/*	*//**
	 * @return
	 */
	/*
	 * private List<ScQtCommittee> retriveQtCommittes() { CommitteReqDTO
	 * committeReqDTO = new CommitteReqDTO(); ISearchBean<ScQtCommittee>
	 * searchBean = new SearchBean<ScQtCommittee>(); CommitteResDTO resDto =
	 * null;
	 * 
	 * if (getManagedBean().getCreatedDtFrom() != null) { SearchFilterBean
	 * creatDtSearchBean = new SearchFilterBean(
	 * SearchOperator.OPERATOR_EQUALS.value());
	 * creatDtSearchBean.setSearchField("scCommittee.createdDate");
	 * creatDtSearchBean.setSearchValue(getManagedBean() .getCreatedDtFrom());
	 * searchBean.getDataFilterBeans().add(creatDtSearchBean); }
	 * 
	 * if (getManagedBean().getCreatedDtTo() != null) { SearchFilterBean
	 * creatDtSearchBean = new SearchFilterBean(
	 * SearchOperator.OPERATOR_EQUALS.value());
	 * creatDtSearchBean.setSearchField("scCommittee.createdDate");
	 * creatDtSearchBean.setSearchValue(getManagedBean().getCreatedDtTo());
	 * searchBean.getDataFilterBeans().add(creatDtSearchBean); }
	 * 
	 * if (getManagedBean().getQtNo() != null &&
	 * !"".equalsIgnoreCase(getManagedBean().getQtNo())) { SearchFilterBean
	 * qtnoSearchBean = new SearchFilterBean(
	 * SearchOperator.OPERATOR_EQUALS.value());
	 * qtnoSearchBean.setSearchField("scQt.qtNo");
	 * qtnoSearchBean.setSearchValue(getManagedBean().getQtNo());
	 * 
	 * searchBean.getDataFilterBeans().add(qtnoSearchBean); }
	 * 
	 * if (getManagedBean().getTitle() != null &&
	 * !"".equalsIgnoreCase(getManagedBean().getTitle())) { SearchFilterBean
	 * qtnoSearchBean = new SearchFilterBean(
	 * SearchOperator.OPERATOR_EQUALS.value());
	 * qtnoSearchBean.setSearchField("scQt.qtTitle");
	 * qtnoSearchBean.setSearchValue(getManagedBean().getTitle());
	 * 
	 * searchBean.getDataFilterBeans().add(qtnoSearchBean); }
	 * 
	 * if (getManagedBean().getStatus() != null &&
	 * !"".equalsIgnoreCase(getManagedBean().getStatus())) { }
	 * committeReqDTO.setCommitteSearchBean(searchBean);
	 * 
	 * try { ICommitteSO committeSO = BusinessDelegate.getInstance()
	 * .getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, ICommitteSO.class);
	 * 
	 * resDto = committeSO.searchQTCommittees(committeReqDTO); } catch
	 * (Exception ex) { ex.printStackTrace(); }
	 * 
	 * return resDto.getCommitteResultBean().getResultList(); }
	 */

	public void onRowCloneAction(SelectEvent event) {
		ScQt scQt = (ScQt) event.getObject();
		QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
		QuationTenderResDTO resDTO = null;
		try {
			IQTDetailSO detailSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTDetailSO.class);
			reqDTO.setQtId(scQt.getQtId());
			List<String> type = new ArrayList<String>();
			type.add("UP");
			type.add("TD");
			reqDTO.setCheckListType(type);

			resDTO = detailSO.retriveQTDocDetails(reqDTO);

			List<ScQtCheckList> list = resDTO.getDocumentResultBean()
					.getResultList();

			System.out.println("list: " + list.size());

			for (ScQtCheckList scQtCheckList : list) {
				System.out.println(scQtCheckList.getQtChecklistId() + "|"
						+ scQtCheckList.getChecklistType() + "|"
						+ scQtCheckList.getDocType() + "|"
						+ scQtCheckList.getTitle() + "|"
						+ scQtCheckList.getChecklistItemId()
						+ "|"
						+ scQtCheckList.getChecklistSeq()
						// + "|" + scQtCheckList.getQtWeightageId()
						+ "|" + scQtCheckList.getPmStatus() + "|"
						+ scQtCheckList.getScQtCheckListAttachment() + "|"
						+ scQtCheckList.getScQtDoc());
			}
			this.getManagedBean().setQtCheckLists(list);
			System.out.println("======== list size==== : "
					+ getManagedBean().getQtCheckLists().size());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void addDocumentCloneAction() {
		debugLogger("addDocumentCloneAction");
		
		if (getManagedBean().getScQtChecklists() == null) {
			getManagedBean().setScQtChecklists(getManagedBean().getSelectedQtCheckLists());
		} else {
			getManagedBean().getScQtChecklists().addAll(getManagedBean().getSelectedQtCheckLists());
		}
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

	public void resetClone() {
		debugLogger("resetClone");
		this.getManagedBean().setTitle(null);
		this.getManagedBean().setQtNo(null);
		this.getManagedBean().setProcurementMode(null);
		this.getManagedBean().setCreatedDtFrom(null);
		this.getManagedBean().setCreatedDtTo(null);
		this.getManagedBean().setStatus(null);
	}
}

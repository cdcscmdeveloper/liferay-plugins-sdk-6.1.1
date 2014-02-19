/**
 * 
 */
package my.ep.web.backingbean.sc.templatedoc;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.pm.common.codecategory.ICodeCategorySO;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.sc.quationtender.IQTDetailSO;
import my.ep.biz.service.sc.quationtender.IQTTemplateSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.pm.common.codecategory.CodeCategoryReqDTO;
import my.ep.dto.pm.common.codecategory.CodeCategoryResDTO;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.dto.sc.quationtender.QTTemplateDocReqDTO;
import my.ep.dto.sc.quationtender.QTTemplateDocResDTO;
import my.ep.dto.sc.quationtender.QuationTenderReqDTO;
import my.ep.dto.sc.quationtender.QuationTenderResDTO;
import my.ep.persistence.entity.pm.common.PmCode;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtCheckList;
import my.ep.persistence.entity.sc.quationtender.ScQtDoc;
import my.ep.persistence.entity.tpl.doc.TplDocMaster;
import my.ep.persistence.entity.tpl.doc.TplDocSection;
import my.ep.type.aut.SearchOperator;
import my.ep.web.backingbean.sc.quatationtender.SourcingBaseBackingBean;
import my.ep.web.backingbean.tpl.doc.TemplateDocBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.sc.templatedoc.SourcingTemplateDocManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;

/**
 * @author ACER
 * 
 */
@ManagedBean(name = "sourcingTemplateDBB")
@ViewScoped
public class SourcingTemplateDocBackingBean extends
		SourcingBaseBackingBean<SourcingTemplateDocManagedBean> {
	private IPagingDataModel<ScQtDoc> templateDocument = new SearchTemplateDocumentPagingDataModel();

	public SourcingTemplateDocBackingBean() {

	}

	@PostConstruct
	public void init() {
		if (getManagedBean().getScQtDoc() == null) {
			getManagedBean().setScQtDoc(new ScQtDoc());

		}

		if (this.getManagedBean().getScQtDoc().getScQt() == null) {
			getManagedBean().getScQtDoc().setScQt(new ScQt());
		}

	}

	public String sourcingTemplateAction() {

		try {

			TemplateDocBackingBean docBB = new TemplateDocBackingBean();
			docBB.createNewTemplate();
			this.getManagedBean()
					.getScQtDoc()
					.setTplDocMaster(
							docBB.getManagedBean().getSelectedTemplate());

			if (this.getManagedBean().getScQtDoc().getScQtCheckList() == null) {
				this.getManagedBean().getScQtDoc()
						.setScQtCheckList(new ScQtCheckList());
			}
			this.getManagedBean().getScQtDoc().getScQt().setQtNo("qt123");
			this.getManagedBean().getScQtDoc().getTplDocMaster()
					.setPmStatus(1L);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "templateDocMaintenance.xhtml?faces-redirect=true";
	}

	public String cloneTemplateAction(ScQtDoc qtDoc) {

		try {

			TemplateDocBackingBean docBB = new TemplateDocBackingBean();
			docBB.createNewTemplate();
			this.getManagedBean()
					.getScQtDoc()
					.setTplDocMaster(
							docBB.getManagedBean().getSelectedTemplate());
			if (this.getManagedBean().getScQtDoc().getScQtCheckList() == null) {
				this.getManagedBean().getScQtDoc()
						.setScQtCheckList(new ScQtCheckList());
			}
			this.getManagedBean().getScQtDoc().getScQt()
					.setQtNo(qtDoc.getScQt().getQtNo());
			this.getManagedBean().getScQtDoc().getTplDocMaster()
					.setPmStatus(1L);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "templateDocMaintenance.xhtml?faces-redirect=true";
	}

	public void retriveChecklist(AjaxBehaviorEvent event) {
		IQTTemplateSO qtTemplateSO;
		try {
			String checkList = (String) event.getComponent().getAttributes()
					.get("value");
			qtTemplateSO = getTemplateSO();
			QTTemplateDocReqDTO reqDTO = new QTTemplateDocReqDTO();
			reqDTO.setQtId(146L);
			reqDTO.setDocType(checkList);
			QTTemplateDocResDTO resDto = qtTemplateSO.retriveCheckList(reqDTO);
			this.getManagedBean().setCheckListItem(
					resDto.getCheckSearchResultBean().getResultList());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * public void retriveTitle(AjaxBehaviorEvent event) { String title =
	 * (String) event.getComponent().getAttributes() .get("value");
	 * this.getManagedBean().getScQtDoc().getTplDocMaster().setDocTitle(title);
	 * }
	 */

	public void handleCheckListChanged(ScQtDoc doc) {
		try {

			ScQtCheckList checkList = doc.getScQtCheckList();

			if (checkList.getQtChecklistId() != null) {

				List<ScQtCheckList> beanCheckList = this.getManagedBean()
						.getCheckListItem();
				for (ScQtCheckList list : beanCheckList) {

					if (list.getQtChecklistId().longValue() == checkList
							.getQtChecklistId().longValue()) {
						doc.setScQtCheckList(list);
						this.getManagedBean()
								.setChecklistTitle(list.getTitle());
						this.getManagedBean().getScQtDoc().getScQtCheckList()
								.setTitle(list.getTitle());
						break;
					}
				}

			} else {
				this.getManagedBean().setChecklistTitle(null);
				this.getManagedBean().getScQtDoc().getScQtCheckList()
						.setTitle("");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void saveAction() {
		IQTTemplateSO qtTemplateSO;
		try {

			ScQtDoc qtDoc = this.getManagedBean().getScQtDoc();
			TplDocMaster master = this.getManagedBean().getScQtDoc()
					.getTplDocMaster();
			ScQtCheckList checkList = this.getManagedBean().getScQtDoc()
					.getScQtCheckList();
			checkList.setScQtDoc(qtDoc);

			QTTemplateDocReqDTO qtTemplateReqDTO = new QTTemplateDocReqDTO();
			QTTemplateDocResDTO resDTO = new QTTemplateDocResDTO();
			IQTDetailSO QtDetailsSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);

			QuationTenderReqDTO tenderReqDTO = new QuationTenderReqDTO();

			tenderReqDTO.setQtTndrNo(this.getManagedBean().getScQtDoc()
					.getScQt().getQtNo());

			QuationTenderResDTO tenderResDTO = new QuationTenderResDTO();

			tenderResDTO = QtDetailsSO.retriveQtByQtNumber(tenderReqDTO);

			if (checkList.getQtChecklistId() == null) {

				checkList.setDocType("0");
				checkList.setChecklistType("TD");
				checkList.setChecklistSeq(1);
				checkList.setRecordStatus(this.getManagedBean().getScQtDoc()
						.isRecordStatus());
				checkList.setManditory(true);
				checkList.setTitle(checkList.getTitle());
				checkList.setScQtCheckListAttachment(null);
				checkList.setChangedBy(10l);
				checkList.setCreatedBy(1l);
				checkList.setScQt(tenderResDTO.getQtDetail());
				checkList.setCreatedDate(new Timestamp(System
						.currentTimeMillis()));
				IStatusSO statussO = BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IStatusSO.class);

				StatusReqDTO statusreq = new StatusReqDTO();
				statusreq.setStatusId(1L);
				StatusResDTO statusres = statussO
						.retrieveStatusByStatusId(statusreq);
				checkList.setPmStatus(statusres.getPmStatus());
				// checkList.setScQtDoc(qtDoc);
			}

			ICodeCategorySO objectSO = (ICodeCategorySO) BusinessDelegate
					.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, ICodeCategorySO.class);
			CodeCategoryReqDTO objreqDTO = new CodeCategoryReqDTO();
			objreqDTO.setCodeId(new Long(1L));
			CodeCategoryResDTO objresDTO = objectSO.retrieveCodeById(objreqDTO);
			PmCode code = objresDTO.getPmCode();

			qtDoc.setChangedBy(10l);
			qtDoc.setCreatedBy(1l);
			qtDoc.setCreatedDate(new Timestamp(System.currentTimeMillis()));

			master.setChangedBy(10l);
			master.setCreatedBy(1l);
			master.setCreatedDate(new Timestamp(System.currentTimeMillis()));
			master.setRecordStatus(this.getManagedBean().getScQtDoc()
					.isRecordStatus());
			master.setDocTitle(checkList.getTitle());
			master.setDocDesc(this.getManagedBean().getDocDescription());
			master.setDocName("TemplateDocument");
			master.setCategoryId(new Long(1L));
			master.setProcessType(code);
			master.setTemplateType(code);
			master.setPmStatus(1l);

			TemplateDocBackingBean docBB = new TemplateDocBackingBean();
			List<TplDocSection> docSectionList = new ArrayList<TplDocSection>();
			docBB.getDocSectionList(1,
					docBB.getManagedBean().getDocSectionTN(), docSectionList);
			for (int i = 0; i < docSectionList.size(); i++) {
				TplDocSection sectionOrder = docSectionList.get(i);
				sectionOrder.setSectionOrder(i);
			}
			master.setTplDocSectionCollection(docSectionList);

			List<TplDocSection> deletedSectionList = new ArrayList<TplDocSection>();
			if (docBB.getManagedBean().getDeletedList() != null
					&& !docBB.getManagedBean().getDeletedList().isEmpty()) {
				for (TplDocSection genTemplateSection : docBB.getManagedBean()
						.getDeletedList()) {
					genTemplateSection.setTplDocMaster(master);
					genTemplateSection.setDeleted(true);
					deletedSectionList.add(genTemplateSection);
				}
			}
			master.setDeletedCollection(deletedSectionList);

			qtDoc.setScQt(tenderResDTO.getQtDetail());
			qtDoc.setTplDocMaster(master);

			qtDoc.setTplSpecMaster(null);
			qtDoc.setScQtCheckList(checkList);

			qtTemplateSO = getTemplateSO();
			QTTemplateDocReqDTO templateReqDTO = new QTTemplateDocReqDTO();
			templateReqDTO.setQtDoc(qtDoc);
			QTTemplateDocResDTO resDto = qtTemplateSO
					.saveOrUpdateTemplateDoc(templateReqDTO);
			if (resDTO.isSuccessful()) {
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO,
								"Record saved successfully.", ""

						));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * @return the templateDocument
	 */
	public IPagingDataModel<ScQtDoc> getTemplateDocument() {
		return templateDocument;
	}

	/**
	 * @param templateDocument
	 *            the templateDocument to set
	 */
	public void setTemplateDocument(IPagingDataModel<ScQtDoc> templateDocument) {
		this.templateDocument = templateDocument;
	}

	public void searchDocAction() {

		templateDocument.reloadData();

	}

	private class SearchTemplateDocumentPagingDataModel extends
			PagingDataModel<ScQtDoc> {
		private static final long serialVersionUID = 1L;

		public SearchTemplateDocumentPagingDataModel() {

			super("searchDoc");

		}

		public ISearchResultBean<ScQtDoc> pageChangeListener(
				ISearchBean<ScQtDoc> searchBean) {
			IQTTemplateSO qtTemplateSO;

			try {
				QTTemplateDocReqDTO qtTemplateReqDTO = new QTTemplateDocReqDTO();

				if (getManagedBean().getDocTitle() != null
						&& !"".equalsIgnoreCase(getManagedBean().getDocTitle())) {

					SearchFilterBean templateSearchBean = new SearchFilterBean(
							SearchOperator.OPERATOR_EQUALS.value());
					templateSearchBean.setSearchField("tplDocMaster.docTitle");
					templateSearchBean.setSearchValue(getManagedBean()
							.getDocTitle());
					searchBean.getDataFilterBeans().add(templateSearchBean);

				}

				if (getManagedBean().getProcurementId() != null) {
					SearchFilterBean templateSearchBean = new SearchFilterBean(
							SearchOperator.OPERATOR_EQUALS.value());
					templateSearchBean.setSearchField("scQt.procurementMode");
					templateSearchBean.setSearchValue(getManagedBean()
							.getProcurementId());
					searchBean.getDataFilterBeans().add(templateSearchBean);
				}

				if (getManagedBean().getStatusId() != null) {
					SearchFilterBean templateSearchBean = new SearchFilterBean(
							SearchOperator.OPERATOR_EQUALS.value());
					templateSearchBean
							.setSearchField("tplDocMaster.pmStatus.statusId");
					templateSearchBean.setSearchValue(getManagedBean()
							.getStatusId());
					searchBean.getDataFilterBeans().add(templateSearchBean);
				}

				if (getManagedBean().isRecordStatus()) {
					SearchFilterBean templateSearchBean = new SearchFilterBean(
							SearchOperator.OPERATOR_EQUALS.value());
					templateSearchBean.setSearchField("recordStatus");
					templateSearchBean.setSearchValue(getManagedBean()
							.isRecordStatus());
					searchBean.getDataFilterBeans().add(templateSearchBean);
				}
				if (getManagedBean().getScQtDoc().getScQt().getQtNo() != null
						&& !"".equalsIgnoreCase(getManagedBean().getScQtDoc()
								.getScQt().getQtNo())) {
					SearchFilterBean templateSearchBean = new SearchFilterBean(
							SearchOperator.OPERATOR_EQUALS.value());
					templateSearchBean.setSearchField("scQt.qtNo");
					templateSearchBean.setSearchValue(getManagedBean()
							.getScQtDoc().getScQt().getQtNo());

					searchBean.getDataFilterBeans().add(templateSearchBean);
				}

				qtTemplateReqDTO.setDocSearchBean(searchBean);
				qtTemplateSO = getTemplateSO();
				QTTemplateDocResDTO resDto = qtTemplateSO
						.retrieveTemplates(qtTemplateReqDTO);

				if (resDto.isSuccessful()) {
					return resDto.getSearchResultBean();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

	}

	public IQTTemplateSO getTemplateSO() {
		IQTTemplateSO templateSO = null;

		try {
			templateSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTTemplateSO.class);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return templateSO;

	}
}
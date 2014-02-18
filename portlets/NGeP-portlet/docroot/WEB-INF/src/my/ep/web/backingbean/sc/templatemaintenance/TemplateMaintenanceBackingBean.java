/**
 * 
 */
package my.ep.web.backingbean.sc.templatemaintenance;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.sc.quationtender.IQTDetailSO;
import my.ep.biz.service.sc.quationtender.IQTTemplateSO;
import my.ep.constant.tpl.TemplateSpecConst;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.dto.sc.quationtender.QTTemplateDocReqDTO;
import my.ep.dto.sc.quationtender.QTTemplateDocResDTO;
import my.ep.dto.sc.quationtender.QuationTenderReqDTO;
import my.ep.dto.sc.quationtender.QuationTenderResDTO;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtCheckList;
import my.ep.persistence.entity.sc.quationtender.ScQtDoc;
import my.ep.persistence.entity.sc.quationtender.ScQtZoneCriteria;
import my.ep.persistence.entity.tpl.spec.TplSpecMaster;
import my.ep.persistence.entity.tpl.spec.TplSpecQuestion;
import my.ep.type.aut.SearchOperator;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.tpl.spec.TemplateSpecBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.bean.tpl.TemplateSpecBean;
import my.ep.web.managedbean.sc.templatemaintenance.TemplateMaintenanceManagedBean;
import my.ep.web.sc.quationtender.common.QTConstants;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.commons.beanutils.BeanUtils;

/**
 * @author ACER
 * 
 */
@ManagedBean(name = "TemplateMBB")
@ViewScoped
public class TemplateMaintenanceBackingBean extends
		BaseBackingBean<TemplateMaintenanceManagedBean> {

	private IPagingDataModel<ScQtDoc> templateSpecSearch = new SearchTemplateSpecPagingDataModel();
	private IPagingDataModel<ScQtDoc> templateSpecCreate = new CreateTemplateSpecPagingDataModel();

	public TemplateMaintenanceBackingBean() {

	}

	@PostConstruct
	public void init() {
		try {

		} catch (Exception e) {
			throw new IllegalArgumentException();
		}

	}

	public String cloneAction(ScQtDoc scqtDoc) {
		IQTTemplateSO qtTemplateSO;
		try {
			QTTemplateDocReqDTO qtTemplateReqDTO = new QTTemplateDocReqDTO();

			QTTemplateDocResDTO resDto = null;

			if (getManagedBean().getScqtDoc() == null) {
				getManagedBean().setScqtDoc(new ScQtDoc());

			}
			if (getManagedBean().getScqtDoc().getScQt() == null) {
				getManagedBean().getScqtDoc().setScQt(new ScQt());
			}
			if (getManagedBean().getScqtDoc().getTplSpecMaster() == null) {

				getManagedBean().getScqtDoc().setTplSpecMaster(
						new TplSpecMaster());

			}

			if (getManagedBean().getScqtDoc().getTplSpecMaster()
					.getProcurementCategory() == null) {

				getManagedBean().getScqtDoc().getTplSpecMaster()
						.setProcurementCategory(new PmParameter());

			}
			if (getManagedBean().getScqtDoc().getTplSpecMaster()
					.getItemTypeCode() == null) {

				getManagedBean().getScqtDoc().getTplSpecMaster()
						.setItemTypeCode(new PmParameter());

			}
			if (getManagedBean().getScqtDoc().getTplSpecMaster()
					.getSpecTypeCode() == null) {

				getManagedBean().getScqtDoc().getTplSpecMaster()
						.setSpecTypeCode(new PmParameter());

			}
			if (getManagedBean().getScqtDoc().getTplSpecMaster()
					.getOrgProfile() == null) {

				getManagedBean().getScqtDoc().getTplSpecMaster()
						.setOrgProfile(new PmOrgProfile());

			}
			if (getManagedBean().getScqtDoc().getTplSpecMaster()
					.getTemplateTypeCode() == null) {

				getManagedBean().getScqtDoc().getTplSpecMaster()
						.setTemplateTypeCode(new PmParameter());

			}

			if (getManagedBean().getScqtDoc().getTplSpecMaster().getStatus() == null) {

				getManagedBean().getScqtDoc().getTplSpecMaster()
						.setStatus(new PmStatus());

			}
			if (getManagedBean().getScqtDoc().getScQtZoneCriteria() == null) {
				getManagedBean().getScqtDoc().setScQtZoneCriteria(
						new ScQtZoneCriteria());
			}
			TemplateSpecBackingBean specBB = new TemplateSpecBackingBean();
			TemplateSpecBean templateSpecBean = getManagedBean().getTemplateSpecBean();
			TplSpecMaster specMaster1 = new TplSpecMaster();
			specMaster1.setSpecMasterId(new Long(scqtDoc.getTplSpecMaster()
					.getSpecMasterId()));
			// specMaster.setSpecMasterId(1L);
			templateSpecBean.setContentType(
					TemplateSpecConst.CONTENT_TYPE_QUESTION);
			templateSpecBean.setSpecMaster(specMaster1);
			specBB.retrieveTemplateSpecMaster(templateSpecBean);
			// scqtDoc.getTplSpecMaster().getProcurementCategory()
			// .getParameterId()
			if (this.getManagedBean().getScqtDoc().getScQtCheckList() == null) {
				this.getManagedBean().getScqtDoc()
						.setScQtCheckList(new ScQtCheckList());
			}
			this.getManagedBean()
					.getScqtDoc()
					.getTplSpecMaster()
					.getProcurementCategory()
					.setParameterId(
							scqtDoc.getTplSpecMaster().getProcurementCategory()
									.getParameterId());
			/*
			 * scqtDoc.getTplSpecMaster().getItemTypeCode() .getParameterId()
			 */
			this.getManagedBean()
					.getScqtDoc()
					.getTplSpecMaster()
					.getItemTypeCode()
					.setParameterId(
							scqtDoc.getTplSpecMaster().getItemTypeCode()
									.getParameterId());
			/*
			 * scqtDoc.getTplSpecMaster().getSpecTypeCode() .getParameterId(
			 */
			this.getManagedBean()
					.getScqtDoc()
					.getTplSpecMaster()
					.getSpecTypeCode()
					.setParameterId(
							scqtDoc.getTplSpecMaster().getSpecTypeCode()
									.getParameterId());
			this.getManagedBean()
					.getScqtDoc()
					.getTplSpecMaster()
					.getStatus()
					.setStatusId(
							scqtDoc.getTplSpecMaster().getStatus()
									.getStatusId());
			this.getManagedBean().getScqtDoc().getScQt()
					.setQtNo(scqtDoc.getScQt().getQtNo());

			qtTemplateReqDTO.setQtId(146L);
			qtTemplateReqDTO.setDocType("1");
			qtTemplateReqDTO.setSpecMaster(scqtDoc.getTplSpecMaster());
			qtTemplateReqDTO.setSpecMasterId(scqtDoc.getTplSpecMaster()
					.getSpecMasterId());

			try {
				qtTemplateSO = getTemplateSO();

				resDto = qtTemplateSO
						.retriveTemplateDocDetails(qtTemplateReqDTO);

				retriveCheckList(resDto.getCheckListItems());
				retriveZoneList(resDto.getZones());

			} catch (Exception e) {
				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			qtTemplateSO = null;
		}

		return "newTemplateMaintenance.xhtml?faces-redirect=true";
	}

	public void retriveZoneList(List zonesList) {
		List<SelectItem> selectItems = new ArrayList<SelectItem>();
		try {
			ScQtZoneCriteria scQtZone;

			for (int i = 0; i < zonesList.size(); i++) {
				scQtZone = (ScQtZoneCriteria) zonesList.get(i);
				selectItems.add(new SelectItem(scQtZone.getQtZoneCriteriaId(),
						scQtZone.getZoneName()));
			}
			this.getManagedBean().setZoneList(selectItems);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void retriveCheckList(List checklist) {
		List<SelectItem> selectItems = new ArrayList<SelectItem>();
		try {

			this.getManagedBean().setCheckListItem(checklist);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void checkSupportingDoc(AjaxBehaviorEvent e) {

		String supportingDoc = (String) e.getComponent().getAttributes()
				.get("value");

		this.getManagedBean().setSupportingDoc(supportingDoc);

	}

	public void handleCheckList(ScQtDoc qtDoc) {
		try {

			ScQtCheckList checkList = qtDoc.getScQtCheckList();

			/*
			 * if(checkList.getQtChecklistId()==null){
			 * System.out.println("=====enter null loop==");
			 * this.getManagedBean().setSpecificationType(null);
			 * this.getManagedBean
			 * ().getScqtDoc().getScQtCheckList().setTitle(""); }
			 */
			if (checkList.getQtChecklistId() != null) {

				List<ScQtCheckList> beanCheckList = this.getManagedBean()
						.getCheckListItem();
				for (ScQtCheckList list : beanCheckList) {

					if (list.getQtChecklistId().longValue() == checkList
							.getQtChecklistId().longValue()) {

						qtDoc.setScQtCheckList(list);
						this.getManagedBean().setSpecificationType(
								list.getTitle());
						this.getManagedBean().getScqtDoc().getScQtCheckList()
								.setTitle(list.getTitle());
						break;
					}
				}

			} else {

				this.getManagedBean().setSpecificationType(null);
				this.getManagedBean().getScqtDoc().getScQtCheckList()
						.setTitle("");

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// this.getManagedBean().getScqtDoc().getTplSpecMaster().setSpecName(checkValue);

	}

	public void supportingDocAdd() {

		try {
			List<ScQtCheckList> supportDocList = getManagedBean()
					.getSupportDocList();
			if (null == supportDocList) {
				supportDocList = new ArrayList<ScQtCheckList>();

			}

			ScQtCheckList docList = new ScQtCheckList();

			supportDocList.add(docList);
			this.getManagedBean().setSupportDocList(supportDocList);
		} catch (Exception e) {
			// e.printStackTrace();
			throw new IllegalArgumentException();
		}
	}

	public void save() {
		IQTTemplateSO qtTemplateSO;
		IStatusSO statussO;
		IOrgProfileSO ProfileSo;
		IQTDetailSO QtDetailsSO;
		try {

			statussO = BusinessDelegate.getInstance().getServiceObject("pm",
					IStatusSO.class);

			StatusReqDTO statusreq = new StatusReqDTO();
			statusreq.setStatusId(this.getManagedBean().getScqtDoc()
					.getTplSpecMaster().getStatus().getStatusId());
			StatusResDTO statusres = statussO
					.retrieveStatusByStatusId(statusreq);

			ProfileSo = BusinessDelegate.getInstance().getServiceObject("pm",
					IOrgProfileSO.class);

			OrgProfileReqDTO profileReq = new OrgProfileReqDTO();
			profileReq.setOrgProfileId(1L);
			OrgProfileResDTO profileRes = ProfileSo
					.retrieveOrgProfileByProfileId(profileReq);
			PmParameter parameter = this.getManagedBean().getScqtDoc()
					.getTplSpecMaster().getTemplateTypeCode();
			parameter.setParameterId(146L);

			ScQtDoc scQtDOc = this.getManagedBean().getScqtDoc();
			TplSpecMaster specMaster = this.getManagedBean().getScqtDoc()
					.getTplSpecMaster();

			ScQtCheckList checkList = this.getManagedBean().getScqtDoc()
					.getScQtCheckList();
			checkList.setScQtDoc(scQtDOc);
			// specMaster.setTemplateTypeCode(parameter);

			java.util.Date date = new java.util.Date(); // Right now
			java.sql.Timestamp timestamp = new java.sql.Timestamp(
					date.getTime());

			TemplateSpecBackingBean specBB = new TemplateSpecBackingBean();

			List<TplSpecQuestion> questions = getManagedBean().getTemplateSpecBean()
					.getSpecMaster().getTplSpecQuestions();
			;
			//TplSpecQuestion queston = questions.remove(0);
			List<TplSpecQuestion> questions1 = new ArrayList<TplSpecQuestion>();

			questions1.addAll(questions);
			List<TplSpecQuestion> questions2 = new ArrayList<TplSpecQuestion>();
			for (TplSpecQuestion tpl : questions1) {

				// TplSpecQuestion tpl2 = new TplSpecQuestion();
				TplSpecQuestion tpl1 = (TplSpecQuestion) BeanUtils
						.cloneBean(tpl);
				tpl1.setSpecQuestionId(null);
				tpl1.setTplSpecMaster(specMaster);
				tpl1.setTplSpecAnswers(null);
				tpl1.setTplSpecScorings(null);
				tpl1.setTplSpecTierQties(null);
				questions2.add(tpl1);
			}

			specMaster.setTplSpecQuestions(questions2);
			specMaster.setTemplateTypeCode(parameter);
			specMaster.setProcessType("SS");
			specMaster.setOrgProfile(profileRes.getPmOrgProfile());
			specMaster.setDurationMonth(12);
			specMaster.setTemplateStatus("D");
			specMaster.setScQtDocs(null);
			specMaster.setChangedBy(2L);
			specMaster.setCreatedBy(2L);
			specMaster.setBrandNew(false);
			specMaster.setRecordStatus(this.getManagedBean().getScqtDoc()
					.isRecordStatus());
			specMaster.setSpecName(this.getManagedBean().getScqtDoc()
					.getScQtCheckList().getTitle());
			specMaster.setCreatedDate(timestamp);

			QTTemplateDocReqDTO qtTemplateReqDTO = new QTTemplateDocReqDTO();
			QTTemplateDocResDTO resDTO = new QTTemplateDocResDTO();
			QtDetailsSO = BusinessDelegate.getInstance().getServiceObject("ss",
					IQTDetailSO.class);

			QuationTenderReqDTO tenderReqDTO = new QuationTenderReqDTO();

			tenderReqDTO.setQtTndrNo(this.getManagedBean().getScqtDoc()
					.getScQt().getQtNo());

			QuationTenderResDTO tenderResDTO = new QuationTenderResDTO();

			tenderResDTO = QtDetailsSO.retriveQtByQtNumber(tenderReqDTO);

			if (checkList.getQtChecklistId() == null) {

				checkList.setDocType("0");
				checkList.setChecklistType("TS");
				checkList.setChecklistSeq(1);
				checkList.setRecordStatus(this.getManagedBean().getScqtDoc()
						.isRecordStatus());
				checkList.setManditory(true);
				checkList.setTitle(checkList.getTitle());
				checkList.setScQtCheckListAttachment(null);
				checkList.setChangedBy(10l);
				checkList.setCreatedBy(1l);
				checkList.setScQt(tenderResDTO.getQtDetail());
				checkList.setCreatedDate(new Timestamp(System
						.currentTimeMillis()));
				checkList.setPmStatus(statusres.getPmStatus());
				// checkList.setScQtDoc(qtDoc);
			}
			scQtDOc.setScQt(tenderResDTO.getQtDetail());
			scQtDOc.setChangedBy(2L);
			scQtDOc.setCreatedBy(2L);
			scQtDOc.setCreatedDate(timestamp);
			scQtDOc.setTplDocMaster(null);
			scQtDOc.setScQtCheckList(checkList);
			scQtDOc.setTplSpecMaster(specMaster);
			scQtDOc.setScQtZoneCriteria(null);

			// qtTemplateReqDTO.setSpecMaster(specMaster);
			qtTemplateReqDTO.setQtDoc(scQtDOc);

			try {
				qtTemplateSO = getTemplateSO();

				resDTO = qtTemplateSO.saveOrUpdateTemplateDoc(qtTemplateReqDTO);
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

		} catch (Exception e) {
			e.printStackTrace();
			// throw new IllegalArgumentException();
		} finally {
			qtTemplateSO = null;
			statussO = null;
			ProfileSo = null;
			QtDetailsSO = null;

		}

	}

	public void submit() {

	}

	public void searchScqtDocAction() {
		templateSpecCreate.reloadData();

	}

	public void searchCreateListAction() {
		templateSpecSearch.reloadData();

	}

	public IQTTemplateSO getTemplateSO() {
		IQTTemplateSO qtTemplateSO = null;
		try {
			qtTemplateSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(QTConstants.SS_MODULE,
							IQTTemplateSO.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return qtTemplateSO;
	}

	/**
	 * @return the templateSpecSearch
	 */
	public IPagingDataModel<ScQtDoc> getTemplateSpecSearch() {
		return templateSpecSearch;
	}

	/**
	 * @param templateSpecSearch
	 *            the templateSpecSearch to set
	 */
	public void setTemplateSpecSearch(
			IPagingDataModel<ScQtDoc> templateSpecSearch) {
		this.templateSpecSearch = templateSpecSearch;
	}

	/**
	 * @return the templateSpecCreate
	 */
	public IPagingDataModel<ScQtDoc> getTemplateSpecCreate() {
		return templateSpecCreate;
	}

	/**
	 * @param templateSpecCreate
	 *            the templateSpecCreate to set
	 */
	public void setTemplateSpecCreate(
			IPagingDataModel<ScQtDoc> templateSpecCreate) {
		this.templateSpecCreate = templateSpecCreate;
	}

	private class SearchTemplateSpecPagingDataModel extends
			PagingDataModel<ScQtDoc> {
		private static final long serialVersionUID = 1L;

		public SearchTemplateSpecPagingDataModel() {

			super("searchTemplateSpec");

		}

		public ISearchResultBean<ScQtDoc> pageChangeListener(
				ISearchBean<ScQtDoc> searchBean) {
			IQTTemplateSO qtTemplateSO;

			try {
				QTTemplateDocReqDTO qtTemplateReqDTO = new QTTemplateDocReqDTO();

				if (getManagedBean().getSpecName() != null
						&& !"".equalsIgnoreCase(getManagedBean().getSpecName())) {
					SearchFilterBean committeSearchBean = new SearchFilterBean(
							SearchOperator.OPERATOR_EQUALS.value());
					committeSearchBean.setSearchField("tplSpecMaster.specName");
					committeSearchBean.setSearchValue(getManagedBean()
							.getSpecName());
					searchBean.getDataFilterBeans().add(committeSearchBean);

				}

				if (getManagedBean().getSearchSpecTypeId() != null) {
					SearchFilterBean committeTypeSearchBean = new SearchFilterBean(
							SearchOperator.OPERATOR_EQUALS.value());
					committeTypeSearchBean
							.setSearchField("tplSpecMaster.specTypeCode.codeId");
					committeTypeSearchBean.setSearchValue(getManagedBean()
							.getSearchSpecTypeId());
					searchBean.getDataFilterBeans().add(committeTypeSearchBean);
				}

				if (getManagedBean().getSearchItemTypeId() != null) {
					SearchFilterBean creatDtSearchBean = new SearchFilterBean(
							SearchOperator.OPERATOR_EQUALS.value());
					creatDtSearchBean
							.setSearchField("tplSpecMaster.itemTypeCode.codeId");
					creatDtSearchBean.setSearchValue(getManagedBean()
							.getSearchItemTypeId());
					searchBean.getDataFilterBeans().add(creatDtSearchBean);
				}

				if (getManagedBean().getSearchProcurementId() != null) {
					SearchFilterBean creatDtSearchBean = new SearchFilterBean(
							SearchOperator.OPERATOR_EQUALS.value());
					creatDtSearchBean
							.setSearchField("tplSpecMaster.procurementCategory.codeId");
					creatDtSearchBean.setSearchValue(getManagedBean()
							.getSearchProcurementId());
					searchBean.getDataFilterBeans().add(creatDtSearchBean);
				}
				if (getManagedBean().getQtNum() != null
						&& !"".equalsIgnoreCase(getManagedBean().getQtNum())) {
					SearchFilterBean qtnoSearchBean = new SearchFilterBean(
							SearchOperator.OPERATOR_EQUALS.value());
					qtnoSearchBean.setSearchField("scQt.qtNo");
					qtnoSearchBean.setSearchValue(getManagedBean().getQtNum());

					searchBean.getDataFilterBeans().add(qtnoSearchBean);
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

	private class CreateTemplateSpecPagingDataModel extends
			PagingDataModel<ScQtDoc> {
		private static final long serialVersionUID = 1L;

		public CreateTemplateSpecPagingDataModel() {

			super("createList");

		}

		public ISearchResultBean<ScQtDoc> pageChangeListener(
				ISearchBean<ScQtDoc> searchBean) {
			IQTTemplateSO qtTemplateSO;

			try {
				QTTemplateDocReqDTO qtTemplateReqDTO = new QTTemplateDocReqDTO();

				if (getManagedBean().getSpecName() != null
						&& !"".equalsIgnoreCase(getManagedBean().getSpecName())) {

					SearchFilterBean committeSearchBean = new SearchFilterBean(
							SearchOperator.OPERATOR_EQUALS.value());
					committeSearchBean.setSearchField("tplSpecMaster.specName");
					committeSearchBean.setSearchValue(getManagedBean()
							.getSpecName());
					searchBean.getDataFilterBeans().add(committeSearchBean);

				}

				if (getManagedBean().getSearchSpecTypeId() != null) {
					SearchFilterBean committeTypeSearchBean = new SearchFilterBean(
							SearchOperator.OPERATOR_EQUALS.value());
					committeTypeSearchBean
							.setSearchField("tplSpecMaster.specTypeCode.codeId");
					committeTypeSearchBean.setSearchValue(getManagedBean()
							.getSearchSpecTypeId());
					searchBean.getDataFilterBeans().add(committeTypeSearchBean);
				}

				if (getManagedBean().getSearchItemTypeId() != null) {
					SearchFilterBean creatDtSearchBean = new SearchFilterBean(
							SearchOperator.OPERATOR_EQUALS.value());
					creatDtSearchBean
							.setSearchField("tplSpecMaster.itemTypeCode.codeId");
					creatDtSearchBean.setSearchValue(getManagedBean()
							.getSearchItemTypeId());
					searchBean.getDataFilterBeans().add(creatDtSearchBean);
				}

				if (getManagedBean().getSearchProcurementId() != null) {
					SearchFilterBean creatDtSearchBean = new SearchFilterBean(
							SearchOperator.OPERATOR_EQUALS.value());
					creatDtSearchBean
							.setSearchField("tplSpecMaster.procurementCategory.codeId");
					creatDtSearchBean.setSearchValue(getManagedBean()
							.getSearchProcurementId());
					searchBean.getDataFilterBeans().add(creatDtSearchBean);
				}
				if (getManagedBean().getQtNum() != null
						&& !"".equalsIgnoreCase(getManagedBean().getQtNum())) {
					SearchFilterBean qtnoSearchBean = new SearchFilterBean(
							SearchOperator.OPERATOR_EQUALS.value());
					qtnoSearchBean.setSearchField("scQt.qtNo");
					qtnoSearchBean.setSearchValue(getManagedBean().getQtNum());

					searchBean.getDataFilterBeans().add(qtnoSearchBean);
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

}

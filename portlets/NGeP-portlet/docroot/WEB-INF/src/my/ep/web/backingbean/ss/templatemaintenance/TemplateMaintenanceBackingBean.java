/**
 * 
 */
package my.ep.web.backingbean.ss.templatemaintenance;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.ss.quationtender.IQTDetailSO;
import my.ep.biz.service.ss.quationtender.IQTTemplateSO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.dto.ss.quationtender.QTTemplateDocReqDTO;
import my.ep.dto.ss.quationtender.QTTemplateDocResDTO;
import my.ep.dto.ss.quationtender.QuationTenderReqDTO;
import my.ep.dto.ss.quationtender.QuationTenderResDTO;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.ss.quationtender.ScQt;
import my.ep.persistence.entity.ss.quationtender.ScQtCheckList;
import my.ep.persistence.entity.ss.quationtender.ScQtDoc;
import my.ep.persistence.entity.ss.quationtender.ScQtZoneCriteria;
import my.ep.persistence.entity.tpl.spec.TplSpecMaster;
import my.ep.persistence.entity.tpl.spec.TplSpecQuestion;
import my.ep.type.aut.SearchOperator;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.tpl.spec.TemplateSpecBackingBean;
import my.ep.web.managedbean.ss.templatemaintenance.TemplateMaintenanceManagedBean;
import my.ep.web.ss.quationtender.common.QTConstants;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.commons.beanutils.BeanUtils;

/**
 * @author ACER
 * 
 */
@ManagedBean(name = "TemplateMBB")
@RequestScoped
public class TemplateMaintenanceBackingBean extends
		BaseBackingBean<TemplateMaintenanceManagedBean> {

	public TemplateMaintenanceBackingBean() {

	}

	@PostConstruct
	public void init() {
		try {

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
			if(getManagedBean().getScqtDoc().getScQtZoneCriteria()==null){
				getManagedBean().getScqtDoc().setScQtZoneCriteria(new ScQtZoneCriteria());
			}
			/*
			 * this.getManagedBean().getScqtDoc().getTplSpecMaster()
			 * .getProcurementCategory().setCodeId(1L);
			 * this.getManagedBean().getScqtDoc().getTplSpecMaster()
			 * .getItemTypeCode().setCodeId(2L);
			 * this.getManagedBean().getScqtDoc().getTplSpecMaster().getStatus()
			 * .setStatusId(1L);
			 */
		} catch (Exception e) {
			throw new IllegalArgumentException();
		}

	}

	public String cloneAction(ScQtDoc scqtDoc) {
		try {
			QTTemplateDocReqDTO qtTemplateReqDTO = new QTTemplateDocReqDTO();

			QTTemplateDocResDTO resDto = null;
			System.out.println("=====enter edit action====");
			System.out.println("=====scqtDoc====" + scqtDoc.getQtDocId());
			/*
			 * System.out.println("==procuremnet id==" +
			 * scqtDoc.getTplSpecMaster().getProcurementCategory()
			 * .getCodeId()); System.out.println("====spec master id===" +
			 * scqtDoc.getTplSpecMaster().getSpecMasterId());
			 */
			TemplateSpecBackingBean specBB = new TemplateSpecBackingBean();
			TplSpecMaster specMaster = new TplSpecMaster();
			specMaster.setSpecMasterId(new Long(scqtDoc.getTplSpecMaster()
					.getSpecMasterId()));

			specBB.getManagedBean().setSpecMaster(specMaster);
			specBB.retrieveTemplateSpecMaster();
			this.getManagedBean()
					.getScqtDoc()
					.getTplSpecMaster()
					.getProcurementCategory()
					.setParameterId(
							scqtDoc.getTplSpecMaster().getProcurementCategory()
									.getParameterId());
			this.getManagedBean()
					.getScqtDoc()
					.getTplSpecMaster()
					.getItemTypeCode()
					.setParameterId(
							scqtDoc.getTplSpecMaster().getItemTypeCode()
									.getParameterId());
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
			System.out.println("====edit specification type==="
					+ scqtDoc.getTplSpecMaster().getSpecTypeCode().getParameterId());
			qtTemplateReqDTO.setQtId(146L);
			qtTemplateReqDTO.setDocType("1");
			qtTemplateReqDTO.setSpecMaster(scqtDoc.getTplSpecMaster());
			qtTemplateReqDTO.setSpecMasterId(scqtDoc.getTplSpecMaster()
					.getSpecMasterId());

			try {
				IQTTemplateSO qtTemplateSO = EpEjbBusinessDelegate
						.getInstance().getServiceObject(QTConstants.SS_MODULE,
								IQTTemplateSO.class);
				System.out.println("=====before retrive details====");
				resDto = qtTemplateSO
						.retriveTemplateDocDetails(qtTemplateReqDTO);
				System.out
						.println("====display retriveTemplateDocDetails size=== "
								+ resDto.getCheckListItems().size());
				retriveCheckList(resDto.getCheckListItems());
				retriveZoneList(resDto.getZones());

			} catch (Exception e) {
				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
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

			ScQtCheckList checkList;

			for (int i = 0; i < checklist.size(); i++) {
				checkList = (ScQtCheckList) checklist.get(i);
				selectItems.add(new SelectItem(checkList.getQtChecklistId(),
						checkList.getTitle()));
			}
			this.getManagedBean().setCheckListItem(selectItems);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void checkSupportingDoc(AjaxBehaviorEvent e) {

		String supportingDoc = (String) e.getComponent().getAttributes()
				.get("value");

		this.getManagedBean().setSupportingDoc(supportingDoc);

	}

	public void selectChecklist(AjaxBehaviorEvent e) {

		String checkValue = (String) e.getComponent().getAttributes()
				.get("value");
		System.out.println("=====display check value==="+checkValue);

		this.getManagedBean().getScqtDoc().getTplSpecMaster().setSpecName(checkValue);

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

	/*
	 * public TplSpecQuestion deepCopy(TplSpecQuestion input){ TplSpecQuestion
	 * copy = new TplSpecQuestion(); copy.setValue(input.);//.. copy primitives,
	 * deep copy objects again
	 * 
	 * return copy; }
	 */
	public void save() {
		try {

			IParameterSO parameterSO = BusinessDelegate.getInstance()
					.getServiceObject("pm", IParameterSO.class);

			ParameterReqDTO reqDTO = new ParameterReqDTO();
			System.out.println("manga template construcoor" + parameterSO);
			reqDTO.setParameterId(this.getManagedBean().getScqtDoc()
					.getTplSpecMaster().getProcurementCategory().getParameterId());
			ParameterResDTO resprocure = parameterSO
					.retrieveParameterById(reqDTO);
			System.out.println("===resprocure===="
					+ resprocure.getPmParameter().getParameterId());
			ParameterReqDTO reqDTOItem = new ParameterReqDTO();
			reqDTOItem.setParameterId(this.getManagedBean().getScqtDoc()
					.getTplSpecMaster().getItemTypeCode().getParameterId());
			ParameterResDTO resItem = parameterSO
					.retrieveParameterById(reqDTOItem);
			System.out.println("===resItem===="
					+ resItem.getPmParameter().getParameterId());
			ParameterReqDTO reqDTOTemp = new ParameterReqDTO();
			reqDTOTemp.setParameterId(1L);
			ParameterResDTO resTemp = parameterSO
					.retrieveParameterById(reqDTOTemp);
			System.out.println("===resTemp===="
					+ resTemp.getPmParameter().getParameterId());
			ParameterReqDTO reqDTOSpec = new ParameterReqDTO();

			reqDTOSpec.setParameterId(this.getManagedBean().getScqtDoc()
					.getTplSpecMaster().getSpecTypeCode().getParameterId());
			ParameterResDTO resSpec = parameterSO
					.retrieveParameterById(reqDTOSpec);
			System.out.println("===resSpec===="
					+ resSpec.getPmParameter().getParameterId());
			IStatusSO statussO = BusinessDelegate.getInstance()
					.getServiceObject("pm", IStatusSO.class);

			StatusReqDTO statusreq = new StatusReqDTO();
			statusreq.setStatusId(this.getManagedBean().getScqtDoc()
					.getTplSpecMaster().getStatus().getStatusId());
			StatusResDTO statusres = statussO
					.retrieveStatusByStatusId(statusreq);

			IOrgProfileSO ProfileSo = BusinessDelegate.getInstance()
					.getServiceObject("pm", IOrgProfileSO.class);

			OrgProfileReqDTO profileReq = new OrgProfileReqDTO();
			profileReq.setOrgProfileId(1L);
			OrgProfileResDTO profileRes = ProfileSo
					.retrieveOrgProfileByProfileId(profileReq);

			TplSpecMaster specMaster = new TplSpecMaster();
			specMaster.setProcurementCategory(resprocure.getPmParameter());

			specMaster.setProcessType("SC");

			specMaster.setTemplateTypeCode(resTemp.getPmParameter());

			specMaster.setOrgProfile(profileRes.getPmOrgProfile());

			specMaster.setItemTypeCode(resItem.getPmParameter());

			specMaster.setSpecTypeCode(resSpec.getPmParameter());

			specMaster.setSpecName(this.getManagedBean().getScqtDoc()
					.getTplSpecMaster().getSpecName());
			specMaster.setSpecDesc(this.getManagedBean().getScqtDoc()
					.getTplSpecMaster().getSpecDesc());
			specMaster.setSpecTotal(this.getManagedBean().getScqtDoc()
					.getTplSpecMaster().getSpecTotal());
			specMaster.setEstCostReq(this.getManagedBean().getScqtDoc()
					.getTplSpecMaster().isEstCostReq());

			specMaster.setNoOfYear(1);
			specMaster.setTemplateStatus("D");
			specMaster.setStatus(statusres.getPmStatus());
			specMaster.setTierPrice(this.getManagedBean().getScqtDoc()
					.getTplSpecMaster().isTierPrice());
			specMaster.setScQtDocs(null);
			specMaster.setChangedBy(2L);
			specMaster.setCreatedBy(2L);
			specMaster.setBrandNew(false);

			java.util.Date date = new java.util.Date(); // Right now
			java.sql.Timestamp timestamp = new java.sql.Timestamp(
					date.getTime());
			specMaster.setCreatedDate(timestamp);
			TemplateSpecBackingBean specBB = new TemplateSpecBackingBean();
			// System.out.println("====before Question List size===="+this.getManagedBean().getSpecMaster().getTplSpecQuestions().size());

			List<TplSpecQuestion> questions = specBB.getManagedBean()
					.getSpecMaster().getTplSpecQuestions();
			;
			TplSpecQuestion queston = questions.remove(0);
			List<TplSpecQuestion> questions1 = new ArrayList<TplSpecQuestion>();

			questions1.addAll(questions);
			List<TplSpecQuestion> questions2 = new ArrayList<TplSpecQuestion>();
			for (TplSpecQuestion tpl : questions1) {
				System.out.println("==Question Number===="
						+ tpl.getQuestionNumber());
				System.out.println("====before question Id==="
						+ tpl.getSpecQuestionId());
				tpl.setSpecQuestionId(null);
				System.out.println("====after question Id==="
						+ tpl.getSpecQuestionId());
				// TplSpecQuestion cloned = (TplSpecQuestion) tpl.cloneEntity();
				/*
				 * System.out.println("====cloned question Id1==="+cloned.
				 * getSpecQuestionId());
				 * System.out.println("cloned object===="+cloned
				 * .getQuestionNumber());
				 */
				// TplSpecQuestion copy = deepCopy(tpl);
				// two.add(copy);
				TplSpecQuestion tpl2 = new TplSpecQuestion();
				TplSpecQuestion tpl1 = (TplSpecQuestion) BeanUtils
						.cloneBean(tpl);
				tpl1.setTplSpecMaster(specMaster);
				tpl1.setTplSpecAnswers(null);
				tpl1.setTplSpecScorings(null);
				tpl1.setTplSpecTierPriceQuestions(null);
				System.out.println("===after copy data==="
						+ tpl1.getQuestionNumber());
				questions2.add(tpl1);
				// BeanUtils.copyProperties(tpl2, tpl);
				// questions2.add(tpl2);

			}

			specMaster.setTplSpecQuestions(questions2);
			// specMaster.setTplSpecQuestions(questions1);
			// specMaster.setTplSpecQuestions(null);
			QTTemplateDocReqDTO qtTemplateReqDTO = new QTTemplateDocReqDTO();
			QTTemplateDocResDTO resDTO = new QTTemplateDocResDTO();
			IQTDetailSO QtDetailsSO = BusinessDelegate.getInstance()
					.getServiceObject("ss", IQTDetailSO.class);
			System.out.println("manga Details Object" + QtDetailsSO);
			QuationTenderReqDTO tenderReqDTO = new QuationTenderReqDTO();

			tenderReqDTO.setQtTndrNo(this.getManagedBean().getScqtDoc()
					.getScQt().getQtNo());

			QuationTenderResDTO tenderResDTO = new QuationTenderResDTO();

			tenderResDTO = QtDetailsSO.retriveQtByQtNumber(tenderReqDTO);

			ScQtDoc scQtDOc = new ScQtDoc();

			System.out.println("====tenderResDTO.getQtDetail()."
					+ tenderResDTO.getQtDetail().getQtNo());
			scQtDOc.setScQt(tenderResDTO.getQtDetail());
			// scQtDOc.setTplDocMaster(null);

			// scQtDOc.setTplSpecMaster(specBB.getManagedBean()
			// .getSpecMaster());

			scQtDOc.setScQtZoneCriteria(this.getManagedBean().getScqtDoc()
					.getScQtZoneCriteria());
			scQtDOc.setSupportDocReq(this.getManagedBean().getScqtDoc()
					.isSupportDocReq());
			// TemplateSpecBackingBean specBB = new TemplateSpecBackingBean();
			scQtDOc.setChangedBy(2L);
			scQtDOc.setCreatedBy(2L);
			/*
			 * java.util.Date date = new java.util.Date(); // Right now
			 * java.sql.Timestamp timestamp = new java.sql.Timestamp(
			 * date.getTime());
			 */
			scQtDOc.setCreatedDate(timestamp);
			// scQtDOc.setChangedDate(timestamp);
			/*
			 * specBB.getManagedBean().getSpecMaster().getTplSpecQuestions()
			 * .remove(0);
			 */

			qtTemplateReqDTO.setSpecMaster(specMaster);
			qtTemplateReqDTO.setQtDoc(scQtDOc);
			try {
				IQTTemplateSO qtTemplateSO = EpEjbBusinessDelegate
						.getInstance().getServiceObject(QTConstants.SS_MODULE,
								IQTTemplateSO.class);
				System.out.println("=======before insert method====");
				resDTO = qtTemplateSO.saveORUpdateTemplateDoc(qtTemplateReqDTO);
				System.out
						.println("=====success fully saves data in scqtdoc table");
			} catch (Exception e) {
				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
			// throw new IllegalArgumentException();
		}

	}

	public void submit() {
		System.out.println("=====enter submit method====");
	}

	public List<ScQtDoc> searchScqtDocAction() {

		QTTemplateDocReqDTO qtTemplateReqDTO = new QTTemplateDocReqDTO();
		ISearchBean<ScQtDoc> searchBean = new SearchBean<ScQtDoc>();
		QTTemplateDocResDTO resDto = null;

		if (getManagedBean().getScqtDoc().getTplSpecMaster().getSpecName() != null
				&& !"".equalsIgnoreCase(getManagedBean().getScqtDoc()
						.getTplSpecMaster().getSpecName())) {
			System.out.println("yes"
					+ getManagedBean().getScqtDoc().getTplSpecMaster()
							.getSpecName());
			SearchFilterBean committeSearchBean = new SearchFilterBean(
					SearchOperator.OPERATOR_EQUALS.value());
			committeSearchBean.setSearchField("tplSpecMaster.specName");
			committeSearchBean.setSearchValue(getManagedBean().getScqtDoc()
					.getTplSpecMaster().getSpecName());
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
		if (getManagedBean().getScqtDoc().getScQt().getQtNo() != null
				&& !"".equalsIgnoreCase(getManagedBean().getScqtDoc().getScQt()
						.getQtNo())) {
			SearchFilterBean qtnoSearchBean = new SearchFilterBean(
					SearchOperator.OPERATOR_EQUALS.value());
			qtnoSearchBean.setSearchField("scQt.qtNo");
			qtnoSearchBean.setSearchValue(getManagedBean().getScqtDoc()
					.getScQt().getQtNo());

			searchBean.getDataFilterBeans().add(qtnoSearchBean);
		}

		qtTemplateReqDTO.setDocSearchBean(searchBean);

		try {
			IQTTemplateSO qtTemplateSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(QTConstants.SS_MODULE,
							IQTTemplateSO.class);

			resDto = qtTemplateSO.fetchTemplates(qtTemplateReqDTO);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		this.getManagedBean().setDocSearchlist(
				resDto.getSearchResultBean().getResultList());
		return resDto.getSearchResultBean().getResultList();

	}

	public List<ScQtDoc> searchCreateListAction() {

		QTTemplateDocReqDTO qtTemplateReqDTO = new QTTemplateDocReqDTO();
		ISearchBean<ScQtDoc> searchBean = new SearchBean<ScQtDoc>();
		QTTemplateDocResDTO resDto = null;

		if (getManagedBean().getScqtDoc().getTplSpecMaster().getSpecName() != null
				&& !"".equalsIgnoreCase(getManagedBean().getScqtDoc()
						.getTplSpecMaster().getSpecName())) {
			SearchFilterBean committeSearchBean = new SearchFilterBean(
					SearchOperator.OPERATOR_EQUALS.value());
			committeSearchBean.setSearchField("tplSpecMaster.specName");
			committeSearchBean.setSearchValue(getManagedBean().getScqtDoc()
					.getTplSpecMaster().getSpecName());
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
		if (getManagedBean().getScqtDoc().getScQt().getQtNo() != null
				&& !"".equalsIgnoreCase(getManagedBean().getScqtDoc().getScQt()
						.getQtNo())) {
			SearchFilterBean qtnoSearchBean = new SearchFilterBean(
					SearchOperator.OPERATOR_EQUALS.value());
			qtnoSearchBean.setSearchField("scQt.qtNo");
			qtnoSearchBean.setSearchValue(getManagedBean().getScqtDoc()
					.getScQt().getQtNo());

			searchBean.getDataFilterBeans().add(qtnoSearchBean);
		}

		qtTemplateReqDTO.setDocSearchBean(searchBean);

		try {
			IQTTemplateSO qtTemplateSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(QTConstants.SS_MODULE,
							IQTTemplateSO.class);

			resDto = qtTemplateSO.fetchTemplates(qtTemplateReqDTO);
			System.out.println("========scQTDOC List size===="
					+ resDto.getSearchResultBean().getResultList().size());
			for (ScQtDoc sc : resDto.getSearchResultBean().getResultList()) {
				System.out.println("--- IN LOOP --" + sc.getQtDocId()
						+ " ---- " + sc.getTplSpecMaster().getSpecName());
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		this.getManagedBean().setDocSearchlist(
				resDto.getSearchResultBean().getResultList());
		return resDto.getSearchResultBean().getResultList();

	}

}

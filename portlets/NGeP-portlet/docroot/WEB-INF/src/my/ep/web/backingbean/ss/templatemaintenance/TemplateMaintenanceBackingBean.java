/**
 * 
 */
package my.ep.web.backingbean.ss.templatemaintenance;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.event.AjaxBehaviorEvent;

import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.common.codecategory.ICodeCategorySO;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.ss.quationtender.IQTDetailSO;
import my.ep.biz.service.tpl.spec.ITemplateSpecSO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
import my.ep.dto.pm.common.codecategory.CodeCategoryReqDTO;
import my.ep.dto.pm.common.codecategory.CodeCategoryResDTO;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.dto.ss.quationtender.QuationTenderReqDTO;
import my.ep.dto.ss.quationtender.QuationTenderResDTO;
import my.ep.dto.ss.specification.SpecificationReqDTO;
import my.ep.dto.ss.specification.SpecificationResDTO;
import my.ep.dto.tpl.spec.TemplateSpecReqDTO;
import my.ep.dto.tpl.spec.TemplateSpecResDTO;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.common.PmCode;
import my.ep.persistence.entity.ss.quationtender.ScQt;
import my.ep.persistence.entity.ss.quationtender.ScQtCheckList;
import my.ep.persistence.entity.ss.quationtender.ScQtDoc;
import my.ep.persistence.entity.tpl.doc.TplDocMaster;
import my.ep.persistence.entity.tpl.spec.TplSpecMaster;
import my.ep.persistence.entity.tpl.spec.TplSpecQuestion;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.tpl.spec.TemplateSpecBackingBean;
import my.ep.web.managedbean.ss.templatemaintenance.TemplateMaintenanceManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;

/**
 * @author ACER
 * 
 */
@ManagedBean(name = "TemplateMBB")
@RequestScoped
public class TemplateMaintenanceBackingBean extends
		BaseBackingBean<TemplateMaintenanceManagedBean> {

	public TemplateMaintenanceBackingBean() {

		IQTDetailSO QtDetailsSO = BusinessDelegate.getInstance()
				.getServiceObject("ss", IQTDetailSO.class);
		System.out.println("manga template construcoor" + QtDetailsSO);
		QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
		// ScQt scQt=new
		reqDTO.setQtDetailId(176L);
		QuationTenderResDTO resDTO = QtDetailsSO.retriveQTDetails(reqDTO);
		// System.out.println("=======scQt Object value===="+resDTO.getQtDetail());

		// init();
	}
	
	public String editAction() {
		System.out.println("=====enter edit action====");
		TemplateSpecBackingBean specBB = new TemplateSpecBackingBean();
		TplSpecMaster specMaster = new TplSpecMaster();
		specMaster.setSpecMasterId(2L);
		
		specBB.getManagedBean().setSpecMaster(specMaster);
		specBB.retrieveTemplateSpecMaster();
		return"templateMaintainence.xhtml?faces-redirect=true";
	}

	@PostConstruct
	public void init() {
		try {

			System.out.println("===template post construct====");
			if (getManagedBean().getScqtDoc() == null) {
				getManagedBean().setScqtDoc(new ScQtDoc());
				getManagedBean().getScqtDoc().setScQt(new ScQt());
				getManagedBean().getScqtDoc().setTplDocMaster(
						new TplDocMaster());
				getManagedBean().getScqtDoc().setTplSpecMaster(
						new TplSpecMaster());
				getManagedBean().getScqtDoc().getTplSpecMaster()
						.setProcurementCategory(new PmCode());
				getManagedBean().getScqtDoc().getTplSpecMaster()
						.setItemTypeCode(new PmCode());
				getManagedBean().getScqtDoc().getTplSpecMaster()
						.setSpecTypeCode(new PmCode());
				getManagedBean().getScqtDoc().getTplSpecMaster()
						.setOrgProfile(new PmOrgProfile());
				getManagedBean().getScqtDoc().getTplSpecMaster()
						.setTemplateTypeCode(new PmCode());
				System.out.println("===template post construct end====");
			}

			if (getManagedBean().getScqtDoc().getTplSpecMaster() == null) {
				getManagedBean().setScqtDoc(new ScQtDoc());
				getManagedBean().getScqtDoc().setScQt(new ScQt());
				getManagedBean().getScqtDoc().setTplDocMaster(
						new TplDocMaster());
				getManagedBean().getScqtDoc().setTplSpecMaster(
						new TplSpecMaster());
				getManagedBean().getScqtDoc().getTplSpecMaster()
						.setProcurementCategory(new PmCode());
				getManagedBean().getScqtDoc().getTplSpecMaster()
						.setItemTypeCode(new PmCode());
				getManagedBean().getScqtDoc().getTplSpecMaster()
						.setSpecTypeCode(new PmCode());
				getManagedBean().getScqtDoc().getTplSpecMaster()
						.setOrgProfile(new PmOrgProfile());
				getManagedBean().getScqtDoc().getTplSpecMaster()
						.setTemplateTypeCode(new PmCode());
				System.out.println("===template post construct end====");
			}

			if (getManagedBean().getScqtDoc().getTplSpecMaster()
					.getProcurementCategory() == null) {
				getManagedBean().setScqtDoc(new ScQtDoc());
				getManagedBean().getScqtDoc().setScQt(new ScQt());
				getManagedBean().getScqtDoc().setTplDocMaster(
						new TplDocMaster());
				getManagedBean().getScqtDoc().setTplSpecMaster(
						new TplSpecMaster());
				getManagedBean().getScqtDoc().getTplSpecMaster()
						.setProcurementCategory(new PmCode());
				getManagedBean().getScqtDoc().getTplSpecMaster()
						.setItemTypeCode(new PmCode());
				getManagedBean().getScqtDoc().getTplSpecMaster()
						.setSpecTypeCode(new PmCode());
				getManagedBean().getScqtDoc().getTplSpecMaster()
						.setOrgProfile(new PmOrgProfile());
				getManagedBean().getScqtDoc().getTplSpecMaster()
						.setTemplateTypeCode(new PmCode());
				System.out.println("===template post construct end====");
			}
			if (getManagedBean().getScqtDoc().getTplSpecMaster()
					.getItemTypeCode() == null) {
				getManagedBean().setScqtDoc(new ScQtDoc());
				getManagedBean().getScqtDoc().setScQt(new ScQt());
				getManagedBean().getScqtDoc().setTplDocMaster(
						new TplDocMaster());
				getManagedBean().getScqtDoc().setTplSpecMaster(
						new TplSpecMaster());
				getManagedBean().getScqtDoc().getTplSpecMaster()
						.setProcurementCategory(new PmCode());
				getManagedBean().getScqtDoc().getTplSpecMaster()
						.setItemTypeCode(new PmCode());
				getManagedBean().getScqtDoc().getTplSpecMaster()
						.setSpecTypeCode(new PmCode());
				getManagedBean().getScqtDoc().getTplSpecMaster()
						.setOrgProfile(new PmOrgProfile());
				getManagedBean().getScqtDoc().getTplSpecMaster()
						.setTemplateTypeCode(new PmCode());
				System.out.println("===template post construct end====");
			}
			if (getManagedBean().getScqtDoc().getTplSpecMaster()
					.getSpecTypeCode() == null) {
				getManagedBean().setScqtDoc(new ScQtDoc());
				getManagedBean().getScqtDoc().setScQt(new ScQt());
				getManagedBean().getScqtDoc().setTplDocMaster(
						new TplDocMaster());
				getManagedBean().getScqtDoc().setTplSpecMaster(
						new TplSpecMaster());
				getManagedBean().getScqtDoc().getTplSpecMaster()
						.setProcurementCategory(new PmCode());
				getManagedBean().getScqtDoc().getTplSpecMaster()
						.setItemTypeCode(new PmCode());
				getManagedBean().getScqtDoc().getTplSpecMaster()
						.setSpecTypeCode(new PmCode());
				getManagedBean().getScqtDoc().getTplSpecMaster()
						.setOrgProfile(new PmOrgProfile());
				getManagedBean().getScqtDoc().getTplSpecMaster()
						.setTemplateTypeCode(new PmCode());
				System.out.println("===template post construct end====");
			}
			if (getManagedBean().getScqtDoc().getTplSpecMaster()
					.getOrgProfile() == null) {
				getManagedBean().setScqtDoc(new ScQtDoc());
				getManagedBean().getScqtDoc().setScQt(new ScQt());
				getManagedBean().getScqtDoc().setTplDocMaster(
						new TplDocMaster());
				getManagedBean().getScqtDoc().setTplSpecMaster(
						new TplSpecMaster());
				getManagedBean().getScqtDoc().getTplSpecMaster()
						.setProcurementCategory(new PmCode());
				getManagedBean().getScqtDoc().getTplSpecMaster()
						.setItemTypeCode(new PmCode());
				getManagedBean().getScqtDoc().getTplSpecMaster()
						.setSpecTypeCode(new PmCode());
				getManagedBean().getScqtDoc().getTplSpecMaster()
						.setOrgProfile(new PmOrgProfile());
				getManagedBean().getScqtDoc().getTplSpecMaster()
						.setTemplateTypeCode(new PmCode());
				System.out.println("===template post construct end====");
			}
			if (getManagedBean().getScqtDoc().getTplSpecMaster()
					.getTemplateTypeCode() == null) {
				getManagedBean().setScqtDoc(new ScQtDoc());
				getManagedBean().getScqtDoc().setScQt(new ScQt());
				getManagedBean().getScqtDoc().setTplDocMaster(
						new TplDocMaster());
				getManagedBean().getScqtDoc().setTplSpecMaster(
						new TplSpecMaster());
				getManagedBean().getScqtDoc().getTplSpecMaster()
						.setProcurementCategory(new PmCode());
				getManagedBean().getScqtDoc().getTplSpecMaster()
						.setItemTypeCode(new PmCode());
				getManagedBean().getScqtDoc().getTplSpecMaster()
						.setSpecTypeCode(new PmCode());
				getManagedBean().getScqtDoc().getTplSpecMaster()
						.setOrgProfile(new PmOrgProfile());
				getManagedBean().getScqtDoc().getTplSpecMaster()
						.setTemplateTypeCode(new PmCode());
				System.out.println("===template post construct end====");
			}
			this.getManagedBean().getScqtDoc().getTplSpecMaster()
					.getProcurementCategory().setCodeId(1L);
			this.getManagedBean().getScqtDoc().getTplSpecMaster()
					.getItemTypeCode().setCodeId(2L);
		} catch (Exception e) {
			throw new IllegalArgumentException();
		}

	}

	public void checkSupportingDoc(AjaxBehaviorEvent e) {
		// doing some stuff here

		// String dueDate = (String) ((UIOutput)e.getSource()).getValue();
		String supportingDoc = (String) e.getComponent().getAttributes()
				.get("value");
		System.out.println("=======get supportingDoc value====="
				+ supportingDoc);
		this.getManagedBean().setSupportingDoc(supportingDoc);

	}

	public void supportingDocAdd() {
		System.out.println("====enter supportdoc add method====");
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
		try {
			System.out.println("=======enter save method====");
			System.out.println("======atnum==="
					+ this.getManagedBean().getScqtDoc().getScQt().getQtNo());
			System.out.println("======procurement id==="
					+ this.getManagedBean().getScqtDoc().getTplSpecMaster()
							.getProcurementCategory().getCodeId());
			System.out.println("======item type id==="
					+ this.getManagedBean().getScqtDoc().getTplSpecMaster()
							.getItemTypeCode().getCodeId());
			System.out.println("====Ministry Name==="
					+ this.getManagedBean().getMinistryName());
			System.out.println("====template tyoe==="
					+ this.getManagedBean().getTemplateType());
			System.out.println("====spec tyoe==="
					+ this.getManagedBean().getSpecificationType());
			System.out.println("====Zone==="
					+ this.getManagedBean().getScqtDoc().getQtZoneCriteriaId());
			System.out.println("====Title==="
					+ this.getManagedBean().getScqtDoc().getTplSpecMaster()
							.getSpecName());
			System.out.println("====Desc==="
					+ this.getManagedBean().getScqtDoc().getTplSpecMaster()
							.getSpecDesc());
			System.out.println("====Total==="
					+ this.getManagedBean().getScqtDoc().getTplSpecMaster()
							.getSpecTotal());
			System.out.println("====estCose==="
					+ this.getManagedBean().getScqtDoc().getTplSpecMaster()
							.isEstCostReq());
			System.out.println("====tier price==="
					+ this.getManagedBean().getScqtDoc().getTplSpecMaster()
							.isTierPrice());
			/*
			 * List<ScQtCheckList>
			 * checklist=this.getManagedBean().getSupportDocList();
			 * System.out.println
			 * ("=======check list size===="+checklist.size());
			 */
			ICodeCategorySO codeCategorySO = BusinessDelegate.getInstance().getServiceObject(
					"pm", ICodeCategorySO.class);
			System.out.println("manga template construcoor" + codeCategorySO);
			CodeCategoryReqDTO reqDTO = new CodeCategoryReqDTO();
			reqDTO.setCodeId(2L);
			CodeCategoryResDTO resprocure = codeCategorySO.retrieveCodeById(reqDTO);
			System.out.println("=====resource procure id==="
					+ resprocure.getPmCode().getCodeId());

			CodeCategoryReqDTO reqDTOItem = new CodeCategoryReqDTO();
			reqDTOItem.setCodeId(2L);
			CodeCategoryResDTO resItem = codeCategorySO.retrieveCodeById(reqDTOItem);
			System.out.println("=====resource item id==="
					+ resItem.getPmCode().getCodeId());

			CodeCategoryReqDTO reqDTOTemp = new CodeCategoryReqDTO();
			reqDTOTemp.setCodeId(Long.valueOf(this.getManagedBean()
					.getTemplateType()));
			CodeCategoryResDTO resTemp = codeCategorySO.retrieveCodeById(reqDTOTemp);
			System.out.println("=====resource template id==="
					+ resTemp.getPmCode().getCodeId());

			CodeCategoryReqDTO reqDTOSpec = new CodeCategoryReqDTO();
			reqDTOSpec.setCodeId(Long.valueOf(this.getManagedBean()
					.getSpecificationType()));
			CodeCategoryResDTO resSpec = codeCategorySO.retrieveCodeById(reqDTOSpec);
			System.out.println("=====resource Spec id==="
					+ resSpec.getPmCode().getCodeId());

			IStatusSO statussO = BusinessDelegate.getInstance()
					.getServiceObject("pm", IStatusSO.class);
			System.out.println("====status Object====" + statussO);
			StatusReqDTO statusreq = new StatusReqDTO();
			statusreq.setStatusId(1L);
			StatusResDTO statusres = statussO
					.retrieveStatusByStatusId(statusreq);
			System.out.println("=====resource Status id==="
					+ statusres.getPmStatus().getStatusId());

			IOrgProfileSO ProfileSo = BusinessDelegate.getInstance()
					.getServiceObject("pm", IOrgProfileSO.class);
			System.out.println("====ProfileSo Object====" + ProfileSo);
			OrgProfileReqDTO profileReq = new OrgProfileReqDTO();
			profileReq.setOrgProfileId(1L);
			OrgProfileResDTO profileRes = ProfileSo
					.retrieveOrgProfileByProfileId(profileReq);
			System.out.println("=====resource profileRes id==="
					+ profileRes.getPmOrgProfile().getOrgProfileId());
			ITemplateSpecSO templateSo = BusinessDelegate.getInstance()
					.getServiceObject("cc", ITemplateSpecSO.class);
			System.out.println("===template so object===" + templateSo);
			TemplateSpecReqDTO templateSpecReq = new TemplateSpecReqDTO();
			TplSpecMaster specMaster = new TplSpecMaster();
			specMaster.setProcurementCategory(resprocure.getPmCode());
			specMaster.setProcessType("SC");
			System.out.println("=======processtype====");
			specMaster.setTemplateTypeCode(resTemp.getPmCode());
			specMaster.setOrgProfile(profileRes.getPmOrgProfile());
			specMaster.setItemTypeCode(resItem.getPmCode());
			specMaster.setSpecTypeCode(resSpec.getPmCode());
			specMaster.setSpecName(this.getManagedBean().getScqtDoc()
					.getTplSpecMaster().getSpecName());
			specMaster.setSpecDesc(this.getManagedBean().getScqtDoc()
					.getTplSpecMaster().getSpecDesc());
			specMaster.setSpecTotal(this.getManagedBean().getScqtDoc()
					.getTplSpecMaster().getSpecTotal());
			specMaster.setEstCostReq(this.getManagedBean().getScqtDoc()
					.getTplSpecMaster().isEstCostReq());
			System.out.println("=====middle of insert===");
			specMaster.setNoOfYear(1);
			specMaster.setTemplateStatus("D");
			specMaster.setStatus(statusres.getPmStatus());
			specMaster.setTierPrice(this.getManagedBean().getScqtDoc()
					.getTplSpecMaster().isTierPrice());
			templateSpecReq.setSpecMaster(specMaster);
			TemplateSpecResDTO templateSpecRes = new TemplateSpecResDTO();
			// templateSpecRes=templateSo.saveTemplateSpec(templateSpecReq);
			// System.out.println("=========templateSpecRes========="+templateSpecRes.getSpecMaster().getSpecMasterId());

			System.out.println("=====successfully template saved in db====");

			IQTDetailSO QtDetailsSO = BusinessDelegate.getInstance()
					.getServiceObject("ss", IQTDetailSO.class);
			System.out.println("manga Details Object" + QtDetailsSO);
			QuationTenderReqDTO tenderReqDTO = new QuationTenderReqDTO();
			// ScQt scQt=new
			tenderReqDTO.setQtTndrNo(this.getManagedBean().getScqtDoc()
					.getScQt().getQtNo());
			System.out.println("=====quotationTndrSetValue======");
			QuationTenderResDTO tenderResDTO = QtDetailsSO
					.retriveQtByQtNumber(tenderReqDTO);
			System.out.println("========tenderResDTO====="
					+ tenderResDTO.getQtDetail().getQtId());
			/*
			 * ISpecificationSO SpecificationSO = BusinessDelegate.getInstance()
			 * .getServiceObject("ss", ISpecificationSO.class);
			 * System.out.println("manga Details Object"+QtDetailsSO);
			 */
			SpecificationReqDTO specificationReqDTO = new SpecificationReqDTO();
			ScQtDoc scQtDOc = new ScQtDoc();
			scQtDOc.setScQt(tenderResDTO.getQtDetail());
			scQtDOc.setTplDocMaster(null);
			scQtDOc.setTplSpecMaster(templateSpecRes.getSpecMaster());
			scQtDOc.setQtZoneCriteriaId(this.getManagedBean().getScqtDoc()
					.getQtZoneCriteriaId());
			scQtDOc.setSupportDocReq(this.getManagedBean().getScqtDoc()
					.getSupportDocReq());
			specificationReqDTO.setScQtDoc(scQtDOc);
			SpecificationResDTO specificationResDTO = new SpecificationResDTO();
			// specificationResDTO=SpecificationSO.saveOrUpdateScQtDoc(specificationReqDTO);
			// System.out.println("==========save or Update Scqt DocId====="+specificationResDTO.getScQtDoc().getQtDocId());
			/*
			 * ISpecificationSO CheckListSO = BusinessDelegate.getInstance()
			 * .getServiceObject("ss", ISpecificationSO.class);
			 */
			SpecificationReqDTO checkListReqDTO = new SpecificationReqDTO();
			List<ScQtCheckList> checklist = this.getManagedBean()
					.getSupportDocList();
			System.out.println("=======check list size====" + checklist.size());
			for (int i = 0; i < checklist.size(); i++) {
				ScQtCheckList scQtCheckList = checklist.get(i);
				ScQtCheckList checkList = new ScQtCheckList();
				checkList.setTitle(scQtCheckList.getTitle());
				checkList.setDocType("5");
				checkList.setEvaluationStage("2");
				checkList.setChecklistType("UP");
				checkList.setChecklistSeq(new BigDecimal(1));
				checkList.setScQt(tenderResDTO.getQtDetail());
				ScQtDoc ScQtDoc = specificationResDTO.getScQtDoc();
				Long qtDocId = ScQtDoc.getQtDocId();
				ScQt qt = new ScQt();
				qt.setQtId(qtDocId);
				checkList.setQtDocId(qt);
				checkList.setPmStatus(statusres.getPmStatus());
				System.out.println("========scQtCheckList Title====="
						+ scQtCheckList.getTitle());
				checkListReqDTO.setScQtCheckList(checkList);
				SpecificationResDTO checkListResDTO = new SpecificationResDTO();
				// checkListResDTO=
				// CheckListSO.saveOrUpdateScQtCheckList(checkListReqDTO);
				System.out
						.println("=====successfully saved scqt checkList====");
				
			
			}
			System.out.println("====tpl spec question ====");
			TemplateSpecBackingBean specBB = new TemplateSpecBackingBean();
			specBB.getManagedBean().getSpecMaster().getTplSpecQuestions();
			System.out.println("========specBB.getManagedBean().getSpecMaster().getTplSpecQuestions();===="+specBB.getManagedBean().getSpecMaster().getTplSpecQuestions().size());
			List<TplSpecQuestion> spec=specBB.getManagedBean().getSpecMaster().getTplSpecQuestions();
			for(int j=0;j<spec.size();j++){
				TplSpecQuestion tpl=spec.get(j);
				System.out.println("======Item======"+tpl.getClItem());
			}
		
		} catch (Exception e) {
			throw new IllegalArgumentException();
		}

	}
	
	
	public void submit(){
		System.out.println("=====enter submit method====");
	}

}

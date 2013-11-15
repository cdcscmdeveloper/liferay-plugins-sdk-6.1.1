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
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.ss.quationtender.ScQt;
import my.ep.persistence.entity.ss.quationtender.ScQtCheckList;
import my.ep.persistence.entity.ss.quationtender.ScQtDoc;
import my.ep.persistence.entity.tpl.spec.TplSpecMaster;
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

		
	}
	
	public String editAction() {
		try{
		System.out.println("=====enter edit action====");
		TemplateSpecBackingBean specBB = new TemplateSpecBackingBean();
		TplSpecMaster specMaster = new TplSpecMaster();
		specMaster.setSpecMasterId(2L);
		
		specBB.getManagedBean().setSpecMaster(specMaster);
		specBB.retrieveTemplateSpecMaster();
		}catch(Exception e ){
			e.printStackTrace();
		}
		return"newTemplateMaintenance.xhtml?faces-redirect=true";
	}

	@PostConstruct
	public void init() {
		try {

			
			if (getManagedBean().getScqtDoc() == null) {
				getManagedBean().setScqtDoc(new ScQtDoc());
			
				
			}
             if(getManagedBean().getScqtDoc().getScQt()==null){
            		getManagedBean().getScqtDoc().setScQt(new ScQt());
             }
			if (getManagedBean().getScqtDoc().getTplSpecMaster() == null) {
			
				getManagedBean().getScqtDoc().setTplSpecMaster(
						new TplSpecMaster());
				
			}

			if (getManagedBean().getScqtDoc().getTplSpecMaster()
					.getProcurementCategory() == null) {
			
				getManagedBean().getScqtDoc().getTplSpecMaster()
						.setProcurementCategory(new PmCode());
			
			}
			if (getManagedBean().getScqtDoc().getTplSpecMaster()
					.getItemTypeCode() == null) {
			
				getManagedBean().getScqtDoc().getTplSpecMaster()
						.setItemTypeCode(new PmCode());
		
			}
			if (getManagedBean().getScqtDoc().getTplSpecMaster()
					.getSpecTypeCode() == null) {
			
				getManagedBean().getScqtDoc().getTplSpecMaster()
						.setSpecTypeCode(new PmCode());
				
			}
			if (getManagedBean().getScqtDoc().getTplSpecMaster()
					.getOrgProfile() == null) {
			
				getManagedBean().getScqtDoc().getTplSpecMaster()
						.setOrgProfile(new PmOrgProfile());
			
			}
			if (getManagedBean().getScqtDoc().getTplSpecMaster()
					.getTemplateTypeCode() == null) {
			
				getManagedBean().getScqtDoc().getTplSpecMaster()
						.setTemplateTypeCode(new PmCode());
			
			}
			
			if (getManagedBean().getScqtDoc().getTplSpecMaster()
					.getStatus() == null) {
		
				getManagedBean().getScqtDoc().getTplSpecMaster().setStatus(new PmStatus());
				
			}
			this.getManagedBean().getScqtDoc().getTplSpecMaster()
					.getProcurementCategory().setCodeId(1L);
			this.getManagedBean().getScqtDoc().getTplSpecMaster()
					.getItemTypeCode().setCodeId(2L);
			this.getManagedBean().getScqtDoc().getTplSpecMaster().getStatus().setStatusId(1L);
		} catch (Exception e) {
			throw new IllegalArgumentException();
		}

	}

	public void checkSupportingDoc(AjaxBehaviorEvent e) {
		
		String supportingDoc = (String) e.getComponent().getAttributes()
				.get("value");
		
		this.getManagedBean().setSupportingDoc(supportingDoc);

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
		try {
			
			ICodeCategorySO codeCategorySO = BusinessDelegate.getInstance().getServiceObject(
					"pm", ICodeCategorySO.class);
			
			CodeCategoryReqDTO reqDTO = new CodeCategoryReqDTO();
			System.out.println("manga template construcoor" + codeCategorySO);
			reqDTO.setCodeId(this.getManagedBean().getScqtDoc().getTplSpecMaster().getProcurementCategory().getCodeId());
			CodeCategoryResDTO resprocure = codeCategorySO.retrieveCodeById(reqDTO);
		
			CodeCategoryReqDTO reqDTOItem = new CodeCategoryReqDTO();
			reqDTOItem.setCodeId(this.getManagedBean().getScqtDoc().getTplSpecMaster().getItemTypeCode().getCodeId());
			CodeCategoryResDTO resItem = codeCategorySO.retrieveCodeById(reqDTOItem);
			
			CodeCategoryReqDTO reqDTOTemp = new CodeCategoryReqDTO();
			reqDTOTemp.setCodeId(1L);
			CodeCategoryResDTO resTemp = codeCategorySO.retrieveCodeById(reqDTOTemp);
			
			CodeCategoryReqDTO reqDTOSpec = new CodeCategoryReqDTO();
			
			reqDTOSpec.setCodeId(1L);
			CodeCategoryResDTO resSpec = codeCategorySO.retrieveCodeById(reqDTOSpec);
			
			IStatusSO statussO = BusinessDelegate.getInstance()
					.getServiceObject("pm", IStatusSO.class);
		
			StatusReqDTO statusreq = new StatusReqDTO();
			statusreq.setStatusId(this.getManagedBean().getScqtDoc().getTplSpecMaster().getStatus().getStatusId());
			StatusResDTO statusres = statussO
					.retrieveStatusByStatusId(statusreq);
			
			IOrgProfileSO ProfileSo = BusinessDelegate.getInstance()
					.getServiceObject("pm", IOrgProfileSO.class);
		
			OrgProfileReqDTO profileReq = new OrgProfileReqDTO();
			profileReq.setOrgProfileId(1L);
			OrgProfileResDTO profileRes = ProfileSo
					.retrieveOrgProfileByProfileId(profileReq);
			
			ITemplateSpecSO templateSo = BusinessDelegate.getInstance()
					.getServiceObject("cc", ITemplateSpecSO.class);
			
			TemplateSpecReqDTO templateSpecReq = new TemplateSpecReqDTO();
			TplSpecMaster specMaster = new TplSpecMaster();
			specMaster.setProcurementCategory(resprocure.getPmCode());
		
			specMaster.setProcessType("SC");
		
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
			
			TemplateSpecBackingBean specBB = new TemplateSpecBackingBean();
			
			specBB.getManagedBean().getSpecMaster().getTplSpecQuestions().remove(0);
			
			templateSpecReq.setSpecMaster(specBB.getManagedBean().getSpecMaster());
			
			//templateSpecReq.setSpecMaster(specMaster);
			TemplateSpecResDTO templateSpecRes = new TemplateSpecResDTO();
			System.out.println("====before insert==");
			System.out.println("MMMMMMMMM   "+templateSo);
			System.out.println("AAAAAAAAAAAAA" +templateSpecReq);
			try{
			 templateSpecRes=templateSo.saveTemplateSpec(templateSpecReq);
			}catch(Exception e){
				e.printStackTrace();
			}
			System.out.println("=====zone id===="+this.getManagedBean().getScqtDoc()
					.getQtZoneCriteriaId());
		 System.out.println("=========templateSpecRes========="+templateSpecRes.getSpecMaster().getSpecMasterId());

			System.out.println("=====successfully template saved in db====");
			try{
			IQTDetailSO QtDetailsSO = BusinessDelegate.getInstance()
					.getServiceObject("ss", IQTDetailSO.class);
			System.out.println("manga Details Object" + QtDetailsSO);
			QuationTenderReqDTO tenderReqDTO = new QuationTenderReqDTO();
			// ScQt scQt=new
			System.out.println("======qtTndrNo====="+this.getManagedBean().getScqtDoc()
					.getScQt().getQtNo());
			tenderReqDTO.setQtTndrNo(this.getManagedBean().getScqtDoc()
					.getScQt().getQtNo());
			System.out.println("=====quotationTndrSetValue======");
			QuationTenderResDTO tenderResDTO=new QuationTenderResDTO();
			
			/*tenderResDTO = QtDetailsSO
					.retriveQtByQtNumber(tenderReqDTO);*/
		/*	System.out.println("========tenderResDTO====="
					+ tenderResDTO.getQtDetail().getQtId());*/
			/* ISpecificationSO SpecificationSO = BusinessDelegate.getInstance()
					  .getServiceObject("ss", ISpecificationSO.class);
					  System.out.println("manga Details Object"+QtDetailsSO);
					*/
					SpecificationReqDTO specificationReqDTO = new SpecificationReqDTO();
					ScQtDoc scQtDOc = new ScQtDoc();
					ScQt scQt=new ScQt();
					
					scQtDOc.setScQt(scQt);
					//scQtDOc.setTplDocMaster(null);
					/*scQtDOc.setTplSpecMaster(templateSpecRes.getSpecMaster());
				
					scQtDOc.setQtZoneCriteriaId(this.getManagedBean().getScqtDoc()
							.getQtZoneCriteriaId());
					scQtDOc.setSupportDocReq(true);
					specificationReqDTO.setScQtDoc(scQtDOc);*/
					List<ScQtDoc> docList=new ArrayList<ScQtDoc>();
					System.out.println("====list===");
					docList.add(scQtDOc);
					
					scQt.setScQtDocs(docList);
					QuationTenderReqDTO tenderReqDTO1 = new QuationTenderReqDTO();
					tenderReqDTO1.setQtdetail(scQt);
					System.out.println("====before save==");
					QtDetailsSO.saveOrUpdateQT(tenderReqDTO1);
					System.out.println("======successfully saved in scqtdoc table===");
					SpecificationResDTO specificationResDTO = new SpecificationResDTO();
					
					// specificationResDTO=SpecificationSO.saveOrUpdateScQtDoc(specificationReqDTO);
					// System.out.println("==========save or Update Scqt DocId====="+specificationRe//sDTO.getScQtDoc().getQtDocId());
			}catch(Exception e){
				e.printStackTrace();
			}
			
			
		
		} catch (Exception e) {
			e.printStackTrace();
			//throw new IllegalArgumentException();
		}

	}
	
	
	public void submit(){
		System.out.println("=====enter submit method====");
	}

}

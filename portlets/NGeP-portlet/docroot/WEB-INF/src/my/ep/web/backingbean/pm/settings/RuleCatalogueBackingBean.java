package my.ep.web.backingbean.pm.settings;

import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;

import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import my.ep.biz.service.cm.codification.ICodificationSO;
import my.ep.biz.service.pm.common.categorycode.ICategoryCodeSO;
import my.ep.biz.service.pm.common.confgeneral.IConfigGeneralSO;
import my.ep.biz.service.pm.general.settings.rule.IRuleSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.cm.codification.CodificationReqDTO;
import my.ep.dto.cm.codification.CodificationResDTO;
import my.ep.dto.pm.common.categorycode.CategoryCodeReqDTO;
import my.ep.dto.pm.common.categorycode.CategoryCodeResDTO;
import my.ep.dto.pm.common.confgeneral.ConfigGeneralReqDTO;
import my.ep.dto.pm.common.confgeneral.ConfigGeneralResDTO;
import my.ep.dto.pm.general.settings.rule.RuleReqDTO;
import my.ep.dto.pm.general.settings.rule.RuleResDTO;

import my.ep.persistence.entity.cm.catalog.CmRequestCode;
import my.ep.persistence.entity.pm.common.PmCategoryL3;
import my.ep.persistence.entity.pm.general.settings.PmConfigRuleValue;
import my.ep.persistence.entity.sc.quationtender.ScQtCategory;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.pm.settings.RuleCatalogueManageBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;
import org.primefaces.component.calendar.Calendar;


@ManagedBean(name = "ruleCatalogueBackingBean")
@ViewScoped
public class RuleCatalogueBackingBean extends
		BaseBackingBean<RuleCatalogueManageBean> {
	private static Logger log = Logger
			.getLogger(RuleCatalogueBackingBean.class);

	public RuleCatalogueBackingBean() {
	
	}
	
	public String preRenderView(){
		log.info("...........preRenderView............");
		IConfigGeneralSO cgeneralSO = null;
		try {
			cgeneralSO = (IConfigGeneralSO) BusinessDelegate
					.getInstance().getServiceObject("pm",
							IConfigGeneralSO.class);
			ConfigGeneralReqDTO reqDTO = new ConfigGeneralReqDTO();
			ConfigGeneralResDTO resDTO =cgeneralSO.getConfigGeneral(reqDTO);
			this.getManagedBean().setSelectedConfigGeneral(resDTO.getPmConfigGeneral());
			IRuleSO ruleSO = null;


				ruleSO = (IRuleSO) BusinessDelegate.getInstance()
						.getServiceObject("pm", IRuleSO.class);

				RuleReqDTO reqRDTO = new RuleReqDTO();
				FacesScopes fs = new FacesScopes();
				reqRDTO.setLanguageCode(fs.getUserLocale().getLanguage());
				System.out.println("......................fs.getUserLocale().getLanguage()="+fs.getUserLocale().getLanguage());
				RuleResDTO resRDTO = ruleSO.getRuleTypes(reqRDTO);

				System.out.println("*********after***********");
				this.getManagedBean().setConfigRuleList(resRDTO.getRuleTypeList());
				this.getManagedBean().setSelectedRuleType(this.getManagedBean().getConfigRuleList().get(0));
				
				FacesContext fc = FacesContext.getCurrentInstance();
				fc.getApplication().getNavigationHandler()
						.handleNavigation(fc, null, "ruleConfig");
//			return "ruleConfig.xhtml";
		}catch(Exception e){
			e.printStackTrace();
		}
		return "";
	}
	
	//postUpdateListener="qTSupplierQulCriteriaBackingBean.restrictedCategoryCodesUpdate(#{index},qTSupplierQulCriteriaBackingBean.managedBean.suppCriteriarestrictedList, qTSupplierQulCriteriaBackingBean.managedBean.suppCriteriarestrictedList[#{index}].pmCategoryL3)"
	public void categoryCodesUpdate(int index, List<PmConfigRuleValue> ruleTypeValueList, PmCategoryL3 categoryL3) {
		log.info("................categoryCodesUpdate...............");
		ICategoryCodeSO categoryCodeSo;
		try {
			categoryCodeSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, ICategoryCodeSO.class);
			CategoryCodeReqDTO codeReqDTO = new CategoryCodeReqDTO();
			codeReqDTO.setCategoryL3Id(categoryL3.getCategoryL3Id());
			CategoryCodeResDTO CategoryCodeResDTO = categoryCodeSo
					.retrieveCategoryL3DtlById(codeReqDTO);
			if (null != CategoryCodeResDTO
					&& null != CategoryCodeResDTO.getCategoryL3()) {
				PmConfigRuleValue ruleValue = ruleTypeValueList.get(index);
				ruleValue.setCategoryL3Id(CategoryCodeResDTO.getCategoryL3());
				String categoryL3Code = CategoryCodeResDTO.getCategoryL3().getCategoryL3Code();
				String categoryL2Code = CategoryCodeResDTO.getCategoryL3().getPmCategoryL2().getCategoryL2Code();
				String categoryL1Code = CategoryCodeResDTO.getCategoryL3().getPmCategoryL2().getPmCategoryL1().getCategoryL1Code();
				log.info("..................CategoryCodeResDTO.getCategoryL3().getCategoryL3Id()="+CategoryCodeResDTO.getCategoryL3().getCategoryL3Id());
				log.info(".............categoryL3Code="+categoryL3Code);
				log.info(".............categoryL2Code="+categoryL2Code);
				log.info(".............categoryL1Code="+categoryL1Code);
				ruleValue.setRuleValue(categoryL3Code+categoryL2Code+categoryL1Code);
				this.getManagedBean().getSelectedRuleType().getConfigRuleValues().remove(index);
				this.getManagedBean().getSelectedRuleType().getConfigRuleValues()
						.add(index, ruleValue);
			}
		} finally {

		}
	}

	public void addMoreRuleValue() {
		log.info(".................addMoreRuleValue...................");
		PmConfigRuleValue newRuleValue= new PmConfigRuleValue();
		newRuleValue.setRuleType(this.getManagedBean().getSelectedRuleType());
		newRuleValue.setEffDate(new Date());
		
		int index = this.getManagedBean().getSelectedRuleType().getConfigRuleValues().size();
        this.getManagedBean().getSelectedRuleType().getConfigRuleValues().add(index,newRuleValue);

	}
	
	public void deleteMoreRuleValue() {
		log.info(".................deleteMoreRuleValue...................");
		
        this.getManagedBean().getSelectedRuleType().getConfigRuleValues().remove(this.getManagedBean().getSelectedRuleValue());

	}
	
	public void saveRuleCatalog(){
		
	}
}

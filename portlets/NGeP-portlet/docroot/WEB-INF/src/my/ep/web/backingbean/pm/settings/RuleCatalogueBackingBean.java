package my.ep.web.backingbean.pm.settings;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.service.cm.codification.ICodificationSO;
import my.ep.biz.service.ct.dtl.IContractItemSO;
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.categorycode.ICategoryCodeSO;
import my.ep.biz.service.pm.common.confgeneral.IConfigGeneralSO;
import my.ep.biz.service.pm.fee.settings.IFeeExemptionSO;
import my.ep.biz.service.pm.general.settings.rule.IRuleSO;
import my.ep.biz.service.pm.general.settings.threshold.IOperationalThresholdSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.RuleTypeConst;
import my.ep.dto.cm.codification.CodificationReqDTO;
import my.ep.dto.cm.codification.CodificationResDTO;
import my.ep.dto.ct.dtl.ContractItemReqDTO;
import my.ep.dto.ct.dtl.ContractItemResDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.common.categorycode.CategoryCodeReqDTO;
import my.ep.dto.pm.common.categorycode.CategoryCodeResDTO;
import my.ep.dto.pm.common.confgeneral.ConfigGeneralReqDTO;
import my.ep.dto.pm.common.confgeneral.ConfigGeneralResDTO;
import my.ep.dto.pm.fee.settings.FeeExemptionReqDTO;
import my.ep.dto.pm.fee.settings.FeeExemptionResDTO;
import my.ep.dto.pm.general.settings.rule.RuleReqDTO;
import my.ep.dto.pm.general.settings.rule.RuleResDTO;
import my.ep.dto.pm.general.settings.threshold.ThresholdReqDTO;
import my.ep.dto.pm.general.settings.threshold.ThresholdResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.cm.catalog.CmItem;
import my.ep.persistence.entity.cm.catalog.CmType;
import my.ep.persistence.entity.cm.catalog.CmUnspsc;
import my.ep.persistence.entity.ct.CtContract;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.common.PmCategoryL3;
import my.ep.persistence.entity.pm.common.PmLanguage;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.fee.settings.PmFeeExemption;
import my.ep.persistence.entity.pm.general.settings.rule.PmConfigRule;
import my.ep.persistence.entity.pm.general.settings.rule.PmConfigRuleValue;
import my.ep.persistence.entity.pm.general.settings.threshold.PmThreshold;
import my.ep.persistence.entity.sc.directpurchase.ScRequestItem;
import my.ep.type.pm.RoleCode;
import my.ep.util.MessageUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.pm.settings.RuleCatalogueManageBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.PmCommonUtil;
import my.ep.web.util.pm.helper.RuleConfigHelper;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

@ManagedBean(name = "ruleCatalogueBackingBean")
@ViewScoped
public class RuleCatalogueBackingBean extends
		BaseBackingBean<RuleCatalogueManageBean> {
	private static Logger log = Logger
			.getLogger(RuleCatalogueBackingBean.class);

	private ISearchBean<PmParameterDesc> parameterSearchBean;

	public ISearchBean<PmParameterDesc> getParameterSearchBean() {
		if (this.parameterSearchBean == null) {
			this.parameterSearchBean = super.createSearchBean(
					PmParameterDesc.class, "parameterId");
		}

		return this.parameterSearchBean;
	}

	public RuleCatalogueBackingBean() {
	

	}

	public boolean isValidDateRange(Date effDate, Date ExpDate) {
		log.info("...............isValidDateRange...............");
		if (ExpDate != null && (ExpDate.getTime() < effDate.getTime())) {
			return false;
		}
		return true;
	}

	public String preRenderView() {
		log.info("...........preRenderView............");
		getManagedBean().setTabActiveIndex(0);
		getManagedBean().setFeeExemptionIndex(0);
		getManagedBean().setThresholdIndex(0);

		IConfigGeneralSO cgeneralSO = null;
		try {
			cgeneralSO = (IConfigGeneralSO) BusinessDelegate.getInstance()
					.getServiceObject("pm", IConfigGeneralSO.class);
			IUserSO userSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
			ConfigGeneralReqDTO reqDTO = new ConfigGeneralReqDTO();
			ConfigGeneralResDTO resDTO = cgeneralSO.getConfigGeneral(reqDTO);
			this.getManagedBean().setSelectedConfigGeneral(
					resDTO.getPmConfigGeneral());
			System.out
					.println("resDTO.getPmConfigGeneral().getFinancialYear()="
							+ resDTO.getPmConfigGeneral().getFinancialYear());
			// this.getManagedBean().setTabActiveIndex(0);

			// /////////////////////////////////////////////////////////
			// /getting general Rule///////////////////////
			// IRuleSO ruleSO = (IRuleSO) BusinessDelegate.getInstance()
			// .getServiceObject("pm", IRuleSO.class);
			// RuleReqDTO reqRDTO = new RuleReqDTO();
			// FacesScopes fs = new FacesScopes();
			// reqRDTO.setLanguageCode(fs.getUserLocale().getLanguage());
			//
			// RuleResDTO resRDTO = ruleSO.getRuleTypes(reqRDTO);

			this.getManagedBean().setConfigRuleList(
					RuleConfigHelper.getInstance().getRuleTypeList());
			this.getManagedBean().setSelectedRuleType(
					RuleConfigHelper.getInstance().getRuleTypeList().get(0));
			// fill up ruleType's ruleValueList
			for (PmConfigRule cRule : this.getManagedBean().getConfigRuleList()) {
				cRule.setConfigRuleValues(RuleConfigHelper.getInstance()
						.getRuleTypeValueListByRuleType(cRule.getRuleType()));
			}
			// fill up category display value
			for (PmConfigRule rule : this.getManagedBean().getConfigRuleList()) {
				if (rule.getRuleDataTypeId().getParameterCode()
						.equalsIgnoreCase("c")) {
					for (PmConfigRuleValue cRule : rule.getConfigRuleValues()) {
						PmCategoryL3 categoryL3 = cRule.getCategoryL3Id();
						if (categoryL3 != null) {
							String categoryL3Code = categoryL3
									.getCategoryL3Code();
							String categoryL2Code = categoryL3
									.getPmCategoryL2().getCategoryL2Code();
							String categoryL1Code = categoryL3
									.getPmCategoryL2().getPmCategoryL1()
									.getCategoryL1Code();
							cRule.setCategoryDisplayValue(categoryL1Code
									+ categoryL2Code + categoryL3Code);
						}
					}
				}
			}
			// ////////////getitng FeeExemption////////////////////////////
			IFeeExemptionSO feeSO = (IFeeExemptionSO) BusinessDelegate
					.getInstance()
					.getServiceObject("pm", IFeeExemptionSO.class);

			FeeExemptionReqDTO reqFDTO = new FeeExemptionReqDTO();
			FeeExemptionResDTO resFDTO = null;

			this.getManagedBean().getFeeExemptionsForOrgAndItem().clear();
			this.getManagedBean().getDeletedOrgAndItemFeeExemptions().clear();
			resFDTO = feeSO.getFeeExemptionsForOrgAndItem(reqFDTO);

			for (PmFeeExemption exemption : resFDTO.getFeeExemptions()) {
				if (exemption.getItemId() == null) {// TODO: clear it before
													// updating the fee list
					exemption.setItemId(new CmItem());
				}
				this.getManagedBean().getFeeExemptionsForOrgAndItem()
						.add(exemption);
			}

			// this.getManagedBean().setFeeExemptionIndex(0);

			this.getManagedBean().getFeeExemptionsForComodity().clear();
			this.getManagedBean().getDeletedComodityFeeExemptions().clear();
			resFDTO = feeSO.getFeeExemptionsForComodity(reqFDTO);

			ICodificationSO unspscSO = (ICodificationSO) BusinessDelegate
					.getInstance()
					.getServiceObject("cm", ICodificationSO.class);
			CodificationReqDTO cReqDTO = new CodificationReqDTO();
			for (PmFeeExemption exemption : resFDTO.getFeeExemptions()) {
				if (exemption.getTypeId() == null) {
					exemption.setTypeId(new CmType());
				}
				cReqDTO.setUnspscCode(exemption.getUnspscId().getUnspscCode());
				CodificationResDTO cResDTO = unspscSO
						.retrieveUNSPSCByCommodityCode(cReqDTO);

				if (cResDTO.isSuccessful()) {

					CmUnspsc commodity = cResDTO.getCommodityObj();

					exemption.setTypes(new ArrayList<CmType>());
					for (CmType type : commodity.getCmType()) {
						if (!StringUtils.isBlank(type.getTypeName())) {
							exemption.getTypes().add(type);
						}
					}
				}
				this.getManagedBean().getFeeExemptionsForComodity()
						.add(exemption);
			}

			// this.getManagedBean().setFeeExemptionIndex(1);

			this.getManagedBean().getFeeExemptionsForContract().clear();
			this.getManagedBean().getDeletedContractFeeExemptions().clear();
			resFDTO = feeSO.getFeeExemptionsForContractAndReqItem(reqFDTO);

			for (PmFeeExemption exemption : resFDTO.getFeeExemptions()) {
				if (exemption.getRequestItemId() == null) {
					exemption.setRequestItemId(new ScRequestItem());// TODO:
																	// clear
																	// it
																	// before
																	// updating
																	// the
																	// fee
																	// list
				}
				this.getManagedBean().getFeeExemptionsForContract()
						.add(exemption);
			}

			// this.getManagedBean().setFeeExemptionIndex(2);
			// ////////////getting Thresholds////////////////////////////
			if (this.getManagedBean().getSelectedConfigGeneral()
					.getIsGeneralThreshold() == 0) {
				retrieveOrgProfileList();
			}
			IOperationalThresholdSO thresholdSO = (IOperationalThresholdSO) BusinessDelegate
					.getInstance().getServiceObject("pm",
							IOperationalThresholdSO.class);

			ThresholdReqDTO reqTDTO = new ThresholdReqDTO();
			ThresholdResDTO resTDTO = null;
			reqTDTO.setOrgProfileId(null);
			resTDTO = thresholdSO.getOperationalThresholds(reqTDTO);
			getManagedBean().setThreshold(resTDTO.getThresholdList().get(0));
			getManagedBean().setUnplannedThreshold(resTDTO.getThresholdList().get(0).getS2Unplanned());
			getManagedBean().setBudgetThreshold(resTDTO.getThresholdList().get(0).getBudgetSpent());
//			for (PmThreshold threshold : resTDTO.getThresholdList()) {
//				this.getManagedBean().getThresholds().add(threshold);
//			}
			
			thresholdSO.insertMinistryThresholds();
			ThresholdResDTO finalResDTO = new ThresholdResDTO();
			if (getFacesScopes().hasRoleUser(RoleCode.EP_ADMIN.value())) {
				finalResDTO = thresholdSO.getMinistryThresholds();				
			}else{
				UserReqDTO userReqDTO = new UserReqDTO();
				userReqDTO.setUserId(getFacesScopes().getUserSessionBean().getUserId());
				UserResDTO userResDTO = userSO.retrieveUserOrgListByUserId(userReqDTO);
				ThresholdReqDTO thresholdreqDTO = new ThresholdReqDTO();
				thresholdreqDTO.setOrgProfileId(userResDTO.getUserOrgList().get(0).getPmOrgProfile().getOrgProfileId());
				finalResDTO = thresholdSO.getOperationalThresholds(thresholdreqDTO);
			}
			
		
			if(!finalResDTO.getThresholdList().isEmpty()){
				getManagedBean().setUnplannedThresholdList(finalResDTO.getThresholdList());
				getManagedBean().setBudgetThresholdList(finalResDTO.getThresholdList());
			}
			
			System.out
					.println("...................resTDTO.getThresholdList().size()="
							+ resTDTO.getThresholdList().size());

			// ///////////////////////////////
			// ///////////////////////////////
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication().getNavigationHandler()
					.handleNavigation(fc, null, "settingConfig");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public void categoryCodesUpdate(int index,
			List<PmConfigRuleValue> ruleTypeValueList, PmCategoryL3 categoryL3) {
		log.info("................categoryCodesUpdate...............");
		ICategoryCodeSO categoryCodeSo;

		categoryCodeSo = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, ICategoryCodeSO.class);
		CategoryCodeReqDTO codeReqDTO = new CategoryCodeReqDTO();
		codeReqDTO.setCategoryL3Id(categoryL3.getCategoryL3Id());
		CategoryCodeResDTO CategoryCodeResDTO = categoryCodeSo
				.retrieveCategoryL3DtlById(codeReqDTO);
		if (CategoryCodeResDTO != null
				&& CategoryCodeResDTO.getCategoryL3() != null) {
			PmConfigRuleValue ruleValue = ruleTypeValueList.get(index);
			ruleValue.setCategoryL3Id(CategoryCodeResDTO.getCategoryL3());
			String categoryL3Code = CategoryCodeResDTO.getCategoryL3()
					.getCategoryL3Code();
			String categoryL2Code = CategoryCodeResDTO.getCategoryL3()
					.getPmCategoryL2().getCategoryL2Code();
			String categoryL1Code = CategoryCodeResDTO.getCategoryL3()
					.getPmCategoryL2().getPmCategoryL1().getCategoryL1Code();
			// log.info("..................CategoryCodeResDTO.getCategoryL3().getCategoryL3Id()="
			// + CategoryCodeResDTO.getCategoryL3().getCategoryL3Id());
			// log.info(".............categoryL3Code=" + categoryL3Code);
			// log.info(".............categoryL2Code=" + categoryL2Code);
			// log.info(".............categoryL1Code=" + categoryL1Code);
			ruleValue.setCategoryDisplayValue(categoryL1Code + categoryL2Code
					+ categoryL3Code);
			this.getManagedBean().getSelectedRuleType().getConfigRuleValues()
					.remove(index);
			this.getManagedBean().getSelectedRuleType().getConfigRuleValues()
					.add(index, ruleValue);
		}

	}

	public void ruleValueUpdate(int index,
			List<PmConfigRuleValue> ruleTypeValueList, String value) {
		log.info("................ruleValueUpdate...............");
		if (value != null) {
			PmConfigRuleValue ruleValue = ruleTypeValueList.get(index);

			ruleValue.setRuleValue(value);
			this.getManagedBean().getSelectedRuleType().getConfigRuleValues()
					.remove(index);
			this.getManagedBean().getSelectedRuleType().getConfigRuleValues()
					.add(index, ruleValue);
		}

	}

	public void fromValueUpdate(int index,
			List<PmConfigRuleValue> ruleTypeValueList, Double fromValue) {
		log.info("................fromValueUpdate...............");
		if (fromValue != null) {
			PmConfigRuleValue ruleValue = ruleTypeValueList.get(index);

			ruleValue.setFromValue(fromValue);
			this.getManagedBean().getSelectedRuleType().getConfigRuleValues()
					.remove(index);
			this.getManagedBean().getSelectedRuleType().getConfigRuleValues()
					.add(index, ruleValue);
		}

	}

	public void toValueUpdate(int index,
			List<PmConfigRuleValue> ruleTypeValueList, Double toValue) {
		log.info("................toValueUpdate...............");
		if (toValue != null) {
			PmConfigRuleValue ruleValue = ruleTypeValueList.get(index);

			ruleValue.setToValue(toValue);
			this.getManagedBean().getSelectedRuleType().getConfigRuleValues()
					.remove(index);
			this.getManagedBean().getSelectedRuleType().getConfigRuleValues()
					.add(index, ruleValue);
		}

	}

	public void effDateUpdate(int index,
			List<PmConfigRuleValue> ruleTypeValueList, Date effDate) {
		log.info("................effDateUpdate...............");
		if (effDate != null) {
			PmConfigRuleValue ruleValue = ruleTypeValueList.get(index);

			ruleValue.setEffDate(effDate);
			this.getManagedBean().getSelectedRuleType().getConfigRuleValues()
					.remove(index);
			this.getManagedBean().getSelectedRuleType().getConfigRuleValues()
					.add(index, ruleValue);
		}

	}

	public void expDateUpdate(int index,
			List<PmConfigRuleValue> ruleTypeValueList, Date expDate) {
		log.info("................expDateUpdate...............");
		if (expDate != null) {
			PmConfigRuleValue ruleValue = ruleTypeValueList.get(index);

			ruleValue.setExpDate(expDate);
			this.getManagedBean().getSelectedRuleType().getConfigRuleValues()
					.remove(index);
			this.getManagedBean().getSelectedRuleType().getConfigRuleValues()
					.add(index, ruleValue);
		}

	}

	public void updateOrgProfileForFee(int index,
			List<PmFeeExemption> feeExcemptionList) {
		log.info("................updateOrgProfileForFee...............");
		IOrgProfileSO profileSO;
		try {
			profileSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IOrgProfileSO.class);

			OrgProfileReqDTO reqDAO = new OrgProfileReqDTO();
			reqDAO.setOrgProfileId(this.getManagedBean()
					.getSelectedSmSupplier().getSupplierId());
			OrgProfileResDTO resDAO = profileSO
					.retrieveOrgWithSupplierByProfileId(reqDAO);
			System.out.println("...............resDAO.getPmOrgProfile()="
					+ resDAO.getPmOrgProfile());
			System.out
					.println("...............this.getManagedBean().getSelectedSmSupplier().getSupplierId()="
							+ this.getManagedBean().getSelectedSmSupplier()
									.getSupplierId());
			PmFeeExemption exemption = feeExcemptionList.get(index);
			exemption.setOrgProfileId(resDAO.getPmOrgProfile());
			this.getManagedBean().getFeeExemptionsForOrgAndItem().remove(index);
			this.getManagedBean().getFeeExemptionsForOrgAndItem()
					.add(index, exemption);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			profileSO = null;
		}
	}

	public void updateDatesForFeeBaseOnContract(int index, CtContract contract) {
		log.info("................updateDatesForFeeBaseOnContract...............");
		Date currentDate = new Date();
		Date defaultEffDate = contract.getLatestContractVer()
				.getCtContractValue().getEffDate();
		Date defaultExpDate = contract.getLatestContractVer()
				.getCtContractValue().getExpDate();
		if (defaultEffDate.getTime() < currentDate.getTime()) {
			defaultEffDate = currentDate;
		}
		PmFeeExemption exemption = this.getManagedBean()
				.getFeeExemptionsForContract().get(index);
		exemption.setDefaultEffDate(defaultEffDate);
		exemption.setEffDate(defaultEffDate);
		exemption.setExpDate(defaultExpDate);
		this.getManagedBean().getFeeExemptionsForContract().remove(index);
		this.getManagedBean().getFeeExemptionsForContract()
				.add(index, exemption);
	}

	public void updateCmItemForFee(int index,
			List<PmFeeExemption> feeExcemptionList) {
		log.info("................updateCmItemForFee...............");

		try {
			CmItem selectedCmItem = this.getManagedBean().getSelectedCmItem();
			if (selectedCmItem == null) {
				selectedCmItem = new CmItem();// set empty cmItem if not
												// selected any
			}
			PmFeeExemption exemption = feeExcemptionList.get(index);
			exemption.setItemId(selectedCmItem);
			this.getManagedBean().getFeeExemptionsForOrgAndItem().remove(index);
			this.getManagedBean().getFeeExemptionsForOrgAndItem()
					.add(index, exemption);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}
	}

	public void updateUnspscForFee(int index,
			List<PmFeeExemption> feeExcemptionList) {
		log.info("................updateUnspscForFee...............");
		ICodificationSO unspscSO;
		try {

			PmFeeExemption exemption = feeExcemptionList.get(index);
			unspscSO = (ICodificationSO) BusinessDelegate.getInstance()
					.getServiceObject("cm", ICodificationSO.class);
			CodificationReqDTO reqDTO = new CodificationReqDTO();
			reqDTO.setUnspscCode(this.getManagedBean().getSelectedComodity()
					.getUnspscCode());
			CodificationResDTO resDTO = unspscSO
					.retrieveUNSPSCByCommodityCode(reqDTO);

			if (resDTO.isSuccessful()) {
				CmUnspsc commodity = resDTO.getCommodityObj();
				exemption.setTypes(new ArrayList<CmType>());
				for (CmType type : commodity.getCmType()) {
					if (!StringUtils.isBlank(type.getTypeName())) {
						exemption.getTypes().add(type);
					}
				}

			}

			exemption.setUnspscId(resDTO.getCommodityObj());
			this.getManagedBean().getFeeExemptionsForComodity().remove(index);
			this.getManagedBean().getFeeExemptionsForComodity()
					.add(index, exemption);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			unspscSO = null;
		}
	}

	public void scRequestItemCodeUpdate(int index,
			List<PmFeeExemption> feeExcemptionList) {
		log.info("................scRequestItemCodeUpdate...............");
		IContractItemSO contractItemSO;
		try {
			contractItemSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.CONTRACT_MANAGEMENT, IContractItemSO.class);

			ContractItemReqDTO reqDAO = new ContractItemReqDTO();
			reqDAO.setContractItemId(this.getManagedBean()
					.getSelectedCtContractItem().getContractItemId());
			ContractItemResDTO resDAO = contractItemSO.getContractItem(reqDAO);
			System.out
					.println("...............this.getManagedBean().getSelectedCtContractItem().getContractItemId()="
							+ this.getManagedBean().getSelectedCtContractItem()
									.getContractItemId());
			System.out
					.println("...............resDAO.getCtContractItem().getScRequestItem()="
							+ resDAO.getCtContractItem().getScRequestItem());
			System.out
					.println("...............resDAO.getCtContractItem().getScRequestItem().getRequestNoteItemId()="
							+ resDAO.getCtContractItem().getScRequestItem()
									.getRequestNoteItemId());
			PmFeeExemption exemption = feeExcemptionList.get(index);
			exemption.setRequestItemId(resDAO.getCtContractItem()
					.getScRequestItem());
			System.out
					.println(".....after set..........exemption.getRequestItemId().getRequestNoteItemId()="
							+ exemption.getRequestItemId()
									.getRequestNoteItemId());
			this.getManagedBean().getFeeExemptionsForContract().remove(index);
			this.getManagedBean().getFeeExemptionsForContract()
					.add(index, exemption);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			contractItemSO = null;
		}

	}

	/**
	 * get all language list
	 * 
	 * @return List<PmLanguage>
	 */
	public List<PmLanguage> getLanguageList() {
		log.info("getLanguageList");
		return PmCommonUtil.retrieveLanguageObjs();
	}

	public void addMoreRuleValue() {
		log.info(".................addMoreRuleValue...................");
		PmConfigRuleValue newRuleValue = new PmConfigRuleValue();
		log.info("...................newRuleValue.getRuleValue()="
				+ newRuleValue.getRuleValue());
		newRuleValue.setRuleType(this.getManagedBean().getSelectedRuleType());
		newRuleValue.setEffDate(new Date());

		if (this.getManagedBean().getSelectedRuleType().getRuleDataTypeId()
				.getParameterCode().equals("C")) {
			newRuleValue.setCategoryL3Id(new PmCategoryL3()); // make sure to
																// delete the
																// new obj
																// before update
																// if doesn't
																// selected
		}

		int index = this.getManagedBean().getSelectedRuleType()
				.getConfigRuleValues().size();
		this.getManagedBean().getSelectedRuleType().getConfigRuleValues()
				.add(index, newRuleValue);

	}

	public void deleteMoreRuleValue(int rowIndex) {
		log.info(".................deleteMoreRuleValue...................");

		// PmConfigRuleValue ruleValue =
		// this.getManagedBean().getSelectedRuleType().getConfigRuleValues().get(rowIndex);

		// int indexOfSelectedRuleType =
		// this.getManagedBean().getConfigRuleList().indexOf(this.getManagedBean().getSelectedRuleType());
		// this.getManagedBean().getConfigRuleList().remove(indexOfSelectedRuleType);

		this.getManagedBean().getSelectedRuleType().getConfigRuleValues()
				.remove(rowIndex);
		// merge selectedRuleType with changes in values into ConfigRuleTypes
		// this.getManagedBean().getConfigRuleList().add(indexOfSelectedRuleType,
		// this.getManagedBean().getSelectedRuleType());
		// for(PmConfigRuleValue value :
		// this.getManagedBean().getConfigRuleList().get(indexOfSelectedRuleType).getConfigRuleValues()){
		// log.info(".......................value.getRuleValue()="+value.getRuleValue());
		// }

	}

	public void addMoreOrgAndItemFee() {
		log.info(".................addMoreOrgAndItemFee...................");
		PmFeeExemption newFeeExemption = new PmFeeExemption();
		newFeeExemption.setFeeExemptionType("1");
		newFeeExemption.setEffDate(new Date());
		newFeeExemption.setItemId(new CmItem());// make sure to delete the new
												// objs before update if doesn't
												// selected
		newFeeExemption.setOrgProfileId(new PmOrgProfile());

		int index = this.getManagedBean().getFeeExemptionsForOrgAndItem()
				.size();
		this.getManagedBean().getFeeExemptionsForOrgAndItem()
				.add(index, newFeeExemption);

	}

	public void deleteOrgAndItemFee(int rowIndex) {
		log.info(".................deleteOrgAndItemFee...................");
		PmFeeExemption deletedExemption = this.getManagedBean()
				.getFeeExemptionsForOrgAndItem().get(rowIndex);
		deletedExemption.setDeleted(true);
		this.getManagedBean().getDeletedOrgAndItemFeeExemptions()
				.add(deletedExemption);// should added to original list before
										// save
		this.getManagedBean().getFeeExemptionsForOrgAndItem()
				.remove(deletedExemption);

	}

	public void deleteOperationalThreshold(int rowIndex) {
		log.info(".................deleteOperationalThreshold...................");
		PmThreshold deletedThreshold = this.getManagedBean().getThresholds()
				.get(rowIndex);
		deletedThreshold.setDeleted(true);
		this.getManagedBean().getDeletedThresholds().add(deletedThreshold);// should
																			// added
																			// to
																			// original
																			// list
																			// before
																			// save
		this.getManagedBean().getThresholds().remove(deletedThreshold);

	}

	public void addMoreComodityFee() {
		log.info(".................addMoreComodityFee...................");
		PmFeeExemption newFeeExemption = new PmFeeExemption();
		newFeeExemption.setFeeExemptionType("6");
		newFeeExemption.setEffDate(new Date());// make sure to delete the new
												// objs before update if doesn't
												// selected
		newFeeExemption.setTypeId(new CmType());
		newFeeExemption.setUnspscId(new CmUnspsc());

		int index = this.getManagedBean().getFeeExemptionsForComodity().size();
		this.getManagedBean().getFeeExemptionsForComodity()
				.add(index, newFeeExemption);

	}

	public void deleteComodityFee(int rowIndex) {
		log.info(".................deleteComodityFee...................");

		PmFeeExemption deletedExemption = this.getManagedBean()
				.getFeeExemptionsForComodity().get(rowIndex);
		deletedExemption.setDeleted(true);
		this.getManagedBean().getDeletedComodityFeeExemptions()
				.add(deletedExemption);// should added to original list before
										// save
		this.getManagedBean().getFeeExemptionsForComodity()
				.remove(deletedExemption);

	}

	public void addMoreContractFee() {
		log.info(".................addMoreContractFee...................");
		PmFeeExemption newFeeExemption = new PmFeeExemption();
		newFeeExemption.setFeeExemptionType("2");
		newFeeExemption.setEffDate(new Date());
		newFeeExemption.setRequestItemId(new ScRequestItem());
		newFeeExemption.setContractId(new CtContract());

		int index = this.getManagedBean().getFeeExemptionsForContract().size();
		this.getManagedBean().getFeeExemptionsForContract()
				.add(index, newFeeExemption);

	}

	public void deleteContractFee(int rowIndex) {
		log.info(".................deleteContractFee...................");

		PmFeeExemption deletedExemption = this.getManagedBean()
				.getFeeExemptionsForContract().get(rowIndex);
		deletedExemption.setDeleted(true);
		this.getManagedBean().getDeletedContractFeeExemptions()
				.add(deletedExemption);// should added to original list before
										// save
		this.getManagedBean().getFeeExemptionsForContract()
				.remove(deletedExemption);

	}

	public void saveRuleCatalog() {
		log.info("...........saveRuleCatalog............");
		// make sure to delete the new obj before update if doesn't selected
		for (PmConfigRule ruleType : this.getManagedBean().getConfigRuleList()) {

			if (ruleType.getRuleDataTypeId().getParameterCode().equals("C")) {
				for (PmConfigRuleValue ruleTypeValue : ruleType
						.getConfigRuleValues()) {
					if (ruleTypeValue.getCategoryL3Id() != null
							&& ruleTypeValue.getCategoryL3Id()
									.getCategoryL3Id() == null) {
						// throw required msg
						throw new EpException(MessageConst.ERROR_770133,
								new String[] {});

					}
				}
			}
			// check validation
			for (int i = 0; i < ruleType.getConfigRuleValues().size(); i++) {

				PmConfigRuleValue baseValueToCompare = ruleType
						.getConfigRuleValues().get(i);
				if(baseValueToCompare.getRecordStatusFlg() == 0){//if is inActive one go to next record
					continue;
				}
				log.info(".........................baseValueToCompare.getRuleValue()="
						+ baseValueToCompare.getRuleValue());
				for (int j = i + 1; j <= (ruleType.getConfigRuleValues().size() - 1); j++) {
					PmConfigRuleValue value = ruleType.getConfigRuleValues()
							.get(j);
					if(value.getRecordStatusFlg() == 0){//if is inActive one go to next record
						continue;
					}
					log.info(".........................second.value.getRuleValue()="
							+ value.getRuleValue());

					if (!ruleValueValidation(baseValueToCompare, value,
							ruleType.getRuleDataTypeId(),
							ruleType.getValidationType())) {
						log.info("................befor throw exeption for category...................");
						// hit error and return
						switch(ruleType.getValidationType()){
						case '1' : throw new EpException(MessageConst.ERROR_770109,
					        	new String[] { ruleType.getRuleTypeDesc() });
						case '2' : throw new EpException(MessageConst.ERROR_770110,
							        	new String[] { ruleType.getRuleTypeDesc() });		           
						case '3' : throw new EpException(MessageConst.ERROR_770154,
					        	new String[] { ruleType.getRuleTypeDesc() });
						}
						
					}

					// }

				}
			}

		}

		IRuleSO ruleSO = null;
		try {
			ruleSO = (IRuleSO) BusinessDelegate.getInstance().getServiceObject(
					"pm", IRuleSO.class);
			RuleReqDTO reqDTO = new RuleReqDTO();

			reqDTO.setRuleTypeList(this.getManagedBean().getConfigRuleList());
			RuleResDTO resDTO = ruleSO.updateRuleValueList(reqDTO);
			// retrive updated list

			RuleConfigHelper.getInstance().relodeRuleTypeValueMaps();
			this.getManagedBean().setConfigRuleList(
					RuleConfigHelper.getInstance().getRuleTypeList());
			this.getManagedBean().setSelectedRuleType(
					RuleConfigHelper.getInstance().getRuleTypeList().get(0));
			// fill up ruleType's ruleValueList
			for (PmConfigRule cRule : this.getManagedBean().getConfigRuleList()) {
				cRule.setConfigRuleValues(RuleConfigHelper.getInstance()
						.getRuleTypeValueListByRuleType(cRule.getRuleType()));
			}
			// fill up category display value
			for (PmConfigRule rule : this.getManagedBean().getConfigRuleList()) {
				if (rule.getRuleDataTypeId().getParameterCode()
						.equalsIgnoreCase("c")) {
					for (PmConfigRuleValue cRule : rule.getConfigRuleValues()) {
						PmCategoryL3 categoryL3 = cRule.getCategoryL3Id();
						if (categoryL3 != null) {
							String categoryL3Code = categoryL3
									.getCategoryL3Code();
							String categoryL2Code = categoryL3
									.getPmCategoryL2().getCategoryL2Code();
							String categoryL1Code = categoryL3
									.getPmCategoryL2().getPmCategoryL1()
									.getCategoryL1Code();
							cRule.setCategoryDisplayValue(categoryL1Code
									+ categoryL2Code + categoryL3Code);
						}
					}
				}
			}
			if (resDTO.isSuccessful()) {
				FacesContext facesContext = FacesContext.getCurrentInstance();
				facesContext.addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, null,
								MessageUtils.getMessage(
										MessageConst.INFO_770009,
										MessageUtils.BUNDLE_COMMON_MESSAGES,
										new FacesScopes().getUserLocale())));
				System.out
						.println("***************rule type values have been updated*****************");

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private boolean ruleValueValidation(PmConfigRuleValue firstRuleValue,
			PmConfigRuleValue secondRuleValue, PmParameter RuleDataTypeId,
			char validationType) {
		boolean validationResult = false;
		boolean isValuesEqual = false;
		if (validationType == '1') {// EffectiveDate validation
			validationResult = !isDateOverlaped(firstRuleValue.getEffDate(),
					firstRuleValue.getExpDate(), secondRuleValue.getEffDate(),
					secondRuleValue.getExpDate());
		} else if (validationType == '2') {// EffectiveDate +value validation
			
			if (RuleDataTypeId.getParameterCode().equalsIgnoreCase("c")) {
				if (firstRuleValue.getCategoryDisplayValue().equals(secondRuleValue
						.getCategoryDisplayValue()))
					isValuesEqual = true;
			} else {
				if (firstRuleValue.getRuleValue().equals(
						secondRuleValue.getRuleValue()))
					isValuesEqual = true;

			}
			if (isValuesEqual) {
				validationResult = !isDateOverlaped(firstRuleValue.getEffDate(),
						firstRuleValue.getExpDate(),
						secondRuleValue.getEffDate(),
						secondRuleValue.getExpDate());
			} else {
				validationResult = true;
			}

		} else {// validationType == '3' , //EffectiveDate + value + Range
				// validation
			if (firstRuleValue.getRuleValue().equals(
					secondRuleValue.getRuleValue())){
				isValuesEqual = true;
			}
			if (isValuesEqual) {
				validationResult = !(isRangeOverlaped(
						firstRuleValue.getFromValue(),
						firstRuleValue.getToValue(),
						secondRuleValue.getFromValue(),
						secondRuleValue.getToValue()) && isDateOverlaped(
						firstRuleValue.getEffDate(),
						firstRuleValue.getExpDate(),
						secondRuleValue.getEffDate(),
						secondRuleValue.getExpDate()));
			} else {
				validationResult = true;
			}
		}
		return validationResult;
	}

	boolean isRangeOverlaped(Double fromValue1, Double toValue1,
			Double fromValue2, Double toValue2) {
//		if (toValue1 == null) {
//			return fromValue1 <= toValue2;
//
//		}
//		if (toValue2 == null) {
//			return fromValue2 <= toValue1;
//
//		}
		return fromValue1 <= toValue2 && fromValue2 <= toValue1;
	}

	boolean isDateOverlaped(Date start1, Date end1, Date start2, Date end2) {

		if (end1 == null && end2 == null) {

			return true;
		}
		if (end1 == null) {
			return start1.getTime() <= end2.getTime();

		}
		if (end2 == null) {
			return start2.getTime() <= end1.getTime();

		}
		return start1.getTime() <= end2.getTime()
				&& start2.getTime() <= end1.getTime();
	}

	public void saveFeeExemptionForOrgItem() {
		log.info("..............saveFeeExemptionForOrgItem..............");
		List<PmFeeExemption> exemptionList = new ArrayList<PmFeeExemption>();
		exemptionList.addAll(this.getManagedBean()
				.getFeeExemptionsForOrgAndItem());

		log.info("............................exemptionList.size="
				+ exemptionList.size());

		for (PmFeeExemption exemption : exemptionList) {
			if (exemption.getOrgProfileId() != null
					&& exemption.getOrgProfileId().getOrgProfileId() == null) {// throw
																				// required
																				// msg
				// hit error and return
				throw new EpException(MessageConst.ERROR_770130,
						new String[] {});
			}
			if (exemption.getItemId() != null
					&& exemption.getItemId().getItemId() == null) {// clear
																	// empty
				// objects
				exemption.setItemId(null);
			}
			if (exemption.getFeeExemptionId() == null) {// set ExemptionType for
														// new FeeExemptions
				if (exemption.getItemId() == null) {
					exemption.setFeeExemptionType("1");
				} else {
					exemption.setFeeExemptionType("4");
				}
			} else if (exemption.getFeeExemptionType().equals("1")
					&& exemption.getItemId() != null) {
				// update exemptionType if Item is selected
				exemption.setFeeExemptionType("4");
			}
		}
		if (isDateOverlappedForExemptionType(exemptionList)) {
			for (PmFeeExemption ex : exemptionList) {
				if (ex.getItemId() == null) {
					ex.setItemId(new CmItem());
				}
			}

			// hit error and return
			throw new EpException(MessageConst.ERROR_770112, new String[] {});
		}
		// add deleted list before update
		exemptionList.addAll(this.getManagedBean()
				.getDeletedOrgAndItemFeeExemptions());
		for (PmFeeExemption exe : exemptionList) {// befor update clear remained
													// empty objs
			if (exe.getItemId() != null && exe.getItemId().getItemId() == null) {// clear
																					// empty
				// objects
				exe.setItemId(null);
			}
		}
		IFeeExemptionSO exemptionSO = null;
		try {
			exemptionSO = (IFeeExemptionSO) BusinessDelegate.getInstance()
					.getServiceObject("pm", IFeeExemptionSO.class);
			FeeExemptionReqDTO reqDTO = new FeeExemptionReqDTO();
			reqDTO.setFeeExemptionList(exemptionList);

			FeeExemptionResDTO resDTO = exemptionSO
					.updateFeeExemptionList(reqDTO);
			// retrieve
			this.getManagedBean().getFeeExemptionsForOrgAndItem().clear();
			this.getManagedBean().getDeletedOrgAndItemFeeExemptions().clear();
			reqDTO = new FeeExemptionReqDTO();
			resDTO = exemptionSO.getFeeExemptionsForOrgAndItem(reqDTO);
			for (PmFeeExemption exemption : resDTO.getFeeExemptions()) {
				if (exemption.getItemId() == null) {// TODO: clear it before
													// updating the fee list
					exemption.setItemId(new CmItem());
				}
				this.getManagedBean().getFeeExemptionsForOrgAndItem()
						.add(exemption);
			}
			if (resDTO.isSuccessful()) {
				FacesContext facesContext = FacesContext.getCurrentInstance();
				facesContext.addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, null,
								MessageUtils.getMessage(
										MessageConst.INFO_770010,
										MessageUtils.BUNDLE_COMMON_MESSAGES,
										new FacesScopes().getUserLocale())));
				System.out
						.println("***************fee Exemption list have been updated*****************");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean isDateOverlappedForExemptionType(
			List<PmFeeExemption> feeExemptionList) {
		log.info("..............isDateOverlappedForExemptionType................");
		boolean result = false;

		for (int i = 0; i < feeExemptionList.size(); i++) {
			log.info("...................first loop");
			PmFeeExemption baseExemption = feeExemptionList.get(i);
			if (baseExemption.getRecordStatusFlg() == 0) {
				// if record inactive choose next one
				continue;
			}
			// log.info(".................after continue..in..first loop");
			for (int j = i + 1; j <= (feeExemptionList.size() - 1); j++) {
				log.info("...................second loop");
				PmFeeExemption ex = feeExemptionList.get(j);
				if (ex.getRecordStatusFlg() == 0) {
					// if record inactive choose next one
					continue;
				}
				log.info(".................after continiue..in..second loop");
				//

				if ((ex.getFeeExemptionType().equals("1") && ex
						.getOrgProfileId().getOrgProfileId() == baseExemption
						.getOrgProfileId().getOrgProfileId())
						|| (ex.getFeeExemptionType().equals("4")
								&& ex.getOrgProfileId().getOrgProfileId() == baseExemption
										.getOrgProfileId().getOrgProfileId() && ex
								.getItemId().getItemId() == baseExemption
								.getItemId().getItemId())) {

					result = isDateOverlaped(ex.getEffDate(), ex.getExpDate(),
							baseExemption.getEffDate(),
							baseExemption.getExpDate());

				} else if ((ex.getFeeExemptionType().equals("6") && ex
						.getUnspscId().getUnspscCode() == baseExemption
						.getUnspscId().getUnspscCode())
						|| (ex.getFeeExemptionType().equals("3")
								&& ex.getUnspscId().getUnspscCode() == baseExemption
										.getUnspscId().getUnspscCode() && ex
								.getTypeId().getTypeId() == baseExemption
								.getTypeId().getTypeId())) {
					result = isDateOverlaped(ex.getEffDate(), ex.getExpDate(),
							baseExemption.getEffDate(),
							baseExemption.getExpDate());

				} else if ((ex.getFeeExemptionType().equals("2") && ex
						.getContractId().getContractId() == baseExemption
						.getContractId().getContractId())
						|| (ex.getFeeExemptionType().equals("5")
								&& ex.getContractId().getContractId() == baseExemption
										.getContractId().getContractId() && ex
								.getRequestItemId().getRequestNoteItemId() == baseExemption
								.getRequestItemId().getRequestNoteItemId())) {

					result = isDateOverlaped(ex.getEffDate(), ex.getExpDate(),
							baseExemption.getEffDate(),
							baseExemption.getExpDate());
				}

				if (result == true) {
					return result;
				}
			}// end of second loop

		}// end of first loop

		return result;
	}

	public void saveFeeExemptionForContract() {
		log.info("...........saveFeeExemptionForContract..................");
		List<PmFeeExemption> exemptionList = new ArrayList<PmFeeExemption>();
		exemptionList.addAll(this.getManagedBean()
				.getFeeExemptionsForContract());

		for (PmFeeExemption exemption : exemptionList) {
			if (exemption.getContractId() != null
					&& exemption.getContractId().getContractId() == null) {
				// throw required msg
				throw new EpException(MessageConst.ERROR_770131,
						new String[] {});
			}
			if (exemption.getRequestItemId() != null
					&& exemption.getRequestItemId().getRequestNoteItemId() == null) {// clear
				// empty
				// //
				// objects
				exemption.setRequestItemId(null);
			}

			if (exemption.getFeeExemptionId() == null) {// set ExemptionType for
														// new FeeExemptions
				if (exemption.getRequestItemId() == null) {
					exemption.setFeeExemptionType("2");
				} else {
					exemption.setFeeExemptionType("5");
				}
			} else if (exemption.getFeeExemptionType().equals("2")
					&& exemption.getRequestItemId() != null) {
				// update exemptionType if requestedItem is selected
				exemption.setFeeExemptionType("5");
			}
		}
		if (isDateOverlappedForExemptionType(exemptionList)) {
			for (PmFeeExemption ex : exemptionList) {
				if (ex.getRequestItemId() == null) {
					ex.setRequestItemId(new ScRequestItem());
				}
			}
			// hit error and return
			throw new EpException(MessageConst.ERROR_770112, new String[] {});
		}
		// add deleted records before update
		exemptionList.addAll(this.getManagedBean()
				.getDeletedContractFeeExemptions());
		for (PmFeeExemption exe : exemptionList) {
			if (exe.getRequestItemId() != null
					&& exe.getRequestItemId().getRequestNoteItemId() == null) {// clear
																				// remaining
				// empty
				// //
				// objects
				exe.setRequestItemId(null);
			}
		}
		IFeeExemptionSO exemptionSO = null;
		try {
			exemptionSO = (IFeeExemptionSO) BusinessDelegate.getInstance()
					.getServiceObject("pm", IFeeExemptionSO.class);
			FeeExemptionReqDTO reqDTO = new FeeExemptionReqDTO();
			reqDTO.setFeeExemptionList(exemptionList);
			FeeExemptionResDTO resDTO = exemptionSO
					.updateFeeExemptionList(reqDTO);
			// retrieve
			this.getManagedBean().getFeeExemptionsForContract().clear();
			this.getManagedBean().getDeletedContractFeeExemptions().clear();
			reqDTO = new FeeExemptionReqDTO();
			resDTO = exemptionSO.getFeeExemptionsForContractAndReqItem(reqDTO);

			for (PmFeeExemption exemption : resDTO.getFeeExemptions()) {
				if (exemption.getRequestItemId() == null) {
					exemption.setRequestItemId(new ScRequestItem());// TODO:
																	// clear
																	// it
																	// before
																	// updating
																	// the
																	// fee
																	// list
				}
				this.getManagedBean().getFeeExemptionsForContract()
						.add(exemption);
			}
			if (resDTO.isSuccessful()) {
				FacesContext facesContext = FacesContext.getCurrentInstance();
				facesContext.addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, null,
								MessageUtils.getMessage(
										MessageConst.INFO_770010,
										MessageUtils.BUNDLE_COMMON_MESSAGES,
										new FacesScopes().getUserLocale())));
				System.out
						.println("***************fee Exemption list have been updated*****************");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void addMoreOperationalThreshod() {
		log.info(".................addMoreOperationalThreshod...................");
		if (this.getManagedBean().getSelectedOrgProfile().getOrgProfileId() == null) {
			// hit required msg and return
			throw new EpException(MessageConst.ERROR_770153, new String[] {});
		} else {
			PmThreshold newThreshold = new PmThreshold();
			newThreshold.setEffDate(new Date());
			newThreshold.setOrgProfileId(this.getManagedBean()
					.getSelectedOrgProfile());

			int index = this.getManagedBean().getThresholds().size();
			this.getManagedBean().getThresholds().add(index, newThreshold);
		}

	}

	public void saveFeeExemptionForComodity() {
		List<PmFeeExemption> exemptionList = new ArrayList<PmFeeExemption>();
		exemptionList.addAll(this.getManagedBean()
				.getFeeExemptionsForComodity());

		for (PmFeeExemption exemption1 : exemptionList) {
			if (exemption1.getUnspscId() != null
					&& exemption1.getUnspscId().getUnspscId() == null) {
				// throw required msg
				throw new EpException(MessageConst.ERROR_770132,
						new String[] {});
			}
			if (exemption1.getTypeId() != null
					&& exemption1.getTypeId().getTypeId() == null) {// if type
																	// not
																	// selected
																	// remove
																	// empty
																	// object
				exemption1.setTypeId(null);
			}

			if (exemption1.getFeeExemptionId() == null) {// set ExemptionType
															// for
				if (exemption1.getTypeId() == null) { // new FeeExemptions
					exemption1.setFeeExemptionType("6");
				} else {
					exemption1.setFeeExemptionType("3");
				}
			} else if (exemption1.getFeeExemptionType().equals("6")
					&& exemption1.getTypeId() != null) {
				// update exemptionType if Type is selected
				exemption1.setFeeExemptionType("3");
			}
		}
		if (isDateOverlappedForExemptionType(exemptionList)) {
			for (PmFeeExemption ex : exemptionList) {
				if (ex.getTypeId() == null) {
					ex.setTypeId(new CmType());
				}
			}
			// hit error and return
			throw new EpException(MessageConst.ERROR_770112, new String[] {});
		}
		// add deleted records before update
		exemptionList.addAll(this.getManagedBean()
				.getDeletedComodityFeeExemptions());
		for (PmFeeExemption exe : exemptionList) { // clear remaining empty objs
			if (exe.getTypeId() != null && exe.getTypeId().getTypeId() == null) {
				exe.setTypeId(null);
			}
		}
		IFeeExemptionSO exemptionSO = null;
		try {
			exemptionSO = (IFeeExemptionSO) BusinessDelegate.getInstance()
					.getServiceObject("pm", IFeeExemptionSO.class);
			FeeExemptionReqDTO reqDTO = new FeeExemptionReqDTO();
			reqDTO.setFeeExemptionList(exemptionList);
			FeeExemptionResDTO resDTO = exemptionSO
					.updateFeeExemptionList(reqDTO);
			// retrive
			this.getManagedBean().getFeeExemptionsForComodity().clear();
			this.getManagedBean().getDeletedComodityFeeExemptions().clear();
			reqDTO = new FeeExemptionReqDTO();
			resDTO = exemptionSO.getFeeExemptionsForComodity(reqDTO);

			ICodificationSO unspscSO = (ICodificationSO) BusinessDelegate
					.getInstance()
					.getServiceObject("cm", ICodificationSO.class);
			CodificationReqDTO cReqDTO = new CodificationReqDTO();
			for (PmFeeExemption exemption : resDTO.getFeeExemptions()) {
				if (exemption.getTypeId() == null) {
					exemption.setTypeId(new CmType());
				}
				cReqDTO.setUnspscCode(exemption.getUnspscId().getUnspscCode());
				CodificationResDTO cResDTO = unspscSO
						.retrieveUNSPSCByCommodityCode(cReqDTO);

				if (cResDTO.isSuccessful()) {

					CmUnspsc commodity = cResDTO.getCommodityObj();

					exemption.setTypes(new ArrayList<CmType>());
					for (CmType type : commodity.getCmType()) {
						if (!StringUtils.isBlank(type.getTypeName())) {
							exemption.getTypes().add(type);
						}
					}
				}
				this.getManagedBean().getFeeExemptionsForComodity()
						.add(exemption);
			}
			if (resDTO.isSuccessful()) {
				FacesContext facesContext = FacesContext.getCurrentInstance();
				facesContext.addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, null,
								MessageUtils.getMessage(
										MessageConst.INFO_770010,
										MessageUtils.BUNDLE_COMMON_MESSAGES,
										new FacesScopes().getUserLocale())));
				System.out
						.println("***************fee Exemption list have been updated*****************");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void saveGeneralSetting() {
		IConfigGeneralSO cgeneralSO = null;
		try {
			cgeneralSO = (IConfigGeneralSO) BusinessDelegate.getInstance()
					.getServiceObject("pm", IConfigGeneralSO.class);
			ConfigGeneralReqDTO reqDTO = new ConfigGeneralReqDTO();
			reqDTO.setPmConfigGeneral(this.getManagedBean()
					.getSelectedConfigGeneral());
			ConfigGeneralResDTO resDTO = cgeneralSO.updateConfigGeneral(reqDTO);
			// retrieve
			reqDTO = new ConfigGeneralReqDTO();
			resDTO = cgeneralSO.getConfigGeneral(reqDTO);
			this.getManagedBean().setSelectedConfigGeneral(
					resDTO.getPmConfigGeneral());
			if (resDTO.isSuccessful()) {
				FacesContext facesContext = FacesContext.getCurrentInstance();
				facesContext.addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, null,
								MessageUtils.getMessage(
										MessageConst.INFO_770008,
										MessageUtils.BUNDLE_COMMON_MESSAGES,
										new FacesScopes().getUserLocale())));
				System.out
						.println("***************general configure have been updated*****************");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// public void saveKpiValues(){
	// IKPIStatusSO kpiSO = null;
	// try {
	// kpiSO = (IKPIStatusSO) BusinessDelegate.getInstance()
	// .getServiceObject("pm", IKPIStatusSO.class);
	// KPIReqDTO reqDTO = new KPIReqDTO();
	// reqDTO.setKpiStatusList(this.getManagedBean().getKpiStatusList());
	// KPIResDTO resDTO = kpiSO.updateKpiValues(reqDTO);
	// if(resDTO.isSuccessful()){
	// FacesContext facesContext = FacesContext.getCurrentInstance();
	// facesContext.addMessage(
	// null,
	// new FacesMessage(FacesMessage.SEVERITY_INFO, null,
	// MessageUtils.getMessage(
	// MessageConst.INFO_770008,
	// MessageUtils.BUNDLE_COMMON_MESSAGES,
	// new FacesScopes().getUserLocale())));
	// System.out
	// .println("***************KPI value list have been updated*****************");
	// }
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	//
	// }
	// public void filterKPITableByDataType() {
	// System.out
	// .println("..............filterKPITableByDataType...............");
	// System.out
	// .println(".....................this.getManagedBean().getSelectedDocumentType()="
	// + this.getManagedBean().getSelectedDocumentType());
	// // this.getManagedBean().getKpiListDataModel().reloadData();
	// IKPIStatusSO kpiSO = (IKPIStatusSO) BusinessDelegate.getInstance()
	// .getServiceObject("pm", IKPIStatusSO.class);
	// KPIReqDTO reqDTO = new KPIReqDTO();
	// FacesScopes fs = new FacesScopes();
	// reqDTO.setLanguageCode(fs.getUserLocale().getLanguage());
	// reqDTO.setDocType(this.getManagedBean().getSelectedDocumentType());
	//
	// KPIResDTO resDTO = kpiSO.getKpiValuesDocyType(reqDTO);
	// this.getManagedBean().setKpiStatusList(resDTO.getKpiStatusList());
	// }
	public void saveOperationalThreshold() {
		log.info("..............saveFeeExemptionForOrgItem..............");
		List<PmThreshold> thresholdList = new ArrayList<PmThreshold>();
		thresholdList.addAll(this.getManagedBean().getThresholds());

		log.info("............................thresholdList.size="
				+ thresholdList.size());

		for (int i = 0; i < thresholdList.size(); i++) {

			PmThreshold baseValueToCompare = thresholdList.get(i);
			log.info(".........................thresholdList.get(i)="
					+ thresholdList.get(i));
			for (int j = i + 1; j <= (thresholdList.size() - 1); j++) {
				PmThreshold value = thresholdList.get(j);
				log.info(".........................second.value.thresholdList.get(j)="
						+ thresholdList.get(j));

				if (isDateOverlaped(baseValueToCompare.getEffDate(),
						baseValueToCompare.getExpDate(), value.getEffDate(),
						value.getExpDate())) {
					// hit error and return
					throw new EpException(MessageConst.ERROR_770152,
							new String[] {});
				}
				// for (PmThreshold th : thresholdList) {
				// if (th.getOrgProfileId() == null) {
				// th.setOrgProfileId(new PmOrgProfile());
				// }
				// }

			}
		}
		// add deleted list before update
		thresholdList.addAll(this.getManagedBean().getDeletedThresholds());

		IOperationalThresholdSO thresholdSO = null;
		try {
			thresholdSO = (IOperationalThresholdSO) BusinessDelegate
					.getInstance().getServiceObject("pm",
							IOperationalThresholdSO.class);
			ThresholdReqDTO reqDTO = new ThresholdReqDTO();
			reqDTO.setThresholdList(thresholdList);

			ThresholdResDTO resDTO = thresholdSO
					.updateOperationalThresholdList(reqDTO);
			// retrieve current list again
			this.getManagedBean().getThresholds().clear();
			this.getManagedBean().getDeletedThresholds().clear();
			reqDTO = new ThresholdReqDTO();
			reqDTO.setOrgProfileId(this.getManagedBean()
					.getSelectedOrgProfile().getOrgProfileId());
			resDTO = thresholdSO.getOperationalThresholds(reqDTO);
			for (PmThreshold thresh : resDTO.getThresholdList()) {

				this.getManagedBean().getThresholds().add(thresh);
			}
			if (resDTO.isSuccessful()) {
				FacesContext facesContext = FacesContext.getCurrentInstance();
				facesContext.addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, null,
								MessageUtils.getMessage(
										MessageConst.INFO_770093,
										MessageUtils.BUNDLE_COMMON_MESSAGES,
										new FacesScopes().getUserLocale())));
				System.out
						.println("***************threshold list have been updated*****************");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void retrieveOrgProfileList() {
		IOrgProfileSO orgProfileSO = (IOrgProfileSO) BusinessDelegate
				.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IOrgProfileSO.class);
		OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
		OrgProfileResDTO resDTO = orgProfileSO
				.retrieveOrgProfileListByMinistry(reqDTO);
		this.getManagedBean().setOrgProfileList(resDTO.getOrgProfileList());
		for (PmOrgProfile orgprofile : resDTO.getOrgProfileList()) {
			log.info("....................orgprofile.getOrgProfileId()="
					+ orgprofile.getOrgProfileId());
		}
	}

	public void ministeryChangeListener() {
		log.info("inside ministeryChangeListener");
		IOperationalThresholdSO thresholdSO = (IOperationalThresholdSO) BusinessDelegate
				.getInstance().getServiceObject("pm",
						IOperationalThresholdSO.class);

		IOrgProfileSO orgProfileSO = (IOrgProfileSO) BusinessDelegate
				.getInstance().getServiceObject("pm", IOrgProfileSO.class);

		OrgProfileReqDTO pReqDTO = new OrgProfileReqDTO();
		OrgProfileResDTO pResDTO = null;
		// get selected ministry from DB
		if (this.getManagedBean().getSelectedOrgProfile().getOrgProfileId() != null) {
			pReqDTO.setOrgProfileId(this.getManagedBean()
					.getSelectedOrgProfile().getOrgProfileId());
			pResDTO = orgProfileSO.retrieveOrgProfileByProfileId(pReqDTO);
			this.getManagedBean().setSelectedOrgProfile(
					pResDTO.getPmOrgProfile());
		}
		ThresholdReqDTO reqTDTO = new ThresholdReqDTO();
		ThresholdResDTO resTDTO = null;
		log.info(".....this.getManagedBean().getSelectedOrgProfile().getOrgProfileId()="
				+ this.getManagedBean().getSelectedOrgProfile()
						.getOrgProfileId());
		// if(this.getManagedBean().getSelectedOrgProfile() != null){
		reqTDTO.setOrgProfileId(this.getManagedBean().getSelectedOrgProfile()
				.getOrgProfileId());
		// }
		this.getManagedBean().getThresholds().clear();
		this.getManagedBean().getDeletedThresholds().clear();
		resTDTO = thresholdSO.getOperationalThresholds(reqTDTO);

		for (PmThreshold threshold : resTDTO.getThresholdList()) {
			if (threshold.getOrgProfileId() == null) {// TODO: clear it before
				// updating the threshold list
				threshold.setOrgProfileId(new PmOrgProfile());
			}
			this.getManagedBean().getThresholds().add(threshold);
		}
		if (resTDTO.getThresholdList().size() == 0) {
			this.getManagedBean().setThresholds(null);
		}
		System.out
				.println("...................resTDTO.getThresholdList().size()="
						+ resTDTO.getThresholdList().size());

	}
	
	/**
	 * get faces scopes
	 * 
	 * @return FacesScopes
	 */
	private FacesScopes getFacesScopes() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
	}
	
	public boolean checkIsEpAdmin(){
		if(getFacesScopes().hasRoleUser(RoleCode.EP_ADMIN.value())){
			return true;
		}else {
			return false;
		}
	}
	
	public void onChangeMainUnplannedThreshold(){
	//	getManagedBean().getThreshold().setS2Unplanned(getManagedBean().getUnplannedThreshold().intValue());
		for(PmThreshold threshold : getManagedBean().getUnplannedThresholdList()){
			threshold.setS2Unplanned(getManagedBean().getThreshold().getS2Unplanned());
		}
	}
	
	public void onChangeMainBudgetThreshold(){
		for(PmThreshold threshold : getManagedBean().getBudgetThresholdList()){
			threshold.setBudgetSpent(getManagedBean().getThreshold().getBudgetSpent());
		}
	}
	
	public void saveThresholds(int type){
		// 1 - unplanned
		// 2 - budget
		
		List<PmThreshold> saveThresholdList = new ArrayList<PmThreshold>();
		saveThresholdList.add(getManagedBean().getThreshold());
		if(type == 1){
			saveThresholdList.addAll(getManagedBean().getUnplannedThresholdList());
		}else if(type == 2){
			saveThresholdList.addAll(getManagedBean().getBudgetThresholdList());			
		}
		
		try{
			IOperationalThresholdSO thresholdSO = (IOperationalThresholdSO) BusinessDelegate
					.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IOperationalThresholdSO.class);
			IUserSO userSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
			ThresholdReqDTO reqDTO = new ThresholdReqDTO();
			reqDTO.setThresholdList(saveThresholdList);
			reqDTO.setPmConfigGeneral(getManagedBean().getSelectedConfigGeneral());
			ThresholdResDTO resDTO = thresholdSO.updateOperationalThresholdList(reqDTO);
			
			ThresholdReqDTO reqTDTO = new ThresholdReqDTO();
			ThresholdResDTO resTDTO = null;
			reqTDTO.setOrgProfileId(null);
			resTDTO = thresholdSO.getOperationalThresholds(reqTDTO);
			getManagedBean().setThreshold(resTDTO.getThresholdList().get(0));
			getManagedBean().setUnplannedThreshold(resTDTO.getThresholdList().get(0).getS2Unplanned());
			getManagedBean().setBudgetThreshold(resTDTO.getThresholdList().get(0).getBudgetSpent());
			
			ThresholdResDTO finalResDTO = new ThresholdResDTO();
			if (getFacesScopes().hasRoleUser(RoleCode.EP_ADMIN.value())) {
				finalResDTO = thresholdSO.getMinistryThresholds();				
			}else{
				UserReqDTO userReqDTO = new UserReqDTO();
				userReqDTO.setUserId(getFacesScopes().getUserSessionBean().getUserId());
				UserResDTO userResDTO = userSO.retrieveUserOrgListByUserId(userReqDTO);
				ThresholdReqDTO thresholdreqDTO = new ThresholdReqDTO();
				thresholdreqDTO.setOrgProfileId(userResDTO.getUserOrgList().get(0).getPmOrgProfile().getOrgProfileId());
				finalResDTO = thresholdSO.getOperationalThresholds(thresholdreqDTO);
			}			
		
			if(!finalResDTO.getThresholdList().isEmpty()){
				getManagedBean().setUnplannedThresholdList(finalResDTO.getThresholdList());
				getManagedBean().setBudgetThresholdList(finalResDTO.getThresholdList());
			}
			if (resDTO.isSuccessful()) {
				FacesContext facesContext = FacesContext.getCurrentInstance();
				facesContext.addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, null,
						MessageUtils.getMessage(
						MessageConst.INFO_770093,
						MessageUtils.BUNDLE_COMMON_MESSAGES,
						new FacesScopes().getUserLocale())));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

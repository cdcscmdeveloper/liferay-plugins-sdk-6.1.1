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
import my.ep.biz.service.pm.common.categorycode.ICategoryCodeSO;
import my.ep.biz.service.pm.common.confgeneral.IConfigGeneralSO;
import my.ep.biz.service.pm.fee.settings.IFeeExemptionSO;
import my.ep.biz.service.pm.general.settings.kpi.IKPIStatusSO;
import my.ep.biz.service.pm.general.settings.rule.IRuleSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.cm.codification.CodificationReqDTO;
import my.ep.dto.cm.codification.CodificationResDTO;
import my.ep.dto.ct.dtl.ContractItemReqDTO;
import my.ep.dto.ct.dtl.ContractItemResDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
import my.ep.dto.pm.common.categorycode.CategoryCodeReqDTO;
import my.ep.dto.pm.common.categorycode.CategoryCodeResDTO;
import my.ep.dto.pm.common.confgeneral.ConfigGeneralReqDTO;
import my.ep.dto.pm.common.confgeneral.ConfigGeneralResDTO;
import my.ep.dto.pm.fee.settings.FeeExemptionReqDTO;
import my.ep.dto.pm.fee.settings.FeeExemptionResDTO;
import my.ep.dto.pm.general.settings.kpi.KPIReqDTO;
import my.ep.dto.pm.general.settings.kpi.KPIResDTO;
import my.ep.dto.pm.general.settings.rule.RuleReqDTO;
import my.ep.dto.pm.general.settings.rule.RuleResDTO;
import my.ep.persistence.entity.cm.catalog.CmItem;
import my.ep.persistence.entity.cm.catalog.CmType;
import my.ep.persistence.entity.cm.catalog.CmUnspsc;
import my.ep.persistence.entity.ct.CtContract;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.common.PmCategoryL3;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.fee.settings.PmFeeExemption;
import my.ep.persistence.entity.pm.general.settings.PmConfigRuleValue;
import my.ep.persistence.entity.sc.directpurchase.ScRequestItem;
import my.ep.util.MessageUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.pm.settings.RuleCatalogueManageBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

@ManagedBean(name = "ruleCatalogueBackingBean")
@ViewScoped
public class RuleCatalogueBackingBean extends
		BaseBackingBean<RuleCatalogueManageBean> {
	private static Logger log = Logger
			.getLogger(RuleCatalogueBackingBean.class);

	private ISearchBean<PmParameterDesc> docTypeSearchBean;

	public ISearchBean<PmParameterDesc> getDocTypeSearchBean() {
		if (this.docTypeSearchBean == null) {
			this.docTypeSearchBean = super.createSearchBean(
					PmParameterDesc.class, "docTypeId");
		}

		return this.docTypeSearchBean;
	}

	public RuleCatalogueBackingBean() {

	}

	public String preRenderView() {
		log.info("...........preRenderView............");
		IConfigGeneralSO cgeneralSO = null;
		try {
			cgeneralSO = (IConfigGeneralSO) BusinessDelegate.getInstance()
					.getServiceObject("pm", IConfigGeneralSO.class);
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
			IRuleSO ruleSO = (IRuleSO) BusinessDelegate.getInstance()
					.getServiceObject("pm", IRuleSO.class);
			RuleReqDTO reqRDTO = new RuleReqDTO();
			FacesScopes fs = new FacesScopes();
			reqRDTO.setLanguageCode(fs.getUserLocale().getLanguage());

			RuleResDTO resRDTO = ruleSO.getRuleTypes(reqRDTO);

			this.getManagedBean().setConfigRuleList(resRDTO.getRuleTypeList());
			this.getManagedBean().setSelectedRuleType(
					this.getManagedBean().getConfigRuleList().get(0));
			// this.getManagedBean().setTabActiveIndex(1);

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
			this.getManagedBean().setFeeExemptionsForComodity(
					resFDTO.getFeeExemptions());

			ICodificationSO unspscSO = (ICodificationSO) BusinessDelegate
					.getInstance()
					.getServiceObject("cm", ICodificationSO.class);
			CodificationReqDTO cReqDTO = new CodificationReqDTO();
			for (PmFeeExemption exemption : resFDTO.getFeeExemptions()) {
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
			System.out
					.println("...................resFDTO.getFeeExemptions().size()="
							+ resFDTO.getFeeExemptions().size());

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

//	public String onTabChange(String status) {
//		System.out.println("******status*******" + status);
//		IConfigGeneralSO cgeneralSO = null;
//		IRuleSO ruleSO = null;
//		// IKPIStatusSO kpiSO = null;
//		IFeeExemptionSO feeSO = null;
//		try {
//			cgeneralSO = (IConfigGeneralSO) BusinessDelegate.getInstance()
//					.getServiceObject("pm", IConfigGeneralSO.class);
//			ruleSO = (IRuleSO) BusinessDelegate.getInstance().getServiceObject(
//					"pm", IRuleSO.class);
//			// kpiSO = (IKPIStatusSO) BusinessDelegate.getInstance()
//			// .getServiceObject("pm", IKPIStatusSO.class);
//			feeSO = (IFeeExemptionSO) BusinessDelegate.getInstance()
//					.getServiceObject("pm", IFeeExemptionSO.class);
//
//			if (status.equalsIgnoreCase("general")) {
//
//				ConfigGeneralReqDTO reqDTO = new ConfigGeneralReqDTO();
//				ConfigGeneralResDTO resDTO = cgeneralSO
//						.getConfigGeneral(reqDTO);
//				this.getManagedBean().setSelectedConfigGeneral(
//						resDTO.getPmConfigGeneral());
//
//				this.getManagedBean().setTabActiveIndex(0);
//			} else if (status.equalsIgnoreCase("rule")) {
//
//				RuleReqDTO reqRDTO = new RuleReqDTO();
//				FacesScopes fs = new FacesScopes();
//				reqRDTO.setLanguageCode(fs.getUserLocale().getLanguage());
//
//				RuleResDTO resRDTO = ruleSO.getRuleTypes(reqRDTO);
//
//				this.getManagedBean().setConfigRuleList(
//						resRDTO.getRuleTypeList());
//				this.getManagedBean().setSelectedRuleType(
//						this.getManagedBean().getConfigRuleList().get(0));
//				this.getManagedBean().setTabActiveIndex(1);
//				// return "ruleConfig.xhtml";
//				// FacesContext fc = FacesContext.getCurrentInstance();
//				// fc.getApplication().getNavigationHandler()
//				// .handleNavigation(fc, null, "ruleConfig");
//			} else if (status.equalsIgnoreCase("fee")) {
//				this.getManagedBean().getFeeExemptionsForOrgAndItem().clear();
//				this.getManagedBean().getDeletedOrgAndItemFeeExemptions()
//						.clear();
//				FeeExemptionReqDTO reqDTO = new FeeExemptionReqDTO();
//
//				FeeExemptionResDTO resDTO = feeSO
//						.getFeeExemptionsForOrgAndItem(reqDTO);
//				for (PmFeeExemption exemption : resDTO.getFeeExemptions()) {
//					if (exemption.getItemId() == null) {// TODO: clear it before
//														// updating the fee list
//						exemption.setItemId(new CmItem());
//					}
//					this.getManagedBean().getFeeExemptionsForOrgAndItem()
//							.add(exemption);
//				}
//
//				this.getManagedBean().setTabActiveIndex(2);
//				this.getManagedBean().setFeeExemptionIndex(0);
//			}
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			cgeneralSO = null;
//			ruleSO = null;
//		}
//		return "settingConfig.xhtml";
//
//	}
//
//	public void onFeeTabChange(String status) {
//		System.out.println("******status*******" + status);
//		IFeeExemptionSO feeSO = null;
//		try {
//			feeSO = (IFeeExemptionSO) BusinessDelegate.getInstance()
//					.getServiceObject("pm", IFeeExemptionSO.class);
//
//			FeeExemptionReqDTO reqDTO = new FeeExemptionReqDTO();
//			FeeExemptionResDTO resDTO = null;
//			if (status.equalsIgnoreCase("byOrg")) {
//				this.getManagedBean().getFeeExemptionsForOrgAndItem().clear();
//				this.getManagedBean().getDeletedOrgAndItemFeeExemptions()
//						.clear();
//				resDTO = feeSO.getFeeExemptionsForOrgAndItem(reqDTO);
//
//				for (PmFeeExemption exemption : resDTO.getFeeExemptions()) {
//					if (exemption.getItemId() == null) {// TODO: clear it before
//														// updating the fee list
//						exemption.setItemId(new CmItem());
//					}
//					this.getManagedBean().getFeeExemptionsForOrgAndItem()
//							.add(exemption);
//				}
//
//				this.getManagedBean().setFeeExemptionIndex(0);
//			} else if (status.equalsIgnoreCase("byComodity")) {
//				this.getManagedBean().getFeeExemptionsForComodity().clear();
//				this.getManagedBean().getDeletedComodityFeeExemptions().clear();
//				resDTO = feeSO.getFeeExemptionsForComodity(reqDTO);
//				this.getManagedBean().setFeeExemptionsForComodity(
//						resDTO.getFeeExemptions());
//
//				ICodificationSO unspscSO = (ICodificationSO) BusinessDelegate
//						.getInstance().getServiceObject("cm",
//								ICodificationSO.class);
//				CodificationReqDTO cReqDTO = new CodificationReqDTO();
//				for (PmFeeExemption exemption : resDTO.getFeeExemptions()) {
//					cReqDTO.setUnspscCode(exemption.getUnspscId()
//							.getUnspscCode());
//					CodificationResDTO cResDTO = unspscSO
//							.retrieveUNSPSCByCommodityCode(cReqDTO);
//
//					if (cResDTO.isSuccessful()) {
//
//						CmUnspsc commodity = cResDTO.getCommodityObj();
//
//						exemption.setTypes(new ArrayList<CmType>());
//						for (CmType type : commodity.getCmType()) {
//							if (!StringUtils.isBlank(type.getTypeName())) {
//								exemption.getTypes().add(type);
//							}
//						}
//					}
//				}
//
//				this.getManagedBean().setFeeExemptionIndex(1);
//			} else if (status.equalsIgnoreCase("byContract")) {
//				this.getManagedBean().getFeeExemptionsForContract().clear();
//				this.getManagedBean().getDeletedContractFeeExemptions().clear();
//				resDTO = feeSO.getFeeExemptionsForContractAndReqItem(reqDTO);
//
//				for (PmFeeExemption exemption : resDTO.getFeeExemptions()) {
//					if (exemption.getRequestItemId() == null) {
//						exemption.setRequestItemId(new ScRequestItem());// TODO:
//																		// clear
//																		// it
//																		// before
//																		// updating
//																		// the
//																		// fee
//																		// list
//					}
//					this.getManagedBean().getFeeExemptionsForContract()
//							.add(exemption);
//				}
//
//				this.getManagedBean().setFeeExemptionIndex(2);
//				System.out
//						.println("...................resDTO.getFeeExemptions().size()="
//								+ resDTO.getFeeExemptions().size());
//			}
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			feeSO = null;
//		}
//		// return "settingConfig.xhtml";
//
//	}

	public void categoryCodesUpdate(int index,
			List<PmConfigRuleValue> ruleTypeValueList, PmCategoryL3 categoryL3) {
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
				String categoryL3Code = CategoryCodeResDTO.getCategoryL3()
						.getCategoryL3Code();
				String categoryL2Code = CategoryCodeResDTO.getCategoryL3()
						.getPmCategoryL2().getCategoryL2Code();
				String categoryL1Code = CategoryCodeResDTO.getCategoryL3()
						.getPmCategoryL2().getPmCategoryL1()
						.getCategoryL1Code();
				log.info("..................CategoryCodeResDTO.getCategoryL3().getCategoryL3Id()="
						+ CategoryCodeResDTO.getCategoryL3().getCategoryL3Id());
				log.info(".............categoryL3Code=" + categoryL3Code);
				log.info(".............categoryL2Code=" + categoryL2Code);
				log.info(".............categoryL1Code=" + categoryL1Code);
				ruleValue.setRuleValue(categoryL3Code + categoryL2Code
						+ categoryL1Code);
				this.getManagedBean().getSelectedRuleType()
						.getConfigRuleValues().remove(index);
				this.getManagedBean().getSelectedRuleType()
						.getConfigRuleValues().add(index, ruleValue);
			}
		} finally {

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
			reqDTO.setUnspscCode(exemption.getUnspscId().getUnspscCode());
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

	public void addMoreRuleValue() {
		log.info(".................addMoreRuleValue...................");
		PmConfigRuleValue newRuleValue = new PmConfigRuleValue();
		newRuleValue.setRuleType(this.getManagedBean().getSelectedRuleType());
		newRuleValue.setEffDate(new Date());
		if(this.getManagedBean().getSelectedRuleType().getRuleDataTypeId().getParameterCode().equals("C")){
			newRuleValue.setCategoryL3Id(new PmCategoryL3());
		}

		int index = this.getManagedBean().getSelectedRuleType()
				.getConfigRuleValues().size();
		this.getManagedBean().getSelectedRuleType().getConfigRuleValues()
				.add(index, newRuleValue);

	}

	public void deleteMoreRuleValue() {
		log.info(".................deleteMoreRuleValue...................");

		this.getManagedBean().getSelectedRuleType().getConfigRuleValues()
				.remove(this.getManagedBean().getSelectedRuleValue());

	}

	public void addMoreOrgAndItemFee() {
		log.info(".................addMoreOrgAndItemFee...................");
		PmFeeExemption newFeeExemption = new PmFeeExemption();
		// newFeeExemption.setFeeExemptionType("1");
		newFeeExemption.setEffDate(new Date());
		newFeeExemption.setItemId(new CmItem());
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

	public void addMoreComodityFee() {
		log.info(".................addMoreComodityFee...................");
		PmFeeExemption newFeeExemption = new PmFeeExemption();

		newFeeExemption.setEffDate(new Date());
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
		// newFeeExemption.setFeeExemptionType("2");
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

		IRuleSO ruleSO = null;
		try {
			ruleSO = (IRuleSO) BusinessDelegate.getInstance().getServiceObject(
					"pm", IRuleSO.class);
			RuleReqDTO reqDTO = new RuleReqDTO();

			reqDTO.setRuleTypeList(this.getManagedBean().getConfigRuleList());
			RuleResDTO resDTO = ruleSO.updateRuleValueList(reqDTO);
			reqDTO = new RuleReqDTO();
			FacesScopes fs = new FacesScopes();
			reqDTO.setLanguageCode(fs.getUserLocale().getLanguage());

			resDTO = ruleSO.getRuleTypes(reqDTO);

			this.getManagedBean().setConfigRuleList(resDTO.getRuleTypeList());
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

	public void saveFeeExemptionForOrgItem() {
		List<PmFeeExemption> exemptionList = new ArrayList<PmFeeExemption>();
		exemptionList.addAll(this.getManagedBean()
				.getFeeExemptionsForOrgAndItem());
		exemptionList.addAll(this.getManagedBean()
				.getDeletedOrgAndItemFeeExemptions());
		System.out.print("............................exemptionList.size="
				+ exemptionList.size());
		for (PmFeeExemption exemption : exemptionList) {
			if (exemption.getItemId().getItemId() == null) {// clear empty
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

	public void saveFeeExemptionForContract() {
		log.info("...........saveFeeExemptionForContract..................");
		List<PmFeeExemption> exemptionList = new ArrayList<PmFeeExemption>();
		exemptionList.addAll(this.getManagedBean()
				.getFeeExemptionsForContract());
		exemptionList.addAll(this.getManagedBean()
				.getDeletedContractFeeExemptions());
		for (PmFeeExemption exemption : exemptionList) {
			if (exemption.getRequestItemId().getRequestNoteItemId() == null) {// clear
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

	public void saveFeeExemptionForComodity() {
		List<PmFeeExemption> exemptionList = new ArrayList<PmFeeExemption>();
		exemptionList.addAll(this.getManagedBean()
				.getFeeExemptionsForComodity());
		exemptionList.addAll(this.getManagedBean()
				.getDeletedComodityFeeExemptions());
		for (PmFeeExemption exemption : exemptionList) {
			if (exemption.getFeeExemptionId() == null) {// set ExemptionType for
														// new FeeExemptions
				exemption.setFeeExemptionType("3");
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
			this.getManagedBean().setFeeExemptionsForComodity(
					resDTO.getFeeExemptions());

			ICodificationSO unspscSO = (ICodificationSO) BusinessDelegate
					.getInstance()
					.getServiceObject("cm", ICodificationSO.class);
			CodificationReqDTO cReqDTO = new CodificationReqDTO();
			for (PmFeeExemption exemption : this.getManagedBean()
					.getFeeExemptionsForComodity()) {
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

}

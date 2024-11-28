package my.ep.web.backingbean.sm.govtsellerregistration;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.model.SelectItem;

import my.ep.constant.MessageConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constants.CommonConstants;
import my.ep.dto.sm.mofRegistration.MofRegistrationReqDTO;
import my.ep.dto.sm.registration.RegistrationReqDTO;
import my.ep.dto.sm.registration.RegistrationResDTO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.dto.sm.search.SupplierSearchResDTO;
import my.ep.exception.EpException;
import my.ep.helper.EntityHelper;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.common.PmAddressType;
import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmCountry;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmDivision;
import my.ep.persistence.entity.pm.common.PmDocument;
import my.ep.persistence.entity.pm.common.PmFinancialOrg;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmSalutation;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.sm.SmAddress;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.persistence.entity.sm.SmCompanyAddress;
import my.ep.persistence.entity.sm.SmCompanyBasic;
import my.ep.persistence.entity.sm.SmGstSupplier;
import my.ep.persistence.entity.sm.SmGstSupplierDoc;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmPersonnelAddress;
import my.ep.persistence.entity.sm.SmSstSupplier;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.sm.SmSupplierBank;
import my.ep.persistence.entity.sm.SmSupplierBranch;
import my.ep.persistence.entity.sm.SmSupplierBranchBank;
import my.ep.type.pm.OrganizationType;
import my.ep.type.pm.RoleCode;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.sm.registration.RegistrationBackingBean;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.sm.govtsellerregistration.GovtSellerRegistrationManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.pm.helper.ParameterHelper;
import my.ep.web.util.sm.helper.SmApplicationUtil;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.util.sm.helper.SmRuleHelper;
import my.ep.web.util.sm.helper.SmServiceLookup;

import org.apache.commons.lang.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;

import org.apache.log4j.Logger;
//#28895_35501 Start - AUDIT TRAIL SM
import my.ep.biz.service.IAuditTrailSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.sm.AuditSupplierManagementConst;
import my.ep.util.CustomUtils;
import my.ep.web.backingbean.sm.audit.AuditSupplierManagementBankInformationBackingBean;
import my.ep.web.backingbean.sm.audit.AuditSupplierManagementBackingBeanTemplate;
import my.ep.web.util.lookupservice.BusinessDelegate;
//#28895_35501 End

@ManagedBean(name = "govtSellerBackingBean")
@RequestScoped
public class GovtSellerRegistrationBackingBean extends
		BaseBackingBean<GovtSellerRegistrationManagedBean> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6832474515383643924L;
	public static RegistrationBackingBean instance;
	private static Map<Long, Object> identityType;
	
	public static Logger log = Logger.getLogger(GovtSellerRegistrationBackingBean.class);

	/**
	 * Pre-render G2G page
	 */
	public void preRenderGovtSeller() {
		renewSesionObject();
		initAgencyTypes();
		getManagedBean().setFinancialOrgLs(
				getInstance().retrieveFinancialOrgList());
		getManagedBean().setTargetPage(
				SupplierManagementConst.GOVT_SELLER_TNC_PAGE);
		getManagedBean().setCountryLs(SmCommonUtil.getCountryList());
		getManagedBean().setSelectedBranch(new SmSupplierBranch());
		getManagedBean().getSelectedBranch().setSmAddress(
				(SmAddress) SerializationUtils.clone(SmApplicationUtil
						.setDefaultAddr(null)));
		getManagedBean().setSelectedBranches(new ArrayList<SmSupplierBranch>());
		assignValReqdToFalse();
		getManagedBean().setShowGst(true);
		//harris
		getManagedBean().setShowSst(true);
		getManagedBean().setShowBranchPointOfContact(false);
		getManagedBean().setSalutationItems(SmCommonUtil.getSalutationItems());
		getManagedBean().setApplType(SupplierManagementConst.ACC_GOVT_SELLER);
		SmCommonUtil.redirectToPage(
				"/xhtml/sm/govtSellerRegistration/govtSellerTncPage.xhtml", "",
				null, null);

	}

	/**
	 * Pre-render G2G maintenance page
	 */
	public void preRenderGovtSellerMaintenance() {
		renewSesionObject();
		SmCommonUtil.setMDCUserId();
		MofRegistrationReqDTO reqDTO = new MofRegistrationReqDTO();
		reqDTO.setSupplierId(SmApplicationUtil.getCurrentSupplier()
				.getSupplierId());
		getManagedBean().setSmAppl(
				SmServiceLookup.getMofRegistrationSO()
						.retrieveLatestSmApplWithChildBySupplierId(reqDTO)
						.getSmAppl());
		getManagedBean()
				.setSmSupp(getManagedBean().getSmAppl().getSmSupplier());
		assignValReqdToFalse();
		getManagedBean().setShowGst(true);
		//harris
		getManagedBean().setShowSst(true);
		getManagedBean().setFinancialOrgLs(
				getInstance().retrieveFinancialOrgList());
		getManagedBean().setCountryLs(SmCommonUtil.getCountryList());
		getManagedBean().setSelectedBranch(new SmSupplierBranch());
		getManagedBean().getSelectedBranch().setSmPersonnel(new SmPersonnel());
		getManagedBean().getSelectedBranch().setSmAddress(
				(SmAddress) SerializationUtils.clone(SmApplicationUtil
						.setDefaultAddr(null)));
		getManagedBean().setApplType(
				SupplierManagementConst.SUPP_TYPE_GOVT_TO_GOVT);
		//#34159 - START
		initCompanyAddressSelectItems(getManagedBean().getSmAppl()
				.getSmCompanyBasic().getSmCompanyAddresses().get(0)
				.getSmAddress(), "ACC_MAINTENANCE");
		//#34159 - END
		// init gst
		getInstance().initGstInfo(getManagedBean().getSmSupp(),
				getManagedBean(), true);
		//harris
		getInstance().initSstInfo(getManagedBean().getSmSupp(),
				getManagedBean(), true);
		getManagedBean().setSelectedBranches(new ArrayList<SmSupplierBranch>());
		getManagedBean().setShowBranchPointOfContact(true);
		initBankBranches();
		//START 17906
		if(this.getManagedBean().getSmSupp().getSmAppl() != null && this.getManagedBean().getSmSupp().getSmAppl().getSmSupplierBanks() != null){
			// #28895_35501 Start - AUDIT TRAIL SM
			this.getManagedBean().setOldBanks(
					CustomUtils.getInstance().deepCopyArrayList(this.getManagedBean().getSmSupp().getSmAppl().getSmSupplierBanks()));
			// #28895_35501 End
			StringBuilder sb = new StringBuilder();
			for(SmSupplierBank bank:this.getManagedBean().getSmSupp().getSmAppl().getSmSupplierBanks()){
				if(bank.getAccountNo() != null){
					sb = new StringBuilder();
					sb.append("<font color='red'>");
					bank.setHasActiveTransactionContractManagement(SmApplicationUtil.hasActiveTransactionForContractManagement(this.getManagedBean().getSmSupp(), bank.getAccountNo(),
							bank.getSmAppl().getSmSupplier().getSupplierId())); //#30693));
					bank.setHasActiveTransactionFulfilment(SmApplicationUtil.hasActiveTransactionForFulfilment(this.getManagedBean().getSmSupp(), bank.getAccountNo(),
							bank.getSmAppl().getSmSupplier().getSupplierId())); //#30693
					
					if(bank.isHasActiveTransactionContractManagement()){
						sb.append(SmCommonUtil.getMessage("lbl.sm.bankDtls.is.active.contract.tied",null));
						sb.append("<br/>");
					}
					if(bank.isHasActiveTransactionFulfilment()){
						sb.append(SmCommonUtil.getMessage("lbl.sm.bankDtls.is.other.active.contract.tied",null));
						sb.append("<br/>");
					}
					sb.append("</font>");
					bank.setDisplayMessageBank(sb.toString());
				}
			}
			
		}
		//END 17906
		SmCommonUtil
				.redirectToPage(
						"/xhtml/sm/govtSellerRegistration/govtSellerMaintenanceBasePage.xhtml",
						"", null, null);
	}

	/**
	 * Set HQ GST
	 * 
	 * @param SmSupplier
	 *            supplier
	 */
	private void setHqGst(SmSupplier supplier) {
		getManagedBean().setSmGstSupplier(null);
		if (!SmCommonUtil.isCollectionEmpty(supplier.getSmGstSuppliers())) {
			for (SmGstSupplier gst : supplier.getSmGstSuppliers()) {
				if (gst.getIsCurrent()
						&& StringUtils.isNotBlank(gst.getGstRegNo())
						&& gst.getBranchCode() == null) {
					getManagedBean().setSmGstSupplier(gst);
				}
			}
		}
	}

	/**
	 * Return to Sign Up page
	 * 
	 * @return String
	 */
	public String returnToSignUp() {
		return getInstance().returnToSignUp();
	}

	/**
	 * Return to tnc page
	 * 
	 * @return String
	 */
	public String returnToTnc() {
		return "/xhtml/sm/govtSellerRegistration/govtSellerTncPage.xhtml?faces-redirect=true";
	}

	public String navigateToSearchPage() {
		getManagedBean().setTargetPage(
				SupplierManagementConst.GOVT_SELLER_SEARCH_PAGE);
		return "/xhtml/sm/govtSellerRegistration/govtSellerBasePage.xhtml?faces-redirect=true";
	}

	/**
	 * Navigate to page
	 * 
	 * @param String
	 *            page
	 */
	public void navigateToPage(String page) {
		getManagedBean().setTargetPage(page);
	}

	/**
	 * Validate and initialized org profile
	 * 
	 * @param String
	 *            orgProfileId
	 */
	public void initOrgProfile(String orgProfileId) {
		RegistrationReqDTO reqDTO = new RegistrationReqDTO();
		reqDTO.setOrgProfileId(Long.parseLong(orgProfileId));
		PmOrgProfile orgProfile = SmServiceLookup.getRegistrationSO()
				.findOrgProfileById(reqDTO).getOrgProfile();
		SmSupplier supplier = new SmSupplier();
		getManagedBean().setDeletedNotActivatedSupplier(false);
		if (orgProfile != null) {
			if (orgProfile.getSmSupplier() != null
					&& orgProfile.getSmSupplier().getRecordStatusFlg() != null) {
				//#28765 - Start add OR DbValueConst.RECORD_STATUS_REJECTED
				if (orgProfile.getSmSupplier().getRecordStatusFlg() == DbValueConst.RECORD_STATUS_DELETED
						|| orgProfile.getSmSupplier().getRecordStatusFlg() == DbValueConst.RECORD_STATUS_REJECTED
						|| orgProfile.getSmSupplier().getRecordStatusFlg() == DbValueConst.RECORD_STATUS_CANCELLED) { //#34319
				//#28765 - End
					//#34319 - Start
					if(orgProfile.getSmSupplier().getRecordStatusFlg() == DbValueConst.RECORD_STATUS_CANCELLED){
						getManagedBean().setCancelApp(true);
					}
					//#34319 - End
					supplier = orgProfile.getSmSupplier();
					getManagedBean().setDeletedNotActivatedSupplier(true);
				} else {
					if(orgProfile
							.getSmSupplier().getSmAppl()!=null && orgProfile
							.getSmSupplier().getSmAppl().getSmPersonnels()!=null){
						SmPersonnel activatedPersonnel = getActivatedPersonnel(orgProfile
								.getSmSupplier().getSmAppl().getSmPersonnels());
						if (orgProfile.getSmSupplier().getRecordStatusFlg() == DbValueConst.RECORD_STATUS_SUSPENDED) {
							// Selected agency account already deactivated. Please
							// reactivate the account
							throw new EpException(MessageConst.ERROR_722625);
						} else if (orgProfile.getSmSupplier().getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE) {
							if (orgProfile.getSmSupplier().getSmAppl() != null
									&& !SmCommonUtil.isCollectionEmpty(orgProfile
											.getSmSupplier().getSmAppl()
											.getSmPersonnels())) {

								if (activatedPersonnel != null) {
									// Selected agency already had account. Please
									// contact:- Sir/Madam: {0} / Mobile No.: {1}
									throw new EpException(
											MessageConst.ERROR_722623,
											new String[] {
													activatedPersonnel.getName(),
													activatedPersonnel
															.getMobileFull() });
								} else {
									// Selected agency already had account but has
									// not
									// activated the account. Please activate the
									// account using activation link that had been
									// sent
									// to the user's email address.
									throw new EpException(MessageConst.ERROR_722624);
								}
							}
						} else {
							if (orgProfile.getSmSupplier().getRecordStatusFlg() == DbValueConst.RECORD_STATUS_IN_PROGRESS) {
								if (activatedPersonnel != null) {
									// Selected agency already had account. Please
									// contact:- Sir/Madam: {0} / Mobile No.: {1}
									throw new EpException(
											MessageConst.ERROR_722623,
											new String[] {
													activatedPersonnel.getName(),
													activatedPersonnel
															.getMobileFull() });
								} else {
									// Selected agency already had account but has
									// not
									// activated the account. Please activate the
									// account using activation link that had been
									// sent
									// to the user's email address.
									throw new EpException(MessageConst.ERROR_722624);
								}
							} else {
								// Unknown problem occurred. Invalid or missing
								// data.
								// Please
								// contact administrator.
								throw new EpException(MessageConst.ERROR_722505);
							}
						}
					}else{
						// Unknown problem occurred. Invalid or missing
						// data.
						// Please
						// contact administrator.
						throw new EpException(MessageConst.ERROR_722505);
					}
				}
			}
		} else {
			// Unknown problem occurred. Invalid or missing data. Please contact
			// administrator.
			throw new EpException(MessageConst.ERROR_722505);
		}

		initSupplier(orgProfile, supplier);
		initGst();
		initBankBranches();
		navigateToPage(SupplierManagementConst.GOVT_SELLER_ACCOUNT_INFO_PAGE);

	}

	/**
	 * Validate GST registration NO.
	 */
	public void validateGstRegNo() {
		String gstNo = this.getManagedBean().getSmGstSupplier().getGstRegNo();
		this.getManagedBean().getSmGstSupplier().setIsCurrent(true);

		SmCommonUtil.validateGstRegNoUniqueness(getManagedBean().getSmSupp()
				.getRegNo(), gstNo);

		if (gstNo != null && gstNo.length() > 0) {
			if (!SmCommonUtil.validateGstRegNo(gstNo)) {
				this.getManagedBean().setGstFlag(false);
				throw new EpException(MessageConst.ERROR_721027);
			} else {
				this.getManagedBean().setGstFlag(true);
			}
		} else {
			this.getManagedBean().setGstFlag(false);
		}
	}
	
	//harris
	public void validateSstRegNo() {
		String sstNo = this.getManagedBean().getSmSstSupplier().getSalestaxregno();
		this.getManagedBean().getSmSstSupplier().setIsCurrent(true);

		SmCommonUtil.validateSstRegNoUniqueness(getManagedBean().getSmSupp()
				.getRegNo(), sstNo);

		if (sstNo != null && sstNo.length() > 0) {
			if (!SmCommonUtil.validateSstRegNo(sstNo)) {
				this.getManagedBean().setSstFlag(false);
				throw new EpException(MessageConst.ERROR_721027);
			} else {
				this.getManagedBean().setSstFlag(true);
			}
		} else {
			this.getManagedBean().setSstFlag(false);
		}
	}
	
	//harris
	public void validateSstServRegNo() {
		String sstServNo = this.getManagedBean().getSmSstSupplier().getServtaxregno();
		this.getManagedBean().getSmSstSupplier().setIsCurrent(true);

		SmCommonUtil.validateSstServRegNoUniqueness(getManagedBean().getSmSupp()
				.getRegNo(), sstServNo);

		if (sstServNo != null && sstServNo.length() > 0) {
			if (!SmCommonUtil.validateSstServRegNo(sstServNo)) {
				this.getManagedBean().setSstFlag(false);
				throw new EpException(MessageConst.ERROR_721027);
			} else {
				this.getManagedBean().setSstFlag(true);
			}
		} else {
			this.getManagedBean().setSstFlag(false);
		}
	}

	/**
	 * FIle upload Handler
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void handleFileUpload(FileUploadEvent event) throws Exception {
		getInstance().handleFileUpload(
				getManagedBean().getSmGstSupplier().getSmGstSupplierDocs(),
				getManagedBean().getSelectedGstSupplierDoc(), event);
	}

	/**
	 * remove uploaded gst attachment
	 */
	public void removeGstUploadedFile() {
		SmGstSupplierDoc gstDoc = getManagedBean().getSelectedGstSupplierDoc();
		gstDoc.setFileName(null);
		gstDoc.setFilePath(null);

	}

	/**
	 * add new row to Supplier Bank
	 */
	public void addSupplierBank() {
		if (this.getManagedBean().getSmAppl().getSmSupplierBanks() == null) {
			this.getManagedBean().getSmAppl()
					.setSmSupplierBanks(new ArrayList<SmSupplierBank>());
		}
		SmSupplierBank smSuppBank = new SmSupplierBank();
		smSuppBank.setPmFinancialOrg(new PmFinancialOrg());
		smSuppBank.setSmAppl(this.getManagedBean().getSmAppl());
		smSuppBank.setAccountPurpose("P");
		smSuppBank.setBankCode(new Long(getMaxTmpBankCode() + 1));
		this.getManagedBean().getSmAppl().getSmSupplierBanks().add(smSuppBank);
		initBankBranches();
	}

	/**
	 * remove selected Supplier Bank
	 */
	public void removeSupplierBank() {
		if (this.getManagedBean().getSelectedSuppBanks() != null
				&& this.getManagedBean().getSelectedSuppBanks().length > 0) {
			for (SmSupplierBank suppBank : this.getManagedBean()
					.getSelectedSuppBanks()) {
				this.getManagedBean().getSmAppl().getSmSupplierBanks()
						.remove(suppBank);
			}
		}
	}

	/**
	 * set Selected Gst Doc
	 * 
	 * @param gstSupplierDoc
	 */
	public void setSelectedGstDoc(SmGstSupplierDoc gstSupplierDoc) {
		this.getManagedBean().setSelectedGstSupplierDoc(gstSupplierDoc);
	}

	public boolean isShowNewChanges() {
		return getInstance().isShowNewChanges();
	}

	public boolean isGmapDisabled() {
		return getInstance().isGmapDisabled();
	}

	/**
	 * Assign selected branch object to managed bean for editing the
	 * information.
	 * 
	 * @param branchBean
	 *            - SmSupplierBranch object
	 * @param index
	 *            - int
	 */
	public void assignSelBranch(SmSupplierBranch branchBean) {
		initBranchAddressSelectItems(branchBean.getSmAddress());
		if (branchBean.getSmPersonnel() == null
				&& getManagedBean().isShowBranchPointOfContact()) {
			branchBean.setSmPersonnel(new SmPersonnel());
		}
		if (branchBean.getGst() == null) {
			branchBean.setGst(new SmGstSupplier());
			branchBean.getGst().setSmGstSupplierDocs(
					new ArrayList<SmGstSupplierDoc>());
		}
		SmApplicationUtil.initGstSuppDocs(branchBean.getGst());
		getManagedBean().setSelectedBranch(
				(SmSupplierBranch) SerializationUtils.clone(branchBean));
		assignValReqdToTrue();
		RequestContext.getCurrentInstance().execute("banchDialog.show()");

	}

	/**
	 * Add or create a new SmSupplierBranch object
	 */
	public void assignNewBranchBean() {
		SmSupplierBranch br = new SmSupplierBranch();
		br.setRevNo(1);
		br.setSmAddress((SmAddress) SerializationUtils.clone(SmApplicationUtil
				.setDefaultAddr(null)));
		br.setSmAppl(getManagedBean().getSmAppl());
		if (getManagedBean().isShowBranchPointOfContact()) {
			br.setSmPersonnel(new SmPersonnel());
		}
		getManagedBean().setBranchCompanyStates(
				SmCommonUtil.getStateList(getManagedBean().getSelectedBranch()
						.getSmAddress().getPmCountry().getCountryId()));
		getManagedBean().setBranchCompanyDistricts(new ArrayList<SelectItem>());
		getManagedBean().setBranchCompanyCities(new ArrayList<SelectItem>());
		getManagedBean().setSelectedBranch(br);
		SmApplicationUtil.addNewBranchGst(getManagedBean().getSmGstSupplier(),
				getManagedBean().getSelectedBranch());
		assignValReqdToTrue();
		RequestContext.getCurrentInstance().execute("banchDialog.show()");
	}

	/**
	 * Cancel add or update functionality and close the supplier branch
	 * information dialog
	 */
	public void assignCancelBranchBean() {
		if (getManagedBean().getSelectedBranch() != null
				&& getManagedBean().getSelectedBranch().getGst() != null) {
			getManagedBean().getSelectedBranch().setGst(null);
		}
		assignValReqdToFalse();
		initGst();
		RequestContext.getCurrentInstance().reset(":form:banchDialog");
		RequestContext.getCurrentInstance().execute("banchDialog.hide()");
	}

	/**
	 * Delete or remove selected branches from the SmSupplierBranch list of the
	 * application.
	 */
	public void deleteSelectedBranches() {
		if (getManagedBean().getSelectedBranches().size() > 0) {
			for (SmSupplierBranch brn : getManagedBean().getSelectedBranches()) {
				getManagedBean().getSmAppl().getSmSupplierBranches()
						.remove(brn);
			}
			getManagedBean().getSelectedBranches().clear();
		} else {
			throw new EpException(MessageConst.ERROR_722534,
					new String[] { "Company Branch" });
		}
		initBankBranches();
	}

	/**
	 * Add or update selected branch info to SmSupplierBranch list of the
	 * application.
	 */
	public void addOrUpdateBranchToSmAppl() {
		int i = 0;

		if (getManagedBean().getSelectedBranch().isSameAsHq()) {
			copyHqGstToBranch(getManagedBean().getSelectedBranch());
		}
		getManagedBean()
				.getSelectedBranch()
				.getSmAddress()
				.setPmState(
						SmCommonUtil.getInstance().getStateById(
								getManagedBean().getSelectedBranch()
										.getSmAddress().getPmState()
										.getStateId()));
		//#13320 - added division - start
		if(getManagedBean().getSelectedBranch().getSmAddress().getPmDivision() != null){
			getManagedBean()
				.getSelectedBranch()
				.getSmAddress()
				.setPmDivision(
						SmCommonUtil.getInstance().getDivisionByUsingId(
								getManagedBean().getSelectedBranch()
									.getSmAddress().getPmDivision()
									.getDivisionId()));
		}
		//#13320 - added division - end
		getManagedBean()
				.getSelectedBranch()
				.getSmAddress()
				.setPmDistrict(
						SmCommonUtil.getInstance().getDistrictById(
								getManagedBean().getSelectedBranch()
										.getSmAddress().getPmDistrict()
										.getDistrictId()));
		getManagedBean()
				.getSelectedBranch()
				.getSmAddress()
				.setPmCity(
						SmCommonUtil.getInstance()
								.getCityById(
										getManagedBean().getSelectedBranch()
												.getSmAddress().getPmCity()
												.getCityId()));

		if (getManagedBean().getSelectedBranch().getBranchCode() != null) {
			for (SmSupplierBranch br : getManagedBean().getSmAppl()
					.getSmSupplierBranches()) {
				if (br.getBranchCode().compareTo(
						getManagedBean().getSelectedBranch().getBranchCode()) == 0) {
					break;
				}
				i++;
			}
			getManagedBean().getSmAppl().getSmSupplierBranches()
					.set(i, getManagedBean().getSelectedBranch());
		} else {
			getManagedBean().getSelectedBranch().setBranchCode(
					new Long(getMaxTmpBrCode() + 1));
			getManagedBean().getSmAppl().getSmSupplierBranches()
					.add(getManagedBean().getSelectedBranch());
		}
		initBankBranches();
		assignValReqdToFalse();
		RequestContext.getCurrentInstance().execute("banchDialog.hide()");
	}

	/**
	 * Delete GST
	 * 
	 * @param SmGstSupplier
	 *            gst
	 */
	public void deleteGst(SmGstSupplier gst) {
		gst.setGstRegNo(null);
		gst.setGstEffDate(null);
		gst.setGstEndDate(null);
		gst.setSmGstSupplierDocs(new ArrayList<SmGstSupplierDoc>());
		SmApplicationUtil.initGstSuppDocs(gst);
	}

	/**
	 * Copy hq gst info to branch
	 * 
	 * @param SmSupplierBranch
	 *            branch
	 */
	public void copyHqGstToBranch(SmSupplierBranch branch) {
		if (branch.getGst() == null) {
			branch.setGst(new SmGstSupplier());
			SmApplicationUtil.initGstSuppDocs(branch.getGst());
		}
		SmCommonUtil.copyHqGstToBranch(getManagedBean().getSmGstSupplier(),
				branch.getGst(), branch.isSameAsHq(), branch.getBranchCode());
	}

	public void handleGstSupDocFileAttachment(FileUploadEvent event) {
		if (event.getFile() != null) {
			if (getManagedBean().getSelectedGstSupplierDoc() != null) {

				String tempPath = String.valueOf((new Date()).getTime());
				String filePath = SmCommonUtil
						.getUploadFilePath(SupplierManagementConst.GST_FILE_PATH)
						+ tempPath + "/";
				String fileName = String.valueOf((new Date()).getTime()) + "_"
						+ event.getFile().getFileName();

				// init suppDocFile (currently only default one file)
				SmGstSupplierDoc doc = getManagedBean()
						.getSelectedGstSupplierDoc();
				doc.setFileName(fileName);
				doc.setFilePath(filePath);

				// set selected doc to main doc list
				setSuppDocFileAttachment(doc);

				// upload file
				commonUploadFile(event, filePath);
			}
		}

	}

	/**
	 * Assign selected GST documents
	 * 
	 * @param SmGstSupplierDoc
	 *            doc
	 */
	public void assignSelectedGstDoc(SmGstSupplierDoc doc) {
		getManagedBean().setSelectedGstSupplierDoc(doc);
	}

	/**
	 * Set state drop down list when on change country drop down based on
	 * address type
	 * 
	 * @param PmCountry
	 *            country
	 * @param String
	 *            addrType
	 */
	public void onChangeCountry(PmCountry country, String addrType) {
		if (addrType != null && addrType.equals("COMPANY_ADDRESS")) {
			if (country != null) {
				getManagedBean().setBusinessStateLs(
						SmCommonUtil.getStateList(country.getCountryId()));
				getManagedBean().setBusinessDistrictLs(
						new ArrayList<SelectItem>());
				getManagedBean().setBusinessCityLs(new ArrayList<SelectItem>());
			} else {
				getManagedBean()
						.setBusinessStateLs(new ArrayList<SelectItem>());
				getManagedBean().setBusinessDistrictLs(
						new ArrayList<SelectItem>());
				getManagedBean().setBusinessCityLs(new ArrayList<SelectItem>());
			}
		} else if (addrType != null
				&& addrType.equals("USER_PERMANENT_ADDRESS")) {
			if (country != null) {
				getManagedBean().setPermenantStateLs(
						SmCommonUtil.getStateList(country.getCountryId()));
				getManagedBean().setPermenantDistrictLs(
						new ArrayList<SelectItem>());
				getManagedBean()
						.setPermenantCityLs(new ArrayList<SelectItem>());
			} else {
				getManagedBean().setPermenantStateLs(
						new ArrayList<SelectItem>());
				getManagedBean().setPermenantDistrictLs(
						new ArrayList<SelectItem>());
				getManagedBean()
						.setPermenantCityLs(new ArrayList<SelectItem>());
			}
		} else if (addrType != null
				&& addrType.equals("USER_RESIDENTIAL_ADDRESS")) {
			if (country != null) {
				getManagedBean().setResidentialStateLs(
						SmCommonUtil.getStateList(country.getCountryId()));
				getManagedBean().setResidentialDistrictLs(
						new ArrayList<SelectItem>());
				getManagedBean().setResidentialCityLs(
						new ArrayList<SelectItem>());
			} else {
				getManagedBean().setResidentialStateLs(
						new ArrayList<SelectItem>());
				getManagedBean().setResidentialDistrictLs(
						new ArrayList<SelectItem>());
				getManagedBean().setResidentialCityLs(
						new ArrayList<SelectItem>());
			}
		} else if (addrType != null && addrType.equals("BRANCH_ADDRESS")) {
			if (country != null) {
				//#34159 - START
				if(country.getCountryId() != null){
					getManagedBean().setBranchCompanyStates(SmCommonUtil.getStateList(country.getCountryId()));
				} else {
					getManagedBean().setBranchCompanyStates(new ArrayList<SelectItem>());
				}
				getManagedBean().setBranchCompanyDivisions(new ArrayList<SelectItem>());
				getManagedBean().setBranchCompanyDistricts(new ArrayList<SelectItem>());
				getManagedBean().setBranchCompanyCities(new ArrayList<SelectItem>());
				getManagedBean().getSelectedBranch().getSmAddress().setPmState(new PmState());
				getManagedBean().getSelectedBranch().getSmAddress().setPmDivision(new PmDivision());
				getManagedBean().getSelectedBranch().getSmAddress().setPmDistrict(new PmDistrict());
				getManagedBean().getSelectedBranch().getSmAddress().setPmCity(new PmCity());
				//#34159 - END
			} else {
				getManagedBean().setBranchCompanyStates(
						new ArrayList<SelectItem>());
				getManagedBean().setBranchCompanyDistricts(
						new ArrayList<SelectItem>());
				getManagedBean().setBranchCompanyCities(
						new ArrayList<SelectItem>());
			}
		}
	}

	/**
	 * Set district drop down list when on change state drop down based on
	 * address type
	 * 
	 * @param PmState
	 *            state
	 * @param String
	 *            addrType
	 */
	public void onChangeState(PmState state, String addrType) {//13320
		if (addrType != null && addrType.equals("COMPANY_ADDRESS")) {
			Long State=this.getManagedBean().getSmAppl().getSmCompanyBasic().getSmCompanyAddresses().get(0).getSmAddress().getPmState().getStateId();
			PmState states = (SmCommonUtil.getInstance().getStateById(State));
			if(states !=null && states.getStateCode().equalsIgnoreCase(SupplierManagementConst.SARAWAK_CODE)) {
				//#13320 - null check
				if(this.getManagedBean().getSmAppl().getSmCompanyBasic().getSmCompanyAddresses().get(0).getSmAddress().getPmDivision() == null)
					this.getManagedBean().getSmAppl().getSmCompanyBasic().getSmCompanyAddresses().get(0).getSmAddress().setPmDivision(new PmDivision());
				List<SelectItem> divisionLs = SmCommonUtil.getDivisionList(State);
				this.getManagedBean().setBusinessDivisionLs(divisionLs);
				getManagedBean().setBusinessDistrictLs(new ArrayList<SelectItem>());
				getManagedBean().setBusinessCityLs(null);
				getManagedBean().getSmAppl().getSmCompanyBasic().getSmCompanyAddresses().get(0).getSmAddress().getPmState().setAdoptDivision(true);
			}else{
				getManagedBean().getSmAppl().getSmCompanyBasic().getSmCompanyAddresses().get(0).getSmAddress().getPmState().setAdoptDivision(false);
				//#13320 - remove division for non-sarawak
				this.getManagedBean().getSmAppl().getSmCompanyBasic().getSmCompanyAddresses().get(0).getSmAddress().setPmDivision(null);
				//#34159 - START - Reset District when change state
				if(this.getManagedBean().getSmAppl().getSmCompanyBasic().getSmCompanyAddresses().get(0).getSmAddress().getPmDistrict().getDistrictId() !=null){
					this.getManagedBean().getSmAppl().getSmCompanyBasic().getSmCompanyAddresses().get(0).getSmAddress().getPmDistrict().setDistrictId(null);
				}
				//#34159 - END
			} 
			if(states !=null && states.getAdoptDivision()) {
				getManagedBean().setDivisionBox(false);	
			}else {
				getManagedBean().setDivisionBox(true);
				List<SelectItem> districtLs = SmCommonUtil.getDistrictList(State);
				getManagedBean().setBusinessDistrictLs(
						districtLs);
				getManagedBean().setBusinessCityLs(new ArrayList<SelectItem>());
			}
		} else if (addrType != null
				&& addrType.equals("USER_PERMANENT_ADDRESS")) {
			//13320-Start
			Long State=getManagedBean().getSmPermenantAdd().getPmState().getStateId();
			PmState states = (SmCommonUtil.getInstance().getStateById(State));
			if (states != null && states.getStateCode().equalsIgnoreCase(SupplierManagementConst.SARAWAK_CODE)) {
				//#13320 - null check
				if(this.getManagedBean().getSmPermenantAdd().getPmDivision() == null)
					this.getManagedBean().getSmPermenantAdd().setPmDivision(new PmDivision());
				
				List<SelectItem> divisionLs = SmCommonUtil.getDivisionList(State);
				this.getManagedBean().setPermenantDivisionLs(divisionLs);//13320-End
				getManagedBean().setPermenantDistrictLs(
						new ArrayList<SelectItem>());
				getManagedBean()
						.setPermenantCityLs(null);
				
				getManagedBean().getSmPermenantAdd().getPmState().setAdoptDivision(true);
			}else{
				//#13320 - remove division for non-sarawak
				this.getManagedBean().getSmPermenantAdd().setPmDivision(null);
				getManagedBean().getSmPermenantAdd().getPmState().setAdoptDivision(false);
				//#34159 - START - Reset District when change state
				if(getManagedBean().getSmPermenantAdd().getPmDistrict().getDistrictId() !=null){
					getManagedBean().getSmPermenantAdd().setPmDistrict(new PmDistrict());
				}
				//#34159 - END

			} if(states !=null && states.getAdoptDivision()) {//13320
				getManagedBean().setDivisionBox(false);	
			}else {
				getManagedBean().setDivisionBox(true);
				List<SelectItem> districtLs = SmCommonUtil.getDistrictList(State);
				getManagedBean().setPermenantDistrictLs(districtLs);
				getManagedBean()
						.setPermenantCityLs(new ArrayList<SelectItem>());
			}
		} else if (addrType != null
				&& addrType.equals("USER_RESIDENTIAL_ADDRESS")) {
			//13320-Start
			Long State=getManagedBean().getSmResAdd().getPmState().getStateId();
			PmState states = (SmCommonUtil.getInstance().getStateById(State));
			if (states != null && states.getStateCode().equalsIgnoreCase(SupplierManagementConst.SARAWAK_CODE)) {
				//#13320 - null check
				if(this.getManagedBean().getSmResAdd().getPmDivision() == null)
					this.getManagedBean().getSmResAdd().setPmDivision(new PmDivision());
				List<SelectItem> divisionLs = SmCommonUtil.getDivisionList(State);
				this.getManagedBean().setResidentialDivisionLs(divisionLs);//13320-End
				getManagedBean().setResidentialDistrictLs(new ArrayList<SelectItem>());
				getManagedBean().setResidentialCityLs(null);
				
				getManagedBean().getSmResAdd().getPmState().setAdoptDivision(true);
			}else{
				//#34159 - remove division for non-Sarawak
				this.getManagedBean().getSmResAdd().setPmDivision(null);
				getManagedBean().getSmResAdd().getPmState().setAdoptDivision(false);
				//#34159 - START - Reset District when change state
				if(getManagedBean().getSmResAdd().getPmDistrict().getDistrictId() !=null){
					getManagedBean().getSmResAdd().setPmDistrict(new PmDistrict());
				}
				//#34159 - END

			}if(states !=null && states.getAdoptDivision()) {//13320
				getManagedBean().setDivisionBox(false);	
			} else {
				getManagedBean().setDivisionBox(true);
				List<SelectItem> districtLs = SmCommonUtil.getDistrictList(State);
				getManagedBean().setResidentialDistrictLs(
						districtLs);
				getManagedBean().setResidentialCityLs(
						new ArrayList<SelectItem>());
			}
		} else if (addrType != null && addrType.equals("BRANCH_ADDRESS")) {
			//13320-Start
			Long stateId = state.getStateId();
			PmState states = (SmCommonUtil.getInstance().getStateById(stateId));
			if (states != null && states.getStateCode().equalsIgnoreCase(SupplierManagementConst.SARAWAK_CODE)) {
				//#13320 - null check
				if(this.getManagedBean().getSelectedBranch().getSmAddress().getPmDivision() == null)
					this.getManagedBean().getSelectedBranch().getSmAddress().setPmDivision(new PmDivision());
				List<SelectItem> divisionLs = SmCommonUtil.getDivisionList(stateId);
				this.getManagedBean().setBranchCompanyDivisions(divisionLs);
				getManagedBean().setBranchCompanyDistricts(new ArrayList<SelectItem>());
				getManagedBean().setBranchCompanyCities(null);
				getManagedBean().getSelectedBranch().getSmAddress().getPmState().setAdoptDivision(true);
			}else{
				//#13320 - remove division for non-sarawak
				this.getManagedBean().getSelectedBranch().getSmAddress().setPmDivision(null);
				getManagedBean().getSelectedBranch().getSmAddress().getPmState().setAdoptDivision(false);
				//#34159 - START - Reset District when change state
				if(this.getManagedBean().getSelectedBranch().getSmAddress().getPmDistrict().getDistrictId() !=null){
					this.getManagedBean().getSelectedBranch().getSmAddress().setPmDistrict(new PmDistrict());
				}
				//#34159 - END

			} if(states !=null && states.getAdoptDivision()) {//13320
				getManagedBean().setDivisionBox(false);	
			}else {
				getManagedBean().setDivisionBox(true);
				List<SelectItem> districtLs = SmCommonUtil.getDistrictList(stateId);
				getManagedBean().setBranchCompanyDistricts(districtLs);
				getManagedBean().setBranchCompanyCities(
						new ArrayList<SelectItem>());
			}
		}
	}

	/**
	 * Set city drop down list when on change district drop down based on
	 * address type
	 * 
	 * @param PmDistrict
	 *            district
	 * @param String
	 *            addrType
	 */
	public void onChangeDistrict(PmDistrict district, String addrType) {
		if (addrType != null && addrType.equals("COMPANY_ADDRESS")) {
			if (district != null) {
				//#34159 - START
				if(district.getDistrictId() != null){
					getManagedBean().setBusinessCityLs(SmCommonUtil.getCityList(district.getDistrictId()));
				} else {
					this.getManagedBean().setBusinessCityLs(new ArrayList<SelectItem>());
				}
				this.getManagedBean().getSmAppl().getSmCompanyBasic().getSmCompanyAddresses().get(0).getSmAddress().setPmCity(new PmCity());
				//#34159 - END
			} else {
				getManagedBean().setBusinessCityLs(new ArrayList<SelectItem>());
			}
		} else if (addrType != null
				&& addrType.equals("USER_PERMANENT_ADDRESS")) {
			if (district != null) {
				getManagedBean().setPermenantCityLs(
						SmCommonUtil.getCityList(district.getDistrictId()));
			} else {
				getManagedBean()
						.setPermenantCityLs(new ArrayList<SelectItem>());
			}
		} else if (addrType != null
				&& addrType.equals("USER_RESIDENTIAL_ADDRESS")) {
			if (district != null) {
				getManagedBean().setResidentialCityLs(
						SmCommonUtil.getCityList(district.getDistrictId()));
			} else {
				getManagedBean().setResidentialCityLs(
						new ArrayList<SelectItem>());
			}
		} else if (addrType != null && addrType.equals("BRANCH_ADDRESS")) {
			if (district != null) {
				//#34159 - START
				if(district.getDistrictId() != null){
					getManagedBean().setBranchCompanyCities(SmCommonUtil.getCityList(district.getDistrictId()));
				} else {
					this.getManagedBean().setBranchCompanyCities(new ArrayList<SelectItem>());
				}
				this.getManagedBean().getSelectedBranch().getSmAddress().setPmCity(new PmCity());
				//#34159 - END
			} else {
				getManagedBean().setBranchCompanyCities(
						new ArrayList<SelectItem>());
			}
		}
	}

	/**
	 * Proceed to user registration
	 */
	public void proceedToFirstUserRegistration() {
		validateHqBranchBank();
		PmParameter icNoType = getParameterByTypeAndCode(
				ParameterTypeConst.IDENTIFICATION_TYPES, "I");
		SmAddress defaultAddr = SmApplicationUtil.setDefaultAddr(null);
		if (this.getManagedBean().getSmAppl().getSmSupplierBanks().size() < 1) {
			throw new EpException(MessageConst.ERROR_721002);

		}
		getInstance().validateBankInfo(
				this.getManagedBean().getSmAppl().getSmSupplierBanks());
		getManagedBean().getSmAppl().getSmCompanyBasic().setWithFederal(true);
		if (getManagedBean().getSmPersonnel() == null) {
			getManagedBean().setSmPersonnel(new SmPersonnel());
		}
		if (getManagedBean().getSmPersonnel().getTitle() == null) {
			getManagedBean().getSmPersonnel().setTitle(new PmSalutation());
		}
		if (getManagedBean().getSmPersonnel().getPmCountry() == null
				|| getManagedBean().getSmPersonnel().getPmCountry()
						.getCountryId() == null) {
			getManagedBean()
					.getSmPersonnel()
					.setPmCountry(
							SmCommonUtil
									.retrieveCountryByCountryCode(SupplierManagementConst.COUNTRY_MALAYSIA_CODE));
		}
		if (getManagedBean().getSmPersonnel().getIdentificationTypeId() == null) {
			getManagedBean().getSmPersonnel().setIdentificationTypeId(
					icNoType.getParameterId());
		}
		if (getManagedBean().getSmPermenantAdd() == null) {
			getManagedBean().setSmPermenantAdd(
					(SmAddress) SerializationUtils.clone(defaultAddr));
		}
		if (getManagedBean().getSmResAdd() == null) {
			getManagedBean().setSmResAdd(
					(SmAddress) SerializationUtils.clone(defaultAddr));
		}
		
		//#27356
		if (StringUtils.isBlank(this.getManagedBean().getSmAppl().getSmCompanyBasic().getSmCompanyAddresses().get(0)
				.getSmAddress().getMapLatitude())
				|| StringUtils.isBlank(this.getManagedBean().getSmAppl().getSmCompanyBasic().getSmCompanyAddresses()
						.get(0).getSmAddress().getMapLongitude())) {
			throw new EpException(SmCommonUtil.getMessage("lbl.sm.coordinate.required.msg",
					new String[] { SmCommonUtil.getMessage("lbl.sm.coordinate.company.info", null) }));
		}

		getManagedBean().getSmPersonnel().setIdentificationType(icNoType);
		initUserPermanentAddressSelectItems(getManagedBean()
				.getSmPermenantAdd());
		initUserResidentialAddressSelectItems(getManagedBean().getSmResAdd());
		initIdentificationTypes();
		navigateToPage(SupplierManagementConst.GOVT_SELLER_USER_INFO_PAGE);
	}

	/**
	 * Submit registration
	 */
	public void submitRegistration() {
		validateBeforeSubmit();
		setSmPersonnelBeforeSubmit();
		setSmSupplierBankBeforeSubmit();
		setSmSupplierBranchBeforeSubmit();
		setSupplierGstBeforeSubmit();
		//harris
		setSupplierSstBeforeSubmit();
		setSmApplBeforeSubmit();
		if (SmServiceLookup.getRegistrationSO()
				.createSupplier(getRegistrationReqDTO()).isSuccessful()) {
			//14623 - start : tukar ayat
			SmCommonUtil.redirectToPage("/xhtml/sm/onlinereg/outputMsg",
					MessageConst.INFO_722685, new String[] {
							getManagedBean().getSmPersonnel().getEmail(),
							SmRuleHelper.getInstance().getEmailActivationDays()
									+ "" }, null);
			//14623 - end
		} else {
			throw new EpException(MessageConst.ERROR_721006);
		}
	}

	/**
	 * Submit maintenance
	 */
	public void submitMaintenance() {
		//#34771 - Start
		log.info("#34771 - checking time");
		//#34771 - End
		validateHqBranchBank();
		// validate bank
		if (getManagedBean().getSmAppl().getSmSupplierBanks().size() < 1) {
			throw new EpException(MessageConst.ERROR_721002);
		}
		getInstance().validateBankInfo(
				getManagedBean().getSmAppl().getSmSupplierBanks());

		RegistrationReqDTO reqDTO = new RegistrationReqDTO();
		setBranchBankBeforeSubmit();
		reqDTO.setSmAppl(getManagedBean().getSmAppl());
		reqDTO.setGstList(getInstance().getCleanGst());
		try {
			RegistrationResDTO resDTO = SmServiceLookup.getRegistrationSO()
					.updateGovtSellerAcc(reqDTO);
			if (resDTO.isSuccessful()) {
				//#15016 Start
				//SmCommonUtil.redirectToPage(
				//		"/xhtml/sm/onlinereg/outputMsg.xhtml",
				//		MessageConst.INFO_721013, null, null);
				// #28895_35501 Start - AUDIT TRAIL SM
				IAuditTrailSO auditSo = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.AUDIT, IAuditTrailSO.class);
				if (auditSo.isAuditConfig(AuditSupplierManagementConst.AU_SM_CONF)) {
					AuditSupplierManagementBackingBeanTemplate auditSmBackingBean = new AuditSupplierManagementBankInformationBackingBean();
					auditSmBackingBean.saveAuditData(getManagedBean(), getUserIdSessionFromLoginId());
				}
				// #28895_35501 End
				SmCommonUtil.redirectToPage(
						"/xhtml/sm/onlinereg/outputMsg.xhtml",
						MessageConst.INFO_721016, null, null);
				//#15016 End
			} else {
				throw new EpException(MessageConst.ERROR_721006);
			}
		} catch (Exception ex) {
			throw new EpException(ex.getMessage());
		} finally {
			reqDTO = null;
		}
	}

	/**
	 * Initialized identification types
	 */
	public void initIdentificationTypes() {
		if (SmCommonUtil.isCollectionEmpty(getManagedBean()
				.getIdentificationTypeItems())) {
			getManagedBean().setIdentificationTypeItems(
					new ArrayList<SelectItem>());
			for (SelectItem item : Arrays
					.asList(getDropdownValues(ParameterTypeConst.IDENTIFICATION_TYPES))) {
				if (item.getValue() != null
						&& !item.getValue().equals(
								SupplierManagementConst.NEW_IC)) {
					getManagedBean().getIdentificationTypeItems().add(item);
				}
			}
			initIdentityTypeMap();
		}
	}

	private void initIdentityTypeMap() {
		List<PmParameterDesc> parameterDescs = ParameterHelper.getInstance()
				.getParameterDescList(ParameterTypeConst.IDENTIFICATION_TYPES);

		identityType = new HashMap<Long, Object>();
		for (PmParameterDesc pmDesc : parameterDescs) {
			identityType.put(pmDesc.getPmParameter().getParameterId(),
					pmDesc.getPmParameter());
		}
	}

	/**
	 * Get minimum gst end date
	 * 
	 * @return Date
	 */
	public Date getMinGstEndDate() {
		if (getManagedBean().getSmGstSupplier() != null
				&& getManagedBean().getSmGstSupplier().getGstEffDate() != null) {
			return getManagedBean().getSmGstSupplier().getGstEffDate();
		} else {
			CommonConstants commonConst = new CommonConstants();
			return commonConst.getGST_LAUNCH_DATE();
		}
	}

	/**
	 * Assign value reqd to false when anyof the pop up is opened in the
	 * screen.Used for enable and disable screen validation dynamically.
	 */
	private void assignValReqdToTrue() {
		getManagedBean().setShowParentMsg(false);
		getManagedBean().setValueReqd(true);
	}

	/**
	 * Assign value reqd to false when any of the pop up is closed in the
	 * screen.Used for enable and disable screen validation dynamically.
	 */
	private void assignValReqdToFalse() {
		getManagedBean().setShowParentMsg(true);
		getManagedBean().setValueReqd(false);
	}

	/**
	 * Set GST document attachment to VO
	 * 
	 * @param SmGstSupplierDoc
	 *            doc
	 */
	private void setSuppDocFileAttachment(SmGstSupplierDoc doc) {
		List<SmGstSupplierDoc> gstSuppDocList = getManagedBean()
				.getSelectedBranch().getGst().getSmGstSupplierDocs();

		if (gstSuppDocList != null && !gstSuppDocList.isEmpty()) {
			Iterator<SmGstSupplierDoc> docVOIter = gstSuppDocList.iterator();
			while (docVOIter.hasNext()) {
				SmGstSupplierDoc docVO = docVOIter.next();
				if (docVO.getPmDocument().getDocumentId()
						.compareTo(doc.getPmDocument().getDocumentId()) == 0) {
					docVO = doc;
				}
			}
		}
	}

	/**
	 * Get maximum temporary branch code
	 * 
	 * @return int
	 */
	private int getMaxTmpBrCode() {
		int tmpNo = 0;
		if (!SmCommonUtil.isCollectionEmpty(getManagedBean().getSmAppl()
				.getSmSupplierBranches())) {

			for (SmSupplierBranch br : getManagedBean().getSmAppl()
					.getSmSupplierBranches()) {
				if (br.getBranchCode() != null
						&& Integer.parseInt(br.getBranchCode().toString()) > tmpNo) {
					tmpNo = Integer.parseInt(br.getBranchCode().toString());
				}
			}
		}
		return tmpNo;
	}

	/**
	 * Get maximum temporary bank code
	 * 
	 * @return int
	 */
	private int getMaxTmpBankCode() {
		int tmpNo = 0;
		if (!SmCommonUtil.isCollectionEmpty(getManagedBean().getSmAppl()
				.getSmSupplierBanks())) {

			for (SmSupplierBank bk : getManagedBean().getSmAppl()
					.getSmSupplierBanks()) {
				if (bk.getBankCode() != null
						&& Integer.parseInt(bk.getBankCode().toString()) > tmpNo) {
					tmpNo = Integer.parseInt(bk.getBankCode().toString());
				}
			}
		}
		return tmpNo;
	}

	/**
	 * Initialized branch address drop down values
	 * 
	 * @param SmAddress
	 *            address
	 */
	private void initBranchAddressSelectItems(SmAddress address) {
		//#34159 - START - refactor this method
		getManagedBean().setBranchCompanyStates(SmCommonUtil.getStateList(address.getPmCountry().getCountryId()));
		if(address.getPmDivision() != null && address.getPmDivision().getDivisionId() != null){
			getManagedBean().setBranchCompanyDivisions(SmCommonUtil.getDivisionList(address.getPmState().getStateId()));
			getManagedBean().setBranchCompanyDistricts(SmCommonUtil.getDistrictListByID(address.getPmDivision().getDivisionId()));
		} else {
			getManagedBean().setBranchCompanyDistricts(SmCommonUtil.getDistrictList(address.getPmState().getStateId()));
		}
		getManagedBean().setBranchCompanyCities(SmCommonUtil.getCityList(address.getPmDistrict().getDistrictId()));
		//#34159 - END
	}

	/**
	 * Initialized agency type
	 */
	private void initAgencyTypes() {
		getManagedBean().setAgencyTypeItems(new ArrayList<SelectItem>());
		SelectItem[] items = getDropdownCodeValues("OT");
		String[] agencyType = new String[] {
				OrganizationType.STATUTORY_BODIES.value(),
				OrganizationType.STATUTORY_BODIES_STATE.value(),
				OrganizationType.STATE_GOVERNMENT.value(),
				OrganizationType.LOCAL_AUTHORITY.value() };
		if (items != null && items.length > 0) {
			List<SelectItem> itemList = Arrays.asList(items);
			for (SelectItem item : itemList) {
				if (item.getValue() != null
						&& Arrays.asList(agencyType).contains(item.getValue())) {
					getManagedBean().getAgencyTypeItems().add(item);
				}
			}
		}
	}

	/**
	 * Initialized SmSupplier based on PmOrgProfile
	 * 
	 * @param PmOrgProfile
	 *            orgProfile
	 */
	private void initSupplier(PmOrgProfile orgProfile, SmSupplier sup) {
		sup.setRegNo(orgProfile.getOrgCode());
		sup.setBusinessType(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_GOVT_SELLER);
		sup.setCompanyName(orgProfile.getOrgName());
		sup.setRecordStatusFlg(DbValueConst.RECORD_STATUS_IN_PROGRESS);
		sup.setSupplierId(orgProfile.getOrgProfileId());

		SmAppl appl = new SmAppl();
		appl.setSupplierType(SupplierManagementConst.SUPP_TYPE_GOVT_TO_GOVT);
		appl.setApplType(SupplierManagementConst.ACC_BASIC);
		appl.setSmSupplierBanks(new ArrayList<SmSupplierBank>());
		appl.setSmPersonnels(new ArrayList<SmPersonnel>());
		appl.setSmSupplierBranches(new ArrayList<SmSupplierBranch>());

		SmCompanyBasic compBasic = new SmCompanyBasic();
		compBasic.setCompanyName(orgProfile.getOrgName());
		compBasic.setRevNo(SupplierManagementConst.DEFAULT_REV_NO);

		SmAddress addr = (SmAddress) SerializationUtils.clone(SmApplicationUtil
				.setDefaultAddr(null));
		SmCompanyAddress compAddr = new SmCompanyAddress();
		if (!SmCommonUtil.isCollectionEmpty(orgProfile.getPmAddressTypes())) {
			PmAddressType addrType = orgProfile.getPmAddressTypes().get(0);
			if (addrType.getPmAddress() != null) {
				//start #30827
				if(addrType.getPmAddress().getPmCountry()!=null){
					addrType.getPmAddress().getPmCountry().getCountryId();
				}
				if(addrType.getPmAddress().getPmCity()!=null){
					addrType.getPmAddress().getPmCity().getCityId();
				}
				if(addrType.getPmAddress().getPmDistrict()!=null){
					addrType.getPmAddress().getPmDistrict().getDistrictId();
				}
				if(addrType.getPmAddress().getPmState()!=null){
					addrType.getPmAddress().getPmState().getStateId();
				}
				if(addrType.getPmAddress().getPmDivision()!=null){
					addrType.getPmAddress().getPmDivision().getDivisionId();
				}
				//end #30827
				addr.setAddress1(addrType.getPmAddress().getAddress1());
				addr.setAddress2(addrType.getPmAddress().getAddress2());
				addr.setAddress3(addrType.getPmAddress().getAddress3());
				addr.setPmCountry(addrType.getPmAddress().getPmCountry());
				addr.setPmState(addrType.getPmAddress().getPmState());
				//13320 - added division
				addr.setPmDivision(addrType.getPmAddress().getPmDivision());
				addr.setPmDistrict(addrType.getPmAddress().getPmDistrict());
				addr.setPmCity(addrType.getPmAddress().getPmCity());
				addr.setPostcode(addrType.getPmAddress().getPostcode());

				compBasic.setPhoneCountry(addrType.getPmAddress()
						.getPhoneCountry());
				compBasic.setPhoneArea(addrType.getPmAddress().getPhoneArea());
				compBasic.setPhoneNo(addrType.getPmAddress().getPhoneNo());
				compBasic
						.setFaxCountry(addrType.getPmAddress().getFaxCountry());
				compBasic.setFaxArea(addrType.getPmAddress().getFaxArea());
				compBasic.setFaxNo(addrType.getPmAddress().getFaxNo());
			}
		}
		compAddr.setSmAddress(addr);
		compAddr.setSmCompanyBasic(compBasic);
		compAddr.setAddressType(SupplierManagementConst.BUS_ADD);
		compBasic.setSmCompanyAddresses(new ArrayList<SmCompanyAddress>());
		compBasic.getSmCompanyAddresses().add(compAddr);
		getManagedBean().setMyCountryId(SmCommonUtil.getMyCountryId());
		compBasic.setSmAppl(appl);
		appl.setSmCompanyBasic(compBasic);

		getManagedBean().setSmSupp(sup);
		getManagedBean().setSmAppl(appl);
		initCompanyAddressSelectItems(addr, "ACC_REGISTRATION");//change place(syazwan code) //#34159

	}

	/**
	 * Check whether personnel already activated
	 * 
	 * @param List
	 *            <SmPersonnel> personnels
	 * @return boolean
	 */
	private SmPersonnel getActivatedPersonnel(List<SmPersonnel> personnels) {
		SmPersonnel retVal = null;
		for (SmPersonnel personnel : personnels) {
			if (StringUtils.isNotBlank(personnel.getEpRole())
					&& personnel.getPmUser() != null
					&& personnel.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE) {
				retVal = personnel;
				break;
			}
		}
		return retVal;
	}

	/**
	 * Initialized GST
	 */
	private void initGst() {
		SmGstSupplier gstSupplier = new SmGstSupplier();
		List<PmDocument> docLs = getInstance().retrieveGstDocFromPmDocument();
		gstSupplier.setSmGstSupplierDocs(new ArrayList<SmGstSupplierDoc>());

		if (docLs.size() > 0) {
			for (PmDocument document : docLs) {
				SmGstSupplierDoc gstSupplierDoc = new SmGstSupplierDoc();
				gstSupplierDoc.setSmGstSupplier(gstSupplier);
				gstSupplierDoc.setPmDocument(document);
				gstSupplier.getSmGstSupplierDocs().add(gstSupplierDoc);
			}
		}
		getManagedBean().setSmGstSupplier(gstSupplier);
	}

	/**
	 * Initialized company address drop down values
	 * 
	 * @param SmAddress
	 *            addr
	 */
	//#34159 - START - refactor this method
	private void initCompanyAddressSelectItems(SmAddress addr, String  panelPortlet) {
		getManagedBean().setCountryLs(SmCommonUtil.getCountryList());
		if(panelPortlet.equals("ACC_MAINTENANCE")){
			getManagedBean().setBusinessStateLs(SmCommonUtil.getStateList(addr.getPmCountry().getCountryId()));
			if(addr.getPmDivision() != null && addr.getPmDivision().getDivisionId() != null){
				getManagedBean().setBusinessDivisionLs(SmCommonUtil.getDivisionList(addr.getPmState().getStateId()));
				getManagedBean().setBusinessDistrictLs(SmCommonUtil.getDistrictListByID(addr.getPmDivision().getDivisionId()));
			} else {
				getManagedBean().setBusinessDistrictLs(SmCommonUtil.getDistrictList(addr.getPmState().getStateId()));
			}
			getManagedBean().setBusinessCityLs(SmCommonUtil.getCityList(addr.getPmDistrict().getDistrictId()));
		} else {
			onChangeCountry(addr.getPmCountry(), "COMPANY_ADDRESS");
			onChangeState(addr.getPmState(), "COMPANY_ADDRESS");
			onChangeDivision(addr.getPmDivision(), "COMPANY_ADDRESS");//13320
			onChangeDistrict(addr.getPmDistrict(), "COMPANY_ADDRESS");
		}
	}
	//#34159 - END

	/**
	 * Initialized user permanent address drop down values
	 * 
	 * @param SmAddress
	 *            addr
	 */
	private void initUserPermanentAddressSelectItems(SmAddress addr) {
		onChangeCountry(addr.getPmCountry(), "USER_PERMANENT_ADDRESS");
		onChangeState(addr.getPmState(), "USER_PERMANENT_ADDRESS");
		onChangeDistrict(addr.getPmDistrict(), "USER_PERMANENT_ADDRESS");
		onChangeDivision(addr.getPmDivision(), "USER_PERMANENT_ADDRESS");//13320
	}

	/**
	 * Initialized user residential address drop down values
	 * 
	 * @param SmAddress
	 *            addr
	 */
	private void initUserResidentialAddressSelectItems(SmAddress addr) {
		onChangeCountry(addr.getPmCountry(), "USER_RESIDENTIAL_ADDRESS");
		onChangeState(addr.getPmState(), "USER_RESIDENTIAL_ADDRESS");
		onChangeDistrict(addr.getPmDistrict(), "USER_RESIDENTIAL_ADDRESS");
		onChangeDivision(addr.getPmDivision(), "USER_RESIDENTIAL_ADDRESS");//13320
	}

	/**
	 * Validate before submit registration
	 */
	private void validateBeforeSubmit() {
		SmPersonnel personnel = getManagedBean().getSmPersonnel();

		// validate personnel
		if (!personnel.getEmail().equals(
				this.getManagedBean().getConfirmEmail())) {
			throw new EpException(MessageConst.ERROR_721020);
		}
		SmCommonUtil.checkEmailDomainWithExceptionMsg(personnel.getName(),
				personnel.getEmail());

		// validate IC length
		PmParameter identificationType = (PmParameter) identityType
				.get(personnel.getIdentificationTypeId());
		if (identificationType != null
				&& !StringUtils.isBlank(personnel.getIdentificationNo())) {
			getInstance().validateIDFormat(personnel.getIdentificationNo(),
					identificationType.getParameterCode());
		}

		// validate IC with MyIdentity
		SmCommonUtil.getInstance().verifyOwnerWithMyIdentity(
				personnel.getName(), personnel.getIdentificationNo(),
				getManagedBean().getSmPermenantAdd().getPostcode(), null,
				SupplierManagementConst.SUPP_TYPE_GOVT_TO_GOVT);

		// check if icNo already assign as supplier
		RegistrationReqDTO reqDTO = new RegistrationReqDTO();
		reqDTO.setIdentificationNo(personnel.getIdentificationNo());
		if (SmServiceLookup.getRegistrationSO()
				.checkIcNoAlreadySupplier(reqDTO).isIcNoAlreadySupplier()) {
			throw new EpException(MessageConst.ERROR_722626);
		}

		// validate bank
		if (getManagedBean().getSmAppl().getSmSupplierBanks().size() < 1) {
			throw new EpException(MessageConst.ERROR_721002);
		}
		getInstance().validateBankInfo(
				getManagedBean().getSmAppl().getSmSupplierBanks());
	}

	/**
	 * Set personnel related values before submit
	 */
	private void setSmPersonnelBeforeSubmit() {
		SmPersonnel smPersonnel = getManagedBean().getSmPersonnel();
		smPersonnel.setRevNo(SupplierManagementConst.DEFAULT_REV_NO);
		smPersonnel.setAuthorizedFlg(SupplierManagementConst.FIRST_USER_AUTHO);
		smPersonnel
				.setContactPerson(SupplierManagementConst.FIRST_USER_CONT_PERSON);
		smPersonnel
				.setContractSignerFlg(SupplierManagementConst.FIRST_USER_CONT_SIGNER);
		smPersonnel.setEpRole(RoleCode.SUPPLIER_TEMP.value());
		smPersonnel
				.setSoftcertFlg(SupplierManagementConst.SOFT_CERT_PENDING_REQ);
		smPersonnel.setClerical(true);
		smPersonnel.setActivationDate(new Timestamp(System.currentTimeMillis())); //32040
		
		if (smPersonnel.getTitle() != null
				&& smPersonnel.getTitle().getSalutationId() == null) {
			smPersonnel.setTitle(null);
		}

		String trimmedName = smPersonnel.getName().trim();
		smPersonnel.setName(trimmedName);

		// set personnel bumi flag or muslim flag
		smPersonnel.setBumiFlg(getManagedBean().getUserBumiFlg());
		smPersonnel.setMuslimFlg(getManagedBean().getUserMuslimFlg());

		SmPersonnelAddress personnelAddRes = new SmPersonnelAddress();
		SmPersonnelAddress personnelAddPerm = new SmPersonnelAddress();

		personnelAddRes.setAddressType(SupplierManagementConst.MAIL_ADD);
		if (this.getManagedBean().isSameAddress()) {
			personnelAddRes.setSmAddress(getManagedBean().getSmPermenantAdd());
			personnelAddRes.setSmPersonnel(smPersonnel);
		} else {
			personnelAddRes.setSmAddress(getManagedBean().getSmResAdd());
			personnelAddRes.setSmPersonnel(smPersonnel);
			getManagedBean().getSmResAdd().setSmPersonnelAddresses(
					new ArrayList<SmPersonnelAddress>());
			getManagedBean().getSmResAdd().getSmPersonnelAddresses()
					.add(personnelAddRes);
		}
		smPersonnel
				.setSmPersonnelAddresses(new ArrayList<SmPersonnelAddress>());
		smPersonnel.getSmPersonnelAddresses().add(personnelAddRes);
		personnelAddPerm.setAddressType(SupplierManagementConst.PERM_ADD);
		personnelAddPerm.setSmAddress(getManagedBean().getSmPermenantAdd());
		personnelAddPerm.setSmPersonnel(smPersonnel);
		getManagedBean().getSmPermenantAdd().setSmPersonnelAddresses(
				new ArrayList<SmPersonnelAddress>());
		getManagedBean().getSmPermenantAdd().getSmPersonnelAddresses()
				.add(personnelAddPerm);
		smPersonnel.getSmPersonnelAddresses().add(personnelAddPerm);
		smPersonnel.setSmAppl(getManagedBean().getSmAppl());
		getManagedBean().getSmAppl().getSmPersonnels().add(smPersonnel);
	}

	/**
	 * Set bank related values before submit
	 */
	private void setSmSupplierBankBeforeSubmit() {
		for (SmSupplierBank suppBank : getManagedBean().getSmAppl()
				.getSmSupplierBanks()) {
			suppBank.setSmAppl(getManagedBean().getSmAppl());
			suppBank.setRevNo(SupplierManagementConst.DEFAULT_REV_NO);
			suppBank.setAccountPurpose("P");
		}
	}

	/**
	 * Set branch related values before submit
	 */
	private void setSmSupplierBranchBeforeSubmit() {
		if (!SmCommonUtil.isCollectionEmpty(getManagedBean().getSmAppl()
				.getSmSupplierBranches())) {
			for (SmSupplierBranch br : getManagedBean().getSmAppl()
					.getSmSupplierBranches()) {
				br.setRevNo(SupplierManagementConst.DEFAULT_REV_NO);
				br.setSmPersonnel(null);
			}
		}
		setBranchBankBeforeSubmit();
	}

	/**
	 * Set GstSupplier Object to supplier
	 */
	private void setSupplierGstBeforeSubmit() {
		if (getManagedBean().isGstFlag()) {
			getManagedBean().getSmSupp().setGstRegisteredFlag(
					DbValueConst.RECORD_STATUS_ACTIVE);
			// main company
			SmGstSupplier gstSupplier = getManagedBean().getSmGstSupplier();
			gstSupplier.setIsCurrent(true);
			SmCommonUtil.removeNotUploadedGstSuppDoc(gstSupplier);
			gstSupplier.setSmSupplier(getManagedBean().getSmSupp());
			getManagedBean().getSmSupp().setSmGstSuppliers(
					new ArrayList<SmGstSupplier>());
			getManagedBean().getSmSupp().getSmGstSuppliers().add(gstSupplier);

			// branch
			if (!SmCommonUtil.isCollectionEmpty(getManagedBean().getSmAppl()
					.getSmSupplierBranches())) {
				for (SmSupplierBranch br : getManagedBean().getSmAppl()
						.getSmSupplierBranches()) {
					if (br.getGst() != null
							&& StringUtils
									.isNotBlank(br.getGst().getGstRegNo())) {
						SmCommonUtil.removeNotUploadedGstSuppDoc(br.getGst());
						br.getGst().setSmSupplier(getManagedBean().getSmSupp());
						br.getGst().setBranchCode(br.getBranchCode());
						getManagedBean().getSmSupp().getSmGstSuppliers()
								.add(br.getGst());
					}
				}
			}
		}
	}
	
	//harris
	private void setSupplierSstBeforeSubmit() {
		if (getManagedBean().isSstFlag()) {
			getManagedBean().getSmSupp().setSstRegisteredFlag(
					DbValueConst.RECORD_STATUS_ACTIVE);
			// main company
			SmSstSupplier sstSupplier = getManagedBean().getSmSstSupplier();
			sstSupplier.setIsCurrent(true);
//			SmCommonUtil.removeNotUploadedSstSuppDoc(sstSupplier);
			sstSupplier.setSmSupplier(getManagedBean().getSmSupp());
			getManagedBean().getSmSupp().setSmSstSuppliers(
					new ArrayList<SmSstSupplier>());
			getManagedBean().getSmSupp().getSmSstSuppliers().add(sstSupplier);

			// branch
			if (!SmCommonUtil.isCollectionEmpty(getManagedBean().getSmAppl()
					.getSmSupplierBranches())) {
				for (SmSupplierBranch br : getManagedBean().getSmAppl()
						.getSmSupplierBranches()) {
//					if (br.getSst() != null
//							&& StringUtils
//									.isNotBlank(br.getSst().getSalestaxregno())) {
//						SmCommonUtil.removeNotUploadedSstSuppDoc(br.getSst());
//						br.getSst().setSmSupplier(getManagedBean().getSmSupp());
//						br.getSst().setBranchCode(br.getBranchCode());
//						getManagedBean().getSmSupp().getSmSstSuppliers()
//								.add(br.getSst());
//					}
				}
			}
		}
	}
	/**
	 * Set application and supplier values before submit
	 */
	private void setSmApplBeforeSubmit() {
		MofRegistrationReqDTO reqDTO = new MofRegistrationReqDTO();
		reqDTO.setAppType(getManagedBean().getSmAppl().getApplType());
		reqDTO.setSupplierType(getManagedBean().getSmAppl().getSupplierType());
		getManagedBean().getSmAppl()
				.setSmSupplier(getManagedBean().getSmSupp());
		getManagedBean().getSmAppl().setApplNo(
				SmServiceLookup.getMofRegistrationSO()
						.generateApplicaitonNumber(reqDTO).getApplNum());
		getManagedBean().getSmSupp().setCompanyName(
				getManagedBean().getSmAppl().getSmCompanyBasic()
						.getCompanyName());
		getManagedBean().getSmSupp().setSmAppls(new ArrayList<SmAppl>());
		getManagedBean().getSmSupp().getSmAppls()
				.add(getManagedBean().getSmAppl());
		//#34319 - Start
		if(getManagedBean().isCancelApp()){
			log.info(">>>> Into cancel application that want to create new <<<<");
			SupplierSearchReqDTO supplierSearchReqDTO = new SupplierSearchReqDTO();
			supplierSearchReqDTO.setSupplierId(getManagedBean().getSmSupp().getSupplierId());
			SupplierSearchResDTO resDTO = SmServiceLookup.getSupplierSearchSO().retriveSupplierSmApplById(supplierSearchReqDTO);
			if(resDTO.getSmSupplier().getSmAppls() != null){
				log.info("Not Null + Size >> " + resDTO.getSmSupplier().getSmAppls().size());
				supplierSearchReqDTO.setSmSupplier(resDTO.getSmSupplier());
				//checking if the application has a record in smPersonnelAuthLog
				SupplierSearchResDTO resDTO1 = SmServiceLookup.getSupplierSearchSO().checkSupplierApplAuthLogRecord(supplierSearchReqDTO);
				if(resDTO1.isRecord()){
					getManagedBean().getSmSupp().getSmAppls().addAll(resDTO.getSmSupplier().getSmAppls());
				}
			}
		}
		//#34319 - End
	}

	private void setBranchBankBeforeSubmit() {
		if (!EntityHelper.isCollectionEmpty(getManagedBean().getSmAppl()
				.getSmSupplierBanks())) {
			SmSupplierBranchBank brBank = null;
			SmSupplierBranch br = null;
			List<SmSupplierBranchBank> toRemoveBranchBankList = new ArrayList<SmSupplierBranchBank>();
			for (SmSupplierBank bk : getManagedBean().getSmAppl()
					.getSmSupplierBanks()) {

				// set supplierBranchBanks
				if (bk.getSmSupplierBranchBanks() == null) {
					bk.setSmSupplierBranchBanks(new ArrayList<SmSupplierBranchBank>());
				}

				if (!EntityHelper.isCollectionEmpty(bk.getBranches())) {
					for (SmSupplierBranch brTmp : bk.getBranches()) {
						br = (SmSupplierBranch) EntityHelper
								.getEntityBySinglePropertyFromList(brTmp
										.getBranchCode(), getManagedBean()
										.getSmAppl().getSmSupplierBranches(),
										"branchCode");
						brBank = getSupplierBranchBank(br,
								bk.getSmSupplierBranchBanks());
						if (brTmp.isSelected()) {
							brBank.setSmSupplierBranch(br);
							brBank.setSmSupplierBank(bk);
							if (brBank.isBrandNew()) {
								bk.getSmSupplierBranchBanks().add(brBank);
							}
						} else {
							if (!brBank.isBrandNew()) {
								toRemoveBranchBankList.add(brBank);
							}
						}
					}
				}

				// set hq
				if (bk.isSelected()) {
					bk.setForHqFlg(1);
				} else if (!bk.isSelected() && bk.getForHqFlg() == 1) {
					bk.setForHqFlg(0);
				}

				// remove not selected branch
				bk.getSmSupplierBranchBanks().removeAll(toRemoveBranchBankList);
			}
		}
	}

	private SmSupplierBranchBank getSupplierBranchBank(SmSupplierBranch branch,
			List<SmSupplierBranchBank> branchBnkList) {
		SmSupplierBranchBank brBank = new SmSupplierBranchBank();
		if (!EntityHelper.isCollectionEmpty(branchBnkList)) {
			for (SmSupplierBranchBank brBk : branchBnkList) {
				if (branch.getBranchCode() != null
						&& brBk.getSmSupplierBranch() != null
						&& brBk.getSmSupplierBranch().getBranchCode()
								.compareTo(branch.getBranchCode()) == 0) {
					brBank = brBk;
				}
			}
		}
		return brBank;
	}

	/**
	 * Set RegistrationReqDTO values
	 * 
	 * @return RegistrationReqDTO
	 */
	private RegistrationReqDTO getRegistrationReqDTO() {
		RegistrationReqDTO reqDTO = new RegistrationReqDTO();
		FacesScopes fs = new FacesScopes();
		reqDTO.setServerName(fs.getRequest().getServerName());
		reqDTO.setServerPort(fs.getRequest().getServerPort());
		reqDTO.setPortalBaseUrl(SmCommonUtil.getBasePortalUrl());
		reqDTO.setLanguage(fs.getUserLocale().getLanguage());
		reqDTO.setSmSupp(this.getManagedBean().getSmSupp());

		// insert tracking diary
		reqDTO.setTrackingDiaryVO(SmCommonUtil.assignTrackingDiaryValues(
				SupplierManagementConst.SUPP_REGISTRATION, getManagedBean()
						.getSmAppl().getApplId(), getManagedBean().getSmAppl()
						.getApplNo(), RoleCode.SUPPLIER_TEMP.value()));
		return reqDTO;
	}

	/**
	 * Initialized bank branches transient object
	 */
	private void initBankBranches() {
		//#34771 - Start
		log.info("#34771 - checking time");
		//#34771 - End
		if (getManagedBean().getBankBranches() == null) {
			getManagedBean().setBankBranches(
					new ArrayList<SmSupplierBranchBank>());
		}
		if (getManagedBean().getSmAppl() != null
				&& !EntityHelper.isCollectionEmpty(getManagedBean().getSmAppl()
						.getSmSupplierBanks())) {
			SmSupplierBranch brTmp = null;
			for (SmSupplierBank bk : getManagedBean().getSmAppl()
					.getSmSupplierBanks()) {

				if (bk.getForHqFlg() == DbValueConst.RECORD_STATUS_ACTIVE) {
					bk.setSelected(true);
				}

				// add supplierBranchBank to branches
				if (bk.getBranches() == null) {
					bk.setBranches(new ArrayList<SmSupplierBranch>());
				}
				if (!EntityHelper.isCollectionEmpty(bk
						.getSmSupplierBranchBanks())) {
					for (SmSupplierBranchBank brBnk : bk
							.getSmSupplierBranchBanks()) {
						brTmp = (SmSupplierBranch) EntityHelper
								.getEntityBySinglePropertyFromList(brBnk
										.getSmSupplierBranch().getBranchCode(),
										bk.getBranches(), "branchCode");
						if (brTmp == null) {
							//#34771 - Start
							SmSupplierBranch brDb = (SmSupplierBranch) brBnk.getSmSupplierBranch().cloneEntity();
							//#34771 - End
							brDb.setSelected(true);
							bk.getBranches().add(brDb);
						}
						brTmp = null;
					}
				}

				// add supplierBranch from appl to branches
				if (!EntityHelper.isCollectionEmpty(getManagedBean()
						.getSmAppl().getSmSupplierBranches())) {
					for (SmSupplierBranch brMain : getManagedBean().getSmAppl()
							.getSmSupplierBranches()) {
						brTmp = (SmSupplierBranch) EntityHelper
								.getEntityBySinglePropertyFromList(
										brMain.getBranchCode(),
										bk.getBranches(), "branchCode");
						if (brTmp == null) {
							// #27283 - START
							SmSupplierBranch brNew = (SmSupplierBranch) brMain.cloneEntity();
							// #27283 - END
							brNew.setSelected(false);
							bk.getBranches().add(brNew);
						}
						brTmp = null;
					}
				}

				// remove from branches if not exist in supplierBranch from appl
				Iterator<SmSupplierBranch> brIt = bk.getBranches().iterator();
				while (brIt.hasNext()) {
					SmSupplierBranch itTmp = brIt.next();
					brTmp = (SmSupplierBranch) EntityHelper
							.getEntityBySinglePropertyFromList(itTmp
									.getBranchCode(), getManagedBean()
									.getSmAppl().getSmSupplierBranches(),
									"branchCode");
					if (brTmp == null) {
						brIt.remove();
					}
				}

				// remove SmSupplierBranchBank if not exist in supplierBranch
				// from appl
				if (bk.getSmSupplierBranchBanks() != null) {
					Iterator<SmSupplierBranchBank> brBkIt = bk
							.getSmSupplierBranchBanks().iterator();
					while (brBkIt.hasNext()) {
						SmSupplierBranchBank brBkTmp = brBkIt.next();
						brTmp = (SmSupplierBranch) EntityHelper
								.getEntityBySinglePropertyFromList(brBkTmp
										.getSmSupplierBranch().getBranchCode(),
										getManagedBean().getSmAppl()
												.getSmSupplierBranches(),
										"branchCode");
						if (brTmp == null) {
							brBkIt.remove();
						}
					}
				}

				// sort branches by branch name
				Collections.sort(bk.getBranches(),
						new Comparator<SmSupplierBranch>() {
							@Override
							public int compare(final SmSupplierBranch s1,
									final SmSupplierBranch s2) {
								String str1 = s1.getBranchName();
								String str2 = s2.getBranchName();

								return (str2.compareTo(str1));
							}
						});
			}

		}
	}

	private void validateHqBranchBank() {
		boolean isBranchHasBank = false;
		// validate branch must have at least one payment bank
		if (!EntityHelper.isCollectionEmpty(getManagedBean().getSmAppl()
				.getSmSupplierBranches())) {
			SmSupplierBranch brTmp = null;
			for (SmSupplierBranch br : getManagedBean().getSmAppl()
					.getSmSupplierBranches()) {
				for (SmSupplierBank bk : getManagedBean().getSmAppl()
						.getSmSupplierBanks()) {
					brTmp = (SmSupplierBranch) EntityHelper
							.getEntityBySinglePropertyFromList(
									br.getBranchCode(), bk.getBranches(),
									"branchCode");
					if (brTmp != null && brTmp.isSelected()) {
						isBranchHasBank = true;
					}
				}

				if (!isBranchHasBank) {
					throw new EpException(MessageConst.ERROR_722649);
				} else {
					isBranchHasBank = false;
				}
			}
		}

		// validate HQ must have at least one payment bank
		SmSupplierBank hqBank = (SmSupplierBank) EntityHelper
				.getEntityBySinglePropertyFromList(true, getManagedBean()
						.getSmAppl().getSmSupplierBanks(), "selected");
		if (hqBank == null) {
			throw new EpException(MessageConst.ERROR_722652);
		}
	}

	public static RegistrationBackingBean getInstance() {
		if (instance == null) {
			instance = new RegistrationBackingBean();
		}
		return instance;

	}
	
	//START 17906
	public void checkingBeforeRemoveSupplierBank() {
		StringBuilder sb = new StringBuilder();
		boolean showErrorContract = false;//to avoid duplicate message
		boolean showErrorFl = false;//to avoid duplicate message
		sb.append("<font color='red'>");
		boolean showErrorPopup = false;
		if(this.getManagedBean().getSelectedSuppBanks() != null && this.getManagedBean().getSelectedSuppBanks().length > 0){
			this.getManagedBean().setDisplayMessageBank("");
			for (SmSupplierBank suppBank : this.getManagedBean().getSelectedSuppBanks()) {
				if(suppBank.isHasActiveTransactionContractManagement() && !showErrorContract){
					showErrorPopup = true;
					showErrorContract = true;
					sb.append(SmCommonUtil.getMessage("lbl.sm.bankDtls.is.active.contract.tied",null));
					sb.append("<br/>");
				}
				if(suppBank.isHasActiveTransactionFulfilment() && !showErrorFl){
					showErrorPopup = true;
					showErrorFl = true;
					sb.append(SmCommonUtil.getMessage("lbl.sm.bankDtls.is.other.active.contract.tied",null));
					sb.append("<br/>");
				}				
			}
		}
		sb.append("</font>");
		this.getManagedBean().setDisplayMessageBank(sb.toString());
		if(showErrorPopup){
			RequestContext.getCurrentInstance().execute("bankAlertPopUp.show()");
			return;
		}
		RequestContext.getCurrentInstance().execute("bankDeletePopUp.show()");
	}
	//END 17906
	
	
	//13320-Start
	public void onChangeDivision(PmDivision division, String addrType) {
		if ((addrType != null && addrType.equals("COMPANY_ADDRESS"))) {
			if (division != null) {
				//#34159 - START
				if(division.getDivisionId() != null){
					List<SelectItem> divisionLs = SmCommonUtil.getDistrictListByID(division.getDivisionId());
					this.getManagedBean().setBusinessDistrictLs(divisionLs);
				} else {
					this.getManagedBean().setBusinessDistrictLs(new ArrayList<SelectItem>());
				}
				this.getManagedBean().setBusinessCityLs(new ArrayList<SelectItem>());
				this.getManagedBean().getSmAppl().getSmCompanyBasic().getSmCompanyAddresses().get(0).getSmAddress().setPmDistrict(new PmDistrict());
				this.getManagedBean().getSmAppl().getSmCompanyBasic().getSmCompanyAddresses().get(0).getSmAddress().setPmCity(new PmCity());
				//#34159 - END
			}
		} else if (addrType != null && addrType.equals("USER_RESIDENTIAL_ADDRESS")) {
			if (division != null) {
				//#34159 - START
				if(division.getDivisionId() != null) {
					List<SelectItem> divisionLs = SmCommonUtil.getDistrictListByID(division.getDivisionId());
					this.getManagedBean().setResidentialDistrictLs(divisionLs);
				} else {
					this.getManagedBean().setResidentialDistrictLs(new ArrayList<SelectItem>());
				}
				this.getManagedBean().setResidentialCityLs(new ArrayList<SelectItem>());
				this.getManagedBean().getSmResAdd().setPmDistrict(new PmDistrict());
				this.getManagedBean().getSmResAdd().setPmCity(new PmCity());
				//#34159 - END
			}
		} else if (addrType != null && addrType.equals("USER_PERMANENT_ADDRESS")) {
			if (division != null) {
				//#34159 - START
				if(division.getDivisionId() != null){
					List<SelectItem> divisionLs = SmCommonUtil.getDistrictListByID(division.getDivisionId());
					this.getManagedBean().setPermenantDistrictLs(divisionLs);
				} else {
					this.getManagedBean().setPermenantDistrictLs(new ArrayList<SelectItem>());
				}
				this.getManagedBean().setPermenantCityLs(new ArrayList<SelectItem>());
				this.getManagedBean().getSmPermenantAdd().setPmDistrict(new PmDistrict());
				this.getManagedBean().getSmPermenantAdd().setPmCity(new PmCity());
				//#34159 - END
				
			}
		} else if (addrType != null && addrType.equals("BRANCH_ADDRESS")) {
			if (division != null) {
				//#34159 - START
				if(division.getDivisionId() != null){
					List<SelectItem> divisionLs = SmCommonUtil.getDistrictListByID(division.getDivisionId());
					this.getManagedBean().setBranchCompanyDistricts(divisionLs);
				} else {
					this.getManagedBean().setBranchCompanyDistricts(new ArrayList<SelectItem>());
				}
				this.getManagedBean().setBranchCompanyCities(new ArrayList<SelectItem>());
				this.getManagedBean().getSelectedBranch().getSmAddress().setPmDistrict(new PmDistrict());
				this.getManagedBean().getSelectedBranch().getSmAddress().setPmCity(new PmCity());
				//#34159 - END
			}
		}
	}
	//13320-End
	
	//14685 - START
	public void showMessageToggle(){
		if(this.getManagedBean().isShowMessage()){
			this.getManagedBean().setShowMessage(false);
		}else{
			this.getManagedBean().setShowMessage(true);
		}
	}
	//14685 - END
}

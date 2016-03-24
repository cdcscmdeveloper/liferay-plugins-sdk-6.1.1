package my.ep.web.backingbean.sm.govtsellerregistration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.model.SelectItem;

import my.ep.constant.MessageConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.dto.sm.mofRegistration.MofRegistrationReqDTO;
import my.ep.dto.sm.registration.RegistrationReqDTO;
import my.ep.dto.sm.registration.RegistrationResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.common.PmAddressType;
import my.ep.persistence.entity.pm.common.PmCountry;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmDocument;
import my.ep.persistence.entity.pm.common.PmFinancialOrg;
import my.ep.persistence.entity.pm.common.PmParameter;
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
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.sm.SmSupplierBank;
import my.ep.persistence.entity.sm.SmSupplierBranch;
import my.ep.type.pm.OrganizationType;
import my.ep.type.pm.RoleCode;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.sm.registration.RegistrationBackingBean;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.sm.govtsellerregistration.GovtSellerRegistrationManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.sm.helper.SmApplicationUtil;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.util.sm.helper.SmRuleHelper;
import my.ep.web.util.sm.helper.SmServiceLookup;

import org.apache.commons.lang.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;

@ManagedBean(name = "govtSellerBackingBean")
@RequestScoped
public class GovtSellerRegistrationBackingBean extends
		BaseBackingBean<GovtSellerRegistrationManagedBean> {

	public static RegistrationBackingBean instance;

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
		getManagedBean().setShowBranchPointOfContact(false);
		getManagedBean().setSalutationItems(SmCommonUtil.getSalutationItems());
		SmCommonUtil.redirectToPage(
				"/xhtml/sm/govtSellerRegistration/govtSellerTncPage.xhtml",
				"", null);

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
		getManagedBean().setShowGst(false);
		getManagedBean().setFinancialOrgLs(
				getInstance().retrieveFinancialOrgList());
		getManagedBean().setCountryLs(SmCommonUtil.getCountryList());
		getManagedBean().setSelectedBranch(new SmSupplierBranch());
		getManagedBean().getSelectedBranch().setSmPersonnel(new SmPersonnel());
		getManagedBean().getSelectedBranch().setSmAddress(
				(SmAddress) SerializationUtils.clone(SmApplicationUtil
						.setDefaultAddr(null)));
		initCompanyAddressSelectItems(getManagedBean().getSmAppl()
				.getSmCompanyBasic().getSmCompanyAddresses().get(0)
				.getSmAddress());
		setHqGst(getManagedBean().getSmSupp());
		if (getManagedBean().getSmGstSupplier() != null) {
			SmApplicationUtil.initBranchGstInformation(getManagedBean()
					.getSmGstSupplier(), getManagedBean().getSmAppl());
		}
		getManagedBean().setSelectedBranches(new ArrayList<SmSupplierBranch>());
		getManagedBean().setShowBranchPointOfContact(true);
		SmCommonUtil
				.redirectToPage(
						"/xhtml/sm/govtSellerRegistration/govtSellerMaintenanceBasePage.xhtml",
						"", null);
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
		if (orgProfile != null) {
			if (orgProfile.getSmSupplier() != null
					&& orgProfile.getSmSupplier().getRecordStatusFlg() != null) {
				if (orgProfile.getSmSupplier().getRecordStatusFlg() == DbValueConst.RECORD_STATUS_SUSPENDED) {
					// Selected agency account already deactivated. Please
					// reactivate the account
					throw new EpException(MessageConst.ERROR_722625);
				} else if (orgProfile.getSmSupplier().getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE) {
					if (orgProfile.getSmSupplier().getSmAppl() != null
							&& !SmCommonUtil.isCollectionEmpty(orgProfile
									.getSmSupplier().getSmAppl()
									.getSmPersonnels())) {
						if (isPersonnelAlreadyActivated(orgProfile
								.getSmSupplier().getSmAppl().getSmPersonnels())) {
							// Selected agency already had account. Please login
							// into ePerolehan system using existing login ID
							// and password
							throw new EpException(MessageConst.ERROR_722623);
						} else {
							// Selected agency already had account but has not
							// activated the account. Please activate the
							// account using activation link that had been sent
							// to the user's email address.
							throw new EpException(MessageConst.ERROR_722624);
						}
					}
				} else {
					if (orgProfile.getSmSupplier().getRecordStatusFlg() == DbValueConst.RECORD_STATUS_IN_PROGRESS) {
						// Selected agency already had account. Please login
						// into ePerolehan system using existing login ID
						// and password
						throw new EpException(MessageConst.ERROR_722623);
					} else {
						// Unknown problem occurred. Invalid or missing data.
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

		initSupplier(orgProfile);
		initGst();
		navigateToPage(SupplierManagementConst.GOVT_SELLER_ACCOUNT_INFO_PAGE);

	}

	/**
	 * Validate GST registration NO.
	 */
	public void validateGstRegNo() {
		String gstNo = this.getManagedBean().getSmGstSupplier().getGstRegNo();
		this.getManagedBean().getSmGstSupplier().setIsCurrent(true);
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

	/**
	 * FIle upload Handler
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void handleFileUpload(FileUploadEvent event) throws Exception {
		if (event != null) {
			String tempPath = String.valueOf((new Date()).getTime());
			String filePath = SmCommonUtil
					.getUploadFilePath(SupplierManagementConst.GST_FILE_PATH)
					+ tempPath + "/";

			String fileName = String.valueOf((new Date()).getTime()) + "_"
					+ event.getFile().getFileName();
			getManagedBean().getSelectedGstSupplierDoc().setFileName(fileName);
			getManagedBean().getSelectedGstSupplierDoc().setFilePath(filePath);

			commonUploadFile(event, filePath);
		}
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
		this.getManagedBean().getSmAppl().getSmSupplierBanks().add(smSuppBank);
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
				getManagedBean().setBranchCompanyStates(
						SmCommonUtil.getStateList(country.getCountryId()));
				getManagedBean().setBranchCompanyDistricts(
						new ArrayList<SelectItem>());
				getManagedBean().setBranchCompanyCities(
						new ArrayList<SelectItem>());
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
	public void onChangeState(PmState state, String addrType) {
		if (addrType != null && addrType.equals("COMPANY_ADDRESS")) {
			if (state != null) {
				getManagedBean().setBusinessDistrictLs(
						SmCommonUtil.getDistrictList(state.getStateId()));
				getManagedBean().setBusinessCityLs(new ArrayList<SelectItem>());
			} else {
				getManagedBean().setBusinessDistrictLs(
						new ArrayList<SelectItem>());
				getManagedBean().setBusinessCityLs(new ArrayList<SelectItem>());
			}
		} else if (addrType != null
				&& addrType.equals("USER_PERMANENT_ADDRESS")) {
			if (state != null) {
				getManagedBean().setPermenantDistrictLs(
						SmCommonUtil.getDistrictList(state.getStateId()));
				getManagedBean()
						.setPermenantCityLs(new ArrayList<SelectItem>());
			} else {
				getManagedBean().setPermenantDistrictLs(
						new ArrayList<SelectItem>());
				getManagedBean()
						.setPermenantCityLs(new ArrayList<SelectItem>());
			}
		} else if (addrType != null
				&& addrType.equals("USER_RESIDENTIAL_ADDRESS")) {
			if (state != null) {
				getManagedBean().setResidentialDistrictLs(
						SmCommonUtil.getDistrictList(state.getStateId()));
				getManagedBean().setResidentialCityLs(
						new ArrayList<SelectItem>());
			} else {
				getManagedBean().setResidentialDistrictLs(
						new ArrayList<SelectItem>());
				getManagedBean().setResidentialCityLs(
						new ArrayList<SelectItem>());
			}
		} else if (addrType != null && addrType.equals("BRANCH_ADDRESS")) {
			if (state != null) {
				getManagedBean().setBranchCompanyDistricts(
						SmCommonUtil.getDistrictList(state.getStateId()));
				getManagedBean().setBranchCompanyCities(
						new ArrayList<SelectItem>());
			} else {
				getManagedBean().setBranchCompanyDistricts(
						new ArrayList<SelectItem>());
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
				getManagedBean().setBusinessCityLs(
						SmCommonUtil.getCityList(district.getDistrictId()));
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
				getManagedBean().setBranchCompanyCities(
						SmCommonUtil.getCityList(district.getDistrictId()));
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
		setSmApplBeforeSubmit();
		if (SmServiceLookup.getRegistrationSO()
				.createSupplier(getRegistrationReqDTO()).isSuccessful()) {
			SmCommonUtil.redirectToPage("/xhtml/sm/onlinereg/outputMsg",
					MessageConst.INFO_721012, new String[] {
							getManagedBean().getSmPersonnel().getEmail(),
							SmRuleHelper.getInstance().getEmailActivationDays()
									+ "" });
		} else {
			throw new EpException(MessageConst.ERROR_721006);
		}
	}

	/**
	 * Submit maintenance
	 */
	public void submitMaintenance() {
		// validate bank
		if (getManagedBean().getSmAppl().getSmSupplierBanks().size() < 1) {
			throw new EpException(MessageConst.ERROR_721002);
		}
		getInstance().validateBankInfo(
				getManagedBean().getSmAppl().getSmSupplierBanks());

		RegistrationReqDTO reqDTO = new RegistrationReqDTO();
		// set company name from company basic to supplier
		getManagedBean()
				.getSmAppl()
				.getSmSupplier()
				.setCompanyName(
						getManagedBean().getSmAppl().getSmSupplier()
								.getSmAppl().getSmCompanyBasic()
								.getCompanyName());
		reqDTO.setSmSupp(getManagedBean().getSmAppl().getSmSupplier());
		try {
			RegistrationResDTO resDTO = SmServiceLookup.getRegistrationSO()
					.updateOnlineAcc(reqDTO);
			if (resDTO.isSuccessful()) {
				SmCommonUtil.redirectToPage(
						"/xhtml/sm/onlinereg/outputMsg.xhtml",
						MessageConst.INFO_721013, null);
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
	 * Initialized branch address drop down values
	 * 
	 * @param SmAddress
	 *            address
	 */
	private void initBranchAddressSelectItems(SmAddress address) {
		onChangeCountry(address.getPmCountry(), "BRANCH_ADDRESS");
		onChangeState(address.getPmState(), "BRANCH_ADDRESS");
		onChangeDistrict(address.getPmDistrict(), "BRANCH_ADDRESS");
	}

	/**
	 * Initialized agency type
	 */
	private void initAgencyTypes() {
		getManagedBean().setAgencyTypeItems(new ArrayList<SelectItem>());
		SelectItem[] items = getDropdownCodeValues("OT");
		String[] agencyType = new String[] {
				OrganizationType.STATUTORY_BODIES.value(),
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
	private void initSupplier(PmOrgProfile orgProfile) {
		SmSupplier sup = new SmSupplier();
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
				addr.setAddress1(addrType.getPmAddress().getAddress1());
				addr.setAddress2(addrType.getPmAddress().getAddress2());
				addr.setAddress3(addrType.getPmAddress().getAddress3());
				addr.setAddress4(addrType.getPmAddress().getAddress4());
				addr.setPmCountry(addrType.getPmAddress().getPmCountry());
				addr.setPmState(addrType.getPmAddress().getPmState());
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
		initCompanyAddressSelectItems(addr);

		compBasic.setSmAppl(appl);
		appl.setSmCompanyBasic(compBasic);

		getManagedBean().setSmSupp(sup);
		getManagedBean().setSmAppl(appl);
	}

	/**
	 * Check whether personnel already activated
	 * 
	 * @param List
	 *            <SmPersonnel> personnels
	 * @return boolean
	 */
	private boolean isPersonnelAlreadyActivated(List<SmPersonnel> personnels) {
		boolean retVal = false;
		for (SmPersonnel personnel : personnels) {
			if (StringUtils.isNotBlank(personnel.getEpRole())
					&& personnel.getPmUser() != null
					&& personnel.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE) {
				retVal = true;
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
	private void initCompanyAddressSelectItems(SmAddress addr) {
		getManagedBean().setCountryLs(SmCommonUtil.getCountryList());
		onChangeCountry(addr.getPmCountry(), "COMPANY_ADDRESS");
		onChangeState(addr.getPmState(), "COMPANY_ADDRESS");
		onChangeDistrict(addr.getPmDistrict(), "COMPANY_ADDRESS");
	}

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
		if (personnel.getIdentificationNo().length() != 12) {
			throw new EpException(MessageConst.ERROR_721013);
		}

		if (!personnel.getIdentificationNo().matches("[0-9]+")) {
			throw new EpException(MessageConst.ERROR_721013);
		}
		// validate IC with MyIdentity
		getInstance().verifyWithMyIdentity(personnel.getName(),
				personnel.getIdentificationNo(),
				getManagedBean().getSmPermenantAdd().getPostcode());

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

	public static RegistrationBackingBean getInstance() {
		if (instance == null) {
			instance = new RegistrationBackingBean();
		}
		return instance;

	}
}

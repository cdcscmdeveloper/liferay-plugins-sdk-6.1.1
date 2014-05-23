package my.ep.web.backingbean.sm;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;

import my.ep.biz.service.pm.common.categorycode.ICategoryCodeSO;
import my.ep.biz.service.pm.common.certbody.ICertBodySO;
import my.ep.biz.service.pm.common.city.ICitySO;
import my.ep.biz.service.pm.common.country.ICountrySO;
import my.ep.biz.service.pm.common.district.IDistrictSO;
import my.ep.biz.service.pm.common.document.IDocumentSO;
import my.ep.biz.service.pm.common.financialorg.IFinancialOrgSO;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.pm.common.reason.IReasonSO;
import my.ep.biz.service.pm.common.state.IStateSO;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.pm.common.uom.IUomSO;
import my.ep.biz.service.sm.mofRegistration.IMofRegistrationSO;
import my.ep.biz.service.sm.reviewApplication.IReviewApplicationSO;
import my.ep.biz.service.sm.siteVisit.ISiteAssignSO;
import my.ep.constant.MessageConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.dto.pm.common.categorycode.CategoryCodeReqDTO;
import my.ep.dto.pm.common.categorycode.CategoryCodeResDTO;
import my.ep.dto.pm.common.certbody.CertBodyResDTO;
import my.ep.dto.pm.common.city.CityReqDTO;
import my.ep.dto.pm.common.city.CityResDTO;
import my.ep.dto.pm.common.country.CountryReqDTO;
import my.ep.dto.pm.common.country.CountryResDTO;
import my.ep.dto.pm.common.district.DistrictReqDTO;
import my.ep.dto.pm.common.district.DistrictResDTO;
import my.ep.dto.pm.common.document.DocumentReqDTO;
import my.ep.dto.pm.common.document.DocumentResDTO;
import my.ep.dto.pm.common.financialorg.FinancialOrgReqDTO;
import my.ep.dto.pm.common.financialorg.FinancialOrgResDTO;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
import my.ep.dto.pm.common.reason.ReasonReqDTO;
import my.ep.dto.pm.common.reason.ReasonResDTO;
import my.ep.dto.pm.common.state.StateReqDTO;
import my.ep.dto.pm.common.state.StateResDTO;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.dto.pm.common.uom.UomResDTO;
import my.ep.dto.sm.mofRegistration.MofRegistrationReqDTO;
import my.ep.dto.sm.mofRegistration.MofRegistrationResDTO;
import my.ep.dto.sm.reviewApplication.ReviewApplicationReqDTO;
import my.ep.dto.sm.reviewApplication.ReviewApplicationResDTO;
import my.ep.dto.sm.siteVisit.SiteAssignReqDTO;
import my.ep.dto.sm.siteVisit.SiteAssignResDTO;
import my.ep.persistence.entity.pm.common.PmCategoryL1;
import my.ep.persistence.entity.pm.common.PmCategoryL2;
import my.ep.persistence.entity.pm.common.PmCategoryL3;
import my.ep.persistence.entity.pm.common.PmCertBody;
import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmCountry;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmDocument;
import my.ep.persistence.entity.pm.common.PmFinancialOrg;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmReasonDesc;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.pm.common.PmUom;
import my.ep.persistence.entity.sm.SmAddress;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.persistence.entity.sm.SmApplDocFile;
import my.ep.persistence.entity.sm.SmApplRejectReason;
import my.ep.persistence.entity.sm.SmApplSectionReview;
import my.ep.persistence.entity.sm.SmApplSupportDoc;
import my.ep.persistence.entity.sm.SmAssociateCompany;
import my.ep.persistence.entity.sm.SmAssociationBody;
import my.ep.persistence.entity.sm.SmChequeSignatory;
import my.ep.persistence.entity.sm.SmCompanyAddress;
import my.ep.persistence.entity.sm.SmCompanyBasic;
import my.ep.persistence.entity.sm.SmCompanyExperience;
import my.ep.persistence.entity.sm.SmEquityCompany;
import my.ep.persistence.entity.sm.SmFacility;
import my.ep.persistence.entity.sm.SmMachineDtl;
import my.ep.persistence.entity.sm.SmManufacturingDtl;
import my.ep.persistence.entity.sm.SmMofAccount;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmPersonnelAddress;
import my.ep.persistence.entity.sm.SmRelatedCompany;
import my.ep.persistence.entity.sm.SmStaffAcademic;
import my.ep.persistence.entity.sm.SmStaffExperience;
import my.ep.persistence.entity.sm.SmStaffTotal;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.sm.SmSupplierBank;
import my.ep.persistence.entity.sm.SmSupplierBranch;
import my.ep.persistence.entity.sm.SmSupplierCategory;
import my.ep.persistence.entity.sm.SmSupplierCert;
import my.ep.persistence.entity.sm.SmSvAddress;
import my.ep.persistence.entity.sm.SmSvCategory;
import my.ep.persistence.entity.sm.SmSvOffice;
import my.ep.persistence.entity.sm.SmSvTask;
import my.ep.persistence.entity.sm.SmSvTemplate;
import my.ep.persistence.logging.LogKey;
import my.ep.type.pm.ReasonCategory;
import my.ep.util.MessageUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.sm.email.SmEmailNotificationHelper;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.sm.SmApplicationManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.vo.SmPersonnleCalcVO;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;

@ManagedBean(name = "backingBean")
@RequestScoped
public class SmApplicationBackingBean extends
		BaseBackingBean<SmApplicationManagedBean> {

	public static Logger log = Logger.getLogger(SmApplicationBackingBean.class);

	private int currentTab;
	private int outputTab;

	public SmApplicationBackingBean() {
		if (MDC.get(LogKey.USER_ID) == null) {
			MDC.put(LogKey.USER_ID, 1L);
		}

	}

	public String proceedToApplication(String applicationType) {
		return proceedToApplication(applicationType, Boolean.FALSE, null, null);
	}

	public String proceedToApplication(String applicationType, boolean fromBpm,
			SmAppl appl, SmSupplier supp) {
		System.out
				.println("**** ENTER Create Application Type Of  *************"
						+ applicationType + "Application type = >>"
						+ getManagedBean().getSmAppl().getSupplierType());
		retrievePmCountryList();
		retrievePmCertBodyList();
		retrievePmProfBodyList();
		retrieveParentCategoryList();
		retrieveUomList();
		retrieveRejectReasonList();
		retrieveRegistrationStatusList();
		SmAppl smAppl = new SmAppl();
		SmSupplier smApplSupplier = new SmSupplier();
		try {
			MofRegistrationReqDTO reqDto = new MofRegistrationReqDTO();
			MofRegistrationResDTO resDto = new MofRegistrationResDTO();

			if (!fromBpm) {

				boolean cannotRenewAlertMsg = false;

				if (applicationType.equalsIgnoreCase("N")) {
					smAppl.setSupplierType(getManagedBean()
							.getNewSupplierType());
				} else if (applicationType.equalsIgnoreCase("R")
						|| applicationType.equalsIgnoreCase("A")) {
					cannotRenewAlertMsg = validateBeforeRenewal((long) 27,
							applicationType);
				}
				if (cannotRenewAlertMsg) {
					return setExceptionNavigationForAppl(applicationType);
				} else {

					resDto = cloneSmApplicationDraft((long) 27,
							applicationType, smAppl.getSupplierType());
					if (resDto.isErrorCaued()) {
						return setExceptionNavigationForAppl(applicationType);

					}
					smApplSupplier = resDto.getSmSupplier();

					resDto = retriveLatestDraftApplication(smApplSupplier,
							applicationType);
					if (resDto.isErrorCaued()) {
						return setExceptionNavigationForAppl(applicationType);
					}

					smAppl = resDto.getSmAppl();

					System.out.println("Retrieved Value ==="
							+ smAppl.getSmCompanyBasic().getIncomeTaxNo()
							+ " === EST DATE ==== "
							+ smApplSupplier.getEstablishDate());
					System.out
							.println("******** Latest Cloned Draft Copy is ******"
									+ smAppl.getApplId()
									+ "--- Basic info ---"
									+ smAppl.getSmCompanyBasic()
											.getCompanyBasicId()
									+ " --- "
									+ smAppl.getSmCompanyBasic()
											.getCompanyName()
									+ " --- Supplier Type ---"
									+ smAppl.getSupplierType());
					System.out
							.println("******** After clone the basic address list *******"
									+ smAppl.getSmCompanyBasic());
					// TODO Set default not null values -- End insert appl ---
					// TODo Remove hard code later
					smAppl.setApplCategoryType(null);
				}
			} else {
				System.out
						.println("******* This is PO or Approver from BPM ************");
				smApplSupplier = supp;
				smAppl = appl;
			}

			getManagedBean().setSmSupplier(smApplSupplier);

			if (getManagedBean().isApproverUser()
					|| getManagedBean().isPoUser()) {
				System.out.println("**** LOGIN IS PO or APPROVER *******"
						+ smAppl.getApplCategoryType());
				// smAppl.setSupportingDocMode("O");
				// smAppl.setSupplierType(SupplierManagementConst.STYPE_CONSULTANT);

				if (smAppl.getSmCompanyBasic() == null) {
					System.out.println(" **** COMPANY BAsic cannot be null");
					smAppl.setSmCompanyBasic(new SmCompanyBasic());
				}
				// smAppl.setApplCategoryType("S");
			}

			if (smAppl.getSmCompanyBasic() != null) {
				if (smAppl.getSmCompanyBasic().getSmCompanyAddresses() != null
						&& !smAppl.getSmCompanyBasic().getSmCompanyAddresses()
								.isEmpty()) {
					SmCompanyAddress comAdd = smAppl.getSmCompanyBasic()
							.getSmCompanyAddresses().get(0);
					SmAddress addr = comAdd.getSmAddress();
					this.getManagedBean().setBasicCompanyStates(
							retrievePmStateList(addr.getPmCountry()
									.getCountryId()));
					this.getManagedBean().setBasicCompanyDistricts(
							retrievePmDistrictList(addr.getPmState()
									.getStateId()));
					this.getManagedBean().setBasicCompanyCities(
							retrievePmCityList(addr.getPmDistrict()
									.getDistrictId()));
				} else {
					SmCompanyAddress comAdd = new SmCompanyAddress();
					SmAddress addr = setDefaultAddr();
					comAdd.setSmAddress(addr);
					List<SmCompanyAddress> addrs = new ArrayList<SmCompanyAddress>();
					addrs.add(comAdd);
					smAppl.getSmCompanyBasic().setSmCompanyAddresses(addrs);
				}
			}

			if (smAppl.getSmFacilities() != null
					&& smAppl.getSmFacilities().isEmpty()
					&& !smAppl.getApplType().equalsIgnoreCase(
							ParameterConst.NEW_SM_APPLN_FOR_BUMI)) {
				System.out.println("*** Empty Facility******");
				SmFacility fac = new SmFacility();
				// fac.setOwnerName("Sample");
				// fac.setFacilityType("H");
				SmAddress addr = setDefaultAddr();
				fac.setSmAppl(smAppl);
				fac.setSmAddress(addr);
				fac.setRevNo(1);
				smAppl.getSmFacilities().add(fac);
				this.getManagedBean().setBasicCompanyFacility(fac);
				getManagedBean()
						.setBasicOffFacStates(
								retrievePmStateList(addr.getPmCountry()
										.getCountryId()));

			} else {
				setApplicationFailities(smAppl.getSmFacilities());
				// TODO
			}

			getManagedBean().setSmAppl(smAppl);
			if (getManagedBean().getSmAppl().getSmStaffTotals().isEmpty()) {
				System.out.println("*** Empty list of staff *****"
						+ smAppl.getSupplierType());
				getManagedBean().getSmAppl().setSmStaffTotals(
						getStaffList(smAppl.getSupplierType()));
			}
			// getManagedBean().setSmAppl(smAppl);

			System.out.println(" ---- Business  Type ----"
					+ getManagedBean().getSmAppl().getSmSupplier()
							.getSupplierId()
					+ " -- For this id ---"
					+ getManagedBean().getSmAppl().getSmSupplier()
							.getBusinessType());
			if (!fromBpm) {
				// TODO remove this hard coded data and make dynamic later
				getManagedBean().getSmAppl().setSupportingDocMode("O");
				// 1-ROB,2-ROC,3-ROS
				if (applicationType.equalsIgnoreCase("N")) {
					getManagedBean().getSmAppl().getSmSupplier()
							.setBusinessType("1");
				} else if (applicationType.equalsIgnoreCase("A")) {
					getManagedBean().getSmAppl().getSmSupplier()
							.setBusinessType("2");
				} else if (applicationType.equalsIgnoreCase("R")) {
					getManagedBean().getSmAppl().getSmSupplier()
							.setBusinessType("3");
				} else {
					getManagedBean().getSmAppl().getSmSupplier()
							.setBusinessType("1");
				}
			}
			System.out.println(" ---- Business  Type After hard code ----"
					+ getManagedBean().getSmAppl().getSmSupplier()
							.getBusinessType());
			getManagedBean().setTabActiveIndex(0);

			System.out
					.println(" **** APPLICATION TYPE **** " + applicationType);
			if (fromBpm) {
				getManagedBean()
						.setMaxReviewCount(
								assignMaxReviewCount(applicationType,
										getManagedBean().getSmAppl()
												.getApplCategoryType(),
										getManagedBean().getSmAppl()
												.getSupplierType()));

				if (getManagedBean().getSmAppl().getSmApplSectionReviews() == null) {
					smAppl.setSmApplSectionReviews(new ArrayList<SmApplSectionReview>());
				}

				if (getManagedBean().isPoUser()) {
					setDefaultPoReviewSection();
				}

				if (getManagedBean().isApproverUser()) {
					setDefaultApproverReviewSection();
				}
			}
			calcPersonnelListCount();
			calculateCapital();
			return setNavigationForAppl(applicationType);

		} catch (Exception e) {
			System.out.println(" **** Unknown Expection ******* ");
			e.printStackTrace();
			return setExceptionNavigationForAppl(applicationType);
		} finally {
			// mofRegistrationSO = null;
		}
	}

	private boolean validateBeforeRenewal(Long supplierId,
			String applicationType) {

		boolean result = false;
		IMofRegistrationSO mofRegistrationSO = (IMofRegistrationSO) BusinessDelegate
				.getInstance().getServiceObject("sm", IMofRegistrationSO.class);

		MofRegistrationReqDTO reqDto = new MofRegistrationReqDTO();
		MofRegistrationResDTO resDto = new MofRegistrationResDTO();

		reqDto.setSupplierId(supplierId);
		resDto = mofRegistrationSO.retrieveSupplierProfile(reqDto);

		if (resDto.getSmSupplier() != null) {

			SmSupplier smSupplier = resDto.getSmSupplier();
			List<SmMofAccount> accounts = smSupplier.getSmMofAccounts();

			for (SmMofAccount account : accounts) {
				if (account.getSupplierType().equalsIgnoreCase(
						smSupplier.getSmAppl().getSupplierType())) {

					long diff = account.getExpDate().getTime()
							- (new Date()).getTime();
					long diffDays = diff / (24 * 60 * 60 * 1000);

					if (applicationType.equalsIgnoreCase("R")) {

						if (diffDays > 90) {
							result = true;
							FacesContext
									.getCurrentInstance()
									.addMessage(
											null,
											new FacesMessage(
													FacesMessage.SEVERITY_INFO,
													"Cannot renew if more than 3 months for account expiry. Please renew once the 90 days renewal period has started.",
													""));
							// throw new EpException(MessageConst.EP_MAJ_00001);
						} else if (diffDays < -365) {
							result = true;
							FacesContext
									.getCurrentInstance()
									.addMessage(
											null,
											new FacesMessage(
													FacesMessage.SEVERITY_ERROR,
													"Cannot renew as account is no longer active. Please register a new account.",
													""));
						}
					} else if (applicationType.equalsIgnoreCase("A")
							&& diffDays < 90) {
						result = true;
						FacesContext
								.getCurrentInstance()
								.addMessage(
										null,
										new FacesMessage(
												FacesMessage.SEVERITY_ERROR,
												"Cannot add new category codes as account is nearing expiry.",
												""));
					}
				}
			}
		}
		return result;
	}

	// TODO Remove the method added only for demo purpose
	public void assignTaskListSampleForDemo(String bpmApplType) {

		IMofRegistrationSO mofRegistrationSO = null;

		try {
			mofRegistrationSO = (IMofRegistrationSO) BusinessDelegate
					.getInstance().getServiceObject("sm",
							IMofRegistrationSO.class);
			MofRegistrationReqDTO reqDto = new MofRegistrationReqDTO();
			SmSupplier smApplSupplier = retriveSupplierById((long) 3)
					.getSmSupplier();
			if (smApplSupplier == null) {
				smApplSupplier = new SmSupplier();
				// smApplSupplier.setSupplierName("My Sample Supplier");
				smApplSupplier.setBusinessType("1");
				smApplSupplier.setRegNo("12345");
				smApplSupplier.setEstablishDate(new Date());
				smApplSupplier.setWebSite("www.samplecompany.com");
			}

			SmAppl bpmSmAppl = smApplSupplier.getSmAppl();
			if (bpmSmAppl == null) {
				bpmSmAppl = new SmAppl();
			}
			if (bpmApplType.equalsIgnoreCase("N")) {
				bpmSmAppl.setApplNo("KN-20102013-1111");
			} else if (bpmApplType.equalsIgnoreCase("B")) {
				bpmSmAppl.setApplNo("KB-25102013-2222");
			} else if (bpmApplType.equalsIgnoreCase("U")) {
				bpmSmAppl.setApplNo(" KU-28102013-3333");
			} else if (bpmApplType.equalsIgnoreCase("A")) {
				bpmSmAppl.setApplNo(" KA-28102013-4444");
			} else if (bpmApplType.equalsIgnoreCase("R")) {
				bpmSmAppl.setApplNo(" KR-31102013-5555");
			} else if (bpmApplType.equalsIgnoreCase("C")) {
				bpmSmAppl.setApplNo(" KC-01112013-6666");
			} else {
				bpmSmAppl.setApplNo("UNKNOWNAPPL");
			}

			bpmSmAppl.setSmSupplier(smApplSupplier);
			// mofRegAppl.setSmAddress(smAddress);
			// 1- For Contractor, 2 - For Consultant
			bpmSmAppl.setSupplierType(SupplierManagementConst.STYPE_CONSULTANT);
			bpmSmAppl.setApplType(bpmApplType);
			// TODO Set default not null values -- Start insert appl ---
			// bpmSmAppl.getPmStatus().setStatusId(20001l);
			// sm.setSmAddress(new SmAddress());
			// SmAddress smAddress = setDefaultAddr();
			// getManagedBean().getSmSupplier().setSmAppl(mofRegAppl);
			bpmSmAppl.setCreatedBy(1l);
			boolean isNewAppln = true;
			for (int i = 0; i < smApplSupplier.getSmAppls().size(); i++) {
				SmAppl app = smApplSupplier.getSmAppls().get(i);
				if (bpmSmAppl.getApplId() == app.getApplId()) {
					System.out.println("****** Update Application******** ");
					isNewAppln = false;
					smApplSupplier.getSmAppls().set(i, bpmSmAppl);
					break;
				}
			}
			if (isNewAppln) {
				System.out
						.println("****** INSERT A FRESH RECORD OF APPL TO SUUPPLIER ******** ");
				smApplSupplier.getSmAppls().add(bpmSmAppl);
			}

			getManagedBean().setSmSupplier(smApplSupplier);
			// TODO Set default not null values -- End insert appl ---

			bpmSmAppl = defaultSmAppl(bpmSmAppl);
			bpmSmAppl
					.setSmStaffTotals(getStaffList(bpmSmAppl.getSupplierType()));
			getManagedBean().setSmAppl(bpmSmAppl);
			getManagedBean().setTabActiveIndex(0);
			// TODo Remove hard code later
			if (getManagedBean().isApproverUser()
					|| getManagedBean().isPoUser()) {
				System.out.println("**** LOGIN IS PO or APPROVER *******");
				getManagedBean().getSmAppl().setSupportingDocMode("O");
			}

		} catch (Exception e) {
			System.out.println(" **** Unknown Expection ******* ");
			e.printStackTrace();

		} finally {
			mofRegistrationSO = null;
		}
	}

	public String deletePendingMofApplication() {

		try {
			SmSupplier smSupplier = retriveSupplierById((long) 3)
					.getSmSupplier();
			if (smSupplier == null) {
				smSupplier = new SmSupplier();
				// smSupplier.setSupplierName("My Sample Supplier");
				smSupplier.setBusinessType("1");
				smSupplier.setRegNo("12345");
				smSupplier.setEstablishDate(new Date());
				smSupplier.setWebSite("www.samplecompany.com");
			}
			// TODO the smappl is supposed to be get the latest application from
			// sm supplier
			SmAppl smAppl = new SmAppl();
			smAppl.setApplNo("KB-25102013-1234");
			smAppl.setPmStatus(new PmStatus());
			smAppl.getPmStatus().setStatusId(2l);
			smAppl.setApplType("B");

			getManagedBean().setSmSupplier(smSupplier);
			// TODO Set default not null values -- End insert appl ---

			smAppl = defaultSmAppl(smAppl);
			getManagedBean().setSmAppl(smAppl);
			return SupplierManagementConst.PROCEED_DEL_PENDING_MOF_APPLN;

		} catch (Exception e) {
			System.out.println(" **** Unknown Expection ******* ");
			e.printStackTrace();
			return SupplierManagementConst.DEFAULT_DEL_PENDING_MOF_APPLN;
		}
	}

	public void updateSmAppl() throws Exception {
		try {
			System.out.println("**** ENTER ACTION METHOD BEAN *************"
					+ currentTab + "  " + outputTab);

			if (getManagedBean().getSmAppl().getCurrentActiveTab() < outputTab) {
				getManagedBean().getSmAppl().setCurrentActiveTab(outputTab);
			} else {
				System.out.println(" ****** REMAIN PREV VALUE ********");
			}
			getManagedBean().setTabActiveIndex(outputTab);
			System.out.println("Value ----"
					+ getManagedBean().getSmAppl().getSupportingDocMode());
			// return SupplierManagementConst.PROCEED_MOF_REG;

		} catch (Exception e) {
			System.out.println("this is error");
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"Unknown problem occurred.Invalid or missing data",
							"Please contact administrator"));
			getManagedBean().setTabActiveIndex(currentTab);
			// return SupplierManagementConst.PROCEED_MOF_REG;
		}

	}

	public void updateSmAppl(String applType, String pageType) {
		boolean valid = false;
		boolean isError = false;
		IMofRegistrationSO mofRegistrationSO = null;
		System.out.println(" ***** Business  Type After hard code *********"
				+ getManagedBean().getSmAppl().getSmSupplier()
						.getBusinessType());
		List<SmPersonnel> perList = new ArrayList<SmPersonnel>();
		try {
			if (getManagedBean().getSmAppl().getCurrentActiveTab() < outputTab) {
				getManagedBean().getSmAppl().setCurrentActiveTab(outputTab);
			} else {
				System.out.println(" ****** REMAIN PREV VALUE ********");
			}
			System.out.println("******* IS PO *******"
					+ getManagedBean().isPoUser());
			if (getManagedBean().isPoUser()
					|| getManagedBean().isApproverUser()) {
				setPoOrApprvrSectionReview();
			}

			mofRegistrationSO = (IMofRegistrationSO) BusinessDelegate
					.getInstance().getServiceObject("sm",
							IMofRegistrationSO.class);
			valid = validateBeforeSave(applType, pageType);

			if (valid) {
				// Save or update to DB

				MofRegistrationReqDTO reqDto = new MofRegistrationReqDTO();

				reqDto.setSmAppl(this.getManagedBean().getSmAppl());

				MofRegistrationResDTO resDTO = mofRegistrationSO
						.updateApplication(reqDto);
				getManagedBean().setTabActiveIndex(outputTab);
			} else {
				getManagedBean().setTabActiveIndex(currentTab);
				getManagedBean().getSmAppl().setCurrentActiveTab(currentTab);
			}
			if (valid) {
				retriveLatestApplAfterUpdate(pageType);
			}
		} catch (Exception e) {
			System.out.println("this is error");
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"Unknown problem occurred.Invalid or missing data",
							"Please contact administrator"));

			getManagedBean().setTabActiveIndex(currentTab);
			getManagedBean().getSmAppl().setCurrentActiveTab(currentTab);
			isError = true;
			valid = false;
			retriveLatestApplAfterUpdate(pageType);
			// return SupplierManagementConst.PROCEED_MOF_REG;
		} finally {
			mofRegistrationSO = null;
		}
	}

	private void retriveLatestApplAfterUpdate(String pageType) {

		IMofRegistrationSO mofRegistrationSO = null;
		mofRegistrationSO = (IMofRegistrationSO) BusinessDelegate.getInstance()
				.getServiceObject("sm", IMofRegistrationSO.class);
		try {

			MofRegistrationReqDTO finReqDto = new MofRegistrationReqDTO();
			finReqDto.setApplId(getManagedBean().getSmAppl().getApplId());
			System.out.println("********* Final Block Id *****"
					+ finReqDto.getApplId());
			System.out.println("***** Its a valid ************");
			MofRegistrationResDTO resDTO = mofRegistrationSO
					.retrieveCurrentDraftApplication(finReqDto);
			getManagedBean().setSmAppl(resDTO.getSmAppl());
			setApplicationFailities(getManagedBean().getSmAppl()
					.getSmFacilities());

			if (getManagedBean().getSmAppl().getSmPersonnels() != null
					&& !getManagedBean().getSmAppl().getSmPersonnels()
							.isEmpty()) {
				setApplicationShareHolders(getManagedBean().getSmAppl()
						.getSmPersonnels());
			}
			System.out
					.println(" ***** Business  Type After in finally block *********"
							+ getManagedBean().getSmAppl().getSmSupplier()
									.getBusinessType());
			// TODO Remove later the hard code value
			// 1-ROB,2-ROC,3-ROS
			if (getManagedBean().getSmAppl().getApplType()
					.equalsIgnoreCase("N")) {
				getManagedBean().getSmAppl().getSmSupplier()
						.setBusinessType("1");
			} else if (getManagedBean().getSmAppl().getApplType()
					.equalsIgnoreCase("A")) {
				getManagedBean().getSmAppl().getSmSupplier()
						.setBusinessType("2");
			} else if (getManagedBean().getSmAppl().getApplType()
					.equalsIgnoreCase("R")) {
				getManagedBean().getSmAppl().getSmSupplier()
						.setBusinessType("3");
			} else {
				getManagedBean().getSmAppl().getSmSupplier()
						.setBusinessType("1");
			}

			if (getManagedBean().isPoUser()) {
				setDefaultPoReviewSection();
			}
			if (getManagedBean().isApproverUser()) {
				setDefaultApproverReviewSection();
			}

			if (pageType.equalsIgnoreCase("CapitalInfo")) {
				System.out.println(" ****8 This is capital ********* ");
				for (int i = 0; i < getManagedBean().getSmAppl()
						.getSmSupplierBranches().size(); i++) {
					SmSupplierBranch br = getManagedBean().getSmAppl()
							.getSmSupplierBranches().get(i);
					if (br.getSmPersonnel() == null) {
						System.out
								.println(" **** This is null personnel ******** ");
						br.setSmPersonnel(new SmPersonnel());
						getManagedBean().getSmAppl().getSmSupplierBranches()
								.set(i, br);

					}
				}
			}

			System.out.println("***** After final Retrieve ****"
					+ getManagedBean().getSmAppl().getPmStatus().getStatusId());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			mofRegistrationSO = null;
		}

	}

	private void setSmApplRejectionReasons() {
		List<SmApplRejectReason> resList = new ArrayList<SmApplRejectReason>();
		for (Long resId : getManagedBean().getReasonId()) {
			for (PmReasonDesc desc : getManagedBean().getReasonDescList()) {
				if (desc.getPmReason().getReasonId().equals(resId)) {
					SmApplRejectReason rej = new SmApplRejectReason();
					rej.setPmReason(desc.getPmReason());
					rej.setSmAppl(getManagedBean().getSmAppl());
					resList.add(rej);
				}
			}
		}
		getManagedBean().getSmAppl().setSmApplRejectReasons(resList);
	}

	private boolean siteVisitDataUpdate() {
		SmSvTask svTask = new SmSvTask();
		List<SmSvAddress> addrList = new ArrayList<SmSvAddress>();
		List<SmSvCategory> smSvCategories = new ArrayList<SmSvCategory>();
		svTask.setSmAppl(getManagedBean().getSmAppl());
		System.out.println("** Basic Office ***"
				+ getManagedBean().getBasicCompanyFacility().getSmAddress()
						.getAddress1() + " -- Id -- "
				+ getManagedBean().getBasicCompanyFacility().getFacilityId());
		SmSvOffice svOff = new SmSvOffice();
		svOff.setSmSvTask(svTask);
		svOff.setSmFacility(getManagedBean().getBasicCompanyFacility());
		svTask.setSmSvOffice(svOff);
		for (SmFacility fac : getManagedBean().getSelectedFactoryFacilities()) {
			SmSvAddress facAddr = new SmSvAddress();
			facAddr.setSmSvTask(svTask);
			facAddr.setSmAddress(fac.getSmAddress());
			addrList.add(facAddr);
		}
		for (SmSupplierBranch br : getManagedBean().getSelectedBranches()) {

			SmSvAddress brAddr = new SmSvAddress();
			brAddr.setSmSvTask(svTask);
			brAddr.setSmAddress(br.getSmAddress());
			addrList.add(brAddr);
		}
		for (SmAssociateCompany assoc : getManagedBean()
				.getSelectedAssocCompanies()) {
			SmSvAddress asocAddr = new SmSvAddress();
			asocAddr.setSmSvTask(svTask);
			asocAddr.setSmAddress(assoc.getSmAddress());
			addrList.add(asocAddr);
		}
		svTask.setSmSvAddresses(addrList);
		List<PmParameter> catParam = new ArrayList<PmParameter>();
		for (SmSupplierCategory cat : getManagedBean().getSelectedCategories()) {
			if (!catParam.contains(cat.getPmCategoryL3().getCategoryTypeCode())) {
				catParam.add(cat.getPmCategoryL3().getCategoryTypeCode());

			}
			SmSvCategory svCat = new SmSvCategory();
			svCat.setSmSvTask(svTask);
			svCat.setPmCategoryL1(cat.getPmCategoryL1());
			svCat.setPmCategoryL2(cat.getPmCategoryL2());
			svCat.setPmCategoryL3(cat.getPmCategoryL3());
			smSvCategories.add(svCat);

		}
		svTask.setSmSvCategories(smSvCategories);
		List<SmSvTemplate> templates = new ArrayList<SmSvTemplate>();
		for (PmParameter param : catParam) {
			SmSvTemplate temp = new SmSvTemplate();
			temp.setPmParameter(param);
			temp.setSmSvTask(svTask);
			templates.add(temp);
		}
		svTask.setSmSvTemplates(templates);
		return updateSiteVisitTask(svTask);

	}

	private boolean updateSiteVisitTask(SmSvTask svTask) {
		ISiteAssignSO siteSO = null;
		try {
			// Save or update to DB
			siteSO = (ISiteAssignSO) BusinessDelegate.getInstance()
					.getServiceObject("sm", ISiteAssignSO.class);
			SiteAssignReqDTO reqDto = new SiteAssignReqDTO();

			reqDto.setSmSvTask(svTask);

			SiteAssignResDTO resDTO = siteSO.updateSiteVisitAssignment(reqDto);
			return resDTO.isSuccessful();
		} catch (Exception e) {
			System.out.println("this is error");
			e.printStackTrace();
			FacesContext
					.getCurrentInstance()
					.addMessage(
							null,
							new FacesMessage(
									FacesMessage.SEVERITY_ERROR,
									"Unknown problem occurred.Could not save the site visit information",
									"Please contact administrator"));

			return false;
		} finally {
			siteSO = null;
		}
	}

	private void setPoOrApprvrSectionReview() {
		if (getManagedBean().isPoUser()
				&& getManagedBean().getPoSecReview().getApplSectionReviewId() != null) {
			for (SmApplSectionReview rev : getManagedBean().getSmAppl()
					.getSmApplSectionReviews()) {
				if (rev.getApplSectionReviewId().equals(
						getManagedBean().getPoSecReview()
								.getApplSectionReviewId())) {
					System.out.println("**** Existing po remarks *******"
							+ getManagedBean().getPoSecReview()
									.getApplSectionReviewId());
					System.out.println("**** Baisc po remarks *******"
							+ getManagedBean().getPoSecReview()
									.getBasicRemark());
					System.out.println("**** Baisc verified po remarks *******"
							+ getManagedBean().getPoSecReview()
									.isBasicVerified());
					getManagedBean()
							.getSmAppl()
							.getSmApplSectionReviews()
							.set(getManagedBean().getSmAppl()
									.getSmApplSectionReviews().indexOf(rev),
									getManagedBean().getPoSecReview());
				}
			}

		}
		if (getManagedBean().isApproverUser()
				&& getManagedBean().getApprvrSecReview()
						.getApplSectionReviewId() != null) {
			System.out.println("**** Existing Apprvr remarks *******");
			for (SmApplSectionReview revs : getManagedBean().getSmAppl()
					.getSmApplSectionReviews()) {
				if (revs.getApplSectionReviewId().equals(
						getManagedBean().getApprvrSecReview()
								.getApplSectionReviewId())) {
					getManagedBean()
							.getSmAppl()
							.getSmApplSectionReviews()
							.set(getManagedBean().getSmAppl()
									.getSmApplSectionReviews().indexOf(revs),
									getManagedBean().getApprvrSecReview());
				}
			}
		}
	}

	private Boolean validateBeforeSave(String applType, String pageType) {
		System.out.println("******* Page Type ********" + pageType);
		switch (pageType) {
		case "BasicInfo":
			if (this.getManagedBean().getSmAppl().getSmFacilities().isEmpty()) {
				this.getManagedBean().getSmAppl().getSmFacilities()
						.add(this.getManagedBean().getBasicCompanyFacility());

			} else {
				// TODO
				for (int i = 0; i < this.getManagedBean().getSmAppl()
						.getSmFacilities().size(); i++) {
					if (this.getManagedBean().getSmAppl().getSmFacilities()
							.get(i).getFacilityType().equalsIgnoreCase("B")
							|| this.getManagedBean().getSmAppl()
									.getSmFacilities().get(i).getFacilityType()
									.equalsIgnoreCase("H")) {
						this.getManagedBean()
								.getSmAppl()
								.getSmFacilities()
								.set(i,
										this.getManagedBean()
												.getBasicCompanyFacility());
						break;
					}
				}
			}
			if (!getManagedBean().getSmAppl().getSmAssociationBodies()
					.isEmpty()) {
				System.out
						.println(" ******* ASSOCIATION BODIES ADDED *********");
				for (SmAssociationBody assoc : getManagedBean().getSmAppl()
						.getSmAssociationBodies()) {
					System.out.println(" ** THE CERT VALUE BEFORE SAVE ***"
							+ assoc.getPmCertBody().getCertBodyId());
				}
			}
			System.out.println("******** Before save cert size ******"
					+ getManagedBean().getSmAppl().getSmSupplierCerts().size());
			break;
		case "CapitalInfo":
			// assignEnteredStaffValues();
			if (getManagedBean().getSmapplShareHolders() == null
					|| getManagedBean().getSmapplShareHolders().isEmpty()) {
				FacesContext
						.getCurrentInstance()
						.addMessage(
								null,
								new FacesMessage(FacesMessage.SEVERITY_ERROR,
										"Empty Shareholders  : ",
										"Please add at least one share holding personnel for the supplier"));
				return false;
			} else if (getManagedBean().getSmAppl().getSmSupplier()
					.getBusinessType().equalsIgnoreCase("2")
					&& getManagedBean().getSmapplShareHolders().size() < 2) {
				FacesContext
						.getCurrentInstance()
						.addMessage(
								null,
								new FacesMessage(
										FacesMessage.SEVERITY_ERROR,
										"Min 2 Shareholing personnel needed  : ",
										"Please add at least 2 share holding personnel for the supplier with business type ROC"));
				return false;
			}
			if (getManagedBean()
					.getSmAppl()
					.getSmCompanyBasic()
					.getPaidUpCapital()
					.compareTo(
							getManagedBean().getSmAppl().getSmCompanyBasic()
									.getAuthorizedCapital()) > 0) {

				FacesContext
						.getCurrentInstance()
						.addMessage(
								null,
								new FacesMessage(FacesMessage.SEVERITY_ERROR,
										"Invalid Paidup Capital  : ",
										"Paid-up capital cannot exceed authorized capital.Re enter the amounts "));
				return false;

			}
			calcPersonnelListCount();
			break;
		case "PersonnelInfo":
			int mgmtCount = 0;
			int staffCount = 0;
			int directorCount = 0;
			int profCount = 0;
			int semiProfCount = 0;

			for (SmPersonnel per : getManagedBean().getSmAppl()
					.getSmPersonnels()) {
				System.out.println("**** EPF Value --- >>>> " + per.getEpfNo());
				if (getManagedBean()
						.getSmAppl()
						.getSupplierType()
						.equalsIgnoreCase(
								SupplierManagementConst.STYPE_CONTRACTOR)) {
					if (per.isDirector() || per.isHigherManagement()) {
						directorCount = directorCount + 1;
					} else if (per.getStaffRoleFlg() != null
							&& per.getStaffRoleFlg() == 834) {
						mgmtCount = mgmtCount + 1;
					} else if (per.getStaffRoleFlg() != null
							&& per.getStaffRoleFlg() == 835) {
						staffCount = staffCount + 1;
					}
				}
				if (getManagedBean()
						.getSmAppl()
						.getSupplierType()
						.equalsIgnoreCase(
								SupplierManagementConst.STYPE_CONSULTANT)) {
					if (per.getStaffRoleFlg() != null
							&& per.getStaffRoleFlg() == 833
							&& per.getEpfNo() != null && per.getEpfNo() != "") {
						// for (SmStaffAcademic aca : per.getSmStaffAcademics())
						// {
						// if (aca.getQualificationLevelId() == 123) {
						// staffCount = staffCount + 1;
						// break;
						// }
						// }
						System.out.println("*** is staff **** ");
						staffCount = staffCount + 1;

					} else if (per.getStaffRoleFlg() != null
							&& per.getStaffRoleFlg() == 831
							&& per.getEpfNo() != null && per.getEpfNo() != "") {
						for (SmStaffAcademic aca : per.getSmStaffAcademics()) {
							if (aca.getQualificationLevelId() >= 83
									&& aca.getQualificationLevelId() <= 85) {
								profCount = profCount + 1;
								break;
							}
						}

					} else if (per.getStaffRoleFlg() != null
							&& per.getStaffRoleFlg() == 832
							&& per.getEpfNo() != null && per.getEpfNo() != "") {
						for (SmStaffAcademic aca : per.getSmStaffAcademics()) {
							if (aca.getQualificationLevelId() >= 83
									&& aca.getQualificationLevelId() <= 86) {
								semiProfCount = semiProfCount + 1;
								break;
							}
						}
					}
				}

			}
			if (getManagedBean().getSmAppl().getSupplierType()
					.equalsIgnoreCase(SupplierManagementConst.STYPE_CONSULTANT)) {
				if (semiProfCount < 1) {
					FacesContext
							.getCurrentInstance()
							.addMessage(
									null,
									new FacesMessage(
											FacesMessage.SEVERITY_ERROR,
											"Semi Professional is required : ",
											"At least one Semi-Professional  (permanent staff who contributed for EPF) staff with at least Diploma is required"));
					return false;
				}
				if (profCount < 1) {
					FacesContext
							.getCurrentInstance()
							.addMessage(
									null,
									new FacesMessage(
											FacesMessage.SEVERITY_ERROR,
											"Professional is required : ",
											"At least one Professional (permanent staff who contributed for EPF) staff with at least Bachelor Degree is required"));
					return false;
				}
				if (staffCount < 1) {
					FacesContext
							.getCurrentInstance()
							.addMessage(
									null,
									new FacesMessage(
											FacesMessage.SEVERITY_ERROR,
											"Clerical Staff is required : ",
											"At least one Clerical Staff (permanent staff who contributed for EPF) staff with at least SijilPelajaran Malaysia or equivalent certificate is required"));
					return false;
				}
			}
			if (getManagedBean().getSmAppl().getSupplierType()
					.equalsIgnoreCase(SupplierManagementConst.STYPE_CONTRACTOR)) {
				if (directorCount < 1) {
					FacesContext
							.getCurrentInstance()
							.addMessage(
									null,
									new FacesMessage(
											FacesMessage.SEVERITY_ERROR,
											"Higher Management or Director is required : ",
											"Please add atleast one user with role Director or Higher Management"));
					return false;
				}
				if (mgmtCount < 1) {
					FacesContext
							.getCurrentInstance()
							.addMessage(
									null,
									new FacesMessage(
											FacesMessage.SEVERITY_ERROR,
											"Management Staff is required : ",
											"Please add atleast one user with role Management"));
					return false;
				}
				if (staffCount < 1) {
					FacesContext
							.getCurrentInstance()
							.addMessage(
									null,
									new FacesMessage(
											FacesMessage.SEVERITY_ERROR,
											"Staff is required : ",
											"Please add atleast one user with role Staff"));
					return false;
				}
			}
			break;
		case "BankInfo":
			int payAccCnt = 0;

			for (SmSupplierBank bank : getManagedBean().getSmAppl()
					.getSmSupplierBanks()) {
				if (bank.getAccountPurpose().equalsIgnoreCase("P")
						|| bank.getAccountPurpose().equalsIgnoreCase("B")) {
					payAccCnt++;
					break;
				}
			}
			if (payAccCnt == 0) {
				FacesContext
						.getCurrentInstance()
						.addMessage(
								null,
								new FacesMessage(FacesMessage.SEVERITY_ERROR,
										"Payment Bank is required : ",
										"Please add atleast one bank account with purpose payment"));
				return false;
			}
			break;
		case "SupportingDocs":
			if (getManagedBean().getSmAppl().getSmSupplierCategories() != null
					&& getManagedBean().getSmAppl().getSmSupplierCategories()
							.size() > 0) {
				for (SmSupplierCategory category : getManagedBean().getSmAppl()
						.getSmSupplierCategories()) {
					if (category.getPmCategoryL3().getCategoryL3Code()
							.equalsIgnoreCase("99")) {
						if (category.getOtherDesc().isEmpty()) {
							FacesContext
									.getCurrentInstance()
									.addMessage(
											null,
											new FacesMessage(
													FacesMessage.SEVERITY_ERROR,
													"Other description is required : ",
													"Please add description to proceed futher"));
							return false;
						}
					}
				}
			}

			if (getManagedBean().isSupplierUser()) {
				System.out.println(" ** For Suporting Docs ** ");
				if (getManagedBean().getSmAppl().getSmSupplierCategories() == null
						|| getManagedBean().getSmAppl()
								.getSmSupplierCategories().isEmpty()) {
					System.out.println("****** Empty Category List *******");
					FacesContext
							.getCurrentInstance()
							.addMessage(
									null,
									new FacesMessage(
											FacesMessage.SEVERITY_ERROR,
											"Category is required : ",
											"Please add atleast one category to proceed futher"));
					return false;
				}
				if (getManagedBean().getSmAppl().getSmApplSupportDocs() == null) {
					getManagedBean().getSmAppl().setSmApplSupportDocs(
							new ArrayList<SmApplSupportDoc>());
				}
				DocumentReqDTO rqDto = new DocumentReqDTO();
				rqDto.setStateCode(getManagedBean().getSmAppl()
						.getSmCompanyBasic().getSmCompanyAddresses().get(0)
						.getSmAddress().getPmState().getStateCode());
				rqDto.setAppTypeCode(getManagedBean().getSmAppl().getApplType());
				if (!getManagedBean().getSmAppl().getSmSupplierCategories()
						.isEmpty()) {
					List<Long> catL3Ids = new ArrayList<Long>();
					for (SmSupplierCategory cat : getManagedBean().getSmAppl()
							.getSmSupplierCategories()) {
						catL3Ids.add(cat.getPmCategoryL3().getCategoryL3Id());
					}
					rqDto.setCategoryL3Ids(catL3Ids);
					System.out
							.println(" **** Total categories sent to Doc So ***"
									+ rqDto.getCategoryL3Ids().size());
				}
				rqDto.setSupplierTypeCode(getManagedBean().getSmAppl()
						.getSupplierType());
				rqDto.setCompanyTypeCode(getManagedBean().getSmAppl()
						.getSmSupplier().getBusinessType());
				DocumentResDTO rsDTO = retrieveSupportingDocumentList(rqDto);

				if (rsDTO != null && rsDTO.getDocumentList() != null
						&& !rsDTO.getDocumentList().isEmpty()) {
					System.out.println("*** Not Empty list ****"
							+ rsDTO.getDocumentList().size());
					List<SmApplSupportDoc> docs = new ArrayList<>();

					if (getManagedBean().getSmAppl().getSmApplSupportDocs()
							.isEmpty()) {
						System.out.println(" ******* Empty Doc List *****");
						for (PmDocument doc : rsDTO.getDocumentList()) {
							docs.add(getDefaultSmApplSupportingDoc(doc));
						}
					} else {
						for (PmDocument doc : rsDTO.getDocumentList()) {
							for (SmApplSupportDoc smDocu : getManagedBean()
									.getSmAppl().getSmApplSupportDocs()) {
								if (smDocu.getPmDocument().getDocumentId()
										.equals(doc.getDocumentId())) {
									// docs.add(smDocu);
									System.out
											.println("******* Document already exists *****");
								} else {
									System.out
											.println("******* This is a new Document ********");
									docs.add(getDefaultSmApplSupportingDoc(doc));
								}
							}
						}
					}

					getManagedBean().getSmAppl().setSmApplSupportDocs(docs);
				}
			}
			break;

		default:
			System.out.println("****** Not a defined page ***********");

		}
		return true;
	}

	private SmApplSupportDoc getDefaultSmApplSupportingDoc(PmDocument doc) {
		SmApplSupportDoc docu = new SmApplSupportDoc();

		docu.setPmDocument(doc);
		docu.setSmAppl(getManagedBean().getSmAppl());
		docu.setVerified(false);
		docu.setVerifiedFlg(0);
		docu.setReceived(false);

		List<SmApplDocFile> files = new ArrayList<SmApplDocFile>();
		SmApplDocFile docFile = new SmApplDocFile();
		docFile.setSmApplSupportDoc(docu);
		docFile.setFileName(" ");
		docFile.setFilePath(" ");
		files.add(docFile);
		docu.setSmApplDocFiles(files);
		return docu;
	}

	public void createNewApplication() throws Exception {
		try {
			System.out.println("**** New BEAN  *************"
					+ getManagedBean().getSmSupplier().getSmAppls().size());
			System.out.println("**** Latest Appl Id   *************"
					+ getManagedBean().getSmSupplier().getSmAppls().get(0)
							.getApplId());
			SmAppl app = getManagedBean().getSmSupplier().getSmAppls().get(0);
			System.out.println(" ******* New Objectr == >> " + app.getApplId()
					+ " Name == " + app.getApplNo());
			getManagedBean().setSmAppl(app);
			// TODO to be confirmed whether reqd to save the appln as draft
			getManagedBean().setTabActiveIndex(outputTab);

		} catch (Exception e) {
			System.out.println("this is error");
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"Sample error message",
							"PrimeFaces makes no mistakes"));
			getManagedBean().setTabActiveIndex(currentTab);
		}

	}

	public void setTabValue(int currentTab, int outputTab) {
		System.out.println(" ***** Tab Values *** " + currentTab + outputTab);
		this.currentTab = currentTab;
		this.outputTab = outputTab;

		// getManagedBean().setTabActiveIndex(outputTab);
	}

	public void assignReviewSupportingNav(String navType) {
		int currentPage = getManagedBean().getTabActiveIndex();
		try {
			this.currentTab = currentPage;
			System.out
					.println("********** ENTER THE PRE RENDER FOR SUPP DOXS ***** "
							+ currentPage + "  , " + navType);
			getManagedBean().getSmAppl().setSupportingDocMode("O");
			if (navType.equalsIgnoreCase("next")) {
				System.out.println("** MY NEXT PAGE ***");
				this.outputTab = currentPage + 1;
			} else if (navType.equalsIgnoreCase("prev")) {
				System.out.println("** MY PREV PAGE ***");
				this.outputTab = currentPage - 1;
			} else {
				this.outputTab = currentPage;
			}
		} catch (Exception e) {
			this.currentTab = currentPage;
			this.outputTab = currentPage;
		}
	}

	public void officeFacilitySelected(AjaxBehaviorEvent e) {
		if (getManagedBean().getBasicCompanyFacility().getOwnershipType()
				.equalsIgnoreCase("O")) {
			System.out.println("******** SELECTED OWN FACILITY *******");
			SmAddress addr = getManagedBean().getBasicCompanyFacility()
					.getSmAddress();
			if (addr.getPmState().getStateId() == null) {
				getManagedBean().getBasicCompanyFacility().getSmAddress()
						.getPmState().setStateId(1l);
			}
			if (addr.getPmDistrict().getDistrictId() == null) {
				getManagedBean().getBasicCompanyFacility().getSmAddress()
						.getPmDistrict().setDistrictId(1l);
			}
			if (addr.getPmCity().getCityId() == null) {
				getManagedBean().getBasicCompanyFacility().getSmAddress()
						.getPmCity().setCityId(1l);
			}
			if (addr.getAddress1() == null) {
				getManagedBean().getBasicCompanyFacility().getSmAddress()
						.setAddress1(" ");
			}
		} else {

		}
		System.out
				.println(" ******* On Change Office Facility ***** "
						+ getManagedBean().getBasicCompanyFacility()
								.getOwnershipType());
		System.out.println(" **** Values In loop *********"
				+ getManagedBean().getBasicCompanyFacility().getRentalRate());
		System.out.println("********* TEst getPmCountry NULL VALUE **********"
				+ this.getManagedBean().getBasicCompanyFacility()
						.getSmAddress().getPmCountry().getCountryId());
	}

	public void assignPersonnelPrivilege(AjaxBehaviorEvent e) {
		System.out.println("**** Enter peronnel privilege ******");
		if (getManagedBean().getSelectedPersonnel().getAuthorizedFlg() == 0) {
			getManagedBean().getSelectedPersonnel().setSoftcertFlg(0);
			getManagedBean().getSelectedPersonnel().setContractSignerFlg(0);
		}

		if (getManagedBean().getSelectedPersonnel().getSoftcertFlg() == 0) {
			getManagedBean().getSelectedPersonnel().setContractSignerFlg(0);
		}
	}

	public void assignShareHolderPrivilege(AjaxBehaviorEvent e) {
		System.out.println("**** Enter Share Holder privilege ******");
		if (getManagedBean().getSelectedShareHolder().getAuthorizedFlg() == 0) {
			getManagedBean().getSelectedShareHolder().setSoftcertFlg(0);
			getManagedBean().getSelectedShareHolder().setContractSignerFlg(0);
		}

		if (getManagedBean().getSelectedShareHolder().getSoftcertFlg() == 0) {
			getManagedBean().getSelectedShareHolder().setContractSignerFlg(0);
		}
	}

	public void applicationCategorySelected(AjaxBehaviorEvent e) {
		System.out
				.println(" ******* On Application Category ***** "
						+ getManagedBean().getSmAppl().getApplCategoryType()
						+ "-- Size --"
						+ getManagedBean().getSmAppl()
								.getSmSupplierCategories().size());
		StringBuffer sb = new StringBuffer();
		String desc = null;
		System.out.println("---- Current Active Tab ---- "
				+ getManagedBean().getSmAppl().getCurrentActiveTab());
		System.out.println("---- Active Tab Index ---- "
				+ getManagedBean().getTabActiveIndex());
		getManagedBean().getSmAppl().setCurrentActiveTab(
				getManagedBean().getTabActiveIndex());
		if (getManagedBean().getSmAppl().getApplCategoryType().equals("N")) {
			desc = "Normal";
		} else {
			desc = "Special";
		}
		sb.append("NOTE : '");
		sb.append(desc);
		sb.append("' categories can only be added in this applicaiton'");
		getManagedBean().setCatTypeConfirm(sb.toString());
		System.out.println("*** Msg Is ****"
				+ getManagedBean().getCatTypeConfirm());
		System.out.println(" -- DOC MODE -- "
				+ getManagedBean().getSmAppl().getSupportingDocMode());
		if (getManagedBean().isPoUser() || getManagedBean().isApproverUser()) {
			assignMaxReviewCount(getManagedBean().getSmAppl().getApplType(),
					getManagedBean().getSmAppl().getApplCategoryType(),
					getManagedBean().getSmAppl().getSupplierType());
		}
	}

	public void assignApplnCategoryType() {
		System.out.println("***** Before confirm +++++++"
				+ getManagedBean().getSmAppl().getSupportingDocMode());
		List<SmSupplierCategory> catList = new ArrayList<SmSupplierCategory>();
		if (getManagedBean().getSmAppl().getApplCategoryType().equals("N")) {
			for (SmSupplierCategory cat : getManagedBean().getSmAppl()
					.getSmSupplierCategories()) {
				if (cat.isSpecialCategory() && !cat.isApprovedByPo()) {
					catList.add(cat);
				}

			}
			getManagedBean().getSmAppl().getSmSupplierCategories()
					.removeAll(catList);
		} else {
			for (SmSupplierCategory cat : getManagedBean().getSmAppl()
					.getSmSupplierCategories()) {
				if (!cat.isSpecialCategory() && !cat.isApprovedByPo()) {
					catList.add(cat);
				}

			}
			getManagedBean().getSmAppl().getSmSupplierCategories()
					.removeAll(catList);
		}
		System.out.println("***** After confirm +++++++"
				+ getManagedBean().getSmAppl().getSupportingDocMode());
		// TODO fix the bug and remove this hard code
		if (getManagedBean().getSmAppl().getSupportingDocMode() == null
				|| getManagedBean().getSmAppl().getSupportingDocMode()
						.equals("")) {
			getManagedBean().getSmAppl().setSupportingDocMode("O");
		}
	}

	public void resetApplnCategoryType() {
		System.out.println("********* Reset the Values ********");
		// getManagedBean().getSmAppl().setApplCategoryType(null);
		getManagedBean().setCatTypeConfirm(null);
		// TODO fix the bug and remove this hard code
		if (getManagedBean().getSmAppl().getSupportingDocMode() == null
				|| getManagedBean().getSmAppl().getSupportingDocMode()
						.equals("")) {
			getManagedBean().getSmAppl().setSupportingDocMode("O");
		}
	}

	public void svcSelected(AjaxBehaviorEvent e) {
		System.out.println(" ******* On Change Office Facility ***** "
				+ getManagedBean().getPoSiteVisitType());

	}

	// -- Process Branches Start
	public void addOrUpdateBranchToSmAppl() {

		if (getManagedBean().getSmAppl().getSmSupplierBranches()
				.contains(getManagedBean().getSelectedBranch())) {

			System.out.println(" **** THIS IS AN UPDATE For Branches******"
					+ getManagedBean().getSelectedBranch().getPhoneNo());
		} else {
			getManagedBean().getSmAppl().getSmSupplierBranches()
					.add(getManagedBean().getSelectedBranch());
		}
	}

	public void assignSelBranch(SmSupplierBranch branchBean, int index) {
		System.out.println("****** Edit Index **********" + index);
		getManagedBean()
				.setSelectedBranchPrev(
						getManagedBean().getSmAppl().getSmSupplierBranches()
								.get(index));
		getManagedBean().setSelectedBranchIndex(index);
		System.out.println("***** Before *****"
				+ getManagedBean().getSelectedBranchPrev().getSmAddress()
						.getAddress1());
		getManagedBean().setSelectedBranch(branchBean);
		System.out.println("***** After *****"
				+ getManagedBean().getSelectedBranchPrev().getSmAddress()
						.getAddress1());

	}

	public void assignNewBranchBean() {
		getManagedBean().setSelectedBranch(new SmSupplierBranch());
		getManagedBean().getSelectedBranch().setRevNo(1);
		getManagedBean().getSelectedBranch().setSmAddress((setDefaultAddr()));
		getManagedBean().getSelectedBranch().setSmAppl(
				getManagedBean().getSmAppl());
		getManagedBean().setBranchCompanyStates(
				retrievePmStateList(getManagedBean().getSelectedBranch()
						.getSmAddress().getPmCountry().getCountryId()));
	}

	public void assignCancelBranchBean() {
		if (!getManagedBean().getSmAppl().getSmSupplierBranches().isEmpty()) {
			System.out.println("******** Into cancel *******"
					+ getManagedBean().getSelectedBranchPrev().getSmAddress()
							.getAddress1() + " **** For Index ********"
					+ getManagedBean().getSelectedBranchIndex());
			getManagedBean()
					.getSmAppl()
					.getSmSupplierBranches()
					.set(getManagedBean().getSelectedBranchIndex(),
							getManagedBean().getSelectedBranchPrev());
		}
		assignNewBranchBean();
	}

	public void deleteSelectedBranches() {

		System.out.println("*** Delete Branches Companies ******* "
				+ getManagedBean().getSelectedBranches().size());
		if (getManagedBean().getSelectedBranches().size() > 0) {
			for (SmSupplierBranch brn : getManagedBean().getSelectedBranches()) {
				getManagedBean().getSmAppl().getSmSupplierBranches()
						.remove(brn);
			}
			getManagedBean().getSelectedBranches().clear();
		} else {
			System.out
					.println(" ******** NO Record selected please select atleast one ");
		}

	}

	// -- Process Branches End

	// -- Process Associated Companies Start
	public void addOrUpdateAssocToSmAppl() {

		if (getManagedBean().getSmAppl().getSmAssociateCompanies()
				.contains(getManagedBean().getSelectedAssocCompany())) {

			System.out.println(" **** THIS IS AN UPDATE Assoc Companies******");
		} else {
			getManagedBean().getSmAppl().getSmAssociateCompanies()
					.add(getManagedBean().getSelectedAssocCompany());
		}
	}

	public void assignSelAssoc(SmAssociateCompany assocBean) {
		getManagedBean().setSelectedAssocCompany(assocBean);
	}

	public void assignNewAssocBean() {
		getManagedBean().setSelectedAssocCompany(new SmAssociateCompany());
		getManagedBean().getSelectedAssocCompany().setRevNo(1);
		getManagedBean().getSelectedAssocCompany().setSmAddress(
				(setDefaultAddr()));
		getManagedBean().getSelectedAssocCompany().setSmAppl(
				getManagedBean().getSmAppl());
		getManagedBean().setAssocCompanyStates(
				retrievePmStateList(getManagedBean().getSelectedAssocCompany()
						.getSmAddress().getPmCountry().getCountryId()));
	}

	public void deleteSelectedAssocCompanies() {
		System.out.println("*** Delete Assoc Companies ******* "
				+ getManagedBean().getSelectedAssocCompanies().size());
		if (getManagedBean().getSelectedAssocCompanies().size() > 0) {
			for (SmAssociateCompany assoc : getManagedBean()
					.getSelectedAssocCompanies()) {
				getManagedBean().getSmAppl().getSmAssociateCompanies()
						.remove(assoc);
			}
			getManagedBean().getSelectedAssocCompanies().clear();
		} else {
			System.out
					.println(" ******** NO Record selected please select atleast one ");
		}
	}

	// -- Process Associated Companies End

	// -- Process Certs Start
	public void addOrUpdateCertsToSmAppl() throws ParseException {
		getManagedBean().getSelectedCert().setSmAppl(
				getManagedBean().getSmAppl());
		if (getManagedBean().getSmAppl().getSmSupplierCerts()
				.contains(getManagedBean().getSelectedCert())) {

			System.out.println(" **** THIS IS AN UPDATE Certs******");
		} else {
			System.out.println(" **** THIS IS AN SAVE NEW Certs******");
			getManagedBean().getSmAppl().getSmSupplierCerts()
					.add(getManagedBean().getSelectedCert());
		}
	}

	public void assignSelCert(SmSupplierCert certBean) {
		getManagedBean().setSelectedCert(certBean);
	}

	public void assignNewCert() {
		SmSupplierCert cert = new SmSupplierCert();
		cert.setPmCertBody(new PmCertBody());
		cert.setRevNo(1);
		getManagedBean().setSelectedCert(cert);
		cert.setSmAppl(getManagedBean().getSmAppl());
	}

	public void deleteSelectedCerts() {
		System.out.println("******** Before delete ******"
				+ getManagedBean().getSmAppl().getSmSupplierCerts().size());
		System.out.println("*** Delete Certs ******* "
				+ getManagedBean().getSelectedCertList().size());
		if (getManagedBean().getSelectedCertList().size() > 0) {
			for (SmSupplierCert cert : getManagedBean().getSelectedCertList()) {
				getManagedBean().getSmAppl().getSmSupplierCerts().remove(cert);
			}
			getManagedBean().getSelectedCertList().clear();
		} else {
			System.out
					.println(" ******** NO Record selected please select atleast one ");
		}
		System.out.println("******** After delete ******"
				+ getManagedBean().getSmAppl().getSmSupplierCerts().size());
	}

	// -- Process Certss End
	// -- Process Assoc Bodies Start
	public void addOrUpdateAssocBodiesToSmAppl() throws ParseException {
		getManagedBean().getSelectedAssocBody().setSmAppl(
				getManagedBean().getSmAppl());
		if (getManagedBean().getSmAppl().getSmAssociationBodies()
				.contains(getManagedBean().getSelectedAssocBody())) {

			System.out.println(" **** THIS IS AN UPDATE Certs******");
		} else {
			System.out
					.println(" **** THIS IS AN SAVE NEW Association Body******"
							+ getManagedBean().getSelectedAssocBody()
									.getRevNo()
							+ " *** Sample id *** "
							+ getManagedBean().getSelectedAssocBody()
									.getPmCertBody().getCertBodyId());
			getManagedBean().getSmAppl().getSmAssociationBodies()
					.add(getManagedBean().getSelectedAssocBody());
		}
	}

	public void assignSelAssocBody(SmAssociationBody assocBean) {
		getManagedBean().setSelectedAssocBody(assocBean);
	}

	public void assignNewAssocBody() {
		SmAssociationBody assocBean = new SmAssociationBody();
		assocBean.setPmCertBody(new PmCertBody());
		assocBean.setRevNo(1);
		getManagedBean().setSelectedAssocBody(assocBean);
	}

	public void deleteSelectedAssocBodies() {

		System.out.println("*** Delete AssocBodies ******* "
				+ getManagedBean().getSelectedAssocBodyList().size());
		if (getManagedBean().getSelectedAssocBodyList().size() > 0) {
			for (SmAssociationBody assoc : getManagedBean()
					.getSelectedAssocBodyList()) {
				getManagedBean().getSmAppl().getSmAssociationBodies()
						.remove(assoc);
			}
			getManagedBean().getSelectedAssocBodyList().clear();
		} else {
			System.out
					.println(" ******** NO Record selected please select atleast one ");
		}

	}

	// -- Process Assoc Bodies End
	// -- Process Contract Projs Start
	public void addOrUpdateContractProjsToSmAppl() {
		// if (getManagedBean().getSmAppl().getSmContractExperiences()
		// .contains(getManagedBean().getSelectedContractProj())) {
		//
		// System.out
		// .println(" **** THIS IS AN UPDATE FOR Contract Prjs******");
		// } else {
		// getManagedBean().getSmAppl().getSmContractExperiences()
		// .add(getManagedBean().getSelectedContractProj());
		// }
	}

	// public void assignSelContractProj(SmContractExperience contractBean) {
	// getManagedBean().setSelectedContractProj(contractBean);
	// }
	//
	// public void assignNewContractProj() {
	// getManagedBean().setSelectedContractProj(new SmContractExperience());
	// }

	// -- Process Contract Projs End

	// -- Process Consultant or Contractor Company Experience Start
	public void addOrUpdateCompanyExperienceToSmAppl() {
		if (getManagedBean().getSelectedCompanyExperience()
				.getPaymentMethodId() == null) {
			getManagedBean().getSelectedCompanyExperience().setPaymentMethodId(
					new BigDecimal(0));
		}
		if (getManagedBean().getSmAppl().getSmCompanyExperiences()
				.contains(getManagedBean().getSelectedCompanyExperience())) {

			System.out
					.println(" **** THIS IS AN UPDATE FOR Consultant Prjs******");
		} else {
			System.out
					.println(" **** THIS IS AN ADD FOR Consultant Prjs******");
			getManagedBean().getSmAppl().getSmCompanyExperiences()
					.add(getManagedBean().getSelectedCompanyExperience());
		}
	}

	public void assignSelCompanyExperience(SmCompanyExperience consultantBean) {
		getManagedBean().setSelectedCompanyExperience(consultantBean);
	}

	public void assignNewCompanyExperience() {
		SmCompanyExperience exp = new SmCompanyExperience();
		exp.setRevNo(1);
		exp.setSupplierType(getManagedBean().getSmAppl().getSupplierType());
		exp.setSmAppl(getManagedBean().getSmAppl());
		if (getManagedBean().getSmAppl().getSupplierType()
				.equals(SupplierManagementConst.STYPE_CONSULTANT)) {
			exp.setProcurementModeId(new BigDecimal(0));
		}
		getManagedBean().setSelectedCompanyExperience(exp);
	}

	public void deleteSelectedCompanyExperiences() {

		System.out.println("*** Delete Exps ******* "
				+ getManagedBean().getSelectedCompanyExperiences().size());
		if (getManagedBean().getSelectedCompanyExperiences().size() > 0) {
			for (SmCompanyExperience exp : getManagedBean()
					.getSelectedCompanyExperiences()) {
				getManagedBean().getSmAppl().getSmCompanyExperiences()
						.remove(exp);
			}
			getManagedBean().getSelectedCompanyExperiences().clear();
		} else {
			System.out
					.println(" ******** NO Record selected please select atleast one ");
		}

	}

	// ---- Process Consultant or Contractor Company Experience End

	// -- Process Equity Companies Start
	public void addOrUpdateEquityCompanyToSmAppl() {

		if (getManagedBean().getSmAppl().getSmEquityCompanies()
				.contains(getManagedBean().getSelectedEquityCompany())) {

			System.out
					.println(" **** THIS IS AN UPDATE Equity Companies******");
		} else {
			System.out.println("****** ADD EQUIRY ********* ");
			getManagedBean().getSmAppl().getSmEquityCompanies()
					.add(getManagedBean().getSelectedEquityCompany());
		}
	}

	public void assignSelEquiCompany(SmEquityCompany equiCompBean) {
		getManagedBean().setSelectedEquityCompany(equiCompBean);
	}

	public void assignNewEquiCompBean() {

		getManagedBean().setSelectedEquityCompany(new SmEquityCompany());
		getManagedBean().getSelectedEquityCompany().setRevNo(1);
		getManagedBean().getSelectedEquityCompany().setSmAddress(
				(setDefaultAddr()));
		getManagedBean().getSelectedEquityCompany().setSmAppl(
				getManagedBean().getSmAppl());
		getManagedBean().setEquiCompanyStates(
				retrievePmStateList(getManagedBean().getSelectedEquityCompany()
						.getSmAddress().getPmCountry().getCountryId()));

	}

	public void deleteSelectedEquiCompanies() {

		if (getManagedBean().getSelectedEquityCompanies().size() > 0) {
			for (SmEquityCompany equi : getManagedBean()
					.getSelectedEquityCompanies()) {
				getManagedBean().getSmAppl().getSmEquityCompanies()
						.remove(equi);
			}
			getManagedBean().getSelectedEquityCompanies().clear();
		} else {
			System.out
					.println(" ******** NO Record selected please select atleast one ");
		}

	}

	// -- Process Equity Companies End

	// -- Process Category Codes Start

	public void changeParentCategory(AjaxBehaviorEvent e) {
		String selPmCatL1 = getManagedBean().getSelectedParentCat();
		if (selPmCatL1 == null) {
			PmCategoryL1 p = new PmCategoryL1();
			p.setPmCategoryL2s(new ArrayList<PmCategoryL2>());
			getManagedBean().getSelectedCategory().setPmCategoryL1(p);
		} else {
			for (PmCategoryL1 p : getManagedBean().getDefaultParentCatList()) {
				if (p.getCategoryL1Code().equalsIgnoreCase(selPmCatL1)) {
					if (p.getPmCategoryL2s() == null) {
						p.setPmCategoryL2s(new ArrayList<PmCategoryL2>());
					}
					getManagedBean().getSelectedCategory().setPmCategoryL1(p);

				}
			}
		}
		PmCategoryL2 p2 = new PmCategoryL2();
		p2.setPmCategoryL3s(new ArrayList<PmCategoryL3>());
		getManagedBean().getSelectedCategory().setPmCategoryL2(
				new PmCategoryL2());
		getManagedBean().getSelectedCategory().setPmCategoryL3(
				new PmCategoryL3());
		getManagedBean().setSelectedChildCat(null);
		getManagedBean().setSelectedSubChildCat(null);

	}

	public void changeChildCategory(AjaxBehaviorEvent e) {
		String selPmCatL2 = getManagedBean().getSelectedChildCat();
		if (selPmCatL2 == null) {
			PmCategoryL2 p2 = new PmCategoryL2();
			p2.setPmCategoryL3s(new ArrayList<PmCategoryL3>());
			getManagedBean().getSelectedCategory().setPmCategoryL2(p2);
		} else {
			for (PmCategoryL2 p2 : getManagedBean().getSelectedCategory()
					.getPmCategoryL1().getPmCategoryL2s()) {
				if (p2.getCategoryL2Code().equalsIgnoreCase(selPmCatL2)) {
					if (p2.getPmCategoryL3s() == null) {
						p2.setPmCategoryL3s(new ArrayList<PmCategoryL3>());
					}
					getManagedBean().getSelectedCategory().setPmCategoryL2(p2);

				}
			}
		}
		getManagedBean().getSelectedCategory().setPmCategoryL3(
				new PmCategoryL3());
		getManagedBean().setSelectedSubChildCat(null);

	}

	public void changeSubChildCategory(AjaxBehaviorEvent e) {
		String selPmCatL3 = getManagedBean().getSelectedSubChildCat();
		if (selPmCatL3 == null) {
			System.out.println(" --- Selected is a null --- ");
			PmCategoryL3 p3 = new PmCategoryL3();
			getManagedBean().getSelectedCategory().setPmCategoryL3(p3);
		} else {
			for (PmCategoryL3 p3 : getManagedBean().getSelectedCategory()
					.getPmCategoryL2().getPmCategoryL3s()) {
				if (p3.getCategoryL3Code().equalsIgnoreCase(selPmCatL3)) {
					System.out.println("***** Selected Child is ********"
							+ p3.getCategoryL3Code() + " --- Name ----"
							+ p3.getCategoryName());
					if (p3.getCategoryL3Code().equals("1")) {
					} else if (p3.getCategoryL3Code().equals("2")) {
						getManagedBean().getSelectedManufacturer().setPmUom(
								new PmUom());
					} else if (p3.getCategoryL3Code().equals("3")) {
					} else {
					}
					getManagedBean().getSelectedCategory().setPmCategoryL3(p3);

				}
			}
		}

	}

	public void changeCatEntryModeValues(AjaxBehaviorEvent e) {
		// System.out.println("**** ENTER THE CHANGE OF CATEGORY TYPE **** "
		// + getManagedBean().getSelectedCategory().getEntryMode());
		resetSelectedCategoryParams();

	}

	/**
	 * resetting all the values
	 * 
	 * @param e
	 */
	private void resetSelectedCategoryParams() {
		// getManagedBean().setSelectedSubChildCat(null);
		// getManagedBean().setSelectedChildCat(null);
		// getManagedBean().setSelectedParentCat(null);
		PmCategoryL1 p = new PmCategoryL1();
		p.setPmCategoryL2s(new ArrayList<PmCategoryL2>());
		getManagedBean().getSelectedCategory().setPmCategoryL1(p);
		PmCategoryL2 p2 = new PmCategoryL2();
		p2.setPmCategoryL3s(new ArrayList<PmCategoryL3>());
		getManagedBean().getSelectedCategory().setPmCategoryL2(p2);
		PmCategoryL3 p3 = new PmCategoryL3();
		getManagedBean().getSelectedCategory().setPmCategoryL3(p3);
	}

	public void assignNewCategoryCode() {
		boolean validCatCode = true;
		if (getManagedBean().getSmAppl().getSmSupplierCategories().size() == 30) {
			FacesContext
					.getCurrentInstance()
					.addMessage(
							null,
							new FacesMessage(
									FacesMessage.SEVERITY_ERROR,
									"Max Limit Reached : ",
									"Only 30 Category Codes can be add. Please contact administrator to exceed the limit"));
			validCatCode = false;
		}

		if (validCatCode) {
			SmSupplierCategory categ = new SmSupplierCategory();
			categ.setRevNo(1);
			if (getManagedBean().getSmAppl().getApplCategoryType()
					.equalsIgnoreCase("S")) {
				categ.setSpecialCategory(true);
			}
			categ.setSmAppl(getManagedBean().getSmAppl());
			categ.setSupplierType(getManagedBean().getSmAppl()
					.getSupplierType());
			getManagedBean().setSelectedCategory(categ);
			resetSelectedCategoryParams();
			RequestContext.getCurrentInstance().execute("regCatDialog.show()");
		}
	}

	public void deleteSelectedCategoryCodes() {
		if (getManagedBean().getDeleteCategoryList().size() > 0) {
			List<SmSupplierCategory> categories = getManagedBean()
					.getDeleteCategoryList();

			for (SmSupplierCategory category : categories) {
				getManagedBean().getSmAppl().getSmSupplierCategories()
						.remove(category);
			}
		}
	}

	public void saveSelectedCategory() {
		// System.out.println(" ******* Save From Selection ****"
		// + getManagedBean().getSelectedCategory().getCategoryType());
		boolean validCat = true;
		System.out.println("***** cat id ********"
				+ getManagedBean().getSelectedCategory().getPmCategoryL1()
						.getCategoryL1Id());
		if (getManagedBean().getSelectedCategory().getPmCategoryL1()
				.getCategoryL1Id() == 21) {
			System.out.println("****** Selected ICT ************");
			validCat = validateIctCatCode();
		}

		if (validCat) {
			for (PmCategoryL1 catL1 : getManagedBean()
					.getSupplierParentCatList()) {
				if (catL1.getCategoryL1Id().equals(
						getManagedBean().getSelectedCategory()
								.getPmCategoryL1().getCategoryL1Id())) {
					getManagedBean().getSelectedCategory().setPmCategoryL1(
							catL1);
					break;
				}
			}
			for (PmCategoryL2 catL2 : getManagedBean()
					.getSupplierChildCatList()) {
				if (catL2.getCategoryL2Id().equals(
						getManagedBean().getSelectedCategory()
								.getPmCategoryL2().getCategoryL2Id())) {
					getManagedBean().getSelectedCategory().setPmCategoryL2(
							catL2);
					break;
				}
			}
			for (PmCategoryL3 catL3 : getManagedBean()
					.getSupplierSubChildCatList()) {
				if (catL3.getCategoryL3Id().equals(
						getManagedBean().getSelectedCategory()
								.getPmCategoryL3().getCategoryL3Id())) {
					getManagedBean().getSelectedCategory().setPmCategoryL3(
							catL3);
					break;
				}
			}
			getManagedBean().getSmAppl().getSmSupplierCategories()
					.add(getManagedBean().getSelectedCategory());

			RequestContext.getCurrentInstance().execute("regCatDialog.hide()");

		}
	}

	// TODO needs many validations and SO retrieve the PMcat1,2,3 details - To
	// be impl in future
	// public void saveEnteredCategory() {
	// // System.out.println(" ******* Save From Entry ****"
	// // + getManagedBean().getSelectedCategory().getCategoryType());
	// PmCategoryL1 p = new PmCategoryL1();
	// p.setCategoryL1Code(getManagedBean().getSelectedParentCat());
	// p.setCategoryName("ParentSample");
	// getManagedBean().getSelectedCategory().setPmCategoryL1(p);
	// PmCategoryL2 p2 = new PmCategoryL2();
	// p2.setCategoryL2Code(getManagedBean().getSelectedChildCat());
	// p2.setCategoryName("ChildSample");
	// getManagedBean().getSelectedCategory().setPmCategoryL2(p2);
	// PmCategoryL3 p3 = new PmCategoryL3();
	// p3.setCategoryL3Code(getManagedBean().getSelectedSubChildCat());
	// p3.setCategoryName("SubChildSample");
	// getManagedBean().getSelectedCategory().setPmCategoryL3(p3);
	// getManagedBean().getSmAppl().getSmSupplierCategories()
	// .add(getManagedBean().getSelectedCategory());
	//
	// }

	// -- Process Category Codes End
	// factory start
	public void addOrUpdateFactoryToSmAppl() {
		SmFacility smFac = getManagedBean().getSelectedFactoryFacility();
		if (getManagedBean().getSmAppl().getSmFacilities().contains(smFac)) {

			if (getManagedBean().getApplFactoryFacilities().contains(smFac)) {
				System.out
						.println(" ******* THIS IS EDIT FOR EXISITING FACTORY **********");
			}

		} else {
			getManagedBean().getApplFactoryFacilities().add(smFac);
			getManagedBean().getSmAppl().getSmFacilities().add(smFac);
		}

	}

	public void assignSelFactory(SmFacility facilityBean) {
		System.out.println("******** Selected value ******"
				+ facilityBean.getSmAddress().getAddress1());
		getManagedBean().setSelectedFactoryFacility(facilityBean);
	}

	public void assignNewFactoryBean() {
		getManagedBean().setSelectedFactoryFacility(new SmFacility());
		getManagedBean().getSelectedFactoryFacility().setRevNo(1);
		getManagedBean().getSelectedFactoryFacility().setSmAddress(
				(setDefaultAddr()));
		getManagedBean().getSelectedFactoryFacility().setSmAppl(
				getManagedBean().getSmAppl());
		getManagedBean().getSelectedFactoryFacility().setFacilityType(
				SupplierManagementConst.SM_FACILITY_TYPE_FACTORY);
		getManagedBean().setFactoryFacilityStates(
				retrievePmStateList(getManagedBean()
						.getSelectedFactoryFacility().getSmAddress()
						.getPmCountry().getCountryId()));
	}

	public void factoryOwnerTypeSel(AjaxBehaviorEvent e) {
		System.out.println("Selected Owner type === "
				+ getManagedBean().getSelectedFactoryFacility()
						.getOwnershipType());
	}

	public void deleteSelectedFactories() {

		System.out.println("*** Delete Branches Companies ******* "
				+ getManagedBean().getSelectedFactoryFacilities().size());
		if (getManagedBean().getSelectedFactoryFacilities().size() > 0) {
			for (SmFacility sm : getManagedBean()
					.getSelectedFactoryFacilities()) {
				getManagedBean().getSmAppl().getSmFacilities().remove(sm);
				getManagedBean().getApplFactoryFacilities().remove(sm);
			}
			getManagedBean().getSelectedFactoryFacilities().clear();
		} else {
			System.out
					.println(" ******** NO Record selected please select atleast one ");
		}

	}

	// factory end

	// Dockyard start
	public void addOrUpdateDockYardToSmAppl() {
		SmFacility smFac = getManagedBean().getSelectedDockyardFacility();
		System.out.println("**** This is dock yard data ********"
				+ smFac.getSmAddress().getAddress1());
		if (getManagedBean().getSmAppl().getSmFacilities().contains(smFac)) {

			if (getManagedBean().getApplDockyardFacilities().contains(smFac)) {
				System.out
						.println(" ******* THIS IS EDIT FOR EXISITING Doc **********");
			}

		} else {
			getManagedBean().getApplDockyardFacilities().add(smFac);
			getManagedBean().getSmAppl().getSmFacilities().add(smFac);
		}

	}

	public void assignSelDockyard(SmFacility facilityBean) {
		System.out.println("******** Selected dock value ******"
				+ facilityBean.getSmAddress().getAddress1());
		getManagedBean().setSelectedDockyardFacility(facilityBean);
	}

	public void assignNewDockyardBean() {
		System.out.println(" ********** New doc yard value *********");
		getManagedBean().setSelectedDockyardFacility(new SmFacility());
		getManagedBean().getSelectedDockyardFacility().setRevNo(1);
		getManagedBean().getSelectedDockyardFacility().setSmAddress(
				(setDefaultAddr()));
		getManagedBean().getSelectedDockyardFacility().setSmAppl(
				getManagedBean().getSmAppl());
		getManagedBean().getSelectedDockyardFacility().setFacilityType(
				SupplierManagementConst.SM_FACILITY_TYPE_DOCKYARD);
		getManagedBean().setDockFacilityStates(
				retrievePmStateList(getManagedBean()
						.getSelectedFactoryFacility().getSmAddress()
						.getPmCountry().getCountryId()));
	}

	public void dockOwnerTypeSel(AjaxBehaviorEvent e) {
		System.out.println("Selected Owner type === "
				+ getManagedBean().getSelectedDockyardFacility()
						.getOwnershipType());
	}

	public void deleteSelectedDocks() {

		System.out.println("*** Delete Dockyars ******* "
				+ getManagedBean().getSelectedDockyardFacilities().size());
		if (getManagedBean().getSelectedDockyardFacilities().size() > 0) {
			for (SmFacility sm : getManagedBean()
					.getSelectedDockyardFacilities()) {
				getManagedBean().getSmAppl().getSmFacilities().remove(sm);
				getManagedBean().getApplDockyardFacilities().remove(sm);
			}
			getManagedBean().getSelectedDockyardFacilities().clear();
		} else {
			System.out
					.println(" ******** NO Record selected please select atleast one ");
		}

	}

	// Dockyard end
	// Workshop start
	public void addOrUpdateWorkshopToSmAppl() {
		SmFacility smFac = getManagedBean().getSelectedWorkShopFacility();
		if (getManagedBean().getSmAppl().getSmFacilities().contains(smFac)) {

			if (getManagedBean().getApplWorkShopFacilities().contains(smFac)) {
				System.out
						.println(" ******* THIS IS EDIT FOR EXISITING workshop **********");
			}

		} else {
			getManagedBean().getApplWorkShopFacilities().add(smFac);
			getManagedBean().getSmAppl().getSmFacilities().add(smFac);
		}

	}

	public void assignSelWorkshop(SmFacility facilityBean) {
		getManagedBean().setSelectedWorkShopFacility(facilityBean);
	}

	public void assignNewWorkshopBean() {
		getManagedBean().setSelectedWorkShopFacility(new SmFacility());
		getManagedBean().getSelectedWorkShopFacility().setRevNo(1);
		getManagedBean().getSelectedWorkShopFacility().setSmAddress(
				(setDefaultAddr()));
		getManagedBean().getSelectedWorkShopFacility().setSmAppl(
				getManagedBean().getSmAppl());
		getManagedBean().getSelectedWorkShopFacility().setFacilityType(
				SupplierManagementConst.SM_FACILITY_TYPE_WORKSHOP);
		getManagedBean().setWorkShopFacilityStates(
				retrievePmStateList(getManagedBean()
						.getSelectedFactoryFacility().getSmAddress()
						.getPmCountry().getCountryId()));
	}

	public void workShopOwnerTypeSel(AjaxBehaviorEvent e) {
		System.out.println("Selected Owner type === "
				+ getManagedBean().getSelectedWorkShopFacility()
						.getOwnershipType());
	}

	public void deleteSelectedWorkShops() {
		if (getManagedBean().getSelectedWorkShopFacilities().size() > 0) {
			for (SmFacility sm : getManagedBean()
					.getSelectedWorkShopFacilities()) {
				getManagedBean().getSmAppl().getSmFacilities().remove(sm);
				getManagedBean().getApplWorkShopFacilities().remove(sm);
			}
			getManagedBean().getSelectedWorkShopFacilities().clear();
		} else {
			System.out
					.println(" ******** NO Record selected please select atleast one ");
		}

	}

	// Workshop end
	// Workshop start
	public void addOrUpdateStoreToSmAppl() {
		SmFacility smFac = getManagedBean().getSelectedStoreFacility();
		if (getManagedBean().getSmAppl().getSmFacilities().contains(smFac)) {

			if (getManagedBean().getApplStoreFacilities().contains(smFac)) {
				System.out
						.println(" ******* THIS IS EDIT FOR EXISITING workshop **********");
			}

		} else {
			getManagedBean().getApplStoreFacilities().add(smFac);
			getManagedBean().getSmAppl().getSmFacilities().add(smFac);
		}

	}

	public void assignSelStore(SmFacility facilityBean) {
		getManagedBean().setSelectedStoreFacility(facilityBean);
	}

	public void assignNewStoreBean() {
		getManagedBean().setSelectedStoreFacility(new SmFacility());
		getManagedBean().getSelectedStoreFacility().setRevNo(1);
		getManagedBean().getSelectedStoreFacility().setSmAddress(
				(setDefaultAddr()));
		getManagedBean().getSelectedStoreFacility().setSmAppl(
				getManagedBean().getSmAppl());
		getManagedBean().getSelectedStoreFacility().setFacilityType(
				SupplierManagementConst.SM_FACILITY_TYPE_STORE);
		getManagedBean().setStoreFacilityStates(
				retrievePmStateList(getManagedBean()
						.getSelectedFactoryFacility().getSmAddress()
						.getPmCountry().getCountryId()));
	}

	public void storeOwnerTypeSel(AjaxBehaviorEvent e) {
		System.out.println("Selected Owner type === "
				+ getManagedBean().getSelectedStoreFacility()
						.getOwnershipType());
	}

	public void deleteSelectedStores() {

		if (getManagedBean().getSelectedStoreFacilities().size() > 0) {
			for (SmFacility sm : getManagedBean().getSelectedStoreFacilities()) {
				getManagedBean().getSmAppl().getSmFacilities().remove(sm);
				getManagedBean().getApplStoreFacilities().remove(sm);
			}
			getManagedBean().getSelectedStoreFacilities().clear();
		} else {
			System.out
					.println(" ******** NO Record selected please select atleast one ");
		}

	}

	// Workshop end
	// manufacturer start
	public void addOrUpdateManufacturerToSmAppl() {
		if (getManagedBean().getSelectedManufacturer().getPmUom().getUomId() != null) {
			for (PmUom uom : getManagedBean().getUomLs()) {
				if (uom.getUomId().equals(
						getManagedBean().getSelectedManufacturer().getPmUom()
								.getUomId())) {
					getManagedBean().getSelectedManufacturer().setPmUom(uom);
					break;
				}
			}
		}
		if (getManagedBean().getSmAppl().getSmManufacturingDtls()
				.contains(getManagedBean().getSelectedManufacturer())) {

			System.out.println(" **** THIS IS AN UPDATE For manufacturer******"
					+ getManagedBean().getSelectedManufacturer().getBrand());
		} else {

			getManagedBean().getSmAppl().getSmManufacturingDtls()
					.add(getManagedBean().getSelectedManufacturer());
		}
	}

	public void assignSelManufacturer(SmManufacturingDtl manBean) {
		getManagedBean().setSelectedManufacturer(manBean);
	}

	public void assignNewManufacturerBean() {
		System.out.println("**** Add new Manufacturer *********");
		SmManufacturingDtl manu = new SmManufacturingDtl();
		manu.setPmUom(new PmUom());
		manu.setRevNo(1);
		manu.setSmAppl(getManagedBean().getSmAppl());
		getManagedBean().setSelectedManufacturer(manu);
		System.out.println("****** After **********"
				+ getManagedBean().getSelectedManufacturer().getPmUom()
						.getUomName());

	}

	public void deleteSelectedManufacturers() {

		if (getManagedBean().getSelectedManufacturingList().size() > 0) {
			for (SmManufacturingDtl man : getManagedBean()
					.getSelectedManufacturingList()) {
				getManagedBean().getSmAppl().getSmManufacturingDtls()
						.remove(man);
			}
			getManagedBean().getSelectedManufacturingList().clear();
		} else {
			System.out
					.println(" ******** NO Record selected please select atleast one ");
		}
	}

	// manufacturer end

	// machinery start
	public void addOrUpdateMachineToSmAppl() {

		if (getManagedBean().getSmAppl().getSmMachineDtls()
				.contains(getManagedBean().getSelectedMachine())) {

			System.out.println(" **** THIS IS AN UPDATE For machinery ******"
					+ getManagedBean().getSelectedMachine().getBrandName());
		} else {

			getManagedBean().getSmAppl().getSmMachineDtls()
					.add(getManagedBean().getSelectedMachine());
		}
	}

	public void assignSelMachinery(SmMachineDtl macBean) {
		getManagedBean().setSelectedMachine(macBean);
	}

	public void assignNewMachineryBean() {
		getManagedBean().setSelectedMachine(new SmMachineDtl());
		getManagedBean().getSelectedMachine().setRevNo(1);
		getManagedBean().getSelectedMachine().setSmAppl(
				getManagedBean().getSmAppl());
	}

	public void deleteSelectedMachinery() {

		if (getManagedBean().getSelectedMachineList().size() > 0) {
			for (SmMachineDtl mac : getManagedBean().getSelectedMachineList()) {
				getManagedBean().getSmAppl().getSmMachineDtls().remove(mac);
			}
			getManagedBean().getSelectedMachineList().clear();
		} else {
			System.out
					.println(" ******** NO Record selected please select atleast one ");
		}
	}

	// manufacturer end

	// -- Process Share Holder Personnel Start
	public void addOrUpdateShareHolderToSmAppl(String fromPage)
			throws ParseException {
		boolean validShrHldr = true;
		if (getManagedBean().getSmAppl().getSupplierType()
				.equalsIgnoreCase(SupplierManagementConst.STYPE_CONSULTANT)
				&& getManagedBean().getSelectedShareHolder()
						.getWorkExperienceYear() < 5) {
			System.out
					.println(" ******* This is a failure condition ********* "
							+ getManagedBean().getSelectedShareHolder()
									.getWorkExperienceYear());
			FacesContext
					.getCurrentInstance()
					.addMessage(
							null,
							new FacesMessage(FacesMessage.SEVERITY_ERROR,
									"Insufficient Experience : ",
									"The equity owner must have a minimum of 5 years work experience"));
			validShrHldr = false;
		}
		validShrHldr = checkValidUser(getManagedBean().getSelectedShareHolder());
		// if (getManagedBean().getSelectedShareHolder().getAuthorizedFlg() ==
		// 1) {
		// validShrHldr = validateAuthPersonLimit();
		// }
		if (validShrHldr) {
			if (getManagedBean().getSmAppl().getSmPersonnels()
					.contains(getManagedBean().getSelectedShareHolder())) {
				getManagedBean()
						.getSmAppl()
						.getSmPersonnels()
						.set(getManagedBean()
								.getSmAppl()
								.getSmPersonnels()
								.indexOf(
										getManagedBean()
												.getSelectedShareHolder()),
								getManagedBean().getSelectedShareHolder());
				getManagedBean().getSmapplShareHolders().set(
						getManagedBean().getSmapplShareHolders().indexOf(
								getManagedBean().getSelectedShareHolder()),
						getManagedBean().getSelectedShareHolder());
				System.out.println(" **** THIS IS AN UPDATE Personnel ******");
			} else {

				getManagedBean().getSmAppl().getSmPersonnels()
						.add(getManagedBean().getSelectedShareHolder());
				getManagedBean().getSmapplShareHolders().add(
						getManagedBean().getSelectedShareHolder());
			}
			IMofRegistrationSO mofRegistrationSO = null;

			try {
				System.out.println("updating personnel into database");
				mofRegistrationSO = (IMofRegistrationSO) BusinessDelegate
						.getInstance().getServiceObject("sm",
								IMofRegistrationSO.class);

				MofRegistrationReqDTO reqDto = new MofRegistrationReqDTO();

				reqDto.setSmAppl(this.getManagedBean().getSmAppl());

				MofRegistrationResDTO resDTO = mofRegistrationSO
						.updateApplication(reqDto);
				// getManagedBean().setSmAppl(resDTO.getSmAppl());
			} finally {
				mofRegistrationSO = null;
				retriveLatestApplAfterUpdate("");
			}
			if (fromPage.equalsIgnoreCase("captialInfo")) {
				RequestContext.getCurrentInstance().execute(
						"shareHolderDialog.hide()");
			}
			if (fromPage.equalsIgnoreCase("personnelInfo")) {
				calcPersonnelListCount();
				RequestContext.getCurrentInstance().execute(
						"perShareHolderDialog.hide()");

			}

		}

	}

	public void assignSelShareHolder(SmPersonnel shareHolderBean) {
		if (shareHolderBean.getAuthorizedFlg() == 1) {
			getManagedBean().setAuthPerCount(0);
		} else {
			getManagedBean().setAuthPerCount(getAuthUserCount());
		}
		getManagedBean().setSelectedShareHolder(shareHolderBean);
	}

	public void addOrUpdateShareHolderAddress(SmPersonnel shareHolderBean)
			throws ParseException {
		if (shareHolderBean.getSmPersonnelAddresses().isEmpty()) {
			System.out.println("******** Empty list ********"
					+ shareHolderBean.getName() + " *** Id ***"
					+ shareHolderBean.getPersonnelId() + " ** Is Equity ****"
					+ shareHolderBean.isEquityOwner());
			shareHolderBean.getSmPersonnelAddresses().add(
					getManagedBean().getShareHolderResdAddr());
			shareHolderBean.getSmPersonnelAddresses().add(
					getManagedBean().getShareHolderPermAddr());
			shareHolderBean.getSmPersonnelAddresses().add(
					getManagedBean().getShareHolderOffAddr());
			shareHolderBean.getSmPersonnelAddresses().add(
					getManagedBean().getShareHolderMailAddr());
			getManagedBean().setSelectedShareHolder(shareHolderBean);
		} else {
			System.out
					.println("******** Exisiting Share holders address **********"
							+ shareHolderBean.getName());
			for (SmPersonnelAddress addr : shareHolderBean
					.getSmPersonnelAddresses()) {
				if (addr.getAddressType().equalsIgnoreCase(
						SupplierManagementConst.SM_PERSONNEL_ADDR_TYPE_MAIL)) {
					shareHolderBean.getSmPersonnelAddresses().set(
							shareHolderBean.getSmPersonnelAddresses().indexOf(
									addr),
							getManagedBean().getShareHolderMailAddr());
				}
				if (addr.getAddressType().equalsIgnoreCase(
						SupplierManagementConst.SM_PERSONNEL_ADDR_TYPE_OFFICE)) {
					shareHolderBean.getSmPersonnelAddresses().set(
							shareHolderBean.getSmPersonnelAddresses().indexOf(
									addr),
							getManagedBean().getShareHolderOffAddr());
				}
				if (addr.getAddressType().equalsIgnoreCase(
						SupplierManagementConst.SM_PERSONNEL_ADDR_TYPE_RES)) {
					shareHolderBean.getSmPersonnelAddresses().set(
							shareHolderBean.getSmPersonnelAddresses().indexOf(
									addr),
							getManagedBean().getShareHolderResdAddr());
				}
				if (addr.getAddressType().equalsIgnoreCase(
						SupplierManagementConst.SM_PERSONNEL_ADDR_TYPE_PERM)) {
					shareHolderBean.getSmPersonnelAddresses().set(
							shareHolderBean.getSmPersonnelAddresses().indexOf(
									addr),
							getManagedBean().getShareHolderPermAddr());
				}
			}
			getManagedBean().setSelectedShareHolder(shareHolderBean);

		}
		System.out.println("*** After set addr value ***"
				+ getManagedBean().getSelectedShareHolder().getName()
				+ "** Id ***"
				+ getManagedBean().getSelectedShareHolder().getPersonnelId());
		if (shareHolderBean.isEquityOwner()) {
			addOrUpdateShareHolderToSmAppl("addrPage");
		} else {
			addOrUpdatePersonnelToSmAppl();
		}
	}

	public void assignNewShareHolder() {
		System.out.println("********* assignNewShareHolder *****");
		getManagedBean().setAuthPerCount(getAuthUserCount());
		SmPersonnel shr = new SmPersonnel();
		shr.setEquityOwner(true);
		shr.setShareAmt(new BigDecimal(0));
		shr.setSharePercentage(new BigDecimal(0));
		setDefaultPersonnelValues(shr);
		// getManagedBean().setSelectedPersonnel(null);
		getManagedBean().setSelectedShareHolder(shr);

	}

	public void deleteSelectedShareHolders() {

		System.out.println("*** Delete Share Holders ******* "
				+ getManagedBean().getSelectedShareHolders().size());
		if (getManagedBean().getSelectedShareHolders().size() > 0) {
			for (SmPersonnel shr : getManagedBean().getSelectedShareHolders()) {
				getManagedBean().getSmAppl().getSmPersonnels().remove(shr);
				getManagedBean().getSmapplShareHolders().remove(shr);
			}
			getManagedBean().getSelectedShareHolders().clear();
			calcPersonnelListCount();
		} else {
			System.out
					.println(" ******** NO Record selected please select atleast one ");
		}

	}

	// -- Process Share Holder Personnel End
	// -- Process Common for both Personnel and Share Holder Start --
	public void addShrHldrJobFunction(Boolean isShrHldr) {
		System.out.println(">>> Add Personnel Job Function >>>>>>>>>>>>>>>>>>"
				+ isShrHldr);
		SmStaffExperience exp = new SmStaffExperience();
		if (isShrHldr) {
			exp.setSmPersonnel(getManagedBean().getSelectedShareHolder());
			getManagedBean().getSelectedShareHolder().getSmStaffExperiences()
					.add(exp);
		} else {
			exp.setSmPersonnel(getManagedBean().getSelectedPersonnel());
			getManagedBean().getSelectedPersonnel().getSmStaffExperiences()
					.add(exp);
		}
	}

	public void addShrHldrAcademicQualification(Boolean isShrHldr) {
		System.out.println(">>>>>> Add Academic Qualification >>>>>>>>>>>>>>>"
				+ isShrHldr);
		SmStaffAcademic aca = new SmStaffAcademic();
		if (isShrHldr) {
			aca.setSmPersonnel(getManagedBean().getSelectedShareHolder());
			getManagedBean().getSelectedShareHolder().getSmStaffAcademics()
					.add(aca);
		} else {
			aca.setSmPersonnel(getManagedBean().getSelectedPersonnel());
			getManagedBean().getSelectedPersonnel().getSmStaffAcademics()
					.add(aca);
		}
	}

	public void addShrHldrProfBody(Boolean isShrHldr) {
		System.out.println(">>>>>>> Add Professional Body >>>>>>>>>>>>>>"
				+ isShrHldr);
		SmAssociationBody assBody = new SmAssociationBody();
		assBody.setPmCertBody(new PmCertBody());
		assBody.setRevNo(1);
		assBody.setSmAppl(getManagedBean().getSmAppl());
		if (isShrHldr) {
			assBody.setSmPersonnel(getManagedBean().getSelectedShareHolder());
			getManagedBean().getSelectedShareHolder().getSmAssociationBodies()
					.add(assBody);
		} else {
			assBody.setSmPersonnel(getManagedBean().getSelectedPersonnel());
			getManagedBean().getSelectedPersonnel().getSmAssociationBodies()
					.add(assBody);
		}
	}

	public void addShrHldrRelatedCompany(Boolean isShrHldr) {
		System.out.println(">>>>>>>> Add Related Company Body >>>>>>>>>>>>>"
				+ isShrHldr);
		SmRelatedCompany com = new SmRelatedCompany();
		if (isShrHldr) {
			com.setSmPersonnel(getManagedBean().getSelectedShareHolder());
			getManagedBean().getSelectedShareHolder().getSmRelatedCompanies()
					.add(com);
		} else {
			com.setSmPersonnel(getManagedBean().getSelectedPersonnel());
			getManagedBean().getSelectedPersonnel().getSmRelatedCompanies()
					.add(com);
		}
	}

	public void deleteSelectedShrHldrJobFunction(Boolean isShrHldr) {

		if (getManagedBean().getSelectedPersJobFunctions().size() > 0) {
			for (SmStaffExperience exp : getManagedBean()
					.getSelectedPersJobFunctions()) {
				if (isShrHldr) {
					getManagedBean().getSelectedShareHolder()
							.getSmStaffExperiences().remove(exp);
				} else {

					getManagedBean().getSelectedPersonnel()
							.getSmStaffExperiences().remove(exp);
				}

			}
			getManagedBean().getSelectedPersJobFunctions().clear();
		} else {
			System.out
					.println(" ******** NO Record selected please select atleast one ");
		}

	}

	public void deleteSelectedShrHldrAcademics(Boolean isShrHldr) {
		System.out.println("****** Delete Academics ******" + isShrHldr);
		if (getManagedBean().getSelectedPersAcademics().size() > 0) {
			for (SmStaffAcademic aca : getManagedBean()
					.getSelectedPersAcademics()) {
				if (isShrHldr) {
					getManagedBean().getSelectedShareHolder()
							.getSmStaffAcademics().remove(aca);
				} else {
					getManagedBean().getSelectedPersonnel()
							.getSmStaffAcademics().remove(aca);
				}
			}
			getManagedBean().getSelectedPersAcademics().clear();
		} else {
			System.out
					.println(" ******** NO Record selected please select atleast one ");
		}

	}

	public void deleteSelectedShrHldrProfBodies(Boolean isShrHldr) {
		System.out.println("****** Delete Prof Bodies ******" + isShrHldr);
		if (getManagedBean().getSelectedPersProfBodies().size() > 0) {
			for (SmAssociationBody prof : getManagedBean()
					.getSelectedPersProfBodies()) {
				if (isShrHldr) {
					getManagedBean().getSelectedShareHolder()
							.getSmAssociationBodies().remove(prof);
				} else {
					getManagedBean().getSelectedPersonnel()
							.getSmAssociationBodies().remove(prof);
				}
			}
			getManagedBean().getSelectedPersProfBodies().clear();
		} else {
			System.out
					.println(" ******** NO Record selected please select atleast one ");
		}

	}

	public void deleteSelectedShrHldrRelatedCompanies(Boolean isShrHldr) {
		System.out
				.println("****** Delete Related Companies ******" + isShrHldr);
		if (getManagedBean().getSelectedPersRelatedCompanies().size() > 0) {
			for (SmRelatedCompany rel : getManagedBean()
					.getSelectedPersRelatedCompanies()) {
				if (isShrHldr) {
					getManagedBean().getSelectedShareHolder()
							.getSmRelatedCompanies().remove(rel);
				} else {
					getManagedBean().getSelectedPersonnel()
							.getSmRelatedCompanies().remove(rel);
				}
			}
			getManagedBean().getSelectedShrHldrRelatedCompanies().clear();
		} else {
			System.out
					.println(" ******** NO Record selected please select atleast one ");
		}

	}

	public void assignSelShareHolderAddress(SmPersonnel shareHolderBean) {
		if (shareHolderBean.getSmPersonnelAddresses().isEmpty()) {
			System.out.println("******** New Address fields ********"
					+ shareHolderBean.getName());
			SmPersonnelAddress perm = new SmPersonnelAddress();
			perm.setAddressType(SupplierManagementConst.SM_PERSONNEL_ADDR_TYPE_PERM);
			perm.setSmAddress(setDefaultAddr());
			perm.setSmPersonnel(shareHolderBean);

			// TODO remove if necessary
			List<PmState> defStates = new ArrayList<PmState>();
			defStates = retrievePmStateList(perm.getSmAddress().getPmCountry()
					.getCountryId());

			SmPersonnelAddress mail = new SmPersonnelAddress();
			mail.setAddressType(SupplierManagementConst.SM_PERSONNEL_ADDR_TYPE_MAIL);
			mail.setSmAddress(setDefaultAddr());
			mail.setSmPersonnel(shareHolderBean);

			SmPersonnelAddress off = new SmPersonnelAddress();
			off.setAddressType(SupplierManagementConst.SM_PERSONNEL_ADDR_TYPE_OFFICE);
			off.setSmAddress(setDefaultAddr());
			off.setSmPersonnel(shareHolderBean);

			SmPersonnelAddress res = new SmPersonnelAddress();
			res.setAddressType(SupplierManagementConst.SM_PERSONNEL_ADDR_TYPE_RES);
			res.setSmAddress(setDefaultAddr());
			res.setSmPersonnel(shareHolderBean);

			getManagedBean().setShareHolderPermAddr(perm);
			getManagedBean().setShareHolderMailAddr(mail);
			getManagedBean().setShareHolderOffAddr(off);
			getManagedBean().setShareHolderResdAddr(res);
			getManagedBean().setShrHldrPermStates(defStates);
			getManagedBean().setShrHldrMailStates(defStates);
			getManagedBean().setShrHldrResdStates(defStates);
			getManagedBean().setShrHldrOffStates(defStates);

			// getManagedBean().setPersonnelPermAddr(perm);
			// getManagedBean().setPersonnelMailAddr(mail);
			// getManagedBean().setPersonnelOffAddr(off);
			// getManagedBean().setPersonnelResdAddr(res);
			// getManagedBean().setPersnlPermStates(defStates);
			// getManagedBean().setPersnlMailStates(defStates);
			// getManagedBean().setPersnlResdStates(defStates);
			// getManagedBean().setPersnlOffStates(defStates);

		} else {
			System.out.println("******* Already contains address ***********");
			for (SmPersonnelAddress addr : shareHolderBean
					.getSmPersonnelAddresses()) {
				if (addr.getAddressType().equalsIgnoreCase(
						SupplierManagementConst.SM_PERSONNEL_ADDR_TYPE_PERM)) {
					getManagedBean().setShareHolderPermAddr(addr);
					// if (isShrHldr) {
					//
					// } else {
					// getManagedBean().setPersonnelPermAddr(addr);
					// }
				}
				if (addr.getAddressType().equalsIgnoreCase(
						SupplierManagementConst.SM_PERSONNEL_ADDR_TYPE_MAIL)) {
					getManagedBean().setShareHolderMailAddr(addr);
					// if (isShrHldr) {
					//
					// } else {
					// getManagedBean().setPersonnelMailAddr(addr);
					// }
				}
				if (addr.getAddressType().equalsIgnoreCase(
						SupplierManagementConst.SM_PERSONNEL_ADDR_TYPE_RES)) {
					getManagedBean().setShareHolderResdAddr(addr);
					// if (isShrHldr) {
					//
					// } else {
					// getManagedBean().setPersonnelResdAddr(addr);
					// }
				}
				if (addr.getAddressType().equalsIgnoreCase(
						SupplierManagementConst.SM_PERSONNEL_ADDR_TYPE_OFFICE)) {
					getManagedBean().setShareHolderOffAddr(addr);
					// if (isShrHldr) {
					//
					// } else {
					// getManagedBean().setPersonnelOffAddr(addr);
					// }
				}
			}
		}
		if (shareHolderBean.isEquityOwner()) {
			getManagedBean().setSelectedShareHolder(shareHolderBean);
		} else {
			getManagedBean().setSelectedPersonnel(shareHolderBean);
		}

	}

	// -- Process Common for both Personnel and Share Holder Start --
	// -- Process Personnel Start

	public void addOrUpdatePersonnelToSmAppl() throws ParseException {
		System.out.println("*** The personnel Info ****"
				+ getManagedBean().getSelectedPersonnel().getName());
		boolean validPer = true;
		// if (getManagedBean().getSelectedPersonnel().getAuthorizedFlg() == 1)
		// {
		// validPer = validateAuthPersonLimit();
		// }
		validPer = checkValidUser(getManagedBean().getSelectedPersonnel());
		if (validPer) {
			if (getManagedBean().getSmAppl().getSmPersonnels()
					.contains(getManagedBean().getSelectedPersonnel())) {
				getManagedBean()
						.getSmAppl()
						.getSmPersonnels()
						.set(getManagedBean()
								.getSmAppl()
								.getSmPersonnels()
								.indexOf(
										getManagedBean().getSelectedPersonnel()),
								getManagedBean().getSelectedPersonnel());

				System.out.println(" **** THIS IS AN UPDATE Personnel ******");
			} else {

				getManagedBean().getSmAppl().getSmPersonnels()
						.add(getManagedBean().getSelectedPersonnel());
			}

			IMofRegistrationSO mofRegistrationSO = null;

			try {
				System.out.println("updating personnel into database");
				mofRegistrationSO = (IMofRegistrationSO) BusinessDelegate
						.getInstance().getServiceObject("sm",
								IMofRegistrationSO.class);

				MofRegistrationReqDTO reqDto = new MofRegistrationReqDTO();

				reqDto.setSmAppl(this.getManagedBean().getSmAppl());

				MofRegistrationResDTO resDTO = mofRegistrationSO
						.updateApplication(reqDto);
				// getManagedBean().setSmAppl(resDTO.getSmAppl());
			} finally {
				mofRegistrationSO = null;
				retriveLatestApplAfterUpdate("");
			}
			// updateSmAppl(getManagedBean().getSmAppl().getApplType(),"Def");
			calcPersonnelListCount();
			RequestContext.getCurrentInstance()
					.execute("directorDialog.hide()");
		}
	}

	public void assignSelPersonnel(SmPersonnel shareHolderBean) {
		if (shareHolderBean.getAuthorizedFlg() == 1) {
			getManagedBean().setAuthPerCount(0);
		} else {
			getManagedBean().setAuthPerCount(getAuthUserCount());
		}
		getManagedBean().setSelectedPersonnel(shareHolderBean);
	}

	public void assignNewPersonnel(String personelType) {
		System.out.println("******* ADD NEW Personnel *************"
				+ personelType);
		getManagedBean().setAuthPerCount(getAuthUserCount());

		SmPersonnel personnel = new SmPersonnel();
		personnel.setEquityOwner(false);
		if (personelType
				.equalsIgnoreCase(SupplierManagementConst.PERSONNEL_TYPE_DIRECTOR)) {
			personnel.setDirOrHghrMngmt(true);
		} else {
			personnel.setDirOrHghrMngmt(false);
		}
		setDefaultPersonnelValues(personnel);
		System.out.println("*** Bumi Flage **** " + personnel.getBumiFlg());
		getManagedBean().setSelectedPersonnel(personnel);

	}

	public void addOrUpdatePeronnelAddress(SmPersonnel persBean) {/*
																 * if (persBean.
																 * getSmPersonnelAddresses
																 * ().isEmpty())
																 * { System.out.
																 * println(
																 * "******** Empty list ********"
																 * +
																 * persBean.getName
																 * ());
																 * persBean.
																 * getSmPersonnelAddresses
																 * ().add(
																 * getManagedBean
																 * ().
																 * getPersonnelResdAddr
																 * ());
																 * persBean.
																 * getSmPersonnelAddresses
																 * ().add(
																 * getManagedBean
																 * ().
																 * getPersonnelPermAddr
																 * ());
																 * persBean.
																 * getSmPersonnelAddresses
																 * ().add(
																 * getManagedBean
																 * ().
																 * getPersonnelOffAddr
																 * ());
																 * persBean.
																 * getSmPersonnelAddresses
																 * ().add(
																 * getManagedBean
																 * ().
																 * getPersonnelMailAddr
																 * ());
																 * getManagedBean
																 * ().
																 * setSelectedPersonnel
																 * (persBean); }
																 * else {
																 * System.out
																 * .println(
																 * "******** Exisiting Share holders address **********"
																 * +
																 * persBean.getName
																 * ()); for (
																 * SmPersonnelAddress
																 * addr :
																 * persBean.
																 * getSmPersonnelAddresses
																 * ()) { if
																 * (addr
																 * .getAddressType
																 * ().
																 * equalsIgnoreCase
																 * (
																 * SupplierManagementConst
																 * .
																 * SM_PERSONNEL_ADDR_TYPE_MAIL
																 * )) {
																 * persBean.
																 * getSmPersonnelAddresses
																 * ().set(
																 * persBean.
																 * getSmPersonnelAddresses
																 * (
																 * ).indexOf(addr
																 * ),
																 * getManagedBean
																 * ().
																 * getPersonnelMailAddr
																 * ()); } if
																 * (addr
																 * .getAddressType
																 * ().
																 * equalsIgnoreCase
																 * (
																 * SupplierManagementConst
																 * .
																 * SM_PERSONNEL_ADDR_TYPE_OFFICE
																 * )) {
																 * persBean.
																 * getSmPersonnelAddresses
																 * ().set(
																 * persBean.
																 * getSmPersonnelAddresses
																 * (
																 * ).indexOf(addr
																 * ),
																 * getManagedBean
																 * ().
																 * getPersonnelOffAddr
																 * ()); } if
																 * (addr
																 * .getAddressType
																 * ().
																 * equalsIgnoreCase
																 * (
																 * SupplierManagementConst
																 * .
																 * SM_PERSONNEL_ADDR_TYPE_RES
																 * )) {
																 * persBean.
																 * getSmPersonnelAddresses
																 * ().set(
																 * persBean.
																 * getSmPersonnelAddresses
																 * (
																 * ).indexOf(addr
																 * ),
																 * getManagedBean
																 * ().
																 * getPersonnelResdAddr
																 * ()); } if
																 * (addr
																 * .getAddressType
																 * ().
																 * equalsIgnoreCase
																 * (
																 * SupplierManagementConst
																 * .
																 * SM_PERSONNEL_ADDR_TYPE_PERM
																 * )) {
																 * persBean.
																 * getSmPersonnelAddresses
																 * ().set(
																 * persBean.
																 * getSmPersonnelAddresses
																 * (
																 * ).indexOf(addr
																 * ),
																 * getManagedBean
																 * ().
																 * getPersonnelPermAddr
																 * ()); } }
																 * getManagedBean
																 * ().
																 * setSelectedPersonnel
																 * (persBean);
																 * System
																 * .out.println(
																 * "*** After set addr value ***"
																 * +
																 * getManagedBean
																 * ().
																 * getSelectedPersonnel
																 * (
																 * ).getName());
																 * } if
																 * (getManagedBean
																 * (
																 * ).getSmAppl()
																 * .
																 * getSmPersonnels
																 * () .contains(
																 * getManagedBean
																 * ().
																 * getSelectedPersonnel
																 * ())) {
																 * 
																 * System.out
																 * .println(
																 * " **** THIS IS AN UPDATE Personnel Address******"
																 * );
																 * getManagedBean
																 * ()
																 * .getSmAppl()
																 * .
																 * getSmPersonnels
																 * () .set(
																 * getManagedBean
																 * (
																 * ).getSmAppl()
																 * .
																 * getSmPersonnels
																 * () .indexOf(
																 * getManagedBean
																 * ().
																 * getSelectedPersonnel
																 * ()),
																 * getManagedBean
																 * ().
																 * getSelectedPersonnel
																 * ()); } else {
																 * 
																 * getManagedBean(
																 * )
																 * .getSmAppl().
																 * getSmPersonnels
																 * () .add(
																 * getManagedBean
																 * ().
																 * getSelectedPersonnel
																 * ()); }
																 */
	}

	public void deleteSelectedPersonnel() {

		System.out.println("*** Delete Personnels ******* "
				+ getManagedBean().getSelectedPersonnels().size());
		if (getManagedBean().getSelectedPersonnels().size() > 0) {
			for (SmPersonnel per : getManagedBean().getSelectedPersonnels()) {
				getManagedBean().getSmAppl().getSmPersonnels().remove(per);
			}
			getManagedBean().getSelectedPersonnels().clear();
			calcPersonnelListCount();
		} else {
			System.out
					.println(" ******** NO Record selected please select atleast one ");
		}

	}

	// -- Process Personnel End

	// Process Bank Info Start
	public void addOrUpdateBankInfoToSmAppl() {
		// TODO to be changed later
		getManagedBean().getSelectedBank().getSmChequeSignatories().clear();
		getManagedBean().setSelectedSignatoryBankAcct(new SmSupplierBank());
		for (Long perId : getManagedBean().getChequeSingPerId()) {
			for (SmPersonnel sm : getManagedBean().getSmAppl()
					.getSmPersonnels()) {
				if (sm.getPersonnelId().equals(perId)) {
					SmChequeSignatory ch = new SmChequeSignatory();
					ch.setSmPersonnel(sm);
					ch.setSmSupplierBank(getManagedBean().getSelectedBank());
					getManagedBean().getSelectedBank().getSmChequeSignatories()
							.add(ch);
				}
			}
		}
		for (PmFinancialOrg org : getManagedBean().getFinOrgLs()) {
			if (getManagedBean().getSelectedBank().getPmFinancialOrg()
					.getFinancialOrgId().equals(org.getFinancialOrgId())) {
				getManagedBean().getSelectedBank().setPmFinancialOrg(org);

			}
		}
		System.out.println("******* Selected value Branch Before******"
				+ getManagedBean().getSelectedBank().getSmSupplierBranch()
						.getSupplierBranchId());
		if (getManagedBean().getSelectedBank().getSmSupplierBranch()
				.getSupplierBranchId() == null
				|| getManagedBean().getSelectedBank().getSmSupplierBranch()
						.getSupplierBranchId().equals("")) {
			System.out.println("******* Null value Branch ******");
			getManagedBean().getSelectedBank().setSmSupplierBranch(
					new SmSupplierBranch());
		} else {
			System.out.println("******* Selected value Branch ******"
					+ getManagedBean().getSelectedBank().getSmSupplierBranch()
							.getSupplierBranchId());
			for (SmSupplierBranch brn : getManagedBean().getSmAppl()
					.getSmSupplierBranches()) {
				if (brn.getSupplierBranchId().equals(
						getManagedBean().getSelectedBank()
								.getSmSupplierBranch().getSupplierBranchId())) {
					getManagedBean().getSelectedBank().setSmSupplierBranch(brn);
				}
			}
		}

		if (getManagedBean().getSmAppl().getSmSupplierBanks()
				.contains(getManagedBean().getSelectedBank())) {

			System.out.println(" **** THIS IS AN UPDATE Bank******");
			System.out.println("Before Update ===== "
					+ getManagedBean().getSelectedBank()
							.getSmChequeSignatories().size());
		} else {
			System.out.println("Before add ===== "
					+ getManagedBean().getSelectedBank()
							.getSmChequeSignatories().size());
			getManagedBean().getSmAppl().getSmSupplierBanks()
					.add(getManagedBean().getSelectedBank());
		}
	}

	public void assignSelBank(SmSupplierBank shareBankBean) {
		getManagedBean().setChequeSingPerId(null);
		System.out.println("******* SIZE ********** "
				+ shareBankBean.getSmChequeSignatories().size());
		Long[] chequSigns = new Long[shareBankBean.getSmChequeSignatories()
				.size()];
		for (int i = 0; i < chequSigns.length; i++) {
			chequSigns[i] = shareBankBean.getSmChequeSignatories().get(i)
					.getSmPersonnel().getPersonnelId();

		}
		getManagedBean().setChequeSingPerId(chequSigns);
		// shareBankBean.getSmChequeSignatories().clear();
		getManagedBean().setSelectedBank(shareBankBean);
	}

	public void viewSignatories(SmSupplierBank signAcctBean) {
		getManagedBean().setSelectedSignatoryBankAcct(signAcctBean);
	}

	public void assignNewBank() {
		System.out.println("******* ADD NEW Bank Info *************");
		getManagedBean().setChequeSingPerId(null);
		SmSupplierBank bank = new SmSupplierBank();
		bank.setSmAddress(setDefaultAddr());
		bank.setSmSupplierBranch(new SmSupplierBranch());
		bank.setSmAppl(getManagedBean().getSmAppl());
		bank.setPmFinancialOrg(new PmFinancialOrg());
		// bank.setPmbank(new PmBank());
		getManagedBean().setBankStates(
				retrievePmStateList(getManagedBean().getSelectedBank()
						.getSmAddress().getPmCountry().getCountryId()));
		bank.setSmChequeSignatories(new ArrayList<SmChequeSignatory>());
		getManagedBean().setSelectedBank(bank);
	}

	// -- Process Bank Info End
	/**
	 * Test BPM Connect
	 */
	public boolean initialBpmTask() {
		IMofRegistrationSO regSo = null;
		try {
			regSo = (IMofRegistrationSO) BusinessDelegate.getInstance()
					.getServiceObject("sm", IMofRegistrationSO.class);
			return regSo.initialBpmTask();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			regSo = null;
		}

	}

	/**
	 * Make a call to the BPM for MofRegistration
	 */
	public void initiateSmApplicationTask() {
		IMofRegistrationSO regSo = null;
		try {
			// TODO to be used while doing the iteration2
			// SmAppl bpmAppl = getManagedBean().getSmSupplier().getSmAppls()
			// .get(0);
			SmAppl bpmAppl = getManagedBean().getSmAppl();
			updateSmAppl(bpmAppl.getApplType(), "BasicInfo");
			System.out.println(" **** SUPPORTING DOC MODE ****"
					+ bpmAppl.getSupportingDocMode());
			if (bpmAppl.getApplType().equalsIgnoreCase("N")) {
				System.out.println(" **** BPM FROM MOF REG CALLED ******");
			} else if (bpmAppl.getApplType().equalsIgnoreCase("B")) {
				System.out.println(" **** BPM FROM BUMI APPL CALLED ******");
			} else if (bpmAppl.getApplType().equalsIgnoreCase("U")) {
				System.out.println(" **** BPM FROM UPDATE APPL CALLED ******");
			} else if (bpmAppl.getApplType().equalsIgnoreCase("A")) {
				System.out
						.println(" **** BPM FROM ADDITIONAL CATEGORY CODE APPL CALLED ******");
			} else if (bpmAppl.getApplType().equalsIgnoreCase("R")) {
				System.out
						.println(" **** BPM FROM RENEWAL APPLICATION IS CALLED ******");
			} else if (bpmAppl.getApplType().equalsIgnoreCase("C")) {
				System.out
						.println(" **** BPM FROM CANCEL REG APPLICATION IS CALLED ******");
			} else {
				bpmAppl.setApplId((long) 777);
			}
			if (bpmAppl.getSupportingDocMode() == null) {
				bpmAppl.setSupportingDocMode("O");
			}
			System.out.println(" ***** VALUE OF SELECTED OBJECT ***** "
					+ getManagedBean().getSmSupplier().getSmAppls().size()
					+ "  ---  " + bpmAppl.getApplId());
			MofRegistrationReqDTO reqDTO = new MofRegistrationReqDTO();

			// TODO Remove the hard code later
			reqDTO.setOutcome(SupplierManagementConst.SM_APPL_BPM_OUTCOME_SUBMIT);
			reqDTO.setApplId(this.getManagedBean().getSmAppl().getApplId());
			reqDTO.setSupplierId(bpmAppl.getSmSupplier().getSupplierId());
			reqDTO.setAppType(bpmAppl.getApplType());
			reqDTO.setTaskId(this.getManagedBean().getTaskId());
			reqDTO.setSmAppl(bpmAppl);
			// reqDTO.setUserName(new FacesScopes().getUserScreenName());
			reqDTO.setToken(new FacesScopes().getBPMAuthenticationCtxToken());
			reqDTO.setSupplierType(this.getManagedBean().getSmAppl()
					.getSupplierType().toUpperCase());
			reqDTO.setBaseOfficeState(getManagedBean().getSmAppl()
					.getSmCompanyBasic().getSmCompanyAddresses().get(0)
					.getSmAddress().getPmState().getStateName().toUpperCase());

			if (this.getManagedBean().getSmAppl().getSupportingDocMode()
					.equalsIgnoreCase("O")) {
				reqDTO.setOnlineDoc(true);
			} else {
				reqDTO.setOnlineDoc(false);
			}

			MofRegistrationResDTO resDTO;
			regSo = (IMofRegistrationSO) BusinessDelegate.getInstance()
					.getServiceObject("sm", IMofRegistrationSO.class);
			resDTO = regSo.initiateSmApplicationTask(reqDTO);
		} finally {
			regSo = null;
		}

	}

	public String submitBumiApplToBpm() {
		try {
			initiateSmApplicationTask();
			FacesContext
					.getCurrentInstance()
					.addMessage(
							null,
							new FacesMessage(
									FacesMessage.SEVERITY_INFO,
									null,
									MessageUtils
											.getMessage(
													MessageConst.INFO_700005,
													new String[] {
															getParameterDesc(
																	ParameterTypeConst.SUPPLIER_APPLICATION_TYPES,
																	ParameterConst.NEW_SM_APPLN_FOR_BUMI),
															getManagedBean()
																	.getSmAppl()
																	.getApplNo() },
													MessageUtils.BUNDLE_COMMON_MESSAGES,
													new FacesScopes()
															.getUserLocale())));

			return CommonUIConst.ACKNOWLEDGE_PAGE;
		} catch (Exception e) {
			return SupplierManagementConst.BUMI_APPL_FAIL;
		}
	}

	public String submitUpdateMofApplToBpm() {
		try {
			initiateSmApplicationTask();
			FacesContext
					.getCurrentInstance()
					.addMessage(
							null,
							new FacesMessage(
									FacesMessage.SEVERITY_INFO,
									null,
									MessageUtils
											.getMessage(
													MessageConst.INFO_700005,
													new String[] {
															getParameterDesc(
																	ParameterTypeConst.SUPPLIER_APPLICATION_TYPES,
																	ParameterConst.NEW_SM_APPLN_FOR_UPDATE),
															getManagedBean()
																	.getSmAppl()
																	.getApplNo() },
													MessageUtils.BUNDLE_COMMON_MESSAGES,
													new FacesScopes()
															.getUserLocale())));

			return CommonUIConst.ACKNOWLEDGE_PAGE;
		} catch (Exception e) {
			return SupplierManagementConst.UPDATE_MOF_APPL_FAIL;
		}
	}

	public String submitRenewMofApplToBpm() {
		try {
			initiateSmApplicationTask();
			FacesContext
					.getCurrentInstance()
					.addMessage(
							null,
							new FacesMessage(
									FacesMessage.SEVERITY_INFO,
									null,
									MessageUtils
											.getMessage(
													MessageConst.INFO_700005,
													new String[] {
															getParameterDesc(
																	ParameterTypeConst.SUPPLIER_APPLICATION_TYPES,
																	ParameterConst.NEW_SM_APPLN_FOR_RENEW),
															getManagedBean()
																	.getSmAppl()
																	.getApplNo() },
													MessageUtils.BUNDLE_COMMON_MESSAGES,
													new FacesScopes()
															.getUserLocale())));

			return CommonUIConst.ACKNOWLEDGE_PAGE;
		} catch (Exception e) {
			return SupplierManagementConst.RENEW_MOF_APPL_FAIL;
		}
	}

	public String submitAddnlCategoryApplToBpm() {
		try {
			initiateSmApplicationTask();
			FacesContext
					.getCurrentInstance()
					.addMessage(
							null,
							new FacesMessage(
									FacesMessage.SEVERITY_INFO,
									null,
									MessageUtils
											.getMessage(
													MessageConst.INFO_700005,
													new String[] {
															getParameterDesc(
																	ParameterTypeConst.SUPPLIER_APPLICATION_TYPES,
																	ParameterConst.NEW_SM_APPLN_FOR_APPLCATCODES),
															getManagedBean()
																	.getSmAppl()
																	.getApplNo() },
													MessageUtils.BUNDLE_COMMON_MESSAGES,
													new FacesScopes()
															.getUserLocale())));

			return CommonUIConst.ACKNOWLEDGE_PAGE;
		} catch (Exception e) {
			return SupplierManagementConst.ADDNLCAT_APPL_FAIL;
		}
	}

	public String submitMofApplToBpm() {
		try {
			initiateSmApplicationTask();
			FacesContext
					.getCurrentInstance()
					.addMessage(
							null,
							new FacesMessage(
									FacesMessage.SEVERITY_INFO,
									null,
									MessageUtils
											.getMessage(
													MessageConst.INFO_700005,
													new String[] {
															getParameterDesc(
																	ParameterTypeConst.SUPPLIER_APPLICATION_TYPES,
																	ParameterConst.NEW_SM_APPLN_FOR_MOF),
															getManagedBean()
																	.getSmAppl()
																	.getApplNo() },
													MessageUtils.BUNDLE_COMMON_MESSAGES,
													new FacesScopes()
															.getUserLocale())));

			return CommonUIConst.ACKNOWLEDGE_PAGE;
		} catch (Exception e) {
			return SupplierManagementConst.MOF_APPL_FAIL;
		}
	}

	public String submitCancelMofApplToBpm() {
		System.out.println(" Call Cancel ---------------->>>>>>>>>>");
		try {
			initiateSmApplicationTask();
			return SupplierManagementConst.CANCEL_MOF_APPL_SUCCESS;
		} catch (Exception e) {
			return SupplierManagementConst.PROCEED_CANCEL_MOF_APPLN;
		}
	}

	public String submitDeletePendingMofApplToBpm() {
		IMofRegistrationSO regSo = null;
		try {

			SmAppl bpmAppl = getManagedBean().getSmAppl();
			System.out.println("*** DELTE PENDING MOF STATUS === >>> "
			// + bpmAppl.getPmStatus().getStatusId()
					+ " -- CURRENT APPL TYPE --- " + bpmAppl.getApplType());
			// if (bpmAppl.getPmStatus().getStatusId() == 1) {
			getManagedBean().setDelPendAckMsg(" is delete successfully.");
			// TODO directly delete the pending record from the DB
			// } else {
			// TODO remove hard code id later
			bpmAppl.setApplId(7898l);
			MofRegistrationReqDTO reqDTO = new MofRegistrationReqDTO();
			reqDTO.setApplId(this.getManagedBean().getSmAppl().getApplId());
			// TODO remove hard code later
			reqDTO.setOutcome("SUBMIT");
			reqDTO.setSupplierId(this.getManagedBean().getSmAppl()
					.getSmSupplier().getSupplierId());
			reqDTO.setAppType(this.getManagedBean().getSmAppl().getApplType());
			reqDTO.setTaskId(this.getManagedBean().getTaskId());
			// reqDTO.setUserName(new FacesScopes().getUserScreenName());
			reqDTO.setToken(new FacesScopes().getBPMAuthenticationCtxToken());
			if (this.getManagedBean().getSmAppl().getSupportingDocMode() == "O") {
				reqDTO.setOnlineDoc(true);
			} else {
				reqDTO.setOnlineDoc(false);
			}

			MofRegistrationResDTO resDTO;
			regSo = (IMofRegistrationSO) BusinessDelegate.getInstance()
					.getServiceObject("sm", IMofRegistrationSO.class);
			resDTO = regSo.initiateSmDelPendingApplicationTask(reqDTO);
			getManagedBean()
					.setDelPendAckMsg(
							" is submitted successfully. Please wait for further notification.");
			// }

			return SupplierManagementConst.DELETE_PENDING_MOF_APPL_SUCCESS;
		} catch (Exception e) {
			return SupplierManagementConst.PROCEED_DEL_PENDING_MOF_APPLN;
		} finally {
			regSo = null;
		}
	}

	public String submitPOorApproverReviewToBPM(String res) {
		IReviewApplicationSO reqSO = null;
		try {
			log.info("----------------------Submit PO / Approver Decision For BPM-------------------------"
					+ res);
			ReviewApplicationReqDTO reqDTO = new ReviewApplicationReqDTO();
			reqSO = (IReviewApplicationSO) BusinessDelegate.getInstance()
					.getServiceObject("sm", IReviewApplicationSO.class);
			reqDTO.setApplId(this.getManagedBean().getSmAppl().getApplId());
			reqDTO.setApplNo(this.getManagedBean().getSmAppl().getApplNo());
			reqDTO.setOutcome(res);
			reqDTO.setSuppId(this.getManagedBean().getSmAppl().getSmSupplier()
					.getSupplierId());
			reqDTO.setAppType(this.getManagedBean().getSmAppl().getApplType());
			reqDTO.setTaskId(this.getManagedBean().getTaskId());
			// reqDTO.setUserName(new FacesScopes().getUserScreenName());
			reqDTO.setToken(new FacesScopes().getBPMAuthenticationCtxToken());

			if (this.getManagedBean().getSmAppl().getSupportingDocMode()
					.equalsIgnoreCase("O")) {
				reqDTO.setOnlineDoc(true);
			} else {
				reqDTO.setOnlineDoc(false);
			}

			boolean validSiteVisit = true;

			if (res.equalsIgnoreCase("SITE_VISIT")
					|| res.equalsIgnoreCase("SITE_VISIT_AFTER_APPROVE")) {
				validSiteVisit = siteVisitDataUpdate();
			}
			if (res.equalsIgnoreCase("Reject")) {
				setSmApplRejectionReasons();
			}
			System.out.println("Before final decision ---"
					+ getManagedBean().getSmAppl().getPmStatus().getStatusId());
			updateSmAppl("Basic", getManagedBean().getSmAppl().getApplType());

			if (validSiteVisit) {
				ReviewApplicationResDTO resDTO = reqSO
						.submitPOorApproverReviewToBPM(reqDTO);
				String msgConst = "";
				// if (getManagedBean().isApproverUser()) {
				// System.out
				// .println(" **** Final Decision from Approver ****");
				// if (res.equalsIgnoreCase("Approve")) {
				// msgConst = MessageConst.INFO_700007;
				// } else if (res.equalsIgnoreCase("Reject")) {
				// SmEmailNotificationHelper
				// .sendApplicaitonRejectNotifMail(getManagedBean()
				// .getSmAppl());
				// msgConst = MessageConst.INFO_700008;
				// }
				// return SupplierManagementConst.APPROVER_DECISION_ACK;
				// }
				// else
				if (getManagedBean().isPoUser()
						|| getManagedBean().isApproverUser()) {
					System.out.println(" **** Final Decision from PO ****");
					if (res.equalsIgnoreCase("Approve")) {
						msgConst = MessageConst.INFO_700007;
					} else if (res.equalsIgnoreCase("Reject")) {
						SmEmailNotificationHelper
								.sendApplicaitonRejectNotifMail(getManagedBean()
										.getSmAppl());
						msgConst = MessageConst.INFO_700008;
					} else if (res.equalsIgnoreCase("Escalate")) {
						msgConst = MessageConst.INFO_700014;
					} else if (res.equalsIgnoreCase("Inquiry")) {
						msgConst = MessageConst.INFO_700016;
					} else if (res.equalsIgnoreCase("SITE_VISIT")) {
						msgConst = MessageConst.INFO_700015;
					} else if (res.equalsIgnoreCase("SITE_VISIT_AFTER_APPROVE")) {
						msgConst = MessageConst.INFO_700017;
					}

					FacesContext
							.getCurrentInstance()
							.addMessage(
									null,
									new FacesMessage(
											FacesMessage.SEVERITY_INFO,
											null,
											MessageUtils
													.getMessage(
															msgConst,
															new String[] {
																	getParameterDesc(
																			ParameterTypeConst.SUPPLIER_APPLICATION_TYPES,
																			setParameterConst(getManagedBean()
																					.getSmAppl()
																					.getApplType())),
																	getManagedBean()
																			.getSmAppl()
																			.getApplNo() },
															MessageUtils.BUNDLE_COMMON_MESSAGES,
															new FacesScopes()
																	.getUserLocale())));
					return CommonUIConst.ACKNOWLEDGE_PAGE;

				} else {
					FacesContext
							.getCurrentInstance()
							.addMessage(
									null,
									new FacesMessage(
											FacesMessage.SEVERITY_ERROR,
											"Sample error message",
											"Un Authorized user to perform this action"));
					return null;
				}
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext
					.getCurrentInstance()
					.addMessage(
							null,
							new FacesMessage(
									FacesMessage.SEVERITY_ERROR,
									"Sample error message",
									"There is problem in submitting the decision.Please contact the administrator or retry later"));
			return null;
		} finally {
			reqSO = null;
		}

	}

	public void changeVerifyAll(Boolean isVerifiedAll) {
		System.out.println(" **** Enter to  verified all ***** "
				+ isVerifiedAll);
		SmApplSectionReview sec = new SmApplSectionReview();

		if (getManagedBean().isPoUser()) {
			System.out.println(" *** PO VERIFIED ALL *******");
			sec = getManagedBean().getPoSecReview();
			getManagedBean().setPoSecReview(
					setVerificationValues(sec, isVerifiedAll));
		} else if (getManagedBean().isApproverUser()) {
			System.out.println(" *** APPROVER VERIFIED ALL *******");
			sec = getManagedBean().getApprvrSecReview();
			getManagedBean().setApprvrSecReview(
					setVerificationValues(sec, isVerifiedAll));
		}

	}

	public void changeVerifySection(Boolean isSectionVerified) {
		System.out.println(" **** Enter to  verified Section ***** "
				+ isSectionVerified);
		// SmApplSectionReview sec = new SmApplSectionReview();

		if (getManagedBean().isPoUser()) {
			System.out.println(" *** PO VERIFIED Section *******");
			getManagedBean().getPoSecReview();
			System.out.println(" *** After change ***"
					+ getManagedBean().getPoSecReview().isBasicVerified());
			getManagedBean().setPoSecReview(
					assignVerificationCount(getManagedBean().getPoSecReview(),
							isSectionVerified));
		} else if (getManagedBean().isApproverUser()) {
			System.out.println(" *** APPROVER VERIFIED Section *******");
			getManagedBean().getApprvrSecReview();
			getManagedBean().setApprvrSecReview(
					assignVerificationCount(getManagedBean()
							.getApprvrSecReview(), isSectionVerified));
		}
	}

	private SmApplSectionReview assignVerificationCount(
			SmApplSectionReview sec, Boolean isSectionVerified) {
		System.out.println("**** Intial Check Count ==== "
				+ sec.getCheckCount());
		if (isSectionVerified) {
			sec.setCheckCount(sec.getCheckCount() + 1);

		} else {
			sec.setCheckCount(sec.getCheckCount() - 1);

		}
		System.out.println("**** Count After checking **** "
				+ sec.getCheckCount());
		if (sec.getCheckCount() >= getManagedBean().getMaxReviewCount()) {
			sec.setVerifiedAll(Boolean.TRUE);
		} else {
			sec.setVerifiedAll(Boolean.FALSE);
		}

		return sec;
	}

	private SmApplSectionReview setVerificationValues(SmApplSectionReview sec,
			Boolean isVerifiedAll) {

		if (isVerifiedAll) {
			sec.setCheckCount(getManagedBean().getMaxReviewCount());

		} else {
			sec.setCheckCount(0);

		}
		sec.setBasicVerified(isVerifiedAll);
		sec.setOfficeFacilityVerfied(isVerifiedAll);
		sec.setStaffVerified(isVerifiedAll);

		sec.setTrnsNetworkVerified(isVerifiedAll);
		sec.setBranchVerified(isVerifiedAll);
		sec.setAssociateVerified(isVerifiedAll);

		sec.setCertVerified(isVerifiedAll);
		sec.setExperienceVerified(isVerifiedAll);
		sec.setCaptialVerified(isVerifiedAll);

		sec.setShareholderVerifed(isVerifiedAll);
		sec.setEquityCompVerifed(isVerifiedAll);
		sec.setPersonnelVerified(isVerifiedAll);

		sec.setBankVerified(isVerifiedAll);
		sec.setCategoryVerified(isVerifiedAll);
		sec.setSitevisitVerified(isVerifiedAll);
		sec.setFactoryVerify(isVerifiedAll);
		sec.setManufacturingVerified(isVerifiedAll);
		sec.setMachineryVerified(isVerifiedAll);
		sec.setLicenseVerified(isVerifiedAll);
		sec.setAssocBodyVerified(isVerifiedAll);

		return sec;
	}

	private SmAddress setDefaultAddr() {
		SmAddress addr = new SmAddress();
		PmCountry cntry = new PmCountry();
		cntry.setCountryId(1l);
		addr.setPmCity(new PmCity());
		addr.setPmCountry(cntry);
		addr.setPmState(new PmState());
		addr.setPmDistrict(new PmDistrict());

		return addr;
	}

	private List<SmStaffTotal> getStaffList(String supplierType) {
		List<SmStaffTotal> staffList = new ArrayList<SmStaffTotal>();
		System.out.println("****  getStaffList *********" + supplierType);
		SmAppl app = getManagedBean().getSmAppl();
		if (supplierType.equals(SupplierManagementConst.STYPE_CONTRACTOR)) {
			SmStaffTotal stf = new SmStaffTotal();
			stf.setRevNo(new BigDecimal(1));
			stf.setStaffCategory("C");
			stf.setSmAppl(app);
			staffList.add(stf);
		} else if (supplierType
				.equals(SupplierManagementConst.STYPE_CONSULTANT)) {
			SmStaffTotal stf1 = new SmStaffTotal();
			stf1.setStaffCategory("P");
			stf1.setSmAppl(app);
			stf1.setRevNo(new BigDecimal(1));
			staffList.add(stf1);
			SmStaffTotal stf2 = new SmStaffTotal();
			stf2.setStaffCategory("S");
			stf2.setSmAppl(app);
			stf2.setRevNo(new BigDecimal(1));
			staffList.add(stf2);
			SmStaffTotal stf3 = new SmStaffTotal();
			stf3.setStaffCategory("L");
			stf3.setSmAppl(app);
			stf3.setRevNo(new BigDecimal(1));
			staffList.add(stf3);
		}
		return staffList;
	}

	private void retrievePmCountryList() {
		ICountrySO reqSO = (ICountrySO) BusinessDelegate.getInstance()
				.getServiceObject("pm", ICountrySO.class);
		CountryResDTO resDTO = reqSO.retrieveCountryList();

		this.getManagedBean().setCountryLs(resDTO.getCountryList());
	}

	private PmCountry retrievePmCountryById(Long countryId) {
		ICountrySO reqSO = (ICountrySO) BusinessDelegate.getInstance()
				.getServiceObject("pm", ICountrySO.class);
		CountryReqDTO reqDTO = new CountryReqDTO();
		reqDTO.setCountryId(countryId);
		CountryResDTO resDTO = reqSO.retrieveCountryById(reqDTO);

		return resDTO.getPmCountry();
	}

	private PmParameter retrievePmParameterById(Long paramId) {
		IParameterSO reqSO = (IParameterSO) BusinessDelegate.getInstance()
				.getServiceObject("pm", IParameterSO.class);
		ParameterReqDTO reqDTO = new ParameterReqDTO();
		reqDTO.setParameterId(paramId);
		ParameterResDTO resDTO = reqSO.retrieveParameterById(reqDTO);

		return resDTO.getPmParameter();
	}

	private void retrieveParentCategoryList() {
		ICategoryCodeSO reqSO = (ICategoryCodeSO) BusinessDelegate
				.getInstance().getServiceObject("pm", ICategoryCodeSO.class);
		CategoryCodeResDTO resDTO = reqSO.retrieveCategoryL1List();

		this.getManagedBean().setSupplierParentCatList(
				resDTO.getCategoryL1List());
	}

	private void retrievePmCertBodyList() {
		ICertBodySO reqSO = (ICertBodySO) BusinessDelegate.getInstance()
				.getServiceObject("pm", ICertBodySO.class);
		CertBodyResDTO resDTO = reqSO.retrieveCertifiedBodyList();

		this.getManagedBean().setCertBodyLs(resDTO.getCertBodyList());
		System.out.println("****** After getting the cert body list *******"
				+ getManagedBean().getCertBodyLs().size());
	}

	private DocumentResDTO retrieveSupportingDocumentList(DocumentReqDTO reqDTO) {
		DocumentResDTO resDto = new DocumentResDTO();
		IDocumentSO reqSO = (IDocumentSO) BusinessDelegate.getInstance()
				.getServiceObject("pm", IDocumentSO.class);
		resDto = reqSO.retrieveSupportingDocumentList(reqDTO);
		return resDto;

	}

	public void retrieveBankFinOrgListByType() {
		boolean runQuery = true;
		FinancialOrgReqDTO reqDTO = new FinancialOrgReqDTO();
		System.out.println("******* Selected Value **********"
				+ getManagedBean().getSelectedBank().getAccountPurpose());
		if (getManagedBean().getSelectedBank().getAccountPurpose()
				.equalsIgnoreCase("P")) {
			reqDTO.setServiceType(ParameterConst.BANK_SERVICES_PAYMENT_BANK);
		} else if (getManagedBean().getSelectedBank().getAccountPurpose()
				.equalsIgnoreCase("R")) {
			reqDTO.setServiceType(ParameterConst.BANK_SERVICES_REGISTRATION);
		} else if (getManagedBean().getSelectedBank().getAccountPurpose()
				.equalsIgnoreCase("B")) {
			// TODO remove this data later to get confirmation
			reqDTO.setServiceType(ParameterConst.BANK_SERVICES_PAYMENT_BANK);
		} else {
			this.getManagedBean().setFinOrgLs(new ArrayList<PmFinancialOrg>());
			runQuery = false;
		}
		if (runQuery) {
			IFinancialOrgSO reqSO = (IFinancialOrgSO) BusinessDelegate
					.getInstance()
					.getServiceObject("pm", IFinancialOrgSO.class);
			System.out.println("******* Service Type ********"
					+ reqDTO.getServiceType());
			FinancialOrgResDTO resDTO = reqSO
					.retrieveFinancialOrgListByServiceType(reqDTO);

			this.getManagedBean().setFinOrgLs(resDTO.getFinancialOrgList());
		}
		System.out.println("****** After getting the fin org list *******"
				+ getManagedBean().getFinOrgLs().size());
	}

	public void assignSelectedSupportDoc(SmApplSupportDoc doc) {
		System.out.println("****** Assign Supporting Doc *******");
		getManagedBean().setSupportDoc(doc);
	}

	public void handleSupDocFileAttachment(FileUploadEvent event) {
		System.out.println("********handleFileAttachment********");
		FacesScopes facesScopes = new FacesScopes();
		if (event.getFile() != null) {
			System.out.println("***** File attached *******"
					+ event.getFile().getFileName());
			if (getManagedBean().getSmAppl().getSmApplSupportDocs()
					.contains(getManagedBean().getSupportDoc())) {
				System.out.println("******* Contains selected file ****");
				SmApplSupportDoc doc = getManagedBean().getSupportDoc();
				doc.getSmApplDocFiles().get(0)
						.setFileName(event.getFile().getFileName());
				getManagedBean()
						.getSmAppl()
						.getSmApplSupportDocs()
						.set(getManagedBean().getSmAppl()
								.getSmApplSupportDocs()
								.indexOf(getManagedBean().getSupportDoc()),
								getManagedBean().getSupportDoc());
				for (SmApplSupportDoc docs : getManagedBean().getSmAppl()
						.getSmApplSupportDocs()) {
					System.out.println(" *** The name is ****"
							+ docs.getSmApplDocFiles().get(0).getFileName());
				}
			}
		}

	}

	private void retrieveUomList() {
		IUomSO reqSO = (IUomSO) BusinessDelegate.getInstance()
				.getServiceObject("pm", IUomSO.class);
		UomResDTO resDTO = reqSO.retrieveUomList();

		this.getManagedBean().setUomLs(resDTO.getPmUomList());
	}

	private void retrieveRejectReasonList() {
		IReasonSO reqSO = (IReasonSO) BusinessDelegate.getInstance()
				.getServiceObject("pm", IReasonSO.class);
		ReasonReqDTO reqDTO = new ReasonReqDTO();
		reqDTO.setReasonCategory(ReasonCategory.SUPPLIER_REJECTION_REASON
				.value());
		reqDTO.setLanguage(new FacesScopes().getUserLocale().getLanguage());
		ReasonResDTO resDTO = reqSO.retrieveReasonDescListByCategory(reqDTO);
		System.out.println(" *** Reject List ****** "
				+ resDTO.getReasonDescList().size());
		this.getManagedBean().setReasonDescList(resDTO.getReasonDescList());
	}

	private void retrieveRegistrationStatusList() {
		IStatusSO reqSO = (IStatusSO) BusinessDelegate.getInstance()
				.getServiceObject("pm", IStatusSO.class);
		StatusReqDTO reqDTO = new StatusReqDTO();
		reqDTO.setLanguageCode(new FacesScopes().getUserLocale().getLanguage());
		StatusResDTO resDTO = reqSO.retrieveRegStatusList(reqDTO);
		System.out.println(" *** Registration status List ****** "
				+ resDTO.getStatusDescList().size());
		this.getManagedBean().setRegStatusDescList(resDTO.getStatusDescList());
	}

	private void retrievePmProfBodyList() {
		ICertBodySO reqSO = (ICertBodySO) BusinessDelegate.getInstance()
				.getServiceObject("pm", ICertBodySO.class);
		CertBodyResDTO resDTO = reqSO.retrieveProfessionalBodyList();

		this.getManagedBean().setProfBodyLs(resDTO.getCertBodyList());
		System.out.println("****** After getting the prof body list *******"
				+ getManagedBean().getProfBodyLs().size());
	}

	private List<PmState> retrievePmStateList(long countryId) {
		StateReqDTO reqDTO = new StateReqDTO();
		reqDTO.setCountryId(countryId);
		IStateSO reqSO = (IStateSO) BusinessDelegate.getInstance()
				.getServiceObject("pm", IStateSO.class);
		StateResDTO resDTO = reqSO.retriveStatesListByCountryID(reqDTO);
		return resDTO.getStateList();
	}

	private List<PmDistrict> retrievePmDistrictList(long stateId) {
		DistrictReqDTO reqDTO = new DistrictReqDTO();
		reqDTO.setStateId(stateId);
		IDistrictSO reqSO = (IDistrictSO) BusinessDelegate.getInstance()
				.getServiceObject("pm", IDistrictSO.class);
		DistrictResDTO resDTO = reqSO.retriveDistrictListByStateID(reqDTO);
		return resDTO.getDistrictList();
	}

	private List<PmCity> retrievePmCityList(long districtId) {
		CityReqDTO reqDTO = new CityReqDTO();
		reqDTO.setDistrictId(districtId);
		ICitySO reqSO = (ICitySO) BusinessDelegate.getInstance()
				.getServiceObject("pm", ICitySO.class);
		CityResDTO resDTO = reqSO.retriveCityListByDistrictId(reqDTO);
		return resDTO.getCityList();
	}

	private List<PmCategoryL2> retrieveChildCategoryList(long categoryL1Id) {
		ICategoryCodeSO reqSO = (ICategoryCodeSO) BusinessDelegate
				.getInstance().getServiceObject("pm", ICategoryCodeSO.class);
		CategoryCodeReqDTO reqDTO = new CategoryCodeReqDTO();
		reqDTO.setCategoryL1Id(categoryL1Id);
		CategoryCodeResDTO resDTO = reqSO.retrieveCategoryL2ListByL1Id(reqDTO);

		return resDTO.getCategoryL2List();
	}

	private List<PmCategoryL3> retrieveSubChildCategoryList(long categoryL2Id) {
		ICategoryCodeSO reqSO = (ICategoryCodeSO) BusinessDelegate
				.getInstance().getServiceObject("pm", ICategoryCodeSO.class);
		CategoryCodeReqDTO reqDTO = new CategoryCodeReqDTO();
		reqDTO.setCategoryL2Id(categoryL2Id);
		System.out.println("** getSupplierType type ***"
				+ getManagedBean().getSmAppl().getSupplierType());
		System.out.println("** getApplCategoryType type ***"
				+ getManagedBean().getSmAppl().getApplCategoryType());
		// reqDTO.setSupplierTypeCode(getManagedBean().getSmAppl()
		// .getSupplierType());
		// if (getManagedBean().getSmAppl().getApplCategoryType().equals("N")) {
		// reqDTO.setIsSpecialCategory(false);
		// } else {
		// reqDTO.setIsSpecialCategory(true);
		// }
		CategoryCodeResDTO resDTO = reqSO.retrieveCategoryL3ListByL2Id(reqDTO);

		return resDTO.getCategoryL3List();
	}

	private SmAppl defaultSmAppl(SmAppl appl) {
		// = new SmAppl();
		if (appl.getSmSupplierBranches() == null) {
			appl.setSmSupplierBranches(new ArrayList<SmSupplierBranch>());
		}
		if (appl.getSmAssociateCompanies() == null) {
			appl.setSmAssociateCompanies(new ArrayList<SmAssociateCompany>());
		}
		if (appl.getSmAssociationBodies() == null) {
			appl.setSmAssociationBodies(new ArrayList<SmAssociationBody>());
		}
		if (appl.getSmSupplierCerts() == null) {
			appl.setSmSupplierCerts(new ArrayList<SmSupplierCert>());
		}
		if (appl.getSmEquityCompanies() == null) {
			appl.setSmEquityCompanies(new ArrayList<SmEquityCompany>());
		}
		if (appl.getSmSupplierCategories() == null) {
			appl.setSmSupplierCategories(new ArrayList<SmSupplierCategory>());
		}
		if (appl.getSmFacilities() == null) {
			appl.setSmFacilities(new ArrayList<SmFacility>());
		}
		if (appl.getSmManufacturingDtls() == null) {
			appl.setSmManufacturingDtls(new ArrayList<SmManufacturingDtl>());
		}
		if (appl.getSmMachineDtls() == null) {
			appl.setSmMachineDtls(new ArrayList<SmMachineDtl>());
		}
		if (appl.getSmPersonnels() == null) {
			appl.setSmPersonnels(new ArrayList<SmPersonnel>());
		}
		if (appl.getSmSupplierBanks() == null) {
			appl.setSmSupplierBanks(new ArrayList<SmSupplierBank>());
		}
		if (appl.getSmApplRejectReasons() == null) {
			appl.setSmApplRejectReasons(new ArrayList<SmApplRejectReason>());
		}

		return appl;
	}

	private MofRegistrationResDTO cloneSmApplicationDraft(long id,
			String applicationType, String supplierType) {
		IMofRegistrationSO mofRegistrationSO = null;

		try {
			mofRegistrationSO = (IMofRegistrationSO) BusinessDelegate
					.getInstance().getServiceObject("sm",
							IMofRegistrationSO.class);
			MofRegistrationReqDTO reqDto = new MofRegistrationReqDTO();
			reqDto.setSupplierId(id);
			reqDto.setAppType(applicationType);
			reqDto.setSupplierType(supplierType);
			// TODO remove the clone later (Problem at company basic clone
			// to be fixed)
			MofRegistrationResDTO resDTO = mofRegistrationSO
					.cloneSmApplicationDraft(reqDto);
			// MofRegistrationResDTO resDTO = mofRegistrationSO
			// .retrieveSupplierProfile(reqDto);
			return resDTO;

		} catch (Exception e) {
			System.out.println("**** THIS IS THE EXCEPTION IN CLONE APPL ****");
			e.printStackTrace();
			MofRegistrationResDTO resDTO = new MofRegistrationResDTO();
			resDTO.setErrorCaued(Boolean.TRUE);
			return resDTO;
		} finally {
			mofRegistrationSO = null;
		}
	}

	private MofRegistrationResDTO retriveSupplierById(long id) {
		IMofRegistrationSO mofRegistrationSO = null;

		try {
			mofRegistrationSO = (IMofRegistrationSO) BusinessDelegate
					.getInstance().getServiceObject("sm",
							IMofRegistrationSO.class);
			MofRegistrationReqDTO reqDto = new MofRegistrationReqDTO();
			reqDto.setSupplierId(id);
			MofRegistrationResDTO resDTO = mofRegistrationSO
					.retrieveSupplierProfile(reqDto);
			return resDTO;

		} catch (Exception e) {
			System.out
					.println("**** THIS IS THE EXCEPTION IN RETRIVE LIST ****");
			MofRegistrationResDTO resDTO = new MofRegistrationResDTO();
			resDTO.setErrorCaued(Boolean.TRUE);
			return resDTO;
		} finally {
			mofRegistrationSO = null;
		}
	}

	private MofRegistrationResDTO retriveLatestDraftApplication(
			SmSupplier supplier, String applType) {
		IMofRegistrationSO mofRegistrationSO = null;

		try {
			mofRegistrationSO = (IMofRegistrationSO) BusinessDelegate
					.getInstance().getServiceObject("sm",
							IMofRegistrationSO.class);
			MofRegistrationReqDTO reqDto = new MofRegistrationReqDTO();
			reqDto.setSmSupplier(supplier);
			reqDto.setAppType(applType);
			MofRegistrationResDTO resDTO = mofRegistrationSO
					.retrieveLatestSmApplicationDraft(reqDto);
			return resDTO;

		} catch (Exception e) {
			System.out
					.println("**** THIS IS THE EXCEPTION IN RETRIVE LIST ****");
			e.printStackTrace();
			MofRegistrationResDTO resDTO = new MofRegistrationResDTO();
			resDTO.setErrorCaued(Boolean.TRUE);
			return resDTO;
		} finally {
			mofRegistrationSO = null;
		}
	}

	private String generateApplicationNum(String applType) {
		return null;

	}

	private int assignMaxReviewCount(String appType, String applCatType,
			String supplierType) {
		int i = 0;
		switch (appType) {

		case "N":
			if (applCatType != null && applCatType.equalsIgnoreCase("S")) {
				i = SupplierManagementConst.NEW_APPL_SPLCAT_REVIEW_MAXCOUNT;
			} else {
				i = SupplierManagementConst.NEW_APPL_NORMCAT_REVIEW_MAXCOUNT;
			}
			break;
		case "A":
			if (applCatType != null && applCatType.equalsIgnoreCase("S")) {
				i = SupplierManagementConst.ADDLN_CATCODES_APPL_SPLCAT_REVIEW_MAXCOUNT;
			} else {
				i = SupplierManagementConst.ADDLN_CATCODES_APPL_NORMCAT_REVIEW_MAXCOUNT;
			}
			break;
		case "B":
			if (applCatType != null && applCatType.equalsIgnoreCase("S")) {
				i = SupplierManagementConst.BUMI_APPL_SPLCAT_REVIEW_MAXCOUNT;
			} else {
				i = SupplierManagementConst.BUMI_APPL_NORMCAT_REVIEW_MAXCOUNT;
			}
			break;
		case "U":
			i = SupplierManagementConst.UPDATE_MOF_APPL_REVIEW_MAXCOUNT;
			break;
		case "R":
			if (applCatType != null && applCatType.equalsIgnoreCase("S")) {
				i = SupplierManagementConst.RENEW_APPL_NORMCAT_REVIEW_MAXCOUNT;
			} else {
				i = SupplierManagementConst.RENEW_APPL_SPLCAT_REVIEW_MAXCOUNT;
			}
			break;
		default:
			break;

		}
		if (supplierType
				.equalsIgnoreCase(SupplierManagementConst.STYPE_CONSULTANT)) {
			i = i + 1;
		}
		return i;
	}

	private String setNavigationForAppl(String appType) {
		String s = null;
		switch (appType) {

		case "N":
			s = SupplierManagementConst.PROCEED_MOF_REG;
			break;
		case "A":
			s = SupplierManagementConst.PROCEED_ADDTNLCAT_APPLN_SIDEMENU;
			// PROCEED_ADDTNLCAT_MOF_APPLN;
			break;
		case "B":
			s = SupplierManagementConst.PROCEED_BUMI_APPLN_SIDEMENU;
			// PROCEED_BUMI_APPLN;
			break;
		case "U":
			s = SupplierManagementConst.PROCEED_UPDATE_APPLN_SIDEMENU;
			// PROCEED_UPDATE_MOF_APPLN;
			break;
		case "R":
			s = SupplierManagementConst.PROCEED_RENEW_APPLN_SIDEMENU;
			// PROCEED_RENEW_MOF_APPLN;
			break;
		case "C":
			s = SupplierManagementConst.PROCEED_CANCEL_MOF_APPLN;
			break;
		default:
			break;

		}
		return s;
	}

	private String setExceptionNavigationForAppl(String appType) {
		String s = null;
		switch (appType) {
		case "N":
			s = SupplierManagementConst.DEFAULT_MOF_REG;
			break;
		case "A":
			s = SupplierManagementConst.DEFAULT_ADDTNLCAT_MOF_APPLN;
			break;
		case "B":
			s = SupplierManagementConst.DEFAULT_BUMI_APPLN;
			break;
		case "U":
			s = SupplierManagementConst.DEFAULT_UPDATE_MOF_APPLN;
			break;
		case "R":
			s = SupplierManagementConst.DEFAULT_RENEW_MOF_APPLN;
			break;
		case "C":
			s = SupplierManagementConst.DEFAULT_CANCEL_MOF_APPLN;
			break;
		default:
			break;

		}
		return s;
	}

	private String setParameterConst(String appType) {
		String s = null;
		switch (appType) {
		case "N":
			s = ParameterConst.NEW_SM_APPLN_FOR_MOF;
			break;
		case "A":
			s = ParameterConst.NEW_SM_APPLN_FOR_APPLCATCODES;
			break;
		case "B":
			s = ParameterConst.NEW_SM_APPLN_FOR_BUMI;
			break;
		case "U":
			s = ParameterConst.NEW_SM_APPLN_FOR_UPDATE;
			break;
		case "R":
			s = ParameterConst.NEW_SM_APPLN_FOR_RENEW;
			break;
		case "C":
			s = ParameterConst.NEW_SM_APPLN_FOR_CANCEL;
			break;
		default:
			break;

		}
		return s;
	}

	public void populateStatesByCountry(long coutryId, String panelName,
			PmState state, PmDistrict district, PmCity city) {
		try {
			List<PmState> stateLs = this.retrievePmStateList(coutryId);
			switch (panelName) {
			case SupplierManagementConst.SM_BASIC_INFO_PANEL:
				System.out.println("**** This is Basic Change Country Panel ");
				this.getManagedBean().setBasicCompanyStates(stateLs);
				this.getManagedBean().setBasicCompanyDistricts(
						new ArrayList<PmDistrict>());
				this.getManagedBean().setBasicCompanyCities(
						new ArrayList<PmCity>());
				break;
			case SupplierManagementConst.SM_OFFICE_FACILITY_INFO_PANEL:
				System.out
						.println("**** This is my correct off fac Change Country Panel ");
				this.getManagedBean().setBasicOffFacStates(stateLs);
				this.getManagedBean().setBasicOffFacDistricts(
						new ArrayList<PmDistrict>());
				this.getManagedBean().setBasicOffFacCities(
						new ArrayList<PmCity>());
				break;

			case SupplierManagementConst.SM_BRANCH_PANEL:
				System.out
						.println("**** This is my branch Change Country Panel ");
				this.getManagedBean().setBranchCompanyStates(stateLs);
				this.getManagedBean().setBranchCompanyDistricts(
						new ArrayList<PmDistrict>());
				this.getManagedBean().setBranchCompanyCities(
						new ArrayList<PmCity>());
				break;
			case SupplierManagementConst.SM_ASSOC_COMPANY_PANEL:
				System.out
						.println("**** This is my  assoc Change Country Panel ");
				this.getManagedBean().setAssocCompanyStates(stateLs);
				this.getManagedBean().setAssocCompanyDistricts(
						new ArrayList<PmDistrict>());
				this.getManagedBean().setAssocCompanyCities(
						new ArrayList<PmCity>());
				break;
			case SupplierManagementConst.SM_EQUI_COMPANY_PANEL:
				System.out
						.println("**** This is my equi Change Country Panel ");
				this.getManagedBean().setEquiCompanyStates(stateLs);
				this.getManagedBean().setEquiCompanyDistricts(
						new ArrayList<PmDistrict>());
				this.getManagedBean().setEquiCompanyCities(
						new ArrayList<PmCity>());
			case SupplierManagementConst.SM_FACTORY_FACILITY_PANEL:
				this.getManagedBean().setFactoryFacilityStates(stateLs);
				this.getManagedBean().setFactoryFacilityDistricts(
						new ArrayList<PmDistrict>());
				this.getManagedBean().setFactoryFacilityCities(
						new ArrayList<PmCity>());
				break;
			case SupplierManagementConst.SM_DOCKYARD_FACILITY_PANEL:
				this.getManagedBean().setDockFacilityStates(stateLs);
				this.getManagedBean().setDockFacilityDistricts(
						new ArrayList<PmDistrict>());
				this.getManagedBean().setDockFacilityCities(
						new ArrayList<PmCity>());
				break;
			case SupplierManagementConst.SM_WORKSHOP_FACILITY_PANEL:
				this.getManagedBean().setWorkShopFacilityStates(stateLs);
				this.getManagedBean().setWorkShopFacilityDistricts(
						new ArrayList<PmDistrict>());
				this.getManagedBean().setWorkShopFacilityCities(
						new ArrayList<PmCity>());
				break;
			case SupplierManagementConst.SM_STORE_FACILITY_PANEL:
				this.getManagedBean().setStoreFacilityStates(stateLs);
				this.getManagedBean().setStoreFacilityDistricts(
						new ArrayList<PmDistrict>());
				this.getManagedBean().setStoreFacilityCities(
						new ArrayList<PmCity>());
				break;
			case SupplierManagementConst.SM_SHRHLDR_PERM_ADDR_PANEL:
				this.getManagedBean().setShrHldrPermStates(stateLs);
				this.getManagedBean().setShrHldrPermDistricts(
						new ArrayList<PmDistrict>());
				this.getManagedBean().setShrHldrPermCities(
						new ArrayList<PmCity>());
				break;
			case SupplierManagementConst.SM_SHRHLDR_RESD_ADDR_PANEL:
				this.getManagedBean().setShrHldrResdStates(stateLs);
				this.getManagedBean().setShrHldrResdDistricts(
						new ArrayList<PmDistrict>());
				this.getManagedBean().setShrHldrResdCities(
						new ArrayList<PmCity>());
				break;
			case SupplierManagementConst.SM_SHRHLDR_OFF_ADDR_PANEL:
				this.getManagedBean().setShrHldrOffStates(stateLs);
				this.getManagedBean().setShrHldrOffDistricts(
						new ArrayList<PmDistrict>());
				this.getManagedBean().setShrHldrOffCities(
						new ArrayList<PmCity>());
				break;
			case SupplierManagementConst.SM_SHRHLDR_MAIL_ADDR_PANEL:
				this.getManagedBean().setShrHldrMailStates(stateLs);
				this.getManagedBean().setShrHldrMailDistricts(
						new ArrayList<PmDistrict>());
				this.getManagedBean().setShrHldrMailCities(
						new ArrayList<PmCity>());
				break;
			case SupplierManagementConst.SM_BANK_INFO_PANEL:
				this.getManagedBean().setBankStates(stateLs);
				this.getManagedBean().setBankDistricts(
						new ArrayList<PmDistrict>());
				this.getManagedBean().setBankCities(new ArrayList<PmCity>());
				break;
			case SupplierManagementConst.SM_PERSNL_PERM_ADDR_PANEL:
				this.getManagedBean().setPersnlPermStates(stateLs);
				this.getManagedBean().setPersnlPermDistricts(
						new ArrayList<PmDistrict>());
				this.getManagedBean().setPersnlPermCities(
						new ArrayList<PmCity>());
				break;
			case SupplierManagementConst.SM_PERSNL_RESD_ADDR_PANEL:
				this.getManagedBean().setPersnlResdStates(stateLs);
				this.getManagedBean().setPersnlResdDistricts(
						new ArrayList<PmDistrict>());
				this.getManagedBean().setPersnlResdCities(
						new ArrayList<PmCity>());
				break;
			case SupplierManagementConst.SM_PERSNL_OFF_ADDR_PANEL:
				this.getManagedBean().setPersnlOffStates(stateLs);
				this.getManagedBean().setPersnlOffDistricts(
						new ArrayList<PmDistrict>());
				this.getManagedBean().setPersnlOffCities(
						new ArrayList<PmCity>());
				break;
			case SupplierManagementConst.SM_PERSNL_MAIL_ADDR_PANEL:
				this.getManagedBean().setPersnlMailStates(stateLs);
				this.getManagedBean().setPersnlMailDistricts(
						new ArrayList<PmDistrict>());
				this.getManagedBean().setPersnlMailCities(
						new ArrayList<PmCity>());
				break;
			default:
				System.out.println("**** This is an unknown panel ***** ");
				break;
			}
			resetState(state);
			resetDistrict(district);
			resetCity(city);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void populateDistrictsByState(long stateId, String panelName,
			PmDistrict district, PmCity city) {
		System.out.println("********** Panel value from page *********"
				+ panelName);
		try {
			List<PmDistrict> distLs = this.retrievePmDistrictList(stateId);
			switch (panelName) {
			case SupplierManagementConst.SM_BASIC_INFO_PANEL:
				System.out.println("**** This is Basic Change State Panel ");
				this.getManagedBean().setBasicCompanyDistricts(distLs);
				this.getManagedBean().setBasicCompanyCities(
						new ArrayList<PmCity>());
				break;
			case SupplierManagementConst.SM_OFFICE_FACILITY_INFO_PANEL:
				System.out.println("**** This is Facility Change State Panel ");
				this.getManagedBean().setBasicOffFacDistricts(distLs);
				this.getManagedBean().setBasicOffFacCities(
						new ArrayList<PmCity>());
				break;

			case SupplierManagementConst.SM_BRANCH_PANEL:
				System.out.println("**** This is Branch Change State Panel ");
				this.getManagedBean().setBranchCompanyDistricts(distLs);
				this.getManagedBean().setBranchCompanyCities(
						new ArrayList<PmCity>());
				break;
			case SupplierManagementConst.SM_ASSOC_COMPANY_PANEL:
				System.out.println("**** This is Assoc Change State Panel ");
				this.getManagedBean().setAssocCompanyDistricts(distLs);
				this.getManagedBean().setAssocCompanyCities(
						new ArrayList<PmCity>());
				break;
			case SupplierManagementConst.SM_EQUI_COMPANY_PANEL:
				System.out.println("**** This is Equi Change State Panel ");
				this.getManagedBean().setEquiCompanyDistricts(distLs);
				this.getManagedBean().setEquiCompanyCities(
						new ArrayList<PmCity>());
				break;

			case SupplierManagementConst.SM_FACTORY_FACILITY_PANEL:
				this.getManagedBean().setFactoryFacilityDistricts(distLs);
				this.getManagedBean().setFactoryFacilityCities(
						new ArrayList<PmCity>());
				break;
			case SupplierManagementConst.SM_DOCKYARD_FACILITY_PANEL:
				this.getManagedBean().setDockFacilityDistricts(distLs);
				this.getManagedBean().setDockFacilityCities(
						new ArrayList<PmCity>());
				break;
			case SupplierManagementConst.SM_WORKSHOP_FACILITY_PANEL:
				this.getManagedBean().setWorkShopFacilityDistricts(distLs);
				this.getManagedBean().setWorkShopFacilityCities(
						new ArrayList<PmCity>());
				break;
			case SupplierManagementConst.SM_STORE_FACILITY_PANEL:
				this.getManagedBean().setStoreFacilityDistricts(distLs);
				this.getManagedBean().setStoreFacilityCities(
						new ArrayList<PmCity>());
				break;
			case SupplierManagementConst.SM_SHRHLDR_PERM_ADDR_PANEL:
				this.getManagedBean().setShrHldrPermDistricts(distLs);
				this.getManagedBean().setShrHldrPermCities(
						new ArrayList<PmCity>());
				break;
			case SupplierManagementConst.SM_SHRHLDR_RESD_ADDR_PANEL:
				this.getManagedBean().setShrHldrResdDistricts(distLs);
				this.getManagedBean().setShrHldrResdCities(
						new ArrayList<PmCity>());
				break;
			case SupplierManagementConst.SM_SHRHLDR_OFF_ADDR_PANEL:
				this.getManagedBean().setShrHldrOffDistricts(distLs);
				this.getManagedBean().setShrHldrOffCities(
						new ArrayList<PmCity>());
				break;
			case SupplierManagementConst.SM_SHRHLDR_MAIL_ADDR_PANEL:
				this.getManagedBean().setShrHldrMailDistricts(distLs);
				this.getManagedBean().setShrHldrMailCities(
						new ArrayList<PmCity>());
				break;
			case SupplierManagementConst.SM_PERSNL_PERM_ADDR_PANEL:
				this.getManagedBean().setPersnlPermDistricts(distLs);
				this.getManagedBean().setPersnlPermCities(
						new ArrayList<PmCity>());
				break;
			case SupplierManagementConst.SM_PERSNL_RESD_ADDR_PANEL:
				this.getManagedBean().setPersnlResdDistricts(distLs);
				this.getManagedBean().setPersnlResdCities(
						new ArrayList<PmCity>());
				break;
			case SupplierManagementConst.SM_PERSNL_OFF_ADDR_PANEL:
				this.getManagedBean().setPersnlOffDistricts(distLs);
				this.getManagedBean().setPersnlOffCities(
						new ArrayList<PmCity>());
				break;
			case SupplierManagementConst.SM_PERSNL_MAIL_ADDR_PANEL:
				this.getManagedBean().setPersnlMailDistricts(distLs);
				this.getManagedBean().setPersnlMailCities(
						new ArrayList<PmCity>());
				break;
			case SupplierManagementConst.SM_BANK_INFO_PANEL:
				this.getManagedBean().setBankDistricts(distLs);
				this.getManagedBean().setBankCities(new ArrayList<PmCity>());
				break;
			default:
				System.out.println("**** This is an unknown panel ***** ");
				break;
			}
			resetDistrict(district);
			resetCity(city);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void populateCitiesByDistrict(long districtId, String panelName,
			PmCity city) {
		try {
			List<PmCity> cityLs = this.retrievePmCityList(districtId);
			switch (panelName) {
			case SupplierManagementConst.SM_BASIC_INFO_PANEL:
				System.out.println("**** This is Basic Change District Panel ");
				this.getManagedBean().setBasicCompanyCities(cityLs);
				break;
			case SupplierManagementConst.SM_OFFICE_FACILITY_INFO_PANEL:
				System.out
						.println("**** This is Facility Change District Panel ");
				this.getManagedBean().setBasicOffFacCities(cityLs);
				break;
			case SupplierManagementConst.SM_BRANCH_PANEL:
				System.out
						.println("**** This is Branch Change District Panel ");
				this.getManagedBean().setBranchCompanyCities(cityLs);
				break;
			case SupplierManagementConst.SM_ASSOC_COMPANY_PANEL:
				System.out.println("**** This is Assoc Change District Panel ");
				this.getManagedBean().setAssocCompanyCities(cityLs);
				break;
			case SupplierManagementConst.SM_EQUI_COMPANY_PANEL:
				System.out
						.println("**** This is Equity Change District Panel ");
				this.getManagedBean().setEquiCompanyCities(cityLs);
				break;
			case SupplierManagementConst.SM_FACTORY_FACILITY_PANEL:
				this.getManagedBean().setFactoryFacilityCities(cityLs);
				break;
			case SupplierManagementConst.SM_DOCKYARD_FACILITY_PANEL:
				this.getManagedBean().setDockFacilityCities(cityLs);
				break;
			case SupplierManagementConst.SM_WORKSHOP_FACILITY_PANEL:
				this.getManagedBean().setWorkShopFacilityCities(cityLs);
				break;
			case SupplierManagementConst.SM_STORE_FACILITY_PANEL:
				this.getManagedBean().setStoreFacilityCities(cityLs);
			case SupplierManagementConst.SM_SHRHLDR_PERM_ADDR_PANEL:
				this.getManagedBean().setShrHldrPermCities(cityLs);
				break;
			case SupplierManagementConst.SM_SHRHLDR_RESD_ADDR_PANEL:
				this.getManagedBean().setShrHldrResdCities(cityLs);
				break;
			case SupplierManagementConst.SM_SHRHLDR_OFF_ADDR_PANEL:
				this.getManagedBean().setShrHldrOffCities(cityLs);
				break;
			case SupplierManagementConst.SM_SHRHLDR_MAIL_ADDR_PANEL:
				this.getManagedBean().setShrHldrMailCities(cityLs);
				break;
			case SupplierManagementConst.SM_PERSNL_PERM_ADDR_PANEL:
				this.getManagedBean().setPersnlPermCities(cityLs);
				break;
			case SupplierManagementConst.SM_PERSNL_RESD_ADDR_PANEL:
				this.getManagedBean().setPersnlResdCities(cityLs);
				break;
			case SupplierManagementConst.SM_PERSNL_OFF_ADDR_PANEL:
				this.getManagedBean().setPersnlOffCities(cityLs);
				break;
			case SupplierManagementConst.SM_PERSNL_MAIL_ADDR_PANEL:
				this.getManagedBean().setPersnlMailCities(cityLs);
				break;
			case SupplierManagementConst.SM_BANK_INFO_PANEL:
				this.getManagedBean().setBankCities(cityLs);
				break;
			default:
				System.out.println("**** This is an unknown panel ***** ");
				break;
			}
			resetCity(city);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void populateChildCategoryByParent(long parentId,
			PmCategoryL2 catL2, PmCategoryL3 catL3) {
		try {
			List<PmCategoryL2> categoryL2s = this
					.retrieveChildCategoryList(parentId);

			this.getManagedBean().setSupplierChildCatList(categoryL2s);
			this.getManagedBean().setSupplierSubChildCatList(
					new ArrayList<PmCategoryL3>());

			resetChildCategory(catL2);
			resetSubChildCategory(catL3);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void populateSubChildCategoryByChild(long childId, PmCategoryL3 catL3) {
		try {
			List<PmCategoryL3> categoryL3s = this
					.retrieveSubChildCategoryList(childId);

			this.getManagedBean().setSupplierSubChildCatList(categoryL3s);

			resetSubChildCategory(catL3);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void resetDistrict(PmDistrict dist) {
		dist.setDistrictId(null);
	}

	private void resetCity(PmCity city) {
		city.setCityId(null);
	}

	private void resetState(PmState state) {
		state.setStateId(null);
	}

	private void resetChildCategory(PmCategoryL2 catL2) {
		catL2.setCategoryL2Id(null);

	}

	private void resetSubChildCategory(PmCategoryL3 catL3) {
		catL3.setCategoryL3Id(null);

	}

	private void setApplicationFailities(List<SmFacility> smFacilities) {
		System.out.println("*** Existing Facility******");
		getManagedBean().setApplFactoryFacilities(new ArrayList<SmFacility>());
		getManagedBean().setApplDockyardFacilities(new ArrayList<SmFacility>());
		getManagedBean().setApplWorkShopFacilities(new ArrayList<SmFacility>());
		getManagedBean().setApplStoreFacilities(new ArrayList<SmFacility>());

		for (SmFacility smFac : smFacilities) {
			if (smFac.getFacilityType().equalsIgnoreCase(
					SupplierManagementConst.SM_FACILITY_TYPE_HOMEOFFICE)
					|| smFac.getFacilityType()
							.equalsIgnoreCase(
									SupplierManagementConst.SM_FACILITY_TYPE_BUSSINESSOFFICE)) {
				getManagedBean().setBasicOffFacStates(
						retrievePmStateList(smFac.getSmAddress().getPmCountry()
								.getCountryId()));
				System.out
						.println("*********Setting Basic company value **********");
				this.getManagedBean().setBasicCompanyFacility(smFac);
			}
			if (smFac.getFacilityType().equalsIgnoreCase(
					SupplierManagementConst.SM_FACILITY_TYPE_FACTORY)) {
				getManagedBean().getApplFactoryFacilities().add(smFac);
			} else if (smFac.getFacilityType().equalsIgnoreCase(
					SupplierManagementConst.SM_FACILITY_TYPE_DOCKYARD)) {
				getManagedBean().getApplDockyardFacilities().add(smFac);
			} else if (smFac.getFacilityType().equalsIgnoreCase(
					SupplierManagementConst.SM_FACILITY_TYPE_WORKSHOP)) {
				getManagedBean().getApplWorkShopFacilities().add(smFac);
			} else if (smFac.getFacilityType().equalsIgnoreCase(
					SupplierManagementConst.SM_FACILITY_TYPE_STORE)) {
				getManagedBean().getApplStoreFacilities().add(smFac);
			}
		}

	}

	private void setApplicationShareHolders(List<SmPersonnel> perList) {
		System.out.println("*** Existing Personnel******");
		getManagedBean().setSmapplShareHolders(new ArrayList<SmPersonnel>());

		for (SmPersonnel per : perList) {

			if (per.isEquityOwner()) {
				getManagedBean().getSmapplShareHolders().add(per);
			}
		}
		System.out.println("*** After reset *****"
				+ getManagedBean().getSmapplShareHolders().size());

	}

	private void setDefaultPersonnelValues(SmPersonnel shr) {

		shr.setRevNo(1);
		shr.setSmAppl(getManagedBean().getSmAppl());
		shr.setBumiFlg(1);
		shr.setMuslimFlg(1);
		shr.setPmCountry(new PmCountry());
		shr.setSmPersonnelAddresses(new ArrayList<SmPersonnelAddress>());
		// shr.setSmPersonnelBranches(new ArrayList<SmPersonnelBranch>());
		// SmPersonnelBranch pbr = new SmPersonnelBranch();
		// pbr.setSmPersonnel(shr);
		// pbr.setSmSupplierBranch(new SmSupplierBranch());
		// shr.getSmPersonnelBranches().add(pbr);
		shr.setSmStaffAcademics(new ArrayList<SmStaffAcademic>());
		shr.setSmStaffExperiences(new ArrayList<SmStaffExperience>());
		shr.setSmRelatedCompanies(new ArrayList<SmRelatedCompany>());
		shr.setSmAssociationBodies(new ArrayList<SmAssociationBody>());
		shr.setSmPersonnelAddresses(new ArrayList<SmPersonnelAddress>());
	}

	private void setDefaultApproverReviewSection() {

		boolean containsAppRev = false;

		if (!getManagedBean().getSmAppl().getSmApplSectionReviews().isEmpty()) {
			for (SmApplSectionReview rev : getManagedBean().getSmAppl()
					.getSmApplSectionReviews()) {
				if (rev.getRoleType().equalsIgnoreCase(
						SupplierManagementConst.SECTION_REV_ROLE_APP)) {
					getManagedBean().setApprvrSecReview(rev);
					containsAppRev = true;
				}
				if (rev.getRoleType().equalsIgnoreCase(
						SupplierManagementConst.SECTION_REV_ROLE_PO)) {
					getManagedBean().setPoSecReview(rev);
				}
			}
		}
		if (!containsAppRev) {
			SmApplSectionReview appRev = new SmApplSectionReview();
			appRev.setSmAppl(getManagedBean().getSmAppl());
			appRev.setRoleType(SupplierManagementConst.SECTION_REV_ROLE_APP);
			getManagedBean().setApprvrSecReview(appRev);
			getManagedBean().getSmAppl().getSmApplSectionReviews()
					.add(getManagedBean().getApprvrSecReview());
		}

	}

	private void setDefaultPoReviewSection() {

		boolean containsPORev = false;

		if (!getManagedBean().getSmAppl().getSmApplSectionReviews().isEmpty()) {
			System.out.println("** Contains a list of reviews***"
					+ getManagedBean().getSmAppl().getSmApplSectionReviews()
							.size());
			for (SmApplSectionReview rev : getManagedBean().getSmAppl()
					.getSmApplSectionReviews()) {
				System.out
						.println("***** Role Type ******" + rev.getRoleType());
				if (rev.getRoleType().equalsIgnoreCase(
						SupplierManagementConst.SECTION_REV_ROLE_PO)) {
					getManagedBean().setPoSecReview(rev);
					containsPORev = true;
				}
			}
		}
		if (!containsPORev) {
			SmApplSectionReview poRev = new SmApplSectionReview();
			poRev.setSmAppl(getManagedBean().getSmAppl());
			poRev.setRoleType(SupplierManagementConst.SECTION_REV_ROLE_PO);
			getManagedBean().setPoSecReview(poRev);
			getManagedBean().getSmAppl().getSmApplSectionReviews()
					.add(getManagedBean().getPoSecReview());
		}

	}

	private boolean validateAuthPersonLimit() {
		boolean validAuthPerCount = true;
		int authPerCount = 0;
		for (SmPersonnel per : getManagedBean().getSmAppl().getSmPersonnels()) {
			if (per.getAuthorizedFlg() == 1) {
				authPerCount++;
			}
		}
		if (authPerCount == 2) {
			FacesContext
					.getCurrentInstance()
					.addMessage(
							null,
							new FacesMessage(
									FacesMessage.SEVERITY_ERROR,
									"Max limit exceed : ",
									"You already have 5 authorized users. Please un authorise the user in Priviliges section"));
			validAuthPerCount = false;
		}
		return validAuthPerCount;
	}

	private int getAuthUserCount() {
		int authPerCount = 0;
		for (SmPersonnel per : getManagedBean().getSmAppl().getSmPersonnels()) {
			if (per.getAuthorizedFlg() == 1) {
				authPerCount = authPerCount + 1;
			}
			// if (per.getSmPersonnelAddresses().isEmpty()) {
			// System.out
			// .println("****** There is record without address ********* ");
			// FacesContext.getCurrentInstance().addMessage(
			// null,
			// new FacesMessage(FacesMessage.SEVERITY_ERROR,
			// "Address Missing : ",
			// "Please provide the address details for "
			// + per.getName()
			// + " before adding a new user"));
			// }
		}
		return authPerCount;
	}

	public void calculateShareHolderPercent() {
		try {
			if (getManagedBean().getSelectedShareHolder().getShareAmt() != null) {
				getManagedBean().getSelectedShareHolder().setShareAmt(
						getManagedBean().getSelectedShareHolder().getShareAmt()
								.setScale(2, RoundingMode.HALF_UP));
			}
			getManagedBean().setEquiPerCapTotal(new BigDecimal(0));
			for (SmPersonnel per : getManagedBean().getSmAppl()
					.getSmPersonnels()) {
				if (per.isEquityOwner()
						&& !per.getPersonnelId().equals(
								getManagedBean().getSelectedShareHolder()
										.getPersonnelId())) {
					getManagedBean().setEquiPerCapTotal(
							getManagedBean().getEquiPerCapTotal()
									.add(per.getShareAmt())
									.setScale(2, RoundingMode.HALF_UP));
				}
			}
			getManagedBean().setEquiPerCapTotal(
					getManagedBean()
							.getEquiPerCapTotal()
							.add(getManagedBean().getSelectedShareHolder()
									.getShareAmt())
							.setScale(2, RoundingMode.HALF_UP));
			if (getManagedBean().getEquiPerCapTotal().compareTo(
					getManagedBean().getSmAppl().getSmCompanyBasic()
							.getPaidUpCapital()) <= 0) {
				BigDecimal mul = new BigDecimal(100);
				if (getManagedBean().getSmAppl().getSmCompanyBasic()
						.getPaidUpCapital().compareTo(new BigDecimal(0)) > 0) {
					getManagedBean().getSelectedShareHolder()
							.setSharePercentage(
									getManagedBean()
											.getSelectedShareHolder()
											.getShareAmt()
											.divide(getManagedBean()
													.getSmAppl()
													.getSmCompanyBasic()
													.getPaidUpCapital(), 4,
													RoundingMode.HALF_UP)
											.multiply(mul)
											.setScale(2, RoundingMode.HALF_UP));
				}

			} else {
				getManagedBean().getSelectedShareHolder().setSharePercentage(
						new BigDecimal(0).setScale(2, RoundingMode.HALF_UP));
				getManagedBean().getSelectedShareHolder().setShareAmt(
						new BigDecimal(0).setScale(2, RoundingMode.HALF_UP));
				FacesContext
						.getCurrentInstance()
						.addMessage(
								null,
								new FacesMessage(
										FacesMessage.SEVERITY_ERROR,
										"Invalid Peronnel Share Amout  : ",
										"Total of personnels share amount cannot exceed Paid-up capital.Re enter the amounts "));
			}
		} catch (NumberFormatException ne) {
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"Invalid Value  : ", "Please enter numbers only "));

			ne.printStackTrace();
		}
	}

	public void calculateEquityCompanyPercent() {
		try {
			if (getManagedBean().getSelectedEquityCompany().getShareAmt() != null) {
				getManagedBean().getSelectedEquityCompany().setShareAmt(
						getManagedBean().getSelectedEquityCompany()
								.getShareAmt()
								.setScale(2, RoundingMode.HALF_UP));
			}
			getManagedBean().setEquiCompCapTotal(new BigDecimal(0));
			for (SmEquityCompany comp : getManagedBean().getSmAppl()
					.getSmEquityCompanies()) {
				if (!(comp.getEquityCompanyId() == null && comp
						.equals(getManagedBean().getSelectedEquityCompany()))
						&& !(comp.getEquityCompanyId() != null && comp
								.getEquityCompanyId().equals(
										getManagedBean()
												.getSelectedEquityCompany()
												.getEquityCompanyId()))) {

					getManagedBean().setEquiCompCapTotal(
							getManagedBean().getEquiCompCapTotal()
									.add(comp.getShareAmt())
									.setScale(2, RoundingMode.HALF_UP));
				}
			}
			getManagedBean().setEquiCompCapTotal(
					getManagedBean()
							.getEquiCompCapTotal()
							.add(getManagedBean().getSelectedEquityCompany()
									.getShareAmt())
							.setScale(2, RoundingMode.HALF_UP));
			if (getManagedBean().getEquiCompCapTotal().compareTo(
					getManagedBean().getSmAppl().getSmCompanyBasic()
							.getPaidUpCapital()) <= 0) {
				BigDecimal mul = new BigDecimal(100);
				if (getManagedBean().getSmAppl().getSmCompanyBasic()
						.getPaidUpCapital().compareTo(new BigDecimal(0)) > 0) {
					getManagedBean().getSelectedEquityCompany()
							.setSharePercentage(
									getManagedBean()
											.getSelectedEquityCompany()
											.getShareAmt()
											.divide(getManagedBean()
													.getSmAppl()
													.getSmCompanyBasic()
													.getPaidUpCapital(), 4,
													RoundingMode.HALF_UP)
											.multiply(mul)
											.setScale(2, RoundingMode.HALF_UP));
				}

			} else {
				getManagedBean().getSelectedEquityCompany().setSharePercentage(
						new BigDecimal(0).setScale(2, RoundingMode.HALF_UP));
				getManagedBean().getSelectedEquityCompany().setShareAmt(
						new BigDecimal(0).setScale(2, RoundingMode.HALF_UP));
				FacesContext
						.getCurrentInstance()
						.addMessage(
								null,
								new FacesMessage(
										FacesMessage.SEVERITY_ERROR,
										"Invalid Company Share Amout  : ",
										"Total of companies share amount cannot exceed Paid-up capital.Re enter the amounts "));
			}
		} catch (NumberFormatException ne) {
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"Invalid Value  : ", "Please enter numbers only "));

			ne.printStackTrace();
		}
	}

	public void calculateCapital() {
		try {
			if (getManagedBean().getSmAppl().getSmCompanyBasic()
					.getBumiEquity() != null) {
				getManagedBean()
						.getSmAppl()
						.getSmCompanyBasic()
						.setBumiEquity(
								getManagedBean().getSmAppl()
										.getSmCompanyBasic().getBumiEquity()
										.setScale(2, RoundingMode.HALF_UP));
			}
			if (getManagedBean().getSmAppl().getSmCompanyBasic()
					.getNonBumiEquity() != null) {
				getManagedBean()
						.getSmAppl()
						.getSmCompanyBasic()
						.setNonBumiEquity(
								getManagedBean().getSmAppl()
										.getSmCompanyBasic().getNonBumiEquity()
										.setScale(2, RoundingMode.HALF_UP));
			}
			if (getManagedBean().getSmAppl().getSmCompanyBasic()
					.getGovtEquity() != null) {
				getManagedBean()
						.getSmAppl()
						.getSmCompanyBasic()
						.setGovtEquity(
								getManagedBean().getSmAppl()
										.getSmCompanyBasic().getGovtEquity()
										.setScale(2, RoundingMode.HALF_UP));
			}
			if (getManagedBean().getSmAppl().getSmCompanyBasic()
					.getGovtUnitrustEquity() != null) {
				getManagedBean()
						.getSmAppl()
						.getSmCompanyBasic()
						.setGovtUnitrustEquity(
								getManagedBean().getSmAppl()
										.getSmCompanyBasic()
										.getGovtUnitrustEquity()
										.setScale(2, RoundingMode.HALF_UP));
			}
			if (getManagedBean().getSmAppl().getSmCompanyBasic()
					.getForeignCompanyEquity() != null) {
				getManagedBean()
						.getSmAppl()
						.getSmCompanyBasic()
						.setForeignCompanyEquity(
								getManagedBean().getSmAppl()
										.getSmCompanyBasic()
										.getForeignCompanyEquity()
										.setScale(2, RoundingMode.HALF_UP));
			}
			if (getManagedBean().getSmAppl().getSmCompanyBasic()
					.getForeignerEquity() != null) {
				getManagedBean()
						.getSmAppl()
						.getSmCompanyBasic()
						.setForeignerEquity(
								getManagedBean().getSmAppl()
										.getSmCompanyBasic()
										.getForeignerEquity()
										.setScale(2, RoundingMode.HALF_UP));
			}
			if (getManagedBean().getSmAppl().getSmCompanyBasic()
					.getLocalCompanyEquity() != null) {
				getManagedBean()
						.getSmAppl()
						.getSmCompanyBasic()
						.setLocalCompanyEquity(
								getManagedBean().getSmAppl()
										.getSmCompanyBasic()
										.getLocalCompanyEquity()
										.setScale(2, RoundingMode.HALF_UP));
			}
			if (getManagedBean().getSmAppl().getSmCompanyBasic()
					.getAuthorizedCapital() != null) {
				getManagedBean()
						.getSmAppl()
						.getSmCompanyBasic()
						.setAuthorizedCapital(
								getManagedBean().getSmAppl()
										.getSmCompanyBasic()
										.getAuthorizedCapital()
										.setScale(2, RoundingMode.HALF_UP));
			}
			getManagedBean()
					.getSmAppl()
					.getSmCompanyBasic()
					.setPaidUpCapital(
							getManagedBean()
									.getSmAppl()
									.getSmCompanyBasic()
									.getBumiEquity()
									.add(getManagedBean().getSmAppl()
											.getSmCompanyBasic()
											.getNonBumiEquity())
									.add(getManagedBean().getSmAppl()
											.getSmCompanyBasic()
											.getLocalCompanyEquity())
									.add(getManagedBean().getSmAppl()
											.getSmCompanyBasic()
											.getForeignCompanyEquity())
									.add(getManagedBean().getSmAppl()
											.getSmCompanyBasic()
											.getForeignerEquity())
									.add(getManagedBean().getSmAppl()
											.getSmCompanyBasic()
											.getGovtEquity())
									.add(getManagedBean().getSmAppl()
											.getSmCompanyBasic()
											.getGovtUnitrustEquity())
									.setScale(2, RoundingMode.HALF_UP));
			System.out.println("**** Paid up capital ****** "
					+ getManagedBean().getSmAppl().getSmCompanyBasic()
							.getPaidUpCapital());
			if (getManagedBean()
					.getSmAppl()
					.getSmCompanyBasic()
					.getPaidUpCapital()
					.compareTo(
							getManagedBean().getSmAppl().getSmCompanyBasic()
									.getAuthorizedCapital()) <= 0) {
				BigDecimal mul = new BigDecimal(100);

				if (getManagedBean().getSmAppl().getSmCompanyBasic()
						.getPaidUpCapital().compareTo(new BigDecimal(0)) > 0) {
					getManagedBean()
							.getSmAppl()
							.getSmCompanyBasic()
							.setBumiPercent(
									getManagedBean()
											.getSmAppl()
											.getSmCompanyBasic()
											.getBumiEquity()
											.divide(getManagedBean()
													.getSmAppl()
													.getSmCompanyBasic()
													.getPaidUpCapital(), 4,
													RoundingMode.HALF_UP)
											.multiply(mul)
											.setScale(2, RoundingMode.HALF_UP));

					getManagedBean()
							.getSmAppl()
							.getSmCompanyBasic()
							.setNonBumiPercent(
									getManagedBean()
											.getSmAppl()
											.getSmCompanyBasic()
											.getNonBumiEquity()
											.divide(getManagedBean()
													.getSmAppl()
													.getSmCompanyBasic()
													.getPaidUpCapital(), 4,
													RoundingMode.HALF_UP)
											.multiply(mul)
											.setScale(2, RoundingMode.HALF_UP));
					getManagedBean()
							.getSmAppl()
							.getSmCompanyBasic()
							.setForiegnerPercent(
									getManagedBean()
											.getSmAppl()
											.getSmCompanyBasic()
											.getForeignerEquity()
											.divide(getManagedBean()
													.getSmAppl()
													.getSmCompanyBasic()
													.getPaidUpCapital(), 4,
													RoundingMode.HALF_UP)
											.multiply(mul)
											.setScale(2, RoundingMode.HALF_UP));
					getManagedBean()
							.getSmAppl()
							.getSmCompanyBasic()
							.setGovtUTPercent(
									getManagedBean()
											.getSmAppl()
											.getSmCompanyBasic()
											.getGovtUnitrustEquity()
											.divide(getManagedBean()
													.getSmAppl()
													.getSmCompanyBasic()
													.getPaidUpCapital(), 4,
													RoundingMode.HALF_UP)
											.multiply(mul)
											.setScale(2, RoundingMode.HALF_UP));
					getManagedBean()
							.getSmAppl()
							.getSmCompanyBasic()
							.setGovtEquityPercent(
									getManagedBean()
											.getSmAppl()
											.getSmCompanyBasic()
											.getGovtEquity()
											.divide(getManagedBean()
													.getSmAppl()
													.getSmCompanyBasic()
													.getPaidUpCapital(), 4,
													RoundingMode.HALF_UP)
											.multiply(mul)
											.setScale(2, RoundingMode.HALF_UP));
					getManagedBean()
							.getSmAppl()
							.getSmCompanyBasic()
							.setForiegnCompPercent(
									getManagedBean()
											.getSmAppl()
											.getSmCompanyBasic()
											.getForeignCompanyEquity()
											.divide(getManagedBean()
													.getSmAppl()
													.getSmCompanyBasic()
													.getPaidUpCapital(), 4,
													RoundingMode.HALF_UP)
											.multiply(mul)
											.setScale(2, RoundingMode.HALF_UP));
					getManagedBean()
							.getSmAppl()
							.getSmCompanyBasic()
							.setLocalCompPercent(
									getManagedBean()
											.getSmAppl()
											.getSmCompanyBasic()
											.getLocalCompanyEquity()
											.divide(getManagedBean()
													.getSmAppl()
													.getSmCompanyBasic()
													.getPaidUpCapital(), 4,
													RoundingMode.HALF_UP)
											.multiply(mul)
											.setScale(2, RoundingMode.HALF_UP));
				}

			} else {
				FacesContext
						.getCurrentInstance()
						.addMessage(
								null,
								new FacesMessage(FacesMessage.SEVERITY_ERROR,
										"Invalid Paidup Capital  : ",
										"Paid-up capital cannot exceed authorized capital.Re enter the amounts "));
			}
		} catch (NumberFormatException ne) {
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"Invalid Value  : ", "Please enter numbers only "));

			ne.printStackTrace();
		}
	}

	private boolean checkValidUser(SmPersonnel pers) {
		boolean validUser = true;
		for (SmPersonnel per : getManagedBean().getSmAppl().getSmPersonnels()) {
			if (pers.getPersonnelId() == null && pers.equals(per)) {
				System.out.println("****** Same obj before save ");
			} else if (pers.getPersonnelId() != null
					&& pers.getPersonnelId().equals(per.getPersonnelId())) {
				System.out.println("**** Same obj without id ******");
			} else if (per.getIdentificationNo().equalsIgnoreCase(
					pers.getIdentificationNo())) {
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR,
								"Invalid User  : ",
								"The user with the specified IC/Passport num : '"
										+ pers.getIdentificationNo()
										+ "' already exists."));
				validUser = false;
				break;
			}
		}
		return validUser;
	}

	private boolean validateIctCatCode() {
		boolean validCat = true;
		if (getManagedBean().getSmAppl().getSupplierType()
				.equalsIgnoreCase(SupplierManagementConst.STYPE_CONSULTANT)) {
			int prfCnt = 0;
			int semPrfCnt = 0;
			for (SmPersonnel per : getManagedBean().getSmAppl()
					.getSmPersonnels()) {
				if (per.getStaffRoleFlg() != null
						&& per.getStaffRoleFlg() == 831
						&& per.getWorkExperienceYear() >= 5) {
					prfCnt = prfCnt + 1;

				} else if (per.getStaffRoleFlg() != null
						&& per.getStaffRoleFlg() == 832
						&& per.getWorkExperienceYear() >= 5) {

					semPrfCnt = semPrfCnt + 1;

				}
				if (prfCnt > 0 && semPrfCnt > 0) {
					break;
				}
			}
			if (semPrfCnt < 1) {
				FacesContext
						.getCurrentInstance()
						.addMessage(
								null,
								new FacesMessage(
										FacesMessage.SEVERITY_ERROR,
										"Valid semi professional staff is required : ",
										"At least one semi professional staff with at least 5 years experience is required to add ICT category.Please update personnel info to add this category"));
				validCat = false;
			}
			if (prfCnt < 1) {
				FacesContext
						.getCurrentInstance()
						.addMessage(
								null,
								new FacesMessage(
										FacesMessage.SEVERITY_ERROR,
										"Valid professional staff is required : ",
										"At least one professional staff with at least 5 years experience is required to add ICT category.Please update personnel info to add this category"));
				validCat = false;
			}

		}

		if (getManagedBean().getSmAppl().getSupplierType()
				.equalsIgnoreCase(SupplierManagementConst.STYPE_CONTRACTOR)) {
			System.out.println("***** Validate ICT For contractor ********");

			int ownrCnt = 0;
			int stfCnt = 0;
			int dirCnt = 0;
			for (SmPersonnel per : getManagedBean().getSmAppl()
					.getSmPersonnels()) {
				if (per.isEquityOwner() && per.getEpfNo() != null
						&& per.getEpfNo() != "") {
					ownrCnt = ownrCnt + 1;

				} else if (per.isDirector() && per.getEpfNo() != null
						&& per.getEpfNo() != "") {
					dirCnt = dirCnt + 1;

				} else if (per.getStaffRoleFlg() != null
						&& per.getStaffRoleFlg() == 835
						&& per.getEpfNo() != null && per.getEpfNo() != "") {

					stfCnt = stfCnt + 1;

				}
				if (stfCnt > 0 && dirCnt > 0 && ownrCnt > 0) {
					break;
				}
			}
			if (stfCnt < 1) {
				FacesContext
						.getCurrentInstance()
						.addMessage(
								null,
								new FacesMessage(
										FacesMessage.SEVERITY_ERROR,
										"Valid staff is required : ",
										"At least one staff with contribution to EPF is required to add ICT category.Please update personnel info to add this category"));
				validCat = false;
			}
			if (ownrCnt < 1) {
				System.out.println("****** No OWNER ********** ");
				FacesContext
						.getCurrentInstance()
						.addMessage(
								null,
								new FacesMessage(
										FacesMessage.SEVERITY_ERROR,
										"Valid owner is required : ",
										"At least one equity owner with contribution to EPF is required to add ICT category.Please update personnel info to add this category"));
				validCat = false;
			}
			if (dirCnt < 1) {
				FacesContext
						.getCurrentInstance()
						.addMessage(
								null,
								new FacesMessage(
										FacesMessage.SEVERITY_ERROR,
										"Valid director is required : ",
										"At least one director with contribution to EPF is required to add ICT category.Please update personnel info to add this category"));
				validCat = false;
			}

		}
		if (validCat) {
			for (SmSupplierCategory cat : getManagedBean().getSmAppl()
					.getSmSupplierCategories()) {
				if (getManagedBean().getSelectedCategory().getPmCategoryL3()
						.getCategoryL3Id() != 99
						&& getManagedBean().getSelectedCategory()
								.getPmCategoryL3().getCategoryL3Id() == cat
								.getPmCategoryL3().getCategoryL3Id()
						&& getManagedBean().getSelectedCategory()
								.getPmCategoryL2().getCategoryL2Id() == cat
								.getPmCategoryL2().getCategoryL2Id()
						&& getManagedBean().getSelectedCategory()
								.getPmCategoryL1().getCategoryL1Id() == cat
								.getPmCategoryL1().getCategoryL1Id()) {
					FacesContext
							.getCurrentInstance()
							.addMessage(
									null,
									new FacesMessage(
											FacesMessage.SEVERITY_ERROR,
											"Already Exists : ",
											"The requested category already exists.Please select another category."));
					validCat = false;
					break;

				}
			}
		}

		return validCat;

	}

	private void assignEnteredStaffValues() {
		for (SmStaffTotal stf : getManagedBean().getSmAppl().getSmStaffTotals()) {
			if (stf.getStaffCategory().equalsIgnoreCase("C")
					|| stf.getStaffCategory().equalsIgnoreCase("L")) {
				getManagedBean().getPersonCalc().setStfBumiActCnt(
						stf.getTotalBumi());
				getManagedBean().getPersonCalc().setStfFrgActCnt(
						stf.getTotalForeigner());
				getManagedBean().getPersonCalc().setStfNonBumiActCnt(
						stf.getTotalNonBumi());
			} else if (stf.getStaffCategory().equalsIgnoreCase("P")) {
				getManagedBean().getPersonCalc().setProfBumiActCnt(
						stf.getTotalBumi());
				getManagedBean().getPersonCalc().setProfFrgActCnt(
						stf.getTotalForeigner());
				getManagedBean().getPersonCalc().setProfNonBumiActCnt(
						stf.getTotalNonBumi());
			} else if (stf.getStaffCategory().equalsIgnoreCase("S")) {
				getManagedBean().getPersonCalc().setSemiBumiActCnt(
						stf.getTotalBumi());
				getManagedBean().getPersonCalc().setSemiFrgActCnt(
						stf.getTotalForeigner());
				getManagedBean().getPersonCalc().setSemiNonBumiActCnt(
						stf.getTotalNonBumi());
			}
		}
	}

	private void calcPersonnelListCount() {
		resetAllPersonnelCount();
		for (SmPersonnel per : getManagedBean().getSmAppl().getSmPersonnels()) {
			calcSumUpByPersonnel(per);
		}

	}

	private void calcSumUpByPersonnel(SmPersonnel per) {
		System.out.println("****** per country id ******"
				+ per.getPmCountry().getCountryId());
		// if (per.getPmCountry().getCountryCode() == null
		// || per.getPmCountry().getCountryCode() == "") {
		per.setPmCountry(retrievePmCountryById(per.getPmCountry()
				.getCountryId()));
		// }
		System.out.println("******Afte per country code ******"
				+ per.getPmCountry().getCountryCode());
		PmParameter param = null;
		if (per.getPmStaffRole() != null
				&& per.getPmStaffRole().getPmParameterType() != null) {
			param = per.getPmStaffRole();

		} else if (per.getStaffRoleFlg() != null) {
			param = new PmParameter();
			param = retrievePmParameterById(per.getStaffRoleFlg());
		}

		if (per.isEquityOwner() || per.isDirector()) {
			setDirectorCount(per);
		}
		if (per.isHigherManagement()) {
			setHigherMngmtCount(per);
		} else if (getManagedBean().getSmAppl().getSupplierType()
				.equalsIgnoreCase(SupplierManagementConst.STYPE_CONTRACTOR)) {
			if (per.getStaffRoleFlg() != null
					&& param != null
					&& param.getPmParameterType()
							.getParameterType()
							.equalsIgnoreCase(
									SupplierManagementConst.SM_PERSONNEL_STAFF_PARAMTYPE_CONTRACTOR)
					&& param.getParameterCode()
							.equals(SupplierManagementConst.PERSONNEL_MANAGEMENT_STAFF_CONTRACTOR_PARAMCODE)) {
				// management
				setManagementCount(per);
			} else if (per.getStaffRoleFlg() != null
					&& param != null
					&& param.getPmParameterType()
							.getParameterType()
							.equalsIgnoreCase(
									SupplierManagementConst.SM_PERSONNEL_STAFF_PARAMTYPE_CONTRACTOR)
					&& param.getParameterCode()
							.equals(SupplierManagementConst.PERSONNEL_NORMAL_STAFF_CONTRACTOR_PARAMCODE)) {
				// staff
				setStaffEnteredCount(per);

			}
		} else if (getManagedBean().getSmAppl().getSupplierType()
				.equalsIgnoreCase(SupplierManagementConst.STYPE_CONSULTANT)) {
			if (per.getStaffRoleFlg() != null
					&& param != null
					&& param.getPmParameterType()
							.getParameterType()
							.equalsIgnoreCase(
									SupplierManagementConst.SM_PERSONNEL_STAFF_PARAMTYPE_CONSULTANT)
					&& param.getParameterCode()
							.equals(SupplierManagementConst.PERSONNEL_MANAGEMENT_STAFF_CONSULTANT_PARAMCODE)) {
				// managment
				setManagementCount(per);

			} else if (per.getStaffRoleFlg() != null
					&& param != null
					&& param.getPmParameterType()
							.getParameterType()
							.equalsIgnoreCase(
									SupplierManagementConst.SM_PERSONNEL_STAFF_PARAMTYPE_CONSULTANT)
					&& param.getParameterCode()
							.equals(SupplierManagementConst.PERSONNEL_PROF_STAFF_CONSULTANT_PARAMCODE)) {
				// prof
				setProfEnteredCount(per);
			} else if (per.getStaffRoleFlg() != null
					&& param != null
					&& param.getPmParameterType()
							.getParameterType()
							.equalsIgnoreCase(
									SupplierManagementConst.SM_PERSONNEL_STAFF_PARAMTYPE_CONSULTANT)
					&& param.getParameterCode()
							.equals(SupplierManagementConst.PERSONNEL_SEMIPROF_STAFF_CONSULTANT_PARAMCODE)) {
				// semi
				setSemiProfEnteredCount(per);
			} else if (per.getStaffRoleFlg() != null
					&& param != null
					&& param.getPmParameterType()
							.getParameterType()
							.equalsIgnoreCase(
									SupplierManagementConst.SM_PERSONNEL_STAFF_PARAMTYPE_CONSULTANT)
					&& param.getParameterCode()
							.equals(SupplierManagementConst.PERSONNEL_CLERICAL_STAFF_CONSULTANT_PARAMCODE)) {
				// staff
				setStaffEnteredCount(per);
			}

		}

	}

	private void setManagementCount(SmPersonnel per) {
		// managment
		if (per.getPmCountry().getCountryCode()
				.equals(SupplierManagementConst.COUNTRY_MALAYSIA_CODE)) {
			if (per.getBumiFlg() == 1) {
				getManagedBean().getPersonCalc().setMngtBumiCnt(
						getManagedBean().getPersonCalc().getMngtBumiCnt() + 1);
			} else {
				getManagedBean().getPersonCalc()
						.setMngtNonBumiCnt(
								getManagedBean().getPersonCalc()
										.getMngtNonBumiCnt() + 1);
			}
		} else {
			getManagedBean().getPersonCalc().setMngtFrgCnt(
					getManagedBean().getPersonCalc().getMngtFrgCnt() + 1);
		}

	}

	private void setStaffEnteredCount(SmPersonnel per) {
		if (per.getPmCountry().getCountryCode()
				.equals(SupplierManagementConst.COUNTRY_MALAYSIA_CODE)) {
			if (per.getBumiFlg() == 1) {
				getManagedBean().getPersonCalc()
						.setStfBumiEnterCnt(
								getManagedBean().getPersonCalc()
										.getStfBumiEnterCnt() + 1);
			} else {
				getManagedBean().getPersonCalc().setStfNonBumiEnterCnt(
						getManagedBean().getPersonCalc()
								.getStfNonBumiEnterCnt() + 1);
			}
		} else {
			getManagedBean().getPersonCalc().setStfFrgEnterCnt(
					getManagedBean().getPersonCalc().getStfFrgEnterCnt() + 1);
		}
	}

	private void setSemiProfEnteredCount(SmPersonnel per) {
		if (per.getPmCountry().getCountryCode()
				.equals(SupplierManagementConst.COUNTRY_MALAYSIA_CODE)) {
			if (per.getBumiFlg() == 1) {
				getManagedBean().getPersonCalc()
						.setSemiBumiEnterCnt(
								getManagedBean().getPersonCalc()
										.getSemiBumiEnterCnt() + 1);
			} else {
				getManagedBean().getPersonCalc().setSemiNonBumiEnterCnt(
						getManagedBean().getPersonCalc()
								.getSemiNonBumiEnterCnt() + 1);
			}
		} else {
			getManagedBean().getPersonCalc().setSemiFrgEnterCnt(
					getManagedBean().getPersonCalc().getSemiFrgEnterCnt() + 1);
		}
	}

	private void setProfEnteredCount(SmPersonnel per) {
		if (per.getPmCountry().getCountryCode()
				.equals(SupplierManagementConst.COUNTRY_MALAYSIA_CODE)) {
			if (per.getBumiFlg() == 1) {
				getManagedBean().getPersonCalc()
						.setProfBumiEnterCnt(
								getManagedBean().getPersonCalc()
										.getProfBumiEnterCnt() + 1);
			} else {
				getManagedBean().getPersonCalc().setProfNonBumiEnterCnt(
						getManagedBean().getPersonCalc()
								.getProfNonBumiEnterCnt() + 1);
			}
		} else {
			getManagedBean().getPersonCalc().setProfFrgEnterCnt(
					getManagedBean().getPersonCalc().getProfFrgEnterCnt() + 1);
		}
	}

	private void setHigherMngmtCount(SmPersonnel per) {
		if (per.getPmCountry().getCountryCode()
				.equals(SupplierManagementConst.COUNTRY_MALAYSIA_CODE)) {
			if (per.getBumiFlg() == 1) {
				getManagedBean().getPersonCalc()
						.setHghMngBumiCnt(
								getManagedBean().getPersonCalc()
										.getHghMngBumiCnt() + 1);
			} else {
				getManagedBean().getPersonCalc()
						.setHghMngNonBumiCnt(
								getManagedBean().getPersonCalc()
										.getHghMngNonBumiCnt() + 1);
			}
		} else {
			getManagedBean().getPersonCalc().setHghMngFrgCnt(
					getManagedBean().getPersonCalc().getHghMngFrgCnt() + 1);
		}
	}

	private void setDirectorCount(SmPersonnel per) {
		System.out.println("**** PM COOOUNTYR CODE *****"
				+ per.getPmCountry().getCountryCode());
		System.out.println("******* FROM CONST ***********"
				+ SupplierManagementConst.COUNTRY_MALAYSIA_CODE);
		if (per.getPmCountry().getCountryCode()
				.equals(SupplierManagementConst.COUNTRY_MALAYSIA_CODE)) {
			if (per.getBumiFlg() == 1) {
				getManagedBean().getPersonCalc().setBodBumiCnt(
						getManagedBean().getPersonCalc().getBodBumiCnt() + 1);
			} else {
				getManagedBean().getPersonCalc()
						.setBodNonBumiCnt(
								getManagedBean().getPersonCalc()
										.getBodNonBumiCnt() + 1);
			}
		} else {
			getManagedBean().getPersonCalc().setBodFrgCnt(
					getManagedBean().getPersonCalc().getBodFrgCnt() + 1);
		}

	}

	private void resetAllPersonnelCount() {
		getManagedBean().setPersonCalc(new SmPersonnleCalcVO());
		assignEnteredStaffValues();
	}
}

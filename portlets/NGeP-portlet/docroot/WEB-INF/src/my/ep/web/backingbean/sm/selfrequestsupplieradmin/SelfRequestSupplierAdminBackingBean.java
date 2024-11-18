package my.ep.web.backingbean.sm.selfrequestsupplieradmin;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;

import my.ep.constant.MessageConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.dto.sm.common.ssm.SsmReqDTO;
import my.ep.dto.sm.common.ssm.SsmResDTO;
import my.ep.dto.sm.selfrequestsupplieradmin.SelfRequestSupplierAdminReqDTO;
import my.ep.exception.EpException;
import my.ep.helper.EntityHelper;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmCountry;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmDivision;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.sm.SmAddress;
import my.ep.persistence.entity.sm.SmMofAccount;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmPersonnelAddress;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.type.pm.RoleCode;
import my.ep.util.MessageUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.sm.selfrequestsupplieradmin.SelfRequestSupplierAdminManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.pm.helper.ParameterHelper;
import my.ep.web.util.sm.helper.SmApplicationUtil;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.util.sm.helper.SmServiceLookup;
import my.ep.web.validator.common.BusinessRegNoValidator;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;

@ManagedBean(name = "selfRequestSupplierAdminBackingBean")
@RequestScoped
public class SelfRequestSupplierAdminBackingBean extends
		BaseBackingBean<SelfRequestSupplierAdminManagedBean> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4774616656344235966L;
	private static Logger log = Logger
			.getLogger(SelfRequestSupplierAdminBackingBean.class);

	private static final String COMPANY_VALIDATION_PAGE = "1";
	private static final String SUPP_ADMIN_PERSONNEL_PAGE = "2";
	private static final String SUPP_ADMIN_DETAILS_PAGE = "3";

	public void preRender() {
		renewSesionObject();
		getManagedBean().setCurrDate(new Date());
		SmCommonUtil
				.redirectToPage(
						"/xhtml/sm/selfRequestSupplierAdmin/selfRequestSuppAdminTnc.xhtml",
						"", null, null);
	}

	public void navigateToCompanyValidation() {
		getManagedBean().setPage(COMPANY_VALIDATION_PAGE);
		getManagedBean().setSupplier(new SmSupplier());
		SmCommonUtil
				.redirectToPage(
						"/xhtml/sm/selfRequestSupplierAdmin/selfRequestSuppAdminBasePage.xhtml",
						null, null, null);
	}

	public void navigateToSuppAdmin() {
		this.getManagedBean().setErrorInvalidSelfRequest(false);
		if (StringUtils.isBlank(getManagedBean().getMofNo())) {
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.addMessage(
					"validationMsg",
					new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
							SmCommonUtil.getMessage("lbl.sm.required.msg",
									new String[] { SmCommonUtil.getMessage(
											"lbl.sm.selfrequest.mof.number",
											null) })));
		} else {
			List<SmPersonnel> filteredPersonnels = new ArrayList<SmPersonnel>();
			SmPersonnel tmp = null;
			
			//START SSM INTEGRATION
			if(SmCommonUtil.getInstance().ssmActive() && this.getManagedBean().getSupplier()!= null && businessTypeTovalidateWithSSM(this.getManagedBean().getSupplier().getRegNo(),this.getManagedBean().getSupplier().getBusinessType())){
				BusinessRegNoValidator validate = new BusinessRegNoValidator();
				validate.validateRegNo(this.getManagedBean().getSupplier().getBusinessType(), this.getManagedBean().getSupplier().getRegNo(),true);
				FacesContext fc = FacesContext.getCurrentInstance();
				SsmReqDTO ssmReqDTO = new SsmReqDTO();
				ssmReqDTO.setRegNo(this.getManagedBean().getSupplier().getRegNo());
				ssmReqDTO.setBusinessType(this.getManagedBean().getSupplier().getBusinessType());
				ssmReqDTO.setCheckOnly(true);				
				SsmResDTO ssmResDTO = SmCommonUtil.getInstance().validateWithSSM(ssmReqDTO);
				//27382
				this.getManagedBean().setStatusFromSSM("");
				if(ssmResDTO.getSeverity().equalsIgnoreCase("SUCC")){
					//check the business/company registration status in ssm
					this.getManagedBean().setStatusDescFromSSM(setStatusMessageFromSSM(ssmResDTO.getStatusFromSSM()));
					this.getManagedBean().setStatusFromSSM(ssmResDTO.getStatusFromSSM());
					if(!ssmResDTO.getStatusFromSSM().equalsIgnoreCase("A") && !ssmResDTO.getStatusFromSSM().equalsIgnoreCase("E")){
						this.getManagedBean().setAutoUpdate(false);
						this.getManagedBean().setCompanyValidWithSSM(true);
						//27382 - start
						if(ssmResDTO.getStatusFromSSM().equalsIgnoreCase("L")){
							/*fc.addMessage("ssmInvalid",
									new FacesMessage(FacesMessage.SEVERITY_ERROR, SmCommonUtil.getOutputMsgBundle(MessageConst.ERROR_721097, new String[]{SmCommonUtil.getOutputMsgBundle(this.getManagedBean().getStatusDescFromSSM(),null), ""}),
											""));*/
							throw new EpException(MessageConst.ERROR_721097,new String[]{SmCommonUtil.getMessage(this.getManagedBean().getStatusDescFromSSM(),null), ""});
						}//27382 - Terminated 
						if(ssmResDTO.getStatusFromSSM().equalsIgnoreCase("T")){
							/*fc.addMessage("ssmInvalid",
									new FacesMessage(FacesMessage.SEVERITY_ERROR, SmCommonUtil.getOutputMsgBundle(MessageConst.ERROR_721098, new String[]{SmCommonUtil.getOutputMsgBundle(this.getManagedBean().getStatusDescFromSSM(),null), ""}),
											""));*/
							throw new EpException(MessageConst.ERROR_721098,new String[]{SmCommonUtil.getMessage(this.getManagedBean().getStatusDescFromSSM(),null), ""});
						}//27382 - Dissolved Conversion to LLP
						if(ssmResDTO.getStatusFromSSM().equalsIgnoreCase("B")){
							if(businessTypeIsROC(this.getManagedBean().getSupplier().getBusinessType())){
								/*fc.addMessage("ssmInvalid",
										new FacesMessage(FacesMessage.SEVERITY_ERROR, SmCommonUtil.getOutputMsgBundle(MessageConst.ERROR_721070, new String[]{SmCommonUtil.getMessage("lbl.sm.status.b", null), ""}),
												""));*/
								throw new EpException(MessageConst.ERROR_721070,new String[]{SmCommonUtil.getMessage("lbl.sm.status.b", null),"" });
									}
							if(businessTypeIsROB(this.getManagedBean().getSupplier().getBusinessType())){
								/*fc.addMessage("ssmInvalid",
										new FacesMessage(FacesMessage.SEVERITY_ERROR, SmCommonUtil.getOutputMsgBundle(MessageConst.ERROR_721070, new String[]{SmCommonUtil.getMessage("lbl.sm.status.dissolved", null), ""}),
												""));*/
								throw new EpException(MessageConst.ERROR_721070,new String[]{SmCommonUtil.getMessage("lbl.sm.status.dissolved", null),""});
									}
						}else{ 
						/*fc.addMessage("ssmInvalid",
								new FacesMessage(FacesMessage.SEVERITY_ERROR, SmCommonUtil.getOutputMsgBundle(MessageConst.ERROR_721070, new String[]{SmCommonUtil.getOutputMsgBundle(this.getManagedBean().getStatusDescFromSSM(),null), ""}),
										""));*/
						throw new EpException(MessageConst.ERROR_721070,new String[]{SmCommonUtil.getOutputMsgBundle(this.getManagedBean().getStatusDescFromSSM(),null), ""});
						}
					}//27382 - start
				} else{
					if("70001".equalsIgnoreCase(ssmResDTO.getStatusCode())){
						/*fc.addMessage("ssmInvalid",
								new FacesMessage(FacesMessage.SEVERITY_ERROR, SmCommonUtil.getOutputMsgBundle(MessageConst.ERROR_721081, new String[]{SmCommonUtil.getOutputMsgBundle(this.getManagedBean().getStatusDescFromSSM(),null), ""}),
										""));*/
						throw new EpException(MessageConst.ERROR_721081,new String[]{SmCommonUtil.getOutputMsgBundle(this.getManagedBean().getStatusDescFromSSM(),null), ""});
					}else{
						//SSM CHANGES START #27541 - 10/03/2020
						try{
							SmCommonUtil.ssmError(ssmResDTO);
						}catch(Exception e){
							String message = MessageUtils.getMessage(e.getMessage(),MessageUtils.BUNDLE_COMMON_MESSAGES,new FacesScopes().getUserLocale());
							/*fc.addMessage("ssmInvalid",
									new FacesMessage(FacesMessage.SEVERITY_ERROR, SmCommonUtil.getMessage(message,null),
											""));*/

						if (e.getMessage().toString() == "ERR-721078"){
								this.getManagedBean().setErrorInvalidSelfRequest(true);
								throw new EpException(MessageConst.ERROR_721078);
							}
							if (message != null) {
								throw new EpException(message + "(ERR-"
										+ e.getMessage().toString() + ")");
							} else {
								throw new EpException(e.getMessage() + "(ERR-729999)");
							}
						}
						//SSM CHANGES END #27541 - 10/03/2020
					}
				}
			}
			//END SSM INTEGRATION
			
			if (getManagedBean().getSupplier() != null
					&& getManagedBean().getSupplier().getSmAppl() != null
					&& !EntityHelper.isCollectionEmpty(getManagedBean()
							.getSupplier().getSmAppl().getSmPersonnels())) {
				for (SmPersonnel per : getManagedBean().getSupplier()
						.getSmAppl().getSmPersonnels()) {
					if (per.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE
							&& per.getPmUser() == null
							&& StringUtils.isBlank(per.getEpRole())
							&& per.getIdentificationTypeId() != null
							&& per.getIdentificationType() != null) {

						tmp = (SmPersonnel) EntityHelper
								.getEntityBySinglePropertyFromList(
										per.getIdentificationNo(),
										filteredPersonnels, "identificationNo");

						if (tmp == null) {
							filteredPersonnels.add(per);
						} else {
							if (per.getRevNo() > tmp.getRevNo()) {
								filteredPersonnels.remove(tmp);
								filteredPersonnels.add(per);
							}
						}
					}
				}
			}

			Collections.sort(filteredPersonnels, new Comparator<SmPersonnel>() {
				@Override
				public int compare(final SmPersonnel s1, final SmPersonnel s2) {
					return (s1.getName()).compareTo(s2.getName());
				}
			});
			SmCommonUtil.setExternalContextSessionMap(
					SupplierManagementConst.KEY_MAP_PERSONNEL_LIST,
					filteredPersonnels);
			getManagedBean().setPage(SUPP_ADMIN_PERSONNEL_PAGE);
		}
	}

	public void back(String page) {
		getManagedBean().setPage(page);
	}

	public void navigateToSuppAdminDetails() {
		FacesContext fc = FacesContext.getCurrentInstance();
		String invalidIcMsg = getInValidIcErrMsg(getManagedBean().getIcNo());
		String invalidNameMsg = getInValidAdminNameErrMsg();
		if (StringUtils.isBlank(invalidIcMsg)
				&& StringUtils.isBlank(invalidNameMsg)) {
			getManagedBean().setPage(SUPP_ADMIN_DETAILS_PAGE);
			getManagedBean().setCountryItems(SmCommonUtil.getCountryList());
			getManagedBean().setIdentificationTypeItems(
					SmCommonUtil.getInstance().getPmIdTypeDropdownValues());
			getManagedBean().setMyCountryId(SmCommonUtil.getMyCountryId());
			SmPersonnelAddress permAddr = (SmPersonnelAddress) EntityHelper
					.getEntityBySinglePropertyFromList(
							SupplierManagementConst.SM_PERSONNEL_ADDR_TYPE_PERM,
							getManagedBean().getSelectedAdmin()
									.getSmPersonnelAddresses(), "addressType");
			if (permAddr == null) {
				permAddr = new SmPersonnelAddress();
				permAddr.setAddressType(SupplierManagementConst.SM_PERSONNEL_ADDR_TYPE_PERM);
				permAddr.setSmAddress(SmApplicationUtil.setDefaultAddr(null));
				permAddr.setSmPersonnel(getManagedBean().getSelectedAdmin());
				if (getManagedBean().getSelectedAdmin()
						.getSmPersonnelAddresses() == null) {
					getManagedBean().getSelectedAdmin()
							.setSmPersonnelAddresses(
									new ArrayList<SmPersonnelAddress>());
				}
				getManagedBean().getSelectedAdmin().getSmPersonnelAddresses()
						.add(permAddr);
			}
			getManagedBean().setSelectedAdminPermAddr(permAddr);
			//#34159 - START
			onChangeCountry(getManagedBean().getSelectedAdminPermAddr()
					.getSmAddress(), true);
			onChangeState(getManagedBean().getSelectedAdminPermAddr()
					.getSmAddress(), true);
			if(getManagedBean().getSelectedAdminPermAddr().getSmAddress().getPmState().getStateCode().equalsIgnoreCase(SupplierManagementConst.SARAWAK_CODE)){
				onChangeDivision(getManagedBean().getSelectedAdminPermAddr().getSmAddress(), true);
			}
			onChangeDistrict(getManagedBean().getSelectedAdminPermAddr()
					.getSmAddress(), true);
			onChangeCity(getManagedBean().getSelectedAdminPermAddr()
					.getSmAddress(), true);
			//#34159 - END
		} else {
			if (!StringUtils.isBlank(invalidIcMsg)) {
				fc.addMessage("icNoId", new FacesMessage(
						FacesMessage.SEVERITY_ERROR, null, invalidIcMsg));
			}
			if (!StringUtils.isBlank(invalidNameMsg)) {
				fc.addMessage("suppAdminNameId", new FacesMessage(
						FacesMessage.SEVERITY_ERROR, null, invalidNameMsg));
			}
		}
	}

	public void submit() {
		
		validateMyIdentity();

		SmPersonnelAddress permAddr = (SmPersonnelAddress) EntityHelper
				.getEntityBySinglePropertyFromList(
						SupplierManagementConst.SM_PERSONNEL_ADDR_TYPE_PERM,
						getManagedBean().getSelectedAdmin()
								.getSmPersonnelAddresses(), "addressType");

		// new permanent addresses added
		if (permAddr == null) {
			getManagedBean().getSelectedAdmin().setSmPersonnelAddresses(
					new ArrayList<SmPersonnelAddress>());
			getManagedBean().getSelectedAdmin().getSmPersonnelAddresses()
					.add(getManagedBean().getSelectedAdminPermAddr());
		}

		boolean isSoftcertCatlogueReqd = false;
		int mofStatus = getManagedBean().getSupplier().getSmMofAccounts()
				.get(0).getRecordStatusFlg();
		if (getManagedBean().getSupplier().getSmAppl().getSupplierType()
				.equalsIgnoreCase(SupplierManagementConst.SUPP_TYPE_CONTRACTOR)
				&& getManagedBean().getSupplier().getSmAppl()
						.getSmCompanyBasic() != null
				&& getManagedBean().getSupplier().getSmAppl()
						.getSmCompanyBasic().isWithFederal()) {
			isSoftcertCatlogueReqd = true;
			if (getManagedBean().getSelectedAdmin().getSoftcertFlg() == 0) {
				getManagedBean().getSelectedAdmin().setSoftcertFlg(
						SupplierManagementConst.SOFT_CERT_PENDING_REQ);
			}
			getManagedBean().getSelectedAdmin().setEpRole(
					RoleCode.SUPPLIER_TEMP.value());
		} else {
			// #30053 - Start
			if(getManagedBean().getSupplier().getSmAppl().getSupplierType().equalsIgnoreCase(SupplierManagementConst.SUPP_TYPE_CONSULTANT)){
				getManagedBean().getSelectedAdmin().setEpRole(RoleCode.MOF_SUPPLIER_ADMIN.value());
			}else{
				if (mofStatus == DbValueConst.RECORD_STATUS_ACTIVE) {
					getManagedBean().getSelectedAdmin().setEpRole(
							RoleCode.MOF_SUPPLIER_ADMIN.value());
				} else if (mofStatus == DbValueConst.RECORD_STATUS_EXPIRED) {
					getManagedBean().getSelectedAdmin().setEpRole(
							RoleCode.BASIC_SUPPLIER_ADMIN.value());
				}
			}
			// #30053 - End
			getManagedBean().getSelectedAdmin().setSoftcertFlg(0);
		}

		SelfRequestSupplierAdminReqDTO reqDTO = new SelfRequestSupplierAdminReqDTO();
		reqDTO.setSoftcertCatalogueReqd(isSoftcertCatlogueReqd);
		reqDTO.setSupplierAdmin(getManagedBean().getSelectedAdmin());
		reqDTO.setLanguage(new FacesScopes().getUserLocale().getLanguage());
		reqDTO.setBaseUrl(SmCommonUtil.getBasePortalUrl());
		reqDTO.setMofStatus(mofStatus);

		SmServiceLookup.getSelfRequestSupplierAdminSO().submitSelfRequestAdmin(
				reqDTO);

		//#34159 - START
		if (permAddr != null && permAddr.getAddressType().equalsIgnoreCase(SupplierManagementConst.SM_PERSONNEL_ADDR_TYPE_PERM)
				&& !permAddr.getSmAddress().getPmState().getStateCode().equalsIgnoreCase(SupplierManagementConst.SARAWAK_CODE)
				&& permAddr.getSmAddress().getPmDivision() != null && permAddr.getSmAddress().getPmDivision().getDivisionId() != null) {
			SmAddress addr = SmServiceLookup.getSmAddressSO().retrieveSmAddressByAddressId(permAddr.getSmAddress().getAddressId());
			addr.getPmDivision().setDivisionId(null);
			SmServiceLookup.getSmAddressSO().updatePmDivisionByAddressId(addr.getPmDivision(), addr.getAddressId());
		}
		//#34159 - END
				
		FacesContext.getCurrentInstance().getExternalContext().getSessionMap()
				.clear();

		SmCommonUtil.redirectToPage(
				SmApplicationUtil.getApplicationErrorPage(),
				MessageConst.INFO_721227, new String[] { getManagedBean()
						.getSupplier().getCompanyName() }, null);

	}
	
	/**
	 * Validate my identity
	 * 
	 * @param SmPersonnel
	 *            personnel
	 */
	private void validateMyIdentity() {
		if (getManagedBean().getSelectedAdmin()!= null
				&& getManagedBean().getSelectedAdmin().getName()!= null
				&& getManagedBean().getSelectedAdmin().getIdentificationNo()!= null
				&& getManagedBean().getSelectedAdminPermAddr().getSmAddress()!= null){
			PmParameter idType = SmApplicationUtil
					.retrievePmParameterById(getManagedBean().getSelectedAdmin().getIdentificationTypeId());

			if (idType.getParameterCode()
					.equals(SupplierManagementConst.NEW_IC)) {
				SmCommonUtil.getInstance().verifyWithMyIdentity(
						getManagedBean().getSelectedAdmin().getName(),
						getManagedBean().getSelectedAdmin().getIdentificationNo(),
						getManagedBean().getSelectedAdminPermAddr().getSmAddress()
								.getPostcode(), null);
			}
		}
	}
	
	public List<SmPersonnel> autoCompletePersonnels(String query) {
		return SmCommonUtil.getFilteredPersonnelsInSession(query);
	}

	public void onBlurMofNo(String mofNo) {
		FacesContext fc = FacesContext.getCurrentInstance();
		StringBuilder fullMsg = new StringBuilder();
		getManagedBean().setDisabledNextBtn(false);
		//27382
		this.getManagedBean().setErrorInvalidSelfRequest(false);
		this.getManagedBean().setStatusFromSSM("");
		if (StringUtils.isBlank(mofNo)) {
			this.getManagedBean().setErrorInvalidSelfRequest(false);
			getManagedBean().setDisabledNextBtn(true);
			fullMsg.append(SmCommonUtil.getMessage("lbl.sm.required.msg",
					new String[] { SmCommonUtil.getMessage(
							"lbl.sm.selfrequest.mof.number", null) }));
			fc.addMessage("validationMsg", new FacesMessage(
					FacesMessage.SEVERITY_ERROR, null, fullMsg.toString()));
		} else {
			String mofStatusMsg = "lbl.sm.selfrequest.msg.mofstatus.notvalid.expired";
			String suppAdminStatusMsg = "lbl.sm.selfrequest.msg.suppadmin.not.appointed";
			String suppAdminName = SmCommonUtil.getMessage(
					"lbl.sm.selfrequest.msg.suppadmin.none", null);
			SelfRequestSupplierAdminReqDTO reqDTO = new SelfRequestSupplierAdminReqDTO();
			reqDTO.setMofNo(getManagedBean().getMofNo());
			getManagedBean().setSupplier(
					SmServiceLookup.getSelfRequestSupplierAdminSO()
							.retrieveSupplierByMofNo(reqDTO).getSupplier());

			// set full alert message
			fullMsg.append(
					SmCommonUtil.getMessage("lbl.sm.selfrequest.msg.alert",
							null)).append("<br />");
			fullMsg.append(
					SmCommonUtil.getMessage(
							"lbl.sm.selfrequest.msg.status.as.below",
							new String[] { getManagedBean().getMofNo() }))
					.append("<br />");

			// mof no. entered is not exist
			if (getManagedBean().getSupplier() == null
					|| EntityHelper.isCollectionEmpty(getManagedBean()
							.getSupplier().getSmMofAccounts())) {
				getManagedBean().setDisabledNextBtn(true);
				fullMsg.append(
						SmCommonUtil.getMessage(
								"lbl.sm.selfrequest.msg.mofstatus",
								new String[] { SmCommonUtil.getMessage(
										mofStatusMsg, null) }))
						.append("<br />");
				fullMsg.append(SmCommonUtil.getMessage(
						"lbl.sm.selfrequest.msg.click.newreg", null));
				fc.addMessage("mofNoInvalid",
						new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
								SmCommonUtil.getMessage(mofStatusMsg, null)));
			} else {
				// set mof account message status
				//#29612-start
				SmMofAccount mofAcc = null;
				for (SmMofAccount acc : getManagedBean().getSupplier().getSmMofAccounts()) {
					if (acc.getRecordStatusFlg().equals(DbValueConst.RECORD_STATUS_ACTIVE)) {
						mofAcc = acc; 
						break;
					}
				}
				if (mofAcc == null) {
					mofAcc = getManagedBean().getSupplier().getSmMofAccounts().get(0);
				}//#29612-end
			
				if ((mofAcc.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE 
						|| mofAcc.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_EXPIRED)
						&& mofAcc.getExpDate() != null) {
					Calendar c = Calendar.getInstance();
					c.setTime(mofAcc.getExpDate());
					c.add(Calendar.YEAR, 1);
					Date expiryDate = c.getTime();					
					// check if expire date less than a year
					if (expiryDate.after(new Date())) {
						mofStatusMsg = "lbl.sm.selfrequest.msg.mofstatus.valid";
					}
				}

				// set supplier admin status
				if (getManagedBean().getSupplier().getSmAppl() != null
						&& !EntityHelper.isCollectionEmpty(getManagedBean()
								.getSupplier().getSmAppl().getSmPersonnels())) {
					List<SmPersonnel> personnels = getManagedBean()
							.getSupplier().getSmAppl().getSmPersonnels();

					for (SmPersonnel per : personnels) {
						if ((per.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE || per
								.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_PENDING_ACTIVATION)
								&& (!StringUtils.isBlank(per.getEpRole()) && (my.ep.constant.sm.SupplierManagementConst.SUPPLIER_ADMIN_ROLES
										.contains(per.getEpRole())))) {
							suppAdminStatusMsg = "lbl.sm.selfrequest.msg.suppadmin.appointed";
							suppAdminName = per.getName();
						}
					}
				}

				fullMsg.append(
						SmCommonUtil.getMessage(
								"lbl.sm.selfrequest.msg.mofstatus",
								new String[] { SmCommonUtil.getMessage(
										mofStatusMsg, null) }))
						.append("<br />");

				// check if mof status is not valid
				if (mofStatusMsg
						.equals("lbl.sm.selfrequest.msg.mofstatus.notvalid.expired")) {
					getManagedBean().setDisabledNextBtn(true);
					fullMsg.append(SmCommonUtil.getMessage(
							"lbl.sm.selfrequest.msg.click.newreg", null));
				}
				// if mof status is valid
				else {
					fullMsg.append(
							SmCommonUtil.getMessage(
									"lbl.sm.selfrequest.msg.suppadmin.status",
									new String[] { SmCommonUtil.getMessage(
											suppAdminStatusMsg, null) }))
							.append("<br />");
					fullMsg.append(
							SmCommonUtil
									.getMessage(
											"lbl.sm.selfrequest.msg.suppadmin.name.appointed",
											new String[] { suppAdminName }))
							.append("<br />");

					// if supp admin not assigned
					if (suppAdminStatusMsg
							.equals("lbl.sm.selfrequest.msg.suppadmin.not.appointed")) {
						fullMsg.append(SmCommonUtil.getMessage(
								"lbl.sm.selfrequest.msg.click.proceed", null));
					}

					// if supp admin already assigned
					else {
						getManagedBean().setDisabledNextBtn(true);
						fullMsg.append(SmCommonUtil.getMessage(
								"lbl.sm.selfrequest.msg.click.login",
								new String[] { SmCommonUtil
										.getPortalLoginPage() }));
					}
				}
			}

			fc.addMessage("validationMsg", new FacesMessage(
					FacesMessage.SEVERITY_INFO, null, fullMsg.toString()));
		}

	}

	public void redirectToMainPage() {
		String url = SmCommonUtil.getBasePortalUrl();
		url = StringUtils.isBlank(url) ? SmCommonUtil
				.getBasePortalAppUrlFromProperties() : url;
		SmCommonUtil.redirectToExternalUrl(url);
	}

	/**
	 * Custom validation for ic no
	 * 
	 * @param FacesContext
	 *            facesContext
	 * @param UIComponent
	 *            uiComponent
	 * @param Object
	 *            value
	 */
	public void onBlurIcNo(String icNo) {
		FacesContext fc = FacesContext.getCurrentInstance();
		String errMsg = getInValidIcErrMsg(icNo);

		if (!StringUtils.isBlank(errMsg)) {
			fc.addMessage("icNoId", new FacesMessage(
					FacesMessage.SEVERITY_ERROR, null, errMsg));
		} else {
			errMsg = getInValidAdminNameErrMsg();
			if (!StringUtils.isBlank(errMsg)) {
				fc.addMessage("suppAdminNameId", new FacesMessage(
						FacesMessage.SEVERITY_ERROR, null, errMsg));
			}
		}

	}

	//#34159 - Refactor this method
	public void onChangeCountry(SmAddress addr, boolean isPreRender) {
		Long  countryId = addr.getCountryId();
		
		if(isPreRender){ 
			countryId = addr.getCountryId() == null ? addr.getPmCountry().getCountryId() : addr.getCountryId();
		} 
	
		if(countryId != null){
			addr.setPmCountry(SmCommonUtil.getInstance().getCountryById(countryId));
			getManagedBean().setStateItems(SmCommonUtil.getStateList(countryId));
		} else {
			addr.setPmCountry(new PmCountry());
			getManagedBean().setStateItems(new ArrayList<SelectItem>());
		}
		
		getManagedBean().setDivisionItems(new ArrayList<SelectItem>());
		getManagedBean().setDistrictItems(new ArrayList<SelectItem>());
		getManagedBean().setCityItems(new ArrayList<SelectItem>());
		addr.setCountryId(countryId);
		addr.setStateId(null);
		addr.setDivisionId(null);
		addr.setDistrictId(null);
		addr.setCityId(null);
		
		if(!isPreRender){ 
			addr.setPmState(new PmState());
			//addr.setPmDivision(new PmDivision());
			addr.setPmDistrict(new PmDistrict());
			addr.setPmCity(new PmCity());
		}
	}

	//#34159 - Refactor this method
	public void onChangeState(SmAddress addr, boolean isPreRender) {
		Long stateId = addr.getStateId();
		
		//PR - preRender
		if(isPreRender){
			stateId = addr.getStateId() == null ? addr.getPmState().getStateId() : addr.getStateId();
		} 
		
		if(stateId != null){
			addr.setPmState(SmCommonUtil.getInstance().getStateById(stateId));
			if(addr.getPmState().getStateCode().equals(SupplierManagementConst.SARAWAK_CODE)){
				getManagedBean().setDivisionItems(SmCommonUtil.getDivisionList(stateId));
				getManagedBean().setDistrictItems(new ArrayList<SelectItem>());
			} else {
				getManagedBean().setDistrictItems(SmCommonUtil.getDistrictList(stateId));
			}
		} else {
			addr.setPmState(new PmState());
			getManagedBean().setDivisionItems(new ArrayList<SelectItem>());
			getManagedBean().setDistrictItems(new ArrayList<SelectItem>());
		}
		
		getManagedBean().setCityItems(new ArrayList<SelectItem>());
		addr.setStateId(stateId);
		addr.setDivisionId(null);
		addr.setDistrictId(null);
		addr.setCityId(null);
		
		if(!isPreRender){
			//addr.setPmDivision(new PmDivision());
			addr.setPmDistrict(new PmDistrict());
			addr.setPmCity(new PmCity());
		}
	}

	//#34159 - Refactor this method
	public void onChangeDistrict(SmAddress addr, boolean isPreRender) {
		Long districtId = addr.getDistrictId();
		
		//PR - preRender
		if(isPreRender){
			districtId = addr.getDistrictId() == null ? addr.getPmDistrict().getDistrictId() : addr.getDistrictId();
		}
		
		if(districtId != null){
			addr.setPmDistrict(SmCommonUtil.getInstance().getDistrictById(districtId));
			this.getManagedBean().setCityItems(SmCommonUtil.getCityList(districtId));
		} else {
			addr.setPmDistrict(new PmDistrict());
			this.getManagedBean().setCityItems(new ArrayList<SelectItem>());
		}
		addr.setDistrictId(districtId);
		addr.setCityId(null);
		
		//PR - preRender
		if(!isPreRender){
			addr.setPmCity(new PmCity());
		}
	}
	
	//13320-Start
	//#34159 - Refactor this method
	public void onChangeDivision(SmAddress addr, boolean isPreRender) {
		Long divisionId = addr.getDivisionId();
		
		if(isPreRender){
			divisionId = addr.getDivisionId() == null ? addr.getPmDivision().getDivisionId() : addr.getDivisionId();
		}
		if(divisionId != null){
			addr.setPmDivision(SmCommonUtil.getInstance().getDivisionByUsingId(divisionId));
			getManagedBean().setDistrictItems(SmCommonUtil.getDistrictListByID(divisionId));
		} else {
			addr.setPmDivision(new PmDivision());
			getManagedBean().setDistrictItems(new ArrayList<SelectItem>());
		}
		getManagedBean().setCityItems(new ArrayList<SelectItem>());
		addr.setDivisionId(divisionId);
		addr.setDistrictId(null);
		addr.setCityId(null);
		
		if(!isPreRender){
			addr.setPmDistrict(new PmDistrict());
			addr.setPmCity(new PmCity());
		}
	}
	//13320-End

	//#34159 - Refactor this method
	public void onChangeCity(SmAddress addr, boolean isPreRender) {
		Long cityId = addr.getCityId();
		
		if(isPreRender){
			cityId = addr.getCityId() == null ? addr.getPmCity().getCityId() : addr.getCityId();
		}
		if(cityId != null){
			addr.setPmCity(SmCommonUtil.getInstance().getCityById(cityId));
		} else {
			addr.setPmCity(new PmCity());
		}
		addr.setCityId(cityId);
	}

	public void searchSuppAdmin() {
		RequestContext.getCurrentInstance().execute(
				"searchSuppAdminDialog.show();");
	}

	/**
	 * Assign selected personnel identity type.
	 * 
	 * @param e
	 *            - AjaxBehaviorEvent
	 */
	public void assignPersonIdentType(AjaxBehaviorEvent e) {
		SmApplicationUtil
				.setSelectedPersonnelIdentificationType(getManagedBean()
						.getSelectedAdmin());
	}

	private String getInValidIcErrMsg(String icNo) {
		String labelNameVal = SmCommonUtil.getMessage("lbl.sm.person.icNum",
				null);
		String errMsg = StringUtils.EMPTY;

		if (!StringUtils.isBlank(icNo)
				&& getManagedBean().getSupplier() != null
				&& getManagedBean().getSupplier().getSmAppl() != null
				&& !EntityHelper.isCollectionEmpty(getManagedBean()
						.getSupplier().getSmAppl().getSmPersonnels())) {
			SmPersonnel per = (SmPersonnel) EntityHelper
					.getEntityBySinglePropertyFromList(icNo, getManagedBean()
							.getSupplier().getSmAppl().getSmPersonnels(),
							"identificationNo");

			if (per == null) {
				errMsg = SmCommonUtil.getMessage(
						"lbl.sm.selfrequest.icno.invalid", null);
			} else {
				if (getManagedBean().getSelectedAdmin() != null
						&& !per.getIdentificationNo().equals(
								getManagedBean().getSelectedAdmin()
										.getIdentificationNo())) {
					errMsg = SmCommonUtil.getMessage(
							"lbl.sm.selfrequest.icno.invalid", null);
				}
			}

		} else {
			errMsg = SmCommonUtil.getMessage("lbl.sm.required.msg",
					new String[] { labelNameVal });
		}

		return errMsg;
	}

	private String getInValidAdminNameErrMsg() {
		String retVal = StringUtils.EMPTY;

		if (getManagedBean().getSelectedAdmin() == null) {
			retVal = SmCommonUtil.getMessage("lbl.sm.required.msg",
					new String[] { SmCommonUtil.getMessage(
							"lbl.sm.selfrequest.suppadmin.name", null) });
		} else {
			if (getManagedBean().getSupplier() != null
					&& getManagedBean().getSupplier().getSmAppl() != null
					&& !EntityHelper.isCollectionEmpty(getManagedBean()
							.getSupplier().getSmAppl().getSmPersonnels())) {
				SmPersonnel personnel = (SmPersonnel) EntityHelper
						.getEntityBySinglePropertyFromList(getManagedBean()
								.getSelectedAdmin().getName(), getManagedBean()
								.getSupplier().getSmAppl().getSmPersonnels(),
								"name");
				if (personnel == null) {
					retVal = SmCommonUtil.getMessage(
							"lbl.sm.selfrequest.suppadmin.name.invalid", null);
				}
			}
		}
		return retVal;
	}

	/**
	 * Validate ID format
	 * 
	 * @param identNo
	 * @param identType
	 */
	public void validateIDFormat(String identNo, String identType) {
		if (identType.equals(SupplierManagementConst.BASIC_IND_IDENT_TYPE)) {
			if (identNo.length() != 12) {
				throw new EpException(MessageConst.ERROR_721013);
			}

			if (!identNo.matches("[0-9]+")) {
				throw new EpException(MessageConst.ERROR_721013);
			}

		}
	}
	// #28720 Start
	public void onChangeEmail()
	{
		// managedBean.selectedAdmin.email
		String email = this.getManagedBean().getSelectedAdmin().getEmail().toLowerCase();
		if(!isAllowedEmail(email))
		{
			this.getManagedBean().getSelectedAdmin().setEmail("");
			RequestContext.getCurrentInstance().execute(
					"emailPopUpWidgetVar.show()");
		}
	}
	// #28720 End

}

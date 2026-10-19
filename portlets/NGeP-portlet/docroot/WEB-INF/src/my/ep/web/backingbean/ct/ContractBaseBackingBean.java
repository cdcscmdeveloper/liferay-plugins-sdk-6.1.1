/**
 * Copyright � 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.ct;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;//34442
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.service.ct.actor.ICtActorSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.constant.MessageConst; //#30737
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.ct.ContractStatusConst;//34442
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constants.CTProcessNameConst; //#30737
import my.ep.dto.ct.actor.CtActorReqDTO;
import my.ep.dto.ct.actor.CtActorResDTO;
import my.ep.dto.pm.authorization.user.InactiveUserReqDTO; // #30737
import my.ep.dto.pm.authorization.user.InactiveUserResDTO; // #30737
import my.ep.dto.pm.authorization.user.UserStatusCheckDTO; // #30737
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.exception.EpException; //#30737
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.ct.CtActor;
import my.ep.persistence.entity.ct.CtContract; //#30737
import my.ep.persistence.entity.ct.CtContractVer; //#30737
import my.ep.persistence.entity.ct.dtl.CtAgency;
import my.ep.persistence.entity.ct.dtl.item.CtItemCeilingQty;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.sc.loaloi.ScLoa;
import my.ep.type.pm.RoleCode;
import my.ep.util.ResourceBundleUtils;
import my.ep.util.SLAUtils;//34442
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.sc.loa.DirectLOABackingBean;
import my.ep.web.backingbean.sc.loa.LOADetailBackingBean;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.ParameterHelper;
import my.ep.web.util.pm.helper.RoleHelper; //#30737

/**
 * ContractBaseBackingBean is the Base class for contract
 * 
 * @author Nancy Ang
 * @since 19.05.2015
 * 
 */
public abstract class ContractBaseBackingBean<T extends BaseManagedBean> extends BaseBackingBean<T> {

	private static final long serialVersionUID = 1L;
	/**
	 * Logger instance.
	 */
	public static Logger log = EpLogger.getLogger(ContractBaseBackingBean.class);
	
	private static final Map<Long, String> CONFIRMATION_MSGS = buildConfirmationMsgs(); // #34442

	/**
	 * This method used to retrieve login user information.
	 */
	public FacesScopes getFacesScopes() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
	}

	/**
	 * The method used to retrieve available approver(s).
	 *
	 * @param contractType the contract type
	 * @param userMap      the user map
	 * @return sourceList  the approver list
	 */
	public List<PmUser> getAvailableApproverList(String contractType, Map<Long, PmUser> userMap) {
		IUserSO userSO = null;
		List<PmUser> sourceList = new ArrayList<PmUser>();
		UserReqDTO reqDTO = new UserReqDTO();
		UserResDTO resDTO = new UserResDTO();

		try {
			userSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);

			reqDTO.setUserId(getFacesScopes().getUserSessionBean().getUserId());				
			resDTO = userSO.retrieveUserOrgListByUserId(reqDTO);

			if (resDTO.getUserOrgList().size() > 0) {
				reqDTO.setOrgProfileId(resDTO.getUserOrgList().get(0).getPmOrgProfile().getOrgProfileId());
			}

			if (contractType.equalsIgnoreCase(
					ParameterConst.CONTRACT_TYPE_MINISTRY)) {							
				reqDTO.setRoleCode(RoleCode.CT_APPROVER.value());
			} else { // Central Contract	
				reqDTO.setRoleCode(RoleCode.BPK_CC_APPROVER.value());
			}			
			resDTO = userSO.retrieveUserListByRole(reqDTO);

			List<PmUser> list = resDTO.getUserList();			
			for (PmUser user : list) {
				sourceList.add(user);
				userMap.put(user.getUserId(), user);
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(), e.getMessage()));
		} finally {
			userSO = null;
		}
		return sourceList;
	}

	/**
	 * Sort agency list.
	 *
	 * @param ctAgencyList the agency list
	 */
	public void sortAgencyList(List<CtAgency> ctAgencyList) {
		Comparator<CtAgency> comparator = new Comparator<CtAgency>() {
			@Override
			public int compare(CtAgency obj1, CtAgency obj2) {
				String minstryName = retrieveMinistryByOrgProfileId(obj1.getOrgProfile().getOrgProfileId());
				String minstryName2 = retrieveMinistryByOrgProfileId(obj2.getOrgProfile().getOrgProfileId());
				int agency = minstryName.compareTo(minstryName2);
				if (agency != 0) {
					return agency;
				} else  {
					String ptjName = obj1.getOrgProfile().getOrgName();
					String ptjName2 = obj2.getOrgProfile().getOrgName();
					int ptj = ptjName.compareTo(ptjName2);
					if (ptj != 0) {
						return ptj;
					} else {
						Date date1 = ((CtAgency) obj1).getEffDate();
						Date date2 = ((CtAgency) obj2).getEffDate();
						int date = date1.compareTo(date2);
						if (date != 0)
							return date;
						else
							return 0;
					}
				}
			}
		};
		Collections.sort(ctAgencyList, comparator);
	}

	/**
	 * Sort item ceiling quantity list.
	 *
	 * @param ctItemCeilingQty the item ceiling quantity list
	 */
	public void sortItemCeilingQtyList(List<CtItemCeilingQty> ctItemCeilingQty) {
		Comparator<CtItemCeilingQty> comparator = new Comparator<CtItemCeilingQty>() {
			@Override
			public int compare(CtItemCeilingQty obj1, CtItemCeilingQty obj2) {				   
				if (obj1 != null) {					   
					String minstryName = retrieveMinistryByOrgProfileId(obj1.getCtAgency().getOrgProfile().getOrgProfileId());
					String minstryName2 = retrieveMinistryByOrgProfileId(obj2.getCtAgency().getOrgProfile().getOrgProfileId());
					int agency = minstryName.compareTo(minstryName2);
					if (agency != 0) {
						return agency;
					} else  {
						String ptjName =  obj1.getCtAgency().getOrgProfile().getOrgName();
						String ptjName2 = obj2.getCtAgency().getOrgProfile().getOrgName();
						int ptj = ptjName.compareTo(ptjName2);
						if (ptj != 0) {
							return ptj;
						} else {
							Date date1 = obj1.getEffDate();
							Date date2 =  obj2.getEffDate();
							return date1.compareTo(date2);
						}
					}
				} 
				return 0;
			}
		};
		Collections.sort(ctItemCeilingQty, comparator);
	}	

	/**
	 * The method used to verify the login user is supplier.
	 * 
	 * @return true, if login user is supplier
	 *         false, if login user is not supplier
	 */
	public boolean isSupplierRole() {
		return (getFacesScopes().hasRoleUser(RoleCode.BASIC_SUPPLIER_ADMIN.value())
				||getFacesScopes().hasRoleUser(RoleCode.BASIC_SUPPLIER_USER.value())
				||getFacesScopes().hasRoleUser(RoleCode.MOF_SUPPLIER_ADMIN.value())
				||getFacesScopes().hasRoleUser(RoleCode.MOF_SUPPLIER_USER.value()));
	}

	/**
	 * Check is it login user is Government Seller
	 * 
	 * @return true, if user is Governement Seller
	 */
	public boolean hasGovtSellerRole() {
		return getFacesScopes().hasRoleUser(RoleCode.GOVT_SELLER.value());
	}

	/**
	 * The method used for generating a list of fulfillment type
	 * for schedule as selection.
	 * 
	 * @return the selection list of fulfillment type
	 */
	public SelectItem[] getScheduleTypeDropDown() {
		SelectItem[] items = null;
		ISearchBean<PmParameterDesc> searchBean = null;

		try {
			List<PmParameterDesc> parameterDescs = ParameterHelper.getInstance().getParameterDescListBySearchBean(
					ParameterTypeConst.SCHEDULE_TYPE, searchBean);

			items = new SelectItem[parameterDescs.size()];
			int i = 0;
			for (PmParameterDesc parameterDesc : parameterDescs) {
				items[i++] = new SelectItem(
						parameterDesc.getPmParameter().getParameterId(), 
						parameterDesc.getCodeName());
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(), e.getMessage()));
		}
		return items;
	}

	/**
	 * Retrieve cancel confirmation message by passing in document type
	 * 
	 * @param docType the document type
	 * @return Cancel Confirmation message
	 */
	public String getCancelConfirmationContent(String docType) {
		return ResourceBundleUtils.getMessage("lbl.msg.confirm.cancel", new Object[]{getParameterDesc(ParameterTypeConst.DOCUMENT_TYPES, docType)}, ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale());
	}
	
	//34442 - start
	/**
	 * Retrieve fulfillment with bond confirmation message by passing in contractNo
	 * 
	 * @param key the statusId to find label values for use
	 * @param value the Contract Number
	 * @return Fulfillment with Bond Confirmation message
	 */
	public String getConfirmationContent(String key, String value) {
		String message = "";
		try {
			message = ResourceBundleUtils.getMessage(getConfirmationMsgs(Long.parseLong(key)), new String[] { value },
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale());
		} catch (Exception e) {
			SLAUtils.getInstance().logSLAError(ModuleCodeConst.CONTRACT_MANAGEMENT,
					"getConfirmationContent(String key, String value)", key, "ContractBaseBackingBean",
					getUserIdSessionFromLoginId(), e);
		}

		return message.replace("<br/>", System.lineSeparator());
	}
	
	/**
	 * Retrieve confirmation message
	 * 
	 * @param key the statusId to find label values for use
	 * @return Fulfillment with Bond Confirmation message
	 */
	public String getConfirmationContent(String key) {
		String message = "";

		try {
			message = ResourceBundleUtils.getMessage(getConfirmationMsgs(Long.parseLong(key)),
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale());
		} catch (Exception e) {
			SLAUtils.getInstance().logSLAError(ModuleCodeConst.CONTRACT_MANAGEMENT,
					"getConfirmationContent(String key)", key, "ContractBaseBackingBean",
					getUserIdSessionFromLoginId(), e);
		}

		return message.replace("<br/>", System.lineSeparator());
	}
	
	/**
	 * Returns the resource bundle message key for the confirmation prompt
	 * associated with the given contract status code.
	 *
	 * @param key the contract status code (see {@link ContractStatusConst})
	 * @return the message key for the confirmation text, or {@code null}
	 *         if no confirmation message is defined for the given status
	 */
	public String getConfirmationMsgs(Long key) {
		return CONFIRMATION_MSGS.get(key);
	}
	
	private static Map<Long, String> buildConfirmationMsgs() {
		Map<Long, String> confirmationMsgs = new HashMap<>();

		confirmationMsgs.put(ContractStatusConst.FD_PENDING_CONTRACT_CREATION, "lbl.ct.fd.bond.confirm");
		confirmationMsgs.put(ContractStatusConst.FD_PENDING_REVISION, "lbl.ct.fd.bond.confirm");
		confirmationMsgs.put(ContractStatusConst.FD_PENDING_APPROVAL, "lbl.ct.fd.rev.bond.confirm");
		confirmationMsgs.put(ContractStatusConst.FD_PENDING_REVISION_APPROVAL, "lbl.ct.fd.rev.bond.confirm");
		confirmationMsgs.put(ContractStatusConst.FU_PENDING_APPROVAL, "lbl.ct.fu.rev.app.confirm");
		confirmationMsgs.put(ContractStatusConst.FU_PENDING_REVISION_APPROVAL, "lbl.ct.fu.rev.app.confirm");
		confirmationMsgs.put(ContractStatusConst.NT_PENDING_APPROVAL, "lbl.ct.nt.rev.app.confirm");
		confirmationMsgs.put(ContractStatusConst.NT_PENDING_REVISION_APPROVAL, "lbl.ct.nt.rev.app.confirm");
		confirmationMsgs.put(ContractStatusConst.FW_PENDING_APPROVAL, "lbl.ct.fw.app.confirm");
		confirmationMsgs.put(ContractStatusConst.FW_PENDING_REVISION_APPROVAL, "lbl.ct.fw.app.confirm");
		confirmationMsgs.put(ContractStatusConst.AC_PENDING_AGREEMENT_APPROVAL, "lbl.ct.ac.app.confirm");
		confirmationMsgs.put(ContractStatusConst.AC_PENDING_AGREEMENT_REVISION_APPROVAL, "lbl.ct.ac.app.confirm");
		confirmationMsgs.put(ContractStatusConst.FS_PENDING_APPROVAL, "lbl.ct.fs.app.confirm");
		confirmationMsgs.put(ContractStatusConst.FS_PENDING_REVISION_APPROVAL, "lbl.ct.fs.app.confirm");
		confirmationMsgs.put(ContractStatusConst.FT_PENDING_APPROVAL, "lbl.ct.ft.app.confirm");
		confirmationMsgs.put(ContractStatusConst.FT_PENDING_REVISION_APPROVAL, "lbl.ct.ft.app.confirm");
		confirmationMsgs.put(ContractStatusConst.RA_PENDING_SUBMISSION, "lbl.ct.ra.submit.confirm");
		confirmationMsgs.put(ContractStatusConst.RA_PENDING_APPROVAL, "lbl.ct.ra.app.confirm");
		confirmationMsgs.put(ContractStatusConst.XA_PENDING_SUBMISSION, "lbl.ct.xa.submit.confirm");
		confirmationMsgs.put(ContractStatusConst.XA_PENDING_APPROVAL, "lbl.ct.xa.app.confirm");
		confirmationMsgs.put(ContractStatusConst.COM_PENDING_SUBMISSION, "lbl.ct.com.app.confirm");
		confirmationMsgs.put(ContractStatusConst.RF_PENDING_ENDORSEMENT, "lbl.ct.rf.app.confirm");
		confirmationMsgs.put(ContractStatusConst.XF_PENDING_ENDORSEMENT, "lbl.ct.xf.app.confirm");
		confirmationMsgs.put(ContractStatusConst.UF_PENDING_ENDORSEMENT, "lbl.ct.uf.app.confirm");
		confirmationMsgs.put(ContractStatusConst.FA_PENDING_LOA_ACKNOWLEDGEMENT, "lbl.ct.fa.scl.ack.confirm");
		confirmationMsgs.put(ContractStatusConst.FA_PENDING_VERIFICATION, "lbl.ct.fa.scl.verify.ack.confirm");
		confirmationMsgs.put(ContractStatusConst.SA_PENDING_SUPPLEMENTAL_AGREEMENT_APPROVAL, "lbl.ct.sa.app.confirm");
		confirmationMsgs.put(ContractStatusConst.SA_PENDING_SUPPLEMENTAL_AGREEMENT_REVISION_APPROVAL, "lbl.ct.sa.app.confirm");

		return Collections.unmodifiableMap(confirmationMsgs);
	}
	//34442 - end
	
	/*#21387 - basri 13/4/22*/
	/**
	 * Retrieve cancel confirmation message for contract request administrator by passing in document type
	 * 
	 * @param docType the document type
	 * @return Cancel Confirmation message
	 */
	public String getCancelRequestAdminConfirmationContent(String docType) {
		return ResourceBundleUtils.getMessage("lbl.ct.msg.confirm.request.cancel", new Object[]{getParameterDesc(ParameterTypeConst.DOCUMENT_TYPES, docType)}, ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale());
	}
	
	/**
	 * Retrieve reject confirmation message for contract request administrator by passing in document type
	 * 
	 * @param docType the document type
	 * @return Reject Confirmation message
	 */
	public String getRejectRequestAdminConfirmationContent(String docType) {
		return ResourceBundleUtils.getMessage("lbl.msg.confirm.reject.contractAdministrator", new Object[]{getParameterDesc(ParameterTypeConst.DOCUMENT_TYPES, docType)}, ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale());
	}
	/*#21387 - basri*/
	
	/**
	 * Method used to retrieve actor by document Id, document type and role code.
	 * 
	 * @param docId      document id
	 * @param docType    document type
	 * @param roleCode   role codde
	 * @return CtActor   an object of contract actor
	 */
	public CtActor retrieveContractActor(Long docId, String docType, String roleCode) {
		ICtActorSO ctActorSO = null;
		CtActorResDTO ctActorResDTO = new CtActorResDTO();

		try {
			ctActorSO = (ICtActorSO) BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.CONTRACT_MANAGEMENT, ICtActorSO.class);

			CtActorReqDTO ctActorReqDTO = new CtActorReqDTO();
			ctActorReqDTO.setDocId(docId);
			ctActorReqDTO.setDocType(docType);
			ctActorReqDTO.setRoleCode(roleCode);
			ctActorResDTO = ctActorSO.retrieveCtActorByDocIdAndTypeAndRole(ctActorReqDTO);
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(), e.getMessage()));
		}
		return ctActorResDTO.getCtActor();
	}

	/**
	 * The method used to navigate to contract details screen
	 * for view purpose.
	 * 
	 * @return the navigate URL 
	 */
	public void goToLoaView(ScLoa scLoa) {
		if (scLoa.getScLoiLoa().getDocType().equalsIgnoreCase(ParameterConst.DOCTYPE_DIRECT_NEGO)) { //Direct Nego
			DirectLOABackingBean directLoaBackingBean = new DirectLOABackingBean();
			FacesContext fc = FacesContext.getCurrentInstance();
			directLoaBackingBean.getManagedBean().setBackToPreviousPage(fc.getViewRoot().getViewId());
			directLoaBackingBean.getManagedBean().setTrackingDairyView(true);
			directLoaBackingBean.getManagedBean().setIconForDL(true); //28854
			directLoaBackingBean.viewLOA(scLoa.getLoaId());
		} else {
			LOADetailBackingBean loaDetailBackingBean = new LOADetailBackingBean();
			FacesContext fc = FacesContext.getCurrentInstance();
			loaDetailBackingBean.getManagedBean().setBackToPreviousPage(fc.getViewRoot().getViewId());
			loaDetailBackingBean.getManagedBean().setTrackingDairyView(true);
			loaDetailBackingBean.viewLOA(scLoa.getLoaId());
		}
	}
	
	/**
	 * The method used to validate login user which same owner org profile
	 * have the authority to initiate contract task.
	 * A error message will display on screen if login user is not same owner org profile.
	 * 
	 * @param  ownerOrgProfileId    contract's owner org proifle id
	 * 
	 */
	public void validateOwnerOrgProfile(Long ownerOrgProfileId) {
		FacesScopes facesScopes = new FacesScopes();
		boolean isValidOrgProfile = false;
		log.info("MIN_CONTRACT_ADMIN role: " + facesScopes.hasRoleUser(RoleCode.MIN_CONTRACT_ADMIN.value()));
		log.info("BPK_CCA role: " + facesScopes.hasRoleUser(RoleCode.BPK_CCA.value()));
		log.info("BPK_CCA role: " + facesScopes.hasRoleUser(RoleCode.DESK_OFFICER.value()));
		if (facesScopes.hasRoleUser(RoleCode.MIN_CONTRACT_ADMIN.value()) 
				|| facesScopes.hasRoleUser(RoleCode.BPK_CCA.value())
				|| facesScopes.hasRoleUser(RoleCode.DESK_OFFICER.value())) {
			IUserSO userSO = (IUserSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
			UserReqDTO userReqDTO = new UserReqDTO();
			userReqDTO.setUserId(facesScopes.getUserSessionBean().getUserId());
			log.info("UserId: " + userReqDTO.getUserId());
			UserResDTO userResDTO = userSO.retrieveUserOrgListByUserId(userReqDTO);
			if (userResDTO.getUserOrgList() != null && userResDTO.getUserOrgList().size() > 0) {
				if (userResDTO.getUserOrgList().get(0).getPmOrgProfile() != null
						&& userResDTO.getUserOrgList().get(0).getPmOrgProfile().getOrgProfileId().longValue() == ownerOrgProfileId.longValue()) {
					log.info("userOrgProfileId: "+ userResDTO.getUserOrgList().get(0).getPmOrgProfile().getOrgProfileId());
					log.info("ownerOrgProfileId: " + ownerOrgProfileId);
					isValidOrgProfile = true;
				}
			}
		}
		
		if (!isValidOrgProfile) {
			log.info("validateOwnerOrgProfile failed.");
			//throw new EpException(MessageConst.ERROR_750040);
		}
	}
	
    //#30737 - Start
    /**
     * Builds and returns a localized {@link EpException} based on the
     * inactive user validation error code.
     *
     * @param ex the original exception containing the inactive user
     * validation error code
     * @return localized {@link EpException} with resolved message
     * parameters
    */
    protected EpException buildInactiveUserException(EpException ex) {
        String errorCode = ex.getMessage();
        String[] messageKeys  =
        		getInactiveUserMessageKeys(
                        errorCode,
                        getSelectedUserNames(),
                        ex.getParameters());
        
        if (messageKeys  != null && messageKeys .length > 0) {

            String[] resolvedMessages = new String[messageKeys .length];
            for (int i = 0; i < messageKeys .length; i++) {
                resolvedMessages[i] =
                    ResourceBundleUtils.getMessage(
                    		messageKeys[i],
                        ResourceBundleUtils.BUNDLE_COMMON_LABELS,
                        getFacesScopes().getUserLocale()
                    );
            }
            return new EpException(errorCode, resolvedMessages);
        }
        return new EpException(errorCode);
    }
    
    /**
     * Returns message parameters based on inactive user validation error code.
     *
     * @param errorCode -inactive user validation error code
     * @param selectedUserNames -selected user name used in message parameter generation
     * @return message parameters for inactive user validation message
    */
    private String[] getInactiveUserMessageKeys(String errorCode, String selectedUserNames, String[] parameters) {

        CtContract ctContract = getContractVer().getCtContract();

        if (ctContract == null) {
            return new String[0];
        }

        String contractNo = ctContract.getContractNo();

        if (isSelectedUserError(errorCode)) {
            return getSelectedUserMessageKeys(selectedUserNames, parameters);
        }

        if (MessageConst.ERROR_750097.equals(errorCode)) {
            return getAcknowledgementMessageKeys(contractNo, selectedUserNames, parameters);
        }

        if (isParameterBasedError(errorCode) && parameters != null) {
            return parameters;
        }

        return new String[0];
    }
    
    private boolean isSelectedUserError(String errorCode) {
        return MessageConst.ERROR_750096.equals(errorCode) || MessageConst.ERROR_750098.equals(errorCode);
    }
    
    private String[] getSelectedUserMessageKeys(String selectedUserNames, String[] parameters) {
        if (parameters != null) {
            return parameters;
        }
        return new String[] { selectedUserNames };
    }
    
    private String[] getAcknowledgementMessageKeys(
            String contractNo,
            String selectedUserNames,
            String[] parameters) {

        if (parameters == null) {
            return new String[] { contractNo, selectedUserNames };
        }

        List<String> result = new ArrayList<>();
        result.add(contractNo);
        Collections.addAll(result, parameters);

        return result.toArray(new String[result.size()]);
    }
    
    private boolean isParameterBasedError(String errorCode) {
        return MessageConst.ERROR_750099.equals(errorCode) || MessageConst.ERROR_750100.equals(errorCode);
    }
    
    /**
     * Retrieves and formats the names of the selected approvers into a single string.
     */
    private String getSelectedUserNames() {
        List<String> userNames = new ArrayList<>();
        for (PmUser user : getSelectedApprovers()) {
            userNames.add(user.getUserName());
        }
        return buildUserNames(userNames);
    }
    
    /**
     * Validates the status of users selected for the current action and throws an
     * exception when one or more users are inactive or unavailable.
     * 
     * @param userStatusResults the status check results for the selected users
    */
    public void validateUserStatusOnAction(List<UserStatusCheckDTO> userStatusResults) {

        List<UserStatusCheckDTO> inactiveUsers = new ArrayList<>();

        for (UserStatusCheckDTO userStatus : userStatusResults) {
            if (isInactiveUserStatus(userStatus.getStatus())) {
                inactiveUsers.add(userStatus);
            }
        }

        // Validate inactive users
        if (!inactiveUsers.isEmpty()) {
            validateInactiveUsers(userStatusResults, inactiveUsers);
        }

        // Validate status 5
        validateUnavailableUser(userStatusResults);
    }
    
    /**
     * Validates inactive users and delegates error handling based on the number
     * of selected users.
     *
     * @param userStatusResults the status check results for all selected users
     * @param inactiveUsers the list of users with inactive statuses
     */
    private void validateInactiveUsers(List<UserStatusCheckDTO> userStatusResults, List<UserStatusCheckDTO> inactiveUsers) {

        String userNames = getInactiveUserNames(inactiveUsers);

        // Single selected user and inactive
        if (userStatusResults.size() == 1) {
            validateSingleInactiveUser(userNames);
            return;
        }

        // Multiple selected users with inactive users
        validateMultipleInactiveUsers(inactiveUsers, userNames);
    }
    
    /**
     * Handles validation errors when a single selected user is inactive,
     * using the appropriate error message for the current task.
     *
     * @param userNames the names of the inactive users
     */
    private void validateSingleInactiveUser(String userNames) {
    	
        String message = MessageConst.ERROR_750096;
        String[] parameters = null;

        if (CTProcessNameConst.REVIEW_FA_HT_NAME.equalsIgnoreCase(getTaskName())) {

            message = MessageConst.ERROR_750098;
            parameters = new String[] { userNames };

        } else if (CTProcessNameConst.ACK_FA_HT_NAME.equalsIgnoreCase(getTaskName())) {

            message = MessageConst.ERROR_750097;
            parameters = new String[] { userNames };
        }
        throw new EpException(message, parameters);
    }
    
    /**
     * Handles validation errors when multiple users are selected and at least
     * one user is inactive, using the appropriate error message for the current task.
     *
     * @param inactiveUsers the list of users with inactive statuses
     * @param userNames the names of the inactive users
     */
    private void validateMultipleInactiveUsers(List<UserStatusCheckDTO> inactiveUsers, String userNames) {

        if (CTProcessNameConst.UPDATE_FA_RESULT_HT_NAME.equalsIgnoreCase(getTaskName())) {
            throw new EpException(
                    MessageConst.ERROR_750099,
                    new String[] {
                        buildInactiveCommitteeUserParameters(inactiveUsers)
                    });
        }

        throw new EpException(MessageConst.ERROR_750096, new String[] { userNames });
    }
    
    /**
     * Validates whether the first selected user's status indicates that all
     * users in the list are unavailable and throws the corresponding exception.
     *
     * @param userStatusResults the status check results for the selected users
     */
    private void validateUnavailableUser(List<UserStatusCheckDTO> userStatusResults) {
        if (Long.valueOf(5L).equals(userStatusResults.get(0).getStatus())) {
            String[] parameters = new String[] {
                userStatusResults.get(0).getUserRoleDesc(),
                getContractVer().getCtContract().getOwnerOrgProfile().getOrgValidity().getOrgName()
            };
            throw new EpException(MessageConst.ERROR_750100, parameters);
        }
    }
    
    /**
     * Builds the message parameters for inactive committee users by combining
     * their committee roles and user names, with each user displayed on a
     * separate line.
     */
    private String buildInactiveCommitteeUserParameters(List<UserStatusCheckDTO> inactiveUsers) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < inactiveUsers.size(); i++) {
            UserStatusCheckDTO inactiveUser = inactiveUsers.get(i);

            if (i > 0) {
                sb.append("<br />");
            }

            sb.append(inactiveUser.getMemberRole())
              .append(" ")
              .append(inactiveUser.getUserName());
        }
        return sb.toString();
    }
    
    /**
     * Retrieves and formats the names of inactive users into a single string.
     */
    private String getInactiveUserNames(List<UserStatusCheckDTO> inactiveUsers) {
        List<String> userNames = new ArrayList<>();
        for (UserStatusCheckDTO userStatus : inactiveUsers) {
            userNames.add(userStatus.getUserName());
        }
        return buildUserNames(userNames);
    }
    
    /**
     * Builds a formatted string containing the specified user names, separating
     * multiple names with commas and using "and" before the last name.
     */
    private String buildUserNames(List<String> userNames) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < userNames.size(); i++) {
            if (i > 0) {
                if (i == userNames.size() - 1) {
                    sb.append(" and ");
                } else {
                    sb.append(", ");
                }
            }
            sb.append(userNames.get(i));
        }
        return sb.toString();
    }
    
    protected abstract List<PmUser> getSelectedApprovers();
    protected abstract CtContractVer getContractVer();
    protected abstract String getTaskName();
    
    /**
     * Retrieves the contract admin and checks the admin user status
     */
  	protected List<UserStatusCheckDTO> getContractAdminUserStatus(Long docId, String docType){
  		String userRole = determineContractAdminRole();
  		CtActor ctActor = retrieveContractActor(docId, docType, userRole);
  		if (ctActor != null) {
	        List<PmUser> users = new ArrayList<>();
	        users.add(ctActor.getUser());
	        return checkUserStatus(userRole, users, docType, null);
  		}
	    return new ArrayList<>();
  	}
  	
  	/**
  	 * Checks the status of the specified users by invoking the user status
  	 * stored procedure and builds the corresponding user status check results.
  	 */
  	protected List<UserStatusCheckDTO> checkUserStatus(String roleCode, List<PmUser> users, String docType, String memberRoleCode) {
  		
  	    IUserSO userSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);

  	    CtContract ctContract = getContractVer().getCtContract();
  	    
  	    InactiveUserReqDTO userStatusRequest = new InactiveUserReqDTO();
  	    userStatusRequest.setUserIds(populateUserIdsIntoString(users));
	  	userStatusRequest.setUserRoleCode(roleCode);
	  	userStatusRequest.setOrgProfileId(ctContract.getOwnerOrgProfile().getOrgProfileId());
	  	userStatusRequest.setDocType(docType);
	  	userStatusRequest.setDocNo(ctContract.getContractNo());
	  	userStatusRequest.setNotifyAdmin(RoleCode.PTJ_ADMIN.value());
  	    
  	    InactiveUserResDTO resDto = userSO.checkInactiveUserBySP(userStatusRequest);
  	    resDto.setRoleCode(roleCode);
  	    resDto.setMemberRoleCode(memberRoleCode);

  	    return buildUserStatusCheckResults(users, resDto);
  	}
  	
  	/**
  	 * Builds a list of user status check results by combining the selected users
  	 * with the status information returned from the user status check.
  	 */
  	private List<UserStatusCheckDTO> buildUserStatusCheckResults(List<PmUser> users, InactiveUserResDTO userStatusResponse) {

  	    List<UserStatusCheckDTO> userStatusList = new ArrayList<>();
  	    if (!users.isEmpty()) {
  	    	String[] userStatuses = userStatusResponse.getStatus().split(",");
  	  	    for (int i = 0; i < userStatuses.length; i++) {
  	  	    	
  	  	    	String userRoleDesc = RoleHelper.getInstance().getRoleDescByCode(userStatusResponse.getRoleCode()).getRoleName();
  	  	    	
  	  	        UserStatusCheckDTO userStatus = new UserStatusCheckDTO();
  	  	        userStatus.setUserId(users.get(i).getUserId());
  	  	        userStatus.setUserName(users.get(i).getUserName());
  	  	        userStatus.setUserRoleDesc(userRoleDesc);
  	  	        userStatus.setStatus(Long.parseLong(userStatuses[i].trim()));
  	  	        
  	  	        String memberRoleCode = userStatusResponse.getMemberRoleCode();
  	  	        if (ParameterConst.COMMITTEE_ROLE_CHAIRPERSON.equals(memberRoleCode)) {
  	  	        	userStatus.setMemberRole(ResourceBundleUtils.getMessage("lbl.ct.chairperson", 
							ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()));
  	  	        } else if (ParameterConst.COMMITTEE_ROLE_MEMBER.equals(memberRoleCode)) {
  	  	        	userStatus.setMemberRole(ResourceBundleUtils.getMessage("lbl.ct.member", 
							ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()));
  	  	        }
  	  	        userStatusList.add(userStatus);
  	  	    }
  	    }
  	    return userStatusList;
  	}
  	
  	/**
  	 * Builds a comma-separated string containing the user IDs of the specified users.
  	 */
  	private String populateUserIdsIntoString(List<PmUser> pmUsers){
  		StringBuilder sb = new StringBuilder();
  		for(PmUser user : pmUsers){
  			if (sb.length() > 0) {
  		        sb.append(",");
  		    }
  		    sb.append(user.getUserId());
  		}
  		return sb.toString();
  	}
  		
  	/**
  	 * Handles inactive user validation during actions.
  	 *
  	 * @param userStatusResults - list of result returned from user status checking process (SP)
  	 * @throws EpException if inactive user conditions are met during validation. 
  	 * Different error messages will be generated based on the returned error code.
  	 */
  	protected void handleInactiveUserValidation(List<UserStatusCheckDTO> userStatusResults) {
  		try {
  			if (userStatusResults.isEmpty()){
  				throw new EpException(MessageConst.ERROR_722505);
  			}
  			validateUserStatusOnAction(userStatusResults);
  		} catch (EpException ex) {
  			throw buildInactiveUserException(ex);
  		}
    }
  	
  	/**
	 * Determines the contract admin role based on the contract type.
	 */
  	protected String determineContractAdminRole() {
  		String userRole = null;
  		String parameterCode = getContractVer().getCtContract().getContractType().getParameterCode();
  		if(ParameterConst.CONTRACT_TYPE_CENTRAL.equalsIgnoreCase(parameterCode)){
  			userRole = RoleCode.BPK_CCA.value();
  		} else if(ParameterConst.CONTRACT_TYPE_MINISTRY.equalsIgnoreCase(parameterCode)) {
  			userRole = RoleCode.MIN_CONTRACT_ADMIN.value();
  		}
  		return userRole;
  	}
  	
  	/**
	 * Determines the contract approver role based on the contract type.
	 */
  	protected String determineContractApproverRole() {
  		String parameterCode = getContractVer().getCtContract().getContractType().getParameterCode();
	    if (ParameterConst.CONTRACT_TYPE_MINISTRY.equalsIgnoreCase(parameterCode)) {
	        return RoleCode.CT_APPROVER.value();
	    } else if (ParameterConst.CONTRACT_TYPE_CENTRAL.equalsIgnoreCase(parameterCode)) {
	        return RoleCode.BPK_CC_APPROVER.value();
	    }
	    return null;
  	}
  	
  	/**
  	 * Determines whether the specified user status represents an inactive user.
  	 *
  	 * @param status the user status to check
  	 * @return {@code true} if the status indicates an inactive user; {@code false} otherwise
  	 */
  	private boolean isInactiveUserStatus(Long status) {
  	    return Long.valueOf(2L).equals(status)
  	            || Long.valueOf(3L).equals(status)
  	            || Long.valueOf(4L).equals(status);
  	}
    //#30737 - End
    
}


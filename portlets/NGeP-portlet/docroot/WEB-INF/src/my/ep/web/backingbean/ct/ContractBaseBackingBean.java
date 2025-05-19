/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.ct;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.service.ct.actor.ICtActorSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.dto.ct.actor.CtActorReqDTO;
import my.ep.dto.ct.actor.CtActorResDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.ct.CtActor;
import my.ep.persistence.entity.ct.dtl.CtAgency;
import my.ep.persistence.entity.ct.dtl.item.CtItemCeilingQty;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.sc.loaloi.ScLoa;
import my.ep.type.pm.RoleCode;
import my.ep.util.ResourceBundleUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.sc.loa.DirectLOABackingBean;
import my.ep.web.backingbean.sc.loa.LOADetailBackingBean;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.ParameterHelper;

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
		if (facesScopes.hasRoleUser(RoleCode.MIN_CONTRACT_ADMIN.value()) 
				|| facesScopes.hasRoleUser(RoleCode.BPK_CCA.value())
				|| facesScopes.hasRoleUser(RoleCode.DESK_OFFICER.value())) { //#25956
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
}


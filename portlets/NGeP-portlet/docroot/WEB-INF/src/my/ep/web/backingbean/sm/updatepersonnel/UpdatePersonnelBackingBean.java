package my.ep.web.backingbean.sm.updatepersonnel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;

import my.ep.constant.MessageConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.dto.sm.mofRegistration.MofRegistrationReqDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.sm.updatepersonnel.UpdatePersonnelManagedBean;
import my.ep.web.util.pm.helper.ParameterHelper;
import my.ep.web.util.sm.helper.SmApplicationUtil;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.util.sm.helper.SmServiceLookup;
import my.ep.web.vo.SmAuthorizedPersonnelCountVO;
import my.ep.web.vo.SmPersonnleCalcVO;

import org.primefaces.context.RequestContext;

@ManagedBean(name = "updatePersonnelBackingBean")
@RequestScoped
public class UpdatePersonnelBackingBean extends
		BaseBackingBean<UpdatePersonnelManagedBean> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5267823583927057585L;

	public void preRenderUpdatePersonnel() {
		renewSesionObject();
		SmCommonUtil.setMDCUserId();
		SmSupplier supplier = SmApplicationUtil.getCurrentSupplier();
		MofRegistrationReqDTO reqDTO = new MofRegistrationReqDTO();
		reqDTO.setSupplierId(supplier.getSupplierId());
		getManagedBean().setSmAppl(
				SmServiceLookup.getMofRegistrationSO()
						.retrieveLatestSmApplWithChildBySupplierId(reqDTO)
						.getSmAppl());
		getManagedBean().setSmSupplier(
				getManagedBean().getSmAppl().getSmSupplier());
		getManagedBean().setSupplierUser(true);
		getManagedBean().setPrevApprovedPersonnels(
				SmApplicationUtil
						.getPrevApprovedPersonnelDirectorOwner(getManagedBean()
								.getSmAppl()));
		calcPersonnelListCount();
		SmApplicationUtil.setPrevApprovedPersonnelNoneEditable(getManagedBean()
				.getSmAppl(), getManagedBean().getPrevApprovedPersonnels(),
				getManagedBean().isDisAllowOwnerEdit());
		SmApplicationUtil.setPersonnelSoftcertRenewalStatus(this
				.getManagedBean().getSmSupplier(), this.getManagedBean()
				.getSmAppl().getSmPersonnels());
		SmApplicationUtil.sortPersonnelByRoleInCompany(getManagedBean()
				.getSmAppl());
		getManagedBean().setBusinessTypeVal(
				SmApplicationUtil.assignSupplierBusinessType(getManagedBean()
						.getSmSupplier()));
		getManagedBean().setCurrentPageType(
				SupplierManagementConst.SM_APPL_PAGE_TYPE_PERSONNEL);
		SmCommonUtil.redirectToPage("updatePersonnelBasePage.xhtml", "", null);
	}

	/**
	 * Add or create a new SmPersonnel bank object
	 * 
	 * @param personelType
	 *            - the personel type to define equity or not
	 */
	public void assignNewShareHolder() {
		SmApplicationUtil.initNewSmPersonnel(getManagedBean(), 0);

	}

	/**
	 * Builds the role list.
	 */
	public void buildRoleList(SmPersonnel personnel) {
		getManagedBean().setePRoleList(
				SmApplicationUtil.getPersonnelRoleItems(personnel,
						getManagedBean().getSmAppl(), getManagedBean()
								.isValidSmUserLimit()));
	}

	/**
	 * Remove un selected admin from the selected personnel list.
	 * 
	 * @param e
	 *            - AjaxBehaviorEvent
	 * 
	 */
	public void unSelectAdmin(AjaxBehaviorEvent e) {
		SmApplicationUtil
				.removePersonnelAdminOrSoftcertProcessFromList(getManagedBean()
						.getSelectedShareHolders());
	}

	/**
	 * To check whether need to disable ep role drop down
	 * 
	 * @param smPersonnel
	 * @return boolean
	 */
	public boolean disableEpRoleDropDown(SmPersonnel smPersonnel) {
		boolean retVal = SmApplicationUtil.disableEpRoleDropDown(smPersonnel,
				getManagedBean().getPrevApprovedPersonnels());
		return retVal;
	}

	/**
	 * Check whether need to disable authorized radio button
	 * 
	 * @param SmPersonnel
	 *            personnel
	 * @return boolean
	 */
	public boolean disableAuthorizedRadio(SmPersonnel personnel) {
		boolean retVal = false;
		retVal = SmApplicationUtil.isDisableAuthorizedRadio(personnel,
				getManagedBean().isValidSmUserLimit());
		getManagedBean().setDisableAuthorizedRadio(retVal);
		return retVal;
	}

	/**
	 * Checks if is personnel view only.
	 * 
	 * @param smPersonnel
	 *            - SmPersonnel object to check
	 * @return true, if is personnel view only
	 */
	public boolean isPersonnelViewOnly(SmPersonnel smPersonnel) {
		boolean retVal = SmApplicationUtil.isPersonnelViewOnly(smPersonnel);
		return retVal;
	}

	/**
	 * Gets the my identity status description based on the responsecode.
	 * 
	 * @param identityResponseCode
	 *            - the identity response code
	 * @return String - MY Identity status description value
	 */
	public String getMyIdentityStatusDescription(String identityResponseCode) {
		return SmCommonUtil.getInstance().getMyIdentityStatusDescription(
				identityResponseCode);
	}

	/**
	 * Delete or remove selected personnel from the SmPeronnel list of the
	 * application.
	 * 
	 * @param personType
	 *            - the person type
	 */
	public void deleteSelectedShareHolders(String personType,
			boolean confirmDelete) {
		int countDeleteAuthPersonnel = SmApplicationUtil
				.validateDeletePersonnel(personType, getManagedBean());

		if (!confirmDelete && countDeleteAuthPersonnel > 0) {
			RequestContext.getCurrentInstance().execute(
					"deleteAuthPersConfirmDialog.show()");
		} else {
			if (getManagedBean().getSelectedShareHolders().size() > 0) {
				for (SmPersonnel shr : getManagedBean()
						.getSelectedShareHolders()) {
					getManagedBean().getSmAppl().getSmPersonnels().remove(shr);
					getManagedBean().getSmapplShareHolders().remove(shr);
				}
				MofRegistrationReqDTO reqDto = new MofRegistrationReqDTO();
				reqDto.setSmAppl(this.getManagedBean().getSmAppl());
				reqDto.setCompanyBasicGst(getManagedBean().getCompanyBasicGst());
				reqDto.setBranchGst(getManagedBean().getSelectedBranch()
						.getGst());
				reqDto.setSameAsHq(getManagedBean().getSelectedBranch()
						.isSameAsHq());
				SmServiceLookup.getMofRegistrationSO()
						.updateApplication(reqDto).getSmAppl();
				MofRegistrationReqDTO reqDTO = new MofRegistrationReqDTO();
				reqDTO.setSupplierId(getManagedBean().getSmSupplier()
						.getSupplierId());
				getManagedBean().setSmAppl(
						SmServiceLookup
								.getMofRegistrationSO()
								.retrieveLatestSmApplWithChildBySupplierId(
										reqDTO).getSmAppl());
				SmApplicationUtil.getInstance().setApplAddressTransientValue(
						getManagedBean().getSmAppl(), false);
				SmApplicationUtil.setPrevApprovedPersonnelNoneEditable(
						getManagedBean().getSmAppl(), getManagedBean()
								.getPrevApprovedPersonnels(), getManagedBean()
								.isDisAllowOwnerEdit());
				SmApplicationUtil.sortPersonnelByRoleInCompany(getManagedBean()
						.getSmAppl());
				getManagedBean().getSelectedShareHolders().clear();
			} else {
				throw new EpException(MessageConst.ERROR_722534,
						new String[] { SmCommonUtil.getMessage(
								"lbl.sm.personnel", null) });
			}
		}

	}

	/**
	 * Assign selected SmPersonnel object to managed bean for editing the
	 * information.
	 * 
	 * @param shareHolderBean
	 *            - SmPersonnel object
	 */
	public void assignSelShareHolder(SmPersonnel shareHolderBean) {
		getManagedBean().setIdentificationTypes(getPmIdTypeDropdownValues());
		SmApplicationUtil.assignSelShareHolder(shareHolderBean,
				getManagedBean(), 0);

	}

	/**
	 * Confirmation on deleting authorized personnel
	 */
	public void confirmDeleteAuthorizedPersonnel() {
		deleteSelectedShareHolders(getManagedBean().getPersonType(), true);
	}

	/**
	 * Retrieve identity type from PM.
	 * 
	 * @return SelectItem[]
	 */
	public SelectItem[] getPmIdTypeDropdownValues() {
		List<PmParameterDesc> parameterDescs = ParameterHelper.getInstance()
				.getParameterDescList(ParameterTypeConst.IDENTIFICATION_TYPES);
		Map<Long, Object> identityType = new HashMap<Long, Object>();
		for (PmParameterDesc pmDesc : parameterDescs) {
			identityType.put(pmDesc.getPmParameter().getParameterId(),
					pmDesc.getPmParameter());
		}

		List<SelectItem> itemList = new ArrayList<SelectItem>();
		SelectItem[] items = getDropdownValues(ParameterTypeConst.IDENTIFICATION_TYPES);

		for (SelectItem selectItem : items) {

			if (selectItem.getValue() == null) {
				itemList.add(selectItem);
			} else {
				PmParameter pmParameter = (PmParameter) identityType
						.get(selectItem.getValue());
				if (!pmParameter.getParameterCode().equals("A")) {
					itemList.add(selectItem);
				}
			}

		}

		return itemList.toArray(new SelectItem[itemList.size()]);

	}

	private void calcPersonnelListCount() {
		SmPersonnleCalcVO calcVO = SmCommonUtil.calcPersonnelListCount(
				getManagedBean().getSmAppl().getSmPersonnels(),
				getManagedBean().getSmAppl().getSmStaffTotals(),
				getManagedBean().getSmAppl().getSupplierType());
		getManagedBean().setPersonCalc(calcVO);
		calcAuthorizedPersonnel();
	}

	private void calcAuthorizedPersonnel() {
		// set already reach valid max authorized personnel and justification
		SmAuthorizedPersonnelCountVO countVO = SmApplicationUtil
				.getSmAuthorizedPersonnelCountVO(getManagedBean()
						.getSmSupplier().getSupplierId(), getManagedBean()
						.getSmAppl().getApplType(), getManagedBean()
						.getSmAppl().getApplId(), false);
		getManagedBean().setValidSmUserLimit(
				countVO.isReachAuthorizedPersonnelLimit());
	}

}

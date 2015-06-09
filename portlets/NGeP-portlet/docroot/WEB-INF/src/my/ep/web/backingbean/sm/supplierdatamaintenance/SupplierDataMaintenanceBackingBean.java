package my.ep.web.backingbean.sm.supplierdatamaintenance;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceException;

import my.ep.dto.sm.mofRegistration.MofRegistrationReqDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.web.backingbean.sm.applicationView.BaseReviewApplicationBackingBean;
import my.ep.web.managedbean.sm.applicationView.ViewSuppProfileManagedBean;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.util.sm.helper.SmServiceLookup;

@ManagedBean(name = "supplierDataMaintenanceBackingBean")
@RequestScoped
public class SupplierDataMaintenanceBackingBean extends
		BaseReviewApplicationBackingBean<ViewSuppProfileManagedBean> {

	public void preRenderSupplierDataMaintenance() {
		renewSesionObject();
		initLogin();
		getManagedBean().setViewSupplierProfile(true);
		getManagedBean().setReviewApplicationExist(false);
		getManagedBean().setViewApplicationHistory(false);
		getManagedBean().setRegStatusDescList(retrieveRegistrationStatusList());
		getManagedBean().setZoneList(
				SmServiceLookup.getZoneSO().retrieveActiveZoneList()
						.getZoneList());
		SmCommonUtil
				.redirectToPage(
						"/xhtml/sm/supplierdatamaintenance/searchSupplierDataMaintenance.xhtml",
						"");
	}

	public void navigateToMaintenance(SmAppl selectedSmAppl) {
		MofRegistrationReqDTO reqDTO = new MofRegistrationReqDTO();
		reqDTO.setApplNo(selectedSmAppl.getApplNo());
		getManagedBean().setSmAppl(
				SmServiceLookup.getMofRegistrationSO()
						.retrieveSmApplWithChildByApplNo(reqDTO).getSmAppl());
		setApplication(getManagedBean().getSmAppl());
		SmCommonUtil
				.redirectToPage(
						"/xhtml/sm/supplierdatamaintenance/editSupplierDataMaintenance.xhtml",
						"");
	}

	public void search() {
		getManagedBean().searchSupplier();
	}

	public void update() {
		MofRegistrationReqDTO reqDTO = new MofRegistrationReqDTO();
		try {
			Long pmStatId = getManagedBean().getSmAppl().getPmStatus()
					.getStatusId();
			for (PmStatusDesc desc : getManagedBean().getRegStatusDescList()) {
				if (pmStatId.equals(desc.getPmStatus().getStatusId())) {
					getManagedBean().getSmAppl()
							.setPmStatus(desc.getPmStatus());
					if (desc.getPmStatus().getPmRegStatus() != null) {
						getManagedBean().getSmAppl().setPmRegStatus(
								desc.getPmStatus().getPmRegStatus());
						getManagedBean()
								.getSmAppl()
								.getSmSupplier()
								.setBumi(
										desc.getPmStatus().getPmRegStatus()
												.isBumiAppl());
						break;
					}
				}
			}
			reqDTO.setSmAppl(getManagedBean().getSmAppl());
			getManagedBean().setSmAppl(
					SmServiceLookup.getMofRegistrationSO()
							.updateApplication(reqDTO).getSmAppl());
		} catch (OptimisticLockException opt) {
			throw new EpException(opt.getMessage());
		} catch (PersistenceException pers) {
			throw new EpException(pers.getMessage());
		} catch (NullPointerException nex) {
			throw new EpException(nex.getMessage());
		} finally {
			reqDTO = null;
		}
	}

	public String backToSearchSupplierMaintenance() {
		return "searchSupplierDataMaintenance.xhtml";
	}

}

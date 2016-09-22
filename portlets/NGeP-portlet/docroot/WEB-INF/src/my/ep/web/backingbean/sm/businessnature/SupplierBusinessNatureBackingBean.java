package my.ep.web.backingbean.sm.businessnature;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import my.ep.biz.service.pm.common.businessnature.IBusinessNatureSO;
import my.ep.biz.service.sm.businessnature.ISupplierBusinessNatureSO;
import my.ep.biz.service.sm.search.ISupplierSearchSO;
import my.ep.constant.EpPropertiesConst;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.pm.common.businessnature.BusinessNatureReqDTO;
import my.ep.dto.pm.common.businessnature.BusinessNatureResDTO;
import my.ep.dto.sm.businessnature.SupplierBusinessNatureReqDTO;
import my.ep.dto.sm.businessnature.SupplierBusinessNatureResDTO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.dto.sm.search.SupplierSearchResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.BaseEntity;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmBusinessNature;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.sm.SmSupplierBusiness;
import my.ep.util.EpPropertiesUtils;
import my.ep.util.MessageUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.sm.businessnature.SupplierBusinessNatureManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.util.sm.helper.SmServiceLookup;

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;

@ManagedBean(name="supplierBusinessNatureBackingBean")
@RequestScoped
public class SupplierBusinessNatureBackingBean extends BaseBackingBean<SupplierBusinessNatureManagedBean>{
	public static Logger log = Logger.getLogger(SupplierBusinessNatureBackingBean.class);
	
	public SupplierBusinessNatureBackingBean(){
		
	}
	
	public void viewBusinessNature(){
		SmSupplier supplier = retrieveSmSupplier();

		if(supplier != null){
			getManagedBean().setSmSupplier(supplier);

			ISupplierBusinessNatureSO businessNatureSO = BusinessDelegate.getInstance().getServiceObject("sm", ISupplierBusinessNatureSO.class);
			SupplierBusinessNatureReqDTO reqDTO = new SupplierBusinessNatureReqDTO();
			reqDTO.setSupplierId(supplier.getSupplierId());

			SupplierBusinessNatureResDTO resDTO = businessNatureSO.retrieveActiveBusinessNatureBySupplierId(reqDTO);
			getManagedBean().getSmSupplier().setSmSupplierBusinesses(resDTO.getBusinessNatureLs());

			//this.getManagedBean().setBusinessNatureLs(resDTO.getBusinessNatureLs());
			log.info("BusinessNature>>>>>" + getManagedBean().getSmSupplier().getSmSupplierBusinesses().size());
			
			if(getManagedBean().getSmSupplier().getSmSupplierBusinesses().size() > 0){
				getManagedBean().setSupplierBusinessMap(new HashMap<Long, SmSupplierBusiness>());
				for(SmSupplierBusiness supplierBusiness : getManagedBean().getSmSupplier().getSmSupplierBusinesses()){
					getManagedBean().getSupplierBusinessMap().put(supplierBusiness.getPmBusinessNature().getBusinessNatureId(), supplierBusiness);
				}
			}

			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication().getNavigationHandler().handleNavigation(fc, null,"/xhtml/sm/supplierbusinessnature/businessNatureMaint.xhtml");
		}
	}
	
	private SmSupplier retrieveSmSupplier() {
		PmUser pmUser = new FacesScopes().getUserSessionBean();
		log.info("******getSmSupplier******" + pmUser.getUserId());
		SupplierSearchReqDTO supplierSearchReqDTO = new SupplierSearchReqDTO();
		supplierSearchReqDTO.setUserId(pmUser.getUserId());

		ISupplierSearchSO supplierSearchSO= null;
		SmSupplier smSupp = null;
		try {
			supplierSearchSO = (ISupplierSearchSO) BusinessDelegate.getInstance().getServiceObject("sm", ISupplierSearchSO.class);
			SupplierSearchResDTO smResDTO = supplierSearchSO.retriveSupplierProfiles(supplierSearchReqDTO);
			smSupp = (SmSupplier)smResDTO.getResultBean().getResultList().get(0);
		} finally {
			supplierSearchSO = null;
		}
		return smSupp;
	}
	

	/**
	 * post Update BizNature
	 * @param bisNatures
	 */
	public void postUpdateBizNature(List<BaseEntity> bisNatures) {
		log.info("bisNatures: " + bisNatures);

		IBusinessNatureSO bizNatureSO = (IBusinessNatureSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IBusinessNatureSO.class);
		Map<Long, PmBusinessNature> bizNatureUniqueMap = new HashMap<Long, PmBusinessNature>();
		
		for(SmSupplierBusiness ssb : getManagedBean().getSmSupplier().getSmSupplierBusinesses()) {
			bizNatureUniqueMap.put(ssb.getPmBusinessNature().getBusinessNatureId(), ssb.getPmBusinessNature());
		}
		
		for(int i = 0; i < bisNatures.size(); i++) {
			PmBusinessNature bn = (PmBusinessNature)bisNatures.get(i);
			log.info("bn.getLevelName(): " + bn.getLevelName());
			
			if(!bizNatureUniqueMap.containsKey(bn.getBusinessNatureId())) {
				
				BusinessNatureReqDTO bnReqDTO = new BusinessNatureReqDTO();
				bnReqDTO.setBusinessNatureId(bn.getBusinessNatureId());
				BusinessNatureResDTO resDTO = bizNatureSO.fetchParentBusinessNature(bnReqDTO);
				bn = resDTO.getBusinessNature();
				bizNatureUniqueMap.put(bn.getBusinessNatureId(), bn);

				SmSupplierBusiness suppBusiness = new SmSupplierBusiness();
				suppBusiness.setPmBusinessNature(bn);
				suppBusiness.setSmSupplier(getManagedBean().getSmSupplier());
				
				if(getManagedBean().getSupplierBusinessMap().get(bn.getBusinessNatureId()) != null){
					SmSupplierBusiness existSupplierBiz = getManagedBean().getSupplierBusinessMap().get(bn.getBusinessNatureId());
					getManagedBean().getSmSupplier().getSmSupplierBusinesses().add(existSupplierBiz);
				}else{
					getManagedBean().getSmSupplier().getSmSupplierBusinesses().add(suppBusiness);
				}
			}
		
		}
	}
	
	/**
	 * get Business Nature Section
	 * @param suppBusinessNat
	 * @return String
	 */
	public String getBusinessNatureSection(SmSupplierBusiness suppBusinessNat) {
		String sectionLvl1Code = "";
		PmBusinessNature pmBizNat = suppBusinessNat.getPmBusinessNature();
		PmBusinessNature pmBizObjL4 = null;
		if (pmBizNat != null
				&& pmBizNat.getParentBusinessNature() != null
				&& pmBizNat.getParentBusinessNature().getBusinessNatureId() != null) {
			pmBizObjL4 = SmCommonUtil.retrieveBusinessNatureById(pmBizNat
					.getParentBusinessNature().getBusinessNatureId());
		}
		PmBusinessNature pmBizObjL3 = null;
		if (pmBizObjL4 != null
				&& pmBizObjL4.getParentBusinessNature() != null
				&& pmBizObjL4.getParentBusinessNature().getBusinessNatureId() != null) {
			pmBizObjL3 = SmCommonUtil.retrieveBusinessNatureById(pmBizObjL4
					.getParentBusinessNature().getBusinessNatureId());
		}
		PmBusinessNature pmBizObjL2 = null;
		if (pmBizObjL3 != null
				&& pmBizObjL3.getParentBusinessNature() != null
				&& pmBizObjL3.getParentBusinessNature().getBusinessNatureId() != null) {
			pmBizObjL2 = SmCommonUtil.retrieveBusinessNatureById(pmBizObjL3
					.getParentBusinessNature().getBusinessNatureId());
		}
		PmBusinessNature pmBizObjL1 = null;
		if (pmBizObjL2 != null
				&& pmBizObjL2.getParentBusinessNature() != null
				&& pmBizObjL2.getParentBusinessNature().getBusinessNatureId() != null) {
			pmBizObjL1 = SmCommonUtil.retrieveBusinessNatureById(pmBizObjL2
					.getParentBusinessNature().getBusinessNatureId());
		}
		sectionLvl1Code = pmBizObjL1 == null ? "" : pmBizObjL1.getLevelCode();
		return sectionLvl1Code;
	}
	
	public void removeBusinessNature() {	
		if(this.getManagedBean().getSelectedBusinessNatureLs().length ==  this.getManagedBean().getSmSupplier().getSmSupplierBusinesses().size()){
			throw new EpException(MessageConst.ERROR_721014);
		}

		for (SmSupplierBusiness suppBusinessNature : this.getManagedBean().getSelectedBusinessNatureLs()) {
			log.info("---------------removeBusinessNature.------------" + suppBusinessNature);
			this.getManagedBean().getSmSupplier().getSmSupplierBusinesses().remove(suppBusinessNature);
		}
		
		getManagedBean().setSelectedBusinessNatureLs(null);
	}
	
	public String updateBusinessNature(){
		if(this.getManagedBean().getSmSupplier().getSmSupplierBusinesses().size() < 1){
			throw new EpException(MessageConst.ERROR_721011);
		}else{
			ISupplierBusinessNatureSO supplierBusinessSO = SmServiceLookup.getSupplierBusinessNatureSO();
			SupplierBusinessNatureReqDTO reqDTO = new SupplierBusinessNatureReqDTO();
			reqDTO.setSmSupplier(getManagedBean().getSmSupplier());
			try{
				SupplierBusinessNatureResDTO resDTO = supplierBusinessSO.saveBusinessNature(reqDTO);

				if(resDTO.isSuccessful()){
					FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(FacesMessage.SEVERITY_INFO,null,MessageUtils.getMessage(MessageConst.INFO_100002,
							MessageUtils.BUNDLE_COMMON_MESSAGES,new FacesScopes().getUserLocale())));	
					//RequestContext.getCurrentInstance().execute("submissionConfirmDlg.hide()");
					renewSesionObject();
					return "/xhtml/common/acknowledge.xhtml";
				}
			}catch(Exception ex){
				RequestContext.getCurrentInstance().execute("submissionConfirmDlg.hide()");
				throw new EpException(ex.getMessage());
			}
		}
		return null;
	}
	
	public void downloadFile(String filePath) throws IOException {
		StringBuilder downloadPath = new StringBuilder();
		downloadPath.append(EpPropertiesUtils.getInstance().getProperty(EpPropertiesConst.DOCUMENT_REPOSITORY_BASE_DIR)).append(filePath);
		
		super.downloadFile(downloadPath.toString());
	}
}

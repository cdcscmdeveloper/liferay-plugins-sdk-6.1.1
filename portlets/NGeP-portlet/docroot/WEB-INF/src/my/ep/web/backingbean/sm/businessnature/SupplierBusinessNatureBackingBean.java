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
import my.ep.dto.sm.common.ssm.SsmReqDTO;
import my.ep.dto.sm.common.ssm.SsmResDTO;
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
import my.ep.web.util.sm.helper.SmApplicationUtil;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.util.sm.helper.SmServiceLookup;

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;

@ManagedBean(name="supplierBusinessNatureBackingBean")
@RequestScoped
public class SupplierBusinessNatureBackingBean extends BaseBackingBean<SupplierBusinessNatureManagedBean>{
	public static Logger log = Logger.getLogger(SupplierBusinessNatureBackingBean.class);
	//16052019 - SSM INTEGRATION CR - Fahmi
	boolean fromtncProceed = false;
	
	public SupplierBusinessNatureBackingBean(){
		
	}
	//16052019 - SSM INTEGRATION CR - Fahmi
	public void goToviewBusinessNature(){
		fromtncProceed = true;
		SmSupplier smSupp = SmApplicationUtil.getCurrentSupplier();
		//27382
		if(SmCommonUtil.getInstance().ssmActive() && businessTypeTovalidateWithSSM(smSupp.getRegNo(),smSupp.getBusinessType())){
			this.getManagedBean().setCompanyValidWithSSM(true);
			// maybe here to put method to check with ssm
			//SmSupplier smSupp = SmApplicationUtil.getCurrentSupplier();
			SsmReqDTO ssmReqDTO = new SsmReqDTO();
			ssmReqDTO.setRegNo(smSupp.getRegNo());
			// ssmReqDTO.setRegNo("IP0218361-W");
			ssmReqDTO.setBusinessType(smSupp.getBusinessType());
			SsmResDTO ssmResDTO = SmCommonUtil.getInstance().validateWithSSM(ssmReqDTO);
			if(ssmResDTO.getSeverity().equalsIgnoreCase("SUCC")){
				this.getManagedBean().setStatusDescFromSSM(setStatusMessageFromSSM(ssmResDTO.getStatusFromSSM()));
				if(!ssmResDTO.getStatusFromSSM().equalsIgnoreCase("A") && !ssmResDTO.getStatusFromSSM().equalsIgnoreCase("E")){
				//27382 - start
					if(ssmResDTO.getStatusFromSSM().equalsIgnoreCase("L")){
						throw new EpException(MessageConst.ERROR_721097,new String[]{SmCommonUtil.getMessage(this.getManagedBean().getStatusDescFromSSM(),null), ""});
					}//27382 - Terminated 
					if(ssmResDTO.getStatusFromSSM().equalsIgnoreCase("T")){
						throw new EpException(MessageConst.ERROR_721098,new String[]{SmCommonUtil.getMessage(this.getManagedBean().getStatusDescFromSSM(),null), ""});
					}//27382
					if(ssmResDTO.getStatusFromSSM().equalsIgnoreCase("B")){
						if(businessTypeIsROC(smSupp.getBusinessType())){
							throw new EpException(MessageConst.ERROR_721070,new String[]{SmCommonUtil.getMessage("lbl.sm.status.b", null)});
						}
						if(businessTypeIsROB(smSupp.getBusinessType())){
							throw new EpException(MessageConst.ERROR_721070,new String[]{SmCommonUtil.getMessage("lbl.sm.status.dissolved", null)});
						}
					}else{ 
					throw new EpException(MessageConst.ERROR_721070,new String[]{SmCommonUtil.getMessage(this.getManagedBean().getStatusDescFromSSM(),null), ""});}
					}
				//27382 - end
				this.getManagedBean().setCompanyValidWithSSM(true);
				this.getManagedBean().setSsmResDTO(ssmResDTO);
			}else{
				throw new EpException(MessageConst.ERROR_721070);
			}
			
		}
		viewBusinessNature();
	}
	
	public void viewBusinessNature(){
		SmSupplier supplier = retrieveSmSupplier();

		if(supplier != null){
			getManagedBean().setSmSupplier(supplier);
			//16052019 - SSM INTEGRATION CR - Fahmi
			FacesContext fc = FacesContext.getCurrentInstance();
			if(!fromtncProceed && businessTypeTovalidateWithSSM(supplier.getRegNo(),supplier.getBusinessType())){
				fc.getApplication().getNavigationHandler().handleNavigation(fc, null,"/xhtml/sm/supplierbusinessnature/businessNatureTermAndCond.xhtml");
				return;
			}
			ISupplierBusinessNatureSO businessNatureSO = BusinessDelegate.getInstance().getServiceObject("sm", ISupplierBusinessNatureSO.class);
			SupplierBusinessNatureReqDTO reqDTO = new SupplierBusinessNatureReqDTO();
			reqDTO.setSupplierId(supplier.getSupplierId());

			SupplierBusinessNatureResDTO resDTO = businessNatureSO.retrieveActiveBusinessNatureBySupplierId(reqDTO);
			getManagedBean().getSmSupplier().setSmSupplierBusinesses(resDTO.getBusinessNatureLs());

			//this.getManagedBean().setBusinessNatureLs(resDTO.getBusinessNatureLs());
			log.info("BusinessNature>>>>>" + getManagedBean().getSmSupplier().getSmSupplierBusinesses().size());
			
			//16052019 - SSM INTEGRATION CR
			if(this.getManagedBean().getSsmResDTO()!= null){
				boolean existInOld=false;
				//compare old data with ssm, if ssm has new data, add data into list
				if(getManagedBean().getSmSupplier().getSmSupplierBusinesses().size() > 0){
					for(SmSupplierBusiness smSupplierBusiness:this.getManagedBean().getSsmResDTO().getSmSupplierBusinessList()){
						//27828
						existInOld=false;
						if(smSupplierBusiness.getPmBusinessNature()!=null){
							for(SmSupplierBusiness smSuppBussOld:getManagedBean().getSmSupplier().getSmSupplierBusinesses()){
								if(smSupplierBusiness.getPmBusinessNature().getLevelCode().equalsIgnoreCase(smSuppBussOld.getPmBusinessNature().getLevelCode())){
									existInOld=true;
								}
							}
						}
						if(!existInOld){
							smSupplierBusiness.setSmSupplier(getManagedBean().getSmSupplier());
							getManagedBean().getSmSupplier().getSmSupplierBusinesses().add(smSupplierBusiness);
						}
					}
				//if old data not exits,set all data from ssm into list
				}else if(this.getManagedBean().getSsmResDTO().getSmSupplierBusinessList().size() > 0){
					getManagedBean().getSmSupplier().setSmSupplierBusinesses(this.getManagedBean().getSsmResDTO().getSmSupplierBusinessList());
					for(SmSupplierBusiness business :getManagedBean().getSmSupplier().getSmSupplierBusinesses()){
						business.setSmSupplier(getManagedBean().getSmSupplier());
					}
				}
			}

			
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




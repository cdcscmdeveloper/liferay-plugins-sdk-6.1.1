package my.ep.web.backingbean.cm.codification;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.cm.codification.RequestCodeManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

@ManagedBean(name = "requestCodeBackingBean")
@ViewScoped
public class RequestCodeBackingBean extends
BaseBackingBean<RequestCodeManagedBean> implements Serializable {

	private static final long serialVersionUID = 1L;


	public RequestCodeBackingBean(){



		IStatusSO statusSO = (IStatusSO) BusinessDelegate.getInstance()
				.getServiceObject("pm", IStatusSO.class);
		String languageCode = new FacesScopes().getUserLocale().getLanguage();

		StatusReqDTO reqStatusDTO = new StatusReqDTO();

		reqStatusDTO.setLanguageCode(languageCode);
		reqStatusDTO.setDocType("CD");
		reqStatusDTO.setModuleCode("CM");
		StatusResDTO resStatusDTO = statusSO.retrieveStatusDescList(reqStatusDTO);


		if(resStatusDTO.getStatusDescList()!=null && resStatusDTO.getStatusDescList().size()>0){

			for(PmStatusDesc desc : resStatusDTO.getStatusDescList()){
				if(desc.getStatusDescId() != 15021L || desc.getStatusDescId()!= 15022L || desc.getStatusDescId()!= 15023 || desc.getStatusDescId()!= 15024){
					this.getManagedBean().getStatusCriteria().add(new SelectItem(desc.getPmStatus().getStatusId(), desc.getStatusName()));
				}
			}
		}

	}





	public void taskDetSearch() {

		this.getManagedBean().getRequestCodeSearchDataModel().reloadData();

	}






}

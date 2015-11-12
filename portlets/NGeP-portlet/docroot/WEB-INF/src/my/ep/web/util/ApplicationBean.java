package my.ep.web.util;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;

import my.ep.biz.service.pm.common.telco.ITelcoSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.pm.common.telco.TelcoReqDTO;
import my.ep.dto.pm.common.telco.TelcoResDTO;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.common.PmTelco;
import my.ep.web.util.lookupservice.BusinessDelegate;

/**
 * common POJO to cache application scoped attributes. 
 * @author Kian Seng
 *
 */
@ManagedBean(name = "applicationBean")
@ApplicationScoped
public class ApplicationBean {

	private static Logger log = EpLogger.getLogger(ApplicationBean.class);
	List<PmTelco> telcoList = new ArrayList<PmTelco>();
	
	/**
	 * to retrieve all active telco from DB and cache in Application Scoped. 
	 * @return
	 */
	public List<PmTelco> getActiveTelcoList() {
		if(telcoList.isEmpty()) {
			ITelcoSO telcoSO = null;
			try {
				telcoSO = (ITelcoSO)BusinessDelegate.getInstance().getServiceObject(
							ModuleCodeConst.PROFILE_MANAGEMENT, ITelcoSO.class);
				TelcoResDTO resDTO = telcoSO.retrieveAllTelcos(new TelcoReqDTO());
				if(resDTO.isSuccessful() && !resDTO.getTelcoList().isEmpty()) {
					telcoList.addAll(resDTO.getTelcoList());					
				}
			} finally {
				telcoSO = null;
			}
		}
		return telcoList;
	}
	
	/**
	 * return the list of select item by telco list. 
	 * @return
	 */
	public List<SelectItem> getTelcoItems() {
		List<SelectItem> telcoItems = new ArrayList<SelectItem>();
		for(PmTelco telco : getActiveTelcoList()) {
			log.info("telco: " + telco.getTelcoId() + ": " + telco.getTelcoName());
			SelectItem item = new SelectItem(telco.getTelcoId(), telco.getTelcoName(), telco.getTelcoName());
			telcoItems.add(item);
		}
		return telcoItems;
	}
}

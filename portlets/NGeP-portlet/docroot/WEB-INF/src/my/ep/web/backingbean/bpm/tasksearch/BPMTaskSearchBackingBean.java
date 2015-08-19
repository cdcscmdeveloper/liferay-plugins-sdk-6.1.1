package my.ep.web.backingbean.bpm.tasksearch;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.bpm.vo.WorklistItemVO;
import my.ep.constant.EpPropertiesConst;
import my.ep.util.EpPropertiesUtils;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;

import org.apache.log4j.Logger;

@ManagedBean(name = "bpmTaskSearchBackingBean")
@RequestScoped
public class BPMTaskSearchBackingBean {

	private static Logger log = Logger.getLogger(BPMTaskSearchBackingBean.class);
	
	private String docNumber;
	
	private List<WorklistItemVO> resultList;
	

	public void searchAction() {
		
		IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance().getServiceObject(IBpmSO.class);
		Object token = bpmSO.authenticate("weblogic");
		
		BpmRequestDTO reqDTO = new BpmRequestDTO();
		reqDTO.setDocNumber(this.getDocNumber());
		reqDTO.setCtxToken(token);
		BpmResponseDTO resDTO = bpmSO.getTasksByDocNumber(reqDTO);
		
		this.setResultList(resDTO.getWorklistVO().getWorklistItem());
		
	}

	public String getDocNumber() {
		return docNumber;
	}

	public void setDocNumber(String docNumber) {
		this.docNumber = docNumber;
	}

	public List<WorklistItemVO> getResultList() {
		return resultList;
	}

	public void setResultList(List<WorklistItemVO> resultList) {
		this.resultList = resultList;
	}
	
	public String getCurrentBuildVersion() {
		String buildOn = EpPropertiesUtils.getInstance().getProperty(
				EpPropertiesConst.BUILD_ON);
		return buildOn;
	}

}

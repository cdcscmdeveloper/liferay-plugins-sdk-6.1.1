package my.ep.web.backingbean.bpm.tasksearch;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

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
		Object token = bpmSO.authenticate(EpPropertiesUtils.getInstance().getProperty(EpPropertiesConst.BPM_PRINCIPAL));
		
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

	public void retrieveCurrentAssigneesByDocNo(String docNo) {

		BpmRequestDTO bpmReqDTO = new BpmRequestDTO();
		bpmReqDTO.setDocNumber(docNo);
		bpmReqDTO.setState("ASSIGNED");
		BpmResponseDTO bpmResDTO = (BpmResponseDTO) ((IBpmSO)EpBpmBusinessDelegate.getInstance().getServiceObject(IBpmSO.class)).getTasks(bpmReqDTO);
		
		if(bpmResDTO.getWorklistVO() != null) {
			log.info("bpmResDTO.getWorklistVO().getTotal(): " + bpmResDTO.getWorklistVO().getTotal());
			for (WorklistItemVO item : bpmResDTO.getWorklistVO()
					.getWorklistItem()) {
				log.info("item.getAssignees(): " + item.getAssignees());
			}
		}
		
	}
	
	public void onloadPaymentResponse() {
		System.out.println("In onloadPaymentResponse");
		System.out.println("doing some payment processes");
		System.out.println("done");
		
		
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication()
				.getNavigationHandler()
				.handleNavigation(fc, null,
						"/xhtml/bpmTaskSearch/paymentresponse.xhtml?faces-redirect=true");
		
//		return "paymentresponse?faces-redirect=true";
	}
}

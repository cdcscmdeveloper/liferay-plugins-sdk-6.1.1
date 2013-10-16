package my.ep.web.backingbean.bpm.tasklist;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.portlet.PortletRequest;

import my.ep.biz.service.cm.codification.ICodificationSO;
import my.ep.biz.service.cm.codification.IRequestCodeSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.dto.cm.codification.CodificationReqDTO;
import my.ep.dto.cm.codification.CodificationResDTO;
import my.ep.dto.cm.codification.RequestCodeReqDTO;
import my.ep.dto.cm.codification.RequestCodeResDTO;
import my.ep.jaxb.cd.unspscdata.CDUNSPSCDataType;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.cm.codification.UnspscCodeBackingBean;
import my.ep.web.constant.PagingConst;
import my.ep.web.managedbean.bpm.tasklist.TaskListManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;

import com.liferay.portal.util.PortalUtil;

@ManagedBean(name="tasklistBackingBean")
@RequestScoped
public class TaskListBackingBean extends BaseBackingBean<TaskListManagedBean>{

	private static Logger log = Logger.getLogger(TaskListBackingBean.class);
	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat( "MM/dd/yy hh:mm a" );



	public TaskListBackingBean(){
		
		
		
//		String testtext = PortalUtil.getHttpServletRequest((PortletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest()).getParameter("testing");
//		System.out.println("new FacesScopes().getParameter(\"testing\"): " + testtext);
		
		this.getManagedBean().setBpmMap(null);
		Map temp = new HashMap();


		temp.put("key", "master-catalogue");
		temp.put("key1", "unspsc");

		this.getManagedBean().setBpmMap(temp);
	}


	public void taskListAction(){

		System.out.println("testing BPM******");
		BpmResponseDTO responseDTO = retrieveWorklistItems();
		//BpmResponseDTO responseDTO = initiateTask();
	}

	public BpmResponseDTO initiateTask(){
		System.out.println("initiateTask******");
		IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance().getServiceObject(IBpmSO.class);
		String ctx = bpmSO.authenticate("roslan","welcome1");
		BpmRequestDTO reqDTO = new BpmRequestDTO();


		//  reqDTO.setProcessId("Procurement_Strategy.EndorseBudget");
		reqDTO.setProcessId("Codification.RequestUNSPSC");
		reqDTO.setOutcome("SUBMIT");
		CDUNSPSCDataType obj = new CDUNSPSCDataType();
		//  obj.setApprover("Asdqwe");
		//  obj.setBudgetId("Asdqwe");
		//  obj.setPtj("Asdqwe");
		reqDTO.setPojo(obj);
		BpmResponseDTO res = bpmSO.initiateAndUpdateTask(reqDTO);

		//bpmSO.getTaskDetails(reqDTO)
		// reqDTO.setCtxToken(ctx);
		//reqDTO.setTaskId(taskId);

		// 
		//--
		//	  Object asd = res.getPojo();
		//	  CDUNSPSCDataType qwe = (CDUNSPSCDataType)asd;
		//	  qwe.getPtj();
		//--
		return res;
	}

	public BpmResponseDTO retrieveWorklistItems(){



		IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance().getServiceObject(IBpmSO.class);

		//String token = bpmSO.authenticate("roslan", "welcome1");
		String token = bpmSO.authenticate("khai", "welcome1");
		System.out.println("testing BPM auth******"+token);
		BpmRequestDTO reqDTO = new BpmRequestDTO();

		reqDTO.setLimit(PagingConst.ROWS_PER_PAGE);
		reqDTO.setOffset(calculateFinalOffSet(1));
		reqDTO.setState("ASSIGNED");
		reqDTO.setCtxToken(token);
		BpmResponseDTO responseDTO = bpmSO.getWorklistItems(reqDTO);

		System.out.println("testing BPM******"+responseDTO.getWorklistVO().getWorklistItem());



		this.getManagedBean().setTaskList(responseDTO.getWorklistVO().getWorklistItem());




		return responseDTO;

	}

	public String navigateToURL(){
		
			IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance().getServiceObject(IBpmSO.class);
			IRequestCodeSO requestSO = (IRequestCodeSO) BusinessDelegate.getInstance().getServiceObject("cm",IRequestCodeSO.class);
			ICodificationSO unspscSO = (ICodificationSO) BusinessDelegate.getInstance().getServiceObject("cm",ICodificationSO.class);
			String urlKey = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("key");
			String taskId = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("taskId");
			String ctx = bpmSO.authenticate("roslan","welcome1");
			BpmRequestDTO reqDTO = new BpmRequestDTO();
			CodificationReqDTO reqCodeDTO = new CodificationReqDTO();
			
			log.info("taskId:"+taskId);
			reqDTO.setCtxToken(ctx);
			reqDTO.setTaskId(taskId);
			BpmResponseDTO res = bpmSO.getTaskDetails(reqDTO);
			
			Object obj = res.getPojo();
			CDUNSPSCDataType unspsc = (CDUNSPSCDataType)obj;
			log.info("requestID:"+unspsc.getRequestId());
			
			//reqCodeDTO.getRequestCode().setTaskId(taskId);
			

			
			RequestCodeReqDTO codeDTO = new RequestCodeReqDTO();
			codeDTO.setRequestId(new Long(unspsc.getRequestId()));
			
			RequestCodeResDTO resDTO = requestSO.retrieveUnspscCodeReview(codeDTO);
			log.info("name:"+resDTO.getRequestCode().getItemName());
			
			resDTO.getRequestCode().setTaskId(taskId);
			reqCodeDTO.setRequestCode(resDTO.getRequestCode());
			CodificationResDTO resCodeDTO = unspscSO.updateCodificationRequest(reqCodeDTO);
			
			UnspscCodeBackingBean unspscBB = new UnspscCodeBackingBean();
			unspscBB.getManagedBean().setRequestCode(resCodeDTO.getRequestCode());
			
			String url = "";
			log.info("******urlKey***********"+urlKey);
			if(!this.getManagedBean().getBpmMap().isEmpty() && this.getManagedBean().getBpmMap().size() > 0 ){ 


				Iterator itMap = this.getManagedBean().getBpmMap().entrySet().iterator();

				while(itMap.hasNext()) {
					Map.Entry entry = (Map.Entry)itMap.next();
					String key = (String)entry.getKey();
					log.info("mapKey:"+key);
					if(key.equalsIgnoreCase("key1")){
						url = (String)entry.getValue();

					}
				}

			}
			log.info("url:"+url);
			
			
			return "taskListCommonPage";
			
			//ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();

			//externalContext.redirect("http://localhost:8080/web/guest/" +url +"?testing=123");
		

	}




	private int calculateFinalOffSet(int currentPage){

		int offSet = (currentPage-1) *PagingConst.ROWS_PER_PAGE;

		return offSet;
	}


}

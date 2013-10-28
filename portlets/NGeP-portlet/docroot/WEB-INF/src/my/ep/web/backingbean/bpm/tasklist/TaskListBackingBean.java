package my.ep.web.backingbean.bpm.tasklist;

import java.text.SimpleDateFormat;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import my.ep.biz.service.cm.codification.IRequestCodeSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.dto.cm.codification.RequestCodeReqDTO;
import my.ep.dto.cm.codification.RequestCodeResDTO;
import my.ep.jaxb.cd.requestdata.CDRequestDataType;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.cm.codification.UnspscCodeBackingBean;
import my.ep.web.constant.PagingConst;
import my.ep.web.managedbean.bpm.tasklist.TaskListManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;

@ManagedBean(name="tasklistBackingBean")
@RequestScoped
public class TaskListBackingBean extends BaseBackingBean<TaskListManagedBean>{

	private static Logger log = Logger.getLogger(TaskListBackingBean.class);
	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat( "MM/dd/yy hh:mm a" );



	public TaskListBackingBean(){

		System.out.println("testing BPM******");

		String viewBy = "";
		BpmResponseDTO responseDTO = retrieveWorklistItems("ASSIGNED", viewBy);
	}



	public BpmResponseDTO initiateTask(){
		System.out.println("initiateTask******");
		IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance().getServiceObject(IBpmSO.class);
		String ctx = bpmSO.authenticate("roslan","welcome1");
		BpmRequestDTO reqDTO = new BpmRequestDTO();

		reqDTO.setProcessId("Codification.RequestUNSPSC");
		reqDTO.setOutcome("SUBMIT");
		CDRequestDataType obj = new CDRequestDataType();
	
		reqDTO.setPojo(obj);
		BpmResponseDTO res = bpmSO.initiateAndUpdateTask(reqDTO);

		return res;
	}

	public BpmResponseDTO retrieveWorklistItems(String status, String viewBy){



		IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance().getServiceObject(IBpmSO.class);

		//String token = bpmSO.authenticate("roslan", "welcome1");
		String token = bpmSO.authenticate("roslan", "welcome1");
		System.out.println("testing BPM auth******"+token);
		BpmRequestDTO reqDTO = new BpmRequestDTO();

		reqDTO.setLimit(PagingConst.ROWS_PER_PAGE);
		reqDTO.setOffset(calculateFinalOffSet(1));
		reqDTO.setState(status);
		reqDTO.setViewBy(viewBy);
		reqDTO.setCtxToken(token);
		BpmResponseDTO responseDTO = bpmSO.getWorklistItems(reqDTO);

		System.out.println("testing BPM******"+responseDTO.getWorklistVO().getWorklistItem().size());



		this.getManagedBean().setTaskList(responseDTO.getWorklistVO().getWorklistItem());




		return responseDTO;

	}

	public String navigateToURL(){

		IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance().getServiceObject(IBpmSO.class);

		String urlKey = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("key");
		String taskId = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("taskId");
		String ctx = bpmSO.authenticate("roslan","welcome1");
		BpmRequestDTO reqDTO = new BpmRequestDTO();


		log.info("taskId:"+taskId);
		reqDTO.setCtxToken(ctx);
		reqDTO.setTaskId(taskId);
		BpmResponseDTO res = bpmSO.getTaskDetails(reqDTO);
		System.out.println("********getProcessName********"+res.getProcessName());
		System.out.println("********res.getAction()********"+res.getAction());
		this.getManagedBean().setProcessName(res.getProcessName());
		this.getManagedBean().setAction(res.getAction());
		Object obj = res.getPojo();
		this.getManagedBean().setTaskId(taskId);

		if(res.getProcessName().equalsIgnoreCase("UNSPSC Request")){

			CDRequestDataType unspsc = (CDRequestDataType)obj;

			IRequestCodeSO requestSO = (IRequestCodeSO) BusinessDelegate.getInstance().getServiceObject("cm",IRequestCodeSO.class);
			RequestCodeReqDTO codeDTO = new RequestCodeReqDTO();
			codeDTO.setRequestId(new Long(unspsc.getRequestId()));
			RequestCodeResDTO resDTO = requestSO.retrieveUnspscCodeReview(codeDTO);

			UnspscCodeBackingBean unspscBB = new UnspscCodeBackingBean();
			unspscBB.getManagedBean().setRequestCode(resDTO.getRequestCode());


		}else if(res.getProcessName().equalsIgnoreCase("Extension Code Request")){
			CDRequestDataType unspsc = (CDRequestDataType)obj;

			IRequestCodeSO requestSO = (IRequestCodeSO) BusinessDelegate.getInstance().getServiceObject("cm",IRequestCodeSO.class);
			RequestCodeReqDTO codeDTO = new RequestCodeReqDTO();
			UnspscCodeBackingBean unspscBB = new UnspscCodeBackingBean();
			for(String itemId:unspsc.getItemId()){
				System.out.println("itemId:"+itemId);
				codeDTO.setRequestId(new Long(itemId));
				RequestCodeResDTO resDTO = requestSO.retrieveUnspscCodeReview(codeDTO);
				unspscBB.getManagedBean().getReviewExtCodeList().add(resDTO.getRequestCode());
			}


		}





		return "taskListCommonPage";
	}

	private int calculateFinalOffSet(int currentPage){

		int offSet = (currentPage-1) *PagingConst.ROWS_PER_PAGE;

		return offSet;
	}

	public void viewby(String viewBy){
		System.out.println("viewBy:"+viewBy);

		BpmResponseDTO responseDTO = retrieveWorklistItems(this.getManagedBean().getStatus(), viewBy);


	}

	public void filterStatus(){

		BpmResponseDTO responseDTO = retrieveWorklistItems(this.getManagedBean().getStatus(), "");
	}


}

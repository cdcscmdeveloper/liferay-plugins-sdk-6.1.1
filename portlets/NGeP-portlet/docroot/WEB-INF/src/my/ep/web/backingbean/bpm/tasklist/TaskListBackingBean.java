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

import org.apache.log4j.Logger;

import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.cm.codification.CodificationBackingBean;
import my.ep.web.constant.PagingConst;
import my.ep.web.managedbean.bpm.tasklist.TaskListManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;

@ManagedBean(name="tasklistBackingBean")
@RequestScoped
public class TaskListBackingBean extends BaseBackingBean<TaskListManagedBean>{
	
	private static Logger log = Logger.getLogger(TaskListBackingBean.class);
	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat( "MM/dd/yy hh:mm a" );
	
	
	
	public TaskListBackingBean(){
		this.getManagedBean().setBpmMap(null);
		Map temp = new HashMap();


		temp.put("key", "master-catalogue");
		temp.put("key1", "development");

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
	  reqDTO.setCtxToken(ctx);

	//  reqDTO.setProcessId("Procurement_Strategy.EndorseBudget");
	  reqDTO.setProcessId("Codification.RequestUNSPSC");
	  reqDTO.setOutcome("");
	//  PSBudgetDataType obj = new PSBudgetDataType();
	//  obj.setApprover("Asdqwe");
	//  obj.setBudgetId("Asdqwe");
	//  obj.setPtj("Asdqwe");
	//  reqDTO.setPojo(obj);
	  BpmResponseDTO res = bpmSO.initiateAndUpdateTask(reqDTO);
	  return res;
	}

	public BpmResponseDTO retrieveWorklistItems(){



		IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance().getServiceObject(IBpmSO.class);

		String token = bpmSO.authenticate("roslan", "welcome1");
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

	public void navigateToURL(){
		try {
			String urlKey = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("key");
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

			ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();

			externalContext.redirect("http://localhost:8080/web/guest/" +url +"?faces-redirect=true");
		} catch (IOException e) {

			e.printStackTrace();
		}

	}




	private int calculateFinalOffSet(int currentPage){

		int offSet = (currentPage-1) *PagingConst.ROWS_PER_PAGE;

		return offSet;
	}
	
	
}

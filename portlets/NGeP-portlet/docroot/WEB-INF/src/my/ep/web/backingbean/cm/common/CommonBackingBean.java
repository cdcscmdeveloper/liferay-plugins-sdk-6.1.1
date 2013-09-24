package my.ep.web.backingbean.cm.common;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import oracle.bpel.services.bpm.common.IBPMContext;

import my.ep.bpm.service.IBpmSO;
import my.ep.dto.BpmRequestDTO;
import my.ep.dto.BpmResponseDTO;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.constant.PagingConst;
import my.ep.web.managedbean.cm.common.CommonManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;

@ManagedBean(name="commonBackingBean")
@RequestScoped
public class CommonBackingBean extends BaseBackingBean<CommonManagedBean>{
	
	
	public CommonBackingBean(){
		//BpmResponseDTO responseDTO = retrieveWorklistItems();
		
	}
	
	
	public void taskList(){
		
		System.out.println("testing BPM******");
		BpmResponseDTO responseDTO = retrieveWorklistItems();
	}
	
	
	
	public BpmResponseDTO retrieveWorklistItems(){


		//reqDTO.setViewBy(getViewDateByResult());
		//ITaskListSO procurementSO= BusinessDelegate.getInstance().getBpmServiceObject(ITaskListSO.class);
		IBpmSO bpmSO= BusinessDelegate.getInstance().getBpmServiceObject(IBpmSO.class);
		IBPMContext ctx = bpmSO.authenticate("roslan", "welcome1");
		System.out.println("testing BPM auth******"+ctx);
		BpmRequestDTO reqDTO = new BpmRequestDTO();
		
		reqDTO.setLimit(PagingConst.ROWS_PER_PAGE);
		reqDTO.setOffset(calculateFinalOffSet(1));
		reqDTO.setState("ASSIGNED");
		reqDTO.setCtx(ctx);
		BpmResponseDTO responseDTO = bpmSO.getWorklistItems(reqDTO);
		
		System.out.println("testing BPM******"+responseDTO.getWorklistVO().getTotal());
		

		return responseDTO;

	}
	
	
	private int calculateFinalOffSet(int currentPage){

		int offSet = (currentPage-1) *PagingConst.ROWS_PER_PAGE;

		return offSet;
	}
	
}

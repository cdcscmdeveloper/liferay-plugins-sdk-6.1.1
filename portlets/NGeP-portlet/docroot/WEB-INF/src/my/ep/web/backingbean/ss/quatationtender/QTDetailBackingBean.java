package my.ep.web.backingbean.ss.quatationtender;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.event.ValueChangeEvent;

import my.ep.biz.dto.ss.quationtender.QuationTenderReqDTO;
import my.ep.biz.dto.ss.quationtender.QuationTenderResDTO;
import my.ep.biz.service.ss.quationtender.IQTDetailSO;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.common.PmDo;
import my.ep.persistence.entity.pm.common.PmGo;
import my.ep.persistence.entity.pm.common.PmSo;
import my.ep.persistence.entity.ss.quationtender.ScQtGosodoBudget;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.ss.quationtender.QTDetailManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;

/**
 * @Author VidyasagarReddy
 */

@ManagedBean(name="qTDetailBB")
@RequestScoped
public class QTDetailBackingBean extends BaseBackingBean<QTDetailManagedBean>{
	
	private static final Logger log= EpLogger.getLogger(QTDetailBackingBean.class);
	
	private static final String MODULE_NAME="ss";
	private IQTDetailSO detailSO=null;
	

	public  QTDetailBackingBean(){
		
		log.info("inside constructor");
		
	}
	/**
	 * Creating the SO after bean object creation. instead of calling from constructor
	 */
	@PostConstruct
	public void initializeObejcts(){
		IQTDetailSO so = BusinessDelegate.getInstance().getServiceObject(MODULE_NAME,IQTDetailSO.class);
		this.detailSO=so;
		log.info("successfully look up the so object"+ detailSO);
	}
	/*
	 * To add empty row to gosodo table
	 */
	public void addNewGoSoDoItem(){
		log.info("begin add gosodo item method");
		List<PmGo> goList=this.getManagedBean().getGoList();
		//Make a SO call only once to get the data if it is empty other wise skip the so call
		if(goList ==null || goList.isEmpty()){
		QuationTenderResDTO dto = detailSO.retriveQTDetail(null);
		goList=dto.getGoList();
		}
	     ScQtGosodoBudget budget = new ScQtGosodoBudget();
		 budget.setGos(goList);	
		 
		List<ScQtGosodoBudget> budg = this.getManagedBean().getQt().getScQtGosodoBudgets();
		if(budg == null){
			budg= new ArrayList<ScQtGosodoBudget>();
			budg.add(budget);
		}else{
			budg.add(budget);
		}
		log.info("End of add gosodo item method");
	}
	/*
	 * Onchange of gos to fetch the so's list
	 */
	public void handleGoChange(ValueChangeEvent changeEvent){
		log.info("inside handle go change method");
		PmGo pmGo = (PmGo) changeEvent.getNewValue();
		if(pmGo != null && pmGo.getGoId() !=null ){
			// get the so list and set it to managed bean property
			QuationTenderReqDTO reqDTO= new QuationTenderReqDTO();
			reqDTO.setPmGo(pmGo);
			QuationTenderResDTO resDTO = detailSO.retrieveSOListByGOCode(reqDTO);
			List<PmSo> solist = this.getManagedBean().getSoList();
			solist.clear();
			solist.addAll(resDTO.getSoList());
		}
		log.info("end of handle go change method");
	}
	/*
	 * OnChange of So's get the do's list
	 */
	public void handleSoChange(ValueChangeEvent changeEvent){
		log.info("inside handle so change method");
		PmSo pmSo = (PmSo) changeEvent.getNewValue();
		if(pmSo != null && pmSo.getSoId()!=null){
			// get the do list and set it to managed bean property
			QuationTenderReqDTO reqDTO= new QuationTenderReqDTO();
			reqDTO.setPmSo(pmSo);
			QuationTenderResDTO resDTO = detailSO.retrieveDOListBySOCode(reqDTO);
			List<PmDo> dolist = this.getManagedBean().getDoList();
			dolist.clear();
			dolist.addAll(resDTO.getDoList());
		}
		log.info("end  of handle go change method");
	}
}

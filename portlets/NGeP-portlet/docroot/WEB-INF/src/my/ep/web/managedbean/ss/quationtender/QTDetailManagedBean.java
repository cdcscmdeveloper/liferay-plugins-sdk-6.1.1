package my.ep.web.managedbean.ss.quationtender;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.common.PmCategoryL1;
import my.ep.persistence.entity.pm.common.PmCategoryL2;
import my.ep.persistence.entity.pm.common.PmCategoryL3;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmDo;
import my.ep.persistence.entity.pm.common.PmGo;
import my.ep.persistence.entity.pm.common.PmSo;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.ss.quationtender.ScQt;
import my.ep.persistence.entity.ss.quationtender.ScQtGosodoBudget;
import my.ep.web.managedbean.BaseManagedBean;

import org.apache.log4j.Logger;


/**
 * @Author VidyasagarReddy
 */

@ManagedBean(name="qTDetailMB")
@SessionScoped
public class QTDetailManagedBean extends BaseManagedBean {

	
	private static final long serialVersionUID = 1L;
	private static final Logger log= EpLogger.getLogger(QTDetailManagedBean.class);
	
	private ScQt qt= new ScQt();
	
	private List<PmSo> soList;
	
	private List<PmDo> doList;
	
	private List<PmGo> goList;
	
	private List<PmCategoryL1> categoryL1s;
	
	private List<PmCategoryL2> categoryL2s;
	
	private List<PmCategoryL3> categoryL3s;
	
	private List<PmState> states;
	
	private List<PmDistrict> districts;
	
	public List<ScQtGosodoBudget> gosodos;
	
	private String qtNumber;
	
		
	public QTDetailManagedBean(){
		log.info("inside ");
	}

	

	public List<PmSo> getSoList() {
		return soList;
	}

	public void setSoList(List<PmSo> soList) {
		this.soList = soList;
	}

	public List<PmDo> getDoList() {
		return doList;
	}

	public void setDoList(List<PmDo> doList) {
		this.doList = doList;
	}

	public List<PmGo> getGoList() {
		return goList;
	}

	public void setGoList(List<PmGo> goList) {
		this.goList = goList;
	}

	public List<PmCategoryL1> getCategoryL1s() {
		return categoryL1s;
	}

	public void setCategoryL1s(List<PmCategoryL1> categoryL1s) {
		this.categoryL1s = categoryL1s;
	}

	public List<PmCategoryL2> getCategoryL2s() {
		return categoryL2s;
	}

	public void setCategoryL2s(List<PmCategoryL2> categoryL2s) {
		this.categoryL2s = categoryL2s;
	}

	public List<PmCategoryL3> getCategoryL3s() {
		return categoryL3s;
	}

	public void setCategoryL3s(List<PmCategoryL3> categoryL3s) {
		this.categoryL3s = categoryL3s;
	}

	public String getQtNumber() {
		return qtNumber;
	}

	public void setQtNumber(String qtNumber) {
		this.qtNumber = qtNumber;
	}



	public ScQt getQt() {
		return qt;
	}



	public void setQt(ScQt qt) {
		this.qt = qt;
	}



	public List<PmState> getStates() {
		return states;
	}



	public void setStates(List<PmState> states) {
		this.states = states;
	}



	public List<PmDistrict> getDistricts() {
		return districts;
	}



	public void setDistricts(List<PmDistrict> districts) {
		this.districts = districts;
	}



	public List<ScQtGosodoBudget> getGosodos() {
		return gosodos;
	}



	public void setGosodos(List<ScQtGosodoBudget> gosodos) {
		this.gosodos = gosodos;
	}



}

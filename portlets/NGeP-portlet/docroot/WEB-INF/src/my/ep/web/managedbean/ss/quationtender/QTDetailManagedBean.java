package my.ep.web.managedbean.ss.quationtender;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.pm.common.PmCategoryL1;
import my.ep.persistence.entity.pm.common.PmCategoryL2;
import my.ep.persistence.entity.pm.common.PmCategoryL3;
import my.ep.persistence.entity.pm.common.PmDo;
import my.ep.persistence.entity.pm.common.PmGo;
import my.ep.persistence.entity.pm.common.PmSo;
import my.ep.persistence.entity.ss.quationtender.ScQt;
import my.ep.web.managedbean.BaseManagedBean;

@ManagedBean(name="qTDetailManagedBean")
@SessionScoped
public class QTDetailManagedBean extends BaseManagedBean {

	
	private static final long serialVersionUID = 1L;
	
	private ScQt scQt;
	
	private List<PmSo> soList;
	
	private List<PmDo> doList;
	
	private List<PmGo> goList;
	
	private List<PmCategoryL1> categoryL1s;
	
	private List<PmCategoryL2> categoryL2s;
	
	private List<PmCategoryL3> categoryL3s;
	
	private String qtNumber;
	
	private QTDetailManagedBean(){
		
	}

	public ScQt getScQt() {
		return scQt;
	}

	public void setScQt(ScQt scQt) {
		this.scQt = scQt;
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

}

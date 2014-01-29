package my.ep.web.backingbean.sc.quatationtender;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import my.ep.factory.EpLogger;
import my.ep.persistence.entity.sc.quationtender.ScQtRemark;
import my.ep.web.managedbean.sc.quationtender.QTDetailManagedBean;
import my.ep.web.util.FacesScopes;

import org.apache.log4j.Logger;

/**
 * @author SudheerKumar
 *
 */
@ManagedBean(name="qTRemarksBB")
@RequestScoped
public class QTRemarksBackingBean extends SourcingBaseBackingBean<QTDetailManagedBean>{
	private static final Logger log=EpLogger.getLogger(QTRemarksBackingBean.class);
	

	
	public QTRemarksBackingBean() {
		log.debug("QTRemarksBackingBean.constructor()");
		initializeRemarkt(new ArrayList<ScQtRemark>());
	}
	
	public void initializeRemarkt(List<ScQtRemark> scQtRemarks) {
		FacesScopes facesScopes = new FacesScopes();
		
		if(scQtRemarks.size()==0 || !compareUserName(scQtRemarks.get(0).getCreatedUsername())) {
			ScQtRemark qtRemark = new ScQtRemark();
			qtRemark .setCreatedBy(facesScopes.getUserSessionBean().getUserId());
			qtRemark .setCreatedDate(new Timestamp(System.currentTimeMillis()));
			qtRemark .setChangedBy(qtRemark .getCreatedBy());
			qtRemark .setChangedDate(qtRemark .getCreatedDate());
			qtRemark .setCreatedUsername(facesScopes.getUserSessionBean().getLoginId());
			
			getManagedBean().setScQtRemarks(scQtRemarks);
			getManagedBean().getScQtRemarks().add(0, qtRemark);
		} else {
			getManagedBean().setScQtRemarks(scQtRemarks);
		}
		
	}
	public void addRemarks(){
		
		 List<ScQtRemark> remarks=this.getManagedBean().getScQtRemarks();
		 if(remarks == null){
			 remarks= new ArrayList<ScQtRemark>();
			 
		 }
		this.getManagedBean().setQtRemark(null);
		ScQtRemark qr=new ScQtRemark();
		
		this.getManagedBean().setQtRemark(qr);
		remarks.add(this.getManagedBean().getQtRemark());
		this.getManagedBean().setScQtRemarks(remarks);
		
		 
		}
}


	
	
	


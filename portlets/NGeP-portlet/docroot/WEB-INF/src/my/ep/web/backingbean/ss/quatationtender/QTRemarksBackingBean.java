package my.ep.web.backingbean.ss.quatationtender;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.impl.xb.xsdschema.Public;

import my.ep.factory.EpLogger;
import my.ep.persistence.entity.ss.quationtender.ScQtDoc;
import my.ep.persistence.entity.ss.quationtender.ScQtRemark;
import my.ep.web.managedbean.ss.quationtender.QTDetailManagedBean;

/**
 * @author SudheerKumar
 *
 */
@ManagedBean(name="qTRemarksBB")
@RequestScoped
public class QTRemarksBackingBean extends SourcingBaseBackingBean<QTDetailManagedBean>{
	private static final Logger log=EpLogger.getLogger(QTRemarksBackingBean.class);
	
	public QTRemarksBackingBean(){
		
		ScQtRemark qtRemark=new ScQtRemark();
		this.getManagedBean().setQtRemark(qtRemark);
		
		
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
	
	
	


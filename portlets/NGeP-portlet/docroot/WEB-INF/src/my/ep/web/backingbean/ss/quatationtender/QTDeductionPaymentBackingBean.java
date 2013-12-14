/**
 * 
 */
package my.ep.web.backingbean.ss.quatationtender;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import org.apache.log4j.Logger;

import my.ep.factory.EpLogger;
import my.ep.persistence.entity.ss.quationtender.ScQtContactPerson;
import my.ep.persistence.entity.ss.quationtender.ScQtDeduction;
import my.ep.web.managedbean.ss.quationtender.QTDetailManagedBean;

/**
 * @author Sudheer Kumar
 *
 */
@ManagedBean(name="qTDeductionBB")
@RequestScoped
public class QTDeductionPaymentBackingBean extends SourcingBaseBackingBean<QTDetailManagedBean>{
	private static final Logger log=EpLogger.getLogger(QTDeductionPaymentBackingBean.class);
		
		public QTDeductionPaymentBackingBean(){
			
		
	}
		
		 public void addDedcutionPayment(){
    		 List<ScQtDeduction> deduction=this.getManagedBean().getScQtDeductions();
    		 if(deduction == null){
    			 deduction= new ArrayList<ScQtDeduction>();
    			 
    		 }
    		this.getManagedBean().setQtDeduction(null);
    		ScQtDeduction qtDeduction=new ScQtDeduction();
    		
    		this.getManagedBean().setQtDeduction(qtDeduction);
    		deduction.add(this.getManagedBean().getQtDeduction());
    		this.getManagedBean().setScQtDeductions(deduction);
    
    		 
    		} 
       
      

}

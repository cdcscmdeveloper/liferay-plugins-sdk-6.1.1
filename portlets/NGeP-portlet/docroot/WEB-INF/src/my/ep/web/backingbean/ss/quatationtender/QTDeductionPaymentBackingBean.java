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
import my.ep.persistence.entity.ss.quationtender.ScQt;
import my.ep.persistence.entity.ss.quationtender.ScQtContactPerson;
import my.ep.persistence.entity.ss.quationtender.ScQtDeduction;
import my.ep.persistence.entity.ss.quationtender.ScQtDeductionClause;
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
			
			ScQtDeduction qtDeduction= new ScQtDeduction();
			
		   
	}
		
		 public void addDedcutionPayment(){
    		 List<ScQtDeduction> deduction=this.getManagedBean().getScQtDeductions();
    		 if(deduction == null){
    			 deduction= new ArrayList<ScQtDeduction>();
    			 
    		 }
    		this.getManagedBean().setQtDeduction(null);
    		ScQtDeduction qtDeduction=new ScQtDeduction();
    		qtDeduction.setScQt(new ScQt());
    		this.getManagedBean().setQtDeduction(qtDeduction);
    		deduction.add(this.getManagedBean().getQtDeduction());
    		this.getManagedBean().setScQtDeductions(deduction);
    
    		 
    		} 
		 
		 public void addDedcutionClause(){
    		 List<ScQtDeductionClause> clauses=this.getManagedBean().getScQtDeductionClauses();
    		 if(clauses == null){
    			clauses= new ArrayList<ScQtDeductionClause>();
    			 
    		 }
    		this.getManagedBean().setQtDeductionClause(null);
    		ScQtDeductionClause qtDeductionClause=new ScQtDeductionClause();
    		
    		this.getManagedBean().setQtDeductionClause(qtDeductionClause);
    		clauses.add(this.getManagedBean().getQtDeductionClause());
    		this.getManagedBean().setScQtDeductionClauses(clauses);
    
    		 
    		} 
    
       
      

}

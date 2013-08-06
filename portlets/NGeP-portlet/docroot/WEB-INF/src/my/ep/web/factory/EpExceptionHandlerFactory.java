/**
 * 
 */
package my.ep.web.factory;

import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerFactory;

import my.ep.web.exceptionhandler.EpExceptionHandler;

/**
 * @author siew mei
 *
 */
public class EpExceptionHandlerFactory extends ExceptionHandlerFactory{
	 private ExceptionHandlerFactory parent;

	 public EpExceptionHandlerFactory(ExceptionHandlerFactory factory){
		 this.parent = factory;
	 }
	 
	 
	@Override
	public ExceptionHandler getExceptionHandler() {
		// TODO Auto-generated method stub
		return new EpExceptionHandler(parent.getExceptionHandler());
	}

}

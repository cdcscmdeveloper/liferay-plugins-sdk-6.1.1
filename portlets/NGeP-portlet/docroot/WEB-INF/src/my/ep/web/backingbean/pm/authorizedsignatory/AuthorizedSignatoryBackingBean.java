package my.ep.web.backingbean.pm.authorizedsignatory;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.pm.profcertbody.ProfCertifiedBodyManagedBean;

/**
 * @author DK
 * 
 */
@ManagedBean(name = "authorizedsignatoryBackingBean")
@RequestScoped
public class AuthorizedSignatoryBackingBean extends
		BaseBackingBean<ProfCertifiedBodyManagedBean> {


}

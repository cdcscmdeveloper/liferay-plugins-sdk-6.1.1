package my.ep.web.backingbean.common;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean(name = "tokenSessionMB")
@SessionScoped
public class TokenSessionBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String sessToken;

	public String getSessToken() {
		return sessToken;
	}

	public void setSessToken(String sessToken) {
		this.sessToken = sessToken;
	}

}

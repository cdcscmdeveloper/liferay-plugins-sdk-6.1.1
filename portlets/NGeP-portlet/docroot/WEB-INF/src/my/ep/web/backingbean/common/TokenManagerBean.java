package my.ep.web.backingbean.common;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import my.ep.web.util.TokenManager;

@ManagedBean(name = "tokenManagerMB")
@RequestScoped
public class TokenManagerBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String getToken() {
		return TokenManager.generateToken();
	}

	public void setToken(String token) {
		// Intentionally left blank - token is generated server-side
	}
}

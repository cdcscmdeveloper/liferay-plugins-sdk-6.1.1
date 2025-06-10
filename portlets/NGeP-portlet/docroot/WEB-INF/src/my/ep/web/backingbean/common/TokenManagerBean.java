package my.ep.web.backingbean.common;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import my.ep.web.util.TokenManager;

@ManagedBean(name = "tokenManagerMB")
@ViewScoped
public class TokenManagerBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String token;

	@PostConstruct
	public void init() {
		// Generate a token when the bean is initialized (i.e., when the view is
		// first created)
		// This ensures the token is generated only once per view load.
		this.token = TokenManager.generateToken();
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

}

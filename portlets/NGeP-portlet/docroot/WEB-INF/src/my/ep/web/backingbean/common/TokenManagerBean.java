package my.ep.web.backingbean.common;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;

import my.ep.web.phaselistener.TokenPhaseListener;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.TokenManager;

import org.apache.log4j.Logger;

@ManagedBean(name = "tokenManagerMB")
@ViewScoped
public class TokenManagerBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(TokenPhaseListener.class);

	@ManagedProperty(value = "#{tokenSessionMB}")
	private TokenSessionBean tokenSessionBean;

	public void setTokenSessionBean(TokenSessionBean tokenSessionBean) {
		this.tokenSessionBean = tokenSessionBean;
	}

	private String token;

	@PostConstruct
	public void init() {
		// Generate a token when the bean is initialized (i.e., when the view is
		// first created)
		// This ensures the token is generated only once per view load.
		TokenManager tokenManager = new TokenManager();
		String currTokenSession = tokenManager.getTokenSession();
		String currTokenSessionBean = tokenSessionBean == null ? "N/A"
				: tokenSessionBean.getSessToken();
		this.token = tokenManager.generateToken();
		tokenSessionBean.setSessToken(token);
		log.info("#38229 post construct hidden token:"
				+ tokenManager.getPartialToken(token) + "  session token:"
				+ tokenManager.getPartialToken(tokenManager.getTokenSession())
				+ " current sess token:"
				+ tokenManager.getPartialToken(currTokenSession)
				+ " sess bean:"
				+ tokenManager.getPartialToken(currTokenSessionBean)
				+ "  loginId:" + new FacesScopes().getUserScreenName());
	}

	public void initAjax(ActionEvent event) {
		log.info("#38229 ajax token:" + token);
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

}

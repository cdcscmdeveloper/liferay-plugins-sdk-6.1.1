package my.ep.web.backingbean.common;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import my.ep.web.util.TokenManager;

@ManagedBean(name = "tokenManagerMB")
@ViewScoped // Use ViewScoped for state that persists across AJAX requests for the same view
            // If you only have full page submits, RequestScoped might be sufficient,
            // but ViewScoped offers better control over the token's lifecycle for a specific view.
public class TokenManagerBean implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String token;

    @PostConstruct
    public void init() {
        // Generate a token when the bean is initialized (i.e., when the view is first created)
        // This ensures the token is generated only once per view load.
        this.token = TokenManager.generateTokenForNewView();
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        // We don't set the token from the outside; it's managed internally.
        // This setter can remain, but it won't be used to change the token.
    }
}

package com.fis.is.terminy.configuration;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        saveException(request, exception);
        String loginWithHash = request.getHeader("referer");
        if (loginWithHash != null && !loginWithHash.isEmpty() && !checkIfAlreadyRedirected(loginWithHash)) {
            loginWithHash += "?error=true";
        }
        if (isUseForward()) {
            this.logger.debug("Forwarding to " + loginWithHash);
            request.getRequestDispatcher(loginWithHash).forward(request, response);
        } else {
            this.logger.debug("Redirecting to " + loginWithHash);
            getRedirectStrategy().sendRedirect(request, response, loginWithHash);
        }
    }

    private boolean checkIfAlreadyRedirected(String url) {
        return url.endsWith("?error=true");
    }
}

package account.security;

import account.entity.Group;
import account.entity.SecurityLogs;
import account.entity.User;
import account.exceptions.CustomNotFoundException;
import account.service.LoggerService;
import account.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
public class AuthenticationFailureEventListener implements ApplicationListener<AuthenticationFailureBadCredentialsEvent> {

    @Autowired
    UserServiceImpl userService;
    @Autowired
    LoggerService logger;
    @Autowired
    private HttpServletRequest request;

    @Override
    public void onApplicationEvent (AuthenticationFailureBadCredentialsEvent event) {
        try {
            String username = event.getAuthentication().getName();
            logger.saveEvent(new SecurityLogs("LOGIN_FAILED", username, request.getServletPath(), request.getServletPath()));
            User user = userService.findByUserName(username.toLowerCase());
            if (user != null) {
                boolean isAdmin = false;
                if (user.isEnabled() && user.isAccountNonLocked()) {
                    for (Group group : user.getUserGroups()) {
                        if (group.getCode().equalsIgnoreCase("ROLE_ADMINISTRATOR")) {
                            isAdmin = true;
                        }
                    }

                    if (user.getFailedAttempt() < 4) {
                        userService.increaseFailedAttempts(username);
                    } else if (!isAdmin){
                        logger.saveEvent(new SecurityLogs("BRUTE_FORCE", username, request.getServletPath(), request.getServletPath()));
                        userService.lock(username);
                        logger.saveEvent(new SecurityLogs("LOCK_USER", username,
                                "Lock user " + user.getUsername().toLowerCase(),
                                request.getServletPath()));
                    }
                }
            }
        } catch (
                CustomNotFoundException e) {
            //
        }
    }

}
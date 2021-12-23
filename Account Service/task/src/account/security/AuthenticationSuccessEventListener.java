
package account.security;

import account.entity.User;
import account.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationSuccessEventListener implements ApplicationListener<AuthenticationSuccessEvent>
{

    @Autowired
    UserServiceImpl userService;

    @Override
    public void onApplicationEvent (AuthenticationSuccessEvent event) {
        String username = event.getAuthentication().getName();
        User user = userService.findByUserName(username);
        if (user != null) {
            userService.resetFailedAttempts(user.getUsername());
        }
    }
}
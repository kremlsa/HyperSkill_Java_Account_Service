package account.cfg;

import account.entity.SecurityLogs;
import account.security.RestAuthenticationEntryPoint;
import account.service.LoggerService;
import account.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    private final BCryptPasswordEncoder encoder;
    private final UserServiceImpl userService;
    private final CustomAccessDeniedHandler customAccessDeniedHandler = new CustomAccessDeniedHandler();
    private final LoggerService logger;

    @Autowired
    public WebSecurityConfig(BCryptPasswordEncoder encoder,
                             UserServiceImpl userService, LoggerService logger) {
        this.encoder = encoder;
        this.userService = userService;
        this.logger = logger;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(encoder);
    }

    public void configure(HttpSecurity http) throws Exception {
        http.httpBasic()
                .authenticationEntryPoint(new RestAuthenticationEntryPoint())
                .and()
                .csrf().disable().headers().frameOptions().disable()
                .and()
                .exceptionHandling()
//                .authenticationEntryPoint(new RestAuthenticationEntryPoint())
                .accessDeniedHandler(customAccessDeniedHandler)
                .and()
                .authorizeRequests()
                .antMatchers("/api/security/events/**")
                .hasAnyAuthority("ROLE_AUDITOR")
                .antMatchers( "/api/auth/changepass/**")
                .hasAnyAuthority("ROLE_USER", "ROLE_ACCOUNTANT", "ROLE_ADMINISTRATOR")
                .antMatchers("/api/empl/payment/**")
                .hasAnyAuthority("ROLE_USER", "ROLE_ACCOUNTANT")
                .antMatchers("/api/admin/user/**")
                .hasAnyAuthority("ROLE_ADMINISTRATOR")
                .antMatchers("/api/acct/payments/**")
                .hasAnyAuthority("ROLE_ACCOUNTANT")
//                .authenticated()
                .antMatchers(HttpMethod.POST, "/api/auth/signup").permitAll()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    public class CustomAccessDeniedHandler implements AccessDeniedHandler {

        @Override
        public void handle(
                HttpServletRequest request, HttpServletResponse response,
                AccessDeniedException exc) throws IOException, ServletException {
            logger.saveEvent(new SecurityLogs("ACCESS_DENIED", request.getRemoteUser(),
                    request.getServletPath(), request.getServletPath()));
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied!");
        }
    }
}
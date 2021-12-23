package account.controller;

import account.dto.UserDTO;
import account.entity.SecurityLogs;
import account.entity.User;
import account.payload.ChangeRequest;
import account.payload.ChangeResponse;
import account.service.LoggerService;
import account.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserServiceImpl userService;
    private LoggerService logger;

    @Autowired
    public AuthController(UserServiceImpl service, LoggerService logger) {
        this.userService = service;
        this.logger = logger;
    }


    @PostMapping(path = "/signup", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDTO> registerNewUser(@Valid @RequestBody UserDTO userDTO) {
        Long userId = userService.registerNewUser(userDTO.getName(), userDTO.getLastname(), userDTO.getEmail(), userDTO.getPassword());
        User user = userService.findByUserName(userDTO.getEmail());
        userDTO.setId(userId);
        userDTO.setRoles(user.getUserGroups().stream().map(x -> x.getCode()).sorted().collect(Collectors.toList()));
        logger.saveEvent(new SecurityLogs("CREATE_USER", "Anonymous", userDTO.getEmail().toLowerCase(),
                "/api/auth/signup"));
        return new ResponseEntity<>(userDTO, HttpStatus.OK);
    }

    @PostMapping(path = "/changepass", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<ChangeResponse> updatePassword(@Autowired Principal principal,
                                                         @Valid @RequestBody ChangeRequest passChange) {
        userService.updateUserPassword(principal.getName(), passChange.getNew_password());
        ChangeResponse resp = new ChangeResponse();
        resp.setEmail(principal.getName());
        resp.setStatus("The password has been updated successfully");
        logger.saveEvent(new SecurityLogs("CHANGE_PASSWORD", principal.getName(),
                principal.getName(), "/api/auth/changepass"));
        return new ResponseEntity<>(resp, HttpStatus.OK);
    }

}
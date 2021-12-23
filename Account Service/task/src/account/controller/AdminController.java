package account.controller;

import account.dao.GroupRepository;
import account.dto.UserDTO;
import account.entity.Group;
import account.entity.SecurityLogs;
import account.entity.User;
import account.exceptions.CustomBadRequestException;
import account.exceptions.CustomNotFoundException;
import account.payload.ChangeAccessRequest;
import account.payload.ChangeRoleRequest;
import account.payload.DefaultResponse;
import account.payload.DeleteUserResponse;
import account.service.LoggerService;
import account.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final UserServiceImpl userService;
    private final GroupRepository groupRepository;
    private final LoggerService logger;
    private Set<String> SERVICE_USERS = Set.of("ROLE_ADMINISTRATOR");
    private Set<String> BUSINESS_USERS = Set.of("ROLE_ACCOUNTANT", "ROLE_USER", "ROLE_AUDITOR");

    @Autowired
    public AdminController(UserServiceImpl service, GroupRepository groupRepository, LoggerService logger) {
        this.groupRepository = groupRepository;
        this.userService = service;
        this.logger = logger;
    }

    @GetMapping("/user")
    public ResponseEntity<Object> getAllUsers() {
        List<UserDTO> userDTOList = new ArrayList<>();
        for (User user : userService.findAllUsers()) {
            userDTOList.add(new UserDTO(user.getId(), user.getName(), user.getLastname(), user.getUsername(),
                    user.getUserGroups().stream().map(x -> x.getCode()).sorted().collect(Collectors.toList()))
            );
        }
        return new ResponseEntity<>(userDTOList, HttpStatus.OK);
    }

    @DeleteMapping("/user/{username}")
    public ResponseEntity<Object> deleteUser(@PathVariable String username, @Autowired Principal principal) {
        User user = userService.findByUserName(username);
        if (user == null) {
            throw new CustomNotFoundException("User not found!");
        }
        for (Group group : user.getUserGroups()) {
            if (group.getCode().equalsIgnoreCase("ROLE_ADMINISTRATOR")) {
                throw new CustomBadRequestException("Can't remove ADMINISTRATOR role!");
            }
        }
        userService.deleteUser(user.getId());
        logger.saveEvent(new SecurityLogs("DELETE_USER", principal.getName(),
                user.getUsername().toLowerCase(), "/api/admin/user"));
        return new ResponseEntity<>(new DeleteUserResponse(user.getUsername(), "Deleted successfully!"),
                HttpStatus.OK);
    }

    @PutMapping(path = "/user/role")
    public ResponseEntity<Object> setAcct(@Autowired Principal principal, @RequestBody @Valid ChangeRoleRequest req) {

        Group groupReq = groupRepository.findByCode("ROLE_" + req.getRole().toUpperCase());
        if (groupReq == null) {
            throw new CustomNotFoundException("Role not found!");
        }
        User user = userService.findByUserName(req.getUser());
        if (user == null) {
            throw new CustomNotFoundException("User not found!");
        }
        String operation = req.getOperation().toUpperCase();
        boolean isService = false;
        boolean isBusiness = false;
        switch (operation) {
            case "GRANT":
                if (user.getUserGroups().contains(groupRepository.findByCode("ROLE_" + req.getRole().toUpperCase()))) {
                    throw new CustomBadRequestException("Role already granted!");
                }
                user.addUserGroups(groupReq);
                for (Group group : user.getUserGroups()) {
                    if (SERVICE_USERS.contains(group.getCode())) isService = true;
                    if (BUSINESS_USERS.contains(group.getCode())) isBusiness = true;
                }
                if (isBusiness &&  isService) {
                    throw new CustomBadRequestException("The user cannot combine administrative and business roles!");
                } else {
                    logger.saveEvent(new SecurityLogs("GRANT_ROLE", principal.getName(),
                            "Grant role " + req.getRole() + " to " + user.getUsername().toLowerCase(),
                            "/api/admin/user/role"));
                    userService.save(user);
                }
                break;
            case "REMOVE":
                if (!user.getUserGroups().contains(groupRepository.findByCode("ROLE_" + req.getRole().toUpperCase()))) {
                    throw new CustomBadRequestException("The user does not have a role!");
                }
                if (req.getRole().equalsIgnoreCase("ADMINISTRATOR")) {
                    throw new CustomBadRequestException("Can't remove ADMINISTRATOR role!");
                }
                if (user.getUserGroups().size() == 1) {
                    throw new CustomBadRequestException("The user must have at least one role!");
                }
                user.removeUserGroups(groupRepository.findByCode("ROLE_" + req.getRole().toUpperCase()));
                logger.saveEvent(new SecurityLogs("REMOVE_ROLE", principal.getName(),
                        "Remove role " + req.getRole() + " from " + user.getUsername().toLowerCase(),
                        "/api/admin/user/role"));
                userService.save(user);
                break;
            default:
                throw new CustomNotFoundException("Operation not found!");

        }
        UserDTO userDto = new UserDTO(user.getId(), user.getName(), user.getLastname(), user.getUsername(),
                user.getUserGroups().stream().map(x -> x.getCode()).sorted().collect(Collectors.toList())
        );

        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }


    @PutMapping(path = "/user/access")
    public ResponseEntity<Object> setAcct(@Autowired Principal principal, @RequestBody ChangeAccessRequest req) {

        User user = userService.findByUserName(req.getUser());
        if (user == null) {
            throw new CustomNotFoundException("User not found!");
        }
        DefaultResponse result = new DefaultResponse("Something wrong!");
        String operation = req.getOperation().toUpperCase();
        switch (operation) {
            case "UNLOCK":
                userService.unlock(user.getUsername());
                logger.saveEvent(new SecurityLogs("UNLOCK_USER", principal.getName(),
                        "Unlock user " + user.getUsername().toLowerCase(),
                        "/api/admin/user/access"));
                result = new DefaultResponse("User " + user.getUsername() + " unlocked!");
                break;
            case "LOCK":
                for (Group group : user.getUserGroups()) {
                    if (group.getCode().equalsIgnoreCase("ROLE_ADMINISTRATOR")) {
                        throw new CustomBadRequestException("Can't lock the ADMINISTRATOR!");
                    }
                }
                userService.lock(user.getUsername());
                logger.saveEvent(new SecurityLogs("LOCK_USER", principal.getName(),
                        "Lock user " + user.getUsername().toLowerCase(),
                        "/api/admin/user/access"));
                result = new DefaultResponse("User " + user.getUsername() + " locked!");
                break;
        }

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

}
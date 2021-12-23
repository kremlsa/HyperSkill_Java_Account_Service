package account.service;

import account.dao.GroupRepository;
import account.dao.UserRepository;
import account.entity.User;
import account.exceptions.CustomNotFoundException;
import account.exceptions.PasswordDuplicateException;
import account.exceptions.UserExistException;
import account.security.PasswordCheck;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final BCryptPasswordEncoder encoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, GroupRepository groupRepository, BCryptPasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
        this.encoder = encoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username.toLowerCase())
                .orElseThrow(() -> new UsernameNotFoundException(
                        String.format("No user %s found", username)));
    }



    public User findById(Long userId) throws UsernameNotFoundException{
        return userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException(
                        String.format("No user %s found for id", userId)));
    }

    public User findByUserName(String name) throws UsernameNotFoundException{
        return userRepository.findByUsername(name.toLowerCase())
                .orElseThrow(() -> new CustomNotFoundException("User not found!"));
    }

    public Long registerNewUser(String name, String lastname, String username, String password) {
        PasswordCheck.checkPassword(password);
        String encodedPassword = encoder.encode(password);
        User user = new User(name, lastname, username.toLowerCase(), encodedPassword);
        if (findAllUsers().size() == 0) {
            user.addUserGroups(groupRepository.findByCode("ROLE_ADMINISTRATOR"));
        } else {
            user.addUserGroups(groupRepository.findByCode("ROLE_USER"));
        }
        try {
            return userRepository.save(user).getId();
        } catch (DataIntegrityViolationException e) {
            throw new UserExistException();
        }
    }

    public void updateUserPassword(String username, String new_password) {
        User user = findByUserName(username);
        if (encoder.matches(new_password, user.getPassword())) {
            throw new PasswordDuplicateException();
        }
        PasswordCheck.checkPassword(new_password);
        String encodedPassword = encoder.encode(new_password);
        user.setPassword(encodedPassword);
        try {
            userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new UserExistException();
        }
    }

    public List<User> findAllUsers() throws UsernameNotFoundException {
        return userRepository.findAllByOrderByIdAsc()
                .orElse(new ArrayList<>());
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public void save(User user) {
        userRepository.save(user);
    }

    public void increaseFailedAttempts(String username) {
        User user = findByUserName(username.toLowerCase());
        user.setFailedAttempt(user.getFailedAttempt() + 1);
        userRepository.save(user);
    }

    public void resetFailedAttempts(String username) {
        User user = findByUserName(username.toLowerCase());
        user.setFailedAttempt(0);
        userRepository.save(user);
    }

    public void lock(String username) {
        User user = findByUserName(username.toLowerCase());
        user.setAccountNonLocked(false);
        userRepository.save(user);
    }

    public void unlock(String username) {
        User user = findByUserName(username.toLowerCase());
        user.setAccountNonLocked(true);
        user.setFailedAttempt(0);
        userRepository.save(user);
    }
}
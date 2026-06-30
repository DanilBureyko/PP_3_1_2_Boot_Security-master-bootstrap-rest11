package ru.kata.spring.boot_security.demo.services;

import org.springframework.context.annotation.Bean;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.kata.spring.boot_security.demo.models.Role;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.repositories.RoleRepository;
import ru.kata.spring.boot_security.demo.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Transactional
@Service
public class UserServiceImp implements UserDetailsService, UserService {

    @PersistenceContext
    private EntityManager em;

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserServiceImp(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
        for (Role role : user.getRoles()) {
            grantedAuthorities.add(new SimpleGrantedAuthority(role.getName()));
        }
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), grantedAuthorities);
    }

    public User findUserByName(String username) {
        return userRepository.findByUsername(username);
    }

    public User findUserById(Long userId) {
        return userRepository.findById(userId).orElse(null);
    }

    public List<User> allUsers() {
        return userRepository.findAll();
    }

    @Transactional
    public boolean saveUser(User user) {
        if (user.getId() != null && !userRepository.existsById(user.getId())) {
            return false;
        }

        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            if (user.getId() != null) {
                userRepository.findById(user.getId()).ifPresent(userFromDb -> user.setPassword(userFromDb.getPassword()));
            }
        } else {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        if (user.getRoles() != null && !user.getRoles().isEmpty()) {
            List<Long> roleIds = user.getRoles().stream()
                    .map(Role::getId)
                    .collect(Collectors.toList());
            user.setRoles(new HashSet<>(roleRepository.findAllById(roleIds)));
        }

        userRepository.save(user);
        return true;
    }

    @Bean
    public BCryptPasswordEncoder bbCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Transactional
    public boolean deleteUser(Long userId) {
        if (userRepository.findById(userId).isPresent()) {
            userRepository.deleteById(userId);
            return true;
        }
        return false;
    }

    public List<User> usergtList(Long idMin) {
        return em.createQuery("SELECT u FROM User u WHERE u.id > :paramId", User.class)
                .setParameter("paramId", idMin).getResultList();
    }

    public List<Role> listRoles() {
        return roleRepository.findAll();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return em.createQuery("select u from User u where u.email =:email", User.class)
                .setParameter("email", email)
                .getResultStream().findAny();
    }

}

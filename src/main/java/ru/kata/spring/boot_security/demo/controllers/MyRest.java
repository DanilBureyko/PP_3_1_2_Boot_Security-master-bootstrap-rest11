package ru.kata.spring.boot_security.demo.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.models.Role;
import ru.kata.spring.boot_security.demo.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import ru.kata.spring.boot_security.demo.services.UserServiceImp;

import java.util.List;

@CrossOrigin("*")
@RestController
public class MyRest {
    private final UserServiceImp userService;

    @Autowired
    private StringHttpMessageConverter stringHttpMessageConverter;

    @Autowired
    private MyRest(UserServiceImp userService) {
        this.userService = userService;
    }

    @GetMapping(value = "/clients")
    public ResponseEntity<List<User>> read() {
        final List<User> users = userService.allUsers();

        return users != null &&  !users.isEmpty()
                ? new ResponseEntity<>(users, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping(value = "/roles")
    public ResponseEntity<List<Role>> readroles() {
        final List<Role> roles = userService.listRoles();

        return roles != null &&  !roles.isEmpty()
                ? new ResponseEntity<>(roles, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping(value = "/client")
    public ResponseEntity<?> create(@RequestBody User model) {
        userService.saveUser(model);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping(value = "/authclient")
    public ResponseEntity<User> authclient() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String us=authentication.getName();
        User user = userService.findUserByName(us);
        return user != null
                ? new ResponseEntity<>(user, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping(value = "/client/{id}")
    public ResponseEntity<User> read(@PathVariable(name = "id") Long id) {
        final User client = userService.findUserById(id);

        return client != null
                ? new ResponseEntity<>(client, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping(value = "/client/{id}", consumes = {"application/json"})
    public ResponseEntity<?> update(@PathVariable(name = "id") Long id,  @RequestBody User model) {
        final boolean updated = userService.saveUser(model);

        return updated
                ? new ResponseEntity<>(HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
    }

    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable(name = "id") Long id) {
        final boolean deleted = userService.deleteUser(id);

        return deleted
                ? new ResponseEntity<>(HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
    }
}

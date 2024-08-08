package backend.controller;

import backend.entity.User;
import backend.repository.UserRepository;
import backend.security.JwtUtil;
import backend.service.CustomUserDetailService;
import backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("api/auth")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtTUtil;

    @Autowired
    private CustomUserDetailService customUserDetailService;

    @Autowired
    private UserRepository userRepository;


    @PostMapping("/register")
    public String registerUser(@RequestBody User user) {
        try {
            userService.registerUser(user);
            return "User registered successfully. Please check your email";
        } catch (IllegalArgumentException e) {
            return e.getMessage();
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username);
        if (user == null) {
            throw new UsernameNotFoundException(username);
        }
        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), new ArrayList<>());
    }

    @PostMapping("/login")
    public String loginUser(@RequestBody User user) {
        try {

            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()));

            final CustomUserDetailService customUserDetailService = userService.loadUserByUsername(user.getEmail());

            final String jwt = jwtTUtil.generateToken(userDetails.getUsername());

            return jwt;
        } catch (Exception e) {
            return "Invalid username or password";
        }
    }
}
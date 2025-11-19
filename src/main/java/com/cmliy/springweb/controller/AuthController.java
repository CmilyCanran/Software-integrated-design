package com.cmliy.springweb.controller;

import com.cmliy.springweb.model.User;
import com.cmliy.springweb.repository.UserRepository;
import com.cmliy.springweb.util.JwtUtil;
import com.cmliy.springweb.security.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> loginRequest) {
        try {
            String username = loginRequest.get("username");
            String password = loginRequest.get("password");

            // 执行认证
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
            );

            // 设置安全上下文
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 获取用户详情
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            String token = jwtUtil.generateToken(userDetails);

            // 获取用户信息
            Optional<User> userOpt = userRepository.findByUsername(username);
            User user = userOpt.orElse(null);

            Map<String, Object> response = new HashMap<>();
            response.put("status", 200);
            response.put("message", "登录成功");
            response.put("token", token);
            response.put("tokenType", "Bearer");
            response.put("expiresIn", jwtUtil.getExpiration());

            if (user != null) {
                response.put("user", Map.of(
                    "id", user.getId(),
                    "username", user.getUsername(),
                    "email", user.getEmail(),
                    "role", user.getRole()
                ));
            }

            response.put("timestamp", LocalDateTime.now().toString());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", 401);
            errorResponse.put("error", "Unauthorized");
            errorResponse.put("message", "用户名或密码错误");
            errorResponse.put("timestamp", LocalDateTime.now().toString());

            return ResponseEntity.status(401).body(errorResponse);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody Map<String, String> registerRequest) {
        try {
            String username = registerRequest.get("username");
            String email = registerRequest.get("email");
            String password = registerRequest.get("password");

            // 检查用户是否已存在
            if (userRepository.existsByUsername(username)) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("status", 400);
                errorResponse.put("error", "Bad Request");
                errorResponse.put("message", "用户名已存在");
                errorResponse.put("timestamp", LocalDateTime.now().toString());
                return ResponseEntity.badRequest().body(errorResponse);
            }

            if (userRepository.existsByEmail(email)) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("status", 400);
                errorResponse.put("error", "Bad Request");
                errorResponse.put("message", "邮箱已存在");
                errorResponse.put("timestamp", LocalDateTime.now().toString());
                return ResponseEntity.badRequest().body(errorResponse);
            }

            // 创建新用户
            User user = new User();
            user.setUsername(username);
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(password));
            user.setRole("USER");
            user.setEnabled(true);

            userRepository.save(user);

            Map<String, Object> response = new HashMap<>();
            response.put("status", 201);
            response.put("message", "注册成功");
            response.put("timestamp", LocalDateTime.now().toString());

            return ResponseEntity.status(201).body(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", 500);
            errorResponse.put("error", "Internal Server Error");
            errorResponse.put("message", "注册失败: " + e.getMessage());
            errorResponse.put("timestamp", LocalDateTime.now().toString());

            return ResponseEntity.status(500).body(errorResponse);
        }
    }
}
package com.morning.security.auth;

import com.morning.security.config.security.JwtEmailService;
import com.morning.security.config.security.JwtService;

import com.morning.security.emailSender.KafkaMailProducer;
import com.morning.security.token.Token;
import com.morning.security.token.TokenRepository;
import com.morning.security.token.TokenType;
import com.morning.security.user.User;
import com.morning.security.user.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
  private final UserRepository repository;
  private final TokenRepository tokenRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final AuthenticationManager authenticationManager;
  private final JwtEmailService jwtEmailService;
  private final KafkaMailProducer kafkaMailProducer;

  @Value("${application.task-api.db-url}")
  private String dbUrl;

  public AuthenticationResponse register(RegisterRequest request) throws SQLException {
    var user = User.builder()
        .username(request.getUsername())
        .email(request.getEmail())
        .password(passwordEncoder.encode(request.getPassword()))
        .role(request.getRole())
        .verified(false)
        .build();
    var savedUser = repository.save(user);
    if(request.getEmail() != null){
      kafkaMailProducer.sendMailNotification(request.getUsername(), request.getEmail());
    }
    Connection connection = DriverManager.getConnection(dbUrl,
            "postgres", "root");
    String insertQuery = "INSERT INTO \"app_user\" (username, created_at, updated_at, status ) VALUES (?, ?, ?, ?)";
    PreparedStatement statement = connection.prepareStatement(insertQuery);

    statement.setString(1, request.getUsername());
    statement.setDate(2, new java.sql.Date(Calendar.getInstance().getTime().getTime()));
    statement.setDate(3,  new java.sql.Date(Calendar.getInstance().getTime().getTime()));
    statement.setString(4, "ACTIVE");

    int count = statement.executeUpdate();

    if (count != 1) {
      repository.delete(user);
      throw new SQLException(("User") + " was not added");
    }
    var jwtToken = jwtService.generateToken(Map.of("role", "USER"),user);
    var refreshToken = jwtService.generateRefreshToken(user);
    saveUserToken(savedUser, jwtToken);
    return AuthenticationResponse.builder()
        .accessToken(jwtToken)
            .refreshToken(refreshToken)
        .build();
  }

  public AuthenticationResponse authenticate(AuthenticationRequest request) {
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            request.getUsername(),
            request.getPassword()
        )
    );
    var user = repository.findByUsername(request.getUsername())
        .orElseThrow();
    var jwtToken = jwtService.generateToken(Map.of("role", user.getRole()),user);
    var refreshToken = jwtService.generateRefreshToken(user);
    revokeAllUserTokens(user);
    saveUserToken(user, jwtToken);
    return AuthenticationResponse.builder()
        .accessToken(jwtToken)
            .refreshToken(refreshToken)
        .build();
  }

  private void saveUserToken(User user, String jwtToken) {
    var token = Token.builder()
        .user(user)
        .token(jwtToken)
        .tokenType(TokenType.BEARER)
        .expired(false)
        .revoked(false)
        .build();
    tokenRepository.save(token);
  }

  private void revokeAllUserTokens(User user) {
    var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
    if (validUserTokens.isEmpty())
      return;
    validUserTokens.forEach(token -> {
      token.setExpired(true);
      token.setRevoked(true);
    });
    tokenRepository.saveAll(validUserTokens);
  }

  public void refreshToken(
          HttpServletRequest request,
          HttpServletResponse response
  ) throws IOException {
    final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
    final String refreshToken;
    final String username;
    if (authHeader == null ||!authHeader.startsWith("Bearer ")) {
      return;
    }
    refreshToken = authHeader.substring(7);
    username = jwtService.extractUsername(refreshToken);
    if (username != null) {
      var user = this.repository.findByUsername(username)
              .orElseThrow();
      if (jwtService.isTokenValid(refreshToken, user)) {
        var accessToken = jwtService.generateToken(Map.of("role", user.getRole()),user);
        revokeAllUserTokens(user);
        saveUserToken(user, accessToken);
        var authResponse = AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
        new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
      }
    }
  }

  public void verifyEmail(String token, String emailToken){
    token = token.substring(7);
    User user = repository.findByUsername(jwtService.extractUsername(token)).orElseThrow();

    if(jwtEmailService.isTokenValid(emailToken, jwtService.extractUsername(token)) &&
      jwtEmailService.extractEmail(emailToken).equals(user.getEmail())){
      user.setVerified(true);
      repository.save(user);
    } else {
      System.out.println("Not uraaa");
    }

  }
}

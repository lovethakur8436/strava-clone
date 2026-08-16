package com.fitness.identity;

import com.fitness.common.security.JwtService;
import com.fitness.identity.dto.AuthResponse;
import com.fitness.identity.dto.LoginRequest;
import com.fitness.identity.dto.UserRegistrationRequest;
import com.fitness.profile.Profile;
import com.fitness.profile.ProfileRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository,
            ProfileRepository profileRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    // @Transactional ensures if profile creation fails, the user is rolled back
    // automatically.
    @Transactional
    public AuthResponse register(UserRegistrationRequest request) {
        // 1. Create the User (Identity)
        User user = User.builder()
                .email(request.email())
                .passwordHash(passwordEncoder.encode(request.password()))
                .build();

        user = userRepository.save(user);

        // 2. Create the Profile (Social Domain) using the generated User ID
        Profile profile = Profile.builder()
                .userId(user.getId()) // 1-to-1 strict mapping
                .firstName(request.firstName())
                .lastName(request.lastName())
                .build();

        profileRepository.save(profile);

        // 3. Generate JWT and return
        String token = jwtService.generateToken(user.getId().toString());
        return new AuthResponse(token);
    }

    public AuthResponse login(LoginRequest request) {
        // 1. Find user by email
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("Invalid email or password")); // We'll handle this properly
                                                                                       // with a Global Handler soon

        // 2. Verify password (constant-time comparison)
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid email or password"); // Do NOT say "Invalid password". That tells
                                                                     // hackers the email exists!
        }

        // 3. Generate and return JWT
        String token = jwtService.generateToken(user.getId().toString());
        return new AuthResponse(token);
    }
}
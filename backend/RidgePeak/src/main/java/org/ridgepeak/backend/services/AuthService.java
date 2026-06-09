package org.ridgepeak.backend.services;

import org.mindrot.jbcrypt.BCrypt;
import org.ridgepeak.backend.dtos.AuthLoginRequest;
import org.ridgepeak.backend.dtos.AuthRegisterRequest;
import org.ridgepeak.backend.models.User;
import org.ridgepeak.backend.repositories.UserRepository;
import org.ridgepeak.backend.exceptions.UnauthorizedException;
import org.ridgepeak.backend.exceptions.BizException;
import org.ridgepeak.backend.utils.JwtUtil;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
public class AuthService {
    private final UserRepository userRepository;

    private static final Pattern EMAIL = Pattern.compile("^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$");
//    private static final Pattern USERNAME = Pattern.compile("^[a-zA-Z0-9_]{3,30}$");
//    private static final Pattern PASSWORD = Pattern.compile("^[a-zA-Z0-9@#$%^&*]{8,30}$");

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String register(AuthRegisterRequest request) {
//        if (!USERNAME.matcher(request.username()).matches())
//            throw new BizException("用户名必须是：3-30位字符，只允许数字、大小写字母、下划线");
//        if (!PASSWORD.matcher(request.password()).matches())
//            throw new BizException("密码必须是：8-30位字符，只允许数字、大小写字母、以及 @ # $ % ^ & * 字符");
//        if (!EMAIL.matcher(request.email()).matches())
//            throw new BizException("无效的电子邮件格式");

        if (userRepository.existsByUsername(request.username()))
            throw new BizException("用户名已存在");
        if (userRepository.existsByEmail(request.email()))
            throw new BizException("邮箱已被注册");

        String hashed = BCrypt.hashpw(request.password(), BCrypt.gensalt());
        String tokenId = UUID.randomUUID().toString();

        User user = new User();
        user.setUsername(request.username());
        user.setPassword(hashed);
        user.setEmail(request.email());
        user.setTokenIdentifier(tokenId);
        userRepository.save(user);

        return JwtUtil.generate(user.getId(), tokenId);
    }

    public String login(AuthLoginRequest request) {
        User user;

        if (EMAIL.matcher(request.account()).matches()) {
            user = userRepository.findByEmail(request.account())
                    .orElseThrow(() -> new BizException("邮箱未注册"));
        } else {
            user = userRepository.findByUsername(request.account())
                    .orElseThrow(() -> new BizException("用户名不存在"));
        }

        if (!BCrypt.checkpw(request.password(), user.getPassword()))
            throw new BizException("密码错误");

        String tokenId = UUID.randomUUID().toString();
        user.setTokenIdentifier(tokenId);
        user.setLastLoginTime(LocalDateTime.now());
        userRepository.save(user);

        return JwtUtil.generate(user.getId(), tokenId);
    }

    public void logout(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BizException("用户不存在"));

        user.setTokenIdentifier(null);
        userRepository.save(user);
    }

    public void validate(Long userId, String tokenIdentifier) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BizException("用户不存在"));

        if (user.getTokenIdentifier() == null || !user.getTokenIdentifier().equals(tokenIdentifier))
            throw new UnauthorizedException("登录已过期，请重新登录");
    }
}

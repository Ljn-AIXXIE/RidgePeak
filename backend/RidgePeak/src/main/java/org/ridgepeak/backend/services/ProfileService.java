package org.ridgepeak.backend.services;

import org.mindrot.jbcrypt.BCrypt;
import org.ridgepeak.backend.dtos.ProfileInfo;
import org.ridgepeak.backend.dtos.ProfilePasswordRequest;
import org.ridgepeak.backend.dtos.ProfilePutRequest;
import org.ridgepeak.backend.models.User;
import org.ridgepeak.backend.repositories.UserRepository;
import org.ridgepeak.backend.exceptions.BizException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Service
public class ProfileService {
    private final UserRepository userRepository;

    public ProfileService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public ProfileInfo find(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BizException("用户不存在"));

        return new ProfileInfo(
                userId,
                user.getUsername(),
                user.getEmail(),
                user.getNickname(),
                user.getAvatarUrl(),
                user.getRole(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getLastLoginTime()
        );
    }

    @Transactional
    public void update(Long userId, ProfilePutRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BizException("用户不存在"));

        user.setNickname(request.nickname());
        userRepository.save(user);
    }

    @Transactional
    public void changePassword(Long userId, ProfilePasswordRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BizException("用户不存在"));

        if (!BCrypt.checkpw(request.oldPassword(), user.getPassword()))
            throw new BizException("旧密码不正确");

        if (BCrypt.checkpw(request.newPassword(), user.getPassword()))
            throw new BizException("新密码不能与旧密码相同");

        String hashed = BCrypt.hashpw(request.newPassword(), BCrypt.gensalt());
        user.setPassword(hashed);
        userRepository.save(user);
    }

    public String uploadAvatar(Long userId, MultipartFile file) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BizException("用户不存在"));

        if (file.isEmpty())
            throw new BizException("文件为空");

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/"))
            throw new BizException("只允许上传图片文件");
        if (file.getSize() > 2 * 1024 * 1024)
            throw new BizException("图片大小不能超过 2MB");

        String ext = "img";
        String originFilename = file.getOriginalFilename();
        if (originFilename != null && originFilename.contains(".")) {
            ext = originFilename.substring(originFilename.lastIndexOf('.') + 1);
        }

        Path uploadDir = Path.of("uploads/avatars");
        String filename = UUID.randomUUID() + "." + ext;
        Path filePath = uploadDir.resolve(filename);

        try {
            Files.createDirectories(uploadDir);
            file.transferTo(filePath);
        } catch (IOException e) {
            if (Files.exists(filePath)) {
                try {
                    Files.delete(filePath);
                } catch (IOException ignored) {}
            }

            throw new BizException("文件上传失败");
        }

        String url = "/uploads/avatars/" + filename;
        user.setAvatarUrl(url);
        userRepository.save(user);

        return url;
    }
}

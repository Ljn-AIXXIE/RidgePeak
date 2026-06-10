package org.ridgepeak.backend.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.ridgepeak.backend.dtos.ProfileInfo;
import org.ridgepeak.backend.dtos.ProfilePasswordRequest;
import org.ridgepeak.backend.dtos.ProfilePutRequest;
import org.ridgepeak.backend.dtos.ProfileSummaryInfo;
import org.ridgepeak.backend.services.ProfileService;
import org.ridgepeak.backend.utils.Result;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {
    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping("/me")
    public Result<?> get(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        ProfileInfo profileInfo = profileService.find(userId);
        return Result.ok(profileInfo);
    }

    @PutMapping("/me")
    public Result<?> put(HttpServletRequest request, @RequestBody @Valid ProfilePutRequest body) {
        Long userId = (Long) request.getAttribute("userId");
        profileService.update(userId, body);
        return Result.ok();
    }

    @GetMapping("/{userId}")
    public Result<?> getById(@PathVariable Long userId) {
        ProfileInfo profileInfo = profileService.find(userId);
        return Result.ok(new ProfileSummaryInfo(
                profileInfo.userId(),
                profileInfo.username(),
                profileInfo.nickname(),
                profileInfo.avatarUrl(),
                profileInfo.role()
        ));
    }

    @PostMapping("/me/password")
    public Result<?> changePassword(HttpServletRequest request, @RequestBody @Valid ProfilePasswordRequest body) {
        Long userId = (Long) request.getAttribute("userId");
        profileService.changePassword(userId, body);
        return Result.ok();
    }

    @PostMapping("/me/avatar")
    public Result<?> uploadAvatar(HttpServletRequest request, @RequestParam("file") MultipartFile file) {
        Long userId = (Long) request.getAttribute("userId");
        String url = profileService.uploadAvatar(userId, file);
        return Result.ok(url);
    }
}

package kr.co.inntavern.dripking.controller;

import kr.co.inntavern.dripking.dto.request.AdminUserRoleRequestDTO;
import kr.co.inntavern.dripking.dto.request.AdminUserStatusRequestDTO;
import kr.co.inntavern.dripking.dto.response.AdminUserResponseDTO;
import kr.co.inntavern.dripking.security.CustomUserDetails;
import kr.co.inntavern.dripking.service.AdminUserService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {
    private final AdminUserService adminUserService;

    public AdminUserController(AdminUserService adminUserService) {
        this.adminUserService = adminUserService;
    }

    @GetMapping
    public ResponseEntity<Page<AdminUserResponseDTO>> getUsers(
            @RequestParam(required = false, value = "page", defaultValue = "0") int page,
            @RequestParam(required = false, value = "size", defaultValue = "10") int size,
            @RequestParam(required = false, value = "search") String search,
            @RequestParam(required = false, value = "locked") Boolean locked) {
        return ResponseEntity.ok(adminUserService.getUsers(page, size, search, locked));
    }

    @PatchMapping("/{userId}/role")
    public ResponseEntity<AdminUserResponseDTO> updateUserRole(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                               @PathVariable Long userId,
                                                               @RequestBody AdminUserRoleRequestDTO requestDTO) {
        return ResponseEntity.ok(adminUserService.updateUserRole(userId, customUserDetails.getId(), requestDTO));
    }

    @PatchMapping("/{userId}/status")
    public ResponseEntity<AdminUserResponseDTO> updateUserStatus(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                                 @PathVariable Long userId,
                                                                 @RequestBody AdminUserStatusRequestDTO requestDTO) {
        return ResponseEntity.ok(adminUserService.updateUserStatus(userId, customUserDetails.getId(), requestDTO));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                           @PathVariable Long userId) {
        adminUserService.deleteUser(userId, customUserDetails.getId());
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}

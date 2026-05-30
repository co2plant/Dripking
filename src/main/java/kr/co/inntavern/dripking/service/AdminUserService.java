package kr.co.inntavern.dripking.service;

import kr.co.inntavern.dripking.dto.request.AdminUserRoleRequestDTO;
import kr.co.inntavern.dripking.dto.request.AdminUserStatusRequestDTO;
import kr.co.inntavern.dripking.dto.response.AdminUserResponseDTO;
import kr.co.inntavern.dripking.model.Authority;
import kr.co.inntavern.dripking.model.User;
import kr.co.inntavern.dripking.repository.AuthorityRepository;
import kr.co.inntavern.dripking.repository.UserRepository;
import kr.co.inntavern.dripking.security.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;

@Service
public class AdminUserService {
    private final UserRepository userRepository;
    private final AuthorityRepository authorityRepository;

    public AdminUserService(UserRepository userRepository, AuthorityRepository authorityRepository) {
        this.userRepository = userRepository;
        this.authorityRepository = authorityRepository;
    }

    @Transactional(readOnly = true)
    public Page<AdminUserResponseDTO> getUsers(int page, int size, String search, Boolean locked) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        return userRepository.searchUsers(blankToNull(search), locked, pageable).map(this::mapToResponseDTO);
    }

    @Transactional
    public AdminUserResponseDTO updateUserRole(Long targetUserId, Long adminUserId, AdminUserRoleRequestDTO requestDTO) {
        if (requestDTO == null || requestDTO.getRole() == null) {
            throw new IllegalArgumentException("role이 필요합니다.");
        }
        if (targetUserId.equals(adminUserId) && requestDTO.getRole() != UserRole.ADMIN) {
            throw new IllegalArgumentException("자기 자신의 관리자 권한은 제거할 수 없습니다.");
        }

        User user = getUser(targetUserId);
        Authority authority = authorityRepository.findByName(requestDTO.getRole())
                .orElseGet(() -> authorityRepository.save(Authority.builder().name(requestDTO.getRole()).build()));
        user.setRoles(new HashSet<>());
        user.getRoles().add(authority);
        return mapToResponseDTO(userRepository.save(user));
    }

    @Transactional
    public AdminUserResponseDTO updateUserStatus(Long targetUserId, Long adminUserId, AdminUserStatusRequestDTO requestDTO) {
        if (requestDTO == null || requestDTO.getLocked() == null) {
            throw new IllegalArgumentException("locked 값이 필요합니다.");
        }
        if (targetUserId.equals(adminUserId) && requestDTO.getLocked()) {
            throw new IllegalArgumentException("자기 자신의 관리자 계정은 잠글 수 없습니다.");
        }

        User user = getUser(targetUserId);
        user.setLocked(requestDTO.getLocked());
        return mapToResponseDTO(userRepository.save(user));
    }

    @Transactional
    public void deleteUser(Long targetUserId, Long adminUserId) {
        if (targetUserId.equals(adminUserId)) {
            throw new IllegalArgumentException("자기 자신의 관리자 계정은 삭제할 수 없습니다.");
        }
        User user = getUser(targetUserId);
        userRepository.delete(user);
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 유저가 존재하지 않습니다."));
    }

    private AdminUserResponseDTO mapToResponseDTO(User user) {
        AdminUserResponseDTO responseDTO = new AdminUserResponseDTO();
        responseDTO.setId(user.getId());
        responseDTO.setEmail(user.getEmail());
        responseDTO.setNickname(user.getNickname());
        responseDTO.setRoles(user.getRoles().stream()
                .map(role -> role.getName().getValue())
                .toList());
        responseDTO.setLocked(user.isLocked());
        responseDTO.setEmailVerified(user.isEmailVerified());
        responseDTO.setPhoneNumber(user.getPhoneNumber());
        responseDTO.setAddress(user.getAddress());
        responseDTO.setCreatedAt(user.getCreatedAt());
        return responseDTO;
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value;
    }
}

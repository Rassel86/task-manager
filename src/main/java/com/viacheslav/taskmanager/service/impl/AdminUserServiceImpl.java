package com.viacheslav.taskmanager.service.impl;

import com.viacheslav.taskmanager.exception.AccessDeniedException;
import com.viacheslav.taskmanager.exception.UserAlreadyBlockedException;
import com.viacheslav.taskmanager.exception.UserAlreadyUnblockedException;
import com.viacheslav.taskmanager.mapper.UserMapper;
import com.viacheslav.taskmanager.model.UserAccount;
import com.viacheslav.taskmanager.model.dto.PageResponse;
import com.viacheslav.taskmanager.model.dto.user.UserCreateDto;
import com.viacheslav.taskmanager.model.dto.user.UserFilterRequest;
import com.viacheslav.taskmanager.model.dto.user.UserResponse;
import com.viacheslav.taskmanager.model.dto.user.UserUpdateByAdminRequest;
import com.viacheslav.taskmanager.model.enums.UserRole;
import com.viacheslav.taskmanager.repository.UserAccountRepository;
import com.viacheslav.taskmanager.specification.UserSpecification;
import com.viacheslav.taskmanager.util.LoggingUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminUserServiceImpl {

    private final UserAccountRepository userAccountRepository;
    private final UserMapper userMapper;
    private final UserSpecification userSpecification;

//    @Override
//    public PageResponse<UserResponse> getUsersPage(UserFilterRequest filter) {
//        log.info("Fetching users page with filter: {}", filter);
//        PageRequest request = PageRequest.of(filter.page(), filter.size(),
//                Sort.by(
//                        Sort.Direction.fromString(filter.sortDirection()),
//                        filter.sortField()
//                )
//        );
//
//        Specification<UserAccount> spec = userSpecification.getUsersSpecification(filter);
//
//        Page<UserAccount> usersPage = userAccountRepository.findAll(spec, request);
//        Page<UserResponse> responsePage = usersPage.map(userMapper::toUserResponse);
//        log.info("Retrieved {} users out of {}", responsePage.getContent().size(), responsePage.getTotalPages());
//        return PageResponse.from(responsePage);
//    }
//
//    @Transactional
//    public UserResponse updateUserByAdmin(UUID id, UserUpdateByAdminRequest request) {
//        log.info("Admin update attempt userAccount with ID: {}", id);
//        UserAccount userAccount = getUserEntityById(id);
//
//        validateAdminCannotModifySelfOrOtherAdmin(userAccount);
//
//        if (request.role() == UserRole.ADMIN) {
//            throw new AccessDeniedException("Cannot assign ADMIN role");
//        }
//
//        if (request.role() != null) {
//            userAccount.setRole(request.role());
//        }
//
//        if (request.enabled() != null) {
//            userAccount.setEnabled(request.enabled());
//        }
//
//        applyBasicChanges(userAccount,
//                request.username(),
//                request.email(),
//                request.firstName(),
//                request.lastName());
//
//        UserAccount updatedUserAccount = userAccountRepository.save(userAccount);
//        log.info("UserAccount updated by admin successfully: {}", LoggingUtils.maskEmail(updatedUserAccount.getContactEmail()));
//        return userMapper.toUserResponse(userAccount);
//    }
//
//    @Override
//    @Transactional
//    public void deleteUserByAdmin(UUID id) {
//        log.info("Admin attempting userAccount with ID {}", id);
//        UserAccount userAccount = getUserEntityById(id);
//        validateAdminCannotModifySelfOrOtherAdmin(userAccount);
//        userAccountRepository.delete(userAccount);
//        log.info("Admin successfully deleted userAccount with ID {}", id);
//    }
//
//    @Override
//    public void blockUser(UUID id) {
//        log.warn("Blocking userAccount with ID: {}", id);
//        UserAccount userAccount = getUserEntityById(id);
//
//        validateAdminCannotModifySelfOrOtherAdmin(userAccount);
//
//        if (!userAccount.isEnabled()) {
//            throw new UserAlreadyBlockedException("UserAccount is already blocked");
//        }
//
//        userAccount.setEnabled(false);
//        userAccountRepository.save(userAccount);
//        log.info("UserAccount with ID {} blocked successfully", id);
//    }
//
//    @Override
//    public void unblockUser(UUID id) {
//        log.warn("Unblocking userAccount with ID: {}", id);
//        UserAccount userAccount = getUserEntityById(id);
//
//        validateAdminCannotModifySelfOrOtherAdmin(userAccount);
//
//        if (userAccount.isEnabled()) {
//            throw new UserAlreadyUnblockedException("UserAccount is already unblocked");
//        }
//
//        userAccount.setEnabled(true);
//        userAccountRepository.save(userAccount);
//        log.info("UserAccount with ID {} unblocked successfully", id);
//    }
}

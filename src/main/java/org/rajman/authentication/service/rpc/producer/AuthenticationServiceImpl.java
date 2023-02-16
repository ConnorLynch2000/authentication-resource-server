package org.rajman.authentication.service.rpc.producer;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.rajman.authentication.controller.exception.ErrorMessage;
import org.rajman.authentication.repository.AuthorizationCodeRequestRepository;
import org.rajman.authentication.repository.PermissionRepository;
import org.rajman.authentication.repository.PlayerRepository;
import org.rajman.authentication.repository.RoleRepository;
import org.rajman.authentication.service.UserService;
import org.rajman.common.genericexceptionhandler.model.GenericErrorException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationServicesInterface {
    PlayerRepository playerRepository;
    AuthorizationCodeRequestRepository authorizationCodeRequestRepository;
    PermissionRepository permissionRepository;
    RoleRepository roleRepository;
    PlayerAuthHistoryService playerAuthHistoryService;
    AccessTokenService accessTokenService;
    UserService userService;
    final short CODE_EXPIRATION_VALID_MINUTE = 2;

    @Override
    public boolean hasRoleByMobileNumber(String mobileNo, String role) {
        log.info("hasRoleByMobileNumber called for player with mobileNo:{} and role:{} ", mobileNo, role);
        try {
            Optional<PlayerEntity> playerOp = playerRepository.findByMobileNumberWithRoles(mobileNo);
            if (playerOp.isPresent()) {
                PlayerEntity player = playerOp.get();
                if (player.getRoles() != null && player.getRoles().size() > 0) {
                    return player.getRoles().stream().anyMatch(p -> p.getRole().getName().equalsIgnoreCase(role));
                } else {
                    log.warn("hasRoleByMobileNumber --> player roles null or empty for mobile:{} ", mobileNo);
                }
            } else {
                log.warn("hasRoleByMobileNumber --> no player found with mobileNo:{} ", mobileNo);
            }
        } catch (Exception x) {
            log.error("hasRoleByMobileNumber --> error in fetching roles of player with mobileNo:{} and role:{} ", mobileNo, role);
        }
        return false;
    }

    @Override
    public String getAuthorizationCode(String source, String currentToken, Long playerId) {
        log.debug("getAuthorizationCode called with  source:{},token:{},playerId:{}", source, currentToken, playerId);
        String code = UUID.randomUUID().toString().replace("-", "");
        AuthorizationCodeRequestEntity authorizationCodeRequestEntity =
                AuthorizationCodeRequestEntity.builder()
                        .playerId(playerId)
                        .codeExpirationDateTime(LocalDateTime.now().plusMinutes(CODE_EXPIRATION_VALID_MINUTE))
                        .requestToken(currentToken)
                        .code(code)
                        .source(source)
                        .build();
        authorizationCodeRequestRepository.save(authorizationCodeRequestEntity);
        return code;
    }

    @Override
    public String getAuthorizationCode(String source, Long playerId) {
        log.info("getAuthorizationCode called with  source:{},playerId:{}", source, playerId);
        try {
            String accessToken = accessTokenService.getAccessTokenFromPlayerId(source, playerId);
            return getAuthorizationCode(source, accessToken, playerId);
        } catch (Exception e) {
            log.error("error in create token from playerId with error {}", e.getMessage());
            return null;
        }
    }

    @Override
    public List<Long> getPlayerIdsWithPermission(String permissionName) throws GenericErrorException {
        boolean permissionExist = permissionRepository.existsByNameEquals(permissionName);
        if (!permissionExist) {
            throw new GenericErrorException(ErrorMessage.PermissionNameDoesNotFound);
        }
        return permissionRepository.getPlayerIdsWithPermission(permissionName);
    }

    @Override
    public List<Long> getPlayerIdsWithRole(String roleName) throws GenericErrorException {

        boolean roleExist = roleRepository.existsByNameEquals(roleName);
        if (!roleExist) {
            throw new GenericErrorException(ErrorMessage.RoleNameDoesNotFound);
        }
        return roleRepository.getPlayerIdsWithRole(roleName);
    }

    @Override
    public void addPlayerAuthHistorySignOut(long playerId, long deviceId) {
        log.debug("call logout in app with player id {}", playerId);
        DeviceEntity device = DeviceEntity.builder().id(deviceId).build();
        PlayerEntity player = PlayerEntity.builder().id(playerId).build();
        playerAuthHistoryService.logSignOutApp(player, device, LocalDateTime.now());
    }

    @Override
    public List<Long> getPlayerIdsByUuid(final String uuid) throws IllegalArgumentException {
        return userService.getPlayerIdsByUuid(uuid);
    }

    @Transactional
    public String validateAuthorizationCodeAndGetToken(String code) throws GenericErrorException {
        AuthorizationCodeRequestEntity authCode = authorizationCodeRequestRepository.findByCode(code).orElseThrow(() -> new GenericErrorException(ErrorMessage.AuthorizationCodeNotFound));
        if (authCode.getUsedDateTime() != null) {
            throw new GenericErrorException(ErrorMessage.AuthorizationCodeNotValid);
        }
        authCode.setUsedDateTime(LocalDateTime.now());
        authorizationCodeRequestRepository.save(authCode);
        if (authCode.getCodeExpirationDateTime().isAfter(LocalDateTime.now())) {
            return authCode.getRequestToken();
        } else {
            throw new GenericErrorException(ErrorMessage.AuthorizationCodeExpired);
        }

    }

    public int deleteExpiredAuthorizationCodes() {
        return authorizationCodeRequestRepository.deleteExpiredCodes(LocalDateTime.now());
    }
}

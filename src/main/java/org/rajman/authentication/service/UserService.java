package org.rajman.authentication.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.rajman.authentication.model.entity.UserEntity;
import org.rajman.authentication.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {

    UserRepository userRepository;

    DeviceService deviceService;

    PasswordEncoder passwordEncoder;

    EventPublisher eventPublisher;

    public Optional<UserEntity> findTempUserByDevice(long deviceId) {
        return userRepository.findByDeviceIdAndTempIsTrue(deviceId)
                .stream().findFirst();
    }

    public UserEntity createUser(PlayerEntity player, DeviceEntity device, String username) {
        String password = String.valueOf((int) (Math.random() * 10000000));
        return createUser(player, device, username, password);
    }

    private UserEntity createUser(PlayerEntity player, DeviceEntity device, String username, String password) {
        UserEntity user = new UserEntity();
        user.setPlayer(player);
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setEnabled(true);
        user.setDevice(device);
        user.setLastActiveDateTime(LocalDateTime.now());
        user.setTemp(player.getTemp());
        return userRepository.save(user);
    }

    UserEntity addUser(String username, String password, PlayerEntity player, DeviceEntity device, Boolean isTemp) {
        UserEntity userEntity = userRepository.findOneByUsername(username);

        if (userEntity == null) {
            UserEntity user = new UserEntity();
            user.setPlayer(player);
            user.setUsername(username);
            user.setPassword(passwordEncoder.encode(password));
            user.setEnabled(true);
            user.setDevice(device);
            user.setLastActiveDateTime(LocalDateTime.now());
            user.setTemp(isTemp);
            try {
                return userRepository.save(user);
            } catch (Exception ignored) {
                log.error("Exception during saving user {} ", username);
            }
        }
        return userEntity;
    }

    public UserEntity getUserFromUsername(PlayerEntity player, String uuid, String identifier, String password) {
        String username = uuid + "+" + identifier;
        return userRepository.findByUsername(username).orElseGet(() -> {
            DeviceEntity device = deviceService.findOrCreateDevice(uuid);
            UserEntity user = createUser(player, device, username, password);
            eventPublisher.publishSignUpUser(player, device);
            return user;
        });
    }

    public List<Long> getPlayerIdsByUuid(final String uuid) throws IllegalArgumentException{
        if (Strings.isBlank(uuid)) {
            throw new IllegalArgumentException("uuid is not valid");
        }
        return userRepository.getPlayerIdsByUuid(uuid);
    }
}

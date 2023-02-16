package org.rajman.authentication.service.rpc.producer;

import org.rajman.common.genericexceptionhandler.model.GenericErrorException;

import java.util.List;

public interface AuthenticationServicesInterface {
    boolean hasRoleByMobileNumber(String mobileNo, String role);

    String getAuthorizationCode(String source, String currentToken, Long playerId);

    String getAuthorizationCode(String source, Long playerId);

    List<Long> getPlayerIdsWithPermission(String permissionName) throws GenericErrorException;

    List<Long> getPlayerIdsWithRole(String roleName) throws GenericErrorException;

    void addPlayerAuthHistorySignOut(long playerId, long deviceId);

    /**
     * return a list of player ids found for input uuid
     * @param uuid uuid of which we want to get list of player ids
     * @return list of player ids
     * @throws IllegalArgumentException when uuid is empty or null
     */
    List<Long> getPlayerIdsByUuid(final String uuid) throws IllegalArgumentException;
}
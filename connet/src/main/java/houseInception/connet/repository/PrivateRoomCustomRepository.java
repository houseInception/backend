package houseInception.connet.repository;

import houseInception.connet.domain.privateRoom.PrivateChat;
import houseInception.connet.domain.privateRoom.PrivateRoom;
import houseInception.connet.domain.privateRoom.PrivateRoomUser;
import houseInception.connet.dto.PrivateChatResDto;
import houseInception.connet.dto.PrivateRoomResDto;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface PrivateRoomCustomRepository {

    Optional<PrivateRoom> findPrivateRoomWithUser(String privateRoomUuid);
    Optional<PrivateRoomUser> findPrivateRoomUser(Long privateRoomId, Long userId);
    Optional<PrivateRoomUser> findTargetRoomUserWithUserInChatRoom(Long userId, Long privateRoomId);
    List<PrivateChat> findPrivateChatsInPrivateRoom(Long privateRoomId);
    Optional<PrivateChat> findPrivateChatsById(Long privateChatId);

    Map<Long, PrivateRoomResDto> getPrivateRoomList(Long userId, int page);
    List<Long> getLastChatTimeOfPrivateRooms(List<Long> privateRoomIdList);

    boolean existsAlivePrivateRoomUser(Long userId, Long privateRoomId);
    boolean existsAlivePrivateRoomUser(Long userId, String privateRoomUuid);
    Long getPrivateRoomIdOfChat(Long privateChatId);

    List<PrivateChatResDto> getPrivateChatList(Long userId, Long privateRoomId, int page);
}

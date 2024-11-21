package houseInception.connet.service;

import houseInception.connet.domain.*;
import houseInception.connet.domain.privateRoom.PrivateChat;
import houseInception.connet.domain.privateRoom.PrivateRoom;
import houseInception.connet.domain.privateRoom.PrivateRoomUser;
import houseInception.connet.dto.PrivateChatAddDto;
import houseInception.connet.dto.PrivateChatAddRestDto;
import houseInception.connet.dto.PrivateChatResDto;
import houseInception.connet.dto.PrivateRoomResDto;
import houseInception.connet.exception.PrivateRoomException;
import houseInception.connet.repository.PrivateRoomRepository;
import houseInception.connet.repository.UserBlockRepository;
import houseInception.connet.repository.UserRepository;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
@Transactional
@SpringBootTest
class PrivateRoomServiceTest {

    @Autowired
    PrivateRoomService privateRoomService;

    @Autowired
    UserRepository userRepository;
    @Autowired
    PrivateRoomRepository privateRoomRepository;
    @Autowired
    UserBlockRepository userBlockRepository;

    @Autowired
    EntityManager em;

    User user1;
    User user2;
    User user3;
    User user4;

    @BeforeEach
    void beforeEach(){
        user1 = User.create("user1", null, null, null);
        user2 = User.create("user2", null, null, null);
        user3 = User.create("user3", null, null, null);
        user4 = User.create("user4", null, null, null);

        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);
        userRepository.save(user4);
    }

    @Test
    void addPrivateChat_채팅방X_파일X() {
        //when
        String message = "mess1";
        PrivateChatAddDto chatAddDto = new PrivateChatAddDto(null, message, null);
        PrivateChatAddRestDto result = privateRoomService.addPrivateChat(user1.getId(), user2.getId(), chatAddDto);

        //then
        PrivateRoom privateRoom = privateRoomRepository.findPrivateRoomWithUser(result.getChatRoomUuid()).orElse(null);
        assertThat(privateRoom).isNotNull();

        List<PrivateRoomUser> privateRoomUsers = privateRoom.getPrivateRoomUsers();
        assertThat(privateRoomUsers.size()).isEqualTo(2);
        assertThat(privateRoomUsers.stream().map(PrivateRoomUser::getUser))
                .extracting("id").contains(user1.getId(), user2.getId());

        List<PrivateChat> privateChats = privateRoomRepository.findPrivateChatsInPrivateRoom(privateRoom.getId());
        assertThat(privateChats.size()).isEqualTo(1);
        assertThat(privateChats.get(0).getMessage()).isEqualTo(message);
    }

    @Test
    void addPrivateChat_채팅방O_파일X() {
        //given
        PrivateRoom privateRoom = PrivateRoom.create(user1, user2);
        privateRoomRepository.save(privateRoom);

        //when
        String message = "mess1";
        PrivateChatAddDto chatAddDto = new PrivateChatAddDto(privateRoom.getPrivateRoomUuid(), message, null);
        PrivateChatAddRestDto result = privateRoomService.addPrivateChat(user1.getId(), user2.getId(), chatAddDto);

        //then
        List<PrivateChat> privateChats = privateRoomRepository.findPrivateChatsInPrivateRoom(privateRoom.getId());
        assertThat(privateChats.size()).isEqualTo(1);
        assertThat(privateChats.get(0).getMessage()).isEqualTo(message);
    }

    @Test
    void addPrivateChat_차단된_유저() {
        //given
        UserBlock userBlock = UserBlock.create(user2, user1, UserBlockType.REQUEST);
        UserBlock reverseUserBlock = UserBlock.create(user1, user2, UserBlockType.ACCEPT);

        userBlockRepository.save(userBlock);
        userBlockRepository.save(reverseUserBlock);

        //when
        String message = "mess1";
        PrivateChatAddDto chatAddDto = new PrivateChatAddDto(null, message, null);
        assertThatThrownBy(() -> privateRoomService.addPrivateChat(user1.getId(), user2.getId(), chatAddDto)).isInstanceOf(PrivateRoomException.class);
    }

    @Test
    void getPrivateRoomList() {
        //given
        PrivateRoom privateRoom1 = PrivateRoom.create(user1, user2);
        PrivateRoom privateRoom2 = PrivateRoom.create(user1, user3);
        PrivateRoom privateRoom3 = PrivateRoom.create(user1, user4);

        privateRoomRepository.save(privateRoom1);
        privateRoomRepository.save(privateRoom2);
        privateRoomRepository.save(privateRoom3);

        privateRoom2.addUserToUserChat("message", null, privateRoom2.getPrivateRoomUsers().get(0));
        em.flush();
        privateRoom3.addUserToUserChat("message", null, privateRoom3.getPrivateRoomUsers().get(0));
        em.flush();
        privateRoom1.addUserToUserChat("message", null, privateRoom1.getPrivateRoomUsers().get(0));
        em.flush();

        //when
        List<PrivateRoomResDto> result = privateRoomService.getPrivateRoomList(user1.getId(), 1).getData();

        //then
        assertThat(result.size()).isEqualTo(3);
        assertThat(result).extracting("chatRoomId").containsExactly(privateRoom1.getId(), privateRoom3.getId(), privateRoom2.getId());
    }

    @Test
    void getPrivateChatList_이모지o() {
        //given
        PrivateRoom privateRoom1 = PrivateRoom.create(user1, user2);
        privateRoomRepository.save(privateRoom1);
        PrivateChat privateChat1 = privateRoom1.addUserToUserChat("message1", null, privateRoom1.getPrivateRoomUsers().get(0));
        PrivateChat privateChat2 = privateRoom1.addUserToUserChat("message2", null, privateRoom1.getPrivateRoomUsers().get(1));
        em.flush();

        ChatEmoji chatEmoji1 = ChatEmoji.createPrivateChatEmoji(user1, privateChat1.getId(), EmojiType.HEART);
        ChatEmoji chatEmoji2 = ChatEmoji.createPrivateChatEmoji(user2, privateChat1.getId(), EmojiType.HEART);
        ChatEmoji chatEmoji3 = ChatEmoji.createPrivateChatEmoji(user2, privateChat1.getId(), EmojiType.CHECK);
        em.persist(chatEmoji1);
        em.persist(chatEmoji2);
        em.persist(chatEmoji3);

        //when
        List<PrivateChatResDto> result = privateRoomService.getPrivateChatList(user1.getId(), privateRoom1.getPrivateRoomUuid(), 1).getData();

        //then
        assertThat(result.size()).isEqualTo(2);
        assertThat(result).extracting("chatId").containsExactly(privateChat2.getId(), privateChat1.getId());

        PrivateChatResDto chatDto = result.get(1);
        assertThat(chatDto.getEmoji()).extracting("emojiType").contains(EmojiType.HEART, EmojiType.CHECK);
        assertThat(chatDto.getEmoji()).extracting("count").contains(1, 2);
    }

    @Test
    void deletePrivateRoom() {
        //given
        PrivateRoom privateRoom1 = PrivateRoom.create(user1, user2);
        privateRoomRepository.save(privateRoom1);

        //when
        privateRoomService.deletePrivateRoom(user1.getId(), privateRoom1.getPrivateRoomUuid());

        //then
        PrivateRoomUser privateRoomUser = privateRoomRepository.findPrivateRoomUser(privateRoom1.getId(), user1.getId()).orElse(null);
        assertThat(privateRoomUser).isNotNull();
        assertThat(privateRoomUser.getStatus()).isEqualTo(Status.DELETED);
    }

    @Test
    void deletePrivateRoom_권한x() {
        //given
        PrivateRoom privateRoom1 = PrivateRoom.create(user1, user2);
        privateRoomRepository.save(privateRoom1);

        //when
        assertThatThrownBy(() -> privateRoomService.deletePrivateRoom(user3.getId(), privateRoom1.getPrivateRoomUuid())).isInstanceOf(PrivateRoomException.class);
    }

    @Test
    void 채팅방_퇴장후_목록_조회() {
        //given
        String chatRoomUuid1 = privateRoomService.addPrivateChat(user1.getId(), user2.getId(), new PrivateChatAddDto(null, "message", null)).getChatRoomUuid();
        String chatRoomUuid2 = privateRoomService.addPrivateChat(user1.getId(), user3.getId(), new PrivateChatAddDto(null, "message", null)).getChatRoomUuid();
        String chatRoomUuid3 = privateRoomService.addPrivateChat(user1.getId(), user4.getId(), new PrivateChatAddDto(null, "message", null)).getChatRoomUuid();
        privateRoomService.deletePrivateRoom(user1.getId(), chatRoomUuid1);

        //when
        List<PrivateRoomResDto> result = privateRoomService.getPrivateRoomList(user1.getId(), 1).getData();

        //then
        assertThat(result.size()).isEqualTo(2);
        assertThat(result).extracting("chatRoomUuid").contains(chatRoomUuid2, chatRoomUuid3);
    }
}
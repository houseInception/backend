package houseInception.connet.service;

import houseInception.connet.domain.User;
import houseInception.connet.domain.gptRoom.GptRoom;
import houseInception.connet.domain.gptRoom.GptRoomChat;
import houseInception.connet.domain.gptRoom.GptRoomUser;
import houseInception.connet.dto.*;
import houseInception.connet.exception.GptRoomException;
import houseInception.connet.repository.GptRoomRepository;
import houseInception.connet.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static houseInception.connet.domain.Status.ALIVE;
import static houseInception.connet.domain.Status.DELETED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
@SpringBootTest
class GptRoomGptRoomServiceTest {

    @Autowired
    GptRoomService gptRoomService;

    @Autowired
    UserRepository userRepository;
    @Autowired
    GptRoomRepository gptRoomRepository;

    @Autowired
    EntityManager em;

    User user1;
    User user2;

    @BeforeEach
    void beforeEach(){
        user1 = User.create("user1", null, null, null);
        userRepository.save(user1);

        user2 = User.create("user2", null, null, null);
        userRepository.save(user2);
    }

//    @AfterEach
    void afterEach(){
        userRepository.deleteAll();
    }

    @Test
    void addGptChat_새채팅방() {
        //given
        String message = "GPT모델 중 가장 싼 모델은 뭐야?";
        GptRoomChatAddDto gptRoomChatAddDto = new GptRoomChatAddDto(null, message);

        //when
        GptChatResDto result = gptRoomService.addGptChat(user1.getId(), gptRoomChatAddDto);

        //then
        GptRoom gptRoom = gptRoomRepository.findByGptRoomUuidAndStatus(result.getChatRoomUuid(), ALIVE).orElse(null);
        assertThat(result.getChatRoomUuid()).isEqualTo(gptRoom.getGptRoomUuid());
        assertThat(result.getTitle()).isEqualTo(gptRoom.getTitle());

        List<GptRoomChat> chatList = gptRoomRepository.getChatListOfGptRoom(gptRoom.getId());
        assertThat(chatList.size()).isEqualTo(2);
        assertThat(chatList).extracting("id").containsExactly(result.getUserChatId(), result.getGptChatId());
    }

    @Test
    void addGptChat_기존채팅방() {
        //given
        GptRoom gptRoom = GptRoom.createGptRoom(user1);
        gptRoomRepository.save(gptRoom);

        String message = "GPT모델 중 가장 싼 모델은 뭐야?";
        GptRoomChatAddDto gptRoomChatAddDto = new GptRoomChatAddDto(gptRoom.getGptRoomUuid(), message);

        //when
        GptChatResDto result = gptRoomService.addGptChat(user1.getId(), gptRoomChatAddDto);

        //then
        GptRoom findGptRoom = gptRoomRepository.findByGptRoomUuidAndStatus(result.getChatRoomUuid(), ALIVE).orElse(null);
        assertThat(findGptRoom.getGptRoomUuid()).isEqualTo(gptRoom.getGptRoomUuid());
        assertThat(findGptRoom.getTitle()).isEqualTo(gptRoom.getTitle());

        List<GptRoomChat> chatList = gptRoomRepository.getChatListOfGptRoom(findGptRoom.getId());
        assertThat(chatList.size()).isEqualTo(2);
    }

    @Test
    void addGptChat_기존채팅방_권한X() {
        //given
        User newUser = User.create("newUser", null, null, null);
        userRepository.save(newUser);

        GptRoom gptRoom = GptRoom.createGptRoom(user1);
        gptRoomRepository.save(gptRoom);

        String message = "GPT모델 중 가장 싼 모델은 뭐야?";
        GptRoomChatAddDto gptRoomChatAddDto = new GptRoomChatAddDto(gptRoom.getGptRoomUuid(), message);

        //when
        assertThatThrownBy(() -> gptRoomService.addGptChat(newUser.getId(), gptRoomChatAddDto)).isInstanceOf(GptRoomException.class);
    }

    @Test
    void getGptChatRoomList() {
        //given
        GptRoom gptRoom1 = GptRoom.createGptRoom(user1);
        gptRoomRepository.save(gptRoom1);

        GptRoom gptRoom2 = GptRoom.createGptRoom(user1);
        gptRoomRepository.save(gptRoom2);

        User newUser = User.create("newUser", null, null, null);
        userRepository.save(newUser);

        GptRoom newUserGptRoom = GptRoom.createGptRoom(newUser);
        gptRoomRepository.save(newUserGptRoom);

        //when
        DataListResDto<GptRoomListResDto> result = gptRoomService.getGptChatRoomList(user1.getId(), 1);

        //then
        List<GptRoomListResDto> data = result.getData();
        assertThat(data.size()).isEqualTo(2);
        assertThat(data).extracting("chatRoomUuid").containsExactly(gptRoom2.getGptRoomUuid(), gptRoom1.getGptRoomUuid());

    }

    @Test
    void updateGptRoom() {
        //given
        GptRoom gptRoom = GptRoom.createGptRoom(user1);
        gptRoomRepository.save(gptRoom);

        //when
        String title = "new title";
        gptRoomService.updateGptRoom(user1.getId(), gptRoom.getGptRoomUuid(), title);

        //then
        GptRoom findGptRoom = gptRoomRepository.findById(gptRoom.getId()).orElse(null);
        assertThat(findGptRoom).isNotNull();
        assertThat(findGptRoom.getTitle()).isEqualTo(title);
    }

    @Test
    void updateGptRoom_권한X() {
        //given
        GptRoom gptRoom = GptRoom.createGptRoom(user1);
        gptRoomRepository.save(gptRoom);

        //when
        String title = "new title";
        assertThatThrownBy(() -> gptRoomService.updateGptRoom(user2.getId(), gptRoom.getGptRoomUuid(), title)).isInstanceOf(GptRoomException.class);
    }

    @Test
    void deleteGptRoom() {
        //given
        GptRoom gptRoom = GptRoom.createGptRoom(user1);
        gptRoomRepository.save(gptRoom);

        //when
        gptRoomService.deleteGptRoom(user1.getId(), gptRoom.getGptRoomUuid());

        //then
        GptRoom findGptRoom = gptRoomRepository.findById(gptRoom.getId()).orElse(null);
        assertThat(findGptRoom).isNotNull();
        assertThat(findGptRoom.getStatus()).isEqualTo(DELETED);
    }

    @Test
    void deleteGptRoom_권한X() {
        //given
        GptRoom gptRoom = GptRoom.createGptRoom(user1);
        gptRoomRepository.save(gptRoom);

        User newUser = User.create("newUser", null, null, null);
        userRepository.save(newUser);

        //when
        assertThatThrownBy(() -> gptRoomService.deleteGptRoom(newUser.getId(), gptRoom.getGptRoomUuid())).isInstanceOf(GptRoomException.class);
    }

    @Test
    void getGptChatRoomChatList() {
        //given
        GptRoom gptRoom = GptRoom.createGptRoom(user1);
        gptRoomRepository.save(gptRoom);

        GptRoomUser gptRoomUser1 = gptRoom.getGptRoomUsers().get(0);
        GptRoomChat chat1 = gptRoom.addUserChat(gptRoomUser1, "content1");
        GptRoomChat chat2 = gptRoom.addGptChat("I am GPT 1");
        GptRoomChat chat3 = gptRoom.addUserChat(gptRoomUser1, "content2");

        em.flush();

        //when
        GptChatRoomChatListResDto result = gptRoomService.getGptChatRoomChatList(user1.getId(), gptRoom.getGptRoomUuid(), 1);

        //then
        List<GptRoomChatResDto> messages = result.getMessages();
        assertThat(messages.size()).isEqualTo(3);
        assertThat(messages).extracting("chatId").containsExactly(chat3.getId(), chat2.getId(), chat1.getId());
        assertThat(messages).extracting("writer").containsNull();
    }

    @Test
    void getGptChatRoomChatList_권한X() {
        //given
        GptRoom gptRoom = GptRoom.createGptRoom(user1);
        gptRoomRepository.save(gptRoom);

        User newUser = User.create("newUser", null, null, null);
        userRepository.save(newUser);

        //when
        assertThatThrownBy(() -> gptRoomService.getGptChatRoomChatList(newUser.getId(), gptRoom.getGptRoomUuid(), 1)).isInstanceOf(GptRoomException.class);
    }
}
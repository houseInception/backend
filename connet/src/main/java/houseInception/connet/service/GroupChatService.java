package houseInception.connet.service;

import houseInception.connet.domain.GroupChat;
import houseInception.connet.domain.User;
import houseInception.connet.domain.group.GroupUser;
import houseInception.connet.dto.groupChat.GroupChatAddDto;
import houseInception.connet.dto.groupChat.GroupChatAddResDto;
import houseInception.connet.exception.ChannelException;
import houseInception.connet.exception.GroupChatException;
import houseInception.connet.exception.GroupException;
import houseInception.connet.externalServiceProvider.s3.S3ServiceProvider;
import houseInception.connet.repository.ChannelRepository;
import houseInception.connet.repository.GroupChatRepository;
import houseInception.connet.repository.GroupRepository;
import houseInception.connet.service.util.CommonDomainService;
import houseInception.connet.socketManager.SocketServiceProvider;
import houseInception.connet.socketManager.dto.GroupChatSocketDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static houseInception.connet.domain.ChatterRole.USER;
import static houseInception.connet.response.status.BaseErrorCode.*;
import static houseInception.connet.service.util.FileUtil.getUniqueFileName;
import static houseInception.connet.service.util.FileUtil.isInValidFile;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class GroupChatService {

    private final GroupChatRepository groupChatRepository;
    private final ChannelRepository channelRepository;
    private final GroupRepository groupRepository;
    private final CommonDomainService domainService;
    private final S3ServiceProvider s3ServiceProvider;
    private final SocketServiceProvider socketServiceProvider;

    @Transactional
    public GroupChatAddResDto addChat(Long userId, String groupUuid, GroupChatAddDto chatAddDto) {
        checkExistTap(chatAddDto.getTapId());
        checkValidContent(chatAddDto.getImage(), chatAddDto.getMessage());
        GroupUser groupUser = findGroupUser(userId, groupUuid);
        User user = domainService.findUser(userId);

        String imageUrl = uploadImages(chatAddDto.getImage());

        GroupChat chat = GroupChat.createUserToUser(groupUser, chatAddDto.getTapId(), chatAddDto.getMessage(), imageUrl);
        groupChatRepository.save(chat);

        GroupChatSocketDto socketDto = new GroupChatSocketDto(groupUuid, chat.getTapId(), chat.getId(), chat.getMessage(), chat.getImage(), USER, user, chat.getCreatedAt());
        sendMessageToGroupUsers(groupUuid, userId, socketDto);

        return new GroupChatAddResDto(chat.getId(), chat.getCreatedAt());
    }

    private void sendMessageToGroupUsers(String groupUuid, Long userId, GroupChatSocketDto socketDto){
        List<Long> groupUserIds = groupRepository.findUserIdsOfGroupExceptUser(groupUuid, userId);
        groupUserIds.forEach((targetId) -> socketServiceProvider.sendMessage(targetId, socketDto));
    }

    private String uploadImages(MultipartFile image){
        if (isInValidFile(image)) {
            return null;
        }

        String newFileName = getUniqueFileName(image.getOriginalFilename());
        return s3ServiceProvider.uploadImage(newFileName, image);
    }

    private GroupUser findGroupUser(Long userId, String groupUuid){
        return groupRepository.findGroupUser(groupUuid, userId)
                .orElseThrow(() -> new GroupException(NOT_IN_GROUP));
    }

    private void checkValidContent(MultipartFile image, String message) {
        if(isInValidFile(image) && !StringUtils.hasText(message)){
            throw new GroupChatException(NO_CONTENT_IN_CHAT);
        }
    }

    private void checkExistTap(Long tapId){
        if (!channelRepository.existsTap(tapId)){
            throw new ChannelException(NO_SUCH_TAP);
        }
    }
}

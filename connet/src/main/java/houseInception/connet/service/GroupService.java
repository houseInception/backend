package houseInception.connet.service;

import houseInception.connet.domain.Status;
import houseInception.connet.domain.User;
import houseInception.connet.domain.group.Group;
import houseInception.connet.dto.group.GroupAddDto;
import houseInception.connet.exception.GroupException;
import houseInception.connet.externalServiceProvider.s3.S3ServiceProvider;
import houseInception.connet.repository.GroupRepository;
import houseInception.connet.service.util.DomainValidatorUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static houseInception.connet.response.status.BaseErrorCode.INVALID_GROUP_TAG;
import static houseInception.connet.response.status.BaseErrorCode.NO_SUCH_GROUP;
import static houseInception.connet.service.util.FileUtil.getUniqueFileName;
import static houseInception.connet.service.util.FileUtil.isInValidFile;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class GroupService {

    private final GroupRepository groupRepository;
    private final DomainValidatorUtil validator;
    private final S3ServiceProvider s3ServiceProvider;

    @Value("${aws.s3.imageUrlPrefix}")
    private String s3UrlPrefix;

    @Transactional
    public String addGroup(Long userId, GroupAddDto groupAddDto) {
        User user = validator.findUser(userId);

        String groupProfileUrl = uploadImages(groupAddDto.getGroupProfile());

        Group group = Group.create(
                user,
                groupAddDto.getGroupName(),
                groupProfileUrl,
                groupAddDto.getGroupDescription(),
                groupAddDto.getUserLimit(),
                groupAddDto.isOpen());

        List<String> tagList = groupAddDto.getTags();
        if (!tagList.isEmpty() && !group.addTag(tagList)) {
            throw new GroupException(INVALID_GROUP_TAG);
        }

        groupRepository.save(group);

        return group.getGroupUuid();
    }

    private String uploadImages(MultipartFile image){
        if (isInValidFile(image)) {
            return null;
        }

        String newFileName = getUniqueFileName(image.getOriginalFilename());
        s3ServiceProvider.uploadImage(newFileName, image);

        return s3UrlPrefix + newFileName;
    }

    @Transactional
    public void addGroupUser(Long userId, String groupUuid) {
        User user = validator.findUser(userId);
        Group group = findGroup(groupUuid);

        group.addUser(user);
    }

    private Group findGroup(String groupUuid){
        return groupRepository.findByGroupUuidAndStatus(groupUuid, Status.ALIVE)
                .orElseThrow(() -> new GroupException(NO_SUCH_GROUP));
    }
}

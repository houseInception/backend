package houseInception.connet.service;

import houseInception.connet.domain.User;
import houseInception.connet.domain.UserBlock;
import houseInception.connet.domain.UserBlockType;
import houseInception.connet.dto.DataListResDto;
import houseInception.connet.dto.DefaultUserResDto;
import houseInception.connet.event.publisher.UserBlockEventPublisher;
import houseInception.connet.exception.UserBlockException;
import houseInception.connet.exception.UserException;
import houseInception.connet.repository.UserBlockRepository;
import houseInception.connet.repository.UserRepository;
import houseInception.connet.service.util.DomainValidatorUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static houseInception.connet.domain.Status.ALIVE;
import static houseInception.connet.domain.Status.DELETED;
import static houseInception.connet.domain.UserBlockType.ACCEPT;
import static houseInception.connet.domain.UserBlockType.REQUEST;
import static houseInception.connet.response.status.BaseErrorCode.*;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class UserBlockService {

    private final UserBlockRepository userBlockRepository;
    private final UserBlockEventPublisher userBlockEventPublisher;
    private final DomainValidatorUtil validator;

    @Transactional
    public Long blockUser(Long userId, Long targetId) {
        User targetUser = validator.findUser(targetId);
        User user = validator.findUser(userId);

        UserBlock findUserBlock = userBlockRepository.findByUserIdAndTargetId(userId, targetId).orElse(null);
        checkAlreadyRequestBlock(findUserBlock);

        userBlockEventPublisher.publishUserBlockEvent(user, targetUser);

        if(findUserBlock == null){
            UserBlock userBlock = UserBlock.create(user, targetUser, REQUEST);
            userBlockRepository.save(userBlock);

            UserBlock reverseUserBlock = UserBlock.create(targetUser, user, ACCEPT);
            userBlockRepository.save(reverseUserBlock);

            return userBlock.getId();
        }else{
            findUserBlock.setBlockType(REQUEST);

            return findUserBlock.getId();
        }
    }

    @Transactional
    public Long cancelBlock(Long userId, Long targetId) {
        validator.checkExistUser(targetId);
        UserBlock userBlock = findUserBlock(userId, targetId);
        UserBlock reverseUserBlock = findUserBlock(targetId, userId);

        if (reverseUserBlock.getBlockType() == ACCEPT) {
            userBlockRepository.delete(userBlock);
            userBlockRepository.delete(reverseUserBlock);
        } else { //상대방도 나를 차단중일때
            userBlock.setBlockType(ACCEPT);
        }

        return userBlock.getId();
    }

    public DataListResDto<DefaultUserResDto> getBlockUserList(Long userId) {
        List<DefaultUserResDto> blockUserList = userBlockRepository.getBlockUserList(userId);

        return new DataListResDto<DefaultUserResDto>(0, blockUserList);
    }

    private UserBlock findUserBlock(Long userId, Long targetId){
        UserBlock userBlock = userBlockRepository.findByUserIdAndTargetId(userId, targetId).orElse(null);
        if (userBlock == null) {
            throw new UserBlockException(NO_SUCH_USER_BLOCK);
        }

        return userBlock;
    }

    private void checkAlreadyRequestBlock(UserBlock findUserBlock) {
        if(findUserBlock != null && findUserBlock.getBlockType() == REQUEST){
            throw new UserBlockException(ALREADY_BLOCK_USER);
        }
    }
}

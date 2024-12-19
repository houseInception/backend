package houseInception.connet.service;

import houseInception.connet.domain.User;
import houseInception.connet.dto.group.GroupAddDto;
import houseInception.connet.exception.UserException;
import houseInception.connet.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static houseInception.connet.response.status.BaseErrorCode.NO_SUCH_USER;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class GroupService {

    private final UserRepository userRepository;

    public Long addGroup(Long userId, GroupAddDto groupAddDto) {
        User user = findUser(userId);

    }

    private User findUser(Long userId){
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserException(NO_SUCH_USER));
    }
}

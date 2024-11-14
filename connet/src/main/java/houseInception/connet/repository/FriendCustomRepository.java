package houseInception.connet.repository;

import houseInception.connet.dto.ActiveUserResDto;
import houseInception.connet.dto.DefaultUserResDto;
import houseInception.connet.dto.FriendFilterDto;

import java.util.List;

public interface FriendCustomRepository {

    List<ActiveUserResDto> getFriendList(Long userId, FriendFilterDto filterDto);
    List<DefaultUserResDto> getFriendRequestList(Long userId);

    boolean existsFriendRequest(Long userId, Long targetId);
}

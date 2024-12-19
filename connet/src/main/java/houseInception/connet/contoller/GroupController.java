package houseInception.connet.contoller;

import houseInception.connet.dto.group.GroupAddDto;
import houseInception.connet.response.BaseResponse;
import houseInception.connet.response.BaseResultDto;
import houseInception.connet.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/groups")
@RequiredArgsConstructor
@RestController
public class GroupController {

    private final GroupService groupService;

    @PostMapping
    public BaseResponse<BaseResultDto> addGroup(@RequestBody GroupAddDto groupAddDto){
        Long userId = UserAuthorizationUtil.getLoginUserId();
        Long resultId = groupService.addGroup(userId, groupAddDto);

        return BaseResponse.getSimpleRes(resultId);
    }
}

package houseInception.gptComm.contoller;

import houseInception.gptComm.dto.TokenResDto;
import houseInception.gptComm.dto.RefreshDto;
import houseInception.gptComm.dto.SignInDto;
import houseInception.gptComm.response.BaseResponse;
import houseInception.gptComm.response.BaseResultDto;
import houseInception.gptComm.service.LoginService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RequestMapping("/login")
@RequiredArgsConstructor
@RestController
public class LoginController {

    private final LoginService loginService;

    @PostMapping("/sign-in")
    public BaseResponse<TokenResDto> signIn(@RequestBody @Valid SignInDto signInDto){
        TokenResDto result = loginService.signIn(signInDto);

        return new BaseResponse<>(result);
    }

    @PostMapping("/refresh")
    public BaseResponse<TokenResDto> refresh(@RequestBody @Valid RefreshDto refreshDto){
        Long userId = UserAuthorizationUtil.getLoginUserId();
        TokenResDto result = loginService.refresh(userId, refreshDto.getRefreshToken());

        return new BaseResponse<>(result);
    }

    @PostMapping("/refresh/check")
    public BaseResponse<Map<String, Boolean>> checkRefreshToken(@RequestBody @Valid RefreshDto refreshDto){
        loginService.checkRefreshToken(refreshDto.getRefreshToken());

        return new BaseResponse<>(Map.of("isValid", true));
    }

    @PostMapping("/sign-out")
    public BaseResponse<BaseResultDto> signOut(){
        Long userId = UserAuthorizationUtil.getLoginUserId();
        Long resultId = loginService.signOut(userId);

        return BaseResponse.getSimpleRes(resultId);
    }
}

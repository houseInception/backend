package houseInception.connet.contoller;

import houseInception.connet.domain.EmojiType;
<<<<<<< Updated upstream
import houseInception.connet.dto.chatEmoji.EmojiDto;
import houseInception.connet.dto.chatEmoji.ChatEmojiUserResDto;
=======
import houseInception.connet.dto.EmojiDto;
>>>>>>> Stashed changes
import houseInception.connet.response.BaseResponse;
import houseInception.connet.response.BaseResultDto;
import houseInception.connet.service.ChatEmojiService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class ChatEmojiController {

    private ChatEmojiService chatEmojiService;

    @PostMapping("/privateChats/{chatId}/emojis")
    public BaseResponse<BaseResultDto> addEmojiOfPrivate(@PathVariable Long chatId,
                                                         @RequestBody EmojiDto emojiDto){
        Long userId = UserAuthorizationUtil.getLoginUserId();
        Long resultId = chatEmojiService.addEmojiToPrivateChat(userId, chatId, emojiDto);

        return BaseResponse.getSimpleRes(resultId);
    }

    @DeleteMapping("/privateChats/{chatId}/emojis")
    public BaseResponse<BaseResultDto> removeEmojiOfPrivate(@PathVariable Long chatId,
                                                            @RequestBody EmojiDto emojiDto){
        Long userId = UserAuthorizationUtil.getLoginUserId();
        Long resultId = chatEmojiService.removeEmojiToPrivateChat(userId, chatId, emojiDto);

        return BaseResponse.getSimpleRes(resultId);
    }

    @GetMapping("/privateChats/{chatId}/emojis")
<<<<<<< Updated upstream
    public BaseResponse<List<ChatEmojiUserResDto>> getEmojiInfo(@PathVariable Long chatId,
                                                                @RequestParam EmojiType emojiType){
        Long userId = UserAuthorizationUtil.getLoginUserId();
        List<ChatEmojiUserResDto> result = chatEmojiService.getEmojiInfoInPrivateRoom(userId, chatId, emojiType);

        return new BaseResponse<>(result);
=======
    public BaseResponse<BaseResultDto> getEmojiDetailOfPrivate(@PathVariable Long chatId,
                                                               @RequestParam EmojiType emojiType) {
        Long userId = UserAuthorizationUtil.getLoginUserId();
        Long resultId = chatEmojiService.getEmojiDetailOfPrivate(userId, chatId, emojiType);

        return BaseResponse.getSimpleRes(resultId);
>>>>>>> Stashed changes
    }
}

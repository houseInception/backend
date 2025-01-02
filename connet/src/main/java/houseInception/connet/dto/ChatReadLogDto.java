package houseInception.connet.dto;

import houseInception.connet.domain.ChatRoomType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ChatReadLogDto {

    private ChatRoomType type;
    private Long tapId;
    private String privateRoomUuid;
    private Long chatId;
}

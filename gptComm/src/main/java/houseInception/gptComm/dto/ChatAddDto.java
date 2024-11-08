package houseInception.gptComm.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ChatAddDto {

    private String chatRoomUuid;

    @NotBlank
    private String message;
}

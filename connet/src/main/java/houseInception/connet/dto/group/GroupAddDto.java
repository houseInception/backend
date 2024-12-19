package houseInception.connet.dto.group;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GroupAddDto {

    @NotBlank
    private String groupName;
    private MultipartFile groupProfile;
    private String groupDefinition;

    @Size(max = 5)
    private List<String> tags;

    @Positive
    private int userLimit;
    private boolean isOpen;
}

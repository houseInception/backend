package houseInception.connet.domain.group;

import houseInception.connet.domain.BaseTime;
import houseInception.connet.domain.Status;
import houseInception.connet.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static houseInception.connet.domain.Status.ALIVE;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class GroupUser extends BaseTime {

    @Column(name = "groupUserId")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @JoinColumn(name = "userId")
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @JoinColumn(name = "groupId")
    @ManyToOne(fetch = FetchType.LAZY)
    private Group group;

    private boolean isOwner;

    @Enumerated(EnumType.STRING)
    private Status status = ALIVE;

    protected GroupUser(User user, Group group, boolean isOwner) {
        this.user = user;
        this.group = group;
        this.isOwner = isOwner;
    }
}

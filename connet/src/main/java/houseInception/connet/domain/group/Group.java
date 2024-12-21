package houseInception.connet.domain.group;

import houseInception.connet.domain.BaseTime;
import houseInception.connet.domain.Status;
import houseInception.connet.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static houseInception.connet.domain.Status.ALIVE;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "team")
@Entity
public class Group extends BaseTime {

    @Column(name = "groupId")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GroupUser> groupUserList = new ArrayList<>();

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GroupTag> groupTagList = new ArrayList<>();

    private String groupUuid;
    private String groupName;
    private String groupProfile;
    private String groupDescription;
    private int userLimit;
    private boolean isOpen;

    @Enumerated(EnumType.STRING)
    private Status status = ALIVE;

    public static Group create(User user, String groupName, String groupProfile, String groupDescription, int userLimit, boolean isOpen) {
        Group group = new Group();
        group.groupUuid = UUID.randomUUID().toString();
        group.groupName = groupName;
        group.groupProfile = groupProfile;
        group.groupDescription = groupDescription;
        group.userLimit = userLimit;
        group.isOpen = isOpen;

        group.addOwner(user);

        return group;
    }

    private void addOwner(User user){
        GroupUser groupUser = new GroupUser(user, this, true);
        this.groupUserList.add(groupUser);
    }

    public void addUser(User user){
        GroupUser groupUser = new GroupUser(user, this, false);
        this.groupUserList.add(groupUser);
    }

    public boolean addTag(List<String> tags){
        if(!isValidTag(tags)){
            return false;
        }

        tags.forEach(tag -> this.groupTagList.add(new GroupTag(tag, this)));
        return true;
    }

    private boolean isValidTag(List<String> tags){
        return tags.stream().noneMatch(tag -> tag.contains(" "));
    }

    public List<GroupUser> getGroupUserList() {
        return List.copyOf(groupUserList);
    }

    public List<GroupTag> getGroupTagList() {
        return List.copyOf(groupTagList);
    }
}

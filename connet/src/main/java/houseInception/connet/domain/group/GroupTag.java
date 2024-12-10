package houseInception.connet.domain.group;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class GroupTag {

    @Column(name = "groupTagId")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    private String tagName;

    @JoinColumn(name = "groupId")
    @ManyToOne(fetch = FetchType.LAZY)
    private Group group;
}

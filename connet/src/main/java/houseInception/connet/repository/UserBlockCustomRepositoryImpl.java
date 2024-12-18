package houseInception.connet.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import houseInception.connet.domain.QUser;
import houseInception.connet.domain.QUserBlock;
import houseInception.connet.domain.UserBlockType;
import houseInception.connet.dto.DefaultUserResDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static houseInception.connet.domain.QUser.user;
import static houseInception.connet.domain.QUserBlock.userBlock;
import static houseInception.connet.domain.UserBlockType.REQUEST;

@RequiredArgsConstructor
@Repository
public class UserBlockCustomRepositoryImpl implements UserBlockCustomRepository{

    private final JPAQueryFactory query;

    @Override
    public List<DefaultUserResDto> getBlockUserList(Long userId) {
        return query.select(Projections.constructor(DefaultUserResDto.class,
                        user.id, user.userName, user.userProfile))
                .from(userBlock)
                .innerJoin(user).on(user.id.eq(userBlock.target.id))
                .where(userBlock.user.id.eq(userId),
                        userBlock.blockType.eq(REQUEST))
                .orderBy(user.userName.asc())
                .fetch();
    }
}

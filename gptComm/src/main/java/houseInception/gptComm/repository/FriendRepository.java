package houseInception.gptComm.repository;

import houseInception.gptComm.domain.Friend;
import houseInception.gptComm.domain.FriendStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FriendRepository extends JpaRepository<Friend, Long>, FriendCustomRepository {
    boolean existsBySenderIdAndRecipientIdAndAcceptStatus(Long senderId, Long recipientId, FriendStatus acceptStatus);
    Optional<Friend> findBySenderIdAndRecipientIdAndAcceptStatus(Long senderId, Long recipientId, FriendStatus acceptStatus);
}
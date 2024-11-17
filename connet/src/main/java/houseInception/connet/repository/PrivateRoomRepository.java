package houseInception.connet.repository;

import houseInception.connet.domain.Status;
import houseInception.connet.domain.privateRoom.PrivateRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PrivateRoomRepository extends JpaRepository<PrivateRoom, Long>, PrivateRoomCustomRepository {

    Optional<PrivateRoom> findByPrivateRoomUuidAndStatus(String privateRoomUuid, Status status);
}
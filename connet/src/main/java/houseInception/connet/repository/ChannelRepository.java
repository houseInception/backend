package houseInception.connet.repository;

import houseInception.connet.domain.channel.Channel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChannelRepository extends JpaRepository<Channel, Long>, ChannelCustomRepository {
}

package fr.norlys.mobilite.repository;

import fr.norlys.mobilite.entity.Station;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StationRepository extends JpaRepository<Station, Long> {

    List<Station> findByActiveTrue();
}

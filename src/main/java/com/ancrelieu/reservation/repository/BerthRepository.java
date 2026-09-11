package com.ancrelieu.reservation.repository;

import com.ancrelieu.reservation.entity.Berth;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BerthRepository extends JpaRepository<Berth, Long> {

    List<Berth> findByActiveTrue();
}

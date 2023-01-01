package com.los.cmisbackend.dao;

import com.los.cmisbackend.entity.Event;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {
	Page<Event> findEventsById(Long id, Pageable pageable);
}

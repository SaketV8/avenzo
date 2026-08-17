package com.maurya.avenzo.repository;

import com.maurya.avenzo.constant.EventStatus;
import com.maurya.avenzo.entity.CategoryEntity;
import com.maurya.avenzo.entity.EventEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventRespository extends JpaRepository<EventEntity, Long> {

    List<EventEntity> findAllByStatus(EventStatus eventStatus);

}

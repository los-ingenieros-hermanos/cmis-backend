package com.los.cmisbackend.dao;

import com.los.cmisbackend.entity.Date;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DateRepository extends JpaRepository<Date, Long> {

}


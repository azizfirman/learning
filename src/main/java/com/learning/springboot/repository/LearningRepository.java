package com.learning.springboot.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.learning.springboot.entity.Learning;

public interface LearningRepository extends JpaRepository<Learning, UUID> {

}
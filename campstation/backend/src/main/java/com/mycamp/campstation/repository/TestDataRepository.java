package com.mycamp.campstation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mycamp.campstation.entity.TestData;

@Repository
public interface TestDataRepository extends JpaRepository<TestData, Long> {
}

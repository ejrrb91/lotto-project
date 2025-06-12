package com.example.lotto_project.repository;

import com.example.lotto_project.domain.Recommendation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {

}

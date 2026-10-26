package com.luxshan.snapify.repository;

import com.luxshan.snapify.model.Link;
import com.luxshan.snapify.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LinkRepository extends JpaRepository<Link, Long>{
    Optional<Link> findByShortCode(String shortCode);

    boolean existsByShortCode(String shortCode);

    int deleteByShortCode(String shortCode);

    List<Link> findAllByUser_Id(Long id);
}
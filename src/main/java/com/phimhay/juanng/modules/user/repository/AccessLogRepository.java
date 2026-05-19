package com.phimhay.juanng.modules.user.repository;

import com.phimhay.juanng.modules.user.entity.AccessLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccessLogRepository extends JpaRepository<AccessLog, Long> {
}

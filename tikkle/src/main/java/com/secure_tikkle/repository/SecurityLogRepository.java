package com.secure_tikkle.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.secure_tikkle.domain.SecurityLog;

public interface SecurityLogRepository extends JpaRepository<SecurityLog, Long> {

}

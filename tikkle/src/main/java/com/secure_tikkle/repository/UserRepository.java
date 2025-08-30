package com.secure_tikkle.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.secure_tikkle.domain.Provider;
import com.secure_tikkle.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByProviderAndUserKey(Provider provider, String userKey);
    Optional<User> findByEmail(String email); 
    
}

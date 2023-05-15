package com.apiator.shop.account;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {

    @Query("SELECT a from Account a where a.id = ?1")
    Optional<Account> getAccountById(UUID id);

    @Modifying
    @Query("UPDATE Account a set a.role = ?1 where a.id = ?2")
    void updateRoleById(Role role, UUID id);
}

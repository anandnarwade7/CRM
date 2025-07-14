package com.crm.chat;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface ChatsRepository extends JpaRepository<Chats, Long> {
	List<Chats> findBySupportIdOrderByCreatedOnDesc(long supportId);

	List<Chats> findByUserId(long userId);

	List<Chats> findByUserIdAndSupportId(long userId, long supportId);

	@Query("SELECT c FROM Chats c WHERE c.supportId = :supportId")
	List<Chats> findBySupportId(long supportId);

	boolean existsByUserIdAndSupportId(long userId, long supportId);

	boolean existsBySupportId(long supportId);

	boolean existsByUserId(long userId);

}

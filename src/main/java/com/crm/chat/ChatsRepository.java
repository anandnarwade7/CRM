package com.crm.chat;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatsRepository extends JpaRepository<Chats, Long>{

	boolean existsByUserId(long userId);

	List<Chats> findByUserId(long userId);

	boolean existsBySupportId(long supportId);

	boolean existsByUserIdAndSupportId(long userId, long supportId);

	List<Chats> findByUserIdAndSupportId(long userId, long supportId);

	List<Chats> findBySupportId(long supportId);

}

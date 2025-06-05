package com.crm.chat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.crm.notifications.Notifications;
import com.crm.notifications.NotificationsRepository;
import com.crm.support.Support;
import com.crm.support.SupportRepository;
import com.crm.user.User;
import com.crm.user.UserRepository;
import com.crm.user.UserServiceException;

@Service
public class ChatsService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private SupportRepository supportRepository;

	@Autowired
	private ChatsRepository chatsRepository;
	
	@Autowired
	private NotificationsRepository notificationsRepository;

	@Transactional
	public ResponseEntity<Chats> addChats(Chats chats) {
		try {
			Optional<User> userOptional = userRepository.findById(chats.getUserId());
			Optional<Support> supportOptional = supportRepository.findById(chats.getSupportId());

			if (userOptional.isPresent() && supportOptional.isPresent()) {
				User user = userOptional.get();
				Support support = supportOptional.get();

				chats.setCreatedOn(System.currentTimeMillis());
				Chats newChats = chatsRepository.save(chats);

//				messagingTemplate.convertAndSend("/topic/comments", newComment);

				String notificationMessage = "Chats added by " + user.getName() + " (" + user.getRole() + ")" + " for "
						+ support.getQuery();
				User supportUserId = support.getUser();

				if (chats.getUserId() == supportUserId.getId()) {
					List<User> admins = userRepository.findByRole("Admin");
					for (User admin : admins) {
						Notifications notification = new Notifications(false, notificationMessage, admin.getEmail(),
								"checkComments", System.currentTimeMillis());
						notificationsRepository.save(notification);
					}
				} else {
					Notifications notification = new Notifications(false, notificationMessage, supportUserId.getEmail(),
							"checkCohats", System.currentTimeMillis());
					notificationsRepository.save(notification);
				}
				return ResponseEntity.ok(newChats);
			} else {
				throw new UserServiceException(401, "User or Support not found");
			}
		} catch (UserServiceException e) {
			e.printStackTrace();
		}
		return null;
	}

	public List<Chats> getChatsByUserId(long userId) {
		try {
			if (!chatsRepository.existsByUserId(userId)) {
				return Collections.emptyList();
			} else {
				return chatsRepository.findByUserId(userId);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return Collections.emptyList();
		}
	}

	public List<Chats> getChatsByUserIdAndSupportId(long userId, long supportId) {
		if (!chatsRepository.existsByUserId(userId) || !chatsRepository.existsBySupportId(supportId)) {
			throw new UserServiceException(401, "UserId or SupportId not found");
		}
		boolean mappingExists = chatsRepository.existsByUserIdAndSupportId(userId, supportId);
		if (!mappingExists) {
			throw new UserServiceException(401, "userId and supportId are not mapped together");
		}
		try {
			return chatsRepository.findByUserIdAndSupportId(userId, supportId);
		} catch (Exception e) {
			return Collections.emptyList();
		}
	}

	public List<Map<String, Object>> getChatsBySupportId(long supportId) {
		try {

			List<Map<String, Object>> result = new ArrayList<>();

			if (!chatsRepository.existsBySupportId(supportId)) {
				throw new UserServiceException(401, "supportId not found");
			} else {

				List<Chats> bySupportId = chatsRepository.findBySupportId(supportId);
				for (Chats com : bySupportId) {
					long userId = com.getUserId();
					Optional<User> user = userRepository.findById(userId);
					User userById = user.get();
					String name = userById.getName();
					String[] words = name.split(" ");
					String initials = "";

					for (String word : words) {
						if (!word.isEmpty()) {
							initials += word.substring(0, 1).toUpperCase();
						}
					}
//					System.out.println("Initials: " + initials);
					Map<String, Object> commentWithInitials = new HashMap<>();
					commentWithInitials.put("id", com.getId());
					commentWithInitials.put("userId", com.getUserId());
					commentWithInitials.put("supportId", com.getSupportId());
					commentWithInitials.put("chat", com.getMassages());
					commentWithInitials.put("createdOn", com.getCreatedOn());
					commentWithInitials.put("initials", initials);
					result.add(commentWithInitials);

				}
//				messagingTemplate.convertAndSend("/topic/comments", result);

				return result;
			}
		} catch (Exception e) {
			return Collections.emptyList();
		}
	}

}

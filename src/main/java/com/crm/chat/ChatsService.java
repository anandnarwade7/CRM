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
	private ChatsRepository chatsRepo;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private NotificationsRepository notificationsRepository;
//	@Autowired
//	private SimpMessagingTemplate messagingTemplate;
	@Autowired
	private SupportRepository supportRepository;

	@Transactional
	public ResponseEntity<Chats> addChats(Chats chats) {
		try {
			Optional<User> userOptional = userRepository.findById(chats.getUserId());
			Optional<Support> supportOptional = supportRepository.findById(chats.getSupportId());

			if (userOptional.isPresent() && supportOptional.isPresent()) {
				User user = userOptional.get();
				Support support = supportOptional.get();

				chats.setCreatedOn(System.currentTimeMillis());
				Chats newChats = chatsRepo.save(chats);

//				messagingTemplate.convertAndSend("/topic/comments", newComment);

				String notificationMessage = "Chats added by " + user.getName() + " (" + user.getRole() + ")" + " for "
						+ support.getQuery();
				Optional<User> supportUser = userRepository.findById(support.getUserId());
				User supportUserId = supportUser.get();

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
			if (!chatsRepo.existsByUserId(userId)) {
				return Collections.emptyList();
			} else {
				return chatsRepo.findByUserId(userId);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return Collections.emptyList();
		}
	}

	public List<Chats> getChatsByUserIdAndSupportId(long userId, long supportId) {
		if (!chatsRepo.existsByUserId(userId) || !chatsRepo.existsBySupportId(supportId)) {
			throw new UserServiceException(401, "UserId or SupportId not found");
		}
		boolean mappingExists = chatsRepo.existsByUserIdAndSupportId(userId, supportId);
		if (!mappingExists) {
			throw new UserServiceException(401, "userId and supportId are not mapped together");
		}
		try {
			return chatsRepo.findByUserIdAndSupportId(userId, supportId);
		} catch (Exception e) {
			return Collections.emptyList();
		}
	}

	public List<Map<String, Object>> getChatsBySupportId(long supportId) {
		try {

			List<Map<String, Object>> result = new ArrayList<>();

			if (!chatsRepo.existsBySupportId(supportId)) {
				throw new UserServiceException(401, "supportId not found");
			} else {

				List<Chats> bySupportId = chatsRepo.findBySupportId(supportId);
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
					Map<String, Object> chatsWithInitials = new HashMap<>();
					chatsWithInitials.put("id", com.getId());
					chatsWithInitials.put("userId", com.getUserId());
					chatsWithInitials.put("supportId", com.getSupportId());
					chatsWithInitials.put("massages", com.getMassages());
					chatsWithInitials.put("createdOn", com.getCreatedOn());
					chatsWithInitials.put("initials", initials);
					result.add(chatsWithInitials);

				}
//				messagingTemplate.convertAndSend("/topic/Chatss", result);

				return result;
			}
		} catch (Exception e) {
			return Collections.emptyList();
		}
	}

}

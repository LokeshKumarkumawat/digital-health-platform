package com.digitalhealth.platform;

import com.digitalhealth.platform.notification.dto.NotificationCreateRequest;
import com.digitalhealth.platform.notification.service.NotificationService;
import com.digitalhealth.platform.users.entity.User;
import com.digitalhealth.platform.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
@EnableAsync
@RequiredArgsConstructor
public class DigitalHealthPlatformApplication {



	public static void main(String[] args) {
		SpringApplication.run(DigitalHealthPlatformApplication.class, args);
	}

//
//	@Bean
//	CommandLineRunner runner(UserRepository userRepository, NotificationService notificationService){
//		return args -> {
//
//			User user = userRepository.findById(5L)
//					.orElseThrow(() -> new RuntimeException("User not found"));
//
//			Map<String, Object> variables = new HashMap<>();
//			variables.put("name", "Lokesh");
//			variables.put("resetLink", "https://app.digitalhealth.com/reset?token=abc123");
//
//			NotificationCreateRequest request = NotificationCreateRequest.builder()
//					.recipient("lokeshkumawat1225@gmail.com")
//					.subject("Testing Email")
//					.message("Hey, this is a test mail")
//					.templateName("password-reset")
//					.templateVariables(variables)
//					.build();
//			notificationService.sendEmail(request, user);
//		};
//	}
}

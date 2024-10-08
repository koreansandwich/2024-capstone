package backend.service;


import backend.entity.ChatMessage;
import backend.entity.User;
import backend.repository.ChatMessageRepository;
import backend.repository.UserRepository;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ChatService {
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;
    private final String OPENAI_API_KEY = System.getenv("OPENAI_API_KEY");


    public ChatService(ChatMessageRepository chatMessageRepository, UserRepository userRepository) {
        this.chatMessageRepository = chatMessageRepository;
        this.userRepository = userRepository;
    }

    public List<ChatMessage> getChatHistory(Long userId) {
        return chatMessageRepository.findByUserIdOrderByTimestampDesc(userId);
    }

    public ChatMessage saveUserMessage(Long userId, String message) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setUser(user);
        chatMessage.setMessage(message);
        chatMessage.setSender("user");
        chatMessage.setTimestamp(LocalDateTime.now());

        System.out.println("chatMessage : " + chatMessage);

        return chatMessageRepository.save(chatMessage);
    }

    public ChatMessage saveBotMessage(Long userId, String userMessage) {

        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        String url = "https://api.openai.com/v1/chat/completions";

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(OPENAI_API_KEY);

        JSONObject requestBody = new JSONObject();
        requestBody.put("model", "gpt-4");
        requestBody.put("messages", new JSONArray().put(new JSONObject().put("role", "user").put("content", userMessage)
        ));
        requestBody.put("max_tokens", 150);
        requestBody.put("temperature", 0.7);

        System.out.println("Using API Key: " + OPENAI_API_KEY);

        // Request Body를 출력해서 확인
        System.out.println("Request Body: " + requestBody.toString());

        HttpEntity<String> request = new HttpEntity<>(requestBody.toString(), headers);

        System.out.println("Sending request to OpenAI API...");

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {

            System.out.println("OpenAI API Response: " + response.getBody());

            JSONObject responseBody = new JSONObject(response.getBody());
            String botResponse = responseBody.getJSONArray("choices").getJSONObject(0)
                    .getJSONObject("message").getString("content").trim();

            System.out.println("Extracted Bot Response: " + botResponse);

            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setUser(user);
            chatMessage.setMessage(botResponse);
            chatMessage.setSender("bot");
            chatMessage.setTimestamp(LocalDateTime.now());

            return chatMessageRepository.save(chatMessage);
        } else {
            System.out.println("Failed to request from OpenAI. Status Code: " + response.getStatusCode());
            throw new RuntimeException("Failed to request from OpenAI");
        }
    }
}

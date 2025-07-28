package com.email.writer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service
public class EmailGeneratorService {
    private final WebClient webClient;

    private String geminiApiUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent";

    private String geminiApiKey = "AIzaSyC2Sl8bbi8qlO1KdhOlqzGdD_fT12h4Amg";

    public EmailGeneratorService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    EmailRequest emailRequest;

//    public String generateEmailReply(EmailRequest emailRequest){
//        //build prompt
//
//        String prompt=buildPrompt(emailRequest);
//        Map<String, Object>reqestBody=Map.of(
//                "content",new Object[]{
//                        Map.of(
//                             "parts",new Object[]{
//                                     Map.of(
//                                             "text",prompt
//                                     )
//                                }
//                        )
//                }
//        );
//
//        String response = webClient.post()
//                .uri(geminiApiUrl+geminiApiKey)
//                .header("Content-Type","application/json")
//                .bodyValue(reqestBody)
//                .retrieve()
//                .bodyToMono(String.class)
//                .block();
//
//        return extractResponse(response);
//
//
//    }

    public String generateEmailReply(EmailRequest emailRequest) {
        String prompt = buildPrompt(emailRequest);

        // Request body as Map
        Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(
                                Map.of("text", prompt)
                        ))
                )
        );

        // Call Gemini API
        String response = webClient.post()
                .uri(geminiApiUrl)
                .header("Content-Type", "application/json")
                .header("X-goog-api-key", geminiApiKey) // <-- Pass API key as header (not in URL)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        return extractResponse(response);
    }


    private String extractResponse(String response) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(response);
            return rootNode.path("candidates")
                    .get(0).path("content").path("parts").get(0).path("text").asText();

        } catch (Exception e) {
            return "Error processing request " + e;
        }
    }

    private String buildPrompt(EmailRequest emailRequest) {
        StringBuilder prompt = new StringBuilder();

        prompt.append("Generate a professional email reply for the following message. ")
                .append("Do not include any explanation, header, subject line, or formatting. ")
                .append("Only return the email body text as plain content. ");

        if (emailRequest.getTone() != null && !emailRequest.getTone().isEmpty()) {
            prompt.append("Use a ").append(emailRequest.getTone()).append(" tone. ");
        }

        prompt.append("\nOriginal email:\n")
                .append(emailRequest.getEmailContent());

        return prompt.toString();
    }
}

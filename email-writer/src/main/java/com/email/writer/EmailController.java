package com.email.writer;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/email")
public class EmailController {

    private final EmailGeneratorService emailGeneratorService;

    public EmailController(EmailGeneratorService emailGeneratorService) {
        this.emailGeneratorService = emailGeneratorService;
    }

    @PostMapping("/generate")
    public ResponseEntity<String> generateEmail(@RequestBody EmailRequest prompt) {
        String email = emailGeneratorService.generateEmailReply(prompt);
        return ResponseEntity.ok(email);
    }
}

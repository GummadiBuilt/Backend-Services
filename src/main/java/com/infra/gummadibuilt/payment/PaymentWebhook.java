package com.infra.gummadibuilt.payment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.infra.gummadibuilt.userregistration.UserRegistrationService;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/payment")
public class PaymentWebhook {
    private static final Logger logger = LoggerFactory.getLogger(UserRegistrationService.class);

    @PostMapping
    private void listenForPayment(HttpServletRequest request, @RequestBody String eventPayload) throws JsonProcessingException, RazorpayException {

        String expectedSignature = request.getHeader("X-Razorpay-Signature");
        boolean valid = Utils.verifyWebhookSignature(eventPayload, expectedSignature, "123456");
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonObject = objectMapper.readTree(eventPayload);
        JsonNode paymentEntity = jsonObject.get("payload").get("payment_link").get("entity");
        String referenceId = paymentEntity.get("reference_id").asText();
        if (valid) {
            String paymentStatus = paymentEntity.get("status").asText();
            String paymentLink = paymentEntity.get("id").asText();
            String paymentAmount = paymentEntity.get("amount_paid").asText();
            logger.info("Looking up for reference id {}", referenceId);
            logger.info("Processing payment link {} for amount {} with status {}", paymentLink, paymentAmount, paymentStatus);
        } else {
            logger.error("Invalid webhook signature for request {}", referenceId);
        }
    }

}

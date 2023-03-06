package com.infra.gummadibuilt.payment;

import com.infra.gummadibuilt.payment.model.dto.PaymentDto;
import com.infra.gummadibuilt.payment.model.dto.PaymentTransactionDto;
import com.infra.gummadibuilt.payment.model.dto.TenderPayUserDto;
import com.razorpay.RazorpayException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;

@RestController
@RequestMapping("/payment/{tenderId}")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Payment APIs",
        description = "APIs that can generate payment links to clients & contractor's")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @Operation(summary = "Get client & applying contractors for the given tender")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success, when client & contractors list is returned",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = TenderPayUserDto.class)))),
            @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")
    })
    @RolesAllowed({"admin"})
    @GetMapping
    public List<TenderPayUserDto> getPayeeUser(@PathVariable("tenderId") @NotBlank String tenderId) {
        return paymentService.getPayeeUser(tenderId);
    }

    @PostMapping("/generate-payment-link")
    public List<PaymentTransactionDto> generatePaymentLink(@PathVariable("tenderId") @NotBlank String tenderId,
                                                           @Valid @RequestBody PaymentDto paymentDto,
                                                           HttpServletRequest request) throws RazorpayException {
        return paymentService.generatePaymentLink(tenderId, paymentDto, request);
    }

}

package com.infra.gummadibuilt.payment;

import com.infra.gummadibuilt.common.ChangeTracking;
import com.infra.gummadibuilt.common.LoggedInUser;
import com.infra.gummadibuilt.common.util.SaveEntityConstraintHelper;
import com.infra.gummadibuilt.payment.model.Payment;
import com.infra.gummadibuilt.payment.model.PaymentClientConfiguration;
import com.infra.gummadibuilt.payment.model.dto.PaymentDto;
import com.infra.gummadibuilt.payment.model.dto.PaymentTransactionDto;
import com.infra.gummadibuilt.payment.model.dto.TenderPayUserDto;
import com.infra.gummadibuilt.tender.TenderInfoDao;
import com.infra.gummadibuilt.tender.model.TenderInfo;
import com.infra.gummadibuilt.tenderapplicants.TenderApplicantsDao;
import com.infra.gummadibuilt.tenderapplicants.model.TenderApplicants;
import com.infra.gummadibuilt.userandrole.ApplicationRoleDao;
import com.infra.gummadibuilt.userandrole.model.ApplicationRole;
import com.infra.gummadibuilt.userandrole.model.ApplicationUser;
import com.infra.gummadibuilt.userandrole.model.dto.ApplicationRoleDto;
import com.razorpay.PaymentLink;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.infra.gummadibuilt.common.util.CommonModuleUtils.*;
import static com.infra.gummadibuilt.common.util.UserInfo.loggedInUserInfo;
import static java.time.format.TextStyle.SHORT;
import static java.util.Locale.ENGLISH;

@Service
public class PaymentService {

    private final PaymentRepo paymentRepo;
    private final TenderInfoDao tenderInfoDao;
    private final TenderApplicantsDao tenderApplicantsDao;

    private final PaymentClientConfiguration paymentClientConfiguration;
    private final ApplicationRoleDao applicationRoleDao;

    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);

    public PaymentService(PaymentRepo paymentRepo,
                          TenderInfoDao tenderInfoDao,
                          TenderApplicantsDao tenderApplicantsDao,
                          ApplicationRoleDao applicationRoleDao,
                          PaymentClientConfiguration paymentClientConfiguration) {
        this.paymentRepo = paymentRepo;
        this.tenderInfoDao = tenderInfoDao;
        this.tenderApplicantsDao = tenderApplicantsDao;
        this.applicationRoleDao = applicationRoleDao;
        this.paymentClientConfiguration = paymentClientConfiguration;
    }


    public List<TenderPayUserDto> getPayeeUser(String tenderId) {
        TenderInfo tenderInfo = getById(tenderInfoDao, tenderId, TENDER_NOT_FOUND);
        var tenderPayUsers = new ArrayList<TenderPayUserDto>();
        var client = new TenderPayUserDto();
        ApplicationUser applicationUser = tenderInfo.getApplicationUser();
        client.setApplicationRole(ApplicationRoleDto.valueOf(applicationUser.getApplicationRole()));
        client.setCompanyName(applicationUser.getCompanyName());
        client.setContactName(String.format("%s, %s", applicationUser.getContactFirstName(), applicationUser.getContactLastName()));
        client.setContactPhoneNumber(applicationUser.getContactPhoneNumber());
        client.setContactEmailAddress(applicationUser.getContactEmailAddress());
        client.setContactAddress(applicationUser.getAddress());
        tenderPayUsers.add(client);

        List<TenderApplicants> tenderApplicants = tenderApplicantsDao.findAllByTenderInfo(tenderInfo);

        tenderApplicants.forEach(applicants -> {
            var contractors = new TenderPayUserDto();
            ApplicationUser appUser = applicants.getApplicationUser();
            contractors.setApplicationRole(ApplicationRoleDto.valueOf(appUser.getApplicationRole()));
            contractors.setCompanyName(appUser.getCompanyName());
            contractors.setContactName(String.format("%s, %s", appUser.getContactFirstName(), appUser.getContactLastName()));
            contractors.setContactPhoneNumber(appUser.getContactPhoneNumber());
            contractors.setContactEmailAddress(appUser.getContactEmailAddress());
            contractors.setContactAddress(appUser.getAddress());
            tenderPayUsers.add(contractors);
        });
        return tenderPayUsers;
    }

    public List<PaymentTransactionDto> generatePaymentLink(String tenderId,
                                                           PaymentDto paymentDto,
                                                           HttpServletRequest request) throws RazorpayException {

        TenderInfo tenderInfo = getById(tenderInfoDao, tenderId, TENDER_NOT_FOUND);
        ApplicationRole applicationRole = getById(applicationRoleDao, paymentDto.getRoleId(), ROLE_NOT_FOUND);
        PaymentLink paymentLink = createPaymentLink(tenderInfo, paymentDto);
        LoggedInUser loggedInUser = loggedInUserInfo(request);

        var status = paymentLink.toJson().get("status").toString();
        logger.info("Payment link creation status {} for tender {}", status, tenderInfo.getId());

        if (status.equalsIgnoreCase("created")) {
            var id = paymentLink.toJson().get("id").toString();
            var amount = paymentLink.toJson().get("amount").toString();
            var expiry = paymentLink.toJson().get("expire_by").toString();
            var paymentReferenceId = paymentLink.toJson().get("reference_id").toString();
            var shortUrl = paymentLink.toJson().get("short_url").toString();

            Payment payment = new Payment();
            payment.setReferenceId(paymentReferenceId);
            payment.setPaymentAmount(Long.parseLong(amount));
            payment.setApplicationRole(applicationRole);
            payment.setTenderInfo(tenderInfo);
            payment.setShortUrl(shortUrl);
            payment.setPaymentId(id);
            payment.setExpireBy(Long.parseLong(expiry));
            payment.setPaymentDescription(paymentDto.getPaymentDescription());
            payment.setContactName(paymentDto.getContactName());
            payment.setContactPhoneNumber(paymentDto.getContactPhoneNumber());
            payment.setNotifyViaSms(paymentDto.getNotifyViaSms());
            payment.setContactEmailAddress(paymentDto.getContactEmailAddress());
            payment.setNotifyViaEmail(paymentDto.getNotifyViaEmail());
            payment.setChangeTracking(new ChangeTracking(loggedInUser.toString()));

            logger.info("Saving payment entity for tender {}", tenderInfo.getId());
            SaveEntityConstraintHelper.save(paymentRepo, payment, null);

            return paymentRepo.findAllByTenderInfo(tenderInfo).stream().map(PaymentTransactionDto::valueOf).collect(Collectors.toList());
        } else {
            throw new PaymentLinkCreationException(
                    String.format("Failed to create payment link for tender %s with reason %s", tenderId, status)
            );
        }

    }

    private PaymentLink createPaymentLink(TenderInfo tenderInfo, PaymentDto paymentDto) throws RazorpayException {
        logger.info("Initiating payment for tender {}", tenderInfo.getId());
        RazorpayClient razorpay = new RazorpayClient(paymentClientConfiguration.getClientKey(), paymentClientConfiguration.getClientSecret());
        JSONObject paymentLinkRequest = new JSONObject();
        paymentLinkRequest.put("amount", paymentDto.getPaymentAmount());
        paymentLinkRequest.put("currency", "INR");
        paymentLinkRequest.put("accept_partial", false);
        final LocalTime timeNow = LocalTime.now();
        final LocalDate todayDate = LocalDate.now();
        String referenceId = "G"
                + tenderInfo.getId().substring(0, 3)
                + paymentDto.getContactName().substring(0, 3)
                + todayDate.getDayOfMonth()
                + todayDate.getMonth().getDisplayName(SHORT, ENGLISH)
                + timeNow.getHour()
                + timeNow.getMinute()
                + timeNow.getSecond()
                + timeNow.getNano();

        logger.info("Reference ID {} created for payment of tender {}", referenceId, tenderInfo.getId());

        paymentLinkRequest.put("reference_id", referenceId);
        paymentLinkRequest.put("description", paymentDto.getPaymentDescription());
        JSONObject customer = new JSONObject();
        JSONObject notify = new JSONObject();

        customer.put("name", paymentDto.getContactName());
        if (paymentDto.getContactPhoneNumber().length() == 13) {
            customer.put("contact", paymentDto.getContactPhoneNumber());
            notify.put("sms", paymentDto.getNotifyViaSms());
        }

        if (paymentDto.getContactEmailAddress().length() > 0) {
            customer.put("email", paymentDto.getContactEmailAddress());
            notify.put("email", paymentDto.getNotifyViaEmail());
        }

        JSONObject options = new JSONObject();
        JSONObject theme = new JSONObject();
        theme.put("hide_topbar", false);

        JSONObject checkout = new JSONObject();
        checkout.put("theme", theme);
        checkout.put("name", "GummadiBuilt LLC");
        options.put("checkout", checkout);

        paymentLinkRequest.put("customer", customer);
        paymentLinkRequest.put("notify", notify);
        paymentLinkRequest.put("reminder_enable", true);
        paymentLinkRequest.put("options", options);
        paymentLinkRequest.put("callback_url", "https://gummadibuilt.com/");
        paymentLinkRequest.put("callback_method", "get");

        logger.info("Sending to payment client to initiate payment link for tender {}", tenderInfo.getId());
        return razorpay.paymentLink.create(paymentLinkRequest);
    }

}

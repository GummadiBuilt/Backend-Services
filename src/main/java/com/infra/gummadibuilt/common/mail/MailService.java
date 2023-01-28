package com.infra.gummadibuilt.common.mail;

import freemarker.cache.ClassTemplateLoader;
import freemarker.core.Environment;
import freemarker.template.*;
import freemarker.template.utility.NullWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;

/**
 * Provides functions related to sending mail.
 */
@Service
public class MailService {

    private static final Logger logger = LoggerFactory.getLogger(MailService.class);

    private final JavaMailSender mailSender;

    private final Configuration freemarker;

    private final MailConfiguration configuration;

    public MailService(MailConfiguration configuration, JavaMailSender mailSender) {
        this.mailSender = mailSender;
        this.configuration = configuration;

        this.freemarker = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
        this.freemarker.setTemplateLoader(new ClassTemplateLoader(MailService.class, "/templates/mail"));
    }

    public void sendMail(String[] to, String[] cc, String templateName, Map<String, Object> model) throws IOException, MessagingException, TemplateException {

        if (configuration.isEnableMailFeature()) {

            Template template = freemarker.getTemplate(templateName);

            Environment env;
            try {
                env = template.createProcessingEnvironment(model, NullWriter.INSTANCE);
                env.getMainNamespace().put("recipient", to);
                env.getMainNamespace().put("sender", configuration.getFromAddress());
                env.getMainNamespace().put("appUrl", configuration.getApplicationLinkBase());
                env.process();
            } catch (TemplateException e) {
                throw new RuntimeException("Unable to apply mail template " + templateName, e);
            }

            String subject = getStringVariable(env, "subject");
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name());

            model.put("appUrl", configuration.getApplicationLinkBase());

            File file = new File(configuration.getHeaderPath());
            String html = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
            helper.setTo(to);
            if (cc != null) {
                helper.setCc(cc);
            }
            helper.setText(html, true);
            helper.setSubject(subject);
            helper.setFrom(configuration.getFromAddress());
            helper.addAttachment("logo.png", file);

            mailSender.send(message);

            logger.info(String.format("Mail sent successfully to user %s", Arrays.toString(to)));
        } else {
            logger.info("Mail feature is disabled, emails will not be sent. Enable mail feature to send emails");
        }
    }

    private String getStringVariable(Environment env, String name) {
        Environment.Namespace namespace = env.getMainNamespace();
        TemplateModel templateModel;
        try {
            templateModel = namespace.get(name);
        } catch (TemplateModelException e) {
            throw new IllegalStateException("The mail template doesn't set a variable '" + name + "'");
        }

        if (!(templateModel instanceof TemplateScalarModel)) {
            throw new IllegalStateException("The mail template sets variable '" + name + "', but it's not a string." +
                    " It's a " + templateModel);
        }

        try {
            return ((TemplateScalarModel) templateModel).getAsString().trim();
        } catch (TemplateModelException e) {
            throw new IllegalStateException("Unable to get variable '" + name + "' from template as string.", e);
        }
    }

}

package com.trackwize.common.jms;

import com.trackwize.common.util.LogUtil;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.TextMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;

import java.time.Duration;
import java.time.Instant;

@Slf4j
public abstract class JmsListenerBase {

    private static final int MAX_PAYLOAD_LENGTH = 500;

    public final void handle(Message message) throws JMSException {
        Instant start = Instant.now();
        String trackingId = message.getJMSCorrelationID();
        String messageId = getMessageId(message);
        String destination = getDestination(message);
        String payload;
        if (message instanceof TextMessage textMessage) {
            payload = textMessage.getText();
        }else {
            payload = message.toString();
        }

        MDC.put("trackingId", trackingId != null ? trackingId : messageId);
        MDC.put("destination", destination);

        try {
            log.info(LogUtil.repeatCharLine('=', null));
            log.info("Incoming JMS Message");
            log.info("     queue        : {}", destination);
            log.info("     trackingId   : {}", trackingId != null ? trackingId : messageId);
            LogUtil.logPrettyJson(log, " payload     ", payload);

            log.info(LogUtil.repeatCharLine('-', null));

            onMessage(message);

            log.info(LogUtil.repeatCharLine('-', null));
            Instant end = Instant.now();
            log.info("Outgoing JMS Processing");
            log.info("    status        : SUCCESS");
            log.info("    processed in  : {} ms", Duration.between(start, end).toMillis());
            log.info(LogUtil.repeatCharLine('=', null));

        } catch (Exception e) {
            log.info(LogUtil.repeatCharLine('-', null));
            Instant end = Instant.now();
            log.error("Outgoing JMS Processing");
            log.error("    status        : FAILED");
            log.error("    processed in  : {} ms", Duration.between(start, end).toMillis());
            log.error("Error while processing JMS message with trackingId={}", trackingId, e);
            log.info(LogUtil.repeatCharLine('=', null));
        } finally {
            MDC.clear();
        }
    }

    protected abstract void onMessage(Message message) throws Exception;

    private String getMessageId(Message message) {
        try {
            return StringUtils.defaultIfBlank(message.getJMSMessageID(), "N/A");
        } catch (Exception e) {
            log.warn("Failed to read JMSMessageID", e);
            return "N/A";
        }
    }

    private String getDestination(Message message) {
        try {
            return message.getJMSDestination() != null
                    ? message.getJMSDestination().toString()
                    : "UNKNOWN";
        } catch (Exception e) {
            log.warn("Failed to read JMSDestination", e);
            return "UNKNOWN";
        }
    }

    private String truncateMessage(Message message) {
        try {
            String payload = message.getBody(String.class);
            if (payload.length() > MAX_PAYLOAD_LENGTH) {
                return payload.substring(0, MAX_PAYLOAD_LENGTH) + "...[truncated]";
            }
            return payload;
        } catch (Exception e) {
            return "[unable to read payload]";
        }
    }
}


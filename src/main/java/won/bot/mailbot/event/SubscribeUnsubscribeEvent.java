package won.bot.mailbot.event;

import won.bot.framework.eventbot.event.BaseEvent;
import won.bot.mailbot.enums.SubscribeStatus;

import javax.mail.internet.MimeMessage;

/**
 * Created by hfriedrich on 16.11.2016.
 */
public class SubscribeUnsubscribeEvent extends BaseEvent {
    private final MimeMessage message;
    private final SubscribeStatus subscribeStatus;

    public SubscribeUnsubscribeEvent(MimeMessage message, SubscribeStatus subscribeStatus) {
        this.message = message;
        this.subscribeStatus = subscribeStatus;
    }

    public MimeMessage getMessage() {
        return message;
    }

    public SubscribeStatus getSubscribeStatus() {
        return subscribeStatus;
    }
}

package won.bot.mailbot.event;

import won.bot.framework.eventbot.event.BaseEvent;

import javax.mail.internet.MimeMessage;

/**
 * Created by fsuda on 18.10.2016.
 */
public class CreateAtomFromMailEvent extends BaseEvent {
    private final MimeMessage message;

    public CreateAtomFromMailEvent(MimeMessage message) {
        this.message = message;
    }

    public MimeMessage getMessage() {
        return message;
    }
}

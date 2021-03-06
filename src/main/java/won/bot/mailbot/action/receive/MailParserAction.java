package won.bot.mailbot.action.receive;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import won.bot.framework.eventbot.EventListenerContext;
import won.bot.framework.eventbot.action.BaseEventBotAction;
import won.bot.framework.eventbot.bus.EventBus;
import won.bot.framework.eventbot.event.Event;
import won.bot.framework.eventbot.listener.EventListener;
import won.bot.mailbot.context.MailBotContextWrapper;
import won.bot.mailbot.enums.SubscribeStatus;
import won.bot.mailbot.event.CreateAtomFromMailEvent;
import won.bot.mailbot.event.MailCommandEvent;
import won.bot.mailbot.event.MailReceivedEvent;
import won.bot.mailbot.event.WelcomeMailEvent;
import won.bot.mailbot.util.MailContentExtractor;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.lang.invoke.MethodHandles;

/**
 * Created by fsuda on 30.09.2016.
 */
public class MailParserAction extends BaseEventBotAction {
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private MailContentExtractor mailContentExtractor;

    public MailParserAction(EventListenerContext eventListenerContext, MailContentExtractor mailContentExtractor) {
        super(eventListenerContext);
        this.mailContentExtractor = mailContentExtractor;
    }

    protected void doRun(Event event, EventListener executingListener) throws Exception {
        if (event instanceof MailReceivedEvent) {
            EventBus bus = getEventListenerContext().getEventBus();
            MimeMessage message = ((MailReceivedEvent) event).getMessage();
            String senderMailAddress = MailContentExtractor.getMailSender(message);
            try {
                if (mailContentExtractor.isCreateAtomMail(message)) {
                    processCreateAtomMail(message);
                } else if (mailContentExtractor.isCommandMail(message)) {
                    logger.debug("received a command mail publishing the MailCommand event");
                    bus.publish(new MailCommandEvent(message));
                } else {
                    logger.warn("unknown mail from user '{}' with subject '{}', no further processing required",
                                    senderMailAddress, message.getSubject());
                }
            } catch (MessagingException me) {
                logger.error("Messaging exception occurred while processing MimeMessage:", me);
                logger.warn("mail from user '{}' with subject '{}' could not be processed", senderMailAddress,
                                message.getSubject());
            }
        }
    }

    private void processCreateAtomMail(MimeMessage message) throws MessagingException, IOException {
        EventListenerContext ctx = getEventListenerContext();
        EventBus bus = ctx.getEventBus();
        String senderMailAddress = MailContentExtractor.getMailSender(message);
        MailBotContextWrapper botContextWrapper = ((MailBotContextWrapper) ctx.getBotContextWrapper());
        SubscribeStatus subscribeStatus = botContextWrapper.getSubscribeStatusForMailAddress(senderMailAddress);
        // depending of the user has subscribed/unsubscribed (via mailto links) his
        // mails will be
        // published as atoms, discarded or cached
        if (SubscribeStatus.SUBSCRIBED.equals(subscribeStatus)) {
            logger.info("received a create mail from subscribed user '{}' with subject '{}' so publish as atom",
                            senderMailAddress, message.getSubject());
            bus.publish(new CreateAtomFromMailEvent(message));
        } else if (SubscribeStatus.UNSUBSCRIBED.equals(subscribeStatus)) {
            logger.info("received mail from unsubscribed user '{}' so discard mail with subject '{}'",
                            senderMailAddress, message.getSubject());
        } else {
            logger.info("received a create mail from new user '{}' with subject '{}' so cache it and send welcome mail",
                            senderMailAddress, message.getSubject());
            botContextWrapper.addCachedMailsForMailAddress(message);
            bus.publish(new WelcomeMailEvent(message));
        }
    }
}

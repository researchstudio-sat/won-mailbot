package won.bot.mailbot.action.receive;

import won.bot.framework.eventbot.EventListenerContext;
import won.bot.framework.eventbot.action.BaseEventBotAction;
import won.bot.framework.eventbot.bus.EventBus;
import won.bot.framework.eventbot.event.Event;
import won.bot.framework.eventbot.listener.EventListener;
import won.bot.mailbot.context.MailBotContextWrapper;
import won.bot.mailbot.enums.SubscribeStatus;
import won.bot.mailbot.event.CreateAtomFromMailEvent;
import won.bot.mailbot.event.SubscribeUnsubscribeEvent;
import won.bot.mailbot.util.MailContentExtractor;

import javax.mail.internet.MimeMessage;
import java.util.Collection;

/**
 * Created by hfriedrich on 16.11.2016.
 */
public class SubscribeUnsubscribeAction extends BaseEventBotAction {
    public SubscribeUnsubscribeAction(EventListenerContext eventListenerContext) {
        super(eventListenerContext);
    }

    @Override
    protected void doRun(final Event event, EventListener executingListener) throws Exception {
        EventListenerContext ctx = getEventListenerContext();
        if (event instanceof SubscribeUnsubscribeEvent && ctx.getBotContextWrapper() instanceof MailBotContextWrapper) {
            MailBotContextWrapper botContextWrapper = (MailBotContextWrapper) ctx.getBotContextWrapper();
            // save the new subscription status of the user to the bot context
            SubscribeUnsubscribeEvent subscribeEvent = (SubscribeUnsubscribeEvent) event;
            SubscribeStatus subscribeStatus = subscribeEvent.getSubscribeStatus();
            String senderMailAddress = MailContentExtractor.getMailSender(subscribeEvent.getMessage());
            botContextWrapper.setSubscribeStatusForMailAddress(senderMailAddress, subscribeStatus);
            // depending on the new subscribe status of the user publish his cached mails as
            // atoms or delete the cache
            if (SubscribeStatus.SUBSCRIBED.equals(subscribeStatus)) {
                EventBus bus = getEventListenerContext().getEventBus();
                Collection<MimeMessage> cachedMessages = botContextWrapper
                                .loadCachedMailsForMailAddress(senderMailAddress);
                cachedMessages.stream().forEach(message -> bus.publish(new CreateAtomFromMailEvent(message)));
            } else if (SubscribeStatus.UNSUBSCRIBED.equals(subscribeStatus)) {
                botContextWrapper.removeCachedMailsForMailAddress(senderMailAddress);
            }
        }
    }
}

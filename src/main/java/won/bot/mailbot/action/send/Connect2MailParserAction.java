package won.bot.mailbot.action.send;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.GenericMessage;
import won.bot.framework.eventbot.EventListenerContext;
import won.bot.framework.eventbot.action.BaseEventBotAction;
import won.bot.framework.eventbot.event.Event;
import won.bot.framework.eventbot.event.impl.wonmessage.ConnectFromOtherAtomEvent;
import won.bot.framework.eventbot.listener.EventListener;
import won.bot.mailbot.context.MailBotContextWrapper;
import won.bot.mailbot.enums.UriType;
import won.bot.mailbot.util.MailContentExtractor;
import won.bot.mailbot.util.WonMimeMessageGenerator;
import won.bot.mailbot.util.model.WonURI;
import won.protocol.model.Connection;

import javax.mail.internet.MimeMessage;
import java.lang.invoke.MethodHandles;
import java.net.URI;

/**
 * Created by fsuda on 03.10.2016.
 */
public class Connect2MailParserAction extends BaseEventBotAction {
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private MessageChannel sendChannel;
    private WonMimeMessageGenerator mailGenerator;

    public Connect2MailParserAction(WonMimeMessageGenerator mailGenerator, MessageChannel sendChannel) {
        super(mailGenerator.getEventListenerContext());
        this.sendChannel = sendChannel;
        this.mailGenerator = mailGenerator;
    }

    @Override
    protected void doRun(Event event, EventListener executingListener) throws Exception {
        EventListenerContext ctx = getEventListenerContext();
        if (event instanceof ConnectFromOtherAtomEvent && ctx.getBotContextWrapper() instanceof MailBotContextWrapper) {
            MailBotContextWrapper botContextWrapper = (MailBotContextWrapper) ctx.getBotContextWrapper();
            Connection con = ((ConnectFromOtherAtomEvent) event).getCon();
            URI responseTo = con.getAtomURI();
            URI targetAtomUri = con.getTargetAtomURI();
            MimeMessage originalMail = botContextWrapper.getMimeMessageForURI(responseTo);
            logger.debug("Someone issued a connect for URI: " + responseTo + " sending a mail to the creator: "
                            + MailContentExtractor.getFromAddressString(originalMail));
            WonMimeMessage answerMessage = mailGenerator.createConnectMail(originalMail, targetAtomUri);
            botContextWrapper.addMailIdWonURIRelation(answerMessage.getMessageID(),
                            new WonURI(con.getConnectionURI(), UriType.CONNECTION));
            sendChannel.send(new GenericMessage<>(answerMessage));
        }
    }
}

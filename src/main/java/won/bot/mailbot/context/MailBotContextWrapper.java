package won.bot.mailbot.context;

import won.bot.framework.bot.context.BotContext;
import won.bot.framework.bot.context.BotContextWrapper;
import won.bot.mailbot.enums.SubscribeStatus;
import won.bot.mailbot.util.MailContentExtractor;
import won.bot.mailbot.util.model.WonURI;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

/**
 * Created by fsuda on 14.04.2017.
 */
public class MailBotContextWrapper extends BotContextWrapper {
    private String userSubscribeStatusName;
    private String userCachedMailsName;
    private String uriMimeMessageName;
    private String mailIdUriName;
    private String mailAddressUriName;

    public MailBotContextWrapper(BotContext botContext, String botName) {
        super(botContext, botName);
        this.userSubscribeStatusName = botName + ":subscribeStatus";
        this.userCachedMailsName = botName + ":userCachedMails";
        this.uriMimeMessageName = botName + ":uriMimeMessage";
        this.mailIdUriName = botName + ":mailIdUri";
        this.mailAddressUriName = botName + ":mailAddressUri";
    }

    // Util Methods to Get/Remove/Add Uri -> MimeMessage Relation
    public void removeUriMimeMessageRelation(URI atomURI) {
        getBotContext().removeFromObjectMap(uriMimeMessageName, atomURI.toString());
    }

    public MimeMessage getMimeMessageForURI(URI uri) throws MessagingException {
        // use the empty default session here for reconstructing the mime message
        byte[] byteMsg = (byte[]) getBotContext().loadFromObjectMap(uriMimeMessageName, uri.toString());
        ByteArrayInputStream is = new ByteArrayInputStream(byteMsg);
        return new MimeMessage(Session.getDefaultInstance(new Properties(), null), is);
    }

    public void addUriMimeMessageRelation(URI atomURI, MimeMessage mimeMessage) throws IOException, MessagingException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        mimeMessage.writeTo(os);
        getBotContext().saveToObjectMap(uriMimeMessageName, atomURI.toString(), os.toByteArray());
    }

    // Util Methods to Get/Remove/Add MailId -> URI Relation
    public void removeMailIdWonURIRelation(String mailId) {
        getBotContext().removeFromObjectMap(mailIdUriName, mailId);
    }

    public WonURI getWonURIForMailId(String mailId) {
        return (WonURI) getBotContext().loadFromObjectMap(mailIdUriName, mailId);
    }

    public void addMailIdWonURIRelation(String mailId, WonURI uri) {
        getBotContext().saveToObjectMap(mailIdUriName, mailId, uri);
    }

    // Util Methods to Get/Remove/Add MailId -> URI Relation
    public List<WonURI> getWonURIsForMailAddress(String mailAddress) {
        List<WonURI> uriList = new LinkedList<>();
        List<Object> objectList = getBotContext().loadFromListMap(mailAddressUriName, mailAddress);
        for (Object o : objectList) {
            uriList.add((WonURI) o);
        }
        return uriList;
    }

    public void addMailAddressWonURIRelation(String mailAddress, WonURI uri) {
        getBotContext().addToListMap(mailAddressUriName, mailAddress, uri);
    }

    public void addCachedMailsForMailAddress(MimeMessage mimeMessage) throws IOException, MessagingException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        mimeMessage.writeTo(os);
        getBotContext().addToListMap(userCachedMailsName, MailContentExtractor.getMailSender(mimeMessage),
                        os.toByteArray());
    }

    public Collection<MimeMessage> loadCachedMailsForMailAddress(String mailAddress) throws MessagingException {
        List<MimeMessage> mimeMessages = new LinkedList<>();
        List<Object> objectList = getBotContext().loadFromListMap(userCachedMailsName, mailAddress);
        for (Object o : objectList) {
            // use the empty default session here for reconstructing the mime message
            byte[] byteMsg = (byte[]) o;
            ByteArrayInputStream is = new ByteArrayInputStream(byteMsg);
            mimeMessages.add(new MimeMessage(Session.getDefaultInstance(new Properties(), null), is));
        }
        return mimeMessages;
    }

    public void removeCachedMailsForMailAddress(String mailAddress) {
        getBotContext().dropCollection(mailAddress);
    }

    public void setSubscribeStatusForMailAddress(String mailAddress, SubscribeStatus status) {
        getBotContext().saveToObjectMap(userSubscribeStatusName, mailAddress, status);
    }

    public SubscribeStatus getSubscribeStatusForMailAddress(String mailAddress) {
        SubscribeStatus status = (SubscribeStatus) getBotContext().loadFromObjectMap(userSubscribeStatusName,
                        mailAddress);
        return (status != null) ? SubscribeStatus.valueOf(status.name()) : SubscribeStatus.NO_RESPONSE;
    }
}

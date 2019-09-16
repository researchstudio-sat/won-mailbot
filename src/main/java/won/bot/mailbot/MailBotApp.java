package won.bot.mailbot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;

import javax.crypto.Cipher;
import java.lang.invoke.MethodHandles;

public class MailBotApp {
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    public static void main(String[] args) throws Exception {
        boolean failed = false;
        if(Cipher.getMaxAllowedKeyLength("AES") != Integer.MAX_VALUE) {
            logger.error("JCE unlimited strength encryption policy is not enabled, WoN applications will not work. Please consult the setup guide.");
            failed = true;
        }
        if(System.getProperty("WON_NODE_URI") == null && System.getenv("WON_NODE_URI") == null) {
            logger.error("WON_NODE_URI needs to be set to the node you want to connect to. e.g. https://hackathonnode.matchat.org/won");
            failed = true;
        }
        if(System.getProperty("WON_CONFIG_DIR") == null && System.getenv("WON_CONFIG_DIR") == null) {
            logger.error("WON_CONFIG_DIR needs to be set");
            failed = true;
        }
        if(System.getProperty("mailbot.email.user") == null && System.getenv("mailbot.email.user") == null) {
            logger.error("mailbot.email.user needs to be set");
            failed = true;
        }
        if(System.getProperty("mailbot.email.address") == null && System.getenv("mailbot.email.address") == null) {
            logger.error("mailbot.email.address needs to be set");
            failed = true;
        }
        if(System.getProperty("mailbot.email.name") == null && System.getenv("mailbot.email.name") == null) {
            logger.error("mailbot.email.name needs to be set");
            failed = true;
        }
        if(System.getProperty("mailbot.email.imap.host") == null && System.getenv("mailbot.email.imap.host") == null) {
            logger.error("mailbot.email.imap.host needs to be set");
            failed = true;
        }
        if(System.getProperty("mailbot.email.imap.port") == null && System.getenv("mailbot.email.imap.port") == null) {
            logger.error("mailbot.email.imap.port needs to be set");
            failed = true;
        }
        if(System.getProperty("mailbot.email.smtp.host") == null && System.getenv("mailbot.email.smtp.host") == null) {
            logger.error("mailbot.email.smtp.host needs to be set");
            failed = true;
        }
        if(System.getProperty("mailbot.email.smtp.port") == null && System.getenv("mailbot.email.smtp.port") == null) {
            logger.error("mailbot.email.smtp.port needs to be set");
            failed = true;
        }

        if (failed) {
            System.exit(1);
        }
        SpringApplication app = new SpringApplication("classpath:/spring/app/botApp.xml");
        app.setWebEnvironment(false);
        app.run(args);
        // ConfigurableApplicationContext applicationContext = app.run(args);
        // Thread.sleep(5*60*1000);
        // app.exit(applicationContext);
    }
}

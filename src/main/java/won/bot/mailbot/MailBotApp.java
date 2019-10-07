package won.bot.mailbot;

import org.springframework.boot.SpringApplication;
import won.bot.framework.bot.utils.BotUtils;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;

@SpringBootConfiguration
@PropertySource("classpath:application.properties")
@ImportResource("classpath:/spring/app/botApp.xml")
public class MailBotApp {
    public static void main(String[] args) {
        if (!BotUtils.isValidRunConfig()) {
            System.exit(1);
        }
        SpringApplication app = new SpringApplication(MailBotApp.class);
        app.setWebEnvironment(false);
        app.run(args);
        // ConfigurableApplicationContext applicationContext = app.run(args);
        // Thread.sleep(5*60*1000);
        // app.exit(applicationContext);
    }
}

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;

import javax.security.auth.login.LoginException;

public class Main {

    public static JDA jda;
    public static String prefix = "!";

    public static void main(String[] args) throws LoginException {
        String token = "OTI5MTUxMjY5MzY3NTc4NjI1.YdjJjQ.B-tvMt5SQc8z3j8ZZy40VPw_gts";
        jda = JDABuilder.createDefault(token).build();
        jda.getPresence().setStatus(OnlineStatus.ONLINE);
        jda.getPresence().setPresence(OnlineStatus.ONLINE, Activity.listening("your lovely voice!"));

        jda.addEventListener(new Commands());

        
    }
}
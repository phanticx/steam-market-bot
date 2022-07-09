import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.openqa.selenium.NoSuchWindowException;

import java.awt.*;
import java.util.ArrayList;

import static java.lang.Integer.parseInt;

public class Commands extends ListenerAdapter {
    public void onMessageReceived(MessageReceivedEvent event) {
        String[] args = event.getMessage().getContentRaw().split("\\s+");
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(Color.CYAN);

        // Basic bot information
        if (args[0].equalsIgnoreCase(Main.prefix + "info")) {
            embed.addField("Information on this bot", "I am a bot which scrapes the data of CS:GO skins from the steam marketplace. " +
                    "\n I am coded in Java. Check me out on [github](https://github.com/phanticx/steam-market-bot).", false);
            event.getChannel().sendMessageEmbeds(embed.build()).queue();
        }

        // Check marketplace listings for floats and prices of specified skin. Discord bot command syntax is !skin <results> <url> <options>
        if (args[0].equalsIgnoreCase(Main.prefix + "checkskin")) {
            if (args.length < 3 || parseInt(args[1]) > 100) {
                embed.addField("Syntax Error", "The correct syntax for !check is **!checkskin <result count> <url> <options>**. Result count must be equal to or less than 100.", false);
                event.getChannel().sendMessageEmbeds(embed.build()).queue();
            } else {
                String options = "";
                for (int i = 3; i < args.length; i++) {
                    options = options + args[i] + "+";
                }
                options.substring(options.length() - 1);
                Script script = new Script();
                boolean error = false;
                ArrayList<ItemData> items;
                try {
                    script.check(args[2], options);
                } catch (NoSuchWindowException e) {
                    error = true;
                }

                if(error == false) {
                    items = script.run(args[2], options);
                    if (items == null) {
                        embed.addField("Syntax Error","Unfortunately, your search failed, likely due to a syntax error. Please try again.",false);
                        event.getChannel().sendMessageEmbeds(embed.build()).queue();
                    } else {
                        embed.setTitle("Listings for " + items.get(0).getName() + " " + items.get(0).getItemWear());
                        String descr = "";
                        for (int i = 0; i < parseInt(args[1]); i++) {
                            descr = descr + items.get(i).getItemFloat() + ", $" + items.get(i).getPrice() + "\n";
                        }
                        embed.addField("Floats and Prices", descr, false);
                        event.getChannel().sendMessageEmbeds(embed.build()).queue();
                    }
                } else {
                    embed.addField("Search Error","Unfortunately, your search failed, likely due to an error. Please try again.",false);
                    event.getChannel().sendMessageEmbeds(embed.build()).queue();
                }
            }
        }

        if (args[0].equalsIgnoreCase(Main.prefix + "help")) {
            if (args.length > 3) {
                embed.addField("Syntax Error", "The correct syntax for !help is **!help or !help <command>**.", false);
                event.getChannel().sendMessageEmbeds(embed.build()).queue();
            } else if (args.length == 1) {
                embed.addField("Help Menu","I am a bot which scrapes the data of CS:GO skins from the steam marketplace." +
                        "\n" +
                        "\n __Commands available:__" +
                        "\n **!info**-  Basic information on bot" +
                        "\n **!checkskin**- Check steam marketplace listings for data on a specified skin",false);
                event.getChannel().sendMessageEmbeds(embed.build()).queue();
            } else if (args.length == 2) {
                switch(args[1]) {
                    case "info":
                        embed.addField("!info","This command gives basic information on this bot.",false);
                        event.getChannel().sendMessageEmbeds(embed.build()).queue();
                        break;

                    case "checkskin":
                        embed.addField("!checkskin","This command checks the steam marketplace listings for data on a specified skin. The correct syntax for !check is **!checkskin <result count> <url> <options>**. Result count must be equal to or less than 100." +
                                "\n" +
                                "\n __Options avaliable:__" +
                                "\n **sort_float**- Sort by lowest float (on first 100 listings)" +
                                "\n **sort_price**- Sort by lowest price (on first 100 listings)",false);
                        event.getChannel().sendMessageEmbeds(embed.build()).queue();
                        break;

                    default:
                        embed.addField("Error","" + args[1] + " is not an existing command.",false);
                        event.getChannel().sendMessageEmbeds(embed.build()).queue();
                        break;
                }
            }
        }



    }
}

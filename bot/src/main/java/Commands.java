import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.openqa.selenium.NoSuchWindowException;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.ArrayList;

import static java.lang.Integer.parseInt;
import java.math.BigDecimal;

public class Commands extends ListenerAdapter {
    public void onMessageReceived(MessageReceivedEvent event) {
        String[] args = event.getMessage().getContentRaw().split("\\s+");
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(Color.CYAN);

        // !info command- Basic bot information
        if (args[0].equalsIgnoreCase(Main.prefix + "info")) {
            long startTime = System.nanoTime();
            long elapsedTime = System.nanoTime() - startTime;
            embed.setFooter("Elapsed Time: " + elapsedTime/1000000 + " ms");
            embed.addField("Information on this bot", "I am a bot which scrapes the data of CS:GO skins from the steam marketplace. " +
                    "\n I am coded in Java. Check me out on [github](https://github.com/phanticx/steam-market-bot).", false);
            event.getChannel().sendMessageEmbeds(embed.build()).queue();
        }

        // !checkskin command- Check marketplace listings for floats and prices of specified skin. Discord bot command syntax is !checkskin <results> <url> <options>
        if (args[0].equalsIgnoreCase(Main.prefix + "checkskin")) {
            long startTime = System.nanoTime();
            if (args.length < 3) {
                long elapsedTime = System.nanoTime() - startTime;
                embed.setFooter("Elapsed Time: " + elapsedTime/1000000 + " ms");
                embed.addField("Syntax Error", "The correct syntax for !check is **!checkskin <result count> <url> <options>**. For more help, use the command !help checkskin.", false);
                event.getChannel().sendMessageEmbeds(embed.build()).queue();
            } else {
                String options = "";
                for (int i = 3; i < args.length; i++) {
                    options = options + args[i] + "+";
                }
                Script script = new Script();
                boolean error = false;
                ArrayList<ItemData> items;
                try {
                    script.check(args[2], options);
                } catch (NoSuchWindowException e) {
                    error = true;
                }

                if(!error) {
                    items = script.run(args[2], options);
                    if (items == null || items.size() == 0) {
                        long elapsedTime = System.nanoTime() - startTime;
                        embed.setFooter("Elapsed Time: " + elapsedTime/1000000 + " ms");
                        embed.addField("Syntax Error","Unfortunately, your search failed, likely due to a syntax error. Please try again.",false);
                        event.getChannel().sendMessageEmbeds(embed.build()).queue();
                    } else {
                        embed.setTitle("Listings for " + items.get(0).getName() + " " + items.get(0).getItemWear());
                        String descr = "**Floats and Prices** \n \n";
                        int answerAmount = items.size();
                        DecimalFormat df = new DecimalFormat("#");
                        df.setMaximumFractionDigits(14);
                        if (parseInt(args[1]) < answerAmount) {
                           answerAmount = parseInt(args[1]);
                        }
                        for (int i = 0; i < answerAmount; i++) {
                            descr = descr + "0" + df.format(items.get(i).getItemFloat()) + ",\t $" + items.get(i).getPrice() + "\n";
                        }
                        long elapsedTime = System.nanoTime() - startTime;
                        embed.setFooter("Elapsed Time: " + elapsedTime/1000000 + " ms");
                        embed.setDescription(descr);
                        event.getChannel().sendMessageEmbeds(embed.build()).queue();
                    }
                } else {
                    long elapsedTime = System.nanoTime() - startTime;
                    embed.setFooter("Elapsed Time: " + elapsedTime/1000000 + " ms");
                    embed.addField("Search Error","Unfortunately, your search failed, likely due to an error. Please try again.",false);
                    event.getChannel().sendMessageEmbeds(embed.build()).queue();
                }
            }
        }

        // !help command- Help menu
        if (args[0].equalsIgnoreCase(Main.prefix + "help")) {
            long startTime = System.nanoTime();
            if (args.length > 3) {
                long elapsedTime = System.nanoTime() - startTime;
                embed.setFooter("Elapsed Time: " + elapsedTime/1000000 + " ms");
                embed.addField("Syntax Error", "The correct syntax for !help is **!help or !help <command>**.", false);
                event.getChannel().sendMessageEmbeds(embed.build()).queue();
            } else if (args.length == 1) {
                long elapsedTime = System.nanoTime() - startTime;
                embed.setFooter("Elapsed Time: " + elapsedTime/1000000 + " ms");
                embed.addField("Help Menu","I am a bot which scrapes the data of CS:GO skins from the steam marketplace." +
                        "\n" +
                        "\n __Commands available:__" +
                        "\n **!info**-  Basic information on bot" +
                        "\n **!checkskin**- Check steam marketplace listings for data on a specified skin",false);
                event.getChannel().sendMessageEmbeds(embed.build()).queue();
            } else if (args.length == 2) {
                long elapsedTime;
                switch(args[1]) {
                    case "info":
                        elapsedTime = System.nanoTime() - startTime;
                        embed.setFooter("Elapsed Time: " + elapsedTime/1000000 + " ms");
                        embed.addField("!info","This command gives basic information on this bot.",false);
                        event.getChannel().sendMessageEmbeds(embed.build()).queue();
                        break;

                    case "checkskin":
                        elapsedTime = System.nanoTime() - startTime;
                        embed.setFooter("Elapsed Time: " + elapsedTime/1000000 + " ms");
                        embed.addField("!checkskin","This command checks the steam marketplace listings for data on a specified skin. The correct syntax for !check is **!checkskin <result count> <url> <options>**. Separate options with a space. Result count must be equal to or less than 100." +
                                "\n" +
                                "\n __Options avaliable:__" +
                                "\n **sort_float_asc**- Sort by lowest float (on first 100 listings)" +
                                "\n **sort_float_dsc**- Sort by highest float (on first 100 listings)" +
                                "\n **sort_price_asc**- Sort by lowest price (on first 100 listings)" +
                                "\n **sort_price_dsc**- Sort by highest price (on first 100 listings)" +
                                "\n **filter_float**- Filter by float, greater or less than a specified value with the operators \">\" or \"<\" (on first 100 listings)" +
                                "\n **filter_price**- Filter by price, greater or less than a specified value with the operators \">\" or \"<\" (on first 100 listings)",false);
                        event.getChannel().sendMessageEmbeds(embed.build()).queue();
                        break;

                    default:
                        elapsedTime = System.nanoTime() - startTime;
                        embed.setFooter("Elapsed Time: " + elapsedTime/1000000 + " ms");
                        embed.addField("Error","!" + args[1] + " is not an existing command.",false);
                        event.getChannel().sendMessageEmbeds(embed.build()).queue();
                        break;
                }
            }
        }



    }
}

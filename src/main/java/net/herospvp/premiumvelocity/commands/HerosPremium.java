package net.herospvp.premiumvelocity.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import net.herospvp.morevelocity.Tasks;
import net.herospvp.premiumvelocity.Main;
import net.herospvp.premiumvelocity.databases.Hikari;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import javax.print.DocFlavor;

public class HerosPremium implements SimpleCommand {

    private CommandSource source;

    @Override
    public void execute(final Invocation invocation) {
        source = invocation.source();
        // Get the arguments after the command alias
        String[] args = invocation.arguments();

        if (args.length <= 1) {
            helpMessage();
            return;
        }

        switch (args[0].toLowerCase()) {
            case "info": {
                if (args.length == 2) {
                    String name = args[1];
                    new Tasks().runAsyncSingleThreaded(() -> {
                        source.sendMessage(Component.text(name + " nel database risulta essere: "
                                + (Main.getHikari().isPremium(name) ? "premium" : "cracked"))
                                .color(NamedTextColor.RED));
                    });
                } else {
                    helpMessage();
                }
                break;
            }
            case "set": {
                if (args.length == 3) {
                    String status = args[1].toLowerCase(), name = args[2];

                    new Tasks().runAsyncSingleThreaded(() -> {
                        boolean isPremium = Main.getHikari().isPremium(name);

                        if (!status.equals("premium") && !status.equals("cracked")) {
                            helpMessage();
                            return;
                        }

                        if (isPremium) {
                            if (status.equals("premium")) {
                                source.sendMessage(Component.text(name + " e' gia' premium!").color(NamedTextColor.RED));
                            } else {
                                Main.getHikari().setConnection(name, false);
                                source.sendMessage(Component.text(name + " e' stato impostato cracked!").color(NamedTextColor.GREEN));
                            }
                        } else {
                            if (status.equals("cracked")) {
                                source.sendMessage(Component.text(name + " e' gia' cracked!").color(NamedTextColor.RED));
                            } else {
                                Main.getHikari().setConnection(name, true);
                                source.sendMessage(Component.text(name + " e' stato impostato premium!").color(NamedTextColor.GREEN));
                            }
                        }
                    });
                } else {
                    helpMessage();
                }
                break;
            }
            default: {
                helpMessage();
                break;
            }
        }
    }

    private void helpMessage() {
        source.sendMessage(Component.text("\n****** HerosPremium *****\nShow infos  »  /hp info <player>\nSet player cracked/premium  »  /hp set <cracked/premium> <player>\n").color(NamedTextColor.GOLD));
    }

}

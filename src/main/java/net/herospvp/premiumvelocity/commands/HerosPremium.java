package net.herospvp.premiumvelocity.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import net.herospvp.premiumvelocity.Main;
import net.herospvp.premiumvelocity.databases.Storage;
import net.herospvp.premiumvelocity.threadbakery.Oven;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class HerosPremium implements SimpleCommand {

    private CommandSource source;

    @Override
    public void execute(final Invocation invocation) {
        source = invocation.source();
        String[] args = invocation.arguments();

        if (!source.hasPermission("heros.premium")) {
            source.sendMessage(Component.text("Non puoi! (heros.premium)").color(NamedTextColor.RED));
            return;
        }

        if (args.length == 0) {
            helpMessage();
            return;
        }

        switch (args[0].toLowerCase()) {
            case "stats": {
                source.sendMessage(Component.text("Ci sono in totale: "
                        + Storage.getDatabaseData().size() + " utenti protetti!").color(NamedTextColor.GREEN));
                break;
            }
            case "info": {
                if (args.length == 2) {
                    String name = args[1];

                    String is = Storage.getDatabaseData().containsKey(name) ? Storage.getDatabaseData().get(name)
                            ? "premium" : "cracked" : "cracked";

                    source.sendMessage(Component.text(name + " nel database risulta essere: " + is)
                            .color(NamedTextColor.GOLD));
                } else {
                    helpMessage();
                }
                break;
            }
            case "set": {
                if (args.length == 3) {
                    String status = args[1].toLowerCase(), name = args[2];

                    Oven.runSingleThreaded(() -> {
                        boolean isPremium = Main.getHikari().isPremium(name);

                        if (!status.equals("premium") && !status.equals("cracked")) {
                            helpMessage();
                            return;
                        }

                        if (isPremium) {
                            if (status.equals("premium")) {
                                source.sendMessage(Component.text(name + " e' gia' premium!")
                                        .color(NamedTextColor.RED));
                            } else {
                                Main.getHikari().setConnection(name, false);
                                source.sendMessage(Component.text(name + " e' stato impostato cracked!")
                                        .color(NamedTextColor.GREEN));
                            }
                        } else {
                            if (status.equals("cracked")) {
                                source.sendMessage(Component.text(name + " e' gia' cracked!")
                                        .color(NamedTextColor.RED));
                            } else {
                                Main.getHikari().setConnection(name, true);
                                source.sendMessage(Component.text(name + " e' stato impostato premium!")
                                        .color(NamedTextColor.GREEN));
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
        source.sendMessage(Component.text("\n****** HerosPremium *****\nShow statistics  »  /hp stats\nShow info  »  /hp info <player>\nSet player cracked/premium  »  /hp set <cracked/premium> <player>\n").color(NamedTextColor.GOLD));
    }

}

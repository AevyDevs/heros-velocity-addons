package net.herospvp.premiumvelocity.threadbakery;

import net.herospvp.premiumvelocity.Main;

public class Oven {

    public static void runSingleThreaded(Runnable runnable) {
        Thread thread = new Thread(() -> {
            try {
                runnable.run();
            } catch (Exception e) {
                e.printStackTrace();
                Main.getLogger().error("AN ERROR OCCURRED WHILE EXECUTING 'runnable' on runSingleThreaded()");
            }
        });
        thread.start();
    }

    public static void runDoubleTaskDoubleThreaded(Runnable runnable, Runnable runnable1) {
        Thread thread = new Thread(() -> {
            try {
                runnable.run();
            } catch (Exception e) {
                e.printStackTrace();
                Main.getLogger().error("AN ERROR OCCURRED WHILE EXECUTING 'runnable' on runDoubleTaskDoubleThreaded()");
            }
        }), thread1 = new Thread(() -> {
            try {
                runnable1.run();
            } catch (Exception e) {
                e.printStackTrace();
                Main.getLogger().error("AN ERROR OCCURRED WHILE EXECUTING 'runnable1' on runDoubleTaskDoubleThreaded()");
            }
        });
        thread.start(); thread1.start();
    }

    public static void runDoubleTaskSingleThreaded(Runnable runnable, Runnable runnable1) {
        Thread thread = new Thread(() -> {
            try {
                runnable.run();
                runnable1.run();
            } catch (Exception e) {
                e.printStackTrace();
                Main.getLogger().error("AN ERROR OCCURRED WHILE EXECUTING 'runnable' or 'runnable1' on runDoubleTaskSingleThreaded()");
            }
        });
        thread.start();
    }

}

package cn.yapeteam.yolbi.command.impl;

import cn.yapeteam.loader.logger.Logger;
import cn.yapeteam.loader.oauth.login.MicrosoftLogin;
import cn.yapeteam.ymixin.utils.Mapper;
import cn.yapeteam.yolbi.command.AbstractCommand;
import net.minecraft.client.Minecraft;
import net.minecraft.client.User;

import java.lang.reflect.Field;
import java.util.Optional;

public class CommandLogin extends AbstractCommand {

    public CommandLogin() {
        super("login");
    }

    private static final Field user;

    static {
        try {
            user = Minecraft.class.getDeclaredField(Mapper.map("net/minecraft/client/Minecraft", "user", null, Mapper.Type.Field));
            user.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
        MicrosoftLogin.setUpdateStatusConsumer(AbstractCommand::printMessage);
    }

    private Thread loginThread = null;

    @Override
    public void process(String[] args) {
        if (loginThread == null) {
            loginThread = new Thread(() -> {
                try {
                    if (new MicrosoftLogin().login().login((name, uuid, access_token, refresh_token) -> {
                        try {
                            printMessage(name);
                            printMessage(uuid.toString());
                            printMessage(access_token);
                            user.set(Minecraft.getInstance(), new User(name, uuid.toString(), access_token, Optional.empty(), Optional.empty(), User.Type.MSA));
                        } catch (IllegalAccessException e) {
                            Logger.exception(e);
                        }
                    })) {
                        printMessage("Login Successfully");
                    }
                } catch (Exception e) {
                    Logger.exception(e);
                }
                loginThread = null;
            });
            loginThread.start();
        }
    }
}

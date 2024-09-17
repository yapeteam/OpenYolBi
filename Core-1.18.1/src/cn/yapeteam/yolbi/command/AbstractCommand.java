package cn.yapeteam.yolbi.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.network.chat.TextComponent;


@Getter
@AllArgsConstructor
public abstract class AbstractCommand {
    private final String key;

    public abstract void process(String[] args);

    public static void printMessage(String msg) {
        Minecraft.getInstance().gui.getChat().addMessage(new TextComponent(msg));
    }
}

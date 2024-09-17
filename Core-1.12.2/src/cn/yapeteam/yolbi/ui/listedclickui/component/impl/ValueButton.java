package cn.yapeteam.yolbi.ui.listedclickui.component.impl;

import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.font.AbstractFontRenderer;
import cn.yapeteam.yolbi.module.values.Value;
import cn.yapeteam.yolbi.module.values.impl.BooleanValue;
import cn.yapeteam.yolbi.module.values.impl.ColorValue;
import cn.yapeteam.yolbi.module.values.impl.ModeValue;
import cn.yapeteam.yolbi.module.values.impl.NumberValue;
import cn.yapeteam.yolbi.ui.listedclickui.ImplScreen;
import cn.yapeteam.yolbi.ui.listedclickui.component.AbstractComponent;
import cn.yapeteam.yolbi.ui.listedclickui.component.Limitation;
import cn.yapeteam.yolbi.utils.render.GradientBlur;
import cn.yapeteam.yolbi.utils.render.RenderUtil;
import lombok.Getter;

import java.awt.*;
import java.util.Arrays;

/**
 * @author TIMER_err
 */
public class ValueButton extends AbstractComponent {
    @Getter
    private final Value<?> value;

    public ValueButton(AbstractComponent parent, Value<?> value) {
        super(parent);
        this.value = value;
    }

    private float sliderAnimeWidth = 0;

    private final GradientBlur blur = new GradientBlur(GradientBlur.Type.TB);

    @Override
    public void update() {
        if (!((ModuleButton) getParent()).isExtended() || !value.getVisibility().get()) sliderAnimeWidth = 0;
        super.update();
        blur.update(getX(), getY(), getWidth(), getHeight());
    }

    @SuppressWarnings("DuplicatedCode")
    @Override
    public void drawComponent(int mouseX, int mouseY, float partialTicks, Limitation limitation) {
        if (!(
                getX() + getWidth() < limitation.getX() ||
                        getX() > limitation.getX() + limitation.getWidth() ||
                        getY() + getHeight() < limitation.getY() ||
                        getY() > limitation.getY() + limitation.getHeight()
        )) {
            blur.render(getX(), getY(), getWidth(), getHeight(), partialTicks, 1);
            AbstractFontRenderer font = YolBi.instance.getFontManager().getPingFang12();
            AbstractFontRenderer icon = YolBi.instance.getFontManager().getFLUXICON14();
            int color = ImplScreen.getComponentColor((int) (getY() * 10));
            if (value instanceof BooleanValue) {
                BooleanValue booleanValue = (BooleanValue) value;
                font.drawString(value.getName(), getX() + 5, getY() + (getHeight() - font.getStringHeight()) / 2f, -1);
                int w = 8, h = 8;
                RenderUtil.drawRect2(getX() + getWidth() - 5 - w, getY() + (getHeight() - h) / 2f - 1, w, h, new Color(0, 0, 0, 0.3f).getRGB());
                if (booleanValue.getValue())
                    icon.drawString("j", getX() + getWidth() - 5 - w - 0.5f, getY() + (getHeight() - icon.getStringHeight()) / 2f - 1, color);
            } else if (value instanceof NumberValue<?>) {
                NumberValue<?> numberValue = (NumberValue<?>) value;
                font.drawString(numberValue.getName(), getX() + 5, getY() + 5, -1);
                font.drawString(String.format("%.2f", numberValue.getValue().floatValue()), getX() + getWidth() - font.getStringWidth(String.format("%.2f", numberValue.getValue().floatValue())) - 5, getY() + 5, -1);
                float w = (getWidth() - 10) * ((numberValue.getValue().floatValue() - numberValue.getMin().floatValue()) / (numberValue.getMax().floatValue() - numberValue.getMin().floatValue()));
                sliderAnimeWidth += (w - sliderAnimeWidth) / 10f;
                RenderUtil.drawRect2(getX() + 5, getY() + getHeight() - 5 - 1, getWidth() - 10, 1, ImplScreen.MainTheme[3].getRGB());
                RenderUtil.drawRect2(getX() + 5, getY() + getHeight() - 5 - 1, sliderAnimeWidth, 1, color);
                // RenderUtil.drawRect2(getX() + 5 + sliderAnimeWidth - 4, getY() + getHeight() - 5 - 1, 8, 1, ImplScreen.MainTheme[1].darker().getRGB());
                RenderUtil.circle(getX() + 5 + sliderAnimeWidth, getY() + getHeight() - 5 - 1 + 0.5f, 2.5f, color);

                if (isDragging()) {
                    if (mouseX >= getX() + 5 && mouseX <= getX() + 5 + getWidth() - 10) {
                        double val = (mouseX - (getX() + 5)) / (getWidth() - 10) * (numberValue.getMax().floatValue() - numberValue.getMin().floatValue()) + numberValue.getMin().floatValue();
                        numberValue.setValue(val - val % numberValue.getInc().doubleValue());
                    } else if (mouseX < getX() + 5)
                        numberValue.setValue(numberValue.getMin());
                    else if (mouseX > getWidth() - 10)
                        numberValue.setValue(numberValue.getMax());
                }
            } else if (value instanceof ModeValue<?>) {
                ModeValue<?> modeValue = (ModeValue<?>) value;
                RenderUtil.drawFastRoundedRect(getX() + 2, getY() + 2, getX() + getWidth() - 2, getY() + getHeight() - 2, 2, new Color(0, 0, 0, 0.3f).getRGB());
                String text = modeValue.getName() + " | " + modeValue.getValue();
                font.drawString(text, getX() + (getWidth() - font.getStringWidth(text)) / 2f, getY() + (getHeight() - font.getStringHeight()) / 2f - 2, -1);
                font.drawString("|", getX() + (getWidth() - font.getStringWidth("|")) / 2f, getY() + getHeight() / 2f + 2.5f, color);
                icon.drawString("h i", getX() + (getWidth() - icon.getStringWidth("h i")) / 2f, getY() + getHeight() / 2f + 1.5f, color);
            } else if (value instanceof ColorValue) {
                ColorValue colorValue = (ColorValue) value;
                font.drawString(colorValue.getName() + ":", getX() + (getWidth() - font.getStringWidth(colorValue.getName() + ":") - 2 - 5) / 2f, getY() + 3, color);
                RenderUtil.drawFastRoundedRect2(getX() + (getWidth() - font.getStringWidth(colorValue.getName() + ":") - 2 - 5) / 2f + font.getStringWidth(colorValue.getName() + ":") + 2, getY() + 2, 5, 5, 1, colorValue.getColor());
                colorValue.draw(getX() + (getWidth() - 54) / 2f, getY() + 9, 40, 40, mouseX, mouseY);
            }
        } else sliderAnimeWidth = 0;
        super.drawComponent(mouseX, mouseY, partialTicks, limitation);
    }

    public void setHeight() {
        if (value instanceof BooleanValue)
            setHeight(12);
        else if (value instanceof NumberValue)
            setHeight(22);
        else if (value instanceof ModeValue)
            setHeight(20);
        else if (value instanceof ColorValue)
            setHeight(52);
    }

    @Override
    public void mouseClicked(float mouseX, float mouseY, int mouseButton) {
        if (isHovering(getParent().getParent().getX(), getParent().getParent().getY() + ImplScreen.panelTopHeight, getParent().getParent().getWidth(), getParent().getParent().getHeight() - ImplScreen.panelTopHeight, mouseX, mouseY))
            if (isHovering(getX(), getY(), getWidth(), getHeight(), mouseX, mouseY)) {
                if (value instanceof BooleanValue) {
                    BooleanValue booleanValue = (BooleanValue) value;
                    booleanValue.setValue(!booleanValue.getValue());
                } else if (value instanceof NumberValue) {
                    if (isHovering(getX() + 2, getY() + getHeight() - 5 - 4, getWidth() - 4, 7, mouseX, mouseY))
                        setDragging(true);
                } else if (value instanceof ModeValue<?>) {
                    ModeValue<?> modeValue = (ModeValue<?>) value;
                    int index = Arrays.asList(modeValue.getModes()).indexOf(modeValue.getValue());
                    if (isHovering(getX(), getY(), getWidth() / 2f, getHeight(), mouseX, mouseY))
                        if (modeValue.getModes().length != 0)
                            modeValue.setMode(modeValue.getModes()[index > 0 ? index - 1 : modeValue.getModes().length - 1].toString());
                    if (isHovering(getX() + getWidth() / 2f, getY(), getWidth() / 2f, getHeight(), mouseX, mouseY))
                        if (modeValue.getModes().length != 0)
                            modeValue.setMode(modeValue.getModes()[index < modeValue.getModes().length - 1 ? index + 1 : 0].toString());
                }
            }
    }
}

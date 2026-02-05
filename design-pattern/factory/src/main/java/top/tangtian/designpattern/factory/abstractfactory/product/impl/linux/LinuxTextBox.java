package top.tangtian.designpattern.factory.abstractfactory.product.impl.linux;

import top.tangtian.designpattern.factory.abstractfactory.product.TextBox;

public class LinuxTextBox implements TextBox {
    private String text = "";

    @Override
    public void render() {
        System.out.println("  [Linux TextBox]");
        System.out.println("  +------------------------+");
        System.out.println("  | " + (text.isEmpty() ? "Enter text..." : text) +
                " ".repeat(Math.max(0, 21 - text.length())) + "|");
        System.out.println("  +------------------------+");
        System.out.println("  Style: Simple border, Monospace hint");
    }

    @Override
    public void setText(String text) {
        this.text = text;
        System.out.println("  Text set in Linux style: " + text);
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public String getStyle() {
        return "GTK/GNOME Theme";
    }
}

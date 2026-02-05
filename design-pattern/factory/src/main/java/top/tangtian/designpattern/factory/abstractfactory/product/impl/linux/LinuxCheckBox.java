package top.tangtian.designpattern.factory.abstractfactory.product.impl.linux;

import top.tangtian.designpattern.factory.abstractfactory.product.CheckBox;

public class LinuxCheckBox implements CheckBox {
    private boolean checked = false;

    @Override
    public void render() {
        System.out.println("  [Linux CheckBox]");
        String box = checked ? "[X]" : "[ ]";
        System.out.println("  " + box + " I agree to terms");
        System.out.println("  Style: Square, Simple X mark");
    }

    @Override
    public void setChecked(boolean checked) {
        this.checked = checked;
        System.out.println("  Linux CheckBox " + (checked ? "checked" : "unchecked"));
    }

    @Override
    public boolean isChecked() {
        return checked;
    }

    @Override
    public String getStyle() {
        return "GTK/GNOME Theme";
    }
}

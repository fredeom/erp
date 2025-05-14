package main.pages;


/**
 * MenuPermission is simple class to store permission name and reference to menu item.
 *
 * @author Alexander Smirnov
 * @version 2025-02-03
 */
public class MenuPermission
{
    public int id;
    public String name;
    public int menu_item_id;
    public MenuPermission() { }
    public String toString() {
        return name;
    }
    public String toStringFull() {
        return "[id=" + id + " name=" + name + " menu_item_id=" + menu_item_id + "]";
    }
}

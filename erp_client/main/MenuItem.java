package main;


/**
 * MenuItem is element of MainMenu which consist of menu item name, menu item pane class name and parent_id that is id of parent menu item
 *
 * @author Alexander Smirnov
 * @version 2025-01-07
 */
public class MenuItem {
    public int id;
    public int parent_id;
    public String name;
    public String pane_class;
    public int posit;
    public int active;

    public MenuItem() {}

    public String toString() {
        return name;
    }
    
    public String toStringWithDetails() {
        return "[" + id + " " + parent_id + " " + name + " " + pane_class + " " + posit + " " + active + "]";
    }
}

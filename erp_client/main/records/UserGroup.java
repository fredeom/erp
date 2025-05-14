package main.records;


/**
 * UserGroup содежрит информацию о группах пользователей.
 *
 * @author Alexander Smirnov
 * @version 2025-02-03
 */
public class UserGroup
{
    public int id;
    public String name;
    public int posit;
    public UserGroup() { }
    public String toString() {
        return name;
    }
}

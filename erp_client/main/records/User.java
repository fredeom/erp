package main.records;


/**
 * User содежрит информацию о пользователе.
 *
 * @author Alexander Smirnov
 * @version 2025-03-16
 */
public class User
{
    public int id;
    public String login;
    public String pass;
    public String name;
    public String phone;
    public int user_group_id;
    public User() { }
    public String toString() {
        return name;
    }
}

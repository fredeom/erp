package main.records;


/**
 * UserShort содежрит информацию о пользователе и его группе.
 *
 * @author Alexander Smirnov
 * @version 2025-02-03
 */
public class UserShort
{
    public int id;
    public String name;
    public int user_group_id;
    public String last_login_at;
    public UserShort() { }
    public String toString() {
        return name;
    }
}

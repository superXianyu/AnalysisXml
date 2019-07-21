package work.soft.vo;

import java.util.List;
import java.util.Map;

public class User {
    private  int id;
    private String name;
    private String passwd;
    private List list;
    private Map map;


    public List getList() {
        return list;
    }

    public Map getMap() {
        return map;
    }

    public void setList(List list) {
        this.list = list;
    }

    public void setMap(Map map) {
        this.map = map;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", passwd='" + passwd + '\'' +
                ", list=" + list +
                ", map=" + map +
                '}';
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPasswd() {
        return passwd;
    }


    public void init(){
        System.out.println("init方法执行");
    }
}

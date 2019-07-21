import org.dom4j.DocumentException;
import work.soft.util.BeanMaker;
import work.soft.vo.Member;

import java.lang.reflect.InvocationTargetException;

public class Main {

    public static void main(String[] args) throws InstantiationException {

        BeanMaker beanMaker = new BeanMaker("UserBean.xml");
        String sb = String.class.toString();
        sb = sb.substring(6,sb.length());
        //System.out.println(sb);
        try {
//            User user = (User) beanMaker.getBean("user", User.class);
            //            //System.out.println(user);
            Member member =(Member) beanMaker.getBean("member",Member.class);
            System.out.println(member);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}

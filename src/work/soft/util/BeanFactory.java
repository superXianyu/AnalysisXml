package work.soft.util;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BeanFactory<T> {
//    private  Files [] files;
    String path ;
    T t;
    public BeanFactory(String path) throws DocumentException {
        this.path= path;
    }
    /*
    * 跟传入的Class对象暴力获取其属性
     * 通过反射将其实例化，且实例化后通过set方法为其赋值
    * */
    private void analysisClass(Class<?> c,Map map) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {
        Field [] fields = c.getDeclaredFields();
        t = (T)c.newInstance();
        if(fields!=null&&fields.length>0){
            for(Field member:fields){
                Method method = c.getDeclaredMethod("set"+toUpperCaseFirstOne(member.getName()),member.getType());
                method.setAccessible(true);
                //System.out.println(map.get(member.getName())+"");
                assignment(method,member,map.get(member.getName())+"");
//                method.invoke(t,"s");
                //System.out.println(t.toString());
            }
        }
    }

    /*
    * 从xml中以根节点遍历
    * 读取bean中的member作为属性
    * 其中member的name属性名 value为属性值
    * 并将其存放在map中  返回map
    * */
    public Map getMapByName(Element root,String domId) throws DocumentException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        if(domId.equals(root.attribute("id").getData().toString())){
            Map<String,String> memberMap = new HashMap<String,String>();
            List<Element> memberList = root.elements("member");
            Attribute isInit = root.attribute("init");
            if(isInit!=null){
                invokeInit(isInit.getData().toString(),t.getClass());
            }
            if(memberList!=null&&memberList.size()>0){
                for(Element member:memberList){
                    String name = (String) member.attribute("name").getData();
//                        //System.out.println(name);
                        String value = (String) member.attribute("value").getData();
//                        //System.out.println(value);
                        memberMap.put(name,value);
                }
            }
            memberMap.put("isGetBean","1");
            return memberMap;
        }else{
            return null;
        }

//        List<Element> beansList = root.elements("bean");
//        if(beansList.size()>0&&beansList!=null){
//            for(Element element:beansList){
//                Attribute id = element.attribute("id");
//                Attribute isInit = element.attribute("init");
////                //System.out.println(id.getData().toString());
//                if(isInit!=null){
//                    invokeInit(isInit.getData().toString(),t.getClass());
//                }
//                if(id==null||!domId.equals(id.getData().toString())){
//                    continue;
//                }else {
//                    List<Element> memberList = element.elements("member");
//                    for(Element member:memberList){
//                        String name = (String) member.attribute("name").getData();
////                        //System.out.println(name);
//                        String value = (String) member.attribute("value").getData();
////                        //System.out.println(value);
//                        memberMap.put(name,value);
//                    }
//                }
//            }
//        }

    }

//    public void setPath(String path,Files files){
//        files.setPath(path);
//    }

    public Object getBean(String domId,Class c) throws IllegalAccessException, InstantiationException, DocumentException, NoSuchMethodException, InvocationTargetException {
        t = (T) c.newInstance();
//        analysisClass(c,);
//            Map memberMap = getMapByName("src/"+path,domId);
//            analysisClass(c,memberMap);
        return  t;
    }


    private Map analysis(String path,String tClass) throws DocumentException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        SAXReader saxReader = new SAXReader();
        Document document = saxReader.read(new File(path));
        Element root = document.getRootElement();
        Boolean isByType = true;
        Map<String,String> memberMap = new HashMap<String,String>();
        List<Element> beansList = root.elements("bean");
        if(beansList!=null&&beansList.size()>0){
            for(Element element:beansList){
                Attribute isByName = element.attribute("autowire");
                if(isByName!=null&&"byname".equals(isByName.getData().toString())){
                    String domid = element.attribute("id").getData().toString();
                    memberMap = getMapByName(element,domid);
                    if(memberMap!=null){
                        break;
                    }
                }else{

                }
            }
        }

        return null;
    }



    private Boolean assignment(Method method,Field field,String arg) throws InvocationTargetException, IllegalAccessException {
        String type = field.getType().toString();

        if("class java.lang.String".equals(type)){
            method.invoke(t,arg);
            return true;
        }

        if("int".equals(type)||"class java.lang.Integer".equals(type)){
            method.invoke(t,Integer.parseInt(arg));
            return true;
        }

        if("double".equals(type)||"class java.lang.Double".equals(type)){
            method.invoke(t,Double.parseDouble(arg));
            return true;
        }

        if("float".equals(type)||"class java.lang.Float".equals(type)){
            method.invoke(t,Float.parseFloat(arg));
            return true;
        }

        if("boolean".equals(type)||"class java.lang.Boolean".equals(type)){
            method.invoke(t,Boolean.parseBoolean(arg));
            return true;
        }

        method.invoke(t,null);
        return false;
    }
    /*
    * 首字母大写
    * */
    private String toUpperCaseFirstOne(String str){
        if(str!=null&&str.length()>0){
            str = str.substring(0,1).toUpperCase()+str.substring(1,str.length());
        }
        return str;
    }

/*
*
* */
    private void invokeInit(String methodName,Class c) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = c.getMethod(methodName);
        method.invoke(t);
    }

/*
* 按照类型查找
* 然后将值存储在map中
* */
    private Map getMapByType(Element root,Field f,String tClass) throws DocumentException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        String fieldType = f.getType().toString();

        if(tClass.equals(fieldType)){

        }else{
            return null;
        }

        //        SAXReader saxReader = new SAXReader();
//        Document document = saxReader.read(new File(path));
//        Element root = document.getRootElement();
//        Map<String,String> memberMap = new HashMap<String,String>();
//        List<Element> beansList = root.elements("bean");
//        if(beansList.size()>0&&beansList!=null){
//            for(Element element:beansList){
//                Attribute type = element.attribute("src");
//                Attribute isInit = element.attribute("init");
////                //System.out.println(id.getData().toString());
//                if(isInit!=null){
//                    invokeInit(isInit.getData().toString(),t.getClass());
//                }
//                if(type==null||!tClass.equals(type.getData().toString())){
//                    continue;
//                }else {
//                    List<Element> memberList = element.elements("member");
//                    for(Element member:memberList){
//                        String name = (String) member.attribute("name").getData();
////                        //System.out.println(name);
//                        String value = (String) member.attribute("value").getData();
////                        //System.out.println(value);
//                        memberMap.put(name,value);
//                    }
//                }
//            }
//        }
//        return memberMap;
        return null;
    }


}

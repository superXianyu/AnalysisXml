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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BeanMaker {
    private String path;
    private  Element root;
    public BeanMaker(String path){
        this.path = "src/"+path;
    }

    public Object getBean(String domId,Class clazz) throws IllegalAccessException, InstantiationException, NoSuchMethodException, NoSuchFieldException, DocumentException, InvocationTargetException {
        Object obj = analysisDom(clazz,domId);
        return obj;
    }

    /**
     *
     * @param clazz   要实例化的bean的Class
     * @param id
     *
     *
     * 用于分析xml  并将xml中的值赋给bean
     *
     *
     */
    private Object analysisDom(Class<?> clazz,String id) throws DocumentException, InvocationTargetException, NoSuchMethodException, NoSuchFieldException, InstantiationException, IllegalAccessException {
        SAXReader saxReader = new SAXReader();
        Document document = saxReader.read(new File(path));
        this.root = document.getRootElement();
        Object obj = null;
        Attribute autowire = root.attribute("autowire");
        Boolean isByName =("byName".equals(autowire.getData().toString()));
        List<Element> beanList = root.elements("bean");
        if(beanList!=null&&beanList.size()>0){
            obj = changeDomToBean(beanList,clazz,id,isByName,false);
        }
        return obj;
    }


    /**
     *
     * @param beanList   bean节点的list集合
     * @param clazz
     * @param id
     * @param isByName  是否是byNmae方式
     * @param isRef     是否存在ref属性
     *
     *   根据bean节点集合  遍历集合   调用beanInvoke方法返回Object对象
     */
    private Object changeDomToBean(List<Element> beanList,Class<?> clazz,String id,Boolean isByName,Boolean isRef) throws IllegalAccessException, InvocationTargetException, InstantiationException, DocumentException, NoSuchMethodException, NoSuchFieldException {
        Object obj = null;
        for(Element bean:beanList){
            obj = beanInvoke(bean,clazz,id,isByName,isRef);
            if(obj!=null){
                break;
            }
        }

        return obj;
    }



    /**
     * @param bean   bean节点
     * @param clazz   传入的Class类点的对象，既要实例化的bean
     * @param id        根据传入的id来实例化bean 实际上该id并不只可以是id  还会是src值，既可以根据类型判断赋值
     * @param isByName   boolean型判断是否是byName方式
     * @param isMember   boolean  判断是该bean是否某个bean的属性
     *
     *    分析某个bean节点   并在分析后  实例化bean
     */
    private Object beanInvoke(Element bean,Class<?> clazz,String id,Boolean isByName,Boolean isMember) throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException, NoSuchFieldException, DocumentException {
        Object obj = null;
        Attribute domId =null;
        //System.out.println(domId);
        Attribute autowire = bean.attribute("autowire");
        //System.out.println("isByname"+isByName);
        //在本bean节点查看用户是否设置byName或者byType属性，若设置则覆盖其isByName值
        if(autowire!=null){
            isByName =("byName".equals(autowire.getData().toString()));
        }
        ///isMember用于判断该bean是某个bean下的属性若不是则直接根据id赋值
        if(isMember){
            if(isByName){
                domId = bean.attribute("id");
            }else{
                domId = bean.attribute("src");
            }
        }else{
            domId = bean.attribute("id");
        }


        /*在传入的id与当前bean标签中的id相等时执行*/
        if(id.equals(domId.getData().toString())){
            Attribute isinit = bean.attribute("init");
            obj = clazz.newInstance();
            if(isinit!=null){
                Method initMethod = clazz.getDeclaredMethod(isinit.getData().toString());
                initMethod.setAccessible(true);
                initMethod.invoke(obj);
            }
            List <Element> memberList = bean.elements("member");
            if(memberList!=null&&memberList.size()>0){
                for(Element member:memberList){
                    Attribute memberType = member.attribute("type");
                    Attribute isRef = member.attribute("ref");
//                    //System.out.println(member.attribute("name").getData().toString()+memberType+"aaa"+isRef);
                    /*当成员既不是自定义的类的对象 且不是map或者list时执行*/
                    if(memberType==null&&isRef==null){
                        String key = member.attribute("name").getData().toString();
                        String val = member.attribute("value").getData().toString();
//                        memberMap.put(key,val);
                        /*还差将普通数据类型的成员实例化*/
//                        //System.out.println(key+"      " +val);
                        Field field = clazz.getDeclaredField(key);
                        String methodName = "set"+toUpperCaseFirstOne(key);
                        Method method = clazz.getDeclaredMethod(methodName,field.getType());
                        method.setAccessible(true);
                        assignment(method,field,val,obj);
                        //System.out.println("赋值结束");
                    }else{

                        /*当成员是map或list时*/
                        if(memberType!=null){
                            //System.out.println("sss"+memberType.getData().toString());
                            if("list".equals(memberType.getData().toString())){
                                //System.out.println("进入list");
                                /*
                                * 当其类型为list遍历member下的<list></list>标签
                                * 获取标签内容存放于list中
                                * */
                                List<Element> lists = member.elements("list");
                                List<String> listValue = new ArrayList<String>();
                                for(Element list:lists){
                                    listValue.add(list.getText());
                                }
                                //反射对相对应field其set方法对该属性进行赋值
                                String methodName ="set"+toUpperCaseFirstOne(member.attribute("name").getData().toString());
                                Method method = clazz.getDeclaredMethod(methodName,List.class);
                                method.setAccessible(true);
                                method.invoke(obj,listValue);
                            }
                            if("map".equals(memberType.getData().toString())){
                                List<Element> lists = member.elements("map");
                                Map<String,String> mapValue = new HashMap<>();
                                /*
                                * 当其类型为map时
                                * 遍历<map></map>标签
                                * 获取标签中的key属性的值作为key   其val属性作为value  存放于mapValue中
                                * */
                                for(Element list:lists){
                                    String key = list.attribute("key").getData().toString();
                                    String val = list.attribute("val").getData().toString();
                                    mapValue.put(key,val);
                                }

                                //调用其set方法为该属性进行赋值
                                String methodName ="set"+toUpperCaseFirstOne(member.attribute("name").getData().toString());
                                Method method = clazz.getDeclaredMethod(methodName,Map.class);
                                method.setAccessible(true);
                                method.invoke(obj,mapValue);
                            }
                        }
                    }
                    /*当其实一个自定义类的对象*/
                    if(isRef!=null){
                        Field field = clazz.getDeclaredField(member.attribute("name").getData().toString());
                        if(isByName){
                           Object memberObj =  changeDomToBean(root.elements("bean"),field.getType(),isRef.getData().toString(),true,true);
                            String methodName = "set"+toUpperCaseFirstOne(field.getName());
                            Method method = clazz.getDeclaredMethod(methodName,field.getType());
                            method.setAccessible(true);
                            method.invoke(obj,memberObj);
                        }else{
                            /*按照类型赋值*/
                            String type = field.getType().toString();
                            type = type.substring(6,type.length());//  根据field的属性截取字符串获得去除了Class 之后的字符串
                            Object memberObj =  changeDomToBean(root.elements("bean"),field.getType(),type,false,true);
                            String methodName = "set"+toUpperCaseFirstOne(field.getName());
                            Method method = clazz.getDeclaredMethod(methodName,field.getType());
                            method.setAccessible(true);
                            method.invoke(obj,memberObj);
                        }
                    }
                }
            }

        }
        return obj;
    }


    /**
     *
     * @param str
     * @return
     *
     * 首字母大写
     */
    private String toUpperCaseFirstOne(String str){
        if(str!=null&&str.length()>0){
            str = str.substring(0,1).toUpperCase()+str.substring(1,str.length());
        }
        return str;
    }

    /**
     *
     * @param method   bean中的set方法
     * @param field    bean中的成员
     * @param arg      调用set方法实例化参数
     *
     *     对于某些基本数据类型的成员通过反射调用其set方法为其赋值
     */
    private Boolean assignment(Method method, Field field, String arg,Object t) throws InvocationTargetException, IllegalAccessException {
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
}

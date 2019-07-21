package work.soft.util;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.util.Iterator;
import java.util.List;

public class Test {


    public static  void main(String args[]){

        Object o = new Integer(21);
        SAXReader saxReader = new SAXReader();
        Document document =null;
        try {
            document  = saxReader.read(new File("src/UserBean.xml"));
//            //System.out.println(document.getRootElement().attribute("id"));
            Element root = document.getRootElement();
            Iterator<Attribute> attrIterator = root.attributeIterator();
            Attribute attribute0 = root.attribute("id");
            Element element1 = root.element("bean");
//            List<Element> elements1 = element1.getChildren()
            List<Element> elements = root.elements("bean");
            for(Element element: elements){
                //System.out.println(element.attribute("src").getData());
//                Element chirld = element.element("member");
//                //System.out.println();
//                //System.out.println(chirld.attribute("name"));
                List<Element> chirldList = element.elements("member");
                //System.out.println(chirldList.size());
                for(Element element2: chirldList){
                    //System.out.println(element2.attribute("name").getData());
                    //System.out.println(element2.attribute("value").getData());
                }
            }
//            while (attrIterator.hasNext()){
//                Attribute attribute = attrIterator.next();
//                //System.out.println(attribute.getData());
//            }
        } catch (DocumentException e) {
            e.printStackTrace();
        }finally{
        }

    }
    public void myXMLTest() throws DocumentException {
        SAXReader saxReader = new SAXReader();
        Document document  = saxReader.read(new File("src/UserBean.xml"));

    }
}

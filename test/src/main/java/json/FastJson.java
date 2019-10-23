package json;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import pojo.Grade;
import pojo.Student;
import sun.org.mozilla.javascript.internal.json.JsonParser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FastJson {


    public static void main(String[] args) {

       /* Student student1 = new Student(101,"张一",21);
        Student student2 = new Student(102,"张二",22);
        Student student3 = new Student(103,"张三",23);
        String[] arr = {"aa","bb","cc"};
        String json1 = JSON.toJSONString(student1);
        String json2 = JSON.toJSONString(student2);
        String json3 = JSON.toJSONString(student3);
        System.out.println(json1+json2+json3);


        List<Student> studentList = new ArrayList<Student>();
        studentList.add(student1);
        studentList.add(student2);
        studentList.add(student3);

        Grade grade = new Grade(101,studentList);

        String jsonGrade = JSON.toJSONString(grade);
        System.out.println(jsonGrade);

        Object object = JSON.toJSON(grade);
        System.out.println(object);

        Grade grade1 = JSON.parseObject(jsonGrade,Grade.class);
        System.out.println(grade1);

        Object objectArr = JSON.toJSON(arr);
        String str = "\"key\""+":"+objectArr;
        Object objStr = JSON.toJSON(str);
        System.out.println(objectArr);
        System.out.println(objStr);
        List<String> list = new ArrayList<String>();
        list.add("1");
        list.add("5");
        list.add("3");
        list.add("4");
        Comparator<String> comp = Collections.reverseOrder();
        Collections.sort(list,comp);
        System.out.println(list);
        String aa = "{aaa:1,bbbb:2}";
        Object o = JSON.toJSON(aa);
        System.out.println(o);
*/


        JSONObject jsonObject = new JSONObject();
        jsonObject.put("张三","20");
        jsonObject.put("李四","21");
        jsonObject.put("王五","22");
        jsonObject.put("赵六","23");
        System.out.println(jsonObject);

        JSONObject jsonObject1 = new JSONObject();
        jsonObject1.put("aa","20");
        jsonObject1.put("bb","21");
        jsonObject1.put("cc","22");
        jsonObject1.put("dd","23");

        JSONArray jsonArray = new JSONArray();
        jsonArray.add(jsonObject);
        System.out.println(jsonArray);
        jsonArray.add(jsonObject1);
        System.out.println(jsonArray);

        JSONArray jsonArray2 = new JSONArray();
        jsonArray2.add("a");
        jsonArray2.add("b");
        System.out.println(jsonArray2);

        JSONObject jsonObject2 = new JSONObject();
        jsonObject2.put("people",jsonArray);
        System.out.println(jsonObject2);

        JSON.toJSON("aa");
        JSON.parse("aa");

    }
}

package com.yilnz.surfing.test.seletorsTest;

import com.yilnz.surfing.core.SurfSpider;
import com.yilnz.surfing.core.basic.Page;
import com.yilnz.surfing.core.selectors.Selectable;
import lombok.Data;
import org.junit.Assert;
import org.junit.Test;
import sun.security.krb5.internal.crypto.Des;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class JsonSelectorTest {

    @Data
    class ClassRoom {
        List<Student> studentList = new ArrayList<>();
        List<Desk> deskList = new ArrayList<>();
    }

    @Data
    class Desk{
        private BigDecimal price;
        private String color;
    }
    @Data
    class Student {
        private String name;
        private int age;
    }

    private ClassRoom mockClassRoom(){
        ClassRoom classRoom = new ClassRoom();
        Desk desk = new Desk();
        desk.color = "purple";
        desk.price = new BigDecimal(11.5);
        Desk desk2 = new Desk();
        desk2.color = "yellow";
        desk2.price = new BigDecimal(12);
        classRoom.deskList.add(desk);
        classRoom.deskList.add(desk2);
        Student student = new Student();
        student.age = 11;
        student.name = "Jackson";
        Student student2 = new Student();
        student2.age = 12;
        student2.name = "Mike";
        classRoom.studentList.add(student);
        classRoom.studentList.add(student2);
        return classRoom;
    }

    @Test
    public void testJson(){
        Page page = SurfSpider.postJSON("https://httpbin.org/anything", mockClassRoom());
        System.out.println(page.getHtml());
        Assert.assertEquals(page.getHtml().selectJson("headers.Host").get(), "httpbin.org");
        Assert.assertNotNull(page.getHtml().selectJson("notExistResponse"));
        Assert.assertNull(page.getHtml().selectJson("notExistResponse").get());
        List<Selectable> nodes = page.getHtml().selectJson("$.json.deskList").nodes();
        Assert.assertEquals(nodes.stream().filter(e->e.selectJson("color").get().equals("yellow")).findFirst().get().selectJson("price").get(),"12");
        Assert.assertEquals(page.getHtml().selectJson("$.json.deskList[price=12]").selectJson("color").get(), "yellow");
    }
}

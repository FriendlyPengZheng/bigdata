package com.taomee.bigdata.task.query.update;

public class Test {
    public static void main(String[] args) {
        ItemInfo itemInfo = new ItemInfo();
        itemInfo.setNewValue("firstPay", 132456876);
        System.out.println(itemInfo.getNewValue("firstPay"));
    }
}


package com.taomee.bigdata.task.query.update;

class MonthPayInfo extends UserInfo {
    protected BasicValue<Double> amount = new BasicValue<Double>();
    protected BasicValue<Integer> count = new BasicValue<Integer>();

    protected boolean changed() {
        if(amount.changed() || count.changed())   return true;
        return false;
    }

    protected void clear() {
        amount.clear();
        count.clear();
    }
}

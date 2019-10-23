package com.taomee.bigdata.task.query.update;

class ItemInfo extends UserInfo {
    protected BasicValue<Long> firstPay = new BasicValue<Long>();
    protected BasicValue<Long> lastPay = new BasicValue<Long>();
    protected BasicValue<Double> totalAmount = new BasicValue<Double>();
    protected BasicValue<Integer> totalCount = new BasicValue<Integer>();

    protected boolean changed() {
        if(firstPay.changed() ||
                lastPay.changed() ||
                totalAmount.changed() ||
                totalCount.changed())   return true;
        return false;
    }

    protected void clear() {
        firstPay.clear();
        lastPay.clear();
        totalAmount.clear();
        totalCount.clear();
    }
}

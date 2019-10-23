package com.taomee.bigdata.task.query.update;

class CoinsInfo extends UserInfo {
    protected BasicValue<Double> consumeAmount = new BasicValue<Double>();
    protected BasicValue<Double> currentAmount = new BasicValue<Double>();

    protected boolean changed() {
        if(consumeAmount.changed() || currentAmount.changed())   return true;
        return false;
    }

    protected void clear() {
        consumeAmount.clear();
        currentAmount.clear();
    }
}

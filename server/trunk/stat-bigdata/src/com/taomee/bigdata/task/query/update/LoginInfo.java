package com.taomee.bigdata.task.query.update;

class LoginInfo extends UserInfo {
    protected BasicValue<Long> firstLogin = new BasicValue<Long>();
    protected BasicValue<Long> lastLogin = new BasicValue<Long>();

    protected boolean changed() {
        if(firstLogin.changed() || lastLogin.changed())   return true;
        return false;
    }

    protected void clear() {
        firstLogin.clear();
        lastLogin.clear();
    }
}

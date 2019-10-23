package com.taomee.bigdata.task.query.update;

abstract public class UserInfo {

    abstract protected boolean changed();
    abstract protected void clear();

    final protected void setOldValue(String param, Comparable value) {
        try {
            BasicValue o = (BasicValue)this.getClass().getDeclaredField(param).get(this);
            o.setOldValue(value);
        } catch (NoSuchFieldException e1) { 
        } catch (IllegalAccessException e2) { 
        }
    }

    final protected void setNewValue(String param, Comparable value) {
        try {
            BasicValue o = (BasicValue)this.getClass().getDeclaredField(param).get(this);
            o.setNewValue(value);
        } catch (NoSuchFieldException e1) { 
            System.out.println(e1.getMessage());
        } catch (IllegalAccessException e2) { 
            System.out.println(e2.getMessage());
        }
    }

    final protected Comparable getOldValue(String param) {
        try {
            BasicValue o = (BasicValue)this.getClass().getDeclaredField(param).get(this);
            return o.getOldValue();
        } catch (NoSuchFieldException e1) { 
        } catch (IllegalAccessException e2) { 
        }
        return null;
    }

    final protected Comparable getNewValue(String param) {
        try {
            BasicValue o = (BasicValue)this.getClass().getDeclaredField(param).get(this);
            return o.getNewValue();
        } catch (NoSuchFieldException e1) { 
            System.out.println(e1.getMessage());
        } catch (IllegalAccessException e2) { 
            System.out.println(e2.getMessage());
        }
        return null;
    }

    final protected boolean hasNewValue(String param) {
        try {
            BasicValue o = (BasicValue)this.getClass().getDeclaredField(param).get(this);
            return o.hasNewValue();
        } catch (NoSuchFieldException e1) { 
        } catch (IllegalAccessException e2) { 
        }
        return false;
    }

    final protected boolean hasOldValue(String param) {
        try {
            BasicValue o = (BasicValue)this.getClass().getDeclaredField(param).get(this);
            return o.hasOldValue();
        } catch (NoSuchFieldException e1) { 
        } catch (IllegalAccessException e2) { 
        }
        return false;
    }
}

package com.taomee.bigdata.task.query.update;

class BasicValue<T extends Comparable<T>> {
    private T oldValue;
    private T newValue;
    private boolean _hasOldValue = false;
    private boolean _hasNewValue = false;

    public void clear() {
        _hasOldValue = false;
        _hasNewValue = false;
    }

    public boolean hasOldValue() {
        return _hasOldValue;
    }

    public boolean hasNewValue() {
        return _hasNewValue;
    }

    public void setOldValue(T value) {
        oldValue = value;
        _hasOldValue = true;
    }

    public void setNewValue(T value) {
        newValue = value;
        _hasNewValue = true;
    }

    public T getOldValue() {
        return oldValue;
    }

    public T getNewValue() {
        return newValue;
    }

    public boolean changed() {
        if(_hasOldValue != _hasNewValue)    return true;
        if(_hasNewValue)    return newValue.compareTo(oldValue) != 0;
        return false;
    }
}

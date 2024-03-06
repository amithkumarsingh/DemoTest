package com.whitecoats.clinicplus.utils;

import androidx.annotation.MainThread;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import java.util.concurrent.atomic.AtomicBoolean;

public class SingleLiveEvent<T>  extends MutableLiveData<T> {

    AtomicBoolean pending = new AtomicBoolean(false);

    @MainThread
    public void observe(LifecycleOwner owner, Observer observer) {
        if (hasActiveObservers()) {
            //System.out.println("Multiple observers registered but only one will be notified of changes.");
        }
        // Observe the internal MutableLiveData
        //super.observe(owner, wrapObserver(observer));
        super.observe(owner, new Observer<T>() {
            @Override
            public void onChanged(T t) {
                if (pending.compareAndSet(true, false)) {
                    observer.onChanged(t);
                }
            }
        });

    }

    @MainThread
    public void observeForever(Observer observer) {
        if (hasActiveObservers()) {
            //  System.out.println("Multiple observers registered but only one will be notified of changes.");
        }
        super.observeForever(new Observer<T>() {
            @Override
            public void onChanged(T t) {
                if (pending.compareAndSet(true, false)) {
                    observer.onChanged(t);
                }
            }
        });
    }

    @MainThread
    public void setValue (T t){
        pending.set(true);
        super.setValue(t);
    }
    /**
     * Used for cases where T is Void, to make calls cleaner.
     */
    @MainThread
    public void call () {
        setValue(null);
    }

}

package com.saigoncoder.cameraipmodule.nestcam.mvc.worker;

/**
 * Created by tiencao on 9/23/15.
 */
public interface WorkerCallback<T> {
    void result(T t);
    void fail();
}

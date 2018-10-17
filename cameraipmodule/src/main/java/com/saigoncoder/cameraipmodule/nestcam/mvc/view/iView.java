package com.saigoncoder.cameraipmodule.nestcam.mvc.view;

/**
 * Created by tiencao on 9/23/15.
 */
public interface iView<T> {
    void response(T t);
    void error(int error);
}

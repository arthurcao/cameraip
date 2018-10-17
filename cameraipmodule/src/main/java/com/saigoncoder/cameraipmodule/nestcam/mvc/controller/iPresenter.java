package com.saigoncoder.cameraipmodule.nestcam.mvc.controller;


import com.saigoncoder.cameraipmodule.nestcam.mvc.view.iView;

/**
 * Created by tiencao on 9/23/15.
 */
public abstract class iPresenter<T> {
    protected iView<T> callback;
    public abstract void setPresenterListener(iView<T> callback);
}

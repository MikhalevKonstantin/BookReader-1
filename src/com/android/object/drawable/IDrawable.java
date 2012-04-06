package com.android.object.drawable;

import javax.microedition.khronos.opengles.GL10;

import com.android.opengl.texture.BaseTextureHolder;

public interface IDrawable {

    // public boolean isInitiated();

    public boolean isActived();

    public void initDrawable(GL10 gl);

    public void draw(GL10 gl);

    public void putTexture(BaseTextureHolder texture);
}

package com.smartnsoft.layoutinflator.sample;

import android.app.Application;

import com.smartnsoft.layoutinflator.graphics.TypefaceManager;
import com.smartnsoft.layoutinflator.graphics.TypefaceManager.SimpleTypefaceable;
import com.smartnsoft.layoutinflator.graphics.TypefaceManager.TypefaceLocation;
import com.smartnsoft.layoutinflator.view.SmartLayoutInflater;

/**
 * @author Ludovic Roland
 * @since 2018.03.22
 */
public final class LayoutInflatorApplication
    extends Application
{

  public static final class Typefaces
      extends SimpleTypefaceable
  {

    public static final Typefaces RobotoBold = new Typefaces(TypefaceLocation.Assets, "Roboto-Bold.ttf");

    public static final Typefaces RobotoLight = new Typefaces(TypefaceLocation.Assets, "Roboto-Light.ttf");

    public static final Typefaces RobotoRegular = new Typefaces(TypefaceLocation.Assets, "Roboto-Regular.ttf");

    private Typefaces(TypefaceLocation location, String fileName)
    {
      super(location, fileName);
    }

  }

  private static TypefaceManager<Typefaces> typefaceManager;

  public static TypefaceManager<Typefaces> getTypefaceManager()
  {
    if (typefaceManager == null)
    {
      typefaceManager = new TypefaceManager<>();
    }
    return typefaceManager;
  }

  @Override
  public void onCreate()
  {
    super.onCreate();

    if (BuildConfig.DEBUG == true)
    {
      SmartLayoutInflater.DEBUG_LOG_ENABLED = true;
    }
  }

}

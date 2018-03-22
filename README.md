[ ![Download](https://api.bintray.com/packages/smartnsoft/maven/layoutInflator/images/download.svg) ](https://bintray.com/smartnsoft/maven/layoutInflator/_latestVersion)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

# Deprecated
The SmartLayoutInflator is deprecated. No more development will be taking place. In order to play with fonts and typefaces use the [fonts in XML](https://developer.android.com/guide/topics/ui/look-and-feel/fonts-in-xml.html) feature available in the Support Library 26.0 and supported in Android Studio 3.0

# SmartLayoutInflator
The SmartLayoutInflator is a component which enables to tune the Android LayoutInflater and play with Typeface.

## Usage

### 1. Declare the fonts used in your app as typefaces 

In order to declare the fonts used into your app as `Typefaces`, you should create your own class using the `SimpleTypefaceable` class :

```java
public final class Typefaces
    extends SimpleTypefaceable
{

   public static final Typefaces RobotoBold = new Typefaces(TypefaceLocation.Assets, "Roboto-Bold.ttf");

   public static final Typefaces RobotoCondensedRegular = new Typefaces(TypefaceLocation.Assets, "RobotoCondensed-Regular.ttf");

  public static final Typefaces RobotoLight = new Typefaces(TypefaceLocation.Assets, "Roboto-Light.ttf");

  public static final Typefaces RobotoMedium = new Typefaces(TypefaceLocation.Assets, "Roboto-Medium.ttf");

  public static final Typefaces RobotoItalic = new Typefaces(TypefaceLocation.Assets, "Roboto-Italic.ttf");

  private Typefaces(TypefaceLocation location, String fileName)
  {
    super(location, fileName);
  }

}
```

In order to optimize the performance of your app, you can then declare a static instance of this class directly into your `Application` class :

```java
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

}
```

### 2. Declare the same fonts as XML attributs

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
  <declare-styleable name="Widgets">
    <attr name="android:textStyle"/>
    <attr name="typeface">
      <enum
        name="RobotoBold"
        value="0"
      />
      <enum
        name="RobotoCondensedRegular"
        value="1"
      />
      <enum
        name="RobotoLight"
        value="2"
      />
      <enum
        name="RobotoMedium"
        value="3"
      />
      <enum
        name="RobotoItalic"
        value="4"
      />
    </attr>
  </declare-styleable>
</resources>
```

### 3. Override the `LAYOUT_INFLATER_SERVICE` into your `Activity`

You have the override the `getSystemService` and return a `SmartLayoutInflater` :

```java
@Override
public Object getSystemService(String name)
{
  if (Context.LAYOUT_INFLATER_SERVICE.equals(name) == true)
  {
    final LayoutInflater defaultLayoutInflator = (LayoutInflater) super.getSystemService(name);
    return SmartLayoutInflater.getLayoutInflater(defaultLayoutInflator, this, this);
  }

  return super.getSystemService(name);
}
```

The static method `SmartLayoutInflater#getLayoutInflater` takes 3 parameters :
* `defaultLayoutInflater`: the default `LayoutInflater`
* `activity`: the current `Activity`
* `onViewInflatedListener`: an implementation of the `OnViewInflatedListener` interface

### 4. Implement the `OnViewInflatedListener` interface

This interface exposes only one method `onViewInflated` which is called each time a widget is inflated. Here you can set the typeface of your widget :

```java
@Override
 public void onViewInflated(Context context, View view, AttributeSet attrs)
{
  if (view instanceof TextView)
  {
    final TypedArray typedArray = obtainStyledAttributes(attrs, R.styleable.Widgets);
    final int typeface = typedArray.getInt(R.styleable.Widgets_typeface, 0);
    final TextView textView = (TextView) view;
     typedArray.recycle();
     switch (typeface)
    {
      case 0:
        textView.setTypeface(LayoutInflatorApplication.getTypefaceManager().getTypeface(getApplicationContext(), Typefaces.RobotoBold));
        break;
      case 1:
        textView.setTypeface(LayoutInflatorApplication.getTypefaceManager().getTypeface(getApplicationContext(), Typefaces.RobotoLight));
        break;
      case 2:
        textView.setTypeface(LayoutInflatorApplication.getTypefaceManager().getTypeface(getApplicationContext(), Typefaces.RobotoRegular));
        break;
    }
  }
}
```

## Other features

* Activate the debug mode in order to display logs :

```java
SmartLayoutInflater.DEBUG_LOG_ENABLED = true;
```

### Know issues

This library can break the activity theme.

## Download

To add SmartLayoutInflator to your project, include the following in your **app module** `build.gradle` file:

```groovy
implementation("om.smartnsoft:layoutinflator:${latest.version}")
```

## License

This SDK is under the MIT license.

## Author

This library was proudly made by [Smart&Soft](https://smartnsoft.com/), Paris FRANCE

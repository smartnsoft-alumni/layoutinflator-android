// The MIT License (MIT)
//
// Copyright (c) 2017 Smart&Soft
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.

package view;

import java.lang.reflect.Field;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Build.VERSION;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.smartnsoft.droid4me.log.Logger;
import com.smartnsoft.droid4me.log.LoggerFactory;

import org.xmlpull.v1.XmlPullParser;

/**
 * A work-around for enabling to intercept all inflated {@link View views}. Very similar to the private
 * {@link com.android.internal.policy.impl.PhoneLayoutInflater} class.
 *
 * @author Édouard Mercier
 * @since 2013.06.27
 */
public class SmartLayoutInflater
    extends LayoutInflater
    implements LayoutInflater.Factory
{

  public interface OnViewInflatedListener
  {

    void onViewInflated(Context context, View view, AttributeSet attrs);

  }

  public static final ThreadLocal<Factory> factoryThreadLocal = new ThreadLocal<Factory>();

  public static final ThreadLocal<Factory2> factory2ThreadLocal = new ThreadLocal<Factory2>();

  protected final static Logger log = LoggerFactory.getInstance(SmartLayoutInflater.class);

  private static final boolean STRIPPER_ENABLED = "".equals("");

  private static final String[] CLASS_PREFIXES = { "android.widget.", "android.webkit." };

  public static boolean DEBUG_LOG_ENABLED = false;

  public static LayoutInflater getLayoutInflater(LayoutInflater defaultLayoutInflater, Activity activity,
      OnViewInflatedListener onViewInflatedListener)
  {
    if (defaultLayoutInflater instanceof SmartLayoutInflater)
    {
      if (log.isDebugEnabled())
      {
        log.debug("Reusing the default layout inflater");
      }

      return defaultLayoutInflater;
    }

    if (log.isDebugEnabled())
    {
      log.debug("Creating a layout inflater");
    }

    return new SmartLayoutInflater(defaultLayoutInflater, activity, onViewInflatedListener);
  }

  @SuppressLint("NewApi")
  private static LayoutInflater stripLayoutFactories(LayoutInflater original)
  {
    if (SmartLayoutInflater.STRIPPER_ENABLED == false)
    {
      return original;
    }

    if (original.getFactory() != null)
    {
      factoryThreadLocal.set(original.getFactory());

      try
      {
        final Field factoryField = LayoutInflater.class.getDeclaredField("mFactory");
        factoryField.setAccessible(true);
        factoryField.set(original, null);
      }
      catch (Exception exception)
      {
        // Should not happen
      }
    }

    if (VERSION.SDK_INT >= 11 && original.getFactory2() != null)
    {
      factory2ThreadLocal.set(original.getFactory2());

      try
      {
        final Field factoryField = LayoutInflater.class.getDeclaredField("mFactory2");
        factoryField.setAccessible(true);
        factoryField.set(original, null);
      }
      catch (Exception exception)
      {
        // Should not happen
      }
    }

    return original;
  }

  private static void unstripLayoutFactories(LayoutInflater original)
  {
    if (SmartLayoutInflater.STRIPPER_ENABLED == false)
    {
      return;
    }

    if (factoryThreadLocal.get() != null)
    {
      try
      {
        final Field factoryField = LayoutInflater.class.getDeclaredField("mFactory");
        factoryField.setAccessible(true);
        factoryField.set(original, factoryThreadLocal.get());
      }
      catch (Exception exception)
      {
        // Should not happen
      }
      factoryThreadLocal.remove();
    }

    if (VERSION.SDK_INT >= 11)
    {
      if (factory2ThreadLocal.get() != null)
      {
        try
        {
          final Field factoryField = LayoutInflater.class.getDeclaredField("mFactory2");
          factoryField.setAccessible(true);
          factoryField.set(original, factory2ThreadLocal.get());
        }
        catch (Exception exception)
        {
          // Should not happen
        }
        factory2ThreadLocal.remove();
      }
    }
  }

  private final OnViewInflatedListener onViewInflatedListener;

  private Factory inheritedFactory;

  public SmartLayoutInflater(LayoutInflater original, Context newContext, OnViewInflatedListener onViewInflatedListener)
  {
    super(SmartLayoutInflater.stripLayoutFactories(original), newContext);
    SmartLayoutInflater.unstripLayoutFactories(original);
    this.onViewInflatedListener = onViewInflatedListener;
  }

  @Override
  public View onCreateView(String name, Context context, AttributeSet attrs)
  {
    if (name.indexOf('.') != -1)
    {
      try
      {
        final View view = createView(name, null, attrs);

        if (view != null)
        {
          onCustomizeView(view, attrs);
        }

        return view;
      }
      catch (Exception exception)
      {
        // Does not matter, we let the LayoutInflater.onCreateView() and LayoutInflater.createView() handle that from the
        // LayoutInflater.createViewFromTag() method
      }
    }
    else if (inheritedFactory != null)
    {
      return inheritedFactory.onCreateView(name, context, attrs);
    }

    return null;
  }

  @Override
  protected View onCreateView(String name, AttributeSet attrs)
      throws ClassNotFoundException
  {
    for (String prefix : SmartLayoutInflater.CLASS_PREFIXES)
    {
      try
      {
        final View view = createView(name, prefix, attrs);

        if (view != null)
        {
          onCustomizeView(view, attrs);
          return view;
        }
      }
      catch (ClassNotFoundException exception)
      {
        // In this case we want to let the base class take a crack at it
      }
    }

    final View view = super.onCreateView(name, attrs);

    if (view != null)
    {
      onCustomizeView(view, attrs);
      return view;
    }

    return null;
  }

  @Override
  public LayoutInflater cloneInContext(Context newContext)
  {
    return new SmartLayoutInflater(this, newContext, this.onViewInflatedListener);
  }

  @Override
  public void setFactory(Factory factory)
  {
    if (factory == this)
    {
      super.setFactory(factory);
    }
    else
    {
      inheritedFactory = factory;
    }
  }

  @Override
  public View inflate(XmlPullParser parser, ViewGroup root, boolean attachToRoot)
  {
    if (getFactory() == null)
    {
      // If the factory is already set, an IllegalStateException exception will be thrown, this is why we check this
      setFactory(this);
    }

    final long start = System.currentTimeMillis();
    final View view = super.inflate(parser, root, attachToRoot);

    if (SmartLayoutInflater.DEBUG_LOG_ENABLED)
    {
      final long durationInMilliseconds = System.currentTimeMillis() - start;
      log.debug("Inflated a view in " + durationInMilliseconds + " ms.");

      if (durationInMilliseconds >= 30)
      {
        log.warn("Expensive view inflation!");
      }
    }

    return view;
  }

  protected void onCustomizeView(View view, AttributeSet attrs)
  {
    if (onViewInflatedListener != null)
    {
      onViewInflatedListener.onViewInflated(getContext(), view, attrs);
    }
  }

}

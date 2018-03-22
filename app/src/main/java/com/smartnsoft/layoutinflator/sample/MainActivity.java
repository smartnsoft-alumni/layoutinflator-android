package com.smartnsoft.layoutinflator.sample;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.smartnsoft.layoutinflator.sample.LayoutInflatorApplication.Typefaces;
import com.smartnsoft.layoutinflator.sample.fragment.MainActivityFragment;
import com.smartnsoft.layoutinflator.view.SmartLayoutInflater;
import com.smartnsoft.layoutinflator.view.SmartLayoutInflater.OnViewInflatedListener;

/**
 * @author Ludovic Roland
 * @since 2018.03.22
 */
public final class MainActivity
    extends AppCompatActivity
    implements OnViewInflatedListener
{

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    final Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    final FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
    fragmentTransaction.replace(R.id.framelayout, new MainActivityFragment());
    fragmentTransaction.commitAllowingStateLoss();
  }

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

}

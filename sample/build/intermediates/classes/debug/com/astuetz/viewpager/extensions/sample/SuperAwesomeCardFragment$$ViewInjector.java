// Generated code from Butter Knife. Do not modify!
package com.astuetz.viewpager.extensions.sample;

import android.view.View;
import butterknife.ButterKnife.Finder;

public class SuperAwesomeCardFragment$$ViewInjector {
  public static void inject(Finder finder, final com.astuetz.viewpager.extensions.sample.SuperAwesomeCardFragment target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131427415, "field 'textView'");
    target.textView = (android.widget.TextView) view;
  }

  public static void reset(com.astuetz.viewpager.extensions.sample.SuperAwesomeCardFragment target) {
    target.textView = null;
  }
}

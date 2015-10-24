// Generated code from Butter Knife. Do not modify!
package com.astuetz.viewpager.extensions.fragment;

import android.view.View;
import butterknife.ButterKnife.Finder;

public class SwapsFragment$$ViewInjector {
  public static void inject(Finder finder, final com.astuetz.viewpager.extensions.fragment.SwapsFragment target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131427423, "field 'textView'");
    target.textView = (android.widget.TextView) view;
  }

  public static void reset(com.astuetz.viewpager.extensions.fragment.SwapsFragment target) {
    target.textView = null;
  }
}

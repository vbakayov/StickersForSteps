// Generated code from Butter Knife. Do not modify!
package com.astuetz.viewpager.extensions.fragment;

import android.view.View;
import butterknife.ButterKnife.Finder;

public class MainActivity$$ViewInjector {
  public static void inject(Finder finder, final com.astuetz.viewpager.extensions.fragment.MainActivity target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131492989, "field 'toolbar'");
    target.toolbar = (android.support.v7.widget.Toolbar) view;
    view = finder.findRequiredView(source, 2131492950, "field 'tabs'");
    target.tabs = (com.astuetz.SlidingTabLayout) view;
    view = finder.findRequiredView(source, 2131492951, "field 'pager'");
    target.pager = (android.support.v4.view.ViewPager) view;
  }

  public static void reset(com.astuetz.viewpager.extensions.fragment.MainActivity target) {
    target.toolbar = null;
    target.tabs = null;
    target.pager = null;
  }
}

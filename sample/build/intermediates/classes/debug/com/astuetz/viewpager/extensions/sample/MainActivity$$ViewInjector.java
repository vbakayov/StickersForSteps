// Generated code from Butter Knife. Do not modify!
package com.astuetz.viewpager.extensions.sample;

import android.view.View;
import butterknife.ButterKnife.Finder;

public class MainActivity$$ViewInjector {
  public static void inject(Finder finder, final com.astuetz.viewpager.extensions.sample.MainActivity target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131427430, "field 'toolbar'");
    target.toolbar = (android.support.v7.widget.Toolbar) view;
    view = finder.findRequiredView(source, 2131427409, "field 'tabs'");
    target.tabs = (com.astuetz.PagerSlidingTabStrip) view;
    view = finder.findRequiredView(source, 2131427410, "field 'pager'");
    target.pager = (android.support.v4.view.ViewPager) view;
  }

  public static void reset(com.astuetz.viewpager.extensions.sample.MainActivity target) {
    target.toolbar = null;
    target.tabs = null;
    target.pager = null;
  }
}

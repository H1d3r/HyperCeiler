/*
  * This file is part of HyperCeiler.

  * HyperCeiler is free software: you can redistribute it and/or modify
  * it under the terms of the GNU Affero General Public License as
  * published by the Free Software Foundation, either version 3 of the
  * License.

  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of
  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  * GNU Affero General Public License for more details.

  * You should have received a copy of the GNU Affero General Public License
  * along with this program.  If not, see <https://www.gnu.org/licenses/>.

  * Copyright (C) 2023-2025 HyperCeiler Contributions
*/
package com.sevtinge.hyperceiler.hook.module.hook.securitycenter.app

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import com.sevtinge.hyperceiler.hook.R
import com.sevtinge.hyperceiler.hook.module.base.BaseHook
import io.github.kyuubiran.ezxhelper.core.finder.MethodFinder.`-Static`.methodFinder
import io.github.kyuubiran.ezxhelper.core.util.ClassUtil.loadClass
import io.github.kyuubiran.ezxhelper.xposed.EzXposed
import io.github.kyuubiran.ezxhelper.xposed.EzXposed.appContext
import io.github.kyuubiran.ezxhelper.xposed.EzXposed.initAppContext
import io.github.kyuubiran.ezxhelper.xposed.dsl.HookFactory.`-Static`.createHook

@SuppressLint("DiscouragedApi")
object AddAppManagerEntry : BaseHook() {
    //override val key = "add_aosp_app_manager_entry"
    private val idIdMiuixActionEndMenuGroup by lazy {
        appContext.resources.getIdentifier("miuix_action_end_menu_group", "id", EzXposed.hookedPackageName)
    }
    private val idDrawableIconSettings by lazy {
        appContext.resources.getIdentifier("icon_settings", "drawable", EzXposed.hookedPackageName)
    }

    override fun init() {
        val clazzAppManagerMainActivity = loadClass("com.miui.appmanager.AppManagerMainActivity")
        clazzAppManagerMainActivity.methodFinder().filterByName("onCreateOptionsMenu").first()
            .createHook {
                after {
                    initAppContext(it.thisObject as Activity, true)
                    val menuItem = (it.args[0] as Menu).add(
                        idIdMiuixActionEndMenuGroup, 0, 0, R.string.security_center_aosp_app_manager
                    )
                    menuItem.intent = Intent(Intent.ACTION_MAIN).setClassName(
                        "com.android.settings",
                        "com.android.settings.applications.ManageApplications"
                    )
                    menuItem.setIcon(idDrawableIconSettings)
                    menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
                }
            }
    }
}

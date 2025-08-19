package com.sevtinge.hyperceiler.hook.module.hook.systemframework

import com.sevtinge.hyperceiler.hook.module.base.BaseHook
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import io.github.kyuubiran.ezxhelper.core.util.ClassUtil

object NoAccessDeviceLogsRequest : BaseHook() {
    private lateinit var mActivityManagerInternal: Any
    private lateinit var mLogcatManagerService: Any
    override fun init() {
        val logAccessClient = ClassUtil.loadClass(
            "com.android.server.logcat.LogcatManagerService\$LogAccessClient",
            lpparam.classLoader
        )

        XposedHelpers.findAndHookMethod(
            "com.android.server.logcat.LogcatManagerService", lpparam.classLoader,
            "onStart",
            object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam) {
                    try {
                        mLogcatManagerService = param.thisObject
                        mActivityManagerInternal = XposedHelpers.getObjectField(
                            mLogcatManagerService, "mActivityManagerInternal"
                        )
                    } catch (t: Throwable) {
                        logE(TAG, "NoAccessDeviceLogsRequest -> onStart", t)
                    }
                }
            }
        )

        XposedHelpers.findAndHookMethod(
            "com.android.server.logcat.LogcatManagerService", lpparam.classLoader,
            "processNewLogAccessRequest",
            logAccessClient,
            object : XC_MethodHook() {
                override fun beforeHookedMethod(param: MethodHookParam) {
                    try {
                        val client = param.args[0]
                        if (client == null || mActivityManagerInternal == null) return
                        val uid = XposedHelpers.getIntField(client, "mUid")
                        val packageName =
                            XposedHelpers.getObjectField(client, "mPackageName") as String
                        XposedHelpers.callMethod(
                            mLogcatManagerService,
                            "onAccessApprovedForClient",
                            client
                        )
                        // debug 用，取消禁用详细日志输出可进行调试
                        logD(
                            TAG,
                            this@NoAccessDeviceLogsRequest.lpparam.packageName,
                            "NoAccessDeviceLogsRequest bypass for package=$packageName uid=$uid"
                        )
                        param.result = null
                    } catch (t: Throwable) {
                        // 输出异常日志
                        logE(
                            TAG,
                            this@NoAccessDeviceLogsRequest.lpparam.packageName,
                            "processNewLogAccessRequest failed",
                            t
                        )
                    }
                }
            })
        // 米客原来的取消方法，未知情况封堵失败
        /*try {
            loadClass("com.android.server.logcat.LogcatManagerService").methodFinder().filter {
                name == "onLogAccessRequested"
            }.toList().createHooks {
                before { param ->
                    XposedHelpers.callMethod(param.thisObject, "declineRequest", param.args[0])
                    param.result = null
                }
            }
        } catch (t: Throwable) {
            logE(TAG, this.lpparam.packageName, t)
        }*/
    }
}

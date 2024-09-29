/*
 * This file is part of HyperCeiler.
 *
 * HyperCeiler is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 * Copyright (C) 2023-2024 HyperCeiler Contributions
 */
package com.sevtinge.hyperceiler.module.hook.downloadsui;

import static de.robv.android.xposed.XposedHelpers.getObjectField;
import static de.robv.android.xposed.XposedHelpers.setObjectField;

import com.sevtinge.hyperceiler.module.base.BaseHook;
import com.sevtinge.hyperceiler.module.base.dexkit.DexKit;
import com.sevtinge.hyperceiler.module.base.dexkit.IDexKit;

import org.luckypray.dexkit.DexKitBridge;
import org.luckypray.dexkit.query.*;
import org.luckypray.dexkit.query.matchers.*;
import org.luckypray.dexkit.result.*;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.lang.reflect.Field;

public class AlwaysShowDownloadLink extends BaseHook {

    @Override
    public void init() {

        Class<?> class1 = (Class<?>) DexKit.getDexKitBridge("DownloadInfo", new IDexKit() {
            @Override
            public AnnotatedElement dexkit(DexKitBridge bridge) throws ReflectiveOperationException {
                ClassData classData = bridge.findClass(FindClass.create()
                        .excludePackages("androidx")
                        .matcher(
                                ClassMatcher.create()
                                        .usingEqStrings("/s", " | ", "/")
                        )).singleOrNull();
                return classData.getInstance(lpparam.classLoader);
            }
        });


        Method method1 = (Method) DexKit.getDexKitBridge("ShowTaskDetailMatcher", new IDexKit() {
            @Override
            public AnnotatedElement dexkit(DexKitBridge bridge) throws ReflectiveOperationException {
                MethodData methodData = bridge.findMethod(FindMethod.create()
                        .matcher(MethodMatcher.create()
                                .declaredClass(ClassMatcher.create().usingStrings("onEventMainThread noScrollListView="))
                                .usingNumbers(4, 8, 0)
                                .paramTypes(class1)
                                .returnType(void.class)
                        )).singleOrNull();
                return methodData.getMethodInstance(lpparam.classLoader);
            }
        });

        Field field1 = (Field) DexKit.getDexKitBridge("DownloadUrl", new IDexKit() {
            @Override
            public AnnotatedElement dexkit(DexKitBridge bridge) throws ReflectiveOperationException {
                FieldData fieldData = bridge.findField(FindField.create()
                        .matcher(FieldMatcher.create()
                                .addReadMethod(MethodMatcher.create()
                                        .usingStrings("android.intent.action.PICK", "com.android.fileexplorer"))
                                .declaredClass(class1)
                                .type(String.class)
                        )).singleOrNull();
                return fieldData.getFieldInstance(lpparam.classLoader);
            }
        });

        Field field2 = (Field) DexKit.getDexKitBridge("DownloadDesc", new IDexKit() {
            @Override
            public AnnotatedElement dexkit(DexKitBridge bridge) throws ReflectiveOperationException {
                FieldDataList fieldDataList = bridge.findField(FindField.create()
                        .matcher(FieldMatcher.create()
                                .addReadMethod(MethodMatcher.create()
                                        .declaredClass(ClassMatcher.create().usingStrings("onEventMainThread noScrollListView="))
                                        .usingNumbers(4, 8, 0)
                                        .paramTypes(class1)
                                        .returnType(void.class))
                                .type(String.class)
                        ));
                // 感觉不太优雅 先这样吧.jpg 暂时没有更好的匹配方式了 反正method里就这两string
                for (FieldData field : fieldDataList) {
                    if (field.getName().equals(field1.getName())) continue;
                    return field.getFieldInstance(lpparam.classLoader);
                }
                return null;

            }
        });

        hookMethod(method1, new MethodHook() {
            public void before(MethodHookParam param) throws Throwable {
                String url = (String) getObjectField(param.args[0], field1.getName());
                // @TODO 显示来源应用和路径
                logD(TAG, lpparam.packageName, "url:" + url);
                setObjectField(param.args[0], field2.getName(), "");
            }
        });

        // findAndHookMethod("h1.h", "R", "i1.a",new MethodHook() {
        //     @Override
        //     public void before(MethodHookParam param) throws Throwable {
        //         logD(TAG, lpparam.packageName, "source: " + getObjectField(param.args[0], "r") + "  path: " + getObjectField(param.args[0], "i"));
        //         setObjectField(param.args[0], "y", "");
        //     }
        // });
    }
}

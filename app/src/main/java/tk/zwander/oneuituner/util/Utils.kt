package tk.zwander.oneuituner.util

import android.app.Activity
import android.content.Context
import android.content.IntentSender
import android.content.pm.PackageInstaller
import android.content.pm.PackageManager
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.TypedValue
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import org.w3c.dom.Document
import tk.zwander.oneuituner.R
import tk.zwander.overlaylib.ResourceData
import tk.zwander.overlaylib.ResourceFileData
import tk.zwander.overlaylib.doCompileAlignAndSign
import tk.zwander.overlaylib.makeResourceXml
import java.io.*
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

val mainHandler = Handler(Looper.getMainLooper())

val Context.prefs: PrefManager
    get() = PrefManager.getInstance(this)

val PreferenceFragmentCompat.navController: NavController
    get() = NavHostFragment.findNavController(this)

val navOptions =
    NavOptions.Builder()
        .setEnterAnim(android.R.anim.fade_in)
        .setExitAnim(android.R.anim.fade_out)
        .setPopEnterAnim(android.R.anim.fade_in)
        .setPopExitAnim(android.R.anim.fade_out)
        .build()

val Activity.navController: NavController
    get() = findNavController(R.id.nav_host)

val String.isValidClockFormat: Boolean
    get() = try {
        SimpleDateFormat(this)
        true
    } catch (e: Exception) {
        false
    }

fun Context.pxToDp(px: Float) =
    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, px, resources.displayMetrics)

val Context.isSynergyInstalled: Boolean
    get() = try {
        packageManager.getPackageInfo("projekt.samsung.theme.compiler", 0)
        true
    } catch (e: java.lang.Exception) {
        false
    }

val needsSynergy: Boolean
    get() = !Build.MODEL.run { contains("960") } || Build.VERSION.SDK_INT > Build.VERSION_CODES.P

fun Document.flattenToString(): String {
    val source = DOMSource(this)
    val writer = StringWriter()
    val result = StreamResult(writer)
    val tf = TransformerFactory.newInstance()
    val transformer = tf.newTransformer()

    transformer.transform(source, result)

    return writer.toString()
}

fun Context.doCompile(listener: (List<File>) -> Unit) {
    var count = 0
    val files = ArrayList<File>()

    count++
    doCompileAlignAndSign(
        "com.android.systemui",
        "oneuituner.systemui",
        resFiles = mutableListOf<ResourceFileData>().apply {
            add(
                ResourceFileData(
                    "status_bar.xml",
                    "layout",
                    (if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) makeAndroid9StatusBar() else makeAndroid10StatusBar()).flattenToString()
                )
            )

            add(
                ResourceFileData(
                    "integers.xml",
                    "values",
                    makeResourceXml(
                        *arrayListOf<ResourceData>().apply {
                            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                                add(
                                    ResourceData(
                                        "integer",
                                        "quick_qs_tile_num",
                                        prefs.headerCountPortrait.toString()
                                    )
                                )
                                add(
                                    ResourceData(
                                        "integer",
                                        "quick_qs_tile_min_num",
                                        "2"
                                    )
                                )
                                add(
                                    ResourceData(
                                        "integer",
                                        "qspanel_screen_grid_columns_5",
                                        prefs.qsColCountPortrait.toString()
                                    )
                                )
                                add(
                                    ResourceData(
                                        "integer",
                                        "qspanel_screen_grid_rows",
                                        prefs.qsRowCountPortrait.toString()
                                    )
                                )
                            } else {
                                add(
                                    ResourceData(
                                        "integer",
                                        "quick_qs_panel_max_columns",
                                        prefs.headerCountPortrait.toString()
                                    )
                                )
                                add(
                                    ResourceData(
                                        "integer",
                                        "quick_qs_tile_min_num",
                                        "2"
                                    )
                                )
                                add(
                                    ResourceData(
                                        "integer",
                                        "sec_quick_settings_max_columns",
                                        "20"
                                    )
                                )
                                add(
                                    ResourceData(
                                        "integer",
                                        "sec_quick_settings_num_columns",
                                        "20"
                                    )
                                )
                                add(
                                    ResourceData(
                                        "integer",
                                        "sec_quick_settings_max_rows",
                                        "20"
                                    )
                                )
                            }
                        }.toTypedArray()
                    )
                )
            )

            add(
                ResourceFileData(
                    "integers.xml",
                    "values-land",
                    makeResourceXml(
                        *arrayListOf<ResourceData>().apply {
                            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                                add(
                                    ResourceData(
                                        "integer",
                                        "quick_qs_tile_num",
                                        prefs.headerCountLandscape.toString()
                                    )
                                )
                                add(
                                    ResourceData(
                                        "integer",
                                        "qspanel_screen_grid_columns_5",
                                        prefs.qsColCountLandscape.toString()
                                    )
                                )
                                add(
                                    ResourceData(
                                        "integer",
                                        "qspanel_screen_grid_rows",
                                        prefs.qsRowCountLandscape.toString()
                                    )
                                )
                            } else {
                                add(
                                    ResourceData(
                                        "integer",
                                        "quick_qs_panel_max_columns",
                                        prefs.headerCountLandscape.toString()
                                    )
                                )
                                add(
                                    ResourceData(
                                        "integer",
                                        "sec_quick_settings_max_columns",
                                        "20"
                                    )
                                )
                                add(
                                    ResourceData(
                                        "integer",
                                        "sec_quick_settings_num_columns",
                                        "20"
                                    )
                                )
                                add(
                                    ResourceData(
                                        "integer",
                                        "sec_quick_settings_max_rows",
                                        "20"
                                    )
                                )
                            }
                        }.toTypedArray()
                    )
                )
            )
            add(
                ResourceFileData(
                    "dimens.xml",
                    "values",
                    makeResourceXml(
                        *arrayListOf<ResourceData>().apply {
                            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                                add(ResourceData(
                                    "dimen",
                                    "qs_tile_height_5x3_ratio",
                                    prefs.qsRowCountPortrait.run {
                                        when {
                                            this > 4 -> "9.5"
                                            this > 3 -> "8.0"
                                            else -> "7.1"
                                        }
                                    }
                                ))
                                add(ResourceData(
                                    "dimen",
                                    "qs_tile_icon_size_5x3_ratio",
                                    prefs.qsRowCountPortrait.run {
                                        when {
                                            this > 4 -> if (!needsSynergy) "21.0" else "3.0"
                                            else -> if (!needsSynergy) "19.43" else "2.73"
                                        }
                                    }
                                ))
                                add(ResourceData(
                                    "dimen",
                                    "qs_tile_text_size",
                                    prefs.qsRowCountPortrait.run {
                                        when {
                                            this > 4 -> "12sp"
                                            else -> "13sp"
                                        }
                                    }
                                ))
                            } else {
                                add(
                                    ResourceData(
                                        "dimen",
                                        "sec_qs_tile_width",
                                        "${calculateQsWidthForColumnCount(
                                            prefs.qsColCountPortrait,
                                            false
                                        )}dp"
                                    )
                                )
                            }
                        }.toTypedArray()
                    )
                )
            )
            add(
                ResourceFileData(
                    "dimens.xml",
                    "values-sw411dp",
                    makeResourceXml(
                        *arrayListOf<ResourceData>().apply {
                            add(ResourceData(
                                "dimen",
                                "qs_tile_text_size",
                                prefs.qsRowCountPortrait.run {
                                    when {
                                        this > 4 -> "13sp"
                                        else -> "14sp"
                                    }
                                }
                            ))

                            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                                add(
                                    ResourceData(
                                        "dimen",
                                        "sec_qs_tile_width",
                                        "${calculateQsWidthForColumnCount(
                                            prefs.qsColCountPortrait,
                                            false
                                        )}dp"
                                    )
                                )
                            }
                        }.toTypedArray()
                    )
                )
            )

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                add(
                    ResourceFileData(
                        "dimens.xml",
                        "values-land",
                        makeResourceXml(
                            ResourceData(
                                "dimen",
                                "sec_qs_tile_width",
                                "${calculateQsWidthForColumnCount(
                                    prefs.qsColCountLandscape,
                                    true
                                )}dp"
                            )
                        )
                    )
                )
            }

            if (prefs.customQsDateFormat && prefs.qsDateFormat.isValidClockFormat) {
                add(
                    ResourceFileData(
                        "strings.xml",
                        "values",
                        makeResourceXml(
                            ResourceData(
                                "string",
                                "system_ui_quick_panel_date_pattern",
                                prefs.qsDateFormat
                            )
                        )
                    )
                )
            }

            if (prefs.hideQsTileBackground) {
                add(
                    ResourceFileData(
                        "colors.xml",
                        "values",
                        makeResourceXml(
                            ResourceData(
                                "color",
                                "qs_tile_round_background_on",
                                "@android:color/transparent"
                            ),
                            ResourceData(
                                "color",
                                "qs_tile_round_background_off",
                                "@android:color/transparent"
                            ),
                            ResourceData(
                                "color",
                                "qs_tile_round_background_dim",
                                "@android:color/transparent"
                            ),
                            ResourceData(
                                "color",
                                "qs_tile_round_bottom_background",
                                "@android:color/transparent"
                            ),
                            ResourceData(
                                "color",
                                "qs_tile_round_background_image",
                                "@android:color/transparent"
                            )
                        )
                    )
                )
                add(
                    ResourceFileData(
                        "colors.xml",
                        "values-night",
                        makeResourceXml(
                            ResourceData(
                                "color",
                                "qs_tile_round_background_off",
                                "@android:color/transparent"
                            ),
                            ResourceData(
                                "color",
                                "qs_tile_round_background_dim",
                                "@android:color/transparent"
                            ),
                            ResourceData(
                                "color",
                                "qs_tile_round_bottom_background",
                                "@android:color/transparent"
                            )
                        )
                    )
                )
            }
            if (prefs.disable5ge) {
                add(
                    ResourceFileData(
                        "stat_sys_data_connected_5ge_att",
                        "drawable",
                        StringBuilder()
                            .append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n")
                            .append("<bitmap xmlns:android=\"https://schemas.android.com/apk/res/android\" android:src=\"@drawable/stat_sys_data_connected_lte_att\" />\n")
                            .toString()
                    )
                )
            }

            if (prefs.disableFlashingWiFi) {
                add(
                    ResourceFileData(
                        "stat_sys_wifi_signal_checking",
                        "drawable",
                        StringBuilder()
                            .append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n")
                            .append("<bitmap xmlns:android=\"https://schemas.android.com/apk/res/android\" android:src=\"@drawable/stat_sys_wifi_signal_checking_dim\" />\n")
                            .toString()
                    )
                )
            }

            add(
                ResourceFileData(
                    "bools.xml",
                    "values",
                    makeResourceXml(
                        ResourceData(
                            "bool",
                            "config_enableLockScreenRotation",
                            "${prefs.lockScreenRotation}"
                        )
                    )
                )
            )
        },
        listener = {
            files.add(it)
            if (files.size >= count) listener(files)
        }
    )

    count++
    doCompileAlignAndSign(
        "android",
        "oneuituner.android",
        resFiles = mutableListOf<ResourceFileData>().apply {
            add(
                ResourceFileData(
                    "config.xml",
                    "values",
                    makeResourceXml(
                        *mutableListOf(
                            ResourceData(
                                "dimen",
                                "navigation_bar_height",
                                "${prefs.navHeight}dp"
                            ),
                            ResourceData(
                                "dimen",
                                "navigation_bar_width",
                                "${prefs.navHeight}dp"
                            ),
                            ResourceData(
                                "dimen",
                                "status_bar_height_portrait",
                                "${prefs.statusBarHeight}dp"
                            )
                        ).apply {
                            if (prefs.oldRecents && Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                                add(
                                    ResourceData(
                                        "string",
                                        "config_recentsComponentName",
                                        "com.android.systemui/.recents.RecentsActivity",
                                        "translatable=\"false\""
                                    )
                                )
                            }
                        }.toTypedArray()
                    )
                )
            )
            add(
                ResourceFileData(
                    "integers.xml",
                    "values",
                    makeResourceXml(
                        *mutableListOf(
                            ResourceData(
                                "integer",
                                "multiwindow_freeform_max_count",
                                "${prefs.freeformMax}"
                            ),
                            ResourceData(
                                "integer",
                                "multiwindow_desktop_freeform_max_count",
                                "${prefs.desktopFreeformMax}"
                            )
                        ).toTypedArray()
                    )
                )
            )
        },
        listener = {
            files.add(it)
            if (files.size >= count) listener(files)
        }
    )
}

private val installParams by lazy {
    PackageInstaller.SessionParams(PackageInstaller.SessionParams.MODE_FULL_INSTALL).apply {
        installFlags = installFlags or PackageManager.INSTALL_REPLACE_EXISTING
    }
}

val Context.statusBarHeight: Int
    get() = resources.getDimensionPixelSize(resources.getIdentifier("status_bar_height", "dimen", "android"))

val Context.navigationBarHeight: Int
    get() = resources.getDimensionPixelSize(resources.getIdentifier("navigation_bar_height", "dimen", "android"))

fun Context.installNormally(updateSender: IntentSender, vararg source: File) {
    val installer = packageManager.packageInstaller
    val sessionId = installer.createSession(installParams)
    val session = installer.openSession(sessionId)

    Log.e("OneUITuner", source.joinToString(","))

    source.forEach {
        val fileUri = FileProvider.getUriForFile(
            this,
            "$packageName.apkprovider", it
        )

        val output = session.openWrite("OneUITunerInstall", 0, -1)
        val input = contentResolver.openInputStream(fileUri)

        try {
            transfer(input, output)
        } catch (e: IOException) {
            Log.e("OneUITuner", "error", e)
        }

        session.fsync(output)
        input.close()
        output.close()
    }

    session.commit(updateSender)
}

fun transfer(input: InputStream, output: OutputStream) {
    var read: Int
    val buffer = ByteArray(64 * 1024)

    while (true) {
        read = input.read(buffer)

        if (read <= 0) break

        output.write(buffer, 0, read)
    }

    output.flush()
}

fun PreferenceFragmentCompat.setPreferenceEnabled(key: CharSequence, enabled: Boolean) {
    findPreference<Preference>(key)?.isEnabled = enabled
}

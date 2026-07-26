package com.example.util

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast

object AquaWalletUtil {
    fun launchAquaWallet(context: Context) {
        val pm = context.packageManager
        val knownPackages = listOf(
            "com.jan3.aqua",
            "io.aquawallet",
            "io.sideswap.aqua",
            "com.aquawallet",
            "io.aquawallet.app"
        )

        var targetPackage: String? = null

        // 1. Direct package check
        for (pkg in knownPackages) {
            try {
                val intent = pm.getLaunchIntentForPackage(pkg)
                if (intent != null) {
                    targetPackage = pkg
                    break
                }
            } catch (_: Exception) {}
        }

        // 2. Scan installed packages for any package containing "aqua" or "jan3"
        if (targetPackage == null) {
            try {
                val installedApps = pm.getInstalledPackages(0)
                for (pInfo in installedApps) {
                    val pName = pInfo.packageName.lowercase()
                    if (pName.contains("aqua") || pName.contains("jan3") || pName.contains("sideswap")) {
                        val launchIntent = pm.getLaunchIntentForPackage(pInfo.packageName)
                        if (launchIntent != null) {
                            targetPackage = pInfo.packageName
                            break
                        }
                    }
                }
            } catch (_: Exception) {}
        }

        // 3. Launch if package found
        if (targetPackage != null) {
            try {
                val launchIntent = pm.getLaunchIntentForPackage(targetPackage)?.apply {
                    action = Intent.ACTION_MAIN
                    addCategory(Intent.CATEGORY_LAUNCHER)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
                }
                if (launchIntent != null) {
                    context.startActivity(launchIntent)
                    Toast.makeText(context, "Aqua Wallet 앱을 실행합니다.", Toast.LENGTH_SHORT).show()
                    return
                }
            } catch (_: Exception) {}
        }

        // 4. Try URI schemes
        val schemes = listOf("aqua://", "bitcoin:", "liquidnetwork:")
        for (scheme in schemes) {
            try {
                val uriIntent = Intent(Intent.ACTION_VIEW, Uri.parse(scheme)).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                val resolveInfo = pm.resolveActivity(uriIntent, PackageManager.MATCH_DEFAULT_ONLY)
                if (resolveInfo != null) {
                    context.startActivity(uriIntent)
                    Toast.makeText(context, "Aqua Wallet 앱으로 이동합니다.", Toast.LENGTH_SHORT).show()
                    return
                }
            } catch (_: Exception) {}
        }

        // 5. Fallback: Not installed -> Open Play Store
        try {
            Toast.makeText(context, "Aqua Wallet 앱이 설치되어 있지 않아 Play 스토어로 이동합니다.", Toast.LENGTH_LONG).show()
            val storeIntent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.jan3.aqua")).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(storeIntent)
        } catch (_: Exception) {
            try {
                val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.jan3.aqua")).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(webIntent)
            } catch (_: Exception) {
                Toast.makeText(context, "Aqua Wallet 연결 실패", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

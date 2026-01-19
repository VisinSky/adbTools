// 文件名：com/davik/adbtools/MainActivity.kt
package com.davik.adbtools

import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.view.WindowCompat
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.davik.adbtools.adb.*

import com.davik.adbtools.screens.*
import com.davik.adbtools.tools.LanScanner
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.net.ssl.SSLContext


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = true
        setContent {
            MaterialTheme(
                colorScheme = MaterialTheme.colorScheme.copy(
                    background = Color(0xFFF8F9FA),
                    primary = Color(0xFF5B93E6)
                )


            ) {
                var connectionTrigger by remember { mutableIntStateOf(0) }
                val connectedDevices = remember { mutableStateListOf<DeviceInfo>() }
                var ipAddress by remember { mutableStateOf("") }
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = "home") {
                    composable("home") {
                        AdbConnectionScreen(
                            activeConnections = AdbConnectionManager.getAllConnections().toMutableMap(),
                            connectedDevices = connectedDevices,
                            currentIp = ipAddress,
                            onIpChange = { newIp -> ipAddress = newIp },
                            onNavigateToDetail = { ip -> navController.navigate("detail/$ip") },
                            onConnectSuccess = { connectionTrigger++ }
                        )
                    }

                    composable(
                        route = "detail/{ip}",
                        arguments = listOf(navArgument("ip") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val ip = backStackEntry.arguments?.getString("ip") ?: ""
                        val dadbInstance = AdbConnectionManager.getConnection(ip)

                        DeviceControlScreen(
                            ip = ip,
                            dadbConnection = dadbInstance,
                            onBack = { navController.popBackStack() },
                            onNavigate = { featureName ->
                                when (featureName) {
                                    "设备信息" -> navController.navigate("info/$ip")
                                    "shell命令" -> navController.navigate("shell/$ip")
                                    "App管理" -> navController.navigate("apps/$ip")
                                    "文件管理" -> navController.navigate("files/$ip")
                                    "进程管理" -> navController.navigate("process/$ip")
                                    "常用工具" -> navController.navigate("tools/$ip")
                                    "虚拟遥控" -> navController.navigate("remote/$ip")
                                    else -> Toast.makeText(this@MainActivity, "正在开发: $featureName", Toast.LENGTH_SHORT).show()
                                }
                            }
                        )
                    }

                    composable(route = "files/{ip}", arguments = listOf(navArgument("ip") { type = NavType.StringType })) {
                        val ip = it.arguments?.getString("ip") ?: ""
                        FileManagerScreen(ip = ip, initialConnection = AdbConnectionManager.getConnection(ip), onBack = { navController.popBackStack() })
                    }
                    composable(route = "info/{ip}", arguments = listOf(navArgument("ip") { type = NavType.StringType })) {
                        val ip = it.arguments?.getString("ip") ?: ""
                        DeviceInfoScreen(ip = ip, dadbConnection = AdbConnectionManager.getConnection(ip), onBack = { navController.popBackStack() })
                    }
                    composable(route = "shell/{ip}", arguments = listOf(navArgument("ip") { type = NavType.StringType })) {
                        val ip = it.arguments?.getString("ip") ?: ""
                        ShellCommandScreen(ip = ip, dadbConnection = AdbConnectionManager.getConnection(ip), onBack = { navController.popBackStack() })
                    }
                    composable(route = "apps/{ip}", arguments = listOf(navArgument("ip") { type = NavType.StringType })) {
                        val ip = it.arguments?.getString("ip") ?: ""
                        AppManagerScreen(ip = ip, initialConnection = AdbConnectionManager.getConnection(ip), onBack = { navController.popBackStack() })
                    }
                    composable(route = "process/{ip}", arguments = listOf(navArgument("ip") { type = NavType.StringType })) {
                        val ip = it.arguments?.getString("ip") ?: ""
                        ProcessManagerScreen(ip = ip, initialConnection = AdbConnectionManager.getConnection(ip), onBack = { navController.popBackStack() })
                    }
                    composable(route = "remote/{ip}", arguments = listOf(navArgument("ip") { type = NavType.StringType })) {
                        val ip = it.arguments?.getString("ip") ?: ""
                        RemoteControlScreen(ip = ip, initialConnection = AdbConnectionManager.getConnection(ip), onBack = { navController.popBackStack() })
                    }
                    composable(route = "tools/{ip}", arguments = listOf(navArgument("ip") { type = NavType.StringType })) {
                        val ip = it.arguments?.getString("ip") ?: ""
                        UsefulToolsScreen(
                            ip = ip,
                            initialConnection = AdbConnectionManager.getConnection(ip),
                            onBack = { navController.popBackStack() },
                            onOpenMirror = { navController.navigate("mirror/$ip") }
                        )
                    }
                    composable(route = "mirror/{ip}", arguments = listOf(navArgument("ip") { type = NavType.StringType })) {
                        val ip = it.arguments?.getString("ip") ?: ""
                        ScreenMirrorScreen(ip = ip, initialConnection = AdbConnectionManager.getConnection(ip), onBack = { navController.popBackStack() })
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdbConnectionScreen(
    activeConnections: Map<String, Adb>,
    connectedDevices: MutableList<DeviceInfo>,
    currentIp: String,
    onIpChange: (String) -> Unit,
    onConnectSuccess: () -> Unit,
    onNavigateToDetail: (String) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var isConnecting by remember { mutableStateOf(false) }
    var showResultDialog by remember { mutableStateOf(false) }
    var scannedIpList by remember { mutableStateOf(listOf<String>()) }
    var showDialog by remember { mutableStateOf(false) }

    // 无线配对状态逻辑
    var showPairDialog by remember { mutableStateOf(false) }
    var pairPort by remember { mutableStateOf("") }
    var pairCode by remember { mutableStateOf("") }
    var isPairing by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ADB Tools", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFFF8F9FA))
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                elevation = CardDefaults.cardElevation(0.dp),
                border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("添加新设备", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color.Gray)
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = currentIp,
                        onValueChange = onIpChange,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("IP 地址 (例如 192.168.2.x)") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        trailingIcon = {
                            IconButton(onClick = {
                                showDialog = true; showResultDialog = true
                                scope.launch {
                                    try { scannedIpList = LanScanner.scan(context) } finally { showDialog = false }
                                }
                            }) {
                                Icon(Icons.Default.Search, "扫描局域网", tint = Color(0xFF5B93E6))
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF5B93E6),
                            unfocusedBorderColor = Color(0xFFEEEEEE)
                        )
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // 按钮组：无线配对 + 立即连接
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick = { showPairDialog = true },
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, Color(0xFF5B93E6))
                        ) {
                            Text("无线配对", color = Color(0xFF5B93E6))
                        }

                        Button(
                            onClick = {
                                if (isConnecting) return@Button
                                isConnecting = true
                                scope.launch {
                                    val success = tryConnect(context, currentIp)
                                    isConnecting = false
                                    if (success) {
                                        onConnectSuccess()
                                        Toast.makeText(context, "连接成功", Toast.LENGTH_SHORT).show()
                                        if (connectedDevices.none { it.ipsum == currentIp }) {
                                            AdbConnectionManager.getConnection(currentIp)?.let {
                                                try { connectedDevices.add(fetchDeviceInfo(it, currentIp)) } catch (e: Exception) {}
                                            }
                                        }
                                    } else { Toast.makeText(context, "连接失败", Toast.LENGTH_SHORT).show() }
                                }
                            },
                            modifier = Modifier
                                .weight(1.2f)
                                .height(50.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5B93E6)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            if (isConnecting) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                            else Text("立即连接", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            if (connectedDevices.isNotEmpty()) {
                Text(
                    text = "已连接设备 (${connectedDevices.size})",
                    modifier = Modifier.padding(start = 24.dp, top = 8.dp, bottom = 8.dp),
                    fontSize = 14.sp, color = Color.Gray, fontWeight = FontWeight.Medium
                )
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(connectedDevices) { device ->
                        DeviceCardItem(deviceName = device.model, ip = device.ipsum, onClick = { onNavigateToDetail(device.ipsum) })
                    }
                }
            } else {
                Box(modifier = Modifier
                    .fillMaxSize()
                    .weight(1f), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.DevicesOther, null, tint = Color.LightGray, modifier = Modifier.size(64.dp))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("暂无连接", color = Color.Gray)
                    }
                }
            }
        }
    }

    // 无线配对弹窗
    if (showPairDialog) {
        Dialog(onDismissRequest = { if(!isPairing) showPairDialog = false }) {
            Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Column(modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth()) {
                    Text("无线配对", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text("请在开发者选项中点击“使用配对码配对”获取信息", fontSize = 12.sp, color = Color.Gray)

                    Spacer(Modifier.height(16.dp))
                    OutlinedTextField(value = currentIp, onValueChange = onIpChange, label = { Text("IP 地址") }, modifier = Modifier.fillMaxWidth())

                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = pairPort, onValueChange = { pairPort = it }, label = { Text("配对端口") }, modifier = Modifier.weight(1f))
                        OutlinedTextField(value = pairCode, onValueChange = { pairCode = it }, label = { Text("配对码") }, modifier = Modifier.weight(1f))
                    }

                    Spacer(Modifier.height(24.dp))
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        onClick = {
                            isPairing = true
                            scope.launch {
                                val port = pairPort.toIntOrNull() ?: 0
                               val   pair =withContext(Dispatchers.IO)  {
//
                                    val pair =
                                        Adb.pair(currentIp, port, pairCode)
                                    pair

                                }
                                if (pair) {
                                    Toast.makeText(context, "配对成功！请输入连接端口点击“立即连接”", Toast.LENGTH_LONG).show()
                                        showPairDialog = false
                                } else {
                                        Toast.makeText(context, "配对失败，请检查端口和码", Toast.LENGTH_SHORT).show()
                                    isPairing = false
                                }

                            }
                        }
                    ) {
                        if (isPairing) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                        else Text("开始配对")
                    }
                    TextButton(onClick = { showPairDialog = false }, Modifier.align(Alignment.CenterHorizontally)) { Text("取消") }
                }
            }
        }
    }

    if (showResultDialog) {
        Dialog(onDismissRequest = { showResultDialog = false }) {
            Card(modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                if (showDialog) {
                    Row(modifier = Modifier
                        .padding(30.dp)
                        .fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color(0xFF5B93E6), strokeWidth = 3.dp)
                        Spacer(modifier = Modifier.width(16.dp))
                        Text("正在扫描局域网...", fontSize = 16.sp, color = Color(0xFF333333))
                    }
                } else {
                    Column(modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Radar, null, tint = Color(0xFF5B93E6))
                            Spacer(Modifier.width(8.dp))
                            Text("扫描结果", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        if (scannedIpList.isEmpty()) {
                            Text("未发现开启 ADB 的设备", color = Color.Gray, modifier = Modifier.padding(vertical = 12.dp))
                        } else {
                            LazyColumn(modifier = Modifier.heightIn(max = 300.dp)) {
                                items(scannedIpList) { scannedIp ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                onIpChange(scannedIp); showResultDialog = false
                                            }
                                            .padding(vertical = 12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.Smartphone, null, tint = Color.Gray, modifier = Modifier.size(20.dp))
                                        Spacer(Modifier.width(12.dp))
                                        Text(scannedIp, fontSize = 16.sp)
                                        Spacer(Modifier.weight(1f))
                                        Icon(Icons.Default.Add, null, tint = Color(0xFF5B93E6))
                                    }
                                    HorizontalDivider(color = Color(0xFFF0F0F0))
                                }
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                        TextButton(onClick = { showResultDialog = false }, modifier = Modifier.align(Alignment.End)) { Text("关闭") }
                    }
                }
            }
        }
    }
}


// 辅助判断是否为 SSL 协议不匹配导致的错误
private fun isSslError(e: Throwable): Boolean {
    return e is javax.net.ssl.SSLHandshakeException ||
            e is java.io.EOFException ||
            (e.cause is java.io.EOFException) ||
            (e.message?.contains("connection closed") == true)
}
// 放在 MainActivity.kt 中，替换原有的 tryConnect 方法
// 放在 MainActivity.kt 中
suspend fun tryConnect(context: Context, ipStr: String): Boolean {
    return withContext(Dispatchers.IO) {
        val parts = ipStr.split(":")
        val ip = parts[0]
        val port = if (parts.size > 1) parts[1].toInt() else 5555

        Log.e("ADB_DEBUG", "➡️ 开始连接: $ip:$port")
        true

        try {
            // 1. 准备 TLS
            Log.d("ADB_DEBUG", "准备 TLS 环境...")

            // 2. 创建连接 (AdbImpl 会用这个 factory)
            val dadb = Adb.create(ip, port);

            // 3. 验证连接 (这一步能过才算真的连上了)
            val result = dadb.shell("echo connection_test").allOutput
            Log.e("ADB_DEBUG", "🎉 TLS 连接验证成功！设备响应:")

            // 4. 保存连接 (必须！)
            AdbConnectionManager.setConnection(ipStr, dadb)
            return@withContext true

        } catch (e: Exception) {
            e.printStackTrace()
        }
            // ❌ 重点：如果失败了，返回 false，不要返回 true！
            return@withContext false
        }
    }
@Composable
fun DeviceCardItem(deviceName: String, ip: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color(0xFFE3F2FD)), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Android, null, tint = Color(0xFF5B93E6), modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(deviceName, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF333333))
                Text(ip, color = Color.Gray, fontSize = 13.sp)
            }
            Icon(Icons.Default.ChevronRight, null, tint = Color.LightGray)
        }
    }
}
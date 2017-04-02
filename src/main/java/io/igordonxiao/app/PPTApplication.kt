package io.igordonxiao.app

import okhttp3.*
import okio.ByteString
import java.awt.GridLayout
import java.awt.Robot
import java.awt.event.KeyEvent
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JTextArea


/**
 * Created by igord on 2017/4/1.
 */

fun main(args: Array<String>) {
    val panel = JPanel().apply {
        layout = GridLayout()
    }
    val frame = JFrame("PPT Controller").apply {
        add(panel)
        defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        setSize(300, 300)
        setLocationRelativeTo(null)
        isVisible = true
    }

    val infoArea = JTextArea().apply {
        text = "开始准备连接服务器......\n"
    }
    panel.add(JScrollPane(infoArea))

    // Robot
    val robot = Robot()
    // websocket 连接
    val client = OkHttpClient.Builder()
            .readTimeout(3000, TimeUnit.SECONDS)
            .writeTimeout(3000, TimeUnit.SECONDS)
            .connectTimeout(3000, TimeUnit.SECONDS)
            .build()
    val url = "ws://pptcontroller.herokuapp.com/ws"
    val req = Request.Builder().url(url).build()
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

    client.newWebSocket(req, object : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: Response) {
            webSocket.send("client: subscription")
        }

        override fun onMessage(webSocket: WebSocket, text: String) = when (text) {
            "cmd:up" -> {
                infoArea.append("\n${dateFormat.format(Date())} 上一页")
                robot.keyPress(KeyEvent.VK_UP)
            }
            "cmd:down" -> {
                infoArea.append("\n${dateFormat.format(Date())} 下一页")
                robot.keyPress(KeyEvent.VK_DOWN)
            }
            else -> {
            }
        }

        override fun onMessage(webSocket: WebSocket, bytes: ByteString) = infoArea.append("\n-收到: $bytes")
        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) = infoArea.append("\n连接关闭")
        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) = infoArea.append("\n连接关闭：$reason")
        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response)= infoArea.append("\n连接失败，请重试")
    })
}
package com.candyworld.sugarlady.gameObjects

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Message
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.PermissionRequest
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.candyworld.sugarlady.databinding.ActivityWvsugarLadyBinding
import com.onesignal.OneSignal
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding


class WVSugarLady : AppCompatActivity() {
    private val binding: ActivityWvsugarLadyBinding by lazy {
        ActivityWvsugarLadyBinding.inflate(layoutInflater)
    }
    var fileChooserCallback: ValueCallback<Array<Uri>>? = null
    private val sharedPreferences by lazy {
        getSharedPreferences("sugar", MODE_PRIVATE)
    }
    private var mFullScreenView: View? = null
    private var mFullscreenViewCallback: WebChromeClient.CustomViewCallback? = null
    private lateinit var pair: Pair<WCC, PermissionRequest>
    private val permissionRequestLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) pair.first.onPermissionRequest(pair.second)
        }
    private var views = mutableListOf<WebView>()

    val activityForResultListener =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                result.data?.let { intent ->
                    fileChooserCallback?.let {
                        intent.data?.let {
                            val dataUris: Array<Uri>? =
                                try {
                                    arrayOf(Uri.parse(intent.dataString))
                                } catch (e: Exception) {
                                    null
                                }
                            fileChooserCallback!!.onReceiveValue(dataUris)
                            fileChooserCallback = null
                        }
                    }
                }
            } else {
                if (fileChooserCallback != null) {
                    fileChooserCallback!!.onReceiveValue(null)
                    fileChooserCallback = null
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        enableEdgeToEdge()
        OneSignal.promptForPushNotifications(true)
        binding.wSugarView.init(WCC(), WVC())
        if (sharedPreferences.getString("statistic", "") != ""){
            binding.wSugarView.loadUrl(sharedPreferences.getString("statistic", "").toString())
        } else if (intent.getStringExtra("statistic") != null || intent.getStringExtra("statistic") != ""){
            sharedPreferences.edit().putString("statistic",intent.getStringExtra("statistic")).apply()
            binding.wSugarView.loadUrl(intent.getStringExtra("statistic").toString())
        }
        binding.wSugarView.isVisible = true

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { view, insets ->
            val imeVisible = insets.isVisible(WindowInsetsCompat.Type.ime())
            val imeHeight = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom

            // Настройка отступов для вашего контента
            view.updatePadding(bottom = if (imeVisible) imeHeight else 0)

            // Возвращаем insets с примененными изменениями
            WindowInsetsCompat.CONSUMED
        }
    }

    inner class WCC : WebChromeClient() {

        override fun onHideCustomView() {
            binding.apply {
                fullscreenContainer.removeView(mFullScreenView)
                mFullscreenViewCallback?.onCustomViewHidden()
                mFullScreenView = null
                wSugarView.visibility = View.VISIBLE
                fullscreenContainer.visibility = View.GONE
            }
        }

        override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
            binding.apply {
                wSugarView.visibility = View.GONE
                fullscreenContainer.visibility = View.VISIBLE
                fullscreenContainer.addView(view)
                mFullScreenView = view
                mFullscreenViewCallback = callback
            }
        }

        private var isItStart = true
        private var startTime = 0L

        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            if (isItStart) {
                startTime = System.currentTimeMillis()
                isItStart = false
            } else {
                if (System.currentTimeMillis() - startTime > 500) {
                    binding.progressBar.isVisible = true
                    view?.isVisible = false
                }
            }
            if (newProgress == 100) {
                binding.progressBar.isVisible = false
                view?.isVisible = true
                isItStart = true
            }
            super.onProgressChanged(view, newProgress)
        }

        override fun onCreateWindow(
            view: WebView,
            isDialog: Boolean,
            isUserGesture: Boolean,
            resultMsg: Message?
        ): Boolean {
            val newWebView = WebView(this@WVSugarLady)
            views.last().isVisible = false
            newWebView.init(WCC(), WVC())
            binding.root.addView(newWebView)
            val transport = resultMsg!!.obj as WebView.WebViewTransport
            transport.webView = newWebView
            resultMsg.sendToTarget()
            return true

        }

        override fun onCloseWindow(window: WebView?) {
            super.onCloseWindow(window)
            val newWebView = if (views.isNotEmpty()) views.removeLast() else null
            if (newWebView != null && newWebView.isVisible) {
                views.last().isVisible = true
                newWebView.isVisible = false
                binding.root.removeView(newWebView)
                newWebView.destroy()
            }
        }

        override fun onShowFileChooser(
            webView: WebView?,
            filePathCallback: ValueCallback<Array<Uri>>?,
            fileChooserParams: FileChooserParams?
        ): Boolean {
            fileChooserCallback?.let { fileChooserCallback!!.onReceiveValue(null) }
            fileChooserCallback = filePathCallback
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "*/*"
            activityForResultListener.launch(Intent.createChooser(intent, ""))
            return true
        }

        override fun onPermissionRequest(request: PermissionRequest) {
            if (ContextCompat.checkSelfPermission(
                    this@WVSugarLady,
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                pair = Pair(this, request)
                permissionRequestLauncher.launch(Manifest.permission.CAMERA)
            } else request.grant(request.resources)
        }
    }

    inner class WVC : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            view.loadUrl(url)
            return uri(url, view)
        }

        override fun shouldOverrideUrlLoading(
            view: WebView,
            request: WebResourceRequest
        ): Boolean {
            return uri(request.url.toString(), view)
        }

        private fun uri(url: String, view: WebView): Boolean {
            if (url.startsWith("https://m.facebook.com/oauth/error")) {
                return true
            }
            return if (url.startsWith("http://") || url.startsWith("https://")) false else try {
                view.context.startActivity(Intent(Intent.ACTION_VIEW, url.toUri()))
                true

            } catch (e: java.lang.Exception) {
                if (url.startsWith("line://")) {
                    view.context.startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            "market://details?id=jp.naver.line.android".toUri()
                        )
                    )
                }
                Log.i("TAG", "shouldOverrideUrlLoading Exception:$e")
                true
            }
        }

        override fun shouldInterceptRequest(
            view: WebView?,
            request: WebResourceRequest?
        ): WebResourceResponse? {
            request?.url?.let {
                if ("http://localhost".toRegex().containsMatchIn(it.toString())) {
                }
            }
            return super.shouldInterceptRequest(view, request)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (binding.wSugarView.isVisible) {
            binding.wSugarView.saveState(outState)
        }
    }

    override fun onBackPressed() {
        if (views.size > 1) {
            val lastVW = views.last()
            if (lastVW.canGoBack()) {
                lastVW.goBack()
            } else {
                lastVW.isVisible = false
                binding.root.removeView(lastVW)
                lastVW.destroy()
                views.removeAt(views.lastIndex)
                views.last().isVisible = true
            }
        } else if (views.size == 1) {
            if (views.last().canGoBack()) {
                views.last().goBack()
            } else {
                super.onBackPressed()
            }
        } else {
            super.onBackPressed()
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        if (binding.wSugarView.isVisible) {
            binding.wSugarView.restoreState(savedInstanceState)
        }
    }

    override fun onResume() {
        super.onResume()
        if (binding.wSugarView.isVisible) {
            CookieManager.getInstance().flush()
            binding.wSugarView.onResume()
        }
    }

    override fun onPause() {
        super.onPause()
        if (binding.wSugarView.isVisible) {
            CookieManager.getInstance().flush()
            binding.wSugarView.onPause()
        }
    }

    @SuppressLint("ClickableViewAccessibility", "SetJavaScriptEnabled")
    private fun WebView.init(wcc: WCC, wvc: WVC) {
        this.apply {

            isSaveEnabled = true
            isFocusableInTouchMode = true
            isFocusable = true

            webChromeClient = wcc
            CookieManager.getInstance().setAcceptThirdPartyCookies(this, true)
            CookieManager.getInstance().setAcceptCookie(true)
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setOnTouchListener { _, _ ->
                CookieManager.getInstance().flush()
                false
            }

            setDownloadListener { url, userAgent, contentDescription, mimetype, _ ->
                val i = Intent(Intent.ACTION_VIEW)
                i.data = url.toUri()
                context.startActivity(i)
            }


            webViewClient = wvc
            settings.apply {
                setRenderPriority(WebSettings.RenderPriority.HIGH)
                allowContentAccess = true
                mediaPlaybackRequiresUserGesture = false
                setSupportMultipleWindows(true)
                pluginState = WebSettings.PluginState.ON
                cacheMode = WebSettings.LOAD_DEFAULT
                loadsImagesAutomatically = true
                mixedContentMode = 0
                setEnableSmoothTransition(true)
                databaseEnabled = true
                savePassword = true
                domStorageEnabled = true
                allowUniversalAccessFromFileURLs = true
                saveFormData = true
                userAgentString = userAgentString.replace("; wv", "")
                allowFileAccess = true
                javaScriptEnabled = true
                javaScriptCanOpenWindowsAutomatically = true
                allowFileAccessFromFileURLs = true
            }
        }

        views.add(this)
    }
}
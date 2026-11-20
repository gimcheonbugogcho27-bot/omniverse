package com.omniverse.x;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.webkit.CookieManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

/**
 * OMNIVERSE X 안드로이드 앱.
 * Render에 배포된 사이트를 WebView로 띄운다. 로그인 세션(쿠키)은 앱에 저장되어 유지된다.
 */
public class MainActivity extends Activity {

    private static final int FILE_CHOOSER = 1001;
    private WebView web;
    private ValueCallback<Uri[]> fileCallback;
    private final String home = BuildConfig.SITE_URL;
    private final String homeHost = Uri.parse(BuildConfig.SITE_URL).getHost();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window w = getWindow();
        w.setStatusBarColor(Color.parseColor("#060818"));
        w.setNavigationBarColor(Color.parseColor("#060818"));

        web = new WebView(this);
        web.setBackgroundColor(Color.parseColor("#050714"));
        web.setOverScrollMode(View.OVER_SCROLL_NEVER);
        setContentView(web);

        WebSettings s = web.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setDatabaseEnabled(true);
        s.setMediaPlaybackRequiresUserGesture(false);
        s.setLoadWithOverviewMode(true);
        s.setUseWideViewPort(true);
        s.setSupportZoom(false);
        s.setAllowFileAccess(false);
        s.setUserAgentString(s.getUserAgentString() + " OmniverseXApp/" + BuildConfig.VERSION_NAME);

        CookieManager cookies = CookieManager.getInstance();
        cookies.setAcceptCookie(true);
        cookies.setAcceptThirdPartyCookies(web, false);

        web.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest req) {
                Uri uri = req.getUrl();
                String host = uri.getHost();
                // OMNIVERSE X 안의 주소는 앱에서, 다른 사이트는 휴대폰 브라우저로 연다
                if (host != null && host.equals(homeHost)) return false;
                openExternal(uri);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                CookieManager.getInstance().flush();
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest req, WebResourceError err) {
                if (req.isForMainFrame()) showOffline();
            }
        });

        web.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onShowFileChooser(WebView view, ValueCallback<Uri[]> callback, FileChooserParams params) {
                if (fileCallback != null) fileCallback.onReceiveValue(null);
                fileCallback = callback;
                try {
                    startActivityForResult(params.createIntent(), FILE_CHOOSER);
                } catch (ActivityNotFoundException e) {
                    fileCallback = null;
                    toast("파일을 고를 앱이 없습니다");
                    return false;
                }
                return true;
            }
        });

        web.setDownloadListener((url, userAgent, contentDisposition, mimetype, length) -> {
            if (url.startsWith("blob:") || url.startsWith("data:")) {
                toast("앱에서는 파일 내보내기를 지원하지 않습니다. 브라우저에서 이용해 주세요");
            } else {
                openExternal(Uri.parse(url));
            }
        });

        if (savedInstanceState != null) {
            web.restoreState(savedInstanceState);
        } else if (isOnline()) {
            web.loadUrl(home);
        } else {
            showOffline();
        }
    }

    private void showOffline() {
        String html = "<html><body style='margin:0;background:#050714;color:#e8eeff;font-family:sans-serif;"
                + "display:flex;flex-direction:column;align-items:center;justify-content:center;height:100vh;text-align:center'>"
                + "<div style='font-size:56px'>🛰️</div><h2>우주와 연결이 끊겼어요</h2>"
                + "<p style='color:#94a3c8'>인터넷 연결을 확인한 뒤 다시 시도하세요.<br>서버가 잠들어 있으면 첫 접속에 1분 정도 걸릴 수 있어요.</p>"
                + "<a href='" + home + "' style='margin-top:12px;padding:12px 26px;border-radius:999px;"
                + "background:#7c5cff;color:#fff;text-decoration:none;font-weight:700'>다시 시도</a></body></html>";
        web.loadDataWithBaseURL(home, html, "text/html", "utf-8", null);
    }

    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        if (cm == null) return true;
        NetworkCapabilities caps = cm.getNetworkCapabilities(cm.getActiveNetwork());
        return caps != null && caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
    }

    private void openExternal(Uri uri) {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, uri));
        } catch (ActivityNotFoundException e) {
            toast("열 수 있는 앱이 없습니다");
        }
    }

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FILE_CHOOSER && fileCallback != null) {
            fileCallback.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, data));
            fileCallback = null;
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        if (web.canGoBack()) web.goBack();
        else super.onBackPressed();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        web.saveState(outState);
    }

    @Override
    protected void onPause() {
        super.onPause();
        CookieManager.getInstance().flush();
        web.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        web.onResume();
    }

    @Override
    protected void onDestroy() {
        web.destroy();
        super.onDestroy();
    }
}

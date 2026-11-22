package com.amirhossein.finance;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
public final class MainActivity extends Activity {
 private static final String HOME="https://amirhmh1000-mh.github.io/warm-ivory-finance/";
 private static final String HOST="amirhmh1000-mh.github.io";
 private static final String MOBILE_CSS="(function(){if(document.getElementById('amir-mobile-app'))return;var st=document.createElement('style');st.id='amir-mobile-app';st.textContent='@media(max-width:700px){html,body{max-width:100%;overflow-x:hidden}main{width:100%!important;margin-right:0!important;padding:12px!important}.grid,.panels{grid-template-columns:1fr!important}.top{flex-wrap:wrap!important}.top h1{font-size:19px!important}.card,.panel{padding:14px!important}.month-options{max-width:100%;overflow-x:auto}.aw-account-panels,.aw-bottom-panels{grid-template-columns:1fr!important}#awTableGrid .aw-scroll,#txList .scroll{max-width:100%;overflow-x:auto!important}#awTableGrid .aw-scroll table{min-width:520px!important}aside{position:relative!important;width:100%!important;max-width:100vw!important}.cloud-auth{padding:12px!important}}';document.head.appendChild(st)})()";
 private WebView web; private FrameLayout root; private LinearLayout error; private ProgressBar progress;
 private android.webkit.ValueCallback<Uri[]> fileCallback; private static final int FILE_PICKER=52;
 @Override public void onCreate(Bundle saved){
  super.onCreate(saved);getWindow().setStatusBarColor(Color.rgb(23,45,64));getWindow().setNavigationBarColor(Color.rgb(247,243,237));
  root=new FrameLayout(this);root.setBackgroundColor(Color.rgb(247,243,237));web=new WebView(this);
  web.setBackgroundColor(Color.rgb(247,243,237));web.setOverScrollMode(View.OVER_SCROLL_NEVER);
  WebSettings st=web.getSettings();st.setJavaScriptEnabled(true);st.setDomStorageEnabled(true);st.setDatabaseEnabled(true);
  st.setAllowFileAccess(false);st.setAllowContentAccess(true);st.setAllowFileAccessFromFileURLs(false);st.setAllowUniversalAccessFromFileURLs(false);
  st.setMixedContentMode(WebSettings.MIXED_CONTENT_NEVER_ALLOW);st.setJavaScriptCanOpenWindowsAutomatically(false);st.setSupportMultipleWindows(false);
  st.setUseWideViewPort(true);st.setLoadWithOverviewMode(true);st.setBuiltInZoomControls(false);
  CookieManager.getInstance().setAcceptCookie(true);CookieManager.getInstance().setAcceptThirdPartyCookies(web,false);
  WebView.setWebContentsDebuggingEnabled(false);
  root.addView(web,new FrameLayout.LayoutParams(-1,-1));
  progress=new ProgressBar(this,null,android.R.attr.progressBarStyleHorizontal);progress.setMax(100);root.addView(progress,new FrameLayout.LayoutParams(-1,4,Gravity.TOP));
  createError();
  web.setWebChromeClient(new WebChromeClient(){
   @Override public void onProgressChanged(WebView w,int value){progress.setProgress(value);progress.setVisibility(value==100?View.GONE:View.VISIBLE);}
   @Override public boolean onShowFileChooser(WebView w,android.webkit.ValueCallback<Uri[]> cb,FileChooserParams params){
    if(fileCallback!=null)fileCallback.onReceiveValue(null);fileCallback=cb;
    Intent i=new Intent(Intent.ACTION_OPEN_DOCUMENT);i.addCategory(Intent.CATEGORY_OPENABLE);i.setType("*/*");i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
    try{startActivityForResult(i,FILE_PICKER);return true;}catch(Exception e){fileCallback=null;return false;}
   }
  });
  web.setWebViewClient(new WebViewClient(){
   @Override public boolean shouldOverrideUrlLoading(WebView w,WebResourceRequest request){
    Uri url=request.getUrl();
    if("https".equalsIgnoreCase(url.getScheme())&&HOST.equalsIgnoreCase(url.getHost()))return false;
    if(!request.isForMainFrame())return false;
    try{startActivity(new Intent(Intent.ACTION_VIEW,url));}catch(Exception e){Toast.makeText(MainActivity.this,"پیوند قابل باز شدن نیست",Toast.LENGTH_SHORT).show();}
    return true;
   }
   @Override public void onPageFinished(WebView w,String url){if(trusted(url)){w.evaluateJavascript(MOBILE_CSS,null);error.setVisibility(View.GONE);}}
   @Override public void onReceivedError(WebView w,WebResourceRequest req,WebResourceError e){if(req.isForMainFrame())error.setVisibility(View.VISIBLE);}
   @Override public void onReceivedSslError(WebView w,SslErrorHandler handler,SslError e){handler.cancel();error.setVisibility(View.VISIBLE);}
  });
  web.setDownloadListener((url,agent,disposition,type,length)->{
   if(!url.startsWith("https://")){Toast.makeText(this,"برای پشتیبان‌گیری از مرورگر استفاده کن",Toast.LENGTH_LONG).show();return;}
   try{DownloadManager.Request q=new DownloadManager.Request(Uri.parse(url));q.setMimeType(type);q.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
    ((DownloadManager)getSystemService(DOWNLOAD_SERVICE)).enqueue(q);}catch(Exception e){Toast.makeText(this,"دریافت فایل ممکن نشد",Toast.LENGTH_SHORT).show();}
  });
  setContentView(root);if(saved==null)web.loadUrl(HOME);else web.restoreState(saved);
 }
 private boolean trusted(String u){try{Uri x=Uri.parse(u);return "https".equalsIgnoreCase(x.getScheme())&&HOST.equalsIgnoreCase(x.getHost());}catch(Exception e){return false;}}
 private void createError(){
  error=new LinearLayout(this);error.setOrientation(LinearLayout.VERTICAL);error.setGravity(Gravity.CENTER);error.setPadding(30,30,30,30);error.setBackgroundColor(Color.rgb(247,243,237));
  TextView title=new TextView(this);title.setText("ارتباط با سامانه برقرار نشد");title.setTextSize(20);title.setTextColor(Color.rgb(23,45,64));title.setGravity(Gravity.CENTER);error.addView(title);
  TextView info=new TextView(this);info.setText("برای ورود و همگام‌سازی اطلاعات به اینترنت نیاز است.");info.setPadding(0,24,0,24);info.setGravity(Gravity.CENTER);error.addView(info);
  Button retry=new Button(this);retry.setAllCaps(false);retry.setText("تلاش دوباره");retry.setTextColor(Color.WHITE);
  GradientDrawable bg=new GradientDrawable();bg.setColor(Color.rgb(23,45,64));bg.setCornerRadius(24);retry.setBackground(bg);
  retry.setOnClickListener(v->{error.setVisibility(View.GONE);web.loadUrl(HOME);});error.addView(retry);
  root.addView(error,new FrameLayout.LayoutParams(-1,-1));error.setVisibility(View.GONE);
 }
 @Override protected void onActivityResult(int request,int result,Intent data){super.onActivityResult(request,result,data);
  if(request==FILE_PICKER&&fileCallback!=null){Uri[] resultUris=(result==RESULT_OK&&data!=null&&data.getData()!=null)?new Uri[]{data.getData()}:null;fileCallback.onReceiveValue(resultUris);fileCallback=null;}}
 @Override protected void onSaveInstanceState(Bundle out){web.saveState(out);super.onSaveInstanceState(out);}
 @Override public void onBackPressed(){if(error.getVisibility()==View.VISIBLE){error.setVisibility(View.GONE);web.loadUrl(HOME);}else if(web.canGoBack())web.goBack();else super.onBackPressed();}
 @Override protected void onDestroy(){if(web!=null)web.destroy();super.onDestroy();}
}

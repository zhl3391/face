package com.zhl.face.helper;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.Toast;

import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXEmojiObject;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.umeng.analytics.MobclickAgent;
import com.zhl.face.R;
import com.zhl.face.app.AppConfig;
import com.zhl.face.app.FaceApp;
import com.zhl.face.model.EnumSendWay;
import com.zhl.face.model.FaceModel;
import com.zhl.face.utils.FileUtils;
import com.zhl.face.utils.Utils;

import java.io.File;

public class SendHelper {

    private static final String APP_ID_WEIXIN = "wx4e5853627a839f08";

    private static SendHelper instance = null;

    private IWXAPI mIwxapi;

    private SendHelper(){
        mIwxapi = WXAPIFactory.createWXAPI(FaceApp.getContext(), APP_ID_WEIXIN, true);
        mIwxapi.registerApp(APP_ID_WEIXIN);
    }

    public static SendHelper getInstance(){
        if (instance == null){
            instance = new SendHelper();
        }
        return instance;
    }


    /**
     * 发送表情
     * @param faceModel
     * @param activity
     * @param sendWay
     */
    public void sendFace(FaceModel faceModel ,Activity activity,EnumSendWay sendWay){
        if (faceModel != null) {
            switch (sendWay) {
                case WeiXin:
                    if (TextUtils.isEmpty(faceModel.thumbPath)) {
                        String thumbPath = FileUtils.TEMP_DIR + Utils.createFaceName() + "thumb";
                        faceModel.thumbPath = thumbPath;
                        FileUtils.getInstance().createThumb(faceModel.expressionPath, thumbPath);
                    }
                    System.out.println(faceModel.thumbPath);
                    System.out.println(faceModel.expressionPath);
                    sendToWeixin(faceModel.expressionPath,
                            faceModel.thumbPath,activity);
                    break;
                case Qq:
                    sendToQq(faceModel.expressionPath, activity);
                    break;
                default:
                    break;
            }
        }else {
            Toast.makeText(activity, activity.getString(R.string.no_face),
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 发送到微信
     * @param imgPath
     * @param thumbPath 缩略图地址
     * @param activity
     */
    public void sendToWeixin(String imgPath,String thumbPath,Activity activity){
        if (Utils.isApkExist(activity, AppConfig.PACKAGE_WEIXIN)) {
            if (imgPath != null&&thumbPath != null) {
                MobclickAgent.onEvent(activity, AppConfig.EVENT_SEND_WEIXIN);
                WXEmojiObject emoji = new WXEmojiObject();
//                emoji.emojiData = FileUtils.readFromFile(thumbPath, 0, (int) new File(thumbPath).length());
                emoji.emojiPath = imgPath;
                WXMediaMessage msg = new WXMediaMessage(emoji);
                msg.title = "Emoji Title";
                msg.description = "Emoji Description";
//                if (new File(imgPath).length() > new File(thumbPath).length()){
//                }else {
//                    msg.thumbData = FileUtils.readFromFile(imgPath, 0, (int) new File(imgPath).length());
//                }
                msg.thumbData = FileUtils.readFromFile(thumbPath, 0, (int) new File(thumbPath).length());
                SendMessageToWX.Req req = new SendMessageToWX.Req();
                req.transaction = buildTransaction("emoji");
                req.message = msg;
                req.scene = SendMessageToWX.Req.WXSceneSession;
                mIwxapi.sendReq(req);
            }else {
                Toast.makeText(activity, "文件不存在,发送失败!", Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(activity, activity.getString(R.string.no_weixin),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }

    /**
     * 发送到qq
     * @param imgPath
     * @param activity
     */
    public void sendToQq(String imgPath,Activity activity){
        if (Utils.isApkExist(activity, AppConfig.PACKAGE_QQ)
                &&imgPath != null) {
            MobclickAgent.onEvent(activity, AppConfig.EVENT_SEND_QQ);
            Intent intent = new Intent(android.content.Intent.ACTION_SEND);
            intent.setType("image/*");
            intent.setClassName(AppConfig.PACKAGE_QQ, "com.tencent.mobileqq.activity.JumpActivity");
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(imgPath)));
            activity.startActivity(intent);
        }else {
            Toast.makeText(activity, activity.getString(R.string.no_qq),
                    Toast.LENGTH_SHORT).show();
        }
    }
}

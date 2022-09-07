package com.android_t.egg;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.dede.android_eggs.EasterEggsServer;
import com.dede.basic.UtilExt;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import fi.iki.elonen.NanoHTTPD;

/**
 * Transform Emoji Image
 *
 * @author shhu
 * @since 2022/9/6
 */
//@Ignore("Transform Emoji Image Only")// remove this line
@RunWith(AndroidJUnit4.class)
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
public class TransformEmojiImageUtil {

    private static final String ZIP_NAME = "emojis.zip";
    private static final String EMOJI_IMAGE_NAME_FORMAT = "t_emoji_%s.webp";
    private static final Bitmap.CompressFormat COMPRESS_FORMAT = Bitmap.CompressFormat.WEBP_LOSSY;
    private static final int IMAGE_SIZE = 512;
    private static final float EMOJI_SIZE = IMAGE_SIZE * 0.85f;
    private static final int IMAGE_QUALITY = 75;

    private static final boolean ENABLE_SIMPLE = false;

    private String[][] EMOJI_SETS;

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint.FontMetrics fontMetrics;
    private File outputDir;
    private Context context;

    @Before
    public void setup() throws IllegalAccessException, NoSuchFieldException {
        Field field = PlatLogoActivity.class.getDeclaredField("EMOJI_SETS");
        field.setAccessible(true);
        EMOJI_SETS = (String[][]) field.get(null);

        paint.setTextSize(EMOJI_SIZE);
        paint.setTextAlign(Paint.Align.CENTER);
        fontMetrics = paint.getFontMetrics();

        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        outputDir = new File(context.getCacheDir(), "emoji_images");
        outputDir.mkdirs();
    }

    @Test
    public void transform() throws IOException {
        float x = IMAGE_SIZE / 2f;
        float y = IMAGE_SIZE / 2f + (-fontMetrics.ascent - (fontMetrics.descent - fontMetrics.ascent) / 2f);
        Canvas canvas = new Canvas();

        final File zipFile = new File(outputDir, ZIP_NAME);
        zipFile.createNewFile();
        ZipOutputStream zipOutput = new ZipOutputStream(new FileOutputStream(zipFile));
        HashSet<String> emojiSet = new HashSet<>();
        for (int i = 0; i < EMOJI_SETS.length; i++) {
            String[] emojis = EMOJI_SETS[i];
            for (int j = 0; j < emojis.length; j++) {
                String emoji = emojis[j];
                if (emojiSet.contains(emoji)) {
                    continue;// 🤩 repeated
                }
                emojiSet.add(emoji);
                Bitmap bitmap = Bitmap.createBitmap(IMAGE_SIZE, IMAGE_SIZE, Bitmap.Config.ARGB_8888);
                canvas.setBitmap(bitmap);
                canvas.drawText(emoji, x, y, paint);
                String fileName = String.format(EMOJI_IMAGE_NAME_FORMAT, UtilExt.unicode(emoji));
                ByteArrayOutputStream byreArray = new ByteArrayOutputStream();
                bitmap.compress(COMPRESS_FORMAT, IMAGE_QUALITY, byreArray);
                bitmap.recycle();
                ZipEntry zipEntry = new ZipEntry("emojis/" + fileName);
                zipOutput.putNextEntry(zipEntry);
                zipOutput.write(byreArray.toByteArray());

                if (ENABLE_SIMPLE && i == 0 && j == 0) {
                    File sampleFile = new File(outputDir, fileName);
                    sampleFile.createNewFile();
                    FileOutputStream sampleOutput = new FileOutputStream(sampleFile);
                    sampleOutput.write(byreArray.toByteArray());
                    sampleOutput.close();
                }
            }
        }
        zipOutput.close();

        // launch http server and wait download emoji.zip request
        EasterEggsServer.WaitFinishLock lock = new EasterEggsServer.WaitFinishLock(15 * 1000L);
        EasterEggsServer server = new EasterEggsServer(context);
        server.registerHandler("/" + ZIP_NAME, new EasterEggsServer.Handler() {
            @Override
            public NanoHTTPD.Response onHandler(@NonNull NanoHTTPD.IHTTPSession session) throws IOException {
                return NanoHTTPD.newFixedLengthResponse(
                        NanoHTTPD.Response.Status.OK,
                        "application/zip",
                        new FileInputStream(zipFile),
                        zipFile.length()
                );
            }

            @Override
            public void onFinish() {
                lock.unlock();
            }
        });
        server.start();

        lock.await();
        server.stop();
    }

}

/*
 * Copyright (C) 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android_s.egg.neko;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android_s.egg.R;
import com.android_s.egg.neko.PrefState.PrefsListener;
import com.dede.basic.ContextExt;
import com.dede.basic.utils.NekoLandEdgeInsetsUtil;
import com.dede.basic.utils.ShareCatUtils;
//import com.android.internal.logging.MetricsLogger;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@RequiresApi(Build.VERSION_CODES.R)
public class NekoLand extends Activity implements PrefsListener {
    public static String CHAN_ID = "S_EGG";

    public static boolean DEBUG = false;
    public static boolean DEBUG_NOTIFICATIONS = false;

    private static final int EXPORT_BITMAP_SIZE = 600;

    private static final int STORAGE_PERM_REQUEST = 123;

    private static final boolean CAT_GEN = false;
    private PrefState mPrefs;
    private CatAdapter mAdapter;
    private Cat mPendingShareCat;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.s_neko_activity);
        final ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setLogo(Cat.create(this));
            actionBar.setDisplayUseLogoEnabled(false);
            actionBar.setDisplayShowHomeEnabled(true);
        }
        NekoLandEdgeInsetsUtil.applyWindowInsets(this);

        mPrefs = new PrefState(this);
        mPrefs.setListener(this);
        final RecyclerView recyclerView = findViewById(R.id.holder);
        mAdapter = new CatAdapter();
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        int numCats = updateCats();
        //MetricsLogger.histogram(this, "egg_neko_visit_gallery", numCats);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPrefs.setListener(null);
    }

    private int updateCats() {
        Cat[] cats;
        if (CAT_GEN) {
            cats = new Cat[50];
            for (int i = 0; i < cats.length; i++) {
                cats[i] = Cat.create(this);
            }
        } else {
            final float[] hsv = new float[3];
            List<Cat> list = mPrefs.getCats();
            Collections.sort(list, new Comparator<Cat>() {
                @Override
                public int compare(Cat cat, Cat cat2) {
                    Color.colorToHSV(cat.getBodyColor(), hsv);
                    float bodyH1 = hsv[0];
                    Color.colorToHSV(cat2.getBodyColor(), hsv);
                    float bodyH2 = hsv[0];
                    return Float.compare(bodyH1, bodyH2);
                }
            });
            cats = list.toArray(new Cat[0]);
        }
        mAdapter.setCats(cats);
        return cats.length;
    }

    private void onCatClick(Cat cat) {
        if (CAT_GEN) {
            mPrefs.addCat(cat);
            new AlertDialog.Builder(NekoLand.this)
                    .setTitle("Cat added")
                    .setPositiveButton(android.R.string.ok, null)
                    .show();
        } else {
            showNameDialog(cat);
        }
    }

    private void onCatRemove(Cat cat) {
        cat.logRemove(this);
        mPrefs.removeCat(cat);
    }

    private void showNameDialog(final Cat cat) {
        final Context context = new ContextThemeWrapper(this,
                android.R.style.Theme_Material_Light_Dialog_NoActionBar);
        // TODO: Move to XML, add correct margins.
        View view = LayoutInflater.from(context).inflate(R.layout.s_edit_text, null);
        final EditText text = (EditText) view.findViewById(android.R.id.edit);
        text.setText(cat.getName());
        text.setSelection(cat.getName().length());
        final int size = context.getResources()
                .getDimensionPixelSize(android.R.dimen.app_icon_size);
        Drawable catIcon = cat.createIcon(this, size, size).loadDrawable(this);
        new AlertDialog.Builder(context)
                .setTitle(" ")
                .setIcon(catIcon)
                .setView(view)
                .setPositiveButton(android.R.string.ok, new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = text.getText().toString().trim();
                        if (TextUtils.isEmpty(name)) {
                            // make cat.getName() notnull, ShortcutInfo.Builder#setShortLabel(getName())
                            return;
                        }
                        cat.logRename(context);
                        cat.setName(name);
                        mPrefs.addCat(cat);
                    }
                }).show();
    }

    @Override
    public void onPrefsChanged() {
        updateCats();
    }

    private class CatAdapter extends RecyclerView.Adapter<CatHolder> {

        private Cat[] mCats;

        public void setCats(Cat[] cats) {
            mCats = cats;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public CatHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new CatHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.s_cat_view, parent, false));
        }

        private void setContextGroupVisible(final CatHolder holder, boolean vis) {
            final View group = holder.contextGroup;
            if (vis && group.getVisibility() != View.VISIBLE) {
                group.setAlpha(0);
                group.setVisibility(View.VISIBLE);
                group.animate().alpha(1.0f).setDuration(333);
                Runnable hideAction = new Runnable() {
                    @Override
                    public void run() {
                        setContextGroupVisible(holder, false);
                    }
                };
                group.setTag(hideAction);
                group.postDelayed(hideAction, 5000);
            } else if (!vis && group.getVisibility() == View.VISIBLE) {
                group.removeCallbacks((Runnable) group.getTag());
                group.animate().alpha(0f).setDuration(250).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        group.setVisibility(View.INVISIBLE);
                    }
                });
            }
        }

        @Override
        public void onBindViewHolder(final CatHolder holder, int position) {
            Context context = holder.itemView.getContext();
            final int size = context.getResources().getDimensionPixelSize(R.dimen.s_neko_display_size);
            holder.imageView.setImageIcon(mCats[position].createIcon(context, size, size));
            holder.textView.setText(mCats[position].getName());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onCatClick(mCats[holder.getBindingAdapterPosition()]);
                }
            });
            holder.itemView.setOnLongClickListener(new OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    setContextGroupVisible(holder, true);
                    return true;
                }
            });
            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.setClickable(false);
                    setContextGroupVisible(holder, false);
                    new AlertDialog.Builder(NekoLand.this)
                            .setTitle(getString(R.string.s_confirm_delete, mCats[position].getName()))
                            .setOnDismissListener((d) -> v.setClickable(true))
                            .setNegativeButton(android.R.string.cancel, null)
                            .setPositiveButton(android.R.string.ok, new OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    int i = holder.getBindingAdapterPosition();
                                    if (i < 0 || i > mCats.length - 1) {
                                        return;
                                    }
                                    onCatRemove(mCats[i]);
                                }
                            })
                            .show();
                }
            });
            holder.share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setContextGroupVisible(holder, false);
                    Cat cat = mCats[holder.getBindingAdapterPosition()];
                    if (ShareCatUtils.isNotRequireStoragePermissions()) {
                        shareCat(cat);
                        return;
                    }
                    String[] permissions = ShareCatUtils.getStoragePermissions();
                    if (ContextExt.checkSelfPermissions(NekoLand.this, permissions)) {
                        mPendingShareCat = cat;
                        requestPermissions(permissions, STORAGE_PERM_REQUEST);
                        return;
                    }
                    shareCat(cat);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mCats.length;
        }
    }

    private void shareCat(Cat cat) {
        Bitmap bitmap = cat.createBitmap(EXPORT_BITMAP_SIZE, EXPORT_BITMAP_SIZE);
        if (bitmap != null) {
            ShareCatUtils.shareCat(this, bitmap, cat.getName());
            cat.logShare(this);
        }
//        final File dir = new File(
//                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
//                "Cats");
//        if (!dir.exists() && !dir.mkdirs()) {
//            Log.e("NekoLand", "save: error: can't create Pictures directory");
//            return;
//        }
//        final File png = new File(dir, cat.getName().replaceAll("[/ #:]+", "_") + ".png");
//        Bitmap bitmap = cat.createBitmap(EXPORT_BITMAP_SIZE, EXPORT_BITMAP_SIZE);
//        if (bitmap != null) {
//            try {
//                OutputStream os = new FileOutputStream(png);
//                bitmap.compress(Bitmap.CompressFormat.PNG, 0, os);
//                os.close();
//                MediaScannerConnection.scanFile(
//                        this,
//                        new String[]{png.toString()},
//                        new String[]{"image/png"},
//                        null);
//                Log.v("Neko", "cat file: " + png);
//                Uri uri = FileProvider.getUriForFile(this, "com.android.egg.fileprovider", png);
//                Log.v("Neko", "cat uri: " + uri);
//                Intent intent = new Intent(Intent.ACTION_SEND);
//                intent.putExtra(Intent.EXTRA_STREAM, uri);
//                intent.putExtra(Intent.EXTRA_SUBJECT, cat.getName());
//                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                intent.setType("image/png");
//                startActivity(Intent.createChooser(intent, null)
//                        .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION));
//                cat.logShare(this);
//            } catch (IOException e) {
//                Log.e("NekoLand", "save: error: " + e);
//            }
//        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == STORAGE_PERM_REQUEST) {
            if (mPendingShareCat != null) {
                shareCat(mPendingShareCat);
                mPendingShareCat = null;
            }
        }
    }

    private static class CatHolder extends RecyclerView.ViewHolder {
        private final ImageView imageView;
        private final TextView textView;
        private final View contextGroup;
        private final View delete;
        private final View share;

        public CatHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(android.R.id.icon);
            textView = (TextView) itemView.findViewById(android.R.id.title);
            contextGroup = itemView.findViewById(R.id.contextGroup);
            delete = itemView.findViewById(android.R.id.closeButton);
            share = itemView.findViewById(R.id.shareText);
        }
    }
}

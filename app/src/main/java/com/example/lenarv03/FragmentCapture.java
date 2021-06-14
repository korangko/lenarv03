package com.example.lenarv03;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import java.io.File;

public class FragmentCapture extends Fragment {

    ImageView imageThumbnailBtn;
    private static final int PICK_FROM_ALBUM = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_capture, container, false);

        imageThumbnailBtn = view.findViewById(R.id.image_thumbnail_btn);

//        Size mSize = new Size(30,30);
//        CancellationSignal ca = new CancellationSignal();
//        File file = new File("/storage/DCIM/Screenshots/");
//        Bitmap thumbnail = ThumbnailUtils.createImageThumbnail(file, mSize, ca);

//        final Bitmap thumbnail = ThumbnailUtils.extractThumbnail(bitmap, 30, 30);
//        if (thumbnail != null && bitmap != null) {
//            video_thumbnail.setImageBitmap(thumbnail);
//        }
//        System.out.println("josh file save");

        return view;
    }
}

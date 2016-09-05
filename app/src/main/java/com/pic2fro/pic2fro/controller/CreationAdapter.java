package com.pic2fro.pic2fro.controller;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import com.pic2fro.pic2fro.activities.CreatorActivity;
import com.pic2fro.pic2fro.model.DataHolder;

import net.yazeed44.imagepicker.model.ImageEntry;
import net.yazeed44.imagepicker.util.Picker;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by srikaram on 01-Sep-16.
 */
public class CreationAdapter implements Picker.PickListener, LoaderManager.LoaderCallbacks<Cursor> {

    private final CreatorActivity activity;
    private Uri audioUri;

    public CreationAdapter(CreatorActivity activity) {
        this.activity = activity;
    }

    public void refreshOnCreate() {
        activity.refreshImages(DataHolder.getImages());
    }

    @Override
    public void onPickedSuccessfully(ArrayList<ImageEntry> images) {
        List<Bitmap> bitmaps = fetchBitmap(images);
        DataHolder.addImages(bitmaps);
        activity.refreshImages(bitmaps);
    }

    private List<Bitmap> fetchBitmap(List<ImageEntry> images) {
        List<Bitmap> bitmaps = new ArrayList<>(images.size());
        for (ImageEntry image : images) {
            bitmaps.add(BitmapFactory.decodeFile(image.path));
        }
        return bitmaps;
    }
    @Override
    public void onCancel() {
    }

    public void removeImage(int pos) {
        DataHolder.removeImage(pos);
        if (DataHolder.imageCount() == 0) {
            activity.toggleAddImageTextView(true);
        }
    }

    public void setAudioUri(Uri uri) {
        audioUri = uri;
        activity.getLoaderManager().initLoader(0, null, this);
    }

    private String getPathFromCursor(Cursor cursor) {
        int colIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(colIndex);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projs = {MediaStore.Audio.Media.DATA};
        return new CursorLoader(activity, audioUri, projs, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        String audioPath = null;
        try {
            audioPath = getPathFromCursor(data);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (audioPath == null) {
            audioPath = audioUri.getPath();
        }
        DataHolder.setAudioPath(audioPath);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
}
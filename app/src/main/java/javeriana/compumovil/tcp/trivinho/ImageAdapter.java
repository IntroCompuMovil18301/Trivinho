package javeriana.compumovil.tcp.trivinho;

import android.content.Context;

import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;

import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import javeriana.compumovil.tcp.trivinho.R;

public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    private List<Bitmap> imagenes = new ArrayList<Bitmap>();
    private static List<byte[]> imagenesBytes = new ArrayList<byte[]>();

    // Constructor
    public ImageAdapter(Context c) {
        mContext = c;
    }

    public int getCount() {
        return imagenes.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;

        if (convertView == null) {
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(500, 500));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
        }
        else
        {
            imageView = (ImageView) convertView;
        }
        imageView.setImageBitmap(imagenes.get(position));
        return imageView;
    }



    public void agregarImagen (Bitmap imagen){

        imagenes.add(imagen);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        imagen.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] imageInByte = stream.toByteArray();
        imagenesBytes.add(imageInByte);

    }

    public static List<byte[]> getImagenes (){
        return imagenesBytes;
    }

    public static void limpiarImagenes (){
        imagenesBytes.clear();
    }

    public int getNumImagenes (){
        return imagenes.size();
    }
}
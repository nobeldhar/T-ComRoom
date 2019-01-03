package becker.andy.map2018.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;

import becker.andy.map2018.R;
import becker.andy.map2018.models.ClusterMarker;

public class MyClusterManagerRenderer extends DefaultClusterRenderer<ClusterMarker> {

    private final IconGenerator iconGenerator;
    private final ImageView imageView;
    private final int  markerHeight;
    private final int markerWidth;

    public MyClusterManagerRenderer(Context context, GoogleMap map, ClusterManager<ClusterMarker> clusterManager
                                    ) {
        super(context, map, clusterManager);

        iconGenerator=new IconGenerator(context.getApplicationContext());
        imageView=new ImageView(context.getApplicationContext());
        markerHeight= (int) context.getResources().getDimension(R.dimen.custom_marker_image);
        markerWidth=(int) context.getResources().getDimension(R.dimen.custom_marker_image);
        imageView.setLayoutParams(new ViewGroup.LayoutParams(markerWidth,markerHeight));
        int padding=(int) context.getResources().getDimension(R.dimen.custom_marker_padding);
        imageView.setPadding(padding,padding,padding,padding);
        iconGenerator.setContentView(imageView);

    }

    @Override
    protected void onBeforeClusterItemRendered(ClusterMarker item, MarkerOptions markerOptions) {
        imageView.setImageResource(item.getInconPicture());
        Bitmap icon=iconGenerator.makeIcon();
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon)).title(item.getTitle());
    }

    @Override
    protected boolean shouldRenderAsCluster(Cluster<ClusterMarker> cluster) {
        return false;

    }
}

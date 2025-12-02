package com.example.app_giaohang;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.app_giaohang.Users.Order;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONObject;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private MapView map;
    private CardView cvStatus, cvOrderPopup;
    private TextView tvStatusText, btnReject;
    private TextView tvPopupDistance, tvPopupPrice, tvPopupPickup, tvPopupDropoff;
    private View viewStatusDot;
    private Button btnAcceptOrder;
    private FloatingActionButton fabMyLocation;

    private boolean isOnline = false;
    private Handler handler = new Handler();

    // Tọa độ Tài xế (Giả lập ở Cầu Giấy)
    private GeoPoint driverPoint = new GeoPoint(21.0307, 105.7836);
    private Order pendingOrder;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Context ctx = requireContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        Configuration.getInstance().setUserAgentValue(requireActivity().getPackageName());

        initViews(view);
        setupMap();
        setupEvents();

        if (pendingOrder != null) {
            processPendingOrder();
        }
    }

    public void setTargetOrder(Order order) {
        this.pendingOrder = order;
        if (isAdded() && map != null) {
            processPendingOrder();
        }
    }

    private void initViews(View view) {
        map = view.findViewById(R.id.mapView);
        cvStatus = view.findViewById(R.id.cvStatus);
        tvStatusText = view.findViewById(R.id.tvStatusText);
        viewStatusDot = view.findViewById(R.id.viewStatusDot);
        cvOrderPopup = view.findViewById(R.id.cvOrderPopup);
        btnAcceptOrder = view.findViewById(R.id.btnAcceptOrder);
        btnReject = view.findViewById(R.id.btnReject);
        fabMyLocation = view.findViewById(R.id.fabMyLocation);

        tvPopupDistance = view.findViewById(R.id.tvPopupDistance);
        tvPopupPrice = view.findViewById(R.id.tvPopupPrice);
        tvPopupPickup = view.findViewById(R.id.tvPopupPickup);
        tvPopupDropoff = view.findViewById(R.id.tvPopupDropoff);
    }

    private void setupMap() {
        String[] tileUrl = {"https://a.basemaps.cartocdn.com/rastertiles/voyager/"};
        XYTileSource cartoDbSource = new XYTileSource("CartoDB_Voyager", 0, 20, 256, ".png", tileUrl);
        map.setTileSource(cartoDbSource);
        map.setMultiTouchControls(true);
        map.getController().setZoom(16.0);
        map.getController().setCenter(driverPoint);
        addDriverMarker();
    }

    private void addDriverMarker() {
        Marker driverMarker = new Marker(map);
        driverMarker.setPosition(driverPoint);
        driverMarker.setTitle("Vị trí của bạn");
        driverMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        Drawable icon = ContextCompat.getDrawable(requireContext(), org.osmdroid.library.R.drawable.person);
        driverMarker.setIcon(icon);
        map.getOverlays().add(driverMarker);
    }

    private void setupEvents() {
        cvStatus.setOnClickListener(v -> toggleStatus());
        btnAcceptOrder.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Bắt đầu đón khách!", Toast.LENGTH_SHORT).show();
            cvOrderPopup.setVisibility(View.GONE);
        });
        btnReject.setOnClickListener(v -> {
            cvOrderPopup.setVisibility(View.GONE);
            map.getOverlays().clear();
            addDriverMarker();
            map.invalidate();
        });
        fabMyLocation.setOnClickListener(v -> {
            map.getController().animateTo(driverPoint);
            map.getController().setZoom(17.0);
        });
    }

    private void toggleStatus() {
        isOnline = !isOnline;
        if (isOnline) {
            tvStatusText.setText("Đang hoạt động");
            tvStatusText.setTextColor(Color.parseColor("#00A651"));
            viewStatusDot.setBackgroundResource(R.drawable.bg_dot_green);
            Toast.makeText(getContext(), "Đang tìm đơn...", Toast.LENGTH_SHORT).show();
        } else {
            tvStatusText.setText("Không nhận việc");
            tvStatusText.setTextColor(Color.parseColor("#D32F2F"));
            viewStatusDot.setBackgroundResource(R.drawable.bg_dot_red);
            cvOrderPopup.setVisibility(View.GONE);
            map.getOverlays().clear();
            addDriverMarker();
            map.invalidate();
        }
    }

    // --- LOGIC THÔNG MINH MỚI (ĐÃ SỬA LỖI NULL POINTER) ---

    private void processPendingOrder() {
        if (pendingOrder == null) return;

        // QUAN TRỌNG: Tạo biến cục bộ để giữ giá trị đơn hàng
        // Vì pendingOrder sẽ bị set null ngay bên dưới
        final Order orderToProcess = pendingOrder;

        // Cập nhật UI ngay lập tức (Dùng biến cục bộ)
        if (tvPopupPickup != null) tvPopupPickup.setText("● " + orderToProcess.getPickupAddress());
        if (tvPopupDropoff != null) tvPopupDropoff.setText("● " + orderToProcess.getDropoffAddress());
        if (tvPopupPrice != null) tvPopupPrice.setText(orderToProcess.getPrice());
        if (tvPopupDistance != null) tvPopupDistance.setText("Khoảng cách: " + orderToProcess.getDistance() + " km");

        cvOrderPopup.setVisibility(View.VISIBLE);
        btnAcceptOrder.setText("Bắt đầu đi");

        // Xử lý tìm đường trong luồng phụ (Background Thread)
        new Thread(() -> {
            // SỬA LỖI: Dùng orderToProcess thay vì pendingOrder
            // 1. Tìm tọa độ thật từ địa chỉ (Geocoding)
            GeoPoint pickup = getGeoPointSmart(orderToProcess.getPickupAddress());
            GeoPoint dropoff = getGeoPointSmart(orderToProcess.getDropoffAddress());

            // 2. Tìm đường đi thực tế (Routing)
            List<GeoPoint> roadPoints = getRoadPoints(driverPoint, pickup, dropoff);

            // 3. Cập nhật bản đồ trên luồng chính (UI Thread)
            new Handler(Looper.getMainLooper()).post(() -> {
                drawSmartRoute(pickup, dropoff, roadPoints);
            });
        }).start();

        // Reset biến toàn cục
        pendingOrder = null;
    }

    // Hàm tìm tọa độ thông minh (Dùng Geocoder của Google)
    private GeoPoint getGeoPointSmart(String address) {
        // Fallback: Nếu địa chỉ null hoặc rỗng -> Trả về Cầu Giấy
        if (address == null || address.isEmpty()) return new GeoPoint(21.0307, 105.7836);

        try {
            Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
            // Tìm kiếm địa chỉ, tối đa 1 kết quả
            List<Address> addresses = geocoder.getFromLocationName(address, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address location = addresses.get(0);
                return new GeoPoint(location.getLatitude(), location.getLongitude());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Nếu Geocoder lỗi hoặc không tìm thấy -> Dùng Fallback giả lập cũ
        return getGeoPointFallback(address);
    }

    // Hàm giả lập (Dự phòng khi mất mạng hoặc lỗi Geocoder)
    private GeoPoint getGeoPointFallback(String address) {
        if (address.contains("Hà Đông") || address.contains("Aeon")) return new GeoPoint(20.9806, 105.7523);
        if (address.contains("Bách Khoa")) return new GeoPoint(21.0041, 105.8438);
        if (address.contains("Royal")) return new GeoPoint(21.0026, 105.8142);
        return new GeoPoint(21.0290, 105.7850); // Mặc định Duy Tân
    }

    // Hàm gọi API OSRM để lấy đường đi thực tế (Miễn phí, không cần Key)
    private List<GeoPoint> getRoadPoints(GeoPoint start, GeoPoint mid, GeoPoint end) {
        List<GeoPoint> points = new ArrayList<>();
        try {
            // URL OSRM (start -> mid -> end)
            String urlString = "http://router.project-osrm.org/route/v1/driving/" +
                    start.getLongitude() + "," + start.getLatitude() + ";" +
                    mid.getLongitude() + "," + mid.getLatitude() + ";" +
                    end.getLongitude() + "," + end.getLatitude() +
                    "?overview=full&geometries=geojson";

            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            // Parse JSON
            JSONObject json = new JSONObject(response.toString());
            JSONArray routes = json.getJSONArray("routes");
            JSONObject route = routes.getJSONObject(0);
            JSONObject geometry = route.getJSONObject("geometry");
            JSONArray coordinates = geometry.getJSONArray("coordinates");

            for (int i = 0; i < coordinates.length(); i++) {
                JSONArray coord = coordinates.getJSONArray(i);
                double lon = coord.getDouble(0);
                double lat = coord.getDouble(1);
                points.add(new GeoPoint(lat, lon));
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Nếu lỗi mạng -> Trả về đường thẳng (Backup)
            points.add(start);
            points.add(mid);
            points.add(end);
        }
        return points;
    }

    private void drawSmartRoute(GeoPoint pickup, GeoPoint dropoff, List<GeoPoint> roadPoints) {
        map.getOverlays().clear();
        addDriverMarker();

        // Marker Điểm Đón
        Marker mPickup = new Marker(map);
        mPickup.setPosition(pickup);
        mPickup.setTitle("Đón khách");
        mPickup.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        map.getOverlays().add(mPickup);

        // Marker Điểm Trả
        Marker mDropoff = new Marker(map);
        mDropoff.setPosition(dropoff);
        mDropoff.setTitle("Trả khách");
        mDropoff.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        map.getOverlays().add(mDropoff);

        // Vẽ đường đi theo tọa độ thực tế
        Polyline line = new Polyline();
        line.setWidth(12f);
        line.setColor(Color.parseColor("#00A651"));
        line.setPoints(roadPoints); // Sử dụng các điểm đường bộ
        map.getOverlays().add(line);

        // Zoom map
        map.post(() -> {
            if (map.getOverlayManager().size() > 0) {
                map.zoomToBoundingBox(line.getBounds(), true, 100);
                map.invalidate();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (map != null) map.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (map != null) map.onPause();
    }
}
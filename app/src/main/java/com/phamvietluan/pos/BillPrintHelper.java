package com.phamvietluan.pos;

import android.content.Context;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.PrintJob;
import android.print.PrintManager;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Lớp tiện ích để in hóa đơn
 */
public class BillPrintHelper {

    /**
     * In hóa đơn từ dữ liệu đơn hàng
     */
    public static void printBill(Context context, String orderId, double totalPrice, String dateTime, List<CartItem> cartItems) {
        // Tạo nội dung HTML cho hóa đơn
        String htmlContent = createHtmlBill(orderId, totalPrice, dateTime, cartItems);
        
        // In hóa đơn sử dụng Android Printing Framework
        printHtml(context, htmlContent, "hoa_don_" + orderId);
    }
    
    /**
     * Tạo nội dung HTML cho hóa đơn
     */
    private static String createHtmlBill(String orderId, double totalPrice, String dateTime, List<CartItem> cartItems) {
        StringBuilder html = new StringBuilder();
        
        html.append("<html><body>");
        html.append("<div style='text-align:center; font-family: sans-serif;'>");
        html.append("<h2>CỬA HÀNG TRÀ SỮA POS</h2>");
        html.append("<p>Địa chỉ: 123 Đường ABC, Quận XYZ</p>");
        html.append("<h3>HÓA ĐƠN THANH TOÁN</h3>");
        html.append("<p><b>Mã đơn:</b> ").append(orderId).append("</p>");
        html.append("<p><b>Ngày:</b> ").append(dateTime).append("</p>");
        html.append("</div>");
        
        html.append("<div style='width:100%; margin-top:20px;'>");
        html.append("<table style='width:100%; border-collapse:collapse;'>");
        html.append("<tr style='border-bottom:1px solid #ddd;'>");
        html.append("<th style='text-align:left; padding:5px;'>Tên món</th>");
        html.append("<th style='text-align:center; padding:5px;'>SL</th>");
        html.append("<th style='text-align:right; padding:5px;'>Đơn giá</th>");
        html.append("<th style='text-align:right; padding:5px;'>Thành tiền</th>");
        html.append("</tr>");
        
        for (CartItem item : cartItems) {
            MenuItem menuItem = item.getMenuItem();
            double itemTotal = menuItem.getPrice() * item.getQuantity();
            
            html.append("<tr style='border-bottom:1px solid #ddd;'>");
            html.append("<td style='text-align:left; padding:5px;'>").append(menuItem.getName()).append("</td>");
            html.append("<td style='text-align:center; padding:5px;'>").append(item.getQuantity()).append("</td>");
            html.append("<td style='text-align:right; padding:5px;'>").append(String.format("%.0f", menuItem.getPrice())).append(" đ</td>");
            html.append("<td style='text-align:right; padding:5px;'>").append(String.format("%.0f", itemTotal)).append(" đ</td>");
            html.append("</tr>");
        }
        
        html.append("</table>");
        html.append("</div>");
        
        html.append("<div style='text-align:right; margin-top:20px; font-weight:bold;'>");
        html.append("<p>TỔNG TIỀN: ").append(String.format("%.0f", totalPrice)).append(" VNĐ</p>");
        html.append("</div>");
        
        html.append("<div style='text-align:center; margin-top:30px;'>");
        html.append("<p>Cảm ơn quý khách và hẹn gặp lại!</p>");
        html.append("</div>");
        
        html.append("</body></html>");
        
        return html.toString();
    }
    
    /**
     * In nội dung HTML bằng Android Printing Framework
     */
    private static void printHtml(Context context, String htmlContent, String jobName) {
        // Lấy dịch vụ in ấn
        PrintManager printManager = (PrintManager) context.getSystemService(Context.PRINT_SERVICE);
        
        if (printManager == null) {
            Toast.makeText(context, "Không tìm thấy dịch vụ in ấn!", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Tạo WebView để hiển thị HTML
        WebView webView = new WebView(context);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                // Sau khi tải xong HTML, bắt đầu in
                PrintDocumentAdapter printAdapter = webView.createPrintDocumentAdapter(jobName);
                
                // Thiết lập thuộc tính in
                PrintAttributes printAttributes = new PrintAttributes.Builder()
                        .setMediaSize(PrintAttributes.MediaSize.ISO_A8)
                        .setColorMode(PrintAttributes.COLOR_MODE_MONOCHROME)
                        .setResolution(new PrintAttributes.Resolution("pdf", "pdf", 300, 300))
                        .setMinMargins(PrintAttributes.Margins.NO_MARGINS)
                        .build();
                
                // Bắt đầu công việc in
                PrintJob printJob = printManager.print(jobName, printAdapter, printAttributes);
                
                if (printJob.isCompleted()) {
                    Toast.makeText(context, "In thành công!", Toast.LENGTH_SHORT).show();
                } else if (printJob.isFailed()) {
                    Toast.makeText(context, "In không thành công!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        
        // Tải nội dung HTML vào WebView
        webView.loadDataWithBaseURL(null, htmlContent, "text/HTML", "UTF-8", null);
    }
} 
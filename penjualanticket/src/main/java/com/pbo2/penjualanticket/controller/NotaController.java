package com.pbo2.penjualanticket.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import com.pbo2.penjualanticket.model.*;
import com.pbo2.penjualanticket.Repository.*;

import jakarta.servlet.http.HttpServletResponse;

@Controller
public class NotaController {

    @Autowired
    private PenjualanRepository penjualanRepo;

    @Autowired
    private DetailPenjualanRepository detailRepo;

    @GetMapping("/nota/{id}")
    public String notaPage(@PathVariable Integer id, Model model){

        Penjualan penjualan = penjualanRepo.findById(id).orElse(null);
        if (penjualan == null) {
            return "redirect:/dashboard";
        }

        List<DetailPenjualan> details = detailRepo.findByPenjualanIdPenjualan(id);

        model.addAttribute("penjualan", penjualan);
        model.addAttribute("details", details);
        return "nota";
    }

    @GetMapping("/nota/{id}/pdf")
    public void notaPdf(@PathVariable Integer id, HttpServletResponse response)
            throws IOException, DocumentException {

        Penjualan penjualan = penjualanRepo.findById(id).orElse(null);
        if (penjualan == null) {
            response.sendRedirect("/dashboard");
            return;
        }

        if (!"SUCCESS".equalsIgnoreCase(penjualan.getPaymentStatus())) {
            response.sendRedirect("/nota/" + id + "?error=unpaid");
            return;
        }
        List<DetailPenjualan> details = detailRepo.findByPenjualanIdPenjualan(id);

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition",
                "attachment; filename=nota-" + penjualan.getNoFaktur() + ".pdf");

        Document document = new Document();
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();

        Font judulFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
        Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 11);

        Paragraph judul = new Paragraph("NOTA PEMBELIAN TIKET", judulFont);
        judul.setAlignment(Element.ALIGN_CENTER);
        document.add(judul);
        document.add(new Paragraph(" "));

        document.add(new Paragraph("No Faktur : " + penjualan.getNoFaktur(), normalFont));
        document.add(new Paragraph("Tanggal   : " + penjualan.getTanggal(), normalFont));
        if (penjualan.getCustomer() != null) {
            document.add(new Paragraph("Customer  : " + penjualan.getCustomer().getName(), normalFont));
        }
        document.add(new Paragraph(" "));

        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{4, 1, 2, 2});
        tambahHeader(table, "Nama Event", "Qty", "Harga", "Subtotal");

        for (DetailPenjualan d : details) {
            table.addCell(new Phrase(d.getTicket().getNameEvent(), normalFont));
            table.addCell(new Phrase(String.valueOf(d.getQty()), normalFont));
            table.addCell(new Phrase("Rp. " + d.getTicket().getPrice(), normalFont));
            table.addCell(new Phrase("Rp. " + d.getSubtotal(), normalFont));
        }
        document.add(table);

        document.add(new Paragraph(" "));
        Paragraph total = new Paragraph("Total Bayar : Rp. " + penjualan.getTotalBayar(),
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12));
        total.setAlignment(Element.ALIGN_RIGHT);
        document.add(total);

        document.add(new Paragraph(" "));
        Paragraph footer = new Paragraph("Terima kasih atas pembelian Anda.", normalFont);
        footer.setAlignment(Element.ALIGN_CENTER);
        document.add(footer);

        document.close();
    }

    private void tambahHeader(PdfPTable table, String... judul) {
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11);
        for (String teks : judul) {
            PdfPCell cell = new PdfPCell(new Phrase(teks, headerFont));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
        }
    }
}

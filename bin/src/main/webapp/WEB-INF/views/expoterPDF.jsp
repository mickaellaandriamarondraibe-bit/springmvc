<%@ page language="java" contentType="application/pdf; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.itextpdf.text.Document" %>
<%@ page import="com.itextpdf.text.Paragraph" %>
<%@ page import="com.itextpdf.text.pdf.PdfWriter" %>
<%@ page import="java.io.ByteArrayOutputStream" %>
<%@ page import="com.itextpdf.text.DocumentException" %>
<%@ page import="java.io.IOException" %>

<%
    @SuppressWarnings("unchecked")
    java.util.List<String> libelles = (java.util.List<String>) request.getAttribute("libelles");
    @SuppressWarnings("unchecked")
    java.util.List<Integer> qtes = (java.util.List<Integer>) request.getAttribute("qtes");
    @SuppressWarnings("unchecked")
    java.util.List<Double> prixUnitaires = (java.util.List<Double>) request.getAttribute("prixUnitaires");

    Document document = new Document();
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try {
        PdfWriter.getInstance(document, baos);
        document.open();

        document.add(new Paragraph("Liste des devis"));
        document.add(new Paragraph(" "));

        if (libelles != null && qtes != null && prixUnitaires != null) {
            int taille = Math.min(libelles.size(), Math.min(qtes.size(), prixUnitaires.size()));
            for (int i = 0; i < taille; i++) {
                document.add(new Paragraph("Devis " + (i + 1)));
                document.add(new Paragraph("Libelle: " + libelles.get(i)));
                document.add(new Paragraph("Qte: " + qtes.get(i)));
                document.add(new Paragraph("Prix unitaire: " + prixUnitaires.get(i)));
                document.add(new Paragraph(" "));
            }
        } else {
            document.add(new Paragraph("Aucune donnee de devis a exporter."));
        }

        document.close();

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=devis.pdf");
        response.getOutputStream().write(baos.toByteArray());
    } catch (DocumentException | IOException e) {
        e.printStackTrace();
    }
%>
